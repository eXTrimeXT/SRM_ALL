import { merge } from 'lodash'
import { orderConfigPj } from './configCus/pjOrderConfig'

export const orderConfigDefault = {
  showContractInfor: 'Y', // 采购订单是否展示合同信息
  showBom: 'Y' // 采购订单/采购申请是否展示bom信息
}

export const orderConfig = merge(orderConfigDefault, orderConfigPj)
