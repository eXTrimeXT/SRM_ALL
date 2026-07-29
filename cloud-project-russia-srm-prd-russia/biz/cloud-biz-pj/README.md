# 二开模块


in run.sh:
```bash
APP_INIT_OPTS="--spring.profiles.active=dev --spring.cloud.nacos.config.server-addr=host.docker.internal:8848 --spring.cloud.nacos.discovery.server-addr=host.docker.internal:8848 --spring.cloud.nacos.config.namespace=dev"
```


```bash
docker build -t srm-biz-pj .
```


```bash
docker run -d --name srm-biz-pj-container -p 8845:8845 srm-biz-pj
```

