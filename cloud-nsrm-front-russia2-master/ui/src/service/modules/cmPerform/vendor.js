/**
 * 合同履约模块模块 - 供应商角色api - /api-cmPerform/vendor
 * 路径：$api.cmPerform.vendor
 */
import plan from './vendor/plan'
import check from './vendor/check'
import inv from './vendor/inv'

export default {
  // 合同履约计划
  plan,
  // 合同验收
  check,
  // 合同履约开票
  inv
}
