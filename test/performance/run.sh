#!/usr/bin/env bash
# 一键执行 JMeter 性能测试
JMETER_CMD=${JMETER_CMD:-jmeter}     # 若已配置环境变量 JMETER_CMD，可自定义 JMeter 可执行文件路径
BASE_DIR=$(dirname "$0")

# 生成结果并输出报告
$JMETER_CMD -n \
  -t "$BASE_DIR/login_chat.jmx" \
  -l "$BASE_DIR/result.jtl" \
  -e -o "$BASE_DIR/html_report"

echo "性能测试完成，报告位于 $BASE_DIR/html_report"