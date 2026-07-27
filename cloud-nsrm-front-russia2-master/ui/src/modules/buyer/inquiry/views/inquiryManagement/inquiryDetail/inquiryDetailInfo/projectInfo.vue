<template>
  <SrmRow>
    <!--询价单号-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bidMod.inquiryNo')">
        <el-input v-model="headerData.souNo" disabled />
      </el-form-item>
    </SrmCol>

    <!--询价标题-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bidMod.inquiryTitle')" prop="souName">
        <el-input
          v-model="headerData.souName"
          maxlength="80"
          show-word-limit
        />
      </el-form-item>
    </SrmCol>

    <!--报价方式-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bidMod.quoteRule')" prop="orderWay">
        <DictSelect
          v-model="headerData.orderWay"
          clearable
          :disabled="orderWayDisabled"
          code="SOU_ORDER_WAY"
        />
      </el-form-item>
    </SrmCol>

    <!--预计报价开始时间-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bidMod.beginQuote')" prop="orderStartTime">
        <el-date-picker
          v-model="headerData.orderStartTime"
          type="datetime"
          :format="$formatDatePickerTime"
          value-format="yyyy-MM-dd HH:mm:ss"
          :picker-options="cannotLessCurrentTimeOptions"
        />
      </el-form-item>
    </SrmCol>

    <!--报价结束时间-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bidMod.deadline')" prop="orderEndTime">
        <el-date-picker
          v-model="headerData.orderEndTime"
          type="datetime"
          :format="$formatDatePickerTime"
          value-format="yyyy-MM-dd HH:mm:ss"
          :picker-options="orderEndTimePickerOptions"
        />
      </el-form-item>
    </SrmCol>

    <!--询价类型-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bidMod.inquiryType')" prop="inquiryType">
        <DictSelect
          v-model="headerData.inquiryType"
          code="SOU_INQUIRY_TYPE"
          clearable
        />
      </el-form-item>
    </SrmCol>

    <!--邀标类型-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bidMod.publishScopeTitle')" prop="publishScope">
        <DictSelect
          v-model="headerData.publishScope"
          code="SOU_PUBLISH_SCOPE"
          clearable
        />
      </el-form-item>
    </SrmCol>

    <!--发起人-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bidMod.createdBy')">
        <el-input v-model="headerData.createdUserName" disabled />
      </el-form-item>
    </SrmCol>

    <!--创建时间-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bidMod.creationDate')">
        <el-date-picker
          v-model="headerData.creationDate"
          type="date"
          :format="$formatDatePicker"
          disabled
        />
      </el-form-item>
    </SrmCol>

    <!--单据状态-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bidMod.billstatus')">
        <DictSelect
          v-model="headerData.extProjectStatus"
          code="SOU_PROJECT_STATUS"
          disabled
        />
      </el-form-item>
    </SrmCol>

    <!--备注-->
    <SrmCol :init-col="1">
      <el-form-item :label="$t('bidMod.remark')">
        <el-input
          v-model="headerData.remark"
          type="textarea"
          :rows="2"
          maxlength="500"
          show-word-limit
        />
      </el-form-item>
    </SrmCol>
  </SrmRow>
</template>

<script>
/**
 * 项目信息
 */
import { cannotLessCurrentTime } from '@/library/mixins/datePickerOptions'
import { SOU_SCORE_RULE_TYPE_ENUM } from '@/library/composition/origin/enum'

export default {
  name: 'ProjectInfo',

  mixins: [cannotLessCurrentTime],

  props: {
    header: {
      type: Object,
      default: () => { /* nothing */ }
    }
  },

  data () {
    return {
      orderEndTimePickerOptions: {
        disabledDate: time => {
          const [nowDate, startDate, valueDate] = [
            this.$dayjs().hour(0).minute(0).second(0).unix(),
            this.$dayjs(this.headerData.orderStartTime).hour(0).minute(0).second(0).unix(),
            this.$dayjs(time).unix()
          ]
          return (valueDate < startDate) || (valueDate < nowDate)
        }
      }
    }
  },

  computed: {
    headerData: {
      get: function () {
        return this.header
      },
      set: function (val) {
        this.$emit('update:header', val)
      }
    },

    // 是否禁止选择报价方式
    orderWayDisabled () {
      return this.headerData.scoreRuleType === SOU_SCORE_RULE_TYPE_ENUM.COMPOSITE_PRICE
    }
  }
}
</script>
