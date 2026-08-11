<template>
  <el-container class="flex-container the-performanceModelDetail-detail" direction="vertical">
    <el-main>
      <div class="form-container2">
        <el-collapse v-model="activeDims" class="tab-form-style">
          <!-- 模板头信息 -->
          <el-collapse-item
            ref="modeFormHeader"
            :title="$t('perfMod.modeFormHeader')"
            name="1"
            style="border-top:1px solid #e6ebf5;"
          >
            <el-form
              ref="modeForm"
              :model="modeForm"
              label-position="top"
              class="form-incontainer"
              :disabled="modeForm.templateStatus === 'VALID' || curOpt === 'view'"
              :rules="rules"
            >
              <srm-row>
                <!-- 版本号 -->
                <srm-col>
                  <el-form-item :label="$t('perfMod.version')">
                    <el-input v-model="modeForm.version" disabled />
                  </el-form-item>
                </srm-col>
                <!-- 模板状态 -->
                <srm-col>
                  <el-form-item :label="$t('perfMod.templateStatus')">
                    <DictSelect v-model="modeForm.templateStatus" code="PERF_TEMPLATE_STATUS" disabled />
                  </el-form-item>
                </srm-col>
                <!-- 模板名称 -->
                <srm-col>
                  <el-form-item :label="$t('perfMod.templateName')" prop="templateName">
                    <span slot="label">
                      <template>
                        {{ $t('perfMod.templateName') }}
                        <el-tooltip class="item" effect="dark" :content="$t('perfMod.recommended')" placement="top">
                          <i class="el-icon-warning-outline" />
                        </el-tooltip>
                      </template>
                    </span>
                    <el-input v-model="modeForm.templateName" />
                  </el-form-item>
                </srm-col>
                <!-- 是否启用高级公式配置 -->
                <srm-col>
                  <el-form-item :label="$t('perfMod.advancedFormulas')" prop="isFormula">
                    <DictSelect
                      v-model="modeForm.isFormula"
                      code="YES_OR_NO"
                      :disabled="modeForm.templateStatus === 'VALID' || curOpt === 'view'"
                      @change="isFormulaChange"
                    />
                  </el-form-item>
                </srm-col>
                <!-- 创建时间 -->
                <srm-col>
                  <el-form-item :label="$t('perfMod.creationDate')">
                    <el-date-picker
                      v-model="modeForm.creationDate"
                      :format="$formatDatePickerTime"
                      value-format="yyyy-MM-dd HH:mm:ss"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <!-- 采购组织 -->
                <srm-col>
                  <el-form-item :label="$t('perfMod.fullPathId')" prop="organizationName">
                    <OrganizationSelectTree
                      v-model="modeForm.organizationId"
                      :parent-id="-1"
                      node-type="OU"
                      :disabled="modeForm.templateStatus === 'VALID' || curOpt === 'view'"
                      :scope="modeForm"
                      @select="addOrgHandle"
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-form>
          </el-collapse-item>
          <!-- 采购分类 -->
          <el-collapse-item ref="categoryInfo" :title="$t('perfMod.categoryName')" name="2">
            <p style="width:100px">
              <CCategorySelect
                select-type="button"
                :multiple="true"
                :selected-lines="purCategoryList"
                @select="catSelectHandel"
              />
            </p>
            <el-table :data="purCategoryList" style="width: 100%" border max-height="250px">
              <el-table-column align="center" type="index" :label="$t('perfMod.index')" width="60" />
              <el-table-column
                align="center"
                prop="categoryCode"
                :label="$t('perfMod.categoryCode')"
                min-width="200"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="categoryName"
                :label="$t('perfMod.categoryName2')"
                min-width="200"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="categoryFullName"
                :label="$t('perfMod.categoryFullName')"
                min-width="200"
                :show-overflow-tooltip="true"
              />
              <el-table-column :label="$t('common.operation')" width="80">
                <template slot-scope="scope">
                  <el-button type="text" :disabled="curOpt === 'view'" @click="delPurCategory(scope.$index, scope.row)">
                    {{
                      $t('common.delete')
                    }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
          <!-- 模型指标信息 -->
          <el-collapse-item ref="modindicatorInfo" :title="$t('perfMod.modindicatorInfo')" name="3">
            <div v-if="modeForm.templateStatus === 'VALID' || curOpt === 'view'" />
            <div v-else class="detail-wd" @click="addOneIndexDimension">
              <i class="el-icon-plus" />{{ $t('perfMod.addIndicatorDimension') }}
            </div>
            <!-- 指标行信息 -->
            <div v-if="perfTemplateDimWeightList.length > 0" class="modelDimLineInfo">
              <div v-for="(dimItem, index) in perfTemplateDimWeightList" :key="index" class="dimCont">
                <el-form
                  label-width="80px"
                  :model="perfTemplateDimWeightList[index].perfTemplateDimWeight"
                  :disabled="modeForm.templateStatus === 'VALID' || curOpt === 'view'"
                >
                  <srm-row>
                    <srm-col>
                      <el-form-item
                        :label="$t('perfMod.indicatorDimension')"
                        :rules="{
                          required: true,
                          message: $t('perfMod.selectMetricDimension')
                        }"
                      >
                        <!-- 指标维度 -->
                        <el-select
                          v-model="
                            perfTemplateDimWeightList[index].perfTemplateDimWeight
                              .indicatorTypeDimension
                          "
                          popper-class="dimSelect"
                          :placeholder="$t('common.pleaseSelect')"
                          @change="dimSelectChange"
                          @focus="dimFocus(index)"
                        >
                          <el-option class="optionFirst" value="" disabled>
                            <srm-row type="flex" class="select-row">
                              <srm-col class="border">
                                {{ $t('perfMod.dimensionName') }}
                              </srm-col>
                              <srm-col class="border">
                                {{ $t('perfMod.dimensionType') }}
                              </srm-col>
                            </srm-row>
                          </el-option>
                          <el-option
                            v-for="(item, index) in dimensionList"
                            :key="index"
                            :label="item.indicatorDimensionName"
                            :value="item.indicatorTypeDimension"
                          >
                            <span style="float: left">{{ item.indicatorDimensionName }}</span>
                            <span style="float: right; color: #8492a6; font-size: 13px">{{
                              item.indicatorTypeName
                            }}</span>
                          </el-option>
                        </el-select>
                      </el-form-item>
                    </srm-col>
                    <!--权重-->
                    <srm-col v-if="modeForm.isFormula !== 'Y'">
                      <el-form-item
                        :label="$t('perfMod.indicatorDimensionWeight')"
                        :rules="{
                          required: true,
                          message: $t('perfMod.enterDimensionWeights')
                        }"
                      >
                        <!-- 维度权重 -->
                        <el-input
                          v-model="
                            perfTemplateDimWeightList[index].perfTemplateDimWeight
                              .indicatorDimensionWeight
                          "
                          type="number"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :initCol="2">
                      <el-button type="primary" class="detail-pbtn" @click="addIndicators(index)">
                        {{
                          $t('perfMod.addIndicator')
                        }}
                      </el-button>
                      <el-button class="detail-pbtn" @click="delDimData(index)">
                        {{ $t('perfMod.delIndicatorInfo') }}
                      </el-button>
                    </srm-col>
                  </srm-row>
                </el-form>
                <el-table
                  :ref="'indLineTable' + index"
                  :data="perfTemplateDimWeightList[index].perfTemplateLineList"
                  style="width: 100%"
                  max-height="200px"
                  class="mutipTablePage"
                  border
                >
                  <!-- 指标名称 -->
                  <el-table-column
                    align="center"
                    :label="$t('perfMod.indicatorName')"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  >
                    <template slot-scope="scope">
                      <el-select
                        v-model="scope.row.perfTemplateLine.indicatorName"
                        @change="indicatorNameChange"
                        @focus="indicatorNameFocus(scope.$index, index)"
                      >
                        <el-option
                          v-for="(item, i) in indicatorListArr['indicator' + index]"
                          :key="i"
                          :label="item.indicatorName"
                          :value="item.indicatorName"
                        />
                      </el-select>
                    </template>
                  </el-table-column>
                  <el-table-column align="center" :label="$t('perfMod.indicatorLogic')" min-width="200">
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.perfTemplateLine.indicatorLogic" />
                    </template>
                  </el-table-column>
                  <el-table-column
                    align="center"
                    :label="$t('perfMod.evaluation')"
                    width="120"
                    :show-overflow-tooltip="true"
                  >
                    <template slot-scope="scope">
                      <DictSelect v-model="scope.row.perfTemplateLine.evaluation" code="SCORE_IS" disabled />
                    </template>
                  </el-table-column>
                  <el-table-column
                    align="center"
                    :label="$t('perfMod.quoteMode')"
                    width="120"
                    :show-overflow-tooltip="true"
                  >
                    <template slot-scope="scope">
                      <DictSelect v-model="scope.row.perfTemplateLine.quoteMode" code="QUOTE_MODE" disabled />
                    </template>
                  </el-table-column>
                  <el-table-column
                    align="center"
                    :label="$t('perfMod.indicatorLineType')"
                    width="120"
                    :show-overflow-tooltip="true"
                  >
                    <template slot-scope="scope">
                      <DictSelect
                        v-model="scope.row.perfTemplateLine.indicatorLineType"
                        code="INDICATORS_LINE_TYPE"
                        disabled
                      />
                    </template>
                  </el-table-column>
                  <el-table-column
                    v-if="modeForm.isFormula !== 'Y'"
                    align="center"
                    :label="$t('perfMod.dimensionWeight')"
                    width="120"
                  >
                    <template slot-scope="scope">
                      <el-input
                        v-model="scope.row.perfTemplateLine.dimensionWeight"
                        type="number"
                        :disabled="scope.row.perfTemplateLine.dimensionWeightBol"
                      />
                    </template>
                  </el-table-column>
                  <el-table-column :label="$t('common.operation')" width="100" align="center">
                    <template slot-scope="scope">
                      <el-button
                        v-if="
                          scope.row.perfTemplateLine.indicatorName &&
                            (modeForm.templateStatus === 'VALID' || curOpt === 'view')
                        "
                        type="text"
                        @click="editTempDimLineData(index, scope.$index, scope.row)"
                      >
                        {{ $t('common.view') }}
                      </el-button>
                      <div v-else>
                        <el-button
                          v-if="scope.row.perfTemplateLine.indicatorName"
                          type="text"
                          :disabled="modeForm.templateStatus === 'VALID' || curOpt === 'view'"
                          @click="editTempDimLineData(index, scope.$index, scope.row)"
                        >
                          {{ $t('common.edit') }}
                        </el-button>
                        <el-button
                          type="text"
                          :disabled="modeForm.templateStatus === 'VALID' || curOpt === 'view'"
                          @click="delTempDimLineData(index, scope.$index, scope.row)"
                        >
                          {{ $t('common.delete') }}
                        </el-button>
                      </div>
                    </template>
                  </el-table-column>
                </el-table>
                <!--维度公式配置-->
                <section v-if="modeForm.isFormula === 'Y'">
                  <div class="title">
                    <i />
                    <div>{{ $t('perfMod.formulaConfiguration') }}</div>
                  </div>
                  <div class="table-header">
                    {{ $t('priceModel.costElement.calculationFormula') }}
                  </div>
                  <div class="table-body">
                    <el-input
                      v-model="perfTemplateDimWeightList[index].perfTemplateDimWeight.formula"
                      type="textarea"
                      :rows="3"
                    />
                  </div>
                </section>
              </div>
            </div>

            <!-- 弹框区域-->
            <!-- 指标详情弹框 -->
            <el-dialog
              v-el-drag-dialog
              :title="$t('perfMod.editIndicatorDetail')"
              width="1000px"
              :visible.sync="dialogSmartVisible"
              :close-on-click-modal="false"
            >
              <div class="the_item1">
                <div class="the_display_content">
                  <srm-row>
                    <srm-col>
                      {{ $t('perfMod.indicatorName') }}:{{ applicantForm.indicatorName }}
                    </srm-col>
                    <srm-col>
                      {{ $t('perfMod.indicatorDimension') }}：
                      {{ $getDictLabel('INDICATORS_DIM', applicantForm.indicatorDimension) }}
                    </srm-col>
                    <srm-col>
                      {{ $t('perfMod.evaluation') }}：
                      {{ $getDictLabel('SCORE_IS', applicantForm.evaluation) }}
                    </srm-col>
                    <srm-col>
                      {{ $t('perfMod.indicatorType') }}：
                      {{ $getDictLabel('INDICATORS_TYPE', applicantForm.indicatorType) }}
                    </srm-col>
                    <srm-col>
                      {{ $t('perfMod.indicatorLineType') }}:
                      {{ $getDictLabel('INDICATORS_LINE_TYPE', applicantForm.indicatorLineType) }}
                    </srm-col>
                    <srm-col>
                      {{ $t('perfMod.indicatorLogic') }}:{{ applicantForm.indicatorLogic }}
                    </srm-col>
                  </srm-row>
                </div>
              </div>
              <div class="the_item1">
                <p>{{ $t('perfMod.indicatorLine') }}</p>
                <el-table
                  ref="indicatorsLineTable"
                  :data="scoreItemList"
                  style="width: 100%;"
                  border
                  max-height="260px"
                >
                  <!-- 考核 -->
                  <el-table-column
                    v-if="applicantForm.indType == 'ASSESSMENT'"
                    :label="$t('perfMod.title1')"
                    align="center"
                  >
                    <el-table-column align="center" prop="indicatorLineDes" :label="$t('perfMod.indicatorLineDes')">
                      <template slot-scope="scope">
                        <el-input v-model="scope.row.indicatorLineDes" />
                      </template>
                    </el-table-column>
                    <el-table-column align="center" prop="assessmentPenalty" :label="$t('perfMod.assessmentFines')">
                      <template slot-scope="scope">
                        <el-input v-model="scope.row.assessmentPenalty" type="number" />
                      </template>
                    </el-table-column>
                  </el-table-column>
                  <!-- 绩效 -->
                  <el-table-column
                    v-if="applicantForm.indType == 'PERFORMANCE'"
                    :label="$t('perfMod.title2')"
                    align="center"
                  >
                    <el-table-column align="center" prop="indicatorLineDes" :label="$t('perfMod.indicatorLineDes')">
                      <template slot-scope="scope">
                        <el-input v-model="scope.row.indicatorLineDes" />
                      </template>
                    </el-table-column>
                    <el-table-column
                      v-if="applicantForm.quoteModeVal == 'INTERVAL_CONVERSION'"
                      align="center"
                      prop="scoreStart"
                      :label="perfTableHeader.scoreStart"
                      width="150px"
                    >
                      <template slot-scope="scope">
                        <el-input v-model="scope.row.scoreStart" type="number" />
                      </template>
                    </el-table-column>
                    <el-table-column
                      v-if="applicantForm.quoteModeVal == 'INTERVAL_CONVERSION'"
                      align="center"
                      prop="scoreEnd"
                      :label="perfTableHeader.scoreEnd"
                      width="150px"
                    >
                      <template slot-scope="scope">
                        <el-input v-model="scope.row.scoreEnd" type="number" />
                      </template>
                    </el-table-column>
                    <el-table-column align="center" prop="pefScore" :label="perfTableHeader.scoreDes">
                      <template slot-scope="scope">
                        <el-input v-model="scope.row.pefScore" type="number" />
                      </template>
                    </el-table-column>
                  </el-table-column>
                </el-table>
              </div>

              <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="confirmHandel">
                  {{
                    $t('common.confirm')
                  }}
                </el-button>
                <el-button @click="dialogSmartVisible = false">
                  {{ $t('common.backTo') }}
                </el-button>
              </div>
            </el-dialog>
          </el-collapse-item>
          <el-collapse-item
            v-if="modeForm.isFormula === 'Y'"
            ref="modindicatorInfoAll"
            :title="$t('perfMod.modindicatorInfoAll')"
            name="4"
          >
            <section>
              <div class="title">
                <i />
                <div>{{ $t('perfMod.formulaConfiguration') }}</div>
              </div>
              <div class="table-header">
                {{ $t('priceModel.costElement.calculationFormula') }}
              </div>
              <div class="table-body">
                <el-input v-model="modeForm.formula" type="textarea" :rows="3" />
              </div>
            </section>
          </el-collapse-item>
        </el-collapse>
      </div>

      <CToolbar>
        <template slot="right">
          <el-button @click="backTo">
            {{ $t('common.backTo') }}
          </el-button>
          <el-button v-if="modeForm.templateStatus !== 'VALID' && curOpt !== 'view'" type="primary" @click="saveBill">
            {{ $t('common.save') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import CToolbar from 'lib@/components/c-toolbar'
import OrganizationSelectTree from 'lib@/components/organization-selector'
import CCategorySelect from 'lib@/components/c-category-select'
import { tabTodoMixin } from '@/utils/mixins'
import _pick from 'lodash/pick'
import { performanceManagement } from 'modb@/performanceManagement/api/index'

export default {
  name: 'PerformanceModelDetail',
  components: {
    CToolbar,
    OrganizationSelectTree,
    CCategorySelect
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      activeDims: ['1', '2', '3', '4'],
      modeForm: {
        version: '', // 版本号
        templateStatus: 'DRAFT', // 模板状态
        templateName: '', // 模板名称
        isFormula: null, // 是否启用高级公式配置
        creationDate: null, // 创建时间
        organizationId: null, // 采购组织
        fullPathId: null,
        organizationCode: '',
        organizationName: '',
        evaluationPeriod: null,
        formula: ''
      },
      purCategoryList: [], // 采购分类
      perfTemplateDimWeightList: [{ // 模型指标信息
        perfTemplateDimWeight: {
          indicatorTypeDimension: '',
          indicatorDimensionType: '',
          indicatorDimensionWeight: '',
          indicatorType: '',
          formula: ''
        },
        perfTemplateLineList: []
      }],
      lineDataIndex: null,
      scoreIs: [], // 评分方式
      indicatorsLineType: [], // 评价值类型
      quoteMode: [], // 取值方式 DIRECT_QUOTE 直接取值| TEXT_CONVERSION 按文本折算 | INTERVAL_CONVERSION 按区间折算
      indicatorsType: [], // 指标类型
      indicatorsDim: [], // 指标维度
      curOrderId: null, // 单据ID
      rules: {
        templateName: [{ required: true, message: this.$t('perfMod.enterTemplateName') }],
        // '请填写是否启用高级公式配置'
        isFormula: [{ required: true, message: this.$t('perfMod.advancedFormulaConfig') }],
        organizationName: [{ required: true, message: this.$t('perfMod.enterTemplateName2') }]
      },

      applicantForm: {
        indicatorName: '',
        indicatorType: '',
        quoteMode: '',
        evaluation: '', // 评分方式 SCORING_MANUAL 评分-手工| DEDUCTION_MANUAL 扣分-手工 |SCORING_SYSTEM_VALUE 评分-系统取值 | DEDUCTION_SYSTEM_VALUE 扣分-系统取值
        indicatorDimension: '',
        indicatorLineType: '',
        indicatorLogic: ''
      },
      scoreItemList: [], // 编辑行弹框表格信息
      dimensionList: [], // 指标维度列表
      dialogSmartVisible: false,
      indicatorListArr: {},
      curOpt: 'add',
      perfTableHeader: {
        // 指标表格头信息
        scoreStart: this.$t('perfMod.scoreStart1'),
        scoreEnd: this.$t('perfMod.scoreEnd1'),
        scoreDes: this.$t('perfMod.desc2')
      }
    }
  },
  created () {
    this.fatchDictData() // 获取数据字典
    this.curOpt = this.$attrs.params.flag
    if (this.$attrs.params.flag !== 'add') {
      this.curOrderId = this.$attrs.params.orderId // 单据Id
      this.getFormDetail()
    }
  },
  methods: {
    isFormulaChange () {
      this.modeForm.formula = ''
      this.perfTemplateDimWeightList.forEach(data => {
        data.perfTemplateDimWeight['formula'] = ''
      })
    },
    // 获取数据字典
    fatchDictData () {
      // 查询绩效指标和类型下拉
      performanceManagement.getPefTempDimAndOrgCat().then(res => {
        this.dimensionList = res.data
      })
    },
    // 查询单据详情
    getFormDetail () {
      let perfTemplateHeadId = this.curOrderId
      performanceManagement.getPefTemplateDetail({ perfTemplateHeadId }).then(res => {
        let formInfo = res.data.perfTemplateHeader
        this.modeForm = _pick(formInfo, [
          'templateHeadId',
          'templateName',
          'evaluationPeriod',
          'version',
          'organizationId',
          'organizationCode',
          'organizationName',
          'templateStatus',
          'fullPathId',
          'formula',
          'isFormula'
        ])
        this.modeForm = formInfo
        this.purCategoryList = res.data.perfTemplateCategoryList // 品类信息
        this.perfTemplateDimWeightList = this.adeptPerfTemplateDimWeightList(
          res.data.perfTemplateDimWeightList,
        )
      })
    },
    // 适配维度下拉显示
    adeptPerfTemplateDimWeightList (data) {
      let arr = []
      if (data && data.length > 0) {
        data.map((elm, index) => {
          elm.perfTemplateDimWeight.indicatorTypeDimension =
            elm.perfTemplateDimWeight.indicatorType +
            '-' +
            elm.perfTemplateDimWeight.indicatorDimensionType // 构造维度类型+维度
          arr.push(elm)
          // 查询维度下的指标列表
          this.lineDataIndex = index
          let params = {}
          params.indicatorType = elm.perfTemplateDimWeight.indicatorType
          params.indicatorDimension = elm.perfTemplateDimWeight.indicatorDimensionType
          this.getPefTempIndicatorByDim(params, this.lineDataIndex) // 查询维度
        })
      }
      return arr
    },

    // 选择组织
    addOrgHandle (node, instanceId) {
      const { organizationCode, organizationName, organizationId } = node
      this.modeForm.organizationCode = organizationCode
      this.modeForm.organizationName = organizationName
      this.modeForm.organizationId = organizationId
    },

    // 品类选择
    catSelectHandel (data) {
      if (data.length > 0) {
        let catIdList = []
        for (let item of this.purCategoryList) {
          item.categoryId && (catIdList.push(item.categoryId))
        }
        data.forEach(item => {
          if (item.categoryId && !catIdList.includes(item.categoryId)) {
            this.purCategoryList.push({
              categoryId: item.categoryId,
              categoryCode: item.categoryCode,
              categoryName: item.categoryName,
              categoryFullName: item.categoryFullName
            })
          }
        })
      }
    },
    // 删除采购分类
    delPurCategory (index, row) {
      this.purCategoryList.splice(index, 1)
      if (row.templateCategoryId) {
        this.$http({
          url: '/api-pef/template-category/deleteTemplateCategoryByLineId',
          method: 'GET',
          params: { templateCategoryId: row.templateCategoryId },
          loading: true
        })
          .then(data => {
            console.log('data', data)
          })
          .catch(err => {
            console.log(err)
          })
      }
    },

    // 添加指标维度
    addOneIndexDimension () {
      this.perfTemplateDimWeightList.unshift({
        perfTemplateDimWeight: {
          indicatorTypeDimension: '',
          indicatorDimensionType: '',
          indicatorDimensionWeight: '',
          indicatorType: '',
          formula: ''
        },
        perfTemplateLineList: []
      })
    },
    dimFocus (index) {
      this.lineDataIndex = index
    },
    // 维度选择切换
    dimSelectChange (val) {
      if (val) {
        const row = this.dimensionList.find(item => item.indicatorTypeDimension === val)
        if (row) {
          this.perfTemplateDimWeightList[this.lineDataIndex].perfTemplateDimWeight.indicatorDimensionType = row.indicatorDimension // 指标维度
          this.perfTemplateDimWeightList[this.lineDataIndex].perfTemplateDimWeight.indicatorType = row.indicatorType // 指标类型
          // 查询维度下的指标列表
          let params = {}
          params.indicatorType = row.indicatorType
          params.indicatorDimension = row.indicatorDimension
          this.getPefTempIndicatorByDim(params, this.lineDataIndex) // 查询维度
          this.$nextTick(() => {
            this.$refs['indLineTable' + this.lineDataIndex][0].doLayout()
          })
        }
        let count = 0
        this.perfTemplateDimWeightList.map(elm => {
          if (elm.perfTemplateDimWeight.indicatorTypeDimension === val) {
            count += 1
          }
        })
        if (count > 1) {
          this.$message({
            message: this.$t('perfMod.dimensionAdded'),
            type: 'error'
          })
          this.perfTemplateDimWeightList[this.lineDataIndex].perfTemplateDimWeight.indicatorTypeDimension = '' // 类型+维度
          this.perfTemplateDimWeightList[this.lineDataIndex].perfTemplateDimWeight.indicatorDimensionType = '' // 指标维度
          this.perfTemplateDimWeightList[this.lineDataIndex].perfTemplateDimWeight.indicatorType =
            '' // 指标类型
        }
      }
      // 切换指标维度 ,对应的表格指标行信息要清空
      this.perfTemplateDimWeightList[this.lineDataIndex].perfTemplateLineList = [] // 行清空
      this.indicatorListArr['indicator' + this.lineDataIndex] = [] // 当前的指标下拉为空
    },
    // 通过维度和类型 查询指标行数据
    getPefTempIndicatorByDim (params, dimIndex) {
      performanceManagement.getPefTempLineByDim(params).then(res => {
        this.$set(this.indicatorListArr, 'indicator' + dimIndex, res.data)
      })
    },
    // 添加指标行
    addIndicators (index) {
      if (!this.perfTemplateDimWeightList[index].perfTemplateDimWeight.indicatorTypeDimension) {
        this.$message({
          message: this.$t('perfMod.selectMetricDimension'),
          type: 'error'
        })
        return
      }
      this.perfTemplateDimWeightList[index].perfTemplateLineList.push({
        perfTemplateLine: {
          evaluation: '',
          indicatorDimension: '',
          indicatorLineType: '',
          indicatorLogic: '',
          indicatorName: '',
          indicatorType: '',
          valueName: '',
          quoteMode: '',
          dimensionWeight: '',
          dimensionWeightBol: false
        },
        perfTemplateIndsLineList: []
      })
    },
    // 删除当前维度信息
    delDimData (index) {
      delete this.indicatorListArr['indicator' + index]
      let dimWeightId = this.perfTemplateDimWeightList[index].perfTemplateDimWeight.dimWeightId
      if (dimWeightId) {
        performanceManagement.delPefTemplateDimWeight({ dimWeightId }).then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.perfTemplateDimWeightList.splice(index, 1)
        })
      } else {
        this.perfTemplateDimWeightList.splice(index, 1)
      }
    },
    indicatorNameFocus (tablesRowindex, dimIndex) {
      this.lineDataIndex = dimIndex // 维度当前行
      this.indLineDataIndex = tablesRowindex // 指标当前行
    },
    // 选择指标名称
    indicatorNameChange (val) {
      if (val) {
        const row = this.indicatorListArr['indicator' + this.lineDataIndex].find(
          item => item.indicatorName === val,
        )
        console.log(row)
        if (row) {
          // 如果评分方式为加减分的话
          if (
            row.evaluation == 'DEDUCTION_SYSTEM_VALUE' ||
            row.evaluation == 'EXTRA_SYSTEM_VALUE' ||
            row.evaluation == 'EXTRA_MANUAL' ||
            row.evaluation == 'DEDUCTION_MANUAL'
          ) {
            row.dimensionWeightBol = true
          } else {
            row.dimensionWeightBol = false
          }
          // 选择重复提示
          let count = 0
          let perfTemplateLineList =
            this.perfTemplateDimWeightList[this.lineDataIndex].perfTemplateLineList
          perfTemplateLineList.map(elm => {
            if (elm.perfTemplateLine.indicatorName === val) {
              count += 1
            }
          })
          if (count > 1) {
            this.$message({
              message: this.$t('perfMod.metricAdded'),
              type: 'error'
            })
            this.perfTemplateDimWeightList[this.lineDataIndex].perfTemplateLineList[this.indLineDataIndex].perfTemplateLine.indicatorName = ''
            this.perfTemplateDimWeightList[this.lineDataIndex].perfTemplateLineList[this.indLineDataIndex].perfTemplateLine.indicatorDimension = ''
            this.perfTemplateDimWeightList[this.lineDataIndex].perfTemplateLineList[this.indLineDataIndex].perfTemplateLine.indicatorLogic = ''
            this.perfTemplateDimWeightList[this.lineDataIndex].perfTemplateLineList[this.indLineDataIndex].perfTemplateLine.evaluation = ''
            this.perfTemplateDimWeightList[this.lineDataIndex].perfTemplateLineList[this.indLineDataIndex].perfTemplateLine.indicatorLineType = ''
            this.perfTemplateDimWeightList[this.lineDataIndex].perfTemplateLineList[this.indLineDataIndex].perfTemplateLine.indicatorType = ''
            this.perfTemplateDimWeightList[this.lineDataIndex].perfTemplateLineList[this.indLineDataIndex].perfTemplateLine.quoteMode = ''
            this.perfTemplateDimWeightList[this.lineDataIndex].perfTemplateLineList[this.indLineDataIndex].perfTemplateLine.dimensionWeight = null
            this.perfTemplateDimWeightList[this.lineDataIndex].perfTemplateLineList[this.indLineDataIndex].perfTemplateLine.indicatorHeadId = ''
            this.perfTemplateDimWeightList[this.lineDataIndex].perfTemplateLineList[this.indLineDataIndex].perfTemplateLine.valueName = ''
          } else {
            this.perfTemplateDimWeightList[this.lineDataIndex].perfTemplateLineList[this.indLineDataIndex].perfTemplateLine.indicatorDimension = row.indicatorDimension
            this.perfTemplateDimWeightList[this.lineDataIndex].perfTemplateLineList[this.indLineDataIndex].perfTemplateLine.indicatorLogic = row.indicatorLogic

            // 评价方式
            this.perfTemplateDimWeightList[this.lineDataIndex].perfTemplateLineList[this.indLineDataIndex].perfTemplateLine.evaluation = row.evaluation

            // 评分类型
            this.perfTemplateDimWeightList[this.lineDataIndex].perfTemplateLineList[this.indLineDataIndex].perfTemplateLine.indicatorLineType = row.indicatorLineType
            this.perfTemplateDimWeightList[this.lineDataIndex].perfTemplateLineList[this.indLineDataIndex].perfTemplateLine.indicatorType = row.indicatorType

            // 取值方式
            this.perfTemplateDimWeightList[this.lineDataIndex].perfTemplateLineList[this.indLineDataIndex].perfTemplateLine.quoteMode = row.quoteMode
            // 指标权重
            this.perfTemplateDimWeightList[this.lineDataIndex].perfTemplateLineList[this.indLineDataIndex].perfTemplateLine.dimensionWeight = null
            // 是否不显示
            this.perfTemplateDimWeightList[this.lineDataIndex].perfTemplateLineList[this.indLineDataIndex].perfTemplateLine.dimensionWeightBol = row.dimensionWeightBol
            // 符号
            this.perfTemplateDimWeightList[this.lineDataIndex].perfTemplateLineList[this.indLineDataIndex].perfTemplateLine.startSymbol = row.startSymbol
            this.perfTemplateDimWeightList[this.lineDataIndex].perfTemplateLineList[this.indLineDataIndex].perfTemplateLine.endSymbol = row.endSymbol
            try {
              this.perfTemplateDimWeightList[this.lineDataIndex].perfTemplateLineList[this.indLineDataIndex].perfTemplateLine.valueName = row.indicatorsLineList[0].valueName
            } catch (err) {
              console.log(err)
            }

            this.perfTemplateDimWeightList[this.lineDataIndex].perfTemplateLineList[this.indLineDataIndex].perfTemplateLine.indicatorHeadId = row.indicatorHeadId // 指标头ID不保存 该ID用于新增的时候查询指标行信息用到
            // 指标信息
            this.perfTemplateDimWeightList[this.lineDataIndex].perfTemplateLineList[this.indLineDataIndex].perfTemplateIndsLineList = row.indicatorsLineList.map(i => ({
              ...i
            }))
          }
        }
      }
    },
    // 编辑指标-维度-指标行信息
    editTempDimLineData (index, rowIndex, row) {
      this.lineDataIndex = index
      this.indLineDataIndex = rowIndex
      let formData = row.perfTemplateLine
      this.applicantForm.indType = formData.indicatorType
      this.applicantForm.quoteModeVal = formData.quoteMode
      this.applicantForm.indicatorType = formData.indicatorType
        ? this.$getDictLabel('INDICATORS_TYPE', formData.indicatorType)
        : '--'

      this.applicantForm.indicatorDimension = formData.indicatorDimension
        ? this.$getDictLabel('INDICATORS_DIM', formData.indicatorDimension)
        : '--'

      this.applicantForm.quoteMode = formData.quoteMode
        ? this.$getDictLabelByValue(this.quoteMode, formData.quoteMode)
        : '--'
      this.applicantForm.indicatorLineType = formData.indicatorLineType
        ? this.$getDictLabel('INDICATORS_LINE_TYPE', formData.indicatorLineType)
        : '--'
      this.applicantForm.evaluation = formData.evaluation
        ? this.$getDictLabel('SCORE_IS', formData.evaluation)
        : '--'
      this.applicantForm.indicatorName = formData.indicatorName
      this.applicantForm.indicatorLogic = formData.indicatorLogic
      this.perfTemplateDimWeightList[this.lineDataIndex].perfTemplateLineList[this.indLineDataIndex].perfTemplateIndsLineList = row.perfTemplateIndsLineList
      this.scoreItemList = row.perfTemplateIndsLineList
      this.dialogSmartVisible = true
      this.switchTableCol()
    },
    switchTableCol () {
      let type = this.applicantForm.indicatorType // 指标类型 PERFORMANCE 绩效 | ASSESSMENT 考核
      let lineType = this.applicantForm.indicatorLineType // 行类型(评价值类型) TEXT 文本| NUMBER 数字 | PERCENTAGE 百分比
      if (type == 'PERFORMANCE') {
        // 绩效
        // 表格头文字切换
        if (lineType === 'NUMBER') {
          this.perfTableHeader.scoreStart = this.$t('perfMod.scoreStart1')
          this.perfTableHeader.scoreEnd = this.$t('perfMod.scoreEnd1')
          this.perfTableHeader.scoreDes = this.$t('perfMod.scorePenalizedPositive')
        } else if (lineType === 'PERCENTAGE') {
          this.perfTableHeader.scoreStart = this.$t('perfMod.scoreStart2')
          this.perfTableHeader.scoreEnd = this.$t('perfMod.scoreEnd2')
          this.perfTableHeader.scoreDes = this.$t('perfMod.penaltyFlush ')
        } else {
          this.perfTableHeader.scoreStart = this.$t('perfMod.scoreStart1')
          this.perfTableHeader.scoreEnd = this.$t('perfMod.scoreEnd1')
          this.perfTableHeader.scoreDes = this.$t('perfMod.scorePenalizedPositive')
        }
      }
      this.$nextTick(() => {
        this.$refs.indicatorsLineTable.doLayout()
      })
    },
    // 删除指标-维度-指标行信息
    delTempDimLineData (index, rowIndex, row) {
      let templateLineId = row.templateLineId
      if (templateLineId) {
        performanceManagement.delPefTemplateLine({ templateLineId }).then(res => {
          this.perfTemplateDimWeightList[index].perfTemplateLineList.splice(rowIndex, 1)
        })
      } else {
        this.perfTemplateDimWeightList[index].perfTemplateLineList.splice(rowIndex, 1)
      }
    },
    // 编辑确认
    confirmHandel () {
      if (this.scoreItemList.length > 0) {
        this.perfTemplateDimWeightList[this.lineDataIndex].perfTemplateLineList[this.indLineDataIndex].perfTemplateIndsLineList = this.scoreItemList
      }
      this.dialogSmartVisible = false
    },

    // 返回
    backTo () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('performanceModelList.getQuerydata')
    },
    // 保存模板
    saveBill () {
      // 必填校验
      this.$refs.modeForm.validate(valid => {
        if (valid) {
          if (this.perfTemplateDimWeightList.length > 0) {
            let indicatorDimensionWeight = 0 // 维度权重
            let indicatorDimensionWeightFillFlag = true // 维度权重
            let dimensionWeightFillFlag = true // 指标权重
            let bolDimensionWeight = 0
            this.perfTemplateDimWeightList.forEach(item => {
              if (this.modeForm.isFormula !== 'Y') {
                if (!item.perfTemplateDimWeight.indicatorDimensionWeight) {
                  return this.__jump_error__(
                    'modindicatorInfo',
                    null,
                    this.$t('perfMod.addMetricDimension') // '请添加指标维度权重'
                  )
                } else {
                  indicatorDimensionWeight += parseFloat(
                    item.perfTemplateDimWeight.indicatorDimensionWeight,
                  ) // 维度权重相加
                  let perfTemplateLineList = item.perfTemplateLineList
                  let dimensionWeight = 0

                  perfTemplateLineList.forEach(elm => {
                    if (elm.perfTemplateLine.dimensionWeight) {
                      dimensionWeight += parseFloat(elm.perfTemplateLine.dimensionWeight) // 维度下面所有指标
                    }
                  })
                  if (dimensionWeight != 100) {
                    bolDimensionWeight = 1
                  }
                }
              }
              if (!dimensionWeightFillFlag) {
                // 当前指标权重之和不等于100，验证不通过 退出循环
                return false
              }
            })
            if (this.modeForm.isFormula !== 'Y') {
              if (bolDimensionWeight == 1) {
                dimensionWeightFillFlag = false

                return this.__jump_error__(
                  'modindicatorInfo',
                  null,
                  this.$t('perfMod.allIndicators') // '维度下面所有指标权重之和为100!'
                )
              }

              if (indicatorDimensionWeight != 100) {
                this.indicatorDimensionWeightFillFlag = false
                return this.__jump_error__(
                  'modindicatorInfo',
                  null,
                  this.$t('perfMod.allDimensions') // '所有维度权重之和为100!'
                )
              }
            }

            // 各个维度和指标数据总和正确
            if (indicatorDimensionWeightFillFlag && dimensionWeightFillFlag) {
              this.savaHandel() // 保存数据
            }
          } else {
            return this.__jump_error__(
              'modeFormHeader',
              null,
              this.$t('perfMod.modelLineModel') // '请输入必填项!'
            )
          }
        } else {
          this.$message({
            message: this.$t('perfMod.maintainRequired'), // '请维护模型主信息必填项'
            type: 'warning'
          })
          return false
        }
      })
    },
    savaHandel () {
      let submitData = {}
      this.modeForm.evaluationPeriod = 'MONTHLY'
      submitData.perfTemplateHeader = this.modeForm // 头信息
      submitData.perfTemplateCategoryList = this.purCategoryList // 采购品类
      submitData.perfTemplateDimWeightList = this.perfTemplateDimWeightList // 行信息

      submitData.perfTemplateDimWeightList.forEach(e => {
        e.perfTemplateLineList.forEach(r => {
          if (r.perfTemplateLine.dimensionWeight == '') {
            r.perfTemplateLine.dimensionWeight = null
          }
        })
      })

      performanceManagement.savePerfTemplate(submitData).then(res => {
        this.$message({
          message: res.message,
          type: 'success'
        })
        if (this.curOpt === 'add') {
          this.curOpt = 'edit'
          this.curOrderId = res.data
          this.$emit('tab-remove', this.$attrs.tabName)
          this.__setTabTodo('PerformanceModelList.getQueryData')
          this.getFormDetail() // 查询单据数据
        }
      })
    }
  }
}
</script>
<style scoped lang="scss">
.table-header {
  background: #F1F2F2;
  border: 1px solid #DCDDDE;
  padding: 10px 15px;
}

.table-body {
  border: 1px solid #DCDDDE;
  padding: 10px 15px;
}

.title {
  display: flex;
  padding: 10px 0;

  i {
    display: block;
    width: 4px;
    height: 14px;
    background: #0077FF;
    margin-right: 15px;
    margin-top: 5px;
  }
}

.detail-wd {
  height: 30px;
  border: 1px dashed #96999C;
  border-radius: 4px;
  cursor: pointer;
  text-align: center;
  margin-bottom: 15px;
}

.the-performanceModelDetail-detail {
  .form-container2 {
    padding: 5px;
  }

  .modelDimLineInfo {
    .dimCont {
      margin-bottom: 35px;

      .el-form {
        background-color: #f4f5f7;
        padding-top: 20px;

        .el-form-item__label {
          line-height: 32px !important;
        }
      }
    }
  }

  .dimSelect {
    .optionFirst {
      padding: 0 !important;
      text-align: center;
      background-color: #f5f7fa;
    }
  }

  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }

  .el-table .el-date-editor {
    width: 135px;
  }

  .the_display_content {
    .srm-row {
      .srm-col {
        margin-bottom: 10px;
        line-height: 28px;
        height: 28px;
        overflow: hidden;
        text-overflow: ellipsis;
        word-break: break-all;
        white-space: nowrap;
      }
    }

    .the_display_footer {
      text-align: center !important;
    }
  }
}

.toRequired {
  color: #ff4949;
  padding-right: 2px;
}
</style>
