<template>
  <SrmRow>
    <SrmCol :init-col="4">
      <el-form-item label="调价申请单号" prop="adjustCode">
        <el-input v-model="baseForm.adjustCode" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="关联集采项目编号" prop="jcCode">
        <QuickSearch
          :disabled="readonly"
          :showInput="baseForm.jcCode"
          show-key="organizationName"
          :scope-data="baseForm"
          name="sou_ch_design_plan"
          @close-quicksearch="getCompanyObj"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="调价申请名称" prop="adjustName">
        <el-input v-model="baseForm.adjustName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="次数" prop="num">
        <el-input v-model="baseForm.num" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="执行时间从" prop="executeDateStart">
        <el-date-picker
          v-model="baseForm.executeDateStart"
          type="datetime"
          :disabled="readonly"
          format="yyyy-MM-dd HH:mm:ss"
          value-format="yyyy-MM-dd HH:mm:ss"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="执行时间至" prop="executeDateEnd">
        <el-date-picker
          v-model="baseForm.executeDateEnd"
          type="datetime"
          :disabled="readonly"
          format="yyyy-MM-dd HH:mm:ss"
          value-format="yyyy-MM-dd HH:mm:ss"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="调价形式" prop="adjustType">
        <DictSelect
          v-model="baseForm.adjustType"
          code="APPLY_ADJUST_TYPE"
          :disabled="readonly"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="创建人" prop="createdFullName">
        <el-input v-model="baseForm.createdFullName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="创建时间" prop="creationDate">
        <el-input v-model="baseForm.creationDate" disabled />
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
      fileInfo: {
        fileModular: 'sou',
        fileFunction: 'centralizedAgree',
        fileType: 'images'
      }
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
    },
    studyObj () {
      const { educationList } = this.form
      if (educationList && educationList.length) {
        return educationList[0]
      }
      return {}
    }
  },
  methods: {
    // 公司主体；快查
    async getCompanyObj (val, scope) {
      this.baseForm.adjustName = val?.projectName + val?.num
      this.baseForm.jcCode = val?.projectCode
      this.baseForm.jcId = val?.projectId
      this.baseForm.designId = val?.designId
    },
    // 采购组织选择
    orgSelect (node, value, scope) {
      console.log('node', node)
      if (node.length) {
        this.baseForm.sccSouJcAgreementOrgList = node.map(item => ({
          buyOrgId: item.organizationId,
          buyOrgCode: item.organizationCode,
          buyOrgName: item.organizationName
        }))
      }
    },
    // 供应商快查
    getSupObj (val, scope) {
      this.baseForm.supId = val ? val.companyId : null
      this.baseForm.supCode = val ? val.companyCode : null
      this.baseForm.supName = val ? val.companyName : null
    },
    // 采购员快查
    getBuyerPersonObj (val, scope) {
      this.baseForm.buyPersonId = val ? val.userId : null
      this.baseForm.buyPersonCode = val ? val.username : null
      this.baseForm.buyPersonName = val ? val.nickname : null
    },
    // 协议附件上传
    handleUploadSuccess (file) {
      const { fileId = '', fileName = '', fileType = '' } = file || {}
      this.baseForm.agreementFileId = fileId.toString()
      this.baseForm.agreementFileName = fileName
    }
  }
}
</script>
