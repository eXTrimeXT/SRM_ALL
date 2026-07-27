import {
  expression,
  i18nExpression,
  generateXindexInOrder
} from '@meicloud/render-engine'
import {
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

export default {
  type: 'void',
  title: i18nExpression('purSettlementMod.detailSelect'),
  'x-decorator': 'QueryEngine',
  'x-component': 'RDialog',
  'x-component-props': {
    size: 'large',
    'close-on-click-modal': false,
    footer: true,
    beforeClose: expression(`(done, type) => {
            if ( type === 'ok') {
              $setAdvancePaymentDetailData ($form, $message)  
            }
            done()
      }`)
  },
  properties: {
    formWrapper: {
      type: 'void',
      'x-component': 'RFormWrapper',
      'x-component-props': {
        formLabelWidth: '120px',
        colLength: 2,
        'form-array': expression('$form.query(\'AdvanceApply\').get(\'data\').advancePaymentDialogQueryForm'),
        '@getFormData': expression(`(obj) => {
            $getAdvancePaymentQuerydata(obj,$form)
          }`)
      }
    },
    advancePaymentTable: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        preColumns: 'checkbox,seq',
        pagination: false,
        sortable: false
      },
      'x-query-engine-skip': true,
      properties: generateXindexInOrder({
        contractName: {
          type: 'string',
          title: i18nExpression('vendorMod.contractName'), // 合同名称
          'x-render-table-column': {
            minWidth: 100
          }
        },
        milestoneType: {
          type: 'string',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'MILESTONE_SCHEDULE'
          },
          'x-render-table-column': {
            title: i18nExpression('contract_mod.processNodeName'), // 里程碑名称
            minWidth: 100
          }
        },
        paymentStage: {
          type: 'string',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'PAYMENT_STAGE'
          },
          'x-render-table-column': {
            title: i18nExpression('bidMod.payStage'), // 付款阶段
            minWidth: 100
          }
        },
        invoiceNo: {
          type: 'string',
          title: i18nExpression('contract_mod.processNum2'), // 合同履约开票单号
          'x-render-table-column': {
            minWidth: 100
          }
        },
        vendorName: {
          type: 'string',
          title: i18nExpression('common.vendorName'), // 供应商名称
          'x-render-table-column': {
            minWidth: 100
          }
        },
        vendorCode: {
          type: 'string',
          title: i18nExpression('common.vendorCode'), // 供应商编码
          'x-render-table-column': {
            minWidth: 100
          }
        },
        invName: {
          type: 'string',
          title: i18nExpression('vendorMod.organization'), // 组织
          'x-render-table-column': {
            minWidth: 100
          }
        },
        materialName: {
          type: 'string',
          title: i18nExpression('vendorMod.materialName'), // 物料名称
          'x-render-table-column': {
            minWidth: 100
          }
        },
        payableTaxedAmount: {
          type: 'string',
          title: i18nExpression('contract_mod.payableTax'), // 应付含税金额
          'x-render-table-column': {
            minWidth: 100
          }
        },
        invoicedTaxedAmount: {
          type: 'string',
          title: i18nExpression('contract_mod.payableTax2'), // 开票含税金额
          'x-render-table-column': {
            minWidth: 100
          }
        },
        stayPaymentAmount: {
          type: 'string',
          title: i18nExpression('contract_mod.paymentAmount'), // 待付款金额
          'x-render-table-column': {
            minWidth: 100
          }
        },
        alreadyPaymentAmount: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('contract_mod.amountPaid'), // 已付款金额
            minWidth: 100
          }
        },
        createdFullName: {
          type: 'string',
          title: i18nExpression('sourcingBuyer.createdFullName'), // 创建人
          'x-render-table-column': {
            minWidth: 100
          }
        },
        creationDate: {
          title: i18nExpression('sourcingBuyer.creationDate'), // 创建时间
          'x-render-table-column': {
            minWidth: 100
          },
          ...yearMonthDaySelectorSegment
        }
      })
    },
    pagination: {
      type: 'void',
      'x-component': 'CPagination',
      'x-component-props': {
        pageNum: expression('$form.query(\'AdvanceApply\').get(\'data\').advancePaymentDialogPageNum'),
        pageSize: expression('$form.query(\'AdvanceApply\').get(\'data\').advancePaymentDialogPageSize'),
        total: expression('$form.query(\'AdvanceApply\').get(\'data\').advancePaymentDialogTotal'),
        pageSizes: [5, 15, 30, 60, 120, 300, 600, 1000, 1500],
        '@current-change': expression(`(num) => {
            $form.query('AdvanceApply').get('data').advancePaymentDialogPageNum = num
            $getAdvancePaymentDialogData($form)
          }`),
        '@size-change': expression(`(size) => {
            $form.query('AdvanceApply').get('data').advancePaymentDialogPageSize = size
            $getAdvancePaymentDialogData($form)
          }`)
      }
    }

  }
}
