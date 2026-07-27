<template>
  <!-- 招标评选 -->
  <el-container
    class="flex-container"
    direction="vertical"
    style="padding-top: 10px;"
  >
    <SearchForm :pre-form-obj="{ round: biddingBase.currentRound || 1 }" @get="getQueryData" />

    <!--按钮操作区-->
    <div class="btn-wrap">
      <!--智能评选-->
      <el-button
        type="primary"
        :disabled="readonly"
        @click="intelligentEvaluation"
      >
        {{ $t("bidMod.intelligentEvaluation") }}
      </el-button>

      <!-- 入围或淘汰 -->
      <el-dropdown style="margin-left: 8px;" @command="changeWinStatus">
        <el-button type="primary">
          {{ $t("bidMod.nextOrEliminate") }}
          <em class="el-icon-arrow-down el-icon--right" />
        </el-button>

        <el-dropdown-menu slot="dropdown">
          <!--入围-->
          <el-dropdown-item command="win" :disabled="readonly">
            {{ $t("bidMod.toNextRoundConfirm") }}
          </el-dropdown-item>
          <!--淘汰-->
          <el-dropdown-item command="loss" :disabled="readonly">
            {{ $t("bidMod.toEliminateConfirm") }}
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>

      <!--决标操作-->
      <el-dropdown style="margin: 0 8px;" @command="changeSelectStatus">
        <el-button type="primary">
          {{ $t("bidMod.bidAwardOperation") }}
          <em class="el-icon-arrow-down el-icon--right" />
        </el-button>
        <el-dropdown-menu slot="dropdown">
          <!--中标-->
          <el-dropdown-item command="win" :disabled="readonly">
            {{ $t("bid_mod.winTheBidding") }}
          </el-dropdown-item>
          <!--落标-->
          <el-dropdown-item command="loss" :disabled="readonly">
            {{ $t("bid_mod.lossTheBidding") }}
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>

      <!--公示本轮结果-->
      <el-button :disabled="readonly" @click="openResult">
        {{ $t("bidMod.toPublishDialog") }}
      </el-button>

      <!--提交中标数量修改-->
      <el-button
        v-if="quotaQuantityEditable"
        :disabled="readonly"
        @click="submitQuotaQuantity(true)"
      >
        {{ $t("bidMod.submitBidModify") }}
      </el-button>

      <!-- 生成寻源结果审批单 -->
      <el-button :disabled="readonly" @click="createPricingApproval">
        {{ $t("bidMod.newSourcingResult") }}
      </el-button>
    </div>

    <!--评选表格-->
    <TableView
      ref="tenderSelectionTable"
      :table-data="tableData"
      :table-header="tableHeader"
      row-key="orderItemId"
      :pre-query-data="queryParam"
      checkbox
      reserve-selection
      table-height="330px"
      :check-change="handleSelectionChange"
      :open-custom-table="false"
      :current-change="rowClick"
      :header-cell-style="tableHeaderCellStyle"
      :span-method="tableSpanMethod"
      :com-active="$attrs['changeTab']"
      :url="tableViewUrl"
      :big-data="false"
      class="evaluation-table"
      style="min-height:530px"
      :reserveSelection="true"
      @afterQuery="afterQuery"
    >
      <!--中标数量-->
      <template #winAmount="{ scope }">
        <el-input
          v-if="quotaQuantityEditable"
          v-model="scope.row.winAmount"
          @click.native.stop
        />
        <span v-else>{{ scope.row.winAmount }}</span>
      </template>

      <template #priceComparison="{ scope }">
        <el-button type="text" @click.stop="openPriceComparisonTab(scope.row)">
          比价
        </el-button>
      </template>

      <!--操作 公式报价 模型报价 比价-->
      <template #quotaInfo="{ scope }">
        <!--公式报价-->
        <el-button
          v-if="pricingType.isFormulaPricing"
          type="text"
          @click="openFormulaQuotaDialog(scope.row)"
        >
          {{ $t('bidMod.formulaQuota') }}
        </el-button>

        <!--模型报价-->
        <el-button
          v-if="pricingType.isModelPricing"
          type="text"
          @click="openModelQuoteReadDialog(scope.row)"
        >
          {{ $t('bidMod.modelQuote') }}
        </el-button>

        <el-button
          v-if="scope.row.isLadder === 'Y'"
          type="text"
          @click.stop="openLadderPriceDialog(scope.row)"
        >
          阶梯报价
        </el-button>

        <!-- 料费分离报价  -->
        <el-button
          v-if="pricingType.isSeparation"
          type="text"
          @click.stop="openSeparationPriceDialog(scope.row)"
        >
          报价明细
        </el-button>
      </template>
    </TableView>

    <!--比价折线图-->
    <div
      v-if="currentItemDesc && currentItemPriceNodes"
      v-loading="priceLineChartLoading"
      element-loading-background="rgba(0, 0, 0, 0.4)"
      class="price-line-chart-wrap"
    >
      <VendorQoutePriceLineChart :vendor-price-nodes="currentItemPriceNodes" :item-desc="currentItemDesc" />
    </div>

    <!-- 模型报价查看 -->
    <ModelQuoteReadDialog
      v-if="modelQuoteReadDialogVisible"
      :visible.sync="modelQuoteReadDialogVisible"
      :source-line="viewRow"
      readonly
    />

    <!--公式报价查看-->
    <FormulaPriceDialog
      v-if="formulaPriceDialogVisible"
      :visible.sync="formulaPriceDialogVisible"
      :view-row="viewRow"
    />

    <!--付款条款-->
    <PaymentTypeDialog
      v-if="paymentTypeDialogVisible"
      :visible.sync="paymentTypeDialogVisible"
      :business-type="BUSINESS_TYPE_ENUM.BIDDING_LTS"
      :edit-row="viewRow"
      readonly
    />

    <!--d 阶梯价-->
    <LadderPrice
      v-if="ladderPriceDialogVisible"
      :visible.sync="ladderPriceDialogVisible"
      :business-type="BUSINESS_TYPE_ENUM.BIDDING_LTS"
      page-type="quote"
      :edit-row="viewRow"
      readonly
    />

    <!-- 料费分离报价 -->
    <SeparationPriceDialog
      v-if="separationPriceDialogVisible"
      :visible.sync="separationPriceDialogVisible"
      :businessType="BUSINESS_TYPE_ENUM.BIDDING_LTS"
      role="buyer"
      :edit-row="viewRow"
      readonly
    />
  </el-container>
</template>

<script>
import { bidBuyerHttp } from 'modb@/bidding/api'
import {
  createPricingApproval,
  quoteSelectionTableHeaderCellStyle,
  quoteSelectionTableSpanMethod,
  targetNumReveal
} from 'lib@/composition/origin/composition'
import {
  SOU_ORDER_TYPE_ENUM,
  SOU_ORDER_WAY_ENUM,
  SOU_SCORE_RULE_TYPE_ENUM,
  BUSINESS_TYPE_ENUM
} from 'lib@/composition/origin/enum'
import { judgeManagement } from '@/library/composition/biddingLts/utils'
import TableView from 'lib@/components/Table/TableView'
import SearchForm from './evaluation/searchForm'
import ModelQuoteReadDialog from 'lib@/composition/biddingLts/modelQuote/modelQuoteReadDialog'
import FormulaPriceDialog from 'lib@/composition/biddingLts/formulaPriceDialog'
import VendorQoutePriceLineChart from 'lib@/composition/origin/vendorQoutePriceLineChart'
import priceComparison from 'lib@/composition/origin/priceComparison'
import priceComparisonModel from 'lib@/composition/origin/priceComparisonModel/index.vue'
import PaymentTypeDialog from 'lib@/composition/origin/paymentTypeDialog'
import LadderPrice from 'lib@/composition/origin/ladderPrice'
import SeparationPriceDialog from 'lib@/composition/quoteSeparation/templatePriceDialog'
import { meiqlCtrl } from '@/config/meiqlConfig'

export default {
  name: 'Evaluation',

  components: {
    SearchForm,
    ModelQuoteReadDialog,
    TableView,
    FormulaPriceDialog,
    VendorQoutePriceLineChart,
    PaymentTypeDialog,
    LadderPrice,
    SeparationPriceDialog
  },

  props: {
    projectStatus: {
      // 招标状态
      type: String,
      default: ''
    },
    createApprovalStatus: {
      // 审批状态
      type: String,
      default: ''
    },
    biddingBase: {
      type: Object,
      default: () => ({})
    },
    pricingType: {
      type: Object,
      required: true
    },
    isActiveMenu: {
      type: Boolean,
      required: true
    }
  },

  data () {
    return {
      tableViewUrl: bidBuyerHttp.select.listPageUrl,
      modelQuoteReadDialogVisible: false,
      quotaQuantityEditable: false,
      tableData: [],
      tableHeader: [],
      multipleSelection: [],
      queryParam: {},
      paymentTypeDialogVisible: false,
      formulaPriceDialogVisible: false,
      viewRow: null,
      currentItemDesc: '',
      currentItemPriceNodes: null,
      currentSouItemId: '',
      priceLineChartLoading: false,
      BUSINESS_TYPE_ENUM,
      ladderPriceDialogVisible: false,
      separationPriceDialogVisible: false
    }
  },

  computed: {
    readonly () {
      // 项目状态=='拟定' && 审批状态=='草稿、审批中'
      return judgeManagement(this.projectStatus, this.createApprovalStatus)
    }
  },

  watch: {
    isActiveMenu: {
      handler (val) {
        if (val) {
          this.getQueryData()
        }
      },
      immediate: true
    },
    biddingBase: {
      handler (val) {
        if (!val || typeof val !== 'object' || !val.souNo || !val.scoreRuleType || !val.orderWay) {
          return false
        }
        const tableHeaderData = [
          // 轮次
          {
            prop: 'round',
            label: this.$t('bidMod.bidingRound'),
            minWidth: 90
          },
          // 本轮入围情况
          {
            prop: 'winStatus',
            label: '本轮入围情况',
            minWidth: 120,
            formattor: val => this.$getDictLabel('SOU_WIN_STATUS', val)
          },
          // 评选结果
          {
            prop: 'selectStatus',
            label: '评选结果',
            minWidth: 120,
            formattor: val => this.$getDictLabel('SOU_SELECT_STATUS', val)
          },
          // 业务实体
          {
            prop: 'orgOuName',
            label: this.$t('bid_mod.businessEntity'),
            minWidth: 150
          },
          // 库存组织
          {
            prop: 'orgInvName',
            label: this.$t('bid_mod.inv'),
            minWidth: 150
          },
          // 价格类型
          {
            prop: 'priceType',
            label: this.$t('bid_mod.priceType'),
            minWidth: 110,
            formattor: val => this.$getDictLabel('PRICE_TYPE', val)
          },
          // 组合
          {
            prop: 'itemGroup',
            label: this.$t('bidMod.itemGroup'),
            hidden: val.orderWay !== SOU_ORDER_WAY_ENUM.COMBINED,
            minWidth: 120
          },
          // 物料编码
          {
            prop: 'itemCode',
            label: this.$t('bidMod.targetNum'),
            minWidth: 120,
            formattor: val => targetNumReveal(val)
          },
          // 物料名称
          {
            prop: 'itemDesc',
            label: this.$t('bidMod.targetDesc'),
            minWidth: 120
          },
          // 小类
          {
            prop: 'categoryName',
            label: this.$t('vendorMod.littleCategory'),
            minWidth: 120
          },
          // 需求数量
          {
            prop: 'requireQuantity',
            label: this.$t('bidMod.demandQuantity2'),
            minWidth: 120
          },
          // 税率
          {
            prop: 'taxKey',
            label: this.$t('bidMod.taxRate'),
            minWidth: 140,
            formattor: val => this.$getDictLabel('tax', val)
          },
          // 单位
          {
            prop: 'unit',
            label: this.$t('bid_mod.unit'),
            minWidth: 100,
            formattor: val => this.$getDictLabel('unit', val)
          },
          // 定价开始时间
          {
            prop: 'priceStartTime',
            label: this.$t('bid_mod.priceStartTime'),
            minWidth: 150,
            formattor: val => this.$dayjsParse(val)
          },
          // 定价结束时间
          {
            prop: 'priceEndTime',
            label: this.$t('bid_mod.priceEndTime'),
            minWidth: 150,
            formattor: val => this.$dayjsParse(val)
          },
          // 投标供应商
          {
            prop: 'vendorName',
            label: this.$t('bidMod.bidingvendorName'),
            minWidth: 150
          },
          // 投标供应商编码
          {
            prop: 'vendorCode',
            label: this.$t('bidMod.bidderCode'),
            minWidth: 150
          },
          // 本轮最高价
          {
            prop: 'standardNotaxMaxPrice',
            label: this.$t('bidMod.maxPriceRound'),
            hidden: val.scoreRuleType !== SOU_SCORE_RULE_TYPE_ENUM.MAX_PRICE,
            minWidth: 120
          },
          // 本轮最低价
          {
            prop: 'standardNotaxMinPrice',
            label: this.$t('bidMod.currentRoundMinPrice'),
            hidden: val.scoreRuleType === SOU_SCORE_RULE_TYPE_ENUM.MAX_PRICE,
            minWidth: 120
          },
          // 投标币种
          {
            prop: 'orderCurrency',
            label: this.$t('bidMod.bidingCurrency3'),
            minWidth: 100,
            formattor: val => this.$getDictLabel('currency', val)
          },
          // 原币未税单价
          {
            prop: 'orderNotaxPrice',
            label: '原币未税单价',
            minWidth: 120
          },
          // 原币含税单价
          {
            prop: 'orderTaxPrice',
            label: '原币含税单价',
            minWidth: 150
          },
          // 本币未税单价
          {
            prop: 'standardNotaxPrice',
            label: '本币未税单价',
            minWidth: 120
          },
          // 本币币种
          {
            prop: 'standardCurrency',
            label: this.$t('bidMod.standardCurrency'),
            minWidth: 100,
            formattor: val => this.$getDictLabel('currency', val)
          },
          // 最小订单量
          {
            prop: 'mqo',
            label: this.$t('bidMod.minOrderQuantity'),
            minWidth: 130
          },
          // 中标数量
          {
            prop: 'winAmount',
            label: this.$t('bidMod.quotaQuantity'),
            minWidth: 100,
            showType: 'slot',
            slot: 'winAmount'
          },
          // 贸易条款
          {
            prop: 'tradeTerm',
            label: this.$t('bid_mod.tradeTerm'),
            minWidth: 100,
            formattor: val => this.$getDictLabel('trade_clause', val)
          },
          // 付款条款
          {
            prop: 'paymentType',
            label: this.$t('paymentType.paymentType'),
            minWidth: 100,
            showType: 'button',
            btnStyle: 'text',
            formattor: () => this.$t('common.view'),
            callback: row => this.openPaymentTypeDialog(row)
          },
          {
            prop: 'isLadder',
            label: this.$t('bidMod.isLadder'),
            minWidth: 120,
            formattor: val => this.$getDictLabel('YES_OR_NO', val)
          },
          // 是否代理投标
          {
            prop: 'isProxy',
            label: '是否代理投标',
            width: 140,
            formattor: val => this.$getDictLabel('YES_OR_NO', val)
          },
          // 质保期(月)
          {
            prop: 'warrantyPeriod',
            label: this.$t('bid_mod.warrantyPeriod'),
            minWidth: 120
          },
          // 承诺交货期
          {
            prop: 'deliverDate',
            label: this.$t('bid_mod.deliverDate'),
            minWidth: 150
          },
          // 供货周期
          {
            prop: 'leadTime',
            label: this.$t('bid_mod.leadTime'),
            minWidth: 170
          },
          // 采购申请号
          {
            prop: 'sourceFromNo',
            label: this.$t('bid_mod.purchaseRequest'),
            minWidth: 150
          },
          // 采购申请行号
          {
            prop: 'sourceFromLineNo',
            label: this.$t('bid_mod.purchaseRequestRowNum'),
            minWidth: 150
          },
          // 价格得分
          {
            prop: 'priceScore',
            label: this.$t('bid_mod.priceScore'),
            minWidth: 100
          },
          // 技术得分
          {
            prop: 'techScore',
            label: this.$t('perfMod.scoreAttribute5'),
            minWidth: 100
          },
          // 绩效得分
          {
            prop: 'performanceScore',
            label: this.$t('perfMod.perScore'),
            minWidth: 100
          },
          // 综合得分
          {
            prop: 'compositeScore',
            label: this.$t('perfMod.scoreAll'),
            minWidth: 100
          },
          // 排名
          {
            prop: 'ranking',
            label: this.$t('perfMod.rank'),
            minWidth: 80
          },
          // 预计采购金额
          {
            prop: 'buyAmount',
            label: this.$t('bidMod.amount'),
            minWidth: 170
          },
          // 备注
          {
            prop: 'comments',
            label: this.$t('bidMod.remark'),
            minWidth: 115
          },
          {
            label: this.$t('bidMod.operation'),
            prop: 'operation',
            // 普通报价没有报价信息按钮
            align: 'center',
            fixed: 'right',
            children: [
              // 报价信息 公式报价/模型报价
              {
                prop: 'quotaInfo',
                width: 150,
                showType: 'slot',
                slot: 'quotaInfo'
              },
              // 比价
              {
                prop: 'priceComparison',
                width: 100,
                showType: 'slot',
                slot: 'priceComparison'
              }
            ]
          }
        ]

        // if (val.orderType === SOU_ORDER_TYPE_ENUM.SIMPLE) {
        //   // 普通报价 移除报价信息按钮
        //   const index = tableHeaderData.findIndex(item => item.prop === 'operation')
        //   tableHeaderData[index].minWidth = 100
        //   tableHeaderData[index].children.splice(0, 1)
        // }

        this.tableHeader = tableHeaderData
      },
      deep: true,
      immediate: true
    }
  },

  methods: {
    /* 搜索 */
    getQueryData (payload) {
      if (payload) {
        this.queryParam = {
          ...payload
        }
      }
      this.queryParam = {
        ...this.queryParam,
        projectId: this.biddingBase.projectId
      }
      // 重置比价折线图参数
      this.currentItemPriceNodes = null
      this.currentItemDesc = ''
      this.currentSouItemId = ''
      this.$nextTick(() => {
        this.$refs.tenderSelectionTable.query()
      })
    },

    /* 查看付款条件 */
    openPaymentTypeDialog (row) {
      this.viewRow = row
      this.paymentTypeDialogVisible = true
    },

    /* 打开公式报价明细弹窗 */
    openFormulaQuotaDialog (row) {
      this.viewRow = row
      this.formulaPriceDialogVisible = true
    },

    /* 打开阶梯价明细弹窗 */
    openLadderPriceDialog (row) {
      this.viewRow = {
        ...row,
        ladderList: row.ladderPriceList || []
      }
      this.ladderPriceDialogVisible = true
    },

    /* 查看模型报价 */
    openModelQuoteReadDialog (row) {
      this.viewRow = row
      this.modelQuoteReadDialogVisible = true
    },

    /** 打开料费分离弹窗 */
    openSeparationPriceDialog (row) {
      const { quoteTempId, quoteTempName, currentRound } = this.biddingBase
      this.viewRow = {
        ...row,
        quoteTempId,
        quoteTempName,
        currentRound
      }
      this.separationPriceDialogVisible = true
    },

    /* 数据查询完成 */
    afterQuery (val) {
      if (val && Array.isArray(val)) {
        this.quotaQuantityEditable = val.length <= 50

        if (val.length > 0) {
          this.$nextTick(() => {
            this.rowClick(val[0])
            this.$refs.tenderSelectionTable.setCurrentRow(val[0])
          })
        }
      }
    },

    /* 记录表格选中行 */
    handleSelectionChange (val) {
      // 获取其他行选中数据
      let otherRows = this.$refs.tenderSelectionTable.getCheckboxReserveRecords(true)
      this.multipleSelection = val.concat(otherRows)
      console.log('multipleSelection', this.multipleSelection)
    },

    /* 智能评选 */
    async intelligentEvaluation () {
      const response = await bidBuyerHttp.select.intelligent({
        projectId: this.biddingBase.projectId
      })
      if (response) {
        this.$message.success(this.$t('common.successSubmit'))
        // 更新基础数据
        this.$emit('refresh')
        // 更新菜单节点完成数据
        this.$emit('refresh-process')
        this.getQueryData()
      }
    },

    /* 判断是否以选行并给予提示 */
    validateSelectionRow () {
      if (this.multipleSelection.length === 0) {
        this.$message.warning(this.$t('bidMod.msgSelData'))
        return []
      }
      return this.multipleSelection.map(item => item.orderItemId)
    },

    /* 入围 / 淘汰 */
    async changeWinStatus (type) {
      const orderItemIds = this.validateSelectionRow()
      if (orderItemIds.length === 0) {
        return
      }

      const toWin = type === 'win'
      const response = await bidBuyerHttp.select.changeWinStatus({ orderItemIds, toWin })
      if (response) {
        // 入围成功！
        this.$message.success(toWin ? this.$t('bidMod.successShortlist') : this.$t('bidMod.successEliminate'))
        this.getQueryData()
      }
    },

    /* 中标 / 落标 */
    async changeSelectStatus (type) {
      const orderItemIds = this.validateSelectionRow()
      if (orderItemIds.length === 0) {
        return
      }

      const toWin = type === 'win'
      if (toWin) {
        // 允许为0
        if (this.multipleSelection.find(item => !item.winAmount && item.winAmount !== 0)) {
          // 请先填写中标数量
          return this.$message.warning(this.$t('vendorMod.msgSelBidder'))
        }
      }

      // 发起中标
      const response = await bidBuyerHttp.select.changeSelectStatus({
        orderItemIds,
        selects: this.multipleSelection.map(item => {
          return {
            orderItemId: item.orderItemId,
            winAmount: item.winAmount
          }
        }),
        toWin
      })
      if (response) {
        this.$message.success(this.$t('common.successSubmit'))
        this.getQueryData()
      }
    },

    /* 公示本轮结果 */
    async openResult () {
      const response = await bidBuyerHttp.select.openResult(this.biddingBase.projectId)
      if (response) {
        // 结果已公示！
        this.$message.success(this.$t('bidMod.resultAnnounce'))
      }
    },

    /* 生成寻源结果审批单 */
    async createPricingApproval () {
      const httpMethod = meiqlCtrl.priceApproval === 'Y' ? bidBuyerHttp.select.createPricingApprovalNew : bidBuyerHttp.select.getCreatePricingApprovalUrl
      // 公共方法生成价格审批单
      await createPricingApproval(
        // 项目式询价[LTS]
        BUSINESS_TYPE_ENUM.BIDDING_LTS,
        // apiUrl
        httpMethod(this.biddingBase.projectId),
        {},
        this.$router
      )
    },

    /* 提交中标数量修改 */
    async submitQuotaQuantity (valid) {
      if (valid && this.multipleSelection.find(item => item.selectStatus !== 'WIN' && item.winAmount > 0)) {
        // 如果其中标数量不为空并且不为0，并且这一行的评选结果为空或者为落标。
        return this.$message.warning(this.$t('bidMod.msgNotBid'))
      }

      const data = this.multipleSelection.map(({ winAmount, orderItemId }) => ({
        winAmount,
        orderItemId
      }))
      const responseChangeWinAmount = await bidBuyerHttp.select.changeWinAmount(data)
      if (responseChangeWinAmount) {
        this.$message.success(this.$t('common.successSubmit'))
        this.getQueryData()
      }
    },

    /* 表格行点击 */
    async rowClick (row) {
      // 避免重复点击
      if (this.currentSouItemId === row.souItemId) {
        return
      }

      // 查询比价折线图
      await this.getPriceCompareInfos(row)
    },

    /* 查询比价折线图 */
    async getPriceCompareInfos (row) {
      this.priceLineChartLoading = true
      // 查询当前物料的历史价格 再设置当前的比价折线图数据
      const response = await bidBuyerHttp.select.getPriceCompareInfos({
        souItemId: row.souItemId,
        round: row.round
      })
      this.priceLineChartLoading = false
      if (response && response.data) {
        this.currentItemPriceNodes = response.data.priceNodes || {}
        this.currentSouItemId = row.souItemId
        this.currentItemDesc = row.itemDesc
      }
    },

    /* 表格表头样式 */
    tableHeaderCellStyle ({ rowIndex }) {
      return quoteSelectionTableHeaderCellStyle(rowIndex)
    },

    /* 表格合并行 */
    tableSpanMethod ({ column, rowIndex }) {
      return quoteSelectionTableSpanMethod(column, rowIndex)
    },

    /* 打开比价tab */
    openPriceComparisonTab (row) {
      console.log('row:::', row)
      const tab = {
        component: this.pricingType.isSeparation ? priceComparisonModel : priceComparison,
        params: {
          businessType: BUSINESS_TYPE_ENUM.BIDDING_LTS,
          pricingType: this.biddingBase.orderType,
          row: {
            id: this.biddingBase.projectId,
            number: this.biddingBase.souNo,
            round: row.currentRound // 取当前的最大轮次
          }
        },
        closable: true,
        title: `${this.biddingBase.souNo} 比价`,
        name: `priceComparison-${this.biddingBase.souNo}`
      }
      this.$emit('tab-add', tab)
    }
  }
}
</script>

<style lang="scss" scoped>
.price-line-chart-wrap {
  width: 100%;
  height: 400px;
  margin-bottom: 20px;
}
// 强制隐藏二级表头的间隔列头
.evaluation-table {
  margin-top: 10px;
  ::v-deep .el-table .el-table__header-wrapper th.el-table__cell.gutter {
    display: none !important;
  }
}
</style>
