<template>
  <SrmRow>
    <SrmCol :init-col="4">
      <el-form-item label="上期/集采前采购额(万元)" prop="aboBeforeMoney">
        <el-input v-model="baseForm.aboBeforeMoney" :disabled="readonly" @change="moneyChange" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="集采后年采购额(万元)" prop="aboAfterMoney">
        <el-input v-model="baseForm.aboAfterMoney" :disabled="readonly" @change="moneyChange" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="本次递减金额(万元)" prop="aboDecrementMoney">
        <el-input v-model="baseForm.aboDecrementMoney" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="成本递减比例(%)" prop="aboDecrementRatio">
        <el-input v-model="baseForm.aboDecrementRatio" disabled />
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
        a1: Number(this.baseForm.aboBeforeMoney),
        a2: Number(this.baseForm.aboAfterMoney),
        a3: 'aboDecrementMoney',
        a4: 'aboDecrementRatio'
      })
    }
  }
}
</script>
