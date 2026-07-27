<template>
  <el-container class="flex-container mouldheader_list_wrapper" direction="vertical">
    <el-main>
      <FormWrapper :form-array="filterConfig" @getFormData="getQueryData" />
      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton type="primary" code="marketLink:formula:add" @click="add">
            {{ $t('common.add') }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :adeptMeiQl="true"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        :checkChange="handleCurrentChange"
        url="/api-cost/api-ql/CostMarketFormula/customQuery"
        checkbox
      />
    </el-main>
  </el-container>
</template>

<script>
import ListFlowBtn from 'lib@/components/c-workflow-button/ListFlowBtn'
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MarketFormulaDetail from './edit'
import { transformMQL } from '@/library/utils/util'

export default {
  name: 'MarketFormulaList',

  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ListFlowBtn
  },

  mixins: [tabTodoWatch, tabTodoMixin],

  data () {
    return {
      currentRows: [],
      gridId: 'list',
      tableHeader: [],
      filterConfig: [
        // 公式名称
        { prop: 'formulaName', label: this.$t('marketBudget.formulaName'), queryOperator: 'contains' },
        // 市况类型
        {
          prop: 'marketType',
          label: this.$t('marketBudget.marketType'),
          type: 'dict',
          code: 'COST_LINK_MARKET_TYPE'
        },
        // 状态
        {
          prop: 'status',
          label: this.$t('marketBudget.status'),
          type: 'dict',
          code: 'BASE_INFO_STATUS'
        },
        // 应用场景
        {
          prop: 'applicationScen',
          label: this.$t('marketBudget.usageScene'),
          type: 'dict',
          code: 'COST_LINK_SCENE_TYPE'
        }
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
      // 公式名称
      {
        prop: 'formulaName',
        label: this.$t('marketBudget.formulaName'),
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        align: 'left',
        callback: row => this.editHandle('view', row)
      },
      // 公式明细
      {
        prop: 'formulaDetailed',
        label: this.$t('marketBudget.formulaDetail'),
        minWidth: 150
      },
      // 联动频次
      {
        prop: 'frequency',
        label: this.$t('marketBudget.linkFrequency'),
        minWidth: 150,
        dataType: 'dict',
        code: 'COST_LINK_FREQUENCY'
      },
      // 市况类型
      {
        prop: 'marketType',
        label: this.$t('marketBudget.marketType'),
        minWidth: 150,
        dataType: 'dict',
        code: 'COST_LINK_MARKET_TYPE'
      },
      // 应用场景
      {
        prop: 'applicationScen',
        label: this.$t('marketBudget.usageScene'),
        minWidth: 150,
        dataType: 'dict',
        code: 'COST_LINK_SCENE_TYPE'
      },
      // 状态
      {
        prop: 'status',
        label: this.$t('marketBudget.status'),
        minWidth: 150,
        dataType: 'dict',
        code: 'BASE_INFO_STATUS'
      },
      // 创建人
      {
        prop: 'createdFullName',
        label: this.$t('marketBudget.createdFullName'),
        minWidth: 150
      },
      // 创建时间
      {
        prop: 'creationDate',
        label: this.$t('marketBudget.creationDate'),
        minWidth: 150
      },
      {
        prop: 'operation',
        label: this.$t('common.operation'),
        showType: 'buttons',
        btnStyle: 'text',
        fixed: 'right',
        width: 150,
        buttons: [
          // 编辑
          {
            show: row => row.status === 'DRAFT',
            formattor: () => this.$t('common.edit'),
            code: 'marketLink:formula:edit',
            callback: row => this.editHandle('edit', row)
          },
          // 查看
          {
            show: row => row.status === 'VALID' || row.status === 'INVALID',
            formattor: () => this.$t('common.view'),
            code: 'marketLink:formula:detail',
            callback: row => this.editHandle('view', row)
          },
          // 生效
          {
            show: row => row.status === 'DRAFT',
            code: 'marketLink:formula:valid',
            formattor: () => this.$t('common.active'),
            callback: row => this.validHandle(row)
          },
          // 失效
          {
            show: row => row.status === 'VALID',
            code: 'marketLink:formula:invalid',
            formattor: () => this.$t('common.inactive'),
            callback: row => this.invalidHandle(row)
          },
          // 删除
          {
            show: row => row.status === 'DRAFT',
            formattor: () => this.$t('common.delete'),
            code: 'marketLink:formula:delete',
            callback: row => this.deleteHandle(row)
          }
          // 复制
          // {
          //   formattor: () => this.$t('common.copy'),
          //   code: 'marketLink:formula:copy',
          //   callback: row => this.copyHandle(row),
          // },
        ]
      }
    ]

    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  methods: {
    validHandle (row) {
      this.$confirm(this.$t('确认执行生效操作？'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        let payload = {
          'filter': {
            'formulaId': {
              'in': [row.formulaId]
            }
          },
          'data': {
            'status': 'VALID'
          }
        }
        let formData = transformMQL.save('CostMarketFormula', payload, 'updateStatus')
        this.$http({
          url: '/api-cost/api-ql/CostMarketFormula/updateStatus',
          method: 'POST',
          data: formData,
          loading: true
        }).then(res => {
          this.$message.success(res.message)
          this.getQueryData()
        })
      })
    },
    copyHandle (row) {
      this.$confirm(this.$t('确认执行复制操作？'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: `/api-cost/api-ql/costMarketFormula/copy/${row.formulaId}`,
          method: 'get',
          loading: true
        }).then(res => {
          this.$message.success(res.message)
          this.getQueryData()
        })
      })
    },
    invalidHandle (row) {
      this.$confirm(this.$t('确认执行失效操作？'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        let payload = {
          'filter': {
            'formulaId': {
              'in': [row.formulaId]
            }
          },
          'data': {
            'status': 'INVALID'
          }
        }
        let formData = transformMQL.save('CostMarketFormula', payload, 'updateStatus')
        this.$http({
          url: '/api-cost/api-ql/CostMarketFormula/updateStatus',
          method: 'POST',
          data: formData,
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
      let name =
        type === 'add'
          ? this.$t('新增市况联动公式定义')
          : this.$t('市况联动公式定义') + row.formulaId
      const tab = {
        component: MarketFormulaDetail,
        params: {
          row,
          flag: type,
          tabName: name,
          getQueryData: this.getQueryData
        },
        title: name,
        name
      }
      this.$emit('tab-add', tab)
    },

    getQueryData (params) {
      let query = {
        '*': {},
        'costMarketFormulaLines': {
          '*': {}
        }
      }
      this.queryParam = transformMQL.listGetData('CostMarketFormula', params, 'lastUpdateDate', query, 'customQuery', undefined, { status: 'eq' })
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
        let payload = [{
          '$delete': row.formulaId,
          'costMarketFormulaLines': [
            {
              '$delete': '*'
            }
          ]
        }]
        let formData = transformMQL.save('CostMarketFormula', payload, 'batchDelete')
        this.$http({
          url: '/api-cost/api-ql/CostMarketFormula/batchDelete',
          method: 'POST',
          data: formData,
          loading: true
        }).then(res => {
          this.$message.success(res.message)
          this.getQueryData()
        })
      })
    },

    handleCurrentChange (val) {
      this.currentRows = val
    }
  }
}
</script>
