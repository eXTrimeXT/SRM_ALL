<template>
  <SrmRow>
    <SrmCol :init-col="4">
      <!-- <el-form-item label="项目所在地" prop="projectAddress"> -->
      <el-form-item :label="$t('cusEntry.supplement20250121.locationOfTheProject')" prop="projectAddress">
        <el-input v-model="baseForm.projectAddress" :disabled="readonly" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- <el-form-item label="报名联系人" prop="contactName"> -->
      <el-form-item :label="$t('cusEntry.common.signUpPerson')" prop="contactName">
        <el-input v-model="baseForm.contactName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- <el-form-item label="报名联系电话" prop="phone"> -->
      <el-form-item :label="$t('cusEntry.common.signUpPhone')" prop="phone">
        <el-input v-model="baseForm.phone" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- <el-form-item label="办公电话" prop="officePhone"> -->
      <el-form-item :label="$t('cusEntry.common.signUpPhone')" prop="officePhone">
        <el-input v-model="baseForm.officePhone" disabled />
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
