# 基于美云统一基础镜像
FROM hub.gwm.cn/feishengchancaigoushuzihuapingtai-test/srm/java-mc-srm:8
USER apps

COPY target/*-boot.jar app.jar
COPY ./lib/*.jar /home/apps/plugins/
COPY run.sh .
ENTRYPOINT sh run.sh app.jar
