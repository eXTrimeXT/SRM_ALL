<template>
  <el-container
    class="flex-container the_vendorDeliverPlanList_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :select-dictionary="selectDictionary"
        :form-array="preArr"
        :pre-form-obj="preFormObj"
        @getFormData="getQuerydata"
      />

      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <MImport
            ref="import"
            style="display: inline-block; margin: 0 15px"
            :title="$t('common.import')"
            :up-load-url="iModal.upLoadUrl"
            :extra-data="extraData"
            @downloadTemplate="openDialogVisible('template')"
            @handleSuccess="handleSuccess"
          />
          <!-- <el-button type="primary" @click="importOrder">{{ $t("common.import") }}</el-button> -->
          <!-- <el-button type="primary" @click="publishBill">{{ $t("common.publish") }}</el-button> -->
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="true"
        :pre-query-data="queryParam"
        :auto-query="false"
        :comActive="$attrs['changeTab']"
        url="/api-sup-ce/deliver/deliverPlan/deliverPlanListPage"
        :open-custom-table="true"
      />
      <!--模版下载弹框-->
      <srm-dialog
        :title="$t('components.importOrExportDialog.filterConditions')"
        size="small"
        class="the_follow_tender_dialog"
        :visible.sync="dialogVisible"
        :close-on-click-modal="false"
      >
        <el-row>
          <el-col :span="18">
            <span>{{ $t('orderMod.planMonth') }}</span>
            <el-date-picker
              v-model="monthlySchDate"
              type="month"
              format="yyyy-MM"
              value-format="yyyy-MM"
            />
          </el-col>
        </el-row>
        <el-row>
          <el-col
            :span="12"
            :offset="12"
          >
            <el-button
              @click="dialogVisible = false"
            >
              {{
                $t('common.cancel')
              }}
            </el-button>
            <el-button
              type="primary"
              @click="downloadTemplate"
            >
              {{
                $t('common.confirm')
              }}
            </el-button>
          </el-col>
        </el-row>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import vendorDeliverPlanDetail from './vendorDeliverPlanDetail'
import { parseTime, findMenuInfoByPath } from '@/utils'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { getToken } from '@/utils/auth'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import { deliverPlanApi } from 'mods@/orderManagementSupplier/api'

export default {
  name: 'VendorDeliverPlanList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    MImport
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      preFormObj: {},
      currentRows: [],
      monthlySchDate: null,
      dialogVisible: false,
      canOperate: false,
      tableUrl: '/api-sup-ce/po/order/listPage',
      isOrderPage: true,
      iModal: {
        title: this.$t('common.excelImport'),
        upLoadUrl: '/api-sup-ce/deliver/deliverPlan/importExcel'
      },
      extraData: {
        fileModular: 'sup-ce',
        fileFunction: 'vendorDeliverPlan',
        fileType: 'excel'
      },
      headers: {},
      orderStatusOpts: [],
      orderStatusOpts2: [],
      selectDictionary: {},
      tableName: 'vendorOrderList',
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
      menuInfo: {},
      preArr: [
        {
          prop: 'orgIds',
          label: () => this.$t('bid_mod.businessEntity'),
          type: 'OUorganizationSelector',
          multiple: true
        },
        {
          prop: 'organizationIds',
          label: () => this.$t('bid_mod.inv'),
          type: 'INVorganizationSelector',
          multiple: true,
          parentId: 'orgIds'
        },
        {
          prop: 'deliveryAddress',
          label: () => this.$t('bid_mod.tradingLocations')
        },
        {
          prop: 'vendorName',
          label: () => this.$t('orderMod.buyerOrderSynergy.vendorName'),
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_display'
        },
        {
          prop: 'materialName',
          label: () => this.$t('orderMod.buyerOrderSynergy.materialName'),
          type: 'quicksearch',
          showKey: 'materialName',
          name: 'scc_base_material_item_display'
        },
        {
          prop: 'categoryName',
          label: () => this.$t('orderMod.buyerOrderSynergy.categoryName'),
          type: 'quicksearch',
          showKey: 'categoryName',
          name: 'scc_base_purchase_category2'
        },
        {
          prop: 'deliverPlanStatus',
          label: () => this.$t('orderMod.planStatus'),
          type: 'dict',
          code: 'DELIVER_PLAN_STATUS'
        },
        {
          prop: 'deliverPlanLineStatus',
          label: () => this.$t('orderMod.deliverPlanStatus'),
          type: 'dict',
          code: 'DELIVER_PLAN_LINE_STATUS'
        },
        {
          prop: 'monthlySchDate',
          label: () => this.$t('orderMod.planMonth'),
          type: 'month'
        },
        {
          prop: 'deliverPlanNum',
          label: () => this.$t('orderMod.arrivalPlanNo')
        },
        { prop: 'version', label: () => this.$t('dataConfMod.version') }
      ],
      operationType: null,
      queryParam: {},
      initHeader: [
        {
          prop: 'deliverPlanNum',
          showType: 'button',
          btnStyle: 'text',
          label: () => this.$t('orderMod.arrivalPlanNo'),
          width: 150,
          callback: (row) => this.readOne(row)
        },
        {
          prop: 'monthlySchDate',
          label: () => this.$t('orderMod.planMonth'),
          width: 100
        },
        {
          prop: 'deliverPlanStatus',
          label: () => this.$t('orderMod.planStatus'),
          width: 100,
          type: 'dict',
          code: 'DELIVER_PLAN_STATUS'
        },
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
          prop: 'deliveryAddress',
          label: () => this.$t('bid_mod.tradingLocations'),
          width: 150
        },
        {
          prop: 'vendorCode',
          label: () => this.$t('common.vendorCode'),
          width: 120
        },
        {
          prop: 'vendorName',
          label: () => this.$t('common.vendorName'),
          minWidth: 150
        },
        {
          prop: 'categoryName',
          label: () => this.$t('dataConfMod.categoryLittle'),
          width: 120
        },
        {
          prop: 'materialCode',
          label: () => this.$t('bidMod.itemCode'),
          width: 120
        },
        {
          prop: 'materialName',
          label: () => this.$t('bidMod.itemName'),
          minWidth: 150
        },
        { prop: 'unit', label: () => this.$t('bid_mod.unit'), width: 100 },
        {
          prop: 'schTotalQuantity',
          label: () => this.$t('orderMod.totalPlanQuantity'),
          width: 120
        },
        {
          prop: 'match',
          label: () => this.$t('orderMod.suitability'),
          width: 100
        },
        {
          prop: 'version',
          label: () => this.$t('dataConfMod.version'),
          width: 100
        },
        {
          prop: 'operation',
          label: () => this.$t('common.operation'),
          width: 150,
          btnStyle: 'text',
          fixed: 'right',
          showType: 'buttons',
          buttons: [
            {
              callback: (row) => this.editOne(row),
              code: 'ce:vendorDeliverPlan:edit',
              formattor: () => this.$t('orderMod.viewDetail')
              // show: row => (row.deliverPlanStatus !== "APPROVAL")
            },
            {
              callback: (row) => this.publishBill(row),
              code: 'ce:vendorDeliverPlan:publishBill',
              formattor: () => this.$t('common.publish'),
              show: (row) => row.deliverPlanStatus === 'DRAFT'
            }
          ]
        }
      ]
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
          this.$route.params.funName === 'vendorDeliverPlan'
        ) {
          // 供应商 工作台跳转
          this.queryParam.deliverPlanStatus = this.$route.params.deliverPlanStatus
          this.preFormObj = Object.assign(
            {},
            { deliverPlanStatus: this.$route.params.deliverPlanStatus }
          )
        }
      }
    }
  },
  updated () {
    this.defaultTableHeader = this.tableHeader
  },
  created () {
    const menus = this.$store.getters.userInfo.menus
    this.menuInfo = findMenuInfoByPath(this.$route.path, menus) || {}
    this.headers = {
      Authorization: `Bearer ${getToken()}`
      // contentType: 'form-data',
    }
    this.tableHeader = this.initHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  mounted () {},
  methods: {
    submitOne (row) {
      // this.$http({
      //   url: '/api-sup-ce/po/order/approval',
      //   method: 'POST',
      //   data: { order: { orderId: row.orderId } }
      // }).then((res) => {
      //   this.$message.success(this.$t('common.success'))
      //   this.getQuerydata()
      // })
    },
    buyerRejectOne (row) {
      // 采购商驳回
      this.$http({
        url: '/api-sup-ce/po/order/reject',
        method: 'POST',
        data: { order: { orderId: row.orderId } }
      }).then((res) => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    rejectOne (row) {
      // 供应商拒绝
      this.$http({
        url: '/api-sup-ce/po/order/supplierReject',
        method: 'POST',
        data: {
          order: {
            orderId: row.orderId,
            refuseReason: this.$t('oneStopShopping.refusedReason') + '_' + new Date()
          }
        }
      }).then((res) => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    acceptOne (row) {
      this.$http({
        url: '/api-sup-ce/po/order/supplierConfirm',
        method: 'POST',
        data: { order: { orderId: row.orderId } }
      }).then((res) => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    handleSuccess (val) {
      this.getQuerydata()
    },
    openDialogVisible (type) {
      // 下载模板
      this.operationType = type
      this.dialogVisible = true
    },
    downloadTemplate () {
      // 下载模板
      if (!this.monthlySchDate) {
        this.$message.error(this.$t('orderMod.msgSelPlanMonthly'))
        return
      }
      if (this.operationType === 'template') {
        // 下载模板
        downloadFileLink(
          '/api-sup-ce/deliver/deliverPlan/importModelDownload?monthlySchDate=' +
            this.monthlySchDate,
          parseTime(new Date()) + this.$t('orderMod.impoerTmpXLSX')
        ).catch(() => {
          this.$message.error(this.$t('components.eio.downloadFail'))
        })
      }
      this.dialogVisible = false
    },
    onSuccess ({ code, message }) {
      const isError = code !== '0'
      this.$message({
        type: isError ? 'error' : 'success',
        message,
        duration: 0,
        showClose: true
      })
    },
    exportOrder () {},
    importOrder () {},
    onError (err) {
      this.$message({
        type: 'error',
        message: err.message
      })
    },
    submitBatch () {
      const submitStaus = ['UNISSUED']
      if (
        this.currentRows.some((i) => submitStaus.findIndex((j) => j === i.deliverPlanStatus) === -1)
      ) {
        this.$message({
          type: 'warning',
          message: this.$t('orderMod.msgVendorOrder[26]')
        })
        return
      }
      const params = this.currentRows.map((i) => i.orderId)
      deliverPlanApi.publishBatch(params).then((res) => {
        this.$message({
          tyep: 'success',
          message: res.message
        })
        this.getQuerydata()
      })
    },
    getQuerydata (v) {
      let query = v || this.preFormObj
      this.queryParam = query
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.canOperate = val && val.length
      this.currentRows = val
    },
    queryOrderList () {
      deliverPlanApi.orderList({})
        .then((res) => {
          this.$refs[this.gridId].tableData = res.data.list || []
        })
        .catch((res) => {
          console.log(res)
          this.$refs[this.gridId].tableData = []
        })
    },
    exportOne () {},
    imoportOne () {},
    deleteOne () {},
    publishBill (row) {
      this.$http({
        url: '/api-sup-ce/deliver/deliverPlan/getDeliverPlanStatus',
        method: 'GET',
        params: { id: row.deliverPlanId },
        loading: true
      }).then((res) => {
        this.$message({
          type: 'success',
          message: res.message
        })
        this.getQuerydata()
      })
    },
    readOne (row) {
      // 查看--只读状态
      const tab = {
        component: vendorDeliverPlanDetail,
        params: {
          flag: 'readOnly',
          row,
          showType: 'readOnly'
        },
        title: row.deliverPlanNum,
        name: 'vendorDeliverPlanDetail' + row.deliverPlanNum
      }
      this.$emit('tab-add', tab)
    },
    editOne (row) {
      // 编辑状态
      const tab = {
        component: vendorDeliverPlanDetail,
        params: {
          flag: 'edit',
          row
        },
        title: row.deliverPlanNum,
        name: 'vendorDeliverPlanDetail' + row.deliverPlanNum
      }
      this.$emit('tab-add', tab)
    },
    addOrder (showType = '') {
      // 编辑tab
      const tab = {
        component: vendorDeliverPlanDetail,
        params: {
          flag: 'add',
          row: {},
          showType
        },
        title: this.$t('orderMod.buyerOrderSynergy.newOrder'),
        name: 'vendorDeliverPlanDetail'
      }
      this.$emit('tab-add', tab)
    },
    dateExchange () {},
    cancelOne () {}
  }
}
</script>
<style scoped lang="scss">
.the_vendorDeliverPlanList_wrapper {
  .the_follow_tender_dialog .el-row {
    margin-bottom: 11px;
    .el-col > span {
      padding-right: 11px;
    }
  }
}
</style>
