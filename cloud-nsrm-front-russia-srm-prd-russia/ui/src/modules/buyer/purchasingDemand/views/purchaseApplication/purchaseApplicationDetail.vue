<template>
  <el-container class="flex-container the-purchaseApplicationDetail-detail" direction="vertical">
    <el-main>
      <CWorkflowMulti
        ref="workflowMulti"
        v-model="activeTabName"
        :fun-params="workflowParamsInfo"
        :button-config-info="buttonConfigInfo"
        :button-custom="buttonCustom"
        @tab-click="workflowView"
        @workflow-handler="workflowHandler"
        @click-handler="type => saveOrSubmitBill(type)"
        @submit-direct="type => saveOrSubmitBill(type)"
        @confirm="(type, comment) => saveOrSubmitBill(type, comment)"
        @close-tab="back"
      >
        <div class="form-container2">
          <el-form
            ref="requirementHeadRef"
            :model="requirementHead"
            label-width="80px"
            label-position="top"
            :rules="rules"
          >
            <el-collapse v-model="activeDims" class="tab-form-style">
              <!-- 采购申请详情 -->
              <el-collapse-item ref="aptInfo" :title="$t('purchaseDemand.purAppDetail')" name="1">
                <srm-row>
                  <!-- 需求类型 -->
                  <srm-col>
                    <el-form-item
                      :label="$t('purchaseDemand.demandType')"

                      prop="demandType"
                    >
                      <dict-select
                        v-model="requirementHead.demandType"
                        code="DEMAND_TYPE"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        @change="handleTypeChange"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 申请编号 -->
                    <el-form-item
                      :label="$t('purchaseDemand.requirementHeadNum')"
                    >
                      <el-input v-model="requirementHead.requirementHeadNum" disabled />
                    </el-form-item>
                  </srm-col>

                  <srm-col>
                    <!-- 采购类型 -->
                    <el-form-item
                      :label="$t('purchaseDemand.purchaseType')"

                      prop="ceeaPurchaseType"
                    >
                      <dict-select
                        v-model="requirementHead.ceeaPurchaseType"
                        code="PURCHASE_TYPE"
                        :disabled="
                          isReadOnly ||
                            requirementHead.auditStatus === 'APPROVED' ||
                            !!allRequirementLineList.length
                        "
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 申请日期 -->
                    <el-form-item
                      prop="applyDate"
                      :label="$t('purchaseDemand.applyDate')"
                    >
                      <el-date-picker
                        v-model="requirementHead.applyDate"
                        disabled
                        type="date"
                        value-format="yyyy-MM-dd"
                      />
                    </el-form-item>
                  </srm-col>

                  <srm-col>
                    <!-- 单据状态 -->
                    <el-form-item
                      :label="$t('purchaseDemand.applyStatus')"
                    >
                      <dict-select
                        v-model="requirementHead.auditStatus"
                        code="APPROVAL_STATUS"
                        disabled
                      />
                    </el-form-item>
                  </srm-col>

                  <srm-col>
                    <!-- 业务实体 -->
                    <el-form-item
                      :label="$t('purchaseDemand.businessEntity')"

                      prop="orgId"
                    >
                      <el-input
                        v-if="isReadOnly"
                        v-model="requirementHead.orgName"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                      />
                      <template v-else>
                        <OrganizationSelector
                          ref="organizationSelector"
                          v-model="requirementHead.orgId"
                          :parent-id="-1"
                          node-type="OU"
                          :placeholder="$t('common.pleaseSelect')"
                          :disabled="
                            isReadOnly ||
                              requirementHead.auditStatus === 'APPROVED' ||
                              !!allRequirementLineList.length
                          "
                          @select="selectHandler"
                        />
                      </template>
                    </el-form-item>
                  </srm-col>

                  <srm-col>
                    <!-- 库存组织 -->
                    <el-form-item
                      :label="$t('purchaseDemand.invOrg')"

                      prop="organizationId"
                    >
                      <el-input
                        v-if="isReadOnly"
                        v-model="requirementHead.organizationName"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                      />
                      <!-- auto-select-when-one-item -->
                      <OrganizationSelector
                        v-else
                        ref="organizationSelector2"
                        v-model="requirementHead.organizationId"
                        :parent-id="requirementHead.orgId"
                        node-type="INV"
                        :placeholder="$t('common.pleaseSelect')"
                        :disabled="
                          isReadOnly ||
                            requirementHead.auditStatus === 'APPROVED' ||
                            !!allRequirementLineList.length
                        "
                        @select="selectHandler2"
                      />
                    </el-form-item>
                  </srm-col>

                  <srm-col>
                    <!-- 申请部门 -->
                    <el-form-item
                      :label="$t('purchaseDemand.ceeaDepartment')"
                    >
                      <el-input v-model="requirementHead.ceeaDepartmentName" disabled />
                    </el-form-item>
                  </srm-col>

                  <srm-col>
                    <!-- 申请人 -->
                    <el-form-item
                      :label="$t('purchaseDemand.applicant')"
                    >
                      <!-- :disabled="requirementHead.auditStatus === 'APPROVED'" -->
                      <QuickSearch
                        disabled
                        :show-input="requirementHead.createdFullName"
                        show-key="nickname"
                        :scope-data="requirementHead"
                        name="scc_rbac_user_display"
                        @close-quicksearch="getUserObj"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 物料大类 -->
                    <el-form-item
                      :label="$t('purchaseDemand.materialCate')"

                      prop="categoryName"
                    >
                      <QuickSearch
                        ref="category"
                        :show-input="requirementHead.categoryName"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        show-key="categoryId"
                        auto-query
                        name="scc_base_purchase_category3"
                        @close-quicksearch="getCategoryObj"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 采购项目 -->
                    <el-form-item
                      :label="$t('purchaseDemand.purchaseItem')"

                      prop="purchaseProject"
                    >
                      <el-input
                        v-model="requirementHead.purchaseProject"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="requirementHead.demandType === 'NONPRODUCTIVE_DEMAND'">
                    <!-- 预算编号 -->
                    <el-form-item
                      :label="$t('purchaseDemand.budgetNumber')"
                    >
                      <QuickSearch
                        ref="budgetNumRef"
                        :show-input="requirementHead.budgetManagementNum"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        show-key="budgetManagementNumber"
                        auto-query
                        name="scc_pb_budget_management_effective"
                        @close-quicksearch="getBudgetNumObj"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="requirementHead.demandType === 'NONPRODUCTIVE_DEMAND'">
                    <!-- 预算金额 -->
                    <el-form-item
                      :label="$t('purchaseDemand.ceeaTotalBudget')"
                    >
                      <el-input v-model="requirementHead.totalBudget" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="requirementHead.demandType === 'NONPRODUCTIVE_DEMAND'">
                    <!-- 实际已用金额 -->
                    <el-form-item
                      :label="$t('purchaseDemand.actualAmountUsed')"
                    >
                      <el-input v-model="requirementHead.usedBudget" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="requirementHead.demandType === 'NONPRODUCTIVE_DEMAND'">
                    <!-- 剩余可用预算 -->
                    <el-form-item
                      :label="$t('purchaseDemand.availableBudget')"
                    >
                      <el-input v-model="requirementHead.unusedBudget" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="1">
                    <el-form-item :label="$t('contractMod.remark')">
                      <el-input
                        v-model="requirementHead.comments"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        type="textarea"
                        :autosize="{ minRows: 2, maxRows: 5 }"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="requirementHead.ceeaPurchaseType === 'URGENT'" :initCol="1">
                    <!-- 紧急情况说明 -->
                    <el-form-item
                      :label="$t('purchaseDemand.ceeaUrgencyExplain')"

                      prop="ceeaUrgencyExplain"
                    >
                      <el-input
                        v-model="requirementHead.ceeaUrgencyExplain"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        type="textarea"
                        :rows="2"
                      />
                    </el-form-item>
                  </srm-col>

                  <srm-col v-if="requirementHead.ceeaPurchaseType === 'APPOINT'" :initCol="1">
                    <!-- 指定原因 -->
                    <el-form-item prop="ceeaAppointReason">
                      <span style="font-size:12px;">
                        <em class="toRequired" style="margin-left:0;">*</em>{{ $t('purchaseDemand.ceeaAppointReason') }}
                      </span>
                      <el-input
                        v-model="requirementHead.ceeaAppointReason"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        type="textarea"
                        :rows="2"
                      />
                    </el-form-item>
                  </srm-col>
                </srm-row>
              </el-collapse-item>
              <!-- 物料明细 -->
              <el-collapse-item ref="itemInfo" :title="$t('purchaseDemand.itemInfo')" name="2">
                <div class="btn_line">
                  <!-- 新增 -->
                  <el-button
                    v-if="requirementHead.auditStatus !== 'APPROVED'"
                    :disabled="isReadOnly"
                    type="primary"
                    class="detail-pbtn"
                    @click="openDialog"
                  >
                    {{ $t('common.add') }}
                  </el-button>

                  <el-tooltip
                    :disabled="isReadOnly || !!requirementHead.requirementHeadNum"
                    :content="$t('purchaseDemand.itemInfoTooltip')"
                    placement="top"
                    effect="dark"
                  >
                    <!-- excel导入 -->
                    <MImport
                      ref="import"
                      style="display: inline-block; margin: 0 10px;"
                      :title="iModal.title"
                      :disabled="isReadOnly || importAbled"
                      :up-load-url="iModal.upLoadUrl"
                      :extra-data="extraData"
                      @beforeUpload="beforeUpload"
                      @downloadTemplate="downloadTemplate"
                      @handleSuccess="handleSuccess"
                    />
                  </el-tooltip>

                  <!-- 导出 -->
                  <el-button
                    v-if="this.$attrs.params.flag !== 'add'"
                    style="margin-left: 0;"
                    type="primary"
                    class="detail-pbtn"
                    :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                    @click="exportFile"
                  >
                    {{ $t('purchaseDemand.export') }}
                  </el-button>

                  <!-- 重新提交 -->
                  <el-button
                    v-if="hideReSubmit"
                    ref="reSubmitRef"
                    :disabled="isReadOnly || !hideReSubmit"
                    type="primary"
                    class="detail-pbtn"
                    @click="reSubmit"
                  >
                    {{ $t('purchaseDemand.resubmit') }}
                  </el-button>
                  <!-- 批量维护 -->
                  <el-button
                    v-if="['edit', 'add'].includes($attrs.params.flag)"
                    type="primary"
                    class="detail-pbtn"
                    @click="materialBatchMaintenance"
                  >
                    {{ $t('vendorMod.batchMaintain') }}
                  </el-button>
                </div>

                <el-table
                  ref="materialDetailRef"
                  :key="detailPag.currentPage"
                  v-loading="tableLoading"
                  row-key="requirementLineId"
                  :data="requirementHead.requirementLineList"
                  style="width: 100%"
                  border
                  :row-height="38"
                  max-height="390px"
                  highlight-current-row
                  @selection-change="handleItemSelection2"
                >
                  <el-table-column id="selectIndex" type="selection" width="50" />
                  <el-table-column
                    align="center"
                    type="index"
                    :label="$t('contractMod.tabindex')"
                    width="50"
                  />
                  <!-- 行状态 -->
                  <el-table-column
                    align="center"
                    prop="applyStatus"
                    :label="$t('purchaseDemand.applicationBankStatus')"
                    width="80"
                    :formatter="formatterStatus"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 物料编码 -->
                  <el-table-column
                    align="center"
                    prop="materialCode"
                    :label="$t('purchaseDemand.itemCode')"
                    width="120"
                    :show-overflow-tooltip="true"
                  >
                    <!-- <template slot="header" slot-scope="scope">
                      <em class="toRequired">*</
                      >{{ $t("purchaseDemand.itemCode") }}
                    </template> -->
                    <template slot-scope="scope">
                      <template>
                        <el-form-item
                          :prop="'requirementLineList.' + scope.$index + '.materialCode'"
                        >
                          {{ scope.row.materialCode }}
                        </el-form-item>
                      </template>
                    </template>
                  </el-table-column>
                  <!-- 物料名称 -->
                  <el-table-column
                    align="center"
                    prop="materialName"
                    :label="$t('purchaseDemand.itemName')"
                    min-width="130"
                    :show-overflow-tooltip="true"
                  >
                    <!-- <template slot="header" slot-scope="scope">
                      <em class="toRequired">*</em
                      >{{ $t("purchaseDemand.itemName") }}
                    </template> -->
                    <template slot-scope="scope">
                      <template>
                        <el-form-item
                          :prop="'requirementLineList.' + scope.$index + '.materialName'"
                        >
                          {{ scope.row.materialName }}
                        </el-form-item>
                      </template>
                    </template>
                  </el-table-column>
                  <!-- 单位 -->
                  <el-table-column
                    align="center"
                    prop="unitCode"
                    :label="$t('purchaseDemand.unitCode')"
                    width="100"
                  >
                    <template slot-scope="scope">
                      <dict-select
                        v-model="scope.row.unitCode"
                        code="unit"
                        :disabled="isReadOnly || (requirementHead.categoryCode !== '40' && requirementHead.categoryCode !== '60')"
                      />
                    </template>
                  </el-table-column>
                  <!-- 需求数量 -->
                  <el-table-column
                    align="center"
                    prop="requirementQuantity"
                    :label="$t('purchaseDemand.requirementQuantity')"
                    width="90"
                  >
                    <template slot="header" slot-scope="scope">
                      <em class="toRequired">*</em>{{ $t('purchaseDemand.requirementQuantity') }}
                    </template>
                    <template slot-scope="scope">
                      <template>
                        <el-form-item
                          :prop="'requirementLineList.' + scope.$index + '.requirementQuantity'"
                          :rules="rules.requirementQuantity"
                        >
                          <el-input-number
                            v-model="scope.row.requirementQuantity"
                            :disabled="
                              isReadOnly ||
                                requirementHead.auditStatus === 'APPROVED' &&
                                scope.row.applyStatus !== 'RETURNING'
                            "
                            :controls="false"
                            :min="0"
                            class="input-number-precision"
                            @focus="getTotalAmount(scope.row.requirementQuantity)"
                            @change="setTotalAmount(scope.row, scope.$index)"
                          />
                        </el-form-item>
                      </template>
                    </template>
                  </el-table-column>
                  <!-- 已下单数量 -->
                  <el-table-column
                    align="center"
                    prop="ceeaExecutedQuantity"
                    :label="$t('purchaseDemand.ceeaExecutedQuantity')"
                    width="100"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 需求日期 -->
                  <el-table-column
                    align="center"
                    prop="requirementDate"
                    :label="$t('purchaseDemand.requirementDate')"
                    width="150"
                  >
                    <template slot="header" slot-scope="scope">
                      <em class="toRequired">*</em>{{ $t('purchaseDemand.requirementDate') }}
                    </template>
                    <template slot-scope="scope">
                      <template>
                        <el-form-item
                          :prop="'requirementLineList.' + scope.$index + '.requirementDate'"
                          :rules="rules.requirementDate"
                        >
                          <el-date-picker
                            v-model="scope.row.requirementDate"
                            type="date"
                            format="yyyy-MM-dd"
                            :disabled="
                              isReadOnly ||
                                requirementHead.auditStatus === 'APPROVED' &&
                                scope.row.applyStatus !== 'RETURNING'
                            "
                            :picker-options="pickerOptions"
                            value-format="yyyy-MM-dd"
                          />
                        </el-form-item>
                      </template>
                    </template>
                  </el-table-column>
                  <!-- 收货地址 -->
                  <el-table-column
                    align="center"
                    prop="receiveAddress"
                    :label="$t('purchaseDemand.ceeaDeliveryPlaceOut')"
                    width="150"
                  >
                    <template slot="header" slot-scope="scope">
                      <em class="toRequired">*</em>{{ $t('purchaseDemand.ceeaDeliveryPlaceOut') }}
                    </template>
                    <template slot-scope="scope">
                      <el-form-item
                        :prop="'requirementLineList.' + scope.$index + '.receiveAddress'"
                        :rules="rules.receiveAddress"
                      >
                        <DictSelect
                          v-model="scope.row.receiveAddress"
                          :disabled="
                            isReadOnly ||
                              requirementHead.auditStatus === 'APPROVED' ||
                              scope.row.applyStatus === 'RETURNING'
                          "
                          :code="requirementHead.organizationId"
                          :custom-select-type="requirementHead.organizationId ? 'RECEIVE_ADDRESS' : ''"
                          @change-value="(val, element) => changeSiteInfo(scope.row, element)"
                        />
                      </el-form-item>
                    </template>
                  </el-table-column>
                  <!-- 明细备注 -->
                  <el-table-column
                    align="center"
                    prop="comments"
                    :label="$t('purchaseDemand.comments')"
                    width="150"
                    :show-overflow-tooltip="true"
                  >
                    <!-- <template slot="header" slot-scope="scope">
                      <em class="toRequired">*</
                      >{{ $t("purchaseDemand.comments") }}
                    </template> -->
                    <template slot-scope="scope">
                      <template>
                        <el-form-item :prop="'requirementLineList.' + scope.$index + '.comments'">
                          <el-input
                            v-model="scope.row.comments"
                            :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                          />
                        </el-form-item>
                      </template>
                    </template>
                  </el-table-column>
                  <!-- 指定供应商 -->
                  <el-table-column
                    align="center"
                    prop="vendorName"
                    :label="$t('purchaseDemand.awardedSupplierName')"
                    width="150"
                    :show-overflow-tooltip="true"
                  >
                    <template slot="header" slot-scope="scope">
                      <em
                        v-if="requirementHead.ceeaPurchaseType === 'APPOINT'"
                        class="toRequired"
                      >*</em>
                      {{ $t('purchaseDemand.awardedSupplierName') }}
                    </template>
                    <template slot-scope="scope">
                      <template>
                        <el-form-item
                          :prop="'requirementLineList.' + scope.$index + '.vendorName'"
                          :rules="[
                            {
                              required: requirementHead.ceeaPurchaseType === 'APPOINT',
                              message: $t('purchaseDemand.selectVendor')
                            }
                          ]"
                        >
                          <QuickSearch
                            :pre-query-data="supplierParam"
                            :show-input="scope.row.vendorName"
                            show-key="companyName"
                            :disabled="
                              isReadOnly ||
                                scope.row.ceeaIfDirectory === 'Y' ||
                                requirementHead.auditStatus === 'APPROVED' ||
                                requirementHead.ceeaPurchaseType !== 'APPOINT'
                            "
                            :scope-data="scope.row"
                            name="scc_sup_company_info_green"
                            @before-open="beforeOpenSuppier(scope.row)"
                            @close-quicksearch="getVendorObj"
                          />
                        </el-form-item>
                      </template>
                    </template>
                  </el-table-column>

                  <!-- 需求部门 -->
                  <el-table-column
                    align="center"
                    prop="dmandLineRequest"
                    :label="$t('purchaseDemand.dmandLineRequest')"
                    width="150"
                    :show-overflow-tooltip="true"
                  >
                    <template slot-scope="scope">
                      <dict-select
                        v-model="scope.row.dmandLineRequest"
                        code="DMAND_LINE_REQUEST"
                        :disabled="
                          isReadOnly ||
                            (requirementHead.auditStatus === 'APPROVED' &&
                            scope.row.applyStatus !== 'RETURNING')
                        "
                      />
                    </template>
                  </el-table-column>
                  <!-- 含税单价 -->
                  <el-table-column
                    v-if="requirementHead.demandType === 'NONPRODUCTIVE_DEMAND'"
                    align="center"
                    prop="notaxPrice"
                    :label="$t('purchaseDemand.priceIncludingTax')"
                    width="100"
                  >
                    <template slot-scope="scope">
                      <el-input
                        v-model="scope.row.notaxPrice"
                        v-input-format="{ type: 'float' }"
                        :disabled="
                          isReadOnly ||
                            scope.row.ceeaIfDirectory === 'Y' ||
                            requirementHead.auditStatus === 'APPROVED'
                        "
                        @change="setTotalAmount(scope.row)"
                      />
                    </template>
                  </el-table-column>
                  <!-- 总金额 -->
                  <el-table-column
                    v-if="requirementHead.demandType === 'NONPRODUCTIVE_DEMAND'"
                    align="center"
                    prop="totalAmount"
                    :label="$t('purchaseDemand.totalAmount')"
                    width="100"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 物料小类 -->
                  <el-table-column
                    align="center"
                    prop="categoryName"
                    :label="$t('purchaseDemand.materialCateSub')"
                    width="120"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 是否目录化 -->
                  <el-table-column
                    align="center"
                    prop="ceeaIfDirectory"
                    :label="$t('purchaseDemand.ceeaIfCatalogMaterial')"
                    width="100"
                    :formatter="formatterIfDirector"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 退回原因 -->
                  <el-table-column
                    align="center"
                    prop="rejectReason"
                    :label="$t('purchaseDemand.returnReason')"
                    width="150"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 删除 -->
                  <el-table-column
                    v-if="requirementHead.auditStatus === 'DRAFT'"
                    :label="$t('common.operation')"
                    width="60"
                    fixed="right"
                  >
                    <template v-if="requirementHead.auditStatus === 'DRAFT'" slot-scope="scope">
                      <el-button :disabled="isReadOnly" type="text" @click="deleteOneContent(scope.$index, scope.row)">
                        {{ $t('common.delete') }}
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <!-- 请检查当前页必填项 -->
                <el-tooltip
                  :offset="100"
                  :disabled="!messagePage"
                  :content="$t('purchaseApplication.prompt1')"
                  placement="top-end"
                >
                  <el-pagination
                    :disabled="messagePage"
                    style="margin-top: 2px"
                    :current-page="detailPag.currentPage"
                    :page-size="detailPag.pageSize"
                    layout="total, prev, pager, next"
                    :total="allRequirementLineList.length"
                    @current-change="handleCurrentChange"
                  />
                </el-tooltip>
              </el-collapse-item>

              <el-collapse-item :title="$t('purSettlementMod.addUploadFile')" name="3">
                <p class="btn_line">
                  <el-button
                    v-if="requirementHead.auditStatus !== 'APPROVED'"
                    :disabled="isReadOnly"
                    type="primary"
                    class="detail-pbtn"
                    @click="addUploadOne"
                  >
                    {{ $t('common.add') }}
                  </el-button>
                </p>
                <el-table :data="requirementAttaches" style="width: 100%" border max-height="250px">
                  <el-table-column
                    align="center"
                    type="index"
                    :label="$t('purSettlementMod.tabindex')"
                    width="50"
                  />
                  <!-- 附件 -->
                  <el-table-column
                    align="center"
                    prop="attachName"
                    :label="$t('purchaseDemand.attachment')"
                  >
                    <template slot-scope="scope">
                      <SrmCommonFile
                        :extra-data="fileInfo"
                        :default-file="{
                          fileId: scope.row.fileuploadId,
                          fileName: scope.row.attachName
                        }"
                        :readonly="false"
                        @on-change="({file}) => outerHandleUploadSuccess(file,scope.row)"
                      />
                    </template>
                  </el-table-column>
                  <!-- 上传人 -->
                  <el-table-column
                    align="center"
                    prop="createdFullName"
                    :label="$t('purchaseDemand.attachmentCreatedBy')"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 账号 -->
                  <el-table-column
                    align="center"
                    prop="createdBy"
                    :label="$t('vendorMod.account')"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 上传时间 -->
                  <el-table-column
                    align="center"
                    prop="creationDate"
                    :label="$t('purchaseDemand.attachmentCreatedDate')"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column :label="$t('common.operation')" width="60">
                    <template slot-scope="scope">
                      <el-button
                        type="text"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
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
      </CWorkflowMulti>
    </el-main>

    <!-- 物料明细新增 -->
    <MaterialSelectDialog
      :visible.sync="dialogVisible"
      :requirementHead="requirementHead"
      :queryForm="queryForm"
      :pageInfo="pageInfo"
      :displayItemTable="displayItemTable"
      @queryContent="queryContent"
      @addOneContent="addOneContent"
      @close="dialogVisible = false"
    />

    <!-- 物料明细 - 批量维护 -->
    <BatchMaintainDialog
      :visible.sync="batchMaintainDialog"
      :requirementHead="requirementHead"
      @close="batchMaintainDialog = false"
      @submit="batchMaintainSubmit"
    />
  </el-container>
</template>
<script>
import _pick from 'lodash/pick'
import OrganizationSelector from 'lib@/components/organization-selector'
import { tabTodoMixin } from '@/utils/mixins'
import QuickSearch from 'lib@/components/QuickSearch'
import { parseTime } from '@/utils'
import CPagination from 'lib@/components/c-pagination'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import WorkflowCommon from '@/library/mixins/workflow-common'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import { getToken } from '@/utils/auth'
import axios from 'axios'
import { getMenuInfo } from '@/utils/menu-auth'
import { sysPrefix } from '@/config/ipConfig'
import { purchaseApplicationApi } from 'modb@/purchasingDemand/api'
import BatchMaintainDialog from 'modb@/purchasingDemand/views/purchaseApplication/components/batchMaintainDialog'
import MaterialSelectDialog from 'modb@/purchasingDemand/views/purchaseApplication/components/materialSelectDialog'

export default {
  name: 'PurchaseApplicationDetail',
  components: {
    MImport,
    QuickSearch,
    CPagination,
    OrganizationSelector,
    BatchMaintainDialog,
    MaterialSelectDialog
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      pageInfo: {
        pageTotal: 0,
        pageIndex: 1,
        pageSize: 15
      },
      detailPag: {
        currentPage: 1,
        pageSize: 15
      },
      copyInit: {
        num: 0,
        bol: true
      }, // 需求数量初始值
      materialParam: {},
      batchMaintainDialog: false, // 物料明细 - 批量维护弹窗
      supplierParam: {},
      lastCategoryId: '',
      plTableColumnRowStyle: {
        boxSizing: 'border-box',
        overflow: 'hidden',
        whiteSpace: 'nowrap',
        textOverflow: 'ellipsis',
        wordBreak: 'break-all'
      },
      extraData: {
        fileModular: 'pm',
        fileFunction: 'purchaseApplication',
        fileType: 'excel'
      },
      iModal: {
        title: this.$t('common.excelImport'),
        upLoadUrl: '/api-sup-ce/pr/requirementLine/v2/import'
      },
      pickerOptions: {
        disabledDate (time) {
          const today = new Date()
          today.setHours(0)
          today.setMinutes(0)
          today.setSeconds(0)
          today.setMilliseconds(0)
          return time.getTime() < today.getTime()
        }
      },
      curRole: this.$store.getters.userType,
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'vendorBiddingManagement',
        fileType: 'images'
      },
      queryForm: {
        categoryCode: null,
        categoryId: null,
        categoryName: null,
        materialCode: null,
        materialId: null,
        materialName: null,
        orgId: null,
        organizationId: null,
        organizationName: null
      },
      requirementHead: {
        budgetManagementNum: '',
        demandType: '',
        requirementLineList: [],
        requirementHeadId: null,
        ceeaPurchaseType: '',
        orgId: '',
        orgCode: '',
        orgName: '',
        createdId: '',
        categoryId: '',
        categoryCode: '',
        categoryName: null,
        organizationId: '',
        organizationCode: '',
        organizationName: '',
        createdFullName: '',
        requirementHeadNum: '',
        auditStatus: 'DRAFT',
        ceeaDepartmentId: '',
        ceeaDepartmentCode: '',
        ceeaDepartmentName: '',
        ceeaPrType: '',
        ceeaProjectName: '',
        ceeaProjectNum: '',
        ceeaProjectUserId: '',
        ceeaProjectUserNickname: '',
        ceeaProjectApprovalNum: '',
        ceeaIfVote: '',
        ceeaVoteProjectName: '',
        ceeaAssetType: '',
        totalBudget: 0,
        usedBudget: 0,
        thisYearBudgetAmount: '0',
        nextYearBudgetAmount: '0',
        ceeaIfHq: '',
        ceeaIfUseLogo: '',
        ceeaUrgencyExplain: '',
        ceeaAppointReason: '',
        comments: '',
        applyDate: parseTime(new Date(), '{y}-{m}-{d}')
      },
      currentPage: 1,
      allRequirementLineList: [],
      activeDims: ['1', '2', '3'],
      rules: {
        vendorName: [{ required: true, message: this.$t('purchaseDemand.selectVendor') }], // 指定供应商
        receiveAddress: [
          { required: true, message: this.$t('purchaseDemand.selectReceiveAddress') }
        ], // 收货地址（仅收货地点）
        requirementDate: [{ required: true, message: this.$t('purchaseDemand.selectRequireDate') }], // 需求日期
        requirementQuantity: [
          { required: true, message: this.$t('purchaseDemand.selectRequireQuantity') }
        ], // 需求数量
        orgId: [{ required: true, message: this.$t('purchaseDemand.orgIdTips') }], // 请选择业务实体
        organizationId: [
          {
            required: true,
            message: this.$t('purchaseDemand.organizationIdTips')
          }
        ], // 请选择库存组织
        ceeaPurchaseType: [
          {
            required: true,
            message: this.$t('purchaseDemand.purchaseTypeTips')
          }
        ], // 请输入采购类型
        categoryName: [
          {
            required: true,
            message: this.$t('purchaseDemand.inputCategoryName')
          } // 请输入物料大类
        ],
        acceptUserName: [
          {
            required: true,
            message: this.$t('purchaseDemand.acceptUserNameTips')
          }
        ], // 请输入验收人
        ceeaUrgencyExplain: [
          {
            required: true,
            message: this.$t('purchaseDemand.ceeaUrgencyExplainTips')
          }
        ], // 请输入紧急情况说明
        demandType: [
          {
            required: true,
            message: this.$t('purchaseDemand.selectRequireType')
          }
        ], // 请选择需求类型
        ceeaAppointReason: [
          {
            validator: (rule, value, callback) => {
              const { ceeaPurchaseType, ceeaAppointReason } = this.requirementHead
              if (ceeaPurchaseType === 'APPOINT' && !ceeaAppointReason) {
                callback(new Error(this.$t('purchaseDemand.ceeaAppointReasonTips')))
              } else {
                callback()
              }
            }
          }
        ]
      },
      isApprovalOnly: this.$attrs.params.flag === 'approvalOnly',
      dialogVisible: false,
      displayItemTable: [],
      requirementAttaches: [],
      multipleSelection: [],
      multipleSelection2: [],
      Viewflag: '',
      messagePage: false,
      tableLoading: false
    }
  },
  computed: {
    hideReSubmit () {
      return this.allRequirementLineList.find(v => v.applyStatus === 'RETURNING')
    },
    importAbled () {
      return (
        this.requirementHead.auditStatus === 'APPROVED' ||
        !this.requirementHead.categoryId ||
        !this.requirementHead.organizationId ||
        !this.requirementHead.orgId
      )
    },
    viewUpdateButton () {
      return (
        this.curRole === 'BUYER' &&
        !this.isReadOnly &&
        this.requirementHead.auditStatus !== 'APPROVED'
      )
    },
    disabledUpdateButton () {
      return (
        this.requirementHead.auditStatus === 'SUBMITTED' ||
        this.requirementHead.auditStatus === 'APPROVING'
      )
    },
    workflowBusinessId () {
      return this.requirementHead ? this.requirementHead.requirementHeadId : null
    },
    workflowTabDisabled () {
      return !this.isApprovalOnly
    },
    isReadOnly () {
      return !['add', 'edit'].includes(this.$attrs.params.flag)
    }
  },
  watch: {
    viewUpdateButton () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
    },
    disabledUpdateButton () {
      this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton
    }
  },
  created () {
    this.Viewflag = this.$attrs.params.flag
    if (this.Viewflag === 'approveNumber') this.workflowParamsInfo.tabDisabled = false
    if (this.$attrs.params.flag === 'add') {
      const { nickname, ceeaDeptId, department } = this.$store.getters.userInfo
      this.requirementHead.createdFullName = nickname
      this.requirementHead.ceeaDepartmentId = ceeaDeptId
      this.requirementHead.ceeaDepartmentName = department
    } else {
      this.getFormDetail(this.$attrs.params.row.requirementHeadId)
    }
    this.getButtonConfig()
  },
  methods: {
    getButtonConfig () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
      this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.cancel.view = !this.isReadOnly
      this.buttonConfigInfo.close.view = this.isReadOnly
    },
    handleTypeChange (val) {
      // 不是非生产性需求清空预算编号
      if (this.requirementHead.demandType !== 'NONPRODUCTIVE_DEMAND') {
        this.requirementHead.budgetManagementNum = null
        this.requirementHead.budgetManagementId = null
        this.requirementHead.totalBudget = null
        this.requirementHead.usedBudget = null
        this.requirementHead.unusedBudget = null
      }
    },
    // 物料明细批量维护
    batchMaintainSubmit (form) {
      this.multipleSelection2.forEach(row => {
        Object.keys(form).forEach(key => {
          this.$set(row, key, form[key])
        })
      })
      this.batchMaintainDialog = false
    },
    // 批量维护按钮
    materialBatchMaintenance () {
      if (this.multipleSelection2.length < 1) {
        this.$message.warning(this.$t('purchaseDemand.selectAtLeastOneData'))
        return
      }
      this.batchMaintainDialog = true
    },
    // 收货地点选择
    changeSiteInfo (row, { element }) {
      this.$set(row, 'receiveContact', element ? element.receiver : '')
      this.$set(row, 'receiveTelephone', element ? element.receiverPhone : '')
      this.$set(row, 'receiveAddress', element ? element.siteName : '')
    },
    // 指定供应商绿牌  参数添加
    beforeOpenSuppier (row) {
      this.supplierParam['c.CATEGORY_ID'] = row.categoryId
    },
    // 物料明细导出
    async exportFile () {
      const Authorization = getToken() ? 'Bearer ' + getToken() : '' // token
      let menuInfo = getMenuInfo()
      const res = await axios({
        method: 'POST',
        url: `${sysPrefix()}/api-sup-ce/pr/requirementLine/excelExport?requirementHeadId=${
          this.$attrs.params.row.requirementHeadId
        }`,
        headers: {
          Authorization: Authorization,
          'X-Fun-Info': menuInfo.secretKey
        },
        responseType: 'blob'
      })
      if (!res) return
      if (res.data.type === 'application/json') {
        throw new Error(this.$t('purchaseDemand.downloadFail'))
      }
      const content = res.data
      const blob = new Blob([content])
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.style.display = 'none'
      link.href = url
      link.rel = 'noopener'
      link.setAttribute(
        'download',
        window.decodeURI(res.headers['content-disposition'].split('=')[1]),
      )
      document.body.appendChild(link)
      link.click()
      document.body.removeChild()
    },
    async getWorkflowBusinessType () {
      return 'REQUIREMENT'
    },
    async getWorkflowBusinessVariables () {
      return {
        Amount: this.requirementHead.totalBudget
      }
    },
    getCWorkflowRefName () {
      return 'workflowMulti'
    },
    beforeUpload () {
      const extraData = this.extraData
      const header = _pick(this.requirementHead, [
        'categoryCode',
        'categoryId',
        'categoryName',
        'ceeaAssetType',
        'ceeaProjectUserNickname',
        'ceeaDepartmentName',
        'ceeaPurchaseType',
        'orgId',
        'orgName',
        'orgCode',
        'organizationCode',
        'organizationId',
        'organizationName',
        'requirementHeadId',
        'requirementHeadNum'
      ])
      this.extraData = { ...header, ...extraData }
    },
    handleSuccess (data) {
      // 导入成功就刷新界面
      if (data.status === 'Y') {
        data.data.forEach(row => {
          this.allRequirementLineList.unshift(row)
          this.handleCurrentChange()
          this.setTotalAmount(row)
        })
      }
    },
    downloadTemplate () {
      downloadFileLink(
        '/api-sup-ce/pr/requirementLine/v2/downloadTemplate',
        this.$t('purchaseDemand.importMaterialItemModelDownload'),
      ).catch(() => {
        this.$message.error(this.$t('purchaseDemand.downloadFail'))
      })
    },
    async handleCurrentChange (val = 1) {
      this.tableLoading = true
      this.$set(this.detailPag, 'currentPage', val)
      this.$set(this.detailPag, 'pageSize', 15)

      this.$set(this.requirementHead, 'requirementLineList', this.allRequirementLineList.slice(
        (val - 1) * 15,
        val * 15
      ))
      setTimeout(() => {
        this.tableLoading = false
        this.multipleSelection2 = []
      })
      this.$forceUpdate()
    },
    async getFormDetail (requirementHeadId) {
      const data = await this.$http({
        url: '/api-sup-ce/pr/requirementHead/getByHeadId',
        method: 'GET',
        params: { requirementHeadId: requirementHeadId },
        loading: true
      })
      if (data.data) {
        this.requirementHead = data.data.requirementHead || {}
        this.requirementAttaches = data.data.requirementAttaches
        let list = data.data.requirementLineList.map(
          ({ ceeaDeliveryPlace, ...rest }) => {
            let d = null
            try {
              if (ceeaDeliveryPlace) {
                d = JSON.parse(ceeaDeliveryPlace)
              }
            } catch (e) {
              console.log(e)
            }
            return {
              ...rest,
              ceeaDeliveryPlace: d
            }
          },
        )
        this.requirementHead.requirementLineList = list
        this.allRequirementLineList = list
        this.handleCurrentChange(1)
        this.lastCategoryId = this.requirementHead.categoryId
      }
    },
    // 删除
    deleteOneContent (index, row) {
      let i = (this.detailPag.currentPage - 1) * 15 + index
      this.allRequirementLineList.splice(i, 1)
      this.handleCurrentChange(this.detailPag.currentPage)
      this.$forceUpdate()
      if (row.notaxPrice) {
        this.requirementHead.totalBudget = this.requirementHead.totalBudget - row.totalAmount
      }
    },
    addUploadOne () {
      this.requirementAttaches.push({
        attachId: null,
        fileuploadId: null,
        attachName: ''
      })
    },
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
    },
    handleItemSelection2 (val) {
      this.multipleSelection2 = val
    },
    outerHandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '', createdBy = '', creationDate = '' } = file || {}
      row.fileuploadId = fileId.toString()
      row.attachName = fileName
      row.createdBy = createdBy
      row.creationDate = creationDate
    },
    // 行删除
    handleDelClick (index, row) {
      this.requirementAttaches.splice(index, 1)
    },
    // 重置对象条件
    resetForm (form) {
      for (let i in form) {
        form[i] = ''
      }
    },
    async openDialog () {
      if (!this.requirementHead.orgId || !this.requirementHead.organizationId) {
        return this.$message.warning(this.$t('purchaseDemand.openDialogWarning1'))
      }
      if (!this.requirementHead.categoryName) {
        return this.$message.warning(this.$t('purchaseDemand.openDialogWarning2'))
      }
      this.resetForm(this.queryForm)
      this.queryForm.organizationName = this.requirementHead.organizationName
      this.queryForm.inputLevel = this.requirementHead.categoryName
      await this.queryContent(this.queryForm)
      this.dialogVisible = true
    },
    reSubmit () {
      if (this.multipleSelection2.length === 0) {
        this.$message({
          type: 'warning',
          message: this.$t('purchaseDemand.pleaseSelectDetailRow')
        })
        return
      }
      for (const item of this.multipleSelection2) {
        if (item.applyStatus !== 'RETURNING') {
          this.$message({
            type: 'warning',
            message: this.$t('purchaseDemand.reSubmitTips1')
          })
          return
        }
      }
      // const params = this.multipleSelection2.map(v => v.requirementLineId);
      const params = {
        requirementHead: this.requirementHead,
        requirementLineList: this.multipleSelection2
      }
      this.$http({
        url: '/api-sup-ce/pr/requirementLine/resubmit',
        method: 'POST',
        data: params,
        loading: true
      })
        .then(data => {
          this.$message.success(this.$t('common.success'))
          this.getFormDetail(this.$attrs.params.row.requirementHeadId)
        })
        .catch(err => {
          console.log(err)
        })
    },
    queryContent (queryForm) {
      this.$http({
        url: '/api-base/material/materialItem/listMaterialByPurchaseCategory',
        method: 'POST',
        data: {
          categoryId: queryForm.categoryId || this.requirementHead.categoryId,
          materialCode: queryForm.materialCode,
          materialName: queryForm.materialName,
          organizationId: this.requirementHead.organizationId,
          organizationName: this.requirementHead.organizationName,
          ceeaPurchaseType: this.requirementHead.ceeaPurchaseType,
          pageSize: queryForm.pageSize || 15,
          pageNum: queryForm.pageNum || 1
        },
        laoding: true
      }).then(data => {
        if (data && data.data) {
          this.displayItemTable = data.data.list
          this.pageInfo.pageTotal = data.data.total
        }
      })
    },
    selectHandler (node, value, scope) {
      this.requirementHead.orgId = node ? node.organizationId : null
      this.requirementHead.orgCode = node ? node.organizationCode : null
      this.requirementHead.orgName = node ? node.organizationName : null

      if (!this.requirementHead.organizationId) return
      this.requirementHead.organizationId = null
      this.requirementHead.organizationName = null
      this.requirementHead.organizationCode = null
    },
    selectHandler2 (node, value, scope) {
      this.requirementHead.organizationId = node ? node.organizationId : null
      this.requirementHead.organizationCode = node ? node.organizationCode : null
      this.requirementHead.organizationName = node ? node.organizationName : null
    },
    addOneContent (multipleSelection) {
      if (multipleSelection.length === 0) {
        return
      }

      const obj = {}
      this.$refs.materialDetailRef.columns.forEach(headers => {
        // 物料明细表所有属性置空初始化
        obj[headers.property] = null
      })

      const dataMap = multipleSelection.map(item => {
        const selectItem = {
          ...item,
          unit: item.unitName,
          unitCode: item.unit,
          totalAmount: (item.notaxPrice || 0) * (item.requirementQuantity || 0),
          ceeaIe: item.ceeaIfDirectory === 'Y' ? 'true' : 'false',
          businessSmall: this.requirementHead.businessSmall,
          // ceeaBusinessSmallCode: this.requirementHead.ceeaBusinessSmallCode
          requirementQuantity: undefined,
          orgId: this.requirementHead.orgId,
          orgCode: this.requirementHead.orgCode,
          orgName: this.requirementHead.orgName,
          organizationId: this.requirementHead.organizationId,
          organizationCode: this.requirementHead.organizationCode,
          organizationName: this.requirementHead.organizationName
        }
        return selectItem
      })
      // 拿到最终的添加数据数组
      const newArr = dataMap.map(row => {
        const o = JSON.parse(JSON.stringify(obj))
        Object.keys(row).forEach(key => {
          this.$set(o, key, row[key])
        })
        return o
      })

      this.allRequirementLineList.unshift(...newArr)
      this.handleCurrentChange()
      this.dialogVisible = false
    },
    checkMaterialList (categoryId) {
      return new Promise(resolve => {
        if (
          this.allRequirementLineList.length &&
          this.lastCategoryId &&
          this.lastCategoryId !== categoryId
        ) {
          this.$confirm(
            this.$t('purchaseDemand.checkMaterialListConfirm'),
            this.$t('common.tips'),
            {
              confirmButtonText: this.$t('common.confirm'),
              cancelButtonText: this.$t('common.cancel'),
              type: 'warning'
            },
          )
            .then(() => {
              this.allRequirementLineList = []
              this.handleCurrentChange()
              this.$message.info(this.$t('purchaseDemand.checkMaterialListTips1'))
              resolve(true)
            })
            .catch(() => {
              this.$message.info(this.$t('purchaseDemand.checkMaterialListTips2'))
              resolve(false)
            })
        } else {
          resolve(true)
        }
      })
    },
    // 预算编号快查关闭回写值
    getBudgetNumObj (val) {
      this.requirementHead.budgetManagementNum = val ? val.budgetManagementNumber : ''
      this.requirementHead.budgetManagementId = val ? val.budgetManagementId : ''
    },
    async getCategoryObj (val, scope) {
      const flag = await this.checkMaterialList(val ? val.categoryId : '')
      if (flag) {
        this.requirementHead.categoryId = val ? val.categoryId : ''
        this.requirementHead.categoryCode = val ? val.categoryCode : ''
        this.requirementHead.categoryName = val ? val.categoryName : ''
        this.lastCategoryId = val ? val.categoryId : ''
      } else {
        const { categoryId, categoryName, categoryCode } = this.requirementHead
        this.$refs.category.setValue({
          categoryId,
          categoryName,
          categoryCode
        })
      }
    },
    getTotalAmount (n) {
      // 存起来一个初始需求数量
      if (this.copyInit.bol) {
        this.copyInit.num = +n
        this.copyInit.bol = false
      }
    },
    async setTotalAmount (row, index) {
      const formFiled = await this.formValidate('requirementHeadRef')
      const fieldKeys = Object.keys(formFiled.obj)
      if (!formFiled.flag && fieldKeys.length > 0 && fieldKeys[0].includes('requirementLineList')) {
        this.messagePage = true
      } else {
        this.messagePage = false
      }

      if (row.applyStatus === 'RETURNING' && row.requirementQuantity >= this.copyInit.num) {
        row.requirementQuantity = this.copyInit.num
        return this.$message.warning(this.$t('purchaseDemand.mustBeLessEqual'))
      }
      if (row.requirementQuantity <= 0) {
        return this.$message.warning(this.$t('purchaseDemand.setTotalAmountTips1'))
      }
      row.totalAmount = Number(
        Number(row.notaxPrice || 0) * Number(row.requirementQuantity || 0),
      ).toFixed(2)
      console.log(row.totalAmount, 'row.totalAmount')
      setTimeout(() => {
        const totalAmountArr = this.allRequirementLineList.map(v => v.totalAmount || 0)
        const totalBudget = totalAmountArr.reduce((p, c) => (Number(p) || 0) + (Number(c) || 0))
        this.$set(this.requirementHead, 'totalBudget', totalBudget)
        this.requirementHead.nextYearBudgetAmount =
          this.requirementHead.totalBudget - this.requirementHead.thisYearBudgetAmount
      }, 100)
    },
    getUserObj (val, scope) {
      scope.createdId = val ? val.userId : ''
      scope.createdFullName = val ? val.nickname : ''
    },
    setBudget (val) {
      if (
        Number(this.requirementHead.thisYearBudgetAmount) > Number(this.requirementHead.totalBudget)
      ) {
        return this.$message.error(this.$t('purchaseDemand.setBudgetTips1'))
      }
      setTimeout(() => {
        this.requirementHead.nextYearBudgetAmount =
          this.requirementHead.totalBudget - this.requirementHead.thisYearBudgetAmount
      }, 100)
    },
    formatterStatus (row, column, cellValue, index) {
      return this.$getDictLabel('APPLICATION_STATUS', cellValue)
    },
    formatterIfDirector (row, column, cellValue, index) {
      return cellValue === 'Y' ? this.$t('common.yes') : this.$t('common.no')
    },
    // 提交校验
    async submitCheck () {
      if (this.allRequirementLineList.length === 0) {
        return this.__jump_error__(
          'materialDetailRef',
          null,
          this.$t('purchaseDemand.saveBillTips5'),
        )
      }

      if (this.requirementHead.ceeaPurchaseType === 'APPOINT') {
        let confirmSelectValue = await this.$confirm(
          // 此申请为指定采购，确认则提交采购申请单！
          this.$t('purchaseDemand.saveBillConfirm1'),
          this.$t('common.tips'),
          {
            confirmButtonText: this.$t('common.confirm'),
            cancelButtonText: this.$t('common.cancel'),
            type: 'warning'
          },
        )
        if (confirmSelectValue !== 'confirm') return false
      }

      let isConfirm = await this.$confirm(
        this.$t('purchaseDemand.clickConfirmToApproval'), // 点击确认则跳转到审批
        this.$t('common.tips'),
        {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        },
      )
      return isConfirm === 'confirm'
    },
    async submitEvent (allparam) {
      const sign = await this.submitCheck()
      if (!sign) return

      this.$http({
        url: '/api-sup-ce/pr/requirementHead/submitPurchaseRequirement',
        method: 'POST',
        data: allparam,
        loading: true
      })
        .then(async data => {
          this.$message({
            message: this.$t('common.success'),
            type: 'success'
          })
          await this.getFormDetail(data.data)
          this.back()
        })
        .catch(err => {
          console.log(err) // 预算告警忽略标识---Y
          this.requirementHead.budgetIgnore = 'Y'
        })
    },
    saveBill (allparam) {
      let saveUrl = this.requirementHead.requirementHeadId
        ? '/api-sup-ce/pr/requirementHead/modifyPurchaseRequirement'
        : '/api-sup-ce/pr/requirementHead/addPurchaseRequirement'

      this.$http({
        url: saveUrl,
        method: 'POST',
        data: allparam,
        loading: true
      })
        .then(async data => {
          this.$message.success(this.$t('common.success'))
          await this.getFormDetail(data.data)
        })
        .catch(err => {
          console.log(err)
        })
    },
    // form验证返回promise校验返回trun or false
    formValidate (formRef) {
      return new Promise(resolve => {
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
      const formFiled = await this.formValidate('requirementHeadRef')

      if (!formFiled.flag && Object.keys(formFiled.obj).length > 0) {
        const warnObj = Object.keys(formFiled.obj)[0]
        return {
          flag: formFiled.flag,
          message: formFiled.obj[warnObj][0].message
        }
      }
      return { flag: true }
    },
    // 抽离保存，提交方法校验及其他数据处理
    saveOtherDeal () {
      if (this.requirementHead.ceeaPrType === '01' && this.requirementAttaches.length === 0) {
        this.$message.error(this.$t('purchaseDemand.saveBillTips3'))
        return
      }
      if (
        Number(this.requirementHead.thisYearBudgetAmount) > Number(this.requirementHead.totalBudget)
      ) {
        return this.$message.error(this.$t('purchaseDemand.saveBillTips4'))
      }
      this.requirementHead.nextYearBudgetAmount =
        this.requirementHead.totalBudget - this.requirementHead.thisYearBudgetAmount

      this.allRequirementLineList.map(v => {
        v.orgId = this.requirementHead.orgId
        v.orgCode = this.requirementHead.orgCode
        v.orgName = this.requirementHead.orgName
      })
    },
    async saveOrSubmitBill (type) {
      const { flag, message } = await this.getCheckForm()
      if (flag) {
        await this.saveOtherDeal()
        const allparam = {
          requirementHead: this.requirementHead,
          requirementAttaches: this.requirementAttaches.filter(item => !!item.fileuploadId),
          requirementLineList: this.allRequirementLineList,
          auditStatus: type === 'SUBMIT' ? 'SUBMITTED' : 'DRAFT'
        }

        if (type === 'SUBMIT') {
          this.submitEvent(allparam)
        } else {
          this.saveBill(allparam)
        }
      } else {
        this.__focus_error__(message)
      }
    },
    back () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('purchaseApplicationList.getQuerydata')
    }
  }
}
</script>
<style scoped lang="scss">
.list-page-query :deep(.el-form-item__label) {
  text-align: right !important;
}
.the-purchaseApplicationDetail-detail {
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .input-with-select .el-input-group__prepend {
    background-color: #fff;
  }
  .isDisabledimport {
    pointer-events: none;
    opacity: 0.5;
  }
  .the_btn_wrapper {
    display: inline-block;
    width: 111px;
  }
  .btn_line {
    display: flex;
    margin: 0 0 8px 0;
  }
  .el-tooltip :deep(.el-button) {
    min-width: 56px;
    font-size: 14px;
    border-radius: 2px;
    padding: 8px 16px;
  }
  .topComment {
    margin-top: 15px;
    text-align: right;
  }
  .input-number-precision {
    width: 100%;
    :deep(.el-input__inner) {
      text-align: left;
      padding-left: 8px;
    }
  }
}
:deep(.el-table td.el-table__cell .el-form-item__content) {
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}
.toRequired {
  color: #ff4949;
  padding-right: 2px;
}
</style>
