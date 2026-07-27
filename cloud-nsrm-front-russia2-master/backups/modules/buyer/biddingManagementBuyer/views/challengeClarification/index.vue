<template>
  <el-container
    class="flex-container-notab the_challengeClarification_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="preArr"
        @getFormData="queryTableData"
      />

      <!-- 质疑列表 -->
      <p>
        <span class="table-title">{{ $t('bidMod.challengeList') }}</span>
      </p>

      <table-view
        ref="challengeTable"
        :table-data="challengeTableData"
        :table-header="challengeTableHeader"
        :page-size="15"
        :pre-query-data="challengeTableQueryParam"
        url="/api-bid/bidingQuestion/listPage"
      />

      <!-- 澄清列表 -->
      <p>
        <span class="table-title">{{ $t("bidMod.clarificationList") }}</span>
        <el-button
          style="margin-left:11px;"
          type="primary"

          @click="openClarificationDetailDialogVisible('add', {}, false)"
        >
          <!-- 新增澄清 -->
          {{ $t("bidMod.addClarification") }}
        </el-button>
      </p>

      <table-view
        ref="clarificationTable"
        :table-data="clarificationTableData"
        :table-header="clarificationTableHeader"
        :page-size="15"
        :pre-query-data="clarificationTableQueryParam"
        url="/api-bid/bidingAnswer/listPage"
      />

      <!--质疑单弹窗-->
      <challenge-detail-dialog
        v-if="challengeDetailDialogVisible"
        :visible.sync="challengeDetailDialogVisible"
        :edit-row="editRow"
      />

      <!--澄清单弹窗-->
      <clarification-detail-dialog
        v-if="clarificationDetailDialogVisible"
        :visible.sync="clarificationDetailDialogVisible"
        :edit-row="editRow"
        :page-type="clarificationDetailDialogPageType"
        :is-from-question="isFromQuestion"
        @refreshList="queryTableData"
      />
    </el-main>
  </el-container>
</template>

<script>
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import challengeDetailDialog from './challengeDetailDialog'
import clarificationDetailDialog from './clarificationDetailDialog'
import { parseTimeYMD } from 'lib@/composition/origin/composition'

export default {
  name: 'ChallengeClarification',
  components: {
    TableView,
    FormWrapper,
    challengeDetailDialog,
    clarificationDetailDialog
  },
  data () {
    return {
      challengeDetailDialogVisible: false,
      clarificationDetailDialogVisible: false,
      clarificationDetailDialogPageType: 'add',
      editRow: null,
      challengeTableHeader: [],
      challengeTableData: [],
      clarificationTableHeader: [],
      clarificationTableData: [],
      isFromQuestion: false,
      challengeTableQueryParam: {},
      clarificationTableQueryParam: {},
      preArr: [
        { prop: 'bidingNum', label: this.$t('bidMod.bidingNumCla') },
        { prop: 'bidingName', label: this.$t('bidMod.bidingNameCla') },
        { prop: 'questionTitle', label: this.$t('bidMod.questionTitle') },
        { prop: 'questionNum', label: this.$t('bidMod.questionNum') },
        // 质疑状态
        {
          prop: 'questionStatus',
          label: this.$t('bidMod.questionStatus'),
          type: 'dict',
          code: 'QUERY_STATUS'
        },
        { prop: 'answerTitle', label: this.$t('bidMod.answerTitle') },
        { prop: 'answerNum', label: this.$t('bidMod.answerNum') },
        {
          prop: 'answerStatus',
          label: this.$t('bidMod.answerStatus'),
          type: 'dict',
          code: 'CLARIFIED_STATUS'
        }
      ]
    }
  },
  created () {
    this.challengeTableHeader = [
      // 质疑编号
      {
        prop: 'questionNum',
        label: this.$t('bidMod.questionNum'),
        width: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.openChallengeDetailDialog(row)
      },
      {
        prop: 'questionTitle',
        label: this.$t('bidMod.questionTitle'),
        width: 150
      },
      {
        prop: 'questionStatus',
        label: this.$t('bidMod.questionStatus'),
        formattor: val => this.$getDictLabel('QUERY_STATUS', val),
        width: 100
      },
      {
        prop: 'submitTime',
        label: this.$t('bidMod.releaseDatetime'),
        width: 100,
        formattor: val => parseTimeYMD(val)
      },
      {
        prop: 'bidingNum',
        label: this.$t('bidMod.bidingNumCla'),
        width: 150
      },
      {
        prop: 'bidingName',
        label: this.$t('bidMod.bidingNameCla'),
        width: 150
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
        width: 150,
        showType: 'buttons',
        btnStyle: 'text',
        fixed: 'right',
        buttons: [
          // 澄清
          {
            show: ({ questionStatus }) => questionStatus !== 'CLARIFIED',
            formattor: () => this.$t('bidMod.clarification'),
            callback: row => this.openClarificationDetailDialogVisible('add', row, true)
          },
          // 驳回
          {
            show: ({ questionStatus }) => questionStatus === 'SUBMITTED',
            formattor: () => this.$t('common.toRefuse'),
            callback: row => this.toRefuse(row)
          }
        ]
      }
    ]

    this.clarificationTableHeader = [
      {
        prop: 'answerNum',
        label: this.$t('bidMod.answerNum'),
        width: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.openClarificationDetailDialogVisible('view', row, false)
      },
      {
        prop: 'answerTitle',
        label: this.$t('bidMod.answerTitle'),
        width: 150
      },
      // 澄清状态
      {
        prop: 'answerStatus',
        label: this.$t('bidMod.answerStatus'),
        width: 100,
        formattor: val => this.$getDictLabel('CLARIFIED_STATUS', val)
      },
      {
        prop: 'submitTime',
        label: this.$t('bidMod.releaseDatetime'),
        width: 100,
        formattor: val => parseTimeYMD(val)
      },
      {
        prop: 'questionNum',
        label: this.$t('bidMod.questionNum'),
        width: 150,
        formattor: val => (val || this.$t('bidMod.tendererIssued')) // 招标方发布
      },
      {
        prop: 'bidingNum',
        label: this.$t('bidMod.bidingNumCla'),
        width: 150
      },
      {
        prop: 'bidingName',
        label: this.$t('bidMod.bidingNameCla'),
        minWidth: 120
      },
      {
        prop: 'operation',
        label: this.$t('bidMod.operation'),
        width: 160,
        showType: 'buttons',
        btnStyle: 'text',
        fixed: 'right',
        buttons: [
          // 编辑
          {
            show: ({ answerStatus }) => answerStatus === 'DRAFT',
            callback: row => this.openClarificationDetailDialogVisible('edit', row, false),
            formattor: () => this.$t('common.edit')
          },
          // 删除
          {
            show: ({ answerStatus }) => answerStatus === 'DRAFT',
            callback: row => this.deleteItem(row),
            formattor: () => this.$t('common.delete')
          },
          // 发布
          {
            show: ({ answerStatus }) => answerStatus === 'DRAFT',
            callback: row => this.publishOne(row),
            formattor: () => this.$t('common.publish')
          },
          // 撤回
          {
            show: ({ answerStatus }) => answerStatus === 'ISSUED',
            callback: row => this.rejectItem(row),
            formattor: () => this.$t('bidMod.withdraw')
          }
        ]
      }
    ]

    this.getChallengeTableData({ questionStatus: 'SUBMITTED' })
    this.getClarificationTableData()
  },
  mounted () {
    // 如果路由参数有招标ID，就打开新增澄清弹窗
    const { bidingId, bidingName, bidingCode } = this.$route.params || {}
    if (bidingId && bidingName && bidingCode) {
      this.openClarificationDetailDialogVisible(
        'add',
        {
          bidingId,
          bidingName,
          bidingCode
        },
        true
      )
    }
  },
  activated () {
    this.dolayout()
  },
  methods: {
    /* 驳回 */
    toRefuse (row) {
      // 请输入驳回原因
      this.$prompt(
        this.$t('bidMod.msgRejectReason'),
        this.$t('common.toRefuse'),
        {
          confirmButtonText: this.$t('common.confirm'), // 确定
          cancelButtonText: this.$t('common.cancel') // 取消
        }
      ).then(({ value }) => {
        this.$http({
          url: '/api-bid/bidingQuestion/reject',
          method: 'POST',
          data: {
            questionId: row.questionId,
            rejectReason: value
          },
          loading: true
        }).then(() => {
          this.$message.success(this.$t('bidMod.successRefuse')) // 成功驳回！
          this.queryTableData()
        })
      })
    },

    dolayout () {
      this.$refs.challengeTable.doLayout()
      this.$refs.clarificationTable.doLayout()
    },

    queryTableData (v = {}) {
      const { questionStatus = 'SUBMITTED', ...rest } = v
      this.getClarificationTableData(v)
      this.getChallengeTableData({ questionStatus, ...rest })
    },

    getChallengeTableData (v) {
      this.challengeTableQueryParam = v
      this.$nextTick(() => {
        this.$refs.challengeTable.query()
      })
    },
    getClarificationTableData (v) {
      this.clarificationTableQueryParam = v
      this.$nextTick(() => {
        this.$refs.clarificationTable.query()
      })
    },

    /* 删除澄清 */
    deleteItem ({ answerId }) {
      // 当前操作将删除数据，确认是否删除数据？
      this.$confirm(this.$t('common.ifDeleteData'), this.$t('common.tips'), {
        confirmButtonText: this.$t('common.confirm'), // 确定
        cancelButtonText: this.$t('common.cancel'), // 取消
        type: 'warning'
      }).then(() => {
        this.$http({
          url: `/api-bid/bidingAnswer/delete/${answerId}`,
          method: 'DELETE',
          loading: true
        }).then(() => {
          // 删除成功
          this.$message.success(this.$t('common.successDelete'))
          this.queryTableData()
        })
      }).catch(() => {
        // 已取消删除
        this.$message.info(this.$t('common.cancelDelete'))
      })
    },

    /* 撤回 */
    rejectItem ({ answerId }) {
      this.$http({
        url: `/api-bid/bidingAnswer/withDraw/${answerId}`,
        method: 'POST',
        loading: true
      }).then(() => {
        // 撤回成功!
        this.$message.success(this.$t('common.successWithdraw'))
        this.queryTableData()
      })
    },

    /* 发布 */
    publishOne (row) {
      this.$http({
        url: `/api-bid/bidingAnswer/publish/${row.answerId}`,
        method: 'POST',
        loading: true
      }).then(() => {
        this.queryTableData()
        // 发布成功!
        this.$message.success(this.$t('common.successPublish'))
      })
    },

    /* 查看质疑 */
    openChallengeDetailDialog (row) {
      this.editRow = row
      this.challengeDetailDialogVisible = true
    },

    /* 新增澄清 / 查看澄清 */
    openClarificationDetailDialogVisible (type, row, isFromQuestion = false) {
      this.clarificationDetailDialogPageType = type
      this.isFromQuestion = isFromQuestion
      this.editRow = row
      this.clarificationDetailDialogVisible = true
    }
  }
}
</script>
