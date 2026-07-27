<template>
  <SrmDialog
    :title="$t('bidMod.ladderPrice')"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
  >
    <!--展示表单，提供插槽-->
    <slot name="formRow">
      <div class="ladder-price-form-row">
        <!--物料编码-->
        <div class="form-col">
          <span class="label">供货区域:</span>
          <span class="value">{{ ladderPriceForm.area || '-' }}</span>
        </div>
        <!--物料编码-->
        <div class="form-col">
          <span class="label">{{ $t('bidMod.itemCode') }}:</span>
          <span class="value">{{ targetNumRevealFilter(ladderPriceForm.targetNum) || '-' }}</span>
        </div>
        <!--物料名称-->
        <div class="form-col">
          <span class="label">{{ $t('bidMod.itemName') }}:</span>
          <span class="value">{{ ladderPriceForm.targetDesc || '-' }}</span>
        </div>
        <!--预计数量-->
        <div class="form-col">
          <span class="label">{{ $t('bidMod.demandQuantity') }}:</span>
          <span class="value">{{ ladderPriceForm.quantity || '-' }}</span>
        </div>
      </div>
    </slot>
    <!-- <p>
      <el-button
        v-if="!isReadOnly"
        type="primary"
        @click="addRow"
      >
        {{ $t("common.new") }}
      </el-button>
    </p> -->

    <el-table
      :data="ladderPriceTable"
      style="width: 100%"
      border
      height="155px"
    >
      <el-table-column
        type="index"
        :label="$t('common.sort')"
        width="50"
      />

      <!--t 数量从（>=）-->
      <el-table-column
        prop="beginQuantity"
        :label="$t('bidMod.beginQuantity')"
        min-width="150"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <el-input
            v-model="scope.row.beginQuantity"
            v-input-format="{ type: 'float' }"
            disabled
          />
        </template>
      </el-table-column>

      <!--t 数量至（<）-->
      <el-table-column
        prop="endQuantity"
        :label="$t('bidMod.endQuantity')"
        min-width="150"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <el-input
            v-model="scope.row.endQuantity"
            v-input-format="{ type: 'float' }"
            disabled
          />
        </template>
      </el-table-column>

      <!--t 单位-->
      <el-table-column
        prop="unit"
        :label="$t('bidMod.unit')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('unit', cellValue)"
      />

      <!--报价-->
      <template>
        <!--未税单价-->
        <el-table-column
          align="right"
          prop="price"
          min-width="100"
          :label="$t('bidMod.quotenotaxPrice2')"
          show-overflow-tooltip
        >
          <template v-slot="scope">
            <el-input
              v-model="scope.row.price"
              v-input-format="{ type: 'float', digits: pricePrecision, negative: false, zero: false }"
              :disabled="isReadOnly"
              @input="priceChange(scope)"
            />
          </template>
        </el-table-column>

        <!--含税单价-->
        <el-table-column
          align="right"
          prop="taxPrice"
          min-width="100"
          :label="$t('bidMod.quotetaxPrice2')"
          show-overflow-tooltip
        />
      </template>

      <!--t 操作-->
      <!-- <el-table-column
        v-if="!isReadOnly"
        :label="$t('bidMod.operation')"
        width="100"
      >
        <template slot-scope="{ $index }">
          <el-button type="text" @click="deleteRow($index)">
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column> -->
    </el-table>

    <div
      slot="footer"
      class="dialog-footer"
    >
      <el-button @click="dialogVisible = false">
        {{ $t('common.cancel') }}
      </el-button>
      <el-button
        type="primary"
        @click="save"
      >
        {{ $t('common.confirm') }}
      </el-button>
    </div>
  </SrmDialog>
</template>

<script>
/**
 * 阶梯报价
 */
import { inqSupplierHttp } from 'mods@/inquirySupplier/api'
import {
  targetNumReveal,
  indexWarningMessage,
  bigCalcTaxPrice,
  validatorBusinessType
} from 'lib@/composition/origin/composition'
import { BUSINESS_TYPE_ENUM, USER_TYPE_ENUM } from 'lib@/composition/origin/enum'
import { mappingPropByBusinessTypeAndKey } from './utils'
import { mapGetters } from 'vuex'

export default {
  name: 'LadderPriceDetail',

  props: {
    // 业务类型
    businessType: {
      type: String,
      required: true,
      validator: value => validatorBusinessType(value)
    },
    visible: {
      type: Boolean,
      default: false
    },
    editRow: {
      type: Object,
      default: () => null
    },
    // 模式 [set设置, quote报价]
    pageType: {
      type: String,
      default: 'set'
    },
    // 是否显示行详情
    showInfo: {
      type: Boolean,
      default: true
    },
    isReadOnly: {
      type: Boolean,
      default: false
    },
    // 价格精度
    pricePrecision: {
      type: Number,
      default: 2,
      required: false
    },
    // 税率
    tax: {
      type: [String, Number],
      required: false
    }
  },

  data () {
    return {
      ladderPriceForm: {
        ladderType: 'standard'
      },
      ladderPriceTable: [],
      targetNumRevealFilter: targetNumReveal
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
    },
    // key map 计算一次缓存下来
    keyMap () {
      const mappingProp = key => {
        return mappingPropByBusinessTypeAndKey(this.businessType, key)
      }
      return {
        targetNum: mappingProp('targetNum'),
        targetDesc: mappingProp('targetDesc'),
        quantity: mappingProp('quantity'),
        price: mappingProp('price'),
        taxPrice: mappingProp('taxPrice'),
        ladderPriceTable: mappingProp('ladderPriceTable')
      }
    },
    pageFlag () {
      return {
        isSet: this.pageType === 'set',
        isQuote: this.pageType === 'quote'
      }
    },
    ...mapGetters(['userType'])
  },

  mounted () {
    this.initDetailData()
  },

  methods: {
    // 初始化数据
    initDetailData () {
      if (this.editRow) {
        // 表单覆盖
        this.ladderPriceForm = {
          targetNum: this.editRow[this.keyMap.targetNum] || '',
          targetDesc: this.editRow[this.keyMap.targetDesc] || '',
          quantity: this.editRow[this.keyMap.quantity] || '',
          ladderType: this.editRow.ladderType || 'standard',
          area: this.editRow.area
        }
        if (this.pageFlag.isSet || (this.editRow.ladderList && Array.isArray(this.editRow.ladderList))) {
          this.ladderPriceTable = (this.editRow.ladderList || []).map(item => {
            return {
              ...item,
              unit: this.editRow.unit,
              price: item[this.keyMap.price],
              taxPrice: item[this.keyMap.taxPrice]
            }
          })
        } else if (this.pageFlag.isQuote) {
          this.getQuoteLadderPrices()
        }
      }
    },

    /* 查询阶梯价报价信息 */
    async getQuoteLadderPrices () {
      let response = null
      let idKey = null

      // 简易询价
      if (this.businessType === BUSINESS_TYPE_ENUM.INQUIRY) {
        idKey = this.editRow.inquiryItemId
        response = await this.$api.inq.quote.batchGetQuoteLadderPrices(
          [
            {
              inquiryItemId: idKey || '',
              quoteItemId: this.editRow.quoteItemId || ''
            }
          ]
        )
      }

      // 简易询价[LTS]
      if (this.businessType === BUSINESS_TYPE_ENUM.INQUIRY_LTS) {
        idKey = this.editRow.souItemId
        const params = [
          {
            souItemId: idKey || '',
            orderItemId: this.editRow.orderItemId || ''
          }
        ]

        if (this.userType === USER_TYPE_ENUM.VENDOR) {
          // 供应商
          response = await inqSupplierHttp.order.batchGetOrderLadderPrices(params)
        }
      }

      // 招标
      if (this.businessType === BUSINESS_TYPE_ENUM.BIDING) {
        idKey = this.editRow.requirementLineId
        response = await this.$api.bid.quote.batchGetQuoteLadderPrices(
          [
            {
              bidingItemId: idKey || '',
              quoteItemId: this.editRow.quoteItemId || ''
            }
          ]
        )
      }

      if (idKey && response && response.data) {
        this.handleData(response.data[idKey])
      }
    },

    /* 处理查询后的数据 */
    handleData (data) {
      // 找到当前的数据
      if (!data || !Array.isArray(data)) {
        return
      }

      const tableData = data.map(item => {
        const price = item[this.keyMap.price]
        const taxPrice = item[this.keyMap.taxPrice]
        let itemObj = {
          ...item,
          price,
          taxPrice,
          unit: this.editRow.unit
        }
        // 不存在未税报价，计算
        if (price && (!taxPrice && taxPrice !== 0) && this.tax) {
          itemObj = {
            ...itemObj,
            taxPrice: bigCalcTaxPrice(price, this.tax)
          }
        }
        return itemObj
      })

      // 写入旧数据 默认序号一一对应
      const rowTableData = this.editRow[this.keyMap.ladderPriceTable]
      if (Array.isArray(rowTableData) && rowTableData.length > 0) {
        rowTableData.forEach((item, index) => {
          const price = item[this.keyMap.price]
          const taxPrice = item[this.keyMap.taxPrice]
          if (price) {
            tableData[index].price = price
          }
          if (taxPrice) {
            tableData[index].taxPrice = taxPrice
          }
        })
      }

      this.ladderPriceTable = tableData
    },

    /* 新增 */
    addRow () {
      this.ladderPriceTable.push({
        beginQuantity: this.ladderPriceTable.length ? this.ladderPriceTable[this.ladderPriceTable.length - 1].endQuantity : '',
        endQuantity: '',
        unit: this.editRow.unit
      })
    },

    /* 删除 */
    deleteRow (index) {
      this.ladderPriceTable.splice(index, 1)
    },

    /* 未税单价改变 */
    priceChange ({ row, $index }) {
      // 再计算含税价
      if (this.tax && this.pricePrecision >= 0 && !this.isReadOnly) {
        row.taxPrice = bigCalcTaxPrice(row.price, this.tax, this.pricePrecision)
      }
      this.ladderPriceTable.splice($index, 1, row)
    },

    /* 保存 */
    save () {
      if (this.pageFlag.isSet) {
        this.saveSet()
      } else if (this.pageFlag.isQuote) {
        this.saveQuote()
      }
    },

    /* 保存设置的阶梯价 */
    saveSet () {
      // 请选择阶梯价类型
      if (!this.ladderPriceForm.ladderType) {
        this.$message.warning(this.$t('bidMod.common.ladderMsg1'))
        return
      }
      // 请设置阶梯报价
      if (this.ladderPriceTable.length === 0) {
        this.$message.warning((this.$t('bidMod.common.ladderMsg2')))
        return
      }

      for (const [index, item] of this.ladderPriceTable.entries()) {
        const [beginQuantity, endQuantity] = [item.beginQuantity, item.endQuantity]

        if (beginQuantity === 0 || endQuantity === 0) {
          // 数量不为0！
          indexWarningMessage(index, this.$t('bidMod.common.ladderMsg3'))
          return
        }

        if (
          !beginQuantity ||
          // 最后一行的至允许为空
          (!endQuantity && index !== (this.ladderPriceTable.length - 1))
        ) {
          // 请输入[数量从]和[数量至]!
          indexWarningMessage(index, this.$t('bidMod.common.ladderMsg4'))
          return
        }
        // [数量从]不能大于[数量至]!
        if (beginQuantity && endQuantity && Number(beginQuantity) > Number(endQuantity)) {
          indexWarningMessage(index, this.$t('bidMod.common.ladderMsg5'))
          return
        }
        // 阶梯价不允许断层报价，接下来的一层只能从上一层的终点数量开始算!
        if (index !== 0 && beginQuantity !== this.ladderPriceTable[index - 1].endQuantity) {
          indexWarningMessage(index, this.$t('bidMod.common.ladderMsg6'))
          return
        }
      }

      // 阶梯报价未包含预计采购量区间
      const quantity = this.ladderPriceForm.quantity
      const endQuantity = this.ladderPriceTable[this.ladderPriceTable.length - 1].endQuantity
      if (
        endQuantity &&
        (
          (Number(quantity) < Number(this.ladderPriceTable[0].beginQuantity)) ||
          (Number(quantity) >= Number(endQuantity))
        )
      ) {
        // 阶梯报价未包含预计采购量区间!
        this.$message.warning(this.$t('bidMod.common.ladderMsg7'))
        return
      }

      this.ladderPriceTable = this.ladderPriceTable.map(item => {
        return {
          ...item,
          // 冗余类型
          ladderType: this.ladderType
        }
      })

      // 返回保存
      this.$emit('save-set', {
        ladderType: this.ladderPriceForm.ladderType,
        ladderPrices: this.ladderPriceTable.map(item => {
          return {
            ...item,
            // 重新反映射
            [this.keyMap.targetNum]: item.targetNum,
            [this.keyMap.targetDesc]: item.targetDesc,
            [this.keyMap.quantity]: item.quantity
          }
        })
      })
      this.dialogVisible = false
    },

    /* 保存报价 */
    saveQuote () {
      for (const [index, item] of this.ladderPriceTable.entries()) {
        if (!item.price) {
          // 请输入未税单价!
          indexWarningMessage(index, this.$t('bidMod.common.ladderMsg8'))
          return
        }
      }

      this.$emit('save-quote', this.ladderPriceTable.map(item => {
        return {
          ...item,
          // 重新反映射
          [this.keyMap.targetNum]: item.targetNum,
          [this.keyMap.targetDesc]: item.targetDesc,
          [this.keyMap.quantity]: item.quantity,
          [this.keyMap.price]: item.price,
          [this.keyMap.taxPrice]: item.taxPrice
        }
      }))
      this.dialogVisible = false
    }
  }
}
</script>

<style lang="scss" scoped>
.ladder-price-form-row {
  display: flex;
  margin-bottom: 10px;
  .form-col:not(:last-child) {
    margin-right: 30px;
  }
  .form-col {
    height: 30px;
    line-height: 30px;
    .label {
      padding-right: 15px;
    }
  }
}
</style>
