// 配置编译环境和线上环境之间的切换
const config = {
  baseUrl: '', // 对应环境-服务器地址
  env: '',
  iamUrl: '', // iam域名地址
  flowEnv: '', // 流程审批用到 测试环境用到流程那边的uat环境
  webName: '', // 对应环境名称
  isSinglePoint: '', // 是否开启单点登录
  singlebaseUrl: '', // 单点登录baseUrl
  isWorkchatLogin: '' // 开启企业微信登录
}
switch (
  import.meta.env.VUE_APP_ENV // 判断环境类型
) {
  case 'PROD':
    config.baseUrl = import.meta.env.VUE_APP_BASE_URL
    config.flowEnv = 'prod'
    config.env = 'prod'
    config.webName = 'DEMO'
    config.iamUrl = 'https://t000000001.iam-uat.meicloud.com'
    config.isSinglePoint = import.meta.env.VUE_APP_ISSINGLEPOINT
    config.singlebaseUrl = import.meta.env.VUE_APP_BASE_URL
    config.isWorkchatLogin = import.meta.env.VUE_APP_ISWORKCHATLOGIN
    break
  case 'UAT':
    config.baseUrl = import.meta.env.VUE_APP_BASE_URL
    config.flowEnv = 'uat'
    config.env = 'uat'
    config.webName = 'DEMO'
    config.iamUrl = 'https://t000000001.iam-uat.meicloud.com'
    config.isSinglePoint = import.meta.env.VUE_APP_ISSINGLEPOINT
    config.singlebaseUrl = import.meta.env.VUE_APP_BASE_URL
    config.isWorkchatLogin = import.meta.env.VUE_APP_ISWORKCHATLOGIN
      break
  case 'SIT':
    config.baseUrl = import.meta.env.VUE_APP_BASE_URL
    config.flowEnv = 'uat'
    config.env = 'sit'
    config.webName = 'SIT'
    config.iamUrl = 'https://tusersit01.iam-sit-stable.meicloud.com'
    config.isSinglePoint = import.meta.env.VUE_APP_ISSINGLEPOINT
    config.singlebaseUrl = import.meta.env.VUE_APP_BASE_URL
    config.isWorkchatLogin = import.meta.env.VUE_APP_ISWORKCHATLOGIN
    break
  case 'DEV':
    config.baseUrl = import.meta.env.VUE_APP_BASE_URL
    config.flowEnv = 'uat'
    config.env = 'dev'
    config.webName = 'DEV'
    config.iamUrl = 'https://iam-dev-stable.meicloud.com'
    config.isSinglePoint = import.meta.env.VUE_APP_ISSINGLEPOINT
    config.singlebaseUrl = import.meta.env.VUE_APP_BASE_URL
    config.isWorkchatLogin = import.meta.env.VUE_APP_ISWORKCHATLOGIN
    break
  default: // 本地运行时取以下地址
    config.baseUrl = 'https://srm-dev.meicloud.com'
    config.flowEnv = 'uat'
    config.env = 'sit' // 本地调试单点登录cookie域名的话设置为 uat  默认 sit
    config.webName = 'DEV'
    config.iamUrl = 'https://iam-dev-stable.meicloud.com'
    config.isSinglePoint = 'Y' // 默认关闭
    config.singlebaseUrl = 'http://10.73.40.31:8099' // 本地调试单点，需要添加配置
    config.isWorkchatLogin = import.meta.env.VUE_APP_ISWORKCHATLOGIN
    break
}
export default config
