<template>
  <el-container direction="vertical">
    <el-main>
      <div v-if="showTitle">
        <p style="font-weight:bold;font-size: 14px;">
          <!-- 供应商报价对比详情 -->
          {{ $t('cusEntry.supplement20250205.vendorQuotaDetail') }}
        </p>
        <p style="color:red;">
          <!-- 注：统一折算成卢布进行价格对比 -->
          {{ $t('cusEntry.supplement20250205.tipMessage1') }}
        </p>
      </div>
      <el-table
        border
        max-height="400"
        :data="itemList"
        :span-method="spanMethod"
        :cell-class-name="cellClassName"
      >
        <el-table-column
          align="center"
          type="index"
          fixed="left"
          :label="$t('common.sort')"
          width="60"
        />
        <!-- 包名 -->
        <!-- <el-table-column
          v-if="mergeFlag"
          align="center"
          prop="extPackageName"
          :label="$t('cusEntry.biddingSettings.bagName')"
          min-width="120"
          show-overflow-tooltip
        /> -->
        <el-table-column
          align="center"
          prop="itemDesc"
          :label="$t('bidMod.designation')"
          min-width="120"
          show-overflow-tooltip
        />
        <!-- <el-table-column
          align="center"
          prop="extBrand"
          :label="$t('dataConfMod.band')"
          min-width="120"
          show-overflow-tooltip
        /> -->
        <el-table-column
          align="center"
          prop="categoryName"
          :label="$t('bidMod.specification')"
          min-width="120"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          prop="unit"
          :label="$t('bidMod.unit')"
          min-width="80"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          prop="extQuantity"
          :label="$t('contractMod.contractQuantity')"
          min-width="100"
          show-overflow-tooltip
        />
        <el-table-column
          v-for="(item,index) in itemList[0]?.priceList"
          :key="index"
          :label="item.vendorName"
          align="center"
          min-width="150"
          show-overflow-tooltip
        >
          <!-- 未税单价（卢布） -->
          <el-table-column
            align="center"
            :prop="`extPriceNoTax${item.vendorId}`"
            :label="$t('cusEntry.supplement20250205.unitPriceExcludingTaxRUB')"
            min-width="150"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              {{ scope.row.priceList[index].extPriceNoTax }}
            </template>
          </el-table-column>
          <!-- 未税总价（卢布） -->
          <el-table-column
            align="center"
            :prop="`extProvPriceSumNoTax${item.vendorId}`"
            :label="$t('cusEntry.supplement20250205.totalPriceExcludingTaxRUB')"
            min-width="150"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              {{ scope.row.priceList[index].extProvPriceSumNoTax }}
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="extTaxRate"
            :label="$t('bidMod.taxRate2')"
            min-width="100"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              {{ scope.row.priceList[index].extTaxRate }}
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="extInvoiceType"
            :label="$t('accountMod.invoiceType')"
            min-width="150"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              {{ scope.row.priceList[index].extInvoiceType ? getLabel(extInvoiceTypeList, scope.row.priceList[index].extInvoiceType) : '' }}
            </template>
          </el-table-column>
        </el-table-column>
      </el-table>
    </el-main>
  </el-container>
</template>
<script>
import { bidBuyerHttp } from 'modcb@/biddingBuyer/api'
import { getDictItemList } from '@/api/common'
import { adaptDictData } from '@/utils'
export default {
  props: {
    projectId: null,
    showTitle: {
      type: Boolean,
      default: true
    }
  },
  data () {
    return {
      mergeFlag: false,
      extInvoiceTypeList: [],
      itemList: []
    }
  },
  computed: {
    curProjectId () {
      return this.projectId || this.$attrs?.params?.projectId
    }
  },
  watch: {
    curProjectId: {
      async handler (nVal) {
        if (nVal) {
          await this.fatchDictData()
          this.getDetail(nVal)
        }
      },
      immediate: true,
      deep: true
    }
  },
  created () {

  },
  methods: {
    // 获取数据字典
    fatchDictData () {
      // 批量查询字典
      let dictParamsArr = [
        { dictCode: 'SOU_BIDPRICE_INVOICE_TYPE' } // 发票类型
      ]
      getDictItemList(dictParamsArr).then(res => {
        const [SOU_BIDPRICE_INVOICE_TYPE] = res.data
        this.extInvoiceTypeList = adaptDictData(SOU_BIDPRICE_INVOICE_TYPE.SOU_BIDPRICE_INVOICE_TYPE)
      })
    },
    getLabel (list, val) {
      let obj = list.find(v => v.value === val) || {}
      return obj.label || ''
    },
    /* 构造合计数据结构 */
    setSumStruct (arr) {
      if (arr.length === 0) return arr
      let firstItem = arr[0]
      if (this.mergeFlag) {
        let finalArr = []
        arr.map((item, index) => {
          if (item.extPackageName == arr[index + 1]?.extPackageName) {
            finalArr.push(item)
          } else {
            finalArr.push(item)
            finalArr.push({
              extPackageName: this.$t('cusEntry.supplement20250205.subtotal'),// '小计'
              priceList: item?.priceList.map(item => ({
                extProvPriceSumNoTax: item.extPacknameProvPriceSumTax
              }))
            })
          }
        })
        return [
          ...finalArr,
          // {
          //   extPackageName: this.$t('cusEntry.supplement20250205.totalTax'), // '合计（含税）'
          //   priceList: firstItem?.priceList.map(item => ({
          //     extProvPriceSumNoTax: item.extTotalProvPriceSumTax
          //   }))
          // },
          {
            extPackageName: this.$t('cusEntry.supplement20250205.totalNoTax'), // '合计（未税）'
            priceList: firstItem?.priceList.map(item => ({
              extProvPriceSumNoTax: item.extTotalProvPriceSumNoTax
            }))
          }
        ]
      } else {
        return [
          ...arr,
          // {
          //   itemDesc: this.$t('cusEntry.supplement20250205.totalTax'), // '合计（含税）'
          //   priceList: firstItem?.priceList.map(item => ({
          //     extProvPriceSumNoTax: item.extTotalProvPriceSumTax
          //   }))
          // },
          {
            itemDesc: this.$t('cusEntry.supplement20250205.totalNoTax'), // '合计（未税）'
            priceList: firstItem?.priceList.map(item => ({
              extProvPriceSumNoTax: item.extTotalProvPriceSumNoTax
            }))
          }
        ]
      }
    },
    // 设置合并表格行列
    spanMethod ({ row, column, rowIndex, columnIndex }) {
      const length = this.itemList.length
      if (this.mergeFlag) { // 合并招标展示包名 有小计 合并前六列
        const mergeRowIndexs = [length - 1]
        if (mergeRowIndexs.includes(rowIndex) || row.extPackageName === this.$t('cusEntry.supplement20250205.subtotal')) { // 小计
          if (column.property == 'extPackageName') {
            return [1, 6]
          } else if (['itemDesc', 'extBrand', 'categoryName', 'unit', 'extQuantity'].includes(column.property)) {
            return [0, 0]
          }
        }
      } else { // 非合并招标不展示包名 无小计 合并前4列
        const mergeRowIndexs = [length - 1]
        if (mergeRowIndexs.includes(rowIndex)) {
          if (column.property == 'itemDesc') {
            return [1, 4]
          } else if (['extBrand', 'categoryName', 'unit', 'extQuantity'].includes(column.property)) {
            return [0, 0]
          }
        }
      }
    },
    getDetail (projectId) {
      bidBuyerHttp.control.priceComparison(projectId).then(res => {
        if (res && res.data) {
          this.mergeFlag = res.data.mergeFlag
          this.itemList = this.setSumStruct(res.data.comparePriceList)
        }
      })
    },
    // 冒泡排序
    bubbleSortByProperty (arr, prop) {
      let len = arr.length
      for (let i = 0; i < len - 1; i++) {
        for (let j = 0; j < len - 1 - i; j++) {
          if (arr[j][prop] > arr[j + 1][prop]) {
            let temp = arr[j]
            arr[j] = arr[j + 1]
            arr[j + 1] = temp
          }
        }
      }
      return arr
    },
    // 每一个报价行，标注最高价的单价和总价为黄色，标注最低价的单价和总价为绿色，只有一家供应商报价时，不标颜色
    getMaxOrMinObj (index) {
      let priceList = JSON.parse(JSON.stringify(this.itemList[index].priceList || []))
      if (priceList.length < 2) {
        return { extPriceNoTaxMax: 'same', extPriceNoTaxMin: 'same', extProvPriceSumNoTaxMax: 'same', extProvPriceSumNoTaxMin: 'same' }
      }
      // 未税单价最高价、最低价
      let extPriceNoTaxList = this.bubbleSortByProperty(priceList.filter(item => item.extPriceNoTax), 'extPriceNoTax')
      const priceLength = extPriceNoTaxList.length
      // 未税总价最高价、最低价
      let extProvPriceSumNoTaxList = this.bubbleSortByProperty(priceList.filter(item => item.extProvPriceSumNoTax), 'extProvPriceSumNoTax')
      const sumLength = extProvPriceSumNoTaxList.length

      let extPriceNoTaxMax = `extPriceNoTax${extPriceNoTaxList[priceLength - 1].vendorId}`
      let extPriceNoTaxMin = `extPriceNoTax${extPriceNoTaxList[0].vendorId}`
      let extProvPriceSumNoTaxMax = `extProvPriceSumNoTax${extProvPriceSumNoTaxList[sumLength - 1].vendorId}`
      let extProvPriceSumNoTaxMin = `extProvPriceSumNoTax${extProvPriceSumNoTaxList[0].vendorId}`

      // 如果未税单价、未税总价最高价最低价相同
      let extPriceNoTaxListSame = extPriceNoTaxList[priceLength - 1].extPriceNoTax == extPriceNoTaxList[0].extPriceNoTax
      let extProvPriceSumNoTaxListSame = extProvPriceSumNoTaxList[sumLength - 1].extProvPriceSumNoTax == extProvPriceSumNoTaxList[0].extProvPriceSumNoTax
      if (extPriceNoTaxListSame && extProvPriceSumNoTaxListSame) {
        return { extPriceNoTaxMax: 'same', extPriceNoTaxMin: 'same', extProvPriceSumNoTaxMax: 'same', extProvPriceSumNoTaxMin: 'same' }
      } else if (extPriceNoTaxListSame) {
        return { extPriceNoTaxMax: 'same', extPriceNoTaxMin: 'same', extProvPriceSumNoTaxMax, extProvPriceSumNoTaxMin }
      } else if (extProvPriceSumNoTaxListSame) {
        return { extPriceNoTaxMax, extPriceNoTaxMin, extProvPriceSumNoTaxMax: 'same', extProvPriceSumNoTaxMin: 'same' }
      } else {
        return { extPriceNoTaxMax, extPriceNoTaxMin, extProvPriceSumNoTaxMax, extProvPriceSumNoTaxMin }
      }
    },
    cellClassName ({ row, column, rowIndex, columnIndex }) {
      if (this.itemList.length > 0 && rowIndex < (this.itemList.length - 1)) {
        if (column.property == this.getMaxOrMinObj(rowIndex)?.extPriceNoTaxMax) {
          return 'comparison-highest-price'
        } else if (column.property == this.getMaxOrMinObj(rowIndex)?.extPriceNoTaxMin) {
          return 'comparison-lowest-price'
        } else if (column.property == this.getMaxOrMinObj(rowIndex)?.extProvPriceSumNoTaxMax) {
          return 'comparison-highest-price'
        } else if (column.property == this.getMaxOrMinObj(rowIndex)?.extProvPriceSumNoTaxMin) {
          return 'comparison-lowest-price'
        }
      }
    }
  }
}
</script>
<style scoped lang="scss">
</style>
<style>
.comparison-highest-price {
  background-color: yellow;
}
.comparison-lowest-price {
  background-color: #91CE51;
}
</style>
