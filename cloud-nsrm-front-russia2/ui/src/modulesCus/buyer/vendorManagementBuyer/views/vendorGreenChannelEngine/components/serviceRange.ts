import {
  expression,
  generateXindexInOrder,
  i18nExpression,
  generateCharExpressionByFunction
} from '@meicloud/render-engine'
import {
  requiredValidatorSegment
} from 'lib@/components/render-engine/schema-segments'
import {
  editTableFormItemValid,
} from "lib@/components/render-engine";
export const serviceRange = {
  serviceRange: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('cusEntry.vendorMod.serviceRange'),
    },
    'x-visible': generateCharExpressionByFunction(({ $form }) => {
      return !$form.query('state').get('data').isSimple
    }),
    'x-query-engine-skip': true,
    properties: {
      toolbar: {
        type: 'void',
        'x-component': 'ButtonList',
        'x-component-props': {
          class: 'list-form__toolbar'
        },
        'x-visible': expression(`!$form.query('state').get('data').$disabled`),
        properties: {
          add: {
            type: 'void',
            title: i18nExpression('common.add'),
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
                $form.query('serviceRangeList').take(field => {
                  field.invoke('add', 'push')
                })
              }`)
            }
          }
        }
      },
      serviceRangeList: {
        type: 'array',
        'x-component': 'ArrayItems',
        items: {
          type: 'void',
          properties: {
            tableForm: {
              type: 'object',
              properties: {
                layout: {
                  type: 'void',
                  'x-decorator': 'FormLayout',
                  'x-decorator-props': {
                    layout: 'vertical'
                  },
                  'x-component': 'FormGrid',
                  'x-component-props': {
                    maxColumns: 3,
                    columnGap: 32,
                    rowGap: 0
                  },
                  properties: {
                    categoryName: {
                      type: 'string',
                      'x-decorator': 'FormItem',
                      'x-component': 'QuickSearchWrapper',
                      'x-component-props': {
                        dialogLabel: i18nExpression('cusEntry.vendorMod.categoryNameTitle'),
                        showKey: 'categoryName',
                        name: 'scc_base_purchase_category4',
                        '@close-quicksearch': expression(`val => {
                          let list = $form.query('serviceRangeList').get('value')
                          let flag = false
                          for(let item of list){
                            item.tableForm.categoryCode == val.categoryCode && (flag = true)
                          }
                          if(flag){
                            // 服务范围内已经存在该品类
                            app.$message.error($t('cusEntry.vendorMod.serviceRangeCateTips'))
                            $form.query('serviceRangeList').take(field => {
                              field.remove($self.index)
                            })
                            return
                          }
                          let form = $form.query($self.parent.parent.address.toString()).take().value
                          form.categoryCode = val?.categoryCode || ''
                          form.categoryName = val?.categoryName || ''
                          form.categoryId = val?.categoryId || ''
                          const [oneLevel, twoLevel] = val?.categoryFullName?.split('-')
                          form.categoryFullName = oneLevel + '-' + twoLevel || ''
                        }`),
                        disabled: expression(`$form.query('state').get('data').$disabled`)
                      },
                      title: '{{t(\'cusEntry.vendorMod.category\', {index: $self.index + 1})}}'
                    },
                    // contactName: {
                    //   type: 'string',
                    //   'x-decorator': 'FormItem',
                    //   title: i18nExpression('cusEntry.vendorMod.contacts')
                    // },
                    formBtn: {
                      type: 'void',
                      'x-component': 'ButtonList',
                      'x-component-props': {
                        style: {
                          'margin-top': '5px'
                        }
                      },
                      'x-visible': expression(`!$form.query('state').get('data').$disabled`),
                      properties: {
                        add: {
                          type: 'void',
                          'x-component-props': {
                            type: 'primary',
                            '@click': expression(`() => {
                              $form.query($self.parent.parent.parent.parent.address.concat($self.index).concat('list')).take(field => {
                                field.componentProps.componentInstance.addRow()
                              })
                            }`)
                          },
                          title: i18nExpression('cusEntry.common.addCustomer')
                        },
                        delete: {
                          type: 'void',
                          title: i18nExpression('cusEntry.common.deleteCategory'),
                          'x-component-props': {
                            type: 'primary',
                            '@click': expression(`() => {
                              const { categoryJournalId = null } = $form.query('serviceRangeList').get('value')[$self.index]?.tableForm || {}
                              $form.query('serviceRangeList').take(field => {
                                const name = 'cateJournalList'
                                if (categoryJournalId) {
                                  if (!$queryEngine.dataCollection.value.relationTableCascadeDeletions[name]) {
                                    $queryEngine.dataCollection.value.relationTableCascadeDeletions[name] = new Set()
                                  }
                                  $queryEngine.dataCollection.value.relationTableCascadeDeletions[name].add(categoryJournalId)
                                }
                                field.remove($self.index)
                              })
                            }`)
                          }
                        }
                      }
                    }
                  }
                }
              }
            },
            list: {
              type: 'array',
              'x-component': 'RenderTable',
              'x-query-engine-skip': true,
              'x-component-props': {
                preColumns: 'seq',
                editMode: expression(`!$form.query('state').get('data').$disabled`),
                maxHeight: 250,
                pagination: false,
                sortable: false
              },
              properties: generateXindexInOrder({
                //业绩
                performanceAmount: {
                  type: 'number',
                  title: i18nExpression('cusEntry.vendorMod.performance'),
                  'x-render-table-column': {
                    minWidth: 120
                  }
                },
                mainCustom: {
                  type: 'string',
                  title: i18nExpression('cusEntry.vendorMod.mainCustomer'),
                  'x-render-table-column': {
                    minWidth: 120
                  }
                },
                fileId: {
                  type: 'string',
                  title: i18nExpression('cusEntry.vendorMod.achievement'),
                  'x-render-table-column': {
                    minWidth: 120
                  },
                  'x-component': 'SrmCommonFile',
                  'x-component-props': {
                    extraData: {
                      fileModular: 'sup',
                      fileFunction: 'companyInfoMaintain',
                      fileType: 'images'
                    },
                    defaultFile: {
                      fileId: expression(`$table.getRowByIndex($self.index)?.fileId`),
                      fileName: expression('$table.getRowByIndex($self.index)?.fileName')
                    },
                    readonly: false,
                    '@on-change': expression(`({file}) => {
                      const { fileId = '', fileName = '' } = file || {}
                      let row = $table.getRowByIndex($self.index)
                      row.fileId = fileId
                      row.fileName = fileName
                    }`)
                  }
                },
                operation: {
                  type: 'void',
                  title: i18nExpression('common.operation'),
                  'x-render-table-column': {
                    width: 60,
                    fiexd: 'right'
                  },
                  'x-visible': expression(`!$form.query('state').get('data').$disabled`),
                  'x-component': 'RenderTableButtonList',
                  properties: {
                    delete: {
                      type: 'void',
                      title: i18nExpression('common.delete'),
                      'x-component-props': {
                        type: 'text',
                        '@click': expression(`() => {
                          const { serciceCustomId = null } = $table.getRowByIndex($self.index) || {}
                          if (serciceCustomId) {
                            let serciceCustomDelList = $form.query('state').get('data').serciceCustomDelList
                            serciceCustomDelList.push({
                              $delete: serciceCustomId
                            })
                          }
                          $table.remove($self.index)
                        }`)
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
  }
}