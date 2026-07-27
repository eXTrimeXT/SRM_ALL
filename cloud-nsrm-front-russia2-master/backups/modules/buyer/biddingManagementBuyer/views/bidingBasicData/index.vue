<template>
  <el-container
    class="flex-container-notab the_inquiryBasicData_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
      />

      <main-header>
        <template slot="left">
          <el-button
            code="bid:bidingBasicData:createNewScoreRule"
            type="primary"

            @click="editTab"
          >
            {{ $t("common.add") }}
          </el-button>
        </template>
      </main-header>

      <table-view
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        url="/api-bid/evaluation/ruleConfig/listPage"
      />

      <!-- 弹框区域-->
      <srm-dialog
        top="2vh"
        :title="$t('bidMod.addEditNewScoreRule')"
        size="large"
        :visible.sync="dialogFormVisible"
        :close-on-click-modal="false"
      >
        <el-form
          ref="form"
          :model="allParams.scoreRuleConfig"
          label-width="80px"
          label-position="top"
          class="form-incontainer"
          :rules="rules"
          :disabled="isDialogFormView"
        >
          <srm-row>
            <srm-col>
              <!--f 评分规则名称-->
              <el-form-item
                :label="$t('bidMod.evalRuleName')"
                :label-width="formLabelWidth"
                prop="ruleConfigName"
              >
                <el-input
                  v-model="allParams.scoreRuleConfig.ruleConfigName"
                  maxlength="150"
                  show-word-limit
                />
              </el-form-item>
            </srm-col>

            <srm-col>
              <!--f 寻源方式-->
              <el-form-item
                :label="$t('bidMod.sourceType')"
                :label-width="formLabelWidth"
                prop="sourcingWay"
              >
                <dict-select
                  v-model="allParams.scoreRuleConfig.sourcingWay"
                  code="SCORE_RULE_WAY"
                  @change="sourcingWayChange"
                />
              </el-form-item>
            </srm-col>

            <srm-col>
              <!--f 总分值-->
              <el-form-item
                :label="$t('bidMod.totalScore')"
                :label-width="formLabelWidth"
                prop="totalScore"
              >
                <el-input
                  v-model="allParams.scoreRuleConfig.totalScore"
                  v-input-format="{ type: 'number' }"
                  disabled
                />
              </el-form-item>
            </srm-col>

            <srm-col>
              <!--f 评选方法-->
              <el-form-item
                :label="$t('bid_mod.evaluateMethod')"
                :label-width="formLabelWidth"
                prop="evaluateMethod"
              >
                <dict-select
                  v-model="allParams.scoreRuleConfig.evaluateMethod"
                  code="BIDDING_GRADING"
                  disabled
                />
              </el-form-item>
            </srm-col>
          </srm-row>
        </el-form>

        <el-button
          v-if="!isDialogFormView"
          type="primary"
          style="margin-bottom: 15px"
          :disabled="allParams.scoreRuleLineConfigList.length === 3 || allParams.scoreRuleConfig.sourcingWay === 'INQUIRY'"
          @click="addOne"
        >
          {{ $t("bidMod.addScoreRuleItem") }}
        </el-button>

        <el-table
          :data="allParams.scoreRuleLineConfigList"
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
            prop="scoreDimension"
            :label="$t('bidMod.dimension')"
            width="150"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              <dict-select
                v-model="scope.row.scoreDimension"
                code="BIDDING_SCOR_DIM"
                :disabled="isDialogFormView || scope.row.isDefault || scope.row.scoreDimension === 'PRICE'"
                :transform-options="transformOptions"
                @change="val => scoreDimensionChange(val, scope)"
              />
            </template>
          </el-table-column>

          <!-- TECHNOLOGY  PRICE -->
          <el-table-column
            align="center"
            prop="scoreItem"
            :label="$t('bidMod.scoreItem')"
            width="150"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.scoreItem"
                :disabled="isDialogFormView || scope.row.scoreDimension === 'PRICE'"
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
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.scoreStandard"
                :disabled="isDialogFormView || scope.row.scoreDimension === 'PRICE'"
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
            <template slot-scope="scope">
              <dict-select
                v-model="scope.row.scoreSource"
                code="SOURCE_VALUE"
                :disabled="isDialogFormView || ['PRICE', 'TECHNOLOGY'].includes(scope.row.scoreDimension) || scope.row.isDefault"
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
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.scoreWeight"
                v-input-format="{ type: 'number', negative: false, zero: false }"
                max="100"
                :disabled="isDialogFormView"
              />
            </template>
          </el-table-column>

          <!--t 满分值-->
          <el-table-column
            align="center"
            prop="fullScore"
            :label="$t('bidMod.fullScore')"
            min-width="100"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              <el-input
                v-model.number="scope.row.fullScore"
                type="number"
                disabled
              />
            </template>
          </el-table-column>

          <el-table-column
            :label="$t('bidMod.operation')"
            width="60"
          >
            <template slot-scope="scope">
              <el-button
                :disabled="isDialogFormView || scope.row.isDefault || scope.row.scoreDimension === 'PRICE'"
                type="text"
                @click="handleDelClick(scope.$index, scope.row)"
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
          <el-button @click="dialogFormVisible = false">
            {{ $t("common.cancel") }}
          </el-button>
          <el-button
            v-if="!isDialogFormView"
            type="primary"
            @click="addNew"
          >
            {{ $t("common.confirm") }}
          </el-button>
        </div>
      </srm-dialog>
    </el-main>
  </el-container>
</template>

<script>
import { parseTime } from '@/utils'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'

export default {
  name: 'BidingBasicData',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  data () {
    return {
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      currentRow: null,
      tableHeader: [],
      tableData: [],
      queryParam: {},
      allParams: {
        scoreRuleConfig: {
          ruleConfigId: '',
          scoreRuleModelNo: '',
          ruleConfigName: '',
          sourcingWay: '',
          enableFlag: '',
          scoreRule: '',
          totalScore: '',
          evaluateMethod: ''
        },
        scoreRuleLineConfigList: []
      },
      rules: {
        // 请选择评分规则名称
        ruleConfigName: [{ required: true, message: this.$t('bidMod.msgEvalRuleName') }],
        // 请选择寻源方式
        type: [{ required: true, message: this.$t('bidMod.msgSourceType') }],
        // 请输入总分
        totalScore: [{ required: true, message: this.$t('bidMod.msgTotalScore') }],
        // 请选择状态
        status: [{ required: true, message: this.$t('bidMod.msgSelStatus') }]
      },
      inquiryTypeList: [],
      dialogFormVisible: false,
      formLabelWidth: '100px',
      isActive: false,
      preArr: [
        { prop: 'ruleConfigCode', label: this.$t('bidMod.scoreRuleModelNo') },
        { prop: 'ruleConfigName', label: this.$t('bidMod.evalRuleName') },
        {
          prop: 'enableFlag',
          label: this.$t('bidMod.status'),
          type: 'dict',
          code: 'SCORE_RULE_STATUS'
        }
      ],
      ruleList: [],
      defaultScoreRuleLineConfig: {
        isDefault: true,
        scoreDimension: 'PRICE',
        scoreItem: '',
        scoreStandard: '',
        scoreSource: 'SYS_VALUE',
        scoreWeight: '',
        fullScore: '100'
      },
      isDialogFormView: false
    }
  },
  computed: {
    // 简易询价只能选绩效
    scoreDimensionDisabledStatus () {
      return this.allParams.scoreRuleConfig.sourcingWay === 'INQUIRY' ? ['TECHNOLOGY', 'PRICE'] : ['PRICE']
    }
  },
  created () {
    this.tableHeader = [
      // 评分规则编码
      {
        prop: 'ruleConfigCode',
        label: this.$t('bidMod.scoreRuleModelNo'),
        minWidth: 150
      },
      // 评分规则名称
      {
        prop: 'ruleConfigName',
        label: this.$t('bidMod.evalRuleName'),
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.getDetailById(row, true)
      },
      {
        prop: 'sourcingWay',
        label: this.$t('bidMod.sourceType'),
        minWidth: 150,
        formattor: val => this.$getDictLabel('SCORE_RULE_WAY', val)
      },
      {
        prop: 'totalScore',
        label: this.$t('bidMod.totalScore'),
        width: 100
      },
      {
        prop: 'enableFlag',
        label: this.$t('bidMod.status'),
        width: 100,
        formattor: val => this.$getDictLabel('SCORE_RULE_STATUS', val)
      },
      {
        prop: 'lastUpdateDate',
        label: this.$t('bidMod.lastUpdateDate'),
        width: 100,
        formattor: val => val ? parseTime(val, '{y}-{m}-{d}') : ''
      },
      {
        prop: 'creationDate',
        label: this.$t('bidMod.creationDate'),
        width: 100,
        formattor: val => val ? parseTime(val, '{y}-{m}-{d}') : ''
      },
      {
        prop: 'operation',
        label: this.$t('bidMod.operation'),
        width: 150,
        btnStyle: 'text',
        fixed: 'right',
        showType: 'buttons',
        buttons: [
          // b 编辑
          {
            // 拟定
            show: row => ['DRAFT'].includes(row.enableFlag),
            callback: row => this.getDetailById(row),
            formattor: () => this.$t('common.edit')
          },
          // 删除
          {
            // 拟定
            show: row => ['DRAFT'].includes(row.enableFlag),
            callback: row => this.deleteOneItem(row),
            formattor: () => this.$t('common.delete')
          },
          // 生效
          {
            callback: row => this.activeOrInactiveItem('active', row),
            formattor: () => this.$t('common.active'),
            // [拟定, 失效]
            show: row => ['DRAFT', 'INVALID'].includes(row.enableFlag)
          },
          // 失效
          {
            callback: row => this.activeOrInactiveItem('inactive', row),
            formattor: () => this.$t('common.inactive'),
            // 生效
            show: row => ['VALID'].includes(row.enableFlag)
          }
        ]
      }
    ]

    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    transformOptions (options) {
      return options.map(item => {
        return {
          ...item,
          disabled: this.scoreDimensionDisabledStatus.includes(item.value)
        }
      })
    },

    scoreDimensionChange (val, scope) {
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

    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },

    deleteOneItem (row) {
      this.$confirm(this.$t('common.delRow'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/api-bid/evaluation/ruleConfig/delete',
          method: 'GET',
          params: { ruleConfigId: row.ruleConfigId },
          loading: true
        }).then(data => {
          this.$message({
            message: this.$t('common.successDelete'), // 删除成功
            type: 'success'
          })
          this.getQuerydata()
        })
      })
    },

    // 编辑tab
    editTab (type) {
      this.allParams = {
        scoreRuleConfig: {
          ruleConfigId: '',
          scoreRuleModelNo: '',
          ruleConfigName: '',
          sourcingWay: '',
          enableFlag: 'A',
          scoreRule: '',
          totalScore: '100',
          evaluateMethod: 'COMPOSITE_SCORE'
        },
        scoreRuleLineConfigList: [
          { ...this.defaultScoreRuleLineConfig }
        ]
      }
      this.isDialogFormView = false
      this.dialogFormVisible = true
    },

    /* 查询详情 */
    getDetailById (row, isView = false) {
      this.$http({
        url: `/api-bid/evaluation/ruleConfig/getScoreRuleConfig/${row.ruleConfigId}`,
        method: 'get',
        loading: true
      }).then(data => {
        if (data.data) {
          let list = data.data.scoreRuleLineConfigList
          const config = data.data.scoreRuleConfig
          if (config.sourcingWay === 'INQUIRY') {
            // 简易询价
            list = list.map(item => {
              return {
                ...item,
                isDefault: true
              }
            })
          }
          this.allParams = {
            scoreRuleConfig: config,
            scoreRuleLineConfigList: list
          }
          // 是否查看
          this.isDialogFormView = isView
          this.dialogFormVisible = true
        }
      })
    },

    addOne () {
      this.allParams.scoreRuleLineConfigList.push({
        ruleConfigId: '',
        scoreDimension: '',
        scoreItem: '',
        scoreStandard: '',
        scoreSource: '',
        scoreWeight: '',
        fullScore: '100'
      })
    },

    // 行删除
    handleDelClick (index, row) {
      this.allParams.scoreRuleLineConfigList.splice(index, 1)
    },

    handleCurrentChange (val) {
      this.currentRow = val
    },

    addNew () {
      this.$refs.form.validate(valid => {
        if (valid) {
          for (const i of this.allParams.scoreRuleLineConfigList) {
            if (!i.scoreWeight) {
              // 请输入权重！
              this.$message.warning(this.$t('bidMod.msgScoreWeight'))
              return
            }
          }
          const scoreWeightList = this.allParams.scoreRuleLineConfigList.map(v => v.scoreWeight)
          const scoreWeightTotal = scoreWeightList.reduce((p, n) => Number(p) + Number(n))
          if (scoreWeightTotal !== 100) {
            // 多个评分项权重累加应为100%
            this.$message.warning(this.$t('bidMod.msgWeightAdd100'))
            return
          }
          const scoreDimensionList = this.allParams.scoreRuleLineConfigList.map(item => item.scoreDimension)
          if ([...new Set(scoreDimensionList)].length !== scoreDimensionList.length) {
            // 每个维度只能选一次！
            this.$message.warning(this.$t('bidMod.msgScoreDimensionOnly'))
            return
          }
          this.$http({
            url: '/api-bid/evaluation/ruleConfig/edit',
            method: 'POST',
            data: this.allParams,
            loading: true
          }).then(() => {
            this.dialogFormVisible = false
            this.$message({
              message: this.$t('common.successSave'), // 保存成功
              type: 'success'
            })
            this.getQuerydata()
          })
        }
      })
    },

    /* 寻源方式变更 */
    sourcingWayChange (val) {
      if (val === 'INQUIRY') {
        // 简易询价 写死只能两条，价格 + 绩效
        this.allParams.scoreRuleLineConfigList = [
          { ...this.defaultScoreRuleLineConfig },
          {
            isDefault: true,
            scoreDimension: 'ACHIEVEMENT',
            scoreItem: '',
            scoreStandard: '',
            scoreSource: 'SYS_VALUE',
            scoreWeight: '',
            fullScore: '100'
          }
        ]
      } else {
        this.allParams.scoreRuleLineConfigList = [
          { ...this.defaultScoreRuleLineConfig }
        ]
      }
    },
    /* 生效 / 失效 */
    activeOrInactiveItem (type, row) {
      const url = `${type === 'active' ? 'validScoreRule' : 'invalidScoreRule'}/${row.ruleConfigId}`
      this.$http({
        url: `/api-bid/evaluation/ruleConfig/${url}`,
        method: 'POST',
        loading: false
      }).then(() => {
        this.$message({
          message: this.$t('common.successSave'), // 保存成功
          type: 'success'
        })
        this.getQuerydata()
      })
    }
  }
}
</script>
