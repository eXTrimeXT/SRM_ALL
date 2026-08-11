<template>
  <el-container
    class="flex-container the_vendorPurchaseOrderList_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :select-dictionary="selectDictionary"
        :form-array="preArr"
        form-label-width="120px"
        @getFormData="getQuerydata"
      />

      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :row-index-fixed="false"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="true"
        :pre-query-data="queryParam"
        url="/api-sup-ce/po/deliveryNote/listPage"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import vendorDeliveryOrderDetail from './vendorDeliveryOrderDetail'
import { adaptDictData, parseTime } from '@/utils'
import { getAllLangList, getDictItemList } from '@/api/common'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import tagManage from 'mods@/orderManagementSupplier/views/vendorDeliveryOrderEngine/tagManage.vue'

export default {
  name: 'BuyerDeliveryOrderList',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  provide () {
    return { context: this }
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      selectDictionary: {},
      tableName: 'buyerDeliveryOrderList',
      defaultTableHeader: [],
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      currentRow: null,
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      statusList: [],
      form: {
        id: '',
        vendorCode: '',
        vendorCompanyName: '',
        reviewFormNumber: '',
        enabled: ''
      },
      rules: {
        vendorCode: [{ required: true, message: this.$t('bidMod.msgDictCode') }],
        vendorCompanyName: [{ required: true, message: this.$t('bidMod.msgDictName') }]
      },
      isModify: false,
      dialogFormVisible: false,
      formLabelWidth: '100px',
      preArr: [
        {
          prop: 'deliveryNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryNumber')
        },
        {
          prop: 'receivedFactory',
          label: () => this.$t('oneStopShopping.receiveAddress')
        },
        {
          prop: 'deliveryNoteStatus',
          label: () => this.$t('orderMod.buyerOrderSynergy.status'),
          type: 'dict',
          code: 'DELIVERY_NOTE_STATUS'
        },
        {
          prop: 'orgIds',
          label: () => this.$t('oneStopShopping.businessEntity'),
          type: 'OUorganizationSelector'
        },
        {
          prop: 'organizationIds',
          parentId: 'orgIds',
          label: () => this.$t('purchaseDemand.invOrg'),
          type: 'INVorganizationSelector'
        },
        {
          prop: 'startDeliveryDate',
          label: () => this.$t('orderMod.buyerOrderSynergy.startDeliveryDate'),
          type: 'date'
        },
        {
          prop: 'endDeliveryDate',
          label: () => this.$t('orderMod.buyerOrderSynergy.endDeliveryDate'),
          type: 'date'
        },
        {
          prop: 'vendorCode',
          label: () => this.$t('orderMod.buyerOrderSynergy.vendorName'),
          type: 'quicksearch',
          showKey: 'companyName',
          propKey: 'companyCode',
          name: 'scc_sup_company_info_display_buyer'
        },
        {
          prop: 'orderNumber',
          label: () => this.$t('purSettlementMod.orderNumber')
        }
      ],
      queryParam: {}
    }
  },
  updated () {
    this.defaultTableHeader = this.tableHeader
  },
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'deliveryNoteStatus',
        label: () => this.$t('orderMod.buyerOrderSynergy.status'),
        dataType: 'dict',
        code: 'DELIVERY_NOTE_STATUS'
      },
      {
        prop: 'deliveryNumber',
        showType: 'button',
        btnStyle: 'text',
        label: () => this.$t('orderMod.buyerOrderSynergy.deliveryNumber'),
        callback: (row) => this.updateDelivery(row)
      },
      {
        prop: 'deliveryDate',
        label: () => this.$t('orderMod.buyerOrderSynergy.deliveryDate2'),
        dataType: 'dateTime'
      },
      {
        prop: 'orgName',
        label: () => this.$t('bid_mod.businessEntity')
      },
      {
        prop: 'organizationName',
        label: () => this.$t('purchaseDemand.invOrg')
      },
      {
        prop: 'receivedFactory',
        label: () => this.$t('oneStopShopping.receiveAddress')
      },
      {
        prop: 'vendorName',
        label: () => this.$t('orderMod.buyerOrderSynergy.vendorName')
      },
      {
        prop: 'comments',
        label: () => this.$t('orderMod.buyerOrderSynergy.comments')
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'),
        minWidth: 120,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: function (row) {
              this.goTagManage(row)
            }.bind(this),
            formattor (val, row) {
              // 条码绑定
              return _this.$t('orderMod.tagManage')
            }
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    getQuerydata (v) {
      const { billDate, ...rest } = v || {}
      let params = { ...rest }
      if (billDate) {
        const [startSubmittedTime, endSubmittedTime] = billDate
        params = { ...rest, startSubmittedTime, endSubmittedTime }
      }
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    updateDelivery (row) {
      // 编辑tab
      let tab = {
        component: vendorDeliveryOrderDetail,
        params: { flag: 'view', row },
        title: this.$t('orderMod.buyerOrderSynergy.vendorDelivery') + row.deliveryNumber,
        name: 'buyerDeliveryOrderDetail' + row.deliveryNumber
      }
      this.$emit('tab-add', tab)
    },
    confirmDelivery () {
      let tab = {
        component: vendorDeliveryOrderDetail,
        params: { flag: 'add' },
        title: this.$t('orderMod.buyerOrderSynergy.vendorDelivery'),
        name: 'buyerDeliveryOrderDetail'
      }
      this.$emit('tab-add', tab)
    },
    goTagManage (row) {
      let params = {
        deliveryNumber: row.deliveryNumber,
        deliveryNoteId: row.deliveryNoteId
      }
      let name = params.deliveryNumber ?? ''
      let tab = {
        component: tagManage,
        params: {
          status: row.deliveryNoteStatus || 'CREATE',
          row: row || '',
          tabName: name ? 'tagManage' + name : 'tagManage'
        },
        title: this.$t('orderMod.buyerOrderSynergy.tagManage') + name,
        name: name ? 'tagManage' + name : 'tagManage'
      }
      this.$emit('tab-add', tab)
    }
  }
}
</script>
<style scoped lang="scss"></style>
