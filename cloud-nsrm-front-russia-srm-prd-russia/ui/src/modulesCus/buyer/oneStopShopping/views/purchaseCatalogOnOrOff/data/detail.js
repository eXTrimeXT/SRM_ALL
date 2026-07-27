let materialInfor = _this => {
  return [
    {
      slot: 'priceLibraryNo',
      itemAttrs: {
        label: '协议编号', // 协议编号
        rules: [
          {
            required: true,
            message: '请选择协议编号'
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
          key: 'extOrgNameList',
          disabled: true
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: '区域'
      },
      computedUIAttrs: _ => {
        return {
          key: 'extAreaName',
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
        label: '品类名称' // 品类名称
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
        label: '基本计量单位'
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
        label: '状态' // 状态
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
      tag: 'date',
      itemAttrs: {
        label: _this.$t('orderMod.buyerOrderSynergy.creationDate') // 创建时间
      },
      computedUIAttrs: _ => {
        return {
          key: 'creationDate',
          disabled: true
        }
      }
    },
    {
      tag: 'date',
      itemAttrs: {
        label: '价格开始日期'
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
        label: '价格结束日期'
      },
      computedUIAttrs: _ => {
        return {
          key: 'expirationDate',
          disabled: true
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: '商品编码'
      },
      computedUIAttrs: _ => {
        return {
          key: 'extGoodsCode',
          disabled: true
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: '协议行号'
      },
      computedUIAttrs: _ => {
        return {
          key: 'extProtocolRowNo',
          disabled: true
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: '商品名称'
      },
      computedUIAttrs: _ => {
        return {
          key: 'extGoodsName',
          disabled: _this.isReadOnly
        }
      }
    },
    {
      tag: 'dictSelect',
      itemAttrs: {
        label: '商品货源类型'
      },
      computedUIAttrs: _ => {
        return {
          key: 'extMaterialType',
          code: 'EXT_MATERIAL_TYPE',
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
        label: '含税单价' // 含税单价
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
        label: '未税单价' // 未税单价
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
        label: '参考价'
      },
      computedUIAttrs: _ => {
        return {
          key: 'extReferencePrice',
          disabled: true
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: '税率' // 税率
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
    }
  ]
}

let paramInfor = _this => {
  return [
    {
      tag: 'input',
      itemAttrs: {
        label: '起订量'
      },
      computedUIAttrs: _ => {
        return {
          key: 'orderQuantityMinimum',
          disabled: true
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: '交货周期（自然日）'
      },
      computedUIAttrs: _ => {
        return {
          key: 'deliveryCycle',
          disabled: true
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: '质保期（自然日）'
      },
      computedUIAttrs: _ => {
        return {
          key: 'extShelfLife',
          disabled: true
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
          disabled: true
        }
      }
    },
    {
      tag: 'input',
      itemAttrs: {
        label: '规格'
      },
      computedUIAttrs: _ => {
        return {
          key: 'specification',
          disabled: true
        }
      }
    }
  ]
}

export { materialInfor, priceInfor, paramInfor }

