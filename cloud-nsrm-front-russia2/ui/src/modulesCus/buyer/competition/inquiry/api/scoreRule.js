/**
 * @description 采购商 - 评分规则配置 /api-sou/buyer/scoreRule
 * @description 路径：$api.sou.buyer.scoreRule
 * @author donghf3
 */
import http from '@/utils/axios/http'

const scoreRulePath = '/api-sou/buyer/scoreRule'
export default {
  // 列表查询
  listPage: data =>
    http({
      url: `${scoreRulePath}/page`,
      method: 'POST',
      data,
      loading: true
    }),
  // 查询详情
  getDetail: id =>
    http({
      url: `${scoreRulePath}/${id}`,
      method: 'GET',
      loading: true
    })
}
