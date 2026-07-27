<template>
  <el-container
    class="flex-container-notab the_buyerDeliveryNotice_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="queryForm"
        form-label-width="120px"
        @getFormData="getQuerydata"
      />

      <main-header
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <el-button
            type="primary"
            :disabled="!currentRows.length"
            @click="acceptNotice('mutil')"
          >
            {{ $t('orderMod.accept') }}
          </el-button>
          <el-button
            type="primary"
            :disabled="!currentRows.length"
            @click="refuseNotice('mutil')"
          >
            {{ $t('common.refused') }}
          </el-button>
          <!-- <el-button
            type="primary"
            @click="exportExcel"
          >Excel导出</el-button> -->
        </template>
      </main-header>
      <table-view
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="true"
        :pre-query-data="queryParam"
        url="/api-sup-ce/order/deliveryNotice/listPage"
        :open-custom-table="true"
        :reserve-selection="true"
        row-key="deliveryNoticeId"
      >
        <template #receiptPlace="{ scope }">
          <render-async-text :cell-value="scope.row.receiptPlace" />
        </template>
      </table-view>
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import RenderAsyncText from '@/library/components/provice-city/renderAsyncText'
import { parseTime, adaptDictData } from '@/utils'
import { getDictItemList } from '@/api/common'

export default {
  name: 'VendorDeliveryNotice',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    RenderAsyncText
  },
  provide () {
    return { context: this }
  },
  data () {
    return {
      gridId: 'list',
      pageSize: 15,
      currentRows: [],
      queryForm: [
        {
          prop: 'orgId',
          label: () => this.$t('oneStopShopping.businessEntity'),
          type: 'OUorganizationSelector'
        },
        {
          prop: 'organizationId',
          label: () => this.$t('dataConfMod.organizationId'), // 库存组织
          type: 'INVorganizationSelector',
          parentId: 'orgId'
        },
        {
          prop: 'deliveryNoticeNum',
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryNoticeNum')
        },
        {
          prop: 'orderNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderNumber')
        },
        {
          prop: 'materialCode',
          label: () => this.$t('common.materialCode'), // 物料编码
          type: 'quicksearch',
          showKey: 'materialCode',
          name: 'scc_base_material_item'
        },
        {
          prop: 'deliveryNoticeStatus',
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryNoticeStatus'),
          type: 'dict',
          code: 'DELIVERY_NOTICE_STATUS'
        },
        // 创建日期
        {
          prop: 'dateList',
          label: () => this.$t('quota.createdDate'),
          type: 'daterange'
        }
      ],
      tableHeader: [
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryNoticeNum'),
          prop: 'deliveryNoticeNum',
          width: 150
        },
        {
          prop: 'ceeaOrgName',
          label: () => this.$t('oneStopShopping.businessEntity'),
          width: 150
        },
        {
          prop: 'organizationName',
          label: () => this.$t('oneStopShopping.businessEntity'),
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
            this.$t('orderMod.buyerOrderSynergy.lineNum'),
          prop: 'orderNumber',
          width: 170,
          formattor: (val, row) => {
            return val + '|' + row.orderLineNum
          }
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.categoryName'),
          prop: 'categoryName',
          width: 130
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
          label: () => this.$t('orderMod.buyerOrderSynergy.orderNum'),
          prop: 'orderNum',
          width: 100,
          align: 'right'
        },
        {
          label: () => this.$t('orderMod.surplusDeliveryQuantity1'),
          prop: 'noticeSum',
          width: 100,
          align: 'right'
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryDate'),
          prop: 'deliveryTime',
          width: 100
        },
        // 暂时隐藏
        // {
        //   label: () => this.$t('orderMod.sumDeliveryQuantity'),
        //   prop: 'deliveryOrderQuantity',
        //   width: 100,
        //   align: 'right'
        // },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.noticeSum'),
          prop: 'deliveryNoticeQuantity',
          width: 100,
          align: 'right'
        },
        {
          label: () => this.$t('oneStopShopping.receiveAddress'), // 收货地址
          prop: 'receiveAddress',
          width: 100
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryNoticeStatus'),
          prop: 'deliveryNoticeStatus',
          width: 120,
          dataType: 'dict',
          code: 'DELIVERY_NOTICE_STATUS'
        },
        {
          label: () => this.$t('oneStopShopping.refusedReason'),
          prop: 'refusedReason',
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
          width: 100
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.lastUpdateDate'),
          prop: 'lastUpdateDate',
          width: 100,
          formattor: (val) => (val ? parseTime(val, '{y}-{m}-{d}') : '')
        },
        {
          prop: 'operation',
          label: () => this.$t('common.operation'),
          width: 120,
          btnStyle: 'text',
          fixed: 'right',
          showType: 'buttons',
          buttons: [
            {
              callback: (row) => this.acceptNotice('one', [row]),
              formattor: () => this.$t('orderMod.accept'),
              show: (row) => row.deliveryNoticeStatus === 'WAITING_VENDOR_CONFIRM'
            },
            {
              callback: (row) => this.refuseNotice('one', [row]),
              formattor: () => this.$t('common.refused'),
              show: (row) => row.deliveryNoticeStatus === 'WAITING_VENDOR_CONFIRM'
            }
          ]
        }
      ],
      queryParam: {}
    }
  },
  mounted () {
    this.getQuerydata()
  },
  methods: {
    getQuerydata (obj = {}) {
      const { dateList, ...rest } = obj
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
    // 接受
    acceptNotice (type, rows) {
      let list = type === 'mutil' ? this.currentRows : rows
      let unDel = 0
      list.forEach((item) => {
        if (item.deliveryNoticeStatus != 'WAITING_VENDOR_CONFIRM') {
          unDel++
        }
      })
      this.$nextTick(() => {
        if (unDel <= 0) {
          const selectData = list.map((i) => i.deliveryNoticeId)
          this.$http({
            url: '/api-sup-ce/order/deliveryNotice/supplierConfirm',
            method: 'POST',
            data: selectData,
            loading: true
          })
            .then((data) => {
              this.$message.success(this.$t('common.success'))
              this.getQuerydata()
            })
            .catch((err) => {})
        } else {
          this.$message({
            type: 'warning',
            message: this.$t('purchaseDemand.have') + unDel + this.$t('orderMod.msgOrder[20]')
          })
        }
      })
    },
    // 拒绝
    refuseNotice (type, rows) {
      let list = type === 'mutil' ? this.currentRows : rows
      let unDel = 0
      list.forEach((item) => {
        if (item.deliveryNoticeStatus != 'WAITING_VENDOR_CONFIRM') {
          unDel++
        }
      })
      this.$nextTick(() => {
        if (unDel <= 0) {
          this.$prompt(this.$t('orderMod.msgRufuseReason'), this.$t('common.tips'), {
            confirmButtonText: this.$t('common.confirm'),
            cancelButtonText: this.$t('common.cancel'),
            inputPattern: /\S{1,}/,
            inputErrorMessage: this.$t('orderMod.refuseReasonRequire')
          })
            .then(({ value }) => {
              let selectData = list.map((i) => i.deliveryNoticeId)
              this.$http({
                url: '/api-sup-ce/order/deliveryNotice/supplierReject',
                method: 'POST',
                data: {
                  ids: selectData,
                  refusedReason: value
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
        } else {
          this.$message({
            type: 'warning',
            message: this.$t('purchaseDemand.have') + unDel + this.$t('orderMod.msgOrder[21]')
          })
        }
      })
    },
    // 导出
    exportExcel () {}
  }
}
</script>
<style scoped lang="scss">
.the_buyerDeliveryNotice_wrapper {
  .order-uploader {
    display: inline-block;
    margin: 0 10px;
  }
  .block {
    display: flex;
    justify-content: center;
  }
}
</style>
<style>
.the_buyerDeliveryNotice_wrapper .el-table th > .cell {
  display: flex;
  justify-content: center;
}
</style>
