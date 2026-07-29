# 基于产品镜像SDK
FROM hub.gwm.cn/feishengchancaigoushuzihuapingtai-test/srm/srm-backend-base:meiqing-V3.0.11

WORKDIR /home/apps/

RUN mkdir -p /home/apps/plugins \
&& mkdir -p /home/apps/BOOT-INF/classes/com/midea/cloud/
COPY ./target/sdk-project-entity/com/midea/cloud/ /home/apps/BOOT-INF/classes/com/midea/cloud/
COPY ./target/classes/meiql-schema/base_organization_ide.json /home/apps/BOOT-INF/classes/meiql-schema/

USER root
RUN zip -r -u /home/apps/app.jar BOOT-INF/classes/com/midea/cloud/* -n jar
RUN zip -r -u /home/apps/app.jar BOOT-INF/classes/meiql-schema/* -n jar

COPY ./target/sdk-project-base-impl*.jar /home/apps/plugins
COPY ./lib/gwm-open-sdk*.jar /home/apps/plugins/
q
ENTRYPOINT sh run.sh app.jar