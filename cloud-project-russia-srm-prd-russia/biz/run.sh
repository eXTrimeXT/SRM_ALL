#!/bin/bash

export LANG="en_US.UTF-8"
echo "=========================================================="
echo "  Запуск SRM Backend (Gateway + PJ) в одном контейнере"
echo "=========================================================="

# Общие JVM параметры (дешифровка + Nacos)
JAVA_OPTS="-agentlib:ByteCodeDecryptor \
-Djasypt.encryptor.password=gn4^Qa0k+WyeCkKt \
-Dspring.cloud.nacos.config.server-addr=host.docker.internal:8848 \
-Dspring.cloud.nacos.config.username=nacos \
-Dspring.cloud.nacos.config.password=nacos \
-Dspring.cloud.nacos.discovery.server-addr=host.docker.internal:8848 \
-Dspring.cloud.nacos.discovery.username=nacos \
-Dspring.cloud.nacos.discovery.password=nacos \
-Xms500m -Xmx500m -Xss256k"

# Общие параметры приложения
APP_OPTS="--spring.profiles.active=dev --spring.cloud.nacos.config.namespace=dev"

# ВАЖНО: Отключаем автоконфигурацию БД для Шлюза, иначе он упадет с ошибкой DataSource
GATEWAY_OPTS="$APP_OPTS --spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"

echo "[1/2] Запуск Gateway (порт 9005)..."
java $JAVA_OPTS -jar /apps/gateway.jar $GATEWAY_OPTS > /apps/logs/gateway.log 2>&1 &
GATEWAY_PID=$!
echo "      -> Gateway запущен (PID: $GATEWAY_PID)"

# Небольшая пауза, чтобы шлюз успел инициализироваться
sleep 5

echo "[2/2] Запуск PJ Module (порт 8845)..."
java $JAVA_OPTS -jar /apps/pj.jar $APP_OPTS > /apps/logs/pj.log 2>&1 &
PJ_PID=$!
echo "      -> PJ Module запущен (PID: $PJ_PID)"

echo "=========================================================="
echo "  Все сервисы запущены. Вывод логов (Ctrl+C для выхода):"
echo "=========================================================="

# Выводим логи обоих приложений в реальном времени
tail -f /apps/logs/gateway.log /apps/logs/pj.log