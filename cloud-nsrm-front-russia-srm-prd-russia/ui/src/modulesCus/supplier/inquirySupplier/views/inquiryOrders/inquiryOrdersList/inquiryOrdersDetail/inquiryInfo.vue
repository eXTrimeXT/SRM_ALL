<template>
  <div class="quote-info">
    <SrmRow>
      <SrmCol :init-col="4">
        <!--f 询价单号-->
        <el-form-item :label="$t('bidMod.inquiryNo')">
          <el-input :value="header.souNo" disabled />
        </el-form-item>
      </SrmCol>
      <SrmCol :init-col="4">
        <!--f 报价开始时间-->
        <el-form-item :label="$t('bidMod.bidingStartDatetime1')">
          <el-date-picker
            :value="header.orderStartTime"
            type="datetime"
            :placeholder="$t('common.pleaseSelectDate')"
            disabled
          />
        </el-form-item>
      </SrmCol>
      <SrmCol :init-col="4">
        <!--f 报价截止时间-->
        <el-form-item :label="$t('bidMod.quotedeadline')">
          <el-date-picker
            :value="header.orderEndTime"
            type="datetime"
            :placeholder="$t('common.pleaseSelectDate')"
            disabled
          />
        </el-form-item>
      </SrmCol>
      <SrmCol :init-col="4">
        <!--f 当前轮次-->
        <el-form-item :label="$t('bidMod.currentRound')">
          <el-input :value="header.currentRound" disabled />
        </el-form-item>
      </SrmCol>
      <SrmCol :init-col="4">
        <!--f 报价单号-->
        <el-form-item :label="$t('bidMod.quoteNo')">
          <el-input :value="orderNo" disabled />
        </el-form-item>
      </SrmCol>
      <SrmCol :init-col="4">
        <!--f 询价方 -->
        <el-form-item :label="$t('cusEntry.bidMod.inquiryCompany')">
          <el-input v-model="header.extDepartmentName" disabled />
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
