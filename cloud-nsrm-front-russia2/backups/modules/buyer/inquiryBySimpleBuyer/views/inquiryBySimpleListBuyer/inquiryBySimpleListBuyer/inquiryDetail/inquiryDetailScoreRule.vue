<template>
  <div class="inquiry-detail-score-rule">
    <el-form
      ref="form"
      :model="headerData"
      label-width="120px"
      label-position="top"
      class="form-incontainer"
      :rules="rules"
      :disabled="readOnly"
    >
      <srm-row>
        <srm-col :init-col="3">
          <!--评分规则-->
          <el-form-item :label="$t('bidMod.inquiryRule')" prop="inquiryRule">
            <dict-select
              v-model="headerData.inquiryRule"
              clearable
              code="RFQ_SCORE_RULE"
              @change="inquiryRuleChange"
            />
          </el-form-item>
        </srm-col>

        <srm-col :init-col="3">
          <!--评分细则-->
          <el-form-item :label="$t('bidMod.evalRuleList')">
            <el-select
              v-model="scoreRuleNameData"
              :disabled="scoreRuleNameDataDisabled"
              clearable
              @change="getInquiryRule"
            >
              <el-option
                v-for="item in ruleList"
                :key="item.ruleConfigId"
                :label="item.ruleConfigName"
                :value="item.ruleConfigName"
              />
            </el-select>
          </el-form-item>
        </srm-col>
        <srm-col />
      </srm-row>
    </el-form>

    <el-table :data="ruleItemsData" style="width: 100%" border height="385px">
      <el-table-column align="center" type="index" width="50" />
      <el-table-column align="center" prop="dimension" :label="$t('bidMod.dimension')" width="150">
        <template slot-scope="scope">
          <span>{{ $getDictLabel('BIDDING_SCOR_DIM', scope.row.scoreDimension) }}</span>
        </template>
      </el-table-column>

      <el-table-column
        align="center"
        prop="scoreItem"
        :label="$t('bidMod.scoreItem')"
        min-width="150"
      />
      <el-table-column
        align="center"
        prop="scoreRule"
        :label="$t('bidMod.scoreRule')"
        min-width="200"
      />
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
  </div>
</template>

<script>
/**
 * 评分规则
 */
import { getAllRule } from '@/api/common'

export default {
  name: 'InquiryDetailScoreRule',
  props: {
    header: Object,
    ruleItems: {
      type: Array,
      required: true
    },
    scoreRuleName: [String, Object, Number],
    readOnly: {
      type: Boolean,
      required: true
    },
    isCurrentActiveTab: Boolean
  },
  data () {
    return {
      displayScoreItem: [],
      ruleList: [],
      dialogFormVisible: false,
      ruleObj: {},
      currentRow: null,
      rules: { inquiryRule: [{ required: true, message: '请选择评分规则' }] }
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
    ruleItemsData: {
      get: function () {
        return this.ruleItems
      },
      set: function (val) {
        this.$emit('update:ruleItems', val)
      }
    },
    scoreRuleNameData: {
      get: function () {
        return this.scoreRuleName
      },
      set: function (val) {
        this.$emit('update:scoreRuleName', val)
      }
    },
    scoreRuleNameDataDisabled () {
      return !(this.headerData.inquiryRule === 'COMPREHENSIVE_SCORING_METHOD')
    }
  },
  watch: {
    isCurrentActiveTab: {
      handler (newValue, oldValue) {
        // 切换到当前标签页
        if (newValue && !oldValue) {
          this.initDetailData()
        }
      },
      immediate: true
    }
  },
  created () {
    // 获取评分规则
    const params = {
      pageNum: 1,
      pageSize: 100,
      sourcingWay: 'INQUIRY'
    }
    getAllRule({ ...params }).then(res => {
      this.ruleList = res.data.list || []
    })
  },
  methods: {
    // 字典格式化，返回label
    formatter (code, value) {
      return this.store.getLabel(code, value)
    },
    /* 初始化查询展示类数据 */
    initDetailData () {
      // 如果是综合评分法 && 评分细则有值 && 列表为空
      if (this.headerData.inquiryRule === 'COMPREHENSIVE_SCORING_METHOD' && this.scoreRuleNameData && this.ruleItemsData.length === 0) {
        this.getInquiryRule(this.scoreRuleNameData)
      }
    },
    /* 查询细则列表 */
    getInquiryRule (val) {
      const ruleConfigId = this.ruleList.find(item => item.ruleConfigName === val).ruleConfigId
      this.$api.inq.inquiryBySimple.getInquiryRule({ ruleConfigId }).then(data => {
        if (data && data.data) {
          this.ruleItemsData = (data.data.list || []).map(item => {
            return {
              ...item,
              // 维度
              dimension: item.scoreDimension,
              // 评分规则
              scoreRule: item.scoreStandard
            }
          })
        }
      })
    },
    /* 选择评分规则 */
    inquiryRuleChange (val) {
      if (val !== 'COMPREHENSIVE_SCORING_METHOD') {
        // 不是综合评分法
        this.scoreRuleNameData = ''
        this.ruleItemsData = []
      }
    },
    /* 校验 */
    validateForm () {
      return new Promise(resolve => {
        if (!this.headerData.inquiryRule) {
          this.$message.warning('请选择评分规则!')
          resolve(false)
          return
        }
        if (this.headerData.inquiryRule === 'COMPREHENSIVE_SCORING_METHOD' && !this.scoreRuleNameData) {
          this.$message.warning('请选择评分细则!')
          resolve(false)
          return
        }
        resolve(true)
      })
    }
  }
}
</script>
