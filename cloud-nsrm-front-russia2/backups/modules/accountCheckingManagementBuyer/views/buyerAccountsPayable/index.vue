<template>
  <el-container
    class="flex-container-notab the_buyerAccountsPayable_wrapper"
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
        style="padding: 10px 0"
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
        :data="tableData"
        border
        :summary-method="getSummaries"
        :show-summary="showSummary"
        style="width: 100%"
        height="100%"
      >
        <!-- 采购组织 -->
        <el-table-column
          prop="organizationName"
          :label="$t('common.orgName')"
          width="180"
        />
        <!-- 业务描述 -->
        <el-table-column
          prop="businessDescription"
          :label="$t('accountMod.businessDescription')"
        />
        <!-- 单据类型 -->
        <el-table-column
          prop="billType"
          :label="$t('bidMod.billType')"
          :formatter="formatter2"
        />
        <!-- 单据编号 -->
        <el-table-column
          prop="billCode"
          :label="$t('vendorMod.reviewFormNumber')"
        />
        <!-- 业务日期 -->
        <el-table-column
          :formatter="formatterDate"
          prop="businessDate"
          :label="$t('accountMod.businessDate')"
        />
        <!-- 币种 -->
        <el-table-column
          prop="rfqSettlementCurrency"
          :label="$t('bid_mod.currencyName')"
          :formatter="formatter1"
        />
        <!-- 期初余额 -->
        <el-table-column
          align="right"
          prop="initialBalance"
          :label="$t('accountMod.initialBalance')"
        />
        <!-- 本期应付 -->
        <el-table-column
          align="right"
          prop="currentShouldPay"
          :label="$t('accountMod.currentShouldPay')"
        />
        <!-- 本期付款 -->
        <el-table-column
          align="right"
          prop="currentPay"
          :label="$t('accountMod.currentPay')"
        />
        <!-- 本期冲销额 -->
        <el-table-column
          align="right"
          prop="chargeAgainst"
          :label="$t('accountMod.currentWriteOffAmount')"
        />
        <!-- 期末余额 -->
        <el-table-column
          align="right"
          prop="endingBalance"
          :label="$t('accountMod.endingBalance')"
        />
      </el-table>
    </el-main>
    <el-footer class="page-bar">
      <el-row type="flex">
        <el-col>
          <c-pagination
            ref="queryPagination"
            style="margin: 0"
            class="c-query-table-pagination"
            :total="total"
            :page-num="currentPage"
            layout="total, prev, pager, next"
            :page-size="pageSize"
            @current-change="handleCurrentChange"
            @size-change="changeCurrentSize"
          />
        </el-col>
      </el-row>
    </el-footer>
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
import CPagination from 'lib@/components/c-pagination'

export default {
  name: 'BuyerAccountsPayable',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    MImport,
    CPagination
  },
  data () {
    return {
      name: 'buyerAccountsPayable',
      reviewFormNumber: '',
      total: 15,
      iModal: {
        title: this.$t('common.excelImport'),
        upLoadUrl:
          '/api-sup-ce/reconciliation/accountsPayable/saveByExcel'
      },
      extraData: {
        fileModular: 'sup-ce',
        fileFunction: 'buyerAccountsPayable',
        fileType: 'excel'
      },
      gridData: [],
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      currentPage: 1,
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
        ],
        vendorCompanyName: [
          { required: true, message: this.$t('bidMod.msgDictName') }
        ]
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
        // {
        //   prop: 'billCode',
        //   label: '单据编号'
        // },
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
      showSummary: false,
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
        prop: 'purOrg',
        label: this.$t('accountMod.penaltyNumber'),
        width: 100
      }, // 罚扣款编号
      {
        prop: 'deliveryNum',
        label: this.$t('accountMod.penaltyType'),
        width: 100
      }, // 罚扣款类型
      {
        prop: 'billNum',
        label: this.$t('accountMod.penaltyCommons'),
        width: 100
      }, // 罚扣描述
      {
        prop: '采购组织',
        label: this.$t('orderMod.buyerOrderSynergy.orderNum'),
        width: 110
      }, // 订单数量
      {
        prop: 'receiveAmount',
        label: this.$t('bid_mod.currencyName'),
        width: 110
      }, // 币种
      {
        prop: 'badAmount',
        label: this.$t('accountMod.penaltyAmount'),
        width: 100
      }, // 扣款金额
      {
        prop: 'badReason',
        label: this.$t('accountMod.penaltyTime'),
        width: 100
      }, // 扣款日期
      {
        prop: 'differAmount',
        label: this.$t('accountMod.invoiceNumber'),
        width: 100
      }, // 发票号
      { prop: 'itemCode', label: this.$t('common.status'), width: 100 } // 状态
    ]
  },
  mounted () {
    this.queryList()
  },
  methods: {
    changeCurrentSize (currentSize) {
      this.pageSize = currentSize
      this.$nextTick(() => this.queryList())
    },
    downloadTemplate () {
      downloadFileLink(
        '/api-file/template/supcooperate/应付款明细模板.xls',
        this.$t('accountMod.accountPayableDetailTemp')
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },
    handleSuccess () {
      this.getQuerydata()
    },
    formatter1 (row, column, cellValue, index) {
      return cellValue ? this.$getDictLabel('currency', cellValue) : cellValue
    },
    formatter2 (row, column, cellValue, index) {
      return cellValue ? this.$getDictLabel('ACCOUNT_PAYABLE_BILL_TYPE', cellValue) : cellValue
    },
    handleCurrentChange (val) {
      this.currentPage = val
      this.$nextTick(() => this.queryList())
      console.log(`当前页: ${val}`)
    },
    queryList () {
      this.$api.pur.accountsPayableList({
        pageNum: this.currentPage,
        pageSize: this.pageSize,
        ...this.queryParam
      }).then(res => {
        this.showSummary = !!this.queryParam.organizationId
        const { total, list } = res.data
        this.tableData = list.filter(i => i.accountsPayableId)
        this.total = total
      })
    },
    formatterDate (row, column, cellValue, index) {
      return this.$dayjs(cellValue).format('YYYY-MM-DD')
    },
    getSummaries (param) {
      const { columns, data } = param
      const sums = []
      columns.forEach((column, index) => {
        if (index === 0) {
          sums[index] =
            `${data[0] ? data[0][column.property] : ''}` +
            this.$t('accountMod.subtotal')
          return
        }
        const notSummaries = [
          'initialBalance',
          'currentShouldPay',
          'currentPay',
          'chargeAgainst',
          'endingBalance'
        ]
        const values = data.map(item => Number(item[column.property]))
        if (
          !values.every(value => isNaN(value)) &&
          notSummaries.findIndex(i => i === column.property) > -1
        ) {
          sums[index] = values.reduce((prev, curr) => {
            const value = Number(curr)
            if (!isNaN(value)) {
              return prev + curr
            } else {
              return prev
            }
          }, 0)
          sums[index] += ''
        } else {
          sums[index] = '-'
        }
      })

      return sums
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
.the_buyerAccountsPayable_wrapper {
  padding: 0 10px;
  .order-uploader {
    display: inline-block;
    margin: 0 10px;
  }
}
</style>
