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
        prop="orgInvName"
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
        label="是否无料号寻源"
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
        prop="demandQuantity"
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
        prop="currency"
        :label="$t('bidMod.currency_price')"
        width="120"
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
      >
        <template>
          <dict-select
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
        prop="notaxPrice"
        :label="$t('bidMod.quotenotaxPrice2')"
        width="120"
      >
        <template v-slot="scope">
          <!--阶梯报价 || 公式报价 || 模板报价 不能输入-->
          <el-input
            v-if="refreshInput || scope.row.isLadder === 'Y' || scope.row.isFormula === 'Y' || scope.row.isTemplate === 'Y'"
            :value="scope.row.notaxPrice"
            :disabled="!refreshInput"
            class="price-input"
          />
          <el-input
            v-else
            v-model="scope.row.notaxPrice"
            v-input-format="{ type: 'float', digits: pricePrecision, negative: false }"
            :disabled="readonly"
            class="price-input"
            @input="noTaxPriceChange(scope)"
          />
        </template>
      </el-table-column>

      <!--t 含税单价-->
      <el-table-column
        align="center"
        prop="taxPrice"
        :label="$t('bidMod.quotetaxPrice2')"
        width="100"
      />

      <!--t 付款条款-->
      <el-table-column
        align="center"
        :label="$t('paymentType.paymentType')"
        width="100"
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
        prop="fixedPriceBegin"
        :label="$t('bidMod.priceBeginDate')"
        width="140"
        :formatter="(row, scope, cellValue) => parseTime(cellValue)"
      />

      <!--t 定价结束日期-->
      <el-table-column
        align="center"
        prop="fixedPriceEnd"
        :label="$t('bidMod.priceOverDate')"
        width="140"
        :formatter="(row, scope, cellValue) => parseTime(cellValue)"
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
            {{ readonly ? '公式报价明细' : $t('bidMod.formulaQuote') }}
          </el-button>

          <!--阶梯价-->
          <el-button
            v-if="row.isLadder === 'Y'"
            type="text"
            @click="ladderPriceClick($index, row)"
          >
            {{ readonly ? '阶梯报价明细' : $t('bidMod.ladderPrice') }}
          </el-button>

          <!--模板报价-->
          <el-button
            v-if="row.isTemplate === 'Y'"
            type="text"
            @click="openTemplatePriceDialog($index, row)"
          >
            {{ readonly ? $t('templatePrice.detailLabel') : $t('templatePrice.label') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--d 阶梯价-->
    <LadderPrice
      v-if="ladderPriceVisible"
      :visible.sync="ladderPriceVisible"
      business-type="INQUIRY"
      page-type="quote"
      :edit-row="editRow"
      :readonly="readonly"
      :tax="tax"
      :price-precision="pricePrecision"
      @save-quote="saveLadderItems"
    />

    <!--d 公式报价-->
    <FormulaInFormation
      v-if="formulaInFormationVisible"
      :visible.sync="formulaInFormationVisible"
      :edit-row="editRow"
      :is-read-only="readonly"
      :item-list-form="itemListForm"
      :price-precision="pricePrecision"
      :proxy-quote-params="proxyQuoteParams"
      @saveFormulaInFormation="saveFormulaInFormation"
    />

    <!--付款条款-->
    <PaymentTypeDialog
      v-if="paymentTypeDialogVisible"
      :visible.sync="paymentTypeDialogVisible"
      business-type="INQUIRY"
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
      :business-type="BUSINESS_TYPE_ENUM.INQUIRY"
      :query-params="queryParams"
      :save-params="saveParams"
      :readonly="readonly"
      @confirm="saveTemplatePriceItem"
    >
      <QuoteRowDetail slot="header" :detail-data="editRow" />
    </TemplatePriceDialog>
  </div>
</template>

<script>
/**
 * 物料信息
 */
import { parseTimeYMD, bigCalcTaxPrice } from 'lib@/composition/origin/composition'
import { FLOAT_FORMAT_MAGIC } from '@/config/sysConfig'
import { QUOTE_TYPE_MAGIC } from '@/library/composition/inquiryBySimple/utils'
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import FormulaInFormation from './itemInfo/formulaInFormation.vue'
import TechnicalDocumentsDialog from 'lib@/composition/origin/technicalDocumentsDialog'
import PaymentTypeDialog from 'lib@/composition/origin/paymentTypeDialog'
import LadderPrice from 'lib@/composition/origin/ladderPrice'
import TemplatePriceDialog from 'lib@/composition/quoteTemplate/templatePriceDialog'
import QuoteRowDetail from './itemInfo/quoteRowDetail.vue'

export default {
  name: 'ItemInfo',

  components: {
    FormulaInFormation,
    LadderPrice,
    PaymentTypeDialog,
    TechnicalDocumentsDialog,
    TemplatePriceDialog,
    QuoteRowDetail
  },

  props: {
    itemList: {
      type: Array,
      required: true
    },
    header: {
      type: Object,
      required: true
    },
    currencyList: {
      type: Array,
      required: true
    },
    readonly: {
      type: Boolean,
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
      formulaInFormationVisible: false,
      ladderPriceVisible: false,
      ladderPriceTable: [],
      itemListData: [],
      itemListForm: {
        taxKey: '',
        currency: ''
      },
      paymentTermVisible: false,
      editRow: null,
      queryParams: null,
      saveParams: null,
      editIndex: '',
      paymentTypeDialogVisible: false,
      tax: '',
      technicalDocumentsDialogVisible: false,
      templatePriceDialogVisible: false,
      BUSINESS_TYPE_ENUM,
      refreshInput: false
    }
  },

  computed: {
    // 是否公式报价
    isFormula () {
      return this.header.quoteType === QUOTE_TYPE_MAGIC.FORMULA
    },
    // 模板报价
    isTemplatePrice () {
      return this.header.quoteType === QUOTE_TYPE_MAGIC.TEMPLATE
    },
    // 价格精度 没选币种默认系统精度
    pricePrecision () {
      const currency = this.currencyList.find(item => item.currencyCode === this.itemListForm.currency)
      return currency ? currency.pricePrecision : FLOAT_FORMAT_MAGIC.DIGITS
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
            const validObj = this.itemListData.find(item => item.currency && item.taxKey)
            if (validObj) {
              this.itemListForm = {
                taxKey: validObj.taxKey,
                currency: validObj.currency
              }
            }
          }
        }
      },
      deep: true,
      immediate: true
    },
    pricePrecision () {
      if (this.header.quoteType === QUOTE_TYPE_MAGIC.NORMAL) {
        // 价格精度改变，刷新一下单价输入框
        this.refreshInput = true
        this.$nextTick(() => {
          this.refreshInput = false
        })
      }
    }
  },

  methods: {
    parseTime (val) {
      return parseTimeYMD(val)
    },

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
      if (this.tax && row.isLadder !== 'Y' && !this.readonly) {
        row.taxPrice = bigCalcTaxPrice(row.notaxPrice, this.tax, this.pricePrecision)
      }
      this.itemListData.splice($index, 1, row)
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
        this.$message.warning('请先选择币种！')
        return
      }
      if (!this.itemListForm.taxKey) {
        // 请先选择税率！
        this.$message.warning('请先选择税率！')
        return
      }
      this.editRow = row
      this.editIndex = index
      this.ladderPriceVisible = true
    },

    /* 公式报价 */
    handleFormulaQuote (index, row) {
      if (!this.itemListForm.currency) {
        // 必须先选币种才知道价格精度
        this.$message.warning('请先选择币种！')
        return
      }
      if (!this.itemListForm.taxKey) {
        // 请先选择税率！
        this.$message.warning('请先选择税率！')
        return
      }
      this.editRow = row
      this.editIndex = index
      this.formulaInFormationVisible = true
    },

    /* 保存阶梯价 */
    saveLadderItems (val) {
      this.itemListData[this.editIndex].ladderPriceTable = val
      this.itemListData[this.editIndex].notaxPrice = val[0].price
      this.itemListData[this.editIndex].taxPrice = val[0].taxPrice
    },

    /* 保存公式报价 */
    saveFormulaInFormation (val) {
      this.itemListData[this.editIndex].formulaAttrValues = val
      let params = {
        inquiryItemId: this.itemListData[this.editIndex].inquiryItemId,
        currency: this.itemListForm.currency,
        taxKey: this.itemListForm.taxKey,
        formulaValue: val
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
        if (data && data.data) {
          this.itemListData[this.editIndex].notaxPrice = data.data.notaxPrice
          this.itemListData[this.editIndex].taxPrice = data.data.taxPrice
        }
      })
    },

    /* 查看付款方式 */
    openPaymentTypeDialog (index, row) {
      this.editIndex = index

      if (row.quoteItemId && row.inquiryItemId) {
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
        businessId: row.inquiryItemId || ''
      }
      this.technicalDocumentsDialogVisible = true
    },

    /* 模板报价 START */
    // 打开
    openTemplatePriceDialog ($index, row) {
      this.editIndex = $index
      // 用于详情展示
      this.editRow = {
        ...row,
        ...this.itemListForm
      }

      let restParams = {}
      if (this.proxyQuoteParams.visible) {
        // 代理报价
        restParams = {
          vendorId: this.proxyQuoteParams.vendorId
        }
      }

      this.queryParams = {
        ...restParams,
        inquiryId: row.inquiryId,
        inquiryItemId: row.inquiryItemId
      }
      if (!this.readonly) {
        this.saveParams = {
          ...restParams,
          inquiryItemId: row.inquiryItemId
        }
      }

      this.templatePriceDialogVisible = true
    },
    // 保存
    saveTemplatePriceItem (data) {
      this.itemListData[this.editIndex].notaxPrice = data

      // 计算含税单价
      this.noTaxPriceChange({
        row: this.itemListData[this.editIndex],
        $index: this.editIndex
      })
    },
    /* 模板报价 END */

    /* 校验并返回表单值 */
    validateForm (isValidate) {
      return new Promise(resolve => {
        // 编排需要提交的数据
        const quoteItemList = (this.itemListData || []).map(item => {
          return {
            // 询价行ID
            inquiryItemId: item.inquiryItemId,
            // 供应商选择的币种
            currency: this.itemListForm.currency,
            // 税率编码
            taxKey: this.itemListForm.taxKey,
            // 供应商的未税报价
            notaxPrice: item.notaxPrice,
            // 付款账期
            paymentList: item.paymentList || [],
            // 公式报价时的参数,例如:"{\"311693985994880\":\"1\",\"311694062098560\":\"1\",\"3116...
            formulaAttrValues: item.formulaAttrValues || '',
            // 阶梯价信息
            ladderPriceList: (item.ladderPriceTable || []).map(itemLadderPrice => {
              return {
                inquiryLadderPriceId: itemLadderPrice.inquiryLadderPriceId,
                price: itemLadderPrice.price
              }
            })
          }
        })

        let resolveStatus = true
        if (isValidate) {
          // 需要校验数据
          const requiredKeys = [
            { key: 'currency', message: '物料信息第$index行缺少币种' },
            { key: 'taxKey', message: '物料信息第$index行缺少税率' }
          ]
          // 遍历查找需要校验必填的字段
          for (const [index, item] of new Map(quoteItemList.map((itemM, indexM) => [indexM, itemM]))) {
            if (this.header.allowPartBiding === 'Y' && !item.notaxPrice) {
              // 部分报价，未填未税报价，不校验
              break
            }
            const errorItem = requiredKeys.find(keyItem => !item[keyItem.key])
            if (errorItem) {
              // 替换提示行字符
              this.$message.warning(errorItem.message.replace('$index', index + 1))
              resolveStatus = false
              break
            }
            const isLadder = this.itemListData[index].isLadder === 'Y'
            const isFormula = this.itemListData[index].isFormula === 'Y'
            // 校验未税报价
            if (!isLadder && !isFormula && !this.readonly && !item.notaxPrice) {
              // 不是阶梯报价也不是公式报价
              this.$message.warning(`物料信息第${index + 1}行缺少未税报价`)
              resolveStatus = false
              break
            }
            // 校验公式报价
            if (isFormula && !item.formulaAttrValues) {
              this.$message.warning(`物料信息第${index + 1}行缺少公式报价`)
              resolveStatus = false
              break
            }
            // 校验阶梯报价
            if (isLadder && item.ladderPriceList.length === 0) {
              this.$message.warning(`物料信息第${index + 1}行缺少阶梯报价`)
              resolveStatus = false
              break
            }
          }
        }
        resolve(resolveStatus ? quoteItemList : false)
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.price-input-number {
  max-width: 100%;
  :deep(.el-input) {
    line-height: 1;
  }
}
</style>
