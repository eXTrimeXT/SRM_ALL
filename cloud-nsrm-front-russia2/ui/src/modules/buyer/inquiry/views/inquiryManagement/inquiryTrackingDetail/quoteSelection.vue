<template>
  <div class="quote-selection">
    <FormWrapper
      :form-array="formWrapperConfig"
      init-active
      form-label-width="120px"
      @getFormData="getQueryData"
    >
      <template #scoreRuleType>
        <div class="form-wrapper-slot-text">
          {{ $getDictLabel('SOU_SCORE_RULE_TYPE', header.scoreRuleType) }}
        </div>
      </template>
      <template #orderWay>
        <div class="form-wrapper-slot-text">
          {{ $getDictLabel('SOU_ORDER_WAY', header.orderWay) }}
        </div>
      </template>
    </FormWrapper>

    <div class="quote-selection-table-operation">
      <!--b 智能评选-->
      <el-button type="primary" @click="evaluateOne">
        {{ $t('bidMod.intelligentEvaluation') }}
      </el-button>

      <!--入围或淘汰 -->
      <el-dropdown class="dropdown-button-wrap" @command="changeWinStatus">
        <el-button type="primary">
          {{ $t('bidMod.nextOrEliminate') }}<em class="el-icon-arrow-down el-icon--right" />
        </el-button>

        <!--入围或淘汰 -->
        <el-dropdown-menu slot="dropdown">
          <!--入围下一轮-->
          <el-dropdown-item command="win">
            {{ $t('bidMod.toNextRoundConfirm') }}
          </el-dropdown-item>
          <!--淘汰-->
          <el-dropdown-item command="loss">
            {{ $t('bidMod.toEliminateConfirm') }}
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>

      <!--决标操作-->
      <el-dropdown class="dropdown-button-wrap mr-10" @command="changeSelectResult">
        <el-button type="primary">
          {{ $t('bidMod.bidAwardOperation') }}<em class="el-icon-arrow-down el-icon--right" />
        </el-button>

        <el-dropdown-menu slot="dropdown">
          <!--中标-->
          <el-dropdown-item command="win">
            {{ $t('bid_mod.winTheBidding') }}
          </el-dropdown-item>
          <!--落标-->
          <el-dropdown-item command="loss">
            {{ $t('bid_mod.lossTheBidding') }}
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>

      <!--b 比价表打印-->
      <el-button
        v-if="isSimple"
        type="primary"
        :loading="priceComparisonExportLoading"
        @click="priceComparisonExport"
      >
        {{ $t('bidMod.printPrice') }}
      </el-button>

      <!--b 公示本轮结果-->
      <el-button type="primary" @click="openResult">
        {{ $t('bidMod.toPublishDialog') }}
      </el-button>

      <!--b 发起新一轮-->
      <el-button type="primary" @click="continueNextRound">
        {{ $t('bidMod.biddingControl.startNewRound') }}
      </el-button>

      <!--b 生成价格审批单-->
      <el-button type="primary" @click="toApprovalBill">
        {{ $t('bidMod.toPriceApproval') }}
      </el-button>

      <!--b 批量修改账期-->
      <el-button type="primary" @click="openBatchPaymentTypeDialog">
        {{ $t('bidMod.BatchModify') }}
      </el-button>

      <!--b 提交中标数量修改-->
      <el-button type="primary" @click="changeQuoteQuantity">
        {{ $t('bidMod.submitBidModify') }}
      </el-button>
    </div>

    <el-table
      ref="quoteSelectionTable"
      :data="quoteSelectionTable"
      style="width: 100%"
      border
      height="260px"
      highlight-current-row
      :header-cell-style="tableHeaderCellStyle"
      :span-method="tableSpanMethod"
      class="quote-selection-table"
      @selection-change="handleSelectionChange"
      @row-click="rowClick"
    >
      <el-table-column
        align="center"
        type="index"
        width="50"
        fixed="left"
      />
      <el-table-column
        align="center"
        type="selection"
        width="55"
        fixed="left"
      />
      <!--t 轮次-->
      <el-table-column
        sortable
        align="center"
        prop="round"
        :label="$t('bidMod.bidingRound')"
        width="70"
      />

      <!--t 本轮入围情况-->
      <el-table-column
        align="center"
        prop="winStatus"
        :label="$t('bidMod.currentInList')"
        width="100"
        :formatter="(row, column, value) => $getDictLabel('SOU_WIN_STATUS', value)"
        show-overflow-tooltip
      />

      <!--t 评选情况-->
      <el-table-column
        align="center"
        prop="selectStatus"
        :label="$t('bidMod.selectSituation')"
        width="100"
        :formatter="(row, column, value) => $getDictLabel('SOU_SELECT_STATUS', value)"
        show-overflow-tooltip
      />

      <!--t 业务实体-->
      <el-table-column
        align="center"
        prop="orgOuName"
        :label="$t('bidMod.affairsEntity')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 库存组织-->
      <el-table-column
        align="center"
        prop="orgInvName"
        :label="$t('bidMod.affairsInventoryOrg')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 供应商编号-->
      <el-table-column
        align="center"
        prop="vendorCode"
        :label="$t('bidMod.vendorCode')"
        width="120"
        show-overflow-tooltip
      />

      <!--t 供应商名称-->
      <el-table-column
        align="center"
        prop="vendorName"
        :label="$t('bidMod.vendorName')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--t 物料编码-->
      <el-table-column
        align="center"
        prop="itemCode"
        :label="$t('bidMod.itemCode')"
        width="120"
        :formatter="(row, column, value) => row.noCodeItem === 'Y' ? '' : value"
        show-overflow-tooltip
      />

      <!--t 物料名称-->
      <el-table-column
        align="center"
        prop="itemDesc"
        :label="$t('bidMod.itemName')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--t 采购分类-->
      <el-table-column
        align="center"
        prop="categoryName"
        :label="$t('bidMod.purcategoryName')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 组合-->
      <el-table-column
        align="center"
        prop="itemGroup"
        :label="$t('bidMod.affairsCombination')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 单位-->
      <el-table-column
        align="center"
        prop="unit"
        :label="$t('bidMod.unit')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('unit', cellValue)"
      />

      <!--t 预计数量-->
      <el-table-column
        align="center"
        prop="requireQuantity"
        :label="$t('bidMod.demandQuantity')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 中标数量-->
      <el-table-column
        align="center"
        prop="winAmount"
        :label="$t('bidMod.quotaQuantity')"
        width="130"
      >
        <template v-slot="{ row }">
          <el-input
            v-model="row.winAmount"
            v-input-format="{ type: 'float' }"
            @click.native.stop
          />
        </template>
      </el-table-column>

      <!--t 目标价(未税)-->
      <el-table-column
        align="center"
        prop="notaxTargetPrice"
        :label="$t('bidMod.notaxTargetPrice')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 报价币种-->
      <el-table-column
        align="center"
        prop="orderCurrency"
        :label="$t('bidMod.bidingCurrency2')"
        width="100"
        :formatter="(row, column, value) => $getDictLabel('currency', value)"
        show-overflow-tooltip
      />

      <!--t 报价税率-->
      <el-table-column
        align="center"
        prop="taxKey"
        :label="$t('bidMod.taxKey')"
        width="100"
        :formatter="(row, column, value) => $getDictLabel('tax', value)"
        show-overflow-tooltip
      />

      <!--t 原币未税单价-->
      <el-table-column
        align="center"
        prop="orderNotaxPrice"
        :label="$t('bidMod.orderNotaxPrice')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 原币含税单价-->
      <el-table-column
        align="center"
        prop="orderTaxPrice"
        :label="$t('bidMod.orderTaxPrice')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 汇率-->
      <el-table-column
        align="center"
        prop="priceTax"
        :label="$t('bid_mod.priceTax')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 本币未税价-->
      <el-table-column
        align="center"
        prop="standardNotaxPrice"
        :label="$t('bidMod.standardNotaxPrice')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 本币币种-->
      <el-table-column
        align="center"
        prop="standardCurrency"
        :label="$t('bidMod.appraisLocalCurrency')"
        width="100"
        :formatter="(row, column, value) => $getDictLabel('currency', value)"
        show-overflow-tooltip
      />

      <!--t 价格得分-->
      <el-table-column
        align="center"
        prop="priceScore"
        :label="$t('bidMod.appraisPricePoints')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 综合得分-->
      <!-- <el-table-column
        align="center"
        prop="compositeScore"
        :label="$t('bidMod.synthesisScore')"
        width="100"
        show-overflow-tooltip
      /> -->

      <!--t 排名-->
      <el-table-column
        align="center"
        prop="ranking"
        :label="$t('bidMod.tech_ranking')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 付款条款-->
      <el-table-column
        align="center"
        :label="$t('paymentType.paymentType')"
        width="100"
      >
        <template v-slot="{ row }">
          <el-button type="text" @click.stop="openPaymentTypeDialog(row)">
            {{ $t("common.view") }}
          </el-button>
        </template>
      </el-table-column>

      <!--t 是否阶梯价-->
      <el-table-column
        align="center"
        prop="isLadder"
        :label="$t('bidMod.isLadder')"
        width="100"
        :formatter="(row, scope, cellValue) => $getDictLabel('YES_OR_NO', cellValue)"
      />

      <!--t 备注-->
      <el-table-column
        align="center"
        prop="remark"
        :label="$t('common.remark')"
        width="100"
        show-overflow-tooltip
      />

      <!--操作-->
      <el-table-column
        align="center"
        :label="$t('common.operation')"
        width="250"
        fixed="right"
      >
        <el-table-column
          align="center"
          prop="quotaInfo"
          width="150"
        >
          <template v-slot="{ row }">
            <!-- 公式报价明细 -->
            <el-button
              v-if="row.isFormula === 'Y'"
              type="text"
              @click.stop="openFormulaQuotaDialog(row)"
            >
              {{ $t('bidMod.formulaQuoteDetail') }}
            </el-button>
            <!-- 阶梯价明细 -->
            <el-button
              v-if="row.isLadder === 'Y'"
              type="text"
              @click.stop="openLadderPriceDialog(row)"
            >
              {{ $t('bidMod.ladderQuoteDetail') }}
            </el-button>
            <!-- 模板报价明细 -->
            <el-button
              v-if="isSeparation"
              type="text"
              @click.stop="openSeparationPriceDialog(row)"
            >
              {{ $t('bidMod.templateQuoteDetail') }}
            </el-button>
          </template>
        </el-table-column>

        <el-table-column
          align="center"
          prop="priceComparison"
          width="100"
        >
          <template v-slot="{ row }">
            <!-- 比价  -->
            <el-button type="text" @click.stop="openPriceComparisonTab(row)">
              {{ $t('bidMod.priceCompare') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table-column>
    </el-table>

    <CPagination
      :total="pagination.total"
      :page-num="pagination.pageNum"
      :page-size="pagination.pageSize"
      @current-change="paginationCurrentChange"
      @size-change="paginationSizeChange"
    />

    <!--比价折线图-->
    <div
      v-loading="priceLineChartLoading"
      element-loading-background="rgba(0, 0, 0, 0.4)"
      class="price-line-chart-wrap"
    >
      <VendorQoutePriceLineChart
        v-if="currentItemDesc && currentItemPriceNodes"
        :price-nodes="currentItemPriceNodes"
        :vendor-price-nodes="currentItemPriceNodes"
        :item-desc="currentItemDesc"
      />
    </div>

    <!--发起新一轮报价-->
    <ContinueTalkPriceDialog
      v-if="continueTalkPriceDialogVisible"
      :visible.sync="continueTalkPriceDialogVisible"
      :header="header"
      @success="saveContinueTalkPriceSuccess"
    />

    <!--查看公式报价-->
    <FormulaPrice
      v-if="formulaPriceDialogVisible"
      :business-type="BUSINESS_TYPE_ENUM.INQUIRY_LTS"
      :visible.sync="formulaPriceDialogVisible"
      :detail-info="viewRow"
      :query-params="formulaPriceQueryParams"
      readonly
    />

    <!--付款条款-->
    <PaymentTypeDialog
      v-if="paymentTypeDialogVisible"
      :visible.sync="paymentTypeDialogVisible"
      :business-type="BUSINESS_TYPE_ENUM.INQUIRY_LTS"
      :edit-row="viewRow"
      :readonly="paymentTypeDialogIsOnlyRead"
      @savePaymentType="saveBatchPaymentType"
    />

    <!--d 阶梯价-->
    <LadderPrice
      v-if="ladderPriceDialogVisible"
      :visible.sync="ladderPriceDialogVisible"
      :business-type="BUSINESS_TYPE_ENUM.INQUIRY_LTS"
      page-type="quote"
      :edit-row="viewRow"
      readonly
    />

    <!-- 料费分离报价 -->
    <SeparationPriceDialog
      v-if="separationPriceDialogVisible"
      :visible.sync="separationPriceDialogVisible"
      :businessType="BUSINESS_TYPE_ENUM.INQUIRY_LTS"
      role="buyer"
      :edit-row="editRow"
      readonly
    />
  </div>
</template>

<script>
/**
 * 报价评选弹窗
 */
import { inqBuyerHttp } from 'modb@/inquiry/api'
import { downloadFileLink } from 'lib@/utils/file'
import {
  quoteSelectionTableSpanMethod,
  quoteSelectionTableHeaderCellStyle,
  createPricingApproval
} from 'lib@/composition/origin/composition'
import { BUSINESS_TYPE_ENUM, SOU_ORDER_TYPE_ENUM } from 'lib@/composition/origin/enum'
import FormWrapper from 'lib@/components/Table/FormWrapper.vue'
import CPagination from 'lib@/components/c-pagination/index.vue'
import ContinueTalkPriceDialog from './quoteSelection/continueTalkPriceDialog.vue'
import VendorQoutePriceLineChart from 'lib@/composition/origin/vendorQoutePriceLineChart/index.vue'
import priceComparison from 'lib@/composition/origin/priceComparison/index.vue'
import priceComparisonModel from 'lib@/composition/origin/priceComparisonModel/index.vue'
import PaymentTypeDialog from 'lib@/composition/origin/paymentTypeDialog/index.vue'
import LadderPrice from 'lib@/composition/origin/ladderPrice'
import FormulaPrice from 'lib@/composition/origin/formulaPrice/index.vue'
import SeparationPriceDialog from 'lib@/composition/quoteSeparation/templatePriceDialog'
import { meiqlCtrl } from '@/config/meiqlConfig'

export default {
  name: 'QuoteSelection',

  components: {
    FormWrapper,
    CPagination,
    ContinueTalkPriceDialog,
    PaymentTypeDialog,
    VendorQoutePriceLineChart,
    LadderPrice,
    FormulaPrice,
    SeparationPriceDialog
  },

  props: {
    header: {
      type: Object,
      required: true
    }
  },

  data () {
    return {
      pagination: {
        total: 0,
        pageNum: 1,
        pageSize: 15
      },
      quoteSelectionTable: [],
      multipleSelection: [],
      queryParam: {},
      formWrapperConfig: [
        // 物料
        {
          prop: 'itemCode',
          label: this.$t('supRisk.material'),
          type: 'select',
          options: []
        },
        // 供应商
        {
          prop: 'vendorId',
          label: this.$t('bidMod.supplier_price'),
          type: 'select',
          options: []
        },
        // 轮次
        {
          prop: 'round',
          label: this.$t('bidMod.bidingRound'),
          type: 'select',
          options: []
        }
      ],
      viewRow: null,
      continueTalkPriceDialogVisible: false,
      ladderPriceDialogVisible: false,
      formulaPriceDialogVisible: false,
      priceComparisonExportLoading: false,
      paymentTypeDialogVisible: false,
      paymentTypeDialogIsOnlyRead: false,
      currentItemDesc: '',
      currentItemPriceNodes: null,
      currentInquiryItemId: '',
      priceLineChartLoading: false,
      BUSINESS_TYPE_ENUM,
      formulaPriceQueryParams: null,
      separationPriceDialogVisible: false,
      editRow: null
    }
  },

  computed: {
    // 料费分离
    isSeparation () {
      return this.header.orderType === SOU_ORDER_TYPE_ENUM.MATERIAL_COST_SEPARATION
    },
    // 普通报价
    isSimple () {
      return this.header.orderType === SOU_ORDER_TYPE_ENUM.SIMPLE
    }
  },

  created () {
    // 写入查询条件
    this.getSearchInfo()

    this.getQuoteSelectionDetailData()
  },

  methods: {
    /* 获取并写入评选的筛选条件 */
    async getSearchInfo () {
      const response = await inqBuyerHttp.select.getSearchInfo(this.header.projectId)

      if (response && response.data) {
        const { items, vendors, currentRound } = response.data
        if (items && Object.keys(items).length > 0) {
          this.formWrapperConfig.splice(0, 1, {
            ...this.formWrapperConfig[0],
            options: Object.keys(items).map((key, index) => {
              return {
                id: `items-${index}`,
                label: items[key],
                value: key
              }
            })
          })
        }
        if (vendors && Object.keys(vendors).length > 0) {
          this.formWrapperConfig.splice(1, 1, {
            ...this.formWrapperConfig[1],
            options: Object.keys(vendors).map((key, index) => {
              return {
                id: `vendors-${index}`,
                label: vendors[key],
                value: key
              }
            })
          })
        }
        if (currentRound && !Number.isNaN(Number(currentRound))) {
          this.formWrapperConfig.splice(2, 1, {
            ...this.formWrapperConfig[2],
            // 当前轮次，转数组
            options: new Array(currentRound).fill(currentRound).map((item, index) => {
              return {
                id: `round-${index}`,
                label: item - index,
                value: item - index
              }
            }).reverse()
          })
        }
      }
    },

    /* 查询参数 */
    getQueryData (val) {
      this.queryParam = val
      this.$nextTick(() => {
        this.getQuoteSelectionDetailData()
      })
    },

    /* 查询列表数据 */
    async getQuoteSelectionDetailData () {
      // 重置比价折线图参数
      this.currentItemPriceNodes = null
      this.currentItemDesc = ''
      this.currentInquiryItemId = ''

      let paramsData = {
        projectId: this.header.projectId,
        ...this.queryParam,
        // 分页
        pageNum: this.pagination.pageNum,
        pageSize: this.pagination.pageSize
      }
      if (!this.queryParam.round && this.header.currentRound) {
        // 默认当前轮
        paramsData = {
          ...paramsData,
          round: this.header.currentRound
        }
      }

      const response = await inqBuyerHttp.select.getSelectingPage(paramsData)

      if (response && response.data) {
        this.quoteSelectionTable = response.data.list || []
        this.pagination.total = response.data.total
        if (this.quoteSelectionTable.length > 0) {
          this.$nextTick(() => {
            if (this.$refs.quoteSelectionTable) {
              this.$refs.quoteSelectionTable.setCurrentRow(this.quoteSelectionTable[0])
            }
          })
          await this.rowClick(this.quoteSelectionTable[0])
        }
      }
    },

    /* 分页改变 */
    paginationCurrentChange (num) {
      this.pagination.pageNum = num
      this.getQuoteSelectionDetailData()
    },

    /* 分页大小改变 */
    paginationSizeChange (size) {
      this.pagination.pageSize = size
      this.getQuoteSelectionDetailData()
    },

    /* 记录选择行 */
    handleSelectionChange (val) {
      this.multipleSelection = val
    },

    /* 智能评选 */
    async evaluateOne () {
      const response = await inqBuyerHttp.select.autoSelecting(this.header.projectId)
      if (response) {
        // 报价评选排名计算已完成
        this.$alert(this.$t('bidMod.quoteEvaCalc'), this.$t('common.tips'))
        await this.getQuoteSelectionDetailData()
        // 更新单据信息
        this.emitParentGetDetailData()
      }
    },

    /* 判断是否以选行并给予提示 */
    validateSelectionRow () {
      if (this.multipleSelection.length === 0) {
        this.$message.warning(this.$t('bidMod.msgSelData'))
        return false
      }
      return this.multipleSelection.map(item => item.orderItemId)
    },

    /* 入围 / 淘汰 */
    async changeWinStatus (type) {
      const orderItemIds = this.validateSelectionRow()
      if (!orderItemIds) {
        return
      }

      const toWin = type === 'win'
      const response = await inqBuyerHttp.select.changeSelectStatus({ orderItemIds, toWin })
      if (response) {
        this.$message.success(toWin ? this.$t('bidMod.successShortlist') : this.$t('bidMod.successEliminate'))
        // 更新列表
        await this.getQuoteSelectionDetailData()
      }
    },

    /* 中标 / 落标 */
    async changeSelectResult (type) {
      const orderItemIds = this.validateSelectionRow()
      if (!orderItemIds) {
        return
      }

      const toWin = type === 'win'
      if (toWin) {
        // 中标，需要提交中标数量
        for (let item of this.multipleSelection) {
          if (!item.winAmount && item.winAmount !== 0) {
            // 请先填写中标数量
            this.$message.warning(`提交行${this.$t('vendorMod.msgSelBidder')}`)
            return
          }
        }
      }

      const response = await inqBuyerHttp.select.changeSelectResult({
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
        // 更新列表
        await this.getQuoteSelectionDetailData()
      }
    },

    /* 比价表导出 */
    priceComparisonExport () {
      this.priceComparisonExportLoading = true
      downloadFileLink(
        inqBuyerHttp.select.getExportPriceCompareInfoUrl(this.header.projectId),
        `${this.header.souNo}_${this.$t('bidMod.priceCompareList')}.pdf`
      ).then(() => {
        this.priceComparisonExportLoading = false
      }).catch(() => {
        this.priceComparisonExportLoading = false
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },

    /* 公示本轮结果 */
    async openResult () {
      const response = await inqBuyerHttp.select.openResult(this.header.projectId)
      if (response) {
        this.$message.success(this.$t('common.successSubmit'))
        // 更新列表
        await this.getQuoteSelectionDetailData()
      }
    },

    /* 发起新一轮 */
    continueNextRound () {
      this.continueTalkPriceDialogVisible = true
    },

    /* 发起新一轮提交成功 */
    saveContinueTalkPriceSuccess () {
      // 发起回调，刷新页面
      this.emitParentGetDetailData()
    },

    /* 生成价格审批单 */
    async toApprovalBill () {
      const httpMethod = meiqlCtrl.priceApproval === 'Y' ? inqBuyerHttp.select.createPricingApprovalNew : inqBuyerHttp.select.getCreatePricingApprovalUrl
      // 公共方法生成价格审批单
      await createPricingApproval(
        // 简易询价[LTS]
        BUSINESS_TYPE_ENUM.INQUIRY_LTS,
        // apiUrl
        httpMethod(this.header.projectId),
        {},
        this.$router
      )
    },

    /* 账期 START */
    // 打开批量修改账期
    openBatchPaymentTypeDialog () {
      if (!this.validateSelectionRow()) {
        return
      }
      this.viewRow = null
      this.paymentTypeDialogIsOnlyRead = false
      this.paymentTypeDialogVisible = true
    },
    // 保存批量修改账期
    async saveBatchPaymentType (val) {
      const response = await inqBuyerHttp.select.batchUpdatePayment({
        projectId: this.header.projectId,
        orderItemIds: this.multipleSelection.map(item => item.orderItemId),
        paymentList: val || []
      })

      if (response) {
        this.$message.success(this.$t('bidMod.batchChangePaySuccess'))
        await this.getQuoteSelectionDetailData()
      }
    },
    // 查看
    openPaymentTypeDialog (row) {
      this.viewRow = row
      this.paymentTypeDialogIsOnlyRead = true
      this.paymentTypeDialogVisible = true
    },
    /* 账期 END */

    /* 打开阶梯价明细弹窗 */
    openLadderPriceDialog (row) {
      this.viewRow = {
        ...row,
        ladderList: row.ladderPriceList || []
      }
      this.ladderPriceDialogVisible = true
    },

    /* 打开料费分离模板报价明细弹窗 */
    openSeparationPriceDialog (row) {
      const { quoteTempId, quoteTempName, currentRound } = this.header
      this.editRow = {
        ...row,
        quoteTempId,
        quoteTempName,
        currentRound
      }
      this.separationPriceDialogVisible = true
    },

    /* 打开公式报价明细弹窗 */
    openFormulaQuotaDialog (row) {
      this.viewRow = {
        ...row,
        currency: row.orderCurrency
      }
      // 公式报价查询参数
      this.formulaPriceQueryParams = {
        souItemId: row.souItemId || '',
        orderItemId: row.orderItemId || '',
        // 币种，用于基材价格根据汇率转换
        currencyCode: row.orderCurrency,
        vendorId: row.vendorId
      }
      this.formulaPriceDialogVisible = true
    },

    /* 表格行点击 */
    async rowClick (row) {
      // 避免重复点击
      if (this.currentInquiryItemId === row.souItemId) {
        return
      }

      this.priceLineChartLoading = true

      // 查询当前物料的历史价格 再设置当前的比价折线图数据
      const response = await inqBuyerHttp.select.getPriceCompareInfos({
        souItemId: row.souItemId,
        round: row.round
      }).catch(() => { this.priceLineChartLoading = false })

      this.priceLineChartLoading = false
      if (response && response.data) {
        this.currentItemPriceNodes = response.data.priceNodes || {}
        this.currentInquiryItemId = row.souItemId
        this.currentItemDesc = row.itemDesc
      }
    },

    /* 提交中标数量修改 */
    async changeQuoteQuantity () {
      const selection = this.validateSelectionRow()
      if (!selection) {
        return
      }

      // 中标，需要提交中标数量
      for (let item of this.multipleSelection) {
        if (!item.winAmount && item.winAmount !== 0) {
          // 请先填写中标数量
          this.$message.warning(`提交行${this.$t('vendorMod.msgSelBidder')}`)
          return
        }
      }

      const response = await inqBuyerHttp.select.changeQuoteQuantity(this.multipleSelection.map(item => {
        return {
          orderItemId: item.orderItemId,
          winAmount: item.winAmount
        }
      }))
      if (response) {
        this.$message.success(this.$t('common.successSubmit'))
        await this.getQuoteSelectionDetailData()
      }
    },

    /* 统一回调，更新列表数据 */
    emitParentGetDetailData () {
      this.$emit('update')
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
        component: this.isSeparation ? priceComparisonModel : priceComparison,
        params: {
          businessType: BUSINESS_TYPE_ENUM.INQUIRY_LTS,
          pricingType: this.header.orderType,
          row: {
            id: this.header.projectId,
            number: this.header.souNo,
            round: row.currentRound
          }
        },
        closable: true,
        title: `${this.header.souNo} ${this.$t('bidMod.priceCompare')}`,
        name: `priceComparison-${this.header.souNo}`
      }

      this.$emit('tab-add', tab)
    }
  }
}
</script>

<style lang="scss" scoped>
.form-wrapper-slot-text {
  height: 32px;
  line-height: 32px;
}

.quote-selection-table-operation {
  margin: 12px 0;
  .el-button {
    height: 24px;
    min-width: 56px;
    border-radius: 2px;
    padding: 1px 10px;
  }
}

.dropdown-button-wrap {
  margin-left: 10px;
}

.mr-10 {
  margin-right: 10px;
}

.price-line-chart-wrap {
  width: 100%;
  height: 400px;
  margin-bottom: 20px;
}

// 强制隐藏二级表头的间隔列头
.quote-selection-table.el-table :deep(th.gutter) {
  display: none !important;
}
</style>
