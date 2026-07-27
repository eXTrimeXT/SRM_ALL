<template>
  <el-container class="flex-container the_vendorPurchaseOrderList_wrapper" direction="vertical">
    <el-main>
      <FormWrapper
        :select-dictionary="selectDictionary"
        :form-array="preArr"
        :pre-form-obj="preFormObj"
        @getFormData="getQuerydata"
      />
      <!-- <el-tabs v-model="activeName" type="card" @tab-click="handleClick">
        <el-tab-pane :label="$t('orderMod.buyerOrderSynergy.orderDetails')" name="orderDetail" />
      </el-tabs> -->
      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton
            code="po:vendorPurchaseOrder:accept"
            type="primary"
            :disabled="!currentRows.length"
            @click="mutilAction('accept')"
          >
            {{ $t('orderMod.accept') }}
          </AuthorityButton>
          <AuthorityButton
            code="po:vendorPurchaseOrder:reject"
            type="primary"
            :disabled="!currentRows.length"
            @click="mutilAction('reject')"
          >
            {{ $t('common.refused') }}
          </AuthorityButton>
          <!-- 导出 -->
          <ExportExcel
            page-url="/api-sup-ce/order/orderDetail/listPage"
            :filter-params="queryParam"
            :table-header="exportDetail"
            :dict-codes="dictCodes"
            timeout="1000000"
            export-mode="front"
          />
        </template>
      </MainHeader>
      <!-- 订单列表 -->
      <!-- <TableView
        v-show="activeName === 'orderList'"
        ref="orderList"
        :table-header="initHeader1"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="true"
        :pre-query-data="queryParam"
        :auto-query="false"
        url="/api-sup-ce/order/order/listPage"
        :open-custom-table="true"
        :reserve-selection="true"
        row-key="orderId"
        :comActive="$attrs['changeTab']"
      /> -->
      <!-- 订单明细  -->
      <TableView
        ref="orderDetail"
        :table-header="initHeader2"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="true"
        :pre-query-data="queryParam"
        :auto-query="false"
        url="/api-sup-ce/order/orderDetail/listPage"
        :open-custom-table="true"
        :reserve-selection="true"
        row-key="orderDetailId"
        :comActive="$attrs['changeTab']"
        :cell-style="
          () => {
            return { 'text-align': 'center' }
          }
        "
        :header-cell-style="
          () => {
            return { 'text-align': 'center' }
          }
        "
        @getFooter="getFooter"
      >
        <template #ceeaPromiseReceiveDate="{ scope }">
          <!-- 承诺到货日期 -->
          <el-date-picker
            v-if="
              scope.row.orderDetailStatus === 'WAITING_VENDOR_CONFIRM' &&
                scope.row.orderStatus !== 'APPROVED'
            "
            v-model="scope.row.ceeaPromiseReceiveDate"
            type="date"
            format="yyyy-MM-dd"
            value-format="yyyy-MM-dd HH:mm:ss"
          />
          <span v-else>{{ $dayjsParse(scope.row.ceeaPromiseReceiveDate) }}</span>
        </template>
        <template #contractInfor="{ scope }">
          <el-button type="text" @click="viewContract(scope.row)">
            {{ $t('common.view') }}
          </el-button>
        </template>
      </TableView>
      <!-- 查看合同 -->
      <contract-infor
        :contract-view="contractView"
        :visible.sync="contractViewVisible"
        @searchData="searchViewContract"
        @close="contractViewVisible = false"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import vendorPurchaseOrderDetail from './vendorPurchaseOrderDetail'
import { parseTime, adaptDictData } from '@/utils'
import { getDictItemList } from '@/api/common'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { downloadFileLinkByPost } from 'lib@/utils/file'
import ExportExcel from 'lib@/components/export-excel'
import contractInfor from '@/library/composition/orderManagementBuyer/contract-infor'

export default {
  name: 'VendorPurchaseOrderList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel,
    contractInfor
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      selectLineRow: {},
      contractView: {
        row: {},
        params: {},
        title: this.$t('orderMod.viewContract'),
        checkbox: false,
        hiddenOperation: true,
        vendor: true
      },
      contractViewVisible: false,
      dictCodes: {
        orderStatus: 'PURCHASE_ORDER',
        orderDetailStatus: 'OrderDetailStatus',
        orderType: 'ORDER_TYPE'
      },
      getFooterNum: 1,
      activeName: 'orderDetail',
      preFormObj: {},
      currentRows: [],
      selectDictionary: {
        orderStatus: [],
        orderDetailStatus: [],
        orderType: []
      },
      pageSize: 15,
      preArr: [
        {
          prop: 'orderNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderNumber')
        },
        {
          prop: 'orderType',
          label: () => this.$t('purchaseDemand.purchaseType'),
          type: 'select'
        },
        {
          prop: 'orgIds',
          label: () => this.$t('oneStopShopping.businessEntity'),
          type: 'OUorganizationSelector',
          multiple: true
        },
        // 创建日期
        {
          prop: 'dateList',
          label: () => this.$t('common.creationDate'),
          type: 'daterange'
        },
        // {
        //   prop: 'userIds',
        //   label: () => this.$t('orderMod.buyerOrderSynergy.buyerName'),
        //   type: 'quicksearch',
        //   showKey: 'nickname',
        //   propKey: 'userId',
        //   name: 'scc_rbac_user_display'
        // },
        {
          prop: 'orderDetailStatus',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderDetailStatus'),
          type: 'select'
        }
      ],
      tableHeader: [],
      initHeader1: [
        {
          prop: 'orderNumber',
          showType: 'button',
          btnStyle: 'text',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderNumber'),
          minWidth: 170,
          callback: row => this.readOne(row)
        },
        {
          prop: 'ceeaPurchaseOrderDate',
          label: () => this.$t('oneStopShopping.orderDate'),
          width: 100,
          formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
        },
        {
          prop: 'orderType',
          label: () => this.$t('purchaseDemand.purchaseType'),
          width: 100,
          formattor: val => {
            const dict = this.selectDictionary.orderType.find(i => i.value === val)
            return dict ? dict.label : val
          }
        },
        {
          prop: 'orderStatus',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderStatus'),
          width: 100,
          formattor: val => {
            const dict = this.selectDictionary.orderStatus.find(i => i.value === val)
            return dict ? dict.label : val
          }
        },
        {
          prop: 'ceeaOrgName',
          label: () => this.$t('bid_mod.businessEntity'),
          width: 150
        },
        {
          prop: 'vendorName',
          label: () => this.$t('common.vendorName'),
          minWidth: 150
        },
        {
          prop: 'buyerName',
          label: () => this.$t('orderMod.buyerOrderSynergy.buyerName'),
          width: 100
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
              callback: row => this.doAction('accept', [row]),
              code: 'po:vendorPurchaseOrder:acceptRow',
              formattor: () => this.$t('orderMod.accept'),
              show: row =>
                row.orderStatus === 'APPROVED_INVALID' &&
                row.ceeaIfSupplierConfirm === 'Y' &&
                row.ifDetailHandle != 'Y'
            },
            {
              callback: row => this.doAction('reject', [row]),
              code: 'po:vendorPurchaseOrder:acceptRow',
              formattor: () => this.$t('common.refused'),
              show: row =>
                row.orderStatus === 'APPROVED_INVALID' &&
                row.ceeaIfSupplierConfirm === 'Y' &&
                row.ifDetailHandle != 'Y'
            }
          ]
        }
      ],
      initHeader2: [
        {
          prop: 'orderNumber',
          showType: 'button',
          btnStyle: 'text',
          label: () =>
            this.$t('orderMod.buyerOrderSynergy.orderNumber') +
            '|' +
            this.$t('orderMod.buyerOrderSynergy.lineNum'),
          minWidth: 170,
          formattor: (val, row) => {
            return val + '|' + row.lineNum
          },
          callback: row => this.readOne(row)
        },
        {
          prop: 'materialCode',
          label: () => this.$t('orderMod.buyerOrderSynergy.materialCode'),
          width: 100
        },
        {
          prop: 'materialName',
          label: () => this.$t('orderMod.buyerOrderSynergy.materialName'),
          width: 100
        },
        {
          prop: 'orderNum',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderNum'),
          align: 'right',
          width: 100
        },
        {
          prop: 'notifiedNum',
          label: () => this.$t('orderMod.notified'), // 已通知
          desc: this.$t('orderMod.notifiedNum'), // 通过订单创建送货通知单的累计通知数量+通过订单创建送货单的累计送货数量
          minWidth: 100
        },
        {
          prop: 'notNotifiedNum',
          label: () => this.$t('orderMod.notNotified'), // 未通知
          desc: this.$t('orderMod.notNotifiedCal'),
          minWidth: 120
        },
        {
          prop: 'inDeliveryNum',
          label: () => this.$t('orderMod.onWay'), // 在途
          desc: this.$t('orderMod.onWayCal'),
          minWidth: 120
        },
        {
          prop: 'unDeliveryNum',
          label: () => this.$t('orderMod.unSent'), // 未送
          desc: this.$t('orderMod.unSentCal'),
          minWidth: 120
        },
        {
          prop: 'inStockNum',
          label: () => this.$t('orderMod.inStock'), // 已入库
          desc: this.$t('orderMod.inStockCal'),
          minWidth: 120
        },
        {
          prop: 'returnNum',
          label: () => this.$t('orderMod.returned'), // 已退货
          minWidth: 100
        },
        {
          prop: 'unit',
          label: () => this.$t('orderMod.buyerOrderSynergy.unit'),
          align: 'right',
          width: 100
        },
        {
          prop: 'vendorName',
          label: () => this.$t('common.vendorName'),
          minWidth: 150
        },
        // 订单日期
        {
          prop: 'ceeaPurchaseOrderDate',
          label: () => this.$t('oneStopShopping.orderDate'),
          width: 100,
          formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
        },
        // 要求到货日期
        {
          prop: 'ceeaPlanReceiveDate',
          label: () => this.$t('purchaseDemand.requirementDate1'),
          width: 120,
          formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
        },
        {
          prop: 'ceeaPromiseReceiveDate',
          label: () => this.$t('purchaseDemand.promiseReceiveDate'), // 承诺到货日期
          width: 150,
          showType: 'slot',
          slot: 'ceeaPromiseReceiveDate'
        },
        {
          prop: 'ceeaUnitNoTaxPrice',
          label: () => this.$t('orderMod.buyerOrderSynergy.untaxedPrice'),
          align: 'right',
          width: 120
        },
        {
          prop: 'ceeaUnitTaxPrice',
          label: () => this.$t('purchaseDemand.taxPrice'),
          align: 'right',
          width: 120
        },
        {
          prop: 'currencyName',
          label: () => this.$t('orderMod.buyerOrderSynergy.currencyName'),
          align: 'right',
          width: 100
        },
        {
          prop: 'ceeaTaxRate',
          label: () => this.$t('orderMod.buyerOrderSynergy.taxRate'),
          align: 'right',
          width: 100
        },
        {
          prop: 'orderStatus',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderStatus'),
          width: 100,
          formattor: val => {
            const dict = this.selectDictionary.orderStatus.find(i => i.value === val)
            return dict ? dict.label : val
          }
        },
        {
          prop: 'orderDetailStatus',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderDetailStatus'),
          width: 120,
          formattor: val => {
            const dict = this.selectDictionary.orderDetailStatus.find(i => i.value === val)
            return dict ? dict.label : val
          }
        },
        {
          prop: 'orderType',
          label: () => this.$t('purchaseDemand.purchaseType'),
          width: 100,
          formattor: val => {
            const dict = this.selectDictionary.orderType.find(i => i.value === val)
            return dict ? dict.label : val
          }
        },
        {
          prop: 'ceeaOrgName',
          label: () => this.$t('bid_mod.businessEntity'),
          width: 100
        },
        {
          prop: 'ceeaEmpUsername',
          label: () => this.$t('orderMod.buyerOrderSynergy.buyerName'),
          width: 100
        },
        {
          prop: 'ceeaIfSupplierConfirm',
          label: () => this.$t('oneStopShopping.ifSupplierConfirm'),
          width: 120,
          formattor: val => (val === 'Y' ? this.$t('common.yes') : this.$t('common.no'))
        },
        {
          prop: 'contractInfor',
          label: () => this.$t('orderMod.contractInfor'), // 合同信息
          width: 100,
          fixed: 'right',
          showType: 'slot',
          slot: 'contractInfor'
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
              callback: row => this.doAction('accept', [row]),
              formattor: () => this.$t('orderMod.accept'),
              show: row =>
                row.orderDetailStatus === 'WAITING_VENDOR_CONFIRM' && row.orderStatus === 'APPROVED_INVALID'
            },
            {
              callback: row => this.doAction('reject', [row]),
              formattor: () => this.$t('common.refused'),
              show: row =>
                row.orderDetailStatus === 'WAITING_VENDOR_CONFIRM' && row.orderStatus === 'APPROVED_INVALID'
            }
          ]
        }
      ],
      queryParam: {},
      exportDetail: [] // 导出明细
    }
  },
  watch: {
    $route: {
      handler () {
        const { from, taskIndex, formId: orderId, formNo: orderNumber, row } = this.$route.params
        if (from === 'fromFun') {
          if (taskIndex === 1) {
            // 待办
            this.queryParam.orderNumber = orderNumber
            this.preFormObj.orderNumber = orderNumber
          } else if (taskIndex === 2) {
            // 已办
            this.readOne({
              ...this.$route.params,
              orderId,
              orderNumber
            })
          }
        }
        // 来源：订单协同-采购订单变更
        if (from === 'supplierPurchaseOrderChange') {
          this.readOne(row)
        }
      },
      deep: true,
      immediate: true
    }
  },
  created () {
    // 字典信息查询
    const dictionaryCodes = [
      { dictCode: 'ORDER_TYPE' }, // 订单类型
      { dictCode: 'PURCHASE_ORDER' }, // 订单状态
      { dictCode: 'OrderDetailStatus' } // 订单行状态
    ]
    getDictItemList(dictionaryCodes).then(res => {
      const [ORDER_TYPE, PURCHASE_ORDER, OrderDetailStatus] = res.data
      const orderStatusOpts = adaptDictData(PURCHASE_ORDER.PURCHASE_ORDER)
      const orderRowStatusOpts = adaptDictData(OrderDetailStatus.OrderDetailStatus)
      const orderType = adaptDictData(ORDER_TYPE.ORDER_TYPE)
      this.selectDictionary = {
        orderStatus: orderStatusOpts,
        orderDetailStatus: orderRowStatusOpts,
        orderType: orderType
      }
    })
    this.tableHeader = this.initHeader1
    this.$nextTick(() => {
      this.getQuerydata()

      this.initHeader2.forEach(column => {
        this.$set(column, 'unsortable', true)
      })

      // 表格导出时设置行号
      this.setHeaderLineNum()
    })
  },
  methods: {
    setHeaderLineNum () {
      this.exportDetail = [...this.initHeader2]

      this.exportDetail[0] = {
        prop: 'orderNumber',
        label: () => this.$t('orderMod.buyerOrderSynergy.orderNumber'),
        width: 150
      }

      this.exportDetail.splice(1, 0, {
        prop: 'lineNum',
        label: () => this.$t('orderMod.buyerOrderSynergy.lineNum'),
        width: 80
      })
    },
    // 合同编号合同编码查询
    async searchViewContract (obj) {
      const data = await this.getContractList(this.selectLineRow, obj)
      this.contractView.params = data
    },
    // 获取合同接口
    async getContractList (row, params) {
      const { data } = await this.$http({
        url: '/api-sup-ce/po/order/queryContractMappingByOrderDetailId',
        method: 'POST',
        data: {
          'orderDetailId': row.orderDetailId,
          ...params
        },
        loading: true
      })
      return data
    },
    // 查看合同
    async viewContract (row) {
      const list = await this.getContractList(row)
      this.selectLineRow = row
      this.contractView.row = row
      this.contractView.params = list
      this.contractViewVisible = true
    },
    getFooter (data) {
      this.getFooterNum = data.value
    },
    // 选择承诺时间校验
    changeReceiveData (row) {
      const ceeaPlanReceiveDate = new Date(row.ceeaPlanReceiveDate).getTime() // 要求到货日期转时间戳
      const ceeaPromiseReceiveDate = new Date(row.ceeaPromiseReceiveDate).getTime() // 承诺到货日期转时间戳
      return ceeaPromiseReceiveDate > ceeaPlanReceiveDate
    },
    // 订单列表、订单明细tab切换
    handleClick () {
      this.$refs[this.activeName].clearSelection()
      this.currentRows = []
      this.getQuerydata()
      this.$refs[this.activeName].doLayout()
    },
    // 订单明细处理type(mutil批量，one行)，actionType(accept接受，reject拒绝)
    mutilAction (type) {
      const actionText = type === 'accept' ? this.$t('orderMod.accept') : this.$t('common.refused')
      const msg =
        this.activeName === 'orderList'
          ? this.$t('orderMod.msgVendorOrder[0]')
          : this.$t('orderMod.msgVendorOrder[1]')
      let unDel = 0
      if (this.activeName === 'orderList') {
        this.currentRows.forEach(item => {
          if (
            item.orderStatus != 'APPROVED' ||
            item.ifDetailHandle === 'Y'
          ) {
            unDel++
          }
        })
      } else {
        this.currentRows.forEach(item => {
          if (item.orderDetailStatus != 'WAITING_VENDOR_CONFIRM') {
            unDel++
          }
        })
      }
      this.$nextTick(() => {
        if (unDel <= 0) {
          this.doAction(type, this.currentRows)
        } else {
          this.$message({
            type: 'warning',
            message:
              this.$t('purchaseDemand.have') +
              unDel +
              this.$t('orderMod.msgVendorOrder[2]') +
              msg +
              this.$t('orderMod.msgVendorOrder[3]') +
              actionText +
              this.$t('orderMod.msgVendorOrder[4]')
          })
        }
      })
    },
    // 接受
    acceptHandler (list) {
      for (const row of list) {
        if (
          this.activeName === 'orderDetail' &&
          this.changeReceiveData(row)
        ) {
          this.$message.warning(
            this.$t('orderMod.promiseDateCheck')
          )
          return false
        }
      }

      let acceptUrl = ''
      if (this.activeName === 'orderList') {
        acceptUrl = '/api-sup-ce/order/order/supplierConfirm'
      } else {
        acceptUrl = '/api-sup-ce/order/orderDetail/supplierConfirm'
      }
      this.$http({
        url: acceptUrl,
        method: 'POST',
        data: list
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    async rejectHandler (list) {
      const prompt = await this.$prompt(this.$t('orderMod.msgRufuseReason'), this.$t('common.tips'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        inputPattern: /\S{1,}/,
        inputErrorMessage: this.$t('orderMod.refuseReasonRequire')
      })
      if (!prompt) return

      let rejectUrl = ''
      let params = null
      if (this.activeName === 'orderList') {
        rejectUrl = '/api-sup-ce/order/order/supplierReject'
        params = {
          ids: list.map(i => i.orderId),
          refusedReason: prompt.value
        }
      } else {
        rejectUrl = '/api-sup-ce/order/orderDetail/supplierReject'
        params = list.map(row => {
          row.refusedReason = prompt.value
          return row
        })
      }
      this.$http({
        url: rejectUrl,
        method: 'POST',
        data: params
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    doAction (type, list) {
      if (type === 'accept') {
        this.acceptHandler(list)
      } else {
        this.rejectHandler(list)
      }
    },
    getQuerydata (v) {
      const params = v || this.preFormObj
      // if (params.userIds && typeof params.userIds === 'number') {
      //   params.userIds = [params.userIds]
      // }
      if (params.dateList) {
        params.startTime = params.dateList[0]
        params.endTime = params.dateList[1]
      }
      let fields = ''
      if (this.activeName === 'orderList') {
        fields = 'ORDER_ID,ORDER_NUMBER,BUDGET_MANAGEMENT_NUM,CEEA_PURCHASE_ORDER_DATE,ORDER_TYPE,ORDER_STATUS,CEEA_ORG_NAME,ORGANIZATION_NAME,VENDOR_NAME,REFUSE_REASON,SOURCE_SYSTEM,CREATED_FULL_NAME,CREATION_DATE,CEEA_IF_SUPPLIER_CONFIRM,VENDOR_CODE,CEEA_EMP_USERNAME,CEEA_SAVE_BY,CREATED_BY'
      } else {
        fields = 'o.ORDER_ID,o.ORDER_NUMBER,o.BUDGET_MANAGEMENT_NUM,o.CEEA_PURCHASE_ORDER_DATE,o.ORDER_TYPE,o.ORDER_STATUS,o.CEEA_ORG_NAME,o.ORGANIZATION_NAME,o.VENDOR_CODE,o.VENDOR_NAME,o.CEEA_EMP_USERNAME,od.REFUSED_REASON,o.SOURCE_SYSTEM,o.CREATED_FULL_NAME,o.CREATION_DATE,o.CEEA_IF_SUPPLIER_CONFIRM,od.CEEA_REQUIREMENT_HEAD_NUM,od.MATERIAL_CODE,od.MATERIAL_NAME,od.CEEA_IF_REQUIREMENT,od.ORDER_NUM,od.STORAGE_NUM,od.RETURN_NUM,od.CLOSED_CAUSE,od.REQUIREMENT_DATE,od.UNIT,od.CEEA_UNIT_TAX_PRICE,od.CEEA_UNIT_NO_TAX_PRICE,od.CEEA_TAX_RATE,od.CURRENCY_NAME,od.CEEA_TAX_AMOUNT,od.CEEA_AMOUNT_INCLUDING_TAX,od.CEEA_AMOUNT_EXCLUDING_TAX,od.CATEGORY_NAME,od.REQUIREMENT_QUANTITY,od.ORDER_DETAIL_STATUS,od.LINE_NUM,od.CEEA_PLAN_RECEIVE_DATE,od.CEEA_PROMISE_RECEIVE_DATE,od.DELIVERY_NOTICE_QUANTITY,od.CEEA_ROW_NUM,od.ORDER_DETAIL_ID'
      }
      this.queryParam = { ...params, fields }
      this.$nextTick(() => {
        this.$refs[this.activeName].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    exportOne () {},
    imoportOne () {},
    deleteOne () {},
    readOne (row) {
      // 查看--只读状态
      const tab = {
        component: vendorPurchaseOrderDetail,
        params: {
          flag: 'edit',
          row,
          showType: 'readOnly'
        },
        title: row.orderNumber,
        name: 'vendorPurchaseOrderDetail' + row.orderNumber
      }
      this.$emit('tab-add', tab)
    },
    dateExchange () {},
    cancelOne () {}
  }
}
</script>
<style scoped lang="scss">
.the_vendorPurchaseOrderList_wrapper {
  .order-uploader {
    display: inline-block;
    margin: 0 10px;
  }
}
</style>
