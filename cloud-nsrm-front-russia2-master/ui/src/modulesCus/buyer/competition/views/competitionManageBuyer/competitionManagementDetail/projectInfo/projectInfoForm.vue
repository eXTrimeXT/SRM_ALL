<template>
  <SrmRow>
    <!--竞价单号-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bidMod.competitionLts.souNo')">
        <el-input v-model="baseInfoData.souNo" disabled />
      </el-form-item>
    </SrmCol>
    <!--竞价标题-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('cusEntry.bidMod.competionName')" prop="souName">
        <el-input v-model="baseInfoData.souName" />
      </el-form-item>
    </SrmCol>
    <!--招标编号-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('cusEntry.bidMod.extProjectNo')" prop="extProjectNo">
        <el-input v-model="baseInfoData.extProjectNo" disabled />
      </el-form-item>
    </SrmCol>
    <!--报名开始时间-->
    <SrmCol v-if="showSignUp" :init-col="3">
      <el-form-item :label="$t('cusEntry.competition.signUpStartTime')" prop="signUpStartTime">
        <el-date-picker
          v-model="baseInfoData.signUpStartTime"
          type="datetime"
          :format="$formatDatePickerTime"
          value-format="yyyy-MM-dd HH:mm:ss"
          disabled
        />
      </el-form-item>
    </SrmCol>
    <!--报名截止时间-->
    <SrmCol v-if="showSignUp" :init-col="3">
      <el-form-item :label="$t('bidMod.registrationDeadline')" prop="signUpEndTime">
        <el-date-picker
          v-model="baseInfoData.signUpEndTime"
          type="datetime"
          :format="$formatDatePickerTime"
          value-format="yyyy-MM-dd HH:mm:ss"
          :placeholder="$t('bidMod.optionDate')"
          :picker-options="cannotLessCurrentTimeOptions"
        />
      </el-form-item>
    </SrmCol>

    <!--竞价开始时间-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bidMod.bidStartTime')" prop="orderStartTime">
        <el-date-picker
          v-model="baseInfoData.orderStartTime"
          type="datetime"
          :format="$formatDatePickerTime"
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
          :format="$formatDatePickerTime"
          value-format="yyyy-MM-dd HH:mm:ss"
          :picker-options="endTiumePickerOptions"
          :placeholder="$t('bidMod.optionDate')"
        />
      </el-form-item>
    </SrmCol>

    <SrmCol :init-col="3">
      <el-form-item :label="$t('cusEntry.competition.publishSubjectTime')" prop="publishTime">
        <el-date-picker
          v-model="baseInfoData.publishTime"
          type="datetime"
          :format="$formatDatePickerTime"
          value-format="yyyy-MM-dd HH:mm:ss"
          disabled
        />
      </el-form-item>
    </SrmCol>

    <!--创建时间-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('common.creationTime')">
        <el-date-picker
          v-model="baseInfoData.creationDate"
          :format="$formatDatePickerTime"
          value-format="yyyy-MM-dd HH:mm:ss"
          disabled
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
  name: 'ProjectInfoForm',

  mixins: [cannotLessCurrentTime],

  props: {
    processList: {
      type: Array,
      default: () => []
    },
    baseInfo: {
      type: Object,
      default: () => { /* nothing */ }
    },
    showSignUp: {
      type: Boolean,
      default: true
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
          this.baseInfoData.publishScope = processData.publishScope // 竞价范围
          this.baseInfoData.scoreRuleType = processData.scoreRuleType // 评分规则

          // this.$emit('clear-data', val)
        }
      }
    },

    /* 选择一个外币，查询税率 */
    async getRateByCode (fromCode) {
      if (!fromCode) {
        this.baseInfoData.priceTax = ''
        return
      }
      const toCode = 'CNY'
      if (toCode === fromCode) {
        this.baseInfoData.priceTax = 1
        return
      }

      const paramData = {
        toCurrencyCode: toCode,
        fromCurrencyCode: fromCode
      }
      const data = await this.$api.base.purchase.purchaseExchangeRate(paramData)
      let list = data?.data?.list || []

      if (!list.length) {
        this.baseInfoData.priceTax = ''
        this.$message.warning(
          `${fromCode} TO ${toCode} 没有配置汇率!`
        )
        return
      }
      list = list.sort((a, b) => new Date(b.creationDate).getTime() - new Date(a.creationDate).getTime())
      this.baseInfoData.priceTax = list[0].priceTax
    }
  }
}
</script>
