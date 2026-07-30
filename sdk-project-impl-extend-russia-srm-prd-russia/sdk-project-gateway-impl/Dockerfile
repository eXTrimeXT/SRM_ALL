# 基于产品镜像SDK
FROM hub.gwm.cn/feishengchancaigoushuzihuapingtai-test/srm/srm-backend-gateway:meiqing-V3.0.11

RUN mkdir -p /home/apps/plugins
WORKDIR /home/apps/
COPY ./target/*.jar /home/apps/plugins
COPY run.sh .

ENTRYPOINT ["sh", "run.sh", "app.jar"]