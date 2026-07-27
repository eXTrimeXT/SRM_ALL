<template>
  <el-container class="flex-container the_quick_list__outter_wrapper" direction="vertical">
    <el-main>
      <div class="enter-container CommonRules">
        <srm-row :gutter="20">
          <srm-col :initCol="2">
            <MainHeader>
              <template slot="left">
                -抽样规则：
              </template>
            </MainHeader>
            <div class="line-box col-1">
              <srm-row>
                <srm-col :initCol="3">
                  <el-radio-group v-model="rules.sample_rule" class="col-left-1" @change="changeSampleRule">
                    <el-radio :disabled="readOnly" label="Xbar-R">
                      常规分组
                    </el-radio>
                    <el-radio :disabled="readOnly" label="I-MR">
                      单值统计
                    </el-radio>
                  </el-radio-group>
                </srm-col>
                <srm-col :initCol="3">
                  <div class="col-leftCheck-1">
                    <el-checkbox v-model="rules.return_sub_group_size" :disabled="readOnly " label="样本数量" />
                    <el-checkbox v-model="rules.return_sub_group_count" :disabled="readOnly" label="样本组" />
                    <el-checkbox v-model="rules.return_value_count" :disabled="readOnly" label="返回记录数" />
                  </div>
                </srm-col>
                <srm-col :initCol="3">
                  <div class="col-leftCheck-1">
                    <el-input v-model="rules.sub_group_size" :disabled="readOnly || rules.sample_rule === 'I-MR' " />
                    <el-input v-model="rules.sub_group_count" :disabled="readOnly" />
                    <el-input v-model="rules.value_count" :disabled="readOnly" />
                  </div>
                </srm-col>
              </srm-row>
            </div>
          </srm-col>
          <srm-col :initCol="2">
            <MainHeader>
              <template slot="left">
                -样本组取样方式：
              </template>
            </MainHeader>
            <div class="line-box col-1">
              <el-radio-group v-model="rules.get_sample_style" class="col-right-1">
                <el-radio :disabled="readOnly" label="1">
                  区间全部数据记录
                </el-radio>
                <el-radio :disabled="readOnly" label="2">
                  区间首部N条记录
                </el-radio>
                <el-radio :disabled="readOnly" label="3">
                  区间尾部N条记录
                </el-radio>
                <el-radio :disabled="readOnly" label="4">
                  区间随机N条记录
                </el-radio>
              </el-radio-group>
            </div>
          </srm-col>
          <srm-col :initCol="2">
            <MainHeader>
              <template slot="left">
                -控制图判异规则：
              </template>
            </MainHeader>
            <div class="line-box col-2">
              <srm-row>
                <srm-col :initCol="2">
                  <div class="col-left-2">
                    <el-checkbox v-model="rules.lt_oc_lower_limit" :disabled="readOnly" label="小于外控下限" />
                    <el-checkbox v-model="rules.gt_oc_upper_limit" :disabled="readOnly" label="大于外控上限" />
                    <el-checkbox v-model="rules.keep_increasing" :disabled="readOnly" label="连续保持递增" />
                    <el-checkbox v-model="rules.keep_decline" :disabled="readOnly" label="连续保持递减" />
                    <el-checkbox v-model="rules.keep_center_up" :disabled="readOnly" label="连续保持在中心线上侧" />
                    <el-checkbox v-model="rules.keep_center_down" :disabled="readOnly" label="连续保持在中心线下侧" />
                  </div>
                </srm-col>
                <srm-col :initCol="2">
                  <div class="col-leftInput-2">
                    <el-input v-model="rules.lt_oc_lower_limit_value" :disabled="readOnly" />
                    <el-input v-model="rules.keep_increasing_value" :disabled="readOnly" />
                    <el-input v-model="rules.keep_center_up_value" :disabled="readOnly" />
                  </div>
                </srm-col>
              </srm-row>
            </div>
          </srm-col>
          <srm-col :initCol="2">
            <MainHeader>
              <template slot="left">
                -附加判异规则：
              </template>
            </MainHeader>
            <div class="line-box col-2">
              <div class="col-right-2">
                <el-checkbox v-model="rules.same_side_b" :disabled="readOnly" label="连续3点中有2点落在中心线同一侧的B区以外" />
                <el-checkbox v-model="rules.same_side_c" :disabled="readOnly" label="连续5点中有4点落在中心线同一侧的C区以外" />
                <el-checkbox v-model="rules.alternation_of_up_and_down" :disabled="readOnly" label="点上下交替" />
                <el-checkbox v-model="rules.continuity_c_inside" :disabled="readOnly" label="连续点C区内" />
                <el-checkbox v-model="rules.continuity_c_outside" :disabled="readOnly" label="连续点C区外" />
                <el-checkbox v-model="rules.continuity_zero" :disabled="readOnly" label="变差连续为零" />
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
                -子组内标准差估值方法：
              </template>
            </MainHeader>
            <div class="line-box col-3">
              <div class="col-top-3">
                子组大小＞1
              </div>
              <el-radio-group v-model="rules.stddev_type_in_group" class="col-left-3">
                <el-radio :disabled="readOnly" label="1">
                  合并标准差
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
                -停线设置：
              </template>
            </MainHeader>
            <div class="line-box col-4">
              <srm-row>
                <srm-col :initCol="2">
                  <div class="col-left-4">
                    <el-checkbox v-model="rules.cpk_up_limit_stopper" :disabled="readOnly" label="CPK值小于" />
                    <el-checkbox v-model="rules.ppm_lower_limit_stopper" :disabled="readOnly" label="PPM值大于" />
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
      </div>
    </el-main>
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
      spcRuleData: {}
    }
  },
  computed: {
    readOnly: () => {
      return true
    }
  },
  mounted () {
    let params = this.$attrs.params.param
    this.getRules(params)
  },
  methods: {
    changeSampleRule (val) {
      console.log('val', val)
      if (val === 'I-MR') this.rules.sub_group_size = 1
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
        this.spcRuleData = response.data.records[0]
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
