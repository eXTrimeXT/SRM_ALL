<template>
  <el-container
    class="flex-container the_quotationPrices_wrapper"
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
          <!-- 新增 -->
          <AuthorityButton type="primary" @click="editTab('add')">
            {{ $t("purSettlementMod.newStatement") }}
          </AuthorityButton>
          <!-- 导出 -->
          <ExportExcel
            type="default"
            :table-header="tableHeader"
            export-mode="front"
            :dict-codes="dictCodes"
            :page-url="queryUrl"
            :filter-params="queryParam"
          />
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :checkbox="true"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :comActive="$attrs['changeTab']"
        :url="queryUrl"
      />
    </el-main>

    <!-- 驳回原因 -->
    <srm-dialog
      :title="$t('purSettlementMod.reasonForRejection')"
      :visible.sync="isRejectResult"
      :modal-append-to-body="false"
      size="middle"
      style="text-align: center"
    >
      <el-input
        v-model="rejectReason"
        type="textarea"
        :rows="4"
        :placeholder="$t('purSettlementMod.pleaseFillReasonForRejection')"
      />
      <div class="topComment">
        <el-button @click="isRejectResult = false">
          {{ $t("common.cancel") }}
        </el-button>
        <el-button type="primary" @click="confirmReject">
          {{ $t("common.confirm") }}
        </el-button>
      </div>
    </srm-dialog>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import invoiceNoticeDetail from './invoiceNoticeDetail'
import { adaptDictData, parseTime } from '@/utils'
import { downloadFileLinkByPost } from 'lib@/utils/file'
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
      preFormObj: {},
      dictCodes: {
        invoiceNoticeStatus: 'INVOICE_NOTICE_STATUS'
      },
      rejectRow: {},
      rejectReason: '', // 驳回原因描述
      isRejectResult: false,
      name: '',
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      tableName: 'invoiceNoticeList',
      gridId: 'invoiceNoticeList',
      selectList: [],
      currentRow: null,
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      preArr: [
        {
          prop: 'invoiceNoticeNumber',
          label: () => this.$t('purSettlementMod.statementNumber') // 对账单号
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
          prop: 'invoiceNoticeStatus',
          label: () => this.$t('purSettlementMod.paymentPlanStatus'), // 单据状态
          type: 'dict',
          code: 'INVOICE_NOTICE_STATUS'
        },
        {
          prop: 'receiveDate',
          label: () => this.$t('purSettlementMod.approvalCompleTime'), // 审批完成时间
          type: 'daterange'
        },
        {
          prop: 'receiveOrderNo',
          label: () => this.$t('accountMod.inboundReturnOrderNo') // 入库/退货单号
        },
        {
          prop: 'orderNumber',
          label: () => this.$t('purSettlementMod.orderNumber') // 采购订单号
        }
      ],
      queryParam: {},
      curRole: this.$store.getters.userType, // VENDOR BUYER curRole==='VENDOR'
      userInfo: this.$store.getters.userInfo,
      queryUrl: ''
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
          this.$route.params.funName === 'purStatementBillSupplier'
        ) {
          // 供应商 工作台跳转
          this.queryParam.invoiceNoticeStatus = this.$route.params.invoiceNoticeStatus
          this.preFormObj = Object.assign(
            {},
            { invoiceNoticeStatus: this.$route.params.invoiceNoticeStatus }
          )
        } else if (
          this.$route.params.from === 'fromFun' &&
          this.$route.params.funName === 'purStatementBillSupplier'
        ) {
          // 采购商 工作台跳转
          const invoiceNoticeId = Number(this.$route.params.formId)
          const formNo = this.$route.params.formNo // 流程标题
          const row = {
            ...this.$route.params,
            invoiceNoticeId,
            invoiceNoticeNumber: formNo // tab 标题显示
          }
          this.editTab('view', row)
        }
      }
    }
  },
  created () {
    this.queryUrl = '/api-sup-ce/sup/invoice/invoiceNotice/listPageByParm'
    const _this = this
    this.tableHeader = [
      { prop: 'orgName', label: _this.$t('quota.org'), width: 120 }, // 业务实体
      {
        prop: 'organizationName',
        label: _this.$t('purchaseDemand.invOrg'),
        width: 120
      }, // 库存组织
      {
        prop: 'invoiceNoticeNumber',
        label: _this.$t('purSettlementMod.statementNumber'), // 对账单号
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
        prop: 'invoiceNoticeStatus',
        label: _this.$t('purSettlementMod.paymentPlanStatus'), // 单据状态
        width: 100,
        dataType: 'dict',
        code: 'INVOICE_NOTICE_STATUS'
      },
      { prop: 'vendorCode', label: _this.$t('common.vendorCode'), width: 120 }, // 供应商编码
      {
        prop: 'vendorName',
        label: _this.$t('bidMod.provider'), // 供应商
        minWidth: 150
      },
      {
        prop: 'ceeaReceiveStartDate',
        label: () => this.$t('purSettlementMod.statementStartTime'), // 对账开始日期
        width: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'ceeaReceiveEndDate',
        label: () => this.$t('purSettlementMod.statementEndTime'), // 对账结束日期
        width: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'ceeaTaxTotalAmount',
        label: _this.$t('contractMod.totalAmountTax'), // 含税总金额
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
      {
        prop: 'createdUserName',
        label: _this.$t('common.creator'), // 创建人
        width: 100
      },
      {
        prop: 'creationDate',
        label: _this.$t('purSettlementMod.creationDate'), // 创建日期
        width: 100,
        formattor: (val) => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      },
      {
        prop: 'approvedDate',
        label: _this.$t('purSettlementMod.approvalCompleTime'), // 审批完成时间
        width: 120,
        formattor: (val) => (val ? parseTime(val, '{y}-{m}-{d}') : '')
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
            // 拟定/采购已驳回
            show: (row) => {
              return (
                ['DRAFT', 'FIRST_REJECTED'].includes(row.invoiceNoticeStatus)
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
            show: (row) =>
              ['FIRST_REJECTED'].includes(row.invoiceNoticeStatus)
          },
          {
            callback: function (row) {
              this.delRowData(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.delete') // 删除
            },
            code: 'pm:invoiceNoticeList:abandon',
            show: (row) =>
              ['DRAFT'].includes(row.invoiceNoticeStatus)
          }
        ]
      }
    ]

    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    // 导出
    exportList () {
      const params = Object.assign(
        {},
        {
          ...this.queryParam,
          pageNum: this.getFooterNum,
          pageSize: 15
        }
      )
      // (待) 等接口
      downloadFileLinkByPost('/api-sup-ce/', null, params).catch(
        () => {
          // 下载失败
          this.$message.error(this.$t('components.eio.downloadFail'))
        }
      )
    },
    getQuerydata (obj) {
      const params = {}
      const { dateList, receiveDate, ...rest } = obj || this.preFormObj
      if (dateList) {
        params.startCreationDate = dateList[0]
        params.endCreationDate = dateList[1]
      }
      if (receiveDate) {
        params.startApprovedDate = receiveDate[0]
        params.endApprovedDate = receiveDate[1]
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
    // 采购方撤回
    buyerWithdraw (row) {
      this.$http({
        url: '/api-sup-ce/sup/invoice/invoiceNotice/withdraw',
        method: 'GET',
        params: { invoiceNoticeId: row.invoiceNoticeId },
        loading: true
      }).then((res) => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    // 作废
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
        url: '/api-sup-ce/sup/invoice/invoiceNotice/abandon',
        method: 'GET',
        params: { invoiceNoticeId: row.invoiceNoticeId },
        loading: true
      }).then((res) => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    // 确认驳回
    async confirmReject () {
      await this.abandonHandel(this.rejectRow)
      this.isRejectResult = false
    },
    // 供应商驳回
    abandonHandel (row) {
      this.$http({
        url: '/api-sup-ce/sup/invoice/invoiceNotice/reject',
        method: 'POST',
        params: {
          invoiceNoticeId: row.invoiceNoticeId,
          rejectReason: this.rejectReason
        },
        loading: true
      }).then((res) => {
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

      const invoiceNoticeId = row.invoiceNoticeId
      this.$http({
        url: '/api-sup-ce/sup/invoice/invoiceNotice/deleteByInvoiceNoticeId',
        method: 'GET',
        params: { invoiceNoticeId },
        loading: true
      }).then((res) => {
        this.$message({
          message: res.message,
          type: 'success'
        })
        this.getQuerydata()
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
  .topComment {
    margin-top: 15px;
    float: right;
  }
</style>
