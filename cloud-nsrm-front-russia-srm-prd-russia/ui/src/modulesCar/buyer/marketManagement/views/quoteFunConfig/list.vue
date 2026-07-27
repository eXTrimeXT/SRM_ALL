<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <FormWrapper :form-array="filterConfig" @getFormData="getQueryData" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <!--新增-->
          <AuthorityButton
            code="marketLink:quoteFunConfig:add"
            type="primary"
            size="mini"
            @click="openDetailDialog('add', null)"
          >
            {{ $t('common.add') }}
          </AuthorityButton>
        </template>
      </MainHeader>

      <TableView
        ref="list"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :adeptMeiQl="true"
        :com-active="$attrs['changeTab']"
        open-custom-table
        custom-table-key="quoteFunConfig"
        url="/api-cost/api-ql/CostApiFunction/customQuery"
      />

      <!--新增编辑查看弹窗-->
      <DetailDialog
        v-if="detailDialogVisible"
        :visible.sync="detailDialogVisible"
        :flag="detailDialogFlag"
        :edit-row="editRow"
        @submit-success="getQueryData"
      />
    </el-main>
  </el-container>
</template>

<script>
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import DetailDialog from './components/detailDialog.vue'
import { transformMQL } from '@/library/utils/util'

export default {
  name: 'QuoteFunConfig',

  components: {
    TableView,
    MainHeader,
    FormWrapper,
    DetailDialog
  },

  data () {
    return {
      tableHeader: [
        // 函数名称
        {
          prop: 'functionName',
          label: this.$t('quoteTemplate.fun.name'),
          minWidth: 150,
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.openDetailDialog('view', row)
        },
        // 函数类型
        {
          prop: 'functionType',
          label: this.$t('quoteTemplate.fun.type'),
          minWidth: 180
        },
        // 状态
        {
          prop: 'status',
          label: this.$t('common.status'),
          minWidth: 100,
          dataType: 'dict',
          code: 'SOU_QUOTE_TEMP_API_STATUS'
        },
        // 创建人
        {
          prop: 'createdFullName',
          label: this.$t('common.creator'),
          minWidth: 100
        },
        // 创建时间
        {
          prop: 'creationDate',
          label: this.$t('common.creationTime'),
          minWidth: 100,
          formattor: val => this.$dayjsParse(val)
        },
        // 最后更新人
        {
          prop: 'lastUpdatedFullName',
          label: this.$t('common.lastUpdatePeople'),
          minWidth: 120
        },
        // 最后更新日期
        {
          prop: 'lastUpdateDate',
          label: this.$t('common.lastUpdateDate2'),
          minWidth: 120,
          formattor: val => this.$dayjsParse(val)
        },
        {
          prop: 'operation',
          label: this.$t('common.operation'),
          minWidth: 160,
          showType: 'buttons',
          fixed: 'right',
          btnStyle: 'text',
          buttons: [
            // 编辑
            {
              show: row => row.status === 'DRAFT',
              code: 'marketLink:quoteFunConfig:edit',
              callback: row => this.openDetailDialog('edit', row),
              formattor: () => this.$t('common.edit')
            },
            // 生效
            {
              show: row => row.status === 'DRAFT' || row.status === 'INVALID',
              code: 'marketLink:quoteFunConfig:valid',
              callback: row => this.handlerFn('active', row),
              formattor: () => this.$t('common.active')
            },
            // 失效
            {
              show: row => row.status === 'VALID',
              code: 'marketLink:quoteFunConfig:invalid',
              callback: row => this.handlerFn('inactive', row),
              formattor: () => this.$t('common.inactive')
            },
            // 删除
            {
              show: row => row.status === 'DRAFT',
              code: 'marketLink:quoteFunConfig:delete',
              callback: row => this.handlerFn('delete', row),
              formattor: () => this.$t('common.delete')
            }
          ]
        }
      ],
      tableData: [],
      filterConfig: [
        // 函数名称
        { prop: 'functionName', label: this.$t('quoteTemplate.fun.name'), queryOperator: 'contains' },
        // 状态
        {
          prop: 'status',
          label: this.$t('common.status'),
          type: 'dict',
          code: 'SOU_QUOTE_TEMP_API_STATUS'
        }
      ],
      queryParam: {},
      detailDialogVisible: false,
      detailDialogFlag: 'add',
      editRow: null
    }
  },

  mounted () {
    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  methods: {
    /* 查询列表数据 */
    getQueryData (params) {
      let query = {
        '*': {},
        'costApiFunctionLines': {
          '*': {}
        }
      }
      this.queryParam = transformMQL.listGetData('CostApiFunction', params, 'lastUpdateDate', query, 'customQuery', undefined, { status: 'eq' })
      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    // active 生效  inactive  失效  delete  删除
    handlerFn (type, row) {
      const obj = {
        active: {
          url: '/api-cost/api-ql/CostApiFunction/updateStatus',
          tips: '请选择处理状态为“拟定”或者“失效”的数据，进行生效操作',
          status: ['DRAFT', 'INVALID'],
          title: this.$t('marketBudget.enableCurrent'),
          method: 'POST',
          action: 'updateStatus',
          payload: {
            'filter': {
              'apiFunctionId': {
                'in': [row.apiFunctionId]
              }
            },
            'data': {
              'status': 'VALID'
            }
          }
        },
        inactive: {
          url: '/api-cost/api-ql/CostApiFunction/updateStatus',
          tips: '请选择处理状态为“生效”的数据，进行失效操作',
          status: ['VALID'],
          title: this.$t('marketBudget.disableCurrent'),
          method: 'POST',
          action: 'updateStatus',
          payload: {
            'filter': {
              'apiFunctionId': {
                'in': [row.apiFunctionId]
              }
            },
            'data': {
              'status': 'INVALID'
            }
          }
        },
        delete: {
          url: '/api-cost/api-ql/CostApiFunction/batchDelete',
          tips: '请选择处理状态为“拟定”的数据，进行删除操作',
          status: ['DRAFT'],
          title: this.$t('common.confirmDelete'),
          method: 'POST',
          action: 'batchDelete',
          payload: [{
            '$delete': row.apiFunctionId,
            'costApiFunctionLines': [
              {
                '$delete': '*'
              }
            ]
          }]
        }
      }
      if (obj[type]) {
        const { url, tips, status, title, method, payload, action } = obj[type]
        const flag = !status.includes(row.status)
        if (flag) {
          this.$message.warning(tips)
          return
        }
        this.$confirm(title, {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        }).then(() => {
          let formData = transformMQL.save('CostApiFunction', payload, action)
          this.$http({
            url,
            method,
            data: formData,
            loading: true
          }).then(res => {
            this.$message.success(res.message)
            this.getQueryData()
          })
        })
      }
    },

    /* 新增 / 编辑 / 查看 */
    openDetailDialog (type, row) {
      this.editRow = row
      this.detailDialogFlag = type
      this.detailDialogVisible = true
    }
  }
}
</script>
