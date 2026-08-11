<template>
  <el-container class="flex-container black_list_wrapper" direction="vertical">
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        queryName="demoListSearch"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton code="base:black:add" type="primary" @click="addHandle">
            {{
              $t('common.add')
            }}
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
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        customTableKey="demoListTableKey"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
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
import demoEdit from './edit.vue'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import ExportExcel from 'lib@/components/export-excel'
import { blackApi } from 'modb@/vendorManagementBuyer/api/black'

export default {
  name: 'BlackList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    MImport,
    ExportExcel
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
      userType: this.$store.getters.userType,
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      extraData: {
        fileModular: 'base',
        fileFunction: 'black',
        fileType: 'excel'
      },
      dictCodes: {
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
          // filterItem: () => { // 字典过滤案例
          //   if (this.userType !== 'BUYER') {
          //     return ['DRAFT']
          //   }
          // }
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
      queryTodoList: [],
      integrationMode: '', // 工作流模式
      workflowParamsInfo: {
        businessType: 'black', // 工作流编码
        businessIdKeyName: 'blackId' // 当前单据的主键ID名称
      }
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
          return val ? _this.$parseTime(val, '{y}-{m}-{d}') : ''
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
          this.orderHandle(row, 'view')
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
        width: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'expirationTime',
        label: this.$t('vendorMod.endDate'),
        width: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'createdBy',
        label: this.$t('common.creator')
      },
      {
        prop: 'creationDate',
        label: this.$t('common.creationTime'),
        width: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'operation',
        label: this.$t('components.headers.operation'),
        showType: 'buttons',
        btnStyle: 'text',
        fixed: 'right',
        width: 100,
        buttons: [
          // 编辑
          {
            callback: row => this.orderHandle(row, 'edit'),
            code: 'base:black:edit',
            show: row => row.approveStatus === 'DRAFT',
            formattor: () => {
              return this.$t('common.edit')
            }
          },
          // 删除
          {
            callback: row => this.deleteHandle(row),
            code: 'base:black:delete',
            show: row => row.approveStatus === 'DRAFT',
            formattor: () => {
              return this.$t('common.delete')
            }
          },
          // 提交审批
          {
            callback: row => this.orderHandle(row, 'approve'),
            formattor: () => {
              return this.$t('vendorMod.doApproval')
            },
            // 打开tab流程审批
            show: row => this.flowWithTabMode.includes(row.integrationMode) &&
            ((row.approveStatus !== 'APPROVED' && row.workflowAuditStatus === 'WAIT') || (row.approveStatus === 'SUBMITTED' && row.workflowAuditStatus !== 'WAIT'))
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    dolayout () {
      this.$refs[this.gridId].query()
    },
    handleSuccess () {
      this.getQuerydata()
    },
    downloadTemplate () {
      downloadFileLink(
        '/api-sup/sup/black/exportExcelTemplate',
        this.$t('drawingshead.drawingImportTemplate')
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },

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
    // 删除
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
        .catch(() => { })
    },
    // 新增
    addHandle (row) {
      this.mode = 'add'
      const tab = {
        component: demoEdit,
        params: {
          row,
          tabName: 'demoEdit',
          flag: this.mode,
          readOnly: false
        },
        title: this.$t('black.blackAdd'),
        name: 'demoEdit'
      }
      this.$emit('tab-add', tab)
    },
    orderHandle (row, type) {
      this.mode = type
      const tab = {
        component: demoEdit,
        params: {
          row,
          flag: this.mode, // 标识
          tabName: 'demoEdit' + row.blackId, // tabName
          activeWorkflowTab: type === 'approve', // 是否调到审批tab
          readOnly: type !== 'edit' // 只读 edit view approve
        },
        title: row.blackCode,
        name: 'demoEdit' + row.blackId
      }
      this.$emit('tab-add', tab)
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    // 上传附件成功
    handleUploadSuccess (file, row, key) {
      const { id, name } = file
      row[key] = id.toString()
    },
    // 删除文件
    handleAttachmentRemove (row, key) {
      row[key] = ''
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
    }
  }
}
</script>
