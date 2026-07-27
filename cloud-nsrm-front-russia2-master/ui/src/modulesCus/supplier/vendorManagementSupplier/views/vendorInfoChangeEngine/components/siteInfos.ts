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
          totalNumber: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.totalNum'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              disabled: true
            }
          },
          socialSecurityNumber: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.socialSecurity'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              disabled: true
            }
          },
          managementNumber: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.managerNum'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              disabled: true
            }
          },
          developerNumber: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.developmentNum'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              disabled: true
            }
          },
          productionNumber: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.productNum'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              disabled: true
            }
          },
          overUndergraduateNumber: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.bachelorDegreeOrAbove'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              disabled: true
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
      siteInfosAfter: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          editMode: true,
          maxHeight: 400,
          pagination: false,
          sortable: false,
          // 如果都没有标记，那么默认使用 id 作为联表主键的 key
          primaryKey: 'companySizeCahngeId',
          // 启用级联删除的储值行为
          cascadeDeletion: true
        },
        'x-query-engine-skip': true,
        'x-query-engine-relation': 'npmCompanySizeChanges:*',
        properties: generateXindexInOrder({
          totalNumber: {
            type: 'number',
            title: i18nExpression('cusEntry.vendorMod.totalNum'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-reactions': expression(`() => {
              const oldData = $form.query('siteInfosBefore').get('value')?.[$self.index]?.totalNumber || null
              let className = redFunction(oldData, $self?.value)
              $self.setComponentProps({ class: className })
            }`)
          },
          socialSecurityNumber: {
            type: 'number',
            title: i18nExpression('cusEntry.vendorMod.socialSecurity'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-reactions': expression(`() => {
              const oldData = $form.query('siteInfosBefore').get('value')?.[$self.index]?.socialSecurityNumber || null
              let className = redFunction(oldData, $self?.value)
              $self.setComponentProps({ class: className })
            }`)
          },
          managementNumber: {
            type: 'number',
            title: i18nExpression('cusEntry.vendorMod.managerNum'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-reactions': expression(`() => {
              const oldData = $form.query('siteInfosBefore').get('value')?.[$self.index]?.managementNumber || null
              let className = redFunction(oldData, $self?.value)
              $self.setComponentProps({ class: className })
            }`)
          },
          developerNumber: {
            type: 'number',
            title: i18nExpression('cusEntry.vendorMod.developmentNum'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-reactions': expression(`() => {
              const oldData = $form.query('siteInfosBefore').get('value')?.[$self.index]?.developerNumber || null
              let className = redFunction(oldData, $self?.value)
              $self.setComponentProps({ class: className })
            }`)
          },
          productionNumber: {
            type: 'number',
            title: i18nExpression('cusEntry.vendorMod.productNum'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-reactions': expression(`() => {
              const oldData = $form.query('siteInfosBefore').get('value')?.[$self.index]?.productionNumber || null
              let className = redFunction(oldData, $self?.value)
              $self.setComponentProps({ class: className })
            }`)
          },
          overUndergraduateNumber: {
            type: 'number',
            title: i18nExpression('cusEntry.vendorMod.bachelorDegreeOrAbove'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-reactions': expression(`() => {
              const oldData = $form.query('siteInfosBefore').get('value')?.[$self.index]?.overUndergraduateNumber || null
              let className = redFunction(oldData, $self?.value)
              $self.setComponentProps({ class: className })
            }`)
          }
        })
      }
    }
  }
}
