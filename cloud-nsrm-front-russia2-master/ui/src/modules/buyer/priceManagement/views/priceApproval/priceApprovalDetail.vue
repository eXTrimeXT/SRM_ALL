<template>
  <el-container
    class="flex-container the_inquiryApprovalFlow_wrapper"
    direction="vertical"
  >
    <CWorkflowMulti
      ref="workflowMulti"
      v-model="activeTabName"
      :fun-params="workflowParamsInfo"
      :button-config-info="buttonConfigInfo"
      :button-custom="buttonCustom"
      @tab-click="workflowView"
      @workflow-handler="workflowHandler"
      @click-handler="type => saveOrSubmitBill(type)"
      @submit-direct="type => saveOrSubmitBill(type)"
      @confirm="(type, comment) => saveOrSubmitBill(type, comment)"
      @close-tab="backTo"
    >
      <!--表单-->
      <ApprovalHeader
        ref="approvalHeader"
        :approval-header="approvalHeader"
        :page-flag="pageFlag"
        :page-type="pageType"
        :approval-bidding-item-list="approvalBiddingItemList"
      />

      <el-divider />

      <!--筛选报表-->
      <template v-if="showReport">
        <ApprovalReport
          ref="approvalReport"
          :approval-header="approvalHeader"
          :attrs-params-row="attrsParams.row"
          :show-report="showReport"
          :page-type="pageType"
        />
        <el-divider />
      </template>

      <!--上传附件-->
      <template v-if="!pageType.isInquiry">
        <ApprovalFiles
          ref="approvalFiles"
          :page-flag="pageFlag"
          :page-type="pageType"
          :approval-file-list.sync="approvalFileList"
        />
        <el-divider />
      </template>

      <!--中标行信息-->
      <ApprovalBidding
        ref="approvalBidding"
        :page-flag="pageFlag"
        :page-type="pageType"
        :approval-header="approvalHeader"
        :approval-bidding-item.sync="approvalBiddingItemList"
        :attrs-params-row="attrsParams.row"
        :get-approval-header-id="getApprovalHeaderId"
      />

      <!--d 起草人意见-->
      <LoggerCommentDialog
        :visible.sync="loggerCommentDialogVisible"
        :page-type="pageType"
        :approval-bidding-item-list="approvalBiddingItemList"
        :approval-file-list="approvalFileList"
        :approval-header="approvalHeader"
        :callBackConfirm="callBackConfirm"
        @saveLoggerCommentSuccess="saveLoggerCommentSuccess"
      />

      <div v-if="pageFlag.isApproval" slot="buttonFive" style="margin-left: 8px;">
        <!--b 审批拒绝-->
        <el-button
          type="primary"
          @click="handlerApprovalStatus('overrule')"
        >
          {{ $t("bidMod.approvalRefuse") }}
        </el-button>

        <!--b 审批通过-->
        <el-button
          type="primary"
          @click="handlerApprovalStatus('approve')"
        >
          {{ $t("bidMod.approvalPass") }}
        </el-button>
      </div>
    </CWorkflowMulti>
  </el-container>
</template>

<script>
/**
 * 价格审批单，用于简易询价、招标、手工创建、项目式询价、竞价
 */
import WorkflowCommon from '@/library/mixins/workflow-common'
import { tabTodoMixin } from '@/utils/mixins'
import { getSaveAndSubmitApiUrl } from './composition'
import CToolbar from 'lib@/components/c-toolbar'
import LoggerCommentDialog from './priceApprovalDetail/loggerCommentDialog'
import ApprovalHeader from './priceApprovalDetail/approvalHeader'
import ApprovalFiles from './priceApprovalDetail/approvalFiles'
import ApprovalBidding from './priceApprovalDetail/approvalBidding'
import ApprovalReport from './priceApprovalDetail/approvalReport'

export default {
  name: 'PriceApprovalDetail',

  components: {
    CToolbar,
    LoggerCommentDialog,
    ApprovalHeader,
    ApprovalFiles,
    ApprovalBidding,
    ApprovalReport
  },

  mixins: [tabTodoMixin, WorkflowCommon],

  data () {
    return {
      approvalHeader: {
        approvalHeaderId: null,
        approvalNo: '',
        status: 'DRAFT',
        createdBy: null,
        sourceType: null,
        approvalTitle: null,
        sourceNo: null,
        awareWay: null,
        allocationType: 'NULL_ALLOCATION',
        ifUpdatePriceLibrary: 'Y',
        demandSummary: null,
        standardCurrency: '',
        bidAmount: '',
        description: null
      },
      approvalBiddingItemList: [],
      approvalFileList: [],
      funParams: {},
      editableTabsValue: 'tab1',
      getApprovalHeaderId: null,
      demandLineReports: [],
      vendorBiddingSummaryReports: [],
      isOpenFlow: false,
      loggerCommentDialogVisible: false,
      attrsParams: {
        flag: this.$attrs.params.flag,
        row: this.$attrs.params.row
      },
      flowBusinessId: '',
      callBackConfirm: null
    }
  },

  computed: {
    /* 当前页面状态 */
    pageFlag () {
      // 新增、编辑、只读、审批
      // flag: ['add', 'edit', 'readonly', 'approval']
      const flag = this.attrsParams.flag
      return {
        isAdd: flag === 'add',
        isEdit: flag === 'edit',
        isReadonly: flag === 'readonly',
        isApproval: flag === 'approval'
      }
    },

    /* 当前页面类型 */
    pageType () {
      // 默认手工创建
      const sourceType = this.approvalHeader.sourceType || 'HAND_MAKE'
      return {
        // 简易询价
        isInquiry: sourceType === 'INQUIRY',
        // 手工创建
        isHandMake: sourceType === 'HAND_MAKE',
        // 招标
        isBiding: sourceType === 'BIDING',
        // 项目式询价
        isBargain: sourceType === 'BARGAIN',
        // 竞价
        isCompetition: sourceType === 'COMPETITION',
        // 竞价pro
        isAuct:sourceType === "AUCT"
      }
    },

    /* 是否显示报表筛选 */
    showReport () {
      // 非手工创建
      return !this.pageFlag.isAdd && !this.pageType.isHandMake
    },

    // 用来控制保存、提交按钮是否可见。如果自定义按钮则无需添加
    viewUpdateButton () {
      return ['DRAFT', 'RESULT_REJECTED', 'WITHDRAW'].includes(this.approvalHeader.status) &&
              !this.pageFlag.isReadonly
    },
    disabledUpdateButton () {
      return (['RESULT_NOT_APPROVED'].includes(this.approvalHeader.status) ||
        this.pageFlag.isReadonly
      )
    },
    // 用来指定工作流的业务ID 单据ID 注意每一个单的ID不一样
    workflowBusinessId () {
      return this.flowBusinessId ? this.flowBusinessId : (this.approvalHeader.approvalHeaderId || null)
    },
    // 禁用流程tab状态
    workflowTabDisabled () {
      return (!this.approvalHeader.approvalHeaderId && (['DRAFT', 'RESULT_REJECTED', 'WITHDRAW'].includes(this.approvalHeader.status) || this.pageFlag.isReadonly)) ||
      !this.approvalHeader.approvalHeaderId
    }
  },

  watch: {
    // 监听保存提交 按钮变更状态，如果自定义按钮则无需添加
    viewUpdateButton () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
    },
    // 按钮禁用状态控制
    disabledUpdateButton () {
      this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton
    }
  },

  created () {
    if (!this.pageFlag.isAdd) {
      this.flowBusinessId = this.approvalHeader.approvalHeaderId

      if (!this.attrsParams.row.approvalHeaderId) {
        // '无法获取价格审批单ID'
        this.$message.warning(this.$t('bidMod.priceApprovalDocument'))
      } else {
        // 不是新增，查询数据
        this.getFormDetailData(this.attrsParams.row.approvalHeaderId)
      }
    }

    this.getButtonConfig()
  },

  methods: {
    getButtonConfig () {
      // 以下内容可控制取消、关闭、保存、提交是否显示。如果自定义按钮，则无需添加
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
      this.buttonConfigInfo.cancel.view = this.pageFlag.isReadonly
      this.buttonConfigInfo.close.view = !this.pageFlag.isReadonly
    },
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'PRICE_APPROVAL_INIT'
    },
    // 获取ref值
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    /* 初始化详情信息 */
    async getFormDetailData (approvalHeaderId) {
      await this.$http({
        url: `/api-inq/price/approval/getApprovalDetail/${approvalHeaderId}`,
        method: 'GET',
        loading: true
      }).then(data => {
        const {
          approvalFileList = [],
          approvalBiddingItemList,
          initWorkFlow,
          cbpmInstaceId
        } = data.data

        this.approvalHeader = data.data.approvalHeader || {}
        this.approvalFileList = approvalFileList

        this.approvalBiddingItemList = approvalBiddingItemList.map(item => {
          let parse = item.arrivalPlace
          if (parse) {
            try {
              parse = JSON.parse(item.arrivalPlace)
            } catch (e) {
              console.log(e)
            }
          }
          return {
            ...item,
            arrivalPlace: parse
          }
        })

        if (initWorkFlow === null) {
          this.isOpenFlow = false
        } else {
          this.isOpenFlow = true
          this.funParams = {
            ...initWorkFlow,
            flag: cbpmInstaceId ? 'edit' : 'add'
          }
        }
        if (cbpmInstaceId) {
          this.funParams.fdId = cbpmInstaceId
          this.isOpenFlow = true
        }
      })
    },

    /* 审批通过 */
    saveBillPass () {
      this.$http({
        url: '/api-inq/price/approval/auditPass',
        method: 'POST',
        params: { approvalHeaderId: this.attrsParams.row.approvalHeaderId },
        loading: true
      }).then(() => {
        this.$message({
          message: this.$t('vendorMod.approvalSuccess'),
          type: 'success'
        })

        this.backTo()
      })
    },

    /* 审批拒绝 */
    saveBillReject () {
      this.$http({
        url: '/api-inq/price/approval/reject',
        method: 'GET',
        params: { approvalHeaderId: this.attrsParams.row.approvalHeaderId },
        loading: true
      }).then(() => {
        this.$message.success(this.$t('bidMod.approvalRefuse'))

        this.backTo()
      })
    },

    // 审批流 驳回
    handlerApprovalStatus (nextStatus) {
      // 描述审批状态  start-提交审批/approve-审批通过/overrule-驳回/discard-废弃/recall-撤回
      const { userId } = this.$store.getters.user.userInfo
      console.log(userId, 'userId')
      this.$http({
        url: '/api-base/flow-anon/workflowcallback/ide/callback',
        method: 'PoST',
        data: {
          'actionCode': 'PRICE_APPROVAL_INIT',
          'currentUserId': userId,
          'formDataId': this.approvalHeader.approvalHeaderId,
          'handleType': nextStatus,
          'procInstId': null,
          'status': '3',
          'taskId': null,
          'taskKey': null
        },
        loading: true
      }).then(() => {
        this.$message.success(this.$t('common.success'))

        this.backTo()
      })
    },

    /* 校验提交表格 */
    async validateForm () {
      let validate = true
      // 先校验公共部分，如果不要就拿掉放在特定类型中校验
      validate = await this.$refs.approvalHeader.validateForm()
      if (!validate) {
        return validate
      }

      if (this.pageType.isInquiry) {
        // 简易询价
        validate = await this.validateInqForm()
      } else if (this.pageFlag.isAdd) {
        // 手动创建 校验approvalBidding
        validate = await this.$refs.approvalBidding.validateForm()
      }
      return validate
    },

    /* 校验简易询价校验表格 */
    validateInqForm () {
      return Promise.resolve(true)
    },

    /* 暂存 */
    async saveDetail () {
      const allParams = {
        approvalBiddingItemList: this.approvalBiddingItemList,
        approvalHeader: this.approvalHeader,
        approvalFileList: this.approvalFileList
      }
      this.$http({
        url: getSaveAndSubmitApiUrl(this.pageType).saveApi,
        method: 'POST',
        data: allParams,
        loading: true
      }).then(data => {
        this.$message.success(this.$t('common.successSave'))
        if (this.pageFlag.isAdd) {
          // 新增的
          this.attrsParams = {
            row: {
              ...this.attrsParams.row || {},
              approvalHeaderId: data.data.approvalHeaderId
            }
          }
        }

        this.getFormDetailData(data.data.approvalHeaderId)
        if (this.showReport) {
          // 刷新报表信息
          this.$refs.approvalReport.getApprovalReportData()
        }

        this.getApprovalHeaderId = data.data.approvalHeaderId
        this.approvalHeader.approvalHeaderId = data.data.approvalHeaderId
      })
    },

    /* 提交 */
    async submitDetail () {
      if (!await this.validateForm()) {
        return
      }

      this.loggerCommentDialogVisible = true

      // 返回一个promise在打开弹窗填写了审批人意见之后函数继续执行
      return new Promise((resolve) => {
        this.callBackConfirm = resolve
      })
    },

    saveLoggerCommentSuccess () {
      // this.backTo()
    },

    async saveOrSubmitBill (type) {
      console.log(type, 'type')
      if (type === 'SUBMIT') {
        const { approvalHeaderId } = await this.submitDetail()
        await this.getFormDetailData(approvalHeaderId)
        this.handlerAfter(type)
      } else {
        this.saveDetail()
      }
    },

    /* 返回 */
    backTo () {
      this.$emit(
        'tab-remove',
        `${!this.pageFlag.isAdd ? 'priceApprovalDetail' + this.attrsParams.row.approvalNo : 'priceApprovalDetail'}`
      )
      this.__setTabTodo('PriceApprovalList.getQueryData')
    }
  }
}
</script>

<style scoped lang="scss">
.topComment {
  text-align: center;
  margin-top: 10px;
}
.el-dialog__body {
  padding-top: 0 !important;
}
.the_approval_btns {
  button {
    float: right;
    margin: 5px;
  }
}
.check:hover {
  cursor: pointer;
}
.the_btn_wrapper {
  display: inline-block;
  width: 111px;
}
.importbtn :deep(.el-button) {
  min-width: 56px;
  height: 24px;
  line-height: 22px;
  font-size: 14px;
  border-radius: 2px;
  padding: 1px 14px;
}
</style>
