import {
  expression,
  generateXindexInOrder,
  i18nExpression
} from '@meicloud/render-engine'

import { formMain } from 'modb@/vendorManagementBuyer/views/financialInforChangesEngine/components/form'

export const collapseMain = {
  type: 'void',
  'x-component': 'Collapse',
    properties: generateXindexInOrder({
      // 供应商标准账期变更信息
      financialInforChangesInfor: {
        type: 'void',
        'x-component': 'CollapseItem',
        'x-component-props': {
          title: i18nExpression('vendorMod.financialInforChangesInfor')
        },
        'x-query-engine-skip': true,
        properties: {
          form: {
            ...formMain
          }
        }
      },
      // 标准账期变更明细
      accountingChangeDetails: {
        type: 'void',
        'x-component': 'CollapseItem',
        'x-component-props': {
          title: i18nExpression('vendorMod.accountingChangeDetails')
        },
        'x-query-engine-skip': true,
        properties: {
              add: {
                type: 'void',
                'x-hidden': '{{$form.readPretty}}',
                'x-component': 'QuickSearchWrapper',
                'x-component-props': {
                  'show-key': 'username',
                  'name': 'scc_sup_company_info2',
                  multiSelect: true,
                  showButton: true,
                  btnTitle: `{{$t('common.new')}}`,
                  '@close-quicksearch': expression(`(val)=>{
                    let companyIdList = []
                    const orgId = $form.query('.form.orgId').take().value

                    if (!orgId) {
                      app.$message.warning($t('dataConfMod.msgInputUnit2'))
                      return false
                    }
                    try {
                      val.forEach(e => {
                        companyIdList.push(e.companyId)
                      })
                    } catch (err) {
                      companyIdList.push(val.companyId)
                    }
                    let obj = {
                      companyIdList: companyIdList,
                      orgId
                    }

                    financeInfoChangeApi.listByCompanyIdAndOrgId(obj).then(res => {
                      if (res.code == '0') {
                        if (res.data.length > 0) {
                          $form.query('.changeBeforeList').take().value = JSON.parse(JSON.stringify(res.data))
                          $form.query('.changeBeforeList').take().value.forEach((val) => {
                            val.changeFlag = 'BEFORE'
                          })
                          $form.query('.changeAfterList').take().value = JSON.parse(JSON.stringify(res.data))
                          $form.query('.changeAfterList').take().value.forEach((val) => {
                            val.changeFlag = 'AFTER'
                          })
                        } else {
                          app.$message.error($t('vendor.financialInformationIsEmpty'))
                        }
                      } else {
                        this.$message.error(res.message)
                      }
                    })
                  }`)
                }
              },
          beforeChangeTitle: {
            type: 'void',
            'x-component': 'changeTitle',
            'x-component-props': {
              language: 'supplierChange.beforeChange'
            }
          },
          changeBeforeList: {
            type: 'array',
            'x-component': 'RenderTable',
            'x-component-props': {
              preColumns: 'seq',
              class: 'table-view-vxe-table',
              editMode: false,
              pagination: false,
              sortable: false,
              height: '250px'
            },
            'x-query-engine-skip': true,
            properties: generateXindexInOrder({
              changeFlag: {
                type: 'string',
                default: 'BEFORE',
                'x-hidden': true,
                'x-render-table-column': {
                  minWidth: 100
                }
              },
              financeInfoId: {
                type: 'number',
                default: null,
                'x-hidden': true
              },
              companyId: {
                type: 'number',
                default: null,
                'x-hidden': true
              },
              companyCode: {
                type: 'string',
                title: i18nExpression('vendorMod.vendorCode'), // 供应商编码
                'x-render-table-column': {
                  minWidth: 150
                }
              },
              companyName: {
                type: 'string',
                title: i18nExpression('vendorMod.vendorName'), // 供应商名称
                'x-render-table-column': {
                  minWidth: 150
                }
              },
              factoryCode: {
                type: 'string',
                title: i18nExpression('vendorMod.factoryCode'), // 工厂代码
                'x-render-table-column': {
                  minWidth: 100
                }
              },
              clearCurrency: {
                type: 'string',
                title: i18nExpression('vendorMod.clearCurrency'), // 结算币种
                'x-component': 'DictSelect',
                'x-render-table-column': {
                  minWidth: 100
                },
                'x-component-props': {
                  code: 'BID_TENDER_CURRENCY'
                }
              },
              paymentMethod: {
                type: 'string',
                title: i18nExpression('vendorMod.paymentMethod'), // 付款方式
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'PAYMENT_METHOD'
                },
                'x-render-table-column': {
                  minWidth: 100
                }
              },
              paymentTerms: {
                type: 'string',
                title: i18nExpression('vendorMod.paymentTerms'), // 付款账期
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'PAYMENT_TERMS'
                },
                'x-render-table-column': {
                  minWidth: 100
                }
              }
            })
          },
          addAfter: {
            type: 'void',
            'x-hidden': '{{$form.readPretty}}',
            'x-component': 'Button',
            'x-content': i18nExpression('common.new'),
            'x-component-props': {
              style: `margin-top:20px`,
              type: 'primary',
              '@click': expression(`({ rowIndex }) => {
                    console.log($form.query(".changeAfterList").take().componentProps.componentInstance)
                    $form.query(".changeAfterList").take().componentProps.componentInstance.addRow("unshift",{Etype:'1',changeFlag:'AFTER'})
              }`)
            }
          },
          afterChangeTitle: {
            type: 'void',
            'x-component': 'changeTitle',
            'x-component-props': {
              language: 'supplierChange.afterChange'
            }
          },
          changeAfterListDele: {
            type: 'array',
            'x-hidden': true
          },
          changeAfterList: {
            type: 'array',
            'x-component': 'RenderTable',
            'x-component-props': {
              preColumns: 'seq',
              class: 'table-view-vxe-table',
              editMode: true,
              pagination: false,
              sortable: false,
              height: '250px'
            },
            'x-query-engine-skip': true,
            properties: generateXindexInOrder({
              changeFlag: {
                type: 'string',
                default: 'AFTER',
                'x-hidden': true,
                'x-render-table-column': {
                  minWidth: 100
                }
              },
              financeInfoId: {
                type: 'number',
                default: null,
                'x-hidden': true,
                'x-render-table-column': {
                  minWidth: 100
                }
              },
              companyId: {
                type: 'number',
                default: null,
                'x-hidden': true,
                'x-render-table-column': {
                  minWidth: 100
                }
              },
              companyCode: {
                type: 'string',
                title: i18nExpression('vendorMod.vendorCode'), // 供应商编码
                'x-read-pretty': '{{true}}',
                'x-render-table-column': {
                  minWidth: 150
                }
              },
              companyName: {
                type: 'string',
                title: i18nExpression('vendorMod.vendorName'), // 供应商名称
                'x-render-table-column': {
                  minWidth: 150
                },
                'x-reactions': expression(`(field) => {
                    const row = $table.getRowByIndex($self.index)
                    const changeBeforeList = $form.query('.changeBeforeList').get('value')
                    let Etype = 0
                    try {
                      Etype = row.Etype
                    } catch (e) {}
                    console.log(field, 'field')
                    if (Etype == '1' && changeBeforeList.length > 0) {
                      field.setComponent('Select')
                      const changeBeforeListC = showCompany(changeBeforeList)
                      $self.dataSource = (changeBeforeListC || []).map(item => {
                        return {
                          key: item.companyId,
                          label: item.companyName,
                          value: item.companyId
                        }
                      })
                      field.setComponentProps({
                        disabled: $form.readPretty || $form.query('.form.approveStatus').take().value === 'SUBMITTED',
                        '@change':()=>{
                            changeBeforeList.forEach(e => {
                              if (e.companyId == row.companyName) {
                                row.companyName = e.companyName
                                row.companyCode = e.companyCode
                                row.companyId = e.companyId
                              }
                            })
                        }
                      })
                    } else if (Etype == '1' && changeBeforeList.length == 0) {
                      field.setComponent('QuickSearchWrapper')
                      field.setComponentProps({
                        'show-input':row.companyName,
                        'show-key':"companyName",
                        'scope-data':row,
                        name:"scc_sup_company_info2",
                        disabled: $form.readPretty,
                        '@close-quicksearch': (val)=>{
                          console.log(val)
                          row.companyName = val ? val.companyName : ''
                          row.companyCode = val ? val.companyCode : ''
                          row.companyId = val ? val.companyId : ''
                        }
                      })
                    } else {
                      field.setComponentProps({readOnly:true,disabled:true})
                    }
                }`)
              },
              factoryCode: {
                type: 'string',
                title: i18nExpression('vendorMod.factoryCode'), // 工厂代码
                'x-render-table-column': {
                  minWidth: 100
                }
              },
              clearCurrency: {
                type: 'string',
                title: i18nExpression('vendorMod.clearCurrency'), // 结算币种
                'x-component': 'DictSelect',
                'x-render-table-column': {
                  minWidth: 100
                },
                'x-component-props': {
                  code: 'BID_TENDER_CURRENCY'
                }
              },
              paymentMethod: {
                type: 'string',
                title: i18nExpression('vendorMod.paymentMethod'), // 付款方式
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'PAYMENT_METHOD'
                },
                'x-render-table-column': {
                  minWidth: 100
                }
              },
              paymentTerms: {
                type: 'string',
                title: i18nExpression('vendorMod.paymentTerms'), // 付款账期
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'PAYMENT_TERMS'
                },
                'x-render-table-column': {
                  minWidth: 100
                }
              },
              operation: {
                type: 'void',
                title: "{{$t('common.operation')}}",
                'x-render-table-column': {
                  width: 150,
                  fixed: 'right'
                },
                'x-component': 'RenderTableButtonList',
                properties: {
                  delete: {
                    type: 'void',
                    'x-hidden': '{{$form.readPretty}}',
                    title: "{{$t('common.delete')}}",
                    'x-component-props': {
                      type: 'text',
                      '@click': expression(`({ row }) => {
                        if(row?.financeChangeId){
                          $table.remove($self.index)
                          $form.query('.changeAfterListDele').take().value.push({ $delete: row.financeChangeId })
                        } else {
                          $table.remove($self.index)
                        }
                      }`)
                    }
                  }
                }
              }
            })
          }
        }
      },
      // 相关附件
      relevantAttachment: {
        type: 'void',
        'x-query-engine-skip': true,
        'x-component': 'CollapseItem',
        'x-component-props': {
          title: i18nExpression('bidMod.attachment')
        },
        properties: {
          fileUploads: {
            type: 'array',
            'x-component': 'FileDynamic',
            'x-component-props': {
              primaryKey: 'sceneFileId',
              // 启用级联删除的储值行为
              cascadeDeletion: true,
              'scene-module-code': 'SCENE_FINANCE_INFO_CHANGE_HEADER',
              'business-id': expression(`$attrs.params.row?.changeHeaderId`),
              'editable': expression(`!$form.readPretty && $form.query('.approveStatus').take()?.value != 'SUBMITTED'`),
              'need-init': false
            }
          }
        }
      }
    })
}
