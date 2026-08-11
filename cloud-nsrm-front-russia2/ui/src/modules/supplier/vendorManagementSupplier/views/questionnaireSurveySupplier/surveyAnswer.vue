<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <div class="main-padding">
        <el-form
          :model="allParams"
          :disabled="allParams.resultFlag !== 'N' || $attrs.params.flag === 'view'"
          label-width="80px"
        >
          <div class="surveyTitle">
            {{ allParams.surveyTitle }}
          </div>
          <div class="mg-l-16">
            <el-row :gutter="32">
              <el-col :span="8">
                <div class="mg-l-header">
                  {{ $t('common.companyName') }}: <strong>{{ allParams.vendorName }}</strong>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="mg-l-header">
                  {{ $t('dashboard.loopMode') }}: <strong>{{ resultFlagFormat(allParams.resultFlag) }}</strong>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="mg-l-header">
                  {{ $t('perfMod.vFeedbackTime') }}: <strong>{{ $parseTime(allParams.lastUpdateDate) }}</strong>
                </div>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="24" style="margin-bottom:20px;">
                {{ $t('bidMod.fileInfo') }}: 
                <FileDynamic
                  ref="buyerSceneAttachment"
                  v-model="allParams.surveyHeaderFileList"
                  scene-module-code="SCENE_SURVEY_HEADER_ATTACHMENT"
                  :business-id="buyerBusinessId"
                  :editable="false"
                />
              </el-col>
              <el-col :span="24">
                {{ $t('components.upload.fileUpload') }}: 
                <FileDynamic
                  ref="supplierSceneAttachment"
                  v-model="surveyVendorFileList"
                  scene-module-code="SCENE_SURVEY_RESULT_SUP_ATTACHMENT"
                  :business-id="supplierBusinessId"
                  :editable="$attrs.params.flag === 'edit'"
                />
              </el-col>
            </el-row>
            <div class="question-list">
              <el-row
                v-for="(item, index) in surveyQuestionDTOList"
                :key="index + 'pp'"
                class="question-listBOx"
              >
                <el-col :span="16">
                  <div>
                    <span
                      v-if="item.surveyQuestion.emptyFlag === 'Y'"
                      style="color:red;margin-right:6px;"
                    >*</span>
                    <span style="padding-right: 10px">{{ index + 1 }}.</span>
                    <span>{{ item.surveyQuestion.questionName }}（{{ item.surveyQuestion.questionType | typeFormat }}）</span>
                  </div>
                  <template v-if="item.surveyQuestion.employeeFlag === 'Y'">
                    <el-row
                      v-for="(el, number) in item.jobEmployeeDtoList"
                      :key="number + 'dd'"
                      class="question-listBOx"
                    >
                      <template v-if=" el.employeeResultDtoList && el.employeeResultDtoList.length > 0">
                        <el-col
                          v-for="(elm, num) in el.employeeResultDtoList"
                          :key="num + 'ff'"
                          :span="24"
                          class="question-listBOx"
                        >
                          <!-- 用户有填写的情况 -->
                          <template v-if="elm.surveyResultDtoList && elm.surveyResultDtoList.length > 0">
                            <div>
                              <span>{{ $t('dashboard.employeeName') }}: <strong>{{ elm.employeeName }}</strong></span>
                              <span>-</span>
                              <span>{{ $t('dashboard.employeeJob') }}: <strong>{{ elm.employeeJob }}</strong></span>
                            </div>

                            <el-radio-group
                              v-if="item.surveyQuestion.questionType == 'S'"
                              v-model="elm.resultValue"
                              class="mg-l-20"
                              @change="checkAddType"
                            >
                              <el-row>
                                <el-col
                                  v-for="itm in item.surveySelectionList"
                                  :key="itm.selectionCode + 'ii'"
                                >
                                  <el-radio :label="itm.selectionCode">
                                    {{ itm.selectionCode }}
                                    {{ itm.selectionValue }}
                                  </el-radio>
                                </el-col>
                              </el-row>
                            </el-radio-group>
                            <div v-if="item.surveyQuestion.questionType == 'M'" class="mg-l-20">
                              <div style="color: red">
                                {{ item.surveyQuestion.maxSelection | maxSelectionFormat }}
                              </div>
                              <el-checkbox-group
                                v-model="elm.surveyResultDtoListMap"
                                :max=" item.surveyQuestion.maxSelection == 1 ? 1000 : item.surveyQuestion.maxSelection"
                                @change="checkAddType"
                              >
                                <el-row>
                                  <el-col
                                    v-for="itm in item.surveySelectionList"
                                    :key="itm.selectionCode + 'i2'"
                                  >
                                    <el-checkbox :label="itm.selectionCode">
                                      {{ itm.selectionCode }}
                                      {{ itm.selectionValue }}
                                    </el-checkbox>
                                  </el-col>
                                </el-row>
                              </el-checkbox-group>
                            </div>
                            <div
                              v-if="item.surveyQuestion.questionType == 'Q'"
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

                          <!-- 没填写的情况下 -->
                          <template v-else>
                            <div>
                              <span>{{ $t('dashboard.employeeName') }}: <strong>{{ elm.employeeName }}</strong></span>
                              <span>-</span>
                              <span>{{ $t('dashboard.employeeJob') }}: <strong>{{ elm.employeeJob }}</strong></span>
                            </div>

                            <el-radio-group
                              v-if="item.surveyQuestion.questionType == 'S'"
                              v-model="elm.resultValue"
                              class="mg-l-20"
                              @change="checkAddType"
                            >
                              <el-row>
                                <el-col
                                  v-for="itm in item.surveySelectionList"
                                  :key="itm.selectionCode + 'ii'"
                                >
                                  <el-radio :label="itm.selectionCode">
                                    {{ itm.selectionCode }}
                                    {{ itm.selectionValue }}
                                  </el-radio>
                                </el-col>
                              </el-row>
                            </el-radio-group>
                            <div v-if="item.surveyQuestion.questionType == 'M'" class="mg-l-20">
                              <div style="color: red">
                                {{ item.surveyQuestion.maxSelection | maxSelectionFormat }}
                              </div>
                              <el-checkbox-group
                                v-model="elm.surveyResultDtoListMap"
                                :max=" item.surveyQuestion.maxSelection == 1 ? 1000 : item.surveyQuestion.maxSelection"
                                @change="checkAddType"
                              >
                                <el-row>
                                  <el-col
                                    v-for="itm in item.surveySelectionList"
                                    :key="itm.selectionCode + 'i2'"
                                  >
                                    <el-checkbox :label="itm.selectionCode">
                                      {{ itm.selectionCode }}
                                      {{ itm.selectionValue }}
                                    </el-checkbox>
                                  </el-col>
                                </el-row>
                              </el-checkbox-group>
                            </div>
                            <div
                              v-if="item.surveyQuestion.questionType == 'Q'"
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
                    <template>
                      <el-radio-group
                        v-if="item.surveyQuestion.questionType == 'S'"
                        v-model="item.resultValue"
                        class="mg-l-20"
                        @change.native="(val) => checkAddType(val,'S_Non',index)"
                      >
                        <el-row>
                          <el-col
                            v-for="itm in item.surveySelectionList"
                            :key="itm.selectionCode + 'ii'"
                          >
                            <el-radio :label="itm.selectionCode">
                              {{ itm.selectionCode }}
                              {{ itm.selectionValue }}
                            </el-radio>
                            <!-- :placeholder="'请输入选择原因' + (item.resultValue === itm.selectionCode ? '(必填)' : '')" -->
                            <el-input
                              v-if="itm.descriptionFlag === 'Y'"
                              v-model="itm.description"
                              style="margin-bottom:10px;display:block;"
                              type="textarea"
                              :placeholder="$t('survey.selection') + (item.resultValue === itm.selectionCode ? $t('cusEntry.supplement20250211.requiredField') : '')"
                            />
                          </el-col>
                        </el-row>
                      </el-radio-group>
                      <div v-if="item.surveyQuestion.questionType == 'M'" class="mg-l-20">
                        <div style="color: red">
                          {{ item.surveyQuestion.maxSelection | maxSelectionFormat }}
                        </div>
                        <el-checkbox-group
                          v-model="item.resultValue"
                          :max=" item.surveyQuestion.maxSelection == 1 ? 1000 : item.surveyQuestion.maxSelection"
                          @change="checkAddType"
                        >
                          <el-row>
                            <el-col
                              v-for="itm in item.surveySelectionList"
                              :key="itm.selectionCode + 'i2'"
                            >
                              <el-checkbox :label="itm.selectionCode">
                                {{ itm.selectionCode }}
                                {{ itm.selectionValue }}
                              </el-checkbox>
                              <!-- :placeholder="'请输入选择原因' + (item.resultValue.includes(itm.selectionCode) ? '(必填)' : '')" -->
                              <el-input
                                v-if="itm.descriptionFlag === 'Y'"
                                v-model="itm.description"
                                style="margin-bottom:10px;display:block;"
                                type="textarea"
                                :placeholder="$t('survey.selection') + (item.resultValue.includes(itm.selectionCode) ? $t('cusEntry.supplement20250211.requiredField') : '')"
                              />
                            </el-col>
                          </el-row>
                        </el-checkbox-group>
                      </div>
                      <div v-if="item.surveyQuestion.questionType == 'Q'" class="mg-l-20 listBOx-q">
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
            <el-button
              v-if="allParams.resultFlag == 'N' && $attrs.params.flag === 'edit'"
              type="primary"
              @click="submit"
            >
              {{ $t('problemManagement.submit') }}
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
import FileDynamic from '@/library/components/c-file-management/file-dynamic'
import { getDictItemList } from '@/api/common'
import { adaptDictData } from '@/utils'

export default {
  name: 'SurveyAnswer',

  components: {
    CToolbar,
    FileDynamic
  },

  filters: {
    typeFormat (type) {
      if (type == 'S') {
        return this.$t('dashboard.singleChoice')
      }
      if (type == 'M') {
        return this.$t('dashboard.multipleChoice')
      }
      if (type == 'Q') {
        return this.$t('dashboard.essayQuestion')
      }
    },
    employeeFormat (data) {
      if (data == 'Y') {
        return this.$t('dashboard.employeeSurveys')
      } else {
        return this.$t('dashboard.notEmployeeSurvey')
      }
    },
    maxSelectionFormat (data) {
      if (data == 1) {
        return this.$t('dashboard.noControl')
      } else {
        return this.$t('cusEntry.supplement20250211.maxSelectableItems', {data}) // 最多可选${data}项
      }
    }
  },

  mixins: [tabTodoWatch, tabTodoMixin],

  data () {
    return {
      buyerBusinessId: null,
      supplierBusinessId: null,
      allParams: {
        surveyHeaderFileList: [] // 问卷设置附件
      },
      surveyVendorFileList: [], // 问卷填写附件
      surveyQuestionDTOList: [],
      resultFlagList: [],
      surveyId: '',
      showJumpEnd: null,
      fileList: [],
      jumpEndOptions: [
        {
          value: 'Y',
          label: this.$t('common.yes')
        },
        {
          value: 'N',
          label: this.$t('common.no')
        }
      ]
    }
  },

  mounted () {
    this.fatchDictData()
    this.surveyId = this.$attrs.params.surveyId
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
        const [
          RESULT_FLAG
        ] = res.data
        this.resultFlagList = adaptDictData(
          RESULT_FLAG.RESULT_FLAG
        )
      })
    },

    handleUploadSuccess (file) {
      const { id, name } = file
      this.surveyVendorFileList.push({
        fileuploadId: id,
        fileName: name,
        surveyVendorId: this.surveyId,
        vendorScopeId: this.allParams.vendorScopeId
      })
    },

    // 删除附件
    handleAttachmentRemove () {
      this.allParams.docId = ''
      this.allParams.docName = ''
    },

    resultFlagFormat (data) {
      if (data) {
        let targe = this.resultFlagList.find(item => item.value == data) || {}
        return targe.label || ''
      }
      return ''
    },

    checkAddType (val, type, index) {
      // 判定是单选时选择特定选项跳转到最后
      if (type === 'S_Non') {
        let surveyQuestion = this.surveyQuestionDTOList[index].surveyQuestion || {}
        let surveySelectionList = this.surveyQuestionDTOList[index].surveySelectionList || []
        if (surveyQuestion.jumpEnd === 'Y') {
          let surveySelectionListJump = surveySelectionList.filter(item => item.jumpEnd === 'Y').map(v => v.selectionCode)
          if ([...surveySelectionListJump].includes(val.target.defaultValue)) {
            let surveyQuestionDTOListdata = this.surveyQuestionDTOList.filter((elm, num) => index >= num)
            this.surveyQuestionDTOList = surveyQuestionDTOListdata
          } else {
            this.surveyQuestionDTOListOrigin.map((item, index) => {
              if (index > this.surveyQuestionDTOList.length - 1) {
                this.surveyQuestionDTOList.push(item)
              }
            })
          }
        }
      }
      this.$forceUpdate()
    },

    getDetails (id) {
      this.$http({
        url: '/api-base/basesupplier/surveyquestionsupplier/questionSurveyInfo',
        method: 'post',
        params: { id: id, vendorCode: '' }
      })
        .then(data => {
          if (data) {
            this.allParams = data.data.surveyScopeVendorSupplierDto
            this.surveyVendorFileList = data.data.surveyVendorFileList || []
            this.fileList = this.surveyVendorFileList.map(item => {
              return {
                id: item.fileuploadId,
                name: item.fileName
              }
            })
            let surveyQuestionDTOList = data.data.surveyQuestionSupplierDTOList
            // this.surveyQuestionDTOList = surveyQuestionDTOList
            data.data.surveyQuestionSupplierDTOList.forEach((item, index) => {
              this.$set(this.surveyQuestionDTOList, index, item)
            })

            this.surveyQuestionDTOListOrigin = surveyQuestionDTOList
            // 员工调查 多选情况下取值
            this.surveyQuestionDTOList.forEach(item => {
              if (item.surveyQuestion.employeeFlag === 'Y' && item.surveyQuestion.questionType === 'M' && item.jobEmployeeDtoList && item.jobEmployeeDtoList.length > 0) {
                item.jobEmployeeDtoList.map(elm => {
                  if (elm.employeeResultDtoList && elm.employeeResultDtoList.length > 0) {
                    elm.employeeResultDtoList.map(el => {
                      el.surveyResultDtoListMap = el.surveyResultDtoList.map(v => v.resultValue)
                    })
                  }
                })
              } else if (item.surveyQuestion.employeeFlag === 'Y' && item.surveyQuestion.questionType === 'S' && item.jobEmployeeDtoList && item.jobEmployeeDtoList.length > 0) {
                item.jobEmployeeDtoList.map(elm => {
                  if (elm.employeeResultDtoList && elm.employeeResultDtoList.length > 0) {
                    elm.employeeResultDtoList.map(el => {
                      if (el.surveyResultDtoList && el.surveyResultDtoList.length > 0) {
                        el.resultValue = el.surveyResultDtoList[0].resultValue
                      }
                    })
                  }
                })
              } else if (item.surveyQuestion.employeeFlag === 'Y' && item.surveyQuestion.questionType === 'Q' && item.jobEmployeeDtoList && item.jobEmployeeDtoList.length > 0) {
                item.jobEmployeeDtoList.map(elm => {
                  if (elm.employeeResultDtoList && elm.employeeResultDtoList.length > 0) {
                    elm.employeeResultDtoList.map(el => {
                      if (el.surveyResultDtoList && el.surveyResultDtoList.length > 0) {
                        el.resultValue = el.surveyResultDtoList[0].resultValue
                      }
                    })
                  }
                })
              }
            })

            if (this.allParams.resultFlag == 'Y') { // 已反馈
              this.surveyQuestionDTOList.forEach(item => {
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
              this.surveyQuestionDTOList.forEach(item => {
                if (item.surveyQuestion.questionType == 'M') {
                  item.resultValue = []
                } else {
                  item.resultValue = null
                }
              })
            }
            this.buyerBusinessId = id
            this.supplierBusinessId = this.allParams.vendorScopeId
            this.$refs.buyerSceneAttachment.loadFileInfo()
            this.$refs.supplierSceneAttachment.loadFileInfo()
            this.$forceUpdate()
          }
        })
    },

    submit () {
      for (let item of this.surveyQuestionDTOList) {
        let { resultValue } = item
        let { emptyFlag, questionType } = item.surveyQuestion
        if (emptyFlag === 'Y') {
          if (['S', 'Q'].includes(questionType)) {
            if (!resultValue) {
              this.$message({
                type: 'error',
                message: this.$t('surveyAnswer.resultValueFillTip')
              })
              return
            }
          } else {
            if (!resultValue || !resultValue.length) {
              this.$message({
                type: 'error',
                message: this.$t('surveyAnswer.resultValueFillTip')
              })
              return
            }
          }
        }
      }
      let params = []
      this.surveyQuestionDTOList.map(item => {
        item.surveySelectionList.map(itm => {
          // 非员工调查
          if (item.surveyQuestion.employeeFlag === 'N') {
            let obj = {}
            if (item.surveyQuestion.questionType == 'M') {
              if (item.resultValue.some(i => i == itm.selectionCode)) {
                obj.selectionId = itm.selectionId
                obj.vendorScopeId = this.allParams.vendorScopeId
                obj.resultValue = itm.selectionCode
                obj.questionId = itm.questionId
                obj.employeeScopeId = null
                obj.description = itm.description
                obj.descriptionFlag = itm.descriptionFlag
                obj.fileUploadId = Number(this.allParams.docId) || ''
                params.push(obj)
              }
            } else if (item.surveyQuestion.questionType == 'Q') {
              obj.selectionId = itm.selectionId
              obj.vendorScopeId = this.allParams.vendorScopeId
              obj.resultValue = item.resultValue
              obj.questionId = itm.questionId
              obj.employeeScopeId = null
              obj.fileUploadId = Number(this.allParams.docId) || ''
              params.push(obj)
            } else {
              if (item.resultValue == itm.selectionCode) {
                obj.selectionId = itm.selectionId
                obj.vendorScopeId = this.allParams.vendorScopeId
                obj.resultValue = item.resultValue
                obj.questionId = itm.questionId
                obj.employeeScopeId = null
                obj.description = itm.description
                obj.descriptionFlag = itm.descriptionFlag
                obj.fileUploadId = Number(this.allParams.docId) || ''
                params.push(obj)
              }
            }
          } else if (item.surveyQuestion.employeeFlag === 'Y') {
            // 员工调查
            if (item.jobEmployeeDtoList && item.jobEmployeeDtoList.length > 0) {
              item.jobEmployeeDtoList.map(elm => {
                if (elm.employeeResultDtoList && elm.employeeResultDtoList.length > 0) {
                  elm.employeeResultDtoList.map(el => {
                    let obj = {}
                    if (item.surveyQuestion.questionType == 'M') {
                      if (el.surveyResultDtoListMap.some(i => i == itm.selectionCode)) {
                        obj.selectionId = itm.selectionId
                        obj.vendorScopeId = this.allParams.vendorScopeId
                        obj.resultValue = itm.selectionCode
                        obj.questionId = itm.questionId
                        obj.employeeScopeId = el.scopeId
                        obj.description = itm.description
                        obj.descriptionFlag = itm.descriptionFlag
                        obj.fileUploadId = Number(this.allParams.docId) || ''
                        params.push(obj)
                      }
                    } else if (item.surveyQuestion.questionType == 'Q') {
                      obj.selectionId = itm.selectionId
                      obj.vendorScopeId = this.allParams.vendorScopeId
                      obj.resultValue = el.resultValue
                      obj.questionId = itm.questionId
                      obj.employeeScopeId = el.scopeId
                      obj.fileUploadId = Number(this.allParams.docId) || ''
                      params.push(obj)
                    } else {
                      if (el.resultValue == itm.selectionCode) {
                        obj.selectionId = itm.selectionId
                        obj.vendorScopeId = this.allParams.vendorScopeId
                        obj.resultValue = el.resultValue
                        obj.questionId = itm.questionId
                        obj.employeeScopeId = el.scopeId
                        obj.description = itm.description
                        obj.descriptionFlag = itm.descriptionFlag
                        obj.fileUploadId = Number(this.allParams.docId) || ''
                        params.push(obj)
                      }
                    }
                  })
                }
              })
            }
          }
        })
      })
      for (let item of params) {
        if (item.descriptionFlag === 'Y' && !item.description) {
          this.$message({
            type: 'error',
            message: this.$t('survey.enterReason')
          })
          return
        }
      }
      this.$http({
        url: '/api-base/basesupplier/surveyresultsupplier/save',
        method: 'post',
        data: {
          surveyVendorFileList: this.surveyVendorFileList,
          surveyResultDtoList: params
        }
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
            this.__setTabTodo('SurveyList.getQuerydata')
          }
        })
    },
    backTo () {
      this.$emit(
        'tab-remove',
        this.$attrs.params.tabName
      )
      this.__setTabTodo('SurveyList.getQuerydata')
    }
  }
}
</script>

<style lang="scss" scoped>
.main-padding {
  padding: 15px;
  :deep(.el-form-item__label) {
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
  :deep(.el-radio-group) {
    line-height: 28px;
    width: 100%;
  }
  :deep(.el-textarea) {
    width: 90%;
  }
  :deep(.el-radio__input.is-disabled + span.el-radio__label) {
    color: #606266;
  }
  :deep(.el-button) {
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
