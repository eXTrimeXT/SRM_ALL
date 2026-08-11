
/* 供应商报表API */
import http from '@/utils/axios/http'
const getUrl = path => `${path}`

// 项目详情
export const supplierReportApi = {
  // 查询项目明细
  getDetail: async data =>
    http({
      url: getUrl('api-sup/supplier/report/forms/getSupRepFormsInfo'),
      method: 'POST',
      data,
      loading: true
    })
}
