<template>
  <el-container class="flex-container the_doBidingDetail_wrapper" direction="vertical">
    <el-main>
      <!--按钮操作区-->
      <p style="padding-left:10px;">
        <!--提交-->
        <el-button
          type="primary"
          :loading="saveOrSubmitButtonLoading"
          @click="saveOrSubmit('SUBMIT')"
        >
          {{ $t("common.submit") }}
        </el-button>

        <!--暂存-->
        <el-button
          v-if="!proxyQuoteParams.visible"
          type="primary"
          :loading="saveOrSubmitButtonLoading"
          @click="saveOrSubmit('SAVE')"
        >
          {{ $t("common.staging") }}
        </el-button>

        <!--刷新-->
        <el-button @click="getOrderDetail">
          {{ $t("common.refresh") }}
        </el-button>

        <!--返回-->
        <el-button v-if="!proxyQuoteParams.visible" @click="backTo">
          {{ $t("common.backTo") }}
        </el-button>
      </p>

      <!--进度 固定4-->
      <div class="the_progress">
        <el-steps :active="4">
          <el-step :title="$t('bidMod.published')" />
          <el-step :title="$t('bidMod.signingUp')" />
          <el-step :title="$t('bidMod.registered')" />
          <el-step :title="$t('bidMod.eligibilityConfirm')" />
          <el-step :title="$t('bidMod.quote')" />
          <el-step :title="$t('bidMod.finishBid')" />
        </el-steps>
      </div>

      <!--报价截止倒计时-->
      <div class="cur-quote-deadline">
        <DynamicCutoffTime
          :label="$t('bidMod.curQuoteDeadline')"
          :deadline-time="biddingData.orderEndTime"
        />
      </div>

      <!--单据信息-->
      <div class="the_display_content">
        <SrmRow type="flex">
          <!--当前轮次-->
          <SrmCol>
            <span>{{ $t("bidMod.currentRound") }}</span>{{ biddingData.currentRound }}
          </SrmCol>
          <!--评分规则-->
          <SrmCol>
            <span>{{ $t("bidMod.inquiryRule") }}</span>
            {{ $getDictLabel('SOU_SCORE_RULE_TYPE', biddingData.scoreRuleType) }}
          </SrmCol>
          <!--决标方式-->
          <SrmCol>
            <span>{{ $t("bidMod.bidingAwardWay") }}</span>
            {{ $getDictLabel('SOU_ORDER_WAY', biddingData.orderWay) }}
          </SrmCol>
          <!--询价类型-->
          <SrmCol>
            <span>{{ $t("bidMod.bidingType1") }}</span>
            {{ $getDictLabel('SOU_BRG_TYPE', biddingData.bargainType) }}
          </SrmCol>
        </SrmRow>

        <!--代理报价上传授权证明，非必填-->
        <SrmRow v-if="proxyQuoteParams.visible" type="flex">
          <!--代理报价授权证明-->
          <SrmCol :init-col="1" class="flex-col">
            <span class="label">{{ $t('bidMod.proxyQuoteCert') }}</span>

            <SrmCommonFile
              :default-file="{
                fileId: proxyData.proxyDocId,
                fileName: proxyData.proxyFileName
              }"
              style="flex: 1; max-width: 250px;"
              @on-change="proxyFileChange"
            />
          </SrmCol>
        </SrmRow>
      </div>

      <!--商务信息区-->
      <div style="padding: 5px">
        <span style="padding: 0 11px;float: left;line-height: 30px;">
          {{ $t("bidMod.businessInfo") }}
        </span>

        <el-dropdown @command="handleCommand">
          <el-button type="primary">
            {{ $t("bidMod.batchOperation") }}<em class="el-icon-arrow-down el-icon--right" />
          </el-button>
          <el-dropdown-menu slot="dropdown">
            <!-- 批量维护付款条款 -->
            <el-dropdown-item command="openBatchPaymentTypeDialog">
              {{ $t("bidMod.batchPaymentTerm") }}
            </el-dropdown-item>
            <!-- 批量维护税率 -->
            <el-dropdown-item command="openBatchAddTaxRateDialog">
              {{ $t("bidMod.batchAddTaxRate") }}
            </el-dropdown-item>
            <!-- 批量维护币种 -->
            <el-dropdown-item command="openBatchMaintainCurrencyDialog">
              {{ $t("bidMod.batchAddCurrency") }}
            </el-dropdown-item>
            <!-- 批量维护承诺交货期 -->
            <el-dropdown-item command="openBatchMaintainDeliverDateDialog">
              {{ $t("bidMod.batchAddDeliverDate") }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>

      <!--物料表格-->
      <div class="table1">
        <el-table
          ref="itemListTable"
          :data="itemList.slice((currentPage - 1) * pageSize, currentPage * pageSize)"
          style="width: 100%"
          border
          height="200px"
        >
          <el-table-column
            type="index"
            :label="$t('common.sort')"
            width="50"
          />

          <!--业务实体-->
          <el-table-column
            prop="orgOuName"
            :label="$t('bid_mod.businessEntity')"
            width="150"
            show-overflow-tooltip
          />

          <!--库存组织-->
          <el-table-column
            prop="orgInvName"
            :label="$t('bid_mod.inv')"
            width="150"
            show-overflow-tooltip
          />

          <!--物料编码-->
          <el-table-column
            prop="itemCode"
            :label="$t('bidMod.targetNum')"
            width="150"
            show-overflow-tooltip
            :formatter="(row, column, cellValue) => row.noCodeItem === 'Y' ? '' : cellValue"
          />

          <!--物料名称-->
          <el-table-column
            prop="itemDesc"
            :label="$t('bidMod.targetDesc')"
            min-width="150"
            show-overflow-tooltip
          />

          <!--报价币种-->
          <el-table-column
            prop="orderCurrency"
            :label="$t('bidMod.bidingCurrency2')"
            width="150"
            :render-header="_addStarToColumn"
          >
            <template v-slot="scope">
              <DictSelect
                v-if="currencyList.length"
                v-model="scope.row.orderCurrency"
                code="currency"
                :clearable="false"
                :transform-options="transformCurrencyOptions"
                @change="val => orderCurrencyChange(val, scope)"
              />
            </template>
          </el-table-column>

          <!--税率-->
          <el-table-column
            prop="taxKey"
            :label="$t('bid_mod.taxRate')"
            width="140"
            show-overflow-tooltip
            :render-header="_addStarToColumn"
          >
            <template v-slot="scope">
              <DictSelect
                v-model="scope.row.taxKey"
                code="tax"
                :clearable="false"
                @change-value="(value, dictItem) => taxKeyChange(value, dictItem, scope)"
              />
            </template>
          </el-table-column>

          <!--未税单价-->
          <el-table-column
            align="right"
            prop="orderNotaxPrice"
            :label="$t('bid_mod.untaxedPrice')"
            width="130"
            :render-header="_addStarToColumn"
          >
            <template v-slot="scope">
              <!--模型报价、公式报价、料费分离无法输入-->
              <el-input
                v-if="!isModelPricing && !isFormulaPricing && !isSeparation && scope.row.isLadder !== 'Y'"
                v-model="scope.row.orderNotaxPrice"
                v-input-format="{ type: 'float', negative: false, zero: false }"
                @change="value => calcTaxPrice(value, scope)"
              />
              <span v-else>{{ scope.row.orderNotaxPrice }}</span>
            </template>
          </el-table-column>

          <!--含税单价-->
          <el-table-column
            align="right"
            prop="orderTaxPrice"
            :label="$t('bidMod.priceIncludeTax')"
            width="130"
            show-overflow-tooltip
          />

          <!--交货地点-->
          <el-table-column
            prop="deliveryPlace"
            :label="$t('bid_mod.tradingLocations')"
            width="150"
            show-overflow-tooltip
          >
            <template v-slot="scope">
              <RenderAsyncText :cell-value="scope.row.deliveryPlace" />
            </template>
          </el-table-column>

          <!--贸易条款-->
          <el-table-column
            prop="tradeTerm"
            :label="$t('bid_mod.tradeTerm')"
            width="150"
            :formatter="(row, column, cellValue) => $getDictLabel('trade_clause', cellValue)"
            show-overflow-tooltip
          />

          <!--运输方式-->
          <el-table-column
            prop="transportType"
            :label="$t('bid_mod.transportType')"
            width="150"
          >
            <template v-slot="scope">
              <DictSelect v-model="scope.row.transportType" code="TRANSF_TYPE" />
            </template>
          </el-table-column>

          <!--供货周期(自然天)-->
          <el-table-column
            prop="leadTime"
            :label="$t('bid_mod.leadTime')"
            width="140"
          >
            <template v-slot="scope">
              <el-input v-model="scope.row.leadTime" v-input-format="{ type: 'float' }" />
            </template>
          </el-table-column>

          <!--质保期(月)-->
          <el-table-column
            prop="warrantyPeriod"
            :label="$t('bid_mod.warrantyPeriod')"
            width="120"
          >
            <template v-slot="scope">
              <el-input v-model="scope.row.warrantyPeriod" v-input-format="{ type: 'float' }" />
            </template>
          </el-table-column>

          <!--采购类型-->
          <el-table-column
            v-if="!proxyQuoteParams.visible"
            prop="purchaseType"
            :label="$t('bid_mod.purchaseType')"
            width="150"
            show-overflow-tooltip
            :formatter="(row, column, cellValue) => $getDictLabel('PURCHASE_TYPE', cellValue)"
          />

          <!--最小订单量-->
          <el-table-column
            prop="mqo"
            :label="$t('bidMod.minOrderQuantity')"
            width="120"
          >
            <template v-slot="scope">
              <el-input v-model="scope.row.mqo" v-input-format="{ type: 'float' }" />
            </template>
          </el-table-column>

          <!--组合-->
          <el-table-column
            prop="itemGroup"
            :label="$t('bidMod.itemGroup')"
            width="100"
            show-overflow-tooltip
          />

          <!--采购数量-->
          <el-table-column
            prop="requireQuantity"
            :label="$t('bid_mod.purQuantity')"
            width="100"
            show-overflow-tooltip
          />

          <!--单位-->
          <el-table-column
            prop="unit"
            :label="$t('bid_mod.unit')"
            width="100"
            show-overflow-tooltip
            :formatter="(row, column, cellValue) => $getDictLabel('unit', cellValue)"
          />

          <!--付款条款-->
          <el-table-column
            prop="paymentType"
            :label="$t('route.paymentType')"
            width="110"
            :render-header="_addStarToColumn"
          >
            <template v-slot="scope">
              <el-button type="text" @click="openPaymentTypeDialog(scope)">
                {{ $t("bidMod.input") }}
              </el-button>
            </template>
          </el-table-column>

          <!--承诺交货期-->
          <el-table-column
            prop="deliverDate"
            :label="$t('bid_mod.deliverDate')"
            width="160"
            show-overflow-tooltip
          >
            <template v-slot="scope">
              <el-date-picker
                v-model="scope.row.deliverDate"
                type="date"
                :format="$formatDatePicker"
                :picker-options="cannotLessCurrentTimeOptions"
              />
            </template>
          </el-table-column>

          <!--需求日期-->
          <el-table-column
            prop="requireDate"
            :label="$t('bidMod.ceeaDemandDate')"
            width="100"
            show-overflow-tooltip
            :formatter="(row, column, cellValue) => $parseTime(cellValue)"
          />

          <!--t 是否阶梯报价-->
          <el-table-column
            prop="isLadder"
            :label="$t('bidMod.isLadder')"
            width="100"
            :formatter="(row, column, cellValue) => $getDictLabel('YES_OR_NO', cellValue)"
          />

          <!--定价开始日期-->
          <el-table-column
            prop="priceStartTime"
            :label="$t('bidMod.fixedPriceBegin')"
            width="100"
            show-overflow-tooltip
            :formatter="(row, column, cellValue) => $parseTime(cellValue)"
          />

          <!--定价结束日期-->
          <el-table-column
            prop="priceEndTime"
            :label="$t('bidMod.fixedPriceEnd')"
            width="100"
            show-overflow-tooltip
            :formatter="(row, column, cellValue) => $parseTime(cellValue)"
          />

          <!--t 技术文件-->
          <el-table-column
            prop="graphFileId"
            :label="$t('bidMod.technicalDocuments.title')"
            width="120"
          >
            <template v-slot="{ row }">
              <el-button type="text" @click="openTechnicalDocumentsDialog(row)">
                {{ $t('common.view') }}
              </el-button>
            </template>
          </el-table-column>

          <!--备注-->
          <el-table-column
            prop="orderRemark"
            :label="$t('bid_mod.remark')"
            width="150"
            show-overflow-tooltip
          >
            <template v-slot="scope">
              <el-input v-model="scope.row.orderRemark" />
            </template>
          </el-table-column>

          <!--操作-->
          <el-table-column
            fixed="right"
            :label="$t('bidMod.operation')"
          >
            <template v-slot="scope">
              <!--公式报价-->
              <el-button
                v-if="isFormulaPricing"
                type="text"
                @click="openFormulaPriceDialog(scope)"
              >
                {{ $t("bidMod.formulaQuote") }}
              </el-button>

              <!--模型报价-->
              <el-button
                v-if="isModelPricing"
                type="text"
                @click="modelQuote(scope)"
              >
                {{ $t("bid_mod.modelQuoteTitle") }}
              </el-button>

              <!--阶梯价-->
              <el-button
                v-if="scope.row.isLadder === 'Y'"
                type="text"
                @click="ladderPriceClick(scope.$index, scope.row)"
              >
                {{ $t('bidMod.ladderPrice') }}
              </el-button>

              <!-- 料费分离报价 -->
              <el-button
                v-if="isSeparation"
                type="text"
                @click="openSeparationPriceDialog(scope.$index, scope.row)"
              >
                {{ $t('bidMod.doBiding1') }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div style="width: 100%; margin-top: 10px">
          <CPagination
            style="margin: 0"
            class="c-query-table-pagination"
            :total="itemList.length"
            :page-num="currentPage"
            layout="total, sizes, prev, pager, next"
            :page-size="pageSize"
            :page-sizes="[5, 10]"
            @current-change="handleCurrentChange"
            @size-change="handleSizeChange"
          />
        </div>
      </div>

      <!--技术信息-->
      <TechInfo :vendor-file-list.sync="orderFileList" :bidding-data="biddingData" />

      <!-- 批量维护承诺交货期-->
      <BatchMaintainDeliverDateDialog
        v-if="batchMaintainDeliverDateDialogVisible"
        :visible.sync="batchMaintainDeliverDateDialogVisible"
        @confirm="saveBatchMaintainDeliverDate"
      />

      <!-- 批量维护币种 -->
      <BatchMaintainCurrencyDialog
        v-if="batchMaintainCurrencyDialogVisible"
        :visible.sync="batchMaintainCurrencyDialogVisible"
        :currency-list="currencyList"
        @confirm="saveBatchMaintainCurrency"
      />

      <!-- 批量维护税率 -->
      <BatchMaintainTaxRateDialog
        v-if="batchMaintainTaxRateDialogVisible"
        :visible.sync="batchMaintainTaxRateDialogVisible"
        @confirm="saveBatchTaxRate"
      />

      <!-- 模型报价 -->
      <ModelQuoteDialog
        v-if="modelQuoteDialogVisible"
        :visible.sync="modelQuoteDialogVisible"
        :is-proxy-quote="proxyQuoteParams.visible"
        :source-line="editRow"
        @saveModelQuoteData="saveModelQuote"
      />

      <!--付款条款-->
      <PaymentTypeDialog
        v-if="paymentTypeDialogVisible"
        :visible.sync="paymentTypeDialogVisible"
        :business-type="BUSINESS_TYPE_ENUM.BIDDING_LTS"
        :edit-row="editRow"
        @savePaymentType="savePaymentType"
      />

      <!--技术文件-->
      <TechnicalDocumentsDialog
        v-if="technicalDocumentsDialogVisible"
        :visible.sync="technicalDocumentsDialogVisible"
        readonly
        :params="editRow"
      />

      <!--公式报价-->
      <FormulaPrice
        v-if="formulaPriceDialogVisible"
        :business-type="BUSINESS_TYPE_ENUM.BIDDING_LTS"
        :visible.sync="formulaPriceDialogVisible"
        :detail-info="editRow"
        :query-params="formulaPriceQueryParams"
        :proxy-quote-params="proxyQuoteParams"
        :price-precision="editRow.pricePrecision"
        @save="saveFormulaInFormation"
      />

      <!--d 阶梯价-->
      <LadderPrice
        v-if="ladderPriceVisible"
        :visible.sync="ladderPriceVisible"
        :business-type="BUSINESS_TYPE_ENUM.BIDDING_LTS"
        page-type="quote"
        :edit-row="editRow"
        :tax="editRow.taxKey"
        :price-precision="editRow.pricePrecision"
        @save-quote="saveLadderItems"
      />

      <!-- 料费分离报价 -->
      <SeparationPriceDialog
        v-if="separationPriceDialogVisible"
        :visible.sync="separationPriceDialogVisible"
        :businessType="BUSINESS_TYPE_ENUM.BIDDING_LTS"
        :edit-row="editRow"
        :role="proxyQuoteParams.visible ? 'buyer' : 'vendor'"
        @confirm="saveTemplatePriceItem"
      />
    </el-main>
  </el-container>
</template>

<script>
import { bidSupplierHttp } from 'mods@/biddingSupplier/api'
import { bigCalcTaxPrice } from 'lib@/composition/origin/composition'
import {
  SOU_ORDER_TYPE_ENUM,
  SOU_ORDER_WAY_ENUM,
  BUSINESS_TYPE_ENUM
} from 'lib@/composition/origin/enum'
import { tabTodoMixin } from '@/utils/mixins'
import { cannotLessCurrentTime } from 'lib@/mixins/datePickerOptions'
import { FLOAT_FORMAT_MAGIC } from '@/config/sysConfig'
import Big from 'big.js'
import ModelQuoteDialog from 'lib@/composition/biddingLts/modelQuote/modelQuoteDialog.vue'
import RenderAsyncText from 'lib@/components/provice-city/renderAsyncText'
import DynamicCutoffTime from 'lib@/components/dynamic-cutoff-time'
import CPagination from 'lib@/components/c-pagination'
import TechnicalDocumentsDialog from 'lib@/composition/origin/technicalDocumentsDialog'
import PaymentTypeDialog from 'lib@/composition/origin/paymentTypeDialog'
import TechInfo from './biddingOrdersQuote/techInfo.vue'
import BatchMaintainCurrencyDialog from './biddingOrdersQuote/batchMaintainCurrencyDialog.vue'
import BatchMaintainDeliverDateDialog from './biddingOrdersQuote/batchMaintainDeliverDateDialog.vue'
import BatchMaintainTaxRateDialog from './biddingOrdersQuote/batchMaintainTaxRateDialog.vue'
import FormulaPrice from 'lib@/composition/origin/formulaPrice'
import LadderPrice from 'lib@/composition/origin/ladderPrice'
import SeparationPriceDialog from 'lib@/composition/quoteSeparation/templatePriceDialog'

export default {
  name: 'BiddingOrdersQuote',

  components: {
    ModelQuoteDialog,
    RenderAsyncText,
    DynamicCutoffTime,
    TechInfo,
    BatchMaintainCurrencyDialog,
    BatchMaintainDeliverDateDialog,
    BatchMaintainTaxRateDialog,
    CPagination,
    PaymentTypeDialog,
    TechnicalDocumentsDialog,
    FormulaPrice,
    LadderPrice,
    SeparationPriceDialog
  },

  mixins: [tabTodoMixin, cannotLessCurrentTime],

  props: {
    // 代理报价参数
    proxyQuoteParams: {
      type: Object,
      default: () => {
        return {
          visible: false
        }
      }
    }
  },

  data () {
    return {
      currentPage: 1,
      pageSize: 10,
      batchMaintainCurrencyDialogVisible: false,
      batchMaintainDeliverDateDialogVisible: false,
      batchMaintainTaxRateDialogVisible: false,
      modelQuoteDialogVisible: false,
      itemList: [],
      biddingData: {
        biddingType: '',
        scoreRuleType: '',
        orderWay: ''
      },
      projectId: '',
      scopeParams: null,
      currencyList: [],
      paymentTypeDialogVisible: false,
      editRow: null,
      editIndex: '',
      formulaPriceDialogVisible: false,
      orderFileList: [],
      isBatchPaymentType: false,
      saveOrSubmitButtonLoading: false,
      // 代理报价提交数据
      proxyData: {
        proxyDocId: '',
        proxyFileName: ''
      },
      oldCurrencyType: '',
      technicalDocumentsDialogVisible: false,
      formulaPriceQueryParams: null,
      BUSINESS_TYPE_ENUM,
      ladderPriceVisible:false,
      separationPriceDialogVisible:false,
      quoteData:new Map(),
      quoteTotalPrice:new Map()
    }
  },

  computed: {
    // 是否公式报价
    isFormulaPricing () {
      return this.biddingData.orderType === SOU_ORDER_TYPE_ENUM.FORMULA
    },
    // 是否模型报价
    isModelPricing () {
      return this.biddingData.orderType === SOU_ORDER_TYPE_ENUM.MODEL
    },
    // 料费分离
    isSeparation(){
      return this.biddingData.orderType === SOU_ORDER_TYPE_ENUM.MATERIAL_COST_SEPARATION
    }
  },

  created () {
    this.scopeParams = this.proxyQuoteParams.visible ? this.proxyQuoteParams : this.$attrs.params.row
    this.projectId = this.scopeParams.projectId

    this.getOrderDetail()
  },

  methods: {
    /* 获取详情数据 */
    async getOrderDetail () {
      let response
      if (this.proxyQuoteParams.visible) {
        // 代理报价 查采购商接口
        response = await this.$http({
          url: '/api-sou/buyer/bid/order/getOrderInfo',
          method: 'GET',
          params: {
            projectId: this.projectId,
            vendorId: this.proxyQuoteParams.vendorId
          },
          loading: true
        })
      } else {
        response = await bidSupplierHttp.order.getOrderInfo(this.projectId)
      }

      if (response && response.data) {
        const {
          itemList = [],
          orderFileList = [],
          initInfo = {}
        } = response.data

        const { projectInfo = {} } = initInfo

        const { currencyList = [] } = projectInfo

        this.biddingData = projectInfo || {}
        // 物料行
        this.itemList = itemList.concat()
        // 可用外币
        this.currencyList = currencyList.concat()
        // 供应商投标附件
        this.orderFileList = orderFileList.concat()

        this.$nextTick(() => {
          this.$refs.itemListTable && this.$refs.itemListTable.doLayout()
        })
      }
    },

    /**料费分离 --- start */
    openSeparationPriceDialog($index,row){
      if (!row.orderCurrency) {
        // 必须先选币种才知道价格精度
        this.$message.warning(this.$t('bidMod.inpCurrency'))
        return
      }
      if (!row.taxKey) {
        // 请先选择税率！
        this.$message.warning(this.$t('bidMod.inpTaxKey'))
        return
      }
      this.editIndex = $index
      const {quoteTempId,quoteTempName,currentRound} = this.biddingData
      this.editRow = {
        ...row,
        quoteTempId,
        quoteTempName,
        currentRound,
        vendorId:row.vendorId ?? this.proxyQuoteParams.vendorId
      }
      this.separationPriceDialogVisible = true
    },

    // 保存
    saveTemplatePriceItem ({totalPrice,templateData,souItemId}) {
      this.quoteData.set(souItemId,templateData)
      this.quoteTotalPrice.set(souItemId,totalPrice)
      this.itemList[this.editIndex].orderNotaxPrice = totalPrice
      if(this.editRow.taxKey){
        this.itemList[this.editIndex].orderTaxPrice = bigCalcTaxPrice(totalPrice,this.editRow.taxKey,this.editRow.pricePrecision || 10)
      }
    },
    /**料费分离 --- end */

    /* 阶梯价 */
    ladderPriceClick (index, row) {
      if (!row.orderCurrency) {
        // 必须先选币种才知道价格精度
        this.$message.warning(this.$t('bidMod.inpCurrency'))
        return
      }
      if (!row.taxKey) {
        // 请先选择税率！
        this.$message.warning(this.$t('bidMod.inpTaxKey'))
        return
      }
      this.editRow = {
        ...row,
        ladderList: row.ladderPriceList
      }
      this.editIndex = index
      this.ladderPriceVisible = true
    },
    /* 保存阶梯价 */
    saveLadderItems (val) {
      this.itemList[this.editIndex].ladderPriceList = [].concat(val)
      this.itemList[this.editIndex].orderNotaxPrice = val[0].orderNotaxPrice
      this.itemList[this.editIndex].orderTaxPrice = val[0].orderTaxPrice
    },

    /* 编排可选的报价币种 */
    transformCurrencyOptions (options) {
      // 过滤存在币种列表的可用货币
      return options.filter(item => {
        return this.currencyList.find(itemC => itemC.currencyCode === item.value)
      })
    },

    /* 物料列表页码大小改变 */
    handleSizeChange (val) {
      this.currentPage = 1
      this.pageSize = val
    },

    /* 物料列表翻页 */
    handleCurrentChange (val) {
      this.currentPage = val
    },

    /* 下拉操作菜单点击 */
    handleCommand (handler) {
      this[handler]()
    },

    /* 币种改变 */
    orderCurrencyChange (val, scope) {
      if (!val) {
        return
      }
      this.calcTaxPrice(scope.row.orderNotaxPrice, scope)
    },

    /* 返回当前币种的的价格精度 */
    getPricePrecision (currency) {
      const currencyObj = this.currencyList.find(item => item.currencyCode === currency)
      return (currencyObj || {}).pricePrecision || FLOAT_FORMAT_MAGIC.DIGITS
    },

    /* 税率改变 */
    taxKeyChange (val, dictItem, scope) {
      if (!val) {
        return
      }
      scope.row.taxRate = dictItem.key
      // 计算含税单价
      this.calcTaxPrice(scope.row.orderNotaxPrice, scope)
    },

    /* 计算含税单价 */
    calcTaxPrice (orderNotaxPrice, { row, $index }) {
      // 存在值和税率
      if (orderNotaxPrice && row.taxRate) {
        this.itemList[$index].orderTaxPrice = bigCalcTaxPrice(
          orderNotaxPrice,
          row.taxRate,
          this.getPricePrecision(row.orderCurrency)
        )
      } else {
        this.itemList[$index].orderTaxPrice = ''
      }
    },

    /* 付款条款弹窗 START */
    // 打开
    openPaymentTypeDialog ({ $index, row }) {
      this.isBatchPaymentType = false

      if (row.souItemId && row.orderItemId) {
        this.editRow = { ...row }
      } else {
        this.editRow = {
          paymentList: row.paymentList && Array.isArray(row.paymentList) ? row.paymentList : []
        }
      }

      this.editIndex = $index
      this.paymentTypeDialogVisible = true
    },
    // 保存
    savePaymentType (val) {
      if (this.isBatchPaymentType) {
        // 批量
        this.itemList = this.itemList.map(item => {
          return {
            ...item,
            paymentList: val
          }
        })
      } else {
        // 单个
        this.itemList[this.editIndex].paymentList = val
      }
    },
    // 打开批量维护付款条款
    openBatchPaymentTypeDialog () {
      // 打开原来弹窗，传空数组，保存覆盖全部的物料行，通过isBatchPaymentType区分
      this.isBatchPaymentType = true
      this.editRow = { paymentList: [] }
      this.paymentTypeDialogVisible = true
    },
    /* END */

    /* 批量维护承诺交货期 START */
    // 打开
    openBatchMaintainDeliverDateDialog () {
      this.batchMaintainDeliverDateDialogVisible = true
    },
    // 保存
    saveBatchMaintainDeliverDate (val) {
      this.itemList = this.itemList.map(item => {
        return {
          ...item,
          deliverDate: val
        }
      })
    },
    /* END */

    /* 批量维护币种 start */
    // 打开弹窗
    openBatchMaintainCurrencyDialog () {
      this.batchMaintainCurrencyDialogVisible = true
      this.oldCurrencyType = this.itemList[0].orderCurrency
    },
    // 保存
    saveBatchMaintainCurrency (val) {
      if (this.oldCurrencyType !== val) {
        // 币种改变
        this.itemList = this.itemList.map((item, index) => {
          this.orderCurrencyChange(val, { row: item, $index: index }, true)
          return {
            ...item,
            orderCurrency: val
          }
        })
      }
    },
    /* END */

    /* 批量维护税率 START */
    // 打开弹窗
    openBatchAddTaxRateDialog () {
      this.batchMaintainTaxRateDialogVisible = true
    },
    // 保存
    saveBatchTaxRate (val, dictItem) {
      this.itemList = this.itemList.map((item, index) => {
        // 税率改变
        this.taxKeyChange(val, dictItem, { row: item, $index: index })
        return {
          ...item,
          taxKey: val
        }
      })
    },
    /* END */

    /* 公式报价 START */
    // 打开
    openFormulaPriceDialog ({ row, $index }) {
      if (!row.orderCurrency || !row.taxKey) {
        // 请先选择币种和税率
        this.$message.warning(this.$t('bidMod.biddingManagementSupplier.currencyAndTaxRequired'))
        return
      }

      this.editRow = {
        ...row,
        // 币种
        currency: row.orderCurrency,
        pricePrecision: this.getPricePrecision(row.orderCurrency)
      }

      this.formulaPriceQueryParams = {
        souItemId: row.souItemId || '',
        orderItemId: row.orderItemId || '',
        // 币种，用于基材价格根据汇率转换
        currencyCode: row.orderCurrency
      }
      this.editIndex = $index
      this.formulaPriceDialogVisible = true
    },
    // 保存
    saveFormulaInFormation (val) {
      const index = (this.currentPage - 1) * this.pageSize + this.editIndex
      this.itemList[index].formulaResult = val.formulaAttrValues
      this.itemList[index].orderNotaxPrice = val.orderNotaxPrice
      this.itemList[index].orderTaxPrice = val.orderTaxPrice
    },
    // 保存公式报价
    saveFormulaPrice (val) {
      // 计算序列号
      const index = (this.currentPage - 1) * this.pageSize + this.editIndex
      this.itemList[index].formulaResult = val
      const row = this.itemList[index]
      // 计算未税单价和含税单价
      let params = {
        souItemId: row.souItemId,
        orderCurrency: row.orderCurrency,
        taxKey: row.taxKey,
        formulaResult: val
      }
      if (this.proxyQuoteParams.visible) {
        // 代理报价
        params = {
          ...params,
          vendorId: this.proxyQuoteParams.vendorId
        }
      }
      this.$http({
        url: '/api-brg/supplierCooperate/orderHead/computeFormulaPrice',
        method: 'GET',
        params
      }).then(data => {
        if (data && data.data) {
          this.itemList[index].orderNotaxPrice = data.data.orderNotaxPrice
          this.itemList[index].orderTaxPrice = data.data.orderTaxPrice
        }
      })
    },
    /* END */

    /* 模型报价 START */
    // 打开
    modelQuote ({ row, $index }) {
      if (!row.orderCurrency || !row.taxKey) {
        this.$message.warning(this.$t('bidMod.biddingManagementSupplier.currencyAndTaxRequired'))
        return
      }
      this.editRow = {
        souItemId: row.souItemId,
        modelQuoteLines: (row.modelPriceList || []).map(item => {
          return {
            ...item,
            // 重置为统一的字段名
            modelPriceLineTemplateList: item.priceLineList || []
          }
        })
      }

      this.editIndex = $index
      this.modelQuoteDialogVisible = true
    },
    // 保存
    saveModelQuote (val) {
      // 计算未税单价 sum所有费用的含税合计
      let orderNotaxPrice = 0
      this.itemList[this.editIndex].modelPriceList = (val || []).map(item => {
        item.modelPriceLineTemplateList.forEach(lineItem => {
          if (lineItem.taxTotalPrice) {
            orderNotaxPrice = Big(lineItem.taxTotalPrice).plus(orderNotaxPrice)
          }
        })
        return {
          ...item,
          // 重置为提交的字段名
          priceLineList: item.modelPriceLineTemplateList || []
        }
      })
      if (orderNotaxPrice !== 0) {
        this.itemList[this.editIndex].orderNotaxPrice = orderNotaxPrice.toString()
        // 计算含税单价
        this.calcTaxPrice(orderNotaxPrice, {
          row: this.itemList[this.editIndex],
          $index: this.editIndex
        })
      }
    },
    /* END */

    /* 代理报价文件变更 */
    proxyFileChange ({ file }) {
      const { fileId = '', fileName = '' } = file || {}
      this.proxyData = {
        ...this.proxyData,
        proxyDocId: fileId,
        proxyFileName: fileName
      }
    },

    /* 校验提交数据 */
    validateForm () {
      return new Promise(resolve => {
        // 决标方式是组合决项前端不校验
        if (this.biddingData.orderWay === SOU_ORDER_WAY_ENUM.COMBINED) {
          resolve(true)
          return
        }

        // 校验物料报价
        for (const [index, item] of this.itemList.entries()) {
          // 公共部分 报价币种、税率、付款条款
          // 普通报价 未税单价orderNotaxPrice
          // 模型报价 模型报价 modelPriceList
          // 公式报价 公式报价 formulaResult
          // 公共必须部分校验
          const showIndex = index + 1
          if (!item.orderCurrency) {
            this.$message.warning(this.$t('bidMod.biddingManagementSupplier.currencyTypeRequired', [showIndex]))
            resolve(false)
            return
          }
          if (!item.taxKey) {
            this.$message.warning(this.$t('bidMod.biddingManagementSupplier.taxKey', [showIndex]))
            resolve(false)
            return
          }
          if (this.isModelPricing) {
            // 模型报价
            if (!item.modelPriceList || !Array.isArray(item.modelPriceList) || item.modelPriceList.length === 0) {
              this.$message.warning(this.$t('bidMod.biddingManagementSupplier.priceLineList', [showIndex]))
              resolve(false)
              return
            }
          }
          if (this.isFormulaPricing) {
            // 公式报价
            if (!item.formulaResult) {
              this.$message.warning(this.$t('bidMod.biddingManagementSupplier.formulaResult', [showIndex]))
              resolve(false)
              return
            }
          }
        }
        resolve(true)
      })
    },

    /* 打开物料技术文件 */
    openTechnicalDocumentsDialog (row) {
      this.editRow = {
        businessId: row.souItemId || ''
      }
      this.technicalDocumentsDialogVisible = true
    },

    /* 提交保存 */
    async saveOrSubmit (type) {
      // 业务过于复杂，前端不再校验
      // if (!(await this.validateForm())) return

      this.saveOrSubmitButtonLoading = true

      const orderItemList = (this.itemList || []).map(item => {
        const quoteData = this.quoteData.get(item.souItemId)
        const quoteTotalPrice = this.quoteTotalPrice.get(item.souItemId)
        return {
          ...item,
          // 供应商的未税报价
          orderNotaxPrice:this.isSeparation ? (quoteTotalPrice ?? item.orderNotaxPrice) :  item.orderNotaxPrice,
          quoteData
        }
      })

      let params = {
        projectId: this.biddingData.projectId,
        // 投标物料信息
        orderItemList,
        // 投标附件
        orderFileList: this.orderFileList,
        isTempSave: type === 'SAVE'
      }

      if (this.biddingData.orderId) {
        params = {
          ...params,
          orderId: this.biddingData.orderId,
          orderNo: this.biddingData.orderNo
        }
      }

      let role = 'vendor'
      if (this.proxyQuoteParams.visible) {
        // 代理报价 走另一个采购商的接口
        role = 'buyer'

        params = {
          ...params,
          vendorId: this.proxyQuoteParams.vendorId
        }
        if (this.proxyData.proxyDocId) {
          params = {
            ...params,
            proxyDocId: this.proxyData.proxyDocId,
            proxyFileName: this.proxyData.proxyFileName
          }
        }
      }
      try {
        const response = await this.$http({
          url: `/api-sou/${role}/bid/order/editOrder`,
          method: 'POST',
          data: params,
          loading: true
        })
        this.saveOrSubmitButtonLoading = false
        if (!response) {
          return
        }
      } catch (e) {
        this.saveOrSubmitButtonLoading = false
        return false
      }

      this.$message.success(this.$t('common.successSave'))

      if (!this.proxyQuoteParams.visible && type === 'SAVE') {
        await this.getOrderDetail()
      } else {
        this.backTo('refresh')
      }
    },

    /* 返回页签 */
    backTo (type) {
      if (this.proxyQuoteParams.visible) {
        // 代理报价
        this.$emit('proxy-quote-success')
      } else {
        this.$emit('tab-remove', this.$attrs.params.tabName)
        if (type === 'refresh') {
          this.__setTabTodo('BiddingOrdersList.getQueryData')
        }
      }
    }
  }
}
</script>

<style scoped lang="scss">
.the_doBidingDetail_wrapper ::v-deep {
  .cur-quote-deadline {
    padding-left: 16px;
  }
  .the_footer_row {
    float: right;
    font-size: 18px;
  }
  .the_display_content {
    padding: 11px;
    .el-row {
      margin-bottom: 11px;
      font-size: 16px;
      span {
        padding-right: 11px;
        display: inline-block;
        color: #999;
      }
    }
    .the_display_footer {
      text-align: center !important;
    }
    .flex-col {
      display: flex;
      .label {
        line-height: 30px;
      }
    }
  }
  .the_progress {
    padding: 11px;
    margin: 10px;
    background: #eee;
    .el-steps {
      padding-bottom: 0;
    }
    .the_footer_row {
      float: right;
      font-size: 18px;
    }
  }
  .table1 {
    padding: 5px 10px;
  }
}
</style>
