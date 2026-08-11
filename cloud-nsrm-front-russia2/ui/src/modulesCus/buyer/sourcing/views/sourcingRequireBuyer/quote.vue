<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <div class="header">
        <div class="header-title">
          <div class="title">
            {{ form.projectName }}
          </div>
          <div v-if="form.status !=='ABANDON'" class="time">
            <DynamicCutoffTime :label="$t('bidMod.competitionLts.signUpEndTime')" :deadline-time="form.publicEndTime" @isDeadline="handleDeadline" />
          </div>
        </div>
        <div class="header-box">
          <div class="row">
            <div class="row-item">
              <!-- 寻源单号: -->
              {{ $t("bidMod.businessNo") }}:
              <span>{{ form.reqHeadNo }}</span>
            </div>
            <div class="row-item">
              <!-- 状态: -->
              {{ $t("components.stratProcess.headers.docStatusValue") }}:
              <span>{{ $getDictLabel('SOU_REQ_HEAD_STATUS',form.status) }}</span>
            </div>
            <div class="row-item">
              <!-- 截止时间: -->
              {{ $t("bidMod.stopTime") }}:
              <span>{{ $parseTime(form.publicEndTime) }}</span>
            </div>
            <div class="row-item">
              <!-- 发布时间: -->
              {{ $t("components.notice.publishTime") }}:
              <span>{{ $parseTime(form.releaseDate) }}</span>
            </div>
          </div>
          <div class="row">
            <div class="row-item">
              <!-- 已邀请供应商: -->
              {{ $t("cusEntry.supplement20250121.invitedSuppliers") }}:
              <span>（{{ form.inviteQuantity }}）</span>
            </div>
            <div class="row-item">
              <!-- 已报名供应商: -->
              {{ $t("cusEntry.supplement20250121.registeredSupplier") }}:
              <span>（{{ totalQuote }}）</span>
            </div>
             <div class="row-item">
              <!-- 项目已阅数量: -->
              {{ $t("cusEntry.supplement20250121.projectViewedQuantity") }}:
              <span>{{ form.projectViewsCount }}</span>
            </div>
            <div class="row-item">
              <!-- 意向金应缴纳（元）:
              <span>{{ form.isNeedDeposit === 'Y' ? form.depositAmount : '-' }}</span> -->
            </div>
          </div>
          <div v-if="form.closePublicReason" class="row">
            <div class="row-item">
              <!-- 关闭公示原因： -->
              {{ $t("cusEntry.supplement20250121.reasonForClosingThePublicNotice") }}:
              <span>{{ form.closePublicReason }}</span>
            </div>
          </div>
        </div>
      </div>
      <div class="content">
        <FormWrapper :colLength="3" :form-array="searchFormConfig" @getFormData="getQueryData" />

        <MainHeader :l-span="22" :r-span="2">
          <template slot="left">
            <AuthorityButton v-if="!disabledFlag" code="sourcingRequireBuyer:adjustPublicTime" type="primary" @click="adjustPublicTime">
              <!-- 调整公示时间 -->
              {{ $t("cusEntry.supplement20250121.adjustThePublicAnnouncementTime") }}
            </AuthorityButton>
            <AuthorityButton v-if="!disabledFlag" code="sourcingRequireBuyer:adjustPublicInfo" type="primary" @click="adjustPublicInfo">
              <!-- 修改公示信息 -->
              {{ $t("cusEntry.supplement20250121.changeThePublicAnnouncementTime") }}
            </AuthorityButton>
            <AuthorityButton code="sourcingRequireBuyer:viewPublicInfo" type="ghost" @click="viewPublicHistory">
              <!-- 查看公示修改历史 -->
              {{ $t("cusEntry.supplement20250121.viewPublicAnnouncementModificationHistory") }}
            </AuthorityButton>
            <!-- <AuthorityButton :disabled="form.status !== 'SIGNUP_DONE'" type="primary" @click="vendorRecommend">
              发起供应商推荐单
            </AuthorityButton> -->
            <!-- <AuthorityButton code="sourcingRequireBuyer:intenQuit" type="primary" @click="intentionQuit">
              意向金退款
            </AuthorityButton>
            <AuthorityButton code="sourcingRequireBuyer:viewIntenHis" type="ghost" @click="viewIntentionHistory">
              查看退款历史
            </AuthorityButton> -->
            <AuthorityButton
              v-if="!disabledFlag"
              code="sourcingRequireBuyer:closePublicInfo"
              :disabled="!!form.closePublicReason"
              type="primary"
              @click="closePublicInfo"
            >
              <!-- 关闭公示信息 -->
              {{ $t("cusEntry.supplement20250121.closePublicInformation") }}
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
      v-if="publicInfoDialogVisible"
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
      @offertoFun="offertoFun"
    />

    <ApplyDialog ref="applyDialog" :visible.sync="applyVisible" :editRows="currentRow" />
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
import ApplyDialog from './components/quote/applyDialog'
import souHttp from '../../api'
import { transformMQL } from 'lib@/utils/util'
import { mapGetters } from 'vuex'
import edit from 'modcs@/biddingSupplier/views/sourcingCooperation/edit'
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
    InvoiceDialog,
    ApplyDialog
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
          // label: '供应商名称'
          label:  () => this.$t("common.companyName")
        },
        {
          prop: 'applyStatus',
          // label: '报名状态',
          label: () => this.$t("bidMod.signUpStatus"),
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
      applyVisible: false,
      mode: 'quit', // 退款:quit,查看历史:history
      totalQuote: 0,
      souHttp,
      transformData: (data) => {
        console.log('data', data.data[0])
        const { souReqApplyList, ...rest } = data.data[0]
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
    ...mapGetters['userInfo'],
    urlParams () {
      return this.$attrs.params || {}
    },
    disabledFlag () {
      return ['SIGNUP_DONE', 'ABANDON'].includes(this.form.status)
    }
  },
  created () {
    this.tableHeader = [
      {
        prop: 'vendorCode',
        // label: '供应商编码',
        label: () => this.$t("common.vendorCode"),
        minWidth: 130
      },
      {
        prop: 'vendorName',
        // label: '报名供应商',
        label: () => this.$t("sourcingBuyer.vendorName"),
        minWidth: 150
      },
      // {
      //   prop: 'isInternalVendor',
      //   label: '是否内部供应商',
      //   minWidth: 130,
      //   dataType: 'dict',
      //   code: 'YES_OR_NO'
      // },
      {
        prop: 'applyStatus',
        // label: '报名状态',
        label: () => this.$t("bidMod.signUpStatus"),
        minWidth: 120,
        dataType: 'dict',
        code: 'SOU_REQ_APPLY_STATUS'
      },
      {
        prop: 'applyContactName',
        // label: '报名联系人',
        label: () => this.$t("cusEntry.common.signUpPerson"),
        minWidth: 120
      },
      {
        prop: 'applyPhone',
        // label: '报名联系方式',
        label: () => this.$t("cusEntry.supplement20250121.registrationContactInformation"),
        minWidth: 130
      },
      {
        prop: 'applyEmail',
        // label: '邮箱',
        label: () => this.$t("common.email"),
        minWidth: 130
      },
      // {
      //   prop: 'depositFileId',
      //   label: '意向金缴纳凭证',
      //   minWidth: 130,
      //   showType: 'slot',
      //   slot: 'depositFileId'
      // },
      // {
      //   prop: 'depositStatus',
      //   label: '意向金缴纳状态',
      //   minWidth: 130,
      //   dataType: 'dict',
      //   code: 'SOU_INT_DEPOSIT_STATUS'
      // },
      // {
      //   prop: 'vendorBankAccount',
      //   label: '供应商缴纳账户',
      //   minWidth: 130
      // },
      // {
      //   prop: 'vendorBankAccountName',
      //   label: '缴纳户名',
      //   minWidth: 130
      // },
      // {
      //   prop: 'vendorBankName',
      //   label: '缴纳银行',
      //   minWidth: 130
      // },
      // {
      //   prop: 'vendorBankNumber',
      //   label: '银行联行号',
      //   minWidth: 130
      // },
      // {
      //   label: '意向金开票详情',
      //   minWidth: 130,
      //   showType: 'button',
      //   btnStyle: 'text',
      //   formattor: () => '查看详情',
      //   show: row => {
      //     // 报名确认中才显示
      //     return true
      //   },
      //   callback: (row) => {
      //     this.currentRow = row
      //     this.invoiceDialogVisible = true
      //   }
      // },
      {
        prop: 'signupTime',
        // label: '报名时间',
        label: () => this.$t("sourcingBuyer.signUpTime"),
        minWidth: 130,
        dataType: 'dateTime'
      },
      {
        prop: 'isAgent',
        // label: '是否代理',
        label: () => this.$t("cusEntry.supplement20250121.whetherToActAsAnAgent"),
        minWidth: 120,
        dataType: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'agentBrand',
        // label: '代理品牌',
        label: () => this.$t("vendorMod.agencyBrand"),
        minWidth: 120
      },
      {
        // label: '报名详情',
        label: this.$t("sourcingBuyer.signUpDetail"),
        minWidth: 130,
        showType: 'button',
        btnStyle: 'text',
        fixed: 'right',
        // formattor: () => '查看详情',
        formattor: () => this.$t("orderMod.viewDetail"),
        show: row => {
          return true
        },
        callback: (row) => {
          this.currentRow = {
            ...row,
            flag: 'view'
          }
          this.applyVisible = true
        }
      },
      {
        prop: 'applyHandleType',
        // label: '报名处理方式',
        label: () => this.$t("cusEntry.reportManagement.applyHandleType"),
        minWidth: 130,
        dataType: 'dict',
        code: 'SOU_APPLY_HANDLE_TYPE'
      },
      {
        prop: 'applyHandleReason',
        // label: '报名处理原因',
        label: () => this.$t("cusEntry.reportManagement.applyHandleReason"),
        minWidth: 120
      },
      {
        prop: 'withdrawReason',
        // label: '报名撤回原因',
        label: () => this.$t("cusEntry.supplement20250121.reasonForWithdrawalOfRegistration"),
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
            show: row => {
              return row.applyStatus === 'CONFIRMING_SIGNUP'
            },
            // formattor: () => '报名处理',
            formattor: () => this.$t("cusEntry.supplement20250121.registrationProcessing"),
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
    offertoFun (row) {
      this.invoiceDialogVisible = false
      this.$emit('tab-add', {
        component: edit,
        params: {
          flag: 'view',
          row: [row],
          tabName: 'sourcing' + row.invoiceNo,
          activeWorkflowTab: true
        },
        title: row.invoiceNo,
        name: 'sourcing' + row.invoiceNo
      })
    },
    /* 查询列表数据 */
    getQueryData (params = {}) {
      // const { responsibilityUserId } = this.urlParams.row
      // let isResponsibility = this.$store.getters.userInfo.userId === responsibilityUserId
      let isResponsibility = true
      let query = {
        '*': {}
      }
      if (isResponsibility) {
        query = {
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
                },
                $not: {
                  applyStatus: {
                    eq: 'NO_SIGNUP'
                  }
                }
              }
            }
          },
          'fileUploads': {
            '*': {}
          }
        }
      }
      this.queryParam = transformMQL.save('SouReqHeadBuyer', [{
        reqHeadId: this.reqHeadId
      }], 'read',
      query)

      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },
    handleCurrentChange (val) {
      this.selectedRows = val
    },
    handleDeadline (flag) {
      if (flag) {
        this.getQueryData()
      }
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
      this.$nextTick(() => {
        this.$refs.publicInfoDialog.resetFields()
      })
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
    async vendorRecommend () {
      // if (!this.selectedRows || !this.selectedRows.length) return this.$message.warning('请勾选报名列表')
      if (!this.selectedRows || !this.selectedRows.length) return this.$message.warning(this.$t('cusEntry.supplement20250121.pleaseCheckTheRegistrationList'))
      for (let item of this.selectedRows) {
        if (item.applyStatus !== 'SUCCESS_SIGNUP') {
          // return this.$message.warning('报名成功才可发起供应商推荐')
          return this.$message.warning(this.$t('cusEntry.supplement20250121.supplierRecommendationCanOnlyBeInitiatedAfterSuccessfulRegistration'))
        }
      }
      let reqApplyIdList = this.selectedRows.map(item => ({
        applyId: item.applyId
      }))
      let transformParams = transformMQL.save('SouReqApplyBuyer', reqApplyIdList, 'createVendorRecommend')
      const response = await souHttp.createVendorRecommend(transformParams)
      if (response && response.data.records.length) {
        const { projectId } = response.data.records[0]
        this.$router.push({
          name: 'recommendVendor',
          params: {
            from: 'sourcingRequireBuyer',
            row: {
              projectId
            }
          }
        })
      }
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
      // if (!this.selectedRows || !this.selectedRows.length) return this.$message.warning('请勾选列表')
      if (!this.selectedRows || !this.selectedRows.length) return this.$message.warning(this.$t('outsource.pleaseCheckList'))
      for (let item of this.selectedRows) {
        if (item.depositStatus !== 'PAID') {
          // this.$message.warning('仅可勾选已缴纳意向金的供应商发起退款')
          this.$message.warning(this.$t('cusEntry.supplement20250121..onlySuppliersWhoHaveAlreadyPaidTheEarnestMoneyCanBeSelectedToInitiateRefunds'))
          return
        }
      }
      // 退款中、退款成功不允许再次发起退款/开票中、开票成功不允许退款
      let validRefundStatus = true
      let errorMsg = ''
      this.selectedRows.some(item => {
        if (['REFUNDING', 'REFUNDED'].includes(item.depositRefundStatus)) {
          validRefundStatus = false
          errorMsg = this.$t('cusEntry.tipMessage.validRefundStatusMsg')
          return true
        }
        // if (item.invoiceStatus) {
        //   validRefundStatus = false
        //   errorMsg = this.$t('cusEntry.tipMessage.invoiceStatusMsg')
        //   return true
        // }
      })
      if (!validRefundStatus) {
        this.$message.warning(errorMsg)
        return false
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
