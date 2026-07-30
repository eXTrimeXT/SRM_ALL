#!/bin/bash

export LANG="en_US.UTF-8"
CURR_DIR=`pwd`
cd `dirname "$0"`
WORK_HOME=`pwd`
app_jar=$1

echo "Work Home: ${WORK_HOME}"

JAVA_OPTS="-agentlib:ByteCodeDecryptor \
-Djasypt.encryptor.password=gn4^Qa0k+WyeCkKt \
-Dspring.cloud.nacos.config.server-addr=host.docker.internal:8848 \
-Dspring.cloud.nacos.config.username=nacos \
-Dspring.cloud.nacos.config.password=nacos \
-Dspring.cloud.nacos.discovery.server-addr=host.docker.internal:8848 \
-Dspring.cloud.nacos.discovery.username=nacos \
-Dspring.cloud.nacos.discovery.password=nacos \
-Dglobal.nacos.url=http://host.docker.internal:8848 \
-Dglobal.nacos.user=nacos \
-Dglobal.nacos.password=nacos \
-Xms500m -Xmx500m -Xss256k"

if [ -n "$JAVA_INIT_OPTS" ]; then
   JAVA_OPTS="$JAVA_OPTS $JAVA_INIT_OPTS"
fi

echo "JAVA_OPTS: ${JAVA_OPTS}"

ARGU_OPTS=$ARGU_OPTS
echo "ARGU_OPTS: ${ARGU_OPTS}"

APP_INIT_OPTS="--spring.profiles.active=dev \
--spring.cloud.nacos.config.server-addr=host.docker.internal:8848 \
--spring.cloud.nacos.config.username=nacos \
--spring.cloud.nacos.config.password=nacos \
--spring.cloud.nacos.config.namespace=dev \
--spring.cloud.nacos.discovery.server-addr=host.docker.internal:8848 \
--spring.cloud.nacos.discovery.username=nacos \
--spring.cloud.nacos.discovery.password=nacos"

APP_OPTS="$APP_OPTS $APP_INIT_OPTS"
echo "APP_OPTS: ${APP_OPTS}"

echo "starting..."

RUN_CMD="java ${ARGU_OPTS} ${JAVA_OPTS} -jar $app_jar $APP_OPTS"
echo "RUN_CMD: $RUN_CMD"

java ${ARGU_OPTS} ${JAVA_OPTS} -jar ${app_jar} ${APP_OPTS}