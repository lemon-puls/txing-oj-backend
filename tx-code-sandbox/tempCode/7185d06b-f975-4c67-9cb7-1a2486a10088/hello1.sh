#!/bin/bash

while true; do
    PID=$(ps -eo pid,etimes,command | grep "[j]ava" | sort -nk 2 | head -n 1 | awk '{print $1}')
    if [ -n "$PID" ]; then
        memory=$(ps -o rss= -p $PID)
        echo "Memory Usage: $memory KB"
    else
        echo "Java process not found."
    fi
    sleep 3
done
