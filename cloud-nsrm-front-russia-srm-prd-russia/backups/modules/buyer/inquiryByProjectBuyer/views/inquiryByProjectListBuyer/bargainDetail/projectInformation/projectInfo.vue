<template>
  <srm-row>
    <srm-col :init-col="3">
      <!--流程模板-->
      <el-form-item
        :label="$t('bidMod.processConfigId')"
        prop="processConfigId"
      >
        <el-select
          v-model="bargainBaseInfo.processConfigId"
          :disabled="!!bargainBaseInfo.processConfigId && !!bargainBaseInfo.bargainNum"
          @change="processConfigIdChange"
        >
          <el-option
            v-for="item in brgProcessConfigIdList"
            :key="item.processConfigId"
            :label="item.processConfigName"
            :value="item.processConfigId"
          />
        </el-select>
      </el-form-item>
    </srm-col>

    <srm-col :init-col="3">
      <!--项目编号-->
      <el-form-item :label="$t('bidMod.bidingNum')">
        <el-input
          v-model="bargainBaseInfo.bargainNum"
          disabled
        />
      </el-form-item>
    </srm-col>

    <srm-col :init-col="3">
      <!--项目名称-->
      <el-form-item
        :label="$t('bidMod.bidingName')"
        prop="bargainName"
      >
        <el-input v-model="bargainBaseInfo.bargainName" />
      </el-form-item>
    </srm-col>

    <srm-col
      v-if="showEnrollEndDatetime"
      :init-col="3"
    >
      <!--报名截止时间-->
      <el-form-item
        :label="$t('bidMod.registrationDeadline')"
        prop="enrollEndDatetime"
      >
        <el-date-picker
          v-model="bargainBaseInfo.enrollEndDatetime"
          type="datetime"
          value-format="timestamp"
          :placeholder="$t('bidMod.datePicker')"
          :picker-options="cannotLessCurrentTimeOptions"
        />
      </el-form-item>
    </srm-col>

    <srm-col :init-col="3">
      <!--预计报价开始时间-->
      <el-form-item
        :label="$t('bidMod.beginQuote')"
        prop="bargainStartDatetime"
      >
        <el-date-picker
          v-model="bargainBaseInfo.bargainStartDatetime"
          type="datetime"
          value-format="timestamp"
          :placeholder="$t('bidMod.datePicker')"
          :picker-options="cannotLessCurrentTimeOptions"
        />
      </el-form-item>
    </srm-col>

    <srm-col :init-col="3">
      <!--报价截止时间-->
      <el-form-item
        :label="$t('bidMod.quotedeadline')"
        prop="bargainEndDatetime"
      >
        <el-date-picker
          v-model="bargainBaseInfo.bargainEndDatetime"
          type="datetime"
          value-format="timestamp"
          :picker-options="bargainEndDatetimePickerOptions"
          :placeholder="$t('bidMod.datePicker')"
        />
      </el-form-item>
    </srm-col>

    <srm-col :init-col="3">
      <!--预计询价地点-->
      <el-form-item
        :label="$t('bidMod.bidingSite1')"
        prop="bargainSite"
      >
        <el-input v-model="bargainBaseInfo.bargainSite" />
      </el-form-item>
    </srm-col>

    <srm-col :init-col="3">
      <!--询价范围-->
      <el-form-item
        :label="$t('bidMod.bidingScope1')"
        prop="bargainScope"
      >
        <dict-select
          v-model="bargainBaseInfo.bargainScope"
          code="BARGAIN_SCOPE"
          disabled
        />
      </el-form-item>
    </srm-col>

    <srm-col :init-col="3">
      <!--预算金额-->
      <el-form-item
        :label="$t('bidMod.budgetAmount')"
        prop="budgetAmount"
      >
        <el-input
          v-model="bargainBaseInfo.budgetAmount"
          v-input-format="{ type: 'float' }"
        />
      </el-form-item>
    </srm-col>

    <srm-col :init-col="3">
      <!--询价类型-->
      <el-form-item
        :label="$t('bidMod.bidingType1')"
        prop="bargainType"
      >
        <dict-select
          v-model="bargainBaseInfo.bargainType"
          code="BARGAIN_TYPE"
          disabled
        />
      </el-form-item>
    </srm-col>

    <srm-col :init-col="3">
      <!--评分规则-->
      <el-form-item
        :label="$t('bidMod.evaluateMethod')"
        prop="evaluateMethod"
      >
        <dict-select
          v-model="bargainBaseInfo.evaluateMethod"
          code="BRG_EVALUATE_METHOD"
          disabled
        />
      </el-form-item>
    </srm-col>

    <srm-col :init-col="3">
      <!--决标方式-->
      <el-form-item
        :label="$t('bidMod.bidingAwardWay')"
        prop="bargainAwardWay"
      >
        <dict-select
          v-model="bargainBaseInfo.bargainAwardWay"
          code="BRG_AWARD_WAY"
        />
      </el-form-item>
    </srm-col>

    <srm-col :init-col="3">
      <!--是否进价格库-->
      <el-form-item
        :label="$t('bid_mod.isSyncToPriceLibrary')"
        prop="isSyncToPriceLibrary"
      >
        <dict-select
          v-model="bargainBaseInfo.isSyncToPriceLibrary"
          code="YES_OR_NO"
        />
      </el-form-item>
    </srm-col>

    <srm-col :init-col="3">
      <!--价格有效期自-->
      <el-form-item
        :label="$t('bid_mod.defaultPriceValidFrom')"
        prop="priceStartTime"
      >
        <el-date-picker
          v-model="bargainBaseInfo.priceStartTime"
          type="date"
          value-format="yyyy-MM-dd"
          :placeholder="$t('bidMod.datePicker')"
        />
      </el-form-item>
    </srm-col>

    <srm-col :init-col="3">
      <!--价格有效期至-->
      <el-form-item
        :label="$t('bid_mod.defaultPriceValidTo')"
        prop="priceEndTime"
      >
        <el-date-picker
          v-model="bargainBaseInfo.priceEndTime"
          type="date"
          value-format="yyyy-MM-dd"
          :picker-options="priceEndTimePickerOptions"
          :placeholder="$t('bidMod.datePicker')"
        />
      </el-form-item>
    </srm-col>
  </srm-row>
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
    bargainBase: {
      type: Object
    },
    brgProcessConfigIdList: {
      type: Array,
      default: () => []
    },
    showEnrollEndDatetime: {
      type: Boolean
    }
  },
  data () {
    return {
      bargainEndDatetimePickerOptions: {
        disabledDate: (time) => {
          const start = new Date(this.bargainBaseInfo.bargainStartDatetime)
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
      priceEndTimePickerOptions: {
        disabledDate: time => {
          const start = new Date(this.bargainBaseInfo.priceStartTime)
          return time.getTime() <= start.getTime()
        }
      }
    }
  },
  computed: {
    bargainBaseInfo: {
      get: function () {
        return this.bargainBase
      },
      set: function (val) {
        this.$emit('update:bargainBase', val)
      }
    }
  },
  methods: {
    /* 获取流程配置 */
    async processConfigIdChange (val) {
      if (val) {
        // 设置左边菜单节点信息
        this.$emit('setMenuNodeConfig', val, 'add')

        const data = await this.$api.brg.inquiryByProject.processConfigList({ processConfigId: val })
        if (data.data && data.data.list && data.data.list.length === 1) {
          this.bargainBaseInfo.bargainScope = data.data.list[0].bargainScope
          this.bargainBaseInfo.bargainType = data.data.list[0].bargainType
          this.bargainBaseInfo.evaluateMethod = data.data.list[0].evaluateMethod

          this.$emit('clearData', val)
        }
      }
    }
  }
}
</script>
