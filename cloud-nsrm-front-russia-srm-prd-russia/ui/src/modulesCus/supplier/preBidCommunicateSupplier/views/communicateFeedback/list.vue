<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <FormWrapper :form-array="searchFormConfig" @getFormData="getQueryData" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left" />
      </MainHeader>

      <TableView
        ref="list"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        :checkbox="false"
        :checkChange="handleCurrentChange"
        open-custom-table
        :url="tableViewUrl"
        :adeptMeiQl="true"
        @afterQuery="afterQuery"
      />
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoWatch } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import CommunicateFeedbackDetail from './edit'
import CommunicateNoticeDetail from 'modcs@/preBidCommunicateSupplier/views/communicateNotice/edit'
import { commuFeedbackHttp } from 'modcs@/preBidCommunicateSupplier/api'
import { transformMQL, transformTimeQuery } from 'lib@/utils/util'

export default {
  name: 'CommunicateFeedbackList',

  components: {
    TableView,
    MainHeader,
    FormWrapper,
    MImport
  },

  mixins: [tabTodoWatch],

  data () {
    return {
      tableViewUrl: commuFeedbackHttp.listPageUrl,
      tableHeader: [],
      tableData: [],
      searchFormConfig: [
        {
          prop: 'bidNoticeNo',
          label: '交流通知单号'
        },
        {
          prop: 'projectName',
          label: '项目名称'
        },
        {
          prop: 'status',
          label: '单据状态',
          type: 'dict',
          code: 'PRE_BID_FEEDBACK_STATUS'
        },
        {
          prop: 'bidUserNickname',
          label: '招标负责人'
        },
        {
          prop: 'creationDate',
          label: '创建日期',
          type: 'daterange'
        }
      ],
      queryParam: {},
      selectedRows: [] // 标记勾选行
    }
  },

  watch: {
    '$route.params': {
      // 寻源需求等其它地方跳转过来
      handler (nVal) {
        const { from, row } = nVal
        if (from) {
          this.editTab('view', row)
        }
      },
      immediate: true,
      deep: true
    }
  },

  mounted () {
    this.tableHeader = [
      {
        prop: 'bidFeedbackNo',
        label: '标前交流反馈单号',
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: (row) => {
          console.log('row:::', row)
          this.editTab('view', row)
        }
      },
      {
        prop: 'bidNoticeNo',
        label: '交流通知单号',
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: (row) => {
          console.log('row:::', row)
          this.editTab('notice', row)
        }
      },
      {
        prop: 'projectName',
        label: '项目名称',
        minWidth: 120
      },
      {
        prop: 'status',
        label: '单据状态',
        dataType: 'dict',
        code: 'PRE_BID_FEEDBACK_STATUS',
        minWidth: 120
      },
      {
        prop: 'feedbackStatus',
        label: '反馈状态',
        // dataType: 'dict',
        // code: 'VENDOR_FEEDBACK_STATUS',
        minWidth: 120,
        formattor: (val) => this.$getDictLabel('VENDOR_FEEDBACK_STATUS', val)
      },
      {
        prop: 'bidNoticeId.bidUserNickname',
        label: '招标负责人',
        minWidth: 120
      },
      {
        prop: 'creationDate',
        label: '发布日期',
        minWidth: 130
      },
      {
        prop: 'operation',
        label: this.$t('bidMod.operation'),
        minWidth: 150,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          /**
           * 1.未反馈、已驳回可以编辑
           * 未反馈：NO_FEEDBACK
           * 已反馈：ALREADY_FEEDBACK
           * 已驳回：REJECT
           */
          {
            show: row => ['NO_FEEDBACK', 'REJECT'].includes(row.feedbackStatus),
            formattor: () => this.$t('common.edit'),
            code: 'communicateFeedback:edit',
            callback: row => {
              this.editTab('edit', row)
            }
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  methods: {
    /* 查询列表数据 */
    getQueryData (params = {}) {
      transformTimeQuery(['creationDate'], params)
      this.queryParam = transformMQL.listPageData({
        type: 'PreBidFeedback',
        action: 'query',
        params: {
          ...params,
          vendorId: this.$store.getters.userInfo.userId
        },
        filterOperator: {
          creationDate: 'between'
        },
        query: {
          '*': {},
          'feedbackVendorList': {
            '*': {},
            '$condition': {
              '$strictQuery': true,
              'filter': {
                vendorId: this.$store.getters.userInfo.companyId
              }
            }
          },
          'bidNoticeId': {
            '*': {}
          }
        }
      })
      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    afterQuery (data) {
      if (data && data.length) {
        data.forEach(item => {
          if (item.feedbackVendorList && item.feedbackVendorList.length) {
            item.feedbackStatus = item.feedbackVendorList[0].feedbackStatus
          }
        })
      }
    },

    editTab (type, row) {
      const map = new Map([
        // 编辑
        [
          'edit',
          {
            component: CommunicateFeedbackDetail,
            params: {
              flag: type,
              row,
              tabName: row.bidFeedbackNo
            },
            title: '标前交流反馈' + (row.bidFeedbackNo || ''),
            name: row.bidFeedbackNo
          }
        ],
        // 查看
        [
          'view',
          {
            component: CommunicateFeedbackDetail,
            params: {
              flag: type,
              row,
              tabName: row.bidFeedbackNo
            },
            title: '标前交流反馈' + (row.bidFeedbackNo || ''),
            name: row.bidFeedbackNo
          }
        ],
        // 标前交流通知
        [
          'notice',
          {
            component: CommunicateNoticeDetail,
            params: {
              flag: 'view',
              row,
              tabName: row.bidNoticeNo
            },
            title: '标前交流通知' + (row.bidNoticeNo || ''),
            name: row.bidNoticeNo
          }
        ]
      ])
      this.$emit('tab-add', map.get(type))
    },

    /* 选中行 */
    handleCurrentChange (val) {
      console.log('val:::', val)
      this.selectedRows = val
    }
  }
}
</script>
