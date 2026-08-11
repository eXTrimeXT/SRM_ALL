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
import FormWrapper from 'lib@/components/Table/FormWrapper'
import ExportExcel from 'lib@/components/export-excel'
import MainHeader from 'lib@/components/Table/MainHeader'
export default {
  name: 'SpecialBiddingList',
  components: {
    TableView,
    FormWrapper,
    ExportExcel,
    MainHeader
  },
  data () {
    return {
      preArr: [],
      pageUrl: '/api-sou/securityDeposit/list',
      tableHeader: [],
      queryParams: {},
      dictCodes: {
        refund: 'YES_OR_NO',
        refundSuccess: 'YES_OR_NO'
      },
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
        prop: 'souName',
        label: () => this.$t('cusEntry.reportManagement.projectName')
      },
      {
        prop: 'extProjectNo',
        label: () => this.$t('cusEntry.reportManagement.projectNo')
      },
      {
        prop: 'mVendorName',
        label: () => this.$t('cusEntry.reportManagement.bidVendorName')
      },
      {
        prop: 'extApplicantDepart',
        label: () => this.$t('cusEntry.reportManagement.extApplicantDepart')
      },
      {
        prop: 'createdFullName',
        label: () => this.$t('cusEntry.reportManagement.createdFullName')
      },
      {
        prop: 'receivableAmount',
        label: () => this.$t('cusEntry.reportManagement.payAmount')
      },
      {
        prop: 'busEndTime',
        label: () => this.$t('cusEntry.reportManagement.orderEndTime'),
        type: 'date'
      },
      {
        prop: 'payName',
        label: () => this.$t('cusEntry.reportManagement.payVendorName')
      },
      {
        prop: 'transTime',
        label: () => this.$t('cusEntry.reportManagement.transTime'),
        type: 'date'
      },
      {
        prop: 'refundVendor',
        label: () => this.$t('cusEntry.reportManagement.refundVendorName')
      },
      {
        prop: 'refundAmount',
        label: () => this.$t('cusEntry.reportManagement.refundAmount')
      },
      {
        prop: 'refund',
        label: () => this.$t('cusEntry.reportManagement.isRefund'),
        type: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'refundSuccess',
        label: () => this.$t('cusEntry.reportManagement.isRefundSuccess'),
        type: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'refundPaymentDate',
        label: () => this.$t('cusEntry.reportManagement.refundPaymentDate'),
        type: 'date'
      },
      {
        prop: 'chargeVendor',
        label: () => this.$t('cusEntry.reportManagement.deductionVendorName')
      },
      {
        prop: 'chargeAmount',
        label: () => this.$t('cusEntry.reportManagement.deductionAmount')
      },
      {
        prop: 'creationDate',
        label: () => this.$t('cusEntry.reportManagement.deductionDate'),
        type: 'date'
      }
    ]
    this.tableHeader = [
      {
        prop: 'extOrgBuName',
        label: () => this.$t('cusEntry.reportManagement.plate'),
        minWidth: 150
      },
      {
        prop: 'extOrgOuName',
        label: () => this.$t('cusEntry.reportManagement.company'),
        minWidth: 150
      },
      {
        prop: 'souName',
        label: () => this.$t('cusEntry.reportManagement.projectName'),
        minWidth: 150
      },
      {
        prop: 'extProjectNo',
        label: () => this.$t('cusEntry.reportManagement.projectNo'),
        minWidth: 150
      },
      {
        prop: 'vendorCode',
        label: () => this.$t('cusEntry.reportManagement.vendorCode'),
        minWidth: 150
      },
      {
        prop: 'mVendorName',
        label: () => this.$t('cusEntry.reportManagement.bidVendorName'),
        minWidth: 150
      },
      {
        prop: 'extApplicantDepart',
        label: () => this.$t('cusEntry.reportManagement.extApplicantDepart'),
        minWidth: 150
      },
      {
        prop: 'createdFullName',
        label: () => this.$t('cusEntry.reportManagement.createdFullName'),
        minWidth: 150
      },
      {
        prop: 'receivableAmount',
        label: () => this.$t('cusEntry.reportManagement.payAmount'),
        minWidth: 150
      },
      {
        prop: 'publishTime',
        label: () => this.$t('cusEntry.reportManagement.techOpenTime'),
        minWidth: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'busEndTime',
        label: () => this.$t('cusEntry.reportManagement.orderEndTime'),
        minWidth: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'payName',
        label: () => this.$t('cusEntry.reportManagement.payVendorName'),
        minWidth: 150
      },
      {
        prop: 'paymentAmount',
        label: () => this.$t('cusEntry.reportManagement.extEarnestAmount'),
        minWidth: 150
      },
      {
        prop: 'payBank',
        label: () => this.$t('cusEntry.reportManagement.payBank'),
        minWidth: 150
      },
      {
        prop: 'bankLine',
        label: () => this.$t('cusEntry.reportManagement.bankLine'),
        minWidth: 150
      },
      {
        prop: 'payAccount',
        label: () => this.$t('cusEntry.reportManagement.payAccount'),
        minWidth: 150
      },
      {
        prop: 'marginStatus',
        label: () => this.$t('cusEntry.reportManagement.payFlag'),
        minWidth: 150
      },
      {
        prop: 'transTime',
        label: () => this.$t('cusEntry.reportManagement.transTime'),
        minWidth: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'causeDesc',
        label: () => this.$t('cusEntry.reportManagement.noPayCause'),
        minWidth: 150
      },
      {
        prop: 'refundVendor',
        label: () => this.$t('cusEntry.reportManagement.refundVendorName'),
        minWidth: 150
      },
      {
        prop: 'refundAmount',
        label: () => this.$t('cusEntry.reportManagement.refundAmount'),
        minWidth: 150
      },
      {
        prop: 'refundBank',
        label: () => this.$t('cusEntry.reportManagement.refundBank'),
        minWidth: 150
      },
      {
        prop: 'refundBankNum',
        label: () => this.$t('cusEntry.reportManagement.refundBankNum'),
        minWidth: 150
      },
      {
        prop: 'refundAccount',
        label: () => this.$t('cusEntry.reportManagement.refundAccount'),
        minWidth: 150
      },
      {
        prop: 'refund',
        label: () => this.$t('cusEntry.reportManagement.isRefund'),
        minWidth: 150,
        dataType: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'refundSuccess',
        label: () => this.$t('cusEntry.reportManagement.isRefundSuccess'),
        minWidth: 150,
        dataType: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'refundPaymentDate',
        label: () => this.$t('cusEntry.reportManagement.refundPaymentDate'),
        minWidth: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'refundDescription',
        label: () => this.$t('cusEntry.reportManagement.refundReason'),
        minWidth: 150
      },
      {
        prop: 'chargeVendor',
        label: () => this.$t('cusEntry.reportManagement.deductionVendorName'),
        minWidth: 150
      },
      {
        prop: 'chargeAmount',
        label: () => this.$t('cusEntry.reportManagement.deductionAmount'),
        minWidth: 150
      },
      {
        prop: 'creationDate',
        label: () => this.$t('cusEntry.reportManagement.deductionDate'),
        minWidth: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'chargeDescription',
        label: () => this.$t('cusEntry.reportManagement.description'),
        minWidth: 150
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
