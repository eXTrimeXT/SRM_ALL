<template>
  <el-container class="flex-container-notab" direction="vertical">
    <el-main>
      <!--搜索表单 可覆盖-->
      <slot name="search">
        <FormWrapper :form-array="searchFormConfig" @getFormData="queryTableData" />
      </slot>

      <!-- 质疑列表 -->
      <p class="table-toolbar">
        <span class="table-toolbar-title">{{ $t('bidMod.challengeList') }}</span>
        <!--新增质疑 供应商-->
        <el-button
          v-if="userTypeFlag.isVendor"
          type="primary"
          @click="openQuestionDetailDialog('add', {})"
        >
          {{ $t("bidMod.addChallenge") }}
        </el-button>
      </p>

      <TableView
        ref="questionTable"
        :table-data="questionTableData"
        :table-header="questionTableHeader"
        :pre-query-data="questionTableQueryParam"
        :url="qaHttp.souQuestion.getListPageUrl(souType)"
      />

      <!-- 澄清列表 -->
      <p class="table-toolbar">
        <span class="table-toolbar-title">{{ $t("bidMod.clarificationList") }}</span>
        <el-button
          v-if="userTypeFlag.isBuyer"
          type="primary"
          @click="openAnswerDetail('add', {}, false)"
        >
          <!-- 新增澄清 -->
          {{ $t("bidMod.addClarification") }}
        </el-button>
      </p>

      <TableView
        ref="answerTable"
        :table-data="answerTableData"
        :table-header="answerTableHeader"
        :pre-query-data="answerTableQueryParam"
        :com-active="$attrs['changeTab']"
        :url="qaHttp.souAnswer.getListPageUrl(souType)"
      />

      <!--质疑单弹窗-->
      <QuestionDetail
        v-if="questionDetailVisible"
        :visible.sync="questionDetailVisible"
        :sou-type="souType"
        :edit-row="editRow"
        :page-type="questionDetailPageType"
        @refresh="queryTableData"
      />

      <!--澄清单弹窗-->
      <AnswerDetail
        v-if="answerDetailVisible"
        :visible.sync="answerDetailVisible"
        :sou-type="souType"
        :edit-row="editRow"
        :page-type="answerDetailPageType"
        :is-from-question="isFromQuestion"
        @refresh="queryTableData"
      />
    </el-main>
  </el-container>
</template>

<script>
import { qaBuyerHttp, qaVendorHttp } from './api'
import { mapGetters } from 'vuex'
import { USER_TYPE_ENUM } from 'lib@/composition/origin/enum'
import TableView from 'lib@/components/Table/TableView.vue'
import FormWrapper from 'lib@/components/Table/FormWrapper.vue'
import QuestionDetail from './questionDetail.vue'
import AnswerDetail from './answerDetail.vue'

export default {
  name: 'Qa',

  components: {
    TableView,
    FormWrapper,
    QuestionDetail,
    AnswerDetail
  },

  props: {
    // 寻源类型
    souType: {
      type: String,
      required: true
    },
    // 查询项目快查code
    quickSearchCode: {
      type: String,
      default: ''
    }
  },

  data () {
    return {
      questionDetailVisible: false,
      answerDetailVisible: false,
      answerDetailPageType: 'add',
      questionDetailPageType: 'add',
      editRow: null,
      questionTableData: [],
      answerTableData: [],
      isFromQuestion: false,
      questionTableQueryParam: {},
      answerTableQueryParam: {},
      searchFormConfig: [
        // 项目编号
        { prop: 'souNo', label: this.$t('bidMod.bidingNum') },
        // 项目名称
        { prop: 'souName', label: this.$t('bidMod.bidingName') },
        // 质疑标题
        { prop: 'questionTitle', label: this.$t('bidMod.questionTitle') },
        // 质疑编号
        { prop: 'questionNum', label: this.$t('bidMod.questionNum') },
        // 质疑状态
        {
          prop: 'questionStatus',
          label: this.$t('bidMod.questionStatus'),
          type: 'dict',
          code: 'QUERY_STATUS'
        },
        // 澄清标题
        { prop: 'answerTitle', label: this.$t('bidMod.answerTitle') },
        // 澄清编号
        { prop: 'answerNum', label: this.$t('bidMod.answerNum') },
        // 澄清状态
        {
          prop: 'answerStatus',
          label: this.$t('bidMod.answerStatus'),
          type: 'dict',
          code: 'CLARIFIED_STATUS'
        }
      ]
    }
  },

  computed: {
    ...mapGetters(['userType']),

    userTypeFlag () {
      return {
        isBuyer: this.userType === USER_TYPE_ENUM.BUYER,
        isVendor: this.userType === USER_TYPE_ENUM.VENDOR
      }
    },

    // api入口
    qaHttp () {
      return this.userTypeFlag.isBuyer ? qaBuyerHttp : qaVendorHttp
    },

    // 质疑表头
    questionTableHeader () {
      return [
        // 质疑编号
        {
          prop: 'questionNum',
          label: this.$t('bidMod.questionNum'),
          minWidth: 150,
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.openQuestionDetailDialog('view', row)
        },
        // 质疑标题
        {
          prop: 'questionTitle',
          label: this.$t('bidMod.questionTitle'),
          minWidth: 150
        },
        // 质疑状态
        {
          prop: 'questionStatus',
          label: this.$t('bidMod.questionStatus'),
          formattor: val => this.$getDictLabel('QUERY_STATUS', val),
          minWidth: 100
        },
        // 项目编号
        {
          prop: 'souNo',
          label: this.$t('bidMod.bidingNum'),
          minWidth: 150
        },
        // 项目名称
        {
          prop: 'souName',
          label: this.$t('bidMod.bidingName'),
          minWidth: 150
        },
        // 提出时间
        {
          prop: 'submitTime',
          label: '提出时间',
          minWidth: 100,
          formattor: val => this.$dayjsParse(val)
        },
        // 质疑来源
        {
          prop: 'vendorName',
          label: '质疑来源',
          minWidth: 130
        },
        // 作废原因
        {
          prop: 'rejectReason',
          label: this.$t('bidMod.rejectReason'),
          minWidth: 100
        },
        {
          prop: 'operation',
          label: this.$t('bidMod.operation'),
          minWidth: 150,
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          buttons: [
            // 采购商 - 澄清质疑
            {
              show: row => this.userTypeFlag.isBuyer && row.questionStatus !== 'CLARIFIED',
              formattor: () => this.$t('bidMod.clarification'),
              callback: row => this.openAnswerDetail('add', row, true)
            },
            // 采购商 - 驳回质疑
            {
              show: row => this.userTypeFlag.isBuyer && row.questionStatus === 'SUBMITTED',
              formattor: () => this.$t('common.toRefuse'),
              callback: row => this.rejectQuestion(row)
            },
            // 供应商 - 编辑质疑
            {
              show: row => this.userTypeFlag.isVendor && row.questionStatus === 'DRAFT',
              formattor: () => this.$t('common.edit'),
              callback: row => this.openQuestionDetailDialog('edit', row)
            },
            // 供应商 - 删除质疑
            {
              show: row => this.userTypeFlag.isVendor && row.questionStatus === 'DRAFT',
              formattor: () => this.$t('common.delete'),
              callback: row => this.deleteQuestion(row)
            },
            // 供应商 - 撤回质疑
            {
              show: row => this.userTypeFlag.isVendor && row.questionStatus === 'SUBMITTED',
              formattor: () => this.$t('bidMod.withdraw'),
              callback: row => this.withdrawQuestion(row)
            }
          ]
        }
      ]
    },

    // 澄清表头
    answerTableHeader () {
      return [
        // 澄清编号
        {
          prop: 'answerNum',
          label: this.$t('bidMod.answerNum'),
          minWidth: 150,
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.openAnswerDetail('view', row, false)
        },
        // 澄清标题
        {
          prop: 'answerTitle',
          label: this.$t('bidMod.answerTitle'),
          minWidth: 150
        },
        // 澄清状态
        {
          prop: 'answerStatus',
          label: this.$t('bidMod.answerStatus'),
          minWidth: 100,
          formattor: val => this.$getDictLabel('CLARIFIED_STATUS', val)
        },
        // 质疑编号
        {
          prop: 'questionNum',
          label: this.$t('bidMod.questionNum'),
          minWidth: 150,
          // 如果没有就是 招标方发布
          formattor: val => (val || this.$t('bidMod.tendererIssued')),
          show: row => !!row.questionNum,
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.openQuestionDetailDialog('view', row)
        },
        // 项目编号
        {
          prop: 'souNo',
          label: this.$t('bidMod.bidingNum'),
          minWidth: 150
        },
        // 项目名称
        {
          prop: 'souName',
          label: this.$t('bidMod.bidingName'),
          minWidth: 120
        },
        // 澄清日期
        {
          prop: 'submitTime',
          label: '澄清日期',
          minWidth: 100,
          formattor: val => this.$dayjsParse(val)
        },
        {
          prop: 'operation',
          label: this.$t('bidMod.operation'),
          minWidth: 160,
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          buttons: [
            // 采购商 - 编辑
            {
              show: row => this.userTypeFlag.isBuyer && row.answerStatus === 'DRAFT',
              formattor: () => this.$t('common.edit'),
              callback: row => this.openAnswerDetail('edit', row, false)
            },
            // 采购商 - 删除澄清
            {
              show: row => this.userTypeFlag.isBuyer && row.answerStatus === 'DRAFT',
              formattor: () => this.$t('common.delete'),
              callback: row => this.deleteAnswer(row)
            },
            // 采购商 - 发布澄清
            {
              show: row => this.userTypeFlag.isBuyer && row.answerStatus === 'DRAFT',
              formattor: () => this.$t('common.publish'),
              callback: row => this.publishAnswer(row)
            },
            // 采购商 - 撤回澄清
            {
              show: row => this.userTypeFlag.isBuyer && row.answerStatus === 'ISSUED',
              formattor: () => this.$t('bidMod.withdraw'),
              callback: row => this.withdrawAnswer(row)
            },
            // 供应商 - 接受澄清
            {
              show: row => this.userTypeFlag.isVendor && row.answerStatus === 'ISSUED',
              formattor: () => this.$t('bidMod.acceptClarify'),
              callback: row => this.openAnswerDetail('view', row, false)
            }
          ]
        }
      ]
    }
  },

  watch: {
    $route: {
      handler () {
        // 如果路由参数有项目ID，就打开新增澄清弹窗
        const { projectId, souName, souNo } = this.$route.params || {}
        if (projectId && souName && souNo) {
          // 采购商
          if (this.userTypeFlag.isBuyer) {
            // 打开澄清新增
            this.openAnswerDetail(
              'add',
              {
                projectId,
                souName,
                souNo
              },
              true
            )
          }

          // 供应商
          if (this.userTypeFlag.isVendor) {
            // 打开质疑新增
            this.openQuestionDetailDialog(
              'add',
              {
                projectId,
                souName,
                souNo
              }
            )
          }
        }
      },
      immediate: true,
      deep: true
    }
  },

  mounted () {
    this.$nextTick(() => {
      this.queryTableData()
    })
  },

  activated () {
    this.doLayout()
  },

  methods: {
    /* 查询列表数据 */
    queryTableData (val = {}) {
      this.getAnswerTableData(val)
      this.getQuestionTableData(val)
    },

    /* 查询 质疑列表数据 */
    getQuestionTableData (val) {
      this.questionTableQueryParam = val
      this.$nextTick(() => {
        this.$refs.questionTable.query()
      })
    },

    /* 查询 澄清列表数据 */
    getAnswerTableData (val) {
      this.answerTableQueryParam = val
      this.$nextTick(() => {
        this.$refs.answerTable.query()
      })
    },

    /* 采购商 - 删除澄清 */
    async deleteAnswer ({ answerId }) {
      const confirmResult = await this.$confirm(this.$t('common.ifDeleteData'), this.$t('common.tips'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => { /* nothing */ })

      if (confirmResult !== 'confirm') {
        return
      }

      const response = await qaBuyerHttp.souAnswer.delete(this.souType, answerId)
      if (response) {
        // 删除成功
        this.$message.success(this.$t('common.successDelete'))
        this.getAnswerTableData()
      }
    },

    /* 采购商 - 撤回澄清 */
    async withdrawAnswer ({ answerId }) {
      const response = await qaBuyerHttp.souAnswer.withdraw(this.souType, answerId)
      if (response) {
        // 撤回成功!
        this.$message.success(this.$t('common.successWithdraw'))
        this.queryTableData()
      }
    },

    /* 采购商 - 发布澄清 */
    async publishAnswer ({ answerId }) {
      const response = await qaBuyerHttp.souAnswer.publish(this.souType, answerId)
      if (response) {
        // 发布成功!
        this.$message.success(this.$t('common.successPublish'))
        this.queryTableData()
      }
    },

    /* 采购商 - 驳回质疑 */
    async rejectQuestion (row) {
      const promptResult = await this.$prompt(
        this.$t('bidMod.msgRejectReason'),
        this.$t('common.toRefuse'),
        {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          inputValidator: value => !(!value || value.length > 200),
          inputErrorMessage: this.$t('bidMod.biddingManagementBuyer.rejectReason')
        }
      )

      if (!promptResult) {
        return
      }
      const response = await qaBuyerHttp.souQuestion.reject(this.souType, {
        questionId: row.questionId,
        rejectReason: promptResult.value
      })

      if (response) {
        // 成功驳回！
        this.$message.success(this.$t('bidMod.successRefuse'))
        this.getQuestionTableData()
      }
    },

    /* 供应商 - 删除质疑 */
    async deleteQuestion ({ questionId }) {
      const confirmResult = await this.$confirm(this.$t('common.ifDeleteData'), this.$t('common.tips'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => { /* nothing */ })

      if (confirmResult !== 'confirm') {
        return
      }

      const response = await qaVendorHttp.souQuestion.delete(this.souType, { id: questionId })
      if (response) {
        // 删除成功
        this.$message.success(this.$t('common.successDelete'))
        this.getQuestionTableData()
      }
    },

    /* 供应商 - 撤回质疑 */
    async withdrawQuestion ({ questionId }) {
      const response = await qaVendorHttp.souQuestion.reject(this.souType, { questionId: questionId })
      if (response) {
        this.$message.success(this.$t('common.successWithdraw'))
        this.getQuestionTableData()
      }
    },

    /* 新增/查看 质疑 */
    openQuestionDetailDialog (type, row = {}) {
      this.questionDetailPageType = type
      this.editRow = row
      this.questionDetailVisible = true
    },

    /* 新增/查看 澄清 */
    openAnswerDetail (type, row, isFromQuestion = false) {
      this.answerDetailPageType = type
      this.isFromQuestion = isFromQuestion
      this.editRow = row
      this.answerDetailVisible = true
    },

    /* 重新布局两个表格 */
    doLayout () {
      this.$refs.questionTable.doLayout()
      this.$refs.answerTable.doLayout()
    }
  }
}
</script>

<style lang="scss" scoped>
.table-toolbar {
  height: 28px;
  line-height: 28px;
  .table-toolbar-title {
    margin-right: 11px;
  }
}
</style>
