FROM hub.gwm.cn/feishengchancaigoushuzihuapingtai-test/srm/srm-backend-oauth:2.1.5.6
USER root
RUN mkdir -p /usr/src/plugins
WORKDIR /usr/src/
COPY ./target/*.jar /usr/src/plugins
ENTRYPOINT sh run.sh app.jar

