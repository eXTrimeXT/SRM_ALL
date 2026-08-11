<template>
  <el-container
    class="flex-container the-purInvoice-detail"
    direction="vertical"
  >
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
        @confirm="(type, comment) => saveOrSubmitData(type, comment)"
        @close-tab="backTo"
      >
        <div class="form-container2">
          <el-form
            ref="invoiceNoticeForm"
            :model="form"
            :rules="formRules"
            label-position="top"
            class="form-incontainer"
            :disabled="curOpt !== 'add' && curOpt !== 'edit'"
          >
            <el-collapse
              v-model="activeDims"
              class="tab-form-style"
            >
              <el-collapse-item
                :title="$t('purSettlementMod.supInvoiceInfo')"
                name="1"
              >
                <el-row>
                  <el-col :span="6">
                    <el-form-item
                      :label="$t('quota.org')"
                      prop="orgId"
                    >
                      <organization-selector
                        ref="organizationSelector"
                        v-model="form.orgId"
                        :parent-id="-1"
                        node-type="OU"
                        disabled
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="6">
                    <el-form-item
                      :label="$t('common.vendorName')"
                      prop="vendorName"
                    >
                      <el-input
                        v-model="form.vendorName"
                        disabled
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="6">
                    <el-form-item
                      :label="$t('purchaseDemand.vendorSite')"
                      prop="costTypeCode"
                    >
                      <el-select
                        v-model="form.costTypeCode"
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
                      <el-input v-model="form.taxInvoiceNum" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="6">
                    <el-form-item :label="$t('quota.currency')">
                      <!-- <QuickSearch
                      :showInput="form.currencyName"
                      show-key="currencyName"
                      :scope-data="form"
                      name="scc_base_purchase_currency_info"
                      @close-quicksearch="getCurrencyObj"
                    /> -->
                      <el-input
                        v-model="form.currencyName"
                        disabled
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="6">
                    <el-form-item :label="$t('bid_mod.priceTax')">
                      <!-- <QuickSearch
                      :preQueryData="{'t.FROM_CURRENCY_CODE': form.currencyCode}"
                      :disabled="!form.currencyCode"
                      :showInput="(form.exchangeRate)"
                      show-key="conversionRate"
                      :scope-data="form"
                      name="scc_base_latest_gidaily_rate_info"
                      @close-quicksearch="getExchangeRateObj"
                    /> -->
                      <el-input
                        v-model="form.exchangeRate"
                        disabled
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="6">
                    <el-form-item
                      :label="$t('contractMod.invocieDate')"
                      prop="invoiceDate"
                    >
                      <el-date-picker
                        v-model="form.invoiceDate"
                        type="date"
                        format="yyyy-MM-dd"
                        value-format="yyyy-MM-dd"
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="6">
                    <el-form-item
                      :label="$t('purSettlementMod.actualInvoiceAmount')"
                      prop="actualInvoiceAmountY"
                    >
                      <el-input-number
                        v-model="form.actualInvoiceAmountY"
                        :precision="2"
                        :step="0.1"
                        style="width:100%"
                        @change="setAmount"
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="6">
                    <el-form-item
                      :label="$t('purSettlementMod.invoiceTaxAmount')"
                      prop="invoiceTax"
                    >
                      <el-input-number
                        v-model="form.invoiceTax"
                        :precision="2"
                        :step="0.1"
                        style="width:100%"
                        @change="setAmount"
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
                    <el-form-item :label="$t('purSettlementMod.invoiceStatus')">
                      <DictSelect
                        v-model="form.invoiceStatus"
                        code="INVOICE_STATUS"
                        disabled
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="6">
                    <el-form-item :label="$t('purSettlementMod.unPaidAmount')">
                      <el-input
                        v-model="form.unPaidAmount"
                        disabled
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="6">
                    <el-form-item :label="$t('purSettlementMod.paidAmount')">
                      <el-input
                        v-model="form.paidAmount"
                        disabled
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="24">
                    <el-form-item :label="$t('purSettlementMod.abstract')">
                      <el-input
                        v-model="form.comment"
                        type="textarea"
                        :autosize="{ minRows: 2, maxRows: 5 }"
                      />
                    </el-form-item>
                  </el-col>
                </el-row>
              </el-collapse-item>
              <el-collapse-item
                :title="$t('purSettlementMod.invoiceDetail')"
                name="2"
              >
                <el-table
                  :data="onlineInvoiceDetails"
                  style="width: 100%"
                  border
                  max-height="251px"
                >
                  <el-table-column
                    align="center"
                    prop="invoiceRow"
                    :label="$t('purSettlementMod.invoiceRow')"
                    width="80"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="compareResult"
                    :label="$t('purSettlementMod.compareResult')"
                    width="80"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="invoiceNoticeNumber"
                    :label="$t('purSettlementMod.invoiceNoticeNumber')"
                    width="120"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="lineNum"
                    :label="$t('purSettlementMod.lineNum')"
                    width="100"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="receiveOrderNo"
                    :label="$t('orderMod.receiveOrderNo')"
                    width="120"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="receiveOrderLineNo"
                    :label="$t('orderMod.receiveOrderLineNo')"
                    width="80"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="receiveDate"
                    :label="$t('purSettlementMod.receiveDate')"
                    width="150"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="type"
                    :label="$t('purSettlementMod.type')"
                    :formatter="formatData"
                    width="100"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="orderNumber"
                    :label="$t('purSettlementMod.orderNumber')"
                    width="150"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="lineNum"
                    :label="$t('purSettlementMod.orderLineNumber')"
                    width="100"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="categoryName"
                    :label="$t('purchaseDemand.materialCateSub')"
                    width="100"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="itemCode"
                    :label="$t('common.materialCode')"
                    width="100"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="itemName"
                    :label="$t('common.materialName')"
                    min-width="150"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="unit"
                    :label="$t('dataConfMod.unit')"
                    width="80"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="receiveNum"
                    :label="$t('orderMod.receiveNum')"
                    width="100"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="invoiceQuantity"
                    :label="$t('purSettlementMod.invoiceQuantity')"
                    width="100"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="orderUnitPriceTaxN"
                    :label="$t('purSettlementMod.orderUnitPriceTaxN')"
                    width="100"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="unitPriceExcludingTax"
                    :label="$t('purSettlementMod.unitPriceExcludingTax')"
                    width="100"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="noTaxAmount"
                    :label="$t('purSettlementMod.noTaxAmount')"
                    width="100"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="taxRate"
                    :label="$t('bidMod.taxRate')"
                    width="100"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="tax"
                    :label="$t('contractMod.taxQuota')"
                    width="100"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="unitPriceContainingTax"
                    :label="$t('quota.taxPrice')"
                    width="100"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="taxAmount"
                    :label="$t('contractMod.amount2')"
                    width="100"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="contractNo"
                    :label="$t('contractMod.contractNo')"
                    width="150"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="projectNum"
                    :label="$t('contractMod.itemNumber')"
                    width="100"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="projectName"
                    :label="$t('contractMod.itemName')"
                    width="100"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="taskNum"
                    :label="$t('contractMod.taskNumber')"
                    width="100"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="taskName"
                    :label="$t('contractMod.taskName')"
                    width="100"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="taskName"
                    :label="$t('purchaseDemand.comments')"
                    width="100"
                    show-overflow-tooltip
                  />
                  <!-- <el-table-column :label="$t('common.operation')" width="60" fixed="right">
                  <template slot-scope="scope">
                    <el-button type="text" @click="delInvoiceDetails(scope.$index, scope.row)" >{{ $t("common.delete") }}</el-button>
                  </template>
                </el-table-column> -->
                </el-table>
              </el-collapse-item>
              <el-collapse-item
                :title="$t('purSettlementMod.deductionRebateDetails')"
                name="3"
              >
                <el-table
                  :data="onlineInvoicePunishes"
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
                    prop="assessmentNo"
                    :label="$t('perfMod.assessmentNo')"
                    width="120"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="assessmentDate"
                    :label="$t('perfMod.assessmentDate')"
                    width="100"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="assessmentType"
                    :label="$t('perfMod.inspectionType')"
                    width="100"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="actualAssessmentAmountN"
                    :label="$t('purSettlementMod.actualAssessmentAmountN')"
                    width="170"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="tax"
                    :label="$t('contractMod.taxQuota')"
                    width="100"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="actualAssessmentAmountY"
                    :label="$t('purSettlementMod.actualAssessmentAmountY')"
                    width="150"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="currencyCode"
                    :label="$t('quota.currency')"
                    width="100"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="categoryName"
                    :label="$t('bidMod.categoryName')"
                    width="100"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="itemCode"
                    :label="$t('common.materialCode')"
                    width="100"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="itemName"
                    :label="$t('common.materialName')"
                    min-width="150"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="comment"
                    :label="$t('common.remark')"
                    width="100"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    :label="$t('common.operation')"
                    width="60"
                    fixed="right"
                  >
                    <template slot-scope="scope">
                      <el-button
                        type="text"
                        @click="delInvoicePunishes(scope.$index, scope.row)"
                      >
                        {{ $t("common.delete") }}
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </el-collapse-item>
              <el-collapse-item
                :title="$t('quota.fileInfo')"
                name="4"
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
                          fileId: scope.row.fileuploadId,
                          fileName: scope.row.fileSourceName
                        }"
                        :readonly="false"
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
        <!--      <c-toolbar>-->
        <!--        <template slot="right">-->
        <!--          <el-button @click="backTo()">{{ curOpt!=='view'? $t('common.cancel') : $t('common.close')}}</el-button>-->
        <!--          <el-button type="primary" @click="saveData" :disabled="curOpt==='view'" v-if="curOpt!=='view'">{{$t('common.staging')}}</el-button>-->
        <!--          <el-button type="primary" @click="submitData" :disabled="curOpt==='view'" v-if="curOpt!=='view'">{{$t('common.submit')}}</el-button>-->
        <!--          <el-button type="primary" @click="approveHandle" v-if="curOpt==='approve' && (rolePermissions==='AccountSpecialist'|| rolePermissions==='Buyer')">{{$t("purchaseDemand.confirm")}}</el-button>-->
        <!--          <el-button type="primary" @click="rejectedHandle" v-if="curOpt==='approve' && (rolePermissions==='AccountSpecialist'|| rolePermissions==='Buyer')">{{$t("common.toRefuse")}}</el-button>-->
        <!--          -->
        <!--        </template>-->
        <!--      </c-toolbar>-->
        <!-- 驳回弹框 -->
        <!-- 驳回说明 -->
        <!--      <srm-dialog :title="$t('vendorMod.refuseMemo')" :visible.sync="rejectedDialog" size="small">-->
        <!--        <el-form ref="rejectedForm" :model="rejectedModel.rejectedForm" :rules="rejectedModel.rules">-->
        <!--          <el-form-item :label="$t('contractMod.rejectReason')" prop="rejectReason">-->
        <!--            <el-input type="textarea" v-model="rejectedModel.rejectedForm.rejectReason"></el-input>-->
        <!--          </el-form-item>-->
        <!--        </el-form>-->
        <!--        <div slot="footer" class="dialog-footer">-->
        <!--          <el-button @click="rejectedDialog = false">{{$t('common.cancel')}}</el-button>-->
        <!--          <el-button type="primary" @click="rejectedComfirm">{{$t('common.confirm')}}</el-button>-->
        <!--        </div>-->
        <!--      </srm-dialog>-->
      </CWorkflowMulti>
    </el-main>
  </el-container>
</template>
<script>
import WorkflowCommon from '@/library/mixins/workflow-common'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch'
import MainHeader from 'lib@/components/Table/MainHeader'
import { parseTime } from '@/utils'
import OrganizationSelector from 'lib@/components/organization-selector'
import CPagination from 'lib@/components/c-pagination'

export default {
  name: 'OnlineInvoiceDetail',
  components: {
    MainHeader,
    QuickSearch,
    CToolbar,
    OrganizationSelector,
    CPagination
  },
  mixins: [WorkflowCommon, tabTodoWatch, tabTodoMixin],
  data () {
    return {
      fileInfo: {
        fileModular: 'pur',
        fileFunction: 'purInvoice',
        fileType: 'images'
      },
      belongOprId: null,
      globalCurrency: null,
      rolePermissions: '', // 操作角色 Buyer 采购员\ AccountSpecialist 财务专员
      userInfo: this.$store.getters.userInfo,
      curRole: this.$store.getters.userType, // VENDOR BUYER curRole==='VENDOR'
      isReadOnly: this.$attrs.params.flag == 'readOnly',
      curOrderId: '',
      yesNoOptions: [
        { value: 'Y', label: this.$t('common.yes') },
        { value: 'N', label: this.$t('common.no') }
      ],
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
        currencyCode: '',
        currencyName: '',
        exchangeRate: '',
        invoiceDate: parseTime(new Date(), '{y}-{m}-{d}'),
        invoiceTax: null,
        actualInvoiceAmountY: null,
        actualInvoiceAmountN: null,
        comment: null,
        invoiceStatus: 'DRAFT',
        onlineInvoiceId: '',
        unPaidAmount: 0,
        paidAmount: 0
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
        startDate: null,
        endDate: null,
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
      globalAmount: null,
      siteOptions: [],
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
  computed: {
    viewUpdateButton () {
      // 用来控制保存、提交按钮是否可见。如果自定义按钮则无需添加
      return (
         this.curOpt !== 'view'
        // this.curRole === "BUYER" &&
        // !this.isReadOnly &&
        // this.form.invoiceStatus !== "APPROVED"
      )
    },
    disabledUpdateButton () {
      return (
        this.form.invoiceStatus === 'SUBMITTED' ||
        this.form.invoiceStatus === 'APPROVING'
      )
    },
    workflowBusinessId () {
      // 用来指定工作流的业务ID
      return this.form ? this.form.onlineInvoiceId : null
    },
    workflowTabDisabled () {
      return this.form.invoiceStatus === 'DRAFT'
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
    this.rolePermissions = this.userInfo.rolePermissions[0]
      ? this.userInfo.rolePermissions[0].roleCode
      : null // 通过这个角色的code去判断如果在角色设置里面修改的话，程序要对应修改
    if (this.curOpt == 'add') {
      this.getOrderListDetail(this.$attrs.params.tabList) // 查询明细行
      this.getSiteInfo2(
        this.$attrs.params.tabList[0].orgId,
        this.$attrs.params.tabList[0].vendorId
      )
      this.getCompanyList(this.$attrs.params.tabList[0].orgId)
    } else {
      // 编辑 查看
      this.curOrderId = this.$attrs.params.onlineInvoiceId
      this.getOrderFormDetail(this.$attrs.params.onlineInvoiceId) // 查询
    }
    this.buttonConfigInfo.save.view = this.viewUpdateButton
    this.buttonConfigInfo.submit.view = this.viewUpdateButton
    this.buttonConfigInfo.cancel.view = !this.isReadOnly
    this.buttonConfigInfo.close.view = this.isReadOnly
  },
  methods: {
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'REQUIREMENT'
    },
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    // 查询单据详情
    getOrderFormDetail (onlineInvoiceId) {
      this.$http({
        url: '/api-sup-ce/invoice/onlineInvoice/get',
        method: 'GET',
        params: { onlineInvoiceId: onlineInvoiceId },
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.form = res.data.onlineInvoice || {} // 头信息
          this.onlineInvoiceDetails = res.data.onlineInvoiceDetails // 入库明细
          this.onlineInvoicePunishes = res.data.onlineInvoicePunishes // 扣罚返利明细
          this.onlineInvoiceAdvances = res.data.onlineInvoiceAdvances // 预付申请明细
          this.fileuploads = res.data.fileuploads // 附件信息
          this.getSiteInfo2(
            res.data.onlineInvoice.orgId,
            res.data.onlineInvoice.vendorId
          )
          this.globalAmount = Number(
            this.form.taxTotalAmount - this.form.totalTax
          ).toFixed(2)
        }
      })
    },
    // 查询明细行详情
    getOrderListDetail (list) {
      this.$http({
        url: '/api-sup-ce/invoice/onlineInvoice/createOnlineInvoice',
        method: 'POST',
        data: list,
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.form = res.data.onlineInvoice || {} // 开票明细
          this.onlineInvoiceDetails = res.data.onlineInvoiceDetails // 入库明细
          this.onlineInvoicePunishes = res.data.onlineInvoicePunishes // 退货明细
          this.onlineInvoiceAdvances = res.data.onlineInvoiceAdvances // 预付申请明细
          this.getCurrencyList(this.form.orgId, this.form.currencyCode)
          // this.form.totalTax =0;            // 税额---（系统）
          // this.form.taxTotalAmount =0;    //含税金额---（系统）
          for (let i = 0; i < this.onlineInvoiceDetails.length; i++) {
            this.onlineInvoiceDetails[i].invoiceRow = i - -1
          }
          //  [start] by liwenhong
          // 入库明细税额、含税金额、本次开票净额保留两位
          let onlineInvoiceDetailsData = []
          onlineInvoiceDetailsData = this.onlineInvoiceDetails
          for (let i = 0; i < onlineInvoiceDetailsData.length; i++) {
            onlineInvoiceDetailsData[i].taxAmount = (
              Number(onlineInvoiceDetailsData[i].taxAmount) || 0
            ).toFixed(2)
            onlineInvoiceDetailsData[i].tax = (
              Number(onlineInvoiceDetailsData[i].tax) || 0
            ).toFixed(2)
            onlineInvoiceDetailsData[i].noTaxAmount = (
              Number(onlineInvoiceDetailsData[i].noTaxAmount) || 0
            ).toFixed(2)
          }
          this.onlineInvoiceDetails = onlineInvoiceDetailsData
          // [end] by liwenhong

          for (let item of this.onlineInvoiceDetails) {
            // 用来做计算的[可用金额]
            item['usableAmountLock'] = item['usableAmount']
            item['chargeOffAmount'] = null
            // this.form.totalTax += (Number(item['tax']) ||0);
            // this.form.taxTotalAmount += (Number(item['taxAmount']) ||0);
          }
          // this.form.totalTax = Number(this.form.totalTax).toFixed(2);
          //  this.form.totalTax = Math.floor(Number(this.form.totalTax) * 100) / 100
          // this.form.taxTotalAmount = Number(this.form.taxTotalAmount).toFixed(2);
          // 用来做计算的明细行[税额]汇总
          this.globalAmount = Number(
            this.form.taxTotalAmount - this.form.totalTax
          ).toFixed(2)
        }
      })
    },
    // 新增税控明细
    addFileuploads () {
      this.fileuploads.push({
        fileuploadId: null,
        fileSourceName: null,
        comment: null
      })
    },
    // 删除税控
    delInvoiceTaxControls (index, row) {
      if (row.fileuploadId) {
        this.$http({
          url: '/api-file/file/fileupload/delete',
          method: 'POST',
          params: { id: row.fileuploadId },
          loading: true
        }).then(res => {
          this.fileuploads.splice(index, 1)
        })
      } else {
        this.fileuploads.splice(index, 1)
      }
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
      const { fileId = '', fileName = '', createdBy = '', creationDate = '' } = file || {}
      row.fileuploadId = fileId.toString()
      row.fileSourceName = fileName
      row.createdBy = createdBy
      row.creationDate = creationDate
    },
    confirmBill () {},
    cancelBill () {},
    reset () {
      // 重置所有过滤条件
      for (let i in this.form) {
        this.form[i] = ''
      }
    },
    saveOrSubmitData (type) {
      console.log(type)
      if (type === 'SUBMIT') {
        this.submitData(type)
      } else {
        this.dataHandle(type)
      }
    },
    // 暂存
    // saveData() {
    //   this.dataHandle('save');
    // },
    dataHandle (type) {
      if (this.form.actualInvoiceAmountN - this.globalAmount > 1) {
        return this.$message.warning(this.$t('purSettlementMod.msgArr[1]'))
      }
      console.log('')
      let submitData = {}
      submitData = {
        onlineInvoice: this.form,
        onlineInvoiceDetails: this.onlineInvoiceDetails, // 发票明细
        onlineInvoicePunishes: this.onlineInvoicePunishes, // 扣罚、返利明细
        onlineInvoiceAdvances: this.onlineInvoiceAdvances, // 预付申请明细
        fileuploads: this.fileuploads // 附件
      }
      if (type === 'SAVE') {
        // 暂存
        if (this.form.actualInvoiceAmountY == 0) {
          this.$message({
            type: 'warning',
            message: this.$t('purSettlementMod.msgArr[7]')
          })
          return false
        }
        this.$http({
          url: '/api-sup-ce/invoice/onlineInvoice/saveTemporary',
          method: 'POST',
          data: submitData,
          loading: true
        }).then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.getOrderFormDetail(res.data)
        })
      } else {
        // 提交

        if (this.form.actualInvoiceAmountY == 0) {
          this.$message({
            type: 'warning',
            message: this.$t('purSettlementMod.msgArr[7]')
          })
          return false
        }
        if (this.form.actualInvoiceAmountY > 0) {
          if (this.form.invoiceTax > this.form.actualInvoiceAmountY) {
            this.$message({
              type: 'warning',
              message: this.$t('purSettlementMod.msgArr[15]')
            })
            return false
          }
        } else {
          if (
            this.form.actualInvoiceAmountY > this.form.invoiceTax ||
            this.form.invoiceTax > 0
          ) {
            this.$message({
              type: 'warning',
              message: this.$t('purSettlementMod.msgArr[15]')
            })
            return false
          }
        }

        this.$http({
          url: '/api-sup-ce/invoice/onlineInvoice/submit',
          method: 'POST',
          data: submitData,
          loading: true
        }).then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.$emit('tab-remove', this.$attrs.params.tabName)
          this.__setTabTodo('supOnlineInvoiceList.getQuerydata')
        })
      }
    },
    // 提交
    submitData (type) {
      if (this.form.actualInvoiceAmountY == 0) {
        this.$message({
          type: 'warning',
          message: this.$t('purSettlementMod.msgArr[7]')
        })
        return false
      }
      let _this = this
      _this.$refs.invoiceNoticeForm.validate(valid => {
        if (!valid) {
          _this.$message({
            message: _this.$t('common.pleasefinishRequired'),
            type: 'warning'
          })
          return false
        } else {
          if (_this.onlineInvoiceDetails.length < 1) {
            _this.$message({
              message: _this.$t('purSettlementMod.editInvoiceDetailTips'),
              type: 'warning'
            })
            return false
          } else {
            _this.dataHandle('SUBMIT')
          }
        }
      })
    },
    // 驳回操作
    rejectedHandle () {
      this.rejectedDialog = true
    },
    // 确认驳回
    rejectedComfirm () {
      this.$refs.rejectedForm.validate(valid => {
        let _this = this
        if (valid) {
          let parame = {}
          parame.onlineInvoiceId = this.curOrderId
          parame.rejectReason = _this.rejectedModel.rejectedForm.rejectReason
          if (this.rolePermissions === 'AccountSpecialist') {
            // 财务专员
            this.$api.pur.financeReject(parame).then(res => {
              _this.$message({
                message: res.message,
                type: 'success'
              })
              _this.rejectedDialog = false
              _this.backTo()
            })
          } else if (this.rolePermissions === 'Buyer') {
            // 财务采购员
            this.$api.pur.buyerReject(parame).then(res => {
              _this.$message({
                message: res.message,
                type: 'success'
              })
              _this.rejectedDialog = false
              _this.backTo()
            })
          }
        }
      })
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
            let vendorOptions = data.data.list
            let vendorObj = vendorOptions.filter(
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
    setAmount () {
      // if(!this.form.invoiceTax || !this.form.actualInvoiceAmountY) return;
      if (this.form.actualInvoiceAmountY > 0) {
        if (this.form.invoiceTax > this.form.actualInvoiceAmountY) {
          return this.$message.warning(this.$t('purSettlementMod.msgArr[0]'))
        }
      } else {
        if (
          this.form.actualInvoiceAmountY > this.form.invoiceTax ||
          this.form.invoiceTax > 0
        ) {
          return this.$message.warning(this.$t('purSettlementMod.msgArr[0]'))
        }
      }

      this.form.actualInvoiceAmountN = Number(
        this.form.actualInvoiceAmountY - this.form.invoiceTax
      ).toFixed(2)
      if (this.form.actualInvoiceAmountN - this.globalAmount > 1) {
        return this.$message.warning(this.$t('purSettlementMod.msgArr[1]'))
      }
      this.form.unPaidAmount =
        this.form.actualInvoiceAmountY - this.form.paidAmount
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
    getCurrencyObj (val, scope) {
      scope.currencyId = val ? val.currencyId : ''
      scope.currencyCode = val ? val.currencyCode : ''
      scope.currencyName = val ? val.currencyName : ''
    },
    getExchangeRateObj (val, scope) {
      scope.exchangeRate = val ? String(val.conversionRate) : ''
    },
    // 财务审批通过
    approveHandle () {
      let onlineInvoiceId = this.curOrderId
      if (this.rolePermissions === 'AccountSpecialist') {
        // 财务终审
        this.$api.pur.financeApprove({ onlineInvoiceId }).then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.backTo()
        })
      } else if (this.rolePermissions === 'Buyer') {
        // 采购初审批
        this.$api.pur.buyerFirstReview({ onlineInvoiceId }).then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.backTo()
        })
      }
    },
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
      this.queryItemList()
    },
    queryItemList () {
      const data = { ...this.parentOrgQueryForm, ...this.filterForm }
      this.$http({
        url: '/api-sup-ce/po/warehousingReturnDetail/listPage',
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
      this.onlineInvoiceDetails = this.onlineInvoiceDetails.concat(
        this.selectionItem
      )
      this.dialogFormVisible = false
    },
    handleSelectionChange (selection) {
      this.selectionItem = selection
    },
    resetFilterForm () {
      for (let i in this.filterForm) {
        this.filterForm[i] = ''
      }
    },
    getcontractObj (val, scope) {
      scope.contractNo = val.contractNo
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
    addNewOne3 () {
      this.onlineInvoiceAdvances = this.onlineInvoiceAdvances.concat(
        this.selectionItem2
      )
      this.dialogFormVisible = false
    },
    handleSelectionChange2 (selection) {
      this.selectionItem2 = selection
    },
    resetFilterForm2 () {
      for (let i in this.filterForm2) {
        this.filterForm2[i] = ''
      }
    },
    formatData (row, column, cellValue, index) {
      return cellValue ? this.$getDictLabel('WAREHOURING_RETURN_DETAIL', cellValue) : cellValue
    },
    backTo () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('supOnlineInvoiceList.getQuerydata')
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
