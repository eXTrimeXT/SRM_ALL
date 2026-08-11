<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <FormWrapper :form-array="searchFormConfig" @getFormData="getQueryData" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <!-- 自定义导出 -->
          <ExportExcel
            :page-url="tableViewUrl"
            :filter-params="computedQueryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            code="expertDatabase:export"
            export-mode="front"
            type="primary"
          />
        </template>
      </MainHeader>

      <TableView
        ref="list"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        :checkbox="false"
        :checkChange="handleCurrentChange"
        open-custom-table
        :url="tableViewUrl"
        :adeptMeiQl="true"
        @afterQuery="afterQuery"
      />
    </el-main>

    <!-- 查看评价弹窗 -->
    <ViewResultDialog
      ref="viewResultDialog"
      :visible.sync="viewResultDialogVisible"
      :editRows="currentRows"
    />

    <!-- 评价 -->
    <ScoreResultDialog
      ref="scoreResultDialog"
      :visible.sync="scoreResultDialogVisible"
      :editRows="currentRows"
      @confirm="scoreResultDialogConfirm"
    />
  </el-container>
</template>

<script>
import { expApplyHttp, expReviewHttp } from 'modcb@/expertLibrary/api'
import { tabTodoWatch } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import ExportExcel from 'lib@/components/export-excel'
import { transformMQL, transformTimeQuery } from 'lib@/utils/util'
import ViewResultDialog from './dialog/viewResultDialog'
import ScoreResultDialog from './dialog/scoreResultDialog'
import { mapGetters } from 'vuex'

export default {
  name: 'ExpertReviewList',

  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel,
    ViewResultDialog,
    ScoreResultDialog
  },

  mixins: [tabTodoWatch],

  data () {
    return {
      tableViewUrl: expReviewHttp.listPageUrl,
      tableHeader: [],
      tableData: [],
      viewResultDialogVisible: false,
      scoreResultDialogVisible: false,
      currentRows: [],
      searchFormConfig: [
        {
          prop: 'souName',
          // label: '项目名称'
          label: () => this.$t('bidMod.bidingName')
        },
        {
          prop: 'managerUserName',
          // label: '招标专家',
          label: () => this.$t('cusEntry.bidSuperviseReport.souPrincipal'),
          type: 'quicksearch',
          showKey: 'nickname',
          propKey: 'username',
          name: 'scc_rbac_user_display'
        },
        // {
        //   prop: 'applyDate',
        //   label: '工号',
        //   type: 'quicksearch',
        //   showKey: '',
        //   name: ''
        // },
        {
          prop: 'leaderUserName',
          // label: '评标委员会主席',
          label: () => this.$t('cusEntry.bidSuperviseReport.leaderPrincipal'),
          type: 'quicksearch',
          showKey: 'nickname',
          propKey: 'username',
          name: 'scc_rbac_user_display'
        },
        // {
        //   prop: 'projectAddress',
        //   label: '项目所在地'
        // },
        {
          prop: 'scoreTime',
          // label: '评价日期',
          label: this.$t('cusEntry.supplement20250205.evaluationDate'),
          type: 'daterange'
        }
      ],
      queryParam: {},
      selectedRows: [], // 标记勾选行
      dictCodes: {
        jobStatus: 'EXT_SOU_EXPERT_JOB_STATUS',
        hasQuite: 'YES_OR_NO',
        scoreStatus: 'EXT_SOU_EXPERT_SCORE_STATUS'
      }
    }
  },

  computed: {
    ...mapGetters(['userInfo']),
    computedQueryParam () {
      let { pageNum, pageSize } = this.queryParam
      return {
        meiqlPayload: {
          ...this.queryParam
        },
        pageNum,
        pageSize
      }
    }
  },

  watch: {
    '$route.params': {
      // 寻源需求等其它地方跳转过来
      handler (nVal) {

      },
      immediate: true,
      deep: true
    }
  },

  mounted () {
    console.log('userInfo', this.userInfo)
    this.tableHeader = [
      {
        prop: 'souName',
        // label: '项目名称',
        label: this.$t('bidMod.bidingName'),
        minWidth: 150
      },
//      {
//        prop: 'projectAddress',
//        label: '项目所在地',
//       minWidth: 150
//      },
      {
        prop: 'totalAmountByTenKilo',
        // label: '概算金额',
        label: () => this.$t('cusEntry.supplement20250205.estimatedAmount'),
        minWidth: 120
      },
      {
        prop: 'leaderNickname',
        // label: '评标委员会主席',
        label: () => this.$t('cusEntry.bidSuperviseReport.leaderPrincipal'),
        minWidth: 180,
        formattor: (val, row) => row.leaderUsername ? `${row.leaderNickname}(${row.leaderUsername})` : ''
      },
      {
        prop: 'managerNickname',
        // label: '招标专家',
        label: () => this.$t('cusEntry.bidSuperviseReport.souPrincipal'),
        minWidth: 180,
        formattor: (val, row) => row.managerUsername ? `${row.managerNickname}(${row.managerUsername})` : ''
      },
      {
        prop: 'expertUsername',
        // label: '评标人工号',
        label: () => this.$t('cusEntry.supplement20250205.evaluationExpertID'),
        minWidth: 130
      },
      {
        prop: 'expertFullName',
        // label: '评标人',
        label: () => this.$t('cusEntry.supplement20250205.evaluationPerson'),
        minWidth: 120
      },
      {
        prop: 'jobStatus',
        // label: '在职状态',
        label: () => this.$t('cusEntry.supplement20250205.employmentStatus'),
        minWidth: 120,
        dataType: 'dict',
        code: 'EXT_SOU_EXPERT_JOB_STATUS'
      },
      {
        prop: 'hasQuite',
        // label: '是否退出',
        label: () => this.$t('cusEntry.supplement20250205.confirmExit'),
        minWidth: 120,
        dataType: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'scoreStatus',
        // label: '评价状态',
        label: () => this.$t('cusEntry.supplement20250205.evaluationStatus'),
        minWidth: 150,
        dataType: 'dict',
        code: 'EXT_SOU_EXPERT_SCORE_STATUS'
      },
      {
        prop: 'scoreResult',
        // label: '评价结果',
        label: () => this.$t('vendorMod.indicatorLineDes'),
        minWidth: 120
      },
      {
        prop: 'scoreTime',
        // label: '评价时间',
        label: () => this.$t('perfMod.mFeedbackTime2'),
        minWidth: 120,
        dataType: 'dateTime'
      },
      {
        prop: 'operation',
        label: this.$t('bidMod.operation'),
        minWidth: 130,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          // 评价
          {
            show: row => {
              let flagIndex = row.scoreLineList.findIndex(item => {
                // if (item.proxyUserId) {
                //   return item.proxyUserId === this.userInfo.userId
                // } else {
                //   return item.username === this.userInfo.username
                // }
                return item.username === this.userInfo.username
              })
              if (flagIndex > -1 && !row.scoreLineList[flagIndex].score) {
                return true
              }
              return false
            },
            // formattor: () => '评价',
            formattor: () => this.$t('vendorMod.evaluate'),
            code: 'expertReview:score',
            callback: row => {
              this.scoreResultDialogVisible = true
              this.currentRows = row.scoreLineList.filter(item => item.username === this.userInfo.username)
            }
          },
          // 查看
          {
            // formattor: () => '查看',
            formattor: () => this.$t('common.view'),
            code: 'expertReview:view',
            callback: row => {
              this.viewResultDialogVisible = true
              this.currentRows = row.scoreLineList || []
            }
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  methods: {
    /* 查询列表数据 */
    getQueryData (v = {}) {
      const { scoreTime, ...rest } = v
      let params = rest
      if (scoreTime && scoreTime.length) {
        params.scoreTimeFrom = scoreTime[0] + ' 00:00:00'
        params.scoreTimeTo = scoreTime[1] + ' 23:59:59'
      }
      this.queryParam = transformMQL.listPageData({
        type: 'ExtSouExpertScoreForBuyer',
        action: 'queryExpertScores',
        params,
        payloadFlag: 'N'
      })

      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    afterQuery (data) {
      // if (data.length) {
      //   data.forEach(item => {
      //     const { scoreLineList = [] } = item
      //     item.leaderName = scoreLineList.filter(innerItem => innerItem.groupType === 'SOU_MANAGER')
      //     item.managerName = scoreLineList.filter(innerItem => innerItem.groupType === 'SOU_MANAGER')
      //   })
      // }
    },

    async scoreResultDialogConfirm (data) {
      let transformParams = transformMQL.save('ExtSouExpertScoreForBuyer', data, 'expertDoScore')
      const response = await expReviewHttp.expertDoScore(transformParams)
      if (response) {
        this.$message.success(this.$t('common.success'))
        this.scoreResultDialogVisible = false
        this.getQueryData()
      }
    },

    /* 选中行 */
    handleCurrentChange (val) {
      console.log('val:::', val)
      this.selectedRows = val
    }
  }
}
</script>
