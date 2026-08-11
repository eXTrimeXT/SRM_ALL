import {
  expression, 
  generateXindexInOrder, 
  i18nExpression,
  generateCharExpressionByFunction
} from '@meicloud/render-engine'
import {
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
                readonly: !($form.values.status === 'SUBMITTED' && $form.values.dataSources !== 'MANUALLY_CREATE' && $attrs.params.flag === 'edit')
              },
              on: {
                change: value => {
                  $form.values.extRejectAttribute9 = value
                }
              }
            })
          }
        }
      )}}`
    },
    'x-visible': generateCharExpressionByFunction(({ $form, $attrs }) => {
      return ['startFileApproval', 'approval', 'view'].includes($attrs.params.flag)
    }),
    'x-query-engine-skip': true,
    properties: {
      // toolbar: {
      //   type: 'void',
      //   'x-component': 'ButtonList',
      //   'x-component-props': {
      //     class: 'list-form__toolbar'
      //   },
      //   properties: {
      //     tips: {
      //       type: 'void',
      //       "x-component": 'div',
      //       "x-component-props": {
      //         style: 'color: red'
      //       },
      //       "x-visible": expression(`$form.query('state').get('data').overseasRelation != 'OUT'`),
      //       "x-content": expression(`$form.query('state').get('data').userType == 'PERSONAL' ? t('cusEntry.vendorMod.qualificationInformationTips1') : t('cusEntry.vendorMod.qualificationInformationTips2')`)
      //     }
      //   }
      // },
      managementAttaches: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          editMode: false,
          height: 350,
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
              code: expression(`'CERTIFICATE_TYPE_' + $form.query('state').get('data').userType`)
            },
            title: i18nExpression('cusEntry.vendorMod.certificateType'),
            'x-render-table-column': {
              minWidth: 200
            }
          },
          startDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                  parseTime(row.startDate, '{y}-{m}-{d}')
              }`)
            },
            title: i18nExpression('cusEntry.vendorMod.startTime'),
            'x-render-table-column': {
              minWidth: 120
            }
          },
          endDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                  parseTime(row.endDate, '{y}-{m}-{d}')
              }`)
            },
            title: i18nExpression('cusEntry.vendorMod.endTime'),
            'x-render-table-column': {
              minWidth: 120
            }
          },
          extCertificatePeriod: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.extCertificatePeriod'), // 证书有效期
            'x-render-table-column': {
              minWidth: 120
            }
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
            }
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
              readonly: false,
              '@on-change': expression(`({file}) => {
                const { fileId = null, fileName = '' } = file || {}
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
          }
        })
      }
    }
  }
}