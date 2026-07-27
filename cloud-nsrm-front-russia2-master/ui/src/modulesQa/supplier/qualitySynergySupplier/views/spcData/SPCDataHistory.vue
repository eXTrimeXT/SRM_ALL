<template>
  <el-container class="flex-container the_quick_list__outter_wrapper" direction="vertical">
    <div class="enter-container">
      <el-form
        ref="applyForm"
        :rules="rules"
        :model="form"
      >
        <el-collapse v-model="activeDims" class="tab-form-style">
          <el-collapse-item :title="$t('quality.spc.selectCondition')" name="1">
            <srm-row :gutter="50">
              <srm-col :initCol="4">
                <el-form-item
                  prop="workGroup"
                  :label="$t('quality.spc.workGroup')"
                >
                  <el-select
                    v-model="form.workGroup"
                    clearable
                    filterable
                    allow-create
                    default-first-option
                  >
                    <el-option
                      v-for="item in workGroupMapList"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item :label="$t('quality.productCode')" prop="erpCodeArray">
                  <el-select
                    v-model="form.erpCodeArray"
                    clearable
                    multiple
                  >
                    <el-option
                      v-for="item in erpCodeList"
                      :key="item.value"
                      :label="item.value"
                      :value="item.value"
                    />
                  </el-select>
                  <!-- <el-input v-model="form.erpCode" disabled /> -->
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item :label="$t('quality.spc.workBatch')" prop="workBatch">
                  <el-select
                    v-model="form.workBatch"
                    clearable
                    filterable
                    allow-create
                    default-first-option
                  >
                    <el-option
                      v-for="item in workBatchMapList"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item :label="$t('quality.lineCode')" prop="lineCode">
                  <el-input v-model="form.lineCode" disabled />
                  <!-- <el-select
                    v-model="form.lineCode"
                    clearable
                    filterable
                    allow-create
                    default-first-option
                    placeholder="产线"
                  >
                    <el-option
                      v-for="item in lineCodeMapList"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select> -->
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item :label="$t('quality.project.workCenter')">
                  <el-input v-model="form.workCenter" disabled />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item :label="$t('quality.onLineEquipment')">
                  <el-input v-model="form.onLineEquipment" disabled />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item :label="$t('quality.monitoringFeature')">
                  <el-input v-model="form.monitoringFeature" disabled />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item :label="$t('quality.spc.customerCompanyName')">
                  <el-input v-model="form.customerCompanyName" disabled />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item :label="$t('quality.customerOrgName')">
                  <el-input v-model="form.customerOrgName" disabled />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item :label="$t('quality.spc.productBill')" prop="productBill">
                  <el-select
                    v-model="form.productBill"
                    clearable
                    filterable
                    allow-create
                    default-first-option
                  >
                    <el-option
                      v-for="item in productBillMapList"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item :label="$t('quality.spc.testMember')" prop="testMember">
                  <el-select
                    v-model="form.testMember"
                    clearable
                    filterable
                    allow-create
                    default-first-option
                  >
                    <el-option
                      v-for="item in testMemberMapList"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item :label="$t('quality.spc.queryStartDate')" prop="startDate">
                  <el-date-picker
                    v-model="form.startDate"
                    type="datetime"
                    clearable
                    value-format="yyyy-MM-dd HH:mm:ss"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item :label="$t('quality.spc.queryEndDate')" prop="endDate">
                  <el-date-picker
                    v-model="form.endDate"
                    default-time="23:59:59"
                    type="datetime"
                    clearable
                    value-format="yyyy-MM-dd HH:mm:ss"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4/3">
                <el-form-item class="butCtrl">
                  <el-button type="primary" @click="getQuerydata">
                    {{ $t('common.export') }}
                  </el-button>
                  <el-button @click="onQuery">
                    {{ $t('common.analysis') }}
                  </el-button>
                  <el-button @click="resetForm('applyForm')">
                    {{ $t('common.clear') }}
                  </el-button>
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-collapse-item>
          <el-collapse-item :title="sampleRule === 'Xbar-R' ? 'X-Bar Chart': 'I Chart'" name="2">
            <div class="Xbar-box">
              <div :id="'Xbar-history_' + primaryId" class="Xbar" />
              <div class="button">
                <el-button
                  type="primary"
                  @click="creatPhoto"
                >
                  {{ $t('quality.spc.XbarData') }}
                </el-button>
                <el-button
                  type="primary"
                  :disabled="XbarDataSelected.length <= 0"
                  @click="clearSelectedPoint('Xbar')"
                >
                  {{ $t('quality.spc.clearSelected') }}
                </el-button>
              </div>
            </div>
          </el-collapse-item>
          <el-collapse-item :title="sampleRule === 'Xbar-R' ? 'R Chart': 'MR Chart'" name="3">
            <div class="Xbar-box">
              <div :id="'Rbar-history_' + primaryId" class="Rbar" />
              <div class="button">
                <el-button
                  type="primary"
                  :disabled="RbarDataSelected.length <= 0"
                  @click="clearSelectedPoint('Rbar')"
                >
                  {{ $t('quality.spc.clearSelected') }}
                </el-button>
              </div>
            </div>
          </el-collapse-item>
          <!-- I-MR -->
          <!-- <el-collapse-item v-if="sampleRule === 'I-MR'" title="I-MR Chart" name="4">
            <div class="Xbar-box">
              <div :id="'IMR-history_' + primaryId" class="Imr" />
              <div class="button">
                <el-button
                  type="primary"
                  :disabled="IbarDataSelected.length <= 0"
                  @click="clearSelectedPoint('Ibar')"
                >
                  {{ $t('quality.spc.clearSelected') }}
                </el-button>
              </div>
            </div>
          </el-collapse-item> -->
          <el-collapse-item v-if="spcPageDataShow" :title="$t('quality.spc.total')" name="5">
            <el-table
              v-if="spcPageDataShow"
              :data="abnormalStatisticsPageDtoList"
              stripe
              border
            >
              <el-table-column
                prop="lineCode"
                align="center"
                :label="$t('quality.lineCode')"
              />
              <el-table-column
                prop="monitoringFeature"
                align="center"
                :label="$t('quality.monitoringFeature')"
              />
              <el-table-column
                prop="warningMessage"
                align="center"
                :label="$t('quality.warningMessage')"
              />
              <el-table-column
                prop="amount"
                align="center"
                :label="$t('quality.warningMessageNum')"
              />
            </el-table>
          </el-collapse-item>
          <el-collapse-item :title="$t('quality.spc.XbarData')" name="6">
            <!-- <div class="NDbar" id="NDbar"></div> -->
            <div id="charts3" name="charts3" style="height:400px; position: relative;">
              <canvas
                :id="'sp_spc_can_' + primaryId"
                width="900px"
                height="400px"
                name="sp_spc_can"
                style="position:absolute;z-index:1"
              />
              <canvas
                :id="'sp_spc_can_line_' + primaryId"
                width="900px"
                height="400px"
                name="sp_spc_can_line"
                style="position:absolute;z-index:2"
              />
              <canvas
                :id="'sp_spc_can_pointer_' + primaryId"
                width="900px"
                height="400px"
                name="sp_spc_can_pointer"
                style="position:absolute;z-index:5"
              />
              <div :id="'sp_spc_divinfo_' + primaryId" name="sp_spc_divinfo" class="hidden" style="position:absolute;z-index:6;background: rgba(0,0,0,0.7);color: rgb(255, 255, 255);border-radius: 6px;padding: 10px;" />
            </div>
          </el-collapse-item>
          <el-collapse-item v-if="spcPageDataShow" :title="$t('quality.project.processAblity')" name="7">
            <div v-if="spcPageDataShow" class="spc-footer">
              <srm-row :gutter="30" class="spc-footer-row">
                <srm-col :initCol="6">
                  <div>{{ $t('qualitySynergy.specValueMax') }}</div>
                  <div class="spc-footer-data">
                    {{ spcPageData.max }}
                  </div>
                </srm-col>
                <srm-col :initCol="6">
                  <div>{{ $t('qualitySynergy.specValueMin') }}</div>
                  <div class="spc-footer-data">
                    {{ spcPageData.min }}
                  </div>
                </srm-col>
                <srm-col :initCol="6">
                  <div>{{ $t('quality.standardMin') }}</div>
                  <div class="spc-footer-data">
                    {{ spcPageData.standardMin }}
                  </div>
                </srm-col>
                <srm-col :initCol="6">
                  <div>{{ $t('quality.standardMax') }}</div>
                  <div class="spc-footer-data">
                    {{ spcPageData.standardMax }}
                  </div>
                </srm-col>
                <srm-col :initCol="6">
                  <div>{{ $t('quality.targetValue') }}</div>
                  <div class="spc-footer-data">
                    {{ spcPageData.targetValue }}
                  </div>
                </srm-col>
                <srm-col :initCol="6">
                  <div>{{ $t('quality.spc.avg') }}</div>
                  <div class="spc-footer-data">
                    {{ spcPageData.avg }}
                  </div>
                </srm-col>
              </srm-row>
              <srm-row :gutter="30" class="spc-footer-row">
                <srm-col :initCol="6">
                  <div>{{ $t('quality.spc.subGroupSize') }}</div>
                  <div class="spc-footer-data">
                    {{ spcPageData.subGroupSize }}
                  </div>
                </srm-col>
                <srm-col :initCol="6">
                  <div>{{ $t('quality.spc.innelGroupStandardDeviations') }}</div>
                  <div class="spc-footer-data">
                    {{ spcPageData.innelGroupStandardDeviations }}
                  </div>
                </srm-col>
                <srm-col :initCol="6">
                  <div>{{ $t('quality.spc.wholeStandardDeviation') }}</div>
                  <div class="spc-footer-data">
                    {{ spcPageData.wholeStandardDeviation }}
                  </div>
                </srm-col>
                <srm-col :initCol="6">
                  <div>{{ $t('quality.spc.rangeValue') }}</div>
                  <div class="spc-footer-data">
                    {{ spcPageData.rangeValue }}
                  </div>
                </srm-col>
                <srm-col :initCol="6">
                  <div>{{ $t('quality.spc.maxGroupAvg') }}</div>
                  <div class="spc-footer-data">
                    {{ spcPageData.maxGroupAvg }}
                  </div>
                </srm-col>
                <srm-col :initCol="6">
                  <div>{{ $t('quality.spc.minGroupAvg') }}</div>
                  <div class="spc-footer-data">
                    {{ spcPageData.minGroupAvg }}
                  </div>
                </srm-col>
              </srm-row>
              <srm-row :gutter="30" class="spc-footer-row">
                <srm-col :initCol="6">
                  <div>{{ $t('quality.spc.subGroupCount') }}</div>
                  <div class="spc-footer-data">
                    {{ spcPageData.subGroupCount }}
                  </div>
                </srm-col>
                <srm-col :initCol="6">
                  <div>{{ $t('quality.spc.cp') }}</div>
                  <div class="spc-footer-data">
                    {{ spcPageData.cp }}
                  </div>
                </srm-col>
                <srm-col :initCol="6">
                  <div>{{ $t('quality.spc.cpl') }}</div>
                  <div class="spc-footer-data">
                    {{ spcPageData.cpl }}
                  </div>
                </srm-col>
                <srm-col :initCol="6">
                  <div>{{ $t('quality.spc.cpu') }}</div>
                  <div class="spc-footer-data">
                    {{ spcPageData.cpu }}
                  </div>
                </srm-col>
                <srm-col :initCol="6">
                  <div>{{ $t('quality.spc.cpk') }}</div>
                  <div class="spc-footer-data">
                    {{ spcPageData.cpk }}
                  </div>
                </srm-col>
                <srm-col :initCol="6">
                  <div>{{ $t('quality.spc.pp') }}</div>
                  <div class="spc-footer-data">
                    {{ spcPageData.pp }}
                  </div>
                </srm-col>
              </srm-row>
              <srm-row :gutter="30" class="spc-footer-row">
                <srm-col :initCol="6">
                  <div>{{ $t('quality.spc.ppl') }}</div>
                  <div class="spc-footer-data">
                    {{ spcPageData.ppl }}
                  </div>
                </srm-col>
                <srm-col :initCol="6">
                  <div>{{ $t('quality.spc.ppu') }}</div>
                  <div class="spc-footer-data">
                    {{ spcPageData.ppu }}
                  </div>
                </srm-col>
                <srm-col :initCol="6">
                  <div>{{ $t('quality.spc.ppk') }}</div>
                  <div class="spc-footer-data">
                    {{ spcPageData.ppk }}
                  </div>
                </srm-col>
                <srm-col :initCol="6">
                  <div>{{ $t('quality.spc.ppm') }}</div>
                  <div class="spc-footer-data">
                    {{ form.ppm }}
                  </div>
                </srm-col>
              </srm-row>
            </div>
          </el-collapse-item>
        </el-collapse>
      </el-form>
    </div>
  </el-container>
</template>
<script>
import MainHeader from 'lib@/components/Table/MainHeader'
import { spcData, qualityProject } from '@/modulesQa/supplier/qualitySynergySupplier/api'
import { parseTime } from '@/utils'
import { transformMQL } from '@/library/utils/util'
import { downloadFileLink } from 'lib@/utils/file'
import { mapGetters } from 'vuex'
import echarts from 'echarts'
const { historyPageCondition, historyGraphDatas, historyAnalysisDatas, historyAnalysisDatasFromIds } = spcData
const { getMonitorSpcParam, saveMonitorSpcParam } = qualityProject

export default {
  name: 'SPCDataHistory',
  components: {
    MainHeader
  },
  data () {
    return {
      erpCodeList: [],
      sampleRule: null,
      subGroupCount: null,
      activeDims: ['1', '2', '3', '4', '5', '6', '7'],
      form: {
        customerCompanyName: '',
        customerOrgName: '',
        customerCompanyId: '',
        customerOrgId: '',
        workCenter: '',
        onLineEquipment: '',
        monitoringFeature: '',
        testMember: '',
        erpCode: '',
        erpCodeArray: [],
        workGroup: '',
        workBatch: '',
        lineCode: '',
        productBill: '',
        startDate: '',
        endDate: ''
      },
      rules: {
        lineCode: [{ required: true, message: this.$t('common.pleaseSelect') }],
        erpCodeArray: [{ required: true, message: this.$t('common.pleaseSelect') }]
      },
      spcPageDataShow: false,
      formLabelWidth: '130px',
      lineCodeMapList: [],
      workGroupMapList: [],
      workBatchMapList: [],
      testMemberMapList: [],
      productBillMapList: [],
      spcPageData: {
        max: '',
        min: '',
        standardMax: '',
        standardMin: '',
        targetValue: '',
        avg: '',
        subGroupSize: '',
        cpk: '',
        cpl: '',
        cpu: '',
        maxGroupAvg: '',
        minGroupAvg: '',
        ppk: '',
        ppl: '',
        ppu: '',
        rangeValue: '',
        subGroupCount: '',
        innelGroupStandardDeviations: '',
        wholeStandardDeviation: ''
      },
      Xbar: null,
      XbarData: {},
      XbarDataSelected: [],
      Rbar: null,
      RbarData: {},
      RbarDataSelected: [],
      Ibar: null,
      IbarData: {},
      IbarDataSelected: [],
      NDbar: null,
      historyInputList: [],
      abnormalStatisticsPageDtoList: [],
      selectId: [],
      primaryId: null
    }
  },
  computed: {
    ...mapGetters([
      'userInfo'
    ])
  },
  mounted () {
    let data = this.$attrs.params.param
    this.primaryId = data.id
    this.getRules(data)
    this.form.customerCompanyName = data.customerCompanyName
    this.form.customerOrgName = data.customerOrgName
    this.form.customerCompanyId = data.customerCompanyId
    this.form.erpCode = data.itemCode
    this.form.customerOrgId = data.customerOrgId
    this.form.workCenter = data.workCenter
    this.form.monitoringFeature = data.monitoringFeature
    this.form.onLineEquipment = data.onLineEquipment
    this.form.ruleStandardId = data.id
    this.form.monitorListNo = data.monitorListNo
    this.form.sampleRule = data.sampleRule
    // 历史分析界面查询参数获取
    let params = {
      customerCompanyId: data.customerCompanyId,
      customerOrgId: data.customerOrgId,
      monitoringFeature: data.monitoringFeature,
      workCenter: data.workCenter,
      onLineEquipment: data.onLineEquipment,
      erpCode: data.erpCode,
      ruleStandardId: data.id,
      monitorListNo: data.monitorListNo
    }
    this.getCondition(params)
    this.getErpCodeList()
  },
  methods: {
    getRules (params) {
      this.sampleRule = params.sampleRule // 控制图
      this.subGroupCount = params.subGroupCount // 样本数量
    },
    getErpCodeList () {
      let data = this.$attrs.params.param
      let payload = {
        'filter': {
          'monitorListNo': {
            'eq': data.monitorListNo
          },
          'standardMax': {
            'eq': data.standardMax
          },
          'targetValue': {
            'eq': data.targetValue
          },
          'standardMin': {
            'eq': data.standardMin
          }
        },
        'page': {
          'sort': 'lastUpdateDate desc'
        }
      }
      let transformParams = transformMQL.save('spcRuleStandard', payload, 'query')
      this.$http({
        url: '/api-qc/api-ql/spcRuleStandard/query',
        method: 'POST',
        data: transformParams,
        loading: true
      }).then(res => {
        let result = res?.data?.records || []
        this.erpCodeList = result.map(item => {
          return {
            value: item.itemCode,
            label: item.itemDesc
          }
        })
      })
    },
    // 历史分析界面查询参数获取
    getCondition (params) {
      let transformParams = transformMQL.save('spcDataInput', [params], 'historyPageCondition')
      historyPageCondition(transformParams).then(response => {
        const data = response.data.records[0]
        Object.keys(data.lineCodeMap).forEach(item => {
          let newObj = {}
          newObj.label = item
          newObj.value = data.lineCodeMap[item]
          this.lineCodeMapList.push(newObj)
          this.form.lineCode = data.lineCodeMap[item] // 直接赋值产线
        })
        Object.keys(data.workBatchMap).forEach(item => {
          let newObj = {}
          newObj.label = item
          newObj.value = data.workBatchMap[item]
          this.workBatchMapList.push(newObj)
        })
        Object.keys(data.workGroupMap).forEach(item => {
          let newObj = {}
          newObj.label = item
          newObj.value = data.workGroupMap[item]
          this.workGroupMapList.push(newObj)
        })
        Object.keys(data.testMemberMap).forEach(item => {
          let newObj = {}
          newObj.label = item
          newObj.value = data.testMemberMap[item]
          this.testMemberMapList.push(newObj)
        })
        Object.keys(data.productBillMap).forEach(item => {
          let newObj = {}
          newObj.label = item
          newObj.value = data.productBillMap[item]
          this.productBillMapList.push(newObj)
        })
      })
    },
    // 重置搜索条件
    resetForm (formName) {
      this.$refs[formName].resetFields()
    },
    // 选点生成正态分布图
    creatPhoto () {
      this.$refs.applyForm.validate((valid) => {
        if (valid) {
          this.spcPageDataShow = true
          if (this.selectId.length > 0) { // 已选点
            let transformParams = transformMQL.save('spcDataInput', this.selectId, 'historyAnalysisDatasFromIds')
            historyAnalysisDatasFromIds(transformParams).then(response => {
              const data = response.data.records[0]
              this.setData(data.historyPageCalculate)
              // 生成正态分布图
              if (this.NDbar) {
                this.NDbar.dispose()
              }
              let xData = data.historyPageCalculate.avgValues
              let yData = data.historyPageCalculate.graphValues
              let allAvg = data.historyPageCalculate.avg
              this.abnormalStatisticsPageDtoList = data.abnormalStatisticsPageDtoList
              let innelGroupStandardDeviations = data.historyPageCalculate.innelGroupStandardDeviations
              this.setNormalValueImg(data.historyPageCalculate)
            })
          } else { // 未选点
            const { erpCodeArray, ...rest } = this.form
            let transformParams = transformMQL.save('spcDataInput', [rest], 'historyAnalysisDatas')
            historyAnalysisDatas(transformParams).then(response => {
              const data = response.data.records[0]
              this.setData(data.historyPageCalculate)
              // 生成正态分布图
              if (this.NDbar) {
                this.NDbar.dispose()
              }
              let xData = data.historyPageCalculate.avgValues
              let yData = data.historyPageCalculate.graphValues
              let allAvg = data.historyPageCalculate.avg
              this.abnormalStatisticsPageDtoList = data.abnormalStatisticsPageDtoList
              let innelGroupStandardDeviations = data.historyPageCalculate.innelGroupStandardDeviations
              this.setNormalValueImg(data.historyPageCalculate)
            })
          }
        }
      })
    },
    // 剔除选中的异常点
    clearSelectedPoint (type) {
      if (type === 'Xbar') {
        this.Xbar.dispose()
        this.XbarDataSelected.reverse()
        this.XbarDataSelected.forEach(item => {
          this.XbarData.data.splice(item, 1)
        })
        console.log(this.XbarDataSelected)
        this.$nextTick(() => {
          this.setAvgValueImg(this.XbarData.data, this.XbarData.Xtime, this.XbarData.avg_cl, this.XbarData.avg_ucl, this.XbarData.avg_lcl)
        })
      } else if (type === 'Rbar') {
        this.Rbar.dispose()
        this.RbarDataSelected.reverse()
        this.RbarDataSelected.forEach(item => {
          this.RbarData.data.splice(item, 1)
        })
        this.$nextTick(() => {
          this.setRangeValueImg(this.RbarData.data, this.RbarData.Xtime, this.RbarData.range_cl, this.RbarData.range_ucl, this.RbarData.range_lcl)
        })
      } else if (type === 'Ibar') {
        this.Ibar.dispose()
        this.IbarDataSelected.reverse()
        this.IbarDataSelected.forEach(item => {
          this.IbarData.data.splice(item, 1)
        })
        this.$nextTick(() => {
          this.setSingleValueImg(this.IbarData.data, this.IbarData.Xtime, this.IbarData.range_cl, this.IbarData.range_ucl, this.IbarData.range_lcl)
        })
      }
    },
    setData (data) {
      this.spcPageData.max = data.max
      this.spcPageData.min = data.min
      this.spcPageData.standardMax = data.standardMax
      this.spcPageData.standardMin = data.standardMin
      this.spcPageData.targetValue = data.targetValue
      this.spcPageData.avg = data.avg ? parseFloat(data.avg.toFixed(3)) : data.avg
      this.spcPageData.subGroupSize = data.subGroupSize
      this.spcPageData.cp = data.cp ? parseFloat(data.cp.toFixed(3)) : data.cp
      this.spcPageData.cpk = data.cpk ? parseFloat(data.cpk.toFixed(3)) : data.cpk
      this.spcPageData.cpl = data.cpl ? parseFloat(data.cpl.toFixed(3)) : data.cpl
      this.spcPageData.cpu = data.cpu ? parseFloat(data.cpu.toFixed(3)) : data.cpu
      this.spcPageData.maxGroupAvg = data.maxGroupAvg ? parseFloat(data.maxGroupAvg.toFixed(3)) : data.maxGroupAvg
      this.spcPageData.minGroupAvg = data.minGroupAvg ? parseFloat(data.minGroupAvg.toFixed(3)) : data.minGroupAvg
      this.spcPageData.pp = data.pp ? parseFloat(data.pp.toFixed(3)) : data.pp
      this.spcPageData.ppk = data.ppk ? parseFloat(data.ppk.toFixed(3)) : data.ppk
      this.spcPageData.ppl = data.ppl ? parseFloat(data.ppl.toFixed(3)) : data.ppl
      this.spcPageData.ppu = data.ppu ? parseFloat(data.ppu.toFixed(3)) : data.ppu
      this.spcPageData.rangeValue = data.rangeValue ? parseFloat(data.rangeValue.toFixed(3)) : data.rangeValue
      this.spcPageData.subGroupCount = data.subGroupCount
      this.spcPageData.innelGroupStandardDeviations = data.innelGroupStandardDeviations ? parseFloat(data.innelGroupStandardDeviations.toFixed(3)) : data.innelGroupStandardDeviations
      this.spcPageData.wholeStandardDeviation = data.wholeStandardDeviation ? parseFloat(data.wholeStandardDeviation.toFixed(3)) : data.wholeStandardDeviation
    },
    // 分析
    onQuery () {
      if (!this.sampleRule) return this.$message.warning(this.$t('quality.spc.fillSampleRule'))
      this.$refs.applyForm.validate((valid) => {
        if (valid) {
          if (this.form.erpCodeArray.length && this.form.erpCodeArray.length > this.subGroupCount) return this.$message.warning(this.$t('quality.spc.erpCodeArrayLimit'))
          this.spcPageDataShow = false
          const { erpCodeArray, ...rest } = this.form
          let transformParams = transformMQL.save('spcDataInput', [rest], 'historyGraphDatas')
          historyGraphDatas(transformParams).then(response => {
            const data = response.data.records[0]
            // 清空图表
            if (this.Xbar) {
              this.Xbar.dispose()
            }
            if (this.Rbar) {
              this.Rbar.dispose()
            }
            if (this.NDbar) {
              this.NDbar.dispose()
            }

            if (data.historyInputList.length === 0 || !data.historyPageGraphCalculate) {
              this.$message({
                message: this.$t('quality.spc.historyInputListIsNull'),
                type: 'warning'
              })
              return
            }
            this.form.ppm = data.historyPageGraphCalculate?.ppm

            // 生成均值控制图
            let avgdata = data.historyPageGraphCalculate.avgValues
            let Xtime = []
            this.historyInputList = data.historyInputList
            data.historyInputList.map(item => {
              let time = parseTime(item.creationDate)
              Xtime.push(time)
            })
            if (this.sampleRule === 'Xbar-R') {
              // 生成均值控制图
              let avg_cl = data.historyPageGraphCalculate.avg_cl
              let avg_ucl = data.historyPageGraphCalculate.avg_ucl
              let avg_lcl = data.historyPageGraphCalculate.avg_lcl
              this.setAvgValueImg(avgdata, Xtime, avg_cl, avg_ucl, avg_lcl)
              // 生成极差控制图
              let rangedata = data.historyPageGraphCalculate.rangeValue
              let range_cl = data.historyPageGraphCalculate.range_cl
              let range_ucl = data.historyPageGraphCalculate.range_ucl
              let range_lcl = data.historyPageGraphCalculate.range_lcl
              this.setRangeValueImg(rangedata, Xtime, range_cl, range_ucl, range_lcl)
            } else if (this.sampleRule === 'I-MR') {
              // 单值控制图
              let singleData = data.historyPageGraphCalculate.singleValue
              let iRangeCl = data.historyPageGraphCalculate.range_cl
              let singleUcl = data.historyPageGraphCalculate.singleUCL
              let singleLcl = data.historyPageGraphCalculate.singleLCL
              this.setAvgValueImg(singleData, Xtime, iRangeCl, singleUcl, singleLcl)

              // 移动极差控制图
              let moveData = data.historyPageGraphCalculate.singleValue
              let moveCL = data.historyPageGraphCalculate.moveCL
              let moveUCL = data.historyPageGraphCalculate.moveUCL
              let moveLCL = data.historyPageGraphCalculate.moveLCL
              this.setRangeValueImg(moveData, Xtime, moveCL, moveUCL, moveLCL)
            }
            // 生成正态分布图
            let xData = data.historyPageGraphCalculate.avgValues
            let yData = data.historyPageGraphCalculate.graphValues
            let allAvg = data.historyPageGraphCalculate.avg
            let innelGroupStandardDeviations = data.historyPageGraphCalculate.innelGroupStandardDeviations
            this.setNormalValueImg(data.historyPageGraphCalculate)
          })
        }
      })
    },
    // 导出
    getQuerydata () {
      this.$refs.applyForm.validate((valid) => {
        if (valid) {
          let params = ''
          Object.keys(this.form).forEach(item => {
            if (this.form[item] && (item === 'startDate' || item === 'endDate')) {
              var d = new Date(this.form[item]).getTime()
              var time = parseTime(d)
              params += `${item}=${time}&`
            } else if (this.form[item]) {
              params += `${item}=${encodeURIComponent(this.form[item])}&`
            }
          })
          let href = `/api-qc/spc/input/export?${params}`
          downloadFileLink(href, this.$t('quality.spc.spcEnterExportFile')).catch(() => {
            this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
          })
          // let a = document.createElement('a')
          // a.href = href
          // a.style.display = 'none'
          // document.body.appendChild(a)
          // a.click()
          // a.remove()
        }
      })
    },
    // 生成均值控制图
    setAvgValueImg (data, Xtime, avg_cl, avg_ucl, avg_lcl) {
      this.XbarData = {
        data: data,
        Xtime: Xtime,
        avg_cl: avg_cl,
        avg_ucl: avg_ucl,
        avg_lcl: avg_lcl
      }
      this.XbarDataSelected = []
      var value = []
      let datas = [...data, avg_cl, avg_ucl, avg_lcl]
      let max = Math.max.apply(null, datas)
      let min = Math.min.apply(null, datas)
      let interval = (max - min) / 4
      var dataThat = JSON.parse(JSON.stringify(data))
      let option = {
        title: {},
        xAxis: {},
        yAxis: {},
        label: {},
        tooltip: {},
        series: []
      }
      option.title = {
        text: this.sampleRule === 'Xbar-R' ? this.$t('quality.spc.avgCtrl') : this.$t('quality.spc.simgleCtrl'),
        x: 'center',
        textAlign: 'left',
        textStyle: { // 主标题文本样式{"fontSize": 18,"fontWeight": "bolder","color": "#333"}
          fontSize: 12
        }
      }
      option.xAxis = {
        type: 'category', // 还有其他的type，可以去官网喵两眼哦
        boundaryGap: false,
        data: Xtime, // x轴数据
        splitLine: {
          show: true
        }
      }
      option.yAxis = {
        type: 'value',
        max: max + interval,
        min: min - interval,
        interval: interval,
        // // splitNumber:5,
        axisLabel: {
          formatter: function (value, index) {
            return value.toFixed(3)
          }
        },
        axisTick: {
          show: false
        },
        scale: true
      }
      option.tooltip = {
        trigger: 'axis' // axis   item   none三个值
      }
      option.series = [
        {
          type: 'line',
          symbol: 'circle',
          symbolSize: function (val) {
            if (val > avg_ucl || val < avg_lcl) return 10
            return 5
          },
          data: data,
          itemStyle: {
            color: function (param) { // 拐点颜色回调
              var key = param.name.replace(/<\/?.+?>/g, '').replace(/[\r\n]/g, '')
              if (value.indexOf(key) > -1) {
                return 'green'
              } else if (param.data > avg_ucl || param.data < avg_lcl) {
                return 'red'
              } else {
                return '#000'
              }
            }
          },
          lineStyle: {
            color: '#000'
          },
          markLine: {
            symbol: 'none', // 去掉箭头
            data: [
              {
                yAxis: avg_cl,
                lineStyle: {
                  color: 'rgba(0,255,0)'
                },
                label: {
                  formatter: ' CL {c}'
                }
              },
              {
                yAxis: avg_ucl,
                lineStyle: {
                  color: 'red'
                },
                label: {
                  formatter: ' UCL {c}'
                }
              },
              {
                yAxis: avg_lcl,
                lineStyle: {
                  color: 'red'
                },
                label: {
                  formatter: ' LCL {c}'
                }
              }
            ]
          }
        }
      ]
      this.Xbar = echarts.init(document.getElementById(`Xbar-history_${this.primaryId}`))
      this.Xbar.setOption(option, true)
      this.Xbar.on('click', (param) => {
        let indx = value.indexOf(param.name)
        if (indx < 0) {
          value.push(param.name)
        } else {
          value.splice(indx, 1)
        }
        // 点击保存id
        this.selectId = []
        this.historyInputList.map(item => {
          let time = parseTime(item.creationDate)
          if (value.indexOf(time) > -1) {
            this.selectId.push(item.id)
          }
        })
        // 点击保存Y值下标
        let YIndx = this.XbarDataSelected.indexOf(param.dataIndex)
        if (YIndx < 0) {
          this.XbarDataSelected.push(param.dataIndex)
        } else {
          this.XbarDataSelected.splice(YIndx, 1)
        }
        option.series[0].data = dataThat
        this.Xbar.setOption(option, true)
      })
    },
    // 生成极差控制图
    setRangeValueImg (data, Xtime, range_cl, range_ucl, range_lcl) {
      this.RbarData = {
        data: data,
        Xtime: Xtime,
        range_cl: range_cl,
        range_ucl: range_ucl,
        range_lcl: range_lcl
      }
      this.RbarDataSelected = []
      var value = []
      let datas = [...data, range_cl, range_ucl, range_lcl]
      let max = Math.max.apply(null, datas)
      let min = Math.min.apply(null, datas)
      let interval = (max - min) / 5
      var dataThat = JSON.parse(JSON.stringify(data))
      let option = {
        title: {},
        xAxis: {},
        yAxis: {},
        label: {},
        tooltip: {},
        series: []
      }
      option.title = {
        text: this.sampleRule === 'Xbar-R' ? this.$t('quality.spc.tangeValueImgCtrl') : this.$t('quality.spc.moveTangeValueImgCtrl'),
        x: 'center',
        textAlign: 'left',
        textStyle: {// 主标题文本样式{"fontSize": 18,"fontWeight": "bolder","color": "#333"}
          fontSize: 12
        }
      }
      option.xAxis = {
        type: 'category', // 还有其他的type，可以去官网喵两眼哦
        boundaryGap: false,
        data: Xtime, // x轴数据
        splitLine: {
          show: true
        }
      }
      option.yAxis = {
        type: 'value',
        max: max + interval,
        min: (min - interval),
        interval: interval,
        splitLine: {
          show: true
        },
        axisLabel: {
          formatter: function (value, index) {
            return value.toFixed(3)
          }
        },
        scale: true
      }
      option.tooltip = {
        trigger: 'axis' // axis   item   none三个值
      }
      option.series = [
        {
          type: 'line',
          symbol: 'circle',
          symbolSize: function (val) {
            if (val > range_ucl || val < range_lcl) return 10
            return 5
          },
          data: data,
          itemStyle: {
            color: function (param) { // 拐点颜色回调
              var key = param.name.replace(/<\/?.+?>/g, '').replace(/[\r\n]/g, '')
              if (value.indexOf(key) > -1) {
                return 'green'
              } else if (param.data > range_ucl || param.data < range_lcl) {
                return 'red'
              } else {
                return '#000'
              }
            }
          },
          lineStyle: {
            color: '#000'
          },
          markLine: {
            symbol: 'none', // 去掉箭头
            data: [
              {
                yAxis: range_cl,
                lineStyle: {
                  color: 'rgba(0,255,0)'
                },
                label: {
                  formatter: ' CL {c}'
                }
              },
              {
                yAxis: range_ucl,
                lineStyle: {
                  color: 'red'
                },
                label: {
                  formatter: ' UCL {c}'
                }
              },
              {
                yAxis: range_lcl,
                lineStyle: {
                  color: 'red'
                },
                label: {
                  formatter: ' LCL {c}'
                }
              }
            ]
          }
        }
      ]
      this.Rbar = echarts.init(document.getElementById(`Rbar-history_${this.primaryId}`))
      this.Rbar.setOption(option, true)
      this.Rbar.on('click', (param) => {
        let indx = value.indexOf(param.name)
        if (indx < 0) {
          value.push(param.name)
        } else {
          value.splice(indx, 1)
        }
        // 点击保存Y值下标
        let YIndx = this.RbarDataSelected.indexOf(param.dataIndex)
        if (YIndx < 0) {
          this.RbarDataSelected.push(param.dataIndex)
        } else {
          this.RbarDataSelected.splice(YIndx, 1)
        }
        option.series[0].data = dataThat
        this.Rbar.setOption(option, true)
      })
    },
    // 生成单值移动极差图
    setSingleValueImg (data, Xtime, range_cl, range_ucl, range_lcl) {
      this.IbarData = {
        data: data,
        Xtime: Xtime,
        range_cl: range_cl,
        range_ucl: range_ucl,
        range_lcl: range_lcl
      }
      this.IbarDataSelected = []
      var value = []
      let datas = [...data, range_cl, range_ucl, range_lcl]
      let max = Math.max.apply(null, datas)
      let min = Math.min.apply(null, datas)
      let interval = (max - min) / 5
      var dataThat = JSON.parse(JSON.stringify(data))
      let option = {
        title: {},
        xAxis: {},
        yAxis: {},
        label: {},
        tooltip: {},
        series: []
      }
      option.title = {
        text: this.$t('quality.spc.tangeValueImgCtrl'),
        x: 'center',
        textAlign: 'left',
        textStyle: {// 主标题文本样式{"fontSize": 18,"fontWeight": "bolder","color": "#333"}
          fontSize: 12
        }
      }
      option.xAxis = {
        type: 'category', // 还有其他的type，可以去官网喵两眼哦
        boundaryGap: false,
        data: Xtime, // x轴数据
        splitLine: {
          show: true
        }
      }
      option.yAxis = {
        type: 'value',
        max: max + interval,
        min: (min - interval),
        interval: interval,
        splitLine: {
          show: true
        },
        axisLabel: {
          formatter: function (value, index) {
            return value.toFixed(3)
          }
        },
        scale: true
      }
      option.tooltip = {
        trigger: 'axis' // axis   item   none三个值
      }
      option.series = [
        {
          type: 'line',
          symbol: 'circle',
          symbolSize: function (val) {
            if (val > range_ucl || val < range_lcl) return 10
            return 5
          },
          data: data,
          itemStyle: {
            color: function (param) { // 拐点颜色回调
              var key = param.name.replace(/<\/?.+?>/g, '').replace(/[\r\n]/g, '')
              if (value.indexOf(key) > -1) {
                return 'green'
              } else if (param.data > range_ucl || param.data < range_lcl) {
                return 'red'
              } else {
                return '#000'
              }
            }
          },
          lineStyle: {
            color: '#000'
          },
          markLine: {
            symbol: 'none', // 去掉箭头
            data: [
              {
                yAxis: range_cl,
                lineStyle: {
                  color: 'rgba(0,255,0)'
                },
                label: {
                  formatter: ' CL {c}'
                }
              },
              {
                yAxis: range_ucl,
                lineStyle: {
                  color: 'red'
                },
                label: {
                  formatter: ' UCL {c}'
                }
              },
              {
                yAxis: range_lcl,
                lineStyle: {
                  color: 'red'
                },
                label: {
                  formatter: ' LCL {c}'
                }
              }
            ]
          }
        }
      ]
      this.Ibar = echarts.init(document.getElementById(`IMR-history_${this.primaryId}`))
      this.Ibar.setOption(option, true)
      this.Ibar.on('click', (param) => {
        let indx = value.indexOf(param.name)
        if (indx < 0) {
          value.push(param.name)
        } else {
          value.splice(indx, 1)
        }
        // 点击保存Y值下标
        let YIndx = this.IbarDataSelected.indexOf(param.dataIndex)
        if (YIndx < 0) {
          this.IbarDataSelected.push(param.dataIndex)
        } else {
          this.IbarDataSelected.splice(YIndx, 1)
        }
        option.series[0].data = dataThat
        this.Ibar.setOption(option, true)
      })
    },
    // 生成正态分布图
    setNormalValueImg (_data) {
      console.log('data:::', _data)
      var $sp_spc_can = document.getElementById(`sp_spc_can_${this.primaryId}`)
      var $sp_spc_can_line = document.getElementById(`sp_spc_can_line_${this.primaryId}`)
      var $sp_spc_can_pointer = document.getElementById(`sp_spc_can_pointer_${this.primaryId}`)
      var $divinfos = document.getElementById(`sp_spc_divinfo_${this.primaryId}`)
      var cxt = $sp_spc_can.getContext('2d')
      var padding_y = 70
      var padding_x = 50
      var group_std = _data.innelGroupStandardDeviations
      var data_agv = _data.avg
      var std = _data.wholeStandardDeviation
      var all_max = _data.max
      var all_min = _data.min
      var datas = _data.allDatas
      var spc_style = {}
      spc_style.normal_distribution = spc_style.normal_distribution || {}
      spc_style.normal_distribution.lcl = spc_style.normal_distribution.lcl || '#ff0000'
      spc_style.normal_distribution.ucl = spc_style.normal_distribution.ucl || '#ff0000'
      spc_style.normal_distribution.cl = spc_style.normal_distribution.cl || '#00ff00'
      spc_style.normal_distribution.target = spc_style.normal_distribution.target || '#0000ff'
      spc_style.normal_distribution.curve = spc_style.normal_distribution.curve || '#0000ff'
      spc_style.normal_distribution.bar1 = spc_style.normal_distribution.bar1 || '#33CC00'
      spc_style.normal_distribution.bar2 = spc_style.normal_distribution.bar2 || '#33FF99'
      // 精度浮动信息框处理
      $sp_spc_can_pointer.addEventListener('mousemove', function (event) {
        var rang = axis.xAxis.points[0].max - axis.xAxis.points[0].min
        var tooltipTop = event.offsetY > 250 ? 250 : event.offsetY
        $divinfos.style.left = (event.offsetX + 20) + 'px'
        $divinfos.style.top = tooltipTop + 'px'
        if (event.offsetX >= axis.xAxis.x1 &&
            event.offsetX < axis.xAxis.x2 &&
            event.offsetY >= axis.yAxis.y2 &&
            event.offsetY <= axis.yAxis.y1
        ) {
          for (var i = 0; i < axis.xAxis.points.length; i++) {
            var p = axis.xAxis.points[i]
            if (p.bar_x1 <= event.offsetX && p.bar_x2 > event.offsetX) {
              var html = '<label>' + this.$t('quality.spc.dataRange') + '</label>' + parseFloat(p.min) + '~' + parseFloat(p.max) + '<br/>'
              html += '<label>' + this.$t('quality.spc.count') + '</label>' + p.count + '<br/>'
              html += '<label>x：</label>' + parseFloat(axis.xAxis['xval' + (event.offsetX - axis.xAxis.x1)]).toFixed(6) + '<br/>'
              html += '<label>' + this.$t('quality.spc.rangPersent') + '</label>' + parseFloat(p.count / (datas.length * rang)).toFixed(6) + '<br/>'
              html += '<label>' + this.$t('quality.spc.quxingailv') + '(<span style=\'background-color:#fff;color:' + axis.xAxis['curve_1_color'] + '\'>' + this.$t('quality.spc.zunei') + '</span>)：</label>' + parseFloat(axis.xAxis['curve_1_fx' + (event.offsetX - axis.xAxis.x1)]).toFixed(6) + '<br/>'
              html += '<label>' + this.$t('quality.spc.quxingailv') + '(<span style=\'background-color:#fff;color:' + axis.xAxis['curve_0_color'] + '\'>' + this.$t('quality.spc.zhengti') + '</span>)：</label>' + parseFloat(axis.xAxis['curve_0_fx' + (event.offsetX - axis.xAxis.x1)]).toFixed(6) + '<br/>'
              $divinfos.innerHTML = html
            }
          }
          $divinfos.classList.remove('hidden')
        } else {
          $divinfos.classList.add('hidden')
        }
      })
      // XY轴
      var axis = {
        // y轴
        yAxis: {
          x1: padding_x,
          y1: $sp_spc_can.height - padding_y,
          x2: padding_x,
          y2: padding_y
        },
        // x轴
        xAxis: {
          x1: padding_x,
          y1: $sp_spc_can.height - padding_y,
          x2: $sp_spc_can.width - padding_x,
          y2: $sp_spc_can.height - padding_y
        }
      }
      // 根据数值获取X坐标
      axis.xAxis.getDataX = function (data) {
        var yAxis_width = axis.xAxis.x2 - axis.yAxis.x1
        var data_rang = axis.xAxis.max - axis.xAxis.min
        var data_percent = (data - axis.xAxis.min) / data_rang
        var yAxis_x = axis.xAxis.x1 + yAxis_width * data_percent
        return yAxis_x
      }
      // 根据坐标获取数值
      axis.xAxis.getXData = function (x) {
        if (x < axis.xAxis.x1) {
          return axis.xAxis.min
        } else if (x > axis.xAxis.x2) {
          return axis.xAxis.max
        } else {
          return (x - axis.xAxis.x1) / (axis.xAxis.x2 - axis.xAxis.x1) * (axis.xAxis.max - axis.xAxis.min) + Number(axis.xAxis.min)
        }
      }
      // 根据数值获取Y坐标
      axis.yAxis.getDataY = function (data) {
        var yAxis_height = axis.yAxis.y1 - axis.yAxis.y2
        var data_percent = 0
        if (axis.yAxis.value_rang != 0) {
          data_percent = (data - axis.yAxis.min) / axis.yAxis.value_rang
        }
        var yAxis_y = axis.yAxis.y1 - yAxis_height * data_percent
        return yAxis_y
      }
      // 绘制轴
      function drawers_drawAxis (minX, maxX, datas) {
        var maxCount = 0
        function drawXAxis () {
          var maxXDecimal = maxX.toString().split('.')[1] ? maxX.toString().split('.')[1].length : 0
          var minXDecimal = minX.toString().split('.')[1] ? minX.toString().split('.')[1].length : 0
          var float_len = Math.max(maxXDecimal, minXDecimal)
          for (var i = 0; i < datas.length; i++) {
            var iDecimal = datas[i].toString().split('.')[1] ? datas[i].toString().split('.')[1].length : 0
            float_len = Math.max(float_len, iDecimal)
          }
          maxX = parseFloat((maxX + std).toFixed(float_len))
          minX = parseFloat((minX - std).toFixed(float_len))
          var group_count = parseInt(Math.sqrt(datas.length).toFixed(0)) + 6// 组数

          group_count = Math.min(group_count, 14)
          var all_point_value_x = parseFloat((maxX - minX).toFixed(10))
          var item_point_value_x = parseFloat((all_point_value_x / group_count).toFixed(10))// 组距值宽
          drawLine(axis.xAxis.x1, axis.xAxis.y1, axis.xAxis.x2, axis.xAxis.y2)// x轴
          var item_w = parseFloat(((axis.xAxis.x2 - axis.xAxis.x1) / group_count).toFixed(2))// 组距
          axis.xAxis.max = maxX
          axis.xAxis.min = minX
          axis.xAxis.points = []

          for (var i = 1; i <= group_count; i++) {
            // 绘制x轴刻度
            cxt.textAlign = 'left'
            drawLine(axis.xAxis.x1 + i * item_w - item_w / 2, axis.xAxis.y1, axis.xAxis.x1 + i * item_w - item_w / 2, axis.xAxis.y1 + 10)
            cxt.fillText(parseFloat((minX + item_point_value_x * i - item_point_value_x / 2).toFixed(2)), axis.xAxis.x1 + i * item_w - item_w / 2 - 15, axis.xAxis.y1 + 20)
            axis.xAxis.points.push({
              x: axis.xAxis.x1 + (i - 1) * item_w,
              y: axis.xAxis.y1,
              min: parseFloat((minX + item_point_value_x * (i - 1)).toFixed(float_len)),
              max: parseFloat((minX + item_point_value_x * i).toFixed(float_len)),
              bar_x1: parseFloat((axis.xAxis.x1 + (i - 1) * item_w).toFixed(0)),
              bar_x2: parseFloat((axis.xAxis.x1 + i * item_w).toFixed(0)),
              count: 0,
              rang: item_w
            })
          }
          // 将数据分布到各刻度柱上
          for (var i = 0; i < datas.length; i++) {
            for (var j = 0; j < axis.xAxis.points.length; j++) {
              if (parseFloat(datas[i]) >= parseFloat(axis.xAxis.points[j].min) && parseFloat(datas[i]) < parseFloat(axis.xAxis.points[j].max)) {
                axis.xAxis.points[j].count++
                maxCount = Math.max(maxCount, axis.xAxis.points[j].count)
                break
              }
            }
          }
        }

        function drawYAxis () {
          var minY = 0
          var _maxCount = getMaxCount()
          var rang = axis.xAxis.points[0].max - axis.xAxis.points[0].min
          var maxY = 0
          if (datas.length * rang != 0) {
            maxY = _maxCount / (datas.length * rang)
          }
          var yCount = 5
          var all_point_value_y = maxY - minY
          var item_point_value_y = all_point_value_y / yCount// y轴每个刻度值
          drawLine(axis.yAxis.x1, axis.yAxis.y1, axis.yAxis.x2, axis.yAxis.y2)// y轴
          var item_h = parseFloat(((axis.yAxis.y1 - axis.yAxis.y2) / yCount).toFixed(2))// y轴刻度间隔
          axis.yAxis.max = maxY
          axis.yAxis.min = minY
          axis.yAxis.value_rang = maxY - minY
          axis.yAxis.points = []

          for (var i = 0; i <= yCount; i++) {
            // 绘制y轴刻度
            cxt.textAlign = 'right'
            drawLine(axis.yAxis.x1, axis.yAxis.y1 - item_h * i, axis.yAxis.x1 - 10, axis.yAxis.y1 - item_h * i)
            cxt.fillText(parseFloat((minY + item_point_value_y * i).toFixed(2)), axis.yAxis.x1 - 15, axis.xAxis.y1 - i * item_h + 5)
            axis.yAxis.points.push({ x: axis.yAxis.x1, y: axis.xAxis.y1 - i * item_h })
          }
        }
        function getMaxCount () {
          var _max_count = maxCount
          var rang = axis.xAxis.points[0].max - axis.xAxis.points[0].min
          var max_curve_y_value = fx(data_agv, std, data_agv) // 曲线最大值

          max_curve_y_value = Math.max(max_curve_y_value, fx(data_agv, group_std, data_agv))
          var top_d = 0
          if (datas.length * rang != 0) {
            top_d = _max_count / (datas.length * rang)
          }
          if (max_curve_y_value > top_d && top_d != 0) {
            _max_count = parseInt((max_curve_y_value / top_d) * _max_count + 1)
          }
          return _max_count
        }
        drawXAxis()
        drawYAxis()
      }
      // 绘制柱状图
      function drawers_drawBar (datas) {
        var rang = axis.xAxis.points[0].max - axis.xAxis.points[0].min
        // 计算柱状
        for (var i = 0; i < axis.xAxis.points.length; i++) {
          if (axis.xAxis.points[i].count > 0) {
            var maxY = axis.xAxis.points[i].count / (datas.length * rang)
            var _y2 = axis.yAxis.getDataY(maxY)
            axis.xAxis.points[i].y2 = _y2
            showBar(axis.xAxis.points[i].bar_x1, _y2, axis.xAxis.points[i].bar_x2 - axis.xAxis.points[i].bar_x1, axis.yAxis.points[0].y - _y2, i % 2)
          }
        }
        // 动态显示柱状
        function showBar (x1, y1, w, h, colorIndex) {
          var y2 = y1 + h
          var intCount = 1
          var timer = setInterval(function () {
            cxt.fillStyle = [spc_style.normal_distribution.bar1, spc_style.normal_distribution.bar2][colorIndex]
            cxt.fillRect(x1, y2 - intCount / 10 * h, w, intCount / 10 * h)
            cxt.stroke()
            intCount++
            if (intCount == 11) {
              clearInterval(timer)
            }
          }, 10)
        }
      }
      // 绘制曲线
      function drawers_drawCurve (curve_type, std, color) {
        color = color || spc_style.normal_distribution.curve
        var old_point = null// 上一个曲线点信息
        var curve_points = []// 曲线点
        var curve_point_index = 0// 曲线点下标
        var max_curve_y_value = fx(data_agv, std, data_agv) // 曲线最大值
        var gap = Math.min(Math.max(Math.round((2 - max_curve_y_value) * 10 / 5, 0), 1), 5)
        var len = 0
        var isDraw = true
        for (var i = axis.xAxis.x1; i < axis.xAxis.x2; i++) {
          var x_val = axis.xAxis.getXData(i)
          var y_val = fx(data_agv, std, x_val)
          var _y = axis.yAxis.getDataY(y_val)
          axis.xAxis['curve_' + curve_type + '_color'] = color
          axis.xAxis['curve_' + curve_type + '_fx' + (i - axis.xAxis.x1)] = y_val
          axis.xAxis['xval' + (i - axis.xAxis.x1)] = x_val
          if (old_point != null) {
            if (len == gap) {
              len = 0
              isDraw = !isDraw
            }
            len++
            if (isDraw) {
              curve_points.push({ x1: old_point.x, y1: old_point.y, x2: i, y2: _y })
            }
          }
          old_point = {}
          old_point.x = i
          old_point.y = _y
        }
        // 绘制曲线
        var liner = setInterval(function () {
          for (var i = 0; i < 5; i++) {
            drawLine(curve_points[curve_point_index].x1, curve_points[curve_point_index].y1, curve_points[curve_point_index].x2, curve_points[curve_point_index].y2, { strokeStyle: color, lineWidth: 1 }, $sp_spc_can_line)
            curve_point_index++
            if (curve_point_index >= curve_points.length) {
              clearInterval(liner)
              break
            }
          }
        }, 1)
      }
      // 正态分布函数
      function fx (avg, ret, x) {
        var yValue = 1 / (Math.sqrt(2 * Math.PI) * ret) * Math.exp(-(x - avg) * (x - avg) / (2 * ret * ret))// 正态分布公式
        return yValue
      }
      // 绘制线
      function drawLine (x1, y1, x2, y2, setting, can) {
        setting = setting || {}
        var _cxt = cxt
        if (can) {
          _cxt = can.getContext('2d')
        }
        _cxt.strokeStyle = setting.strokeStyle || '#000'
        _cxt.beginPath()
        _cxt.lineCap = setting.lineCap || 'butt'
        _cxt.lineWidth = setting.lineWidth || 0.2
        _cxt.moveTo(x1, y1)
        _cxt.lineTo(x2, y2)
        _cxt.closePath()
        _cxt.stroke()
      }
      var yLines = []
      // 绘制纵向线条
      function drawers_drawYLine (max, txt, setting) {
        setting = setting || {}
        offset_y = offset_y || 0
        var _x = parseFloat(axis.xAxis.getDataX(max).toFixed(0))
        var offset_y = getYLineOffsetTop(_x)
        yLines.push({
          x1: parseFloat(_x),
          y1: parseFloat(axis.yAxis.y2),
          x2: parseFloat(_x),
          y2: parseFloat(axis.yAxis.y2 - 10 - offset_y),
          offset_y: parseFloat(offset_y)
        })
        for (var y = axis.yAxis.y2 - 10 - offset_y; y <= axis.yAxis.y1; y += 20) {
          drawLine(_x, y, _x, Math.min(y + 10, axis.yAxis.y1), { strokeStyle: setting.strokeStyle || '#ff0000', lineWidth: 1 }, $sp_spc_can_line)
        }
        if (txt) {
          cxt.textAlign = 'center'
          cxt.fillStyle = setting.strokeStyle || '#ff0000'
          cxt.fillText(txt, _x, axis.yAxis.y2 - offset_y - 20)
        }
      }
      function getYLineOffsetTop (x) {
        var intCount = 0
        for (var i = 0; i < yLines.length; i++) {
          if (x > yLines[i].x1 - 50 && x < yLines[i].x1 + 50) {
            intCount++
          }
        }
        return intCount * 20
      }
      // 绘制坐标轴
      drawers_drawAxis(all_min, all_max, datas)
      // 绘制柱图
      drawers_drawBar(datas)
      // 绘制曲线
      drawers_drawCurve(0, std)
      // 绘制曲线
      drawers_drawCurve(1, group_std, '#ff0000')
      // 绘制规格下限
      if (_data.standardMin != null) {
        drawers_drawYLine(parseFloat(_data.standardMin), this.$t('quality.standardMin'), { strokeStyle: spc_style.normal_distribution.lcl })
      }

      // 绘制目标值
      if (_data.targetValue != null) {
        drawers_drawYLine(parseFloat(_data.targetValue), this.$t('quality.targetValue'), { strokeStyle: spc_style.normal_distribution.target })
      }

      // 绘制平均值
      drawers_drawYLine(_data.avg, this.$t('quality.spc.avg'), { strokeStyle: spc_style.normal_distribution.cl })

      // 绘制规格上限
      if (_data.standardMax != null) {
        drawers_drawYLine(parseFloat(_data.standardMax), this.$t('quality.standardMax'), { strokeStyle: spc_style.normal_distribution.ucl })
      }
    }
  }
}
</script>
<style scoped lang="scss">
.Xbar-box {
  position: relative;
  .button {
    position: absolute;
    right: 12px;
    top: 12px;
    z-index: 1000;
  }
}
.Xbar,.Rbar, .NDbar ,.Imr{
  width: 100%;
  height: 400px;
  padding: 20px 0 0;
  border: 1px solid #efefef;
}
.enter-container {
    border: 1px solid #efefef;
    padding: 10px;
    overflow: auto;
}
.spc-footer {
  font-size: 12px;
  color: #606266;
  .spc-footer-row {
    margin-top: 15px;;
  }
  .spc-footer-data {
    line-height: 30px;
    text-align: center;
    border: 1px solid #DCDFE6;
    box-sizing: border-box;
    margin-top: 8px;
  }
}
.butCtrl {
  text-align: right;
  margin-top: 25px;
}
.hidden {
  display: none !important;
}
</style>
