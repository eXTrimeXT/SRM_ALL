import {
  expression, 
  generateXindexInOrder, 
  i18nExpression,
  generateCharExpressionByFunction
} from '@meicloud/render-engine'
import {
  editTableFormItemValid,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

export const qualificationInformation = {
  qualificationInformation: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: `{{observer(
        {
          render(h) {
            return h($$components.Note, {
              props: {
                title: t('cusEntry.vendorMod.qualificationInformation'),
                value: $form.values.extRejectAttribute9,
                readonly: true
              }
            })
          }
        }
      )}}`
    },
    'x-visible': generateCharExpressionByFunction(({ $form }) => {
      return $form.query('state').get('data').supplementAble || $form.values.extIsQualifiedFileUpload == 'Y' || $form.values.dataSources == 'MANUALLY_CREATE'
    }),
    'x-query-engine-skip': true,
    properties: {
      // toolbar: {
        // type: 'void',
        // 'x-component': 'ButtonList',
        // 'x-component-props': {
        //   class: 'list-form__toolbar'
        // },
        // properties: {
          // add: {
          //   type: 'void',
          //   title: i18nExpression('common.add'),
          //   'x-component-props': {
          //     type: 'primary',
          //     '@click': expression(`() => {
          //       $self.query('qualificationInfo')
          //         .take(field => {
          //           field.componentProps.componentInstance.addRow('push', {})
          //         })
          //     }`)
          //   }
          // }
          // tips: {
          //   type: 'void',
          //   "x-component": 'div',
          //   "x-component-props": {
          //     style: 'color: red'
          //   },
          //   "x-visible": expression(`$form.query('state').get('data').overseasRelation != 'OUT'`),
          //   "x-content": expression(`$form.query('state').get('data').overseasRelation == 'PERSONAL' ? t('cusEntry.vendorMod.qualificationInformationTips1') : t('cusEntry.vendorMod.qualificationInformationTips2')`)
          // }
        // }
      // },
      qualificationInfo: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          editMode: expression(`$form.query('state').get('data').supplementAble || ($form.values.dataSources == 'MANUALLY_CREATE' && $form.values.ifSupplierCompleteInfo != 'Y')`),
          height: 350,
          pagination: false,
          sortable: false,
          primaryKey: 'managementAttachId',
          // 启用级联删除的储值行为
          cascadeDeletion: true
        },
        'x-query-engine-skip': true,
        'x-query-engine-relation': 'managementAttaches:*',
        properties: generateXindexInOrder({
          // categoryName: {
          //   type: 'string',
          //   'x-component': "QuickSearchWrapper",
          //   'x-component-props': {
          //     showKey: "categoryName",
          //     name:"scc_base_purchase_category4",
          //     '@close-quicksearch': expression(`(val, scope) => {
          //       let row = $table.getRowByIndex($self.index)
          //       row.categoryCode = val?.categoryCode
          //       row.categoryName = val?.categoryName
          //       row.categoryId = val?.categoryId
          //     }`),
          //     disabled: expression(`$form.query('state').get('data').$disabled`)
          //   },
          //   title: i18nExpression('cusEntry.vendorMod.category'),
          //   'x-render-table-column': {
          //     minWidth: 120
          //   }
          // },
          authNum: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: expression(`'CERTIFICATE_TYPE_' + $form.query('state').get('data').overseasRelation`)
            },
            title: i18nExpression('cusEntry.vendorMod.certificateType'),
            'x-render-table-column': {
              minWidth: 200
            },
            'x-read-pretty': true
          },
          startDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.startDate, '{y}-{m}-{d}')
              }`),
              '@change': expression(`(value) => {
                if(!($table.getRowByIndex($self.index).endDate && $table.getRowByIndex($self.index).startDate)){
                  $table.getRowByIndex($self.index).extCertificatePeriod = null
                  return
                }
                let date1 = new Date($table.getRowByIndex($self.index).endDate)
                let date2 = new Date($table.getRowByIndex($self.index).startDate)
                let timeDifference = date1 - date2
                const dayDifference = Math.floor(timeDifference / (1000 * 60 * 60 * 24));
                if(dayDifference < 0){
                  $table.getRowByIndex($self.index).extCertificatePeriod = 0+$t('bidMod.heaven')
                  return
                }
                $table.getRowByIndex($self.index).extCertificatePeriod = dayDifference+$t('bidMod.heaven')
              }`)
            },
            title: i18nExpression('cusEntry.vendorMod.startTime'),
            'x-render-table-column': {
              minWidth: 180
            },
            ...editTableFormItemValid
          },
          // 有效截止时间是否必填
          extIfEndDateRequired: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.extIfEndDateRequired'),
            'x-render-table-column': {
              minWidth: 180
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            },
            ...editTableFormItemValid
          },
          endDate: {
            type: 'void',
            'x-component': 'Space',
            title: i18nExpression('cusEntry.vendorMod.endTime'),
            'x-render-table-column': {
              minWidth: 180
            },
            properties: {
              endDate: {
                ...yearMonthDaySelectorSegment,
                'x-visible': expression(`$table.getRowByIndex($self.index).extIfEndDateRequired === 'Y'`),
                'x-component-props': {
                  style: 'width: 100%',
                  ...yearMonthDaySelectorSegment['x-component-props'],
                  formatter: expression(`({ cellValue, row, column }) => {
                    parseTime(row.endDate, '{y}-{m}-{d}')
                  }`),
                  '@change': expression(`(value) => {
                    if(!($table.getRowByIndex($self.index).endDate && $table.getRowByIndex($self.index).startDate)){
                      $table.getRowByIndex($self.index).extCertificatePeriod = null
                      return
                    }
                    let date1 = new Date($table.getRowByIndex($self.index).endDate)
                    let date2 = new Date($table.getRowByIndex($self.index).startDate)
                    let timeDifference = date1 - date2
                    const dayDifference = Math.floor(timeDifference / (1000 * 60 * 60 * 24))
                    if(dayDifference < 0){
                      $table.getRowByIndex($self.index).extCertificatePeriod = 0 + $t('bidMod.heaven')
                      return
                    }
                    $table.getRowByIndex($self.index).extCertificatePeriod = dayDifference + $t('bidMod.heaven')
                  }`)
                }
              }
            }
          },
          extCertificatePeriod: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.extCertificatePeriod'), // 证书有效期
            'x-render-table-column': {
              minWidth: 120
            },
            'x-read-pretty': true
          },
          extIsMandatory: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            },
            title: i18nExpression('dataConfMod.isRequested'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-read-pretty': true
          },
          fileuploadId: {
            type: 'void',
            'x-component': 'Space',
            title: i18nExpression('cusEntry.vendorMod.file'),
            'x-render-table-column': {
              minWidth: 150
            },
            properties: {
              starFlag: {
                type: 'void',
                'x-component': 'Span',
                'x-component-props': {
                  style: 'color: red'
                },
                'x-content': expression(`$table.getRowByIndex($self.index).extIsMandatory === 'Y' ? '*' : null`),
              },
              fileuploadId: {
                type: 'number',
                'x-component': 'SrmCommonFile',
                'x-component-props': {
                  extraData: {
                    fileModular: 'sup',
                    fileFunction: 'companyInfoMaintain',
                    fileType: 'images'
                  },
                  defaultFile: {
                    fileId: expression(`$table.getRowByIndex($self.index)?.fileuploadId`),
                    fileName: expression('$table.getRowByIndex($self.index)?.authType')
                  },
                  readonly: expression(`!($form.query('state').get('data').supplementAble || ($form.values.dataSources == 'MANUALLY_CREATE' && $form.values.ifSupplierCompleteInfo != 'Y'))`),
                  '@on-change':expression(`({file}) => {
                    const { fileId = null, fileName = '' } = file || {}
                    let row = $table.getRowByIndex($self.index)
                    row.fileuploadId = fileId
                    row.authType = fileName
                  }`)
                }
              }
            }
          },
          authDescription: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.remark'),
            'x-render-table-column': {
              minWidth: 120
            }
          }
        })
      }
    }
  }
}