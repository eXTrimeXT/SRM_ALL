<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <el-tabs type="card">
        <div v-if="withdrawReservationFlag && curOpt === 'edit'" class="tips">
          {{ $t('survey.detail') }}
        </div>
        <el-tab-pane :label="$t('survey.QuestionnaireInformation')">
          <el-collapse v-model="activeDims">
            <!-- 基本信息 -->
            <el-collapse-item :title="$t('vendorMod.baseInfo')" name="1">
              <el-form
                ref="info"
                :disabled="disabled || withdrawReservationFlag"
                :model="allParams"
                :rules="rules"
              >
                <el-row :gutter="32">
                  <el-col :span="6">
                    <el-form-item :label="$t('bidMod.businessEntity')" prop="buId">
                      <OrganizationSelector
                        ref="organizationSelector"
                        v-model="allParams.buId"
                        :parent-id="-1"
                        node-type="OU"
                        :placeholder="$t('common.pleaseSelect')"
                        :scope="allParams"
                        @select="addOrgHandle"
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="6">
                    <el-form-item :label="$t('components.notice.publishTime')">
                      <el-date-picker
                        v-model="allParams.publishDate"
                        type="datetime"
                        :placeholder="$t('purchaseDemand.datePicker')"
                        default-time="0:00:00"
                        value-format="yyyy-MM-dd HH:mm:ss"
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="6">
                    <el-form-item :label="$t('dashboard.endDate')" prop="endDate">
                      <el-date-picker
                        v-model="allParams.endDate"
                        type="datetime"
                        :placeholder="$t('purchaseDemand.datePicker')"
                        default-time="23:59:59"
                        value-format="yyyy-MM-dd HH:mm:ss"
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="6">
                    <el-form-item :label="$t('survey.ResultViewingPermission')" prop="feedbackFlag">
                      <dict-select v-model="allParams.feedbackFlag" code="FEEDBACK_FLAG" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="6">
                    <el-form-item :label="$t('dataConfMod.questionnaireNumber')">
                      <el-input v-model="allParams.surveyNum" disabled />
                    </el-form-item>
                  </el-col>

                  <el-col :span="6">
                    <el-form-item :label="$t('dataConfMod.statusInventory')">
                      <dict-select v-model="allParams.statusCode" code="STATUS_CODE" disabled />
                    </el-form-item>
                  </el-col>
                  <el-col :span="6">
                    <el-form-item :label="$t('dataConfMod.createdBy')">
                      <el-input v-model="allParams.createdBy" disabled />
                    </el-form-item>
                  </el-col>
                  <el-col :span="6">
                    <el-form-item :label="$t('common.creationTime')">
                      <el-date-picker
                        v-model="allParams.creationDate"
                        disabled
                        format="yyyy-MM-dd"
                        type="date"
                        :placeholder="$t('purchaseDemand.datePicker')"
                      />
                    </el-form-item>
                  </el-col>
                </el-row>
              </el-form>
            </el-collapse-item>
            <!-- 供应商范围 -->
            <el-collapse-item v-show="surveyId" :title="$t('survey.SupplierScope')" name="2">
              <el-row class="vednor-box">
                <el-col :span="24">
                  <MainHeader v-if="!disabled" :l-span="22" :r-span="2">
                    <template slot="left">
                      <QuickSearch
                        ref="vendorQuicksearch"
                        btn-title="新增"
                        :pre-query-data="quickSearch"
                        name="scc_sup_company_info_url"
                        multi-select
                        style="display:inline-block;margin-right:10px;vertical-align:top;"
                        @close-quicksearch="handleVendorAdd"
                      />
                      <el-button type="primary" @click="handleVendorDelete">
                        {{ $t('components.common.delete') }}
                      </el-button>
                      <MImport
                        ref="import"
                        style="display: inline-block;"
                        :title="$t('components.eio.importTitle')"
                        up-load-url="/api-base/base/surveyscopevendor/customImportExcelTemplate"
                        :extra-data="vendorExtraData"
                        :extra-post-data="extraPostData"
                        :disabled="disabled || withdrawReservationFlag"
                        @downloadTemplate="vendorDownloadTemplate"
                        @handleSuccess="vendorHandleSuccess"
                      />
                    </template>
                  </MainHeader>
                  <TableView
                    :ref="gridId"
                    :checkbox="!disabled"
                    :table-data="tableData"
                    :table-header="tableHeader"
                    :page-size="pageSize"
                    out-side-height="400px"
                    :pre-query-data="queryParam"
                    :check-change="selectChange"
                    :row-index-fixed="false"
                    url="/api-base/base/surveyscopevendor/querySupplierHasSelected"
                    @afterQuery="afterQuery"
                  />
                </el-col>
              </el-row>
            </el-collapse-item>
            <!-- 问卷详情 -->
            <el-collapse-item v-show="surveyId" :title="$t('survey.QuestionnaireDetails')" name="3">
              <el-form
                ref="infoDetail"
                :disabled="disabled"
                :model="allParams"
                label-width="80px"
                :rules="rules"
              >
                <el-row :gutter="32">
                  <el-col :span="24" style="margin-bottom: 25px;">
                    <el-form-item :label="$t('dataConfMod.questionnaire')" prop="surveyTitle">
                      <el-input v-model="allParams.surveyTitle" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="24">
                    <el-form-item :label="$t('survey.QuestionnaireDescription')">
                      <el-input v-model="allParams.surveyDesc" type="textarea" :rows="2" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="24">
                    <el-form-item :label="$t('perfMod.accessory')">
                      <FileDynamic
                        ref="sceneAttachment"
                        v-model="allParams.surveyHeaderFileList"
                        scene-module-code="SCENE_SURVEY_HEADER_ATTACHMENT"
                        :business-id="businessId"
                        :need-init="false"
                        :editable="!(disabled || withdrawReservationFlag)"
                      />
                    </el-form-item>
                  </el-col>
                </el-row>
              </el-form>
              <div class="question-list">
                <el-row
                  v-if="!allParams.surveyQuestionDTOList.length"
                  :gutter="32"
                  style="margin-top: 16px"
                >
                  <el-col :span="24">
                    <el-button type="primary" @click="addTitle">
                      {{ $t('survey.NewTopic') }}
                    </el-button>
                  </el-col>
                </el-row>
                <template v-else>
                  <el-row
                    v-for="(item, index) in allParams.surveyQuestionDTOList"
                    :key="index + 'pp'"
                    :gutter="32"
                    class="question-listBOx"
                  >
                    <el-col :span="16">
                      <div>
                        <span
                          v-if="item.surveyQuestion.emptyFlag === 'Y'"
                          style="color:red;margin-right:6px;"
                        >*</span>
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
                            <el-radio :label="itm.selectionCode">
                              {{ itm.selectionCode }} {{ itm.selectionValue }}
                            </el-radio>
                            <el-checkbox
                              v-if="itm.jumpEnd === 'Y'"
                              v-model="itm.jumpEnd"
                              true-label="Y"
                              false-label="N"
                              style="margin-left: 20px"
                              disabled
                            >
                              {{ itm.selectionCode + ` 勾选后跳转到末尾` }}
                            </el-checkbox>
                            <el-input
                              v-if="itm.descriptionFlag === 'Y'"
                              v-model="itm.description"
                              style="margin-bottom:10px;display:block;"
                              :placeholder="$t('survey.selection')"
                              type="textarea"
                              disabled
                            />
                          </el-col>
                        </el-row>
                      </el-radio-group>
                      <div v-if="item.surveyQuestion.questionType == 'M'" class="mg-l-20">
                        <div style="color: red">
                          {{ item.surveyQuestion.maxSelection | maxSelectionFormat }}
                        </div>
                        <el-checkbox-group disabled>
                          <el-row>
                            <el-col
                              v-for="itm in item.surveySelectionList"
                              :key="itm.selectionCode + 'ii'"
                            >
                              <el-checkbox :label="itm.selectionCode">
                                {{ itm.selectionCode }} {{ itm.selectionValue }}
                              </el-checkbox>
                              <el-input
                                v-if="itm.descriptionFlag === 'Y'"
                                v-model="itm.description"
                                style="margin-bottom:10px;display:block;"
                                :placeholder="$t('survey.selection')"
                                type="textarea"
                                disabled
                              />
                            </el-col>
                          </el-row>
                        </el-checkbox-group>
                      </div>
                      <div v-if="item.surveyQuestion.questionType == 'Q'">
                        <div class="mg-l-20 listBOx-q">
                          <div>{{ $t('dashboard.answer') }}</div>
                          <el-input type="textarea" :rows="2" disabled />
                        </div>
                      </div>
                    </el-col>
                    <el-col v-if="!disabled" :span="8">
                      <el-button type="text" :disabled="withdrawReservationFlag" @click="addTitle">
                        {{ $t('bidMod.affairsIncreased') }}
                      </el-button>
                      <el-button type="text" @click="editQuestion(item, index)">
                        {{ $t('orderMod.buyerOrderSynergy.management') }}
                      </el-button>
                      <el-button
                        type="text"
                        :disabled="withdrawReservationFlag"
                        @click="copyQuestion(item)"
                      >
                        {{ $t('common.copy') }}
                      </el-button>
                      <el-button
                        type="text"
                        :disabled="!(allParams.surveyQuestionDTOList.length >= 2 && index > 0) || withdrawReservationFlag"
                        @click="moveUp(item, index)"
                      >
                        {{ $t('priceModel.costElement.moveUp') }}
                      </el-button>
                      <el-button
                        type="text"
                        :disabled="
                          !(
                            allParams.surveyQuestionDTOList.length >= 2 &&
                            index < allParams.surveyQuestionDTOList.length - 1
                          ) || withdrawReservationFlag
                        "
                        @click="moveDown(item, index)"
                      >
                        {{ $t('quoteTemplate.moveDown') }}
                      </el-button>
                      <el-button
                        type="text"
                        :disabled="withdrawReservationFlag"
                        @click="delQuestion(index)"
                      >
                        {{ $t('components.common.delete') }}
                      </el-button>
                    </el-col>
                  </el-row>
                </template>
              </div>
              <!-- 新增 编辑弹框区域-->
              <srm-dialog
                :title="$t('survey.NewTopic')"
                :visible.sync="dialogFormVisible"
                :close-on-click-modal="false"
                size="middle"
              >
                <el-form ref="addForm" :model="surveyQuestion" label-width="80px">
                  <el-row :gutter="32">
                    <el-col :span="24">
                      <el-form-item :label="$t('survey.TopicType')">
                        <el-radio-group
                          v-model="surveyQuestion.questionType"
                          :disabled="withdrawReservationFlag"
                        >
                          <el-radio label="S">
                            {{ $t('dashboard.singleChoice') }}
                          </el-radio>
                          <el-radio label="M">
                            {{ $t('dashboard.multipleChoice') }}
                          </el-radio>
                          <el-radio label="Q">
                            {{ $t('dashboard.essayQuestion') }}
                          </el-radio>
                        </el-radio-group>
                      </el-form-item>
                    </el-col>
                    <el-col
                      v-if="
                        surveyQuestion.questionType == 'S' || surveyQuestion.questionType == 'M'
                      "
                      :span="12"
                    >
                      <el-form-item :label="$t('survey.NumberOfOptions')" prop="selectionCount">
                        <el-select
                          v-model="surveyQuestion.selectionCount"
                          :disabled="withdrawReservationFlag"
                        >
                          <el-option
                            v-for="item in 8"
                            :key="item + 'ww'"
                            :label="item + 1"
                            :value="item + 1"
                          />
                        </el-select>
                      </el-form-item>
                    </el-col>
                    <el-col v-if="surveyQuestion.questionType == 'M'" :span="12">
                      <el-form-item :label="$t('survey.MaximumOptions')">
                        <el-select
                          v-model="surveyQuestion.maxSelection"
                          :disabled="withdrawReservationFlag"
                        >
                          <el-option
                            v-for="item in maxSelectionList"
                            :key="item.value + 'ee'"
                            :label="item.label"
                            :value="item.value"
                          />
                        </el-select>
                      </el-form-item>
                    </el-col>
                    <el-col :span="24">
                      <el-form-item :label="$t('dataConfMod.isRequested')">
                        <el-radio-group
                          v-model="surveyQuestion.emptyFlag"
                          :disabled="withdrawReservationFlag"
                        >
                          <el-radio label="Y">
                            {{ $t('common.yes') }}
                          </el-radio>
                          <el-radio label="N">
                            {{ $t('common.no') }}
                          </el-radio>
                        </el-radio-group>
                      </el-form-item>
                    </el-col>
                    <el-col v-if="surveyQuestion.questionType == 'S'" :span="24">
                      <el-form-item :label="$t('survey.singleChoice')">
                        <el-input v-model="surveyQuestion.questionName" :placeholder="$t('survey.singleChoice')" />
                      </el-form-item>
                    </el-col>
                    <el-col v-if="surveyQuestion.questionType == 'S'" :span="24">
                      <div
                        v-for="(item, index) in surveySelectionList"
                        :key="index + 'tt'"
                        class="select-box"
                      >
                        <el-radio v-model="item.selectionCode" disabled :label="item.selectionCode">
                          {{ item.selectionCode }}
                        </el-radio>
                        <el-input
                          v-model="item.selectionValue"
                          :placeholder="item.selectionHolder"
                        />
                        <el-checkbox
                          v-model="item.descriptionFlag"
                          true-label="Y"
                          false-label="N"
                          style="margin-left: 20px"
                          :disabled="withdrawReservationFlag"
                        >
                          {{ $t('survey.AddDescriptionColumn') }}
                        </el-checkbox>
                        <el-checkbox
                          v-show="surveyQuestion.jumpEnd === 'Y'"
                          v-model="item.jumpEnd"
                          true-label="Y"
                          false-label="N"
                          :disabled="withdrawReservationFlag"
                          @change="(val) => jumpEndIndex(val, index)"
                        >
                          {{ item.selectionCode + ` 勾选后跳转到末尾` }}
                        </el-checkbox>
                      </div>
                      <el-col :span="12" style="padding-left: 0px">
                        <el-form-item :label="$t('survey.JumpToTheEndOrNot')">
                          <el-select
                            v-model="surveyQuestion.jumpEnd"
                            style="margin-left: 14px"
                            clearable
                            :placeholder="$t('common.pleaseSelect')"
                            :disabled="withdrawReservationFlag"
                            @change="jumpEndCheck"
                          >
                            <el-option
                              v-for="item in jumpEndOptions"
                              :key="item.value"
                              :label="item.label"
                              :value="item.value"
                            />
                          </el-select>
                        </el-form-item>
                      </el-col>
                    </el-col>
                    <el-col v-if="surveyQuestion.questionType == 'M'" :span="24">
                      <el-form-item :label="$t('survey.MultipleChoiceQuestions')">
                        <el-input v-model="surveyQuestion.questionName" :placeholder="$t('survey.MultipleChoiceQuestions')" />
                      </el-form-item>
                    </el-col>
                    <el-col v-if="surveyQuestion.questionType == 'M'" :span="24">
                      <div
                        v-for="(item, index) in surveySelectionList"
                        :key="index + 'tt'"
                        class="select-box"
                        style="margin-left: 35px"
                      >
                        <el-checkbox
                          v-model="item.selectionCode"
                          disabled
                          :label="item.selectionCode"
                        >
                          {{ item.selectionCode }}
                        </el-checkbox>
                        <el-input
                          v-model="item.selectionValue"
                          :placeholder="item.selectionHolder"
                        />
                        <el-checkbox
                          v-model="item.descriptionFlag"
                          true-label="Y"
                          false-label="N"
                          style="margin-left: 20px"
                          :disabled="withdrawReservationFlag"
                        >
                          {{ $t('survey.AddDescriptionColumn') }}
                        </el-checkbox>
                      </div>
                    </el-col>
                    <el-col v-if="surveyQuestion.questionType == 'Q'" :span="24">
                      <el-form-item :label="$t('survey.Questions')">
                        <el-input
                          v-model="surveyQuestion.questionName"
                          type="textarea"
                          :rows="2"
                          :placeholder="$t('survey.Questions')"
                        />
                      </el-form-item>
                    </el-col>
                  </el-row>
                </el-form>
                <div slot="footer" class="dialog-footer">
                  <el-button @click="dialogFormVisible = false">
                    <!-- 取 消 -->
                    {{ $t('common.cancel') }}
                  </el-button>
                  <el-button type="primary" @click="comfirmSave">
                    <!-- 确 定 -->
                    {{ $t('common.confirm') }}
                  </el-button>
                </div>
              </srm-dialog>
            </el-collapse-item>
          </el-collapse>
          <CToolbar>
            <template slot="right">
              <el-button v-if="surveyId" type="primary" @click="preview">
                {{ $t('common.preview') }}
              </el-button>
              <el-button v-if="disabled" type="primary" @click="toResult">
                {{ $t('dashboard.totalCount') }}
              </el-button>
              <el-button v-if="!disabled && curOpt != 'add'" type="primary" @click="saveInfo">
                {{ $t('bidMod.temporaryStorage') }}
              </el-button>
              <el-button v-if="!disabled && curOpt == 'add'" type="primary" @click="saveInfo">
                {{ $t('bidMod.nextOne') }}
              </el-button>
              <el-button v-if="!disabled && surveyId" type="primary" @click="submitted">
                {{ $t('perfMod.release') }}
              </el-button>
            </template>
          </CToolbar>
        </el-tab-pane>
      </el-tabs>
    </el-main>
  </el-container>
</template>

<script>
import OrganizationSelector from 'lib@/components/organization-selector'
import CToolbar from 'lib@/components/c-toolbar'
import MainHeader from 'lib@/components/Table/MainHeader'
import TableView from 'lib@/components/Table/TableView'
import { parseTime } from '@/utils'
import { downloadFileLinkByPost, downloadFileLink } from 'lib@/utils/file'
import surveyPreview from './surveyPreview'
import surveyResult from './surveyResult'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import MImport from 'lib@/components/import'
import uniqWith from 'lodash/uniqWith'
import isEqual from 'lodash/isEqual'
import QuickSearch from 'lib@/components/QuickSearch'
import FileDynamic from '@/library/components/c-file-management/file-dynamic'

export default {
  name: 'SurveyDetail',

  components: {
    MImport,
    OrganizationSelector,
    MainHeader,
    TableView,
    CToolbar,
    QuickSearch,
    FileDynamic
  },

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
      quickSearch: { 't.DATA_SOURCES': 'IMAGE', 't.STATUS': 'APPROVED' },
      curOpt: 'add',
      activeDims: ['1', '2', '3', '4'],
      allParams: {
        buId: null,
        buCode: null,
        buName: null,
        endDate: null,
        feedbackFlag: '',
        statusCode: 'DRAFT',
        surveyTitle: '',
        surveyQuestionDTOList: [],
        fileFlag: 'N',
        surveyHeaderFileList: []
      },
      surveyId: '',

      businessId: null,
      selectedRows: [],
      orgValue: {},
      operationType: 'add',

      gridId: 'list',
      gridId1: 'list1',
      currentRow: null,
      selectList: [],
      isOperationAll: false,
      dialogFormVisible: false,
      jumpEndOptions: [
        {
          value: 'Y',
          label: '是'
        },
        {
          value: 'N',
          label: '否'
        }
      ],
      currentHeaderRows: [],
      maxSelectionList: [],
      tableHeader: [
        {
          prop: 'vendorCode',
          label: '供应商编码'
        },
        {
          prop: 'vendorName',
          label: '供应商名称'
        },
        {
          prop: 'operate',
          label: '操作',
          showType: 'button',
          btnStyle: 'text',
          formattor: () => '删除',
          show: row => !this.disabled,
          callback: row => {
            this.confirmDelete(() => this.vendorDelete([row]))
          }
        }
      ],
      tableHeaderSelected: [
        {
          prop: 'vendorName',
          label: '已选供应商'
        }
      ],
      upLoadUrl: '/api-base/base/surveyheader/importSurveyQuestionExcel',
      extraPostData: {
        surveyHeader: { surveyId: '' }
      },
      tableData: [],
      pageSize: 15,
      queryParam: {},
      wordList: ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'],
      surveyQuestion: {
        questionType: 'S',
        selectionCount: 2,
        maxSelection: 1,
        employeeFlag: 'N',
        jobList: []
      },

      surveySelectionList: [
        {
          selectionCode: 'A',
          selectionValue: null,
          selectionHolder: '选项1',
          descriptionFlag: 'N',
          description: null
        },
        {
          selectionCode: 'B',
          selectionValue: null,
          selectionHolder: '选项2',
          descriptionFlag: 'N',
          description: null
        }
      ],
      fileList: [],
      rules: {
        buId: [{ required: true, message: '请选择业务实体' }],
        endDate: [{ required: true, message: '请选择反馈截止时间' }],
        feedbackFlag: [{ required: true, message: '请选择查看结果权限' }],
        surveyTitle: [{ required: true, message: '请输入问卷标题' }]
      },
      preArr: [
        {
          prop: 'vendorName',
          label: () => this.$t('common.vendorName'), // '供应商名称'
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_display_buyer'
        },
        {
          prop: 'categoryName',
          label: '采购品类'
        },
        {
          prop: 'materialName',
          label: '物料名称'
        }
      ]
    }
  },

  computed: {
    disabled () {
      return !(this.allParams.statusCode == 'DRAFT')
    },
    withdrawReservationFlag () {
      return this.allParams.withdrawReservationFlag === 'Y'
    },
    vendorExtraData () {
      return {
        fileType: 'excel',
        fileModular: 'base',
        fileFunction: 'accountAccess',
        businessId: this.surveyId
      }
    }
  },

  watch: {
    'surveyQuestion.selectionCount': {
      immediate: true,
      handler: function (n, o) {
        let arr = [{ value: 1, label: '不控制' }]
        let count = this.surveyQuestion.selectionCount
        let arr1 = []
        for (let i = 0; i < count - 1; i++) {
          arr.push({ value: i + 2, label: i + 2 })
        }
        for (let i = 0; i < count; i++) {
          arr1.push({
            selectionCode: this.wordList[i],
            selectionValue: this.surveySelectionList[i]
              ? this.surveySelectionList[i].selectionValue
              : null,
            selectionHolder: '选项' + (i + 1),
            jumpEnd: this.surveySelectionList[i] ? this.surveySelectionList[i].jumpEnd : '',
            descriptionFlag: this.surveySelectionList[i]
              ? this.surveySelectionList[i].descriptionFlag
              : 'N',
            description: this.surveySelectionList[i]
              ? this.surveySelectionList[i].description
              : null,
            questionId: this.surveySelectionList[i]
              ? this.surveySelectionList[i].questionId
              : undefined,
            selectionId: this.surveySelectionList[i]
              ? this.surveySelectionList[i].selectionId
              : undefined
          })
        }
        this.maxSelectionList = arr
        this.surveySelectionList = arr1
      },
      deep: true
    },
    'surveyQuestion.questionType': {
      immediate: true,
      handler: function (n, o) {
        if (this.surveyQuestion.questionType == 'Q') {
          this.surveySelectionList = [
            {
              selectionCode: 'A',
              selectionValue: null,
              selectionHolder: '选项1'
            }
          ]
        } else {
          let count = this.surveyQuestion.selectionCount
          let arr = []
          for (let i = 0; i < count; i++) {
            arr.push({
              selectionCode: this.wordList[i],
              selectionValue: this.surveySelectionList[i]
                ? this.surveySelectionList[i].selectionValue
                : null,
              selectionHolder: '选项' + (i + 1),
              jumpEnd: this.surveySelectionList[i] ? this.surveySelectionList[i].jumpEnd : '',
              descriptionFlag: this.surveySelectionList[i]
                ? this.surveySelectionList[i].descriptionFlag
                : 'N',
              description: this.surveySelectionList[i]
                ? this.surveySelectionList[i].description
                : null,
              questionId: this.surveySelectionList[i]
                ? this.surveySelectionList[i].questionId
                : undefined,
              selectionId: this.surveySelectionList[i]
                ? this.surveySelectionList[i].selectionId
                : undefined
            })
          }
          this.surveySelectionList = arr
        }
      },
      deep: true
    },
    surveyId: {
      handler: function (nVal) {
        if (nVal) this.extraPostData.surveyHeader = JSON.stringify({ surveyId: nVal })
      },
      immediate: true
    }
  },

  mounted () {
    this.$nextTick(() => {
      this.getQuerydata()
    })
    this.curOpt = this.$attrs.params.flag
    if (this.$attrs.params.flag === 'edit' || this.$attrs.params.flag === 'view') {
      this.surveyId = this.$attrs.params.surveyId
      this.getDetails(this.surveyId, true)
    }
  },

  methods: {
    preQueryData1 () {
      return { 't.DATA_SOURCES': 'IMAGE', 't.STATUS': 'APPROVED' }
    },
    getDetails (id, flag = false) {
      this.$http({
        url: '/api-base/base/surveyheader/get?id=' + id,
        method: 'get',
        params: {}
      })
        .then(data => {
          if (data) {
            this.allParams = data.data
            delete this.allParams.deleteFlag
            if (flag) {
              this.businessId = id
            }
            this.$refs.sceneAttachment.loadFileInfo()
          }
        })
    },

    // 选择组织
    addOrgHandle (e, id, scope) {
      scope.buId = e ? e.organizationId : ''
      scope.buCode = e ? e.organizationCode : ''
      scope.buName = e ? e.organizationName : ''
    },

    handleVendorAdd (rows, scopeData, index) {
      this.$refs.vendorQuicksearch.selectedDataBak = []
      let selfRows = []
      selfRows = Object.prototype.toString.call(rows) === '[object Object]' ? [rows] : rows
      let tableData = this.$refs[this.gridId].tableData
      let tableCodeList = []
      for (let item of tableData) {
        item.vendorCode && tableCodeList.push(item.vendorCode)
      }
      selfRows = selfRows.filter(item => !tableCodeList.includes(item.companyCode))
      if (selfRows.length) this.vendorAdd(selfRows)
    },

    vendorAdd (rows) {
      if (!this.surveyId) {
        this.$message.error('请先暂存基本信息')
        return
      }
      this.$http({
        url: '/api-base/base/surveyscopevendor/addVendorSurvey',
        method: 'post',
        data: {
          surveyId: this.surveyId,
          surveyScopeVendorList: rows.map(item => ({
            vendorName: item.companyName,
            vendorCode: item.companyCode
          }))
        }
      })
        .then(data => {
          this.getQuerydata()
        })
    },

    handleVendorDelete () {
      if (!this.selectedRows.length) {
        this.$message.warning('请勾选需删除数据')
        return
      }
      this.confirmDelete(() => this.vendorDelete(this.selectedRows))
    },

    vendorDownloadTemplate () {
      downloadFileLinkByPost(
        '/api-base/base/surveyscopevendor/downLoadImportExcelTemplate',
        '供应商导入模板下载.xlsx',
      ).catch(err => {
        this.$message.error(err.message)
      })
    },

    vendorHandleSuccess () {
      this.getQuerydata()
    },

    selectChange (selections) {
      this.selectedRows = selections
    },

    getQuerydata (v) {
      this.queryParam = { ...v, surveyId: this.surveyId }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },

    // 已选供应商回调
    afterQuery (data) {
      this.selectList = data
    },

    confirmDelete (fn) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        fn && fn()
      })
    },

    vendorDelete (rows) {
      if (!this.surveyId) {
        this.$message.error('请先暂存基本信息')
        return
      }
      this.$http({
        url: '/api-base/base/surveyscopevendor/deleteVendorSurvey',
        method: 'post',
        data: {
          surveyId: this.surveyId,
          surveyScopeVendorList: rows.map(item => ({
            vendorName: item.vendorName,
            vendorCode: item.vendorCode
          }))
        }
      })
        .then(() => {
          this.getQuerydata()
        })
    },

    // 新增题目
    addTitle () {
      this.operationType = 'add'
      this.dialogFormVisible = true
      this.surveyQuestion = {
        questionType: 'S',
        selectionCount: 2,
        maxSelection: 1,
        employeeFlag: 'N',
        jumpEnd: 'N',
        emptyFlag: ''
      }
      this.surveySelectionList = [
        {
          selectionCode: 'A',
          selectionValue: null,
          selectionHolder: '选项1',
          jumpEnd: null,
          descriptionFlag: null
        },
        {
          selectionCode: 'B',
          selectionValue: null,
          selectionHolder: '选项2',
          jumpEnd: null,
          descriptionFlag: null
        }
      ]
    },

    jumpEndIndex (val, index) {
      this.$set(this.surveySelectionList[index], 'jumpEnd', val)
    },

    // 编辑题目
    editQuestion (item, index) {
      this.operationType = 'edit'
      this.operationIndex = index

      this.surveyQuestion = item.surveyQuestion
      this.surveySelectionList = []

      item.surveySelectionList.map((itm, index) => {
        this.$set(this.surveySelectionList, index, itm)
      })
      this.dialogFormVisible = true
    },

    // 复制题目
    copyQuestion (item) {
      this.$confirm('确定复制此题目？', {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.allParams.surveyQuestionDTOList.unshift(item)
        })
    },

    downloadTemplate () {
      downloadFileLink(
        '/api-base/base/surveyheader/exportSurveyQuestionExcelTemplate',
        `${parseTime(new Date())}.xlsx`,
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },

    handleSuccess (val) {
      if (val) {
        if (val.data.data && val.data.data.length > 0) {
          val.data.data.map(item => {
            this.allParams.surveyQuestionDTOList.push(item)
          })
        }
      }
    },

    // 上移题目
    moveUp (item, index) {
      let targe = this.allParams.surveyQuestionDTOList[index]
      this.allParams.surveyQuestionDTOList.splice(
        index,
        1,
        this.allParams.surveyQuestionDTOList[index - 1],
      )
      this.allParams.surveyQuestionDTOList.splice(index - 1, 1, targe)
    },

    // 下移题目
    moveDown (item, index) {
      let targe = this.allParams.surveyQuestionDTOList[index]
      this.allParams.surveyQuestionDTOList.splice(
        index,
        1,
        this.allParams.surveyQuestionDTOList[index + 1],
      )
      this.allParams.surveyQuestionDTOList.splice(index + 1, 1, targe)
    },

    // 删除题目
    delQuestion (index) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.allParams.surveyQuestionDTOList.splice(index, 1)
        })
    },

    jumpEndCheck (val) {
      if (val === 'N') {
        this.surveySelectionList.forEach(item => {
          item.jumpEnd = ''
        })
      }
    },

    // 新增保存
    comfirmSave () {
      if (!this.surveyQuestion.emptyFlag) {
        this.$message.error('请选择是否必填')
        return
      }
      if (!this.surveyQuestion.questionName) {
        this.$message.error('请输入题目')
        return
      }
      let index = this.surveySelectionList.findIndex(item => !item.selectionValue)
      if (index > -1 && this.surveyQuestion.questionType !== 'Q') {
        this.$message.error('请填写选项' + (index + 1))
        return
      }
      if (
        this.surveyQuestion.employeeFlag === 'Y' &&
        this.surveyQuestion.jobList &&
        this.surveyQuestion.jobList.length === 0
      ) {
        this.$message.error('请选择员工调研范围')
        return
      }

      if (this.surveyQuestion.jumpEnd === 'Y' && this.surveySelectionList.length > 0) {
        let surveyJumpEnd = this.surveySelectionList.filter(item => item.jumpEnd === 'Y')
        if (surveyJumpEnd && surveyJumpEnd.length === 0) {
          this.$message.error('请选择选项跳转到末尾')
          return
        }
      }
      // 校验新增时是否已存在相同的题目
      let questionNameList = []
      let questionNameListCheck = false
      if (
        this.operationType === 'add' &&
        this.allParams.surveyQuestionDTOList &&
        this.allParams.surveyQuestionDTOList.length > 0
      ) {
        questionNameList = this.allParams.surveyQuestionDTOList.map(v => {
          if (v.surveyQuestion && Object.keys(v.surveyQuestion).length > 0) {
            return {
              questionName: v.surveyQuestion.questionName,
              questionType: v.surveyQuestion.questionType,
              employeeFlag: v.surveyQuestion.employeeFlag
            }
          }
        })
        if (questionNameList && questionNameList.length > 0) {
          questionNameList.map(v => {
            if (
              v.questionName === this.surveyQuestion.questionName &&
              v.questionType === this.surveyQuestion.questionType &&
              v.employeeFlag === this.surveyQuestion.employeeFlag
            ) {
              this.$message.warning('已有该题目')
              questionNameListCheck = true
            }
          })
        }
      }
      if (questionNameListCheck) return false
      if (this.operationType == 'add') {
        this.allParams.surveyQuestionDTOList.push({
          surveyQuestion: JSON.parse(JSON.stringify(this.surveyQuestion)),
          jobList: this.surveyQuestion.jobList,
          surveySelectionList: JSON.parse(JSON.stringify(this.surveySelectionList))
        })
      } else if (this.operationType == 'edit') {
        this.allParams.surveyQuestionDTOList.splice(this.operationIndex, 1, {
          surveyQuestion: JSON.parse(JSON.stringify(this.surveyQuestion)),
          jobList: this.surveyQuestion.jobList,
          surveySelectionList: JSON.parse(JSON.stringify(this.surveySelectionList))
        })
      }
      this.dialogFormVisible = false
    },

    // 清空已选供应商
    clearAll () {
      this.$confirm('是否清除所有已选供应商', {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          if (!this.surveyId) return
          this.$http({
            url: '/api-base/base/surveyscopevendor/deleteVendorSurvey',
            method: 'get',
            params: {
              id: this.surveyId
            }
          })
            .then(() => {
              this.$nextTick(() => {
                this.$refs[this.gridId].query()
                this.$refs[this.gridId1].query()
              })
            })
        })
    },

    // 预览
    preview () {
      if (!this.allParams.surveyTitle) {
        this.$message.error('标题不能为空')
        return
      }
      if (!this.allParams.surveyQuestionDTOList.length) {
        this.$message.error('题目列表不能为空')
        return
      }
      let tab = {
        component: surveyPreview,
        params: {
          tabName: 'surveyPreview' + this.surveyId,
          row: this.allParams
        },
        title: () => '问卷调查预览',
        name: 'surveyPreview' + this.surveyId
      }
      this.$emit('tab-add', tab)
    },

    // 反馈结果
    toResult () {
      let tab = {
        component: surveyResult,
        params: {
          tabName: 'surveyResult' + this.surveyId,
          row: this.allParams
        },
        title: () => '问卷调查反馈结果',
        name: 'surveyResult' + this.surveyId
      }
      this.$emit('tab-add', tab)
    },

    // 暂存
    saveInfo () {
      let getCheckDuplicateTopic = this.checkDuplicateTopic() || ''
      if (getCheckDuplicateTopic) {
        this.$message.warning('问卷详情含有重复的题目')
        return false
      }
      this.$http({
        url: '/api-base/base/surveyheader/addOrUpdate',
        method: 'POST',
        data: this.allParams,
        loading: true
      })
        .then(data => {
          if (data) {
            this.$message.success(this.$t('common.successSave'))
            this.surveyId = data.data
            this.curOpt = 'edit'
            this.getDetails(data.data)
          }
        })
    },

    checkDuplicateTopic () {
      // 判定问卷调查是否含有重复的题目
      let questionNameList = []
      let questionNameListNewArr = []
      if (this.allParams.surveyQuestionDTOList && this.allParams.surveyQuestionDTOList.length > 0) {
        questionNameList = this.allParams.surveyQuestionDTOList.map(v => {
          if (v.surveyQuestion && Object.keys(v.surveyQuestion).length > 0) {
            return {
              questionName: v.surveyQuestion.questionName,
              questionType: v.surveyQuestion.questionType,
              employeeFlag: v.surveyQuestion.employeeFlag
            }
          }
        })
        questionNameListNewArr = uniqWith(questionNameList, isEqual)
        return questionNameListNewArr.length !== questionNameList.length
      }
    },

    submitted () {
      let getCheckDuplicateTopic = this.checkDuplicateTopic() || ''
      if (getCheckDuplicateTopic) {
        this.$message.warning('问卷详情含有重复的题目')
        return false
      }
      this.allParams.surveyQuestionDTOList.forEach(item => {
        item.jobList = item.surveyQuestion.jobList || []
      })
      this.$refs.info.validate(valid => {
        if (valid) {
          if (!this.selectList.length) {
            this.$message.error('已选供应商不能为空')
            return
          }
          this.$refs.infoDetail.validate(valid => {
            if (!this.allParams.surveyQuestionDTOList.length) {
              this.$message.error('题目列表不能为空')
              return
            }
            if (valid) {
              this.$http({
                url: '/api-base/base/surveyheader/submitted',
                method: 'POST',
                data: this.allParams,
                loading: true
              })
                .then(data => {
                  if (data) {
                    this.$message.success(this.$t('common.successPublish'))
                    this.$emit('tab-remove', this.$attrs.params.tabName)
                    this.__setTabTodo('SurveyList.getQuerydata')
                  }
                })
            } else {
              this.$message.error(this.$t('vendorMod.pleasefinishRequired'))
              return false
            }
          })
        } else {
          this.$message.error(this.$t('vendorMod.pleasefinishRequired'))
          return false
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
:deep(.main-header) {
  padding: 0 0 16px;
}
.question-list {
  margin-top: 30px;
  padding-bottom: 30px;
  border-top: 1px solid #e6e9ec;
}
.question-listBOx {
  border-bottom: 1px solid #e6e9ec;
  padding: 16px 0 40px;
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
.select-box {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  :deep(.el-radio) {
    width: 80px;
    text-align: right;
    margin-right: 12px;
  }
  :deep(.el-radio__input.is-disabled + span.el-radio__label) {
    color: #606266;
  }
  :deep(.el-checkbox__label) {
    padding-right: 10px;
  }
}
.listBOx-q {
  display: flex;
  align-items: center;
}
.mg-l-20 {
  padding-left: 20px;
}
.tips {
  color: red;
}
</style>
