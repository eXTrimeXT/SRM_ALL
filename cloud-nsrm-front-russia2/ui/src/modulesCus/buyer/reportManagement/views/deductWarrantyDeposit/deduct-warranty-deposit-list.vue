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
      pageUrl: '/api-sup-ce/mdv/deductionDepositPage',
      tableHeader: [],
      queryParams: {},
      dictCodes: {
        description: 'EXT_DEDUCTION_TYPE'
      },
      pageSize: 15,
      gridList: 'BlackSupplierQuantityList'
    }
  },
  created () {
    this.preArr = [
      {
        prop: 'description',
        label: () => this.$t('cusEntry.reportManagement.deductType'),
        type: 'dict',
        code: 'EXT_DEDUCTION_TYPE'
      },
      {
        prop: 'orgBuName',
        label: () => this.$t('cusEntry.reportManagement.plate')
      },
      {
        prop: 'year',
        label: () => this.$t('cusEntry.reportManagement.year'),
        type: 'year'
      }
    ]
    this.tableHeader = [
      {
        prop: 'description',
        label: () => this.$t('cusEntry.reportManagement.deductType'),
        dataType: 'dict',
        code: 'EXT_DEDUCTION_TYPE'
      },
      {
        prop: 'orgBuName',
        label: () => this.$t('cusEntry.reportManagement.plate')
      },
      {
        prop: 'year',
        label: () => this.$t('cusEntry.reportManagement.year'),
        type: 'year'
      },
      {
        prop: 'totalAmount',
        label: () => this.$t('cusEntry.reportManagement.accumulatedDeduction'),
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
