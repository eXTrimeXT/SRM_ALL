/**
 * @description 新增退料-物料选择
 */
import {
  generateXindexInOrder,
  i18nExpression,
  expression
} from '@meicloud/render-engine'

const outMaterialsDailogSegment: Record<any, any> = {
  materialsSelect: {
    type: 'void',
    'x-query-engine': {
      service: 'sup-ce',
      type: 'OsMaterialDetail',
      actions: {
        paginationQuery: {
          action: 'pageDetail',
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
      outsourceMaterialsDailog: {
        type: 'object',
        title: i18nExpression('outsource.materialReqSelect'), // '委外领料单明细选择',
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
          beforeClose: expression(`(done) => {
            // 重置表单，如果不需要可以去掉
            $form.query($self.address.concat('query')).take((field) => {
              field.reset()
            })
            done()
          }`),
          beforeOkClose: expression(`async () => {
            const selection = $form.query($self.address.concat('mtDetailList')).take()
              .invoke('getCheckboxRecords')
              if (!selection.length) {
                return Promise.reject(
                  $message.error($t('contractMod.msgSelData'))
                )
              }
              $setMaterialData(selection,$form,$message)
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
            properties: generateXindexInOrder({
              // 委外用料单号
              materialHeadNum: {
                type: 'string',
                title: '委外用料单号',
                'x-query-engine-relation': 'materialHeadId',
                'x-query-engine-relation-strict': true,
                'x-query-engine-query-operator': 'contains'
              },
              // 采购订单号
              orderNumber: {
                type: 'string',
                title: i18nExpression('outsource.orderNumber'),
                'x-query-engine-relation': 'materialHeadId',
                'x-query-engine-relation-strict': true,
                'x-query-engine-query-operator': 'contains'
              },
              // 物料编码
              materialCode: {
                type: 'string',
                title: i18nExpression('purchaseDemand.itemCode'),
                'x-query-engine-relation': 'materialHeadId',
                'x-query-engine-relation-strict': true,
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
                'x-query-engine-relation': 'materialHeadId',
                'x-query-engine-relation-strict': true
              },
              organizationId: {
                type: 'string',
                'x-hidden': true,
                default: expression('$form.values.organizationId'),
                'x-query-engine-relation': 'materialHeadId',
                'x-query-engine-relation-strict': true
              },
              vendorId: {
                type: 'string',
                'x-hidden': true,
                default: expression('$form.values.vendorId'),
                'x-query-engine-relation': 'materialHeadId',
                'x-query-engine-relation-strict': true
              },
              status: {
                type: 'string',
                'x-hidden': true,
                default: 'VALID',
                'x-query-engine-relation': 'materialHeadId',
                'x-query-engine-relation-strict': true
              }
            })
          },
          mtDetailList: {
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
              // 委外用料ID
              materialLineId: {
                type: 'string',
                'x-hidden': true
              },
              orgId: {
                type: 'string',
                'x-hidden': true,
                'x-query-engine-relation': 'materialHeadId'
              },
              organizationId: {
                type: 'string',
                'x-hidden': true,
                'x-query-engine-relation': 'materialHeadId'
              },
              vendorId: {
                type: 'string',
                'x-hidden': true,
                'x-query-engine-relation': 'materialHeadId'
              },
              materialHeadId: {
                type: 'string',
                'x-hidden': true,
                'x-query-engine-relation': 'materialHeadId'
              },
              status: {
                type: 'string',
                'x-hidden': true,
                'x-query-engine-relation': 'materialHeadId'
              },
              // 委外用料单号
              materialHeadNum: {
                type: 'string',
                'x-query-engine-relation': 'materialHeadId',
                'x-render-table-column': {
                  title: i18nExpression('outsource.materialHeadNum'),
                  minWidth: 150
                }
              },
              // 委外用料单行号
              rowNum: {
                type: 'string',
                'x-render-table-column': {
                  title: i18nExpression('outsource.materialRowNum'),
                  minWidth: 150
                }
              },
              // 采购订单明细ID
              orderDetailId: {
                type: 'string',
                'x-query-engine-relation': 'materialHeadId',
                'x-hidden': true
              },
              // 采购订单号
              orderNumber: {
                type: 'string',
                'x-query-engine-relation': 'materialHeadId',
                'x-render-table-column': {
                  title: i18nExpression('outsource.orderNumber'),
                  minWidth: 150
                }
              },
              // 采购订单行号
              orderDetailRow: {
                type: 'string',
                'x-query-engine-relation': 'materialHeadId',
                'x-render-table-column': {
                  title: i18nExpression('outsource.requirementHeadNum'),
                  minWidth: 150
                }
              },
              // 物料Id
              materialId: {
                type: 'string',
                'x-query-engine-relation': 'materialHeadId',
                'x-hidden': true
              },
              // 物料编码
              materialCode: {
                type: 'string',
                'x-query-engine-relation': 'materialHeadId',
                title: i18nExpression('purchaseDemand.itemCode'),
                'x-render-table-column': {
                  minWidth: 150
                }
              },
              // 物料名称
              materialName: {
                type: 'string',
                'x-query-engine-relation': 'materialHeadId',
                title: i18nExpression('purchaseDemand.itemName'),
                'x-render-table-column': {
                  minWidth: 200
                }
              },
              // 单位
              materialUnit: {
                type: 'string',
                'x-query-engine-relation': 'materialHeadId',
                'x-render-table-column': {
                  minWidth: 100,
                  title: i18nExpression('purchaseDemand.unitCode')
                }
              },
              // 采购订单数量
              orderDetailQuantity: {
                type: 'string',
                'x-query-engine-relation': 'materialHeadId',
                title: i18nExpression('purchaseDemand.requirementQuantity'),
                'x-render-table-column': {
                  minWidth: 100
                }
              },
              // 已退料数量
              returnQuantity: {
                type: 'string',
                'x-hidden': true
              },
              // 已领料数量
              receivedQuantity: {
                type: 'string',
                'x-hidden': true
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
              // 未领数量
              unreceivedQuantity: {
                type: 'string',
                title: i18nExpression('outsource.unreceivedQuantity'),
                'x-render-table-column': {
                  minWidth: 100
                }
              },
              lastUpdateDate: {
                type: 'string',
                'x-hidden': true,
                'x-query-engine-sort': 'desc'
              }
            })
          }
        }
      }
    }
  }
}

export default outMaterialsDailogSegment
