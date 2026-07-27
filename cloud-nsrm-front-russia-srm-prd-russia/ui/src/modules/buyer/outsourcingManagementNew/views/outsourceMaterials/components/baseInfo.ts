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
    title: i18nExpression('outsource.materialReqNum') // '委外领料单基础信息'
  },
  'x-read-pretty': expression('$form.readPretty'),
  properties: {
    baseInfo: {
      type: 'void',
      ...formGridSegment,
      properties: {
        materialReqId: {
          type: 'string',
          'x-hidden': true
        },
        // 委外领料单号
        materialReqNum: {
          type: 'string',
          title: i18nExpression('outsource.materialReqNum'),
          'x-decorator': 'FormItem',
          'x-component-props': {
            disabled: true
          }
        },
        // 单据状态
        handleStatus: {
          type: 'string',
          title: i18nExpression('outsource.handleStatus'),
          'x-decorator': 'FormItem',
          'x-component': 'DictSelect',
          'x-component-props': {
            'disabled': true,
            code: 'OS_MATERIAL_REQUISITION_STATUS'
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
          type: 'string',
          title: i18nExpression('common.creationDate'),
          'x-decorator': 'FormItem',
          'x-component-props': {
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
            disabled: expression('$form.readPretty ? undefined : ![undefined, "", "CREATE"].includes($form.values.handleStatus) || ($form.values.detailList?.length > 0)'), // expression('$formEditFlag($form)')
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
        orgCode: {
          type: 'string',
          'x-hidden': true
        },
        orgName: {
          type: 'string',
          'x-hidden': true
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
            disabled: expression('$form.readPretty ? undefined : ![undefined, "", "CREATE"].includes($form.values.handleStatus) || ($form.values.detailList?.length > 0)'), // expression('$formEditFlag($form)')
            '@select': expression(`(node) => {
              $values.organizationId = node ? String(node.organizationId) : null
              $values.organizationCode = node ? String(node.organizationCode) : null
              $values.organizationName = node ? node.organizationName : null
              $form.values.detailList = []
            }`)
          },
          ...requiredValidatorSegment
        },
        organizationCode: {
          type: 'string',
          'x-hidden': true
        },
        organizationName: {
          type: 'string',
          'x-hidden': true
        },
        // 供应商
        vendorName: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('common.vendor'),
          'x-component': 'QuickSearchWrapper',
          'x-component-props': {
            readPretty: '{{$form.readPretty}}',
            showKey: 'companyName',
            propKey: 'companyName',
            disabled: expression('$form.readPretty ? undefined : ![undefined, "", "CREATE"].includes($form.values.handleStatus) || ($form.values.detailList?.length > 0)'), // expression('$formEditFlag($form)')
            name: 'scc_sup_company_info_display_buyer',
            '@close-quicksearch': expression(`(val, instance) => {
              $values.vendorId = val ? val.companyId : ''
              $values.vendorCode = val ? val.companyCode : ''
              $values.vendorName = val ? val.companyName : ''
              $form.values.detailList = []
            }`)
          },
          ...requiredValidatorSegment
        },
        vendorCode: {
          type: 'string',
          'x-hidden': true
        },
        vendorId: {
          type: 'string',
          'x-hidden': true
        },
        // 备注
        buyerSpecialRemarks: {
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
