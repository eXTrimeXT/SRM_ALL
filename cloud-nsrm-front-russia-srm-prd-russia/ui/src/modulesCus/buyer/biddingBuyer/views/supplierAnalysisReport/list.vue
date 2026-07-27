<template>
  <el-container
    class="flex-container supplierAnalysisReportList"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @synchronous-value="syncFilterParams"
        @getFormData="getQuerydata"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <ExportExcel
            type="default"
            :page-url="tableUrl"
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
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :table-header="tableHeader"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        :url="tableUrl"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import ExportExcel from 'lib@/components/export-excel'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'

export default {
  name: 'SupplierAnalysisReportList',
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
      pageSize: 15,
      dictCodes: {},
      tableUrl: '/api-sou/extReportInq/priceRate/listPage',
      preArr: [
        {
          prop: 'vendorName',
          label: () => this.$t('common.vendorName')
        },
        {
          prop: 'vendorCode',
          label: () => this.$t('common.vendorCode')
        },
        {
          prop: 'orgOuId',
          type: 'OUorganizationSelector',
          label: '询价方'
        },
        {
          prop: 'creationDateFrom',
          label: '开始日期',
          type: 'date'
        },
        {
          prop: 'creationDateTo',
          label: '结束日期',
          type: 'date'
        }
      ],
      queryParam: {},
      filterParams: {},
      tableData: [],
      tableHeader: [
        {
          prop: 'vendorCode',
          label: () => this.$t('common.vendorCode'),
          minWidth: 150
        },
        {
          prop: 'vendorName',
          label: () => this.$t('common.vendorName'),
          minWidth: 150
        },
        {
          prop: 'orgOuName',
          label: '询价方',
          minWidth: 150
        },
        {
          prop: 'inqTimes',
          label: '询价单次数',
          minWidth: 120
        },
        {
          prop: 'orderTimes',
          label: '报价单次数',
          minWidth: 120
        },
        {
          prop: 'orderJoinRate',
          label: '报价参与率',
          minWidth: 120
        },
        {
          prop: 'itemTimes',
          label: '报价物资项数',
          minWidth: 120
        }
      ]
    }
  },
  created () {
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    syncFilterParams (values) {
      this.filterParams = values
    },
    getQuerydata (v) {
      this.queryParam = v || {}
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    }
  }
}
</script>
<style scoped lang="scss">
</style>
