<template>
  <SrmRow>
    <SrmCol :init-col="1">
      <!-- <el-form-item label="交流标题" prop="bidNoticeTitle"> -->
      <el-form-item :label="$t('bidMod.communicateTitle')" prop="bidNoticeTitle">
        <el-input v-model="baseForm.bidNoticeTitle" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- <el-form-item label="交流通知单" prop="bidNoticeNo"> -->
      <el-form-item :label="$t('cusEntry.supplement20250205.communicationNotice')" prop="bidNoticeNo">
        <el-input v-model="baseForm.bidNoticeNo" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- <el-form-item label="项目名称" prop="projectName"> -->
      <el-form-item :label="$t('bidMod.bidingName')" prop="projectName">
        <el-input v-model="baseForm.projectName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- <el-form-item label="反馈状态" prop="feedbackStatus"> -->
      <el-form-item :label="$t('dashboard.loopMode')" prop="feedbackStatus">
        <DictSelect
          v-model="baseForm.feedbackStatus"
          code="VENDOR_FEEDBACK_STATUS"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- <el-form-item label="招标专家" prop="bidUserNickname"> -->
      <el-form-item :label="$t('cusEntry.bidSuperviseReport.souPrincipal')" prop="bidUserNickname">
        <el-input v-model="baseForm.bidUserNickname" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- <el-form-item label="联系电话" prop="bidUserPhone"> -->
      <el-form-item :label="$t('orderMod.buyerOrderSynergy.linkPhone')" prop="bidUserPhone">
        <el-input v-model="baseForm.bidUserPhone" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- <el-form-item label="办公电话" prop="bidUserOfficePhone"> -->
      <el-form-item :label="$t('cusEntry.supplement20250205.officePhone')" prop="bidUserOfficePhone">
        <el-input v-model="baseForm.bidUserOfficePhone" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- <el-form-item label="标前交流反馈单" prop="bidFeedbackNo"> -->
      <el-form-item :label="$t('cusEntry.supplement20250205.preBidFeedbackForm')" prop="bidFeedbackNo">
        <el-input v-model="baseForm.bidFeedbackNo" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- <el-form-item label="发布日期" prop="creationDate"> -->
      <el-form-item :label="$t('orderMod.buyerOrderSynergy.submittedTime')" prop="creationDate">
        <el-date-picker
          v-model="baseForm.creationDate"
          :format="$formatDatePickerTime"
          value-format="yyyy-MM-dd HH:mm:ss"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="1">
      <!-- <el-form-item label="备注" prop="remark"> -->
      <el-form-item :label="$t('cusEntry.bidSuperviseReport.orderstaus')" prop="remark">
        <el-input v-model="baseForm.remark" type="textarea" disabled :autosize="{minRows:4,maxRows:6}" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="1">
      <!-- <el-form-item label="驳回说明" prop="rejectDescription"> -->
      <el-form-item :label="$t('vendorMod.refuseMemo')" prop="rejectDescription">
        <el-input v-model="baseForm.rejectDescription" type="textarea" :disabled="readonly" :autosize="{minRows:4,maxRows:6}" />
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

  }
}
</script>
