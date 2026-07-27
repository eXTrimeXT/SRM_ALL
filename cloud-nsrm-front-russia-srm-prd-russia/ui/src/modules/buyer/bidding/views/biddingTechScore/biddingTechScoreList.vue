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
        ref="biddingProjectTable"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        :url="tableViewUrl"
        style="margin-top: 10px"
      />
    </el-main>
  </el-container>
</template>

<script>
/**
 * 技术标评分列表
 */
import { bidBuyerHttp } from 'modb@/bidding/api'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { SOU_TECH_SCORE_STATUS_ENUM } from 'lib@/composition/origin/enum'
import { daterangePayloadFormat } from '@/library/composition/commonComposition'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import biddingTechScoreDetail from './biddingTechScoreDetail.vue'

export default {
  name: 'TechnologyScoreList',

  components: {
    TableView,
    FormWrapper
  },

  mixins: [tabTodoWatch, tabTodoMixin],

  data () {
    return {
      tableViewUrl: bidBuyerHttp.tech.techProgressReviewUrl,
      tableData: [],
      pageSize: 15,
      tableHeader: [
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
          minWidth: 150,
          formattor: val => val || '--'
        },
        // 项目状态
        {
          prop: 'projectStatus',
          label: this.$t('bidMod.bidingStatus'),
          minWidth: 100,
          dataType: 'dict',
          code: 'SOU_PROJECT_STATUS'
        },
        // 技术评分状态 scoreStatus
        {
          prop: 'scoreStatus',
          label: this.$t('bidMod.biddingManagementSupplier.scoreStatus'),
          minWidth: 125,
          dataType: 'dict',
          code: 'SOU_TECH_SCORE_STATUS'
        },
        // 评分规则
        {
          prop: 'scoreRuleType',
          label: this.$t('bidMod.evaluateMethod'),
          minWidth: 100,
          dataType: 'dict',
          code: 'SOU_SCORE_RULE_TYPE'
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
          formattor: val => this.$dayjsParse(val)
        },
        // 发布时间
        {
          prop: 'publishTime',
          label: this.$t('bidMod.releaseDatetime'),
          minWidth: 100,
          formattor: val => this.$dayjsParse(val)
        },
        // 报价截止时间
        {
          prop: 'orderEndTime',
          label: this.$t('bidMod.stopTime'),
          minWidth: 100,
          formattor: val => this.$dayjsParse(val)
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
              show: row => row.currentRound === 1 && row.scoreStatus === SOU_TECH_SCORE_STATUS_ENUM.UNFINISHED,
              callback: row => this.editOrgData(row),
              formattor: () => this.$t('bidMod.technicalScore')
            },
            // 查看
            {
              // 技术评分状态：已完成
              show: row => row.scoreStatus === SOU_TECH_SCORE_STATUS_ENUM.FINISHED,
              callback: row => this.editOrgData(row),
              formattor: () => this.$t('common.view')
            }
          ]
        }
      ],
      searchFormConfig: [
        // 项目编号
        { prop: 'souNo', label: this.$t('bidMod.bidingNum') },
        // 项目名称
        { prop: 'souName', label: this.$t('bidMod.bidingName') },
        // 项目状态
        {
          prop: 'projectStatus',
          label: this.$t('bidMod.bidingStatus'),
          type: 'dict',
          code: 'SOU_PROJECT_STATUS'
        },
        // 评分规则
        {
          prop: 'scoreRuleType',
          label: this.$t('bidMod.evaluateMethod'),
          type: 'dict',
          code: 'SOU_SCORE_RULE_TYPE'
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
          code: 'SOU_TECH_SCORE_STATUS'
        },
        // 创建时间
        {
          prop: 'creationDate',
          label: this.$t('bidMod.creationDate'),
          type: 'daterange'
        },
        // 发布时间
        {
          prop: 'publishTime',
          label: this.$t('bidMod.releaseDatetime'),
          type: 'daterange'
        },
        // 截止时间
        {
          prop: 'orderEndTime',
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
        // 格式化时间范围
        payload = daterangePayloadFormat(payload, [
          // 创建时间
          { prop: 'creationDate', fromProp: 'creationDateFrom', toProp: 'creationDateTo' },
          // 发布时间
          { prop: 'publishTime', fromProp: 'publishTimeFrom', toProp: 'publishTimeTo' },
          // 截止时间
          { prop: 'orderEndTime', fromProp: 'orderEndTimeFrom', toProp: 'orderEndTimeTo' }
        ])
      }
      this.queryParam = payload || this.queryParam
      this.$nextTick(() => {
        this.$refs.biddingProjectTable.query()
      })
    },

    /* 技术评分详情页 */
    editOrgData (row) {
      let tab = {
        component: biddingTechScoreDetail,
        params: {
          flag: 'edit',
          row: row,
          tabName: `biddingTechScoreDetail${row.souName}`
        },
        title: row.souName,
        name: `biddingTechScoreDetail${row.souName}`
      }
      this.$emit('tab-add', tab)
    }
  }
}
</script>
