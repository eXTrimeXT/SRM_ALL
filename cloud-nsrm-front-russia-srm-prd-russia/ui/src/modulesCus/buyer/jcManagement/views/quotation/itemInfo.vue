<template>
  <div class="item-info">
    <div v-if="!isReadOnly">
      <MImport
        type="default"
        title="导入"
        up-load-url="/api-sou/npm/vendor/ext_pur_inq/order/getLastOrderItems/import"
        :extra-data="extraData"
        @downloadTemplate="downloadTemplate"
        @handleSuccess="handleSuccess"
      />
    </div>
    <span class="tip">阶梯价不允许导入</span>
    <el-table
      ref="itemInfoTable"
      :data="pageList"
      border
      max-height="405px"
    >
      <el-table-column
        align="center"
        type="index"
        :label="$t('cusEntry.inq.index')"
        width="50"
        fixed="left"
      />
      <el-table-column
        align="center"
        prop="area"
        :label="'供货范围'"
        :formatter="(row, column, val) => getAreaLabel(val ? val.split(',') : [])"
      />
      <el-table-column
        align="center"
        prop="itemDesc"
        :label="'物资名称'"
        width="120"
      />
      <el-table-column
        align="center"
        prop="itemCode"
        :label="'物资编码'"
        width="120"
      />
      <el-table-column
        align="center"
        prop="model"
        :label="'规格型号'"
        width="120"
      />
      <el-table-column
        align="center"
        prop="unit"
        :label="'计量单位'"
        width="120"
      />
      <el-table-column
        align="center"
        prop="requireQuantity"
        :label="'数量'"
        width="120"
      />
      <el-table-column
        align="center"
        prop="brand"
        :label="'品牌'"
        width="120"
      />
      <el-table-column
        align="center"
        prop="taxKey"
        label="税率"
        width="120"
        :render-header="_addStarToColumn"
      >
        <template slot-scope="scope">
          <DictSelect
            v-model="scope.row.taxKey"
            code="tax"
            :clearable="false"
            :filter-item="scope.row.invoiceType === 'SPECIAL_TICKET' ? ['VAT_IN_0'] : []"
            :disabled="isReadOnly"
            @change="value => taxKeySelectChange(value, scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="invoiceType"
        :label="$t('cusEntry.bidMod.invoiceType')"
        min-width="120"
        :render-header="_addStarToColumn"
      >
        <template slot-scope="scope">
          <DictSelect
            v-model="scope.row.invoiceType"
            code="EXT_SOU_INQ_ORDER_INVOICE_TYPE"
            :clearable="false"
            :disabled="isReadOnly"
            @change="value => invoiceTypeChange(value, scope.row)"
          />
        </template>
      </el-table-column>
      <!--t 未税单价-->
      <el-table-column
        align="center"
        prop="orderNotaxPrice"
        :label="$t('cusEntry.bidMod.quotenotaxPrice2')"
        width="120"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <el-input
            v-model="scope.row.orderNotaxPrice"
            v-input-format="{ type: 'float', negative: false, zero: false, digits: 2 }"
            class="price-input"
            :disabled="scope.row.isLadder === 'Y' || isReadOnly"
            @change="noTaxPriceChange(scope)"
          />
        </template>
      </el-table-column>
      <!--t 含税单价-->
      <el-table-column
        align="center"
        prop="orderTaxPrice"
        :label="$t('cusEntry.bidMod.quotetaxPrice2')"
        width="120"
      />
      <!--t 价税合计-->
      <el-table-column
        align="center"
        prop="priceTaxTotal"
        :label="$t('cusEntry.bidMod.taxAmount')"
        width="120"
      />
      <!-- 到货周期(自然日) -->
      <el-table-column
        align="center"
        prop="extLeadTime"
        :label="$t('cusEntry.bidMod.deliveryCycle')"
        min-width="150"
        :render-header="_addStarToColumn"
      >
        <template slot-scope="scope">
          <el-input
            v-model="scope.row.extLeadTime"
            v-input-format="{ type: 'integer', negative: false}"
            :disabled="isReadOnly"
          />
        </template>
      </el-table-column>
      <!-- 质保期(自然日) -->
      <el-table-column
        align="center"
        prop="extWarrantyPeriod"
        :label="$t('cusEntry.bidMod.warrantyPeriod')"
        min-width="150"
        :render-header="_addStarToColumn"
      >
        <template slot-scope="scope">
          <el-input
            v-model="scope.row.extWarrantyPeriod"
            v-input-format="{ type: 'integer', negative: false}"
            :disabled="isReadOnly"
          />
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
      <!--t 备注-->
      <el-table-column
        align="center"
        prop="orderRemark"
        :label="$t('common.remark')"
        min-width="160"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <el-input
            v-model="scope.row.orderRemark"
            :disabled="isReadOnly"
          />
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="userCode"
        :label="'操作'"
        fixed="right"
        width="80"
      >
        <template v-slot="{ row, $index }">
          <!--阶梯价-->
          <el-button
            v-if="row.isLadder === 'Y'"
            type="text"
            @click="ladderPriceClick($index, row)"
          >
            {{ '阶梯报价' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <div :class="['c-pagination',device]">
      <el-pagination
        class="pagination"
        popper-class="c-pagination-dropdown"
        :layout="layoutRes"
        :current-page="pageNum"
        :page-size="pageSize"
        :page-sizes="pageSizes"
        :total="pageTotal"
        :pager-count="pagerCount"
        @size-change="changeCurrentSize"
        @current-change="changeCurrentIndex"
      />
    </div>

    <!--d 阶梯价-->
    <LadderPrice
      v-if="ladderPriceVisible"
      :visible.sync="ladderPriceVisible"
      :business-type="BUSINESS_TYPE_ENUM.INQUIRY_LTS"
      page-type="quote"
      :edit-row="editRow"
      :tax="rowTax"
      :isReadOnly="isReadOnly"
      @save-quote="saveLadderItems"
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
import LadderPrice from './ladderPrice'
import TemplatePriceDialog from 'lib@/composition/inquiry/templatePriceDialog'
import FormulaPrice from 'lib@/composition/origin/formulaPrice'
import SeparationPriceDialog from 'lib@/composition/quoteSeparation/templatePriceDialog'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import { inqSupplierHttp } from 'modcs@/inquirySupplier/api'
import { adaptDictData } from '@/utils'
import { getAllPurTax } from '@/api/common'
export default {
  name: 'ItemInfo',

  components: {
    LadderPrice,
    PaymentTypeDialog,
    TechnicalDocumentsDialog,
    TemplatePriceDialog,
    FormulaPrice,
    SeparationPriceDialog,
    MImport
  },

  props: {
    itemList: Array,

    isReadOnly: Boolean,
    // 代理报价参数
    projectId: {
      type: Number,
      default: null
    },
    round: {
      type: Number,
      default: null
    }
  },

  data () {
    return {
      extraData: {
        projectId: null
      },
      upLoadUrl: '/api-sou/npm/vendor/ext_pur_inq/order/getLastOrderItems/import',
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
      quoteTotalPrice: new Map(),
      currentRow: {},
      taxList: [],
      rowTax: '',
      pageNum: 1,
      pageSize: 15,
      pageSizes: [15],
      pagerCount: 7,
      pageTotal: 0,
      layout: 'total, prev, pager, next,sizes, jumper',
      pageList: []
    }
  },
  computed: {
    device () {
      return this.$store.state.app.device
    },
    layoutRes () {
      if (this.device === 'device-xs') {
        return 'total, prev, next,sizes, jumper'
      } else {
        return this.layout
      }
    }
  },
  watch: {
    itemList: {
      handler (newValue) {
        if (newValue) {
          // 深拷贝
          this.itemListData = JSON.parse(JSON.stringify(newValue))
          this.pageTotal = this.itemListData ? this.itemListData.length : 0
          this.pageNum = 1
          this.extraData.projectId = this.projectId
          this.initPageList()
        }
      },
      deep: true,
      immediate: true
    },
    projectId: {
      immediate: true,
      deep: true,
      handler (newValue) {
        this.extraData.projectId = newValue
      }
    }
  },
  created () {
    // 获取所有税率
    getAllPurTax().then(res => {
      this.taxList = adaptDictData(res.data, 'tax')
    })
  },
  methods: {
    getAreaLabel (array) {
      let str = ''
      let index = array.length
      for (let item of array) {
        str += this.$getDictLabel('REGION', item)
        --index
        if (index != 0) {
          str += ','
        }
      }
      return str
    },
    // 前端分页
    // 改变 currentNum
    changeCurrentIndex (value) {
      this.pageNum = value
      this.initPageList()
    },
    // 改变 currentSize
    changeCurrentSize (value) {
      console.log(value)
    },
    initPageList () {
      this.pageList = []
      let index = this.pageNum
      let size = this.pageSize
      this.pageList = this.itemListData.filter((item, i) => {
        return (index - 1) * size <= i & i < index * size
      })
      this.$nextTick(() => {
        if (this.$refs.itemInfoTable) this.$refs.itemInfoTable.doLayout()
      })
    },
    /* 选择行 */
    handleCurrentChange (row) {
      this.currentRow = row
    },
    /* 导入成功 */
    handleSuccess () {
      this.$message.success(this.$t('cusEntry.tipMessage.importSuccess'))
      this.$emit('refresh')
    },
    downloadTemplate () {
      downloadFileLink(
        `/api-sou/npm/vendor/ext_pur_inq/order/getLastOrderItems/download?projectId=${this.projectId}&round=${this.round}`,
      ).catch(() => {
        this.$message.error(this.$t('purchaseDemand.downloadFail'))
      })
    },
    /* 未税单价改变 */
    noTaxPriceChange ({ row, $index }) {
      let i = this.pageNum - 1
      let index = $index + i * 15
      // 先格式化
      if (this.pricePrecision >= 0 && (row.orderNotaxPrice || row.orderNotaxPrice === 0)) {
        row.orderNotaxPrice = bigPriceRound(row.orderNotaxPrice, 2)
      }
      const taxRate = this.taxList.find(item => item.value === row.taxKey).key
      if (row.taxKey && row.isLadder !== 'Y' && !this.isReadOnly) {
        row.orderTaxPrice = (Number(row.orderNotaxPrice) * (1 + Number(taxRate) / 100)).toFixed(4)
      }
      row.priceTaxTotal = (row.orderTaxPrice && row.requireQuantity) ? (Number(row.orderNotaxPrice) * (1 + Number(taxRate) / 100) * Number(row.requireQuantity)).toFixed(4) : ''
      this.$set(this.itemListData, index, row)
      // this.itemListData.splice($index, 1, row)
    },
    /* 税率改变 */
    taxKeySelectChange (value, row) {
      // if (row.invoiceType === 'ORDINARY_TICKET' && value === 'VAT_IN_0') {
      //   this.$message.warning(this.$t('cusEntry.tipMessage.'))
      //   return
      // }
      if (value === 'VAT_IN_0') {
        row.invoiceType = 'ORDINARY_TICKET'
      } else if (value === 'VAT_IN_13') {
        row.invoiceType = 'SPECIAL_TICKET'
      }
      // this.tax = item.key
      // this.currencyAndTaxChange()
      /* 获取相应的税率值 */
      const taxRate = this.taxList.find(item => item.value === value).key
      /* 计算含税单价 */
      row.orderTaxPrice = row.orderNotaxPrice ? (Number(row.orderNotaxPrice) * (1 + Number(taxRate) / 100)).toFixed(4) : ''
      /* 计算价税合计 */
      row.priceTaxTotal = (row.orderNotaxPrice && row.requireQuantity) ? (Number(row.orderNotaxPrice) * (1 + Number(taxRate) / 100) * Number(row.requireQuantity)).toFixed(4) : ''
    },
    /* 发票类型变更 */
    invoiceTypeChange (value, row) {
      /* 普票 */
      if (value === 'ORDINARY_TICKET') {
        row.taxKey = 'VAT_IN_0'
      } else {
        /* 专票 */
        row.taxKey = 'VAT_IN_13'
      }
      /* 重新计算含税单价、价税合计 */
      this.taxKeySelectChange(row.taxKey, row)
    },
    /* 阶梯价 */
    ladderPriceClick (index, row) {
      if (!row.taxKey) {
        this.$message.warning(this.$t('bidMod.inpTaxKey'))
        return
      }
      this.rowTax = this.taxList.find(item => item.value === row.taxKey).key
      this.editRow = {
        ...row,
        ladderList: row.ladderPriceList
      }
      let i = this.pageNum - 1
      this.editIndex = index + i * 15
      this.ladderPriceVisible = true
    },
    /* 保存阶梯价 */
    saveLadderItems (val) {
      this.itemListData[this.editIndex].ladderPriceList = [].concat(val)
      this.itemListData[this.editIndex].orderNotaxPrice = val.length ? val[0].orderNotaxPrice : null
      this.itemListData[this.editIndex].orderTaxPrice = val.length ? val[0].orderTaxPrice : null
    },
    /* 校验并返回表单值 */
    validateForm (isValidate) {
      return new Promise(resolve => {
        // 编排需要提交的数据
        const orderItemList = (this.itemListData || []).map(item => {
          const quoteData = this.quoteData.get(item.souItemId)
          const quoteTotalPrice = this.quoteTotalPrice.get(item.souItemId)
          return {
            ...item,
            // 询价行ID
            souItemId: item.souItemId,
            // 供应商选择的币种
            // orderCurrency: this.itemListForm.currency,
            // 税率编码
            taxKey: item.taxKey,
            // 供应商的未税报价
            orderNotaxPrice: this.isSeparation ? (quoteTotalPrice ?? item.orderNotaxPrice) : item.orderNotaxPrice,
            // 付款账期
            paymentList: item.paymentList || [],
            // 公式报价时的参数,例如:"{\"311693985994880\":\"1\",\"311694062098560\":\"1\",\"3116...
            formulaAttrValues: item.formulaAttrValues || '',
            // 阶梯价信息
            ladderPriceList: item.ladderPriceList || [],
            quoteData,
            extLeadTime: item.extLeadTime,
            extWarrantyPeriod: item.extWarrantyPeriod,
            advancePaymentRemark: item.advancePaymentRemark,
            invoiceType: item.invoiceType,
            orderRemark: item.orderRemark
          }
        })
        let resolveStatus = true
        if (isValidate) {
          // 遍历查找需要校验必填的字段
          for (const [index, item] of orderItemList.entries()) {
            if (!item.taxKey && item.orderNotaxPrice) {
              this.$message.warning(this.$t('bidMod.inpTaxKey'))
              resolve(false)
              return
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
.tip{
    font-size: 12px;
    color:red;
    display: inline-block;
    margin: 8px 0;
}
.c-pagination {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding-top: 16px;
  padding-bottom: 16px;
  width: 100%;
}

.c-pagination-dropdown {
  .el-select-dropdown__item {
    font-size: 12px;
    font-weight: normal;
  }
}
</style>
