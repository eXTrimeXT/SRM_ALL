FROM hub.gwm.cn/feishengchancaigoushuzihuapingtai-test/app/srm/srm-backend-supplier-cooperate:2.1.9.2
USER root

RUN mkdir -p /usr/src/plugins
WORKDIR /usr/src/
RUN mkdir -p /usr/src/BOOT-INF/classes/com/midea/cloud/srm/model
RUN mkdir -p /usr/src/BOOT-INF/classes/com/midea/cloud/common

RUN mkdir -p /usr/share/fonts/win


COPY . .
RUN ls ./

RUN jar -xvf sdk-project-supplier-cooperate-impl.jar
RUN mv sdk-project-supplier-cooperate-impl.jar /tmp/
run ls ./classes/template/fonts
RUN cp -rf ./classes/template/fonts /usr/share/fonts/win
RUN cp -rf ./sdk-project-entity/com/midea/cloud/srm/model/* /usr/src/BOOT-INF/classes/com/midea/cloud/srm/model
RUN cp -rf ./classes/com/midea/cloud/srm/* /usr/src/BOOT-INF/classes/com/midea/cloud/srm
RUN cp -rf ./classes/com/midea/cloud/common/* /usr/src/BOOT-INF/classes/com/midea/cloud/common
RUN ls /usr/src/BOOT-INF/classes/com/midea/cloud
RUN jar uvf app.jar BOOT-INF/classes/com/midea/cloud


ENV aliyun_logs_app-stdout-log=stdout
ENV aliyun_logs_app-stdout-log_tags="app-name=srm-java-cooperate-srm-midea-module"
ENV aliyun_logs_app-file-log=/apps/logs/*.log
ENV aliyun_logs_app-file-log_tags="app-name=srm-java-cooperate-srm-midea-module"
ENV JAVA_OPTS='-Dsun.jnu.encoding=UTF-8 -Dfile.encoding=UTF-8 -agentlib:ByteCodeDecryptor -Xms2000m -Xmx2000m'
ENV APP_OPTS='  --spring.config.additional-location=/apps/conf/  --spring.cloud.nacos.discovery.ip=srm-cnp-bdtest.gwmit.cn  --spring.cloud.nacos.discovery.port=80'
RUN cp ./*.jar /usr/src/plugins
COPY  ./*.yaml /apps/conf/
RUN mv /tmp/sdk-project-supplier-cooperate-impl.jar .
ENTRYPOINT sh run.sh app.jar