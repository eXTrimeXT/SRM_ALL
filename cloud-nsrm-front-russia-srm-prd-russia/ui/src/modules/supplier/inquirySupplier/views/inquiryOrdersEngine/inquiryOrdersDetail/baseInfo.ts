/**
 * @description 项目信息
 */
import { i18nExpression } from '@meicloud/render-engine'

const BaseInfoSegment: Record<any, any> = {
  baseInfo: {
    type: 'void',
    'x-component': 'FormGrid',
    'x-component-props': {
      minColumns: 1,
      maxColumns: 3,
      columnGap: 32,
      rowGap: 0
    },
    'x-read-pretty': true,
    properties: {
      // 询价单号
      'souProject.souNo': {
        type: 'string',
        title: i18nExpression('bidMod.inquiryNo'),
        'x-query-engine-skip': true,
        'x-query-engine-relation': 'souProject:*',
        default: '',
        'x-decorator': 'FormItem'
      },
      // 询价标题
      'souProject.souName': {
        type: 'string',
        title: i18nExpression('bidMod.inquiryTitle'),
        'x-decorator': 'FormItem'
      },
      // 评分规则
      'souProject.scoreRuleType': {
        type: 'string',
        title: i18nExpression('bidMod.inquiryRule'),
        'x-decorator': 'FormItem',
        'x-component': 'DictSelect',
        'x-component-props': {
          code: 'SOU_SCORE_RULE_TYPE'
        }
      },
      // 报价方式
      'souProject.orderWay': {
        type: 'string',
        title: i18nExpression('bidMod.quoteRule'),
        'x-decorator': 'FormItem',
        'x-component': 'DictSelect',
        'x-component-props': {
          code: 'SOU_ORDER_WAY'
        }
      },
      // 预计报价开始时间
      'souProject.orderStartTime': {
        type: 'string',
        title: i18nExpression('bidMod.beginQuote'),
        'x-decorator': 'FormItem'
      },
      // 报价结束时间
      'souProject.orderEndTime': {
        type: 'string',
        title: i18nExpression('bidMod.deadline'),
        'x-decorator': 'FormItem'
      },
      // 当前轮次
      'souProject.currentRound': {
        type: 'string',
        title: i18nExpression('bidMod.currentRound'),
        'x-decorator': 'FormItem'
      },
      // 备注
      'souProject.remark': {
        type: 'string',
        title: i18nExpression('bidMod.remark'),
        'x-decorator': 'FormItem',
        'x-decorator-props': {
          gridSpan: 3
        },
        'x-component': 'Input.TextArea',
        'x-component-props': {
          maxlength: '500',
          showWordLimit: true,
          rows: 2
        }
      }
    }
  }
}

export default BaseInfoSegment
