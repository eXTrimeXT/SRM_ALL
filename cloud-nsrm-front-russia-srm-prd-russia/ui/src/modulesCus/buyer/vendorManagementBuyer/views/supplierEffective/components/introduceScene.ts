import {
  generateXindexInOrder,
  i18nExpression
} from '@meicloud/render-engine'

export const introduceScene = {
  introduceScene: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('cusEntry.vendorMod.introduceScene')
    },
    properties: {
      introduceSceneList: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          editMode: false,
          maxHeight: 250,
          pagination: false,
          sortable: false,
        },
        'x-query-engine-skip': true,
        'x-query-engine-relation': 'effectFormImportScenes:*',
        properties: generateXindexInOrder({
          importScene: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.vendorMod.introduceScene'),
              minWidth: 120
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'QUA_REVIEW_TYPE'
            }
          },
          ifReview: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.vendorMod.ifReview'),
              minWidth: 120
            },
            'x-component': 'Checkbox',
            'x-component-props': {
              trueLabel: 'Y',
              falseLabel: 'N'
            }
          },
          ifSite: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.vendorMod.ifSite'),
              minWidth: 120
            },
            'x-component': 'Checkbox',
            'x-component-props': {
              trueLabel: 'Y',
              falseLabel: 'N'
            }
          },
          ifSample: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.vendorMod.ifSample'),
              minWidth: 120
            },
            'x-component': 'Checkbox',
            'x-component-props': {
              trueLabel: 'Y',
              falseLabel: 'N'
            }
          },
          ifMaterial: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.vendorMod.ifMaterial'),
              minWidth: 120
            },
            'x-component': 'Checkbox',
            'x-component-props': {
              trueLabel: 'Y',
              falseLabel: 'N'
            }
          },
          ifEffective: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.vendorMod.ifEffective'),
              minWidth: 120
            },
            'x-component': 'Checkbox',
            'x-component-props': {
              trueLabel: 'Y',
              falseLabel: 'N'
            }
          }
        })
      }
    }
  }
}