<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <FormWrapper :form-array="searchFormConfig" @getFormData="getQueryData" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton code="communicateFeedbackBuyer:add" type="primary" @click="editTab('add',{})">
            <!-- 创建标前交流反馈 -->
            {{ $t("cusEntry.supplement20250205.createPreBidCommunicationFeedback") }}
          </AuthorityButton>
        </template>
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
import CommunicateFeedbackBuyerDetail from './edit'
import CommunicateNoticeBuyerDetail from 'modcb@/preBidCommunicate/views/communicateNoticeBuyer/edit'
import { commuFeedbackHttp } from 'modcb@/preBidCommunicate/api'
import { transformMQL, transformTimeQuery } from 'lib@/utils/util'

export default {
  name: 'CommunicateFeedbackBuyerList',

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
          prop: 'bidFeedbackNo',
          // label: '标前交流反馈单号'
          label: () => this.$t('cusEntry.supplement20250205.createPreBidCommunicationFeedback')
        },
        {
          prop: 'projectName',
          // label: '项目名称'
          label: () => this.$t('bidMod.bidingName')
        },
        {
          prop: 'status',
          // label: '单据状态',
          label: () => this.$t('vendorMod.relegation.documentStatus'),
          type: 'dict',
          code: 'PRE_BID_FEEDBACK_STATUS'
        },
        {
          prop: 'createdFullName',
          // label: '创建人'
          label: () => this.$t('common.creator')
        },
        {
          prop: 'creationDate',
          // label: '创建日期',
          label: () => this.$t('common.creationDate'),
          type: 'daterange'
        },
        {
          prop: 'bidNoticeNo',
          // label: '交流通知单号'
          label: () => this.$t('cusEntry.supplement20250205.communicationNoticeNumber')
        }
      ],
      queryParam: {},
      selectedRows: [] // 标记勾选行
    }
  },

  watch: {
    '$route.params': {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      handler (nVal) {
        const { from, funName, row, taskIndex } = nVal
        if (from === 'fromFun' && funName === 'communicateFeedbackBuyer') {
          let rowObj = { bidFeedbackId: row.formId, bidFeedbackNo: row.formNo }
          if (taskIndex === 1) { // 待办
            this.editTab('add', {})
          } else if (taskIndex === 2) { // 已办
            this.editTab('view', rowObj)
          }
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
        // label: '标前交流反馈单号',
        label: () => this.$t('cusEntry.supplement20250205.preBidFeedbackNumber'),
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
        // label: '交流通知单号',
        label: () => this.$t('cusEntry.supplement20250205.communicationNoticeNumber'),
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: (row) => {
          console.log('row:::', row)
          this.editTab('notice', row)
        }
      },
      {
        prop: 'orgBuName',
        // label: '板块',
        label: () => this.$t('cusEntry.reportManagement.plate'),
        minWidth: 130
      },
      {
        prop: 'orgName',
        // label: '公司',
        label: () => this.$t('components.organization.COMPANY'),
        minWidth: 130
      },
      {
        prop: 'projectName',
        // label: '项目名称',
        label: () => this.$t('bidMod.bidingName'),
        minWidth: 120
      },
      {
        prop: 'vendorUserNickname',
        // label: '供应商专家',
        label: () => this.$t('cusEntry.bidSuperviseReport.vendorPrincipal'),
        minWidth: 120
      },
      {
        prop: 'bidNoticeId.bidUserNickname',
        // label: '招标专家',
        label: () => this.$t('cusEntry.bidSuperviseReport.souPrincipal'),
        minWidth: 120
      },
      {
        prop: 'status',
        // label: '单据状态',
        label: () => this.$t('vendorMod.relegation.documentStatus'),
        dataType: 'dict',
        code: 'PRE_BID_FEEDBACK_STATUS',
        minWidth: 120
      },
      {
        prop: 'createdFullName',
        // label: '创建人'
        label: () => this.$t('common.creator')
      },
      {
        prop: 'creationDate',
        // label: '创建日期',
        label: () => this.$t('common.creationDate'),
        minWidth: 130,
        dataType: 'dateTime'
      },
      // {
      //   prop: '',
      //   label: '废弃说明',
      //   minWidth: 130
      // },
      {
        prop: 'operation',
        label: this.$t('bidMod.operation'),
        minWidth: 150,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          /**
           * 1.创建人可以删除
           * 2.需求人可编辑
           * 3.拟定可以编辑、删除
           * 4.待提交报告可以编辑
           */
          // 拟定可以编辑
          {
            show: row => ['DRAFT', 'ISSUED'].includes(row.status),
            formattor: () => this.$t('common.edit'),
            code: 'communicateFeedbackBuyer:edit',
            callback: row => {
              this.editTab('edit', row)
            }
          },
          // 拟定可以删除
          {
            show: row => ['DRAFT'].includes(row.status),
            formattor: () => this.$t('common.delete'),
            code: 'communicateFeedbackBuyer:delete',
            callback: row => {
              this.deleteRows(row)
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
        type: 'PreBidFeedbackBuyer',
        action: 'query',
        params,
        filterOperator: {
          creationDate: 'between'
        },
        query: {
          '*': {},
          'bidNoticeId': {
            '*': {}
          }
        }
      })
      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    editTab (type, row) {
      const map = new Map([
        // 新增
        [
          'add',
          {
            component: CommunicateFeedbackBuyerDetail,
            params: {
              flag: type,
              row,
              tabName: 'communicateFeedbackBuyer'
            },
            // title: '标前交流反馈',
            title: () => this.$t('cusEntry.supplement20250205.preBidCommunicationFeedback'),
            name: 'communicateFeedbackBuyer'
          }
        ],
        // 编辑
        [
          'edit',
          {
            component: CommunicateFeedbackBuyerDetail,
            params: {
              flag: type,
              row,
              tabName: row.bidFeedbackNo
            },
            // title: '标前交流反馈' + (row.bidFeedbackNo || ''),
            title: this.$t('cusEntry.supplement20250205.preBidCommunicationFeedback') + (row.bidFeedbackNo || ''),
            name: row.bidFeedbackNo
          }
        ],
        // 查看
        [
          'view',
          {
            component: CommunicateFeedbackBuyerDetail,
            params: {
              flag: type,
              row,
              tabName: row.bidFeedbackNo
            },
            // title: '标前交流反馈' + (row.bidFeedbackNo || ''),
            title: this.$t('cusEntry.supplement20250205.preBidCommunicationFeedback') + (row.bidFeedbackNo || ''),
            name: row.bidFeedbackNo
          }
        ],
        // 标前交流通知
        [
          'notice',
          {
            component: CommunicateNoticeBuyerDetail,
            params: {
              flag: 'view',
              row,
              tabName: row.bidNoticeNo
            },
            // title: '标前交流通知' + (row.bidNoticeNo || ''),
            title: this.$t('cusEntry.supplement20250205.preBidCommunicationNotice') + (row.bidNoticeNo || ''),
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
    },

    /** 删除 */
    async deleteRows (row) {
      const confirmResult = await this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => {})
      if (confirmResult !== 'confirm') return
      let transformParams = transformMQL.save('PreBidFeedbackBuyer', [row.bidFeedbackId], 'delete')
      const response = await commuFeedbackHttp.delete(transformParams)
      if (response) {
        this.$message.success(this.$t('common.successDelete'))
        this.getQueryData()
      }
    }
  }
}
</script>
