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
      url="/api-sup-ce/order/orderDetail/listMaterialPage"
    />
  </div>
</template>
<script>

import FormWrapper from 'lib@/components/Table/FormWrapper'
import TableView from 'lib@/components/Table/TableView'
import { parseTime } from '@/utils'
export default {
  name: 'DeliveryNoticeDialog',
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
          prop: 'orderNumber',
          label: () => this.$t('purSettlementMod.orderNumber')
        },
        {
          prop: 'materialCode',
          label: () => this.$t('common.materialCode')
        }
      ],
      tableHeader: [
        {
          prop: 'orderNumber',
          label: () => this.$t('purSettlementMod.orderNumber'),
          width: 150
        },
        {
          prop: 'lineNum',
          label: () => this.$t('orderMod.orderLineNum'),
          width: 150
        },
        {
          prop: 'categoryName',
          label: () => this.$t('purchaseDemand.materialCateSub'),
          width: 150
        },
        {
          prop: 'materialCode',
          label: () => this.$t('common.materialCode'),
          width: 150
        },
        {
          prop: 'materialName',
          label: () => this.$t('common.materialName'),
          width: 150
        },
        {
          prop: 'unit',
          label: () => this.$t('orderMod.buyerOrderSynergy.unit'),
          width: 150
        },
        {
          prop: 'orderNum',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderNum'),
          width: 150
        },
        {
          prop: 'numberRemaining',
          label: () => this.$t('orderMod.remainUndeliveryQuantity'),
          width: 150
        },
        {
          prop: 'ceeaPlanReceiveDate',
          label: () => this.$t('orderMod.buyerOrderSynergy.requirementDateStr'),
          width: 150,
          formattor (val) {
            return val ? parseTime(val, '{y}-{m}-{d}') : ''
          }
        },
        {
          prop: 'ceeaPromiseReceiveDate',
          label: () => this.$t('purchaseDemand.promiseReceiveDate'),
          width: 150,
          formattor (val) {
            return val ? parseTime(val, '{y}-{m}-{d}') : ''
          }
        },
        {
          prop: 'comments',
          label: () => this.$t('purchaseDemand.comments'),
          width: 150
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
