import {
  i18nExpression,
  generateXindexInOrder
} from "@meicloud/render-engine"

export const userInfoForm = {
  userInfoForm: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('vendorMod.vendorUserInfo')
    },
    'x-query-engine-skip': true,
    properties: {
      userInfo: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          editMode: false,
          maxHeight: 250,
          pagination: false,
          sortable: false
        },
        'x-query-engine-skip': true,
        properties: generateXindexInOrder({
          // 用户名
          username: {
            type: 'string',
            title: i18nExpression('vendorMod.username'),
            'x-render-table-column': {
              minWidth: 120
            }
          },
          nickname: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.nickname'),
            'x-render-table-column': {
              minWidth: 120
            }
          },
          phone: {
            type: 'string',
            title: i18nExpression('vendorMod.contactMethod'),
            'x-render-table-column': {
              minWidth: 120
            }
          },
          email: {
            type: 'string',
            title: i18nExpression('common.email'),
            'x-render-table-column': {
              minWidth: 120
            }
          },
          ceeaJobcodeDescr: {
            type: 'string',
            title: i18nExpression('dataConfMod.position'),
            'x-render-table-column': {
              minWidth: 120
            }
          },
          role: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.role'),
            'x-render-table-column': {
              minWidth: 120
            }
          },
          mainType: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.isMainAccount'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            }
          }
        })
      }
    }
  }
}
