<template>
  <div class="wrapper">
    <el-row :gutter="32">
      <el-col :span="12">
        <!-- <el-form-item label="项目名称" prop="souName"> -->
        <el-form-item :label="$t('bidMod.bidingName')" prop="souName">
          <el-input v-model="baseForm.souName" disabled />
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <!-- <el-form-item label="招标项目编号" prop="extProjectNo"> -->
        <el-form-item :label="$t('bidMod.bidingNumCla')" prop="extProjectNo">
          <el-input v-model="baseForm.extProjectNo" disabled />
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <!-- <el-form-item label="预算（卢布）" prop="extBudget"> -->
        <el-form-item :label="$t('cusEntry.bidMod.budget')" prop="extBudget">
          <el-input v-model="baseForm.extBudget" disabled />
        </el-form-item>
      </el-col>
      <!-- <el-col :span="12">
        <el-form-item label="工期/交货期要求" prop="timeLimit">
          <el-input
            v-model="baseForm.timeLimit"
            :disabled="readonly"
          />
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item label="质保期" prop="warrantyPeriod">
          <el-input
            v-model="baseForm.warrantyPeriod"
            :disabled="readonly"
          />
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item label="评分规则" prop="extScoreRule">
          <DictSelect
            v-model="baseForm.extScoreRule"
            code="SOU_BID_SCORE_RULE"
            disabled
          />
        </el-form-item>
      </el-col> -->
    </el-row>
    <SrmRow>
      <!-- <SrmCol v-if="applicantNoList.length > 1" :init-col="1">
        <span style="font-size:14px;">合并申请单号：</span>
        <el-button v-for="(item,index) in applicantNoList" :key="index" type="text" @click="applyNoClick(item,index)">
          {{ item }}
        </el-button>
      </SrmCol> -->
      <SrmCol :init-col="1">
        <!-- <el-form-item label="项目概况与招标范围" prop="projectOverviewAndBidScope"> -->
        <el-form-item :label="$t('cusEntry.bidMod.bidScope')" prop="projectOverviewAndBidScope">
          <el-input
            v-model="baseForm.projectOverviewAndBidScope"
            :maxlength="3000"
            :disabled="!isWriteBid"
            :autosize="{ minRows: 4, maxRows: 6}"
            type="textarea"
          />
        </el-form-item>
      </SrmCol>
      <!-- <SrmCol :init-col="1">
        <el-form-item label="付款要求" prop="paymentRequirements">
          <el-input
            v-model="baseForm.paymentRequirements"
            :maxlength="1500"
            :disabled="readonly"
            :autosize="{ minRows: 4, maxRows: 6}"
            type="textarea"
          />
        </el-form-item>
      </SrmCol> -->
    </SrmRow>

    <!-- <BidVendor
      ref="bidVendor"
      :value.sync="baseForm.caOrders"
      :applicantNo="baseForm.applicantNo"
    /> -->

    <!-- <VendorSituation
      v-if="baseForm.extSouProcess !== 'INQUIRY'"
      ref="vendorSituation"
      class="mt-10"
      :value.sync="baseForm.caSuppliers"
      :isWrite="isWrite"
      :readonly="readonly"
      :applicantNo="baseForm.applicantNo"
      :extScoreRule="baseForm.extScoreRule"
    /> -->

    <!-- 查看评审详情（技术标） -->
    <!-- <TechScoreDetailDialog
      v-if="techScoreDetailDialogVisible"
      :visible.sync="techScoreDetailDialogVisible"
      :edit-row="{
        projectId:baseForm.projectId,
        groupId:baseForm.groupId
      }"
    /> -->
  </div>
</template>
<script>
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import VendorSituation from './businessInfo/vendorSituation'
import BidVendor from './businessInfo/bidVendor'
import TechScoreDetail from 'modcb@/biddingBuyer/views/biddingManagement/biddingDetail/techManagement/techScoreDetail'
import purchaseApplicationDetail2 from '@/modulesCus/buyer/purchasingDemand/views/purchaseApplication/purchaseApplicationDetailZhaobiao'

export default {
  components: {
    QuickSearch,
    OrganizationSelector,
    VendorSituation,
    BidVendor
  },
  props: {
    form: {
      type: Object,
      default: () => ({})
    },
    flag: {
      type: String,
      default: ''
    },
    approvalFlag: {
      type: Boolean,
      default: false
    },
    isWriteBid: {
      type: Boolean,
      default: false
    },
    readonly: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      techScoreDetailDialogVisible: false
    }
  },
  computed: {
    baseForm: {
      get: function () {
        return this.form
      },
      set: function (val) {
        this.$emit('update:form', val)
      }
    },
    isWrite () { // BMP回调标志是否填写 Y是可以填写，N不可以填写
      return this.form.ifWrite === 'N'
    },
    applicantNoList () {
      let applicantNo = this.baseForm.applicantNo
      if (applicantNo) {
        return applicantNo.toString().split(';')
      }
      return []
    }
  },
  methods: {
    tabAdd (val) {
      this.$emit('tabAdd', val)
    },
    // 确认选择品类
    comfirmCategory (node, scope) {
      scope.categoryId = node ? node.categoryId : null
      scope.categoryName = node ? node.categoryName : ''
      scope.categoryCode = node ? node.categoryCode : ''
      scope.categoryFullName = node ? node.categoryFullName : ''
    },
    /** 申请单号单击 */
    applyNoClick (item, index) {
      if (this.form.applicantId) {
        let curId = this.form.applicantId.toString().split(';')[index]
        const row = {
          requirementHeadNum: item,
          requirementHeadId: curId
        }
        this.$emit('tab-add', {
          component: purchaseApplicationDetail2,
          params: {
            flag: 'approveNumber',
            row: row,
            showType: 'readOnly',
            tabName: 'purchaseApplicationDetail' + row.requirementHeadNum,
            activeWorkflowTab: false
          },
          title: row.requirementHeadNum,
          name: 'purchaseApplicationDetail' + row.requirementHeadNum
        })
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.mt-10 {
  margin-top: 10px;
}
</style>
