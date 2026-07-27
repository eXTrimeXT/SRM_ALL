<template>
  <div class="quote-selection">
    <div
      class="quote-selection-table-operation"
    >
      <!--b 发起新一轮-->
      <el-button
        v-if="showQuoteEvaluation"
        type="primary"
        @click="continueNextRound"
      >
        {{ $t('bidMod.biddingControl.startNewRound') }}
      </el-button>
      <!--b 总价比价-->
      <el-button
        v-if="showQuoteEvaluation"
        type="primary"
        @click="compareTotalAmount"
      >
        {{ $t('cusEntry.bidMod.totalPriceComparison') }}
      </el-button>
      <!-- 取消 -->
      <el-button
        v-if="showQuoteEvaluation || header.extProjectStatus === 'PRICE_END'"
        type="primary"
        @click="cancelRow"
      >
        {{ $t('common.cancel') }}
      </el-button>
      <!--b 询价结束-->
      <el-button
        v-if="showQuoteEvaluation"
        type="primary"
        @click="stopInquiry"
      >
        {{ $t('cusEntry.bidMod.inquiryEnd') }}
      </el-button>
      <!--b 导出-->
      <el-button
        v-if="['PRICE_END', 'EVALUATING'].includes(header.extProjectStatus)"
        type="primary"
        @click="exportDetail"
      >
        {{ $t('cusEntry.bidMod.export') }}
      </el-button>
      <el-button
        v-if="header.extProjectStatus === 'PRICE_END'"
        type="primary"
        @click="closeRow"
      >
        {{ $t('common.close') }}
      </el-button>
    </div>

    <el-table
      ref="quoteSelectionTable"
      :data="quoteSelectionTable"
      border
      max-height="350"
      highlight-current-row
      @selection-change="handleSelectionChange"
    >
      <el-table-column
        align="center"
        type="selection"
        width="55"
        fixed="left"
      />
      <el-table-column
        align="center"
        type="index"
        :label="$t('cusEntry.inq.index')"
        width="50"
        fixed="left"
      />
      <!--t 轮次-->
      <el-table-column
        sortable
        align="center"
        prop="round"
        :label="$t('bidMod.bidingRound')"
        fixed="left"
        width="70"
      />
      <!--t 业务实体-->
      <el-table-column
        align="center"
        prop="orgOuName"
        :label="$t('bidMod.affairsEntity')"
        min-width="100"
        show-overflow-tooltip
      />
      <!--t 区域-->
      <el-table-column
        align="center"
        prop="extAreaCode"
        :label="$t('cusEntry.inq.extAreaName')"
        min-width="120"
        :formatter="(row) => {
          return row.extAreaCode ? $getDictLabel('REGION', row.extAreaCode) : ''
        }"
      />

      <!--t 报价数次-->
      <el-table-column
        align="center"
        prop="orderCount"
        :label="$t('cusEntry.bidMod.quoteNum')"
        min-width="120"
      >
        <template slot-scope="scope">
          <el-button
            v-if="showQuoteEvaluation || ['PRICE_END', 'ACCEPT_ORDER'].includes(header.extProjectStatus)"
            type="text"
            @click="showSupplierChangeDialog(scope.row)"
          >
            {{ scope.row.orderCount }}
          </el-button>
          <span v-else>{{ scope.row.orderCount }}</span>
        </template>
      </el-table-column>
      <!--t 物料编码-->
      <el-table-column
        align="center"
        prop="itemCode"
        :label="$t('bidMod.itemCode')"
        min-width="120"
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
        min-width="100"
        show-overflow-tooltip
      />
      <!--t 规格型号-->
      <el-table-column
        align="center"
        prop="extMaterialModel"
        :label="$t('cusEntry.bidMod.specification')"
        min-width="120"
        show-overflow-tooltip
      />
      <!--t 数量-->
      <el-table-column
        align="center"
        prop="requireQuantity"
        :label="$t('cusEntry.inq.quantity')"
        min-width="100"
        show-overflow-tooltip
      />

      <!--t 单位-->
      <el-table-column
        align="center"
        prop="unit"
        :label="$t('cusEntry.inq.baseMeasurmentUnit')"
        min-width="120"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('unit', cellValue)"
      />
      <!--t 品牌-->
      <el-table-column
        prop="extBrand"
        align="center"
        :label="$t('cusEntry.inq.brand')"
        min-width="100"
        show-overflow-tooltip
      />
      <!--t 供应商编码-->
      <el-table-column
        align="center"
        prop="winVendorCode"
        :label="$t('bidMod.vendorCode')"
        min-width="120"
        show-overflow-tooltip
      />

      <!--t 中标供应商 -->
      <el-table-column
        align="center"
        prop="winVendorName"
        :label="$t('cusEntry.bidMod.winVendor')"
        min-width="120"
        show-overflow-tooltip
      />

      <!--t 税率-->
      <el-table-column
        align="center"
        prop="winTaxKey"
        :label="$t('bidMod.taxRate2')"
        min-width="120"
        show-overflow-tooltip
        :formatter="row => {
          return row.winTaxKey ? $getDictLabel('tax', row.winTaxKey) : ''
        }"
      />
      <!--t 发票类型-->
      <el-table-column
        align="center"
        prop="winInvoiceType"
        :label="$t('cusEntry.bidMod.invoiceType')"
        min-width="120"
        show-overflow-tooltip
        :formatter="row => {
          return row.winInvoiceType ? $getDictLabel('EXT_SOU_INQ_ORDER_INVOICE_TYPE', row.winInvoiceType) : ''
        }"
      />
      <!--t 未税单价-->
      <el-table-column
        align="center"
        prop="winStandardNotaxPrice"
        :label="$t('bidMod.quotenotaxPrice2')"
        min-width="100"
        show-overflow-tooltip
      />
      <!--t 未税总价-->
      <el-table-column
        align="center"
        prop="winStandardTotalPrice"
        :label="$t('cusEntry.bidMod.orderNotaxAmount')"
        min-width="100"
        show-overflow-tooltip
      />
      <!--t 到货周期(自然日) -->
      <el-table-column
        align="center"
        prop="winExtLeadTime"
        :label="$t('cusEntry.bidMod.deliveryCycle')"
        min-width="100"
        show-overflow-tooltip
      />
      <!--t 质保期(自然日) -->
      <el-table-column
        align="center"
        prop="winExtWarrantyPeriod"
        :label="$t('cusEntry.bidMod.warrantyPeriod')"
        min-width="100"
        show-overflow-tooltip
      />
      <!--t 备注 -->
      <el-table-column
        align="center"
        prop="remark"
        :label="$t('cusEntry.inq.remark')"
        min-width="100"
        show-overflow-tooltip
      />
      <!--t 是否生成定价单-->
      <el-table-column
        align="center"
        prop="hasFixPrice"
        :label="$t('cusEntry.bidMod.priceOrder')"
        min-widths="120"
        show-overflow-tooltip
        :formatter="row => {
          return row.hasFixPrice ? $getDictLabel('YES_OR_NO', row.hasFixPrice) : ''
        }"
      />
      <!--供应商列表-->
      <el-table-column
        v-for="item in vendorList"
        :key="item.vendorId"
        align="center"
      >
        <template #header>
          <span>{{ item.vendorName }}</span>
          <span
            v-if="item.extTotalAmount || item.extTotalAmount === 0"
            style="margin-left:10px;"
          >
            ({{ $t('cusEntry.inq.totalPrice') }}{{ item.extTotalAmount }})
          </span>
        </template>
        <el-table-column
          :label="$t('bidMod.taxRate2')"
          prop="taxRate"
          width="100"
          align="center"
          show-overflow-tooltip
        >
          <template slot-scope="scope">
            {{ showValueByProps(scope.row.orderItemList, 'taxRate', item.vendorId) }}
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          prop="invoiceType"
          :label="$t('cusEntry.bidMod.invoiceType')"
          min-width="120"
          show-overflow-tooltip
        >
          <template slot-scope="scope">
            {{ showValueByProps(scope.row.orderItemList, 'invoiceType', item.vendorId) ? $getDictLabel('EXT_SOU_INQ_ORDER_INVOICE_TYPE', showValueByProps(scope.row.orderItemList, 'invoiceType', item.vendorId)) : '' }}
          </template>
        </el-table-column>
        <el-table-column
          :label="$t('bidMod.quotenotaxPrice2')"
          prop="standardNotaxPrice"
          width="100"
          align="center"
          show-overflow-tooltip
        >
          <template slot-scope="scope">
            {{ showValueByProps(scope.row.orderItemList, 'standardNotaxPrice', item.vendorId) }}
          </template>
        </el-table-column>
        <el-table-column
          :label="$t('cusEntry.bidMod.orderNotaxAmount')"
          prop="extStandardNotaxTotalPrice"
          width="100"
          align="center"
          show-overflow-tooltip
        >
          <template slot-scope="scope">
            {{ showValueByProps(scope.row.orderItemList, 'extStandardNotaxTotalPrice', item.vendorId) }}
          </template>
        </el-table-column>
        <!--t 到货周期(自然日) -->
        <el-table-column
          align="center"
          prop="extLeadTime"
          :label="$t('cusEntry.bidMod.deliveryCycle')"
          min-width="120"
          show-overflow-tooltip
        >
          <template slot-scope="scope">
            {{ showValueByProps(scope.row.orderItemList, 'extLeadTime', item.vendorId) }}
          </template>
        </el-table-column>
        <!--t 质保期(自然日) -->
        <el-table-column
          align="center"
          prop="extWarrantyPeriod"
          :label="$t('cusEntry.bidMod.warrantyPeriod')"
          min-width="120"
          show-overflow-tooltip
        >
          <template slot-scope="scope">
            {{ showValueByProps(scope.row.orderItemList, 'extWarrantyPeriod', item.vendorId) }}
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          prop="orderRemark"
          :label="$t('cusEntry.inq.supplierRemark')"
          min-width="120"
          show-overflow-tooltip
        >
          <template slot-scope="scope">
            {{ showValueByProps(scope.row.orderItemList, 'orderRemark', item.vendorId) }}
          </template>
        </el-table-column>
      </el-table-column>
      <el-table-column
        align="center"
        prop="extHistoryVendorName1"
        :label="$t('cusEntry.bidMod.extHisttoryPriceVendorName')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="extHistoryVendorPrice1"
        :label="$t('cusEntry.bidMod.extHisttoryPrice')"
        min-width="150"
        show-overflow-tooltip
      />
    </el-table>

    <!-- <CPagination
      :total="pagination.total"
      :page-num="pagination.pageNum"
      :page-size="pagination.pageSize"
      @current-change="paginationCurrentChange"
      @size-change="paginationSizeChange"
    /> -->

    <!--比价折线图-->
    <!-- <div
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
    </div> -->

    <!--发起新一轮报价-->
    <ContinueTalkPriceDialog
      v-if="continueTalkPriceDialogVisible"
      :visible.sync="continueTalkPriceDialogVisible"
      :header="header"
      :material-list="multipleSelection"
      :vendor-list="vendorList"
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
    <!--修改中标供应商 -->
    <ChangeBidSupplierDialog
      :visible.sync="changeBidSupplierVisible"
      :header="selectRow"
      :vendor-list="changeBidVendorList"
      :order-status="header.extProjectStatus"
      @refreshSelectList="getSelectList"
    />
  </div>
</template>

<script>
/**
 * 报价评选弹窗
 */
import { inqBuyerHttp } from 'modcb@/inquiry/api'
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
import ChangeBidSupplierDialog from './quoteSelection/change-bid-supplier-dialog'
import { downloadFileLinkByPost } from '@/library/utils/file'
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
    SeparationPriceDialog,
    ChangeBidSupplierDialog
  },

  props: {
    header: {
      type: Object,
      required: true
    },
    /* 控制编辑权限 */
    showQuoteEvaluation: {
      type: Boolean,
      default: true
    }
  },

  data () {
    return {
      /* 修改供应商弹窗 */
      changeBidSupplierVisible: false,
      /* 选择变更行 */
      selectRow: {},
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
      editRow: null,
      vendorList: [],
      changeBidVendorList: []
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
  watch: {
    'header.projectId': {
      immediate: true,
      handler (newValue, oldValue) {
        if (newValue !== oldValue) {
          newValue && this.getSelectList()
        }
      }
    }
  },
  mounted () {
    this.$refs.quoteSelectionTable.doLayout()
  },
  methods: {
    // 关闭行
    closeRow () {
      if (this.multipleSelection.length === 0) {
        this.$message.warning(this.$t('cusEntry.tipMessage.selectColseData'))
        return false
      }
      if (this.multipleSelection.length > 1) {
        this.$message.warning(this.$t('cusEntry.tipMessage.onlyOneData'))
        return false
      }
      this.$prompt('', this.$t('cusEntry.inq.closeReason'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        inputType: 'textarea',
        inputValidator: (value) => {
          if (!value) {
            return this.$t('cusEntry.tipMessage.closeReason')
          }
          return true
        }
      }).then(({ value }) => {
        const data = {
          souItemId: this.multipleSelection[0].souItemId,
          reason: value
        }
        inqBuyerHttp.select.closeRow(data).then(res => {
          this.$message.success(this.$t('cusEntry.tipMessage.closeSuccess'))
          this.getSelectList()
        })
      })
    },
    /* 导出 */
    exportDetail () {
      downloadFileLinkByPost(
        '/api-sou/npm/buyer/inq/select/queryItemSelectInfo/downloadExcel',
        this.$t('cusEntry.inq.exportSelectDetail'),
        { projectId: this.header.projectId }
      ).catch(() => {
        this.$message.error(this.$t('cusEntry.tipMessage.exportFail'))
      })
    },
    /* 取消行 */
    cancelRow () {
      if (this.multipleSelection.length === 0) {
        this.$message.warning(this.$t('cusEntry.tipMessage.selectCancelRows'))
        return false
      }
      this.$confirm('取消数据回到需求池是否确认?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const submitData = this.multipleSelection.map(item => item.souItemId)
        inqBuyerHttp.select.cancelRow(submitData).then(res => {
          this.$message.success(this.$t('cusEntry.tipMessage.successCancel'))
          this.getSelectList()
        })
      }).catch(err => {
        console.log(err)
      })
    },
    /* 终止询价 */
    stopInquiry () {
      /* 存在未报价物料不允许结束 */
      let validFlag = true
      this.quoteSelectionTable.some(item => {
        if (item.orderCount == 0) {
          validFlag = false
          return true
        }
      })
      if (!validFlag) {
        this.$message.warning(this.$t('cusEntry.tipMessage.hasMaterialNoQuote'))
        return false
      }
      inqBuyerHttp.select.stopInquiry(this.header.projectId).then(res => {
        this.$message.success(this.$t('cusEntry.tipMessage.stopInquirySuccess'))
        this.$emit('backTo')
      })
    },
    /* 显示修改中标供应商弹窗 */
    showSupplierChangeDialog (row) {
      this.selectRow = row
      const {
        souItemId,
        round
      } = row
      inqBuyerHttp.select.getChangeSuppilers({ souItemId, round }).then(res => {
        this.changeBidVendorList = res.data || []
      })
      this.changeBidSupplierVisible = true
    },
    /* 总价比价 */
    compareTotalAmount () {
      inqBuyerHttp.select.compareTotalAmount(this.header.projectId).then(res => {
        this.$message.success(this.$t('cusEntry.tipMessage.compareTotalAmountSuccess'))
        this.getSelectList()
      })
    },
    /* 根据属性展示对应的值 */
    showValueByProps (orderItemList = [], prop, vendorId) {
      let value = ''
      if (orderItemList.length) {
        /* 获取对应的报价数据 */
        const targetRow = orderItemList.find(item => item.vendorId === vendorId)
        if (targetRow) {
          value = targetRow[prop]
        }
      }
      return value
    },
    /* 获取评选数据 */
    getSelectList (winRow) {
      inqBuyerHttp.select.getSelectListByProjectId({ projectId: this.header.projectId }).then(res => {
        if (res.data) {
          const {
            itemList,
            vendorList
          } = res.data
          this.quoteSelectionTable = itemList
          this.vendorList = vendorList
          /* 获取最新的评选数据 */
          if (winRow) {
            /* 重新渲染中标修改供应商列表数据 */
            const {
              souItemId,
              round
            } = winRow
            const row = itemList.find(item => item.souItemId === souItemId && item.round === round)
            this.showSupplierChangeDialog({ ...winRow, winVendorId: winRow.vendorId, orderItemList: row.orderItemList || [] })
          }
        }
      })
    },
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
        // this.pagination.total = response.data.total
        // if (this.quoteSelectionTable.length > 0) {
        //   this.$nextTick(() => {
        //     if (this.$refs.quoteSelectionTable) {
        //       this.$refs.quoteSelectionTable.setCurrentRow(this.quoteSelectionTable[0])
        //     }
        //   })
        //   await this.rowClick(this.quoteSelectionTable[0])
        // }
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
      if (this.multipleSelection.length === 0) {
        this.$message.warning(this.$t('cusEntry.tipMessage.selectNextRoundData'))
        return false
      }
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
    // tableSpanMethod ({ column, rowIndex }) {
    //   return quoteSelectionTableSpanMethod(column, rowIndex)
    // },

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
// .quote-selection-table.el-table :deep(th.gutter) {
//   display: none !important;
// }
</style>
