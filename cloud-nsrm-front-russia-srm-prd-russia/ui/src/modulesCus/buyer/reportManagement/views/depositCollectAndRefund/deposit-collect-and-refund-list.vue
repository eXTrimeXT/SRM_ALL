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
  name: 'DepositCollectAndRefundList',
  components: {
    TableView,
    FormWrapper,
    ExportExcel,
    MainHeader
  },
  data () {
    return {
      preArr: [],
      pageUrl: '/api-sou/earnestMoney/list',
      tableHeader: [],
      queryParams: {},
      dictCodes: {
        applyHandleType: 'SOU_APPLY_HANDLE_TYPE',
        refundReason: 'INT_DEPOSIT_REFUND_REASON',
        refund: 'YES_OR_NO',
        refundSuccess: 'YES_OR_NO',
        hStatus: 'YES_OR_NO'
      },
      pageSize: 15,
      gridList: 'DepositCollectAndRefundList'
    }
  },
  created () {
    this.preArr = [
      {
        prop: 'reqHeadNo',
        label: () => this.$t('cusEntry.reportManagement.reqHeadNo')
      },
      {
        prop: 'orgBuName',
        label: () => this.$t('cusEntry.reportManagement.plate')
      },
      {
        prop: 'orgName',
        label: () => this.$t('cusEntry.reportManagement.company')
      },
      {
        prop: 'projectName',
        label: () => this.$t('cusEntry.reportManagement.projectName')
      },
      {
        prop: 'publicEndTime',
        label: () => this.$t('cusEntry.reportManagement.publicEndTime'),
        type: 'date'
      },
      {
        prop: 'intentionReceivable',
        label: () => this.$t('cusEntry.reportManagement.depositAmount')
      },
      {
        prop: 'responsibilityUserName',
        label: () => this.$t('cusEntry.reportManagement.responsibilityUserName')
      },
      {
        prop: 'vendorCode',
        label: () => this.$t('cusEntry.reportManagement.vendorCode')
      },
      {
        prop: 'aVendorName',
        label: () => this.$t('cusEntry.reportManagement.aVendorName')
      },
      {
        prop: 'payerName',
        label: () => this.$t('cusEntry.reportManagement.vendorBankAccountName')
      },
      {
        prop: 'transTime',
        label: () => this.$t('cusEntry.reportManagement.transTime'),
        type: 'date'
      },
      {
        prop: 'iVendorName',
        label: () => this.$t('cusEntry.reportManagement.iVendorName')
      },
      {
        prop: 'creationDate',
        label: () => this.$t('cusEntry.reportManagement.creationDate'),
        type: 'date'
      },
      {
        prop: 'iStatus',
        label: () => this.$t('cusEntry.reportManagement.iStatus'),
        type: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'refundSupplierName',
        label: () => this.$t('cusEntry.reportManagement.depositRefundVendorName')
      },
      {
        prop: 'refundTime',
        label: () => this.$t('cusEntry.reportManagement.refundTime'),
        type: 'date'
      },
      {
        prop: 'refund',
        label: () => this.$t('cusEntry.reportManagement.ifRefund'),
        type: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'refundSuccess',
        label: () => this.$t('cusEntry.reportManagement.refundSuccess'),
        type: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'hStatus',
        label: () => this.$t('cusEntry.reportManagement.hStatus'),
        type: 'dict',
        code: 'YES_OR_NO'
      }
    ]
    this.tableHeader = [
      {
        prop: 'reqHeadNo',
        label: () => this.$t('cusEntry.reportManagement.reqHeadNo'),
        minWidth: 120
      },
      {
        prop: 'orgBuName',
        label: () => this.$t('cusEntry.reportManagement.plate'),
        minWidth: 120
      },
      {
        prop: 'orgName',
        label: () => this.$t('cusEntry.reportManagement.company'),
        minWidth: 120
      },
      {
        prop: 'projectName',
        label: () => this.$t('cusEntry.reportManagement.projectName'),
        minWidth: 120
      },
      {
        prop: 'publicEndTime',
        label: () => this.$t('cusEntry.reportManagement.publicEndTime'),
        minWidth: 120
      },
      {
        prop: 'intentionReceivable',
        label: () => this.$t('cusEntry.reportManagement.depositAmount'),
        minWidth: 120
      },
      {
        prop: 'responsibilityUserName',
        label: () => this.$t('cusEntry.reportManagement.responsibilityUserName'),
        minWidth: 120
      },
      {
        prop: 'vendorCode',
        label: () => this.$t('cusEntry.reportManagement.vendorCode'),
        minWidth: 120
      },
      {
        prop: 'aVendorName',
        label: () => this.$t('cusEntry.reportManagement.aVendorName'),
        minWidth: 120
      },
      {
        prop: 'payerName',
        label: () => this.$t('cusEntry.reportManagement.vendorBankAccountName'),
        minWidth: 120
      },
      {
        prop: 'transTime',
        label: () => this.$t('cusEntry.reportManagement.transTime'),
        minWidth: 120
      },
      {
        prop: 'paymentAmount',
        label: () => this.$t('cusEntry.reportManagement.depositPayAmount'),
        minWidth: 120
      },
      {
        prop: 'vendorBankName',
        label: () => this.$t('cusEntry.reportManagement.vendorBankName'),
        minWidth: 120
      },
      {
        prop: 'vendorBankNumber',
        label: () => this.$t('cusEntry.reportManagement.vendorBankNumber'),
        minWidth: 120
      },
      {
        prop: 'vendorBankAccount',
        label: () => this.$t('cusEntry.reportManagement.vendorBankAccount'),
        minWidth: 120
      },
      {
        prop: 'applyHandleType',
        label: () => this.$t('cusEntry.reportManagement.applyHandleType'),
        minWidth: 120,
        dataType: 'dict',
        code: 'SOU_APPLY_HANDLE_TYPE'
      },
      {
        prop: 'applyHandleReason',
        label: () => this.$t('cusEntry.reportManagement.applyHandleReason'),
        minWidth: 120
      },
      {
        prop: 'iVendorName',
        label: () => this.$t('cusEntry.reportManagement.iVendorName'),
        minWidth: 120
      },
      {
        prop: 'price',
        label: () => this.$t('cusEntry.reportManagement.price'),
        minWidth: 120
      },
      {
        prop: 'creationDate',
        label: () => this.$t('cusEntry.reportManagement.creationDate'),
        minWidth: 120
      },
      {
        prop: 'iStatus',
        label: () => this.$t('cusEntry.reportManagement.iStatus'),
        minWidth: 120
      },
      {
        prop: 'refundSupplierName',
        label: () => this.$t('cusEntry.reportManagement.depositRefundVendorName'),
        minWidth: 120
      },
      {
        prop: 'refundBankNumber',
        label: () => this.$t('cusEntry.reportManagement.refundBankNumber'),
        minWidth: 120
      },
      {
        prop: 'refundBankName',
        label: () => this.$t('cusEntry.reportManagement.refundBankName'),
        minWidth: 120
      },
      {
        prop: 'refundBankAccount',
        label: () => this.$t('cusEntry.reportManagement.refundBankAccount'),
        minWidth: 120
      },
      {
        prop: 'refundAmount',
        label: () => this.$t('cusEntry.reportManagement.depositRefundAmount'),
        minWidth: 120
      },
      {
        prop: 'refundTime',
        label: () => this.$t('cusEntry.reportManagement.refundTime'),
        minWidth: 120
      },
      {
        prop: 'refundReason',
        label: () => this.$t('cusEntry.reportManagement.refundReason'),
        minWidth: 120,
        dataType: 'dict',
        code: 'INT_DEPOSIT_REFUND_REASON'
      },
      {
        prop: 'refund',
        label: () => this.$t('cusEntry.reportManagement.ifRefund'),
        minWidth: 120,
        dataType: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'refundSuccess',
        label: () => this.$t('cusEntry.reportManagement.refundSuccess'),
        minWidth: 120,
        dataType: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'hStatus',
        label: () => this.$t('cusEntry.reportManagement.hStatus'),
        minWidth: 120,
        dataType: 'dict',
        code: 'YES_OR_NO'
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
