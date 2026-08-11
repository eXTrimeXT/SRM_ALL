<template>
  <div class="quote-info">
    <SrmRow>
      <SrmCol :init-col="3">
        <!--f 询价单号-->
        <el-form-item :label="$t('bidMod.inquiryNo')">
          <el-input :value="header.souNo" disabled />
        </el-form-item>
      </SrmCol>

      <SrmCol :init-col="3">
        <!--评分规则-->
        <el-form-item :label="$t('bidMod.inquiryRule')">
          <DictSelect v-model="header.scoreRuleType" code="SOU_SCORE_RULE_TYPE" />
        </el-form-item>
      </SrmCol>

      <SrmCol :init-col="3">
        <!--f 报价方式-->
        <el-form-item :label="$t('bidMod.quoteRule')">
          <DictSelect v-model="header.orderWay" code="SOU_ORDER_WAY" />
        </el-form-item>
      </SrmCol>
    </SrmRow>

    <SrmRow>
      <SrmCol :init-col="3">
        <!--f 报价开始时间-->
        <el-form-item :label="$t('bidMod.bidingStartDatetime1')">
          <el-date-picker
            :value="header.orderStartTime"
            type="datetime"
            :format="$formatDatePickerTime"
            :placeholder="$t('common.pleaseSelectDate')"
            disabled
          />
        </el-form-item>
      </SrmCol>

      <SrmCol :init-col="3">
        <!--f 报价截止时间-->
        <el-form-item :label="$t('bidMod.quotedeadline')">
          <el-date-picker
            :value="header.orderEndTime"
            type="datetime"
            :format="$formatDatePickerTime"
            :placeholder="$t('common.pleaseSelectDate')"
            disabled
          />
        </el-form-item>
      </SrmCol>

      <SrmCol :init-col="3">
        <!--f 当前轮次-->
        <el-form-item :label="$t('bidMod.currentRound')">
          <el-input :value="header.currentRound" disabled />
        </el-form-item>
      </SrmCol>
    </SrmRow>

    <SrmRow v-if="orderNo">
      <SrmCol :init-col="3">
        <!--f 报价单号-->
        <el-form-item :label="$t('bidMod.quoteNo')">
          <el-input :value="orderNo" disabled />
        </el-form-item>
      </SrmCol>
    </SrmRow>

    <SrmRow>
      <SrmCol :init-col="1">
        <!--f 备注-->
        <el-form-item :label="$t('bidMod.remark')">
          <el-input
            :value="header.remark"
            type="textarea"
            disabled
          />
        </el-form-item>
      </SrmCol>
    </SrmRow>
  </div>
</template>

<script>
/**
 * 报价信息
 */
export default {
  name: 'QuoteInfo',

  props: {
    header: {
      type: Object,
      required: true
    },
    orderNo: {
      type: String,
      required: true
    }
  }
}
</script>
