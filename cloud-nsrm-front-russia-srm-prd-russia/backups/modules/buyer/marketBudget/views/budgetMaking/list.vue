<template>
  <el-container class="flex-container mouldheader_list_wrapper" direction="vertical">
    <el-main>
      <FormWrapper :form-array="filterConfig" @getFormData="getQueryData" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton type="primary" code="" @click="add">
            {{ $t('common.add') }}
          </AuthorityButton>
        </template>
      </MainHeader>

      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-cost/budget/budgetVersion/listPage"
        :checkbox="false"
      />
    </el-main>
    <srm-dialog
      size="largePlus"
      title="市况基准/预估："
      :visible.sync="visible"
    >
      <div style="padding:10px">
        <table-view-vxe
          :ref="'gridId'"
          url="/api-cost/budget/budgetVersion/listBudgetPrice"
          :requestMethod="'POST'"
          :pre-query-data="subQueryParam"
          :table-header="subTableHeader"
        />
      </div>
    </srm-dialog>
  </el-container>
</template>

<script>
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import BudgetMakingDetail from './edit'
import { STORE_COMMON_CACHE } from '@/config/store-config'
import { parseTime } from '@/utils'

export default {
  name: 'BudgetMakingList',

  components: {
    TableView,
    MainHeader,
    FormWrapper
  },

  mixins: [tabTodoWatch, tabTodoMixin],

  data () {
    return {
      subQueryParam: {},
      visible: false,
      gridId: 'list',
      tableHeader: [],
      filterConfig: [
        // 待办编号
        { prop: 'versionName', label: this.$t('单据名称') },
        { prop: 'planVersion', label: this.$t('产销计划版本名称') },
        { prop: 'status', label: this.$t('单据状态'), type: 'dict', code: 'MARKET_BUDGET_VERSION_STATUS' },
        { prop: 'quoteStatus', label: this.$t('试算状态'), type: 'dict', code: 'BUDGET_VERSION_QUOTE_STATUS' },
        { prop: 'versionYear', label: this.$t('年度'), type: 'year' }

      ],
      queryParam: {},
      userInfo: this.$store.getters.userInfo,
      marketTypeList: [],
      subTableHeader: [
        {
          prop: 'marketType',
          label: this.$t('市况类型'),
          dataType: 'dict',
          code: 'COST_LINK_MARKET_TYPE',
          minWidth: 130
        },
        {
          prop: 'linkFrequency',
          label: this.$t('联动频次'),
          dataType: 'dict',
          code: 'BID_LINK_FREQUENCY',
          minWidth: 130
        },
        {
          prop: 'materialName',
          label: this.$t('原材料名称'),
          minWidth: 130
        },
        {
          prop: 'materialCode',
          label: this.$t('原材料编码'),
          minWidth: 130
        },
        {
          prop: 'forecastStartDate',
          label: this.$t('预估行情开始时间'),
          formattor: (cellValue) => (cellValue ? parseTime(cellValue, '{y}-{m}-{d}') : ''),
          minWidth: 160
        },
        {
          prop: 'forecastEndDate',
          label: this.$t('预估行情结束时间'),
          formattor: (cellValue) => (cellValue ? parseTime(cellValue, '{y}-{m}-{d}') : ''),
          minWidth: 160
        },

        {
          prop: 'forecastPrice',
          label: this.$t('市况预估价'),
          minWidth: 130
        },
        {
          prop: 'standardStartDate',
          label: this.$t('基准行情开始时间'),
          formattor: (cellValue) => (cellValue ? parseTime(cellValue, '{y}-{m}-{d}') : ''),
          minWidth: 160
        },
        {
          prop: 'standardEndDate',
          label: this.$t('基准行情结束时间'),
          formattor: (cellValue) => (cellValue ? parseTime(cellValue, '{y}-{m}-{d}') : ''),
          minWidth: 160
        },
        {
          prop: 'standardPrice',
          label: this.$t('市况基准价'),
          minWidth: 130
        },
        {
          prop: 'currencyCode',
          label: this.$t('币种'),
          dataType: 'dict',
          code: 'currency',
          minWidth: 130
        }
      ]
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
    this.$store
      .dispatch(STORE_COMMON_CACHE.LIST_DICT_DETAIL, { dictCode: 'COST_LINK_MARKET_TYPE' })
      .then((data) => {
        let result = data || []
        let temp = [...result]
        temp.unshift({ 'label': '全选', 'value': 'ALL' })
        this.marketTypeList = temp
      })
    let _this = this
    this.tableHeader = [
      // 年度
      {
        prop: 'versionYear',
        label: this.$t('年度'),
        minWidth: 130
      },
      // 市况类型
      {
        prop: 'marketType',
        label: this.$t('市况类型'),
        minWidth: 130,
        formattor (val) {
          return _this.$getDictLabelByValue(_this.marketTypeList, val)
        }
      },
      // 单据名称
      {
        prop: 'versionName',
        label: this.$t('单据名称'),
        minWidth: 130,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.editHandle('view', row)
      },
      // 单据状态
      {
        prop: 'status',
        label: this.$t('单据状态'),
        minWidth: 130,
        dataType: 'dict',
        code: 'MARKET_BUDGET_VERSION_STATUS'
      },
      // 试算状态
      {
        prop: 'quoteStatus',
        label: this.$t('试算状态'),
        minWidth: 130,
        dataType: 'dict',
        code: 'BUDGET_VERSION_QUOTE_STATUS'
      },
      // 试算异常原因
      {
        prop: 'quoteFailLog',
        label: this.$t('试算异常原因'),
        minWidth: 130
      },
      // 产销计划版本
      {
        prop: 'planVersion',
        label: this.$t('产销计划版本'),
        minWidth: 130
      },
      // 子版本
      {
        prop: 'developVersion',
        label: this.$t('子版本'),
        minWidth: 130
      },
      // 市况基准/预估
      {

        label: this.$t('市况基准/预估'),
        minWidth: 130,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.openDialog(row),
        formattor (val) {
          return '查看'// '编辑'
        }
      },
      // 创建人
      {
        prop: 'createdFullName',
        label: this.$t('创建人'),
        minWidth: 130
      },
      // 创建时间
      {
        prop: 'creationDate',
        label: this.$t('创建时间'),
        minWidth: 130
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
            formattor: () => this.$t('common.edit'),
            callback: row => this.editHandle('edit', row)
          },
          // 确认
          {
            show: row => row.status === 'DRAFT',
            formattor: () => this.$t('common.affirm'),
            callback: row => this.confirmHandle(row)
          },
          // 作废
          {
            show: row => row.status === 'DRAFT' || row.status === 'CONFIRM',
            formattor: () => this.$t('common.cancelled'),
            callback: row => this.cancelHandle(row)
          },
          // 传输
          {
            show: row => row.status === 'CONFIRM',
            formattor: () => this.$t('传输'),
            callback: row => this.transmissionHandle(row)
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
    openDialog (row) {
      this.visible = true
      this.subQueryParam = {
        id: row.versionId
      }
      this.$nextTick(() => {
        this.$refs['gridId'].query()
      })
    },
    transmissionHandle (row) {
      this.$confirm(this.$t('确定执行传输操作吗？'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: `/api-cost/budget/budgetVersion/transmission/${row.versionId}`,
          method: 'POST',
          data: { id: row.versionId },
          loading: true
        }).then(res => {
          this.$message.success(res.message)
          this.getQueryData()
        })
      })
    },
    confirmHandle (row) {
      this.$confirm(this.$t('确定执行确认操作吗？'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/api-cost/budget/budgetVersion/confirm',
          method: 'POST',
          data: { id: row.versionId },
          loading: true
        }).then(res => {
          this.$message.success(res.message)
          this.getQueryData()
        })
      })
    },
    add () {
      this.editHandle('add')
    },

    editHandle (type, row) {
      let name = type === 'add' ? this.$t('marketBudget.addBudgetMaking') : this.$t('marketBudget.budgetMaking') + '-' + row.versionName
      const tab = {
        component: BudgetMakingDetail,
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

    cancelHandle (row) {
      this.$confirm(this.$t('确定执行作废操作吗？'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/api-cost/budget/budgetVersion/abandoned',
          method: 'POST',
          data: { id: row.versionId },
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
