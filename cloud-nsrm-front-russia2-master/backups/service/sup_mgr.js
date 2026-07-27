import http from '@/utils/axios/http'
const getUrl = (path) => `${path}`

// 删除银行信息
export function bankInfoDel (params) {
  return http({
    url: '/api-sup/info/bankInfo/delete',
    method: 'GET',
    params: params
  })
}
// 删除联系人信息
export function contactInfoDel (params) {
  return http({
    url: '/api-sup/info/contactInfo/delete',
    method: 'GET',
    params: params
  })
}
// 删除财务信息
export function financeInfoDel (params) {
  return http({
    url: '/api-sup/info/financeInfo/delete',
    method: 'GET',
    params: params
  })
}
// 删除合作组织信息
export function orgInfoDel (params) {
  return http({
    url: '/api-sup/info/orgInfo/delete',
    method: 'GET',
    params: params
  })
}
// 删除组织品类信息
export function orgCategoryDel (params) {
  return http({
    url: '/api-sup/info/orgCategory/delete',
    method: 'GET',
    params: params
  })
}
// 删除附件
export function deleteAttachById (params) {
  return http({
    url: '/api-sup/info/companyInfo/deleteAttachById',
    method: 'POST',
    params: params
  })
}

// 绿色通道供应商删除
export function companyGreenChannelDel (params) {
  return http({
    url: '/api-sup/info/companyInfo/companyGreenChannelDelete',
    method: 'POST',
    data: params
  })
}
// 供应商清单驳回
export function companyGreenChannelDeleteNotDelUser (params) {
  return http({
    url: '/api-sup/info/vendorInformation/rejectInformation',
    method: 'POST',
    data: params
  })
}
// 供应商注册撤回
export function companyWithdraw (params) {
  return http({
    url: '/api-sup/info/vendorInformation/withdrawCompany',
    method: 'POST',
    data: params
  })
}

// 供应商档案驳回
export function rejectCompanyInfo (params) {
  return http({
    url: '/api-sup/info/companyInfo/rejectCompanyInfo',
    method: 'POST',
    data: params
  })
}
// 查询供应商档案变更记录
export function getCompanyStatusLog (params) {
  return http({
    url: '/api-sup/statusLog/companyStatusLog/listAllByParam',
    method: 'POST',
    data: params
  })
}
// 绿色通道暂存 提交
// /api-sup/info/companyInfo/saveCompanyGreenChannel 暂存
// /api-sup/info/companyInfo/companyGreenChannelSubmit 提交
export function saveOrUpdatuGreenChannelInfo (url, params) {
  return http({
    url: url,
    method: 'POST',
    data: params
  })
}
// //////////////// 样品
// 查询单据信息
export function getQuaSampleData (params) {
  return http({
    url: '/api-sup/qua/quaSample/getQualifiedSample',
    method: 'GET',
    params: params
  })
}

// 样品确认单据删除(批量)
export function quaSampleBathDel (params) {
  return http({
    url: '/api-sup/qua/quaSample/bathDeleteByList',
    method: 'POST',
    data: params
  })
}
// 资质审查单据删除
export function reviewFormhDel (params) {
  return http({
    url: '/api-sup/review/reviewForm/delete',
    method: 'GET',
    params: params
  })
}
// 非材资质审查单据删除
export function serviceReviewFormhDel (params) {
  return http({
    url: '/api-sup/review/serviceReviewForm/delete',
    method: 'GET',
    params: params
  })
}
// 根据资质审查单ID查询供应商档案组织与品类状态
export function getOrgCatByReviewId (params) {
  return http({
    url: '/api-sup/review/reviewForm/listOrgCateServiceStatusByReviewId',
    method: 'GET',
    params: params
  })
}
// 根据供应商ID查询供应商档案组织与品类状态
export function listOrgCateServiceStatusByCompanyId (params) {
  return http({
    url: '/api-sup/info/companyInfo/listOrgCateServiceStatusByCompanyId',
    method: 'GET',
    params: params
  })
}
// 根据资质审查单ID查询银行信息
export function getBankJournaByReviewId (params) {
  return http({
    url: '/api-sup/review/bankJournal/listBankJournal',
    method: 'GET',
    params: params
  })
}
// 根据供应商ID获取上一次评审信息
export function getLastSiteFormMessage (params) {
  return http({
    url: '/api-sup/review/siteForm/getLastSiteFormMessage',
    method: 'GET',
    params: params
  })
}

// 更新 修改单据信息
// 样品
// /api-sup/qua/quaSample/saveTemporary 暂存
// /api-sup/qua/quaSample/publish 发布
// /api-sup/qua/quaSample/confirmed 确认
// /api-sup/qua/quaSample/refused 拒绝
// /api-sup/qua/quaSample/submittedSave 提交保存
// /api-sup/qua/quaSample/submitted 提交
// /api-sup/qua/quaSample/approved 批准
// /api-sup/qua/quaSample/rejected 驳回
// 供方生效
// /api-sup/review/effectForm/saveTemporary 暂存
// /api-sup/review/effectForm/submitted 提交
// 物料试用
// /api-sup/materialTrial/saveTemporary
// /api-sup/materialTrial/publish 发布
// /api-sup/materialTrial/confirmed 确认
// /api-sup/materialTrial/refused 拒绝
// /api-sup/materialTrial/submittedSave 提交保存
// /api-sup/materialTrial/submitted 提交
// /api-sup/materialTrial/approved 批准
// /api-sup/materialTrial/rejected 驳回
// 采购目录
// /api-sup/saveOrUpdateCatalog 新增 保存
// 合作终止
// /api-sup/orgcategory/orgCatForm/saveTemporary
// /api-sup/orgcategory/orgCatForm/submitted

export function saveOrUpdatuOrderByStatus (url, params) {
  return http({
    url: url,
    method: 'POST',
    data: params
  })
}

// 查询供应商列表
export function getVendorDataList (params) {
  return http({
    url: '/api-sup/info/companyInfo/listPageByDTO',
    method: 'POST',
    data: params
  })
}
// 查询资质审查单
export function getReviewFormDataList (params) {
  return http({
    url: '/api-sup/review/reviewForm/listPageByParm',
    method: 'POST',
    data: params
  })
}

// 判断资质审类型返回准入流程类型
export function getEntryConfigByQuaReviewType (params) {
  return http({
    url: '/api-sup/entry/entryConfig/getEntryConfigByQuaReviewType',
    method: 'GET',
    params: params
  })
}

// 通过公司id和资质审查类型查询资质审查单
export function getReviewFormByParm (params) {
  return http({
    url: '/api-sup/review/reviewForm/listPageByParm',
    method: 'POST',
    data: params
  })
}

// 根据资质审查ID获取现场评审单
export function getSiteFormByReviewFormId (params) {
  return http({
    url: '/api-sup/review/siteForm/getSiteFormByReviewFormId',
    method: 'GET',
    params: params
  })
}
// 通过ID获取生效单详情 effectFormId
export function getEffectFormDTOById (params) {
  return http({
    url: '/api-sup/review/effectForm/getEffectFormDTOById',
    method: 'GET',
    params: params
  })
}
// 通过id查询物料试用详情
export function getMaterialTrialById (params) {
  return http({
    url: '/api-sup/materialTrial/get',
    method: 'GET',
    params: params
  })
}
// 查询样品确认单
export function getSampleListPageByParam (params) {
  return http({
    url: '/api-sup/qua/quaSample/listPageByParam',
    method: 'POST',
    data: params
  })
}
// 物料试用删除单据
export function materialTrialOrderDel (params) {
  return http({
    url: '/api-sup/materialTrial/bathDeleteByList',
    method: 'POST',
    data: params
  })
}

// 采购目录
// 查询采购目录详情信息
export function getPurchaseCataLogById (params) {
  return http({
    url: '/api-sup/purchaseCataLog/get',
    method: 'GET',
    params: params
  })
}
// 删除采购目录
export function purchaseCataLogDel (params) {
  return http({
    url: '/api-sup/purchaseCataLog/delete',
    method: 'POST',
    params: params
  })
}
// 删除现场评审单
export function siteFormDel (params) {
  return http({
    url: '/api-sup/review/siteForm/delete',
    method: 'GET',
    params: params
  })
}

// 供方生效单删除
export function vendoeEffectDel (data) {
  return http({
    url: '/api-sup/review/effectForm/bachDeleteByList',
    method: 'POST',
    data: data
  })
}

// 合作终止拟定单据删除
export function orgCatFormDel (params) {
  return http({
    url: '/api-sup/orgcategory/orgCatForm/delete',
    method: 'GET',
    params: params
  })
}

// 根据控制类型和供应商ID分页查询组织与品类
export function getOrgCatFormByParm (params) {
  return http({
    url: '/api-sup/orgcategory/orgCatForm/listOrgCateServiceStatusPageByParm',
    method: 'GET',
    params: params
  })
}
// 通过ID获取合作终止单据明细
export function getOrgCatFormDTOById (params) {
  return http({
    url: '/api-sup/orgcategory/orgCatForm/getOrgCatFormDTO',
    method: 'GET',
    params: params
  })
}

// 供应商变更
// 获取变更单据详情
export function getInfoByChangeId (params) {
  return http({
    url: '/api-sup/change/infoChange/getInfoByChangeId',
    method: 'GET',
    params: params
  })
}

// 变更单据删除
export function changeInfoDel (params) {
  return http({
    url: '/api-sup/change/infoChange/deleteChangeInfo',
    method: 'GET',
    params: params
  })
}
// 供应商信息变更暂存 提交
// /api-sup/change/infoChange/saveTemporary 暂存
// /api-sup/change/infoChange/submitted 提交
// /api-sup/change/infoChange/approved 审批
// /api-sup/change/infoChange/rejected 驳回
export function saveOrUpdateChannelInfo (url, data) {
  return http({
    url: url,
    method: 'POST',
    data: data,
    loading: true// change by liwenhong
  })
}
