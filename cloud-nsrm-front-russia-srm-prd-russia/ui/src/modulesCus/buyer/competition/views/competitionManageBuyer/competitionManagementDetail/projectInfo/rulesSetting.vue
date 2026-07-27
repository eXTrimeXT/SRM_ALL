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
      <el-form-item :label="$t('bidMod.competitionLts.competitionRules')" prop="auctSouProject.souRules">
        <DictSelect v-model="baseInfoData.souRules" code="COMPETITION_RULES" />
      </el-form-item>
    </SrmCol>

    <!--公开规则-->
    <!-- <SrmCol :init-col="3">
      <el-form-item :label="$t('bidMod.competitionLts.openRules')" prop="auctSouProject.publicRules">
        <DictSelect
          v-model="baseInfoData.publicRules"
          code="SOU_AUCT_SCOPE_RULE"
          disabled
        />
      </el-form-item>
    </SrmCol> -->

    <!--每项物资报价次数-->
    <SrmCol :init-col="3">
      <el-form-item :label="$t('cusEntry.competition.quoteNumOfMaterial')" prop="auctSouProject.orderNum">
        <el-input v-model="baseInfoData.orderNum" v-input-format="{ type: 'integer', negative: false }" />
      </el-form-item>
    </SrmCol>

    <!--报价上限-->
    <SrmCol :init-col="3">
      <el-form-item
        prop="auctSouProject.quoteCap"
        :rules="{
          required: baseInfoData.souRules === 'FORWARD_RULE',
          message: this.$t('cusEntry.tipMessage.quoteCap')
        }"
      >
        <template slot="label">
          {{ $t('cusEntry.competition.quoteCap') }}
          <el-tooltip
            class="item"
            effect="dark"
            :content="$t('cusEntry.competition.noticeQuoteCap')"
            placement="top"
          >
            <em class="el-icon-question" />
          </el-tooltip>
        </template>
        <el-input v-model="baseInfoData.quoteCap" v-input-format="{ type: 'float', negative: false }" />
      </el-form-item>
    </SrmCol>
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
