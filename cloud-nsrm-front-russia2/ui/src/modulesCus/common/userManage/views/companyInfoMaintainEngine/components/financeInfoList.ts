import {
  expression,
  generateXindexInOrder,
  i18nExpression,
  generateCharExpressionByFunction
} from "@meicloud/render-engine"

export const financeInfoList = {
  financeInfo: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: `{{observer(
        {
          render(h) {
            return h($$components.Note, {
              props: {
                title: t('cusEntry.vendorMod.financeReport'),
                value: $form.values.extRejectAttribute6,
                readonly: true
              }
            })
          }
        }
      )}}`
    },
    'x-visible': generateCharExpressionByFunction(({ $form }) => {
      return $form.query('state').get('data').overseasRelation !== 'PERSONAL'
    }),
    'x-query-engine-skip': true,
    properties: {
      financeInfoForm: {
        type: 'void',
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
                'x-component-props': {
                  disabled: expression(`$form.query('state').get('data').$disabled`)
                }
              },
              currentAssets: {
                type: 'number',
                title: i18nExpression('cusEntry.vendorMod.workingCapital'),
                'x-decorator': 'FormItem',
                'x-component-props': {
                  disabled: expression(`$form.query('state').get('data').$disabled`)
                }
              },
              fixedAssets: {
                type: 'number',
                title: i18nExpression('cusEntry.vendorMod.fixedAssets'),
                'x-decorator': 'FormItem',
                'x-component-props': {
                  disabled: expression(`$form.query('state').get('data').$disabled`)
                }
              },
              avgAnnualOutput: {
                type: 'number',
                title: i18nExpression('cusEntry.vendorMod.threeYearsOutput'),
                'x-decorator': 'FormItem',
                'x-component-props': {
                  disabled: expression(`$form.query('state').get('data').$disabled`)
                }
              },
              avgAnnualProfit: {
                type: 'number',
                title: i18nExpression('cusEntry.vendorMod.threeYearsNetProfits'),
                'x-decorator': 'FormItem',
                'x-component-props': {
                  disabled: expression(`$form.query('state').get('data').$disabled`)
                }
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
        'x-component': 'Space',
        'x-component-props': {
          class: 'list-form__toolbar'
        },
        'x-visible': expression(`!$form.query('state').get('data').$disabled`),
        properties: {
          add: {
            type: 'void',
            title: i18nExpression('common.new'),
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
                 $self.query('.npmFinanceReports')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)
            }
          }
        }
      },
      npmFinanceReports: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          editMode: true,
          maxHeight: 400,
          pagination: false,
          sortable: false,
          primaryKey: 'financeReportId',
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
              'value-format': 'yyyy',
              disabled: expression(`$form.query('state').get('data').$disabled`)
            }
          },
          remark: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.remark'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              disabled: expression(`$form.query('state').get('data').$disabled`)
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
              '@on-change': expression(`({file}) => {
                const { fileId, fileName } = file || {}
                $table.getRowByIndex($self.index).fileId = fileId
                $table.getRowByIndex($self.index).fileName = fileName
              }`),
              readonly: expression(`$form.query('state').get('data').$disabled`)
            }
          },
          operation: {
            type: 'void',
            title: "{{$t('common.operation')}}",
            'x-render-table-column': {
              width: 60,
              fixed: 'right'
            },
            'x-visible': expression(`!$form.query('state').get('data').$disabled`),
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
            }
          }
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
