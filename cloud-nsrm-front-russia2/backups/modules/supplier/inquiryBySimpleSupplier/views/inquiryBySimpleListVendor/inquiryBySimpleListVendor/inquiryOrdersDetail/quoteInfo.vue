<template>
  <div class="quote-info">
    <srm-row>
      <srm-col :init-col="3">
        <!--f 询价单号-->
        <el-form-item :label="$t('bidMod.inquiryNo')">
          <el-input
            :value="header.inquiryNo"
            disabled
          />
        </el-form-item>
      </srm-col>

      <srm-col :init-col="3">
        <!--评分规则-->
        <el-form-item :label="$t('bidMod.inquiryRule')">
          <el-select
            :value="header.inquiryRule"
            disabled
          >
            <el-option
              v-for="item in ruleList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
      </srm-col>

      <srm-col :init-col="3">
        <!--f 报价方式-->
        <el-form-item :label="$t('bidMod.quoteRule')">
          <el-select
            :value="header.quoteRule"
            disabled
          >
            <el-option
              v-for="item in quoteTypeList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
      </srm-col>
    </srm-row>

    <srm-row>
      <srm-col :init-col="3">
        <!--f 报价开始时间-->
        <el-form-item :label="$t('bidMod.bidingStartDatetime1')">
          <el-date-picker
            :value="header.beginQuote"
            type="datetime"
            placeholder="日期选择"
            disabled
          />
        </el-form-item>
      </srm-col>

      <srm-col :init-col="3">
        <!--f 报价截止时间-->
        <el-form-item :label="$t('bidMod.quotedeadline')">
          <el-date-picker
            :value="header.deadline"
            type="datetime"
            placeholder="日期选择"
            disabled
          />
        </el-form-item>
      </srm-col>

      <srm-col :init-col="3">
        <!--f 当前轮次-->
        <el-form-item :label="$t('bidMod.currentRound')">
          <el-input
            :value="header.round"
            disabled
          />
        </el-form-item>
      </srm-col>
    </srm-row>

    <srm-row v-if="quoteNo">
      <srm-col :init-col="3">
        <!--f 报价单号-->
        <el-form-item :label="$t('bidMod.quoteNo')">
          <el-input
            :value="quoteNo"
            disabled
          />
        </el-form-item>
      </srm-col>
    </srm-row>

    <srm-row>
      <srm-col :init-col="1">
        <!--f 备注-->
        <el-form-item :label="$t('bidMod.remark')">
          <el-input
            :value="header.remark"
            type="textarea"
            disabled
          />
        </el-form-item>
      </srm-col>
    </srm-row>
  </div>
</template>

<script>
/**
 * 报价信息
 */
import { getDictItem } from '@/api/common'
import { adaptDictData } from '@/utils'

export default {
  name: 'QuoteInfo',
  props: {
    header: Object,
    quoteNo: String
  },
  data () {
    return {
      ruleList: [],
      quoteTypeList: []
    }
  },
  created () {
    // 询价单评分规则
    getDictItem('RFQ_SCORE_RULE').then(res => {
      this.ruleList = adaptDictData(res.data, 'dict')
    })
    // 报价方式就是评选方式
    getDictItem('RFQ_QUOTE_TYPE').then(res => {
      this.quoteTypeList = adaptDictData(res.data, 'dict')
    })
  }
}
</script>
