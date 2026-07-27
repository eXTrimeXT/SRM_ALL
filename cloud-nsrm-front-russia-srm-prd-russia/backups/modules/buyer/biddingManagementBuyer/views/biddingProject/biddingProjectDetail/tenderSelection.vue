<template>
  <!-- 招标评选 -->
  <el-container
    class="flex-container"
    direction="vertical"
    style="padding-top: 10px;"
  >
    <div>
      <search-form
        :pre-form-obj="{ round: bidingBase.currentRound || 1 }"
        @getQueryData="getQueryData"
      />

      <!--按钮操作区-->
      <div class="btn-wrap">
        <!--智能评选-->
        <el-button
          type="primary"
          :disabled="isDisabledTable"
          style="margin-right: 10px;"
          @click="intelligentEvaluation"
        >
          {{ $t("bidMod.intelligentEvaluation") }}
        </el-button>

        <!-- 入围或淘汰 -->
        <el-dropdown style="margin-right: 10px;">
          <el-button type="primary">
            {{ $t("bidMod.nextOrEliminate") }}
            <em class="el-icon-arrow-down el-icon--right" />
          </el-button>

          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item>
              <div class="paddingBt">
                <!--入围下一轮-->
                <el-button
                  type="primary"
                  :disabled="isDisabledTable"
                  @click="toNextRoundConfirm"
                >
                  {{ $t("bidMod.toNextRoundConfirm") }}
                </el-button>
              </div>
            </el-dropdown-item>

            <el-dropdown-item>
              <!-- 淘汰 -->
              <el-button
                type="primary"
                :disabled="isDisabledTable"
                @click="toEliminateConfirm"
              >
                {{ $t("bidMod.toEliminateConfirm") }}
              </el-button>
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>

        <!--决标操作-->
        <el-dropdown style="margin-right: 10px;">
          <el-button type="primary">
            {{ $t("bidMod.bidAwardOperation") }}<em class="el-icon-arrow-down el-icon--right" />
          </el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="winTheBidding">
              <div class="paddingBt">
                <!--中标-->
                <el-button
                  type="primary"
                  :disabled="isDisabledTable"
                  @click="winTheBidding"
                >
                  {{ $t("bid_mod.winTheBidding") }}
                </el-button>
              </div>
            </el-dropdown-item>

            <el-dropdown-item command="lossTheBidding">
              <!--落标-->
              <el-button
                type="primary"
                :disabled="isDisabledTable"
                @click="lossTheBidding"
              >
                {{ $t("bid_mod.lossTheBidding") }}
              </el-button>
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>

        <!--公示本轮结果-->
        <el-button
          :disabled="isDisabledTable"
          @click="toPublishDialog"
        >
          {{ $t("bidMod.toPublishDialog") }}
        </el-button>

        <!--提交中标数量修改-->
        <el-button
          v-if="quotaQuantityEditable"
          :disabled="isDisabledTable"
          @click="submitQuotaQuantity"
        >
          {{ $t("bidMod.submitBidModify") }}
        </el-button>

        <!-- 生成寻源结果审批单 -->
        <el-button
          :disabled="isDisabledTable"
          @click="createPricingApproval"
        >
          {{ $t("bidMod.newSourcingResult") }}
        </el-button>
      </div>

      <!--评选表格-->
      <TableView
        ref="tenderSelectionTable"
        style="padding: 0; height: 330px"
        :table-data="tableData"
        :table-header="tableHeader"
        front-paging
        row-key="orderLineId"
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
        custom-table-key="bidTenderSelectionTable"
        url="/api-bid/evaluation/queryEvaluationPage"
        class="quote-selection-table"
        @afterQuery="afterQuery"
      >
        <!--中标数量-->
        <template #winBidAmount="{ scope }">
          <el-input
            v-if="quotaQuantityEditable"
            v-model="scope.row.winBidAmount"
            @click.native.stop
          />
          <span v-else>{{ scope.row.winBidAmount }}</span>
        </template>

        <template #priceComparison="{ scope }">
          <el-button
            type="text"
            @click.stop="openPriceComparisonTab(scope.row)"
          >
            比价
          </el-button>
        </template>

        <!--操作 公式报价 模型报价 比价-->
        <template #quotaInfo="{ scope }">
          <!--公式报价-->
          <el-button
            v-if="pricingType.isFormulPricing"
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

          <!--模板报价明细-->
          <el-button
            v-if="pricingType.isTemplatePricing"
            type="text"
            @click="openTemplatePriceDialog(scope.row)"
          >
            {{ $t('templatePrice.detailLabel') }}
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
        <vendor-qoute-price-line-chart
          :vendor-price-nodes="currentItemPriceNodes"
          :item-desc="currentItemDesc"
        />
      </div>
    </div>

    <!-- 模型报价查看 -->
    <ModelQuoteReadDialog
      v-if="modelQuoteReadDialogVisible"
      :visible.sync="modelQuoteReadDialogVisible"
      :source-line="viewRow"
      is-only-read
    />

    <!--公式报价查看-->
    <formula-price-dialog
      v-if="formulaPriceDialogVisible"
      :visible.sync="formulaPriceDialogVisible"
      :view-row="viewRow"
    />

    <!--付款条款-->
    <PaymentTypeDialog
      v-if="paymentTypeDialogVisible"
      :visible.sync="paymentTypeDialogVisible"
      :business-type="BUSINESS_TYPE_ENUM.BIDING"
      :edit-row="viewRow"
      readonly
    />

    <!--模板报价-->
    <TemplatePriceDialog
      v-if="templatePriceDialogVisible"
      :visible.sync="templatePriceDialogVisible"
      :business-type="BUSINESS_TYPE_ENUM.BIDING"
      :query-params="queryParams"
      readonly
    />
  </el-container>
</template>

<script>
import { parseTime } from '@/utils'
import {
  quoteSelectionTableHeaderCellStyle,
  quoteSelectionTableSpanMethod,
  targetNumReveal
} from 'lib@/composition/origin/composition'
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import TableView from 'lib@/components/Table/TableView'
import searchForm from './tenderSelection/searchForm'
import ModelQuoteReadDialog from 'lib@/composition/biddingManagement/modelQuote/modelQuoteReadDialog'
import formulaPriceDialog from '@/library/composition/biddingManagement/formulaPriceDialog'
import vendorQoutePriceLineChart from '@/library/composition/origin/vendorQoutePriceLineChart'
import priceComparison from 'lib@/composition/origin/priceComparison'
import PaymentTypeDialog from 'lib@/composition/origin/paymentTypeDialog'
import TemplatePriceDialog from 'lib@/composition/quoteTemplate/templatePriceDialog'

export default {
  name: 'TenderSelection',

  components: {
    searchForm,
    ModelQuoteReadDialog,
    TableView,
    formulaPriceDialog,
    vendorQoutePriceLineChart,
    PaymentTypeDialog,
    TemplatePriceDialog
  },

  props: {
    scopeBidingId: {
      // 招标ID
      type: [Number, String],
      default: ''
    },
    bidingStatus: {
      // 招标状态
      type: String,
      default: ''
    },
    auditStatus: {
      // 审批状态
      type: String,
      default: ''
    },
    bidingBase: {
      type: Object,
      default: () => { /* nothing */ }
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
      modelQuoteReadDialogVisible: false,
      quotaQuantityEditable: false,
      tableData: [],
      tableHeader: [],
      multipleSelection: [],
      preform3: {
        targetNum: '',
        targetDesc: '',
        orgName: '',
        vendorName: '',
        selectionStatus: '',
        round: '',
        itemGroup: ''
      },
      queryParam: {},
      paymentTypeDialogVisible: false,
      formulaPriceDialogVisible: false,
      viewRow: null,
      currentItemDesc: '',
      currentItemPriceNodes: null,
      currentRequirementLineId: '',
      priceLineChartLoading: false,
      BUSINESS_TYPE_ENUM,
      templatePriceDialogVisible: false,
      queryParams: null
    }
  },

  computed: {
    isDisabledTable () {
      // 项目状态=='拟定' && 审批状态=='草稿、审批中'
      return this.bidingStatus === 'DRAW_UP' && ['DRAFT', 'SUBMITTED'].includes(this.auditStatus)
    },
    bidingNum () {
      return this.bidingBase.bidingNum
    }
  },

  watch: {
    bidingBase: {
      handler (value) {
        if (!value || typeof value !== 'object' || !value.bidingNum || !value.evaluateMethod || !value.bidingAwardWay) {
          return
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
            prop: 'win',
            label: '本轮入围情况',
            minWidth: 120,
            formattor: val => this.$getDictLabel('SUPPLIER_SELECTED_STATES', val)
          },
          // 评选结果
          {
            prop: 'selectionStatus',
            label: '评选结果',
            minWidth: 120,
            formattor: val => this.$getDictLabel('BIDDING_SELECT_STATES', val)
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
            hidden: value.bidingAwardWay !== 'COMBINED_DECISION',
            minWidth: 120
          },
          // 物料编码
          {
            prop: 'targetNum',
            label: this.$t('bidMod.targetNum'),
            minWidth: 120,
            formattor: val => targetNumReveal(val)
          },
          // 物料名称
          {
            prop: 'targetDesc',
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
            prop: 'quantity',
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
            formattor: val => val ? parseTime(val, '{y}-{m}-{d}') : ''
          },
          // 定价结束时间
          {
            prop: 'priceEndTime',
            label: this.$t('bid_mod.priceEndTime'),
            minWidth: 150,
            formattor: val => val ? parseTime(val, '{y}-{m}-{d}') : ''
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
            label: this.$t('bidMod.bidingvendorCode'),
            minWidth: 150
          },
          // 本轮最高价
          {
            prop: 'currentRoundMaxNotaxPrice',
            label: this.$t('bidMod.maxPriceRound'),
            hidden: value.evaluateMethod !== 'HIGH_PRICE',
            minWidth: 120
          },
          // 本轮最低价
          {
            prop: 'currentRoundMinNotaxPrice',
            label: this.$t('bidMod.currentRoundMinPrice'),
            hidden: value.evaluateMethod === 'HIGH_PRICE',
            minWidth: 120
          },
          // 报价币种
          {
            prop: 'currencyType',
            label: this.$t('bidMod.bidingCurrency2'),
            minWidth: 120,
            formattor: val => this.$getDictLabel('currency', val)
          },
          // 原币未税单价
          {
            prop: 'notaxPrice',
            label: '原币未税单价',
            minWidth: 120
          },
          // 原币含税单价
          {
            prop: 'taxPrice',
            label: '原币含税单价',
            minWidth: 150
          },
          // 本币未税单价
          {
            prop: 'bidNotaxPrice',
            label: '本币未税单价',
            minWidth: 120
          },
          // 本币币种
          {
            prop: 'standardCurrency',
            label: this.$t('bidMod.standardCurrency'),
            minWidth: 120,
            formattor: val => this.$getDictLabel('currency', val)
          },
          // mqo
          {
            prop: 'mqo',
            label: 'MQO',
            minWidth: 100
          },
          // 汇率
          {
            prop: 'priceTax',
            label: this.$t('bidMod.appraisRate'),
            minWidth: 85
          },
          // 中标数量
          {
            prop: 'winBidAmount',
            label: this.$t('bidMod.quotaQuantity'),
            minWidth: 100,
            showType: 'slot',
            slot: 'winBidAmount'
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
          // 是否代理报价
          {
            prop: 'isProxyBidding',
            label: this.$t('bid_mod.isProxyBidding'),
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
            prop: 'purchaseRequestNum',
            label: this.$t('bid_mod.purchaseRequest'),
            minWidth: 150
          },
          // 采购申请行号
          {
            prop: 'purchaseRequestRowNum',
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
            prop: 'perfScore',
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
            prop: 'rank',
            label: this.$t('perfMod.rank'),
            minWidth: 80
          },
          // 预计采购金额
          {
            prop: 'amount',
            label: this.$t('bidMod.amount'),
            minWidth: 170
          },
          // 备注
          {
            prop: 'comments',
            label: this.$t('bidMod.remark'),
            minWidth: 115
          },
          // 操作
          {
            label: this.$t('bidMod.operation'),
            prop: 'operation',
            // 普通报价没有报价信息按钮
            minWidth: 250,
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

        if (value.pricingType === 'SIMPLE_PRICING') {
          // 普通报价 移除报价信息按钮
          const index = tableHeaderData.findIndex(item => item.prop === 'operation')
          tableHeaderData[index].minWidth = 100
          tableHeaderData[index].children.splice(0, 1)
        }

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
        bidingId: this.scopeBidingId
      }
      // 重置比价折线图参数
      this.currentItemPriceNodes = null
      this.currentItemDesc = ''
      this.currentRequirementLineId = ''
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

    /* 查看模型报价 */
    openModelQuoteReadDialog (row) {
      this.viewRow = row
      this.modelQuoteReadDialogVisible = true
    },

    /* 打开模板报价明细弹窗 */
    openTemplatePriceDialog (row) {
      this.queryParams = {
        requirementLineId: row.requirementLineId,
        vendorId: row.vendorId
      }

      this.templatePriceDialogVisible = true
    },

    /* 数据查询完成 */
    afterQuery (val) {
      if (val && Array.isArray(val)) {
        this.quotaQuantityEditable = val.length <= 50

        if (val.length > 0) {
          this.$nextTick(() => {
            this.$refs.tenderSelectionTable.setCurrentRow(val[0])
          })
          // 选中第一行
          this.rowClick(val[0])
        }
      }
    },

    /* 记录表格选中行 */
    handleSelectionChange (val) {
      this.multipleSelection = val
    },

    /* 智能评选 */
    intelligentEvaluation () {
      this.$http({
        url: `/api-bid/evaluation/intelligentEvaluation/${this.scopeBidingId}`,
        method: 'POST',
        loading: true
      }).then(() => {
        this.$message.success(this.$t('common.successSubmit'))
        this.getQueryData()
        this.$emit('fetchBaseInfo')
      })
    },

    /* 入围下一轮 */
    toNextRoundConfirm () {
      let idList = this.multipleSelection.map(v => v.orderLineId)
      if (idList.length === 0) {
        // 请先选择数据！
        this.$message.warning(this.$t('bidMod.msgSelData'))
        return
      }
      this.$http({
        url: '/api-bid/evaluation/enterNextRound',
        method: 'POST',
        data: idList,
        loading: true
      }).then(data => {
        if (data) {
          // 入围成功！
          this.$message.success(this.$t('bidMod.successShortlist'))
          this.getQueryData()
        }
      })
    },

    /* 淘汰 */
    toEliminateConfirm () {
      let idList = this.multipleSelection.map(v => v.orderLineId)
      if (idList.length === 0) {
        // 请先选择数据！
        this.$message.warning(this.$t('bidMod.msgSelData'))
        return
      }
      this.$http({
        url: '/api-bid/evaluation/eliminate',
        method: 'POST',
        data: idList,
        loading: true
      }).then(data => {
        if (data) {
          // 淘汰成功！
          this.$message.success(this.$t('bidMod.successEliminate'))
          this.getQueryData()
        }
      })
    },

    /* 中标 */
    winTheBidding () {
      if (!this.multipleSelection.length) {
        // 请选择中标对象！
        return this.$message.warning(this.$t('bidMod.msgSelWinBidder'))
      }

      let multipleSelectionNext = this.multipleSelection.filter(i => !i.winBidAmount)
      if (multipleSelectionNext && multipleSelectionNext.length > 0) {
        return this.$message.warning(this.$t('vendorMod.msgSelBidder'))
      }

      const data = this.multipleSelection.map(({ winBidAmount, orderLineId }) => ({
        winBidAmount: winBidAmount,
        orderLineId
      }))

      // 发起中标
      this.$http({
        url: '/api-bid/evaluation/changeSelectionStatus/win',
        method: 'POST',
        data: this.multipleSelection.map(v => v.orderLineId),
        loading: true
      }).then(res => {
        this.$message.success(res.message)
        // 同时提交中标数量
        this.$http({
          url: '/api-bid/evaluation/changeWinQuantity',
          method: 'POST',
          data,
          loading: true
        }).then(() => {
          this.getQueryData()
        })
      })
    },

    /* 落标 */
    lossTheBidding () {
      if (!this.multipleSelection.length) {
        // 请选择落标对象！
        return this.$message.warning(this.$t('bidMod.msgSelLoseBidder'))
      }

      this.$http({
        url: '/api-bid/evaluation/changeSelectionStatus/fail',
        method: 'POST',
        data: this.multipleSelection.map(item => item.orderLineId),
        loading: true
      }).then(res => {
        this.$message.success(res.message)
        this.getQueryData()
      })
    },

    /* 公示本轮结果 */
    toPublishDialog () {
      this.$http({
        url: `/api-bid/evaluation/publishResult/${this.scopeBidingId}`,
        method: 'POST',
        loading: true
      }).then(() => {
        this.$message.success(this.$t('bidMod.resultAnnounce')) // 结果已公示！
      })
    },

    /* 生成寻源结果审批单 */
    createPricingApproval () {
      this.$confirm('确定生成寻源结果审批单吗？', {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: `/api-bid/evaluation/createPricingApproval/${this.scopeBidingId}`,
          method: 'POST',
          loading: true
        }).then(data => {
          if (data && data.data) {
            this.$confirm('价格审批单创建成功，需要跳转到价格审批单页面吗？', {
              confirmButtonText: '跳转到价格审批单页面',
              cancelButtonText: this.$t('common.cancel'),
              type: 'warning'
            }).then(() => {
              this.$router.push({
                name: 'priceApproval',
                params: {
                  from: 'fromFun',
                  formId: data.data.approvalHeaderId,
                  formNo: data.data.approvalNo,
                  funName: 'priceApproval',
                  sourceType: BUSINESS_TYPE_ENUM.BIDING
                }
              })
            })
          }
        })
      })
    },

    /* 查询价格审批单 */
    getApprovalDetails () {
      return this.$http({
        url: '/api-inq/price/approval/getApprovalDetails',
        method: 'GET',
        params: { ceeaSourceNo: this.bidingNum },
        loading: true
      })
    },

    /* 提交中标数量修改 */
    submitQuotaQuantity () {
      for (let item of this.multipleSelection) {
        // 如果其中标数量不为空并且不为0，并且这一行的评选结果为空或者为落标。
        if (item.selectionStatus !== 'WIN' && item.winBidAmount > 0) {
          item.winBidAmount = null
          return this.$message.error(this.$t('bidMod.msgNotBid'))
        }
      }

      this.$http({
        url: '/api-bid/evaluation/changeWinQuantity',
        method: 'POST',
        data: this.multipleSelection.map(({ winBidAmount, orderLineId }) => ({
          winBidAmount,
          orderLineId
        })),
        loading: true
      }).then(res => {
        this.getQueryData()
        this.$message.success(res.message)
      })
    },

    /* 表格行点击 */
    async rowClick (row) {
      // 避免重复点击
      if (this.currentRequirementLineId === row.requirementLineId) return

      this.priceLineChartLoading = true
      // 查询当前物料的历史价格 再设置当前的比价折线图数据
      const response = await this.$http({
        url: '/api-bid/evaluation/getPriceCompareInfos',
        method: 'GET',
        params: {
          requirementLineId: row.requirementLineId,
          round: row.round
        },
        // 不是关键数据，不需要loading
        loading: false
      })
      this.priceLineChartLoading = false
      if (response && response.data) {
        this.currentItemPriceNodes = response.data.priceNodes || {}
        this.currentRequirementLineId = row.requirementLineId
        this.currentItemDesc = row.targetDesc
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
      this.$emit('tab-add', {
        component: priceComparison,
        params: {
          businessType: BUSINESS_TYPE_ENUM.BIDING,
          pricingType: this.bidingBase.pricingType,
          row: {
            id: this.scopeBidingId,
            number: this.bidingNum,
            round: row.round
          }
        },
        closable: true,
        title: `${this.bidingNum} 比价`,
        name: `priceComparison-${this.bidingNum}`
      })
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

.btn-wrap {
  padding: 10px 0;
}

// 强制隐藏二级表头的间隔列头
.quote-selection-table :deep(.el-table th.gutter)  {
  display: none !important;
}
</style>
