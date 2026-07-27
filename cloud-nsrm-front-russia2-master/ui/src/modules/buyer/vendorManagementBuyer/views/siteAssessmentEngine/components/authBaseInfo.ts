import {
  expression,
  generateXindexInOrder,
  i18nExpression, methodExpression
} from "@meicloud/render-engine";



export const authBaseInfo = {
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
        title: i18nExpression('common.add'),
        'x-component-props': {
          disabled: expression(`$disabledAdd($form)`),
          type: 'primary',
          '@click': expression(`() => {
           $self.query('siteFormAddressList')
             .take(field => {
               field.componentProps.componentInstance.addRow()
           })
          }`)
        }
      }
    }
  },
  siteFormAddressList: {
    type: 'array',
    'x-component': 'RenderTable',
    'x-component-props': {
      // 如果都没有标记，那么默认使用 id 作为联表主键的 key
      primaryKey: 'siteFormAddressId',
      // 启用级联删除的储值行为
      cascadeDeletion: true,
      editMode: true,
      maxHeight: 400,
      pagination: false,
      sortable: false
    },
    'x-query-engine-skip': true,
    properties: generateXindexInOrder({
      authFlag: {
        type: 'string',
        title: i18nExpression('本次认证地址(请勾选)'),
        'x-render-table-column': {
          minWidth: 150
        },
        'x-component': 'Checkbox',
        'x-component-props': {
          trueLabel: 'Y',
          falseLabel: 'N',
          disabled: expression(`$disabledAdd($form)`)
        }
      },
      country: {
        type: 'string',
        title: i18nExpression('components.address.country'),
        'x-render-table-column': {
          minWidth: 120
        },
        'x-component': 'DictSelect',
        'x-component-props': {
          code: 'country',
          placeholder:  expression(`$t('common.pleaseSelect')`),
          disabled: expression(`$disabledAdd($form)`),
          '@change': expression(`(val) => {
             let row = $table.getRowByIndex($self.index)
             // 选择国外就清理省市区，并且禁用
             if (row.plantCountry !== 'CN') {
               row.province = null
               row.city = null
             }
          }`)
        }
      },
      province: {
        type: 'string',
        title: i18nExpression('components.address.area'),
        'x-render-table-column': {
          minWidth: 120
        },
        'x-component': 'DictSelect',
        'x-component-props': {
          code: 'PROVINCE',
          'custom-select-type':"PROVINCE",
          placeholder:  expression(`$t('common.pleaseSelect')`),
          disabled: expression(`$disabledAdd($form) || $table.getRowByIndex($self.index).country!='CN'`)
        }
      },
      city: {
        type: 'string',
        title: i18nExpression('components.address.city'),
        'x-render-table-column': {
          minWidth: 120
        },
        'x-component': 'DictSelect',
        'x-component-props': {
          code: expression(`$table.getRowByIndex($self.index).province`),
          'custom-select-type': "CITY",
          placeholder:  expression(`$t('common.pleaseSelect')`),
          disabled: expression(`$disabledAdd($form) || $table.getRowByIndex($self.index).country!='CN'`)
        }
      },
      addressDetail: {
        type: 'string',
        title: i18nExpression('components.address.detailAddress'),
        'x-render-table-column': {
          minWidth: 120
        },
        'x-component-props': {
          disabled: expression(`$disabledAdd($form)`)
        },
      },
      postCode: {
        type: 'string',
        title: i18nExpression('components.address.postalCode'),
        'x-render-table-column': {
          minWidth: 120
        },
        'x-component-props': {
          disabled: expression(`$disabledAdd($form)`)
        },
      },
      siteComment: {
        type: 'string',
        title: i18nExpression('components.address.remark'),
        'x-render-table-column': {
          minWidth: 120
        },
        'x-component-props': {
          disabled: expression(`$disabledAdd($form)`)
        },
      },
      enableFlag: {
        type: 'string',
        title: i18nExpression('vendorMod.enableFlag'),
        'x-render-table-column': {
          minWidth: 120
        },
        'x-component': 'Checkbox',
        'x-component-props': {
          'true-label': "true",
          'false-label': "false",
          disabled: expression(`$disabledAdd($form)`)
        },
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
              disabled: expression(`$disabledAdd($form)`),
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
