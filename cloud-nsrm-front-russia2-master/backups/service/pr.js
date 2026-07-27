/**
 * 采购需求API
 */

import http from '@/utils/axios/http'
import { getToken } from '@/utils/auth'
import axios from 'axios'

const getUrl = path => `${path}`

// 采购需求_申请/审批页分页查询列表
export const listPage = async data =>
  http({
    url: getUrl('/api-sup-ce/pr/requirementHead/listPage'),
    method: 'POST',
    data
  })

// 采购需求新增接口
export const addPurchaseRequirement = async data =>
  http({
    url: getUrl('/api-sup-ce/pr/requirementHead/addPurchaseRequirement'),
    method: 'POST',
    data,
    loading: true
  })

// 编辑接口
export const modify = async data =>
  http({
    url: getUrl('/api-sup-ce/pr/requirementHead/modify'),
    method: 'POST',
    data,
    loading: true
  })

// 根据条件查询价格目录
export const getPriceLibraryByParam = async data =>
  http({
    url: getUrl('/api-inq/price/priceLibrary/getPriceLibraryByParam'),
    method: 'POST',
    data,
    loading: true
  })

// 获取详情
export const getByHeadId = async requirementHeadId =>
  http({
    url: getUrl('/api-sup-ce/pr/requirementHead/getByHeadId'),
    method: 'GET',
    params: { requirementHeadId },
    loading: true
  })

// 删除接口
export const deleteByHeadId = async requirementHeadId =>
  http({
    url: getUrl('/api-sup-ce/pr/requirementHead/deleteByHeadId'),
    method: 'GET',
    params: { requirementHeadId },
    loading: true
  })

// 提交审批
export const submitApproval = async data =>
  http({
    url: getUrl('/api-sup-ce/pr/requirementHead/submitApprovalWithFlow'),
    method: 'POST',
    data,
    loading: true
  })

// 通过审批
export const approval = async requirementHeadId =>
  http({
    url: getUrl('/api-sup-ce/pr/requirementHead/approval'),
    method: 'GET',
    params: { requirementHeadId },
    loading: true
  })

// 废弃
export const abandon = async requirementHeadId =>
  http({
    url: getUrl('/api-sup-ce/pr/requirementHead/abandon'),
    method: 'GET',
    params: { requirementHeadId },
    loading: true
  })

// 采购需求管理/采购页分页查询接口
export const listApprovedApplyByPage = async data =>
  http({
    url: getUrl('/api-sup-ce/pr/requirementLine/listApprovedApplyByPage'),
    method: 'POST',
    data,
    loading: true
  })

// 分配/取消分配接口
export const bachAssigned = async params =>
  http({
    url: getUrl('/api-sup-ce/pr/requirementManage/bachAssigned'),
    method: 'POST',
    params,
    loading: true
  })

// 采购需求-驳回接口
export const bachRejectRequirement = async params =>
  http({
    url: getUrl('/api-sup-ce/pr/requirementManage/bachRejectRequirement'),
    method: 'POST',
    params,
    loading: true
  })

// 检查采购需求能否合并接口
export const checkMergeRequirement = async requirementLineIds =>
  http({
    url: getUrl('/api-sup-ce/pr/requirementManage/checkMergeRequirement'),
    method: 'GET',
    params: { requirementLineIds },
    loading: true
  })

// 合并采购需求接口
export const bachRequirementMerge = async requirementLineIds =>
  http({
    url: getUrl('/api-sup-ce/pr/requirementManage/bachRequirementMerge'),
    method: 'POST',
    params: { requirementLineIds },
    loading: true
  })

// 返回货源供应商和有效价格
export const getVendorAndEffectivePrice = async data =>
  http({
    url: getUrl('/api-sup-ce/pr/requirementLine/getVendorAndEffectivePrice'),
    method: 'POST',
    data,
    loading: true
  })

// 生成采购订单
export const genOrder = async data =>
  http({
    url: getUrl('/api-sup-ce/pr/requirementLine/genOrder'),
    method: 'POST',
    data,
    loading: true
  })

// 推荐供应商
export const listRecommendVendor = async data =>
  http({
    url: getUrl('/api-sup-ce/pr/requirementLine/listRecommendVendor'),
    method: 'POST',
    data,
    loading: true
  })

// 编辑采购需求行信息
export const modifyLine = async data =>
  http({
    url: getUrl('/api-sup-ce/pr/requirementLine/modify'),
    method: 'POST',
    data,
    loading: true
  })

// 获取采购需求合并信息接口
export const findRequirementMergeList = async requirementLineIds =>
  http({
    url: getUrl('/api-sup-ce/pr/requirementManage/findRequirementMergeList'),
    method: 'GET',
    params: { requirementLineIds },
    loading: true
  })

// 生成寻源单据
export const genSourceBusiness = async data =>
  http({
    url: getUrl('/api-sup-ce/pr/requirementLine/genSourceBusiness'),
    method: 'POST',
    data,
    loading: true
  })

// 导出模板
export const excelExport = async (requirementHeadId, filename) =>
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
      dom.setAttribute('download', filename) // 指示浏览器下载url,而不是导航到它；因此将提示用户将其保存为本地文件
      document.body.appendChild(dom)
      dom.click()
    })
    .catch(error => {
      console.log(error)
    })

// 导入
export const excelImport = async data =>
  http({
    url: getUrl('/api-sup-ce/pr/requirementLine/excelImport'),
    method: 'POST',
    data,
    loading: true
  })

//   requirementHeadId;//采购需求头ID
// requirementHeadNum;//申请编号
// requirementLineId;//采购需求行ID
// rowNum;//申请行号
// organizationId;//采购组织id
// organizationName;//采购组织名称
// categoryId;//品类id
// categoryName;//品类名称
// itemId;//物料id
// itemCode;//物料编码
// itemDesc;//物料描述
// requirementQuantity;//需求数量
// vendorId;//供应商id
// vendorCode;//供应商编码
// vendorName;//供应商名称
// notaxPrice;//单价（未税）
// taxPrice;//单价（含税）
// currency;//币种
// taxRate;//税率
// priceUnit;//价格单位
// quota;//配额
// buyerName;//采购员名称
// inventoryPlace;//库存地点
// unit;//单位
// requirementDate;//需求日期
