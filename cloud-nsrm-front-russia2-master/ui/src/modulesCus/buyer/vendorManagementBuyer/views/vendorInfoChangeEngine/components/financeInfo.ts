import {expression, generateXindexInOrder, i18nExpression, methodExpression} from '@meicloud/render-engine'
import {
  formGridSegment
} from 'lib@/components/render-engine/schema-segments'
export const financeInfo = {
  beforeChange: {
    type: 'void',
    'x-component': 'div',
    'x-component-props': {
      class: ''
    },
    properties: {
      // 变更前
      beforeChangeTitle: {
        type: 'void',
        'x-component': 'changeTitle',
        'x-component-props': {
          language: 'supplierChange.beforeChange'
        }
      },
      financeInfoBeforeForm: {
        type: 'object',
        'x-query-engine-skip': true,
        properties: {
          layout: {
            type: 'void',
            'x-decorator': 'FormLayout',
            'x-decorator-props': {
              layout: 'vertical'
            },
            'x-component': 'FormGrid',
            'x-component-props': {
              maxColumns: 3,
              columnGap: 32,
              rowGap: 0
            },
            properties: {
              totalAssets: {
                type: 'string',
                title: i18nExpression('cusEntry.vendorMod.totalAssets'),
                'x-decorator': 'FormItem',
                'x-component-props': {
                  disabled: true
                }
              },
              currentAssets: {
                type: 'string',
                title: i18nExpression('cusEntry.vendorMod.workingCapital'),
                'x-decorator': 'FormItem',
                'x-component-props': {
                  disabled: true
                }
              },
              fixedAssets: {
                type: 'string',
                title: i18nExpression('cusEntry.vendorMod.fixedAssets'),
                'x-decorator': 'FormItem',
                'x-component-props': {
                  disabled: true
                }
              },
              avgAnnualOutput: {
                type: 'string',
                title: i18nExpression('cusEntry.vendorMod.threeYearsOutput'),
                'x-decorator': 'FormItem',
                'x-component-props': {
                  disabled: true
                }
              },
              avgAnnualProfit: {
                type: 'string',
                title: i18nExpression('cusEntry.vendorMod.threeYearsNetProfits'),
                'x-decorator': 'FormItem',
                'x-component-props': {
                  disabled: true
                }
              }
            }
          }
        }
      },
      financeInfoBeforeTag: {
        type: 'void',
        'x-component': 'p',
        'x-content': i18nExpression('cusEntry.vendorMod.threeYearsReportFile')
      },
      financeReport: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          editMode: false,
          maxHeight: 400,
          pagination: false,
          sortable: false
        },
        'x-query-engine-skip': true,
        properties: generateXindexInOrder({
          year: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.year'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component': 'DatePicker',
            'x-component-props': {
              type: 'year',
              format: 'yyyy',
              'value-format': 'yyyy'
            }
          },
          remark: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.remark'),
            'x-render-table-column': {
              minWidth: 120
            }
          },
          // 附件
          fileId: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.file'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component': 'SrmCommonFile',
            'x-component-props': {
              'extra-data': {
                uploadType: 'DEF',
                sourceType: 'WEB_APP',
                fileModular: 'sup',
                fileFunction: 'companyInfoMaintain',
                fileType: 'images'
              },
              'default-file': {
                fileId: expression(`$table.getRowByIndex($self.index)?.fileId`),
                fileName: expression('$table.getRowByIndex($self.index)?.fileName')
              },
              'readonly': true
            }
          }
        })
      },
      financeInfoBeforeRemark: {
        type: 'void',
        'x-component': 'p',
        'x-content': i18nExpression('cusEntry.vendorMod.financeInfoRemark')
      }
    }
  },
  afterChange: {
    type: 'void',
    'x-component': 'div',
    'x-component-props': {
      class: ''
    },
    properties: {
      // 变更后
      afterChangeTitle: {
        type: 'void',
        'x-component': 'changeTitle',
        'x-component-props': {
          language: 'supplierChange.afterChange'
        }
      },
      financeInfoAfterForm: {
        type: 'object',
        'x-query-engine-skip': true,
        properties: {
          layout: {
            type: 'void',
            'x-decorator': 'FormLayout',
            'x-decorator-props': {
              layout: 'vertical'
            },
            'x-component': 'FormGrid',
            'x-component-props': {
              maxColumns: 3,
              columnGap: 32,
              rowGap: 0
            },
            properties: {
              totalAssets: {
                type: 'number',
                title: i18nExpression('cusEntry.vendorMod.totalAssets'),
                'x-decorator': 'FormItem',
                'x-reactions': expression(`(field) => {
                  const newData = $form.query('.financeInfoAfterForm.totalAssets').take().value
                  const oldData = $form.query('.financeInfoBeforeForm.totalAssets').take().value
                  let className = redFunction(oldData, newData)
                  $self.setComponentProps({ class: className })
                  field.value = field?.value ? field?.value : null
                }`)
              },
              currentAssets: {
                type: 'number',
                title: i18nExpression('cusEntry.vendorMod.workingCapital'),
                'x-decorator': 'FormItem',
                'x-reactions': expression(`(field) => {
                  const newData = $form.query('.financeInfoAfterForm.currentAssets').take().value
                  const oldData = $form.query('.financeInfoBeforeForm.currentAssets').take().value
                  let className = redFunction(oldData, newData)
                  $self.setComponentProps({ class: className })
                }`)
              },
              fixedAssets: {
                type: 'number',
                title: i18nExpression('cusEntry.vendorMod.fixedAssets'),
                'x-decorator': 'FormItem',
                'x-reactions': expression(`(field) => {
                  const newData = $form.query('.financeInfoAfterForm.fixedAssets').take().value
                  const oldData = $form.query('.financeInfoBeforeForm.fixedAssets').take().value
                  let className = redFunction(oldData, newData)
                  $self.setComponentProps({ class: className })
                }`)
              },
              avgAnnualOutput: {
                type: 'number',
                title: i18nExpression('cusEntry.vendorMod.threeYearsOutput'),
                'x-decorator': 'FormItem',
                'x-reactions': expression(`(field) => {
                  const newData = $form.query('.financeInfoAfterForm.avgAnnualOutput').take().value
                  const oldData = $form.query('.financeInfoBeforeForm.avgAnnualOutput').take().value
                  let className = redFunction(oldData, newData)
                  $self.setComponentProps({ class: className })
                }`)
              },
              avgAnnualProfit: {
                type: 'string',
                title: i18nExpression('cusEntry.vendorMod.threeYearsNetProfits'),
                'x-decorator': 'FormItem',
                'x-reactions': expression(`(field) => {
                  const newData = $form.query('.financeInfoAfterForm.avgAnnualProfit').take().value
                  const oldData = $form.query('.financeInfoBeforeForm.avgAnnualProfit').take().value
                  let className = redFunction(oldData, newData)
                  $self.setComponentProps({ class: className })
                }`)
              }
            }
          }
        }
      },
      financeInfoAfterTag: {
        type: 'void',
        'x-component': 'p',
        'x-content': i18nExpression('cusEntry.vendorMod.threeYearsReportFile')
      },
      toolbar: {
        type: 'void',
        'x-visible': expression('!$form.readPretty'),
        'x-component': 'ButtonList',
        'x-component-props': {
          class: 'list-form__toolbar'
        },
        properties: {
          add: {
            type: 'void',
            title: i18nExpression('common.add'),
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
                 $self.query('financeReportChange')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)
            }
          }
        }
      },
      financeReportChange: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          editMode: true,
          maxHeight: 400,
          pagination: false,
          sortable: false,
          // 如果都没有标记，那么默认使用 id 作为联表主键的 key
          primaryKey: 'financeChangeId',
          // 启用级联删除的储值行为
          cascadeDeletion: true
        },
        'x-query-engine-skip': true,
        properties: generateXindexInOrder({
          year: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.year'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component': 'DatePicker',
            'x-component-props': {
              type: 'year',
              format: 'yyyy',
              'value-format': 'yyyy'
            },
            'x-reactions': expression(`() => {
              const oldData = $form.query('financeReport').get('value')?.[$self.index]?.year || null
              let className = redFunction(oldData, $self?.value)
              $self.setComponentProps({ class: className })
            }`)
          },
          remark: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.remark'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-reactions': expression(`() => {
              const oldData = $form.query('financeReport').get('value')?.[$self.index]?.remark || null
              let className = redFunction(oldData, $self?.value)
              $self.setComponentProps({ class: className })
            }`)
          },
          // 附件
          fileId: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.file'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component': 'SrmCommonFile',
            'x-component-props': {
              'extra-data': {
                uploadType: 'DEF',
                sourceType: 'WEB_APP',
                fileModular: 'sup',
                fileFunction: 'companyInfoMaintain',
                fileType: 'images'
              },
              'default-file': {
                fileId: expression(`$table.getRowByIndex($self.index)?.fileId`),
                fileName: expression('$table.getRowByIndex($self.index)?.fileName')
              },
              '@on-change': expression(`({file}) => {
                const { fileId, fileName } = file || {}
                $table.getRowByIndex($self.index).fileId = fileId
                $table.getRowByIndex($self.index).fileName = fileName
              }`)
            },
            'x-reactions': expression(`() => {
              const oldData = $form.query('financeReport').get('value')?.[$self.index]?.fileId || null
              let className = redFunction(oldData, $self?.value)
              $self.setComponentProps({ class: className })
            }`)
          },
          operation: {
            type: 'void',
            title: "{{$t('common.operation')}}",
            'x-visible': expression('!$form.readPretty'),
            'x-render-table-column': {
              width: 150,
              fixed: 'right'
            },
            'x-component': 'RenderTableButtonList',
            properties: {
              delete: {
                type: 'void',
                title: "{{$t('common.delete')}}",
                'x-component-props': {
                  'disabled': expression(`$form.readPretty`),
                  type: 'text',
                  '@click': expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)
                }
              }
            }}
        })
      },
      financeInfoAfterRemark: {
        type: 'void',
        'x-component': 'p',
        'x-content': i18nExpression('cusEntry.vendorMod.financeInfoRemark')
      }
    }
  }
}
