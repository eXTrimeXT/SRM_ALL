<template>
  <!-- 评分规则 -->
  <div style="padding-top: 10px">
    <el-form
      ref="scoreRuleForm"
      :model="scoreRuleForm"
      :rules="scoreRuleFormRules"
      label-position="left"
      label-width="80px"
      :disabled="readonly"
    >
      <SrmRow>
        <!-- 评标模板 -->
        <SrmCol :init-col="3">
          <el-form-item
            prop="scoreConfigId"
            :label="$t('cusEntry.bidMod.scoreTempName')"
          >
            <el-select
              v-model="scoreRuleForm.scoreConfigId"
              @change="scoreConfigIdChange"
            >
              <el-option
                v-for="item in scoreConfigList"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </SrmCol>
      </SrmRow>
    </el-form>
    <el-button
      type="primary"
      style="margin-bottom: 16px"
      :disabled="readonly || ruleItems.length==0"
      @click="addRuleItem"
    >
      {{ $t('bidMod.addScoreRuleItem') }}
    </el-button>
    <el-button type="ghost" :disabled="readonly || ruleItems.length == 0" @click="upForward">
      {{ $t("priceModel.costElement.moveUp") }}
    </el-button>
    <el-button type="ghost" :disabled="readonly || ruleItems.length == 0" @click="downForward">
      {{ $t("priceModel.costElement.moveDown") }}
    </el-button>
    <el-table
      :data="ruleItems"
      style="width: 100%"
      border
      @selection-change="handleSelectionChange"
    >
      <el-table-column
        type="selection"
        width="55"
        fixed="left"
      />
      <!-- 序号 -->
      <el-table-column
        align="center"
        type="index"
        :label="$t('common.sort')"
        width="50"
      />
      <!--打分项-->
      <el-table-column
        align="center"
        prop="scoreItem"
        :label="$t('cusEntry.bidMod.scoreItem')"
        :render-header="_addStarToColumn"
        width="120"
      >
        <template slot-scope="scope">
          <dict-select
            v-model="scope.row.scoreItem"
            code="SOU_SCORE_CONFIG_ITEM"
            :disabled="readonly"
            @change="val => scoreItemChange(val,scope.row)"
          />
        </template>
      </el-table-column>
      <!--*评审项-->
      <el-table-column
        align="center"
        prop="reviewItem"
        :label="$t('cusEntry.bidMod.reviewItem')"
        :render-header="_addStarToColumn"
        min-width="120"
      >
        <template slot-scope="scope">
          <dict-select
            v-if="scope.row.scoreItem==='COM_REVIEW'"
            v-model="scope.row.reviewItem"
            code="SCORE_REVIEW_ITEM"
            :disabled="readonly"
          />
          <el-input
            v-else
            v-model="scope.row.reviewItem"
            :disabled="readonly"
          />
        </template>
      </el-table-column>
      <!--评分说明-->
      <el-table-column
        align="center"
        prop="scoreDesc"
        :label="$t('cusEntry.bidMod.scoreDesc')"
        :render-header="_addStarToColumn"
        min-width="150"
      >
        <template slot-scope="scope">
          <el-input v-model="scope.row.scoreDesc" :disabled="readonly" />
        </template>
      </el-table-column>
      <!--最高分值-->
      <el-table-column
        align="center"
        prop="maxScore"
        :label="$t('cusEntry.bidMod.maxScore')"
        width="100"
      >
        <template slot-scope="scope">
          <el-input-number
            v-model="scope.row.maxScore"
            :disabled="readonly"
            style="width: 100%"
            :controls="false"
            :min="0"
            :max="100"
          />
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        :label="$t('common.operation')"
        width="80"
      >
        <template slot-scope="scope">
          <el-button v-if="!readonly" type="text" @click="deleteRow(scope.$index)">
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { bidBuyerHttp, scoreRuleBuyerHttp } from 'modcb@/biddingBuyer/api'

export default {
  name: 'ScoreRule',

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
      scoreRuleForm: {
        scoreConfigId: null
      },
      scoreRuleFormRules: {
        scoreConfigId: [{ required: true, message: this.$t('bidMod.bidMsgList[0]') }]
      },
      selectedRows: [],
      ruleItems: [],
      scoreConfigList: []
    }
  },

  watch: {
    isActiveMenu: {
      async handler (val) {
        if (val) {
          await this.getScoreConfigList()
          this.getScoreRule()
        }
      },
      immediate: true
    }
  },

  methods: {
    scoreItemChange (val, row) {
      if (val == 'COM_REVIEW') {
        row.reviewItem = '' // 选择综合评审时, 清空评审项
      }
    },
    upForward () {
      if (!this.selectedRows.length) return this.$message.warning(this.$t('outsource.pleaseCheckList'))
      // 仅可勾选一条数据
      if (this.selectedRows.length > 1) return this.$message.warning(this.$t('cusEntry.supplement20250121.onlyOnePieceOfDataCanBeSelected'))
      let index = this.ruleItems.indexOf(this.selectedRows[0])
      // 第一列无法上移
      if (index === 0) return this.$message.warning(this.$t('cusEntry.supplement20250121.theFirstColumnCannotBeMovedUp'))
      this.ruleItems.splice(index, 1)
      this.ruleItems.splice(index - 1, 0, this.selectedRows[0])
    },
    downForward () {
      if (!this.selectedRows.length) return this.$message.warning(this.$t('outsource.pleaseCheckList'))
      // 仅可勾选一条数据
      if (this.selectedRows.length > 1) return this.$message.warning(this.$t('cusEntry.supplement20250121.onlyOnePieceOfDataCanBeSelected'))
      let index = this.ruleItems.indexOf(this.selectedRows[0])
      // 最后一列无法下移
      if (index === this.ruleItems.length - 1) return this.$message.warning(this.$t('cusEntry.supplement20250121.theLastColumnCannotBeMovedDown'))
      this.ruleItems.splice(index, 1)
      this.ruleItems.splice(index + 1, 0, this.selectedRows[0])
    },
    handleSelectionChange (selection) {
      this.selectedRows = selection
    },
    addRuleItem () {
      this.ruleItems.push({
        projectId: this.biddingBase.projectId,
        configDetailId: this.ruleItems[0].configDetailId,
        scoreConfigId: this.ruleItems[0].scoreConfigId,
        scoreRuleId: '', // 主键
        scoreItem: '',
        reviewItem: '',
        scoreDesc: '',
        maxScore: ''
      })
    },
    deleteRow (index) {
      this.ruleItems.splice(index, 1)
    },
    // 获取 评标模板下拉框
    getScoreConfigList () {
      this.$http({
        url: '/api-pj/sou/scoreConfig/listValidScoreConfig',
        method: 'GET',
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.scoreConfigList = res.data.map(item => {
            return {
              id: item.scoreConfigId,
              value: item.scoreConfigId,
              label: item.scoreTempName
            }
          })
        }
      })
    },
    // 切换评标模板
    scoreConfigIdChange (val) {
      val && this.$http({
        url: `/api-pj/sou/scoreConfig/listDetail?scoreConfigId=${val}`,
        method: 'GET',
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.ruleItems = res.data
        }
      })
    },
    // 查询评分规则
    async getScoreRule () {
      const id = this.biddingBase.projectId
      if (!id) {
        // id不存在，不需要查询
        return
      }
      const response = await scoreRuleBuyerHttp.getDetail(id)
      if (response && response.data) {
        this.ruleItems = response.data
        this.scoreRuleForm.scoreConfigId = response.data[0].scoreConfigId
      }
    },
    /* 保存保存评分规则数据 */
    async saveScoreRule (type) {
      this.$refs.scoreRuleForm.validate(async valid => {
        if (valid) {
          let flag = this.ruleItems.some(item => !item.scoreItem || !item.reviewItem || !item.scoreDesc)
          if (flag) {
            this.$message.warning(this.$t('cusEntry.biddingSettings.messageTip2'))
            return
          }
          // 校验【技术评审】的【最高分值】之和须为100
          let scoreTotal = 0
          let techFlag = false
          this.ruleItems.map(item => {
            if (item.scoreItem == 'TEH_REVIEW') {
              scoreTotal += item.maxScore
              techFlag = true
            }
          })
          if (techFlag && scoreTotal != 100) {
            this.$message.error(this.$t('cusEntry.biddingSettings.messageTip1'))
            return
          }

          const response = await bidBuyerHttp.init.editScoreRule({
            projectId: this.biddingBase.projectId,
            scoreRuleList: this.ruleItems,
            tempSave: type !== 'nextOne'
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
      })
    }
  }
}
</script>
<style lang="scss" scoped></style>
