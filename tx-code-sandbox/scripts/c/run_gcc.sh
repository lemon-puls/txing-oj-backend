#!/bin/bash

# Check if the required number of arguments are provided
if [ "$#" -ne 2 ]; then
    echo "Usage: $0 <numbers> <main_class_path>"
    exit 1
fi

numbers=$1
main_class_path=$2

# Start the Java program in the background with provided parameters
echo "$numbers" | "$main_class_path"/Main &

# Start the Java program in the background
# echo "3 1 2 3" | java -cp /app Main &

while true; do
    PID=$(pgrep -n -f "Main" | grep -v "jps")
    # PID=$(pgrep -n -f "java")
    if [ -n "$PID" ]; then
        memory=$(ps -o rss= -p $PID)
        echo "memory&&&$memory&&&memory"
    else
        # echo "Java process not found."
        break
    fi
    sleep 0.01
done

