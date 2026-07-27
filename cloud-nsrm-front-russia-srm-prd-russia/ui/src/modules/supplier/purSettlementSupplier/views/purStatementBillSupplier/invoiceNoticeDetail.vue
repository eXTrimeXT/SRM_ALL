<template>
  <el-container class="flex-container the-purInvoice-detail" direction="vertical">
    <el-main>
      <div class="form-container2">
        <el-form
          ref="invoiceNoticeForm"
          :model="form"
          :rules="formRules"
          label-position="top"
          class="form-incontainer"
        >
          <el-collapse v-model="activeDims" class="tab-form-style">
            <el-collapse-item :title="$t('vendorMod.receiptInfo')" name="1">
              <srm-row :gutter="32">
                <!-- 业务实体 -->
                <srm-col>
                  <el-form-item :label="$t('quota.org')" prop="orgId">
                    <OrganizationSelector
                      ref="organizationSelector"
                      v-model="form.orgId"
                      :disabled="isDisabled || isReject"
                      :parent-id="-1"
                      node-type="OU"
                      :placeholder="$t('common.pleaseSelect')"
                      @select="selectHandler"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 库存组织 -->
                  <el-form-item :label="$t('purchaseDemand.invOrg')" prop="organizationId">
                    <!-- auto-select-when-one-item -->
                    <OrganizationSelector
                      ref="organizationSelector2"
                      v-model="form.organizationId"
                      :parent-id="form.orgId"
                      node-type="INV"
                      :placeholder="$t('common.pleaseSelect')"
                      :disabled="isDisabled || isReject"
                      @select="selectHandler2"
                    />
                  </el-form-item>
                </srm-col>
                <!-- 供应商 -->
                <srm-col>
                  <el-form-item :label="$t('common.vendor')" prop="vendorName">
                    <QuickSearch
                      disabled
                      :show-input="form.vendorName"
                      show-key="vendorCode"
                      :scope-data="form"
                      name="scc_sup_company_info5"
                      @close-quicksearch="getVendorObj"
                    />
                  </el-form-item>
                </srm-col>
                <!-- 对账期间 -->
                <srm-col>
                  <el-form-item :label="$t('accountMod.statementDate')" prop="receiveDate">
                    <el-date-picker
                      v-model="form.receiveDate"
                      :disabled="isReadOnly || isReject"
                      type="daterange"
                      value-format="yyyy-MM-dd"
                      range-separator="~"
                      :start-placeholder="$t('dataConfMod.startDay')"
                      :end-placeholder="$t('dataConfMod.endDay')"
                      @change="invoiceDetails = []"
                    />
                  </el-form-item>
                </srm-col>
                <!-- 币种 -->
                <srm-col>
                  <el-form-item :label="$t('quota.currency')" prop="currencyName">
                    <QuickSearch
                      :disabled="isDisabled || isReject"
                      :show-input="form.currencyName"
                      show-key="currencyName"
                      :scope-data="form"
                      name="scc_base_purchase_currency_info"
                      @close-quicksearch="getCurrencyObj"
                    />
                  </el-form-item>
                </srm-col>
                <!-- 税率 -->
                <srm-col>
                  <el-form-item
                    :label="$t('purchaseDemand.taxRate')"
                    prop="taxKey"
                    :rules="{
                      required: true,
                      message: this.$t('bidMod.msgSelTaxRate'),
                    }"
                  >
                    <dict-select
                      v-model="form.taxKey"
                      code="tax"
                      :disabled="isDisabled || isReject"
                      @change-value="taxRateChangeHandel"
                    />
                  </el-form-item>
                </srm-col>
                <!-- 单据号 -->
                <srm-col>
                  <el-form-item :label="$t('flowMod.documentNo')">
                    <el-input v-model="form.invoiceNoticeNumber" disabled />
                  </el-form-item>
                </srm-col>
                <!-- 账期 -->
                <srm-col>
                  <el-form-item
                    :label="$t('paymentType.paymentDay')"
                    prop="paymentPeriod"
                    :rules="{
                      required: true,
                      message: this.$t('purSettlementMod.selectAccountingPeriod'),
                    }"
                  >
                    <dict-select v-model="form.paymentPeriod" code="PAYMENT_PERIOD" :disabled="isDisabled || isReject" />
                  </el-form-item>
                </srm-col>
                <!-- 单据状态 -->
                <srm-col>
                  <el-form-item :label="$t('purSettlementMod.paymentPlanStatus')">
                    <dict-select
                      v-model="form.invoiceNoticeStatus"
                      code="INVOICE_NOTICE_STATUS"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <!-- 创建日期 -->
                <srm-col>
                  <el-form-item :label="$t('purSettlementMod.creationDate')" prop="creationDate">
                    <el-date-picker
                      v-model="form.creationDate"
                      disabled
                      type="date"
                      format="yyyy-MM-dd"
                      value-format="yyyy-MM-dd"
                    />
                  </el-form-item>
                </srm-col>
                <!-- 创建人 -->
                <srm-col>
                  <el-form-item :label="$t('common.creator')" prop="createdUserName">
                    <el-input v-model="form.createdUserName" disabled />
                  </el-form-item>
                </srm-col>
                <!-- 含税总金额 -->
                <srm-col>
                  <el-form-item>
                    <template #label>
                      <div>
                        <span style="margin-right: 18px">{{ $t('contractMod.totalAmountTax') }}</span>
                        <el-tooltip class="item" effect="dark" :content="$t('purSettlementMod.amount2Tip')" placement="top">
                          <i class="el-icon-question" />
                        </el-tooltip>
                      </div>
                    </template>
                    <el-input-number
                      v-model="form.ceeaTaxTotalAmount"
                      :precision="8"
                      disabled
                      :controls="false"
                      class="input-number-precision"
                    />
                  </el-form-item>
                </srm-col>
                <!-- 未税总金额 -->
                <srm-col>
                  <el-form-item :label="$t('contractMod.totalAmountNoTax2')">
                    <el-input-number
                      v-model="form.ceeaNoTaxTotalAmount"
                      :precision="8"
                      disabled
                      :controls="false"
                      class="input-number-precision"
                    />
                  </el-form-item>
                </srm-col>
                <!-- 总税额 -->
                <srm-col>
                  <el-form-item :label="$t('accountMod.totalTax')">
                    <template #label>
                      <div>
                        <span style="margin-right: 18px">{{ $t('accountMod.totalTax') }}</span>
                        <el-tooltip class="item" effect="dark" :content="$t('purSettlementMod.taxQuotaTip')" placement="top">
                          <i class="el-icon-question" />
                        </el-tooltip>
                      </div>
                    </template>
                    <el-input-number
                      v-model="form.ceeaTotalTax"
                      :precision="8"
                      disabled
                      :controls="false"
                      class="input-number-precision"
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>
            <el-collapse-item :title="$t('route.warehousingAndReturnGoods')" name="2">
              <p class="btn_line">
                <el-button
                  type="primary"
                  :disabled="isDisabled"
                  class="detail-pbtn"
                  @click="addInvoiceDetails"
                >
                  {{ $t('common.add') }}
                </el-button>
                <!-- <el-button type="primary" class="detail-pbtn" @click="openVisible">
                  {{ $t('common.export') }}
                </el-button> -->
              </p>
              <el-table :data="invoiceDetails" border stripe style="width: 100%">
                <el-table-column
                  :label="$t('purchaseDemand.lineNum')"
                  align="center"
                  type="index"
                  width="50"
                />
                <!-- 事务类型 -->
                <el-table-column
                  prop="type"
                  :label="$t('purSettlementMod.type')"
                  :formatter="formatData"
                  width="100"
                  align="center"
                  show-overflow-tooltip
                />
                <!-- 事务处理日期 -->
                <el-table-column
                  prop="receiveDate"
                  :label="$t('orderMod.transactionDate')"
                  width="150"
                  align="center"
                  show-overflow-tooltip
                />
                <!-- 入库/退货单号 -->
                <el-table-column
                  prop="receiveOrderNo"
                  :label="$t('accountMod.inboundReturnOrderNo')"
                  width="150"
                  align="center"
                  show-overflow-tooltip
                />
                <!-- 入库/退货行号 -->
                <el-table-column
                  prop="receiveOrderLineNo"
                  :label="$t('accountMod.inboundReturnLineNo')"
                  width="120"
                  align="center"
                  show-overflow-tooltip
                />
                <!-- 采购订单号 -->
                <el-table-column
                  prop="orderNumber"
                  :label="$t('purSettlementMod.orderNumber')"
                  width="130"
                  align="center"
                  show-overflow-tooltip
                />
                <!-- 采购订单行号 -->
                <el-table-column
                  prop="lineNum"
                  :label="$t('purSettlementMod.orderLineNumber')"
                  width="100"
                  align="center"
                  show-overflow-tooltip
                />
                <!-- 物料编码 -->
                <el-table-column
                  prop="itemCode"
                  :label="$t('common.materialCode')"
                  width="100"
                  align="center"
                  show-overflow-tooltip
                />
                <!-- 物料名称 -->
                <el-table-column
                  prop="itemName"
                  :label="$t('common.materialName')"
                  min-width="130"
                  align="center"
                  show-overflow-tooltip
                />
                <!-- 单位 -->
                <el-table-column
                  prop="unit"
                  :label="$t('dataConfMod.unit')"
                  width="60"
                  align="center"
                  show-overflow-tooltip
                />
                <!-- 事务处理数量 -->
                <el-table-column
                  prop="receiveNum"
                  label="事务处理数量"
                  width="80"
                  align="center"
                  show-overflow-tooltip
                />
                <!-- 对账数量 与事务处理数量取值一样 -->
                <el-table-column
                  prop="invoiceQuantity"
                  :label="this.$t('purSettlementMod.reconciliationQuantity')"
                  width="80"
                  align="center"
                  show-overflow-tooltip
                >
                  <template slot-scope="scope">
                    <span v-if="scope.row.type === 'RETURN'" style="color:red;font-weight:700;">
                      {{ scope.row.invoiceQuantity }}
                    </span>
                    <span v-else>
                      {{ scope.row.invoiceQuantity }}
                    </span>
                  </template>
                </el-table-column>
                <!-- 未税单价 -->
                <el-table-column
                  prop="unitPriceExcludingTax"
                  :label="$t('purSettlementMod.unitPriceNoTax')"
                  width="80"
                  align="center"
                  :formatter="setNumberPrecision"
                  show-overflow-tooltip
                />
                <!-- 税率 -->
                <el-table-column
                  prop="taxRate"
                  :label="$t('bidMod.taxRate')"
                  width="80"
                  align="center"
                  show-overflow-tooltip
                />
                <!-- 含税金额 -->
                <el-table-column
                  prop="taxAmount"
                  :label="$t('contractMod.amount2')"
                  width="80"
                  align="center"
                  :formatter="setNumberPrecision"
                  show-overflow-tooltip
                />
                <!-- 币种 -->
                <el-table-column
                  align="center"
                  width="100"
                  prop="currencyName"
                  :label="$t('purchaseDemand.currency')"
                  show-overflow-tooltip
                />
                <el-table-column
                  :label="$t('common.operation')"
                  width="60"
                  align="center"
                  fixed="right"
                >
                  <template slot-scope="scope">
                    <el-button
                      :disabled="isDisabled"
                      type="text"
                      @click="delInvoiceDetails(scope.$index, scope.row)"
                    >
                      {{ $t('common.delete') }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>

            <el-collapse-item :title="$t('quota.fileInfo')" name="3">
              <p class="btn_line">
                <el-button
                  :disabled="isDisabled"
                  type="primary"
                  class="detail-pbtn"
                  @click="addFileuploads"
                >
                  {{ $t('common.add') }}
                </el-button>
              </p>
              <upload-attach
                :attachData="fileuploads"
                :fileInfo="fileInfo"
                :attachName="'fileSourceName'"
              />
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </div>

      <CToolbar>
        <template slot="right">
          <el-button @click="backTo">
            {{ !isDisabled ? $t('common.cancel') : $t('common.close') }}
          </el-button>
          <el-button
            v-if="['add', 'edit'].includes(curOpt)"
            type="primary"
            @click="saveOrSubmitData('SAVE')"
          >
            {{ $t('flowMod.temporaryView') }}
          </el-button>
          <el-button
            v-if="['add', 'edit'].includes(curOpt)"
            type="primary"
            @click="saveOrSubmitData('SUBMIT')"
          >
            {{ $t('bidMod.submitapprovlaFlowing') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>

    <!-- 明细新增-->
    <srm-dialog
      :title="$t('purSettlementMod.stockInReturnDetailsSel')"
      size="large"
      :visible.sync="dialogFormVisible"
      :close-on-click-modal="false"
    >
      <div>
        <el-form ref="filterForm" :model="filterForm" label-position="top" class="form-incontainer">
          <srm-row :gutter="32">
            <!-- 入库退货单号 -->
            <srm-col :initCol="3">
              <el-form-item
                :label="$t('purSettlementMod.inboundReturnNumber')"
                :label-width="formLabelWidth"
              >
                <el-input v-model="filterForm.receiveOrderNo" clearable />
              </el-form-item>
            </srm-col>
            <!-- 采购订单号 -->
            <srm-col :initCol="3">
              <el-form-item
                :label="$t('purSettlementMod.orderNumber')"
                :label-width="formLabelWidth"
              >
                <el-input v-model="filterForm.orderNumber" clearable />
              </el-form-item>
            </srm-col>
            <!-- 物料名称 -->
            <srm-col :initCol="3">
              <el-form-item prop="materialId" :label="$t('common.materialName')">
                <QuickSearch
                  :disable="isReadOnly"
                  :show-input="filterForm.materialName"
                  show-key="materialId"
                  :scope-data="filterForm"
                  name="scc_base_material_item"
                  @close-quicksearch="getMaterialByQuick"
                />
              </el-form-item>
            </srm-col>
          </srm-row>
        </el-form>
      </div>
      <div style="margin-bottom: 8px;">
        <el-button type="primary" @click="queryItemList">
          {{ $t('common.search') }}
        </el-button>
        <el-button type="primary" @click="addNewOne">
          {{ $t('common.confirm') }}
        </el-button>
      </div>
      <el-table
        :data="displayMaterialItem"
        style="width: 100%"
        border
        height="345px"
        highlight-current-row
        @selection-change="handleSelectionChange"
        @row-dblclick="handleItemDBClick"
      >
        <el-table-column type="selection" width="55" fixed="left" />
        <el-table-column :label="$t('common.sort')" align="center" type="index" width="50" />
        <!-- 事务类型 -->
        <el-table-column
          prop="type"
          :label="$t('purSettlementMod.type')"
          :formatter="formatData"
          width="100"
          align="center"
          show-overflow-tooltip
        />
        <!-- 事务处理日期 -->
        <el-table-column
          prop="receiveDate"
          :label="$t('orderMod.transactionDate')"
          width="150"
          align="center"
          show-overflow-tooltip
        />
        <!-- 入库/退货单号 -->
        <el-table-column
          prop="receiveOrderNo"
          :label="$t('accountMod.inboundReturnOrderNo')"
          width="150"
          align="center"
          show-overflow-tooltip
        />
        <!-- 入库/退货行号 -->
        <el-table-column
          prop="receiveOrderLineNo"
          :label="$t('accountMod.inboundReturnLineNo')"
          width="120"
          align="center"
          show-overflow-tooltip
        />
        <!-- 采购订单号 -->
        <el-table-column
          prop="orderNumber"
          :label="$t('purSettlementMod.orderNumber')"
          width="150"
          align="center"
          show-overflow-tooltip
        />
        <!-- 采购订单行号 -->
        <el-table-column
          prop="lineNum"
          :label="$t('purSettlementMod.orderLineNumber')"
          width="100"
          align="center"
          show-overflow-tooltip
        />
        <!-- 物料编码 -->
        <el-table-column
          prop="itemCode"
          :label="$t('common.materialCode')"
          width="100"
          align="center"
          show-overflow-tooltip
        />
        <!-- 物料名称 -->
        <el-table-column
          prop="itemName"
          :label="$t('common.materialName')"
          min-width="150"
          align="center"
          show-overflow-tooltip
        />
        <!-- 单位 -->
        <el-table-column
          prop="unit"
          :label="$t('dataConfMod.unit')"
          width="60"
          align="center"
          show-overflow-tooltip
        />
        <!-- 事务处理数量 -->
        <el-table-column
          prop="receiveNum"
          :label="$t('purSettlementMod.receiveNum')"
          width="80"
          align="center"
          show-overflow-tooltip
        />
        <!-- 对账数量 与事务处理数量取值一样 -->
        <el-table-column
          prop="invoiceQuantity"
          :label="this.$t('purSettlementMod.reconciliationQuantity')"
          width="80"
          align="center"
          show-overflow-tooltip
        />
        <!-- 未税单价 -->
        <el-table-column
          prop="unitPriceExcludingTax"
          :label="$t('purSettlementMod.unitPriceNoTax')"
          width="80"
          align="center"
          :formatter="setNumberPrecision"
          show-overflow-tooltip
        />
        <!-- 税率 -->
        <el-table-column
          prop="taxRate"
          :label="$t('bidMod.taxRate')"
          width="80"
          align="center"
          show-overflow-tooltip
        />
        <!-- 含税单价 -->
        <el-table-column
          prop="unitPriceContainingTax"
          :label="$t('quota.taxPrice')"
          width="80"
          align="center"
          :formatter="setNumberPrecision"
          show-overflow-tooltip
        />
        <!-- 含税金额 -->
        <el-table-column
          prop="taxAmount"
          :label="$t('contractMod.amount2')"
          width="80"
          align="center"
          show-overflow-tooltip
        />
        <!-- 币种 -->
        <el-table-column
          align="center"
          width="100"
          prop="currencyName"
          :label="$t('purchaseDemand.currency')"
          show-overflow-tooltip
        />
      </el-table>
      <CPagination
        :total="parentOrgTableDataPage.total"
        :page-num="parentOrgTableDataPage.pageNum"
        :page-size="parentOrgTableDataPage.pageSize"
        @current-change="parentDataCurrentChange"
        @size-change="parentDataSizeChange"
      />
    </srm-dialog>
  </el-container>
</template>
<script>
import uploadAttach from '@/library/composition/orderManagementBuyer/upload-attach'
import CToolbar from 'lib@/components/c-toolbar'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import QuickSearch from 'lib@/components/QuickSearch'
import { parseTime } from '@/utils'
import OrganizationSelector from 'lib@/components/organization-selector'
import CPagination from 'lib@/components/c-pagination'
import { downloadFileLink } from 'lib@/utils/file'
import { mapGetters } from 'vuex'

export default {
  name: 'InvoiceNoticeDetail',
  components: {
    QuickSearch,
    OrganizationSelector,
    CPagination,
    CToolbar,
    uploadAttach
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      paymentOpts: [], // 账期
      getbutton: false,
      fileInfo: {
        uploadType: 'DEF',
        sourceType: 'WEB_APP',
        fileModular: 'pur',
        fileFunction: 'purInvoice',
        fileType: 'images'
      },
      minusTax: null,
      minusTaxTotalAmount: null,
      minusNoTaxTotalAmount: null,
      // userInfo: this.$store.getters.userInfo,
      curRole: this.$store.getters.userType, // VENDOR BUYER curRole==='VENDOR'
      isReadOnly: this.$attrs.params.flag === 'readOnly',
      isApprovalOnly: this.$attrs.params.flag === 'approvalOnly',
      curOrderId: '',
      toCurrency: null,
      yesNoOptions: [
        { value: 'Y', label: this.$t('common.yes') },
        { value: 'N', label: this.$t('common.no') }
      ],
      form: {
        receiveDate: [],
        creationDate: '',
        paymentPeriod: '',
        ceeaReceiveStartDate: '',
        ceeaReceiveEndDate: '',
        invoiceNoticeId: '',
        invoiceNoticeStatus: '',
        orgId: '',
        orgCode: '',
        orgName: '',
        organizationId: '',
        organizationName: '',
        organizationCode: '',
        taxRate: '',
        taxKey: '',
        vendorCode: '',
        vendorName: '',
        vendorId: '',
        currencyId: '',
        currencyCode: '',
        currencyName: '',
        ceeaInvoiceDate: parseTime(new Date(), '{y}-{m}-{d}')
      },
      formLabelWidth: '120px',
      selectionItem: [],
      displayMaterialItem: [],
      parentOrgTableDataPage: {
        total: 0,
        pageNum: 1,
        pageSize: 20
      },
      dialogFormVisible: false,
      filterForm: {
        vendorId: '',
        ceeaReceiveStartDate: '',
        ceeaReceiveEndDate: '',
        taxKey: '',
        materialId: '',
        materialCode: null,
        materialName: null,
        orgName: null,
        ceeaEmpUsername: null,
        categoryCode: null,
        organizationId: null,
        orderNumber: null,
        orderStatus: 'ACCEPT'
      },
      parentOrgQueryForm: {
        pageNum: 1,
        pageSize: 10
      },
      parentOrgQueryForm2: {
        pageNum: 1,
        pageSize: 10
      },
      selectionItem2: [],
      displayMaterialItem2: [],
      parentOrgTableDataPage2: {
        total: 0,
        pageNum: 1,
        pageSize: 20
      },
      dialogFormVisible2: false,
      filterForm2: {
        materialCode: null,
        materialName: null,
        orgName: null,
        categoryCode: null,
        organizationId: null,
        orderNumber: null,
        startDate: null,
        endDate: null,
        orderStatus: 'ACCEPT'
      },
      formRules: {
        orgId: [{ required: true, message: this.$t('quota.orgIdTips') }],
        organizationId: [
          {
            required: true,
            message: this.$t('purchaseDemand.organizationIdTips')
          }
        ],
        ceeaInvoiceDate: [
          {
            required: true,
            message: this.$t('purSettlementMod.invoiceDateTips')
          }
        ],
        currencyName: [{ required: true, message: this.$t('vendorMod.msgCurrencyCode') }],
        receiveDate: [{ required: true, message: this.$t('qualitySynergy.msgSelDate') }]
      },
      loading: false,
      invoiceDetails: [], // 入库明细
      fileuploads: [],
      curOpt: 'add',
      activeDims: ['1', '2', '3', '4']
    }
  },
  computed: {
    ...mapGetters(['userInfo']),
    isDisabled () {
      // 采购已驳回/add/edit可编辑
      return this.curOpt !== 'add' && this.curOpt !== 'edit'
    },
    isReject () {
      return this.invoiceDetails.length >= 1
    }
  },
  created () {
    this.curOpt = this.$attrs.params.flag
    this.form.vendorCode = this.userInfo.companyCode
    this.form.vendorName = this.userInfo.companyName
    this.form.vendorId = this.userInfo.companyId
    if (this.curOpt !== 'add') {
      // 编辑 查看
      this.getOrderFormDetail(this.$attrs.params.invoiceNoticeId) // 查询
    }
  },
  methods: {
    // 设置小数点位数8位
    setNumberPrecision (row, column, cellValue, index) {
      return cellValue.toFixed(8)
    },
    // 选择税率
    taxRateChangeHandel (value, dictItem) {
      this.form.taxRate = dictItem.key // 税率值
    },
    // 明细 - 物料名称
    getMaterialByQuick (val, scope) {
      scope.materialId = val ? val.materialId : ''
      scope.materialCode = val ? val.materialCode : ''
      scope.materialName = val ? val.materialName : ''
    },
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'invoiceNotice'
    },
    getCWorkflowRefName () {
      // 对应CWorkflowMulti标签中的ref
      return 'workflowMulti'
    },
    // 查询单据详情
    async getOrderFormDetail (invoiceNoticeId) {
      await this.$http({
        url: '/api-sup-ce/sup/invoice/invoiceNotice/getInvoiceNoticeSaveDTO',
        method: 'GET',
        params: { invoiceNoticeId: invoiceNoticeId },
        loading: true
      }).then(res => {
        if (res && res.data) {
          // 头信息
          Object.assign(this.form, {
            ...res.data.invoiceNotice
          })
          // 对账期间日期合并
          const receiveDate = res.data.invoiceNotice.ceeaReceiveStartDate
            ? [
              res.data.invoiceNotice.ceeaReceiveStartDate,
              res.data.invoiceNotice.ceeaReceiveEndDate
            ]
            : []
          this.$set(this.form, 'receiveDate', receiveDate)
          if (res.data.invoiceNotice.ceeaTotalTax) {
            this.form.ceeaTotalTax = res.data.invoiceNotice.ceeaTotalTax
          }
          this.invoiceDetails = res.data.invoiceDetails // 入库明细
          this.fileuploads = res.data.fileuploads // 税控发票信息
        }
      })
    },
    // 退货单明细新增
    addInvoiceDetails () {
      const sign =
        !this.form.orgId ||
        !this.form.organizationId ||
        this.form.receiveDate.length < 1 ||
        !this.form.vendorId ||
        !this.form.currencyCode ||
        !this.form.taxKey
      if (sign) {
        // 请先选择业务实体、库存组织、对账期间、币种、税率
        return this.$message.error(this.$t('purSettlementMod.selectionCriteria'))
      }
      Object.assign(this.filterForm, {
        orgId: this.form.orgId,
        organizationId: this.form.organizationId,
        ceeaReceiveStartDate: this.form.receiveDate[0] + ' 00:00:00',
        ceeaReceiveEndDate: this.form.receiveDate[1] + ' 23:59:59',
        vendorId: this.form.vendorId,
        currencyCode: this.form.currencyCode,
        taxKey: this.form.taxKey
      })
      this.queryItemList()
    },
    // 新增税控明细
    addFileuploads () {
      this.fileuploads.push({
        fileuploadId: null,
        fileSourceName: null,
        comment: null
      })
    },
    // 删除明细
    delInvoiceDetails (index, row) {
      this.invoiceDetails.splice(index, 1)
      this.setTotal()
    },
    // 导出
    async openVisible () {
      downloadFileLink(
        '/api-sup-ce/invoice/invoiceNotice/exportInvoiceDetails?invoiceNoticeId=' +
          this.$attrs.params.invoiceNoticeId,
        this.$attrs.params.tabName + this.$t('purSettlementMod.selectExport'),
      ).catch(() => {
        this.$message.error(this.$t('purchaseDemand.downloadFail'))
      })
    },
    // 删除银行证明文件
    outerHandleAttachmentRemove (row) {
      row.fileuploadId = ''
      row.fileSourceName = ''
    },
    reset () {
      // 重置所有过滤条件
      for (const i in this.form) {
        this.form[i] = ''
      }
    },
    // 设置个别属性
    setPropsInfor () {
      if (!this.form.receiveDate) return
      // 对账期间
      this.$set(this.form, 'ceeaReceiveStartDate', this.form.receiveDate[0])
      this.$set(this.form, 'ceeaReceiveEndDate', this.form.receiveDate[1])
    },
    async saveOrSubmitData (type) {
      if (this.form.ceeaTaxTotalAmount < 0) {
        // 入库退货明细 采购接收金额应大于采购退货金额!
        return this.$message.warning(this.$t('purInvoice.prompt3'))
      }
      this.setPropsInfor()

      if (type === 'SUBMIT') {
        const { flag, message } = await this.getCheckForm()
        if (flag) {
          this.submitData(type)
        } else {
          this.__focus_error__(message)
        }
      } else {
        this.saveData(type)
      }
    },
    // form验证返回promise校验返回trun or false
    formValidate (formRef) {
      return new Promise((resolve) => {
        this.$refs[formRef].validate((flag, obj) => {
          resolve({ flag, obj })
        })
      })
    },
    /*
    * @Description: 校验表单表格必填项
    * @return: {
    *   flag: true/false,  校验是否通过
    *   message: 返回填写信息
    * }
    */
    async getCheckForm () {
      const formFiled = await this.formValidate('invoiceNoticeForm')

      if (!formFiled.flag && Object.keys(formFiled.obj).length > 0) {
        const warnObj = Object.keys(formFiled.obj)[0]
        return {
          flag: formFiled.flag,
          message: formFiled.obj[warnObj][0].message
        }
      }
      return { flag: true }
    },
    // 暂存
    async saveData (type) {
      const { code, data } = await this.$http({
        url: '/api-sup-ce/sup/invoice/invoiceNotice/saveTemporary',
        method: 'POST',
        data: {
          invoiceNotice: this.form,
          invoiceDetails: this.invoiceDetails, // 入库明细
          fileuploads: this.fileuploads // 税控发票信息
        },
        loading: true
      })

      if (code === '0') {
        await this.getOrderFormDetail(data)
        type === 'SAVE' && this.$message.success(this.$t('common.successSave'))
      }
    },
    // 提交
    async submitData (type) {
      // 明细行可以修改的情况下要先掉暂存接口，获取最新数据，避免其他人操作单据导致数量对不上
      ['add', 'edit'].includes(this.curOpt) && await this.saveData(type)

      this.$http({
        url: '/api-sup-ce/sup/invoice/invoiceNotice/submit',
        method: 'POST',
        data: {
          invoiceNotice: this.form,
          invoiceDetails: this.invoiceDetails, // 入库明细
          fileuploads: this.fileuploads // 税控发票信息
        },
        loading: true
      }).then(_ => {
        this.$message.success(this.$t('common.successSubmit'))
        this.backTo()
      })
    },
    selectHandler (node, value, scope) {
      this.form.orgId = node ? node.organizationId : null
      this.form.orgCode = node ? node.organizationCode : null
      this.form.orgName = node ? node.organizationName : null

      if (this.form.organizationId) {
        this.form.organizationId = null
        this.form.organizationCode = null
        this.form.organizationName = null
      }
    },
    selectHandler2 (node, value, scope) {
      if (node) {
        this.$set(this.form, 'organizationId', node.organizationId)
        this.$set(this.form, 'organizationCode', node.organizationCode)
        this.$set(this.form, 'organizationName', node.organizationName)
      }
    },
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
    },
    getCurrencyObj (val, scope) {
      scope.currencyId = val ? val.currencyId : ''
      scope.currencyCode = val ? val.currencyCode : ''
      scope.currencyName = val ? val.currencyName : ''
    },
    parentDataCurrentChange (num) {
      this.parentOrgQueryForm.pageNum = num
      this.queryItemList()
    },
    parentDataSizeChange (size) {
      this.parentOrgQueryForm.pageSize = size
      this.queryItemList()
    },
    queryItemList () {
      const data = { ...this.parentOrgQueryForm, ...this.filterForm }
      this.$http({
        url: '/api-sup-ce/ps/invoice/invoiceNoticeDetail/searchWarehousingReturnDetail',
        method: 'POST',
        data: data,
        loading: true
      }).then(res => {
        this.displayMaterialItem = res.data.list
        this.parentOrgTableDataPage.total = res.data.total
        this.dialogFormVisible = true
      })
    },
    addNewOne () {
      if (this.selectionItem.length === 0) {
        return this.$message.warning(this.$t('common.msgSelectData'))
      }
      const warehousingReturnDetailIdArr = this.invoiceDetails.map(v => v.warehousingReturnDetailId)
      for (const item of this.selectionItem) {
        if (!warehousingReturnDetailIdArr.includes(item.warehousingReturnDetailId)) {
          this.invoiceDetails.push(item)
        } else {
          this.$message.warning(this.$t('purInvoice.prompt4')) // 存在重复行
          return
        }
      }
      this.getTaxCal()
      this.dialogFormVisible = false
    },
    // 计算含税金额，未税金额，税额
    getTaxCal () {
      const taxAll = this.invoiceDetails.map(item => {
        const obj = {}
        if (item.tax) {
          obj.tax = item.type === 'RETURN' ? -item.tax : Number(item.tax)
        }
        if (item.taxAmount) {
          obj.taxAmount = item.type === 'RETURN' ? -item.taxAmount : Number(item.taxAmount)
        }
        if (item.noTaxAmount) {
          obj.noTaxAmount = item.type === 'RETURN' ? -item.noTaxAmount : Number(item.noTaxAmount)
        }
        return obj
      })

      // 税额
      this.form.ceeaTotalTax = taxAll
        .map(v => v.tax)
        .reduce((p, c) => (Number(p) || 0) + (Number(c) || 0))
      // 含税金额
      this.form.ceeaTaxTotalAmount = taxAll
        .map(v => v.taxAmount)
        .reduce((p, c) => (Number(p) || 0) + (Number(c) || 0))
      // 未税金额
      this.form.ceeaNoTaxTotalAmount = taxAll
        .map(v => v.noTaxAmount)
        .reduce((p, c) => (Number(p) || 0) + (Number(c) || 0))
    },
    resetFilterForm () {
      for (const i in this.filterForm) {
        this.filterForm[i] = ''
      }
    },
    parentDataCurrentChange2 (num) {
      this.parentOrgQueryForm2.pageNum = num
      this.queryItemList2()
    },
    parentDataSizeChange2 (size) {
      this.parentOrgQueryForm2.pageSize = size
      this.queryItemList2()
    },
    queryItemList2 () {
      const data = {
        ...this.parentOrgQueryForm2,
        ...this.filterForm2,
        // status: 'ASSESSED',
        vendorCode: this.form.vendorCode
        // ceeaAssociatedStates: 'Y'
      }
      this.$http({
        url: '/api-pef/vendorAsses/listPageForInvoice',
        method: 'POST',
        data: data,
        loading: true
      }).then(res => {
        this.displayMaterialItem2 = res.data.list
        this.parentOrgTableDataPage2.total = res.data.total
        this.dialogFormVisible2 = true
      })
    },
    setTotal (type) {
      if (this.invoiceDetails.length === 0) {
        this.form.ceeaTotalTax = 0
        this.form.ceeaTaxTotalAmount = 0
        this.form.ceeaNoTaxTotalAmount = 0
      } else {
        this.getTaxCal()
      }
    },
    handleSelectionChange (selection) {
      this.selectionItem = selection
      if (this.selectionItem.length !== 0) {
        // 把【接收数量】赋值给【本次开票数量和未开票数量】
        for (const item of this.selectionItem) {
          item.invoiceQuantity = item.receiveNum || 0
          item.notInvoiceQuantity = item.receiveNum || 0
        }
      }
    },
    handleItemDBClick (val) {
      this.selectionItem = [val]
      if (this.selectionItem.length !== 0) {
        // 把【接收数量】赋值给【本次开票数量和未开票数量】
        for (const item of this.selectionItem) {
          item.invoiceQuantity = item.receiveNum || 0
          item.notInvoiceQuantity = item.receiveNum || 0
        }
      }
      this.addNewOne()
    },
    handleSelectionChange2 (selection) {
      this.selectionItem2 = selection
    },
    resetFilterForm2 () {
      for (const i in this.filterForm2) {
        this.filterForm2[i] = ''
      }
    },
    formatData (row, column, cellValue, index) {
      return cellValue ? this.$getDictLabel('WAREHOURING_RETURN_DETAIL', cellValue) : cellValue
    },
    backTo () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('invoiceNoticeList.getQuerydata')
    }
  }
}
</script>
<style scoped lang="scss">
.the-purInvoice-detail {
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }

  .el-table .el-date-editor {
    width: 135px;
  }

  .btn_line {
    margin: 0 0 10px 0;
  }
  .input-number-precision {
    width: 100%;
    :deep(.el-input__inner) {
      text-align: left;
      padding-left: 8px;
    }
  }
}
</style>
