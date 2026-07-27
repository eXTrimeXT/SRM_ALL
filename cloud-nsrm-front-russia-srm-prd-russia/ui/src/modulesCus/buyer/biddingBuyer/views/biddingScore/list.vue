<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <FormWrapper :form-array="searchFormConfig" @getFormData="getQueryData" />
      <TableView
        ref="biddingScoreTable"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        open-custom-table
        :com-active="$attrs['changeTab']"
        url="/api-sou/ext/buyer/bid/init/getTechScore"
      />
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { daterangePayloadFormat } from 'lib@/composition/commonComposition'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import biddingScoreDetail from './detail'

export default {
  name: 'BiddingScoreList',

  components: {
    TableView,
    FormWrapper
  },

  mixins: [tabTodoWatch, tabTodoMixin],

  data () {
    return {
      tableData: [],
      queryParam: {},
      searchFormConfig: [
        // 招标单号
        { prop: 'souNo', label: this.$t('cusEntry.biddingSettings.bidingNum') },
        // 项目编号
        { prop: 'extProjectNo', label: this.$t('bidMod.bidingNum') },
        // 招标状态
        {
          prop: 'projectStatus',
          label: this.$t('cusEntry.biddingSettings.projectStatus'),
          type: 'dict',
          code: 'SOU_BIDDING_PRO_STATUS'
        },
        { prop: 'fullName', label: '评标人' },
        // 项目名称
        { prop: 'souName', label: this.$t('bidMod.bidingName') },
        {
          prop: 'creationDate',
          label: this.$t('common.creationDate'),
          type: 'daterange'

        }
      ],
      tableHeader: [
        // 招标单号
        {
          prop: 'souNo',
          label: this.$t('cusEntry.biddingSettings.bidingNum'),
          minWidth: 150
        },
        // 招标项目编号
        {
          prop: 'extProjectNo',
          label: this.$t('bidMod.bidingNumCla'),
          minWidth: 150
        },
        // 项目名称
        {
          prop: 'souName',
          label: this.$t('bidMod.bidingName'),
          minWidth: 150
        },
        // 公司
        {
          prop: 'extOrgOuName',
          label: this.$t('cusEntry.common.company'),
          minWidth: 150
        },
        // 单据状态
        {
          prop: 'projectStatus',
          label: this.$t('bidMod.billstatus'),
          minWidth: 120,
          dataType: 'dict',
          code: 'SOU_BIDDING_PRO_STATUS'
        },
        {
          prop: 'fullName',
          label: '评标人',
          minWidth: 120
        },
        {
          prop: 'scoreStatus',
          label: '评分状态',
          minWidth: 120,
          dataType: 'dict',
          code: 'SOU_TECH_SCORE_STATUS'
        },
        {
          prop: 'extRejectReason',
          label: '驳回原因',
          minWidth: 180
        },
        // 创建日期
        {
          prop: 'creationDate',
          label: this.$t('common.creationDate'),
          minWidth: 120,
          formattor: val => this.$dayjsParse(val)
        },
        {
          prop: 'operation',
          label: this.$t('bidMod.operation'),
          width: 100,
          showType: 'buttons',
          fixed: 'right',
          btnStyle: 'text',
          buttons: [
            // 已评分状态展示【查看】按钮
            {
              show: row => row.scoreStatus == 'FINISHED' && row.projectStatus != 'ARCHIVE_DONE',
              formattor: () => this.$t('common.view'),
              callback: row => this.openDetailTab('view', row)
            },
            // 【未评分、已驳回】状态展示【评分】
            {
              show: row => ['UNFINISHED', 'REJECT'].includes(row.scoreStatus),
              formattor: () => this.$t('cusEntry.bidMod.score'),
              callback: row => this.openDetailTab('edit', row)
            }
          ]
        }
      ]
    }
  },

  watch: {
    '$route.params': {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler (nVal) {
        const { from, funName, row, taskIndex } = nVal
        if (from == 'fromFun' && funName == 'biddingScore') {
          // 首页待办跳转过来
          let rowObj = { projectId: row.formId, extProjectNo: row.formNo, groupId: row.groupId, techScoreHeadId: row.techScoreHeadId }
          if (taskIndex === 1) { // 待办
            this.openDetailTab('edit', rowObj)
          } else if (taskIndex === 2) { // 已办
            this.openDetailTab('view', rowObj)
          }
        }
      }
    }
  },

  created () {
    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  methods: {
    /* 查询 */
    getQueryData (payload) {
      if (payload) {
        // 格式化时间范围
        payload = daterangePayloadFormat(payload, [
          // 创建时间
          { prop: 'creationDate', fromProp: 'creationDateFrom', toProp: 'creationDateTo' }
        ])
      }
      this.queryParam = payload || this.queryParam
      this.$nextTick(() => {
        this.$refs.biddingScoreTable.query()
      })
    },

    /* 打开新增 or 编辑tab */
    openDetailTab (type, row = {}) {
      let tab = {
        component: biddingScoreDetail,
        params: {
          flag: type,
          row: row,
          tabName: `biddingScoreDetail${row.extProjectNo}`
        },
        title: row.extProjectNo,
        name: `biddingScoreDetail${row.extProjectNo}`
      }
      this.$emit('tab-add', tab)
    }
  }
}
</script>
