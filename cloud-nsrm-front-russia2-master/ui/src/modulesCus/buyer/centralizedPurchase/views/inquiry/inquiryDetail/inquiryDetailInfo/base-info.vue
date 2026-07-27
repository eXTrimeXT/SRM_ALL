<template>
  <SrmRow>
    <!--项目编号-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('cusEntry.centralizedPurchase.projectNo')">
        <el-input v-model="headerData.designProjectCode" disabled />
      </el-form-item>
    </SrmCol>
    <!--项目名称-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('cusEntry.centralizedPurchase.projectName')" prop="designProjectName">
        <el-input v-model="headerData.designProjectName" disabled />
      </el-form-item>
    </SrmCol>
    <!--轮次-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('cusEntry.centralizedPurchase.round')" prop="currentRound">
        <el-input v-model="headerData.currentRound" disabled />
      </el-form-item>
    </SrmCol>
    <!--创建人-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('bidMod.createdBy')">
        <el-input v-model="headerData.designCreateNickName" disabled />
      </el-form-item>
    </SrmCol>
    <!--联系方式-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('cusEntry.centralizedPurchase.contactMethod')">
        <el-input v-model="headerData.designCreatePhone" disabled />
      </el-form-item>
    </SrmCol>
    <!--部门-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('cusEntry.centralizedPurchase.department')">
        <el-input v-model="headerData.designOrgDeptName" disabled />
      </el-form-item>
    </SrmCol>
    <!--项目金额-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('cusEntry.centralizedPurchase.projectAmount')">
        <el-input v-model="headerData.designProjMoney" disabled />
      </el-form-item>
    </SrmCol>
    <!--供货范围-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('cusEntry.centralizedPurchase.supplyScope')">
        <DictSelect
          v-model="headerData.designArea"
          code="REGION"
          multiple
          disabled
        />
      </el-form-item>
    </SrmCol>
    <!--调价申请单号-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('cusEntry.centralizedPurchase.adjustPriceNo')">
        <el-input v-model="headerData.adjustCode" disabled />
      </el-form-item>
    </SrmCol>
    <!--调价申请名称-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('cusEntry.centralizedPurchase.adjustPriceName')">
        <el-input v-model="headerData.adjustName" disabled />
      </el-form-item>
    </SrmCol>
    <!--项目介绍-->
    <SrmCol :init-col="1">
      <el-form-item :label="$t('cusEntry.centralizedPurchase.projectDesc')">
        <el-input
          v-model="headerData.designProjIntroduce"
          type="textarea"
          :rows="2"
          maxlength="500"
          show-word-limit
          disabled
        />
      </el-form-item>
    </SrmCol>
    <!--定价思路-->
    <SrmCol :init-col="1">
      <el-form-item :label="$t('cusEntry.centralizedPurchase.pricingStrategy')">
        <el-input
          v-model="headerData.designPricingIdeas"
          type="textarea"
          :rows="2"
          maxlength="500"
          show-word-limit
          disabled
        />
      </el-form-item>
    </SrmCol>
  </SrmRow>
</template>

<script>
/**
 * 基础信息
 */
import { cannotLessCurrentTime } from '@/library/mixins/datePickerOptions'
import { SOU_SCORE_RULE_TYPE_ENUM } from '@/library/composition/origin/enum'

export default {
  name: 'BaseInfo',

  mixins: [cannotLessCurrentTime],

  props: {
    header: {
      type: Object,
      default: () => { /* nothing */ }
    }
  },

  data () {
    return {
      orderEndTimePickerOptions: {
        disabledDate: time => {
          const [nowDate, startDate, valueDate] = [
            this.$dayjs().hour(0).minute(0).second(0).unix(),
            this.$dayjs(this.headerData.orderStartTime).hour(0).minute(0).second(0).unix(),
            this.$dayjs(time).unix()
          ]
          return (valueDate < startDate) || (valueDate < nowDate)
        }
      }
    }
  },

  computed: {
    headerData: {
      get: function () {
        return this.header
      },
      set: function (val) {
        this.$emit('update:header', val)
      }
    },

    // 是否禁止选择报价方式
    orderWayDisabled () {
      return this.headerData.scoreRuleType === SOU_SCORE_RULE_TYPE_ENUM.COMPOSITE_PRICE
    }
  }
}
</script>
