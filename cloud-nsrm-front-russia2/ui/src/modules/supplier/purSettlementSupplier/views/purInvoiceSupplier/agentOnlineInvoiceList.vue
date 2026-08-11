<template>
  <el-container
    class="flex-container the_onlineInvoice_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :pre-form-obj="preFormObj"
        :form-array="preArr"
        form-label-width="120px"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <!-- <AuthorityButton
            code="pm:agentOnlineInvoice:add"
            style="margin: 0  10px 0 0;"
            type="primary"
            @click="openDialog"
            >{{ $t('common.add') }}</AuthorityButton
          > -->
          <!-- 创建开票单 -->
          <AuthorityButton type="primary" @click="editTab('add')">
            {{ $t("purSettlementMod.createBillingSlip") }}
          </AuthorityButton>
          <ExportExcel
            type="default"
            :table-header="tableHeader"
            export-mode="front"
            :dict-codes="dictCodes"
            page-url="/api-sup-ce/sup/invoice/onlineInvoice/listPageByParm"
            :filter-params="queryParam"
          />
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :comActive="$attrs['changeTab']"
        url="/api-sup-ce/sup/invoice/onlineInvoice/listPageByParm"
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
import agentOnlineInvoiceDetail from './agentOnlineInvoiceDetail'
import { adaptDictData, parseTime } from '@/utils'
import ExportExcel from 'lib@/components/export-excel'
import { getDictItemList, getAllPurCurrency, getAllPurUnit, getAllPurTax } from '@/api/common'

export default {
  name: 'AgentOnlineInvoiceList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    CPagination,
    OrganizationSelector,
    QuickSearch,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      preFormObj: {},
      dictCodes: {
        invoiceStatus: 'INVOICE_STATUS',
        payMethod: 'PAYMENT_MODE'
      },
      globalNickname: null,
      name: '',
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      tableName: 'agentOnlineInvoiceList',
      gridId: 'agentOnlineInvoiceList',
      selectList: [],
      currentRow: null,
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      preArr: [
        {
          prop: 'onlineInvoiceNum',
          label: () => this.$t('purSettlementMod.billingNumber') // 开票单号
        },
        {
          prop: 'orgId',
          label: () => this.$t('quota.org'), // 业务实体
          type: 'OUorganizationSelector'
        },
        {
          prop: 'organizationId',
          parentId: 'orgId',
          label: () => this.$t('purchaseDemand.invOrg'), // 库存组织
          type: 'INVorganizationSelector'
        },
        {
          prop: 'dateList',
          label: () => this.$t('bidMod.dateCreated'), // 创建日期
          type: 'daterange'
        },
        {
          prop: 'dateComplates',
          label: () => this.$t('purSettlementMod.approvalCompleTime'),
          type: 'daterange'
        },
        {
          prop: 'invoiceNoticeNumber',
          label: () => this.$t('purSettlementMod.statementNumber') // 对账单号
        },
        // {
        //   prop: 'vendorId',
        //   label: () => this.$t('bidMod.provider'), // 供应商
        //   type: 'quicksearch',
        //   showKey: 'companyName',
        //   propKey: 'companyId',
        //   name: 'scc_sup_company_info_all'
        // },
        {
          prop: 'invoiceStatus',
          label: () => this.$t('purSettlementMod.paymentPlanStatus'), // 单据状态
          type: 'dict',
          code: 'INVOICE_STATUS'
        },
        {
          prop: 'payMethod',
          label: () => this.$t('paymentType.paymentWay'), // 付款方式
          type: 'dict',
          code: 'PAYMENT_MODE'
        },
        {
          prop: 'invoiceCode',
          label: () => this.$t('accountMod.invoiceCode') // 发票代码
        }
      ],
      queryParam: {},
      // curRole: this.$store.getters.userType, // VENDOR BUYER curRole==='VENDOR'
      curRole: '',
      rolePermissions: '', // 操作角色 Buyer 采购员\ AccountSpecialist 财务专员
      // userInfo: this.$store.getters.userInfo,
      userInfo: {},
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
      formLabelWidth: '120px'
    }
  },
  watch: {
    $route: {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler () {
        if (
          this.$route.params.from === 'workCount' &&
          this.$route.params.funName === 'purInvoiceSupplier'
        ) {
          // 供应商 工作台跳转
          this.queryParam.invoiceStatus = this.$route.params.invoiceStatus
          this.preFormObj = Object.assign(
            {},
            { invoiceStatus: this.$route.params.invoiceStatus }
          )
        }
      }
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      { prop: 'orgName', label: _this.$t('quota.org'), width: 120 }, // 业务实体
      { prop: 'organizationName', label: _this.$t('purchaseDemand.invOrg'), width: 120 }, // 库存组织
      {
        prop: 'onlineInvoiceNum',
        label: this.$t('purSettlementMod.billingNumber'), // 开票单号
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
      // 单据状态
      {
        prop: 'invoiceStatus',
        label: _this.$t('purSettlementMod.paymentPlanStatus'),
        width: 100,
        dataType: 'dict',
        code: 'INVOICE_STATUS'
      },
      // 供应商编码
      { prop: 'vendorCode', label: _this.$t('common.vendorCode'), width: 120 },
      // 供应商名称
      { prop: 'vendorName', label: _this.$t('common.vendorName'), width: 150 },
      // 开票含税金额
      {
        prop: 'taxTotalAmount',
        label: this.$t('purSettlementMod.taxTotalAmount'),
        width: 120
      },
      // 付款方式
      {
        prop: 'payMethod',
        label: _this.$t('paymentType.paymentWay'),
        dataType: 'dict',
        code: 'PAYMENT_MODE',
        width: 120
      },
      {
        prop: 'currencyName',
        label: _this.$t('vendorMod.currencyCode'), // 币种
        width: 120
      },
      {
        prop: 'taxRate',
        label: _this.$t('bid_mod.taxRate'), // 税率
        width: 120
      },
      // 创建人
      {
        prop: 'createdUserName', // createdBy
        label: _this.$t('common.creator'),
        width: 100
      },
      // 创建时间
      {
        prop: 'creationDate',
        label: _this.$t('common.creationTime'),
        width: 100,
        formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      },
      // 创建时间
      {
        prop: 'approvedDate',
        label: this.$t('purSettlementMod.approvedDate'),
        width: 130,
        formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      },
      {
        prop: 'rejectReason',
        label: this.$t('purSettlementMod.rejectReason'), // 驳回原因
        width: 150
      },
      {
        prop: 'operation',
        label: _this.$t('common.operation'),
        width: 180,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: function (row) {
              this.editTab('edit', row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.edit') // 编辑
            },
            show: row => {
              return (
                ['DRAFT', 'FIRST_REJECTED'].includes(row.invoiceStatus)
              )
            }
          },
          {
            callback: function (row) {
              this.buyerAbandon(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.cancelled') // 作废
            },
            // 采购已驳回
            show: row => ['FIRST_REJECTED'].includes(row.invoiceStatus)
          },
          {
            callback: function (row) {
              this.delRowData(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.delete') // 删除
            },
            show: row =>
              ['DRAFT'].includes(row.invoiceStatus)
          }
        ]
      }
    ]

    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    // 【作废】
    async buyerAbandon (row) {
      const isConfirm = await this.$confirm(
        this.$t('purSettlementMod.isDiscarded'),
        this.$t('common.tips'),
        {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        },
      )
      if (isConfirm !== 'confirm') return

      this.$http({
        url: '/api-sup-ce/sup/invoice/onlineInvoice/abandon',
        method: 'GET',
        params: { onlineInvoiceId: row.onlineInvoiceId },
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    getQuerydata (obj) {
      const params = {}
      const { dateList, dateComplates, ...rest } = obj || this.preFormObj
      if (dateList) {
        params.startCreationDate = dateList[0]
        params.endCreationDate = dateList[1]
      }
      if (dateComplates) {
        params.startApprovedDate = dateComplates[0]
        params.endApprovedDate = dateComplates[1]
      }
      this.queryParam = { ...rest, ...params }
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
    // 采购商审核
    buyerApproval (row) {
      this.$http({
        url: '/api-sup-ce/invoice/invoiceNotice/confirm',
        method: 'GET',
        params: { onlineInvoiceId: row.onlineInvoiceId },
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    // 删除
    async delRowData (row) {
      const sign = await this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
      if (sign !== 'confirm') return

      this.$http({
        url: '/api-sup-ce/sup/invoice/onlineInvoice/deleteByOnlineInvoiceId',
        method: 'GET',
        params: { onlineInvoiceId: row.onlineInvoiceId },
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    editTab (type, row) {
      let tab = {}
      if (type === 'add') {
        // 新增
        tab = {
          component: agentOnlineInvoiceDetail,
          params: {
            flag: 'add',
            tabName: 'agentOnlineInvoiceDetail'
          },
          title: this.$t('purSettlementMod.newOnlineInvoice'),
          name: 'agentOnlineInvoiceDetail'
        }
      } else {
        // 修改
        tab = {
          component: agentOnlineInvoiceDetail,
          params: {
            flag: type,
            onlineInvoiceId: row.onlineInvoiceId,
            tabName: 'agentOnlineInvoiceDetail' + row.onlineInvoiceNum
          },
          title: row.onlineInvoiceNum,
          name: 'agentOnlineInvoiceDetail' + row.onlineInvoiceNum
        }
      }
      this.$emit('tab-add', tab)
    },
    handleSelectionChange2 (selection) {
      this.selectionItem = selection
    },
    setRowAmount (row) {
      if (row.notInvoiceQuantity > 0) {
        if (row.invoiceQuantity > row.notInvoiceQuantity) {
          return this.$message.warning(this.$t('purSettlementMod.invoiceMsg[0]'))
        }
      } else {
        if (row.invoiceQuantity < row.notInvoiceQuantity || row.invoiceQuantity > 0) {
          return this.$message.warning(this.$t('purSettlementMod.invoiceMsg[0]'))
        }
      }
      row.noTaxAmount = Number(
        Number(row.invoiceQuantity) * Number(row.unitPriceExcludingTax || 0)
      ).toFixed(2)
    },
    getcontractObj (val, scope) {
      scope.contractNo = val.contractCode
    },
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
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
