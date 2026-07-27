import {expression, generateXindexInOrder, i18nExpression, methodExpression} from '@meicloud/render-engine'

import {
  checkboxByYOrNSegment,
  formGridSegment, radioGroupByYOrNSegment, requiredValidatorSegment
} from 'lib@/components/render-engine/schema-segments'

export const siteInfos = {
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
      siteInfosBefore: {
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
              'disabled': true,
              'read-pretty': true,
              '@select': expression(`(node) => {
                let row = $table.getRowByIndex($self.index)
                row.orgId = node ? node.organizationId : null
                row.orgCode = node ? node.organizationCode : null
                row.orgName = node ? node.organizationName : null
                if (node) {
                  this.$http({
                    url: '/api-base/organization/organization/get',
                    method: 'GET',
                    params: { organizationId: node.organizationId },
                    loading: true
                  }).then(res => {
                    if (res.data) {
                      row.belongOprId = res.data.erpOrgId
                    }
                  })
                }
              }`)
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
              'disabled': true
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
              'disabled': true,
              '@change': expression(`(val) => {
                let row = $table.getRowByIndex($self.index)
                // 选择国外就清理省市区，并且禁用
                if (row.country !== 'CN') {
                  row.province = null
                  row.plantCity = null
                }
              }`)
            },
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
              placeholder: expression(`$t('common.pleaseSelect')`),
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
              code: expression(`$table.getRowByIndex($self.index).province`),
              'custom-select-type': "CITY",
              placeholder: expression(`$t('common.pleaseSelect')`),
              disabled: true
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
              'disabled': true
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
              'disabled': true
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
              'disabled': true
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
                 $self.query('siteInfosAfter')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)
            }
          }
        }
      },
      siteInfosAfter: {
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
            'x-reactions': methodExpression(`() => {
                const oldData = $form.query('siteInfosBefore').get('value')[$self.index]?.orgId || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'parent-id': -1,
              'node-type': 'OU',
              placeholder: i18nExpression('common.pleaseSelect'),
              'read-pretty': expression(`$form.readPretty`),
              multiple: false,
              '@select': expression(`(node) => {
                let row = $table.getRowByIndex($self.index)
                row.orgId = node ? node.organizationId : null
                row.orgCode = node ? node.organizationCode : null
                row.orgName = node ? node.organizationName : null
                if (node) {
                  app.$http({
                    url: '/api-base/organization/organization/get',
                    method: 'GET',
                    params: { organizationId: node.organizationId },
                    loading: true
                  }).then(res => {
                    if (res.data) {
                      row.belongOprId = res.data.erpOrgId
                    }
                  })
                }
              }`)
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
            'x-reactions': methodExpression(`() => {
                const oldData = $form.query('siteInfosBefore').get('value')[$self.index]?.vendorSiteCode || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'VENDOR_SITE_CODE',
              'disabled': expression(`$form.readPretty`)
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
            'x-reactions': methodExpression(`() => {
                const oldData = $form.query('siteInfosBefore').get('value')[$self.index]?.country || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'country',
              placeholder:  expression(`$t('common.pleaseSelect')`),
              'disabled': expression(`$form.readPretty`),
              '@change': expression(`(val) => {
                let row = $table.getRowByIndex($self.index)
                // 选择国外就清理省市区，并且禁用
                if (row.country !== 'CN') {
                  row.province = null
                  row.plantCity = null
                }
              }`)
            }
          },
          // 省
          province: {
            type: 'string',
            title: i18nExpression('components.address.area'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-reactions': methodExpression(`() => {
                const oldData = $form.query('siteInfosBefore').get('value')[$self.index]?.province || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PROVINCE',
              'custom-select-type':"PROVINCE",
              placeholder:  expression(`$t('common.pleaseSelect')`),
              disabled: expression(`$form.readPretty || $table.getRowByIndex($self.index).country!='CN'`)
            }
          },
          // 城市
          city: {
            type: 'string',
            title: i18nExpression('components.address.city'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-reactions': methodExpression(`() => {
                const oldData = $form.query('siteInfosBefore').get('value')[$self.index]?.city || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: expression(`$table.getRowByIndex($self.index).province`),
              'custom-select-type': "CITY",
              placeholder:  expression(`$t('common.pleaseSelect')`),
              disabled: expression(`$form.readPretty || $table.getRowByIndex($self.index).country!='CN'`)
            }
          },
          // 详细地址
          addressDetail:{
            type: 'string',
            title: i18nExpression('components.address.detailAddress'),
            'x-render-table-column': {
              minWidth: 150
            },
            'x-reactions': methodExpression(`() => {
                const oldData = $form.query('siteInfosBefore').get('value')[$self.index]?.addressDetail || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component-props': {
              'disabled': expression(`$form.readPretty`)
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
            'x-reactions': methodExpression(`() => {
                const oldData = $form.query('siteInfosBefore').get('value')[$self.index]?.postCode || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component-props': {
              'disabled': expression(`$form.readPretty`)
            }
          },
          // 地址备注
          siteComment:{
            type: 'string',
            title: i18nExpression('components.address.remark'),
            'x-render-table-column': {
              minWidth: 150
            },
            'x-reactions': methodExpression(`() => {
                const oldData = $form.query('siteInfosBefore').get('value')[$self.index]?.siteComment || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component-props': {
              'disabled': expression(`$form.readPretty`)
            }
          },
          // 启用
          enabledFlag: {
            type: 'string',
            title: i18nExpression('common.enable'),
            'x-render-table-column': {
              minWidth: 100
            },
            'x-reactions': methodExpression(`() => {
                const oldData = $form.query('siteInfosBefore').get('value')[$self.index]?.enabledFlag || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component': 'Checkbox',
            'x-component-props': {
              'true-label': "Y",
              'false-label': "N",
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
