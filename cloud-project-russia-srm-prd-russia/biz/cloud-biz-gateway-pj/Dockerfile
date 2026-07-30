# cloud-biz-gateway-pj/Dockerfile
FROM hub.gwm.cn/feishengchancaigoushuzihuapingtai-test/srm/java-mc-srm:8
#FROM hub.gwm.cn/feishengchancaigoushuzihuapingtai-test/srm/srm-backend-gateway:meiqing-V3.0.11

USER root
RUN mkdir -p /apps/logs && chown -R apps:apps /apps/logs
USER apps

COPY target/*-boot.jar app.jar
#COPY ./lib/*.jar /home/apps/plugins/
COPY target/*.jar /home/apps/plugins/
COPY run.sh .

ENTRYPOINT ["sh", "run.sh", "app.jar"]