<template>
  <el-container class="flex-container black_list_wrapper" direction="vertical">
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton code="base:black:add" type="primary" @click="orderEditHandle({},'add')">
            {{ $t('common.add') }}
          </AuthorityButton>

          <ExportExcel
            page-url="/api-sup/sup/black/listPage"
            :filter-params="queryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            timeout="1000000"
            export-mode="front"
            type="default"
          />
          <!-- 列表页提交多个单据审批 -->
          <!-- <ListFlowBtn
            ref="workflowButtonSUBMIT"
            :disabled="false"
            type="default"
            button-name="提交审批"
            businessType="black"
            :integrationMode="integrationMode"
            :getOrderData="getFlowOrderData"
            @click-handler="submitApprove"
          /> -->
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :checkbox="true"
        :open-custom-table="true"
        :com-active="$attrs['changeTab']"
        :source="blackApi.list"
        @afterQuery="afterQuery"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import ListFlowBtn from 'lib@/components/c-workflow-button/ListFlowBtn'
import blackEdit from './edit.vue'
import listApprove from './listApprove.vue'
import { downloadFileLink } from 'lib@/utils/file'
import ExportExcel from 'lib@/components/export-excel'
import { blackApi } from 'modb@/vendorManagementBuyer/api/black'

export default {
  name: 'BlackList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel,
    ListFlowBtn
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      blackApi: blackApi,
      name: 'blackList',
      tableName: 'blackTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      flowOrderData: {},
      dictCodes: {
        companyType: 'COMPANY_NATURE',
        blackType: 'BLACK_TYPE',
        approveStatus: 'APPROVE_STATUS_TYPE'
      },
      filterParams: {},
      tableHeader: [],

      filterConfig: [
        { prop: 'blackCode', label: this.$t('black.blacklistApprovalNumber') },
        { prop: 'companyName', label: this.$t('common.vendorName') },
        { prop: 'socialCreditCode', label: this.$t('vendorMod.lcCode') },
        {
          prop: 'approveStatus',
          label: this.$t('vendorMod.relegation.documentStatus'),
          width: 180,
          type: 'dict',
          code: 'APPROVE_STATUS_TYPE'
        },
        {
          prop: 'dateList',
          width: 180,
          label: this.$t('common.creationTime'),
          type: 'daterange'
        },
        {
          prop: 'createdId',
          label: this.$t('common.creator'),
          type: 'quicksearch',
          showKey: 'nickname',
          propKey: 'userId',
          name: 'scc_rbac_user_display'
        }
      ],
      queryParam: {},
      queryTodoList: [], // 代办数据接收
      integrationMode: '' // 工作流模式
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'companyName',
        label: this.$t('common.vendorName'),
        width: 150
      },
      {
        prop: 'socialCreditCode',
        label: this.$t('vendorMod.lcCode'),
        width: 150
      },
      {
        prop: 'companyCreationDate',
        label: this.$t('bidMod.companyCreationDate'),
        width: 100,
        formattor (val) {
          return val ? _this.$dayjs(val).format('YYYY-MM-DD') : ''
        }
      },
      {
        prop: 'registeredCapital',
        label: this.$t('vendorMod.registeredCapital'),
        width: 140,
        aling: 'right'
      },
      {
        prop: 'companyType',
        label: this.$t('vendorMod.companyType'),
        dataType: 'dict',
        code: 'COMPANY_NATURE',
        width: 120
      },
      {
        prop: 'legalPerson',
        label: this.$t('vendorMod.corporateRepresentative'),
        width: 120
      },
      {
        prop: 'blackCode',
        label: this.$t('black.blacklistApprovalNumber'),
        width: 140,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.orderEditHandle(row, 'view')
        }.bind(this)
      },

      {
        prop: 'blackType',
        label: this.$t('black.blackType'),
        dataType: 'dict',
        code: 'BLACK_TYPE',
        width: 120
      },
      {
        prop: 'approveStatus',
        label: this.$t('vendorMod.relegation.documentStatus'),
        dataType: 'dict',
        code: 'APPROVE_STATUS_TYPE',
        width: 120,
        showType: 'statusCol', // 标识状态列
        statusList: { // 状态分类入参
          green: ['APPROVED'],
          red: ['REJECTED'],
          orange: ['SUBMITTED', 'WITHDRAW'],
          invalid: ['ABANDONED']
        }
      },

      {
        prop: 'effectiveTime',
        label: this.$t('vendorMod.startDate'),
        width: 100,
        formattor (val) {
          return val ? _this.$dayjs(val).format('YYYY-MM-DD') : ''
        }
      },
      {
        prop: 'expirationTime',
        label: this.$t('vendorMod.endDate'),
        width: 100,
        formattor (val) {
          return val ? _this.$dayjs(val).format('YYYY-MM-DD') : ''
        }
      },
      {
        prop: 'createdBy',
        label: this.$t('common.creator')
      },
      {
        prop: 'creationDate',
        label: this.$t('common.creationTime'),
        width: 100,
        formattor (val) {
          return val ? _this.$dayjs(val).format('YYYY-MM-DD') : ''
        }
      },
      {
        prop: 'operation',
        label: this.$t('components.headers.operation'),
        showType: 'buttons',
        btnStyle: 'text',
        fixed: 'right',
        width: 100,
        buttons: [
          {
            callback: row => this.orderEditHandle(row, 'edit'),
            code: 'base:black:edit',
            // 拟定、驳回、撤回 显示编辑按钮
            show: row => ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(row.approveStatus),
            formattor: () => {
              return this.$t('common.edit')
            }
          },
          {
            callback: row => this.deleteHandle(row),
            code: 'base:black:delete',
            show: row => row.approveStatus === 'DRAFT',
            formattor: () => {
              return this.$t('common.delete')
            }
          },
          {
            callback: row => this.approvalOne(row),
            formattor: () => {
              return this.$t('vendorMod.doApproval')
            },
            // 点击打开流程tab tab模式下，已提交 的状态 是当前人的审批单据时显示审批按钮
            // 已撤回、已驳回 显示编辑按钮重新提交
            show: row => this.flowWithTabMode.includes(row.integrationMode) &&
                          ['SUBMITTED'].includes(row.approveStatus) &&
                          row.workflowAuditStatus === 'WAIT'
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader
    this.getFlowIntegrationMode()
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },

  methods: {
    syncFilterParams (values) {
      this.filterParams = values
    },

    getQuerydata (obj) {
      const { dateList, ...rest } = obj || this.queryParam
      const params = {}
      if (dateList) {
        params.creationStartDate = dateList[0]
        params.creationEndDate = dateList[1]
      }
      this.queryParam = { ...rest, ...params }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },

    deleteHandle (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          blackApi.delete(row.blackCompanyId).then(res => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
    },

    orderEditHandle (row, type) {
      let tabName = type == 'add' ? 'blackEdit' : 'blackEdit' + row.blackId
      const tab = {
        component: blackEdit,
        ctrlHeight: true,
        params: {
          row,
          flag: type,
          tabName: tabName,
          activeWorkflowTab: false,
          readOnly: type == 'view'
        },
        title: type == 'add' ? '新增单据' : row.blackCode,
        name: tabName
      }
      this.$emit('tab-add', tab)
    },

    async approvalOne (row) {
      // const { data } = await this.$api.base.flowAPI.getFlowMainId(row.blackId)
      // data 为多个单据合并的ID
      // if (data) {
      //   this.$emit('tab-add', {
      //     component: listApprove,
      //     ctrlHeight: true,
      //     params: {
      //       flag: 'flowView',
      //       tabName: 'blackEdit' + data,
      //       activeWorkflowTab: true,
      //       businessId: data,
      //       readOnly: true
      //     },
      //     title: '合成审批单据' + data,
      //     name: 'blackEdit' + data
      //   })
      // } else {
        this.$emit('tab-add', {
          component: blackEdit,
          ctrlHeight: true,
          params: {
            flag: 'edit',
            row: row,
            tabName: 'blackEdit' + row.blackId,
            activeWorkflowTab: true,
            readOnly: true
          },
          title: row.blackCode,
          name: 'blackEdit' + row.blackId
        })
      // }
    },

    handleCurrentChange (val) {
      this.currentRows = val
    },

    afterQuery () {
      this.$refs[this.gridId].setTableData(async tableData => {
        for (let i = 0; i < tableData.length; i++) {
          this.$set(tableData[i], 'integrationMode', this.integrationMode)
        }
        if (this.notSearchTodoMode.includes(this.integrationMode)) {
          return
        }
        await this.listQueryTodo()
        for (let i = 0; i < tableData.length; i++) {
          const tableItem = tableData[i]
          for (let j = 0; j < this.queryTodoList.length; j++) {
            const todoItem = this.queryTodoList[j]
            if (tableItem.blackId + '' === todoItem.businessId + '') {
              this.$set(tableItem, 'workflowAuditStatus', 'WAIT')
              break
            }
          }
        }
      })
    },

    async listQueryTodo () {
      const res = await this.$api.base.flowAPI.queryTodo({ businessType: 'black' })
      this.queryTodoList = res.data
    },

    async getFlowIntegrationMode () {
      const res = await this.$api.base.flowAPI.getFlowIntegrationMode({ businessType: 'black' })
      if (res.data) {
        this.integrationMode = res.data
      }
    },
    beforeOpenHandle () {
      return true
    },
    // 获取提交单据信息
    getFlowOrderData () {
      // 数据选 拟定 驳回 撤回 的单据
      let selectData = this.currentRows.filter(item => ['REJECTED', 'DRAFT', 'WITHDRAW'].includes(item.approveStatus))
      let orderId = selectData.map(i => (i.blackId))
      if (selectData.length > 0) {
        return {
          businessData: selectData, // 选中的多个单据集合
          fileuploadIds: [], // 附件信息
          businessType: 'black',
          businessIds: orderId
        }
      } else {
        return this.$message.warning('拟定、驳回、撤回的单据可提交，请选择可提交的单据！')
      }
    },
    // 提交审批
    submitApprove (businessId) {
      console.log('businessId', businessId)
      if (this.srmFlowMode.includes(this.integrationMode)) {
        this.$message.success('提交成功！')
        this.getQuerydata()
      } else {
        this.$emit('tab-add', {
          component: listApprove,
          ctrlHeight: true,
          params: {
            flag: 'flowEdit',
            tabName: 'blackEdit' + businessId,
            activeWorkflowTab: true,
            businessId: businessId,
            readOnly: true
          },
          title: this.$t('returnGoodsBill.key13', {businessId: businessId}),
          name: 'blackEdit' + businessId
        })
      }
    }
  }
}
</script>
