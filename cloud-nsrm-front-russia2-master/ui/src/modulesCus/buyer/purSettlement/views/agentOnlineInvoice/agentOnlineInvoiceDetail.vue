<template>
  <el-container class="flex-container the-purInvoice-detail" direction="vertical">
    <el-main>
      <el-collapse v-model="activeDims" class="tab-form-style">
        <el-form
          ref="formRef"
          :model="form"
          :rules="formRules"
          label-position="top"
          class="form-incontainer"
          :disabled="isReadonly"
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
                    :disabled="isReadonly || !isEmptyDetailTable || isFormHeadDisable"
                    @select="selectHandler"
                  />
                </el-form-item>
              </srm-col>
              <!-- 开票主体 -->
              <srm-col>
                <el-form-item :label="$t('cusEntry.orderMod.invoiceBody')">
                  <el-input v-model="form.extPrincipalName" disabled />
                </el-form-item>
              </srm-col>
              <!-- 供应商 -->
              <srm-col>
                <el-form-item :label="$t('common.vendor')" prop="vendorName">
                  <QuickSearch
                    :disabled="isReadonly || !isEmptyDetailTable || isFormHeadDisable"
                    :show-input="form.vendorName"
                    show-key="vendorCode"
                    :scope-data="form"
                    name="scc_sup_company_info_all"
                    @close-quicksearch="getVendorObj"
                  />
                </el-form-item>
              </srm-col>
              <!-- 币种 -->
              <srm-col>
                <el-form-item :label="$t('quota.currency')" prop="currencyCode">
                  <dict-select
                    v-model="form.currencyCode"
                    code="currency"
                    :disabled="isReadonly || !isEmptyDetailTable || isFormHeadDisable"
                    @change-value="getCurrencyObj"
                  />
                </el-form-item>
              </srm-col>
              <!-- 单据来源 -->
              <srm-col>
                <el-form-item :label="$t('advancePayment.billType')" prop="extSource">
                  <dict-select
                    v-model="form.extSource"
                    code="ONLINE_INVOICE_SOURCE"
                    :clearable="false"
                    :disabled="isReadonly || !isEmptyDetailTable"
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
              <!-- 是否代开发票 -->
              <srm-col>
                <el-form-item :label="$t('cusEntry.orderMod.ifInvoice')">
                  <dict-select
                    v-model="form.extBehalfInvoice"
                    code="YES_OR_NO"
                    :clearable="false"
                  />
                </el-form-item>
              </srm-col>
              <!-- 是否进项税转出 -->
              <srm-col>
                <el-form-item>
                  <template #label>
                    <div>
                      <span>
                        {{ $t('cusEntry.orderMod.ifInputTax') }}
                      </span>
                      <el-tooltip
                        class="item"
                        effect="dark"
                        :content="$t('cusEntry.orderMod.ifInputTaxMsg')"
                        placement="top"
                      >
                        <i class="el-icon-question" />
                      </el-tooltip>
                    </div>
                  </template>
                  <dict-select
                    v-model="form.extInputTax"
                    code="YES_OR_NO"
                    :clearable="false"
                  />
                </el-form-item>
              </srm-col>
              <!-- 应开票含税总金额 -->
              <srm-col>
                <el-form-item>
                  <template #label>
                    <div>
                      <span>
                        {{ $t('cusEntry.orderMod.systemTotalAmountTax') }}
                      </span>
                      <el-tooltip
                        class="item"
                        effect="dark"
                        :content="$t('cusEntry.orderMod.totalAmountTaxedCal')"
                        placement="top"
                      >
                        <i class="el-icon-question" />
                      </el-tooltip>
                    </div>
                  </template>
                  <el-input-number
                    v-model="form.taxTotalAmount"
                    :controls="false"
                    :precision="2"
                    disabled
                    class="input-number-precision"
                  />
                </el-form-item>
              </srm-col>
              <!-- 应开票未税总金额 -->
              <srm-col>
                <el-form-item>
                  <template #label>
                    <div>
                      <span>
                        {{ $t('cusEntry.orderMod.totalAmountNotTaxed') }}
                      </span>
                      <el-tooltip
                        class="item"
                        effect="dark"
                        :content="$t('cusEntry.orderMod.totalAmountNotTaxedCal')"
                        placement="top"
                      >
                        <i class="el-icon-question" />
                      </el-tooltip>
                    </div>
                  </template>
                  <el-input-number
                    v-model="form.excluTaxTotalAmount"
                    :controls="false"
                    :precision="2"
                    disabled
                    class="input-number-precision"
                  />
                </el-form-item>
              </srm-col>
              <!-- 应开票总税额 -->
              <srm-col>
                <el-form-item :label="$t('cusEntry.orderMod.totalSystemTax')">
                  <el-input-number
                    v-model="form.totalTax"
                    :controls="false"
                    :precision="2"
                    disabled
                    class="input-number-precision"
                  />
                </el-form-item>
              </srm-col>
              <!-- 是否免赠 -->
              <srm-col>
                <el-form-item :label="$t('cusEntry.orderMod.ifFree')">
                  <dict-select
                    v-model="form.extFreeOfCharge"
                    code="YES_OR_NO"
                    :clearable="false"
                  />
                </el-form-item>
              </srm-col>
              <!-- 发票含税总金额 = 发票明细含税金额之和 -->
              <srm-col>
                <el-form-item>
                  <template #label>
                    <div>
                      <span>
                        {{ $t('purSettlementMod.totalAmountInvoiceTax') }}
                      </span>
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
                    disabled
                    :precision="2"
                    :controls="false"
                    class="input-number-precision"
                  />
                </el-form-item>
              </srm-col>
              <!-- 发票未税总金额=发票明细未税总金额之和 -->
              <srm-col>
                <el-form-item>
                  <template #label>
                    <div>
                      <span>
                        {{ $t('purSettlementMod.totalAmountInvoiceNoTax') }}
                      </span>
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
                    disabled
                    :precision="2"
                    :controls="false"
                    class="input-number-precision"
                  />
                </el-form-item>
              </srm-col>
              <!-- 发票总税额 -->
              <srm-col>
                <el-form-item :label="$t('purSettlementMod.invoiceTotalTax')">
                  <el-input-number
                    v-model="form.invoiceTax"
                    disabled
                    :precision="2"
                    :controls="false"
                    class="input-number-precision"
                  />
                </el-form-item>
              </srm-col>
              <!-- 创建人 -->
              <srm-col>
                <el-form-item :label="$t('common.creator')" prop="createdUserName">
                  <el-input v-model="form.createdUserName" disabled />
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
        </el-form>
        <el-form
          ref="statementDetailsTableRef"
          :model="detailsForm"
          :disabled="isReadonly"
        >
          <!-- 对账单明细 -->
          <el-collapse-item v-if="form.extSource==='INVOICE_NOTICE'" :title="$t('accountMod.statementDetail1')" name="2">
            <p class="btn_line">
              <el-button
                v-if="!isReadonly"
                type="primary"
                @click="addStatementDetails"
              >
                {{ $t('common.add') }}
              </el-button>
            </p>
            <el-table
              :data="detailsForm.statementDetailsTable"
              border
              style="width: 100%"
              max-height="240px"
            >
              <!-- 序号 -->
              <el-table-column
                align="center"
                type="index"
                fixed="left"
                :label="$t('common.sort')"
                width="60"
              />
              <!-- 对账单号 -->
              <el-table-column
                align="center"
                prop="invoiceNoticeNumber"
                :label="$t('purSettlementMod.statementNumber')"
                minWidth="150"
                show-overflow-tooltip
              />
              <!-- 入库/退货单号 -->
              <el-table-column
                align="center"
                prop="receiveOrderNo"
                :label="$t('cusEntry.orderMod.inboundReturnOrderNo')"
                minWidth="120"
                show-overflow-tooltip
              />
              <!-- 入库/退货行号 -->
              <el-table-column
                align="center"
                prop="receiveOrderLineNo"
                :label="$t('cusEntry.orderMod.inboundReturnLineNo')"
                minWidth="120"
                show-overflow-tooltip
              />
              <!-- 事务类型 -->
              <el-table-column
                align="center"
                prop="type"
                :label="$t('cusEntry.supplement20250121.storageType')"
                :formatter="(row, column, cellValue) => cellValue ? this.$getDictLabel('WAREHOURING_RETURN_DETAIL', cellValue) : null"
                minWidth="100"
                show-overflow-tooltip
              />
              <!-- 入库时间 -->
              <el-table-column
                align="center"
                prop="receiveDate"
                :label="$t('orderMod.warehouseTime')"
                minWidth="120"
                :formatter="(row, column, cellValue) => $parseTime(cellValue)"
                show-overflow-tooltip
              />
              <!-- 采购订单号 -->
              <el-table-column
                align="center"
                prop="orderNumber"
                :label="$t('purSettlementMod.orderNumber')"
                minWidth="150"
                show-overflow-tooltip
              />
              <!-- 订单行号 -->
              <el-table-column
                align="center"
                prop="lineNum"
                :label="$t('orderMod.orderLineNum')"
                minWidth="100"
                show-overflow-tooltip
              />
              <!-- 物料编码 -->
              <el-table-column
                align="center"
                prop="itemCode"
                :label="$t('common.materialCode')"
                minWidth="150"
                show-overflow-tooltip
              />
              <!-- 物料名称 -->
              <el-table-column
                align="center"
                prop="itemName"
                :label="$t('common.materialName')"
                minWidth="150"
                show-overflow-tooltip
              />
              <!-- 单位 -->
              <el-table-column
                align="center"
                prop="unit"
                :label="$t('dataConfMod.unit')"
                minWidth="100"
                show-overflow-tooltip
              />
              <!-- 可开票数量 -->
              <el-table-column
                align="center"
                prop="notInvoiceQuantity"
                :label="$t('purSettlementMod.invoicesAvailable')"
                minWidth="120"
                show-overflow-tooltip
              />
              <!-- 本次开票数量 -->
              <el-table-column
                align="center"
                prop="invoiceQuantity"
                :label="$t('purSettlementMod.invoiceQuantity')"
                :render-header="_addStarToColumn"
                minWidth="120"
              >
                <template slot-scope="scope">
                  <el-form-item
                    :prop="'statementDetailsTable.' + scope.$index + '.invoiceQuantity'"
                    :rules="{ required: true, validator: invoiceValidator, trigger: 'blur' }"
                  >
                    <el-input-number
                      v-model="scope.row.invoiceQuantity"
                      :min="0"
                      :controls="false"
                      class="input-number-precision"
                      @blur="setAmountCal(scope.row)"
                    />
                  </el-form-item>
                </template>
              </el-table-column>
              <!-- 未税单价 -->
              <el-table-column
                align="center"
                prop="unitPriceExcludingTax"
                :label="$t('purSettlementMod.unitPriceNoTax')"
                minWidth="100"
                show-overflow-tooltip
              />
              <!-- 税率 -->
              <el-table-column
                align="center"
                prop="taxRate"
                :label="$t('bidMod.taxRate2')"
                minWidth="100"
                show-overflow-tooltip
              />
              <!-- 未税金额 -->
              <el-table-column
                align="center"
                prop="noTaxAmount"
                :label="$t('contractMod.unAmount')"
                minWidth="100"
                show-overflow-tooltip
              />
              <!-- 尾差调整 -->
              <el-table-column
                align="center"
                prop="extAdjustAmount"
                :label="$t('cusEntry.supplement20250205.extAdjustAmount')"
                minWidth="100"
                :render-header="_addStarToColumn"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <el-form-item
                    :prop="'statementDetailsTable.' + scope.$index + '.extAdjustAmount'"
                    :rules="{ required: true, validator: extAdjustAmountValidator, trigger: 'blur' }"
                  >
                    <el-input-number
                      v-model="scope.row.extAdjustAmount"
                      :controls="false"
                      class="input-number-precision"
                      @blur="setAmountCal(scope.row)"
                    />
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="tax"
                :label="$t('contractMod.taxQuota')"
                minWidth="100"
                show-overflow-tooltip
              />
              <!-- 含税金额 -->
              <el-table-column
                align="center"
                prop="taxAmount"
                :label="$t('contractMod.amount2')"
                minWidth="100"
                show-overflow-tooltip
              />
              <!-- 转出税额 -->
              <el-table-column
                v-if="form.extInputTax === 'Y'"
                align="center"
                prop="extInputTaxAmount"
                :label="$t('cusEntry.supplement20250205.extInputTaxAmount')"
                minWidth="100"
                :render-header="_addStarToColumn"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <el-form-item
                    :prop="'statementDetailsTable.' + scope.$index + '.extInputTaxAmount'"
                    :rules="{ required: true, validator: extInputTaxAmountValidator, trigger: 'blur' }"
                  >
                    <el-input-number
                      v-model="scope.row.extInputTaxAmount"
                      :controls="false"
                      :min="0"
                      class="input-number-precision"
                    />
                  </el-form-item>
                </template>
              </el-table-column>
              <!-- SAP成本中心 -->
              <el-table-column
                v-if="form.extInputTax === 'Y'"
                align="center"
                prop="extSapCostCode"
                :label="$t('cusEntry.supplement20250205.extSapCostCode')"
                minWidth="140"
                :render-header="_addStarToColumn"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <el-form-item
                    :prop="'statementDetailsTable.' + scope.$index + '.extSapCostCode'"
                    :rules="{ required: true, message: $t('cusEntry.supplement20250205.extSapCostCodeTip'), trigger: 'blur' }"
                  >
                    <QuickSearch
                      :disabled="isReadonly"
                      :show-input="scope.row.extSapCostCode"
                      show-key="costnumber"
                      :scope-data="scope.row"
                      name="npm_invoice_cost_center"
                      @close-quicksearch="getSAPObj"
                    />
                  </el-form-item>
                </template>
              </el-table-column>
              <!-- SAP成本中心内容 -->
              <el-table-column
                v-if="form.extInputTax === 'Y'"
                align="center"
                prop="extSapCostContent"
                :label="$t('cusEntry.supplement20250205.extSapCostContent')"
                show-overflow-tooltip
                minWidth="140"
              />
              <!-- 发票用途 -->
              <el-table-column
                v-if="form.extInputTax === 'Y'"
                align="center"
                prop="extInvoiceUsage"
                :label="$t('cusEntry.supplement20250205.extInvoiceUsage')"
                minWidth="140"
                :render-header="_addStarToColumn"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <el-form-item
                    :prop="'statementDetailsTable.' + scope.$index + '.extInvoiceUsage'"
                    :rules="{ required: true, message: $t('cusEntry.supplement20250205.extInvoiceUsageTip'), trigger: 'blur' }"
                  >
                    <dict-select
                      v-model="scope.row.extInvoiceUsage"
                      code="ONLINE_INVOICE_USETO"
                    />
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column v-if="!isReadonly" :label="$t('common.operation')" width="80" fixed="right">
                <template slot-scope="scope">
                  <el-button type="text" @click="deleteStatement(scope.$index, scope.row)">
                    {{ $t('common.delete') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
        </el-form>
        <el-form
          ref="checkOrderDetailsTableRef"
          :model="detailsForm"
          :disabled="isReadonly"
        >
          <!-- 验收单明细 -->
          <el-collapse-item v-if="form.extSource==='CHECK_ORDER'" :title="$t('cusEntry.orderMod.checkOrderDetail')" name="2">
            <p class="btn_line">
              <el-button
                v-if="!isReadonly"
                type="primary"
                class="detail-pbtn"
                @click="addCheckOrderDetails"
              >
                {{ $t('common.add') }}
              </el-button>
            </p>
            <el-table
              :data="detailsForm.checkOrderDetailsTable"
              border
              style="width: 100%"
              max-height="240px"
            >
              <el-table-column
                align="center"
                type="index"
                fixed="left"
                :label="$t('common.sort')"
                width="60"
              />
              <!-- 验收单号(没字段，先存到对账单号里) -->
              <el-table-column
                align="center"
                prop="invoiceNoticeNumber"
                :label="$t('contractMod.acceptNumber')"
                minWidth="120"
                show-overflow-tooltip
              />
              <el-table-column
                align="center"
                prop="orderNumber"
                :label="$t('purSettlementMod.orderNumber')"
                minWidth="150"
                show-overflow-tooltip
              />
              <el-table-column
                align="center"
                prop="lineNum"
                :label="$t('orderMod.orderLineNum')"
                minWidth="100"
                show-overflow-tooltip
              />
              <el-table-column
                align="center"
                prop="categoryName"
                :label="$t('sourcingBuyer.categoryType')"
                minWidth="150"
                show-overflow-tooltip
              />
              <el-table-column
                align="center"
                prop="itemCode"
                :label="$t('common.materialCode')"
                minWidth="150"
                show-overflow-tooltip
              />
              <el-table-column
                align="center"
                prop="itemName"
                :label="$t('common.materialName')"
                minWidth="150"
                show-overflow-tooltip
              />
              <el-table-column
                align="center"
                prop="unit"
                :label="$t('dataConfMod.unit')"
                minWidth="80"
                show-overflow-tooltip
              />
              <el-table-column
                align="center"
                prop="notInvoiceQuantity"
                :label="$t('purSettlementMod.invoicesAvailable')"
                minWidth="120"
                show-overflow-tooltip
              />
              <el-table-column
                align="center"
                prop="invoiceQuantity"
                :label="$t('purSettlementMod.invoiceQuantity')"
                :render-header="_addStarToColumn"
                show-overflow-tooltip
                minWidth="120"
              >
                <template slot-scope="scope">
                  <el-form-item
                    :prop="'checkOrderDetailsTable.' + scope.$index + '.invoiceQuantity'"
                    :rules="{ required: true, validator: invoiceCheckValidator, trigger: 'blur' }"
                  >
                    <el-input-number
                      v-model="scope.row.invoiceQuantity"
                      :min="0"
                      :controls="false"
                      class="input-number-precision"
                      @blur="setAmountCal(scope.row)"
                    />
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="unitPriceExcludingTax"
                :label="$t('purSettlementMod.unitPriceNoTax')"
                minWidth="100"
                show-overflow-tooltip
              />
              <el-table-column
                align="center"
                prop="taxRate"
                :label="$t('bidMod.taxRate2')"
                minWidth="100"
                show-overflow-tooltip
              />
              <el-table-column
                align="center"
                prop="noTaxAmount"
                :label="$t('contractMod.unAmount')"
                minWidth="100"
                show-overflow-tooltip
              />
              <el-table-column
                align="center"
                prop="tax"
                :label="$t('contractMod.taxQuota')"
                minWidth="100"
                show-overflow-tooltip
              />
              <el-table-column
                align="center"
                prop="taxAmount"
                :label="$t('contractMod.amount2')"
                minWidth="100"
                show-overflow-tooltip
              />
              <el-table-column v-if="!isReadonly" :label="$t('common.operation')" width="80" fixed="right">
                <template slot-scope="scope">
                  <el-button type="text" @click="deleteCheckOrderDetails(scope.$index, scope.row)">
                    {{ $t('common.delete') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
        </el-form>
        <el-form
          ref="invoiceTableRef"
          :model="detailsForm"
          :disabled="isReadonly"
        >
          <!-- 发票信息 -->
          <el-collapse-item :title="$t('accountMod.invoiceInfo')" name="3">
            <p class="btn_line">
              <!-- 新增发票 -->
              <el-button
                v-if="!isReadonly"
                type="primary"
                class="detail-pbtn"
                :disabled="form.extFreeOfCharge==='Y'"
                @click="addInvoice"
              >
                {{ $t('common.add') }}
              </el-button>
            </p>
            <el-table
              :data="detailsForm.invoiceTable"
              border
              style="width: 100%; margin-bottom: 20px;"
              max-height="240px"
            >
              <!-- 序号 -->
              <el-table-column
                align="center"
                fixed="left"
                type="index"
                :label="$t('common.sort')"
                width="60"
              />
              <!-- 发票类型 -->
              <el-table-column
                align="center"
                prop="invoiceType"
                :label="$t('accountMod.invoiceType')"
                :formatter="(row, colum, value) => $getDictLabel('ONLINE_INVOICE_TYPE', value)"
                show-overflow-tooltip
                minWidth="120"
              />
              <!-- 采购方税号 -->
              <!-- <el-table-column
                align="center"
                prop="purchaserRegisterNum"
                :label="$t('purSettlementMod.purchaserRegisterNum')"
                show-overflow-tooltip
                minWidth="150"
              /> -->
              <!-- 发票代码 -->
              <el-table-column
                align="center"
                prop="invoiceCode"
                :label="$t('purSettlementMod.invoiceCode')"
                show-overflow-tooltip
                minWidth="150"
              />
              <!-- 发票号码 -->
              <el-table-column
                align="center"
                prop="invoiceNum"
                :label="$t('purSettlementMod.invoiceNum')"
                show-overflow-tooltip
                minWidth="150"
              />
              <!-- 数电号码 -->
              <el-table-column
                align="center"
                prop="checkCode"
                :label="$t('cusEntry.supplement20250205.checkCode')"
                show-overflow-tooltip
                minWidth="120"
              />
              <!-- 开票日期 -->
              <el-table-column
                align="center"
                prop="invoiceDate"
                :label="$t('purSettlementMod.invoiceDate2')"
                :formatter="(row, column, cellValue) => $parseTime(cellValue)"
                show-overflow-tooltip
                minWidth="150"
              />
              <!-- 发票金额 -->
              <el-table-column
                align="center"
                prop="totalAmount"
                :label="$t('accountMod.invoiceAmount')"
                show-overflow-tooltip
                minWidth="120"
              />
              <!-- 税额 -->
              <el-table-column
                align="center"
                prop="totalTax"
                :label="$t('purSettlementMod.totalTax')"
                show-overflow-tooltip
                minWidth="120"
              />
              <!-- 未税金额 -->
              <el-table-column
                align="center"
                prop="noTaxTotalAmount"
                :label="$t('purSettlementMod.noTaxTotalAmount2')"
                show-overflow-tooltip
                minWidth="120"
              />
              <!-- 供方 -->
              <el-table-column
                align="center"
                prop="sellerName"
                :label="$t('common.companyName')"
                show-overflow-tooltip
                minWidth="150"
              />
              <!-- 校验码 -->
              <!-- <el-table-column
                align="center"
                prop="checkCode"
                :label="$t('purSettlementMod.checkCode')"
                show-overflow-tooltip
                minWidth="150"
              /> -->
              <!-- 采购方 -->
              <!-- <el-table-column
                align="center"
                prop="purchaserName"
                label="购买方名称"
                show-overflow-tooltip
                minWidth="150"
              /> -->
              <!-- 销售方纳税人识别号 -->
              <!-- <el-table-column
                align="center"
                prop="sellerRegisterNum"
                :label="$t('agentOnlineInvoice.sellerRegisterNum')"
                show-overflow-tooltip
                minWidth="150"
              /> -->
              <!-- 价税合计(小写) -->
              <!-- <el-table-column
                align="center"
                prop="amountInFigures"
                label="价税合计"
                show-overflow-tooltip
                minWidth="150"
              /> -->
              <el-table-column v-if="!isReadonly" :label="$t('common.operation')" width="80" fixed="right">
                <template slot-scope="scope">
                  <el-button type="text" @click="deleteInvoice(scope.$index, scope.row)">
                    {{ $t('common.delete') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
        </el-form>
        <el-form
          ref="onlineInvoicePunishesRef"
          :model="detailsForm"
          :disabled="isReadonly"
        >
          <!-- 绩效考核 -->
          <el-collapse-item :title="$t('route.performanceAssessment')" name="4">
            <p class="btn_line">
              <el-button
                v-if="!isReadonly"
                type="primary"
                class="detail-pbtn"
                @click="addOnlineInvoicePunishes"
              >
                {{ $t('common.add') }}
              </el-button>
            </p>
            <el-table
              :data="detailsForm.onlineInvoicePunishes"
              border
              style="width: 100%"
              max-height="240px"
            >
              <el-table-column
                align="center"
                type="index"
                fixed="left"
                :label="$t('common.sort')"
                width="60"
              />
              <!-- 考核时间 -->
              <el-table-column
                align="center"
                prop="assessmentDate"
                :label="$t('perfMod.assessmentDate')"
                minWidth="150"
                :render-header="_addStarToColumn"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <el-form-item
                    :prop="'onlineInvoicePunishes.' + scope.$index + '.assessmentDate'"
                    :rules="{ required: true, message: $t('perfMod.selectExamination'), trigger: 'blur' }"
                  >
                    <el-date-picker
                      v-model="scope.row.assessmentDate"
                      type="date"
                      :format="$formatDatePicker"
                      value-format="yyyy-MM-dd"
                      style="width: 100%"
                      :placeholder="$t('perfMod.assessmentDate')"
                    />
                  </el-form-item>
                </template>
              </el-table-column>
              <!-- 考核事件 -->
              <el-table-column
                align="center"
                prop="assessmentType"
                :label="$t('purSettlementMod.assessmentEvent')"
                minWidth="150"
                :render-header="_addStarToColumn"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <el-form-item
                    :prop="'onlineInvoicePunishes.' + scope.$index + '.assessmentType'"
                    :rules="{ required: true, message: $t('cusEntry.supplement20250205.assessmentTypeTip'), trigger: 'blur' }"
                  >
                    <el-input v-model="scope.row.assessmentType" />
                  </el-form-item>
                </template>
              </el-table-column>
              <!-- 实际考核未税金额 -->
              <el-table-column
                align="center"
                prop="actualAssessmentAmountN"
                :label="$t('purSettlementMod.actualAssessmentAmountN2')"
                minWidth="150"
                :render-header="_addStarToColumn"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <el-form-item
                    :prop="'onlineInvoicePunishes.' + scope.$index + '.actualAssessmentAmountN'"
                    :rules="{ required: true, message: $t('cusEntry.supplement20250205.actualAssessmentAmountNTip'), trigger: 'blur' }"
                  >
                    <el-input-number
                      v-model="scope.row.actualAssessmentAmountN"
                      :controls="false"
                      class="input-number-precision"
                      @blur="actualAssessmentAmountChange(scope.row)"
                    />
                  </el-form-item>
                </template>
              </el-table-column>
              <!-- 实际考核含税金额 -->
              <el-table-column
                align="center"
                prop="actualAssessmentAmountY"
                :label="$t('purSettlementMod.actualAssessmentAmountY2')"
                minWidth="150"
                :render-header="_addStarToColumn"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <el-form-item
                    :prop="'onlineInvoicePunishes.' + scope.$index + '.actualAssessmentAmountY'"
                    :rules="{ required: true, message: $t('cusEntry.supplement20250205.actualAssessmentAmountYTip'), trigger: 'blur' }"
                  >
                    <el-input-number
                      v-model="scope.row.actualAssessmentAmountY"
                      :controls="false"
                      class="input-number-precision"
                      @blur="actualAssessmentAmountChange(scope.row)"
                    />
                  </el-form-item>
                </template>
              </el-table-column>
              <!-- 税额 -->
              <el-table-column
                align="center"
                prop="tax"
                :label="$t('contractMod.taxQuota')"
                minWidth="150"
                show-overflow-tooltip
              />
              <!-- 物料编码 -->
              <el-table-column
                align="center"
                prop="itemCode"
                :label="$t('common.materialCode')"
                minWidth="150"
                :render-header="_addStarToColumn"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <el-form-item
                    :prop="'onlineInvoicePunishes.' + scope.$index + '.itemCode'"
                    :rules="{ required: true, message: $t('vendorMod.enterMaterialCode'), trigger: 'blur' }"
                  >
                    <QuickSearch
                      :show-input="scope.row.itemCode"
                      show-key="materialCode"
                      :scope-data="scope.row"
                      name="scc_base_material_item_contract"
                      @close-quicksearch="getMaterialObj"
                    />
                  </el-form-item>
                </template>
              </el-table-column>
              <!-- 物料名称 -->
              <el-table-column
                align="center"
                prop="itemName"
                :label="$t('common.materialName')"
                minWidth="150"
                show-overflow-tooltip
              />
              <!-- 物料品类 -->
              <el-table-column
                align="center"
                prop="categoryName"
                :label="$t('cusEntry.orderMod.materialCategory')"
                minWidth="150"
                show-overflow-tooltip
              />
              <!-- 备注 -->
              <el-table-column
                align="center"
                prop="comment"
                :label="$t('common.remark')"
                minWidth="150"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <el-input v-model="scope.row.comment" />
                </template>
              </el-table-column>
              <el-table-column v-if="!isReadonly" :label="$t('common.operation')" width="80" fixed="right">
                <template slot-scope="scope">
                  <el-button type="text" @click="delInvoicePunishes(scope.$index, scope.row)">
                    {{ $t('common.delete') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
        </el-form>
        <el-form
          ref="onlineInvoiceAdvancesRef"
          :model="detailsForm"
          :disabled="isReadonly"
        >
          <!-- 预付款申请明细 -->
          <el-collapse-item :title="$t('purSettlementMod.prepayApplyDetails')" style="margin-bottom: 16px;" name="5">
            <p class="btn_line">
              <!-- 新增 -->
              <el-button
                v-if="!isReadonly"
                type="primary"
                class="detail-pbtn"
                @click="openAdvanceItem"
              >
                {{ $t('common.add') }}
              </el-button>
            </p>
            <el-table
              :data="detailsForm.onlineInvoiceAdvances"
              border
              style="width: 100%"
              max-height="240px"
            >
              <!-- 序号 -->
              <el-table-column
                align="center"
                type="index"
                fixed="left"
                :label="$t('common.sort')"
                width="60"
              />
              <!-- 预付申请单号 -->
              <el-table-column
                align="center"
                prop="advanceApplyNumber"
                :label="$t('purSettlementMod.advanceApplyNum')"
                show-overflow-tooltip
                minWidth="150"
              />
              <!-- 单据创建日期 -->
              <el-table-column
                align="center"
                prop="creationDate"
                :label="$t('purSettlementMod.appliedDate')"
                :formatter="(row, column, cellValue) => $parseTime(cellValue)"
                show-overflow-tooltip
                minWidth="150"
              />
              <!-- 预付款金额 -->
              <el-table-column
                align="center"
                prop="includeTaxAmount"
                :label="$t('accountMod.advancePaymentAmount')"
                show-overflow-tooltip
                minWidth="150"
              />
              <!-- 可核销金额 -->
              <el-table-column
                align="center"
                prop="unWrittenOffAmount"
                :label="$t('purSettlementMod.unWrittenOffAmount2')"
                show-overflow-tooltip
                minWidth="150"
              />
              <!-- 本次核销金额 -->
              <el-table-column
                align="center"
                prop="curWrittenOffAmount"
                :label="$t('purSettlementMod.chargeOffAmount')"
                show-overflow-tooltip
                :render-header="_addStarToColumn"
                minWidth="150"
              >
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
                minWidth="100"
              />
              <!-- 税率 -->
              <el-table-column
                align="center"
                prop="taxRate"
                :label="$t('bidMod.taxRate2')"
                show-overflow-tooltip
                minWidth="100"
              />
              <!-- 预付款创建人 -->
              <el-table-column
                align="center"
                prop="createdFullName"
                :label="$t('purSettlementMod.prepaymentCreator')"
                show-overflow-tooltip
                minWidth="100"
              />
              <el-table-column v-if="!isReadonly" :label="$t('common.operation')" width="80" fixed="right">
                <template slot-scope="scope">
                  <el-button type="text" @click="delonlineInvoiceAdvances(scope.$index, scope.row)">
                    {{ $t('common.delete') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
        </el-form>
      </el-collapse>
    </el-main>
    <CToolbar>
      <template slot="right">
        <el-button v-if="!isReadonly" @click="backTo">
          {{ $t('common.cancel') }}
        </el-button>
        <el-button v-if="!isReadonly" type="primary" @click="saveOrSubmitData('SAVE')">
          {{ $t('flowMod.temporaryView') }}
        </el-button>
        <el-button v-if="!isReadonly" type="primary" @click="saveOrSubmitData('SUBMIT')">
          {{ $t('common.submit') }}
        </el-button>
      </template>
    </CToolbar>

    <!-- 对账单明细-新增 -->
    <StatementTable
      v-if="statementDialogVisible"
      :visible.sync="statementDialogVisible"
      :statement-params="statementParams"
      @after-confirm="addStatementItem"
    />

    <!-- 验收单明细-新增 -->
    <CheckOrderTable
      v-if="checkOrderDialogVisible"
      :visible.sync="checkOrderDialogVisible"
      :check-order-params="checkOrderParams"
      @after-confirm="addCheckOrderItem"
    />

    <!-- 预付款申请明细-新增 -->
    <PreApplyTable
      v-if="preApplyDialogVisible"
      :visible.sync="preApplyDialogVisible"
      :pre-apply-params="preApplyParams"
      @after-confirm="addPreApplyItem"
    />

    <!-- 发票明细-新增 -->
    <InvoiceTable
      v-if="invoiceDialogVisible"
      :visible.sync="invoiceDialogVisible"
      :invoice-params="invoiceParams"
      @after-confirm="addInvoiceItem"
    />
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import OrganizationSelector from 'lib@/components/organization-selector'
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import TableView from 'lib@/components/Table/TableView'
import { parseTime } from '@/utils'
import { transformMQL } from 'lib@/utils/util'
import StatementTable from './components/statementTable'
import CheckOrderTable from './components/checkOrderTable'
import PreApplyTable from './components/preApplyTable'
import InvoiceTable from './components/invoiceTable'

export default {
  name: 'AgentOnlineInvoiceDetail',
  components: {
    MainHeader,
    FormWrapper,
    TableView,
    QuickSearch,
    CToolbar,
    OrganizationSelector,
    StatementTable,
    CheckOrderTable,
    PreApplyTable,
    InvoiceTable
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      activeDims: ['1', '2', '3'],
      form: {
        orgId: null,
        orgCode: '',
        orgName: '',
        extPrincipalCode: '',
        extPrincipalName: '',
        vendorId: null,
        vendorCode: '',
        vendorName: '',
        currencyId: null,
        currencyName: this.$t('bidMod.yuan'),
        currencyCode: 'RMB',
        extSource: 'INVOICE_NOTICE',
        onlineInvoiceNum: '',
        invoiceStatus: 'DRAFT',
        extBehalfInvoice: 'N',
        extInputTax: 'N',
        extFreeOfCharge: 'N',
        taxTotalAmount: null,
        excluTaxTotalAmount: null,
        totalTax: null,
        actualInvoiceAmountY: null,
        actualInvoiceAmountN: null,
        invoiceTax: null,
        unPaidAmount: null,
        paidAmount: null,
        createdUserName: '',
        creationDate: ''
      },
      formRules: {
        orgId: [{ required: true, message: this.$t('quota.orgIdTips') }], // 请选择业务实体
        vendorName: [{ required: true, message: this.$t('quota.vendorTips') }], // 请选择供应商
        currencyCode: [{ required: true, message: this.$t('vendorMod.msgCurrencyCode') }], // 请选择币种
        extSource: [{ required: true, message: this.$t('cusEntry.supplement20250205.billSource') }] // 请选择单据来源
      },
      detailsForm: {
        statementDetailsTable: [], // 对账单明细
        statementDetailsTableDel: [],
        checkOrderDetailsTable: [], // 验收单明细
        checkOrderDetailsTableDel: [],
        onlineInvoicePunishes: [], // 绩效考核明细
        onlineInvoicePunishesDel: [],
        onlineInvoiceAdvances: [], // 预付申请明细
        onlineInvoiceAdvancesDel: [],
        invoiceTable: [], // 发票信息
        invoiceTableDel: []
      },
      statementDialogVisible: false,
      statementParams: {},
      checkOrderDialogVisible: false,
      checkOrderParams: {},
      preApplyDialogVisible: false,
      preApplyParams: {},
      invoiceDialogVisible: false,
      invoiceParams: {}
    }
  },
  computed: {
    isReadonly () {
      return this.$attrs.params.flag === 'view'
    },
    isEmptyDetailTable () { // 开票明细表格为空（对账单和验收单表格为空）
      return this.detailsForm.statementDetailsTable.length === 0 &&
        this.detailsForm.checkOrderDetailsTable.length === 0
    },
    isFormHeadDisable () {
      return (
        this.detailsForm.statementDetailsTable.length !== 0 ||
        this.detailsForm.checkOrderDetailsTable.length !== 0 ||
        this.detailsForm.onlineInvoicePunishes.length !== 0 ||
        this.detailsForm.onlineInvoiceAdvances.length !== 0 ||
        this.detailsForm.invoiceTable.length !== 0
      )
    }
  },
  created () {
    const { flag, onlineInvoiceId } = this.$attrs.params
    this.invoiceParams.username = this.$store.getters.userInfo.username
    if (flag === 'add') {
      this.form.createdUserName = this.$store.getters.userInfo.nickname
      this.form.creationDate = new Date()
      this.$http({
        url: `/api-base/pj-anon/user/getHrUserOrgnizationByUsername?username=${this.$store.getters.userInfo.username}`,
        method: 'GET',
        loading: true
      }).then((res) => {
        this.form.orgId = res.data.ouOrganization?.organizationId
        this.form.orgCode = res.data.ouOrganization?.organizationCode
        this.form.orgName = res.data.ouOrganization?.organizationName
        // 根据业务实体查询开票主体、利润中心
        if (this.form.orgId) {
          this.getInvoiceBody()
        } else {
          this.form.extPrincipalCode = null
          this.form.extPrincipalName = null
          this.form.extProfitCenterCode = null
          this.form.extProfitCenterName = null
        }
      })
    } else {
      this.getOrderFormDetail(onlineInvoiceId)
    }
  },
  methods: {
    backTo () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('agentOnlineInvoiceList.getQuerydata')
    },
    // 选择业务实体
    selectHandler (node, value, scope) {
      this.form.orgId = node ? node.organizationId : null
      this.form.orgCode = node ? node.organizationCode : null
      this.form.orgName = node ? node.organizationName : null

      // 根据业务实体查询开票主体、利润中心
      if (this.form.orgId) {
        this.getInvoiceBody()
      } else {
        this.form.extPrincipalCode = null
        this.form.extPrincipalName = null
        this.form.extProfitCenterCode = null
        this.form.extProfitCenterName = null
      }
    },
    // 查询开票主体、利润中心
    getInvoiceBody () {
      const saveData = transformMQL.listPageData({
        type: 'InvoicePrincipal',
        action: 'query',
        params: { orgId: this.form.orgId }
      })
      this.$http({
        url: '/api-sup-ce/api-ql/InvoicePrincipal/query',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        this.form.extPrincipalCode = res.data.records[0]?.principalCode
        this.form.extPrincipalName = res.data.records[0]?.principalName
        this.form.extProfitCenterCode = res.data.records[0]?.profitCenterCode
        this.form.extProfitCenterName = res.data.records[0]?.profitCenterName
      })
    },
    // 选择供应商
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
    },
    // 选择币种
    getCurrencyObj (val, dictItem) {
      this.form.currencyId = val ? dictItem.id : ''
      this.form.currencyCode = val ? dictItem.value : ''
      this.form.currencyName = val ? dictItem.currencyName : ''
    },
    // 对账单明细新增
    addStatementDetails () {
      // 默认查出所选 业务实体 + 供应商 + 币种 + 未开票数量>0 + 单据状态：供应商已确认
      let checkArr = ['orgId', 'vendorId', 'currencyCode']
      for (let key in this.form) {
        if (checkArr.includes(key)) {
          if (!this.form[key]) {
            return this.$message.warning(this.$t('vendorMod.pleasefinishRequired'))
          } else {
            this.$set(this.statementParams, key, this.form[key])
          }
        }
      }
      this.statementDialogVisible = true
    },
    // 对账单明细 - 保存
    addStatementItem (selection) {
      const ids = this.detailsForm.statementDetailsTable.map(item => item.invoiceDetailId)
      selection.forEach(item => {
        if (!ids.includes(item.invoiceDetailId)) {
          this.detailsForm.statementDetailsTable.push(item)
        }
      })
      this.setAmountCal()
    },
    // 对账单明细 - 删除
    deleteStatement (index, row) {
      if (row.onlineInvoiceDetailId) {
        this.detailsForm.statementDetailsTableDel.push({
          '$delete': row.onlineInvoiceDetailId
        })
      }
      this.detailsForm.statementDetailsTable.splice(index, 1)
      this.setAmountCal()
    },
    // 对账单-本次开票数量
    invoiceValidator (rule, value, callback) {
      const index = Number(rule.field.split('.')[1]) // 获取行号
      const sign = this.detailsForm.statementDetailsTable.some((row, i) => {
        if (index === i) {
          !row.invoiceQuantity && (row.invoiceQuantity = 0)
          return row.invoiceQuantity > row.notInvoiceQuantity
        }
      })
      // 本次开票数量不可大于可开票数量
      sign && callback(new Error(this.$t('cusEntry.supplement20250205.agentOnlineInvoiceTip1')))
      callback()
    },
    // 对账单-尾差调整
    extAdjustAmountValidator (rule, value, callback) {
      if (value > 1 || value < -1) {
        // 尾差调整仅可填写[-1, 1]范围内的数值
        callback(new Error(this.$t('cusEntry.supplement20250205.agentOnlineInvoiceTip2')))
      }
      callback()
    },
    // 对账单-转出税额
    extInputTaxAmountValidator (rule, value, callback) {
      // 转出税额必须大于0
      !value && callback(new Error(this.$t('cusEntry.supplement20250205.agentOnlineInvoiceTip3')))
      const index = Number(rule.field.split('.')[1]) // 获取行号
      const sign = this.detailsForm.statementDetailsTable.some((row, i) => {
        if (index === i) {
          return row.extInputTaxAmount > row.tax
        }
      })
      // 转出税额不可大于税额
      sign && callback(new Error(this.$t('cusEntry.supplement20250205.agentOnlineInvoiceTip4')))
      callback()
    },
    // 对账单-SAP成本中心
    getSAPObj (val, scope) {
      scope.extSapCostCode = val ? val.costnumber : null
      scope.extSapCostContent = val ? val.faltgroup : null
    },

    // 验收单明细新增
    addCheckOrderDetails () {
      // 默认查出所选 业务实体 + 供应商 + 币种 + 未开票数量>0 + 单据状态：供应商已确认
      let checkArr = ['orgId', 'vendorId', 'currencyCode']
      for (let key in this.form) {
        if (checkArr.includes(key)) {
          if (!this.form[key]) {
            return this.$message.warning(this.$t('vendorMod.pleasefinishRequired'))
          } else {
            this.$set(this.checkOrderParams, key, this.form[key])
          }
        }
      }
      this.checkOrderDialogVisible = true
    },
    // 验收单明细 - 保存
    addCheckOrderItem (selection) {
      const ids = this.detailsForm.checkOrderDetailsTable.map(item => item.extCheckDetailId)
      selection.forEach(item => {
        if (!ids.includes(item.checkOrderDetailId)) {
          this.detailsForm.checkOrderDetailsTable.push(item)
        }
      })
      this.setAmountCal()
    },
    // 验收单明细 - 删除
    deleteCheckOrderDetails (index, row) {
      if (row.onlineInvoiceDetailId) {
        this.detailsForm.checkOrderDetailsTableDel.push({
          '$delete': row.onlineInvoiceDetailId
        })
      }
      this.detailsForm.checkOrderDetailsTable.splice(index, 1)
      this.setAmountCal()
    },
    // 验收单-本次开票数量
    invoiceCheckValidator (rule, value, callback) {
      const index = Number(rule.field.split('.')[1]) // 获取行号
      const sign = this.detailsForm.checkOrderDetailsTable.some((row, i) => {
        if (index === i) {
          !row.invoiceQuantity && (row.invoiceQuantity = 0)
          return row.invoiceQuantity > row.notInvoiceQuantity
        }
      })
      // 本次开票数量不可大于可开票数量
      sign && callback(new Error(this.$t('cusEntry.supplement20250205.agentOnlineInvoiceTip1')))
      callback()
    },

    // 预付款申请新增
    openAdvanceItem () {
      // 默认查出所选 业务实体 + 供应商 + 币种
      let checkArr = ['orgId', 'vendorId', 'currencyCode']
      for (let key in this.form) {
        if (checkArr.includes(key)) {
          if (!this.form[key]) {
            return this.$message.warning(this.$t('vendorMod.pleasefinishRequired'))
          } else {
            this.$set(this.preApplyParams, key, this.form[key])
          }
        }
      }
      this.preApplyDialogVisible = true
    },
    // 预付款新增 - 确认
    addPreApplyItem (selection) {
      const ids = this.detailsForm.onlineInvoiceAdvances.map(item => item.advanceApplyId)
      selection.forEach(item => {
        if (!ids.includes(item.advanceApplyId)) {
          this.detailsForm.onlineInvoiceAdvances.push(item)
        }
      })
    },
    // 预付款 - 删除
    delonlineInvoiceAdvances (index, row) {
      if (row.onlineInvoiceAdvanceId) {
        this.detailsForm.onlineInvoiceAdvancesDel.push({
          '$delete': row.onlineInvoiceAdvanceId
        })
      }
      this.detailsForm.onlineInvoiceAdvances.splice(index, 1)
    },
    // 预付申请明细 - 本次核销金额校验
    amountValidator (rule, value, callback) {
      const index = Number(rule.field.split('.')[1]) // 获取行号
      const sign = this.detailsForm.onlineInvoiceAdvances.some((row, i) => {
        if (index === i) {
          !row.curWrittenOffAmount && (row.curWrittenOffAmount = 0)
          return row.curWrittenOffAmount > row.unWrittenOffAmount
        }
      })
      // 本次核销金额不可大于可核销金额
      sign && callback(new Error(this.$t('cusEntry.supplement20250205.agentOnlineInvoiceTip5')))
      callback()
    },

    // 绩效考核 - 添加
    addOnlineInvoicePunishes () {
      this.detailsForm.onlineInvoicePunishes.push({
        assessmentDate: null,
        assessmentType: null,
        actualAssessmentAmountN: null,
        actualAssessmentAmountY: null,
        tax: null,
        itemId: null,
        itemCode: null,
        itemName: null,
        categoryId: null,
        categoryCode: null,
        categoryName: null,
        comment: null
      })
    },
    // 绩效考核 - 删除
    delInvoicePunishes (index, row) {
      if (row.onlineInvoicePunishId) {
        this.detailsForm.onlineInvoicePunishesDel.push({
          '$delete': row.onlineInvoicePunishId
        })
      }
      this.detailsForm.onlineInvoicePunishes.splice(index, 1)
      this.setAmountCal()
    },
    // 绩效考核 - 选择物料
    getMaterialObj (val, scope) {
      scope.itemId = val ? val.materialId : null
      scope.itemCode = val ? val.materialCode : null
      scope.itemName = val ? val.materialName : null
      scope.categoryId = val ? val.categoryId : null
      scope.categoryName = val ? val.categoryName : null
      scope.categoryCode = val ? val.categoryCode : null
    },
    // 绩效考核 - 填写金额
    actualAssessmentAmountChange (row) {
      row.tax = parseFloat((Number(row.actualAssessmentAmountY || 0) - Number(row.actualAssessmentAmountN || 0)).toFixed(8))
      this.setAmountCal()
    },

    // 发票 - 新增
    addInvoice () {
      // 默认查出所选 开票主体编码 + 供应商名称 (是否代开发票=是, 不传供应商名称)
      let checkArr = ['extPrincipalCode', 'vendorName', 'extBehalfInvoice']
      for (let key in this.form) {
        if (checkArr.includes(key)) {
          if (!this.form[key]) {
            return this.$message.warning(this.$t('vendorMod.pleasefinishRequired'))
          } else {
            this.$set(this.invoiceParams, key, this.form[key])
          }
        }
      }
      this.invoiceDialogVisible = true
    },
    // 发票 - 保存
    addInvoiceItem (selection) {
      const ids = this.detailsForm.invoiceTable.map(item => item.invoiceName)
      selection.forEach(item => {
        if (!ids.includes(item.invoiceName)) {
          this.detailsForm.invoiceTable.push(item)
        }
      })
      this.setNoTaxCal()
    },
    // 删除开票信息
    async deleteInvoice (index, row) {
      if (row.ocrInvoiceId) {
        this.detailsForm.invoiceTableDel.push({
          '$delete': row.ocrInvoiceId
        })
      }
      this.detailsForm.invoiceTable.splice(index, 1)
      this.setNoTaxCal()
    },
    // 发票信息计算
    setNoTaxCal () {
      // 发票明细未税金额之和 (普票的未税金额 汇总时取含税金额)
      const notaxs = this.detailsForm.invoiceTable.map(item => {
        let noTaxTotalAmount = item.noTaxTotalAmount
        if (['10', '11', '14', '04', '97', '32', '186'].includes(item.invoiceType)) {
          noTaxTotalAmount = item.totalAmount
        }
        return noTaxTotalAmount
      })
      this.form.actualInvoiceAmountN =
        notaxs.length > 0 ? notaxs.reduce((p, c) => (Number(p) || 0) + (Number(c) || 0), 0) : 0

      // 发票含税总金额 = 发票明细含税金额之和
      const taxs = this.detailsForm.invoiceTable.map(item => item.totalAmount)
      this.form.actualInvoiceAmountY =
        taxs.length > 0 ? taxs.reduce((p, c) => (Number(p) || 0) + (Number(c) || 0), 0) : 0

      // 发票税额
      this.form.invoiceTax = this.form.actualInvoiceAmountY - this.form.actualInvoiceAmountN
    },

    // 系统税额计算
    setAmountCal (row) {
      if (row) {
        // 对账单/验收单明细行 未税总额 = 本次开票数量 * 未税单价
        row.noTaxAmount = parseFloat(
          (Number(row.invoiceQuantity || 0) * Number(row.unitPriceExcludingTax || 0)).toFixed(8)
        )
        // 对账单/验收单明细行 含税金额 = (未税总额 + 尾差调整) * (1 + 税率)
        row.taxAmount = parseFloat(
          ((Number(row.noTaxAmount || 0) + Number(row.extAdjustAmount || 0)) * (1 + Number(row.taxRate || 0) / 100)).toFixed(8)
        )
        // 对账单/验收单明细行 税额 = 含税金额 - 未税总额
        row.tax = parseFloat(
          (Number(row.taxAmount || 0) - Number(row.noTaxAmount || 0)).toFixed(8)
        )
      }

      let { statementNoTax, statementTax } = this.calStatementReduce(this.detailsForm.statementDetailsTable)
      let { checkOrderNoTax, checkOrderTax } = this.calCheckOrderReduce(this.detailsForm.checkOrderDetailsTable)
      let { punishesNoTax, punishesTax } = this.calPunishesReduce(this.detailsForm.onlineInvoicePunishes)

      // 系统未税总金额 = 对账单/验收单明细未税金额之和 + 考核单未税金额之和
      // 系统含税总金额 = 对账单/验收单明细含税金额之和 + 考核单含税金额之和
      if (this.form.extSource === 'INVOICE_NOTICE') {
        this.form.excluTaxTotalAmount = statementNoTax + punishesNoTax
        this.form.taxTotalAmount = statementTax + punishesTax
      } else if (this.form.extSource === 'CHECK_ORDER') {
        this.form.excluTaxTotalAmount = checkOrderNoTax + punishesNoTax
        this.form.taxTotalAmount = checkOrderTax + punishesTax
      }
      // 系统税额 = 系统含税总金额 - 系统未税总金额
      this.form.totalTax = this.form.taxTotalAmount - this.form.excluTaxTotalAmount
    },
    // 对账单明细计算未税总金额之和、含税金额之和
    calStatementReduce (statementDetailsTable) {
      let statementNoTax = 0 // 对账单明细未税总金额之和
      let statementTax = 0 // 对账单明细含税金额之和
      if (statementDetailsTable.length > 0) {
        statementNoTax = statementDetailsTable
          .map(row => {
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
    // 验收单明细计算未税总金额之和、含税金额之和
    calCheckOrderReduce (checkOrderDetailsTable) {
      let checkOrderNoTax = 0 // 验收单明细未税总金额之和
      let checkOrderTax = 0 // 验收单明细含税金额之和
      if (checkOrderDetailsTable.length > 0) {
        checkOrderNoTax = checkOrderDetailsTable
          .map(row => row.noTaxAmount)
          .reduce((p, c) => (Number(p) || 0) + (Number(c) || 0), 0)

        checkOrderTax = checkOrderDetailsTable
          .map(row => row.taxAmount)
          .reduce((p, c) => (Number(p) || 0) + (Number(c) || 0), 0)
      }
      return { checkOrderNoTax, checkOrderTax }
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

    // 查询单据详情
    async getOrderFormDetail (onlineInvoiceId) {
      const searchData = transformMQL.save(
        'OnlineInvoice',
        [onlineInvoiceId],
        'read',
        {
          '*': {},
          'detailList': { '*': {} },
          'advanceApplyList': { '*': {} },
          'ocrInvoiceList': { '*': {} },
          'punishList': { '*': {} },
          'fileUploads': { '*': {} }
        }
      )
      this.$http({
        url: '/api-sup-ce/api-ql/OnlineInvoice/read',
        method: 'POST',
        data: searchData,
        loading: true
      }).then(res => {
        if (res && res.data && res.data.length) {
          const { detailList = [], advanceApplyList = [], ocrInvoiceList = [], punishList = [], fileUploads = [], ...rest } = res.data[0]
          this.form = { ...rest } // 头信息
          let detailListNew = detailList.map(item => { // 单据保存会占用可开票数量，为正确展示，可开票数量 得加上 本次开票数量
            return { ...item, notInvoiceQuantity: item.notInvoiceQuantity + item.invoiceQuantity }
          })
          if (this.form.extSource === 'INVOICE_NOTICE') {
            this.detailsForm.statementDetailsTable = detailListNew // 对账单明细
          } else if (this.form.extSource === 'CHECK_ORDER') {
            this.detailsForm.checkOrderDetailsTable = detailListNew // 验收单明细
          }
          this.detailsForm.onlineInvoiceAdvances = advanceApplyList.map(item => { // 预付申请明细 (可核销金额 得加上 本次核销金额)
            return { ...item, unWrittenOffAmount: item.unWrittenOffAmount + item.curWrittenOffAmount }
          })
          this.detailsForm.onlineInvoicePunishes = punishList // 绩效考核明细
          this.detailsForm.invoiceTable = ocrInvoiceList // 发票信息
        }
      })
    },

    // 暂存/提交 占用开票数量，需校验
    async saveOrSubmitData (type) {
      const { flag, message } = await this.getCheckForm()
      if (flag) {
        if (this.isEmptyDetailTable) {
          // 至少添加一条对账单/验收单明细
          return this.$message.error(this.$t('cusEntry.supplement20250205.agentOnlineInvoiceTip6'))
        }
        this.saveHandle(type)
      } else {
        this.__focus_error__(message)
      }
    },
    async getCheckForm () {
      const formFiled = await this.formValidate('formRef')
      const formFiled1 = await this.formValidate('statementDetailsTableRef')
      const formFiled2 = await this.formValidate('checkOrderDetailsTableRef')
      const formFiled3 = await this.formValidate('onlineInvoicePunishesRef')
      const formFiled4 = await this.formValidate('onlineInvoiceAdvancesRef')

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

      if (!formFiled2.flag && Object.keys(formFiled2.obj).length > 0) {
        const warnObj2 = Object.keys(formFiled2.obj)[0]
        return {
          flag: formFiled2.flag,
          message: formFiled2.obj[warnObj2][0].message
        }
      }

      if (!formFiled3.flag && Object.keys(formFiled3.obj).length > 0) {
        const warnObj3 = Object.keys(formFiled3.obj)[0]
        return {
          flag: formFiled3.flag,
          message: formFiled3.obj[warnObj3][0].message
        }
      }

      if (!formFiled4.flag && Object.keys(formFiled4.obj).length > 0) {
        const warnObj4 = Object.keys(formFiled4.obj)[0]
        return {
          flag: formFiled4.flag,
          message: formFiled4.obj[warnObj4][0].message
        }
      }

      return { flag: true }
    },
    // form验证返回promise校验返回trun or false
    formValidate (formRef) {
      return new Promise((resolve) => {
        this.$refs[formRef].validate((flag, obj) => {
          resolve({ flag, obj })
        })
      })
    },
    // 暂存 or 提交
    async saveHandle (type) {
      let detailList = []
      if (this.form.extSource === 'INVOICE_NOTICE') { // 对账单明细
        detailList = [
          ...this.detailsForm.statementDetailsTable,
          ...this.detailsForm.statementDetailsTableDel,
          ...this.detailsForm.checkOrderDetailsTableDel

        ]
      } else if (this.form.extSource === 'CHECK_ORDER') { // 验收单明细
        detailList = [
          ...this.detailsForm.statementDetailsTableDel,
          ...this.detailsForm.checkOrderDetailsTable,
          ...this.detailsForm.checkOrderDetailsTableDel
        ]
      }
      let punishList = [ // 绩效考核单
        ...this.detailsForm.onlineInvoicePunishes,
        ...this.detailsForm.onlineInvoicePunishesDel
      ]
      let advanceApplyList = [ // 预付款明细
        ...this.detailsForm.onlineInvoiceAdvances,
        ...this.detailsForm.onlineInvoiceAdvancesDel
      ]
      let ocrInvoiceList = [ // 发票信息
        ...this.detailsForm.invoiceTable,
        ...this.detailsForm.invoiceTableDel
      ]
      const params = {
        ...this.form,
        invoiceStatus: type == 'SAVE' ? 'DRAFT' : 'FINAL_REVIEW_APPROVED',
        detailList,
        punishList,
        advanceApplyList,
        ocrInvoiceList,
        fileUploads: []
      }
      const saveData = transformMQL.save('OnlineInvoice', [params], 'extSaveOrUpdate')
      this.$http({
        url: '/api-sup-ce/api-ql/OnlineInvoice/extSaveOrUpdate',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.detailsForm.statementDetailsTableDel = []
        this.detailsForm.checkOrderDetailsTableDel = []
        this.detailsForm.onlineInvoicePunishesDel = []
        this.detailsForm.onlineInvoiceAdvancesDel = []
        this.detailsForm.invoiceTableDel = []
        if (type == 'SAVE') {
          this.__setTabTodo('agentOnlineInvoiceList.getQuerydata')
          let onlineInvoiceId = res.data[0]?.onlineInvoiceId
          this.getOrderFormDetail(onlineInvoiceId)
        } else {
          this.backTo()
        }
      })
    }
  }
}
</script>
<style scoped lang="scss">
.the-purInvoice-detail {
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
