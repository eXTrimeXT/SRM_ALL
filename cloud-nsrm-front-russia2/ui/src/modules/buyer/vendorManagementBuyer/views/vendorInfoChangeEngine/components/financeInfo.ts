import {expression, generateXindexInOrder, i18nExpression, methodExpression} from '@meicloud/render-engine'

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
      financeInfoBefore: {
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
          // 引入组织
          orgCode: {
            type: 'string',
            'x-hidden': true
          },
          orgName: {
            type: 'string',
            // '引入组织'
            title: i18nExpression('vendorMod.ceeaOrgName2'),
            'x-render-table-column': {
              minWidth: 120
            }
          },
          orgId: {
            type: 'string',
            // '引入组织'
            title: i18nExpression('vendorMod.ceeaOrgName2'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-hidden': true,
            'x-component': 'Select',
            'x-component-props': {
              'disabled': true,
              '@change': expression(`(val) => {
                  const orgCategorys = $form.query('.orgCategorys').take()?.value
                  let datas = []
                  orgCategorys?.forEach(resData => {
                    const objs = {
                      key:resData.orgId,
                      label:resData.orgName,
                      value:resData.orgId
                    }
                    datas.push(objs)
                  })
                  let dictItem = datas.find(i => i.orgId === val) || {}
                  let row = $table.getRowByIndex($self.index)
                  row.orgCode = dictItem.orgCode
                  row.orgName = dictItem.orgName
               }`)
            },
            'x-reactions': [
              expression(`(field) => {
                const orgCategorys = $form.query('.orgCategorys').take()?.value
                let datas = []
                orgCategorys?.forEach(resData => {
                  const objs = {
                    key:resData.orgId,
                    label:resData.orgName,
                    value:resData.orgId
                  }
                  datas.push(objs)
                })
                // 去重
                let attrId = []
                let attr = []
                datas?.forEach((dataE) => {
                  if (!attrId.includes(dataE.orgId)) {
                    attrId.push(dataE.orgId)
                    attr.push(dataE)
                  }
                })
                $self.dataSource = attr
              }`)
            ],
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
            }
          },
          // 工厂代码
          factoryCode: {
            type: 'string',
            title: i18nExpression('vendorMod.factoryCode'),
            'x-render-table-column': {
              minWidth: 150
            },
            'x-component-props': {
              'disabled': true,
              maxlength: "50",
              'show-word-limit': true,
              '@onKeyUp': "value=value.replace(/[^\w\\/]/ig,'')"
            }
          },
          // 结算币种
          clearCurrency: {
            type: 'string',
            title: i18nExpression('vendorMod.clearCurrency'),
            'x-render-table-column': {
              minWidth: 150
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'BID_TENDER_CURRENCY',
              'disabled': true
            },
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
            }
          },
          // 付款方式
          paymentMethod: {
            type: 'string',
            title: i18nExpression('vendorMod.paymentMethod'),
            'x-render-table-column': {
              minWidth: 150
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PAYMENT_METHOD',
              'disabled': true
            },
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
            }
          },
          // 付款账期
          paymentTerms: {
            type: 'string',
            title: i18nExpression('vendorMod.paymentTerms'),
            'x-render-table-column': {
              minWidth: 150
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PAYMENT_TERMS',
              'disabled': true
            },
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
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
                 $self.query('financeInfoChanges')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)
            }
          }
        }
      },
      financeInfoChanges: {
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
          // 引入组织
          orgCode: {
            type: 'string',
            'x-hidden': true
          },
          orgName: {
            type: 'string',
            'x-hidden': true
          },
          orgId: {
            type: 'string',
            // '引入组织'
            title: i18nExpression('vendorMod.ceeaOrgName2'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component': 'OrganizationSelector',
            'x-reactions': expression(`() => {
                const oldData = $form.query('financeInfoBefore').get('value')[$self.index]?.orgId || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component-props': {
              nodeType: "OU",
              placeholder: i18nExpression('common.pleaseSelect'),
              'read-pretty': expression(`$form.readPretty`),
              '@select': expression(`(val) => {
                  const row = $table.getRowByIndex($self.index)
                  row.orgId = val ? val.organizationId : null
                  row.orgCode = val ? val.organizationCode : ''
                  row.orgName = val ? val.organizationName : ''
              }`)
            },
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
            }
          },
          // 工厂代码
          factoryCode: {
            type: 'string',
            title: i18nExpression('vendorMod.factoryCode'),
            'x-render-table-column': {
              minWidth: 150
            },
            'x-reactions': expression(`() => {
                const oldData = $form.query('financeInfoBefore').get('value')[$self.index]?.factoryCode || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component-props': {
              'disabled': expression(`$form.readPretty`),
              maxlength: "50",
              'show-word-limit': true,
              '@onKeyUp': "value=value.replace(/[^\w\\/]/ig,'')"
            }
          },
          // 结算币种
          clearCurrency: {
            type: 'string',
            title: i18nExpression('vendorMod.clearCurrency'),
            'x-render-table-column': {
              minWidth: 150
            },
            'x-component': 'DictSelect',
            'x-reactions': expression(`() => {
                const oldData = $form.query('financeInfoBefore').get('value')[$self.index]?.clearCurrency || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component-props': {
              code: 'BID_TENDER_CURRENCY',
              'disabled': expression(`$form.readPretty`)
            },
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
            }
          },
          // 付款方式
          paymentMethod: {
            type: 'string',
            title: i18nExpression('vendorMod.paymentMethod'),
            'x-render-table-column': {
              minWidth: 150
            },
            'x-component': 'DictSelect',
            'x-reactions': expression(`() => {
                const oldData = $form.query('financeInfoBefore').get('value')[$self.index]?.paymentMethod || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component-props': {
              code: 'PAYMENT_METHOD',
              'disabled': expression(`$form.readPretty`)
            },
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
            }
          },
          // 付款账期
          paymentTerms: {
            type: 'string',
            title: i18nExpression('vendorMod.paymentTerms'),
            'x-render-table-column': {
              minWidth: 150
            },
            'x-reactions': expression(`() => {
                const oldData = $form.query('financeInfoBefore').get('value')[$self.index]?.paymentTerms || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PAYMENT_TERMS',
              'disabled': expression(`$form.readPretty`)
            },
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
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
