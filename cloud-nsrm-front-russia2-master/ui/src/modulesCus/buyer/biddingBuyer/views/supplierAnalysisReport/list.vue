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
        // 询价方
        {
          prop: 'orgOuId',
          type: 'OUorganizationSelector',
          label: () => this.$t('cusEntry.bidMod.inquiryCompany')
        },
        {
          prop: 'creationDateFrom',
          label: () => this.$t('components.beginDate'),
          type: 'date'
        },
        {
          prop: 'creationDateTo',
          label: () => this.$t('components.dateClosed'),
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
        // 询价方
        {
          prop: 'orgOuName',
          label: this.$t('cusEntry.bidMod.inquiryCompany'),
          minWidth: 150
        },
        // 询价单次数
        {
          prop: 'inqTimes',
          label: this.$t('cusEntry.supplement20250205.inqTimes'),
          minWidth: 120
        },
        // 报价单次数
        {
          prop: 'orderTimes',
          label: this.$t('cusEntry.supplement20250205.orderTimes'),
          minWidth: 120
        },
        // 报价参与率
        {
          prop: 'orderJoinRate',
          label: this.$t('cusEntry.supplement20250205.orderJoinRate'),
          minWidth: 120
        },
        // 报价物资项数
        {
          prop: 'itemTimes',
          label: this.$t('cusEntry.supplement20250205.itemTimes'),
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
