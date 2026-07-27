<template>
  <el-container class="flex-container mouldheader_list_wrapper" direction="vertical">
    <el-main>
      <FormWrapper :form-array="filterConfig" @getFormData="getQueryData" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <!--创建议题-->
          <AuthorityButton
            type="primary"
            code="meet:subject:add"
            @click="openDetailTab('add')"
          >
            {{ $t('meeting.createTopic') }}
          </AuthorityButton>
        </template>
      </MainHeader>

      <TableView
        ref="list"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :url="meetTopicsApi.listPageUrl"
        :checkbox="false"
        :com-active="$attrs['changeTab']"
      />
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import { parseTimeYMD } from 'lib@/composition/origin/composition'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MeetTopicsDetail from './meetTopicsDetail'
import inquiryDetail from 'modb@/inquiry/views/inquiryManagement/inquiryDetail.vue'
// import quoteSelectionPage from 'modb@/inquiryBySimpleBuyer/views/inquiryBySimpleListBuyer/inquiryBySimpleListBuyer/inquiryTrackingDetail/inquiryEvaluationTab/quoteSelectionPage'
// import supaccountperiodchangeEdit from 'modb@/vendorManagementBuyer/views/supAccountPeriodChange/edit'
import { meetTopicsApi } from 'modb@/meetManagement/api'
export default {
  name: 'MeetTopicsList',

  components: {
    TableView,
    MainHeader,
    FormWrapper
  },

  mixins: [tabTodoWatch, tabTodoMixin],

  data () {
    return {
      meetTopicsApi: meetTopicsApi,
      tableHeader: [],
      filterConfig: [
        // 议题编号
        { prop: 'topicCode', label: this.$t('meeting.topicCode') },
        // 议题名称
        { prop: 'topicName', label: this.$t('meeting.topicName') },
        // 上会类型
        {
          prop: 'topicType',
          label: this.$t('meeting.topicType'),
          type: 'dict',
          code: 'MEET_TYPE'
        },
        // 关联单据号
        { prop: 'relationBillCode', label: this.$t('meeting.relationBillCode') },
        // 创建人
        { prop: 'createdFullName', label: this.$t('common.creator') },
        // 创建时间
        {
          prop: 'creationDate',
          label: this.$t('common.creationTime'),
          type: 'daterange'
        },
        // 议题状态
        {
          prop: 'status',
          label: this.$t('meeting.topicStatus'),
          type: 'dict',
          code: 'MEET_SUBJECT_STATUS'
        },
        // 库存组织
        {
          prop: 'invId',
          label: this.$t('common.invOrg'),
          type: 'INVorganizationSelector'
        },
        // 完成时间
        {
          prop: 'completeDate',
          label: this.$t('meeting.completeDate'),
          type: 'daterange'
        }
      ],
      queryParam: {}
    }
  },

  watch: {
    $route: {
      handler (val) {
        if (val) {
          let { from, row } = val.params || {}
          // 会议管理跳转过来
          if (from === 'fromMeetManage') {
            this.openDetailTab('view', row)
          }
          // 询价界面跳转过来
          if (from === 'fromFun') {
            this.openDetailTab('edit', row)
          }
        }
      },
      immediate: true,
      deep: true
    }
  },

  created () {
    this.tableHeader = [
      // 议题编号
      {
        prop: 'topicCode',
        label: this.$t('meeting.topicCode'),
        minWidth: 130,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.openDetailTab('view', row)
      },
      // 库存组织
      {
        prop: 'invName',
        label: this.$t('common.invOrg'),
        minWidth: 130
      },
      // 议题名称
      {
        prop: 'topicName',
        label: this.$t('meeting.topicName'),
        minWidth: 130
      },
      // 上会类型
      {
        prop: 'topicType',
        label: this.$t('meeting.topicType'),
        minWidth: 130,
        dataType: 'dict',
        code: 'MEET_TYPE'
      },
      // 关联单据号
      {
        prop: 'relationBillCode',
        label: this.$t('meeting.relationBillCode'),
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: async row => {
          if (!row.relationBillId) return
          let tab
          let ismeeting = row.status === 'MEETING' ? 'Y' : 'N'
          if (row.topicType === 'SS') {
            tab = {
              component: inquiryDetail,
              params: {
                flag: 'view',
                readOnly: true,
                row: { projectId: row.relationBillId, ismeeting },
                tabName: `inquiryDetail${row.relationBillCode}`
              },
              title: row.relationBillCode,
              name: `inquiryDetail${row.relationBillCode}`
            }
          } else if (row.topicType === 'SD') {
            // let result = await this.$http({
            //   url: `/api-inq/quote/selection/management/${row.relationBillId}`,
            //   method: 'GET'
            // })
            // let { header = {} } = result.data
            // tab = {
            //   component: quoteSelectionPage,
            //   params: {
            //     header,
            //     tabName: `quoteSelectionPage${header.inquiryId}`
            //   },
            //   title: header.inquiryId,
            //   name: `quoteSelectionPage${header.inquiryId}`
            // }
          } else if (row.topicType === 'PC') {
            // let result = await this.$http({
            //   url: `/api-sup/sup/supaccountperiodchange/get?id=${row.relationBillId}`,
            //   method: 'GET'
            // })
            // let header = result.data
            // tab = {
            //   component: supaccountperiodchangeEdit,
            //   params: {
            //     row: header,
            //     flag: 'view',
            //     tabName: `supaccountperiodchangeEdit${header.accountPeriodChangeId}`
            //   },
            //   title: '供应商账期变更详情',
            //   name: `supaccountperiodchangeEdit${header.accountPeriodChangeId}`
            // }
          }
          this.$emit('tab-add', tab)
        }
      },
      // 议题状态
      {
        prop: 'status',
        label: this.$t('meeting.topicStatus'),
        minWidth: 100,
        dataType: 'dict',
        code: 'MEET_SUBJECT_STATUS'
      },
      // 完成时间
      {
        prop: 'completeDate',
        label: this.$t('meeting.completeDate'),
        minWidth: 100,
        formattor: val => parseTimeYMD(val)
        // formattor: val => val ? /\d{4}-\d{1,2}-\d{1,2}/.exec(val) : null
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
        label: this.$t('common.creationTime'),
        minWidth: 100,
        formattor: val => parseTimeYMD(val)
      },
      // 更新人
      {
        prop: 'lastUpdatedFullName',
        label: this.$t('common.lastUpdatedFullName'),
        minWidth: 100
      },
      // 更新日期
      {
        prop: 'lastUpdateDate',
        label: this.$t('common.lastUpdateDate'),
        minWidth: 100,
        formattor: val => parseTimeYMD(val)
      },
      {
        prop: 'operation',
        label: this.$t('common.operation'),
        showType: 'buttons',
        btnStyle: 'text',
        fixed: 'right',
        width: 130,
        buttons: [
          // 编辑
          {
            show: row => row.status === 'DRAFT',
            code: 'meet:subject:edit',
            formattor: () => this.$t('common.edit'),
            callback: row => this.openDetailTab('edit', row)
          },
          // 删除
          {
            show: row => row.status === 'DRAFT',
            code: 'meet:subject:delete',
            formattor: () => this.$t('common.delete'),
            callback: row => this.deleteRow(row)
          },
          // 驳回
          {
            show: row => row.status === 'APPLY',
            code: 'meet:subject:reject',
            formattor: () => this.$t('common.toRefuse'),
            callback: row => this.rejectRow(row)
          }
        ],
        selectList: []
      }
    ]

    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  methods: {
    openDetailTab (type, row) {
      let name = type === 'add' ? this.$t('meeting.addIssueManagement')
        : this.$t('meeting.issueManagement') + row.topicCode
      const tab = {
        component: MeetTopicsDetail,
        params: {
          row,
          flag: type,
          tabName: name
        },
        title: name,
        name
      }
      this.$emit('tab-add', tab)
    },

    getQueryData (params) {
      this.queryParam = JSON.parse(JSON.stringify(params || {}))
      let { creationDate, completeDate } = this.queryParam
      if (creationDate && creationDate.length) {
        this.queryParam.startDate = creationDate[0]
        this.queryParam.endDate = creationDate[1]
      }
      if (completeDate && completeDate.length) {
        this.queryParam.completeStartDate = completeDate[0]
        this.queryParam.completeEndDate = completeDate[1]
      }
      delete this.queryParam.creationDate
      delete this.queryParam.completeDate
      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    /* 删除行 */
    async deleteRow (row) {
      const confirmResult = await this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => { /* nothing */ })

      if (confirmResult !== 'confirm') {
        return
      }

      const response = await meetTopicsApi.delete({ topicId: row.topicId })
      if (response) {
        this.$message.success(response.message)
        this.getQueryData()
      }
    },

    /* 驳回行 */
    async rejectRow (row) {
      const confirmResult = await this.$confirm(this.$t('meeting.rejectTip'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => { /* nothing */ })

      if (confirmResult !== 'confirm') {
        return
      }

      const response = await meetTopicsApi.reject({ topicId: row.topicId })
      if (response) {
        this.$message.success(response.message)
        this.getQueryData()
      }
    }
  }
}
</script>
