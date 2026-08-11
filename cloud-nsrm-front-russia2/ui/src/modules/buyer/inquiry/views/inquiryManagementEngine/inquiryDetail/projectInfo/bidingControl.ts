/**
 * @description 投标控制
 */
import { generateCharFunctionExpression } from '@meicloud/render-engine'
import { checkboxByYOrNSegment } from 'lib@/components/render-engine'

const BidingControlSegment: Record<any, any> = {
  bidingControlForm: {
    type: 'void',
    'x-component': 'FormGrid',
    'x-component-props': {
      maxColumns: 3,
      columnGap: 32,
      rowGap: 0
    },
    properties: {
      // 允许供应商撤回报价
      allowWithdraw: {
        ...checkboxByYOrNSegment,
        'x-component-props': {
          ...checkboxByYOrNSegment['x-component-props'],
          label: '允许供应商撤回报价'
        }
      },
      // 允许供应商只对部分商品（组合）报价
      allowPartPrice: {
        ...checkboxByYOrNSegment,
        'x-component-props': {
          ...checkboxByYOrNSegment['x-component-props'],
          label: '允许供应商只对部分商品（组合）报价'
        }
      },
      // 密封报价
      needEncryptPrice: {
        ...checkboxByYOrNSegment,
        'x-component-props': {
          ...checkboxByYOrNSegment['x-component-props'],
          label: '密封报价',
          '@change': generateCharFunctionExpression(({ $values }, value) => {
            if (value === 'Y') {
              $values.allowProxyOrder = 'N'
            }
          })
        }
      },
      // 是否允许代理报价
      allowProxyOrder: {
        ...checkboxByYOrNSegment,
        'x-component-props': {
          ...checkboxByYOrNSegment['x-component-props'],
          label: '是否允许代理报价',
          '@change': generateCharFunctionExpression(({ $values }, value) => {
            if (value === 'Y') {
              $values.needEncryptPrice = 'N'
            }
          })
        }
      }
    }
  }
}

export default BidingControlSegment
