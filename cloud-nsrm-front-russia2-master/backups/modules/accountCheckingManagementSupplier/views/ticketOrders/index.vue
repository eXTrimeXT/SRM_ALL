<template>
  <el-container
    class="flex-container-notab the_vendorTicketOrders_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="queryForm"
        form-label-width="120px"
        @getFormData="getQuerydata"
      />

      <main-header
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <!-- <el-button
            type="primary"
            @click="detailImport"
          >
            导出
          </el-button> -->
          <!-- <el-upload
            class="order-uploader"
            ref="upload"
            :with-credentials="true"
            :show-file-list="false"
            :headers="headers"
            withCredentials
            action="/api-sup-ce/reconciliation/penalty/saveByExcel"
            :on-error="onError"
            :on-success="onSuccess"
          >
            <el-button type="primary" slot="trigger" >Excel导入</el-button>
          </el-upload > -->
        </template>
      </main-header>
      <table-view
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="true"
        :pre-query-data="queryParam"
        url="/api-sup-ce/reconciliation/penalty/listPage"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime, adaptDictData } from '@/utils'
import { getToken } from '@/utils/auth'

export default {
  name: 'VendorTicketOrders',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  data () {
    return {
      name: 'vendorTicketOrders',
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
      ableSelectTreeNodes: [],
      rules: {
        vendorCode: [{ required: true, message: this.$t('bidMod.msgDictCode') }], // 请输入字典编码
        vendorCompanyName: [{ required: true, message: this.$t('bidMod.msgDictName') }] // 请输入字典名称
      },
      currencyList: [],
      isModify: false,
      dialogFormVisible: false,
      formLabelWidth: '100px',
      queryForm: [
        {
          prop: 'organizationId',
          label: this.$t('common.orgName'), // 采购组织
          type: 'OUorganizationSelector'
        },
        {
          prop: 'penaltyType',
          label: this.$t('accountMod.penaltyType'),
          type: 'dict',
          code: 'PENALTY_TYPE'
        }, // 罚扣款类型
        {
          prop: 'reconciliatStatus',
          label: this.$t('purSettlementMod.statementStatus'),
          type: 'dict',
          code: 'PENALTY_RECONCILIAT_STATUS'
        }, // 对账状态
        {
          prop: 'invoiceStatus',
          label: this.$t('accountMod.penaltyDeductionStatus'),
          type: 'dict',
          code: 'PENALTY_INVOICE_STATUS'
        }, // 罚扣款状态
        {
          prop: 'invoiceNumber',
          label: this.$t('accountMod.taxControlInvoiceNum')
        }, // 税控发票号
        {
          prop: 'startPenaltyTime',
          label: this.$t('accountMod.startPenaltyTime'), // 罚扣款开始日期
          type: 'date' // daterange
          // name: ['startDate', 'endDate']
        },
        {
          prop: 'endPenaltyTime',
          label: this.$t('accountMod.endPenaltyTime'), // 罚扣款截至日期
          type: 'date' // daterange
          // name: ['startDate', 'endDate']
        }
      ],
      queryParam: {},
      headers: {}
    }
  },
  created () {
    this.headers = {
      Authorization: `Bearer ${getToken()}`
      // contentType: 'form-data',
    }
    this.tableHeader = [
      {
        prop: 'penaltyNumber',
        label: this.$t('accountMod.penaltyNumber'), // 罚扣款编号
        width: 110
      },
      {
        prop: 'penaltyType',
        label: this.$t('accountMod.penaltyType'), // 罚扣款类型
        width: 110,
        dataType: 'dict',
        code: 'PENALTY_TYPE'
      },
      { prop: 'penaltyCommons', label: this.$t('accountMod.penaltyCommons') }, // 罚扣描述
      { prop: 'organizationName', label: this.$t('common.orgName') }, // 采购组织
      {
        prop: 'rfqSettlementCurrency',
        label: this.$t('bid_mod.currencyName'), // 币种
        dataType: 'dict',
        code: 'currency'
      },
      {
        prop: 'penaltyAmount',
        label: this.$t('accountMod.penaltyAmount'),
        align: 'right'
      }, // 扣款金额
      {
        prop: 'penaltyTime',
        label: this.$t('accountMod.penaltyTime'), // 扣款日期
        formattor: this.formatterDate
      },
      { prop: 'invoiceNumber', label: this.$t('accountMod.invoiceNumber') }, // 发票号
      {
        prop: 'invoiceStatus',
        label: this.$t('accountMod.penaltyDeductionStatus'), // 罚扣款状态
        width: 120,
        dataType: 'dict',
        code: 'PENALTY_INVOICE_STATUS'
      },
      {
        prop: 'reconciliatStatus',
        label: this.$t('purSettlementMod.statementStatus'), // 对账状态
        dataType: 'dict',
        code: 'PENALTY_RECONCILIAT_STATUS'
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    onSuccess ({ code, message }) {
      const isError = code !== '0'
      this.$message({
        type: isError ? 'error' : 'success',
        message,
        duration: 0,
        showClose: true
      })
    },
    onError (err) {
      this.$message({
        type: 'error',
        message: err.message
      })
    },
    formatterDate (cellValue) {
      return this.$dayjs(cellValue).format('YYYY-MM-DD')
    },
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    detailImport () {},
    deleteOne () {}
  }
}
</script>
<style scoped lang="scss">
.the_vendorTicketOrders_wrapper {
  .order-uploader {
    display: inline-block;
    margin: 0 10px;
  }
}
</style>
