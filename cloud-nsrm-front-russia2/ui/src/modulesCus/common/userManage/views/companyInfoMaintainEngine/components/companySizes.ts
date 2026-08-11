import {
  generateXindexInOrder,
  i18nExpression,
  expression,
  generateCharExpressionByFunction
} from "@meicloud/render-engine"

export const companySizesList = {
  companySizesList: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: `{{observer(
        {
          render(h) {
            return h($$components.Note, {
              props: {
                title: t('vendorMod.companySize'),
                value: $form.values.extRejectAttribute7,
                readonly: true
              }
            })
          }
        }
      )}}`
    },
    'x-query-engine-skip': true,
    'x-visible': generateCharExpressionByFunction(({ $form }) => {
      return $form.query('state').get('data').overseasRelation !== 'PERSONAL'
    }),
    properties: {
      companySizes: {
        type: 'array',
        'x-component': 'RenderTable',
        default: expression(`[{type: $t('vendorMod.peopleNumber')}]`),
        'x-component-props': {
          preColumns: 'seq',
          editMode: true,
          maxHeight: 250,
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
              'v-input-format': {
                type: 'integer', 
                negative: false
              },
              disabled: expression(`$form.query('state').get('data').$disabled`)
            }
          },
          socialSecurityNumber: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.socialSecurity'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              disabled: expression(`$form.query('state').get('data').$disabled`),
              'v-input-format': {
                type: 'integer', 
                negative: false
              }
            }
          },
          managementNumber: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.managerNum'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              disabled: expression(`$form.query('state').get('data').$disabled`),
              'v-input-format': {
                type: 'integer', 
                negative: false
              }
            }
          },
          developerNumber: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.developmentNum'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              disabled: expression(`$form.query('state').get('data').$disabled`),
              'v-input-format': {
                type: 'integer', 
                negative: false
              }
            }
          },
          productionNumber: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.productNum'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              disabled: expression(`$form.query('state').get('data').$disabled`),
              'v-input-format': {
                type: 'integer', 
                negative: false
              }
            }
          },
          overUndergraduateNumber: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.bachelorDegreeOrAbove'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              disabled: expression(`$form.query('state').get('data').$disabled`),
              'v-input-format': {
                type: 'integer', 
                negative: false
              }
            }
          }
        })
      }
    }
  }
}
