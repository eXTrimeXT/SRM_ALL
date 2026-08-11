import {
  expression,
  i18nExpression
} from '@meicloud/render-engine'
import {
  requiredValidatorSegment,
  formGridSegment,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

export default {
  type: 'void',
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: i18nExpression('outsource.returnReqBaseInfo') // '委外退料单基础信息'
  },
  'x-read-pretty': expression('$form.readPretty'),
  properties: {
    baseInfo: {
      type: 'void',
      ...formGridSegment,
      properties: {
        returnId: {
          type: 'string',
          'x-hidden': true
        },
        // 委外退料单号
        returnNum: {
          type: 'string',
          title: i18nExpression('outsource.returnNum'),
          'x-decorator': 'FormItem',
          'x-component-props': {
            disabled: true
          }
        },
        // 退料单状态
        status: {
          type: 'string',
          title: i18nExpression('outsource.outsourceReturnStatus'),
          'x-decorator': 'FormItem',
          'x-component': 'DictSelect',
          'x-component-props': {
            'disabled': true,
            code: 'OS_MATERIAL_RETURN_ORDER_STATUS'
          }
        },
        // 创建人
        createdFullName: {
          type: 'string',
          title: i18nExpression('common.createdFullName'),
          'x-decorator': 'FormItem',
          'x-component-props': {
            disabled: true
          }
        },
        // 创建时间
        creationDate: {
          ...yearMonthDaySelectorSegment,
          title: i18nExpression('common.creationDate'),
          'x-decorator': 'FormItem',
          'x-component-props': {
            ...yearMonthDaySelectorSegment['x-component-props'],
            disabled: true
          }
        },
        // 业务实体
        orgId: {
          type: 'string',
          title: i18nExpression('dataConfMod.orgId'),
          'x-decorator': 'FormItem',
          'x-component': 'OrganizationSelector',
          'x-component-props': {
            readPretty: '{{$form.readPretty}}',
            'parent-id': -1,
            'node-type': 'OU',
            disabled: expression('$form.readPretty ? undefined : ![undefined, "", "DRAFT"].includes($form.values.status) || ($form.values.detailList?.length > 0)'), // expression('$formEditFlag($form)')
            '@select': expression(`(node) => {
              $values.orgId = node ? String(node.organizationId) : null
              $values.orgCode = node ? String(node.organizationCode) : null
              $values.orgName = node ? node.organizationName : null

              if($form.values.organizationId){
                $form.values.organizationId = null
                $form.values.organizationCode = null
                $form.values.organizationName = null
                $form.values.detailList = []
              }
            }`)
          },
          ...requiredValidatorSegment
        },
        // 库存组织
        organizationId: {
          type: 'string',
          title: i18nExpression('dataConfMod.organizationId'),
          'x-decorator': 'FormItem',
          'x-component': 'OrganizationSelector',
          'x-component-props': {
            readPretty: '{{$form.readPretty}}',
            'parent-id': '{{$values.orgId}}',
            'node-type': 'INV',
            disabled: expression('$form.readPretty ? undefined : ![undefined, "", "DRAFT"].includes($form.values.status) || ($form.values.detailList?.length > 0)'), // expression('$formEditFlag($form)')
            '@select': expression(`(node) => {
              $values.organizationId = node ? String(node.organizationId) : null
              $values.organizationCode = node ? String(node.organizationCode) : null
              $values.organizationName = node ? node.organizationName : null
              $form.values.detailList = []
            }`)
          },
          ...requiredValidatorSegment
        },
        // 供应商
        vendorName: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('common.vendor'),
          default: expression('app.$store.getters.userInfo.companyName'),
          'x-component-props': {
            disabled: true
          }
        },
        vendorCode: {
          type: 'string',
          default: expression('app.$store.getters.userInfo.companyCode'),
          'x-hidden': true
        },
        vendorId: {
          type: 'string',
          default: expression('app.$store.getters.userInfo.companyId'),
          'x-hidden': true
        },
        // 备注
        comments: {
          type: 'string',
          'x-decorator': 'FormItem',
          'x-decorator-props': { gridSpan: 4 },
          title: i18nExpression('contractMod.remark'),
          'x-component-props': {
            type: 'textarea',
            maxlength: '500',
            showWordLimit: true,
            disabled: expression('$formEditFlag($form)'),
            autosize: { minRows: 2, maxRows: 5 }
          }
        }
      }
    }
  }
}
