<template>
  <SrmRow>
    <SrmCol :init-col="1">
      <el-form-item label="交流标题" prop="bidNoticeTitle">
        <el-input v-model="baseForm.bidNoticeTitle" :disabled="readonly" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="申请单号" prop="requirementHeadNo">
        <el-input v-model="baseForm.requirementHeadNo" :disabled="readonly" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="项目名称" prop="projectName">
        <el-input v-model="baseForm.projectName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="单据状态" prop="status">
        <DictSelect
          v-model="baseForm.status"
          code="PRE_BID_NOTICE_STATUS"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="招标负责人" prop="bidUserNickname">
        <el-input v-model="baseForm.bidUserNickname" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="联系电话" prop="bidUserPhone">
        <el-input v-model="baseForm.bidUserPhone" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="办公电话" prop="bidUserOfficePhone">
        <el-input v-model="baseForm.bidUserOfficePhone" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="交流通知单号" prop="bidNoticeNo">
        <el-input v-model="baseForm.bidNoticeNo" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="发布日期" prop="creationDate">
        <el-input v-model="baseForm.creationDate" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="1">
      <el-form-item label="备注" prop="remark">
        <el-input v-model="baseForm.remark" type="textarea" :disabled="readonly" :autosize="{minRows:4,maxRows:6}" />
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
    getReqObj (val) {
      this.baseForm.requirementHeadId = val ? val.requirementHeadId : null
      this.baseForm.requirementHeadNo = val ? val.requirementHeadNum : null
      this.baseForm.projectName = val ? val.projectName : null
      this.baseForm.orgBuId = val ? val.orgBuId : null
      this.baseForm.orgBuCode = val ? val.orgBuCode : null
      this.baseForm.orgBuName = val ? val.orgBuName : null
      this.baseForm.orgId = val ? val.orgId : null
      this.baseForm.orgCode = val ? val.orgCode : null
      this.baseForm.orgName = val ? val.orgName : null
      this.baseForm.demandDepartmentId = val ? val.demandDepartmentId : null
      this.baseForm.demandDepartmentCode = val ? val.demandDepartmentCode : null
      this.baseForm.demandDepartmentName = val ? val.demandDepartmentName : null
      this.baseForm.demandUserId = val ? val.applyById : null
      this.baseForm.demandUserNickname = val ? val.applyByNickname : null
      this.baseForm.vendorUserId = val ? val.vendorUserId : null
      this.baseForm.vendorUserNickname = val ? val.vendorFullName : null
      this.baseForm.bidUserId = val ? val.ceeaProjectUserId : null // CEEA_PROJECT_USER_ID
      this.baseForm.bidUserNickname = val ? val.ceeaProjectUserNickname : null // CEEA_PROJECT_USER_NICKNAME
    }
  }
}
</script>
