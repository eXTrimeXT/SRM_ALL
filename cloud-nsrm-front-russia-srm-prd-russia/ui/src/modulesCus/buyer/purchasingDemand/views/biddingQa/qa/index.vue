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
        <!-- <el-button
          v-if="userTypeFlag.isVendor"
          type="primary"
          @click="openQuestionDetailDialog('add', {})"
        >
          {{ $t("bidMod.addChallenge") }}
        </el-button> -->
      </p>

      <TableView
        ref="questionTable"
        :table-data="questionTableData"
        :table-header="questionTableHeader"
        :adept-mei-ql="true"
        :pre-query-data="questionTableQueryParam"
        :url="userTypeFlag.isBuyer ? '/api-sou/api-ql/Question/query' : '/api-sou/api-ql/VendorQuestion/query'"
      />

      <!-- 澄清列表 -->
      <p class="table-toolbar">
        <span class="table-toolbar-title">{{ $t("bidMod.clarificationList") }}</span>
        <el-button
          v-if="userTypeFlag.isBuyer"
          type="primary"
          @click="openAnswerDetail('add', {extSource:'INITIATE'}, false)"
        >
          <!-- 新增澄清 -->
          {{ $t("bidMod.addClarification") }}
        </el-button>
      </p>

      <TableView
        ref="answerTable"
        :table-data="answerTableData"
        :table-header="answerTableHeader"
        :adept-mei-ql="true"
        :pre-query-data="answerTableQueryParam"
        :com-active="$attrs['changeTab']"
        :url="userTypeFlag.isBuyer ? '/api-sou/api-ql/Answer/query' : '/api-sou/api-ql/VendorAnswer/query'"
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

      <!--供应商列表-->
      <!-- <supplierList
        v-if="supplierVisible"
        :dataList="supplierListData"
        :dialogVisible="supplierVisible"
      /> -->
      <SrmDialog
        title="供应商"
        size="middle"
        :visible.sync="supplierVisible"
        :close-on-click-modal="false"
        destroy-on-close
        append-to-body
      >
        <el-table
          ref="supplierList"
          :data="supplierListData"
          style="width: 100%"
          border
          :row-height="38"
          max-height="390px"
          highlight-current-row
        >
          <el-table-column prop="vendorName" label="供应商名称" />
        </el-table>
      </SrmDialog>

      <SrmDialog
        :title="openHuifuType == 'view' ? '澄清回复查看' : '澄清回复'"
        size="middle"
        :visible.sync="dialogVisible"
        :close-on-click-modal="false"
        destroy-on-close
        append-to-body
      >
        <FileDynamic
          ref="sceneAttachmentHuifu"
          v-model="huifuData"
          scene-module-code="SCENE_SOU_ANSWER_REPLAY_FILE_ATTACHMENT"
          :business-id="huifuId"
          :editable="openHuifuType !== 'view'"
          :need-init="false"
        />
        <div slot="footer">
          <!--取消-->
          <el-button @click="dialogVisible = false">
            {{ $t("common.cancel") }}
          </el-button>
          <el-button v-if="openHuifuType !== 'view'" @click="huifuFuntion">
            回复
          </el-button>
        </div>
      </SrmDialog>
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
import supplierList from './supplierList'
import { transformMQL } from '@/library/utils/util'
import { qa } from 'modc@/buyer/purchasingDemand/api'
import FileDynamic from 'lib@/components/c-file-management/file-dynamic.vue'

export default {
  name: 'Qa',

  components: {
    TableView,
    FormWrapper,
    QuestionDetail,
    AnswerDetail,
    supplierList,
    FileDynamic
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
      extType: '',
      huifuType: false,
      openHuifuType: '',
      userId: null,
      huifuData: [],
      huifuId: null,
      huifuRow: {},
      dialogVisible: false,
      supplierVisible: false,
      supplierListData: [],
      questionDetailVisible: false,
      answerDetailVisible: false,
      answerDetailPageType: 'add',
      questionDetailPageType: 'add',
      editRow: null,
      questionTableData: [],
      answerTableData: [],
      isFromQuestion: false, // 1 供应商回复
      questionTableQueryParam: {},
      answerTableQueryParam: {},
      searchFormConfig: [
        // 质疑标题
        { prop: 'questionTitle', label: this.$t('bidMod.questionTitle') },
        // 质疑编号
        { prop: 'questionNum', label: this.$t('bidMod.questionNum') },
        // 项目名称
        { prop: 'souName', label: this.$t('bidMod.bidingName') },
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
        },
        // 供应商负责人
        {
          prop: 'extVendorNickname',
          label: this.$t('cusEntry.bidMod.extVendorNickname')
        },
        // 招标负责人
        {
          prop: 'extBidNickname',
          label: this.$t('cusEntry.bidMod.extBidNickname')
        }
      ],
      workFlag: false
    }
  },

  computed: {
    ...mapGetters(['userType', 'user']),

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
        {
          prop: 'vendorName',
          label: '质疑来源',
          minWidth: 130
        },
        // 质疑状态
        {
          prop: 'questionStatus',
          label: this.$t('bidMod.questionStatus'),
          formattor: val => this.$getDictLabel('QUERY_STATUS', val),
          minWidth: 100
        },
        // 项目编号
        // {
        //   prop: 'souNo',
        //   label: this.$t('bidMod.bidingNum'),
        //   minWidth: 150
        // },
        // 项目名称
        {
          prop: 'souName',
          label: this.$t('bidMod.bidingName'),
          minWidth: 150
        },
        {
          prop: 'extIfRecommendFinish',
          label: '推荐是否完成',
          minWidth: 130,
          formattor: val => this.$getDictLabel('YES_OR_NO', val),
          hidden: !this.userTypeFlag.isBuyer
        },
        {
          prop: 'extVendorNickname',
          label: '供应商负责人',
          minWidth: 130,
          hidden: !this.userTypeFlag.isBuyer
        },
        // {
        //   prop: 'extBidEvalLeaderName',
        //   label: '评标组长',
        //   minWidth: 130
        // },
        {
          prop: 'extBidNickname',
          label: '招标负责人',
          minWidth: 130,
          hidden: !this.userTypeFlag.isBuyer
        },
        {
          prop: 'submitTime',
          label: '提出日期',
          minWidth: 130,
          formattor: val => this.$dayjsParse(val)
        },
        {
          prop: 'rejectReason',
          label: '作废原因',
          minWidth: 130,
          hidden: this.userTypeFlag.isBuyer
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
            // && row.extBidUsername == this.user.username
            {
              show: row => this.userTypeFlag.isBuyer && row.questionStatus !== 'CLARIFIED' && row.questionStatus !== 'REPLAYED',
              formattor: () => '质疑回复',
              callback: row => this.openQuestionDetailDialog('huifu', row)
            },
            {
              show: row => row.questionStatus == 'REPLAYED',
              formattor: () => '查看回复',
              callback: row => this.openQuestionDetailDialog('view', row)
            },
            // 采购商 - 驳回质疑
            // {
            //   show: row => this.userTypeFlag.isBuyer && row.questionStatus === 'SUBMITTED',
            //   formattor: () => this.$t('common.toRefuse'),
            //   callback: row => this.rejectQuestion(row)
            // },
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
            }
            // 供应商 - 撤回质疑
            // {
            //   show: row => this.userTypeFlag.isVendor && row.questionStatus === 'SUBMITTED',
            //   formattor: () => this.$t('bidMod.withdraw'),
            //   callback: row => this.withdrawQuestion(row)
            // }
          ]
        }
      ]
    },

    // 澄清表头
    answerTableHeader () {
      if (this.userTypeFlag.isBuyer) {
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
          // 项目名称
          {
            prop: 'souName',
            label: this.$t('bidMod.bidingName'),
            minWidth: 120
          },
          {
            prop: 'vendorName',
            label: this.$t('common.vendorName'),
            minWidth: 120
          },
          {
            prop: 'createdUserName',
            label: '创建人',
            minWidth: 120
          },
          {
            prop: 'extBidNickname',
            label: '招标负责人',
            minWidth: 120,
            show: () => this.userTypeFlag.isBuyer
          },
          {
            prop: 'readNum',
            label: '已阅供应商',
            minWidth: 120,
            showType: 'button',
            btnStyle: 'text',
            callback: row => this.openSupplierList(row)
          },
          {
            prop: 'replayNum',
            label: '已回复供应商数量',
            minWidth: 150
          },
          {
            prop: 'creationDate',
            label: '创建日期',
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
                show: row => this.userTypeFlag.isBuyer && (['DRAFT'].includes(row.answerStatus) || (row.answerStatus == 'WAIT_PUBLISH' && row.extBidUserId == this.userId)),
                formattor: () => this.$t('common.edit'),
                callback: row => this.openAnswerDetail('edit', row, false)
              },
              // 采购商 - 删除澄清
              {
                show: row => this.userTypeFlag.isBuyer && ['DRAFT'].includes(row.answerStatus),
                formattor: () => this.$t('common.delete'),
                callback: row => this.deleteAnswer(row)
              },
              // 采购商 - 发布澄清
              // {
              //   show: row => this.userTypeFlag.isBuyer && row.answerStatus === 'DRAFT',
              //   formattor: () => this.$t('common.publish'),
              //   callback: row => this.publishAnswer(row)
              // },
              // 采购商 - 撤回澄清
              // {
              //   show: row => this.userTypeFlag.isBuyer && row.answerStatus === 'ISSUED',
              //   formattor: () => this.$t('bidMod.withdraw'),
              //   callback: row => this.withdrawAnswer(row)
              // },
              {
                show: row => this.userTypeFlag.isBuyer && row.answerStatus === 'ISSUED' && (row.extBidUserId == this.userId && row.createdId != row.extBidUserId) &&
                  // 2024.3.7 新加校验，extReplayFlag为Y代表已经点过【回复关闭】
                  row.extReplayFlag !== 'Y',
                formattor: () => this.$t('回复'),
                callback: row => this.openHuifu(row, 'huifu')
              },
              {
                show: row => this.userTypeFlag.isBuyer && row.answerStatus === 'ISSUED' && (row.extBidUserId == this.userId && row.createdId != row.extBidUserId) &&
                  // 2024.3.7 新加校验，extReplayFlag为Y代表已经点过【回复关闭】
                  row.extReplayFlag !== 'Y',
                formattor: () => this.$t('结束回复'),
                callback: row => this.finishReplay(row)
              },
              {
                show: row => this.userTypeFlag.isBuyer && row.answerStatus === 'ISSUED' && (row.createdId == this.userId && row.createdId != row.extBidUserId),
                formattor: () => this.$t('查看回复'),
                callback: row => this.openHuifu(row, 'view')
              },
              // 供应商 - 接受澄清
              {
                show: row => this.userTypeFlag.isVendor && row.answerStatus === 'ISSUED' && row.confirmStatus !== 'UNCOMFIRMED',
                formattor: () => this.$t('澄清回复'),
                callback: row => this.openAnswerDetail('view', row, 1)
              }
            ]
          }
        ]
      } else {
        return [
          // 澄清编号
          // {
          //   prop: 'answerNum',
          //   label: this.$t('bidMod.answerNum'),
          //   hidden: this.userTypeFlag.isVendor,
          //   minWidth: 150,
          //   showType: 'button',
          //   btnStyle: 'text',
          //   callback: row => this.openAnswerDetail('view', row, false)
          // },
          // 澄清编号(带澄清回复)
          {
            prop: 'answerNum',
            label: this.$t('bidMod.answerNum'),
            minWidth: 150,
            showType: 'button',
            btnStyle: 'text',
            callback: row => this.openAnswerDetail('view', row, 1)
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
          {
            prop: 'ifReplay',
            label: '澄清回复状态',
            minWidth: 120,
            formattor: val => (val == null || val == '' || !val) ? '未回复' : this.$getDictLabel('ANSWER_REPLAY_STATUS', val)
          },
          // 项目名称
          {
            prop: 'souName',
            label: this.$t('bidMod.bidingName'),
            minWidth: 120
          },
          {
            prop: 'extBidNickname',
            label: '发布人',
            minWidth: 120
          },
          {
            prop: 'creationDate',
            label: '创建日期',
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
                show: row => this.userTypeFlag.isBuyer && (['DRAFT'].includes(row.answerStatus) || (row.answerStatus == 'WAIT_PUBLISH' && row.extBidUserId == this.userId)),
                formattor: () => this.$t('common.edit'),
                callback: row => this.openAnswerDetail('edit', row, false)
              },
              // 采购商 - 删除澄清
              {
                show: row => this.userTypeFlag.isBuyer && ['DRAFT'].includes(row.answerStatus),
                formattor: () => this.$t('common.delete'),
                callback: row => this.deleteAnswer(row)
              },
              // 采购商 - 发布澄清
              // {
              //   show: row => this.userTypeFlag.isBuyer && row.answerStatus === 'DRAFT',
              //   formattor: () => this.$t('common.publish'),
              //   callback: row => this.publishAnswer(row)
              // },
              // 采购商 - 撤回澄清
              // {
              //   show: row => this.userTypeFlag.isBuyer && row.answerStatus === 'ISSUED',
              //   formattor: () => this.$t('bidMod.withdraw'),
              //   callback: row => this.withdrawAnswer(row)
              // },
              {
                show: row => this.userTypeFlag.isBuyer && row.answerStatus === 'ISSUED' && (row.extBidUserId == this.userId && row.createdId != row.extBidUserId),
                formattor: () => this.$t('回复'),
                callback: row => this.openHuifu(row, 'huifu')
              },
              {
                show: row => this.userTypeFlag.isBuyer && row.answerStatus === 'ISSUED' && (row.createdId == this.userId && row.createdId != row.extBidUserId),
                formattor: () => this.$t('查看回复'),
                callback: row => this.openHuifu(row, 'view')
              },
              // 供应商 - 接受澄清
              {
                show: row => this.userTypeFlag.isVendor && row.answerStatus === 'ISSUED' && row.confirmStatus !== 'COMFIRMED',
                formattor: () => this.$t('澄清回复'),
                callback: row => this.openAnswerDetail('huifuView', row, 1)
              }
            ]
          }
        ]
      }
    }
  },

  watch: {
    $route: {
      handler () {
        // 如果路由参数有项目ID，就打开新增澄清弹窗
        const { projectId, souName, souNo, from, formId, taskIndex, formNo, row } = this.$route.params || {}
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
        /* 首页待办、已办跳转 */
        if (from === 'fromFun' && formId) {
          /* 区分待办/已办 */
          switch (taskIndex) {
          case 1:
            /* 待办 */
            if (row.sourceFrom === 'question') {
              /* 质疑 */
              this.openQuestionDetailDialog('huifu', { questionId: formId })
            } else {
              /* 澄清 */
              if (row.moduleName === 'CC_ANSWER_SUBMIT_TODO') {
                /* 澄清提交 */
                this.openAnswerDetail('edit', { answerId: formId }, false)
              } else {
                /* 澄清回复 */
                this.openAnswerDetail('view', { answerId: formId, projectId: row.projectId }, false)
                // this.openHuifu({ answerId: formId, projectId: row.projectId }, 'huifu')
              }
            }
            break
          case 2:
            /* 已办 */
            if (row.sourceFrom === 'question') {
              /* 质疑 */
              this.openQuestionDetailDialog('view', { questionId: formId })
            } else {
              /* 澄清 */
              if (row.moduleName === 'CC_ANSWER_SUBMIT_DONE') {
                /* 澄清提交 */
                this.openAnswerDetail('view', { answerId: formId }, false)
              } else {
                /* 澄清回复 */
                this.openHuifu({ answerId: formId, projectId: row.projectId }, 'view')
              }
            }
            break
          default:
            break
          }
        }
        if (from === 'workCount') {
          this.workFlag = true
          this.getAnswerTableData({
            answerStatus: 'ISSUED',
            ifReplay: 'N'
          })
        }
      },
      immediate: true,
      deep: true
    }
  },

  created () {
    this.userId = this.$store.getters.user.userId
    const flag = this.$route.params?.flag
    const params = this.$route.params
    console.log(params, 'params')
    console.log(flag, 'flag')
    if ((flag && flag == 'bid') || this.$route.query?.flag == 'bid') {
      const extType = this.$route.params.row?.extType
      let row = {}
      if (extType) {
        row = {
          ...params?.row,
          extType: extType
        }
        console.log(row, 'row')
      } else {
        row = this.$attrs.params?.row
        this.editRow = row
        this.questionDetailPageType = 'add'
      }
      // 这个后面可能需要数据转换

      this.openQuestionDetailDialog('add', row)
    }
  },

  mounted () {
    this.$nextTick(() => {
      !this.workFlag && this.getAnswerTableData()
      this.getQuestionTableData()
    })
  },

  activated () {
    this.doLayout()
  },

  methods: {
    huifuFuntion () {
      let datas = {}
      datas.replayFiles = this.huifuData
      datas.answerId = this.huifuId
      datas.projectId = this.huifuRow?.projectId
      datas.answerStatus = 'ISSUED'
      if (this.openHuifuType == 'huifu') {
        datas.replayFiles.forEach(e => {
          e.sceneFileId = null
        })
      }
      const saveData = transformMQL.save('Answer', [datas], 'save')
      qa.answerSave(saveData).then((datas) => {
        this.$message.success('确认成功')
        this.dialogVisible = false
        this.getAnswerTableData()
      })
    },
    async finishReplay (row) {
      const confirmResult = await this.$confirm('确定结束回复？', {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
      if (!confirmResult) return
      const params = transformMQL.save(
        'Answer',
        [{ 'answerId': row.answerId }],
        'replayClose'
      )
      this.$http({
        url: '/api-sou/api-ql/Answer/replayClose',
        method: 'POST',
        data: params,
        loading: true
      }).then(res => {
        this.$message.success('结束回复成功!')
        this.getAnswerTableData()
      })
    },
    openHuifu (row, type) {
      this.dialogVisible = true
      this.huifuId = row.answerId
      this.huifuRow = row
      this.openHuifuType = type
      this.$nextTick(() => {
        this.$refs.sceneAttachmentHuifu.loadFileInfo()
      })
    },
    openSupplierList (row = []) {
      console.log(row)
      const answerId = row.answerId
      const saveData = transformMQL.listPageData({
        type: 'AnswerVendor',
        params: {
          answerId: answerId,
          ifRead: 'Y'
        },
        action: 'query',
        pageNum: 1,
        pageSize: 1000
      })
      qa.answerVendorQuery(saveData).then((datas) => {
        console.log(datas, 'datas')
        const data = datas.data.records
        this.supplierListData = data
        this.supplierVisible = true
      })
    },

    /* 查询列表数据 */
    queryTableData (val = {}) {
      this.getAnswerTableData(val)
      this.getQuestionTableData(val)
    },

    /* 查询 质疑列表数据 */
    getQuestionTableData (val = {}) {
      if (this.userTypeFlag.isVendor) {
        this.questionTableQueryParam = transformMQL.listPageData({
          type: 'VendorQuestion',
          action: 'query',
          params: val
        })
      } else {
        let params = {}
        params.questionStatus = !val.questionStatus ? 'DRAFT' : val.questionStatus
        this.questionTableQueryParam = transformMQL.listPageData({
          type: 'Question',
          action: 'query',
          params: { ...val, ...params },
          filterOperator: params.questionStatus === 'DRAFT' ? { questionStatus: 'ne' } : {}
        })
      }

      this.$nextTick(() => {
        this.$refs.questionTable.query()
      })
    },

    /* 查询 澄清列表数据 */
    getAnswerTableData (val = {}) {
      // 删除供应商负责人字段
      const queryParams = JSON.parse(JSON.stringify(val))
      if (queryParams?.extVendorNickname) {
        Reflect.deleteProperty(queryParams, 'extVendorNickname')
      }
      if (this.userTypeFlag.isVendor) {
        this.answerTableQueryParam = transformMQL.listPageData({
          type: 'VendorAnswer',
          action: 'query',
          params: queryParams
        })
      } else {
        let params = {}
        let defaultFlag = false
        if (!val.answerStatus) {
          defaultFlag = true
        }
        params.answerStatus = !val.answerStatus ? 'COMFIRMED' : val.answerStatus
        this.answerTableQueryParam = transformMQL.listPageData({
          type: 'Answer',
          action: 'query',
          params: { ...val, ...params },
          filterOperator: params.answerStatus === 'COMFIRMED' && defaultFlag ? { answerStatus: 'ne' } : {}
        })
      }
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

      const allparam = { answerId: answerId }
      const saveData = transformMQL.save('Answer', [allparam], 'delete')
      qa.answerDelete(saveData).then(() => {
        this.$message.success(this.$t('common.successDelete'))
        this.getAnswerTableData()
      })
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
      if (type == 'view' && !this.userTypeFlag.isBuyer) {
        const users = this.$store.getters.userInfo
        console.log(users, 'users')
        const obj = {
          supplierId: users?.companyId,
          supplierName: users?.companyName,
          supplierCode: users?.companyCode,
          source: '质疑查看'
        }
        this.$http({
          url: '/api-sou/bids/ip/address/ipAddress/save',
          method: 'POST',
          data: obj
        }).then(async res => {

        })
      }
      this.questionDetailPageType = type
      this.editRow = row
      this.questionDetailVisible = true
    },

    /* 新增/查看 澄清 */
    openAnswerDetail (type, row, isFromQuestion = false) {
      if (type == 'view' && !this.userTypeFlag.isBuyer) {
        const users = this.$store.getters.userInfo
        console.log(users, 'users')

        let obj = {}
        if (isFromQuestion == 1) {
          obj = {
            supplierId: users?.companyId,
            supplierName: users?.companyName,
            supplierCode: users?.companyCode,
            source: '澄清回复'
          }
        } else {
          obj = {
            supplierId: users?.companyId,
            supplierName: users?.companyName,
            supplierCode: users?.companyCode,
            source: '澄清查看'
          }
        }

        this.$http({
          url: '/api-sou/bids/ip/address/ipAddress/save',
          method: 'POST',
          data: obj
        }).then(async res => {

        })
      }
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
