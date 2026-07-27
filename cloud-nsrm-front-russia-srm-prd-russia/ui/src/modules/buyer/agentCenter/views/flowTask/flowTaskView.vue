<template>
  <el-container class="order-form-contain">
    <component
      :is="compName"
      :params="formQuery"
    />
  </el-container>
</template>
<script>
import { getToken, setRedirectUrl } from '@/utils/auth'
export default {
  name: 'FlowTaskView',
  data () {
    return {
      isMobile: false,
      flowPage: {}
    }
  },
  computed: {
    compName () {
      let funName = this.$route.query.funName
      let myComponent = null
      switch (funName) {
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
      case 'quaOfReview':
        // 供应商资质审查
        myComponent = () =>
            import(
              'modb@/vendorManagementBuyer/views/quaOfReview/quaOfReviewDetail.vue'
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
      case 'black':
        // 黑名单审核
        myComponent = () => import('modb@/vendorManagementBuyer/views/black/edit.vue')
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
      case 'cooperationEnded':
        // 合作终止
        myComponent = () => import('modb@/vendorManagementBuyer/views/cooperationEnded/cooperationEndedDetail.vue')
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
      let funName = this.$route.query.funName
      let query = {}
      switch (funName) {
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
      case 'quaOfReview':
        // 供应商资质审查
        query = {
          row: { reviewFormId: this.$route.query.formId },
          reviewFormId: this.$route.query.formId,
          flag: 'view',
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
      case 'black':
        // 黑名单审核
        query = {
          row: { blackId: this.$route.query.formId },
          flag: 'edit',
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
      case 'cooperationEnded':
        // 合作终止
        query = {
          row: { orgCatFormId: this.$route.query.formId },
          orderId: this.$route.query.formId,
          isReadonly: true,
          flag: 'doApproval'
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
