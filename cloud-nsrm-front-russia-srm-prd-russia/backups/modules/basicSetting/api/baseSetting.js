import http from '@/utils/http'
// 组织设置接口
// 组织设置 类别列表
export function getOrgTypeList (params) {
  return http({
    url: '/api-base/organization/organizationsType/listAll',
    method: 'POST',
    params: params
  })
}

// 批量添加组织类型
export function addOrgType (data) {
  return http({
    url: '/api-base/organization/organizationsType/saveOrUpdateBatch',
    method: 'POST',
    data: data
  })
}
// 查询上层组织
export function getParentOrgData (data) {
  return http({
    url: '/api-base/organization/organization/listParentOrganization',
    method: 'POST',
    data: data
  })
}
// 获取当前组织的信息 (编辑的时候先获取旧数据)
export function getOrgDataForEdit (params) {
  return http({
    url: '/api-base/organization/organization/get',
    method: 'GET',
    params: params
  })
}
// 获取当前组织的信息 (编辑的时候先获取旧数据)
export function getOrganization (params) {
  return http({
    url: '/api-base/organization/organization/getOrganization',
    method: 'GET',
    params: params,
    loading: true
  })
}
// 获取字典维护界面的信息
export function getDictInfo (params) {
  return http({
    url: '/api-base/dict/base-dict/queryPageByConditions/1/10',
    method: 'GET',
    params: params
  })
}

// 新增 或编辑组织接口
export function orgSaveOrUpdate (data) {
  return http({
    url: '/api-base/organization/organization/saveOrUpdate',
    method: 'POST',
    data: data,
    loading: true
  })
}
// 查询全部组织列表
export function getAllListOrgData (params) {
  return http({
    url: '/api-base/organization/organization/listAllOrganization',
    method: 'POST',
    params: params
  })
}

// 获取下一级节点
export function getListChildrenData (params) {
  return http({
    url: '/api-base/organization/relation/listChildrenOrganization',
    method: 'POST',
    params: params
  })
}

// 新增|编辑币种
export function currencySaveOrUpdate (data) {
  return http({
    url: '/api-base/purchase/purchaseCurrency/saveOrUpdate',
    method: 'POST',
    data: data
  })
}

// 查询币种
export function currencyGetListPage (params) {
  return http({
    url: '/api-base/purchase/purchaseCurrency/listPage',
    method: 'POST',
    params: params
  })
}

// 新增|编辑税率
export function rateSaveOrUpdate (data) {
  return http({
    url: '/api-base/purchase/purchaseTax/saveOrUpdate',
    method: 'POST',
    data: data
  })
}

// 查询税率
export function rateGetListPage (params) {
  return http({
    url: '/api-base/purchase/purchaseTax/listPage',
    method: 'POST',
    params: params
  })
}

// 新增|编辑税率
export function unitSaveOrUpdate (data) {
  return http({
    url: '/api-base/purchase/purchaseUnit/saveOrUpdate',
    method: 'POST',
    data: data
  })
}

// 查询税率
export function unitGetListPage (params) {
  return http({
    url: '/api-base/purchase/purchaseUnit/listPage',
    method: 'POST',
    params: params
  })
}

// 查询属性列表
export function fieldGetListPage (params) {
  return http({
    url: '/api-sup/dim/dimField/listPageByParam',
    method: 'POST',
    data: params
  })
}

// 新增或编辑属性
export function fieldSaveOrUpdate (data) {
  return http({
    url: '/api-sup/dim/dimField/saveOrUpdateField',
    method: 'POST',
    data: data
  })
}
// 获取属性信息
export function getFieldInfo (params) {
  return http({
    url: '/api-sup/dim/dimField/get',
    method: 'GET',
    params: params
  })
}
// 维度查询
export function getFieldDim (params) {
  return http({
    url: '/api-sup/dim/dim/listOrder',
    method: 'POST',
    params: params
  })
}
// 维度查询(下拉框)
export function getFieldDimS (params) {
  return http({
    url: '/api-sup/dim/dim/listAll',
    method: 'POST',
    params: params
  })
}
// 新增维度
export function addFieldDim (data) {
  return http({
    url: '/api-sup/dim/dim/add',
    method: 'POST',
    data: data
  })
}
// 编辑维度
export function editFieldDim (data) {
  return http({
    url: '/api-sup/dim/dim/modify',
    method: 'POST',
    data: data
  })
}
// 删除维度
export function deleteFieldDim (params) {
  return http({
    url: '/api-sup/dim/dim/delete',
    method: 'DELETE',
    params: params
  })
}

// 获取属性配置详细
export function getDimTemplateById (params) {
  return http({
    url: '/api-sup/dim/dimTemplate/getByTemplateId',
    method: 'GET',
    params: params
  })
}
// 获取属性配置 通过模板id和dimId 获取单个维度下面的配置信息
export function getTemplateByDimId (params) {
  return http({
    url: '/api-sup/dim/dimConfig/getDtoByParam',
    method: 'POST',
    data: params
  })
}

// 新增或保存属性配置
export function saveOrUpdateTemplate (data) {
  return http({
    url: '/api-sup/dim/dimTemplate/saveOrUpdateTemplate',
    method: 'POST',
    data: data
  })
}

// 修改维度基本信息
export function updateDimBasicData (data) {
  return http({
    url: '/api-sup/dim/dim/updateBasic',
    method: 'POST',
    data: data
  })
}
// 修改维度配置信息
export function definitionDimData (data) {
  return http({
    url: '/api-sup/dim/dim/definitionDim',
    method: 'POST',
    data: data
  })
}

// 业务状态新增 修改
export function saveOrUpdateBdaData (data) {
  return http({
    url: '/api-sup/bda/bdaState/saveOrUpdateBda',
    method: 'POST',
    data: data
  })
}
// 删除配置流程
export function bdaStateDel (params) {
  return http({
    url: '/api-sup/bda/bdaState/delete',
    method: 'GET',
    params: params
  })
}

// 准入流程新增 更新
export function saveOrUpdateEntryConfig (data) {
  return http({
    url: '/api-sup/entry/entryConfig/saveOrUpdateEntryConfig',
    method: 'POST',
    data: data
  })
}
// 删除配置流程
export function entryConfigDel (params) {
  return http({
    url: '/api-sup/entry/entryConfig/delete',
    method: 'POST',
    params: params
  })
}

// ///////////采购分类
// 新增 或编辑组织接口
export function saveOrUpdateCat (data) {
  return http({
    url: '/api-base/purchase/purchaseCategory/batchSaveOrUpdate',
    method: 'POST',
    data: data
  })
}
// 获取全部品类
export function getCatChildrenAllData (params) {
  return http({
    url: '/api-base/purchase/purchaseCategory/listAll',
    method: 'POST',
    params: params
  })
}
// 获取下一级节点
export function getCatChildrenData (params) {
  return http({
    url: '/api-base/purchase/purchaseCategory/listChildren',
    method: 'POST',
    params: params
  })
}
// 查询父级品类
export function getCatListParent (params) {
  return http({
    url: '/api-base/purchase/purchaseCategory/listParent',
    method: 'POST',
    params: params
  })
}
// 删除品类
export function categoryDelete (params) {
  return http({
    url: '/api-base/purchase/purchaseCategory/delete',
    method: 'POST',
    params: params
  })
}

// 物料维护
// 查询物料列表
export function getMaterialItemList (params) {
  return http({
    url: '/api-base/material/materialItem/listPage',
    method: 'POST',
    data: params
  })
}
export function listPageByCondition (params) {
  return http({
    url: '/api-base/material/materialItem/listPageByCondition',
    method: 'POST',
    data: params
  })
}
// 报表查询物料列表
export function reportsTestItemList (params) {
  return http({
    url: '/api-base/material/materialItem/listPageMaterialItemChart',
    method: 'POST',
    data: params
  })
}
// 删除物料
export function materialItemDel (params) {
  return http({
    url: '/api-base/material/materialItem/delete',
    method: 'POST',
    data: params
  })
}
// 删除物料
export function batchDelete (params) {
  return http({
    url: '/api-base/material/materialItem/batchDelete',
    method: 'POST',
    data: params
  })
}
// 新增编辑物料保存
export function saveOrUpdateMBatch (params) {
  return http({
    url: '/api-base/material/materialItem/saveOrUpdateMBatch',
    method: 'POST',
    data: params
  })
}
// 消息定义列表
export function getMessageList (params) {
  return http({
    url: '/api-base/messageInit/listPageByParam',
    method: 'POST',
    data: params
  })
}
// 删除消息定义
export function messageItemDel (params) {
  return http({
    url: '/api-base/messageInit/delete',
    method: 'GET',
    params: params
  })
}

// 批量新增编辑消息
export function saveOrUpdateMessage (params) {
  return http({
    url: '/api-base/messageInit/bathSaveOrUpdateMessageInit',
    method: 'POST',
    data: params
  })
}

// 物料查询搜索
export function getMaterialItemByParam (params) {
  return http({
    url: '/api-base/material/materialItem/listByParam',
    method: 'POST',
    data: params
  })
}
// 流程设置模块
// 保存流程模板 / 更新
export function saveOrUpdateProcessTemplate (url, data) {
  return http({
    url: url,
    method: 'POST',
    data: data
  })
}
// 查询模板详情
export function queryProcessTemplateById (params) {
  return http({
    url: '/api-flow/flow/processTemplent/queryProcessTemplateById',
    method: 'GET',
    params: params
  })
}
// 查询模板名称
export function queryEnablePermission (data) {
  return http({
    url: '/api-flow/flow/processTemplent/queryEnablePermission',
    method: 'GET',
    params: data
  })
}

// 删除行信息
export function deleteTemplateLines (data) {
  return http({
    url: '/api-flow/flow/processTemplent/deleteTemplateLines',
    method: 'POST',
    params: data
  })
}
// 启用、禁用流程模板
export function processTempUpdateEnable (params) {
  return http({
    url: '/api-flow/flow/processTemplent/updateEnableFlag',
    method: 'POST',
    params: params
  })
}

// 查询场景编码和场景名称
export function listSceneCodeAndSceneName (data) {
  return http({
    url: '/api-base/sceneAttachment/listSceneCodeAndSceneName',
    method: 'POST',
    params: data
  })
}

// 附件管理分页条件查询
export function listPageByParm (data) {
  return http({
    url: '/api-base/sceneAttachment/listPageByParm',
    method: 'POST',
    data
  })
}

// 根据附件ID删除
export function deleteSceneAttachment (params) {
  return http({
    url: '/api-base/sceneAttachment/delete',
    method: 'GET',
    params
  })
}

// 批量添加场景附件管理
export function batchSaveOrUpdateSceneAttachment (data) {
  return http({
    url: '/api-base/sceneAttachment/batchSaveOrUpdate',
    method: 'POST',
    data
  })
}

// 文件删除
export function fileuploadDelete (params) {
  return http({
    url: '/api-base/sceneAttachment/deleteFileUpLoad',
    method: 'POST',
    params
  })
}

// 品类分工
// 查询品类分工列表
export function getCategoryDivisionList (data) {
  return http({
    url: '/api-base/categoryDv/listPageByParam',
    method: 'POST',
    data: data
  })
}
// 删除品类分工 categoryDvId
export function categoryDvDel (params) {
  return http({
    url: '/api-base/categoryDv/delete',
    method: 'GET',
    params: params
  })
}
// 新增编辑品类分工保存
export function saveOrUpdateDvBatch (data) {
  return http({
    url: '/api-base/categoryDv/saveOrUpdateDvBatch',
    method: 'POST',
    data: data
  })
}
// 采购分类根据层级查询
export function catListByLevel (data) {
  return http({
    url: '/api-base/purchase/purchaseCategory/listByLevel',
    method: 'POST',
    data: data
  })
}
// 物流品类查询
export function listLogisticsCategoryByLevel (data) {
  return http({
    url: '/api-base/purchase/purchaseCategory/listLogisticsCategoryByLevel',
    method: 'POST',
    data: data
  })
}

// 查询附件模板配置
export function sceneAttachmentConf (data) {
  return http({
    url: '/api-base/sceneAttachment/listPageByParm',
    method: 'POST',
    data: data
  })
}

// 业务状态查询功能
export function listFunctionByParm (data) {
  return http({
    url: '/api-rbac/perm/permission/listFunctionByParm',
    method: 'POST',
    data: data
  })
}

// 配置导引查询
export function getConfigGuide (data) {
  return http({
    url: '/api-base/configGuide/getInfoByUser',
    method: 'GET',
    data: data
  })
}

// 新增|编辑现场评审周期配置 供应商
export function siteConfigVendorSaveOrUpdate (data) {
  return http({
    url: '/api-sup/review/siteConfigVendor/saveOrUpdate',
    method: 'POST',
    data: data
  })
}
// 新增|编辑现场评审周期配置 平泪
export function siteConfigCateSaveOrUpdate (data) {
  return http({
    url: '/api-sup/review/siteConfigCate/saveOrUpdate',
    method: 'POST',
    data: data
  })
}

// 获取上架合同
export function getOnShelvesContractList (data) {
  return http({
    url: '/api-cm/contract/contractHead/getOnShelvesContractList',
    method: 'POST',
    data: data
  })
}
// 组织设置收货地点保存
export function batchSaveOrUpdate (data) {
  return http({
    url: '/api-base/base/site/batchSaveOrUpdate',
    method: 'POST',
    data: data
  })
}
// 组织设置收货地点删除
export function batchdeleteUpdate (data) {
  return http({
    url: '/api-base/base/site/batchDelete',
    method: 'POST',
    data: data
  })
}
// 采购申请-物料明细-收货地点
export function listSiteByCondition (data) {
  return http({
    url: '/api-base/base/site/listSiteByCondition',
    method: 'POST',
    data: data
  })
}

export const companyDept = {

  list: async data =>
    http({
      url: '/api-base/base/org_company_dept/listPage',
      method: 'POST',
      data,
      loading: true
    }),

  listAll: async data =>
    http({
      url: '/api-base/base/org_company_dept/listAll',
      method: 'POST',
      data,
      loading: true
    }),

  listByOu: async data =>
    http({
      url: '/api-base/base/org_company_dept/listByOu',
      method: 'POST',
      data,
      loading: true
    }),

  add: async data =>
    http({
      url: '/api-base/base/org_company_dept/add',
      method: 'POST',
      data,
      loading: true
    }),

  update: async data =>
    http({
      url: '/api-base/base/org_company_dept/modify',
      method: 'POST',
      data,
      loading: true
    }),

  delete: async id =>
    http({
      url: '/api-base/base/org_company_dept/delete',
      method: 'GET',
      params: { id },
      loading: true
    }),

  get: async id =>
    http({
      url: '/api-base/base/org_company_dept/get',
      method: 'GET',
      params: { id },
      loading: true
    })
}

export const dataPermission = {
  list: async data =>
    http({
      url: '/api-rbac/rbac/data_permission/listPage',
      method: 'POST',
      data,
      loading: true
    }),

  add: async data =>
    http({
      url: '/api-rbac/rbac/data_permission/add',
      method: 'POST',
      data,
      loading: true
    }),

  update: async data =>
    http({
      url: '/api-rbac/rbac/data_permission/modify',
      method: 'POST',
      data,
      loading: true
    }),

  delete: async id =>
    http({
      url: '/api-rbac/rbac/data_permission/delete',
      method: 'GET',
      params: { id },
      loading: true
    }),

  get: async id =>
    http({
      url: '/api-rbac/rbac/data_permission/get',
      method: 'GET',
      params: { id },
      loading: true
    }),

  saveOrUpdate: async data =>
    http({
      url: '/api-rbac/rbac/data_permission/saveOrUpdate',
      method: 'POST',
      data,
      loading: true
    })

}

export const dataPermissionVarOption = {
  list: async data =>
    http({
      url: '/api-rbac/rbac/data_permission_var_option/listPage',
      method: 'POST',
      data,
      loading: true
    }),
  listActive: async data =>
    http({
      url: '/api-rbac/rbac/data_permission_var_option/listActive',
      method: 'POST',
      data,
      loading: true
    }),

  add: async data =>
    http({
      url: '/api-rbac/rbac/data_permission_var_option/add',
      method: 'POST',
      data,
      loading: true
    }),

  update: async data =>
    http({
      url: '/api-rbac/rbac/data_permission_var_option/modify',
      method: 'POST',
      data,
      loading: true
    }),

  delete: async id =>
    http({
      url: '/api-rbac/rbac/data_permission_var_option/delete',
      method: 'GET',
      params: { id },
      loading: true
    }),

  get: async id =>
    http({
      url: '/api-rbac/rbac/data_permission_var_option/get',
      method: 'GET',
      params: { id },
      loading: true
    }),

  saveOrUpdate: async data =>
    http({
      url: '/api-rbac/rbac/data_permission_var_option/saveOrUpdate',
      method: 'POST',
      data,
      loading: true
    })

}

export const systemConfigureApi = {
  list: async data =>
    http({
      url: '/api-base/base/system_configure/listPage',
      method: 'POST',
      data,
      loading: true
    }),

  get: async id =>
    http({
      url: '/api-base/base/system_configure/get',
      method: 'GET',
      params: { id },
      loading: true
    }),

  saveOrUpdate: async data =>
    http({
      url: '/api-base/base/system_configure/saveOrUpdate',
      method: 'POST',
      data,
      loading: true
    }),

  delete: async id =>
    http({
      url: '/api-base/base/system_configure/delete',
      method: 'GET',
      params: { id },
      loading: true
    }),

  refreshConfigureCache: async () =>
    http({
      url: '/api-base/base/system_configure/refreshConfigureCache',
      method: 'POST',
      data: {},
      loading: true
    })

}
