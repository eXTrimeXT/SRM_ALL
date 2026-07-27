<template>
  <SrmRow>
    <SrmCol :init-col="4">
      <el-form-item label="板块" prop="orgBuName">
        <!-- <QuickSearch
          :disabled="readonly"
          :show-input="baseForm.orgBuName"
          show-key="organizationName"
          :scope-data="baseForm"
          name="plate_scc_base_organization"
          @close-quicksearch="getOrgObj"
        /> -->
        <el-input v-model="baseForm.orgBuName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="公司" prop="orgName">
        <!-- <OrganizationSelector
          v-model="baseForm.orgOuId"
          :scope="baseForm"
          node-type="OU"
          :parent-id="-1"
          :clearable="false"
          :placeholder="$t('common.pleaseSelect')"
          :disabled="readonly"
          @select="getOrgOu"
        /> -->
        <el-input v-model="baseForm.orgName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- pubconfigId -->
      <el-form-item label="寻源公示模板" prop="pubconfigName">
        <!--  :pre-query-data="{
            't.ORGANIZATION_ID':baseForm.orgBuId
          }" -->
        <QuickSearch
          :disabled="readonly"
          :showInput="baseForm.pubconfigName"
          show-key="pubconfigName"
          :scope-data="baseForm"
          name="scc_pj_source_pubconfig"
          @close-quicksearch="getPubconfigObj"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="需求部门" prop="reqDepartment">
        <el-input v-model="baseForm.reqDepartment" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="需求人" prop="reqUserName">
        <el-input v-model="baseForm.reqUserName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="供应商负责人" prop="responsibilityUserName">
        <el-input v-model="baseForm.responsibilityUserName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="招标负责人" prop="souPersonUserName">
        <el-input v-model="baseForm.souPersonUserName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="是否前置交流" prop="isPreComm">
        <DictSelect
          v-model="baseForm.isPreComm"
          code="YES_OR_NO"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="单据状态" prop="status">
        <DictSelect
          v-model="baseForm.status"
          code="SOU_REQ_HEAD_STATUS"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="创建人" prop="createdFullName">
        <el-input v-model="baseForm.createdFullName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="创建日期" prop="creationDate">
        <el-date-picker
          v-model="baseForm.creationDate"
          type="date"
          disabled
          format="yyyy-MM-dd"
          value-format="yyyy-MM-dd"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="最后更新日期" prop="lastUpdateDate">
        <el-date-picker
          v-model="baseForm.lastUpdateDate"
          type="date"
          disabled
          format="yyyy-MM-dd"
          value-format="yyyy-MM-dd"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="baseForm.status==='ABANDON'" :init-col="1">
      <el-form-item label="废弃说明" prop="reasonDesc">
        <el-input v-model="baseForm.reasonDesc" disabled />
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
    getPubconfigObj (val, scope) {
      console.log('val', val)
      scope.pubconfigId = val ? val.pubconfigId : null
      scope.pubconfigName = val ? val.pubconfigName : null
      scope.bankAccount = val ? val.bankAccount : null
      scope.bankAccountName = val ? val.bankAccountName : null
      scope.bankName = val ? val.bankName : null
      scope.bankNumber = val ? val.bankNumber : null
      scope.organizationId = val ? val.organizationId : null
      scope.organizationCode = val ? val.organizationCode : null
      scope.organizationName = val ? val.organizationName : null
      this.$emit('pubConfigChange', val)
    }
  }
}
</script>
