<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <FormWrapper :form-array="searchFormConfig" @getFormData="getQueryData" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <!--b 新增-->
          <el-button type="primary" @click="openDetailTab('add')">
            {{ $t("common.add") }}
          </el-button>
        </template>
      </MainHeader>

      <TableView
        ref="bargainListTable"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        open-custom-table
        :com-active="$attrs['changeTab']"
        :url="tableViewUrl"
      />
    </el-main>
  </el-container>
</template>

<script>
import { brgBuyerHttp } from 'modb@/bargain/api'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { judgeAbandonProject, judgeDeleteProject } from 'lib@/composition/bargainLts/utils'
import { daterangePayloadFormat } from 'lib@/composition/commonComposition'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import bargainDetail from './bargainDetail'

export default {
  name: 'BargainList',

  components: {
    TableView,
    MainHeader,
    FormWrapper
  },

  mixins: [tabTodoWatch, tabTodoMixin],

  data () {
    return {
      tableViewUrl: brgBuyerHttp.init.listPageUrl,
      scoreRuleName: '',
      tableHeader: [
        // 项目编号
        {
          prop: 'souNo',
          label: this.$t('bidMod.bidingNum'),
          minWidth: 150,
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.openDetailTab('view', row)
        },
        // 项目名称
        {
          prop: 'souName',
          label: this.$t('bidMod.bidingName'),
          minWidth: 150
        },
        // 项目状态
        {
          prop: 'projectStatus',
          label: this.$t('bidMod.bidingStatus'),
          minWidth: 100,
          dataType: 'dict',
          code: 'SOU_PROJECT_STATUS'
        },
        // 审批状态
        {
          prop: 'createApprovalStatus',
          label: this.$t('bidMod.auditStatus'),
          minWidth: 100,
          dataType: 'dict',
          code: 'SOU_APPROVAL_STATUS'
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
          prop: 'createdUserName',
          label: this.$t('bidMod.publishBy'),
          minWidth: 100
        },
        // 创建日期
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
        // 报价截止时间
        {
          prop: 'orderEndTime',
          label: this.$t('bidMod.quotedeadline'),
          minWidth: 120,
          formattor: val => this.$parseTime(val)
        },
        {
          prop: 'operation',
          label: this.$t('bidMod.operation'),
          minWidth: 150,
          showType: 'buttons',
          fixed: 'right',
          btnStyle: 'text',
          buttons: [
            // 管理
            {
              formattor: () => this.$t('bidMod.management'),
              callback: row => this.openDetailTab('edit', row)
            },
            // 废弃
            {
              show: row => judgeAbandonProject(row.projectStatus),
              formattor: () => this.$t('common.abandon'),
              callback: row => this.abandonRow(row)
            },
            // 删除
            {
              show: row => judgeDeleteProject(row.projectStatus),
              formattor: () => this.$t('common.delete'),
              callback: row => this.deleteRow(row)
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
        // 立项审核状态
        {
          prop: 'createApprovalStatus',
          label: this.$t('bidMod.biddingManagementBuyer.auditStatus'),
          type: 'dict',
          code: 'SOU_APPROVAL_STATUS'
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
        }
      ],
      status: false,
      tableData: [],
      queryParam: {}
    }
  },

  watch: {
    $route: {
      handler () {
        const routeParams = this.$route.params
        // 工作流 或者其他地方 跳转过来
        if (routeParams.from === 'fromFun' && routeParams.funName === 'bargainList') {
          let projectId = null
          let title = null
          if (routeParams.formId) {
            projectId = Number(routeParams.formId)
            title = routeParams.formNo
          }
          let row = {
            ...routeParams,
            projectId,
            souNo: title
          }
          this.openDetailTab('edit', row)
        }

        // 需求池跳转
        if (routeParams.from === 'demandPoolManagement' && routeParams.funName === 'bargainManagement') {
          this.openDetailTab(routeParams.type, {
            projectId: routeParams.formId,
            souNo: routeParams.formNo
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

    const routeParams = this.$route.params
    this.status = routeParams.status ? routeParams.status : this.status
    if (this.status) {
      this.openDetailTab('edit', {
        projectId: routeParams.projectId,
        souNo: routeParams.inquiryNumber
      })
    }
  },

  methods: {
    /* 查询 */
    getQueryData (payload) {
      if (payload) {
        // 格式化时间范围
        payload = daterangePayloadFormat(payload, [
          // 创建时间
          { prop: 'creationDate', fromProp: 'creationDateFrom', toProp: 'creationDateTo' },
          // 发布时间
          { prop: 'publishTime', fromProp: 'publishTimeFrom', toProp: 'publishTimeTo' }
        ])
      }

      this.queryParam = payload || this.queryParam
      this.$nextTick(() => {
        this.$refs.bargainListTable.query()
      })
    },

    /* 废弃行 */
    async abandonRow (row) {
      const promptResult = await this.$prompt(
        this.$t('common.abandonA'),
        '',
        {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          inputValidator: value => !(!value || value.length > 200),
          inputErrorMessage: this.$t('bidMod.abandonRowTip')  // 请输入废弃原因并且字符数不能超过200
        }
      )

      if (!promptResult) {
        return
      }

      const response = await brgBuyerHttp.init.cancel({
        projectId: row.projectId,
        cancelReason: promptResult.value
      })
      if (response) {
        this.$message.success(this.$t('common.successAbandon'))
        this.getQueryData()
      }
    },

    /* 删除行 */
    async deleteRow (row) {
      const confirmResult = await this.$confirm(this.$t('common.delRow'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => { /* nothing */ })

      if (confirmResult !== 'confirm') {
        return
      }

      const response = await brgBuyerHttp.init.remove(row.projectId)
      if (response) {
        this.$message.success(this.$t('common.successDelete'))
        this.getQueryData()
      }
    },

    /* 打开新增 or 编辑tab */
    openDetailTab (type, row = {}) {
      const map = new Map([
        // 新增
        [
          'add',
          {
            component: bargainDetail,
            params: { flag: type, tabName: 'bargainDetail' },
            // 创建项目
            title: this.$t('bidMod.createProject'),
            name: 'bargainDetail'
          }
        ],
        // 编辑
        [
          'edit',
          {
            component: bargainDetail,
            params: {
              flag: type,
              row: row,
              tabName: `bargainDetail${row.souNo}`
            },
            title: row.souName || row.souNo,
            name: `bargainDetail${row.souNo}`
          }
        ],
        // 查看
        [
          'view',
          {
            component: bargainDetail,
            params: {
              flag: type,
              row: row,
              tabName: `bargainDetail${row.souNo}`
            },
            title: row.souName || row.souNo,
            name: `bargainDetail${row.souNo}`
          }
        ]
      ])

      this.$emit('tab-add', map.get(type))
    }
  }
}
</script>
