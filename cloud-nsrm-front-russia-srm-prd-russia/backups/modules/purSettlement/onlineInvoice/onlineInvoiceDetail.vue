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
        @click-handler="(type) => saveHandle(type)"
        @submit-direct="(type) => saveHandle(type)"
        @confirm="(type, comment) => saveHandle(type, comment)"
        @close-tab="backTo"
      >
        <div class="form-container2">
          <el-form
            ref="invoiceNoticeForm"
            :model="form"
            :rules="formRules"
            label-position="top"
            class="form-incontainer"
            :disabled="curOpt=='view'"
          >
            <el-collapse
              v-model="activeDims"
              class="tab-form-style"
            >
              <el-collapse-item
                :title="$t('purSettlementMod.invoiceNoticeInfo')"
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
                      >
                        <el-option
                          v-for="item in siteOptions"
                          :key="item.vendorSiteId"
                          :label="item.vendorSiteCode"
                          :value="item.vendorSiteCode"
                        />
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :span="6">
                    <el-form-item :label="$t('purSettlementMod.onlineInvoiceNum')">
                      <el-input
                        v-model="form.onlineInvoiceNum"
                        disabled
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="6">
                    <el-form-item :label="$t('purSettlementMod.taxationInvoiceNum')">
                      <el-input
                        v-model="form.taxInvoiceNum"
                        disabled
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="6">
                    <el-form-item :label="$t('quota.currency')">
                      <DictSelect
                        v-model="form.currencyCode"
                        code="currency"
                        disabled
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="6">
                    <el-form-item :label="$t('bid_mod.priceTax')">
                      <el-input
                        v-model="form.exchangeRate"
                        v-input-format="{ type: 'float' }"
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
                        disabled
                        value-format="yyyy-MM-dd"
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="6">
                    <el-form-item :label="$t('purSettlementMod.actualInvoiceAmount')">
                      <el-input
                        v-model="form.actualInvoiceAmountY"
                        disabled
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="6">
                    <el-form-item :label="$t('purSettlementMod.invoiceTaxAmount')">
                      <el-input
                        v-model="form.invoiceTax"
                        disabled
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="6">
                    <el-form-item :label="$t('purSettlementMod.invoiceNetAmount')">
                      <el-input
                        v-model="form.actualInvoiceAmountN"
                        disabled
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="6">
                    <el-form-item :label="$t('purSettlementMod.includeTaxAmountSys')">
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
                        clearable
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
                        clearable
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
                      <!-- <el-input v-model="form.businessType"/>  -->
                      <DictSelect
                        v-model="form.businessType"
                        code="BUSINESS_TYPE"
                        filterable
                        clearable
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="6">
                    <el-form-item
                      :label="$t('quota.ifPaperAttach')"
                      prop="ifPaperAttach"
                    >
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
                    <el-form-item
                      :label="$t('flowMod.approvers')"
                      prop="approverNickname"
                    >
                      <quick-search
                        :show-input="form.approverNickname"
                        show-key="nickname"
                        :scope-data="form"
                        name="scc_rbac_user_display"
                        @close-quicksearch="getUserObj"
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="6">
                    <el-form-item
                      :label="$t('flowMod.approvDept')"
                      prop="approverDept"
                    >
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
                    <el-form-item
                      :label="$t('purSettlementMod.abstract')"
                      prop="comment"
                    >
                      <el-input
                        v-model="form.comment"
                        type="textarea"
                        :autosize="{ minRows: 2, maxRows: 5}"
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="24">
                    <el-form-item
                      :label="$t('vendorMod.loggerCommentSubmit')"
                      prop="drafterView"
                    >
                      <el-input
                        v-model="form.drafterView"
                        type="textarea"
                        :autosize="{ minRows: 2, maxRows: 5}"
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
                    type="index"
                    width="50"
                    :label="$t('common.addShoppingCart')"
                  />
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
                    prop="contractCode"
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
                    prop="comment"
                    :label="$t('purchaseDemand.comments')"
                    width="150"
                    show-overflow-tooltip
                  >
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.comment" />
                    </template>
                  </el-table-column>
                  <el-table-column
                    v-if="form.invoiceStatus !=='UNDER_APPROVAL'"
                    :label="$t('common.operation')"
                    width="60"
                    fixed="right"
                  >
                    <template slot-scope="scope">
                      <el-button
                        type="text"
                        @click="delInvoiceDetails(scope.$index, scope.row)"
                      >
                        {{ $t("common.delete") }}
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </el-collapse-item>
              <el-collapse-item
                :title="$t('purSettlementMod.deductionRebateDetails')"
                name="3"
              >
                <p
                  v-if="curRole==='BUYER'"
                  class="btn_line"
                >
                  <el-button
                    type="primary"
                    class="detail-pbtn"
                    @click="addOnlineInvoicePunishes"
                  >
                    {{ $t('common.add') }}
                  </el-button>
                </p>
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
                    :label="$t('perfMod.assessmentType')"
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
                <!-- 弹框区域 dialogFormVisiblePunishes-->
                <srm-dialog
                  :title="$t('purSettlementMod.supplierAssessmentDetails')"
                  size="large"
                  :visible.sync="dialogFormVisiblePunishes"
                  :close-on-click-modal="false"
                >
                  <div>
                    <el-form
                      ref="filterFormPunishes"
                      :model="filterFormPunishes"
                      label-width="80px"
                      label-position="top"
                      class="form-incontainer"
                    >
                      <el-row type="flex">
                        <el-col>
                          <el-form-item
                            :label="$t('purSettlementMod.orderNumber')"
                            :label-width="formLabelWidth"
                          >
                            <el-input v-model="filterFormPunishes.orderNumber" />
                          </el-form-item>
                        </el-col>
                        <el-col>
                          <el-form-item
                            :label="$t('purchaseDemand.requestDateFrom')"
                            :label-width="formLabelWidth"
                          >
                            <el-date-picker v-model="filterFormPunishes.startDate" />
                          </el-form-item>
                        </el-col>
                        <el-col>
                          <el-form-item
                            :label="$t('purchaseDemand.requestDateTo')"
                            :label-width="formLabelWidth"
                          >
                            <el-date-picker v-model="filterFormPunishes.endDate" />
                          </el-form-item>
                        </el-col>
                        <el-col>
                          <el-form-item
                            :label="$t('common.materialCode')"
                            :label-width="formLabelWidth"
                          >
                            <el-input v-model="filterFormPunishes.materialCode" />
                          </el-form-item>
                        </el-col>
                      </el-row>
                    </el-form>
                    <p>
                      <el-button
                        type="primary"
                        @click="queryPunishes"
                      >
                        {{ $t("common.search") }}
                      </el-button>
                      <el-button
                        type="primary"
                        @click="addConfirmPunishes"
                      >
                        {{ $t("common.save") }}
                      </el-button>
                    </p>
                  </div>
                  <el-table
                    :data="displayPunishes"
                    style="width: 100%"
                    border
                    height="345px"
                    highlight-current-row
                    @selection-change="punishesHandleSelectionChange"
                    @row-dblclick="punishesHandleDBClick"
                  >
                    <el-table-column
                      type="selection"
                      width="55"
                      fixed="left"
                    />
                    <el-table-column
                      align="center"
                      type="index"
                      width="50"
                    />
                    <el-table-column
                      prop="assessmentNo"
                      :label="$t('perfMod.assessmentNo')"
                      width="120"
                      show-overflow-tooltip
                    />
                    <el-table-column
                      prop="assessmentDate"
                      :label="$t('perfMod.assessmentDate')"
                      width="100"
                      show-overflow-tooltip
                    />
                    <el-table-column
                      prop="ceeaAssessmentType"
                      :label="$t('perfMod.assessmentType')"
                      width="100"
                      :formatter="filterCatHandler"
                      show-overflow-tooltip
                    />
                    <el-table-column
                      prop="actualAssessmentAmountN"
                      :label="$t('purSettlementMod.actualAssessmentAmountN')"
                      width="170"
                      show-overflow-tooltip
                    />
                    <el-table-column
                      prop="tax"
                      :label="$t('contractMod.taxQuota')"
                      width="100"
                      show-overflow-tooltip
                    />
                    <el-table-column
                      prop="actualAssessmentAmountY"
                      :label="$t('purSettlementMod.actualAssessmentAmountY')"
                      width="150"
                      show-overflow-tooltip
                    />
                    <el-table-column
                      prop="currencyCode"
                      :label="$t('quota.currency')"
                      :formatter="filterCurrency"
                      width="100"
                      show-overflow-tooltip
                    />
                    <el-table-column
                      prop="ceeaAssessmentTitle"
                      :label="$t('bidMod.title')"
                      min-width="150"
                      show-overflow-tooltip
                    />
                    <el-table-column
                      prop="vendorName"
                      :label="$t('common.vendorName')"
                      min-width="150"
                      show-overflow-tooltip
                    />
                    <el-table-column
                      prop="categoryName"
                      :label="$t('bidMod.categoryName')"
                      width="120"
                      show-overflow-tooltip
                    />
                    <el-table-column
                      prop="materialCode"
                      :label="$t('common.materialCode')"
                      width="100"
                      show-overflow-tooltip
                    />
                    <el-table-column
                      prop="materialName"
                      :label="$t('common.materialName')"
                      min-width="150"
                      show-overflow-tooltip
                    />
                    <el-table-column
                      prop="comment"
                      :label="$t('common.remark')"
                      min-width="150"
                      show-overflow-tooltip
                    />
                  </el-table>
                  <c-pagination
                    :total="punishesPage.total"
                    :page-num="punishesPage.pageNum"
                    :page-size="punishesPage.pageSize"
                    @current-change="punishesCurrentChange"
                    @size-change="punishesSizeChange"
                  />
                </srm-dialog>
              </el-collapse-item>
              <el-collapse-item
                :title="$t('purSettlementMod.prepayApplyDetails')"
                name="4"
              >
                <p class="btn_line">
                  <el-button
                    type="primary"
                    class="detail-pbtn"
                    @click="openAdvanceItem"
                  >
                    {{ $t('common.add') }}
                  </el-button>
                </p>
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
                    prop="contractCode"
                    :label="$t('contractMod.contractCode')"
                    min-width="150"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="projectNum"
                    :label="$t('bidMod.bidingNum')"
                    width="120"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="projectName"
                    :label="$t('bidMod.bidingName')"
                    min-width="150"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="businessType"
                    :label="$t('bidMod.businessType')"
                    :formatter="filterBusinessType"
                    width="100"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    align="center"
                    prop="currencyName"
                    :label="$t('quota.currency')"
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
                  >
                    <template slot-scope="scope">
                      <el-input
                        v-model="scope.row.chargeOffAmount"
                        type="number"
                        @input="setRowAmount(scope.row)"
                      />
                    </template>
                  </el-table-column>
                  <el-table-column
                    :label="$t('common.operation')"
                    width="60"
                    fixed="right"
                  >
                    <template slot-scope="scope">
                      <el-button
                        type="text"
                        @click="delonlineInvoiceAdvances(scope.$index, scope.row)"
                      >
                        {{ $t("common.delete") }}
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <!-- 弹框区域-->
                <srm-dialog
                  :title="$t('purSettlementMod.selPrepayApplyDetails')"
                  size="large"
                  :visible.sync="dialogFormVisible"
                  :close-on-click-modal="false"
                >
                  <div>
                    <el-form
                      ref="filterForm"
                      :model="filterForm"
                      label-width="80px"
                      label-position="top"
                      class="form-incontainer the_filter_form"
                    >
                      <el-row type="flex">
                        <el-col>
                          <el-form-item
                            :label="$t('quota.org')"
                            :label-width="formLabelWidth"
                          >
                            <organization-selector
                              ref="organizationSelector12"
                              v-model="filterForm.orgId"
                              :parent-id="-1"
                              node-type="OU"
                              disabled
                            />
                          </el-form-item>
                        </el-col>
                        <el-col>
                          <el-form-item
                            :label="$t('common.vendor')"
                            :label-width="formLabelWidth"
                          >
                            <el-input
                              v-model="filterForm.vendorName"
                              disabled
                            />
                          </el-form-item>
                        </el-col>
                        <el-col>
                          <el-form-item
                            :label="$t('purchaseDemand.vendorSite')"
                            :label-width="formLabelWidth"
                          >
                            <el-select
                              v-model="filterForm.costTypeCode"
                              disabled
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
                        <el-col><p /></el-col>
                      </el-row>
                      <el-row type="flex">
                        <el-col>
                          <el-form-item
                            :label="$t('contractMod.startAcceptDate')"
                            :label-width="formLabelWidth"
                          >
                            <el-date-picker
                              v-model="filterForm.applyStartDate"
                              type="date"
                              format="yyyy-MM-dd"
                              value-format="yyyy-MM-dd"
                            />
                          </el-form-item>
                        </el-col>
                        <el-col>
                          <el-form-item
                            :label="$t('contractMod.endAcceptDate')"
                            :label-width="formLabelWidth"
                          >
                            <el-date-picker
                              v-model="filterForm.applyEndDate"
                              type="date"
                              format="yyyy-MM-dd"
                              value-format="yyyy-MM-dd"
                            />
                          </el-form-item>
                        </el-col>
                        <el-col>
                          <el-form-item
                            :label="$t('purSettlementMod.advanceApplyNum')"
                            :label-width="formLabelWidth"
                          >
                            <el-input v-model="filterForm.advanceApplyNum" />
                          </el-form-item>
                        </el-col>
                        <el-col><p /></el-col>
                      </el-row>
                      <el-row type="flex">
                        <el-col>
                          <el-form-item
                            :label="$t('contractMod.contractNo')"
                            :label-width="formLabelWidth"
                          >
                            <quick-search
                              :show-input="filterForm.contractNo"
                              show-key="contractNo"
                              :scope-data="filterForm"
                              name="scc_contract_head"
                              @close-quicksearch="getcontractObj"
                            />
                          </el-form-item>
                        </el-col>
                        <el-col>
                          <el-form-item
                            :label="$t('contractMod.itemNumber')"
                            :label-width="formLabelWidth"
                          >
                            <el-input v-model="filterForm.projectNum" />
                          </el-form-item>
                        </el-col>
                        <el-col>
                          <el-form-item
                            :label="$t('purSettlementMod.paymentPlanStatus')"
                            :label-width="formLabelWidth"
                          >
                            <DictSelect
                              v-model="filterForm.advanceApplyStatus"
                              code="INVOICE_STATUS"
                              disabled
                            />
                          </el-form-item>
                        </el-col>
                        <el-col>
                          <p style="padding:8px;">
                            <el-button
                              type="primary"
                              @click="queryItemList"
                            >
                              {{ $t("common.search") }}
                            </el-button>
                            <!-- <el-button @click="resetFilterForm">{{$t("common.reset") }}</el-button> -->
                            <el-button
                              type="primary"
                              @click="addOneItem"
                            >
                              {{ $t("common.confirm") }}
                            </el-button>
                          </p>
                        </el-col>
                      </el-row>
                    </el-form>
                  </div>
                  <el-table
                    :data="displayMaterialItem"
                    style="width: 100%"
                    border
                    height="345px"
                    highlight-current-row
                    @selection-change="handleSelectionChange2"
                    @row-dblclick="handleItemDBClick"
                  >
                    <el-table-column
                      type="selection"
                      width="55"
                    />
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
                      prop="contractNo"
                      :label="$t('contractMod.contractNo')"
                      min-width="150"
                      show-overflow-tooltip
                    />
                    <el-table-column
                      align="center"
                      prop="projectNum"
                      :label="$t('contractMod.itemNumber')"
                      width="150"
                      show-overflow-tooltip
                    />
                    <el-table-column
                      align="center"
                      prop="projectName"
                      :label="$t('contractMod.itemName')"
                      min-width="150"
                      show-overflow-tooltip
                    />
                    <el-table-column
                      align="center"
                      prop="businessType"
                      :label="$t('bidMod.businessType')"
                      width="100"
                      :formatter="filterBusinessType"
                      show-overflow-tooltip
                    />
                    <el-table-column
                      align="center"
                      prop="currencyName"
                      :label="$t('quota.currency')"
                      width="100"
                      show-overflow-tooltip
                    />
                    <el-table-column
                      align="center"
                      prop="applyPayAmount"
                      :label="$t('purSettlementMod.applyAmount')"
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
                  </el-table>
                  <c-pagination
                    style="margin:3px"
                    :total="parentOrgTableDataPage.total"
                    :page-num="parentOrgTableDataPage.pageNum"
                    :page-size="parentOrgTableDataPage.pageSize"
                    @current-change="parentDataCurrentChange"
                    @size-change="parentDataSizeChange"
                  />
                </srm-dialog>
              </el-collapse-item>
              <el-collapse-item
                :title="$t('quota.fileInfo')"
                name="5"
              >
                <p class="btn_line">
                  <el-button
                    type="primary"
                    class="detail-pbtn"
                    @click="addFileuploads"
                  >
                    {{ $t('common.add') }}
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
        <!--      <c-toolbar v-if="curOpt!=='view'">-->
        <!--        <template slot="right">-->
        <!--           <el-button @click="backTo">{{ $t("common.cancel") }}</el-button>-->
        <!--          <el-button type="primary" @click="resetHandle" v-if="form.invoiceStatus ==='REJECTED' && poUnitPriceChangeFlag !=='Reset'">{{$t('common.reset')}}</el-button>-->
        <!--          <el-button type="primary" @click="saveHandle" v-if="!form.accountPayableDealine && !(form.invoiceStatus ==='REJECTED' && poUnitPriceChangeFlag !=='Reset') ">{{ $t("common.submit") }}</el-button>-->
        <!--          <el-button type="primary" @click="approveHandle" v-if="!!form.accountPayableDealine ">{{$t("purchaseDemand.confirm")}}</el-button>-->
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
import http from '@/utils/axios/http'
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
  mixins: [WorkflowCommon, tabTodoWatch, tabTodoMixin],
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
      isReadOnly: this.$attrs.params.flag == 'readOnly',
      curOrderId: '',
      form: {
        orgId: null,
        orgCode: '',
        orgName: '',
        taxRate: '',
        taxKey: '',
        vendorCode: '',
        vendorName: '',
        vendorId: '',
        currency: '',
        payAccountPeriodCode: '',
        payAccountPeriodName: '',
        invoiceTax: '',
        actualInvoiceAmountY: '',
        invoiceDate: parseTime(new Date(), '{y}-{m}-{d}'),
        statementTotalAmount: null,
        invoiceTotalAmount: null,
        remark: '',
        sourceNumber: '',
        onlineInvoiceId: '',
        invoiceStatus: 'DRAFT',
        ifPaperAttach: '',
        bpcount: '',
        unPaidAmount: '',
        paidAmount: ''
      },
      siteOptions: [],
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
        contractNo: null,
        materialCode: null,
        materialName: null,
        orgName: null,
        categoryCode: null,
        organizationId: null,
        orderNumber: null,
        applyStartDate: null,
        applyEndDate: null,
        'orderStatus': 'ACCEPT'
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
        'orderStatus': 'ACCEPT'
      },
      formRules: {
        orgId: [{ required: true, message: this.$t('quota.orgIdTips') }],
        vendorName: [{ required: true, message: this.$t('quota.vendorTips') }],
        ceeaCostType: [{ required: true, message: this.$t('quota.costTypeTips') }],
        invoiceDate: [{ required: true, message: this.$t('purSettlementMod.invoiceDateTips') }],
        currency: [{ required: true, message: this.$t('vendorMod.msgCurrencyCode') }],
        ifPaperAttach: [{ required: true, message: this.$t('quota.ifPaperAttachTips') }],
        approverNickname: [{ required: true, message: this.$t('flowMod.msgApprovers') }]
        // approverDept: [{ required: true, message: this.$t("flowMod.msgApprovDept") }],
      },
      loading: false,
      sourceNumber: [], // 来源单号ID
      tempSourceNumber: [], // 来源单号临时
      orgList: [{
        organizationId: '6803628383141888',
        organizationCode: '10102',
        organizationName: '生活电器事业部'
      }], // 合作组织下拉
      vendorOptions: [], // 供应商下拉
      unitList: [], // 单位
      onlineInvoiceDetails: [], // 入库明细
      onlineInvoicePunishes: [], // 退货明细
      onlineInvoiceAdvances: [], // 预付申请明细
      fileuploads: [],
      curOpt: 'add',
      isModify: false,
      activeDims: ['1', '2', '3', '4', '5'],
      selectOrderData: [],
      rejectedDialog: false,
      rejectedModel: { // 驳回信息
        rejectedForm: {
          rejectReason: ''
        },
        rules: {
          rejectReason: [{ required: true, message: this.$t('bidMod.msgRejectReason') }]// 请输入驳回原因
        }
      },
      dialogFormVisiblePunishes: false,
      filterFormPunishes: {
        materialCode: null,
        materialName: null,
        orgName: null,
        categoryCode: null,
        organizationId: null,
        orderNumber: null,
        startDate: null,
        endDate: null,
        'orderStatus': 'ACCEPT'
      },
      displayPunishes: [],
      punishesQueryPage: {
        pageNum: 1,
        pageSize: 10
      },
      punishesPage: {
        total: 0,
        pageNum: 1,
        pageSize: 20
      },
      selectionPunishes: [],
      minusTax: null,
      minusTaxTotalAmount: null,
      minusNoTaxTotalAmount: null,
      invoiceStatusSave: null,
      poUnitPriceChangeFlag: null
    }
  },
  computed: {
    viewUpdateButton () { // 用来控制保存、提交按钮是否可见。如果自定义按钮则无需添加
      return this.curRole === 'BUYER' && !this.isReadOnly && this.form.invoiceStatus !== 'APPROVED'
      // return false;
    },
    viewUpdateButtonSave () {
      return false
    },
    disabledUpdateButton () {
      return this.form.invoiceStatus === 'IMPORTED' || this.form.invoiceStatus === 'APPROVING'
    },
    workflowBusinessId () { // 用来指定工作流的业务ID
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
    // console.log("store",this.$store.getters.userType)
    // console.log("poUnitPriceChangeFlag",this.$attrs.params.row.poUnitPriceChangeFlag)
    // this.invoiceStatusSave = this.$attrs.params.row.invoiceStatus || ""
    this.curOpt = this.$attrs.params.flag
    this.rolePermissions = this.userInfo.rolePermissions[0].roleCode // 通过这个角色的code去判断如果在角色设置里面修改的话，程序要对应修改
    this.curOrderId = this.$attrs.params.onlineInvoiceId
    this.getOrderFormDetail() // 查询

    this.buttonConfigInfo.save.view = this.viewUpdateButtonSave
    this.buttonConfigInfo.submit.view = this.viewUpdateButton
    this.buttonConfigInfo.cancel.view = !this.isReadOnly
    this.buttonConfigInfo.close.view = this.isReadOnly
  },
  methods: {
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'onlineInvoice'
    },
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },

    getSiteInfo2 (val) {
      this.$http({
        url: '/api-sup/info/siteInfo/listSiteInfoByParam',
        method: 'POST',
        data: {
          'companyId': val,
          'pageNum': 1,
          'pageSize': 9999
        },
        loading: true
      }).then(data => {
        if (data && data.data) {
          this.siteOptions = data.data
        }
      })
        .catch(err => {
          console.log(err)
        })
    },
    // 重置
    resetHandle () {
      this.$http({
        url: '/api-sup-ce/ps/invoice/onlineInvoice/restart',
        method: 'GET',
        params: { onlineInvoiceId: this.$attrs.params.onlineInvoiceId },
        loading: true
      }).then(res => {
        if (res) {
          console.log('res', res)
          this.getOrderFormDetail()
        }
      })
    },
    // 查询单据详情
    getOrderFormDetail () {
      this.$http({
        url: '/api-sup-ce/ps/invoice/onlineInvoice/get',
        method: 'GET',
        params: { onlineInvoiceId: this.$attrs.params.onlineInvoiceId },
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.form = res.data.onlineInvoice || {} // 头信息
          this.form.taxTotalAmount = Number(this.form.taxTotalAmount).toFixed(2)
          //  [start] by liwenhong
          let onlineInvoiceDetailsData = []
          onlineInvoiceDetailsData = res.data.onlineInvoiceDetails
          for (let i = 0; i < onlineInvoiceDetailsData.length; i++) {
            onlineInvoiceDetailsData[i].taxAmount = (Number(onlineInvoiceDetailsData[i].taxAmount) || 0).toFixed(2)
            onlineInvoiceDetailsData[i].tax = (Number(onlineInvoiceDetailsData[i].tax) || 0).toFixed(2)
            onlineInvoiceDetailsData[i].noTaxAmount = (Number(onlineInvoiceDetailsData[i].noTaxAmount) || 0).toFixed(2)
          }
          if (this.form.invoiceStatus === 'REJECTED') {
            let poUnitPriceChangeFlag = []
            poUnitPriceChangeFlag = onlineInvoiceDetailsData.filter(v => v.poUnitPriceChangeFlag === '')
            console.log('poUnitPriceChangeFlag', poUnitPriceChangeFlag)
            this.poUnitPriceChangeFlag = ''
            if (poUnitPriceChangeFlag.length !== 0) {
              this.poUnitPriceChangeFlag = 'Reset'
              console.log('poUnitPriceChangeFlagRrR', this.poUnitPriceChangeFlag)
            }
          }

          // poUnitPriceChangeFlag
          // [end] by liwenhong
          this.onlineInvoiceDetails = onlineInvoiceDetailsData// 入库明细
          this.onlineInvoicePunishes = res.data.onlineInvoicePunishes// 扣罚返利明细
          this.onlineInvoiceAdvances = res.data.onlineInvoiceAdvances// 预付申请明细
          this.fileuploads = res.data.fileuploads // 附件信息
          // 【审批人，部门】默认赋值
          /* if (this.$store.getters.user && this.$store.getters.user.userInfo) {
              const { userId,username,nickname, ceeaDeptId,ceeaEmpNo,department } = this.$store.getters.user.userInfo;
              this.form.approverId = userId;
              this.form.approverNickname = nickname;
              this.form.approverUsername = username;
              this.form.approverDept = department;
              this.form.approverDeptid = ceeaDeptId;
              this.form.approverEmpNo = ceeaEmpNo;
              this.form.ifPaperAttach = 'N';
            } */
          /* for(let item of this.onlineInvoiceAdvances) {
              //用来做计算的[可用金额]
              item['usableAmountLock'] = item['usableAmount'];
              item['chargeOffAmount'] = null;
            } */
          this.getSiteInfo2(res.data.onlineInvoice.vendorId)
          this.getBuObj(res.data.onlineInvoice.orgId, res.data.onlineInvoice.costTypeCode)
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
      // this.onlineInvoicePunishes.splice(index, 1);
      if (row.onlineInvoicePunishId) {
        this.$http({
          url: '/api-sup-ce/invoice/onlineInvoicePunish/batchDelete',
          method: 'POST',
          data: [row.onlineInvoicePunishId],
          loading: true
        }).then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.onlineInvoicePunishes.splice(index, 1)
          this.setTotal()
        })
      } else {
        this.onlineInvoicePunishes.splice(index, 1)
        this.setTotal()
      }
    },
    delonlineInvoiceAdvances (index, row) {
      if (row.onlineInvoiceAdvanceId) {
        this.$http({
          url: '/api-sup-ce/ps/invoice/onlineInvoiceAdvance/batchDelete',
          method: 'POST',
          data: [row.onlineInvoiceAdvanceId],
          loading: true
        }).then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.onlineInvoiceAdvances.splice(index, 1)
          if (this.onlineInvoiceAdvances.length === 0) {
            this.form.paidAmount = 0
            this.form.unPaidAmount = this.form.actualInvoiceAmountY - this.form.paidAmount
            return
          }
          // setTimeout(()=>{
          this.form.paidAmount = this.onlineInvoiceAdvances.map(v => v.chargeOffAmount).reduce((p, c) => ((Number(p) || 0) + (Number(c) || 0)))
          if (Math.abs(this.form.paidAmount) > Math.abs(this.form.actualInvoiceAmountY)) {
            return this.$message.warning(this.$t('purSettlementMod.msgArr[6]'))
          } else {
            this.form.unPaidAmount = this.form.actualInvoiceAmountY - this.form.paidAmount
          }
          // },100);
        })
      } else {
        debugger
        this.onlineInvoiceAdvances.splice(index, 1)
        if (this.onlineInvoiceAdvances.length === 0) {
          this.form.paidAmount = 0
          this.form.unPaidAmount = this.form.actualInvoiceAmountY - this.form.paidAmount
          return
        }
        // setTimeout(()=>{
        this.form.paidAmount = this.onlineInvoiceAdvances.map(v => v.chargeOffAmount).reduce((p, c) => ((Number(p) || 0) + (Number(c) || 0)))
        if (Math.abs(this.form.paidAmount) > Math.abs(this.form.actualInvoiceAmountY)) {
          return this.$message.warning(this.$t('purSettlementMod.msgArr[6]'))
        } else {
          this.form.unPaidAmount = this.form.actualInvoiceAmountY - this.form.paidAmount
        }
        // },100);
      }
    },
    outerHandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '', createdBy = '', creationDate = '' } = file
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
    // 驳回操作
    rejectedHandle () {
      this.rejectedDialog = true
    },
    getUserObj (val, scope) {
      scope.approverNickname = val ? val.nickname : ''
      scope.approverUsername = val ? val.username : ''
      scope.approverId = val ? val.userId : ''
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
    // 确认驳回
    rejectedComfirm () {
      this.$refs.rejectedForm.validate(valid => {
        let _this = this
        if (valid) {
          let parame = {}
          parame.onlineInvoiceId = this.curOrderId
          parame.rejectReason = _this.rejectedModel.rejectedForm.rejectReason
          if (this.rolePermissions === 'AccountSpecialist') { // 财务专员
            this.$api.pur.financeReject(parame).then(res => {
              _this.$message({
                message: res.message,
                type: 'success'
              })
              _this.rejectedDialog = false
              _this.backTo()
            })
          } else if (this.rolePermissions === 'Buyer') { // 财务采购员
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
    // 财务审批通过
    approveHandle () {
      if (!this.form.businessType) {
        return this.$message.warning(this.$t('quota.businessTypeTips'))
      }
      for (let row of this.onlineInvoiceAdvances) {
        // 用来做计算的[可用金额]
        if (row.chargeOffAmount > row.hangAccountAmount) {
          return this.$message.warning(this.$t('purSettlementMod.msgArr[9]'))
        }
      }
      if (Math.abs(this.form.paidAmount) > Math.abs(this.form.actualInvoiceAmountY)) {
        return this.$message.warning(this.$t('purSettlementMod.msgArr[6]'))
      }
      this.$refs.invoiceNoticeForm.validate(valid => {
        let _this = this
        if (valid) {
          http({
            url: '/api-sup-ce/ps/invoice/onlineInvoice/audit',
            method: 'POST',
            data: {
              onlineInvoice: this.form,
              onlineInvoiceDetails: this.onlineInvoiceDetails,
              onlineInvoicePunishes: this.onlineInvoicePunishes,
              onlineInvoiceAdvances: this.onlineInvoiceAdvances,
              fileuploads: this.fileuploads // 附件
            },
            loading: true
          }).then(res => {
            this.$message({
              message: res.message,
              type: 'success'
            })
            this.backTo()
          }).catch((err) => {
            console.log(err)
            this.form.budgetIgnore = 'Y'
          })
        }
      })
    },
    saveHandle (type) {
      if (!this.form.payAccountPeriodCode) {
        return this.$message.warning(this.$t('purSettlementMod.selectAccountingPeriod'))
      }
      for (let row of this.onlineInvoiceAdvances) {
        // 用来做计算的[可用金额]
        if (row.chargeOffAmount > row.hangAccountAmount) {
          return this.$message.warning(this.$t('purSettlementMod.msgArr[9]'))
        }
        if (row.chargeOffAmount > this.form.actualInvoiceAmountY) {
          return this.$message.warning(this.$t('purSettlementMod.msgArr[4]'))
        }
      }
      if (Math.abs(this.form.paidAmount) > Math.abs(this.form.actualInvoiceAmountY)) {
        return this.$message.warning(this.$t('purSettlementMod.msgArr[6]'))
      }
      if (!this.form.accountPayableDealine) {
        this.$confirm(this.$t('purSettlementMod.msgArr[8]'), {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        }).then(() => {
          //
          http({
            url: '/api-sup-ce/ps/invoice/onlineInvoice/saveTemporaryBeforeAudit',
            method: 'POST',
            data: {
              onlineInvoice: this.form,
              onlineInvoiceDetails: this.onlineInvoiceDetails,
              onlineInvoicePunishes: this.onlineInvoicePunishes,
              onlineInvoiceAdvances: this.onlineInvoiceAdvances,
              fileuploads: this.fileuploads // 附件
            },
            loading: true
          }).then(res => {
            this.$message({
              message: res.message,
              type: 'success'
            })
            this.getOrderFormDetail()
            // this.backTo();
          })
        }).catch(() => {})
      } else {
        http({
          url: '/api-sup-ce/ps/invoice/onlineInvoice/submit',
          method: 'POST',
          data: {
            onlineInvoice: this.form,
            onlineInvoiceDetails: this.onlineInvoiceDetails,
            onlineInvoicePunishes: this.onlineInvoicePunishes,
            onlineInvoiceAdvances: this.onlineInvoiceAdvances,
            fileuploads: this.fileuploads // 附件
          },
          loading: true
        }).then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.getOrderFormDetail()
          // this.backTo();
        })
      }
    },
    setPeriodObj (val) {
      let label = this.$getDictLabel('PAYMENT_PERIOD', val)
      this.form.payAccountPeriodName = label
    },
    selectHandler (node, value, scope) {
      this.form.orgId = node.organizationId
      this.form.orgCode = node.organizationCode
      this.form.orgName = node.organizationName
    },
    getBuObj (organizationId, costTypeCode) {
      this.$http({
        url: '/api-base/organization/organization/getBuByOrgId',
        method: 'GET',
        params: { organizationId: organizationId },
        loading: true
      })
        .then(data => {
          if (data.data && data.data.organizationCode) {
            this.$http({
              url: '/api-base/businessType/listPageByParam',
              method: 'POST',
              data: {
                divisionId: data.data.organizationCode,
                vendorSiteCode: costTypeCode,
                paymentDocumentType: 'INVOICE'
              },
              loading: true
            }).then(data => {
              this.businessTypeList = data.data.list.map((val) => {
                return {
                  value: val.businessType,
                  label: val.businessTypeName
                }
              })
            })
          }
        }).catch(err => {
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
      this.filterForm.orgId = this.form.orgId
      this.filterForm.vendorName = this.form.vendorName
      this.filterForm.costTypeCode = this.form.costTypeCode
      this.queryItemList()
    },
    queryItemList () {
      const data = { ...this.parentOrgQueryForm, ...this.filterForm }
      this.$http({
        url: '/api-sup-ce/ps/invoice/onlineInvoice/listPageAdvanceByParam',
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
      this.onlineInvoiceDetails = this.onlineInvoiceDetails.concat(this.selectionItem)
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
    addOneItem () {
      if (this.selectionItem2.length === 0) {
        return this.$message.warning(this.$t('common.msgSelectData'))
      }
      let advanceApplyHeadIdArr = this.onlineInvoiceAdvances.map(v => v.advanceApplyHeadId)
      for (let item of this.selectionItem2) {
        if (!advanceApplyHeadIdArr.includes(item.advanceApplyHeadId)) {
          this.onlineInvoiceAdvances.push({
            advanceApplyHeadId: item.advanceApplyHeadId,
            advanceApplyNum: item.advanceApplyNum,
            applyDate: item.applyDate,
            contractCode: item.contractCode,
            contractNum: item.contractNum,
            projectNum: item.projectNum,
            projectName: item.projectName,
            businessType: item.businessType,
            currencyCode: item.currencyCode,
            currencyName: item.currencyName,
            hangAccountAmount: item.applyPayAmount,
            usableAmount: item.usableAmount,
            usableAmountLock: item.usableAmount, // 不可变动的[可用金额]
            chargeOffAmount: null
          })
        }
      }
      this.dialogFormVisible = false
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
    setRowAmount (row) {
      if (row.chargeOffAmount > this.form.actualInvoiceAmountY) {
        return this.$message.warning(this.$t('purSettlementMod.msgArr[4]'))
      }
      if (row.chargeOffAmount > row.hangAccountAmount) {
        return this.$message.warning(this.$t('purSettlementMod.msgArr[10]'))
      } else {
        // row.usableAmount = row.usableAmountLock - row.chargeOffAmount;
      }
      setTimeout(() => {
        this.form.paidAmount = this.onlineInvoiceAdvances.map(v => v.chargeOffAmount).reduce((p, c) => ((Number(p) || 0) + (Number(c) || 0)))
        if (Math.abs(this.form.paidAmount) > Math.abs(this.form.actualInvoiceAmountY)) {
          return this.$message.warning(this.$t('purSettlementMod.msgArr[6]'))
        } else {
          this.form.unPaidAmount = this.form.actualInvoiceAmountY - this.form.paidAmount
        }
      }, 500)
    },
    formatData (row, column, cellValue, index) {
      return cellValue ? this.$getDictLabel('WAREHOURING_RETURN_DETAIL', cellValue) : cellValue
    },
    backTo () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('onlineInvoiceList.getQuerydata')
    },
    // 添加罚扣款
    addOnlineInvoicePunishes () {
      if (!this.form.vendorCode) {
        return this.$message.error(this.$t('bid_mod.setPermissionError'))
      }
      if (this.onlineInvoiceDetails.length == 0) {
        this.$message.error(this.$t('purSettlementMod.msgSelInvoiceDetail'))
        return
      }
      this.queryPunishes()
    },
    queryPunishes () {
      let invoiceNoticeIds = []
      this.onlineInvoiceDetails.forEach(elm => {
        invoiceNoticeIds.push(elm.invoiceNoticeId)
      })
      let ids = [...new Set(invoiceNoticeIds)]
      const data = {
        ...this.punishesQueryPage, // 分页
        ...this.filterFormPunishes, // 查询框
        invoiceNoticeIds: ids,
        isService: this.form.isService
      }
      this.$http({
        url: '/api-sup-ce/invoice/invoicePunish/listPageByParam',
        method: 'POST',
        data: data,
        loading: true
      }).then(res => {
        this.displayPunishes = res.data.list
        this.punishesPage.total = res.data.total
        this.dialogFormVisiblePunishes = true
      })
    },
    punishesCurrentChange (num) {
      this.punishesQueryPage.pageNum = num
      this.queryPunishes()
    },
    punishesSizeChange (size) {
      this.punishesQueryPage.pageSize = size
      this.queryPunishes()
    },
    punishesHandleSelectionChange (selection) {
      this.selectionPunishes = selection
    },
    punishesHandleDBClick (val) {
      this.selectionPunishes = [val]
      this.addConfirmPunishes()
    },
    // 确认选择考核单据
    addConfirmPunishes () {
      if (this.selectionPunishes.length == 0) {
        return this.$message.warning(this.$t('common.msgSelectData'))
      }
      this.setTotal('addInvoicePunishes')
    },
    setTotal (type) {
      // 1 开票明细
      if (this.onlineInvoiceDetails.length == 0) {
        this.form.totalTax = 0
        this.form.taxTotalAmount = 0
        this.form.noTaxTotalAmount = 0
      } else {
        this.form.totalTax = this.onlineInvoiceDetails.map(v => v.tax).reduce((p, c) => ((Number(p) || 0) + (Number(c) || 0)))
        this.form.taxTotalAmount = this.onlineInvoiceDetails.map(v => v.taxAmount).reduce((p, c) => ((Number(p) || 0) + (Number(c) || 0)))
        this.form.noTaxTotalAmount = this.onlineInvoiceDetails.map(v => v.noTaxAmount).reduce((p, c) => ((Number(p) || 0) + (Number(c) || 0)))
      }
      // 2 invoicePunishes 罚扣款明细
      if (type && type === 'addInvoicePunishes') { // 选择数据计算
        const oldData = this.onlineInvoicePunishes
        let newData = oldData.concat(this.selectionPunishes)
        this.minusTax = newData.map(v => v.tax).reduce((p, c) => ((Number(p) || 0) + (Number(c) || 0)))
        this.minusTaxTotalAmount = newData.map(v => v.actualAssessmentAmountY).reduce((p, c) => ((Number(p) || 0) + (Number(c) || 0)))
        this.minusNoTaxTotalAmount = newData.map(v => v.actualAssessmentAmountN).reduce((p, c) => ((Number(p) || 0) + (Number(c) || 0)))
      } else { // 数据变化计算
        if (this.onlineInvoicePunishes.length == 0) {
          this.minusTax = 0
          this.minusTaxTotalAmount = 0
          this.minusNoTaxTotalAmount = 0
        } else {
          this.minusTax = this.onlineInvoicePunishes.map(v => v.tax).reduce((p, c) => ((Number(p) || 0) + (Number(c) || 0)))
          this.minusTaxTotalAmount = this.onlineInvoicePunishes.map(v => v.actualAssessmentAmountY).reduce((p, c) => ((Number(p) || 0) + (Number(c) || 0)))
          this.minusNoTaxTotalAmount = this.onlineInvoicePunishes.map(v => v.actualAssessmentAmountN).reduce((p, c) => ((Number(p) || 0) + (Number(c) || 0)))
        }
      }

      // 3
      let ceeaTotalTax = Number(this.form.totalTax - this.minusTax).toFixed(2) // 税额
      let ceeaTaxTotalAmount = Number(this.form.taxTotalAmount - this.minusTaxTotalAmount).toFixed(2) // 含税总金额
      let ceeaNoTaxTotalAmount = Number(this.form.noTaxTotalAmount - this.minusNoTaxTotalAmount).toFixed(2) // 不含税总金额

      if (type && type === 'addInvoicePunishes') { // 选择罚扣款单
        if (ceeaTaxTotalAmount < 0) {
          this.$message.warning(this.$t('purSettlementMod.msgArr[9]'))
        } else {
          // taxTotalAmount totalTax
          this.form.totalTax = ceeaTotalTax // 税额
          this.form.taxTotalAmount = ceeaTaxTotalAmount // 含税总金额
          this.form.noTaxTotalAmount = ceeaNoTaxTotalAmount // 不含税总金额
          this.onlineInvoicePunishes = this.onlineInvoicePunishes.concat(this.selectionPunishes)
          this.dialogFormVisiblePunishes = false
        }
      } else {
        if (ceeaTaxTotalAmount < 0) {
          this.$message.warning(this.$t('purSettlementMod.msgArr[9]'))
          this.form.totalTax = ceeaTotalTax // 税额
          this.form.taxTotalAmount = ceeaTaxTotalAmount // 含税总金额
          this.form.noTaxTotalAmount = ceeaNoTaxTotalAmount // 不含税总金额
        }
      }
    },
    // 考核类型---显示
    filterCatHandler (row, column, cellValue, index) {
      return cellValue ? this.$getDictLabel('ASSESSMENT_BILL_TYPE', cellValue) : cellValue
    },
    filterCurrency (row, column, cellValue, index) {
      return cellValue ? this.$getDictLabel('currency', cellValue) : cellValue
    },
    filterBusinessType (row, column, cellValue, index) {
      return cellValue ? this.$getDictLabel('BUSINESS_TYPE', cellValue) : cellValue
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
  }
</style>
<style>
  .order-select .el-select-dropdown__list {
    padding: 0;
  }
  .order-select .el-select-dropdown__list .el-select-dropdown__item {
    border-bottom: 1px solid #ddd;
  }
  .order-select .el-select-dropdown__item.option-item.is-disabled{
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
  .order-select.el-select-dropdown.is-multiple .el-select-dropdown__item.selected::after{
    top: 0 !important;
    right: 15px !important;
  }
</style>
