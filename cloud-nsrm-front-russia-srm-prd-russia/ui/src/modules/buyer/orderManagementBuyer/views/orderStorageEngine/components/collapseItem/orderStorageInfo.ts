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
    title: i18nExpression('supRisk.baseInfo') // 基础信息
  },
  'x-query-engine-skip': true,
  'x-read-pretty': expression('$form.readPretty'),
  properties: {
    layout: {
      type: 'void',
      ...formGridSegment,
      properties: {
        warehouseReceiptNumber: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('orderMod.warehouseReceiptNo'), // 入库单据号
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        vendorName: {
          type: 'string',
          title: i18nExpression('common.vendor'), // 供应商
          'x-decorator': 'FormItem',
          'x-component': 'QuickSearchWrapper',
          'x-component-props': {
            readPretty: '{{$form.readPretty}}',
            disabled: expression('$form.readPretty ? undefined : !!$form.values.detailList?.length'),
            showKey: 'companyName',
            propKey: 'companyName',
            'name': 'scc_sup_company_info_all',
            '@close-quicksearch': expression(`(val, scope) => {
              $form.values.vendorId = val ? val.companyId : ''
              $form.values.vendorCode = val ? val.companyCode : ''
              $form.values.vendorName = val ? val.companyName : ''
            }`)
          },
          ...requiredValidatorSegment
        },
        vendorCode: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('orderMod.buyerOrderSynergy.vendorCode'), // 供应商编码
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        orgId: {
          type: 'number',
          'x-decorator': 'FormItem',
          title: i18nExpression('oneStopShopping.businessEntity'), // 业务实体
          'x-component': 'OrganizationSelector',
          'x-component-props': {
            readPretty: '{{$form.readPretty}}',
            'parent-id': -1,
            'node-type': 'OU',
            'select-type': 'input',
            placeholder: i18nExpression('common.pleaseSelect'),
            multiple: false,
            disabled: expression('$form.readPretty ? undefined : !!$form.values.detailList?.length'),
            '@select': expression(`(node) => {
                $form.values.orgId = node ? node.organizationId : null
                $form.values.orgCode = node ? node.organizationCode : null
                $form.values.orgName = node ? node.organizationName : null
                $form.values.receiveAddress = null
                if($form.values.organizationId){
                  $form.values.organizationId = null
                  $form.values.organizationCode = null
                  $form.values.organizationName = null
                }
              }`)
          },
          ...requiredValidatorSegment
        },
        organizationId: {
          type: 'number',
          'x-decorator': 'FormItem',
          title: i18nExpression('dataConfMod.organizationId'), // 库存组织
          'x-component': 'OrganizationSelector',
          'x-component-props': {
            readPretty: '{{$form.readPretty}}',
            'node-type': 'INV',
            'select-type': 'input',
            placeholder: i18nExpression('common.pleaseSelect'),
            multiple: false,
            'parent-id': expression('$form.values.orgId || -1'),
            disabled: expression('$form.readPretty ? undefined : !!$form.values.detailList?.length'),
            '@select': expression(`(node) => {
                $form.values.organizationId = node ? node.organizationId : null
                $form.values.organizationCode = node ? node.organizationCode : null
                $form.values.organizationName = node ? node.organizationName : null
                $form.values.receiveAddress = null
              }`)
          },
          ...requiredValidatorSegment
        },
        receiveAddressName: {
          type: 'string',
          'x-hidden': expression('!$form.readPretty'),
          'x-decorator': 'FormItem',
          title: i18nExpression('oneStopShopping.receiveAddress') // 收货地址
        },
        receiveAddress: {
          type: 'string',
          'x-hidden': expression('$form.readPretty'),
          'x-decorator': 'FormItem',
          title: i18nExpression('oneStopShopping.receiveAddress'), // 收货地址
          'x-component': 'DictSelect',
          'x-component-props': {
            code: expression('$form.values.organizationId+\'\' || \'\''),
            'custom-select-type': expression('$form.values.organizationId ? \'RECEIVE_ADDRESS\' : \'\''),
            '@change-value': expression(`(val, {element}) =>{
                $form.values.receiveContact = element.receiver
                 $form.values.receiveTelephone = element.receiverPhone
                 $form.values.receiveAddress = element.siteName
              }`)
          },
          ...requiredValidatorSegment
        },
        warehouseDate: {
          'x-decorator': 'FormItem',
          title: i18nExpression('orderMod.warehouseDate'), // 入库日期
          ...yearMonthDaySelectorSegment,
          ...requiredValidatorSegment,
          default: '{{parseTime(new Date(), \'{y}-{m}-{d}\')}}'
        },
        warehouseReceiptStatus: {
          type: 'string',
          'x-hidden': '{{$form.isAdd}}',
          title: i18nExpression('common.status'), // 状态
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'WAREHOUSE_RECEIPT_STATUS',
            disabled: expression('$form.readPretty ? undefined : true')
          },
          'x-decorator': 'FormItem'
        },
        createdFullName: {
          type: 'string',
          'x-hidden': '{{$form.isAdd}}',
          'x-decorator': 'FormItem',
          title: i18nExpression('common.creator'), // 创建人
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        creationDate: {
          'x-hidden': '{{$form.isAdd}}',
          'x-decorator': 'FormItem',
          title: i18nExpression('common.creationTime'), // 创建时间,
          ...yearMonthDaySelectorSegment,
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        lastUpdatedFullName: {
          type: 'string',
          'x-hidden': '{{$form.isAdd}}',
          'x-decorator': 'FormItem',
          title: i18nExpression('orderMod.buyerOrderSynergy.lastUpdateBy'), // 最后更新人
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        lastUpdateDate: {
          'x-hidden': '{{$form.isAdd}}',
          'x-decorator': 'FormItem',
          title: i18nExpression('dataConfMod.lastUpdateDate'), // 最后更新时间
          ...yearMonthDaySelectorSegment,
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')

          }
        },
        comments: {
          type: 'string',
          'x-decorator': 'FormItem',
          'x-decorator-props': {
            gridSpan: 4
          },
          title: i18nExpression('common.remark'), // 备注
          'x-component-props': {
            type: 'textarea',
            rows: 2
          }
        }
      }
    }
  }
}
