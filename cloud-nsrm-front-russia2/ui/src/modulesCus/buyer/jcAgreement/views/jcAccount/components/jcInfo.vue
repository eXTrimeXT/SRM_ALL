<template>
  <SrmRow>
    <SrmCol :init-col="4">
      <!-- 本次新增项目数 -->
      <el-form-item :label="$t('cusEntry.supplement20250121.addNum')" prop="projectName">
        <el-input v-model="baseForm.addNum" :disabled="readonly" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- 原临采年采购额（卢布） -->
      <el-form-item :label="$t('cusEntry.supplement20250121.addBeforeMoney')" prop="addBeforeMoney">
        <el-input v-model="baseForm.addBeforeMoney" :disabled="readonly" @change="moneyChange" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- 集采后年采购额（卢布） -->
      <el-form-item :label="$t('cusEntry.supplement20250121.addAfterMoney')" prop="addAfterMoney">
        <el-input v-model="baseForm.addAfterMoney" :disabled="readonly" @change="moneyChange" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- 本次递减金额（卢布） -->
      <el-form-item :label="$t('cusEntry.supplement20250121.addDecrementMoney')" prop="addDecrementMoney">
        <el-input v-model="baseForm.addDecrementMoney" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- 成本递减比例(%) -->
      <el-form-item :label="$t('cusEntry.supplement20250121.aboDecrementRatio')" prop="addDecrementRatio">
        <el-input v-model="baseForm.addDecrementRatio" disabled />
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
    moneyChange () {
      this.$emit('moneyChages',{
        a1: Number(this.baseForm.addBeforeMoney),
        a2: Number(this.baseForm.addAfterMoney),
        a3: 'addDecrementMoney',
        a4: 'addDecrementRatio'
      })
    }
  }
}
</script>
