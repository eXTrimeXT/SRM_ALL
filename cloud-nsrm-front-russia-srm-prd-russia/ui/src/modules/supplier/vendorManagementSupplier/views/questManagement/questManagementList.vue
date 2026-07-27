<template>
  <el-container
    class="flex-container questManagement_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            type="primary"
            @click="editTab('add')"
          >
            {{
              $t('common.add')
            }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="true"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-sup/quest/questSupplier/listPageByParm"
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
import questManagementDetail from './questManagementDetail'
import { parseTime, adaptDictData } from '@/utils'
import questManagementFlow from '../questManagement/questManagementFlow'
import questSupplierResultView from '../questSupplier/questSupplierResultView'

export default {
  name: 'QuestManagementList',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      name: 'questManagementList',
      tableName: 'questSupplierTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      curRole: this.$store.getters.userType, // vendor buyer
      integrationMode: '', // 工作流模板
      queryTodoList: [],
      tableHeader: [
        {
          prop: 'questNo',
          label: this.$t('quest.questNo'),
          width: 150,
          showType: 'button',
          btnStyle: 'text',
          callback: function (row) {
            this.editTab('view', row)
          }.bind(this)
        },
        {
          prop: 'questName',
          label: this.$t('quest.questName'),
          width: 150
        },
        {
          prop: 'approvalStatus',
          label: this.$t('quest.approvalStatus'),
          width: 100,
          dataType: 'dict', // 数据类型为字典
          code: 'QUEST_SUPPLIER_APPROVE_STATUS' // 字典code
        },
        {
          prop: 'companyCode',
          label: this.$t('common.vendorCode'),
          width: 150
        },
        {
          prop: 'companyName',
          label: this.$t('dashboard.vendorName'),
          minWidth: 150
        },
        {
          prop: 'questTemplateOrgName',
          label: this.$t('quest.questTemplateOrgName'),
          width: 200
        },
        {
          prop: 'questTemplateCode',
          label: this.$t('quest.questTemplateCode'),
          width: 100
        },
        {
          prop: 'questTemplateName',
          label: this.$t('quest.questTemplateName'),
          minWidth: 150
        },
        {
          prop: 'createdFullName',
          label: this.$t('common.creator'),
          width: 100
        },
        {
          prop: 'creationDate',
          label: this.$t('common.creationTime'),
          width: 150
        },
        {
          prop: 'lastUpdateDate',
          label: this.$t('vendorMod.modifyTime'),
          width: 150
        },
        {
          prop: 'operation',
          label: this.$t('common.operation'),
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          minWidth: 200,
          buttons: [
            // DRAFT:拟定;PUBLISH:已发布;WRITING:填写中;WRITED:已填写;SUBMITTED:已提交;REJECTED:已驳回;APPROVED:已审批;ABANDONED:已废弃;WITHDRAW:已撤回;
            {
              callback: (row) => this.editTab('edit', row),
              // code: "pr:requirementApply:edit",
              show: (row) => this.curRole === 'BUYER' && ['DRAFT'].includes(row.approvalStatus),
              formattor: () => {
                return this.$t('common.edit')
              }
            },
            {
              callback: (row) => this.deleteHandle(row),
              // code: "pr:requirementApply:edit",
              show: (row) => this.curRole === 'BUYER' && row.approvalStatus === 'DRAFT',
              formattor: () => {
                return this.$t('common.delete')
              }
            },
            {
              callback: (row) => this.approvalOne(row),
              // code: "pr:requirementApply:edit",
              // 打开跳到流程tab审批
              show: (row) =>
                this.curRole === 'BUYER' &&
                this.flowWithTabMode.includes(row.integrationMode) &&
                ((['PRE_PASS', 'WRITED'].includes(row.approvalStatus) &&
                row['workflowAuditStatus'] === 'WAIT')),
              formattor: () => {
                return this.$t('accountMod.review')
              }
            },
            {
              callback: function (row) {
                this.approvalOneItem(row)
              }.bind(this),
              formattor: (val) => {
                return this.$t('components.runningProcess.options.approved') // 审批通过
              },
              // code: "pr:purchaseApplicationList:approval",
              show: (row) =>
                // srmFlowMode为本地
                this.srmFlowMode.includes(row.integrationMode) && ['SUBMITTED', 'PRE_PASS'].includes(row.approvalStatus)
            }
          ]
        }
      ],
      tableData: [],
      filterConfig: [
        { prop: 'questNoForQuery', label: this.$t('quest.questNo') },
        { prop: 'questTemplateOrgId', label: this.$t('quest.questTemplateOrgName'), type: 'OUorganizationSelector' },
        {
          prop: 'companyIdForQuery',
          label: this.$t('dashboard.vendorName'),
          type: 'quicksearch',
          showKey: 'companyName',
          propKey: 'companyId',
          // name: 'scc_sup_company_info_display'
          name: 'scc_sup_company_info'
        },
        { prop: 'creationDateBegin', label: this.$t('dashboard.datetimeStart'), type: 'datetime' },
        { prop: 'creationDateEnd', label: this.$t('dashboard.datetimeEnd'), type: 'datetime' },
        {
          prop: 'approvalStatus',
          label: this.$t('quest.approvalStatus'),
          type: 'dict', // 字典类型
          code: 'QUEST_SUPPLIER_APPROVE_STATUS'
        }
      ],
      queryParam: {},
      approveStatusList: [], // 审批状态
      questTemplateTypeList: [], // 调查模板类型
      yesOrNoList: [] // 是否
    }
  },
  watch: {
    $route: {
      handler (nVal) {
        if (nVal && nVal.params) {
          let { from, row } = nVal.params
          if (from === 'portrait') {
            this.questSupplierResultView(row)
          }
        }
      },
      immediate: true,
      deep: true
    }
  },
  provide () {
    return { context: this }
  },
  async created () {
    if (this.$store.getters.userType === 'VENDOR') {
      this.filterConfig = this.filterConfig.filter(item => item.prop !== 'companyIdForQuery')
    }
    await this.getFlowIntegrationMode()
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  mounted () {},
  methods: {
    getQuerydata (params) {
      if (params === 'queryByParams') {
        this.$nextTick(() => {
          this.$refs[this.gridId].query()
        })
      } else {
        this.queryParam = params
        this.$nextTick(() => {
          this.$refs[this.gridId].query()
        })
      }
    },
    // 获取数据字典
    syncFilterParams (values) {
      this.queryParam = values
    },
    deleteHandle (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-sup/quest/questSupplier/deleteById',
            method: 'GET',
            params: { id: row.questSupId }
          }).then((res) => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    approvalOne (row) {
      this.$emit('tab-add', {
        component: questManagementFlow,
        params: {
          flag: 'approvalOnly',
          readOnly: true,
          row: row,
          tabName: 'questManagementFlow' + row.questSupId,
          activeWorkflowTab: true
        },
        title: this.$t('dashboard.enumerationForm') + row.questNo,
        name: 'questManagementFlow' + row.questSupId
      })
    },
    approvalOneItem (row) {
      this.$http({
        url: '/api-sup/quest/questSupplier/flow/questSupplierPass',
        method: 'POST',
        data: { questSupId: row.questSupId, approvalStatus: 'APPROVED' },
        loading: true
      })
        .then((data) => {
          this.$message.success(this.$t('common.success')) // 操作成功
          this.getQuerydata()
        })
        .catch((err) => {
          console.log(err)
        })
    },
    // 编辑tab
    editTab (type, row) {
      let tab = {}
      if (type === 'add') {
        // 新增
        tab = {
          component: questManagementDetail,
          params: {
            flag: 'add',
            row: row,
            tabName: 'questManagementDetail'
          },
          title: () => this.$t('route.questSupplierAdd'), // '调查表新增',
          name: 'questManagementDetail'
        }
      } else if (type === 'view') {
        // 查看
        tab = {
          component: questManagementDetail,
          params: {
            flag: 'view',
            row: row,
            tabName: 'questManagementDetail' + row.questSupId
          },
          title: this.$t('dashboard.enumerationForm') + row.questNo,
          name: 'questManagementDetail' + row.questSupId
        }
      } else {
        // 修改
        tab = {
          component: questManagementDetail,
          params: {
            flag: 'edit',
            row: row,
            tabName: 'questManagementDetail' + row.questSupId
          },
          title: this.$t('dashboard.enumerationForm') + row.questNo,
          name: 'questManagementDetail' + row.questSupId
        }
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
    questSupplierResultView (row) {
      this.$emit('tab-add', {
        component: questSupplierResultView,
        params: {
          flag: 'view',
          row: row,
          tabName: 'questSupplierResultView' + row.questSupId
        },
        title: this.$t('dashboard.enumerationForm') + row.questNo,
        name: 'questSupplierResultView' + row.questSupId
      })
    },
    afterQuery () {
      this.$refs[this.gridId].setTableData(async (tableData) => {
        for (let i = 0; i < tableData.length; i++) {
          this.$set(tableData[i], 'integrationMode', this.integrationMode)
        }
        if (this.notSearchTodoMode.includes(this.integrationMode)) {
          return
        }
        await this.listQueryTodo()
        for (let i = 0; i < tableData.length; i++) {
          var tableItem = tableData[i]
          for (let j = 0; j < this.queryTodoList.length; j++) {
            var todoItem = this.queryTodoList[j]
            if (tableItem.questSupId + '' === todoItem.businessId + '') {
              this.$set(tableItem, 'workflowAuditStatus', 'WAIT')
              break
            }
          }
        }
        console.log(tableData)
      })
    },
    async listQueryTodo () {
      let res = await this.$api.base.flowAPI.queryTodo({ businessType: 'questResultApprove' })
      this.queryTodoList = res.data
    },
    async getFlowIntegrationMode () {
      let res = await this.$api.base.flowAPI.getFlowIntegrationMode({ businessType: 'questResultApprove' })
      if (res.data) {
        this.integrationMode = res.data
        console.log('【integrationMode】', this.integrationMode)
      }
    }
  }
}
</script>
