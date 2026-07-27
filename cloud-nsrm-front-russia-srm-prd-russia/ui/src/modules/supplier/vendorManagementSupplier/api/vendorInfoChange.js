/**
 * @description 供应商 - 供应商变更 /api-sup/vendor/pur
 * @description 路径：
 * @author 伟龙
 */
import http from '@/utils/axios/http'
const getUrl = path => `${path}`

// 货源清单列表页
export const vendorInfoChangeApi = {
  // 供应商变更
  // 供应商点击编辑时触发
  ifAddInfoChange: async companyId =>
    http({
      url: getUrl('/api-sup/change/infoChange/ifAddInfoChange'),
      method: 'GET',
      params: { companyId },
      loading: true
    }),
  // 获取变更单据详情
  getInfoByChangeId: async params =>
    http({
      url: getUrl('/api-sup/change/infoChange/getInfoByChangeId'),
      method: 'GET',
      params,
      loading: true
    }),
  // 变更单据删除
  changeInfoDel: async params =>
    http({
      url: getUrl('/api-sup/change/infoChange/deleteChangeInfo'),
      method: 'GET',
      params,
      loading: true
    }),
  // 供应商信息变更暂存 提交
  saveOrUpdateChannelInfo: async (url, data) =>
    http({
      url: getUrl(url),
      method: 'POST',
      data,
      loading: true
    }),
  saveOrUpdatuOrderByStatus: async (url, data) =>
    http({
      url: getUrl(url),
      method: 'POST',
      data,
      loading: true
    })
}
