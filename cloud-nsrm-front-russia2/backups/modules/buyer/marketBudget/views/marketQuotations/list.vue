<template>
  <el-container
    class="flex-container mouldheader_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQueryData"
        @synchronous-value="syncFilterParams"
      />

      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            type="primary"
            code=""
            @click="createModel"
          >
            {{ $t('common.add') }}
          </AuthorityButton>
          <MImport
            title="导入"
            up-load-url="/api-cost/budget/budgetMarket/importExcel"
            :extra-data="extraData"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
          <ExportExcel
            page-url="/api-cost/budget/budgetMarket/listPage"
            :filter-params="filterParams"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            timeout="1000000"
            export-mode="front"
          />
        </template>
      </MainHeader>

      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        url="/api-cost/budget/budgetMarket/listPage"
        :checkChange="handleCurrentChange"
        :open-custom-table="true"
        :checkbox="true"
        :comActive="$attrs['changeTab']"
      />
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MImport from 'lib@/components/import'
import ExportExcel from 'lib@/components/export-excel'
import { parseTime } from '@/utils'
import { downloadFileLink } from 'lib@/utils/file'
import marketQuotationsDetail from './edit'

export default {
  name: 'MeetTodoList',

  components: {
    TableView,
    MainHeader,
    MImport,
    ExportExcel,
    FormWrapper
  },

  mixins: [tabTodoWatch, tabTodoMixin],

  data () {
    return {
      dictCodes: {
        marketType: 'COST_LINK_MARKET_TYPE',
        scene: 'BUDGET_BNS_TYPE',
        countType: 'BID_MARKET_COUNT_TYPE',
        priceType: 'BID_MARKET_BUDGET_PRICE_TYPE',
        status: 'BID_MARKET_BUDGET_STATUS'
      },
      filterParams: {},
      extraData: {
        sourceType: 'WEB_APP',
        uploadType: 'FASTDFS',
        fileModular: 'base',
        fileFunction: 'demoorder',
        fileType: 'excel'
      },
      currentRow: [],
      gridId: 'list',
      tableHeader: [],
      filterConfig: [
        // 待办编号
        { prop: 'budgetYear', label: this.$t('marketBudget.budgetYear') },
        { prop: 'marketStartTime', label: this.$t('marketBudget.marketStartTime'), type: 'date' },
        { prop: 'marketEndTime', label: this.$t('marketBudget.marketEndTime'), type: 'date' },
        { prop: 'countType', label: this.$t('marketBudget.countType'), type: 'dict', code: 'BID_MARKET_COUNT_TYPE' },
        { prop: 'priceType', label: this.$t('marketBudget.priceType'), type: 'dict', code: 'BID_MARKET_BUDGET_PRICE_TYPE' },
        { prop: 'marketType', label: this.$t('marketBudget.marketType'), type: 'dict', code: 'COST_LINK_MARKET_TYPE' }
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
      // 行情单号
      {
        prop: 'budgetNo',
        label: this.$t('marketBudget.budgetNo'),
        minWidth: 130,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.editHandle('view', row)
      },
      // 年度
      {
        prop: 'budgetYear',
        label: this.$t('marketBudget.budgetYear'),
        minWidth: 130
      },
      // 行情开始时间
      {
        prop: 'marketStartTime',
        label: this.$t('marketBudget.marketStartTime'),
        minWidth: 130,
        formattor: cellValue => (cellValue ? parseTime(cellValue, '{y}-{m}-{d}') : '')
      },
      // 行情结束时间
      {
        prop: 'marketEndTime',
        label: this.$t('marketBudget.marketEndTime'),
        minWidth: 130,
        formattor: cellValue => (cellValue ? parseTime(cellValue, '{y}-{m}-{d}') : '')
      },
      // 市况类型
      {
        prop: 'marketType',
        label: this.$t('marketBudget.marketType'),
        minWidth: 130,
        dataType: 'dict',
        code: 'COST_LINK_MARKET_TYPE'
      },
      // 产销场景
      {
        prop: 'scene',
        label: this.$t('marketBudget.scene'),
        minWidth: 130,
        dataType: 'dict',
        code: 'BUDGET_BNS_TYPE'
      },
      // 计算方式
      {
        prop: 'countType',
        label: this.$t('marketBudget.countType'),
        minWidth: 130,
        dataType: 'dict',
        code: 'BID_MARKET_COUNT_TYPE'
      },
      // 价格方式
      {
        prop: 'priceType',
        label: this.$t('marketBudget.priceType'),
        minWidth: 130,
        dataType: 'dict',
        code: 'BID_MARKET_BUDGET_PRICE_TYPE'
      },
      // 币种
      {
        prop: 'currency',
        label: this.$t('marketBudget.currency'),
        minWidth: 130,
        formattor: cellValue => 'CNY:人民币'
      },
      // 状态
      {
        prop: 'status',
        label: this.$t('marketBudget.status'),
        minWidth: 130,
        dataType: 'dict',
        code: 'BID_MARKET_BUDGET_STATUS'
      },
      // 创建人
      {
        prop: 'createdFullName',
        label: this.$t('marketBudget.createdFullName'),
        minWidth: 130
      },
      // 创建时间
      {
        prop: 'creationDate',
        label: this.$t('marketBudget.creationDate'),
        minWidth: 130,
        formattor: cellValue => (cellValue ? parseTime(cellValue, '{y}-{m}-{d}') : '')
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
            callback: row => this.editHandle('edit', row),
            show: row => row.status === 'DRAFT',
            formattor: () => {
              return this.$t('common.edit')
            }
          },
          // 确认
          {
            callback: row => this.confirmHandle(row),
            show: row => row.status === 'DRAFT',
            formattor: () => {
              return this.$t('common.affirm')
            }
          },
          // 删除
          {
            callback: row => this.deleteHandle(row),
            show: row => row.status === 'DRAFT',
            formattor: () => {
              return this.$t('common.delete')
            }
          },
          // 设定为预算编制
          {
            callback: row => this.budgetVersionHandle(row),
            show: row => row.status === 'CONFIRM',
            formattor: () => {
              return this.$t('marketBudget.budgetVersion')
            }
          },
          // 取消设定
          {
            callback: row => this.cancelBudgetVersionHandle(row),
            show: row => row.status === 'BUDGET_VERSION',
            formattor: () => {
              return this.$t('marketBudget.cancelBudgetVersion')
            }
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
    syncFilterParams (values) {
      this.filterParams = values
    },
    // 导入成功回调
    handleSuccess () {
      this.getQueryData()
    },
    downloadTemplate () {
      downloadFileLink('/api-cost/budget/budgetMarket/exportExcelTemplate', '导入模板.xlsx').catch(() => {
        this.$message.error('下载失败')
      })
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    createModel () {
      this.editHandle('add')
    },

    editHandle (type, row) {
      let name =
        type === 'add' ? this.$t('marketBudget.addMarketQuotations') : this.$t('marketBudget.marketQuotations') + row.budgetNo
      const tab = {
        component: marketQuotationsDetail,
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

    deleteHandle (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/api-cost/budget/budgetMarket/delete',
          method: 'GET',
          params: { id: row.budgetId },
          loading: true
        }).then(res => {
          this.$message.success(res.message)
          this.getQueryData()
        })
      })
    },

    confirmHandle (row) {
      this.$confirm(this.$t('确定执行确认操作？'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/api-cost/budget/budgetMarket/confirmResult',
          method: 'POST',
          data: { id: row.budgetId },
          loading: true
        }).then(res => {
          this.$message.success(res.message)
          this.getQueryData()
        })
      })
    },
    budgetVersionHandle (row) {
      this.$confirm(this.$t('确定制定预算版本？'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/api-cost/budget/budgetMarket/budgetVersion',
          method: 'POST',
          data: { id: row.budgetId },
          loading: true
        }).then(res => {
          this.$message.success(res.message)
          this.getQueryData()
        })
      })
    },
   cancelBudgetVersionHandle (row) {
      this.$confirm(this.$t('确定取消制定预算版本？'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/api-cost/budget/budgetMarket/cancelBudgetVersion',
          method: 'POST',
          data: { id: row.budgetId },
          loading: true
        }).then(res => {
          this.$message.success(res.message)
          this.getQueryData()
        })
      })
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

    handleValid () {
      if (!this.currentRow.length) return this.$message.warning('请选择数据')
      let flag = this.currentRow.every(item => {
        return item.status == 'DRAFT'
      })
      if (!flag) return this.$message.warning('只允许处理状态为拟定的数据')
      let list = this.currentRow.map(item => item.id)
      this.$confirm(this.$t('确认执行生效操作？'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/api-cost/budget/budgetMarket/valid',
          method: 'POST',
          data: { ids: list },
          loading: true
        }).then(res => {
          this.$message.success(res.message)
          this.getQueryData()
        })
      })
    },
    handleInvalid () {
      if (!this.currentRow.length) return this.$message.warning('请选择数据')
      let flag = this.currentRow.every(item => {
        return item.status == 'VALID'
      })
      if (!flag) return this.$message.warning('只允许处理状态为已生效的数据')
      let list = this.currentRow.map(item => item.id)
      this.$confirm(this.$t('确认执行失效操作？'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/api-cost/budget/budgetMarket/invalid',
          method: 'POST',
          data: { ids: list },
          loading: true
        }).then(res => {
          this.$message.success(res.message)
          this.getQueryData()
        })
      })
    },

    handleDelete (row) {
      if (!this.currentRow.length) return this.$message.warning('请选择数据')
      let flag = this.currentRow.every(item => {
        return item.status == 'DRAFT'
      })
      if (!flag) return this.$message.warning('只允许删除状态为拟定的数据')
      let list = this.currentRow.map(item => item.id)
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/api-cost/budget/budgetMarket/delete',
          method: 'DELETE',
          data: { ids: list },
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
