<template>
  <div class="quote-selection">
    <FormWrapper
      :form-array="formWrapperConfig"
      init-active
      form-label-width="120px"
      @getFormData="getQueryData"
    >
      <template #inquiryRule>
        <div class="form-wrapper-slot-text">
          {{ $getDictLabel('RFQ_SCORE_RULE', inquiryTrackingData.header.inquiryRule) }}
        </div>
      </template>
      <template #quoteRule>
        <div class="form-wrapper-slot-text">
          {{ $getDictLabel('RFQ_QUOTE_TYPE', inquiryTrackingData.header.quoteRule) }}
        </div>
      </template>
    </FormWrapper>

    <div class="quote-selection-table-operation">
      <!--b 智能评选-->
      <el-button type="primary" @click="evaluateOne">
        {{ $t('bidMod.intelligentEvaluation') }}
      </el-button>

      <!--入围或淘汰 -->
      <el-dropdown class="dropdown-button-wrap" @command="changeSelectSubmit">
        <el-button type="primary">
          {{ $t('bidMod.nextOrEliminate') }}<em class="el-icon-arrow-down el-icon--right" />
        </el-button>

        <!--入围或淘汰 -->
        <el-dropdown-menu slot="dropdown">
          <!--入围下一轮-->
          <el-dropdown-item command="changeSelectStatus/shortlisted">
            {{ $t('bidMod.toNextRoundConfirm') }}
          </el-dropdown-item>
          <!--淘汰-->
          <el-dropdown-item command="changeSelectStatus/failed">
            {{ $t('bidMod.toEliminateConfirm') }}
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>

      <!--决标操作-->
      <el-dropdown class="dropdown-button-wrap" @command="changeSelectSubmit">
        <el-button type="primary">
          {{ $t('bidMod.bidAwardOperation') }}<em class="el-icon-arrow-down el-icon--right" />
        </el-button>

        <el-dropdown-menu slot="dropdown">
          <!--中标-->
          <el-dropdown-item command="changeSelectResult/win">
            {{ $t('bid_mod.winTheBidding') }}
          </el-dropdown-item>
          <!--落标-->
          <el-dropdown-item command="changeSelectResult/lose">
            {{ $t('bid_mod.lossTheBidding') }}
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>

      <!--b 比价表打印-->
      <el-button
        type="primary"
        :loading="priceComparisonExportLoading"
        style="margin-left: 10px;"
        @click="priceComparisonExport"
      >
        比价表打印
      </el-button>

      <!--b 公示本轮结果-->
      <el-button type="primary" @click="openResult">
        {{ $t('bidMod.toPublishDialog') }}
      </el-button>

      <!--b 发起新一轮-->
      <el-button type="primary" @click="continueNextRound">
        发起新一轮
      </el-button>

      <!--b 生成价格审批单-->
      <el-button type="primary" @click="toApprovalBill">
        {{ $t('bidMod.toPriceApproval') }}
      </el-button>

      <!--b 批量修改账期-->
      <el-button type="primary" @click="batchPaymentDays">
        批量修改账期
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
      <el-table-column align="center" type="index" width="50" />
      <el-table-column align="center" type="selection" width="55" />
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
        prop="selectStatus"
        label="本轮入围情况"
        width="100"
        :formatter="(row, column, value) => $getDictLabel('INQ_SELECT_STATUS', value)"
        show-overflow-tooltip
      />

      <!--t 评选情况-->
      <el-table-column
        align="center"
        prop="selectResult"
        :label="$t('bidMod.selectSituation')"
        width="100"
        :formatter="(row, column, value) => $getDictLabel('INQ_SELECT_RESULT', value)"
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
        prop="demandQuantity"
        :label="$t('bidMod.demandQuantity')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 中标数量-->
      <el-table-column
        align="center"
        prop="quoteQuantity"
        :label="$t('bidMod.quotaQuantity')"
        width="130"
      >
        <template v-slot="{ row }">
          <el-input v-model="row.quoteQuantity" @click.native.stop />
        </template>
      </el-table-column>

      <!--t 目标价(未税)-->
      <el-table-column
        align="center"
        prop="notaxTargetPrice"
        label="目标价(未税)"
        width="100"
        show-overflow-tooltip
      />

      <!--t 报价币种-->
      <el-table-column
        align="center"
        prop="quoteCurrency"
        :label="$t('bidMod.bidingCurrency2')"
        width="100"
        :formatter="(row, column, value) => $getDictLabel('currency', value)"
        show-overflow-tooltip
      />

      <!--t 报价税率-->
      <el-table-column
        align="center"
        prop="taxKey"
        label="报价税率"
        width="100"
        :formatter="(row, column, value) => $getDictLabel('tax', value)"
        show-overflow-tooltip
      />

      <!--t 原币未税单价-->
      <el-table-column
        align="center"
        prop="notaxPrice"
        label="原币未税单价"
        width="100"
        show-overflow-tooltip
      />

      <!--t 原币含税单价-->
      <el-table-column
        align="center"
        prop="taxPrice"
        label="原币含税单价"
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
        prop="inqNotaxPrice"
        label="本币未税价"
        width="100"
        show-overflow-tooltip
      />

      <!--t 本币币种-->
      <el-table-column
        align="center"
        prop="inqCurrency"
        :label="$t('bidMod.appraisLocalCurrency')"
        width="100"
        :formatter="(row, column, value) => $getDictLabel('currency', value)"
        show-overflow-tooltip
      />

      <!--t 与目标价价差-->
      <el-table-column
        align="center"
        prop="priceDifference"
        label="与目标价价差"
        width="100"
        :formatter="(row, column, value) => value || value === 0 ? `${value}%` : ''"
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
      <el-table-column
        align="center"
        prop="compositeScore"
        :label="$t('bidMod.synthesisScore')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 排名-->
      <el-table-column
        align="center"
        prop="ranking"
        :label="$t('bidMod.tech_ranking')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 付款条款-->
      <el-table-column align="center" :label="$t('paymentType.paymentType')" width="100">
        <template v-slot="{ row }">
          <el-button type="text" @click="openPaymentTypeDialog(row)">
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
            <!--公式报价明细-->
            <el-button
              v-if="row.isFormula === 'Y'"
              type="text"
              @click="openFormulaQuotaDialog(row)"
            >
              公式报价明细
            </el-button>

            <!--阶梯价明细-->
            <el-button
              v-if="row.isLadder === 'Y'"
              type="text"
              @click="openLadderPriceDialog(row)"
            >
              阶梯价明细
            </el-button>

            <!--模板报价明细-->
            <el-button
              v-if="row.isTemplate === 'Y'"
              type="text"
              @click="openTemplatePriceDialog(row)"
            >
              {{ $t('templatePrice.detailLabel') }}
            </el-button>
          </template>
        </el-table-column>

        <el-table-column
          align="center"
          prop="priceComparison"
          width="100"
        >
          <template v-slot="{ row }">
            <el-button type="text" @click="openPriceComparisonTab(row)">
              比价
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
      :header="inquiryTrackingData.header"
      @saveContinueTalkPriceSuccess="saveContinueTalkPriceSuccess"
    />

    <!--查看阶梯价-->
    <LadderPriceDetail
      v-if="ladderPriceDialogVisible"
      :visible.sync="ladderPriceDialogVisible"
      :business-type="BUSINESS_TYPE_ENUM.INQUIRY"
      :edit-row="viewRow"
      readonly
      page-type="quote"
    />

    <!--查看公式报价-->
    <FormulaPriceDialog
      v-if="formulaPriceDialogVisible"
      :visible.sync="formulaPriceDialogVisible"
      :view-row="viewRow"
    />

    <!--付款条款-->
    <PaymentTypeDialog
      v-if="paymentTypeDialogVisible"
      :visible.sync="paymentTypeDialogVisible"
      business-type="INQUIRY"
      :edit-row="viewRow"
      :readonly="paymentTypeDialogIsOnlyRead"
      @savePaymentType="saveBatchPaymentType"
    />

    <!--模板报价-->
    <TemplatePriceDialog
      v-if="templatePriceDialogVisible"
      :visible.sync="templatePriceDialogVisible"
      :business-type="BUSINESS_TYPE_ENUM.INQUIRY"
      :query-params="queryParams"
      readonly
    />
  </div>
</template>

<script>
/**
 * 报价评选弹窗
 */
import { downloadFileLink } from 'lib@/utils/file'
import {
  quoteSelectionTableSpanMethod,
  quoteSelectionTableHeaderCellStyle
} from 'lib@/composition/origin/composition'
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import CPagination from 'lib@/components/c-pagination'
import ContinueTalkPriceDialog from './quoteSelection/continueTalkPriceDialog'
import VendorQoutePriceLineChart from '@/library/composition/origin/vendorQoutePriceLineChart'
import priceComparison from '@/library/composition/origin/priceComparison'
import FormulaPriceDialog from 'lib@/composition/inquiryBySimple/formulaPriceDialog'
import PaymentTypeDialog from 'lib@/composition/origin/paymentTypeDialog'
import TemplatePriceDialog from 'lib@/composition/quoteTemplate/templatePriceDialog'
import LadderPriceDetail from 'lib@/composition/origin/ladderPrice'

export default {
  name: 'QuoteSelection',

  components: {
    FormWrapper,
    CPagination,
    ContinueTalkPriceDialog,
    FormulaPriceDialog,
    PaymentTypeDialog,
    VendorQoutePriceLineChart,
    TemplatePriceDialog,
    LadderPriceDetail
  },

  props: {
    inquiryTrackingData: {
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
      templatePriceDialogVisible: false,
      queryParams: null

    }
  },

  created () {
    // 写入查询条件
    this.getSearchInfo()

    this.getQuoteSelectionDetailData()
  },

  methods: {
    /* 获取并写入评选的筛选条件 */
    getSearchInfo () {
      this.$api.inq.inquiryBySimple.getSearchInfo(this.inquiryTrackingData.header.inquiryId).then(data => {
        if (data && data.data) {
          const { items, vendors, currentRound } = data.data
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
      })
    },

    /* 查询参数 */
    getQueryData (val) {
      this.queryParam = val
      this.$nextTick(() => {
        this.getQuoteSelectionDetailData()
      })
    },

    /* 查询列表数据 */
    getQuoteSelectionDetailData () {
      // 重置比价折线图参数
      this.currentItemPriceNodes = null
      this.currentItemDesc = ''
      this.currentInquiryItemId = ''

      let paramsData = {
        ...this.queryParam,
        // 分页
        pageNum: this.pagination.pageNum,
        pageSize: this.pagination.pageSize
      }
      if (!this.queryParam.round && this.inquiryTrackingData.header.round) {
        // 默认当前轮
        paramsData = {
          ...paramsData,
          round: this.inquiryTrackingData.header.round
        }
      }
      this.$api.inq.inquiryBySimple.getQuoteSelectionDetailData(paramsData, this.inquiryTrackingData.header.inquiryId).then(data => {
        if (data && data.data) {
          this.quoteSelectionTable = data.data.list || []
          this.pagination.total = data.data.total
          if (this.quoteSelectionTable.length > 0) {
            this.$nextTick(() => {
              this.$refs.quoteSelectionTable.setCurrentRow(this.quoteSelectionTable[0])
            })
            this.rowClick(this.quoteSelectionTable[0])
          }
        }
      })
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

    /* 判断是否以选行并给予提示 */
    validateSelectionRow () {
      if (this.multipleSelection.length === 0) {
        this.$message.warning(this.$t('components.userSelection.selectData'))
        return false
      }
      return this.multipleSelection.map(item => item.quoteSelectionId)
    },

    /* 智能评选 */
    evaluateOne () {
      this.$api.inq.inquiryBySimple.getEvaluateOne(this.inquiryTrackingData.header.inquiryId).then(data => {
        if (data) {
          this.$alert('报价评选排名计算已完成', this.$t('common.tips'))
          this.getQuoteSelectionDetailData()
        }
      })
    },

    /* 入围 / 淘汰 / 中标 / 落标 */
    changeSelectSubmit (apiName) {
      const selection = this.validateSelectionRow()
      if (!selection) {
        return
      }

      if (apiName === 'changeSelectResult/win') {
        // 中标，需要提交中标数量
        let multipleSelectionNext = this.multipleSelection.filter(item => !item.quoteQuantity && item !== 0)
        if (multipleSelectionNext && multipleSelectionNext.length > 0) {
          return this.$message.warning(this.$t('vendorMod.msgSelBidder'))
        }
      }

      this.$api.inq.inquiryBySimple.changeSelectSubmit(selection, apiName).then(data => {
        if (data) {
          if (apiName === 'changeSelectResult/win') {
            // 中标，去提交中标数量
            this.changeQuoteQuantity()
          } else {
            this.$message.success(this.$t('common.successSubmit'))
            // 更新列表
            this.getQuoteSelectionDetailData()
          }
        }
      })
    },

    /* 比价表导出 */
    priceComparisonExport () {
      this.priceComparisonExportLoading = true
      downloadFileLink(
        `/api-inq/quote/selection/exportPriceCompareInfo/${this.inquiryTrackingData.header.inquiryId}`,
        `${this.inquiryTrackingData.header.inquiryNo}_比价表.pdf`
      ).then(() => {
        this.priceComparisonExportLoading = false
      }).catch(() => {
        this.priceComparisonExportLoading = false
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },

    /* 公示本轮结果 */
    openResult () {
      this.$api.inq.inquiryBySimple.openResult(this.inquiryTrackingData.header.inquiryId).then(data => {
        if (data) {
          this.$message.success(this.$t('common.successSubmit'))
          // 更新列表
          this.getQuoteSelectionDetailData()
        }
      })
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
    toApprovalBill () {
      this.$api.inq.inquiryBySimple.createPricingApproval(this.inquiryTrackingData.header.inquiryId).then(data => {
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
                sourceType: 'INQUIRY'
              }
            })
          })
        }
      })
    },

    /* 批量修改账期 */
    batchPaymentDays () {
      if (!this.validateSelectionRow()) {
        return
      }
      this.viewRow = null
      this.paymentTypeDialogIsOnlyRead = false
      this.paymentTypeDialogVisible = true
    },

    /* 批量修改账期 */
    saveBatchPaymentType (val) {
      this.$api.inq.inquiryBySimple.batchUpdatePayment({
        inquiryId: this.inquiryTrackingData.header.inquiryId,
        quoteItemIds: this.multipleSelection.map(item => item.quoteItemId),
        paymentList: val || []
      }).then(() => {
        this.$message.success('批量修改账期成功')
        this.getQuoteSelectionDetailData()
      })
    },

    /* 打开阶梯价明细弹窗 */
    openLadderPriceDialog (row) {
      this.viewRow = row
      this.ladderPriceDialogVisible = true
    },

    /* 打开公式报价明细弹窗 */
    openFormulaQuotaDialog (row) {
      this.viewRow = row
      this.formulaPriceDialogVisible = true
    },

    /* 打开模板报价明细弹窗 */
    openTemplatePriceDialog (row) {
      this.queryParams = {
        inquiryId: row.inquiryId,
        inquiryItemId: row.inquiryItemId,
        vendorId: row.vendorId
      }

      this.templatePriceDialogVisible = true
    },

    /* 查看付款方式 */
    openPaymentTypeDialog (row) {
      this.viewRow = {
        inquiryItemId: row.inquiryItemId,
        quoteItemId: row.quoteItemId
      }
      this.paymentTypeDialogIsOnlyRead = true
      this.paymentTypeDialogVisible = true
    },

    /* 表格行点击 */
    async rowClick (row) {
      // 避免重复点击
      if (this.currentInquiryItemId === row.inquiryItemId) return

      this.priceLineChartLoading = true

      // 查询当前物料的历史价格 再设置当前的比价折线图数据
      const response = await this.$api.inq.inquiryBySimple.getPriceCompareInfos({
        inquiryItemId: row.inquiryItemId,
        round: row.round
      })
      this.priceLineChartLoading = false
      if (response && response.data) {
        this.currentItemPriceNodes = response.data.priceNodes || {}
        this.currentInquiryItemId = row.inquiryItemId
        this.currentItemDesc = row.itemDesc
      }
    },

    /* 提交中标数量修改 */
    changeQuoteQuantity () {
      let data = this.multipleSelection.map(item => {
        return {
          quoteSelectionId: item.quoteSelectionId,
          quoteQuantity: item.quoteQuantity
        }
      })
      this.$api.inq.inquiryBySimple.changeQuoteQuantity(data).then(() => {
        this.$message.success(this.$t('common.successSubmit'))
        this.getQuoteSelectionDetailData()
      })
    },

    /* 统一回调，更新列表数据 */
    emitParentGetDetailData () {
      this.$emit('updateDetailData')
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
      const header = this.inquiryTrackingData.header
      const tab = {
        component: priceComparison,
        params: {
          businessType: BUSINESS_TYPE_ENUM.INQUIRY,
          pricingType: header.quoteType,
          row: {
            id: header.inquiryId,
            number: header.inquiryNo,
            round: row.round
          }
        },
        closable: true,
        title: `${header.inquiryNo} 比价`,
        name: `priceComparison-${header.inquiryNo}`
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
