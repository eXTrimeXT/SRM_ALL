<template>
  <el-container
    class="flex-container the-statementBillDetail-detail"
    direction="vertical"
  >
    <el-main>
      <div class="form-container2">
        <el-form
          ref="form"
          :disabled="curRole === 'VENDOR'"
          :model="statementHead"
          label-width="80px"
          label-position="top"
          class="form-incontainer"
          :rules="rules"
        >
          <el-collapse
            v-model="activeDims"
            class="tab-form-style"
          >
            <el-collapse-item
              :title="$t('purSettlementMod.basicInfo')"
              name="1"
            >
              <el-row type="flex">
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.statementNumber')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="statementHead.statementNumber"
                      disabled
                    />
                  </el-form-item>
                </el-col>

                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.vendorCode')"
                    :label-width="formLabelWidth"
                    prop="vendorCode"
                  >
                    <quick-search
                      :show-input="statementHead.vendorCode"
                      show-key="companyCode"
                      :scope-data="statementHead"
                      name="scc_sup_company_info"
                      @close-quicksearch="getVendorObj"
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.vendorName')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="statementHead.vendorName"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.fullPathId')"
                    :label-width="formLabelWidth"
                    prop="organizationId"
                  >
                    <organization-select-tree
                      v-model="statementHead.organizationId"
                      :scope="statementHead"
                      @select="addOrgHandle"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row type="flex">
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.paymentMethod')"
                    :label-width="formLabelWidth"
                  >
                    <el-select
                      v-model="statementHead.paymentMethod"
                      disabled
                    >
                      <el-option
                        v-for="item in payModeList"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.termOfPayment')"
                    :label-width="formLabelWidth"
                  >
                    <el-select
                      v-model="statementHead.termOfPayment"
                      disabled
                    >
                      <el-option
                        v-for="item in paymentTermsList"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.currency')"
                    :label-width="formLabelWidth"
                  >
                    <dict-select
                      v-model="statementHead.currency"
                      code="currency"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.taxRate')"
                    :label-width="formLabelWidth"
                  >
                    <dict-select
                      v-model="statementHead.taxKey"
                      code="tax"
                      disabled
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row type="flex">
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.receiptAmount')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="statementHead.receiptAmount"
                      v-input-format="{ type: 'number' }"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.returnAmount')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="statementHead.returnAmount"
                      v-input-format="{ type: 'number' }"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.statementTotalAmount')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="statementHead.statementTotalAmount"
                      v-input-format="{ type: 'number' }"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.statementStatus')"
                    :label-width="formLabelWidth"
                  >
                    <el-select
                      v-model="statementHead.statementStatus"
                      disabled
                    >
                      <el-option
                        v-for="item in statusList"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row type="flex">
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.statementStartTime')"
                    :label-width="formLabelWidth"
                  >
                    <el-date-picker
                      v-model="statementHead.statementStartTime"
                      type="date"
                      value-format="yyyy-MM-dd"
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.statementEndTime')"
                    :label-width="formLabelWidth"
                  >
                    <el-date-picker
                      v-model="statementHead.statementEndTime"
                      type="date"
                      value-format="yyyy-MM-dd"
                    />
                  </el-form-item>
                </el-col>
                <el-col><p /></el-col>
                <el-col><p /></el-col>
              </el-row>

              <el-row type="flex">
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.supplierNote')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="statementHead.supplierNote"
                      type="textarea"
                      :rows="2"
                      :disabled="curRole !== 'VENDOR'"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row type="flex">
                <el-col>
                  <el-form-item
                    :label="$t('purSettlementMod.purchaserNote')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="statementHead.purchaserNote"
                      type="textarea"
                      :rows="2"
                      :disabled="curRole !== 'BUYER'"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-collapse-item>
            <el-collapse-item
              :title="$t('purSettlementMod.addUploadFile')"
              name="2"
            >
              <p class="btn_line">
                <el-button
                  type="primary"
                  class="detail-pbtn"
                  @click="addUploadOne"
                >
                  {{ $t('common.add') }}
                </el-button>
              </p>
              <el-table
                :data="fileuploadList"
                style="width: 100%"
                border
                max-height="250px"
              >
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('purSettlementMod.tabindex')"
                />
                <el-table-column
                  align="center"
                  prop="fileSourceName"
                  :label="$t('purSettlementMod.purchaserNote')"
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
                  :label="$t('common.operation')"
                  width="60"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="handleDelClick(scope.$index, scope.row)"
                    >
                      {{ $t('common.delete') }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <el-collapse-item
              :title="$t('purSettlementMod.receiptDetail')"
              name="3"
            >
              <p class="btn_line">
                <el-button
                  type="primary"
                  class="detail-pbtn"
                  @click="openOneDialog"
                >
                  {{ $t('common.add') }}
                </el-button>
              </p>
              <el-table
                :data="receiptList"
                style="width: 100%"
                border
                max-height="250px"
              >
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('purSettlementMod.tabindex')"
                  width="60"
                />
                <el-table-column
                  align="center"
                  prop="warehouseReceiptNumber"
                  :label="$t('purSettlementMod.warehouseReceiptNumber')"
                  width="120"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="warehouseReceiptRowNum"
                  :label="$t('purSettlementMod.warehouseReceiptRowNum')"
                  width="90"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="happenDate"
                  :label="$t('purSettlementMod.happenDate')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="materialCode"
                  :label="$t('purSettlementMod.materialCode')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="materialName"
                  :label="$t('purSettlementMod.materialName')"
                  min-width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="unit"
                  :label="$t('purSettlementMod.unit')"
                  width="60"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="orderNumber"
                  :label="$t('purSettlementMod.orderNumber')"
                  width="150"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="orderLineNum"
                  :label="$t('purSettlementMod.orderLineNumber')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="warehouseReceiptQuantity"
                  :label="$t('purSettlementMod.warehouseReceiptQuantity')"
                  width="80"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="unitPriceNoTax"
                  :label="$t('purSettlementMod.unitPriceNoTax')"
                  width="80"
                />
                <el-table-column
                  align="center"
                  prop="totalAmountNoTax"
                  :label="$t('purSettlementMod.totalAmountNoTax')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  :label="$t('common.operation')"
                  fixed="right"
                  width="60"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="deleteOneContent(scope.$index, scope.row)"
                    >
                      {{ $t('common.delete') }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
              <srm-dialog
                :title="$t('purSettlementMod.receiptDetail')"
                :visible.sync="dialogFormVisible"
                size="large"
                :close-on-click-modal="false"
              >
                <div>
                  <el-form
                    ref="receiptForm"
                    :model="receiptForm"
                    label-width="80px"
                    label-position="top"
                    class="form-incontainer"
                  >
                    <el-row type="flex">
                      <el-col>
                        <el-form-item
                          :label="$t('purSettlementMod.sourceNumber')"
                          :label-width="formLabelWidth"
                        >
                          <el-input v-model="receiptForm.sourceNumber" />
                        </el-form-item>
                      </el-col>
                      <el-col>
                        <el-form-item
                          :label="$t('purSettlementMod.warehouseReceiptNumber')"
                          :label-width="formLabelWidth"
                        >
                          <el-input v-model="receiptForm.warehouseReceiptNumber" />
                        </el-form-item>
                      </el-col>
                      <el-col>
                        <el-form-item
                          :label="$t('purSettlementMod.materialName')"
                          :label-width="formLabelWidth"
                        >
                          <quick-search
                            :show-input="receiptForm.materialName"
                            show-key="itemCode"
                            :scope-data="receiptForm"
                            name="scc_base_material_item_display"
                            @close-quicksearch="getItemObj"
                          />
                        </el-form-item>
                      </el-col>
                      <el-col>
                        <p style="padding-top: 10px;">
                          <el-button
                            type="primary"
                            @click="addOneContent"
                          >
                            {{ $t('common.search') }}
                          </el-button>
                        </p>
                      </el-col>
                    </el-row>
                  </el-form>

                  <el-table
                    :data="diaReceptionList"
                    style="width: 100%"
                    border
                    max-height="351px"
                    @selection-change="handleSelectionChange"
                  >
                    <el-table-column
                      type="selection"
                      width="55"
                    />
                    <el-table-column
                      align="center"
                      type="index"
                      :label="$t('purSettlementMod.tabindex')"
                      width="60"
                    />
                    <el-table-column
                      align="center"
                      prop="warehouseReceiptNumber"
                      :label="$t('purSettlementMod.warehouseReceiptNumber')"
                      width="120"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="warehouseReceiptRowNum"
                      :label="$t('purSettlementMod.warehouseReceiptRowNum')"
                      width="90"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="happenDate"
                      :label="$t('purSettlementMod.happenDate')"
                      width="100"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="materialCode"
                      :label="$t('purSettlementMod.materialCode')"
                      width="100"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="materialName"
                      :label="$t('purSettlementMod.materialName')"
                      min-width="100"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="orderNumber"
                      :label="$t('purSettlementMod.orderNumber')"
                      width="150"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="orderLineNum"
                      :label="$t('purSettlementMod.orderLineNumber')"
                      width="100"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="warehouseReceiptQuantity"
                      :label="$t('purSettlementMod.warehouseReceiptQuantity')"
                      width="80"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="unitPriceNoTax"
                      :label="$t('purSettlementMod.unitPriceNoTax')"
                      width="80"
                    />
                    <el-table-column
                      align="center"
                      prop="totalAmountNoTax"
                      :label="$t('purSettlementMod.totalAmountNoTax')"
                      width="100"
                      :show-overflow-tooltip="true"
                    />
                  </el-table>
                  <el-row type="flex">
                    <el-col>
                      <c-pagination
                        ref="queryPagination"
                        style="margin:5px"
                        class="c-query-table-pagination"
                        :total="queryTotal"
                        :page-num="viewIndex"
                        :page-size="viewSize"
                        @current-change="changeCurrentIndex"
                        @size-change="changeCurrentSize"
                      />
                    </el-col>
                  </el-row>
                </div>
                <div
                  slot="footer"
                  class="dialog-footer"
                >
                  <el-button @click="dialogFormVisible = false">
                    {{ $t('common.cancel') }}
                  </el-button>
                  <el-button
                    type="primary"
                    @click="addOneReceptionItem"
                  >
                    {{ $t('common.confirm') }}
                  </el-button>
                </div>
              </srm-dialog>
            </el-collapse-item>
            <el-collapse-item
              :title="$t('purSettlementMod.returnDetail')"
              name="4"
            >
              <p class="btn_line">
                <el-button
                  type="primary"
                  class="detail-pbtn"
                  @click="openContent2Dialog"
                >
                  {{ $t('common.add') }}
                </el-button>
              </p>
              <el-table
                :data="returnList"
                style="width: 100%"
                border
                max-height="250px"
              >
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('purSettlementMod.tabindex')"
                  width="60"
                />
                <el-table-column
                  align="center"
                  prop="returnOrderNumber"
                  :label="$t('purSettlementMod.returnOrderNumber')"
                  width="120"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="returnLineNum"
                  :label="$t('purSettlementMod.returnLineNum')"
                  width="90"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="happenDate"
                  :label="$t('purSettlementMod.happenDate')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="materialCode"
                  :label="$t('purSettlementMod.materialCode')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="materialName"
                  :label="$t('purSettlementMod.materialName')"
                  min-width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="unit"
                  :label="$t('purSettlementMod.unit')"
                  width="60"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="orderNumber"
                  :label="$t('purSettlementMod.orderNumber')"
                  width="150"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="orderLineNum"
                  :label="$t('purSettlementMod.orderLineNumber')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="returnNum"
                  :label="$t('purSettlementMod.returnNum')"
                  width="80"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="unitPriceNoTax"
                  :label="$t('purSettlementMod.unitPriceNoTax')"
                  width="80"
                />
                <el-table-column
                  align="center"
                  prop="totalAmountNoTax"
                  :label="$t('purSettlementMod.totalAmountNoTax')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  :label="$t('common.operation')"
                  fixed="right"
                  width="60"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="deleteOneContent2(scope.$index, scope.row)"
                    >
                      {{ $t('common.delete') }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
              <srm-dialog
                :title="$t('purSettlementMod.returnDetail')"
                :visible.sync="dialogFormVisible2"
                size="large"
                :close-on-click-modal="false"
              >
                <div>
                  <el-form
                    ref="returnForm"
                    :model="returnForm"
                    label-width="80px"
                    label-position="top"
                    class="form-incontainer"
                  >
                    <el-row type="flex">
                      <el-col>
                        <el-form-item
                          :label="$t('purSettlementMod.sourceNumber')"
                          :label-width="formLabelWidth"
                        >
                          <el-input v-model="returnForm.sourceNumber" />
                        </el-form-item>
                      </el-col>
                      <el-col>
                        <el-form-item
                          :label="$t('purSettlementMod.returnOrderNumber')"
                          :label-width="formLabelWidth"
                        >
                          <el-input v-model="returnForm.returnOrderNumber" />
                        </el-form-item>
                      </el-col>
                      <el-col>
                        <el-form-item
                          :label="$t('purSettlementMod.materialName')"
                          :label-width="formLabelWidth"
                        >
                          <!-- <el-input v-model="returnForm.materialName" /> -->
                          <quick-search
                            :show-input="returnForm.materialName"
                            show-key="itemCode"
                            :scope-data="returnForm"
                            name="scc_base_material_item_display"
                            @close-quicksearch="getItemObj"
                          />
                        </el-form-item>
                      </el-col>
                      <el-col>
                        <p style="padding-top: 10px;">
                          <el-button
                            type="primary"
                            @click="addOneContent2"
                          >
                            {{ $t('common.search') }}
                          </el-button>
                        </p>
                      </el-col>
                    </el-row>
                  </el-form>
                  <el-table
                    :data="diaReturnList"
                    style="width: 100%"
                    border
                    max-height="351px"
                    @selection-change="handleSelectionChange2"
                  >
                    <el-table-column
                      type="selection"
                      width="55"
                    />
                    <el-table-column
                      align="center"
                      type="index"
                      :label="$t('purSettlementMod.tabindex')"
                      width="60"
                    />
                    <el-table-column
                      align="center"
                      prop="returnOrderNumber"
                      :label="$t('purSettlementMod.returnOrderNumber')"
                      width="120"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="returnLineNum"
                      :label="$t('purSettlementMod.returnLineNum')"
                      width="90"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="happenDate"
                      :label="$t('purSettlementMod.happenDate')"
                      width="100"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="materialCode"
                      :label="$t('purSettlementMod.materialCode')"
                      width="100"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="materialName"
                      :label="$t('purSettlementMod.materialName')"
                      min-width="100"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="orderNumber"
                      :label="$t('purSettlementMod.orderNumber')"
                      width="150"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="orderLineNum"
                      :label="$t('purSettlementMod.orderLineNumber')"
                      width="100"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="returnNum"
                      :label="$t('purSettlementMod.returnNum')"
                      width="80"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="unitPriceNoTax"
                      :label="$t('purSettlementMod.unitPriceNoTax')"
                      width="100"
                    />
                    <el-table-column
                      align="center"
                      prop="totalAmountNoTax"
                      :label="$t('purSettlementMod.totalAmountNoTax')"
                      width="100"
                      :show-overflow-tooltip="true"
                    />
                  </el-table>
                  <el-row type="flex">
                    <el-col>
                      <c-pagination
                        ref="queryPagination2"
                        style="margin: 5px"
                        class="c-query-table-pagination"
                        :total="queryTotal2"
                        :page-num="viewIndex2"
                        :page-size="viewSize2"
                        @current-change="changeCurrentIndex2"
                        @size-change="changeCurrentSize2"
                      />
                    </el-col>
                  </el-row>
                </div>
                <div
                  slot="footer"
                  class="dialog-footer"
                >
                  <el-button @click="dialogFormVisible2 = false">
                    {{ $t('common.cancel') }}
                  </el-button>
                  <el-button
                    type="primary"
                    @click="addOneReturnItem"
                  >
                    {{ $t('common.confirm') }}
                  </el-button>
                </div>
              </srm-dialog>
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </div>
      <c-toolbar>
        <template slot="right">
          <el-button
            v-if="(curRole === 'BUYER')&&(statementHead.statementStatus === 'CREATE')"
            type="primary"
            @click="saveBill"
          >
            {{ $t('common.staging') }}
          </el-button>
          <el-button
            v-if="(curRole === 'BUYER')&&(statementHead.statementStatus === 'CREATE')"
            type="primary"
            @click="submitBill"
          >
            {{ $t('common.publish') }}
          </el-button>
          <el-button
            v-if="(curRole === 'BUYER')&&(statementHead.statementStatus === 'REJECTED')"
            type="primary"
            @click="cancelBill"
          >
            {{ $t('common.cancelled') }}
          </el-button>
          <el-button
            v-if="(curRole === 'VENDOR')&&(statementHead.statementStatus === 'SUBMITTED')"
            type="primary"
            @click="approvalBill"
          >
            {{ $t('common.toApprove') }}
          </el-button>
          <el-button
            v-if="(curRole === 'VENDOR')&&(statementHead.statementStatus === 'SUBMITTED')"
            type="primary"
            @click="rejectBill"
          >
            {{ $t('common.toRefuse') }}
          </el-button>
        </template>
      </c-toolbar>
    </el-main>
  </el-container>
</template>
<script>
import CToolbar from 'lib@/components/c-toolbar'
import {
  getDictItem
} from '@/api/common'
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import { adaptDictData, parseTime } from '@/utils'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelectTree from 'lib@/components/organization-selector'
import CPagination from 'lib@/components/c-pagination'
export default {
  name: 'StatementBillDetail',
  components: {
    MainHeader,
    CToolbar,
    QuickSearch,
    OrganizationSelectTree,
    CPagination
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'vendorBiddingManagement',
        fileType: 'images'
      },
      queryTotal: -1,
      viewSize: 10,
      viewIndex: 1,
      queryTotal2: -1,
      viewSize2: 10,
      viewIndex2: 1,
      statementHead: {
        'vendorId': null,
        'vendorCode': '',
        'vendorName': '',
        'organizationId': '',
        'organizationName': '',
        'organizationCode': '',
        'currency': '',
        'taxKey': '',
        'taxRate': '',
        'statementStatus': 'CREATE',
        'statementStartTime': '',
        'statementEndTime': '',
        'paymentType': '',
        'termOfPayment': '',
        'receiptAmount': '',
        'returnAmount': '',
        'statementTotalAmount': '',
        'supplierNote': '',
        'purchaserNote': ''
      },
      payModeList: [],
      fileuploadList: [],
      receiptList: [],
      returnList: [],
      diaReturnList: [],
      receptionList3: [],
      diaReceptionList: [],
      diaReceptionList2: [],
      diaReceptionList3: [],
      activeDims: ['1', '2', '3', '4', '5'],
      rules: {
        vendorCode: [{ required: true, message: this.$t('quota.vendorTips') }], // 请选择供应商
        fullPathId: [{ required: true, message: this.$t('dataConfMod.msgSelectOrganation') }]// 请输入采购组织
      },
      receiptForm: {
        warehouseReceiptNumber: '',
        sourceNumber: '',
        materialId: '',
        materialCode: '',
        materialName: ''
      },
      returnForm: {
        returnOrderNumber: '',
        sourceNumber: '',
        materialId: '',
        materialCode: '',
        materialName: ''
      },
      isDisabled: this.$attrs.params.flag == 'edit',
      formLabelWidth: '120px',
      dialogFormVisible: false,
      dialogFormVisible2: false,
      dialogFormVisible3: false,
      isModify: false,
      statusList: [],
      pubRangeList: [],
      paymentTermsList: [],
      receplistSelection: [],
      returnlistSelection: [],
      curRole: this.$store.getters.userType
    }
  },
  created () {
    if (this.$attrs.params.flag == 'edit') {
      this.getFormDetail()
    }
    // get---
    // 付款条件
    getDictItem('PAYMENT_TERMS').then(res => {
        this.paymentTermsList = adaptDictData(res.data, 'dict')
      })
    // 对账状态
    getDictItem('RECONCILIATION_STATUS').then(res => {
        this.statusList = adaptDictData(res.data, 'dict')
      })
    // 发布范围
    getDictItem('PUBLISH_RANGE').then(res => {
      this.pubRangeList = adaptDictData(res.data, 'dict')
    })
    getDictItem('PAYMENT_MODE').then(res => {
      this.payModeList = adaptDictData(res.data, 'dict')
    })
  },
  methods: {
    getFormDetail () {
      this.$http({
        url: '/api-sup-ce/pm/ps/statementHead/getStatementById',
        method: 'GET',
        params: { statementHeadId: this.$attrs.params.row.statementHeadId },
        loading: true
      })
        .then(data => {
          this.statementHead = data.data.statementHead
          this.receiptList = data.data.receiptList
          this.returnList = data.data.returnList
          this.fileuploadList = data.data.fileuploadList
        })
        .catch(err => {
          console.log(err)
        })
    },
    deleteOneContent (index, row) {
      this.receiptList.splice(index, 1)
    },
    deleteOneContent2 (index, row) {
      this.returnList.splice(index, 1)
    },
    deleteOneContent3 (index, row) {
      this.receptionList3.splice(index, 1)
    },
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
    },
    backBill () {
      if (this.$attrs.params.flag == 'edit') {
        this.$emit(
          'tab-remove',
          'statementBillDetail' + this.$attrs.params.row.statementNumber
        )
      } else {
        this.$emit('tab-remove', 'statementBillDetail')
      }
      this.__setTabTodo('statementBillList.getQuerydata')
    },
    openOneDialog () {
      if (!this.statementHead.vendorCode) {
        this.$message.info(this.$t('bid_mod.setPermissionError'))
        return
      }
      this.receiptForm.vendorCode = this.statementHead.vendorCode
      this.receiptForm.vendorId = this.statementHead.vendorId
      this.dialogFormVisible = true
    },
    addOneContent () {
      let params = Object.assign({ pageSize: this.viewSize,
          pageNum: this.viewIndex }, this.receiptForm)
      this.$http({
        url: '/api-sup-ce/pm/ps/statementHead/listStatementReceiptDTOPage',
        method: 'POST',
        data: params,
        loading: true
      }).then(data => {
          if (data && data.data) {
            this.diaReceptionList = data.data.list
            this.queryTotal = data.data.total
            // this.dialogFormVisible =true;
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    openContent2Dialog () {
      if (!this.statementHead.vendorCode) {
        this.$message.info(this.$t('bid_mod.setPermissionError'))
        return
      }
      this.returnForm.vendorCode = this.statementHead.vendorCode
      this.returnForm.vendorId = this.statementHead.vendorId
      this.dialogFormVisible2 = true
    },
    addOneContent2 () {
      let params = Object.assign({ pageSize: this.viewSize2,
          pageNum: this.viewIndex2 }, this.returnForm)
      this.$http({
        url: '/api-sup-ce/pm/ps/statementHead/listStatementReturnDTOPage',
        method: 'POST',
        data: params,
        loading: true
      }).then(data => {
          if (data && data.data) {
            this.diaReturnList = data.data.list
            this.queryTotal2 = data.data.total
            // this.dialogFormVisible2 =true;
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    addOneContent3 () {
      // this.receptionList3.push({})
      this.dialogFormVisible3 = true
    },
    handleSelectionChange (selection) {
      this.receplistSelection = selection
    },
    handleSelectionChange2 (selection) {
      this.returnlistSelection = selection
    },
    getItemObj (val, scope) {
      scope.materialId = val ? val.materialId : ''
      scope.materialCode = val ? val.materialCode : ''
      scope.materialName = val ? val.materialName : ''
    },
    // 改变 currentNum
    changeCurrentIndex (currentNum) {
      this.viewIndex = currentNum
      this.addOneContent()
    },
    // 改变 currentSize
    changeCurrentSize (currentSize) {
      this.viewSize = currentSize
      this.addOneContent()
    },
    // 改变 currentNum2
    changeCurrentIndex2 (currentNum) {
      this.viewIndex2 = currentNum
      this.addOneContent2()
    },
    // 改变 currentSize2
    changeCurrentSize2 (currentSize) {
      this.viewSize2 = currentSize
      this.addOneContent2()
    },
    // 选择组织2
    addOrgHandle (e, value, scope) {
      scope.organizationId = e ? e.organizationId : ''
      scope.organizationCode = e ? e.organizationCode : ''
      scope.organizationName = e ? e.organizationName : ''
      // this.queryCompanyList({ orgId: e.organizationId });

      this.$http({
        url: '/api-sup/info/financeInfo/getByCompanyIdAndOrgId',
        method: 'GET',
        params: {
          orgId: e.organizationId,
          companyId: this.statementHead.vendorId
        },
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            this.statementHead.taxRate = data.data.taxRate
            this.statementHead.currency = data.data.clearCurrency
            this.statementHead.paymentMethod = data.data.paymentMethod
            this.statementHead.termOfPayment = data.data.paymentTerms
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    addOneReturnItem () {
      this.returnList = this.returnList.concat(this.returnlistSelection)
      if (this.returnList.length > 0) {
        let totalAmountNoTaxArr = this.returnList.map(v => v.totalAmountNoTax)
        let alltotalAmountNoTax = totalAmountNoTaxArr.reduce((p, n) => Number(p) + Number(n))
        this.statementHead.returnAmount = alltotalAmountNoTax
        if (this.statementHead.receiptAmount && this.statementHead.returnAmount) {
          this.statementHead.statementTotalAmount = Number(this.statementHead.receiptAmount) - Number(this.statementHead.returnAmount)
        } else {
          this.statementHead.statementTotalAmount = ''
        }
      }
      this.dialogFormVisible2 = false
    },
    addOneReceptionItem () {
      let warehouseReceiptIdArr = this.receiptList.map(v => v.warehouseReceiptId)
      for (let i of this.receplistSelection) {
        if (!warehouseReceiptIdArr.includes(i.warehouseReceiptId)) {
          this.receiptList.push(i)
        }
      }
      // this.receiptList = this.receiptList.concat(this.receplistSelection);
      if (this.receiptList.length > 0) {
        let totalAmountNoTaxArr = this.receiptList.map(v => v.totalAmountNoTax)
        let alltotalAmountNoTax = totalAmountNoTaxArr.reduce((p, n) => Number(p) + Number(n))
        this.statementHead.receiptAmount = alltotalAmountNoTax
      }
      this.dialogFormVisible = false
    },
    addUploadOne () {
      this.fileuploadList.push({
        fileuploadId: null,
        fileSourceName: ''
      })
    },
    outerHandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.fileuploadId = fileId.toString()
      row.fileSourceName = fileName
    },
    handleDelClick (index, row) {
      this.fileuploadList.splice(index, 1)
    },
    readOneContent () {},
    copyOneContent () {},
    cancelBill () {
        this.$http({
          url: '/api-sup-ce/pm/ps/statementHead/cancelStatement',
          method: 'GET',
          params: { statementHeadId: this.$attrs.params.row.statementHeadId },
          loading: true
        }).then(data => {
          this.$message({
            message: this.$t('common.success'),
            type: 'success'
          })
          this.$emit(
            'tab-remove',
            'statementBillDetail' + this.$attrs.params.row.statementNumber
          )
          this.__setTabTodo('statementBillList.getQuerydata')
        })
        .catch(err => {
          console.log(err)
        })
    },
    approvalBill () {
      this.$prompt(this.$t('purSettlementMod.supplierNoteTips'), this.$t('purSettlementMod.supplierNote'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel')
      })
        .then(({ value }) => {
          this.$http({
            url: '/api-sup-ce/ps/statementHead/passStatement',
            method: 'POST',
            data: {
              statementHeadId: this.$attrs.params.row.statementHeadId,
              supplierNote: value
            },
            loading: true
          })
            .then(data => {
              this.$message({
                message: this.$t('common.success'),
                type: 'success'
              })
              this.$emit(
                'tab-remove',
                'statementBillDetail' + this.$attrs.params.row.statementNumber
              )
              this.__setTabTodo('statementBillList.getQuerydata')
            })
            .catch(err => {
              console.log(err)
            })
        })
        .catch(() => {})
    },
    rejectBill () {
      this.$prompt(this.$t('contractMod.msgRefuseReason'), this.$t('oneStopShopping.refusedReason'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel')
      })
        .then(({ value }) => {
          this.$http({
            url: '/api-sup-ce/ps/statementHead/rejectStatement',
            method: 'POST',
            data: {
              statementHeadId: this.$attrs.params.row.statementHeadId,
              rejectReason: value
            },
            loading: true
          })
            .then(data => {
              this.$message({
                message: this.$t('common.success'),
                type: 'success'
              })
              this.$emit(
                'tab-remove',
                'statementBillDetail' + this.$attrs.params.row.statementNumber
              )
              this.__setTabTodo('statementBillList.getQuerydata')
            })
            .catch(err => {
              console.log(err)
            })
        })
        .catch(() => {})
    },
    saveBill () {
      if (!this.statementHead.vendorCode) {
        this.$message.info(this.$t('vendorMod.msgVendor'))
        return
      }
      if (!this.statementHead.fullPathId) {
        this.$message.info(this.$t('perfMod.selectOrg'))
        return
      }
      let allParam = {
        'statementHead': this.statementHead,
        'fileuploadList': this.fileuploadList,
        'receiptList': this.receiptList,
        'returnList': this.returnList
      }
      let urlById = '/api-sup-ce/pm/ps/statementHead/saveStatement'
      if (this.$attrs.params.flag == 'edit') {
        urlById = '/api-sup-ce/pm/ps/statementHead/updateStatement'
      }
      this.$http({
        url: urlById,
        method: 'POST',
        data: allParam,
        loading: true
      })
        .then(data => {
          this.$message({
            message: this.$t('common.successSave'),
            type: 'success'
          })
          if (this.$attrs.params.flag == 'edit') {
            this.$emit(
              'tab-remove',
              'statementBillDetail' + this.$attrs.params.row.statementNumber
            )
          } else {
            this.$emit('tab-remove', 'statementBillDetail')
          }
          this.__setTabTodo('statementBillList.getQuerydata')
        })
        .catch(err => {
          console.log(err)
        })
    },
    submitBill () { // 发布
      if (!this.statementHead.vendorCode) {
        this.$message.info(this.$t('quota.vendorTips'))
        return
      }
      if (!this.statementHead.fullPathId) {
        this.$message.info(this.$t('perfMod.selectOrg'))
        return
      }
      if (!this.statementHead.statementStartTime) {
        this.$message.info(this.$t('purSettlementMod.statementStartTimeTips'))
        return
      }
      if (!this.statementHead.statementEndTime) {
        this.$message.info(this.$t('purSettlementMod.statementEndTimeTips'))
        return
      }
      if (this.receiptList.length === 0) {
        this.$message.info(this.$t('orderMod.msgOrder[43]'))
        return
      }
      let allParam = {
        'statementHead': this.statementHead,
        'fileuploadList': this.fileuploadList,
        'receiptList': this.receiptList,
        'returnList': this.returnList
      }
      this.$http({
        url: '/api-sup-ce/pm/ps/statementHead/submitStatement',
        method: 'POST',
        data: allParam,
        loading: true
      })
        .then(data => {
          this.$message({
            message: this.$t('common.successSave'),
            type: 'success'
          })
          if (this.$attrs.params.flag == 'edit') {
            this.$emit(
              'tab-remove',
              'statementBillDetail' + this.$attrs.params.row.statementNumber
            )
          } else {
            this.$emit('tab-remove', 'statementBillDetail')
          }
          this.__setTabTodo('statementBillList.getQuerydata')
        })
        .catch(err => {
          console.log(err)
        })
    }
  }
}
</script>
<style scoped lang="scss">
.the-statementBillDetail-detail {
  .form-container2{padding: 5px;}
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
}
.btn_line {
  margin: 0 0 10px 0;
}
</style>
