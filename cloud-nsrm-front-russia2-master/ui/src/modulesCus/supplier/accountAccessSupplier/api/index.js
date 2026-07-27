/*
  子账号管理API
*/
import http from '@/utils/axios/http'
const getUrl = path => `${path}`

export const accountAccessApi = {
  deleteRow: async id =>
    http({
      url: `/api-rbac/extUser/deleteUser?userId=${id}`,
      method: 'GET',
      loading: true
    })
}

// 账号
export const accountApi = {
  // 根据用户账号获取用户信息(采购商)
  getByBuyer: async params =>
    http({
      url: '/api-rbac/user/getByBuyer',
      method: 'GET',
      params,
      loading: true
    }),

  // 根据用户账号获取用户信息（供应商）
  getByVendor: async params =>
    http({
      url: '/api-rbac/user/getByVendor',
      method: 'GET',
      params,
      loading: true
    }),

  // 新增用户（采购商）
  addBuyer: async data =>
    http({
      url: '/api-rbac/user/addBuyer',
      method: 'POST',
      data,
      loading: true
    }),

  // 新增用户（供应商）
  addVendor: async data =>
    http({
      url: '/api-rbac/extUser/addVendor',
      method: 'POST',
      data,
      loading: true
    }),

  // 修改用户信息（采购商）
  modifyBuyer: async data =>
    http({
      url: '/api-rbac/user/modifyBuyer',
      method: 'POST',
      data,
      loading: true
    }),

  // 修改用户信息（供应商）
  modifyVendor: async data =>
    http({
      url: '/api-rbac/extUser/modifyVendor',
      method: 'POST',
      data,
      loading: true
    }),

  // 重置密码（供应商）
  resetVendorPw: async data =>
    http({
      url: '/api-rbac/user/resetVendorPw',
      method: 'POST',
      data,
      loading: true
    }),

  // 重置密码（采购商）
  resetBuyerPw: async data =>
    http({
      url: '/api-rbac/user/resetBuyerPw',
      method: 'POST',
      data,
      loading: true
    }),

  // 重置密码
  resetUserPwByManage: async data =>
    http({
      url: '/api-rbac/user/resetUserPwByManage',
      method: 'POST',
      data,
      loading: true
    }),
  // 重置密码
  resetUserPwByManageIam: async data =>
    http({
      url: '/api-rbac/buyer/userinfo/resetPassword',
      method: 'POST',
      data,
      loading: true
    })
}
// 用户账号信息
export const accessApi = {
  // 角色列表
  roleListHttp: async data =>
    http({
      url: '/api-rbac/role/role/listPage',
      method: 'POST',
      loading: false,
      data
    }),

  // 接口权限
  getInterfaceTree: async data =>
    http({
      url: '/api-rbac/perm/permission/interfaceTree',
      method: 'POST',
      data,
      loading: true
    }),
  // 组织类型
  getOrganizationList: async (data = {}) =>
    http({
      url: '/api-base/organization/organizationsType/listAll',
      unToken: true,
      method: 'POST',
      data,
      loading: true
    }),
  // 语言信息
  getLanguageList: async (data = {}) =>
    http({
      url: '/api-base/dict/base-dict-language/listAll',
      method: 'POST',
      data,
      loading: true
    }),
  getResetError: async params =>
    http({
      url: '/api-sup-ce/erp/ErpController/getResetError',
      method: 'GET',
      params,
      loading: true
    }),

  getHeavyPush: async data =>
    http({
      url: '/api-base/workflow/getHeavyPush',
      method: 'POST',
      data,
      loading: true
    })
}
