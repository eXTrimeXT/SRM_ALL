/**
 * 合同模板API
 */

import http from '@/utils/axios/http'
const getUrl = path => `${path}`

const modelLine = {
  // 查询模板下的元素列表
  getModelLine: async modelHeadId =>
    http({
      url: getUrl('/api-cm/modelLine/getModelLine'),
      method: 'GET',
      params: { modelHeadId }
    })
}

const contract = {
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
}

const condFactor = {
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
}

const payType = {
  // 付款条件新增
  paymentTermsAdd: async data =>
    http({
      url: getUrl('/api-cm/template/payType/paymentTermsAdd'),
      method: 'POST',
      data
    }),
  // 付款条件保存
  save: async data =>
    http({
      url: getUrl('/api-cm/cond-factor/batchSaveOrUpdate'),
      method: 'POST',
      data
    }),
  // 付款条件修改
  paymentTermsUpdate: async data =>
    http({
      url: getUrl('/api-cm/template/payType/paymentTermsUpdate'),
      method: 'POST',
      data
    }),
  // 付款条件分页查询
  paymentTermsPage: async data =>
    http({
      url: getUrl('/api-cm/template/payType/paymentTermsPage'),
      method: 'POST',
      data
    })
}

const elelment = {
  // 合同要素保存
  save: async data =>
    http({
      url: getUrl('/api-cm/elem-maintain/batchSaveOrUpdate'),
      method: 'POST',
      data
    }),
  // 合同要素分页查询
  listPage: async data =>
    http({
      url: getUrl('/api-cm/elem-maintain/listPage'),
      method: 'POST',
      data,
      loading: true
    })
}

const typeElement = {
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
}

export default {
  modelLine,
  contract,
  condFactor,
  payType,
  elelment,
  typeElement
}
