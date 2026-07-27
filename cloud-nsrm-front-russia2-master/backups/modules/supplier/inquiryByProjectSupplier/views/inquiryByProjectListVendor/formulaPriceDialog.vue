<template>
  <srm-dialog
    :visible.sync="dialogVisible"
    :title="$t('bidMod.formulaQuote')"
    size="large"
    append-to-body
    :close-on-click-modal="false"
  >
    <div class="formulaValDiv">
      <!--价格公式-->
      <p><span>{{ $t('materialMainData.priceFormula') }}：</span>{{ (editRow || {}).formulaValue || '' }}</p>
    </div>

    <!--公式表格-->
    <BaseTable
      stripe
      :data="formulaInformationTableData"
      :columns="formulaInformationColumns"
      columns-name="formulaInformationColumns"
      :empty-text="$t('components.noData')"
      border
    >
      <template
        v-for="item in slotCellList"
        #[item.slot]="scope"
      >
        <!--基材价格，isNewPrice代表已更新，显示红色字样-->
        <span
          v-if="item.type === 'BASE_MATERIAL_PRICE'"
          :key="item.slot"
          :class="{ 'price-update': item.isNewPrice }"
        >
          {{ scope.row[item.slot] }}
        </span>
        <!--报价输入框-->
        <el-input
          v-else
          :key="item.slot"
          v-model.number="scope.row[item.slot]"
          :disabled="isReadOnly"
          type="number"
        />
      </template>
    </BaseTable>

    <template
      #footer
      class="dialog-footer"
    >
      <!--b 价格计算-->
      <el-button
        v-if="!isReadOnly"
        type="primary"
        @click="calculationNotaxPrice"
      >
        价格计算
      </el-button>

      <!--取消-->
      <el-button @click="dialogVisible = false">
        {{ $t('common.cancel') }}
      </el-button>

      <!--b 确定-->
      <el-button
        v-if="!isReadOnly"
        type="primary"
        @click="saveFormulaPrice"
      >
        {{ $t('common.confirm') }}
      </el-button>
    </template>
  </srm-dialog>
</template>

<script>
/**
 * 公式报价
 */
import BaseTable from '@/library/components/BaseTable'

export default {
  name: 'FormulaPriceDialog',

  components: { BaseTable },

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    editRow: {
      type: Object,
      required: true
    },
    proxyQuoteParams: {
      type: Object,
      required: false,
      default: () => {}
    },
    isReadOnly: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      slotCellList: [],
      formulaInformationTableData: [],
      formulaInformationColumns: []
    }
  },

  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    }
  },

  mounted () {
    if (this.dialogVisible && this.editRow) {
      this.getQuoteFormulaPrices()
    }
  },

  methods: {
    /* 查询公式报价 */
    async getQuoteFormulaPrices () {
      const { data } = await this.$api.brg.getQuoteFormulaPrices({
        requirementLineId: this.editRow.requirementLineId,
        orderLineId: this.editRow.orderLineId,
        // 币种，用于基材价格根据汇率转换
        currencyCode: this.editRow.currencyType
      })
      // 编排表格columns配置
      this.initTableColumnsConfig(data)

      // 编排表格数据
      this.initTableData(data)
    },

    /* 编排公式报价表格columns配置返回 */
    initTableColumnsConfig (columnsList) {
      // 固定列，row带上来的数据
      const columns = [
        // 业务实体
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: this.$t('bid_mod.businessEntity'),
            prop: 'orgOuName',
            showOverflowTooltip: true
          }
        },
        // 物料编号
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: this.$t('common.materialCode'),
            prop: 'itemCode',
            showOverflowTooltip: true
          }
        },
        // 物料名称
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: this.$t('bidMod.itemName'),
            prop: 'itemDesc',
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: '合计',
            prop: 'notaxPrice'
          }
        }
      ]

      const [
        priceColChildren,
        mainColChildren,
        materialColChildren,
        slotPriceColList,
        priceLibraryChildren
      ] = [[], [], [], [], []]
      columnsList.forEach(item => {
        const attrs = {
          align: 'center',
          minWidth: '100',
          label: item.essentialFactorName,
          prop: item.essentialFactorId.toString()
        }
        switch (item.essentialFactorFrom) {
          case 'SUPPLIER_QUOTED_PRICE':
            // 价格输入框
            priceColChildren.push({ attrs, slot: item.essentialFactorId.toString() })
            // 编排输入框slot输入框
            slotPriceColList.push({
              slot: item.essentialFactorId.toString(),
              key: item.essentialFactorId.toString(),
              label: item.essentialFactorName,
              type: 'SUPPLIER_QUOTED_PRICE'
            })
            break
          case 'MATERIAL_MAIN_DATA':
            // 物料属性
            mainColChildren.push({ attrs })
            break
          case 'BASE_MATERIAL_PRICE':
            // 基材价格
            materialColChildren.push({ attrs, slot: item.essentialFactorId.toString() })
            // 编排基材价格slot
            slotPriceColList.push({
              slot: item.essentialFactorId.toString(),
              key: item.essentialFactorId.toString(),
              label: item.essentialFactorName,
              type: 'BASE_MATERIAL_PRICE',
              // 标记该列基材是否已更新
              isNewPrice: item.isNewPrice === 'Y'
            })
            break
          case 'PRICE_LIBRARY':
            // 价格目录
            priceLibraryChildren.push({ attrs })
            break
          default:
        }
      })
      // 往下按顺序push 供应商报价列 => 物料属性 => 基材价格
      // 供应商报价列
      if (priceColChildren.length > 0) {
        columns.push({
          attrs: {
            align: 'center',
            // 约定col宽度100
            minWidth: (priceColChildren.length * 100).toString(),
            label: this.$t('bidMod.biddingManagementSupplier.supplierPrice')
          },
          children: priceColChildren
        })
      }
      // 物料属性
      if (mainColChildren.length > 0) {
        columns.push({
          attrs: {
            align: 'center',
            // 约定col宽度100
            minWidth: (mainColChildren.length * 100).toString(),
            label: this.$t('bidMod.biddingManagementSupplier.preserve')
          },
          children: mainColChildren
        })
      }
      // 基材价格
      if (materialColChildren.length > 0) {
        columns.push({
          attrs: {
            align: 'center',
            // 约定col宽度100
            minWidth: (materialColChildren.length * 100).toString(),
            label: this.$t('basicPrice.baseMaterialPrice')
          },
          children: materialColChildren
        })
      }
      // 价格目录
      if (priceLibraryChildren.length > 0) {
        columns.push({
          attrs: {
            align: 'center',
            // 约定col宽度100
            minWidth: (priceLibraryChildren.length * 100).toString(),
            label: this.$t('route.priceCatalog')
          },
          children: priceLibraryChildren
        })
      }

      this.formulaInformationColumns = columns
      this.slotCellList = slotPriceColList
    },

    /* 编排表格数据 */
    initTableData (columnsList) {
      // 编排表格数据，只有一行
      const tableData = [
        // 带入row数据
        {
          orgOuName: this.editRow.orgOuName,
          itemCode: this.editRow.targetNum,
          itemDesc: this.editRow.targetDesc,
          notaxPrice: this.editRow.notaxPrice
        }
      ]
      // 查询的数据
      columnsList.forEach(item => {
        let value
        if (item.value) {
          const numberValue = Number(item.value)
          value = isNaN(numberValue) ? item.value : numberValue
        } else {
          value = ''
        }
        tableData[0] = {
          ...tableData[0],
          [item.essentialFactorId.toString()]: value
        }
      })

      if (this.editRow.formulaResult) {
        // 存在旧数据，带入，覆盖查询的
        try {
          tableData[0] = {
            ...tableData[0],
            ...JSON.parse(this.editRow.formulaResult)
          }
        } catch (e) {
          console.log(e)
        }
      }

      this.formulaInformationTableData = tableData
    },

    /* 校验输入框的值都是必填的, 通过校验就返回数据 */
    validateInputData () {
      let validateStatus = true
      const saveData = JSON.parse(JSON.stringify(this.formulaInformationTableData[0]))
      // 清除多余的属性
      const deleteKeys = ['orgOuName', 'itemDesc', 'itemCode', 'notaxPrice']
      deleteKeys.forEach(item => {
        delete saveData[item]
      })
      for (const item of this.slotCellList) {
        if (!saveData[item.key] && saveData[item.key] !== 0 && item.type === 'SUPPLIER_QUOTED_PRICE') {
          // 值可为0
          this.$message.warning(`${item.label}${this.$t('bidMod.biddingManagementSupplier.valueRequired')}`)
          validateStatus = false
          break
        }
      }
      return {
        validateStatus,
        saveData: validateStatus ? saveData : null
      }
    },

    /* 未税价格计算 */
    calculationNotaxPrice () {
      const validateResult = this.validateInputData()

      if (validateResult.validateStatus) {
        // 计算未税单价和含税单价
        let params = {
          requirementLineId: this.editRow.requirementLineId,
          currencyType: this.editRow.currencyType,
          taxKey: this.editRow.taxKey,
          formulaResult: validateResult.saveData
        }
        if (this.proxyQuoteParams.visible) {
          // 代理报价
          params = {
            ...params,
            vendorId: this.proxyQuoteParams.vendorId
          }
        }
        this.$http({
          url: '/api-brg/supplierCooperate/orderHead/computeFormulaPrice',
          method: 'GET',
          params
        }).then(data => {
          if (data && data.data) {
            this.$set(this.formulaInformationTableData[0], 'notaxPrice', data.data.notaxPrice || 0)
          }
        })
      }
    },

    /* 保存提交 */
    saveFormulaPrice () {
      // 校验所有值必填
      const validateResult = this.validateInputData()
      if (validateResult.validateStatus) {
        this.$emit('saveFormulaPrice', JSON.stringify(validateResult.saveData))
        this.dialogVisible = false
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.price-update {
  color: red;
}
</style>
