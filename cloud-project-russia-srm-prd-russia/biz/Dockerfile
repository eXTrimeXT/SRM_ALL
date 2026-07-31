FROM hub.gwm.cn/feishengchancaigoushuzihuapingtai-test/srm/java-mc-srm:8

# Переключаемся на root, чтобы создать папки и выдать права
USER root
RUN mkdir -p /apps/logs /apps/plugins && chown -R apps:apps /apps

WORKDIR /apps

# Принимаем имя файла как аргумент при сборке
ARG JAR_FILE
COPY ${JAR_FILE} app.jar

# ВАЖНО: Эта строка полностью отключает скрытые скрипты запуска базового образа!
ENTRYPOINT []

EXPOSE 8845