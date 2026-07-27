<template>
  <div>
    <FormWrapper
      ref="lineFormRef"
      :colLength="2"
      :form-array="queryForm"
      @getFormData="getQuerydata"
      @synchronous-value="syncFilterParams"
    />
    <TableView
      :ref="gridId"
      :table-data="tableData"
      :table-header="tableHeader"
      :page-size="pageSize"
      :pre-query-data="queryParam"
      :row-index="false"
      checkbox
      :check-change="checkChange"
      url="/api-sup-ce/po/deliveryNotice/searchOrderDetail"
    />
  </div>
</template>
<script>

import FormWrapper from 'lib@/components/Table/FormWrapper'
import TableView from 'lib@/components/Table/TableView'
import { parseTime } from '@/utils'
export default {
  name: 'PurchaseOrder',
  components: {
    FormWrapper,
    TableView
  },
  props: {
    init: {
      type: Boolean
    },
    queryData: {
      type: Object
    }
  },
  data () {
    return {
      selections: [],
      pageSize: 15,
      gridId: 'list',
      tableData: [],
      queryParam: {},
      queryForm: [
        {
          prop: 'orderNumbers',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderNumber'),
          type: 'inputMultiValue',
          icon: 'iconselect',
          multiTitle: this.$t('orderMod.buyerOrderSynergy.orderNumber'),
          method: (value, item, formData) => this.$set(formData, item.prop, value.split(','))
        },
        {
          prop: 'materialCodes',
          label: () => this.$t('materialMainData.materialCode'),
          type: 'inputMultiValue',
          icon: 'iconselect',
          multiTitle: this.$t('materialMainData.materialCode'),
          method: (value, item, formData) => this.$set(formData, item.prop, value.split(','))
        }
      ],
      tableHeader: [
        {
          prop: 'orderNumber',
          label: () =>
            this.$t('orderMod.buyerOrderSynergy.orderNumber') +
            '|' +
            this.$t('vendorMod.relegation.lineNumber'),
          minWidth: 150,
          formattor: (val, row) => {
            return val + '|' + row.orderDetailLineNum
          },
          callback: row => this.readOne(row)
        },
        {
          prop: 'materialCode',
          label: () => this.$t('orderMod.buyerOrderSynergy.materialCode'),
          minWidth: 120
        },
        {
          prop: 'materialName',
          label: () => this.$t('orderMod.buyerOrderSynergy.materialName'),
          minWidth: 120
        },
        {
          prop: 'unit',
          label: () => this.$t('orderMod.buyerOrderSynergy.unit'),
          minWidth: 120
        },
        {
          prop: 'categoryName',
          label: this.$t('common.category'), // 品类
          minWidth: 120
        },
        {
          prop: 'orderNum',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderNum'),
          minWidth: 120
        },
        {
          prop: 'remainingDeliveryNoticeQuantity',
          label: this.$t('buyerDeliveryNotice.remainingNoticeQuantity'), // 剩余可通知数量
          minWidth: 130
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
        }
      ]
    }
  },
  watch: {
    init (newValue) {
      if (newValue) {
        this.$refs['lineFormRef'].reset()
        this.getQuerydata()
      }
    }
  },

  methods: {
    getQuerydata (obj = {}) {
      this.queryParam = {
        ...obj,
        ...this.queryData
      }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    syncFilterParams (values) {
      this.queryParam = { ...values, ...this.queryData }
    },
    checkChange (val) {
      this.selections = val
      this.$emit('getSelections', this.selections)
    }
  }
}
</script>
