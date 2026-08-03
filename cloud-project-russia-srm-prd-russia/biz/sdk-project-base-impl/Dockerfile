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



# 1. Используем базовый образ, который уже содержит app.jar
FROM hub.gwm.cn/feishengchancaigoushuzihuapingtai-test/srm/srm-backend-base:meiqing-V3.0.11

WORKDIR /home/apps/

RUN mkdir -p /home/apps/plugins \
&& mkdir -p /home/apps/BOOT-INF/classes/com/midea/cloud/

# 2. Копируем классы для обновления
COPY ./target/sdk-project-entity/com/midea/cloud/ /home/apps/BOOT-INF/classes/com/midea/cloud/
COPY ./target/classes/meiql-schema/base_organization_ide.json /home/apps/BOOT-INF/classes/meiql-schema/

USER root
# 3. Обновляем исполняемый app.jar
RUN zip -r -u /home/apps/app.jar BOOT-INF/classes/com/midea/cloud/* -n jar
RUN zip -r -u /home/apps/app.jar BOOT-INF/classes/meiql-schema/* -n jar

# 4. Копируем плагины
COPY ./target/sdk-project-base-impl*.jar /home/apps/plugins
COPY ./lib/gwm-open-sdk*.jar /home/apps/plugins/
ENTRYPOINT []

# 5. ВАЖНО: Мы НЕ указываем ENTRYPOINT здесь.
# Запуск будет полностью контролироваться через 'command' в docker-compose.yml