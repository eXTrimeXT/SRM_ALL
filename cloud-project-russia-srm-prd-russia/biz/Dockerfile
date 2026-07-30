# Используем базовый образ
FROM hub.gwm.cn/feishengchancaigoushuzihuapingtai-test/srm/java-mc-srm:8

# Создаем папки и даем права
USER root
RUN mkdir -p /apps/logs /apps/plugins && chown -R apps:apps /apps
USER apps

WORKDIR /apps

# Копируем собранные исполняемые jar-файлы.
# Внутри них УЖЕ встроены все библиотеки (api, commons, feign) благодаря Maven!
COPY cloud-biz-gateway-pj/target/*-boot.jar /apps/gateway.jar
COPY cloud-biz-pj/target/*-boot.jar /apps/pj.jar


# Копируем скрипт запуска
COPY run.sh .
#RUN chmod +x run.sh

# Открываем порты Шлюза и PJ модуля
EXPOSE 9005 8845

# Запускаем скрипт
ENTRYPOINT ["./run.sh"]