<template>
  <SrmRow>
    <SrmCol :init-col="1">
      <!-- <el-form-item label="交流标题" prop="bidNoticeTitle"> -->
      <el-form-item :label="$t('bidMod.communicateTitle')" prop="bidNoticeTitle">
        <el-input v-model="baseForm.bidNoticeTitle" :disabled="readonly || !isCreator" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- <el-form-item label="申请单号" prop="requirementHeadNo"> -->
      <el-form-item :label="$t('contractMod.applicationOrderNum')" prop="requirementHeadNo">
        <!-- requirementHeadId -->
        <QuickSearch
          :disabled="readonly || !isCreator"
          :showInput="baseForm.requirementHeadNo"
          show-key="requirementHeadNum"
          :scope-data="baseForm"
          name="pr_requirement_head3"
          @close-quicksearch="getReqObj"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- <el-form-item label="项目名称" prop="projectName"> -->
      <el-form-item :label="$t('cusEntry.bidSuperviseReport.souName')" prop="projectName">
        <el-input v-model="baseForm.projectName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- <el-form-item label="板块" prop="orgBuName"> -->
      <el-form-item :label="$t('cusEntry.bidSuperviseReport.extOrgBuName')" prop="orgBuName">
        <el-input v-model="baseForm.orgBuName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- <el-form-item label="公司" prop="orgName"> -->
      <el-form-item :label="$t('components.organization.COMPANY')" prop="orgName">
        <el-input v-model="baseForm.orgName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item :label="$t('purchaseDemand.requirementDepartment')" prop="demandDepartmentName">
        <el-input v-model="baseForm.demandDepartmentName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- <el-form-item label="单据状态" prop="status"> -->
      <el-form-item :label="$t('cusEntry.reportManagement.auditStatus')" prop="status">
        <DictSelect
          v-model="baseForm.status"
          code="PRE_BID_NOTICE_STATUS"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- <el-form-item label="创建人" prop="createdFullName"> -->
      <el-form-item :label="$t('cusEntry.orderMod.createdBy')" prop="createdFullName">
        <el-input v-model="baseForm.createdFullName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- <el-form-item label="申请人" prop="demandUserNickname"> -->
      <el-form-item :label="$t('cusEntry.orderMod.applyer')" prop="demandUserNickname">
        <el-input v-model="baseForm.demandUserNickname" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- <el-form-item label="交流通知单号" prop="bidNoticeNo"> -->
      <el-form-item :label="$t('cusEntry.supplement20250205.communicationNoticeNumber')" prop="bidNoticeNo">
        <el-input v-model="baseForm.bidNoticeNo" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- <el-form-item label="供应商专家" prop="vendorUserNickname"> -->
      <el-form-item :label="$t('cusEntry.bidSuperviseReport.vendorPrincipal')" prop="vendorUserNickname">
        <el-input v-model="baseForm.vendorUserNickname" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item :label="$t('cusEntry.bidSuperviseReport.souPrincipal')" prop="bidUserNickname">
        <el-input v-model="baseForm.bidUserNickname" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- <el-form-item label="创建日期" prop="creationDate"> -->
      <el-form-item :label="$t('common.creationDate')" prop="creationDate">
        <el-date-picker
          v-model="baseForm.creationDate"
          type="date"
          :format="$formatDatePicker"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- <el-form-item label="最后更新日期" prop="lastUpdateDate"> -->
      <el-form-item :label="$t('cusEntry.bidMod.updateDate')" prop="lastUpdateDate">
        <el-date-picker
          v-model="baseForm.lastUpdateDate"
          type="date"
          :format="$formatDatePicker"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="1">
      <!-- <el-form-item label="备注" prop="remark"> -->
      <el-form-item :label="$t('components.eio.headers.remark')" prop="remark">
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
    },
    isCreator: {
      type: Boolean,
      default: true
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
      this.baseForm.bidUserId = val ? val.souPersonId : null // CEEA_PROJECT_USER_ID
      this.baseForm.bidUserNickname = val ? val.souPersonName : null // CEEA_PROJECT_USER_NICKNAME
      // this.baseForm.bidNoticeTitle = val ? '关于' + val.projectName + '的标前交流' : ''
      this.baseForm.bidNoticeTitle = val ? this.$t('cusEntry.supplement20250205.about') + val.projectName + this.$t('cusEntry.supplement20250205.preBidCommunication') : ''
      // 需带出招标专家联系电话、办公电话
    }
  }
}
</script>
