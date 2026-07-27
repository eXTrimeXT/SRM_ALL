import {
  expression,
  i18nExpression,
  generateXindexInOrder,
  queryFieldValueBySelfExpression
} from '@meicloud/render-engine'

import {
  editTableFormItemValid
} from 'lib@/components/render-engine/schema-segments'

export default {
  type: 'void',
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: i18nExpression('vendorMod.vendorSiteInfos') // 供应商地点信息
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
          title: i18nExpression('common.add'),
          'x-component-props': {
            type: 'primary',
            '@click': expression(`() => {
                    $self.query('.siteJournals').take().componentProps.componentInstance.addRow()
                  }`)
          }
        },
        vendorSiteBatch: {
          type: 'void',
          'x-component': 'VendorSiteBatch',
          'x-component-props': {
            isNewVendor: expression('$form.query(\'ReviewForm\').get(\'data\').isNewVendor'),
            '@batchSelectCountry': expression(`(data) => {
                  $batchSelectCountry(data,$form)
              }`)
          }
        }
      }
    },
    siteJournals: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        editMode: true,
        maxHeight: 400,
        pagination: false,
        sortable: false,
        // 联表主键的 key
        primaryKey: 'siteJournalId',
        // 启用级联删除的储值行为
        cascadeDeletion: true
      },
      'x-query-engine-skip': true,
      'x-query-engine-relation': 'siteJournals:*',
      properties: generateXindexInOrder({
        siteJournalId: {
          type: 'string',
          'x-hidden': true

        },
        orgId: {
          type: 'string',
          title: i18nExpression('dataConfMod.orgId'), // 业务实体
          'x-render-table-column': {
            minWidth: 180
          },
          'x-component': 'OrganizationSelector',
          'x-component-props': {
            readPretty: '{{$form.readPretty}}',
            placeholder: i18nExpression('common.pleaseSelect'),
            'parent-id': -1,
            'node-type': 'OU',
            '@select': expression(`(node) =>{
               const row = $table.getRowByIndex($self.index)
                $selectHandler(node,row)
             }`)
          }
        },
        vendorSiteCode: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('vendorMod.siteName'), // 地点名称
            minWidth: 150
          },
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'VENDOR_SITE_CODE'
          },
          ...editTableFormItemValid
        },

        country: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('components.address.country'), // 国家
            minWidth: 120
          },
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'country',
            '@change': expression(`(node) =>{
              const row = $table.getRowByIndex($self.index)
              if(row.country !== 'CN'){
                row.province = null
                row.city = null
              }
            }`)

          },
          ...editTableFormItemValid
        },
        province: {
          type: 'string',
          title: i18nExpression('components.address.area'), // 地区
          'x-render-table-column': {
            minWidth: 120
          },
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'PROVINCE',
            'custom-select-type': 'PROVINCE',
            disabled: expression('$self.query(\'.country\').get(\'value\') !== \'CN\'')
          },
          'x-validator': {
            required: expression('$self.query(\'.country\').get(\'value\') === \'CN\''),
            message: i18nExpression('common.requiredField')
          }
        },
        city: {
          type: 'string',
          title: i18nExpression('components.address.city'), // 城市
          'x-render-table-column': {
            minWidth: 120
          },
          'x-component': 'DictSelect',
          'x-component-props': {
            code: queryFieldValueBySelfExpression('.province'),
            'custom-select-type': 'CITY',
            disabled: expression('$self.query(\'.country\').get(\'value\') !== \'CN\'')
          },
          'x-validator': {
            required: expression('$self.query(\'.country\').get(\'value\') === \'CN\''),
            message: i18nExpression('common.requiredField')
          }
        },
        addressDetail: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('components.address.detailAddress'), // 详细地址
            minWidth: 120
          },
          ...editTableFormItemValid
        },
        postCode: {
          type: 'string',
          title: i18nExpression('components.address.postalCode'), // 邮政编码
          'x-render-table-column': {
            minWidth: 120
          },
          default: '',
          'x-component-props': {
            maxlength: 6
          }
        },
        siteComment: {
          type: 'string',
          title: i18nExpression('components.address.remark'), // 地址备注
          'x-render-table-column': {
            minWidth: 120
          }
        },
        enabledFlag: {
          type: 'string',
          title: i18nExpression('common.enable'), // 启用
          'x-render-table-column': {
            minWidth: 120
          },
          default: 'Y',
          'x-component': 'Checkbox',
          'x-component-props': {
            trueLabel: 'Y',
            falseLabel: 'N'
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
                        () => {
                          $table.remove($self.index)
                        }
                      `)
              }
            }
          }
        }
      })
    }
  }
}
