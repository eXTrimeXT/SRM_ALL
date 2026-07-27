<template>
  <SrmRow>
    <SrmCol v-if="!approvalFlag && !isMobile" :init-col="4">
      <!-- <el-form-item label="申请单号" prop="applicantNo"> -->
      <el-form-item :label="$t('contractMod.applicationOrderNum')" prop="applicantNo">
        <el-input v-model="baseForm.applicantNo" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- <el-form-item label="项目名称" prop="souName"> -->
      <el-form-item :label="$t('bidMod.bidingName')" prop="souName">
        <el-input v-model="baseForm.souName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="!isMobile" :init-col="4">
      <!-- <el-form-item label="需求来源" prop="sourceFromType"> -->
      <el-form-item :label="$t('cusEntry.supplement20250121.sourceOfDemand')" prop="sourceFromType">
        <DictSelect
          v-model="baseForm.sourceFromType"
          code="PR_SOU_REQUIREMENT_FROM"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- <el-form-item label="预算（卢布）" prop="extBudget"> -->
      <el-form-item :label="$t('cusEntry.bidMod.budget')" prop="extBudget">
        <el-input v-model="baseForm.extBudget" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- <el-form-item label="品类" prop="extCategoryName"> -->
      <el-form-item :label="$t('common.category')" prop="extCategoryName">
        <el-input v-model="baseForm.extCategoryName" disabled />
        <!-- <CCategorySelect
          v-model="baseForm.categoryName"
          :scope="baseForm"
          :diabled="false"
          show-key="categoryName"
          :placeholder="$t('vendorMod.msgCategoryNormalizer')"
          @select="comfirmCategory"
        /> -->
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- <el-form-item label="规模数量" prop="extScaleQuantity"> -->
      <el-form-item :label="$t('cusEntry.bidMod.scaleQuantity')" prop="extScaleQuantity">
        <el-input v-model="baseForm.extScaleQuantity" :disabled="readonly || isAdd" />
      </el-form-item>
    </SrmCol>
    <!-- 投标意向金（元） -->
    <!-- <SrmCol v-if="!isMobile && isShowFlag == 'Y'" :init-col="4">
      <el-form-item :label="$t('cusEntry.supplement20250121.bidIntentionDeposit_yuan')" prop="extEarnestAmount">
        <el-input v-model="baseForm.extEarnestAmount" disabled />
      </el-form-item>
    </SrmCol> -->
    <SrmCol v-if="!approvalFlag && !isMobile" :init-col="4">
      <!-- <el-form-item label="推荐供应商类型" prop="rcommendType"> -->
      <el-form-item :label="$t('cusEntry.supplement20250121.recommendedSupplierTypes')" prop="rcommendType">
        <DictSelect
          v-model="baseForm.rcommendType"
          code="SOU_RECOMMVENDOR_TYPE"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- <el-form-item label="是否公示" prop="publishFlag"> -->
      <el-form-item :label="$t('cusEntry.supplement20250121.isItPubliclyAnnounced')" prop="publishFlag">
        <DictSelect
          v-model="baseForm.publishFlag"
          code="YES_OR_NO"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="isApend && !isMobile" :init-col="4">
      <!-- <el-form-item label="原推荐供应商单号" prop="originalExtRecommendNo"> -->
      <el-form-item :label="$t('cusEntry.supplement20250121.originalRecommendedSupplierTrackingNumber')" prop="originalExtRecommendNo">
        <el-input v-model="baseForm.originalExtRecommendNo" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="applicantNoList.length > 1 && !isMobile" :init-col="1">
      <!-- <span style="font-size:14px;">合并申请单号：</span> -->
      <span style="font-size:14px;">{{ $t("cusEntry.bidMod.mergeOrder") }}</span>
      <el-button v-for="(item,index) in applicantNoList" :key="index" type="text" @click="applyNoClick(item,index)">
        {{ item }}
      </el-button>
    </SrmCol>
    <SrmCol v-if="isApend && !isMobile" :init-col="1">
      <!-- 追加供应商才有此项 -->
      <!-- <el-form-item label="追加供应商原因" prop="addVendorReason"> -->
      <el-form-item :label="$t('cusEntry.supplement20250121.reasonForAddingSuppliers')" prop="addVendorReason">
        <el-input v-model="baseForm.addVendorReason" :autosize="{ minRows: 4, maxRows: 6}" type="textarea" :disabled="readonly" />
      </el-form-item>
    </SrmCol>
  </SrmRow>
</template>
<script>
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import CCategorySelect from 'lib@/components/c-category-select'
import purchaseApplicationDetail2 from '@/modulesCus/buyer/purchasingDemand/views/purchaseApplication/purchaseApplicationDetailZhaobiao'

export default {
  components: {
    QuickSearch,
    OrganizationSelector,
    CCategorySelect
  },
  props: {
    form: {
      type: Object,
      default: () => ({})
    },
    readonly: {
      type: Boolean,
      default: false
    },
    approvalFlag: { // 审批流页面字段展示标识
      type: Boolean,
      default: false
    },
    isMobile: {
      type: Boolean,
      default: false
    },
    isAdd: {
      type: Boolean,
      default: false
    },
    isShowFlag: {
      type: String,
      default: ''
    }
  },
  data () {
    return {
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
    isApend () {
      return this.baseForm.rcommendType === 'ADD'
    },
    applicantNoList () {
      if (this.form.applicantNo) {
        return this.form.applicantNo.toString().split(';')
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
    }
  }
}
</script>
