<template>
  <el-container
    class="flex-container-notab the_expertDatabase_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        form-label-width="120px"
        @getFormData="getQueryData"
      />

      <MainHeader :l-span="24">
        <template slot="left">
          <!--自定义导出-->
          <ExportExcel
            page-url="/api-inq/price/priceLibrary/listPage"
            export-mode="front"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            :filter-params="queryParam"
            :timeout="10000000"
          />
        </template>
      </MainHeader>

      <TableView
        ref="list"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        row-key="priceLibraryId"
        reserve-selection
        url="/api-inq/price/priceLibrary/vendor/listPage"
      >
        <!-- <template #ceeaArrivalPlace="{ scope }">
          <RenderAsyncText :cell-value="scope.row.ceeaArrivalPlace" />
        </template> -->
      </TableView>

      <!-- 阶梯价 -->
      <LadderPriceDialog
        v-if="ladderPriceDialogVisible"
        :visible.sync="ladderPriceDialogVisible"
        :view-row="editRow"
      />

      <!--付款条款-->
      <PaymentTypeDialog
        v-if="paymentTypeDialogVisible"
        :visible.sync="paymentTypeDialogVisible"
        :edit-row="{ paymentList: globalApprovalBiddingItemPaymentTermList }"
        readonly
        business-type="PRICE"
      />
    </el-main>
  </el-container>
</template>

<script>
import { parseTime } from '@/utils'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import RenderAsyncText from 'lib@/components/provice-city/renderAsyncText'
import ExportExcel from 'lib@/components/export-excel'
import PaymentTypeDialog from 'lib@/composition/origin/paymentTypeDialog'
import LadderPriceDialog from './ladderPriceDialog'

export default {
  name: 'PriceCatalog',

  components: {
    TableView,
    MainHeader,
    FormWrapper,
    RenderAsyncText,
    ExportExcel,
    PaymentTypeDialog,
    LadderPriceDialog
  },

  data () {
    return {
      paymentTypeDialogVisible: false,
      globalApprovalBiddingItemPaymentTermList: [],
      queryParam: {},
      tableHeader: [],
      tableData: [],
      preArr: [
        // 物料编码
        {
          prop: 'itemCode',
          label: () => this.$t('bidMod.itemCode'),
          type: 'quicksearch',
          showKey: 'materialCode',
          name: 'scc_base_material_item'
        },
        // 品类
        {
          prop: 'categoryName',
          label: () => this.$t('common.category'),
          type: 'quicksearch',
          showKey: 'categoryName',
          name: 'scc_base_purchase_category2'
        },
        // 业务实体
        {
          prop: 'ceeaOrgId',
          label: () => this.$t('bid_mod.businessEntity'),
          type: 'OUorganizationSelector'
        },
        // 库存组织
        {
          prop: 'ceeaOrganizationId',
          parentId: 'ceeaOrgId',
          label: () => this.$t('bid_mod.inv'),
          type: 'INVorganizationSelector'
        }
      ],
      dictCodes: {
        ceeaIfUse: 'YES_OR_NO',
        unit: 'unit',
        currencyCode: 'currency',
        taxRate: 'tax',
        ceeaAllocationType: 'CeeaQuotaAllocationType',
        isLadder: 'YES_OR_NO',
        priceType: 'DMAND_LINE_TYPE'
      },
      ladderPriceDialogVisible: false,
      editRow: null
    }
  },

  created () {
    this.tableHeader = [
      // 价格库编号
      {
        prop: 'priceLibraryNo',
        label: this.$t('bidMod.priceLibraryNo'),
        minWidth: 140
      },
      // 物料编码
      {
        prop: 'itemCode',
        label: this.$t('bidMod.itemCode'),
        minWidth: 120
      },
      // 物料名称
      {
        prop: 'itemDesc',
        label: this.$t('bidMod.itemDesc'),
        minWidth: 150
      },
      // 品类
      {
        prop: 'categoryName',
        label: this.$t('common.category'),
        minWidth: 100
      },
      // 最小订单量
      {
        prop: 'minOrderQuantity',
        label: this.$t('bidMod.minOrderQuantity'),
        minWidth: 150
      },
      // 合同编码
      {
        prop: 'contractCode',
        label: this.$t('contractMod.contractCode'),
        minWidth: 150
      },
      // 合同名称
      {
        prop: 'contractName',
        label: this.$t('contractMod.contractName'),
        minWidth: 150
      },
      // 单位
      {
        prop: 'unit',
        label: this.$t('bidMod.unit'),
        minWidth: 70,
        formattor: val => this.$getDictLabel('unit', val)
      },
      // 业务实体
      {
        prop: 'ceeaOrgName',
        label: this.$t('bid_mod.businessEntity'),
        minWidth: 150
      },
      // 库存组织
      {
        prop: 'ceeaOrganizationName',
        label: this.$t('bid_mod.inv'),
        minWidth: 150
      },
      // 到货地点
      // {
      //   prop: 'ceeaArrivalPlace',
      //   label: this.$t('contractMod.arrivalPlace'),
      //   minWidth: 150,
      //   slot: 'ceeaArrivalPlace',
      //   showType: 'slot'
      // },
      {
        prop: 'ceeaArrivalPlaceStr',
        label: this.$t('contractMod.arrivalPlace'),
        minWidth: 150
      },
      // 价格类型
      {
        prop: 'priceType',
        label: this.$t('bid_mod.priceType'),
        minWidth: 100,
        formattor: val => this.$getDictLabel('DMAND_LINE_TYPE', val)
      },
      // 未税单价
      {
        prop: 'notaxPrice',
        label: this.$t('bid_mod.untaxedPrice'),
        minWidth: 100
      },
      // 税率
      {
        prop: 'taxRate',
        align: 'right',
        label: this.$t('bidMod.taxRate'),
        minWidth: 100,
        formattor: val => this.$getDictLabel('tax', val)
      },
      // 含税单价
      {
        prop: 'taxPrice',
        label: this.$t('bid_mod.taxUnitPrice'),
        minWidth: 100
      },
      // 价格执行有效期自
      {
        prop: 'effectiveDate',
        label: this.$t('bid_mod.priceStartTime'),
        minWidth: 150,
        formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      },
      // 价格执行有效期至
      {
        prop: 'expirationDate',
        label: this.$t('bid_mod.priceEndTime'),
        minWidth: 150,
        formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      },
      // 是否阶梯报价
      {
        prop: 'isLadder',
        label: this.$t('bidMod.isLadder'),
        minWidth: 120,
        showType: 'button',
        btnStyle: 'text',
        show: row => row.isLadder === 'Y',
        formattor: val => this.$getDictLabel('YES_OR_NO', val),
        callback: row => this.openLadderPriceDialog(row)
      },
      // 付款条款
      {
        prop: 'paymentProvision',
        label: this.$t('paymentType.paymentType'),
        minWidth: 100,
        showType: 'button',
        btnStyle: 'text',
        formattor: () => this.$t('common.view'),
        callback: row => this.openPaymentTypeDialog(row)
      },
      // 贸易术语
      {
        prop: 'tradeTerm',
        label: this.$t('bidMod.tradeTerm'),
        minWidth: 150,
        formattor: val => this.$getDictLabel('trade_clause', val)
      },
      // 创建时间
      {
        prop: 'creationDate',
        label: this.$t('bidMod.creationDate'),
        minWidth: 100,
        formattor: (val) => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      },
      // 更新时间
      {
        prop: 'lastUpdateDate',
        label: this.$t('bidMod.updateTime'),
        minWidth: 100,
        formattor: (val) => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      }
    ]
    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  methods: {
    /* 查询列表数据 */
    getQueryData (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    /* 打开付款条款弹窗 */
    openPaymentTypeDialog (row) {
      this.globalApprovalBiddingItemPaymentTermList = row.priceLibraryPaymentTermList
      this.paymentTypeDialogVisible = true
    },

    /* 打开阶梯价弹窗 */
    openLadderPriceDialog (row) {
      this.editRow = row
      this.ladderPriceDialogVisible = true
    }
  }
}
</script>
