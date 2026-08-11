<template>
  <el-container class="flex-container tender-selection-wrap" direction="vertical">
    <el-main>
      <SearchForm @getQueryData="getQueryData" />

      <div class="the_btn_check" style="margin-bottom: 10px">
        <!--智能决标-->
        <el-button
          type="primary"
          :disabled="readonly"
          @click="intelligentEvaluation"
        >
          {{ $t('bidMod.intelligentEvaluations') }}
        </el-button>

        <!--提交中标数量修改-->
        <el-button :disabled="readonly" @click="changeQuotaQuantity">
          {{ $t('bidMod.submitBidModify') }}
        </el-button>

        <!-- 生成寻源结果审批单 -->
        <el-button :disabled="readonly" @click="createPricingApproval">
          {{ $t("bidMod.newSourcingResult") }}
        </el-button>

        <!--自定义导出-->
        <ExportExcel
          :page-url="tableViewUrl"
          export-mode="front"
          :table-header="tableHeader"
          :dict-codes="dictCodes"
          :filter-params="queryParam"
          :timeout="10000000"
          :disabled="readonly"
        />
      </div>

      <TableView
        ref="tenderSelectionTable"
        :table-data="tableData"
        :table-header="tableHeader"
        front-paging
        open-all-check
        row-key="orderLineId"
        request-method="post"
        :pre-query-data="queryParam"
        checkbox
        reserve-selection
        open-custom-table
        :row-click-check="false"
        :check-change="checkChange"
        :url="tableViewUrl"
      />

      <!--批量维护付款条款-->
      <PaymentTypeDialog
        v-if="paymentTypeDialogVisible"
        :visible.sync="paymentTypeDialogVisible"
        :business-type="BUSINESS_TYPE_ENUM.COMPETITION"
        :edit-row="viewRow"
        readonly
      />
    </el-main>
  </el-container>
</template>

<script>
/**
 * 评选
 */
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import { compBuyerHttp } from 'modb@/competition/api'
import SearchForm from './tenderSelection/searchForm'
import TableView from 'lib@/components/Table/TableView'
import ExportExcel from 'lib@/components/export-excel'
import PaymentTypeDialog from 'lib@/composition/origin/paymentTypeDialog'
import { meiqlCtrl } from '@/config/meiqlConfig'

export default {
  name: 'TenderSelection',

  components: {
    SearchForm,
    TableView,
    ExportExcel,
    PaymentTypeDialog
  },

  props: {
    projectId: {
      type: [Number, String],
      default: ''
    },
    readonly: {
      type: Boolean,
      default: false
    },
    // 是否当前tab页
    isCurrentActiveTab: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      tableViewUrl: compBuyerHttp.select.listPageUrl,
      preform: {},
      tableData: [],
      tableHeader: [
        // 业务实体
        {
          prop: 'orgOuName',
          label: this.$t('bidMod.businessEntity'),
          minWidth: 150
        },
        // 库存组织
        {
          prop: 'orgInvName',
          label: this.$t('bidMod.inventory'),
          minWidth: 150
        },
        // 价格类型
        {
          prop: 'priceType',
          label: this.$t('bidMod.priceType'),
          minWidth: 150,
          dataType: 'dict',
          code: 'PRICE_TYPE'
        },
        // 物料编码
        {
          prop: 'itemCode',
          label: this.$t('bidMod.materialCode'),
          minWidth: 120
        },
        // 物料名称
        {
          prop: 'itemDesc',
          label: this.$t('bidMod.itemName'),
          minWidth: 120
        },
        // 小类
        {
          prop: 'categoryName',
          label: this.$t('bidMod.subclass'),
          minWidth: 120
        },
        // 税率
        {
          prop: 'taxKey',
          label: this.$t('bidMod.taxRate'),
          minWidth: 120,
          dataType: 'dict',
          code: 'tax'
        },
        // 单位
        {
          prop: 'unit',
          label: this.$t('bidMod.tech_Unit'),
          minWidth: 120,
          dataType: 'dict',
          code: 'unit'
        },
        // 本轮最低价 latestNotaxPrice[本轮原币未税最新价]
        {
          prop: 'latestOrderNotaxPrice',
          label: '最新价',
          minWidth: 120
        },
        // 价格执行有效期自
        {
          prop: 'priceStartTime',
          label: this.$t('bidMod.priceCarryValidForm_price'),
          minWidth: 150,
          formattor: val => this.$parseTime(val)
        },
        // 价格执行有效期至
        {
          prop: 'priceEndTime',
          label: this.$t('bidMod.appraisPriceCarryTo'),
          minWidth: 150,
          formattor: val => this.$parseTime(val)
        },
        // 报价供应商
        {
          prop: 'vendorName',
          label: this.$t('bidMod.appraisQuoteSupplier'),
          minWidth: 150
        },
        // 报价供应商编码
        {
          prop: 'vendorCode',
          label: this.$t('bidMod.quoteSupplierCode'),
          minWidth: 150
        },
        // 原币未税价(单价)
        {
          prop: 'orderNotaxPrice',
          label: this.$t('bidMod.untaxedValue'),
          minWidth: 140
        },
        // 原币含税价(单价)
        {
          prop: 'orderTaxPrice',
          label: this.$t('bidMod.originalCurrency'),
          minWidth: 140
        },
        // 报价总金额
        {
          prop: 'totalOrderNotaxPrice',
          label: this.$t('bidMod.totalQuotation'),
          minWidth: 120
        },
        // 原币币种
        {
          prop: 'orderCurrency',
          label: this.$t('bidMod.appraisCurrency'),
          minWidth: 130,
          dataType: 'dict',
          code: 'currency'
        },
        // 本币未含税价
        {
          prop: 'standardNotaxPrice',
          label: this.$t('bidMod.localCurrencyUnit'),
          minWidth: 120
        },
        // 需求数量
        {
          prop: 'requireQuantity',
          label: this.$t('bidMod.appraisRequired'),
          minWidth: 120
        },
        // 中标数量
        {
          showType: 'input',
          prop: 'winAmount',
          label: this.$t('bidMod.quotaQuantity'),
          minWidth: 100
        },
        // 本币币种
        {
          prop: 'standardCurrency',
          label: this.$t('bidMod.appraisLocalCurrency'),
          minWidth: 100,
          dataType: 'dict',
          code: 'currency'
        },
        // 汇率
        {
          prop: 'priceTax',
          label: this.$t('bidMod.appraisRate'),
          minWidth: 100
        },
        // 贸易条款
        {
          prop: 'tradeTerm',
          label: this.$t('bidMod.technical_tradeClause'),
          minWidth: 100,
          dataType: 'dict',
          code: 'trade_clause'
        },
        // 付款条款
        {
          prop: 'paymentType',
          label: this.$t('bidMod.paymentTerms'),
          minWidth: 100,
          showType: 'button',
          btnStyle: 'text',
          formattor: () => this.$t('common.view'),
          callback: row => this.openPaymentTypeDialog(row)
        },
        // 质保期(月)
        {
          prop: 'warrantyPeriod',
          label: this.$t('bidMod.tech_warrantyPeriod'),
          minWidth: 120
        },
        // 采购申请号
        {
          prop: 'sourceFromNo',
          label: this.$t('bidMod.purchaseRequestNum'),
          minWidth: 150
        },
        // 采购申请行号
        {
          prop: 'sourceFromLineNo',
          label: this.$t('bidMod.purchaseRequestLine'),
          minWidth: 150
        },
        // 价格得分
        {
          prop: 'priceScore',
          label: this.$t('bidMod.appraisPricePoints'),
          minWidth: 100
        },
        // 综合得分
        {
          prop: 'compositeScore',
          label: this.$t('bidMod.compositeScore'),
          minWidth: 100
        },
        // 排名
        {
          prop: 'ranking',
          label: this.$t('bidMod.tech_ranking'),
          minWidth: 80
        },
        // 评选结果
        {
          prop: 'winStatus',
          label: this.$t('bidMod.selectionStatus'),
          minWidth: 120,
          dataType: 'dict',
          code: 'SOU_WIN_STATUS'
        },
        // 预计采购金额（万元）
        {
          prop: 'buyAmount',
          label: this.$t('bidMod.expectedPurchaseAmount'),
          minWidth: 200
        },
        // 备注
        {
          prop: 'remark',
          label: this.$t('common.remark'),
          minWidth: 100
        }
      ],
      dictCodes: {
        priceType: 'PRICE_TYPE',
        taxKey: 'tax',
        unit: 'unit',
        orderCurrency: 'currency',
        standardCurrency: 'currency',
        tradeTerm: 'trade_clause',
        winStatus: 'SOU_WIN_STATUS'
      },
      queryParam: {},
      checkRows: [],
      paymentTypeDialogVisible: false,
      viewRow: null,
      BUSINESS_TYPE_ENUM
    }
  },

  watch: {
    isCurrentActiveTab: {
      handler (newValue, oldValue) {
        // 切换到当前标签页
        if (newValue && !oldValue) {
          this.getQueryData()
        }
      },
      immediate: true
    }
  },

  methods: {
    /* 查询 */
    getQueryData (val = {}) {
      this.queryParam = {
        ...val,
        projectId: this.projectId
      }
      this.checkRows = []

      this.$nextTick(() => {
        this.$refs.tenderSelectionTable.query()
      })
    },

    /* 表格选择行 */
    checkChange (rows) {
      this.checkRows = rows || []
    },

    /* 智能决标 */
    async intelligentEvaluation () {
      const response = await compBuyerHttp.select.intelligent({ projectId: this.projectId })
      if (response) {
        this.$message.success(this.$t('common.successSubmit'))
        this.getQueryData()
        // 更新基础数据
        this.$emit('fetchBaseInfo')
      }
    },

    /* 提交中标数量修改 */
    async changeQuotaQuantity () {
      if (this.checkRows.length === 0) {
        // '请选择提交行'
        this.$message.warning(this.$t('cusEntry.supplement20250211.selectSubmitRow'))
        return
      }

      for (const item of this.checkRows) {
        if (!item.winAmount && item.winAmount !== 0) {
          // '提交行请输入中标数量！'
          this.$message.warning(this.$t('cusEntry.supplement20250211.submitRowInputWinningQuantity'))
          return
        }
      }

      const params = this.checkRows.map(item => {
        return {
          orderItemId: item.orderItemId,
          winAmount: item.winAmount
        }
      })
      const response = await compBuyerHttp.select.changeWinAmount(params)
      if (response) {
        this.$message.success(this.$t('common.successSubmit'))
        this.getQueryData()
      }
    },

    /* 生成寻源结果审批单 */
    async createPricingApproval () {
      const confirmSubmitResult = await this.$confirm('确定生成寻源结果审批单吗？', {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => { /* nothing */ })

      if (confirmSubmitResult !== 'confirm') {
        return
      }

      const httpMethod = meiqlCtrl.priceApproval === 'Y' ? compBuyerHttp.select.createPricingApprovalNew : compBuyerHttp.select.getCreatePricingApprovalUrl

      const response = await httpMethod(this.projectId)
      if (!response || !response.data) {
        return
      }
      // '价格审批单创建成功，需要跳转到价格审批单页面吗？'
      const confirmResult = await this.$confirm(this.$t('competition.createPricingApprovalAndJump'), {
        // '跳转到价格审批单页面'
        confirmButtonText: this.$t('competition.jumpPricingApproval'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })

      if (confirmResult === 'confirm') {
        // 确认跳转
        this.$router.push({
          name: 'priceApproval',
          params: {
            from: 'fromFun',
            // 业务单据ID 新版 approvalId 旧版 approvalHeaderId
            formId: response.data.approvalHeaderId ?? response.data.approvalId,
            // 业务单据ID
            formNo: response.data.approvalNo,
            // 功能
            funName: 'priceApproval',
            // 来源类型
            sourceType: 'COMPETITION'
          }
        })
      }
    },

    /* 打开付款条款弹窗 */
    openPaymentTypeDialog (row) {
      this.viewRow = {
        paymentList: row.paymentList
      }
      this.paymentTypeDialogVisible = true
    }
  }
}
</script>

<style lang="scss" scoped>
.tender-selection-wrap {
  padding-top: 10px;
}
</style>
