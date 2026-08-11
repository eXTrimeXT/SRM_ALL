<template>
  <el-container
    class="flex-container the-contractMaintainDetail-detail"
    direction="vertical"
  >
    <el-main>
      <div class="form-container2">
        <el-form
          ref="contractHead"
          :model="contractHead"
          label-width="80px"
          label-position="top"
          class="form-fill-style"
          :rules="rules"
        >
          <el-collapse
            v-model="activeDims"
            class="tab-form-style"
          >
            <el-collapse-item
              ref="createContract"
              :title="$t('contractMod.createContract')"
              name="1"
            >
              <el-row :gutter="32">
                <el-col :span="6">
                  <el-form-item
                    :label="$t('contractMod.contractType')"
                    :label-width="formLabelWidth"
                    prop="contractType"
                  >
                    <dict-select
                      v-model="contractHead.contractType"
                      code="CONTRACT_TYPE"
                      :disabled="!isMIAN_CONTRACT_ADD && !!contractHead.contractHeadId"
                      @change="setContractType"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('contractMod.templHeadId')"
                    :label-width="formLabelWidth"
                    prop="templHeadId"
                  >
                    <el-select
                      v-model="contractHead.templHeadId"
                      :disabled="!isMIAN_CONTRACT_ADD"
                      @change="settemplate"
                    >
                      <el-option
                        v-for="item in templTypeList"
                        :key="item.templHeadId"
                        :label="item.templName"
                        :value="item.templHeadId"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('contractMod.contractNo')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="contractHead.contractNo"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('contractMod.contractName')"
                    :label-width="formLabelWidth"
                    prop="contractName"
                  >
                    <el-input
                      v-model="contractHead.contractName"
                      :disabled="!isMIAN_CONTRACT_ADD"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('contractMod.sourceNumber')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="contractHead.sourceNumber"
                      :disabled="!isMIAN_CONTRACT_ADD"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    v-if="!isMIAN_CONTRACT_ADD"
                    :label="$t('contractMod.contractHeadId')"
                    :label-width="formLabelWidth"
                  >
                    <quick-search
                      :disabled="
                        isMIAN_CONTRACT_ADD ||
                          (!isMIAN_CONTRACT_ADD && !!contractHead.contractHeadId)
                      "
                      :pre-query-data="{
                        't.contract_Type': 'MIAN_CONTRACT_ADD',
                        't.contract_Status': 'ARCHIVED'
                      }"
                      :show-input="contractHead.mainContractNo"
                      show-key="contractNo"
                      :scope-data="contractHead"
                      name="scc_contract_head"
                      @close-quicksearch="getcontractObj"
                    />
                  </el-form-item>
                  <p v-else />
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('contractMod.fullPathId')"
                    :label-width="formLabelWidth"
                    prop="organizationId"
                  >
                    <organization-select-tree
                      v-model="contractHead.organizationId"
                      :disabled="!isMIAN_CONTRACT_ADD"
                      :scope="contractHead"
                      @select="addOrgHandle"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('contractMod.vendorName')"
                    :label-width="formLabelWidth"
                    prop="vendorId"
                  >
                    <el-select
                      v-model="contractHead.vendorId"
                      :disabled="!isMIAN_CONTRACT_ADD"
                      filterable
                      remote
                      :placeholder="$t('contractMod.enterKeywords')"
                      :remote-method="remoteMethod"
                      clearable
                      automatic-dropdown
                      @change="elSelectChange"
                    >
                      <el-option
                        v-for="item in options"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('contractMod.vendorCode')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="contractHead.vendorCode"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('contractMod.taxRate')"
                    :label-width="formLabelWidth"
                  >
                    <dict-select
                      v-model="contractHead.taxKey"
                      disabled
                      code="tax"
                      @change-value="taxHandler"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('contractMod.excludeTaxAmount')"
                    :label-width="formLabelWidth"
                    prop="excludeTaxAmount"
                  >
                    <el-input
                      v-model="contractHead.excludeTaxAmount"
                      v-input-format="{ type: 'number' }"
                      :disabled="!isMIAN_CONTRACT_ADD && isSUPPLEMENTAL_AGREEMENT"
                      @input="setEveryExcludeTaxPayAmount(contractHead.excludeTaxAmount)"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('contractMod.currencyCode')"
                    :label-width="formLabelWidth"
                  >
                    <dict-select
                      v-model="contractHead.currencyCode"
                      code="currency"
                      @change-value="currencyHandler"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('contractMod.engineerStartDate')"
                    :label-width="formLabelWidth"
                  >
                    <el-date-picker
                      v-model="contractHead.engineerStartDate"
                      type="date"
                      :disabled="!isMIAN_CONTRACT_ADD && isSUPPLEMENTAL_AGREEMENT"
                      format="yyyy-MM-dd"
                      value-format="yyyy-MM-dd"
                      @change="getEngineerDays"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('contractMod.engineerEndDate')"
                    :label-width="formLabelWidth"
                  >
                    <el-date-picker
                      v-model="contractHead.engineerEndDate"
                      type="date"
                      :disabled="!isMIAN_CONTRACT_ADD && isSUPPLEMENTAL_AGREEMENT"
                      format="yyyy-MM-dd"
                      value-format="yyyy-MM-dd"
                      @change="getEngineerDays"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('contractMod.engineerDays')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="contractHead.engineerDays"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('contractMod.contractRemark')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="contractHead.contractRemark"
                      :disabled="!isMIAN_CONTRACT_ADD && isSUPPLEMENTAL_AGREEMENT"
                      type="textarea"
                      :rows="2"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('contractMod.createdBy')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="contractHead.createdUserName"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('contractMod.approvalAdvice')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="contractHead.approvalAdvice"
                      disabled
                    />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-collapse-item>
            <el-collapse-item
              ref="paymentPlan"
              :title="$t('contractMod.paymentPlan')"
              name="2"
            >
              <p class="btn_line">
                <el-button
                  type="primary"
                  :disabled="!isMIAN_CONTRACT_ADD && isSUPPLEMENTAL_AGREEMENT"
                  class="detail-pbtn"
                  @click="addOnepayPlansList"
                >
                  {{ $t('common.add') }}
                </el-button>
              </p>
              <el-table
                :data="payPlans"
                style="width: 100%"
                border
                max-height="250px"
              >
                <el-table-column
                  align="center"
                  prop="milestoneStage"
                  :label="$t('contractMod.milestoneStage')"
                  width="100"
                />
                <el-table-column
                  align="center"
                  prop="milestone"
                  :label="$t('contractMod.milestone')"
                  width="100"
                >
                  <template slot="header">
                    <i class="toRequired">*</i>{{ $t('contractMod.milestone') }}
                  </template>
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.milestone"
                      :disabled="!isMIAN_CONTRACT_ADD && isSUPPLEMENTAL_AGREEMENT"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="milestoneExplain"
                  :label="$t('contractMod.milestoneExplain')"
                  width="100"
                >
                  <template slot="header">
                    <i class="toRequired">*</i>{{ $t('contractMod.milestoneExplain') }}
                  </template>
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.milestoneExplain"
                      :disabled="!isMIAN_CONTRACT_ADD && isSUPPLEMENTAL_AGREEMENT"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="milestoneDate"
                  :label="$t('contractMod.milestoneDate')"
                  width="160"
                >
                  <template slot="header">
                    <i class="toRequired">*</i>{{ $t('contractMod.milestoneDate') }}
                  </template>
                  <template slot-scope="scope">
                    <el-date-picker
                      v-model="scope.row.milestoneDate"
                      type="date"
                      format="yyyy-MM-dd"
                      value-format="yyyy-MM-dd"
                      :disabled="!isMIAN_CONTRACT_ADD && isSUPPLEMENTAL_AGREEMENT"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="milestoneStatus"
                  :label="$t('contractMod.milestoneStatus')"
                  width="100"
                >
                  <template slot-scope="scope">
                    <dict-select
                      v-model="scope.row.milestoneStatus"
                      code="MILESTONE_STATUS"
                      disabled
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="payType"
                  :label="$t('contractMod.payType')"
                  width="150"
                >
                  <template slot-scope="scope">
                    <quick-search
                      :pre-query-data="{ 't.PAY_TYPE_STATUS': 'EFFECTIVE' }"
                      :show-input="scope.row.payType"
                      show-key="payType"
                      :scope-data="scope.row"
                      name="scc_contract_templ_pay_type_display"
                      :disabled="!isMIAN_CONTRACT_ADD && isSUPPLEMENTAL_AGREEMENT"
                      @close-quicksearch="getPaymentTypeObj"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="payStage"
                  :label="$t('contractMod.payStage')"
                  width="80"
                />
                <el-table-column
                  align="center"
                  prop="payExplain"
                  :label="$t('contractMod.payExplain')"
                  width="150"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="payStatus"
                  :label="$t('contractMod.payStatus')"
                  width="150"
                >
                  <template slot-scope="scope">
                    <el-select
                      v-model="scope.row.payStatus"
                      :disabled="!isMIAN_CONTRACT_ADD && isSUPPLEMENTAL_AGREEMENT"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="payRatio"
                  :label="$t('contractMod.payRatio')"
                  width="120"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.payRatio"
                      v-input-format="{ type: 'number' }"
                      :disabled="!isMIAN_CONTRACT_ADD && isSUPPLEMENTAL_AGREEMENT"
                      @input="setExcludeTaxPayAmount(scope.row)"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="payMethod"
                  :label="$t('contractMod.paymentMethod')"
                  width="150"
                >
                  <template slot-scope="scope">
                    <dict-select
                      v-model="scope.row.payMethod"
                      code="PAYMENT_MODE"
                      :disabled="!isMIAN_CONTRACT_ADD && isSUPPLEMENTAL_AGREEMENT"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="paidAmount"
                  :label="$t('contractMod.paidAmount')"
                  width="100"
                />
                <el-table-column
                  align="center"
                  prop="excludeTaxPayAmount"
                  :label="$t('contractMod.excludeTaxPayAmount')"
                  width="100"
                />
                <el-table-column
                  :label="$t('common.operation')"
                  width="60"
                  fixed="right"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      :disabled="!isMIAN_CONTRACT_ADD && isSUPPLEMENTAL_AGREEMENT"
                      @click="deleteOnePaymentPlan(scope.$index, scope.row)"
                    >
                      {{ $t('common.delete') }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <el-collapse-item
              ref="contractItem"
              :title="$t('contractMod.contractItemList')"
              name="3"
            >
              <p class="btn_line">
                <el-button
                  type="primary"
                  class="detail-pbtn"
                  :disabled="!isMIAN_CONTRACT_ADD && isSUPPLEMENTAL_AGREEMENT"
                  @click="addOneContractItem"
                >
                  {{ $t('common.add') }}
                </el-button>
              </p>
              <el-table
                :data="contractMaterials"
                style="width: 100%"
                border
                max-height="250px"
              >
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('contractMod.contractindex')"
                  width="80"
                />
                <el-table-column
                  align="center"
                  prop="sourceNumber"
                  :label="$t('contractMod.sourceNumber')"
                  width="150"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.sourceNumber"
                      :disabled="!isMIAN_CONTRACT_ADD && isSUPPLEMENTAL_AGREEMENT"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="sourceLineNumber"
                  :label="$t('contractMod.sourceLineNumber')"
                  width="150"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.sourceLineNumber"
                      v-input-format="{ type: 'number' }"
                      :disabled="!isMIAN_CONTRACT_ADD && isSUPPLEMENTAL_AGREEMENT"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="materialCode"
                  :label="$t('contractMod.materialCode')"
                  min-width="150"
                >
                  <template slot="header">
                    <i class="toRequired">*</i>{{ $t('contractMod.materialCode') }}
                  </template>
                  <template slot-scope="scope">
                    <quick-search
                      :show-input="scope.row.materialCode"
                      show-key="materialCode"
                      :scope-data="scope.row"
                      name="scc_base_material_item_display"
                      :disabled="!isMIAN_CONTRACT_ADD && isSUPPLEMENTAL_AGREEMENT"
                      @close-quicksearch="getItemObj"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="materialName"
                  :label="$t('contractMod.materialName')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="categoryName"
                  :label="$t('contractMod.categoryName')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="amount"
                  :label="$t('contractMod.amount2')"
                  width="120"
                />
                <el-table-column
                  align="center"
                  prop="contractQuantity"
                  :label="$t('contractMod.contractQuantity')"
                  width="100"
                >
                  <template slot="header">
                    <i class="toRequired">*</i>{{ $t('contractMod.contractQuantity') }}
                  </template>
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.contractQuantity"
                      v-input-format="{ type: 'number' }"
                      :disabled="!isMIAN_CONTRACT_ADD && isSUPPLEMENTAL_AGREEMENT"
                      @input="calcAmount(scope)"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="untaxedPrice"
                  :label="$t('contractMod.untaxedPrice')"
                  width="100"
                >
                  <template slot="header">
                    <i class="toRequired">*</i>{{ $t('contractMod.untaxedPrice') }}
                  </template>
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.untaxedPrice"
                      v-input-format="{ type: 'number' }"
                      :disabled="!isMIAN_CONTRACT_ADD && isSUPPLEMENTAL_AGREEMENT"
                      @input="calcAmount(scope)"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="unitName"
                  :label="$t('contractMod.unit')"
                  width="60"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  :label="$t('common.operation')"
                  width="60"
                  fixed="right"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      :disabled="!isMIAN_CONTRACT_ADD && isSUPPLEMENTAL_AGREEMENT"
                      @click="deleteOneItem(scope.$index, scope.row)"
                    >
                      {{ $t('common.delete') }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
              <!-- 弹框区域-->
              <srm-dialog
                :title="$t('contractMod.addContractItem')"
                size="large"
                :visible.sync="dialogVisible"
                :close-on-click-modal="false"
              >
                <el-table
                  :data="displayItemTable"
                  style="width: 100%"
                  border
                  height="333px"
                  highlight-current-row
                  @selection-change="handleItemSelection"
                >
                  <el-table-column
                    align="center"
                    type="index"
                    width="40"
                  />
                  <el-table-column
                    type="selection"
                    width="55"
                  />
                  <el-table-column
                    align="center"
                    prop="orderNumber"
                    :label="$t('contractMod.orderNumber')"
                    width="150"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="orderLineNumber"
                    :label="$t('contractMod.orderLineNumber')"
                    width="150"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="sourceNumber"
                    :label="$t('contractMod.sourceNumber')"
                    width="150"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="sourceLineNumber"
                    :label="$t('contractMod.sourceLineNumber')"
                    width="150"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="materialCode"
                    :label="$t('contractMod.materialCode')"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="materialName"
                    :label="$t('contractMod.materialName')"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="specification"
                    :label="$t('contractMod.specification')"
                    width="100"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="amount"
                    :label="$t('contractMod.amount')"
                    width="100"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="contractQuantity"
                    :label="$t('contractMod.contractQuantity')"
                    width="100"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="untaxedPrice"
                    :label="$t('contractMod.untaxedPrice')"
                    width="100"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="peoplePrice"
                    :label="$t('contractMod.peoplePrice')"
                    width="100"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="materialPrice"
                    :label="$t('contractMod.materialPrice')"
                    width="100"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="taxRate"
                    :label="$t('contractMod.taxRate')"
                    width="110"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="unitName"
                    :label="$t('contractMod.unit')"
                    width="60"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="deliveryDate"
                    :label="$t('contractMod.deliveryDate')"
                    width="100"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="receptionFlag"
                    :label="$t('contractMod.receptionFlag')"
                    width="100"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="receptionQuantity"
                    :label="$t('contractMod.receptionQuantity')"
                    width="100"
                    :show-overflow-tooltip="true"
                  />
                </el-table>
                <div
                  slot="footer"
                  class="dialog-footer"
                >
                  <el-button @click="dialogVisible = false">
                    {{ $t('common.cancel') }}
                  </el-button>
                  <el-button
                    type="primary"
                    @click="addBySourceNum"
                  >
                    {{
                      $t('common.confirm')
                    }}
                  </el-button>
                </div>
              </srm-dialog>
            </el-collapse-item>
            <el-collapse-item
              ref="contractFile"
              :title="$t('contractMod.addContractFile')"
              name="4"
            >
              <p class="btn_line">
                <el-button
                  type="primary"
                  class="detail-pbtn"
                  @click="addUploadOne"
                >
                  {{
                    $t('common.new')
                  }}
                </el-button>
              </p>
              <el-table
                :data="fileuploads"
                style="width: 100%"
                border
                max-height="250px"
              >
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('contractMod.tabindex')"
                  width="50"
                />
                <el-table-column
                  align="center"
                  prop="fileSourceName"
                  :label="$t('contractMod.fileSourceName')"
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
                      @on-change="({file}) => outerHandleUploadSuccess(file,scope.row,'file')"
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
                      {{
                        $t('common.delete')
                      }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <el-collapse-item
              ref="contractOtherInfo"
              :title="$t('contractMod.otherContractInfo')"
              name="5"
            >
              <el-row type="flex">
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.contractStatus')"
                    :label-width="formLabelWidth"
                  >
                    <dict-select
                      v-model="contractHead.contractStatus"
                      code="CONTRACT_STATUS"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.vendorConfirmDate')"
                    :label-width="formLabelWidth"
                  >
                    <el-date-picker
                      v-model="contractHead.vendorConfirmDate"
                      disabled
                      type="date"
                      format="yyyy-MM-dd"
                      value-format="yyyy-MM-dd"
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.vendorConfirmBy')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="contractHead.vendorConfirmBy"
                      disabled
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row type="flex">
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.paidSumAmount')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="contractHead.paidSumAmount"
                      v-input-format="{ type: 'number' }"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.unpaidAmount')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="contractHead.unpaidAmount"
                      v-input-format="{ type: 'number' }"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col><p /></el-col>
              </el-row>
              <el-row type="flex">
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.vendorRemark')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="contractHead.vendorRemark"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.vendorRejectReason')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="contractHead.vendorRejectReason"
                      disabled
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row type="flex">
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.contractSendType')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="contractHead.contractSendType"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.logisticsNo')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="contractHead.logisticsNo"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.arrivalTime')"
                    :label-width="formLabelWidth"
                  >
                    <el-date-picker
                      v-model="contractHead.arrivalTime"
                      disabled
                      type="date"
                      format="yyyy-MM-dd"
                      value-format="yyyy-MM-dd"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row type="flex">
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.warrantyStartDate')"
                    :label-width="formLabelWidth"
                  >
                    <el-date-picker
                      v-model="contractHead.warrantyStartDate"
                      disabled
                      type="date"
                      format="yyyy-MM-dd"
                      value-format="yyyy-MM-dd"
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.warrantyEndDate')"
                    :label-width="formLabelWidth"
                  >
                    <el-date-picker
                      v-model="contractHead.warrantyEndDate"
                      disabled
                      type="date"
                      format="yyyy-MM-dd"
                      value-format="yyyy-MM-dd"
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.receivedBy')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="contractHead.receivedBy"
                      disabled
                    />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-collapse-item>
            <el-collapse-item
              ref="contractContent"
              :title="$t('contractMod.contractContent')"
              name="6"
            >
              <el-row type="flex">
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.owner')"
                    :label-width="formLabelWidth"
                    prop="owner"
                  >
                    <el-input v-model="contractHead.owner" />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.fax')"
                    :label-width="formLabelWidth"
                    prop="fax"
                  >
                    <el-input v-model="contractHead.fax" />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.phone')"
                    :label-width="formLabelWidth"
                    prop="phone"
                  >
                    <el-input v-model="contractHead.phone" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row type="flex">
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.signingSite')"
                    :label-width="formLabelWidth"
                    prop="signingSite"
                  >
                    <el-input v-model="contractHead.signingSite" />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.postcode')"
                    :label-width="formLabelWidth"
                    prop="postcode"
                  >
                    <el-input v-model="contractHead.postcode" />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.openingBank')"
                    :label-width="formLabelWidth"
                    prop="openingBank"
                  >
                    <el-input v-model="contractHead.openingBank" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row type="flex">
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.bankAccount')"
                    :label-width="formLabelWidth"
                    prop="bankAccount"
                  >
                    <el-input v-model="contractHead.bankAccount" />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.legalPerson')"
                    :label-width="formLabelWidth"
                    prop="legalPerson"
                  >
                    <el-input v-model="contractHead.legalPerson" />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.entrustedAgent')"
                    :label-width="formLabelWidth"
                    prop="entrustedAgent"
                  >
                    <el-input v-model="contractHead.entrustedAgent" />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-collapse-item>
            <div>
              <el-table
                :data="contractLines"
                style="width: 100%"
                border
                max-height="2000px"
              >
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('contractMod.tabindex')"
                  width="60"
                />
                <el-table-column
                  align="center"
                  prop="contractItem"
                  :label="$t('contractMod.contractItem')"
                  width="150"
                />
                <el-table-column
                  align="center"
                  prop="contractContent"
                  :label="$t('contractMod.contractContent')"
                  min-width="250"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.contractContent"
                      :disabled="scope.row.isModify === 'N'"
                      type="textarea"
                      :rows="2"
                      @change="setDiffTextHignLight(scope.row, scope.$index)"
                    />
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <el-collapse-item
              ref="contractImpleRecord"
              :title="$t('contractMod.contractImpleRecord')"
              name="7"
            >
              <el-table
                :data="contractContentList"
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
                  prop="contractContent"
                  :label="$t('contractMod.contractContentStage')"
                  width="150"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.contractContent" />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="contractContent"
                  :label="$t('contractMod.projectPlan')"
                  width="150"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.contractContent" />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="contractContent"
                  :label="$t('contractMod.contractDesc')"
                  min-width="200"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.contractContent" />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="fileName"
                  :label="$t('contractMod.addUploadFile')"
                  width="150"
                >
                  <template slot-scope="scope">
                    <SrmCommonFile
                      :extra-data="fileInfo"
                      :default-file="{
                        fileId: scope.row.docId,
                        fileName: scope.row.fileName
                      }"
                      :readonly="false"
                      @on-change="({file}) => outerHandleUploadSuccess(file,scope.row,'doc')"
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
                      @click="deleteOneContent(scope.$index, scope.row)"
                    >
                      {{
                        $t('common.delete')
                      }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <el-collapse-item
              ref="contentCompare"
              :title="$t('contractMod.contentCompare')"
              name="9"
            >
              <ul class="the_compare_top_header">
                <li>{{ $t('contractMod.oldtemplContent') }}</li>
                <li>
                  {{ $t('contractMod.newtemplContent')
                  }}<span style="color: red; padding-left: 11px">{{ $t('contractMod.greenIsAddRedIsDelete') }}</span>
                </li>
              </ul>
              <ul class="compareTable">
                <li
                  v-for="(val, key) in contractLines"
                  :key="key"
                >
                  <div class="el-col el-col-12 the_left_div">
                    {{ val.templContent }}
                  </div>
                  <div class="el-col el-col-12 the_right_div">
                    {{ val.templContent }}
                  </div>
                </li>
              </ul>
              <!-- <pre id="display"></pre> -->
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </div>
      <c-fill-progress
        node-name="合同节点"
        :data="nodeData"
        :percentage="false"
        @index-click="indexClickTo"
      />
      <c-toolbar>
        <template slot="right">
          <el-button
            type="primary"
            @click="saveBill"
          >
            {{
              $t('common.submit')
            }}
          </el-button>
          <el-button
            type="primary"
            :disabled="!isDisabled"
            @click="submitApprovalBill"
          >
            {{ $t('contractMod.submitApproval') }}
          </el-button>
          <el-button
            v-if="contractHead.contractStatus === 'SUPPLIER_CONFIRMING'"
            type="primary"
            :disabled="!isDisabled"
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
import CFillProgress from 'lib@/components/c-fill-progress'
import { tabTodoMixin } from '@/utils/mixins'
import QuickSearch from 'lib@/components/QuickSearch'
import MainHeader from 'lib@/components/Table/MainHeader'
import OrganizationSelectTree from 'lib@/components/organization-selector'
import DictSelect from '@/library/components/c-select/dict-select'

export default {
  name: 'ContractMaintainDetail',
  components: {
    MainHeader,
    CToolbar,
    CFillProgress,
    QuickSearch,
    OrganizationSelectTree,
    DictSelect
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'vendorBiddingManagement',
        fileType: 'images'
      },
      nodeData: [
        // 进度条节点信息
        { code: 'createContract', name: this.$t('contractMod.createContract') },
        { code: 'paymentPlan', name: this.$t('contractMod.paymentPlan') },
        { code: 'contractItem', name: this.$t('contractMod.contractItemList') },
        { code: 'contractFile', name: this.$t('contractMod.addContractFile') },
        { code: 'contractOtherInfo', name: this.$t('contractMod.otherContractInfo') },
        { code: 'contractContent', name: this.$t('contractMod.contractContent2') },
        { code: 'contractImpleRecord', name: this.$t('contractMod.contractImpleRecord') },
        { code: 'contentCompare', name: this.$t('contractMod.contentCompare') }
      ],
      options: [],
      contractHead: {
        contractHeadId: null,
        contractType: '',
        templHeadId: null,
        templName: '',
        contractName: '',
        sourceNumber: '',
        mainContractNo: '',
        vendorId: '',
        vendorCode: '',
        vendorName: '',
        currencyId: '',
        currencyCode: '',
        currencyName: '',
        excludeTaxAmount: '',
        organizationId: '',
        organizationCode: '',
        organizationName: '',
        engineerStartDate: '',
        engineerEndDate: '',
        engineerDays: '',
        contractRemark: '',
        owner: '',
        fax: '',
        phone: '',
        signingSite: '',
        postcode: '',
        openingBank: '',
        bankAccount: '',
        legalPerson: '',
        entrustedAgent: '',
        secondFax: '',
        secondPhone: '',
        secondSite: '',
        secondPostCode: '',
        secondOpeningBank: '',
        secondLegalPerson: '',
        secondEntrustedAgent: '',
        secondSignDate: '',
        buyerName: '',
        contractStatus: '',
        orderNumber: '',
        vendorConfirmDate: '',
        paidSumAmount: '',
        unpaidAmount: '',
        vendorConfirmBy: '',
        vendorRemark: '',
        vendorRejectReason: '',
        contractSendType: '',
        logisticsNo: '',
        arrivalTime: '',
        receivedBy: '',
        warrantyStartDate: '',
        warrantyEndDate: '',
        vendorFileName: '',
        approvalAdvice: '',
        taxKey: '',
        taxRate: ''
      },
      dialogVisible: false,
      displayItemTable: [],
      multipleItemSelection: [],
      fileuploads: [],
      contractHistoryList: [],
      approvalHistoryList: [],
      contractItemsCompareList: [],
      contractMaterials: [],
      payPlans: [],
      contractContentList: [],
      activeDims: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10'],
      rules: {
        templHeadId: [{ required: true, message: this.$t('contractMod.plsChooseContractTemplate') }],
        templateType: [{ required: true, message: this.$t('contractMod.plsChooseTemplateType') }],
        contractType: [{ required: true, message: this.$t('contractMod.plsChooseContractType') }],
        contractName: [{ required: true, message: this.$t('contractMod.plsChooseContractName') }],
        vendorCode: [{ required: true, message: this.$t('contractMod.plsEnterSupplierCode') }],
        owner: [{ required: true, message: this.$t('contractMod.plsEnterFirstParty') }],
        fax: [{ required: true, message: this.$t('contractMod.plsEnterFax') }],
        phone: [{ required: true, message: this.$t('contractMod.plsEnterPhone') }],
        signingSite: [{ required: true, message: this.$t('contractMod.plsEnterContractingPlace') }],
        postcode: [{ required: true, message: this.$t('contractMod.plsEnterZipCode') }],
        mainContractNo: [{ required: true, message: '' }],
        excludeTaxAmount: [{ required: true, message: this.$t('contractMod.plsEnterTotalAmountExcludingTax') }],
        fullPathId: [{ required: true, message: this.$t('contractMod.plsEnterProcurementOrg') }],
        secondPostCode: [{ required: true, message: this.$t('contractMod.plsEnterPostalCodeOfPartyB') }],
        openingBank: [{ required: true, message: this.$t('contractMod.msgOpeningBank') }],
        bankAccount: [{ required: true, message: this.$t('contractMod.enterAccount') }],
        legalPerson: [{ required: true, message: this.$t('contractMod.plsEnterLegalRepresentative') }],
        entrustedAgent: [{ required: true, message: this.$t('contractMod.plsEnterAgent') }],
        secondEntrustedAgent: [{ required: true, message: this.$t('contractMod.plsEnterEntrustedAgentOfPartyB') }],
        secondSignDate: [{ required: true, message: this.$t('contractMod.plsEnterSigningPeriodOfPartyB') }]
      },
      isMIAN_CONTRACT_ADD: true, // 主合同新增
      isSUPPLEMENTAL_AGREEMENT: false, // 补充协议
      isMIAN_CONTRACT_ALTER: false, // 主合同变更
      isDisabled: this.$attrs.params.flag == 'edit',
      formLabelWidth: '120px',
      isModify: false,
      templTypeList: [],
      paymentTypeList: [],
      initContractLines: [],
      contractLines: [],
      curRole: this.$store.getters.userType
    }
  },
  created () {
    if (this.$attrs.params.flag == 'edit') {
      this.getFormDetail()
    }
    this.getTemplTypeList()
    this.getpaymentTypeList()
  },
  methods: {
    taxHandler (value, dictItem) {
      this.contractHead.taxRate = dictItem.key
    },
    getpaymentTypeList () {
      this.$http({
        url: '/api-cm/template/payType/listPageByParm',
        method: 'POST',
        data: {},
        loading: true
      })
        .then((data) => {
          this.paymentTypeList = data.data.list
        })
        .catch((err) => {
          console.log(err)
        })
    },
    setExcludeTaxPayAmount (row) {
      if (row.payRatio && this.contractHead.excludeTaxAmount) {
        row.excludeTaxPayAmount = (this.contractHead.excludeTaxAmount * row.payRatio) / 100
      } else {
        row.excludeTaxPayAmount = ''
      }
    },
    setEveryExcludeTaxPayAmount (val) {
      if (this.payPlans.length !== 0 && val) {
        for (let i of this.payPlans) {
          if (i.payRatio) {
            i.excludeTaxPayAmount = (val * i.payRatio) / 100
          } else {
            i.excludeTaxPayAmount = ''
          }
        }
      }
    },
    getPaymentTypeObj (val, scope) {
      scope.payTypeId = val ? val.payTypeId : ''
      scope.payType = val ? val.payType : ''
      scope.payExplain = val ? val.payExplain : ''
    },
    setRowpayType (row) {
      let currentRow = this.paymentTypeList.filter((v) => v.payTypeId === row.payTypeId)[0]
      row.payExplain = currentRow.payExplain
      row.delayedDays = currentRow.payDelaytime
      row.logicalExplain = currentRow.logicalExplain
      row.payType = this.paymentTypeList.filter((v) => v.payTypeId === row.payTypeId)[0].payType
    },
    currencyHandler (value, dictItem) {
      console.log('dict:::', dictItem)
      this.contractHead.currencyId = dictItem.id
      this.contractHead.currencyName = dictItem.label
    },
    getTemplTypeList () {
      this.$http({
        url: '/api-cm/contract/contractHead/listEffectiveTempl',
        method: 'POST',
        data: {},
        loading: true
      })
        .then((data) => {
          this.templTypeList = data.data
        })
        .catch((err) => {
          console.log(err)
        })
    },
    setContractType (val) {
      // 当合同类型为合同新增，主/原合同编号不允许输入
      this.isMIAN_CONTRACT_ADD = val === 'MIAN_CONTRACT_ADD'
      this.isSUPPLEMENTAL_AGREEMENT = val === 'SUPPLEMENTAL_AGREEMENT'
      this.isMIAN_CONTRACT_ALTER = val === 'MIAN_CONTRACT_ALTER'
      if (!this.isMIAN_CONTRACT_ADD && !this.contractHead.contractHeadId) {
        this.$message.info(this.$t('contractMod.plsEnterTheMainOrOriginalContractNumberFirst'))
      }
    },
    settemplate (val) {
      this.contractHead.templName = this.templTypeList.filter(
        (v) => v.templHeadId === val
      )[0].templName
      this.$http({
        url: '/api-cm/template/templHead/getContractTemplDTO',
        method: 'GET',
        params: { templHeadId: val },
        loading: true
      })
        .then((data) => {
          if (data.data && data.data.templLines) {
            this.fileuploads = data.data.fileuploads || []
            this.initContractLines = data.data.templLines.filter((x) => x.isDisplayed === 'Y') || []
            this.contractLines = data.data.templLines
              .filter((x) => x.isDisplayed === 'Y')
              .map((v) => {
                return {
                  contractItem: v['contractItem'],
                  templContent: v['templContent'],
                  contractContent: v['templContent'],
                  isDisplayed: v['isDisplayed'] || 'N',
                  isModify: v['isModify'] || 'N'
                }
              })
          }
        })
        .catch((err) => {
          console.log(err)
        })
    },
    setDiffTextHignLight (rowData, index) {
      let color = ''
        let span = null
      let diff = Diff.diffChars(rowData.templContent, rowData.contractContent)
        let the_right_div = document.querySelector('.compareTable').querySelectorAll('.the_right_div')[
          index
        ]
        let fragment = document.createDocumentFragment()
      diff.forEach(function (part) {
        // green for additions, red for deletions
        // grey for common parts
        color = part.added ? 'green' : part.removed ? 'red' : '#aaa'
        span = document.createElement('span')
        span.style.color = color
        span.appendChild(document.createTextNode(part.value))
        fragment.appendChild(span)
      })
      the_right_div.innerHTML = ''
      the_right_div.appendChild(fragment)
    },
    getFormDetail () {
      this.$http({
        url: '/api-cm/contract/contractHead/getContractDTO',
        method: 'GET',
        params: { contractHeadId: this.$attrs.params.row.contractHeadId },
        loading: true
      })
        .then((data) => {
          if (data && data.data) {
            this.contractHead = data.data.contractHead
            this.contractLines = data.data.contractLines
            this.contractMaterials = data.data.contractMaterials
            this.fileuploads = data.data.fileuploads
            this.payPlans = data.data.payPlans
            this.setContractType(data.data.contractHead.contractType)
            if (data.data.contractHead) {
              this.queryCompanyList({
                orgId: data.data.contractHead.organizationId
              })
            }
          }
        })
        .catch((err) => {
          console.log(err)
        })
    },
    getEngineerDays () {
      if (
        this.contractHead.engineerStartDate &&
        this.contractHead.engineerEndDate &&
        this.contractHead.engineerStartDate < this.contractHead.engineerEndDate
      ) {
        this.contractHead.engineerDays =
          (new Date(this.contractHead.engineerEndDate) -
            new Date(this.contractHead.engineerStartDate)) /
          (3600000 * 24)
      } else {
        this.contractHead.engineerDays = ''
      }
    },
    // 点击右边进度调跳转到对应的区域
    indexClickTo (code) {
      let anchorEle = this.$refs[code].$el
      if (anchorEle) {
        anchorEle.scrollIntoView(true)
      }
    },
    addUploadOne () {
      this.fileuploads.push({
        fileuploadId: null,
        fileSourceName: ''
      })
    },
    // 基础信息form 上传附件成功
    handleUploadSuccess (file) {
      const { id, name } = file
      this.contractHead.fileuploadId = id.toString()
      this.contractHead.fileName = name
    },
    // 附件删除
    handleAttachmentRemove () {
      this.contractHead.fileuploadId = null
      this.contractHead.fileName = ''
    },
    handleScriptProgress (percent) {},
    outerButtonClick (index) {
      this.bankRowIndex = index
    },
    outerHandleUploadSuccess (file, row, type) {
      const { fileId = '', fileName = '' } = file || {}
      if (type == 'doc') {
        row.docId = fileId.toString()
        row.fileName = fileName
      } else {
        row.fileuploadId = fileId.toString()
        row.fileSourceName = fileName
      }
    },
    // 移除
    outerHandleRemove (docId) {},
    // 删除银行证明文件
    outerHandleAttachmentRemove (row) {
      row.docId = ''
      row.fileName = ''
    },
    handleDelClick (index, row) {
      this.fileuploads.splice(index, 1)
    },
    handleItemSelection (val) {
      this.multipleItemSelection = val
    },
    launchPaymentRequest (row) {
      // 发起付款申请
      this.$http({
        url: '/api-cm/contract/payPlan/startPayApplication',
        method: 'GET',
        params: { payPlanId: row.payPlanId },
        loading: true
      }).then((res) => {
        this.$message({
          type: 'success',
          message: res.message
        })
      })
    },
    deleteOneItem (index, row) {
      if (row.contractMaterialId) {
        this.$http({
          url: '/api-cm/contract/contractMaterial/batchDelete',
          method: 'POST',
          data: [row.contractMaterialId],
          loading: true
        })
          .then((res) => {
            this.$message({
              type: 'success',
              message: res.message
            })
            this.contractMaterials.splice(index, 1)
          })
          .catch((err) => {
            console.log(err)
          })
      } else {
        this.contractMaterials.splice(index, 1)
      }
    },
    deleteOnePaymentPlan (index, row) {
      if (row.payPlanId) {
        this.$http({
          url: '/api-cm/contract/payPlan/batchDelete',
          method: 'POST',
          data: [row.payPlanId],
          loading: true
        })
          .then((data) => {
            this.payPlans.splice(index, 1)
          })
          .catch((err) => {
            console.log(err)
          })
      } else {
        this.payPlans.splice(index, 1)
      }
    },
    deleteOneContent (index, row) {
      this.contractContentList.splice(index, 1)
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
      scope.categoryName = val ? val.categoryName : ''
      scope.unitCode = val ? val.unit : ''
      scope.unitName = val ? val.unitName : ''
    },
    getcontractObj (val, scope) {
      let templContractType = this.contractHead.contractType
      if (val.contractHeadId) {
        this.$http({
          url: '/api-cm/contract/contractHead/getContractDTO',
          method: 'GET',
          params: { contractHeadId: val.contractHeadId },
          loading: true
        })
          .then((data) => {
            if (data && data.data) {
              this.contractHead = data.data.contractHead
              this.contractLines = data.data.contractLines
              this.contractMaterials = data.data.contractMaterials
              this.fileuploads = data.data.fileuploads
              this.payPlans = data.data.payPlans
              // 设置合同ID为null

              this.contractHead.mainContractNo = data.data.contractHead.contractNo
              this.contractHead.contractType = templContractType
              this.contractHead.contractNo = null
              this.contractHead.contractHeadId = null
              console.log('--da--', data.data.contractHead)
            }
          })
          .catch((err) => {
            console.log(err)
          })
      }
    },
    backBill () {
      if (this.$attrs.params.flag == 'edit') {
        this.$emit('tab-remove', 'contractMaintainDetail' + this.$attrs.params.row.contractName)
      } else {
        this.$emit('tab-remove', 'contractMaintainDetail')
      }
      this.__setTabTodo('contractMaintainList.getQuerydata')
    },
    addOneContent () {
      this.contractContentList.push({})
    },
    openDialog () {
      if (!this.contractHead.sourceNumber) {
        this.$message.info(this.$t('contractMod.plsEnterSourceNo'))
        return
      }
      this.$http({
        url: '/api-cm/contract/contractHead/getMaterialsBySourceNumber',
        method: 'GET',
        params: { sourceNumber: this.contractHead.sourceNumber },
        loading: true
      })
        .then((data) => {
          if (data && data.data) {
            this.displayItemTable = data.data
            this.dialogVisible = true
          }
        })
        .catch((err) => {
          console.log(err)
        })
    },
    addBySourceNum () {
      if (this.multipleItemSelection.length === 0) {
        this.$message.info(this.$t('contractMod.plsChooseMaterialData'))
        return
      }
      this.contractMaterials.push(this.multipleItemSelection)
      this.dialogVisible = false
    },
    addOneContractItem () {
      this.contractMaterials.push({
        contractMaterialId: null,
        orderNumber: '',
        orderLineNumber: '',
        sourceNumber: '',
        sourceLineNumber: '',
        materialCode: '',
        materialName: '',
        specification: '',
        categoryName: '',
        amount: '',
        contractQuantity: '',
        orderQuantity: '',
        untaxedPrice: '',
        peoplePrice: '',
        materialPrice: '',
        taxRate: '',
        unitCode: '',
        unitName: '',
        deliveryDate: '',
        fileuploadId: null,
        fileSourceName: ''
      })
    },
    addOnepayPlansList () {
      this.payPlans.push({
        milestone: '',
        milestoneDesc: '',
        milestoneDate: '',
        milestoneStatus: '',
        milestoneStage: this.payPlans.length - -1 || 1,
        payStage: this.payPlans.length - -1 || 1,
        payTypeId: null,
        payType: '',
        payExplain: '',
        payRatio: '',
        deductionRatio: '',
        payMethod: '',
        delayedDays: '',
        excludeTaxPayAmount: '',
        payTax: '',
        currencyId: '',
        currencyCode: '',
        currencyName: '',
        payDate: '',
        logicalExplain: '',
        payStatus: ''
      })
    },
    readOneContent () {},
    copyOneContent () {},
    calcAmount (scope) {
      const { contractQuantity, untaxedPrice } = scope.row
      if (contractQuantity && untaxedPrice) {
        this.$set(
          this.contractMaterials[scope.$index],
          'amount',
          (contractQuantity * untaxedPrice).toFixed(3)
        )
      }
    },
    submitApprovalBill () {
      if (this.payPlans.length === 0) {
        this.$message.info(this.$t('contractMod.plsEnterPaymentPlanInformation'))
        return
      }
      if (this.contractMaterials.length === 0) {
        this.$message.info(this.$t('plsEnterContractMaterialInformation'))
        return
      }
      for (let i of this.payPlans) {
        if (!i.payTypeId) {
          this.$message.info(this.$t('contractMod.plsEnterPaymentType'))
          return
        }
        if (!i.payRatio) {
          this.$message.info(this.$t('contractMod.plsEnterPaymentProportion'))
          return
        }
      }
      let payRatioArr = this.payPlans.map((v) => v.payRatio)
      let payRatioTotal = payRatioArr.reduce((p, n) => Number(p) + Number(n))
      if (payRatioTotal != 100) {
        this.$message.info(this.$t('contractMod.sumOfMultiplePaymentProportions'))
        return
      }
      let allParams = {
        contractHead: this.contractHead,
        fileuploads: this.fileuploads,
        payPlans: this.payPlans,
        contractLines: this.contractLines,
        contractMaterials: this.contractMaterials
      }
      let urlBy = '/api-cm/contract/contractHead/buyerSubmitApproval'
      this.$http({
        url: urlBy,
        method: 'POST',
        data: allParams,
        loading: true
      })
        .then((data) => {
          this.$message.success(this.$t('contractMod.operateSuccessfully'))
          if (this.$attrs.params.flag == 'edit') {
            this.$emit('tab-remove', 'contractMaintainDetail' + this.$attrs.params.row.contractName)
          } else {
            this.$emit('tab-remove', 'contractMaintainDetail')
          }
          this.__setTabTodo('contractMaintainList.getQuerydata')
        })
        .catch((err) => {
          console.log(err)
        })
    },
    rejectBill (row) {
      this.$prompt(this.$t('contractMod.msgRefuseReason'), this.$t('contractMod.refusedReason'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel')
      })
        .then(({ value }) => {
          this.$http({
            url: '/api-cm/contract/contractHead/buyerRefused',
            method: 'POST',
            data: {
              contractHeadId: this.$attrs.params.row.contractHeadId,
              approvalAdvice: value
            },
            loading: true
          })
            .then((data) => {
              this.$message.success(this.$t('contractMod.operateSuccessfully'))
              if (this.$attrs.params.flag == 'edit') {
                this.$emit(
                  'tab-remove',
                  'contractMaintainDetail' + this.$attrs.params.row.contractName
                )
              } else {
                this.$emit('tab-remove', 'contractMaintainDetail')
              }
              this.__setTabTodo('contractMaintainList.getQuerydata')
            })
            .catch((err) => {
              console.log(err)
            })
        })
        .catch(() => {})
    },
    closeBill () {
      if (this.$attrs.params.flag == 'edit') {
        this.$emit('tab-remove', 'contractMaintainDetail' + this.$attrs.params.row.contractName)
      } else {
        this.$emit('tab-remove', 'contractMaintainDetail')
      }
      this.__setTabTodo('contractMaintainList.getQuerydata')
    },
    preReadBill () {},
    saveBill () {
      if (this.payPlans.length === 0) {
        this.$message.info(this.$t('contractMod.plsEnterPaymentPlanInformation'))
        return
      }
      if (this.contractMaterials.length === 0) {
        this.$message.info(this.$t('contractMod.plsEnterContractMaterialInformation'))
        return
      }

      for (let i of this.payPlans) {
        if (!i.payTypeId) {
          this.$message.info(this.$t('contractMod.plsEnterPaymentType'))
          return
        }
        if (!i.payRatio) {
          this.$message.info(this.$t('contractMod.plsEnterPaymentProportion'))
          return
        }
      }
      for (let j of this.contractMaterials) {
        if (!j.contractQuantity) {
          this.$message.info(this.$t('contractMod.plsEnterNumber'))
          return
        }
        if (!j.untaxedPrice) {
          this.$message.info(this.$t('contractMod.plsEnterUnitPriceBeforeTax'))
          return
        }
      }
      let payRatioArr = this.payPlans.map((v) => v.payRatio)
      let payRatioTotal = payRatioArr.reduce((p, n) => Number(p) + Number(n))
      if (payRatioTotal != 100) {
        this.$message.info(this.$t('contractMod.sumOfMultiplePaymentProportions'))
        return
      }
      this.contractMaterials.map((v, k) => (v.lineNumber = k - -1))
      let allParams = {
        contractHead: this.contractHead,
        fileuploads: this.fileuploads,
        payPlans: this.payPlans,
        contractLines: this.contractLines,
        contractMaterials: this.contractMaterials
      }
      this.$refs.contractHead.validate((valid) => {
        if (valid) {
          let urlByRole = '/api-cm/contract/contractHead/buyerSaveOrUpdateContractDTO'
          this.$http({
            url: urlByRole,
            method: 'POST',
            data: allParams,
            loading: true
          })
            .then((data) => {
              if (data) {
                this.$message({
                  message: this.$t('common.successSave'),
                  type: 'success'
                })
                if (this.$attrs.params.flag == 'edit') {
                  this.$emit(
                    'tab-remove',
                    'contractMaintainDetail' + this.$attrs.params.row.contractName
                  )
                } else {
                  this.$emit('tab-remove', 'contractMaintainDetail')
                }
                this.__setTabTodo('contractMaintainList.getQuerydata')
              }
            })
            .catch((err) => {
              console.log(err)
            })
        } else {
          return false
        }
      })
    },
    queryCompanyList ({ keyWord = '', orgId }) {
      this.$api.sup_ce.orderManagement.listPageByOrgIdAndKeyWord({
        keyWord,
        orgId
      }).then((res) => {
        this.options = res.data.map((i) => ({
          id: i.companyCode,
          value: i.companyId,
          label: i.companyName
        }))
      })
    },
    elSelectChange (val) {
      const company = this.options.find((i) => i.value === val)
      const { id, label } = company || {}
      this.contractHead = {
        ...this.contractHead,
        vendorName: label,
        vendorCode: id
      }
      if (val) {
        this.$api.sup_ce.orderManagement.getByCompanyIdAndOrgId({
          orgId: this.contractHead.organizationId,
          companyId: val
        }).then((res) => {
          const { taxRate, clearCurrency } = res.data
          this.contractHead.taxRate = taxRate
          this.contractHead.currencyCode = clearCurrency
        })
      }
    },
    remoteMethod (keyWord) {
      if (!this.contractHead.organizationId) {
        return this.$message({ type: 'warning', message: this.$t('contractMod.selectPurchasingOrganization') })
      }
      this.queryCompanyList({
        keyWord,
        orgId: this.contractHead.organizationId
      })
    },
    // 选择组织2
    addOrgHandle (e, value, scope) {
      scope.organizationId = e ? e.organizationId : ''
      scope.organizationCode = e ? e.organizationCode : ''
      scope.organizationName = e ? e.organizationName : ''
      this.queryCompanyList({ orgId: e.organizationId })
    }
  }
}
</script>
<style scoped lang="scss">
.the-contractMaintainDetail-detail {
  .toRequired {
    color: #ff4949;
    padding-right: 2px;
  }
  .form-container2 {
    padding-left: 5px;
    padding-right: 24%;
  }
  .btn_line {
    margin: 2px;
  }
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
  .the_compare_top_header {
    list-style: none;
    display: flex;
    li {
      text-align: center;
      flex: 1;
    }
  }
  .compareTable {
    list-style: none;
    overflow: hidden;
    padding: 0;
    border-bottom: 1px solid #dfe6ec;
    > li {
      border: 1px solid #dfe6ec;
      border-bottom: none;
      overflow: hidden;
      > div {
        padding: 4px;
      }
      .the_left_div {
        border-right: 1px solid #dfe6ec;
        color: #aaa;
      }
    }
  }
}
</style>
