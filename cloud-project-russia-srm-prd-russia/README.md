# cloud-srm
- cloud-biz-api-pj 项目数据模型API【整个SRM所有的java bean，包含entity、dto等】
- cloud-biz-commons-pj 项目通用共享包【包含共享的工具、枚举、常量、业务基类等】
- cloud-biz-feign-pj 接口feign rpc调用，远程调用接口定义模块.
- cloud-biz-gateway-pj 接口API本地调试统一入口【整个项目统一的路由入口】
- cloud-biz-pj 项目二开模块【所有的二开代码在此定义】

# nacos配置导入
将目录nacos/srm/srm-biz-pj导入到srm注册的命名空间下，并且分组设置为srm

# nacos各模块配置命名
srm-base.yml
srm-biz-pj.yml
srm-contract.yml
srm-cost.yml
srm-file-center.yml
srm-gateway.yml
srm-inq.yml
srm-oauth-center.yml
srm-onlineview-center.yml
srm-performance.yml
srm-ppap.yml
srm-price.yml
srm-rbac-center.yml
srm-reduce.yml
srm-report.yml
srm-sou.yml
srm-supplier.yml
srm-supplier-cooperate.yml
srm-qc.yml 
#祝你开发愉快2
