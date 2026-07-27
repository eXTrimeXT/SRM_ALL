<template>
  <el-container class="flex-container mouldheader_list_wrapper" direction="vertical">
    <el-main>
      <FormWrapper :form-array="filterConfig" @getFormData="getQueryData" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton type="primary" code="meet:todo:add" @click="createModel">
            {{ $t('meeting.createTodo') }}
          </AuthorityButton>
        </template>
      </MainHeader>

      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        url="/api-inq/inq/meetTodo/listPage"
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
import MeetTodoDetail from './meetTodoDetail'
import { mouldflow } from 'modb@/mould/api'

export default {
  name: 'MeetTodoList',

  components: {
    TableView,
    MainHeader,
    FormWrapper
  },

  mixins: [tabTodoWatch, tabTodoMixin],

  data () {
    return {
      gridId: 'list',
      tableHeader: [],
      filterConfig: [
        // 待办编号
        { prop: 'meetTodoCode', label: this.$t('meeting.meetTodoCode') },
        // 待办内容
        { prop: 'todoContent', label: this.$t('meeting.todoContent') },
        // 负责人
        { prop: 'todoDirectorName', label: this.$t('meeting.todoDirectorName') },
        // 跟踪人
        { prop: 'todoStalkerName', label: this.$t('meeting.todoStalkerName') },
        // 创建人
        { prop: 'createdFullName', label: this.$t('common.creator') },
        // 创建时间
        {
          prop: 'creationDate',
          label: this.$t('common.creationTime'),
          type: 'daterange'
        },
        // 议题类型
        {
          prop: 'topicType',
          label: this.$t('meeting.todoType'),
          code: 'MEET_TYPE',
          type: 'dict'
        },
        // 责任部门
        { prop: 'department', label: this.$t('meeting.department') },
        // 议题编号
        { prop: 'topicCode', label: this.$t('meeting.topicCode') },
        // 议题名称
        { prop: 'topicName', label: this.$t('meeting.topicName') }
      ],
      queryParam: {},
      userInfo: this.$store.getters.userInfo
    }
  },

  watch: {
    $route: {
      handler (val) {
        if (val) {
          let { from, row } = val.params || {}
          if (from === 'fromMeetManage') {
            this.editHandle('view', row)
          }
        }
      },
      immediate: true,
      deep: true
    }
  },

  created () {
    this.tableHeader = [
      // 待办编号
      {
        prop: 'meetTodoCode',
        label: this.$t('meeting.meetTodoCode'),
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
      // 待办内容
      {
        prop: 'todoContent',
        label: this.$t('meeting.todoContent'),
        minWidth: 150
      },
      // 状态
      {
        prop: 'todoStatus',
        label: this.$t('common.status'),
        minWidth: 100,
        dataType: 'dict',
        code: 'MEET_TODO_STATUS'
      },
      // 跟踪人
      {
        prop: 'todoStalkerName',
        label: this.$t('meeting.todoStalkerName'),
        minWidth: 100
      },
      // 负责人
      {
        prop: 'todoDirectorName',
        label: this.$t('meeting.todoDirectorName'),
        minWidth: 100
      },
      // 责任部门
      {
        prop: 'department',
        label: this.$t('meeting.department'),
        minWidth: 100
      },
      // 议题编号
      {
        prop: 'topicCode',
        label: this.$t('meeting.topicCode'),
        minWidth: 130,
        showType: 'button',
        btnStyle: 'text',
        callback: row => {
          if (!row.topicId) return
          this.$router.push({
            name: 'meetSubject',
            params: {
              from: 'fromMeetManage',
              row
            }
          })
        }
      },
      // 议题名称
      {
        prop: 'topicName',
        label: this.$t('meeting.topicName'),
        minWidth: 130
      },
      // 议题类型
      {
        prop: 'topicType',
        label: this.$t('meeting.todoType'),
        minWidth: 130,
        dataType: 'dict',
        code: 'MEET_TYPE'
      },
      // 预警状态
      {
        prop: 'earlyWarningStatus',
        label: this.$t('meeting.warningStatus'),
        minWidth: 100,
        dataType: 'dict',
        code: 'MEET_WARNING_STATUS'
      },
      {
        prop: 'createdFullName',
        label: this.$t('common.creator'),
        minWidth: 100
      },
      {
        prop: 'creationDate',
        label: this.$t('common.creationTime'),
        minWidth: 100,
        formattor: val => parseTimeYMD(val)
      },
      {
        prop: 'lastUpdatedFullName',
        label: this.$t('common.lastUpdatedFullName'),
        minWidth: 100
      },
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
          // 跟踪
          {
            show: row => row.todoStatus === 'SUBMITTED',
            code: 'meet:todo:follow',
            formattor: () => this.$t('meeting.follow'),
            callback: row => this.editHandle('follow', row)
          },
          // 编辑
          {
            show: row => row.todoStatus === 'DRAFT',
            code: 'meet:todo:edit',
            formattor: () => this.$t('common.edit'),
            callback: row => this.editHandle('edit', row)
          }
        ]
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
      let name = type === 'add' ? this.$t('meeting.addTodo') : this.$t('meeting.todo') + row.meetTodoCode
      const tab = {
        component: MeetTodoDetail,
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
      let { creationDate } = this.queryParam
      if (creationDate && creationDate.length) {
        this.queryParam.startDate = creationDate[0]
        this.queryParam.endDate = creationDate[1]
      }
      delete this.queryParam.creationDate
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },

    deleteHandle (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        mouldflow.deleteFlow(row.mouldFlowLogId).then(res => {
          this.$message.success(res.message)
          this.getQueryData()
        })
      })
    }
  }
}
</script>
