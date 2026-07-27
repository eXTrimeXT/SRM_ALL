<template>
  <el-container class="the-indicators-detail" direction="vertical">
    <el-main>
      <el-collapse v-model="activeDims" class="tab-form-style">
        <!-- 指标头信息 -->
        <el-collapse-item :title="$t('perfMod.indicatorHeader')" name="1">
          <el-form
            ref="indicatorsForm"
            :model="indicatorsForm.indicatorsHeader"
            :rules="rules"
            class="form-fill-style"
            :disabled="curOpt === 'view'"
          >
            <srm-row>
<!--              <srm-col :initCol="4">-->
<!--                <el-form-item :label="$t('perfMod.indicatorType')" prop="indicatorType">-->
<!--                  <DictSelect-->
<!--                    v-model="indicatorsForm.indicatorsHeader.indicatorType"-->
<!--                    code="INDICATORS_TYPE"-->
<!--                    :disabled="showDisabled"-->
<!--                    @change="indicatorsTypeChange"-->
<!--                  />-->
<!--                </el-form-item>-->
<!--              </srm-col>-->
              <srm-col :initCol="4">
                <el-form-item :label="$t('perfMod.indicatorDimension')" prop="indicatorDimension">
                  <!-- 指标维度 -->
                  <DictSelect
                    v-model="indicatorsForm.indicatorsHeader.indicatorDimension"
                    code="INDICATORS_DIM"
                    :disabled="showDisabled"
                    @change="indicatorsDimChange"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item :label="$t('perfMod.evaluation')">
                  <el-select
                    v-model="indicatorsForm.indicatorsHeader.evaluation"
                    :disabled="evaluationDis||showDisabled"
                    @change="evaluationDisChange"
                  >
                    <el-option
                      v-for="item in scoreIs"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item :label="$t('perfMod.quoteMode')">
                  <!-- 取值方式 -->
                  <el-select
                    v-model="indicatorsForm.indicatorsHeader.quoteMode"
                    :disabled="conversionTypeDis||showDisabled"
                    @change="conversionTypeChange"
                  >
                    <el-option
                      :label="$t('perfMod.valuesDirectly')"
                      value="DIRECT_QUOTE"
                      :disabled="directQuoteDis"
                    />
                    <el-option
                      :label="$t('perfMod.convertText')"
                      value="TEXT_CONVERSION"
                      :disabled="textQuoteDis"
                    />
                    <el-option :label="$t('perfMod.convertInterval')" value="INTERVAL_CONVERSION" />
                  </el-select>
                </el-form-item>
              </srm-col>
            </srm-row>
            <srm-row>
              <srm-col :initCol="2">
                <!-- 指标名称 -->
                <el-form-item :label="$t('perfMod.indicatorName')" prop="indicatorName">
                  <span slot="label">
                    {{ $t('perfMod.indicatorName') }}
                    <span style="color:red;margin-left: 10px">{{
                      $t('perfMod.deliveryRate')
                    }}</span>
                  </span>
                  <el-input
                    v-model="indicatorsForm.indicatorsHeader.indicatorName"
                    :disabled="showDisabled"
                  />
                </el-form-item>
              </srm-col>
              <srm-col
                v-if="
                  indicatorsForm.indicatorsHeader.evaluation == 'SCORING_SYSTEM_VALUE' ||
                    indicatorsForm.indicatorsHeader.evaluation == 'DEDUCTION_SYSTEM_VALUE' ||
                    indicatorsForm.indicatorsHeader.evaluation == 'EXTRA_SYSTEM_VALUE'
                "
                :initCol="4"
              >
                <el-form-item :label="$t('perfMod.valueName')" prop="valueName">
                  <!-- 取值名称 -->
                  <el-select
                    v-model="indicatorsForm.indicatorsHeader.valueName"
                    :disabled="showDisabled"
                    @change="valueNameChange"
                  >
                    <el-option
                      v-for="(item, index) in valueNameData"
                      :key="index"
                      :label="item.name"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item :label="$t('perfMod.indicatorLineType')">
                  <!-- 评价值类型 -->
                  <el-select
                    v-model="indicatorsForm.indicatorsHeader.indicatorLineType"
                    :disabled="indicatorLineTypeDis || showDisabled"
                    @change="indicatorsLineTypeChange"
                  >
                    <el-option :label="$t('perfMod.text')" value="TEXT" :disabled="textDis" />
                    <el-option :label="$t('perfMod.figure')" value="NUMBER" />
                    <el-option :label="$t('perfMod.percentage')" value="PERCENTAGE" />
                  </el-select>
                </el-form-item>
              </srm-col>
              <srm-col
                v-if="indicatorsForm.indicatorsHeader.quoteMode == 'INTERVAL_CONVERSION'"
                :initCol="4">
                <el-form-item :label="$t('perfMod.startSymbol')">
                  <!-- 评分值起始符号 -->
                  <DictSelect
                    v-model="indicatorsForm.indicatorsHeader.startSymbol"
                    code="INDICATOR_SYMPOL"
                    :disabled="showDisabled"
                    @change="startSymbolChange"
                  />
                </el-form-item>
              </srm-col>
              <srm-col
                v-if="indicatorsForm.indicatorsHeader.quoteMode == 'INTERVAL_CONVERSION'"
                :initCol="4">
                <el-form-item :label="$t('perfMod.endSymbol')">
                  <!-- 评分值结束符号 -->
                  <DictSelect
                    v-model="indicatorsForm.indicatorsHeader.endSymbol"
                    code="INDICATOR_SYMPOL"
                    :disabled="true"
                  />
                </el-form-item>
              </srm-col>
              <srm-col
                v-if="['SCORING_SYSTEM_VALUE','DEDUCTION_SYSTEM_VALUE','EXTRA_SYSTEM_VALUE'].includes(indicatorsForm.indicatorsHeader.evaluation)"
                :initCol="4"
              >
                <el-form-item
                  :label="$t('priceModel.costElement.calculationFormula')"
                  prop="calcFormula"
                >
                  <!-- 计算公式 -->
                  <el-input
                    v-model="indicatorsForm.indicatorsHeader.calcFormula"
                    type="textarea"
                    :disabled="true"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
            <srm-row>
              <srm-col :initCol="1">
                <el-form-item :label="$t('perfMod.indicatorLogic')" prop="indicatorLogic">
                  <el-input
                    v-model="indicatorsForm.indicatorsHeader.indicatorLogic"
                    :disabled="showDisabled"
                    type="textarea"
                    :rows="2"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
        </el-collapse-item>
        <!-- 指标行信息-评价方式非系统取值 -->
        <el-collapse-item v-if="indicatorsForm.indicatorsHeader.quoteMode != 'INTERVAL_CONVERSION'" :title="$t('perfMod.indicatorLine')" name="2">
          <div class="mb10">
            <el-button
              v-if="!showDisabled"
              type="primary"
              class="detail-pbtn"
              :disabled="addIndLineDis"
              @click="addIndicatorsLine"
            >
              {{ $t('common.new') }}
            </el-button>
          </div>
          <el-table
            ref="indicatorsLineTable"
            :data="indicatorsForm.indicatorsLineList"
            style="width: 100%;"
            :disabled="curOpt === 'view'"
            border
            max-height="250px"
          >
            <el-table-column type="index" align="center" width="50px" />
            <!-- 考核 -->
            <el-table-column
              v-if="indicatorsForm.indicatorsHeader.indicatorType == 'ASSESSMENT'"
              :label="$t('perfMod.title1')"
              align="center"
            >
              <el-table-column
                align="center"
                prop="indicatorLineDes"
                :label="$t('perfMod.indicatorLineDes')"
              >
                <template slot-scope="scope">
                  <el-input v-model="scope.row.indicatorLineDes" :disabled="curOpt === 'view'" />
                </template>
              </el-table-column>
              <el-table-column align="center" prop="assessmentPenalty" :label="$t('perfMod.desc1')">
                <template slot-scope="scope">
                  <el-input-number
                    v-model="scope.row.assessmentPenalty"
                    :disabled="curOpt === 'view'"
                    class="pefScoreInput"
                  />
                </template>
              </el-table-column>
            </el-table-column>
            <!-- 绩效 -->
            <el-table-column
              v-if="indicatorsForm.indicatorsHeader.indicatorType == 'PERFORMANCE'"
              :label="$t('perfMod.title2')"
              align="center"
            >
              <el-table-column
                align="center"
                prop="indicatorLineDes"
                :label="$t('perfMod.indicatorLineDes')"
              >
                <template slot-scope="scope">
                  <el-input v-model="scope.row.indicatorLineDes" :disabled="curOpt === 'view'" />
                </template>
              </el-table-column>
              <el-table-column
                v-if="indicatorsForm.indicatorsHeader.quoteMode == 'INTERVAL_CONVERSION'"
                align="center"
                prop="scoreStart"
                :label="perfTableHeader.scoreStart"
                width="150px"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.scoreStart"
                    :disabled="curOpt === 'view'"
                    type="number"
                  />
                </template>
              </el-table-column>
              <el-table-column
                v-if="indicatorsForm.indicatorsHeader.quoteMode == 'INTERVAL_CONVERSION'"
                align="center"
                prop="scoreEnd"
                :label="perfTableHeader.scoreEnd"
                width="150px"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.scoreEnd"
                    :disabled="curOpt === 'view'"
                    type="number"
                  />
                </template>
              </el-table-column>
              <el-table-column align="center" prop="pefScore" :label="perfTableHeader.scoreDes">
                <template slot-scope="scope">
                  <el-input-number
                    v-model="scope.row.pefScore"
                    :disabled="curOpt === 'view'"
                    class="pefScoreInput"
                  />
                </template>
              </el-table-column>
            </el-table-column>
            <el-table-column
              align="center"
              prop="operation"
              :label="$t('common.operation')"
              width="90"
            >
              <template slot-scope="scope">
                <el-button
                  :disabled="curOpt === 'view'"
                  type="text"
                  @click="delIndicatorLine(scope.$index, scope.row)"
                >
                  {{ $t('common.delete') }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
        <!-- 指标行信息-评价方式系统取值 -->
        <el-collapse-item v-if="indicatorsForm.indicatorsHeader.quoteMode == 'INTERVAL_CONVERSION'" :title="$t('perfMod.indicatorLine')" name="2">
          <div class="mb10">
            <el-button
              v-if="!showDisabled"
              type="primary"
              class="detail-pbtn"
              :disabled="addIndLineDis"
              @click="addIndicatorsLine"
            >
              {{ $t('common.new') }}
            </el-button>
          </div>
          <el-table
            ref="indicatorsLineTable"
            :data="indicatorsForm.indicatorsLineList"
            style="width: 100%;"
            :disabled="curOpt === 'view'"
            border
            max-height="250px"
          >
            <el-table-column type="index" align="center" width="50px" />
            <!-- 绩效 -->
            <el-table-column
              v-if="indicatorsForm.indicatorsHeader.indicatorType == 'PERFORMANCE'"
              :label="$t('perfMod.title2')"
              align="center"
            >
              <!--指标描述-->
              <el-table-column
                align="center"
                prop="indicatorLineDes"
                :label="$t('perfMod.indicatorLineDes')"
              >
                <template slot-scope="scope">
                  <el-input v-model="scope.row.indicatorLineDes" :disabled="curOpt === 'view'" />
                </template>
              </el-table-column>
              <!--评分值始-->
              <el-table-column
                align="center"
                prop="scoreStart"
                :label="$t('perfMod.startSymbol2')"
                width="150px"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.scoreStart"
                    :disabled="curOpt === 'view'"
                    type="number"
                  />
                </template>
              </el-table-column>
              <!--起始符号-->
              <el-table-column
                align="center"
                prop="startSymbol"
                :label="$t('perfMod.startSymbol1')"
                width="150px"
              >
                <template slot-scope="scope">
                  <DictSelect
                    v-model="scope.row.startSymbol"
                    code="INDICATOR_SYMPOL"
                    :disabled="true"
                  />
                </template>
              </el-table-column>
              <!--评分值末-->
              <el-table-column
                align="center"
                prop="scoreEnd"
                :label="$t('perfMod.endSymbol2')"
                width="150px"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.scoreEnd"
                    :disabled="curOpt === 'view'"
                    type="number"
                  />
                </template>
              </el-table-column>
              <!--结束符号-->
              <el-table-column
                align="center"
                prop="endSymbol"
                :label="$t('perfMod.endSymbol1')"
                width="150px"
              >
                <template slot-scope="scope">
                  <DictSelect
                    v-model="scope.row.endSymbol"
                    code="INDICATOR_SYMPOL"
                    :disabled="true"
                  />
                </template>
              </el-table-column>
              <el-table-column align="center" prop="pefScore" :label="perfTableHeader.scoreDes">
                <template slot-scope="scope">
                  <el-input-number
                    v-model="scope.row.pefScore"
                    :disabled="curOpt === 'view'"
                    class="pefScoreInput"
                  />
                </template>
              </el-table-column>
            </el-table-column>
            <el-table-column
              align="center"
              prop="operation"
              :label="$t('common.operation')"
              width="90"
            >
              <template slot-scope="scope">
                <el-button
                  :disabled="curOpt === 'view'"
                  type="text"
                  @click="delIndicatorLine(scope.$index, scope.row)"
                >
                  {{ $t('common.delete') }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
      </el-collapse>
      <CToolbar v-if="curOpt !== 'view'">
        <template slot="right">
          <el-button @click="toBack()">
            {{ $t('common.backTo') }}
          </el-button>
          <el-button type="primary" @click="submitHandle('staging')">
            <!--暂存-->
            {{ $t('common.staging') }}
          </el-button>
          <el-button type="primary" @click="submitHandle()">
            <!--提交-->
            {{ $t('common.submit') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { getDictItemList } from '@/api/common'
import { adaptDictData } from '@/utils'
import CToolbar from 'lib@/components/c-toolbar'
import { performanceManagement } from 'modb@/performanceManagement/api/index'

export default {
  name: 'IndicatorsDetail',
  components: {
    CToolbar
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      bolDict: [], // 字典中有多少个系统加减分的
      activeDims: ['1', '2'],
      indicatorsForm: {
        indicatorsHeader: {
          startSymbol: '',
          endSymbol: '',
          indicatorType: 'PERFORMANCE', // 指标类型  （默认绩效）
          indicatorDimension: '', // 指标维度
          evaluation: '', // 评分方式
          quoteMode: '', // 取值方式
          indicatorName: '', // 指标名称
          indicatorLineType: '', // 指标行类型
          valueName: '', // 取值名称
          calcFormula: '', // 计算公式
          indicatorLogic: '', // 指标逻辑
          enableFlag: 'Y' // 指标状态 添加默认启用
        },
        indicatorsLineList: [] // 指标行数据
      },
      showDisabled: false,
      valueNameData: [], // 取值名称数据
      scoreIs: [], // 评分方式
      scoreIs2: [], // 评分方式
      maxPointsDis: true, // 是否可填写扣分上限
      evaluationDis: false, // 评分方式
      conversionTypeDis: false, // 是否禁用取值类型
      indicatorLineTypeDis: false,
      textDis: false, // 文本类型是都禁用
      directQuoteDis: false, // 直接取值
      textQuoteDis: false, // 文本取值
      addIndLineDis: false, // 新增行信息是否禁用
      rules: {
        indicatorType: [{ required: true, message: this.$t('perfMod.selectMetricType') }],
        indicatorDimension: [{ required: true, message: this.$t('perfMod.selectMetricDimension') }],
        indicatorName: [{ required: true, message: this.$t('perfMod.enterMetricName') }],
        valueName: [{ required: true, message: this.$t('common.pleaseInput') }]
      },
      curOpt: 'add',
      curOrderId: null, // 单据Id
      perfTableHeader: {
        // 指标表格头信息
        scoreStart: () => this.$t('perfMod.scoreStart1'),
        scoreEnd: () => this.$t('perfMod.scoreEnd1'),
        scoreDes: () => this.$t('perfMod.desc2')
      }
    }
  },
  created () {
    this.curOpt = this.$attrs.params.flag

    if (this.$attrs.params.flag == 'view') {
      this.showDisabled = true
    }
    this.switchTableCol() // 切换table
    if (this.$attrs.params.flag === 'edit' || this.$attrs.params.flag === 'view') {
      this.curOrderId = this.$attrs.params.orderId // 单据Id
      this.getOrderFormDetail()
    } else {
      this.indicatorsForm.indicatorsHeader.quoteMode = ''
    }
    let dictParamsArr = [
      { dictCode: 'SCORE_IS' } // 评分方式 scoreIs
    ]
    getDictItemList(dictParamsArr).then(res => {
      const [
        SCORE_IS
      ] = res.data
      this.scoreIs = adaptDictData(SCORE_IS.SCORE_IS, 'dict')
      console.log(this.scoreIs, 'scoreIs')
      this.scoreIs.forEach((e) => {
        if (!['EXTRA_SYSTEM_VALUE', 'DEDUCTION_SYSTEM_VALUE', 'SCORING_SYSTEM_VALUE'].includes(e.value)) {
          this.scoreIs2.push(e)
        } else {
          this.bolDict.push(e)
        }
      })
    })
  },
  methods: {
    startSymbolChange (){ // 选择头信息的起始符号
      const startSymbol = this.indicatorsForm.indicatorsHeader.startSymbol
      let endSymbol = ''
      if (startSymbol == 'GREATER') { // 大于
        endSymbol = 'LESS_EQUAL' // 小于等于
      } else if (startSymbol == 'LESS_EQUAL') { // 小于等于
        endSymbol = 'GREATER' // 大于
      } else if (startSymbol == 'GREATER_EQUAL') { // 大于等于
        endSymbol = 'LESS' // 小于
      } else if (startSymbol == 'LESS') { // 大于等于
        endSymbol = 'GREATER_EQUAL' // 小于
      }
      this.indicatorsForm.indicatorsHeader.endSymbol = endSymbol
      this.indicatorsForm.indicatorsLineList?.forEach(data => {
        this.$set(data,'startSymbol',startSymbol)
        this.$set(data,'endSymbol',endSymbol)
      })
    },
    // 取值名称切换
    valueNameChange (val) {
      console.log(val)
      this.$set(this.indicatorsForm.indicatorsHeader, 'valueName', val)
      const valueName = this.indicatorsForm.indicatorsHeader.valueName
      const valueNameData = JSON.parse(JSON.stringify(this.valueNameData))
      valueNameData.forEach(datas => {
        if (datas.value == valueName) {
          this.indicatorsForm.indicatorsHeader.calcFormula = datas.formula
        }
      })
    },
    // 指标维度切换
    indicatorsDimChange (val, type = null) {
      this.indicatorsForm.indicatorsLineList = []
      if (type !== 'getData') {
        this.indicatorsForm.indicatorsHeader.evaluation = ''
      }
      this.systemScore(val)
    },
    // 显示系统评分状态
    systemScore (val) {
      let scoreIs2 = this.scoreIs2
      this.scoreIs = JSON.parse(JSON.stringify(scoreIs2))
      console.log(scoreIs2)
      if (val == 'DELIVER') {
        this.bolDict.forEach(e => {
          if (['SCORING_SYSTEM_VALUE', 'EXTRA_SYSTEM_VALUE'].includes(e.value)) {
            this.scoreIs.unshift(e)
          }
        })
      } else if (val == 'QUALITY') {
        this.bolDict.forEach(e => {
          if (['SCORING_SYSTEM_VALUE', 'DEDUCTION_SYSTEM_VALUE'].includes(e.value)) {
            this.scoreIs.unshift(e)
          }
        })
      }
    },
    // 评分方式切换
    evaluationDisChange (val) {
      console.log(val)
      this.indicatorsForm.indicatorsHeader.valueName = ''
      if (
        val == 'DEDUCTION_SYSTEM_VALUE' ||
        val == 'SCORING_SYSTEM_VALUE' ||
        val == 'EXTRA_SYSTEM_VALUE'
      ) {
        this.indicatorLineTypeDis = true
        let obj = {
          evaluation: this.indicatorsForm.indicatorsHeader.evaluation,
          indicatorDimensionType: this.indicatorsForm.indicatorsHeader.indicatorDimension
        }
        const _this = this
        performanceManagement.valueDataGet(obj).then(res => {
          if (res.code == '0') {
            _this.valueNameData = res.data
          }
          console.log(_this.valueNameData)
        })
      } else {
        this.indicatorsForm.indicatorsHeader.indicatorLineType = ''
        this.indicatorLineTypeDis = false
      }
      if (val == 'DEDUCTION_SYSTEM_VALUE' || val == 'EXTRA_SYSTEM_VALUE') {
        this.indicatorsForm.indicatorsHeader.indicatorLineType = 'NUMBER'
      }
      if (val == 'SCORING_SYSTEM_VALUE') {
        this.indicatorsForm.indicatorsHeader.indicatorLineType = 'PERCENTAGE'
      }
      this.indicatorsForm.indicatorsHeader.quoteMode = ''
      this.indicatorsForm.indicatorsLineList = []
      this.switchTableCol() // 切换类型
    },
    // 取值方式切换
    conversionTypeChange (val) {
      this.indicatorsForm.indicatorsHeader.indicatorLineType = ''
      this.indicatorsForm.indicatorsLineList = []
      this.switchTableCol() // 切换表格列
    },
    // 指标类型切换
    indicatorsTypeChange (val) {
      if (val == 'PERFORMANCE') {
        // 绩效
        this.indicatorsForm.indicatorsHeader.evaluation = ''
        this.indicatorsForm.indicatorsHeader.quoteMode = ''
        this.indicatorsForm.indicatorsHeader.indicatorLineType = ''
        this.evaluationDis = false // 评分方式
        this.conversionTypeDis = false // 折算类型
        this.indicatorLineTypeDis = false
      } else {
        // 考核 ASSESSMENT
        this.indicatorsForm.indicatorsHeader.evaluation = '' // 评分方式为空
        this.indicatorsForm.indicatorsHeader.quoteMode = 'TEXT_CONVERSION' // 按文本折算
        this.indicatorsForm.indicatorsHeader.indicatorLineType = 'TEXT' // 文本
        this.evaluationDis = true // 评分方式
        this.conversionTypeDis = true // 折算类型
        this.indicatorLineTypeDis = true
      }
      this.indicatorsForm.indicatorsLineList = []
      this.$refs.indicatorsLineTable.doLayout()
    },
    // 指标行类型切换
    indicatorsLineTypeChange (val) {
      this.switchTableCol() // 切换类型
    },
    // 根据切换表格的列显示
    switchTableCol () {
      let type = this.indicatorsForm.indicatorsHeader.indicatorType // 指标类型 PERFORMANCE 绩效 | ASSESSMENT 考核
      let lineType = this.indicatorsForm.indicatorsHeader.indicatorLineType // 行类型(评价值类型) TEXT 文本| NUMBER 数字 | PERCENTAGE 百分比
      let quoteMode = this.indicatorsForm.indicatorsHeader.quoteMode // 取值方式 DIRECT_QUOTE 直接取值| TEXT_CONVERSION 按文本折算 | INTERVAL_CONVERSION 按区间折算
      let evaluation = this.indicatorsForm.indicatorsHeader.evaluation // 评分方式 SCORING_MANUAL 评分-手工| DEDUCTION_MANUAL 扣分-手工 |SCORING_SYSTEM_VALUE 评分-系统取值 | DEDUCTION_SYSTEM_VALUE 扣分-系统取值

      if (type == 'PERFORMANCE') {
        // 绩效
        this.evaluationDis = false // 评分方式 是否禁用
        this.conversionTypeDis = false // 折算类型 是否禁用
        this.indicatorLineTypeDis = false // 评价值类型是否禁用
        // 评分方式 -> 取值方式 -> 评价值类型
        if (evaluation === 'DEDUCTION_MANUAL' || evaluation === 'SCORING_MANUAL' || evaluation === 'EXTRA_MANUAL') {
          //  扣分-手工  | 评分-手工
          this.textDis = false // 禁用文本项
          this.directQuoteDis = false // 禁用直接取值
          this.textQuoteDis = false // 禁用文本折算取值
          if (quoteMode === 'DIRECT_QUOTE') {
            // 直接取值
            this.addIndLineDis = true // 直接取值禁用行信息增加
            if (evaluation === 'DEDUCTION_MANUAL') {
              // 扣分-手工
              this.indicatorsForm.indicatorsHeader.indicatorLineType = 'NUMBER' // 数字
              this.indicatorLineTypeDis = true
              this.maxPointsDis = false // 启用扣分上限
            } else {
              // 评分-手工
              this.indicatorsForm.indicatorsHeader.indicatorLineType = 'NUMBER' // 数字
              this.indicatorLineTypeDis = true
              this.maxPointsDis = true // 禁用扣分上限
            }
          } else if (quoteMode === 'TEXT_CONVERSION') {
            // 按文本折算
            this.indicatorsForm.indicatorsHeader.indicatorLineType = 'TEXT' // 文本
            this.addIndLineDis = false // 直接取值禁用行信息增加
            this.indicatorLineTypeDis = true
            this.maxPointsDis = true // 禁用扣分上限
          } else if (quoteMode === 'INTERVAL_CONVERSION') {
            // 按区间折算
            this.indicatorLineTypeDis = false
            this.addIndLineDis = false // 直接取值禁用行信息增加
            this.textDis = true // 禁用文本项
          }
        } else {
          this.indicatorsForm.indicatorsHeader.quoteMode = 'INTERVAL_CONVERSION'
          this.indicatorLineTypeDis = true
          // 扣分-系统取值 DEDUCTION_SYSTEM_VALUE | 评分-系统取值 SCORING_SYSTEM_VALUE
          this.textDis = true // 禁用文本项
          this.directQuoteDis = true // 禁用直接取值
          this.textQuoteDis = true // 禁用文本折算取值

          if (quoteMode === 'TEXT_CONVERSION') {
            // 按文本折算
            this.indicatorsForm.indicatorsHeader.indicatorLineType = 'TEXT' // 文本
            this.indicatorLineTypeDis = true
            this.maxPointsDis = true // 禁用扣分上限
          }
        }
        // 表格头文字切换
        if (lineType === 'NUMBER') {
          this.perfTableHeader.scoreStart = this.$t('perfMod.scoreStart1')
          this.perfTableHeader.scoreEnd = this.$t('perfMod.scoreEnd1')
          this.perfTableHeader.scoreDes = this.$t('perfMod.desc2')
        } else if (lineType === 'PERCENTAGE') {
          this.perfTableHeader.scoreStart = this.$t('perfMod.scoreStart2')
          this.perfTableHeader.scoreEnd = this.$t('perfMod.scoreEnd2')
          this.perfTableHeader.scoreDes = this.$t('perfMod.desc2')
        } else {
          this.perfTableHeader.scoreStart = this.$t('perfMod.scoreStart1')
          this.perfTableHeader.scoreEnd = this.$t('perfMod.scoreEnd1')
          this.perfTableHeader.scoreDes = this.$t('perfMod.desc2')
        }
      } else {
        // 考核类型
        this.evaluationDis = true // 评分方式
        this.conversionTypeDis = true // 折算类型
        this.indicatorLineTypeDis = true
        this.indicatorsForm.indicatorsHeader.evaluation = '' // 评分方式为空
        this.indicatorsForm.indicatorsHeader.quoteMode = 'TEXT_CONVERSION' // 按文本折算
        this.indicatorsForm.indicatorsHeader.indicatorLineType = 'TEXT' // 文本
      }
      this.$nextTick(() => {
        this.$refs.indicatorsLineTable.doLayout()
      })
    },
    // 新增指标行类型
    addIndicatorsLine () {
      if (!this.indicatorsForm.indicatorsHeader.indicatorType) {
        this.$message({
          message: this.$t('perfMod.selectMetricType'),
          type: 'error'
        })
        return false
      } else {
        if (!this.indicatorsForm.indicatorsHeader.indicatorDimension) {
          this.$message({
            message: this.$t('perfMod.selectIndicatorRowType'),
            type: 'error'
          })
          return false
        }
      }
      if (
        this.indicatorsForm.indicatorsHeader.quoteMode == 'INTERVAL_CONVERSION'
      ) {
        let name = ''
        const valueName = this.indicatorsForm.indicatorsHeader.valueName
        const valueNameData = JSON.parse(JSON.stringify(this.valueNameData))
        valueNameData.forEach(datas => {
          if (datas.value == valueName) {
            name = datas.name
          }
        })
        this.indicatorsForm.indicatorsLineList.push({
          indicatorLineDes: name,
          valueName: valueName,
          assessmentPenalty: null,
          scoreStart: null,
          scoreEnd: null,
          pefScore: null,
          startSymbol: this.indicatorsForm.indicatorsHeader.startSymbol,
          endSymbol: this.indicatorsForm.indicatorsHeader.endSymbol
        })
      } else {
        this.indicatorsForm.indicatorsLineList.push({
          indicatorLineDes: '',
          valueName: '',
          assessmentPenalty: null,
          scoreStart: null,
          scoreEnd: null,
          pefScore: null
        })
      }
    },
    // 查询单据详情
    getOrderFormDetail () {
      let indicatorHeadId = this.curOrderId
      performanceManagement.getIndicatorsDetail({ indicatorHeadId }).then(res => {
        let formInfo = JSON.parse(JSON.stringify(res.data))
        this.$set(this.indicatorsForm, 'indicatorsHeader', formInfo)
        this.indicatorsDimChange(formInfo.indicatorDimension,'getData')
        this.evaluationDisChange(formInfo.evaluation)
        this.indicatorsForm.indicatorsLineList = res.data.indicatorsLineList
        this.switchTableCol() // 切换表格列
        this.$set(this.indicatorsForm.indicatorsHeader, 'valueName', res.data.valueName)
        this.$set(this.indicatorsForm.indicatorsHeader, 'quoteMode', res.data.quoteMode)
        this.$set(this.indicatorsForm.indicatorsHeader, 'indicatorLineType', res.data.indicatorLineType)
        if (this.indicatorsForm.indicatorsHeader.quoteMode == 'DIRECT_QUOTE') {
          this.addIndLineDis = true
        }
      })
    },
    // 删除指标行数据
    delIndicatorLine (index, row) {
      let indicatorLineId = row.indicatorLineId
      if (indicatorLineId) {
        performanceManagement.delIndicatorsLine({ indicatorLineId }).then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
        })
        this.getOrderFormDetail() // 查询单据数据
      } else {
        this.indicatorsForm.indicatorsLineList.splice(index, 1)
      }
    },
    // 返回
    toBack () {
      this.$emit('tab-remove', this.$attrs['tabName'])
      this.__setTabTodo('IndicatorsList.getQuerydata')
      // 如果关闭后没有正常查询列表页，要检查一下列表的name是否一致
    },
    // 提交
    submitHandle (type) {
      this.$refs.indicatorsForm.validate(valid => {
        if (!valid) {
          this.$message({
            message: this.$t('perfMod.enterRequired'),
            type: 'warning'
          })
          return false
        } else {
          if (type == 'staging') {
            this.dataHandle('staging')
          } else {
            if (this.indicatorsForm.indicatorsLineList.length <= 0 && this.indicatorsForm.indicatorsHeader.quoteMode != 'DIRECT_QUOTE') {
              this.$message({
                message: this.$t('perfMod.enterIndicatorRowInfo'),
                type: 'warning'
              })
              return false
            }
            this.dataHandle('save')
          }
        }
      })
    },
    // 数据处理
    dataHandle (type) {
      let submitData = this.indicatorsForm // 单据
      if (type == 'staging') {
        submitData.indicatorsHeader.enableFlag = 'DRAFT'
      } else {
        submitData.indicatorsHeader.enableFlag = ''
      }
      if (this.curOpt === 'add') {
        // 新增
        performanceManagement.saveIndicator(submitData).then(res => {
          if (res) {
            this.$message({
              message: res.message,
              type: 'success'
            })
            if (type === 'save' || type === 'staging') {
              this.$emit('tab-remove', this.$attrs.params.tabName)
              this.curOrderId = res.data
              this.curOpt = 'edit'
              this.getOrderFormDetail() // 查询单据数据
              this.__setTabTodo('IndicatorsList.getQuerydata')
            }
          }
        })
      } else {
        performanceManagement.updateIndicator(submitData).then(res => {
          if (res) {
            this.$message({
              message: res.message,
              type: 'success'
            })
            if (type === 'save') {
              this.$emit('tab-remove', this.$attrs.params.tabName)
              this.curOrderId = res.data
              this.curOpt = 'edit'
              this.getOrderFormDetail() // 查询单据数据
              this.__setTabTodo('IndicatorsList.getQuerydata')
            }
          }
        })
      }
    }
  }
}
</script>
<style scoped lang="scss">
.the-indicators-detail {
  .el-table .el-date-editor {
    width: 135px;
  }
  .el-collapse-item__content > .el-button {
    margin-bottom: 5px;
  }
  .pefScoreInput {
    .el-input-number__decrease {
      display: none !important;
    }
    .el-input-number__increase {
      display: none !important;
    }
  }
}
</style>
<style>
.orgCatPage .c-pagination {
  margin: 10px 5px;
}
.orgCatPage .c-pagination .el-input__inner {
  height: 24px !important;
}
.the-indicators-detail .pefScoreInput .el-input-number__increase,
.the-indicators-detail .pefScoreInput .el-input-number__decrease {
  display: none;
}
</style>
