<template>
  <el-container class="flex-container the-paymentPlanDetail-detail" direction="vertical">
    <el-main>
      <CWorkflowMulti
        ref="workflowMulti"
        v-model="activeTabName"
        :fun-params="workflowParamsInfo"
        :button-config-info="buttonConfigInfo"
        @tab-click="workflowView"
        @workflow-handler="workflowHandler"
        @click-handler="type => saveOrSubmitData(type)"
        @submit-direct="type => saveOrSubmitData(type)"
        @confirm="(type, comment) => sasaveOrSubmitDataveHandle(type, comment)"
        @close-tab="backTo"
      >
        <div class="form-container2">
          <el-form
            ref="formRef"
            :disabled="isReadOnly || curOpt === 'approvalOnly'"
            :model="paymentApply"
            label-width="80px"
            label-position="top"
            class="form-incontainer"
            :rules="rules"
          >
            <el-collapse v-model="activeDims" class="tab-form-style">
              <el-collapse-item :title="$t('contractMod.paymentApplyDetail')" name="1">
                <form-detail
                  ref="formWapRef"
                  :form-array="paymentApplyHead"
                  :colLength="4"
                  :formData="paymentApply"
                />
              </el-collapse-item>
              <!-- 开票单 -->
              <el-collapse-item v-if="paymentApply.billType == 'ORDER'" :title="$t('purSettlementMod.invoice')" name="2">
                <p class="btn_line">
                  <el-button type="primary" class="detail-pbtn" @click="addInvoiceList">
                    {{ $t('common.add') }}
                  </el-button>
                </p>

                <TableView
                  ref="invoiceRef"
                  :bigData="true"
                  :tableInfor="paymentApply.invoiceInforData"
                  style="height: 150px;"
                  class="invoice-table"
                  :table-header="invoiceHeader"
                  :pageEnabled="false"
                >
                  <template #payingAmount="{ scope }">
                    <el-form-item
                      :prop="'invoiceInforData.' + scope.$index + '.payingAmount'"
                      :rules="{ required: true, validator: invoiceValidator, trigger: 'blur' }"
                    >
                      <el-input-number
                        v-model="scope.row.payingAmount"
                        :min="0"
                        :controls="false"
                        class="input-number-precision"
                        @change="setAmountCal"
                      />
                    </el-form-item>
                  </template>
                  <template #payMethod="props">
                    {{ $getDictLabel('PAYMENT_MODE', props.scope.row.payMethod) }}
                  </template>
                  <template #payAccountPeriodCode="props">
                    {{ $getDictLabel('PAYMENT_PERIOD', props.scope.row.payAccountPeriodCode) }}
                  </template>
                </TableView>
              </el-collapse-item>
              <!-- 合同履约预付款明细 -->
              <el-collapse-item v-if="paymentApply.billType == 'CONTRACT'" :title="$t('accountMod.advancePaymentDetail3')" name="3">
                <p class="btn_line">
                  <el-button type="primary" class="detail-pbtn" @click="openOneContent2">
                    {{ $t('common.add') }}
                  </el-button>
                </p>
                <el-table
                  :data="paymentApply.perAdvanceApplyDetails"
                  style="width: 100%"
                  border
                  max-height="700px"
                >
                  <el-table-column
                    :label="$t('common.sort')"
                    align="center"
                    type="index"
                    width="50"
                  />
                  <!-- 合同名称 -->
                  <el-table-column
                    align="center"
                    prop="contractName"
                    :label="$t('vendorMod.contractName')"
                    width="150"
                    show-overflow-tooltip
                  />
                  <!-- 里程碑名称 -->
                  <el-table-column
                    align="center"
                    prop="milestoneType"
                    :label="$t('contract_mod.processNodeName')"
                    width="150"
                    show-overflow-tooltip
                  >
                    <template slot-scope="scope">
                      {{ $getDictLabel('MILESTONE_SCHEDULE', scope.row.milestoneType) }}
                    </template>
                  </el-table-column>
                  <!-- 付款阶段 -->
                  <el-table-column
                    align="center"
                    prop="paymentStage"
                    :label="$t('bidMod.payStage')"
                    width="150"
                    show-overflow-tooltip
                  >
                    <template slot-scope="scope">
                      {{ $getDictLabel('PAYMENT_STAGE', scope.row.paymentStage) }}
                    </template>
                  </el-table-column>
                  <!-- 合同履约开票单号 -->
                  <el-table-column
                    align="center"
                    prop="invoiceNo"
                    :label="$t('contract_mod.processNum2')"
                    width="150"
                    show-overflow-tooltip
                  />
                  <!-- 供应商名称 -->
                  <el-table-column
                    align="center"
                    prop="vendorName"
                    :label="$t('common.vendorName')"
                    width="150"
                    show-overflow-tooltip
                  />
                  <!-- 供应商编号 -->
                  <el-table-column
                    align="center"
                    prop="vendorCode"
                    :label="$t('supplierRating.supplierCode')"
                    width="150"
                    show-overflow-tooltip
                  />
                  <!-- 组织 -->
                  <el-table-column
                    align="center"
                    prop="invName"
                    :label="$t('vendorMod.organization')"
                    width="150"
                    show-overflow-tooltip
                  />
                  <!-- 物料名称 -->
                  <el-table-column
                    align="center"
                    prop="materialName"
                    :label="$t('vendorMod.materialName')"
                    width="150"
                    show-overflow-tooltip
                  />
                  <!-- 物料编码 -->
                  <el-table-column
                    align="center"
                    prop="materialCode"
                    :label="$t('vendorMod.materialCode')"
                    width="150"
                    show-overflow-tooltip
                  />
                  <!-- 应付含税金额 -->
                  <el-table-column
                    align="center"
                    prop="payableTaxedAmount"
                    :label="$t('contract_mod.payableTax')"
                    width="150"
                    show-overflow-tooltip
                  />
                  <!-- 开票含税金额 -->
                  <el-table-column
                    align="center"
                    prop="invoicedTaxedAmount"
                    :label="$t('contract_mod.payableTax2')"
                    width="150"
                    show-overflow-tooltip
                  />
                  <!-- 代付款金额 -->
                  <el-table-column
                    align="center"
                    prop="stayPaymentAmount"
                    :label="$t('contract_mod.paymentAmount')"
                    width="150"
                    show-overflow-tooltip
                  />
                  <!-- 本次付款金额 -->
                  <el-table-column
                    align="center"
                    prop="currentPaymentAmount"
                    :label="$t('purSettlementMod.payingAmount')"
                    width="150"
                  >
                    <template #header>
                      <i style="color: red">*</i>
                      {{ $t("purSettlementMod.payingAmount") }}
                    </template>
                    <template slot-scope="scope">
                      <el-input
                        v-model="scope.row.currentPaymentAmount"
                      />
                    </template>
                  </el-table-column>
                  <!-- 已付款金额 -->
                  <el-table-column
                    align="center"
                    prop="alreadyPaymentAmount"
                    :label="$t('contract_mod.amountPaid')"
                    width="150"
                    show-overflow-tooltip
                  />
                  <!-- 创建人 -->
                  <el-table-column
                    align="center"
                    prop="createdBy"
                    :label="$t('sourcingBuyer.createdFullName')"
                    width="150"
                    show-overflow-tooltip
                  />
                  <!-- 创建时间 -->
                  <el-table-column
                    align="center"
                    prop="creationDate"
                    :label="$t('sourcingBuyer.creationDate')"
                    width="150"
                    show-overflow-tooltip
                  />
                  <!-- 操作 -->
                  <el-table-column :label="$t('common.operation')" width="60" fixed="right">
                    <template slot-scope="scope">
                      <el-button type="text" @click="deleteOneContent(scope.$index, scope.row)">
                        {{ $t('common.delete') }}
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <srm-dialog
                  :title="$t('purSettlementMod.detailSelect')"
                  size="large"
                  :visible.sync="dialogVisible2"
                  :close-on-click-modal="false"
                >
                  <div>
                    <el-form
                      ref="queryForm"
                      :model="queryForm2"
                      label-width="80px"
                      label-position="top"
                      class="form-incontainer"
                    >
                      <srm-row>
                        <!-- 合同名称 -->
                        <srm-col :initCol="3">
                          <el-form-item :label="$t('vendorMod.contractName')">
                            <el-input v-model="queryForm2.contractName" />
                          </el-form-item>
                        </srm-col>
                        <!-- 合同履约开票单号 -->
                        <srm-col :initCol="3">
                          <el-form-item :label="$t('contract_mod.processNum2')">
                            <el-input v-model="queryForm2.invoiceNo" />
                          </el-form-item>
                        </srm-col>
                        <srm-col :initCol="3">
                          <div style="text-align: right;margin-top: 24px;">
                            <el-button type="primary" @click="getListData(queryForm2.contractName, queryForm2.invoiceNo)">
                              {{ $t('common.search') }}
                            </el-button>
                            <el-button type="primary" @click="addContentInfo2">
                              {{ $t('common.confirm') }}
                            </el-button>
                          </div>
                        </srm-col>
                      </srm-row>
                    </el-form>
                    <el-table
                      :data="displayItemTable2"
                      style="width: 100%"
                      border
                      height="250px"
                      highlight-current-row
                      @selection-change="handleItemSelection"
                    >
                      <el-table-column type="selection" width="55" />
                      <el-table-column
                        align="center"
                        type="index"
                        :label="$t('contractMod.tabindex')"
                        width="60"
                      />
                      <!-- 合同名称 -->
                      <el-table-column
                        align="center"
                        prop="contractName"
                        :label="$t('vendorMod.contractName')"
                        width="150"
                        show-overflow-tooltip
                      />
                      <!-- 里程碑名称 -->
                      <el-table-column
                        align="center"
                        prop="milestoneType"
                        :label="$t('contract_mod.processNodeName')"
                        width="150"
                        show-overflow-tooltip
                      >
                        <template slot-scope="scope">
                          {{ $getDictLabel('MILESTONE_SCHEDULE', scope.row.milestoneType) }}
                        </template>
                      </el-table-column>
                      <!-- 付款阶段 -->
                      <el-table-column
                        align="center"
                        prop="paymentStage"
                        :label="$t('bidMod.payStage')"
                        width="150"
                        show-overflow-tooltip
                      >
                        <template slot-scope="scope">
                          {{ $getDictLabel('PAYMENT_STAGE', scope.row.paymentStage) }}
                        </template>
                      </el-table-column>
                      <!-- 合同履约开票单号 -->
                      <el-table-column
                        align="center"
                        prop="invoiceNo"
                        :label="$t('contract_mod.processNum2')"
                        width="150"
                        show-overflow-tooltip
                      />
                      <!-- 供应商名称 -->
                      <el-table-column
                        align="center"
                        prop="vendorName"
                        :label="$t('common.vendorName')"
                        width="150"
                        show-overflow-tooltip
                      />
                      <!-- 供应商编号 -->
                      <el-table-column
                        align="center"
                        prop="vendorCode"
                        :label="$t('supplierRating.supplierCode')"
                        width="150"
                        show-overflow-tooltip
                      />
                      <!-- 组织 -->
                      <el-table-column
                        align="center"
                        prop="invName"
                        :label="$t('vendorMod.organization')"
                        width="150"
                        show-overflow-tooltip
                      />
                      <!-- 物料名称 -->
                      <el-table-column
                        align="center"
                        prop="materialName"
                        :label="$t('vendorMod.materialName')"
                        width="150"
                        show-overflow-tooltip
                      />
                      <!-- 物料编码 -->
                      <el-table-column
                        align="center"
                        prop="materialCode"
                        :label="$t('vendorMod.materialCode')"
                        width="150"
                        show-overflow-tooltip
                      />
                      <!-- 应付含税金额 -->
                      <el-table-column
                        align="center"
                        prop="payableTaxedAmount"
                        :label="$t('contract_mod.payableTax')"
                        width="150"
                        show-overflow-tooltip
                      />
                      <!-- 开票含税金额 -->
                      <el-table-column
                        align="center"
                        prop="invoicedTaxedAmount"
                        :label="$t('contract_mod.payableTax2')"
                        width="150"
                        show-overflow-tooltip
                      />
                      <!-- 代付款金额 -->
                      <el-table-column
                        align="center"
                        prop="stayPaymentAmount"
                        :label="$t('contract_mod.paymentAmount')"
                        width="150"
                        show-overflow-tooltip
                      />
                      <!-- 已付款金额 -->
                      <el-table-column
                        align="center"
                        prop="alreadyPaymentAmount"
                        :label="$t('contract_mod.amountPaid')"
                        width="150"
                        show-overflow-tooltip
                      />
                      <!-- 创建人 -->
                      <el-table-column
                        align="center"
                        prop="createdBy"
                        :label="$t('sourcingBuyer.createdFullName')"
                        width="150"
                        show-overflow-tooltip
                      />
                      <!-- 创建时间 -->
                      <el-table-column
                        align="center"
                        prop="creationDate"
                        :label="$t('sourcingBuyer.creationDate')"
                        width="150"
                        show-overflow-tooltip
                      />
                    </el-table>
                    <srm-row type="flex">
                      <srm-col>
                        <CPagination
                          ref="queryPagination"
                          style="margin:5px"
                          class="c-query-table-pagination"
                          :total="pageInfo.total"
                          :page-num="pageInfo.pageNum"
                          :page-size="pageInfo.pageSize"
                          @current-change="changeCurrentIndex"
                          @size-change="changeCurrentSize"
                        />
                      </srm-col>
                    </srm-row>
                  </div>
                </srm-dialog>
              </el-collapse-item>
              <!-- 附件 -->
              <el-collapse-item :title="$t('purSettlementMod.addUploadFile')" name="4">
                <p class="btn_line">
                  <el-button
                    :disabled="isReadOnly || curOpt === 'approvalOnly'"
                    type="primary"
                    class="detail-pbtn"
                    @click="addFileuploads"
                  >
                    {{ $t('common.add') }}
                  </el-button>
                </p>
                <upload-attach :readonly="isReadOnly || curOpt === 'approvalOnly'" :attachData="paymentApplyAttaches" :fileInfo="fileInfo" />
              </el-collapse-item>
            </el-collapse>
          </el-form>
        </div>

        <!-- ctoolbar按钮新增 -->
        <template v-slot:buttonTwo>
          <el-button
            v-if="
              paymentApply.status === 'SUBMITTED' &&
                curOpt !== 'view' &&
                workflowParamsInfo.integrationMode === 'None'
            "
            @click="rejectForm"
          >
            {{ $t('common.toRefuse') }}
          </el-button>
        </template>
      </CWorkflowMulti>

      <!-- 新增开票单 -->
      <srm-dialog
        :title="$t('purSettlementMod.invoice')"
        :visible.sync="isInvoiceDialog"
        :modal-append-to-body="false"
        size="large"
        :destroy-on-close="true"
        @opened="openAfter"
      >
        <p class="btn_line">
          <el-button type="primary" class="detail-pbtn" @click="confirmAddInvoice">
            {{ $t('common.confirm') }}
          </el-button>
          <el-button class="detail-pbtn" @click="isInvoiceDialog = false">
            {{ $t('common.cancel') }}
          </el-button>
        </p>
        <TableView
          ref="invoiceInfoRef"
          style="height: 300px;"
          :checkbox="true"
          :table-header="invoiceInfoHeader"
          :check-change="handleInvoiceChange"
          :pageEnabled="true"
          :pre-query-data="queryParam"
          url="/api-sup-ce/payment/paymentApplyDetail/searchOnlineInvoice"
          :open-custom-table="true"
          @row-dblclick="rowDblclick"
        >
          <template #payMethod="props">
            {{ $getDictLabel('PAYMENT_MODE', props.scope.row.payMethod) }}
          </template>
          <template #payAccountPeriodCode="props">
            {{ $getDictLabel('PAYMENT_PERIOD', props.scope.row.payAccountPeriodCode) }}
          </template>
        </TableView>
      </srm-dialog>

      <!-- 驳回原因 -->
      <srm-dialog
        :title="$t('purSettlementMod.reasonForRejection')"
        :visible.sync="isRejectResult"
        :modal-append-to-body="false"
        size="middle"
        style="text-align: center"
      >
        <!-- 请填写驳回原因 -->
        <el-input
          v-model="rejectReason"
          type="textarea"
          :rows="4"
          :placeholder="$t('purSettlementMod.pleaseFillReasonForRejection')"
        />
        <div class="topComment">
          <el-button @click="isRejectResult = false">
            {{ $t('common.cancel') }}
          </el-button>
          <el-button type="primary" @click="confirmReject">
            {{ $t('common.confirm') }}
          </el-button>
        </div>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import uploadAttach from '@/library/composition/orderManagementBuyer/upload-attach'
import WorkflowCommon from '@/library/mixins/workflow-common'
import { tabTodoMixin } from '@/utils/mixins'
import QuickSearch from 'lib@/components/QuickSearch'
import CPagination from 'lib@/components/c-pagination'
import OrganizationSelector from 'lib@/components/organization-selector'
import formDetail from '@/library/composition/orderManagementBuyer/form-detail'
import TableView from 'lib@/components/Table/TableView'
import { parseTime } from '@/utils'
import { advancePaymentApi } from 'modb@//purSettlement/api'

export default {
  name: 'PaymentPlanDetail',
  components: {
    QuickSearch,
    CPagination,
    OrganizationSelector,
    formDetail,
    TableView,
    uploadAttach
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      showType: '',
      perAdvanceApplyDetails: [],
      displayItemTable2: [],
      multipleSelection: [],
      dialogVisible2: false,
      queryForm2: {
        contractName: '',
        invoiceNo: ''
      },
      pageInfo: {
        total: 0,
        pageNum: 1,
        pageSize: 15
      },
      rejectReason: '',
      isRejectResult: false,
      invoiceSign: false,
      invoiceSelect: [],
      isInvoiceDialog: false,
      curOpt: 'add',
      pageSize: 15,
      queryParam: {},
      paymentApplyAttaches: [],
      fileInfo: {
        uploadType: 'DEF',
        sourceType: 'WEB_APP',
        fileModular: 'sup',
        fileFunction: 'vendorBiddingManagement',
        fileType: 'images'
      },
      paymentApplyLineList: [], // 申请明细
      displayPaymentPlanLines: [],
      activeDims: ['1', '2', '3', '4'],
      paymentApply: {
        perAdvanceApplyDetails: [],
        billType: 'ORDER',
        orgId: '',
        orgCode: '',
        orgName: '',
        organizationId: '',
        organizationCode: '',
        organizationName: '',
        vendorId: '',
        vendorName: '',
        vendorCode: '',
        currencyId: '',
        currencyName: '',
        currencyCode: '',
        taxRate: '',
        taxKey: '',
        payMethod: '',
        paymentApplyNumber: '',
        actualInvoiceAmountY: undefined,
        includeTaxAmount: undefined,
        excludeTaxAmount: undefined,
        totalTax: undefined,
        status: '',
        bankName: '',
        openingBank: '',
        bankAccountName: '',
        bankAccount: '',
        createdUserName: '',
        creationDate: '',
        invoiceInforData: []
      },
      paymentApplyHead: [
        {
          prop: 'orgId',
          label: () => this.$t('oneStopShopping.businessEntity'), // 业务实体
          type: 'OUorganizationSelector',
          callback: node => this.orgIdHandle(node)
        },
        {
          prop: 'organizationId',
          parentId: 'orgId',
          label: () => this.$t('purchaseDemand.invOrg'), // 库存组织
          type: 'INVorganizationSelector',
          callback: node => this.organizationIdHandle(node)
        },
        {
          prop: 'vendorName',
          label: () => this.$t('common.vendor'), // 供应商
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_all',
          callback: node => this.setVendor(node)
        },
        {
          prop: 'currencyName',
          label: () => this.$t('quota.currency'), // 币种
          type: 'quicksearch',
          showKey: 'currencyName',
          name: 'scc_base_purchase_currency_info',
          callback: node => this.setCurrency(node)
        },
        {
          prop: 'taxKey',
          label: this.$t('purchaseDemand.taxRate'), // 税率
          type: 'dict',
          code: 'tax',
          callback: (value, dictItem) => this.setTax(value, dictItem)
        },
        {
          prop: 'payMethod',
          label: this.$t('paymentType.paymentWay'), // 付款方式
          type: 'dict',
          code: 'PAYMENT_MODE'
        },
        {
          prop: 'paymentApplyNumber',
          label: () => this.$t('contractMod.paymentApplyNumber'), // 付款申请单号
          disabled: true
        },
        {
          prop: 'actualInvoiceAmountY',
          label: this.$t('purSettlementMod.actualInvoiceAmountY2'), // 发票含税总金额
          disabled: true,
          type: 'precision',
          toFixed: 8,
          controls: false
        },
        {
          prop: 'includeTaxAmount',
          label: this.$t('purSettlementMod.includeTaxAmount3'), // 付款含税总金额
          disabled: true,
          type: 'precision',
          toFixed: 8,
          controls: false
        },
        {
          prop: 'excludeTaxAmount',
          label: this.$t('purSettlementMod.excludeTaxAmount'), // 付款未税总金额
          disabled: true,
          type: 'precision',
          toFixed: 8,
          controls: false
        },
        {
          prop: 'totalTax',
          label: this.$t('purSettlementMod.totalTax2'), // 付款总税额
          disabled: true,
          type: 'precision',
          toFixed: 8,
          controls: false
        },
        {
          prop: 'status',
          label: this.$t('purSettlementMod.paymentPlanStatus'), // 单据状态
          type: 'dict',
          code: 'PAYMENT_APPLY_STATUS',
          disabled: true
        },
        {
          prop: 'bankName',
          label: () => this.$t('components.bank.bankName'), // 银行名称
          disabled: true
        },
        {
          prop: 'openingBank',
          label: () => this.$t('components.bank.branchBankName'), // 开户行名称
          disabled: true
        },
        {
          prop: 'bankAccountName',
          label: () => this.$t('components.bank.accountName'), // 账户名称
          disabled: true
        },
        {
          prop: 'bankAccount',
          label: () => this.$t('components.bank.bankAccount'), // 银行账号
          disabled: true
        },
        {
          prop: 'createdUserName',
          label: () => this.$t('common.creator'), // 创建人
          disabled: true
        },
        {
          prop: 'creationDate',
          label: () => this.$t('quota.createdDate'), // 创建日期
          type: 'date',
          disabled: true
        },
        {
          prop: 'billType',
          label: this.$t('单据来源'), // 单据来源
          type: 'dict',
          code: 'BILLTYPE',
          callback: (value, dictItem) => {
            this.paymentApply.invoiceInforData = []
            this.paymentApply.perAdvanceApplyDetails = []
          }
        }
      ],
      invoiceInfoHeader: [],
      invoiceHeader: [],
      rules: {
        orgId: [{ required: true, message: this.$t('purchaseDemand.orgIdTips') }], // 请选择业务实体
        organizationId: [{ required: true, message: this.$t('purchaseDemand.organizationIdTips') }], // 请选择库存组织
        vendorName: [{ required: true, message: this.$t('vendorMod.msgVendor') }],
        currencyName: [{ required: true, message: this.$t('vendorMod.msgCurrencyCode') }], // 请选择币种
        taxKey: [{ required: true, message: this.$t('bidMod.msgSelTaxRate') }], // 请选择税率
        payMethod: [{ required: true, message: this.$t('purSettlementMod.paymentMethodTips') }] // 请选择付款方式
      },
      formLabelWidth: '120px',
      isReject: false
    }
  },
  computed: {
    isReadOnly () {
      let statusSign =
        this.paymentApply.status &&
        !['DRAFT', 'REJECTED', 'WITHDRAW'].includes(this.paymentApply.status)
      return statusSign || this.curOpt === 'readOnly'
    },
    // 用来控制保存、提交按钮是否可见。如果自定义按钮则无需添加
    viewUpdateButton () {
      return !this.isReadOnly && this.paymentApply.status !== 'APPROVED'
    },
    disabledUpdateButton () {
      return this.paymentApply.status === 'SUBMITTED' || this.paymentApply.status === 'APPROVING'
    },
    // 用来指定工作流的业务ID
    workflowBusinessId () {
      return this.paymentApply ? this.paymentApply.paymentApplyId : null
    },
    // 不是审批按钮进来或者是送单号进来则禁用流程tab
    workflowTabDisabled () {
      return this.curOpt !== 'approvalOnly' || this.$attrs.params.showType === 'approveNumber'
    }
  },
  watch: {
    // 监听保存提交 按钮变更状态，如果自定义按钮则无需添加
    viewUpdateButton () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
    }
  },
  created () {
    this.curOpt = this.$attrs.params.flag
    this.initButtonsInfor()
    this.invoiceHeader = [
      {
        prop: 'orgName',
        label: this.$t('oneStopShopping.businessEntity'), // 业务实体
        type: 'OUorganizationSelector',
        width: 120
      },
      {
        prop: 'organizationName',
        parentId: 'orgId',
        label: this.$t('purchaseDemand.invOrg'), // 库存组织
        type: 'INVorganizationSelector',
        width: 120
      },
      {
        prop: 'onlineInvoiceNum',
        label: this.$t('purSettlementMod.billingNumber'), // 开票单号
        width: 120
      },
      {
        prop: 'vendorCode',
        label: this.$t('common.vendorCode'), // 供应商编码
        width: 120
      },
      {
        prop: 'vendorName',
        label: this.$t('common.vendor'), // 供应商
        minWidth: 120
      },
      {
        prop: 'actualInvoiceAmountY',
        label: this.$t('purSettlementMod.actualInvoiceAmountY3'), // 发票含税金额
        minWidth: 120,
        formattor: val => val.toFixed(8)
      },
      {
        prop: 'unPaidAmount',
        label: this.$t('purSettlementMod.unPaidAmount'), // 未付款金额
        minWidth: 120,
        formattor: val => val.toFixed(8)
      },
      {
        prop: 'payingAmount', // *本次付款金额
        label: this.$t('purSettlementMod.payingAmount'),
        minWidth: 150,
        showType: 'slot',
        slot: 'payingAmount',
        addStarToColumn: true
      },
      {
        prop: 'payMethod',
        label: this.$t('paymentType.paymentWay'), // 付款方式
        minWidth: 110,
        // dataType: 'dict',
        // code: 'PAYMENT_MODE'
        showType: 'slot',
        slot: 'payMethod'
      },
      {
        prop: 'payAccountPeriodCode',
        label: this.$t('paymentType.paymentDay1'), // 付款账期
        showType: 'slot',
        slot: 'payAccountPeriodCode',
        width: 100
      },
      {
        prop: 'currencyName',
        label: this.$t('quota.currency'), // 币种
        width: 100
      },
      {
        prop: 'taxRate',
        label: this.$t('purchaseDemand.taxRate'), // 税率
        width: 100
      },
      {
        prop: 'appliedBy',
        label: this.$t('common.creator'), // 创建人
        width: 100
      },
      {
        prop: 'appliedDate',
        label: this.$t('quota.createdDate'), // 创建日期
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'operation',
        label: this.$t('common.operation'), // 操作
        width: 80,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: row => this.deleteRow(row),
            formattor: () => this.$t('common.delete')
          }
        ]
      }
    ]
    if (this.curOpt === 'playPlan') {
      const { nickname, ceeaDeptId, department } = this.$store.getters.userInfo
      this.paymentApply.createdFullName = nickname
      this.paymentApply.departmentId = ceeaDeptId
      this.paymentApply.departmentName = department
      const head = this.$attrs.params.head
      this.$set(this.paymentApply, 'vendorCode', head.vendorCode)
      this.$set(this.paymentApply, 'vendorId', head.vendorId)
      this.$set(this.paymentApply, 'vendorName', head.vendorName)
      this.$set(this.paymentApply, 'orgId', head.buId)
      this.$set(this.paymentApply, 'orgCode', head.buCode)
      this.$set(this.paymentApply, 'orgName', head.buName)
      this.$set(this.paymentApply, 'currencyId', head.currencyId)
      this.$set(this.paymentApply, 'currencyName', head.currencyName)
      this.$set(this.paymentApply, 'currencyCode', head.currencyCode)
      this.$set(this.paymentApply, 'billType', 'CONTRACT')
      return false
    }
    if (this.curOpt !== 'add') {
      // 编辑 查看
      this.getOrderFormDetail(this.$attrs.params.paymentApplyId) // 查询
    }
  },
  methods: {
    deleteOneContent (indexs, row) {
      this.paymentApply.perAdvanceApplyDetails.splice(indexs, 1)
    },
    // 改变 currentNum
    changeCurrentIndex (currentNum) {
      this.pageInfo.pageNum = currentNum
      this.getListData()
    },
    // 改变 currentSize
    changeCurrentSize (currentSize) {
      this.pageInfo.pageSize = currentSize
      this.getListData()
    },
    // 合同履约打开新增
    openOneContent2 () {
      const checkObj = {
        orgId: this.paymentApply.orgId,
        organizationId: this.paymentApply.organizationId,
        vendorId: this.paymentApply.vendorId
      }
      for (let key in checkObj) {
        if (!checkObj[key]) {
          return this.$message.warning(this.$t('purSettlementMod.openOneContentWarning2'))
        }
      }
      this.dialogVisible2 = true
      this.getListData()
    },
    getListData (contractName, invoiceNo) {
      const obj = {
        vendorId: this.paymentApply.vendorId,
        vendorCode: this.paymentApply.vendorCode,
        vendorName: this.paymentApply.vendorName,
        invId: this.paymentApply.organizationId,
        buId: this.paymentApply.orgId,
        contractName: contractName || '',
        invoiceNo: invoiceNo || '',
        querySource: 'payment'
      }
      advancePaymentApi.prepaymentDetails(obj).then(data => {
        this.displayItemTable2 = data.data.list
        this.pageInfo.total = data.data.total
      })
    },
    // 合同履约行添加明细
    addContentInfo2 () {
      const perInvoiceDetailId = this.paymentApply.perAdvanceApplyDetails.map(item => item.perInvoiceDetailId)
      this.multipleSelection.forEach(row => {
        if (!perInvoiceDetailId.includes(row.perInvoiceDetailId)) {
          this.paymentApply.perAdvanceApplyDetails.push(row)
        }
      })
      this.dialogVisible2 = false
    },
    // 明细选择
    handleItemSelection (selects) {
      this.multipleSelection = selects
    },
    invoiceValidator (rule, val, callback) {
      const index = Number(rule.field.split('.')[1]) // 获取行号
      const sign = this.paymentApply.invoiceInforData.some((row, i) => {
        if (index === i) {
          !row.payingAmount && (row.payingAmount = 0)
          return row.payingAmount > row.unPaidAmount
        }
      })
      // 本次付款金额不可大于未付款金额！
      sign && callback(new Error(this.$t('purPaymentApply.prompt12')))
      callback()
    },
    // 驳回
    rejectForm () {
      this.isRejectResult = true
    },
    confirmReject () {
      this.$http({
        url: '/api-sup-ce/payment/paymentApply/rejectPaymentApply',
        method: 'GET',
        params: {
          id: this.paymentApply.paymentApplyId,
          rejectReason: this.rejectReason
        },
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.isRejectResult = false
        this.backTo()
      })
    },
    // 按钮控制
    initButtonsInfor () {
      this.buttonConfigInfo.save.view =
        this.viewUpdateButton && ['add', 'edit', 'playPlan'].includes(this.curOpt)
      this.buttonConfigInfo.submit.view =
        ['add', 'edit', 'playPlan'].includes(this.curOpt) || this.showType === 'viewApproval'
      this.buttonConfigInfo.cancel.view = !this.isReadOnly
      this.buttonConfigInfo.close.view = this.isReadOnly
    },
    // form验证返回promise校验返回trun or false
    formValidate (formRef) {
      return new Promise(resolve => {
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
      const formFiled = await this.formValidate('formRef')

      if (!formFiled.flag && Object.keys(formFiled.obj).length > 0) {
        const warnObj = Object.keys(formFiled.obj)[0]
        return {
          flag: formFiled.flag,
          message: formFiled.obj[warnObj][0].message
        }
      }
      return { flag: true }
    },
    // 暂存/提交
    async saveOrSubmitData (type) {
      if (type === 'SUBMIT') {
        const { flag, message } = await this.getCheckForm()
        if (flag) {
          this.submitHandle(type)
        } else {
          this.__focus_error__(message)
        }
      } else {
        this.saveHandle(type)
      }
    },
    // 暂存
    async saveHandle (type) {
      let fileArr = this.paymentApplyAttaches.filter(item => !!item.fileuploadId)
      const { code, data } = await this.$http({
        url: '/api-sup-ce/payment/paymentApply/addOrUpdatePaymentApply',
        method: 'POST',
        data: {
          paymentApply: this.paymentApply,
          paymentApplyDetails: this.paymentApply.invoiceInforData,
          paymentApplyAttaches: fileArr,
          perAdvanceApplyDetails: this.paymentApply.perAdvanceApplyDetails
        },
        loading: true
      })

      if (code === '0') {
        await this.getOrderFormDetail(data)
        type === 'SAVE' && this.$message.success(this.$t('common.successSave'))
      }
    },
    // 提交
    async submitHandle (type) {
      // 明细行可以修改的情况下要先掉暂存接口，获取最新数据，避免其他人操作单据导致数量对不上
      ['add', 'edit'].includes(this.curOpt) && await this.saveHandle(type)
      let fileArr = this.paymentApplyAttaches.filter(item => !!item.fileuploadId)
      this.$http({
        url: '/api-sup-ce/payment/paymentApply/submitPaymentApply',
        method: 'POST',
        data: {
          paymentApply: this.paymentApply,
          paymentApplyDetails: this.paymentApply.invoiceInforData,
          paymentApplyAttaches: fileArr,
          perAdvanceApplyDetails: this.paymentApply.perAdvanceApplyDetails
        },
        loading: true
      }).then(async res => {
        if (res.code === '0') {
          // if (!['None', 'Push'].includes(this.workflowParamsInfo.integrationMode)) {
          //   await this.handlerAfter(type)
          // }
          this.backTo()
          this.$message.success(this.$t('common.successSubmit'))
        }
      })
    },
    rowDblclick (select) {
      this.invoiceSelect = [select]
      this.confirmAddInvoice()
    },
    // 开票单新增 - select
    handleInvoiceChange (select) {
      this.invoiceSelect = select
    },
    // 新增开票单
    confirmAddInvoice () {
      if (this.invoiceSelect.length === 0) {
        return this.$message.warning(this.$t('common.msgSelectData'))
      }

      const ids = this.paymentApply.invoiceInforData.map(item => item.onlineInvoiceId)

      this.invoiceSelect.forEach(item => {
        if (!ids.includes(item.onlineInvoiceId)) {
          this.paymentApply.invoiceInforData.push(item)
        }
      })

      this.setHeaderConfig(this.paymentApply.invoiceInforData)
      this.setAmountCal()

      this.isInvoiceDialog = false
    },
    setAmountCal () {
      let tableData = this.paymentApply.invoiceInforData
      if (tableData.length > 0) {
        // 发票含税总金额 = 开票单明细发票含税金额之和
        this.paymentApply.actualInvoiceAmountY = tableData
          .map(row => row.actualInvoiceAmountY)
          .reduce((p, c) => (Number(p) || 0) + (Number(c) || 0))

        // 付款含税总金额：开票单明细行的本次付款金额之和
        this.paymentApply.includeTaxAmount = tableData
          .map(row => row.payingAmount)
          .reduce((p, c) => (Number(p) || 0) + (Number(c) || 0))
      } else {
        this.paymentApply.actualInvoiceAmountY = 0
        this.paymentApply.includeTaxAmount = 0
      }

      if (this.paymentApply.includeTaxAmount) {
        // 付款未税总金额=付款含税总金额/(1+税率)
        this.paymentApply.excludeTaxAmount =
          this.paymentApply.includeTaxAmount / (1 + this.paymentApply.taxRate / 100)
      } else {
        this.paymentApply.excludeTaxAmount = 0
      }

      if (this.paymentApply.actualInvoiceAmountY) {
        // 付款总税额=发票含税总金额*税率
        this.paymentApply.totalTax =
          this.paymentApply.actualInvoiceAmountY * (this.paymentApply.taxRate / 100)
      } else {
        this.paymentApply.totalTax = 0
      }
    },
    // 设置明细表高度
    setInvoiceHeight (tableData) {
      this.$nextTick(() => {
        if (this.$refs.invoiceRef) {
          const t = document.querySelector('.invoice-table .table-wrapper')
          const r = document.querySelector('.invoice-table .el-table__row')
          t.style.height = r ? r.offsetHeight * (tableData.length + 1) + 'px' : 60
        }
      })
    },
    // 查询单据详情
    async getOrderFormDetail (paymentApplyId) {
      const { code, data } = await this.$http({
        url: '/api-sup-ce/payment/paymentApply/get',
        method: 'GET',
        params: { id: paymentApplyId },
        loading: true
      })

      if (code !== '0') return

      const { paymentApply, paymentApplyDetails, paymentApplyAttaches, perAdvanceApplyDetails } = data
      this.paymentApplyAttaches = paymentApplyAttaches
      this.paymentApply = paymentApply
      this.paymentApply.perAdvanceApplyDetails = perAdvanceApplyDetails

      this.paymentApply.invoiceInforData = paymentApplyDetails

      this.setHeaderConfig(this.paymentApply.invoiceInforData)
    },
    // 处理表格disabled 如果表格有数据置灰字段
    setHeaderConfig (tableData) {
      if (tableData.length > 0) {
        this.isReject = true
      } else {
        this.isReject = false
      }

      let arrs = ['currencyName', 'orgId', 'organizationId', 'payMethod', 'taxKey', 'vendorName']
      this.paymentApplyHead.forEach(item => {
        if (arrs.includes(item.prop)) {
          this.$set(item, 'disabled', this.isReject)
        }
      })
    },
    // 开票单 -新增
    addInvoiceList () {
      const params = {}
      const conditions = [
        'orgId',
        'orgCode',
        'orgName',
        'organizationId',
        'organizationCode',
        'organizationName',
        'vendorId',
        'vendorName',
        'vendorCode',
        'currencyId',
        'currencyName',
        'currencyCode',
        'taxRate',
        'taxKey',
        'payMethod'
      ]
      Object.keys(this.paymentApply).forEach(key => {
        if (conditions.includes(key) && this.paymentApply[key]) {
          params[key] = this.paymentApply[key]
        }
      })
      // 所需表单参数赋值
      this.queryParam = params

      this.setDialogHeader()
      this.isInvoiceDialog = true
    },
    // 设置开票单表头列
    setDialogHeader () {
      const headerCopy = JSON.parse(JSON.stringify(this.invoiceHeader))
      headerCopy.forEach((item, i) => {
        if (item.prop === 'payingAmount') {
          headerCopy.splice(i, 1)
        }
      })
      headerCopy.pop()
      this.invoiceInfoHeader = headerCopy
    },
    openAfter () {
      this.$nextTick(() => {
        this.$refs.invoiceInfoRef.query()
      })
    },
    // 开票单 -删除
    deleteRow (row) {
      const tableData = this.$refs.invoiceRef.tableData
      let index = tableData.indexOf(row)
      this.$refs.invoiceRef.deleteRow(index)
      this.setHeaderConfig(tableData)
      this.setAmountCal(tableData)
    },
    // 业务实体选择
    orgIdHandle (node) {
      this.paymentApply.orgId = node ? node.organizationId : ''
      this.paymentApply.orgCode = node ? node.organizationCode : ''
      this.paymentApply.orgName = node ? node.organizationName : ''
    },
    // 业务实体选择
    organizationIdHandle (node) {
      this.paymentApply.organizationId = node ? node.organizationId : ''
      this.paymentApply.organizationCode = node ? node.organizationCode : ''
      this.paymentApply.organizationName = node ? node.organizationName : ''
    },
    // 设置供应商
    setVendor (node) {
      this.paymentApply.vendorId = node ? node.companyId : ''
      this.paymentApply.vendorCode = node ? node.companyCode : ''
      this.paymentApply.vendorName = node ? node.companyName : ''

      // 携带银行信息
      this.$http({
        url: '/api-sup/info/bankInfo/getMainAccountByCompanyId',
        method: 'GET',
        params: { companyId: this.paymentApply.vendorId },
        loading: true
      }).then(res => {
        if (res.code === '0') {
          const { bankAccount, bankAccountName, bankName, openingBank } = res.data
          Object.assign(this.paymentApply, {
            bankAccount,
            bankAccountName,
            bankName,
            openingBank
          })
        }
      })
    },
    // 币种选择
    setCurrency (node) {
      this.paymentApply.currencyId = node ? node.currencyId : ''
      this.paymentApply.currencyCode = node ? node.currencyCode : ''
      this.paymentApply.currencyName = node ? node.currencyName : ''
    },
    // 设置税率
    setTax (val, obj) {
      this.paymentApply.taxRate = obj.key
      this.paymentApply.taxKey = val
    },
    backTo () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('paymentPlanList.getQuerydata')
    },
    // 新增附件
    addFileuploads () {
      this.paymentApplyAttaches.push({
        fileuploadId: null,
        attachName: null
      })
    },
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'paymentapply'
    },
    getCWorkflowRefName () {
      // 对应CWorkflowMulti标签中的ref
      return 'workflowMulti'
    }
  }
}
</script>
<style scoped lang="scss">
.the-paymentPlanDetail-detail {
  .form-container2 {
    padding: 5px;
  }
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
  .the_TableView {
    padding: 0;
  }
  .invoice-table {
    max-height: 200px;
  }
  .topComment {
    margin-top: 15px;
    float: right;
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
