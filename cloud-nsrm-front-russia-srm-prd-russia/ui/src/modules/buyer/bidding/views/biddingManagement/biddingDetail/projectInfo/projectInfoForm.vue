<template>
  <SrmRow>
    <SrmCol :init-col="3">
      <!--流程模板-->
      <el-form-item :label="$t('bidMod.processConfigId')" prop="processConfigId">
        <el-select
          v-model="biddingBaseInfo.processConfigId"
          :disabled="!!biddingBaseInfo.processConfigId && !!biddingBaseInfo.souNo"
          @change="processConfigIdChange"
        >
          <el-option
            v-for="item in processList"
            :key="item.processConfigId"
            :label="item.processConfigName"
            :value="item.processConfigId"
          />
        </el-select>
      </el-form-item>
    </SrmCol>

    <SrmCol :init-col="3">
      <!--项目编号-->
      <el-form-item :label="$t('bidMod.bidingNum')">
        <el-input v-model="biddingBaseInfo.souNo" disabled />
      </el-form-item>
    </SrmCol>

    <SrmCol :init-col="3">
      <!--项目名称-->
      <el-form-item :label="$t('bidMod.bidingName')" prop="souName">
        <el-input v-model="biddingBaseInfo.souName" />
      </el-form-item>
    </SrmCol>

    <SrmCol v-if="showEnrollEndDatetime" :init-col="3">
      <!--报名截止时间-->
      <el-form-item :label="$t('bidMod.registrationDeadline')" prop="signUpEndTime">
        <el-date-picker
          v-model="biddingBaseInfo.signUpEndTime"
          type="datetime"
          value-format="yyyy-MM-dd HH:mm:ss"
          :placeholder="$t('bidMod.datePicker')"
          :picker-options="cannotLessCurrentTimeOptions"
        />
      </el-form-item>
    </SrmCol>

    <SrmCol :init-col="3">
      <!--预计投标开始时间-->
      <el-form-item label="预计投标开始时间" prop="orderStartTime">
        <el-date-picker
          v-model="biddingBaseInfo.orderStartTime"
          type="datetime"
          value-format="yyyy-MM-dd HH:mm:ss"
          :placeholder="$t('bidMod.datePicker')"
          :picker-options="cannotLessCurrentTimeOptions"
        />
      </el-form-item>
    </SrmCol>

    <SrmCol :init-col="3">
      <!--投标截止时间-->
      <el-form-item label="投标截止时间" prop="orderEndTime">
        <el-date-picker
          v-model="biddingBaseInfo.orderEndTime"
          type="datetime"
          value-format="yyyy-MM-dd HH:mm:ss"
          :picker-options="orderEndTimePickerOptions"
          :placeholder="$t('bidMod.datePicker')"
        />
      </el-form-item>
    </SrmCol>

    <SrmCol :init-col="3">
      <!--预计招标地点-->
      <el-form-item label="预计招标地点" prop="orderSite">
        <el-input v-model="biddingBaseInfo.orderSite" />
      </el-form-item>
    </SrmCol>

    <SrmCol :init-col="3">
      <!--招标范围-->
      <el-form-item :label="$t('bidMod.bidingScope')" prop="publishScope">
        <DictSelect
          v-model="biddingBaseInfo.publishScope"
          code="SOU_PUBLISH_SCOPE"
          disabled
        />
      </el-form-item>
    </SrmCol>

    <SrmCol :init-col="3">
      <!--预算金额-->
      <el-form-item :label="$t('bidMod.budgetAmount')" prop="budgetAmount">
        <el-input v-model="biddingBaseInfo.budgetAmount" v-input-format="{ type: 'float' }" />
      </el-form-item>
    </SrmCol>

    <SrmCol :init-col="3">
      <!--招标类型-->
      <el-form-item :label="$t('bidMod.bidingType')" prop="bargainType">
        <DictSelect
          v-model="biddingBaseInfo.bargainType"
          code="SOU_BRG_TYPE"
          disabled
        />
      </el-form-item>
    </SrmCol>

    <SrmCol :init-col="3">
      <!--评分规则-->
      <el-form-item :label="$t('bidMod.evaluateMethod')" prop="scoreRuleType">
        <DictSelect
          v-model="biddingBaseInfo.scoreRuleType"
          code="SOU_SCORE_RULE_TYPE"
          disabled
        />
      </el-form-item>
    </SrmCol>

    <SrmCol :init-col="3">
      <!--决标方式-->
      <el-form-item :label="$t('bidMod.bidingAwardWay')" prop="orderWay">
        <DictSelect v-model="biddingBaseInfo.orderWay" code="SOU_ORDER_WAY" />
      </el-form-item>
    </SrmCol>

    <SrmCol :init-col="3">
      <!--是否进价格库-->
      <el-form-item :label="$t('bid_mod.isSyncToPriceLibrary')" prop="isSyncToPriceLibrary">
        <DictSelect v-model="biddingBaseInfo.isSyncToPriceLibrary" code="YES_OR_NO" />
      </el-form-item>
    </SrmCol>

    <SrmCol :init-col="3">
      <!--价格有效期自-->
      <el-form-item :label="$t('bid_mod.defaultPriceValidFrom')" prop="priceStartTime">
        <el-date-picker
          v-model="biddingBaseInfo.priceStartTime"
          type="date"
          value-format="yyyy-MM-dd"
          :placeholder="$t('bidMod.datePicker')"
        />
      </el-form-item>
    </SrmCol>

    <SrmCol :init-col="3">
      <!--价格有效期至-->
      <el-form-item :label="$t('bid_mod.defaultPriceValidTo')" prop="priceEndTime">
        <el-date-picker
          v-model="biddingBaseInfo.priceEndTime"
          type="date"
          value-format="yyyy-MM-dd"
          :picker-options="priceEndTimePickerOptions"
          :placeholder="$t('bidMod.datePicker')"
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
    biddingBase: {
      type: Object,
      required: true
    },
    processList: {
      type: Array,
      default: () => []
    },
    showEnrollEndDatetime: {
      type: Boolean,
      required: true
    }
  },

  data () {
    return {
      orderEndTimePickerOptions: {
        disabledDate: time => {
          const [nowDate, startDate, valueDate] = [
            this.$dayjs().hour(0).minute(0).second(0).unix(),
            this.$dayjs(this.biddingBaseInfo.orderStartTime).hour(0).minute(0).second(0).unix(),
            this.$dayjs(time).unix()
          ]
          return (valueDate < startDate) || (valueDate < nowDate)
        }
      },
      priceEndTimePickerOptions: {
        disabledDate: time => {
          const [startDate, valueDate] = [
            this.$dayjs(this.biddingBaseInfo.priceStartTime).unix(),
            this.$dayjs(time).unix()
          ]
          return valueDate <= startDate
        }
      }
    }
  },

  computed: {
    biddingBaseInfo: {
      get: function () {
        return this.biddingBase
      },
      set: function (val) {
        this.$emit('update:biddingBase', val)
      }
    }
  },

  methods: {
    /* 获取流程配置 */
    processConfigIdChange (val) {
      if (val) {
        // 设置左边菜单节点信息
        this.$emit('set-menu-config', val, 'add')

        const processData = this.processList.find(item => item.processConfigId === val)
        console.log(processData)

        if (processData) {
          this.$set(this.biddingBaseInfo, 'publishScope', processData.publishScope)
          this.$set(this.biddingBaseInfo, 'bargainType', processData.bargainType)
          this.$set(this.biddingBaseInfo, 'scoreRuleType', processData.scoreRuleType)

          this.$emit('clear-data', val)
        }
      }
    }
  }
}
</script>
