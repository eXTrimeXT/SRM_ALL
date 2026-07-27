/* 合同模块 */

import http from '@/utils/axios/http'
const getUrl = path => `${path}`

export const contractManagement = {
  // 印章管理
  seal: {
    add: async data =>
      http({
        url: getUrl('/api-pj/contract/seal/add'),
        method: 'POST',
        data,
        loading: true
      }),
    delete: async params =>
      http({
        url: getUrl('/api-pj/contract/seal/delete'),
        method: 'GET',
        params,
        loading: true
      }),
    get: async params =>
      http({
        url: getUrl('/api-pj/contract/seal/get'),
        method: 'GET',
        params,
        loading: true
      }),
    update: async data =>
      http({
        url: getUrl('/api-pj/contract/seal/update'),
        method: 'POST',
        data,
        loading: true
      })
  },
  contract: {
    // 获取合同经办人部门
    getDepartment: async username =>
      http({
        url: `/api-pj/pj-anon/user/getHrUserOrgnizationByUsername?username=${username}`,
        method: 'GET'
      }),
    // 发布
    release: async data =>
      http({
        url: getUrl('/api-cm/contract/contractHead/release'),
        method: 'POST',
        data,
        loading: true
      }),
    close: async contractHeadId =>
      http({
        url: getUrl('/api-cm/contract/contractHead/close'),
        method: 'GET',
        params: { contractHeadId }
      }),
    uploadCloseAnnex: async data =>
      http({
        url: getUrl('/api-cm/contract/contractHead/uploadCloseAnnex'),
        method: 'POST',
        data
      }),
    paperArchiveConfirm: async data =>
      http({
        url: getUrl('/api-cm/contract/contractHead/paperArchive'),
        method: 'POST',
        data
      }),
    // 获取合同详情
    getInfoById: async (contractHeadId, sourceId) =>
      http({
        url: getUrl('/api-cm/contract/contractHead/getContract'),
        method: 'GET',
        params: { contractHeadId, sourceId },
        loading: true
      }),
    // 发布供应商暂存
    savePublish: async data =>
      http({
        url: getUrl(
          '/api-cm/contract/contractHead/saveOrUpdate'
        ),
        method: 'POST',
        data,
        loading: true
      }),
    // 发布供应商提交
    publish: async data =>
      http({
        url: getUrl(
          '/api-cm/contract/contractHead/publish'
        ),
        method: 'POST',
        data,
        loading: true
      }),
    // 新增合同
    save: async data =>
      http({
        url: getUrl(
          '/api-cm/contract/contractHead/buyerSaveOrUpdateContractDTOSecond'
        ),
        method: 'POST',
        data,
        loading: true
      }),
    // 新增合同，onlyoffice版本
    saveContractOnlyOffice: async data =>
      http({
        url: getUrl(
          '/api-cm/contract/contractHead/onlyOffice/buyerSaveOrUpdateContractDTOSecond'
        ),
        method: 'POST',
        data,
        loading: true
      }),
    // 保存合同详情缓存，用于预览时替换站占位符
    saveCache: data =>
      http({
        url: getUrl(
          '/api-cm/contract/contractHead/saveCache'
        ),
        method: 'POST',
        data
        // loading: true
      }),
    // 获取合同模板中添加的元素
    getModelElement: modelHeadId =>
      http({
        url: getUrl(
          '/api-cm/cm/elemhead/list'
        ),
        method: 'POST',
        data: { modelHeadId },
        loading: true
      }),
    // 根据来源单号和组织id和供应商ID查找合同物料
    getMaterialsBySource: async data =>
      http({
        url: getUrl('/api-cm/contract/contractHead/getMaterialsBySource'),
        method: 'POST',
        data,
        loading: true
      }),
    // 采购商提交审批
    approval: async data =>
      http({
        url: getUrl('/api-cm/contract/contractHead/buyerSubmitApprovalSecond'),
        method: 'POST',
        data,
        loading: true
      }),
    // 供应商提交合同其他信息
    vendorConfirm: async data =>
      http({
        url: getUrl('/api-cm/contract/contractHead/vendorConfirm'),
        method: 'POST',
        data,
        loading: true
      }),
    // 供应商确认
    vendorConfirm2: async (contractHeadId) =>
      http({
        url: getUrl('/api-cm/contract/contractHead/pass'),
        method: 'GET',
        params: { contractHeadId },
        loading: true
      }),
    // 供应商驳回
    reject: async (contractHeadId) =>
      http({
        url: getUrl('/api-cm/contract/contractHead/rejected'),
        method: 'GET',
        params: { contractHeadId },
        loading: true
      }),
    // 物料发起价格变更
    cratePriceChangeSource: async data =>
      http({
        url: getUrl('/api-cm/contract/contractHead/cratePriceChangeSource'),
        method: 'POST',
        data
      })
  },
  // 合同定级维护
  gradingRules: {
    // 合同定级维护新增
    add: async data =>
      http({
        url: getUrl('/api-cm/contract/level-maintain/add'),
        method: 'POST',
        data
      }),
    // 合同定级维护修改
    modify: async data =>
      http({
        url: getUrl('/api-cm/contract/level-maintain/modify'),
        method: 'POST',
        data
      }),
    // 合同定级维护删除
    delete: async levelMaintainId =>
      http({
        url: getUrl('/api-cm/contract/level-maintain/delete'),
        method: 'GET',
        params: { levelMaintainId }
      }),
    // 查询合同定级维护分页
    listPage: async data =>
      http({
        url: getUrl('/api-cm/contract/level-maintain/listPage'),
        method: 'POST',
        data
      })
  },
  elelment: {
    // 合同要素保存
    save: async data =>
      http({
        url: getUrl('/api-cm/elem-maintain/batchSaveOrUpdate'),
        method: 'POST',
        data,
        loading: true
      }),
    // 合同要素分页查询
    listPage: async data =>
      http({
        url: getUrl('/api-cm/elem-maintain/listPage'),
        method: 'POST',
        data,
        loading: true
      })
  },
  typeElement: {
    // 合同专属要素保存
    save: async data =>
      http({
        url: getUrl('/api-cm/type-range/batchSaveOrUpdate'),
        method: 'POST',
        data
      }),
    // 合同专属要素分页查询
    listPage: async data =>
      http({
        url: getUrl('/api-cm/type-range/listPage'),
        method: 'POST',
        data
      }),
    // 查询指定合同类型有效的合同
    queryByValid: async contractType =>
      http({
        url: getUrl('/api-cm/type-range/queryByValid'),
        method: 'GET',
        params: { contractType }
      })
  },
  condFactor: {
    // 条件因素分页查询
    listPage: async data =>
      http({
        url: getUrl('/api-cm/cond-factor/listPage'),
        method: 'POST',
        data
      }),
    // 条件因素新增
    add: async data =>
      http({
        url: getUrl('/api-cm/cond-factor/add'),
        method: 'POST',
        data
      }),
    // 条件因素更新
    modify: async data =>
      http({
        url: getUrl('/api-cm/cond-factor/modify'),
        method: 'POST',
        data
      }),
    // 条件因素保存
    save: async data =>
      http({
        url: getUrl('/api-cm/cond-factor/batchSaveOrUpdate'),
        method: 'POST',
        data
      }),
    // 条件因素删除
    delete: async condFactorId =>
      http({
        url: getUrl('/api-cm/cond-factor/delete'),
        method: 'GET',
        params: { condFactorId }
      })
  },
  modelLine: {
    // 查询模板下的元素列表
    getModelLine: async modelHeadId =>
      http({
        url: getUrl('/api-cm/modelLine/getModelLine'),
        method: 'GET',
        params: { modelHeadId }
      })
  },
  // 根据合同类型获取合同模板选择列表
  modelListByType: async modelType =>
    http({
      url: getUrl('/api-cm/modelHead/modelListByType'),
      method: 'GET',
      params: { modelType }
    }),
  // 检查是否有变更单据
  changePreCheck: async data =>
    http({
      url: getUrl('/api-cm/contract/contractHead/changePreCheck'),
      method: 'POST',
      data
    }),
  // 通过id查询模板信息
  getById: async modelHeadId =>
    http({
      url: getUrl('/api-cm/modelHead/getById'),
      method: 'GET',
      params: { modelHeadId }
    }),
  // 合同模板生效
  takeEffect: async modelHeadId =>
    http({
      url: getUrl('/api-cm/modelHead/takeEffect'),
      method: 'GET',
      params: { modelHeadId }
    }),
  failure: async modelHeadId =>
    http({
      url: getUrl('/api-cm/modelHead/failure'),
      method: 'GET',
      params: { modelHeadId }
    }),
  // 合同模板冻结
  freeze: async modelHeadId =>
    http({
      url: getUrl('/api-cm/modelHead/freeze'),
      method: 'GET',
      params: { modelHeadId }
    }),
  // 删除合同模板
  deleteMode2: async modelHeadId =>
    http({
      url: getUrl('/api-cm/modelHead/delete'),
      method: 'GET',
      params: { modelHeadId }
    }),
  copyItem: async (modelHeadId) =>
    http({
      url: getUrl('/api-cm/modelHead/copyItem'),
      method: 'GET',
      params: { modelHeadId },
      loading: true
    }),
  // 合同模板新增
  add: data =>
    http({
      url: getUrl('/api-cm/modelHead/add'),
      method: 'POST',
      data
    }),
  // 合同模板修改（拟定状态）
  modifyAll: async data =>
    http({
      url: getUrl('/api-cm/modelHead/modifyAll'),
      method: 'POST',
      data
    })
}
