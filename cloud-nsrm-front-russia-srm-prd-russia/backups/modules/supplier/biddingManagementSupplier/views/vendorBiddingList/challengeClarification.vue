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
        :url="apiList.bidingQuestion"
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
        :url="apiList.bidingAnswer"
      />

      <!-- 新增质疑 -->
      <srm-dialog
        v-el-drag-dialog
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
            <srm-row>
              <srm-col>
                <el-form-item :label="$t('bidMod.questionNum')" :label-width="formLabelWidth">
                  <el-input v-model="form.questionNum" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('bidMod.questionStatus')" :label-width="formLabelWidth">
                  <dict-select v-model="form.questionStatus" code="QUERY_STATUS" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
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
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('bidMod.bidingNum')" :label-width="formLabelWidth">
                  <el-input v-model="form.bidingNum" disabled />
                </el-form-item>
              </srm-col>
            </srm-row>
            <h3>
              <!-- 质疑信息 -->
              {{ $t("bidMod.questionInfo") }}
            </h3>
            <srm-row>
              <srm-col>
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
              :readonly="isLock"
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
          <Tinymce id="biddingSupplierChallengeTinymce" v-model="mainClarification" :height="260" />
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
      >
        <div class="the_clarification_dialog">
          <el-form ref="form2" :model="form" label-position="top" label-width="80px">
            <srm-row :gutter="16">
              <srm-col>
                <el-form-item :label="$t('bidMod.answerNum')" :label-width="formLabelWidth">
                  <el-input v-model="answerForm.answerNum" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('bidMod.bidingName')" :label-width="formLabelWidth">
                  <el-input v-model="answerForm.bidingName" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('bidMod.answerTitle')" :label-width="formLabelWidth">
                  <el-input v-model="answerForm.answerTitle" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('bidMod.bidingNum')" :label-width="formLabelWidth">
                  <el-input v-model="answerForm.bidingNum" disabled />
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
                prop="prefixBidingStartTime"
                :label="$t('bidMod.bidingStartDatetime')"
              >
                <template v-slot="scope">
                  <el-date-picker v-model="scope.row.prefixBidingStartTime" disabled />
                </template>
              </el-table-column>

              <!--投标结束时间-->
              <el-table-column
                align="center"
                prop="prefixBidingEndTime"
                :label="$t('bidMod.bidingEndDatetime')"
              >
                <template v-slot="scope">
                  <el-date-picker v-model="scope.row.prefixBidingEndTime" disabled />
                </template>
              </el-table-column>

              <!--投标地点-->
              <el-table-column
                align="center"
                prop="prefixBidingSite"
                :label="$t('bidMod.bidingSite2')"
              >
                <template v-slot="scope">
                  <el-input v-model="scope.row.prefixBidingSite" disabled />
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
                prop="suffixBidingStartTime"
                :label="$t('bidMod.bidingStartDatetime')"
              >
                <template v-slot="scope">
                  <el-date-picker v-model="scope.row.suffixBidingStartTime" disabled />
                </template>
              </el-table-column>

              <!--投标结束时间-->
              <el-table-column
                align="center"
                prop="suffixBidingEndTime"
                :label="$t('bidMod.bidingEndDatetime')"
              >
                <template v-slot="scope">
                  <el-date-picker v-model="scope.row.suffixBidingEndTime" disabled />
                </template>
              </el-table-column>

              <!--投标地点-->
              <el-table-column
                align="center"
                prop="suffixBidingSite"
                :label="$t('bidMod.bidingSite2')"
              >
                <template v-slot="scope">
                  <el-input v-model="scope.row.suffixBidingSite" disabled />
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
            id="biddingSupplierClarificationTinymce"
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
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime } from '@/utils'
import Tinymce from '@/components/Tinymce'

export default {
  name: 'VendorChallengeClarification',
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
        save: '/api-bid/bidingQuestion/save',
        submit: '/api-bid/bidingQuestion/submit',
        clarified: '/api-bid/bidingQuestion/clarified',
        bidingQuestion: '/api-bid/bidingQuestion/listPage',
        bidingAnswer: '/api-bid/bidingAnswer/listPage',
        getBidingQuestionById: '/api-bid/bidingQuestion/getBidingQuestionById',
        getBidingAnswerById: '/api-bid/bidingAnswer/getBidingAnswerById',
        delete: '/api-bid/supplierCooperate/vendorFile/delete',
        supplierBiding: '/api-bid/supplierCooperate/orderHead/queryBiding',
        bidingGet: '/api-bid/bidInitiating/biding/get',
        deleteItem: '/api-bid/bidingQuestion/delete',
        rejectItem: '/api-bid/bidingQuestion/withDraw'
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
          prefixBidingStartTime: '',
          prefixBidingEndTime: '',
          prefixBidingSite: ''
        }
      ],
      suffixTable: [
        {
          suffixEnrollEndTime: '',
          suffixBidingStartTime: '',
          suffixBidingEndTime: '',
          suffixBidingSite: ''
        }
      ]
    }
  },
  watch: {
    '$route.params': {
      handler (val) {
        // 监听路由参数，如果存在招标单信息，就打开新增质疑弹窗
        const { bidingId, bidingName, bidingNum } = val || {}
        if (bidingId && bidingName && bidingNum) {
          this.dialogFormVisible = true
          this.form = {
            ...this.form,
            bidingId,
            bidingName,
            bidingNum
          }
          this.queryCompanyList({ bidingNum })
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
        formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      },
      {
        prop: 'bidingNum',
        label: () => this.$t('bidMod.bidingNumCla'),
        minWidth: 150
      },
      {
        prop: 'bidingName',
        label: () => this.$t('bidMod.bidingNameCla'),
        minWidth: 150
      },
      {
        prop: 'rejectReason',
        label: () => this.$t('bidMod.rejectReason1')
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
        formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      },
      {
        prop: 'questionNum',
        label: () => this.$t('bidMod.questionNum'),
        minWidth: 150,
        formattor: val => val || this.$t('bidMod.tendererIssued')
      },
      {
        prop: 'bidingNum',
        label: () => this.$t('bidMod.bidingNumCla'),
        minWidth: 150
      },
      { prop: 'bidingName', label: () => this.$t('bidMod.bidingNameCla') }
    ]
    this.getQuerydata()
    this.getQuerydata2()
  },
  // mounted () {
  //   this.$nextTick(() => {
  //     const { bidingId, bidingName, bidingNum } = this.$route.params || {}
  //     console.log(bidingId, bidingName, bidingNum)
  //     if (bidingId && bidingName && bidingNum) {
  //       this.dialogFormVisible = true
  //       this.form = {
  //         ...this.form,
  //         bidingId,
  //         bidingName,
  //         bidingNum
  //       }
  //       this.queryCompanyList({ bidingNum })
  //     }
  //   })
  // },
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
        url: `/api-bid/bidingAnswer/getDetail/${answerId}`,
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
            prefixBidingStartTime: rest.prefixBidingStartTime,
            prefixBidingEndTime: rest.prefixBidingEndTime,
            prefixBidingSite: rest.prefixBidingSite
          }
        ]

        this.suffixTable = [
          {
            suffixEnrollEndTime: rest.suffixEnrollEndTime,
            suffixBidingStartTime: rest.suffixBidingStartTime,
            suffixBidingEndTime: rest.suffixBidingEndTime,
            suffixBidingSite: rest.suffixBidingSite
          }
        ]

        this.$http({
          url: this.apiList.bidingGet,
          method: 'GET',
          params: { id: rest.bidingId }
        }).then(res2 => {
          const { enrollEndDatetime, bidingStartDatetime, bidingSite } = res2.data
          this.answerForm = {
            ...this.answerForm,
            enrollEndDatetime,
            bidingStartDatetime,
            bidingSite
          }
        })
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
        url: `/api-bid/bidingAnswer/vendorAccept/${this.answerForm.answerId}`,
        method: 'POST'
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
      this.$confirm(this.$t('common.ifDeleteData'), this.$t('common.tips'), {
        confirmButtonText: this.$t('common.confirm'), // 确定
        cancelButtonText: this.$t('common.cancel'), // 取消
        type: 'warning'
      })
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
            ...this.form,
            questionComtent: this.mainClarification,
            files: this.fileList
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

    /* 文件变更 */
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
