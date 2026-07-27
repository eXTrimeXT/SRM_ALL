/* eslint-disable quotes */
import {
  expression,
  i18nExpression,
  generateXindexInOrder
} from '@meicloud/render-engine'

export default {
  type: 'void',
  'x-component': 'FormCollapse.Item',
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
                field.visible = !$disabledFlag($attrs)
              }`),
      properties: {
        add: {
          type: 'void',
          'x-component': 'CInvoiceUpload',
          'x-component-props': {
            isReadonly: expression(`$form.readPretty`),
            fieldSupplement: [], // 可附加补充字段显示
            fileFunction: expression(`$buyer() ? 'BUYER_INVOICE_CONTRACT_PERFORMANCE' : 'SUPPLIER_INVOICE_CONTRACT_PERFORMANCE'`),
            invoiceInformation: expression('$form.values.perInvoiceInformationList'),
            '@editInvoiceRow': expression(`(row) => {
                  $editInvoiceRow($form,row)
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
            disabled: expression(`$form.readPretty`),
            '@click': expression(`() => {
                $batchDeleteInvoice($form,$message)
              }`)
          }
        },
        uploadTip: {
          type: 'void',
          'x-component': 'span',
          'x-content': '* 发票仅支持JPG、PNG、JPEG、PDF、OFD格式，单个文件大小不超过10M',
          'x-component-props': {
            style: 'color: red; font-size: 12px;'
          }
        }
      }
    },
    perInvoiceInformationList: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        maxHeight: 400,
        pagination: false,
        preColumns: 'checkbox,seq',
        sortable: false,
        primaryKey: 'perInvoiceInformationId',
        cascadeDeletion: true
      },
      'x-query-engine-skip': true,
      'x-query-engine-relation': 'perInvoiceInformationList:*',
      properties: generateXindexInOrder({
        perInvoiceInformationId: {
          type: 'string',
          'x-hidden': true
        },
        invoiceName: {
          type: 'string',
          title: '发票名称', // 发票名称
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
          }
          // 'x-component': 'DictSelect',
          // 'x-component-props': {
          //   code: 'INVOICE_TYPE'
          // }
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
          type: 'string',
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
          title: '销售方纳税人识别号', // 销售方纳税人识别号
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
          title: '价税合计(小写)', // 价税合计(小写)
          'x-render-table-column': {
            minWidth: 120
          }
        },
        amountInWords: {
          type: 'string',
          title: '价税合计(大写)', // 价税合计(大写)
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
          'x-component': 'RenderTableButtonList',
          'x-reactions': expression(`(field) => {
              field.visible = !$disabledFlag($attrs)
            }`),
          properties: {
            edit: {
              type: 'void',
              title: i18nExpression('common.edit'),
              'x-component-props': {
                disabled: expression('$disabledFlag($attrs) || $form.readPretty'),
                type: 'text',
                '@click': expression(`({ row }) => {
                      $form.query('uploadInvoice').get('componentProps').visible = true
                      $form.query('PerInvoice').get('data').fileList = [{ ...row, img: getImgSrc(row.fileuploadId, row.fileSourceName) }]
                    }
                  `)
              }
            },
            delete: {
              type: 'void',
              title: i18nExpression('common.delete'),
              'x-component-props': {
                disabled: expression('$disabledFlag($attrs) || $form.readPretty'),
                '@click': expression(`() => {
                      $table.remove($self.index)
                  }`)
              }
            },
            download: {
              type: 'void',
              title: i18nExpression('common.download'),
              'x-component-props': {
                disabled: expression('$disabledFlag($attrs) || $form.readPretty'),
                type: 'text',
                '@click': expression(`({ row }) => {
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
