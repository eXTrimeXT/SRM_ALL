<template>
  <div class="order-line">
    <srm-dialog
      v-el-drag-dialog
      :title="$t('route.buyerPurchaseOrder')"
      size="large"
      :destroy-on-close="true"
      :visible.sync="visible"
      :close-on-click-modal="false"
      v-bind="$attrs"
      v-on="$listeners"
    >
      <FormWrapper
        ref="lineFormRef"
        :form-array="queryForm"
        @getFormData="getQuerydata"
      />
      <div class="btn_line">
        <el-button
          type="primary"
          @click="confirmAdd"
        >
          {{ $t('common.affirm') }}
        </el-button>
        <el-button
          type="primary"
          @click="visible = false"
        >
          {{ $t('common.cancel') }}
        </el-button>
      </div>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="15"
        :checkbox="true"
        :pre-query-data="queryParam"
        url="/api-sup-ce/po/deliveryNotice/searchOrderDetail"
        :reserve-selection="true"
        :row-key="rowKey"
      />
    </srm-dialog>
  </div>
</template>

<script>
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime } from '@/utils'
export default {
  name: 'PurchaseOrderLine',
  components: {
    TableView,
    FormWrapper
  },
  props: {
    form: {
      type: Object,
      default: () => {
        return {}
      }
    },
    visible: {
      type: Boolean,
      default: () => {
        return false
      }
    }
  },
  data () {
    return {
      gridId: 'list',
      currentSelect: [],
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
            this.$t('orderMod.buyerOrderSynergy.lineNum'),
          minWidth: 170,
          formattor: (val, row) => {
            return val + '|' + row.orderDetailLineNum
          },
          callback: row => this.readOne(row)
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
          prop: 'unit',
          label: () => this.$t('orderMod.buyerOrderSynergy.unit'),
          minWidth: 100
        },
        {
          prop: 'categoryName',
          label: this.$t('common.category'), // 品类
          minWidth: 120
        },
        {
          prop: 'orderNum',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderNum'),
          minWidth: 100
        },
        {
          prop: 'remainingDeliveryNoticeQuantity',
          label: this.$t('orderMod.surplusDeliveryQuantity'), // 剩余可通知送货数量
          minWidth: 170
        },
        {
          label: () => this.$t('purchaseDemand.promiseReceiveDate'), // 供方承诺到货日期
          prop: 'promiseReceiveDate',
          minWidth: 170,
          formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
        },
        {
          label: () => this.$t('oneStopShopping.receiveContacts'), // 收货联系人
          prop: 'receiveContact',
          minWidth: 150
        },
        {
          label: () => this.$t('oneStopShopping.receiveTelephone'), // 收货联系电话
          prop: 'receiveTelephone',
          width: 150
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
    visible (_oldValue, newValue) {
      // 每次打开重置搜索条件
      this.$nextTick(() => {
        if (!newValue) {
          this.$refs.lineFormRef.reset()
          this.getQuerydata()
        }
      })
    }
  },
  created () {

  },
  methods: {
    getQuerydata (obj = {}) {
      this.queryParam = {
        ...obj,
        orgId: this.form.orgId,
        organizationId: this.form.organizationId,
        vendorId: this.form.vendorId
      }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (select) {
      this.currentSelect = select
    },
    // 确认新增
    confirmAdd () {
      if (this.currentSelect.length < 1) return this.$message.warning(this.$t('orderMod.selectLeastData'))
      this.$emit('confirmAdd', this.currentSelect)
    },
    // tableView使用rowKey
    rowKey (row) {
      return row.orderNumber + '|' + row.orderDetailLineNum
    }
  }
}

</script>

<style lang="scss" scoped>
.btn_line {
  margin: 0 0 10px 0;
}
</style>
