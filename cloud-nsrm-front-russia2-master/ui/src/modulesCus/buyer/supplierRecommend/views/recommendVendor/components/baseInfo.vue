<template>
  <SrmRow>
    <SrmCol v-if="!approvalFlag && !isMobile" :init-col="4">
      <!-- <el-form-item label="推荐供应商单号" prop="extRecommendNo"> -->
      <el-form-item :label="$t('cusEntry.bidMod.recommendOrderNum')" prop="extRecommendNo">
        <el-input v-model="baseForm.extRecommendNo" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="!isMobile" :init-col="4">
      <!-- <el-form-item label="板块" prop="extOrgBuName"> -->
      <el-form-item :label="$t('cusEntry.bidSuperviseReport.extOrgBuName')" prop="extOrgBuName">
        <el-input v-model="baseForm.extOrgBuName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- <el-form-item label="公司" prop="extOrgOuName"> -->
      <el-form-item :label="$t('components.organization.COMPANY')" prop="extOrgOuName">
        <el-input v-model="baseForm.extOrgOuName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="!isMobile" :init-col="4">
      <!-- <el-form-item label="需求部门" prop="extApplicantDepart"> -->
      <el-form-item :label="$t('purchaseDemand.requirementDepartment')" prop="extApplicantDepart">
        <el-input v-model="baseForm.extApplicantDepart" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="!approvalFlag && !isMobile" :init-col="4">
      <!-- <el-form-item label="寻源单号" prop="souRequirementNo"> -->
      <el-form-item :label="$t('bidMod.businessNo')" prop="souRequirementNo">
        <el-input v-model="baseForm.souRequirementNo" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- <el-form-item label="创建人" prop="createdFullName"> -->
      <el-form-item :label="$t('common.creator')" prop="createdFullName">
        <el-input v-model="baseForm.createdFullName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="!approvalFlag && !isMobile" :init-col="4">
      <!-- <el-form-item label="创建日期" prop="creationDate"> -->
      <el-form-item :label="$t('common.creationDate')" prop="creationDate">
        <el-date-picker
          v-model="baseForm.creationDate"
          :format="$formatDatePicker"
          value-format="yyyy-MM-dd"
          type="date"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="!approvalFlag && !isMobile" :init-col="4">
      <!-- <el-form-item label="最后更新日期" prop="lastUpdateDate"> -->
      <el-form-item :label="$t('common.lastUpdateDate2')" prop="lastUpdateDate">
        <el-date-picker
          v-model="baseForm.lastUpdateDate"
          :format="$formatDatePicker"
          value-format="yyyy-MM-dd"
          type="date"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- <el-form-item label="技术专家" prop="extTechPrincipal"> -->
      <el-form-item :label="$t('cusEntry.bidSuperviseReport.extTechPrincipal')" prop="extTechPrincipal">
        <el-input v-model="baseForm.extTechPrincipal" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="!isMobile" :init-col="4">
      <!-- <el-form-item label="联系电话" prop="tel"> -->
      <el-form-item :label="$t('orderMod.buyerOrderSynergy.linkPhone')" prop="tel">
        <el-input v-model="baseForm.tel" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="!approvalFlag && !isMobile" :init-col="4">
      <!-- <el-form-item label="单据状态" prop="projectStatus"> -->
      <el-form-item :label="$t('hierarchical.Documentstatus')" prop="projectStatus">
        <DictSelect
          v-model="baseForm.projectStatus"
          code="SOU_RECOMMVENDOR_STATUS"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- <el-form-item label="招标专家" prop="extSouPrincipal"> -->
      <el-form-item :label="$t('cusEntry.bidSuperviseReport.souPrincipal')" prop="extSouPrincipal">
        <el-input v-model="baseForm.extSouPrincipal" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="baseForm.projectStatus==='ABANDON' && !isMobile" :init-col="1">
      <!-- <el-form-item label="废弃说明" prop="cancelReason"> -->
      <el-form-item :label="$t('cusEntry.supplement20250121.abandoned')" prop="cancelReason">
        <el-input v-model="baseForm.cancelReason" disabled />
      </el-form-item>
    </SrmCol>
  </SrmRow>
</template>
<script>
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
export default {
  components: {
    QuickSearch,
    OrganizationSelector
  },
  props: {
    approvalFlag: { // 审批流页面字段展示标识
      type: Boolean,
      default: false
    },
    isMobile: {
      type: Boolean,
      default: false
    },
    form: {
      type: Object,
      default: () => ({})
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
    }
  },
  methods: {
    orgSelect (node, value, scope) {
      if (!node) return
      scope.organizationId = node.organizationId
      scope.organizationCode = node.organizationCode
      scope.organizationName = node.organizationName
    }
  }
}
</script>
