<template>
  <div class="item-info">
    <el-table
      ref="itemInfoTable"
      :data="itemListData"
      style="width: 100%"
      border
      height="222px"
    >
      <el-table-column
        align="center"
        type="index"
        width="50"
      />

      <!--t 业务实体-->
      <el-table-column
        align="center"
        prop="orgOuName"
        :label="$t('bid_mod.businessEntity')"
        width="150"
      />

      <!--t 库存组织-->
      <el-table-column
        align="center"
        prop="orgInvName"
        :label="$t('bid_mod.inv')"
        width="150"
      />

      <!--t 是否无料号寻源-->
      <el-table-column
        align="center"
        prop="noCodeItem"
        :label="$t('bidMod.biddingManagementBuyer.isNoCodeItem')"
        width="120"
        :formatter="(row, column, cellValue) => $getDictLabel('YES_OR_NO', cellValue)"
        show-overflow-tooltip
      />

      <!--t 物料编码-->
      <el-table-column
        align="center"
        prop="itemCode"
        :label="$t('bidMod.itemCode')"
        width="120"
        :formatter="(row, column, cellValue) => row.noCodeItem === 'Y' ? '' : cellValue"
        show-overflow-tooltip
      />

      <!--t 物料名称-->
      <el-table-column
        align="center"
        prop="itemDesc"
        :label="$t('bidMod.itemName')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--t 物料分类-->
      <el-table-column
        align="center"
        prop="categoryName"
        :label="$t('bidMod.categoryName')"
        width="150"
        show-overflow-tooltip
      />

      <!--t 组合-->
      <el-table-column
        align="center"
        prop="itemGroup"
        :label="$t('bidMod.itemGroup')"
        width="150"
        show-overflow-tooltip
      />

      <!--t 行类型-->
      <el-table-column
        align="center"
        prop="itemType"
        :label="$t('bidMod.itemType')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('DMAND_LINE_TYPE', cellValue)"
      />

      <!--t 预计数量-->
      <el-table-column
        align="center"
        prop="requireQuantity"
        :label="$t('bidMod.demandQuantity')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 单位-->
      <el-table-column
        align="center"
        prop="unit"
        :label="$t('bidMod.unit')"
        width="60"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('unit', cellValue)"
      />

      <!--t 公式值-->
      <el-table-column
        v-if="isFormula"
        align="center"
        :label="$t('bid_mod.formulaValue')"
        prop="formulaValue"
        width="110"
        show-overflow-tooltip
      />

      <!--t 币种-->
      <el-table-column
        align="center"
        prop="orderCurrency"
        :label="$t('bidMod.currency_price')"
        width="120"
        :render-header="_addStarToColumn"
      >
        <template>
          <DictSelect
            v-model="itemListForm.currency"
            code="currency"
            :disabled="readonly"
            :transform-options="allowCurrencyList"
            @change="currencyAndTaxChange"
          />
        </template>
      </el-table-column>

      <!--t 税率-->
      <el-table-column
        align="center"
        prop="taxKey"
        :label="$t('bidMod.taxRate2')"
        width="120"
        :render-header="_addStarToColumn"
      >
        <template>
          <DictSelect
            v-model="itemListForm.taxKey"
            code="tax"
            :disabled="readonly"
            @change-value="taxKeySelectChange"
          />
        </template>
      </el-table-column>

      <!--t 未税单价-->
      <el-table-column
        align="center"
        prop="orderNotaxPrice"
        :label="$t('bidMod.quotenotaxPrice2')"
        width="100"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <!--阶梯报价 || 公式报价 || 料费分离 不能输入-->
          <el-input
            v-model="scope.row.orderNotaxPrice"
            v-input-format="{ type: 'float', negative: false, zero: false }"
            :disabled="scope.row.isLadder === 'Y' || scope.row.isFormula === 'Y' || isSeparation || readonly"
            class="price-input"
            @change="noTaxPriceChange(scope)"
          />
        </template>
      </el-table-column>

      <!--t 含税单价-->
      <el-table-column
        align="center"
        prop="orderTaxPrice"
        :label="$t('bidMod.quotetaxPrice2')"
        width="100"
      />

      <!--t 付款条款-->
      <el-table-column
        align="center"
        :label="$t('paymentType.paymentType')"
        width="100"
        :render-header="_addStarToColumn"
      >
        <template v-slot="{ $index, row }">
          <el-button type="text" @click="openPaymentTypeDialog($index, row)">
            {{ readonly ? $t('common.view') : $t('bidMod.input') }}
          </el-button>
        </template>
      </el-table-column>

      <!--t 是否阶梯报价-->
      <el-table-column
        align="center"
        prop="isLadder"
        :label="$t('bidMod.isLadder')"
        width="100"
        :formatter="(row, column, cellValue) => $getDictLabel('YES_OR_NO', cellValue)"
      />

      <!--t 定价开始日期-->
      <el-table-column
        align="center"
        prop="priceStartTime"
        :label="$t('bidMod.priceBeginDate')"
        width="140"
        :formatter="(row, scope, cellValue) => $parseTime(cellValue)"
      />

      <!--t 定价结束日期-->
      <el-table-column
        align="center"
        prop="priceEndTime"
        :label="$t('bidMod.priceOverDate')"
        width="140"
        :formatter="(row, scope, cellValue) => $parseTime(cellValue)"
      />

      <!--t 技术文件-->
      <el-table-column
        align="center"
        prop="graphFileId"
        :label="$t('bidMod.technicalDocuments.title')"
        width="120"
      >
        <template v-slot="{ row }">
          <el-button type="text" @click="openTechnicalDocumentsDialog(row)">
            {{ $t('common.view') }}
          </el-button>
        </template>
      </el-table-column>

      <!--t 备注-->
      <el-table-column
        align="center"
        prop="remark"
        :label="$t('common.remark')"
        width="160"
        show-overflow-tooltip
      />

      <!--t 操作-->
      <el-table-column
        align="center"
        fixed="right"
        :label="$t('common.operation')"
        width="150"
      >
        <template v-slot="{ row, $index }">
          <!--公式报价-->
          <el-button
            v-if="row.isFormula === 'Y'"
            type="text"
            @click="handleFormulaQuote($index, row)"
          >
            {{ readonly ? $t('bidMod.formulaQuoteDetail') : $t('bidMod.formulaQuote') }}
          </el-button>

          <!--阶梯价-->
          <el-button
            v-if="row.isLadder === 'Y'"
            type="text"
            @click="ladderPriceClick($index, row)"
          >
            {{ readonly ? $t('bidMod.ladderQuoteDetail') : $t('bidMod.ladderPrice') }}
          </el-button>

          <!--模板报价-->
          <el-button
            v-if="isTemplatePrice"
            type="text"
            @click="openTemplatePriceDialog($index, row)"
          >
            {{ readonly ? $t('bidMod.templateQuoteDetail') : $t('templatePrice.label') }}
          </el-button>

          <!-- 料费分离报价 -->
          <el-button
            v-if="isSeparation"
            type="text"
            @click="openSeparationPriceDialog($index, row)"
          >
            {{ readonly ? $t('bidMod.quoteDetails') : $t('bidMod.doBiding1') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--d 阶梯价-->
    <LadderPrice
      v-if="ladderPriceVisible"
      :visible.sync="ladderPriceVisible"
      :business-type="BUSINESS_TYPE_ENUM.INQUIRY_LTS"
      page-type="quote"
      :edit-row="editRow"
      :readonly="readonly"
      :tax="tax"
      :price-precision="pricePrecision"
      @save-quote="saveLadderItems"
    />

    <!--付款条款-->
    <PaymentTypeDialog
      v-if="paymentTypeDialogVisible"
      :visible.sync="paymentTypeDialogVisible"
      :business-type="BUSINESS_TYPE_ENUM.INQUIRY_LTS"
      :edit-row="editRow"
      :readonly="readonly"
      @savePaymentType="savePaymentType"
    />

    <!--技术文件-->
    <TechnicalDocumentsDialog
      v-if="technicalDocumentsDialogVisible"
      :visible.sync="technicalDocumentsDialogVisible"
      readonly
      :params="editRow"
    />

    <!--模板报价-->
    <TemplatePriceDialog
      v-if="templatePriceDialogVisible"
      :visible.sync="templatePriceDialogVisible"
      :edit-row="editRow"
      :readonly="readonly"
      @confirm="saveTemplatePriceItem"
    />

    <!--公式报价-->
    <FormulaPrice
      v-if="formulaPriceDialogVisible"
      :business-type="BUSINESS_TYPE_ENUM.INQUIRY_LTS"
      :visible.sync="formulaPriceDialogVisible"
      :detail-info="editRow"
      :query-params="formulaPriceQueryParams"
      :proxy-quote-params="proxyQuoteParams"
      :price-precision="pricePrecision"
      :readonly="readonly"
      @save="saveFormulaInFormation"
    />

    <!-- 料费分离报价 -->
    <SeparationPriceDialog
      v-if="separationPriceDialogVisible"
      :visible.sync="separationPriceDialogVisible"
      :businessType="BUSINESS_TYPE_ENUM.INQUIRY_LTS"
      :edit-row="editRow"
      :readonly="readonly"
      :role="proxyQuoteParams.visible ? 'buyer' : 'vendor'"
      @confirm="saveTemplatePriceItem"
    />
  </div>
</template>

<script>
/**
 * 物料信息
 */
import { bigCalcTaxPrice } from 'lib@/composition/origin/composition'
import { BUSINESS_TYPE_ENUM, SOU_ORDER_TYPE_ENUM } from 'lib@/composition/origin/enum'
import { bigPriceRound } from 'lib@/composition/commonComposition'
import TechnicalDocumentsDialog from 'lib@/composition/origin/technicalDocumentsDialog'
import PaymentTypeDialog from 'lib@/composition/origin/paymentTypeDialog'
import LadderPrice from 'lib@/composition/origin/ladderPrice'
import TemplatePriceDialog from 'lib@/composition/inquiry/templatePriceDialog'
import FormulaPrice from 'lib@/composition/origin/formulaPrice'
import SeparationPriceDialog from 'lib@/composition/quoteSeparation/templatePriceDialog'

export default {
  name: 'ItemInfo',

  components: {
    LadderPrice,
    PaymentTypeDialog,
    TechnicalDocumentsDialog,
    TemplatePriceDialog,
    FormulaPrice,
    SeparationPriceDialog
  },

  props: {
    itemList: Array,
    header: Object,
    currencyList: Array,
    readonly: Boolean,
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
      formulaPriceDialogVisible: false,
      ladderPriceVisible: false,
      ladderPriceIndex: '',
      itemListData: [],
      itemListForm: {
        taxKey: '',
        currency: ''
      },
      paymentTermVisible: false,
      editRow: null,
      editIndex: '',
      paymentTypeDialogVisible: false,
      tax: '',
      technicalDocumentsDialogVisible: false,
      templatePriceDialogVisible: false,
      separationPriceDialogVisible: false,
      BUSINESS_TYPE_ENUM,
      formulaPriceQueryParams: null,
      quoteData: new Map(),
      quoteTotalPrice: new Map()
    }
  },

  computed: {
    // 是否公式报价
    isFormula () {
      return this.header.orderType === SOU_ORDER_TYPE_ENUM.FORMULA
    },
    // 模板报价
    isTemplatePrice () {
      return this.header.orderType === SOU_ORDER_TYPE_ENUM.TEMPLATE
    },
    // 料费分离
    isSeparation () {
      return this.header.orderType === SOU_ORDER_TYPE_ENUM.MATERIAL_COST_SEPARATION
    },
    // 价格精度
    pricePrecision () {
      const currency = this.currencyList.find(item => item.currencyCode === this.itemListForm.currency)
      return currency ? currency.pricePrecision : 6
    }
  },

  watch: {
    itemList: {
      handler (newValue) {
        if (newValue) {
          // 深拷贝
          this.itemListData = JSON.parse(JSON.stringify(newValue))
          this.$nextTick(() => {
            if (this.$refs.itemInfoTable) this.$refs.itemInfoTable.doLayout()
          })
          if (this.itemListData.length > 0) {
            // 币种，税率每一行都一样
            const validObj = this.itemListData.find(item => item.orderCurrency && item.taxKey)
            if (validObj) {
              this.itemListForm = {
                taxKey: validObj.taxKey,
                currency: validObj.orderCurrency
              }
              this.tax = validObj.taxRate
            }
          }
        }
      },
      deep: true,
      immediate: true
    }
  },

  methods: {
    /* 自定义编排options */
    taxTransformOptions (options) {
      return options.map(item => {
        return {
          ...item,
          // 用key覆盖value
          value: item.key
        }
      })
    },

    /* 过滤存在币种列表的可用货币 */
    allowCurrencyList (options) {
      return options.filter(item => {
        return this.currencyList.find(itemC => itemC.currencyCode === item.value)
      })
    },

    /* 未税单价改变 */
    noTaxPriceChange ({ row, $index }) {
      // 先格式化
      if (this.pricePrecision >= 0 && (row.orderNotaxPrice || row.orderNotaxPrice === 0)) {
        row.orderNotaxPrice = bigPriceRound(row.orderNotaxPrice, this.pricePrecision)
      }
      if (this.tax && row.isLadder !== 'Y' && !this.readonly) {
        row.orderTaxPrice = bigCalcTaxPrice(row.orderNotaxPrice, this.tax, this.pricePrecision || 10)
      }
      this.$set(this.itemListData, $index, row)
      // this.itemListData.splice($index, 1, row)
    },

    /* 币种改变 / 税率改变 */
    currencyAndTaxChange () {
      // 检查所有行，格式化未税单价，计算含税单价
      this.itemListData.forEach((item, index) => {
        if (item.isLadder !== 'Y' && !this.readonly) {
          this.noTaxPriceChange({
            row: item,
            $index: index
          })
        }
      })
    },

    /* 税率改变 */
    taxKeySelectChange (_value, item) {
      this.tax = item.key
      this.currencyAndTaxChange()
    },

    /* 阶梯价 */
    ladderPriceClick (index, row) {
      if (!this.itemListForm.currency) {
        // 必须先选币种才知道价格精度
        this.$message.warning(this.$t('bidMod.inpCurrency'))
        return
      }
      if (!this.itemListForm.taxKey) {
        // 请先选择税率！
        this.$message.warning(this.$t('bidMod.inpTaxKey'))
        return
      }
      this.editRow = {
        ...row,
        ladderList: row.ladderPriceList
      }
      this.editIndex = index
      this.ladderPriceVisible = true
    },
    /* 保存阶梯价 */
    saveLadderItems (val) {
      this.itemListData[this.editIndex].ladderPriceList = [].concat(val)
      this.itemListData[this.editIndex].orderNotaxPrice = val[0].orderNotaxPrice
      this.itemListData[this.editIndex].orderTaxPrice = val[0].orderTaxPrice
    },

    /* 公式报价 START */
    // 打开
    handleFormulaQuote (index, row) {
      if (!this.itemListForm.currency) {
        // 必须先选币种才知道价格精度
        this.$message.warning(this.$t('bidMod.inpCurrency'))
        return
      }
      if (!this.itemListForm.taxKey) {
        // 请先选择税率！
        this.$message.warning(this.$t('bidMod.inpTaxKey'))
        return
      }
      this.editRow = {
        ...row,
        currency: this.itemListForm.currency,
        taxKey: this.itemListForm.taxKey
      }
      this.editIndex = index
      this.formulaPriceQueryParams = {
        souItemId: row.souItemId || '',
        orderItemId: row.orderItemId || '',
        // 币种，用于基材价格根据汇率转换
        currencyCode: this.itemListForm.currency
      }
      this.formulaPriceDialogVisible = true
    },
    // 保存
    saveFormulaInFormation (val) {
      this.itemListData[this.editIndex].formulaAttrValues = val.formulaAttrValues
      this.itemListData[this.editIndex].orderNotaxPrice = val.orderNotaxPrice
      this.itemListData[this.editIndex].orderTaxPrice = val.orderTaxPrice
    },
    /* 公式报价 END */

    /* 查看付款方式 */
    openPaymentTypeDialog (index, row) {
      this.editIndex = index

      if (row.souItemId && row.orderItemId) {
        this.editRow = { ...row }
      } else {
        this.editRow = {
          paymentList: row.paymentList && Array.isArray(row.paymentList) ? row.paymentList : []
        }
      }

      this.paymentTypeDialogVisible = true
    },

    /* 保存付款条款 */
    savePaymentType (val) {
      this.itemListData[this.editIndex].paymentList = val
    },

    /* 打开物料技术文件 */
    openTechnicalDocumentsDialog (row) {
      this.editRow = {
        businessId: row.souItemId || ''
      }
      this.technicalDocumentsDialogVisible = true
    },

    /* 模板报价 START */
    // 打开
    openTemplatePriceDialog ($index, row) {
      this.editIndex = $index
      // FIXME [ORION] 调试
      this.editRow = {
        ...row,
        tempId: 372953966041088
      }

      this.templatePriceDialogVisible = true
    },
    openSeparationPriceDialog ($index, row) {
      console.log('row:::', row)
      if (!this.itemListForm.currency) {
        // 必须先选币种才知道价格精度
        this.$message.warning(this.$t('bidMod.inpCurrency'))
        return
      }
      if (!this.itemListForm.taxKey) {
        // 请先选择税率！
        this.$message.warning(this.$t('bidMod.inpTaxKey'))
        return
      }
      this.editIndex = $index
      const { quoteTempId, quoteTempName, currentRound } = this.header
      this.editRow = {
        ...row,
        quoteTempId,
        quoteTempName,
        currentRound,
        // 默认取行id没有的话就取代理报价vendorId
        vendorId: row.vendorId ?? this.proxyQuoteParams.vendorId
      }
      console.log('vendorId', this.editRow.vendorId)
      this.separationPriceDialogVisible = true
    },
    // 保存
    saveTemplatePriceItem ({ totalPrice, templateData, souItemId }) {
      this.quoteData.set(souItemId, templateData)
      this.quoteTotalPrice.set(souItemId, totalPrice)
      this.itemListData[this.editIndex].orderNotaxPrice = totalPrice
      if (this.tax) {
        this.itemListData[this.editIndex].orderTaxPrice = bigCalcTaxPrice(totalPrice, this.tax, this.pricePrecision || 10)
      }
    },
    /* 模板报价 END */

    /* 校验并返回表单值 */
    validateForm (isValidate) {
      return new Promise(resolve => {
        // 编排需要提交的数据
        const orderItemList = (this.itemListData || []).map(item => {
          const quoteData = this.quoteData.get(item.souItemId)
          const quoteTotalPrice = this.quoteTotalPrice.get(item.souItemId)
          return {
            // 询价行ID
            souItemId: item.souItemId,
            // 供应商选择的币种
            orderCurrency: this.itemListForm.currency,
            // 税率编码
            taxKey: this.itemListForm.taxKey,
            // 供应商的未税报价
            orderNotaxPrice: this.isSeparation ? (quoteTotalPrice ?? item.orderNotaxPrice) : item.orderNotaxPrice,
            // 付款账期
            paymentList: item.paymentList || [],
            // 公式报价时的参数,例如:"{\"311693985994880\":\"1\",\"311694062098560\":\"1\",\"3116...
            formulaAttrValues: item.formulaAttrValues || '',
            // 阶梯价信息
            ladderPriceList: item.ladderPriceList || [],
            quoteData
          }
        })

        let resolveStatus = true
        if (isValidate) {
          // 需要校验
          if (!this.itemListForm.currency) {
            this.$message.warning(this.$t('bidMod.inpCurrency'))
            resolve(false)
            return
          }
          if (!this.itemListForm.taxKey) {
            this.$message.warning(this.$t('bidMod.inpTaxKey'))
            resolve(false)
            return
          }

          // 遍历查找需要校验必填的字段
          for (const [index, item] of orderItemList.entries()) {
            if (this.header.allowPartPrice === 'Y' && !item.orderNotaxPrice) {
              // 部分报价，未填未税报价，不校验
              break
            }
            // 校验付款条款
            if (item.paymentList.length === 0) {
              this.$message.warning(this.$t('bidMod.itemInfoMsg1', { index: index + 1 }))
              resolveStatus = false
              break
            }
            const isLadder = item.isLadder === 'Y'
            const isFormula = item.isFormula === 'Y'
            // 校验未税报价
            if (!isLadder && !isFormula && !this.isSeparation && !this.readonly && !item.orderNotaxPrice) {
              // 不是阶梯报价也不是公式报价
              this.$message.warning(this.$t('bidMod.itemInfoMsg2', { index: index + 1 }))
              resolveStatus = false
              break
            }
            // 校验公式报价
            if (isFormula && !item.formulaAttrValues) {
              this.$message.warning(this.$t('bidMod.itemInfoMsg3', { index: index + 1 }))
              resolveStatus = false
              break
            }
            // 校验阶梯报价
            if (isLadder && item.ladderPriceList.length === 0) {
              this.$message.warning(this.$t('bidMod.itemInfoMsg4', { index: index + 1 }))
              resolveStatus = false
              break
            }
          }
        }
        resolve(resolveStatus ? orderItemList : false)
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.price-input :deep(.el-input__inner)  {
  text-align: center;
}
.price-input-number {
  max-width: 100%;
  :deep(.el-input) {
    line-height: 1;
  }
}
</style>
