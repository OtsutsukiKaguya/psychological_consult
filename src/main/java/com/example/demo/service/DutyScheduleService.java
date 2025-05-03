package com.example.demo.service;

import com.example.demo.entity.Duty_calendar;
import com.example.demo.mapper.BackstageArrangementMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class DutyScheduleService {
    @Autowired
    private BackstageArrangementMapper mapper;

    // 权重 & 硬约束参数
    private static final double W_BALANCE           = 1.0;
    private static final double W_GAP               = 0.2;
    private static final int    L_MAX_CONSECUTIVE   = 5;

    /** 生成排班（贪心初始 + 模拟退火优化）并持久化 */
    public void generateSchedule(LocalDate start, LocalDate end, int perDay) {
        // 1. 读数据
        List<String> counselors = mapper.findAllCounselorIds();
        final Map<String, Set<LocalDate>> leaves = counselors.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> new HashSet<>(mapper.findLeaveDatesByCounselor(id, start, end))
                ));

        // 2. 构建初始解
        Map<LocalDate, List<String>> schedule = buildGreedySchedule(counselors, leaves, start, end, perDay);

        // 3. 模拟退火局部优化
        schedule = optimizeBySimulatedAnnealing(schedule, leaves, perDay);

        // 4. 转实体 & 批量写入
        List<Duty_calendar> records = new ArrayList<>();
        for (Map.Entry<LocalDate, List<String>> e : schedule.entrySet()) {
            LocalDate d = e.getKey();
            for (String id : e.getValue()) {
//                Duty_calendar dc = new Duty_calendar();
//                dc.setStaffId(id);
//                dc.setDutyDate(d.atStartOfDay());
//                dc.setIsLeave(false);
//                dc.setShiftType(1);
//                records.add(dc);
                Duty_calendar dc = new Duty_calendar();
                dc.setStaffId(id);
                dc.setDutyDate(d);           // 保留 LocalDate 类型
                dc.setIsLeave(0);            // 0 表示不是请假（对应 false）
                dc.setShiftType(1);
                records.add(dc);

            }
        }
        mapper.batchInsertDuties(records);
    }

    /** 构建贪心初始排班：每次选“累积排班最少”的可用咨询师 */
    private Map<LocalDate, List<String>> buildGreedySchedule(
            List<String> counselors,
            Map<String, Set<LocalDate>> leaves,
            LocalDate start, LocalDate end, int perDay) {

        Map<String, Integer> load = new HashMap<>();
        counselors.forEach(id -> load.put(id, 0));

        Map<LocalDate, List<String>> sched = new LinkedHashMap<>();
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            final LocalDate date = d;  // ← 确保后面 lambda 捕获的是 final 变量

            List<String> available = counselors.stream()
                    .filter(id -> !leaves.getOrDefault(id, Collections.emptySet()).contains(date))
                    .sorted(Comparator.comparingInt(load::get))
                    .limit(perDay)
                    .collect(Collectors.toList());

            sched.put(date, available);
            // 更新每人已排次数
            available.forEach(id -> load.put(id, load.get(id) + 1));
        }
        return sched;
    }

    /** 模拟退火局部优化 */
    private Map<LocalDate, List<String>> optimizeBySimulatedAnnealing(
            Map<LocalDate, List<String>> current,
            Map<String, Set<LocalDate>> leaves,
            int perDay) {

        double T        = 100.0, T_min = 0.1, alpha = 0.95;
        int    iter     = 50;

        Map<LocalDate, List<String>> best      = deepCopy(current);
        double                        bestScore = objective(best);

        while (T > T_min) {
            for (int i = 0; i < iter; i++) {
                Map<LocalDate, List<String>> neighbor = generateNeighbor(deepCopy(current), leaves, perDay);
                double delta = objective(neighbor) - objective(current);
                if (delta < 0 || Math.exp(-delta / T) > Math.random()) {
                    current = neighbor;
                    double cs = objective(current);
                    if (cs < bestScore) {
                        best = deepCopy(current);
                        bestScore = cs;
                    }
                }
            }
            T *= alpha;
        }
        return best;
    }

    /** 目标函数 = 工时平衡度 + 排班间隔均匀度 */
    private double objective(Map<LocalDate, List<String>> sched) {
        // 按咨询师分组
        Map<String, List<LocalDate>> byCounselor = new HashMap<>();
        for (Map.Entry<LocalDate, List<String>> e : sched.entrySet()) {
            LocalDate day = e.getKey();
            for (String id : e.getValue()) {
                byCounselor.computeIfAbsent(id, k -> new ArrayList<>()).add(day);
            }
        }

        // f_balance
        int   total = byCounselor.values().stream().mapToInt(List::size).sum();
        double mean = (double) total / byCounselor.size();
        double f_bal = byCounselor.values().stream()
                .mapToDouble(lst -> Math.pow(lst.size() - mean, 2))
                .sum();

        // f_gap
        double f_gap = 0;
        for (List<LocalDate> lst : byCounselor.values()) {
            if (lst.size() <= 1) continue;
            Collections.sort(lst);
            List<Long> gaps = new ArrayList<>();
            for (int i = 0; i < lst.size() - 1; i++) {
                gaps.add(ChronoUnit.DAYS.between(lst.get(i), lst.get(i + 1)));
            }
            double gm  = gaps.stream().mapToLong(v -> v).average().orElse(0);
            double var = gaps.stream().mapToDouble(g -> Math.pow(g - gm, 2)).sum();
            f_gap += var;
        }

        return W_BALANCE * f_bal + W_GAP * f_gap;
    }

    /** 生成邻域解：随机“交换”或“替换” */
    private Map<LocalDate, List<String>> generateNeighbor(
            Map<LocalDate, List<String>> sched,
            Map<String, Set<LocalDate>> leaves,
            int perDay) {

        Random rnd = ThreadLocalRandom.current();
        int    tries = 100;

        for (int t = 0; t < tries; t++) {
            Map<LocalDate, List<String>> cand = deepCopy(sched);

            if (rnd.nextBoolean()) {
                // Swap：随机选两天各换一人
                List<LocalDate> days = new ArrayList<>(cand.keySet());
                LocalDate d1 = days.get(rnd.nextInt(days.size()));
                LocalDate d2 = days.get(rnd.nextInt(days.size()));
                if (d1.equals(d2)) continue;

                String c1 = randomPick(cand.get(d1), rnd);
                String c2 = randomPick(cand.get(d2), rnd);

                if (isAllowed(c1, d2, cand, leaves) && isAllowed(c2, d1, cand, leaves)) {
                    cand.get(d1).remove(c1); cand.get(d1).add(c2);
                    cand.get(d2).remove(c2); cand.get(d2).add(c1);
                    return cand;
                }
            } else {
                // Replace：某天换一个可用未排的人
                List<LocalDate> days = new ArrayList<>(cand.keySet());
                LocalDate d = days.get(rnd.nextInt(days.size()));
                final LocalDate date = d;  // 再次保证 lambda 捕获 final

                String oldC = randomPick(cand.get(date), rnd);
                List<String> pool = leaves.keySet().stream()
                        .filter(id -> !cand.get(date).contains(id))
                        .filter(id -> !leaves.getOrDefault(id, Collections.emptySet()).contains(date))
                        .filter(id -> isAllowed(id, date, cand, leaves))
                        .collect(Collectors.toList());

                if (pool.isEmpty()) continue;
                String newC = pool.get(rnd.nextInt(pool.size()));

                cand.get(date).remove(oldC);
                cand.get(date).add(newC);
                return cand;
            }
        }
        return sched;
    }

    /** 判定该咨询师在这天排班是否满足“请假”和“连续上限” */
    private boolean isAllowed(String counselor,
                              LocalDate day,
                              Map<LocalDate, List<String>> sched,
                              Map<String, Set<LocalDate>> leaves) {
        // 请假约束
        if (leaves.getOrDefault(counselor, Collections.emptySet()).contains(day)) {
            return false;
        }
        // 连续天数约束
        int count = 1;
        LocalDate p = day.minusDays(1);
        while (sched.containsKey(p) && sched.get(p).contains(counselor)) {
            count++; p = p.minusDays(1);
        }
        LocalDate n = day.plusDays(1);
        while (sched.containsKey(n) && sched.get(n).contains(counselor)) {
            count++; n = n.plusDays(1);
        }
        return count <= L_MAX_CONSECUTIVE;
    }

    /** 从列表随机取一人 */
    private String randomPick(List<String> list, Random rnd) {
        return list.get(rnd.nextInt(list.size()));
    }

    /** 深度拷贝调度表 */
    private Map<LocalDate, List<String>> deepCopy(Map<LocalDate, List<String>> orig) {
        return orig.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> new ArrayList<>(e.getValue()),
                        (a,b) -> a,
                        LinkedHashMap::new
                ));
    }
}
