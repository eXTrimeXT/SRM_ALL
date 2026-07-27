<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />

      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <!-- 导出 -->
          <ExportExcel
            v-loading
            :page-url="tableUrl"
            :filter-params="queryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            timeout="1000000"
            export-mode="front"
          />
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :auto-query="false"
        :url="tableUrl"
        :open-custom-table="true"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime, findMenuInfoByPath } from '@/utils'
import { downloadFileLinkByPost } from 'lib@/utils/file'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import ExportExcel from 'lib@/components/export-excel'
import { warehousReturnGoodsApi } from 'mods@/orderManagementSupplier/api'

export default {
  name: 'WarehousReturnGoodsVendorList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      dictCodes: {},
      currentRows: [],
      canOperate: false,
      tableUrl: '/api-sup-ce/order/warehousingReturnDetail/listPage',
      isOrderPage: true,
      headers: {},
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
          prop: 'type',
          label: this.$t('orderMod.transactionType'),
          type: 'dict',
          code: 'WAREHOURING_RETURN_DETAIL'
        },
        {
          prop: 'startTime',
          label: this.$t('orderMod.transactionDateFrom'),
          type: 'date'
        },
        {
          prop: 'endTime',
          label: this.$t('orderMod.transactionDateTo'),
          type: 'date'
        },
        { prop: 'receiveOrderNo', label: this.$t('orderMod.receiveOrderNo') },
        {
          prop: 'orgIdList',
          label: this.$t('purchaseDemand.businessEntity'),
          type: 'OUorganizationSelector',
          multiple: true
        },
        {
          prop: 'organizationIdList',
          label: this.$t('purchaseDemand.invOrg'),
          type: 'INVorganizationSelector',
          parentId: 'orgIdList',
          multiple: true
        },
        {
          prop: 'categoryKey',
          label: this.$t('purchaseDemand.materialCateSub'),
          type: 'quicksearch',
          showKey: 'categoryName',
          name: 'scc_base_purchase_category'
        },
        {
          prop: 'materialKey',
          label: () => this.$t('orderMod.buyerOrderSynergy.materialName'),
          type: 'quicksearch',
          showKey: 'materialName',
          name: 'scc_base_material_item'
        },
        {
          prop: 'orderNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderNumber')
        },
        {
          prop: 'requirementHeadNum',
          label: this.$t('purchaseDemand.purRequisitionNum')
        },
        {
          prop: 'deliveryNumber',
          label: this.$t('orderMod.buyerOrderSynergy.deliveryNumber')
        }
      ],
      queryParam: {},
      initHeader: [
        {
          prop: 'orgName',
          label: this.$t('oneStopShopping.businessEntity'), // 业务实体
          minWidth: 150
        },
        {
          prop: 'organizationName',
          label: this.$t('purchaseDemand.invOrg'), // 库存组织
          minWidth: 150
        },
        {
          prop: 'vendorCode',
          label: this.$t('purchaseDemand.vendorCode'), // 供应商编码
          width: 120
        },
        {
          prop: 'vendorName',
          label: this.$t('purchaseDemand.vendorName'), // 供应商名称
          minWidth: 150
        },
        {
          prop: 'receiveOrderNo',
          label: this.$t('orderMod.receiveOrderNo'), // 接收单号
          width: 100
        },
        {
          prop: 'type',
          label: this.$t('orderMod.transactionType'), // 事务处理类型
          width: 120,
          formattor: val => this.$getDictLabel('WAREHOURING_RETURN_DETAIL', val)
        },
        {
          prop: 'receiveOrderLineNo',
          label: this.$t('orderMod.receiveOrderLineNo'), // 接收行号
          width: 100
        },
        {
          prop: 'receiveDate',
          label: this.$t('orderMod.transactionDate'), // 事务处理日期
          width: 120,
          formattor (val, row) {
            return row.type === 'RECEIVE' ? parseTime(row.receiveDate, '{y}-{m}-{d}') : parseTime(row.returnToSupplierDate, '{y}-{m}-{d}')
          }
        },
        {
          prop: 'categoryName',
          label: this.$t('purchaseDemand.materialCateSub'), // 物料小类
          width: 100
        },
        {
          prop: 'itemCode',
          label: this.$t('purchaseDemand.itemCode'), // 物料编码
          width: 100
        },
        {
          prop: 'itemName',
          label: this.$t('purchaseDemand.itemName'), // 物料名称
          minWidth: 150
        },
        // 单位
        { prop: 'unit', label: this.$t('purchaseDemand.unitCode'), width: 80 },
        {
          prop: 'receiveNum',
          label: this.$t('orderMod.transactionsNumber'), // 事务处理数量
          width: 120
          /* formattor (val, row) {
            return row.type =='采购接收' ? row.receiveNum : row.returnToSupplierNum
          } */
        },
        {
          prop: 'requirementHeadNum',
          label: this.$t('purchaseDemand.purRequisitionNum'), // 采购申请单号
          width: 120
        },
        // 申请行号
        { prop: 'rowNum', label: this.$t('purchaseDemand.rowNum'), width: 100 },
        {
          prop: 'orderNumber',
          label: this.$t('purSettlementMod.orderNumber'), // 采购订单号
          width: 150
        },
        {
          prop: 'lineNum',
          label: this.$t('orderMod.orderLineNum'), // 订单行号
          width: 100
        },
        {
          prop: 'createdUserName',
          label: this.$t('purchaseDemand.createdBy1'), // 创建人
          width: 100
        },
        {
          prop: 'deliveryNumber',
          label: this.$t('orderMod.buyerOrderSynergy.deliveryNumber'), // 送货单号
          width: 150
        },
        {
          prop: 'deliveryLineNum',
          label: this.$t('orderMod.buyerOrderSynergy.deliveryLineNum'), // 送货单行号
          width: 160
        },
        {
          prop: 'creationDate',
          label: () => this.$t('orderMod.buyerOrderSynergy.creationDate'), // 创建日期
          width: 100,
          formattor: (val) => (val ? parseTime(val, '{y}-{m}-{d}') : '')
        }
      ]
    }
  },
  updated () {
    this.defaultTableHeader = this.tableHeader
  },
  created () {
    const menus = this.$store.getters.userInfo.menus
    this.menuInfo = findMenuInfoByPath(this.$route.path, menus) || {}
    this.tableHeader = this.initHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    handleSuccess () {
      this.getQuerydata()
    },
    syncFilterParams (values) {
      this.queryParam = values
    },
    exportList () {
      if (!this.queryParam.orgIdList || this.queryParam.orgIdList.length !== 1) {
        return this.$message.error(this.$t('orderMod.msgOrder[51]'))
      }
      let params = Object.assign({}, this.queryParam)
      downloadFileLinkByPost(
        '/api-sup-ce/order/warehousingReturnDetail/exportExcel',
        parseTime(new Date()) + this.$t('orderMod.inboundReturnDetailExp'),
        params
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },
    getQuerydata (v) {
      const { billDate, ...rest } = v || {}
      let params = { ...rest }
      if (billDate) {
        const [startSubmittedTime, endSubmittedTime] = billDate
        params = { ...rest, startSubmittedTime, endSubmittedTime }
      }
      this.queryParam = Object.assign({}, params)
      // this.queryParam = Object.assign({
      //     "version":1,
      //     "orderStatusList":["APPROVED","REFUSED","ACCEPT"]
      //   }, params) ;
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.canOperate = val && val.length
      this.currentRows = val
    },
    queryOrderList () {
      warehousReturnGoodsApi.orderList({})
        .then((res) => {
          this.$refs[this.gridId].tableData = res.data.list || []
        })
        .catch((res) => {
          console.log(res)
          this.$refs[this.gridId].tableData = []
        })
    }
  }
}
</script>
<style scoped lang="scss">
.the_purchaseOrderChangeList {
  .order-uploader {
    display: inline-block;
    margin: 0 10px;
  }
}
</style>
