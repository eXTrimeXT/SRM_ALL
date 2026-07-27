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
        url="/api-cost/marketLink/formula/listPage"
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
import MeetTodoDetail from './edit'

export default {
  name: 'MarketFormulaList',

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
        // 待办编号
        { prop: 'name', label: this.$t('marketBudget.formulaName') },
        { prop: 'marketType', label: this.$t('marketBudget.marketType'), type: 'dict', code: 'COST_LINK_MARKET_TYPE' },
        { prop: 'status', label: this.$t('marketBudget.status'), type: 'dict', code: 'BASE_INFO_STATUS' },
        { prop: 'usageScene', label: this.$t('marketBudget.usageScene'), type: 'dict', code: 'BASE_FORMULA_USAGE_SCENE' }
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
        prop: 'name',
        label: this.$t('marketBudget.formulaName'),
        minWidth: 130,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.editHandle('view', row)
      },
      // 公式明细
      {
        prop: 'detail',
        label: this.$t('marketBudget.formulaDetail'),
        minWidth: 130
      },
      // 联动频次
      {
        prop: 'frequency',
        label: this.$t('marketBudget.linkFrequency'),
        minWidth: 130,
        dataType: 'dict',
        code: 'BID_LINK_FREQUENCY'
      },
      // 市况类型
      {
        prop: 'marketType',
        label: this.$t('marketBudget.marketType'),
        minWidth: 130,
        dataType: 'dict',
        code: 'COST_LINK_MARKET_TYPE'
      },
      // 应用场景
      {
        prop: 'usageScene',
        label: this.$t('marketBudget.usageScene'),
        minWidth: 130,
        dataType: 'dict',
        code: 'BASE_FORMULA_USAGE_SCENE'
      },
      // 状态
      {
        prop: 'status',
        label: this.$t('marketBudget.status'),
        minWidth: 130,
        dataType: 'dict',
        code: 'BASE_INFO_STATUS'
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
          // 查看
          {
            show: row => row.status === 'VALID' || row.status === 'INVALID',
            formattor: () => this.$t('common.view'),
            callback: row => this.editHandle('view', row)
          },
          // 生效
          {
            show: row => row.status === 'DRAFT',
            formattor: () => this.$t('common.active'),
            callback: row => this.validHandle(row)
          },
          // 失效
          {
            show: row => row.status === 'VALID',
            formattor: () => this.$t('common.inactive'),
            callback: row => this.invalidHandle(row)
          },
          // 删除
          {
            show: row => row.status === 'DRAFT',
            formattor: () => this.$t('common.delete'),
            callback: row => this.deleteHandle(row)
          },
          // 复制
          {
            formattor: () => this.$t('复制'),
            callback: row => this.copyHandle(row)
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
        this.$http({
          url: `/api-cost/marketLink/formula/valid/${row.formulaId}`,
          method: 'POST',
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
          url: `/api-cost/marketLink/formula/copy/${row.formulaId}`,
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
        this.$http({
          url: `/api-cost/marketLink/formula/invalid/${row.formulaId}`,
          method: 'POST',
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
      let name = type === 'add' ? this.$t('新增市况联动公式定义') : this.$t('市况联动公式定义') + row.formulaId
      const tab = {
        component: MeetTodoDetail,
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

    deleteHandle (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: `/api-cost/marketLink/formula/delete/${row.formulaId}`,
          method: 'DELETE',
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
