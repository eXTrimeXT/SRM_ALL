import {
  expression,
  generateXindexInOrder,
  i18nExpression,
  generateCharExpressionByFunction
} from "@meicloud/render-engine";

export const vendorSiteInfoList = {
  vendorSiteInfo: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: `{{observer(
        {
          render(h) {
            return h($$components.Note, {
              props: {
                title: t('vendorMod.vendorSiteInfos2'),
                value: '',
                readonly: true
              }
            })
          }
        }
      )}}`
    },
    'x-query-engine-skip': true,
    'x-visible': generateCharExpressionByFunction(({ $form }) => {
      return $form.query('state').get('data').userType !== 'PERSONAL'
    }),
    properties: {
      companyAddressInfos: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          editMode: true,
          maxHeight: 400,
          pagination: false,
          sortable: false,
          primaryKey: 'companyAddressId',
          // 启用级联删除的储值行为
          cascadeDeletion: true
        },
        'x-query-engine-skip': true,
        properties: generateXindexInOrder({
          // 国家
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
              'disabled': true,
              '@change': expression(`(val) => {
                let row = $table.getRowByIndex($self.index)
                row.area = null
                row.city = null
              }`)
            },
            'x-reactions': expression(`() => {
              const data = $taxDictClass.getDictDetail('country', $self.value)
              $form.query('CompanyInfo.SchemaWorkflow.layout.collapse.vendorSiteInfo.companyAddressInfos.' + [$self.index] + '.area').take()?.setComponentProps({ code: data ? data.description : undefined })
            }`),
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
            }
          },
          // 省
          area: {
            type: 'string',
            title: i18nExpression('components.address.area'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              'custom-select-type':"PROVINCE",
              placeholder:  expression(`$t('common.pleaseSelect')`),
              disabled: true
            },
          },
          // 城市
          city: {
            type: 'string',
            title: i18nExpression('components.address.city'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: expression(`$table.getRowByIndex($self.index).area || ''`),
              'custom-select-type': "CITY",
              placeholder:  expression(`$t('common.pleaseSelect')`),
              disabled: true
            },
          },
          // 详细地址
          address:{
            type: 'string',
            title: i18nExpression('components.address.detailAddress'),
            'x-render-table-column': {
              minWidth: 150
            },
            'x-component-props': {
              'disabled': true
            },
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
            }
          },
          // 邮政编码
          postalCode:{
            type: 'string',
            title: i18nExpression('components.address.postalCode'),
            'x-render-table-column': {
              minWidth: 150
            },
            'x-component-props': {
              'disabled': true
            }
          },
          // 地址备注
          remark:{
            type: 'string',
            title: i18nExpression('components.address.remark'),
            'x-render-table-column': {
              minWidth: 150
            },
            'x-component-props': {
              'disabled': true
            }
          },
          // 启用
          isActive: {
            type: 'string',
            title: i18nExpression('common.enable'),
            'x-render-table-column': {
              minWidth: 100
            },
            'x-component': 'Checkbox',
            'x-component-props': {
              'true-label': "Y",
              'false-label': "N",
              'disabled': true
            }
          }
        })
      }
    }
  }
}
