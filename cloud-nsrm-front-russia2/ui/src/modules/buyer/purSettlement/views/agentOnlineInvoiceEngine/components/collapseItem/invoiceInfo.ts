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
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: i18nExpression('accountMod.invoiceInfo') // 发票信息
  },
  properties: {
    toolbar: {
      type: 'void',
      'x-component': 'ButtonList',
      'x-component-props': {
        class: 'list-form__toolbar'
      },
      'x-reactions': expression(`(field) => {
                field.visible = !$form.readPretty && !$form.query('OnlineInvoice').get('data').isFirstApproveShow
              }`),
      properties: {
        add: {
          type: 'void',
          'x-component': 'CInvoiceUpload',
          'x-component-props': {
            editMode: false,
            fieldSupplement: [], // 可附加补充字段显示
            fileFunction: 'BUYER_INVOICE_SETTLE',
            invoiceInformation: expression('$form.values.ocrInvoiceList'),
            '@editInvoiceRow': expression(`(row) => {
                console.log(row, 'editInvoiceRow')
                $editInvoiceRow($form, row)
              }`),
            '@saveInvoice': expression(`(fileList) => {
                  $saveInvoice($form,fileList)
              }`)
          }
        },
        delete: {
          type: 'void',
          title: i18nExpression('common.delete'),
          'x-component-props': {
            '@click': expression(`() => {
                $batchDeleteInvoice($form,$message)
              }`)
          }
        },
        uploadTip: {
          type: 'void',
          'x-component': 'span',
          'x-content': '* 发票仅支持JPG、PNG、JPEG、PDF、OFD格式，单个文件大小不超过10M', // * 发票仅支持JPG、PNG、JPEG、PDF、OFD格式，单个文件大小不超过10M
          'x-component-props': {
            style: 'color: red; front-size: 12px;'
          }
        }
      }
    },
    ocrInvoiceList: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        maxHeight: 400,
        pagination: false,
        preColumns: 'checkbox,seq',
        sortable: false,
        primaryKey: 'ocrInvoiceId',
        cascadeDeletion: true
      },
      'x-query-engine-skip': true,
      'x-query-engine-relation': 'ocrInvoiceList:*',
      properties: generateXindexInOrder({
        ocrInvoiceId: {
          type: 'string',
          'x-hidden': true
        },
        invoiceName: {
          type: 'string',
          title: i18nExpression('agentOnlineInvoice.invoiceName'), // 发票名称
          'x-render-table-column': {
            minWidth: 180
          }
        },
        fileSourceName: {
          type: 'string',
          'x-component': 'TableButton',
          'x-component-props': {
            type: 'text',
            '@click': expression('({ row }) => $invoicePreview($form,row)')
          },
          'x-render-table-column': {
            title: i18nExpression('purSettlementMod.fileSourceName2'), // 发票影像
            minWidth: 120,
            customRender: true
          },
          'x-read-pretty': false
        },
        invoiceType: {
          type: 'string',
          title: i18nExpression('purSettlementMod.invoiceType'), // 增值税发票类型
          'x-render-table-column': {
            minWidth: 120
          },
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'INVOICE_TYPE'
          }
        },
        purchaserRegisterNum: {
          type: 'string',
          title: i18nExpression('purSettlementMod.purchaserRegisterNum'), // 采购方税号
          'x-render-table-column': {
            minWidth: 120
          }
        },
        invoiceCode: {
          type: 'string',
          title: i18nExpression('purSettlementMod.invoiceCode'), // 发票代码
          'x-render-table-column': {
            minWidth: 120
          }
        },
        invoiceNum: {
          type: 'string',
          title: i18nExpression('purSettlementMod.invoiceNum'), // 发票号码
          'x-render-table-column': {
            minWidth: 120
          }
        },
        invoiceDate: {
          ...yearMonthDaySelectorSegment,
          'x-component-props': {
            ...yearMonthDaySelectorSegment['x-component-props'],
            formatter: expression(`({ cellValue, row, column }) => {
              parseTime(row.invoiceDate, '{y}-{m}-{d}')
            }`)
          },
          title: i18nExpression('purSettlementMod.invoiceDate2'), // 开票日期
          'x-render-table-column': {
            minWidth: 120
          }
        },
        checkCode: {
          type: 'string',
          title: i18nExpression('purSettlementMod.checkCode'), // 校验码
          'x-render-table-column': {
            minWidth: 120
          }
        },
        purchaserName: {
          type: 'string',
          title: i18nExpression('purSettlementMod.purchaserName'), // 采购方
          'x-render-table-column': {
            minWidth: 120
          }
        },
        sellerName: {
          type: 'string',
          title: i18nExpression('purSettlementMod.sellerName2'), // 供方
          'x-render-table-column': {
            minWidth: 120
          }
        },
        noTaxTotalAmount: {
          type: 'string',
          title: i18nExpression('purSettlementMod.noTaxTotalAmount2'), // 未税金额
          'x-render-table-column': {
            minWidth: 120
          }
        },
        totalTax: {
          type: 'string',
          title: i18nExpression('purSettlementMod.totalTax'), // 税额
          'x-render-table-column': {
            minWidth: 120
          }
        },
        totalAmount: {
          type: 'string',
          title: i18nExpression('purSettlementMod.totalAmount'), // 含税金额
          'x-render-table-column': {
            minWidth: 120
          }
        },
        commodityTaxRate: {
          type: 'string',
          title: i18nExpression('bidMod.taxRate'), // 税率
          'x-render-table-column': {
            minWidth: 120
          }
        },
        sellerRegisterNum: {
          type: 'string',
          title: i18nExpression('agentOnlineInvoice.sellerRegisterNum'), // 销售方纳税人识别号
          'x-render-table-column': {
            minWidth: 120
          }
        },
        invoiceCourierNo: {
          type: 'string',
          title: i18nExpression('purSettlementMod.invoiceCourierNo'), // 发票快递单号
          'x-render-table-column': {
            minWidth: 120
          }
        },
        amountInFigures: {
          type: 'string',
          title: i18nExpression('agentOnlineInvoice.amountInFigures'), // 价税合计(小写)
          'x-render-table-column': {
            minWidth: 120
          }
        },
        amountInWords: {
          type: 'string',
          title: i18nExpression('agentOnlineInvoice.amountInWords'), // 价税合计(大写)
          'x-render-table-column': {
            minWidth: 120
          }
        },
        operation: {
          type: 'void',
          title: i18nExpression('common.operation'),
          'x-render-table-column': {
            width: 160,
            fixed: 'right'
          },
          'x-component': 'Space',
          'x-reactions': expression(`(field) => {
              field.visible = !$form.readPretty
            }`),
          properties: {
            editDisabled: {
              type: 'void',
              'x-component': 'RButton',
              'x-visible': expression('$form.query(\'OnlineInvoice\').get(\'data\').isFirstApproveShow'),
              title: i18nExpression('common.edit'),
              'x-component-props': {
                type: 'text',
                disabled: true
              }
            },
            edit: {
              type: 'void',
              title: i18nExpression('common.edit'),
              'x-hidden': expression('$form.query(\'OnlineInvoice\').get(\'data\').isFirstApproveShow'),
              'x-component': 'CInvoiceUpload',
              'x-component-props': {
                editMode: true,
                editRow: expression('$table.getRowByIndex($self.index)'),
                fileFunction: 'BUYER_INVOICE_SETTLE',
                invoiceInformation: expression('$form.values.ocrInvoiceList'),
                '@editInvoiceRow': expression(`(row) => {
                    $editInvoiceRow($form, row)
                  }`),
                '@saveInvoice': expression(`(fileList) => {
                      $saveInvoice($form,fileList)
                  }`)
              }
            },
            delete: {
              type: 'void',
              'x-component': 'RButton',
              title: i18nExpression('common.delete'),
              'x-component-props': {
                type: 'text',
                disabled: expression('$form.query(\'OnlineInvoice\').get(\'data\').isFirstApproveShow'),
                '@click': expression(`() => {
                      $table.remove($self.index)
                      $setNoTaxCal($form)
                  }`)
              }
            },
            download: {
              type: 'void',
              'x-component': 'RButton',
              title: i18nExpression('common.download'),
              'x-component-props': {
                disabled: expression('$form.query(\'OnlineInvoice\').get(\'data\').isFirstApproveShow'),
                type: 'text',
                '@click': expression(`() => {
                    const row = $table.getRowByIndex($self.index)
                    $downloadInvoice(row,$message)
                  }`)
              }
            }
          }
        }
      })
    }
  }
}
