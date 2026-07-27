<template>
  <srm-dialog
    :title="$t('bidMod.scoreRuleList')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <el-table
      :data="displayScoreItem"
      style="width: 100%"
      border
      height="200"
      highlight-current-row
      @current-change="selectChange"
      @row-dblclick="save"
    >
      <el-table-column
        align="center"
        type="index"
        width="50"
      />

      <el-table-column
        align="center"
        prop="ruleConfigCode"
        :label="$t('bidMod.scoreRuleModelNo')"
        width="150"
        :show-overflow-tooltip="true"
      />

      <el-table-column
        align="center"
        prop="ruleConfigName"
        :label="$t('bidMod.evalRuleName')"
        width="150"
        :show-overflow-tooltip="true"
      />

      <el-table-column
        align="center"
        prop="sourcingWay"
        :label="$t('bidMod.sourceType')"
        width="120"
        :show-overflow-tooltip="true"
      >
        <template slot-scope="scope">
          <span>{{ $getDictLabel('SCORE_RULE_WAY', scope.row.sourcingWay) }}</span>
        </template>
      </el-table-column>

      <el-table-column
        align="center"
        prop="totalScore"
        :label="$t('bidMod.totalScore')"
        width="100"
        :show-overflow-tooltip="true"
      />

      <el-table-column
        align="center"
        prop="enableFlag"
        :label="$t('bidMod.status')"
        width="100"
        :show-overflow-tooltip="true"
      >
        <template slot-scope="scope">
          <span>{{ $getDictLabel('SCORE_RULE_STATUS', scope.row.enableFlag) }}</span>
        </template>
      </el-table-column>

      <el-table-column
        align="center"
        prop="lastUpdateDate"
        :label="$t('bidMod.lastUpdateDate')"
        width="150"
        :show-overflow-tooltip="true"
      />

      <el-table-column
        align="center"
        prop="creationDate"
        :label="$t('bidMod.creationDate')"
        width="150"
        :show-overflow-tooltip="true"
      />
    </el-table>

    <div
      slot="footer"
      class="dialog-footer"
    >
      <el-button @click="dialogVisible = false">
        {{ $t("common.cancel") }}
      </el-button>
      <el-button
        type="primary"
        @click="save(null)"
      >
        {{ $t("common.submit") }}
      </el-button>
    </div>
  </srm-dialog>
</template>

<script>
/**
 * 评分规则列表
 */
export default {
  name: 'ScoreRuleListDialog',
  props: {
    visible: {
      type: Boolean
    },
    bargainId: {
      type: [Number, String]
    },
    // 投标基础信息
    bargainBase: {
      type: Object,
      default () {
        return {}
      }
    }
  },
  data () {
    return {
      displayScoreItem: [],
      selectRow: null
    }
  },
  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    }
  },
  watch: {
    dialogVisible: {
      handler (newVal) {
        if (newVal) {
          this.getRuleConfig()
        }
      },
      immediate: true
    }
  },
  methods: {
    /* 查询评分规则列表 */
    getRuleConfig () {
      this.$api.brg.inquiryByProject.ruleConfigList({
          pageNum: 1,
          pageSize: 99999,
          evaluateMethod: this.bargainBase.evaluateMethod,
          sourcingWay: 'BARGAIN',
          enableFlag: 'VALID'
        }).then(data => {
        if (((data || {}).data || {}).list) {
          this.displayScoreItem = data.data.list || []
        }
      })
    },

    /* 选择一条评分规则 */
    selectChange (val) {
      this.selectRow = val
    },

    /* 保存 */
    save (row) {
      const rowData = row || this.selectRow
      if (!rowData) {
        // 需要单击选择
        this.$message.warning(this.$t('common.pleaseSelectOne')) // 请选择一条数据
        return
      }
      let emitData = {
        ruleConfigName: rowData.ruleConfigName,
        totalScore: rowData.totalScore
      }

      // 查询细则列表
      this.$api.brg.inquiryByProject.ruleLineConfigList({ ruleConfigId: rowData.ruleConfigId }).then(data => {
        if (data && data.data) {
          emitData = {
            ...emitData,
            ruleItems: (data.data.list || []).map(item => {
              return {
                ...item,
                bargainId: this.bargainId
              }
            })
          }
          this.$emit('saveScoreRuleList', emitData)
          this.dialogVisible = false
        }
      })
    }
  }
}
</script>
