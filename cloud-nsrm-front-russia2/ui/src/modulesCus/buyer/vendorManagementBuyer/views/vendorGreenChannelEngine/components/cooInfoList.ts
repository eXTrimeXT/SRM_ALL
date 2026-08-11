import {
  expression,
  generateXindexInOrder,
  i18nExpression,
} from "@meicloud/render-engine";
import {editTableFormItemValid} from "lib@/components/render-engine";



export const cooInfoList = {
  cooInfo: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('supRisk.cooInfo'),
    },
    'x-query-engine-skip': true,
    properties: {
      toolbar: {
        type: 'void',
        'x-visible': expression('!$disabled'),
        'x-component': 'ButtonList',
        'x-component-props': {
          class: 'list-form__toolbar'
        },
        properties: {
          add: {
            type: 'void',
            title: i18nExpression('common.new'),
            'x-component-props': {
              type: 'primary',
              'disabled': expression(`$form.query('state').get('data').$disabled`),
              '@click': expression(`() => {
                 $self.query('orgCategorys')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)
            }
          }
        }
      },
      orgCategorys: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          editMode: expression('!$disabled'),
          maxHeight: 400,
          pagination: false,
          sortable: false,
          primaryKey: 'orgCategoryId',
          // 启用级联删除的储值行为
          cascadeDeletion: true
        },
        'x-query-engine-skip': true,
        properties: generateXindexInOrder({
          orgCode: {
            type: 'number',
            'x-hidden': true
          },
          orgName: {
            type: 'string',
            'x-hidden': true
          },
          // 组织
          orgId: {
            type: 'number',
            title: i18nExpression('components.userSelection.orgName'),
            'x-render-table-column': {
              minWidth: 120
            },
            default: null,
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'parent-id': -1,
              'node-type': 'OU',
              placeholder: i18nExpression('common.pleaseSelect'),
              multiple: false,
              'disabled': expression(`$disabled`),
              '@select': expression(`(node) => {
                let row = $table.getRowByIndex($self.index)
                row.orgId = node ? node.organizationId : null
                row.orgCode = node ? node.organizationCode : null
                row.orgName = node ? node.organizationName : null
              }`)
            },
            ...editTableFormItemValid
          },
          categoryId: {
            type: 'number',
            'x-hidden': true
          },
          categoryCode: {
            type: 'number',
            'x-hidden': true
          },
          // 采购品类
          categoryName: {
            type: 'string',
            title: i18nExpression('vendorMod.category'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component': 'CCategorySelect',
            'x-component-props': {
              showKey:"categoryName",
              placeholder: i18nExpression('vendorMod.msgCategoryNormalizer'),
              'disabled': expression(`$disabled`),
              '@select': expression(`(node) => {
                let row = $table.getRowByIndex($self.index)
                row.categoryId = node ? node.categoryId : null
                row.categoryName = node ? node.categoryName : ''
                row.categoryCode = node ? node.categoryCode : ''
              }`)
            },
            ...editTableFormItemValid
          },
          operation: {
            type: 'void',
            'x-visible': expression('!$disabled'),
            title: "{{$t('common.operation')}}",
            'x-render-table-column': {
              width: 60,
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
