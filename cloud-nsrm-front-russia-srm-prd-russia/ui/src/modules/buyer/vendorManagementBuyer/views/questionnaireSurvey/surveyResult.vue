<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <el-tabs type="border-card">
        <el-tab-pane :label="$t('dashboard.totalCount')">
          <el-collapse v-model="activeDims">
            <el-collapse-item :title="$t('survey.FeedbackSupplierList')" name="1">
              <FormWrapper :form-array="preArr" @getFormData="getQuerydata" />
              <MainHeader :l-span="22" :r-span="2">
                <template slot="left">
                  <AuthorityButton type="primary" @click="exportFile">
                    {{ $t('common.export') }}
                  </AuthorityButton>
                  <el-button @click="showFile">
                    {{ $t('perfMod.accessory') }}
                  </el-button>
                </template>
              </MainHeader>
              <TableView
                :ref="gridId"
                :table-data="tableData"
                :table-header="tableHeader"
                :page-size="pageSize"
                table-height="400px"
                :pre-query-data="queryParam"
                :checkbox="false"
                url="/api-base/base/surveyresult/listFeedbackVendor"
              />
              <el-dialog :title="$t('perfMod.accessory')" :visible.sync="centerDialogVisible" width="50%" center>
                <span>{{ $t('survey.list4') }}</span>
                <TableView
                  :ref="gridIdFile"
                  :table-data="showFileData"
                  :table-header="fileHeader"
                  :page-size="pageSize"
                  table-height="400px"
                  :check-change="handleSelectionChange"
                  :pre-query-data="queryParamFile"
                  :checkbox="true"
                  url="/api-base/base/surveyscopevendor/queryVendorAllFile"
                >
                  <template #downloadFileName="{ scope }">
                    <SrmCommonFile
                      :default-file="{
                        fileId: scope.row.fileUploadId,
                        fileName: scope.row.fileName
                      }"
                      :readonly="true"
                    />
                  </template>
                </TableView>
                <span slot="footer" class="dialog-footer">
                  <el-button @click="centerDialogVisible = false">{{ $t('vendorMod.relegation.abolish') }}</el-button>
                  <el-button type="primary" @click="batchDownload">{{ $t('common.download') }}</el-button>
                </span>
              </el-dialog>
            </el-collapse-item>
            <el-collapse-item :title="$t('survey.StatisticsOfFeedbackResults')" name="2">
              <div class="surveyTitle">
                {{ surveyTitle }}
              </div>
              <div class="question-list">
                <el-row :gutter="32" class="question-listBOx">
                  <el-col :span="12" class="result-left">
                    <strong>供应商反馈数量(已反馈数量/总数量) {{ backCount }}/{{ totalCount }}</strong>
                    <el-button type="primary" @click="preview">
                      {{ $t('survey.ExportStatistics') }}
                    </el-button>
                  </el-col>
                  <el-col :span="12">
                    <PieChart :chart-data="chartData" />
                  </el-col>
                </el-row>
                <el-row
                  v-for="(item, index) in surveyQuestionDTOList"
                  :key="index + 'pp'"
                  :gutter="32"
                  class="question-listBOx"
                >
                  <el-col :span="12">
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
                          <el-input
                            v-if="itm.descriptionFlag === 'Y'"
                            v-model="itm.description"
                            style="margin-bottom:10px;display:block;"
                            type="textarea"
                            disabled
                            :placeholder="$t('survey.selection')"
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
                              type="textarea"
                              disabled
                              :placeholder="$t('survey.selection')"
                            />
                          </el-col>
                        </el-row>
                      </el-checkbox-group>
                    </div>
                    <div v-if="item.surveyQuestion.questionType == 'Q'" class="mg-l-20 listBOx-q">
                      <div style="color: red">
                        {{ $t('survey.Please') }}
                      </div>
                    </div>
                  </el-col>
                  <el-col v-if="item.surveyQuestion.questionType !== 'Q'" :span="12">
                    <PieChart :chart-data="item.chartData" />
                  </el-col>
                </el-row>
              </div>
            </el-collapse-item>
          </el-collapse>
          <CToolbar>
            <template slot="right">
              <el-button @click="backTo">
                {{ $t('vendorMod.goBack') }}
              </el-button>
            </template>
          </CToolbar>
        </el-tab-pane>
      </el-tabs>
    </el-main>
  </el-container>
</template>

<script>
import CToolbar from 'lib@/components/c-toolbar'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import TableView from 'lib@/components/Table/TableView'
import { downloadWithParam, downloadFileLinkByPost } from 'lib@/utils/file'
import surveyAnswer from './surveyAnswer'
import { adaptDictData, parseTime } from '@/utils'
import { getDictItemList } from '@/api/common'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import PieChart from './chart/PieChart'

export default {
  name: 'SurveyResult',

  components: {
    FormWrapper,
    MainHeader,
    TableView,
    CToolbar,
    PieChart
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
      dict: {
        RESULT_FLAG: []
      },
      centerDialogVisible: false,
      activeDims: ['1', '2', '3', '4'],
      surveyId: '',
      surveyTitle: '',
      surveyQuestionDTOList: [],
      fileURL: '',
      preArr: [
        {
          prop: 'vendorName',
          label: () => this.$t('common.vendorName'), // '供应商名称'
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_display_buyer'
        },
        {
          prop: 'resultFlag',
          label: '反馈状态', // '反馈状态'
          type: 'dict',
          code: 'RESULT_FLAG'
        }
      ],
      gridId: 'list',
      gridIdFile: 'list',
      tableData: [],
      showFileData: [],
      tableHeader: [],
      fileHeader: [],
      pageSize: 15,
      queryParam: {},
      chartData: {},
      backCount: 0,
      totalCount: 0,
      multipleSelection: [],
      queryParamFile: {}
    }
  },

  mounted () {
    this.$bus.$on('surveyResultDoLayout', flag => {
      if (flag) {
        this.$refs[this.gridId].query()
        this.$refs[this.gridId].doLayout()
      }
    })
    this.fetchDictData()
    this.surveyId = this.$attrs.params.row.surveyId
    this.surveyTitle = this.$attrs.params.row.surveyTitle
    this.$nextTick(() => {
      this.getQuerydata()
    })
    this.tableHeader = [
      {
        prop: 'surveyTitle',
        label: '问卷标题'
      },
      {
        prop: 'vendorName',
        label: '供应商名称'
      },
      {
        prop: 'vendorCode',
        label: '供应商编码'
      },
      {
        prop: 'resultFlag',
        label: '反馈状态',
        formattor: val => {
          return this.$getDictLabelByValue(this.dict.RESULT_FLAG, val)
        },
        dataType: 'dict',
        code: 'RESULT_FLAG'
      },
      {
        prop: 'feedbackTime',
        label: '反馈时间',
        formattor: (val, item) => {
          return item.resultFlag === 'N' ? '-' : val
        }
      },
      {
        prop: 'operation',
        label: this.$t('bidMod.operation'),
        width: 120,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: row => this.editTab('edit', row),
            formattor: () => '问卷报告'
          }
        ]
      }
    ]
    this.fileHeader = [
      {
        prop: 'fileName',
        label: '名称',
        showType: 'slot',
        slot: 'downloadFileName'
      },
      {
        prop: 'vendorCode',
        label: '上传供方编码'
      },
      {
        prop: 'vendorName',
        label: '上传供方名称'
      },
      {
        prop: 'uploadDate',
        label: '上传时间'
      }
    ]
    this.queryFeedbackResult(this.surveyId)
    this.queryFeedbackChartResult(this.surveyId)
  },

  beforeDestroy () {
    this.$bus.$off('surveyResultDoLayout')
  },

  methods: {
    dolayout () {
      this.$refs[this.gridId].doLayout()
    },

    async fetchDictData () {
      let keyList = Object.keys(this.dict)
      let res = await getDictItemList(
        keyList.map(key => {
          return { dictCode: key }
        }),
      )
      if (res.data) {
        keyList.forEach((key, index) => {
          this.dict[key] = adaptDictData(res.data[index][key])
        })
      }
    },

    getQuerydata (v) {
      this.queryParam = { ...v, surveyId: this.surveyId }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },

    exportFile () {
      downloadFileLinkByPost(
        '/api-base/base/surveyscopevendor/feedbackResultExport',
        `供应商反馈列表${parseTime(new Date())}.xlsx`,
        { surveyId: this.surveyId },
      ).catch(err => {
        this.$message.error(err.message)
      })
    },

    batchDownload () {
      if (!this.multipleSelection || !this.multipleSelection.length) {
        this.$message.error('请选择需要下载的附件')
        return
      }
      let promiseArr = []
      for (let item of this.multipleSelection) {
        if (item.fileUploadId) {
          promiseArr.push({
            id: item.fileUploadId,
            name: item.fileName
          })
        }
      }
      if (!promiseArr.length) {
        this.$message.error('选择数据没有附件')
        return
      }
      Promise.all(promiseArr.map(item => downloadWithParam(item.id, item.name))).catch(err =>
        this.$message.error(err.message),
      )
    },

    preview () {
      downloadFileLinkByPost(
        '/api-base/base/surveyresult/resultExport',
        `统计结果${parseTime(new Date())}.xlsx`,
        { surveyId: this.surveyId },
      ).catch(err => {
        this.$message.error(err.message)
      })
    },

    showFile () {
      this.queryParamFile = {
        surveyId: this.surveyId || ''
      }
      this.$nextTick(() => {
        this.$refs[this.gridIdFile].query()
      })

      this.centerDialogVisible = true
    },

    handleSelectionChange (val) {
      this.multipleSelection = val
    },

    queryFeedbackResult (id) {
      this.$http({
        url: '/api-base/base/surveyresult/queryFeedbackResult',
        method: 'post',
        data: { surveyId: id }
      })
        .then(data => {
          if (data) {
            let params = data.data
            this.backCount = params.backCount
            this.totalCount = params.totalCount
            this.chartData = {
              legend: ['未反馈', '已反馈'],
              seriesData: [
                { name: '未反馈', value: params.totalCount - params.backCount },
                { name: '已反馈', value: params.backCount }
              ],
              color: [
                '#5470C6',
                '#91CC75',
                '#FAC858',
                '#EE6666',
                '#73C0DE',
                '#3BA272',
                '#FC8452',
                '#9A60B4',
                '#73c0de'
              ],
              seriesOpts: {
                radius: [0, 70],
                center: ['50%', '40%'],
                roseType: ''
              }
            }
          }
        })
    },

    queryFeedbackChartResult (id) {
      this.$http({
        url: '/api-base/base/surveyresult/queryFeedbackChartResult',
        method: 'post',
        data: { surveyId: id }
      })
        .then(data => {
          if (data) {
            this.surveyQuestionDTOList = data.data
            this.surveyQuestionDTOList.map(item => {
              item.chartData = {
                legend: [],
                seriesData: [],
                color: [
                  '#5470C6',
                  '#91CC75',
                  '#FAC858',
                  '#EE6666',
                  '#73C0DE',
                  '#3BA272',
                  '#FC8452',
                  '#9A60B4',
                  '#73c0de'
                ],
                seriesOpts: {
                  radius: [0, 70],
                  center: ['50%', '40%'],
                  roseType: ''
                }
              }
              item.surveySelectionList.map(itm => {
                item.chartData.legend.push(itm.selectionCode)
                item.chartData.seriesData.push({
                  value: itm.feedBackCount,
                  name: itm.selectionCode
                })
              })
            })
          }
        })
    },

    editTab (type, row) {
      if (type === 'edit') {
        let tab = {
          component: surveyAnswer,
          params: {
            tabName: 'surveyAnswer' + this.surveyId,
            surveyId: this.surveyId,
            row
          },
          title: () => '问卷调查报告',
          name: 'surveyAnswer' + this.surveyId
        }
        this.$emit('tab-add', tab)
      }
    },
    backTo () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
    }
  }
}
</script>

<style lang="scss" scoped>
.surveyTitle {
  height: 36px;
  line-height: 36px;
  text-align: center;
  font-size: 14px;
  color: #1c2438;
  font-weight: 700;
}
.question-list {
  padding-bottom: 30px;
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
}
.result-left {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 250px;
}
.listBOx-q {
  display: flex;
  align-items: center;
}
.mg-l-20 {
  padding-left: 20px;
}
</style>
