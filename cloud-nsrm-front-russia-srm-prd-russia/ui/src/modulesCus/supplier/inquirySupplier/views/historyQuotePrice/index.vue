<template>
  <el-container class="flex-container">
    <el-main>
      <FormWrapper :form-array="preArr" @getFormData="getQueryData" />
      <MainHeader>
        <template slot="left">
          <ExportExcel
            :page-url="url"
            export-mode="front"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            :filter-params="queryParams"
            :title="$t('components.eio.customExport')"
            code="inq:history:export"
            :fileName="$t('cusEntry.inq.historyPriceExport')"
            type="default"
          />
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParams"
        :com-active="$attrs['changeTab']"
        :auto-query="true"
        open-custom-table
        :url="url"
      />
    </el-main>
  </el-container>
</template>

<script>
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import ExportExcel from 'lib@/components/export-excel'
export default {
  name: 'HistoryQuotePrice',
  components: {
    TableView,
    FormWrapper,
    MainHeader,
    ExportExcel
  },
  data () {
    return {
      preArr: [],
      tableHeader: [],
      pageSize: 15,
      gridId: 'historyList',
      queryParams: {},
      dictCodes: {
        orderStatus: 'INQ_SOU_ORDER_STATUS',
        unit: 'unit',
        taxKey: 'tax'
      }
    }
  },
  created () {
    const userType = this.$store.getters.userType
    this.url = userType === 'VENDOR' ? '/api-sou/npm/vendor/inq/order/listVendorOrderHis' : '/api-sou/npm/buyer/inq/order/listVendorOrderHis'
    this.preArr = [
      {
        prop: 'souNo',
        label: this.$t('bidMod.inquiryNo')
      },
      {
        prop: 'itemCode',
        label: this.$t('bidMod.itemCode')
      },
      {
        prop: 'itemDesc',
        label: this.$t('bidMod.itemDesc')
      },
      {
        prop: 'vendorName',
        label: this.$t('bidMod.vendorName'),
        hidden: () => {
          return userType === 'VENDOR'
        }
      },
      {
        prop: 'extBrand',
        label: this.$t('cusEntry.inq.brand'),
        hidden: () => {
          return userType === 'VENDOR'
        }
      }
    ]
    this.tableHeader = [
      {
        prop: 'souNo',
        label: this.$t('bidMod.inquiryNo'),
        minWidth: 120
      },
      {
        prop: 'orderStartTime',
        label: this.$t('cusEntry.bidMod.supplierReceiptDate'),
        minWidth: 140,
        hidden: userType === 'VENDOR'
      },
      {
        prop: 'orderEndTime',
        label: this.$t('bidMod.quotedeadline'),
        minWidth: 120
      },
      {
        prop: 'orderStatus',
        label: this.$t('bidMod.quoteStatus'),
        minWidth: 120,
        dataType: 'dict',
        code: 'INQ_SOU_ORDER_STATUS'
      },
      {
        prop: 'orderNo',
        label: this.$t('bidMod.quoteNo'),
        minWidth: 150
      },
      {
        prop: 'orgOuName',
        label: this.$t('bid_mod.businessEntity'),
        minWidth: 120
      },
      {
        prop: 'itemCode',
        label: this.$t('bidMod.itemCode'),
        minWidth: 120
      },
      {
        prop: 'itemDesc',
        label: this.$t('bidMod.itemName'),
        minWidth: 120
      },
      {
        prop: 'categoryName',
        label: this.$t('bidMod.categoryName'),
        minWidth: 120
      },
      {
        prop: 'extMaterialModel',
        label: this.$t('cusEntry.bidMod.specification'),
        minWidth: 120
      },
      {
        prop: 'requireQuantity',
        label: this.$t('cusEntry.inq.quantity'),
        minWidth: 120
      },
      {
        prop: 'unit',
        label: this.$t('cusEntry.inq.baseMeasurmentUnit'),
        minWidth: 120,
        dataType: 'dict',
        code: 'unit'
      },
      {
        prop: 'extBrand',
        label: this.$t('cusEntry.inq.brand'),
        minWidth: 120
      },
      {
        prop: 'vendorName',
        label: this.$t('bidMod.vendorName'),
        minWidth: 120
      },
      {
        prop: 'vendorCode',
        label: this.$t('bidMod.vendorCode'),
        minWidth: 120
      },
      {
        prop: 'taxKey',
        label: this.$t('bidMod.taxRate2'),
        minWidth: 120,
        dataType: 'dict',
        code: 'tax'
      },
      {
        prop: 'orderNotaxPrice',
        label: this.$t('bidMod.quotenotaxPrice2'),
        minWidth: 120
      },
      {
        prop: 'orderTaxPrice',
        label: this.$t('bidMod.quotetaxPrice2'),
        minWidth: 120
      },
      {
        prop: 'priceTaxTotal',
        label: this.$t('cusEntry.bidMod.taxAmount'),
        minWidth: 120
      },
      {
        prop: 'extWarrantyPeriod',
        label: this.$t('cusEntry.bidMod.warrantyPeriod'),
        minWidth: 150
      },
      {
        prop: 'extLeadTime',
        label: this.$t('cusEntry.bidMod.deliveryCycle'),
        minWidth: 150
      },
      {
        prop: 'advancePaymentRemark',
        label: this.$t('cusEntry.bidMod.advancePayment'),
        minWidth: 120,
        dataType: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'priceActiveDay',
        label: this.$t('cusEntry.bidMod.quoteEffictDate'),
        minWidth: 150
      },
      {
        prop: 'extOrderByNickname',
        label: this.$t('cusEntry.bidMod.quoter'),
        minWidth: 120
      },
      {
        prop: 'extOrderPhone',
        label: this.$t('cusEntry.bidMod.quotePhone'),
        minWidth: 120
      },
      {
        prop: 'submitByIp',
        label: this.$t('cusEntry.bidMod.ipAddress'),
        minWidth: 150,
        hidden: userType === 'VENDOR'
      }
    ]
  },
  methods: {
    /* 查询 */
    getQueryData (params) {
      this.queryParams = params
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    }
  }
}
</script>
