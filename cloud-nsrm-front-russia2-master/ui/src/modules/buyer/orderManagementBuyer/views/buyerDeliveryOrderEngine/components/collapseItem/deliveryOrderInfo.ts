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
    title: i18nExpression('orderMod.buyerOrderSynergy.appointDeliveryFormList')
  },
  'x-query-engine-skip': true,
  'x-read-pretty': expression('$form.readPretty'),
  properties: {
    layout: {
      type: 'void',
      ...formGridSegment,
      properties: {
        deliveryNumber: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('orderMod.buyerOrderSynergy.deliveryNumber'), // 送货单号
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        deliveryNoteStatus: {
          type: 'string',
          default: 'CREATE',
          title: i18nExpression('bidMod.billstatus'), // 单据状态
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'DELIVERY_NOTE_STATUS',
            disabled: expression('$form.readPretty ? undefined : true')
          },
          'x-decorator': 'FormItem'
        },
        deliveryDate: {
          'x-decorator': 'FormItem',
          title: i18nExpression('orderMod.buyerOrderSynergy.deliveryDate2'), // 送货日期
          ...yearMonthDaySelectorSegment,
          ...requiredValidatorSegment
        },
        vendorId: {
          'x-hidden': true,
          default: expression('app.$store.getters.userInfo.companyId')

        },
        vendorCode: {
          type: 'string',
          'x-decorator': 'FormItem',
          default: expression('app.$store.getters.userInfo.companyCode'),
          title: i18nExpression('orderMod.buyerOrderSynergy.vendorCode'), // 供应商编码
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        vendorName: {
          type: 'string',
          'x-decorator': 'FormItem',
          default: expression('app.$store.getters.userInfo.companyName'),
          title: i18nExpression('orderMod.buyerOrderSynergy.vendorName'), // 供应商名称
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
              $form.values.receivedFactory = null
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
              $form.values.receivedFactory = null
            }`)
          },
          ...requiredValidatorSegment
        },
        receivedFactory: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('oneStopShopping.receiveAddress'), // 收货地址
          'x-component': 'DictSelect',
          'x-component-props': {
            code: expression('$form.values.organizationId+\'\' || \'\''),
            disabled: expression('$form.readPretty ? undefined : !!$form.values.detailList?.length'),
            'custom-select-type': expression('$form.values.organizationId ? \'RECEIVE_ADDRESS\' : \'\''),
            '@change-value': expression(`(val, {element}) =>{
              $form.values.contactPeople = element.receiver
               $form.values.contactNumber = element.receiverPhone
               $form.values.receivedFactory = element.siteName
            }`)
          },
          ...requiredValidatorSegment
        },
        orderSource: {
          type: 'string',
          title: i18nExpression('buyerDeliveryOrder.orderSource'), // 送货单来源
          enum: expression('$orderSourceList.value.map(item => ({  ...item }))'),
          'x-component': 'Select',
          'x-decorator': 'FormItem',
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : !!$form.values.detailList?.length')
          },
          ...requiredValidatorSegment
        },
        comments: {
          type: 'string',
          'x-decorator': 'FormItem',
          'x-decorator-props': {
            gridSpan: 4
          },
          title: i18nExpression('orderMod.buyerOrderSynergy.comments'), // 备注
          'x-component-props': {
            type: 'textarea',
            'rows': '2'
          }
        }
      }
    }
  }
}
