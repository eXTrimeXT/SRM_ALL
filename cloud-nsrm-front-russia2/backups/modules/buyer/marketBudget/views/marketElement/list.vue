<template>
  <el-container
    class="flex-container mouldheader_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQueryData"
      />

      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            type="primary"
            code=""
            @click="add"
          >
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
        url="/api-cost/marketLink/element/listPage"
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

export default {
  name: 'MeetTodoList',

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
        { prop: 'elementName', label: this.$t('marketBudget.elementName') },
        { prop: 'marketType', label: this.$t('marketBudget.marketType'), type: 'dict', code: 'BASE_MARKET_TYPE' },
        { prop: 'status', label: this.$t('marketBudget.status'), type: 'dict', code: 'BASE_INFO_STATUS' }
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
        prop: 'elementName',
        label: this.$t('marketBudget.elementName'),
        minWidth: 130
      },
      // 要素类型
      {
        prop: 'elementType',
        label: this.$t('marketBudget.elementType'),
        minWidth: 130,
        dataType: 'dict',
        code: 'BASE_MARKET_LINK_ELEMENT_TYPE'
      },
      // 市况类型
      {
        prop: 'marketType',
        label: this.$t('marketBudget.marketType'),
        minWidth: 130,
        dataType: 'dict',
        code: 'BASE_MARKET_TYPE'
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
          url: `/api-cost/marketLink/element/valid/${row.id}`,
          method: 'POST',
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
          url: `/api-cost/marketLink/element/invalid/${row.id}`,
          method: 'POST',
          loading: true
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
        this.$http({
          url: `/api-cost/marketLink/element/delete/${row.id}`,
          method: 'DELETE',
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
        type === 'add' ? this.$t('新增要素定义') : this.$t('新增要素定义') + row.id
      const tab = {
        component: marketElementDetail,
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
    }
  }
}
</script>
