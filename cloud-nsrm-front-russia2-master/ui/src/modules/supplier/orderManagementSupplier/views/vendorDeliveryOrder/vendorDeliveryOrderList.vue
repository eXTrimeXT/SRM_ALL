<template>
  <el-container class="flex-container the_vendorPurchaseOrderList_wrapper" direction="vertical">
    <el-main>
      <FormWrapper :pre-form-obj="preFormObj" :form-array="preArr" @getFormData="getQuerydata" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton type="primary" code="sup:vendorDeliveryOrder:createdDelivery" @click="createdDelivery">
            {{ $t('orderMod.buyerOrderSynergy.createDelivery') }}
          </AuthorityButton>
          <!--<el-button
            type="primary"
            @click="submitBatch"
            :disabled="canOperate"
            >提交</el-button
          >
          <el-button type="primary"  @click="getDelivery"
            >预约送货</el-button
          >-->
          <!-- <el-button  @click="deleteOne" :disabled="! currentRow" >删除</el-button> -->
          <!-- <el-button  @click="exportOne">Excel导出</el-button> -->
          <!-- <el-button  @click="printOne">打印</el-button> -->
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :row-index-fixed="false"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        url="/api-sup-ce/order/deliveryNote/deliveryNotePage"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
      />
    </el-main>
  </el-container>
</template>
<script>
import http from '@/utils/axios/http'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import vendorDeliveryOrderDetail from './vendorDeliveryOrderDetail'
import { adaptDictData, parseTime } from '@/utils'
import { getAllLangList, getDictItemList } from '@/api/common'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import deliveryAppointmentDetail from '../deliveryAppointments/deliveryAppointmentDetail'
import { deliveryOrderApi } from 'mods@/orderManagementSupplier/api'

import tagManage from 'mods@/orderManagementSupplier/views/vendorDeliveryOrderEngine/tagManage.vue'

export default {
  name: 'VendorDeliveryOrderList',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      tableName: 'vendorDeliveryOrderList',
      defaultTableHeader: [],
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      currentRows: [],
      canOperate: true,
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
        // { prop: "jitOrder", label: "JIT订单", type: "select" },
        // { prop: "orderStatus", label: "订单状态", type: "select" },
        {
          prop: 'orderNumber',
          label: () => this.$t('purSettlementMod.orderNumber')
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
        }
        // {
        //   prop: "vendorCode",
        //   label: "供应商编码",
        //   type: "quicksearch",
        //   showKey: "companyCode",
        //   name: "scc_sup_company_info_display"
        // }
      ],
      queryParam: {},
      curRole: this.$store.getters.userType,
      firstLoad: true,
      preFormObj: {}
    }
  },
  watch: {
    $route: {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler () {
        if (
          this.$route.params.from === 'workCount' &&
          this.$route.params.funName === 'vendorDeliveryOrder'
        ) {
          // 供应商 工作台跳转
          this.queryParam.deliveryNoteStatus = this.$route.params.deliveryNoteStatus
          this.preFormObj = Object.assign(
            {},
            { deliveryNoteStatus: this.$route.params.deliveryNoteStatus }
          )
        }
      }
    }
  },
  updated () {
    this.defaultTableHeader = this.tableHeader
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
        callback: (row) => this.readDelivery(row)
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
        prop: 'deliveryNoteStatus',
        label: () => this.$t('bidMod.billstatus'),
        width: 120,
        dataType: 'dict',
        code: 'DELIVERY_NOTE_STATUS'
      },
      // { prop: 'orderNumber', label: '订单号' },
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
        prop: 'receivedFactory',
        label: () => this.$t('oneStopShopping.receiveAddress'),
        width: 150
      },
      {
        prop: 'vendorName',
        label: () => this.$t('common.vendor'),
        minWidth: 150
      },
      {
        prop: 'comments',
        label: () => this.$t('orderMod.buyerOrderSynergy.comments'),
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
        minWidth: 220,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: function (row) {
              this.updateDeliveryConfirm(row)
            }.bind(this),
            code: 'sup:vendorDeliveryOrder:updateDelivery',
            formattor (val) {
              return _this.$t('common.edit')
            },
            show: (row) => row.deliveryNoteStatus !== 'DELIVERED' && row.deliveryNoteStatus !== 'CANCELLED'
          },
          {
            callback: function (row) {
              this.cancelDelivery(row)
            }.bind(this),
            code: 'sup:vendorDeliveryOrder:cancelDelivery',
            formattor (val) {
              return _this.$t('orderMod.cancelDelivery')
            },
            show: (row) => row.deliveryNoteStatus === 'DELIVERED'
          },
          {
            callback: function (row) {
              this.deleteDelivery(row)
            }.bind(this),
            code: 'sup:vendorDeliveryOrder:deleteDelivery',
            formattor (val) {
              return _this.$t('common.delete')
            },
            show: (row) => row.deliveryNoteStatus === 'CREATE'
          },
          {
            callback: function (row) {
              this.confirmDelivery(row)
            }.bind(this),
            code: 'sup:vendorDeliveryOrder:deleteDelivery',
            formattor (val) {
              // 确认发货
              return _this.$t('orderMod.confirmDelivery')
            },
            show: (row) => row.deliveryNoteStatus !== 'DELIVERED'
          },
          {
            callback: function (row) {
              this.goTagManageConfirm(row)
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
    confirmDelivery (row) {
      this.$confirm(this.$t('orderMod.isConfirmDelivery'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-sup-ce/order/deliveryNote/getAffirmDelivery',
            method: 'GET',
            params: { deliveryNoteId: row.deliveryNoteId },
            loading: true
          }).then((res) => {
            this.$message.success(this.$t('common.success'))
            this.getQuerydata()
          })
        })
        .catch((e) => {
          console.log(e, 'err')
        })
    },
    submitBatch () {
      const submitStaus = ['CREATE']
      if (
        this.currentRows.some(
          (i) => submitStaus.findIndex((j) => j === i.deliveryNoteStatus) === -1
        )
      ) {
        this.$message({
          type: 'warning',
          message: this.$t('orderMod.msgVendorOrder[5]')
        })
        return
      }
      const data = this.currentRows.map((i) => i.deliveryNoteId)
      deliveryOrderApi.vendorDeliveryNoteSubmitBatch(data).then((res) => {
        this.$message({
          type: 'success',
          message: res.message
        })
        this.getQuerydata()
      })
    },
    getQuerydata (v) {
      // let query = v || this.preFormObj;
      // this.queryParam = query;
      const { billDate, ...rest } = v || this.preFormObj
      let params = { ...rest }
      if (billDate) {
        const [startSubmittedTime, endSubmittedTime] = billDate
        params = { ...rest, startSubmittedTime, endSubmittedTime }
      }
      this.queryParam = Object.assign({ vendorId: this.$store.getters.user.companyId }, params)
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRows = val
      this.canOperate = !val.length
    },
    printOne () { },
    exportOne () { },
    getDelivery () {
      const tab = {
        component: deliveryAppointmentDetail,
        params: { flag: 'add' },
        ctrlHeight: true,
        title: this.$t('orderMod.addAppointmentDeliveryNote'),
        name: 'deliveryAppointmentDetail'
      }
      this.$emit('tab-add', tab)
    },
    cancelDelivery (row) {
      http({
        url: '/api-sup-ce/order/deliveryNote/getCancelDelivery',
        method: 'GET',
        params: { deliveryNoteId: row.deliveryNoteId },
        loading: true
      }).then((res) => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    async deleteDelivery (row) {
      const sign = await this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
      if (sign !== 'confirm') return

      http({
        url: '/api-sup-ce/order/deliveryNote/delete',
        method: 'GET',
        params: { deliveryNoteId: row.deliveryNoteId },
        loading: true
      }).then((res) => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    readDelivery (row) {
      // 只读模式
      let tab = {
        component: vendorDeliveryOrderDetail,
        params: { flag: 'readOnly', row },
        ctrlHeight: true,
        title: this.$t('orderMod.buyerOrderSynergy.vendorDelivery') + row.deliveryNumber,
        name: 'vendorDeliveryOrderDetail' + row.deliveryNumber
      }
      this.$emit('tab-add', tab)
    },
    tagOuterBoxFetch (row) {
      return http({
        url: '/api-sup-ce/api-ql/TagOuterBox/query',
        method: 'post',
        data: {
          type: 'TagOuterBox',
          lang: 'zh-cn',
          query: {
            outerBoxId: {}
          },
          payload: {
            filter: {
              deliveryNumber: {
                eq: row.deliveryNumber
              }
            },
            page: {
              pageNum: 1,
              pageSize: 15
            }
          },
          action: 'query'
        },
        loading: true
      })
    },
    async updateDeliveryConfirm (row) {
      let res = await this.tagOuterBoxFetch(row)
      if (res?.data?.payload?.total > 0) {
        this.$confirm(
          this.$t('buyerDeliveryOrder.prompt2'),
          {
            confirmButtonText: this.$t('buyerDeliveryOrder.viewTag'),
            cancelButtonText: this.$t('common.confirm'),
            type: 'warning'
          },
        )
          .then(() => {
            this.goTagManage(row)
          })
          .catch(() => { })
      } else {
        this.updateDelivery(row)
      }
    },
    updateDelivery (row) {
      // 编辑tab
      let tab = {
        component: vendorDeliveryOrderDetail,
        params: { flag: 'edit', row },
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
    },
    async goTagManageConfirm (row) {
      if (row.deliveryNoteStatus === 'CREATE') {
        let res = await this.tagOuterBoxFetch(row)
        if (res?.data?.payload?.total > 0) {
          this.goTagManage(row)
        } else {
          this.$confirm(this.$t('buyerDeliveryOrder.prompt1'), {
            confirmButtonText: this.$t('buyerDeliveryOrder.toBind'),
            cancelButtonText: this.$t('common.cancel'),
            type: 'warning'
          })
            .then(() => {
              this.goTagManage(row)
            })
            .catch(() => { })
        }
      } else {
        this.goTagManage(row)
      }
    }
  }
}
</script>
<style scoped lang="scss"></style>
