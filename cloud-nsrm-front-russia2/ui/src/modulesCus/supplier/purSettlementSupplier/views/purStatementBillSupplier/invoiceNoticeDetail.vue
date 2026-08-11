<template>
  <el-container class="flex-container the-purInvoice-detail" direction="vertical">
    <el-main>
      <div class="form-container2">
        <el-form
          ref="invoiceNoticeForm"
          :model="form"
          :rules="formRules"
        >
          <el-collapse v-model="activeDims" class="tab-form-style">
            <el-collapse-item :title="$t('vendorMod.receiptInfo')" name="1">
              <srm-row :gutter="32">
                <!-- 业务实体 -->
                <srm-col>
                  <el-form-item :label="$t('quota.org')" prop="orgName">
                    <el-input v-model="form.orgName" disabled />
                    <!-- <OrganizationSelector
                      ref="organizationSelector"
                      v-model="form.orgId"
                      :parent-id="-1"
                      node-type="OU"
                      disabled
                    /> -->
                  </el-form-item>
                </srm-col>
                <!-- 单据状态 -->
                <srm-col>
                  <el-form-item :label="$t('purSettlementMod.paymentPlanStatus')" prop="invoiceNoticeStatus">
                    <dict-select
                      v-model="form.invoiceNoticeStatus"
                      code="INVOICE_NOTICE_STATUS"
                      disabled
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
                      name="scc_sup_company_info_all"
                    />
                  </el-form-item>
                </srm-col>
                <!-- 对账期间 -->
                <srm-col>
                  <el-form-item :label="$t('accountMod.statementDate')" prop="receiveDate">
                    <el-date-picker
                      v-model="form.receiveDate"
                      type="daterange"
                      :format="$formatDatePicker"
                      value-format="yyyy-MM-dd"
                      range-separator="~"
                      :start-placeholder="$t('dataConfMod.startDay')"
                      :end-placeholder="$t('dataConfMod.endDay')"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <!-- 供应商联系人 -->
                <srm-col>
                  <el-form-item :label="$t('vendorMod.vendorContact')" prop="extVendorContacts">
                    <el-input v-model="form.extVendorContacts" disabled />
                  </el-form-item>
                </srm-col>
                <!-- 供应商联系方式 -->
                <srm-col>
                  <el-form-item :label="$t('sourcingBuyer.supplierInfo')" prop="extVendorPhone">
                    <el-input v-model="form.extVendorPhone" disabled />
                  </el-form-item>
                </srm-col>
                <!-- 账期 -->
                <srm-col>
                  <el-form-item :label="$t('paymentType.paymentDay')" prop="paymentPeriod">
                    <dict-select
                      v-model="form.paymentPeriod"
                      code="PAYMENT_PROVISION"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <!-- 币种 -->
                <srm-col>
                  <el-form-item :label="$t('quota.currency')" prop="currencyCode">
                    <dict-select
                      v-model="form.currencyCode"
                      code="currency"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <!-- 创建人 -->
                <srm-col>
                  <el-form-item :label="$t('common.creator')" prop="createdUserName">
                    <el-input v-model="form.createdUserName" disabled />
                  </el-form-item>
                </srm-col>
                <!-- 含税金额 -->
                <srm-col>
                  <el-form-item :label="$t('contractMod.totalAmountTax')" prop="ceeaTaxTotalAmount">
                    <el-input-number
                      v-model="form.ceeaTaxTotalAmount"
                      :precision="4"
                      :controls="false"
                      class="input-number-precision"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <!-- 未税总金额 -->
                <srm-col>
                  <el-form-item :label="$t('contractMod.totalAmountNoTax2')" prop="ceeaNoTaxTotalAmount">
                    <el-input-number
                      v-model="form.ceeaNoTaxTotalAmount"
                      :precision="4"
                      :controls="false"
                      class="input-number-precision"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <!-- 总税额 -->
                <srm-col>
                  <el-form-item :label="$t('accountMod.totalTax')" prop="ceeaTotalTax">
                    <el-input-number
                      v-model="form.ceeaTotalTax"
                      :precision="4"
                      :controls="false"
                      class="input-number-precision"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <!-- 单据号 -->
                <srm-col>
                  <el-form-item :label="$t('flowMod.documentNo')" prop="invoiceNoticeNumber">
                    <el-input v-model="form.invoiceNoticeNumber" disabled />
                  </el-form-item>
                </srm-col>
                <!-- 创建日期 -->
                <srm-col>
                  <el-form-item :label="$t('purSettlementMod.creationDate')" prop="creationDate">
                    <el-date-picker
                      v-model="form.creationDate"
                      disabled
                      type="date"
                      :format="$formatDatePicker"
                      value-format="yyyy-MM-dd"
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>
            <!-- 入库或入库冲销明细 -->
            <el-collapse-item :title="$t('cusEntry.supplement20250205.reversalDetails')" name="2">
              <el-button
                type="primary"
                style="margin-bottom: 10px;"
                @click="exportDetail"
              >
                {{ $t('common.export') }}
              </el-button>
              <el-table :data="invoiceDetails" border stripe style="width: 100%">
                <el-table-column
                  :label="$t('common.sort')"
                  align="center"
                  type="index"
                  fixed="left"
                  width="50"
                />
                <!-- 事务类型 -->
                <el-table-column
                  prop="type"
                  :label="$t('cusEntry.supplement20250121.storageType')"
                  :formatter="formatData"
                  minWidth="100"
                  align="center"
                  show-overflow-tooltip
                />
                <!-- 事务处理日期 -->
                <el-table-column
                  prop="receiveDate"
                  :label="$t('cusEntry.supplement20250205.receiveDate')"
                  minWidth="100"
                  align="center"
                  :formatter="(row, column, cellValue) => $parseTime(cellValue)"
                  show-overflow-tooltip
                />
                <!-- 采购订单号 -->
                <el-table-column
                  prop="orderNumber"
                  :label="$t('purSettlementMod.orderNumber')"
                  minWidth="150"
                  align="center"
                  show-overflow-tooltip
                />
                <!-- 采购订单行号 -->
                <el-table-column
                  prop="lineNum"
                  :label="$t('purSettlementMod.orderLineNumber')"
                  minWidth="100"
                  align="center"
                  show-overflow-tooltip
                />
                <!-- 物料编码 -->
                <el-table-column
                  prop="itemCode"
                  :label="$t('common.materialCode')"
                  minWidth="150"
                  align="center"
                  show-overflow-tooltip
                />
                <!-- 物料名称 -->
                <el-table-column
                  prop="itemName"
                  :label="$t('common.materialName')"
                  minWidth="150"
                  align="center"
                  show-overflow-tooltip
                />
                <el-table-column
                  prop="extMaterialModel"
                  :label="$t('cusEntry.common.specification')"
                  minWidth="150"
                  align="center"
                  show-overflow-tooltip
                />

                <!-- 单位 -->
                <el-table-column
                  prop="unit"
                  :label="$t('dataConfMod.unit')"
                  minWidth="100"
                  align="center"
                  show-overflow-tooltip
                />
                <!-- 事务处理数量 -->
                <el-table-column
                  prop="receiveNum"
                  :label="$t('orderMod.buyerOrderSynergy.warehouseReceiptQuantity')"
                  minWidth="100"
                  align="center"
                  show-overflow-tooltip
                />
                <!-- 对账数量 -->
                <el-table-column
                  prop="invoiceQuantity"
                  :label="this.$t('purSettlementMod.reconciliationQuantity')"
                  minWidth="100"
                  align="center"
                  show-overflow-tooltip
                />
                <!-- 未税单价 -->
                <el-table-column
                  prop="unitPriceExcludingTax"
                  :label="$t('purSettlementMod.unitPriceNoTax')"
                  minWidth="150"
                  align="center"
                  :formatter="setNumberPrecision"
                  show-overflow-tooltip
                />
                <!-- 税率 -->
                <el-table-column
                  prop="taxKey"
                  :label="$t('bidMod.taxRate')"
                  minWidth="150"
                  align="center"
                  show-overflow-tooltip
                  :render-header="_addStarToColumn"
                >
                  <template slot-scope="scope">
                    <dict-select
                      v-model="scope.row.taxKey"
                      code="tax"
                      disabled
                    />
                  </template>
                </el-table-column>
                <!-- 含税单价 -->
                <el-table-column
                  prop="unitPriceContainingTax"
                  :label="$t('bid_mod.taxUnitPrice')"
                  minWidth="150"
                  align="center"
                  :formatter="setNumberPrecision"
                  show-overflow-tooltip
                />
                <!-- 未税总价 -->
                <el-table-column
                  prop="noTaxAmount"
                  :label="$t('cusEntry.bidMod.orderNotaxAmount')"
                  minWidth="150"
                  align="center"
                  :formatter="setNumberPrecision"
                  show-overflow-tooltip
                />
                <!-- 含税总价 -->
                <el-table-column
                  prop="taxAmount"
                  :label="$t('cusEntry.bidMod.orderTaxAmount')"
                  minWidth="150"
                  align="center"
                  :formatter="setNumberPrecision"
                  show-overflow-tooltip
                />
                <!-- 币种 -->
                <el-table-column
                  align="center"
                  minWidth="100"
                  prop="currencyName"
                  :label="$t('purchaseDemand.currency')"
                  show-overflow-tooltip
                />
              </el-table>
            </el-collapse-item>
            <!-- 开票信息/收票信息 -->
            <el-collapse-item :title="$t('cusEntry.supplement20250205.invoiceInfo')" name="3">
              <srm-row :gutter="32">
                <srm-col>
                  <!-- 发票类别 -->
                  <el-form-item :label="$t('cusEntry.dataConfMod.invoiceType')">
                    <DictSelect
                      v-model="form.extInvoiceType"
                      code="EXT_SOU_INQ_ORDER_INVOICE_TYPE"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 公司名称 -->
                  <el-form-item :label="$t('vendorMod.corporateName')">
                    <el-input v-model="form.extInvoiceCompany" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 开户行名称 -->
                  <el-form-item :label="$t('cusEntry.dataConfMod.openingName')">
                    <el-input v-model="form.extInvoiceOpeningName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 开户行账号 -->
                  <el-form-item :label="$t('cusEntry.dataConfMod.openingAccount')">
                    <el-input v-model="form.extInvoiceOpeningAccount" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 纳税人识别号 -->
                  <el-form-item :label="$t('vendorMod.socialCreditCode3')">
                    <el-input v-model="form.extInvoiceTaxpayerNum" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 电话 -->
                  <el-form-item :label="$t('common.phone')">
                    <el-input v-model="form.extInvoicePhone" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 地址 -->
                  <el-form-item :label="$t('vendorMod.location')">
                    <el-input v-model="form.extInvoiceAddress" disabled />
                  </el-form-item>
                </srm-col>
              </srm-row>
              <srm-row :gutter="32">
                <srm-col>
                  <!-- 收票人 -->
                  <el-form-item :label="$t('cusEntry.dataConfMod.collectPerson')">
                    <el-input v-model="form.extInvoiceReceiver" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 收票地址 -->
                  <el-form-item :label="$t('cusEntry.dataConfMod.collectAddress')">
                    <el-input v-model="form.extInvoiceReceiveAddr" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 收票邮箱 -->
                  <el-form-item :label="$t('cusEntry.supplement20250205.ticketReceiptEmail')">
                    <el-input v-model="form.collectMail" disabled />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>
            <el-collapse-item :title="$t('quota.fileInfo')" name="4">
              <el-table
                :data="fileUploads"
                border
                style="width: 100%; margin-bottom:16px;"
                max-height="250px"
              >
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('common.sort')"
                  width="60"
                />
                <el-table-column
                  align="center"
                  prop="fileName"
                  :label="$t('bidMod.fileName')"
                  minWidth="180"
                >
                  <template slot-scope="scope">
                    <SrmCommonFile
                      :extra-data="fileInfo"
                      :default-file="{
                        fileId: scope.row.fileuploadId,
                        fileName: scope.row.fileName
                      }"
                      readonly
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="createdFullName"
                  :label="$t('quota.uploadBy')"
                  minWidth="120"
                />
                <el-table-column
                  align="center"
                  prop="creationDate"
                  :label="$t('quota.uploadDate')"
                  :formatter="(row, column, cellValue) => $parseTime(cellValue)"
                  minWidth="120"
                />
              </el-table>
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </div>

      <CToolbar>
        <template slot="right">
          <el-button @click="backTo">
            {{ isReadOnly ? $t('common.close') : $t('common.cancel') }}
          </el-button>
          <el-button v-if="isManage" type="primary" @click="confirmInvoice">
            {{ $t('components.common.confirm') }}
          </el-button>
          <el-button v-if="isManage" type="primary" @click="refuseInvoice">
            {{ $t('common.refused') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import QuickSearch from 'lib@/components/QuickSearch'
import { parseTime } from '@/utils'
import OrganizationSelector from 'lib@/components/organization-selector'
import CToolbar from 'lib@/components/c-toolbar'
import { transformMQL } from 'lib@/utils/util'
import { downloadFileLink } from 'lib@/utils/file'
export default {
  name: 'InvoiceNoticeDetail',
  components: {
    QuickSearch,
    OrganizationSelector,
    CToolbar
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      activeDims: ['1', '2', '3', '4'],
      fileInfo: {
        uploadType: 'DEF',
        sourceType: 'WEB_APP',
        fileModular: 'pur',
        fileFunction: 'purInvoice',
        fileType: 'images'
      },
      form: {
        orgId: null,
        orgCode: null,
        orgName: null,
        organizationId: null,
        organizationName: null,
        organizationCode: null,
        invoiceNoticeId: null,
        invoiceNoticeStatus: null,
        vendorCode: null,
        vendorName: null,
        vendorId: null,
        receiveDate: null,
        extVendorContacts: null,
        extVendorPhone: null,
        paymentPeriod: null,
        currencyId: null,
        currencyCode: null,
        currencyName: null,
        createdUserName: null,
        ceeaTaxTotalAmount: null,
        ceeaNoTaxTotalAmount: null,
        ceeaTotalTax: null,
        invoiceNoticeNumber: null,
        creationDate: null,
        extInvoiceType: null,
        extInvoiceCompany: null,
        extInvoiceOpeningName: null,
        extInvoiceOpeningAccount: null,
        extInvoiceTaxpayerNum: null,
        extInvoicePhone: null,
        extInvoiceAddress: null,
        extInvoiceReceiver: null,
        extInvoiceReceiveAddr: null,
        collectMail: null
      },
      formRules: {
        orgId: [{ required: true, message: this.$t('quota.orgIdTips') }],
        vendorName: [{ required: true, message: this.$t('quota.vendorTips') }],
        receiveDate: [{ required: true, message: this.$t('qualitySynergy.msgSelDate') }],
        paymentPeriod: [{ required: true, message: this.$t('purSettlementMod.selectAccountingPeriod') }],
        currencyCode: [{ required: true, message: this.$t('vendorMod.msgCurrencyCode') }]
      },
      invoiceDetails: [], // 入库明细
      invoiceDetailsDelete: [],
      fileUploads: [], // 场景附件
      fileUploadsDelete: []
    }
  },
  computed: {
    isReadOnly () {
      // 管理按钮进来无编辑权限
      return ['view', 'manage'].includes(this.$attrs.params.flag)
    },
    isManage () {
      return this.$attrs.params.flag === 'manage'
    }
  },
  created () {
    if (this.$attrs.params.flag !== 'add') {
      this.getOrderFormDetail(this.$attrs.params.invoiceNoticeId) // 查询
      this.getFileAttach(this.$attrs.params.invoiceNoticeId)
    }
  },
  methods: {
    // 导出明细
    exportDetail () {
      downloadFileLink(
        `/api-sup-ce/InvoiceNotice/getInvoiceNoticeUpload?invoiceNoticeId=${this.form.invoiceNoticeId}`,
        this.$t('cusEntry.bidMod.exportLineDetail')
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    },
    formatData (row, column, cellValue, index) {
      return cellValue ? this.$getDictLabel('WAREHOURING_RETURN_DETAIL', cellValue) : cellValue
    },
    // 设置小数点位数4位
    setNumberPrecision (row, column, cellValue, index) {
      return cellValue ? cellValue.toFixed(4) : ''
    },
    // 查询场景附件
    getFileAttach (invoiceNoticeId) {
      this.$http({
        url: '/api-base/base/scene_file/listAll',
        method: 'POST',
        data: {
          businessId: invoiceNoticeId,
          sceneModuleCode: 'SCENE_INVOICE_NOTICE_ATTACHMENT'
        },
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.fileUploads = res.data
        }
      })
    },
    // 查询单据详情
    async getOrderFormDetail (invoiceNoticeId) {
      const searchData = transformMQL.save(
        'InvoiceNoticeVendor',
        [invoiceNoticeId],
        'read',
        {
          '*': {},
          'detailList': { '*': {} },
          'fileUploads': { '*': {} }
        }
      )
      this.$http({
        url: '/api-sup-ce/api-ql/InvoiceNoticeVendor/read',
        method: 'POST',
        data: searchData,
        loading: true
      }).then(res => {
        if (res && res.data) {
          const { detailList = [], fileUploads = [], ceeaReceiveStartDate, ceeaReceiveEndDate, extStatus, invoiceNoticeStatus, ...rest } = res.data[0] || {}
          this.form = {
            ...rest,
            invoiceNoticeStatus: extStatus || invoiceNoticeStatus, // 单据状态优先取扩展状态
            receiveDate: [ceeaReceiveStartDate, ceeaReceiveEndDate]
          }
          this.invoiceDetails = detailList?.map(item => { // 去除无关时间字段
            const { startDate, endDate, ...rest } = item
            return { ...rest }
          })
          if (this.form.orgId) {
            this.getInvoiceInfo(this.form.orgId)
          }
          // 场景附件单独查询
          // this.fileUploads = fileUploads
        }
      })
    },
    // 确认对账单
    async confirmInvoice () {
      const params = { invoiceNoticeId: this.form.invoiceNoticeId }
      const saveData = transformMQL.save('InvoiceNoticeVendor', [params], 'extSupplierConfirm')
      this.$http({
        url: '/api-sup-ce/api-ql/InvoiceNoticeVendor/extSupplierConfirm',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.backTo()
      })
    },
    // 拒绝对账单
    async refuseInvoice () {
      const prompt = await this.$prompt(this.$t('orderMod.msgRufuseReason'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        inputPattern: /\S{1,}/,
        inputErrorMessage: this.$t('orderMod.refuseReasonRequire')
      })
      if (!prompt) return
      const params = {
        invoiceNoticeId: this.form.invoiceNoticeId,
        rejectReason: prompt.value
      }
      const saveData = transformMQL.save('InvoiceNoticeVendor', [params], 'extSupplierRefuse')
      this.$http({
        url: '/api-sup-ce/api-ql/InvoiceNoticeVendor/extSupplierRefuse',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.backTo()
      })
    },
    backTo () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('invoiceNoticeList.getQuerydata')
    },
    // 查询开票信息
    getInvoiceInfo (organizationId) {
      this.$http({
        url: '/api-base/organization/organization/pj/getOrganization',
        method: 'GET',
        params: { organizationId },
        loading: true
      }).then(res => {
        if (res && res.data) {
          const { collectMail } = res.data?.orgCollectInfoList[0] || {}
          this.$set(this.form, 'collectMail', collectMail)
        }
      })
    }
  }
}
</script>
<style scoped lang="scss">
.the-purInvoice-detail {
  .input-number-precision {
    width: 100%;
    :deep(.el-input__inner) {
      text-align: left;
      padding-left: 8px;
    }
  }
  .btn_line {
    margin-bottom: 12px;
  }
}
</style>
