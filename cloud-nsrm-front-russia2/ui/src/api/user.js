import qs from 'qs'
import http from '@/utils/axios/http'
import routesMock from '@/router/routesMock'

// 用户相关
// 用户登录
// 获取密码加密公钥
export function getPassPublicKey () {
  return http({
    url: '/api-base/common/publicKey',
    method: 'GET'
  })
}
// 供应商密码修改-密码配置规则
export function getVendorPwdCheckConfig () {
  return http({
    url: '/api-rbac/vendor/security_config/getPwdCheckConfig',
    method: 'GET'
  })
}
//  采购商密码修改-密码配置规则
export function getBuyerPwdCheckConfig () {
  return http({
    url: '/api-rbac/buyer/security_config/getPwdCheckConfig',
    method: 'GET'
  })
}
// 查询审批人员(新)
export function getUserList (params) {
  return http({
    url: '/api-base/ext/flow/instance/task/user/query',
    method: 'POST',
    data: params,
    loading: true
  })
}
export function login (params) {
  return http({
    url: '/sys/login',
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, // 登录安全考虑，数据放到form-data下面
    data: qs.stringify(params)
  })
}
// 用户登录人脸
export function loginFace (params) {
  return http({
    url: '/sys/loginFace',
    method: 'POST',
    data: params
  })
}
// 获取用户信息
export function getUserInfo (params) {
  return http({
    url: '/api-rbac/user/current',
    method: 'GET',
    params: params
  })
}
// 查询审批人员 （分页获取采购商信息）
// userType:BUYER | VENDOR
export function listByBuyer (params) {
  return http({
    url: '/api-rbac/user/listByBuyer',
    method: 'POST',
    data: params,
    loading: true
  })
}
// 获取公司信息
export function getCompanyInfo (companyId) {
  return http({
    url: '/api-sup/info/companyInfo/get',
    method: 'GET',
    params: { companyId }
  })
}

// 用户退出
export function logout (params) {
  return http({
    url: '/sys/logout',
    method: 'POST',
    params: params
  })
}

// 获取语言
export function getLang () {
  return http({
    url: '/locale/get',
    method: 'GET'
  })
}

// 设置语言
export function modifyLang (params) {
  return http({
    url: '/locale/modify',
    method: 'GET',
    params: params
  })
}
// 确认协议
export function confirmDeal (type) {
  return http({
    url: '/api-rbac/user/confirmDeal',
    method: 'GET',
    params: { confirmType: type }
  })
}

// 获取用户菜单
export function getResuorceBySysAuth (params) {
  return routesMock
  // return http({
  //   url: '/quality/front/login/logout',
  //   method: 'post',
  //   params: params
  // })
}

// 查询品类层级数量
export function getCatLavel (params) {
  return http({
    url: '/api-base/serviceConfig/base-service-config/listAll',
    method: 'GET',
    params
  })
}

// 生成扫码二维码
export function genScanCode () {
  return http({
    url: '/sys/genScanCode',
    method: 'POST',
    data: {}
  })
}

// 获取二维码token
export function getScanCode (scanCode) {
  return http({
    url: '/sys/getScanCode',
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, // 登录安全考虑，数据放到form-data下面
    data: qs.stringify({ scanCode: scanCode })
  })
}
// 知识问答-获取token
export function getAssistantToken () {
  return http({
    url: '/api-pj/external/ai/getToken',
    method: 'POST',
    data: {}
  })
}

// 字典接口
export function getDictList (data) {
  return http({
    url: '/api-base/dict/base-dict-item/listByDictCode',
    method: 'POST',
    data: [data.code]
  })
}
