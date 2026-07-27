/**
 * @description 合同履约模块api，从采购商和供应商区分入口
 * @description cmPerform API $api.cmPerform
 * @author 伟龙
 */
import buyer from './cmPerform/buyer'
import vendor from './cmPerform/vendor'

// 采购商接口
export { buyer }

// 供应商接口
export { vendor }
