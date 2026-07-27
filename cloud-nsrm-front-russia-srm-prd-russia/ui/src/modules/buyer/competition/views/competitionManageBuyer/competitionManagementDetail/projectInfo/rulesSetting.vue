<!--
 * @Author: linyk7 linyk7@meicloud.com
 * @Date: 2023-06-09 14:27:12
 * @LastEditors: linyk7 linyk7@meicloud.com
 * @LastEditTime: 2023-06-12 13:52:29
 * @FilePath: \ui\src\modules\buyer\competition\views\competitionManageBuyer\competitionManagementDetail\projectInfo\rulesSetting.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<template>
  <SrmRow>
    <!--竞价规则-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bidMod.competitionLts.competitionRules')" prop="auctSouProject.auctRule">
        <DictSelect v-model="baseInfoData.auctRule" code="SOU_AUCT_RULE" />
      </el-form-item>
    </SrmCol>
    <!--当竞价规则为"不允许报相同价格,前几名不允许相同价格"时-->
    <SrmCol v-if="baseInfoData.auctRule === 'NO_ALLOW_SAME_PRICE'" :init-col="3">
      <el-form-item label="前几名不允许相同价格" prop="auctSouProject.noAllowSamePriceCount">
        <el-input v-model="baseInfoData.noAllowSamePriceCount" v-input-format="{type:'number'}" />
      </el-form-item>
    </SrmCol>

    <!--涨降幅百分比(%)-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bidMod.competitionLts.floating')" prop="auctSouProject.minPercent">
        <el-input v-model="baseInfoData.minPercent" v-input-format="{type:'float',negative:false}" :disabled="!!baseInfoData.minAmount" @input="amountOrPercentInput" />
      </el-form-item>
    </SrmCol>

    <!--涨降金额-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bidMod.competitionLts.floatingAmount')" prop="auctSouProject.minAmount">
        <el-input v-model="baseInfoData.minAmount" v-input-format="{type:'float',negative:false}" :disabled="!!baseInfoData.minPercent" @input="amountOrPercentInput" />
      </el-form-item>
    </SrmCol>

    <!--公开规则-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bidMod.competitionLts.openRules')" prop="auctSouProject.scopeRule">
        <DictSelect v-model="baseInfoData.scopeRule" code="SOU_AUCT_SCOPE_RULE" />
      </el-form-item>
    </SrmCol>

    <!--启用自动延时竞价-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bidMod.competitionLts.compAutomaticDelayed')" prop="auctSouProject.allowExtendTime">
        <el-switch
          v-model="baseInfoData.allowExtendTime"
          active-value="Y"
          inactive-value="N"
        />
      </el-form-item>
    </SrmCol>

    <template v-if="baseInfoData.allowExtendTime === 'Y'">
      <!--延时竞价触发点(分钟)-->
      <SrmCol :init-col="3">
        <el-form-item :label="$t('bidMod.competitionLts.delayTriggerPoint1')" prop="auctSouProject.extendTrigger">
          <el-input v-model="baseInfoData.extendTrigger" v-input-format="{type:'float',negative:false}" />
        </el-form-item>
      </SrmCol>

      <!--延时分钟数-->
      <SrmCol :init-col="3">
        <el-form-item :label="$t('bidMod.competitionLts.delayMinutes1')" prop="auctSouProject.extendMinute">
          <el-input v-model="baseInfoData.extendMinute" v-input-format="{type:'float',negative:false}" />
        </el-form-item>
      </SrmCol>

      <!--延时竞价最多可报次数-->
      <SrmCol :init-col="3">
        <el-form-item :label="$t('bidMod.competitionLts.delayedTimes')" prop="auctSouProject.extendMaxOrderCount">
          <el-input v-model="baseInfoData.extendMaxOrderCount" v-input-format="{type:'float',negative:false}" />
        </el-form-item>
      </SrmCol>
      <!--延时触发的最大次数-->
      <SrmCol :init-col="3">
        <el-form-item label="延时触发的最大次数" prop="auctSouProject.extendTriggerCount">
          <el-input v-model="baseInfoData.extendTriggerCount" v-input-format="{type:'float',negative:false}" />
        </el-form-item>
      </SrmCol>
      <!--首次延时触发后最长延时时间限制 -->
      <SrmCol :init-col="3">
        <el-form-item label="首次延时触发后最长延时时间限制" prop="auctSouProject.extendMaxMinute">
          <el-input v-model="baseInfoData.extendMaxMinute" v-input-format="{type:'float',negative:false}" />
        </el-form-item>
      </SrmCol>
    </template>
  </SrmRow>
</template>

<script>
/**
 * 规则设置
 */
export default {
  name: 'RulesSetting',

  props: {
    baseInfo: {
      type: Object,
      default: () => { /* nothing */ }
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
    amountOrPercentInput () {
      this.$emit('amount-input')
    }
  }
}
</script>
