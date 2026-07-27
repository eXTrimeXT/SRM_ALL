<template>
  <SrmDialog
    :title="$t('bidMod.addEditNewScoreRule')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <el-form
      ref="form"
      :model="detailFormData"
      label-width="100px"
      label-position="top"
      :rules="rules"
      :disabled="pageFlag.isView"
    >
      <SrmRow>
        <!--f 评分规则名称-->
        <SrmCol>
          <el-form-item :label="$t('bidMod.evalRuleName')" prop="scoreRuleName">
            <el-input
              v-model="detailFormData.scoreRuleName"
              maxlength="150"
              show-word-limit
            />
          </el-form-item>
        </SrmCol>

        <!--f 寻源方式-->
        <SrmCol>
          <el-form-item :label="$t('bidMod.sourceType')" prop="souType">
            <DictSelect
              v-model="detailFormData.souType"
              code="SOU_TYPE"
              :filter-item="[SOU_TYPE_ENUM.INQ, SOU_TYPE_ENUM.COMP]"
              @change="souTypeChange"
            />
          </el-form-item>
        </SrmCol>

        <!--f 总分值-->
        <SrmCol>
          <el-form-item :label="$t('bidMod.totalScore')" prop="totalScore">
            <el-input
              v-model="detailFormData.totalScore"
              v-input-format="{ type: 'number' }"
              disabled
            />
          </el-form-item>
        </SrmCol>

        <!--f 评选方法-->
        <SrmCol>
          <el-form-item :label="$t('bid_mod.evaluateMethod')" prop="scoreRuleType">
            <DictSelect
              v-model="detailFormData.scoreRuleType"
              code="SOU_SCORE_RULE_TYPE"
              disabled
            />
          </el-form-item>
        </SrmCol>
      </SrmRow>
    </el-form>

    <el-button
      v-if="!pageFlag.isView"
      type="primary"
      style="margin-bottom: 15px"
      :disabled="ruleLineList.length === 3 || detailFormData.souType === 'INQ'"
      @click="addRow"
    >
      {{ $t("bidMod.addScoreRuleItem") }}
    </el-button>

    <el-table
      :data="ruleLineList"
      style="width: 100%"
      border
    >
      <el-table-column
        align="center"
        type="index"
        width="50"
      />

      <!--维度-->
      <el-table-column
        align="center"
        prop="dimension"
        :label="$t('bidMod.dimension')"
        width="150"
        show-overflow-tooltip
      >
        <template v-slot="scope">
          <DictSelect
            v-model="scope.row.dimension"
            code="SOU_SCORE_RULE_DIMENSION"
            :disabled="pageFlag.isView || scope.row.isDefault || scope.row.dimension === 'PRICE'"
            :filter-item="[SOU_SCORE_RULE_DIMENSION_ENUM.ACHIEVEMENT, SOU_SCORE_RULE_DIMENSION_ENUM.COMPOSITE]"
            @change="val => dimensionChange(val, scope)"
          />
        </template>
      </el-table-column>

      <!--评分项-->
      <el-table-column
        align="center"
        prop="scoreItem"
        :label="$t('bidMod.scoreItem')"
        width="150"
        show-overflow-tooltip
      >
        <template v-slot="scope">
          <el-input
            v-model="scope.row.scoreItem"
            :disabled="pageFlag.isView"
            maxlength="120"
          />
        </template>
      </el-table-column>

      <!--评分标准-->
      <el-table-column
        align="center"
        prop="scoreStandard"
        :label="$t('bidMod.scoreStandard')"
        width="150"
        show-overflow-tooltip
      >
        <template v-slot="scope">
          <el-input
            v-model="scope.row.scoreStandard"
            :disabled="pageFlag.isView"
            maxlength="240"
          />
        </template>
      </el-table-column>

      <!--t 取值来源-->
      <el-table-column
        align="center"
        prop="scoreSource"
        :label="$t('bidMod.scoreSource')"
        width="150"
        show-overflow-tooltip
      >
        <template v-slot="scope">
          <DictSelect
            v-model="scope.row.scoreSource"
            code="SOU_SOURCE_RULE_SOURCE"
            :disabled="pageFlag.isView || ['PRICE', 'TECHNOLOGY'].includes(scope.row.dimension) || scope.row.isDefault"
          />
        </template>
      </el-table-column>

      <!--t 权重-->
      <el-table-column
        align="center"
        prop="scoreWeight"
        :label="$t('bidMod.scoreWeight')"
        width="100"
        show-overflow-tooltip
      >
        <template v-slot="scope">
          <el-input
            v-model="scope.row.scoreWeight"
            v-input-format="{ type: 'number', negative: false, zero: false }"
            max="100"
            :disabled="pageFlag.isView"
          />
        </template>
      </el-table-column>

      <!--t 满分值-->
      <el-table-column
        align="center"
        prop="totalScore"
        :label="$t('bidMod.totalScore')"
        min-width="100"
        show-overflow-tooltip
      >
        <template v-slot="scope">
          <el-input
            v-model.number="scope.row.totalScore"
            type="number"
            disabled
          />
        </template>
      </el-table-column>

      <el-table-column
        :label="$t('bidMod.operation')"
        width="60"
      >
        <template v-slot="scope">
          <el-button
            :disabled="pageFlag.isView || scope.row.isDefault || scope.row.dimension === 'PRICE'"
            type="text"
            @click="deleteRow(scope.$index, scope.row)"
          >
            {{ $t("common.delete") }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div
      slot="footer"
      class="dialog-footer"
    >
      <el-button @click="dialogVisible = false">
        {{ $t("common.cancel") }}
      </el-button>

      <!--保存-->
      <el-button
        v-if="!pageFlag.isView"
        type="primary"
        @click="saveOrSubmit('save')"
      >
        {{ $t("common.save") }}
      </el-button>

      <!--提交-->
      <el-button
        v-if="!pageFlag.isView"
        type="primary"
        @click="saveOrSubmit('submit')"
      >
        {{ $t("common.submit") }}
      </el-button>
    </div>
  </SrmDialog>
</template>

<script>

/**
 * 详情弹窗
 */
import { scoreRuleHttp } from 'modb@/souConfiguration/api'
import { SOU_TYPE_ENUM, SOU_SCORE_RULE_DIMENSION_ENUM } from 'lib@/composition/origin/enum'

export default {
  name: 'DetailDialog',

  props: {
    visible: {
      type: Boolean,
      default: false
    },
    editRow: {
      type: Object,
      default: () => { /* nothing */ }
    },
    flag: {
      type: String,
      default: 'add'
    }
  },

  data () {
    return {
      detailFormData: {
        scoreRuleId: '',
        scoreRuleName: '',
        souType: '',
        enableFlag: '',
        totalScore: '',
        scoreRuleType: ''
      },
      ruleLineList: [],
      rules: {
        // 请选择评分规则名称
        scoreRuleName: [{ required: true, message: this.$t('bidMod.msgEvalRuleName') }],
        // 请选择寻源方式
        type: [{ required: true, message: this.$t('bidMod.msgSourceType') }],
        // 请输入总分
        totalScore: [{ required: true, message: this.$t('bidMod.msgTotalScore') }],
        // 请选择状态
        status: [{ required: true, message: this.$t('bidMod.msgSelStatus') }]
      },
      defaultScoreRuleLineConfig: {
        isDefault: true,
        dimension: 'PRICE',
        scoreItem: '',
        scoreStandard: '',
        scoreSource: 'SYS_VALUE',
        scoreWeight: '',
        totalScore: '100'
      },
      SOU_TYPE_ENUM,
      SOU_SCORE_RULE_DIMENSION_ENUM
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
    },
    // 简易询价只能选绩效
    dimensionDisabledStatus () {
      return this.detailFormData.souType === 'INQ' ? ['TECHNOLOGY', 'PRICE'] : ['PRICE']
    },
    pageFlag () {
      // 新增、编辑、只读
      // flag: ['add', 'edit', 'view']
      return {
        isAdd: this.flag === 'add',
        isEdit: this.flag === 'edit',
        isView: this.flag === 'view'
      }
    }
  },

  mounted () {
    if (!this.pageFlag.isAdd && this.editRow && this.editRow.scoreRuleId) {
      this.getDetailData()
    }

    if (this.pageFlag.isAdd) {
      this.detailFormData = {
        scoreRuleId: '',
        scoreRuleName: '',
        souType: '',
        totalScore: '100',
        scoreRuleType: 'COMPOSITE_PRICE'
      }
      this.ruleLineList = [
        { ...this.defaultScoreRuleLineConfig }
      ]
    }
  },

  methods: {
    /* 查询详情 */
    async getDetailData () {
      const response = await scoreRuleHttp.getDetail(this.editRow.scoreRuleId)
      if (response) {
        let { ruleLineList, ...params } = response.data
        const obj = {}
        for (let key in this.detailFormData) {
          if (params[key] || params[key] === 0) {
            obj[key] = params[key]
          }
        }
        this.detailFormData = obj
        this.ruleLineList = ruleLineList.map(item => ({ ...item, isDefault: params.souType === 'INQ' }))
      }
    },

    /* 可选维度过滤 */
    transformOptions (options) {
      return options.map(item => {
        return {
          ...item,
          disabled: this.dimensionDisabledStatus.includes(item.value)
        }
      })
    },

    /* 寻源方式变更 */
    souTypeChange (val) {
      if (val === 'INQ') {
        // 简易询价 写死只能两条，价格 + 绩效
        this.ruleLineList = [
          { ...this.defaultScoreRuleLineConfig },
          {
            isDefault: true,
            dimension: 'ACHIEVEMENT',
            scoreItem: '',
            scoreStandard: '',
            scoreSource: 'SYS_VALUE',
            scoreWeight: '',
            totalScore: '100'
          }
        ]
      } else {
        this.ruleLineList = [
          { ...this.defaultScoreRuleLineConfig }
        ]
      }
    },

    /* 新增行 */
    addRow () {
      this.ruleLineList.push({
        scoreRuleId: '',
        dimension: '',
        scoreItem: '',
        scoreStandard: '',
        scoreSource: '',
        scoreWeight: '',
        totalScore: '100'
      })
    },

    /* 维度 */
    dimensionChange (val, scope) {
      scope.row.scoreItem = ''
      scope.row.scoreStandard = ''
      if (val === 'PRICE') {
        // 价格
        scope.row.scoreSource = 'SYS_VALUE'
        // SYS_VALUE  MANUAL_VALUE
      } else if (val === 'TECHNOLOGY') {
        // 技术
        scope.row.scoreSource = 'MANUAL_VALUE'
      }
    },

    /* 行删除 */
    deleteRow (index) {
      this.ruleLineList.splice(index, 1)
    },

    /* 保存 */
    saveOrSubmit (type) {
      this.$refs.form.validate(async valid => {
        if (valid) {
          for (const item of this.ruleLineList) {
            if (!item.scoreWeight) {
              // 请输入权重！
              this.$message.warning(this.$t('bidMod.msgScoreWeight'))
              return
            }
          }

          const scoreWeightList = this.ruleLineList.map(v => v.scoreWeight)
          const scoreWeightTotal = scoreWeightList.reduce((p, n) => Number(p) + Number(n))

          if (scoreWeightTotal !== 100) {
            // 多个评分项权重累加应为100%
            this.$message.warning(this.$t('bidMod.msgWeightAdd100'))
            return
          }

          const dimensionList = this.ruleLineList.map(item => item.dimension)
          if ([...new Set(dimensionList)].length !== dimensionList.length) {
            // 每个维度只能选一次！
            this.$message.warning(this.$t('bidMod.msgScoreDimensionOnly'))
            return
          }

          const response = await scoreRuleHttp[type]({
            ...this.detailFormData,
            ruleLineList: this.ruleLineList
          })
          if (response) {
            // 删除成功
            this.$message.success(this.$t('common.successSave'))
            this.$emit('save-success')
            this.dialogVisible = false
          }
        }
      })
    }
  }
}
</script>
