<template>
  <SrmDialog
    :visible.sync="dialogVisible"
    :title="$t('bidMod.formulaQuote')"
    size="large"
    append-to-body
    :close-on-click-modal="false"
    v-bind="$attrs"
    v-on="$listeners"
  >
    <!--头部信息插槽-->
    <slot name="header">
      <!--提供默认值展示-->
    </slot>

    <!--价格公式-->
    <p>
      <span>{{ $t('materialMainData.priceFormula') }}：</span>{{ (detailInfo || {})[keyMap.formulaResult] || '' }}
    </p>

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
          v-model.number="scope.row[item.slot]"
          :disabled="readonly"
          type="number"
          @input="priceChange(scope, item.slot)"
        />
      </template>
    </BaseTable>

    <template #footer class="dialog-footer">
      <!--b 价格计算-->
      <el-button
        v-if="!readonly"
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
      <el-button
        v-if="!readonly"
        type="primary"
        @click="saveFormulaInFormation"
      >
        {{ $t('common.confirm') }}
      </el-button>
    </template>
  </SrmDialog>
</template>

<script>
/**
 * 寻源模块 - 公式报价
 */
import { getApiByBusinessType, mappingPropByBusinessTypeAndKey } from './utils'
import { bigPriceRound } from 'lib@/composition/commonComposition'
import { validatorBusinessType } from 'lib@/composition/origin/composition'
import BaseTable from 'lib@/components/BaseTable'
export default {
  name: 'FormulaPrice',

  components: { BaseTable },

  props: {
    // 业务类型
    businessType: {
      type: String,
      // required: true,
      validator: value => validatorBusinessType(value)
    },
    visible: {
      type: Boolean
      // required: true
    },
    detailInfo: {
      type: Object
      // required: true
    },
    readonly: {
      type: Boolean,
      default: false
    },
    // 价格精度
    pricePrecision: {
      type: Number,
      default: 2
    },
    // 代理报价参数
    proxyQuoteParams: {
      type: Object,
      default: () => {
        return {
          visible: false
        }
      }
    },
    // 查询参数 调用方决定
    queryParams: {
      type: Object,
      default: () => null
    }
  },

  data () {
    return {
      slotInputList: [],
      formulaInformationTableData: [],
      formulaInformationColumns: [],
      calculationNotaxPriceLoading: false,
      dialogVisible: false
    }
  },

  computed: {
    // key map 计算一次缓存下来
    keyMap () {
      const mappingProp = key => {
        return mappingPropByBusinessTypeAndKey(this.businessType, key)
      }
      return {
        formulaResult: mappingProp('formulaResult')
      }
    }
  },
  watch: {
    visible (sign) {
      this.dialogVisible = sign
      if (this.dialogVisible && this.detailInfo) {
        this.getQuoteFormulaPrices()
      }
    }
  },

  methods: {
    /* 查询公式报价 */
    async getQuoteFormulaPrices () {
      let query = { ...this.queryParams }
      // 代理报价设置vendorId
      if (this.proxyQuoteParams.visible) {
        query.vendorId = this.proxyQuoteParams.vendorId
      }
      const response = await this.$api.utils.common(
        getApiByBusinessType(this.businessType, 'get'),
        { queryParams: query }
      )
      if (response && Array.isArray(response.data)) {
        // 编排表格columns配置
        this.initTableColumnsConfig(response.data)

        // 编排表格数据
        this.initTableData(response.data)
      }
    },

    /* 价格改变 格式化价格 */
    priceChange ({ row }, slot) {
      // 格式化输入价格
      if (this.pricePrecision >= 0 && (row[slot] || row[slot] === 0)) {
        row[slot] = bigPriceRound(row[slot], this.pricePrecision)
      }
      this.formulaInformationTableData[0][slot] = row[slot]
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
            prop: 'currency',
            formatter: (_row, _column, cellValue) => this.$getDictLabel('currency', cellValue)
          }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: '合计',
            prop: 'orderNotaxPrice'
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
          orgInvNameOrOuGroupName: this.detailInfo.orgInvName || this.detailInfo.ouGroupName,
          itemDesc: this.detailInfo.itemDesc,
          // currency: this.$getDictLabel('currency', this.detailInfo.currency),
          currency: this.detailInfo.currency,
          orderNotaxPrice: this.detailInfo.orderNotaxPrice || ''
        }
      ]
      // 查询的数据
      columnsList.forEach(item => {
        tableData[0] = {
          ...tableData[0],
          [item.essentialFactorId.toString()]: item.value || ''
        }
      })
      if (this.detailInfo.formulaAttrValues) {
        // 存在旧数据，带入，覆盖查询的
        tableData[0] = {
          ...tableData[0],
          ...JSON.parse(this.detailInfo.formulaAttrValues)
        }
      }

      this.formulaInformationTableData = tableData
    },

    /* 校验输入框的值都是必填的, 通过校验就返回数据 */
    validateInputData () {
      let validateStatus = true
      const saveData = JSON.parse(JSON.stringify(this.formulaInformationTableData[0]))
      // 清除多余的属性
      const deleteKeys = ['orgInvNameOrOuGroupName', 'itemDesc', 'currency', 'orderNotaxPrice']
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
        saveData: validateStatus ? JSON.stringify(saveData) : null
      }
    },

    /* 价格计算 */
    async calculationNotaxPrice () {
      this.calculationNotaxPriceLoading = true
      const validateResult = this.validateInputData()

      if (!validateResult.validateStatus) {
        this.calculationNotaxPriceLoading = false
        return null
      }

      let params = {
        souItemId: this.detailInfo.souItemId,
        currency: this.detailInfo.currency,
        taxKey: this.detailInfo.taxKey,
        [this.keyMap.formulaResult]: validateResult.saveData
      }
      // 代理报价需要传供应商ID
      if (this.proxyQuoteParams.visible) {
        params = {
          ...params,
          vendorId: this.proxyQuoteParams.vendorId
        }
      }

      // 计算未税单价和含税单价
      const response = await this.$api.utils.common(
        getApiByBusinessType(this.businessType, 'compute'),
        { queryParams: params }
      ).catch(() => {
        this.calculationNotaxPriceLoading = false
      })

      this.calculationNotaxPriceLoading = false

      if (response && response.data) {
        this.$set(
          this.formulaInformationTableData[0],
          'orderNotaxPrice',
          response.data.orderNotaxPrice || 0
        )

        return {
          formulaAttrValues: validateResult.saveData,
          orderNotaxPrice: response.data.orderNotaxPrice,
          orderTaxPrice: response.data.orderTaxPrice
        }
      } else {
        return null
      }
    },

    /* 保存提交 */
    async saveFormulaInFormation () {
      const computeResult = await this.calculationNotaxPrice()

      if (computeResult) {
        this.$emit('save', computeResult)
        this.dialogVisible = false
      }
    }
  }
}
</script>
