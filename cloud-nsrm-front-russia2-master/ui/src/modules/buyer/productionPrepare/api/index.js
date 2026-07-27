import http from '@/utils/axios/http'
const getUrl = path => `${path}`

export const productionApproval = {
  deleteInventory: async data =>
    http({
      url: getUrl('/api-ppap/outputConfirm/deleteInventory'),
      method: 'POST',
      data,
      loading: true
    }),

  deletePartDetail: async data =>
    http({
      url: getUrl('/api-ppap/outputConfirm/deletePartDetail'),
      method: 'POST',
      data,
      loading: true
    })
}

export const categoryResponsibility = {
  listPageByParam: async data =>
    http({
      url: getUrl('/api-ppap/productionLeader/listPageByParam'),
      method: 'POST',
      data,
      loading: true
    }),

  delete: async params =>
    http({
      url: getUrl('/api-ppap/productionLeader/delete'),
      method: 'GET',
      params,
      loading: true
    }),

  modifyResponsibilityByBatch: async data =>
    http({
      url: getUrl('/api-ppap/productionLeader/modifyResponsibilityByBatch'),
      method: 'POST',
      data,
      loading: true
    })
}

export const problemManagement = {
  delete: async params =>
    http({
      url: getUrl('/api-ppap/problem/exchange/delete'),
      method: 'POST',
      params,
      loading: true
    }),

  info: async params =>
    http({
      url: getUrl('/api-ppap/problem/exchange/info'),
      method: 'GET',
      params,
      loading: true
    }),

  modify: async (url, data) =>
    http({
      url,
      method: 'POST',
      data,
      loading: true
    })
}

export const questTemplate = {
  questTemplateData: async params =>
    http({
      url: getUrl('/api-ppap/quest/questTemplate/questTemplateData'),
      method: 'GET',
      params,
      loading: true
    }),

  modify: async data =>
    http({
      url: getUrl('/api-ppap/quest/questTemplate/modify'),
      method: 'POST',
      data,
      loading: true
    }),

  listPageByParm: async data =>
    http({
      url: getUrl('/api-ppap/quest/questTemplate/listPageByParm'),
      method: 'POST',
      data,
      loading: true
    }),

  saveQuestTemplateData: async data =>
    http({
      url: getUrl('/api-ppap/quest/questTemplate/saveQuestTemplateData'),
      method: 'POST',
      data,
      loading: true,
      check: 'Y'
    })
}

export const questManagement = {
  deleteById: async params =>
    http({
      url: getUrl('/api-ppap/quest/questSupplier/deleteById'),
      method: 'GET',
      params,
      loading: true
    }),

  closeQuestInventory: async data =>
    http({
      url: getUrl('/api-ppap/quest/questSupplier/closeQuestInventory'),
      method: 'POST',
      data,
      loading: true
    }),

  getQuestSupplierOperator: async data =>
    http({
      url: getUrl('/api-ppap/quest/questSupplier/getQuestSupplierOperator'),
      method: 'POST',
      data,
      loading: true
    }),

  getDetailByQuestNo: async data =>
    http({
      url: getUrl('/api-ppap/quest/questSupplier/getDetailByQuestNo'),
      method: 'POST',
      data,
      loading: true
    }),

  questinventoryfileList: async data =>
    http({
      url: getUrl('/api-ppap/quest/questinventoryfile/listPage'),
      method: 'POST',
      data,
      loading: true
    }),

  getTemplateOrg: async data =>
    http({
      url: getUrl('/api-ppap/quest/questTemplateOrg/listPage'),
      method: 'POST',
      data,
      loading: true
    }),

  getProgressDataPic: async data =>
    http({
      url: getUrl('/api-ppap/quest/questProgressReport/getProgressDataPic'),
      method: 'POST',
      data,
      loading: true
    }),

  questinventoryfileAdd: async data =>
    http({
      url: getUrl('/api-ppap/quest/questinventoryfile/add'),
      method: 'POST',
      data,
      loading: true
    }),

  questinventoryfileDelete: async data =>
    http({
      url: getUrl('/api-ppap/quest/questinventoryfile/delete'),
      method: 'POST',
      data,
      loading: true
    }),

  updateQuestSupplierApprovalStatus: async data =>
    http({
      url: getUrl('/api-ppap/quest/questSupplier/flow/updateQuestSupplierApprovalStatus'),
      method: 'POST',
      data,
      loading: true
    }),

  saveOrUpdateQuestSupplierForm: async data =>
    http({
      url: getUrl('/api-ppap/quest/questSupplier/saveOrUpdateQuestSupplierForm'),
      method: 'POST',
      data,
      loading: true
    })
}
