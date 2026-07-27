/* 采购需求 */

import http from '@/utils/axios/http'
import axios from 'axios'
const getUrl = path => `${path}`

// 意向金
export const yixiangJin = {
  save: async data =>
    http({
      url: getUrl('/api-sou/api-ql/SouIntDepositInvoice/submit'),
      method: 'POST',
      data,
      loading: true
    }),
}

/* 采购申请 */
export const purchaseApplicationApi = {
  // JD物料异常处理确认
  confirmAbnormal: async data =>
    http({
      url: getUrl('/api-sup-ce/api-ql/PurchaseRequirementHead/confirmAbnormal'),
      method: 'POST',
      data,
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
  query: async data =>
    http({
      url: getUrl('/api-sup-ce/api-ql/PurchaseRequirementHead/query'),
      method: 'POST',
      data,
      loading: true
    }),
  queryData: async data =>
    http({
      url: getUrl('/api-sup-ce/api-ql/PurchaseRequirementHead/read'),
      method: 'POST',
      data,
      loading: true
    }),
  save: async data =>
    http({
      url: getUrl('/api-sup-ce/api-ql/PurchaseRequirementHead/saveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    }),
  saveZhaobiao: async data =>
    http({
      url: getUrl('/api-sup-ce/api-ql/PrRequirementForBuyer/tempSaveRequirement'),
      method: 'POST',
      data,
      loading: true
    }),
  submitZhaobiao: async data =>
    http({
      url: getUrl('/api-sup-ce/api-ql/PrRequirementForBuyer/submitRequirement'),
      method: 'POST',
      data,
      loading: true
    }),
  changeZhaobiao: async data =>
    http({
      url: getUrl('/api-sup-ce/api-ql/PrSouRequirementPoolForBuyer/changeSouPlan'),
      method: 'POST',
      data,
      loading: true
    }),
  searchCaigou: async data =>
    http({
      url: getUrl('/api-sup-ce/api-ql/CatalogOnShelves/query'),
      method: 'POST',
      data,
      loading: true
    }),
  delete: async data =>
    http({
      url: getUrl('/api-sup-ce/api-ql/PrRequirementForBuyer/removeRequirement'),
      method: 'POST',
      data,
      loading: true
    }),
  pushPool: async data =>
    http({
      url: getUrl('/api-sup-ce/api-ql/PurchaseRequirementHead/pushPool'),
      method: 'POST',
      data,
      loading: true
    }),
  listProjectPlans: async data =>
    http({
      url: getUrl('/api-sup-ce/api-ql/PrSouProjectPlanForBuyer/listProjectPlans'),
      method: 'POST',
      data,
      loading: true
    }),
  projectPlanDelete: async data =>
    http({
      url: getUrl('/api-sup-ce/api-ql/PrSouProjectPlanForBuyer/removePlan'),
      method: 'POST',
      data,
      loading: true
    }),
  projectPlanSave: async data =>
    http({
      url: getUrl('/api-sup-ce/api-ql/PrSouProjectPlanForBuyer/editPlan'),
      method: 'POST',
      data,
      loading: true
    }),
  getRequirementInfo: async data =>
    http({
      url: getUrl('/api-sup-ce/api-ql/PrRequirementForBuyer/getRequirementInfo'),
      method: 'POST',
      data,
      loading: true
    }),
  getCancelInfo: async data =>
    http({
      url: getUrl('/api-sup-ce/api-ql/PrSouRequirementCancelForBuyer/getCancelInfo'),
      method: 'POST',
      data,
      loading: true
    }),
  tempSaveReqCancel: async data =>
    http({
      url: getUrl('/api-sup-ce/api-ql/PrSouRequirementCancelForBuyer/tempSaveReqCancel'),
      method: 'POST',
      data,
      loading: true
    }),
  submitReqCancel: async data =>
    http({
      url: getUrl('/api-sup-ce/api-ql/PrSouRequirementCancelForBuyer/submitReqCancel'),
      method: 'POST',
      data,
      loading: true
    }),
  getCancelInfoQuery: async data =>
    http({
      url: getUrl('/api-sup-ce/api-ql/PrSouRequirementCancelForBuyer/query'),
      method: 'POST',
      data,
      loading: true
    }),
  removeReqCancel: async data =>
    http({
      url: getUrl('/api-sup-ce/api-ql/PrSouRequirementCancelForBuyer/removeReqCancel'),
      method: 'POST',
      data,
      loading: true
    })
}

// 计划池
export const planPool = {
  checkCancelCondition: async data =>
    http({
      url: getUrl('/api-sup-ce/api-ql/PrSouRequirementPoolForBuyer/checkCancelCondition'),
      method: 'POST',
      data,
      loading: true
    }),
  createVendorRecommend: async data =>
    http({
      url: getUrl('/api-sup-ce/api-ql/PrSouRequirementPoolForBuyer/createVendorRecommend'),
      method: 'POST',
      data,
      loading: true
    }),
  createSouReq: async data =>
    http({
      url: getUrl('/api-sup-ce/api-ql/PrSouRequirementPoolForBuyer/createSouReq'),
      method: 'POST',
      data,
      loading: true
    }),
  createSou: async data =>
    http({
      url: getUrl('/api-sup-ce/api-ql/PrSouRequirementPoolForBuyer/createSou'),
      method: 'POST',
      data,
      loading: true
    }),
  createBidSou: async data =>
    http({
      url: getUrl('/api-sup-ce/api-ql/PrSouRequirementPoolForBuyer/createBidSou'),
      method: 'POST',
      data,
      loading: true
    })
}

// 共享库存
export const sharedINV = {
  save: async data =>
    http({
      url: getUrl('/api-sup-ce/api-ql/PrShareStock/save'),
      method: 'POST',
      data,
      loading: true
    }),
  delete: async data =>
    http({
      url: getUrl('/api-sup-ce/api-ql/PrShareStock/delete'),
      method: 'POST',
      data,
      loading: true
    })
}

// 招标资料
export const biddingDocuments = {
  createVendorRecommend: async data =>
    http({
      url: getUrl('/api-sou/api-ql/SubmitBuyer/query'),
      method: 'POST',
      data,
      loading: true
    }),
  read: async data =>
    http({
      url: getUrl('/api-sou/api-ql/SubmitBuyer/read'),
      method: 'POST',
      data,
      loading: true
    }),
  delete: async data =>
    http({
      url: getUrl('/api-sou/api-ql/SubmitBuyer/delete'),
      method: 'POST',
      data,
      loading: true
    }),
  save: async data =>
    http({
      url: getUrl('/api-sou/api-ql/SubmitBuyer/saveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    }),
  submit: async data =>
    http({
      url: getUrl('/api-sou/api-ql/SubmitBuyer/submit'),
      method: 'POST',
      data,
      loading: true
    }),
  querySouPool: async data =>
    http({
      url: getUrl('/api-sou/api-ql/PrSouRequirementPoolForBuyer/querySouPool'),
      method: 'POST',
      data,
      loading: true
    }),
  extSouExpertForBuyer: async data =>
    http({
      url: getUrl('/api-sou/api-ql/ExtSouExpertForBuyer/queryExperts'),
      method: 'POST',
      data,
      loading: true
    })
}

// 澄清质疑
export const qa = {
  batchAssign: async data =>
    http({
      url: getUrl('/api-sup-ce/api-ql/PrSouRequirementPoolForBuyer/batchAssign'),
      method: 'POST',
      data,
      loading: true
    }),
  pushSgin: async data =>
    http({
      url: getUrl('/api-sou/sou/answer/pushSgin'),
      method: 'POST',
      data,
      loading: true
    }),
  saveVendor: async data =>
    http({
      url: getUrl('/api-sou/api-ql/VendorQuestion/save'),
      method: 'POST',
      data,
      loading: true
    }),
  submitVendor: async data =>
    http({
      url: getUrl('/api-sou/api-ql/VendorQuestion/submit'),
      method: 'POST',
      data,
      loading: true
    }),
  readVendor: async data =>
    http({
      url: getUrl('/api-sou/api-ql/VendorQuestion/read'),
      method: 'POST',
      data,
      loading: true
    }),
  read: async data =>
    http({
      url: getUrl('/api-sou/api-ql/Question/read'),
      method: 'POST',
      data,
      loading: true
    }),
  save: async data =>
    http({
      url: getUrl('/api-sou/api-ql/Question/save'),
      method: 'POST',
      data,
      loading: true
    }),
  submit: async data =>
    http({
      url: getUrl('/api-sou/api-ql/Question/submit'),
      method: 'POST',
      data,
      loading: true
    }),
  answerRead: async data =>
    http({
      url: getUrl('/api-sou/api-ql/Answer/read'),
      method: 'POST',
      data,
      loading: true
    }),
  answerReadVendor: async data =>
    http({
      url: getUrl('/api-sou/api-ql/VendorAnswer/read'),
      method: 'POST',
      data,
      loading: true
    }),
  answerSave: async data =>
    http({
      url: getUrl('/api-sou/api-ql/Answer/save'),
      method: 'POST',
      data,
      loading: true
    }),
  answerSubmit: async data =>
    http({
      url: getUrl('/api-sou/api-ql/Answer/submit'),
      method: 'POST',
      data,
      loading: true
    }),
  answerDelete: async data =>
    http({
      url: getUrl('/api-sou/api-ql/Answer/delete'),
      method: 'POST',
      data,
      loading: true
    }),
  readByAnswer: async data =>
    http({
      url: getUrl('/api-sou/api-ql/Replay/read'),
      method: 'POST',
      data,
      loading: true
    }),
  replaySave: async data =>
    http({
      url: getUrl('/api-sou/api-ql/Replay/save'),
      method: 'POST',
      data,
      loading: true
    }),
  contractDetails: async data =>
    http({
      url: getUrl('/api-cm/api-ql/ContractHead/read'),
      method: 'POST',
      data,
      loading: true
    }),
  answerSaveVendor: async data =>
    http({
      url: getUrl('/api-sou/api-ql/VendorAnswer/save'),
      method: 'POST',
      data,
      loading: true
    }),
  answerSubmitVendor: async data =>
    http({
      url: getUrl('/api-sou/api-ql/VendorAnswer/submit'),
      method: 'POST',
      data,
      loading: true
    }),
  confirm: async data =>
    http({
      url: getUrl('/api-sou/api-ql/AnswerVendor/confirm'),
      method: 'POST',
      data,
      loading: true
    }),
  answerVendorQuery: async data =>
    http({
      url: getUrl('/api-sou/api-ql/AnswerVendor/query'),
      method: 'POST',
      data,
      loading: true
    })
}

/* 需求提报 */
export const applicationAndAuditApi = {
  approval: async requirementHeadId =>
    http({
      url: getUrl('/api-sup-ce/pr/requirementHead/approval'),
      method: 'GET',
      params: { requirementHeadId },
      loading: true
    }),
  submitApproval: async data =>
    http({
      url: getUrl('/api-sup-ce/pr/requirementHead/submitApprovalWithFlow'),
      method: 'POST',
      data,
      loading: true
    }),
  deleteByHeadId: async requirementHeadId =>
    http({
      url: getUrl('/api-sup-ce/pr/requirementHead/deleteByHeadId'),
      method: 'GET',
      params: { requirementHeadId },
      loading: true
    }),
  abandon: async requirementHeadId =>
    http({
      url: getUrl('/api-sup-ce/pr/requirementHead/abandon'),
      method: 'GET',
      params: { requirementHeadId },
      loading: true
    }),
  // 编辑
  modify: async data =>
    http({
      url: getUrl('/api-sup-ce/pr/requirementHead/modify'),
      method: 'POST',
      data,
      loading: true
    }),
  // 采购需求新增接口
  addPurchaseRequirement: async data =>
    http({
      url: getUrl('/api-sup-ce/pr/requirementHead/addPurchaseRequirement'),
      method: 'POST',
      data,
      loading: true
    }),
  // 获取详情
  getByHeadId: async requirementHeadId =>
    http({
      url: getUrl('/api-sup-ce/pr/requirementHead/getByHeadId'),
      method: 'GET',
      params: { requirementHeadId },
      loading: true
    }),
  requirementHeadListPage: async data =>
    http({
      url: getUrl('/api-sup-ce/pr/requirementHead/listPage'),
      method: 'POST',
      data
    }),
  excelExport: async (requirementHeadId, filename) =>
    axios({
      method: 'GET',
      url: getUrl('/api-sup-ce/pr/requirementLine/excelExport'),
      headers: {
        Authorization: 'Bearer ' + getToken()
      },
      params: { requirementHeadId },
      responseType: 'arraybuffer'
    })
      .then(response => {
        const { data } = response
        if (response.headers['content-type'].startsWith('application/json')) {
          let enc = new TextDecoder('utf-8')
          let res = JSON.parse(enc.decode(new Uint8Array(data))) // 转化成json对象
          throw new Error(res.message)
        }
        const blob = new Blob([data])
        // const disposition = headers['content-disposition'] || ''
        const url = window.URL.createObjectURL(blob) // URL.createObjectURL(object)表示生成一个File对象或Blob对象
        let dom = document.createElement('a') // 设置一个隐藏的a标签，href为输出流，设置download
        dom.style.display = 'none'
        dom.href = url
        dom.rel = 'noopener'
        dom.setAttribute('download', filename) // 指示浏览器下载url,而不是导航到它；因此将提示用户将其保存为本地文件
        document.body.appendChild(dom)
        dom.click()
      })
      .catch(error => {
        console.log(error)
      })
}

export const managementAndPurchaseApi = {
  // 生成采购订单
  genOrder: async data =>
    http({
      url: getUrl('/api-sup-ce/pr/requirementLine/genOrder'),
      method: 'POST',
      data,
      loading: true
    }),
  // 检查采购需求能否合并接口
  checkMergeRequirement: async requirementLineIds =>
    http({
      url: getUrl('/api-sup-ce/pr/requirementManage/checkMergeRequirement'),
      method: 'GET',
      params: { requirementLineIds },
      loading: true
    }),
  // 推荐供应商
  listRecommendVendor: async data =>
    http({
      url: getUrl('/api-sup-ce/pr/requirementLine/listRecommendVendor'),
      method: 'POST',
      data,
      loading: true
    }),
  // 编辑采购需求行信息
  modifyLine: async data =>
    http({
      url: getUrl('/api-sup-ce/pr/requirementLine/modify'),
      method: 'POST',
      data,
      loading: true
    }),
  // 分配/取消分配接口
  bachAssigned: async params =>
    http({
      url: getUrl('/api-sup-ce/pr/requirementManage/bachAssigned'),
      method: 'POST',
      params,
      loading: true
    }),
  // 生成寻源单据
  genSourceBusiness: async data =>
    http({
      url: getUrl('/api-sup-ce/pr/requirementLine/genSourceBusiness'),
      method: 'POST',
      data,
      loading: true
    }),
  // 合并采购需求接口
  bachRequirementMerge: async requirementLineIds =>
    http({
      url: getUrl('/api-sup-ce/pr/requirementManage/bachRequirementMerge'),
      method: 'POST',
      params: { requirementLineIds },
      loading: true
    }),
  // 采购需求管理/采购页分页查询接口
  listApprovedApplyByPage: async data =>
    http({
      url: getUrl('/api-sup-ce/pr/requirementLine/listApprovedApplyByPage'),
      method: 'POST',
      data,
      loading: true
    }),
  // 根据条件查询价格目录
  getPriceLibraryByParam: async data =>
    http({
      url: getUrl('/api-inq/price/priceLibrary/getPriceLibraryByParam'),
      method: 'POST',
      data,
      loading: true
    })
}
export const extPrSouRequirementApi = {
  // 推荐供应商
  query: async data =>
    http({
      url: getUrl('/api-sup-ce/api-ql/ExtPrSouRequirementBidResult/query'),
      method: 'POST',
      data,
      loading: true
    })
}
