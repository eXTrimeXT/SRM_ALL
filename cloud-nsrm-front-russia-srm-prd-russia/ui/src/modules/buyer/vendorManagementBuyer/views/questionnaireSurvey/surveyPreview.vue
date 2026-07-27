<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <div class="main-padding">
        <el-form
          :model="allParams"
          label-width="80px"
        >
          <div class="surveyTitle">
            {{ allParams.surveyTitle }}
          </div>
          <div class="mg-l-16">
            <el-row :gutter="32">
              <el-col :span="8">
                <div class="mg-l-header">
                  {{ $t('orderMod.buyerOrderSynergy.vendorName') }}
                </div>
              </el-col>
              <el-col :span="8">
                <div class="mg-l-header">
                  {{ $t('dashboard.loopMode') }}
                </div>
              </el-col>
              <el-col :span="8">
                <div class="mg-l-header">
                  {{ $t('dashboard.loopTime') }}
                </div>
              </el-col>
              <el-col :span="24">
                <el-form-item :label="$t('dashboard.surveyDesc')">
                  <el-input
                    v-model="allParams.surveyDesc"
                    disabled
                    type="textarea"
                    :rows="2"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item :label="$t('dashboard.accessory')">
                  <div
                    class="download-link-wrap"
                    style="margin-right: 15px"
                  >
                    <template v-if="allParams.surveyHeaderFileList && allParams.surveyHeaderFileList.length">
                      <SrmCommonFile
                        v-for="(item,index) in allParams.surveyHeaderFileList"
                        :key="index + item.fileuploadId"
                        :default-file="{
                          fileId: item.fileuploadId,
                          fileName: item.fileName
                        }"
                        :readonly="true"
                      />
                    </template>
                    <span
                      v-else
                      style="
                        padding-left: 10px;
                        font-size: 12px;
                        color: #606266;
                      "
                    >{{ $t('contractMod.no') }}</span>
                  </div>
                </el-form-item>
              </el-col>
            </el-row>
            <div class="question-list">
              <el-row
                v-for="(item, index) in allParams.surveyQuestionDTOList"
                :key="index + 'pp'"
                class="question-listBOx"
              >
                <el-col :span="16">
                  <div>
                    <span v-if="item.surveyQuestion.emptyFlag === 'Y'" style="color:red;margin-right:6px;">*</span>
                    <span style="padding-right: 10px">{{ index + 1 }}.</span>
                    <span>{{ item.surveyQuestion.questionName }}（{{
                      item.surveyQuestion.questionType | typeFormat
                    }}）</span>
                  </div>
                  <el-radio-group
                    v-if="item.surveyQuestion.questionType == 'S'"
                    class="mg-l-20"
                    disabled
                  >
                    <el-row>
                      <el-col
                        v-for="itm in item.surveySelectionList"
                        :key="itm.selectionCode + 'ii'"
                      >
                        <el-radio
                          :label="itm.selectionCode"
                        >
                          {{ itm.selectionCode }}
                          {{ itm.selectionValue }}
                        </el-radio>
                        <el-input
                          v-if="itm.descriptionFlag === 'Y'"
                          v-model="itm.description"
                          style="margin-bottom:10px;display:block;"
                          disabled
                          :placeholder="$t('survey.selection')"
                          type="textarea"
                        />
                      </el-col>
                    </el-row>
                  </el-radio-group>
                  <div
                    v-if="item.surveyQuestion.questionType == 'M'"
                    class="mg-l-20"
                  >
                    <div style="color: red">
                      {{
                        item.surveyQuestion.maxSelection | maxSelectionFormat
                      }}
                    </div>
                    <el-checkbox-group disabled>
                      <el-row>
                        <el-col
                          v-for="itm in item.surveySelectionList"
                          :key="itm.selectionCode + 'ii'"
                        >
                          <el-checkbox
                            :label="itm.selectionCode"
                          >
                            {{ itm.selectionCode }}
                            {{ itm.selectionValue }}
                          </el-checkbox>
                          <el-input
                            v-if="itm.descriptionFlag === 'Y'"
                            v-model="itm.description"
                            style="margin-bottom:10px;display:block;"
                            disabled
                            :placeholder="$t('survey.selection')"
                            type="textarea"
                          />
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
                      type="textarea"
                      width="90%"
                      :rows="2"
                      disabled
                    />
                  </div>
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

export default {
  name: 'SurveyPreview',

  components: { CToolbar },

  filters: {
    typeFormat (type) {
      if (type == 'S') {
        return '单选题'
      }
      if (type == 'M') {
        return '多选题'
      }
      if (type == 'Q') {
        return '问答题'
      }
    },
    employeeFormat (data) {
      if (data == 'Y') {
        return '员工调查'
      } else {
        return '非员工调查'
      }
    },
    maxSelectionFormat (data) {
      if (data == 1) {
        return '不控制'
      } else {
        return `最多可选${data}项`
      }
    }
  },

  mixins: [tabTodoWatch, tabTodoMixin],

  data () {
    return {
      allParams: {},
      jumpEndOptions: [{
        value: 'Y',
        label: '是'
      }, {
        value: 'N',
        label: '否'
      }]
    }
  },

  mounted () {
    this.allParams = this.$attrs.params.row
  },

  methods: {
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
  :deep(.el-form-item__label ){
    text-align: left;
  }
}
.surveyTitle {
  height: 48px;
  text-align: center;
  font-size: 14px;
  color: #1c2438;
  font-weight: 700;
  border-bottom: 1px solid #e6e9ec;
}
.question-list {
  margin-top: 30px;
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
