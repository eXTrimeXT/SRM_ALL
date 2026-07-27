<template>
  <!--  localhost:8080/#/formDataPreview?funName=purchaseApplication&formId=328713533341696   采购申请-->
  <!-- http://localhost:8080/#/formDataPreview?funName=quaOfReview&formId=316349346697344  供应商资质审查-->

  <!-- http://localhost:8080/#/formDataPreview?funName=siteAssessment&formId=318068473317504 供应商评审 -->
  <!-- http://localhost:8080/#/formDataPreview?funName=vendorInfoChange&formId=310286263994496  供应商信息变更(1) -->
  <!-- localhost:8080/#/formDataPreview?funName=crossOrgImport&formId=7904305291329536  供应商跨组织引入 -->
  <!-- localhost:8080/#/formDataPreview?funName=vendorGreenChannel&formId=294577207328768 供应商绿色通道 -->
  <!-- localhost:8080/#/formDataPreview?funName=performanceAssessment&formId=328893908068352 供应商考核-->

  <!-- localhost:8080/#/formDataPreview?funName=buyerPurchaseOrder&formId=7975618815197184 采购订单
  -->
  <!-- localhost:8080/#/formDataPreview?funName=purchaseOrderChange&formId=7976726550675456 采购订单变更 -->
  <!-- CR20200515000001 变更单号 -->

  <!-- 合同 -->
  <!-- localhost:8080/#/formDataPreview?funName=contractManager&formId=329045934647296  查询合同 -->
  <!-- localhost:8080/#/formDataPreview?funName=contractManager_alteration&formId=8021176737923072  变更合同-->
  <!-- localhost:8080/#/formDataPreview?funName=contractManager_agreements&formId=8021176737923072  补充协议-->

  <!-- localhost:8080/#/formDataPreview?funName=inspectionBill&formId=7989302780821504  验收单列表 开放权限 -->

  <!--   询比价项目管理  -->
  <!--  -->
  <!-- http://localhost:8080/#/formDataPreview?funName=inquiryByProjectListBuyer&formId=8216278766780416 -->
  <!-- http://localhost:8080/#/formDataPreview?funName=inquiryByProjectListBuyer&formId=8147118190362624 -->
  <!-- http://localhost:8080/cloud-srm/api-brg/bidInitiating/biding/get?id=8016602021560320 开放权限 -->
  <!--  -->
  <!-- localhost:8080/#/formDataPreview?funName=inquiryByProjectListBuyer&formId=324540533950464 -->
  <!-- localhost:8080/#/formDataPreview?funName=biddingProject&formId=329034414368768 招标 -->
  <!-- http://localhost:8080/cloud-srm/api-bid/bidInitiating/biding/get?id=8016472067145728 开放权限-->
  <!-- localhost:8080/#/formDataPreview?funName=inquiryApprovalFlow&formId=8095358099652608 价格审批单（寻源）-->
  <!-- 8197066467901952 -->
  <!-- PAN20103000003 -->
  <!-- localhost:8080/#/formDataPreview?funName=inquiryApprovalFlow&formId=8141364081786880 -->

  <!-- localhost:8080/#/formDataPreview?funName=inquiryOrders&inquiryId=7467604717338624&vendorId=1 询价单  *****NO-->
  <!-- localhost:8080/#/formDataPreview?funName=quotaModulation&formId=292284715259904 配额调整 ******NO-->
  <div>
    <el-container class="order-form-contain">
      <component
        :is="compName"
        :params="formQuery"
      />
    </el-container>
    <div class="order-list-btn">
      <el-button
        type="warning"
        size="mini"
      >
        审批
      </el-button>
      <el-button
        type="danger"
        size="mini"
      >
        驳回
      </el-button>
      <el-button
        type="primary"
        size="mini"
      >
        通过
      </el-button>
    </div>
  </div>
</template>
<script>
import Vue from 'vue'
export default {
  name: 'FormDataPreview',
  data () {
    return {
      isMobile: false,
      // tempName: "",
      formQuery: {}
      // funUrl:{
      //   quaOfReview: 'mod@/vendorManagementBuyer/views/quaOfReview/quaOfReviewDetail.vue', // 供应商资质审查
      //   siteAssessment: 'mod@/vendorManagementBuyer/views/siteAssessment/siteAssessmentDetail.vue', //供应商评审流程
      //   purchaseApplication:'mod@/purchasingDemand/views/purchaseApplication/purchaseApplicationDetail.vue',//采购申请创建
      //   buyerPurchaseOrder:'mod@/orderManagementBuyer/views/buyerPurchaseOrder/purchaseOrderDetail.vue',//采购订单

      // },

      // compName:null
    }
  },
  computed: {
    compName () {
      let fildUrl = ''
      if (this.isMobile) {
        let funName = this.$route.query.funName
        if (funName === 'purchaseApplication') {
          var myComponent = () => import('@/views/formDataPreview/mobileModule/purchaseApplication.vue')// 采购申请移动端
        } else if (funName === 'quaOfReview') {
          var myComponent = () => import('@/views/formDataPreview/mobileModule/quaOfReview.vue')// 资质审查
        } else if (funName === 'siteAssessment') {
          var myComponent = () => import('@/views/formDataPreview/mobileModule/siteAssessment.vue')// 供应商评审
        } else if (funName === 'vendorInfoChange') {
          var myComponent = () => import('@/views/formDataPreview/mobileModule/vendorInfoChange.vue')// 供应商信息变更
        } else if (funName === 'crossOrgImport') {
          var myComponent = () => import('@/views/formDataPreview/mobileModule/crossOrgImport.vue')// 供应商信息变更
        } else if (funName === 'buyerPurchaseOrder') {
          var myComponent = () => import('@/views/formDataPreview/mobileModule/buyerPurchaseOrder.vue')// 采购订单（订单管理）
        } else if (funName === 'purchaseOrderChange') {
          var myComponent = () => import('@/views/formDataPreview/mobileModule/purchaseOrderChange.vue')// 采购订单变更（订单管理）
        } else if (funName === 'contractManager') {
          var myComponent = () => import('@/views/formDataPreview/mobileModule/contractManager.vue')// 合同管理（查看）
        } else if (funName === 'contractManager_alteration') {
          var myComponent = () => import('@/views/formDataPreview/mobileModule/contractManager.vue')// 合同管理（合同变更）
        } else if (funName === 'contractManager_agreements') {
          var myComponent = () => import('@/views/formDataPreview/mobileModule/contractManager.vue')// 合同管理（补充协议）
        } else if (funName === 'inspectionBill') {
          var myComponent = () => import('@/views/formDataPreview/mobileModule/inspectionBill.vue')// 验收订单列表
        } else if (funName === 'inquiryOrders') {
          var myComponent = () => import('@/views/formDataPreview/mobileModule/inquiryOrders.vue')// 询价单
        } else if (funName === 'vendorGreenChannel') {
          var myComponent = () => import('@/views/formDataPreview/mobileModule/vendorGreenChannel.vue')// 供应商绿色通道
        } else if (funName === 'inquiryByProjectListBuyer') {
          var myComponent = () => import('@/views/formDataPreview/mobileModule/biddingProject_new.vue')// 询比价
        } else if (funName === 'biddingProject') {
          var myComponent = () => import('@/views/formDataPreview/mobileModule/biddingProject.vue')// 询比价
        } else if (funName === 'quotaModulation') {
          var myComponent = () => import('@/views/formDataPreview/mobileModule/quotaModulation.vue')// 调整配额
        } else if (funName === 'performanceAssessment') {
          var myComponent = () => import('@/views/formDataPreview/mobileModule/performanceAssessment.vue')// 供应商考核
        } else if (funName === 'inquiryApprovalFlow') {
          var myComponent = () => import('@/views/formDataPreview/mobileModule/inquiryApprovalFlow.vue')// 价格审批单（寻源）
        }
      } else {
        let funName = this.$route.query.funName
        if (funName === 'quaOfReview') {
          // 供应商资质审查
          var myComponent = () => import('mod@/vendorManagementBuyer/views/quaOfReview/quaOfReviewDetail.vue')
        } else if (funName === 'siteAssessment') {
          // 供应商评审流程
          var myComponent = () => import('mod@/vendorManagementBuyer/views/siteAssessment/siteAssessmentDetail.vue')
        } else if (funName === 'purchaseApplication') {
          // 采购申请
          var myComponent = () => import('mod@/purchasingDemand/views/purchaseApplication/purchaseApplicationDetail.vue')
        } else if (funName === 'vendorInfoChange') {
          // 供应商信息变更
          var myComponent = () => import('mod@/vendorManagementBuyer/views/vendorInfoChange/vendorInfoChangeDetail.vue')
        } else if (funName === 'crossOrgImport') {
          // 跨OU
          var myComponent = () => import('mod@/vendorManagementBuyer/views/crossOrgImport/crossOrgImportDetail.vue')
        } else if (funName === 'buyerPurchaseOrder') {
          // 采购订单（订单管理）
          var myComponent = () => import('mod@/orderManagementBuyer/views/buyerPurchaseOrder/purchaseOrderDetail.vue')
        } else if (funName === 'purchaseOrderChange') {
          // 采购订单变更（订单管理）
          var myComponent = () => import('mod@/orderManagementBuyer/views/purchaseOrderChange/purchaseOrderChangeDetail.vue')
        } else if (funName === 'contractManager') {
          // 合同管理（查看）
          var myComponent = () => import('mod@/contractManagement/views/contractManager/edit.vue')
        } else if (funName === 'contractManager_alteration') {
          // 合同管理（合同变更）
          var myComponent = () => import('mod@/contractManagement/views/contractManager/edit.vue')
        } else if (funName === 'contractManager_agreements') {
          // 合同管理（补充协议）
          var myComponent = () => import('mod@/contractManagement/views/contractManager/edit.vue')
        } else if (funName === 'inspectionBill') {
          // 验收单列表
          var myComponent = () => import('mod@/contractManagement/views/inspectionBill/inspectionBillDetail.vue')
        } else if (funName === 'inquiryOrders') {
          // 询价单
          var myComponent = () => import('mod@/inquiryBySimpleSupplier/views/inquiryBySimpleListVendor/inquiryBySimpleListVendor/inquiryOrdersDetail.vue')
        } else if (funName === 'vendorGreenChannel') {
          // 供应商绿色通道
          var myComponent = () => import('mod@/vendorManagementBuyer/views/vendorGreenChannel/vendorGreenChannelDetail.vue')
        } else if (funName === 'inquiryByProjectListBuyer') {
          // 询比价
          var myComponent = () => import('@/modules/inquiryByProjectBuyer/views/inquiryByProjectListBuyer/bargainDetail.vue')
        } else if (funName === 'biddingProject') {
          // 招标
          var myComponent = () => import('mod@/biddingManagementBuyer/views/biddingProject/biddingProjectDetail.vue')
        } else if (funName === 'quotaModulation') {
          // 配额调整
          var myComponent = () => import('mod@/quotaManagement/views/quotaModulation/quotaModulationDetail.vue')
        } else if (funName === 'performanceAssessment') {
          // 供应商考核 、、绩效考核
          var myComponent = () => import('mod@/performanceManagement/views/performanceAssessment/assessmentDetail.vue')
        } else if (funName === 'inquiryApprovalFlow') {
          // 价格审批单（寻源）
          var myComponent = () => import('mod@/inquiryBySimpleBuyer/views/inquiryApprovalFlow/inquiryApprovalFlowDetail.vue')
        }
      }
      return myComponent
    }
  },
  created () {
    this.isMobile = this.isMobileDevice() // 获取当前是那种类型的设备
    console.log('isMobile', this.isMobile)
    console.log('query', this.$route.query)
    let funName = this.$route.query.funName

    if (funName === 'quaOfReview') {
      // 供应商资质审查
      this.formQuery = {
        row: { reviewFormId: this.$route.query.formId },
        reviewFormId: this.$route.query.formId,
        flag: 'view',
        isOnlyRead: true
      }
    } else if (funName === 'siteAssessment') {
      // 供应商评审流程
      this.formQuery = {
        row: { siteFormId: this.$route.query.formId },
        siteFormId: this.$route.query.formId,
        reviewFormId: this.$route.query.reviewFormId,
        flag: 'view'
      }
    } else if (funName === 'purchaseApplication') {
      // 采购申请创建
      this.formQuery = {
        row: { requirementHeadId: this.$route.query.formId },
        requirementHeadId: this.$route.query.formId,
        flag: 'readOnly'
      }
    } else if (funName === 'buyerPurchaseOrder') {
      // 采购订单
      this.formQuery = {
        row: { orderId: this.$route.query.formId },
        requirementHeadId: this.$route.query.formId,
        showType: 'readOnly',
        flag: 'edit',
        isOnlyRead: true,
        jumpLogin: false
      }
    } else if (funName === 'vendorInfoChange') {
      // 供应商信息更改
      this.formQuery = {
        changeId: this.$route.query.formId,
        flag: 'view',
        router: '/vendorManagement/vendorInfoChange',
        ouptCup: 'view'
      }
    } else if (funName === 'crossOrgImport') {
    // 跨组织
      this.formQuery = {
        row: { importId: this.$route.query.formId },
        flag: 'view',
        isOnlyRead: true
      }
    } else if (funName === 'purchaseOrderChange') {
      // 采购订单变更
      this.formQuery = {
        row: { orderId: this.$route.query.formId },
        orderId: this.$route.query.formId,
        flag: 'readOnly',
        isOnlyRead: true
      }
    } else if (funName === 'contractManager') {
      //  合同管理（查看）
      this.formQuery = {
        row: { contractHeadId: this.$route.query.formId },
        rowId: this.$route.query.formId,
        illegal: 'view',
        flag: 'edit',
        isReadOnly: true,
        jumpLogin: false
      }
    } else if (funName === 'contractManager_alteration') {
      //  合同管理（合同变更）
      this.formQuery = {
        // row:{contractHeadId:this.$route.query.formId},
        illegal: 'view',
        sourceId: 'get',
        contractType: 'MIAN_CONTRACT_ALTER',
        flag: 'add',
        isReadOnly: true,
        rowId: this.$route.query.formId,
        jumpLogin: false
      }
    } else if (funName === 'contractManager_agreements') {
      //  补充协议（合同变更）
      this.formQuery = {
        // row:{contractHeadId:this.$route.query.formId},
        illegal: 'view',

        contractType: 'SUPPLEMENTAL_AGREEMENT',
        sourceId: 'get',
        flag: 'add',
        isReadOnly: true,
        rowId: this.$route.query.formId,
        jumpLogin: false
      }
    } else if (funName === 'inspectionBill') {
      //  验收单列表
      this.formQuery = {
        flag: 'readOnly',
        row: { acceptOrderId: this.$route.query.formId }
      }
    } else if (funName === 'inquiryOrders') {
      //  询价单列表***NO***
      this.formQuery = {
        flag: 'edit',
        row: { quoteNo: true, inquiryId: this.$route.query.inquiryId, vendorId: this.$route.query.vendorId }
      }
    } else if (funName === 'vendorGreenChannel') {
      //  供应商绿色通道
      this.formQuery = {
        flag: 'readOnly',
        isReadOnly: true,
        companyId: this.$route.query.formId
      }
    } else if (funName === 'inquiryByProjectListBuyer') {
      //  询比价
      this.formQuery = {
        flag: 'edit',
        readOnly: true,
        isReadOnly: true,
        row: { bidingId: Number(this.$route.query.formId) }
      }
    } else if (funName === 'biddingProject') {
      //  招标
      this.formQuery = {
        flag: 'edit',
        readOnly: true,
        isReadOnly: true,
        row: { bidingId: Number(this.$route.query.formId) }

      }
    } else if (funName === 'quotaModulation') {
      //  调整配额
      this.formQuery = {
        flag: 'isReadOnly',
        // readOnly:true,
        // isReadOnly:true,
        row: { quotaAdjustId: Number(this.$route.query.formId) }

      }
    } else if (funName === 'performanceAssessment') {
      //  供应商考核
      this.formQuery = {
        flag: 'view',
        orderId: this.$route.query.formId,
        tabName: 'assessmentDetail' + this.$route.query.formId,
        isReadOnly: true

      }
    } else if (funName === 'inquiryApprovalFlow') {
      //  价格审批单（寻源）
      this.formQuery = {

        row: { approvalHeaderId: this.$route.query.formId },
        flag: 'edit',
        isReadonly: true,
        isEdit: false,
        isApproval: false,
        illegal: 'view'

      }
    }
  },
  mounted () {

  },

  methods: {
    // 判断是否移动端打开

    isMobileDevice () {
      let mobile = false
      if (navigator.userAgent.match(/(iPhone|iPod|Android|ios|iPad)/i)) {
        mobile = true
      }
      return mobile
    },

    getFileUrl (isM, type) {
      let fildUrl = ''
      if (isM) {
        fildUrl = '@/views/formDataPreview/mobileModule/quaOfReview.vue'
      } else {
        fildUrl = 'mod@/vendorManagementBuyer/views/quaOfReview/quaOfReviewDetail.vue'
      }
      return fildUrl
    },
    message () {
      top.postMessage(document.body.scrollHeight)
    }

  }

}

//  window.addEventListener('message', function(event) {

//                 event.source.postMessage(document.body.scrollHeight, event.origin)
//             });
//             console.log("document.body.scrollHeight",document.body.scrollHeight)

</script>
<style scoped>
.order-list-btn {
  position: fixed;
  bottom: 0;
  left: 0;
  background: #fff;
  width: 100%;
  padding: 12px 16px;
  /* display: flex; */
  /* justify-content: flex-end; */
  z-index:100;
  display: none;
}
.order-form-contain {
  width:100%;
  margin:0 0 58px 0;
  height:100%;
}
.order-form-contain > .el-container.flex-container {
  height:100%;
}
</style>
