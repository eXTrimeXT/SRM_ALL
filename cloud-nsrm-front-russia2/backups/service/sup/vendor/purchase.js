/**
 * @description 供应商 - 货源清单与货源变更 /api-sup/vendor/pur
 * @description 路径：$api.sup.vendor.pur
 * @author 伟龙
 */
import http from '@/utils/axios/http'
const getUrl = path => `${path}`

// 货源清单列表页
const list = {
  saveOrUpdatuOrderByStatus: async (url, data) =>
    http({
      url: getUrl(url),
      method: 'POST',
      data,
      loading: true
    })
}

export default {
  list
}
