/**
 * @description 采购商 - 询价管理
 * @author donghf3
 */
import inq from './inq'
import scoreRule from './scoreRule'
import formula from './formula'

// 根据后端不同的 Controlller 区分不同的对象拆分
export {
  inq as inqBuyerHttp,
  scoreRule as scoreRuleHttp,
  formula as formulaHttp
}
