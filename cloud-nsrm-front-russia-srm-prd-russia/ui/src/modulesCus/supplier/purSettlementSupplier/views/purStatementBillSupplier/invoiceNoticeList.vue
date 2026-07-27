<template>
  <el-container
    class="flex-container the_invoiceNoticeList_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        :pre-form-obj="preFormObj"
        @getFormData="getQuerydata"
      />
      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <!-- 新增 -->
          <!-- <AuthorityButton type="primary" @click="editTab('add')">
            {{ $t("purSettlementMod.newStatement") }}
          </AuthorityButton> -->
          <!-- 导出 -->
          <ExportExcel
            type="default"
            :table-header="tableHeader"
            export-mode="front"
            :dict-codes="dictCodes"
            :page-url="queryUrl"
            :filter-params="filterParams"
            fileName="对账单导出文件.xlsx"
            timeout="1000000"
          />
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :bigData="true"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :comActive="$attrs['changeTab']"
        :url="queryUrl"
        :adeptMeiQl="true"
        @afterQuery="afterQuery"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import invoiceNoticeDetail from './invoiceNoticeDetail'
import { adaptDictData, parseTime } from '@/utils'
import ExportExcel from 'lib@/components/export-excel'

export default {
  name: 'InvoiceNoticeList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      dictCodes: {
        extStatus: 'INVOICE_NOTICE_STATUS',
        invoiceNoticeStatus: 'INVOICE_NOTICE_STATUS'
      },
      gridId: 'invoiceNoticeList',
      pageSize: 15,
      preFormObj: {},
      queryParam: {},
      filterParams: {},
      selectList: [],
      tableHeader: [],
      tableData: [],
      preArr: [
        {
          prop: 'invoiceNoticeNumber',
          label: () => this.$t('purSettlementMod.statementNumber')
        },
        {
          prop: 'invoiceNoticeStatus',
          label: () => this.$t('purSettlementMod.paymentPlanStatus'),
          type: 'dict',
          filterItem: () => ['DRAFT'],
          code: 'INVOICE_NOTICE_STATUS'
        },
        {
          prop: 'orgName',
          label: () => this.$t('quota.org')
        },
        {
          prop: 'createdFullName',
          label: () => this.$t('common.creator')
        },
        {
          prop: 'creationDate',
          label: () => this.$t('bidMod.dateCreated'),
          type: 'daterange'
        }
      ],
      queryUrl: '/api-sup-ce/api-ql/InvoiceNoticeVendor/query'
    }
  },
  watch: {
    $route: {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler (nVal) {
        const { from, funName } = this.$route.params
        if (from === 'workCount' && funName === 'purStatementBillSupplier') {
          this.preFormObj = { invoiceNoticeStatus: 'WAITING' }
          this.getQuerydata(this.preFormObj)
        }
      }
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'orgName',
        label: _this.$t('quota.org'),
        width: 120
      },
      {
        prop: 'invoiceNoticeNumber',
        label: _this.$t('purSettlementMod.statementNumber'),
        width: 120,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.editTab('view', row),
        formattor (val) {
          return val || '--'
        }
      },
      {
        prop: 'extStatus',
        label: _this.$t('purSettlementMod.paymentPlanStatus'),
        width: 100,
        dataType: 'dict',
        code: 'INVOICE_NOTICE_STATUS'
      },
      {
        prop: 'vendorCode',
        label: _this.$t('common.vendorCode'),
        width: 120
      },
      {
        prop: 'vendorName',
        label: _this.$t('bidMod.vendorName'),
        minWidth: 150
      },
      {
        prop: 'ceeaReceiveStartDate',
        label: () => this.$t('purSettlementMod.statementStartTime'),
        width: 150
      },
      {
        prop: 'ceeaReceiveEndDate',
        label: () => this.$t('purSettlementMod.statementEndTime'),
        width: 150
      },
      {
        prop: 'ceeaTaxTotalAmount',
        label: _this.$t('contractMod.totalAmountTax'),
        width: 120
      },
      {
        prop: 'currencyName',
        label: _this.$t('vendorMod.currencyCode'),
        width: 120
      },
      {
        prop: 'rejectReason',
        label: _this.$t('orderMod.refuseReason'),
        width: 120
      },
      {
        prop: 'createdFullName',
        label: _this.$t('common.creator'),
        width: 100
      },
      {
        prop: 'creationDate',
        label: _this.$t('purSettlementMod.creationDate'),
        width: 100,
        formattor: val => val ? parseTime(val, '{y}-{m}-{d}') : ''
      },
      {
        prop: 'operation',
        label: _this.$t('common.operation'),
        width: 100,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: row => this.editTab('manage', row),
            formattor: _ => this.$t('purchaseDemand.manage'),
            // 待供应商确认
            show: row => ['WAITING'].includes(row.invoiceNoticeStatus)
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata(this.preFormObj)
    })
  },
  methods: {
    afterQuery (data) {
      this.tableData = data.map(item => {
        // 对账单状态优先取【扩展状态：extStatus】
        item.invoiceNoticeStatus = item.extStatus || item.invoiceNoticeStatus
        return item
      })
    },
    getQuerydata (v) {
      // 【待供方确认、已确认、已拒绝】扩展状态 使用 extStatus 查询, 其他状态使用 deliveryNoteStatus 查询(此时 extStatus为空)
      let params = {}
      params.vendorId = { eq: this.$store.getters.user.companyId }
      params.invoiceNoticeStatus = { notIn: ['DRAFT'] }
      const { invoiceNoticeNumber, invoiceNoticeStatus, orgName, createdFullName, creationDate } = v || {}
      if (invoiceNoticeNumber) {
        params.invoiceNoticeNumber = { contains: invoiceNoticeNumber }
      }
      if (invoiceNoticeStatus && ['WAITING', 'CONFIRM', 'REFUSE'].includes(invoiceNoticeStatus)) {
        params.extStatus = { eq: invoiceNoticeStatus }
      } else if (invoiceNoticeStatus) {
        params.invoiceNoticeStatus = { eq: invoiceNoticeStatus }
        params.extStatus = { isNull: true }
      }
      if (orgName) {
        params.orgName = { contains: orgName }
      }
      if (createdFullName) {
        params.createdFullName = { contains: createdFullName }
      }
      if (creationDate) {
        params.creationDate = { between: creationDate }
      }

      this.queryParam = {
        type: 'InvoiceNoticeVendor',
        action: 'query',
        payload: {
          filter: { ...params },
          page: {
            pageNum: 1,
            pageSize: 15,
            sort: 'lastUpdateDate desc'
          }
        },
        query: { '*': {} },
        lang: 'zh-cn',
        tree: true
      }
      this.filterParams = { meiqlPayload: this.queryParam }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    editTab (type, row) {
      let tab = {}
      if (type === 'add') {
        // 新增
        tab = {
          component: invoiceNoticeDetail,
          params: {
            flag: 'add',
            tabName: 'invoiceNoticeDetail'
          },
          title: this.$t('purSettlementMod.newStatement'),
          name: 'invoiceNoticeDetail'
        }
      } else {
        // 修改
        tab = {
          component: invoiceNoticeDetail,
          params: {
            flag: type,
            invoiceNoticeId: row.invoiceNoticeId,
            tabName: 'invoiceNoticeDetail' + row.invoiceNoticeNumber
          },
          title: row.invoiceNoticeNumber,
          name: 'invoiceNoticeDetail' + row.invoiceNoticeNumber
        }
      }
      this.$emit('tab-add', tab)
    }
  }
}
</script>
<style scoped lang="scss">
</style>
