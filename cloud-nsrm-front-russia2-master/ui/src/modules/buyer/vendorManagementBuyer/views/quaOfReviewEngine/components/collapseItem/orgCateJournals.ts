import {
  expression,
  i18nExpression,
  queryFieldValueExpression,
  generateXindexInOrder
} from '@meicloud/render-engine'
import {
  requiredValidatorSegment,
  editTableFormItemValid
} from 'lib@/components/render-engine/schema-segments'

export default {

  type: 'void',
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: i18nExpression('vendorMod.orgCateJournals') // 引入组织和品类
  },

  properties: {
    org: {
      type: 'void',
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
              'x-component': 'OrganizationSelector',
              'x-component-props': {
                placeholder: i18nExpression('common.pleaseSelect'),
                'parent-id': -1,
                'multiple': true,
                'defaultValue': queryFieldValueExpression('orgJournals'),
                'node-type': 'OU',
                'select-type': 'button',
                '@select': expression('(node) => $organizationSelectHandel(node,$form, $message)')
              }
            }
          }

        },
        orgJournals: {
          type: 'array',
          'x-component': 'RenderTable',
          'x-component-props': {
            maxHeight: 400,
            preColumns: 'seq',
            pagination: false,
            sortable: false,
            // 联表主键的 key
            primaryKey: 'orgJournalId',
            // 启用级联删除的储值行为
            cascadeDeletion: true
          },
          'x-query-engine-skip': true,
          'x-query-engine-relation': 'orgJournals:*',
          properties: generateXindexInOrder({
            orgJournalId: {
              type: 'string',
              'x-hidden': true

            },
            formType: {
              type: 'string',
              'x-hidden': true,
              default: 'REVIEW_FORM'
            },
            orgName: {
              type: 'string',
              title: i18nExpression('vendorMod.addOrg'), // 引入组织
              'x-render-table-column': {
                minWidth: 100,
                align: 'center'
              },
              ...editTableFormItemValid
            },
            operation: {
              type: 'void',
              title: i18nExpression('common.operation'),
              'x-render-table-column': {
                width: 100,
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
                        ({ rowIndex }) => {
                          $table.remove($self.index)
                          const row = $table.getRowByIndex(rowIndex)
                          $delOrgJournals($form, row, rowIndex)
                        }
                      `)
                  }
                }
              }
            }
          })
        }
      }

    },
    cate: {
      type: 'void',
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
              'x-component': 'CCategorySelect',
              'x-component-props': {
                placeholder: i18nExpression('common.pleaseSelect'),
                'multiple': true,
                'select-type': 'button',
                '@select': expression('(data) => $addCategorysList(data,$form,$message)')
              }
            }
          }

        },
        cateJournals: {
          type: 'array',
          'x-component': 'RenderTable',
          'x-component-props': {
            editMode: true,
            maxHeight: 400,
            preColumns: 'seq',
            pagination: false,
            sortable: false,
            // 联表主键的 key
            primaryKey: 'categoryJournalId',
            // 启用级联删除的储值行为
            cascadeDeletion: true
          },
          'x-query-engine-skip': true,
          'x-query-engine-relation': 'cateJournals:*',
          'x-read-pretty': true,
          properties: generateXindexInOrder({
            categoryJournalId: {
              type: 'string',
              'x-hidden': true

            },
            formType: {
              type: 'string',
              'x-hidden': true,
              default: 'REVIEW_FORM'
            },
            categoryName: {
              type: 'string',
              title: i18nExpression('vendorMod.addCategory'), // 引入品类
              'x-render-table-column': {
                minWidth: 100
              },
              ...editTableFormItemValid
            },
            thisYearAmount: {
              type: 'number',
              title: i18nExpression('vendorMod.thisYearAmount'), // 品类本年度采购金额（万元）
              default: 0,
              'x-component-props': {
                controls: false
              },
              'x-render-table-column': {
                minWidth: 150
              },
              'x-read-pretty': expression('$form.readPretty')
            },
            existCountOfCompany: {
              type: 'string',
              title: i18nExpression('vendorMod.existCountOfCompany'), // 当前供应商数量
              'x-render-table-column': {
                minWidth: 100
              }
            },
            supplierCountLimit: {
              type: 'string',
              title: i18nExpression('vendorMod.supplierCountLimit'), // 供应商数量上限
              'x-render-table-column': {
                minWidth: 100
              }
            },
            operation: {
              type: 'void',
              title: i18nExpression('common.operation'),
              'x-render-table-column': {
                width: 150,
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
                        ({rowIndex}) => {
                          $table.remove($self.index)
                          $delCateJournals($form, rowIndex)
                        }
                      `)
                  }
                },
                qualifications: {
                  type: 'void',
                  title: i18nExpression('vendorMod.qualificationCriteriaView'), // 查看资质标准
                  'x-component-props': {
                    type: 'text',
                    '@click': expression(`
                        ({ rowIndex }) => {
                          const row = $table.getRowByIndex(rowIndex)
                          $qualifications(row,$form,$message)
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

  }
}
