import { parseTime } from '@/utils'

let formItems = _this => {
  return [
    {
      itemAttrs: {
        label: _this.$t('orderMod.buyerOrderSynergy.deliveryNoticeNum') // 送货通知单号
      },
      computedUIAttrs: _ => {
        return {
          key: 'deliveryNoticeNumber',
          disabled: true
        }
      }
    },
    {
      tag: 'dictSelect',
      itemAttrs: {
        label: _this.$t('orderMod.deliveryNoteStatus') // 单据状态
      },
      computedUIAttrs: _ => {
        return {
          key: 'status',
          code: 'DELIVERY_NOTICE_STATUS_NEW',
          disabled: true
        }
      }
    },
    {
      tag: 'dictSelect',
      itemAttrs: {
        label: () => _this.$t('orderMod.buyerOrderSynergy.createdBy') // 创建人
      },
      computedUIAttrs: _ => {
        return {
          key: 'createdUserName',
          disabled: true
        }
      }
    },
    {
      tag: 'date',
      itemAttrs: {
        label: () => _this.$t('vendorMod.creationDate2') // 创建时间
      },
      computedUIAttrs: _ => {
        return {
          key: 'creationDate',
          disabled: true
        }
      }
    },
    {
      tag: 'organizationSelector',
      itemAttrs: {
        label: _this.$t('purchaseDemand.businessEntity'), // 业务实体
        rules: [
          {
            required: true,
            message: _this.$t('purchaseDemand.orgIdTips')
          }
        ]
      },
      computedUIAttrs: _ => {
        return {
          key: 'orgId',
          parentId: -1,
          nodeType: 'OU',
          scope: _this.form,
          disabled: !!_this.detailModelReal.length || _this.isReadOnly
        }
      },
      listeners: {
        select: _this.selectHandler
      }
    },
    {
      tag: 'organizationSelector',
      itemAttrs: {
        label: _this.$t('purchaseDemand.invOrg'), // 库存组织
        rules: [
          {
            required: true,
            message: _this.$t('purchaseDemand.organizationIdTips')
          }
        ]
      },
      computedUIAttrs: _ => {
        return {
          key: 'organizationId',
          nodeType: 'INV',
          disabled: !!_this.detailModelReal.length || _this.isReadOnly,
          parentId: _this.form.orgId
        }
      },
      listeners: {
        select: _this.selectHandler2
      }
    },
    {
      tag: 'quickSearch',
      itemAttrs: {
        label: _this.$t('common.vendor'), // 供应商
        rules: [
          {
            required: true,
            message: _this.$t('vendorMod.msgVendor')
          }
        ]
      },
      listeners: {
        'close-quicksearch': _this.getVendorObj
      },
      computedUIAttrs: _ => {
        return {
          key: 'vendorName',
          showInput: _this.form.vendorName,
          showKey: 'companyName',
          scopeData: _this.form,
          name: 'scc_sup_company_info_all',
          disabled: _this.isReadOnly || !!_this.detailModelReal.length
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: _this.$t('orderMod.buyerOrderSynergy.comments') // 备注
      },
      computedUIAttrs: _ => {
        return {
          key: 'comments',
          span: 24,
          type: 'textarea',
          maxlength: 80,
          autosize: { minRows: 2, maxRows: 4 },
          showWordLimit: true,
          disabled: _this.isReadOnly
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: _this.$t('orderMod.vendorRejectDesc') // 供方拒绝说明
      },
      ifRender: form => form.status === 'REFUSE',
      computedUIAttrs: _ => {
        return {
          key: 'refuseReason',
          span: 24,
          type: 'textarea',
          autosize: { minRows: 2, maxRows: 4 },
          showWordLimit: true,
          disabled: true
        }
      }
    }
  ]
}

let detailColumn = _this => {
  return [
    {
      attrs: {
        prop: 'lineNum',
        label: _ => _this.$t('orderMod.noticeLineNum'), // 送货通知行号
        align: 'center',
        minWidth: '120',
        showOverflowTooltip: true
      }
    },
    {
      attrs: {
        prop: 'status',
        label: _ => _this.$t('orderMod.rowStatus'), // 行状态
        align: 'center',
        minWidth: '100',
        formatter: value => _this.$getDictLabel('DELIVERY_NOTICE_DETAIL_STATUS_NEW', value),
        showOverflowTooltip: true
      }
    },
    {
      attrs: {
        prop: 'orderNumber',
        label: _ => _this.$t('orderMod.buyerOrderSynergy.orderNumber') + '|' + _this.$t('vendorMod.relegation.lineNumber'), // 采购订单编号|行号
        align: 'center',
        minWidth: '150',
        showOverflowTooltip: true
      },
      slot: 'orderNumberSlot'
    },
    {
      attrs: {
        prop: 'materialCode',
        label: _ => _this.$t('purchaseDemand.itemCode'), // 物料编码
        align: 'center',
        minWidth: '120',
        showOverflowTooltip: true
      }
    },
    {
      attrs: {
        prop: 'materialName',
        label: _ => _this.$t('purchaseDemand.itemName'), // 物料名称
        align: 'center',
        minWidth: '120',
        showOverflowTooltip: true
      }
    },
    {
      attrs: {
        prop: 'unit',
        label: _ => _this.$t('purchaseDemand.unitCode'), // 单位
        align: 'center',
        minWidth: '100',
        showOverflowTooltip: true,
        formatter: value => _this.$getDictLabel('unit', value)
      }
    },
    {
      attrs: {
        prop: 'categoryName',
        label: _ => _this.$t('common.category'), // 品类
        align: 'center',
        minWidth: '100',
        showOverflowTooltip: true
      }
    },
    {
      attrs: {
        prop: 'orderNum',
        label: _ => _this.$t('orderMod.buyerOrderSynergy.orderNum'), // 订单数量
        align: 'center',
        minWidth: '100',
        showOverflowTooltip: true
      }
    },
    {
      attrs: {
        prop: 'noticeSum',
        label: _ => _this.$t('orderMod.surplusDeliveryQuantity1'), // 本次通知数量
        align: 'center',
        minWidth: '130',
        showOverflowTooltip: true
      }
    },
    {
      attrs: {
        prop: 'deliveryQuantity',
        label: _ => _this.$t('orderMod.deliveryQuantityHeader'), // 已发货数量
        align: 'center',
        minWidth: '120',
        showOverflowTooltip: true
      },
      headerSlot: 'deliveryQuantityHeader'
    },
    {
      attrs: {
        prop: 'warehouseQuantity',
        label: _ => _this.$t('orderMod.warehouseQuantity'), // 已入库数量
        align: 'center',
        minWidth: '120',
        showOverflowTooltip: true
      },
      headerSlot: 'warehouseQuantityHeader'
    },
    {
      attrs: {
        prop: 'returnedQuantity',
        label: _ => _this.$t('orderMod.returnedQuantity'), // 已退货数量
        align: 'center',
        minWidth: '120',
        showOverflowTooltip: true
      }
    },
    {
      attrs: {
        prop: 'requirementDate',
        label: _ => _this.$t('purchaseDemand.requirementDate'), // 需求日期
        align: 'center',
        minWidth: '150',
        showOverflowTooltip: true,
        formatter: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      }
    },
    {
      attrs: {
        prop: 'receiveDate',
        label: _ => _this.$t('purchaseDemand.requirementDate1'), // 要求到货日期
        align: 'center',
        minWidth: '150',
        showOverflowTooltip: true,
        formatter: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      }
    },
    {
      attrs: {
        prop: 'promiseReceiveDate',
        label: _ => _this.$t('purchaseDemand.promiseReceiveDate'), // 供方承诺到货日期
        align: 'center',
        minWidth: '150',
        showOverflowTooltip: true,
        formatter: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      }
    },
    {
      attrs: {
        prop: 'receiveContact',
        label: _ => _this.$t('oneStopShopping.receiveContacts'), // 收货联系人
        align: 'center',
        minWidth: '120',
        showOverflowTooltip: true
      }
    },
    {
      attrs: {
        prop: 'receiveTelephone',
        label: _ => _this.$t('oneStopShopping.receiveTelephone'), // 收货联系电话
        align: 'center',
        minWidth: '120',
        showOverflowTooltip: true
      }
    },
    {
      attrs: {
        prop: 'receiveAddress',
        label: _ => _this.$t('oneStopShopping.receiveAddress'), // 收货地址
        align: 'center',
        minWidth: '120',
        showOverflowTooltip: true
      }
    }
  ]
}

export {
  formItems, // 基础信息
  detailColumn // 明细行
}
