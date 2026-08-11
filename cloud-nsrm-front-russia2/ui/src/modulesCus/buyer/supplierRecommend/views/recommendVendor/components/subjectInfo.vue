<template>
  <SrmRow>
    <SrmCol :init-col="1">
      <!-- <el-form-item label="项目概述及招标范围" prop="projectRemark"> -->
      <el-form-item :label="$t('cusEntry.supplement20250121.projectOverviewAndBiddingScope')" prop="projectRemark">
        <el-input
          v-model="baseForm.projectRemark"
          :maxlength="3000"
          :disabled="readonly || isAdd"
          :autosize="{ minRows: 4, maxRows: 6}"
          type="textarea"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="1">
      <!-- <el-form-item label="供应商资质要求" prop="vendorFlairAdjure"> -->
      <el-form-item :label="$t('cusEntry.supplement20250121.supplierQualificationRequirements')" prop="vendorFlairAdjure">
        <el-input v-model="baseForm.vendorFlairAdjure" :disabled="readonly || isAdd" :autosize="{ minRows: 4, maxRows: 6}" type="textarea" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="1">
      <!-- <el-form-item label="供应商业绩要求" prop="vendorBizAdjure"> -->
      <el-form-item :label="$t('cusEntry.supplement20250121.supplierPerformanceRequirements')" prop="vendorBizAdjure">
        <el-input v-model="baseForm.vendorBizAdjure" :disabled="readonly || isAdd" :autosize="{ minRows: 4, maxRows: 6}" type="textarea" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="1">
      <!-- <el-form-item label="前期招标情况" prop="preInviteTenders"> -->
      <el-form-item :label="$t('cusEntry.supplement20250121.preliminaryBiddingSituation')" prop="preInviteTenders">
        <el-input v-model="baseForm.preInviteTenders" :disabled="readonly" :autosize="{ minRows: 4, maxRows: 6}" type="textarea" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="1">
      <!-- <el-form-item label="备注" prop="remark"> -->
      <el-form-item :label="$t('components.eio.headers.remark')" prop="remark">
        <el-input v-model="baseForm.remark" :disabled="readonly" :autosize="{ minRows: 4, maxRows: 6}" type="textarea" />
      </el-form-item>
    </SrmCol>
  </SrmRow>
</template>
<script>
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import CCategorySelect from 'lib@/components/c-category-select'
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
    isAdd: {
      type: Boolean,
      default: false
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
    // 确认选择品类
    comfirmCategory (node, scope) {
      scope.categoryId = node ? node.categoryId : null
      scope.categoryName = node ? node.categoryName : ''
      scope.categoryCode = node ? node.categoryCode : ''
      scope.categoryFullName = node ? node.categoryFullName : ''
    },
    /** 申请单号单击 */
    applyNoClick () {

    }
  }
}
</script>
