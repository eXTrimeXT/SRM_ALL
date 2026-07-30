# 基于美云统一基础镜像
FROM hub.gwm.cn/feishengchancaigoushuzihuapingtai-test/srm/java-mc-srm:8

# 1. Временно переключаемся на root, чтобы создать папки и выдать права
USER root

# 2. Создаем директорию для логов и назначаем владельцем пользователя apps
RUN mkdir -p /apps/logs && chown -R apps:apps /apps/logs

# 3. Возвращаемся к пользователю apps для безопасности
USER apps

# 4. Копируем файлы
COPY target/*-boot.jar app.jar
COPY ./lib/*.jar /home/apps/plugins/
COPY run.sh .

# 5. Делаем скрипт запуска исполняемым (важно для Linux-контейнеров)
#RUN chmod +x run.sh

# 6. Запускаем приложение
ENTRYPOINT ["sh", "run.sh", "app.jar"]