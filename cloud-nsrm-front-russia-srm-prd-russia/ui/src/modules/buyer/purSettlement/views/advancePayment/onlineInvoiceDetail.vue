<template>
  <el-container
    class="flex-container the-purInvoice-detail"
    direction="vertical"
  >
    <el-main>
      <div class="form-container2">
        <el-form
          ref="invoiceNoticeForm"
          :model="form"
          label-position="top"
          class="form-incontainer"
          :disabled="curOpt !== 'add' && curOpt !== 'edit'"
        >
          <el-collapse
            v-model="activeDims"
            class="tab-form-style"
          >
            <el-collapse-item
              :title="$t('perfMod.documentsInformation')"
              name="1"
            >
              <el-row>
                <el-col :span="6">
                  <el-form-item :label="$t('quota.org')">
                    <OrganizationSelector
                      ref="organizationSelector"
                      v-model="form.orgId"
                      :parent-id="-1"
                      node-type="OU"
                      disabled
                      :placeholder="$t('common.pleaseSelect')"
                      @select="selectHandler"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item :label="$t('common.vendorName')">
                    <el-input
                      v-model="form.vendorName"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item :label="$t('purchaseDemand.vendorSite')">
                    <el-select
                      v-model="form.costTypeCode"
                      disabled
                      @change="setCostTypeObj"
                    >
                      <el-option
                        v-for="item in siteOptions"
                        :key="item.vendorSiteId"
                        :label="item.vendorSiteCode"
                        :value="item.vendorSiteId"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('purSettlementMod.onlineInvoiceNum')"
                  >
                    <el-input
                      v-model="form.onlineInvoiceNum"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('purSettlementMod.taxationInvoiceNum')"
                  >
                    <el-input
                      v-model="form.taxInvoiceNum"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item :label="$t('quota.currency')">
                    <el-input
                      v-model="form.currencyName"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item :label="$t('bid_mod.priceTax')">
                    <el-input
                      v-model="form.exchangeRate"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item :label="$t('contractMod.invocieDate')">
                    <el-date-picker
                      v-model="form.invoiceDate"
                      type="date"
                      format="yyyy-MM-dd"
                      disabled
                      value-format="yyyy-MM-dd"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('purSettlementMod.actualInvoiceAmount')"
                  >
                    <el-input
                      v-model="form.actualInvoiceAmountY"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('purSettlementMod.invoiceTaxAmount')"
                    prop="invoiceTax"
                  >
                    <el-input
                      v-model="form.invoiceTax"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('purSettlementMod.invoiceNetAmount')"
                  >
                    <el-input
                      v-model="form.actualInvoiceAmountN"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('purSettlementMod.includeTaxAmountSys')"
                  >
                    <el-input
                      v-model="form.taxTotalAmount"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item :label="$t('purSettlementMod.TaxSys')">
                    <el-input
                      v-model="form.totalTax"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item :label="$t('paymentType.paymentDay')">
                    <DictSelect
                      v-model="form.payAccountPeriodCode"
                      code="PAYMENT_PERIOD"
                      filterable
                      @change="setPeriodObj"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item :label="$t('paymentType.paymentWay')">
                    <DictSelect
                      v-model="form.payMethod"
                      code="PAYMENT_MODE"
                      filterable
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item :label="$t('paymentType.paymentDateDue')">
                    <el-date-picker
                      v-model="form.accountPayableDealine"
                      type="date"
                      format="yyyy-MM-dd"
                      disabled
                      value-format="yyyy-MM-dd"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item :label="$t('bidMod.businessType')">
                    <DictSelect
                      v-model="form.businessType"
                      code="BUSINESS_TYPE"
                      filterable
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item :label="$t('quota.ifPaperAttach')">
                    <DictSelect
                      v-model="form.ifPaperAttach"
                      code="YES_OR_NO"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item :label="$t('quota.paperNum')">
                    <el-input
                      v-model="form.bpcount"
                      v-input-format="{ type: 'number' }"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item :label="$t('flowMod.approvers')">
                    <QuickSearch
                      :show-input="form.approverNickname"
                      show-key="nickname"
                      :scope-data="form"
                      name="scc_rbac_user_display"
                      @close-quicksearch="getUserObj"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item :label="$t('flowMod.approvDept')">
                    <DictSelect
                      v-model="form.approverDept"
                      :code="form.orgId"
                      custom-select-type="OU_DEPT"
                      @change-value="changeDeptHandle"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item :label="$t('contractMod.contractNo')">
                    <el-input
                      v-model="form.contractCode"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item :label="$t('purSettlementMod.invoiceStatus')">
                    <DictSelect
                      v-model="form.invoiceStatus"
                      code="INVOICE_STATUS"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="24">
                  <el-form-item :label="$t('purSettlementMod.abstract')">
                    <el-input
                      v-model="form.comment"
                      type="textarea"
                      :rows="2"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="24">
                  <el-form-item :label="$t('vendorMod.loggerCommentSubmit')">
                    <el-input
                      v-model="form.drafterView"
                      type="textarea"
                      :rows="2"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-collapse-item>
            <el-collapse-item
              v-if="curRole == 'BUYER'"
              :title="$t('purSettlementMod.prepayApplyDetails')"
              name="2"
            >
              <el-table
                :data="onlineInvoiceAdvances"
                style="width: 100%"
                border
                max-height="251px"
              >
                <el-table-column
                  align="center"
                  type="index"
                  width="50"
                  :label="$t('common.sort')"
                />
                <el-table-column
                  align="center"
                  prop="advanceApplyNum"
                  :label="$t('purSettlementMod.advanceApplyNum')"
                  width="120"
                  show-overflow-tooltip
                />
                <el-table-column
                  align="center"
                  prop="applyDate"
                  :label="$t('qualitySynergy.orderDate')"
                  width="120"
                  show-overflow-tooltip
                />
                <el-table-column
                  align="center"
                  prop="contractNum"
                  :label="$t('purchaseDemand.contractNum')"
                  min-width="150"
                  show-overflow-tooltip
                />
                <el-table-column
                  align="center"
                  prop="projectNum"
                  :label="$t('purchaseDemand.projectId')"
                  width="120"
                  show-overflow-tooltip
                />
                <el-table-column
                  align="center"
                  prop="projectName"
                  :label="$t('purchaseDemand.projectName')"
                  min-width="150"
                  show-overflow-tooltip
                />
                <el-table-column
                  align="center"
                  prop="businessType"
                  :label="$t('dataConfMod.businessType')"
                  width="100"
                  show-overflow-tooltip
                />
                <el-table-column
                  align="center"
                  prop="currencyName"
                  :label="$t('purchaseDemand.currency')"
                  width="100"
                  show-overflow-tooltip
                />
                <el-table-column
                  align="center"
                  prop="hangAccountAmount"
                  :label="$t('purSettlementMod.hangAccountAmount')"
                  width="100"
                  show-overflow-tooltip
                />
                <el-table-column
                  align="center"
                  prop="usableAmount"
                  :label="$t('purSettlementMod.usableAmount')"
                  width="100"
                  show-overflow-tooltip
                />
                <el-table-column
                  align="center"
                  prop="chargeOffAmount"
                  :label="$t('purSettlementMod.chargeOffAmount')"
                  width="100"
                  show-overflow-tooltip
                />
              </el-table>
              <!-- 弹框区域-->
            </el-collapse-item>
            <el-collapse-item
              :title="$t('quota.fileInfo')"
              name="3"
            >
              <p style="margin:0 0 10px 0">
                <el-button
                  type="primary"
                  class="detail-pbtn"
                  @click="addFileuploads"
                >
                  {{ $t("common.add") }}
                </el-button>
              </p>
              <el-table
                :data="fileuploads"
                style="width: 100%"
                border
                max-height="251px"
              >
                <el-table-column
                  align="center"
                  prop="fileSourceName"
                  :label="$t('quota.fileupload')"
                  width="250"
                >
                  <template slot-scope="scope">
                    <SrmCommonFile
                      :extra-data="fileInfo"
                      :default-file="{
                        fileId: scope.row.fileUploadId,
                        fileName: scope.row.fileSourceName
                      }"
                      :readonly="!(curOpt === 'add' || curOpt === 'edit')"
                      @on-change="({file}) => outerHandleUploadSuccess(file,scope.row)"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="createdUserName"
                  :label="$t('quota.uploadBy')"
                  width="150"
                />
                <el-table-column
                  align="center"
                  prop="creationDate"
                  :label="$t('quota.uploadDate')"
                  width="150"
                />
                <el-table-column
                  align="center"
                  prop="comment"
                  :label="$t('common.remark')"
                  min-width="200"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.comment" />
                  </template>
                </el-table-column>
                <el-table-column
                  :label="$t('common.operation')"
                  width="60"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="delInvoiceTaxControls(scope.$index, scope.row)"
                    >
                      {{ $t("common.delete") }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </div>
      <CToolbar>
        <template slot="right">
          <el-button @click="backTo()">
            {{ $t("common.cancel") }}
          </el-button>
          <el-button
            v-if="curOpt !== 'view'"
            type="primary"
            @click="saveHandle"
          >
            {{ $t("common.staging") }}
          </el-button>
          <el-button
            v-if="curOpt !== 'view'"
            type="primary"
            @click="submitHandle"
          >
            {{ $t("common.submit") }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch'
import MainHeader from 'lib@/components/Table/MainHeader'
import { adaptDictData, parseTime } from '@/utils'
import OrganizationSelector from 'lib@/components/organization-selector'
import CPagination from 'lib@/components/c-pagination'

import Big from 'big.js'

export default {
  name: 'OnlineInvoiceDetail',
  components: {
    MainHeader,
    QuickSearch,
    CToolbar,
    OrganizationSelector,
    CPagination
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      fileInfo: {
        fileModular: 'pur',
        fileFunction: 'purInvoice',
        fileType: 'images'
      },
      rolePermissions: '', // 操作角色 Buyer 采购员\ AccountSpecialist 财务专员
      userInfo: this.$store.getters.userInfo,
      curRole: this.$store.getters.userType, // VENDOR BUYER curRole==='VENDOR'
      curOrderId: '',
      globalAmount: null,
      form: {
        orgId: null,
        orgCode: '',
        orgName: '',
        taxRate: '',
        taxKey: '',
        vendorCode: '',
        vendorName: '',
        vendorId: '',
        currencyId: '',
        currencyName: '',
        currencyCode: '',
        exchangeRate: '',
        taxTotalAmount: '',
        payAccountPeriodCode: '',
        payAccountPeriodName: '',
        invoiceDate: parseTime(new Date(), '{y}-{m}-{d}'),
        statementTotalAmount: null,
        invoiceTotalAmount: null,
        contractCode: null,
        approverNickname: null,
        approverDept: null,
        accountPayableDealine: null,
        payAccountPeriod: null,
        ifPaperAttach: null,
        businessType: null,
        invoiceTax: null,
        actualInvoiceAmountY: null,
        actualInvoiceAmountN: null,
        invoiceStatus: 'DRAFT'
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
        materialCode: null,
        materialName: null,
        orgName: null,
        categoryCode: null,
        organizationId: null,
        orderNumber: null,
        applyStartDate: null,
        applyEndDate: null,
        contractNo: null,
        projectNum: null,
        advanceApplyNum: null,
        advanceApplyStatus: 'APPROVAL'
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
        orgId: [{ required: true, message: this.$t('quota.orgIdTips') }], // 请选择业务实体
        vendorName: [{ required: true, message: this.$t('quota.vendorTips') }], // 请选择供应商名称
        costTypeCode: [
          { required: true, message: this.$t('quota.costTypeTips') }
        ], // 请选择成本类型
        invoiceDate: [
          {
            required: true,
            message: this.$t('purSettlementMod.invoiceDateTips')
          }
        ], // 请选择开票日期
        currency: [
          { required: true, message: this.$t('vendorMod.msgCurrencyCode') }
        ], // 请选择币种
        actualInvoiceAmountY: [
          {
            required: true,
            message: this.$t('purSettlementMod.actualInvoiceAmountYTips')
          }
        ], // 请输入实际发票总额
        invoiceTax: [
          {
            required: true,
            message: this.$t('purSettlementMod.invoiceTaxTips')
          }
        ] // 请输入发票税额
      },
      loading: false,
      sourceNumber: [], // 来源单号ID
      tempSourceNumber: [], // 来源单号临时
      orgList: [
        {
          organizationId: '6803628383141888',
          organizationCode: '10102',
          organizationName: '生活电器事业部'
        }
      ], // 合作组织下拉
      vendorOptions: [], // 供应商下拉
      siteOptions: [],
      belongOprId: null,
      deptOptions: [],
      onlineInvoiceDetails: [], // 入库明细
      onlineInvoicePunishes: [], // 退货明细
      onlineInvoiceAdvances: [], // 预付申请明细
      fileuploads: [],
      curOpt: 'add',
      isModify: false,
      activeDims: ['1', '2', '3', '4'],
      selectOrderData: [],
      rejectedDialog: false,
      rejectedModel: {
        // 驳回信息
        rejectedForm: {
          rejectReason: ''
        },
        rules: {
          rejectReason: [
            { required: true, message: this.$t('bidMod.msgRejectReason') }
          ] // 请输入驳回原因
        }
      }
    }
  },
  created () {
    this.curOpt = this.$attrs.params.flag
    this.rolePermissions = this.userInfo.rolePermissions[0].roleCode // 通过这个角色的code去判断如果在角色设置里面修改的话，程序要对应修改
    this.curOrderId = this.$attrs.params.onlineInvoiceId
    if (this.$attrs.params.readVirtualOne) {
      this.getOrderFormDetail2(this.$attrs.params.row.onlineInvoiceId) // 查询
    } else {
      this.getOrderFormDetail(this.$attrs.params.onlineInvoiceId) // 查询
    }
  },
  methods: {
    getCurrencyList (organizationId, currencyCode) {
      this.$http({
        url: '/api-base/organization/organization/get',
        method: 'GET',
        params: { organizationId: organizationId },
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            // this.globalCurrency = data.data.ceeaCurrencyCode;
            this.getExchangeRate(currencyCode, data.data.ceeaCurrencyCode)
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    getExchangeRate (from, to) {
      this.$http({
        url:
          '/api-base/purchase/latest-gidaily-rate/getRateByFromTypeAndToType',
        method: 'GET',
        params: {
          from,
          to
        },
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            this.form.exchangeRate = data.data
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    setPeriodObj (val) {
      let label = this.$getDictLabel('PAYMENT_PERIOD', val)
      this.form.payAccountPeriodName = label
    },
    // 查询单据详情
    getOrderFormDetail (onlineInvoiceId) {
      this.$http({
        url: '/api-sup-ce/ps/advanceApplyHead/advanceReturn',
        method: 'POST',
        data: this.$attrs.params.rowList,
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.form = res.data.onlineInvoice || {} // 头信息
          this.onlineInvoiceAdvances = res.data.onlineInvoiceAdvances // 预付申请明细
          this.fileuploads = res.data.fileuploads // 附件信息
          // 【审批人，部门】默认赋值
          /* if (this.$store.getters.user && this.$store.getters.user.userInfo) {
              const { nickname, department } = this.$store.getters.user.userInfo;
              this.form.approverNickname = nickname;
              this.form.approverDept = department;
            } */
          this.form.ifPaperAttach = 'N'
          for (let item of this.onlineInvoiceAdvances) {
            // 用来做计算的[可用金额]
            item['usableAmountLock'] = item['usableAmount']
            // item['chargeOffAmount'] = null;
          }
          if (res.data.onlineInvoice) {
            this.getCompanyList(res.data.onlineInvoice.orgId)
            this.getSiteInfo2(
              res.data.onlineInvoice.orgId,
              res.data.onlineInvoice.vendorId
            )
          }
        }
      })
    },
    getOrderFormDetail2 (onlineInvoiceId) {
      this.$http({
        url: '/api-sup-ce/ps/invoice/onlineInvoice/get',
        method: 'GET',
        params: { onlineInvoiceId: onlineInvoiceId },
        loading: true
      }).then(res => {
        if (res && res.data) {
          //
          this.form = res.data.onlineInvoice || {} // 头信息
          this.onlineInvoiceAdvances = res.data.onlineInvoiceAdvances // 预付申请明细
          this.fileuploads = res.data.fileuploads // 附件信息
        }
      })
    },
    // 创建网上开票,created[查询明细行详情]
    getOrderListDetail (list) {
      this.$http({
        url:
          '/api-sup-ce/ps/invoice/onlineInvoice/createOnlineInvoice',
        method: 'POST',
        data: list,
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.form = res.data.onlineInvoice || {} // 开票明细
          this.onlineInvoiceDetails = res.data.onlineInvoiceDetails // 开票明细
          this.onlineInvoicePunishes = res.data.onlineInvoicePunishes // 扣罚&返利明细
          this.onlineInvoiceAdvances = res.data.onlineInvoiceAdvances // 预付申请明细
          this.getCurrencyList(this.form.orgId, this.form.currencyCode)
          // 【审批人，部门】默认赋值
          /* if (this.$store.getters.user && this.$store.getters.user.userInfo) {
              const { nickname, department } = this.$store.getters.user.userInfo;
              this.form.approverNickname = nickname;
              this.form.approverDept = department;
            } */
          this.form.ifPaperAttach = 'N'
          for (let i = 0; i < this.onlineInvoiceDetails.length; i++) {
            this.onlineInvoiceDetails[i].invoiceRow = i - -1
          }
          this.form.totalTax = 0 // 税额---（系统）
          this.form.taxTotalAmount = 0 // 含税金额---（系统）
          for (let item of this.onlineInvoiceDetails) {
            // 用来做计算的[可用金额]
            item['usableAmountLock'] = item['usableAmount']
            // item['chargeOffAmount'] = null;
            this.form.totalTax += Number(item['tax']) || 0
            this.form.taxTotalAmount += Number(item['taxAmount']) || 0
          }
          this.form.totalTax = Number(this.form.totalTax).toFixed(2)
          this.form.taxTotalAmount = Number(this.form.taxTotalAmount).toFixed(
            2
          )
          // 用来做计算的明细行[税额]汇总
          this.globalAmount = this.onlineInvoiceDetails
            .map(v => v.noTaxAmount)
            .reduce((p, c) => (Number(p) || 0) + (Number(c) || 0))
        }
      })
    },
    // 新增税控明细
    addFileuploads () {
      this.fileuploads.push({
        fileUploadId: null,
        fileSourceName: null,
        comment: null
      })
    },
    // 删除税控
    delInvoiceTaxControls (index, row) {
      this.fileuploads.splice(index, 1)
    },
    delInvoiceDetails (index, row) {
      this.onlineInvoiceDetails.splice(index, 1)
    },
    delInvoicePunishes (index, row) {
      this.onlineInvoicePunishes.splice(index, 1)
    },
    delonlineInvoiceAdvances (index, row) {
      this.onlineInvoiceAdvances.splice(index, 1)
    },
    outerHandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.fileUploadId = fileId.toString()
      row.fileSourceName = fileName
    },
    confirmBill () {},
    cancelBill () {},
    reset () {
      // 重置所有过滤条件
      for (let i in this.form) {
        this.form[i] = ''
      }
    },
    getUserObj (val, scope) {
      scope.approverNickname = val ? val.nickname : ''
      scope.approverUsername = val ? val.username : ''
      scope.approverId = val ? val.userId : ''
      scope.approverDeptid = val ? val.ceeaDeptid : ''
      scope.approverDept = val ? val.department : ''
    },
    changeDeptHandle (deptCode, deptInfo) {
      if (deptInfo && deptInfo.element) {
        this.form.approverDeptid = deptInfo.element.deptCode
        this.form.approverDept = deptInfo.element.deptName
      } else {
        this.form.approverDeptid = ''
        this.form.approverDept = ''
      }
    },
    // 暂存
    saveHandle () {
      this.dataHandle('save')
    },
    dataHandle (type) {
      for (let row of this.onlineInvoiceAdvances) {
        if (row.chargeOffAmount < 0) {
          return this.$message.warning(this.$t('purSettlementMod.msgArr[2]'))
        }
        if (row.chargeOffAmount > row.usableAmountLock) {
          return this.$message.warning(this.$t('purSettlementMod.msgArr[3]'))
        }
      }
      let submitData = {
        onlineInvoice: this.form,
        onlineInvoiceDetails: this.onlineInvoiceDetails, // 发票明细
        onlineInvoicePunishes: this.onlineInvoicePunishes, // 扣罚、返利明细
        onlineInvoiceAdvances: this.onlineInvoiceAdvances, // 预付申请明细
        fileuploads: this.fileuploads // 附件
      }
      if (type === 'save') {
        // 暂存
        this.$http({
          url: '/api-sup-ce/ps/invoice/onlineInvoice/saveTemporary',
          method: 'POST',
          data: submitData,
          loading: true
        }).then(res => {
          this.$message.success(this.$t('common.success'))
          this.getOrderFormDetail2(res.data) // 查询
        })
      } else {
        // 提交
        this.$http({
          url: '/api-sup-ce/ps/invoice/onlineInvoice/submit',
          method: 'POST',
          data: submitData,
          loading: true
        }).then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.backTo()
        })
      }
    },
    // 提交
    submitHandle () {
      this.dataHandle('submit')
    },
    getCurrencyObj (val, scope) {
      scope.currencyId = val ? val.currencyId : ''
      scope.currencyCode = val ? val.currencyCode : ''
      scope.currencyName = val ? val.currencyName : ''
    },
    getExchangeRateObj (val, scope) {
      scope.exchangeRate = val ? String(val.conversionRate) : ''
    },
    selectHandler (node, value, scope) {
      this.form.orgId = node ? node.organizationId : null
      this.form.orgCode = node ? node.organizationCode : null
      this.form.orgName = node ? node.organizationName : null
      if (node) {
        this.getCompanyList(node.organizationId)
      } else {
        this.vendorOptions = []
      }
    },
    getCompanyList (organizationId) {
      this.$http({
        url: '/api-sup/info/companyInfo/listPageByOrgId',
        method: 'POST',
        data: {
          orgId: organizationId,
          pageNum: 1,
          pageSize: 9999
        },
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            this.vendorOptions = data.data.list
            let vendorObj = this.vendorOptions.filter(
              v => v.companyId == this.form.vendorId
            )
            if (vendorObj && vendorObj[0]) {
              this.form.erpVendorCode = vendorObj[0].erpVendorCode
            }
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    getSiteInfo2 (organizationId, vendorId) {
      this.$http({
        url: '/api-base/organization/organization/get',
        method: 'GET',
        params: { organizationId: organizationId },
        loading: true
      }).then(res => {
        if (res.data) {
          this.belongOprId = res.data.erpOrgId
          this.$http({
            url: '/api-sup/info/siteInfo/listSiteInfoByParam',
            method: 'POST',
            data: {
              companyId: vendorId,
              belongOprId: this.belongOprId,
              pageNum: 1,
              pageSize: 9999
            },
            loading: true
          }).then(data => {
            if (data && data.data) {
              this.siteOptions = data.data
            }
          })
        }
      })
    },
    setCostTypeObj (val) {
      let obj = this.siteOptions.filter(v => v.vendorSiteId === val)
      if (obj) {
        this.form.costTypeName = obj[0].vendorSiteCode
      }
    },
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
    },
    parentDataCurrentChange (num) {
      this.parentOrgQueryForm.pageNum = num
      this.queryItemList()
    },
    parentDataSizeChange (size) {
      this.parentOrgQueryForm.pageSize = size
      this.queryItemList()
    },
    openAdvanceItem () {
      this.filterForm.orgId = this.form.orgId
      this.filterForm.vendorId = this.form.vendorId
      this.filterForm.costTypeCode = this.form.costTypeCode
      this.queryItemList()
    },
    queryItemList () {
      const data = { ...this.parentOrgQueryForm, ...this.filterForm }
      this.$http({
        url:
          '/api-sup-ce/ps/invoice/onlineInvoice/listPageAdvanceByParam',
        method: 'POST',
        data: data,
        loading: true
      }).then(res => {
        this.displayMaterialItem = res.data.list
        this.parentOrgTableDataPage.total = res.data.total
        this.dialogFormVisible = true
        // [申请金额]赋值给[可用金额]
        for (let item of this.displayMaterialItem) {
          item['usableAmount'] = item['applyPayAmount']
        }
      })
    },
    addOneItem () {
      if (this.selectionItem2.length === 0) {
        return this.$message.warning(this.$t('common.msgSelectData'))
      }
      let advanceApplyHeadIdArr = this.onlineInvoiceAdvances.map(
        v => v.advanceApplyHeadId
      )
      for (let item of this.selectionItem2) {
        if (!advanceApplyHeadIdArr.includes(item.advanceApplyHeadId)) {
          this.onlineInvoiceAdvances.push({
            advanceApplyHeadId: item.advanceApplyHeadId,
            advanceApplyNum: item.advanceApplyNum,
            applyDate: item.applyDate,
            contractNum: item.contractNum,
            projectNum: item.projectNum,
            projectName: item.projectName,
            businessType: item.businessType,
            currencyCode: item.currencyCode,
            currencyName: item.currencyName,
            hangAccountAmount: item.applyPayAmount,
            usableAmount: item.usableAmount,
            usableAmountLock: item.usableAmount, // 不可变动的[可用金额]
            lockUsableAmount: item.usableAmount // [锁定可用金额]
            // chargeOffAmount: null
          })
        }
      }
      this.dialogFormVisible = false
    },
    handleSelectionChange (selection) {
      this.selectionItem = selection
    },
    resetFilterForm () {
      for (let i in this.filterForm) {
        this.filterForm[i] = ''
      }
      this.filterForm.advanceApplyStatus = 'APPROVAL'
    },
    getcontractObj (val, scope) {
      scope.contractCode = val.contractNo
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
      const data = { ...this.parentOrgQueryForm2, ...this.filterForm2 }
      this.$http({
        url: '/api-pef/vendorAsses/listPage',
        method: 'POST',
        data: data,
        loading: true
      }).then(res => {
        this.displayMaterialItem2 = res.data.list
        this.parentOrgTableDataPage2.total = res.data.total
        this.dialogFormVisible2 = true
      })
    },
    handleSelectionChange2 (selection) {
      this.selectionItem2 = selection
    },
    handleItemDBClick (val) {
      this.selectionItem2 = [val]
      this.addOneItem()
    },
    resetFilterForm2 () {
      for (let i in this.filterForm2) {
        this.filterForm2[i] = ''
      }
    },
    setAmount () {
      if (!this.form.invoiceTax || !this.form.actualInvoiceAmountY) return
      if (this.form.invoiceTax > this.form.actualInvoiceAmountY) {
        return this.$message.warning(this.$t('purSettlementMod.msgArr[0]'))
      } else {
        this.form.actualInvoiceAmountN = Number(
          this.form.actualInvoiceAmountY - this.form.invoiceTax
        ).toFixed(2)
        if (this.form.actualInvoiceAmountN - this.globalAmount > 1) {
          return this.$message.warning(this.$t('purSettlementMod.msgArr[1]'))
        }
      }
    },
    setRowAmount (row) {
      if (row.chargeOffAmount < 0) {
        return this.$message.warning(this.$t('purSettlementMod.msgArr[2]'))
      }
      if (row.chargeOffAmount > row.usableAmountLock) {
        return this.$message.warning(this.$t('purSettlementMod.msgArr[3]'))
      } else {
        row.usableAmount = row.usableAmountLock - row.chargeOffAmount
      }
    },
    backTo () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('advancePaymentList.getQuerydata')
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
}
</style>
<style>
.order-select .el-select-dropdown__list {
  padding: 0;
}
.order-select .el-select-dropdown__list .el-select-dropdown__item {
  border-bottom: 1px solid #ddd;
}
.order-select .el-select-dropdown__item.option-item.is-disabled {
  background: #f5f5f5;
}
.order-select .el-select-dropdown__item.option-item.is-disabled .border {
  /* color: #1890ff !important; */
  font-weight: bold;
  color: #666 !important;
}
.order-select .el-select-dropdown__list .el-select-dropdown__item .name {
  text-overflow: ellipsis;
  overflow: hidden;
  line-height: 26px !important;
}
.order-select .el-select-dropdown__list .el-select-dropdown__item .fullName {
  font-size: 12px;
}
.order-select.el-select-dropdown.is-multiple
  .el-select-dropdown__item.selected::after {
  top: 0 !important;
  right: 15px !important;
}
</style>
