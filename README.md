http://10.168.130.235:8081/#browse/browse:maven-public:com%2Fmidea%2Fcloud-biz%2Fmeiqing-V3.0.11.1

# Пример запуска NameServer и Broker
docker run -d -p 9876:9876 --name rmqnamesrv apache/rocketmq:5.1.4 sh mqnamesrv
docker run -d -p 10911:10911 -p 10909:10909 --link rmqnamesrv:namesrv -e "NAMESRV_ADDR=rmqnamesrv:9876" --name rmqbroker apache/rocketmq:5.1.4 sh mqbroker -n namesrv:9876