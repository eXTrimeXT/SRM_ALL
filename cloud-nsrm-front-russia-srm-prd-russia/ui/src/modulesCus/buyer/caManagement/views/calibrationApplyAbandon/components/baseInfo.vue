<template>
  <SrmRow>
    <SrmCol v-if="!approvalFlag" :init-col="4">
      <el-form-item label="定标申请单" prop="caNo">
        <el-input v-model="baseForm.caNo" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="板块" prop="extOrgBuName">
        <el-input v-model="baseForm.extOrgBuName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="公司" prop="extOrgOuName">
        <el-input v-model="baseForm.extOrgOuName" disabled />
        <!-- <OrganizationSelector
          v-model="baseForm.extOrgOuId"
          :scope="baseForm"
          node-type="OU"
          :parent-id="-1"
          :placeholder="$t('common.pleaseSelect')"
          disabled
          @select="orgSelect"
        /> -->
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="需求部门" prop="demandDepartmentName">
        <el-input v-model="baseForm.demandDepartmentName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="!approvalFlag" :init-col="4">
      <el-form-item label="单据状态" prop="status">
        <DictSelect
          v-model="baseForm.status"
          code="SOU_CA_STATUS"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="创建人" prop="createdFullName">
        <el-input v-model="baseForm.createdFullName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="!approvalFlag" :init-col="4">
      <el-form-item label="创建日期" prop="creationDate">
        <el-date-picker
          v-model="baseForm.creationDate"
          format="yyyy-MM-dd"
          value-format="yyyy-MM-dd"
          type="date"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="!approvalFlag" :init-col="4">
      <el-form-item label="最后更新日期" prop="lastUpdateDate">
        <el-date-picker
          v-model="baseForm.lastUpdateDate"
          format="yyyy-MM-dd"
          value-format="yyyy-MM-dd"
          type="date"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="!approvalFlag" :init-col="4">
      <el-form-item label="需求人" prop="demandUserNickname">
        <el-input v-model="baseForm.demandUserNickname" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="合同经办人" prop="contractOperatorNickname">
        <el-input v-model="baseForm.contractOperatorNickname" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="定标申请单" prop="originalCaNo">
        <el-input v-model="baseForm.originalCaNo" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="baseForm.status==='ABANDON'" :init-col="1">
      <el-form-item label="废弃说明" prop="discardDescription">
        <el-input v-model="baseForm.discardDescription" disabled />
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
    approvalFlag: {
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
    orgSelect (node, value, scope) {
      this.form.extOrgOuId = node ? node.organizationId : null
      this.form.extOrgOuCode = node ? node.organizationCode : null
      this.form.extOrgOuName = node ? node.organizationName : null
    }
  }
}
</script>
