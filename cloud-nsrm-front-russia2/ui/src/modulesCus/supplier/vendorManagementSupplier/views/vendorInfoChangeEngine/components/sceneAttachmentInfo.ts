import {expression, generateXindexInOrder, i18nExpression, methodExpression} from '@meicloud/render-engine'
import {
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

export const sceneAttachmentInfo = {
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
      sceneAttachmentInfoBefore: {
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
          // 证件要求
          documentInspection: {
            type: 'string',
            title: i18nExpression('vendorMod.certificateRequirements'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-read-pretty': true,
            'x-component-props': {
              'disabled': true
            }
          },
          // 认证类型
          authType: {
            type: 'string',
            title: i18nExpression('vendorMod.authType'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component': 'SrmCommonFile',
            'x-component-props': {
              'disabled': true,
              extraData: {
                fileModular: 'sup',
                fileFunction: 'companyInfoMaintain',
                fileType: 'images'
              },
              defaultFile: {
                fileId: expression(`$table.getRowByIndex($self.index)?.fileuploadId`),
                fileName: expression('$self?.value')
              },
              'validate-options': {
                accept: ['jpg', 'png', 'jpeg']
              },
              readonly:false,
              '@on-change':expression(`({file}) => {
                const { fileId = '', fileName = '' } = file || {}
                let row = $table.getRowByIndex($self.index)
                row.fileuploadId = fileId.toString()
                row.authType = fileName
              }`)
            }
          },
          // 认证描述
          authDescription: {
            type: 'string',
            title: i18nExpression('vendorMod.authDesc'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              'disabled': true
            }
          },
          // 认证编号
          authNum: {
            type: 'string',
            title: i18nExpression('vendorMod.authNum'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              'disabled': true,
              '@change': expression(`() => {
                let row = $table.getRowByIndex($self.index)
                row.authNum = row.authNum.replace(/[\\W]/g, '')
              }`)
            }
          },
          // 认证时间
          authDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.authDate, '{y}-{m}-{d}')
              }`),
              'disabled': true
            },
            title: i18nExpression('vendorMod.authDate'),
            'x-render-table-column': {
              minWidth: 210
            }
          },
          // 认证机构
          authOrg: {
            type: 'string',
            title: i18nExpression('vendorMod.authOrg'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              'disabled': true
            }
          },
          // 证件有效期至
          endDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.endDate, '{y}-{m}-{d}')
              }`),
              'disabled': true
            },
            title: i18nExpression('vendorMod.certUntil'),
            'x-render-table-column': {
              minWidth: 210
            }
          }
        })
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
      toolbar: {
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
                 $self.query('sceneAttachmentInfoAfter')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)
            }
          }
        }
      },
      sceneAttachmentInfoAfter: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          editMode: true,
          maxHeight: 400,
          pagination: false,
          sortable: false
        },
        'x-query-engine-skip': true,
        properties: generateXindexInOrder({
          // 证件要求
          documentInspection: {
            type: 'string',
            title: i18nExpression('vendorMod.certificateRequirements'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-reactions': expression(`() => {
                const oldData = $form.query('sceneAttachmentInfoBefore').get('value')[$self.index]?.documentInspection || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-read-pretty': true,
            'x-component-props': {
              'disabled': expression(`$form.readPretty`)
            }
          },
          // 认证类型
          authType: {
            type: 'string',
            title: i18nExpression('vendorMod.authType'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-reactions': expression(`() => {
                const oldData = $form.query('sceneAttachmentInfoBefore').get('value')[$self.index]?.authType || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component': 'SrmCommonFile',
            'x-component-props': {
              'disabled': expression(`$form.readPretty`),
              extraData: {
                fileModular: 'sup',
                fileFunction: 'companyInfoMaintain',
                fileType: 'images'
              },
              defaultFile: {
                fileId: expression(`$table.getRowByIndex($self.index)?.fileuploadId`),
                fileName: expression('$self?.value')
              },
              'validate-options': {
                accept: ['jpg', 'png', 'jpeg']
              },
              readonly:false,
              '@on-change':expression(`({file}) => {
                const { fileId = '', fileName = '' } = file || {}
                let row = $table.getRowByIndex($self.index)
                row.fileuploadId = fileId.toString()
                row.authType = fileName
              }`)
            }
          },
          // 认证描述
          authDescription: {
            type: 'string',
            title: i18nExpression('vendorMod.authDesc'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-reactions': expression(`() => {
                const oldData = $form.query('sceneAttachmentInfoBefore').get('value')[$self.index]?.authDescription || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component-props': {
              'disabled': expression(`$form.readPretty`)
            }
          },
          // 认证编号
          authNum: {
            type: 'string',
            title: i18nExpression('vendorMod.authNum'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-reactions': expression(`() => {
                const oldData = $form.query('sceneAttachmentInfoBefore').get('value')[$self.index]?.authNum || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component-props': {
              'disabled': expression(`$form.readPretty`),
              '@change': expression(`() => {
                let row = $table.getRowByIndex($self.index)
                row.authNum = row.authNum.replace(/[\\W]/g, '')
              }`)
            }
          },
          // 认证时间
          authDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.authDate, '{y}-{m}-{d}')
              }`),
              'disabled': expression(`$form.readPretty`)
            },
            title: i18nExpression('vendorMod.authDate'),
            'x-render-table-column': {
              minWidth: 220
            },
            'x-reactions': expression(`() => {
                const oldData = $form.query('sceneAttachmentInfoBefore').get('value')[$self.index]?.authDate || null
                let className = redFunction(oldData, app.$dayjs($self?.value).format('YYYY-MM-DD'))
                $self.setComponentProps({ class: className })
            }`)
          },
          // 认证机构
          authOrg: {
            type: 'string',
            title: i18nExpression('vendorMod.authOrg'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-reactions': expression(`() => {
                const oldData = $form.query('sceneAttachmentInfoBefore').get('value')[$self.index]?.authOrg || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component-props': {
              'disabled': expression(`$form.readPretty`)
            }
          },
          // 证件有效期至
          endDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.authDate, '{y}-{m}-{d}')
              }`),
              'disabled': expression(`$form.readPretty`)
            },
            title: i18nExpression('vendorMod.certUntil'),
            'x-render-table-column': {
              minWidth: 220
            },
            'x-reactions': expression(`() => {
                const oldData = $form.query('sceneAttachmentInfoBefore').get('value')[$self.index]?.endDate || null
                let className = redFunction(oldData, app.$dayjs($self?.value).format('YYYY-MM-DD'))
                $self.setComponentProps({ class: className })
            }`)
          },
          operation: {
            type: 'void',
            title: "{{$t('common.operation')}}",
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
      }
    }
  }
}
