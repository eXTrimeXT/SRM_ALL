<template>
  <el-container class="flex-container mouldheader_list_wrapper" direction="vertical">
    <el-main>
      <FormWrapper :form-array="filterConfig" @getFormData="getQueryData" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton type="primary" code="marketLink:element:new" @click="add">
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
        url="/api-cost/api-ql/CostMarketElement/customQuery"
        :checkbox="false"
      />
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import marketElementDetail from './edit'
import { transformMQL } from '@/library/utils/util'

export default {
  name: 'MarketElementList',

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
        // 要素名称
        { prop: 'elementCode',
          label: this.$t('marketBudget.elementName'),
          type: 'dict',
          code: 'SCC_COST_MARKET_LINK_ELEMENT',
          queryOperator: 'contains'
        },
        // 市况类型
        {
          prop: 'marketType',
          label: this.$t('marketBudget.marketType'),
          type: 'dict',
          code: 'COST_LINK_MARKET_TYPE'
        },
        //  状态
        {
          prop: 'status',
          label: this.$t('marketBudget.status'),
          type: 'dict',
          code: 'BASE_INFO_STATUS'
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
      // 要素名称
      {
        prop: 'elementCode',
        label: this.$t('marketBudget.elementName'),
        minWidth: 150,
        dataType: 'dict',
        code: 'SCC_COST_MARKET_LINK_ELEMENT'

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
      // api名称
      {
        prop: 'functionName',
        label: this.$t('quoteTemplate.fun.name'),
        minWidth: 150
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
      // 更新人
      {
        prop: 'lastUpdatedUserName',
        label: this.$t('common.updatePeople'),
        minWidth: 150
      },
      // 更新时间
      {
        prop: 'lastUpdateDate',
        label: this.$t('common.updateTime'),
        minWidth: 150
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
            code: 'marketLink:element:edit',
            callback: row => this.editHandle('edit', row)
          },
          // 查看
          {
            show: row => ['VALID', 'INVALID'].includes(row.status),
            formattor: () => this.$t('common.view'),
            code: 'marketLink:element:view',
            callback: row => this.editHandle('view', row)
          },
          // 生效
          {
            show: row => ['DRAFT', 'INVALID'].includes(row.status),
            formattor: () => this.$t('common.active'),
            code: 'marketLink:element:valid',
            callback: row => this.validHandle(row)
          },
          // 失效
          {
            show: row => row.status === 'VALID',
            formattor: () => this.$t('common.inactive'),
            code: 'marketLink:element:invalid',
            callback: row => this.invalidHandle(row)
          },
          // 删除
          {
            show: row => row.status === 'DRAFT',
            formattor: () => this.$t('common.delete'),
            code: 'marketLink:element:del',
            callback: row => this.deleteHandle(row)
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
    validHandle (row) {
      this.$confirm(this.$t('确认执行生效操作？'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        let payload = {
          'filter': {
            'elementId': {
              'in': [row.elementId]
            }
          },
          'data': {
            'status': 'VALID'
          }
        }
        let formData = transformMQL.save('CostMarketElement', payload, 'updateStatus')
        this.$http({
          url: '/api-cost/api-ql/CostMarketElement/updateStatus',
          method: 'POST',
          loading: true,
          data: formData
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
            'elementId': {
              'in': [row.elementId]
            }
          },
          'data': {
            'status': 'INVALID'
          }
        }
        let formData = transformMQL.save('CostMarketElement', payload, 'updateStatus')
        this.$http({
          url: '/api-cost/api-ql/CostMarketElement/updateStatus',
          method: 'POST',
          loading: true,
          data: formData
        }).then(res => {
          this.$message.success(res.message)
          this.getQueryData()
        })
      })
    },
    deleteHandle (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        let formData = transformMQL.save('CostMarketElement', [{ elementId: row.elementId }], 'batchDelete')
        this.$http({
          url: '/api-cost/api-ql/CostMarketElement/batchDelete',
          method: 'POST',
          loading: true,
          data: formData
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
      let name = type === 'add' ? this.$t('新增要素定义') : this.$t('新增要素定义') + row.elementId
      const tab = {
        component: marketElementDetail,
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

    getQueryData (params = {}) {
      this.queryParam = transformMQL.listGetData('CostMarketElement', params, 'lastUpdateDate', undefined, 'customQuery', undefined, { status: 'eq' })
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    }
  }
}
</script>
