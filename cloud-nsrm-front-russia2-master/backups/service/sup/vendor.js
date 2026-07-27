/**
 * 供应商模块模块 - 供应商角色api - /api-sup/vendor
 * 路径：$api.sup.vendor
 */
import pur from './vendor/purchase'
import drawing from './vendor/drawingshead'
import change from './vendor/vendorInfoChange'

export default {
  // 货源清单与货源变更 缩写为pur
  pur,
  // 物料图纸
  drawing,
  // 供应商变更
  change
}
