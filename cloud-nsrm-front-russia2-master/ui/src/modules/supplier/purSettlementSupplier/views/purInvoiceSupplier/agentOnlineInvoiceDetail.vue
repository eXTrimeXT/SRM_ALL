<template>
  <el-container class="flex-container the-purInvoice-detail" direction="vertical">
    <el-main>
      <div class="form-container2">
        <el-collapse v-model="activeDims" class="tab-form-style">
          <el-form
            ref="invoiceNoticeForm"
            :model="form"
            :rules="formRules"
            label-position="top"
            class="form-incontainer"
            :disabled="curOpt !== 'add' && curOpt !== 'edit'"
          >
            <el-collapse-item :title="$t('perfMod.documentsInformation')" name="1">
              <srm-row :gutter="32">
                <!-- 业务实体 -->
                <srm-col>
                  <el-form-item :label="$t('quota.org')" prop="orgId">
                    <OrganizationSelector
                      ref="organizationSelector"
                      v-model="form.orgId"
                      :parent-id="-1"
                      node-type="OU"
                      :placeholder="$t('common.pleaseSelect')"
                      :disabled="curOpt !== 'add' && curOpt !== 'edit'"
                      @select="selectHandler"
                    />
                  </el-form-item>
                </srm-col>
                <!-- 库存组织 -->
                <srm-col>
                  <el-form-item :label="$t('purchaseDemand.invOrg')" prop="organizationId">
                    <!-- auto-select-when-one-item -->
                    <OrganizationSelector
                      ref="organizationSelector2"
                      v-model="form.organizationId"
                      :parent-id="form.orgId"
                      node-type="INV"
                      :placeholder="$t('common.pleaseSelect')"
                      :disabled="curOpt !== 'add' && curOpt !== 'edit'"
                      @select="selectHandler2"
                    />
                  </el-form-item>
                </srm-col>
                <!-- 供应商 -->
                <srm-col>
                  <el-form-item :label="$t('common.vendor')" prop="vendorName">
                    <QuickSearch
                      :disabled="(curOpt !== 'add' && curOpt !== 'edit') || !!form.vendorCode"
                      :show-input="form.vendorName"
                      show-key="vendorCode"
                      :scope-data="form"
                      name="scc_sup_company_info_all"
                      @close-quicksearch="getVendorObj"
                    />
                  </el-form-item>
                </srm-col>
                <!-- 付款方式 -->
                <srm-col>
                  <el-form-item :label="$t('paymentType.paymentWay')">
                    <dict-select
                      v-model="form.payMethod"
                      code="PAYMENT_MODE"
                      :disabled="isReadOnly"
                    />
                  </el-form-item>
                </srm-col>
                <!-- 币种 -->
                <srm-col>
                  <el-form-item :label="$t('quota.currency')" prop="currencyName">
                    <QuickSearch
                      :disabled="curOpt !== 'add' && curOpt !== 'edit'"
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
                      :disabled="curOpt !== 'add' && curOpt !== 'edit'"
                      @change-value="taxRateChangeHandel"
                    />
                  </el-form-item>
                </srm-col>

                <!-- 付款账期 -->
                <srm-col>
                  <el-form-item :label="$t('paymentType.paymentDay1')" prop="payAccountPeriodCode">
                    <dict-select
                      v-model="form.payAccountPeriodCode"
                      code="PAYMENT_PERIOD"
                      @change-value="(value, dictItem) => getPaymentDayByCode(value, dictItem)"
                    />
                  </el-form-item>
                </srm-col>
                <!-- 开票单号 -->
                <srm-col>
                  <el-form-item :label="$t('purSettlementMod.billingNumber')">
                    <el-input v-model="form.onlineInvoiceNum" disabled />
                  </el-form-item>
                </srm-col>
                <!-- 单据状态 -->
                <srm-col>
                  <el-form-item :label="$t('bidMod.billstatus')">
                    <dict-select v-model="form.invoiceStatus" code="INVOICE_STATUS" disabled />
                  </el-form-item>
                </srm-col>
                <!-- 系统含税总金额 = 对账单明细含税金额之和-考核单含税金额之和-->
                <srm-col>
                  <el-form-item :label="$t('purSettlementMod.systemTotalAmountTax')">
                    <template #label>
                      <div>
                        <span style="margin-right: 18px">{{
                          $t('purSettlementMod.systemTotalAmountTax')
                        }}</span>
                        <el-tooltip
                          class="item"
                          effect="dark"
                          :content="$t('purSettlementMod.systemTotalAmountTaxCal')"
                          placement="top"
                        >
                          <i class="el-icon-question" />
                        </el-tooltip>
                      </div>
                    </template>
                    <el-input-number
                      v-model="form.taxTotalAmount"
                      :precision="8"
                      disabled
                      :controls="false"
                      class="input-number-precision"
                    />
                  </el-form-item>
                </srm-col>
                <!-- 系统未税总金额 = 对账单明细未税金额之和 - 考核单未税金额之和 -->
                <srm-col>
                  <el-form-item :label="$t('purSettlementMod.totalAmountNotTaxed')">
                    <template #label>
                      <div>
                        <span style="margin-right: 18px">{{
                          $t('purSettlementMod.totalAmountNotTaxed')
                        }}</span>
                        <el-tooltip
                          class="item"
                          effect="dark"
                          :content="$t('purSettlementMod.totalAmountNotTaxedCal')"
                          placement="top"
                        >
                          <i class="el-icon-question" />
                        </el-tooltip>
                      </div>
                    </template>
                    <el-input-number
                      v-model="form.excluTaxTotalAmount"
                      :precision="8"
                      disabled
                      :controls="false"
                      class="input-number-precision"
                    />
                  </el-form-item>
                </srm-col>
                <!-- 系统总税额 -->
                <srm-col>
                  <el-form-item :label="$t('purSettlementMod.totalSystemTax')">
                    <el-input-number
                      v-model="form.totalTax"
                      :precision="8"
                      disabled
                      :controls="false"
                      class="input-number-precision"
                    />
                  </el-form-item>
                </srm-col>
                <!-- 发票含税总金额 = 发票明细含税金额之和 -->
                <srm-col>
                  <el-form-item
                    :label="$t('purSettlementMod.totalAmountInvoiceTax')"
                    prop="actualInvoiceAmountY"
                  >
                    <template #label>
                      <div>
                        <span style="margin-right: 18px">{{
                          $t('purSettlementMod.totalAmountInvoiceTax')
                        }}</span>
                        <el-tooltip
                          class="item"
                          effect="dark"
                          :content="$t('purSettlementMod.totalAmountInvoiceTaxCal')"
                          placement="top"
                        >
                          <i class="el-icon-question" />
                        </el-tooltip>
                      </div>
                    </template>
                    <el-input-number
                      v-model="form.actualInvoiceAmountY"
                      :precision="8"
                      disabled
                      :controls="false"
                      class="input-number-precision"
                    />
                  </el-form-item>
                </srm-col>
                <!-- 发票未税总金额=发票明细未税总金额之和 -->
                <srm-col>
                  <el-form-item
                    :label="$t('purSettlementMod.totalAmountInvoiceNoTax')"
                    prop="actualInvoiceAmountN"
                  >
                    <template #label>
                      <div>
                        <span style="margin-right: 18px">{{
                          $t('purSettlementMod.totalAmountInvoiceNoTax')
                        }}</span>
                        <el-tooltip
                          class="item"
                          effect="dark"
                          :content="$t('purSettlementMod.totalAmountInvoiceNoTaxCal')"
                          placement="top"
                        >
                          <i class="el-icon-question" />
                        </el-tooltip>
                      </div>
                    </template>
                    <el-input-number
                      v-model="form.actualInvoiceAmountN"
                      :precision="8"
                      disabled
                      :controls="false"
                      class="input-number-precision"
                    />
                  </el-form-item>
                </srm-col>
                <!-- 发票总税额 -->
                <srm-col>
                  <el-form-item :label="$t('purSettlementMod.invoiceTotalTax')" prop="invoiceTax">
                    <el-input-number
                      v-model="form.invoiceTax"
                      :precision="8"
                      disabled
                      :controls="false"
                      class="input-number-precision"
                    />
                  </el-form-item>
                </srm-col>
                <!-- 已付款金额=当前开票单预付款的本次核销金额（含税）+单前开票单付款申请的已付款金额 -->
                <srm-col>
                  <el-form-item :label="$t('purSettlementMod.paidAmount')">
                    <template #label>
                      <div>
                        <span style="margin-right: 18px">{{
                          $t('purSettlementMod.paidAmount')
                        }}</span>
                        <el-tooltip
                          class="item"
                          effect="dark"
                          :content="$t('purSettlementMod.paidAmountCal')"
                          placement="top"
                        >
                          <i class="el-icon-question" />
                        </el-tooltip>
                      </div>
                    </template>
                    <el-input-number
                      v-model="form.paidAmount"
                      :precision="8"
                      disabled
                      :controls="false"
                      class="input-number-precision"
                    />
                  </el-form-item>
                </srm-col>
                <!-- 未付款金额=当前开票单的系统含税总金额 - 已付款金额 -->
                <srm-col>
                  <el-form-item :label="$t('purSettlementMod.unPaidAmount')">
                    <template #label>
                      <div>
                        <span style="margin-right: 18px">{{
                          $t('purSettlementMod.unPaidAmount')
                        }}</span>
                        <el-tooltip
                          class="item"
                          effect="dark"
                          :content="$t('purSettlementMod.unpaidAmountCal')"
                          placement="top"
                        >
                          <i class="el-icon-question" />
                        </el-tooltip>
                      </div>
                    </template>
                    <el-input-number
                      v-model="form.unPaidAmount"
                      :precision="8"
                      disabled
                      :controls="false"
                      class="input-number-precision"
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
                      :format="$formatDatePicker"
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
              </srm-row>
            </el-collapse-item>
          </el-form>
          <el-form
            ref="advanceForm"
            :model="advanceForm"
            :disabled="curOpt !== 'add' && curOpt !== 'edit'"
          >
            <!-- 对账单明细 -->
            <el-collapse-item :title="$t('accountMod.statementDetail1')" name="2">
              <p class="btn_line">
                <el-button
                  type="primary"
                  :disabled="curOpt !== 'add' && curOpt !== 'edit'"
                  class="detail-pbtn"
                  @click="addStatementDetails"
                >
                  {{ $t('common.add') }}
                </el-button>
              </p>
              <el-table
                :data="advanceForm.statementDetailsTable"
                style="width: 100%"
                border
                max-height="251px"
              >
                <!-- 序号 -->
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('common.sort')"
                  width="60"
                />
                <!-- 对账单号 -->
                <el-table-column
                  align="center"
                  prop="invoiceNoticeNumber"
                  :label="$t('purSettlementMod.statementNumber')"
                  width="120"
                  show-overflow-tooltip
                />
                <!-- 对账行号 -->
                <el-table-column
                  align="center"
                  prop="invoiceDetailNum"
                  :label="$t('purSettlementMod.invoiceDetailNum')"
                  width="120"
                  show-overflow-tooltip
                />
                <!-- 入库/退货单号 -->
                <el-table-column
                  align="center"
                  prop="receiveOrderNo"
                  :label="$t('accountMod.inboundReturnOrderNo')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 入库/退货行号 -->
                <el-table-column
                  align="center"
                  prop="receiveOrderLineNo"
                  :label="$t('accountMod.inboundReturnLineNo')"
                  width="120"
                  show-overflow-tooltip
                />
                <!-- 接收日期 -->
                <el-table-column
                  align="center"
                  prop="receiveDate"
                  :label="$t('orderMod.transactionDate')"
                  width="150"
                  show-overflow-tooltip
                  :formatter="(row, column, cellValue) => $parseTime(cellValue)"
                />
                <!-- 事务类型 -->
                <el-table-column
                  align="center"
                  prop="type"
                  :label="$t('purSettlementMod.type')"
                  :formatter="formatData"
                  width="100"
                  show-overflow-tooltip
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
                  prop="lineNum"
                  :label="$t('orderMod.orderLineNum')"
                  width="100"
                  show-overflow-tooltip
                />
                <!-- 物料编码 -->
                <el-table-column
                  align="center"
                  prop="itemCode"
                  :label="$t('common.materialCode')"
                  width="100"
                  show-overflow-tooltip
                />
                <!-- 物料名称 -->
                <el-table-column
                  align="center"
                  prop="itemName"
                  :label="$t('common.materialName')"
                  min-width="150"
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
                  align="center"
                  prop="unit"
                  :label="$t('dataConfMod.unit')"
                  width="80"
                  show-overflow-tooltip
                />
                <!-- 对账数量 -->
                <el-table-column
                  align="center"
                  prop="receiveNum"
                  :label="$t('purSettlementMod.reconciliationQuantity')"
                  width="100"
                  show-overflow-tooltip
                />
                <!-- 可开票数量 -->
                <el-table-column
                  align="center"
                  prop="notInvoiceQuantity"
                  :label="$t('purSettlementMod.invoicesAvailable')"
                  width="100"
                  show-overflow-tooltip
                />
                <!-- 本次开票数量 -->
                <el-table-column
                  align="center"
                  prop="invoiceQuantity"
                  :label="$t('purSettlementMod.invoiceQuantity')"
                  width="100"
                >
                  <template slot="header">
                    <em class="toRequired">*</em>{{ $t('purSettlementMod.invoiceQuantity') }}
                  </template>
                  <template slot-scope="scope">
                    <el-form-item
                      :prop="'statementDetailsTable.' + scope.$index + '.invoiceQuantity'"
                      :rules="{ required: true, validator: invoiceValidator, trigger: 'blur' }"
                    >
                      <el-input-number
                        v-model="scope.row.invoiceQuantity"
                        :min="0"
                        :disabled="isReadOnly"
                        :controls="false"
                        class="input-number-precision"
                        @blur="setAmountCal(scope.row, scope.$index)"
                      />
                    </el-form-item>
                  </template>
                </el-table-column>
                <!-- 未税单价 -->
                <el-table-column
                  align="center"
                  prop="unitPriceExcludingTax"
                  :label="$t('purSettlementMod.unitPriceNoTax')"
                  width="100"
                  show-overflow-tooltip
                />
                <!-- 税率 -->
                <el-table-column
                  align="center"
                  prop="taxRate"
                  :label="$t('bidMod.taxRate')"
                  width="100"
                  show-overflow-tooltip
                />
                <!-- 含税金额 -->
                <el-table-column
                  align="center"
                  prop="taxAmount"
                  :label="$t('contractMod.amount2')"
                  width="100"
                  show-overflow-tooltip
                />
                <!-- 未税金额 -->
                <el-table-column
                  align="center"
                  prop="noTaxAmount"
                  :label="$t('contractMod.unAmount')"
                  width="100"
                  show-overflow-tooltip
                />
                <!-- 币种 -->
                <el-table-column
                  align="center"
                  prop="currencyName"
                  :label="$t('purchaseDemand.currency')"
                  width="100"
                  show-overflow-tooltip
                />
                <el-table-column :label="$t('common.operation')" width="60" fixed="right">
                  <template slot-scope="scope">
                    <el-button type="text" @click="deleteStatement(scope.$index, scope.row)">
                      {{ $t('common.delete') }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <!-- 扣罚&返利明细 -->
            <el-collapse-item :title="$t('purSettlementMod.deductionRebateDetails')" name="3">
              <p class="btn_line">
                <el-button type="primary" class="detail-pbtn" @click="addOnlineInvoicePunishes">
                  {{ $t('common.add') }}
                </el-button>
              </p>
              <el-table :data="onlineInvoicePunishes" style="width: 100%" border max-height="251px">
                <el-table-column
                  align="center"
                  type="index"
                  width="50"
                  :label="$t('common.sort')"
                />
                <!-- 考核单号 -->
                <el-table-column
                  align="center"
                  prop="assessmentNo"
                  :label="$t('perfMod.assessmentNo')"
                  width="120"
                  show-overflow-tooltip
                />
                <!-- 考核时间 -->
                <el-table-column
                  align="center"
                  prop="assessmentDate"
                  :label="$t('perfMod.assessmentDate')"
                  width="100"
                  show-overflow-tooltip
                  :formatter="(row, column, cellValue) => $parseTime(cellValue)"
                />
                <!-- 考核事件 -->
                <el-table-column
                  align="center"
                  prop="assessmentType"
                  :label="$t('purSettlementMod.assessmentEvent')"
                  width="100"
                  :formatter="filterCatHandler"
                  show-overflow-tooltip
                />
                <!-- 实际考核未税金额 -->
                <el-table-column
                  align="center"
                  prop="actualAssessmentAmountN"
                  :label="$t('purSettlementMod.actualAssessmentAmountN2')"
                  width="170"
                  show-overflow-tooltip
                />
                <!-- 税额 -->
                <el-table-column
                  align="center"
                  prop="tax"
                  :label="$t('contractMod.taxQuota')"
                  width="100"
                  show-overflow-tooltip
                />
                <!-- 实际考核含税金额 -->
                <el-table-column
                  align="center"
                  prop="actualAssessmentAmountY"
                  :label="$t('purSettlementMod.actualAssessmentAmountY2')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 币种 -->
                <el-table-column
                  align="center"
                  prop="currencyCode"
                  :label="$t('quota.currency')"
                  width="100"
                  show-overflow-tooltip
                />
                <!-- 物料分类 -->
                <el-table-column
                  align="center"
                  prop="categoryName"
                  :label="$t('bidMod.categoryName')"
                  width="100"
                  show-overflow-tooltip
                />
                <!-- 物料编码 -->
                <el-table-column
                  align="center"
                  prop="itemCode"
                  :label="$t('common.materialCode')"
                  width="100"
                  show-overflow-tooltip
                />
                <!-- 物料名称 -->
                <el-table-column
                  align="center"
                  prop="itemName"
                  :label="$t('common.materialName')"
                  min-width="150"
                  show-overflow-tooltip
                />
                <!-- 备注 -->
                <el-table-column
                  align="center"
                  prop="comment"
                  :label="$t('common.remark')"
                  width="100"
                  show-overflow-tooltip
                />
                <el-table-column :label="$t('common.operation')" width="60" fixed="right">
                  <template slot-scope="scope">
                    <el-button type="text" @click="delInvoicePunishes(scope.$index, scope.row)">
                      {{ $t('common.delete') }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <!-- 预付款申请明细 -->
            <el-collapse-item :title="$t('purSettlementMod.prepayApplyDetails')" name="4">
              <p class="btn_line">
                <!-- 新增 -->
                <el-button type="primary" class="detail-pbtn" @click="openAdvanceItem">
                  {{ $t('common.add') }}
                </el-button>
              </p>
              <el-table
                :data="advanceForm.onlineInvoiceAdvances"
                style="width: 100%"
                border
                max-height="251px"
              >
                <!-- 序号 -->
                <el-table-column
                  align="center"
                  type="index"
                  width="50"
                  :label="$t('common.sort')"
                />
                <!-- 预付申请单号 -->
                <el-table-column
                  align="center"
                  prop="advanceApplyNumber"
                  :label="$t('purSettlementMod.advanceApplyNum')"
                  show-overflow-tooltip
                />
                <!-- 单据创建日期 -->
                <el-table-column
                  align="center"
                  prop="appliedDate"
                  :label="$t('purSettlementMod.appliedDate')"
                  show-overflow-tooltip
                  :formatter="(row, column, cellValue) => $parseTime(cellValue)"
                />
                <!-- 预付款金额 -->
                <el-table-column
                  align="center"
                  prop="includeTaxAmount"
                  :label="$t('accountMod.advancePaymentAmount')"
                />
                <!-- 可核销金额 -->
                <el-table-column
                  align="center"
                  prop="unWrittenOffAmount"
                  :label="$t('purSettlementMod.unWrittenOffAmount2')"
                  show-overflow-tooltip
                />
                <!-- 本次核销金额 -->
                <el-table-column
                  align="center"
                  prop="curWrittenOffAmount"
                  :label="$t('purSettlementMod.chargeOffAmount')"
                >
                  <template slot="header">
                    <em class="toRequired">*</em>{{ $t('purSettlementMod.chargeOffAmount') }}
                  </template>
                  <template slot-scope="scope">
                    <el-form-item
                      :prop="'onlineInvoiceAdvances.' + scope.$index + '.curWrittenOffAmount'"
                      :rules="{ required: true, validator: amountValidator, trigger: 'blur' }"
                    >
                      <el-input-number
                        v-model="scope.row.curWrittenOffAmount"
                        :min="0"
                        :controls="false"
                        class="input-number-precision"
                        @blur="setRowAmount(scope.row, scope.$index)"
                      />
                    </el-form-item>
                  </template>
                </el-table-column>
                <!-- 币种 -->
                <el-table-column
                  align="center"
                  prop="currencyName"
                  :label="$t('quota.currency')"
                  show-overflow-tooltip
                />
                <!-- 税率 -->
                <el-table-column
                  align="center"
                  prop="taxRate"
                  :label="$t('bidMod.taxRate')"
                  show-overflow-tooltip
                />
                <!-- 预付款创建人 -->
                <el-table-column
                  align="center"
                  prop="appliedFullName"
                  :label="$t('purSettlementMod.prepaymentCreator')"
                  show-overflow-tooltip
                />
                <el-table-column :label="$t('common.operation')" width="60" fixed="right">
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="delonlineInvoiceAdvances(scope.$index, scope.row)"
                    >
                      {{ $t('common.delete') }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
              <!-- 预付款申请 - 新增 -->
              <srm-dialog
                :title="$t('purSettlementMod.selPrepayApplyDetails')"
                size="large"
                :visible.sync="dialogFormVisible"
                :close-on-click-modal="false"
                :destroy-on-close="true"
              >
                <p class="btn_line">
                  <el-button type="primary" @click="affirmSelprepay">
                    {{ $t('common.affirm') }}
                  </el-button>
                  <el-button @click="dialogFormVisible = false">
                    {{ $t('common.backTo') }}
                  </el-button>
                </p>
                <el-table
                  ref="refPrepayApplyInfo"
                  :data="prepayApplyInfo"
                  border
                  height="250px"
                  @selection-change="prepaySelectionChange"
                  @row-dblclick="prepayDBClick"
                >
                  <el-table-column type="selection" width="50" fixed="left" />
                  <el-table-column
                    :label="$t('contractMod.tabindex')"
                    align="center"
                    type="index"
                    width="50"
                  />
                  <!-- 预付申请单号 -->
                  <el-table-column
                    align="center"
                    prop="advanceApplyNumber"
                    :label="$t('purSettlementMod.advanceApplyNum')"
                    show-overflow-tooltip
                  />
                  <!-- 单据创建日期 -->
                  <el-table-column
                    align="center"
                    prop="appliedDate"
                    :label="$t('purSettlementMod.appliedDate')"
                    show-overflow-tooltip
                    :formatter="(row, column, cellValue) => $parseTime(cellValue)"
                  />
                  <!-- 预付款金额 -->
                  <el-table-column
                    align="center"
                    prop="includeTaxAmount"
                    :label="$t('accountMod.advancePaymentAmount')"
                    show-overflow-tooltip
                  />
                  <!-- 可核销金额 -->
                  <el-table-column
                    align="center"
                    prop="unWrittenOffAmount"
                    :label="$t('purSettlementMod.unWrittenOffAmount2')"
                    show-overflow-tooltip
                  />
                  <!-- 币种 -->
                  <el-table-column
                    align="center"
                    prop="currencyName"
                    :label="$t('quota.currency')"
                    show-overflow-tooltip
                  />
                  <!-- 税率 -->
                  <el-table-column
                    align="center"
                    prop="taxRate"
                    :label="$t('bidMod.taxRate')"
                    show-overflow-tooltip
                  />
                  <!-- 预付款创建人 -->
                  <el-table-column
                    align="center"
                    prop="appliedFullName"
                    :label="$t('purSettlementMod.prepaymentCreator')"
                    show-overflow-tooltip
                  />
                </el-table>
                <CPagination
                  style="margin: 3px"
                  :total="parentOrgTableDataPage.total"
                  :page-num="parentOrgTableDataPage.pageNum"
                  :page-size="parentOrgTableDataPage.pageSize"
                  @current-change="parentDataCurrentChange"
                  @size-change="parentDataSizeChange"
                />
              </srm-dialog>
            </el-collapse-item>
            <!-- 发票信息 -->
            <el-collapse-item :title="$t('accountMod.invoiceInfo')" name="5">
              <p class="btn_line">
                <!-- 新增发票 -->
                <CInvoiceUpload
                  class="invoice-btn"
                  :editMode="false"
                  :isReadonly="curOpt !== 'add' && curOpt !== 'edit'"
                  fileFunction="SUPPLIER_INVOICE_SETTLE"
                  :invoiceInformation="invoiceInformation"
                  @editInvoiceRow="(row) => editInvoiceRow(row)"
                  @saveInvoice="(fileList) => saveInvoice(fileList)"
                />
                <el-button
                  :disabled="curOpt !== 'add' && curOpt !== 'edit'"
                  class="detail-pbtn"
                  @click="deleteBatchInvoice"
                >
                  {{ $t('common.delete') }}
                </el-button>
                <el-button
                  type="text"
                  style="color: red; front-size: 12px;"
                >
                  <!-- * 发票仅支持JPG、PNG、JPEG、PDF、OFD格式，单个文件大小不超过10M -->
                  {{ $t('agentOnlineInvoice.prompt4') }}
                </el-button>
              </p>
              <el-table
                class="invoice-table"
                :data="invoiceInformation"
                style="width: 100%"
                border
                max-height="251px"
                @selection-change="handleChangeInvoice"
              >
                <el-table-column
                  align="center"
                  type="selection"
                  width="60"
                  fixed="left"
                />
                <!-- 序号 -->
                <el-table-column
                  align="center"
                  type="index"
                  width="60"
                  fixed="left"
                  :label="$t('common.sort')"
                />
                <!-- 发票名称 -->
                <el-table-column
                  align="center"
                  prop="invoiceName"
                  width="120"
                  :label="$t('agentOnlineInvoice.invoiceName')"
                  show-overflow-tooltip
                />
                <!-- 发票影像 -->
                <el-table-column
                  align="center"
                  prop="fileSourceName"
                  width="80"
                  :label="$t('purSettlementMod.fileSourceName2')"
                  show-overflow-tooltip
                >
                  <template slot-scope="scope">
                    <el-button class="invoice-preview" type="text" @click="invoicePreview(scope.row)">
                      {{ scope.row.fileSourceName }}
                    </el-button>
                    <span class="tooltip-show">{{ scope.row.fileSourceName }}</span>
                  </template>
                </el-table-column>
                <!-- 增值税发票类型 -->
                <el-table-column
                  align="center"
                  prop="invoiceType"
                  width="120"
                  :label="$t('purSettlementMod.invoiceType')"
                  :formatter="(row, colum, value) => $getDictLabel('INVOICE_TYPE', value)"
                  show-overflow-tooltip
                />
                <!-- 采购方税号 -->
                <el-table-column
                  align="center"
                  prop="purchaserRegisterNum"
                  width="120"
                  :label="$t('purSettlementMod.purchaserRegisterNum')"
                  show-overflow-tooltip
                />
                <!-- 发票代码 -->
                <el-table-column
                  align="center"
                  prop="invoiceCode"
                  width="120"
                  :label="$t('purSettlementMod.invoiceCode')"
                  show-overflow-tooltip
                />
                <!-- 发票号码 -->
                <el-table-column
                  align="center"
                  prop="invoiceNum"
                  width="120"
                  :label="$t('purSettlementMod.invoiceNum')"
                  show-overflow-tooltip
                />
                <!-- 开票日期 -->
                <el-table-column
                  align="center"
                  prop="invoiceDate"
                  width="150"
                  :label="$t('purSettlementMod.invoiceDate2')"
                  :formatter="(row, column, cellValue) => $parseTime(cellValue)"
                  show-overflow-tooltip
                />
                <!-- 校验码 -->
                <el-table-column
                  align="center"
                  prop="checkCode"
                  width="120"
                  :label="$t('purSettlementMod.checkCode')"
                  show-overflow-tooltip
                />
                <!-- 采购方 -->
                <el-table-column
                  align="center"
                  prop="purchaserName"
                  width="120"
                  :label="$t('purSettlementMod.purchaserName')"
                  show-overflow-tooltip
                />
                <!-- 供方 -->
                <el-table-column
                  align="center"
                  prop="sellerName"
                  width="120"
                  :label="$t('purSettlementMod.sellerName2')"
                  show-overflow-tooltip
                />
                <!-- 未税金额 -->
                <el-table-column
                  align="center"
                  prop="noTaxTotalAmount"
                  width="120"
                  :label="$t('purSettlementMod.noTaxTotalAmount2')"
                  show-overflow-tooltip
                />
                <!-- 税额 -->
                <el-table-column
                  align="center"
                  prop="totalTax"
                  width="120"
                  :label="$t('purSettlementMod.totalTax')"
                  show-overflow-tooltip
                />
                <!-- 含税金额 -->
                <el-table-column
                  align="center"
                  prop="totalAmount"
                  width="120"
                  :label="$t('purSettlementMod.totalAmount')"
                  show-overflow-tooltip
                />
                <!-- 税率 -->
                <el-table-column
                  align="center"
                  prop="commodityTaxRate"
                  width="80"
                  :label="$t('bidMod.taxRate')"
                  show-overflow-tooltip
                />
                <!-- 销售方纳税人识别号 -->
                <el-table-column
                  align="center"
                  prop="sellerRegisterNum"
                  width="120"
                  :label="$t('agentOnlineInvoice.sellerRegisterNum')"
                  show-overflow-tooltip
                />
                <!-- 发票快递单号 -->
                <el-table-column
                  align="center"
                  prop="invoiceCourierNo"
                  width="120"
                  :label="$t('purSettlementMod.invoiceCourierNo')"
                  show-overflow-tooltip
                />
                <!-- 价税合计(小写) -->
                <el-table-column
                  align="center"
                  prop="amountInFigures"
                  width="120"
                  :label="$t('agentOnlineInvoice.amountInFigures')"
                  show-overflow-tooltip
                />
                <!-- 价税合计(大写) -->
                <el-table-column
                  align="center"
                  prop="amountInWords"
                  width="120"
                  :label="$t('agentOnlineInvoice.amountInWords')"
                  show-overflow-tooltip
                />
                <el-table-column :label="$t('common.operation')" width="150" fixed="right">
                  <template slot-scope="scope">
                    <!-- 编辑 -->
                    <CInvoiceUpload
                      class="invoice-btn"
                      :editMode="true"
                      :editRow="scope.row"
                      fileFunction="SUPPLIER_INVOICE_SETTLE"
                      :invoiceInformation="invoiceInformation"
                      @editInvoiceRow="(row) => editInvoiceRow(row)"
                      @saveInvoice="(fileList) => saveInvoice(fileList)"
                    />
                    <el-button type="text" @click="deleteInvoice(scope.$index, scope.row)">
                      {{ $t('common.delete') }}
                    </el-button>
                    <el-button type="text" @click="downloadInvoice(scope.row)">
                      {{ $t('common.download') }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <el-collapse-item :title="$t('quota.fileInfo')" name="6">
              <p class="btn_line">
                <el-button type="primary" class="detail-pbtn" @click="addFileuploads">
                  {{ $t('common.add') }}
                </el-button>
              </p>
              <upload-attach
                :readonly="curOpt !== 'add' && curOpt !== 'edit'"
                :attachData="fileuploads"
                :fileInfo="fileInfo"
              />
            </el-collapse-item>
          </el-form>
        </el-collapse>
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
      <!-- 驳回弹框 -->
      <srm-dialog
        :title="$t('vendorMod.refuseMemo')"
        :visible.sync="rejectedDialog"
        :destroy-on-close="true"
        size="small"
      >
        <el-form
          ref="rejectedForm"
          :model="rejectedModel.rejectedForm"
          :rules="rejectedModel.rules"
        >
          <el-form-item :label="$t('contractMod.rejectReason')" prop="rejectReason">
            <el-input v-model="rejectedModel.rejectedForm.rejectReason" type="textarea" />
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="rejectedDialog = false">
            {{ $t('common.cancel') }}
          </el-button>
          <el-button type="primary" @click="rejectedComfirm">
            {{ $t('common.confirm') }}
          </el-button>
        </div>
      </srm-dialog>

      <!-- 对账单明细 -->
      <srm-dialog
        :title="$t('accountMod.addStatementDetail')"
        size="large"
        :visible.sync="statementDialogVisible"
        :close-on-click-modal="false"
        :destroy-on-close="true"
      >
        <el-form ref="statementFormRef" label-position="top" :model="statementQueryForm">
          <srm-row :gutter="32">
            <srm-col :initCol="3">
              <!-- 对账单 -->
              <el-form-item :label="$t('purSettlementMod.statementNumber')">
                <el-input v-model="statementQueryForm.invoiceNoticeNumber" />
              </el-form-item>
            </srm-col>
            <!-- 订单号 -->
            <srm-col :initCol="3">
              <el-form-item :label="$t('orderMod.buyerOrderSynergy.orderNumber2')">
                <el-input v-model="statementQueryForm.orderNumber" />
              </el-form-item>
            </srm-col>
            <!-- 入库退货单 -->
            <srm-col :initCol="3">
              <el-form-item :label="$t('purSettlementMod.inboundReturnNumber')">
                <el-input v-model="statementQueryForm.receiveOrderNo" />
              </el-form-item>
            </srm-col>
          </srm-row>
        </el-form>

        <div style="margin-bottom: 8px;">
          <el-button type="primary" @click="queryStatementContent">
            {{ $t('common.search') }}
          </el-button>
          <el-button type="primary" @click="addOneItem">
            {{ $t('common.confirm') }}
          </el-button>
        </div>
        <el-table
          :data="statementTable"
          style="width: 100%"
          border
          height="345px"
          highlight-current-row
          max-height="251px"
          @row-dblclick="handleItemDBClick"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <!-- 序号 -->
          <el-table-column align="center" type="index" :label="$t('common.sort')" width="60" />
          <!-- 对账单号 -->
          <el-table-column
            align="center"
            prop="invoiceNoticeNumber"
            :label="$t('purSettlementMod.statementNumber')"
            width="120"
            show-overflow-tooltip
          />
          <!-- 对账行号 -->
          <el-table-column
            align="center"
            prop="invoiceDetailNum"
            :label="$t('purSettlementMod.invoiceDetailNum')"
            width="120"
            show-overflow-tooltip
          />
          <!-- 入库/退货单号 -->
          <el-table-column
            align="center"
            prop="receiveOrderNo"
            :label="$t('accountMod.inboundReturnOrderNo')"
            width="150"
            show-overflow-tooltip
          />
          <!-- 入库/退货行号 -->
          <el-table-column
            align="center"
            prop="receiveOrderLineNo"
            :label="$t('accountMod.inboundReturnLineNo')"
            width="120"
            show-overflow-tooltip
          />
          <!-- 接收日期 -->
          <el-table-column
            align="center"
            prop="receiveDate"
            :label="$t('orderMod.transactionDate')"
            width="150"
            show-overflow-tooltip
            :formatter="(row, column, cellValue) => $parseTime(cellValue)"
          />
          <!-- 事务类型 -->
          <el-table-column
            align="center"
            prop="type"
            :label="$t('purSettlementMod.type')"
            :formatter="formatData"
            width="100"
            show-overflow-tooltip
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
            prop="lineNum"
            :label="$t('orderMod.orderLineNum')"
            width="100"
            show-overflow-tooltip
          />
          <!-- 物料编码 -->
          <el-table-column
            align="center"
            prop="itemCode"
            :label="$t('common.materialCode')"
            width="100"
            show-overflow-tooltip
          />
          <!-- 物料名称 -->
          <el-table-column
            align="center"
            prop="itemName"
            :label="$t('common.materialName')"
            min-width="150"
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
          <!-- 对账数量 -->
          <el-table-column
            align="center"
            prop="receiveNum"
            :label="$t('purSettlementMod.reconciliationQuantity')"
            width="100"
            show-overflow-tooltip
          />
          <!-- 可开票数量 -->
          <el-table-column
            align="center"
            prop="notInvoiceQuantity"
            :label="$t('purSettlementMod.invoicesAvailable')"
            width="100"
            show-overflow-tooltip
          />
          <!-- 本次开票数量 -->
          <el-table-column
            align="center"
            prop="invoiceQuantity"
            :label="$t('purSettlementMod.invoiceQuantity')"
            width="100"
            show-overflow-tooltip
          />
          <!-- 未税单价 -->
          <el-table-column
            align="center"
            prop="unitPriceExcludingTax"
            :label="$t('purSettlementMod.unitPriceNoTax')"
            width="100"
            show-overflow-tooltip
          />
          <!-- 税率 -->
          <el-table-column
            align="center"
            prop="taxRate"
            :label="$t('bidMod.taxRate')"
            width="100"
            show-overflow-tooltip
          />
          <!-- 含税单价 -->
          <el-table-column
            align="center"
            prop="unitPriceContainingTax"
            :label="$t('purchaseDemand.taxPrice')"
            width="100"
            show-overflow-tooltip
          />
        </el-table>

        <srm-row type="flex">
          <srm-col>
            <CPagination
              ref="queryPagination"
              style="margin: 5px"
              class="c-query-table-pagination"
              :total="queryTotal"
              :page-num="viewIndex"
              :page-size="viewSize"
              @current-change="changeCurrentIndex"
              @size-change="changeCurrentSize"
            />
          </srm-col>
        </srm-row>
      </srm-dialog>

      <!-- 绩效考核明细-新增 -->
      <srm-dialog
        :title="$t('purSettlementMod.supplierAssessmentDetails')"
        size="large"
        :visible.sync="dialogFormVisiblePunishes"
        :close-on-click-modal="false"
      >
        <div style="margin-bottom: 8px;">
          <el-button type="primary" @click="addConfirmPunishes">
            {{ $t('common.save') }}
          </el-button>
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
          <el-table-column type="selection" width="55" fixed="left" />
          <el-table-column align="center" type="index" width="50" />
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
            :formatter="(row, column, cellValue) => $parseTime(cellValue)"
          />
          <!-- 考核事件 -->
          <el-table-column
            prop="assessmentType"
            :label="$t('purSettlementMod.assessmentEvent')"
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
            prop="categoryName"
            :label="$t('bidMod.categoryName')"
            width="120"
            show-overflow-tooltip
          />
          <!-- 物料编码 -->
          <el-table-column
            prop="itemCode"
            :label="$t('common.materialCode')"
            width="100"
            show-overflow-tooltip
          />
          <!-- 物料名称 -->
          <el-table-column
            prop="itemName"
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
        <CPagination
          :total="punishesPage.total"
          :page-num="punishesPage.pageNum"
          :page-size="punishesPage.pageSize"
          @current-change="punishesCurrentChange"
          @size-change="punishesSizeChange"
        />
      </srm-dialog>

      <!-- 发票预览 -->
      <filePreview
        :visible="previewObj.isFileSource"
        :fileupload-id="previewObj.fileuploadId"
        :file-name="previewObj.fileSourceName"
        @cancel="previewObj.isFileSource = false"
      />
    </el-main>
  </el-container>
</template>
<script>
import { sysPrefix } from '@/config/ipConfig'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch'
import MainHeader from 'lib@/components/Table/MainHeader'
import { parseTime } from '@/utils'
import OrganizationSelector from 'lib@/components/organization-selector'
import CPagination from 'lib@/components/c-pagination'
import { mapGetters } from 'vuex'
import filePreview from 'lib@/components/filePreview'
import { downloadWithParam, getImgSrc } from 'lib@/utils/file'
import uploadAttach from '@/library/composition/orderManagementBuyer/upload-attach'
import CInvoiceUpload from 'lib@/components/c-ocr/c-invoice-upload'
import UploadInvoice from 'lib@/components/c-ocr/c-invoice-upload/uploadInvoice'

export default {
  name: 'AgentOnlineInvoiceDetail',
  components: {
    MainHeader,
    QuickSearch,
    CToolbar,
    OrganizationSelector,
    CPagination,
    filePreview,
    uploadAttach,
    UploadInvoice,
    CInvoiceUpload
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      selectInvoice: [],
      editMode: false,
      hasRepeat: false,
      fileList: [],
      previewObj: {
        isFileSource: false,
        fileuploadId: '',
        fileSourceName: ''
      },
      isReadOnly: this.$attrs.params.flag === 'readOnly',
      selectionPrepay: [],
      advanceParams: {},
      invoiceInforData: [],
      invoiceSelects: [],
      displayPunishes: [],
      punishesPage: {
        total: 0,
        pageNum: 1,
        pageSize: 20
      },
      selectionPunishes: [],
      prepayApplyInfo: [],
      queryTotal: 0,
      viewSize: 15,
      viewIndex: 1,
      statementTable: [],
      statementDialogVisible: false,
      statementQueryForm: {},
      fileInfo: {
        uploadType: 'DEF',
        sourceType: 'WEB_APP',
        fileModular: 'pur',
        fileFunction: 'purInvoice',
        fileType: 'images'
      },
      userInfo: this.$store.getters.userInfo,
      curRole: this.$store.getters.userType, // VENDOR BUYER curRole==='VENDOR'
      curOrderId: '',
      yesNoOptions: [
        { value: 'Y', label: this.$t('common.yes') },
        { value: 'N', label: this.$t('common.no') }
      ],
      form: {
        organizationId: '',
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
        payAccountPeriodCode: '',
        payAccountPeriodName: '',
        invoiceDate: parseTime(new Date(), '{y}-{m}-{d}', true),
        statementTotalAmount: null,
        invoiceTotalAmount: null,
        contractCode: null,
        accountPayableDealine: null,
        payAccountPeriod: null,
        ifPaperAttach: null,
        businessType: null,
        invoiceStatus: '',
        actualInvoiceAmountN: undefined,
        actualInvoiceAmountY: undefined,
        excluTaxTotalAmount: undefined,
        totalTax: undefined,
        taxTotalAmount: undefined,
        unPaidAmount: undefined,
        paidAmount: undefined,
        invoiceTax: undefined
      },
      advanceForm: {
        onlineInvoiceAdvances: [], // 预付申请明细
        statementDetailsTable: [] // 对账单明细
      },
      formLabelWidth: '120px',
      selectionItem: [],
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
      displayMaterialItem2: [],
      parentOrgTableDataPage2: {
        total: 0,
        pageNum: 1,
        pageSize: 20
      },
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
      dialogFormVisiblePunishes: false,
      formRules: {
        orgId: [{ required: true, message: this.$t('quota.orgIdTips') }], // 请选择业务实体
        organizationId: [
          {
            required: true,
            message: this.$t('purchaseDemand.organizationIdTips')
          }
        ], // 请选择业务实体
        costTypeCode: [{ required: true, message: this.$t('quota.costTypeTips') }], // 请选择成本类型
        invoiceDate: [
          {
            required: true,
            message: this.$t('purSettlementMod.invoiceDateTips')
          }
        ], // 请选择开票日期
        currencyName: [{ required: true, message: this.$t('vendorMod.msgCurrencyCode') }], // 请选择币种
        payAccountPeriodCode: [
          {
            required: true,
            message: this.$t('purSettlementMod.pleaseSelectBillingPeriod')
          }
        ] // 请选择账期
      },
      loading: false,
      sourceNumber: [], // 来源单号ID
      tempSourceNumber: [], // 来源单号临时
      orgList: [
        {
          organizationId: '6803628383141888',
          organizationCode: '10102',
          organizationName: this.$t('purSettlementMod.homeAppliancesDivision')
        }
      ], // 合作组织下拉
      vendorOptions: [], // 供应商下拉
      siteOptions: [],
      belongOprId: null,
      deptOptions: [],
      currencyList: [], // 币种
      onlineInvoicePunishes: [], // 绩效考核
      invoiceInformation: [],
      fileuploads: [],
      curOpt: 'add',
      isModify: false,
      activeDims: ['1', '2', '3', '4', '5', '6'],
      selectOrderData: [],
      rejectedDialog: false,
      rejectedModel: {
        // 驳回信息
        rejectedForm: {
          rejectReason: ''
        },
        rules: {
          rejectReason: [{ required: true, message: this.$t('bidMod.msgRejectReason') }] // 请输入驳回原因
        }
      },
      minusTax: null,
      minusTaxTotalAmount: null,
      minusNoTaxTotalAmount: null
    }
  },
  computed: {
    // ...mapGetters(['userInfo']),
    isDisabled () {
      // 采购已驳回/add/edit可编辑
      return this.curOpt !== 'add' && this.curOpt !== 'edit'
    },
    isReject () {
      return this.invoiceDetails.length >= 1
    }
  },
  watch: {
    dialogFormVisible (val) {
      if (val) {
        this.$nextTick(() => {
          this.$refs.refPrepayApplyInfo.doLayout()
        })
      }
    }
  },
  created () {
    this.curOpt = this.$attrs.params.flag
    this.form.vendorCode = this.userInfo.companyCode
    this.form.vendorName = this.userInfo.companyName
    this.form.vendorId = this.userInfo.companyId
    if (this.curOpt !== 'add') {
      // 编辑 查看
      this.curOrderId = this.$attrs.params.onlineInvoiceId
      this.getOrderFormDetail(this.curOrderId) // 查询
    }
  },
  methods: {
    invoicePreview (row) {
      Object.assign(this.previewObj, {
        isFileSource: true,
        fileuploadId: row.fileuploadId,
        fileSourceName: row.fileSourceName.split('.')[0]
      })
    },
    // 设置只读状态下也可点击预览
    setPreviewRead () {
      this.$nextTick(() => {
        let names = document.getElementsByClassName('invoice-preview')
        if (names.length > 0) {
          for (let item of names) {
            item.removeAttribute('disabled')
            item.classList.remove('is-disabled')
          }
        }
      })
    },
    // 预付申请明细 - 本次核销金额校验
    amountValidator (rule, value, callback) {
      const index = Number(rule.field.split('.')[1]) // 获取行号
      const sign = this.advanceForm.onlineInvoiceAdvances.some((row, i) => {
        if (index === i) {
          !row.curWrittenOffAmount && (row.curWrittenOffAmount = 0)
          return row.curWrittenOffAmount > row.unWrittenOffAmount
        }
      })
      // 本次核销金额不可大于未核销金额！
      sign && callback(this.$message.warning(this.$t('agentOnlineInvoice.prompt15')))
      callback()
    },
    // 对账单本次开票数量
    invoiceValidator (rule, value, callback) {
      const index = Number(rule.field.split('.')[1]) // 获取行号
      const sign = this.advanceForm.statementDetailsTable.some((row, i) => {
        if (index === i) {
          !row.invoiceQuantity && (row.invoiceQuantity = 0)
          return row.invoiceQuantity > row.notInvoiceQuantity
        }
      })
      // 本次开票数量不可大于未开票数量
      sign && callback(this.$message.warning(this.$t('agentOnlineInvoice.prompt16')))
      callback()
    },
    // 付款账期
    getPaymentDayByCode (val, dictItem) {
      this.form.payAccountPeriodCode = val
      this.form.payAccountPeriodName = dictItem.description
    },
    // 对账单明细计算未税总金额之和、含税金额之和
    calStatementReduce (statementDetailsTable) {
      let statementNoTax = 0 // 对账单明细未税总金额之和
      let statementTax = 0 // 对账单明细含税金额之和
      if (statementDetailsTable.length > 0) {
        statementNoTax = statementDetailsTable
          .map(row => {
            row.noTaxAmount = row.unitPriceExcludingTax * row.invoiceQuantity
            return row.type === 'RETURN' ? -row.noTaxAmount : row.noTaxAmount
          })
          .reduce((p, c) => (Number(p) || 0) + (Number(c) || 0), 0)

        statementTax = statementDetailsTable
          .map(row => {
            return row.type === 'RETURN' ? -row.taxAmount : row.taxAmount
          })
          .reduce((p, c) => (Number(p) || 0) + (Number(c) || 0), 0)
      }
      return { statementNoTax, statementTax }
    },
    // 绩效考核明细未税总金额、含税总金额计算
    calPunishesReduce (onlineInvoicePunishes) {
      let punishesNoTax = 0 // 绩效单未税总金额
      let punishesTax = 0 // 绩效单含税总金额
      if (onlineInvoicePunishes.length > 0) {
        punishesNoTax = onlineInvoicePunishes
          .map(row => row.actualAssessmentAmountN)
          .reduce((p, c) => (Number(p) || 0) + (Number(c) || 0), 0)

        punishesTax = onlineInvoicePunishes
          .map(row => row.actualAssessmentAmountY)
          .reduce((p, c) => (Number(p) || 0) + (Number(c) || 0), 0)
      }
      return { punishesNoTax, punishesTax }
    },
    // 系统税额计算
    setAmountCal (row, index) {
      if (row) {
        // 对账单明细行含税金额：含税单价 * 数量
        row.taxAmount = row.invoiceQuantity * row.unitPriceContainingTax
      }

      let { statementNoTax, statementTax } = this.calStatementReduce(this.advanceForm.statementDetailsTable)
      let { punishesNoTax, punishesTax } = this.calPunishesReduce(this.onlineInvoicePunishes)

      // 系统未税总金额 = 对账单明细未税金额之和 - 考核单未税金额之和
      this.form.excluTaxTotalAmount = statementNoTax - punishesNoTax
      // 系统含税总金额 = 对账单明细含税金额之和 - 考核单含税金额之和
      this.form.taxTotalAmount = statementTax - punishesTax
      // 系统税额 = 系统含税总金额 - 系统未税总金额
      this.form.totalTax = this.form.taxTotalAmount - this.form.excluTaxTotalAmount

      // 计算付款未付款金额
      this.setRowAmount()
    },
    // 预付款申请新增
    openAdvanceItem () {
      let checkArr = [
        'orgId',
        'organizationId',
        'vendorId',
        'currencyId',
        'taxKey',
        'payAccountPeriodCode'
      ]
      for (let key in this.form) {
        if (checkArr.includes(key)) {
          if (!this.form[key]) {
            return this.$message.warning(this.$t('vendorMod.pleasefinishRequired'))
          } else {
            this.$set(this.advanceParams, key, this.form[key])
          }
        }
      }
      this.dialogFormVisible = true
      this.queryItemList()
    },
    queryItemList () {
      const data = {
        ...this.advanceParams,
        ...this.parentOrgQueryForm
      }
      this.$http({
        url: '/api-sup-ce/ps/invoice/onlineInvoice/listPageOnlineInvoiceAdvanceApply',
        method: 'POST',
        data: data,
        loading: true
      }).then(res => {
        this.prepayApplyInfo = res.data.list
        this.parentOrgTableDataPage.total = res.data.total
        this.dialogFormVisible = true
      })
    },
    // 预付款 - 选择
    prepaySelectionChange (selection) {
      this.selectionPrepay = selection
    },
    // 预付款 - 双击
    prepayDBClick (val) {
      this.selectionPrepay = [val]
      this.affirmSelprepay()
    },
    // 预付款新增 - 确认
    affirmSelprepay () {
      if (this.selectionPrepay.length === 0) {
        return this.$message.warning(this.$t('common.msgSelectData'))
      }
      const ids = this.advanceForm.onlineInvoiceAdvances.map(item => item.advanceApplyId)

      this.selectionPrepay.forEach(item => {
        if (!ids.includes(item.advanceApplyId)) {
          this.advanceForm.onlineInvoiceAdvances.push(item)
        }
      })

      this.setRowAmount()
      this.dialogFormVisible = false
    },
    // 绩效考核 - 添加
    addOnlineInvoicePunishes () {
      if (!this.form.orgId) {
        // 请选择业务实体
        return this.$message.error(this.$t('dataConfMod.msgPleaseSelectOrg'))
      }
      if (!this.form.vendorCode) {
        // 请先选择供应商
        return this.$message.error(this.$t('bid_mod.setPermissionError'))
      }
      this.dialogFormVisiblePunishes = true
      this.queryPunishes()
    },
    // 绩效考核 - 搜索
    queryPunishes () {
      this.$http({
        url: '/api-sup-ce/ps/invoice/onlineInvoice/listPageOnlineInvoicePunish',
        method: 'POST',
        data: {
          pageNum: this.punishesPage.pageNum,
          pageSize: this.punishesPage.pageSize,
          organizationId: this.form.orgId,
          ceeaVendorId: this.form.vendorId
        },
        loading: true
      }).then(res => {
        this.displayPunishes = res.data.list
        this.punishesPage.total = res.data.total
        this.dialogFormVisiblePunishes = true
      })
    },
    punishesCurrentChange (num) {
      this.punishesPage.pageNum = num
      this.queryPunishes()
    },
    punishesSizeChange (size) {
      this.punishesPage.pageSize = size
      this.queryPunishes()
    },
    // 绩效考核 - 选择
    punishesHandleSelectionChange (selection) {
      this.selectionPunishes = selection
    },
    // 绩效考核 - 双击
    punishesHandleDBClick (val) {
      this.selectionPunishes = [val]
      this.addConfirmPunishes()
    },
    // 绩效考核 - 确定
    addConfirmPunishes () {
      if (this.selectionPunishes.length == 0) {
        return this.$message.warning(this.$t('common.msgSelectData'))
      }
      const ids = this.onlineInvoicePunishes.map(item => item.lastUpdatedId)

      this.selectionPunishes.forEach(item => {
        if (!ids.includes(item.lastUpdatedId)) {
          this.onlineInvoicePunishes.push(item)
        }
      })

      this.setAmountCal()
      this.dialogFormVisiblePunishes = false
    },
    // 考核类型---显示
    filterCatHandler (row, column, cellValue, index) {
      return cellValue ? this.$getDictLabel('ASSESSMENT_BILL_TYPE', cellValue) : cellValue
    },
    filterCurrency (row, column, cellValue, index) {
      return cellValue ? this.$getDictLabel('currency', cellValue) : cellValue
    },
    // 对账单明细 - 删除
    deleteStatement (index) {
      this.advanceForm.statementDetailsTable.splice(index, 1)
      this.setAmountCal()
    },
    // 对账单明细 - 选择
    handleSelectionChange (selection) {
      this.selectionItem = selection
    },
    // 对账单明细 - 双击
    handleItemDBClick (val) {
      this.selectionItem = [val]
      this.addOneItem()
    },
    // 对账单明细 - 保存
    addOneItem () {
      if (this.selectionItem.length === 0) {
        return this.$message.warning(this.$t('common.msgSelectData'))
      }
      const ids = this.advanceForm.statementDetailsTable.map(item => item.invoiceDetailId)

      this.selectionItem.forEach(item => {
        if (!ids.includes(item.invoiceDetailId)) {
          this.advanceForm.statementDetailsTable.push(item)
        }
      })

      this.setAmountCal()
      this.statementDialogVisible = false
    },
    // 编辑发票行更新
    editInvoiceRow (row) {
      console.log(row, 'editInvoiceRow row')
      this.setNoTaxCal()
    },
    // 新增发票 - 保存
    saveInvoice (fileList) {
      this.invoiceInformation.push(...fileList)
      this.setNoTaxCal()
    },
    // 发票下载
    downloadInvoice (row) {
      if (row.fileuploadId) {
        downloadWithParam(
          row.fileuploadId,
          row.fileSourceName,
        ).catch(() => {
          this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
        })
      } else {
        throw new Error('AttachId is null.')
      }
    },
    // 删除开票信息
    async deleteInvoice (index) {
      const sign = await this.$confirm(this.$t('agentOnlineInvoice.prompt14'), { // 确定删除该发票？
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
      if (sign !== 'confirm') return
      this.invoiceInformation.splice(index, 1)
      this.setNoTaxCal()
    },
    // 发票选中
    handleChangeInvoice (select) {
      this.selectInvoice = select
    },
    // 批量删除
    deleteBatchInvoice () {
      const ids = this.selectInvoice.map(select => select.fileuploadId)
      this.invoiceInformation = this.invoiceInformation.filter(row => !ids.includes(row.fileuploadId))
    },
    // 发票信息计算
    setNoTaxCal () {
      // 发票明细未税金额之和
      const notaxs = this.invoiceInformation.map(item => item.noTaxTotalAmount)
      this.form.actualInvoiceAmountN =
        notaxs.length > 0 ? notaxs.reduce((p, c) => (Number(p) || 0) + (Number(c) || 0)) : 0

      // 发票含税总金额 = 发票明细含税金额之和
      const taxs = this.invoiceInformation.map(item => item.totalAmount)
      this.form.actualInvoiceAmountY =
        taxs.length > 0 ? taxs.reduce((p, c) => (Number(p) || 0) + (Number(c) || 0)) : 0

      // 发票税额
      const invoiceTaxs = this.invoiceInformation.map(item => item.totalTax)
      this.form.invoiceTax =
        invoiceTaxs.length > 0
          ? invoiceTaxs.reduce((p, c) => (Number(p) || 0) + (Number(c) || 0))
          : 0
    },
    // 新增发票 - 选择
    handleInvoieSelection (select) {
      this.invoiceSelects = select
    },
    // 改变 currentNum
    changeCurrentIndex (currentNum) {
      this.viewIndex = currentNum
      this.queryStatementContent()
    },
    // 改变 currentSize
    changeCurrentSize (currentSize) {
      this.viewSize = currentSize
      this.queryStatementContent()
    },
    // 对账单明细新增-查询
    queryStatementContent () {
      this.$http({
        url: '/api-sup-ce/ps/invoice/onlineInvoice/listPageOnlineInvoiceDetail',
        method: 'POST',
        data: {
          ...this.statementQueryForm,
          pageNum: this.viewIndex,
          pageSize: this.viewSize
        },
        laoding: true
      }).then(res => {
        if (res && res.data) {
          this.statementTable = res.data.list
          this.queryTotal = res.data.total
        }
      })
    },
    // 币种选择
    getCurrencyObj (val, scope) {
      scope.currencyId = val ? val.currencyId : ''
      scope.currencyCode = val ? val.currencyCode : ''
      scope.currencyName = val ? val.currencyName : ''
    },
    // 选择税率
    taxRateChangeHandel (value, dictItem) {
      this.form.taxRate = dictItem.key // 税率值
    },
    // 校验文件格式
    checkFileFormat (file) {
      console.log(file, 'checkFile')
      if (!file.type.includes('image') && !file.type.includes('pdf')) {
        return {
          status: 'error',
          msg: `${file.name}: ${this.$t('agentOnlineInvoice.prompt12')}` // 上传失败,仅支持JPG、PNG、JPEG、PDF、OFD格式
        }
      }

      if (file.size / 1024 / 1024 > 10) {
        return {
          status: 'error',
          msg: `${file.name}: ${this.$t('agentOnlineInvoice.prompt13')}, ${this.$t('purSettlementMod.uploadTableMes2')}` // 上传失败,上传文件大小不能超过 10MB!
        }
      }

      return {
        status: 'ok',
        data: file
      }
    },
    // 对账单明细新增
    addStatementDetails () {
      let checkArr = [
        'orgId',
        'organizationId',
        'vendorId',
        'currencyId',
        'taxKey',
        'payAccountPeriodCode'
      ]
      for (let key in this.form) {
        if (checkArr.includes(key)) {
          if (!this.form[key]) {
            // 请输入单据必填信息
            return this.$message.warning(this.$t('vendorMod.pleasefinishRequired'))
          } else {
            this.$set(this.statementQueryForm, key, this.form[key])
          }
        }
      }
      this.statementDialogVisible = true
    },
    // 查询单据详情
    async getOrderFormDetail (onlineInvoiceId) {
      await this.$http({
        url: '/api-sup-ce/sup/invoice/onlineInvoice/getOnlineInvoiceSaveDTO',
        method: 'GET',
        params: { onlineInvoiceId: onlineInvoiceId },
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.form = res.data.onlineInvoice || {} // 头信息
          this.advanceForm.statementDetailsTable = res.data.onlineInvoiceDetails // 对账单明细
          this.onlineInvoicePunishes = res.data.onlineInvoicePunishes // 扣罚返利明细
          this.advanceForm.onlineInvoiceAdvances = res.data.onlineInvoiceAdvanceApplies // 预付申请明细
          this.invoiceInformation = res.data.onlineInvoiceOcrInvoices || [] // 发票信息
          this.fileuploads = res.data.onlineInvoiceAttaches // 附件信息
          // 只读状态下设置发票可预览
          this.setPreviewRead()
        }
      })
    },
    // 重置
    resetHandle () {
      this.$http({
        url: '/api-sup-ce/sup/invoice/onlineInvoice/restart',
        method: 'GET',
        params: { onlineInvoiceId: this.$attrs.params.onlineInvoiceId },
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.getOrderFormDetail(res.data)
        }
      })
    },
    // 新增税控明细
    addFileuploads () {
      this.fileuploads.push({
        attachId: '',
        fileuploadId: '',
        attachName: ''
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
    // 删除罚扣款单
    delInvoicePunishes (index) {
      this.onlineInvoicePunishes.splice(index, 1)
      this.setAmountCal()
    },
    // 删除预付款
    async delonlineInvoiceAdvances (index, row) {
      this.advanceForm.onlineInvoiceAdvances.splice(index, 1)
      this.setRowAmount()
    },
    handleScriptProgress (percent) {},
    outerButtonClick (index) {
      this.curRowIndex = index
    },
    outerHandleUploadSuccess (file) {
      const { id, name, createdBy, creationDate } = file
      this.fileuploads[this.curRowIndex].fileuploadId = id.toString()
      this.fileuploads[this.curRowIndex].fileSourceName = name
      this.fileuploads[this.curRowIndex].createdBy = createdBy
      this.fileuploads[this.curRowIndex].creationDate = creationDate
    },
    // 移除
    outerHandleRemove (docId) {},
    // 删除银行证明文件
    outerHandleAttachmentRemove (row) {
      row.fileuploadId = ''
      row.fileSourceName = ''
    },
    // 暂存/提交
    async saveOrSubmitData (type) {
      if (type === 'SUBMIT') {
        const { flag, message } = await this.getCheckForm()
        if (flag) {
          this.submitBill(type)
        } else {
          this.__focus_error__(message)
        }
      } else {
        this.saveHandle(type)
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
      const formFiled1 = await this.formValidate('advanceForm')

      if (!formFiled.flag && Object.keys(formFiled.obj).length > 0) {
        const warnObj = Object.keys(formFiled.obj)[0]
        return {
          flag: formFiled.flag,
          message: formFiled.obj[warnObj][0].message
        }
      }

      if (!formFiled1.flag && Object.keys(formFiled1.obj).length > 0) {
        const warnObj1 = Object.keys(formFiled1.obj)[0]
        return {
          flag: formFiled1.flag,
          message: formFiled1.obj[warnObj1][0].message
        }
      }
      return { flag: true }
    },
    // 提交
    async submitBill (type) {
      // 明细行可以修改的情况下要先掉暂存接口，获取最新数据，避免其他人操作单据导致数量对不上
      ['add', 'edit'].includes(this.curOpt) && await this.saveHandle(type)

      const { code } = await this.$http({
        url: '/api-sup-ce/sup/invoice/onlineInvoice/submit',
        method: 'POST',
        data: {
          onlineInvoice: this.form,
          onlineInvoiceDetails: this.advanceForm.statementDetailsTable, // 对账单明细
          onlineInvoicePunishes: this.onlineInvoicePunishes, // 扣罚、返利明细
          onlineInvoiceAdvanceApplies: this.advanceForm.onlineInvoiceAdvances, // 预付申请明细
          onlineInvoiceOcrInvoices: this.invoiceInformation, // 发票信息
          onlineInvoiceAttaches: this.fileuploads // 附件
        },
        loading: true
      })

      if (code === '0') {
        this.$message.success(this.$t('common.successSubmit'))
        this.backTo()
      }
    },
    // 暂存
    async saveHandle (type) {
      const params = {
        onlineInvoice: this.form,
        onlineInvoiceDetails: this.advanceForm.statementDetailsTable, // 对账单明细
        onlineInvoicePunishes: this.onlineInvoicePunishes, // 扣罚、返利明细
        onlineInvoiceAdvanceApplies: this.advanceForm.onlineInvoiceAdvances, // 预付申请明细
        onlineInvoiceOcrInvoices: this.invoiceInformation, // 发票信息
        onlineInvoiceAttaches: this.fileuploads // 附件
      }
      const { code, data } = await this.$http({
        url: '/api-sup-ce/sup/invoice/onlineInvoice/saveTemporary',
        method: 'POST',
        data: params,
        loading: true
      })

      if (code === '0') {
        await this.getOrderFormDetail(data)
        type === 'SAVE' && this.$message.success(this.$t('common.successSave'))
      }
    },
    async dataHandle (type, url, params) {
      this.$http({
        url,
        method: 'POST',
        data: params,
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        if (type === 'SAVE') {
          this.getOrderFormDetail(res.data)
        } else {
          this.$message.success(this.$t('common.success'))
          this.backTo()
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
        const _this = this
        if (valid) {
          const parame = {}
          parame.onlineInvoiceId = this.curOrderId
          parame.rejectReason = _this.rejectedModel.rejectedForm.rejectReason
        }
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
    // 预付款申请 num
    parentDataCurrentChange (num) {
      this.parentOrgQueryForm.pageNum = num
      this.queryItemList()
    },
    // 预付款申请 size
    parentDataSizeChange (size) {
      this.parentOrgQueryForm.pageSize = size
      this.queryItemList()
    },
    // 已付款未付款金额计算
    setRowAmount (row, index) {
      // if (row.curWrittenOffAmount > row.unWrittenOffAmount) {
      //   // 本次核销金额不可大于可核销金额！
      //   return this.$message.warning(this.$t('purSettlementMod.paidAmoundCheck'))
      // }
      // 已付款金额：当前开票单的本次核销金额（含税）
      this.form.paidAmount = this.advanceForm.onlineInvoiceAdvances
        .map(v => v.curWrittenOffAmount)
        .reduce((p, c) => (Number(p) || 0) + (Number(c) || 0), 0)

      // 未付款金额：系统含税总金额 - 已付款金额
      this.form.unPaidAmount = this.form.taxTotalAmount - this.form.paidAmount
    },
    formatData (row, column, cellValue, index) {
      return cellValue ? this.$getDictLabel('WAREHOURING_RETURN_DETAIL', cellValue) : cellValue
    },
    backTo () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('agentOnlineInvoiceList.getQuerydata')
    }
  }
}
</script>
<style scoped lang="scss">
.the-purInvoice-detail {
  .el-table .el-date-editor {
    width: 135px;
  }
  .btn_line {
    margin: 0 0 10px 0;
  }
  .invoice-btn {
    display: inline-block;
    margin-right: 8px;
  }
  :deep(.voice-title) {
    color: #46a6ff;
    font-size: 14px;
    margin-left: 18px;
  }
  .toRequired {
    color: #ff4949;
    padding-right: 2px;
  }
  .invoid-input {
    outline-style: none;
    border: 1px solid #ccc;
    width: 100%;
    line-height: 24px;
    min-height: 24px;
    border-radius: 2px;
    padding: 0 8px;
    &:focus {
      border-color: #46a6ff;
      outline: 0;
    }
  }
  .input-number-precision {
    width: 100%;
    :deep(.el-input__inner) {
      text-align: left;
      padding-left: 8px;
    }
  }
  :deep(.invoice-table) {
    .el-table__row {
      td {
        position: relative;
      }
      .tooltip-show {
        width: 100%;
        visibility: hidden;
        position: absolute;
        left: 0;
        line-height: 34px;
      }
    }
  }
}
</style>
