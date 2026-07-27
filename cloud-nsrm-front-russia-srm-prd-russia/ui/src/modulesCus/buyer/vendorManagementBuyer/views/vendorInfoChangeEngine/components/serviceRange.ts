import {expression, generateXindexInOrder, i18nExpression} from '@meicloud/render-engine'
import {
  requiredValidatorSegment
} from 'lib@/components/render-engine/schema-segments'
import {
  editTableFormItemValid,
} from "lib@/components/render-engine";
export const serviceRange = {
  beforeChange: {
    type: 'void',
    'x-component': 'div',
    'x-component-props': {
      class: ''
    },
    properties: {
      // 变更前
      beforeChangeTitle: {
        type: 'void',
        'x-component': 'changeTitle',
        'x-component-props': {
          language: 'supplierChange.beforeChange'
        }
      },
      serviceRangeBefore: {
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
                        showKey: 'categoryName',
                        disabled: true,
                        name: 'scc_base_purchase_category4',
                        '@close-quicksearch': expression(`val => {
                          let row = $table.getRowByIndex($self.index)
                          row.categoryCode = val?.categoryCode || ''
                          row.categoryName = val?.categoryName || ''
                          row.categoryId = val?.categoryId || ''
                        }`)
                      },
                      title: '{{t(\'cusEntry.vendorMod.category\', {index: $self.index + 1})}}'
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
                editMode: false,
                maxHeight: 250,
                pagination: false,
                sortable: false
              },
              properties: generateXindexInOrder({
                //业绩
                performanceAmount: {
                  type: 'string',
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
                  },
                  'x-validator': {
                    required: true,
                    message: i18nExpression('cusEntry.tipMessage.required')
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
                      fileId: expression(`$table.getRowByIndex($self.index)?.fileuploadId`),
                      fileName: expression('$self?.value')
                    },
                    readonly: true
                  }
                }
              })
            }
          }
        }
      }
    }
  },
  afterChange: {
    type: 'void',
    'x-component': 'div',
    'x-component-props': {
      class: ''
    },
    properties: {
      // 变更后
      afterChangeTitle: {
        type: 'void',
        'x-component': 'changeTitle',
        'x-component-props': {
          language: 'supplierChange.afterChange'
        }
      },
      toolbar: {
        type: 'void',
        'x-visible': expression('!$form.readPretty'),
        'x-component': 'ButtonList',
        'x-component-props': {
          class: 'list-form__toolbar'
        },
        properties: {
          add: {
            type: 'void',
            title: i18nExpression('common.add'),
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
                $form.query('serviceRangeAfter').take(field => {
                  field.invoke('add', 'push')
                })
              }`)
            }
          }
        }
      },
      serviceRangeAfter: {
        type: 'array',
        'x-component': 'ArrayItems',
        'x-query-engine-skip': true,
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
                        name: 'scc_base_purchase_category2',
                        '@close-quicksearch': expression(`val => {
                          let list = $form.query('serviceRangeAfter').get('value')
                          let flag = false
                          for(let item of list){
                            item.tableForm.categoryCode == val.categoryCode && (flag = true)
                          }
                          if(flag){
                            app.$message.error('服务范围内已经存在该品类')
                            $form.query('serviceRangeAfter').take(field => {
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
                        }`)
                      },
                      'x-reactions': expression(`() => {
                        const oldData = $form.query('serviceRangeBefore').get('value')?.[$self.index]?.tableForm?.categoryName || null
                        let className = redFunction(oldData, $self?.value)
                        $self.setComponentProps({ class: className })
                      }`),
                      ...requiredValidatorSegment,
                      title: '{{t(\'cusEntry.vendorMod.category\', {index: $self.index + 1})}}'
                    },
                    formBtn: {
                      type: 'void',
                      'x-visible': expression('!$form.readPretty'),
                      'x-component': 'ButtonList',
                      'x-component-props': {
                        style: {
                          'margin-top': '5px'
                        }
                      },
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
                              const { categoryJournalChangeId = null } = $form.query('serviceRangeAfter').get('value')[$self.index]?.tableForm || {}
                              $form.query('serviceRangeAfter').take(field => {
                                const name = 'npmCateJournalChanges'
                                if (categoryJournalChangeId) {
                                  if (!$queryEngine.dataCollection.value.relationTableCascadeDeletions[name]) {
                                    $queryEngine.dataCollection.value.relationTableCascadeDeletions[name] = new Set()
                                  }
                                  $queryEngine.dataCollection.value.relationTableCascadeDeletions[name].add(categoryJournalChangeId)
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
              'x-component-props': {
                preColumns: 'seq',
                editMode: true,
                maxHeight: 250,
                pagination: false,
                sortable: false
              },
              'x-query-engine-skip': true,
              properties: generateXindexInOrder({
                //业绩
                performanceAmount: {
                  type: 'number',
                  title: i18nExpression('cusEntry.vendorMod.performance'),
                  'x-render-table-column': {
                    minWidth: 120
                  },
                  'x-reactions': expression(`() => {
                    const parentIndex = $self.parent?.index
                    const oldData = $form.query('serviceRangeBefore').get('value')?.[parentIndex]?.list?.[$self.index]?.performanceAmount || null
                    let className = redFunction(oldData, $self?.value)
                    $self.setComponentProps({ class: className })
                  }`)
                },
                mainCustom: {
                  type: 'string',
                  title: i18nExpression('cusEntry.vendorMod.mainCustomer'),
                  'x-render-table-column': {
                    minWidth: 120
                  },
                  'x-validator': {
                    required: true,
                    message: i18nExpression('cusEntry.tipMessage.required')
                  },
                  'x-reactions': expression(`() => {
                    const parentIndex = $self.parent?.index
                    const oldData = $form.query('serviceRangeBefore').get('value')?.[parentIndex]?.list?.[$self.index]?.mainCustom || null
                    let className = redFunction(oldData, $self?.value)
                    $self.setComponentProps({ class: className })
                  }`)
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
                    readonly:false,
                    '@on-change': expression(`({file}) => {
                      const { fileId = '', fileName = '' } = file || {}
                      let row = $table.getRowByIndex($self.index)
                      row.fileId = fileId
                      row.fileName = fileName
                    }`)
                  },
                  'x-reactions': expression(`() => {
                    const parentIndex = $self.parent?.index
                    const oldData = $form.query('serviceRangeBefore').get('value')?.[parentIndex]?.list?.[$self.index]?.fileId || null
                    let className = redFunction(oldData, $self?.value)
                    $self.setComponentProps({ class: className })
                  }`),
                  ...editTableFormItemValid
                },
                operation: {
                  type: 'void',
                  title: i18nExpression('common.operation'),
                  'x-visible': expression('!$form.readPretty'),
                  'x-render-table-column': {
                    width: 60,
                    fiexd: 'right'
                  },
                  'x-component': 'RenderTableButtonList',
                  properties: {
                    delete: {
                      type: 'void',
                      title: i18nExpression('common.delete'),
                      'x-component-props': {
                        type: 'text',
                        '@click': expression(`() => {
                          const { serciceCustomChangeId = null } = $table.getRowByIndex($self.index) || {}
                          const name = 'npmSerciceCustomChanges'
                          if (serciceCustomChangeId) {
                            let serciceCustomDelList = $form.query('state').get('data').serciceCustomDelList
                            serciceCustomDelList.push({
                              $delete: serciceCustomChangeId
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
    },
  }
}