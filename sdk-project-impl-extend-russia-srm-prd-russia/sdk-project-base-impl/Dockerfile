## 基于产品镜像SDK
#FROM hub.gwm.cn/feishengchancaigoushuzihuapingtai-test/srm/srm-backend-base:meiqing-V3.0.11
#
#WORKDIR /home/apps/
#
#RUN mkdir -p /home/apps/plugins \
#&& mkdir -p /home/apps/BOOT-INF/classes/com/midea/cloud/
#COPY ./target/sdk-project-entity/com/midea/cloud/ /home/apps/BOOT-INF/classes/com/midea/cloud/
#COPY ./target/classes/meiql-schema/base_organization_ide.json /home/apps/BOOT-INF/classes/meiql-schema/
#
#USER root
#RUN zip -r -u /home/apps/app.jar BOOT-INF/classes/com/midea/cloud/* -n jar
#RUN zip -r -u /home/apps/app.jar BOOT-INF/classes/meiql-schema/* -n jar
#
#COPY ./target/sdk-project-base-impl*.jar /home/apps/plugins
#COPY ./lib/gwm-open-sdk*.jar /home/apps/plugins/
#COPY run.sh .
#
#ENTRYPOINT ["sh", "run.sh", "app.jar"]

#v2
#FROM hub.gwm.cn/feishengchancaigoushuzihuapingtai-test/srm/java-mc-srm:8
#
#USER root
## Создаем папки и выдаем права пользователю apps
#RUN mkdir -p /apps/logs /apps/plugins && chown -R apps:apps /apps
#USER apps
#
#WORKDIR /apps
#
## Копируем ТОЛЬКО исполняемый boot-jar
#COPY target/*.jar app.jar
#
## Копируем скрипт запуска (создадим его на шаге 4)
#COPY run.sh .
##RUN chmod +x run.sh
#
#EXPOSE 8831
#
#ENTRYPOINT ["./run.sh", "app.jar"]

# v3
FROM hub.gwm.cn/feishengchancaigoushuzihuapingtai-test/srm/java-mc-srm:8

USER root
RUN mkdir -p /apps/logs /apps/plugins && chown -R apps:apps /apps
USER apps

WORKDIR /apps

COPY target/*.jar app.jar

# Для gateway нужно отключить DataSource
# Создаем переменную окружения для определения типа модуля
ENV APP_TYPE=base

EXPOSE 8831

ENTRYPOINT ["java", \
  "-agentlib:ByteCodeDecryptor", \
  "-Djasypt.encryptor.password=gn4^Qa0k+WyeCkKt", \
  "-Dspring.cloud.nacos.config.server-addr=http://10.168.154.42:8848", \
  "-Dspring.cloud.nacos.config.username=nacos", \
  "-Dspring.cloud.nacos.config.password=nacos", \
  "-Dspring.cloud.nacos.discovery.server-addr=http://10.168.154.42:8848", \
  "-Dspring.cloud.nacos.discovery.username=nacos", \
  "-Dspring.cloud.nacos.discovery.password=nacos", \
  "-Dglobal.nacos.url=http://10.168.154.42:8848", \
  "-Dglobal.nacos.user=nacos", \
  "-Dglobal.nacos.password=nacos", \
  "-Xms500m", "-Xmx500m", "-Xss256k", \
  "-jar", "app.jar", \
  "--spring.profiles.active=dev", \
  "--spring.cloud.nacos.config.namespace=dev"]