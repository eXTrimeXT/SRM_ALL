import {
  expression,
  generateXindexInOrder,
  i18nExpression,
  generateCharExpressionByFunction
} from "@meicloud/render-engine";

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
                readonly: !($form.values.status === 'SUBMITTED' && $form.values.dataSources !== 'MANUALLY_CREATE' && $attrs.params.flag === 'edit')
              },
              on: {
                change: value => {
                  $form.values.extRejectAttribute7 = value
                }
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
      companySizes: {
        type: 'array',
        'x-component': 'RenderTable',
        default: expression(`[{type: $t('vendorMod.peopleNumber')}]`),
        'x-component-props': {
          preColumns: 'seq',
          editMode: false,
          maxHeight: 250,
          pagination: false,
          sortable: false,
          primaryKey: 'id',
          // 启用级联删除的储值行为
          cascadeDeletion: true
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
              }
            }
          },
          socialSecurityNumber: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.socialSecurity'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
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
