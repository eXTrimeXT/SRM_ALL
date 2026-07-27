<template>
  <SrmRow>
    <!--项目编号-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bidMod.bidingNum')">
        <el-input v-model="baseInfoData.souNo" disabled />
      </el-form-item>
    </SrmCol>

    <!--项目名称-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bidMod.bidingName')" prop="souName">
        <el-input v-model="baseInfoData.souName" />
      </el-form-item>
    </SrmCol>

    <!--报名截止时间-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bidMod.registrationDeadline')" prop="signUpEndTime">
        <el-date-picker
          v-model="baseInfoData.signUpEndTime"
          type="datetime"
          value-format="yyyy-MM-dd HH:mm:ss"
          :placeholder="$t('bidMod.optionDate')"
          :picker-options="cannotLessCurrentTimeOptions"
        />
      </el-form-item>
    </SrmCol>

    <!--预计竞价地点-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bidMod.expectedBiddingPlace')" prop="orderSite">
        <el-input v-model="baseInfoData.orderSite" />
      </el-form-item>
    </SrmCol>

    <!--竞价开始时间-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bidMod.bidStartTime')" prop="orderStartTime">
        <el-date-picker
          v-model="baseInfoData.orderStartTime"
          type="datetime"
          value-format="yyyy-MM-dd HH:mm:ss"
          :placeholder="$t('bidMod.optionDate')"
          :picker-options="cannotLessCurrentTimeOptions"
        />
      </el-form-item>
    </SrmCol>

    <!--竞价截止时间-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bidMod.bidClosingTime')" prop="orderEndTime">
        <el-date-picker
          v-model="baseInfoData.orderEndTime"
          type="datetime"
          value-format="yyyy-MM-dd HH:mm:ss"
          :picker-options="endTiumePickerOptions"
          :placeholder="$t('bidMod.optionDate')"
        />
      </el-form-item>
    </SrmCol>

    <!--预算金额-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bidMod.budgetAmount')" prop="budgetAmount">
        <el-input v-model="baseInfoData.budgetAmount" v-input-format="{ type: 'float' }" />
      </el-form-item>
    </SrmCol>

    <!--评分规则-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bidMod.evaluateMethod')" prop="scoreRuleType">
        <DictSelect
          v-model="baseInfoData.scoreRuleType"
          code="SOU_COMP_SCORE_RULE_TYPE"
          :transform-options="transformOptions"
        />
      </el-form-item>
    </SrmCol>

    <!--是否进价格库-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bid_mod.isSyncToPriceLibrary')" prop="isSyncToPriceLibrary">
        <DictSelect v-model="baseInfoData.isSyncToPriceLibrary" code="YES_OR_NO" />
      </el-form-item>
    </SrmCol>

    <!--价格有效期自-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bid_mod.defaultPriceValidFrom')" prop="priceStartTime">
        <el-date-picker
          v-model="baseInfoData.priceStartTime"
          type="date"
          value-format="yyyy-MM-dd"
          :placeholder="$t('bidMod.optionDate')"
        />
      </el-form-item>
    </SrmCol>

    <!--价格有效期至-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bid_mod.defaultPriceValidTo')" prop="priceEndTime">
        <el-date-picker
          v-model="baseInfoData.priceEndTime"
          type="date"
          value-format="yyyy-MM-dd"
          :picker-options="endTiumePickerOptions2"
          :placeholder="$t('bidMod.optionDate')"
        />
      </el-form-item>
    </SrmCol>

    <!--涨降幅百分比-->
    <SrmCol :init-col="3">
      <el-form-item
        :label="minPercentTips.label"
        prop="minPercent"
        :rules="{
          required: !baseInfoData.minAmount,
          message: this.$t('common.pleaseInput')
        }"
      >
        <template #label>
          <span>
            <span>{{ minPercentTips.label }}</span>
            <el-tooltip
              style="margin-left: 2px"
              placement="top"
            >
              <div slot="content">
                <p>{{ minPercentTips.tip }}</p>
              </div>
              <em class="el-icon-question table-msg" />
            </el-tooltip>
          </span>
        </template>
        <el-input
          v-model="baseInfoData.minPercent"
          v-input-format="{ type: 'float' }"
          :disabled="!!baseInfoData.minAmount"
          @input="amountOrPercentInput"
        />
      </el-form-item>
    </SrmCol>

    <!--金额-->
    <SrmCol :init-col="3">
      <el-form-item
        :label="minAmountTips.label"
        prop="minAmount"
        :rules="{
          required: !baseInfoData.minPercent,
          message: this.$t('common.pleaseInput')
        }"
      >
        <template #label>
          <span>
            <span>{{ minAmountTips.label }}</span>
            <el-tooltip style="margin-left: 2px" placement="top">
              <div slot="content">
                <p>{{ minAmountTips.tip }}</p>
              </div>
              <em class="el-icon-question table-msg" />
            </el-tooltip>
          </span>
        </template>
        <el-input
          v-model="baseInfoData.minAmount"
          v-input-format="{ type: 'float' }"
          :disabled="!!baseInfoData.minPercent"
          @input="amountOrPercentInput"
        />
      </el-form-item>
    </SrmCol>

    <!--延长竞价触发点-->
    <SrmCol :init-col="3">
      <el-form-item
        :label="$t('bidMod.extendBidTrigger')"
        :title="showHoverMsg"
        prop="extendTrigger"
        class="the_hover_class"
      >
        <template #label>
          <span>
            <span>{{ $t('bidMod.extendBidTrigger') }}</span>
            <el-tooltip
              style="margin-left: 2px"
              placement="top"
            >
              <div slot="content">
                <p>{{ showHoverMsg }}</p>
              </div>
              <em class="el-icon-question table-msg" />
            </el-tooltip>
          </span>
        </template>
        <el-input
          v-model="baseInfoData.extendTrigger"
          v-input-format="{ type: 'float' }"
          min="1"
        />
      </el-form-item>
    </SrmCol>

    <!--延长分钟数-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bidMod.extendedMinutes')" prop="extendMinute">
        <el-input
          v-model="baseInfoData.extendMinute"
          v-input-format="{ type: 'float' }"
          min="1"
        />
      </el-form-item>
    </SrmCol>

    <!--中标供应商数量-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bidMod.winCount')" prop="maxWinVendorCount">
        <el-input
          v-model="baseInfoData.maxWinVendorCount"
          v-input-format="{ type: 'integer' }"
          min="1"
        />
      </el-form-item>
    </SrmCol>
  </SrmRow>
</template>

<script>
/**
 * 项目信息
 */
import { cannotLessCurrentTime } from 'lib@/mixins/datePickerOptions'

export default {
  name: 'ProjectInfo',

  mixins: [cannotLessCurrentTime],

  props: {
    baseInfo: {
      type: Object,
      default: () => { /* nothing */ }
    }
  },

  data () {
    return {
      endTiumePickerOptions: {
        disabledDate: (time) => {
          const start = new Date(this.baseInfoData.orderStartTime)
          start.setHours(0)
          start.setMinutes(0)
          start.setSeconds(0)
          start.setMilliseconds(0)
          const nowDate = new Date()
          nowDate.setHours(0)
          nowDate.setMinutes(0)
          nowDate.setSeconds(0)
          nowDate.setMilliseconds(0)
          return (time.getTime() < start.getTime()) || (time.getTime() < nowDate.getTime())
        }
      },
      endTiumePickerOptions2: {
        disabledDate: (time) => {
          const start = new Date(this.baseInfoData.priceStartTime)
          return time.getTime() <= start.getTime()
        }
      }
    }
  },

  computed: {
    baseInfoData: {
      get: function () {
        return this.baseInfo
      },
      set: function (val) {
        this.$emit('update:baseInfo', val)
      }
    },

    // 涨降幅百分比
    minPercentTips () {
      if (this.baseInfoData.scoreRuleType === 'MIN_PRICE') {
        return {
          label: `${this.$t('bidMod.minimalPercentage')}(%)`,
          tip: `${this.$t('bidMod.minimumPriceReductionMsg')}${this.baseInfoData.minPercent || '？'}%`
        }
      }
      if (this.baseInfoData.scoreRuleType === 'MAX_PRICE') {
        return {
          label: `${this.$t('bidMod.minimumPercentage')}(%)`,
          tip: `${this.$t('bidMod.minimumPriceIncreaseMsg')}${this.baseInfoData.minPercent || '？'}%`
        }
      }
      return {
        label: '涨降幅百分比',
        tip: ''
      }
    },

    // 涨降金额
    minAmountTips () {
      if (this.baseInfoData.scoreRuleType === 'MIN_PRICE') {
        return {
          label: this.$t('bidMod.minimumPriceReduction'),
          tip: `${this.$t('bidMod.minimumPriceReductionMsg')}${this.baseInfoData.minAmount || '？'}${this.$t('common.yuan')}`
        }
      }
      if (this.baseInfoData.scoreRuleType === 'MAX_PRICE') {
        return {
          label: this.$t('bidMod.minimumPriceIncrease'),
          tip: `${this.$t('bidMod.minimumPriceIncreaseMsg')}${this.baseInfoData.minAmount || '？'}${this.$t('common.yuan')}`
        }
      }
      return {
        label: '涨降金额',
        tip: ''
      }
    },

    showHoverMsg () {
      let showTime1 = this.baseInfoData.extendTrigger || '?'
      let showTime2 = this.baseInfoData.extendMinute || '?'
      return `${this.$t('bidMod.closeRoundBefore')}${showTime1}${this.$t(
        'bidMod.currentRoundExtended'
      )}${showTime2}${this.$t('bidMod.minute')}`
    }
  },

  methods: {
    amountOrPercentInput () {
      this.$emit('amount-input')
    },

    /* 过滤综合评分 */
    transformOptions (options) {
      return options.filter(item => item.value !== 'COMPOSITE_PRICE')
    }
  }
}
</script>
