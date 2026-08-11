<template>
  <div class="approval-report">
    <!--筛选条件-->
    <div style="padding: 10px 0">
      <span>{{ $t("bidMod.reportFilterCondition") }}</span>
      <el-select v-model="currentVendorKey">
        <el-option
          v-for="item in vendorKeyOptions"
          :key="item.key"
          :label="item.label"
          :value="item.key"
        />
      </el-select>
    </div>

    <!--筛选表格-->
    <el-table
      ref="approvalReportTable"
      :data="approvalReportTable"
      border
      stripe
      highlight-current-row
      style="width: 100%"
      @row-click="rowClick"
    >
      <el-table-column
        v-for="column in approvalReportTableColumn"
        :key="column.prop"
        :fixed="column.fixed"
        :prop="column.prop"
        :min-width="column.width + 'px'"
        :label="column.label"
        show-overflow-tooltip
      >
        <template v-slot="scope">
          <!--付款条款-->
          <el-button
            v-if="currentVendorKey === 'paymentTerms' && /^vendor_/.test(column.prop)"
            type="text"
            @click.stop="openPaymentTypeDialog(scope.row, column.prop)"
          >
            {{ $t("common.view") }}
          </el-button>

          <!--阶梯报价 / 公式报价 简易询价-->
          <el-button
            v-else-if="pageType.isInquiry && currentVendorKey === 'orderNotaxPrice' &&
              (scope.row[`${column.prop}_isLadder`] || scope.row[`${column.prop}_isFormula`])
            "
            type="text"
            @click.stop="openInqPriceDialog({
              ladder: scope.row[`${column.prop}_isLadder`],
              formula: scope.row[`${column.prop}_isFormula`]
            }, scope.row)"
          >
            {{ scope.row[column.prop] }}
          </el-button>

          <!--公式报价 / 模型报价 招标-->
          <el-button
            v-else-if="((pageType.isBiding || pageType.isBargain) && currentVendorKey === 'orderNotaxPrice') &&
              (scope.row[`${column.prop}_isModel`] || scope.row[`${column.prop}_isFormula`])
            "
            type="text"
            @click.stop="openPriceDialog({
              model: scope.row[`${column.prop}_isModel`],
              formula: scope.row[`${column.prop}_isFormula`]
            })"
          >
            {{ scope.row[column.prop] }}
          </el-button>

          <!--模板报价 招标/简易询价-->
          <el-button
            v-else-if="((pageType.isInquiry || pageType.isBiding) && currentVendorKey === 'orderNotaxPrice') && scope.row[`${column.prop}_isTemplate`]"
            type="text"
            @click.stop="openTemplatePriceDialog({ template: scope.row[`${column.prop}_isTemplate`] })"
          >
            {{ scope.row[column.prop] }}
          </el-button>

          <!--税率-->
          <span v-else-if="currentVendorKey === 'taxKey'">
            {{ $getDictLabel('tax', scope.row[column.prop]) }}
          </span>
          <span v-else>{{ scope.row[column.prop] }}</span>
        </template>
      </el-table-column>
    </el-table>

    <CPagination
      :total="approvalReportData.length"
      :page-num="currentPage"
      :page-sizes="[10]"
      :page-size="pageSize"
      @current-change="handleCurrentChange"
      @size-change="handleSizeChange"
    />

    <!--汇总表格-->
    <el-table
      :data="approvalReportSummaryTable"
      border
      stripe
      style="width: 100%; margin-bottom: 20px"
    >
      <el-table-column
        v-for="column in approvalReportSummaryTableColumn"
        :key="column.prop"
        :prop="column.prop"
        :min-width="column.width + 'px'"
        :label="column.label"
        show-overflow-tooltip
      >
        <template v-slot="scope">
          <el-button
            v-if="scope.row.key === 'paymentTerms' && column.prop !== 'collectItem' && currentQuoteInfos.length"
            type="text"
            @click.stop="openPaymentTypeDialog({ quoteInfos: currentQuoteInfos }, column.prop)"
          >
            {{ $t('common.view') }}
          </el-button>
          <span v-else>{{ scope.row[column.prop] }}</span>
        </template>
      </el-table-column>
    </el-table>

    <!--折线图-->
    <VendorQoutePriceLineChart
      v-if="currentItem.itemDesc && currentItemPriceNodes"
      :vendor-price-nodes="currentItemPriceNodes"
      :item-desc="currentItem.itemDesc"
    />

    <!--付款条款-->
    <PaymentTypeDialog
      v-if="paymentTypeDialogVisible"
      :visible.sync="paymentTypeDialogVisible"
      :business-type="approvalHeader.sourceType"
      :edit-row="viewRow"
      readonly
    />

    <!--查看阶梯价 暂时只有简易询价有阶梯价-->
    <LadderPriceDetail
      v-if="ladderPriceDialogVisible"
      :visible.sync="ladderPriceDialogVisible"
      :business-type="BUSINESS_TYPE_ENUM.INQUIRY"
      :edit-row="viewRow"
      :show-info="false"
      page-type="quote"
      readonly
    />

    <!--查看公式报价 简易询价-->
    <InqFormulaPriceDialog
      v-if="inqFormulaPriceDialogVisible"
      :visible.sync="inqFormulaPriceDialogVisible"
      :view-row="viewRow"
    />

    <!--查看公式报价 招标-->
    <BidFormulaPriceDialog
      v-if="bidFormulaPriceDialogVisible"
      :visible.sync="bidFormulaPriceDialogVisible"
      :view-row="viewRow"
      :show-all-supplier="false"
    />

    <!--查看公式报价 项目式询价-->
    <BrgFormulaPriceDialog
      v-if="brgFormulaPriceDialogVisible"
      :visible.sync="brgFormulaPriceDialogVisible"
      :view-row="viewRow"
      :show-all-supplier="false"
    />

    <!-- 模型报价 招标 -->
    <BidModelQuoteDialog
      v-if="bidModelQuoteDialogVisible"
      :visible.sync="bidModelQuoteDialogVisible"
      :is-proxy-quote="false"
      is-read-only-by-buyer
      :source-line="viewRow"
    />

    <!-- 模型报价 项目式询价 -->
    <BrgModelQuoteDialog
      v-if="brgModelQuoteDialogVisible"
      :visible.sync="brgModelQuoteDialogVisible"
      :is-proxy-quote="false"
      is-read-only-by-buyer
      :source-line="viewRow"
    />

    <!--模板报价-->
    <TemplatePriceDialog
      v-if="templatePriceDialogVisible"
      :visible.sync="templatePriceDialogVisible"
      :business-type="approvalHeader.sourceType"
      :query-params="queryParams"
      readonly
    />
  </div>
</template>

<script>
/**
 * 报表区域
 */
import { isIE, isIE11 } from 'lib@/utils/validate'
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import Big from 'big.js'
import CPagination from 'lib@/components/c-pagination'
import InqFormulaPriceDialog from 'lib@/composition/inquiryBySimple/formulaPriceDialog'
import BidFormulaPriceDialog from 'lib@/composition/biddingManagement/formulaPriceDialog'
import BrgFormulaPriceDialog from 'lib@/composition/bargain/formulaPriceDialog'
import BidModelQuoteDialog from 'lib@/composition/biddingManagement/modelQuote/modelQuoteDialog'
import BrgModelQuoteDialog from 'lib@/composition/bargain/modelQuote/modelQuoteDialog'
import VendorQoutePriceLineChart from 'lib@/composition/origin/vendorQoutePriceLineChart'
import PaymentTypeDialog from 'lib@/composition/origin/paymentTypeDialog'
import LadderPriceDetail from 'lib@/composition/origin/ladderPrice'
import TemplatePriceDialog from 'lib@/composition/quoteTemplate/templatePriceDialog'

export default {
  name: 'ApprovalReport',

  components: {
    CPagination,
    InqFormulaPriceDialog,
    BidFormulaPriceDialog,
    BrgFormulaPriceDialog,
    BidModelQuoteDialog,
    BrgModelQuoteDialog,
    VendorQoutePriceLineChart,
    PaymentTypeDialog,
    LadderPriceDetail,
    TemplatePriceDialog
  },

  props: {
    showReport: Boolean,
    attrsParamsRow: Object,
    approvalHeader: Object,
    pageType: Object
  },

  data () {
    return {
      // 默认含税单价
      currentVendorKey: '',
      pageSize: 10,
      currentPage: 1,
      currentDemandRowId: null,
      currentDemandLineId: null,
      viewRow: null,
      editIndex: '',
      demandLineReports: [],
      vendorBiddingSummaryReports: [],
      priceNodes: null,
      itemQuoteInfos: [],
      vendorQuoteInfos: [],
      currentItem: {},
      currentQuoteInfos: [],
      ladderPriceDialogVisible: false,
      inqFormulaPriceDialogVisible: false,
      bidFormulaPriceDialogVisible: false,
      brgFormulaPriceDialogVisible: false,
      bidModelQuoteDialogVisible: false,
      brgModelQuoteDialogVisible: false,
      currentItemPriceNodes: null,
      paymentTypeBusinessType: '',
      paymentTypeDialogVisible: false,
      BUSINESS_TYPE_ENUM,
      templatePriceDialogVisible: false,
      queryParams: null
    }
  },

  computed: {
    /* 前端分页 */
    approvalReportTable () {
      return this.approvalReportData.slice((this.currentPage - 1) * this.pageSize, this.currentPage * this.pageSize)
    },

    /* 报表筛选条件 */
    vendorKeyOptions () {
      const list = [
        // o 价格得分
        { key: 'priceScore', label: this.$t('bid_mod.priceScore') },
        // o 综合得分
        { key: 'compositeScore', label: this.$t('perfMod.scoreAll') },
        // o 付款条件
        { key: 'paymentTerms', label: this.$t('paymentType.paymentTerm') },
        // 税率
        { key: 'taxKey', label: this.$t('bid_mod.taxRate') }
      ]
      if (this.pageType.isInquiry) {
        // 简易询价 加入绩效得分 未税单价
        list.splice(
          2,
          0,
          { key: 'performanceScore', label: this.$t('perfMod.perScore') },
          { key: 'orderNotaxPrice', label: this.$t('bidMod.quotenotaxPrice2') }
        )
      }
      if (this.pageType.isBiding) {
        // 招标 加入未税单价
        list.splice(
          2,
          0,
          { key: 'orderNotaxPrice', label: this.$t('bidMod.quotenotaxPrice2') }
        )
      }
      if (this.pageType.isBargain) {
        // 项目式询价 加入未税单价
        list.splice(
          2,
          0,
          { key: 'orderNotaxPrice', label: this.$t('bidMod.quotenotaxPrice2') }
        )
      }
      if (this.pageType.isCompetition) {
        // 竞价 加入未税单价
        list.splice(
          2,
          0,
          { key: 'orderNotaxPrice', label: this.$t('bidMod.quotenotaxPrice2') }
        )
      }
      if (this.pageType.isAuct) {
        // 竞价pro 加入未税单价
        list.splice(
          2,
          0,
          { key: 'orderNotaxPrice', label: this.$t('bidMod.quotenotaxPrice2') }
        )
      }
      return list
    },

    /* 报表行 */
    approvalReportTableColumn () {
      const columns = [
        // t 业务实体
        {
          fixed: true,
          prop: 'orgOuName',
          label: this.$t('bid_mod.businessEntity'),
          width: '120'
        },
        // t 库存组织
        {
          fixed: true,
          prop: 'orgInvName',
          label: this.$t('bidMod.affairsInventoryOrg'),
          width: '100'
        },
        // t 物料编码
        {
          fixed: true,
          prop: 'itemCode',
          label: this.$t('bidMod.targetNum'),
          width: '120'
        },
        // t 物料名称
        {
          fixed: true,
          prop: 'itemDesc',
          label: this.$t('common.materialName'),
          width: '120'
        },
        // t 需求数量
        {
          prop: 'demandQuantity',
          label: this.$t('bidMod.demandQuantity2'),
          width: '120'
        },
        // t 历史价格
        // {
        //   prop: 'latestTaxPrice',
        //   label: this.$t('bidMod.historyTaxPrice'),
        //   width: '120'
        // }
      ]
      if (this.vendorQuoteInfos.length) {
        // 供应商筛选条件展示行
        this.vendorQuoteInfos.forEach(item => {
          columns.push({
            prop: `vendor_${item.vendorId}`,
            label: item.vendorName,
            width: this.getTextWidth(item.vendorName)
          })
        })

        // 中标供应商展示行
        this.vendorQuoteInfos.forEach((item, index) => {
          // 中标供应商
          columns.push({
            prop: `win_vendor_${item.vendorId}`,
            label: this.$t('bidMod.wonBidVendor') + `${index + 1}`,
            width: '120'
          })
          // 中标金额
          columns.push({
            prop: `win_vendor_${item.vendorId}_discountPrice`,
            label: this.$t('bidMod.wonBidAmount'),
            width: '120'
          })
        })
      }
      return columns
    },

    /* 报表所有数据 */
    approvalReportData () {
      if (this.itemQuoteInfos.length === 0) {
        return []
      }
      return this.itemQuoteInfos.map(item => {
        let {
          orgInvName,
          orgOuName,
          itemCode,
          itemDesc,
          itemId,
          unit,
          demandQuantity
        } = item.item
        if (!itemId) {
          // 无物料ID不显示物料编码
          itemCode = ''
        }
        // 供应商筛选条件展示行
        const vendorColumnData = item.quoteInfos.reduce((last, current) => {
          last[`vendor_${current.vendorId}`] = current[this.currentVendorKey]
          if (this.pageType.isInquiry) {
            // 简易询价
            if (current.isLadder === 'Y') {
              // 阶梯报价
              last[`vendor_${current.vendorId}_isLadder`] = {
                inquiryItemId: current.inquiryItemId,
                quoteItemId: current.quoteItemId
              }
            }
            if (current.isFormula === 'Y') {
              // 公式报价
              last[`vendor_${current.vendorId}_isFormula`] = {
                inquiryItemId: current.inquiryItemId,
                quoteItemId: current.quoteItemId,
                formulaValue: current.formulaValue,
                // 币种，用于基材价格根据汇率转换
                quoteCurrency: current.quoteCurrency
              }
            }
            if (item.item.isTemplate === 'Y') {
              // 模板报价
              last[`vendor_${current.vendorId}_isTemplate`] = {
                inquiryId: current.inquiryId,
                inquiryItemId: current.inquiryItemId,
                vendorId: current.vendorId
              }
            }
          }
          if (this.pageType.isBiding || this.pageType.isBargain) {
            // 招标 || 项目式询价
            if (item.item.isFormula === 'Y') {
              // 公式报价
              last[`vendor_${current.vendorId}_isFormula`] = {
                requirementLineId: current.requirementLineId,
                orderLineId: current.orderLineId,
                formulaValue: current.formulaValue,
                targetNum: current.itemCode,
                targetDesc: current.itemDesc,
                quantity: current.quantity,
                // 币种，用于基材价格根据汇率转换
                currencyType: current.currencyType
              }
            }
            // 旧的模型报价，暂用于项目式询价，理论上模型报价接近废弃
            if (item.item.isModel === 'Y' && this.pageType.isBargain) {
              // 项目式询价-模型报价
              last[`vendor_${current.vendorId}_isModel`] = {
                requirementLineId: current.requirementLineId,
                orderLineId: current.orderLineId
              }
            }

            // 招标后端有问题，后端直接废弃了模型报价，但是又不改字段直接复用isModel
            if (item.item.isModel === 'Y' && this.pageType.isBiding) {
              // 招标-模板报价
              last[`vendor_${current.vendorId}_isTemplate`] = {
                requirementLineId: current.requirementLineId,
                vendorId: current.vendorId
              }
            }
          }
          return last
        }, {})

        // 中标供应商展示行  winAmount  standardNotaxPrice
        const winVendorColumnData = this.vendorQuoteInfos.reduce((last, current) => {
          last[`win_vendor_${current.vendorId}`] = current.vendorName
          // 找到物料报价list中该供应商对这个物料的报价
          const winInfo = item.quoteInfos.find(winItem => winItem.vendorId === current.vendorId)
          if (winInfo) {
            const bigQuoteQuantity = winInfo.winAmount ? Big(winInfo.winAmount) : ''
            const bigPrice = winInfo.standardNotaxPrice ? Big(winInfo.standardNotaxPrice) : ''
            last[`win_vendor_${current.vendorId}_discountPrice`] = bigQuoteQuantity && bigPrice ? bigPrice.times(bigQuoteQuantity) : ''
          }
          return last
        }, {})

        return {
          orgInvName,
          orgOuName,
          itemCode,
          itemDesc,
          itemId,
          unit,
          demandQuantity,
          latestTaxPrice: item.latestNotaxPrice,
          item: item.item,
          quoteInfos: item.quoteInfos || [],
          ...vendorColumnData,
          ...winVendorColumnData
        }
      })
    },

    /* 汇总报表行 */
    approvalReportSummaryTableColumn () {
      const columns = [
        // t 汇总项
        {
          prop: 'collectItem',
          label: this.$t('bidMod.collectItem'),
          width: '120'
        }
      ]
      if (this.vendorQuoteInfos.length) {
        this.vendorQuoteInfos.forEach(row => {
          columns.push({
            prop: `vendor_${row.vendorId}`,
            minWidth: this.getTextWidth(`vendor_${row.vendorId}`),
            label: row.vendorName
          })
        })
      }
      return columns
    },

    /* 汇总报表 */
    approvalReportSummaryTable () {
      if (this.vendorQuoteInfos.length) {
        const keys2Rows = ['quoteTotalTaxPrice', 'winTotalTaxPrice']
        const keys3Rows = ['compositeScore', 'paymentTerms']
        const label2Rows = {
          // 报价总金额
          quoteTotalTaxPrice: this.$t('bidMod.quotationAmount'),
          // 中标总金额
          winTotalTaxPrice: this.$t('bidMod.bidAmount')
        }
        const label3Rows = {
          // 综合得分
          compositeScore: this.$t('perfMod.scoreAll'),
          // 付款条件
          paymentTerms: this.$t('paymentType.paymentTerm')
        }
        // 编排1，2行数据
        const data2Rows = keys2Rows.map(key => {
          const row = this.vendorQuoteInfos.reduce((last, current) => {
            // 这个key跟表头配置一致
            last[`vendor_${current.vendorId}`] = current[key]
            return last
          }, {})
          row.collectItem = label2Rows[key]
          row.key = key
          return row
        })
        // 编排3行后的数据
        const data3Rows = keys3Rows.map(key => {
          const row = this.currentQuoteInfos.reduce((last, current) => {
            // 这个key跟表头配置一致
            last[`vendor_${current.vendorId}`] = current[key]
            return last
          }, {})
          row.collectItem = label3Rows[key]
          row.key = key
          return row
        })
        // 合并
        return [...data2Rows, ...data3Rows]
      }
      return []
    }
  },

  watch: {
    pageType: {
      handler () {
        if (this.approvalHeader.sourceId) {
          this.getApprovalReportData()
        }
      },
      immediate: true,
      deep: true
    }
  },

  methods: {
    /* 查询报表数据 */
    getApprovalReportData () {
      this.currentVendorKey = 'orderNotaxPrice'
      if (this.pageType.isInquiry) {
        // 简易询价
        this.getGeneratePriceFilterReportForInquiry()
      }
      if (this.pageType.isBiding) {
        // 招标
        this.getGeneratePriceFilterReportForBiding()
      }
      if (this.pageType.isBargain) {
        // 项目式询价
        this.getGeneratePriceFilterReportForBargain()
      }
      if (this.pageType.isCompetition) {
        // 竞价
        this.generatePriceFilterReportForComp()
      }
    },
    /* 查询简易询价报表数据 */
    // /api-sou/buyer/inq/select/generatePriceReport/
    getGeneratePriceFilterReportForInquiry () {
      this.$http({
        url: `/api-sou/buyer/inq/select/generatePriceReport/${this.approvalHeader.sourceId}`,
        method: 'GET',
        loading: true
      }).then(data => {
        this.arrangeReportForBidAndBrg(data, 'INQ')
      })
    },

    /* 查询招标报表数据 */
    // /api-sou/buyer/bid/select/generatePriceReport/
    getGeneratePriceFilterReportForBiding () {
      this.$http({
        url: `/api-sou/buyer/bid/select/generatePriceReport/${this.approvalHeader.sourceId}`,
        method: 'GET',
        loading: true
      }).then(data => {
        this.arrangeReportForBidAndBrg(data, 'BID')
      })
    },

    /* 查询项目式询价报表数据 跟招标基本一致 */
    // /api-sou/buyer/brg/select/generatePriceReport/{sourceId}
    getGeneratePriceFilterReportForBargain () {
      this.$http({
        url: `/api-sou/buyer/brg/select/generatePriceReport/${this.approvalHeader.sourceId}`,
        method: 'GET',
        loading: true
      }).then(data => {
        this.arrangeReportForBidAndBrg(data, 'BRG')
      })
    },

    /* 编排招标 or 项目式询价报表数据，基本一致 */
    arrangeReportForBidAndBrg (resData, type) {
      if (resData && resData.data) {
        this.priceNodes = resData.data.priceNodes || {}
        this.itemQuoteInfos = (resData.data.souItemInfos || []).map(item => {
          // 重置字段名为共用的字段名
          let newItemData = {}
          if (item.souItem) {
            newItemData = {
              ...(item.souItem || {}),
              itemCode: item.souItem.itemCode, // 物料编码
              itemDesc: item.souItem.itemDesc, // 物料名称
              itemId: item.souItem.itemId, // 物料ID
              demandQuantity: item.souItem.requireQuantity // 采购需求数量
            }
          }
          const newItem = {
            ...item,
            item: newItemData,
            quoteInfos: (item.orderInfos || []).map(orderInfo => {
              return {
                ...orderInfo,
                // 映射供应商中标物料单价和数量
                winVendorQuoteQuantity: orderInfo.winAmount,
                winVendorPrice: orderInfo.orderNotaxPrice // orderInfo[type === 'BID' ? 'bidNotaxPrice' : 'brgNotaxPrice']
              }
            })
          }
          delete item.souItem
          delete item.orderInfos
          return newItem
        })
        this.vendorQuoteInfos = (resData.data.vendorOrderInfos || []).map(item => {
          // 重置字段名为共用的字段名
          return {
            ...item,
            quoteTotalTaxPrice: item.standardTaxTotalPrice, // 报价总金额(含税)
            winTotalTaxPrice: item.winTaxTotalPrice // 中标总金额(含税)
          }
        })

        this.setCurrentRow()
      }
    },

    /* 查询竞价 */
    generatePriceFilterReportForComp () {
      this.$http({
        url: `/api-sou/buyer/comp/select/generatePriceReport/${this.approvalHeader.sourceId}`,
        method: 'GET',
        loading: true
      }).then(data => {
        this.arrangeReportForBidAndBrg(data, 'COMP')
      })
    },

    /* 设置默认选中第一行物料 */
    setCurrentRow () {
      if ((this.itemQuoteInfos[0] || {}).item) {
        this.currentItem = this.itemQuoteInfos[0].item
        if (this.currentItem.itemId) {
          this.currentItemPriceNodes = this.priceNodes[this.currentItem.itemId]
        }
        this.currentQuoteInfos = this.itemQuoteInfos[0].quoteInfos || []
        this.$nextTick(() => {
          this.$refs.approvalReportTable.setCurrentRow(this.approvalReportData[0])
        })
      }
    },

    /* 报表表格行点击 */
    rowClick (row) {
      this.currentItem = row.item || {}
      if (this.currentItem.itemId) {
        this.currentItemPriceNodes = this.priceNodes[this.currentItem.itemId]
      }
      this.currentQuoteInfos = row.quoteInfos || []
    },

    /* 打开付款条件 */
    openPaymentTypeDialog (row, prop) {
      // 移除vendor_拿到供应商ID
      const rowObj = row.quoteInfos.find(item => item.vendorId.toString() === prop.slice(7))
      if (rowObj.paymentList && rowObj.paymentList.length > 0) {
        this.viewRow = {
          paymentList: rowObj.paymentList
        }
      } else {
        this.viewRow = {
          ...rowObj,
          paymentList: null
        }
      }
      this.paymentTypeDialogVisible = true
    },

    /* 打开报价弹窗 简易询价 */
    openInqPriceDialog ({ ladder, formula }, row) {
      this.viewRow = ladder || formula || null
      if (this.viewRow) {
        if (ladder) {
          // 阶梯报价
          if (row.unit) {
            // 单位code转name
            this.viewRow.unit = this.$getDictLabel('unit', row.unit)
          }
          this.ladderPriceDialogVisible = true
        }
        if (formula) {
          // 公式报价
          this.inqFormulaPriceDialogVisible = true
        }
      }
    },

    /* 打开报价弹窗 招标 项目式询价 */
    openPriceDialog ({ model, formula }) {
      this.viewRow = model || formula || null
      if (!this.viewRow) {
        return
      }

      if (this.pageType.isBiding) {
        // 招标
        if (model) {
          this.bidModelQuoteDialogVisible = true
        }
        if (formula) {
          this.bidFormulaPriceDialogVisible = true
        }
      }

      if (this.pageType.isBargain) {
        // 项目式询价
        if (model) {
          this.brgModelQuoteDialogVisible = true
        }
        if (formula) {
          this.brgFormulaPriceDialogVisible = true
        }
      }
    },

    /* 模板报价 */
    openTemplatePriceDialog ({ template }) {
      console.log(template)
      this.queryParams = template || null
      if (!this.queryParams) {
        return
      }

      this.templatePriceDialogVisible = true
    },

    /* 报表表格页码大小改变 */
    handleSizeChange (val) {
      this.currentPage = 1
      this.pageSize = val
    },

    /* 报表表格页码改变 */
    handleCurrentChange (val) {
      this.currentPage = val
    },

    /* 计算文本宽度 */
    getTextWidth (text) {
      let width = 0
      const html = document.createElement('span')
      html.innerText = text
      html.className = 'getTextWidth'
      document.querySelector('body').appendChild(html)
      width = document.querySelector('.getTextWidth').offsetWidth
      const geTextWidths = document.querySelector('.getTextWidth')
      if (geTextWidths.length) {
        Array.from(geTextWidths).forEach(i => this.removeElement(i))
      } else {
        this.removeElement(geTextWidths)
      }
      return `${width}`
    },

    /* 移除dom */
    removeElement (_element) {
      if (isIE11() || isIE()) {
        _element.removeNode(true)
      } else {
        const _parentElement = _element.parentNode
        if (_parentElement) {
          _parentElement.removeChild(_element)
        }
      }
    }
  }
}
</script>
