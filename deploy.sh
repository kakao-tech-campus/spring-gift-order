#!/bin/bash

BUILD_JAR=$(ls /home/ubuntu/*[^plain].jar)
JAR_NAME=$(basename $BUILD_JAR)
echo "> build 파일명: $JAR_NAME"

CURRENT_PID=$(pgrep -f $JAR_NAME)
echo "> 현재 실행중인 애플리케이션 pid: $CURRENT_PID"

if [ -z "$CURRENT_PID" ]; then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> $JAR_NAME 배포"
# [수정] 로그를 /dev/null 대신 nohup.out 파일에 기록하도록 변경
nohup java -jar $BUILD_JAR > nohup.out 2>&1 &

echo "> 배포 완료"