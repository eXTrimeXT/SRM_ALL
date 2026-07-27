<template>
  <el-container class="flex-container the_quick_list__outter_wrapper" direction="vertical">
    <el-main>
      <div class="enter-container CommonRules">
        <el-form ref="form" :rules="ruleList" :model="rules">
          <srm-row :gutter="20">
            <srm-col :initCol="2">
              <MainHeader>
                <template slot="left">
                  {{ $t('quality.project.sampleRule') }}
                </template>
              </MainHeader>
              <div class="line-box col-1">
                <srm-row>
                  <srm-col :initCol="3">
                    <el-radio-group v-model="rules.sample_rule" class="col-left-1" @change="changeSampleRule">
                      <el-radio :disabled="readOnly" label="Xbar-R">
                        {{ $t('quality.project.normalGroup') }}
                      </el-radio>
                      <el-radio :disabled="readOnly" label="I-MR">
                        {{ $t('quality.project.danzhitongji') }}
                      </el-radio>
                    </el-radio-group>
                  </srm-col>
                  <srm-col :initCol="3">
                    <div class="col-leftCheck-1">
                      <el-checkbox v-model="rules.return_sub_group_size" :disabled="readOnly " :label="$t('quality.project.sampleNum')" />
                      <el-checkbox v-model="rules.return_sub_group_count" :disabled="readOnly" :label="$t('quality.project.sampleGroup')" />
                      <el-checkbox v-model="rules.return_value_count" :disabled="readOnly" :label="$t('quality.project.backLog')" />
                    </div>
                  </srm-col>
                  <srm-col :initCol="3">
                    <div class="col-leftCheck-1">
                      <el-form-item prop="sub_group_size">
                        <el-input v-model="rules.sub_group_size" :disabled="readOnly || rules.sample_rule === 'I-MR' " />
                      </el-form-item>
                      <el-form-item prop="sub_group_count">
                        <el-input v-model="rules.sub_group_count" :disabled="readOnly" />
                      </el-form-item>
                      <el-form-item prop="value_count">
                        <el-input v-model="rules.value_count" :disabled="readOnly" />
                      </el-form-item>
                    </div>
                  </srm-col>
                </srm-row>
              </div>
            </srm-col>
            <srm-col :initCol="2">
              <MainHeader>
                <template slot="left">
                  {{ $t('quality.project.getSampleMenthod') }}
                </template>
              </MainHeader>
              <div class="line-box col-1">
                <el-radio-group v-model="rules.get_sample_style" class="col-right-1">
                  <el-radio :disabled="readOnly" label="1">
                    {{ $t('quality.project.qujianquanbujilu') }}
                  </el-radio>
                  <el-radio :disabled="readOnly" label="2">
                    {{ $t('quality.project.qujianshoubujilu') }}
                  </el-radio>
                  <el-radio :disabled="readOnly" label="3">
                    {{ $t('quality.project.qujianweibujilu') }}
                  </el-radio>
                  <el-radio :disabled="readOnly" label="4">
                    {{ $t('quality.project.jujiansuijijilu') }}
                  </el-radio>
                </el-radio-group>
              </div>
            </srm-col>
            <srm-col :initCol="2">
              <MainHeader>
                <template slot="left">
                  {{ $t('quality.project.ctrlRule') }}
                </template>
              </MainHeader>
              <div class="line-box col-2">
                <srm-row>
                  <srm-col :initCol="2">
                    <div class="col-left-2">
                      <el-checkbox v-model="rules.lt_oc_lower_limit" :disabled="readOnly" :label="$t('quality.project.ocLowerLimit')" />
                      <el-checkbox v-model="rules.gt_oc_upper_limit" :disabled="readOnly" :label="$t('quality.project.ocUpperLimit')" />
                      <el-checkbox v-model="rules.keep_increasing" :disabled="readOnly" :label="$t('quality.project.keepIncresing')" />
                      <el-checkbox v-model="rules.keep_decline" :disabled="readOnly" :label="$t('quality.project.keepDecline')" />
                      <el-checkbox v-model="rules.keep_center_up" :disabled="readOnly" :label="$t('quality.project.keepCenterUp')" />
                      <el-checkbox v-model="rules.keep_center_down" :disabled="readOnly" :label="$t('quality.project.keepCenterDown')" />
                    </div>
                  </srm-col>
                  <srm-col :initCol="2">
                    <div class="col-leftInput-2">
                      <el-form-item prop="lt_oc_lower_limit_value">
                        <el-input v-model="rules.lt_oc_lower_limit_value" :disabled="readOnly" />
                      </el-form-item>
                      <el-form-item prop="keep_increasing_value">
                        <el-input v-model="rules.keep_increasing_value" :disabled="readOnly" />
                      </el-form-item>
                      <el-form-item prop="keep_center_up_value">
                        <el-input v-model="rules.keep_center_up_value" :disabled="readOnly" />
                      </el-form-item>
                    </div>
                  </srm-col>
                </srm-row>
              </div>
            </srm-col>
            <srm-col :initCol="2">
              <MainHeader>
                <template slot="left">
                  {{ $t('quality.project.addOtherRule') }}
                </template>
              </MainHeader>
              <div class="line-box col-2">
                <div class="col-right-2">
                  <el-checkbox v-model="rules.same_side_b" :disabled="readOnly" :label="$t('quality.project.sameSideb')" />
                  <el-checkbox v-model="rules.same_side_c" :disabled="readOnly" :label="$t('quality.project.sameSadeC')" />
                  <el-checkbox v-model="rules.alternation_of_up_and_down" :disabled="readOnly" :label="$t('quality.project.upAndDown')" />
                  <el-checkbox v-model="rules.continuity_c_inside" :disabled="readOnly" :label="$t('quality.project.continuityCInside')" />
                  <el-checkbox v-model="rules.continuity_c_outside" :disabled="readOnly" :label="$t('quality.project.continuityCOutside')" />
                  <el-checkbox v-model="rules.continuity_zero" :disabled="readOnly" :label="$t('quality.project.continuityZero')" />
                </div>
                <div class="col-rightInput-2">
                  <el-input v-model="rules.alternation_of_up_and_down_value" :disabled="readOnly" />
                  <el-input v-model="rules.continuity_c_inside_value" :disabled="readOnly" />
                  <el-input v-model="rules.continuity_c_outside_value" :disabled="readOnly" />
                  <el-input v-model="rules.continuity_zero_value" :disabled="readOnly" />
                </div>
              </div>
            </srm-col>
            <srm-col :initCol="2">
              <MainHeader>
                <template slot="left">
                  {{ $t('quality.project.biaozhunguzhifangfa') }}
                </template>
              </MainHeader>
              <div class="line-box col-3">
                <div class="col-top-3">
                  {{ $t('quality.project.childCom') }}
                </div>
                <el-radio-group v-model="rules.stddev_type_in_group" class="col-left-3">
                  <el-radio :disabled="readOnly" label="1">
                    {{ $t('quality.project.hebingbiaozhuncha') }}
                  </el-radio>
                  <el-radio :disabled="readOnly" label="2">
                    Rbar（R）
                  </el-radio>
                  <el-radio :disabled="readOnly" label="3">
                    Sbar（S）
                  </el-radio>
                </el-radio-group>
              </div>
            </srm-col>
            <!-- <srm-col  :initCol="2">
            <MainHeader>
              <template slot="left">
                -规格上下限：
              </template>
            </MainHeader>
            <div class="line-box col-3">
              <srm-row>
                <srm-col :initCol="2">
                  <div class="col-right-3">
                    <div>规格上限</div>
                    <div>规格下限</div>
                    <div>目标值</div>
                  </div>
                </srm-col>
                <srm-col :initCol="2">
                  <div class="col-right-3">
                    <el-input v-model="rules.standardMax" :disabled="readOnly" />
                    <el-input v-model="rules.standardMin" :disabled="readOnly" />
                    <el-input v-model="rules.targetValue" :disabled="readOnly" />
                  </div>
                </srm-col>
              </srm-row>
            </div>
          </srm-col> -->
            <srm-col :initCol="2">
              <MainHeader>
                <template slot="left">
                  {{ $t('quality.project.tingxianshezhi') }}
                </template>
              </MainHeader>
              <div class="line-box col-4">
                <srm-row>
                  <srm-col :initCol="2">
                    <div class="col-left-4">
                      <el-checkbox v-model="rules.cpk_up_limit_stopper" :disabled="readOnly" :label="$t('quality.project.cpkxiaoyu')" />
                      <el-checkbox v-model="rules.ppm_lower_limit_stopper" :disabled="readOnly" :label="$t('quality.project.ppmzhidayu')" />
                    </div>
                  </srm-col>
                  <srm-col :initCol="2">
                    <div class="col-left-4">
                      <el-input v-model="rules.cpk_up_limit_stopper_value" :disabled="readOnly" />
                      <el-input v-model="rules.ppm_lower_limit_stopper_value" :disabled="readOnly" />
                    </div>
                  </srm-col>
                </srm-row>
              </div>
            </srm-col>
          </srm-row>
        </el-form>
      </div>
    </el-main>
    <CToolbar>
      <template slot="right">
        <!--返回-->
        <el-button type="ghost" @click="back">
          {{ $t('common.cancel') }}
        </el-button>
        <!--提交-->
        <el-button type="primary" @click="saveBill('SAVE')">
          {{ $t('common.save') }}
        </el-button>
      </template>
    </CToolbar>
  </el-container>
</template>
<script>
import MainHeader from 'lib@/components/Table/MainHeader'
import { tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import { qualityProject } from '@/modulesQa/buyer/qualitySynergy/api'
import { transformMQL } from '@/library/utils/util'
const { getMonitorSpcParam, saveMonitorSpcParam } = qualityProject

export default {
  components: {
    MainHeader, CToolbar
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      rules: {},
      ruleList: {
        sub_group_size: [
          { required: true, message: this.$t('common.pleaseInput') },
          { pattern: /^[1-9]\d*$/, message: this.$t('quality.project.fillUperZero'), trigger: 'blur' }
        ],
        sub_group_count: [
          { required: true, message: this.$t('common.pleaseInput') },
          { pattern: /^[1-9]\d*$/, message: this.$t('quality.project.fillUperZero'), trigger: 'blur' }
        ],
        value_count: [
          { required: true, message: this.$t('common.pleaseInput') },
          { pattern: /^[1-9]\d*$/, message: this.$t('quality.project.fillUperZero'), trigger: 'blur' }
        ],
        lt_oc_lower_limit_value: [
          { required: true, message: this.$t('common.pleaseInput') },
          { pattern: /^[1-9]\d*$/, message: this.$t('quality.project.fillUperZero'), trigger: 'blur' }
        ],
        keep_increasing_value: [
          { required: true, message: this.$t('common.pleaseInput') },
          { pattern: /^[1-9]\d*$/, message: this.$t('quality.project.fillUperZero'), trigger: 'blur' }
        ],
        keep_center_up_value: [
          { required: true, message: this.$t('common.pleaseInput') },
          { pattern: /^[1-9]\d*$/, message: this.$t('quality.project.fillUperZero'), trigger: 'blur' }
        ]
      },
      spcRuleData: {},
      existRule: null
    }
  },
  computed: {
    readOnly: () => {
      return false
    }
  },
  mounted () {
    let params = this.$attrs.params.param
    this.existRule = params.existRule
    this.getRules(params)
  },
  methods: {
    changeSampleRule (val) {
      console.log('val', val)
      if (val === 'I-MR') {
        this.$set(this.rules, 'sub_group_size', 1)
      } else {
        this.$set(this.rules, 'sub_group_size', 2)
      }
    },
    back () {
      let { tabName } = this.$attrs.params
      this.$emit('tab-remove', tabName)
      this.__setTabTodo('QualityProjectBuyerList.getQuerydata')
    },
    saveBill () {
      if (this.rules.sample_rule !== 'I-MR' && this.rules.sample_rule !== 'Xbar-R') return this.$message.warning(this.$t('quality.project.fillSampleRule'))
      if (this.rules.sample_rule === 'Xbar-R' && this.rules.sub_group_size < 2) return this.$message.warning(this.$t('quality.project.nomalGroupNumerLarge'))
      this.spcRuleData.rule = JSON.stringify(this.rules)
      this.spcRuleData.sampleRule = this.rules.sample_rule
      this.spcRuleData.ruleFlag = 'SPC_MONITOR_LIST'
      this.spcRuleData.state = 'A'
      this.spcRuleData.monitorListNo = this.$attrs.params.param.monitorListNo
      let params = this.$attrs.params.param
      this.spcRuleData.flagCondition = `${params.customerOrgId}|${params.workCenter}|${params.onLineEquipment}|${params.monitoringFeature}`
      this.$refs.form.validate(valid => {
        if (valid) {
          if (this.existRule === 'Y') {
            let transformParams = transformMQL.save('spcRuleParam', [this.spcRuleData], 'save')
            saveMonitorSpcParam(transformParams).then(response => {
              if (response) {
                this.$message.success(this.$t('common.success'))
                this.back()
              }
            })
          } else {
            const { id, ...rest } = this.spcRuleData
            let transformParams = transformMQL.save('spcRuleParam', [rest], 'save')
            saveMonitorSpcParam(transformParams).then(response => {
              if (response) {
                this.$message.success(this.$t('common.success'))
                this.back()
              }
            })
          }
        }
      })
    },
    getRules (params) {
      let payload = {
        'filter': {
          'monitorListNo': {
            'contains': params.monitorListNo
          }
        },
        'page': {
          'sort': 'lastUpdateDate desc'
        }
      }
      let transformParams = transformMQL.save('spcRuleParam', payload, 'query')
      getMonitorSpcParam(transformParams).then(response => {
        this.spcRuleData = response.data.records[0] || {}
        this.rules = JSON.parse(this.spcRuleData.rule)
        // this.$set(this.rules, 'standardMax', this.spcRuleData.standardMax)
        // this.$set(this.rules, 'standardMin', this.spcRuleData.standardMin)
        // this.$set(this.rules, 'targetValue', this.spcRuleData.targetValue)
      })
    }
  }
}
</script>
<style scoped lang="scss">
.enter-container {
  border: 1px solid #efefef;
  padding: 10px 10px 50px 10px;
  overflow: auto;
}
.line-box {
  border: 1px solid #efefef;
  padding: 10px;
  margin-bottom: 10px;
}
.col-1, .col-3 {
  height: 180px;
}
.col-right-1, .col-left-1, .col-leftCheck-1, .col-left-2, .col-right-2, .col-left-3, .col-left-4 {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.col-right-1, .col-left-1 {
  height: 160px;
}
.col-leftCheck-1 {
  height: 120px;
}
.col-2 {
  height: 260px;
  position: relative;
}
.col-left-2, .col-right-2 {
  height: 240px;
}
.col-leftInput-2 {
  height: 240px;
  display: flex;
  flex-direction: column;
  justify-content: space-around;
}
.col-rightInput-2 {
  width: 50%;
  height: 160px;
  position: absolute;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  justify-content: space-around;
}
.col-top-3 {
  line-height: 40px;
  text-align: center;
  color: #606266;
  font-size: 14px;
}
.col-left-3 {
  height: 120px;
}
.col-right-3 {
  height: 160px;
  color: #606266;
  font-size: 14px;
  display: flex;
  flex-direction: column;
  justify-content: space-around;
}
.col-left-4 {
  height: 80px;
}
</style>
<style>
.line-box .el-input__inner {
  text-align: center;
}
.CommonRules .el-input.is-disabled .el-input__inner {
  color: #606266;
  background-color: #fff;
}
.CommonRules .el-checkbox__input.is-disabled+span.el-checkbox__label {
  color: #606266;
}
.CommonRules .el-checkbox__input.is-disabled.is-checked .el-checkbox__inner {
  border: 1px solid #606266;
  background-color: #fff;
}
.CommonRules .el-checkbox__input.is-disabled .el-checkbox__inner {
  border: 1px solid #606266;
  background-color: #fff;
}
.CommonRules .el-radio__input.is-disabled.is-checked .el-radio__inner {
  border-color: #606266;
  background-color: #fff;
}
.CommonRules .el-checkbox__input.is-disabled.is-checked .el-checkbox__inner::after {
  border-color: #606266;
}
.CommonRules .el-radio__input.is-disabled+span.el-radio__label {
  color: #606266;
}
.CommonRules .el-radio__input.is-disabled .el-radio__inner {
  border-color: #606266;
  background-color: #fff;
}
.CommonRules .el-radio__input.is-disabled.is-checked .el-radio__inner::after {
  background-color: #606266;
}
</style>
