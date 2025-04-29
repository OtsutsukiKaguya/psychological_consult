package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.dto.InformationDTO;
import com.example.demo.entity.*;
import com.example.demo.mapper.InformationMapper;
import jakarta.websocket.server.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class InformationController {

    private static final Logger logger = LoggerFactory.getLogger(InformationController.class);

    @Autowired
    private InformationMapper informationMapper;

    // 查询 Person
    @GetMapping("/search/person/{id}")
    public Result query(@PathVariable("id") String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                return Result.error("ID cannot be empty");
            }
            List<Person> list = informationMapper.getPersonById(id);
            if (list == null || list.isEmpty()) {
                return Result.error("Person not found with ID: " + id);
            }
            logger.info("Queried person with ID: {}", id);
            return Result.success(list);
        } catch (Exception e) {
            logger.error("Error querying person with ID: {}", id, e);
            return Result.error("An error occurred while querying person");
        }
    }

    // 查询 User
    @GetMapping("/search/user/{id}")
    public Result query1(@PathVariable("id") String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                return Result.error("ID cannot be empty");
            }
            List<User> list = informationMapper.getUserById(id);
            if (list == null || list.isEmpty()) {
                return Result.error("User not found with ID: " + id);
            }
            logger.info("Queried user with ID: {}", id);
            return Result.success(list);
        } catch (Exception e) {
            logger.error("Error querying user with ID: {}", id, e);
            return Result.error("An error occurred while querying user");
        }
    }

    // 查询 Counselor
    @GetMapping("/search/counselor/{id}")
    public Result query2(@PathVariable("id") String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                return Result.error("ID cannot be empty");
            }
            List<Counselor> list = informationMapper.getCounselorById(id);
            if (list == null || list.isEmpty()) {
                return Result.error("Counselor not found with ID: " + id);
            }
            logger.info("Queried counselor with ID: {}", id);
            return Result.success(list);
        } catch (Exception e) {
            logger.error("Error querying counselor with ID: {}", id, e);
            return Result.error("An error occurred while querying counselor");
        }
    }

    // 查询 Tutor
    @GetMapping("/search/tutor/{id}")
    public Result query3(@PathVariable("id") String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                return Result.error("ID cannot be empty");
            }
            List<Tutor> list = informationMapper.getTutorById(id);
            if (list == null || list.isEmpty()) {
                return Result.error("Tutor not found with ID: " + id);
            }
            logger.info("Queried tutor with ID: {}", id);
            return Result.success(list);
        } catch (Exception e) {
            logger.error("Error querying tutor with ID: {}", id, e);
            return Result.error("An error occurred while querying tutor");
        }
    }

    // 禁用用户
    @PostMapping("/delete")
    public Result bannedUser(@RequestParam("id") String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                return Result.error("ID cannot be empty");
            }
            int i = informationMapper.bannedUser(id);
            if (i <= 0) {
                return Result.error("User not found or already banned with ID: " + id);
            }
            logger.info("Banned user with ID: {}", id);
            return Result.success("User banned successfully");
        } catch (Exception e) {
            logger.error("Error banning user with ID: {}", id, e);
            return Result.error("An error occurred while banning user");
        }
    }

    // 更新用户信息
    @PostMapping("/person/update")
    public Result updatePerson(
            @RequestParam("newid") String newid,
            @RequestParam("id") String id,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("selfDescription") String selfDescription,
            @RequestParam("idPictureLink") String idPictureLink) {
        try {
            if (id == null || id.trim().isEmpty()) {
                return Result.error("ID cannot be empty");
            }
            int i = informationMapper.updatePerson(newid, id, email, phone, selfDescription, idPictureLink);
            if (i <= 0) {
                return Result.error("Person not found with ID: " + id);
            }
            logger.info("Updated person with ID: {}", id);
            return Result.success("Update successful");
        } catch (Exception e) {
            logger.error("Error updating person with ID: {}", id, e);
            return Result.error("An error occurred while updating person");
        }
    }

    @PostMapping("/user/update")
    public Result updateUser(
            @RequestParam("newid") String newid,
            @RequestParam("id") String id,
            @RequestParam("urgentName") String urgentName,
            @RequestParam("urgentPhone") String urgentPhone) {
        try {
            if (id == null || id.trim().isEmpty()) {
                return Result.error("ID cannot be empty");
            }
            int i = informationMapper.updateUser(newid, id, urgentName, urgentPhone);
            if (i <= 0) {
                return Result.error("User not found with ID: " + id);
            }
            logger.info("Updated user with ID: {}", id);
            return Result.success("Update successful");
        } catch (Exception e) {
            logger.error("Error updating user with ID: {}", id, e);
            return Result.error("An error occurred while updating user");
        }
    }

    // 插入 Person
    @PostMapping("/insert/person")
    public Result insert(@RequestBody Person person) {
        try {
            if (person == null || person.getId() == null || person.getId().trim().isEmpty()) {
                return Result.error("Person ID cannot be empty");
            }
            int i = informationMapper.insertPerson(person);
            if (i <= 0) {
                return Result.error("Insert failed for person with ID: " + person.getId());
            }
            logger.info("Inserted person with ID: {}", person.getId());
            return Result.success("Insert successful");
        } catch (Exception e) {
            logger.error("Error inserting person with ID: {}", person != null ? person.getId() : "null", e);
            return Result.error("An error occurred while inserting person");
        }
    }

    // 插入 User
    @PostMapping("/insert/user")
    public Result insert1(@RequestBody User user) {
        try {
            if (user == null || user.getId() == null || user.getId().trim().isEmpty()) {
                return Result.error("User ID cannot be empty");
            }
            int i = informationMapper.insertUser(user);
            if (i <= 0) {
                return Result.error("Insert failed for user with ID: " + user.getId());
            }
            logger.info("Inserted user with ID: {}", user.getId());
            return Result.success("Insert successful");
        } catch (Exception e) {
            logger.error("Error inserting user with ID: {}", user != null ? user.getId() : "null", e);
            return Result.error("An error occurred while inserting user");
        }
    }

    // 插入 Counselor
    @PostMapping("/insert/counselor")
    public Result insert2(@RequestBody Counselor counselor) {
        try {
            if (counselor == null || counselor.getId() == null || counselor.getId().trim().isEmpty()) {
                return Result.error("Counselor ID cannot be empty");
            }
            int i = informationMapper.insertCounselor(counselor);
            if (i <= 0) {
                return Result.error("Insert failed for counselor with ID: " + counselor.getId());
            }
            logger.info("Inserted counselor with ID: {}", counselor.getId());
            return Result.success("Insert successful");
        } catch (Exception e) {
            logger.error("Error inserting counselor with ID: {}", counselor != null ? counselor.getId() : "null", e);
            return Result.error("An error occurred while inserting counselor");
        }
    }

    // 插入 Tutor
    @PostMapping("/insert/tutor")
    public Result insert3(@RequestBody Tutor tutor) {
        try {
            if (tutor == null || tutor.getId() == null || tutor.getId().trim().isEmpty()) {
                return Result.error("Tutor ID cannot be empty");
            }
            int i = informationMapper.insertTutor(tutor);
            if (i <= 0) {
                return Result.error("Insert failed for tutor with ID: " + tutor.getId());
            }
            logger.info("Inserted tutor with ID: {}", tutor.getId());
            return Result.success("Insert successful");
        } catch (Exception e) {
            logger.error("Error inserting tutor with ID: {}", tutor != null ? tutor.getId() : "null", e);
            return Result.error("An error occurred while inserting tutor");
        }
    }

    // 修改密码
    @PostMapping("/auth/change-password")
    public Result changePassword(@RequestParam("id") String id, @RequestParam("password") String password) {
        try {
            if (id == null || id.trim().isEmpty()) {
                return Result.error("ID cannot be empty");
            }
            if (password == null || password.length() < 6) {
                return Result.error("Password must be at least 6 characters");
            }
            int i = informationMapper.changePassword(id, password);
            if (i <= 0) {
                return Result.error("User not found with ID: " + id);
            }
            logger.info("Changed password for ID: {}", id);
            return Result.success("Password changed successfully");
        } catch (Exception e) {
            logger.error("Error changing password for ID: {}", id, e);
            return Result.error("An error occurred while changing password");
        }
    }

    // 查询最后登录时间
    @GetMapping("/person/last-login/{id}")
    public Result lastLogin(@PathVariable("id") String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                return Result.error("ID cannot be empty");
            }
            List<Person> list = informationMapper.getLastLoginTime(id);
            if (list == null || list.isEmpty()) {
                return Result.error("Person not found with ID: " + id);
            }
            logger.info("Queried last login time for ID: {}", id);
            return Result.success(list);
        } catch (Exception e) {
            logger.error("Error querying last login time for ID: {}", id, e);
            return Result.error("An error occurred while querying last login time");
        }
    }


    @PostMapping("/login")
    public Result login(@RequestParam String id, @RequestParam String lastLoginTime, @RequestParam String password) {
        try {
            // 调用 Mapper 层更新最后登录时间
            int updateResult = informationMapper.login(id, lastLoginTime, password);
            if (updateResult > 0) {
                // 登录成功后，查询人员信息
                List<Person> personList = informationMapper.loginPerson(id, password);
                if (!personList.isEmpty()) {
                    return Result.success(personList); // 返回人员信息
                } else {
                    return Result.error("未找到匹配的用户信息");
                }
            } else {
                return Result.error("登录失败，用户名或密码错误");
            }
        } catch (Exception e) {
            return Result.error("无效的日期格式或服务器错误: " + e.getMessage());
        }
    }

    @GetMapping("/counselor/byDutyDate")
    public Result getCounselorByDutyDate(@RequestParam("dutyDate") String dutyDate) {
        try {
            if (dutyDate == null || dutyDate.trim().isEmpty()) {
                return Result.error("Duty date cannot be empty");
            }
            List<InformationDTO> counselors = informationMapper.getCounselorByDutyDate(dutyDate);
            if (counselors == null || counselors.isEmpty()) {
                return Result.error("No counselors found for duty date: " + dutyDate);
            }
            logger.info("Retrieved {} counselors for duty date: {}", counselors.size(), dutyDate);
            return Result.success(counselors);
        } catch (Exception e) {
            logger.error("Error retrieving counselors for duty date: {}", dutyDate, e);
            return Result.error("An error occurred while retrieving counselors");
        }
    }

    @PostMapping("/quit")
    public Result quit(@RequestParam String id) {
        try {
            // 调用 Mapper 层更新状态为 OFFLINE
            int updateResult = informationMapper.quit(id);
            if (updateResult > 0) {
                return Result.success("退出成功");
            } else {
                return Result.error("退出失败，用户ID不存在");
            }
        } catch (Exception e) {
            return Result.error("服务器错误: " + e.getMessage());
        }
    }
}