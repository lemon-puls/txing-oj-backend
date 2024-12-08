#!/bin/bash
# ./app.sh start 启动 stop 停止 restart 重启 status 状态
AppName=tx-code-sandbox
AppPath="./${AppName}"

# 确保目标文件具有可执行权限
chmod +x "$AppName"

if [ -z "$1" ]; then
  echo -e "\033[0;31m 未输入操作名 \033[0m  \033[0;34m {start|stop|restart|status} \033[0m"
  exit 1
fi

if [ -z "$AppName" ]; then
  echo -e "\033[0;31m 未输入应用名 \033[0m"
  exit 1
fi

function start() {
  # PID=$(pgrep -f "$AppName" | grep -v /tx-code-sandbox)
  PID=$(ps aux | grep "$AppName" | grep -v "grep" | grep -v "$0" | awk '{print $2}')
  if [ -n "$PID" ]; then
    echo "$AppName is running..."
  else
    nohup java -jar "$AppPath" >/dev/null 2>&1 &
    echo "Start $AppName success..."
  fi
}

function stop() {
  echo "Stop $AppName"

  PID=""
  query() {
    PID=$(pgrep -f "$AppName")
  }

  query
  if [ -n "$PID" ]; then
    kill -TERM $PID
    echo "$AppName (pid:$PID) exiting..."
    while [ -n "$PID" ]; do
      sleep 1
      query
    done
    echo "$AppName exited."
  else
    echo "$AppName already stopped."
  fi
}

function restart() {
  stop
  sleep 2
  start
}

function status() {
  PID=$(pgrep -c -f "$AppName")
  if [ "$PID" -ne 0 ]; then
    echo "$AppName is running..."
  else
    echo "$AppName is not running..."
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
  echo -e "\033[0;31m 无效的操作名 \033[0m  \033[0;34m {start|stop|restart|status} \033[0m"
  exit 1
  ;;
esac
