<template>
  <SrmRow>
    <SrmCol :init-col="1"  style="margin: 0 15px">
      <!-- 调价介绍 -->
      <el-form-item :label="$t('cusEntry.supplement20250121.adjustIntro')" prop="introduce">
        <el-input v-model="form.introduce" type="textarea" :autosize="{minRows:3,maxRows:5}" />
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
    // baseForm: {
    //   get: function () {
    //     return this.form
    //   },
    //   set: function (val) {
    //     this.$emit('update:form', val)
    //   }
    // },
    studyObj () {
      const { educationList } = this.form
      if (educationList && educationList.length) {
        return educationList[0]
      }
      return {}
    }
  },
  watch: {
    // form: {
    //   handler(value){
    //     this.$emit('input', value)
    //   },
    //   deep: true
    // },
  },
  methods: {
    // 公司主体；快查
    async getCompanyObj (val, scope) {
      this.baseForm.companyId = val ? val.organizationId : null
      this.baseForm.companyCode = val ? val.organizationCode : null
      this.baseForm.companyName = val ? val.organizationName : null
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
