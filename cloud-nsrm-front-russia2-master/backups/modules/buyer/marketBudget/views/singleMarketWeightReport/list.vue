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
          <AuthorityButton type="primary" code="" @click="handleRefresh">
            {{ $t('common.refresh') }}
          </AuthorityButton>
          <ExportExcel
            page-url="/api-cost/singleMarket/weight/listPage"
            :filter-params="filterParams"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            timeout="1000000"
            export-mode="front"
          />
          <span style="margin-left:16px">{{ hasComplete? `刷新状态: ${hasComplete}`:'' }}</span>
        </template>
      </MainHeader>

      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-cost/singleMarket/weight/listPage"
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
import ExportExcel from 'lib@/components/export-excel'
import { parseTime } from '@/utils'

export default {
  name: 'SingleMarketWeightReportList',

  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },

  mixins: [tabTodoWatch, tabTodoMixin],

  data () {
    return {
      gridId: 'list',
      dictCodes: { marketType: 'COST_LINK_MARKET_TYPE' },
      tableHeader: [],
      filterConfig: [
        // 待办编号
        { prop: 'year', label: this.$t('marketBudget.year'), type: 'year', rules: { required: true, message: '请选择年度' } },
        { prop: 'partCode', label: this.$t('marketBudget.partCode') },
        { prop: 'partName', label: this.$t('marketBudget.partName') },
        { prop: 'mto', label: this.$t('marketBudget.mto') },
        { prop: 'marketType', label: this.$t('marketBudget.marketType'), type: 'dict', code: 'COST_LINK_MARKET_TYPE' }
      ],
      queryParam: {},
      filterParams: {},
      userInfo: this.$store.getters.userInfo,
      hasComplete: ''
    }
  },

  watch: {
    $route: {
      handler (val) {
        if (val) {
          let { from, row } = val.params || {}
        }
      },
      immediate: true,
      deep: true
    }
  },

  created () {
    this.tableHeader = [

      // 车型名称
      {
        prop: 'modelName',
        label: this.$t('marketBudget.modelName'),
        minWidth: 130
      },
      // 车型代码
      {
        prop: 'modelCode',
        label: this.$t('marketBudget.modelCode'),
        minWidth: 130
      },
      // MTO
      {
        prop: 'mto',
        label: this.$t('marketBudget.mto'),
        minWidth: 130
      },
      // 阀门
      {
        prop: 'tap',
        label: this.$t('marketBudget.tap'),
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
      // 原材料
      {
        prop: 'materialName',
        label: this.$t('marketBudget.materialName'),
        minWidth: 130
      },
      // 原材料
      {
        prop: 'materialCode',
        label: this.$t('marketBudget.materialCode'),
        minWidth: 130
      },
      // 单位
      {
        prop: 'unit',
        label: this.$t('marketBudget.unit'),
        minWidth: 130
      },
      // 重量
      {
        prop: 'weight',
        label: this.$t('marketBudget.weight'),
        minWidth: 130
      },
      // 生效时间
      {
        prop: 'startTime',
        label: '生效时间',
        width: 100,
        formattor: (cellValue) => (cellValue ? parseTime(cellValue, '{y}-{m}-{d}') : '')
      },
      // 失效时间
      {
        prop: 'endTime',
        label: '失效时间',
        width: 100,
        formattor: (cellValue) => (cellValue ? parseTime(cellValue, '{y}-{m}-{d}') : '')
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
      }
    ]

    this.defaultTableHeader = this.tableHeader
    this.hasCompleteFun()
  },

  methods: {
    syncFilterParams (values) {
      this.filterParams = values
    },
    handleRefresh () {
      this.$confirm(this.$t('确认执行刷新操作？'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/api-cost/singleMarket/weight/refresh',
          method: 'POST',
          loading: true
        }).then(res => {
          this.$message.success('数据处理中，请稍后再点击查询按钮')
          this.hasCompleteFun()
        })
      })
    },
    hasCompleteFun () {
      this.$http({
          url: '/api-cost/budgetTaskRecord/hasComplete',
          method: 'POST',
          data: { type: 'BUILD_SINGLE_MARKET_WEIGHT_REPORT' },
          loading: true
        }).then(res => {
          if (res.data) {
            this.hasComplete = '执行中'
          } else {
            this.hasComplete = '执行完成'
          }
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
      this.hasCompleteFun()
    }
  }
}
</script>
