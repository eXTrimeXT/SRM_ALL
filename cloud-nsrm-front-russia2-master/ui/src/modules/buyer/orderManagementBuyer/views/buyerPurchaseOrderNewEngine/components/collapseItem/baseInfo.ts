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
    title: i18nExpression('supRisk.baseInfo')
  },
  'x-query-engine-skip': true,
  'x-read-pretty': expression('$form.readPretty'),
  properties: {
    layout: {
      type: 'void',
      ...formGridSegment,
      properties: {
        //  是否手动创建
        isManual: {
          type: 'string',
          default: 'Y',
          'x-hidden': true
        },
        orderNumber: {
          type: 'string',
          title: i18nExpression('orderMod.buyerOrderSynergy.orderNumber'), // 采购订单编号
          'x-decorator': 'FormItem',
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        orderStatus: {
          type: 'string',
          default: 'DRAFT',
          'x-decorator': 'FormItem',
          title: i18nExpression('orderMod.buyerOrderSynergy.orderStatus'), // 订单状态
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'PURCHASE_ORDER',
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        storageStatus: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('orderMod.buyerOrderSynergy.warehouseReceiptStatus'), // 入库状态
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'STORAGE_STATUS',
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        orderChangeVersion: {
          type: 'string',
          default: 0,
          'x-decorator': 'FormItem',
          title: i18nExpression('dataConfMod.version'), // 版本号
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        ceeaEmpUsername: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('orderMod.buyerOrderSynergy.buyerName'), // 采购员
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        ceeaDepartmentName: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('oneStopShopping.department'), // 采购部门
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        demandType: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('purchaseDemand.demandType'), // 需求类型
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'DEMAND_TYPE',
            disabled: expression('$form.readPretty ? undefined : (!!$form.values.detailList?.length || $form.values.isManual !== \'Y\')')
          }
        },
        orderType: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('purchaseDemand.purchaseType'), // 采购类型
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'PURCHASE_TYPE',
            disabled: expression('$form.readPretty ? undefined : !!$form.values.detailList?.length')
          },
          ...requiredValidatorSegment
        },
        ifSample: {
          type: 'string',
          default: 'N',
          'x-decorator': 'FormItem',
          title: i18nExpression('bidMod.ifSampleSmallOrder'), // 是否样品小批量订单
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'YES_OR_NO',
            disabled: expression('$form.readPretty ? undefined : (!!$form.values.detailList?.length || $form.values.isManual !== \'Y\')')
          }
        },
        ceeaIfSupplierConfirm: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('oneStopShopping.ifSupplierConfirm'), // 是否供方确认
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'YES_OR_NO'
          },
          ...requiredValidatorSegment
        },
        ceeaPurchaseOrderDate: {
          'x-decorator': 'FormItem',
          title: i18nExpression('oneStopShopping.orderDate'), // 订单日期,
          ...yearMonthDaySelectorSegment,
          default: '{{parseTime(new Date(), \'{y}-{m}-{d}\', true)}}'
        },
        ceeaOrgId: {
          type: 'number',
          'x-decorator': 'FormItem',
          title: i18nExpression('oneStopShopping.businessEntity'), // 业务实体
          'x-component': 'OrganizationSelector',
          'x-component-props': {
            readPretty: '{{$form.readPretty}}',
            'parent-id': -1,
            disabled: expression('$form.readPretty ? undefined : !!$form.values.detailList?.length'),
            'node-type': 'OU',
            'select-type': 'input',
            placeholder: i18nExpression('common.pleaseSelect'),
            '@select': expression(`(node) => {
                $form.values.ceeaOrgId = node ? node.organizationId : null
                $form.values.ceeaOrgCode = node ? node.organizationCode : null
                $form.values.ceeaOrgName = node ? node.organizationName : null
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
            'parent-id': expression('$form.values.ceeaOrgId || -1'),
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
          title: i18nExpression('oneStopShopping.receiveAddress'), // 收货地址
          ...requiredValidatorSegment
        },
        receiveAddress: {
          type: 'string',
          'x-hidden': expression('$form.readPretty'),
          'x-decorator': 'FormItem',
          title: i18nExpression('oneStopShopping.receiveAddress'), // 收货地址
          'x-component': 'DictSelect',
          'x-component-props': {
            code: expression('$form.values.organizationId+\'\' || \'\''),
            disabled: expression('$form.readPretty ? undefined : !!$form.values.detailList?.length'),
            'custom-select-type': expression('$form.values.organizationId ? \'RECEIVE_ADDRESS\' : \'\''),
            '@change-value': expression(`(val, {element}) =>{
              $form.values.receiveContact = element ? element.receiver : null
               $form.values.receiveTelephone = element ? element.receiverPhone : null
               $form.values.receiveAddress = element ? element.siteName : null
            }`)
          },
          ...requiredValidatorSegment
        },
        receiveContact: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('oneStopShopping.receiveContacts'), // 收货联系人
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : !!$form.values.detailList?.length')
          }
        },
        receiveTelephone: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('oneStopShopping.receiveTelephone'), // 收货联系电话
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : !!$form.values.detailList?.length')
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
                $setVendor($form, val)
             }`)
          },
          ...requiredValidatorSegment
        },
        ceeaSupplierContacts: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('contractMod.linkMan'), // 供应商联系人
          'x-component-props': {
            maxlength: 30,
            showWordLimit: true
          }
        },
        ceeaReceiveOrderTelephone: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('contractMod.supplierContactPhone'), // 供方联系人电话
          'x-component-props': {
            maxlength: 50,
            showWordLimit: true
          }
        },
        budgetManagementNum: {
          type: 'string',
          'x-decorator': 'FormItem',
          // 生产需求不显示，非生产需求显示
          'x-hidden': `{{
            $values.demandType !== 'NONPRODUCTIVE_DEMAND'
          }}`,
          title: i18nExpression('purchaseDemand.budgetNumber'), // 预算编号
          'x-component': 'QuickSearchWrapper',
          'x-component-props': {
            readPretty: '{{$form.readPretty}}',
            disabled: expression('$form.readPretty ? undefined : !!$form.values.detailList?.length'),
            showKey: 'budgetManagementNumber',
            propKey: 'budgetManagementNumber',
            autoQuery: true,
            name: 'scc_pb_budget_management_effective',
            '@close-quicksearch': expression(`(val) => {
              $form.values.budgetManagementNum = val ? val.budgetManagementNumber : ''
              $form.values.budgetManagementId = val ? val.budgetManagementId : ''
            }`)
          },
          ...requiredValidatorSegment
        },
        ceeaTotalNum: {
          type: 'number',
          default: 0,
          'x-hidden': true

        },
        ceeaTaxAmount: {
          type: 'number',
          default: 0,
          'x-decorator': 'FormItem',
          title: i18nExpression('oneStopShopping.totalAmountIncludingTax'), // 合计金额含税
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        ceeaNoTaxAmount: {
          type: 'number',
          default: 0,
          'x-decorator': 'FormItem',
          title: i18nExpression('oneStopShopping.totalAmountExcludingTax'), // 合计金额不含税
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        // ceeaCostType: {
        //   type: 'string',
        //   'x-decorator': 'FormItem',
        //   title: i18nExpression('purchaseDemand.costType'), // 成本类型
        //   'x-component-props': {
        //     maxlength: 50,
        //     showWordLimit: true
        //   }
        // },
        comments: {
          type: 'string',
          'x-decorator': 'FormItem',
          'x-decorator-props': {
            gridSpan: 4
          },
          title: i18nExpression('contractMod.remark'), // 备注
          'x-component-props': {
            type: 'textarea',
            maxlength: 80,
            autosize: { minRows: 2, maxRows: 4 },
            showWordLimit: true
          }
        }
      }
    }
  }
}
