<template>
  <!-- 评分规则 -->
  <div>
    <SrmRow style="padding-top: 10px">
      <SrmCol :init-col="2">
        <!-- 评分模板名称 -->
        <span style="padding-right: 11px">{{ $t("bidMod.evalTempName") }}</span>
        <el-input
          v-model="scoreRuleName"
          style="width: 180px"
          disabled
        />
      </SrmCol>
      <SrmCol :init-col="2">
        <!-- 总分 -->
        <span style="padding-right: 11px">{{ $t("bidMod.evalRuleScore") }}</span>
        <el-input
          v-model="totalScore"
          style="width: 180px"
          disabled
        />
      </SrmCol>
    </SrmRow>

    <div class="sou-score-rule-title">
      <!-- 评分细则 -->
      <span class="title-label">{{ $t("bidMod.evalRuleList") }}</span>
      <!--选择-->
      <!-- 选择评分规则 -->
      <QuickSearch
        show-button
        :btn-title="$t('bidMod.selectScoreRule')"
        name="scc_sou_score_rule"
        :pre-query-data="scoreRuleQueryData"
        @close-quicksearch="selectScoreRule"
      />
    </div>

    <el-table
      :data="ruleItems"
      style="width: 100%"
      border
      height="311px"
    >
      <!-- 序号 -->
      <el-table-column
        type="index"
        :label="$t('common.sort')"
        width="50"
      />

      <!--维度-->
      <el-table-column
        prop="dimension"
        :label="$t('bidMod.dimension')"
        width="150"
        :formatter="(row, column, cellValue) => $getDictLabel('SOU_SCORE_RULE_DIMENSION', cellValue)"
      />

      <!--评分项-->
      <el-table-column
        prop="scoreItem"
        :label="$t('bidMod.scoreItem')"
        width="150"
      />

      <!--评分标准-->
      <el-table-column
        prop="scoreStandard"
        :label="$t('bidMod.scoreStandard')"
        min-width="200"
      />

      <!--取值来源-->
      <el-table-column
        prop="scoreSource"
        :label="$t('bidMod.scoreSource')"
        width="150"
        :formatter="(row, column, cellValue) => $getDictLabel('SOU_SOURCE_RULE_SOURCE', cellValue)"
      />

      <!--权重（%）-->
      <el-table-column
        prop="scoreWeight"
        :label="$t('bidMod.scoreWeight')"
        width="100"
      />

      <!--满分值-->
      <el-table-column
        prop="totalScore"
        :label="$t('bidMod.fullScore')"
        width="100"
      />
    </el-table>
  </div>
</template>

<script>
import { bidBuyerHttp, scoreRuleBuyerHttp } from 'modb@/bidding/api'
import QuickSearch from 'lib@/components/QuickSearch'

export default {
  name: 'ScoreRule',

  components: { QuickSearch },

  props: {
    readonly: {
      type: Boolean,
      default: false
    },
    // 投标基础信息
    biddingBase: {
      type: Object,
      default: () => ({})
    },
    isActiveMenu: {
      type: Boolean,
      required: true
    }
  },

  data () {
    return {
      scoreRuleName: '',
      scoreTemplateId: '',
      totalScore: '',
      ruleItems: [],
      scoreRuleListDialogVisible: false
    }
  },

  computed: {
    scoreRuleQueryData () {
      return {
        't.score_rule_status': 'VALID',
        't.sou_type': 'bid',
        't.score_rule_type': this.biddingBase.scoreRuleType
      }
    }
  },

  watch: {
    isActiveMenu: {
      handler (val) {
        if (val) {
          this.getScoreRule()
        }
      },
      immediate: true
    }
  },

  methods: {
    /* 查询评分规则 */
    async getScoreRule () {
      const id = this.scoreTemplateId || this.biddingBase.scoreTemplateId
      if (!id) {
        // id不存在，不需要查询
        return
      }

      const response = await scoreRuleBuyerHttp.getDetail(id)
      if (response && response.data) {
        this.scoreRuleName = response.data.scoreRuleName
        this.totalScore = response.data.totalScore
        this.ruleItems = (response.data.ruleLineList || []).concat()
      }
    },

    /* 导入 打开评分规则弹窗 */
    openScoreRuleListDialog () {
      this.scoreRuleListDialogVisible = true
    },

    /* 选择评分规则 */
    async selectScoreRule (value) {
      const { scoreRuleId = '' } = value || {}
      this.scoreTemplateId = scoreRuleId
      // 查询细则列表
      await this.getScoreRule()
    },

    /* 保存保存评分规则数据 */
    async saveScoreRule (type) {
      if (!this.scoreTemplateId) {
        // '请选择一个评分规则'
        this.$message.warning(this.$t('bidMod.scoreTemplateIdSelect'))
        return
      }

      const response = await bidBuyerHttp.init.editScoreRule({
        projectId: this.biddingBase.projectId,
        scoreRuleType: this.biddingBase.scoreRuleType,
        scoreTemplateId: this.scoreTemplateId,
        isTempSave: type !== 'nextOne'
      })
      if (response) {
        this.$message.success(this.$t('common.success'))

        if (type !== 'nextOne') {
          // 暂存触发 请求数据更新
          await this.getScoreRule()
        }

        // 发起保存成功回调
        this.$emit('temp-save-success', type)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.sou-score-rule-title {
  display: flex;
  margin: 10px 0;
  .title-label {
    padding-right: 10px;
    line-height: 30px;
  }
}
</style>
