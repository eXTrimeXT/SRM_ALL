<template>
  <div class="wrapper">
    <div class="btns">
      <el-button v-if="!readonly && !isChange" type="primary" @click="add">
        {{ $t('common.add') }}
      </el-button>
      <!-- 导入行信息 -->
      <MImport
        v-if="!readonly && agreementId && !isChange"
        ref="import"
        :title="$t('cusEntry.supplement20250121.importLine')"
        :up-load-url="upLoadUrl"
        type="default"
        :extra-data="extraData"
        @downloadTemplate="downloadTemplate(4)"
        @handleSuccess="handleSuccess"
      />
      <ExportExcel
        page-url="/api-sou/jcAgreement/exportLineAgreementPage"
        :filter-params="filterParams"
        :dict-codes="dictCodes"
        :table-header="tableHeader"
        export-mode="front"
        type="default"
      />
    </div>
    <el-table
      class="mg-10"
      border
      stripe
      :data="tableData.slice((pageInfo.pageNum-1)*pageInfo.pageSize,pageInfo.pageNum*pageInfo.pageSize)"
    >
      <!-- 物料行号 -->
      <el-table-column
        type="index"
        :label="$t('cusEntry.supplement20250121.materialLineNo')"
        width="80"
      />
      <!-- 物料编码 -->
      <el-table-column
        prop="materialCode"
        :label="$t('common.materialCode')"
        minWidth="130"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <QuickSearch
            v-if="!readonly && !isChange"
            :show-input="scope.row.materialCode"
            show-key="materialCode"
            :scope-data="scope.row"
            name="scc_base_material_item_contract"
            @close-quicksearch="getItemObj"
          />
          <span v-else>{{ scope.row.materialCode }}</span>
        </template>
      </el-table-column>
      <!-- 物料名称 -->
      <el-table-column
        prop="materialNameShow"
        :label="$t('common.materialName')"
        minWidth="130"
        showOverflowTooltip
      />
      <!-- 商品分类 -->
      <el-table-column
        prop="goodsTypeName"
        :label="$t('cusEntry.supplement20250121.goodsType')"
        minWidth="130"
        showOverflowTooltip
      />
      <!-- 规格 -->
      <el-table-column
        prop="extMaterialModelShow"
        :label="$t('common.specification')"
        minWidth="130"
        showOverflowTooltip
      />
      <!-- 单位 -->
      <el-table-column
        prop="unit"
        :label="$t('dataConfMod.unit')"
        minWidth="130"
        :formatter="(row,column,cellValue) => $getDictLabel('unit', cellValue)"
      />
      <!-- 品牌 -->
      <el-table-column
        prop="brand"
        :label="$t('dataConfMod.band')"
        minWidth="130"
      >
        <template v-slot="scope">
          <el-input v-if="!readonly && !isChange" v-model="scope.row.brand" />
          <span v-else>{{ scope.row.brand }}</span>
        </template>
      </el-table-column>
      <!-- 币种 -->
      <el-table-column
        prop="extCurrency"
        :label="$t('vendorMod.currencyCode')"
        minWidth="130"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <DictSelect
            v-model="scope.row.extCurrency"
            code="currency"
            :disabled="readonly"
          />
        </template>
      </el-table-column>
      <!-- 税率% -->
      <el-table-column
        prop="taxRate"
        :label="$t('bidMod.taxRatePer_price')"
        minWidth="130"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <DictSelect
            v-if="!readonly"
            v-model="scope.row.taxRate"
            :transformOptions="transformOptions"
            code="tax"
            @change="(val) => taxRateChange(val,scope)"
          />
          <span v-else>{{ scope.row.taxRate }}</span>
        </template>
      </el-table-column>
      <!-- 未税单价 -->
      <el-table-column
        prop="priceTax"
        :label="$t('bid_mod.untaxedPrice')"
        minWidth="130"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <el-input v-if="!readonly" v-model="scope.row.priceTax" v-input-format="{type: 'float', digits: 2, negative: false}" @change="(val) => priceTaxChange(val,scope)" />
          <span v-else>{{ scope.row.priceTax }}</span>
        </template>
      </el-table-column>

      <!-- 含税单价-保留2位小数 -->
      <el-table-column
        prop="ratePrice"
        :label="$t('bidMod.unitPrice_price')"
        minWidth="130"
      />
      <!-- 参考价 -->
      <el-table-column
        prop="referencePrice"
        :label="$t('cusEntry.sup.extReferencePrice')"
        minWidth="130"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <el-input v-if="!readonly" v-model="scope.row.referencePrice" v-input-format="{type: 'float', digits: 4, negative: false}" />
          <span v-else>{{ scope.row.referencePrice }}</span>
        </template>
      </el-table-column>
      <!-- 交货周期（自然日） -->
      <el-table-column
        prop="leadTime"
        :label="$t('cusEntry.supplement20250121.leadTime')"
        minWidth="150"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <el-input v-if="!readonly" v-model="scope.row.leadTime" v-input-format="{type: 'integer', negative: false}" />
          <span v-else>{{ scope.row.leadTime }}</span>
        </template>
      </el-table-column>
      <!-- 质保期（自然日） -->
      <el-table-column
        prop="sellByDate"
        :label="$t('cusEntry.supplement20250121.sellByDate')"
        minWidth="130"
      >
        <template v-slot="scope">
          <el-input v-if="!readonly" v-model="scope.row.sellByDate" v-input-format="{type: 'integer', negative: false}" />
          <span v-else>{{ scope.row.sellByDate }}</span>
        </template>
      </el-table-column>

      <!-- 起订量--默认1 -->
      <el-table-column
        prop="startNum"
        :label="$t('cusEntry.sup.orderQuantityMinimum')"
        minWidth="130"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <el-input v-if="!readonly" v-model="scope.row.startNum" v-input-format="{type: 'integer', negative: false}" />
          <span v-else>{{ scope.row.startNum }}</span>
        </template>
      </el-table-column>
      <!-- 是否预付 -->
      <el-table-column
        prop="extIsPrepaid"
        :label="$t('cusEntry.bidMod.extIsPrepaid')"
        minWidth="130"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <DictSelect
            v-model="scope.row.extIsPrepaid"
            code="YES_OR_NO"
            :disabled="readonly"
          />
        </template>
      </el-table-column>
      <!-- 预付比例% -->
      <el-table-column
        prop="extPrepaidRatio"
        :label="$t('cusEntry.bidMod.extPrepaidRatio')"
        minWidth="130"
      >
        <template v-slot="scope">
          <el-input v-if="!readonly" v-model="scope.row.extPrepaidRatio" v-input-format="{type: 'float', digits: 2, negative: false}" />
          <span v-else>{{ scope.row.extPrepaidRatio }}</span>
        </template>
      </el-table-column>
      <!-- 协议行说明 -->
      <el-table-column
        prop="agreementDes"
        :label="$t('cusEntry.supplement20250121.protocolLineRemark')"
        minWidth="130"
      >
        <template v-slot="scope">
          <el-input v-if="!readonly" v-model="scope.row.agreementDes" />
          <span v-else>{{ scope.row.agreementDes }}</span>
        </template>
      </el-table-column>
      <!-- 操作 -->
      <el-table-column
        v-if="!readonly"
        prop="operation"
        :label="$t('common.operation')"
        width="100"
        fixed="right"
      >
        <template v-slot="scope">
          <el-button
            v-if="!isChange"
            type="text"
            @click="deleteRow(scope)"
          >
            {{ $t('common.delete') }}
          </el-button>
          <el-button
            v-if="agreementStatus === 'EXECUTING'"
            type="text"
            @click="abandon(scope.row)"
          >
            {{ $t('common.abandon') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <CPagination
      ref="queryPagination"
      class="c-query-table-pagination"
      style="padding-bottom:20px"
      :total="tableData.length"
      :page-num="pageInfo.pageNum"
      :page-size="pageInfo.pageSize"
      @current-change="handleCurrentChange"
      @size-change="handleSizeChange"
    />

    <!-- 阶梯价弹窗 -->
    <LadderDialog
      ref="ladderDialog"
      :visible.sync="ladderDialogVisible"
      :readonly="readonly"
      :value="sccSouTieredPricingList"
      :row="curRow"
      @confirm="ladderDialogConfirm"
    />
  </div>
</template>
<script>
import QuickSearch from 'lib@/components/QuickSearch'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import LadderDialog from './dialog/ladderDilaog'
import ExportExcel from 'lib@/components/export-excel'
import CPagination from 'lib@/components/c-pagination'
import { centralHttp } from 'modcb@/jcAgreement/api'
export default {
  components: {
    QuickSearch,
    MImport,
    LadderDialog,
    ExportExcel,
    CPagination
  },
  props: {
    value: {
      type: Array,
      default: () => []
    },
    readonly: {
      type: Boolean,
      default: false
    },
    isChange: {
      type: Boolean,
      default: false
    },
    agreementId: null,
    // 协议：合同协议:contract;集采协议:central
    mode: {
      type: String,
      default: 'central'
    },
    // 单据状态
    agreementStatus: {
      type: String,
      default: ''
    }
  },
  data () {
    return {
      dictCodes: {
        unit: 'unit',
        extCurrency: 'currency',
        extIsPrepaid: 'YES_OR_NO'
      },
      extraData: {
        fileModular: 'sou',
        fileFunction: 'centralizedAgree',
        fileType: 'excel'
      },
      sccSouTieredPricingList: [],
      ladderDialogVisible: false,
      curRow: {},
      curIndex: null,
      transformOptions (data) {
        if (data && data.length) {
          return data.map(item => ({
            id: item.id,
            value: +item.key,
            label: item.label
          }))
        }
        return []
      },
      pageInfo: {
        pageNum: 1,
        pageSize: 15
      }
    }
  },
  computed: {
    tableData: {
      get () {
        return this.value
      },
      set (val) {
        this.$emit('update:value', val)
      }
    },
    upLoadUrl () {
      let url
      if (this.mode === 'central') {
        url = '/api-sou/jcAgreement/importEditAgreementLine'
      } else {
        url = '/api-sou/jcAgreement/importHtEditAgreementLine'
      }
      return `${url}?agreementId=${this.agreementId}`
    },
    filterParams () {
      return {
        agreementId: this.agreementId
      }
    },
    tableHeader () {
      return [
        {
          prop: 'materialCode',
          label: this.$t('common.materialCode') // '物料编码'
        },
        {
          prop: 'materialName',
          label: this.$t('common.materialName') // '物料名称'
        },
        {
          prop: 'goodsTypeName',
          label: this.$t('cusEntry.supplement20250121.goodsType') // '商品分类'
        },
        {
          prop: 'standards',
          label: this.$t('common.specification') // '规格'
        },
        {
          prop: 'unit',
          label: this.$t('dataConfMod.unit') // '单位'
        },
        {
          prop: 'brand',
          label: this.$t('dataConfMod.band') // '品牌'
        },
        {
          prop: 'priceTax',
          label: this.$t('orderMod.untaxedPrice') // '未税单价'
        },
        {
          prop: 'extCurrency',
          label: this.$t('vendorMod.currencyCode') // '币种'
        },
        {
          prop: 'taxRate',
          label: this.$t('bidMod.taxRatePer_price') // '税率%'
        },
        {
          prop: 'ratePrice',
          label: this.$t('bidMod.unitPrice_price') // '含税单价'
        },
        {
          prop: 'referencePrice',
          label: this.$t('cusEntry.sup.extReferencePrice') // '参考价'
        },
        {
          prop: 'leadTime',
          label: this.$t('cusEntry.supplement20250121.leadTime') // '交货周期（自然日）'
        },
        {
          prop: 'sellByDate',
          label: this.$t('cusEntry.supplement20250121.sellByDate') // '质保期（自然日）'
        },
        {
          prop: 'startNum',
          label: this.$t('cusEntry.sup.orderQuantityMinimum') // '起订量'
        },
        {
          prop: 'extIsPrepaid',
          label: this.$t('cusEntry.bidMod.extIsPrepaid') // '是否预付'
        },
        {
          prop: 'extPrepaidRatio',
          label: this.$t('cusEntry.bidMod.extPrepaidRatio') // '预付比例%'
        },
        {
          prop: 'agreementDes',
          label: this.$t('cusEntry.supplement20250121.protocolLineRemark') // '协议行说明'
        }
      ]
    }
  },
  methods: {
    // 废弃
    abandon (row) {
      // 是否废弃
      this.$confirm(this.$t('common.abandonA'), this.$t('common.tips'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(async () => {
        const data = {
          agreementInfoId: row.agreementInfoId,
          agreementId: row.agreementId,
          materialCode: row.materialCode
        }
        await centralHttp.abandon(data)
        this.$message.success(this.$t('cusEntry.tipMessage.successAbandon'))
        this.$emit('refresh')
      }).catch(err => {
        console.log(err)
      })
    },
    add () {
      this.tableData.push({
        materialId: null,
        materialCode: null,
        materialName: null,
        categoryName: null,
        specification: null,
        brand: null,
        expectNum: null,
        isTieredPricing: null,
        priceTax: null,
        extCurrency: null,
        taxRate: null,
        taxKey: null,
        ratePrice: null,
        referencePrice: null,
        leadTime: null,
        sellByDate: null,
        startNum: 1,
        multipleStartNum: null,
        extIsPrepaid: null,
        extPrepaidRatio: null,
        agreementDes: null,
        unit: null,
        unitName: null,
        sccSouTieredPricingList: []
      })
    },
    priceTaxChange (val, scope) {
      this.getRatePrice(val, scope.row.taxRate, scope)
    },
    taxRateChange (val, scope) {
      this.getRatePrice(scope.row.priceTax, val, scope)
    },
    getRatePrice (priceTax, taxRate, scope) {
      if (!priceTax || (!taxRate && taxRate != '0')) return
      // let rateArr = taxRate.match(/\d+/)
      // let taxNum = +rateArr[rateArr.length - 1] / 100
      let taxNum = taxRate / 100
      let ratePrice = priceTax * (1 + taxNum)
      ratePrice = Math.round(ratePrice * 100) / 100
      scope.row.ratePrice = ratePrice
      this.tableData[scope.$index] = scope.row
    },
    deleteRow (scope) {
      this.tableData.splice(scope.$index, 1)
    },
    ladderClick (row, index) {
      if (!row.taxRate && row.taxRate !== 0) {
        // 请选择税率
        this.$message.warning(this.$t('bidMod.msgSelTaxRate'))
        return
      }
      this.curIndex = index
      this.curRow = row
      this.sccSouTieredPricingList = row.sccSouTieredPricingList || []
      this.sccSouTieredPricingList.forEach(item => {
        if (item.priceTax && (row.taxRate || row.taxRate === 0)) {
          item.ratePrice = Math.round(item.priceTax * (1 + row.taxRate / 100) * 100) / 100
        }
      })
      this.ladderDialogVisible = true
    },
    ladderDialogConfirm (val) {
      let newRow = { ...this.curRow, sccSouTieredPricingList: val }
      this.tableData.splice(this.curIndex, 1, newRow)
      this.ladderDialogVisible = false
    },
    async getItemObj (val, scope) {
      scope.materialId = val ? val.materialId : ''
      scope.materialCode = val ? val.materialCode : ''
      scope.materialName = val ? val.materialName : ''
      scope.goodsTypeName = val ? val.categoryName : ''
      scope.goodsTypeId = val ? val.categoryId : ''
      scope.goodsTypeCode = val ? val.categoryCode : ''
      scope.standards = val ? val.extMaterialModel : ''
      scope.unit = val ? val.unit : ''
      scope.unitName = val ? val.unitName : ''
      if (val) {
        const res = await this.$http({
          url: '/api-base/material/materialItem/ext/multilingual',
          method: 'POST',
          data: { materialIds: [val.materialId], language: this.$i18n.locale },
          loading: true
        })
        this.$set(scope, 'materialNameShow', res.data[0].materialName)
        this.$set(scope, 'extMaterialModelShow', res.data[0].extMaterialModel)
      } else {
        scope.materialNameShow = null
        scope.extMaterialModelShow = null
      }
    },
    handleSuccess () {
      this.$emit('fileSuccess')
    },
    downloadTemplate (type) {
      // 1、协议头导入模板。2、协议行导入模板。3、编辑页协议行导入模板
      downloadFileLink(
        `/api-sou/jcAgreement/downloadTemplate?type=${type}`
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    },
    handleCurrentChange (pageNum) {
      this.pageInfo.pageNum = pageNum
    },
    handleSizeChange (pageSize) {
      this.pageInfo.pageNum = 1
      this.pageInfo.pageSize = pageSize
    }
  }
}
</script>
<style lang="scss" scoped>
.red {
  color: red;
}
.mg-10 {
  margin: 10px 0;
}
</style>
