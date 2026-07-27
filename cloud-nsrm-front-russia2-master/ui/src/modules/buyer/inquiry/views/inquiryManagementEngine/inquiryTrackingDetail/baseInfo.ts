/**
 * @description 详细信息
 */
import { i18nExpression } from '@meicloud/render-engine'

const BaseInfoSegment: Record<any, any> = {
  requireInfoForm: {
    type: 'void',
    'x-decorator': 'FormLayout',
    'x-decorator-props': {
      colon: false,
      layout: 'horizontal',
      feedbackLayout: 'terse'
    },
    'x-component': 'FormGrid',
    'x-component-props': {
      minColumns: 1,
      maxColumns: 4,
      columnGap: 32
    },
    'x-read-pretty': true,
    properties: {
      // 询价标题
      'header.souName': {
        type: 'string',
        title: i18nExpression('bidMod.inquiryTitle'),
        'x-decorator': 'FormItem'
      },
      // 询价单号
      'header.souNo': {
        type: 'string',
        title: i18nExpression('bidMod.inquiryNo'),
        'x-decorator': 'FormItem'
      },
      // 询价单状态
      'header.extProjectStatus': {
        type: 'string',
        title: i18nExpression('bidMod.inQstatus'),
        'x-decorator': 'FormItem',
        'x-component': 'DictSelect',
        'x-component-props': {
          code: 'SOU_PROJECT_STATUS'
        }
      },
      // 审核状态
      'header.createApprovalStatus': {
        type: 'string',
        title: i18nExpression('bidMod.auditStatus'),
        'x-decorator': 'FormItem',
        'x-component': 'DictSelect',
        'x-component-props': {
          code: 'SOU_APPROVAL_STATUS'
        }
      },
      // 评分规则
      'header.scoreRuleType': {
        type: 'string',
        title: i18nExpression('bidMod.inquiryRule'),
        'x-decorator': 'FormItem',
        'x-component': 'DictSelect',
        'x-component-props': {
          code: 'SOU_SCORE_RULE_TYPE'
        }
      },
      // 报价方式
      'header.orderWay': {
        type: 'string',
        title: i18nExpression('bidMod.quoteRule'),
        'x-decorator': 'FormItem',
        'x-component': 'DictSelect',
        'x-component-props': {
          code: 'SOU_ORDER_WAY'
        }
      }
    }
  }
}

export default BaseInfoSegment
