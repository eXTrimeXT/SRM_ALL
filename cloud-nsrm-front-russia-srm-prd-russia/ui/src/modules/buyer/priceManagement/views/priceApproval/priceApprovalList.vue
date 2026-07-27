<template>
  <el-container
    class="flex-container the_inquiryApprovalFlow_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper :form-array="preArr" @getFormData="getQueryData" />

      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <!--b 创建价格审批单-->
          <el-button
            type="primary"
            @click="createProjectDetail"
          >
            {{ $t("bidMod.addInquiryApproval") }}
          </el-button>
        </template>
      </MainHeader>

      <TableView
        ref="list"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        url="/api-inq/price/approval/listPage"
        open-custom-table
        @afterQuery="afterQuery"
      />

      <!--创建合同-->
      <CreateContractDialog
        v-if="createContractDialogVisible"
        :visible.sync="createContractDialogVisible"
        :methods-opts="methodsOpts"
        :edit-row="editRow"
      />
    </el-main>
  </el-container>
</template>

<script>
import { parseTime } from '@/utils'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import priceApprovalDetail from './priceApprovalDetail'
import CreateContractDialog from './priceApprovalList/createContractDialog'

export default {
  name: 'PriceApprovalList',

  components: {
    TableView,
    MainHeader,
    FormWrapper,
    CreateContractDialog
  },

  mixins: [tabTodoWatch, tabTodoMixin],

  data () {
    return {
      gridId: 'list',
      tableHeader: [],
      tableData: [],
      queryParam: {},
      preArr: [
        // 价格审批单号
        { prop: 'approvalNo', label: () => this.$t('bidMod.approvalNo') },
        // 标题
        { prop: 'ceeaTitle', label: () => this.$t('bidMod.title') },
        // 审核状态
        {
          prop: 'status',
          label: () => this.$t('bidMod.auditStatus'),
          type: 'dict',
          code: 'PRICE_APPROVAL_STATUS'
        },
        // 寻源单号
        { prop: 'businessNo', label: () => this.$t('bidMod.businessNo') },
        // 创建时间
        {
          prop: 'creationDate',
          label: () => this.$t('bidMod.creationDate'),
          type: 'date'
        },
        // 创建人
        { prop: 'createdBy', label: () => this.$t('common.creator') },
        // 物料编码
        {
          prop: 'itemCode',
          label: () => this.$t('bidMod.itemCode'),
          type: 'quicksearch',
          showKey: 'materialCode',
          name: 'scc_base_material_item'
        },
        // 供应商名称
        {
          prop: 'vendorName',
          label: () => this.$t('bidMod.vendorName'),
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_display_buyer'
        }
      ],
      methodsOpts: [],
      createContractDialogVisible: false,
      editRow: null
    }
  },

  watch: {
    $route: {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler () {
        if (this.$route.params.from === 'fromFun' && this.$route.params.funName === 'priceApproval') {
          if (this.$route.params.formId) {
            // sourceType 必须有
            const approvalHeaderId = Number(this.$route.params.formId)
            const formNo = this.$route.params.formNo // 单据号
            const row = {
              ...this.$route.params,
              approvalHeaderId,
              approvalNo: formNo // tab 标题显示
            }
            this.tabAddApproval('edit', row)
          }
        }
      }
    }
  },

  created () {
    this.tableHeader = [
      // t 价格审批单号
      {
        prop: 'approvalNo',
        label: this.$t('bidMod.approvalNo'),
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.tabAddApproval('readonly', row)
      },
      // t 标题
      {
        prop: 'approvalTitle',
        label: this.$t('bidMod.title'),
        minWidth: 150
      },
      // t 寻源方式
      {
        prop: 'sourceType',
        label: this.$t('bidMod.sourceType'),
        minWidth: 120,
        formattor: val => this.$getDictLabel('SOURCING_TYPE', val)
      },
      // t 寻源单号
      {
        prop: 'sourceNo',
        label: this.$t('bidMod.businessNo'),
        minWidth: 150
      },
      // t 寻源标题
      // {
      //   prop: 'businessTitle',
      //   label: this.$t('bidMod.businessTitle'),
      //   minWidth: 150
      // },
      // t 审核状态
      {
        prop: 'status',
        label: this.$t('bidMod.auditStatus'),
        minWidth: 100,
        formattor: val => this.$getDictLabel('PRICE_APPROVAL_STATUS', val)
      },
      // t 创建人
      {
        prop: 'createdUserName',
        label: this.$t('bidMod.bidingCreatedBy'),
        minWidth: 100
      },
      // t 创建时间
      {
        prop: 'creationDate',
        label: this.$t('bidMod.creationDate'),
        minWidth: 150,
        formattor: val => val ? parseTime(val) : ''
      },
      {
        prop: 'operation',
        label: this.$t('bidMod.operation'),
        minWidth: 120,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          // b 审批
          {
            // 未审批
            show: row => row.status === 'RESULT_NOT_APPROVED',
            formattor: () => this.$t('bidMod.doApproval'),
            callback: row => this.tabAddApproval('approval', row)
          },
          // b 创建合同
          {
            // 已审批 && 不更新到价格库
            show: row => row.status === 'RESULT_PASSED' && row.ifUpdatePriceLibrary === 'N',
            formattor: () => this.$t('contractMod.createContract'),
            callback: row => this.openCreateContractDialog(row)
          },
          // b 编辑
          {
            // 拟定 , 'RESULT_REJECTED', 'WITHDRAW' 驳回撤回自定义上
            show: row => ['DRAFT'].includes(row.status),
            formattor: () => this.$t('common.edit'),
            callback: row => this.tabAddApproval('edit', row)
          },
          // b 废弃
          {
            show: row => ['WITHDRAW', 'RESULT_REJECTED'].includes(row.status),
            formattor: () => this.$t('common.abandon'),
            callback: row => this.abandonedApprovalFlow(row)
          },
          // b 删除
          {
            show: row => ['DRAFT', 'WITHDRAW', 'ABANDONED'].includes(row.status) && row.sourceType,
            formattor: () => this.$t('common.delete'),
            callback: row => this.deleteApprovalFlow(row)
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQueryData()
    })

    this.getFlowIntegrationMode()
  },

  methods: {
    /* 获取查询参数并执行查询 */
    getQueryData (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    /* 删除价格审批单 */
    deleteApprovalFlow (row) {
      this.$confirm(this.$t('common.delRow'), this.$t('common.tips'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: `/api-inq/price/approval/dropApproval/${row.approvalHeaderId}`,
          method: 'POST',
          loading: true
        }).then(() => {
          this.$message.success(this.$t('common.successDelete'))
          this.getQueryData()
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: this.$t('common.cancelDelete')
        })
      })
    },

    /* 废弃价格审批单 */
    abandonedApprovalFlow (row) {
      this.$http({
        url: '/api-inq/price/approval/abandon',
        method: 'GET',
        params: { approvalHeaderId: row.approvalHeaderId },
        loading: true
      }).then(() => {
        this.$message.success(this.$t('common.success'))
        this.getQueryData()
      })
    },

    /* 创建合同 */
    openCreateContractDialog (row) {
      this.editRow = row
      this.$http({
        url: '/api-inq/price/approval/checkWhetherCreateContract',
        method: 'GET',
        params: { approvalHeaderId: row.approvalHeaderId },
        loading: true
      }).then(res => {
        if (!res.data) {
          return this.$message.warning(this.$t('contractMod.noContractDetRow'))
        } else {
          this.getContractType(row)
        }
      })
    },

    /* 获取创建合同方式 */
    getContractType (row) {
      this.$http({
        url: '/api-inq/price/approval/getContractTypeByApprovalHeaderId',
        method: 'GET',
        params: { approvalHeaderId: row.approvalHeaderId },
        loading: true
      }).then(res => {
        this.methodsOpts = res.data
        this.createContractDialogVisible = true
      })
    },

    /* 创建价格审批单 */
    createProjectDetail () {
      this.$emit('tab-add', {
        component: priceApprovalDetail,
        params: { flag: 'add' },
        title: this.$t('bidMod.inquiryapproval'),
        name: 'priceApprovalDetail'
      })
    },

    /* 审批 / 查看 / 编辑 */
    tabAddApproval (flag, row) {
      this.$emit('tab-add', {
        component: priceApprovalDetail,
        params: { flag, row },
        title: row.approvalNo,
        name: `priceApprovalDetail${row.approvalNo}`
      })
    },

    afterQuery () {
      this.$refs[this.gridId].setTableData(async tableData => {
        tableData.forEach(item => this.$set(item, 'integrationMode', this.integrationMode))

        if (this.notSearchTodoMode.includes(this.integrationMode)) {
          return
        }

        await this.listQueryTodo()

        const maps = []
        this.queryTodoList.forEach(item => maps.push(item.businessId))
        tableData.forEach(row => {
          let tempId = String(row.approvalHeaderId)
          if (maps.includes(tempId)) {
            this.$set(row, 'workflowAuditStatus', 'WAIT')
            this.$set(row, 'arroverId', tempId)
          }
        })
      })
    },
    async listQueryTodo () {
      const res = await this.$api.base.flowAPI.queryTodo({ businessType: 'PRICE_APPROVAL_INIT' })
      this.queryTodoList = res.data
    },
    async getFlowIntegrationMode () {
      const res = await this.$api.base.flowAPI.getFlowIntegrationMode({ businessType: 'PRICE_APPROVAL_INIT' })
      if (res.data) {
        this.integrationMode = res.data
      }
    }
  }
}
</script>

<style scoped lang="scss">
.el-dialog__body {
  padding-top: 0 !important;
}
</style>
