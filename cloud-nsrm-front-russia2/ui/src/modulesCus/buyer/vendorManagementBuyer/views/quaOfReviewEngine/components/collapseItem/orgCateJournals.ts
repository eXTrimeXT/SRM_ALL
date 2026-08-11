import {
  expression,
  i18nExpression,
  queryFieldValueExpression,
  generateXindexInOrder
} from '@meicloud/render-engine'
import {
  requiredValidatorSegment,
  buttonListItemVisibleByPermission,
  editTableFormItemValid
} from 'lib@/components/render-engine/schema-segments'

export default {

  type: 'void',
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: i18nExpression('cusEntry.vendorMod.orgCateJournals') // 引入组织和品类
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
              title: i18nExpression('cusEntry.vendorMod.addCompany'), // 引入组织
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
                          const row = $table.getRowByIndex($self.index)
                          $table.remove($self.index)
                          $delOrgJournals($form, row, rowIndex)
                        }
                      `)
                  }
                }
              }
            }
          })
        },
        page: {
          type: 'void',
          'x-component': 'CPagination',
          'x-component-props': {
            total: expression('$form.query(\'ReviewForm\').get(\'data\').orgLength'),
            'page-num': expression('$form.query(\'ReviewForm\').get(\'data\').pageNum'),
            'page-size': expression('$form.query(\'ReviewForm\').get(\'data\').pageSize'),
            '@current-change': expression(`pageNum => {
              const formData = $form.query(\'ReviewForm\').get(\'data\')
              formData.pageNum = pageNum
              const pageSize = formData.pageSize
              $form.values.orgJournals = formData.allOrg.slice((pageNum - 1) * pageSize, pageNum * pageSize)
            }`),
            '@size-change': expression(`pageSize => {
              const formData = $form.query(\'ReviewForm\').get(\'data\')
              formData.pageSize = pageSize
              const pageNum = formData.pageNum
              $form.values.orgJournals = formData.allOrg.slice((pageNum - 1) * pageSize, pageNum * pageSize)
            }`)
          }
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
              'x-component': 'QuickSearchWrapper',
              'x-component-props': {
                name: 'ceea_sup_auth_cate_journal_supplier',
                showButton: true,
                'pre-query-data': expression(`{'t.form_id': $form.values.vendorId,'m.last_level_flag':'Y'}`),
                multiSelect: true,
                btnTitle: i18nExpression('cusEntry.common.categorySelect'),
                '@close-quicksearch': expression(`data => $addCategorysList(data,$form,$message)`),
                '@before-open': expression(`(params, callback) => {
                  if (!$form.values.vendorId) {
                    $message.warning($t('cusEntry.tipMessage.selectVendor'))
                    callback()
                  }
                }`)
              }
            },
            addAll: {
              type: 'void',
              title: i18nExpression('common.add'),
              'x-component': 'CategorySelect',
              'x-component-props': {
                placeholder: i18nExpression('common.pleaseSelect'),
                ...buttonListItemVisibleByPermission('sup:quaOfReviewDetail:categorySelectAll'),
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
                    '@click': expression(`({rowIndex}) => {
                      const row = $table.getRowByIndex($self.index)
                      $table.remove($self.index)
                      $delCateJournals($form, row, rowIndex)
                    }`)
                  }
                }
              }
            }
          })
        },
        page: {
          type: 'void',
          'x-component': 'CPagination',
          'x-component-props': {
            total: expression('$form.query(\'ReviewForm\').get(\'data\').catLength'),
            'page-num': expression('$form.query(\'ReviewForm\').get(\'data\').CatPageNum'),
            'page-size': expression('$form.query(\'ReviewForm\').get(\'data\').catPageSize'),
            '@current-change': expression(`pageNum => {
              const formData = $form.query(\'ReviewForm\').get(\'data\')
              formData.catPageNum = pageNum
              const pageSize= formData.catPageSize
              $form.values.cateJournals = formData.allCat.slice((pageNum - 1) * pageSize, pageNum * pageSize)
            }`),
            '@size-change': expression(`pageSize => {
              const formData = $form.query(\'ReviewForm\').get(\'data\')
              formData.catPageSize = pageSize
              const pageNum = formData.catPageNum
              $form.values.cateJournals = formData.allCat.slice((pageNum - 1) * pageSize, pageNum * pageSize)
            }`)
          }
        }
      }

    }

  }
}
