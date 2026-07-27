<template>
  <el-container
    class="flex-container the_returnedGoodsNoticeList_vendor_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        form-label-width="120px"
        @getFormData="getQuerydata"
      />

      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <el-button
            type="primary"
            :disabled="!currentRows.length"
            @click="handleAccept('mutil')"
          >
            {{ $t('orderMod.accept') }}
          </el-button>
          <el-button
            :disabled="!currentRows.length"
            @click="handleRefuse('mutil')"
          >
            {{ $t('common.refused') }}
          </el-button>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="true"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-sup-ce/order/returnOrder/listPage"
        :reserve-selection="true"
        row-key="returnOrderId"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import returnedGoodsNoticeDetail from './returnedGoodsNoticeDetail'
import { parseTime, adaptDictData } from '@/utils'
import { getDictItemList } from '@/api/common'

export default {
  name: 'ReturnedGoodsNoticeList',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  provide () {
    return { context: this }
  },
  data () {
    return {
      gridId: 'list',
      pageSize: 15,
      currentRows: [],
      tableHeader: [
        {
          prop: 'returnOrderNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.returnOrderNumber'),
          showType: 'button',
          btnStyle: 'text',
          callback: (row) => this.handleView(row),
          minWidth: 130
        },
        {
          prop: 'vendorName',
          label: () => this.$t('orderMod.buyerOrderSynergy.vendorName'),
          width: 150
        },
        {
          prop: 'vendorCode',
          label: () => this.$t('orderMod.buyerOrderSynergy.vendorCode'),
          width: 150
        },
        {
          prop: 'organizationName',
          label: () => this.$t('bid_mod.businessEntity'),
          width: 150
        },
        {
          prop: 'returnStatus',
          width: 100,
          label: () => this.$t('orderMod.buyerOrderSynergy.status'),
          dataType: 'dict',
          code: 'RETURN_ORDER_STATUS'
        },
        { label: () => this.$t('common.remark'), prop: 'comments', width: 100 },
        {
          label: () => this.$t('oneStopShopping.refusedReason'),
          prop: 'rejectReason',
          width: 100
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.createdBy'),
          prop: 'createdUserName', // createdBy
          width: 100
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.creationDate'),
          prop: 'creationDate',
          width: 100,
          formattor: (val) => (val ? parseTime(val, '{y}-{m}-{d}') : '')
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.lastUpdateBy'),
          prop: 'lastUpdatedUserName', // lastUpdatedBy
          width: 120
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.lastUpdateDate'),
          prop: 'lastUpdateDate',
          width: 120,
          formattor: (val) => (val ? parseTime(val, '{y}-{m}-{d}') : '')
        },
        {
          prop: 'operation',
          label: () => this.$t('common.operation'),
          width: 100,
          btnStyle: 'text',
          fixed: 'right',
          showType: 'buttons',
          buttons: [
            {
              callback: (row) => this.handleAccept('one', [row]),
              formattor: () => this.$t('orderMod.accept'),
              show: (row) => row.returnStatus === 'WAITING_CONFIRM'
            },
            {
              callback: (row) => this.handleRefuse('one', [row]),
              formattor: () => this.$t('common.refused'),
              show: (row) => row.returnStatus === 'WAITING_CONFIRM'
            }
          ]
        }
      ],
      preArr: [
        {
          prop: 'organizationId',
          label: () => this.$t('oneStopShopping.businessEntity'),
          type: 'OUorganizationSelector'
        },
        {
          prop: 'vendorName',
          label: () => this.$t('orderMod.buyerOrderSynergy.vendorName'),
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_display'
        },
        {
          prop: 'returnOrderNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.returnOrderNumber')
        },
        {
          prop: 'materialCode',
          label: () => this.$t('orderMod.buyerOrderSynergy.materialName'),
          type: 'quicksearch',
          showKey: 'materialName',
          propKey: 'materialCode',
          name: 'scc_base_material_item'
        },
        {
          prop: 'orderNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderNumber')
        },
        {
          prop: 'deliveryNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryNumber')
        },
        {
          prop: 'returnStatus',
          label: () => this.$t('orderMod.buyerOrderSynergy.returnStatus'),
          type: 'dict',
          code: 'RETURN_ORDER_STATUS',
          filterItem: () => ['DRAFT']
        }
      ],
      queryParam: {}
    }
  },
  created () {},
  mounted () {
    this.getQuerydata()
  },
  methods: {
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    // 查看退货单详情
    handleView (row) {
      const tab = {
        component: returnedGoodsNoticeDetail,
        params: { flag: 'view', row: row },
        title: this.$t('route.returnGoodsBill') + row.returnOrderNumber,
        name: 'returnedGoodsNoticeDetail' + row.returnOrderId
      }
      this.$emit('tab-add', tab)
    },
    handleAccept (type, rows) {
      let list = type === 'mutil' ? this.currentRows : rows
      let count = 0
      list.forEach((item) => {
        if (item.returnStatus != 'WAITING_CONFIRM') {
          count++
        }
      })
      this.$nextTick(() => {
        if (count > 0) {
          this.$message({
            type: 'warning',
            message: this.$t('purchaseDemand.have') + count + this.$t('orderMod.msgVendorOrder[17]')
          })
        } else {
          const selectData = list.map((i) => i.returnOrderId)
          this.$http({
            url: '/api-sup-ce/order/returnOrder/batchConfirm',
            method: 'POST',
            data: selectData,
            loading: true
          })
            .then((data) => {
              this.$message.success(this.$t('common.success'))
              this.getQuerydata()
            })
            .catch((err) => {})
        }
      })
    },
    handleRefuse (type, rows) {
      let list = type === 'mutil' ? this.currentRows : rows
      let count = 0
      list.forEach((item) => {
        if (item.returnStatus != 'WAITING_CONFIRM') {
          count++
        }
      })
      this.$nextTick(() => {
        if (count > 0) {
          this.$message({
            type: 'warning',
            message: this.$t('purchaseDemand.have') + count + this.$t('orderMod.msgVendorOrder[18]')
          })
        } else {
          this.$prompt(this.$t('orderMod.msgRufuseReason'), this.$t('common.tips'), {
            confirmButtonText: this.$t('common.confirm'),
            cancelButtonText: this.$t('common.cancel'),
            inputPattern: /\S{1,}/,
            inputErrorMessage: this.$t('orderMod.refuseReasonRequire')
          })
            .then(({ value }) => {
              let selectData = list.map((i) => i.returnOrderId)
              this.$http({
                url: '/api-sup-ce/order/returnOrder/batchReject',
                method: 'POST',
                data: {
                  ids: selectData,
                  rejectReason: value
                }
              }).then((res) => {
                this.$message.success(this.$t('common.success'))
                this.getQuerydata()
              })
            })
            .catch(() => {
              this.$message({
                type: 'info',
                message: this.$t('orderMod.cancelRefuse')
              })
            })
        }
      })
    }
  }
}
</script>
<style scoped lang="scss"></style>
