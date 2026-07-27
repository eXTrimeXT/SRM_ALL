/**
 * @description 询价进度
 */
import { i18nExpression, generateCharExpressionByFunction } from '@meicloud/render-engine'

const InquiryStepsSegment: Record<any, any> = {
  inquirySteps: {
    type: 'void',
    'x-decorator': 'div',
    'x-decorator-props': {
      style: 'height: 100px; margin-top:  10px; border: none;'
    },
    'x-component': 'Steps',
    'x-component-props': {
      alignCenter: true,
      finishStatus: 'success'
    },
    'x-reactions': {
      dependencies: ['header.extProjectStatus'],
      fulfill: {
        state: {
          'component[1].active': generateCharExpressionByFunction(({ $deps }) => {
            return $deps[0]
              ? ($deps[0] === 'ACCEPT_ORDER'
                ? 2
                : (
                  $deps[0] === 'ACCEPT_ORDER'
                    ? 5
                    : (
                      ['ORDER_END', 'EVALUATING', 'PRICING'].includes($deps[0])
                        ? 3
                      : 1
                    )
                )
              ) : 0
          })
        }
      }
    },
    properties: {
      // 发布
      step1: {
        type: 'void',
        'x-component-props': {
          title: i18nExpression('bidMod.inQstatus2'),
          // 发布时间 publishTime
          description: generateCharExpressionByFunction(({ $values }) => $values.header?.publishTime)
        }
      },
      // 报价开始
      step2: {
        type: 'void',
        'x-component-props': {
          title: i18nExpression('bidMod.inQstatus3'),
          // 报价开始时间 orderStartTime
          description: generateCharExpressionByFunction(({ $values }) => $values.header?.orderStartTime)
        }
      },
      // 报价截止
      step3: {
        type: 'void',
        'x-component-props': {
          title: i18nExpression('bidMod.inQstatus4'),
          // 报价结束时间 orderEndTime
          description: generateCharExpressionByFunction(({ $values }) => $values.header?.orderEndTime)
        }
      },
      // 评选中
      step4: {
        type: 'void',
        'x-component-props': {
          title: i18nExpression('bidMod.pingxuan')  // '评选中'
        }
      },
      // 询价结束
      step5: {
        type: 'void',
        // 'x-component': 'el-step',
        'x-component-props': {
          title: i18nExpression('bidMod.inQstatus6')
        }
      }
    }
  }
}

export default InquiryStepsSegment
