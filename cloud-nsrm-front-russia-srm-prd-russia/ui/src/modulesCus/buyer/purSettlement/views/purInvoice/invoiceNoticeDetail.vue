<template>
  <el-container class="flex-container the-purInvoice-detail" direction="vertical">
    <el-main>
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
                <el-form-item :label="$t('quota.org')" prop="orgId">
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
                    @close-quicksearch="getVendorObj"
                  />
                </el-form-item>
              </srm-col>
              <!-- 对账期间 -->
              <srm-col>
                <el-form-item :label="$t('accountMod.statementDate')" prop="receiveDate">
                  <el-date-picker
                    v-model="form.receiveDate"
                    type="daterange"
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
                    :disabled="isReadOnly"
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
                    @change="getCurrencyObj"
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
                    :precision="2"
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
                    :precision="2"
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
                    :precision="2"
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
                    format="yyyy-MM-dd"
                    value-format="yyyy-MM-dd"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-collapse-item>
          <el-collapse-item title="入库或入库冲销明细" name="2">
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
                label="入库类型"
                :formatter="formatData"
                minWidth="100"
                align="center"
                show-overflow-tooltip
              />
              <!-- 事务处理日期 -->
              <el-table-column
                prop="receiveDate"
                label="处理日期"
                minWidth="100"
                align="center"
                :formatter="parseTimeData"
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
                label="入库数量"
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
                    :disabled="isReadOnly"
                    @change-value="(value, dictItem) => taxRateChange(value, dictItem, scope.row)"
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
                show-overflow-tooltip
              />
              <!-- 含税总价 -->
              <el-table-column
                prop="taxAmount"
                label="含税总价"
                minWidth="150"
                align="center"
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
              <el-table-column
                :label="$t('common.operation')"
                width="80"
                align="center"
                fixed="right"
              >
                <template slot-scope="scope">
                  <el-button
                    type="text"
                    :disabled="isReadOnly"
                    @click="delInvoiceDetails(scope.$index, scope.row)"
                  >
                    {{ $t('common.delete') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
          <el-collapse-item title="开票信息/收票信息" name="3">
            <srm-row :gutter="32">
              <srm-col>
                <el-form-item label="发票类别">
                  <DictSelect
                    v-model="form.extInvoiceType"
                    code="EXT_SOU_INQ_ORDER_INVOICE_TYPE"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item label="公司名称">
                  <el-input v-model="form.extInvoiceCompany" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item label="开户行名称">
                  <el-input v-model="form.extInvoiceOpeningName" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item label="开户行账号">
                  <el-input v-model="form.extInvoiceOpeningAccount" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item label="纳税人识别号">
                  <el-input v-model="form.extInvoiceTaxpayerNum" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item label="电话">
                  <el-input v-model="form.extInvoicePhone" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item label="地址">
                  <el-input v-model="form.extInvoiceAddress" disabled />
                </el-form-item>
              </srm-col>
            </srm-row>
            <srm-row :gutter="32">
              <srm-col>
                <el-form-item label="收票人">
                  <el-input v-model="form.extInvoiceReceiver" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item label="收票地址">
                  <el-input v-model="form.extInvoiceReceiveAddr" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item label="收票邮箱">
                  <el-input v-model="form.collectMail" disabled />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-collapse-item>
          <el-collapse-item :title="$t('quota.fileInfo')" name="4">
            <div class="btn_line">
              <el-button
                type="primary"
                :disabled="isReadOnly"
                @click="addFileuploads"
              >
                {{ $t('common.add') }}
              </el-button>
            </div>
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
                    :readonly="isReadOnly"
                    @on-change="({file}) => uploadSuccess(file,scope.row)"
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
                minWidth="120"
              />
              <el-table-column :label="$t('common.operation')" width="100">
                <template slot-scope="scope">
                  <el-button
                    type="text"
                    :disabled="isReadOnly"
                    @click="delFileuploads(scope.$index, scope.row)"
                  >
                    {{ $t('common.delete') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
        </el-collapse>
      </el-form>
      <CToolbar>
        <template slot="right">
          <el-button @click="backTo">
            {{ isReadOnly ? $t('common.close') : $t('common.cancel') }}
          </el-button>
          <el-button v-if="!isReadOnly" type="primary" @click="saveOrSubmitData('SAVE')">
            {{ $t('common.save') }}
          </el-button>
          <el-button v-if="!isReadOnly" type="primary" @click="saveOrSubmitData('SUBMIT')">
            {{ $t('common.submit') }}
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
        extInvoiceReceiveAddr: null
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
      return this.$attrs.params.flag === 'view'
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
      this.form.organizationId = node ? node.organizationId : null
      this.form.organizationCode = node ? node.organizationCode : null
      this.form.organizationName = node ? node.organizationName : null
    },
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
    },
    getCurrencyObj (val) {
      this.form.currencyId = val ? val.currencyId : ''
      this.form.currencyCode = val ? val.currencyCode : ''
      this.form.currencyName = val ? val.currencyName : ''
    },
    formatData (row, column, cellValue, index) {
      return cellValue ? this.$getDictLabel('WAREHOURING_RETURN_DETAIL', cellValue) : cellValue
    },
    parseTimeData (row, column, cellValue) {
      return cellValue ? parseTime(cellValue, '{y}-{m}-{d}') : ''
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
        'InvoiceNotice',
        [invoiceNoticeId],
        'read',
        {
          '*': {},
          'detailList': { '*': {} },
          'fileUploads': { '*': {} }
        }
      )
      this.$http({
        url: '/api-sup-ce/api-ql/InvoiceNotice/read',
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
          this.invoiceDetails = detailList?.map(item => {
            // 去除无关时间字段
            const { startDate, endDate, ...rest } = item
            return { ...rest }
          })
          // 计算金额
          this.setTotal()

          // 场景附件单独查询
          // this.fileUploads = fileUploads
          // 查询开票信息
          let noInvoiceInfoFlag = !this.form.extInvoiceType && !this.form.extInvoiceCompany && !this.form.extInvoiceOpeningName &&
            !this.form.extInvoiceOpeningAccount && !this.form.extInvoiceTaxpayerNum && !this.form.extInvoicePhone &&
            !this.form.extInvoiceAddress && !this.form.extInvoiceReceiver && !this.form.extInvoiceReceiveAddr
          if (this.form.orgId && noInvoiceInfoFlag) {
            this.getInvoiceInfo(this.form.orgId)
          }
          // 查询供应商信息
          if (this.form.vendorId && !this.form.extVendorPhone && !this.form.extVendorContacts) {
            this.getContactInfo(this.form.vendorId)
          }
          // 默认账期：收票后45天
          if (!this.form.paymentPeriod) {
            this.form.paymentPeriod = 'NET45'
          }
          // 默认币种：人民币
          if (!this.form.currencyCode) {
            this.form.currencyCode = 'RMB'
            this.form.currencyName = '人民币'
          }
          // 默认对账期间：当前日期所在月的上一个月
          if (!ceeaReceiveStartDate || !ceeaReceiveEndDate) {
            let currentDate = new Date()
            let lastDayPrevMonthDate = new Date(currentDate.getFullYear(), currentDate.getMonth(), 0) // 获取上一个月的最后一天的日期
            let prevMonthFirstDay = currentDate.getFullYear() + '-' + currentDate.getMonth() + '-01' // 获取上一个月的第一天的日期
            // 当前日期为1月份, 则上一个月为0（12月）
            if (currentDate.getMonth() === 0) {
              lastDayPrevMonthDate = new Date(currentDate.getFullYear() - 1, 12, 0)
              prevMonthFirstDay = (currentDate.getFullYear() - 1) + '-' + 12 + '-01'
            }
            let prevMonthLastDay = lastDayPrevMonthDate.getFullYear() + '-' + (lastDayPrevMonthDate.getMonth() + 1) + '-' + lastDayPrevMonthDate.getDate()
            this.form.receiveDate = [prevMonthFirstDay, prevMonthLastDay]
          }
        }
      })
    },
    // 查询供应商联系人
    async getContactInfo (companyId) {
      const saveData = transformMQL.listPageData({
        type: 'ContactInfo',
        action: 'query',
        params: { companyId },
        filterOperator: {
          companyId: 'eq'
        }
      })
      this.$http({
        url: '/api-sup/api-ql/ContactInfo/query',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        if (res && res.data && res.data.records) {
          this.form.extVendorContacts = res.data.records[0]?.contactName
          this.form.extVendorPhone = res.data.records[0]?.ceeaContactMethod
        }
      })
    },
    // 查询开票信息
    getInvoiceInfo (organizationId) {
      this.$http({
        url: '/api-pj/organization/organization/getOrganization',
        method: 'GET',
        params: { organizationId },
        loading: true
      }).then(res => {
        if (res && res.data) {
          const { invoiceType, companyName, openingName, openingAccount, taxpayerNum, phone, address } = res.data?.orgInvoiceInfoList[0] || {}
          const { collectPerson, collectAddress, collectMail } = res.data?.orgCollectInfoList[0] || {}
          this.form.extInvoiceType = invoiceType
          this.form.extInvoiceCompany = companyName
          this.form.extInvoiceOpeningName = openingName
          this.form.extInvoiceOpeningAccount = openingAccount
          this.form.extInvoiceTaxpayerNum = taxpayerNum
          this.form.extInvoicePhone = phone
          this.form.extInvoiceAddress = address
          this.form.extInvoiceReceiver = collectPerson
          this.form.extInvoiceReceiveAddr = collectAddress
          this.form.collectMail = collectMail
        }
      })
    },
    delFileuploads (index, row) {
      if (row.sceneFileId) {
        this.fileUploadsDelete.push({ '$delete': row.sceneFileId })
      }
      this.fileUploads.splice(index, 1)
    },
    // 税率改变
    taxRateChange (val, dictItem, row) {
      // 税率值 用于计算金额
      row.taxRate = dictItem ? dictItem.key : ''
      this.setTotal()
      return val
    },
    setTotal () {
      this.getTaxCal()
      this.form.ceeaTotalTax = 0
      this.form.ceeaTaxTotalAmount = 0
      this.form.ceeaNoTaxTotalAmount = 0
      if (this.invoiceDetails.length) {
        // 总金额直接相加：入库时，入库数量和对账数量为正数，入库冲销为退货，入库数量和对账数量为负数
        this.invoiceDetails.map(item => {
          // 含税金额
          // 未税金额
          // 税额
          if (item.type == 'RECEIVE') {
            this.form.ceeaTaxTotalAmount += Number(item.taxAmount)
            this.form.ceeaNoTaxTotalAmount += Number(item.noTaxAmount)
            this.form.ceeaTotalTax += Number(item.tax)
          } else if (item.type == 'RETURN') {
            this.form.ceeaTaxTotalAmount -= Number(item.taxAmount)
            this.form.ceeaNoTaxTotalAmount -= Number(item.noTaxAmount)
            this.form.ceeaTotalTax -= Number(item.tax)
          }
        })
      }
    },
    // 计算明细行金额
    getTaxCal () {
      this.invoiceDetails.map(row => {
        // 含税单价 = 未税单价 * (1 + 税率)
        if (row.unitPriceExcludingTax && row.taxKey) { // 税率编码 taxKey
          row.unitPriceContainingTax = Number(row.unitPriceExcludingTax) * (1 + Number(row.taxRate) / 100)
        } else {
          row.unitPriceContainingTax = null
        }
        // 含税总价 = 含税单价 * 对账数量
        if (row.unitPriceContainingTax && row.invoiceQuantity) {
          row.taxAmount = (Number(row.unitPriceContainingTax) * Number(row.invoiceQuantity)).toFixed(2)
        } else {
          row.taxAmount = null
        }
        // 计算未税总价 = 未税单价 * 对账数量
        if (row.unitPriceExcludingTax && row.invoiceQuantity) {
          row.noTaxAmount = (Number(row.unitPriceExcludingTax) * Number(row.invoiceQuantity)).toFixed(2)
        } else {
          row.noTaxAmount = null
        }
        // 税额 = 含税总价 - 未税总价
        row.tax = (Number(row.taxAmount) - Number(row.noTaxAmount)).toFixed(2)
      })
    },
    // 删除明细
    delInvoiceDetails (index, row) {
      if (row.invoiceDetailId) {
        this.invoiceDetailsDelete.push({ '$delete': row.invoiceDetailId })
      }
      this.invoiceDetails.splice(index, 1)
      this.setTotal()
    },
    // 新增场景附件
    addFileuploads () {
      this.fileUploads.push({
        sceneFileId: null, // 场景附件主键id
        businessId: null,
        fileuploadId: null,
        fileName: null,
        sceneModuleCode: 'SCENE_INVOICE_NOTICE_ATTACHMENT'
      })
    },
    uploadSuccess (file, row) {
      const { fileId = null, fileName = null, createdFullName = null, creationDate = null } = file || {}
      row.fileuploadId = fileId
      row.fileName = fileName
      row.createdFullName = createdFullName
      row.creationDate = creationDate
    },
    async saveOrSubmitData (type) {
      // 列表有提交按钮，保存也校验必填
      const { flag, message } = await this.getCheckForm()
      if (flag) {
        let taxFlag = false
        let errMsg = ''
        this.invoiceDetails.some((item, index) => {
          if (!item.taxKey) {
            taxFlag = true
            errMsg = `明细数据第${index + 1}行税率为空`
            return true
          }
        })
        if (taxFlag) {
          this.$message.error(errMsg)
          return
        }
        this.saveData(type)
      } else {
        this.__focus_error__(message)
      }
    },
    // 暂存状态传DRAFT, 提交状态传SUBMITTED
    async saveData (type) {
      const { receiveDate = [], ...rest } = this.form
      const fileUploads = this.fileUploads.filter(item => !!item.fileuploadId)
      const params = {
        ...rest,
        ceeaReceiveStartDate: receiveDate ? receiveDate[0] : null,
        ceeaReceiveEndDate: receiveDate ? receiveDate[1] : null,
        invoiceNoticeStatus: type == 'SAVE' ? 'DRAFT' : 'SUBMITTED',
        detailList: [ ...this.invoiceDetails, ...this.invoiceDetailsDelete ],
        fileUploads: [ ...fileUploads, ...this.fileUploadsDelete ]
      }

      const saveData = transformMQL.save('InvoiceNotice', [params], 'extSaveOrUpdate')
      this.$http({
        url: '/api-sup-ce/api-ql/InvoiceNotice/extSaveOrUpdate',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.$message.success(this.$t('common.success'))
          if (type == 'SAVE') {
            this.invoiceDetailsDelete = []
            this.fileUploadsDelete = []
            let invoiceNoticeId = res.data[0]?.invoiceNoticeId
            this.getOrderFormDetail(invoiceNoticeId)
            this.getFileAttach(invoiceNoticeId)
            this.__setTabTodo('invoiceNoticeList.getQuerydata')
          } else {
            this.backTo()
          }
        }
      })
    },
    // form验证返回promise校验返回trun or false
    formValidate (formRef) {
      return new Promise((resolve) => {
        this.$refs[formRef].validate((flag, obj) => {
          resolve({ flag, obj })
        })
      })
    },
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
    backTo () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('invoiceNoticeList.getQuerydata')
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
