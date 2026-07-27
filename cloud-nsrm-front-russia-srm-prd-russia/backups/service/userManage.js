import http from '@/utils/axios/http'
// 公司性质以及境外关系查询配置信息
export function getConfigByTemplate (data) {
  return http({
    url: '/api-sup/dim/dimTemplate/getConfigByTemplate',
    method: 'POST',
    data: data
  })
}

// 查询公司信息
export function getCompanyForEdit (data) {
  return http({
    url: '/api-sup/info/companyInfo/getInfoByParam',
    method: 'POST',
    params: data,
    loading: true
  })
}

// 供应商公司信息维护 暂存
export function saveOrUpdateCompany (data) {
  return http({
    url: '/api-sup/info/companyInfo/saveOrUpdateInfo',
    method: 'POST',
    data: data,
    loading: true
  })
}

// 供应商公司信息维护 提交
export function submitCompany (data) {
  return http({
    url: '/api-sup/info/companyInfo/submitInfo',
    method: 'POST',
    data: data,
    loading: true
  })
}

// 校验用户名唯一

// /api-sup/register/checkUserName

export function getNoticeInfo (params) {
  return http({
    url: '/api-base/notice/notice/get',
    method: 'GET',
    params
  })
}

export function noticeList (data) {
  return http({
    url: '/api-base/notice/notice/listPage',
    method: 'POST',
    data
  })
}

export function addNotice (data) {
  return http({
    url: '/api-base/notice/notice/add',
    method: 'POST',
    data
  })
}

export function modifyNotice (data) {
  return http({
    url: '/api-base/notice/notice/modify',
    method: 'POST',
    data
  })
}

export function deleteNotice (params) {
  return http({
    url: '/api-base/notice/notice/delete',
    method: 'GET',
    params
  })
}

export function publishNotice (data) {
  return http({
    url: '/api-base/notice/notice/publish',
    method: 'POST',
    data
  })
}

export function modifyUser (data) {
  return http({
    url: '/api-rbac/user/modifyUser',
    method: 'POST',
    data
  })
}

export function checkOldPassword (data) {
  return http({
    url: '/api-rbac/user/checkOldPassword',
    method: 'POST',
    data
  })
}

export function modifyPassword (data) {
  return http({
    url: '/api-rbac/user/modifyPassword',
    method: 'POST',
    data
  })
}

export function getInterfaceLog (params) {
  return http({
    url: '/api-ac/interfacelog/get',
    method: 'GET',
    params
  })
}

export function sendInterface (data) {
  return http({
    url: '/api-ac/interfacelog/send',
    method: 'POST',
    data
  })
}

export function sendAgain (params) {
  return http({
    url: '/api-ac/interfacelog/sendAgain',
    method: 'GET',
    params
  })
}

export function systemConfigList (data) {
  return http({
    url: '/api-ac/systemconfig/listPage',
    method: 'POST',
    data
  })
}

export function systemconfigDel (params) {
  return http({
    url: '/api-ac/systemconfig/delete',
    method: 'GET',
    params
  })
}

export function systemconfigSaveOrUpdate (params) {
  return http({
    url: '/api-ac/systemconfig/saveOrUpdate',
    method: 'POST',
    data: params
  })
}

export function interfaceTest (params) {
  return http({
    url: '/api-ac/interface/test',
    method: 'GET',
    params
  })
}

export function interfaceconfigList (data) {
  return http({
    url: '/api-ac/interfaceconfig/listPage',
    method: 'POST',
    data
  })
}

export function interfaceconfigSave (data) {
  return http({
    url: '/api-ac/interfaceconfig/save',
    method: 'POST',
    data
  })
}

export function getInterfaceconfig (params) {
  return http({
    url: '/api-ac/interfaceconfig/get',
    method: 'GET',
    params,
    loading: true
  })
}

// 上传人脸
export function modifyFace (data) {
  return http({
    url: '/api-rbac/user-security/modifyFace',
    method: 'POST',
    data
  })
}
