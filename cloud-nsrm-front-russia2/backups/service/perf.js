/**
 * @description 绩效模块api，从采购商和供应商区分入口
 * @description perf API $api.perf
 * @author 伟龙
 */
import buyer from './perf/buyer'
import vendor from './perf/vendor'

// 采购商接口
export { buyer }

// 供应商接口
export { vendor }
