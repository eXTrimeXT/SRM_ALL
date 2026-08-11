<template>
  <el-container
    class="flex-container the-contractMaintainDetailRead-detail"
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
          disabled
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
                    <DictSelect
                      v-model="contractHead.contractType"
                      code="CONTRACT_TYPE"
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
                    <el-input v-model="contractHead.contractName" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('contractMod.sourceNumber')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="contractHead.sourceNumber" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('contractMod.contractHeadId')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="contractHead.mainContractNo" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('contractMod.vendorCode')"
                    :label-width="formLabelWidth"
                    prop="vendorCode"
                  >
                    <quick-search
                      :show-input="contractHead.vendorCode"
                      show-key="companyCode"
                      :scope-data="contractHead"
                      name="scc_sup_company_info"
                      @close-quicksearch="getVendorObj"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('contractMod.vendorName')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="contractHead.vendorName"
                      disabled
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
                    :label="$t('contractMod.taxRate')"
                    :label-width="formLabelWidth"
                  >
                    <dict-select
                      v-model="contractHead.taxKey"
                      disabled
                      code="tax"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('contractMod.excludeTaxAmount')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="contractHead.excludeTaxAmount"
                      v-input-format="{ type: 'number' }"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('contractMod.fullPathId')"
                    :label-width="formLabelWidth"
                  >
                    <organization-select-tree
                      v-model="contractHead.organizationId"
                      :scope="contractHead"
                      @select="addOrgHandle"
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
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.milestone" />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="milestoneExplain"
                  :label="$t('contractMod.milestoneExplain')"
                  width="100"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.milestoneExplain" />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="milestoneDate"
                  :label="$t('contractMod.milestoneDate')"
                  width="160"
                >
                  <template slot-scope="scope">
                    <el-date-picker
                      v-model="scope.row.milestoneDate"
                      type="date"
                      format="yyyy-MM-dd"
                      value-format="yyyy-MM-dd"
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
                    <DictSelect
                      v-model="scope.row.milestoneStatus"
                      code="MILESTONE_STATUS"
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
                    <el-select
                      v-model="scope.row.payTypeId"
                      @change="setRowpayType(scope.row)"
                    >
                      <el-option
                        v-for="item in paymentTypeList"
                        :key="item.payTypeId"
                        :label="item.payType"
                        :value="item.payTypeId"
                      />
                    </el-select>
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
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.payExplain" />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="payStatus"
                  :label="$t('contractMod.payStatus')"
                  width="150"
                >
                  <template slot-scope="scope">
                    <el-select v-model="scope.row.payStatus" />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="payRatio"
                  :label="$t('contractMod.payRatio')"
                  width="120"
                />
                <el-table-column
                  align="center"
                  prop="payMethod"
                  :label="$t('contractMod.paymentMethod')"
                  width="150"
                >
                  <template slot-scope="scope">
                    <DictSelect
                      v-model="scope.row.payMethod"
                      code="PAYMENT_MODE"
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
                  width="110"
                  fixed="right"
                >
                  <template slot-scope="scope">
                    <div
                      style="display: inline-block;margin-left: 15px;color: #409eff;cursor: pointer;"
                      @click="launchPaymentRequest(scope.row)"
                    >
                      {{ $t('contractMod.initiatePaymentRequest') }}
                    </div>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <el-collapse-item
              ref="contractItem"
              :title="$t('contractMod.contractItemList')"
              name="3"
            >
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
                />
                <el-table-column
                  align="center"
                  prop="sourceLineNumber"
                  :label="$t('contractMod.sourceLineNumber')"
                  width="150"
                />
                <el-table-column
                  align="center"
                  prop="materialCode"
                  :label="$t('contractMod.materialCode')"
                  min-width="150"
                />
                <el-table-column
                  align="center"
                  prop="materialName"
                  :label="$t('contractMod.materialName')"
                  min-width="150"
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
                />
                <el-table-column
                  align="center"
                  prop="untaxedPrice"
                  :label="$t('contractMod.untaxedPrice')"
                  width="100"
                />
                <el-table-column
                  align="center"
                  prop="unitName"
                  :label="$t('contractMod.unit')"
                  width="60"
                />
                <el-table-column
                  :label="$t('common.operation')"
                  width="60"
                  fixed="right"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="deleteOneItem(scope.$index, scope.row)"
                    >
                      {{ $t('common.delete') }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <el-collapse-item
              ref="contractFile"
              :title="$t('contractMod.addContractFile')"
              name="4"
            >
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
                      {{ $t('common.delete') }}
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
                    <DictSelect
                      v-model="contractHead.contractStatus"
                      code="CONTRACT_STATUS"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col><p /></el-col>
                <el-col><p /></el-col>
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
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.vendorConfirmBy')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="contractHead.vendorConfirmBy" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row type="flex">
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.vendorConfirmDate')"
                    :label-width="formLabelWidth"
                  >
                    <el-date-picker
                      v-model="contractHead.vendorConfirmDate"
                      type="date"
                      format="yyyy-MM-dd"
                      value-format="yyyy-MM-dd"
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.vendorRemark')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="contractHead.vendorRemark" />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.vendorRejectReason')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="contractHead.vendorRejectReason" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row type="flex">
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.contractSendType')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="contractHead.contractSendType" />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.logisticsNo')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="contractHead.logisticsNo" />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.arrivalTime')"
                    :label-width="formLabelWidth"
                  >
                    <el-date-picker
                      v-model="contractHead.arrivalTime"
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
                      type="date"
                      format="yyyy-MM-dd"
                      value-format="yyyy-MM-dd"
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.vendorFileName')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="contractHead.vendorFileName" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row type="flex">
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.receivedBy')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="contractHead.receivedBy" />
                  </el-form-item>
                </el-col>
                <el-col><p /></el-col>
                <el-col><p /></el-col>
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
              <div>
                <el-row type="flex">
                  <el-col>
                    <el-form-item
                      :label="$t('contractMod.secondPhone')"
                      :label-width="formLabelWidth"
                    >
                      <el-input v-model="contractHead.secondPhone" />
                    </el-form-item>
                  </el-col>
                  <el-col>
                    <el-form-item
                      :label="$t('contractMod.secondFax')"
                      :label-width="formLabelWidth"
                    >
                      <el-input v-model="contractHead.secondFax" />
                    </el-form-item>
                  </el-col>
                  <el-col>
                    <el-form-item
                      :label="$t('contractMod.secondSite')"
                      :label-width="formLabelWidth"
                    >
                      <el-input v-model="contractHead.secondSite" />
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row type="flex">
                  <el-col>
                    <el-form-item
                      :label="$t('contractMod.secondPostCode')"
                      :label-width="formLabelWidth"
                      prop="secondPostCode"
                    >
                      <el-input v-model="contractHead.secondPostCode" />
                    </el-form-item>
                  </el-col>
                  <el-col>
                    <el-form-item
                      :label="$t('contractMod.secondOpeningBank')"
                      :label-width="formLabelWidth"
                    >
                      <el-input v-model="contractHead.secondOpeningBank" />
                    </el-form-item>
                  </el-col>
                  <el-col>
                    <el-form-item
                      :label="$t('contractMod.secondBankAccount')"
                      :label-width="formLabelWidth"
                    >
                      <el-input v-model="contractHead.secondBankAccount" />
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row type="flex">
                  <el-col>
                    <el-form-item
                      :label="$t('contractMod.secondLegalPerson')"
                      :label-width="formLabelWidth"
                    >
                      <el-input v-model="contractHead.secondLegalPerson" />
                    </el-form-item>
                  </el-col>
                  <el-col>
                    <el-form-item
                      :label="$t('contractMod.secondEntrustedAgent')"
                      :label-width="formLabelWidth"
                      prop="secondEntrustedAgent"
                    >
                      <el-input v-model="contractHead.secondEntrustedAgent" />
                    </el-form-item>
                  </el-col>
                  <el-col>
                    <el-form-item
                      :label="$t('contractMod.secondSignDate')"
                      :label-width="formLabelWidth"
                      prop="secondSignDate"
                    >
                      <el-date-picker
                        v-model="contractHead.secondSignDate"
                        type="date"
                        format="yyyy-MM-dd"
                        value-format="yyyy-MM-dd"
                      />
                    </el-form-item>
                  </el-col>
                </el-row>
              </div>
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
                      {{ $t('common.delete') }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
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
            @click="closeBill"
          >
            {{
              $t("common.cancel")
            }}
          </el-button>
        </template>
      </c-toolbar>
    </el-main>
  </el-container>
</template>

<script>
import OrganizationSelectTree from 'lib@/components/organization-selector'
import CToolbar from 'lib@/components/c-toolbar'
import CFillProgress from 'lib@/components/c-fill-progress'
import { tabTodoMixin } from '@/utils/mixins'
import QuickSearch from 'lib@/components/QuickSearch'
import MainHeader from 'lib@/components/Table/MainHeader'
import { diffChars } from '@/utils/diff.min'

export default {
  name: 'ContractMaintainDetailRead',
  components: {
    MainHeader,
    CToolbar,
    CFillProgress,
    QuickSearch,
    OrganizationSelectTree
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
        { code: 'contractImpleRecord', name: this.$t('contractMod.contractImpleRecord') }
      ],
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
      isDisabled: this.$attrs.params.flag == 'edit',
      formLabelWidth: '120px',
      isModify: false,
      currencyList: [],
      templTypeList: [],
      paymentTypeList: [],
      initContractLines: [],
      contractLines: [],
      curRole: this.$store.getters.userType
    }
  },
  created () {
    this.getFormDetail()
    this.getTemplTypeList()
    this.getpaymentTypeList()
  },
  methods: {
    getpaymentTypeList () {
      this.$http({
        url: '/api-cm/template/payType/listPageByParm',
        method: 'POST',
        data: {},
        loading: true
      })
        .then(data => {
          this.paymentTypeList = data.data.list
        })
        .catch(err => {
          console.log(err)
        })
    },
    setRowpayType (row) {
      row.payType = this.paymentTypeList.filter(
        v => v.payTypeId === row.payTypeId
      )[0].payType
    },
    currencyHandler (value, dictItem) {
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
        .then(data => {
          this.templTypeList = data.data
        })
        .catch(err => {
          console.log(err)
        })
    },
    settemplate (val) {
      this.contractHead.templName = this.templTypeList.filter(
        v => v.templHeadId === val
      )[0].templName
      this.$http({
        url: '/api-cm/template/templHead/getContractTemplDTO',
        method: 'GET',
        params: { templHeadId: val },
        loading: true
      })
        .then(data => {
          if (data.data && data.data.templLines) {
            this.fileuploads = data.data.fileuploads || []
            this.initContractLines =
              data.data.templLines.filter(x => x.isDisplayed === 'Y') || []
            this.contractLines = data.data.templLines
              .filter(x => x.isDisplayed === 'Y')
              .map(v => {
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
        .catch(err => {
          console.log(err)
        })
    },
    setDiffTextHignLight (rowData, index) {
      let color = ''
        let span = null
      let diff = Diff.diffChars(rowData.templContent, rowData.contractContent)
        let the_right_div = document
          .querySelector('.compareTable')
          .querySelectorAll('.the_right_div')[index]
        let fragment = document.createDocumentFragment()
      diff.forEach(function (part) {
        // green for additions, red for deletions
        // grey for common parts
        color = part.added ? 'green' : part.removed ? 'red' : ''
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
        .then(data => {
          if (data && data.data) {
            this.contractHead = data.data.contractHead
            this.contractLines = data.data.contractLines
            this.contractMaterials = data.data.contractMaterials
            this.fileuploads = data.data.fileuploads
            this.payPlans = data.data.payPlans
          }
        })
        .catch(err => {
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
    outerHandleUploadSuccess (file, row, type) {
      const { fileId = '', fileName = '' } = file || {}
      if (type === 'doc') {
        row.fileuploadId = fileId.toString()
        row.fileSourceName = fileName
      } else {
        row.docId = fileId.toString()
        row.fileName = fileName
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
    deleteOneItem (index, row) {
      if (row.contractMaterialId) {
        this.$http({
          url: '/api-cm/contract/contractMaterial/batchDelete',
          method: 'POST',
          data: [row.contractMaterialId],
          loading: true
        })
          .then(data => {
            this.contractMaterials.splice(index, 1)
          })
          .catch(err => {
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
          .then(data => {
            this.payPlans.splice(index, 1)
          })
          .catch(err => {
            console.log(err)
          })
      } else {
        this.payPlans.splice(index, 1)
      }
    },
    launchPaymentRequest (row) {
      // 发起付款申请
      this.$http({
        url: '/api-cm/contract/payPlan/startPayApplication',
        method: 'GET',
        params: { payPlanId: row.payPlanId },
        loading: true
      }).then(res => {
        this.$message({
          type: 'success',
          message: res.message
        })
      })
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
      scope.unitCode = val ? val.unit : ''
      scope.unitName = val ? val.unitName : ''
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
        url:
          '/api-cm/contract/contractHead/getMaterialsBySourceNumber',
        method: 'GET',
        params: { sourceNumber: this.contractHead.sourceNumber },
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            this.displayItemTable = data.data
            this.dialogVisible = true
          }
        })
        .catch(err => {
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
        amount: '',
        contractQuantity: '',
        orderQuantity: '',
        untaxedPrice: '',
        peoplePrice: '',
        materialPrice: '',
        taxKey: '',
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
    closeBill () {
      this.$emit(
        'tab-remove',
        'contractMaintainDetail' + this.$attrs.params.row.contractName
      )
      this.__setTabTodo('contractMaintainList.getQuerydata')
    },
    preReadBill () {},
    // 选择组织2
    addOrgHandle (e, id, scope) {
      scope.organizationId = e ? e.organizationId : ''
      scope.organizationCode = e ? e.organizationCode : ''
      scope.organizationName = e ? e.organizationName : ''
    }
  }
}
</script>
<style scoped lang="scss">
.the-contractMaintainDetailRead-detail {
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
      }
    }
  }
}
</style>
