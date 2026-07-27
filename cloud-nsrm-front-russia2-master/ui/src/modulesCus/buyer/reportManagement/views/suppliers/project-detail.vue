<template>
  <el-container class="flex-contianer" direction="vertical">
    <el-main>
      <TableView
        :table-header="tableHeader"
        :table-infor="tableData"
        :page-enabled="false"
        :com-active="$attrs['changeTab']"
      />
    </el-main>
  </el-container>
</template>

<script>
import TableView from 'lib@/components/Table/TableView'
import { supplierReportApi } from './api'
export default {
  name: 'ProjectDetail',
  components: {
    TableView
  },
  data () {
    return {
      tableData: [],
      tableHeader: []
    }
  },
  created () {
    this.tableHeader = [
      {
        prop: 'zbCode',
        label: () => this.$t('cusEntry.reportManagement.projectNo'),
        minWidth: 120
      },
      {
        prop: 'supName',
        label: () => this.$t('cusEntry.reportManagement.vendorName'),
        minWidth: 120
      },
      {
        prop: 'projectName',
        label: () => this.$t('cusEntry.reportManagement.projectName'),
        minWidth: 120
      },
      {
        prop: 'tbPerson',
        label: () => this.$t('cusEntry.reportManagement.bidContactName'),
        minWidth: 120
      },
      {
        prop: 'tbTel',
        label: () => this.$t('cusEntry.reportManagement.linkMethod'),
        minWidth: 120
      },
      {
        prop: 'pl',
        label: () => this.$t('cusEntry.reportManagement.categoryOrTarget'),
        minWidth: 120
      },
      {
        prop: 'projectStatus',
        label: () => this.$t('cusEntry.reportManagement.projectStatus'),
        minWidth: 120
      },
      {
        prop: 'sfBid',
        label: () => this.$t('cusEntry.reportManagement.ifSubmitBid'),
        minWidth: 120,
        formattor: val => {
          return this.$getDictLabel('YES_OR_NO', val)
        }
      },
      {
        prop: 'bBidReason',
        label: () => this.$t('cusEntry.reportManagement.noBidReason'),
        minWidth: 120
      },
      {
        prop: 'sfBidder',
        label: () => this.$t('cusEntry.reportManagement.ifBid'),
        minWidth: 120,
        formattor: val => {
          return this.$getDictLabel('YES_OR_NO', val)
        }
      },
      {
        prop: 'bidderMoney',
        label: () => this.$t('cusEntry.reportManagement.bidAmount'),
        formattor: val => val ? parseFloat(Number(val || 0)) : null,
        width: 140
      },
      {
        prop: 'htStatus',
        label: () => this.$t('cusEntry.reportManagement.contractStatus'),
        minWidth: 120,
        formattor: val => {
          return this.$getDictLabel('PROJECT_SCORE_HEADER_STATUS', val)
        }
      },
      {
        prop: 'htGetScore',
        label: () => this.$t('cusEntry.reportManagement.contarctScore'),
        minWidth: 120
      },
      {
        prop: 'htResult',
        label: () => this.$t('cusEntry.reportManagement.contractResult'),
        width: 140
      },
      {
        prop: 'htDealResult',
        label: () => this.$t('cusEntry.reportManagement.contractHandlerResult'),
        minWidth: 120
      }
    ]
    const {
      supId
    } = this.$attrs.params.row
    const {
      projectName,
      pl,
      projectCode
    } = this.$attrs.params.queryParams
    let params = {}
    if (projectName) {
      params.projectName = projectName
    }
    if (pl) {
      params.pl = pl
    }
    if (projectCode) {
      params.projectCode = projectCode
    }
    supplierReportApi.getDetail({ supId, ...params }).then(res => {
      if (res.data) {
        this.tableData = res.data
      }
    })
  }
}
</script>
