/**
 * 绩效模块模块 - 供应商角色api - /api-perf/vendor
 * 路径：$api.perf.vendor
 */
import query from './vendor/query'
import imp from './vendor/improvement'
import ass from './vendor/assessment'

export default {
  // 绩效结果
  query,
  // 供应商改善
  imp,
  // 供方绩效考核
  ass
}
