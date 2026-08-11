<template>
  <!-- 评分规则 -->
  <div>
    <srm-row style="padding-top: 10px">
      <srm-col :init-col="2">
        <!-- 评分模板名称 -->
        <span style="padding-right: 11px">{{ $t("bidMod.evalTempName") }}</span>
        <el-input
          v-model="ruleConfigName"
          style="width: 180px"
          disabled
        />
      </srm-col>
      <srm-col :init-col="2">
        <!-- 总分 -->
        <span style="padding-right: 11px">{{ $t("bidMod.evalRuleScore") }}</span>
        <el-input
          v-model="totalScore"
          style="width: 180px"
          disabled
        />
      </srm-col>
    </srm-row>

    <MainHeader
      :l-span="22"
      :r-span="2"
      style="padding: 10px 3px"
    >
      <template slot="left">
        <!-- 评分细则 -->
        <span style="padding-right: 11px">{{ $t("bidMod.evalRuleList") }}</span>
        <el-button
          type="primary"
          :disabled="readOnly"
          class="detail-pbtn"
          @click="openScoreRuleListDialog"
        >
          {{ $t("common.add") }}
        </el-button>
      </template>
    </MainHeader>

    <el-table
      :data="ruleItems"
      style="width: 100%"
      border
      height="311px"
    >
      <!-- 序号 -->
      <el-table-column
        align="center"
        type="index"
        :label="$t('common.sort')"
        width="65"
      />

      <!--维度-->
      <el-table-column
        align="center"
        prop="scoreDimension"
        :label="$t('bidMod.dimension')"
        width="150"
      >
        <template slot-scope="scope">
          <span>{{ $getDictLabel('BIDDING_SCOR_DIM', scope.row.scoreDimension) }}</span>
        </template>
      </el-table-column>

      <!--评分项-->
      <el-table-column
        align="center"
        prop="scoreItem"
        :label="$t('bidMod.scoreItem')"
        width="150"
      />

      <!--评分标准-->
      <el-table-column
        align="center"
        prop="scoreStandard"
        :label="$t('bidMod.scoreStandard')"
        min-width="200"
      />

      <!--取值来源-->
      <el-table-column
        align="center"
        prop="scoreSource"
        :label="$t('bidMod.scoreSource')"
        width="150"
      >
        <template slot-scope="scope">
          <span>{{ $getDictLabel('SOURCE_VALUE', scope.row.scoreSource) }}</span>
        </template>
      </el-table-column>

      <!--权重（%）-->
      <el-table-column
        align="center"
        prop="scoreWeight"
        :label="$t('bidMod.scoreWeight')"
        width="100"
      />

      <el-table-column
        align="center"
        prop="fullScore"
        :label="$t('bidMod.fullScore')"
        width="100"
      />
    </el-table>

    <!-- 弹框区域-->
    <score-rule-list-dialog
      :visible.sync="scoreRuleListDialogVisible"
      :bargain-id="scopeBargainId"
      :bargain-base="bargainBase"
      @saveScoreRuleList="saveScoreRuleList"
    />
  </div>
</template>

<script>
import MainHeader from 'lib@/components/Table/MainHeader'
import scoreRuleListDialog from './judgingRules/scoreRuleListDialog'

export default {
  name: 'JudgingRules',
  components: {
    MainHeader,
    scoreRuleListDialog
  },
  props: {
    scopeBargainId: {
      // 招标ID
      type: [Number, String],
      default: ''
    },
    readOnly: {
      type: Boolean,
      default: false
    },
    // 投标基础信息
    bargainBase: {
      type: Object,
      default: () => {}
    },
    activeMenu: {
      type: String,
      default: ''
    }
  },
  data () {
    return {
      ruleConfigName: '',
      totalScore: '',
      ruleItems: [],
      scoreRuleListDialogVisible: false
    }
  },
  created () {
    if (this.activeMenu === 't14') {
      this.getScoreRule()
    }
  },
  methods: {
    /* 查询评分规则 */
    getScoreRule () {
      this.$api.brg.inquiryByProject.getScoreRule(this.scopeBargainId).then(data => {
        if (data && data.data) {
          this.ruleConfigName = data.data.ruleConfigName
          this.totalScore = data.data.totalScore
          this.ruleItems = data.data.scoreRuleLineList || []
        }
      })
    },

    /* 导入 打开评分规则弹窗 */
    openScoreRuleListDialog () {
      this.scoreRuleListDialogVisible = true
    },

    /* 设置评分规则 */
    saveScoreRuleList (data) {
      this.ruleConfigName = data.ruleConfigName
      this.totalScore = data.totalScore
      this.ruleItems = data.ruleItems
    },

    /* 保存保存评分规则数据 */
    saveJudgingRules (type) {
      if (!this.ruleConfigName) {
        this.$message.warning('请选择一个评分规则')
        return
      }
      this.$api.brg.inquiryByProject.tempSaveOrSubmitScoreRule({
          bargainId: this.scopeBargainId,
          // 评分规则名称
          ruleConfigName: this.ruleConfigName,
          isTempSave: !(type === 'nextOne')
        }).then(() => {
        this.$message.success(this.$t('common.success'))

        // 下一步保存触发
        if (type === 'nextOne') {
          this.$emit('fetchBaseInfo')
          this.$emit('saveNextTodo') // 保存后下一步操作
        } else {
          // 暂存触发
          // 请求数据更新
          this.getScoreRule()
          // 更新节点
          this.$emit('updateProcessNode')
        }
      })
    }
  }
}
</script>
