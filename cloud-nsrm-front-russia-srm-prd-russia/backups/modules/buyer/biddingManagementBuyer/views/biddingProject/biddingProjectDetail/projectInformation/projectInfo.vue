<template>
  <srm-row>
    <srm-col :init-col="3">
      <!--流程模板-->
      <el-form-item
        :label="$t('bidMod.processConfigId')"
        prop="processConfigId"
      >
        <el-select
          v-model="bidingBaseInfo.processConfigId"
          :disabled="!!bidingBaseInfo.processConfigId && !!bidingBaseInfo.bidingNum"
          @change="processConfigIdChange"
        >
          <el-option
            v-for="item in bidProcessConfigIdList"
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
          v-model="bidingBaseInfo.bidingNum"
          disabled
        />
      </el-form-item>
    </srm-col>

    <srm-col :init-col="3">
      <!--项目名称-->
      <el-form-item
        :label="$t('bidMod.bidingName')"
        prop="bidingName"
      >
        <el-input v-model="bidingBaseInfo.bidingName" />
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
          v-model="bidingBaseInfo.enrollEndDatetime"
          type="datetime"
          value-format="timestamp"
          :placeholder="$t('bidMod.datePicker')"
          :picker-options="cannotLessCurrentTimeOptions"
        />
      </el-form-item>
    </srm-col>

    <srm-col :init-col="3">
      <!--预计投标开始时间-->
      <el-form-item
        label="预计投标开始时间"
        prop="bidingStartDatetime"
      >
        <el-date-picker
          v-model="bidingBaseInfo.bidingStartDatetime"
          type="datetime"
          value-format="timestamp"
          :placeholder="$t('bidMod.datePicker')"
          :picker-options="cannotLessCurrentTimeOptions"
        />
      </el-form-item>
    </srm-col>

    <srm-col :init-col="3">
      <!--投标截止时间-->
      <el-form-item
        :label="$t('bidMod.enrollEndDatetime')"
        prop="bidingEndDatetime"
      >
        <el-date-picker
          v-model="bidingBaseInfo.bidingEndDatetime"
          type="datetime"
          value-format="timestamp"
          :picker-options="bidingEndDatetimePickerOptions"
          :placeholder="$t('bidMod.datePicker')"
        />
      </el-form-item>
    </srm-col>

    <srm-col :init-col="3">
      <!--预计投标地点-->
      <el-form-item
        :label="$t('bidMod.bidingSite')"
        prop="bidingSite"
      >
        <el-input v-model="bidingBaseInfo.bidingSite" />
      </el-form-item>
    </srm-col>

    <srm-col :init-col="3">
      <!--招标范围-->
      <el-form-item
        :label="$t('bidMod.bidingScope')"
        prop="bidingScope"
      >
        <dict-select
          v-model="bidingBaseInfo.bidingScope"
          code="BID_SCOPE"
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
          v-model="bidingBaseInfo.budgetAmount"
          v-input-format="{ type: 'float' }"
        />
      </el-form-item>
    </srm-col>

    <srm-col :init-col="3">
      <!--招标类型-->
      <el-form-item
        :label="$t('bidMod.bidingType')"
        prop="bidingType"
      >
        <dict-select
          v-model="bidingBaseInfo.bidingType"
          code="BID_TYPE"
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
          v-model="bidingBaseInfo.evaluateMethod"
          code="BID_GRADING"
          disabled
        />
      </el-form-item>
    </srm-col>

    <srm-col :init-col="3">
      <!--决标方式-->
      <el-form-item
        :label="$t('bidMod.bidingAwardWay')"
        prop="bidingAwardWay"
      >
        <dict-select
          v-model="bidingBaseInfo.bidingAwardWay"
          code="BID_DECIDE_METHOD"
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
          v-model="bidingBaseInfo.isSyncToPriceLibrary"
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
          v-model="bidingBaseInfo.priceStartTime"
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
          v-model="bidingBaseInfo.priceEndTime"
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
    bidingBase: {
      type: Object
    },
    bidProcessConfigIdList: {
      type: Array,
      default: () => []
    },
    showEnrollEndDatetime: {
      type: Boolean
    }
  },
  data () {
    return {
      bidingEndDatetimePickerOptions: {
        disabledDate: (time) => {
          const start = new Date(this.bidingBaseInfo.bidingStartDatetime)
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
          const start = new Date(this.bidingBaseInfo.priceStartTime)
          return time.getTime() <= start.getTime()
        }
      }
    }
  },
  computed: {
    bidingBaseInfo: {
      get: function () {
        return this.bidingBase
      },
      set: function (val) {
        this.$emit('update:bidingBase', val)
      }
    }
  },
  methods: {
    /* 获取流程配置 */
    async processConfigIdChange (val) {
      if (val) {
        // 设置左边菜单节点信息
        this.$emit('setMenuNodeConfig', val, 'add')

        const data = await this.$http({
          url: '/api-bid/bidProcessConfig/bidProcessConfig/listPage',
          method: 'POST',
          data: { processConfigId: val },
          loading: true
        })
        if (data.data && data.data.list && data.data.list.length === 1) {
          this.bidingBaseInfo.bidingScope = data.data.list[0].bidingScope
          this.bidingBaseInfo.bidingType = data.data.list[0].bidingType
          this.bidingBaseInfo.evaluateMethod = data.data.list[0].evaluateMethod

          this.$emit('clearData', val)
        }
      }
    }
  }
}
</script>
