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
      pageUrl: '/api-sup-ce/mdv/controlSupplierPage',
      tableHeader: [],
      queryParams: {},
      dictCodes: {
        supplierControlType: 'SUPPLIER_CONTROL_TYPE2'
      },
      pageSize: 15,
      gridList: 'BlackSupplierQuantityList'
    }
  },
  created () {
    this.preArr = [
      {
        prop: 'supplierControlType',
        label: () => this.$t('cusEntry.reportManagement.limitType'),
        type: 'dict',
        code: 'SUPPLIER_CONTROL_TYPE2'
      },
      {
        prop: 'yearMonth',
        label: () => this.$t('cusEntry.reportManagement.month'),
        type: 'month'
      }
    ]
    this.tableHeader = [
      {
        prop: 'supplierControlType',
        label: () => this.$t('cusEntry.reportManagement.limitType'),
        dataType: 'dict',
        code: 'SUPPLIER_CONTROL_TYPE2'
      },
      {
        prop: 'yearMonth',
        label: () => this.$t('cusEntry.reportManagement.month'),
        minWidth: 120
      },
      {
        prop: 'quantity',
        label: () => this.$t('cusEntry.reportManagement.limitQuantity'),
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
