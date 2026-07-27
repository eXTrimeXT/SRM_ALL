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
      pageUrl: '/api-sup-ce/mdv/budgetAndWinAmountPage',
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
        prop: 'extOrgBuName',
        label: () => this.$t('cusEntry.reportManagement.plate')
      },
      {
        prop: 'extOrgOuName',
        label: () => this.$t('cusEntry.reportManagement.company')
      },
      {
        prop: 'extBudget',
        label: () => this.$t('cusEntry.reportManagement.budgetAmount')
      },
      {
        prop: 'winAmount',
        label: () => this.$t('cusEntry.reportManagement.bidAmount')
      },
      {
        prop: 'year',
        label: () => this.$t('cusEntry.reportManagement.yearOfOccurrence'),
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
        prop: 'extProjectNo',
        label: () => this.$t('cusEntry.reportManagement.projectNum'),
        minWidth: 120
      },
      {
        prop: 'extProjectName',
        label: () => this.$t('cusEntry.reportManagement.projectName'),
        minWidth: 120
      },
      {
        prop: 'extBudget',
        label: () => this.$t('cusEntry.reportManagement.budgetAmount'),
        minWidth: 120
      },
      {
        prop: 'winAmount',
        label: () => this.$t('cusEntry.reportManagement.bidAmount'),
        minWidth: 120
      },
      {
        prop: 'passTime',
        label: () => this.$t('cusEntry.reportManagement.bidNoticeApprovalTime'),
        minWidth: 120
      },
      {
        prop: 'year',
        label: () => this.$t('cusEntry.reportManagement.yearOfOccurrence'),
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
