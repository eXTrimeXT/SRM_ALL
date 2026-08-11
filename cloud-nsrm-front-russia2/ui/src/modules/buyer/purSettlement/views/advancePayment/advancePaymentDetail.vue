<template>
  <el-container class="flex-container the-advancePaymentDetail-detail" direction="vertical">
    <el-main>
      <CWorkflowMulti
        ref="workflowMulti"
        v-model="activeTabName"
        :fun-params="workflowParamsInfo"
        :button-config-info="buttonConfigInfo"
        @tab-click="workflowView"
        @workflow-handler="workflowHandler"
        @click-handler="type => saveOrSubmitBill(type)"
        @submit-direct="type => saveOrSubmitBill(type)"
        @confirm="(type, comment) => saveOrSubmitBill(type, comment)"
        @close-tab="goback"
      >
        <div class="form-container2">
          <el-form
            ref="advanceApplyHead"
            :disabled="isReadOnly"
            :model="advanceApplyHead"
            label-width="80px"
            label-position="top"
            class="form-incontainer"
            :rules="rules"
          >
            <el-collapse v-model="activeDims" class="tab-form-style">
              <el-collapse-item :title="$t('purSettlementMod.prepayApplyDetails')" name="1">
                <form-detail
                  ref="formWapRef"
                  :form-array="formHead"
                  :form-data="advanceApplyHead"
                />
              </el-collapse-item>
              <!-- 订单预付款明细 -->
              <el-collapse-item v-if="advanceApplyHead.billType == 'ORDER'" :title="$t('accountMod.advancePaymentDetail')" name="2">
                <p class="btn_line">
                  <el-button type="primary" class="detail-pbtn" @click="openOneContent">
                    {{ $t('common.add') }}
                  </el-button>
                </p>
                <el-table
                  :data="advanceApplyHead.advanceApplyLines"
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
                  <!-- 采购订单号 -->
                  <el-table-column
                    align="center"
                    prop="orderNumber"
                    :label="$t('purSettlementMod.orderNumber')"
                    width="150"
                    show-overflow-tooltip
                  />
                  <!-- 订单行号 -->
                  <el-table-column
                    align="center"
                    prop="orderDetailLineNum"
                    :label="$t('orderMod.orderLineNum')"
                    width="80"
                    show-overflow-tooltip
                  />
                  <!-- 物料编码 -->
                  <el-table-column
                    align="center"
                    prop="materialCode"
                    :label="$t('common.materialCode')"
                    width="100"
                    show-overflow-tooltip
                  />
                  <!-- 物料名称 -->
                  <el-table-column
                    align="center"
                    prop="materialName"
                    :label="$t('common.materialName')"
                    min-width="100"
                    show-overflow-tooltip
                  />
                  <!-- 单位 -->
                  <el-table-column
                    align="center"
                    prop="unit"
                    :label="$t('dataConfMod.unit')"
                    width="80"
                    show-overflow-tooltip
                  />
                  <!-- 订单数量 -->
                  <el-table-column
                    align="center"
                    prop="orderNum"
                    :label="$t('orderMod.buyerOrderSynergy.orderNum')"
                    width="90"
                    show-overflow-tooltip
                  />
                  <!-- 未税单价 -->
                  <el-table-column
                    align="center"
                    prop="unitNoTaxPrice"
                    :label="$t('purSettlementMod.unitPriceNoTax')"
                    width="100"
                    show-overflow-tooltip
                  />
                  <!-- 税率 -->
                  <el-table-column
                    align="center"
                    prop="taxRate"
                    :label="$t('bidMod.taxRate')"
                    width="90"
                    show-overflow-tooltip
                  />
                  <!-- 含税金额 -->
                  <el-table-column
                    align="center"
                    prop="amountIncludingTax"
                    :label="$t('contractMod.amount2')"
                    width="100"
                    :formatter="(row, column, val) => val.toFixed(2)"
                    show-overflow-tooltip
                  />
                  <!-- 未申请付款金额 -->
                  <el-table-column
                    align="center"
                    prop="paymentAmountAppliedN"
                    :label="$t('purSettlementMod.paymentAmountAppliedN')"
                    width="100"
                    :formatter="(row, column, val) => val.toFixed(2)"
                    show-overflow-tooltip
                  />
                  <!-- 本次申请付款金额 -->
                  <el-table-column
                    align="center"
                    prop="paymentAmountApply"
                    :label="$t('purSettlementMod.paymentAmountApply')"
                    min-width="100"
                  >
                    <template slot="header">
                      <em class="toRequired">*</em>{{ $t('purSettlementMod.paymentAmountApply') }}
                    </template>
                    <template slot-scope="scope">
                      <el-form-item
                        :prop="'advanceApplyLines.' + scope.$index + '.paymentAmountApply'"
                        :rules="{ required: true, validator: paymentValidator }"
                      >
                        <el-input-number
                          v-model="scope.row.paymentAmountApply"
                          :min="0"
                          :precision="2"
                          :controls="false"
                          class="input-number-precision"
                          @blur="setRateAmount"
                        />
                      </el-form-item>
                    </template>
                  </el-table-column>
                  <!-- 操作 -->
                  <el-table-column :label="$t('common.operation')" width="60" fixed="right">
                    <template slot-scope="scope">
                      <el-button type="text" @click="deleteOneContent(scope.$index, scope.row)">
                        {{ $t('common.delete') }}
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <!-- 预付款明细选择 -->
                <DetailSelectDialog
                  :visible.sync="dialogVisible"
                  :displayItemTable="displayItemTable"
                  :queryForm="queryForm"
                  :pageInfo="pageInfo"
                  @queryContent="queryContent"
                  @addContentInfo="addContentInfo"
                  @close="dialogVisible = false"
                />
              </el-collapse-item>
              <!-- 合同履约预付款明细 -->
              <el-collapse-item v-if="advanceApplyHead.billType == 'CONTRACT'" :title="$t('accountMod.advancePaymentDetail2')" name="3">
                <p class="btn_line">
                  <el-button type="primary" class="detail-pbtn" @click="openOneContent2">
                    {{ $t('common.add') }}
                  </el-button>
                </p>
                <el-table
                  :data="advanceApplyHead.perAdvanceApplyDetails"
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
                  >
                    <template slot-scope="scope">
                      {{ scope.row.stayPaymentAmount }}
                    </template>
                  </el-table-column>
                  <!-- 本次付款金额 -->
                  <el-table-column
                    align="center"
                    prop="currentPaymentAmount"
                    :label="$t('purSettlementMod.payingAmount')"
                    width="150"
                  >
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
                    :formatter="(row, column, cellValue) => $parseTime(cellValue)"
                  />
                  <!-- 操作 -->
                  <el-table-column :label="$t('common.operation')" width="60" fixed="right">
                    <template slot-scope="scope">
                      <el-button type="text" @click="deleteOneContent2(scope.$index, scope.row)">
                        {{ $t('common.delete') }}
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <!-- 合同履约预付款明细 -->
                <ContractDetailSelect
                  :visible.sync="dialogVisible2"
                  :queryForm="queryForm2"
                  :displayItemTable="displayItemTable2"
                  :pageInfo="pageInfo"
                  @getListData="getListData"
                  @addContentInfo="addContentInfo2"
                  @close="dialogVisible2 = false"
                />
              </el-collapse-item>
              <!-- 附件 -->
              <el-collapse-item :title="$t('purSettlementMod.addUploadFile')" name="4">
                <p class="btn_line">
                  <el-button type="primary" class="detail-pbtn" @click="addUploadOne">
                    {{ $t('common.add') }}
                  </el-button>
                </p>
                <upload-attach
                  :readonly="isReadOnly"
                  :attach-data="attachList"
                  :file-info="fileInfo"
                />
              </el-collapse-item>
            </el-collapse>
          </el-form>
        </div>
      </CWorkflowMulti>
    </el-main>
  </el-container>
</template>
<script>
// 工作流
import WorkflowCommon from '@/library/mixins/workflow-common'
import CToolbar from 'lib@/components/c-toolbar'
import { tabTodoMixin } from '@/utils/mixins'
import QuickSearch from 'lib@/components/QuickSearch'
import MainHeader from 'lib@/components/Table/MainHeader'
import CPagination from 'lib@/components/c-pagination'
import OrganizationSelector from 'lib@/components/organization-selector'
import http from '@/utils/axios/http'
import uploadAttach from '@/library/composition/orderManagementBuyer/upload-attach'
import formDetail from '@/library/composition/orderManagementBuyer/form-detail'
import { advancePaymentApi } from 'modb@/purSettlement/api'
import DetailSelectDialog from 'modb@/purSettlement/views/advancePayment/components/detailSelectDialog'
import ContractDetailSelect from 'modb@/purSettlement/views/advancePayment/components/contractDetailSelect'
export default {
  name: 'AdvancePaymentDetail',
  components: {
    MainHeader,
    CToolbar,
    QuickSearch,
    CPagination,
    OrganizationSelector,
    uploadAttach,
    formDetail,
    DetailSelectDialog,
    ContractDetailSelect
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      queryForm2: {
        contractName: '',
        invoiceNo: ''
      },
      pageInfo: {
        total: 0,
        pageNum: 1,
        pageSize: 15
      },
      queryForm: {
        orderNumber: '',
        materialId: '',
        materialCode: '',
        materialName: ''
      },
      dialogVisible2: false,
      multipleSelection: [], // 明细选择
      attachList: [],
      bankRowIndex: 1,
      isReadOnly: this.$attrs.params.flag == 'readOnly',
      // 文件上传配置信息
      fileInfo: {
        uploadType: 'DEF', // 固定参数
        sourceType: 'WEB_APP', // 固定参数
        fileModular: 'sup', // 文件所属模块 -》基础模块
        fileFunction: 'vendorBiddingManagement', // 文件所属功能
        fileType: 'images' // 文件所属类型
      },
      activeDims: ['1', '2', '3', '4'],
      advanceApplyHead: {
        billType: 'ORDER',
        bankAccount: '',
        bankAccountName: '',
        bankName: '',
        openingBank: '',
        orgId: '',
        orgCode: '',
        orgName: '',
        organizationId: '',
        organizationCode: '',
        organizationName: '',
        vendorId: '',
        vendorCode: '',
        vendorName: '',
        createdFullName: '',
        currencyId: '',
        currencyName: '',
        currencyCode: '',
        taxKey: '',
        taxRate: '',
        payMethod: '',
        includeTaxAmount: undefined,
        excludeTaxAmount: undefined,
        totalTax: undefined,
        advanceApplyStatus: '',
        creationDate: '',
        createdUserName: '',
        advanceApplyNumber: '',
        costExplain: '',
        advanceApplyLines: [],
        perAdvanceApplyDetails: [],
        departmentName: '',
        departmentId: ''
      },
      formHead: [
        {
          prop: 'orgId',
          label: () => this.$t('oneStopShopping.businessEntity'), // 业务实体
          type: 'OUorganizationSelector',
          callback: node => this.selectHandler(node)
        },
        {
          prop: 'organizationId',
          parentId: 'orgId',
          label: () => this.$t('purchaseDemand.invOrg'), // 库存组织
          type: 'INVorganizationSelector',
          callback: node => this.selectHandler2(node)
        },
        {
          prop: 'vendorName',
          label: () => this.$t('common.vendor'), // 供应商
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_all',
          callback: node => this.getVendorObj(node)
        },
        {
          prop: 'currencyName',
          label: () => this.$t('quota.currency'), // 币种
          type: 'quicksearch',
          showKey: 'currencyName',
          name: 'scc_base_purchase_currency_info',
          callback: node => this.getCurrencyObj(node)
        },
        {
          prop: 'taxKey',
          label: this.$t('purchaseDemand.taxRate'), // 税率
          type: 'dict',
          code: 'tax',
          callback: (value, dictItem) => this.getRaxRateObj(value, dictItem)
        },
        {
          prop: 'payMethod',
          label: this.$t('paymentType.paymentWay'), // 付款方式
          type: 'dict',
          code: 'PAYMENT_MODE'
        },
        {
          prop: 'includeTaxAmount',
          label: this.$t('contractMod.amount2'), // 含税金额
          disabled: true,
          type: 'precision',
          toFixed: 8,
          controls: false
        },
        {
          prop: 'excludeTaxAmount',
          label: this.$t('contractMod.unAmount'), // 未税金额
          disabled: true,
          type: 'precision',
          toFixed: 8,
          controls: false
        },
        {
          prop: 'totalTax',
          label: this.$t('contractMod.taxQuota'), // 税额
          disabled: true,
          type: 'precision',
          toFixed: 8,
          controls: false
        },
        {
          prop: 'advanceApplyStatus',
          label: this.$t('purSettlementMod.paymentPlanStatus'), // 单据状态
          type: 'dict',
          code: 'ADVANCE_APPLY_STATUS',
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
          prop: 'advanceApplyNumber',
          label: () => this.$t('flowMod.documentNo'), // 单据号
          disabled: true
        },
        {
          prop: 'departmentName',
          label: () => this.$t('purchaseDemand.ceeaDepartment'), // 申请部门
          disabled: true
        },
        {
          prop: 'billType',
          label: this.$t('advancePayment.billType'), // 单据来源
          type: 'dict',
          code: 'BILLTYPE',
          callback: (value, dictItem) => {
            this.advanceApplyHead.perAdvanceApplyDetails = []
            this.advanceApplyHead.advanceApplyLines = []
          }
        },
        {
          prop: 'costExplain',
          label: () => this.$t('purchaseDemand.paymentRequestInstruct'), // 付款申请说明
          colLength: 1,
          attrs: {
            type: 'textarea'
          }
        }
      ],
      dialogVisible: false,
      displayItemTable: [],
      displayItemTable2: [],
      Viewflag: '',
      rules: {
        orgId: [{ required: true, message: this.$t('purchaseDemand.orgIdTips') }], // 请选择业务实体
        organizationId: [{ required: true, message: this.$t('purchaseDemand.organizationIdTips') }], // 请选择库存组织
        vendorName: [{ required: true, message: this.$t('vendorMod.msgVendor') }],
        currencyName: [{ required: true, message: this.$t('vendorMod.msgCurrencyCode') }], // 请选择币种
        taxKey: [{ required: true, message: this.$t('bidMod.msgSelTaxRate') }], // 请选择税率
        payMethod: [{ required: true, message: this.$t('purSettlementMod.paymentMethodTips') }], // 请选择付款方式
        costExplain: [{ required: true, message: this.$t('purSettlementMod.PleaseFillPaymentMethodInstructions') }], // 请填写付款方式说明
        createdFullName: [{ required: true, message: this.$t('purSettlementMod.PleaseSelectApplicant') }], // 请选择申请人
        billType: [{ required: true, message: this.$t('perfMod.enterRequired') }]
        // paymentAmountApply: [{ required: true, validator: this.paymentValidator, trigger: 'blur' }]
      }
    }
  },
  computed: {
    // 用来控制保存、提交按钮是否可见。如果自定义按钮则无需添加
    viewUpdateButton () {
      return (
        (this.Viewflag === 'edit' || this.Viewflag === 'playPlan' || !!this.advanceApplyHead.advanceApplyStatus) &&
        !['approveNumber', 'approvalOnly'].includes(this.$attrs.params.showType)
      )
    },
    submitBtn () {
      // 添加、编辑且不是单号或者审批方式进入可显示提交审批按钮
      return (
        (this.Viewflag === 'edit' || this.Viewflag === 'playPlan' || !!this.advanceApplyHead.advanceApplyStatus) &&
					!['approveNumber', 'approvalOnly'].includes(this.$attrs.params.showType)
      )
    },
    disabledUpdateButton () {
      return (
        this.advanceApplyHead.advanceApplyStatus === 'SUBMITTED' ||
				this.advanceApplyHead.advanceApplyStatus === 'APPROVING'
      )
    },
    // 用来指定工作流的业务ID
    workflowBusinessId () {
      return this.advanceApplyHead ? this.advanceApplyHead.advanceApplyId : null
    },
    // 不是审批按钮进来或者是送单号进来则禁用流程tab
    workflowTabDisabled () {
      return this.$attrs.params.showType !== 'approvalOnly' || this.$attrs.params.showType === 'approveNumber'
    }
  },
  watch: {
    // 监听保存提交 按钮变更状态，如果自定义按钮则无需添加
    viewUpdateButton () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
    },
    submitBtn () {
      this.buttonConfigInfo.submit.view = this.submitBtn
    }
  },
  created () {
    this.pageInit()
    this.initButtonsInfor()
  },
  methods: {
    // 预付款明细行校验：本次申请付款金额不可大于未申请付款金额
    paymentValidator (rule, value, callback) {
      const index = Number(rule.field.split('.')[1]) // 获取行号

      const sign = this.advanceApplyHead.advanceApplyLines.some((row, i) => {
        return index === i && row.paymentAmountApply > row.paymentAmountAppliedN
      })
      // 本次申请付款金额不可大于未申请付款金额！
      sign && callback(new Error(this.$t('purSettlementMod.paymentValidator')))
      callback()
    },
    // 按钮控制
    initButtonsInfor () {
      const isEdit = ['add', 'edit'].includes(this.$attrs.params.flag)
      this.buttonConfigInfo.save.view = isEdit
      this.buttonConfigInfo.submit.view = isEdit
      this.buttonConfigInfo.cancel.view = !this.isReadOnly
      this.buttonConfigInfo.close.view = this.isReadOnly
    },
    pageInit () {
      this.Viewflag = this.$attrs.params.flag
      if (this.Viewflag === 'add') {
        const { nickname, ceeaDeptId, department } = this.$store.getters.userInfo
        this.advanceApplyHead.createdFullName = nickname
        this.advanceApplyHead.departmentId = ceeaDeptId
        this.advanceApplyHead.departmentName = department
      } else if (this.Viewflag === 'playPlan') {
        const { nickname, ceeaDeptId, department } = this.$store.getters.userInfo
        this.advanceApplyHead.createdFullName = nickname
        this.advanceApplyHead.departmentId = ceeaDeptId
        this.advanceApplyHead.departmentName = department
        const head = this.$attrs.params.head
        this.$set(this.advanceApplyHead, 'vendorCode', head.vendorCode)
        this.$set(this.advanceApplyHead, 'vendorId', head.vendorId)
        this.$set(this.advanceApplyHead, 'vendorName', head.vendorName)
        this.$set(this.advanceApplyHead, 'orgId', head.buId)
        this.$set(this.advanceApplyHead, 'orgCode', head.buCode)
        this.$set(this.advanceApplyHead, 'orgName', head.buName)
        this.$set(this.advanceApplyHead, 'currencyId', head.currencyId)
        this.$set(this.advanceApplyHead, 'currencyName', head.currencyName)
        this.$set(this.advanceApplyHead, 'currencyCode', head.currencyCode)
        this.$set(this.advanceApplyHead, 'billType', 'CONTRACT')
      } else {
        this.getFormDetail(this.$attrs.params.row.advanceApplyId)
      }
    },
    setHeaderConfig (tableData) {
      let sign = false
      if (tableData.length > 0) {
        sign = true
      } else {
        sign = false
      }

      let arrs = [
        'orgId',
        'organizationId',
        'currencyName',
        'taxKey',
        'vendorName'
      ]
      this.formHead.forEach(item => {
        if (arrs.includes(item.prop)) {
          this.$set(item, 'disabled', sign)
        }
      })
    },
    /**
		 * 含税总金额 = 明细行本次申请金额的总和 = 1
		 * 未含税金额 = 含税总金额/ (1+税率 ) = 1/1.13 = 0.88
		 * 税额 = 1 - 未含税金额 = 0.12
		 */
    setRateAmount () {
      //  明细行本次申请付款金额
      const payApplys = []
      for (let item of this.advanceApplyHead.advanceApplyLines) {
        if (!item.paymentAmountApply) {
          item.paymentAmountApply = 0
        } else {
          payApplys.push(item.paymentAmountApply)
        }

        // if (item.paymentAmountApply > item.paymentAmountAppliedN) {
        //   // item.paymentAmountApply = item.paymentAmountAppliedN
        //   // 本次申请金额不可大于未申请付款金额
        //   return this.$message.warning(this.$t('purSettlementMod.paymentValidator'))
        // }
      }
      // 含税金额
      if (payApplys.length > 0) {
        const total = payApplys.reduce((pre, next) => {
          return (Number(pre) || 0) + (Number(next) || 0)
        })
        this.$set(this.advanceApplyHead, 'includeTaxAmount', total)
        // 未税金额
        const noRate = total / (1 + Number(this.advanceApplyHead.taxRate) / 100)
        this.$set(this.advanceApplyHead, 'excludeTaxAmount', noRate.toFixed(2))

        // 税额
        let tax = total - noRate
        this.$set(this.advanceApplyHead, 'totalTax', tax.toFixed(2))
      }
    },
    // 明细打开新增
    openOneContent () {
      this.reset()

      const checkObj = {
        orgId: this.advanceApplyHead.orgId,
        organizationId: this.advanceApplyHead.organizationId,
        vendorId: this.advanceApplyHead.vendorId,
        currencyId: this.advanceApplyHead.currencyId,
        taxKey: this.advanceApplyHead.taxKey
      }
      for (let key in checkObj) {
        if (!checkObj[key]) {
          return this.$message.warning(this.$t('purSettlementMod.openOneContentWarning'))
        }
      }
      Object.assign(this.queryForm, checkObj)
      this.dialogVisible = true
      this.queryContent(this.queryForm)
    },
    // 合同履约打开新增
    openOneContent2 () {
      const checkObj = {
        orgId: this.advanceApplyHead.orgId,
        organizationId: this.advanceApplyHead.organizationId,
        vendorId: this.advanceApplyHead.vendorId
      }
      for (let key in checkObj) {
        if (!checkObj[key]) {
          return this.$message.warning(this.$t('purSettlementMod.openOneContentWarning2'))
        }
      }
      this.dialogVisible2 = true
    },
    getListData (queryForm) {
      const obj = {
        vendorId: this.advanceApplyHead.vendorId,
        vendorCode: this.advanceApplyHead.vendorCode,
        vendorName: this.advanceApplyHead.vendorName,
        invId: this.advanceApplyHead.organizationId,
        buId: this.advanceApplyHead.orgId,
        contractName: queryForm.contractName || '',
        invoiceNo: queryForm.invoiceNo || '',
        querySource: 'advance'
      }
      advancePaymentApi.prepaymentDetails(obj).then(data => {
        this.displayItemTable2 = data.data.list
        this.pageInfo.total = data.data.total
      })
    },
    // 币种选择
    getCurrencyObj (scope) {
      this.advanceApplyHead.currencyId = scope ? scope.currencyId : ''
      this.advanceApplyHead.currencyCode = scope ? scope.currencyCode : ''
      this.advanceApplyHead.currencyName = scope ? scope.currencyName : ''
    },
    // 选择税率
    getRaxRateObj (value, dictItem) {
      this.advanceApplyHead.taxRate = dictItem.key // 税率值
      this.advanceApplyHead.taxKey = value // 税率值
    },
    getUserObj (val, scope) {
      scope.createdFullName = val
    },
    // 供应商
    getVendorObj (node) {
      this.advanceApplyHead.vendorId = node ? node.companyId : ''
      this.advanceApplyHead.vendorCode = node ? node.companyCode : ''
      this.advanceApplyHead.vendorName = node ? node.companyName : ''

      this.$http({
        url: '/api-sup/info/bankInfo/getMainAccountByCompanyId',
        method: 'GET',
        params: { companyId: this.advanceApplyHead.vendorId },
        loading: true
      }).then((res) => {
        if (res.code === '0') {
          const { bankAccount, bankAccountName, bankName, openingBank } = res.data
          Object.assign(this.advanceApplyHead, {
            bankAccount,
            bankAccountName,
            bankName,
            openingBank
          })
        }
      })
    },
    // 业务实体
    selectHandler (node) {
      this.advanceApplyHead.orgId = node ? node.organizationId : ''
      this.advanceApplyHead.orgCode = node ? node.organizationCode : ''
      this.advanceApplyHead.orgName = node ? node.organizationName : ''

      if (!this.advanceApplyHead.organizationId) return
      // 清空库存组织
      this.advanceApplyHead.organizationId = null
      this.advanceApplyHead.organizationCode = null
      this.advanceApplyHead.organizationName = null
    },
    // 库存组织
    selectHandler2 (node) {
      this.advanceApplyHead.organizationId = node ? node.organizationId : ''
      this.advanceApplyHead.organizationCode = node ? node.organizationCode : ''
      this.advanceApplyHead.organizationName = node ? node.organizationName : ''
    },
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'ADVANCEPAYMENT'
    },
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    async getFormDetail (advanceApplyId) {
      await this.$http({
        url: '/api-sup-ce/sup/advanceApply/get',
        method: 'GET',
        params: { id: advanceApplyId },
        loading: true
      })
        .then(res => {
          if (res.code === '0') {
            this.advanceApplyHead = res.data.advanceApply
            this.advanceApplyHead.advanceApplyLines = res.data.advanceApplyDetailList
            this.advanceApplyHead.perAdvanceApplyDetails = res.data.perAdvanceApplyDetails
            this.attachList = res.data.advanceApplyAttachList

            this.setHeaderConfig(this.advanceApplyHead.advanceApplyLines)
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    deleteOneContent (index, row) {
      this.advanceApplyHead.advanceApplyLines.splice(index, 1)
      this.setHeaderConfig(this.advanceApplyHead.advanceApplyLines)
      this.setRateAmount()
    },
    deleteOneContent2 (index, row) {
      this.advanceApplyHead.perAdvanceApplyDetails.splice(index, 1)
    },
    // 添加明细
    addContentInfo (multipleSelection) {
      if (multipleSelection.length < 1) {
        return this.$message.warning(this.$t('purSettlementMod.selectAtLeastOnePieceOfData'))
      }
      const orderIds = this.advanceApplyHead.advanceApplyLines.map(item => item.orderDetailId)
      multipleSelection.forEach(row => {
        if (!orderIds.includes(row.orderDetailId)) {
          this.advanceApplyHead.advanceApplyLines.push(row)
        }
      })
      this.setHeaderConfig(this.advanceApplyHead.advanceApplyLines)
      this.dialogVisible = false
      // this.setRateAmount()
    },
    // 合同履约行添加明细
    addContentInfo2 (multipleSelection) {
      if (multipleSelection.length < 1) {
        return this.$message.warning(this.$t('purSettlementMod.selectAtLeastOnePieceOfData'))
      }
      const perInvoiceDetailId = this.advanceApplyHead.perAdvanceApplyDetails.map(item => item.perInvoiceDetailId)
      multipleSelection.forEach(row => {
        if (!perInvoiceDetailId.includes(row.perInvoiceDetailId)) {
          this.advanceApplyHead.perAdvanceApplyDetails.push(row)
        }
      })
      this.dialogVisible2 = false
    },
    // 明细查询
    queryContent (queryForm) {
      this.$http({
        url: '/api-sup-ce/sup/advanceApplyDetail/searchOrderDetail',
        method: 'POST',
        data: queryForm,
        loading: true
      }).then(res => {
        this.displayItemTable = res.data.list
        this.pageInfo.total = res.data.total
        this.dialogVisible = true
      })
    },
    reset () {
      for (let i in this.queryForm) {
        this.queryForm[i] = null
      }
    },
    async saveOrSubmitBill (type) {
      if (type === 'SAVE') {
        this.saveBill(type)
      } else {
        const { flag, message } = await this.getCheckForm()
        if (flag) {
          this.submitBill(type)
        } else {
          this.__focus_error__(message)
        }
      }
    },
    // 关闭调用
    goback () {
      if (this.$attrs.params.flag == 'add') {
        this.$emit('tab-remove', 'advancePaymentDetail')
      } else {
        this.$emit('tab-remove', this.$attrs.params.tabName)
      }
      this.__setTabTodo('advancePaymentList.getQuerydata')
    },
    async saveBill (type) {
      let fileArr = this.attachList.filter(item => !!item.fileuploadId)
      const { code, data } = await http({
        url: '/api-sup-ce/sup/advanceApply/addOrUpdateAdvanceApply',
        method: 'POST',
        data: {
          advanceApply: this.advanceApplyHead,
          advanceApplyDetailList: this.advanceApplyHead.advanceApplyLines,
          advanceApplyAttachList: fileArr,
          perAdvanceApplyDetails: this.advanceApplyHead.perAdvanceApplyDetails
        },
        loading: true
      })

      if (code === '0') {
        await this.getFormDetail(data)
        type === 'SAVE' && this.$message.success(this.$t('common.successSave'))
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
      const formFiled = await this.formValidate('advanceApplyHead')

      if (!formFiled.flag && Object.keys(formFiled.obj).length > 0) {
        const warnObj = Object.keys(formFiled.obj)[0]
        return {
          flag: formFiled.flag,
          message: formFiled.obj[warnObj][0].message
        }
      }

      return { flag: true }
    },
    async submitBill (type) {
      // 明细行可以修改的情况下要先掉暂存接口，获取最新数据，避免其他人操作单据导致数量对不上
      ['add', 'edit'].includes(this.Viewflag) && await this.saveBill(type)
      let fileArr = this.attachList.filter(item => !!item.fileuploadId)
      http({
        url: '/api-sup-ce/sup/advanceApply/submitAdvanceApply',
        method: 'POST',
        data: {
          advanceApply: this.advanceApplyHead,
          advanceApplyDetailList: this.advanceApplyHead.advanceApplyLines,
          advanceApplyAttachList: fileArr,
          perAdvanceApplyDetails: this.advanceApplyHead.perAdvanceApplyDetails
        }
      })
        .then(async data => {
          if (!['None', 'Push'].includes(this.workflowParamsInfo.integrationMode)) {
            await this.handlerAfter('SUBMIT')
          }
          this.goback()
          this.$message.success(this.$t('common.successSubmit'))
        })
        .catch(err => {
          console.log(err)
        })
    },
    addUploadOne () {
      this.attachList.push({
        attachId: '',
        fileuploadId: '',
        attachName: ''
      })
    }
  }
}
</script>
<style scoped lang="scss">
.the-advancePaymentDetail-detail {
	.form-container2 {
		padding: 5px;
	}
	.btn_line {
		margin: 0 0 10px 0;
	}
	.toRequired {
		color: #ff4949;
		padding-right: 2px;
	}
	.input-number-precision {
		width:100%;
		:deep(.el-input__inner) {
			text-align:left;
			padding-left: 8px;
		}
	}
}
</style>
