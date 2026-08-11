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
        open-custom-table
        :url="tableViewUrl"
      />
    </el-main>
  </el-container>
</template>

<script>
import { SOU_AUCT_PROJECT_STATUS_ENUM } from 'lib@/composition/competition/utils'
import { carBuyerHttp } from 'modcb@/competition/api'
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
        {
          prop: 'souName',
          label: this.$t('cusEntry.bidMod.competionName'),
          minWidth: 120
        },
        {
          prop: 'extProjectNo',
          label: this.$t('cusEntry.bidMod.extProjectNo'),
          minWidth: 120
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
        // 创建人
        {
          prop: 'createdFullName',
          label: this.$t('common.creator'),
          minWidth: 100
        },
        // 创建时间
        {
          prop: 'creationDate',
          label: this.$t('bidMod.creationDate'),
          minWidth: 100,
          dataType: 'dateTime'
        },
        // 发布时间
        {
          prop: 'publishTime',
          label: this.$t('bidMod.releaseDatetime'),
          minWidth: 100,
          dataType: 'dateTime'
        },
        // 竞价开始时间
        {
          prop: 'orderStartTime',
          label: this.$t('bidMod.bidStartTime'),
          minWidth: 120,
          dataType: 'dateTime'
        },
        // 竞价截止时间
        {
          prop: 'orderEndTime',
          label: this.$t('bidMod.bidClosingTime'),
          minWidth: 120,
          dataType: 'dateTime'
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
              formattor: () => this.$t('bidMod.management'),
              code: 'inq:competition:inquiryManage'
            },
            // 废弃
            {
              // 拟定、已定价、已废弃不显示 审核状态已审批不显示
              show: row => !['LOA', 'CANCEL', 'FILE'].includes(row.projectStatus),
              callback: row => this.abandonRow(row),
              formattor: () => this.$t('common.abandon'),
              code: 'inq:competition:abandon'
            },
            {
              // 撤回 (审核状态为已提交的)
              show: row => row.createApprovalStatus === 'SUBMITTED',
              callback: row => this.undo(row),
              formattor: () => this.$t('common.recall')
            }
            // 删除
            // {
            //   // 拟定
            //   show: row => row.projectStatus === SOU_AUCT_PROJECT_STATUS_ENUM.DRAFT,
            //   callback: row => this.deleteRow(row),
            //   formattor: () => this.$t('common.delete')
            // }
            // 复制 测试用
            // {
            //   callback: row => this.copyRow(row),
            //   formattor: () => this.$t('common.copy')
            // }
          ]
        }
      ],
      tableData: [],
      searchFormConfig: [
        // 竞价单号
        { prop: 'souNo', label: () => this.$t('bidMod.competitionLts.souNo') },
        // 竞价状态
        {
          prop: 'projectStatus',
          label: () => this.$t('bidMod.competitionLts.souStatus'),
          type: 'dict',
          code: 'SOU_AUCT_PROJECT_STATUS'
        },
        // 审核状态
        {
          prop: 'createApprovalStatus',
          label: () => this.$t('bidMod.auditStatus'),
          type: 'dict',
          code: 'SOU_APPROVAL_STATUS'
        },
        // 发布人
        {
          prop: 'currentUserId',
          label: () => this.$t('bidMod.bidsIssuer'),
          type: 'quicksearch',
          showKey: 'username',
          propKey: 'userId',
          name: 'scc_rbac_user_display'
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
    /* 撤回 */
    undo (row) {
      this.$prompt('', this.$t('cusEntry.competition.undoReason'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        inputType: 'textarea',
        inputValidator: (value) => {
          if (!value) {
            return this.$t('cusEntry.tipMessage.undoReason')
          }
          return true
        }
      }).then(({ value }) => {
        const data = {
          bussinessType: 'SOU_BID_FLOW',
          dataId: row.projectId,
          commentmsg: value
        }
        carBuyerHttp.process.undo(data).then(res => {
          if (res.data) {
            this.$message.warning(this.$t('cusEntry.tipMessage.recallSuccess'))
            this.getQueryData()
          }
        })
      })
    },
    /* 查询列表数据 */
    getQueryData (params = {}) {
      // this.queryParam = transformMQL.listPageData({
      //   type: 'AuctSouProjectForBuyer',
      //   action: 'listProjectsMql',
      //   params
      // })
      this.queryParam = params
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
      const response = await carBuyerHttp.init.cancel({ projectId: row.projectId })
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
