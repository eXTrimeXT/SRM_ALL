/**
 * @description 采购商 - 简易询价原来的旧能力调用  /api-sou/buyer/inq
 * @description 路径：$api.sou.buyer.inq
 * @author donghf3
 */
// @ts-ignore
import http from '@/utils/axios/http'

// 采购商 - 询价管理
const buyerPath = '/api-sou/buyer/inq'

/**
 * select 评选 Controlller
 */
const selectPath = `${buyerPath}/select`
const select = {
  getExportPriceCompareInfoUrl: (id: any) => `${selectPath}/exportPriceCompareInfos/pdf/${id}`
}

export default {
  select
}
