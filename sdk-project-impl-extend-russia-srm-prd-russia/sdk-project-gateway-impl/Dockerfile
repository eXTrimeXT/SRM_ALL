## 基于产品镜像SDK
#FROM hub.gwm.cn/feishengchancaigoushuzihuapingtai-test/srm/srm-backend-gateway:meiqing-V3.0.11
#
#RUN mkdir -p /home/apps/plugins
#WORKDIR /home/apps/
#COPY ./target/*.jar /home/apps/plugins
#COPY run.sh .
#
#ENTRYPOINT ["sh", "run.sh", "app.jar"]

FROM hub.gwm.cn/feishengchancaigoushuzihuapingtai-test/srm/java-mc-srm:8

USER root
# Создаем папки и выдаем права пользователю apps
RUN mkdir -p /apps/logs /apps/plugins && chown -R apps:apps /apps
USER apps

WORKDIR /apps

# Копируем ТОЛЬКО исполняемый boot-jar
COPY target/*.jar app.jar

# Копируем скрипт запуска (создадим его на шаге 4)
COPY run.sh .
# RUN chmod +x run.sh

EXPOSE 9005

ENTRYPOINT ["./run.sh", "app.jar"]