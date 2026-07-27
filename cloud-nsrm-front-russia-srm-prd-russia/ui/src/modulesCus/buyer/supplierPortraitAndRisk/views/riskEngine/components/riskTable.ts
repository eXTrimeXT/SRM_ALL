import {
  expression,
  i18nExpression,
  generateXindexInOrder,
  generateCharFunctionExpression
} from '@meicloud/render-engine'

import {
  feedbackLayoutIsPopover,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

export default {
  type: 'void',
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: i18nExpression('supRisk.title3')
  },
  properties: {
    toolbar: {
      type: 'void',
      'x-component': 'ButtonList',
      'x-component-props': {
        class: 'list-form__toolbar'
      },
      'x-reactions': expression(`(field) => {
           field.visible = !$form.readPretty
       }`),
      properties: {
        add: {
          type: 'void',
          title: `{{$t('common.add')}}`,
          'x-component-props': {
            type: 'primary',
            '@click': expression(`() => {
              $self.query('.responses').take().componentProps.componentInstance.addRow()
             }`)
          }
        },
        viewLast: {
          type: 'void',
          title: `{{$t('supRisk.viewLast')}}`,
          'x-component-props': {
            type: 'primary',
            '@click': expression(`() => {
              if(!$form.values.vendorId || !$form.values.riskType) return app.$message.warning('请选择供应商和风险影响类型')
              $form.query('lastDialog').take().setComponentProps({ visible: true })
              let data = {
                vendorId: $form.values.vendorId,
                riskType: $form.values.riskType
              }
              $queryEngine.request.baseRequest({
                  type: 'Responses',
                  lang: 'zh-cn',
                  loading: true,
                  payload: [data],
                  action: 'queryResponses',
                  query:{
                    '*':{},
                  }
                }).then((res) => {
                  $form.query('lastDetailList').take().setValue(res.data)
                })
             }`)
          }
        },
      }
    },
    responses: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        class: 'table-view-vxe-table',
        editMode: true,
        preColumns: expression('$form.readPretty ? \'seq\' : \'seq\''),
        pagination: false,
        sortable: false,
        // 联表主键的 key
        primaryKey: 'riskMonitoringId',
        // 启用级联删除的储值行为
        cascadeDeletion: true
      },
      'x-query-engine-skip': true,
      'x-read-pretty': `{{$form.readPretty}}`,
      'x-query-engine-relation': 'responses:*',
      properties: generateXindexInOrder({
        riskResponsesId: {
          type: 'string',
          'x-hidden': true

        },
        responsesType: {
          type: 'string',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'RESPONSES_TYPE',
          },
          'x-render-table-column': {
            title: "{{$t('supRisk.responsesType')}}",
            minWidth: 100,
          },
        },
        // fileuploadId: {
        //   type: 'string',
        //   'title': `{{$t('dataConfMod.attachmentTemplate')}}`,
        //   'x-component': 'SrmCommonFile',
        //   'x-query-engine-skip': true,
        //   'x-component-props': {
        //     'default-file': {
        //       fileuploadId: '{{$table.getRowByIndex($self.index)?.fileuploadId}}',
        //       fileName: '{{$table.getRowByIndex($self.index)?.fileName}}'
        //     }
        //   },
        //   'x-render-table-column': {
        //     minWidth: 130
        //   }
        // },
        fileName: {
          // 附件名称
          type: 'void',
          title: i18nExpression('附件名称'),
          'x-component': 'SrmCommonFile',
          'x-component-props': {
            'extra-data': {
              fileModular: 'sup',
              fileFunction: 'vendorBiddingManagement',
              fileType: 'images'
            },
            defaultFile: {
              fileId: `{{$table.getRowByIndex($self.index)?.fileuploadId}}`,
              fileName: `{{$table.getRowByIndex($self.index)?.fileName}}`
            },
            readonly: false,
            '@on-change': expression(`({file}) => {
                const row = $table.getRowByIndex($self.index)
                row.fileuploadId = file.fileId.toString()
                row.fileName = file.fileName
                row.createdFullName = file.createdFullName
                row.createdBy = file.createdBy
                row.creationDate = file.creationDate
              }`)
          },
          'x-render-table-column': {
          }
        },
        completeDate: {
          'x-render-table-column': {
            title: "{{$t('supRisk.completeDate')}}",
            minWidth: 120,
          },
          ...yearMonthDaySelectorSegment,
        },

        responseMeasures: {
          type: 'string',
          title: i18nExpression('supRisk.responseMeasures'),
          'x-render-table-column': {
            minWidth: 120
          }
        },

        operation: {
          type: 'void',
          title: i18nExpression('common.operation'),
          'x-render-table-column': {
            width: 60,
            fixed: 'right'
          },
          'x-component': 'RenderTableButtonList',
          'x-reactions': expression(`(field) => {
                field.visible = !$form.readPretty
            }`),
          properties: {
            delete: {
              type: 'void',
              title: i18nExpression('common.delete'),
              'x-component-props': {
                type: 'text',
                '@click': expression(`
                       ({ rowIndex }) => {
                          $table.remove(rowIndex)
                       }
                   `)
              }
            }
          }
        }
      })
    },
    lastDialog: {
      type: 'void',
      title: i18nExpression('supRisk.viewLast'),
      'x-component': 'RDialog',
      'x-component-props': {
        'close-on-click-modal':false,
        'destroy-on-close': true,
        size: 'middle',
        footerButtonList: expression(`(_, { cancelButton,okButton }) => {
        return [
          cancelButton,

        ]

        }`),
        beforeClose: expression(`(done, type) => {
          if ( type === 'ok') {

          } else {
            done()
            }
          }
        `)
      },
      properties: {
        lastDetailList: {
          type: 'array',
          'x-component': 'RenderTable',
          'x-component-props': {
            class: 'table-view-vxe-table',
            editMode: true,
            preColumns: 'seq',
            pagination: false,
            sortable: false,
            // 启用级联删除的储值行为
            cascadeDeletion: true
          },
          'x-query-engine-skip': true,
          'x-read-pretty': true,
          properties: generateXindexInOrder({

            responsesType: {
              type: 'string',
              title: "{{$t('supRisk.responsesType')}}",
              'x-render-table-column': {
                minWidth: 120
              },
              'x-component': 'DictSelect',
              'x-component-props': {
                code: 'RESPONSES_TYPE',
              },
            },
            fileuploadId: {
              type: 'string',
              'title': `{{$t('supRisk.fileuploadId')}}`,
              'x-component': 'SrmCommonFile',
              'x-query-engine-skip': true,
              'x-component-props': {
                defaultFile: {
                  fileId: `{{$table.getRowByIndex($self.index)?.fileuploadId}}`,
                  fileName: `{{$table.getRowByIndex($self.index)?.fileName}}`
                },
              },
              'x-render-table-column': {
                minWidth: 130
              }
            },
            completeDate: {
              'x-render-table-column': {
                title: "{{$t('supRisk.completeDate')}}",
                minWidth: 120,
              },
              ...yearMonthDaySelectorSegment
            },
            responseMeasures: {
              type: 'string',
              'x-render-table-column': {
                title: "{{$t('supRisk.responseMeasures')}}",
                minWidth: 120,
              },
            },


          })
        }
      }
    }
  }
}
