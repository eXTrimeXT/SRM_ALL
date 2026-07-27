<template>
  <el-container
    class="flex-container-notab the_vendorStatementTracking_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="queryForm"
        @getFormData="getQuerydata"
      />

      <main-header
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <m-import
            ref="import"
            style="display: inline-block;margin-left: 15px;"
            :title="iModal.title"
            :up-load-url="iModal.upLoadUrl"
            :extra-data="extraData"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
        </template>
      </main-header>
      <el-table
        :ref="gridId"
        height="100%"
        :data="tableData"
        border
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      >
        >
        <el-table-column type="selection" />
        <!-- 基本信息 -->
        <el-table-column
          prop="base"
          :label="$t('vendorMod.baseInfo')"
        >
          <!-- 单据类型 -->
          <el-table-column
            :label="$t('bidMod.billType')"
            prop="billType"
            min-width="120px"
            :formatter="formatter2"
          />
          <!-- 单据编码 -->
          <el-table-column
            :label="$t('bidMod.billCode')"
            prop="billCode"
          />
          <!-- 业务日期 -->
          <el-table-column
            :label="$t('accountMod.businessDate')"
            prop="businessDate"
            min-width="120px"
            :formatter="formatterDate"
          />
          <!-- 币种 -->
          <el-table-column
            :label="$t('bidMod.allAurrency')"
            prop="rfqSettlementCurrency"
            :formatter="formatter"
          />
          <!-- 价税合计 -->
          <el-table-column
            :label="$t('accountMod.priceTaxSum')"
            prop="priceTaxSum"
            align="right"
          />
          <!-- 订单编号 -->
          <el-table-column
            :label="$t('logisticsMod.orderNum')"
            prop="orderNumber"
            min-width="120px"
          />
          <!-- 入库单编号 -->
          <el-table-column
            :label="$t('accountMod.warehouseReceiptNum')"
            min-width="120px"
            prop="warehouseReceiptNumber"
          />
          <!-- 退货单编号 -->
          <el-table-column
            :label="$t('accountMod.returnOrderNum')"
            min-width="120px"
            prop="returnOrderNumber"
          />
        </el-table-column>
        <!-- 开票情况 -->
        <el-table-column
          prop="tikect"
          :label="$t('accountMod.invoiceSituation')"
        >
          <!-- 发票单据号 -->
          <el-table-column
            :label="$t('accountMod.invoiceNumber1')"
            min-width="120px"
            prop="invoiceNumber"
          />
          <!-- 发票数量 -->
          <el-table-column
            :label="$t('accountMod.invoicesNum')"
            prop="invoiceNum"
            align="right"
          />
          <!-- 发票金额 -->
          <el-table-column
            :label="$t('accountMod.invoiceAmount')"
            prop="invoiceAmount"
            align="right"
          />
        </el-table-column>
        <!-- 付款情况 -->
        <el-table-column
          prop="pay"
          :label="$t('accountMod.paymentSitustion')"
        >
          <!-- 付款单编号 -->
          <el-table-column
            :label="$t('accountMod.paymentNoteNumber')"
            min-width="120px"
            prop="paymentNoteNumber"
          />
          <!-- 付款金额 -->
          <el-table-column
            :label="$t('accountMod.paymentAmount')"
            prop="paymentAmount"
            align="right"
          />
        </el-table-column>
      </el-table>
      <el-row
        type="flex"
        justify="center"
      >
        <el-pagination
          :current-page.sync="currentPage"
          :page-size="15"
          layout="total, prev, pager, next"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </el-row>
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime, adaptDictData } from '@/utils'
import { getToken } from '@/utils/auth'
import { getAllPurCurrency } from '@/api/common'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'

export default {
  name: 'VendorStatementTracking',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    MImport
  },
  data () {
    return {
      iModal: {
        title: this.$t('common.excelImport'), // Excel导入
        upLoadUrl:
          '/api-sup-ce/reconciliation/reconciliationTrack/saveByExcel'
      },
      extraData: {
        fileModular: 'sup-ce',
        fileFunction: 'vendorStatementTracking',
        fileType: 'excel'
      },
      currentPage: 1,
      currencyList: [],
      total: 100,
      name: 'vendorStatementTracking',
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      currentRow: null,
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      statusList: [],
      form: {
        id: '',
        vendorCode: '',
        vendorCompanyName: '',
        reviewFormNumber: '',
        enabled: ''
      },
      rules: {
        vendorCode: [
          { required: true, message: this.$t('bidMod.msgDictCode') }
        ], // 请输入字典编码
        vendorCompanyName: [
          { required: true, message: this.$t('bidMod.msgDictName') }
        ] // 请输入字典名称
      },
      isModify: false,
      headers: {},
      dialogFormVisible: false,
      formLabelWidth: '100px',
      queryForm: [
        {
          prop: 'organizationId',
          label: this.$t('common.orgName'), // 采购组织
          type: 'OUorganizationSelector'
        },
        {
          prop: 'billCode',
          label: this.$t('vendorMod.reviewFormNumber') // 单据编号
        },
        {
          prop: 'startBusinessDate',
          label: this.$t('accountMod.startBusinessDate'), // 业务开始日期
          type: 'date'
        },
        {
          prop: 'endBusinessDate',
          label: this.$t('accountMod.endBusinessDate'), // 业务截止日期
          type: 'date'
        }
      ],
      queryParam: {}
    }
  },
  created () {
    this.headers = {
      Authorization: `Bearer ${getToken()}`
      // contentType: 'form-data',
    }
  },
  mounted () {
    this.queryList()
    // 获取所有币种
    getAllPurCurrency().then(res => {
      this.currencyList = adaptDictData(res.data, 'currency')
    })
  },
  methods: {
    formatter2 (row, column, cellValue, index) {
      return cellValue ? this.$getDictLabel('RECONCILIATION_TRACK_BILL_TYPE', cellValue) : cellValue
    },
    downloadTemplate () {
      downloadFileLink(
        '/api-file/template/supcooperate/对账单跟踪表模板.xls',
        this.$t('accountMod.statementTrackTemp')
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },
    handleSuccess () {
      this.getQuerydata()
    },
    formatterDate (row, column, cellValue, index) {
      return this.$dayjs(cellValue).format('YYYY-MM-DD')
    },
    normalizer (node) {
      const isDisabled =
        this.ableSelectTreeNodes.findIndex(j => j === node.organizationId) ===
        -1
      const result = {
        id: node.organizationId,
        label: node.organizationName,
        isDisabled
      }
      if (node && (node.childOrganRelation || []).length) {
        result.children = node.childOrganRelation
      }
      return result
    },
    formatter (row, column, cellValue, index) {
      if (this.currencyList && this.currencyList.length) {
        const dict = this.currencyList.find(i => i.value === cellValue)
        return dict ? dict.label : cellValue
      }
    },
    handleSizeChange (val) {
      console.log(`每页 ${val} 条`)
    },
    handleCurrentChange (val) {
      this.currentPage = val
      this.queryList()
      console.log(`当前页: ${val}`)
    },
    queryList () {
      this.$api.pur.reconciliationTrackList({
        pageNum: this.currentPage,
        pageSize: this.pageSize,
        ...this.queryParam
      }).then(res => {
        const { list: List, total } = res.data
        const list = List.reduce((res, item) => {
          const {
            reconciliationInvoices = [],
            reconciliationPayments = [],
            ...rest
          } = item
          const max = Math.max(
            reconciliationInvoices.length,
            reconciliationPayments.length
          )
          const prototypes = {
            ...rest,
            ...reconciliationInvoices[0],
            ...reconciliationPayments[0],
            id: rest.reconciliationTrackId,
            children: []
          }
          for (let i = 0; i < max; i++) {
            const child = {
              ...reconciliationInvoices[i],
              ...reconciliationPayments[i]
            }
            child.id = `${child.invoiceId}_${child.paymentId}`
            prototypes.children.push(child)
          }
          const result = res.concat(prototypes)
          return result
        }, [])
        this.tableData = list
        this.total = total
      })
    },
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.queryList()
      })
    }
  }
}
</script>
<style scoped lang="scss">
.the_vendorStatementTracking_wrapper {
  .order-uploader {
    display: inline-block;
    margin: 0 10px;
  }
  .block {
    display: flex;
    justify-content: center;
  }
}
</style>
<style>
.the_vendorStatementTracking_wrapper .el-table th > .cell {
  display: flex;
  justify-content: center;
}
</style>
