<template>
  <el-container
    class="flex-container warehousingAndReturnGoodsList"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        :pre-form-obj="preFormObj"
        form-label-width="120px"
        @getFormData="getQuerydata"
      />

      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <!-- 创建对账单 -->
          <AuthorityButton
            type="primary"
            @click="createStatement"
          >
            <!-- 新增对账单 -->
            {{ $t("accountMod.addStatement") }}
          </AuthorityButton>
          <AuthorityButton
            type="primary"
            @click="closeBill"
          >
            {{ $t('common.close') }}
          </AuthorityButton>
          <!-- 导出 -->
          <ExportExcel
            type="default"
            :page-url="tableUrl"
            :filter-params="filterParams"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            timeout="1000000"
            export-mode="front"
          />
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :bigData="true"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :auto-query="false"
        :url="tableUrl"
        :open-custom-table="true"
        :reserve-selection="true"
        :adeptMeiQl="true"
        :checkbox="true"
        :check-change="handleCurrentChange"
        :comActive="$attrs['changeTab']"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import ExportExcel from 'lib@/components/export-excel'
import { transformMQL } from 'lib@/utils/util'
import { parseTime } from '@/utils'

export default {
  name: 'WarehousingAndReturnGoodsList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      dictCodes: {
        type: 'WAREHOURING_RETURN_DETAIL'
      },
      tableUrl: '/api-sup-ce/api-ql/StorageReturn/query',
      gridId: 'list',
      pageSize: 15,
      preFormObj: {},
      queryParam: {},
      filterParams: {},
      selectList: [],
      tableData: [],
      preArr: [
        {
          prop: 'orderNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderNumber')
        },
        {
          prop: 'vendorId',
          label: () => this.$t('orderMod.buyerOrderSynergy.vendorName'),
          type: 'quicksearch',
          showKey: 'companyName',
          propKey: 'companyId',
          name: 'scc_sup_company_info_all'
        },
        {
          prop: 'itemCode',
          label: () => this.$t('orderMod.buyerOrderSynergy.materialCode'),
          type: 'quicksearch',
          showKey: 'materialCode',
          name: 'scc_base_material_item'
        },
        {
          prop: 'type',
          // label: '入库类型',
          label: () => this.$t('cusEntry.supplement20250121.storageType'),
          type: 'dict',
          code: 'WAREHOURING_RETURN_DETAIL'
        },
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
          prop: 'receiveOrderNo',
          // label: '入库单号'
          label: () => this.$t('cusEntry.orderMod.inboundReturnOrderNo')
        },
        {
          prop: 'warehousingDate',
          // label: '入库时间',
          label: () => this.$t('orderMods.warehouseTime'),
          type: 'daterange'
        },
        {
          prop: 'ifCreated',
          // label: '是否创建对账单',
          label: () => this.$t('cusEntry.supplement20250121.createStatement'),
          type: 'dict',
          code: 'YES_OR_NO'
        }
      ],
      tableHeader: [
        {
          prop: 'type',
          // label: '入库类型',
          label: () => this.$t('cusEntry.supplement20250121.storageType'),
          width: 120,
          formattor: val => this.$getDictLabel('WAREHOURING_RETURN_DETAIL', val)
        },
        {
          prop: 'itemCode',
          label: this.$t('purchaseDemand.itemCode'), // 物料编码
          width: 150
        },
        {
          prop: 'itemName',
          label: this.$t('purchaseDemand.itemName'), // 物料名称
          minWidth: 150
        },
        {
          prop: 'categoryName',
          label: this.$t('sourcingBuyer.categoryType'), // 物料品类
          width: 150
        },
        {
          prop: 'receiveNum',
          // label: '入库数量',
          label: () => this.$t('orderMod.warehouseReceiptQuantity'),
          width: 120
        },
        { prop: 'unit', label: this.$t('purchaseDemand.unitCode'), width: 100 },
        {
          prop: 'unitPriceExcludingTax',
          label: this.$t('contractMod.notaxPrice'),
          width: 120
        },
        {
          prop: 'taxAmount', // 数据库中不存在此字段，用于导出
          label: this.$t('contractMod.taxQuota'),
          // // 税额 = 含税总额 - 未税总额
          // formattor: (val, row) => {
          //   return row.unitPriceContainingTax * row.receiveNum - row.unitPriceExcludingTax * row.receiveNum
          // },
          width: 120
        },
        {
          prop: 'taxTotalAmount', // 数据库中不存在此字段，用于导出
          // label: '含税总额',
          label: () => this.$t('cusEntry.supplement20250121.totalAmountIncludingTax'),
          // // 含税总额 = 含税单价 * 入库数量
          // formattor: (val, row) => {
          //   return row.unitPriceContainingTax * row.receiveNum
          // },
          width: 120
        },
        {
          prop: 'currencyName',
          label: this.$t('bid_mod.currencyName'),
          width: 100
        },
        {
          prop: 'warehousingDate',
          // label: '入库时间',
          label: () => this.$t('orderMod.warehouseTime'),
          formattor: val => val ? parseTime(val, '{y}-{m}-{d}') : '',
          width: 120
        },
        {
          prop: 'receiveOrderNo',
          // label: '入库单号',
          label: () => this.$t('orderMod.warehouseReceiptNumber'),
          width: 120
        },
        {
          prop: 'orderNumber',
          label: this.$t('orderMod.buyerOrderSynergy.orderNumber'),
          width: 150
        },
        {
          prop: 'lineNum',
          label: this.$t('orderMod.orderLineNum'), // 订单行号
          width: 100
        },
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
          width: 150
        },
        {
          prop: 'vendorName',
          label: this.$t('purchaseDemand.vendorName'), // 供应商名称
          minWidth: 150
        },
        {
          prop: 'invoiceNoticeQty', // 数据库中不存在此字段，用于导出
          // label: '已对账数量',
          label: () => this.$t('cusEntry.supplement20250121.reconciledQuantity'),
          // // 已对账数量 = 入库数量 - 未对账数量
          // formattor: (val, row) => row.receiveNum - row.notInvoiceQuantity,
          width: 120
        },
        {
          prop: 'invoiceNoticeFlag', // 数据库中不存在此字段，用于导出
          // label: '是否已创建对账单',
          label: () => this.$t('cusEntry.supplement20250121.isStatementCreated'),
          // // 未对账数量 notInvoiceQuantity = 0, 是否已创建对账单 = 'Y'
          // formattor: (val, row) => {
          //   if (row.notInvoiceQuantity == 0) return '是'
          //   else if (row.notInvoiceQuantity > 0) return '否'
          // },
          width: 150
        },
        {
          prop: 'extClosedCause',
          // label: '关闭原因',
          label:  () => this.$t('qualitySynergy.closeReason'),
          width: 120
        }
      ]
    }
  },
  created () {
    this.preFormObj = { ifCreated: 'N' }
    this.$nextTick(() => {
      this.getQuerydata(this.preFormObj)
    })
  },
  methods: {
    getQuerydata (v) {
      let params = {}
      const { orderNumber, vendorId, itemCode, type, orgIdList = [], organizationIdList = [], receiveOrderNo, warehousingDate, ifCreated } = v || {}
      if (orderNumber) {
        params.orderNumber = { contains: orderNumber }
      }
      if (vendorId) {
        params.vendorId = { eq: vendorId }
      }
      if (itemCode) {
        params.itemCode = { contains: itemCode }
      }
      if (type) {
        params.type = { eq: type }
      }
      if (orgIdList.length > 0) {
        params.orgId = { in: orgIdList }
      }
      if (organizationIdList.length > 0) {
        params.organizationId = { in: organizationIdList }
      }
      if (receiveOrderNo) {
        params.receiveOrderNo = { contains: receiveOrderNo }
      }
      if (warehousingDate) {
        params.warehousingDate = { between: warehousingDate }
      }
      if (ifCreated === 'Y') {
        params.notInvoiceQuantity = { eq: 0 }
      } else if (ifCreated === 'N') {
        params.notInvoiceQuantity = { gt: 0 }
      }

      this.queryParam = {
        type: 'StorageReturn',
        action: 'query',
        payload: {
          filter: { ...params },
          page: {
            pageNum: 1,
            pageSize: 15,
            sort: 'lastUpdateDate desc'
          }
        },
        query: { '*': {} },
        lang: 'zh-cn',
        tree: true
      }
      this.filterParams = { meiqlPayload: this.queryParam }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.selectList = val
    },
    async closeBill () {
      if (this.selectList.length == 0) {
        // this.$message.error('请先勾选数据')
        this.$message.error(this.$t("cusEntry.supplement20250121.pleaseSelectDataFirst2"))
        return
      }
      if (this.selectList.some(item => item.notInvoiceQuantity == 0)) {
        // return this.$message.error('仅可勾选是否已创建对账单=否的单据')
        return this.$message.error(this.$t("cusEntry.supplement20250121.onlySelectableIfStatementCreatedIsNo"))
      }
      const prompt = await this.$prompt( this.$t("qualitySynergy.closeReason"), {   //关闭原因
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        closeOnClickModal: false,
        inputPattern: /\S{1,}/,
        // inputErrorMessage: '关闭原因必填'
        inputErrorMessage: this.$t("cusEntry.supplement20250121.closeReasonRequired")
      })
      if (!prompt) return

      const params = {
        warehousingReturnDetailIds: this.selectList.map(item => item.warehousingReturnDetailId),
        extClosedCause: prompt.value
      }
      const saveData = transformMQL.save('StorageReturn', [params], 'extClose')
      this.$http({
        url: '/api-sup-ce/api-ql/StorageReturn/extClose',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.$message.success(this.$t('common.success'))
          this.getQuerydata()
        }
      })
    },
    createStatement () {
      if (this.selectList.length == 0) {
        // this.$message.error('请先勾选数据')
        this.$message.error(this.$t("cusEntry.supplement20250121.pleaseSelectDataFirst2"))
        return
      }
      const params = { storageReturnIds: this.selectList.map(item => item.warehousingReturnDetailId) }
      const saveData = transformMQL.save('InvoiceNotice', [params], 'extCreateByStorageReturn')
      this.$http({
        url: '/api-sup-ce/api-ql/InvoiceNotice/extCreateByStorageReturn',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.$message.success(this.$t('common.success'))
          this.getQuerydata()
        }
      })
    }
  }
}
</script>
<style scoped lang="scss">
</style>
