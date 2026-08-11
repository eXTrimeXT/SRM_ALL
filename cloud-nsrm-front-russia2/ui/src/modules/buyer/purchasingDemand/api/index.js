/* 采购需求 */

import http from '@/utils/axios/http'
import axios from 'axios'
const getUrl = path => `${path}`

/* 采购申请 */
export const purchaseApplicationApi = {
  // 获取下一级节点
  getCatChildrenData: async params =>
    http({
      url: getUrl('/api-base/purchase/purchaseCategory/listChildren'),
      method: 'POST',
      params,
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
