import dialogAttributes from './dialog.js'

export default {
  components: {},
  scope: () => {},
  patches: {
    add: {
      newDialog: dialogAttributes,
      ContractHead: {
        SchemaWorkflow: {
          layout: {
            collapse: {
              contractInfo: {
                layout: {
                  contractHeadId: {
                    __field__: true,
                    type: 'string',
                    title: '合同ID',
                    'x-index': 1,
                    'x-decorator': 'FormItem',
                    'x-decorator-props': {
                      'class': 'patches'
                    }
                  },
                }
              },
              testArea: {
                __field__: true,
                type: 'void',
                'x-component': 'CollapseItem',
                'x-component-props': {
                  title: '【新增】测试区域'
                },
                properties: {
                  PerPlan: {
                    type: 'void',
                    'x-query-engine': {
                      actions: {
                        paginationQuery: { immediate: true },
                      }
                    },
                    'x-component': 'QueryEngine',
                    properties: {
                      mockTable: {
                        type: 'array',
                        'x-component': 'RenderTable',
                        'x-query-engine-skip': true,
                        // 'x-query-engine-relation': 'annexes:*',
                        'x-read-pretty': true,
                        'x-component-props': {
                          preColumns: 'seq',
                          maxHeight: 400,
                          pagination: false,
                          sortable: false,
                        },
                        
                        properties: {
                          perPlanId: {
                            type: 'string',
                            title: "合同计划ID",
                            'x-render-table-column': {
                              minWidth: 200
                            },
                            'x-query-engine-primary-key': true
                          },
                          perPlanNo: {
                            type: 'string',
                            title: "合同计划编码",
                            'x-render-table-column': {
                              minWidth: 200
                            }
                          },
                          contractNo: {
                            type: 'string',
                            title: "合同编码",
                            'x-render-table-column': {
                              minWidth: 200
                            }
                          },
                          status: {
                            type: 'string',
                            title: "状态",
                            'x-component': 'DictSelect',
                            'x-component-props': {
                              code: 'CONTRACT_PLAN_STATUS'
                            },
                            'x-render-table-column': {
                              minWidth: 150
                            }
                          },                      
                        }
                      }
                    }
                  },

                },
              }
            }
          }
        }
      }
    },
    update: {
      ContractHead: {
        SchemaWorkflow: {
          layout: {
            __field__: true,
            items: {
              properties: {
                openDialog: {
                  type: 'void',
                  'x-content': "【新增】按钮",
                  'x-component': 'Button',
                  'x-component-props': {
                    'class': 'patches',
                    '@click': `{{
                      () => {
                        // $form.query('newDialog.a').take().setComponentProps({ value: "您好，详情" })
                        console.log('==newDialog.a==', $form.query('newDialog').take())
                        $form.query('newDialog').take().setComponentProps({ visible: true })
                      }
                    }}`
                  }
                }
              }
            },
            collapse: {
              contractInfo: {
                layout: {
                  contractNo: {
                    __field__: true,
                    title: '【修改】合同编号',
                    'x-decorator-props': {
                      'class': 'patches'
                    }
                  },
                }
              },

            }
          }
        }
      }
    },
    remove: {
      'ContractHead.SchemaWorkflow.layout.collapse.contractInfo.layout.signingAddress': true
    },
  },
}
