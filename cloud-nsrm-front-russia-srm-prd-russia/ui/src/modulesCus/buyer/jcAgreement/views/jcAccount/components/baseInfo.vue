<template>
  <SrmRow>
    <SrmCol :init-col="1">
      <el-form-item label="项目名称" prop="projectName">
        <el-input v-model="baseForm.projectName" :disabled="readonly" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="合同日期从" prop="contractStartDate">
        <el-date-picker
          v-model="baseForm.contractStartDate"
          type="datetime"
          :disabled="readonly"
          format="yyyy-MM-dd"
          value-format="yyyy-MM-dd"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="合同日期至" prop="contractEndDate">
        <el-date-picker
          v-model="baseForm.contractEndDate"
          type="datetime"
          :disabled="readonly"
          format="yyyy-MM-dd"
          value-format="yyyy-MM-dd"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="负责人" prop="headPerson">
        <QuickSearch
          :disabled="readonly"
          :showInput="baseForm.headPerson"
          show-key="nickname"
          :scope-data="baseForm"
          name="scc_rbac_user_display"
          @close-quicksearch="getBuyerPersonObj"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="项目总金额（万元）" prop="projectTotalMoney">
        <el-input v-model="baseForm.projectTotalMoney" :disabled="readonly" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="1">
      <el-form-item label="下轮项目建议及注意" prop="nextSuggest">
        <el-input v-model="baseForm.nextSuggest" type="textarea" :disabled="readonly" :autosize="{minRows:4,maxRows:6}" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="1">
      <el-form-item label="延期原因" prop="delayReason">
        <el-input v-model="baseForm.delayReason" type="textarea" :disabled="readonly" :autosize="{minRows:4,maxRows:6}" />
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
    // 采购员快查
    getBuyerPersonObj (val, scope) {
      this.baseForm.headPerson = val ? val.nickname : null
    }
  }
}
</script>
