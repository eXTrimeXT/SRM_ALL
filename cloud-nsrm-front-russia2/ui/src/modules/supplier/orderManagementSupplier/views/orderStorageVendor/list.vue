<template>
  <el-container
    class="flex-container the_vendorPurchaseOrderList_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
      />

      <MainHeader
        :l-span="22"
        :r-span="2"
      />
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :auto-query="false"
        :source="orderStorageApi.listPageVendor"
        :open-custom-table="true"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime, adaptDictData } from '@/utils'
import { getDictItemList } from '@/api/common'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { orderStorageApi } from 'mods@/orderManagementSupplier/api'

export default {
  name: 'OrderStorageListBuyer',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      orderStorageApi: orderStorageApi,
      currentRows: [],
      canOperate: false,
      tableUrl: '/api-sup-ce/po/order/listPage',
      tableName: 'orderStorageList',
      defaultTableHeader: [],
      pageSize: 15,
      gridId: 'list',
      formLabelWidth: '100px',
      preArr: [
        {
          prop: 'vendorName',
          label: () => this.$t('orderMod.buyerOrderSynergy.vendorName'),
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_display'
        },
        {
          prop: 'receivedFactory',
          label: () => this.$t('orderMod.buyerOrderSynergy.receivedFactory')
        },
        {
          prop: 'deliveryNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryNumber')
        },
        {
          prop: 'warehouseReceiptNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.warehouseReceiptNumber')
        },
        {
          prop: 'warehouseReceiptStatus',
          label: () => this.$t('orderMod.buyerOrderSynergy.warehouseReceiptStatus'),
          type: 'dict',
          code: 'RECEIVED_STATUS'
        },
        {
          prop: 'organizationId',
          label: () => this.$t('orderMod.buyerOrderSynergy.organizationName'),
          type: 'OUorganizationSelector'
        },
        {
          prop: 'materialCode',
          label: () => this.$t('orderMod.buyerOrderSynergy.materialCode'),
          type: 'quicksearch',
          showKey: 'materialCode',
          name: 'scc_base_material_item'
        }
      ],
      queryParam: {},
      tableHeader: [
        {
          prop: 'warehouseReceiptStatus',
          label: () => this.$t('orderMod.buyerOrderSynergy.status'),
          width: 100,
          dataType: 'dict',
          code: 'RECEIVED_STATUS'
        },
        // { prop: "warehouseReceiptId", label: "入库单ID", width: 150 },
        {
          prop: 'categoryName',
          label: () => this.$t('orderMod.buyerOrderSynergy.categoryName'),
          width: 100
        },
        {
          prop: 'materialCode',
          label: () => this.$t('orderMod.buyerOrderSynergy.materialCode'),
          width: 100
        },
        {
          prop: 'deliveryQuantity',
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryQuantity'),
          width: 100
        },
        {
          prop: 'warehouseReceiptQuantity',
          label: () => this.$t('orderMod.buyerOrderSynergy.warehouseReceiptQuantity'),
          width: 100
        },
        {
          prop: 'warehouseReceiptNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.warehouseReceiptNumber'),
          width: 150
        },
        {
          prop: 'warehouseReceiptRowNum',
          label: () => this.$t('orderMod.buyerOrderSynergy.warehouseReceiptRowNum'),
          width: 120
        },
        {
          prop: 'organizationName',
          label: () => this.$t('orderMod.buyerOrderSynergy.organizationName'),
          width: 100
        },
        {
          prop: 'orderNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderNumber'),
          width: 150
        },
        {
          prop: 'vendorCode',
          label: () => this.$t('orderMod.buyerOrderSynergy.vendorCode'),
          width: 120
        },
        {
          prop: 'vendorName',
          label: () => this.$t('orderMod.buyerOrderSynergy.vendorName'),
          width: 120
        },
        {
          prop: 'deliveryNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryNumber'),
          width: 150
        },
        // { prop: "contactNumber", label: "联系方式", width: 100 },
        {
          prop: 'receivedFactory',
          label: () => this.$t('orderMod.buyerOrderSynergy.receivedFactory'),
          width: 100
        },
        {
          prop: 'inventoryPlace',
          label: () => this.$t('orderMod.buyerOrderSynergy.inventoryPlace'),
          width: 100
        },
        // { prop: "returnOrderNumber", label: "退货单号", width: 100 },
        {
          prop: 'deliveryDate',
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryDate'),
          width: 100,
          formattor: (val) => (val ? parseTime(val, '{y}-{m}-{d}') : '')
        },
        {
          prop: 'confirmTime',
          label: () => this.$t('orderMod.buyerOrderSynergy.confirmTime'),
          width: 100,
          formattor: (val) => (val ? parseTime(val, '{y}-{m}-{d}') : '')
        }
      ]
    }
  },
  updated () {
    this.defaultTableHeader = this.tableHeader
  },
  created () {
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  mounted () {},
  methods: {
    handleCurrentChange (val) {
      this.currentRows = val
    },
    getQuerydata (v) {
      this.queryParam = {
        ...v,
        vendorId: this.$store.getters.userInfo.companyId
      }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    }
  }
}
</script>
<style scoped lang="scss">
.the_vendorPurchaseOrderList_wrapper {
  .order-uploader {
    display: inline-block;
    margin: 0 10px;
  }
}
</style>
