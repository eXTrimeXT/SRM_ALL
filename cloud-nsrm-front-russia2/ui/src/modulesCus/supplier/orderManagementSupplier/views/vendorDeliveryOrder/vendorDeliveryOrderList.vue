<template>
  <el-container class="flex-container the_vendorDeliveryOrderList_wrapper" direction="vertical">
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
      />
      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <!-- <AuthorityButton type="primary" code="sup:vendorDeliveryOrder:createdDelivery" @click="createdDelivery">
            {{ $t('orderMod.buyerOrderSynergy.createDelivery') }}
          </AuthorityButton> -->
          <!-- 导出 -->
          <ExportExcel
            type="default"
            :page-url="tableUrl"
            :filter-params="filterParams"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            timeout="1000000"
            export-mode="front"
          />
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
        customTableKey="vendorDeliveryOrderList"
        :comActive="$attrs['changeTab']"
        @afterQuery="afterQueryData"
      />
    </el-main>
    <!-- 取消发货弹框 -->
    <CancleDialog
      v-if="canclDialogVisible"
      :visible.sync="canclDialogVisible"
      :current-row="currentRow"
      @after-cancle="getQuerydata"
    />
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import ExportExcel from 'lib@/components/export-excel'
import vendorDeliveryOrderDetail from './vendorDeliveryOrderDetail'
import { parseTime } from '@/utils'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { transformMQL } from 'lib@/utils/util'
import CancleDialog from './cancleDialog'

export default {
  name: 'VendorDeliveryOrderList',
  components: {
    TableView,
    MainHeader,
    ExportExcel,
    FormWrapper,
    CancleDialog
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      gridId: 'list',
      pageSize: 15,
      filterParams: {},
      queryParam: {},
      tableHeader: [],
      tableData: [],
      tableUrl: '/api-sup-ce/api-ql/DeliveryNoteVendor/query',
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
          prop: 'orgName',
          label: this.$t('purchaseDemand.businessEntity')
        },
        {
          prop: 'extPurchaserName',
          label: this.$t('orderMod.buyerOrderSynergy.buyerName')
        },
        {
          prop: 'purchaseOrderCode',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderNumber')
        },
        {
          prop: 'materialCode',
          label: () => this.$t('common.materialCode')
        },
        {
          prop: 'materialName',
          label: () => this.$t('common.materialName')
        },
        {
          prop: 'specification',
          label: () => this.$t('cusEntry.orderMod.specification')
        },
        {
          prop: 'extBrand',
          label: () => this.$t('dataConfMod.band')
        },
      ],
      dictCodes: {
        extStatus: 'DELIVERY_NOTE_STATUS',
        deliveryNoteStatus: 'DELIVERY_NOTE_STATUS'
      },
      currentRow: {},
      canclDialogVisible: false
    }
  },
  watch: {
    $route: {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler () {
        if (this.$route.params.from === 'vendorPurchaseOrder') {
          let row = this.$route.params.row
          this.readDelivery(row, 'edit')
        }
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
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'),
        width: 150,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: row => this.readDelivery(row, 'edit'),
            code: 'sup:vendorDeliveryOrder:updateDelivery',
            formattor (val) {
              return _this.$t('common.edit')
            },
            show: row => row.deliveryNoteStatus === 'CREATE'
          },
          {
            callback: function (row) {
              this.deleteDelivery(row)
            }.bind(this),
            code: 'sup:vendorDeliveryOrder:deleteDelivery',
            formattor (val) {
              return _this.$t('common.delete')
            },
            show: row => row.deliveryNoteStatus === 'CREATE'
          },
          {
            callback: row => this.readDelivery(row, 'manage'),
            code: 'sup:vendorDeliveryOrder:updateDelivery',
            formattor (val) {
              return _this.$t('purchaseDemand.manage')
            },
            show: row => row.deliveryNoteStatus === 'DELIVERED'
          },
          {
            callback: function (row) {
              this.cancelDelivery(row)
            }.bind(this),
            code: 'sup:vendorDeliveryOrder:cancelDelivery',
            formattor (val) {
              return _this.$t('orderMod.cancelDelivery')
            },
            show: row => row.deliveryNoteStatus === 'DELIVERED'
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
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
      let relation = {}
      params.vendorId = { eq: this.$store.getters.user.companyId }
      const {
        deliveryNumber,
        deliveryDate,
        deliveryNoteStatus,
        orgName,
        extPurchaserName,
        purchaseOrderCode,
        materialCode,
        materialName,
        specification,
        extBrand
      } = v || {}
      if (deliveryNumber) {
        params.deliveryNumber = { contains: deliveryNumber }
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
      if (orgName) {
        params.orgName = { contains: orgName }
      }
      if (extPurchaserName) {
        params.extPurchaserName = { contains: extPurchaserName }
      }
      if (purchaseOrderCode) {
        params.purchaseOrderCode = purchaseOrderCode
      }
      if (materialCode || materialName || extBrand || specification) {
        relation.detailList = {
          '$condition': {
            '$strictQuery': true
          },
          orderDetailId: {
            '$condition': {
              '$strictQuery': true,
              filter: {}
            }
          }
        }
        const queryObj = { materialCode, materialName, extBrand, specification }
        Object.keys(queryObj).filter(key => queryObj[key]).forEach(item => {
          relation.detailList.orderDetailId['$condition'].filter[item] = {
            contains: queryObj[item]
          }
        })
      }
      this.queryParam = {
        type: 'DeliveryNoteVendor',
        action: 'query',
        payload: {
          filter: { ...params },
          page: {
            pageNum: 1,
            pageSize: 15,
            sort: 'lastUpdateDate desc'
          }
        },
        query: { '*': {}, ...relation },
        lang: 'zh-cn',
        tree: true
      }
      this.filterParams = { meiqlPayload: this.queryParam }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    async deleteDelivery (row) {
      const sign = await this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
      if (sign !== 'confirm') return
      const saveData = transformMQL.save(
        'DeliveryNoteVendor',
        [{
          'deliveryNoteId': row.deliveryNoteId,
          'detailList': [{ $delete: '*' }],
          'fileUploads': [{ $delete: '*' }]
        }],
        'delete'
      )
      this.$http({
        url: '/api-sup-ce/api-ql/DeliveryNoteVendor/delete',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    // 取消送货
    async cancelDelivery (row) {
      this.currentRow = row
      this.canclDialogVisible = true
    },
    readDelivery (row, type) {
      // 只读模式
      let tab = {
        component: vendorDeliveryOrderDetail,
        params: { flag: type, row },
        ctrlHeight: true,
        title: this.$t('orderMod.buyerOrderSynergy.vendorDelivery') + row.deliveryNumber,
        name: 'vendorDeliveryOrderDetail' + row.deliveryNumber
      }
      this.$emit('tab-add', tab)
    },
    // 创建送货单
    createdDelivery () {
      let tab = {
        component: vendorDeliveryOrderDetail,
        params: { flag: 'add' },
        ctrlHeight: true,
        title: this.$t('orderMod.buyerOrderSynergy.vendorDelivery'),
        name: 'vendorDeliveryOrderDetail'
      }
      this.$emit('tab-add', tab)
    }
  }
}
</script>
<style scoped lang="scss"></style>
