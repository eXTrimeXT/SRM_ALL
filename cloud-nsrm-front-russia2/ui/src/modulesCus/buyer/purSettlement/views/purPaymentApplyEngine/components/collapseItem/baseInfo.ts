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
    title: i18nExpression('contractMod.paymentApplyDetail')
  },
  'x-query-engine-skip': true,
  'x-read-pretty': expression('$form.readPretty'),
  properties: {
    layout: {
      type: 'void',
      ...formGridSegment,
      properties: {
        paymentApplyNumber: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('contractMod.paymentApplyNumber'), // 付款申请单号
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        status: {
          type: 'string',
          default: 'DRAFT',
          title: i18nExpression('purSettlementMod.paymentPlanStatus'), // 单据状态
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'PAYMENT_APPLY_STATUS',
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
        orgId: {
          type: 'number',
          'x-decorator': 'FormItem',
          title: i18nExpression('oneStopShopping.businessEntity'), // 业务实体
          'x-component': 'OrganizationSelector',
          'x-component-props': {
            readPretty: '{{$form.readPretty}}',
            disabled: expression('$form.readPretty ? undefined : (!!$form.values.invoices?.length || !!$form.values.contracts?.length)'),
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
            'parent-id': expression('$form.values.orgId || -1'),
            disabled: expression('$form.readPretty ? undefined : (!!$form.values.invoices?.length || !!$form.values.contracts?.length)'),
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
          title: i18nExpression('common.vendor'), // 供应商
          'x-decorator': 'FormItem',
          'x-component': 'QuickSearchWrapper',
          'x-component-props': {
            readPretty: '{{$form.readPretty}}',
            disabled: expression('$form.readPretty ? undefined : (!!$form.values.invoices?.length || !!$form.values.contracts?.length)'),
            showKey: 'companyName',
            propKey: 'companyName',
            'name': 'scc_sup_company_info_all',
            '@close-quicksearch': expression(`(val, scope) => {
                $setVendor($form, val)
             }`)
          },
          ...requiredValidatorSegment
        },
        currencyName: {
          type: 'string',
          title: i18nExpression('quota.currency'), // 币种
          'x-decorator': 'FormItem',
          'x-component': 'QuickSearchWrapper',
          'x-component-props': {
            readPretty: '{{$form.readPretty}}',
            disabled: expression('$form.readPretty ? undefined : (!!$form.values.invoices?.length || !!$form.values.contracts?.length)'),
            showKey: 'currencyName',
            propKey: 'currencyName',
            'name': 'scc_base_purchase_currency_info',
            '@close-quicksearch': expression(`(val, scope) => {
                                $form.values.currencyId = val ? val.currencyId : ''
                                $form.values.currencyCode = val ? val.currencyCode : ''
                                $form.values.currencyName = val ? val.currencyName : ''
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
            disabled: expression('$form.readPretty ? undefined : (!!$form.values.invoices?.length || !!$form.values.contracts?.length)'),
            code: 'tax',
            '@change-value': expression(`(_value, dictItem) => {
              $form.values.taxRate = dictItem.key
              $form.values.taxKey = _value
            }`)
          },
          ...requiredValidatorSegment
        },
        payMethod: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('paymentType.paymentWay'), // 付款方式
          'x-component': 'DictSelect',
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : (!!$form.values.invoices?.length || !!$form.values.contracts?.length)'),
            code: 'PAYMENT_MODE'
          },
          ...requiredValidatorSegment
        },
        billType: {
          type: 'string',
          'x-decorator': 'FormItem',
          default: 'ORDER',
          title: i18nExpression('advancePayment.billType'), // 单据来源
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'BILLTYPE'
          }
        },
        bankName: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('components.bank.bankName'), // 银行名称
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        openingBank: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('components.bank.branchBankName'), // 分行名称
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        bankAccountName: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('components.bank.accountName'), // 账户名称
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        bankAccount: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('components.bank.bankAccount'), // 银行账号
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        actualInvoiceAmountY: {
          type: 'number',
          'x-decorator': 'FormItem',
          title: i18nExpression('purSettlementMod.actualInvoiceAmountY2'), // 发票含税总金额
          'x-component-props': {
            precision: 8,
            controls: false,
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        includeTaxAmount: {
          type: 'number',
          'x-decorator': 'FormItem',
          title: i18nExpression('purSettlementMod.includeTaxAmount3'), // 付款含税总金额
          'x-component-props': {
            precision: 8,
            controls: false,
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        excludeTaxAmount: {
          type: 'number',
          'x-decorator': 'FormItem',
          title: i18nExpression('purSettlementMod.excludeTaxAmount'), // 付款未税总金额
          'x-component-props': {
            precision: 8,
            controls: false,
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        totalTax: {
          type: 'number',
          'x-decorator': 'FormItem',
          title: i18nExpression('purSettlementMod.totalTax2'), // 付款总税额
          'x-component-props': {
            precision: 8,
            controls: false,
            disabled: expression('$form.readPretty ? undefined : true')
          }
        }
      }
    }
  }
}
