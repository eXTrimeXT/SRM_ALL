<template>
  <el-container
    class="flex-container the_onlineInvoice_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="preArr"
        form-label-width="120px"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <main-header
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            code="pm:onlineInvoice:export"
            type="primary"
            @click="exportList"
          >
            {{ $t("common.export") }}
          </AuthorityButton>
        </template>
      </main-header>
      <table-view
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :comActive="$attrs['changeTab']"
        url="/api-sup-ce/ps/invoice/onlineInvoice/vendorListPage"
      />
    </el-main>
  </el-container>
</template>
<script>
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import CPagination from 'lib@/components/c-pagination'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import onlineInvoiceDetail from './onlineInvoiceDetail'
import { parseTime } from '@/utils'
import { downloadFileLink, downloadFileLinkByPost } from 'lib@/utils/file'

export default {
  name: 'OnlineInvoiceList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    CPagination,
    OrganizationSelector,
    QuickSearch
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      name: '',
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      tableName: 'onlineInvoiceList',
      gridId: 'onlineInvoiceList',
      selectList: [],
      currentRow: null,
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      transTypeList: [], // 事物类型list
      taxList: [],
      currencyList: [],
      preArr: [
        {
          prop: 'onlineInvoiceNum',
          label: this.$t('purSettlementMod.onlineInvoiceNum')
        },
        {
          prop: 'startDate',
          label: this.$t('paymentType.paymentDateDueFrom'),
          type: 'date'
        },
        {
          prop: 'endDate',
          label: this.$t('paymentType.paymentDateDueTo'),
          type: 'date'
        },
        {
          prop: 'orgIds',
          label: this.$t('quota.org'),
          type: 'OUorganizationSelector',
          multiple: true
        },
        {
          prop: 'vendorName',
          label: this.$t('common.vendorName'),
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_display'
        },
        {
          prop: 'costType',
          label: this.$t('purchaseDemand.vendorSite'),
          type: 'dict',
          code: 'COST_TYPE'

        },
        {
          prop: 'invoiceStatus',
          label: this.$t('purSettlementMod.invoiceStatus'),
          type: 'dict',
          code: 'INVOICE_STATUS'
        },
        {
          prop: 'importStatus',
          label: this.$t('purSettlementMod.importStatus'),
          type: 'dict',
          code: 'INVOICE_IMPORT_STATUS'
        },
        {
          prop: 'businessType',
          label: this.$t('dataConfMod.businessType'),
          type: 'dict',
          code: 'BUSINESS_TYPE'
        },
        {
          prop: 'taxInvoiceNum',
          label: this.$t('purSettlementMod.taxationInvoiceNum')
        },
        { prop: 'boeNo', label: this.$t('purSettlementMod.boeNo') },
        {
          prop: 'payMethod',
          label: this.$t('paymentType.paymentWay'),
          type: 'dict',
          code: 'PAYMENT_MODE'
        }
      ],
      queryParam: {},
      curRole: this.$store.getters.userType, // VENDOR BUYER curRole==='VENDOR'
      rolePermissions: '', // 操作角色 Buyer 采购员\ AccountSpecialist 财务专员
      userInfo: this.$store.getters.userInfo,
      selectionItem: [],
      displayMaterialItem: [],
      parentOrgTableDataPage: {
        total: 0,
        pageNum: 1,
        pageSize: 20
      },
      dialogFormVisible: false,
      parentOrgQueryForm: {
        pageNum: 1,
        pageSize: 10
      },
      filterForm: {
        orgId: null,
        organizationId: null,
        materialCode: null,
        materialName: null,
        orgName: null,
        categoryCode: null,
        orderNumber: null,
        startDate: null,
        endDate: null
      },
      formLabelWidth: '120px'
    }
  },
  created () {
    this.rolePermissions = this.userInfo.rolePermissions[0]
      ? this.userInfo.rolePermissions[0].roleCode
      : null // 通过这个角色的code去判断如果在角色设置里面修改的话，程序要对应修改
    let _this = this
    this.tableHeader = [
      {
        prop: 'onlineInvoiceNum',
        label: _this.$t('purSettlementMod.onlineInvoiceNum'),
        width: 120,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.currentRow = row
          this.editTab('view', row)
        }.bind(this),
        formattor (val) {
          return val || '--'
        }
      },
      {
        prop: 'accountPayableDealine',
        label: _this.$t('paymentType.paymentDateDue'),
        width: 130,
        formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      },
      {
        prop: 'invoiceStatus',
        label: _this.$t('purSettlementMod.invoiceStatus'),
        width: 100,
        dataType: 'dict',
        code: 'INVOICE_STATUS'

      },
      {
        prop: 'importStatus',
        label: _this.$t('purSettlementMod.importStatus'),
        width: 100,
        dataType: 'dict',
        code: 'INVOICE_IMPORT_STATUS'

      },
      { prop: 'orgName', label: _this.$t('quota.org'), width: 120 },
      { prop: 'vendorCode', label: _this.$t('common.vendorCode'), width: 120 },
      {
        prop: 'vendorName',
        label: _this.$t('common.vendorName'),
        minWidth: 150
      },
      {
        prop: 'costTypeCode',
        label: _this.$t('purchaseDemand.vendorSite'),
        width: 120
        /* formattor (val) {
            return _this.$getDictLabelByValue(_this.costTypeList, val)
          } */
      },
      {
        prop: 'payAccountPeriodName',
        label: _this.$t('paymentType.paymentDay'),
        width: 120
      },
      {
        prop: 'taxTotalAmount',
        label: _this.$t('contractMod.totalAmountTax'),
        width: 120
      },
      { prop: 'totalTax', label: _this.$t('contractMod.taxQuota'), width: 120 },
      {
        prop: 'actualInvoiceAmountY',
        label: _this.$t('purSettlementMod.actualInvoiceAmountY'),
        width: 120
      },
      {
        prop: 'invoiceTax',
        label: _this.$t('purSettlementMod.invoiceTaxAmount'),
        width: 120
      },
      {
        prop: 'taxInvoiceNum',
        label: _this.$t('purSettlementMod.taxationInvoiceNum'),
        width: 120
      },
      { prop: 'boeNo', label: _this.$t('purSettlementMod.boeNo'), width: 120 },
      {
        prop: 'businessType',
        label: _this.$t('bidMod.businessType'),
        width: 120,
        dataType: 'dict',
        code: 'BUSINESS_TYPE'

      },
      {
        prop: 'createdUserName', // createdBy
        label: _this.$t('common.creator'),
        width: 100
      },
      {
        prop: 'creationDate',
        label: _this.$t('common.creationTime'),
        width: 100,
        formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      },
      { prop: 'comment', label: _this.$t('common.remark'), minWidth: 150 },
      {
        prop: 'operation',
        label: _this.$t('common.operation'),
        width: 150,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: function (row) {
              this.editTab('edit', row)
            }.bind(this),
            formattor (val) {
              return _this.$t('purSettlementMod.viewApprove')
            },
            code: 'pm:onlineInvoice:edit',
            // UNDER_APPROVAL----[审核中]---[REJECTED---已驳回]
            show: row =>
              ['UNDER_APPROVAL', 'REJECTED'].includes(row.invoiceStatus)
          },
          {
            callback: function (row) {
              _this.buyerWithdraw(row)
            },
            formattor (val) {
              return _this.$t('purchaseDemand.reject')
            },
            code: 'pm:onlineInvoice:withdraw',
            show: row =>
              (row.invoiceStatus === 'UNDER_APPROVAL' ||
                row.invoiceStatus === 'REJECTED') &&
              _this.curRole === 'BUYER'
          },
          {
            callback: function (row) {
              this.buyerAbandon(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.cancelled')
            }, // 【新建】【已驳回】
            code: 'pm:onlineInvoice:abandon',
            show: row => row.invoiceStatus === 'DRAFT'
          }
        ]
      }
    ]

    this.$nextTick(() => {
      // this.getQuerydata()
    })
  },
  methods: {
    getQuerydata (v) {
      this.queryParam = v || this.queryParam
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    syncFilterParams (values) {
      this.queryParam = values
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    // 【作废】
    buyerAbandon (row) {
      this.$http({
        url: '/api-sup-ce/ps/invoice/onlineInvoice/abandon',
        method: 'GET',
        params: { onlineInvoiceId: row.onlineInvoiceId },
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    exportList () {
      let params = Object.assign({}, this.queryParam)
      downloadFileLinkByPost(
        '/api-sup-ce/invoice/onlineInvoice/export',
        parseTime(new Date()) + this.$t('purSettlementMod.onlineInvoiceExport'),
        params
      ).catch(() => {
        this.$message.error(this.$t('purchaseDemand.downloadFail'))
      })
    },
    // 删除
    delRowData (row) {
      let onlineInvoiceId = row.onlineInvoiceId
      this.$api.pur.invoiceNoticeDel({ onlineInvoiceId }).then(res => {
        this.$message({
          message: res.message,
          type: 'success'
        })
        this.getQuerydata()
      })
    },
    editTab (type, row) {
      // 修改
      this.$emit('tab-add', {
        component: onlineInvoiceDetail,
        params: {
          flag: type,
          onlineInvoiceId: row.onlineInvoiceId,
          tabName: 'onlineInvoiceDetail' + row.onlineInvoiceNum,
          row
        },
        title: row.onlineInvoiceNum,
        name: 'onlineInvoiceDetail' + row.onlineInvoiceNum
      })
    },
    // 采购方撤回
    buyerWithdraw (row) {
      this.$http({
        url: '/api-sup-ce/ps/invoice/onlineInvoice/withdraw',
        method: 'GET',
        params: { onlineInvoiceId: row.onlineInvoiceId },
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    }
  }
}
</script>
<style scoped lang="scss">
.the_onlineInvoice_wrapper {
  .the_filter_form {
    .el-form-item {
      margin-bottom: 5px;
    }
  }
}
</style>
