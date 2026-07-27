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
        // label: '订单类型'
        label:  _this.$t('orderMod.buyerOrderSynergy.orderType')
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
      slot: 'ceeaPurchaseOrderDate',
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
        // label: '收货人',
        label: _this.$t('cusEntry.orderMod.extReceiveContact'),
        rules: [
          {
            required: true,
            // message: '请输入收货人'
            message: _this.$t('cusEntry.supplement20250121.pleaseEnterRecipient')
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
        // label: '收货人联系方式',
        label: _this.$t('cusEntry.orderMod.extReceiveTelephone'),
        rules: [
          {
            required: true,
            // message: '收货人联系方式'
            message: _this.$t('cusEntry.orderMod.extReceiveTelephone')
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
        // label: '区域',
        label: _this.$t('vendorMod.area1'),
        rules: [
          {
            required: true,
            // message: '请选择区域'
            message: _this.$t('cusEntry.supplement20250121.pleaseSelectRegion')
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
            // message: '请选择采购员'
            message: _this.$t('vendorMod.purchaseAgent')
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
        // label: '采购员电话'
        label: _this.$t('cusEntry.supplement20250121.purchasingOfficersPhoneNumber')
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
        // label: '采购员邮箱'
        label: _this.$t('cusEntry.supplement20250121.buyerEmail')
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
        // label: '采购单位'
        label: _this.$t('cusEntry.reportManagement.createUserOrgOuName')
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
        // label: '供应商联系人电话'
        label: _this.$t('cusEntry.orderMod.extVendorPhone')
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
        // label: '付款方式',
        label: _this.$t('vendorMod.paymentMethod'),
        // rules: [{ required: true, message: '请选择付款方式' }]
        rules: [{ required: true, message: _this.$t('cusEntry.tipMessage.paymentMethodMsg')}]
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
        // label: '付款条款',
        label: _this.$t('route.paymentType'),
        // rules: [{ required: true, message: '请选择付款条款' }]
        rules: [{ required: true, message: _this.$t('cusEntry.tipMessage.paymentTermMsg') }]
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
        // label: '本位币',
        label: _this.$t('bidMod.standardCurrency'),
        // rules: [{ required: true, message: '请选择本位币' }]
        rules: [{ required: true, message: _this.$t('cusEntry.supplement20250121.pleaseSelectBaseCurrency') }]
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
        // label: '订单性质',
        label: _this.$t('cusEntry.supplement20250121.orderNature'),
        // rules: [{ required: true, message: '请选择订单性质' }]
        rules: [{ required: true, message: _this.$t('cusEntry.supplement20250121.pleaseSelectOrderNature') }]
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
        label: _this.$t('oneStopShopping.totalAmountIncludingTax') // 合计含税金额（卢布）
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
        label: _this.$t('oneStopShopping.totalAmountExcludingTax') // 合计不含税金额（卢布）
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
        // label: '采购科长',
        label: _this.$t('cusEntry.supplement20250121.purchaseSectionChief'),
        rules: [
          {
            required: true,
            // message: '请选择采购科长'
            message: _this.$t('cusEntry.supplement20250121.pleaseSelectPurchaseSectionChief')
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
        // label: '订单备注'
        label:  _this.$t('cusEntry.supplement20250121.orderRemarks')
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

// 采购订单详情 - 预付信息
let formItems1 = _this => {
  return [
    {
      tag: 'dictSelect',
      itemAttrs: {
        label: _this.$t('cusEntry.orderMod.ifPrepayment') // 是否预付款
      },
      computedUIAttrs: _ => {
        return {
          key: 'extIfPrepayment',
          code: 'YES_OR_NO',
          disabled: true
        }
      }
    },
    {
      itemAttrs: {
        label: _this.$t('cusEntry.orderMod.prepaymentRatio') // 预付比例
      },
      uiAttrs: {
        key: 'extPrepaymentRatio',
        disabled: true
      }
    },
    {
      tag: 'dictSelect',
      itemAttrs: {
        label: _this.$t('cusEntry.orderMod.extErpPaymentStatus') //  ERP付款状态
      },
      uiAttrs: {
        key: 'extErpPaymentStatus',
        code: 'EXT_ERP_PAYMENT_STATUS',
        disabled: true
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
        // label: '规格型号',
        label: _ => _this.$t('vendorMod.specification'),
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
        // label: '品牌',
        label: _ => _this.$t('dataConfMod.band'),
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
        // label: '基本计量单位',
        label: _ => _this.$t('cusEntry.inq.baseMeasurmentUnit'),
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
        minWidth: '180',
        showOverflowTooltip: true
      },
      rules: { required: true, message: _this.$t('common.pleasefinishRequired') },
      slot: 'requirementDate'
    },
    {
      attrs: {
        prop: 'deliveryDate',
        // label: '供方承诺到货日期',
        label: _ => _this.$t('purchaseDemand.promiseReceiveDate'),
        align: 'center',
        minWidth: '180'
      },
      rules: { required: true, message: _this.$t('common.pleasefinishRequired') },
      slot: 'deliveryDate'
    },
    {
      attrs: {
        prop: 'ceeaPromiseReceiveDate',
        // label: _ => _this.$t('实际送货日期'),
        label: _ => _this.$t('cusEntry.supplement20250121.actualDeliveryDate'),
        align: 'center',
        minWidth: '150',
        formatter: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      }
    },
    {
      attrs: {
        prop: 'categoryName',
        // label: '品类',
        label: _ => _this.$t('common.category'),
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
        // label: '使用人',
        label: _ => _this.$t('cusEntry.orderMod.extUserName'),
        align: 'center',
        minWidth: '100'
      },
      rules: { required: true, message: _this.$t('common.pleasefinishRequired') },
      slot: 'extUserName'
    },
    {
      attrs: {
        prop: 'extUseDepartmentCode',
        // label: '使用部门',
        label: _ => _this.$t('cusEntry.orderMod.extUseDepartmentName'),
        align: 'center',
        minWidth: '100'
      },
      rules: { required: true, message: _this.$t('common.pleasefinishRequired') },
      slot: 'extUseDepartmentCode'
    },
    {
      attrs: {
        prop: 'extWarrantyPeriod',
        // label: '质保期（自然日）',
        label: _ => _this.$t('cusEntry.orderMod.extWarrantyPeriod'),
        align: 'center',
        minWidth: '150'
      },
      slot: 'extWarrantyPeriod'
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
        // label: '订单行附件',
        label:  _ => _this.$t('cusEntry.supplement20250121.WorderLineAttachment'),
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
      label: () => _this.$t('bid_mod.purchaseType'),
      type: 'dict',
      code: 'EXT_PR_BUY_TYPE'
    },
    {
      prop: 'ceeaRequirementHeadNum',
      label: () => _this.$t('purchaseDemand.purRequisitionNum')
    },
    {
      prop: 'materialName',
      label: () => _this.$t('common.materialName')
    },
    {
      prop: 'specification',
      label: () => _this.$t('contractMod.eqpSpecification')
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
      // label: '采购员电话',
      label: () => _this.$t('cusEntry.supplement20250121.purchasingOfficersPhoneNumber'),
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
      // label: '订单明细状态',
      label: () => _this.$t('cusEntry.supplement20250121.orderDetailStatus'),
      minWidth: 120,
      formattor: val => _this.$getDictLabel('OrderDetailStatus', val)
    },
    {
      prop: 'materialCode',
      label: () => _this.$t('orderMod.buyerOrderSynergy.materialCode'),
      minWidth: 120
    },
    {
      prop: 'materialNameShow',
      label: () => _this.$t('orderMod.buyerOrderSynergy.materialName'),
      minWidth: 120
    },
    {
      prop: 'specificationShow',
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
      // label: '订单剩余数量',
      label: () => _this.$t('cusEntry.supplement20250121.orderRemainingQuantity'),
      // 剩余订单数量 = 该行的订单数量-在途数量-入库数量
      formattor: (val, row) => row.orderNum - (row.receiveSum - row.storageNum) - row.storageNum,
      minWidth: 120
    },
    {
      prop: 'storageNum',
      // label: '入库数量',
      label: () => _this.$t('contractMod.warehouseReceiptQuantity'),
      minWidth: 120
    },
    {
      prop: 'deliveryDate',
      // label: () => _this.$t('供方承诺到货日期'), // 要求到货日期
      label: () => _this.$t('purchaseDemand.promiseReceiveDate'), // 要求到货日期
      minWidth: 120,
      formattor: val => val ? parseTime(val, '{y}-{m}-{d}') : ''
    },
    {
      prop: 'confirmNum',
      // label: '供方确认订单数量', // 取订单数量
      label: () => _this.$t('purchaseOrder.confirmNum'), // 取订单数量
      minWidth: 150
    },
    {
      prop: 'ceeaPromiseReceiveDate',
      // label: () => _this.$t('实际送货日期'), // 供方承诺到货日期
      label: () => _this.$t('cusEntry.supplement20250121.actualDeliveryDate'), // 供方承诺到货日期
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
      prop: 'extExchangeRate',
      label: () => _this.$t('orderMod.exchangeRate'), // 汇率
      minWidth: 120
    },
    {
      prop: 'extStandardCurrency',
      label: () => _this.$t('bid_mod.standardCurrency'), // 本位币
      minWidth: 120
    },
    {
      prop: 'ceeaTaxRate',
      label: () => _this.$t('bidMod.taxRate'), // 税率
      minWidth: 120
    },
    {
      prop: 'ceeaAmountIncludingTax',
      // label: '含税总额',
      label: () => _this.$t('cusEntry.supplement20250121.totalAmountIncludingTax'),
      minWidth: 120
    },
    {
      prop: 'ceeaAmountExcludingTax',
      label: () => _this.$t('contractMod.excludeTaxPayAmount'), // 未税总额
      minWidth: 120
    },
    {
      prop: 'extUserName',
      // label: '使用人',
      label: () => _this.$t('cusEntry.orderMod.extUserName'),
      minWidth: 120
    },
    {
      prop: 'extUseDepartmentName',
      // label: '使用部门',
      label: () => _this.$t('cusEntry.orderMod.extUseDepartmentName'),
      minWidth: 120
    },
    {
      prop: 'closedCause',
      // label: '取消原因',
      label: () => _this.$t('cusEntry.bidSuperviseReport.cancelReason'),
      width: 120
    },
    {
      prop: 'comments',
      // label: '订单备注',
      label: () => _this.$t('cusEntry.supplement20250121.orderRemarks'),
      minWidth: 120
    },
    {
      prop: 'extOrderRemark',
      // label: '供应商备注',
      label: () => _this.$t('contractMod.vendorRemark'),
      minWidth: 120
    },
    {
      prop: 'extAttachId',
      // label: '订单附件',
      label: () => _this.$t('cusEntry.supplement20250121.orderAttachment'),
      showType: 'slot',
      slot: 'extAttachId',
      minWidth: 200
    },
    {
      prop: 'extVendorContacts',
      // label: '供应商联系人',
      label: () => _this.$t('vendorMod.vendorContact'),
      minWidth: 150,
      formattor: (val, row) => {
        return row.orderId.extVendorContacts || '--'
      }
    },
    {
      prop: 'extVendorPhone',
      // label: '供应商联系人电话',
      label: () => _this.$t('cusEntry.orderMod.extVendorPhone'),
      minWidth: 150,
      formattor: (val, row) => {
        return row.orderId.extVendorPhone || '--'
      }
    },
    {
      prop: 'extDeliveryCycle',
      // label: '交货周期',
      label: () => _this.$t('cusEntry.sup.deliveryCycle'),
      minWidth: 150
    },
    {
      prop: 'extBuyType',
      // label: '采购类型',
      label: () => _this.$t('bid_mod.formulaValue'),
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
      width: 120,
      dataType: 'dateTime'
    }
  ]
}

export {
  formItems, // 采购订单详情 - 单据信息
  formItems1, // 采购订单详情 - 预付信息
  detailColumn, // 采购订单详情 - 明细表格
  formDetailSearchList,
  formDetailListData, // 采购订单明细表格
  reportHeader // 采购订单详情 - 采购订单变更记录
}
