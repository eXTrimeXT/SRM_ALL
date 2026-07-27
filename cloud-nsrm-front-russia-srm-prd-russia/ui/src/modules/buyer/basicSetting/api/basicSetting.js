import http from '@/utils/axios/http'
const getUrl = path => `${path}`

// 品类状态配置
export const vendorStateSetting = {
  list: async data =>
    http({
      url: getUrl('/api-sup/sup/categoryState/listPage'),
      method: 'POST',
      data,
      loading: true
    }),

  saveOrUpdate: async data =>
    http({
      url: getUrl('/api-sup/sup/categoryState/batchSaveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    })

}

// 准入流程配置
export const accessProcessConfiguration = {
  listConfigNodeById: async id =>
    http({
      url: getUrl('/api-sup/entry/entryConfig/listConfigNodeById/' + id),
      method: 'GET',
      loading: true
    }),

  saveOrUpdateNode: async data =>
    http({
      url: getUrl('/api-sup/entry/entryConfig/saveOrUpdateNode'),
      method: 'POST',
      data
    })
}

// 物料图纸
const getDrawingsPrefix = '/api-base/base/drawingshead'
const getDrawingsUrl = path => getDrawingsPrefix + '/' + path
export const drawingsHeadApi = {
  list: async data =>
    http({
      url: getDrawingsUrl('listPage'),
      method: 'POST',
      data,
      loading: true
    }),
  getById: async id =>
    http({
      url: getDrawingsUrl('get'),
      method: 'GET',
      params: { id },
      loading: true
    }),
  add: async data =>
    http({
      url: getDrawingsUrl('add'),
      method: 'POST',
      data,
      loading: true
    }),
  update: async data =>
    http({
      url: getDrawingsUrl('update'),
      method: 'POST',
      data,
      loading: true
    }),
  updateStatus: async data =>
    http({
      url: getDrawingsUrl('update/status'),
      method: 'POST',
      data,
      loading: true
    }),
  delete: async id =>
    http({
      url: getDrawingsUrl('delete'),
      method: 'GET',
      params: { id },
      loading: true
    }),
  bathDelete: async data =>
    http({
      url: getDrawingsUrl('bathDelete'),
      method: 'POST',
      data,
      loading: true
    })
}

export const accessFlowSetting = {
  // 合同模板分页查询
  listPage: async data =>
    http({
      url: getUrl('/api-cm/modelHead/listPage'),
      method: 'POST',
      data
    }),
  // 准入流程配置合同定义模块请求列表
  contractListPage: async data =>
    http({
      url: getUrl('/api-sup/entry/entryConfigContract/listPage'),
      method: 'POST',
      data
    }),
  // 准入流程配置合同定义模块保存
  batchSave: async data =>
    http({
      url: getUrl('/api-sup/entry/entryConfigContract/batchSave'),
      method: 'POST',
      data
    }),
  // 查询准入流程配置里面维护品类的列表
  listPageForEntryConfig: async data =>
    http({
      url: getUrl('/api-base/purchase/purchaseCategory/listPageForEntryConfig'),
      method: 'POST',
      data,
      loading: true
    })
}

export const dataPermission = {
  list: async data =>
    http({
      url: getUrl('/api-rbac/rbac/data_permission/listPage'),
      method: 'POST',
      data,
      loading: true
    }),
  add: async data =>
    http({
      url: getUrl('/api-rbac/rbac/data_permission/add'),
      method: 'POST',
      data,
      loading: true
    }),
  update: async data =>
    http({
      url: getUrl('/api-rbac/rbac/data_permission/modify'),
      method: 'POST',
      data,
      loading: true
    }),
  delete: async id =>
    http({
      url: getUrl('/api-rbac/rbac/data_permission/delete'),
      method: 'GET',
      params: { id },
      loading: true
    }),
  get: async id =>
    http({
      url: getUrl('/api-rbac/rbac/data_permission/get'),
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
export const dataPermissionOption = {
  list: async data =>
    http({
      url: '/api-rbac/rbac/data_permission_var_option/listPage',
      method: 'POST',
      data,
      loading: false
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

export const dynamicReportConfig = {
  listPageWithUserType: async data =>
    http({
      url: getUrl('/api-base/dynamicsql/listPageWithUserType'),
      method: 'POST',
      data,
      loading: true
    }),
  listPage: async data =>
    http({
      url: getUrl('/api-base/dynamicsql/listPage'),
      method: 'POST',
      data,
      loading: true
    }),
  saveOrUpdate: async data =>
    http({
      url: getUrl('/api-base/dynamicsql/saveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    }),
  getById: async id =>
    http({
      url: getUrl('/api-base/dynamicsql/get'),
      method: 'GET',
      params: { id },
      loading: true
    }),
  delete: async id =>
    http({
      url: getUrl('/api-base/dynamicsql/delete'),
      method: 'GET',
      params: { id },
      loading: true
    }),
  getByName: async name =>
    http({
      url: getUrl('/api-base/dynamicsql/getByName'),
      method: 'GET',
      params: { name },
      loading: true
    }),
  getAttrs: async data =>
    http({
      url: getUrl('/api-base/dynamicsql/getAttrs'),
      method: 'POST',
      data,
      loading: true
    }),
  listByFormCondition: async data =>
    http({
      url: getUrl('/api-base/dynamicsql/listByFormCondition'),
      method: 'POST',
      data,
      loading: true
    })
}

export const exportCenter = {
  listExportPage: async data =>
    http({
      url: getUrl('/api-file/file/fileupload/listExportPage'),
      method: 'POST',
      data,
      loading: true
    })
}

export const messageMaintenance = {
  // 消息定义列表
  getMessageList: async data =>
    http({
      url: getUrl('/api-base/messageInit/listPageByParam'),
      method: 'POST',
      data,
      loading: true
    }),
  // 删除消息定义
  messageItemDel: async params =>
    http({
      url: getUrl('/api-base/messageInit/delete'),
      method: 'GET',
      params,
      loading: true
    }),
  // 批量新增编辑消息
  saveOrUpdateMessage: async data =>
    http({
      url: getUrl('/api-base/messageInit/bathSaveOrUpdateMessageInit'),
      method: 'POST',
      data,
      loading: true
    })
}

export const monitorBizConfig = {
  list: async data =>
    http({
      url: getUrl('/api-base/base/monitor_biz_config/listPage'),
      method: 'POST',
      data,
      loading: true
    }),

  add: async data =>
    http({
      url: getUrl('/api-base/base/monitor_biz_config/addOrUpdate'),
      method: 'POST',
      data,
      loading: true
    }),

  update: async data =>
    http({
      url: getUrl('/api-base/base/monitor_biz_config/addOrUpdate'),
      method: 'POST',
      data,
      loading: true
    }),
  startTask: async data =>
    http({
      url: getUrl('/api-base/base/monitor_biz_config/startTask'),
      method: 'POST',
      data,
      loading: true
    }),

  stopTask: async data =>
    http({
      url: getUrl('/api-base/base/monitor_biz_config/stopTask'),
      method: 'POST',
      data,
      loading: true
    }),

  delete: async id =>
    http({
      url: getUrl('/api-base/base/monitor_biz_config/delete'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  batchSaveOrUpdate: async data =>
    http({
      url: getUrl('/api-base/base/monitor_biz_config/batchSaveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    })
}

export const monitorBizLog = {
  list: async data =>
    http({
      url: getUrl('/api-base/base/monitor_biz_log/listPage'),
      method: 'POST',
      data,
      loading: true
    }),

  add: async data =>
    http({
      url: getUrl('/api-base/base/monitor_biz_log/add'),
      method: 'POST',
      data,
      loading: true
    }),

  dealHandle: async data =>
    http({
      url: getUrl('/api-base/base/monitor_biz_log/deal'),
      method: 'POST',
      data,
      loading: true
    }),

  update: async data =>
    http({
      url: getUrl('/api-base/base/monitor_biz_log/modify'),
      method: 'POST',
      data,
      loading: true
    }),

  delete: async id =>
    http({
      url: getUrl('/api-base/base/monitor_biz_log/delete'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  batchSaveOrUpdate: async data =>
    http({
      url: getUrl('/api-base/base/monitor_biz_log/batchSaveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    })
}

// 通知模板
export const noticetemplate = {
  list: async data =>
    http({
      url: getUrl('/api-base/base/noticetemplate/listPage'),
      method: 'POST',
      data,
      loading: true
    }),

  add: async data =>
    http({
      url: getUrl('/api-base/base/noticetemplate/add'),
      method: 'POST',
      data,
      loading: true
    }),

  update: async data =>
    http({
      url: getUrl('/api-base/base/noticetemplate/modify'),
      method: 'POST',
      data,
      loading: true
    }),

  delete: async id =>
    http({
      url: getUrl('/api-base/base/noticetemplate/delete'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  batchSaveOrUpdate: async data =>
    http({
      url: getUrl('/api-base/base/noticetemplate/batchSaveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    })
}

export const orderNoConfig = {
  // 新增
  seqAdd: async data =>
    http({
      url: getUrl('/api-base/seq/add'),
      method: 'POST',
      data,
      loading: true
    }),
  // 更新
  seqUpdate: async data =>
    http({
      url: getUrl('/api-base/seq/update'),
      method: 'POST',
      data,
      loading: true
    }),
  // 查询
  seqQueryById: async params =>
    http({
      url: getUrl('/api-base/seq/queryById'),
      method: 'GET',
      params,
      loading: true
    }),
  // 删除
  seqDelete: async params =>
    http({
      url: getUrl('/api-base/seq/delete'),
      method: 'GET',
      params,
      loading: true
    }),
  // 列表
  seqPageList: async data =>
    http({
      url: getUrl('/api-base/seq/pageList'),
      method: 'POST',
      data,
      loading: false
    })
}

export const organizationSetting = {
  list: async data =>
    http({
      url: getUrl('/api-base/base/org_company_dept/listPage'),
      method: 'POST',
      data,
      loading: true
    }),
  listAll: async data =>
    http({
      url: getUrl('/api-base/base/org_company_dept/listAll'),
      method: 'POST',
      data,
      loading: true
    }),
  listByOu: async data =>
    http({
      url: getUrl('/api-base/base/org_company_dept/listByOu'),
      method: 'POST',
      data,
      loading: true
    }),
  add: async data =>
    http({
      url: getUrl('/api-base/base/org_company_dept/add'),
      method: 'POST',
      data,
      loading: true
    }),
  update: async data =>
    http({
      url: getUrl('/api-base/base/org_company_dept/modify'),
      method: 'POST',
      data,
      loading: true
    }),
  delete: async id =>
    http({
      url: getUrl('/api-base/base/org_company_dept/delete'),
      method: 'GET',
      params: { id },
      loading: true
    }),
  get: async id =>
    http({
      url: getUrl('/api-base/base/org_company_dept/get'),
      method: 'GET',
      params: { id },
      loading: true
    }),
  // 获取当前组织的信息 (编辑的时候先获取旧数据)
  getOrganization: async params =>
    http({
      url: getUrl('/api-base/organization/organization/getOrganization'),
      method: 'GET',
      params,
      loading: true
    }),
  // 新增 或编辑组织接口
  orgSaveOrUpdate: async data =>
    http({
      url: getUrl('/api-base/organization/organization/saveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    }),
  // 获取下一级节点
  getListChildrenData: async (params, loading = true) =>
    http({
      url: getUrl('/api-base/organization/relation/listChildrenOrganization'),
      method: 'POST',
      params,
      loading: loading
    }),
  // 组织设置 类别列表
  getOrgTypeList: async params =>
    http({
      url: getUrl('/api-base/organization/organizationsType/listAll'),
      method: 'POST',
      unToken: true,
      params,
      loading: true
    }),
  // 批量添加组织类型
  addOrgType: async data =>
    http({
      url: getUrl('/api-base/organization/organizationsType/saveOrUpdateBatch'),
      method: 'POST',
      data,
      loading: true
    }),
  // 查询上层组织
  getParentOrgData: async data =>
    http({
      url: getUrl('/api-base/organization/organization/listParentOrganization'),
      method: 'POST',
      data,
      loading: true
    }),
  // 查询中台组织数据
  getRpcOrg: async params =>
    http({
      url: getUrl('/api-base/organization/rpc/listChildOrg'),
      method: 'GET',
      params,
      loading: true
    })
}

export const purchaseBaseSetting = {
  // 新增|编辑币种
  currencySaveOrUpdate: async data =>
    http({
      url: getUrl('/api-base/purchase/purchaseCurrency/saveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    }),
  // 新增|编辑税率
  rateSaveOrUpdate: async data =>
    http({
      url: getUrl('/api-base/purchase/purchaseTax/saveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    }),
  // 新增|编辑单位
  unitSaveOrUpdate: async data =>
    http({
      url: getUrl('/api-base/purchase/purchaseUnit/saveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    })
}

export const purchaseCategoryMaintenance = {
  // 获取下一级节点
  getCatChildrenData: async params =>
    http({
      url: getUrl('/api-base/purchase/purchaseCategory/listChildren'),
      method: 'POST',
      params,
      loading: true
    }),
  // 查询父级品类
  getCatListParent: async params =>
    http({
      url: getUrl('/api-base/purchase/purchaseCategory/listParent'),
      method: 'POST',
      params,
      loading: true
    }),
  // 删除品类
  categoryDelete: async params =>
    http({
      url: getUrl('/api-base/purchase/purchaseCategory/delete'),
      method: 'POST',
      params,
      loading: true
    }),
  // 新增 或编辑组织接口
  saveOrUpdateCat: async data =>
    http({
      url: getUrl('/api-base/purchase/purchaseCategory/batchSaveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    }),
  // 采购分类根据层级查询
  catListByLevel: async data =>
    http({
      url: getUrl('/api-base/purchase/purchaseCategory/listByLevel'),
      method: 'POST',
      data,
      loading: true
    })
}

export const redisCacheList = {
  listPage: async data =>
    http({
      url: getUrl('/api-base/rediscache/listPage'),
      method: 'POST',
      data,
      loading: true
    }),
  // 新增
  redisCacheAdd: async data =>
    http({
      url: getUrl('/api-base/rediscache/add'),
      method: 'POST',
      data,
      loading: true
    }),
  // 修改
  redisCacheModify: async data =>
    http({
      url: getUrl('/api-base/rediscache/modify'),
      method: 'POST',
      data,
      loading: true
    }),
  redisBatchDelete: async data =>
    http({
      url: getUrl('/api-base/rediscache/deleteByIds'),
      method: 'POST',
      data,
      loading: true
    }),
  // 根据redisKey前缀匹配删除
  delRedisCacheByPrefix: async data =>
    http({
      url: getUrl('/api-base/rediscache/delRedisCacheByPrefix'),
      method: 'POST',
      data,
      loading: true
    }),
  // 根据redisKey查找内容
  getRedisCacheContent: async data =>
    http({
      url: getUrl('/api-base/rediscache/getRedisCacheContent'),
      method: 'POST',
      data,
      loading: true
    }),
  // 删除
  redisCacheDelete: async params =>
    http({
      url: getUrl('/api-base/rediscache/delete'),
      method: 'GET',
      params,
      loading: true
    }),
  // 根据redisKey删除
  delRedisCacheByKey: async data =>
    http({
      url: getUrl('/api-base/rediscache/delRedisCacheByKey'),
      method: 'POST',
      data,
      loading: true
    })
}

export const reportSetting = {
  // 获取全部品类
  getCatChildrenAllData: async params =>
    http({
      url: getUrl('/api-base/purchase/purchaseCategory/listAll'),
      method: 'POST',
      params,
      loading: true
    })
}

export const settingGuide = {
  // 配置导引查询
  getConfigGuide: async data =>
    http({
      url: getUrl('/api-base/configGuide/getInfoByUser'),
      method: 'GET',
      data,
      loading: true
    })
}

export const statusSetting = {
  // 状态流转配置查询详情
  getStatusConfigDetail: async params =>
    http({
      url: getUrl('/api-base/base/statusConfig/getDetailById'),
      method: 'GET',
      params,
      loading: true
    }),
  // 状态流转配置保存
  saveOrUpdateStatusConfig: async data =>
    http({
      url: getUrl('/api-base/base/statusConfig/addOrUpdate'),
      method: 'POST',
      data,
      loading: true
    }),
  // 状态流转配置保存
  saveOrUpdateStatusConfigCondition: async data =>
    http({
      url: getUrl('/api-base/base/statusConfig/addOrUpdateCondition'),
      method: 'POST',
      data,
      loading: true
    })
}

export const systemConfigure = {
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

export const vendorAttributeControl = {
  // 新增维度
  addFieldDim: async data =>
    http({
      url: getUrl('/api-sup/dim/dim/add'),
      method: 'POST',
      data,
      loading: true
    }),
  // 编辑维度
  editFieldDim: async data =>
    http({
      url: getUrl('/api-sup/dim/dim/modify'),
      method: 'POST',
      data,
      loading: true
    }),
  // 维度查询
  getFieldDim: async (data = {}) =>
    http({
      url: getUrl('/api-sup/dim/dim/listOrder'),
      method: 'POST',
      data,
      loading: true
    }),
  // 修改维度基本信息
  updateDimBasicData: async data =>
    http({
      url: getUrl('/api-sup/dim/dim/updateBasic'),
      method: 'POST',
      data,
      loading: true
    }),
  // 删除维度
  deleteFieldDim: async params =>
    http({
      url: getUrl('/api-sup/dim/dim/delete'),
      method: 'DELETE',
      params,
      loading: true
    }),
  // 新增或编辑属性
  fieldSaveOrUpdate: async data =>
    http({
      url: getUrl('/api-sup/dim/dimField/saveOrUpdateField'),
      method: 'POST',
      data,
      loading: true
    }),
  // 获取属性信息
  getFieldInfo: async params =>
    http({
      url: getUrl('/api-sup/dim/dimField/get'),
      method: 'GET',
      params,
      loading: true
    })
}

export const vendorAttributeSetting = {
  // 获取属性配置 通过模板id和dimId 获取单个维度下面的配置信息
  getTemplateByDimId: async data =>
    http({
      url: getUrl('/api-sup/dim/dimConfig/getDtoByParam'),
      method: 'POST',
      data,
      loading: true
    }),
  // 查询属性列表
  fieldGetListPage: async data =>
    http({
      url: getUrl('/api-sup/dim/dimField/listPageByParam'),
      method: 'POST',
      data,
      loading: true
    }),
  // 获取属性配置详细
  getDimTemplateById: async params =>
    http({
      url: getUrl('/api-sup/dim/dimTemplate/getByTemplateId'),
      method: 'GET',
      params,
      loading: true
    }),
  // 新增或保存属性配置
  saveOrUpdateTemplate: async data =>
    http({
      url: getUrl('/api-sup/dim/dimTemplate/saveOrUpdateTemplate'),
      method: 'POST',
      data,
      loading: true
    }),
  // 修改维度配置信息
  definitionDimData: async data =>
    http({
      url: getUrl('/api-sup/dim/dim/definitionDim'),
      method: 'POST',
      data,
      loading: true
    })
}

// 属性维度配置其他功能常调用接口
// import { vendorAttributeComApi } from 'modb@/basicSetting/api/basicSetting'
export const vendorAttributeComApi = {
  // 维度查询
  getFieldDim: async (data = {}) =>
    http({
      url: getUrl('/api-sup/dim/dim/listOrder'),
      method: 'POST',
      data,
      loading: true
    })
}

export const workflowSetting = {
  // 查询模板名称
  queryEnablePermission: async params =>
    http({
      url: getUrl('/api-base/flow/processTemplent/queryEnablePermission'),
      method: 'GET',
      params,
      loading: false
    }),
  // 启用、禁用流程模板
  processTempUpdateEnable: async params =>
    http({
      url: getUrl('/api-base/flow/processTemplent/updateEnableFlag'),
      method: 'POST',
      params,
      loading: true
    }),
  // 查询模板详情
  queryProcessTemplateById: async params =>
    http({
      url: getUrl('/api-base/flow/processTemplent/queryProcessTemplateById'),
      method: 'GET',
      params,
      loading: true
    }),
  // 保存流程模板 / 更新
  saveOrUpdateProcessTemplate: async (url, data) =>
    http({
      url: getUrl(url),
      method: 'POST',
      data,
      loading: true
    }),
  // 删除行信息
  deleteTemplateLines: async params =>
    http({
      url: getUrl('/api-base/flow/processTemplent/deleteTemplateLines'),
      method: 'POST',
      params,
      loading: true
    })
}

// 动态附件-附件场景
export const sceneFileApi = {
  listAll: async data =>
    http({
      url: getUrl('/api-base/base/scene_file/listAll'),
      method: 'POST',
      data,
      loading: true
    }),
  listPage: async data =>
    http({
      url: getUrl('/api-base/base/scene_template/listPage'),
      method: 'POST',
      data,
      loading: true
    }),
  delete: async id =>
    http({
      url: getUrl('/api-base/base/scene_template/delete'),
      method: 'GET',
      params: { id },
      loading: true
    }),
  batchSaveOrUpdate: async data =>
    http({
      url: getUrl('/api-base/base/scene_template/batchSaveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    }),
  modify: async data =>
    http({
      url: getUrl('/api-base/base/scene_template/modify'),
      method: 'POST',
      data,
      loading: true
    })
}
// 动态附件-附件场景模板
export const sceneTemplateApi = {
  listAll: async data =>
    http({
      url: getUrl('/api-base/base/scene_template/listAll'),
      method: 'POST',
      data,
      loading: true
    }),
  listPage: async data =>
    http({
      url: getUrl('/api-base/base/scene_template/listPage'),
      method: 'POST',
      data,
      loading: true
    }),
  delete: async id =>
    http({
      url: getUrl('/api-base/base/scene_template/delete'),
      method: 'GET',
      params: { id },
      loading: true
    }),
  batchSaveOrUpdate: async data =>
    http({
      url: getUrl('/api-base/base/scene_template/batchSaveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    }),
  modify: async data =>
    http({
      url: getUrl('/api-base/base/scene_template/modify'),
      method: 'POST',
      data,
      loading: true
    })
}

// 动态sql
export const dynamicSqlApi = {
  listPageWithUserType: async data =>
    http({
      url: getUrl('/api-base/dynamicsql/listPageWithUserType'),
      method: 'POST',
      data,
      loading: true
    }),
  listPage: async data =>
    http({
      url: getUrl('/api-base/dynamicsql/listPage'),
      method: 'POST',
      data,
      loading: true
    }),
  saveOrUpdate: async data =>
    http({
      url: getUrl('/api-base/dynamicsql/saveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    }),
  getById: async id =>
    http({
      url: getUrl('/api-base/dynamicsql/get'),
      method: 'GET',
      params: { id },
      loading: true
    }),
  delete: async id =>
    http({
      url: getUrl('/api-base/dynamicsql/delete'),
      method: 'GET',
      params: { id },
      loading: true
    }),
  getByName: async name =>
    http({
      url: getUrl('/api-base/dynamicsql/getByName'),
      method: 'GET',
      params: { name },
      loading: false
    }),
  getAttrs: async data =>
    http({
      url: getUrl('/api-base/dynamicsql/getAttrs'),
      method: 'POST',
      data,
      loading: true
    }),
  listByFormCondition: async data =>
    http({
      url: getUrl('/api-base/dynamicsql/listByFormCondition'),
      method: 'POST',
      data,
      loading: false
    })
}

// 页面工具配置
// import { globalToolAPI, formPageAPI } from 'modb@/basicSetting/api/basicSetting'
export const globalToolAPI = {
  listTables: async params =>
    http({
      method: 'GET',
      url: getUrl('/api-base/global/tool/listTablesInfo'),
      params: params
    }),
  listColumns: async params =>
    http({
      method: 'GET',
      url: getUrl('/api-base/global/tool/listColumns'),
      params: params
    })
}
// 页面表单配置
export const formPageAPI = {
  list: async data =>
    http({
      url: getUrl('/api-base/base/form_page/listPage'),
      method: 'POST',
      data,
      loading: false
    }),

  add: async data =>
    http({
      url: getUrl('/api-base/base/form_page/add'),
      method: 'POST',
      data,
      loading: true
    }),

  saveOrUpdate: async data =>
    http({
      url: getUrl('/api-base/base/form_page/saveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    }),

  update: async data =>
    http({
      url: getUrl('/api-base/base/form_page/modify'),
      method: 'POST',
      data,
      loading: true
    }),

  copyPageForm: async data =>
    http({
      url: getUrl('/api-base/base/form_page/copyPageForm'),
      method: 'POST',
      data,
      loading: true
    }),

  delete: async id =>
    http({
      url: getUrl('/api-base/base/form_page/delete'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  getDetail: async id =>
    http({
      url: getUrl('/api-base/base/form_page/get_detail'),
      method: 'GET',
      params: { id },
      loading: true
    }),
  getPageCode: async pageCode =>
    http({
      url: getUrl('/api-base/base/form_page/get_page_code'),
      method: 'GET',
      params: { pageCode },
      loading: true
    }),

  batchSaveOrUpdate: async data =>
    http({
      url: getUrl('/api-base/base/form_page/batchSaveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    })
}
