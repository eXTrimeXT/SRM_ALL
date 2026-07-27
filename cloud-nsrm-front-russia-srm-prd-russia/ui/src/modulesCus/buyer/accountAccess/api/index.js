/**
 * API 账号权限
 */
import http from '@/utils/axios/http'

// 功能维护
export const functionApi = {
  // 功能删除
  functionDel: async params =>
    http({
      url: '/api-rbac/function/delete',
      method: 'GET',
      params: params
    }),
  // 新增功能
  functionAdd: async params =>
    http({
      url: '/api-rbac/function/add',
      method: 'POST',
      data: params
    }),
  // 编辑功能
  functionModify: async params =>
    http({
      url: '/api-rbac/function/modify',
      method: 'POST',
      data: params
    }),
  // 查询
  functionGet: async params =>
    http({
      url: '/api-rbac/function/get',
      method: 'GET',
      params: params
    })
}

// 角色维护
export const roleApi = {
  // 角色新增
  roleAddHttp: async data =>
    http({
      url: '/api-rbac/role/role/add',
      method: 'POST',
      loading: true,
      data
    }),

  // 角色复制
  roleCopyHttp: async data =>
    http({
      url: '/api-rbac/role/role/copyRole',
      method: 'POST',
      loading: true,
      data
    }),

  // 角色修改
  roleModifyHttp: async data =>
    http({
      url: '/api-rbac/role/role/modify',
      method: 'POST',
      loading: true,
      data
    }),

  // 通过ID查询角色详情
  queryRoleInfoById: async id =>
    http({
      url: '/api-rbac/role/role/get',
      method: 'GET',
      loading: true,
      params: { id }
    }),
  // 按钮权限
  getbuttonTree: async data =>
    http({
      url: '/api-rbac/perm/permission/buttonTree',
      method: 'POST',
      data,
      loading: true
    })
}
// 菜单维护
export const menuApi = {
  // 获取权限
  getPermission: async params =>
    http({
      url: '/api-rbac/perm/permission/get',
      method: 'GET',
      params,
      loading: true
    }),
  // 修改菜单
  permissionModify: async (data, path) =>
    http({
      url: `/api-rbac/perm/permission/${path}`,
      method: 'POST',
      data,
      loading: true
    }),
  // 查询功能列表
  getParentOrg: async data =>
    http({
      url: '/api-rbac/function/listPage',
      method: 'POST',
      data,
      loading: true
    }),
  // 查询菜单树
  getPermissionTree: async data =>
    http({
      url: '/api-rbac/perm/permission/tree',
      method: 'POST',
      data,
      loading: false
    }),
  // 所有菜单
  getListChildrenData: async data =>
    http({
      url: '/api-rbac/perm/permission/listPage',
      method: 'POST',
      data,
      loading: true
    })
}
// 账号
export const accountApi = {
  // 根据用户账号获取用户信息(采购商)
  getByBuyer: async params =>
    http({
      url: '/api-rbac/extUser/getByBuyer',
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
      url: '/api-rbac/extUser/addBuyer',
      method: 'POST',
      data,
      loading: true
    }),

  // 新增用户（供应商）
  addVendor: async data =>
    http({
      url: '/api-rbac/user/addVendor',
      method: 'POST',
      data,
      loading: true
    }),

  // 修改用户信息（采购商）
  modifyBuyer: async data =>
    http({
      url: '/api-rbac/extUser/modifyBuyer',
      method: 'POST',
      data,
      loading: true
    }),

  // 修改用户信息（供应商）
  modifyVendor: async data =>
    http({
      url: '/api-rbac/user/modifyVendor',
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
// 角色权限
export const roleSetApi = {
  roleFuncSetDelete: async params =>
    http({
      url: '/api-rbac/role/roleFuncSet/delete',
      method: 'GET',
      params,
      loading: true
    }),

  roleFuncSetAdd: async data =>
    http({
      url: '/api-rbac/role/roleFuncSet/add',
      method: 'POST',
      data,
      loading: true
    }),

  roleFuncSetModify: async data =>
    http({
      url: '/api-rbac/role/roleFuncSet/modify',
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
