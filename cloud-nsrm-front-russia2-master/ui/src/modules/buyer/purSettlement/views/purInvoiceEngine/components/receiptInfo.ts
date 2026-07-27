/* eslint-disable quotes */
import { expression, generateXindexInOrder } from '@meicloud/render-engine'
import { requiredValidatorSegment, yearMonthDaySelectorSegment } from 'lib@/components/render-engine/schema-segments'

const disabledFlag = {
  disabled: expression(`isViewApproval.value || !!$values.detailList?.length`)
}

export default {
  type: 'void',
  'x-query-engine-skip': true,
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: "{{$t('vendorMod.receiptInfo')}}"
  },
  properties: {
    receiptForm: {
      type: 'void',
      'x-decorator': 'FormLayout',
      'x-decorator-props': {
        layout: 'vertical'
      },
      'x-component': 'FormGrid',
      'x-component-props': {
        maxColumns: 4,
        columnGap: 32,
        rowGap: 0
      },
      properties: generateXindexInOrder({
        invoiceNoticeId: {
          type: 'number',
          'x-hidden': true
        },
        userType: {
          type: 'string',
          'x-hidden': true,
          default: 'BUYER'
        },
        invoiceNoticeNumber: {
          type: 'string',
          title: "{{$t('flowMod.documentNo')}}",
          'x-decorator': 'FormItem',
          'x-component-props': {
            disabled: true
          }
        },
        invoiceNoticeStatus: {
          type: 'string',
          title: "{{$t('purSettlementMod.paymentPlanStatus')}}",
          'x-decorator': 'FormItem',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'INVOICE_NOTICE_STATUS',
            disabled: true
          }
        },
        creationDate: {
          ...yearMonthDaySelectorSegment,
          title: "{{$t('purSettlementMod.creationDate')}}",
          'x-decorator': 'FormItem',
          'x-component': 'DatePicker',
          'x-component-props': {
            ...yearMonthDaySelectorSegment['x-component-props'],
            disabled: true
          }
        },
        createdFullName: {
          type: 'string',
          title: "{{$t('common.creator')}}",
          'x-decorator': 'FormItem',
          'x-component-props': {
            disabled: true
          }
        },
        orgId: {
          type: 'string',
          title: "{{$t('quota.org')}}",
          'x-decorator': 'FormItem',
          ...requiredValidatorSegment,
          'x-component': 'OrganizationSelector',
          'x-component-props': {
            readPretty: expression(`['approvalOnly','view'].includes($attrs.params.flag)`),
            ...disabledFlag,
            'parent-id': -1,
            'node-type': 'OU',
            '@select': expression(`(val) => {
              $values.orgId = val ? val.organizationId : null
              $values.orgCode = val ? val.organizationCode : null
              $values.orgName = val ? val.organizationName : null
              if ($values.organizationId) {
                $values.organizationId = null
                $values.organizationCode = null
                $values.organizationName = null
              }
            }`)
          }
        },
        orgCode: {
          type: 'string',
          'x-hidden': true
        },
        orgName: {
          type: 'string',
          'x-hidden': true
        },
        organizationId: {
          type: 'string',
          title: "{{$t('purchaseDemand.invOrg')}}",
          'x-decorator': 'FormItem',
          ...requiredValidatorSegment,
          'x-component': 'OrganizationSelector',
          'x-component-props': {
            readPretty: expression(`['approvalOnly','view'].includes($attrs.params.flag)`),
            ...disabledFlag,
            'parent-id': expression(`$values.orgId`),
            'node-type': 'INV',
            '@select': expression(`(val) => {
              $values.organizationId = val ? val.organizationId : null
              $values.organizationCode = val ? val.organizationCode : null
              $values.organizationName = val ? val.organizationName : null
            }`)
          }
        },
        organizationCode: {
          type: 'string',
          'x-hidden': true
        },
        organizationName: {
          type: 'string',
          'x-hidden': true
        },
        vendorName: {
          type: 'string',
          title: "{{$t('common.vendorName')}}",
          'x-decorator': 'FormItem',
          ...requiredValidatorSegment,
          'x-component': 'QuickSearchWrapper',
          'x-component-props': {
            name: 'scc_sup_company_info_all',
            ...disabledFlag,
            'read-pretty': expression(`['approvalOnly','view'].includes($attrs.params.flag)`),
            'show-key': 'companyName',
            'prop-key': 'companyName',
            '@close-quicksearch': expression(`(val) => {
              $values.vendorId = val ? val.companyId : null
              $values.vendorCode = val ? val.companyCode : null
              $values.vendorName = val ? val.companyName : null
            }`)
          }
        },
        vendorId: {
          type: 'string',
          'x-hidden': true
        },
        vendorCode: {
          type: 'string',
          'x-hidden': true
        },
        '[ceeaReceiveStartDate,ceeaReceiveEndDate]': {
          ...yearMonthDaySelectorSegment,
          title: "{{$t('accountMod.statementDate')}}",
          'x-decorator': 'FormItem',
          'x-component': 'DatePicker',
          'x-component-props': {
            ...disabledFlag,
            ...yearMonthDaySelectorSegment['x-component-props'],
            type: 'daterange',
            'value-format': 'yyyy-MM-dd',
            'range-separator': '~',
            'start-placeholder': "{{$t('dataConfMod.startDay')}}",
            'end-placeholder': "{{$t('dataConfMod.endDay')}}"
          },
          ...requiredValidatorSegment
        },
        currencyName: {
          type: 'string',
          title: "{{$t('quota.currency')}}",
          'x-decorator': 'FormItem',
          ...requiredValidatorSegment,
          'x-component': 'QuickSearchWrapper',
          'x-component-props': {
            name: 'scc_base_purchase_currency_info',
            ...disabledFlag,
            'read-pretty': expression(`['approvalOnly','view'].includes($attrs.params.flag)`),
            'show-key': 'currencyName',
            'show-input': 'currencyName',
            '@close-quicksearch': expression(`(val) => {
              $values.currencyId = val ? val.currencyId : null
              $values.currencyCode = val ? val.currencyCode : null
              $values.currencyName = val ? val.currencyName : null
            }`)
          }
        },
        currencyId: {
          type: 'string',
          'x-hidden': true
        },
        currencyCode: {
          type: 'string',
          'x-hidden': true
        },
        taxKey: {
          type: 'string',
          title: "{{$t('purchaseDemand.taxRate')}}",
          'x-decorator': 'FormItem',
          ...requiredValidatorSegment,
          'x-component': 'DictSelect',
          'x-component-props': {
            ...disabledFlag,
            code: 'tax',
            '@change-value': expression(`(val,dictItem) => {
              $values.taxRate = dictItem.key
            }`)
          }
        },
        taxRate: {
          type: 'string',
          'x-hidden': true
        },

        paymentPeriod: {
          type: 'string',
          title: "{{$t('paymentType.paymentDay')}}",
          'x-decorator': 'FormItem',
          ...requiredValidatorSegment,
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'PAYMENT_PERIOD',
            disabled: expression(`isViewApproval.value || $values.invoiceNoticeStatus === 'VENDOR_SUBMITTED' || $values.userType === 'VENDOR'`)
          }
        },

        ceeaTaxTotalAmount: {
          type: 'number',
          title: "{{$t('contractMod.totalAmountTax')}}",
          'x-decorator': 'FormItem',
          'x-decorator-props': {
            tooltip: "{{$t('purSettlementMod.amount2Tip')}}",
            tooltipLayout: 'icon'
          },
          'x-component-props': {
            precision: 8,
            controls: false,
            disabled: true
          }
        },
        ceeaNoTaxTotalAmount: {
          type: 'number',
          title: "{{$t('contractMod.totalAmountNoTax2')}}",
          'x-decorator': 'FormItem',
          'x-component-props': {
            precision: 8,
            controls: false,
            disabled: true
          }
        },
        ceeaTotalTax: {
          type: 'number',
          title: "{{$t('accountMod.totalTax')}}",
          'x-decorator': 'FormItem',
          'x-decorator-props': {
            tooltip: "{{$t('purSettlementMod.taxQuotaTip')}}",
            tooltipLayout: 'icon'
          },
          'x-component-props': {
            precision: 8,
            controls: false,
            disabled: true
          }
        }
      })
    }
  }
}
