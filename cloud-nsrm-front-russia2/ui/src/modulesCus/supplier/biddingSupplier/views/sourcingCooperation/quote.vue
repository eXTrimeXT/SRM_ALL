<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <div class="header">
        <div class="header-title">
          <div class="title">
            {{ form.projectName }}
          </div>
          <div class="time">
            <DynamicCutoffTime :label="$t('bidMod.competitionLts.signUpEndTime')" :deadline-time="form.publicEndTime" />
          </div>
        </div>
        <div class="header-box">
          <div class="row">
            <div class="row-item">
              {{ $t('bidMod.businessNo') }}: 
              <span>{{ form.reqHeadNo }}</span>
            </div>
            <div class="row-item">
              {{ $t('bidMod.status') }}: 
              <span>{{ $getDictLabel('SOU_REQ_HEAD_STATUS',form.status) }}</span>
            </div>
            <div class="row-item">
              {{ $t('bidMod.stopTime') }}: 
              <span>{{ $parseTime(form.publicEndTime) }}</span>
            </div>
            <div class="row-item">
              {{ $t('qualitySynergy.releaseDate') }}: 
              <span>{{ $parseTime(form.releaseDate) }}</span>
            </div>
          </div>
          <div class="row">
            <div class="row-item">
              <!-- 已邀请供应商: -->
              {{ $t('cusEntry.supplement20250121.invitedSuppliers') }}: 
              <span>（{{ form.inviteQuantity }}）</span>
            </div>
            <div class="row-item">
              <!-- 已报名供应商: -->
              {{ $t('cusEntry.supplement20250121.registeredSupplier') }}: 
              <span>（{{ totalQuote }}）</span>
            </div>
            <div class="row-item">
              <!-- 意向金应缴纳（卢布）: -->
              {{ $t('cusEntry.supplement20250205.depositAmount') }}: 
              <span>{{ form.depositAmount }}</span>
            </div>
            <div class="row-item">
              <!-- 项目已阅数量: -->
              {{ $t('cusEntry.supplement20250121.projectViewedQuantity') }}: 
              <span>{{ form.projectViewsCount }}</span>
            </div>
          </div>
          <div v-if="form.closePublicReason" class="row">
            <div class="row-item">
              <!-- 关闭公示原因： -->
              {{ $t('cusEntry.supplement20250121.reasonForClosingThePublicNotice') }}: 
              <span>{{ form.closePublicReason }}</span>
            </div>
          </div>
        </div>
      </div>
      <div class="content">
        <FormWrapper :colLength="3" :form-array="searchFormConfig" @getFormData="getQueryData" />

        <MainHeader :l-span="22" :r-span="2">
          <template v-if="!disabledFlag" slot="left">
            <AuthorityButton type="primary" @click="adjustPublicTime">
              <!-- 调整公示时间 -->
              {{ $t('cusEntry.supplement20250121.adjustThePublicAnnouncementTime') }}
            </AuthorityButton>
            <AuthorityButton type="primary" @click="adjustPublicInfo">
              <!-- 修改公示信息 -->
              {{ $t('cusEntry.supplement20250121.modifyPublicInformation') }}
            </AuthorityButton>
            <AuthorityButton type="ghost" @click="viewPublicHistory">
              <!-- 查看公示修改历史 -->
              {{ $t('cusEntry.supplement20250121.viewPublicAnnouncementModificationHistory') }}
            </AuthorityButton>
            <AuthorityButton type="primary" @click="vendorRecommend">
              <!-- 发起供应商推荐单 -->
              {{ $t('cusEntry.supplement20250205.vendorRecommend') }}
            </AuthorityButton>
            <AuthorityButton type="primary" @click="intentionQuit">
              <!-- 意向金退款 -->
              {{ $t('cusEntry.supplement20250205.intentionQuit') }}
            </AuthorityButton>
            <AuthorityButton type="ghost" @click="viewIntentionHistory">
              <!-- 查看退款历史 -->
              {{ $t('cusEntry.supplement20250205.viewIntentionHistory') }}
            </AuthorityButton>
            <AuthorityButton :disabled="!!form.closePublicReason" type="primary" @click="closePublicInfo">
              <!-- 关闭公示信息 -->
              {{ $t('cusEntry.supplement20250121.closePublicInformation') }}
            </AuthorityButton>
          </template>
        </MainHeader>

        <div class="table-wrapper">
          <TableView
            ref="list"
            :table-data="tableData"
            :table-header="tableHeader"
            :pre-query-data="queryParam"
            :com-active="$attrs['changeTab']"
            :checkbox="true"
            :checkChange="handleCurrentChange"
            :open-custom-table="false"
            tableHeight="350px"
            :pageEnabled="false"
            :source="souHttp.read"
            :transformData="transformData"
            :adeptMeiQl="true"
          >
            <template #depositFileId="{scope}">
              <SrmCommonFile
                :extra-data="fileInfo"
                :default-file="{
                  fileId: scope.row.depositFileId,
                  fileName: scope.row.depositFileName
                }"
                :readonly="true"
              />
            </template>
          </TableView>
        </div>
      </div>
    </el-main>

    <!-- 意向金退款弹窗 -->
    <IntentionDialog
      ref="intentionDialog"
      :visible.sync="intentionDialogVisible"
      :readonly="intentionReadonly"
      :editRows="selectedRows"
      :form="form"
      :mode="mode"
      @confirm="intentionDialogConfirm"
    />

    <!-- 修改公示信息 -->
    <PublicInfoDialog
      ref="publicInfoDialog"
      :visible.sync="publicInfoDialogVisible"
      :baseForm="form"
      :readonly="false"
      @confirm="publicInfoConfirm"
    />

    <!-- 查看公示修改历史 -->
    <PublicHistoryDialog
      ref="publicHistoryDialog"
      :visible.sync="publicHistoryDialogVisible"
      :form="form"
    />

    <!-- 调整公示时间 -->
    <PublicTimeDialog
      ref="publicTimeDialog"
      :visible.sync="publicTimeDialogVisible"
      :form="form"
      @confirm="PublicTimeDialogConfirm"
    />

    <!-- 报名处理 -->
    <QuoteDealDialog
      ref="quoteDealDialog"
      :visible.sync="quoteDealDialogVisible"
      @confirm="quoteDealDialogConfirm"
    />

    <!-- 关闭公示信息 -->
    <ClosePublicDialog
      ref="closePublicDialog"
      :visible.sync="closePublicDialogVisible"
      @confirm="closePublicDialogConfirm"
    />

    <!-- 意向金开票详情 -->
    <InvoiceDialog
      ref="invoiceDialog"
      :visible.sync="invoiceDialogVisible"
      :editRows="currentRow"
    />
  </el-container>
</template>
<script>
import DynamicCutoffTime from 'lib@/components/dynamic-cutoff-time'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import IntentionDialog from './components/quote/intentionDialog'
import PublicInfoDialog from './components/quote/publicInfoDialog'
import PublicHistoryDialog from './components/quote/publicHistoryDialog'
import PublicTimeDialog from './components/quote/publicTimeDialog'
import QuoteDealDialog from './components/quote/quoteDealDialog'
import ClosePublicDialog from './components/quote/closePublicDialog'
import InvoiceDialog from './components/quote/invoiceDialog'
import souHttp from '../../api'
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'SourcingRequireQuote',
  components: {
    DynamicCutoffTime,
    FormWrapper,
    MainHeader,
    TableView,
    IntentionDialog,
    PublicInfoDialog,
    PublicHistoryDialog,
    PublicTimeDialog,
    QuoteDealDialog,
    ClosePublicDialog,
    InvoiceDialog
  },
  data () {
    return {
      form: {
        projectName: null,
        publicEndTime: null,
        reqHeadId: null,
        reqHeadNo: null,
        status: null,
        releaseDate: null,
        inviteQuantity: null,
        depositAmount: null,
        projectViewsCount: null
      },
      queryParam: {},
      tableViewUrl: '',
      fileInfo: {
        fileModular: 'sou',
        fileFunction: 'sourcingRequireBuyer',
        fileType: 'images'
      },
      searchFormConfig: [
        {
          prop: 'vendorName',
          label: this.$t('common.vendorName')
        },
        {
          prop: 'applyStatus',
          label: this.$t('sourcingBuyer.applyStatus'),
          type: 'dict',
          code: 'SOU_REQ_APPLY_STATUS'
        }
      ],
      tableData: [],
      tableHeader: [],
      currentRow: {},
      selectedRows: [],
      intentionDialogVisible: false,
      intentionReadonly: false,
      publicInfoDialogVisible: false,
      publicHistoryDialogVisible: false,
      publicTimeDialogVisible: false,
      quoteDealDialogVisible: false,
      closePublicDialogVisible: false,
      invoiceDialogVisible: false,
      mode: 'quit', // 退款:quit,查看历史:history
      totalQuote: 0,
      souHttp,
      transformData: (data) => {
        console.log('data', data.data[0])
        const { fileUploads, souReqApplyList, ...rest } = data.data[0]
        this.form = rest
        this.totalQuote = souReqApplyList.length
        return {
          data: {
            records: souReqApplyList
          }
        }
      }
    }
  },
  computed: {
    urlParams () {
      return this.$attrs.params || {}
    },
    disabledFlag () {
      return ['SIGNUP_DONE'].includes(this.form.status)
    }
  },
  created () {
    this.tableHeader = [
      {
        prop: 'vendorCode',
        label: this.$t('common.vendorCode'),
        minWidth: 130
      },
      {
        prop: 'vendorName',
        label: this.$t('sourcingBuyer.vendorName'),
        minWidth: 150
      },
      // 是否内部供应商
      {
        prop: 'isInternalVendor',
        label: this.$t('cusEntry.bidMod.innerFlag'),
        minWidth: 130,
        dataType: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'applyStatus',
        label: this.$t('sourcingBuyer.applyStatus'),
        minWidth: 120,
        dataType: 'dict',
        code: 'SOU_REQ_APPLY_STATUS'
      },
      // 报名联系人
      {
        prop: 'applyContactName',
        label: this.$t('cusEntry.common.signUpPerson'),
        minWidth: 120
      },
      // 报名联系方式
      {
        prop: 'applyPhone',
        label: this.$t('cusEntry.supplement20250121.registrationContactInformation'),
        minWidth: 130
      },
      {
        prop: 'applyEmail',
        label: this.$t('sourcingBuyer.email'),
        minWidth: 130
      },
      // 意向金缴纳凭证
      {
        prop: 'depositFileId',
        label: this.$t('cusEntry.supplement20250205.depositFileId'),
        minWidth: 130,
        showType: 'slot',
        slot: 'depositFileId'
      },
      // 意向金缴纳状态
      {
        prop: 'depositStatus',
        label: this.$t('cusEntry.supplement20250205.depositStatus'),
        minWidth: 130,
        dataType: 'dict',
        code: 'SOU_INT_DEPOSIT_STATUS'
      },
      // 供应商缴纳账户
      {
        prop: 'vendorBankAccount',
        label: this.$t('cusEntry.bidMod.payAccount'),
        minWidth: 130
      },
      // 缴纳户名
      {
        prop: 'vendorBankAccountName',
        label: this.$t('cusEntry.bidMod.payAccountName'),
        minWidth: 130
      },
      // 缴纳银行
      {
        prop: 'vendorBankName',
        label: this.$t('cusEntry.bidMod.payBank'),
        minWidth: 130
      },
      // 银行联行号
      {
        prop: 'vendorBankNumber',
        label: this.$t('cusEntry.biddingSettings.bankNum'),
        minWidth: 130
      },
      // 意向金开票详情
      {
        label: this.$t('cusEntry.supplement20250121.invoiceDetailsForEarnestMoney'),
        minWidth: 130,
        showType: 'button',
        btnStyle: 'text',
        formattor: () => this.$t('sourcingBuyer.viewDetail'),
        show: row => {
          // 报名确认中才显示
          return row.applyStatus === 'CONFIRMING_SIGNUP'
        },
        callback: (row) => {
          this.currentRow = row
          this.invoiceDialogVisible = true
        }
      },
      {
        prop: 'creationDate',
        label: this.$t('sourcingBuyer.signUpTime'),
        minWidth: 130,
        dataType: 'dateTime'
      },
      // 是否代理
      {
        prop: 'isAgent',
        label: this.$t('cusEntry.supplement20250121.whetherToActAsAnAgent'),
        minWidth: 120,
        dataType: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'agentBrand',
        label: this.$t('vendorMod.agencyBrand'),
        minWidth: 120
      },
      {
        label: this.$t('sourcingBuyer.signUpDetail'),
        minWidth: 130,
        showType: 'button',
        btnStyle: 'text',
        fixed: 'right',
        formattor: () => this.$t('sourcingBuyer.viewDetail'),
        show: row => {
          return true
        },
        callback: (col, row) => {

        }
      },
      // 退款状态
      {
        prop: 'depositRefundStatus',
        label: this.$t('cusEntry.bidMod.refundStatus'),
        minWidth: 120,
        dataType: 'dict',
        code: 'SOU_INT_DEPOSIT_REFUND_STATUS'
      },
      // 报名处理方式
      {
        prop: 'applyHandleType',
        label: this.$t('cusEntry.reportManagement.applyHandleType'),
        minWidth: 130,
        dataType: 'dict',
        code: 'SOU_APPLY_HANDLE_TYPE'
      },
      // 报名处理原因
      {
        prop: 'applyHandleReason',
        label: this.$t('cusEntry.reportManagement.applyHandleReason'),
        minWidth: 120
      },
      // {
      //   prop: 'closePublicReason',
      //   label: '关闭公示原因',
      //   minWidth: 150
      // },
      {
        prop: 'operation',
        label: this.$t('bidMod.operation'),
        minWidth: 130,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            show: row => true,
            formattor: () => this.$t('cusEntry.supplement20250121.registrationProcessing'), // 报名处理
            callback: row => {
              this.currentRow = row
              this.quoteDealDialogVisible = true
              this.$refs.quoteDealDialog.resetFields()
            }
          }
        ]
      }
    ]
    this.reqHeadId = this.urlParams.row.reqHeadId
    this.getQueryData()
  },
  methods: {
    /* 查询列表数据 */
    getQueryData (params = {}) {
      this.queryParam = transformMQL.save('SouReqHeadBuyer', [{
        reqHeadId: this.reqHeadId
      }], 'read',
      {
        '*': {},
        'souReqApplyList': {
          '*': {},
          '$condition': {
            $strictQuery: true,
            filter: {
              vendorName: {
                contains: params.vendorName
              },
              applyStatus: {
                eq: params.applyStatus
              }
            }
          }
        },
        'fileUploads': {
          '*': {}
        }
      })

      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },
    handleCurrentChange (val) {
      this.selectedRows = val
    },
    /** 操作按钮列 */
    adjustPublicTime () {
      this.publicTimeDialogVisible = true
      this.$refs.publicTimeDialog.resetFields()
    },
    async PublicTimeDialogConfirm (time) {
      let transformParams = transformMQL.save('SouReqHeadBuyer', [{
        reqHeadId: this.reqHeadId,
        publicEndTime: time
      }], 'updatePublicEndTime')
      const response = await souHttp.updatePublicEndTime(transformParams)
      if (response) {
        this.publicTimeDialogVisible = false
        this.$message.success(this.$t('common.success'))
        this.getQueryData()
      }
    },
    adjustPublicInfo () {
      this.publicInfoDialogVisible = true
      this.$refs.publicInfoDialog.resetFields()
    },
    async publicInfoConfirm (data) {
      let transformParams = transformMQL.save('SouInfoHistoryBuyer', [{
        ...data,
        reqHeadId: this.reqHeadId
      }], 'submit')
      const response = await souHttp.publicHisSubmit(transformParams)
      if (response) {
        this.publicInfoDialogVisible = false
        this.$message.success(this.$t('common.success'))
        this.getQueryData()
      }
    },
    viewPublicHistory () {
      this.publicHistoryDialogVisible = true
      this.$refs.publicHistoryDialog.doLayout()
    },
    vendorRecommend () {

    },
    async quoteDealDialogConfirm (data) {
      let transformParams = transformMQL.save('SouReqApplyBuyer', [{
        ...this.currentRow,
        ...data
      }], 'handleApply')
      const response = await souHttp.handleApply(transformParams)
      if (response) {
        this.quoteDealDialogVisible = false
        this.$message.success(this.$t('common.success'))
        this.getQueryData()
      }
    },
    intentionQuit () {
      if (!this.selectedRows || !this.selectedRows.length) return this.$message.warning(this.$t('outsource.pleaseCheckList'))
      for (let item of this.selectedRows) {
        if (item.depositStatus !== 'PAID') {
          // 仅可勾选已缴纳意向金的供应商发起退款
          this.$message.warning(this.$t('cusEntry.supplement20250121.onlySuppliersWhoHaveAlreadyPaidTheEarnestMoneyCanBeSelectedToInitiateRefunds'))
          return
        }
      }
      this.mode = 'quit'
      this.intentionDialogVisible = true
      this.intentionReadonly = false
    },
    async intentionDialogConfirm (data) {
      let transformParams = transformMQL.save('SouIntDepositRefundBuyer', data, 'submit')
      const response = await souHttp.refundSubmit(transformParams)
      if (response) {
        this.intentionDialogVisible = false
        this.$message.success(this.$t('common.success'))
        this.getQueryData()
      }
    },
    viewIntentionHistory () {
      this.mode = 'history'
      this.intentionDialogVisible = true
      this.intentionReadonly = true
    },
    closePublicInfo () {
      this.closePublicDialogVisible = true
      this.$refs.closePublicDialog.resetFields()
    },
    async closePublicDialogConfirm (data) {
      let transformParams = transformMQL.save('SouReqHeadBuyer', [{
        reqHeadId: this.reqHeadId,
        closePublicReason: data
      }], 'cancelPublic')
      const response = await souHttp.cancelPublic(transformParams)
      if (response) {
        this.closePublicDialogVisible = false
        this.$message.success(this.$t('common.success'))
        this.getQueryData()
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.header {
  background: #f2f2f2;
  padding: 20px;
  .header-title {
    display: flex;
    align-items: center;
    justify-content: space-between;
    .title {
      font-size: 16px;
      font-weight: bold;
    }
  }
  .header-box {
    padding:10px 30px;
    .row {
      display: flex;
      align-items: center;
      justify-content: space-around;
      & + .row {
        margin-top: 10px;
      }
      .row-item {
        flex: 1;
      }
    }
  }
}
.content {
  padding:10px;
}
</style>
