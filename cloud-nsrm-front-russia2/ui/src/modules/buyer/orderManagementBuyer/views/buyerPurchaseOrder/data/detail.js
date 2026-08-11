import { parseTime } from '@/utils'

let formItems = _this => {
  return [
    {
      itemAttrs: {
        label: _this.$t('orderMod.buyerOrderSynergy.orderNumber') // 采购订单编号
      },
      uiAttrs: {
        key: 'orderNumber',
        disabled: true
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
          key: 'ceeaOrgId',
          jumpLogin: _this.jumpLogin,
          parentId: -1,
          nodeType: 'OU',
          scope: _this.form,
          disabled: !!_this.detailModel.length || _this.isReadOnly
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
          disabled: !!_this.detailModel.length || _this.isReadOnly,
          jumpLogin: _this.jumpLogin,
          parentId: _this.form.ceeaOrgId
        }
      },
      listeners: {
        select: _this.selectHandler2
      }
    },
    {
      tag: 'date',
      itemAttrs: {
        label: _this.$t('oneStopShopping.orderDate') // 订单日期
      },
      computedUIAttrs: _ => {
        return {
          key: 'ceeaPurchaseOrderDate',
          disabled: _this.isReadOnly
        }
      }
    },
    {
      tag: 'dictSelect',
      itemAttrs: {
        label: _this.$t('oneStopShopping.ifSupplierConfirm'), // 是否供方确认
        rules: [{ required: true, message: _this.$t('orderMod.msgOrder[4]') }]
      },
      computedUIAttrs: _ => {
        return {
          key: 'ceeaIfSupplierConfirm',
          code: 'YES_OR_NO',
          disabled: _this.isReadOnly
        }
      }
    },
    {
      tag: 'dictSelect',
      itemAttrs: {
        label: _this.$t('purchaseDemand.demandType') // 需求类型
      },
      listeners: {
        'change-value': _this.setBugetInfor
      },
      computedUIAttrs: _ => {
        return {
          key: 'demandType',
          code: 'DEMAND_TYPE',
          disabled: _this.form.isManual !== 'Y' || _this.isReadOnly
        }
      }
    },
    {
      tag: 'dictSelect',
      itemAttrs: {
        label: _this.$t('bid_mod.purchaseType'), // 采购类型
        rules: [
          {
            required: true,
            message: _this.$t('logisticsMod.msgPurchaseApply[2]')
          }
        ]
      },
      computedUIAttrs: _ => {
        return {
          key: 'orderType',
          code: 'ORDER_TYPE',
          disabled: !!_this.detailModel.length || _this.isReadOnly
        }
      }
    },
    {
      tag: 'dictSelect',
      itemAttrs: {
        label: _this.$t('bidMod.ifSampleSmallOrder') // 是否样品小批量订单
      },
      computedUIAttrs: _ => {
        return {
          key: 'ifSample',
          code: 'YES_OR_NO',
          disabled: _this.form.isManual !== 'Y' || _this.isReadOnly
        }
      }
    },
    {
      tag: 'dictSelect',
      itemAttrs: {
        label: _this.$t('orderMod.buyerOrderSynergy.orderStatus') // 订单状态
      },
      computedUIAttrs: _ => {
        return {
          key: 'orderStatus',
          code: 'PURCHASE_ORDER',
          disabled: true
        }
      }
    },
    {
      tag: 'dictSelect',
      itemAttrs: {
        label: _this.$t('orderMod.buyerOrderSynergy.warehouseReceiptStatus') // 入库状态
      },
      computedUIAttrs: _ => {
        return {
          key: 'storageStatus',
          code: 'STORAGE_STATUS',
          disabled: true
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: _this.$t('orderMod.buyerOrderSynergy.buyerName') // 采购员
      },
      computedUIAttrs: _ => {
        return {
          key: 'ceeaEmpUsername',
          disabled: true
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: _this.$t('oneStopShopping.department') // 采购部门
      },
      computedUIAttrs: _ => {
        return {
          key: 'ceeaDepartmentName',
          disabled: true
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: _this.$t('dataConfMod.version') // 版本号
      },
      computedUIAttrs: _ => {
        return {
          key: 'orderChangeVersion',
          disabled: true
        }
      }
    },
    {
      tag: 'dictSelect',
      itemAttrs: {
        label: _this.$t('oneStopShopping.receiveAddress'), // 收货地址
        rules: [
          {
            required: true,
            message: _this.$t('orderMod.buyerOrderSynergy.msgReceiveAddress')
          }
        ]
      },
      listeners: {
        'change-value': (val, element) => _this.changeSiteInfo(_this.form, element)
      },
      computedUIAttrs: _ => {
        return {
          key: 'receiveAddress',
          code: _this.form.organizationId,
          customSelectType: _this.form.organizationId ? 'RECEIVE_ADDRESS' : '',
          disabled: _this.isReadOnly || !!_this.detailModel.length
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: _this.$t('oneStopShopping.receiveContacts') // 收货联系人
      },
      computedUIAttrs: _ => {
        return {
          key: 'receiveContact',
          disabled: _this.isReadOnly || !!_this.detailModel.length
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: _this.$t('oneStopShopping.receiveTelephone') // 收货联系电话
      },
      computedUIAttrs: _ => {
        return {
          key: 'receiveTelephone',
          disabled: _this.isReadOnly || !!_this.detailModel.length
        }
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
          disabled: _this.isReadOnly || !!_this.detailModel.length
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: _this.$t('contractMod.linkMan') // 供应商联系人
      },
      computedUIAttrs: _ => {
        return {
          key: 'ceeaSupplierContacts',
          disabled: _this.isReadOnly
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: _this.$t('contractMod.supplierContactPhone') // 供方联系人电话
      },
      computedUIAttrs: _ => {
        return {
          key: 'ceeaReceiveOrderTelephone',
          disabled: _this.isReadOnly
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: _this.$t('purchaseDemand.costType') // 成本类型
      },
      computedUIAttrs: _ => {
        return {
          key: 'ceeaCostType',
          disabled: _this.isReadOnly
        }
      }
    },
    {
      tag: 'quickSearch',
      itemAttrs: {
        label: _this.$t('purchaseDemand.budgetNumber'), // 预算编号
        rules: [
          {
            required: true,
            message: _this.$t('purchaseDemand.fillBudgetNumber'),
            trigger: 'blur'
          }
        ]
      },
      listeners: {
        'close-quicksearch': _this.getBudgetNumObj
      },
      ifRender: _ => {
        return _this.form.demandType === 'NONPRODUCTIVE_DEMAND'
      },
      computedUIAttrs: _ => {
        return {
          key: 'budgetManagementNum',
          showInput: _this.form.budgetManagementNum,
          showKey: 'budgetManagementNumber',
          name: 'scc_pb_budget_management_effective',
          autoQuery: true,
          disabled: _this.isReadOnly
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: _this.$t('oneStopShopping.totalAmountIncludingTax') // 合计金额含税
      },
      computedUIAttrs: _ => {
        return {
          key: 'ceeaTaxAmount',
          disabled: true
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: _this.$t('oneStopShopping.totalAmountExcludingTax') // 合计金额不含税
      },
      computedUIAttrs: _ => {
        return {
          key: 'ceeaNoTaxAmount',
          disabled: true
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: _this.$t('orderMod.buyerOrderSynergy.comments') // 备注
      },
      listeners: {
        input: $event => _this.onInputBlur($event.target)
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
    }
  ]
}

let detailColumn = _this => {
  return [
    {
      attrs: {
        prop: 'lineNum',
        label: _ => _this.$t('purchaseDemand.lineNum'), // 行号
        align: 'center',
        minWidth: '60'
      }
    },
    {
      attrs: {
        prop: 'ceeaRequirementHeadNum',
        label: _ => _this.$t('purchaseDemand.purRequisitionNum'), // 采购申请单号
        align: 'center',
        minWidth: '120'
      }
    },
    {
      attrs: {
        prop: 'ceeaRowNum',
        label: _ => _this.$t('purchaseDemand.rowNum'), // 申请行号
        align: 'center',
        minWidth: '80'
      }
    },
    {
      attrs: {
        prop: 'orderDetailStatus',
        label: _ => _this.$t('orderMod.buyerOrderSynergy.orderDetailStatus'), // 订单行状态
        align: 'center',
        minWidth: '100',
        formatter: value => _this.$getDictLabel('OrderDetailStatus', value)
      }
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
        minWidth: '150',
        showOverflowTooltip: true
      },
      slot: 'materialName'
    },
    {
      attrs: {
        prop: 'requirementQuantity',
        label: _ => _this.$t('purchaseDemand.requirementQuantity'), // 需求数量
        align: 'center',
        minWidth: '80'
      }
    },
    {
      attrs: {
        prop: 'orderNum',
        label: _ => _this.$t('orderMod.buyerOrderSynergy.orderNum'), // 订单数量
        align: 'center',
        minWidth: '80'
      },
      slot: 'orderNum'
    },
    {
      attrs: {
        prop: 'requirementDate',
        label: _ => _this.$t('purchaseDemand.requirementDate'), // 需求日期
        align: 'center',
        minWidth: '110',
        showOverflowTooltip: true,
        formatter: val => this.$parseTime(val)
      }
    },
    {
      attrs: {
        prop: 'ceeaPlanReceiveDate',
        label: _ => _this.$t('purchaseDemand.requirementDate1'), // 要求到货日期
        align: 'center',
        minWidth: '150'
      },
      slot: 'ceeaPlanReceiveDate',
      rules: { required: true, validator: _this.setDateValidate, trigger: 'change' }
    },
    {
      attrs: {
        prop: 'ceeaPromiseReceiveDate',
        label: _ => _this.$t('purchaseDemand.promiseReceiveDate'), // 承诺到货日期
        align: 'center',
        minWidth: '150',
        formatter: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      }
    },
    {
      attrs: {
        prop: 'purchaseProject',
        label: _ => _this.$t('purchaseDemand.purchaseItem'), // 采购项目
        align: 'center',
        minWidth: '120',
        showOverflowTooltip: true
      }
    },
    {
      attrs: {
        prop: 'comments',
        label: _ => _this.$t('purchaseDemand.comments'), // 明细备注
        align: 'center',
        minWidth: '120',
        showOverflowTooltip: true
      },
      slot: 'comments'
    },
    {
      attrs: {
        prop: 'refusedReason',
        label: _ => _this.$t('purchaseDemand.refusedReason'), // 供方拒绝原因
        align: 'center',
        minWidth: '120',
        showOverflowTooltip: true
      }
    },
    {
      attrs: {
        prop: 'categoryName',
        label: _ => _this.$t('purchaseDemand.materialCateSub'), // 物料小类
        align: 'center',
        minWidth: '100',
        showOverflowTooltip: true
      }
    },
    {
      attrs: {
        prop: 'unit',
        label: _ => _this.$t('purchaseDemand.unitCode'), // 单位
        align: 'center',
        minWidth: '60',
        showOverflowTooltip: true,
        formatter: value => _this.$getDictLabel('unit', value)
      }
    },
    {
      attrs: {
        prop: 'ceeaUnitTaxPrice',
        label: _ => _this.$t('purchaseDemand.taxPrice'), // 含税单价
        align: 'center',
        minWidth: '100'
      },
      slot: 'ceeaUnitTaxPrice',
      rules: { required: true, message: _this.$t('orderMod.inputTaxPrice') }
    },
    {
      attrs: {
        prop: 'ceeaUnitNoTaxPrice',
        label: _ => _this.$t('contractMod.notaxPrice'), // 未税单价
        align: 'center',
        minWidth: '100'
      }
    },
    {
      attrs: {
        prop: 'currencyName',
        label: _ => _this.$t('purchaseDemand.currency'), // 币种
        align: 'center',
        minWidth: '100'
      },
      slot: 'currencyName',
      rules: { required: true, message: _this.$t('orderMod.inputCurrency') }
    },
    {
      attrs: {
        prop: 'ceeaTaxKey',
        label: _ => _this.$t('purchaseDemand.taxRate'), // 税率
        align: 'center',
        minWidth: '120',
        formatter: value => _this.$getDictLabel('tax', value)
      },
      slot: 'ceeaTaxKey',
      rules: { required: true, message: _this.$t('orderMod.inputTaxKey') }
    },
    {
      attrs: {
        prop: 'ceeaAmountIncludingTax',
        label: _ => _this.$t('contractMod.amount2'), // 含税金额
        align: 'center',
        minWidth: '100'
      }
    },
    {
      attrs: {
        prop: 'ceeaAmountExcludingTax',
        label: _ => _this.$t('contractMod.excludeTaxPayAmount'), // 未税总额
        align: 'center',
        minWidth: '100'
      }
    },
    {
      attrs: {
        prop: 'ceeaTaxAmount',
        label: _ => _this.$t('contractMod.taxQuota'), // 税额
        align: 'center',
        minWidth: '100'
      }
    },
    {
      attrs: {
        prop: 'usedContractQuantity',
        label: _ => _this.$t('orderMod.contractQuantity'), // 合同已关联数量
        align: 'center',
        minWidth: '120',
        fixed: 'right'
      },
      hidden: () => _this.$store.getters.userType === 'VENDOR',
      slot: 'usedContractQuantity'
    },
    {
      attrs: {
        prop: 'contractInfor',
        label: _ => _this.$t('orderMod.contractInfor'), // 合同信息
        align: 'center',
        minWidth: '120',
        fixed: 'right'
      },
      hidden: () => _this.$store.getters.userType === 'BUYER',
      slot: 'contractInfor'
    },
    {
      attrs: {
        prop: 'operation',
        label: _this.$t('common.operation'), // 操作
        minWidth: '120',
        fixed: 'right'
      },
      slot: 'operation'
    }
  ]
}

// 采购订单明细查询表单
let formDetailSearchList = _this => {
  return [
    {
      prop: 'orderNumber',
      label: () => _this.$t('orderMod.buyerOrderSynergy.orderNumber')
    },
    {
      prop: 'orderType',
      label: () => _this.$t('bid_mod.purchaseType'), // 采购类型
      type: 'dict',
      code: 'ORDER_TYPE'
    },
    // 业务实体
    {
      prop: 'orgIds',
      label: () => _this.$t('oneStopShopping.businessEntity'),
      type: 'OUorganizationSelector',
      multiple: true,
      collapseTags: true
    },
    {
      prop: 'organizationIds',
      parentId: 'orgIds',
      label: () => _this.$t('purchaseDemand.invOrg'),
      type: 'INVorganizationSelector', // 库存组织
      multiple: true,
      collapseTags: true
    },
    // 创建日期
    {
      prop: 'dateList',
      label: () => _this.$t('common.creationDate'),
      type: 'daterange'
    },
    {
      prop: 'vendorId',
      label: () => _this.$t('orderMod.buyerOrderSynergy.vendorName'),
      type: 'quicksearch',
      showKey: 'companyName',
      propKey: 'companyId',
      name: 'scc_sup_company_info_all'
    },
    {
      prop: 'orderStatus',
      label: () => _this.$t('orderMod.buyerOrderSynergy.orderStatus'), // 订单状态
      type: 'dict',
      code: 'PURCHASE_ORDER'
    },
    {
      prop: 'ceeaIfSupplierConfirm',
      label: () => _this.$t('oneStopShopping.ifSupplierConfirm'),
      type: 'dict',
      code: 'YES_OR_NO'
    },
    {
      prop: 'budgetManagementId',
      label: _this.$t('purchaseDemand.budgetNumber'), // 预算编号
      type: 'quicksearch',
      showKey: 'budgetManagementNumber',
      propKey: 'budgetManagementId',
      name: 'scc_pb_budget_management_effective'
    },
    {
      prop: 'sourceSystem',
      label: _this.$t('orderMod.buyerOrderSynergy.sourceSystem'), // 来源系统
      type: 'dict',
      code: 'SOURCE_SYSTERM'
    },
    {
      prop: 'orderDetailStatus',
      label: () => _this.$t('orderMod.buyerOrderSynergy.orderDetailStatus'), // 订单行状态
      type: 'dict',
      code: 'OrderDetailStatus'
    }
  ]
}

// 采购订单明细表格
let formDetailListData = _this => {
  return [
    {
      prop: 'orderNumber',
      showType: 'button',
      btnStyle: 'text',
      label: () => _this.$t('orderMod.buyerOrderSynergy.orderNumber'), // 采购订单编号
      minWidth: 150,
      callback: row => _this.readOne(row)
    },
    {
      prop: 'lineNum',
      label: _this.$t('purSettlementMod.orderLineNumber'), // 采购订单行号
      minWidth: 120
    },
    {
      prop: 'budgetManagementNum',
      label: _this.$t('purchaseDemand.budgetNumber'), // 预算编号
      minWidth: 150
    },
    {
      prop: 'ceeaPurchaseOrderDate',
      label: () => _this.$t('oneStopShopping.orderDate'), // 订单日期
      minWidth: 100,
      formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
    },
    {
      prop: 'orderType',
      label: () => _this.$t('bid_mod.purchaseType'), // 采购类型
      minWidth: 100,
      formattor: val => _this.$getDictLabel('ORDER_TYPE', val)
    },
    {
      prop: 'orderStatus',
      label: () => _this.$t('orderMod.buyerOrderSynergy.orderStatus'), // 订单状态
      minWidth: 100,
      formattor: val => _this.$getDictLabel('PURCHASE_ORDER', val)
    },
    {
      prop: 'orderDetailStatus',
      label: () => _this.$t('orderMod.buyerOrderSynergy.orderDetailStatus'), // 订单行状态
      minWidth: 120,
      formattor: val => _this.$getDictLabel('OrderDetailStatus', val)
    },
    {
      prop: 'ceeaOrgName',
      label: () => _this.$t('purchaseDemand.businessEntity'), // 业务实体
      minWidth: 150
    },
    {
      prop: 'organizationName',
      label: () => _this.$t('purchaseDemand.invOrg'), // 库存组织
      minWidth: 100
    },
    {
      prop: 'vendorCode',
      label: () => _this.$t('purchaseDemand.vendorCode'),
      minWidth: 120
    },
    {
      prop: 'vendorName',
      label: () => _this.$t('purchaseDemand.vendorName'),
      minWidth: 150
    },
    {
      prop: 'buyerName',
      label: () => _this.$t('orderMod.buyerOrderSynergy.buyerName'),
      minWidth: 100
    },
    {
      prop: 'ceeaIfSupplierConfirm',
      label: () => _this.$t('oneStopShopping.ifSupplierConfirm'), // 是否供方确认
      minWidth: 130,
      formattor: val => _this.$getDictLabel('YES_OR_NO', val)
    },
    // 采购申请编号
    {
      prop: 'ceeaRequirementHeadNum',
      label: _this.$t('orderMod.requirementHeadNum'),
      width: 130,
      showType: 'button',
      btnStyle: 'text',
      formattor: val => val || '--',
      callback: row => _this.readPurchaseApplication(row)
    },
    {
      prop: 'ceeaRowNum',
      label: () => _this.$t('bid_mod.purchaseRequestRowNum'), // 采购申请行号
      formattor: val => val || '--',
      minWidth: 120
    },
    {
      prop: 'materialCode',
      label: () => _this.$t('orderMod.buyerOrderSynergy.materialCode'),
      minWidth: 100
    },
    {
      prop: 'materialName',
      label: () => _this.$t('orderMod.buyerOrderSynergy.materialName'),
      minWidth: 100
    },
    {
      prop: 'requirementQuantity',
      label: () => _this.$t('orderMod.buyerOrderSynergy.requirementQuantity'), // 需求数量
      minWidth: 100
    },
    {
      prop: 'orderNum',
      label: () => _this.$t('orderMod.buyerOrderSynergy.orderNum'),
      minWidth: 100
    },
    {
      prop: 'notifiedNum',
      label: () => _this.$t('orderMod.notified'), // 已通知
      desc: _this.$t('orderMod.notifiedNum'), // 通过订单创建送货通知单的累计通知数量+通过订单创建送货单的累计送货数量
      minWidth: 100
    },
    {
      prop: 'notNotifiedNum',
      label: () => _this.$t('orderMod.notNotified'), // 未通知
      desc: _this.$t('orderMod.notNotifiedCal'), // 未通知=订单数量-已通知
      minWidth: 120
    },
    {
      prop: 'inDeliveryNum',
      label: () => _this.$t('orderMod.onWay'), // 在途
      desc: _this.$t('orderMod.onWayCal'), // 在途=已确认发货-累计入库数量
      minWidth: 120
    },
    {
      prop: 'unDeliveryNum',
      label: () => _this.$t('orderMod.unSent'), // 未送
      desc: _this.$t('orderMod.unSentCal'), // 未送=订单数量-已确认发货（已关闭状态为0）
      minWidth: 120
    },
    {
      prop: 'inStockNum',
      label: () => _this.$t('orderMod.inStock'), // 已入库
      desc: _this.$t('orderMod.inStockCal'),
      minWidth: 120
    },
    {
      prop: 'returnNum',
      label: () => _this.$t('orderMod.returned'), // 已退货
      minWidth: 100
    },
    {
      prop: 'requirementDate',
      label: () => _this.$t('purchaseDemand.requirementDate'), // 需求日期
      minWidth: 120,
      formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
    },
    {
      prop: 'ceeaPlanReceiveDate',
      label: () => _this.$t('purchaseDemand.requirementDate1'), // 要求到货日期
      minWidth: 120,
      formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
    },
    {
      prop: 'ceeaPromiseReceiveDate',
      label: () => _this.$t('purchaseDemand.promiseReceiveDate'), // 供方承诺到货日期
      minWidth: 150,
      formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
    },
    {
      prop: 'categoryName',
      label: () => _this.$t('purchaseDemand.materialCateSub'), // 物料小类
      minWidth: 100
    },
    {
      prop: 'unit',
      label: () => _this.$t('orderMod.buyerOrderSynergy.unit'),
      minWidth: 100
    },
    {
      prop: 'ceeaUnitTaxPrice',
      label: () => _this.$t('purchaseDemand.taxPrice'), // 含税单价
      minWidth: 100
    },
    {
      prop: 'ceeaUnitNoTaxPrice',
      label: () => _this.$t('contractMod.notaxPrice'), // 未税单价
      minWidth: 120
    },
    {
      prop: 'currencyName',
      label: () => _this.$t('purchaseDemand.currency'), // 币种
      minWidth: 100
    },
    {
      prop: 'ceeaTaxRate',
      label: () => _this.$t('bidMod.taxRate'), // 税率
      minWidth: 100
    },
    {
      prop: 'ceeaAmountIncludingTax',
      label: () => _this.$t('purSettlementMod.totalAmount'), // 含税金额
      minWidth: 100
    },
    {
      prop: 'ceeaAmountExcludingTax',
      label: () => _this.$t('contractMod.excludeTaxPayAmount'), // 未税总额
      minWidth: 120
    },
    {
      prop: 'ceeaTaxAmount',
      label: () => _this.$t('contractMod.taxQuota'), // 税额
      minWidth: 100
    },
    {
      prop: 'refusedReason',
      label: () => _this.$t('orderMod.buyerOrderSynergy.refuseReason'), // 关闭说明
      minWidth: 100
    }
    // {
    //   prop: 'closedCause',
    //   label: () => _this.$t('orderMod.closeDes'), // 关闭说明
    //   minWidth: 100
    // }
  ]
}

let reportHeader = _this => {
  return [
    {
      label: () => _this.$t('orderMod.orderChangeNumber'),
      prop: 'orderChangeNumber',
      width: 120
    },
    {
      label: () => _this.$t('purchaseDemand.businessEntity'),
      prop: 'orgName',
      width: 100
    },
    {
      label: () => _this.$t('purchaseDemand.invOrg'),
      prop: 'organizationName',
      width: 100
    },
    {
      label: () => _this.$t('orderMod.orderVersion'),
      prop: 'orderChangeVersion',
      width: 110
    },
    {
      label: () => _this.$t('orderMod.buyerOrderSynergy.lineNum'),
      prop: 'lineNum',
      width: 100
    },
    {
      label: () => _this.$t('orderMod.buyerOrderSynergy.materialCode'),
      prop: 'materialCode',
      width: 100
    },
    {
      label: () => _this.$t('orderMod.buyerOrderSynergy.materialName'),
      prop: 'materialName',
      width: 100
    },
    {
      label: () => _this.$t('orderMod.oldOrderNum'),
      prop: 'originOrderNum',
      width: 120
    },
    {
      label: () => _this.$t('orderMod.orderChangeAfterNum'),
      prop: 'orderNum',
      width: 120
    },
    {
      label: () => _this.$t('orderMod.oldPlanReceiveDate'),
      prop: 'originPlanReceiveDate',
      width: 140,
      formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
    },
    {
      label: () => _this.$t('orderMod.changeAfterReceiveDate'),
      prop: 'planReceiveDate',
      width: 150,
      formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
    },
    {
      prop: 'preChangeInfor',
      label: () => _this.$t('orderMod.preChangeInfor'), // 变更前合同信息
      width: 150,
      showType: 'slot',
      slot: 'preChangeInfor'
    },
    {
      prop: 'afterChangeInfor',
      label: () => _this.$t('orderMod.afterChangeInfor'), // 变更后合同信息
      width: 150,
      showType: 'slot',
      slot: 'afterChangeInfor'
    },
    {
      label: () => _this.$t('orderMod.changePerson'),
      prop: 'submittedBy',
      width: 100
    },
    {
      label: () => _this.$t('orderMod.changeCompletTime'),
      prop: 'lastUpdateDate',
      width: 120,
      dataType: 'dateTime'
    }
  ]
}

export {
  formItems, // 采购订单详情 - 单据信息
  detailColumn, // 采购订单详情 - 明细表格
  formDetailSearchList,
  formDetailListData, // 采购订单明细表格
  reportHeader // 采购订单详情 - 采购订单变更记录
}
