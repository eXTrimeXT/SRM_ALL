<template>
  <el-container
    class="flex-container-notab the_buyerTicketOrders_wrapper"
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
            style="display: inline-block;margin-left: 10px;"
            :title="iModal.title"
            :up-load-url="iModal.upLoadUrl"
            :extra-data="extraData"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
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
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'

export default {
  name: 'BuyerTicketOrders',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    MImport
  },
  data () {
    return {
      name: 'buyerTicketOrders',
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'list',
      extraData: {
        fileModular: 'sup-ce',
        fileFunction: 'buyerTicketOrders',
        fileType: 'excel'
      },
      iModal: {
        title: this.$t('common.excelImport'), // Excel导入
        upLoadUrl: '/api-sup-ce/reconciliation/penalty/saveByExcel'
      },
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
        width: 130,
        dataType: 'dict',
        code: 'PENALTY_TYPE'
      },
      { prop: 'penaltyCommons', label: this.$t('accountMod.penaltyCommons') }, // 罚扣描述
      { prop: 'organizationName', label: this.$t('common.orgName') }, // 采购组织
      {
        prop: 'rfqSettlementCurrency',
        label: this.$t('bidMod.allAurrency'), // 币种
        width: 90,
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
        label: this.$t('accountMod.penaltyTime'),
        formattor: this.formatterDate
      }, // 扣款日期
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
    downloadTemplate () {
      downloadFileLink(
        '/api-file/template/supcooperate/罚扣款单模板.xls',
        this.$t('accountMod.fineDeductionSlipTemp')
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },
    handleSuccess () {
      this.getQuerydata()
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
    }
  }
}
</script>
<style scoped lang="scss">
.the_buyerTicketOrders_wrapper {
  .order-uploader {
    display: inline-block;
    margin: 0 10px;
  }
}
</style>
