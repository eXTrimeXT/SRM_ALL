<template>
  <div>
    <FormWrapper
      ref="lineFormRef"
      :form-array="queryForm"
      :colLength="2"
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
      url="/api-sup-ce/po/deliveryNoteDetail/listInWarehouseReceipt"
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
          prop: 'deliveryNoticeNum',
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryNumber')
        }
      ],
      tableHeader: [
        {
          label: () =>
            this.$t('orderMod.buyerOrderSynergy.deliveryNumber') +
            '|' +
            this.$t('purchaseDemand.lineNum'),
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
            this.$t('purchaseDemand.lineNum'),
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
          label: () => this.$t('orderMod.abledWarehouseNum'),
          prop: 'notWarehouseQuantity',
          width: 120
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
        this.$refs['lineFormRef'].setValue('orgName', this.queryData.orgName)
        this.$refs['lineFormRef'].setValue('vendorName', this.queryData.vendorName)
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
      delete this.queryParam.orgName
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
