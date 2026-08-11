<template>
  <el-container class="flex-container mouldheader_list_wrapper" direction="vertical">
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQueryData"
        @synchronous-value="syncFilterParams"
      />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <MImport
            title="导入"
            up-load-url="/api-cost/bid/materialWeight/importExcel"
            :extra-data="extraData"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
          <AuthorityButton type="primary" code="" @click="handleValid">
            {{ $t('common.active') }}
          </AuthorityButton>
          <AuthorityButton type="primary" code="" @click="handleInvalid">
            {{ $t('common.inactive') }}
          </AuthorityButton>
          <AuthorityButton type="primary" code="" @click="handleDelete">
            {{ $t('common.delete') }}
          </AuthorityButton>
          <ExportExcel
            page-url="/api-cost/bid/materialWeight/listPage"
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
        url="/api-cost/bid/materialWeight/listPage"
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
        status: 'BASE_INFO_STATUS'
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
        { prop: 'partCode', label: this.$t('marketBudget.partCode') },
        { prop: 'partName', label: this.$t('marketBudget.partName') },
        { prop: 'materialCode', label: this.$t('marketBudget.materialCode') },
        { prop: 'materialName', label: this.$t('marketBudget.materialName') },
        { prop: 'marketType',
label: this.$t('marketBudget.marketType'),
type: 'dict',
          code: 'COST_LINK_MARKET_TYPE' }
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
      // 零件编码
      {
        prop: 'partCode',
        label: this.$t('marketBudget.partCode'),
        minWidth: 130
      },
      // 零件名称
      {
        prop: 'partName',
        label: this.$t('marketBudget.partName'),
        minWidth: 130
      },
      // 市况类型
      {
        prop: 'marketType',
        label: this.$t('marketBudget.marketType'),
        minWidth: 130,
        dataType: 'dict',
        code: 'COST_LINK_MARKET_TYPE'
      },
      // 原材料编码
      {
        prop: 'materialCode',
        label: this.$t('marketBudget.materialCode'),
        minWidth: 130
      },
      // 原材料名称
      {
        prop: 'materialName',
        label: this.$t('marketBudget.materialName'),
        minWidth: 130
      },
      // 单位
      {
        prop: 'unitName',
        label: this.$t('marketBudget.unit'),
        minWidth: 130
      },
      // 重量
      {
        prop: 'quantity',
        label: this.$t('marketBudget.weight'),
        minWidth: 130
      },
      // 生效时间
      {
        prop: 'startTime',
        label: this.$t('marketBudget.enableDate'),
        minWidth: 130,
        formattor: (cellValue) => (cellValue ? parseTime(cellValue, '{y}-{m}-{d}') : '')
      },
      // 失效时间
      {
        prop: 'endTime',
        label: this.$t('marketBudget.disableDate'),
        minWidth: 130,
        formattor: (cellValue) => (cellValue ? parseTime(cellValue, '{y}-{m}-{d}') : '')
      },
      // 状态
      {
        prop: 'status',
        label: this.$t('marketBudget.status'),
        minWidth: 130,
        dataType: 'dict',
        code: 'BASE_INFO_STATUS'
      },
      {
        prop: 'createdFullName',
        label: this.$t('导入人姓名'),
        minWidth: 130
      },
      {
        prop: 'createdBy',
        label: this.$t('导入人工号'),
        minWidth: 130
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
      downloadFileLink(
        '/api-cost/bid/materialWeight/exportExcelTemplate',
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
      let list = this.currentRow.map(item => item.weightId)
      this.$confirm(this.$t('marketBudget.enableCurrent'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/api-cost/bid/materialWeight/effect',
          method: 'POST',
          data: { ids: list, status: 'VALID' },
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
      let list = this.currentRow.map(item => item.weightId)
      this.$confirm(this.$t('marketBudget.disableCurrent'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/api-cost/bid/materialWeight/effect',
          method: 'POST',
          data: { ids: list, status: 'INVALID' },
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
      let list = this.currentRow.map(item => item.weightId)
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/api-cost/bid/materialWeight/deleteBath',
          method: 'POST',
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
