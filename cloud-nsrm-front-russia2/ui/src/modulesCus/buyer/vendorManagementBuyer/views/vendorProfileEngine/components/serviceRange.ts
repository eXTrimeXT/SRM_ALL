import {
  expression,
  generateXindexInOrder,
  i18nExpression
} from '@meicloud/render-engine'
import {
  requiredValidatorSegment
} from 'lib@/components/render-engine/schema-segments'

export const serviceRange = {
  serviceRange: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: `{{observer(
        {
          render(h) {
            return h($$components.Note, {
              props: {
                title: t('cusEntry.vendorMod.serviceRange'),
                value: $form.values.extRejectAttribute8,
                readonly: !($form.values.status === 'SUBMITTED' && $form.values.dataSources !== 'MANUALLY_CREATE' && $attrs.params.flag === 'edit')
              },
              on: {
                change: value => {
                  $form.values.extRejectAttribute8 = value
                }
              }
            })
          }
        }
      )}}`
    },
    'x-query-engine-skip': true,
    properties: {
      toolbar: {
        type: 'void',
        'x-visible': false,
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
              'x-hidden': "{{ JSON.stringify($self.value) === '{}' }}",
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
                        name: 'scc_base_purchase_category4',
                        '@close-quicksearch': expression(`val => {
                          let form = $form.query($self.parent.parent.address.toString()).take().value
                          form.categoryCode = val?.categoryCode || ''
                          form.categoryName = val?.categoryName || ''
                          form.categoryId = val?.categoryId || ''
                        }`),
                        disabled: true
                      },
                      title: '{{t(\'cusEntry.vendorMod.category\', {index: $self.index + 1})}}'
                    },
                    // contactName: {
                    //   type: 'string',
                    //   'x-decorator': 'FormItem',
                    //   title: i18nExpression('cusEntry.vendorMod.contacts'),
                    //   'x-component-props': {
                    //     disabled: true
                    //   }
                    // },
                    formBtn: {
                      type: 'void',
                      'x-visible': false,
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
                              $form.query('serviceRangeList').take(field => {
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
                editMode: false,
                maxHeight: 250,
                pagination: false,
                sortable: false
              },
              'x-hidden': "{{ JSON.stringify($self.query('.tableForm').take().value) === '{}' }}",
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
                  'x-visible': false,
                  title: i18nExpression('common.operation'),
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
      },
      pagination: {
        type: 'void',
        'x-component': 'ElPagination',
        'x-component-props': {
          pageSize: 10,
          layout:"total, prev, pager, next",
          total: expression('$form.query(\'CompanyInfo\').get(\'data\')?.totalServiceRangeList?.length'),
          '@current-change': expression(`(value) => {
            const totalServiceRangeList = $form.query(\'CompanyInfo\').get(\'data\')?.totalServiceRangeList || []
            const showList = totalServiceRangeList.slice((value - 1) * 10, value * 10)
            // 暂时解决渲染引擎底层存在的bug
            if (showList.length !== 10) {
              for (let i = showList.length; i < 10; i++) {
                showList.push({ tableForm: {}, list: [] })
              }
            }
            $form.query('serviceRangeList').take().value = showList
          }`)
        }
      }
    }
  }
}