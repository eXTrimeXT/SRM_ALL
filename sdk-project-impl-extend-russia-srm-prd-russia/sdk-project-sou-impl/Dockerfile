# 基于产品镜像SDK
FROM hub.gwm.cn/feishengchancaigoushuzihuapingtai-test/srm/srm-backend-sou:meiqing-V3.0.11

WORKDIR /home/apps/

RUN mkdir -p /home/apps/plugins \
&& mkdir -p /home/apps/BOOT-INF/classes/com/midea/cloud/
COPY ./target/sdk-project-entity/com/midea/cloud/ /home/apps/BOOT-INF/classes/com/midea/cloud/

USER root
RUN zip -r -u /home/apps/app.jar BOOT-INF/classes/com/midea/cloud/* -n jar

COPY ./lib/com*.jar /home/apps/plugins/
COPY ./target/lib/*.jar /home/apps/plugins/

COPY ./target/sdk-project-sou-impl*.jar /home/apps/plugins

ENTRYPOINT sh run.sh app.jar