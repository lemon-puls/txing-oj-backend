#!/bin/sh
# ./app.sh start 启动 stop 停止 restart 重启 status 状态
AppName=tx-oj.jar

# JVM参数
JVM_OPTS="-Dname=$AppName -Duser.timezone=Asia/Shanghai -Xms512m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m -XX:+HeapDumpOnOutOfMemoryError -Xlog:gc*:file=gc.log:time,uptime,level,tags:filecount=5,filesize=10m -XX:NewRatio=1 -XX:SurvivorRatio=30 -XX:+UseParallelGC"

if [ "$1" = "" ]; then
    echo -e "\033[0;31m 未输入操作名 \033[0m  \033[0;34m {start|stop|restart|status} \033[0m"
    exit 1
fi

if [ "$AppName" = "" ]; then
    echo -e "\033[0;31m 未输入应用名 \033[0m"
    exit 1
fi

start() {
    PID=$(ps -ef | grep java | grep "$AppName" | grep -v grep | awk '{print $2}')

    if [ -n "$PID" ]; then
        echo "$AppName 正在运行..."
    else
        nohup java $JVM_OPTS -jar "$AppName" > app.log 2>&1 &
        echo "启动 $AppName 成功..."
    fi
}

stop() {
    echo "停止 $AppName"
    PID=$(ps -ef | grep java | grep "$AppName" | grep -v grep | awk '{print $2}')

    if [ -n "$PID" ]; then
        kill -TERM "$PID"
        echo "$AppName (pid:$PID) 正在退出..."
        while [ -n "$(ps -p $PID -o pid=)" ]; do
            sleep 1
        done
        echo "$AppName 已退出."
    else
        echo "$AppName 已停止."
    fi
}

restart() {
    stop
    sleep 2
    start
}

status() {
    PID=$(ps -ef | grep java | grep "$AppName" | grep -v grep | wc -l)
    if [ "$PID" -ne 0 ]; then
        echo "$AppName 正在运行..."
    else
        echo "$AppName 未运行..."
    fi
}

case $1 in
    start)
        start
        ;;
    stop)
        stop
        ;;
    restart)
        restart
        ;;
    status)
        status
        ;;
    *)
        echo -e "\033[0;31m 无效操作 \033[0m"
        exit 1
        ;;
esac
