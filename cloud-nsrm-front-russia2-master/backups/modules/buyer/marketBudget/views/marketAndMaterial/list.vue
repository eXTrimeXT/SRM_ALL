<template>
  <el-container class="flex-container mouldheader_list_wrapper" direction="vertical">
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQueryData"
        @synchronous-value="syncFilterParams"
      />

      <EasyTable
        ref="table"
        :selection="true"
        :methods="methods"
        :columns="tableHeader"
        row-key="id"
        table-name="redisCache"
        :query-params.sync="queryParam"
        @row-dblclick="rowDbClick"
        @selection-change="handleCurrentChange"
      >
        <template #btns>
          <MImport
            title="导入"
            up-load-url="/api-cost/marketType/material/importExcel"
            :extra-data="extraData"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
          <el-button type="primary" @click="add">
            {{ $t('common.add') }}
          </el-button>
          <el-button type="primary" @click="save">
            {{ $t('common.save') }}
          </el-button>
          <AuthorityButton type="primary" code="" @click="handleDelete">
            {{ $t('common.delete') }}
          </AuthorityButton>
          <AuthorityButton type="primary" code="" @click="handleValid">
            {{ $t('common.active') }}
          </AuthorityButton>
          <AuthorityButton type="primary" code="" @click="handleInvalid">
            {{ $t('common.inactive') }}
          </AuthorityButton>
          <ExportExcel
            page-url="/api-cost/marketType/material/listPage"
            :filter-params="filterParams"
            :table-header="tableHeader2"
            :dict-codes="dictCodes"
            timeout="1000000"
            export-mode="front"
          />
        </template>
        <template #marketType="{ scope }">
          <DictSelect
            v-model="scope.row.marketType"
            code="COST_LINK_MARKET_TYPE"
            @change="setFrequency(scope.row,scope)"
          />
        </template>
        <template #materialCode="{ scope }">
          <el-input v-model="scope.row.materialCode" />
        </template>
        <template #materialName="{ scope }">
          <el-input v-model="scope.row.materialName" />
        </template>
        <template #frequency="{ scope }">
          <DictSelect
            v-model="scope.row.frequency"
            code="BID_LINK_FREQUENCY"
          />
        </template>
      </EasyTable>
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'

import EasyTable from 'lib@/components/BaseTable/EasyTable'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MImport from 'lib@/components/import'
import ExportExcel from 'lib@/components/export-excel'
import { downloadFileLink } from 'lib@/utils/file'
import DictSelect from 'lib@/components/c-select/dict-select'
import { EDITABLE_KEY } from 'lib@/components/BaseTable/utils'

export default {
  name: 'MeetTodoList',

  components: {
    EasyTable,
    MainHeader,
    MImport,
    ExportExcel,
    FormWrapper,
    DictSelect
  },

  mixins: [tabTodoWatch, tabTodoMixin],

  data () {
    return {
      dictCodes: {
        marketType: 'COST_LINK_MARKET_TYPE',
        frequency: 'BID_LINK_FREQUENCY',
        status: 'BASE_INFO_STATUS'
      },
      tableHeader2: [
        {
          prop: 'marketType',
          label: () => this.$t('marketBudget.marketType')
        },
        {
          prop: 'materialCode',
          label: () => this.$t('marketBudget.materialCode')
        },
        {
          prop: 'materialName',
          label: () => this.$t('marketBudget.materialName')
        },
        {
          prop: 'frequency',
          label: () => this.$t('marketBudget.linkFrequency')
        },
        {
          prop: 'status',
          label: () => this.$t('marketBudget.status')
        },
        {
          prop: 'enableDate',
          label: () => this.$t('marketBudget.enableDate')
        },
        {
          prop: 'disableDate',
          label: () => this.$t('marketBudget.disableDate')
        }
      ],
      filterParams: {},
      extraData: {
        sourceType: 'WEB_APP',
        uploadType: 'FASTDFS',
        fileModular: 'base',
        fileFunction: 'demoorder',
        fileType: 'excel'
      },
      currentRow: [],
      tableHeader: [],
      methods: {
        listPage: async params => {
          let dataFun = await this.$http({
            url: '/api-cost/marketType/material/listPage',
            method: 'POST',
            data: params,
            loading: true
          })
          return dataFun
        }
      },
      filterConfig: [
        { prop: 'marketType',
label: this.$t('marketBudget.marketType'),
type: 'dict',
          code: 'COST_LINK_MARKET_TYPE' },
        // 原材料名称
        { prop: 'materialName', label: this.$t('marketBudget.materialName') }
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
      {
        attrs: {
          label: () => this.$t('marketBudget.marketType'),
          prop: 'marketType',
          sortable: true,
          formatter: (cellValue, row) =>
              this.$getDictLabel('COST_LINK_MARKET_TYPE', cellValue)
        },
        slot: 'marketType',
        rules: { required: true, message: this.$t('dataConfMod.required') }
      },
      {
        attrs: {
          label: () => this.$t('marketBudget.materialCode'),
          prop: 'materialCode',
          sortable: true
        },
        slot: 'materialCode',
        rules: { required: true, message: this.$t('dataConfMod.required') }
      },
      {
        attrs: {
          label: () => this.$t('marketBudget.materialName'),
          prop: 'materialName',
          sortable: true
        },
        slot: 'materialName',
        rules: { required: true, message: this.$t('dataConfMod.required') }
      },
      {
        attrs: {
          label: () => this.$t('marketBudget.linkFrequency'),
          prop: 'frequency',
          sortable: true,
          formatter: (cellValue, row) =>
              this.$getDictLabel('BID_LINK_FREQUENCY', cellValue)
        },
        slot: 'frequency',
        rules: { required: true, message: this.$t('dataConfMod.required') }
      },
      {
        attrs: {
          label: () => this.$t('marketBudget.status'),
          prop: 'status',
          sortable: true,
          formatter: (cellValue, row) =>
              this.$getDictLabel('BASE_INFO_STATUS', cellValue)
        },
        rules: { required: true, message: this.$t('dataConfMod.required') }
      },
      {
        attrs: {
          label: () => this.$t('marketBudget.enableDate'),
          prop: 'enableDate',
          sortable: true
        },
        rules: { required: true, message: this.$t('dataConfMod.required') }
      },
      {
        attrs: {
          label: () => this.$t('marketBudget.disableDate'),
          prop: 'disableDate',
          sortable: true
        },
        rules: { required: true, message: this.$t('dataConfMod.required') }
      }
    ]

    this.defaultTableHeader = this.tableHeader
  },

  methods: {

    setFrequency (row, scope) {
      // 获取联动频次
      this.$http({
        url: '/api-cost/marketType/material/getLinkFrequency',
        method: 'GET',
        params: { marketType: row.marketType },
        loading: true
      }).then(res => {
        let result = res.data || ''
        this.$set(this.$refs.table.realDataSource[scope.$index], 'frequency', result || '')
      })
    },
    rowDbClick (row) {
      if (row.status != 'DRAFT') {
        row[EDITABLE_KEY] = false
      }
    },
    add () {
      this.$refs.table.add({})
      this.$nextTick(() => {
        this.$refs.table.doLayout()
      })
    },
    async save () {
      if (!this.currentRow.length) return this.$message.warning('请选择数据')
      this.$refs.table.validate(f => {
        if (f) {
          let list = this.currentRow.map(item => {
            return {
              marketType: item.marketType,
              materialCode: item.materialCode,
              materialName: item.materialName,
              frequency: item.frequency
            }
          })

          this.$http({
            url: '/api-cost/marketType/material/save',
            method: 'POST',
            data: { list: list },
            loading: true
          }).then(res => {
            this.$message.success(res.message)
            this.getQueryData()
          })
        } else {
          this.$message({
            message: '请输入单据必填信息',
            type: 'error'
          })
        }
      })
    },
    syncFilterParams (values) {
      this.filterParams = values
    },
    // 导入成功回调
    handleSuccess () {
      this.getQueryData()
    },
    downloadTemplate () {
      downloadFileLink(
        '/api-cost/marketType/material/template',
        '导入模板.xlsx'
      ).catch(() => {
        this.$message.error('下载失败')
      })
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    createModel () {
      this.editHandle('add')
    },

    getQueryData (params) {
      if (!params) {
        params = {}
      }
      const { pageSize, pageNum } = this.queryParam
      this.$refs.table.search({ pageSize, pageNum, ...params }, true)
    },

    handleValid () {
      if (!this.currentRow.length) return this.$message.warning('请选择数据')
      let flag = this.currentRow.every(item => {
        return item.status == 'DRAFT'
      })
      if (!flag) return this.$message.warning('只允许处理状态为拟定的数据')
      let list = this.currentRow.map(item => item.id)
      this.$confirm(this.$t('marketBudget.enableCurrent'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/api-cost/marketType/material/valid',
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
      this.$confirm(this.$t('marketBudget.disableCurrent'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/api-cost/marketType/material/invalid',
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
          url: '/api-cost/marketType/material/delete',
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
