<template>
  <div class="wrapper">
    <SrmRow>
      <SrmCol :init-col="4">
        <!-- <el-form-item label="定标申请单" prop="originalCaNo"> -->
        <el-form-item :label="$t('cusEntry.supplement20250205.bidAwardApplicationForm')" prop="originalCaNo">
          <el-input v-model="baseForm.originalCaNo" disabled />
        </el-form-item>
      </SrmCol>
      <SrmCol :init-col="4">
        <!-- <el-form-item label="招标项目单号" prop="extProjectNo"> -->
        <el-form-item :label="$t('cusEntry.supplement20250205.tenderProjectSerialNumber')" prop="extProjectNo">
          <el-input v-model="baseForm.extProjectNo" disabled />
        </el-form-item>
      </SrmCol>
      <SrmCol :init-col="4">
        <!-- <el-form-item label="项目名称" prop="souName"> -->
        <el-form-item :label="$t('bidMod.bidingName')" prop="souName">
          <el-input v-model="baseForm.souName" disabled />
        </el-form-item>
      </SrmCol>
      <!-- <SrmCol :init-col="4">
        <el-form-item label="质保期" prop="warrantyPeriod">
          <el-input v-model="baseForm.warrantyPeriod" :disabled="readonly" />
        </el-form-item>
      </SrmCol>
      <SrmCol :init-col="4">
        <el-form-item label="预算（卢布）" prop="extBudget">
          <el-input v-model="baseForm.extBudget" disabled />
        </el-form-item>
      </SrmCol>
      <SrmCol :init-col="4">
        <el-form-item label="发标时间" prop="publishTime">
          <el-date-picker
            v-model="baseForm.publishTime"
            :format="$formatDatePickerTime"
            value-format="yyyy-MM-dd HH:mm:ss"
            disabled
          />
        </el-form-item>
      </SrmCol>
      <SrmCol :init-col="4">
        <el-form-item label="收标时间" prop="busEndTime">
          <el-date-picker
            v-model="baseForm.busEndTime"
            :format="$formatDatePickerTime"
            value-format="yyyy-MM-dd HH:mm:ss"
            disabled
          />
        </el-form-item>
      </SrmCol>
      <SrmCol :init-col="4">
        <el-form-item label="评标结束" prop="techEvaluationTime">
          <el-date-picker
            v-model="baseForm.techEvaluationTime"
            :format="$formatDatePickerTime"
            value-format="yyyy-MM-dd HH:mm:ss"
            disabled
          />
        </el-form-item>
      </SrmCol>
      <SrmCol :init-col="4">
        <el-form-item label="开价格标时间" prop="priceOpeningTime">
          <el-date-picker
            v-model="baseForm.priceOpeningTime"
            :format="$formatDatePickerTime"
            value-format="yyyy-MM-dd HH:mm:ss"
            disabled
          />
        </el-form-item>
      </SrmCol>
      <SrmCol :init-col="4">
        <el-form-item label="工期/交货期要求" prop="timeLimit">
          <el-input v-model="baseForm.timeLimit" :disabled="readonly" />
        </el-form-item>
      </SrmCol>
      <SrmCol :init-col="4">
        <el-form-item label="评分规则" prop="extScoreRule">
          <DictSelect
            v-model="baseForm.extScoreRule"
            code="SOU_BID_SCORE_RULE"
            disabled
          />
        </el-form-item>
      </SrmCol> -->
      <SrmCol v-if="baseForm.extSouProcess !== 'INQUIRY'" :init-col="4">
        <!-- <el-form-item label="技术评分结果"> -->
        <el-form-item :label="$t('cusEntry.supplement20250205.technicalScoreResult')">
          <el-button style="width:100%" type="ghost" @click="viewScoreDetail">
            <!-- 查看 -->
            {{ $t("common.view") }}
          </el-button>
        </el-form-item>
      </SrmCol>
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
            :disabled="readonly"
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
      <SrmCol :init-col="1">
        <!-- <el-form-item label="备注" prop="remark"> -->
        <el-form-item :label="$t('components.eio.headers.remark')" prop="remark">
          <el-input
            v-model="baseForm.remark"
            :maxlength="3000"
            :disabled="readonly"
            :autosize="{ minRows: 4, maxRows: 6}"
            type="textarea"
          />
        </el-form-item>
      </SrmCol>
      <SrmCol :init-col="1">
        <!-- <el-form-item label="废弃定标说明" prop="abandonDesc"> -->
        <el-form-item :label="$t('cusEntry.supplement20250205.cancellationOfBidAwardExplanation')" prop="abandonDesc">
          <el-input
            v-model="baseForm.abandonDesc"
            :maxlength="3000"
            :disabled="disabledFlag"
            :autosize="{ minRows: 4, maxRows: 6}"
            type="textarea"
          />
        </el-form-item>
      </SrmCol>
    </SrmRow>

    <!-- <BidVendor
      ref="bidVendor"
      :value.sync="baseForm.caOrders"
      :applicantNo="baseForm.applicantNo"
    />

    <VendorSituation
      v-if="baseForm.extSouProcess !== 'INQUIRY'"
      ref="vendorSituation"
      class="mt-10"
      :value.sync="baseForm.caSuppliers"
      :readonly="true"
    />

    <VendorResult
      ref="vendorResult"
      class="mt-10"
      :value.sync="baseForm.caSelectionResults"
      :readonly="true"
    /> -->

    <!-- 查看评审详情（技术标） -->
    <TechScoreDetailDialog
      v-if="techScoreDetailDialogVisible"
      :visible.sync="techScoreDetailDialogVisible"
      :edit-row="{
        projectId:baseForm.projectId,
        groupId:baseForm.groupId,
        souName: baseForm.souName
      }"
    />
  </div>
</template>
<script>
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import VendorSituation from './businessInfo/vendorSituation'
import BidVendor from './businessInfo/bidVendor'
import VendorResult from './businessInfo/vendorResult'
import TechScoreDetailDialog from 'modcb@/biddingBuyer/views/biddingManagement/biddingDetail/techManagement/techScoreDetailDialog'
import purchaseApplicationDetail2 from '@/modulesCus/buyer/purchasingDemand/views/purchaseApplication/purchaseApplicationDetailZhaobiao'

export default {
  components: {
    QuickSearch,
    OrganizationSelector,
    VendorSituation,
    BidVendor,
    VendorResult,
    TechScoreDetailDialog
  },
  props: {
    form: {
      type: Object,
      default: () => ({})
    },
    approvalFlag: {
      type: Boolean,
      default: false
    },
    readonly: {
      type: Boolean,
      default: false
    },
    disabledFlag: {
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
    applicantNoList () {
      let applicantNo = this.baseForm.applicantNo
      if (applicantNo) {
        return applicantNo.toString().split(';')
      }
      return []
    }
  },
  methods: {
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
    },
    viewScoreDetail () {
      if (!this.baseForm.projectId) return
      this.techScoreDetailDialogVisible = true
    }
  }
}
</script>
<style lang="scss" scoped>
.mt-10 {
  margin-top: 10px;
}
</style>
