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
    title: i18nExpression('outsource.returnReqMtDetail') // '委外退料单明细'
  },
  properties: {
    // toolbar: {
    //   type: 'void',
    //   'x-component': 'ButtonList',
    //   'x-component-props': {
    //     class: 'list-form__toolbar'
    //   },
    //   'x-reactions': expression(`(field) => {
    //       field.visible = !$form.readPretty
    //   }`),
    //   properties: {}
    // },
    detailList: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        class: 'table-view-vxe-table',
        maxHeight: 400,
        pagination: false,
        sortable: false,
        editMode: true,
        // 联表主键的 key
        primaryKey: 'returnLineId',
        // 启用级联删除的储值行为
        cascadeDeletion: true
      },
      'x-query-engine-skip': true,
      'x-query-engine-relation': 'detailList:*',
      properties: generateXindexInOrder({
        returnLineId: {
          type: 'string',
          'x-hidden': true
        },
        // 行号
        rowNum: {
          type: 'string',
          title: i18nExpression('purchaseDemand.lineNum'),
          'x-read-pretty': true,
          'x-render-table-column': {
            minWidth: 80
          }
        },
        returnId: {
          type: 'string',
          'x-hidden': true
        },
        // 退料单行状态
        rowStatus: {
          type: 'string',
          'x-hidden': true,
          'x-read-pretty': true,
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'OrderDetailStatus'
          },
          'x-render-table-column': {
            title: i18nExpression('outsource.returnRowStatus'),
            minWidth: 100
          }
        },
        // 委外领料单号
        materialReqNum: {
          type: 'string',
          'x-read-pretty': true,
          'x-render-table-column': {
            title: i18nExpression('outsource.materialReqNum'),
            minWidth: 120
          }
        },
        // 委外领料单行号
        materialReqRow: {
          type: 'string',
          'x-read-pretty': true,
          'x-render-table-column': {
            title: i18nExpression('outsource.materialReqRow'),
            minWidth: 120
          }
        },
        // 委外领料单ID
        materialReqDetailId: {
          type: 'string',
          'x-hidden': true
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
        // 采购订单明细ID
        orderDetailId: {
          type: 'string',
          'x-hidden': true
        },
        // 采购订单号
        orderNumber: {
          type: 'string',
          'x-read-pretty': true,
          'x-render-table-column': {
            title: i18nExpression('outsource.orderNumber'),
            minWidth: 120
          }
        },
        // 采购订单行号
        orderDetailRow: {
          type: 'number',
          'x-read-pretty': true,
          'x-render-table-column': {
            title: i18nExpression('outsource.requirementHeadNum'),
            minWidth: 100
          }
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
        orderQuantity: {
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
        // 领料数量
        receivedQuantity: {
          type: 'number',
          'x-read-pretty': true,
          'x-render-table-column': {
            title: i18nExpression('outsource.returnReceivedQuantity'),
            minWidth: 120
          }
        },
        // 已退料数量
        returnQuantity: {
          type: 'string',
          title: i18nExpression('outsource.returnQuantity'),
          'x-read-pretty': true,
          'x-render-table-column': {
            minWidth: 100,
            titlePrefix: { content: i18nExpression('outsource.materialsReturnQuantityTip') } // '已退料数量=该领料单明细行累计已退料数量'
          }
        },
        // 本次退料数量 （不做校验）
        // 默认值：已领料数量- 已退料数量；不可大于已领料数量 - 已退料数量之差；
        // 否则提示：本次退料数量不能大于已领料数量 - 已退料数量之差
        thisReturnQuantity: {
          type: 'number',
          'x-render-table-column': {
            title: i18nExpression('outsource.thisReturnQuantity'),
            minWidth: 120,
            customRender: true
          },
          'x-component-props': {
            disabled: true
          },
          ...feedbackLayoutIsPopover,
          'x-validator': {
            required: true,
            message: i18nExpression('common.requiredField')
          }
        },
        // 退料原因
        returnReason: {
          type: 'string',
          title: i18nExpression('outsource.rowReturnReason'),
          'x-render-table-column': {
            minWidth: 130,
            customRender: true
          },
          'x-component': 'DictSelect',
          'x-component-props': {
            disabled: true,
            code: 'OS_MATERIAL_RETURN_REASON_TYPE'
          },
          ...editTableFormItemValid
        },
        // 是否更新未领数量
        isUpdateUnreceived: {
          type: 'string',
          title: i18nExpression('outsource.isUpdateUnreceived'),
          'x-render-table-column': {
            minWidth: 150,
            customRender: true
          },
          'x-component': 'Checkbox',
          'x-component-props': {
            disabled: true,
            'true-label': 'Y',
            'false-label': 'N'
          },
          ...editTableFormItemValid
        },
        // 明细备注
        detailComments: {
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
        // operation: {
        //   type: 'void',
        //   title: i18nExpression('common.operation'),
        //   'x-render-table-column': {
        //     width: 160,
        //     fixed: 'right'
        //   },
        //   'x-component': 'RenderTableButtonList',
        //   'x-reactions': expression(`(field) => {
        //     field.visible = !$form.readPretty
        //   }`),
        //   properties: {
        //     delete: {
        //       type: 'void',
        //       title: i18nExpression('common.delete'),
        //       'x-component-props': {
        //         type: 'text',
        //         '@click': expression(`
        //             ({ rowIndex }) => {
        //               $table.remove(rowIndex)
        //             }
        //         `)
        //       }
        //     }
        //   }
        // }
      })
    },
    reback: {
      type: 'void',
      ...formGridSegment,
      'x-component-props': {
        style: 'margin-top:20px;'
      },
      properties: {
        // 采购商驳回原因
        rejectReason: {
          type: 'string',
          'x-decorator': 'FormItem',
          'x-decorator-props': { gridSpan: 24 },
          title: i18nExpression('outsource.replyBuyerRejectReason'),
          'x-component-props': {
            type: 'textarea',
            maxlength: '500',
            showWordLimit: true,
            disabled: false,
            autosize: { minRows: 2, maxRows: 5 }
          },
          'x-reactions': changeFieldVisibleByDeps(
            ['.status'],
            '(["WAITING_BUYER_CONFIRM","VALID"].includes($deps[0]))'
          )
        },
        // 供应商补充说明
        vendorAdditionalExp: {
          type: 'string',
          'x-decorator': 'FormItem',
          'x-decorator-props': { gridSpan: 24 },
          title: i18nExpression('outsource.vendorAdditionalExp'),
          'x-component-props': {
            type: 'textarea',
            maxlength: '500',
            showWordLimit: true,
            disabled: true,
            autosize: { minRows: 2, maxRows: 5 }
          },
          'x-reactions': changeFieldVisibleByDeps(
            ['.status'],
            '(["BUYER_REJECT","WAITING_BUYER_CONFIRM","VALID"].includes($deps[0]))'
          )
        }
      }
    }
  }
}
