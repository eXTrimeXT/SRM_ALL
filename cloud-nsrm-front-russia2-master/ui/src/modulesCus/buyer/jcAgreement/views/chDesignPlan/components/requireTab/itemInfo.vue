<template>
  <div class="wrapper">
    <div class="btns mb-10">
      <ExportExcel
        v-if="form.designId"
        page-url="/api-sou/design/plan/getPullOrder"
        :filter-params="filterParams"
        :dict-codes="dictCodes"
        :table-header="tableHeader"
        export-mode="front"
        type="primary"
      />
      <MImport
        v-if="!readonly"
        ref="import"
        :title="$t('common.import')"
        :up-load-url="uploadUrl"
        type="default"
        :extra-data="extraData"
        @downloadTemplate="downloadTemplate"
        @handleSuccess="handleSuccess"
      />
      <!-- 合并数据 -->
      <AuthorityButton v-if="!readonly" code="chDesignPlan:merge" :loading="mergeLoading" @click="mergeData">
        {{ $t('cusEntry.supplement20250121.mergeData') }}
      </AuthorityButton>
    </div>
    <el-table
      border
      stripe
      :data="tableData.slice((pageInfo.pageNum-1)*pageInfo.pageSize,pageInfo.pageNum*pageInfo.pageSize)"
      max-height="250px"
    >
    <!-- 供货范围 -->
      <el-table-column
        prop="areaCode"
        :label="$t('cusEntry.centralizedPurchase.supplyScope')"
        showOverflowTooltip
        min-width="130"
        :formatter="(row,column,cellValue) => $getDictLabel('REGION',cellValue)"
      />
      <!-- 物资编码 -->
      <el-table-column
        prop="materialCode"
        :label="$t('common.materialCode')"
        showOverflowTooltip
        min-width="130"
      />
      <!-- 物资名称 -->
      <el-table-column
        prop="materialName"
        :label="$t('common.materialName')"
        showOverflowTooltip
        min-width="130"
      />
      <!-- 规格型号 -->
      <el-table-column
        prop="model"
        :label="$t('common.specification')"
        showOverflowTooltip
        min-width="130"
      />
      <!-- 计量单位 -->
      <el-table-column
        prop="unitCode"
        :label="$t('cusEntry.competition.measurementUnit')"
        showOverflowTooltip
        min-width="130"
        :formatter="(row,column,cellValue) => $getDictLabel('unit',cellValue)"
      />
      <!-- 数量 -->
      <el-table-column
        prop="orderNum"
        :label="$t('competition.quantity')"
        showOverflowTooltip
        min-width="100"
      />
      <!-- 品牌 -->
      <el-table-column
        prop="brand"
        :label="$t('dataConfMod.band')"
        showOverflowTooltip
        min-width="100"
      />
      <!-- 未税单价 -->
      <el-table-column
        prop="priceTax"
        :label="$t('orderMod.untaxedPrice')"
        showOverflowTooltip
        min-width="130"
      />
      <!-- 税率% -->
      <el-table-column
        prop="taxRate"
        :label="$t('bidMod.taxRatePer_price')"
        showOverflowTooltip
        min-width="100"
      />
      <!-- 含税单价 -->
      <el-table-column
        prop="ratePrice"
        :label="$t('bidMod.unitPrice_price')"
        showOverflowTooltip
        min-width="130"
      />
      <!-- 金额 -->
      <el-table-column
        prop="moneyAmount"
        :label="$t('orderMod.amount')"
        showOverflowTooltip
        min-width="130"
      />
      <!-- 价税合计 -->
      <el-table-column
        prop="priceTotal"
        :label="$t('cusEntry.inq.totalPriceAndTax')"
        showOverflowTooltip
        min-width="130"
      />
      <!-- 来源 -->
      <el-table-column
        prop="dataSource"
        :label="$t('cusEntry.inq.dataSource')"
        showOverflowTooltip
        min-width="130"
      />
      <el-table-column
        v-if="!['APPROVING', 'APPROVED'].includes(form.status)"
        prop="operation"
        :label="$t('common.operation')"
        width="60"
        fixed="right"
      >
        <template slot-scope="scope">
          <el-button
            type="text"
            @click="deleleRow(scope.row)"
          >
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <CPagination
      ref="queryPagination"
      class="c-query-table-pagination"
      style="padding-bottom:4px"
      :total="tableData.length"
      :page-num="pageInfo.pageNum"
      :page-size="pageInfo.pageSize"
      @current-change="handleCurrentChange"
      @size-change="handleSizeChange"
    />
  </div>
</template>
<script>
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import { designPlanHttp } from 'modcb@/jcAgreement/api'
import InquiryDetail from 'modcb@/centralizedPurchase/views/inquiry/inquiry-detail'
import CPagination from 'lib@/components/c-pagination'
import ExportExcel from 'lib@/components/export-excel'

export default {
  components: {
    MImport,
    CPagination,
    ExportExcel
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
    form: {
      type: Object,
      default: () => {}
    }
  },
  emits: ['update:value'],
  data () {
    return {
      exportLoading: false,
      mergeLoading: false,
      inqLoading: false,
      pageInfo: {
        pageNum: 1,
        pageSize: 15,
        total: 0
      },
      dictCodes: {
        unitCode: 'unit',
        areaCode: 'REGION'
      }
    }
  },
  computed: {
    tableData: {
      get () {
        return this.value
      },
      set (value) {
        this.$emit('update:value', value)
      }
    },
    uploadUrl () {
      return '/api-sou/design/plan/importReqInfo'
    },
    extraData () {
      return {
        fileModular: 'sou',
        fileFunction: 'chDesignPlan',
        fileType: 'excel',
        designId: this.form.designId
      }
    },
    filterParams () {
      return {
        type: 3,
        designId: this.form.designId
      }
    },
    tableHeader () {
      return [
        {
          prop: 'areaCode',
          label: this.$t('cusEntry.centralizedPurchase.supplyScope'), // '供货范围'
        },
        {
          prop: 'materialCode',
          label: this.$t('common.materialCode'), // '物资编码'
        },
        {
          prop: 'materialName',
          label: this.$t('common.materialName'), // '物资名称'
        },
        {
          prop: 'model',
          label: this.$t('common.specification'), // '规格型号'
        },
        {
          prop: 'unitCode',
          label: this.$t('cusEntry.competition.measurementUnit'), // '计量单位'
        },
        {
          prop: 'orderNum',
          label: this.$t('competition.quantity'), // '数量'
        },
        {
          prop: 'brand',
          label: this.$t('dataConfMod.band'), // '品牌'
        },
        {
          prop: 'priceTax',
          label: this.$t('orderMod.untaxedPrice'), // '未税单价'
        },
        {
          prop: 'taxRate',
          label: this.$t('bidMod.taxRatePer_price'), // '税率%'
        },
        {
          prop: 'ratePrice',
          label: this.$t('bidMod.unitPrice_price'), // '含税单价'
        },
        {
          prop: 'moneyAmount',
          label: this.$t('orderMod.amount'), // '金额'
        },
        {
          prop: 'priceTotal',
          label: this.$t('cusEntry.inq.totalPriceAndTax'), // '价税合计'
        },
        {
          prop: 'dataSource',
          label: this.$t('cusEntry.inq.dataSource'), // '来源'
        }
      ]
    }
  },
  methods: {
    // 删除行
    async deleleRow (row) {
      const res = await designPlanHttp.deleteDetail({ yearId: row.yearId })
      if (res.data) {
        this.$message.success(this.$t('common.successDelete'))
      } else {
        return false
      }
      this.getPullOrder(3).then(data => {
        this.tableData = data
      })
    },
    addNew () {
      this.tableData.push({})
    },
    deleteRow (index) {
      this.tableData.splice(index, 1)
    },
    // 导出
    handleExport () {

    },
    handleSuccess (data) {
      this.$emit('file-success')
    },
    downloadTemplate (type) {
      downloadFileLink(
        '/api-sou/design/plan/downloadReqTemplate'
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    },
    // 合并数据据
    async mergeData () {
      if (!this.form.designId) return
      const response = await designPlanHttp.mergeOrderData({ designId: this.form.designId })
      if (response) {
        this.tableData = response.data || []
      }
      this.$emit('merge-data')
    },
    // 获取物料数据
    // type:1、上年订单数据。2、上上年订单数据。3、合并的数据
    getPullOrder (type, designId, pageNum, pageSize) {
      return new Promise(async (resolve) => {
        const response = await designPlanHttp.getPullOrder({
          type,
          designId: this.form.designId,
          pageNum: 1,
          pageSize: 1000000
        })
        if (response && response.data) {
          let list = response.data.list || []
          resolve(list)
        }
      })
    },
    handleCurrentChange (pageNum) {
      this.pageInfo.pageNum = pageNum
    },
    handleSizeChange (pageSize) {
      this.pageInfo.pageNum = 1
      this.pageInfo.pageSize = pageSize
    },
    // 发起询价
    async createInquiry () {
      if (!this.form.designId) return
      const response = await this.$http({
        url: `/api-sou/design/plan/createPurInq/${this.form.designId}`,
        method: 'POST',
        data: {},
        loading: true
      })
      if (response) {
        let result = response.data || {}
        this.$emit('tab-add', {
          name: `inquiryDetail${result.souNo}`,
          component: InquiryDetail,
          params: {
            tab: 'edit',
            tabName: `inquiryDetail${result.souNo}`,
            row: {
              projectId: result.projectId,
              souNo: result.souNo
            },
            readOnly: false
          },
          title: result.souNo
        })
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.mt-10 {
  margin-top: 10px;
}
.mb-10 {
  margin-bottom: 10px;
}
</style>
