<template>
  <el-container class="flex-container mouldheader_list_wrapper" direction="vertical">
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQueryData"
      />
      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <!--创建会议-->
          <AuthorityButton
            type="primary"
            code="meet:manage:add"
            @click="createModel"
          >
            {{ $t('meeting.addConference') }}
          </AuthorityButton>
        </template>
      </MainHeader>

      <TableView
        ref="list"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        url="/api-inq/inq/meeting/listPage"
        :checkbox="false"
        :com-active="$attrs['changeTab']"
      />
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MeetManageDetail from './meetManageDetail'
import MeetManageSee from './see'

export default {
  name: 'MeetManageList',

  components: {
    TableView,
    MainHeader,
    FormWrapper
  },

  mixins: [tabTodoWatch, tabTodoMixin],

  data () {
    return {
      tableHeader: [],
      filterConfig: [
        // 会议编号
        { prop: 'meetingCode', label: this.$t('meeting.meetingCode') },
        // 会议名称
        { prop: 'meetingTitle', label: this.$t('meeting.meetingTitle') },
        // 会议时间
        {
          prop: 'meetingTime',
          label: this.$t('meeting.meetingTime'),
          type: 'daterange'
        },
        // 议题名称
        { prop: 'topicName', label: this.$t('meeting.topicName') },
        // 库存组织
        {
          prop: 'invId',
          label: this.$t('common.invOrg'),
          type: 'INVorganizationSelector'
        },
        // 创建人
        { prop: 'createdFullName', label: this.$t('common.creator') },
        // 创建时间
        {
          prop: 'creationDate',
          label: this.$t('common.creationTime'),
          type: 'daterange'
        },
        // 会议状态
        {
          prop: 'status',
          label: this.$t('meeting.meetingStatus'),
          type: 'dict',
          code: 'MEET_MANAGE_STATUS'
        }
      ],
      queryParam: {},
      userInfo: this.$store.getters.userInfo
    }
  },

  created () {
    this.tableHeader = [
      // 会议编号
      {
        prop: 'meetingCode',
        label: this.$t('meeting.meetingCode'),
        minWidth: 130,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.editHandle('view', row)
      },
      // 库存组织
      {
        prop: 'invName',
        label: this.$t('common.invOrg'),
        minWidth: 130
      },
      // 会议名称
      {
        prop: 'meetingTitle',
        label: this.$t('meeting.meetingTitle'),
        minWidth: 130
      },
      // 会议时间
      {
        prop: 'meetingStartTime',
        label: this.$t('meeting.meetingTime'),
        minWidth: 130,
        dataType: 'dateTime'
      },
      // 会议地点
      {
        prop: 'meetingAddr',
        label: this.$t('meeting.meetingAddr'),
        minWidth: 130
      },
      // 会议状态
      {
        prop: 'status',
        label: this.$t('meeting.meetingStatus'),
        minWidth: 100,
        dataType: 'dict',
        code: 'MEET_MANAGE_STATUS'
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
        dataType: 'dateTime'
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
        dataType: 'dateTime'
      },
      {
        prop: 'operation',
        label: this.$t('common.operation'),
        showType: 'buttons',
        btnStyle: 'text',
        fixed: 'right',
        width: 130,
        buttons: [
          {
            show: row => row.status === 'DRAFT',
            code: 'meet:manage:edit',
            formattor: () => this.$t('common.edit'),
            callback: row => this.editHandle('edit', row)
          },
          {
            show: row => row.status === 'DRAFT',
            code: 'meet:manage:delete',
            formattor: () => this.$t('common.delete'),
            callback: row => this.deleteHandle(row)
          },
          {
            show: row => row.status === 'PUBLISHED',
            code: 'meet:manage:change',
            formattor: () => this.$t('meeting.meetingChange'),
            callback: row => this.editHandle('change', row)
          },
          {
            show: row => ['IN_PROGRESS'].includes(row.status),
            code: 'meet:manage:into',
            formattor: () => this.$t('meeting.enterMeeting'),
            callback: row => this.editHandle('see', row)
          },
          {
            show: row => ['IN_PROGRESS'].includes(row.status) && row.enableMaker,
            code: 'meet:manage:make',
            formattor: () => this.$t('meeting.makeDecision'),
            callback: row => this.editHandle('make', row)
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
    createModel () {
      this.editHandle('add')
    },

    editHandle (type, row) {
      let name = type === 'add' ? this.$t('meeting.addMeeting') : this.$t('meeting.meetingManagement') + (row.meetingCode || '')
      let component = ['see', 'make'].includes(type) ? MeetManageSee : MeetManageDetail
      if (type === 'view') {
        component = ['IN_PROGRESS', 'END'].includes(row.status) ? MeetManageSee : MeetManageDetail
      }
      const tab = {
        component,
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
      let { creationDate, meetingTime } = this.queryParam
      if (creationDate && creationDate.length) {
        this.queryParam.createdStartDate = creationDate[0]
        this.queryParam.createdEndDate = creationDate[1]
      }
      if (meetingTime && meetingTime.length) {
        this.queryParam.meetingStartDate = meetingTime[0]
        this.queryParam.meetingEndDate = meetingTime[1]
      }
      delete this.queryParam.creationDate
      delete this.queryParam.meetingTime
      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    deleteHandle (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/api-inq/inq/meeting/delete',
          method: 'POST',
          params: { meetingId: row.meetingId },
          loading: true
        }).then(res => {
          this.$message.success(res.message)
          this.getQueryData()
        })
      })
    }
  }
}
</script>
