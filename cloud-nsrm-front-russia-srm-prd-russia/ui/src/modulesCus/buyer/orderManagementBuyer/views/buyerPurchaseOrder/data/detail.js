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
          disabled: _this.isReadOnly || !_this.isManual
        }
      },
      listeners: {
        select: _this.selectHandler
      }
    },
    {
      tag: 'dictSelect',
      itemAttrs: {
        label: '订单类型'
      },
      computedUIAttrs: _ => {
        return {
          key: 'orderType',
          code: 'NPM_ORDER_TYPE',
          disabled: true
        }
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
          disabled: true
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: '收货人',
        rules: [
          {
            required: true,
            message: '请输入收货人'
          }
        ]
      },
      // listeners: {
      //   'close-quicksearch': _this.getContactObj
      // },
      computedUIAttrs: _ => {
        return {
          key: 'receiveContact',
          // showInput: _this.form.receiveContact,
          // showKey: 'nickname',
          // scopeData: _this.form,
          // name: 'scc_rbac_user_display',
          disabled: _this.isReadOnly || !_this.isManual
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: '收货人联系方式',
        rules: [
          {
            required: true,
            message: '收货人联系方式'
          }
        ]
      },
      computedUIAttrs: _ => {
        return {
          key: 'receiveTelephone',
          disabled: _this.isReadOnly || !_this.isManual
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: _this.$t('oneStopShopping.receiveAddress'), // 收货地址
        rules: [
          {
            required: true,
            message: _this.$t('orderMod.buyerOrderSynergy.msgReceiveAddress')
          }
        ]
      },
      // listeners: {
      //   'change-value': (val, element) => _this.changeSiteInfo(_this.form, element)
      // },
      computedUIAttrs: _ => {
        return {
          key: 'receiveAddress',
          disabled: _this.isReadOnly || !_this.isManual
        }
      }
    },
    {
      tag: 'dictSelect',
      itemAttrs: {
        label: '区域',
        rules: [
          {
            required: true,
            message: '请选择区域'
          }
        ]
      },
      computedUIAttrs: _ => {
        return {
          key: 'extAreaCode',
          code: 'REGION',
          disabled: _this.isReadOnly || !_this.isManual
        }
      }
    },
    {
      tag: 'quickSearch',
      itemAttrs: {
        label: _this.$t('orderMod.buyerOrderSynergy.buyerName'), // 采购员
        rules: [
          {
            required: true,
            message: '请选择采购员'
          }
        ]
      },
      listeners: {
        'close-quicksearch': _this.getCeeaEmpUserObj
      },
      computedUIAttrs: _ => {
        return {
          key: 'ceeaEmpUsername',
          showInput: _this.form.ceeaEmpUsername,
          showKey: 'nickname',
          scopeData: _this.form,
          name: 'scc_rbac_user_display',
          disabled: _this.isReadOnly || !_this.isManual
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
        label: '采购员电话'
      },
      computedUIAttrs: _ => {
        return {
          key: 'extPurchaserPhone',
          disabled: true
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: '采购员邮箱'
      },
      computedUIAttrs: _ => {
        return {
          key: 'extPurchaserEmail',
          disabled: true
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: '采购单位'
      },
      computedUIAttrs: _ => {
        return {
          key: 'extPurchaserOrgName',
          disabled: true
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
          disabled: _this.isReadOnly || !_this.isManual
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
          key: 'extVendorContacts',
          disabled: true
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: '供应商联系人电话'
      },
      computedUIAttrs: _ => {
        return {
          key: 'extVendorPhone',
          disabled: true
        }
      }
    },
    {
      tag: 'dictSelect',
      itemAttrs: {
        label: '付款方式',
        rules: [{ required: true, message: '请选择付款方式' }]
      },
      computedUIAttrs: _ => {
        return {
          key: 'paymentMethod',
          code: 'JC_PAYMENT_WAY',
          disabled: _this.isReadOnly || !_this.isManual
        }
      }
    },
    {
      tag: 'dictSelect',
      itemAttrs: {
        label: '付款条款',
        rules: [{ required: true, message: '请选择付款条款' }]
      },
      computedUIAttrs: _ => {
        return {
          key: 'termOfPayment',
          code: 'PAYMENT_PROVISION',
          disabled: _this.isReadOnly || !_this.isManual
        }
      }
    },
    {
      tag: 'dictSelect',
      itemAttrs: {
        label: '币种',
        rules: [{ required: true, message: '请选择币种' }]
      },
      computedUIAttrs: _ => {
        return {
          key: 'rfqSettlementCurrency',
          code: 'currency',
          disabled: true
        }
      }
    },
    {
      tag: 'dictSelect',
      itemAttrs: {
        label: '订单性质',
        rules: [{ required: true, message: '请选择订单性质' }]
      },
      computedUIAttrs: _ => {
        return {
          key: 'extOrderProperty',
          code: 'ORDER_PROPERTY',
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
      tag: 'quickSearch',
      ifRender: form => form.orderType === 'MANUAL',
      itemAttrs: {
        label: '采购科长',
        rules: [
          {
            required: true,
            message: '请选择采购科长'
          }
        ]
      },
      listeners: {
        'close-quicksearch': _this.getExtApproveUseObj
      },
      computedUIAttrs: _ => {
        return {
          key: 'extApproveUserName',
          showInput: _this.form.extApproveUserName,
          showKey: 'nickname',
          scopeData: _this.form,
          name: 'scc_rbac_user_display',
          disabled: _this.isReadOnly || !_this.isManual
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: '订单备注'
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
        prop: 'ceeaRequirementHeadNum',
        label: _ => _this.$t('purchaseDemand.purRequisitionNum'), // 采购申请单号
        align: 'center',
        minWidth: '150'
      }
    },
    {
      attrs: {
        prop: 'ceeaRowNum',
        label: _ => _this.$t('purchaseDemand.rowNum'), // 申请行号
        align: 'center',
        minWidth: '100'
      }
    },
    {
      attrs: {
        prop: 'extDetailStatus',
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
        minWidth: '150',
        showOverflowTooltip: true
      },
      rules: { required: true, message: _this.$t('common.pleasefinishRequired') },
      slot: 'materialCode'
    },
    {
      attrs: {
        prop: 'materialName',
        label: _ => _this.$t('purchaseDemand.itemName'), // 物料名称
        align: 'center',
        minWidth: '150',
        showOverflowTooltip: true
      }
      // rules: { required: true, message: _this.$t('common.pleasefinishRequired') },
      // slot: 'materialName'
    },
    {
      attrs: {
        prop: 'specification',
        label: '规格型号',
        align: 'center',
        minWidth: '100',
        showOverflowTooltip: true
      }
      // rules: { required: true, message: _this.$t('common.pleasefinishRequired') },
      // slot: 'specification'
    },
    {
      attrs: {
        prop: 'extBrand',
        label: '品牌',
        align: 'center',
        minWidth: '100',
        showOverflowTooltip: true
      },
      // rules: { required: true, message: _this.$t('common.pleasefinishRequired') },
      slot: 'extBrand'
    },
    {
      attrs: {
        prop: 'unitCode',
        label: '基本计量单位',
        align: 'center',
        minWidth: '150',
        showOverflowTooltip: true
      },
      // rules: { required: true, message: _this.$t('common.pleasefinishRequired') },
      slot: 'unitCode'
    },
    {
      attrs: {
        prop: 'requirementQuantity',
        label: _ => _this.$t('purchaseDemand.requirementQuantity'), // 需求数量
        align: 'center',
        minWidth: '100'
      }
    },
    {
      attrs: {
        prop: 'orderNum',
        label: _ => _this.$t('orderMod.buyerOrderSynergy.orderNum'), // 订单数量
        align: 'center',
        minWidth: '100'
      },
      rules: { required: true, message: _this.$t('common.pleasefinishRequired') },
      slot: 'orderNum'
    },
    {
      attrs: {
        prop: 'requirementDate',
        label: _ => _this.$t('purchaseDemand.requirementDate'), // 需求日期
        align: 'center',
        minWidth: '150',
        showOverflowTooltip: true
      },
      rules: { required: true, message: _this.$t('common.pleasefinishRequired') },
      slot: 'requirementDate'
    },
    {
      attrs: {
        prop: 'deliveryDate',
        label: '供方承诺到货日期',
        align: 'center',
        minWidth: '150'
      },
      rules: { required: true, message: _this.$t('common.pleasefinishRequired') },
      slot: 'deliveryDate'
    },
    {
      attrs: {
        prop: 'ceeaPromiseReceiveDate',
        label: _ => _this.$t('实际送货日期'),
        align: 'center',
        minWidth: '150',
        formatter: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      }
    },
    {
      attrs: {
        prop: 'categoryName',
        label: '品类',
        align: 'center',
        minWidth: '150',
        showOverflowTooltip: true
      }
      // rules: { required: true, message: _this.$t('common.pleasefinishRequired') },
      // slot: 'categoryName'
    },
    {
      attrs: {
        prop: 'ceeaUnitTaxPrice',
        label: _ => _this.$t('purchaseDemand.taxPrice'), // 含税单价
        align: 'center',
        minWidth: '100'
      }
    },
    {
      attrs: {
        prop: 'ceeaUnitNoTaxPrice',
        label: _ => _this.$t('contractMod.notaxPrice'), // 未税单价
        align: 'center',
        minWidth: '100'
      },
      rules: { required: true, message: _this.$t('common.pleasefinishRequired') },
      slot: 'ceeaUnitNoTaxPrice'
    },
    {
      attrs: {
        prop: 'ceeaTaxKey',
        label: _ => _this.$t('purchaseDemand.taxRate'), // 税率
        align: 'center',
        minWidth: '100',
        formatter: value => _this.$getDictLabel('tax', value)
      },
      rules: { required: true, message: _this.$t('common.pleasefinishRequired') },
      slot: 'ceeaTaxKey'
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
        prop: 'extUserName',
        label: '使用人',
        align: 'center',
        minWidth: '100'
      },
      rules: { required: true, message: _this.$t('common.pleasefinishRequired') },
      slot: 'extUserName'
    },
    {
      attrs: {
        prop: 'extUseDepartmentCode',
        label: '使用部门',
        align: 'center',
        minWidth: '100'
      },
      rules: { required: true, message: _this.$t('common.pleasefinishRequired') },
      slot: 'extUseDepartmentCode'
    },
    {
      attrs: {
        prop: 'extWarrantyPeriod',
        label: '质保期（自然日）',
        align: 'center',
        minWidth: '150'
      },
      slot: 'extWarrantyPeriod'
    },
    {
      attrs: {
        prop: 'extInvoiceType',
        label: '发票类型',
        align: 'center',
        minWidth: '100'
      },
      slot: 'extInvoiceType'
    },
    {
      attrs: {
        prop: 'comments',
        label: _ => _this.$t('purchaseDemand.comments'), // 明细备注
        align: 'center',
        minWidth: '150',
        showOverflowTooltip: true
      },
      slot: 'comments'
    },
    {
      attrs: {
        prop: 'extAttachId',
        label: '订单行附件',
        align: 'center',
        minWidth: '180',
        showOverflowTooltip: true
      },
      slot: 'extAttachId'
    },
    {
      attrs: {
        prop: 'operation',
        label: _this.$t('common.operation'), // 操作
        minWidth: '100',
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
      prop: 'ceeaOrgId',
      label: () => _this.$t('oneStopShopping.businessEntity'),
      type: 'OUorganizationSelector',
      multiple: true,
      collapseTags: true
    },
    {
      prop: 'orderStatus',
      label: () => _this.$t('orderMod.buyerOrderSynergy.orderStatus'), // 订单状态
      type: 'dict',
      code: 'PURCHASE_ORDER'
    },
    {
      prop: 'materialCode',
      label: () => _this.$t('orderMod.buyerOrderSynergy.materialCode')
    },
    {
      prop: 'ceeaEmpUsername',
      label: () => _this.$t('orderMod.buyerOrderSynergy.buyerName')
    },
    {
      prop: 'vendorName',
      label: () => _this.$t('purchaseDemand.vendorName')
    },
    {
      prop: 'ceeaPurchaseOrderDate',
      label: () => _this.$t('oneStopShopping.orderDate'),
      type: 'daterange'
    },
    {
      prop: 'extBuyType',
      label: () => _this.$t('采购类型'),
      type: 'dict',
      code: 'EXT_PR_BUY_TYPE'
    },
    {
      prop: 'ceeaRequirementHeadNum',
      label: () => _this.$t('采购申请单号')
    },
    {
      prop: 'materialName',
      label: () => _this.$t('物料名称')
    },
    {
      prop: 'specification',
      label: () => _this.$t('规格')
    }
  ]
}

// 采购订单明细表格
let formDetailListData = _this => {
  return [
    {
      prop: 'ceeaOrgName',
      label: () => _this.$t('purchaseDemand.businessEntity'), // 业务实体
      formattor: (val, row) => {
        return row.orderId.ceeaOrgName
      },
      minWidth: 150
    },
    {
      prop: 'vendorName',
      label: () => _this.$t('purchaseDemand.vendorName'),
      formattor: (val, row) => {
        return row.orderId.vendorName
      },
      minWidth: 150
    },
    {
      prop: 'ceeaEmpUsername',
      label: () => _this.$t('orderMod.buyerOrderSynergy.buyerName'),
      formattor: (val, row) => {
        return row.orderId.ceeaEmpUsername
      },
      minWidth: 100
    },
    {
      prop: 'extPurchaserPhone',
      label: '采购员电话',
      formattor: (val, row) => {
        return row.orderId.extPurchaserPhone
      },
      minWidth: 120
    },
    {
      prop: 'ceeaPurchaseOrderDate',
      label: () => _this.$t('oneStopShopping.orderDate'), // 订单日期
      minWidth: 120,
      formattor: (val, row) => {
        return row.orderId.ceeaPurchaseOrderDate ? parseTime(row.orderId.ceeaPurchaseOrderDate, '{y}-{m}-{d}') : ''
      }
    },
    {
      prop: 'orderNumber',
      showType: 'button',
      btnStyle: 'text',
      label: () => _this.$t('orderMod.buyerOrderSynergy.orderNumber'), // 采购订单编号
      formattor: (val, row) => {
        return row.orderId.orderNumber || '--'
      },
      minWidth: 150,
      callback: row => _this.readOne(row.orderId)
    },
    {
      prop: 'lineNum',
      label: _this.$t('purSettlementMod.orderLineNumber'), // 采购订单行号
      formattor: val => val || '--',
      minWidth: 120
    },
    // 采购申请编号
    {
      prop: 'ceeaRequirementHeadNum',
      label: _this.$t('orderMod.requirementHeadNum'),
      width: 150,
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
      prop: 'extStatus',
      label: () => _this.$t('orderMod.buyerOrderSynergy.orderStatus'), // 订单状态
      minWidth: 120,
      formattor: (val, row) => {
        return _this.$getDictLabel('PURCHASE_ORDER', row.orderId.extStatus || row.orderId.orderStatus)
      }
    },
    {
      prop: 'extDetailStatus',
      label: '订单明细状态',
      minWidth: 120,
      formattor: val => _this.$getDictLabel('OrderDetailStatus', val)
    },
    {
      prop: 'materialCode',
      label: () => _this.$t('orderMod.buyerOrderSynergy.materialCode'),
      minWidth: 120
    },
    {
      prop: 'materialName',
      label: () => _this.$t('orderMod.buyerOrderSynergy.materialName'),
      minWidth: 120
    },
    {
      prop: 'specification',
      label: () => _this.$t('cusEntry.orderMod.extMaterialItemType'),
      minWidth: 120
    },
    {
      prop: 'extBrand',
      label: () => _this.$t('cusEntry.orderMod.brand'),
      minWidth: 120
    },
    {
      prop: 'requirementQuantity',
      label: () => _this.$t('orderMod.buyerOrderSynergy.requirementQuantity'), // 需求数量
      minWidth: 120
    },
    {
      prop: 'orderNum',
      label: () => _this.$t('orderMod.buyerOrderSynergy.orderNum'),
      minWidth: 120
    },
    {
      prop: 'remainNum',
      label: '订单剩余数量',
      // 剩余订单数量 = 该行的订单数量-在途数量-入库数量
      formattor: (val, row) => row.orderNum - (row.receiveSum - row.storageNum) - row.storageNum,
      minWidth: 120
    },
    {
      prop: 'inDeliveryNum',
      label: '在途数量',
      // 在途数量 = 已创建送货单数量 - 入库数量
      formattor: (val, row) => row.receiveSum - row.storageNum,
      minWidth: 120
    },
    {
      prop: 'storageNum',
      label: '入库数量',
      minWidth: 120
    },
    {
      prop: 'deliveryDate',
      label: () => _this.$t('供方承诺到货日期'), // 要求到货日期
      minWidth: 120,
      formattor: val => val ? parseTime(val, '{y}-{m}-{d}') : ''
    },
    {
      prop: 'confirmNum',
      label: '供方确认订单数量', // 取订单数量
      minWidth: 150
    },
    {
      prop: 'ceeaPromiseReceiveDate',
      label: () => _this.$t('实际送货日期'), // 供方承诺到货日期
      minWidth: 150,
      formattor: val => val ? parseTime(val, '{y}-{m}-{d}') : ''
    },
    {
      prop: 'categoryName',
      label: () => _this.$t('purchaseDemand.materialCateSub'), // 物料小类
      minWidth: 120
    },
    {
      prop: 'unit',
      label: () => _this.$t('orderMod.buyerOrderSynergy.unit'),
      minWidth: 120
    },
    {
      prop: 'ceeaUnitTaxPrice',
      label: () => _this.$t('purchaseDemand.taxPrice'), // 含税单价
      minWidth: 120
    },
    {
      prop: 'ceeaUnitNoTaxPrice',
      label: () => _this.$t('contractMod.notaxPrice'), // 未税单价
      minWidth: 120
    },
    {
      prop: 'currencyName',
      label: () => _this.$t('purchaseDemand.currency'), // 币种
      minWidth: 120
    },
    {
      prop: 'ceeaTaxRate',
      label: () => _this.$t('bidMod.taxRate'), // 税率
      minWidth: 120
    },
    {
      prop: 'ceeaAmountIncludingTax',
      label: '含税总额',
      minWidth: 120
    },
    {
      prop: 'ceeaAmountExcludingTax',
      label: () => _this.$t('contractMod.excludeTaxPayAmount'), // 未税总额
      minWidth: 120
    },
    {
      prop: 'extUserName',
      label: '使用人',
      minWidth: 120
    },
    {
      prop: 'extUseDepartmentName',
      label: '使用部门',
      minWidth: 120
    },
    {
      prop: 'closedCause',
      label: '取消原因',
      width: 120
    },
    {
      prop: 'comments',
      label: '订单备注',
      minWidth: 120
    },
    {
      prop: 'extOrderRemark',
      label: '供应商备注',
      minWidth: 120
    },
    {
      prop: 'extAttachId',
      label: '订单附件',
      showType: 'slot',
      slot: 'extAttachId',
      minWidth: 200
    },
    {
      prop: 'extVendorContacts',
      label: '供应商联系人',
      minWidth: 150,
      formattor: (val, row) => {
        return row.orderId.extVendorContacts || '--'
      }
    },
    {
      prop: 'extVendorPhone',
      label: '供应商联系人电话',
      minWidth: 150,
      formattor: (val, row) => {
        return row.orderId.extVendorPhone || '--'
      }
    },
    {
      prop: 'extInvoiceType',
      label: '发票类型',
      minWidth: 150,
      formattor: (val, row) => {
        return _this.$getDictLabel('EXT_SOU_INQ_ORDER_INVOICE_TYPE', row.extInvoiceType)
      }
    },
    {
      prop: 'extDeliveryCycle',
      label: '交货周期',
      minWidth: 150
    },
    {
      prop: 'extBuyType',
      label: '采购类型',
      minWidth: 120,
      formattor: (val, row) => {
        return _this.$getDictLabel('EXT_PR_BUY_TYPE', row.extBuyType)
      }
    }
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
      width: 120
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
