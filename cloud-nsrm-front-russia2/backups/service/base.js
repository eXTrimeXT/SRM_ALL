/**
 * 基础设置新增API
 */

import http from '@/utils/axios/http'
const getUrl = path => `${path}`

// 公开寻源的接口 by 伟龙
export const portalSourcing = {
  // 公开寻源列表
  listPage: async data =>
    http({
      url: getUrl('/api-inq/inq-anon/reqhead/listPage'),
      method: 'POST',
      data
    })
}

export const sceneFile = {
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

export const sceneTemplate = {
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

export const dynamicSql = {
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

// 获取文件列表 根据业务单据ID查询对应附件  businessId
export function getFileListByBusinessId (params) {
  http({
    url: getUrl('/api-file/file/fileupload/listPageByBusinessId'),
    method: 'POST',
    params: params
  })
}

export const listByDictCode = async data => {
  http({
    url: getUrl('/api-base/dict/base-dict-item/listByDictCode'),
    method: 'POST',
    params: data,
    loading: true
  })
}

// 导出中心列表
export const listExportPage = async data =>
  http({
    url: getUrl('/api-file/file/fileupload/listExportPage'),
    method: 'POST',
    data,
    loading: true
  })
// 列表
export const seqPageList = async data =>
  http({
    url: getUrl('/api-base/seq/pageList'),
    method: 'POST',
    data,
    loading: true
  })
// 列表
export const redisCacheList = async data =>
  http({
    url: getUrl('/api-base/rediscache/listPage'),
    method: 'POST',
    data,
    loading: true
  })
// 批量删除
export const redisBatchDelete = async data =>
  http({
    url: getUrl('/api-base/rediscache/deleteByIds'),
    method: 'POST',
    data,
    loading: true
  })
// 新增
export const redisCacheAdd = async data =>
  http({
    url: getUrl('/api-base/rediscache/add'),
    method: 'POST',
    data,
    loading: true
  })
// 修改
export const redisCacheModify = async data =>
  http({
    url: getUrl('/api-base/rediscache/modify'),
    method: 'POST',
    data,
    loading: true
  })
// 删除
export const redisCacheDelete = async params =>
  http({
    url: getUrl('/api-base/rediscache/delete'),
    method: 'GET',
    params,
    loading: true
  })
// 根据redisKey查找内容
export const getRedisCacheContent = async data =>
  http({
    url: getUrl('/api-base/rediscache/getRedisCacheContent'),
    method: 'POST',
    data,
    loading: true
  })
// 根据redisKey前缀匹配删除
export const delRedisCacheByPrefix = async data =>
  http({
    url: getUrl('/api-base/rediscache/delRedisCacheByPrefix'),
    method: 'POST',
    data,
    loading: true
  })
// 根据redisKey删除
export const delRedisCacheByKey = async data =>
  http({
    url: getUrl('/api-base/rediscache/delRedisCacheByKey'),
    method: 'POST',
    data,
    loading: true
  })
// 获取指定组织下指定类型的组织列表接口
export const getOrganizationByOrgCode = async data =>
  http({
    url: getUrl('/api-base/organization/organization/getOrganizationByOrgCode'),
    method: 'POST',
    data,
    loading: true
  })
// 获取供应商的组织列表接口
export const getSupplierOrgTree = async data =>
  http({
    url: getUrl('/api-sup/info/orgCategory/supplierTree'),
    method: 'POST',
    data,
    loading: true
  })
// 删除
export const seqDelete = async params =>
  http({
    url: getUrl('/api-base/seq/delete'),
    method: 'GET',
    params,
    loading: true
  })

// 查询
export const seqQueryById = async params =>
  http({
    url: getUrl('/api-base/seq/queryById'),
    method: 'GET',
    params,
    loading: true
  })
// 新增
export const seqAdd = async data =>
  http({
    url: getUrl('/api-base/seq/add'),
    method: 'POST',
    data,
    loading: true
  })
// 更新
export const seqUpdate = async data =>
  http({
    url: getUrl('/api-base/seq/update'),
    method: 'POST',
    data,
    loading: true
  })
// 查询指定组织和公司的部门树
export const deptTree = async params =>
  http({
    url: getUrl('/api-base/dept/deptTree'),
    method: 'GET',
    params,
    loading: true
  })

// 按条件查找所有业务实体+公司下的部门信息
export const getAll = async data =>
  http({
    url: getUrl('/api-base/virtual-depart/getAll'),
    method: 'POST',
    data,
    loading: true
  })

// 通过选择业务实体带出事业部
export const queryBuInfoByOrgId = async params =>
  http({
    url: getUrl('/api-base/base/base-ou-group/queryBuInfoByOrgId'),
    method: 'GET',
    params,
    loading: true
  })

export const formPageAPI = {
  list: async data =>
    http({
      url: getUrl('/api-base/base/form_page/listPage'),
      method: 'POST',
      data,
      loading: true
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

export const purchase = {
  // 根据转换的两个币种查询汇率
  purchaseExchangeRate: async data =>
    http({
      url: getUrl('/api-base/purchase/purchaseExchangeRate/listByParams'),
      method: 'POST',
      data,
      loading: true
    }),

  // 根据币种和汇率类型批量查询汇率
  getPriceTaxList: async data =>
    http({
      url: '/api-base/purchase/purchaseExchangeRate/getPriceTaxList',
      method: 'POST',
      data,
      loading: true
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

// 页面配置
export const pageConfig = {
  // 条件配置获取
  getCurrentConfig: async data =>
    http({
      url: getUrl('/api-base/base/page_view_config/getCurrent'),
      method: 'POST',
      data
    }),
  // 保存用户设置
  saveUserConfig: async data =>
    http({
      url: getUrl('/api-base/base/page_view_config/save'),
      method: 'POST',
      data,
      loading: true
    }),
  // 清除用户配置
  removeUserConfig: async data =>
    http({
      url: getUrl('/api-base/base/page_view_config/removeUserConfig'),
      method: 'POST',
      data,
      loading: true
    })
}

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
// 审批流相关接口
export const flowAPI = {
  // 流程模式
  getFlowIntegrationMode: async data =>
    http({
      method: 'POST',
      url: getUrl('/api-base/flow/event/getFlowIntegrationMode'),
      data: data,
      loading: true
    }),
  // 查询代办
  queryTodo: async params =>
    http({
      method: 'GET',
      url: getUrl('/api-base/flow/event/queryTodo'),
      params: params
    }),
  // 列表批量审批获取流程单据ID
  getFlowMainId: async id =>
    http({
      method: 'GET',
      url: getUrl('/api-base/flow/event/flow/mainId'),
      params: { businessId: id }
    })
}
export const basicSetting = {
  // 组织设置 类别列表
  getOrgTypeList: async params =>
    http({
      url: getUrl('/api-base/organization/organizationsType/listAll'),
      method: 'POST',
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
   // 查询中台组织数据
  getRpcOrg: async params =>
    http({
      url: getUrl('/api-flow/organization/rpc/listChildOrg'),
      method: 'GET',
      params,
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
  // 获取当前组织的信息 (编辑的时候先获取旧数据)
  getOrgDataForEdit: async params =>
    http({
      url: getUrl('/api-base/organization/organization/get'),
      method: 'GET',
      params,
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
  // 获取字典维护界面的信息
  getDictInfo: async params =>
    http({
      url: getUrl('/api-base/dict/base-dict/queryPageByConditions/1/10'),
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
  // 查询全部组织列表
  getAllListOrgData: async data =>
    http({
      url: getUrl('/api-base/organization/organization/listAllOrganization'),
      method: 'POST',
      data,
      loading: true
    }),
  // 获取下一级节点
  getListChildrenData: async params =>
    http({
      url: getUrl('/api-base/organization/relation/listChildrenOrganization'),
      method: 'POST',
      params,
      loading: true
    }),
  // 新增|编辑币种
  currencySaveOrUpdate: async data =>
    http({
      url: getUrl('/api-base/purchase/purchaseCurrency/saveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    }),
  // 查询币种
  currencyGetListPage: async params =>
    http({
      url: getUrl('/api-base/purchase/purchaseCurrency/listPage'),
      method: 'POST',
      params,
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
  // 查询税率
  rateGetListPage: async params =>
    http({
      url: getUrl('/api-base/purchase/purchaseTax/listPage'),
      method: 'POST',
      params,
      loading: true
    }),
  // 新增|编辑单位
  unitSaveOrUpdate: async data =>
    http({
      url: getUrl('/api-base/purchase/purchaseUnit/saveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    }),
  // 查询单位
  unitGetListPage: async params =>
    http({
      url: getUrl('/api-base/purchase/purchaseUnit/listPage'),
      method: 'POST',
      params,
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
    }),
  // 维度查询
  getFieldDim: async params =>
    http({
      url: getUrl('/api-sup/dim/dim/listOrder'),
      method: 'POST',
      params,
      loading: true
    }),
  // 维度查询(下拉框)
  getFieldDimS: async params =>
    http({
      url: getUrl('/api-sup/dim/dim/listAll'),
      method: 'POST',
      params,
      loading: true
    }),
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
  // 删除维度
  deleteFieldDim: async params =>
    http({
      url: getUrl('/api-sup/dim/dim/delete'),
      method: 'DELETE',
      params,
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
  // 获取属性配置 通过模板id和dimId 获取单个维度下面的配置信息
  getTemplateByDimId: async data =>
    http({
      url: getUrl('/api-sup/dim/dimConfig/getDtoByParam'),
      method: 'POST',
      data,
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
  // 修改维度基本信息
  updateDimBasicData: async data =>
    http({
      url: getUrl('/api-sup/dim/dim/updateBasic'),
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
    }),
  // 业务状态新增 修改
  saveOrUpdateBdaData: async data =>
    http({
      url: getUrl('/api-sup/bda/bdaState/saveOrUpdateBda'),
      method: 'POST',
      data,
      loading: true
    }),
  // 删除配置流程
  bdaStateDel: async params =>
    http({
      url: getUrl('/api-sup/bda/bdaState/delete'),
      method: 'GET',
      params,
      loading: true
    }),
  // 准入流程新增 更新
  saveOrUpdateEntryConfig: async data =>
    http({
      url: getUrl('/api-sup/entry/entryConfig/saveOrUpdateEntryConfig'),
      method: 'POST',
      data,
      loading: true
    }),
  // 删除配置流程
  entryConfigDel: async params =>
    http({
      url: getUrl('/api-sup/entry/entryConfig/delete'),
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
  // 获取全部品类
  getCatChildrenAllData: async params =>
    http({
      url: getUrl('/api-base/purchase/purchaseCategory/listAll'),
      method: 'POST',
      params,
      loading: true
    }),
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
  // 查询物料列表
  getMaterialItemList: async data =>
    http({
      url: getUrl('/api-base/material/materialItem/listPage'),
      method: 'POST',
      data,
      loading: true
    }),
  listPageByCondition: async data =>
    http({
      url: getUrl('/api-base/material/materialItem/listPageByCondition'),
      method: 'POST',
      data,
      loading: true
    }),
  // 报表查询物料列表
  reportsTestItemList: async data =>
    http({
      url: getUrl('/api-base/material/materialItem/listPageMaterialItemChart'),
      method: 'POST',
      data,
      loading: true
    }),
  // 删除物料
  materialItemDel: async data =>
    http({
      url: getUrl('/api-base/material/materialItem/delete'),
      method: 'POST',
      data,
      loading: true
    }),
  // 删除物料
  batchDelete: async data =>
    http({
      url: getUrl('/api-base/material/materialItem/batchDelete'),
      method: 'POST',
      data,
      loading: true
    }),
  // 新增编辑物料保存
  saveOrUpdateMBatch: async data =>
    http({
      url: getUrl('/api-base/material/materialItem/saveOrUpdateMBatch'),
      method: 'POST',
      data,
      loading: true
    }),
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
    }),
  // 物料查询搜索
  getMaterialItemByParam: async data =>
    http({
      url: getUrl('/api-base/material/materialItem/listByParam'),
      method: 'POST',
      data,
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
  // 查询模板详情
  queryProcessTemplateById: async params =>
    http({
      url: getUrl('/api-flow/flow/processTemplent/queryProcessTemplateById'),
      method: 'GET',
      params,
      loading: true
    }),
  // 查询模板名称
  queryEnablePermission: async params =>
    http({
      url: getUrl('/api-flow/flow/processTemplent/queryEnablePermission'),
      method: 'GET',
      params,
      loading: true
    }),
  // 删除行信息
  deleteTemplateLines: async params =>
    http({
      url: getUrl('/api-flow/flow/processTemplent/deleteTemplateLines'),
      method: 'POST',
      params,
      loading: true
    }),
  // 启用、禁用流程模板
  processTempUpdateEnable: async params =>
    http({
      url: getUrl('/api-flow/flow/processTemplent/updateEnableFlag'),
      method: 'POST',
      params,
      loading: true
    }),
  // 查询场景编码和场景名称
  listSceneCodeAndSceneName: async params =>
    http({
      url: getUrl('/api-base/sceneAttachment/listSceneCodeAndSceneName'),
      method: 'POST',
      params,
      loading: true
    }),
  // 附件管理分页条件查询
  listPageByParm: async data =>
    http({
      url: getUrl('/api-base/sceneAttachment/listPageByParm'),
      method: 'POST',
      data,
      loading: true
    }),
  // 根据附件ID删除
  deleteSceneAttachment: async params =>
    http({
      url: getUrl('/api-base/sceneAttachment/delete'),
      method: 'GET',
      params,
      loading: true
    }),
  // 批量添加场景附件管理
  batchSaveOrUpdateSceneAttachment: async data =>
    http({
      url: getUrl('/api-base/sceneAttachment/batchSaveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    }),
  // 文件删除
  fileuploadDelete: async params =>
    http({
      url: getUrl('/api-base/sceneAttachment/deleteFileUpLoad'),
      method: 'POST',
      params,
      loading: true
    }),
  // 查询品类分工列表
  getCategoryDivisionList: async data =>
    http({
      url: getUrl('/api-base/categoryDv/listPageByParam'),
      method: 'POST',
      data,
      loading: true
    }),
  // 删除品类分工
  categoryDvDel: async params =>
    http({
      url: getUrl('/api-base/categoryDv/delete'),
      method: 'GET',
      params,
      loading: true
    }),
  // 新增编辑品类分工保存
  saveOrUpdateDvBatch: async data =>
    http({
      url: getUrl('/api-base/categoryDv/saveOrUpdateDvBatch'),
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
    }),
  // 物流品类查询
  listLogisticsCategoryByLevel: async data =>
    http({
      url: getUrl('/api-base/purchase/purchaseCategory/listLogisticsCategoryByLevel'),
      method: 'POST',
      data,
      loading: true
    }),
  // 查询附件模板配置
  sceneAttachmentConf: async data =>
    http({
      url: getUrl('/api-base/sceneAttachment/listPageByParm'),
      method: 'POST',
      data,
      loading: true
    }),
  // 业务状态查询功能
  listFunctionByParm: async data =>
    http({
      url: getUrl('/api-rbac/perm/permission/listFunctionByParm'),
      method: 'POST',
      data,
      loading: true
    }),
  // 配置导引查询
  getConfigGuide: async data =>
    http({
      url: getUrl('/api-base/configGuide/getInfoByUser'),
      method: 'GET',
      data,
      loading: true
    }),
  // 新增|编辑现场评审周期配置 供应商
  siteConfigVendorSaveOrUpdate: async data =>
    http({
      url: getUrl('/api-sup/review/siteConfigVendor/saveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    }),
  // 新增|编辑现场评审周期配置 品类
  siteConfigCateSaveOrUpdate: async data =>
    http({
      url: getUrl('/api-sup/review/siteConfigCate/saveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    }),
  // 获取上架合同
  getOnShelvesContractList: async data =>
    http({
      url: getUrl('/api-cm/contract/contractHead/getOnShelvesContractList'),
      method: 'POST',
      data,
      loading: true
    }),
  // 组织设置收货地点保存
  batchSaveOrUpdate: async data =>
    http({
      url: getUrl('/api-base/base/site/batchSaveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    }),
  // 组织设置收货地点删除
  batchdeleteUpdate: async data =>
    http({
      url: getUrl('/api-base/base/site/batchDelete'),
      method: 'POST',
      data,
      loading: true
    }),
  // 采购申请-物料明细-收货地点
  listSiteByCondition: async data =>
    http({
      url: getUrl('/api-base/base/site/listSiteByCondition'),
      method: 'POST',
      data,
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
  // 状态流转配置查询详情
  getStatusConfigDetail: async params =>
    http({
      url: getUrl('/api-base/base/statusConfig/getDetailById'),
      method: 'GET',
      params,
      loading: true
    }),
  // 状态流转配置保存
  saveOrUpdateStatusConfigCondition: async data =>
    http({
      url: getUrl('/api-base/base/statusConfig/addOrUpdateCondition'),
      method: 'POST',
      data,
      loading: true
    }),
  companyDept: {
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
      })
  },
  dataPermission: {
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
  },
  dataPermissionVarOption: {
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
  },
  systemConfigureApi: {
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
}

export const themeConfig = {
  // 获取当前用户主题
  getSystemStyle: async data =>
    http({
      url: '/api-rbac/systemStyle/getSystemStyle',
      method: 'POST',
      data,
      loading: true
    }),
  // 新增当前用户主题
  addSystemStyle: async data =>
    http({
      url: '/api-rbac/systemStyle/addSystemStyle',
      method: 'POST',
      data,
      loading: true
    }),
  // 更新当前用户主题
  updateSystemStyle: async data =>
    http({
      url: '/api-rbac/systemStyle/updateSystemStyle',
      method: 'POST',
      data,
      loading: true
    }),
  // 删除当前用户主题
  deleteSystemStyle: async data =>
    http({
      url: '/api-rbac/systemStyle/deleteSystemStyle',
      method: 'POST',
      data,
      loading: true
    })
}
