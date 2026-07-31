##!/bin/bash
#
##字符配置
#
#export LANG="en_US.UTF-8"
#
#CURR_DIR=`pwd`
#
#cd `dirname "$0"`
#
#WORK_HOME=`pwd`
#
#app_jar=$1
#
#echo "Work Home: ${WORK_HOME}"
#
#
#
########################产品填写部分#########################
#
##set JAVA_OPTS
#
## define JAVA_OPTS
#
##产品填写JAVA_OPTS，并把注销除掉，否则使用JVM默认值
#
##JAVA_INIT_OPTS="-Xms2000m -Xmx4000m -Xmn1000m -Xss256k -XX:MetaspaceSize=64m -XX:MaxMetaspaceSize=128m"
#
#
#
##if [ -z "$JAVA_OPTS" ]; then
###默认值
###   JAVA_OPTS="-agentlib:ByteCodeDecryptor  -Djasypt.encryptor.password=gn4^Qa0k+WyeCkKt -Xms500m -Xmx500m -Xss256k "
##   JAVA_OPTS="-agentlib:ByteCodeDecryptor -Djasypt.encryptor.password=gn4^Qa0k+WyeCkKt -Dspring.cloud.nacos.config.username=nacos -Dspring.cloud.nacos.config.password=nacos -Dspring.cloud.nacos.discovery.username=nacos -Dspring.cloud.nacos.discovery.password=nacos -Xms500m -Xmx500m -Xss256k "
##else
##   JAVA_OPTS="$JAVA_OPTS $JAVA_INIT_OPTS"
##fi
#
##if [ -z "$JAVA_OPTS" ]; then
##   JAVA_OPTS="-agentlib:ByteCodeDecryptor -Djasypt.encryptor.password=gn4^Qa0k+WyeCkKt -Dspring.cloud.nacos.config.server-addr=10.168.154.42:8848 -Dspring.cloud.nacos.config.username=nacos -Dspring.cloud.nacos.config.password=nacos -Dspring.cloud.nacos.discovery.server-addr=10.168.154.42:8848 -Dspring.cloud.nacos.discovery.username=nacos -Dspring.cloud.nacos.discovery.password=nacos -Dglobal.nacos.url=http://10.168.154.42:8848 -Dglobal.nacos.user=nacos -Dglobal.nacos.password=nacos -Xms500m -Xmx500m -Xss256k "
##else
##   JAVA_OPTS="$JAVA_OPTS $JAVA_INIT_OPTS"
##fi
#
#if [ -z "$JAVA_OPTS" ]; then
#   JAVA_OPTS="-agentlib:ByteCodeDecryptor \
#-Djasypt.encryptor.password=gn4^Qa0k+WyeCkKt \
#-Dspring.cloud.nacos.config.server-addr=host.docker.internal:8848 \
#-Dspring.cloud.nacos.config.username=nacos \
#-Dspring.cloud.nacos.config.password=nacos \
#-Dspring.cloud.nacos.discovery.server-addr=host.docker.internal:8848 \
#-Dspring.cloud.nacos.discovery.username=nacos \
#-Dspring.cloud.nacos.discovery.password=nacos \
#-Dglobal.nacos.url=http://host.docker.internal:8848 \
#-Dglobal.nacos.user=nacos \
#-Dglobal.nacos.password=nacos \
#-Xms500m -Xmx500m -Xss256k "
#else
#   JAVA_OPTS="$JAVA_OPTS $JAVA_INIT_OPTS"
#fi
#
#echo "JAVA_OPTS: ${JAVA_OPTS}"
#
#
#
##set Argu，除JVM参数外的其他参数，可自定义，以下为例子
#
##  -Xloggc:/home/work/spring-boot/logs/gc-%t.log
#
## -javaagent:/usr/skywalking/agent/skywalking-agent.jar
#
## https://docs.oracle.com/javase/7/docs/technotes/tools/solaris/java.html#BGBJAAEH
#
#ARGU_OPTS=$ARGU_OPTS
#
#echo $ARGU_OPTS
#
#
#
##APP_OPTS应用�����行参数
#
##产品填写所需要APP_INIT_OPTS，并把注销除掉，否则使用默认值，默认值为空
#
##APP_INIT_OPTS=""
##APP_INIT_OPTS="--spring.profiles.active=dev --spring.cloud.nacos.config.server-addr=host.docker.internal:8848 --spring.cloud.nacos.discovery.server-addr=host.docker.internal:8848 --spring.cloud.nacos.config.namespace=dev --spring"
##APP_INIT_OPTS="--spring.profiles.active=dev --spring.cloud.nacos.config.server-addr=host.docker.internal:8848 --spring.cloud.nacos.config.username=nacos --spring.cloud.nacos.config.password=nacos --spring.cloud.nacos.config.namespace=dev --spring.cloud.nacos.discovery.server-addr=host.docker.internal:8848 --spring.cloud.nacos.discovery.username=nacos --spring.cloud.nacos.discovery.password=nacos"
##APP_INIT_OPTS="--spring.profiles.active=dev --spring.cloud.nacos.config.server-addr=host.docker.internal:8848 --spring.cloud.nacos.config.username=nacos --spring.cloud.nacos.config.password=$2a$10$HnB1fDeqxyU.dmtRRj3dT.oiPOJyEiFUO03cmuQ3bqMMX7i1jP5pq --spring.cloud.nacos.config.namespace=dev --spring.cloud.nacos.discovery.server-addr=host.docker.internal:8848 --spring.cloud.nacos.discovery.username=nacos --spring.cloud.nacos.discovery.password=$2a$10$HnB1fDeqxyU.dmtRRj3dT.oiPOJyEiFUO03cmuQ3bqMMX7i1jP5pq"
##APP_INIT_OPTS="--spring.profiles.active=dev --spring.cloud.nacos.config.server-addr=10.168.154.42:8848 --spring.cloud.nacos.config.username=nacos --spring.cloud.nacos.config.password=nacos --spring.cloud.nacos.config.namespace=dev --spring.cloud.nacos.discovery.server-addr=10.168.154.42:8848 --spring.cloud.nacos.discovery.username=nacos --spring.cloud.nacos.discovery.password=nacos"
#APP_INIT_OPTS="--spring.profiles.active=dev --spring.cloud.nacos.config.server-addr=host.docker.internal:8848 --spring.cloud.nacos.config.username=nacos --spring.cloud.nacos.config.password=nacos --spring.cloud.nacos.config.namespace=dev --spring.cloud.nacos.discovery.server-addr=host.docker.internal:8848 --spring.cloud.nacos.discovery.username=nacos --spring.cloud.nacos.discovery.password=nacos"
#
#APP_OPTS="$APP_OPTS $APP_INIT_OPTS"
#
#echo  "APP_OPTS: ${APP_OPTS}"
#
#
#
##stop
#
##STOP_CMD=`ps aux | grep java | grep ${WORK_HOME} | grep "$2" | awk '{print $2}'`
##
##if [ -n "$STOP_CMD" ];then
##
##   echo stop $(ps aux | grep java | grep ${WORK_HOME} | grep $2)
##
##   eval "kill -9 $STOP_CMD"
##
##fi
#
#
##start
#
#echo "starting..."
#
#
#RUN_CMD="java ${ARGU_OPTS} ${JAVA_OPTS} -jar $app_jar $APP_OPTS"
#
#echo $RUN_CMD
#
##eval $RUN_CMD
#
#java ${ARGU_OPTS} ${JAVA_OPTS} -jar ${app_jar} ${APP_OPTS}



#!/bin/bash

export LANG="en_US.UTF-8"
CURR_DIR=`pwd`
cd `dirname "$0"`
WORK_HOME=`pwd`
app_jar=$1

echo "Work Home: ${WORK_HOME}"

# 1. ЖЕСТКО задаем JAVA_OPTS
#JAVA_OPTS="-agentlib:ByteCodeDecryptor \
#-Djasypt.encryptor.password=gn4^Qa0k+WyeCkKt \
#-Dspring.cloud.nacos.config.server-addr=10.168.154.42:8848 \
#-Dspring.cloud.nacos.config.username=nacos \
#-Dspring.cloud.nacos.config.password=nacos \
#-Dspring.cloud.nacos.discovery.server-addr=10.168.154.42:8848 \
#-Dspring.cloud.nacos.discovery.username=nacos \
#-Dspring.cloud.nacos.discovery.password=nacos \
#-Dglobal.nacos.url=10.168.154.42:8848 \
#-Dglobal.nacos.user=nacos \
#-Dglobal.nacos.password=nacos \
#-Xms500m -Xmx500m -Xss256k"

JAVA_OPTS="-agentlib:ByteCodeDecryptor \
-Dspring.cloud.nacos.config.server-addr=10.168.154.42:8848 \
-Dspring.cloud.nacos.config.username=nacos \
-Dspring.cloud.nacos.config.password=nacos \
-Dspring.cloud.nacos.discovery.server-addr=10.168.154.42:8848 \
-Dspring.cloud.nacos.discovery.username=nacos \
-Dspring.cloud.nacos.discovery.password=nacos \
-Dglobal.nacos.url=10.168.154.42:8848 \
-Dglobal.nacos.user=nacos \
-Dglobal.nacos.password=nacos"

# 2. Если были дополнительные JAVA_INIT_OPTS, просто добавляем их в конец
if [ -n "$JAVA_INIT_OPTS" ]; then
   JAVA_OPTS="$JAVA_OPTS $JAVA_INIT_OPTS"
fi

echo "JAVA_OPTS: ${JAVA_OPTS}"

ARGU_OPTS=$ARGU_OPTS
echo "ARGU_OPTS: ${ARGU_OPTS}"

# 3. APP_INIT_OPTS также используем host.docker.internal
APP_INIT_OPTS="--spring.profiles.active=dev \
--spring.cloud.nacos.config.server-addr=10.168.154.42:8848 \
--spring.cloud.nacos.config.username=nacos \
--spring.cloud.nacos.config.password=nacos \
--spring.cloud.nacos.config.namespace=dev \
--spring.cloud.nacos.discovery.server-addr=10.168.154.42:8848 \
--spring.cloud.nacos.discovery.username=nacos \
--spring.cloud.nacos.discovery.password=nacos \
--debug"

APP_OPTS="$APP_OPTS $APP_INIT_OPTS"
echo "APP_OPTS: ${APP_OPTS}"

echo "starting..."

RUN_CMD="java ${ARGU_OPTS} ${JAVA_OPTS} -jar $app_jar $APP_OPTS"
echo "RUN_CMD: $RUN_CMD"

# Запуск
java ${ARGU_OPTS} ${JAVA_OPTS} -jar ${app_jar} ${APP_OPTS}