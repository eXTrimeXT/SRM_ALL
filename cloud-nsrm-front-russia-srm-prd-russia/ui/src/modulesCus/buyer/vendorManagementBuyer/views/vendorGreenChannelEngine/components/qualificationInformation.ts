import {
  expression, 
  generateXindexInOrder, 
  i18nExpression,
  generateCharExpressionByFunction
} from '@meicloud/render-engine'

export const qualificationInformation = {
  qualificationInformation: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('cusEntry.vendorMod.qualificationInformation'),
    },
    'x-visible': generateCharExpressionByFunction(({ $form }) => {
      return $form.query('state').get('data').overseasRelation !== 'PERSONAL'
    }),
    'x-query-engine-skip': true,
    properties: {
      toolbar: {
        'x-visible': expression(`!$disabled`),
        type: 'void',
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
                $self.query('qualificationInfo')
                  .take(field => {
                    field.componentProps.componentInstance.addRow('push', {})
                  })
              }`)
            }
          }
        }
      },
      qualificationInfo: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          editMode: expression(`!$disabled`),
          maxHeight: 400,
          pagination: false,
          sortable: false
        },
        'x-query-engine-skip': true,
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
          //     }`)
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
              code: 'CERTIFICATE_TYPE'
            },
            title: i18nExpression('cusEntry.vendorMod.certificateType'),
            'x-render-table-column': {
              minWidth: 120
            }
          },
          startDate: {
            type: 'date',
            title: i18nExpression('cusEntry.vendorMod.startTime'),
            'x-render-table-column': {
              minWidth: 120
            }
          },
          endDate: {
            type: 'date',
            title: i18nExpression('cusEntry.vendorMod.endTime'),
            'x-render-table-column': {
              minWidth: 120
            }
          },
          fileuploadId: {
            type: 'string',
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
              readonly:false,
              '@on-change':expression(`({file}) => {
                const { fileId = '', fileName = '' } = file || {}
                let row = $table.getRowByIndex($self.index)
                row.fileuploadId = fileId
                row.authType = fileName
              }`)
            },
            title: i18nExpression('cusEntry.vendorMod.file'),
            'x-render-table-column': {
              minWidth: 120
            }
          },
          authDescription: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.remark'),
            'x-render-table-column': {
              minWidth: 120
            }
          },
          operation: {
            type: 'void',
            'x-visible': expression(`!$disabled`),
            title: i18nExpression('common.operation'),
            'x-render-table-column': {
              width: 60,
              fixed: 'right'
            },
            'x-component': 'RenderTableButtonList',
            properties: {
              delete: {
                type: 'void',
                title: i18nExpression('common.delete'),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`() => {
                    $table.remove($self.index)
                  }`)
                }
              }
            }
          }
        })
      }
    }
  }
}