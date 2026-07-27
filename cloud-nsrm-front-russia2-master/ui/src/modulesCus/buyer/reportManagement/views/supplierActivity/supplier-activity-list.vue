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
      pageUrl: '/api-sup-ce/mdv/supplierActiveStatusPage',
      tableHeader: [],
      queryParams: {},
      dictCodes: {
        status: 'SUPPLIER_ACTIVITY_STATUS'
      },
      pageSize: 15,
      gridList: 'BlackSupplierQuantityList'
    }
  },
  created () {
    this.preArr = [
      {
        prop: 'status',
        label: () => this.$t('cusEntry.reportManagement.supplierActivityList'),
        type: 'dict',
        code: 'SUPPLIER_ACTIVITY_STATUS'
      }
    ]
    this.tableHeader = [
      {
        prop: 'companyCode',
        label: () => this.$t('cusEntry.reportManagement.vendorCode'),
        minWidth: 120
      },
      {
        prop: 'companyName',
        label: () => this.$t('cusEntry.reportManagement.vendorName'),
        minWidth: 120
      },
      {
        prop: 'yearMonth',
        label: () => this.$t('cusEntry.reportManagement.invitationSupplierBiddingTime'),
        minWidth: 180,
        dataType: 'dateTime'
      },
      {
        prop: 'statusType',
        label: () => this.$t('cusEntry.reportManagement.comparisonResults')
      },
      {
        prop: 'status',
        label: () => this.$t('cusEntry.reportManagement.supplierActivityList'),
        dataType: 'dict',
        code: 'SUPPLIER_ACTIVITY_STATUS'
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
