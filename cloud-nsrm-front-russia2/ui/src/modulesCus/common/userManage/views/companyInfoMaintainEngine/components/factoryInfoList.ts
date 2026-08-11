import {
  expression,
  generateXindexInOrder,
  i18nExpression,
} from "@meicloud/render-engine";
import {editTableFormItemValid} from "lib@/components/render-engine";

export const factoryInfoList = {
  factoryInfo: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('vendorMod.factoryInfo'),
    },
    'x-query-engine-skip': true,
    properties: {
      toolbar: {
        type: 'void',
        'x-component': 'Space',
        'x-component-props': {
          class: 'list-form__toolbar'
        },
        properties: {
          add: {
            type: 'void',
            title: i18nExpression('common.new'),
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              disabled: expression(`$form.query('state').get('data').$disabled`),
              '@click': expression(`() => {
                 $self.query('plantInfos')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)
            }
          }
        }
      },
      plantInfos: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          editMode: true,
          maxHeight: 400,
          pagination: false,
          sortable: false,
          primaryKey: 'plantId',
          // 启用级联删除的储值行为
          cascadeDeletion: true
        },
        'x-query-engine-skip': true,
        properties: generateXindexInOrder({
          // 厂房名称
          plantName: {
            type: 'string',
            title: i18nExpression('vendorMod.factoryInfo'),
            'x-render-table-column': {
              minWidth: 150
            },
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            },
            ...editTableFormItemValid
          },
          // 厂房性质
          plantNature: {
            type: 'string',
            title: i18nExpression('vendorMod.factoryType'),
            'x-render-table-column': {
              minWidth: 150
            },
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            },
            ...editTableFormItemValid
          },
          // 厂房面积
          plantArea: {
            type: 'string',
            title: i18nExpression('vendorMod.factoryArea'),
            'x-render-table-column': {
              minWidth: 150
            },
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            },
            ...editTableFormItemValid
          },
          // 国家
          plantCountry: {
            type: 'string',
            title: i18nExpression('components.address.country'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'country',
              placeholder:  expression(`$t('common.pleaseSelect')`),
              'disabled': expression(`$form.query('state').get('data').$disabled`),
              '@change': expression(`(val) => {
                let row = $table.getRowByIndex($self.index)
                row.plantProvince = null
                row.plantCity = null
              }`)
            },
            'x-reactions': expression(`() => {
              const data = $taxDictClass.getDictDetail('country', $self.value)
              $form.query('CompanyInfo.layout.collapse.factoryInfo.plantInfos.' + [$self.index] + '.plantProvince').take()?.setComponentProps({ code: data ? data.description : undefined })
            }`),
            ...editTableFormItemValid
          },
          // 省
          plantProvince: {
            type: 'string',
            title: i18nExpression('components.address.area'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              'custom-select-type':"PROVINCE",
              placeholder:  expression(`$t('common.pleaseSelect')`),
              disabled: expression(`$form.query('state').get('data').$disabled || !['CN', 'RU'].includes($table.getRowByIndex($self.index).plantCountry)`),
              '@change': expression(`(val) => {
                $table.getRowByIndex($self.index).plantCity = null
              }`)
            },
          },
          // 城市
          plantCity: {
            type: 'string',
            title: i18nExpression('components.address.city'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: expression(`$table.getRowByIndex($self.index).plantProvince || ''`),
              'custom-select-type': "CITY",
              placeholder:  expression(`$t('common.pleaseSelect')`),
              disabled: expression(`$form.query('state').get('data').$disabled || !$table.getRowByIndex($self.index).plantProvince`)
            },
          },
          // 厂房地址
          plantAddress: {
            type: 'string',
            title: i18nExpression('vendorMod.factoryAddress'),
            'x-render-table-column': {
              minWidth: 150
            },
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            },
            ...editTableFormItemValid
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
                  'disabled': expression(`$form.query('state').get('data').$disabled`),
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
