import {expression, generateXindexInOrder, i18nExpression, methodExpression} from '@meicloud/render-engine'


export const contactData = {
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
      contactDataBefore: {
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
          // 姓名
          contactName: {
            type: 'string',
            title: i18nExpression('vendorMod.nickname'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              'disabled': true
            },
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
            }
          },
          // 性别
          ceeaGender: {
            type: 'string',
            title: i18nExpression('vendorMod.sex'),
            'x-render-table-column': {
              minWidth: 100
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'GENDER',
              'disabled': true
            }
          },
          // 部门
          ceeaDeptName: {
            type: 'string',
            title: i18nExpression('vendorMod.department'),
            'x-render-table-column': {
              minWidth: 100
            },
            'x-component-props': {
              'disabled': true
            }
          },
          // 职位
          position: {
            type: 'string',
            title: i18nExpression('dataConfMod.position'),
            'x-render-table-column': {
              minWidth: 100
            },
            'x-component-props': {
              'disabled': true
            }
          },
          // 联系方式
          ceeaContactMethod: {
            type: 'string',
            title: i18nExpression('vendorMod.contactMethod'),
            'x-render-table-column': {
              minWidth: 100
            },
            'x-component-props': {
              'disabled': true
            }
          },
          // 邮箱
          email: {
            type: 'string',
            title: i18nExpression('vendorMod.email'),
            'x-render-table-column': {
              minWidth: 100
            },
            'x-component-props': {
              'disabled': true
            }
          },
          // 默认联系人
          ceeaDefaultContact: {
            type: 'string',
            title: i18nExpression('dataConfMod.isDefault'),
            'x-render-table-column': {
              minWidth: 100
            },
            'x-component': 'Checkbox',
            'x-component-props': {
              'true-label': "Y",
              'false-label': "N",
              'disabled': true
            }
          },
          // 备注
          ceeaComments: {
            type: 'string',
            title: i18nExpression('dataConfMod.remark'),
            'x-render-table-column': {
              minWidth: 100
            },
            'x-component-props': {
              'disabled': true
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
                 $self.query('contactDataAfter')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)
            }
          }
        }
      },
      contactDataAfter: {
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
          // 姓名
          contactName: {
            type: 'string',
            title: i18nExpression('vendorMod.nickname'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-reactions': expression(`() => {
                const oldData = $form.query('contactDataBefore').get('value')[$self.index]?.contactName || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component-props': {
              'disabled': expression(`$form.readPretty`),
            },
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
            }
          },
          // 性别
          ceeaGender: {
            type: 'string',
            title: i18nExpression('vendorMod.sex'),
            'x-render-table-column': {
              minWidth: 100
            },
            'x-reactions': expression(`() => {
                const oldData = $form.query('contactDataBefore').get('value')[$self.index]?.ceeaGender || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'GENDER',
              class: '',
              'disabled': expression(`$form.readPretty`)
            }
          },
          // 部门
          ceeaDeptName: {
            type: 'string',
            title: i18nExpression('vendorMod.department'),
            'x-render-table-column': {
              minWidth: 100
            },
            'x-reactions': expression(`() => {
                const oldData = $form.query('contactDataBefore').get('value')[$self.index]?.ceeaDeptName || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component-props': {
              'disabled': expression(`$form.readPretty`)
            }
          },
          // 职位
          position: {
            type: 'string',
            title: i18nExpression('dataConfMod.position'),
            'x-render-table-column': {
              minWidth: 100
            },
            'x-reactions': expression(`() => {
                const oldData = $form.query('contactDataBefore').get('value')[$self.index]?.position || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component-props': {
              'disabled': expression(`$form.readPretty`)
            }
          },
          // 联系方式
          ceeaContactMethod: {
            type: 'string',
            title: i18nExpression('vendorMod.contactMethod'),
            'x-render-table-column': {
              minWidth: 100
            },
            'x-reactions': expression(`() => {
                const oldData = $form.query('contactDataBefore').get('value')[$self.index]?.ceeaContactMethod || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component-props': {
              'disabled': expression(`$form.readPretty`)
            }
          },
          // 邮箱
          email: {
            type: 'string',
            title: i18nExpression('vendorMod.email'),
            'x-render-table-column': {
              minWidth: 100
            },
            'x-reactions': expression(`() => {
                const oldData = $form.query('contactDataBefore').get('value')[$self.index]?.email || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component-props': {
              'disabled': expression(`$form.readPretty`)
            }
          },
          // 默认联系人
          ceeaDefaultContact: {
            type: 'string',
            title: i18nExpression('dataConfMod.isDefault'),
            'x-render-table-column': {
              minWidth: 100
            },
            'x-component': 'Checkbox',
            'x-reactions': expression(`() => {
                const oldData = $form.query('contactDataBefore').get('value')[$self.index]?.ceeaDefaultContact || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component-props': {
              'true-label': "Y",
              'false-label': "N",
              'disabled': expression(`$form.readPretty`)
            }
          },
          // 备注
          ceeaComments: {
            type: 'string',
            title: i18nExpression('dataConfMod.remark'),
            'x-render-table-column': {
              minWidth: 100
            },
            'x-reactions': expression(`() => {
                const oldData = $form.query('contactDataBefore').get('value')[$self.index]?.ceeaComments || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component-props': {
              'disabled': expression(`$form.readPretty`)
            }
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
