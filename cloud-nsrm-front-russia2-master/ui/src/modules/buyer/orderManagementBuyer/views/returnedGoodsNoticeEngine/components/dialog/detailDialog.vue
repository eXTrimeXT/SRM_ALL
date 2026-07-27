<template>
  <div>
    <FormWrapper ref="lineFormRef" :colLength="2" :form-array="queryForm" @getFormData="getQuerydata" />
    <TableView
      :ref="gridId"
      :table-data="tableData"
      :table-header="tableHeader"
      :page-size="pageSize"
      :pre-query-data="queryParam"
      :row-index="false"
      checkbox
      :check-change="checkChange"
      url="/api-sup-ce/po/deliveryNoteDetail/listInReturnOrder"
    />
  </div>
</template>
<script>

import FormWrapper from 'lib@/components/Table/FormWrapper'
import TableView from 'lib@/components/Table/TableView'
import { parseTime } from '@/utils'
export default {
  name: 'OrderDetailDialog',
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
          prop: 'orgName',
          label: () => this.$t('purchaseDemand.businessEntity'),
          disabled: true
        },
        {
          prop: 'vendorName',
          label: () => this.$t('orderMod.buyerOrderSynergy.vendorName'),
          disabled: true
        },
        {
          prop: 'orderNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderNumber')
        },
        {
          prop: 'materialCode',
          label: () => this.$t('orderMod.buyerOrderSynergy.materialCode')
        },
        {
          prop: 'materialName',
          label: () => this.$t('orderMod.buyerOrderSynergy.materialName')
        },
        {
          prop: 'deliveryNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryNumber')
        }
      ],
      tableHeader: [
        {
          label: () =>
            this.$t('orderMod.buyerOrderSynergy.deliveryNumber') +
            '|' +
            this.$t('orderMod.buyerOrderSynergy.lineNum'),
          prop: 'deliveryNumber',
          width: 190,
          formattor: (val, row) => {
            return val + '|' + row.lineNum
          }
        },
        {
          prop: 'orgName',
          label: () => this.$t('purchaseDemand.businessEntity'),
          width: 150
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.vendorName'),
          prop: 'vendorName',
          width: 150
        },
        {
          label: () =>
            this.$t('orderMod.buyerOrderSynergy.orderNumber') +
            '|' +
            this.$t('orderMod.buyerOrderSynergy.orderLineNum'),
          prop: 'orderNumber',
          width: 210,
          formattor: (val, row) => {
            return val + '|' + row.orderLineNum
          }
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.materialCode'),
          prop: 'materialCode',
          width: 100
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.materialName'),
          prop: 'materialName',
          width: 100
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryQuantity'),
          prop: 'deliveryQuantity',
          width: 100
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.warehouseReceiptQuantity'), // 入库数量
          prop: 'warehouseQuantity',
          width: 100
        },
        {
          label: () => this.$t('orderMod.notReturnedNum'), // 可退货数量
          prop: 'notReturnedNum',
          width: 110
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryDate'),
          prop: 'deliveryDate',
          width: 100,
          dataType: 'dateTime'
        }
      ]
    }
  },
  watch: {
    init (newValue, oldValue) {
      if (newValue) {
        this.$refs['lineFormRef'].reset()
        this.getQuerydata()
      }
    }
  },

  methods: {
    getQuerydata (obj = {}) {
      this.$refs['lineFormRef'].setValue('orgName', this.queryData.orgName)
      this.$refs['lineFormRef'].setValue('vendorName', this.queryData.vendorName)
      this.queryParam = {
        ...obj,
        ...this.queryData
      }
      delete this.queryParam.orgName
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    checkChange (val) {
      this.selections = val
      this.$emit('getSelections', this.selections)
    }
  }
}
</script>
