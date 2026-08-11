<template>
  <el-container
    class="flex-container the_buyerDeliveryOrderList_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
      />
      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <!-- <ExportExcel
            type="default"
            :page-url="tableUrl"
            :filter-params="filterParams"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            :title="'自定义列表导出'"
            timeout="1000000"
            export-mode="front"
          /> -->
          <ExportExcel
            type="default"
            :page-url="tableUrl"
            :filter-params="filterParams"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            :title="$t('cusEntry.supplement20250121.customListExport')"
            timeout="1000000"
            export-mode="front"
          />
          <el-button @click="exportHandle">
            <!-- 明细行导出 -->
            {{ $t("cusEntry.supplement20250121.detailRowExport") }}
          </el-button>
        </template>
      </MainHeader>

      <TableView
        :ref="gridId"
        :bigData="true"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :auto-query="false"
        :url="tableUrl"
        :open-custom-table="true"
        :reserve-selection="true"
        :adeptMeiQl="true"
        row-key="deliveryNoteId"
        customTableKey="buyerDeliveryOrderList"
        :comActive="$attrs['changeTab']"
        @afterQuery="afterQueryData"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import ExportExcel from 'lib@/components/export-excel'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime } from '@/utils'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import buyerDeliveryOrderDetail from './buyerDeliveryOrderDetail'
import { downloadFileLinkByPost } from 'lib@/utils/file'

export default {
  name: 'BuyerDeliveryOrderList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      gridId: 'list',
      pageSize: 15,
      filterParams: {},
      queryParam: {},
      tableHeader: [],
      tableData: [],
      tableUrl: '/api-sup-ce/api-ql/DeliveryNote/query',
      preArr: [
        {
          prop: 'deliveryNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryNumber')
        },
        {
          prop: 'deliveryDate',
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryDate2'),
          type: 'daterange'
        },
        {
          prop: 'deliveryNoteStatus',
          label: () => this.$t('orderMod.buyerOrderSynergy.status'),
          type: 'dict',
          code: 'DELIVERY_NOTE_STATUS'
        },
        {
          prop: 'orgId',
          label: this.$t('purchaseDemand.businessEntity'),
          type: 'OUorganizationSelector'
        },
        {
          prop: 'orderNumber',
          label: this.$t('cusEntry.orderMod.orderNumber')
        },
        {
          prop: 'vendorName',
          label: this.$t('orderMod.buyerOrderSynergy.vendorName')
        }
      ],
      dictCodes: {
        extStatus: 'DELIVERY_NOTE_STATUS',
        deliveryNoteStatus: 'DELIVERY_NOTE_STATUS'
      }
    }
  },

  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'deliveryNumber',
        label: () => this.$t('orderMod.buyerOrderSynergy.deliveryNumber'),
        width: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: (row) => this.readDelivery(row, 'view')
      },
      {
        prop: 'deliveryDate',
        label: () => this.$t('orderMod.buyerOrderSynergy.deliveryDate2'),
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        },
        width: 120
      },
      {
        prop: 'extStatus',
        label: () => this.$t('bidMod.billstatus'),
        width: 120,
        dataType: 'dict',
        code: 'DELIVERY_NOTE_STATUS'
      },
      {
        prop: 'extExpressType',
        label: () => this.$t('cusEntry.orderMod.extExpressType'),
        width: 120,
        dataType: 'dict',
        code: 'DELIVERY_WAY'
      },
      {
        prop: 'orgName',
        label: () => this.$t('oneStopShopping.businessEntity'),
        width: 150
      },
      {
        prop: 'organizationName',
        label: () => this.$t('bid_mod.inv'),
        width: 150
      },
      {
        prop: 'ceeaDeliveryPlace',
        label: () => this.$t('oneStopShopping.receiveAddress'),
        width: 150
      },
      {
        prop: 'vendorName',
        label: () => this.$t('common.vendorName'),
        width: 150
      },
      {
        prop: 'extPurchaserName',
        label: () => this.$t('orderMod.buyerOrderSynergy.buyerName'),
        width: 150
      },
      {
        prop: 'comments',
        label: () => this.$t('orderMod.buyerOrderSynergy.comments'),
        width: 120
      },
      {
        prop: 'createdFullName',
        label: () => this.$t('common.creator'),
        width: 120
      },
      {
        prop: 'creationDate',
        label: () => this.$t('orderMod.buyerOrderSynergy.creationDate'),
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        },
        width: 100
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    // 明细行导出
    exportHandle () {
      const param = this.queryParam.payload.filter
      const query = this.queryParam.query?.detailList || {}
      const data = {
        deliveryNumber: param?.deliveryNumber?.contains || '',
        beginDate: param?.deliveryDate?.between[0] || '',
        endDate: param?.deliveryDate?.between[1] || '',
        deliveryNoteStatus: param?.extStatus?.eq || '',
        orgId: param?.orgId?.eq || '',
        vendorName: param?.vendorName?.contains || '',
        extPurchaserNo: query?.orderDetailId?.orderId?.$condition?.filter?.orderNumber?.contains || ''
      }
      // downloadFileLinkByPost(
      //   '/api-sup-ce/deliveryNotes/getDeliveryNotesUpload',
      //   '送货单.xlsx',
      //   data
      // ).catch(() => {
      //   this.$message.error(this.$t('purchaseDemand.downloadFail'))
      // })
      downloadFileLinkByPost(
        '/api-sup-ce/deliveryNotes/getDeliveryNotesUpload',
        this.$t("cusEntry.supplement20250121.deliverySheetXlsx"),
        data
      ).catch(() => {
        this.$message.error(this.$t('purchaseDemand.downloadFail'))
      })
    },
    afterQueryData (tableData) {
      this.tableData = tableData.map(item => {
        // 送货单状态优先取【扩展状态：extStatus】
        item.deliveryNoteStatus = item.extStatus || item.deliveryNoteStatus
        return item
      })
    },
    getQuerydata (v) {
      // 已完成状态 使用 extStatus 查询, 其他状态使用 deliveryNoteStatus 查询(此时 extStatus为空)
      let params = {}
      let query = {
        '*': {}
      }
      const { deliveryNumber, deliveryDate, deliveryNoteStatus, orgId, extPurchaserName, vendorName, orderNumber } = v || {}
      if (deliveryNumber) {
        params.deliveryNumber = { contains: deliveryNumber }
      }
      if (vendorName) {
        params.vendorName = { contains: vendorName }
      }
      if (deliveryDate) {
        params.deliveryDate = { between: deliveryDate }
      }
      if (deliveryNoteStatus && ['FINISHED'].includes(deliveryNoteStatus)) {
        params.extStatus = { eq: deliveryNoteStatus }
      } else if (deliveryNoteStatus) {
        params.deliveryNoteStatus = { eq: deliveryNoteStatus }
        params.extStatus = { isNull: true }
      }
      if (orgId) {
        params.orgId = { eq: orgId }
      }
      if (extPurchaserName) {
        params.extPurchaserName = { contains: extPurchaserName }
      }
      if (orderNumber) {
        query.detailList = {
          '*': {},
          '$condition': {
            '$strictQuery': true
          },
          orderDetailId: {
            '*': {},
            '$condition': {
              '$strictQuery': true
            },
            orderId: {
              '*': {},
              '$condition': {
                '$strictQuery': true,
                filter: {
                  orderNumber: {
                    contains: orderNumber
                  }
                }
              }
            }
          }
        }
      }

      this.queryParam = {
        type: 'DeliveryNote',
        action: 'query',
        payload: {
          filter: { ...params },
          page: {
            pageNum: 1,
            pageSize: 15,
            sort: 'lastUpdateDate desc'
          }
        },
        query,
        lang: 'zh-cn',
        tree: true
      }
      this.filterParams = { meiqlPayload: this.queryParam }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    readDelivery (row, type) {
      // 只读模式
      let tab = {
        component: buyerDeliveryOrderDetail,
        params: { flag: type, row },
        title: this.$t('orderMod.buyerOrderSynergy.vendorDelivery') + row.deliveryNumber,
        name: 'buyerDeliveryOrderDetail' + row.deliveryNumber
      }
      this.$emit('tab-add', tab)
    }
  }
}
</script>
<style scoped lang="scss"></style>
