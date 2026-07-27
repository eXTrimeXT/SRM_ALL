<template>
  <SrmRow>
    <SrmCol :init-col="1">
      <!-- 项目名称 -->
      <el-form-item :label="$t('bidMod.bidingName')" prop="projectName">
        <el-input v-model="baseForm.projectName" :disabled="readonly" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- 合同日期从 -->
      <el-form-item :label="$t('cusEntry.supplement20250121.contractStartDate')" prop="contractStartDate">
        <el-date-picker
          v-model="baseForm.contractStartDate"
          type="date"
          :disabled="readonly"
          :format="$formatDatePicker"
          value-format="yyyy-MM-dd"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- 合同日期至 -->
      <el-form-item :label="$t('cusEntry.supplement20250121.contractEndDate')" prop="contractEndDate">
        <el-date-picker
          v-model="baseForm.contractEndDate"
          type="date"
          :disabled="readonly"
          :format="$formatDatePicker"
          value-format="yyyy-MM-dd"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- 负责人 -->
      <el-form-item :label="$t('dataConfMod.principal')" prop="headPerson">
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
      <!-- 项目总金额（卢布） -->
      <el-form-item :label="$t('cusEntry.supplement20250121.projectTotalMoney')" prop="projectTotalMoney">
        <el-input v-model="baseForm.projectTotalMoney" :disabled="readonly" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="1">
      <!-- 下轮项目建议及注意 -->
      <el-form-item :label="$t('cusEntry.supplement20250121.nextSuggest')" prop="nextSuggest">
        <el-input v-model="baseForm.nextSuggest" type="textarea" :disabled="readonly" :autosize="{minRows:4,maxRows:6}" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="1">
      <!-- 延期原因 -->
      <el-form-item :label="$t('cusEntry.supplement20250121.delayReason')" prop="delayReason">
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
