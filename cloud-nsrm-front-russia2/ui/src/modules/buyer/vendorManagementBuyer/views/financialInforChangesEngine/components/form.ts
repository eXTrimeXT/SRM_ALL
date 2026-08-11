import {expression, generateCharExpressionByFunction, i18nExpression} from '@meicloud/render-engine'
import {
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

import {
  formGridSegment
} from 'lib@/components/render-engine/schema-segments'

export const formMain = {
  type: 'object',
  'x-query-engine-skip': true,
  ...formGridSegment,
  properties: {
    changeHeaderId: {
      type: 'number',
      'x-hidden': true,
      'x-decorator': 'FormItem'
    },
    changeHeaderName: {
      type: 'string',
      title: i18nExpression('vendorMod.relegation.billName'), // 单据名称
      'x-component-props': {},
      'x-decorator': 'FormItem',
      'x-validator': {
        required: true
      }
    },
    changeHeaderCode: {
      type: 'string',
      title: i18nExpression('dataConfMod.sequenceCode'), // 单据编码
      'x-component-props': {
        disabled: true
      },
      'x-decorator': 'FormItem'
    },
    approveStatus: {
      type: 'string',
      title: i18nExpression('dataConfMod.triggerState'), // 状态
      'x-component': 'DictSelect',
      'x-component-props': {
        code: 'APPROVE_STATUS_TYPE',
        disabled: true
      },
      'x-decorator': 'FormItem'
    },
    orgId: {
      type: 'number',
      title: i18nExpression('vendorMod.changeOrganization'), // 变更组织
      'x-decorator': 'FormItem',
      'x-component': 'OrganizationSelector',
      'x-component-props': {
        'read-pretty': expression(`$form.readPretty`),
        nodeType: 'OU'
      },
      'x-validator': {
        required: true
      }
    },
    createdFullName: {
      type: 'string',
      title: i18nExpression('common.creator'), // 创建人
      'x-component-props': {
        disabled: true
      },
      'x-decorator': 'FormItem'
    },
    department: {
      type: 'string',
      title: i18nExpression('purchaseDemand.ceeaDepartment'), // 申请部门
      'x-component-props': {},
      'x-decorator': 'FormItem'
    },
    creationDate: {
      ...yearMonthDaySelectorSegment,
      title: i18nExpression('purchaseDemand.creationDate'), // 创建时间
      'x-component-props': {
        ...yearMonthDaySelectorSegment['x-component-props'],
        disabled: true
      },
      'x-decorator': 'FormItem'
    },
    null: {
      type: 'void',
      'x-decorator': 'FormItem'
    },
    remark: {
      type: 'string',
      title: i18nExpression('components.eio.headers.remark'), // 备注
      'x-component': 'Input.TextArea',
      'x-component-props': {
        autosize: { minRows: 2, maxRows: 4 }
      },
      'x-decorator': 'FormItem',
      'x-decorator-props': {
        gridSpan: 4
      }
    },
    advice: {
      type: 'string',
      title: i18nExpression('vendorMod.advice'), // 起草人节点建议
      'x-component-props': {
        autosize: { minRows: 2, maxRows: 4 },
        type: 'textarea'
      },
      'x-decorator': 'FormItem',
      'x-decorator-props': {
        gridSpan: 4
      }
    }
  }
}
