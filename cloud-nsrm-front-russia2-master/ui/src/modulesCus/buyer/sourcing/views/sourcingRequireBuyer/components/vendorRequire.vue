<template>
  <SrmRow>
    <SrmCol :init-col="1">
      <!-- <el-form-item label="技术要求" prop="technicalReq"> -->
      <el-form-item :label="$t('qualitySynergy.technicalRequirement')" prop="technicalReq">
        <el-input
          v-model="baseForm.technicalReq"
          :disabled="readonly"
          :autosize="{ minRows: 4, maxRows: 6}"
          type="textarea"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="1">
      <!-- <el-form-item label="资质要求" prop="vendorQualReq"> -->
      <el-form-item :label="$t('cusEntry.supplement20250121.qualificationRequirements')" prop="vendorQualReq">
        <el-input v-model="baseForm.vendorQualReq" :disabled="readonly" :autosize="{ minRows: 4, maxRows: 6}" type="textarea" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="1">
      <!-- <el-form-item label="业绩要求" prop="performanceReq"> -->
      <el-form-item :label="$t('cusEntry.supplement20250121.performanceRequirements')" prop="performanceReq">
        <el-input v-model="baseForm.performanceReq" :disabled="readonly" :autosize="{ minRows: 4, maxRows: 6}" type="textarea" />
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
    form: {
      type: Object,
      default: () => ({})
    },
    readonly: {
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
    applyNoClick () {

    }
  }
}
</script>
