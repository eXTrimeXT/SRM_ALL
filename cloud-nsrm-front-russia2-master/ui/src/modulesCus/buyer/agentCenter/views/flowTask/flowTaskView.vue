<template>
  <el-container>
    <component
      :is="compName"
      :params="formQuery"
    />
  </el-container>
</template>
<script>
import { getToken, setRedirectUrl } from '@/utils/auth'
export default {
  name: 'FlowTaskViewBase',
  data () {
    return {
      isMobile: false,
      flowPage: {}
    }
  },
  computed: {
    query () {
      // let base64 = 'ZnJvbT1mcm9tRnVuJmZ1bk5hbWU9YmlkUHJpY2VDb21wYXJpc29uJmZvcm1JZD0xJmZvcm1Obz3moIfpopg='.replaceAll('+', '-').replaceAll('/', '_').replaceAll('=', '.')
      // this.$router.push(`/flowTaskViewBase/${base64}`)
      // const atobId = atob(this.$route.params.id.replaceAll('-', '+').replaceAll('_', '/').replaceAll('.', '='))
      const query = this.$route.query || {}
      // const keyValueArr = atobId.split('&')
      // keyValueArr.forEach(keyValue => {
      //   const [key, value] = keyValue.split('=')
      //   const decodedKey = decodeURIComponent(key)
      //   const decodedValue = decodeURIComponent(value)
      //   query[decodedKey] = isNaN(decodedValue) ? decodedValue : Number(decodedValue)
      // })
      return query
    },
    compName () {
      let funName = this.query.funName // this.$route.query.funName
      let myComponent = null
      switch (funName) {
      case 'dashboard':
        this.$router.push('/')
        break
      case 'supplierLimitation':
        // 合作终止
        myComponent = () => import('modcb@/vendorManagementBuyer/views/cooperationEndedEngine/cooperationEndedDetail')
        break
      case 'MQL_PR_SOU_REQUIREME':
        // 采购需求提报-招标立项
        myComponent = () => import('modcb@/purchasingDemand/views/purchaseApplication/purchaseApplicationDetailZhaobiao')
        break
      case 'MQL_PR_REQUIREMENT_I':
        // 采购需求提报-临采需求
        myComponent = () => import('modcb@/purchasingDemand/views/purchaseApplication/purchaseApplicationDetail')
        break
      case 'souReqHead':
        // 寻源报名大厅
        myComponent = () => import('modcb@/sourcing/views/sourcingRequireBuyer/edit')
        break
      case 'InviteVendor':
        // 供应商库
        myComponent = () => import('modcb@/vendorManagementBuyer/views/vendorProfileEngine/vendorProfileDetailReadEngine')
        break
      case 'RCOMMVENDOR':
        // 推荐供应商
        myComponent = () => import('modcb@/supplierRecommend/views/recommendVendor/edit')
        break
      case 'bidDataSubmit':
        // 招标资料提交
        myComponent = () => import('modcb@/purchasingDemand/views/biddingDocuments/edit')
        break
      case 'SOU_CA':
        // 定标申请
        myComponent = () => import('modcb@/caManagement/views/calibrationApply/edit')
        break
      case 'SOU_DCA':
        // 定标废弃申请
        myComponent = () => import('modcb@/caManagement/views/calibrationApplyAbandon/edit')
        break
      case 'SOU_TN':
        // 中落标通知
        myComponent = () => import('modcb@/bidNoticeManagement/views/bidNotice/edit')
        break
      case 'SOU_ANT':
        // 中落标废弃申请
        myComponent = () => import('modcb@/bidNoticeManagement/views/bidNoticeAbandon/edit')
        break
      case 'EXT_SOU_FIX_PRICE':
        // 询比价定价单
        myComponent = () => import('modcb@/inquiry/views/priceOrders/priceOrderDetail')
        break
      case 'MATERIAL_ADD':
        // 物料查询
        myComponent = () => import('modc@/common/baseSettingCommon/views/materialMaintenance/materialMaintenanceDetail')
        break
      case 'quaBusReview':
        // 供应商资质审查
        myComponent = () => import('modcb@/vendorManagementBuyer/views/quaOfReviewEngine/quaOfReviewDetail')
        break
      case 'black':
        // 黑名单审核
        myComponent = () => import('modcb@/vendorManagementBuyer/views/blackEngine/edit-engine')
        break
      case 'BlackRescind':
        // 黑名单解除
        myComponent = () => import('modcb@/vendorManagementBuyer/views/blackSecure/edit-engine')
        break
      case 'MQL_SOU_EXPERT_APPLY':
      case 'MQL_SOU_EXPERT_CHANG':
        // 专家信息审核
        myComponent = () => import('modcb@/expertLibrary/views/expertApply/edit')
        break
// ====================================以上为长城俄罗斯项目改造============================================
      case 'BID_PROCESS':
      // 招标流程
        myComponent = () =>
          import(
            '@/modulesCus/buyer/biddingBuyer/views/biddingManagement/biddingDetail'
          )
        break
      case 'REQUIREMENT':
        // 采购需求提报
        myComponent = () =>
          import(
            'modcb@/purchasingDemand/views/purchaseApplication/purchaseApplicationDetail'
          )
        break
      case 'MQL_PR_SOU_REQ_CANCEL_INIT':
        // 招标计划取消
        myComponent = () =>
          import(
            'modcb@/purchasingDemand/views/withdrawZhaobiao/edit'
          )
        break
      case 'CONTRACT':
        // 合同审批
        myComponent = () =>
          import('modcb@/contractManagement/views/contractManager/edit-engine.vue')
        break
      case 'performAcceptance':
        // 合同验收
        myComponent = () =>
          import('modcb@/contractPerformance/views/contractPerformanceCheck/edit')
        break
      case 'BORROW':
        // 借阅申请审批
        myComponent = () =>
          import(
            'modcb@/loanManagement/views/loanApply/detail.vue'
          )
        break
      case 'INSPECT_APPLY':
        // 考察申请
        myComponent = () =>
          import(
            'modcb@/inspectManagement/views/inspectManage/applyDetail.vue'
          )
        break
      case 'INSPECT_REPORT':
        // 考察报告
        myComponent = () =>
          import(
            'modcb@/inspectManagement/views/inspectManage/reportDetail.vue'
          )
        break
      case 'RECRUIT':
        // 招募管理
        myComponent = () =>
          import(
            'modcb@/recruitmentManagement/views/recruitment/detail.vue'
          )
        break
      case 'EXT_SOU_PUR_FIX_PRICE':
        // 集采定价单 (协议定价单)
        myComponent = () =>
          import(
            'modcb@/jcManagement/views/pricing/detail.vue'
          )
        break
      case 'designPlan':
        // 集采策划方案审批
        myComponent = () =>
          import(
            'modcb@/jcAgreement/views/chDesignPlan/edit.vue'
          )
        break
      case 'BIDDING_RESULT':
        // 竞价-定商定价审批
        myComponent = () =>
          import(
            'modcb@/competition/views/competitionManageBuyer/competitionManagementDetail.vue'
          )
        break
      case 'BIDDING_SUCCESS':
        // 竞价-编制定标结果（中标通知）
        myComponent = () =>
          import(
            'modcb@/competition/views/competitionManageBuyer/competitionManagementDetail.vue'
          )
        break
      case 'PRICE_ADJUST_APPLY':
        // 协议调价申请
        myComponent = () =>
          import(
            'modcb@/jcAgreement/views/priceAdjustApply/edit.vue'
          )
        break
      case 'vendorRisk':
        myComponent = () =>
          import(
            'modcb@/supplierRecommend/views/recommendVendor/components/vendorRiskDetail.vue'
          )
        break
      case 'sourcingRequireBuyer':
        myComponent = () =>
          import(
            'modcb@/sourcing/views/sourcingRequireBuyer/sourcingApplicationDetailNew.vue'
            )
        break
      // bpm查看寻源需求
      case 'sourcingRequireDetail':
        myComponent = () =>
          import(
            'modcb@/sourcing/views/sourcingRequireBuyer/quote.vue'
            )
        break
      case 'bidTechScoreDetail':
        // 招标管理-招标详情-查看技术评分
        myComponent = () =>
          import(
            'modcb@/biddingBuyer/views/biddingManagement/biddingDetail/techManagement/techScoreDetail.vue'
          )
        break
      case 'bidTechScoreHistory':
        // 招标管理-招标详情-查看技术评分历史
        myComponent = () =>
          import(
            'modcb@/biddingBuyer/views/biddingManagement/biddingDetail/techManagement/techScoreHistory.vue'
          )
        break
      case 'bidPriceComparison':
        // 招标管理-招标详情-比价表
        myComponent = () =>
          import(
            'modcb@/biddingBuyer/views/biddingManagement/biddingDetail/businessManagement/priceComparison.vue'
          )
        break
      case 'bidHistoryCooperation':
        // 招标管理-招标详情-比价表
        myComponent = () =>
          import(
            'modcb@/biddingBuyer/views/biddingManagement/biddingDetail/businessManagement/historyCooperationInfo.vue'
            )
        break
      case 'OPEN_BUSINESS_BID':
        // 开商务标
        myComponent = () =>
          import(
            'modcb@/biddingBuyer/views/biddingManagement/mobile/business'
          )
        break
      case 'OPEN_TECHNICAL_BID':
        // 开技术标
        myComponent = () =>
          import(
            'modcb@/biddingBuyer/views/biddingManagement/mobile/technical'
          )
        break
      case 'bidPriceLibrary':
        // 招标价格库
        myComponent = () =>
          import(
            'modcb@/biddingBuyer/views/bidPriceLibrary/edit.vue'
          )
        break
      case 'purPaymentApply':
        // 付款申请单
        myComponent = () =>
            import(
              'modb@/purSettlement/views/purPaymentApply/paymentPlanDetail.vue'
            )
        break
      case 'advancePayment':
        // 预付款申请
        myComponent = () =>
            import(
              'modb@/purSettlement/views/advancePayment/advancePaymentDetail.vue'
            )
        break
      case 'purInvoiceSupplier':
        // 开票协同
        myComponent = () =>
            import(
              'mods@/purSettlementSupplier/views/purInvoiceSupplier/agentOnlineInvoiceDetail.vue'
            )
        break
      case 'agentOnlineInvoice':
        // 开票管理
        myComponent = () =>
            import(
              'modb@/purSettlement/views/agentOnlineInvoice/agentOnlineInvoiceDetail.vue'
            )
        break
      case 'purStatementBillSupplier':
        // 对账协同
        myComponent = () =>
            import(
              'mods@/purSettlementSupplier/views/purStatementBillSupplier/invoiceNoticeDetail.vue'
            )
        break
      case 'purInvoice':
        // 对账管理
        myComponent = () =>
            import(
              'modb@/purSettlement/views/purInvoice/invoiceNoticeDetail.vue'
            )
        break
      case 'nonQuaOfReview':
        // 非材-资质审查
        myComponent = () =>
            import(
              'modb@/vendorManagementBuyer/views/nonQuaOfReview/quaOfReviewDetail.vue'
            )
        break

      case 'siteAssessment':
        // 供应商评审流程
        myComponent = () =>
            import(
              'modb@/vendorManagementBuyer/views/siteAssessment/siteAssessmentDetail.vue'
            )
        break
      case 'purchaseApplication':
        // 采购申请创建
        myComponent = () =>
            import(
              'modb@/purchasingDemand/views/purchaseApplication/purchaseApplicationDetail.vue'
            )
        break
      case 'buyerPurchaseOrder':
        // 采购订单（订单管理）
        myComponent = () =>
            import(
              'modb@/orderManagementBuyer/views/buyerPurchaseOrder/purchaseOrderDetail.vue'
            )
        break
      case 'vendorInfoChange':
        // 供应商信息更改
        myComponent = () =>
            import(
              'modb@/vendorManagementBuyer/views/vendorInfoChange/vendorInfoChangeDetail.vue'
            )
        break
      case 'purchaseOrderChange':
        // 采购订单变更（订单管理）
        myComponent = () =>
            import(
              'modb@/orderManagementBuyer/views/purchaseOrderChange/purchaseOrderChangeDetail.vue'
            )
        break
      case 'contractManager':
        // 合同管理（查看）
        myComponent = () =>
            import('modb@/contractManagement/views/contractManager/edit-engine.vue')
        break
      case 'contractManager_alteration':
        // 合同管理（合同变更）
        myComponent = () =>
            import('modb@/contractManagement/views/contractManager/edit.vue')
        break
      case 'contractManager_agreements':
        // 合同管理（补充协议）
        myComponent = () =>
            import('modb@/contractManagement/views/contractManager/edit.vue')
        break
      case 'vendorGreenChannel':
        // 供应商绿色通道
        myComponent = () =>
            import(
              'modb@/vendorManagementBuyer/views/vendorGreenChannel/vendorGreenChannelDetail.vue'
            )
        break
      case 'performanceAssessment':
        // 供应商考核 、、绩效考核
        myComponent = () =>
            import(
              'modb@/performanceManagement/views/performanceAssessment/assessmentDetail.vue'
            )
        break
      case 'mouldCreate':
        // 磨具新建
        myComponent = () => import('modb@/mould/views/mouldheader/edit.vue')
        break
      case 'mouldUpdate':
        //  磨具更新
        myComponent = () => import('modb@/mould/views/mouldheader/edit.vue')
        break
      case 'mouldScrap':
        // 磨具报废
        myComponent = () => import('modb@/mould/views/mouldheader/mouldScrap.vue')
        break
      case 'mouldChange':
        //  磨具转移
        myComponent = () => import('modb@/mould/views/mouldheader/mouldChange.vue')
        break
      case 'blacktemporary':
        // 黑名单临时业务
        myComponent = () => import('modb@/vendorManagementBuyer/views/blacktemporary/edit.vue')
        break
      case 'hierarchicalReview':
        // 供应商等级
        myComponent = () => import('modb@/vendorHierarchicalManagement/views/hierarchicalReview/hierarchicalReviewDeatil.vue')
        break
      case 'nonSiteAssessment':
        // 非材-供应商评审
        myComponent = () => import('modb@/vendorManagementBuyer/views/nonSiteAssessment/siteAssessmentDetail.vue')
        break
      case 'quotaFlow':
        // 配额审批
        myComponent = () => import('modb@/quotaManagement/views/quotaFlow/edit.vue')
        break
      case 'materialTrial':
        // 物料试用
        myComponent = () => import('modb@/vendorManagementBuyer/views/materialTrial/materialTrialDetail.vue')
        break
      case 'sampleConfirmed':
        // 样品确认
        myComponent = () => import('modb@/vendorManagementBuyer/views/sampleConfirmed/sampleConfirmedDetail.vue')
        break
      case 'siteReviewPlanConfirm':
        // 计划落实管理
        myComponent = () => import('modb@/vendorManagementBuyer/views/siteReviewPlanConfirm/edit.vue')
        break
      case 'inquiryCreate':
        // 简易询价 - 立项
        myComponent = () => import('modb@//inquiry/views/inquiryManagement/inquiryDetail')
        break
      case 'bargainCreate':
        // 项目式询价 - 立项
        myComponent = () => import('modb@/bargain/views/bargainManagement/bargainDetail.vue')
        break
      case 'bidingCreate':
        // 招标管理 - 立项
        myComponent = () => import('modb@/bidding/views/biddingManagement/biddingDetail.vue')
        break
      case 'purchaseDirectoryChange':
        // 货源变更
        myComponent = () => import('modb@/vendorManagementBuyer/views/purchaseDirectoryChange/purchaseDirectoryChangeDetail.vue')
        break
      case 'contractPerformancePlan':
        // 合同履约计划
        myComponent = () => import('modb@/contractPerformance/views/contractPerformancePlan/edit-engine.vue')
        break
      case 'questManagement':
        // 调查表管理
        myComponent = () => import('modb@/vendorManagementBuyer/views/questManagement/questManagementFlow.vue')
        break
      case 'financialInforChanges':
        // 财务信息变更
        myComponent = () => import('modb@/vendorManagementBuyer/views/financialInforChanges/edit.vue')
        break
      case 'contractPerformanceCheck':
        // 合同验收
        myComponent = () => import('modb@/contractPerformance/views/contractPerformanceCheck/edit.vue')
        break
      case 'sourcingApplicationBuyer':
        // 寻源需求报名审批
        myComponent = () => import('modb@/sourcing/views/sourcingApplicationBuyer/vendorSignUp.vue')
        break
      case 'outsourceMaterialChange':
        // 委外用料清单变更
        myComponent = () => import('modb@/outsourcingManagementNew/views/outsourceMaterialListChange/detail.vue')
        break

      default:
        myComponent = null // () => import(`modb@/agentCenter/views/flowTask/blankPage.vue`)
      }
      return myComponent
    },
    formQuery () {
      let funName = this.query.funName // this.$route.query.funName
      let formId = Number(this.query.formId)
      let status = this.query.status || '1' // 待审批状态，2：起草，1：执行中
      let query = {}
      console.log('funName', funName)
      switch (funName) {
      case 'dashboard':
        break
      case 'supplierLimitation':
        // 合作终止
        query = {
          flag: status == '1' ? 'view' : 'edit',
          orderId: formId,
          row: { orgCatFormId: formId }
        }
        break
      case 'MQL_PR_SOU_REQUIREME':
        // 采购需求提报-招标立项
        query = {
          flag: status == '1' ? 'approvalOnly' : 'edit',
          row: { requirementHeadId: formId },
          activeWorkflowTab: true
        }
        if (status == '1') query.showType = 'readOnly'
        break
      case 'MQL_PR_REQUIREMENT_I':
        // 采购需求提报-临采需求
        query = {
          flag: status == '1' ? 'approvalOnly' : 'edit',
          row: { requirementHeadId: formId },
          activeWorkflowTab: true
        }
        if (status == '1') query.showType = 'readOnly'
        break
      case 'souReqHead':
        // 寻源报名大厅
        query = {
          flag: status == '1' ? 'approval' : 'edit',
          row: { reqHeadId: formId },
          activeWorkflowTab: true
        }
        break
      case 'InviteVendor':
        // 供应商库
        query = {
          flag: 'approval',
          companyId: formId
        }
        break
      case 'RCOMMVENDOR':
        // 推荐供应商
        query = {
          flag: status == '1' ? 'approval' : 'edit',
          row: { projectId: formId }
        }
        break
      case 'bidDataSubmit':
        // 招标资料提交
        query = {
          flag: 'edit',
          row: { dataSubmitId: formId },
          activeWorkflowTab: true,
          readOnly: status == '1'
        }
        break
      case 'SOU_CA':
        query = {
          flag: status == '1' ? 'approval' : 'edit',
          row: { caId: formId },
          activeWorkflowTab: true
        }
        break
      case 'SOU_DCA':
        query = {
          flag: status == '1' ? 'approval' : 'edit',
          row: { caId: formId },
          activeWorkflowTab: true
        }
        break
      case 'SOU_TN':
        query = {
          isThirdParty: true,
          flag: status == '1' ? 'approval' : 'edit',
          row: { bidNoticeId: formId },
          activeWorkflowTab: true
        }
        break
      case 'SOU_ANT':
        query = {
          flag: status == '1' ? 'approval' : 'edit',
          row: { bidNoticeId: formId },
          activeWorkflowTab: true
        }
        break
      case 'EXT_SOU_FIX_PRICE':
        // 询比价定价单
        query = {
          type: status == '1' ? 'approve' : 'edit',
          row: { fixPriceHeadId: formId }
        }
        break
      case 'MATERIAL_ADD':
        // 物料查询
        query = {
          flag: status == '1' ? 'approval' : 'edit',
          row: { materialId: formId }
        }
        break
      case 'quaBusReview':
        // 供应商资质审查
        query = {
          flag: status == '1' ? 'view' : 'edit',
          row: { reviewFormId: formId }
        }
        break
      case 'black':
        // 黑名单审核
        query = {
          flag: status == '1' ? 'view' : 'edit',
          row: { blackId: formId }
        }
        break
      case 'BlackRescind':
        // 黑名单解除
        query = {
          flag: status == '1' ? 'view' : 'edit',
          row: { rescindId: formId }
        }
        break
      case 'MQL_SOU_EXPERT_APPLY':
      case 'MQL_SOU_EXPERT_CHANG':
        // 专家信息审核
        query = {
          flag: status == '1' ? 'approval' : 'edit',
          row: {
            expertApplyId: formId,
            applyFromType: funName === 'MQL_SOU_EXPERT_APPLY' ? 'INDEPENDENT' : 'GREEN_CHANNEL'
          }
        }
        break
//=================================以上为长城俄罗斯项目改造=========================================
      case 'BID_PROCESS':
        query = {
          flag: 'edit',
          row: {
            projectId: this.query.formId
          }
        }
        break
      case 'REQUIREMENT':
        query = {
          hidden: true,
          isMobile: this.isMobileDevice(),
          showType: 'readOnly',
          row: {
            requirementHeadId: this.query.formId
          }
        }
        break
      case 'MQL_PR_SOU_REQ_CANCEL_INIT':
        query = {
          showType: 'readOnly',
          row: {
            requirementCancelId: this.query.formId
          }
        }
        break
      case 'CONTRACT':
        query = {
          isReadOnly: true,
          termination: this.query.contractType === 'TERMINATION',
          flag: 'view',
          contractType: this.query.contractType,
          row: {
            contractHeadId: this.query.formId
          }
        }
        break
      case 'performAcceptance':
        query = {
          flag: 'view',
          row: {
            perAcceptanceId: this.query.formId
          }
        }
        break
      case 'BORROW':
        // 借阅申请审批
        query = {
          flag: 'approval',
          approvalFlag: true,
          row: { borrowId: this.query.formId }
        }
        break
      case 'INSPECT_APPLY':
        // 考察申请
        query = {
          flag: 'approval',
          approvalFlag: true,
          row: { inspectId: this.query.formId }
        }
        break
      case 'INSPECT_REPORT':
        // 考察报告
        query = {
          flag: 'approval',
          approvalFlag: true,
          row: { inspectId: this.query.formId }
        }
        break
      case 'RECRUIT':
        // 招募管理
        query = {
          flag: 'approval',
          approvalFlag: true,
          row: { recruitId: this.query.formId }
        }
        break
      case 'EXT_SOU_PUR_FIX_PRICE':
        // 集采定价单 (协议定价单)
        query = {
          type: 'view',
          approvalFlag: true,
          row: { purFixPriceHeadId: this.query.formId }
        }
        break
      case 'designPlan':
        // 集采策划方案审批
        query = {
          flag: 'view',
          approvalFlag: true,
          tabVal: 'tab2',
          row: { designId: this.query.formId }
        }
        break
      case 'BIDDING_RESULT':
        // 竞价-定商定价审批
        query = {
          flag: 'view',
          approvalFlag: true,
          row: { projectId: this.query.formId }
        }
        break
      case 'BIDDING_SUCCESS':
        // 竞价-编制定标结果（中标通知）
        query = {
          flag: 'view',
          approvalFlag: true,
          row: { projectId: this.query.formId }
        }
        break
      case 'PRICE_ADJUST_APPLY':
        // 协议调价申请
        query = {
          flag: 'view',
          approvalFlag: true,
          row: { adjustId: this.query.formId }
        }
        break
      case 'vendorRisk':
        query = {
          caId: this.query.formId
        }
        break
      case 'sourcingRequireBuyer':
        query = {
          row: {
            reqHeadId: this.query.formId
          }
        }
        break
      case 'sourcingRequireDetail':
        query = {
          row: {
            reqHeadId: this.query.formId
          }
        }
        break
      case 'bidTechScoreDetail':
        // 招标管理-招标详情-查看技术评分
        query = {
          editRow: {
            flag: 'approval',
            projectId: this.query.formId,
            groupId: this.query.groupId || null,
            souName: this.query.souName || '',
            from: this.query.from || ''
          }
        }
        break
      case 'bidTechScoreHistory':
        // 招标管理-招标详情-查看技术评分历史
        query = {
          row: {
            projectId: this.query.formId,
            groupId: this.query.row.groupId || null,
            extProjectNo: this.query.extProjectNo || '',
            souName: this.query.souName || ''
          },
          groupRoleList: this.query.groupRoleList
        }
        break
      case 'bidPriceComparison':
        // 招标管理-招标详情-比价表
        query = {
          projectId: this.query.formId // this.$route.query.formId
        }
        break
      case 'bidHistoryCooperation':
        // 招标管理-招标详情-比价表
        query = {
          idList: this.query.formId // this.$route.query.formId
        }
        break
      case 'OPEN_BUSINESS_BID':
        // 开商务标
        query = {
          projectId: this.query.formId, // this.$route.query.formId
          round: this.query.round
        }
        break
      case 'OPEN_TECHNICAL_BID':
        // 开技术标
        query = {
          projectId: this.query.formId // this.$route.query.formId
        }
        break
      case 'bidPriceLibrary':
        // 招标价格库
        query = {
          flag: 'view',
          approvalFlag: true,
          row: { bidPriceId: this.query.formId }
        }
        break
      case 'purPaymentApply':
        // 付款申请单
        query = {
          paymentApplyId: this.$route.query.formId,
          flag: 'approvalOnly'
        }
        break
      case 'advancePayment':
        // 预付款申请
        query = {
          row: { advanceApplyId: this.$route.query.formId },
          flag: 'readOnly',
          showType: 'approvalOnly',
          isOnlyRead: true
        }
        break
      case 'purInvoiceSupplier':
        // 开票协同
        query = {
          onlineInvoiceId: this.$route.query.formId,
          flag: 'approvalOnly',
          isOnlyRead: true
        }
        break
      case 'agentOnlineInvoice':
        // 开票管理
        query = {
          onlineInvoiceId: this.$route.query.formId,
          flag: 'approvalOnly',
          isOnlyRead: true
        }
        break
      case 'purStatementBillSupplier':
        // 对账协同
        query = {
          invoiceNoticeId: this.$route.query.formId,
          flag: 'approvalOnly',
          isOnlyRead: true
        }
        break
      case 'purInvoice':
        // 对账管理
        query = {
          invoiceNoticeId: this.$route.query.formId,
          flag: 'approvalOnly',
          isOnlyRead: true
        }
        break
      case 'nonQuaOfReview':
        // 非材-资质审查
        query = {
          row: { reviewFormId: this.$route.query.formId },
          reviewFormId: this.$route.query.formId,
          flag: 'view',
          isOnlyRead: true
        }
        break
      case 'siteAssessment':
        // 供应商评审流程
        query = {
          row: { siteFormId: this.$route.query.formId },
          siteFormId: this.$route.query.formId,
          reviewFormId: this.$route.query.reviewFormId,
          flag: 'view'
        }
        break
      case 'purchaseApplication':
        // 采购申请创建
        query = {
          row: { requirementHeadId: this.$route.query.formId },
          flag: 'approvalOnly',
          showType: 'readOnly'
        }
        break
      case 'buyerPurchaseOrder':
        // 采购订单
        query = {
          row: { orderId: this.$route.query.formId },
          flag: 'approvalOnly',
          showType: 'readOnly'
        }
        break
      case 'vendorInfoChange':
        // 供应商信息更改
        query = {
          changeId: this.$route.query.formId,
          flag: 'view',
          router: '/vendorManagement/vendorInfoChange',
          ouptCup: 'view'
        }
        break
      case 'purchaseOrderChange':
        // 采购订单变更
        query = {
          row: { orderChangeId: this.$route.query.formId },
          orderChangeId: this.$route.query.formId,
          flag: 'approvalOnly',
          showType: 'readOnly'
        }
        break
      case 'contractManager':
        //  合同管理（查看）
        query = {
          row: { contractHeadId: this.$route.query.formId },
          rowId: this.$route.query.formId,
          illegal: 'view',
          flag: 'edit',
          isReadOnly: true,
          jumpLogin: false
        }
        break
      case 'contractManager_alteration':
        //  合同管理（合同变更）
        query = {
          illegal: 'view',
          sourceId: 'get',
          contractType: 'MIAN_CONTRACT_ALTER',
          flag: 'add',
          isReadOnly: true,
          rowId: this.$route.query.formId,
          jumpLogin: false
        }
        break
      case 'contractManager_agreements':
        //  补充协议（合同变更）
        query = {
          illegal: 'view',
          contractType: 'SUPPLEMENTAL_AGREEMENT',
          sourceId: 'get',
          flag: 'add',
          isReadOnly: true,
          rowId: this.$route.query.formId,
          jumpLogin: false
        }
        break
      case 'inspectionBill':
        //  验收单列表
        query = {
          flag: 'readOnly',
          row: { acceptOrderId: this.$route.query.formId }
        }
        break
      case 'vendorGreenChannel':
        //  供应商绿色通道
        query = {
          flag: 'readOnly',
          isReadOnly: true,
          companyId: this.$route.query.formId
        }
        break
      case 'performanceAssessment':
        //  供应商考核
        query = {
          flag: 'view',
          orderId: this.$route.query.formId,
          tabName: 'assessmentDetail' + this.$route.query.formId,
          isReadOnly: true
        }
        break
      case 'mouldCreate':
        //  磨具新增
        query = {
          row: { mouldHeaderId: this.$route.query.formId },
          tabName: 'mouldheader_update' + this.$route.query.formId,
          flag: 'edit',
          readOnly: true
        }
        break
      case 'mouldUpdate':
        //  磨具更新
        query = {
          row: { mouldFlowLogId: this.$route.query.formId },
          tabName: 'mouldheader_update' + this.$route.query.formId,
          flag: 'update',
          readOnly: true,
          firstFlag: false
        }
        break
      case 'mouldScrap':
        //  磨具报废
        query = {
          row: { mouldHeaderId: this.$route.query.formId },
          mouldFlowLogId: this.$route.query.formId,
          tabName: 'mouldheader_update' + this.$route.query.formId,
          flag: 'view',
          readOnly: true
        }
        break
      case 'mouldChange':
        //  磨具转移
        query = {
          row: { mouldFlowLogId: this.$route.query.formId },
          mouldFlowLogId: this.$route.query.formId,
          tabName: 'mouldheader_update' + this.$route.query.formId,
          flag: 'view',
          readOnly: true
        }
        break
      case 'blacktemporary':
        // 黑名单临时业务
        query = {
          row: { blackTemporaryId: this.$route.query.formId },
          flag: 'doApproval',
          readOnly: true
        }
        break
      case 'hierarchicalReview':
        // 供应商等级
        query = {
          row: { vendorLevelApproveId: this.$route.query.formId },
          flag: 'view'
        }
        break
      case 'nonSiteAssessment':
        // 非材-供应商评审
        query = {
          row: { siteFormId: this.$route.query.formId },
          siteFormId: this.$route.query.formId,
          reviewFormId: this.$route.query.reviewFormId,
          flag: 'view'
        }
        break
      case 'quotaFlow':
        // 配额审批
        query = {
          row: { quotaFlowId: this.$route.query.formId },
          isReadonly: true,
          flag: 'edit'
        }
        break
      case 'materialTrial':
        // 物料试用
        query = {
          row: { materialTrialId: this.$route.query.formId },
          materialTrialId: this.$route.query.formId,
          isReadonly: true,
          flag: 'doApproval'
        }
        break
      case 'sampleConfirmed':
        // 样品确认
        query = {
          row: { sampleId: this.$route.query.formId },
          sampleId: this.$route.query.formId,
          isReadonly: true,
          flag: 'doApproval'
        }
        break
      case 'siteReviewPlanConfirm':
        // 计划落实管理
        query = {
          row: { planConfirmId: this.$route.query.formId },
          planConfirmId: this.$route.query.formId,
          isReadonly: true,
          flag: 'doApproval'
        }
        break
      case 'inquiryCreate':
        // 简易询价 - 立项
        query = {
          flag: 'approve',
          readOnly: true,
          row: { projectId: this.$route.query.formId }
        }
        break
      case 'bargainCreate':
        // 项目式询价 - 立项
        query = {
          flag: 'approval',
          readOnly: true,
          row: { projectId: this.$route.query.formId }
        }
        break
      case 'bidingCreate':
        // 招标管理 - 立项
        query = {
          flag: 'approval',
          readOnly: true,
          row: { projectId: this.$route.query.formId }
        }
        break
      case 'purchaseDirectoryChange':
        // 货源变更
        query = {
          flag: 'approval',
          row: { changeId: this.$route.query.formId }
        }
        break
      case 'contractPerformancePlan':
        // 合同履约计划
        query = {
          flag: 'approval',
          row: { perPlanId: this.$route.query.formId }
        }
        break
      case 'questManagement':
        // 调查表管理
        query = {
          flag: 'approvalOnly',
          row: { questSupId: this.$route.query.formId }
        }
        break
      case 'financialInforChanges':
        // 调查表管理
        query = {
          flag: 'approved',
          row: { changeHeaderId: this.$route.query.formId }
        }
        break
      case 'contractPerformanceCheck':
        // 合同验收
        query = {
          flag: 'view',
          // fromContractPerformancePlan: true,
          row: { perAcceptanceId: this.$route.query.formId }
        }
        break
      case 'sourcingApplicationBuyer':
        // 寻源需求报名审批
        query = {
          flag: 'view',
          row: { reqHeadId: this.$route.query.formId }
        }
        break
      case 'outsourceMaterialChange':
        // 委外用料清单变更
        query = {
          flag: 'approvalOnly',
          row: { changeId: this.$route.query.formId }
        }
        break

      default:
        return {}
      }
      return query
    }
  },
  created () {
    const token = getToken()
    if (!token) { // 由于配了白名单 所以不会跳转登录页
      let redirectUrl = window.location.hash
      setRedirectUrl(redirectUrl)
      this.$router.push({ path: '/login' })
    }
  },

  methods: {
    // 判断是否移动端打开
    isMobileDevice () {
      let mobile = false
      if (navigator.userAgent.match(/(iPhone|iPod|Android|ios|iPad)/i)) {
        mobile = true
      }
      return mobile
    }
  }
}
</script>
<style scoped>
.order-list-btn {
  position: fixed;
  bottom: 0;
  left: 0;
  background: #fff;
  width: 100%;
  padding: 12px 16px;
  display: flex;
  justify-content: flex-end;
  z-index: 100;
}
.order-form-contain {
  display: block;
  /* width: 100%;
  margin: 0 0 58px 0;
  height: 100%; */
  /* min-width: 840px; */
}

.order-form-contain > .el-container.flex-container {
  /* height: 100%; */
}
.blankPage{
  margin:100px 50px;
  font-size:20px;
  line-height:40px;
  text-align:center;
}
</style>
