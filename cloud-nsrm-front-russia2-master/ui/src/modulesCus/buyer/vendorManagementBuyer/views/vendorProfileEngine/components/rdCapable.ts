import {
  expression,
  generateXindexInOrder,
  i18nExpression,
} from "@meicloud/render-engine";

export const rdCapableList = {
  rdCapableList: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('vendorMod.RandDCapable5'),
    },
    'x-query-engine-skip': true,
    properties: {
      rdCapables: {
        type: 'array',
        'x-component': 'RenderTable',
        default: expression(`[{type: $t('vendorMod.peopleNumber')}]`),
        'x-component-props': {
          class: 'rdCapableList',
          preColumns: '',
          editMode: true,
          height: '100px',
          pagination: false,
          sortable: false
        },
        'x-query-engine-skip': true,
        properties: generateXindexInOrder({
          // 类型
          type: {
            type: 'string',
            title: i18nExpression(''),
            'x-render-table-column': {
              width: '90px'
            },
            'x-read-pretty': true,
            'x-component-props': {
              'disabled': expression(`$disabled`)
            }
          },
          // 研发人员数量
          rdQuantity: {
            type: 'string',
            title: i18nExpression('vendorMod.RDPersonNum'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              'disabled': expression(`$disabled`)
            }
          },
          // 高级工程师数量
          seniorEngineerQuantity: {
            type: 'string',
            title: i18nExpression('vendorMod.seniorEngineers'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              'disabled': expression(`$disabled`)
            }
          },
          // 工程师数量
          engineerQuantity: {
            type: 'string',
            title: i18nExpression('vendorMod.engineers'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              'disabled': expression(`$disabled`)
            }
          }
        })
      },
      rdCapableAdditionals: {
        type: 'void',
        'x-decorator': 'FormLayout',
        'x-decorator-props': {
          layout: 'vertical',
        },
        'x-component': 'FormGrid',
        'x-component-props': {
          maxColumns: 3,
          columnGap: 32,
          rowGap: 0
        },
        properties: {
          // 是否建立企业技术标准
          'ifTechnicalStandard': {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component': 'Radio.Group',
            enum: [
              {
                label: i18nExpression('common.yes'),
                value: 'Y'
              },
              {
                label: i18nExpression('common.no'),
                value: 'N'
              }
            ],
            'x-component-props': {
              'disabled': expression(`$disabled`)
            },
            title: i18nExpression('vendorMod.ifTechnicalStandard')
          },
          // 产品采用技术标准
          productsTechnicalStandard: {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component': 'DictSelect',
            'x-component-props': {
              'disabled': expression(`$disabled`),
              code: 'INS_TYPE'
            },
            title: i18nExpression('vendorMod.productsTechnicalStandard')
          },
          // 产品技术标准说明
          memo: {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component': 'Input',
            title: i18nExpression('vendorMod.memo'),
            'x-component-props': {
              'disabled': expression(`$disabled`),
              type:"textarea"
            }
          }
        }
      }
    }
  },

}
