/**
 * 供应商 API
 * 注册
 */

import http from '@/utils/axios/http'
const getUrl = path => `${path}`


// 信息注册
export const vendorRegisterApi = {
  // 供应商注册撤回
  companyWithdraw: async data =>
    http({
      url: getUrl('/api-sup/info/vendorInformation/withdrawCompany'),
      method: 'POST',
      data,
      loading: true
    })

}

// 供应商相关公共接口
export const vendorOptCommonApi = {
  // 公司性质以及境外关系查询配置信息
  getConfigByTemplate: async data =>
    http({
      url: '/api-sup/dim/dimTemplate/getConfigByTemplate',
      method: 'POST',
      data: data
    }),
  // 查询公司信息
  getCompanyForEdit: async data =>
    http({
      url: '/api-sup/info/companyInfo/getInfoByParam',
      method: 'POST',
      params: data,
      loading: true
    }),
    // 供应商公司信息维护 提交
  submitCompany: async data =>
    http({
      url: '/api-sup/info/companyInfo/submitInfo',
      method: 'POST',
      data: data,
      loading: true
    }),
  // 供应商公司信息维护 暂存
  saveOrUpdateCompany: async data =>
    http({
      url: '/api-sup/info/companyInfo/saveOrUpdateInfo',
      method: 'POST',
      data: data,
      loading: true
    }),

  // 供应商公司信息维护 暂存 MQL
  saveCompanyMQL: async data =>
    http({
      url: '/api-sup/api-ql/CompanyInfo/updateFirstLog',
      method: 'POST',
      data: data,
      loading: true
    }),
  // 供应商公司信息维护 撤回 MQL
  withdrawCompanyMQL: async data =>
    http({
      url: '/api-sup/api-ql/CompanyInfo/vendorWithdraw',
      method: 'POST',
      data: data,
      loading: true
    })
}

// 通知公告接口
export const noticeApi = {
  getNoticeInfo: async params =>
    http({
      url: '/api-base/notice/notice/pj/get',
      method: 'GET',
      params
    }),
  noticeList: async data =>
    http({
      url: '/api-base/notice/notice/pj/listPage',
      method: 'POST',
      data
    }),
  addNotice: async data =>
    http({
      url: '/api-base/notice/notice/add',
      method: 'POST',
      data
    }),
  modifyNotice: async data =>
    http({
      url: '/api-base/notice/notice/modify',
      method: 'POST',
      data
    }),
  deleteNotice: async params =>
    http({
      url: '/api-base/notice/notice/delete',
      method: 'GET',
      params
    }),
  publishNotice: async data =>
    http({
      url: '/api-base/notice/notice/publish',
      method: 'POST',
      data
    })
}

// 接口配置
export const interfaceApi = {
  getInterfaceconfig: async params =>
    http({
      url: '/api-ac/interfaceconfig/get',
      method: 'GET',
      params,
      loading: true
    }),
  interfaceconfigSave: async data =>
    http({
      url: '/api-ac/interfaceconfig/save',
      method: 'POST',
      data
    }),
  interfaceTest: async params =>
    http({
      url: '/api-ac/interface/test',
      method: 'GET',
      params
    }),
  getInterfaceLog: async params =>
    http({
      url: '/api-ac/interfacelog/get',
      method: 'GET',
      params
    }),
  sendInterface: async data =>
    http({
      url: '/api-ac/interfacelog/send',
      method: 'POST',
      data
    }),
  sendAgain: async params =>
    http({
    url: '/api-ac/interfacelog/sendAgain',
    method: 'GET',
    params
  }),
  getHttpColumns: async data =>
    http({
      url: getUrl('/api-ac/interfaceconfig/getHttpColumns'),
      method: 'POST',
      data,
      loading: true
    }),
  getColumnBySql: async params =>
    http({
      url: getUrl('/api-ac/interfaceconfig/getColumnBySql'),
      method: 'POST',
      params,
      loading: true
    }),
  getSqlResults: async data =>
    http({
      url: getUrl('/api-ac/interfaceconfig/getSqlResults'),
      method: 'POST',
      data,
      loading: true
    }),
  showDoc: async data =>
    http({
      url: getUrl('/api-ac/interfaceconfig/showDoc'),
      method: 'POST',
      data,
      loading: true
    }),
  testInterface: async data =>
    http({
      url: getUrl('/api-ac/interfaceconfig/testInterface'),
      method: 'POST',
      data,
      loading: true
    }),
  getHttpParam: async data =>
    http({
      url: getUrl('/api-ac/interfaceconfig/getHttpParam'),
      method: 'POST',
      data,
      loading: true
    })
}

// 系统配置
export const systemConfigApi = {
  systemConfigList: async data =>
    http({
      url: '/api-ac/systemconfig/listPage',
      method: 'POST',
      data
    }),
  systemconfigDel: async params =>
    http({
      url: '/api-ac/systemconfig/delete',
      method: 'GET',
      params
    }),
  systemconfigSaveOrUpdate: async params =>
    http({
      url: '/api-ac/systemconfig/saveOrUpdate',
      method: 'POST',
      data: params
    }),
  interfaceTest: async params =>
    http({
      url: '/api-ac/interface/test',
      method: 'GET',
      params
    })
}

// 用户信息操作
export const userOptApi = {
  modifyUser: async data =>
    http({
      url: '/api-rbac/user/modifyUser',
      method: 'POST',
      data
    }),
  checkOldPassword: async data =>
    http({
      url: '/api-rbac/user/checkOldPassword',
      method: 'POST',
      data
    }),
  modifyPassword: async data =>
    http({
      url: '/api-rbac/user/modifyPassword',
      method: 'POST',
      data
    }),
    // 上传人脸
  modifyFace: async data =>
    http({
      url: '/api-rbac/user-security/modifyFace',
      method: 'POST',
      data
    })
}

// 接口配置
