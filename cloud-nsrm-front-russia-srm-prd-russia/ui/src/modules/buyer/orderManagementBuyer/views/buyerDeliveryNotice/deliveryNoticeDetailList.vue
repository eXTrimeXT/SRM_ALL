<template>
  <el-container
    class="flex-container-notab the_buyerDeliveryNotice_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        @getFormData="getQuerydata"
      />

      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <ExportExcel
            :page-url="tableUrl"
            :filter-params="queryParam"
            :table-header="tableHeaderExport"
            :dict-codes="dictCodes"
            timeout="1000000"
            export-mode="front"
          />
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="15"
        :pre-query-data="queryParam"
        :url="tableUrl"
        :open-custom-table="true"
        :reserve-selection="true"
        row-key="deliveryNoticeDetailId"
        customTableKey="deliveryNoticeDetailList"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime } from '@/utils'
import ExportExcel from 'lib@/components/export-excel'
import deliveryNoticeDetail from './deliveryNoticeDetail'
import purchaseOrderDetail from 'modb@/orderManagementBuyer/views/buyerPurchaseOrder/purchaseOrderDetail'

export default {
  name: 'DeliveryNoticeDetailList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel,
    deliveryNoticeDetail,
    purchaseOrderDetail
  },
  data () {
    return {
      tableUrl: '/api-sup-ce/po/deliveryNotice/listPageDeliveryNoticeDetail',
      dictCodes: {
        status: 'DELIVERY_NOTICE_DETAIL_STATUS_NEW' // 行状态
      },
      gridId: 'list',
      currentRows: [],
      queryForm: [
        {
          prop: 'orgId',
          label: () => this.$t('dataConfMod.orgId'), // 业务实体
          type: 'OUorganizationSelector'
        },
        {
          prop: 'organizationId',
          label: () => this.$t('dataConfMod.organizationId'), // 库存组织
          type: 'INVorganizationSelector',
          parentId: 'orgId'
        },
        {
          prop: 'deliveryNoticeNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryNoticeNum')
        },
        {
          prop: 'vendorName',
          label: () => this.$t('orderMod.buyerOrderSynergy.vendorName'),
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_all'
        },
        {
          prop: 'materialId',
          label: () => this.$t('orderMod.buyerOrderSynergy.materialName'),
          type: 'quicksearch',
          showKey: 'materialName',
          propKey: 'materialId',
          name: 'scc_base_material_item'
        },
        {
          prop: 'orderNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderNumber')
        },
        {
          prop: 'status',
          label: () => this.$t('orderMod.rowStatus'),
          type: 'dict',
          code: 'DELIVERY_NOTICE_DETAIL_STATUS_NEW'
        },
        // 创建日期
        {
          prop: 'dateList',
          label: () => this.$t('quota.createdDate'),
          type: 'daterange'
        }
      ],
      tableHeaderExport: [],
      tableHeader: [
        {
          prop: 'deliveryNoticeNumber',
          showType: 'button',
          btnStyle: 'text',
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryNoticeNum') + '|' + this.$t('vendorMod.relegation.lineNumber'),
          minWidth: 170,
          formattor: (val, row) => val + '|' + row.lineNum,
          callback: row => this.readOne(row)
        },
        {
          prop: 'orgName',
          label: () => this.$t('oneStopShopping.businessEntity'),
          minWidth: 150
        },
        {
          prop: 'organizationName',
          label: () => this.$t('purchaseDemand.invOrg'),
          minWidth: 150
        },
        {
          label: () => this.$t('common.vendor'),
          prop: 'vendorName',
          minWidth: 150
        },
        {
          label: () => this.$t('orderMod.rowStatus'), // 行状态
          prop: 'status',
          minWidth: 120,
          dataType: 'dict',
          code: 'DELIVERY_NOTICE_DETAIL_STATUS_NEW'
        },
        {
          prop: 'orderNumber',
          showType: 'button',
          btnStyle: 'text',
          label: () =>
            this.$t('orderMod.buyerOrderSynergy.orderNumber') +
            '|' + this.$t('vendorMod.relegation.lineNumber'),
          minWidth: 170,
          formattor: (val, row) => {
            return val + '|' + row.orderDetailLineNum
          },
          callback: row => this.readOrder(row)
        },
        {
          prop: 'materialCode',
          label: () => this.$t('orderMod.buyerOrderSynergy.materialCode'),
          minWidth: 100
        },
        {
          prop: 'materialName',
          label: () => this.$t('orderMod.buyerOrderSynergy.materialName'),
          minWidth: 100
        },
        {
          prop: 'unitCode',
          label: () => this.$t('orderMod.buyerOrderSynergy.unit'),
          minWidth: 100,
          formatter: value => this.$getDictLabel('unit', value)
        },
        {
          prop: 'categoryName',
          label: this.$t('common.category'), // 品类
          minWidth: 120
        },
        {
          prop: 'orderNum',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderNum'), // 订单数量
          minWidth: 120
        },
        {
          prop: 'remainingDeliveryNoticeQuantity',
          label: this.$t('orderMod.noticeQuantity'), // 可通知数量
          desc: this.$t('orderMod.noticeQuantityDesc'), // 可通知数量=订单数量-累计通知数量
          minWidth: 150
        },
        {
          label: () => this.$t('orderMod.noticeSum'), // 本次通知数量
          prop: 'noticeSum',
          minWidth: 150
        },
        {
          prop: 'deliveryNoticeQuantity',
          label: this.$t('orderMod.buyerOrderSynergy.noticeSum'), // 累计通知数量
          minWidth: 120
        },
        {
          prop: 'deliveryQuantity',
          label: this.$t('orderMod.deliveryQuantityHeader'), // 已发货数量
          desc: this.$t('orderMod.deliveryQuantityHeaderDesc'), // 已发货数量=送货通知单创建送货单已发货数量
          minWidth: 150
        },
        {
          prop: 'warehouseQuantity',
          label: this.$t('orderMod.warehouseQuantity'), // 已入库数量
          desc: this.$t('orderMod.warehouseQuantityDesc'), // 已入库数量=送货通知单创建送货单已入库数量
          minWidth: 150
        },
        {
          prop: 'returnedQuantity',
          label: this.$t('orderMod.returnedQuantity'), // 已退货数量
          minWidth: 120
        },
        {
          label: () => this.$t('contractMod.deliveryDate1'), // 到货日期
          prop: 'receiveDate',
          minWidth: 100,
          formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
        },
        {
          label: () => this.$t('purchaseDemand.promiseReceiveDate'), // 供方承诺到货日期
          prop: 'promiseReceiveDate',
          minWidth: 150,
          formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
        },
        {
          label: () => this.$t('oneStopShopping.receiveContacts'), // 收货联系人
          prop: 'receiveContact',
          minWidth: 120
        },
        {
          label: () => this.$t('oneStopShopping.receiveTelephone'), // 收货联系电话
          prop: 'receiveTelephone',
          width: 120
        },
        {
          label: () => this.$t('oneStopShopping.receiveAddress'), // 收货地址
          prop: 'receiveAddress',
          minWidth: 120
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.createdBy'), // 创建人
          prop: 'createdUserName',
          minWidth: 100
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.creationDate'),
          prop: 'creationDate',
          minWidth: 120,
          formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
        },
        {
          label: () => this.$t('common.updatePeople'),
          prop: 'lastUpdatedUserName',
          minWidth: 100
        },
        {
          label: () => this.$t('common.lastUpdateDate'),
          prop: 'lastUpdateDate',
          minWidth: 120,
          formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
        },
        {
          label: () => this.$t('orderMod.confirmDate'),
          prop: 'confirmDate',
          width: 120,
          formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
        }
      ],
      queryParam: {}
    }
  },
  created () {
    this.setTableHeaderExort()
    this.getQuerydata()
  },
  methods: {
    // 设置下载表头字段，行号下载处理
    setTableHeaderExort () {
      this.tableHeaderExport = this.tableHeader.map(item => {
        if (item.prop === 'deliveryNoticeNumber') {
          return {
            prop: 'deliveryNoticeNumber',
            showType: 'button',
            btnStyle: 'text',
            label: () => this.$t('orderMod.buyerOrderSynergy.deliveryNoticeNum'),
            minWidth: 170
          }
        }
        if (item.prop === 'orderNumber') {
          return {
            prop: 'orderNumber',
            showType: 'button',
            btnStyle: 'text',
            label: () =>
              this.$t('orderMod.buyerOrderSynergy.orderNumber'),
            minWidth: 170
          }
        }
        return item
      })
      let exportTable = this.tableHeaderExport
      exportTable.forEach((item, i) => {
        if (item.prop === 'deliveryNoticeNumber') {
          this.tableHeaderExport.splice(i + 1, 0, {
            prop: 'lineNum',
            label: this.$t('orderMod.deliveryLineNum'),
            minWidth: 150
          })
        }
        if (item.prop === 'orderNumber') {
          this.tableHeaderExport.splice(i + 1, 0, {
            prop: 'orderDetailLineNum',
            label: this.$t('orderMod.buyerOrderSynergy.orderLineNum'),
            minWidth: 150
          })
        }
      })
    },
    getQuerydata (obj) {
      const { dateList, ...rest } = obj || this.queryParam
      const params = {}
      if (dateList) {
        params.startCreationDate = dateList[0]
        params.endCreationDate = dateList[1]
      }
      this.queryParam = { ...rest, ...params }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    // 查看送货通知单
    readOne (row) {
      // 查看--只读状态
      const tab = {
        component: deliveryNoticeDetail,
        params: {
          flag: 'readOnly',
          row
        },
        ctrlHeight: true,
        title: row.deliveryNoticeNumber,
        name: 'deliveryNoticeDetail' + row.deliveryNoticeNumber
      }
      this.$emit('tab-add', tab)
    },
    // 查看采购订单
    readOrder (row) {
      // 查看--只读状态
      const tab = {
        component: purchaseOrderDetail,
        params: {
          flag: 'approveNumber',
          row,
          showType: 'readOnly',
          activeWorkflowTab: true
        },
        title: row.orderNumber,
        name: 'purchaseOrderDetail' + row.orderNumber
      }
      this.$emit('tab-add', tab)
    }
  }
}
</script>
<style scoped lang="scss">
.the_buyerDeliveryNotice_wrapper {
}
</style>
