import { getEntranceType, getToken } from '@/utils/auth'
// 在调用多个系统的数据接口做的接口前缀适配
// 在调用多个系统的数据时可灵活调用
// 系统迁移修改前缀的时候方便配置
// srm系统

export const sysPrefix = () => {
  let token = getToken() // 获取token
  let entrance = getEntranceType() // 获取登录类型 // inside 内部登录方式 || singlePoint 单点登录方式
  let singlePointType = singlePointDev()
  if (entrance == 'singlePoint' && token) {
    return singlePointType ? debuggerSinglePointUrlPrefix() : '/cloud-srm' // /flow/cloud-srm
  } else {
    return '/cloud-srm'
  }
}
// 获取本地调试单点url网关前缀
export const debuggerSinglePointUrlPrefix = () => {
  if (debuggerSinglePointType() === 'mideaNetwork') {
    return '/cloud-srm' // /flow/api/cloud-srm
  } else {
    return '/cloud-srm' // /ssc/workflow/cloud-srm
  }
}
// 本地调试单点类型 mideaNetwork 表示在美的网络下可联调线上ipass | local 表示后端本地启动的ipass
export const debuggerSinglePointType = () => 'local'

// 本地调试单点和后端约定名称默认为空 添加值后不可提交，只做本地调试用 zjj
export const singlePointDev = () => ''

// 配置系统网关url
// 使用 getUrl(/api-ac/interfaceconfig/showDoc)
export const getUrl = path => `${sysPrefix()}${path}`
