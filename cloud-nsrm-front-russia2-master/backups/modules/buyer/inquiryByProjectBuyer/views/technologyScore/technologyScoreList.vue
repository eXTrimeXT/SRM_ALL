<template>
  <el-container
    class="flex-container the_biddingProject_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="searchFormConfig"
        form-label-width="120px"
        @getFormData="getQueryData"
      />

      <TableView
        ref="bargainProjectTable"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        url="/api-brg/techProposal/queryTechProgressReview"
        style="margin-top: 10px"
      />
    </el-main>
  </el-container>
</template>

<script>
/**
 * 技术标评分列表
 */
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { parseTimeYMD } from 'lib@/composition/origin/composition'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import technologyScoreDetail from './technologyScoreDetail'

export default {
  name: 'TechnologyScoreList',
  components: {
    TableView,
    FormWrapper
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      tableData: [],
      pageSize: 15,
      tableHeader: [
        // 项目编号
        {
          prop: 'bargainNum',
          label: this.$t('bidMod.bidingNum'),
          minWidth: 150
        },
        // 项目名称
        {
          prop: 'bargainName',
          label: this.$t('bidMod.bidingName'),
          minWidth: 150,
          formattor: val => val || '--'
        },
        // 项目状态
        {
          prop: 'bargainStatus',
          label: this.$t('bidMod.bidingStatus'),
          minWidth: 100,
          dataType: 'dict',
          code: 'BRG_PROJECT_STATUS'
        },
        // 技术评分状态 scoreStatus
        {
          prop: 'scoreStatus',
          label: this.$t('bidMod.biddingManagementSupplier.scoreStatus'),
          minWidth: 125,
          dataType: 'dict',
          code: 'SCORE_PROGERESS'
        },
        // 评分规则
        {
          prop: 'evaluateMethod',
          label: this.$t('bidMod.evaluateMethod'),
          minWidth: 100,
          dataType: 'dict',
          code: 'BRG_EVALUATE_METHOD'
        },
        // 当前轮次
        {
          prop: 'currentRound',
          label: this.$t('bidMod.currentRound'),
          minWidth: 100
        },
        // 发布人
        {
          prop: 'createdBy',
          label: this.$t('bidMod.publishBy'),
          minWidth: 100
        },
        // 创建日期
        {
          prop: 'creationDate',
          label: this.$t('bidMod.creationDate'),
          minWidth: 100,
          formattor: val => parseTimeYMD(val)
        },
        // 发布时间
        {
          prop: 'releaseDatetime',
          label: this.$t('bidMod.releaseDatetime'),
          minWidth: 100,
          formattor: val => parseTimeYMD(val)
        },
        // 报价截止时间
        {
          prop: 'bargainEndDatetime',
          label: this.$t('bidMod.stopTime'),
          minWidth: 100,
          formattor: val => parseTimeYMD(val)
        },
        {
          prop: 'operation',
          label: this.$t('bidMod.operation'),
          minWidth: 100,
          showType: 'buttons',
          fixed: 'right',
          btnStyle: 'text',
          buttons: [
            // 技术评分
            {
              // 当前轮次：1 && 技术评分状态：未完成
              show: row => row.currentRound === 1 && row.scoreStatus === 'UNFINISHED',
              callback: row => this.editOrgData(row),
              formattor: () => this.$t('bidMod.technicalScore')
            },
            // 查看
            {
              // 技术评分状态：已完成
              show: row => row.scoreStatus === 'FINISHED',
              callback: row => this.editOrgData(row),
              formattor: () => this.$t('common.view')
            }
          ]
        }
      ],
      searchFormConfig: [
        // 项目编号
        { prop: 'bargainNum', label: this.$t('bidMod.bidingNum') },
        // 项目名称
        { prop: 'bargainName', label: this.$t('bidMod.bidingName') },
        // 项目状态
        {
          prop: 'bargainStatus',
          label: this.$t('bidMod.bidingStatus'),
          type: 'dict',
          code: 'BRG_PROJECT_STATUS'
        },
        // 评分规则
        {
          prop: 'evaluateMethod',
          label: this.$t('bidMod.evaluateMethod'),
          type: 'dict',
          code: 'BRG_EVALUATE_METHOD'
        },
        // 发布人
        {
          prop: 'createdBy',
          label: this.$t('bidMod.publishBy'),
          type: 'quicksearch',
          showKey: 'username',
          name: 'scc_rbac_user_display'
        },
        // 技术评分状态
        {
          prop: 'scoreStatus',
          label: this.$t('bidMod.biddingManagementSupplier.scoreStatus'),
          type: 'dict',
          code: 'SCORE_PROGERESS'
        },
        // 创建时间
        {
          prop: 'creationDate',
          label: this.$t('bidMod.creationDate'),
          type: 'daterange'
        },
        // 发布时间
        {
          prop: 'releaseDatetime',
          label: this.$t('bidMod.releaseDatetime'),
          type: 'daterange'
        },
        // 截止时间
        {
          prop: 'bargainEndDate',
          label: this.$t('bidMod.stopTime'),
          type: 'daterange'
        }
      ],
      queryParam: {}
    }
  },
  mounted () {
    this.$nextTick(() => {
      this.getQueryData()
    })
  },
  methods: {
    /* 列表查询 */
    getQueryData (payload) {
      if (payload) {
        // 创建时间
        if (payload.creationDate && Array.isArray(payload.creationDate) && payload.creationDate.length === 2) {
          payload = {
            ...payload,
            creationDateFrom: payload.creationDate[0],
            creationDateTo: payload.creationDate[1]
          }
          delete payload.creationDate
        }
        // 发布时间
        if (payload.releaseDatetime && Array.isArray(payload.releaseDatetime) && payload.releaseDatetime.length === 2) {
          payload = {
            ...payload,
            releaseDatetimeFrom: payload.releaseDatetime[0],
            releaseDatetimeTo: payload.releaseDatetime[1]
          }
          delete payload.releaseDatetime
        }
        // 截止时间 bargainEndDateFrom
        if (payload.bargainEndDate && Array.isArray(payload.bargainEndDate) && payload.bargainEndDate.length === 2) {
          payload = {
            ...payload,
            bargainEndDateFrom: payload.bargainEndDate[0],
            bargainEndDateTo: payload.bargainEndDate[1]
          }
          delete payload.bargainEndDate
        }
      }
      this.queryParam = payload || this.queryParam
      this.$nextTick(() => {
        this.$refs.bargainProjectTable.query()
      })
    },

    /* 技术评分详情页 */
    editOrgData (row) {
      let tab = {
        component: technologyScoreDetail,
        params: {
          flag: 'edit',
          row: row,
          tabName: 'technologyScoreDetail' + row.bargainName
        },
        title: row.bargainName,
        name: 'technologyScoreDetail' + row.bargainName
      }
      this.$emit('tab-add', tab)
    }
  }
}
</script>
