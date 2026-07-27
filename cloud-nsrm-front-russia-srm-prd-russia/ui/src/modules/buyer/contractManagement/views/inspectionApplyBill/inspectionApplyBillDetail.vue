<template>
  <el-container
    class="flex-container the-inspectionApplyBillDetail-detail"
    direction="vertical"
  >
    <el-main>
      <div class="form-container2">
        <el-form
          ref="acceptOrder"
          :model="acceptOrder"
          label-width="80px"
          label-position="top"
          class="form-incontainer"
          :rules="rules"
          :disabled="isReadOnly || isApprovalOnly"
        >
          <el-collapse
            v-model="activeDims"
            class="tab-form-style"
          >
            <el-collapse-item
              :title="$t('contractMod.acceptOrderInfo')"
              name="1"
            >
              <el-row type="flex">
                <el-col>
                  <el-form-item
                    :label="$t('bidMod.businessEntity')"
                    :label-width="formLabelWidth"
                  >
                    <organization-selector
                      ref="organizationSelector"
                      v-model="acceptOrder.ceeaOrgId"
                      :parent-id="-1"
                      node-type="OU"
                      @select="selectHandler"
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.acceptNumber')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="acceptOrder.acceptNumber"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.vendorName')"
                    :label-width="formLabelWidth"
                  >
                    <quick-search
                      disabled
                      :show-input="acceptOrder.vendorName"
                      show-key="companyName"
                      :scope-data="acceptOrder"
                      name="scc_sup_company_info"
                      @close-quicksearch="getVendorObj"
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.applicationStatus')"
                    :label-width="formLabelWidth"
                  >
                    <dict-select
                      v-model="acceptOrder.acceptStatus"
                      code="YSSQ_STATUS"
                      disabled
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row type="flex">
                <el-col>
                  <el-form-item
                    :label="$t('orderMod.orderNumber')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="acceptOrder.orderNumber"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('purchaseDemand.applyDate')"
                    :label-width="formLabelWidth"
                  >
                    <el-date-picker
                      v-model="acceptOrder.ceeaApplicationDate"
                      type="date"
                      format="yyyy-MM-dd"
                      value-format="yyyy-MM-dd"
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.followTechnicalDoc')"
                    :label-width="formLabelWidth"
                  >
                    <el-checkbox
                      v-model="acceptOrder.ceeaTechnicalDocuments"
                      true-label="1"
                      false-label="0"
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.withTools')"
                    :label-width="formLabelWidth"
                  >
                    <el-checkbox
                      v-model="acceptOrder.ceeaToolEquipment"
                      true-label="1"
                      false-label="0"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row type="flex">
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.totalAmount')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="acceptOrder.totalNum"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('relegationEntity.key30')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="acceptOrder.totalAmount"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.otherExpense')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="acceptOrder.ceeaTotalQuantity"
                      v-input-format="{ type: 'number' }"
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.totalValue')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="acceptOrder.totalValue"
                      disabled
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row type="flex">
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.remark')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="acceptOrder.remark"
                      type="textarea"
                      :rows="2"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-collapse-item>
            <el-collapse-item
              :title="$t('contractMod.acceptDetail')"
              name="2"
            >
              <p style="margin:0">
                <el-button
                  type="primary"
                  @click="openDialog"
                >
                  {{
                    $t("common.add")
                  }}
                </el-button>
              </p>
              <el-table
                :data="acceptDetails"
                style="width: 100%"
                border
                max-height="250px"
              >
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('contractMod.tabindex')"
                  width="60"
                />
                <el-table-column
                  align="center"
                  prop="orderNumber"
                  :label="$t('orderMod.orderNumber')"
                  width="150"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="orderLineNumber"
                  :label="$t('orderMod.orderLineNum')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="organizationName"
                  :label="$t('purchaseDemand.invOrg')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="ceeaSmallClassMat"
                  :label="$t('bidMod.materialSubclassName')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="materialCode"
                  :label="$t('contractMod.materialCode')"
                  width="150"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="ceeaDesc"
                  :label="$t('bidMod.itemDesc')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="ceeaUnit"
                  :label="$t('contractMod.unit')"
                  width="60"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="waitAcceptQuantity"
                  :label="$t('contractMod.appAcceptQuantity')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="ceeaWeight"
                  :label="$t('common.weight')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="ceeaBulk"
                  :label="$t('contractMod.bulk')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="ceeaProductionDate"
                  :label="$t('contractMod.productionDate')"
                  width="150"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="ceeaProductionNum"
                  :label="$t('contractMod.productionNum')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="ceeaProjectApprovalNum"
                  :label="$t('purchaseDemand.ceeaProjectApprovalNum')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="ceeaUntaxedPrice"
                  :label="$t('bid_mod.untaxedPrice')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="deliveryQuantity"
                  :label="$t('orderMod.buyerOrderSynergy.orderNum')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="ceeaTaxrate"
                  :label="$t('dataConfMod.settingGuide.step3.4')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="ceeaUnitPrice"
                  :label="$t('purchaseDemand.taxPrice')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="ceeaRequirementHeadNum"
                  :label="$t('purchaseDemand.purRequisitionNum')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="ceeaRownum"
                  :label="$t('purchaseDemand.rowNum')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="ceeaContractNo"
                  :label="$t('orderMod.buyerOrderSynergy.contractNo')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  :label="$t('formula.handle')"
                  width="60"
                  fixed="right"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="deleteOneContent(scope.$index, scope.row)"
                    >
                      {{ $t("common.delete") }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
              <srm-dialog
                :title="$t('contractMod.addacceptDetail')"
                size="large"
                :visible.sync="dialogVisible"
                :close-on-click-modal="false"
              >
                <div>
                  <el-form
                    ref="filterForm"
                    :model="filterForm"
                    label-width="80px"
                    label-position="top"
                    class="form-incontainer"
                  >
                    <el-row type="flex">
                      <el-col>
                        <el-form-item
                          :label="$t('orderMod.orderNumber')"
                          :label-width="formLabelWidth"
                        >
                          <el-input
                            v-model="filterForm.orderNumber"
                            disabled
                          />
                        </el-form-item>
                      </el-col>
                      <el-col>
                        <el-form-item
                          :label="$t('bidMod.businessEntity')"
                          :label-width="formLabelWidth"
                        >
                          <organization-selector
                            ref="organizationSelectorFilter1"
                            v-model="filterForm.orgId"
                            :parent-id="-1"
                            node-type="OU"
                            @select="selectHandler2"
                          />
                        </el-form-item>
                      </el-col>
                      <el-col>
                        <el-form-item
                          :label="$t('purchaseDemand.invOrg')"
                          :label-width="formLabelWidth"
                        >
                          <organization-selector
                            ref="organizationSelectorFilter"
                            v-model="filterForm.organizationId"
                            :parent-id="filterForm.orgId"
                            node-type="INV"
                            @select="selectHandler3"
                          />
                        </el-form-item>
                      </el-col>
                    </el-row>
                    <el-row type="flex">
                      <el-col>
                        <el-form-item
                          :label="$t('common.vendor')"
                          :label-width="formLabelWidth"
                        >
                          <el-input v-model="filterForm.vendorCode" />
                        </el-form-item>
                      </el-col>
                      <el-col>
                        <el-form-item
                          :label="$t('supRisk.material')"
                          :label-width="formLabelWidth"
                        >
                          <el-input v-model="filterForm.materialCode" />
                        </el-form-item>
                      </el-col>
                      <el-col>
                        <el-form-item
                          :label="$t('bidMod.materialSubclassName')"
                          :label-width="formLabelWidth"
                        >
                          <el-input v-model="filterForm.categoryCode" />
                        </el-form-item>
                      </el-col>
                    </el-row>
                    <el-row type="flex">
                      <el-col>
                        <el-form-item
                          :label="$t('orderMod.buyerOrderSynergy.orderDateFrom')"
                          :label-width="formLabelWidth"
                        >
                          <el-date-picker v-model="filterForm.startDate" />
                        </el-form-item>
                      </el-col>
                      <el-col>
                        <el-form-item
                          :label="$t('orderMod.buyerOrderSynergy.orderDateTo')"
                          :label-width="formLabelWidth"
                        >
                          <el-date-picker v-model="filterForm.endDate" />
                        </el-form-item>
                      </el-col>
                      <el-col>
                        <el-form-item
                          :label="$t('purchaseDemand.purRequisitionNum')"
                          :label-width="formLabelWidth"
                        >
                          <el-input v-model="filterForm.requirementHeadNum" />
                        </el-form-item>
                      </el-col>
                    </el-row>
                    <el-row type="flex">
                      <el-col>
                        <el-form-item
                          :label="$t('orderMod.buyerOrderSynergy.contractNo')"
                          :label-width="formLabelWidth"
                        >
                          <el-input v-model="filterForm.contractNo" />
                        </el-form-item>
                      </el-col>
                      <el-col>
                        <el-form-item
                          :label="$t('qualitySynergy.purchaseAgent1')"
                          :label-width="formLabelWidth"
                        >
                          <el-input v-model="filterForm.buyer" />
                        </el-form-item>
                      </el-col>
                      <el-col>
                        <el-form-item
                          :label="$t('bidMod.acceptanceNo')"
                          :label-width="formLabelWidth"
                        >
                          <el-input v-model="filterForm.acceptApplicationNum" />
                        </el-form-item>
                      </el-col>
                    </el-row>
                  </el-form>
                  <p>
                    <el-button
                      type="primary"
                      @click="queryItemList"
                    >
                      {{ $t("common.search") }}
                    </el-button>
                    <el-button @click="resetFilterForm">
                      {{ $t("common.reset") }}
                    </el-button>
                    <el-button
                      type="primary"
                      @click="addOneContent"
                    >
                      {{ $t("common.confirm") }}
                    </el-button>
                  </p>
                </div>
                <el-table
                  :data="displayItemTable"
                  style="width: 100%"
                  border
                  height="345px"
                  highlight-current-row
                  @selection-change="handleItemSelection"
                >
                  <el-table-column
                    align="center"
                    type="index"
                    :label="$t('contractMod.tabindex')"
                    width="60"
                  />
                  <el-table-column
                    type="selection"
                    width="55"
                  />
                  <el-table-column
                    align="center"
                    prop="orderNumber"
                    :label="$t('orderMod.orderNumber')"
                    width="150"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="orderLineNumber"
                    :label="$t('orderMod.orderLineNum')"
                    width="100"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="orgName"
                    :label="$t('bidMod.businessEntity')"
                    width="100"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="organizationName"
                    :label="$t('purchaseDemand.invOrg')"
                    width="100"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="buyer"
                    :label="$t('qualitySynergy.purchaseAgent1')"
                    width="100"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="smallClassMat"
                    :label="$t('bidMod.materialSubclassName')"
                    width="100"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="itemCode"
                    :label="$t('contractMod.materialCode')"
                    width="150"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="itemLongDesc"
                    :label="$t('bidMod.itemDesc')"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="deliveryQuantity"
                    :label="$t('orderMod.buyerOrderSynergy.orderNum')"
                    width="100"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="warehouseReceiptQuantity"
                    :label="$t('orderMod.receiveNum')"
                    width="100"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="acceptQuantity"
                    :label="$t('contractMod.receiveNum')"
                    width="100"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="remainingQuantity"
                    :label="$t('contractMod.remainAcceptQuantity')"
                    width="120"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="untaxedPrice"
                    :label="$t('bid_mod.untaxedPrice')"
                    width="100"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="unitPrice"
                    :label="$t('purchaseDemand.taxPrice')"
                    width="100"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="taxrate"
                    :label="$t('dataConfMod.settingGuide.step3.4')"
                    width="100"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="currency"
                    :label="$t('dataConfMod.settingGuide.step3.2')"
                    width="100"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="requirementHeadNum"
                    :label="$t('purchaseDemand.purRequisitionNum')"
                    width="100"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="rownum"
                    :label="$t('purchaseDemand.rowNum')"
                    width="100"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="contractNo"
                    :label="$t('orderMod.buyerOrderSynergy.contractNo')"
                    width="100"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="acceptApplicationNum"
                    :label="$t('contractMod.acceptApplicationNum')"
                    width="100"
                    :show-overflow-tooltip="true"
                  />
                </el-table>
                <c-pagination
                  :total="parentOrgTableDataPage.total"
                  :page-num="parentOrgTableDataPage.pageNum"
                  :page-size="parentOrgTableDataPage.pageSize"
                  @current-change="parentDataCurrentChange"
                  @size-change="parentDataSizeChange"
                />
              </srm-dialog>
            </el-collapse-item>
            <el-collapse-item
              :title="$t('contractMod.followTechnicalDoc')"
              name="3"
            >
              <p class="btn_line">
                <el-button
                  type="primary"
                  :disabled="acceptOrder.ceeaTechnicalDocuments!=='1'"
                  @click="addUploadOneFile"
                >
                  {{ $t('common.add') }}
                </el-button>
              </p>
              <el-table
                :data="techFile"
                style="width: 100%"
                border
                max-height="250px"
              >
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('purSettlementMod.tabindex')"
                  width="50"
                />
                <el-table-column
                  align="center"
                  prop="fileSourceName"
                  :label="$t('contract_mod.fileName')"
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
                  :label="$t('components.fileupload.uploadDate')"
                  width="150"
                />
                <el-table-column
                  align="center"
                  prop="comments"
                  :label="$t('bidMod.appraisRemark')"
                  min-width="150"
                />
                <el-table-column
                  :label="$t('common.operation')"
                  width="60"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="handleDelFileClick(scope.$index, scope.row)"
                    >
                      {{ $t('common.delete') }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <el-collapse-item
              :title="$t('contractMod.withTools')"
              name="4"
            >
              <p class="btn_line">
                <el-button
                  type="primary"
                  :disabled="acceptOrder.ceeaToolEquipment!=='1'"
                  @click="addOneEquipment"
                >
                  {{ $t('common.add') }}
                </el-button>
              </p>
              <el-table
                :data="toolEqp"
                style="width: 100%"
                border
                max-height="250px"
              >
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('purSettlementMod.tabindex')"
                  width="50"
                />
                <el-table-column
                  align="center"
                  prop="toolEqpName"
                  :label="$t('contractMod.toolEqpName')"
                  min-width="150"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.toolEqpName" />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="eqpSpecification"
                  :label="$t('contractMod.eqpSpecification')"
                  min-width="150"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.eqpSpecification" />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="quantity"
                  :label="$t('bid_mod.quantity')"
                  min-width="150"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.quantity"
                      v-input-format="{ type: 'number' }"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="unit"
                  :label="$t('dataConfMod.settingGuide.step3.3')"
                  min-width="150"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.unit" />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="remark"
                  :label="$t('bidMod.appraisRemark')"
                  min-width="150"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.remark" />
                  </template>
                </el-table-column>
                <el-table-column
                  :label="$t('common.operation')"
                  width="60"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="handleDelEqipmentClick(scope.$index, scope.row)"
                    >
                      {{ $t('common.delete') }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <el-collapse-item
              :title="$t('bidMod.fileInfo')"
              name="5"
            >
              <p class="btn_line">
                <el-button
                  type="primary"
                  :disabled="acceptOrder.ceeaAssetQualification!=='1'"
                  @click="addUploadOneApproval"
                >
                  {{ $t('common.add') }}
                </el-button>
              </p>
              <el-table
                :data="assetFile"
                style="width: 100%"
                border
                max-height="250px"
              >
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('purSettlementMod.tabindex')"
                  width="50"
                />
                <el-table-column
                  align="center"
                  prop="fileSourceName"
                  :label="$t('contract_mod.fileName')"
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
                  :label="$t('components.fileupload.uploadDate')"
                  width="150"
                />
                <el-table-column
                  align="center"
                  prop="comments"
                  :label="$t('bidMod.appraisRemark')"
                  min-width="150"
                />
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
          </el-collapse>
        </el-form>
      </div>
    </el-main>
  </el-container>
</template>
<script>
import CToolbar from 'lib@/components/c-toolbar'
import { tabTodoMixin } from '@/utils/mixins'
import QuickSearch from 'lib@/components/QuickSearch'
import MainHeader from 'lib@/components/Table/MainHeader'
import { parseTime } from '@/utils'
import OrganizationSelector from 'lib@/components/organization-selector'
import CPagination from 'lib@/components/c-pagination'
import DictSelect from '@/library/components/c-select/dict-select'

export default {
  name: 'InspectionApplyBillDetail',
  components: {
    MainHeader,
    CToolbar,
    OrganizationSelector,
    CPagination,
    QuickSearch,
    DictSelect
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      curRole: this.$store.getters.userType,
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'vendorBiddingManagement',
        fileType: 'images'
      },
      filterForm: {
        orgId: null,
        orgCode: null,
        orgName: null,
        organizationId: null,
        organizationCode: null,
        organizationName: null,
        materialCode: null,
        materialName: null,
        categoryCode: null,
        requirementHeadNum: null,
        startDate: null,
        endDate: null
      },
      parentOrgTableDataPage: {
        total: 0,
        pageNum: 1,
        pageSize: 20
      },
      parentOrgQueryForm: {
        pageNum: 1,
        pageSize: 10
      },
      acceptOrder: {
        ceeaOrgId: null,
        ceeaOrgCode: null,
        ceeaOrgName: null,
        acceptOrderId: null,
        acceptNumber: '',
        ceeaAcceptApplicationNum: '',
        acceptDate: parseTime(new Date(), '{y}-{m}-{d}'),
        vendorName: '',
        vendorCode: '',
        vendorId: null,
        ceeaApplictionCode: '',
        acceptUserName: '',
        acceptStatus: 'APPLY_DRAFT',
        remark: '',
        ceeaAssetName: '',
        ceeaApplicationDate: '',
        ceeaSpecificationsModels: '',
        ceeaStoreCode: '',
        ceeaAssetNumber: '',
        ceeaProjectNumber: '',
        ceeaUserDepartment: '',
        ceeaUserName: '',
        ceeaToolEquipment: '0',
        ceeaTechnicalDocuments: '0',
        ceeaAssetQualification: '0',
        totalNum: 0,
        totalAmount: 0,
        totalValue: 0,
        ceeaTotalQuantity: '',
        ceeaDraftsmanOpinion: '',
        ceeaAssetType: '',
        orderNum: ''
      },
      acceptDetails: [],
      toolEqp: [],
      techFile: [],
      assetFile: [],
      activeDims: ['1', '2', '3', '4', '5', '6'],
      rules: {
        contractNo: [{ required: true, message: '请输入合同编号' }],
        acceptDate: [{ required: true, message: '请输入验收日期' }],
        acceptUserName: [{ required: true, message: '请输入验收人' }]
      },
      isDisabled: this.$attrs.params.flag == 'edit',
      isReadOnly: this.$attrs.params.flag == 'readOnly',
      isApprovalOnly: this.$attrs.params.flag == 'approvalOnly',
      formLabelWidth: '120px',
      isModify: false,
      dialogVisible: false,
      displayItemTable: [],
      multipleSelection: []
    }
  },
  created () {
    if (this.$attrs.params.flag == 'add') {
      // 默认加载采购商联系方式，如果没有才需要填写
      if (this.$store.state.user && this.$store.state.user.userInfo) {
        console.log(
          '-this.$store.state.user.userInfo--',
          this.$store.state.user.userInfo
        )
        this.acceptOrder.vendorId = this.$store.state.user.userInfo.companyId
        this.acceptOrder.vendorCode = this.$store.state.user.userInfo.companyCode
        this.acceptOrder.vendorName = this.$store.state.user.userInfo.companyName
      }
    } else if (this.$attrs.params.flag == 'edit') {
      this.getFormDetail(this.$attrs.params.row.acceptOrderId)
    } else if (
      this.$attrs.params.flag == 'approvalOnly' ||
      this.$attrs.params.flag == 'readOnly'
    ) {
      this.getFormDetail(this.$attrs.params.row.acceptOrderId)
    }
  },
  methods: {
    getFormDetail (acceptOrderId) {
      this.$http({
        url: '/api-cm/accept/acceptOrder/getAcceptDTO',
        method: 'GET',
        params: { acceptOrderId: acceptOrderId },
        loading: true
      })
        .then(data => {
          if (data.data) {
            this.acceptOrder = data.data.acceptOrder
            this.toolEqp = data.data.toolEqp
            this.techFile = data.data.techFile
            this.assetFile = data.data.assetFile
            // console.log('--data', data.data)
            this.acceptDetails = data.data.acceptDetails
            this.acceptOrder.ceeaTechnicalDocuments = String(this.acceptOrder.ceeaTechnicalDocuments)
            this.acceptOrder.ceeaToolEquipment = String(this.acceptOrder.ceeaToolEquipment)
            this.acceptOrder.ceeaAssetQualification = String(this.acceptOrder.ceeaAssetQualification)
            this.acceptOrder.totalNum = 0
            this.acceptOrder.totalAmount = 0
            this.acceptDetails.map(v => {
              this.acceptOrder.totalNum += v.acceptQuantity || 0
              this.acceptOrder.totalAmount += v.acceptQuantity * v.unitPrice || 0
            })
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    deleteOneContent (index, row) {
      this.acceptDetails.splice(index, 1)
    },
    selectHandler (node, value, scope) {
      this.acceptOrder.ceeaOrgId = node.organizationId
      this.acceptOrder.ceeaOrgCode = node.organizationCode
      this.acceptOrder.ceeaOrgName = node.organizationName
    },
    selectHandler2 (node, value, scope) {
      this.filterForm.orgId = node.organizationId
      this.filterForm.orgCode = node.organizationCode
      this.filterForm.orgName = node.organizationName
    },
    selectHandler3 (node, value, scope) {
      this.filterForm.organizationId = node.organizationId
      this.filterForm.organizationCode = node.organizationCode
      this.filterForm.organizationName = node.organizationName
    },
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
    },
    getItemObj (val, scope) {
      scope.materialId = val ? val.materialId : ''
      scope.materialCode = val ? val.materialCode : ''
      scope.materialName = val ? val.materialName : ''
      scope.specification = val ? val.specification : ''
      scope.unitCode = val ? val.unit : ''
      scope.unitName = val ? val.unitName : ''
    },
    handleItemSelection (val) {
      this.multipleSelection = val
    },
    backBill () {
      if (this.$attrs.params.flag == 'edit') {
        this.$emit(
          'tab-remove',
          'inspectionApplyBillDetail' + this.$attrs.params.row.ceeaAcceptApplicationNum
        )
      } else {
        this.$emit('tab-remove', 'inspectionApplyBillDetail')
      }
      this.__setTabTodo('inspectionApplyBillList.getQuerydata')
    },
    outerHandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.fileuploadId = fileId.toString()
      row.fileSourceName = fileName
    },
    openDialog () {
      this.dialogVisible = true
      /* if(!this.acceptOrder.contractNo){
        this.$message.error('请先输入合同编号');
        return
      } */
      /* this.$http({
        url: "/api-sup-ce/order/deliveryNoteDetail/listPage",
        method: "POST",
        data: {
          pageNum: 1,
          pageSize: 1111,
          externalNum: this.acceptOrder.contractNo
        },
        loading: true
      })
        .then(data => {
          // debugger
          if (data && data.data) {
            this.displayItemTable = data.data.list;
            this.dialogVisible = true;
          }
        })
        .catch(err => {
          console.log(err);
        }); */
    },
    parentDataCurrentChange (num) {
      this.parentOrgQueryForm.pageNum = num
      this.queryItemList()
    },
    parentDataSizeChange (size) {
      this.parentOrgQueryForm.pageSize = size
      this.queryItemList()
    },
    queryItemList () {
      const data = { ...this.parentOrgQueryForm, ...this.filterForm }
      this.$http({
        url: '/api-cm/accept/detailLine/listPageByParm',
        method: 'POST',
        data: data,
        loading: true
      }).then(res => {
          this.displayItemTable = res.data.list
          this.parentOrgTableDataPage.total = res.data.total
          this.dialogVisible = true
      })
    },
    resetFilterForm () {
      for (let i in this.filterForm) {
        this.filterForm[i] = ''
      }
    },
    addOneContent () {
      if (this.multipleSelection.length === 0) {
        return
      }
      this.multipleSelection.map(v => {
        this.acceptDetails.push({
          orderNumber: v.orderNumber,
          orderLineNumber: v.orderLineNumber,
          ceeaOrganizationId: v.organizationId,
          ceeaOrganizationCode: v.organizationCode,
          ceeaOrganizationName: v.organizationName,
          ceeaSmallClassMat: v.smallClassMat,
          materialCode: v.itemCode,
          ceeaDesc: v.itemLongDesc,
          ceeaUnit: v.unit,
          deliveryQuantity: v.deliveryQuantity,
          waitAcceptQuantity: v.remainingQuantity,
          ceeaWeight: v.weight,
          ceeaBulk: v.bulk,
          ceeaOrg: v.orgId,
          ceeaOrgCode: v.orgCode,
          ceeaOrgName: v.orgName,
          ceeaProductionDate: v.productionDate,
          ceeaProductionNum: v.productionNum,
          ceeaProjectApprovalNum: v.projectApprovalNum,
          ceeaUntaxedPrice: v.untaxedPrice,
          ceeaUnitPrice: v.unitPrice,
          ceeaTaxrate: v.taxrate,
          ceeaRequirementHeadNum: v.requirementHeadNum,
          ceeaRownum: v.rownum,
          ceeaContractNo: v.contractNo,
          ceeaAcceptApplicationNum: v.acceptApplicationNum,
          warehouseReceiptQuantity: v.acceptQuantity
        })
        // 总金额=每一行汇总【含税单据*验收数量】
        this.acceptOrder.totalNum += v.acceptQuantity || 0
        this.acceptOrder.totalAmount += v.acceptQuantity * v.unitPrice || 0
      })
      this.dialogVisible = false
    },
    readOneContent () {},
    copyOneContent () {},
    approvalBill () {
      this.$http({
        url: '/api-cm/accept/acceptOrder/vendorPass',
        method: 'POST',
        data: {
          acceptOrderId: this.acceptOrder.acceptOrderId
        },
        loading: true
      })
        .then(data => {
          this.$message({
            message: '操作成功',
            type: 'success'
          })
          this.backBill()
        })
        .catch(err => {
          console.log(err)
        })
    },
    addUploadOneApproval () {
      this.assetFile.push({
        fileuploadId: null,
        fileSourceName: '',
        fileFunction: 'acceptASSET' // 附件
      })
    },
    addUploadOneFile () {
      this.techFile.push({
        fileuploadId: null,
        fileSourceName: '',
        fileFunction: 'acceptTECH' // 技术附件
      })
    },
    addOneEquipment () {
      this.toolEqp.push({
        toolEqpId: null,
        toolEqpName: '',
        eqpSpecification: '',
        quantity: '',
        unit: '',
        remark: ''
      })
    },
    handleDelEqipmentClick (index, row) {
      this.toolEqp.splice(index, 1)
    },
    handleDelFileClick (index, row) {
      this.assetFile.splice(index, 1)
    },
    handleDelClick (index, row) {
      this.assetFile.splice(index, 1)
    },
    rejectBill () {
      this.$prompt('请输入驳回原因', '驳回原因', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(({ value }) => {
          this.$http({
        url: '/api-cm/accept/acceptOrder/vendorReject',
        method: 'POST',
        data: {
          acceptOrderId: this.acceptOrder.acceptOrderId,
          rejectReason: value
        },
        loading: true
      }).then(data => {
          this.$message({
            message: '操作成功',
            type: 'success'
          })
          this.backBill()
        })
        .catch(err => {
          console.log(err)
        })
        })
        .catch(() => {})
    },
    saveBill (type) {
      this.$refs.acceptOrder.validate(valid => {
        if (valid) {
          // -----
          if (this.acceptDetails.length === 0) {
            this.$message.info('请输入验收明细!')
            return
          }
          let allparam = {
            'acceptOrder': this.acceptOrder,
            'acceptDetails': this.acceptDetails,
            'techFile': this.techFile,
            'assetFile': this.assetFile,
            'toolEqp': this.toolEqp
          }
          this.$http({
            url: '/api-cm/accept/acceptOrder/buyerSaveTemporary',
            method: 'POST',
            data: allparam,
            loading: true
          })
            .then(data => {
              this.$message({
                message: '保存成功',
                type: 'success'
              })
              this.backBill()
            })
            .catch(err => {
              console.log(err)
            })
        } else {
          return false
        }
      })
    }
  }
}
</script>
<style scoped lang="scss">
.the-inspectionApplyBillDetail-detail {
  .form-container2 {
    padding: 5px;
  }
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
  .btn_line{margin:0;}
}
</style>
