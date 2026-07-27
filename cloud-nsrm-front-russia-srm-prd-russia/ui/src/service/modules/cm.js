/**
 * @description 合同模块api，从采购商和供应商区分入口
 * @description cm API $api.cm
 * @author 伟龙
 */
import buyer from './cm/buyer'
import vendor from './cm/vendor'

// 采购商接口
export { buyer }

// 供应商接口
export { vendor }
