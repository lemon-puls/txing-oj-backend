#!/bin/bash

# 获取传递进来的路径参数
APP_PATH="$1"

# 检查路径参数是否为空
if [ -z "$APP_PATH" ]; then
    echo "Error: Please provide the path to your Java application."
    exit 1
fi

# 启动 Java 应用程序
java "$APP_PATH/Main" &

# Get the PID of the Java process
PID=$!

# Monitor memory usage of the Java process
while true; do
    memory=$(docker exec ab589f96b253 ps -o pid,%mem ax | grep $PID | awk '{print $2}')
    echo "Memory Usage: $memory%"
    sleep 1
done