<template>
  <div class="inquiry-detail-score-rule">
    <el-form
      ref="form"
      :model="headerData"
      label-width="120px"
      label-position="top"
      class="form-incontainer"
      :rules="rules"
      :disabled="readonly"
    >
      <SrmRow>
        <SrmCol :init-col="3">
          <!--评分规则-->
          <el-form-item :label="$t('bidMod.inquiryRule')" prop="scoreRuleType">
            <DictSelect
              v-model="headerData.scoreRuleType"
              clearable
              code="SOU_SCORE_RULE_TYPE"
              :filterItem="['COMPOSITE_PRICE']"
              @change="scoreRuleTypeChange"
            />
          </el-form-item>
        </SrmCol>

        <SrmCol v-if="isCompositePrice" :init-col="3">
          <!--评分细则-->
          <el-form-item :label="$t('bidMod.evalRuleList')">
            <el-select
              v-model="headerData.scoreTemplateId"
              :disabled="headerData.scoreRuleType !== SOU_SCORE_RULE_TYPE_ENUM.COMPOSITE_PRICE"
              clearable
              @change="getInquiryRule"
            >
              <el-option
                v-for="item in ruleList"
                :key="item.scoreTemplateId"
                :label="item.scoreRuleName"
                :value="item.scoreTemplateId"
              />
            </el-select>
          </el-form-item>
        </SrmCol>
        <SrmCol />
      </SrmRow>
    </el-form>

    <el-table
      v-if="isCompositePrice"
      :data="ruleItemsData"
      style="width: 100%"
      border
      height="385px"
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
        :formatter="(_row, _column, cellValue) => $getDictLabel('SOU_SCORE_RULE_DIMENSION', cellValue)"
      />

      <!--评分项-->
      <el-table-column
        align="center"
        prop="scoreItem"
        :label="$t('bidMod.scoreItem')"
        min-width="150"
      />

      <!--评分规则-->
      <el-table-column
        align="center"
        prop="scoreStandard"
        :label="$t('bidMod.scoreRule')"
        min-width="200"
      />

      <!--取值来源-->
      <el-table-column
        align="center"
        prop="scoreSource"
        :label="$t('bidMod.scoreSource')"
        width="150"
        :formatter="(_row, _column, cellValue) => $getDictLabel('SOU_SOURCE_RULE_SOURCE', cellValue)"
      />

      <!--权重（%）-->
      <el-table-column
        align="center"
        prop="scoreWeight"
        :label="$t('bidMod.scoreWeight')"
        width="100"
      />

      <el-table-column
        align="center"
        prop="totalScore"
        :label="$t('bidMod.fullScore')"
        width="100"
      />
    </el-table>
  </div>
</template>

<script>
/**
 * 评分规则
 */
import { scoreRuleHttp } from '@/modules/buyer/inquiry/api'
import { SOU_SCORE_RULE_TYPE_ENUM, SOU_TYPE_ENUM } from '@/library/composition/origin/enum'

export default {
  name: 'InquiryDetailScoreRule',

  props: {
    header: {
      type: Object,
      required: true
    },
    readonly: {
      type: Boolean,
      required: true
    },
    isCurrentActiveTab: {
      type: Boolean,
      required: true
    }
  },

  data () {
    return {
      ruleList: [],
      ruleItemsData: [],
      rules: { scoreRuleType: [{ required: true, message: this.$t('bidMod.selectGradingRules') }] },
      SOU_SCORE_RULE_TYPE_ENUM
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

    // 是否是综合评分
    isCompositePrice () {
      return this.headerData.scoreRuleType === SOU_SCORE_RULE_TYPE_ENUM.COMPOSITE_PRICE
    }
  },

  watch: {
    isCurrentActiveTab: {
      handler (newValue, oldValue) {
        // 切换到当前标签页
        if (newValue && !oldValue) {
          this.getInqValidScoreRule()
        }
      },
      immediate: true
    },
    isCompositePrice (newVal, oldVal) {
      if (newVal && !oldVal && this.ruleItemsData.length != 0) {
        // 综合评分法，并且评分细则为空
        this.getInquiryRule()
      }
    }
  },

  methods: {
    /* 查询简易询价类型的 生效状态的 评分规则 */
    async getInqValidScoreRule () {
      const response = await scoreRuleHttp.listPage({
        pageNum: 1,
        pageSize: 999,
        souType: SOU_TYPE_ENUM.INQ,
        status: 'VALID'
      })
      if (response) {
        this.ruleList = response.data.list
      }
    },

    /* 查询细则列表 */
    async getInquiryRule () {
      if (!this.headerData.scoreTemplateId) return
      const response = await scoreRuleHttp.getDetail(this.headerData.scoreTemplateId)

      if (response && response.data) {
        this.ruleItemsData = (response.data.ruleLineList || []).concat()
      }
    },

    /* 选择评分规则 */
    scoreRuleTypeChange (val) {
      if (val !== SOU_SCORE_RULE_TYPE_ENUM.COMPOSITE_PRICE) {
        // 不是综合评分法
        this.headerData.scoreTemplateId = ''
        this.ruleItemsData = []
      }
    },

    /* 校验 */
    validateForm () {
      return new Promise(resolve => {
        if (!this.headerData.scoreRuleType) {
          this.$message.warning(this.$t('bidMod.selectGradingRules'))
          resolve(false)
          return
        }
        if (this.headerData.scoreRuleType === SOU_SCORE_RULE_TYPE_ENUM.COMPOSITE_PRICE && !this.headerData.scoreTemplateId) {
          this.$message.warning(this.$t('bidMod.selectGradingRules'))
          resolve(false)
          return
        }
        resolve(true)
      })
    }
  }
}
</script>
