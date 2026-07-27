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
      <p>
        <span>{{ $t('materialMainData.priceFormula') }}：</span>{{ (editRow || {}).formulaValue || '' }}
      </p>
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
      <template v-for="item in slotInputList" #[item.slot]="scope">
        <el-input
          :key="item.slot"
          v-model="scope.row[item.slot]"
          v-input-format="{ type: 'float', digits: pricePrecision }"
          :disabled="isReadOnly"
        />
      </template>
    </BaseTable>

    <template #footer class="dialog-footer">
      <!--b 价格计算-->
      <el-button
        v-if="!isReadOnly"
        type="primary"
        :loading="calculationNotaxPriceLoading"
        @click="calculationNotaxPrice"
      >
        价格计算
      </el-button>

      <!--取消-->
      <el-button @click="dialogVisible = false">
        {{ $t('common.cancel') }}
      </el-button>

      <!--b 提交-->
      <el-button v-if="!isReadOnly" type="primary" @click="saveFormulaInFormation">
        {{ $t('common.confirm') }}
      </el-button>
    </template>
  </srm-dialog>
</template>

<script>
/**
 * 公式报价
 */
import BaseTable from 'lib@/components/BaseTable'

export default {
  name: 'FormulaInFormation',

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
    isReadOnly: {
      type: Boolean,
      default: false
    },
    itemListForm: {
      type: Object,
      required: true
    },
    pricePrecision: {
      type: Number,
      required: true
    },
    // 代理报价参数
    proxyQuoteParams: {
      type: Object,
      default: () => {
        return {
          visible: false
        }
      }
    }
  },

  data () {
    return {
      slotInputList: [],
      formulaInformationTableData: [],
      formulaInformationColumns: [],
      calculationNotaxPriceLoading: false
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

  async mounted () {
    if (this.dialogVisible && this.editRow) {
      this.getQuoteFormulaPrices()
    }
  },

  methods: {
    /* 查询公式报价 */
    getQuoteFormulaPrices () {
      this.$api.inq.inquiryBySimple.getQuoteFormulaPrices({
        inquiryItemId: this.editRow.inquiryItemId || '',
        quoteItemId: this.editRow.quoteItemId || '',
        // 币种，用于基材价格根据汇率转换
        currencyCode: this.itemListForm.currency
      }).then(data => {
        if (data && data.data && Array.isArray(data.data)) {
          // 编排表格columns配置
          this.initTableColumnsConfig(data.data)

          // 编排表格数据
          this.initTableData(data.data)
        }
      })
    },

    /* 编排公式报价表格columns配置返回 */
    initTableColumnsConfig (columnsList) {
      // 固定列，row带上来的数据
      const columns = [
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: '业务实体',
            prop: 'orgInvNameOrOuGroupName',
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: '物料名称',
            prop: 'itemDesc',
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: '报价币种',
            prop: 'currency'
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

      const [priceColChildren, mainColChildren, materialColChildren, slotPriceColList] = [[], [], [], []]
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
            priceColChildren.push({
              attrs,
              slot: item.essentialFactorId.toString()
            })
            // 编排输入框slot输入框
            slotPriceColList.push({
              slot: item.essentialFactorId.toString(),
              key: item.essentialFactorId.toString(),
              label: item.essentialFactorName
            })
            break
          case 'MATERIAL_MAIN_DATA':
            // 物料属性
            mainColChildren.push({ attrs })
            break
          case 'BASE_MATERIAL_PRICE':
            // 基材价格
            materialColChildren.push({ attrs })
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
            label: '供应商报价'
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
            label: '物料属性'
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
            label: '基材价格'
          },
          children: materialColChildren
        })
      }

      this.formulaInformationColumns = columns
      this.slotInputList = slotPriceColList
    },

    /* 编排表格数据 */
    initTableData (columnsList) {
      // 编排表格数据，只有一行
      const tableData = [
        // 带入row数据
        {
          orgInvNameOrOuGroupName: this.editRow.orgInvName || this.editRow.ouGroupName,
          itemDesc: this.editRow.itemDesc,
          currency: this.$getDictLabel('currency', this.itemListForm.currency),
          notaxPrice: this.editRow.notaxPrice || ''
        }
      ]
      // 查询的数据
      columnsList.forEach(item => {
        tableData[0] = {
          ...tableData[0],
          [item.essentialFactorId.toString()]: item.value || ''
        }
      })
      if (this.editRow.formulaAttrValues) {
        // 存在旧数据，带入，覆盖查询的
        tableData[0] = {
          ...tableData[0],
          ...JSON.parse(this.editRow.formulaAttrValues)
        }
      }

      this.formulaInformationTableData = tableData
    },

    /* 校验输入框的值都是必填的, 通过校验就返回数据 */
    validateInputData () {
      let validateStatus = true
      const saveData = JSON.parse(JSON.stringify(this.formulaInformationTableData[0]))
      // 清除多余的属性
      const deleteKeys = ['orgInvNameOrOuGroupName', 'itemDesc', 'currency', 'notaxPrice']
      deleteKeys.forEach(item => {
        delete saveData[item]
      })
      for (const item of this.slotInputList) {
        if (!saveData[item.key] && saveData[item.key] !== 0) {
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
      this.calculationNotaxPriceLoading = true
      const validateResult = this.validateInputData()

      if (validateResult.validateStatus) {
        let params = {
          inquiryItemId: this.editRow.inquiryItemId,
          currency: this.itemListForm.currency,
          taxKey: this.itemListForm.taxKey,
          formulaValue: validateResult.saveData
        }
        // 代理报价需要传供应商ID
        if (this.proxyQuoteParams.visible) {
          params = {
            ...params,
            vendorId: this.proxyQuoteParams.vendorId
          }
        }
        // 计算未税单价和含税单价
        this.$api.inq.inquiryBySimple.computeFormulaPrice(params).then(data => {
          this.calculationNotaxPriceLoading = false
          if (data && data.data) {
            this.$set(this.formulaInformationTableData[0], 'notaxPrice', data.data.notaxPrice || 0)
          }
        }).catch(() => {
          this.calculationNotaxPriceLoading = false
        })
      }
    },

    /* 保存提交 */
    saveFormulaInFormation () {
      // 校验所有值必填
      const validateResult = this.validateInputData()
      if (validateResult.validateStatus) {
        this.$emit('saveFormulaInFormation', JSON.stringify(validateResult.saveData))
        this.dialogVisible = false
      }
    }
  }
}
</script>
