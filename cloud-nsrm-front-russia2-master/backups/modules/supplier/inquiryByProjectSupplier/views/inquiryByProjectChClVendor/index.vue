<template>
  <el-container
    class="flex-container-notab the_challengeClarification_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper :form-array="preArr" @getFormData="queryClick" />

      <!-- 质疑列表 -->
      <p>
        <span class="table-title">
          {{ $t("bidMod.challengeList") }}
        </span>
        <el-button style="margin-left:11px;" type="primary" @click="editTab('add')">
          {{ $t("bidMod.addChallenge") }}
        </el-button>
      </p>

      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :url="apiList.bargainQuestion"
      />

      <!-- 澄清列表 -->
      <p>
        <span class="table-title">
          {{ $t("bidMod.clarificationList") }}
        </span>
      </p>

      <TableView
        :ref="gridId2"
        :table-data="tableData2"
        :table-header="tableHeader2"
        :page-size="pageSize"
        :pre-query-data="queryParam2"
        :url="apiList.bargainAnswer"
      />

      <!-- 质疑 -->
      <srm-dialog
        v-el-drag-dialog
        top="10vh"
        title="质疑"
        size="large"
        :visible.sync="dialogFormVisible"
        :close-on-click-modal="false"
        destroy-on-close
      >
        <div class="the_clarification_dialog">
          <el-form
            ref="form"
            :model="form"
            label-position="top"
            label-width="80px"
            :rules="rules"
          >
            <srm-row>
              <srm-col :init-col="4">
                <el-form-item :label="$t('bidMod.questionNum')" :label-width="formLabelWidth">
                  <el-input v-model="form.questionNum" disabled />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="4">
                <el-form-item :label="$t('bidMod.questionStatus')" :label-width="formLabelWidth">
                  <dict-select v-model="form.questionStatus" code="QUERY_STATUS" disabled />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="4">
                <el-form-item
                  prop="bargainNum"
                  :label="$t('bidMod.bidingName')"
                  :label-width="formLabelWidth"
                >
                  <el-select
                    v-model="form.bargainNum"
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
              </srm-col>
              <srm-col :init-col="4">
                <el-form-item :label="$t('bidMod.bidingNum')" :label-width="formLabelWidth">
                  <el-input v-model="form.bargainNum" disabled />
                </el-form-item>
              </srm-col>
            </srm-row>
            <h3>
              <!-- 质疑信息 -->
              {{ $t("bidMod.questionInfo") }}
            </h3>
            <srm-row>
              <srm-col :init-col="3">
                <el-form-item
                  :label="$t('bidMod.questionTitle')"
                  prop="questionTitle"
                  :label-width="formLabelWidth"
                >
                  <el-input v-model="form.questionTitle" />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
          <h3>
            <!-- 附件 -->
            {{ $t("bidMod.attachment") }}
            <el-button type="primary" @click="addOne">
              {{ $t("common.add") }}
            </el-button>
          </h3>
          <el-table :data="fileList" style="width: 100%" border height="121px">
            <el-table-column align="center" type="index" width="50" />

            <!--附件名称-->
            <SrmCommonFile
              type="table-column"
              :extra-data="fileInfo"
              :table-column-options="{
                label: $t('bidMod.attachmentName'),
                prop: 'docId',
                nameProp: 'fileName'
              }"
              @on-change="fileChange"
            />

            <el-table-column align="center" prop="remark" :label="$t('bidMod.remark')">
              <template slot-scope="scope">
                <el-input v-model="scope.row.remark" />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="operation"
              :label="$t('common.operation')"
              width="100"
            >
              <template slot-scope="scope">
                <el-button :disabled="isLock" type="text" @click="deleOne(scope.$index, scope.row)">
                  {{ $t("common.delete") }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <h3>
            <!-- 质疑内容 -->
            {{ $t("bidMod.questionContent") }}
          </h3>
          <Tinymce
            v-if="dialogFormVisible"
            id="inquirySupplierChallengeTinymce"
            v-model="mainClarification"
            :height="260"
          />
        </div>
        <div slot="footer">
          <el-button @click="backOne">
            {{ $t("common.cancel") }}
          </el-button>
          <el-button type="primary" :disabled="isLock" @click="saveOne">
            {{ $t("common.staging") }}
          </el-button>
          <el-button type="primary" :disabled="isLock" @click="publishOne">
            {{ $t("common.publish") }}
          </el-button>
        </div>
      </srm-dialog>

      <!--澄清单-->
      <srm-dialog
        :title="$t('bidMod.clarifySheet')"
        size="large"
        :visible.sync="showAnswerInfo"
        :close-on-click-modal="false"
        destroy-on-close
      >
        <div class="the_clarification_dialog">
          <el-form ref="form2" :model="form" label-position="top" label-width="80px">
            <srm-row>
              <srm-col :init-col="3">
                <el-form-item :label="$t('bidMod.answerNum')" :label-width="formLabelWidth">
                  <el-input v-model="answerForm.answerNum" disabled />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="3">
                <el-form-item :label="$t('bidMod.bidingName')" :label-width="formLabelWidth">
                  <el-input v-model="answerForm.bargainName" disabled />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="3">
                <el-form-item :label="$t('bidMod.answerTitle')" :label-width="formLabelWidth">
                  <el-input v-model="answerForm.answerTitle" disabled />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="3">
                <el-form-item :label="$t('bidMod.bidingNum')" :label-width="formLabelWidth">
                  <el-input v-model="answerForm.bargainNum" disabled />
                </el-form-item>
              </srm-col>
            </srm-row>

            <!-- 项目信息变更 -->
            <h3>{{ $t("bidMod.proInfoChange") }}</h3>

            <h4>变更前</h4>

            <el-table :data="prefixTable" style="width: 100%;" border>
              <!--报名截止时间-->
              <el-table-column
                align="center"
                prop="prefixEnrollEndTime"
                :label="$t('bidMod.registrationDeadline')"
              >
                <template v-slot="scope">
                  <el-date-picker v-model="scope.row.prefixEnrollEndTime" disabled />
                </template>
              </el-table-column>

              <!--投标开始时间-->
              <el-table-column
                align="center"
                prop="prefixBargainStartTime"
                :label="$t('bidMod.bidingStartDatetime')"
              >
                <template v-slot="scope">
                  <el-date-picker v-model="scope.row.prefixBargainStartTime" disabled />
                </template>
              </el-table-column>

              <!--投标结束时间-->
              <el-table-column
                align="center"
                prop="prefixBargainEndTime"
                :label="$t('bidMod.bidingEndDatetime')"
              >
                <template v-slot="scope">
                  <el-date-picker v-model="scope.row.prefixBargainEndTime" disabled />
                </template>
              </el-table-column>

              <!--投标地点-->
              <el-table-column
                align="center"
                prop="prefixBargainSite"
                :label="$t('bidMod.bidingSite2')"
              >
                <template v-slot="scope">
                  <el-input v-model="scope.row.prefixBargainSite" disabled />
                </template>
              </el-table-column>
            </el-table>

            <h4>变更后</h4>

            <el-table :data="suffixTable" style="width: 100%;" border>
              <!--报名截止时间-->
              <el-table-column
                align="center"
                prop="suffixEnrollEndTime"
                :label="$t('bidMod.registrationDeadline')"
              >
                <template v-slot="scope">
                  <el-date-picker v-model="scope.row.suffixEnrollEndTime" disabled />
                </template>
              </el-table-column>

              <!--投标开始时间-->
              <el-table-column
                align="center"
                prop="suffixBargainStartTime"
                :label="$t('bidMod.bidingStartDatetime')"
              >
                <template v-slot="scope">
                  <el-date-picker v-model="scope.row.suffixBargainStartTime" disabled />
                </template>
              </el-table-column>

              <!--投标结束时间-->
              <el-table-column
                align="center"
                prop="suffixBargainEndTime"
                :label="$t('bidMod.bidingEndDatetime')"
              >
                <template v-slot="scope">
                  <el-date-picker v-model="scope.row.suffixBargainEndTime" disabled />
                </template>
              </el-table-column>

              <!--投标地点-->
              <el-table-column
                align="center"
                prop="suffixBargainSite"
                :label="$t('bidMod.bidingSite2')"
              >
                <template v-slot="scope">
                  <el-input v-model="scope.row.suffixBargainSite" disabled />
                </template>
              </el-table-column>
            </el-table>
          </el-form>
          <h3>
            <!-- 附件 -->
            {{ $t("bidMod.attachment") }}
          </h3>
          <el-table :data="fileList2" style="width: 100%" border height="121px">
            <el-table-column align="center" type="index" width="50" />

            <!--附件名称-->
            <SrmCommonFile
              type="table-column"
              :table-column-options="{
                label: $t('bidMod.attachmentName'),
                prop: 'docId',
                nameProp: 'fileName'
              }"
              readonly
            />

            <el-table-column align="center" prop="remark" :label="$t('bidMod.remark')">
              <template slot-scope="scope">
                <el-input v-model="scope.row.remark" disabled />
              </template>
            </el-table-column>
          </el-table>
          <h3>
            <!-- 澄清内容 -->
            {{ $t("bidMod.clarifyContent") }}
          </h3>
          <Tinymce
            id="inquirySupplierClarificationTinymce"
            v-model="answerInfo"
            :height="260"
            @setup="ready"
          />
        </div>
        <div slot="footer">
          <!--存在质疑ID，并且已发布-->
          <el-button v-if="answerForm.questionId && canAccept" type="primary" @click="acceptAnswer">
            {{ $t("bidMod.acceptClarify") }}
          </el-button>
        </div>
      </srm-dialog>
    </el-main>
  </el-container>
</template>

<script>
import { parseTimeYMD } from 'lib@/composition/origin/composition'
import Tinymce from '@/components/Tinymce'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'

export default {
  name: 'InquiryByProjectChClVendor',
  components: {
    TableView,
    FormWrapper,
    Tinymce
  },
  data () {
    return {
      fileList2: [],
      showAnswerInfo: false,
      fileInfos: [],
      apiList: {
        save: '/api-brg/bargainQuestion/save',
        submit: '/api-brg/bargainQuestion/submit',
        clarified: '/api-brg/bargainQuestion/clarified',
        bargainQuestion: '/api-brg/bargainQuestion/listPage',
        bargainAnswer: '/api-brg/bargainAnswer/listPage',
        getBargainQuestionById: '/api-brg/bargainQuestion/getBargainQuestionById',
        getBargainAnswerById: '/api-brg/bargainAnswer/getBargainAnswerById',
        delete: '/api-brg/supplierCooperate/vendorFile/delete',
        supplierBargain: '/api-brg/supplierCooperate/orderHead/queryBargain',
        bargainGet: '/api-brg/brgInitiating/bargain/get',
        deleteItem: '/api-brg/bargainQuestion/delete',
        rejectItem: '/api-brg/bargainQuestion/withDraw'
      },
      isLock: false,
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'inquiry',
        fileType: 'images'
      },
      pageSize: 15,
      gridId: 'list',
      tableHeader: [],
      tableData: [],
      gridId2: 'list2',
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
      mainClarification: null,
      answerInfo: null,
      dialogFormVisible: false,
      formLabelWidth: '140px',
      options: [],
      queryParam: {},
      queryParam2: {},
      answerForm: {},
      canAccept: false,
      rules: {
        bargainNum: { required: true, message: this.$t('bidMod.msgSelProject') },
        questionTitle: {
          message: this.$t('bidMod.msgInputTitle'),
          required: true
        }
      },
      preArr: [
        { prop: 'bargainNum', label: () => this.$t('bidMod.bidingNumCla1') },
        { prop: 'bargainName', label: () => this.$t('bidMod.bidingNameCla1') },
        { prop: 'questionTitle', label: () => this.$t('bidMod.questionTitle') },
        { prop: 'questionNum', label: () => this.$t('bidMod.questionNum') },
        {
          prop: 'questionStatus',
          label: () => this.$t('bidMod.questionStatus'),
          type: 'dict',
          code: 'QUERY_STATUS'
        },
        { prop: 'answerTitle', label: () => this.$t('bidMod.answerTitle') },
        { prop: 'answerNum', label: () => this.$t('bidMod.answerNum') },
        {
          prop: 'answerStatus',
          label: () => this.$t('bidMod.answerStatus'),
          type: 'dict',
          code: 'CLARIFIED_STATUS'
        }
      ],
      prefixTable: [
        {
          prefixEnrollEndTime: '',
          prefixBargainStartTime: '',
          prefixBargainEndTime: '',
          prefixBargainSite: ''
        }
      ],
      suffixTable: [
        {
          suffixEnrollEndTime: '',
          suffixBargainStartTime: '',
          suffixBargainEndTime: '',
          suffixBargainSite: ''
        }
      ]
    }
  },
  watch: {
    '$route.params': {
      handler (val) {
        // 监听路由参数，如果存在招标单信息，就打开新增质疑弹窗
        const { bargainId, bargainName, bargainNum } = val || {}
        if (bargainId && bargainName && bargainNum && bargainId !== this.form.bargainId) {
          this.dialogFormVisible = true
          this.form = {
            ...this.form,
            bargainId,
            bargainName,
            bargainNum
          }
          this.queryCompanyList({ bargainNum })
        }
      },
      deep: true,
      immediate: true
    }
  },
  created () {
    this.tableHeader = [
      {
        prop: 'questionNum',
        label: () => this.$t('bidMod.questionNum'),
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.editTab('edit', row)
      },
      {
        prop: 'questionTitle',
        label: () => this.$t('bidMod.questionTitle'),
        minWidth: 150
      },
      {
        prop: 'questionStatus',
        label: () => this.$t('bidMod.questionStatus'),
        formattor: val => this.$getDictLabel('QUERY_STATUS', val),
        minWidth: 100
      },
      {
        prop: 'submitTime',
        label: () => this.$t('bidMod.releaseDatetime'),
        minWidth: 100,
        formattor: val => parseTimeYMD(val)
      },
      {
        prop: 'bargainNum',
        label: () => this.$t('bidMod.bidingNumCla1'),
        minWidth: 150
      },
      {
        prop: 'bargainName',
        label: () => this.$t('bidMod.bidingNameCla1'),
        minWidth: 150
      },
      {
        prop: 'rejectReason',
        label: () => this.$t('bidMod.rejectReason1'),
        minWidth: 150
      },
      {
        prop: 'operation',
        label: () => this.$t('bidMod.operation'),
        minWidth: 150,
        showType: 'buttons',
        btnStyle: 'text',
        fixed: 'right',
        buttons: [
          {
            callback: row => this.deleteItem(row),
            show: ({ questionStatus }) => questionStatus === 'DRAFT',
            formattor: () => this.$t('common.delete')
          },
          {
            callback: row => this.rejectItem(row),
            show: ({ questionStatus }) => questionStatus === 'SUBMITTED',
            formattor: () => this.$t('bidMod.withdraw')
          }
        ]
      }
    ]

    this.tableHeader2 = [
      {
        prop: 'answerNum',
        label: () => this.$t('bidMod.answerNum'),
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.reviewInfo(row)
      },
      {
        prop: 'answerTitle',
        label: () => this.$t('bidMod.answerTitle'),
        minWidth: 150
      },
      {
        prop: 'answerStatus',
        label: () => this.$t('bidMod.answerStatus'),
        minWidth: 100,
        formattor: val => this.$getDictLabel('CLARIFIED_STATUS', val)
      },
      {
        prop: 'submitTime',
        label: () => this.$t('bidMod.releaseDatetime'),
        minWidth: 100,
        formattor: val => parseTimeYMD(val)
      },
      {
        prop: 'questionNum',
        label: () => this.$t('bidMod.questionNum'),
        minWidth: 150,
        formattor: val => (val || this.$t('bidMod.tendererIssued'))
      },
      {
        prop: 'bargainNum',
        label: () => this.$t('bidMod.bidingNumCla1'),
        minWidth: 150
      },
      { prop: 'bargainName', label: () => this.$t('bidMod.bidingNameCla1') }
    ]
    this.getQuerydata()
    this.getQuerydata2()
  },
  // mounted () {
  //   this.$nextTick(() => {
  //     const { bargainId, bargainName, bargainNum } = this.$route.params || {}
  //     console.log(bargainId, bargainName, bargainNum)
  //     if (bargainId && bargainName && bargainNum) {
  //       this.dialogFormVisible = true
  //       this.form = {
  //         ...this.form,
  //         bargainId,
  //         bargainName,
  //         bargainNum
  //       }
  //       this.queryCompanyList({ bargainNum })
  //     }
  //   })
  // },
  methods: {
    elSelectChange (val) {
      const company = this.options.find(i => i.value === val)
      const { id, label } = company || {}
      this.form = { ...this.form, bargainId: id, bargainName: label }
    },

    queryCompanyList ({ bargainName = '', bargainNum = '' }) {
      this.$http({
        url: this.apiList.supplierBargain,
        method: 'POST',
        data: { bargainName, bargainNum, pageSize: 999, pageNum: 1 }
      }).then(res => {
        this.options = res.data.list.map(i => ({
          id: i.bargainId,
          value: i.bargainNum,
          label: i.bargainName
        }))
      })
    },

    remoteMethod (bargainName) {
      this.queryCompanyList({ bargainName })
    },

    reviewInfo ({ answerId }) {
      this.showAnswerInfo = true
      this.$http({
        url: `/api-brg/bargainAnswer/getDetail/${answerId}`,
        method: 'GET',
        loading: true
      }).then(res => {
        const { fileList, answerContent, ...rest } = res.data
        this.answerForm = { ...rest }
        this.fileList2 = fileList
        this.answerInfo = answerContent
        // 已发布
        this.canAccept = rest.answerStatus === 'ISSUED'

        this.prefixTable = [
          {
            prefixEnrollEndTime: rest.prefixEnrollEndTime,
            prefixBargainStartTime: rest.prefixBargainStartTime,
            prefixBargainEndTime: rest.prefixBargainEndTime,
            prefixBargainSite: rest.prefixBargainSite
          }
        ]

        this.suffixTable = [
          {
            suffixEnrollEndTime: rest.suffixEnrollEndTime,
            suffixBargainStartTime: rest.suffixBargainStartTime,
            suffixBargainEndTime: rest.suffixBargainEndTime,
            suffixBargainSite: rest.suffixBargainSite
          }
        ]
      })
    },

    queryClick (v = {}) {
      const { ...rest } = v
      this.getQuerydata2({ ...rest })
      this.getQuerydata(v)
    },

    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },

    getQuerydata2 (v) {
      this.queryParam2 = v
      this.$nextTick(() => {
        this.$refs[this.gridId2].query()
      })
    },

    /* 接受澄清 */
    acceptAnswer () {
      this.$http({
        url: `/api-brg/bargainAnswer/vendorAccept/${this.answerForm.answerId}`,
        method: 'POST',
        loading: true
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
            ...this.form,
            questionComtent: this.mainClarification,
            files: this.fileList
          }
          this.$http({
            url: this.apiList.save,
            method: 'POST',
            data,
            loading: true
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
      this.$confirm(this.$t('common.ifDeleteData'), this.$t('common.tips'), {
        confirmButtonText: this.$t('common.confirm'), // 确定
        cancelButtonText: this.$t('common.cancel'), // 取消
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: this.apiList.deleteItem,
            method: 'GET',
            params: { id: questionId },
            loading: true
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
        params: { id: questionId },
        loading: true
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
            ...this.form,
            questionComtent: this.mainClarification,
            files: this.fileList
          }
          this.$http({
            url: this.apiList.submit,
            method: 'POST',
            data,
            loading: true
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

    /* 编辑tab */
    editTab (type, row) {
      this.dialogFormVisible = true
      if (type === 'add') {
        this.form = {}
        this.fileList = []
        this.mainClarification = null
        this.isLock = false
      } else {
        this.$http({
          url: this.apiList.getBargainQuestionById,
          method: 'GET',
          params: { questionId: row.questionId },
          loading: true
        }).then(res => {
          const { files, questionComtent, ...rest } = res.data
          this.queryCompanyList({ bargainNum: rest.bargainNum })
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
        remark: ''
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
            params: { vendorFileId: row.vendorFileId },
            loading: true
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

    /* 内部查看文件变更 */
    fileChange ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.fileList[$index].docId = fileId
      this.fileList[$index].fileName = fileName
    },
    ready (editorInstance) {
      editorInstance.setMode('readonly')
    }
  }
}
</script>
