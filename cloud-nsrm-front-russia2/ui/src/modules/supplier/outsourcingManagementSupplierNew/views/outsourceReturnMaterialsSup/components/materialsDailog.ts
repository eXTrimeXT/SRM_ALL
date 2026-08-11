/**
 * @description 新增退料-物料选择
 */
import {
  generateXindexInOrder,
  i18nExpression,
  expression
} from '@meicloud/render-engine'

const ReturnMaterialsDailogSegment: Record<any, any> = {
  materialsSelect: {
    type: 'void',
    'x-query-engine': {
      service: 'sup-ce',
      type: 'OsMaterialReqDetail',
      actions: {
        paginationQuery: {
          immediate: false,
          action: 'query',
          method: 'paginationQuery'
        }
      }
    },
    'x-decorator': 'el-container',
    'x-component': 'QueryEngine',
    'x-decorator-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    properties: {
      returnMaterialsDailog: {
        type: 'object',
        title: i18nExpression('outsource.returnMaterialsDailog'), // '委外退料单明细选择',
        'x-component': 'RDialog',
        'x-component-props': {
          class: 'dialogMain',
          size: 'large',
          'close-on-click-modal': false,
          '@opened': expression(`() => {
            $form.query($self.address.concat('query')).take((field) => {
              field.invoke('query')
            })
          }`),
          beforeClose: expression(`(done, type, closeLoading) => {
            if(type === 'ok'){
              const selection = $form.query($self.address.concat('returnMtDetailList')).take().invoke('getCheckboxRecords')
              if (!selection.length) {
                closeLoading()
                return $message.error($t('contractMod.msgSelData'))
              }
              $setMaterialData(selection,$form,$message)
              closeLoading()
            }else{
              done()
            }
          }`)
        },
        'x-data': {
          viewRow: null
        },
        properties: {
          query: {
            type: 'object',
            'x-query-engine-skip': true,
            'x-component': 'QueryFormByQueryEngine',
            'x-component-props': {
              immediateQueryForm: false
            },
            properties: generateXindexInOrder({
              // 委外领料单号
              materialReqNum: {
                type: 'string',
                title: i18nExpression('outsource.materialReqNum'),
                'x-query-engine-query-operator': 'contains',
                'x-query-engine-relation': 'materialReqId',
                'x-query-engine-relation-strict': true
              },
              // 采购订单号
              orderNumber: {
                'type': 'string',
                'title': i18nExpression('outsource.orderNumber'),
                'x-query-engine-query-operator': 'contains'
              },
              // 物料编码
              materialCode: {
                type: 'string',
                title: i18nExpression('purchaseDemand.itemCode'),
                'x-query-engine-query-operator': 'contains'
              },
              // 组件物料编码
              baseMaterialCode: {
                type: 'string',
                title: i18nExpression('outsource.baseMaterialCode'),
                'x-query-engine-query-operator': 'contains'
              },
              orgId: {
                type: 'string',
                'x-hidden': true,
                default: expression('$form.values.orgId'),
                'x-query-engine-relation': 'materialReqId',
                'x-query-engine-relation-strict': true
              },
              organizationId: {
                type: 'string',
                'x-hidden': true,
                default: expression('$form.values.organizationId'),
                'x-query-engine-relation': 'materialReqId',
                'x-query-engine-relation-strict': true
              },
              vendorId: {
                type: 'string',
                'x-hidden': true,
                default: expression('$form.values.vendorId'),
                'x-query-engine-relation': 'materialReqId',
                'x-query-engine-relation-strict': true
              },
              handleStatus: {
                type: 'string',
                'x-hidden': true,
                default: 'VALID',
                'x-query-engine-relation': 'materialReqId',
                'x-query-engine-relation-strict': true
              },
              haveReturnQuantity: {
                type: 'string',
                'x-hidden': true,
                default: 'N',
                'x-query-engine-query-operator': 'ne'
              }
            })
          },
          returnMtDetailList: {
            type: 'array',
            'x-component': 'RenderTable',
            'x-component-props': {
              class: 'table-view-vxe-table',
              preColumns: 'checkbox, seq',
              maxHeight: 350,
              pagination: true,
              sortable: false
            },
            properties: generateXindexInOrder({
              materialReqDetailId: {
                type: 'string',
                'x-hidden': true
              },
              orgId: {
                type: 'string',
                'x-hidden': true,
                'x-query-engine-relation': 'materialReqId'
              },
              organizationId: {
                type: 'string',
                'x-hidden': true,
                'x-query-engine-relation': 'materialReqId'
              },
              vendorId: {
                type: 'string',
                'x-hidden': true,
                'x-query-engine-relation': 'materialReqId'
              },
              handleStatus: {
                type: 'string',
                'x-hidden': true,
                'x-query-engine-relation': 'materialReqId'
              },
              materialReqId: {
                type: 'string',
                'x-query-engine-relation': 'materialReqId',
                'x-hidden': true
              },
              // 委外领料单号
              materialReqNum: {
                type: 'string',
                'x-query-engine-relation': 'materialReqId',
                'x-render-table-column': {
                  title: i18nExpression('outsource.materialReqNum'),
                  minWidth: 150
                }
              },
              // 委外领料单行号
              rowNum: {
                type: 'string',
                'x-render-table-column': {
                  title: i18nExpression('outsource.materialReqRow'),
                  minWidth: 120
                }
              },
              // 委外用料单行id
              materialLineId: {
                type: 'string',
                'x-hidden': true
              },
              // 委外用料单号
              materialHeadNum: {
                type: 'string',
                'x-render-table-column': {
                  title: i18nExpression('outsource.materialHeadNum'),
                  minWidth: 150
                }
              },
              // 委外用料单行号
              materialRowNum: {
                type: 'string',
                'x-render-table-column': {
                  title: i18nExpression('outsource.materialRowNum'),
                  minWidth: 150
                }
              },
              // 采购订单ID
              orderDetailId: {
                type: 'string',
                'x-hidden': true
              },
              // 采购订单号
              orderNumber: {
                type: 'string',
                'x-render-table-column': {
                  title: i18nExpression('outsource.orderNumber'),
                  minWidth: 150
                }
              },
              // 采购订单行号
              orderRowNum: {
                type: 'string',
                'x-render-table-column': {
                  title: i18nExpression('outsource.requirementHeadNum'),
                  minWidth: 100
                }
              },
              // 物料Id
              materialId: {
                type: 'string',
                'x-hidden': true
              },
              // 物料编码
              materialCode: {
                type: 'string',
                title: i18nExpression('purchaseDemand.itemCode'),
                'x-render-table-column': {
                  minWidth: 150
                }
              },
              // 物料名称
              materialName: {
                type: 'string',
                title: i18nExpression('purchaseDemand.itemName'),
                'x-render-table-column': {
                  minWidth: 150
                }
              },
              // 单位
              materialUnit: {
                type: 'string',
                'x-render-table-column': {
                  minWidth: 100,
                  title: i18nExpression('purchaseDemand.unitCode')
                }
              },
              // 采购订单数量
              orderNum: {
                type: 'string',
                title: i18nExpression('purchaseDemand.requirementQuantity'),
                'x-render-table-column': {
                  minWidth: 100
                }
              },
              // 委外组件Id
              baseMaterialId: {
                type: 'string',
                'x-hidden': true
              },
              // 组件物料编码
              baseMaterialCode: {
                type: 'string',
                title: i18nExpression('outsource.baseMaterialCode'),
                'x-render-table-column': {
                  minWidth: 100
                }
              },
              // 组件物料名称
              baseMaterialName: {
                type: 'string',
                title: i18nExpression('outsource.baseMaterialName'),
                'x-render-table-column': {
                  minWidth: 100
                }
              },
              // 组件物料单位
              baseMaterialUnit: {
                type: 'string',
                title: i18nExpression('outsource.baseMaterialUnit'),
                'x-render-table-column': {
                  minWidth: 100
                }
              },
              // 组件物料数量
              baseMaterialNum: {
                type: 'string',
                title: i18nExpression('outsource.baseMaterialNum'),
                'x-render-table-column': {
                  minWidth: 100
                }
              },
              // 本次领料数量
              thisReceivedQuantity: {
                type: 'number',
                title: i18nExpression('outsource.thisReceivedQuantity'),
                'x-render-table-column': {
                  minWidth: 100
                },
                'x-hidden': true
              },
              // 差异数量
              vendorDiffQty: {
                type: 'number',
                title: i18nExpression('outsource.vendorDiffQty'),
                'x-render-table-column': {
                  minWidth: 100
                },
                'x-hidden': true
              },
              // 领料数量
              receivedQuantity: {
                type: 'number',
                title: i18nExpression('outsource.returnReceivedQuantity'),
                'x-render-table-column': {
                  minWidth: 100
                },
                'x-reactions': expression(`() => {
                  let row = $table.getRowByIndex($self.index)
                  if (row) {
                    let thisReceivedQuantity = row?.thisReceivedQuantity
                    let vendorDiffQty = row?.vendorDiffQty || 0
                    // 本次领料数量 - 差异数量
                    if (thisReceivedQuantity) {
                      let receivedQuantity = thisReceivedQuantity-vendorDiffQty
                      row.receivedQuantity = receivedQuantity // 赋值
                    } else {
                      row.receivedQuantity = null // 赋值
                    }
                  }
                }`)
              },
              // 已退料数量
              returnQuantity: {
                type: 'string',
                title: i18nExpression('outsource.returnQuantity'),
                'x-render-table-column': {
                  minWidth: 100
                }
              },
              lastUpdateDate: {
                type: 'string',
                'x-hidden': true,
                'x-query-engine-sort': 'desc'
              },
              haveReturnQuantity: {
                type: 'string',
                'x-hidden': true
              }
            })
          }
        }
      }
    }
  }
}

export default ReturnMaterialsDailogSegment
