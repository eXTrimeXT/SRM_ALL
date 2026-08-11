<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <FormWrapper :form-array="searchFormConfig" @getFormData="getQueryData" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <!--新增-->
          <el-button type="primary" @click="openDetailTab('add')">
            {{ $t('common.add') }}
          </el-button>
        </template>
      </MainHeader>

      <TableView
        ref="list"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        :adeptMeiQl="true"
        open-custom-table
        :url="tableViewUrl"
      />
    </el-main>
  </el-container>
</template>

<script>
import { SOU_AUCT_PROJECT_STATUS_ENUM } from 'lib@/composition/competition/utils'
import { carBuyerHttp } from 'modb@/competition/api'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import competitionManagementDetail from './competitionManagementDetail'
import { transformMQL } from 'lib@/utils/util'
import { tabTodoWatch } from '@/utils/mixins'

export default {
  name: 'CompetitionManagementList',

  components: {
    TableView,
    MainHeader,
    FormWrapper
  },

  mixins: [tabTodoWatch],

  data () {
    return {
      tableViewUrl: carBuyerHttp.init.listPageUrl,
      tableHeader: [
        // 竞价单号
        {
          prop: 'souNo',
          label: this.$t('bidMod.competitionLts.souNo'), // '竞价单号'
          minWidth: 150,
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.openDetailTab('view', row)
        },
        // 竞价标题
        {
          prop: 'souName',
          label: this.$t('bidMod.competitionLts.souName'), // '竞价标题'
          minWidth: 180
        },
        // 竞价状态
        {
          prop: 'projectStatus',
          label: this.$t('bidMod.competitionLts.souStatus'), // '竞价状态'
          minWidth: 100,
          dataType: 'dict',
          code: 'SOU_AUCT_PROJECT_STATUS'
        },
        // 审核状态
        {
          prop: 'createApprovalStatus',
          label: this.$t('bidMod.auditStatus'),
          minWidth: 100,
          dataType: 'dict',
          code: 'SOU_APPROVAL_STATUS'
        },
        // 报价回应
        {
          prop: 'quoteCnt',
          label: this.$t('bidMod.quoteCnt'),
          minWidth: 120,
          formattor: (_val, row) => (row.orderCount || 0) + ' / ' + (row.inviteCount || 0)
        },
        // 评分规则
        {
          prop: 'scoreRuleType',
          label: this.$t('bidMod.evaluateMethod'),
          minWidth: 100,
          dataType: 'dict',
          code: 'SOU_AUCT_SCORE_RULE_TYPE'
        },
        // 创建人
        {
          prop: 'createdUserName',
          label: this.$t('common.creator'),
          minWidth: 100
        },
        // 创建时间
        {
          prop: 'creationDate',
          label: this.$t('bidMod.creationDate'),
          minWidth: 100,
          formattor: val => this.$parseTime(val)
        },
        // 发布时间
        {
          prop: 'publishTime',
          label: this.$t('bidMod.releaseDatetime'),
          minWidth: 100,
          formattor: val => this.$parseTime(val)
        },
        // 截止时间
        {
          prop: 'orderEndTime',
          label: this.$t('bidMod.stopTime'),
          minWidth: 100,
          formattor: val => this.$parseTime(val)
        },
        {
          prop: 'operation',
          label: this.$t('bidMod.operation'),
          minWidth: 160,
          showType: 'buttons',
          fixed: 'right',
          btnStyle: 'text',
          buttons: [
            // 管理
            {
              // 已废弃不显示
              show: row => row.projectStatus !== SOU_AUCT_PROJECT_STATUS_ENUM.CANCEL,
              callback: row => this.openDetailTab('edit', row),
              formattor: () => this.$t('bidMod.management')
            },
            // 废弃
            {
              // 拟定、已定价、已废弃不显示 审核状态已审批不显示
              show: row => ![
                SOU_AUCT_PROJECT_STATUS_ENUM.DRAFT,
                SOU_AUCT_PROJECT_STATUS_ENUM.PRICE_END,
                SOU_AUCT_PROJECT_STATUS_ENUM.CANCEL
              ].includes(row.projectStatus) && row.createApprovalStatus !== 'APPROVED',
              callback: row => this.abandonRow(row),
              formattor: () => this.$t('common.abandon')
            },
            // 删除
            {
              // 拟定
              show: row => row.projectStatus === SOU_AUCT_PROJECT_STATUS_ENUM.DRAFT,
              callback: row => this.deleteRow(row),
              formattor: () => this.$t('common.delete')
            },
            // 复制 测试用
            {
              callback: row => this.copyRow(row),
              formattor: () => this.$t('common.copy')
            }
          ]
        }
      ],
      tableData: [],
      searchFormConfig: [
        // 竞价标题
        { prop: 'souName', label: () => this.$t('bidMod.competitionLts.souName') },
        // 竞价单号
        { prop: 'souNo', label: () => this.$t('bidMod.competitionLts.souNo') },
        // 竞价状态
        {
          prop: 'projectStatus',
          label: () => this.$t('bidMod.competitionLts.souStatus'),
          type: 'dict',
          code: 'SOU_AUCT_PROJECT_STATUS'
        },
        // 评分规则
        {
          prop: 'scoreRuleType',
          label: () => this.$t('bidMod.scoringRubric'),
          type: 'dict',
          code: 'SOU_AUCT_SCORE_RULE_TYPE'
        },
        // 发布人
        {
          prop: 'createdBy',
          label: () => this.$t('bidMod.bidsIssuer'),
          type: 'quicksearch',
          showKey: 'username',
          name: 'scc_rbac_user_display'
        },
        // 审核状态
        {
          prop: 'createApprovalStatus',
          label: () => this.$t('bidMod.auditStatus'),
          type: 'dict',
          code: 'SOU_APPROVAL_STATUS'
        }
      ],
      queryParam: {}
    }
  },

  watch: {
    '$route.params': {
      handler (val) {
        const {
          from,
          funName,
          formId,
          formNo,
          type
        } = val

        // 需求池跳转
        if (from === 'demandPoolManagement' && funName === 'competitionManageBuyer') {
          this.openDetailTab(type, {
            projectId: formId,
            souNo: formNo
          })
        }
      },
      immediate: true,
      deep: true
    }
  },

  mounted () {
    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  methods: {
    /* 查询列表数据 */
    getQueryData (params = {}) {
      this.queryParam = transformMQL.listPageData({
        type: 'AuctSouProjectForBuyer',
        action: 'listProjectsMql',
        params
      })

      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    /* 废弃 */
    async abandonRow (row) {
      const confirmResult = await this.$confirm(this.$t('common.abandonA'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => { /* nothing */ })

      if (confirmResult !== 'confirm') {
        return
      }
      let transformParams = transformMQL.save('AuctSouProjectForBuyer', [{ projectId: row.projectId }], 'cancelSou')
      const response = await carBuyerHttp.init.cancel(transformParams)
      if (response) {
        this.$message.success(this.$t('bidMod.abandonedSuccess'))
        this.getQueryData()
      }
    },

    /* 复制 */
    async copyRow (row) {
      const confirmResult = await this.$confirm('是否复制该行', {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => { /* nothing */ })

      if (confirmResult !== 'confirm') {
        return
      }
      let transformParams = transformMQL.save('AuctSouProjectForBuyer', [{ projectId: row.projectId }], 'copySou')
      const response = await carBuyerHttp.init.copy(transformParams)
      if (response) {
        this.$message.success('复制成功！')
        this.getQueryData()
      }
    },

    /* 删除 */
    async deleteRow (row) {
      const confirmResult = await this.$confirm(this.$t('common.delRow'), {
        confirmButtonText: this.$t('common.affirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => { /* nothing */ })

      if (confirmResult !== 'confirm') {
        return
      }
      let transformParams = transformMQL.save('AuctSouProjectForBuyer', [{ projectId: row.projectId }], 'removeSou')
      const response = await carBuyerHttp.init.remove(transformParams)
      if (response) {
        this.$message.success(this.$t('common.successDelete'))
        this.getQueryData()
      }
    },

    /* 打开详情页 */
    openDetailTab (type, row = {}) {
      let tab = {}
      if (type === 'add') {
        // 新增
        tab = {
          component: competitionManagementDetail,
          params: {
            flag: type,
            tabName: 'competitionManagementDetail'
          },
          // 创建项目
          title: this.$t('purchaseDemand.createdBidProject'),
          name: 'competitionManagementDetail'
        }
      } else {
        // 修改
        tab = {
          component: competitionManagementDetail,
          params: {
            flag: type,
            row: row,
            tabName: `competitionManagementDetail${row.souNo || ''}`
          },
          title: row.souNo || '',
          name: `competitionManagementDetail${row.souNo || ''}`
        }
      }
      this.$emit('tab-add', tab)
    }
  }
}
</script>
