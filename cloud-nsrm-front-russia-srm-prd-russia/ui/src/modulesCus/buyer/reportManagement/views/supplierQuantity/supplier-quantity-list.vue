<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <FormWrapper :form-array="preArr" @getFormData="getQueryData" />
    <MainHeader>
      <template slot="left">
        <ExportExcel
          :page-url="pageUrl"
          export-mode="front"
          :table-header="tableHeader"
          :dict-codes="dictCodes"
          :filter-params="queryParams"
          :title="$t('components.eio.customExport')"
          type="default"
        />
      </template>
    </MainHeader>
    <TableView
      :ref="gridList"
      :table-header="tableHeader"
      :page-size="pageSize"
      :pre-query-data="queryParams"
      open-custom-table
      :auto-query="true"
      :com-active="$attrs['changeTab']"
      :url="pageUrl"
    />
  </el-container>
</template>

<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import ExportExcel from 'lib@/components/export-excel'
export default {
  name: 'SupplierQuantityList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  data () {
    return {
      preArr: [],
      pageUrl: '/api-sup-ce/mdv/vendorQuantityListPage',
      tableHeader: [],
      queryParams: {},
      dictCodes: {
        extUseType: 'SUPPLIER_USE'
      },
      pageSize: 15,
      gridList: 'SupplierQuantityList'
    }
  },
  created () {
    this.preArr = [
      {
        prop: 'extUseType',
        label: () => this.$t('cusEntry.reportManagement.useClassify'),
        type: 'dict',
        code: 'SUPPLIER_USE'
      },
      {
        prop: 'year',
        label: () => this.$t('cusEntry.reportManagement.year'),
        type: 'year'
      },
      {
        prop: 'month',
        label: () => this.$t('cusEntry.reportManagement.month')
      }
    ]
    this.tableHeader = [
      {
        prop: 'extUseType',
        label: () => this.$t('cusEntry.reportManagement.useClassify'),
        minWidth: 120,
        dataType: 'dict',
        code: 'SUPPLIER_USE'
      },
      {
        prop: 'year',
        label: () => this.$t('cusEntry.reportManagement.year'),
        minWidth: 120
      },
      {
        prop: 'month',
        label: () => this.$t('cusEntry.reportManagement.month'),
        minWidth: 120
      },
      {
        prop: 'thisYearQuantity',
        label: () => this.$t('cusEntry.reportManagement.currentYearQuantity'),
        minWidth: 120
      },
      {
        prop: 'thisMonthQuantity',
        label: () => this.$t('cusEntry.reportManagement.currentMonthQuantity'),
        minWidth: 120
      },
      {
        prop: 'lastMonthQuantity',
        label: () => this.$t('cusEntry.reportManagement.lastMonthQuantity'),
        minWidth: 120
      }
    ]
  },
  methods: {
    // 查询
    getQueryData (params) {
      this.queryParams = params
      this.$nextTick(() => {
        this.$refs[this.gridList].query()
      })
    }
  }
}
</script>
