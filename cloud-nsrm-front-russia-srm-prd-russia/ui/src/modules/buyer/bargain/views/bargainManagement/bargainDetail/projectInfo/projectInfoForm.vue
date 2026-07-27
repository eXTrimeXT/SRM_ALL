<template>
  <SrmRow>
    <SrmCol :init-col="3">
      <!--流程模板-->
      <el-form-item :label="$t('bidMod.processConfigId')" prop="processConfigId">
        <el-select
          v-model="bargainBaseInfo.processConfigId"
          :disabled="!!bargainBaseInfo.processConfigId && !!bargainBaseInfo.souNo"
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
        <el-input v-model="bargainBaseInfo.souNo" disabled />
      </el-form-item>
    </SrmCol>

    <SrmCol :init-col="3">
      <!--项目名称-->
      <el-form-item :label="$t('bidMod.bidingName')" prop="souName">
        <el-input v-model="bargainBaseInfo.souName" />
      </el-form-item>
    </SrmCol>

    <SrmCol v-if="showEnrollEndDatetime" :init-col="3">
      <!--报名截止时间-->
      <el-form-item :label="$t('bidMod.registrationDeadline')" prop="signUpEndTime">
        <el-date-picker
          v-model="bargainBaseInfo.signUpEndTime"
          type="datetime"
          value-format="yyyy-MM-dd HH:mm:ss"
          :placeholder="$t('bidMod.datePicker')"
          :picker-options="cannotLessCurrentTimeOptions"
        />
      </el-form-item>
    </SrmCol>

    <SrmCol :init-col="3">
      <!--预计报价开始时间-->
      <el-form-item :label="$t('bidMod.beginQuote')" prop="orderStartTime">
        <el-date-picker
          v-model="bargainBaseInfo.orderStartTime"
          type="datetime"
          value-format="yyyy-MM-dd HH:mm:ss"
          :placeholder="$t('bidMod.datePicker')"
          :picker-options="cannotLessCurrentTimeOptions"
        />
      </el-form-item>
    </SrmCol>

    <SrmCol :init-col="3">
      <!--报价截止时间-->
      <el-form-item :label="$t('bidMod.quotedeadline')" prop="orderEndTime">
        <el-date-picker
          v-model="bargainBaseInfo.orderEndTime"
          type="datetime"
          value-format="yyyy-MM-dd HH:mm:ss"
          :picker-options="orderEndTimePickerOptions"
          :placeholder="$t('bidMod.datePicker')"
        />
      </el-form-item>
    </SrmCol>

    <SrmCol :init-col="3">
      <!--预计询价地点-->
      <el-form-item :label="$t('bidMod.bidingSite1')" prop="orderSite">
        <el-input v-model="bargainBaseInfo.orderSite" />
      </el-form-item>
    </SrmCol>

    <SrmCol :init-col="3">
      <!--询价范围-->
      <el-form-item :label="$t('bidMod.bidingScope1')" prop="publishScope">
        <DictSelect
          v-model="bargainBaseInfo.publishScope"
          code="SOU_PUBLISH_SCOPE"
          disabled
        />
      </el-form-item>
    </SrmCol>

    <SrmCol :init-col="3">
      <!--预算金额-->
      <el-form-item :label="$t('bidMod.budgetAmount')" prop="budgetAmount">
        <el-input v-model="bargainBaseInfo.budgetAmount" v-input-format="{ type: 'float' }" />
      </el-form-item>
    </SrmCol>

    <SrmCol :init-col="3">
      <!--询价类型-->
      <el-form-item :label="$t('bidMod.bidingType1')" prop="bargainType">
        <DictSelect
          v-model="bargainBaseInfo.bargainType"
          code="SOU_BRG_TYPE"
          disabled
        />
      </el-form-item>
    </SrmCol>

    <SrmCol :init-col="3">
      <!--评分规则-->
      <el-form-item :label="$t('bidMod.evaluateMethod')" prop="scoreRuleType">
        <DictSelect
          v-model="bargainBaseInfo.scoreRuleType"
          code="SOU_SCORE_RULE_TYPE"
          disabled
        />
      </el-form-item>
    </SrmCol>

    <SrmCol :init-col="3">
      <!--决标方式-->
      <el-form-item :label="$t('bidMod.bidingAwardWay')" prop="orderWay">
        <DictSelect v-model="bargainBaseInfo.orderWay" code="SOU_ORDER_WAY" />
      </el-form-item>
    </SrmCol>

    <SrmCol :init-col="3">
      <!--是否进价格库-->
      <el-form-item :label="$t('bid_mod.isSyncToPriceLibrary')" prop="isSyncToPriceLibrary">
        <DictSelect v-model="bargainBaseInfo.isSyncToPriceLibrary" code="YES_OR_NO" />
      </el-form-item>
    </SrmCol>

    <SrmCol :init-col="3">
      <!--价格有效期自-->
      <el-form-item :label="$t('bid_mod.defaultPriceValidFrom')" prop="priceStartTime">
        <el-date-picker
          v-model="bargainBaseInfo.priceStartTime"
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
          v-model="bargainBaseInfo.priceEndTime"
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
    bargainBase: {
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
            this.$dayjs(this.bargainBaseInfo.orderStartTime).hour(0).minute(0).second(0).unix(),
            this.$dayjs(time).unix()
          ]
          return (valueDate < startDate) || (valueDate < nowDate)
        }
      },
      priceEndTimePickerOptions: {
        disabledDate: time => {
          const [startDate, valueDate] = [
            this.$dayjs(this.bargainBaseInfo.priceStartTime).unix(),
            this.$dayjs(time).unix()
          ]
          return valueDate <= startDate
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
    processConfigIdChange (val) {
      if (val) {
        // 设置左边菜单节点信息
        this.$emit('set-menu-config', val, 'add')

        const processData = this.processList.find(item => item.processConfigId === val)

        if (processData) {
          this.bargainBaseInfo.publishScope = processData.publishScope
          this.bargainBaseInfo.bargainType = processData.bargainType
          this.bargainBaseInfo.scoreRuleType = processData.scoreRuleType

          this.$emit('clear-data', val)
        }
      }
    }
  }
}
</script>
