import {
  expression,
  changeFieldVisibleByDeps,
  i18nExpression,
  generateXindexInOrder
} from '@meicloud/render-engine'

import {
  feedbackLayoutIsPopover,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

export default {
  type: 'void',
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: i18nExpression('orderMod.deliveryNoticeDetail')
  },
  properties: {
    toolbar: {
      type: 'void',
      'x-component': 'Space',
      'x-component-props': {
        style: 'margin-bottom: 16px'
      },
      'x-reactions': expression(`(field) => {
          field.visible = !$form.readPretty && !$form.query('DeliveryNoticeVendor').get('data').configValue.vendorSplitReply
       }`),
      properties: {
        // 接受
        accept: {
          type: 'void',
          title: i18nExpression('orderMod.accept'), // 接受
          'x-component': 'RButton',
          'x-component-props': {
            type: 'primary',
            '@click': expression(`() => {
              $rejectOrAccept($form,$self,$message,'ACCEPT')
            }`)
          }
        },
        // 拒绝
        reject: {
          type: 'void',
          title: i18nExpression('common.refused'), // 拒绝
          'x-component': 'RButton',
          'x-component-props': {
            type: 'default',
            '@click': expression(`() => {
              $rejectOrAccept($form,$self,$message,'REFUSE')
            }`)
          }
        }
      }
    },
    detailList: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        class: 'table-view-vxe-table',
        style: 'flex: 1',
        maxHeight: 400,
        pagination: false,
        preColumns: '{{!$form.query(\'DeliveryNoticeVendor\').get(\'data\').configValue.vendorSplitReply ? \'checkbox\' : \'\'}}',
        sortable: false,
        checkboxConfig: { trigger: 'defatult' }
      },
      'x-query-engine-skip': true,
      'x-query-engine-relation': 'detailList:*',
      properties: generateXindexInOrder({
        lineNum: {
          type: 'string',
          title: i18nExpression('orderMod.deliveryLineNum'), // 送货通知单行号
          'x-render-table-column': {
            minWidth: 120
          }
        },
        status: {
          type: 'string',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'DELIVERY_NOTICE_DETAIL_STATUS_NEW'
          },
          'x-render-table-column': {
            title: i18nExpression('orderMod.rowStatus'), // 行状态
            minWidth: 120,
            sortable: '{{!$form.query(\'DeliveryNoticeVendor\').get(\'data\').configValue.vendorSplitReply}}'
          }
        },
        orderNumberAndOrderDetailLineNum: {
          type: 'string',
          'x-component': 'TableButton',
          'x-component-props': {
            disabled: false,
            type: 'text',
            '@click': expression('({ row }) => $readOrder(row)')
          },
          'x-render-table-column': {
            title: `{{$t('orderMod.buyerOrderSynergy.orderNumber') +
          '|' + $t('vendorMod.relegation.lineNumber')}}`, // 采购订单编号|行号
            minWidth: 150,
            customRender: true
          },
          'x-reactions': expression(`(field) => {
            let row = $table.getRowByIndex($self.index)
            $self.value = row?.orderNumber + '|' + row?.orderDetailLineNum
          }`)
        },
        materialCode: {
          type: 'string',
          title: i18nExpression('purchaseDemand.itemCode'), // 物料编码
          'x-render-table-column': {
            minWidth: 120
          }
        },
        materialName: {
          type: 'string',
          title: i18nExpression('purchaseDemand.itemName'), // 物料名称
          'x-render-table-column': {
            minWidth: 120
          }
        },
        unit: {
          type: 'string',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'unit'
          },
          'x-render-table-column': {
            title: i18nExpression('purchaseDemand.unitCode'), // 单位
            minWidth: 120
          }
        },
        categoryName: {
          type: 'string',
          title: i18nExpression('common.category'), // 品类
          'x-render-table-column': {
            minWidth: 120
          }
        },
        orderNum: {
          type: 'string',
          title: i18nExpression('orderMod.buyerOrderSynergy.orderNum'), // 订单数量
          'x-render-table-column': {
            minWidth: 120
          }
        },
        noticeSum: {
          type: 'string',
          title: i18nExpression('orderMod.surplusDeliveryQuantity1'), // 本次通知送货数量
          'x-render-table-column': {
            minWidth: 120
          }
        },
        deliveryQuantity: {
          type: 'string',
          title: i18nExpression('buyerDeliveryNotice.deliveryQuantity'), // 已送货数量
          'x-render-table-column': {
            minWidth: 120,
            titlePrefix: { content: i18nExpression('buyerDeliveryNotice.deliveryQuantityTip') } // 已送货数量：送货通知单创建送货单累计已送货数量
          }
        },
        warehouseQuantity: {
          type: 'string',
          title: i18nExpression('orderMod.warehouseQuantity'), // 已入库数量
          'x-render-table-column': {
            minWidth: 120,
            titlePrefix: { content: i18nExpression('orderMod.warehouseQuantityDesc') } // 已入库数量=送货通知单创建送货单已入库数量
          }
        },
        returnedQuantity: {
          type: 'string',
          title: i18nExpression('orderMod.returnedQuantity'), // 已退货数量
          'x-render-table-column': {
            minWidth: 120
          }
        },
        receiveDate: {
          title: i18nExpression('contractMod.deliveryDate1'), // 到货日期
          'x-render-table-column': {
            minWidth: 160
          },
          ...yearMonthDaySelectorSegment,
          'x-component-props': {
            ...yearMonthDaySelectorSegment['x-component-props'],
            formatter: expression(`({ cellValue, row, column }) => {
              parseTime(row.receiveDate, '{y}-{m}-{d}')
            }`)
          }
        },
        confirmNum: {
          type: 'number',
          'x-hidden': expression('!$form.query(\'DeliveryNoticeVendor\').get(\'data\').configValue?.vendorSplitReply'),
          'x-render-table-column': {
            title: i18nExpression('buyerDeliveryNotice.confirmNoticeNum'), // 供方确认通知数量
            minWidth: 160,
            customRender: true
          },
          'x-reactions': expression(`(field) => {
            const row = $table.getRowByIndex($self.index)
            let total = $form.values?.detailList?.filter(item => item.parentLineNum === row?.parentLineNum)?.reduce((prev, cur) => prev + Number(cur.confirmNum || 0), 0)
            setTimeout(()=>{
              field.setComponentProps({
                class: (row?.noticeSum > total || total === 0)  ? 'high-light' :''
              })
            })
          }`),
          ...feedbackLayoutIsPopover,
          'x-validator': {
            required: true,
            message: i18nExpression('common.requiredField'),
            validator: expression(`(value, rule) => {
              if (value < 0) {
                return $t('buyerDeliveryNotice.prompt7') // 供方确认通知数量能小于0，请检查！
              }
            }`)
          },
          'x-read-pretty': '{{$form.readPretty}}'
        },
        promiseReceiveDate: {
          ...yearMonthDaySelectorSegment,
          'x-component-props': {
            ...yearMonthDaySelectorSegment['x-component-props'],
            formatter: expression(`({ cellValue, row, column }) => {
              parseTime(row.promiseReceiveDate, '{y}-{m}-{d}')
            }`),
            'picker-options': {
              disabledDate: (time:any) => {
                const start = new Date()
                return time.getTime() < start.getTime() - 24 * 60 * 60 * 1000
              }
            }
          },
          'x-render-table-column': {
            title: i18nExpression('purchaseDemand.promiseReceiveDate'), // 供方承诺到货日期
            minWidth: 150,
            customRender: true
          },
          ...feedbackLayoutIsPopover,
          'x-validator': {
            required: true,
            message: i18nExpression('common.requiredField'),
            validator: expression(`(value, rule) => {
              if(new Date(value) < (new Date() - 24*60*60*1000)){
                return $t('purchaseOrder.prompt15')// 供方承诺到货日期不能小于当前日期！
              }
            }`)
          },
          'x-read-pretty': '{{$form.readPretty}}'
        },
        refusedReason: {
          type: 'string',
          'x-hidden': expression('$form.query(\'DeliveryNoticeVendor\').get(\'data\').configValue?.vendorSplitReply'),
          'x-render-table-column': {
            title: i18nExpression('orderMod.refuseReason'), // 拒绝原因
            minWidth: 120,
            customRender: true
          },
          'x-component-props': {
            disabled: expression('$table.getRowByIndex($self.index)?.status !== \'REFUSE\'')
          },
          ...feedbackLayoutIsPopover,
          'x-validator': {
            validator: expression(`(value, rule) => {
              const row = $table.getRowByIndex($self.index)
              if(row.status === 'REFUSE' && !value){
                return $t('buyerDeliveryNotice.prompt6')// 请填写拒绝原因
              }
            }`)
          },
          'x-read-pretty': '{{$form.readPretty}}'
        },
        receiveContact: {
          type: 'string',
          title: i18nExpression('oneStopShopping.receiveContacts'), // 收货联系人
          'x-render-table-column': {
            minWidth: 120
          }
        },
        receiveTelephone: {
          type: 'string',
          title: i18nExpression('oneStopShopping.receiveTelephone'), // 收货联系电话
          'x-render-table-column': {
            minWidth: 120
          }
        },
        receiveAddress: {
          type: 'string',
          title: i18nExpression('oneStopShopping.receiveAddress'), // 收货地址
          'x-render-table-column': {
            minWidth: 120
          }
        },
        operation: {
          type: 'void',
          'x-render-table-column': {
            title: i18nExpression('common.operation'),
            width: 80,
            fixed: 'right',
            sortable: false
          },
          'x-component': 'RenderTableButtonList',
          'x-visible': expression('!$form.readPretty && $form.query(\'DeliveryNoticeVendor\').get(\'data\').configValue.vendorSplitReply'),
          properties: {
            split: {
              type: 'void',
              title: i18nExpression('purchaseOrder.split'), // 拆分
              'x-reactions': changeFieldVisibleByDeps(
                [],
                // 待供方确认
                '$table.getRowByIndex($self.index)?.isParentLine'
              ),
              'x-component-props': {
                '@click': expression('({ row }) => $opentSplitDialog(row, $form)')
              }
            },
            delete: {
              type: 'void',
              title: i18nExpression('common.delete'), // 删除
              'x-reactions': changeFieldVisibleByDeps(
                [],
                // 待供方确认
                '!$table.getRowByIndex($self.index)?.isParentLine'
              ),
              'x-component-props': {
                '@click': expression(`
                     ({ rowIndex }) => {
                      // $table.remove(rowIndex)
                      $form.values.detailList.splice(rowIndex,1)
                      $form.values.detailList.forEach((item, index) => {
                        item.lineNum = index + 1 + ''
                      })
                     }
                 `)
              }
            }
          }
        }
      })
    }
  }
}
