<template>
  <el-container class="flex-container the-quick-demo" direction="vertical">
    <el-main>
      <div class="enter-container">
        <el-form
          ref="spcEnterForm"
          :model="dialogModle.dialogForm"
          :rules="dialogModle.rules"
        >
          <el-collapse v-model="activeDims" class="tab-form-style">
            <el-collapse-item :title="$t('pdca.baseData')" name="1">
              <srm-row :gutter="50">
                <srm-col :initCol="4">
                  <el-form-item
                    prop="monitorListNo"
                    required
                    :label="$t('quality.monitorListNo')"
                  >
                    <el-input v-model="dialogModle.dialogForm.monitorListNo" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="4">
                  <el-form-item
                    prop="workGroup"
                    :label="$t('quality.spc.workGroup')"
                  >
                    <el-select v-model="dialogModle.dialogForm.workGroup">
                      <el-option label="白班" value="白班" />
                      <el-option label="夜班" value="夜班" />
                    </el-select>
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="4">
                  <el-form-item :label="$t('quality.productCode')" prop="erpCode">
                    <el-input v-model="dialogModle.dialogForm.erpCode" :disabled="isDisabled" />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="4">
                  <el-form-item
                    prop="workBatch"
                    :label="$t('quality.spc.workBatch')"
                  >
                    <el-input v-model="dialogModle.dialogForm.workBatch" />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="4">
                  <el-form-item :label="$t('quality.lineCode')" required prop="lineCode">
                    <el-select
                      v-model="dialogModle.dialogForm.lineCode"
                      clearable
                      default-first-option
                      @change="getQueryData"
                    >
                      <el-option
                        v-for="(item,index) in lineCodeMapList"
                        :key="item.lineCode + index"
                        :label="item.lineCode"
                        :value="item.lineCode"
                      />
                    </el-select>
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="4">
                  <el-form-item :label="$t('quality.project.workCenter')">
                    <el-input v-model="dialogModle.dialogForm.workCenter" :disabled="isDisabled" />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="4">
                  <el-form-item :label="$t('quality.onLineEquipment')">
                    <el-input v-model="dialogModle.dialogForm.onLineEquipment" :disabled="isDisabled" />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="4">
                  <el-form-item :label="$t('quality.monitoringFeature')">
                    <el-input v-model="dialogModle.dialogForm.monitoringFeature" :disabled="isDisabled" />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="4">
                  <el-form-item :label="$t('quality.spc.customerCompanyName')">
                    <el-input v-model="dialogModle.dialogForm.customerCompanyName" :disabled="isDisabled" />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="4">
                  <el-form-item :label="$t('quality.customerOrgName')">
                    <el-input v-model="dialogModle.dialogForm.customerOrgName" :disabled="isDisabled" />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="4">
                  <el-form-item
                    prop="productBill"
                    :label="$t('quality.spc.productBill')"
                  >
                    <el-input v-model="dialogModle.dialogForm.productBill" />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="4">
                  <el-form-item :label="$t('quality.subGroupSize')">
                    <el-input v-model="spcPageData.sub_group_size" :disabled="isDisabled" />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="4">
                  <el-form-item
                    prop="productEquitmentCode"
                    :label="$t('quality.spc.productEquitmentCode')"
                  >
                    <el-input v-model="dialogModle.dialogForm.productEquitmentCode" />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="4">
                  <el-form-item
                    prop="testEquitmentCode"
                    :label="$t('quality.spc.testEquitmentCode')"
                  >
                    <el-input v-model="dialogModle.dialogForm.testEquitmentCode" />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="4">
                  <el-form-item
                    :label="$t('quality.spc.modelCav')"
                  >
                    <el-input v-model="dialogModle.dialogForm.modelCav" />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="4">
                  <el-form-item
                    prop="testMember"
                    :label="$t('quality.spc.testMember')"
                  >
                    <el-input v-model="dialogModle.dialogForm.testMember" />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="4">
                  <el-form-item
                    prop="examinedDate"
                    required
                    :label="$t('quality.examinedDate')"
                  >
                    <el-date-picker
                      v-model="dialogModle.dialogForm.examinedDate"
                      type="datetime"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>
            <el-collapse-item :title="$t('quality.testValueEnter')" name="2">
              <srm-row>
                <srm-col :initCol="4">
                  <el-form-item
                    prop="standardMax"
                    :label="$t('quality.standardMax')"
                  >
                    <el-input v-model="dialogModle.dialogForm.standardMax" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="4">
                  <el-form-item
                    prop="targetValue"
                    :label="$t('quality.targetValue')"
                  >
                    <el-input v-model="dialogModle.dialogForm.targetValue" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="4">
                  <el-form-item
                    prop="standardMin"
                    :label="$t('quality.standardMin')"
                  >
                    <el-input v-model="dialogModle.dialogForm.standardMin" disabled />
                  </el-form-item>
                </srm-col>
              </srm-row>
              <div class="enter-data">
                <div class="enter-title">
                  {{ $t('quality.testStandard') }}
                  <span v-if="standardMin">{{ standardMin }}≤</span>
                  <span style="color:red">{{ $t('quality.testValue') }}</span>
                  <span v-if="standardMax">≤{{ standardMax }}</span>
                </div>
                <div class="enter-datas">
                  <div class="enter-labels">
                    <el-row type="flex">
                      <el-col v-for="(item,index) in 15" :key="index">
                        <div class="enter-label">
                          {{ $t('quality.sample') }} <span>{{ index+1 }}</span>
                        </div>
                      </el-col>
                    </el-row>
                  </div>
                  <div class="enter-inputs">
                    <el-row type="flex">
                      <el-col v-for="(item,index) in dialogModle.dialogForm.measureDataList" :key="index+'e1'">
                        <el-form-item
                          label-width="0px"
                          :show-message="false"
                          :prop="'measureDataList[' + index + '].data'"
                          :rules="dialogModle.rules.measureData"
                        >
                          <el-input v-model="item.data" type="number" @blur="standarInput" />
                        </el-form-item>
                      </el-col>
                      <el-col v-for="(item,index) in (15-spcPageData.sub_group_size)" :key="index+'e2'">
                        <el-form-item
                          label-width="0px"
                          :show-message="false"
                        >
                          <el-input v-model="item.data" disabled />
                        </el-form-item>
                      </el-col>
                    </el-row>
                  </div>
                  <div class="enter-title">
                    {{ $t('quality.measuringDetail') }}
                  </div>
                  <div class="enter-textarea">
                    <srm-row type="flex">
                      <srm-col :initCol="1">
                        <el-form-item
                          label-width="0px"
                        >
                          <el-input v-model="dialogModle.dialogForm.measureDesc" type="textarea" />
                        </el-form-item>
                      </srm-col>
                    </srm-row>
                  </div>
                </div>
              </div>
            </el-collapse-item>
            <el-collapse-item :title="$t('quality.testResult')" name="3">
              <el-table
                :data="spcPageData.historyInputData"
                stripe
                border
                style="width: 100%"
              >
                <el-table-column
                  v-for="(item,index) in spcPageData.historyInputColumn"
                  :key="item.historyInputColumn"
                  :prop="'data'+index"
                  :fixed="index===0?true:false"
                  :width="index===0?'90':'160'"
                  :align="index===0?'center':'right'"
                  :label="item.datetimeCreated"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <div v-html="formatter(scope.row['data'+index])" />
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <el-collapse-item title="-X-Bar Chart" name="4">
              <div id="Xbar" class="Xbar" />
            </el-collapse-item>
            <el-collapse-item title="-R Chart" name="5">
              <div id="Rbar" class="Rbar" />
            </el-collapse-item>
            <el-collapse-item :title="$t('quality.inputCalculate')" name="6">
              <el-table
                :data="spcPageData.inputCalculate"
                stripe
                border
                :show-header="false"
                style="width: 100%"
              >
                <el-table-column
                  v-if="inputCalculate&&inputCalculate.minValues"
                  prop="data0"
                  fixed
                  width="100"
                />
                <el-table-column
                  v-for="(item,index) in inputCalculate.minValues"
                  :key="index"
                  width="100"
                  align="right"
                  :prop="'data'+(index+1)"
                />
              </el-table>
              <div v-if="spcPageData.maxGroupAvg" class="spc-footer">
                <srm-row :gutter="30" class="spc-footer-row">
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
                </srm-row>
                <srm-row :gutter="30" class="spc-footer-row">
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
                </srm-row>
              </div>
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </div>
      <CToolbar>
        <template slot="right">
          <el-button @click="back">
            {{ $t('common.cancel') }}
          </el-button>
          <el-button type="primary" @click="submitOne">
            {{ $t('quality.dataSubmit') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import MainHeader from 'lib@/components/Table/MainHeader'
import { qualityProject, spcData } from '@/modulesQa/supplier/qualitySynergySupplier/api'
import { parseTime } from '@/utils'
import { transformMQL } from '@/library/utils/util'
import { mapGetters } from 'vuex'
import echarts from 'echarts'
const { pageCondition } = qualityProject
const { spcInputAdd, spcListPageData } = spcData

export default {
  name: 'SPCDataEnter',
  components: {
    MainHeader, CToolbar
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      activeDims: ['1', '2', '3', '4', '5', '6'],
      dialogModle: {
        dialogForm: {
          'monitorListNo': '',
          'drawingsArea': '',
          'characterUnit': '',
          'standardMax': '',
          'targetValue': '',
          'standardMin': '',
          'customerCompanyName': '',
          'customerOrgName': '',
          'productCode': '',
          'workBatch': '',
          'customerCompanyId': '',
          'customerOrgId': '',
          'erpSupplierId': '',
          'productBill': '',
          'lineCode': '',
          'workCenter': '',
          'onLineEquipment': '',
          'monitoringFeature': '',
          'workGroup': '',
          // 'productModel': '',
          'modelCav': '',
          measureDataList: [],
          'measureData': '',
          'measureDesc': '',
          'examinedDate': new Date(),
          'productEquitmentCode': '',
          'testEquitmentCode': '',
          'testMember': '',
          'checkData': '',
          'inputGroupId': '',
          'chartType': '',
          'version': '',
          'isExceptionPoint': '',
          'erpCode': ''
        },
        rules: {
          // workGroup: [{ required: true, message: this.$t('common.pleaseSelect')}],
          lineCode: [{ required: true, message: this.$t('common.pleaseInput') }],
          // workBatch: [{ required: true, message: this.$t('common.pleaseInput') }],
          // productBill: [{ required: true, message: this.$t('common.pleaseInput') }],
          // productEquitmentCode: [{ required: true, message: this.$t('common.pleaseInput') }],
          // testEquitmentCode: [{ required: true, message: this.$t('common.pleaseInput') }],
          // testMember: [{ required: true, message: this.$t('common.pleaseInput') }],
          examinedDate: [{ required: true, message: this.$t('common.pleaseInput') }],
          measureData: [{ required: true, message: this.$t('common.pleaseInput') }]
        }
      },
      ProductCodeList: [],
      lineCodeMapList: [],
      testEquitmentCodeList: [],
      productEquitmentCodeList: [],
      inputCalculate: {},
      isDisabled: this.$attrs.params.flag == 'enter',
      formLabelWidth: '130px',
      spcPageData: {
        sub_group_size: '',
        standard_min: '',
        standard_max: '',
        historyInputData: [],
        historyInputColumn: [],
        inputCalculate: []
      },
      standardMax: '',
      standardMin: '',
      modelData: {}
    }
  },
  computed: {
    ...mapGetters([
      'userInfo'
    ])
  },
  async mounted () {
    let data = this.$attrs.params.param
    console.log('data:::', data)
    this.modelData = this.$attrs.params.param
    this.dialogModle.dialogForm.customerCompanyName = data.customerCompanyName
    this.dialogModle.dialogForm.customerCompanyId = data.customerCompanyId
    this.dialogModle.dialogForm.customerOrgId = data.customerOrgId
    this.standardMax = data.standardMax
    this.standardMin = data.standardMin
    this.spcPageData.sub_group_size = data.subGroupCount
    for (var i = 0; i < this.spcPageData.sub_group_size; i++) {
      this.dialogModle.dialogForm.measureDataList.push({ data: '' })
    }
    this.dialogModle.dialogForm.erpSupplierId = data.erpSupplierId
    this.dialogModle.dialogForm.erpSupplierName = data.erpSupplierName
    this.dialogModle.dialogForm.erpCode = data.itemCode
    this.dialogModle.dialogForm.workCenter = data.workCenter
    this.dialogModle.dialogForm.monitoringFeature = data.monitoringFeature
    // this.dialogModle.dialogForm.productModel = data.itemCode
    this.dialogModle.dialogForm.onLineEquipment = data.onLineEquipment
    this.dialogModle.dialogForm.customerOrgName = data.customerOrgName
    this.dialogModle.dialogForm.version = data.version
    this.dialogModle.dialogForm.monitorListNo = data.monitorListNo
    this.dialogModle.dialogForm.ruleStandardId = data.id
    this.dialogModle.dialogForm.standardMax = data.standardMax
    this.dialogModle.dialogForm.targetValue = data.targetValue
    this.dialogModle.dialogForm.standardMin = data.standardMin
    this.dialogModle.dialogForm.drawingsArea = data.drawingsArea
    this.dialogModle.dialogForm.characterUnit = data.characterUnit
    this.getLineCodeAll()
  },
  methods: {
    // 获取所有产线
    async getLineCodeAll () {
      let param = {
        filter: {
          monitorListNo: {
            'eq': this.modelData.monitorListNo
          }
        }
      }
      let transformParams = transformMQL.save('spcMonitorList', param, 'pageCondition')
      await pageCondition(transformParams).then(response => {
        const data = response.data.records[0]
        this.lineCodeMapList = []
        Object.keys(data.lineCodeMap).forEach(key => {
          this.lineCodeMapList.push({
            lineCode: key
          })
        })
      })
    },
    back () {
      this.$emit('tab-remove', this.$attrs.tabName)
    },
    // 获取数据展示
    async getQueryData () {
      this.lineCodeMapList.map(item => {
        if (item.lineCode === this.dialogModle.dialogForm.lineCode) {
          this.dialogModle.dialogForm.productEquitmentCode = item.productionEquipment
          this.dialogModle.dialogForm.testEquitmentCode = item.testEquipment
        }
      })
      // 获取数据展示
      let { workCenter, onLineEquipment, monitoringFeature, customerCompanyId, customerOrgId, monitorListNo, id: ruleStandardId } = this.modelData
      let params = { workCenter, onLineEquipment, monitoringFeature, customerCompanyId, customerOrgId, monitorListNo, ruleStandardId }
      params.lineCode = this.dialogModle.dialogForm.lineCode
      let otherParams = JSON.parse(JSON.stringify(params))
      let initParams = {}
      Object.keys(otherParams).forEach(key => {
        initParams[key] = { eq: otherParams[key] }
      })
      let transformParams = transformMQL.save('spcDataInput', { filter: initParams }, 'inputPageAnalysisData')
      // 查询数据
      let response = await spcListPageData(transformParams)
      const data = response.data.records[0]
      let rule = JSON.parse(data.standParam.rule)
      // 结果数据
      this.spcPageData.historyInputData = []
      this.setHistorydata(data.showDatas)
      let resInputCalculate = data.inputCalculate || {}
      // 计算值数据
      this.inputCalculate = resInputCalculate
      this.setInputCalculate(this.inputCalculate)

      // 生成均值控制图
      let avgdata = resInputCalculate.avgValues
      let Xtime = resInputCalculate.dateStrings
      let avg_cl = resInputCalculate.avg_cl
      let avg_ucl = resInputCalculate.avg_ucl
      let avg_lcl = resInputCalculate.avg_lcl
      this.setAvgValueImg(avgdata, Xtime, avg_cl, avg_ucl, avg_lcl)
      // 生成极差控制图
      let rangedata = resInputCalculate.rangeValue
      let range_cl = resInputCalculate.range_cl
      let range_ucl = resInputCalculate.range_ucl
      let range_lcl = resInputCalculate.range_lcl
      this.setRangeValueImg(rangedata, Xtime, range_cl, range_ucl, range_lcl)
    },
    // blur (val) {
    //   let num = 0
    //   this.dialogModle.dialogForm.measureDataList.map((item,index) => {
    //     if (item.data < this.standardMin || item.data > this.standardMax) {
    //       this.flag = true
    //       this.dialogModle.rules.measureDesc[0].required = true
    //     } else {
    //       num++
    //     }
    //   })
    //   if (num == this.spcPageData.sub_group_size) {
    //     this.dialogModle.rules.measureDesc[0].required = false
    //   }
    // },
    // 测量值录入校验是否符合测量标准
    standarInput (e) {
      let flag = false
      let val = Number(e.target.value)
      if (this.standardMax && this.standardMin) {
        flag = !!(val > this.standardMax || val < this.standardMin)
      } else if (this.standardMax && !this.standardMin) {
        flag = val > this.standardMax
      } else if (!this.standardMax && this.standardMin) {
        flag = val < this.standardMin
      }
      if (flag) {
        e.target.style.color = 'red'
        e.target.style.borderColor = 'red'
      } else {
        e.target.style.color = '#000'
        e.target.style.borderColor = '#B9BABD'
      }
    },
    // 合并列
    // 超过规格值标红
    formatter (data) {
      let flag
      if (this.standardMax && this.standardMin) {
        flag = !!(data > this.standardMax || data < this.standardMin)
      } else if (this.standardMax && !this.standardMin) {
        flag = data > this.standardMax
      } else if (!this.standardMax && this.standardMin) {
        flag = data < this.standardMin
      }
      return flag ? `<span style="color:red;">${data}</span>` : data
    },
    // 结果数据
    setHistorydata (data) {
      this.spcPageData.historyInputColumn = []
      this.spcPageData.historyInputColumn.push({ datetimeCreated: this.$t('quality.dateTime') })
      if (!data) return
      data.map((item, index) => {
        if (index === 0) {
          item.map((iem, idx) => {
            this.spcPageData.historyInputColumn.push({ datetimeCreated: iem })
          })
        }
        if (index > 0 && index < data.length - 1) {
          let newObj = {}
          newObj['data0'] = this.$t('quality.testValue') + index
          item.map((iem, idx) => {
            newObj['data' + (idx + 1)] = iem
          })
          this.spcPageData.historyInputData.push(newObj)
        }
        // 最后一项是测量描述
        if (index === data.length - 1) {
          let newObj = {}
          newObj['data0'] = this.$t('quality.measuringDetail')
          item.map((iem, idx) => {
            newObj['data' + (idx + 1)] = iem
          })
          this.spcPageData.historyInputData.push(newObj)
        }
      })
    },
    // 生成均值控制图
    setAvgValueImg (data, Xtime, avg_cl, avg_ucl, avg_lcl) {
      let datas = [...data, avg_cl, avg_ucl, avg_lcl]
      let max = Math.max.apply(null, datas)
      let min = Math.min.apply(null, datas)
      let interval = (max - min) / 4
      let option = {
        title: {},
        xAxis: {},
        yAxis: {},
        label: {},
        tooltip: {},
        series: []
      }
      option.title = {
        text: this.dialogModle.dialogForm.lineCode + ' ' + this.dialogModle.dialogForm.workCenter + this.$t('quality.spc.avgCtrl'),
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
              if (param.data > avg_ucl || param.data < avg_lcl) {
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
      // option.visualMap.pieces[0] = {gte: avg_ucl, color: 'red'};
      // option.visualMap.pieces[1] = {lte: avg_lcl, color: 'red'};
      let Xbar = echarts.init(document.getElementById('Xbar'))
      Xbar.setOption(option)
    },
    // 生成极差控制图
    setRangeValueImg (data, Xtime, range_cl, range_ucl, range_lcl) {
      let datas = [...data, range_cl, range_ucl, range_lcl]
      let max = Math.max.apply(null, datas)
      let min = Math.min.apply(null, datas)
      let interval = (max - min) / 5
      let option = {
        title: {},
        xAxis: {},
        yAxis: {},
        label: {},
        tooltip: {},
        series: []
      }
      option.title = {
        text: this.dialogModle.dialogForm.lineCode + ' ' + this.dialogModle.dialogForm.workCenter + this.$t('quality.spc.tangeValueImgCtrl'),
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
        min: (min - interval) < 0 ? 0 : (min - interval),
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
              if (param.data > range_ucl || param.data < range_lcl) {
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
      let Rbar = echarts.init(document.getElementById('Rbar'))
      Rbar.setOption(option)
    },
    // 计算数据初始化
    setInputCalculate (inputCalculate = []) {
      let maxValues = { data0: this.$t('qualitySynergy.specValueMax') }
      inputCalculate.maxValues.map((item, indx) => {
        maxValues['data' + (indx + 1)] = item
      })
      let minValues = { data0: this.$t('qualitySynergy.specValueMin') }
      inputCalculate.minValues.map((item, indx) => {
        minValues['data' + (indx + 1)] = item
      })
      let avgValues = { data0: this.$t('quality.spc.avg') }
      inputCalculate.avgValues.map((item, indx) => {
        avgValues['data' + (indx + 1)] = item.toFixed(3)
      })
      let specimenValues = { data0: this.$t('quality.spc.subGroupSize') }
      inputCalculate.specimenValues.map((item, indx) => {
        specimenValues['data' + (indx + 1)] = item
      })
      let innelGroupStandardDeviationsList = { data0: this.$t('quality.spc.innelGroupStandardDeviations') }
      inputCalculate.innelGroupStandardDeviationsList.map((item, indx) => {
        innelGroupStandardDeviationsList['data' + (indx + 1)] = item.toFixed(3)
      })
      let wholeStandardDeviationList = { data0: this.$t('quality.zhengtibiaozhuncha') }
      inputCalculate.wholeStandardDeviationList.filter(item => item).map((item, indx) => {
        wholeStandardDeviationList['data' + (indx + 1)] = item.toFixed(3)
      })
      let rangeValue = { data0: this.$t('quality.spc.rangeValue') }
      inputCalculate.rangeValue.map((item, indx) => {
        rangeValue['data' + (indx + 1)] = item.toFixed(3)
      })
      this.spcPageData.inputCalculate = []
      this.spcPageData.inputCalculate.push(maxValues)
      this.spcPageData.inputCalculate.push(minValues)
      this.spcPageData.inputCalculate.push(avgValues)
      this.spcPageData.inputCalculate.push(specimenValues)
      this.spcPageData.inputCalculate.push(innelGroupStandardDeviationsList)
      this.spcPageData.inputCalculate.push(wholeStandardDeviationList)
      this.spcPageData.inputCalculate.push(rangeValue)
      this.spcPageData.maxGroupAvg = inputCalculate.maxGroupAvg ? inputCalculate.maxGroupAvg.toFixed(3) : inputCalculate.maxGroupAvg
      this.spcPageData.minGroupAvg = inputCalculate.minGroupAvg ? inputCalculate.minGroupAvg.toFixed(3) : inputCalculate.minGroupAvg
      this.spcPageData.subGroupCount = inputCalculate.subGroupCount
      this.spcPageData.cp = inputCalculate.cp ? inputCalculate.cp.toFixed(3) : inputCalculate.cp
      this.spcPageData.cpl = inputCalculate.cpl ? inputCalculate.cpl.toFixed(3) : inputCalculate.cpl
      this.spcPageData.cpu = inputCalculate.cpu ? inputCalculate.cpu.toFixed(3) : inputCalculate.cpu
      this.spcPageData.cpk = inputCalculate.cpk ? inputCalculate.cpk.toFixed(3) : inputCalculate.cpk
      this.spcPageData.pp = inputCalculate.pp ? inputCalculate.pp.toFixed(3) : inputCalculate.pp
      this.spcPageData.ppl = inputCalculate.ppl ? inputCalculate.ppl.toFixed(3) : inputCalculate.ppl
      this.spcPageData.ppu = inputCalculate.ppu ? inputCalculate.ppu.toFixed(3) : inputCalculate.ppu
      this.spcPageData.ppk = inputCalculate.ppk ? inputCalculate.ppk.toFixed(3) : inputCalculate.ppk
    },
    // 提交按钮
    submitOne () {
      if (!this.modelData.sampleRule) return this.$message.warning(this.$t('quality.spc.fillSampleRule'))
      let measureData = []
      this.dialogModle.dialogForm.measureDataList.map(item => {
        measureData.push(item.data)
      })
      this.dialogModle.dialogForm.measureData = measureData.join(',')
      this.dialogModle.dialogForm.examinedDate = parseTime(new Date())
      let transformParams = transformMQL.save('spcDataInput', [this.dialogModle.dialogForm], 'add')
      this.$refs.spcEnterForm.validate((valid) => {
        if (valid) {
          if (measureData.length >= 2 && this.modelData.sampleRule !== 'I-MR') {
            let measureFlag = false
            // 测量值录入的值不能完全一致
            for (let item of measureData) {
              if (item !== measureData[0]) {
                measureFlag = true
                break
              }
            }
            if (!measureFlag) {
              this.$message.info(this.$t('quality.testValueEnterLimit'))
              return
            }
          }
          spcInputAdd(transformParams).then(response => {
            this.$message({
              message: this.$t('common.successSubmit'),
              type: 'success'
            })
            this.$emit('tab-remove', this.$attrs.tabName)
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    }
  }
}
</script>
<style scoped lang="scss">
.enter-container {
    border: 1px solid #efefef;
    padding: 10px;
    overflow: auto;
}
.enter-data {
  border: 1px solid #efefef;
  padding: 10px;
  .enter-title {
    font-size: 14px;
    margin-bottom: 10px;
  }
  .enter-label {
    text-align: center;
  }
  .enter-input {
    margin: 10px 0;
    text-align: center;
  }
}
.Xbar,.Rbar, .NDbar {
  width: 100%;
  height: 400px;
  padding: 20px 0 0;
  border: 1px solid #efefef;
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
</style>
<style lang="scss">
.form-incontainer .enter-datas {
  .srm-row .el-form-item {
    &:first-child {
      margin-bottom: 12px;
      padding-left: 0;
      padding-right: 0;
    }
  }

}
</style>
