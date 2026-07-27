import {
  expression,
  generateXindexInOrder,
  i18nExpression,
} from "@meicloud/render-engine";

export const vendorSiteInfoList = {
  vendorSiteInfo: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('vendorMod.vendorSiteInfos'),
    },
    'x-query-engine-skip': true,
    properties: {
      siteInfos: {
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
          // 业务实体
          belongOprId:{
            type: 'number',
            'x-hidden': true
          },
          orgCode: {
            type: 'number',
            'x-hidden': true
          },
          orgName: {
            type: 'string',
            'x-hidden': true
          },
          orgId: {
            type: 'number',
            title: i18nExpression('dataConfMod.orgId'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'parent-id': -1,
              'node-type': 'OU',
              placeholder: i18nExpression('common.pleaseSelect'),
              multiple: false,
              'disabled': expression(`$disabled`)
            },
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
            }
          },
          // 地点名称
          vendorSiteCode: {
            type: 'string',
            title: i18nExpression('vendorMod.siteName'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'VENDOR_SITE_CODE',
              'disabled': expression(`$disabled`)
            },
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
            }
          },
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
              'disabled': expression(`$disabled`)
            },
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
            }
          },
          // 省
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
              disabled: expression(`$disabled || $table.getRowByIndex($self.index).country!='CN'`)
            }
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
              code: expression(`$table.getRowByIndex($self.index).province`),
              'custom-select-type': "CITY",
              placeholder:  expression(`$t('common.pleaseSelect')`),
              disabled: expression(`$disabled || $table.getRowByIndex($self.index).country!='CN'`)
            },
          },
          // 详细地址
          addressDetail:{
            type: 'string',
            title: i18nExpression('components.address.detailAddress'),
            'x-render-table-column': {
              minWidth: 150
            },
            'x-component-props': {
              'disabled': expression(`$disabled`)
            },
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
            }
          },
          // 邮政编码
          postCode:{
            type: 'string',
            title: i18nExpression('components.address.postalCode'),
            'x-render-table-column': {
              minWidth: 150
            },
            'x-component-props': {
              'disabled': expression(`$disabled`)
            }
          },
          // 地址备注
          siteComment:{
            type: 'string',
            title: i18nExpression('components.address.remark'),
            'x-render-table-column': {
              minWidth: 150
            },
            'x-component-props': {
              'disabled': expression(`$disabled`)
            }
          },
          // 启用
          enabledFlag: {
            type: 'string',
            title: i18nExpression('common.enable'),
            'x-render-table-column': {
              minWidth: 100
            },
            'x-component': 'Checkbox',
            'x-component-props': {
              'true-label': "Y",
              'false-label': "N",
              'disabled': expression(`$disabled`)
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
                  'disabled': expression(`$disabled`),
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
