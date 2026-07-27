/**
 * @description 报价单头
 */
import {
  // expression,
  generateCharExpressionByFunction,
  generateCharFunctionExpression,
  i18nExpression
} from '@meicloud/render-engine'

interface DetailHeaderProps {}

export default function (props?: DetailHeaderProps): Record<any, any> {
  return {
    // 标题 询价详情
    headerTips: {
      type: 'void',
      'x-component': 'h2',
      'x-component-props': {
        style: 'margin-top: 0'
      },
      'x-content': i18nExpression('bidMod.inquiryDetail')
    },

    // 步骤条
    steps: {
      type: 'void',
      'x-decorator': 'div',
      'x-decorator-props': {
        style: 'padding: 11px 33px 30px;'
      },
      'x-component': 'Steps',
      'x-component-props': {
        alignCenter: true,
        finishStatus: 'success'
      },
      'x-reactions': {
        dependencies: ['extProjectStatus', 'orderStatus'],
        fulfill: {
          state: {
            'component[1].active': generateCharExpressionByFunction(({ $deps }) => {
              return ['PRICING', 'PRICE_END'].includes($deps[0]) ? 2 : ($deps[1] === 'SUBMISSION' ? 1 : 0)
            })
          }
        }
      },
      properties: {
        step1: {
          type: 'void',
          'x-component': 'el-step',
          'x-component-props': {
            title: '待报价'
          }
        },
        step2: {
          type: 'void',
          'x-component': 'el-step',
          'x-component-props': {
            title: i18nExpression('bidMod.inquiryStatus3')
          }
        },
        step3: {
          type: 'void',
          'x-component': 'el-step',
          'x-component-props': {
            title: i18nExpression('bidMod.inquiryStatus4')
          }
        }
      }
    },

    // 截止提示
    dynamicCutoffTime: {
      type: 'void',
      'x-decorator': 'div',
      'x-component': 'DynamicCutoffTime',
      'x-component-props': {
        label: i18nExpression('bidMod.curQuoteDeadline'),
        deadlineTime: generateCharExpressionByFunction(({ $values }) => {
          return $values.initInfo?.projectInfo?.orderEndTime || ''
        }),
        '@isDeadline': generateCharFunctionExpression(({ $form }, value) => {
          const state = $form.query('state').get('data')
          if (state) {
            state.isDeadline = value
          }
        })
      }
    }
  }
}
