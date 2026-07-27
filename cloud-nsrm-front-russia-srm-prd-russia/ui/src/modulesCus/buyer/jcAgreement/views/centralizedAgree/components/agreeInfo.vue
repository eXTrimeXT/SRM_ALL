<template>
  <div class="wrapper">
    <div class="btns">
      <el-button v-if="!readonly && !isChange" type="primary" @click="add">
        新增
      </el-button>
      <MImport
        v-if="!readonly && agreementId && !isChange"
        ref="import"
        title="导入行信息"
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
      <el-table-column
        type="index"
        label="物料行号"
        width="80"
      />

      <el-table-column
        prop="materialCode"
        label="物料编码"
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

      <el-table-column
        prop="materialName"
        label="物料名称"
        minWidth="130"
        showOverflowTooltip
      />

      <el-table-column
        prop="goodsTypeName"
        label="商品分类"
        minWidth="130"
        showOverflowTooltip
      />

      <el-table-column
        prop="standards"
        label="规格"
        minWidth="130"
        showOverflowTooltip
      />

      <el-table-column
        prop="unit"
        label="单位"
        minWidth="130"
        :formatter="(row,column,cellValue) => $getDictLabel('unit', cellValue)"
      />

      <el-table-column
        prop="brand"
        label="品牌"
        minWidth="130"
      >
        <template v-slot="scope">
          <el-input v-if="!readonly && !isChange" v-model="scope.row.brand" />
          <span v-else>{{ scope.row.brand }}</span>
        </template>
      </el-table-column>

      <!-- <el-table-column
        prop="expectNum"
        label="预计数量"
        minWidth="130"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <el-input v-if="!readonly" v-model="scope.row.expectNum" v-input-format="{type:'integer',negative:false}" />
          <span v-else>{{ scope.row.expectNum }}</span>
        </template>
      </el-table-column> -->

      <el-table-column
        prop="taxRate"
        label="税率%"
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

      <el-table-column
        prop="isTieredPricing"
        label="是否阶梯价"
        minWidth="100"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <el-checkbox v-model="scope.row.isTieredPricing" true-label="1" false-label="0" :disabled="readonly" />
        </template>
      </el-table-column>

      <el-table-column
        prop="ladderDetail"
        label="阶梯价"
        minWidth="130"
      >
        <template v-slot="scope">
          <el-button :disabled="scope.row.isTieredPricing != '1'" type="text" @click="ladderClick(scope.row,scope.$index)">
            阶梯价
          </el-button>
        </template>
      </el-table-column>

      <el-table-column
        prop="priceTax"
        label="未税单价"
        minWidth="130"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <el-input v-if="!readonly" v-model="scope.row.priceTax" v-input-format="{type: 'float', digits: 2, negative: false}" @change="(val) => priceTaxChange(val,scope)" />
          <span v-else>{{ scope.row.priceTax }}</span>
        </template>
      </el-table-column>

      <!-- 保留4位小数 -->
      <el-table-column
        prop="ratePrice"
        label="含税单价"
        minWidth="130"
      />

      <el-table-column
        prop="referencePrice"
        label="参考价"
        minWidth="130"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <el-input v-if="!readonly" v-model="scope.row.referencePrice" v-input-format="{type: 'float', digits: 4, negative: false}" />
          <span v-else>{{ scope.row.referencePrice }}</span>
        </template>
      </el-table-column>

      <el-table-column
        prop="leadTime"
        label="交货周期（自然日）"
        minWidth="150"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <el-input v-if="!readonly" v-model="scope.row.leadTime" v-input-format="{type: 'integer', negative: false}" />
          <span v-else>{{ scope.row.leadTime }}</span>
        </template>
      </el-table-column>

      <el-table-column
        prop="sellByDate"
        label="质保期（自然日）"
        minWidth="130"
      >
        <template v-slot="scope">
          <el-input v-if="!readonly" v-model="scope.row.sellByDate" v-input-format="{type: 'integer', negative: false}" />
          <span v-else>{{ scope.row.sellByDate }}</span>
        </template>
      </el-table-column>

      <!-- 默认1 -->
      <el-table-column
        prop="startNum"
        label="起订量"
        minWidth="130"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <el-input v-if="!readonly" v-model="scope.row.startNum" v-input-format="{type: 'integer', negative: false}" />
          <span v-else>{{ scope.row.startNum }}</span>
        </template>
      </el-table-column>

      <el-table-column
        prop="multipleStartNum"
        label="整倍起售数量"
        minWidth="130"
      >
        <template v-slot="scope">
          <el-input v-if="!readonly" v-model="scope.row.multipleStartNum" v-input-format="{type: 'integer', negative: false}" />
          <span v-else>{{ scope.row.multipleStartNum }}</span>
        </template>
      </el-table-column>

      <el-table-column
        prop="agreementDes"
        label="协议行说明"
        minWidth="130"
      >
        <template v-slot="scope">
          <el-input v-if="!readonly" v-model="scope.row.agreementDes" />
          <span v-else>{{ scope.row.agreementDes }}</span>
        </template>
      </el-table-column>

      <el-table-column
        v-if="!readonly"
        prop="operation"
        label="操作"
        width="100"
        fixed="right"
      >
        <template v-slot="scope">
          <el-button
            v-if="!isChange"
            type="text"
            @click="deleteRow(scope)"
          >
            删除
          </el-button>
          <el-button
            v-if="agreementStatus === 'EXECUTING'"
            type="text"
            @click="abandon(scope.row)"
          >
            废弃
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
        unit: 'unit'
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
          label: '物料编码'
        },
        {
          prop: 'materialName',
          label: '物料名称'
        },
        {
          prop: 'goodsTypeName',
          label: '商品分类'
        },
        {
          prop: 'standards',
          label: '规格'
        },
        {
          prop: 'unit',
          label: '单位'
        },
        {
          prop: 'brand',
          label: '品牌'
        },
        {
          prop: 'isTieredPricing',
          label: '是否阶梯价'
        },
        {
          prop: 'priceTax',
          label: '未税单价'
        },
        {
          prop: 'taxRate',
          label: '税率%'
        },
        {
          prop: 'ratePrice',
          label: '含税单价'
        },
        {
          prop: 'referencePrice',
          label: '参考价'
        },
        {
          prop: 'leadTime',
          label: '交货周期（自然日）'
        },
        {
          prop: 'sellByDate',
          label: '质保期（自然日）'
        },
        {
          prop: 'startNum',
          label: '起订量'
        },
        {
          prop: 'multipleStartNum',
          label: '整倍起售数量'
        },
        {
          prop: 'agreementDes',
          label: '协议行说明'
        }
      ]
    }
  },
  methods: {
    // 废弃
    abandon (row) {
      this.$confirm('是否废弃?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
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
        taxRate: null,
        taxKey: null,
        ratePrice: null,
        referencePrice: null,
        leadTime: null,
        sellByDate: null,
        startNum: 1,
        multipleStartNum: null,
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
      ratePrice = Math.round(ratePrice * 10000) / 10000
      scope.row.ratePrice = ratePrice
      this.tableData[scope.$index] = scope.row
    },
    deleteRow (scope) {
      this.tableData.splice(scope.$index, 1)
    },
    ladderClick (row, index) {
      if (!row.taxRate && row.taxRate !== 0) {
        this.$message.warning('请选择税率')
        return
      }
      this.curIndex = index
      this.curRow = row
      this.sccSouTieredPricingList = row.sccSouTieredPricingList || []
      this.sccSouTieredPricingList.forEach(item => {
        if (item.priceTax && (row.taxRate || row.taxRate === 0)) {
          item.ratePrice = Math.round(item.priceTax * (1 + row.taxRate / 100) * 10000) / 10000
        }
      })
      this.ladderDialogVisible = true
    },
    ladderDialogConfirm (val) {
      let newRow = { ...this.curRow, sccSouTieredPricingList: val }
      this.tableData.splice(this.curIndex, 1, newRow)
      this.ladderDialogVisible = false
    },
    getItemObj (val, scope) {
      console.log('val', val)
      scope.materialId = val ? val.materialId : ''
      scope.materialCode = val ? val.materialCode : ''
      scope.materialName = val ? val.materialName : ''
      scope.goodsTypeName = val ? val.categoryName : ''
      scope.goodsTypeId = val ? val.categoryId : ''
      scope.goodsTypeCode = val ? val.categoryCode : ''
      scope.standards = val ? val.materialType : ''
      scope.unit = val ? val.unit : ''
      scope.unitName = val ? val.unitName : ''
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
