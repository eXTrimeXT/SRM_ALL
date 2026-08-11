<template>
  <CWorkflowMulti
    ref="workflowMulti"
    v-model="activeTabName"
    :funParams="workflowParamsInfo"
    :buttonConfigInfo="buttonConfigInfo"
    :showTopBtn="$attrs.params.type != 'view'"
    @tab-click="workflowView"
    @workflow-handler="workflowHandler"
    @click-handler="(type) => saveOrSubmit(type)"
    @submit-direct="(type) => saveOrSubmit(type)"
    @confirm="(type, comment) => saveOrSubmit(type, comment)"
    @close-tab="back"
  >
  <!-- <ApprovalProcess
    :approval-status="baseInfo.fixPriceStatus"
    :business-id="businessId"
    :readonly="$attrs.params.type === 'view'"
    business-type="EXT_SOU_FIX_PRICE"
    :operation-pre-options="operationPreOptions"
    @approval-handler-callback="approvalHandlerCallback"
  > -->
    <el-collapse v-model="activeNames" class="tab-form-style">
      <el-collapse-item :title="$t('cusEntry.inq.priceInfo')" name="1">
        <el-form
          ref="baseInfo"
          :model="baseInfo"
          :rules="rules"
          :disabled="isReadOnly"
        >
          <SrmRow>
            <SrmCol :initCol="3">
              <el-form-item :label="$t('cusEntry.inq.priceOrderNo')" prop="fixPriceNo">
                <el-input v-model="baseInfo.fixPriceNo" disabled />
              </el-form-item>
            </SrmCol>
            <SrmCol :initCol="3">
              <el-form-item :label="$t('cusEntry.inq.applyDate')" prop="fixPriceDate">
                <el-date-picker
                  v-model="baseInfo.fixPriceDate"
                  type="date"
                  :format="$formatDatePicker"
                  value-format="yyyy-MM-dd"
                />
              </el-form-item>
            </SrmCol>
            <SrmCol v-if="!$attrs.params.isMobile" :initCol="3">
              <el-form-item :label="$t('cusEntry.inq.purchaseDepartment')" prop="orgDepName">
                <QuickSearch
                  :show-input="baseInfo.orgDepName"
                  show-key="organizationName"
                  name="scc_base_organization"
                  :pre-query-data="{ 't.organization_type_code': 'DEP' }"
                  @close-quicksearch="getDepartment"
                />
              </el-form-item>
            </SrmCol>
            <SrmCol v-if="!$attrs.params.isMobile" :initCol="3">
              <el-form-item :label="$t('cusEntry.inq.priceOrderState')" prop="fixPriceStatus">
                <DictSelect
                  v-model="baseInfo.fixPriceStatus"
                  code="EXT_FIX_PRICE_STATUS"
                  disabled
                />
              </el-form-item>
            </SrmCol>
            <SrmCol :initCol="3">
              <el-form-item :label="$t('cusEntry.inq.totalNotaxPrice')" prop="totalNotaxPrice">
                <el-input v-model="baseInfo.totalNotaxPrice" disabled />
              </el-form-item>
            </SrmCol>
            <SrmCol :initCol="3">
              <el-form-item :label="$t('cusEntry.inq.totalPriceAndTax')" prop="totalTaxPrice">
                <el-input v-model="baseInfo.totalTaxPrice" disabled />
              </el-form-item>
            </SrmCol>
          </SrmRow>
          <SrmRow>
            <el-col :span="$attrs.params.isMobile ? 24 : 8">
              <el-form-item v-if="!$attrs.params.isMobile" :label="$t('cusEntry.inq.file')">
                <SrmCommonFile
                  limit="10"
                  multiple
                  :extraData="{
                    fileModular: 'inq',
                    fileFunction: 'priceOrders',
                    fileType: 'images'
                  }"
                  :file-list="fileList"
                  :readonly="isReadOnly"
                  @on-change="({fileList}) => handleUploadSuccess(fileList)"
                />
              </el-form-item>
              <!-- <el-form-item :label="$t('cusEntry.inq.purchaseOnBehalf')" prop="dc">
                <DictSelect
                  v-model="baseInfo.dc"
                  code="YES_OR_NO"
                />
              </el-form-item> -->
              <!-- 研发/生产 -->
              <el-form-item :label="$t('cusEntry.supplement20250218.extFixPriceType')" prop="extFixPriceType">
                <DictSelect
                  v-model="baseInfo.extFixPriceType"
                  code="FIX_PRICE_TYPE"
                />
              </el-form-item>
            </el-col>
            <el-col :span="$attrs.params.isMobile ? 24 : 16">
              <el-form-item :label="$t('cusEntry.inq.priceRemark')" prop="remark">
                <el-input
                  v-model="baseInfo.remark"
                  type="textarea"
                  :rows="5"
                  maxlength="1000"
                />
              </el-form-item>
            </el-col>
          </SrmRow>
        </el-form>
      </el-collapse-item>
      <el-collapse-item :title="$t('cusEntry.inq.materialDetail')" name="2">
        <div
          v-if="!isReadOnly"
          class="table-header-btn"
          style="margin-bottom: 10px;"
        >
          <el-button
            type="primary"
            @click="inquiryVisible = true"
          >
            {{ $t('cusEntry.common.inquiry') }}
          </el-button>
          <!-- <el-button
            type="primary"
            @click="recentPurchaseVisible = true"
          >
            {{ $t('cusEntry.common.recentPurchase') }}
          </el-button> -->
          <el-button
            type="primary"
            :disabled="materialDetail.length === 0"
            @click="paymethodVisible = true"
          >
            {{ $t('cusEntry.common.batchPayMethodOrPayterm') }}
          </el-button>
          <el-button
            type="primary"
            @click="exportDetail"
          >
            {{ $t('common.export') }}
          </el-button>
        </div>
        <el-table
          ref="materialTable"
          border
          :data="materialDetail.slice((pagination.pageNum - 1) * pagination.pageSize, pagination.pageNum * pagination.pageSize)"
          max-height="580"
          class="hidden-default-background"
          :row-class-name="setRowClass"
          :header-cell-class-name="leftHeaderStyle"
          @current-change="handleCurrentChange"
          @select-all="selectAll"
        >
          <el-table-column
            type="selection"
            align="center"
            width="50"
            fixed="left"
            :selectable="row => false"
          />
          <el-table-column
            type="index"
            :label="$t('common.sort')"
            width="50"
            align="center"
            fixed="left"
          />
          <el-table-column
            align="center"
            :label="$t('cusEntry.inq.materialCode')"
            prop="itemCode"
            fixed="left"
            sortable
            show-overflow-tooltip
            min-width="120"
          />
          <el-table-column
            align="center"
            :label="$t('cusEntry.inq.materialName')"
            prop="materialNameShow"
            fixed="left"
            sortable
            show-overflow-tooltip
            min-width="120"
          />
          <el-table-column
            align="center"
            :label="$t('cusEntry.bidMod.specification')"
            prop="extMaterialModelShow"
            fixed="left"
            sortable
            show-overflow-tooltip
            min-width="120"
          />
          <el-table-column
            align="center"
            :label="$t('cusEntry.inq.baseMeasurmentUnit')"
            prop="unit"
            sortable
            show-overflow-tooltip
            min-width="120"
            :formatter="(row) => {
              return row.unit ? $getDictLabel('unit', row.unit) : ''
            }"
          />
          <el-table-column
            align="center"
            :label="$t('cusEntry.inq.quantity')"
            prop="quantity"
            sortable
            show-overflow-tooltip
            min-width="120"
          />
          <el-table-column
            align="center"
            :label="$t('bidMod.vendorName')"
            prop="vendorName"
            sortable
            show-overflow-tooltip
            min-width="120"
          />
          <!-- 原币种 -->
          <el-table-column
            align="center"
            :label="$t('cusEntry.supplement20250218.originalCurrency')"
            prop="currencyCode"
            sortable
            show-overflow-tooltip
            min-width="120"
            :formatter="row => {
              return row.currencyCode ? $getDictLabel('currency', row.currencyCode) : ''
            }"
          />
          <!-- 转换后币种 -->
          <el-table-column
            align="center"
            :label="$t('cusEntry.supplement20250218.convertedCurrency')"
            prop="extConvertCurrency"
            sortable
            show-overflow-tooltip
            min-width="120"
            :formatter="row => {
              return row.extConvertCurrency ? $getDictLabel('currency', row.extConvertCurrency) : ''
            }"
          />
          <el-table-column
            align="center"
            :label="$t('bidMod.taxRate2')"
            prop="taxRate"
            sortable
            show-overflow-tooltip
            min-width="120"
          />
          <el-table-column
            align="center"
            :label="$t('bidMod.quotenotaxPrice2')"
            prop="notaxPrice"
            sortable
            show-overflow-tooltip
            min-width="120"
          />
          <el-table-column
            align="center"
            :label="$t('cusEntry.inq.noTaxAmount')"
            prop="notaxTotalPrice"
            sortable
            show-overflow-tooltip
            min-width="120"
          />
          <el-table-column
            v-if="!$attrs.params.isMobile"
            align="center"
            :label="$t('cusEntry.inq.invoiceType')"
            prop="invoiceType"
            sortable
            show-overflow-tooltip
            min-width="120"
            :formatter="row => {
              return row.invoiceType ? $getDictLabel('EXT_SOU_INQ_ORDER_INVOICE_TYPE', row.invoiceType) : ''
            }"
          />
          <el-table-column
            v-if="!$attrs.params.isMobile"
            align="center"
            :label="$t('cusEntry.inq.recentLowPrice')"
            prop="latestMinNotaxPrice"
            sortable
            show-overflow-tooltip
            min-width="160"
          />
          <el-table-column
            v-if="!$attrs.params.isMobile"
            align="center"
            :label="$t('cusEntry.inq.floatRatio')"
            prop="priceFloatScale"
            sortable
            min-width="120"
            :formatter="row => {
              return (row.priceFloatScale || row.priceFloatScale == 0) ? `${(Number(row.priceFloatScale) * 100).toFixed(2)}%` : ''
            }"
          />
          <el-table-column
            v-if="!$attrs.params.isMobile"
            align="center"
            :label="$t('cusEntry.inq.recentLowSupplier')"
            prop="latestMinVendorName"
            sortable
            show-overflow-tooltip
            min-width="140"
          />
          <el-table-column
            v-if="!$attrs.params.isMobile"
            align="center"
            :label="$t('cusEntry.inq.recentLowPriceBrand')"
            prop="latestMinBrand"
            sortable
            show-overflow-tooltip
            min-width="140"
          />
          <el-table-column
            v-if="!$attrs.params.isMobile"
            align="center"
            :label="$t('cusEntry.inq.modifyBidReason')"
            prop="extWinReason"
            sortable
            show-overflow-tooltip
            min-width="120"
          />
          <el-table-column
            v-if="!$attrs.params.isMobile"
            align="center"
            :label="$t('cusEntry.inq.supplierRemark')"
            prop="orderRemark"
            sortable
            show-overflow-tooltip
            min-width="120"
          />
          <!-- 是否预付 -->
          <el-table-column
            align="center"
            :label="$t('cusEntry.bidMod.extIsPrepaid')"
            sortable
            prop="extIsPrepaid"
            min-width="120"
            :formatter="(row) => {
              return row.extIsPrepaid ? $getDictLabel('YES_OR_NO', row.extIsPrepaid) : ''
            }"
          />
          <!-- 预付比例% -->
          <el-table-column
            align="center"
            :label="$t('cusEntry.bidMod.extPrepaidRatio')"
            sortable
            prop="extPrepaidRatio"
            min-width="120"
          />
          <!-- <el-table-column
            v-if="!$attrs.params.isMobile"
            align="center"
            :label="$t('cusEntry.bidMod.advancePayment')"
            prop="advancePaymentRemark"
            sortable
            show-overflow-tooltip
            min-width="120"
            :formatter="(row) => {
              return row.advancePaymentRemark ? $getDictLabel('YES_OR_NO', row.advancePaymentRemark) : ''
            }"
          /> -->
          <el-table-column
            v-if="!$attrs.params.isMobile"
            align="center"
            :label="$t('cusEntry.inq.applyUnit')"
            prop="orgOuName"
            sortable
            show-overflow-tooltip
            min-width="120"
          />
          <el-table-column
            v-if="!$attrs.params.isMobile"
            align="center"
            :label="$t('purchaseDemand.ceeaDepartment')"
            prop="ceeaDepartmentName"
            sortable
            show-overflow-tooltip
            min-width="120"
          />
          <el-table-column
            v-if="!$attrs.params.isMobile"
            align="center"
            :label="$t('cusEntry.bidMod.deliveryCycle')"
            prop="extLeadTime"
            sortable
            show-overflow-tooltip
            min-width="140"
          />
          <el-table-column
            v-if="!$attrs.params.isMobile"
            align="center"
            :label="$t('cusEntry.inq.purchaser')"
            prop="buyerNickname"
            sortable
            show-overflow-tooltip
            min-width="120"
          />
          <!-- <el-table-column
            v-if="!$attrs.params.isMobile"
            align="center"
            :label="$t('cusEntry.inq.applyType')"
            prop="applyType"
            min-width="120"
            sortable
            show-overflow-tooltip
            :formatter="(row) => {
              return row.applyType ? $getDictLabel('application_form_type', row.applyType) : ''
            }"
          /> -->
          <el-table-column
            v-if="!$attrs.params.isMobile"
            align="center"
            :label="$t('cusEntry.bidMod.warrantyPeriod')"
            prop="extWarrantyPeriod"
            sortable
            show-overflow-tooltip
            min-width="140"
          />
          <el-table-column
            v-if="!$attrs.params.isMobile"
            align="center"
            :label="$t('cusEntry.inq.estimatePrice')"
            prop="extPredictPrice"
            sortable
            min-width="120"
          />
          <el-table-column
            v-if="!$attrs.params.isMobile"
            align="center"
            :label="$t('cusEntry.inq.estimateAmount')"
            prop="extPredictAmount"
            sortable
            show-overflow-tooltip
            min-width="120"
          />
          <!-- <el-table-column
            v-if="!$attrs.params.isMobile"
            align="center"
            :label="$t('cusEntry.inq.dataSource')"
            prop="sourceFromType"
            min-width="120"
            sortable
            show-overflow-tooltip
            :formatter="(row) => {
              return row.sourceFromType ? $getDictLabel('EXT_SOU_FIX_PRICE_LINE_FROM_TYPE', row.sourceFromType) : ''
            }"
          /> -->
          <el-table-column
            v-if="!$attrs.params.isMobile"
            align="center"
            :label="$t('cusEntry.inq.inquiryNo')"
            prop="sourceFromNo"
            min-width="120"
            sortable
            show-overflow-tooltip
          />
          <!-- <el-table-column
            v-if="!$attrs.params.isMobile"
            align="center"
            :label="$t('cusEntry.inq.buyType')"
            prop="extBuyType"
            min-width="120"
            sortable
            show-overflow-tooltip
            :formatter="(row) => {
              return row.extBuyType ? $getDictLabel('PR_BUY_TYPE', row.extBuyType) : ''
            }"
          /> -->
          <!-- <el-table-column
            v-if="!$attrs.params.isMobile"
            prop="fixPriceLineStatus"
            align="center"
            :label="$t('bidMod.auditStatus')"
            min-width="120"
            sortable
            show-overflow-tooltip
            :formatter="(row) => {
              return row.fixPriceLineStatus ? $getDictLabel('EXT_SOU_FIX_PRICE_LINE_STATUS', row.fixPriceLineStatus) : ''
            }"
          /> -->
          <!-- <el-table-column
            v-if="!$attrs.params.isMobile"
            prop="hasClosed"
            align="center"
            :label="$t('cusEntry.inq.fixPriceStatus')"
            min-width="120"
            sortable
            show-overflow-tooltip
            :formatter="(row) => {
              return row.hasClosed ? $getDictLabel('YES_OR_NO', row.hasClosed) : ''
            }"
          /> -->
          <el-table-column
            v-if="!$attrs.params.isMobile"
            align="center"
            :label="$t('cusEntry.inq.payMethod')"
            prop="paymentMethod"
            sortable
            min-width="120"
          >
            <template slot-scope="scope">
              <DictSelect
                v-if="!isReadOnly"
                v-model="scope.row.paymentMethod"
                code="JC_PAYMENT_WAY"
              />
              <span v-else> {{ scope.row.paymentMethod ? $getDictLabel('JC_PAYMENT_WAY', scope.row.paymentMethod) : '' }}</span>
            </template>
          </el-table-column>
          <el-table-column
            v-if="!$attrs.params.isMobile"
            align="center"
            :label="$t('cusEntry.inq.payment')"
            sortable
            prop="paymentTerm"
            min-width="120"
          >
            <template slot-scope="scope">
              <DictSelect
                v-if="!isReadOnly"
                v-model="scope.row.paymentTerm"
                code="PAYMENT_PROVISION"
              />
              <span v-else> {{ scope.row.paymentTerm ? $getDictLabel('PAYMENT_PROVISION', scope.row.paymentTerm) : '' }}</span>
            </template>
          </el-table-column>
          <!-- <el-table-column
            v-if="!$attrs.params.isMobile"
            align="center"
            :label="$t('cusEntry.inq.ifSignContract')"
            sortable
            prop="hasSignedContract"
            min-width="120"
          >
            <template slot-scope="scope">
              <DictSelect
                v-if="!isReadOnly"
                v-model="scope.row.hasSignedContract"
                code="YES_OR_NO"
              />
              <span v-else> {{ scope.row.hasSignedContract ? $getDictLabel('YES_OR_NO', scope.row.hasSignedContract) : '' }}</span>
            </template>
          </el-table-column> -->
          <el-table-column
            v-if="!$attrs.params.isMobile"
            :label="$t('common.operation')"
            width="100"
            fixed="right"
          >
            <template slot-scope="scope">
              <el-button
                v-if="!isReadOnly"
                type="text"
                @click.stop="deleteRow(scope.$index)"
              >
                {{ $t('common.delete') }}
              </el-button>
              <!-- 非新增、行未关闭、且状态不是审批中-->
              <!-- <el-button
                v-if="!$attrs.params.hidden && $attrs.params.type !== 'add' && scope.row.hasClosed === 'N' && baseInfo.fixPriceStatus !== 'SUBMITTED'"
                type="text"
                @click.stop="closeRow(scope.row)"
              >
                {{ $t('common.close') }}
              </el-button> -->
            </template>
          </el-table-column>
        </el-table>
        <!--分页 -->
        <CPagination
          :total="materialDetail.length"
          :page-num="pagination.pageNum"
          :page-size="pagination.pageSize"
          @current-change="paginationCurrentChange"
          @size-change="paginationSizeChange"
        />
      </el-collapse-item>
      <el-collapse-item :title="$t('cusEntry.inq.supplierQuoteDetail')" name="3">
        <el-table
          border
          :data="supplierQuoteDetail"
          :row-class-name="setRowClassQuote"
          max-height="250"
        >
          <el-table-column
            type="index"
            :label="$t('common.sort')"
            align="center"
            width="50"
            fixed="left"
          />
          <el-table-column
            v-if="!$attrs.params.isMobile"
            align="center"
            :label="$t('bidMod.vendorCode')"
            prop="vendorCode"
            min-width="120"
            show-overflow-tooltip
          />
          <el-table-column
            align="center"
            :label="$t('bidMod.vendorName')"
            prop="vendorName"
            min-width="120"
            show-overflow-tooltip
          />
          <el-table-column
            align="center"
            :label="$t('bidMod.quotenotaxPrice2')"
            prop="orderNotaxPrice"
            min-width="120"
            show-overflow-tooltip
          />
          <el-table-column
            v-if="!$attrs.params.isMobile"
            align="center"
            :label="$t('cusEntry.inq.quoteTime')"
            prop="submitTime"
            min-width="120"
            show-overflow-tooltip
            :formatter="(row, column, cellValue) => $parseTime(cellValue)"
          />
          <el-table-column
            align="center"
            :label="$t('bidMod.currency_price')"
            prop="orderCurrency"
            sortable
            show-overflow-tooltip
            min-width="120"
            :formatter="(row, column, cellValue) => cellValue ? $getDictLabel('currency', cellValue) : ''"
          />
          <!-- 转换后币种 -->
          <el-table-column
            align="center"
            :label="$t('cusEntry.supplement20250218.convertedCurrency')"
            prop="extConvertCurrency"
            sortable
            show-overflow-tooltip
            min-width="120"
            :formatter="(row, column, cellValue) => cellValue ? $getDictLabel('currency', cellValue) : ''"
          />
          <!-- 转换后未税单价 -->
          <el-table-column
            align="center"
            :label="$t('cusEntry.supplement20250218.extConvertNotaxPrice')"
            prop="extConvertNotaxPrice"
            sortable
            show-overflow-tooltip
            min-width="130"
          />
          <el-table-column
            align="center"
            :label="$t('bidMod.taxRate2')"
            prop="taxRate"
            min-width="120"
            show-overflow-tooltip
          />
          <el-table-column
            align="center"
            :label="$t('cusEntry.bidMod.deliveryCycle')"
            prop="extLeadTime"
            min-width="120"
            show-overflow-tooltip
          />
          <el-table-column
            align="center"
            :label="$t('cusEntry.bidMod.remark')"
            prop="orderRemark"
            min-width="120"
            show-overflow-tooltip
          />
          <el-table-column
            v-if="!$attrs.params.isMobile"
            align="center"
            :label="$t('cusEntry.bidMod.quoteEffictDate')"
            prop="priceActiveDay"
            min-width="140"
            show-overflow-tooltip
          />
          <el-table-column
            align="center"
            :label="$t('cusEntry.bidMod.warrantyPeriod')"
            prop="extWarrantyPeriod"
            min-width="120"
            show-overflow-tooltip
          />
          <el-table-column
            align="center"
            :label="$t('cusEntry.bidMod.extIsPrepaid')"
            prop="extIsPrepaid"
            min-width="120"
            show-overflow-tooltip
            :formatter="(row, column, cellValue) => cellValue ? $getDictLabel('YES_OR_NO', cellValue) : ''"
          />
          <el-table-column
            align="center"
            :label="$t('cusEntry.bidMod.extPrepaidRatio')"
            prop="extPrepaidRatio"
            min-width="120"
            show-overflow-tooltip
          />
          <el-table-column
            align="center"
            :label="$t('cusEntry.bidMod.extLogisticsCost')"
            prop="extLogisticsCost"
            min-width="120"
            show-overflow-tooltip
          />
          <!-- <el-table-column
            v-if="!$attrs.params.isMobile"
            align="center"
            :label="$t('cusEntry.bidMod.advancePayment')"
            prop="advancePaymentRemark"
            show-overflow-tooltip
            min-width="120"
            :formatter="(row) => {
              return row.advancePaymentRemark ? $getDictLabel('YES_OR_NO', row.advancePaymentRemark) : ''
            }"
          /> -->
          <el-table-column
            align="center"
            :label="$t('cusEntry.bidMod.quoter')"
            prop="extOrderByNickname"
            min-width="120"
            show-overflow-tooltip
          />
        </el-table>
      </el-collapse-item>
    </el-collapse>
    <!--批量修改付款方式&付款条件-->
    <PaymethodOrPayTermDialog
      :visible.sync="paymethodVisible"
      @updateRow="updateRow"
    />
    <!--询价弹窗 -->
    <InquiryDialog
      :visible.sync="inquiryVisible"
      @setMaterialDetail="setMaterialDetail"
    />
    <!-- 近期采购 -->
    <RecentPruchaseDialog
      :visible.sync="recentPurchaseVisible"
      @setMaterialDetail="setMaterialDetail"
    />
  <!-- </ApprovalProcess> -->
  </CWorkflowMulti>
</template>

<script>
import CToolbar from 'lib@/components/c-toolbar'
import { tabTodoMixin } from '@/utils/mixins'
import WorkflowCommon from '@/library/mixins/workflow-common'
import InquiryDialog from './components/inquiry-dialog'
import RecentPruchaseDialog from './components/recent-purchase-dialog'
import PaymethodOrPayTermDialog from './components/paymethod-or-payTerm-dialog'
import { inqBuyerHttp } from 'modcb@/inquiry/api'
import QuickSearch from 'lib@/components/QuickSearch'
import CPagination from 'lib@/components/c-pagination'
import ApprovalProcess from 'modc@/components/approval-process'
import { downloadFileLink } from 'lib@/utils/file'

export default {
  name: 'PriceOrderDetail',
  components: {
    CToolbar,
    InquiryDialog,
    RecentPruchaseDialog,
    QuickSearch,
    CPagination,
    PaymethodOrPayTermDialog,
    ApprovalProcess
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      businessId: null,
      activeNames: ['1', '2', '3'],
      materialDetail: [],
      baseInfo: {
        fixPriceHeadId: null,
        fixPriceNo: null,
        dc: 'N',
        extFixPriceType: null,
        fixPriceDate: this.getNowDate(),
        fixPriceStatus: 'DRAFT',
        orgDepName: this.$store.getters.userInfo.department,
        orgDepCode: `HR_${this.$store.getters.userInfo.ceeaDeptId}`
      },
      rules: {
        fixPriceDate: [{ required: true, message: this.$t('cusEntry.tipMessage.applyDateMsg') }],
        orgDepName: [{ required: true, message: this.$t('cusEntry.tipMessage.purchaseDepartmentMsg') }],
        extFixPriceType: [{ required: true, message: this.$t('components.approvalHead.headers.selectNode') }],
        dc: [{ required: true, message: this.$t('cusEntry.tipMessage.purchaseOnBehalfMsg') }]
      },
      supplierQuoteDetail: [],
      fileList: [],
      inquiryVisible: false,
      recentPurchaseVisible: false,
      paymethodVisible: false,
      currentCloseRow: {},
      pagination: {
        pageSize: 30,
        pageNum: 1
      },
      operationPreOptions: {
        nextStep: this.preNextStepHandler
      }
    }
  },
  computed: {
    isReadOnly () {
      return ['view', 'approve'].includes(this.$attrs.params.type)
    },
    viewUpdateButton () { // 用来控制保存、提交按钮是否可见。如果自定义按钮则无需添加
      return ['add', 'edit'].includes(this.$attrs.params.type)
    },
    workflowBusinessId () { // 用来指定工作流的业务ID
      return this.$attrs.params.row.fixPriceHeadId || this.baseInfo.fixPriceHeadId
    },
  },
  created () {
    if (this.$attrs.params.type !== 'add') {
      const id = this.$attrs.params.row.fixPriceHeadId
      this.businessId = id
      this.getDetail(id)
    }
    this.buttonConfigInfo.save.view = this.viewUpdateButton
    this.buttonConfigInfo.submit.view = this.viewUpdateButton
    this.buttonConfigInfo.cancel.view = this.$attrs.params.type != 'view'
    this.buttonConfigInfo.close.view = this.$attrs.params.type == 'view'
  },
  methods: {
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'EXT_SOU_FIX_PRICE'
    },
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    // 定义流程额外变量，如果没有就不用添加这个函数
    async getWorkflowBusinessVariables () {
     const procTitleObj = {
      fixPriceNo: this.baseInfo.fixPriceNo
     }
      return {
        ...procTitleObj,
        procTitleObj,
        formData: procTitleObj
      }
    },
    // 禁用全选
    selectAll () {
      this.$refs.materialTable.clearSelection()
    },
    // 导出明细
    exportDetail () {
      downloadFileLink(
        `/api-sou/npm/fix-price/buyer/exportFixPriceDetail?fixPriceHeadId=${this.baseInfo.fixPriceHeadId}`,
        this.$t('cusEntry.inq.priceLineDetail')
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    },
    // 下一步前置处理
    async preNextStepHandler () {
      let validResult = true
      this.$refs.baseInfo.validate(async valid => {
        if (valid) {
          /* 校验明细行必填 */
          if (this.materialDetail.length === 0) {
            this.$message.warning(this.$t('cusEntry.tipMessage.aLeastOneMaterial'))
            validResult = false
          }
        } else {
          this.$message.warning(this.$t('cusEntry.tipMessage.required'))
          validResult = false
        }
      })
      if (validResult) {
        const submitParams = {
          ...this.baseInfo,
          fileList: this.fileList,
          lineList: this.materialDetail,
          tempSave: true
        }
        const { data } = await inqBuyerHttp.price.save(submitParams)
        this.businessId = data?.fixPriceHeadId
        this.baseInfo.fixPriceNo = data?.fixPriceNo
        if (!this.businessId) {
          this.$message.warning(this.$t('cusEntry.tipMessage.businessIdIsNotExit'))
          return false
        }
        if (data.checkCode === '1') {
          // const confirm = await this.$confirm(data.checkMsg, '提示', {
          //   confirmButtonText: '确定',
          //   cancelButtonText: '取消',
          //   type: 'warning',
          //   showClose: false,
          //   showCancelButton: false
          // })
          const confirm = await this.$confirm(data.checkMsg, this.$t('components.approvalHead.tips.tip'), {
            confirmButtonText: this.$t('common.confirm'),
            cancelButtonText: this.$t('common.cancel'),
            type: 'warning',
            showClose: false,
            showCancelButton: false
          })
        }
        await this.getDetail(data.fixPriceHeadId)
        this.handlerAfter('SUBMIT')
      }
      // 调用暂存接口
      return validResult
    },
    // 审批流操作回调
    approvalHandlerCallback (type) {
      switch (type) {
      case 'save':
        this.$refs.baseInfo.validate(valid => {
          if (valid) {
            const submitParams = {
              ...this.baseInfo,
              fileList: this.fileList,
              lineList: this.materialDetail,
              tempSave: type === 'save'
            }
            inqBuyerHttp.price.save(submitParams).then(async res => {
              this.$message.success(this.$t('common.successSave'))
              this.businessId = res.data.fixPriceHeadId
              this.getDetail(res.data.fixPriceHeadId)
            })
          } else {
            this.$message.warning(this.$t('cusEntry.tipMessage.required'))
          }
        })
        break
      case 'submit':
        this.back()
        break
      case 'abandon':
        this.back()
        break
      case 'recall':
        this.getDetail(this.businessId)
        break
      case 'pass':
        this.getDetail(this.businessId)
        break
      default:
        break
      }
    },
    /* 更新付款方式&付款条款 */
    updateRow (data) {
      const {
        paymentMethod,
        paymentTerm
      } = data
      this.materialDetail.forEach(item => {
        item.paymentMethod = paymentMethod || item.paymentMethod
        item.paymentTerm = paymentTerm || item.paymentTerm
      })
    },
    /* 页码变更 */
    paginationCurrentChange (pageNum) {
      // 确保页码 >=1 且 <= 最大页
      const maxPage = Math.max(Math.ceil(this.materialDetail.length / this.pagination.pageSize), 1)
      this.pagination.pageNum = Math.min(Math.max(pageNum, 1), maxPage)
    },
    /* 页条数变更 */
    paginationSizeChange (pageSize) {
      const oldPageNum = this.pagination.pageNum
      this.pagination.pageSize = pageSize

      // 计算新的最大页码
      const sourceLength = this.materialDetail.length
      const maxPage = Math.max(Math.ceil(sourceLength / pageSize), 1)

      // 自动修正页码逻辑
      if (sourceLength === 0) {
        this.pagination.pageNum = 1 // 无数据时强制为第1页
      } else {
        // 计算旧页码在新 pageSize 下对应的近似位置
        const approximateIndex = (oldPageNum - 1) * this.pagination.pageSize
        this.pagination.pageNum = Math.ceil((approximateIndex + 1) / pageSize)
        // 确保不超过最大页
        this.pagination.pageNum = Math.min(this.pagination.pageNum, maxPage)
      }
    },
    /* 获取当前日期 */
    getNowDate () {
      const nowDate = new Date()
      const year = nowDate.getFullYear()
      const month = (nowDate.getMonth() + 1).toString().padStart(2, '0')
      const day = (nowDate.getDate()).toString().padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    /* 设置行类名 */
    setRowClass ({ row }) {
      if (Number(row.extOrderCount) < 3) {
        return 'light-blue'
      }
    },
    /* 设置表头样式 */
    leftHeaderStyle ({ columnIndex }) {
      if (columnIndex === 0) {
        return 'set-all-select-btn'
      }
    },
    /* 获取行对应的报价明细 */
    handleCurrentChange (currentRow) {
      inqBuyerHttp.price.getQuoteDetail(currentRow.sourceFromLineId).then(res => {
        if (res.data) {
          this.supplierQuoteDetail = res.data || []
          setTimeout(() => {
            this.$refs.materialTable.toggleRowSelection(currentRow, true)
          }, 100)
        }
      })
    },
    /* 获取明细 */
    setMaterialDetail (data) {
      const {
        selectRows,
        type
      } = data
      /* 获取已经存在的数据唯一标识sourceFromLineId */
      const sourceFromLineIdList = this.materialDetail.map(item => item.sourceFromLineId)
      let selectList = []
      if (type === 'INQ') {
        selectList = selectRows.map(item => ({
          notaxPrice: item.standardNotaxPrice,
          quantity: item.requireQuantity,
          notaxTotalPrice: item.standardNotaxTotalPrice,
          hasSignedContract: 'N',
          sourceFromLineId: item.orderItemId,
          paymentMethod: '2',
          paymentTerm: 'NET45',
          sourceFromNo: item.souNo,
          currencyCode: item.orderCurrency,
          ...item
        }))
      } else {
        selectList = selectRows.map(({
          unitCode,
          unit,
          materialCode,
          materialName,
          materialId,
          requirementQuantity,
          requirementLineId,
          ceeaPrType,
          sourceFromLineId,
          extInvoiceType,
          extWarrantyPeriod,
          extAdvancePaymentRemark,
          ceeaPerformUserNickname,
          buyerNickname,
          ...other
        }) => ({
          itemCode: materialCode,
          itemDesc: materialName,
          itemId: materialId,
          unit: unitCode,
          applyType: ceeaPrType,
          hasSignedContract: 'N',
          quantity: requirementQuantity,
          sourceFromLineId: requirementLineId,
          paymentMethod: '2',
          paymentTerm: 'NET45',
          invoiceType: extInvoiceType,
          extWarrantyPeriod,
          advancePaymentRemark: extAdvancePaymentRemark,
          buyerNickname: ceeaPerformUserNickname,
          ...other
        }))
      }
      selectList.forEach(item => {
        if (!sourceFromLineIdList.includes(item.sourceFromLineId)) {
          this.materialDetail.push(item)
        }
      })
    },
    /* 附件上传成功 */
    handleUploadSuccess (fileList) {
      this.fileList = fileList?.map(item => ({
        fileId: item.fileId,
        fileName: item.fileName
      })) || []
    },
    /* 删除行 */
    deleteRow (index) {
      const {
        pageNum,
        pageSize
      } = this.pagination
      const realIndex = (pageNum - 1) * pageSize + index
      this.materialDetail.splice(realIndex, 1)
      // 计算当前数据长度和最大页码
      const sourceLength = this.materialDetail.length
      const maxPage = Math.max(Math.ceil(sourceLength / pageSize), 1) // 最小为 1

      // 自动修正页码
      if (sourceLength === 0) {
        // 数据为空时强制页码为 1
        this.pagination.pageNum = 1
      } else if (pageNum > maxPage) {
        // 当前页码超过最大页时，回退到最后一页
        this.pagination.pageNum = maxPage
      }
    },
    /* 关闭行 */
    closeRow (row) {
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
          sourceFromLineId: row.sourceFromLineId,
          sourceFromType: row.sourceFromType,
          fixPriceLineId: row.fixPriceLineId,
          closeReason: value
        }
        inqBuyerHttp.price.close(data).then(res => {
          this.$message.success(this.$t('cusEntry.tipMessage.closeSuccess'))
          if (this.$attrs.params.type !== 'add') {
            const id = this.$attrs.params.row.fixPriceHeadId
            this.getDetail(id)
          }
        })
      })
    },
    /* 提交保存 */
    saveOrSubmit (type) {
      if (type === 'SUBMIT') {
        this.preNextStepHandler()
      } else {
        this.approvalHandlerCallback('save')
      }
    },
    /* 获取详情 */
    getDetail (id) {
      inqBuyerHttp.price.getDetail(id).then(async res => {
        if (res.data) {
          const {
            lineList,
            fileList,
            ...baseInfo
          } = res.data
          this.fileList = fileList
          this.baseInfo = baseInfo

          const materialIds = lineList.map(item => item.itemId)
          const response = await this.$http({
            url: '/api-base/material/materialItem/ext/multilingual',
            method: 'POST',
            data: { materialIds, language: this.$i18n.locale },
            loading: true
          })
          const materialDetail = lineList.map(item => {
            const data = response.data.find(it => it.materialId === item.itemId)
            return {
              ...item,
              materialNameShow: data?.materialName,
              extMaterialModelShow: data?.extMaterialModel
            }
          })
          this.materialDetail = materialDetail
        }
      })
    },
    // 设置行类名
    setRowClassQuote ({ row }) {
      if (row.selectStatus === 'WIN') {
        return 'light-blue'
      }
    },
    /* 获取部门 */
    getDepartment (node) {
      const {
        organizationId,
        organizationName,
        organizationCode
      } = node || {}
      this.baseInfo.orgDepName = organizationName
      this.baseInfo.orgDepCode = organizationCode
      this.baseInfo.orgDepId = organizationId
    },
    back () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('PriceOrderList.getQueryData')
    }
  }
}
</script>
<style lang="scss">
.light-blue {
  background-color: #f4b7b8 !important;
}
.hidden-default-background .el-table__body .light-blue.hover-row td {
  background-color: #f4b7b8 !important;
}
.set-all-select-btn .cell {
  visibility: hidden;
}
</style>
