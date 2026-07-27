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
  name: 'BlackSupplierQuantityList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  data () {
    return {
      preArr: [],
      pageUrl: '/api-sup-ce/mdv/blackSupplierPage',
      tableHeader: [],
      queryParams: {},
      dictCodes: {},
      pageSize: 15,
      gridList: 'BlackSupplierQuantityList'
    }
  },
  created () {
    this.preArr = [
      {
        prop: 'year',
        label: () => this.$t('cusEntry.reportManagement.effectiveYear'),
        type: 'year'
      },
      {
        prop: 'month',
        label: () => this.$t('cusEntry.reportManagement.effectiveMonth')
      }
    ]
    this.tableHeader = [
      {
        prop: 'year',
        label: () => this.$t('cusEntry.reportManagement.effectiveYear'),
        minWidth: 120
      },
      {
        prop: 'month',
        label: () => this.$t('cusEntry.reportManagement.effectiveMonth'),
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
