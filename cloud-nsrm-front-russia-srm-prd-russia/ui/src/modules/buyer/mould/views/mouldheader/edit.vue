<template>
  <el-container class="mouldheaderEdit" direction="vertical">
    <el-main>
      <CWorkflowMulti
        ref="workflowMulti"
        v-model="activeTabName"
        :fun-params="workflowParamsInfo"
        :button-config-info="buttonConfigInfo"
        @tab-click="workflowView"
        @workflow-handler="workflowHandler"
        @click-handler="type => save(type)"
        @submit-direct="type => save(type)"
        @confirm="(type, comment) => save(type, comment)"
        @close-tab="back"
      >
        <div class="form-container">
          <el-form ref="form" :model="form" :rules="rules">
            <el-collapse v-model="activeDims" class="tab-form-style">
              <!--基本信息-->
              <el-collapse-item ref="basicInformation" :title="$t('vendorMod.basicInformation')" name="1">
                <srm-row>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('mould.mouldCode')">
                      <el-input v-model="form.mouldCode" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item prop="mouldName" :label="$t('mould.mouldName')">
                      <el-input v-model="form.mouldName" :disabled="readOnly" />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <!-- 模具类型 -->
                    <el-form-item :label="$t('mould.mouldType')" prop="mouldTypeCode">
                      <DictSelect v-model="form.mouldTypeCode" code="MOULD_TYPE" :disabled="readOnly" />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <!-- 管理状态 -->
                    <el-form-item :label="$t('mould.mouldStatus')" prop="mouldStatusCode">
                      <DictSelect v-model="form.mouldStatusCode" code="MOULD_STATUS" :disabled="readOnly" />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <!-- 业务实体 -->
                    <el-form-item :label="$t('mould.orgId')">
                      <template>
                        <OrganizationSelector
                          ref="organizationSelector"
                          v-model="showForOrgId"
                          :parent-id="-1"
                          node-type="OU"
                          :placeholder="$t('common.pleaseSelect')"
                          :multiple="true"
                          :disabled="readOnly"
                          @select="selectHandler"
                        />
                      </template>
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item prop="mouldHoleNumber" :label="$t('mould.mouldHoleNumber')">
                      <el-input v-model="form.mouldHoleNumber" v-input-format="{ type: 'number' }" :disabled="readOnly" />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <!-- 备模/原模 -->
                    <el-form-item :label="$t('mould.originalMouldFlag')" prop="originalMouldFlag">
                      <DictSelect v-model="form.originalMouldFlag" code="ORIGINAL_MOULD_FLAG" :disabled="readOnly" />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('mould.dailyCapacity')">
                      <el-input v-model="form.dailyCapacity" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('mould.processingProcedure')">
                      <el-input v-model="form.processingProcedure" :disabled="readOnly" />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('mould.formingCycle')">
                      <el-input v-model="form.formingCycle" :disabled="readOnly" />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('mould.shrinkage')">
                      <el-input v-model="form.shrinkage" v-input-format="{ type: 'float' }" :disabled="readOnly" />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('mould.surplusSize')">
                      <el-input v-model="form.surplusSize" :disabled="readOnly" />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('mould.closingHeight')">
                      <el-input v-model="form.closingHeight" v-input-format="{ type: 'float' }" :disabled="readOnly" />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('mould.materialSpec')">
                      <el-input v-model="form.materialSpec" :disabled="readOnly" />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('mould.mouldTotalWeight')">
                      <el-input v-model="form.mouldTotalWeight" :disabled="readOnly" />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('mould.nozzleWeight')">
                      <el-input v-model="form.nozzleWeight" :disabled="readOnly" />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('common.creator')">
                      <el-input v-model="form.createdBy" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('mould.creationDate')">
                      <el-input v-model="form.creationDate" disabled />
                    </el-form-item>
                  </srm-col>
                </srm-row>
              </el-collapse-item>

              <!--模具制造信息-->
              <el-collapse-item :title="$t('mould.moldManufacturingInformation')" name="2">
                <srm-row>
                  <srm-col :initCol="4">
                    <!-- 申请部门 -->
                    <el-form-item :label="$t('purchaseDemand.ceeaDepartment')">
                      <QuickSearch
                        :show-input="form.applyOrgName"
                        show-key="applyOrgName"
                        :scope-data="form"
                        name="ceea_base_dept"
                        :disabled="readOnly"
                        @close-quicksearch="getApplyOrg"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('mould.applyId')">
                      <el-input v-model="form.applyBy" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('bidMod.position')">
                      <el-input v-model="form.jobcodeDescr" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <!-- 加工类型 -->
                    <el-form-item :label="$t('mould.machiningTypeCode')">
                      <DictSelect v-model="form.machiningTypeCode" code="MACHINING_TYPE" :disabled="readOnly" />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('mould.openMoldTime')">
                      <el-date-picker
                        v-model="form.openMoldTime"
                        type="date"
                        value-format="yyyy-MM-dd"
                        :placeholder="$t('vendorMod.datePicker')"
                        :disabled="readOnly"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('mould.actualFinishedMoldTime')">
                      <el-date-picker
                        v-model="form.actualFinishedMoldTime"
                        type="date"
                        value-format="yyyy-MM-dd"
                        :placeholder="$t('vendorMod.datePicker')"
                        :disabled="readOnly"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('mould.warehousingTime')">
                      <el-date-picker
                        v-model="form.warehousingTime"
                        type="date"
                        value-format="yyyy-MM-dd"
                        :placeholder="$t('vendorMod.datePicker')"
                        :disabled="readOnly"
                      />
                    </el-form-item>
                  </srm-col>
                </srm-row>
              </el-collapse-item>

              <!--模具费用信息-->
              <el-collapse-item :title="$t('mould.moldCostInformation')" name="3">
                <srm-row>
                  <srm-col :initCol="4">
                    <!-- 模具费用承担方 -->
                    <el-form-item :label="$t('mould.mouldPriceAssumerCode')" prop="mouldPriceAssumerCode">
                      <DictSelect v-model="form.mouldPriceAssumerCode" code="MOULD_PRICE_ASSUMER" :disabled="readOnly" />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('quota.currency')">
                      <DictSelect
                        v-model="form.currencyCode"
                        code="currency"
                        :disabled="readOnly"
                        @change="currencyChange"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('mould.mouldPaidAmount')">
                      <el-input v-model="form.mouldPaidAmount" v-input-format="{ type: 'float' }" :disabled="readOnly" />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('mould.mouldTobePaidAmount')">
                      <el-input
                        v-model="form.mouldTobePaidAmount"
                        v-input-format="{ type: 'float' }"
                        :disabled="readOnly"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('bidMod.paymentClause')">
                      <el-input v-model="form.paidCondition" :disabled="readOnly" />
                    </el-form-item>
                  </srm-col>
                </srm-row>
              </el-collapse-item>

              <!--责任人与存放位置-->
              <el-collapse-item :key="form.storageEntity" :title="$t('mould.responsiblePerson')" name="4">
                <srm-row>
                  <srm-col :initCol="4">
                    <!-- 存放对象 -->
                    <el-form-item :label="$t('mould.storageEntity')" prop="storageEntity">
                      <DictSelect
                        v-model="form.storageEntity"
                        code="STORAGE_ENTITY"
                        :disabled="readOnly || flag === 'update'"
                        @change="storageEntityChange"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <!-- 供应商查询 -->
                    <el-form-item :label="$t('mould.supplierCode')" prop="supplierCode">
                      <QuickSearch
                        :show-input="form.supplierCode"
                        show-key="supplierCode"
                        :scope-data="form"
                        name="scc_sup_company_info"
                        :disabled="readOnly || flag === 'update'"
                        @close-quicksearch="getSupplyObj"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('purSettlementMod.vendorName')">
                      <el-input v-model="form.supplierName" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('mould.mouldShiftTime')">
                      <!--移模日期-->
                      <el-date-picker
                        v-model="form.mouldShiftTime"
                        type="date"
                        value-format="yyyy-MM-dd"
                        :placeholder="$t('vendorMod.datePicker')"
                        :disabled="readOnly || flag === 'update'"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="1">
                    <el-form-item prop="storageAddress" :label="$t('mould.storageAddress')">
                      <el-input v-model="form.storageAddress" type="textarea" :disabled="readOnly || flag === 'update'" />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <div class="the_QuickSearch_wrapper">
                      <!-- 供应商责任人 -->
                      <el-form-item :label="$t('mould.supplierResponer')" prop="supplierResponerName">
                        <QuickSearch
                          :show-input="form.supplierResponerName"
                          show-key="supplierResponerName"
                          :scope-data="form"
                          name="scc_rbac_user_vendor_display"
                          :disabled="readOnly || flag === 'update'"
                          @close-quicksearch="getSupplierUserObj"
                        />
                      </el-form-item>
                    </div>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('mould.supplierResponerEmail')">
                      <el-input v-model="form.supplierResponerEmail" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <div class="the_QuickSearch_wrapper">
                      <!-- 采购商责任人 -->
                      <el-form-item prop="purchaserResponerName" :label="$t('mould.purchaserResponer')">
                        <QuickSearch
                          :show-input="form.purchaserResponerName"
                          show-key="purchaserResponerName"
                          :scope-data="form"
                          name="scc_rbac_user_display"
                          :disabled="readOnly || flag === 'update'"
                          @close-quicksearch="getPurchaserUserObj"
                        />
                      </el-form-item>
                    </div>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('mould.purchaserResponerEmail')">
                      <el-input v-model="form.purchaserResponerEmail" disabled />
                    </el-form-item>
                  </srm-col>
                </srm-row>
              </el-collapse-item>

              <!--模具履历信息-->
              <el-collapse-item :title="$t('mould.mouldResumeInformation')" name="5">
                <srm-row>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('mould.ceeaRequirementHeadNum')">
                      <QuickSearch
                        :show-input="form.ceeaRequirementHeadNum"
                        show-key="requirementHeadNum"
                        :scope-data="form"
                        name="scc_pr_requirement_head"
                        :disabled="readOnly"
                        @close-quicksearch="getRequirementHeadNum"
                      />
                    </el-form-item>
                  </srm-col>

                  <srm-col :initCol="4">
                    <el-form-item :label="$t('bidMod.approvalNo')">
                      <QuickSearch
                        :show-input="form.sourceNumber"
                        show-key="approvalNo"
                        :scope-data="form"
                        name="scc_price_approval_no"
                        :disabled="readOnly"
                        @close-quicksearch="getSourceNumber"
                      />
                    </el-form-item>
                  </srm-col>

                  <srm-col :initCol="4">
                    <el-form-item :label="$t('purSettlementMod.orderNumber')">
                      <QuickSearch
                        :show-input="form.orderNumber"
                        show-key="orderNumber"
                        :scope-data="form"
                        name="scc_sc_order"
                        :disabled="readOnly"
                        @close-quicksearch="getOrderNumber"
                      />
                    </el-form-item>
                  </srm-col>

                  <srm-col :initCol="4">
                    <el-form-item :label="$t('purSettlementMod.warehouseReceiptNumber')">
                      <QuickSearch
                        :show-input="form.warehouseReceiptNumber"
                        show-key="warehouseReceiptNumber"
                        :scope-data="form"
                        name="scc_sc_warehouse_receipt"
                        :disabled="readOnly"
                        @close-quicksearch="getWarehouseNumber"
                      />
                    </el-form-item>
                  </srm-col>

                  <srm-col :initCol="4">
                    <el-form-item :label="$t('contractMod.contractNo')">
                      <QuickSearch
                        :show-input="form.contractNo"
                        show-key="contractNo"
                        :scope-data="form"
                        name="scc_contract_head"
                        :disabled="readOnly"
                        @close-quicksearch="getContractNo"
                      />
                    </el-form-item>
                  </srm-col>
                </srm-row>
              </el-collapse-item>

              <!--模具保养信息-->
              <el-collapse-item :title="$t('mould.moldMaintenanceInformation')" name="6">
                <srm-row>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('mould.maintainCycle')">
                      <el-input v-model="form.maintainCycle" :disabled="readOnly" />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('mould.maintainCostTime')">
                      <el-input v-model="form.maintainCostTime" :disabled="readOnly" />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('mould.maintainTime')">
                      <el-date-picker
                        v-model="form.maintainTime"
                        type="date"
                        value-format="yyyy-MM-dd"
                        :placeholder="$t('vendorMod.datePicker')"
                        :disabled="readOnly"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('mould.maintainNextTime')">
                      <el-date-picker
                        v-model="form.maintainNextTime"
                        type="date"
                        value-format="yyyy-MM-dd"
                        :placeholder="$t('vendorMod.datePicker')"
                        :disabled="readOnly"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <!-- 模具是否续开 -->
                    <el-form-item :label="$t('mould.mouldContinuityFlag')">
                      <DictSelect v-model="form.mouldContinuityFlag" code="YES_OR_NO" :disabled="readOnly" />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('mould.mouldContinuityCycle')">
                      <el-input v-model="form.mouldContinuityCycle" :disabled="readOnly" />
                    </el-form-item>
                  </srm-col>
                </srm-row>
              </el-collapse-item>
              <!-- 模具返还信息 -->
              <el-collapse-item :title="$t('mould.moldReturnInformation')" name="7">
                <srm-row>
                  <srm-col :initCol="4">
                    <el-form-item prop="mouldTotalAmount" :label="$t('mould.mouldTotalAmount')">
                      <el-input
                        v-model="form.mouldTotalAmount"
                        v-input-format="{ type: 'float' }"
                        :disabled="readOnly"
                        @input="mouldTotalAmountChange"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <!-- 是否返回模具 -->
                    <el-form-item :label="$t('mould.mouldReturnFlag')" prop="mouldReturnFlag">
                      <DictSelect
                        v-model="form.mouldReturnFlag"
                        code="YES_OR_NO"
                        :disabled="readOnly"
                        @change="mouldReturnFlagChange"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="this.form.mouldReturnFlag === 'Y'" :initCol="4">
                    <el-form-item :label="$t('mould.mouldReturnNum')">
                      <el-input
                        v-model="form.mouldReturnNum"
                        v-input-format="{ type: 'number' }"
                        :disabled="this.form.mouldReturnFlag !== 'Y' || readOnly"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="this.form.mouldReturnFlag === 'Y'" :initCol="4">
                    <el-form-item :label="$t('mould.totalReturnTime')">
                      <el-date-picker
                        v-model="form.totalReturnTime"
                        type="date"
                        value-format="yyyy-MM-dd"
                        :placeholder="$t('vendorMod.datePicker')"
                        :disabled="this.form.mouldReturnFlag !== 'Y' || readOnly"
                      />
                    </el-form-item>
                  </srm-col>
                </srm-row>
              </el-collapse-item>

              <!--模具分摊与寿命信息-->
              <el-collapse-item :title="$t('mould.dieAllocation')" name="8">
                <srm-row>
                  <!-- 进行分摊 -->
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('mould.mouldShareFlag')">
                      <DictSelect v-model="form.mouldShareFlag" code="YES_OR_NO" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <!-- 分摊状态 -->
                    <el-form-item :label="$t('mould.mouldShareStatus')">
                      <DictSelect v-model="form.mouldShareStatus" code="MOULD_SHARE_STATUS" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('mould.totalShareAmount')">
                      <el-input v-model="form.totalShareAmount" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('mould.sharedAmount')">
                      <el-tooltip class="item" effect="dark" placement="top">
                        <div slot="content" class="tooltip-content">
                          {{ $t("mould.tips[0]") }}
                        </div>
                        <i class="el-icon-info info-icon" />
                      </el-tooltip>
                      <el-input v-model="form.sharedAmount" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('mould.sharedPercent')">
                      <el-tooltip class="item" effect="dark" placement="top">
                        <div slot="content" class="tooltip-content">
                          {{ $t("mould.tips[1]") }}
                        </div>
                        <i class="el-icon-info info-icon" />
                      </el-tooltip>
                      <el-input v-model="form.sharedPercent" disabled />
                    </el-form-item>
                  </srm-col>

                  <srm-col :initCol="4">
                    <el-form-item :label="$t('mould.sharedNum')">
                      <el-tooltip class="item" effect="dark" placement="top">
                        <div slot="content" class="tooltip-content">
                          {{ $t("mould.tips[2]") }}
                        </div>
                        <i class="el-icon-info info-icon" />
                      </el-tooltip>
                      <el-input v-model="form.sharedNum" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('mould.predictUseTime')">
                      <el-input
                        v-model="form.predictUseTime"
                        v-input-format="{ type: 'number' }"
                        :disabled="readOnly"
                        @input="predictUseTimeChange"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('mould.usedTime')">
                      <el-tooltip class="item" effect="dark" placement="top">
                        <div slot="content" class="tooltip-content">
                          {{ $t("mould.tips[3]") }}
                        </div>
                        <i class="el-icon-info info-icon" />
                      </el-tooltip>
                      <el-input v-model="form.usedTime" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('mould.usedPercent')">
                      <el-tooltip class="item" effect="dark" placement="top">
                        <div slot="content" class="tooltip-content">
                          {{ $t("mould.tips[4]") }}
                        </div>
                        <i class="el-icon-info info-icon" />
                      </el-tooltip>
                      <el-input v-model="form.usedPercent" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <!-- 模具与制件对应关系 -->
                    <el-form-item :label="$t('mould.mouldItemType')" prop="mouldItemType">
                      <DictSelect
                        v-model="form.mouldItemType"
                        code="MOULD_ITEM_TYPE"
                        :disabled="readOnly"
                        @change="mouldItemTypeChange"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <!-- 制件产出顺序 -->
                    <el-form-item v-if="form.mouldItemType === '3'" :label="$t('mould.itemProductType')">
                      <DictSelect v-model="form.itemProductType" code="ITEM_PRODUCT_TYPE" :disabled="readOnly" />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="4">
                    <el-form-item v-if="form.mouldItemType === '2'" :label="$t('mould.sameShareId')">
                      <QuickSearch
                        :show-input="form.sameShareId"
                        show-key="sameShareId"
                        :scope-data="form"
                        name="scc_sc_mould_header_for_share"
                        :disabled="readOnly"
                        @close-quicksearch="getSameShareId"
                      />
                    </el-form-item>
                  </srm-col>
                </srm-row>
              </el-collapse-item>

              <!--模具与物料对应关系-->
              <el-collapse-item ref="mouldItemType" :title="$t('mould.mouldItemType')" name="9">
                <el-container class="flex-container" style="height: 300px;">
                  <el-main>
                    <div v-if="!readOnly && (form.mouldItemType === '3' || (form.mouldItemType !== '3' && realDataSource.length < 1)) " style="padding: 12px 0;">
                      <el-button class="detail-pbtn" type="primary" @click="addLine">
                        {{ $t('common.new') }}
                      </el-button>
                    </div>
                    <BaseTable
                      ref="table"
                      :columns="columns"
                      :data-source="dataSource"
                      :initialize="false"
                      row-key="mouldLineId"
                      border
                      @asyncGetRealDataSource="asyncGetRealDataSource"
                    >
                      <template #itemNumber="{ scope }">
                        <QuickSearch
                          :show-input="scope.row.itemNumber"
                          show-key="itemNumber"
                          :scope-data="scope"
                          name="scc_base_material_item"
                          @close-quicksearch="value => getMaterial(value, scope)"
                        />
                      </template>
                      <template #itemDescZhs="{ scope }">
                        <el-input v-model="scope.row.itemDescZhs" disabled />
                      </template>
                      <template #productNum="{ scope }">
                        <el-input
                          v-model="scope.row.productNum"
                          v-input-format="{ type: 'number' }"
                          @input="productNumChange"
                        />
                      </template>
                      <template #shareUnitPrice="{ scope }">
                        <el-input
                          v-model="scope.row.shareUnitPrice"
                          v-input-format="{ type: 'float' }"
                          @input="shareUnitPriceChange"
                        />
                      </template>
                      <template #predictDailyCapacity="{ scope }">
                        <el-input v-model="scope.row.predictDailyCapacity" v-input-format="{ type: 'number' }" />
                      </template>
                      <template #shareStartTime="{ scope }">
                        <el-date-picker
                          v-model="scope.row.shareStartTime"
                          type="date"
                          size="small"
                          value-format="yyyy-MM-dd"
                          :placeholder="$t('vendorMod.datePicker')"
                          @change="value => selectShareStartTime(value, scope)"
                        />
                      </template>
                      <template #shareEndTime="{ scope }">
                        <el-date-picker
                          v-model="scope.row.shareEndTime"
                          type="date"
                          value-format="yyyy-MM-dd"
                          :placeholder="$t('vendorMod.datePicker')"
                          @change="value => selectShareEndTime(value, scope)"
                        />
                      </template>
                      <template #orderNum="{ scope }">
                        <el-input v-model="scope.row.orderNum" disabled />
                      </template>
                      <template #warehousingNum="{ scope }">
                        <el-input v-model="scope.row.warehousingNum" disabled />
                      </template>
                      <template #createdBy="{ scope }">
                        <el-input v-model="scope.row.createdBy" disabled />
                      </template>
                      <template #creationDate="{ scope }">
                        <el-input v-model="scope.row.creationDate" disabled />
                      </template>
                      <template #lastUpdateDate="{ scope }">
                        <el-input v-model="scope.row.lastUpdateDate" disabled />
                      </template>
                    </BaseTable>
                  </el-main>
                </el-container>
              </el-collapse-item>

              <!--说明-->
              <el-collapse-item :title="$t('vendorMod.operationMemo')" name="10">
                <srm-row>
                  <srm-col :initCol="1">
                    <el-form-item prop="explanation">
                      <el-input v-model="form.explanation" type="textarea" :disabled="readOnly" />
                    </el-form-item>
                  </srm-col>
                </srm-row>
              </el-collapse-item>
              <!-- 附件 -->
              <el-collapse-item :title="$t('dataConfMod.attachment')" name="11">
                <div v-if="!readOnly" style="padding: 12px 0;">
                  <el-button type="primary" class="detail-pbtn" @click="addFile">
                    {{ $t("common.add") }}
                  </el-button>
                </div>
                <el-table :data="attachmentList" style="width: 100%" border max-height="200">
                  <el-table-column align="center" type="index" :label="$t('purSettlementMod.tabindex')" width="50" />
                  <el-table-column align="center" prop="attachName" :label="$t('bidMod.fileName')">
                    <template slot-scope="scope">
                      <SrmCommonFile
                        :extra-data="fileInfo"
                        :default-file="{
                          fileId: scope.row.fileuploadId,
                          fileName: scope.row.fileSourceName
                        }"
                        :readonly="readOnly"
                        @on-change="({ file }) => HandleUploadSuccess(file, scope.row)"
                      />
                    </template>
                  </el-table-column>
                  <el-table-column :label="$t('common.operation')" width="100">
                    <template slot-scope="scope">
                      <el-button v-if="!readOnly" type="text" @click="deleteOneContent3(scope.$index, scope.row)">
                        {{ $t("common.delete") }}
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </el-collapse-item>
            </el-collapse>
          </el-form>
        </div>
      </CWorkflowMulti>
    </el-main>
  </el-container>
</template>

<script>

import { tabTodoMixin } from '@/utils/mixins'
import { downloadFileLink } from 'lib@/utils/file'
import { getToken } from '@/utils/auth'
import { numberToPercent, percentToNumber } from '@/library/utils/number'
import axios from 'axios'
import { sysPrefix } from '@/config/ipConfig'
import BaseTable from 'lib@/components/BaseTable/baseTable'
import QuickSearch from '@/library/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import WorkflowCommon from '@/library/mixins/workflow-common'
import { mouldheader } from 'modb@/mould/api'

import { createDictClass } from '@/library/utils/dict/dict-utils'
const dictClass = createDictClass({ currency: [] })

export default {
  name: 'MouldheaderEdit',
  components: {
    BaseTable,
    QuickSearch,
    OrganizationSelector
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      dictClass: dictClass,
      activeDims: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11'],
      fileInfo: {
        fileModular: 'workFlow',
        fileFunction: 'workflowReport',
        fileType: 'images'
      },
      attachmentList: [],
      realDataSource: [], // 明细表数据
      orgList: [], // 业务实体
      dataSource: [],
      columns: [
        {
          attrs: {
            prop: 'itemId',
            label: this.$t('mould.itemId')
          },
          slot: 'itemId',
          hidden: true
        },
        {
          attrs: {
            prop: 'itemNumber',
            label: this.$t('mould.itemNumber')
          },
          slot: 'itemNumber',
          rules: { required: true, message: this.$t('dataConfMod.required') }
        },
        {
          attrs: {
            prop: 'itemDescZhs',
            label: this.$t('supplierCapacityReport.materialName')
          },
          slot: 'itemDescZhs'
        },
        {
          attrs: {
            prop: 'productNum',
            label: this.$t('mould.productNum')
          },
          slot: 'productNum',
          rules: { required: true, message: this.$t('dataConfMod.required') }
        },
        {
          attrs: {
            prop: 'shareUnitPrice',
            label: this.$t('mould.shareUnitPrice')
          },
          slot: 'shareUnitPrice'
        },
        {
          attrs: {
            prop: 'predictDailyCapacity',
            label: this.$t('mould.predictDailyCapacity')
          },
          slot: 'predictDailyCapacity'
        },
        {
          attrs: {
            prop: 'shareStartTime',
            label: this.$t('mould.shareStartTime'),
            width: 140
          },
          slot: 'shareStartTime',
          rules: { required: true, message: this.$t('dataConfMod.required') }
        },
        {
          attrs: {
            prop: 'shareEndTime',
            label: this.$t('mould.shareEndTime'),
            width: 140
          },
          slot: 'shareEndTime'
        },
        {
          attrs: {
            prop: 'orderNum',
            label: this.$t('mould.orderNum')
          },
          slot: 'orderNum'
        },
        {
          attrs: {
            prop: 'warehousingNum',
            label: this.$t('purSettlementMod.warehouseReceiptQuantity')
          },
          slot: 'warehousingNum'
        },
        {
          attrs: {
            prop: 'creationDate',
            label: this.$t('purSettlementMod.creationDate')
          },
          slot: 'creationDate'
        },
        {
          attrs: {
            prop: 'lastUpdateDate',
            label: this.$t('common.updateTime')
          },
          slot: 'lastUpdateDate'
        },
        {
          attrs: {
            prop: 'operation',
            label: this.$t('common.operation'),
            width: 80,
            fixed: 'right'
          },
          operations: [
            {
              event: 'deleteItem',
              name: this.$t('common.delete'),
              func: this.deleteItem,
              show: () => !this.readOnly
            }
          ]
        }
      ],
      showForOrgId: [],
      disabledButton: false,
      form: {
        mouldShareFlag: null,
        flowLogId: null,
        mouldHeaderId: null,
        approveStatus: 'DRAFT',
        explanation: null,
        mouldCode: null,
        mouldName: null,
        mouldTypeCode: null,
        mouldStatusCode: null,
        mouldHoleNumber: '',
        originalMouldFlag: null,
        dailyCapacity: null,
        processingProcedure: null,
        formingCycle: null,
        shrinkage: null,
        surplusSize: null,
        closingHeight: null,
        materialSpec: null,
        mouldTotalWeight: null,
        nozzleWeight: null,
        applyOrgId: null,
        applyOrgCode: null,
        applyOrgName: null,
        applyId: null,
        applyCode: null,
        applyBy: null,
        jobcode: null,
        jobcodeDescr: null,
        machiningTypeCode: null,
        machiningTypeName: null,
        openMoldTime: null,
        actualFinishedMoldTime: null,
        warehousingTime: null,
        mouldPriceAssumerCode: null,
        mouldPriceAssumerName: null,
        currencyId: null,
        currencyCode: null,
        currencyName: null,
        mouldTotalAmount: '',
        mouldPaidAmount: null,
        mouldTobePaidAmount: null,
        paidCondition: null,
        storageEntity: null,
        supplierId: null,
        supplierCode: null,
        supplierName: null,
        mouldShiftTime: null,
        storageAddress: null,
        supplierResponerId: null,
        supplierResponerCode: null,
        supplierResponerName: null,
        supplierResponerEmail: null,
        purchaserResponerId: null,
        purchaserResponerCode: null,
        purchaserResponerName: null,
        purchaserResponerEmail: null,
        ceeaRequirementHeadNum: null,
        sourceNumber: null,
        orderNumber: null,
        warehouseReceiptNumber: null,
        contractNo: null,
        maintainCycle: null,
        maintainCostTime: null,
        maintainTime: null,
        maintainNextTime: null,
        mouldContinuityFlag: null,
        mouldContinuityCycle: null,
        mouldReturnFlag: null,
        mouldReturnNum: null,
        totalReturnTime: null,
        mouldShareStatus: null,
        totalShareAmount: null,
        sharedAmount: null,
        sharedNum: null,
        sharedPercent: null,
        predictUseTime: null,
        usedTime: null,
        usedPercent: null,
        mouldItemType: null,
        itemProductType: null,
        sameShareId: null,
        createdBy: null,
        creationDate: null,
        lastUpdatedBy: null
      },
      dynamicRules: {
        supplierCode: [{ required: true, message: this.$t('common.pleaseSelect') }],
        supplierResponerName: [{ required: true, message: this.$t('common.pleaseSelect') }]
      },
      rules: {
        mouldName: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        mouldTypeCode: [{ required: true, message: this.$t('common.pleaseSelect') }],
        mouldStatusCode: [{ required: true, message: this.$t('common.pleaseSelect') }],
        mouldHoleNumber: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        originalMouldFlag: [{ required: true, message: this.$t('common.pleaseSelect') }],
        mouldPriceAssumerCode: [{ required: true, message: this.$t('common.pleaseSelect') }],
        currencyName: [{ required: true, message: this.$t('common.pleaseSelect') }],
        mouldTotalAmount: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        storageEntity: [{ required: true, message: this.$t('common.pleaseSelect') }],
        storageAddress: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        purchaserResponerName: [{ required: true, message: this.$t('common.pleaseSelect') }],
        mouldItemType: [{ required: true, message: this.$t('common.pleaseSelect') }],
        mouldReturnFlag: [{ required: true, message: this.$t('common.pleaseSelect') }]
      },
      readOnly: false,
      flag: ''
    }
  },
  computed: {
    viewUpdateButton () {
      return !this.readOnly && this.form.approveStatus !== 'APPROVED'
    },
    disabledUpdateButton () {
      return this.form.approveStatus === 'SUBMITTED' || this.form.approveStatus === 'APPROVING'
    },
    workflowBusinessId () {
      const { flag } = this.$attrs.params
      if (flag === 'edit' || flag === 'add') {
        return this.form ? this.form.mouldHeaderId : null
      } else {
        return this.form ? this.form.flowLogId : null
      }
    },
    workflowTabDisabled () {
      return this.form.approveStatus === 'DRAFT'
    }
  },
  watch: {
    viewUpdateButton () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
    }
  },
  created () {
    const { flag, firstFlag, readOnly, row } = this.$attrs.params
    this.readOnly = readOnly
    this.flag = flag
    if (flag === 'edit' || flag === 'view') {
      this.getDetail(row.mouldHeaderId)
    }
    if (flag === 'update' && firstFlag) {
      this.getDetail(row.mouldHeaderId)
    } else if (flag === 'update') {
      // 读取历史记录表
      this.getHistoryDetail(row.mouldFlowLogId)
    }

    this.buttonConfigInfo.save.view = this.viewUpdateButton
    this.buttonConfigInfo.submit.view = this.viewUpdateButton
    this.buttonConfigInfo.cancel.view = !this.readOnly
    this.buttonConfigInfo.close.view = this.readOnly
  },
  methods: {
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      if (this.$attrs.params.flag === 'edit' || this.$attrs.params.flag === 'add') {
        return 'MOULDCREATE'
      } else if (this.$attrs.params.flag === 'update') {
        return 'MOULDUPDATE'
      }
    },
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },

    async getDetail (headerId) {
      if (!headerId) return
      await mouldheader.getById(headerId).then(res => {
        const { mouldLineList, mouldHeaderOrgRelationList, mouldAttachList, ...rest } = res.data
        this.form = rest
        this.dataSource = mouldLineList
        this.orgList = mouldHeaderOrgRelationList
        this.attachmentList = mouldAttachList
        this.form.sharedPercent = numberToPercent(this.form.sharedPercent)
        this.form.usedPercent = numberToPercent(this.form.usedPercent)
        mouldHeaderOrgRelationList.forEach(item => {
          this.showForOrgId.push(item.orgId)
        })
      })
    },
    async getHistoryDetail (headerId) {
      if (!headerId) return
      await mouldheader.getHistory(headerId).then(res => {
        const { mouldLineList, mouldHeaderOrgRelationList, mouldAttachList, ...rest } = res.data
        this.form = rest
        this.dataSource = mouldLineList || []
        this.orgList = mouldHeaderOrgRelationList || []
        this.attachmentList = mouldAttachList || []
        this.form.sharedPercent = numberToPercent(this.form.sharedPercent)
        this.form.usedPercent = numberToPercent(this.form.usedPercent)
        mouldHeaderOrgRelationList.forEach(item => {
          this.showForOrgId.push(item.orgId)
        })
      })
    },

    // 计算已分摊百分比
    calcSharedPercent (val) {
      if (this.form.totalShareAmount && this.form.sharedAmount) {
        const result = this.form.sharedAmount / this.form.totalShareAmount
        this.form.sharedPercent = numberToPercent(result.toFixed(4))
      } else {
        this.form.sharedPercent = numberToPercent(0)
      }
      // 修改分摊状态
      if (percentToNumber(this.form.sharedPercent) < 1) {
        this.form.mouldShareStatus = '2' // 未完成
      } else if (percentToNumber(this.form.sharedPercent) >= 1) {
        this.form.mouldShareStatus = '1' // 完成
      }
    },
    // 计算已使用百分比
    calcUsedPercent (val) {
      if (this.form.predictUseTime && this.form.usedTime) {
        const result = this.form.usedTime / this.form.predictUseTime
        this.form.usedPercent = numberToPercent(result.toFixed(4))
      } else {
        this.form.usedPercent = numberToPercent(0)
      }
    },
    // 计算已分摊金额("下单数量"*单价之和),已分摊数量(各物料"下单数量"之和),已使用寿命("入库数量"/单次产出数量,多个物料取最大值)
    calcSharedAmount () {
      const that = this
      setTimeout(function () {
        let shareAmountResult = 0 // 已分摊金额
        let sharedNumResult = 0 // 已分摊数量
        let usedTimeResult = 0 // 已使用寿命次数
        that.realDataSource.forEach(item => {
          if (item.orderNum) {
            // 计算已分摊数量
            sharedNumResult += item.orderNum
          }
          if (item.orderNum && item.shareUnitPrice) {
            // 计算已分摊金额
            shareAmountResult += item.orderNum * item.shareUnitPrice
          }
          if (item.warehousingNum && item.productNum) {
            usedTimeResult += Math.ceil(item.warehousingNum / item.productNum)
          }
        })
        that.form.sharedAmount = shareAmountResult // 已分摊金额
        that.form.sharedNum = sharedNumResult // 已分摊数量
        that.form.usedTime = usedTimeResult // 已使用次数
        that.calcSharedPercent()
        that.calcUsedPercent()
      }, 1000)
    },
    // 计算当前物料的下单数量和入库数量
    calcOrderNumberAndWarehousingNum (scope) {
      // 校验条件是否填写
      if (
        this.orgList.length > 0 &&
        this.form.supplierCode &&
        this.form.supplierCode !== '' &&
        this.form.mouldItemType &&
        this.form.mouldItemType !== ''
      ) {
        if (
          this.form.mouldItemType === '2' &&
          (this.form.sameShareId === null || this.form.sameShareId === '')
        ) {
          // 一料多模
          return
        }
        if (scope) {
          // 当前物料行选中了
          if (scope.row.itemNumber && scope.row.shareStartTime) {
            const orgIds = []
            this.orgList.forEach(item => orgIds.push(item.orgId))
            const dataParam = {
              orgIds: orgIds,
              supplierCode: this.form.supplierCode,
              mouldItemType: this.form.mouldItemType,
              itemNumber: scope.row.itemNumber,
              mouldLineId: scope.row.mouldLineId,
              shareStartTime: scope.row.shareStartTime,
              shareEndTime: scope.row.shareEndTime,
              sameShareId: this.form.sameShareId
            }
            mouldheader.calcOrderNumberAndWarehousingNum(dataParam).then(res => {
              this.$set(
                this.$refs.table.form.dataSource[scope.$index],
                'orderNum',
                res.data.orderNum ? res.data.orderNum : 0,
              )
              this.$set(
                this.$refs.table.form.dataSource[scope.$index],
                'warehousingNum',
                res.data.warehousingNum ? res.data.warehousingNum : 0,
              )
            })
            this.calcSharedAmount()
          }
        } else {
          // 当前明细所有都要计算
          for (let i = 0; i < this.realDataSource.length; i++) {
            const item = this.realDataSource[i]
            if (item.itemNumber && item.shareStartTime) {
              const orgIds = []
              this.orgList.forEach(item => orgIds.push(item.orgId))
              const dataParam = {
                orgIds: orgIds,
                supplierCode: this.form.supplierCode,
                mouldItemType: this.form.mouldItemType,
                itemNumber: item.itemNumber,
                shareStartTime: item.shareStartTime,
                shareEndTime: item.shareEndTime,
                mouldLineId: item.mouldLineId,
                sameShareId: this.form.sameShareId
              }
              mouldheader.calcOrderNumberAndWarehousingNum(dataParam).then(res => {
                this.$set(
                  this.$refs.table.form.dataSource[i],
                  'orderNum',
                  res.data.orderNum ? res.data.orderNum : 0,
                )
                this.$set(
                  this.$refs.table.form.dataSource[i],
                  'warehousingNum',
                  res.data.warehousingNum ? res.data.warehousingNum : 0,
                )
              })
              this.calcSharedAmount()
            }
          }
        }
      }
    },

    selectHandler (node, value, scope) {
      this.orgList = []
      if (node) {
        node.forEach(item => {
          this.orgList.push({
            orgId: item.organizationId,
            orgCode: item.organizationCode,
            orgName: item.organizationName
          })
        })
        this.calcOrderNumberAndWarehousingNum()
      }
    },

    getApplyOrg (v) {
      this.form.applyOrgId = v ? v.deptid : ''
      this.form.applyOrgName = v ? v.descr : ''
      this.form.applyBy = v ? v.managerName : '' // 申请人名称
      this.form.jobcode = v ? v.managerPosn : '' // 申请人ID
      this.form.jobcodeDescr = v ? v.posnDescr : '' // 申请人名称
    },

    currencyChange (val) {
      const dRowObj = this.dictClass.getDictDetail('currency', val)
      console.log(dRowObj)
      this.form.currencyCode = dRowObj.value
      this.form.currencyName = dRowObj.label
    },

    storageEntityChange (val) {
      if (val !== '2') {
        // rules删除必填
        delete this.rules.supplierCode
        delete this.rules.supplierResponerName
      } else {
        // eslint-disable-next-line no-prototype-builtins
        if (!this.rules.hasOwnProperty('supplierCode')) {
          Object.assign(this.rules, this.dynamicRules)
        }
      }
    },
    getSupplyObj (v) {
      this.form.supplierCode = v ? v.companyCode : ''
      this.form.supplierName = v ? v.companyName : ''
      this.form.supplierId = v ? v.companyId : ''
      this.calcOrderNumberAndWarehousingNum()
    },
    getSupplierUserObj (v) {
      this.form.supplierResponerCode = v ? v.username : ''
      this.form.supplierResponerName = v ? v.nickname : ''
      this.form.supplierresponerId = v ? v.userId : ''
      this.form.supplierResponerEmail = v ? v.email : ''
    },
    getPurchaserUserObj (v) {
      this.form.purchaserResponerCode = v ? v.username : ''
      this.form.purchaserResponerName = v ? v.nickname : ''
      this.form.purchaserResponerId = v ? v.userId : ''
      this.form.purchaserResponerEmail = v ? v.email : ''
    },

    getRequirementHeadNum (val) {
      console.log(val)
      this.form.ceeaRequirementHeadNum = val ? val.requirementHeadNum : ''
    },
    getSourceNumber (val) {
      this.form.sourceNumber = val ? val.approvalNo : ''
    },
    getOrderNumber (val) {
      console.log(val)
      this.form.orderNumber = val ? val.orderNumber : ''
    },
    getWarehouseNumber (val) {
      this.form.warehouseReceiptNumber = val ? val.warehouseReceiptNumber : ''
    },
    getContractNo (val) {
      this.form.contractNo = val ? val.contractNo : ''
    },

    // 模具总价格变化触发
    mouldTotalAmountChange (val) {
      this.form.totalShareAmount = val
      this.calcSharedPercent()
    },
    mouldReturnFlagChange (val) {
      if (val === 'Y') {
        this.form.mouldShareFlag = 'N'
      }
      if (val === 'N') {
        this.form.mouldShareFlag = 'Y'
      }
    },

    // 预计使用寿命修改时触发
    predictUseTimeChange (val) {
      this.calcSharedAmount()
    },
    mouldItemTypeChange (val) {
      this.calcOrderNumberAndWarehousingNum()
    },
    getSameShareId (val) {
      if (val) {
        this.form.sameShareId = val.sameShareId ? val.sameShareId : val.mouldCode
      }
      this.calcOrderNumberAndWarehousingNum()
    },

    addLine () {
      if (this.form.mouldItemType === '3') {
        // 一模多料
        this.$refs.table.add({ productNum: '' })
      } else {
        if (this.realDataSource.length < 1) {
          this.$refs.table.add({ productNum: '' })
        }
      }
    },
    deleteItem (scope, data) {
      data.splice(scope.$index, 1)
    },
    asyncGetRealDataSource (data) {
      this.realDataSource = data
    },
    getMaterial (val, scope) {
      // base-table 设值
      this.$set(this.$refs.table.form.dataSource[scope.$index], 'itemId', val ? val.materialId : null)
      this.$set(this.$refs.table.form.dataSource[scope.$index], 'itemNumber', val ? val.materialCode : null)
      this.$set(this.$refs.table.form.dataSource[scope.$index], 'itemDescZhs', val ? val.materialName : null)
      this.calcOrderNumberAndWarehousingNum(scope)
    },
    // 单次产出数量变化时候
    productNumChange (val) {
      this.calcSharedAmount()
    },
    // 单价变化触发
    shareUnitPriceChange (val) {
      this.calcSharedAmount()
    },
    selectShareStartTime (val, scope) {
      this.calcOrderNumberAndWarehousingNum(scope)
    },
    selectShareEndTime (val, scope) {
      this.calcOrderNumberAndWarehousingNum(scope)
    },

    addFile () {
      this.attachmentList.push({
        fileuploadId: null,
        fileSourceName: '',
        fileFunction: 'mouldAttachment' // 文件所属功能
      })
    },
    // 上传附件成功
    HandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.fileuploadId = fileId.toString()
      row.fileSourceName = fileName
    },
    deleteOneContent3 (index, row) {
      if (row.fileuploadId) {
        mouldheader.fileuploadDelete({ id: row.fileuploadId }).then(res => {
          this.attachmentList.splice(index, 1)
        })
      } else {
        this.attachmentList.splice(index, 1)
      }
    },
    back () {
      if (this.$attrs.params.flag === 'add') {
        this.$emit('tab-remove', 'mouldheaderEdit')
      } else {
        this.$emit('tab-remove', this.$attrs.params.tabName)
      }
      this.__setTabTodo('mouldheaderList.getQuerydata')
    },
    async save (type) {
      this.disabledButton = true
      setTimeout(() => {
        this.disabledButton = false
      }, 1000)
      const checkResult = this.checkItemTime()
      if (!checkResult) {
        return
      }
      const checkMouldHoleNumberResult = this.checkMouldHoleNumber()
      if (!checkMouldHoleNumberResult) {
        return
      }
      if (this.orgList === null || this.orgList.length === 0) {
        return this.__jump_error__(
          'basicInformation',
          null,
          this.$t('mould.businessEntityMandatory'),
        )
      }
      this.saveFunction(type)
    },
    saveFunction (type) {
      this.$refs.form.validate(result => {
        if (result) {
          const { flag } = this.$attrs.params
          const data = {
            ...this.form,
            mouldLineList: this.realDataSource,
            mouldHeaderOrgRelationList: this.orgList,
            mouldAttachList: this.attachmentList
          }
          data.usedPercent = percentToNumber(data.usedPercent)
          data.sharedPercent = percentToNumber(data.sharedPercent)
          if (flag === 'edit' || flag === 'add') {
            if (this.form.mouldHeaderId) {
              mouldheader.update(data).then(async res => {
                this.$message({
                  type: 'success',
                  message: res.message
                })
                await this.getDetail(res.data)
                await this.handlerAfter(type)
                // 切换到工作流tab页
                var workflowMode =
                  this.workflowParamsInfo.integrationMode === 'Product' ||
                  this.workflowParamsInfo.integrationMode === 'Iframe' ||
                  this.workflowParamsInfo.integrationMode === 'Self'
                if (workflowMode) {
                  this.activeTabName = 'workflowTab'
                }
              })
            } else {
              mouldheader.add(data).then(async res => {
                this.$message({
                  type: 'success',
                  message: res.message
                })
                await this.getDetail(res.data)
                await this.handlerAfter(type)
                // 切换到工作流tab页
                var workflowMode =
                  this.workflowParamsInfo.integrationMode === 'Product' ||
                  this.workflowParamsInfo.integrationMode === 'Iframe' ||
                  this.workflowParamsInfo.integrationMode === 'Self'
                if (workflowMode) {
                  this.activeTabName = 'workflowTab'
                }
              })
            }
          } else if (flag === 'update') {
            // 如果是模具变更
            this.addHistory(data, type)
          }
        } else {
          this.__focus_error__()
        }
      })
    },
    // 检查物料的时间范围是否合规
    checkItemTime () {
      // this.realDataSource 校验如果是一模多料
      if (this.form.mouldItemType === '3') {
        for (let i = 0; i < this.realDataSource.length; i++) {
          if (this.realDataSource[i].shareEndTime <= this.realDataSource[i].shareStartTime) {
            return this.__jump_error__(
              'mouldItemType',
              null,
              this.$t('mould.timeLimit'),
            )
          }
        }
        if (this.form.itemProductType === '1') {
          // 1.同一次产出  2.非同一次产出
          // 开始时间必须一样
          const checkList = this.realDataSource.map(item => {
            return item.shareStartTime
          })
          const checkTempList = []
          checkList.forEach(item => {
            if (checkTempList.indexOf(item) === -1) {
              checkTempList.push(item)
            }
          })
          if (checkTempList.length > 1) {
            // 同一次产出,分摊开始时间必须相同,请检查参数
            return this.__jump_error__(
              'mouldItemType',
              null,
              this.$t('mould.checkParams'),
            )
          }
        } else if (this.form.itemProductType === '2') {
          // 时间范围不能重叠,且开始时间和结束时间必填
          const startTimeList = this.realDataSource.map(item => {
            return item.shareStartTime
          })
          // eslint-disable-next-line no-prototype-builtins
          const endTimeList = this.realDataSource
            .filter(
              item =>
                item.hasOwnProperty('shareEndTime') &&
                item.shareEndTime !== null &&
                item.shareEndTime !== '',
            )
            .map(item => {
              return item.shareEndTime
            })
          if (startTimeList.length > endTimeList.length) {
            // 非同一次产出,分摊开始和分摊结束时间都必填,请检查参数
            return this.__jump_error__(
              'mouldItemType',
              null,
              this.$t('mould.paramsRequire'),
            )
          }
          //   for (let i = 1; i < startTimeList.length; i++) {
          //     if (startTimeList[i] <= endTimeList[i - 1]) {
          //       // 非同一次产出,时间范围有重叠,请检查参数
          // return this.__jump_error__(
          //   'mouldItemType',
          //   null,
          //   this.$t('mould.timeFramesOverlap'),
          // )
          //     }
          //   }i
          for (let i = 0; i < this.realDataSource.length; i++) {
            for (let j = i + 1; j < this.realDataSource.length; j++) {
              let flag1 = this.realDataSource[j].shareStartTime > this.realDataSource[i].shareStartTime && this.realDataSource[j].shareStartTime < this.realDataSource[i].shareEndTime
              let flag2 = this.realDataSource[j].shareEndTime > this.realDataSource[i].shareStartTime && this.realDataSource[j].shareEndTime < this.realDataSource[i].shareEndTime
              let flag3 = this.realDataSource[j].shareStartTime < this.realDataSource[i].shareStartTime && this.realDataSource[j].shareEndTime > this.realDataSource[i].shareStartTime
              let flag4 = this.realDataSource[j].shareEndTime > this.realDataSource[i].shareEndTime && this.realDataSource[j].shareStartTime < this.realDataSource[i].shareEndTime
              if (flag1 || flag2 || flag3 || flag4) {
                return this.__jump_error__(
                  'mouldItemType',
                  null,
                  this.$t('mould.timeFramesOverlap'), // 非同一次产出,时间范围有重叠,请检查参数
                )
              }
            }
          }
        }
      }
      return true
    },
    // 校验 所有物料行中的单次产出数量相加（不能多于模穴数）
    checkMouldHoleNumber () {
      let tempNum = 0
      this.realDataSource.forEach(item => {
        tempNum = item.productNum + tempNum
      })
      if (tempNum > this.form.mouldHoleNumber) {
        // 所有物料行中的单次产出数量相加（不能多于模穴数）
        return this.__jump_error__(
          'mouldItemType',
          null,
          this.$t('mould.noMoreNumber'),
        )
      }
      return true
    },
    addHistory (data, type) {
      const { firstFlag } = this.$attrs.params
      if (!firstFlag) {
        mouldheader.updateMouldHistory(data).then(async res => {
          this.$message({
            type: 'success',
            message: res.message
          })
          await this.getHistoryDetail(res.data)
          await this.handlerAfter(type)
        })
      } else {
        mouldheader.addMouldHistory(data).then(async res => {
          this.$message({
            type: 'success',
            message: res.message
          })
          await this.getHistoryDetail(res.data)
          await this.handlerAfter(type)
        })
      }
    },
    cancelBill () {
      const { flag, row } = this.$attrs.params
      if (flag === 'add') {
        this.$emit('tab-remove', 'mouldheaderEdit')
      } else {
        this.$emit('tab-remove', 'mouldheaderEdit' + row.mouldHeaderId)
      }
      this.__setTabTodo('mouldheaderList.getQuerydata')
    }
  }
}
</script>

<style scoped lang="scss">
.mouldheaderEdit {
  height: 100%;
  padding-bottom: 50px;

  :deep(.table-wrapper) {
    padding-left: 0;
    padding-right: 0;
  }

  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }

  .el-table .el-date-editor {
    width: 120px;
  }

  .base-form {
    padding: 15px 30px 0;
  }

  .toRequired {
    color: #ff4949;
    padding-right: 2px;
  }

  .edit_cond {
    color: #23adf4;
    cursor: pointer;
  }
}
</style>
