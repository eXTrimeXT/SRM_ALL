import {
  expression,
  i18nExpression,
  generateXindexInOrder,
  changeFieldVisibleByDeps
} from '@meicloud/render-engine'

import {
  feedbackLayoutIsPopover,
  formGridSegment,
  requiredValidatorSegment,
  editTableFormItemValid,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

export default {
  type: 'void',
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: i18nExpression('outsource.materialReqItem') // '委外领料单明细'
  },
  properties: {
    detailList: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        class: 'table-view-vxe-table',
        maxHeight: 400,
        editMode: true,
        pagination: false,
        sortable: false,
        // 联表主键的 key
        primaryKey: 'materialReqDetailId',
        // 启用级联删除的储值行为
        cascadeDeletion: true
      },
      'x-query-engine-skip': true,
      'x-query-engine-relation': 'detailList:*',
      properties: generateXindexInOrder({
        materialReqDetailId: {
          type: 'string',
          'x-hidden': true
        },
        // 行号
        rowNum: {
          type: 'string',
          'x-read-pretty': true,
          title: i18nExpression('purchaseDemand.lineNum'),
          'x-render-table-column': {
            minWidth: 80
          }
        },
        // 领料单行状态
        rowStatus: {
          type: 'string',
          'x-hidden': true,
          'x-read-pretty': true,
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'OrderDetailStatus'
          },
          'x-render-table-column': {
            title: i18nExpression('outsource.materialReqRowStatus'),
            minWidth: 100
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
          'x-read-pretty': true,
          'x-render-table-column': {
            title: i18nExpression('outsource.materialHeadNum'),
            minWidth: 120
          }
        },
        // 委外用料单行号
        materialRowNum: {
          type: 'string',
          'x-read-pretty': true,
          'x-render-table-column': {
            title: i18nExpression('outsource.materialRowNum'),
            minWidth: 120
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
          'x-read-pretty': true,
          'x-render-table-column': {
            title: i18nExpression('outsource.orderNum'),
            minWidth: 120
          }
        },
        // 采购订单行号
        orderRowNum: {
          type: 'string',
          'x-read-pretty': true,
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
          'x-read-pretty': true,
          title: i18nExpression('purchaseDemand.itemCode'),
          'x-render-table-column': {
            minWidth: 100
          }
        },
        // 物料名称
        materialName: {
          type: 'string',
          'x-read-pretty': true,
          title: i18nExpression('purchaseDemand.itemName'),
          'x-render-table-column': {
            minWidth: 150
          }
        },
        // 单位
        materialUnit: {
          type: 'string',
          'x-read-pretty': true,
          'x-render-table-column': {
            minWidth: 100,
            title: i18nExpression('purchaseDemand.unitCode')
          }
        },
        // 采购订单数量
        orderNum: {
          type: 'string',
          'x-read-pretty': true,
          title: i18nExpression('outsource.orderNum'),
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
          'x-read-pretty': true,
          title: i18nExpression('outsource.baseMaterialCode'),
          'x-render-table-column': {
            minWidth: 100
          }
        },
        // 组件物料名称
        baseMaterialName: {
          type: 'string',
          'x-read-pretty': true,
          title: i18nExpression('outsource.baseMaterialName'),
          'x-render-table-column': {
            minWidth: 100
          }
        },
        // 组件物料单位
        baseMaterialUnit: {
          type: 'string',
          'x-read-pretty': true,
          title: i18nExpression('outsource.baseMaterialUnit'),
          'x-render-table-column': {
            minWidth: 100
          }
        },
        // 组件物料数量
        baseMaterialNum: {
          type: 'string',
          'x-read-pretty': true,
          title: i18nExpression('outsource.baseMaterialNum'),
          'x-render-table-column': {
            minWidth: 100
          }
        },
        // 已退料数量
        returnQuantity: {
          type: 'number',
          'x-read-pretty': true,
          'x-render-table-column': {
            title: i18nExpression('outsource.returnQuantity'),
            minWidth: 120,
            titlePrefix: { content: i18nExpression('outsource.materialsReturnQuantityTip') } // '已退料数量=此领料单明细行累计退料数量'
          }
        },
        // sumOutsourcingQty  委外组件总数量（物料-需求数量-所需组件物料数量）
        // buyerOutsourcingQty 组件物料数量（采购方）
        // 已领料数量
        receivedQuantity: {
          type: 'number',
          'x-read-pretty': true,
          'x-render-table-column': {
            title: i18nExpression('outsource.receivedQuantity'),
            minWidth: 120,
            titlePrefix: { content: i18nExpression('outsource.receivedQuantityTitlePrefix') } // '已领料数量=此委外用料清单行累计已领料数量 -累计退料数量（更新未领数量的退料数量）'
          }
        },
        // 未领数量
        unreceivedQuantity: {
          type: 'string',
          title: i18nExpression('outsource.unreceivedQuantity'),
          'x-read-pretty': true,
          'x-render-table-column': {
            minWidth: 100
          }
        },
        // 本次领料数量 （默认带出本次领料数量）
        thisReceivedQuantity: {
          type: 'number',
          'x-render-table-column': {
            title: i18nExpression('outsource.thisReceivedQuantity'),
            minWidth: 125
          },
          'x-component-props': {
            disabled: true
          }
        },
        // 供方反馈以后有这个字段
        // 供应商签收数量 (默认带出本次领料数量)// 供应商隐藏
        vendorReceiptQty: {
          type: 'number',
          title: i18nExpression('outsource.vendorReceiptQty'),
          'x-render-table-column': {
            minWidth: 130
          },
          'x-read-pretty': '{{ $form.readPretty }}',
          'x-reactions': expression(`() => {
            let row = $table.getRowByIndex($self.index)
            // 第一次点确认 才默认赋值，后续驳回不做赋值
            let thisReceivedQuantity = row?.thisReceivedQuantity
            let vendorReceiptQty = row?.vendorReceiptQty
            let handleStatus = $form.values.handleStatus // SUBMIT
            if (thisReceivedQuantity && !vendorReceiptQty && handleStatus=='SUBMIT') {
              row.vendorReceiptQty = thisReceivedQuantity // 赋值
              let vendorReceiptQtyN = row?.vendorReceiptQty
              let vendorDiffQty = thisReceivedQuantity - vendorReceiptQtyN
              row.vendorDiffQty = vendorDiffQty // 赋值
            }
          }`),
          'x-hidden': expression('$receiveConfirmCtrl($form)'),
          ...editTableFormItemValid,
          'x-component-props': {
            'min': 0, // 设置最小值为0
            '@change': expression(`(val) => {
              if(val>-1) {
                let fileVal = val
                let row = $table.getRowByIndex($self.index)
                let thisReceivedQuantity = row?.thisReceivedQuantity
                let vendorDiffQty = thisReceivedQuantity - fileVal
                row.vendorDiffQty = vendorDiffQty
              }
            }`)
          }
        },
        // 供方反馈以后有这个字段
        // 差异数量 （本次领料数量-供应商签收数量）
        vendorDiffQty: {
          type: 'number',
          title: i18nExpression('outsource.vendorDiffQty'),
          'x-render-table-column': {
            minWidth: 110
          },
          'x-component-props': {
            disabled: true
          },
          'x-hidden': expression('$receiveConfirmCtrl($form)')
        },
        // 领料地址
        receivedAddress: {
          type: 'string',
          title: i18nExpression('outsource.receivedAddress'),
          'x-render-table-column': {
            minWidth: 100,
            customRender: true
          },
          'x-component-props': {
            disabled: true
          }
        },
        // 领料联系人
        receivedLinkman: {
          type: 'string',
          title: i18nExpression('outsource.receivedLinkman'),
          'x-render-table-column': {
            minWidth: 100,
            customRender: true
          },
          'x-component-props': {
            disabled: true
          }
        },
        // 领料联系电话
        receivedPhone: {
          type: 'string',
          title: i18nExpression('outsource.receivedPhone'),
          'x-render-table-column': {
            minWidth: 100,
            customRender: true
          },
          'x-component-props': {
            disabled: true
          }
        },
        // 采购商 明细备注
        buyerRemark: {
          type: 'string',
          title: i18nExpression('purchaseDemand.comments'), // 明细备注
          'x-component-props': {
            maxlength: 50,
            showWordLimit: true,
            disabled: true
          },
          'x-render-table-column': {
            minWidth: 150,
            customRender: true
          }
        }
      })
    },
    reback: {
      type: 'void',
      ...formGridSegment,
      'x-component-props': {
        style: 'margin-top:20px;'
      },
      properties: {
        // 供应商差异说明 (采购商发布以后供应商确认差异)
        vendorDiffDescription: {
          type: 'string',
          'x-decorator': 'FormItem',
          'x-decorator-props': { gridSpan: 12 },
          title: i18nExpression('outsource.vendorDiffDescription'),
          'x-component-props': {
            type: 'textarea',
            maxlength: '500',
            showWordLimit: true,
            disabled: false,
            autosize: { minRows: 2, maxRows: 5 }
          },
          'x-hidden': expression('$formInputRebackFlag($form,["CREATE"])')
        },
        // 采购商补充说明 （供应商提交以后采购商确认差异）
        buyerAdditionalRemarks: {
          type: 'string',
          'x-decorator': 'FormItem',
          'x-decorator-props': { gridSpan: 12 },
          title: i18nExpression('outsource.buyerAdditionalRemarks'),
          'x-component-props': {
            type: 'textarea',
            maxlength: '500',
            showWordLimit: true,
            autosize: { minRows: 2, maxRows: 5 },
            disabled: true
          },
          'x-hidden': expression('$formInputRebackFlag($form,["CREATE"])')
        }
      }
    }
  }
}
