let materialInfor = _this => {
  return [
    {
      slot: 'priceLibraryNo',
      itemAttrs: {
        label: this.$t('bidMod.priceLibraryNo'), // 价格库编号
        rules: [
          {
            required: true,
            message: this.$t('bidMod.selectPriceLibraryNo')  // '请选择价格库编号'
          }
        ]
      },
      computedUIAttrs: _ => {
        return {
          key: 'priceLibraryNo',
          disabled: _this.isReadOnly
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: _this.$t('purchaseDemand.businessEntity') // 业务实体
      },
      computedUIAttrs: _ => {
        return {
          key: 'orgName',
          disabled: true
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: _this.$t('purchaseDemand.invOrg') // 库存组织
      },
      computedUIAttrs: _ => {
        return {
          key: 'organizationName',
          disabled: true
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: _this.$t('common.vendorCode') //  供应商编码
      },
      computedUIAttrs: _ => {
        return {
          key: 'vendorCode',
          disabled: true
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: _this.$t('common.vendorName') // 供应商名称
      },
      computedUIAttrs: _ => {
        return {
          key: 'vendorName',
          disabled: true
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: _this.$t('common.materialCode') // 物料编码
      },
      computedUIAttrs: _ => {
        return {
          key: 'materialCode',
          disabled: true
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: _this.$t('common.materialName') // 物料名称
      },
      computedUIAttrs: _ => {
        return {
          key: 'materialName',
          disabled: true
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: this.$t('components.category.categoryName') // 品类名称
      },
      computedUIAttrs: _ => {
        return {
          key: 'categoryName',
          disabled: true
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: this.$t('dataConfMod.settingGuide.step3.3') // 单位
      },
      computedUIAttrs: _ => {
        return {
          key: 'unit',
          disabled: true
        }
      }
    },
    {
      tag: 'dictSelect',
      itemAttrs: {
        label: this.$t('components.stratProcess.headers.docStatusValue') // 状态
      },
      computedUIAttrs: _ => {
        return {
          key: 'status',
          code: 'CATALOG_ON_SHELVES_STATUS',
          disabled: true
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: _this.$t('orderMod.buyerOrderSynergy.createdBy') // 创建人
      },
      computedUIAttrs: _ => {
        return {
          key: 'createdUserName',
          disabled: true
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: _this.$t('orderMod.buyerOrderSynergy.creationDate') // 创建时间
      },
      computedUIAttrs: _ => {
        return {
          key: 'creationDate',
          disabled: true
        }
      }
    }
  ]
}

let priceInfor = _this => {
  return [
    {
      tag: 'input',
      itemAttrs: {
        label: this.$t('bid_mod.taxUnitPrice') // 含税单价
      },
      computedUIAttrs: _ => {
        return {
          key: 'taxPrice',
          disabled: true
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: this.$t('bid_mod.untaxedPrice') // 未税单价
      },
      computedUIAttrs: _ => {
        return {
          key: 'notaxPrice',
          disabled: true
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: this.$t('components.ocr.commodityTaxRate') // 税率
      },
      computedUIAttrs: _ => {
        return {
          key: 'taxRate',
          disabled: true
        }
      }
    },
    {
      tag: 'dictSelect',
      itemAttrs: {
        label: _this.$t('purchaseDemand.currency') // 币种
      },
      computedUIAttrs: _ => {
        return {
          key: 'currencyCode',
          code: 'currency',
          disabled: true
        }
      }
    },
    {
      tag: 'date',
      itemAttrs: {
        label: this.$t('bid_mod.defaultPriceValidFrom')  // '价格有效期自'
      },
      computedUIAttrs: _ => {
        return {
          key: 'effectiveDate',
          disabled: true
        }
      }
    },
    {
      tag: 'date',
      itemAttrs: {
        label: this.$t('dataConfMod.priceExpirationDate') // '价格有效期至'
      },
      computedUIAttrs: _ => {
        return {
          key: 'expirationDate',
          disabled: true
        }
      }
    }
  ]
}

let paramInfor = _this => {
  return [
    {
      tag: 'input',
      itemAttrs: {
        label: this.$t('dataConfMod.deliveryCycle') // 送货周期  
      },
      computedUIAttrs: _ => {
        return {
          key: 'deliveryCycle',
          maxlength: 25,
          disabled: _this.isReadOnly
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: _this.$t('dataConfMod.band') // 品牌
      },
      computedUIAttrs: _ => {
        return {
          key: 'brand',
          maxlength: 25,
          disabled: _this.isReadOnly
        }
      }
    },
    {
      slot: 'orderQuantityMinimum',
      itemAttrs: {
        label: _this.$t('dataConfMod.orderQuantityMinimum') // 最小起订量
      },
      computedUIAttrs: _ => {
        return {
          key: 'orderQuantityMinimum'
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: this.$t('common.specification') // 规格/型号
      },
      computedUIAttrs: _ => {
        return {
          key: 'specification',
          maxlength: 25,
          disabled: _this.isReadOnly
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: this.$t('common.weight') // 重量
      },
      computedUIAttrs: _ => {
        return {
          key: 'weight',
          maxlength: 25,
          disabled: _this.isReadOnly
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: this.$t('common.size') // 尺寸
      },
      computedUIAttrs: _ => {
        return {
          key: 'size',
          maxlength: 25,
          disabled: _this.isReadOnly
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: this.$t('dataConfMod.ceeaColor') // 颜色
      },
      computedUIAttrs: _ => {
        return {
          key: 'color',
          maxlength: 25,
          disabled: _this.isReadOnly
        }
      }
    }
  ]
}

export { materialInfor, priceInfor, paramInfor }
