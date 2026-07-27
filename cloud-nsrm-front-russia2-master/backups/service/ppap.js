/**
 * API 神准管理
 */
import http from '@/utils/axios/http'

export const categoryResponsibilityList = async data =>
  http({
    url: '/api-ppap/productionLeader/listPageByParam',
    method: 'POST',
    data,
    loading: true
  })

export const categoryResponsibilityDelete = async params =>
  http({
    url: '/api-ppap/productionLeader/delete',
    method: 'GET',
    params,
    loading: true
  })

export const categoryResponsibilityModify = async data =>
  http({
    url: '/api-ppap/productionLeader/modifyResponsibilityByBatch',
    method: 'POST',
    data,
    loading: true
  })
export const problemManagementDetail = async params =>
  http({
    url: '/api-ppap/problem/exchange/info',
    method: 'GET',
    params,
    loading: true
  })
export const problemManagementModify = async (url, data) =>
  http({
    url: url,
    method: 'POST',
    data,
    loading: true
  })
export const problemManagementDelete = async params =>
  http({
    url: '/api-ppap/problem/exchange/delete',
    method: 'POST',
    params,
    loading: true
  })
export const productionApprovalDelete = async data =>
  http({
    url: '/api-ppap/outputConfirm/deletePartDetail',
    method: 'POST',
    data,
    loading: true
  })
export const deleteInventory = async data =>
  http({
    url: '/api-ppap/outputConfirm/deleteInventory',
    method: 'POST',
    data,
    loading: true
  })
export const getQuestSupplierOperator = async data =>
  http({
    url: '/api-ppap/quest/questSupplier/getQuestSupplierOperator',
    method: 'POST',
    data,
    loading: true
  })
export const updateQuestSupplierApprovalStatus = async data =>
  http({
    url: '/api-ppap/quest/questSupplier/flow/updateQuestSupplierApprovalStatus',
    method: 'POST',
    data,
    loading: true
  })
export const questinventoryfileDelete = async data =>
  http({
    url: '/api-ppap/quest/questinventoryfile/delete',
    method: 'POST',
    data,
    loading: true
  })

export const questinventoryfileAdd = async data =>
  http({
    url: '/api-ppap/quest/questinventoryfile/add',
    method: 'POST',
    data,
    loading: true
  })

export const questinventoryfileList = async data =>
  http({
    url: '/api-ppap/quest/questinventoryfile/listPage',
    method: 'POST',
    data,
    loading: true
  })

export const getProgressDataPic = async data =>
  http({
    url: '/api-ppap/quest/questProgressReport/getProgressDataPic',
    method: 'POST',
    data,
    loading: true
  })

export const saveOrUpdateQuestSupplierForm = async data =>
  http({
    url: '/api-ppap/quest/questSupplier/saveOrUpdateQuestSupplierForm',
    method: 'POST',
    data,
    loading: true
  })
export const getDetailByQuestNo = async data =>
  http({
    url: '/api-ppap/quest/questSupplier/getDetailByQuestNo',
    method: 'POST',
    data,
    loading: true
  })
export const getTemplateOrg = async data =>
  http({
    url: '/api-ppap/quest/questTemplateOrg/listPage',
    method: 'POST',
    data,
    loading: true
  })
export const questManagementDelete = async params =>
  http({
    url: '/api-ppap/quest/questSupplier/deleteById',
    method: 'GET',
    params,
    loading: true
  })
export const questManagementClose = async data =>
  http({
    url: '/api-ppap/quest/questSupplier/closeQuestInventory',
    method: 'POST',
    data,
    loading: true
  })
export const questTemplateList = async data =>
  http({
    url: '/api-ppap/quest/questTemplate/listPageByParm',
    method: 'POST',
    data,
    loading: true
  })

export const questTemplateData = async params =>
  http({
    url: '/api-ppap/quest/questTemplate/questTemplateData',
    method: 'GET',
    params,
    loading: true
  })

export const questTemplateModify = async data =>
  http({
    url: '/api-ppap/quest/questTemplate/modify',
    method: 'POST',
    data,
    loading: true
  })

export const saveQuestTemplateData = async data =>
  http({
    url: '/api-ppap/quest/questTemplate/saveQuestTemplateData',
    method: 'POST',
    data,
    loading: true,
    check: 'Y'
  })
