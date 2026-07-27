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
  name: 'SpecialBiddingList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  data () {
    return {
      preArr: [],
      pageUrl: '/api-sup-ce/mdv/specialSouPage',
      tableHeader: [],
      queryParams: {},
      dictCodes: {},
      pageSize: 15,
      gridList: 'SpecialBiddingList'
    }
  },
  created () {
    this.preArr = [
      {
        prop: 'extOrgBuName',
        label: () => this.$t('cusEntry.reportManagement.plate')
      },
      {
        prop: 'extOrgOuName',
        label: () => this.$t('cusEntry.reportManagement.company')
      },
      {
        prop: 'passTime',
        label: () => this.$t('cusEntry.reportManagement.approvalEndYear'),
        type: 'year'
      }
    ]
    this.tableHeader = [
      {
        prop: 'extOrgBuName',
        label: () => this.$t('cusEntry.reportManagement.plate'),
        minWidth: 120
      },
      {
        prop: 'extOrgOuName',
        label: () => this.$t('cusEntry.reportManagement.company'),
        minWidth: 120
      },
      {
        prop: 'passTime',
        label: () => this.$t('cusEntry.reportManagement.approvalEndYear'),
        minWidth: 120
      },
      {
        prop: 'quantity',
        label: () => this.$t('cusEntry.reportManagement.quantity'),
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
