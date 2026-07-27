<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <div class="main-padding">
        <el-form
          :model="allParams"
          disabled
          label-width="80px"
        >
          <div class="surveyTitle">
            {{ allParams.surveyTitle }}
          </div>
          <div class="mg-l-16">
            <el-row
              :gutter="50"
            >
              <el-col :span="8">
                <div class="mg-l-header">
                  {{ $t('surveyAnswer.vendorName') }}<strong>{{ allParams.vendorName }}</strong>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="mg-l-header">
                  {{ $t('surveyAnswer.resultFlay') }}<strong>{{ resultFlagFormat(allParams.resultFlag) }}</strong>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="mg-l-header">
                  {{ $t('surveyAnswer.lastUpdate') }}<strong>{{ $parseTime(allParams.lastUpdateDate) }}</strong>
                </div>
              </el-col>
            </el-row>
            <div class="question-list">
              <el-row
                v-for="(item,index) in allParams.surveyQuestionDTOList"
                :key="index+'pp'"
                class="question-listBOx"
              >
                <el-col :span="24">
                  <div>
                    <span v-if="item.surveyQuestion.emptyFlag === 'Y'" style="color:red;margin-right:6px;">*</span>
                    <span style="padding-right: 10px;">{{ index+1 }}.</span>
                    <span>{{ item.surveyQuestion.questionName }}（{{ item.surveyQuestion.questionType | typeFormat }}）</span>
                  </div>
                  <template v-if="item.surveyQuestion.employeeFlag==='Y'">
                    <el-row
                      v-for="(el,number) in item.jobEmployeeDtoList"
                      :key="number+'dd'"
                      class="question-listBOx"
                    >
                      <template v-if="el.employeeResultDtoList && el.employeeResultDtoList.length>0">
                        <el-col
                          v-for="(elm,num) in el.employeeResultDtoList"
                          :key="num+'ff'"
                          :span="24"
                          class="question-listBOx"
                        >
                          <!-- 用户有填写的情况 -->
                          <template v-if="elm.surveyResultDtoList && elm.surveyResultDtoList.length>0">
                            <div>
                              <span>{{ $t('surveyAnswer.employeeName') }}<strong>{{ elm.employeeName }}</strong></span>
                              <span>-</span>
                              <span>{{ $t('surveyAnswer.employeeJob') }}<strong>{{ elm.employeeJob }}</strong></span>
                            </div>

                            <el-radio-group
                              v-if="item.surveyQuestion.questionType=='S'"
                              v-model="elm.surveyResultDtoList[0].resultValue"
                              class="mg-l-20"
                              @change="checkAddType"
                            >
                              <el-row>
                                <el-col
                                  v-for="itm in item.surveySelectionList"
                                  :key="itm.selectionCode+'ii'"
                                >
                                  <el-radio :label="itm.selectionCode">
                                    {{ itm.selectionCode }} {{ itm.selectionValue }}
                                  </el-radio>
                                </el-col>
                              </el-row>
                            </el-radio-group>
                            <div
                              v-if="item.surveyQuestion.questionType=='M'"
                              class="mg-l-20"
                            >
                              <div style="color:red;">
                                {{ item.surveyQuestion.maxSelection | maxSelectionFormat }}
                              </div>
                              <el-checkbox-group
                                v-model="elm.surveyResultDtoListMap"
                                :max="item.surveyQuestion.maxSelection==1?1000:item.surveyQuestion.maxSelection"
                                @change="checkAddType"
                              >
                                <el-row>
                                  <el-col
                                    v-for="itm in item.surveySelectionList"
                                    :key="itm.selectionCode+'i2'"
                                  >
                                    <el-checkbox :label="itm.selectionCode">
                                      {{ itm.selectionCode }} {{ itm.selectionValue }}
                                    </el-checkbox>
                                  </el-col>
                                </el-row>
                              </el-checkbox-group>
                            </div>
                            <div
                              v-if="item.surveyQuestion.questionType=='Q'"
                              class="mg-l-20 listBOx-q"
                            >
                              <div>{{ $t('dashboard.answer') }}</div>
                              <el-input
                                v-model="elm.surveyResultDtoList[0].resultValue"
                                type="textarea"
                                width="60%"
                                :rows="2"
                                @input="checkAddType"
                              />
                            </div>
                          </template>

                          <!-- 没填写的情况下 -->
                          <template v-else>
                            <div>
                              <span>{{ $t('surveyAnswer.employeeName') }}<strong>{{ elm.employeeName }}</strong></span>
                              <span>-</span>
                              <span>{{ $t('surveyAnswer.employeeJob') }}<strong>{{ elm.employeeJob }}</strong></span>
                            </div>

                            <el-radio-group
                              v-if="item.surveyQuestion.questionType=='S'"
                              v-model="elm.resultValue"
                              class="mg-l-20"
                              @change="checkAddType"
                            >
                              <el-row>
                                <el-col
                                  v-for="itm in item.surveySelectionList"
                                  :key="itm.selectionCode+'ii'"
                                >
                                  <el-radio :label="itm.selectionCode">
                                    {{ itm.selectionCode }} {{ itm.selectionValue }}
                                  </el-radio>
                                </el-col>
                              </el-row>
                            </el-radio-group>
                            <div
                              v-if="item.surveyQuestion.questionType=='M'"
                              class="mg-l-20"
                            >
                              <div style="color:red;">
                                {{ item.surveyQuestion.maxSelection | maxSelectionFormat }}
                              </div>
                              <el-checkbox-group
                                v-model="resultValue"
                                :max="item.surveyQuestion.maxSelection==1?1000:item.surveyQuestion.maxSelection"
                                @change="checkAddType"
                              >
                                <el-row>
                                  <el-col
                                    v-for="itm in item.surveySelectionList"
                                    :key="itm.selectionCode+'i2'"
                                  >
                                    <el-checkbox :label="itm.selectionCode">
                                      {{ itm.selectionCode }} {{ itm.selectionValue }}
                                    </el-checkbox>
                                  </el-col>
                                </el-row>
                              </el-checkbox-group>
                            </div>
                            <div
                              v-if="item.surveyQuestion.questionType=='Q'"
                              class="mg-l-20 listBOx-q"
                            >
                              <div>{{ $t('dashboard.answer') }}</div>
                              <el-input
                                v-model="elm.resultValue"
                                type="textarea"
                                width="60%"
                                :rows="2"
                                @input="checkAddType"
                              />
                            </div>
                          </template>
                        </el-col>
                      </template>
                    </el-row>
                  </template>
                  <template v-else>
                    <el-radio-group
                      v-if="item.surveyQuestion.questionType=='S'"
                      v-model="item.resultValue"
                      class="mg-l-20"
                      @change="checkAddType"
                    >
                      <el-row>
                        <el-col
                          v-for="itm in item.surveySelectionList"
                          :key="itm.selectionCode+'ii'"
                        >
                          <el-radio :label="itm.selectionCode">
                            {{ itm.selectionCode }} {{ itm.selectionValue }}
                          </el-radio>
                          <el-input v-if="itm.descriptionFlag === 'Y'" v-model="itm.description" style="margin-bottom:10px;" type="textarea" />
                        </el-col>
                      </el-row>
                    </el-radio-group>
                    <div
                      v-if="item.surveyQuestion.questionType=='M'"
                      class="mg-l-20"
                    >
                      <div style="color:red;">
                        {{ item.surveyQuestion.maxSelection | maxSelectionFormat }}
                      </div>
                      <el-checkbox-group
                        v-model="item.resultValue"
                        :max="item.surveyQuestion.maxSelection==1?1000:item.surveyQuestion.maxSelection"
                        @change="checkAddType"
                      >
                        <el-row>
                          <el-col
                            v-for="itm in item.surveySelectionList"
                            :key="itm.selectionCode+'i2'"
                          >
                            <el-checkbox :label="itm.selectionCode">
                              {{ itm.selectionCode }} {{ itm.selectionValue }}
                            </el-checkbox>
                          </el-col>
                        </el-row>
                      </el-checkbox-group>
                    </div>
                    <div
                      v-if="item.surveyQuestion.questionType=='Q'"
                      class="mg-l-20 listBOx-q"
                    >
                      <div>{{ $t('dashboard.answer') }}</div>
                      <el-input
                        v-model="item.resultValue"
                        type="textarea"
                        width="60%"
                        :rows="2"
                        @input="checkAddType"
                      />
                    </div>
                  </template>
                </el-col>
              </el-row>
            </div>
          </div>
        </el-form>

        <CToolbar>
          <template slot="right">
            <el-button @click="backTo">
              {{ $t('vendorMod.goBack') }}
            </el-button>
          </template>
        </CToolbar>
      </div>
    </el-main>
  </el-container>
</template>

<script>
import CToolbar from 'lib@/components/c-toolbar'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { getDictItemList } from '@/api/common'
import { adaptDictData } from '@/utils'

export default {
  name: 'SurveyAnswer',

  components: { CToolbar },

  filters: {
    typeFormat (type) {
      if (type == 'S') {
        return '单选题'
      }
      if (type == 'M') {
        // '多选题'
        return this.$t('dashboard.multipleChoice')
      }
      if (type == 'Q') {
        // '问答题'
        return this.$t('dashboard.essayQuestion')
      }
    },
    employeeFormat (data) {
      if (data == 'Y') {
        // '员工调查'
        return this.$t('dashboard.employeeSurveys')
      } else {
        // '非员工调查'
        return this.$t('dashboard.notEmployeeSurvey')
      }
    },
    maxSelectionFormat (data) {
      if (data == 1) {
        // '不控制'
        return this.$t('dashboard.noControl')
      } else {
        // return `最多可选${data}项`
        return `${this.$t('cusEntry.supplement20250211.maxOptional')}${data}${this.$t('cusEntry.supplement20250211.itemName')}`
      }
    }
  },

  mixins: [tabTodoWatch, tabTodoMixin],

  data () {
    return {
      allParams: {},
      resultFlagList: [],
      resultValue: [],
      surveyId: ''
    }
  },

  mounted () {
    this.fatchDictData()
    this.surveyId = this.$attrs.params.surveyId || ''
    this.getDetails(this.surveyId)
  },

  methods: {
    // 获取数据字典
    fatchDictData () {
      // 批量查询字典
      let dictParamsArr = [
        { dictCode: 'RESULT_FLAG' } // 反馈结果
      ]
      getDictItemList(dictParamsArr).then(res => {
        const [RESULT_FLAG] = res.data
        this.resultFlagList = adaptDictData(RESULT_FLAG.RESULT_FLAG)
      })
    },

    resultFlagFormat (data) {
      if (data) {
        let targe = this.resultFlagList.find(item => item.value == data) || {}
        return targe.label || ''
      }
      return ''
    },

    checkAddType () {
      this.$forceUpdate()
    },

    getDetails (id) {
      this.$http({
        url: '/api-base/basesupplier/surveyquestionsupplier/questionSurveyInfo',
        method: 'post',
        params: {
          id,
          vendorCode: this.$attrs.params.row.vendorCode || ''
        }
      })
        .then(data => {
          if (data) {
            this.allParams = data.data.surveyScopeVendorSupplierDto
            this.allParams.surveyQuestionDTOList = data.data.surveyQuestionSupplierDTOList || []

            // 员工调查 多选情况下取值
            this.allParams.surveyQuestionDTOList.map(item => {
              if (item.surveyQuestion.employeeFlag === 'Y' && item.surveyQuestion.questionType === 'M' && item.jobEmployeeDtoList && item.jobEmployeeDtoList.length > 0) {
                item.jobEmployeeDtoList.map(elm => {
                  if (elm.employeeResultDtoList && elm.employeeResultDtoList.length > 0) {
                    elm.employeeResultDtoList.map(el => {
                      if (el.surveyResultDtoList && el.surveyResultDtoList.length > 0) {
                        el.surveyResultDtoListMap = el.surveyResultDtoList.map(v => v.resultValue)
                      }
                    })
                  }
                })
              }
            })

            // 非员工调查 多选情况下取值
            if (this.allParams.resultFlag == 'Y') { // 已反馈
              this.allParams.surveyQuestionDTOList.map(item => {
                if (item.surveyQuestion.questionType == 'M') {
                  item.resultValue = []
                  item.surveyResultDtoList.map(itm => {
                    item.resultValue.push(itm.resultValue)
                  })
                } else {
                  item.resultValue = item.surveyResultDtoList[0] ? item.surveyResultDtoList[0].resultValue : null
                }
              })
            } else {
              this.allParams.surveyQuestionDTOList.map(item => {
                if (item.surveyQuestion.questionType == 'M') {
                  item.resultValue = []
                } else {
                  item.resultValue = null
                }
              })
            }
          }
        })
    },

    submit () {
      let flag = false
      this.allParams.surveyQuestionDTOList.map(item => {
        if (item.surveyQuestion.questionType == 'M') {
          flag = !item.resultValue.length
        } else {
          flag = !item.resultValue
        }
      })
      if (flag) {
        this.$message({
          message: this.$t('cusEntry.supplement20250211.answerAllQuestions'),  // '请答完所有题目'
          type: 'error'
        })
        return
      }
      let params = []
      this.allParams.surveyQuestionDTOList.map(item => {
        item.surveySelectionList.map(itm => {
          let obj = {}
          if (item.surveyQuestion.questionType == 'M') {
            if (item.resultValue.some(i => i == itm.selectionCode)) {
              obj.selectionId = itm.selectionId
              obj.vendorScopeId = this.allParams.vendorScopeId
              obj.resultValue = itm.selectionCode
              obj.questionId = itm.questionId
              obj.employeeScopeId = null
              params.push(obj)
            }
          } else if (item.surveyQuestion.questionType == 'Q') {
            obj.selectionId = itm.selectionId
            obj.vendorScopeId = this.allParams.vendorScopeId
            obj.resultValue = item.resultValue
            obj.questionId = itm.questionId
            obj.employeeScopeId = null
            params.push(obj)
          } else {
            if (item.resultValue == itm.selectionCode) {
              obj.selectionId = itm.selectionId
              obj.vendorScopeId = this.allParams.vendorScopeId
              obj.resultValue = item.resultValue
              obj.questionId = itm.questionId
              obj.employeeScopeId = null
              params.push(obj)
            }
          }
        })
      })
      this.$http({
        url: '/api-base/basesupplier/surveyresultsupplier/save',
        method: 'post',
        data: params
      })
        .then(data => {
          if (data) {
            this.$message({
              message: data.message,
              type: 'success'
            })
            this.$emit(
              'tab-remove',
              this.$attrs.params.tabName
            )
          }
        })
    },

    backTo () {
      this.$emit(
        'tab-remove',
        this.$attrs.params.tabName
      )
    }
  }
}
</script>

<style lang="scss" scoped>
.main-padding {
  padding: 15px;
  :deep(.el-form-item__label){
    text-align: left;
  }
}
.surveyTitle {
  height: 48px;
  line-height: 48px;
  text-align: center;
  font-size: 14px;
  color: #1c2438;
  font-weight: 700;
}
.question-list {
  margin-top: 20px;
  padding-bottom: 30px;
  border-top: 1px solid #e6e9ec;
}
.question-listBOx {
  border-bottom: 1px solid #e6e9ec;
  padding: 16px 10px 20px;
  font-size: 14px;
  line-height: 30px;
  :deep(.el-radio-group){
    line-height: 30px;
  }
  :deep(.el-textarea){
    width: 90%;
  }
  :deep(.el-radio__input.is-disabled+span.el-radio__label){
    color: #606266;
  }
  :deep(.el-button){
    min-width: 40px;
    padding: 0;
    text-align: right;
  }
}
.listBOx-q {
  display: flex;
  align-items: center;
}
.mg-l-16 {
  padding: 16px;
}
.mg-l-20 {
  padding-left: 20px;
}
.mg-l-header {
  font-size: 12px;
  color: #606266;
  height: 36px;
}
</style>
