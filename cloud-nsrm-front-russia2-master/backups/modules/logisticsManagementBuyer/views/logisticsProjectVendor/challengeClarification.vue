<template>
  <el-container
    class="flex-container-notab the_challengeClarification_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :select-dictionary="selectDictionary"
        :form-array="preArr"
        @getFormData="queryClick"
      />
      <p>
        <span class="table-title">
          <!-- 质疑列表 -->
          {{ $t("bidMod.challengeList") }}
        </span>
        <el-button
          style="margin-left:11px;"
          type="primary"
          @click="editTab('add')"
        >
          {{ $t("bidMod.addChallenge") }}
        </el-button>
      </p>
      <table-view
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :check-change="checkChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :url="apiList.bidingQuestion"
        checkbox
      />
      <p>
        <span class="table-title">
          <!-- 澄清列表 -->
          {{ $t("bidMod.clarificationList") }}
        </span>
      </p>

      <table-view
        :ref="gridId2"
        :table-data="tableData2"
        :table-header="tableHeader2"
        :current-change="handleCurrentChange2"
        :page-size="pageSize"
        :pre-query-data="queryParam2"
        :url="apiList.bidingAnswer"
      />
      <!-- 弹框区域-->
      <srm-dialog
        top="10vh"
        :title="$t('bidMod.addChallenge')"
        size="large"
        :visible.sync="dialogFormVisible"
        :close-on-click-modal="false"
      >
        <div class="the_clarification_dialog">
          <el-form
            ref="form"
            :model="form"
            label-position="top"
            label-width="80px"
            :rules="rules"
          >
            <el-row
              type="flex"
              :gutter="50"
            >
              <el-col :span="8">
                <el-form-item
                  :label="$t('bidMod.questionNum')"
                  :label-width="formLabelWidth"
                >
                  <el-input
                    v-model="form.questionNum"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item
                  :label="$t('bidMod.questionStatus')"
                  :label-width="formLabelWidth"
                >
                  <el-select
                    v-model="form.questionStatus"
                    disabled
                  >
                    <el-option
                      v-for="item in questionStatus"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item
                  prop="bidingNum"
                  :label="$t('bidMod.bidingName')"
                  :label-width="formLabelWidth"
                >
                  <el-select
                    v-model="form.bidingNum"
                    filterable
                    remote
                    :placeholder="$t('bidMod.msgKeyword')"
                    :remote-method="remoteMethod"
                    clearable
                    automatic-dropdown
                    @change="elSelectChange"
                  >
                    <el-option
                      v-for="item in options"
                      :key="item.id"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item
                  :label="$t('bidMod.bidingNum')"
                  :label-width="formLabelWidth"
                >
                  <el-input
                    v-model="form.bidingNum"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <h3>
              <!-- 质疑信息 -->
              {{ $t("bidMod.questionInfo") }}
            </h3>
            <el-row
              type="flex"
              :gutter="50"
            >
              <el-col :span="8">
                <el-form-item
                  :label="$t('bidMod.questionTitle')"
                  prop="questionTitle"
                  :label-width="formLabelWidth"
                >
                  <el-input v-model="form.questionTitle" />
                </el-form-item>
              </el-col>
              <el-col :span="8" />
              <el-col :span="8" />
              <el-col :span="8" />
            </el-row>
          </el-form>
          <h3>
            <!-- 附件 -->
            {{ $t("bidMod.attachment") }}
            <el-button
              type="primary"
              @click="addOne"
            >
              {{
                $t("common.add")
              }}
            </el-button>
          </h3>
          <el-table
            :data="fileList"
            style="width: 100%"
            border
            height="121px"
          >
            <el-table-column
              align="center"
              type="index"
              width="50"
            />
            <el-table-column
              align="center"
              prop="fileName"
              :label="$t('bidMod.attachmentName')"
            >
              <template slot-scope="scope">
                <SrmCommonFile
                  :extra-data="fileInfo"
                  :default-file="{
                    fileId: scope.row.docId,
                    fileName: scope.row.fileName
                  }"
                  :readonly="false"
                  @on-change="({file}) => handleUploadSuccess(file,scope.row)"
                />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="comments"
              :label="$t('bidMod.remark')"
            >
              <template slot-scope="scope">
                <el-input v-model="scope.row.comments" />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="operation"
              :label="$t('common.operation')"
              width="100"
            >
              <template slot-scope="scope">
                <el-button
                  :disabled="isLock"
                  type="text"
                  @click="deleOne(scope.$index, scope.row)"
                >
                  {{ $t("common.delete") }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <h3>
            <!-- 质疑内容 -->
            {{ $t("bidMod.questionContent") }}
          </h3>
          <quill-editor
            v-if="dialogFormVisible"
            ref="myQuillEditor"
            v-model="mainClarification"
            :options="editorOption"
          />
        </div>
        <div slot="footer">
          <el-button
            type="primary"
            @click="backOne"
          >
            {{
              $t("common.cancel")
            }}
          </el-button>
          <el-button
            type="primary"
            :disabled="isLock"
            @click="saveOne"
          >
            {{ $t("common.staging") }}
          </el-button>
          <el-button
            type="primary"
            :disabled="isLock"
            @click="publishOne"
          >
            {{ $t("common.publish") }}
          </el-button>
        </div>
      </srm-dialog>

      <srm-dialog
        :title="$t('bidMod.clarifySheet')"
        size="large"
        :visible.sync="showAnswerInfo"
        :close-on-click-modal="false"
      >
        <div class="the_clarification_dialog">
          <el-form
            ref="form2"
            :model="form"
            label-position="top"
            label-width="80px"
          >
            <el-row
              type="flex"
              :gutter="50"
            >
              <el-col :span="8">
                <el-form-item
                  :label="$t('bidMod.answerNum')"
                  :label-width="formLabelWidth"
                >
                  <el-input
                    v-model="answerForm.answerNum"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item
                  :label="$t('bidMod.bidingName')"
                  :label-width="formLabelWidth"
                >
                  <el-input
                    v-model="answerForm.bidingName"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item
                  :label="$t('bidMod.answerTitle')"
                  :label-width="formLabelWidth"
                >
                  <el-input
                    v-model="answerForm.answerTitle"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item
                  :label="$t('bidMod.bidingNum')"
                  :label-width="formLabelWidth"
                >
                  <el-input
                    v-model="answerForm.bidingNum"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <h3>
              <!-- 项目信息变更 -->
              {{ $t("bidMod.proInfoChange") }}
            </h3>
            <el-row
              type="flex"
              :gutter="50"
            >
              <el-col :span="8">
                <el-form-item
                  :label="$t('bidMod.regisDeadline')"
                  :label-width="formLabelWidth"
                >
                  <el-date-picker
                    v-model="answerForm.enrollEndDatetime"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item
                  :label="$t('bidMod.bidStartAfterChange')"
                  :label-width="formLabelWidth"
                >
                  <el-date-picker
                    v-model="answerForm.bidingStartDatetime"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item
                  :label="$t('bidMod.bidingSiteAfterChange')"
                  :label-width="formLabelWidth"
                >
                  <el-input
                    v-model="answerForm.bidingSite"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="8" />
            </el-row>
          </el-form>
          <h3>
            <!-- 附件 -->
            {{ $t("bidMod.attachment") }}
          </h3>
          <el-table
            :data="fileList2"
            style="width: 100%"
            border
            height="121px"
          >
            <el-table-column
              align="center"
              type="index"
              width="50"
            />
            <el-table-column
              align="center"
              prop="fileName"
              :label="$t('bidMod.attachmentName')"
            >
              <template slot-scope="scope">
                <SrmCommonFile
                  :extra-data="fileInfo"
                  :default-file="{
                    fileId: scope.row.docId,
                    fileName: scope.row.fileName
                  }"
                  :readonly="false"
                  @on-change="({file}) => handleUploadSuccess(file,scope.row)"
                />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="comments"
              :label="$t('bidMod.remark')"
            >
              <template slot-scope="scope">
                <el-input v-model="scope.row.comments" />
              </template>
            </el-table-column>
          </el-table>
          <h3>
            <!-- 澄清内容 -->
            {{ $t("bidMod.clarifyContent") }}
          </h3>
          <quill-editor
            v-if="showAnswerInfo"
            ref="myQuillEditor"
            v-model="answerInfo"
            :options="editorOption"
          />
        </div>
        <div slot="footer">
          <el-button
            type="primary"
            :disabled="!canAccept"
            @click="acceptAnswer"
          >
            {{ $t("bidMod.acceptClarify") }}
          </el-button>
        </div>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime, adaptDictData } from '@/utils'
import { quillEditor } from 'vue-quill-editor'
import { getDictItemList } from '@/api/common'

import 'quill/dist/quill.core.css'
import 'quill/dist/quill.snow.css'
import 'quill/dist/quill.bubble.css'

export default {
  name: 'VendorChallengeClarification',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    quillEditor
  },
  data () {
    return {
      fileList2: [],
      showAnswerInfo: false,
      answerInfo: null,
      apiList: {
        save: '/api-pd/bidingQuestion/save',
        submit: '/api-pd/bidingQuestion/submit',
        clarified: '/api-pd/bidingQuestion/clarified',
        bidingQuestion: '/api-pd/bidingQuestion/listPage',
        bidingAnswer: '/api-pd/bidingAnswer/listPage',
        getBidingQuestionById:
          '/api-pd/bidingQuestion/getBidingQuestionById',
        getBidingAnswerById:
          '/api-pd/bidingAnswer/getBidingAnswerById',
        delete: '/api-pd/supplierCooperate/vendorFile/delete',
        supplierBiding:
          '/api-pd/supplierCooperate/supplierBiding/listPage',
        bidingGet: '/api-pd/bidInitiating/biding/get',
        deleteItem: '/api-pd/bidingQuestion/delete',
        rejectItem: '/api-pd/bidingQuestion/withDraw'
      },
      isLock: false,
      editorOption: {
        theme: 'snow',
        placeholder: this.$t('dataConfMod.msgNoticeDetail'),
        modules: {
          toolbar: [
            ['bold', 'italic', 'underline', 'strike'],
            ['blockquote', 'code-block'],
            [{ header: 1 }, { header: 2 }],
            [{ list: 'ordered' }, { list: 'bullet' }],
            [{ script: 'sub' }, { script: 'super' }],
            [{ indent: '-1' }, { indent: '+1' }],
            [{ direction: 'rtl' }],
            [{ size: ['small', false, 'large', 'huge'] }],
            [{ header: [1, 2, 3, 4, 5, 6, false] }],
            [{ font: [] }],
            [{ color: [] }, { background: [] }],
            [{ align: [] }],
            ['clean'],
            // ['link', 'image', 'video']
            ['link']
          ]
        }
        // Some Quill options...
      },
      // 文件上传配置信息
      fileInfo: {
        uploadType: 'FASTDFS', // 固定参数
        sourceType: 'WEB_APP', // 固定参数
        fileModular: 'sup', // 文件所属模块 -》基础模块
        fileFunction: 'inquiry', // 文件所属功能
        fileType: 'images' // 文件所属类型
      },
      name: 'challengeTable',
      mainClarification: '',
      pageSize: 15,
      gridId: 'list',
      currentRow: null,
      tableHeader: [],
      tableData: [],
      gridId2: 'list2',
      currentRow2: null,
      tableHeader2: [],
      tableData2: [],
      fileList: [],
      form: {
        expertName: '',
        expertType: '',
        expertID: '',
        telephone: '',
        email: '',
        status: '',
        enableDate: '',
        disableDate: ''
      },
      dialogFormVisible: false,
      formLabelWidth: '140px',
      isActive: false,
      selectDictionary: {},
      options: [],
      queryParam: {},
      queryParam2: {},
      answerForm: {},
      isModify: false,
      questionStatus: [],
      answerStatus: [],
      canAccept: false,
      rules: {
        bidingNum: { required: true, message: this.$t('bidMod.msgSelProject') },
        questionTitle: {
          message: this.$t('bidMod.msgInputTitle'),
          required: true
        }
      },
      preArr: [
        { prop: 'bidingNum', label: () => this.$t('bidMod.bidingNumCla') },
        { prop: 'bidingName', label: () => this.$t('bidMod.bidingNameCla') },
        { prop: 'questionTitle', label: () => this.$t('bidMod.questionTitle') },
        { prop: 'questionNum', label: () => this.$t('bidMod.questionNum') },
        {
          prop: 'questionStatus',
          label: () => this.$t('bidMod.questionStatus'),
          type: 'select'
        },
        { prop: 'answerTitle', label: () => this.$t('bidMod.answerTitle') },
        { prop: 'answerNum', label: () => this.$t('bidMod.answerNum') },
        {
          prop: 'answerStatus',
          label: () => this.$t('bidMod.answerStatus'),
          type: 'select'
        }
      ]
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'questionNum',
        label: () => this.$t('bidMod.questionNum'),
        width: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.currentRow = row
          this.editTab('edit', row)
        }.bind(this)
      },
      {
        prop: 'questionTitle',
        label: () => this.$t('bidMod.questionTitle'),
        width: 150
      },
      // { prop: "clarifyVendor", label: "质疑供应商", width: 150 },
      {
        prop: 'questionStatus',
        label: () => this.$t('bidMod.questionStatus'),
        formattor: val => {
          if (this.questionStatus && this.questionStatus.length) {
            return (
              (this.questionStatus.find(i => i.value === val) || {}).label ||
              val
            )
          }
          return val
        },
        width: 100
      },
      {
        prop: 'submitTime',
        label: () => this.$t('bidMod.releaseDatetime'),
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'bidingNum',
        label: () => this.$t('bidMod.bidingNumCla'),
        width: 150
      },
      {
        prop: 'bidingName',
        label: () => this.$t('bidMod.bidingNameCla'),
        width: 150
      },
      { prop: 'rejectReason', label: () => this.$t('bidMod.rejectReason1') },
      {
        prop: 'operation',
        label: () => this.$t('bidMod.operation'),
        width: 150,
        showType: 'buttons',
        btnStyle: 'text',
        fixed: 'right',
        buttons: [
          {
            callback: function (row) {
              this.deleteItem(row)
            }.bind(this),
            show: ({ questionStatus }) => questionStatus === 'DRAFT',
            formattor (val) {
              return _this.$t('common.delete')
            }
          },
          {
            callback: function (row) {
              this.rejectItem(row)
            }.bind(this),
            show: ({ questionStatus }) => questionStatus === 'SUBMITTED',
            formattor (val) {
              return _this.$t('bidMod.withdraw')
            }
          }
        ]
      }
    ]
    this.tableHeader2 = [
      {
        prop: 'answerNum',
        label: () => this.$t('bidMod.answerNum'),
        width: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.currentRow = row
          this.reviewInfo(row)
        }.bind(this)
      },
      {
        prop: 'answerTitle',
        label: () => this.$t('bidMod.answerTitle'),
        width: 150
      },
      // { prop: "vendorId", label: "质疑供应商", width: 150 },
      {
        prop: 'answerStatus',
        label: () => this.$t('bidMod.answerStatus'),
        width: 100,
        formattor: val => {
          if (this.answerStatus && this.answerStatus.length) {
            return (
              (this.answerStatus.find(i => i.value === val) || {}).label || val
            )
          }
          return val
        }
      },
      {
        prop: 'submitTime',
        label: () => this.$t('bidMod.releaseDatetime'),
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'questionNum',
        label: () => this.$t('bidMod.sourceType'),
        width: 150,
        formattor: val => (val || _this.$t('bidMod.tendererIssued'))
      },
      {
        prop: 'bidingNum',
        label: () => this.$t('bidMod.bidingNumCla'),
        width: 150
      },
      { prop: 'bidingName', label: () => this.$t('bidMod.bidingNameCla') }
    ]
    this.initDictionary()
    this.getQuerydata()
    this.getQuerydata2({ answerStatus: 'ISSUED' })
  },
  mounted () {
    const { bidingId, bidingName, bidingCode } = this.$route.params || {}
    if (bidingId && bidingName && bidingCode) {
      this.dialogFormVisible = true
      this.form = { bidingId, bidingName, bidingCode }
      this.queryCompanyList({ bidingNum: bidingCode })
    }
  },
  methods: {
    elSelectChange (val) {
      const company = this.options.find(i => i.value === val)
      const { id, label } = company || {}
      this.form = { ...this.form, bidingId: id, bidingName: label }
    },
    queryCompanyList ({ bidingName = '', bidingNum = '' }) {
      this.$http({
        url: this.apiList.supplierBiding,
        method: 'POST',
        data: { bidingName, bidingNum, pageSize: 999, pageNum: 1 }
      }).then(res => {
        this.options = res.data.list.map(i => ({
          id: i.bidingId,
          value: i.bidingNum,
          label: i.bidingName
        }))
      })
    },
    remoteMethod (bidingName) {
      this.queryCompanyList({ bidingName })
    },
    reviewInfo ({ answerId }) {
      this.showAnswerInfo = true
      this.$http({
        url: this.apiList.getBidingAnswerById,
        method: 'GET',
        params: { answerId }
      }).then(res => {
        const { files, answerComtent, ...rest } = res.data
        this.answerForm = { ...rest }
        this.fileList2 = files
        this.answerInfo = answerComtent
        this.canAccept = rest.answerStatus === 'SUBMITTED'
        this.$http({
          url: this.apiList.bidingGet,
          method: 'GET',
          params: { id: rest.bidingId }
        }).then(res => {
          const {
            enrollEndDatetime,
            bidingStartDatetime,
            bidingSite
          } = res.data
          this.answerForm = {
            ...this.answerForm,
            enrollEndDatetime,
            bidingStartDatetime,
            bidingSite
          }
        })
      })
    },
    initDictionary () {
      const params = ['QUERY_STATUS', 'CLARIFIED_STATUS'].map(i => ({
        dictCode: i
      }))
      getDictItemList(params).then(res => {
        const [QUERY_STATUS, CLARIFIED_STATUS] = res.data
        const answerStatus = adaptDictData(
          CLARIFIED_STATUS.CLARIFIED_STATUS.filter(
            i => i.dictItemCode !== 'DRAFT'
          )
        )
        const questionStatus = adaptDictData(QUERY_STATUS.QUERY_STATUS)
        this.answerStatus = answerStatus
        this.questionStatus = questionStatus
        this.selectDictionary = {
          answerStatus,
          questionStatus
        }
      })
    },
    queryClick (v = {}) {
      //  { prop: "bidingNum", label: "招标项目编号" },
      //   { prop: "bidingName", label: "招标项目名称" },
      //   { prop: "questionTitle", label: "质疑标题" },
      //   { prop: "questionNum", label: "质疑编号" },
      //   { prop: "questionStatus", label: "质疑状态", type: "select" },
      //   { prop: "answerTitle", label: "澄清标题" },
      //   { prop: "answerNum", label: "澄清编号" },
      //   { prop: "answerStatus", label: "澄清状态", type: "select" }
      // const { bidingNum, bidingName, questionTitle, questionNum, questionStatus, answerTitle, answerNum, answerStatus } = v;
      // if ()
      const { answerStatus = 'ISSUED', ...rest } = v
      this.getQuerydata2({ answerStatus, ...rest })
      this.getQuerydata(v)
    },
    checkChange (selection) {
      if (!selection.length) {
        return this.getQuerydata2({ answerStatus: 'ISSUED' })
      }
      this.getQuerydata2({
        answerStatus: 'ISSUED',
        questionIds: selection.map(i => i.questionId)
      })
    },
    getQuerydata (v) {
      console.log(v)
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    getQuerydata2 (v) {
      console.log(v)
      this.queryParam2 = v
      this.$nextTick(() => {
        this.$refs[this.gridId2].query()
      })
    },
    acceptAnswer () {
      this.$http({
        url: this.apiList.clarified,
        method: 'POST',
        data: { questionId: this.answerForm.questionId }
      }).then(res => {
        this.$message({ message: res.message, type: 'success' })
        this.showAnswerInfo = false
        this.queryClick()
      })
    },
    saveOne () {
      if (!this.mainClarification) {
        return this.$message({
          type: 'warning',
          message: this.$t('bidMod.msgInputQuestion')
        })
      }
      this.$refs.form.validate(status => {
        if (status) {
          const data = {
            questionComtent: this.mainClarification,
            files: this.fileList,
            ...this.form
          }
          this.$http({
            url: this.apiList.save,
            method: 'POST',
            data
          }).then(() => {
            this.$message({
              message: this.$t('common.successSave'),
              type: 'success'
            })
            this.queryClick()
            this.dialogFormVisible = false
          })
        }
      })
    },
    deleteItem ({ questionId }) {
      this.$confirm(
        this.$t('logisticsMod.msgPurchaseApply[34]'),
        this.$t('common.tips'),
        {
          confirmButtonText: this.$t('common.confirm'), // 确定
          cancelButtonText: this.$t('common.cancel'), // 取消
          type: 'warning'
        }
      )
        .then(() => {
          this.$http({
            url: this.apiList.deleteItem,
            method: 'GET',
            params: { id: questionId }
          }).then(() => {
            this.$message({
              type: 'success',
              message: this.$t('common.successDelete')
            })
            this.queryClick()
          })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: this.$t('common.cancelDelete')
          })
        })
    },
    rejectItem ({ questionId }) {
      this.$http({
        url: this.apiList.rejectItem,
        method: 'GET',
        params: { id: questionId }
      }).then(() => {
        this.$message({
          type: 'success',
          message: this.$t('common.successWithdraw')
        })
        this.queryClick()
      })
    },
    publishOne () {
      if (!this.mainClarification) {
        return this.$message({
          type: 'warning',
          message: this.$t('bidMod.msgInputQuestion')
        })
      }
      this.$refs.form.validate(status => {
        if (status) {
          const data = {
            questionComtent: this.mainClarification,
            files: this.fileList,
            ...this.form
          }
          this.$http({
            url: this.apiList.submit,
            method: 'POST',
            data
          }).then(() => {
            this.queryClick()
            this.$message({
              message: this.$t('common.successPublish'),
              type: 'success'
            })
            this.dialogFormVisible = false
          })
        }
      })
    },
    backOne () {
      this.dialogFormVisible = false
    },
    // 编辑tab
    editTab (type, row) {
      this.dialogFormVisible = true
      this.isModify = type === 'edit'
      if (type === 'add') {
        this.form = {}
        this.fileList = []
        this.mainClarification = null
        this.isLock = false
      } else {
        this.$http({
          url: this.apiList.getBidingQuestionById,
          method: 'GET',
          params: { questionId: row.questionId }
        }).then(res => {
          const { files, questionComtent, ...rest } = res.data
          this.queryCompanyList({ bidingNum: rest.bidingNum })
          this.form = { ...rest }
          this.mainClarification = questionComtent
          this.fileList = files
          this.isLock = rest.questionStatus !== 'DRAFT'
        })
      }
    },
    addOne () {
      this.fileList.push({
        docId: '',
        fileName: '',
        comments: ''
      })
    },
    deleOne (index, row) {
      if (!row.vendorFileId) {
        this.fileList.splice(index, 1)
        return
      }
      this.$confirm(this.$t('bidMod.ifDeleteFile'), this.$t('common.tips'), {
        confirmButtonText: this.$t('common.confirm'), // 确定
        cancelButtonText: this.$t('common.cancel'), // 取消
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: this.apiList.delete,
            method: 'GET',
            params: { vendorFileId: row.vendorFileId }
          }).then(() => {
            this.fileList.splice(index, 1)
            this.$message({
              type: 'success',
              message: this.$t('common.successDelete')
            })
          })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: this.$t('common.cancelDelete')
          })
        })
    },
    handleCurrentChange (val) {
      console.log(val)
      this.currentRow = val
    },
    handleCurrentChange2 (val) {
      this.currentRow2 = val
    },
    handleUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.docId = fileId.toString()
      row.fileName = fileName
    }
  }
}
</script>
<style scoped lang="scss">
.the_challengeClarification_wrapper /deep/ {
  .the_clarification_dialog {
    height: 600px;
    overflow: auto;
    padding: 10px;
  }
  .table-title {
    font-size: 16px;
    margin-left: 10px;
    font-weight: bolder;
  }
}
</style>
