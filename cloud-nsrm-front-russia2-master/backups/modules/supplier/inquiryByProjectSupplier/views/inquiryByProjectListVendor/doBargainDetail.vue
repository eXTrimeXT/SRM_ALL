<template>
  <el-container
    class="flex-container the_doBidingDetail_wrapper"
    direction="vertical"
  >
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
        <el-button
          @click="getOrderDetail"
        >
          {{ $t("common.refresh") }}
        </el-button>

        <!--返回-->
        <el-button
          v-if="!proxyQuoteParams.visible"
          @click="backTo"
        >
          {{ $t("common.backTo") }}
        </el-button>
      </p>

      <!--进度-->
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
          :deadline-time="bargainData.bargainEndDatetime"
        />
      </div>

      <!--单据信息-->
      <div class="the_display_content">
        <SrmRow type="flex">
          <!--当前轮次-->
          <SrmCol>
            <span>{{ $t("bidMod.currentRound") }}</span>{{ bargainData.currentRound }}
          </SrmCol>
          <!--评分规则-->
          <SrmCol>
            <span>{{ $t("bidMod.inquiryRule") }}</span>
            {{ $getDictLabel('BRG_EVALUATE_METHOD', bargainData.evaluateMethod) }}
          </SrmCol>
          <!--决标方式-->
          <SrmCol>
            <span>{{ $t("bidMod.bidingAwardWay") }}</span>
            {{ $getDictLabel('BRG_AWARD_WAY', bargainData.bargainAwardWay) }}
          </SrmCol>
          <!--询价类型-->
          <SrmCol>
            <span>{{ $t("bidMod.bidingType1") }}</span>
            {{ $getDictLabel('BARGAIN_TYPE', bargainData.bargainType) }}
          </SrmCol>
        </SrmRow>

        <!--代理报价上传授权证明，非必填-->
        <SrmRow
          v-if="proxyQuoteParams.visible"
          type="flex"
        >
          <!--代理报价授权证明-->
          <SrmCol class="flex-col">
            <span class="label">代理报价授权证明</span>

            <SrmCommonFile
              :default-file="{
                fileId: proxyData.proxyDocId,
                fileName: proxyData.proxyFileName
              }"
              :extra-data="fileInfo"
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
          <el-button
            type="primary"
          >
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
          ref="orderLineListTable"
          :data="orderLineList.slice((currentPage - 1) * pageSize, currentPage * pageSize)"
          style="width: 100%"
          border
          height="200px"
        >
          <el-table-column
            align="center"
            type="index"
            width="50"
          />

          <!--业务实体-->
          <el-table-column
            align="center"
            prop="orgOuName"
            :label="$t('bid_mod.businessEntity')"
            width="150"
            show-overflow-tooltip
          />

          <!--库存组织-->
          <el-table-column
            align="center"
            prop="orgInvName"
            :label="$t('bid_mod.inv')"
            width="150"
            show-overflow-tooltip
          />

          <!--物料编码-->
          <el-table-column
            align="center"
            prop="targetNum"
            :label="$t('bidMod.targetNum')"
            width="150"
            show-overflow-tooltip
            :formatter="(row, column, cellValue) => row.isNoCodeItem === 'Y' ? '' : cellValue"
          />

          <!--物料名称-->
          <el-table-column
            align="center"
            prop="targetDesc"
            :label="$t('bidMod.targetDesc')"
            min-width="150"
            show-overflow-tooltip
          />

          <!--未税单价-->
          <el-table-column
            align="center"
            prop="notaxPrice"
            :label="$t('bid_mod.untaxedPrice')"
            width="130"
          >
            <template slot-scope="scope">
              <!--模型报价和公式报价无法输入-->
              <el-input
                v-if="!isTemplatePricing && !isFormulaPricing"
                v-model="scope.row.notaxPrice"
                @change="value => calcTaxPrice(value, scope)"
              />
              <span v-else>{{ scope.row.notaxPrice }}</span>
            </template>
          </el-table-column>

          <!--含税单价-->
          <el-table-column
            align="center"
            prop="taxPrice"
            :label="$t('bidMod.priceIncludeTax')"
            width="130"
            show-overflow-tooltip
          />

          <!--报价币种-->
          <el-table-column
            align="center"
            prop="currencyType"
            :label="$t('bidMod.bidingCurrency2')"
            width="150"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              <DictSelect
                v-model="scope.row.currencyType"
                code="currency"
                :clearable="false"
                :transform-options="transformCurrencyOptions"
                @change="val => currencyTypeChange(val, scope)"
              />
            </template>
          </el-table-column>

          <!--税率-->
          <el-table-column
            align="center"
            prop="taxKey"
            :label="$t('bid_mod.taxRate')"
            width="140"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              <DictSelect
                v-model="scope.row.taxKey"
                code="tax"
                :clearable="false"
                @change-value="(value, dictItem) => taxKeyChange(value, dictItem, scope)"
              />
            </template>
          </el-table-column>

          <!--交货地点-->
          <el-table-column
            align="center"
            prop="deliveryPlace"
            :label="$t('bid_mod.tradingLocations')"
            width="150"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              <RenderAsyncText :cell-value="scope.row.deliveryPlace" />
            </template>
          </el-table-column>

          <!--贸易条款-->
          <el-table-column
            align="center"
            prop="tradeTerm"
            :label="$t('bid_mod.tradeTerm')"
            width="150"
            :formatter="(row, column, cellValue) => $getDictLabel('trade_clause', cellValue)"
            show-overflow-tooltip
          />

          <!--运输方式-->
          <el-table-column
            align="center"
            prop="transportType"
            :label="$t('bid_mod.transportType')"
            width="150"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              <DictSelect
                v-model="scope.row.transportType"
                code="TRANSF_TYPE"
              />
            </template>
          </el-table-column>

          <!--供货周期(自然天)-->
          <el-table-column
            align="center"
            prop="leadTime"
            :label="$t('bid_mod.leadTime')"
            width="140"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              <el-input v-model="scope.row.leadTime" />
            </template>
          </el-table-column>

          <!--质保期(月)-->
          <el-table-column
            align="center"
            prop="warrantyPeriod"
            :label="$t('bid_mod.warrantyPeriod')"
            width="120"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              <el-input v-model="scope.row.warrantyPeriod" />
            </template>
          </el-table-column>

          <!--采购类型-->
          <el-table-column
            v-if="!proxyQuoteParams.visible"
            align="center"
            prop="purchaseType"
            :label="$t('bid_mod.purchaseType')"
            :formatter="(row, column, cellValue) => $getDictLabel('PURCHASE_TYPE', cellValue)"
            width="150"
            show-overflow-tooltip
          />

          <!--最小订单量-->
          <el-table-column
            align="center"
            prop="mqo"
            :label="$t('bidMod.minOrderQuantity')"
            width="120"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              <el-input v-model="scope.row.mqo" />
            </template>
          </el-table-column>

          <!--组合-->
          <el-table-column
            align="center"
            prop="itemGroup"
            :label="$t('bidMod.itemGroup')"
            width="100"
            show-overflow-tooltip
          />

          <!--采购数量-->
          <el-table-column
            align="center"
            prop="quantity"
            :label="$t('bid_mod.purQuantity')"
            width="100"
            show-overflow-tooltip
          />

          <!--单位-->
          <el-table-column
            align="center"
            prop="unit"
            :label="$t('bid_mod.unit')"
            width="100"
            show-overflow-tooltip
            :formatter="(row, column, cellValue) => $getDictLabel('unit', cellValue)"
          />

          <!--付款条款-->
          <el-table-column
            align="center"
            prop="paymentType"
            :label="$t('route.paymentType')"
            width="110"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              <el-button type="text" @click="openPaymentTypeDialog(scope)">
                {{ $t("bidMod.input") }}
              </el-button>
            </template>
          </el-table-column>

          <!--承诺交货期-->
          <el-table-column
            align="center"
            prop="deliverDate"
            :label="$t('bid_mod.deliverDate')"
            width="160"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              <el-date-picker
                v-model="scope.row.deliverDate"
                type="date"
                :picker-options="cannotLessCurrentTimeOptions"
              />
            </template>
          </el-table-column>

          <!--需求日期-->
          <el-table-column
            align="center"
            prop="demandDate"
            :label="$t('bidMod.ceeaDemandDate')"
            width="100"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              <span>{{ scope.row.demandDate ? scope.row.demandDate.slice(0, 10) : "" }}</span>
            </template>
          </el-table-column>

          <!--定价开始日期-->
          <el-table-column
            align="center"
            prop="priceStartTime"
            :label="$t('bidMod.fixedPriceBegin')"
            width="100"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              <span>{{ scope.row.priceStartTime ? scope.row.priceStartTime.slice(0, 10) : "" }}</span>
            </template>
          </el-table-column>

          <!--定价结束日期-->
          <el-table-column
            align="center"
            prop="priceEndTime"
            :label="$t('bidMod.fixedPriceEnd')"
            width="100"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              <span>{{ scope.row.priceEndTime ? scope.row.priceEndTime.slice(0, 10) : "" }}</span>
            </template>
          </el-table-column>

          <!--t 技术文件-->
          <el-table-column
            align="center"
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
            align="center"
            prop="comments"
            :label="$t('bid_mod.remark')"
            width="150"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              <el-input v-model="scope.row.comments" />
            </template>
          </el-table-column>

          <!--操作-->
          <el-table-column
            v-if="isFormulaPricing || isTemplatePricing"
            fixed="right"
            :label="$t('bidMod.operation')"
          >
            <template slot-scope="scope">
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
                v-if="isTemplatePricing"
                type="text"
                @click="modelQuote(scope)"
              >
                {{ $t("bid_mod.modelQuoteTitle") }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div style="width: 100%; margin-top: 10px">
          <CPagination
            style="margin: 0"
            class="c-query-table-pagination"
            :total="orderLineList.length"
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
      <TechInfo
        :vendor-file-list.sync="vendorFileList"
        :bargain-data="bargainData"
      />

      <!-- 批量维护承诺交货期-->
      <BatchMaintainDeliverDateDialog
        v-if="batchMaintainDeliverDateDialogVisible"
        :visible.sync="batchMaintainDeliverDateDialogVisible"
        @saveBatchDeliverDate="saveBatchMaintainDeliverDate"
      />

      <!-- 批量维护币种 -->
      <BatchMaintainCurrencyDialog
        v-if="batchMaintainCurrencyDialogVisible"
        :visible.sync="batchMaintainCurrencyDialogVisible"
        :currency-list="currencyList"
        @saveBatchCurrency="saveBatchMaintainCurrency"
      />

      <!-- 批量维护税率 -->
      <BatchMaintainTaxRateDialog
        v-if="batchMaintainTaxRateDialogVisible"
        :visible.sync="batchMaintainTaxRateDialogVisible"
        @saveBatchTaxRate="saveBatchTaxRate"
      />

      <!--公式报价-->
      <FormulaPriceDialog
        v-if="formulaPriceDialogVisible"
        :visible.sync="formulaPriceDialogVisible"
        :edit-row="editRow"
        :base-info="bargainData"
        :proxy-quote-params="proxyQuoteParams"
        @saveFormulaPrice="saveFormulaPrice"
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
        business-type="BARGAIN"
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
    </el-main>
  </el-container>
</template>

<script>
import { bigCalcTaxPrice } from 'lib@/composition/origin/composition'
import { tabTodoMixin } from '@/utils/mixins'
import { cannotLessCurrentTime } from '@/library/mixins/datePickerOptions'
import ModelQuoteDialog from 'lib@/composition/bargain/modelQuote/modelQuoteDialog'
import RenderAsyncText from 'lib@/components/provice-city/renderAsyncText'
import DynamicCutoffTime from 'lib@/components/dynamic-cutoff-time'
import TechInfo from './doBrgainDetail/techInfo'
import BatchMaintainCurrencyDialog from './doBrgainDetail/batchMaintainCurrencyDialog'
import BatchMaintainDeliverDateDialog from './doBrgainDetail/batchMaintainDeliverDateDialog'
import BatchMaintainTaxRateDialog from './doBrgainDetail/batchMaintainTaxRateDialog'
import CPagination from 'lib@/components/c-pagination'
import FormulaPriceDialog from './formulaPriceDialog'
import Big from 'big.js'
import TechnicalDocumentsDialog from 'lib@/composition/origin/technicalDocumentsDialog'
import PaymentTypeDialog from 'lib@/composition/origin/paymentTypeDialog'

export default {
  name: 'DoBrgainDetail',

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
    FormulaPriceDialog,
    TechnicalDocumentsDialog
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
      currentPage: 1, // 当前页码
      total: 20, // 总条数
      pageSize: 10, // 每页的数据条数
      batchMaintainCurrencyDialogVisible: false,
      batchMaintainDeliverDateDialogVisible: false,
      batchMaintainTaxRateDialogVisible: false,
      modelQuoteDialogVisible: false,
      expLoading: false,
      orderLineList: [],
      bargainData: {
        round: '',
        bargainCurrency: '',
        taxInclusivePrice: '',
        taxRate: '',
        decimalAccuracy: '',
        bargainType: '',
        evaluateMethod: '',
        bargainAwardWay: ''
      },
      scopeBargainId: '',
      scopeParams: null,
      currencyList: [],
      paymentTypeDialogVisible: false,
      editRow: null,
      editIndex: '',
      formulaPriceDialogVisible: false,
      vendorFileList: [],
      isBatchPaymentType: false,
      saveOrSubmitButtonLoading: false,
      fileInfo: {
        fileModular: 'brg',
        fileFunction: 'bargainProject',
        fileType: 'file'
      },
      // 代理报价提交数据
      proxyData: {
        proxyDocId: '',
        proxyFileName: ''
      },
      oldCurrencyType: '',
      technicalDocumentsDialogVisible: false
    }
  },

  computed: {
    // 是否公式报价
    isFormulaPricing () {
      return this.bargainData.pricingType === 'FORMULA_PURCHASER'
    },
    // 是否模型报价
    isTemplatePricing () {
      return this.bargainData.pricingType === 'MODEL_PURCHASER'
    }
  },

  created () {
    this.scopeParams = this.proxyQuoteParams.visible ? this.proxyQuoteParams : this.$attrs.params.row
    this.scopeBargainId = this.scopeParams.bargainId

    this.getOrderDetail()
  },

  methods: {
    /* 获取详情数据 */
    async getOrderDetail () {
      let params = {
        bargainId: this.scopeBargainId
      }
      if (this.proxyQuoteParams.visible) {
        // 代理报价
        params = {
          ...params,
          vendorId: this.proxyQuoteParams.vendorId
        }
      }
      const data = await this.$http({
        url: '/api-brg/supplierCooperate/orderHead/getOrderDetail',
        method: 'GET',
        params,
        loading: true
      })
      if (data && data.data) {
        const orderData = data.data

        this.bargainData = orderData.bargain || {}

        // 物料行
        this.orderLineList = orderData.reqLineList

        // 可用外币
        this.currencyList = orderData.currencyList

        // 供应商投标附件
        this.vendorFileList = orderData.vendorFileList || []

        this.$nextTick(() => {
          this.$refs.orderLineListTable && this.$refs.orderLineListTable.doLayout()
        })
      }
    },

    /* 编排文件类型 */
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
    currencyTypeChange (val, scope) {
      if (!val) return
      this.calcTaxPrice(scope.row.notaxPrice, scope)
    },

    /* 税率改变 */
    taxKeyChange (val, dictItem, scope) {
      if (!val) return
      scope.row.taxRate = dictItem.key
      // 计算含税单价
      this.calcTaxPrice(scope.row.notaxPrice, scope)
    },

    /* 计算含税单价 */
    calcTaxPrice (notaxPrice, { row, $index }) {
      // 存在值和税率
      if (notaxPrice && row.taxRate) {
        // 拿到当前币种的价格精度
        let pricePrecision = 10
        const currencyObj = this.currencyList.find(item => item.currencyCode === row.currencyType)
        pricePrecision = (currencyObj || {}).pricePrecision || 10
        this.orderLineList[$index].taxPrice = bigCalcTaxPrice(notaxPrice, row.taxRate, pricePrecision)
      } else {
        this.orderLineList[$index].taxPrice = ''
      }
    },

    /* 付款条款弹窗 START */
    // 打开
    openPaymentTypeDialog ({ $index, row }) {
      this.isBatchPaymentType = false
      this.editRow = row
      this.editIndex = $index
      this.paymentTypeDialogVisible = true
    },
    // 保存
    savePaymentType (val) {
      if (this.isBatchPaymentType) {
        // 批量
        this.orderLineList = this.orderLineList.map(item => {
          return {
            ...item,
            paymentList: val
          }
        })
      } else {
        // 单个
        this.orderLineList[this.editIndex].paymentList = val
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
      this.orderLineList = this.orderLineList.map(item => {
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
      this.oldCurrencyType = this.orderLineList[0].currencyType
    },
    // 保存
    saveBatchMaintainCurrency (val) {
      if (this.oldCurrencyType !== val) {
        // 币种改变
        this.orderLineList = this.orderLineList.map((item, index) => {
          this.currencyTypeChange(val, { row: item, $index: index }, true)
          return {
            ...item,
            currencyType: val
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
      this.orderLineList = this.orderLineList.map((item, index) => {
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
      if (!row.currencyType || !row.taxKey) {
        this.$message.warning(this.$t('bidMod.biddingManagementSupplier.currencyAndTaxRequired'))
        return
      }
      this.editRow = row
      this.editIndex = $index
      this.formulaPriceDialogVisible = true
    },
    // 保存公式报价
    saveFormulaPrice (val) {
      // 计算序列号
      const index = (this.currentPage - 1) * this.pageSize + this.editIndex
      this.orderLineList[index].formulaResult = val
      const row = this.orderLineList[index]
      // 计算未税单价和含税单价
      let params = {
        requirementLineId: row.requirementLineId,
        currencyType: row.currencyType,
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
          this.orderLineList[index].notaxPrice = data.data.notaxPrice
          this.orderLineList[index].taxPrice = data.data.taxPrice
        }
      })
    },
    /* END */

    /* 模型报价 START */
    // 打开
    modelQuote ({ row, $index }) {
      if (!row.currencyType || !row.taxKey) {
        this.$message.warning(this.$t('bidMod.biddingManagementSupplier.currencyAndTaxRequired'))
        return
      }
      this.editRow = {
        requirementLineId: row.requirementLineId,
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
      let notaxPrice = 0
      this.orderLineList[this.editIndex].modelPriceList = (val || []).map(item => {
        item.modelPriceLineTemplateList.forEach(lineItem => {
          if (lineItem.taxTotalPrice) {
            notaxPrice = Big(lineItem.taxTotalPrice).plus(notaxPrice)
          }
        })
        return {
          ...item,
          // 重置为提交的字段名
          priceLineList: item.modelPriceLineTemplateList || []
        }
      })
      if (notaxPrice !== 0) {
        this.orderLineList[this.editIndex].notaxPrice = notaxPrice.toString()
        // 计算含税单价
        this.calcTaxPrice(notaxPrice, {
          row: this.orderLineList[this.editIndex],
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
        if (this.bargainData.bargainAwardWay === 'COMBINED_DECISION') {
          resolve(true)
          return
        }
        // 校验物料报价
        for (const [index, item] of new Map(this.orderLineList.map((itemM, indexM) => [indexM, itemM]))) {
          // 公共部分 报价币种、税率、付款条款
          // 普通报价 未税单价notaxPrice
          // 模型报价 模型报价 modelPriceList
          // 公式报价 公式报价 formulaResult
          // 公共必须部分校验
          const showIndex = index + 1
          if (!item.currencyType) {
            this.$message.warning(this.$t('bidMod.biddingManagementSupplier.currencyTypeRequired', [showIndex]))
            resolve(false)
            return
          }
          if (!item.taxKey) {
            this.$message.warning(this.$t('bidMod.biddingManagementSupplier.taxKey', [showIndex]))
            resolve(false)
            return
          }
          if (this.isTemplatePricing) {
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
        businessId: row.requirementLineId || ''
      }
      this.technicalDocumentsDialogVisible = true
    },

    /* 提交保存 */
    async saveOrSubmit (type) {
      // 业务过于复杂，前端不再校验
      // if (!(await this.validateForm())) return

      this.saveOrSubmitButtonLoading = true

      let params = {
        bargainId: this.scopeBargainId,
        // 投标物料信息
        orderLineList: this.orderLineList,
        // 投标附件
        vendorFileList: this.vendorFileList,
        isTempSave: type === 'SAVE'
      }

      if (this.proxyQuoteParams.visible) {
        // 代理报价
        params = {
          ...params,
          proxyVendorId: this.proxyQuoteParams.vendorId
        }
        if (this.proxyData.proxyDocId) {
          params = {
            ...params,
            proxyDocId: this.proxyData.proxyDocId,
            proxyFileName: this.proxyData.proxyFileName
          }
        }
      }

      this.$http({
        url: '/api-brg/supplierCooperate/orderHead/tempSaveOrSubmitOrder',
        method: 'POST',
        data: params,
        loading: true
      }).then(() => {
        this.saveOrSubmitButtonLoading = false
        this.$message.success(this.$t('common.successSave'))

        if (this.proxyQuoteParams.visible) {
          // 代理报价成功回调
          this.$emit('proxyQuoteSuccess')
        } else {
          if (type === 'SAVE') {
            // 暂存
            this.getOrderDetail()
          } else {
            this.backTo('refresh')
          }
        }
      }).catch(() => {
        this.saveOrSubmitButtonLoading = false
      })
    },

    /* 返回页签 */
    backTo (type) {
      this.$emit('tab-remove', `doBargainDetail${this.scopeParams.bargainNum}`)
      if (type === 'refresh') {
        this.__setTabTodo('BargainVendorList.getQueryData')
      }
    }
  }
}
</script>

<style scoped lang="scss">
:deep(.the_doBidingDetail_wrapper) {
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
