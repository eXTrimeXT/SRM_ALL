/**
 * @description 采购商 - 评分规则配置 /api-sou/buyer/scoreRule
 * @description 路径：$api.sou.buyer.scoreRule
 * @author donghf3
 */
import http from '@/utils/axios/http'

const scoreRulePath = '/api-sou/buyer/scoreRule'
export default {
  // 列表查询
  listPageUrl: `${scoreRulePath}/page`,
  // 列表查询
  listPage: data =>
    http({
      url: `${scoreRulePath}/page`,
      method: 'POST',
      data,
      loading: true
    }),
  // 删除
  delete: id =>
    http({
      url: `${scoreRulePath}/remove/${id}`,
      method: 'DELETE',
      loading: true
    }),
  // 生效
  valid: id =>
    http({
      url: `${scoreRulePath}/valid/${id}`,
      method: 'POST',
      loading: true
    }),
  // 失效
  invalid: id =>
    http({
      url: `${scoreRulePath}/invalid/${id}`,
      method: 'POST',
      loading: true
    }),
  // 提交
  save: data =>
    http({
      url: `${scoreRulePath}/edit`,
      method: 'POST',
      data,
      loading: true
    }),
  // 提交
  submit: data =>
    http({
      url: `${scoreRulePath}/submit`,
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
