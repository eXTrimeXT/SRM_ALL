<template>
  <SrmRow>
    <SrmCol :init-col="1">
      <el-form-item label="交流标题" prop="bidNoticeTitle">
        <el-input v-model="baseForm.bidNoticeTitle" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="交流通知单" prop="bidNoticeNo">
        <QuickSearch
          :disabled="readonly"
          :showInput="baseForm.bidNoticeNo"
          show-key="bidNoticeNo"
          :scope-data="baseForm"
          name="pre_bid_notice"
          @close-quicksearch="getNoticeObj"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="项目名称" prop="projectName">
        <el-input v-model="baseForm.projectName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="板块" prop="orgBuName">
        <el-input v-model="baseForm.orgBuName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="公司" prop="orgName">
        <el-input v-model="baseForm.orgName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="需求部门" prop="demandDepartmentName">
        <el-input v-model="baseForm.demandDepartmentName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="单据状态" prop="status">
        <DictSelect
          v-model="baseForm.status"
          code="PRE_BID_FEEDBACK_STATUS"
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
      <el-form-item label="需求人" prop="demandUserNickname">
        <el-input v-model="baseForm.demandUserNickname" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="标前交流反馈单" prop="bidFeedbackNo">
        <el-input v-model="baseForm.bidFeedbackNo" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="供应商负责人" prop="vendorUserNickname">
        <el-input v-model="baseForm.vendorUserNickname" disabled />
      </el-form-item>
    </SrmCol>
    <!-- <SrmCol :init-col="4">
      <el-form-item label="招标负责人" prop="bidUserNickname">
        <el-input v-model="baseForm.bidUserNickname" disabled />
      </el-form-item>
    </SrmCol> -->
    <SrmCol :init-col="4">
      <el-form-item label="创建日期" prop="creationDate">
        <el-input v-model="baseForm.creationDate" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="最后更新日期" prop="lastUpdateDate">
        <el-input v-model="baseForm.lastUpdateDate" disabled />
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
    }
  },
  methods: {
    getNoticeObj (val) {
      let attrs = ['bidNoticeId', 'bidNoticeNo', 'bidNoticeTitle', 'demandUserId', 'demandUserNickname', 'vendorUserNickname', 'vendorUserId',
        'orgBuCode', 'orgBuId', 'orgBuName', 'orgCode', 'orgId', 'orgName', 'projectName', 'requirementHeadId', 'requirementHeadNo', 'demandDepartmentName']
      for (let key of attrs) {
        this.baseForm[key] = val ? val[key] : null
      }
      if (val.bidNoticeId) {
        this.$emit('getNotice', val.bidNoticeId)
      }
    }
  }
}
</script>
