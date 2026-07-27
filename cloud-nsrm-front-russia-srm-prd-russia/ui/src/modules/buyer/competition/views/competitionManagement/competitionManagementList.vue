<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
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
        open-custom-table
        :url="tableViewUrl"
      />
    </el-main>
  </el-container>
</template>

<script>
import { SOU_PROJECT_STATUS_ENUM } from 'lib@/composition/origin/enum'
import { compBuyerHttp } from 'modb@/competition/api'
import { tabTodoWatch } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import competitionManagementDetail from './competitionManagementDetail'

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
      tableViewUrl: compBuyerHttp.init.listPageUrl,
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
          minWidth: 180
        },
        // 项目状态
        {
          prop: 'projectStatus',
          label: this.$t('bidMod.bidingStatus'),
          minWidth: 100,
          dataType: 'dict',
          code: 'SOU_PROJECT_STATUS'
        },
        // 审核状态
        {
          prop: 'createApprovalStatus',
          label: this.$t('bidMod.auditStatus'),
          minWidth: 100,
          dataType: 'dict',
          code: 'SOU_APPROVAL_STATUS'
        },
        // 邀请供应商
        {
          prop: 'inviteCount',
          label: this.$t('bidMod.bidVendorCount'),
          minWidth: 120
        },
        // 已报价供应商
        {
          prop: 'orderCount',
          label: this.$t('bidMod.quotatedSupplier'),
          minWidth: 130
        },
        // 评分规则
        {
          prop: 'scoreRuleType',
          label: this.$t('bidMod.evaluateMethod'),
          minWidth: 100,
          dataType: 'dict',
          code: 'SOU_COMP_SCORE_RULE_TYPE'
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
          formattor: val => this.$dayjsParse(val)
        },
        // 发布时间
        {
          prop: 'publishTime',
          label: this.$t('bidMod.releaseDatetime'),
          minWidth: 100,
          formattor: val => this.$dayjsParse(val)
        },
        // 截止时间
        {
          prop: 'orderEndTime',
          label: this.$t('bidMod.stopTime'),
          minWidth: 100,
          formattor: val => this.$dayjsParse(val)
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
              show: row => row.projectStatus !== SOU_PROJECT_STATUS_ENUM.CANCEL,
              callback: row => this.openDetailTab('edit', row),
              formattor: () => this.$t('bidMod.management')
            },
            // 废弃
            {
              // 拟定、已定价、已废弃不显示
              show: row => ![
                SOU_PROJECT_STATUS_ENUM.DRAFT,
                SOU_PROJECT_STATUS_ENUM.PRICE_END,
                SOU_PROJECT_STATUS_ENUM.CANCEL
              ].includes(row.projectStatus),
              callback: row => this.abandonRow(row),
              formattor: () => this.$t('common.abandon')
            },
            // 删除
            {
              // 拟定
              show: row => row.projectStatus === SOU_PROJECT_STATUS_ENUM.DRAFT,
              callback: row => this.deleteRow(row),
              formattor: () => this.$t('common.delete')
            }
          ]
        }
      ],
      tableData: [],
      searchFormConfig: [
        // 项目编号
        { prop: 'souNo', label: () => this.$t('bidMod.enquiry_projectNum') },
        // 项目名称
        { prop: 'souName', label: () => this.$t('bidMod.bidingName') },
        // 项目状态
        {
          prop: 'projectStatus',
          label: () => this.$t('bidMod.bidingStatus'),
          type: 'dict',
          code: 'SOU_PROJECT_STATUS'
        },
        // 评分规则
        {
          prop: 'scoreRuleType',
          label: () => this.$t('bidMod.scoringRubric'),
          type: 'dict',
          code: 'SOU_COMP_SCORE_RULE_TYPE'
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
        if (from === 'demandPoolManagement' && funName === 'competitionManagement') {
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
    getQueryData (v) {
      this.queryParam = Object.assign({}, v)

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

      const response = await compBuyerHttp.init.cancel({ projectId: row.projectId })
      if (response) {
        this.$message.success(this.$t('bidMod.abandonedSuccess'))
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

      const response = await compBuyerHttp.init.remove(row.projectId)
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
