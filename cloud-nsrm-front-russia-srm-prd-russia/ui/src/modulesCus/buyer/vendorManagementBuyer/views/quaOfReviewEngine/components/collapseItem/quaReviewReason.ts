import {
  expression,
  i18nExpression,
  generateXindexInOrder
} from '@meicloud/render-engine'

import {
  editTableFormItemValid
} from 'lib@/components/render-engine/schema-segments'

export default {
  type: 'void',
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: i18nExpression('vendorMod.quaReviewReason')
  },
  'x-reactions': expression(`field => {
    field.visible = $form.values.ifSiteForm === 'N'
  }`),
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
                  $self.query('.reviewFormExps').take().componentProps.componentInstance.addRow()
                }`)
          }
        }
      }
    },
    reviewFormExps: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        editMode: true,
        maxHeight: 400,
        pagination: false,
        sortable: false,
        // 联表主键的 key
        primaryKey: 'reviewFormExpId',
        // 启用级联删除的储值行为
        cascadeDeletion: true
      },
      'x-query-engine-skip': true,
      'x-query-engine-relation': 'reviewFormExps:*',
      properties: generateXindexInOrder({
        reviewFormExpId: {
          type: 'string',
          'x-hidden': true

        },
        reviewReason: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('vendorMod.reviewReason'), // 原因
            minWidth: 180
          },
          // 'x-component': 'DictSelect',
          // 'x-component-props': {
          //   code: 'REVIEW_REASON_TYPE'
          // },
          ...editTableFormItemValid
        },
        reasonExplain: {
          type: 'string',
          title: i18nExpression('vendorMod.reasonExplain'), // 原因描述
          'x-render-table-column': {
            minWidth: 200
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
