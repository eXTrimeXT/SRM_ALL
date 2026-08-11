import {
  expression,
  i18nExpression
} from '@meicloud/render-engine'

import {
  requiredValidatorSegment,
  formGridSegment,
  yearMonthDayHourMinuteSecondSelectorSegment
} from 'lib@/components/render-engine/schema-segments'

export default {
  type: 'void',
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: i18nExpression('perfMod.documentsInformation') // 单据信息
  },
  'x-query-engine-skip': true,
  'x-read-pretty': expression('$form.readPretty'),
  properties: {
    layout: {
      type: 'void',
      'x-decorator': 'FormLayout',
      'x-decorator-props': {
        layout: 'vertical'
      },
      ...formGridSegment,
      properties: {
        // 开票单号
        onlineInvoiceNum: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('purSettlementMod.billingNumber'),
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        // 单据状态
        invoiceStatus: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('bidMod.billstatus'),
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'INVOICE_STATUS',
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        // 创建日期
        creationDate: {
          'x-decorator': 'FormItem',
          title: i18nExpression('purSettlementMod.creationDate'),
          'x-disabled': expression('$form.readPretty ? undefined : true'),
          ...yearMonthDayHourMinuteSecondSelectorSegment
        },
        // 创建人
        createdFullName: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('common.creator'),
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
            '@select': expression(`(node) => {
                  $form.values.orgId = node ? node.organizationId : null
                  $form.values.orgCode = node ? node.organizationCode : null
                  $form.values.orgName = node ? node.organizationName : null
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
            '@select': expression(`(node) => {
                  $form.values.organizationId = node ? node.organizationId : null
                  $form.values.organizationCode = node ? node.organizationCode : null
                  $form.values.organizationName = node ? node.organizationName : null
                }`)
          },
          ...requiredValidatorSegment
        },
        vendorName: {
          type: 'string',
          'x-decorator': 'FormItem',
          default: expression('app.$store.getters.userInfo.companyName'),
          title: i18nExpression('common.vendor'), // 供应商
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          },
          ...requiredValidatorSegment
        },
        vendorCode: {
          type: 'string',
          'x-decorator': 'FormItem',
          default: expression('app.$store.getters.userInfo.companyCode'),
          'x-hidden': true
        },
        vendorId: {
          type: 'string',
          'x-decorator': 'FormItem',
          default: expression('app.$store.getters.userInfo.companyId'),
          'x-hidden': true
        },
        payMethod: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('paymentType.paymentWay'), // 付款方式
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'PAYMENT_MODE'
          },
          ...requiredValidatorSegment
        },
        currencyName: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('quota.currency'), // 币种
          'x-component': 'QuickSearchWrapper',
          'x-component-props': {
            readPretty: '{{$form.readPretty}}',
            showKey: 'currencyName',
            propKey: 'currencyName',
            'name': 'scc_base_purchase_currency_info',
            '@close-quicksearch': expression(`(value) => {
                $form.values.currencyId = value ? value.currencyId : null
                $form.values.currencyCode = value ? value.currencyCode : null
                $form.values.currencyName = value ? value.currencyName : null
              }`)
          },
          ...requiredValidatorSegment
        },
        taxKey: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('purchaseDemand.taxRate'), // 税率
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'tax',
            '@change-value': expression(`(val, dictItem) =>{
                $self.value = val 
                $form.values.taxRate = dictItem ? dictItem.key: ''
              }`)
          },
          ...requiredValidatorSegment
        },
        payAccountPeriodCode: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('paymentType.paymentDay1'), // 付款账期
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'PAYMENT_PERIOD',
            '@change-value': expression(`(val, dictItem) => {
                $form.values.payAccountPeriodCode = val ? val: null
                $form.values.payAccountPeriodName = dictItem ? dictItem.description : null
              }`)
          },
          ...requiredValidatorSegment
        },
        // 系统含税总金额 = 对账单明细含税金额之和-考核单含税金额之和
        taxTotalAmount: {
          type: 'number',
          'x-decorator': 'FormItem',
          'x-decorator-props': {
            tooltip: i18nExpression('purSettlementMod.systemTotalAmountTaxCal'),
            tooltipLayout: 'icon'
          },
          title: i18nExpression('purSettlementMod.systemTotalAmountTax'),
          'x-component-props': {
            precision: 8,
            controls: false,
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        // 系统未税总金额 = 对账单明细未税金额之和 - 考核单未税金额之和
        excluTaxTotalAmount: {
          type: 'number',
          'x-decorator': 'FormItem',
          'x-decorator-props': {
            tooltip: i18nExpression('purSettlementMod.totalAmountNotTaxedCal'),
            tooltipLayout: 'icon'
          },
          title: i18nExpression('purSettlementMod.totalAmountNotTaxed'),
          'x-component-props': {
            precision: 8,
            controls: false,
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        // 系统总税额
        totalTax: {
          type: 'number',
          'x-decorator': 'FormItem',
          title: i18nExpression('purSettlementMod.totalSystemTax'),
          'x-disabled': expression('$form.readPretty ? undefined : true'),
          'x-component-props': {
            precision: 8,
            controls: false,
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        // 发票含税总金额 = 发票明细含税金额之和
        actualInvoiceAmountY: {
          type: 'number',
          'x-decorator': 'FormItem',
          'x-decorator-props': {
            tooltip: i18nExpression('purSettlementMod.totalAmountInvoiceTaxCal'),
            tooltipLayout: 'icon'
          },
          title: i18nExpression('purSettlementMod.totalAmountInvoiceTax'),
          'x-disabled': expression('$form.readPretty ? undefined : true'),
          'x-component-props': {
            precision: 8,
            controls: false
          }
        },
        // 发票未税总金额=发票明细未税总金额之和
        actualInvoiceAmountN: {
          type: 'number',
          'x-decorator': 'FormItem',
          'x-decorator-props': {
            tooltip: i18nExpression('purSettlementMod.totalAmountInvoiceTaxCal'),
            tooltipLayout: 'icon'
          },
          title: i18nExpression('purSettlementMod.totalAmountInvoiceNoTax'),
          'x-disabled': expression('$form.readPretty ? undefined : true'),
          'x-component-props': {
            precision: 8,
            controls: false
          }
        },
        // 发票总税额
        invoiceTax: {
          type: 'number',
          'x-decorator': 'FormItem',
          title: i18nExpression('purSettlementMod.invoiceTotalTax'),
          'x-disabled': expression('$form.readPretty ? undefined : true'),
          'x-component-props': {
            precision: 8,
            controls: false
          }
        },
        // 已付款金额=当前开票单预付款的本次核销金额（含税）+当前开票单付款申请的已付款金额
        paidAmount: {
          type: 'number',
          'x-decorator': 'FormItem',
          'x-decorator-props': {
            tooltip: i18nExpression('purSettlementMod.paidAmountCal'),
            tooltipLayout: 'icon'
          },
          title: i18nExpression('purSettlementMod.paidAmount'),
          'x-disabled': expression('$form.readPretty ? undefined : true'),
          'x-component-props': {
            precision: 8,
            controls: false
          }
        },
        // 未付款金额=当前开票单的系统含税总金额 - 已付款金额
        unPaidAmount: {
          type: 'number',
          'x-decorator': 'FormItem',
          'x-decorator-props': {
            tooltip: i18nExpression('purSettlementMod.unpaidAmountCal'),
            tooltipLayout: 'icon'
          },
          title: i18nExpression('purSettlementMod.unPaidAmount'),
          'x-disabled': expression('$form.readPretty ? undefined : true'),
          'x-component-props': {
            precision: 8,
            controls: false
          }
        }

      }
    }
  }
}
