<template>
  <el-container class="flex-container the-quaOfReviewDetail-detail" direction="vertical">
    <el-main>
      <CWorkflowMulti
        ref="workflowMulti"
        v-model="activeTabName"
        :funParams="workflowParamsInfo"
        :buttonConfigInfo="buttonConfigInfo"
        @tab-click="workflowView"
        @workflow-handler="workflowHandler"
        @click-handler="(type) => save(type)"
        @submit-direct="(type) => save(type)"
        @confirm="(type, comment) => save(type, comment)"
        @close-tab="back"
        @afterProcessActionSuccess="afterProcessActionSuccess"
      >
        <!-- 单据保存以后才显示 -->
        <div v-if="curOrderId" class="vendorAccessSteps">
          <VendorAccessSteps access-type="qua" :approve-status="orderStatus" />
        </div>
        <el-collapse v-model="activeDims" class="tab-form-style">
          <!-- 资质审查单 -->
          <el-collapse-item :title="$t('vendorMod.quaOrderInfo')" name="1">
            <el-form
              ref="quaForm"
              :model="allParams.reviewForm"
              :rules="rules"
              class="form-fill-style"
              :disabled="curOpt === 'view'"
            >
              <srm-row :gutter="32">
                <srm-col>
                  <!-- 资质审查类型 -->
                  <el-form-item :label="$t('vendorMod.quaType')" prop="quaReviewType">
                    <DictSelect
                      v-model="allParams.reviewForm.quaReviewType"
                      code="QUA_REVIEW_TYPE"
                      @change="getQuaReviewType"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 供应商名称 -->
                  <el-form-item :label="$t('common.vendorName')" prop="vendorId">
                    <QuickSearch
                      :show-input="allParams.reviewForm.vendorName"
                      :disabled="curOpt === 'view'"
                      show-key="companyName"
                      :scope-data="allParams.reviewForm"
                      name="scc_sup_company_info2"
                      @close-quicksearch="getCompanyObj"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 资质审查单号 -->
                  <el-form-item :label="$t('vendorMod.quaNum')">
                    <el-input v-model="allParams.reviewForm.reviewFormNumber" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 审批状态 -->
                  <el-form-item :label="$t('vendorMod.approveStatus')">
                    <DictSelect v-model="allParams.reviewForm.approveStatus" code="APPROVE_STATUS_TYPE" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 创建人 -->
                  <el-form-item :label="$t('common.creator')">
                    <el-input v-model="allParams.reviewForm.createdFullName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 部门 -->
                  <el-form-item :label="$t('vendorMod.department')">
                    <el-input v-model="allParams.reviewForm.ceeaDeptName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 创建时间 -->
                  <el-form-item :label="$t('common.creationTime')">
                    <el-date-picker
                      v-model="allParams.reviewForm.creationDate"
                      :format="$formatDatePickerTime"
                      value-format="yyyy-MM-dd HH:mm:ss"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 单据说明 -->
                  <!-- 开发原因分析 -->
                  <el-form-item :label="$t('vendorMod.reviewExplain')" prop="reviewExplain">
                    <el-input
                      v-model="allParams.reviewForm.reviewExplain"
                      type="textarea"
                      maxlength="1000"
                      show-word-limit
                      :autosize="{ minRows: 2, maxRows: 4 }"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 需求分析 -->
                  <el-form-item :label="$t('vendorMod.demandAnalysis')" prop="ceeaDemandAnalysis">
                    <el-input
                      v-model="allParams.reviewForm.ceeaDemandAnalysis"
                      type="textarea"
                      maxlength="1000"
                      show-word-limit
                      :autosize="{ minRows: 2, maxRows: 4 }"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 市场供应分析 -->
                  <el-form-item :label="$t('vendorMod.supAnalysis')" prop="ceeaSupAnalysis">
                    <el-input
                      v-model="allParams.reviewForm.ceeaSupAnalysis"
                      type="textarea"
                      maxlength="1000"
                      show-word-limit
                      :autosize="{ minRows: 2, maxRows: 4 }"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 品类本期采购策略 -->
                  <el-form-item :label="$t('vendorMod.categoryStrategy')">
                    <el-input
                      v-model="allParams.reviewForm.ceeaCategoryStrategy"
                      type="textarea"
                      maxlength="1000"
                      show-word-limit
                      :autosize="{ minRows: 2, maxRows: 4 }"
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-form>
          </el-collapse-item>
          <!-- 寻源需求单 -->
          <el-collapse-item ref="sourcingInfo" :title="$t('route.pollingSource2')" name="2">
            <div class="commonPad">
              <!-- 新增 -->
              <el-button type="primary" :disabled="curOpt === 'view' || !allParams.reviewForm.vendorId" @click="addItem">
                {{ $t('common.add') }}
              </el-button>
            </div>
            <BaseTable
              stripe
              :data="allParams.reviewRelations"
              :columns="columns"
              border
              @deleteItem="deleteItem"
            >
              <!-- 寻源单号 -->
              <template #reqHeadNo="scope">
                <el-button type="text" @click="readSourcing(scope.row)">
                  {{ scope.row.reqHeadNo }}
                </el-button>
              </template>
              <!-- 状态 -->
              <template #auditStatus="scope">
                {{ $getDictLabel('APPROVE_STATUS', scope.row.auditStatus) }}
              </template>
            </BaseTable>
          </el-collapse-item>
          <!-- 引入组织和品类 -->
          <el-collapse-item ref="orgCateJournals" :title="$t('vendorMod.orgCateJournals')" name="3">
            <div class="commonPad">
              <OrganizationSelector
                :disabled="curOpt === 'view'"
                selectType="button"
                node-type="OU"
                multiple
                matchField="orgId"
                :defaultValue="allParams.orgJournals"
                @select="organizationSelectHandel"
              />
            </div>
            <el-table
              :data="allParams.orgJournals"
              style="width: 100%; margin-bottom:8px;text-align:center;"
              border
              use-virtual
              :empty-text="$t('components.noData')"
              :row-height="35"
              max-height="390px"
            >
              <el-table-column align="center" type="index" width="50" />
              <!-- 引入组织 -->
              <el-table-column
                align="center"
                prop="orgName"
                :label="$t('vendorMod.addOrg')"
                min-width="200"
                :render-header="_addStarToColumn"
              >
                <template slot-scope="scope">
                  {{ scope.row.orgName }}
                </template>
              </el-table-column>
              <!-- 操作 -->
              <el-table-column align="center" prop="operation" :label="$t('common.operation')" width="100">
                <template slot-scope="scope">
                  <el-button
                    v-if="!scope.row.reqHeadId"
                    :disabled="curOpt === 'view'"
                    type="text"
                    @click="delOrgCateJournals(scope.$index, scope.row)"
                  >
                    {{ $t('common.delete') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <!--品类信息-->
            <CCategorySelect
              :disabled="curOpt === 'view'"
              select-type="button"
              :multiple="true"
              :selected-lines="allParams.cateJournals"
              style="margin-bottom: 8px"
              @select="addCategorysList"
            />
            <el-table :data="allParams.cateJournals" style="width: 100%" border max-height="250px">
              <el-table-column align="center" type="index" width="50" />
              <!-- 引入品类 -->
              <el-table-column align="center" prop="categoryName" :label="$t('vendorMod.addCategory')" min-width="200">
                <template slot-scope="scope">
                  {{ scope.row.categoryName }}
                </template>
              </el-table-column>
              <!-- 品类本年度采购金额（万元） -->
              <el-table-column
                align="center"
                prop="thisYearAmount"
                :label="$t('vendorMod.thisYearAmount')"
                min-width="150"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.thisYearAmount"
                    v-input-format="{ type: 'float' }"
                    :disabled="curOpt === 'view'"
                  />
                </template>
              </el-table-column>
              <!-- 当前供应商数量 -->
              <el-table-column
                align="center"
                prop="existCountOfCompany"
                :label="$t('vendorMod.existCountOfCompany')"
                min-width="200"
              />
              <!-- 供应商数量上限 -->
              <el-table-column
                align="center"
                prop="supplierCountLimit"
                :label="$t('vendorMod.supplierCountLimit')"
                min-width="200"
              />
              <!-- 供应商数量上限 -->
              <el-table-column
                v-if="false"
                align="center"
                prop="supplierCountLimitFlag"
                :label="$t('vendorMod.supplierCountLimitFlag')"
              />
              <!-- 操作 -->
              <el-table-column align="center" prop="operation" :label="$t('common.operation')" width="180">
                <template slot-scope="scope">
                  <el-button
                    v-if="!scope.row.reqHeadId"
                    :disabled="curOpt === 'view'"
                    type="text"
                    @click="delOrgCateJournals2(scope.$index, scope.row)"
                  >
                    {{ $t('common.delete') }}
                  </el-button>
                  <el-button :disabled="curOpt === 'view'" type="text" @click="qualifications(scope)">
                    {{ $t('vendorMod.qualificationCriteriaView') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
          <!--财务信息-->
          <el-collapse-item ref="finance" name="4" :title="$t('vendorMod.financeInfo')">
            <el-button type="primary" :disabled="curOpt === 'view' || allParams.orgJournals == 0" @click="addFinance">
              {{
                $t('common.new')
              }}
            </el-button>
            <el-table
              ref="financeTable"
              :data="allParams.financeInfoList"
              style="width: 100%"
              border
              max-height="250px"
            >
              <!-- 引入组织 -->
              <el-table-column align="center" prop="fullPathId" :label="$t('vendorMod.ceeaOrgName2')" min-width="120px">
                <template slot-scope="scope">
                  <el-select
                    v-model="scope.row.orgId"
                    style="width: 100%"
                    :disabled="isReadOnly || scope.row.enableFlag == 'Y'"
                    @change="orgChange(scope.row.orgId, scope.row)"
                  >
                    <el-option
                      v-for="item in allParams.orgJournals"
                      :key="item.orgId"
                      :label="item.orgName"
                      :value="item.orgId"
                    />
                  </el-select>
                </template>
              </el-table-column>
              <!-- 工厂代码 -->
              <el-table-column align="center" prop="factoryCode" :label="$t('vendorMod.factoryCode')" width="150">
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.factoryCode"
                    :disabled="isReadOnly || scope.row.enableFlag == 'Y'"
                    onKeyUp="value=value.replace(/[^\w\\/]/ig,'')"
                    maxlength="50"
                    show-word-limit
                  />
                </template>
              </el-table-column>
              <!-- 结算币种 -->
              <el-table-column align="center" prop="clearCurrency" width="150">
                <template slot="header">
                  <i class="toRequired">*</i>{{ $t('vendorMod.clearCurrency') }}
                </template>
                <template slot-scope="scope">
                  <DictSelect
                    v-model="scope.row.clearCurrency"
                    code="BID_TENDER_CURRENCY"
                    :disabled="isReadOnly || scope.row.enableFlag == 'Y'"
                  />
                </template>
              </el-table-column>
              <!-- 付款方式 -->
              <el-table-column align="center" prop="paymentMethod" width="150">
                <template slot="header">
                  <i class="toRequired">*</i>{{ $t('vendorMod.paymentMethod') }}
                </template>
                <template slot-scope="scope">
                  <DictSelect
                    v-model="scope.row.paymentMethod"
                    code="PAYMENT_METHOD"
                    :disabled="isReadOnly || scope.row.enableFlag == 'Y'"
                  />
                </template>
              </el-table-column>
              <!-- 付款账期 -->
              <el-table-column align="center" prop="paymentTerms" min-width="120">
                <template slot="header">
                  <i class="toRequired">*</i>{{ $t('vendorMod.paymentTerms') }}
                </template>
                <template slot-scope="scope">
                  <DictSelect
                    v-model="scope.row.paymentTerms"
                    code="PAYMENT_TERMS"
                    :disabled="isReadOnly || scope.row.enableFlag == 'Y'"
                  />
                </template>
              </el-table-column>

              <!-- 拓展字段 ]]]-->
              <!-- 操作 -->
              <el-table-column
                align="center"
                prop="operation"
                :label="$t('common.operation')"
                width="100"
                fixed="right"
              >
                <template slot-scope="scope">
                  <el-button type="text" :disabled="isReadOnly || curOpt === 'view'" @click="financeDel(scope.$index, scope.row)">
                    {{
                      $t('common.delete')
                    }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
          <!-- 银行信息 -->
          <el-collapse-item
            v-if="changeDim.bankInfo === 'Y'"
            ref="bankJournals"
            :title="$t('vendorMod.bankInfo')"
            name="5"
          >
            <div class="commonPad">
              <el-button type="primary" class="detail-pbtn" :disabled="curOpt === 'view'" @click="addBankInfo">
                {{ $t('common.new') }}
              </el-button>
            </div>
            <el-table
              ref="bankTable"
              :data="
                allParams.bankJournals.slice(
                  (currentPage - 1) * pageSize_approvalBiddingItemLis,
                  currentPage * pageSize_approvalBiddingItemLis
                )
              "
              style="width: 100%"
              max-height="250px"
              border
            >
              <el-table-column align="center" type="index" width="50" />
              <!-- 银行代码 -->
              <el-table-column
                align="center"
                prop="bankCode"
                :label="$t('components.bank.bankCode')"
                width="150"
                :show-overflow-tooltip="true"
                :render-header="_addStarToColumn"
              >
                <template slot-scope="scope">
                  <QuickSearch
                    :disabled="curOpt === 'view'"
                    :show-input="scope.row.bankCode"
                    :pre-query-data="{ 't.attr1': 'Y' }"
                    propKey="bankNum"
                    show-key="branchBankNum"
                    :scope-data="scope.row"
                    name="ceea_base_erp_branch_bank_info"
                    @close-quicksearch="getBankObj"
                  />
                </template>
              </el-table-column>
              <!-- 银行名称 -->
              <el-table-column align="center" prop="bankName" min-width="150" :show-overflow-tooltip="true">
                <template slot="header">
                  <i class="toRequired">*</i>{{ $t('components.bank.bankName') }}
                </template>
              </el-table-column>
              <!-- 开户行名称 -->
              <el-table-column align="center" prop="openingBank" min-width="160" :show-overflow-tooltip="true">
                <template slot="header">
                  <i class="toRequired">*</i>{{ $t('components.bank.branchBankName') }}
                </template>
              </el-table-column>
              <!-- 分行编码 -->
              <el-table-column align="center" prop="unionCode" min-width="150" :show-overflow-tooltip="true">
                <template slot="header">
                  <i class="toRequired">*</i>{{ $t('components.bank.unionCode') }}
                </template>
              </el-table-column>
              <!-- 账户名称 -->
              <el-table-column align="center" prop="bankAccountName" min-width="150" :show-overflow-tooltip="true">
                <template slot="header">
                  <i class="toRequired">*</i>{{ $t('components.bank.accountName') }}
                </template>
                <template slot-scope="scope">
                  <el-input v-model="scope.row.bankAccountName" :disabled="curOpt === 'view'" />
                </template>
              </el-table-column>
              <!-- 银行账号 -->
              <el-table-column align="center" prop="bankAccount" width="150" :show-overflow-tooltip="true">
                <template slot="header">
                  <i class="toRequired">*</i>{{ $t('components.bank.bankAccount') }}
                </template>
                <template slot-scope="scope">
                  <el-input v-model="scope.row.bankAccount" :disabled="curOpt === 'view'" />
                </template>
              </el-table-column>
              <!-- 币种 -->
              <el-table-column
                align="center"
                prop="currencyCode"
                :label="$t('vendorMod.currencyCode')"
                width="120"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <DictSelect v-model="scope.row.currencyCode" code="BID_TENDER_CURRENCY" :disabled="curOpt === 'view'" />
                </template>
              </el-table-column>
              <!-- 是否主账户 -->
              <el-table-column
                align="center"
                prop="ceeaMainAccount"
                :label="$t('components.bank.isMain')"
                width="100"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <el-checkbox
                    v-model="scope.row.ceeaMainAccount"
                    true-label="Y"
                    false-label="N"
                    :disabled="curOpt === 'view'"
                  />
                </template>
              </el-table-column>
              <!-- 启用 -->
              <el-table-column
                align="center"
                prop="ceeaEnabled"
                :label="$t('components.bank.isActive')"
                width="100"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <el-checkbox
                    v-model="scope.row.ceeaEnabled"
                    true-label="Y"
                    false-label="N"
                    :disabled="curOpt === 'view'"
                  />
                </template>
              </el-table-column>
              <el-table-column fixed="right" :label="$t('common.operation')" width="60">
                <template slot-scope="scope">
                  <el-button
                    type="text"
                    :disabled="curOpt === 'view'"
                    @click="handleDelClickBank(scope.$index, scope.row)"
                  >
                    {{ $t('common.delete') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <div style="width: 100%;margin: 10px 0 25px 0;">
              <el-pagination
                align="center"
                :current-page="currentPage"
                :page-sizes="[5, 10]"
                :page-size="pageSize_approvalBiddingItemLis"
                layout="total, sizes, prev, pager, next, jumper"
                :total="allParams.bankJournals.length"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
              />
            </div>
          </el-collapse-item>
          <!-- 供应商地点信息 -->
          <el-collapse-item ref="siteJournals" :title="$t('vendorMod.vendorSiteInfos')" name="6">
            <div class="commonPad">
              <el-button
                type="primary"
                class="detail-pbtn"
                style="margin-right:5px;"
                :disabled="curOpt === 'view'"
                @click="addSiteInfo"
              >
                {{ $t('common.new') }}
              </el-button>
              <el-popover v-model="countryListVisible" placement="top" width="400">
                <div style="padding:11px;">
                  <p>
                    <!-- 请选择国家 -->
                    <span style="padding-right:5px;">{{ $t('vendorMod.msgSelCountry') }}</span>
                    <DictSelect v-model="globalCountry" code="country" filterable clearable />
                  </p>
                  <p v-if="isNewVendor">
                    <!-- 请选择地点名称 -->
                    <span style="padding-right:5px;">{{ $t('vendorMod.msgAddressName') }}</span>
                    <DictSelect v-model="globalAddress" code="VENDOR_SITE_CODE" filterable clearable />
                  </p>
                  <p>
                    <!-- 请输入详细地址 -->
                    <span style="padding-right:5px;">{{ $t('vendorMod.msgDetailAddr') }}</span>
                    <el-input v-model="globalAddressDetail" clearable style="display:block;" />
                  </p>
                </div>
                <div style="text-align: right; margin: 0">
                  <el-button type="text" @click="countryListVisible = false">
                    {{
                      $t('common.cancel')
                    }}
                  </el-button>
                  <el-button type="text" @click="batchSelectCountry">
                    {{
                      $t('common.confirm')
                    }}
                  </el-button>
                </div>
                <!-- 批量维护 -->
                <el-button slot="reference" type="primary" class="detail-pbtn" :disabled="curOpt === 'view'">
                  {{ $t('vendorMod.batchMaintain') }}
                </el-button>
              </el-popover>
            </div>
            <el-table
              ref="siteTable"
              :data="
                allParams.siteJournals.slice(
                  (currentPageA - 1) * pageSize_approvalBiddingItemLisA,
                  currentPageA * pageSize_approvalBiddingItemLisA
                )
              "
              style="width: 100%"
              max-height="250px"
              border
            >
              <el-table-column align="center" type="index" width="50" />
              <!-- 业务实体 -->
              <el-table-column align="center" prop="orgId" width="150" :show-overflow-tooltip="true">
                <template slot="header">
                  <i class="toRequired">*</i>{{ $t('dataConfMod.orgId') }}
                </template>
                <template slot-scope="scope">
                  <OrganizationSelector
                    ref="organizationSelector2"
                    v-model="scope.row.orgId"
                    :disabled="curOpt === 'view'"
                    :parent-id="-1"
                    node-type="OU"
                    :placeholder="$t('common.pleaseSelect')"
                    :scope="scope.row"
                    @select="selectHandler"
                  />
                </template>
              </el-table-column>
              <!-- 地点名称 -->
              <el-table-column align="center" prop="vendorSiteCode" min-width="150" :show-overflow-tooltip="true">
                <template slot="header">
                  <i class="toRequired">*</i>{{ $t('vendorMod.siteName') }}
                </template>
                <template slot-scope="scope">
                  <DictSelect v-model="scope.row.vendorSiteCode" code="VENDOR_SITE_CODE" :disabled="curOpt === 'view'" />
                </template>
              </el-table-column>
              <!-- 国家 -->
              <el-table-column align="center" prop="country" min-width="150" :show-overflow-tooltip="true">
                <template slot="header">
                  <i class="toRequired">*</i>{{ $t('components.address.country') }}
                </template>
                <template slot-scope="scope">
                  <DictSelect
                    v-model="scope.row.country"
                    code="country"
                    filterable
                    :disabled="curOpt === 'view'"
                    @change="getCountry(scope.row)"
                  />
                </template>
              </el-table-column>
              <!-- 地区 -->
              <el-table-column
                align="center"
                prop="province"
                :label="$t('components.address.area')"
                min-width="150"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <DictSelect
                    v-model="scope.row.province"
                    code="PROVINCE"
                    custom-select-type="PROVINCE"
                    :disabled="curOpt === 'view' || scope.row.country !== 'CN'"
                  />
                </template>
              </el-table-column>
              <!-- 城市 -->
              <el-table-column
                align="center"
                prop="city"
                :label="$t('components.address.city')"
                min-width="150"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <DictSelect
                    v-model="scope.row.city"
                    :code="scope.row.province"
                    custom-select-type="CITY"
                    :disabled="curOpt === 'view' || scope.row.country !== 'CN'"
                  />
                </template>
              </el-table-column>
              <!-- 详细地址 -->
              <el-table-column align="center" prop="addressDetail" width="150" :show-overflow-tooltip="true">
                <template slot="header">
                  <i class="toRequired">*</i>{{ $t('components.address.detailAddress') }}
                </template>
                <template slot-scope="scope">
                  <el-input v-model="scope.row.addressDetail" :disabled="curOpt === 'view'" />
                </template>
              </el-table-column>
              <!-- 邮政编码 -->
              <el-table-column
                align="center"
                prop="postCode"
                :label="$t('components.address.postalCode')"
                width="120"
                :show-overflow-tooltip="true"
                :disabled="curOpt === 'view'"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.postCode"
                    v-input-format="{ type: 'number' }"
                    :disabled="curOpt === 'view'"
                    @change="setRowAmount(scope.row)"
                  />
                </template>
              </el-table-column>
              <!-- 地址备注 -->
              <el-table-column
                align="center"
                prop="siteComment"
                :label="$t('components.address.remark')"
                min-width="150"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <el-input v-model="scope.row.siteComment" :disabled="curOpt === 'view'" />
                </template>
              </el-table-column>
              <!-- 启用 -->
              <el-table-column
                align="center"
                prop="enabledFlag"
                :label="$t('common.enable')"
                width="100"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <el-checkbox
                    v-model="scope.row.enabledFlag"
                    true-label="Y"
                    false-label="N"
                    :disabled="curOpt === 'view'"
                  />
                </template>
              </el-table-column>
              <el-table-column fixed="right" :label="$t('common.operation')" width="60">
                <template slot-scope="scope">
                  <el-button
                    type="text"
                    :disabled="curOpt === 'view'"
                    @click="handleDelClickSite(scope.$index, scope.row)"
                  >
                    {{ $t('common.delete') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <div style="width: 100%;margin: 10px 0 25px 0;">
              <el-pagination
                align="center"
                :current-page="currentPageA"
                :page-sizes="[5, 10]"
                :page-size="pageSize_approvalBiddingItemLisA"
                layout="total, sizes, prev, pager, next, jumper"
                :total="allParams.siteJournals.length"
                @size-change="handleSizeChangeA"
                @current-change="handleCurrentChangeA"
              />
            </div>
          </el-collapse-item>
          <!-- 资质审查原因 -->
          <el-collapse-item ref="reviewFormExps" :title="$t('vendorMod.quaReviewReason')" name="7">
            <div class="commonPad">
              <el-button type="primary" class="detail-pbtn" :disabled="curOpt === 'view'" @click="addReviewReason">
                {{ $t('common.new') }}
              </el-button>
            </div>
            <el-table :data="allParams.reviewFormExps" style="width: 100%" border max-height="250px">
              <el-table-column align="center" type="index" width="50" />
              <!-- 原因 -->
              <el-table-column align="center" prop="reviewReason" :label="$t('vendorMod.reviewReason')" min-width="150">
                <template slot="header">
                  <i class="toRequired">*</i>{{ $t('vendorMod.reviewReason') }}
                </template>
                <template slot-scope="scope">
                  <DictSelect
                    v-model="scope.row.reviewReason"
                    code="REVIEW_REASON_TYPE"
                    :disabled="curOpt === 'view'"
                    style="width: 100%"
                  />
                </template>
              </el-table-column>
              <!-- 原因描述 -->
              <el-table-column align="center" prop="reasonExplain" :label="$t('vendorMod.reasonExplain')" min-width="200">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.reasonExplain" :disabled="curOpt === 'view'" />
                </template>
              </el-table-column>
              <!-- 操作 -->
              <el-table-column align="center" prop="operation" :label="$t('common.operation')" width="60">
                <template slot-scope="scope">
                  <el-button
                    :disabled="curOpt === 'view'"
                    type="text"
                    @click="delReviewFormExps(scope.$index, scope.row)"
                  >
                    {{ $t('common.delete') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
          <!-- 附件 -->
          <el-collapse-item :title="$t('vendorMod.attachment')" name="8">
            <vendorAccessAttachment
              ref="sceneAttachment"
              v-model="allParams.fileRecords"
              sence-code="QUA"
              :business-id="curOrderId"
              :att-opt="curOpt"
              :up-file-info="fileInfo"
            />
          </el-collapse-item>
          <!-- 调查表清单 -->
          <el-collapse-item ref="questSupplierModule" :title="$t('quest.questSupplierModule')" name="9">
            <div class="left_div">
              <el-table :data="allParams.questSupplierList" style="width: 100%" border max-height="251px">
                <el-table-column align="center" type="index" width="50" />
                <!-- 调查表编号 -->
                <el-table-column
                  align="center"
                  prop="questNo"
                  :label="$t('quest.questNo')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <!--                    <el-button type="text">-->
                    {{ scope.row.questNo }}
                    <!--                    </el-button>-->
                    <!-- @click="questSupplierResultView(scope.row)" -->
                  </template>
                </el-table-column>
                <!-- 调查表名称 -->
                <el-table-column
                  align="center"
                  prop="questName"
                  :label="$t('quest.questName')"
                  min-width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 调查表状态 -->
                <el-table-column
                  align="center"
                  prop="approvalStatus"
                  :label="$t('quest.approvalStatus')"
                  min-width="100"
                  :show-overflow-tooltip="true"
                  :formatter="formatApprovalStatus"
                />
                <!-- 供应商编码 -->
                <el-table-column
                  align="center"
                  prop="companyCode"
                  :label="$t('quest.companyCode')"
                  min-width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 供应商名称 -->
                <el-table-column
                  align="center"
                  prop="companyName"
                  :label="$t('quest.companyName')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                />
                <!-- 业务组织 -->
                <el-table-column
                  align="center"
                  prop="questTemplateOrgName"
                  :label="$t('quest.questTemplateOrgName')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                />
                <!-- 调查模板编码 -->
                <el-table-column
                  align="center"
                  prop="questTemplateCode"
                  :label="$t('quest.questTemplateCode')"
                  min-width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 调查表模板名称 -->
                <el-table-column
                  align="center"
                  prop="questTemplateName"
                  :label="$t('quest.questTemplateName')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                />
                <!-- 创建人 -->
                <el-table-column
                  align="center"
                  prop="createdFullName"
                  :label="$t('quest.createdFullName')"
                  min-width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 创建时间 -->
                <el-table-column
                  align="center"
                  prop="creationDate"
                  :label="$t('quest.creationDate')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                  :formatter="(row, column, cellValue) => $parseTime(cellValue)"
                />
              </el-table>
            </div>
          </el-collapse-item>
        </el-collapse>
        <template v-if="curOpt !== 'view'" slot="buttonOne">
          <!-- 查看供应商信息 -->
          <el-button v-if="allParams.reviewForm.vendorId" type="primary" @click="toVendorProfile">
            {{ $t('vendorMod.checkVendorInfo') }}
          </el-button>
        </template>
      </CWorkflowMulti>
    </el-main>

    <!-- 操作记录 -->
    <srm-dialog :title="$t('vendorMod.operationRecord')" :visible.sync="loggerVisible" size="large">
      <LoggerList :visible="loggerVisible" :business-id="allParams.reviewForm.reviewFormId" />
      <!-- <template #footer class="dialog-footer">
                      <el-button @click="loggerVisible = false">
                        {{ $t("common.cancel") }}
                      </el-button>
                    </template> -->
    </srm-dialog>

    <!-- 起草人意见 -->
    <srm-dialog
      :title="$t('vendorMod.loggerComment')"
      :visible.sync="loggerComment"
      size="middle"
      style="text-align: center"
    >
      <!-- 请审批，谢谢！ -->
      <el-input v-model="inputComment" type="textarea" :rows="4" :placeholder="$t('vendorMod.pleaseApproval')" />
      <div class="topComment">
        <el-button @click="loggerComment = false">
          {{ $t('common.cancel') }}
        </el-button>
        <el-button type="primary" @click="commentForm">
          {{ $t('common.confirm') }}
        </el-button>
      </div>
    </srm-dialog>

    <!-- 资质标准 -->
    <srm-dialog
      :visible.sync="previewBol"
      size="middle"
      :title="$t('vendorMod.qualificationStandard')"
      :append-to-body="true"
    >
      <div ref="printer">
        <el-collapse v-model="activeDims">
          <el-collapse-item
            v-for="item in editableTabs"
            :ref="item.name"
            :key="item.name"
            :title="item.title"
            name="1"
          >
            <srm-row :gutter="32">
              <srm-col v-for="(item2, index) in item.content" :key="index" :initCol="2">
                {{ item2.fieldName }}：
                <div style="border:1px solid #ccc;border-radius:5px;padding: 5px;">
                  {{ item2.fieldContent }}
                </div>
                <!-- <el-input
                                  v-model="item2.fieldContent"
                                  :value="item2.fieldContent"
                                  readonly
                                  :type="item2.fieldType == 'textarea' ? 'textarea' : 'text'"
                                  placeholder="请输入名称"
                              /> -->
              </srm-col>
            </srm-row>
          </el-collapse-item>
        </el-collapse>
      </div>
      <div slot="footer">
        <!-- <el-button type="primary" @click="printer">打印</el-button> -->
        <el-button @click="previewBol = false">
          {{ $t('common.backTo') }}
        </el-button>
      </div>
    </srm-dialog>
    <!-- 付款方式 -->
    <srm-dialog :visible.sync="paymentBol" size="middle" :title="$t('vendorMod.paymentMethod')" :append-to-body="true">
      <div ref="payment">
        <el-collapse v-model="activeDims">
          <pay-plan
            ref="payList"
            v-model="orgJournalPayPlanList"
            :context="this"
            :is-buyer="true"
            :illegal="curOpt"
            visible
            @change="payPlanDatachange"
          />
        </el-collapse>
      </div>
      <div slot="footer">
        <el-button type="primary" @click="paymentClick">
          {{ $t('common.confirm') }}
        </el-button>
        <el-button @click="paymentBol = false">
          {{ $t('common.backTo') }}
        </el-button>
      </div>
    </srm-dialog>

    <SourcingDialog ref="sourcingDialog" :visible.sync="sourcingVisible" @getSelections="getSourcingSelections" />
  </el-container>
</template>
<script>

import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch'
import LoggerList from '@/library/components/logger'
import CCategorySelect from 'lib@/components/c-category-select'
import vendorAccessAttachment from 'modb@/vendorManagementBuyer/components/vendorAccessAttachment'
import vendorProfileDetailRead from 'modb@///vendorManagementBuyer/views/vendorProfile/vendorProfileDetailRead'
import VendorAccessSteps from 'modb@///vendorManagementBuyer/components/VendorAccessSteps'
import OrganizationSelector from 'lib@/components/organization-selector'
import payPlan from 'mod@/common/userManage/views/companyInfoMaintain/pay-plan'
import WorkflowCommon from '@/library/mixins/workflow-common'
import sourcingApplicationDetail from 'modb@///sourcing/views/sourcingApplicationBuyer/sourcingApplicationDetail'
import BaseTable from 'lib@/components/BaseTable'
import SourcingDialog from './sourcingDialog'
import TableDialog from './tableDialog'
import { accessCommonApi, quaApi, reviewFormStandard } from 'modb@/vendorManagementBuyer/api/vendorManagement'
import { vendorAttributeComApi } from 'modb@/basicSetting/api/basicSetting'

export default {
  name: 'QuaOfReviewDetail',
  components: {
    MainHeader,
    CCategorySelect,
    QuickSearch,
    CToolbar,
    VendorAccessSteps,
    vendorAccessAttachment,
    LoggerList,
    OrganizationSelector,
    payPlan,
    TableDialog,
    BaseTable,
    SourcingDialog
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      sourcingVisible: false,
      organizationUsers: [], // 该用户所有的组织
      displaySmartVendor: [], // 该用户所有的组织
      dialogorganizationVisible: false, // 是否显示组织弹框
      multipleVendorSelection: [], // 弹框组织已选择的的组织
      lookIndex: null,
      filterVendorCode: null,
      filterVendorName: null,
      orgJournalPayPlanList: [
        {
          paymentPeriod: 1
        }
      ], // 付款方式
      paymentBol: false, // 付款方式
      editableTabs: [],
      previewBol: false,
      isReadOnly: false,
      currentPage: 1,
      pageSize_approvalBiddingItemLis: 10,
      currentPageA: 1,
      pageSize_approvalBiddingItemLisA: 10,
      submitData: [],
      inputComment: '', // 起草人意见
      loggerComment: false,
      catDialog: false,
      fileInfo: {
        uploadType: 'DEF',
        sourceType: 'WEB_APP',
        fileModular: 'sup',
        fileFunction: 'quaOfReview',
        fileType: 'images'
      },
      orgInfoList: [],
      // globalCategoryName: null,
      addressList: [],
      quaActiveInfo: 'tab1',
      menuId: '',
      curOpt: 'add',
      fileRefresh: false,
      siteAccessType: 'NON',
      loggerVisible: false,
      loggerVisible2: false,
      orderStatus: 'DRAFT', // 单据状态
      curOrderId: null, // 单据ID
      approveStatusList: [], // 审批状态
      quaReviewTypeList: [], // 资质审查类型
      yesOrNoList: [], // 是否
      currencyList: [], // 币种列表
      globalDivisionId: null,
      BUList: [],
      isNewVendor: false,
      countryListVisible: null,
      globalAddressDetail: null,
      globalCountry: null,
      globalAddress: null,
      companyStatus: [], // 营业状态
      bankAccountType: [], // 账户类型
      paymantType: [], // 付款类型
      invoiceLimit: [], // 发票限额
      paymantTerms: [], // 付款条件
      exploitReason: [], // 资质审查原因
      rules: {
        quaReviewType: [{ required: true, message: this.$t('vendorMod.msgQuaReviewType') }], // '请选择资质审查类型'
        vendorId: [{ required: true, message: this.$t('vendorMod.msgVendorId') }], // '请输入供应商名称'
        reviewExplain: [
          { required: true, message: this.$t('vendorMod.pleaseEnterTheDocumentDescription') }
        ], // 请输入单据说明
        ceeaDemandAnalysis: [{ required: true, message: this.$t('vendorMod.msgDemandAnalysis') }], // 请输入需求分析
        ceeaSupAnalysis: [{ required: true, message: this.$t('vendorMod.msgSupAnalysis') }] // 请输入市场供应分析
      },
      disableIfSiteForm: false, // 是否允许选择【现场评审
      allParams: {
        reviewForm: {
          quaReviewType: '', // 资质审查类型
          quaReviewTypeName: '', // 资质审查类型名称
          vendorId: '',
          vendorCode: '', // 供应商code
          vendorName: '', // 供应商名称
          reviewFormNumber: '', // 资质审查单号
          createdFullName: '',
          ceeaDeptId: null,
          ceeaDeptName: '',
          creationDate: new Date(),
          approveStatus: '',
          reviewCycle: '',
          reviewExplain: '',
          fileUploads: [] // 附件
        },
        bankJournals: [],
        siteJournals: [],
        orgJournals: [],
        cateJournals: [],
        reviewRelations: [], // 寻源需求单
        reviewAttaches: [],
        reviewFormExps: [],
        fileRecords: [],
        questSupplierList: [],
        financeInfoList: [],
        opType: ''
      },
      isDisabled: this.$attrs.params.flag === 'edit',
      isModify: false,
      activeDims: ['1', '2', '3', '4', '5', '6', '7', '8', '9'],
      bankJournalsSelection: [], // 选择的银行信息
      bankSelectData: [], // 选中旧数据
      fieldDimConf: [], // 维度配置
      curRole: this.$store.getters.userType, // 用户类型 BUYER || VENDOR
      flowParams: {}, // 流程参数
      openWorkFlow: true, // 暂时开启
      integrationModeFlow: '', // 工作流集成模式
      questSupplierApproveStatusList: [],
      columns: [
        {
          attrs: {
            align: 'center',
            type: 'index',
            width: '50',
            label: ''
          }
        },
        // 寻源单号
        {
          attrs: {
            label: () => this.$t('sourcingBuyer.reqHeadNo'),
            minWidth: 100,
            prop: 'reqHeadNo'
          },
          slot: 'reqHeadNo'
        },
        // 需求标题
        {
          attrs: {
            label: () => this.$t('sourcingBuyer.souReqTitile'),
            minWidth: 100,
            prop: 'souReqTitile'
          }
        },
        // 状态
        {
          attrs: {
            label: () => this.$t('sourcingBuyer.status'),
            minWidth: 100,
            prop: 'auditStatus'
          },
          slot: 'auditStatus'
        },
        // 截止时间
        {
          attrs: {
            label: () => this.$t('sourcingBuyer.expirationTime'),
            minWidth: 100,
            prop: 'expirationTime',
            formatter: val => this.$parseTime(val, '{y}-{m}-{d}')
          }
        },
        // 创建人
        {
          attrs: {
            label: () => this.$t('sourcingBuyer.createdFullName'),
            minWidth: 100,
            prop: 'reviewCreatedBy'
          }
        },
        // 创建时间
        {
          attrs: {
            label: () => this.$t('sourcingBuyer.creationDate'),
            minWidth: 100,
            prop: 'reviewCreationDate',
            formatter: val => this.$parseTime(val, '{y}-{m}-{d}')
          }
        },
        // 操作
        {
          attrs: {
            align: 'center',
            label: t => t.$t('common.operation'),
            fixed: 'right',
            width: 80
          },
          operations: [
            {
              show: this.curOpt !== 'view',
              event: 'deleteItem',
              name: this.$t('common.delete'),
              attrs: { type: 'text' }
            }
          ]
        }
      ]
    }
  },
  computed: {
    changeDim () {
      let dimObj = {}
      if (this.fieldDimConf.length > 0) {
        this.fieldDimConf.map(item => {
          if (item.isAudit === 'Y') {
            dimObj[item.dimCode] = item.isAudit
          }
        })
      }
      return dimObj
    },
    viewUpdateButton () {
      // 用来控制保存、提交按钮是否可见。如果自定义按钮则无需添加
      return (
        this.orderStatus === 'DRAFT' ||
        this.curOpt === 'add' ||
        this.orderStatus === 'REJECTED' ||
        this.orderStatus === 'WITHDRAW'
      )
    },
    workflowBusinessId () {
      // 用来指定工作流的业务ID
      return this.allParams.reviewForm.reviewFormId
        ? this.allParams.reviewForm.reviewFormId
        : this.curOrderId
    },
    // 展示工作流tab页
    workflowTabDisabled () {
      return this.orderStatus === 'DRAFT'
    }
  },
  watch: {
    // 监听保存提交 按钮变更状态，如果自定义按钮则无需添加
    viewUpdateButton () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
    }
    // financeInfoList:{
    //   handler(curVal, oldVal) {
    //       console.log(curVal);
    //       console.log(oldVal)
    //   },
    //   deep: true
    // }
  },
  async created () {
    this.isReadOnly = this.$attrs.params.isOnlyRead
    this.buttonConfigInfo.save.view = this.viewUpdateButton
    this.buttonConfigInfo.submit.view = this.viewUpdateButton
    this.buttonConfigInfo.save.name = this.$t('common.staging')
    this.buttonConfigInfo.submit.name = this.$t('common.submit')
    this.buttonConfigInfo.cancel.view = false
    this.buttonConfigInfo.close.view = this.isReadOnly
    // console.log(this.$store.getters.userInfo.organizationUsers)
    // this.organizationUsers = this.$store.getters.userInfo.organizationUsers
    // this.displaySmartVendor = this.$store.getters.userInfo.organizationUsers

    this.fatchDictData() // 字典
    this.getFieldDimConf() // 查询维度配置
    this.curOpt = this.$attrs.params.flag
    if (this.$attrs.params.flag === 'add') {
      const { companyId, phone, nickname, username, ceeaDeptId, department } =
        this.$store.getters.userInfo
      this.allParams.reviewForm.createdFullName = nickname
      this.allParams.reviewForm.ceeaDeptId = ceeaDeptId
      this.allParams.reviewForm.ceeaDeptName = department
    }
    if (this.$attrs.params.flag === 'add' && this.$attrs.params.row) {
      if (this.$attrs.params.row) {
        this.allParams.reviewForm.vendorId = this.$attrs.params.row.vendorId
        this.allParams.reviewForm.vendorCode = this.$attrs.params.row.vendorCode
        this.allParams.reviewForm.vendorName = this.$attrs.params.row.vendorName
        this.getFinBankInfo()
        this.getSiteList(this.$attrs.params.row.vendorId, false)
      }
    }
    if (this.$attrs.params.flag === 'edit' || this.$attrs.params.flag === 'view') {
      this.curOrderId = this.$attrs.params.row.reviewFormId
      let reviewFormId = this.$attrs.params.row.reviewFormId
      this.getreviewFormDetail(reviewFormId)
      this.getQuestSupplierList(this.$attrs.params.row.vendorId)
    }

    console.log(this.$store.getters.userInfo.organizationUsers)
    // const res = await this.$api.base.getSupplierOrgTree({ organizationTypeCode: 'OU', parentOrganizationIds: -1 })
    const res = await this.$api.base.getOrganizationByOrgCode({
      organizationTypeCode: 'OU',
      parentOrganizationId: '-1'
    })
    this.organizationUsers = res.data
    this.displaySmartVendor = res.data
  },
  methods: {
    payPlanDatachange () { },
    addItem () {
      this.sourcingVisible = true
      this.$refs.sourcingDialog.init(this.allParams.reviewRelations, this.allParams.reviewForm.vendorId)
    },
    getSourcingSelections (selections) {
      let orgList = []
      let categoryList = []
      selections.forEach(item => {
        this.allParams.reviewRelations.push({ ...item })
        categoryList.push({ reqHeadId: item.reqHeadId, categoryId: item.categoryId, categoryName: item.categoryName, categoryCode: item.categoryCode, thisYearAmount: '', existCountOfCompany: '', supplierCountLimit: '', supplierCountLimitFlag: '' })
        orgList.push({ reqHeadId: item.reqHeadId, organizationName: item.orgName, organizationId: item.orgId, organizationCode: item.orgCode })
        this.organizationSelectHandel(orgList)
        this.addCategorysList(categoryList)
      })
    },
    deleteItem ({ row, $index }) {
      if (row.relationId) {
        this.$http({
          url: `/api-sup/review/relation/delete?relationId=${row.relationId}`,
          method: 'POST',
          loading: true
        }).then(res => {
          this.deleteSouring(row, $index)
        })
      } else {
        this.deleteSouring(row, $index)
      }
    },
    deleteSouring (row, i) {
      this.allParams.reviewRelations.splice(i, 1)
      this.allParams.orgJournals.forEach((item, index) => {
        if (item.reqHeadId === row.reqHeadId) {
          this.delOrgCateJournals(index, item)
          this.delOrgCateJournals2(index, item)
        }
      })
    },
    readSourcing (row) {
      this.$emit('tab-add', {
        component: sourcingApplicationDetail,
        params: {
          flag: 'view',
          row: row,
          showType: 'readOnly',
          tabName: 'sourcingApplicationDetail' + row.reqHeadNo
        },
        title: row.reqHeadNo,
        name: 'sourcingApplicationDetail' + row.reqHeadNo
      })
    },
    // 选择品类
    addCategorysList (data) {
      if (this.allParams.cateJournals.length < 1) {
        data.forEach(e => {
          quaApi.getCategoryInfoById(e.categoryId).then(res => {
            e.existCountOfCompany = res.data.existCountOfCompany
            e.supplierCountLimit = res.data.supplierCountLimit
            e.supplierCountLimitFlag = res.data.supplierCountLimitFlag
            this.allParams.cateJournals.push(e)
          })
        })
      } else {
        data.forEach(e => {
          let bol = true
          let reqHeadId = e.reqHeadId || null
          let categoryId = e.categoryId
          this.allParams.cateJournals.find(v => {
            if (v.categoryId == e.categoryId) {
              bol = false
            }
          })
          if (bol) {
            quaApi.getCategoryInfoById(e.categoryId).then(res => {
              e.existCountOfCompany = res.data.existCountOfCompany
              e.supplierCountLimit = res.data.supplierCountLimit
              e.supplierCountLimitFlag = res.data.supplierCountLimitFlag
              this.allParams.cateJournals.push(e)
            })
          } else if (reqHeadId) {
            this.allParams.cateJournals.forEach((item, index) => {
              if (categoryId === item.categoryId) {
                this.allParams.cateJournals.splice(index, 1, { ...item, reqHeadId })
              }
            })
          }
        })
      }

      let categoryId = this.allParams.cateJournals.length ? this.allParams.cateJournals[0].categoryId : data[0].categoryId
      let quaReviewType = this.allParams.reviewForm.quaReviewType
      let query = { quaReviewType: quaReviewType, categoryId }
      this.fatchQuaFileConfig(query)
    },
    orgChange (orgId, row) {
      let dictItem = this.allParams.orgJournals.find(i => i.orgId === orgId) || {}
      row.orgCode = dictItem.orgCode
      row.orgName = dictItem.orgName
    },
    // 删除财务信息操作
    financeDel (index, row) {
      let bol = false
      let i = 0
      this.allParams.orgJournals.forEach(u => {
        if (u.orgId == row.orgId) {
          bol = true
        }
      })
      this.allParams.financeInfoList.forEach(e => {
        if (e.orgId == row.orgId) {
          i++
        }
      })
      if (bol) {
        // 账期与上面组织有关联的时候
        if (i > 1) {
          // 如果自动带出的账期不能修改
          if (row.enableFlag != 'Y') {
            this.allParams.financeInfoList.splice(index, 1)
          } else {
            this.$message.error(this.$t('vendorMod.msgDel2'))
          }
        } else {
          this.$message.error(this.$t('vendorMod.msgDel1'))
        }
      } else {
        this.allParams.financeInfoList.splice(index, 1)
      }
    },
    addFinance () {
      // 添加财务信息
      this.allParams.financeInfoList.unshift({
        add: true,
        orgId: null,
        taxKey: '',
        taxRate: ''
      })
    },
    async getWorkflowBusinessType () {
      return 'quaBusReview'
    },
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    queryFilter (val) {
      if (val.target.value) {
        this.organizationUsers = this.displaySmartVendor.filter(v => {
          let p = v.organizationId.toString()
          p.includes(val.target.value)
        })
      } else {
        this.organizationUsers = this.displaySmartVendor
      }
    },
    queryFilterByName (val) {
      if (val.target.value) {
        this.organizationUsers = this.displaySmartVendor.filter(v =>
          v.organizationName.includes(val.target.value),
        )
      } else {
        this.organizationUsers = this.displaySmartVendor
      }
    },
    organizationSelectHandel (orgs) {
      this.multipleVendorSelection = orgs || []
      this.addNewSmartVendor()
    },
    // 点击弹窗的确认
    addNewSmartVendor () {
      if (this.multipleVendorSelection.length == 0) {
        this.$message.warning(this.$t('common.msgSelectOrg'))
        return false
      }
      if (this.allParams.orgJournals.length < 1) {
        this.multipleVendorSelection.forEach(e => {
          this.allParams.orgJournals.push({
            reqHeadId: e.reqHeadId || null,
            orgName: e.organizationName,
            orgId: e.organizationId,
            orgCode: e.organizationCode,
            orgJournalPayPlanList: [
              {
                paymentPeriod: 1
              }
            ]
          })
          this.financeInfoListAdd(e)
        })
      } else {
        this.multipleVendorSelection.forEach(e => {
          let bol = true
          let reqHeadId = e.reqHeadId || null
          let organizationId = e.organizationId
          this.allParams.orgJournals.find(v => {
            if (v.orgId == e.organizationId) {
              bol = false
            }
          })
          if (bol) {
            this.allParams.orgJournals.push({
              reqHeadId: e.reqHeadId || null,
              orgName: e.organizationName,
              orgId: e.organizationId,
              orgCode: e.organizationCode,
              orgJournalPayPlanList: [
                {
                  paymentPeriod: 1
                }
              ]
            })
            this.financeInfoListAdd(e)
          } else if (reqHeadId) {
            this.allParams.orgJournals.forEach((item, index) => {
              if (organizationId === item.orgId) {
                this.allParams.orgJournals.splice(index, 1, { ...item, reqHeadId })
              }
            })
          }
        })
      }
      this.dialogorganizationVisible = false
    },
    async financeInfoListAdd (e) {
      try {
        let attr = []
        attr.push(this.allParams.reviewForm.vendorId)
        let datas = await reviewFormStandard.listByCompanyIdAndOrgId({
          companyIdList: attr,
          orgId: e.organizationId
        })
        if (datas.data.length > 0) {
          datas.data.forEach(p => {
            this.allParams.financeInfoList.push({
              orgName: e.organizationName,
              orgId: e.organizationId,
              orgCode: e.organizationCode,
              fullPathId: e.fullPathId,
              factoryCode: p.factoryCode,
              clearCurrency: p.clearCurrency,
              paymentMethod: p.paymentMethod,
              paymentTerms: p.paymentTerms,
              enableFlag: p.enableFlag,
              financeInfoId: p.financeInfoId
            })
          })
        } else {
          this.allParams.financeInfoList.push({
            orgName: e.organizationName,
            orgId: e.organizationId,
            orgCode: e.organizationCode,
            fullPathId: e.fullPathId,
            financeInfoId: e.financeInfoId
          })
        }
      } catch (e) {
        this.allParams.financeInfoList.push({
          orgName: e.organizationName,
          orgId: e.organizationId,
          orgCode: e.organizationCode,
          fullPathId: e.fullPathId
        })
      }
    },
    paymentClick () {
      let orgJournalPayPlanList = this.orgJournalPayPlanList
      let bol = true
      orgJournalPayPlanList.forEach(e => {
        if (Object.getOwnPropertyNames(e).length < 6) {
          bol = false
        }
      })
      if (!bol) {
        this.$message.error(this.$t('vendorMod.pleaseEnterCompleteInformation'))
        return false
      } else {
        this.allParams.orgJournals[this.lookIndex].orgJournalPayPlanList =
          this.orgJournalPayPlanList
        this.lookIndex = null
        this.paymentBol = false
      }
      console.log(this.orgJournalPayPlanList)
    },
    look (scope) {
      console.log(scope)
      this.orgJournalPayPlanList = scope.row.orgJournalPayPlanList
      this.lookIndex = scope.$index
      this.paymentBol = true
    },
    getList (res) {
      let editableTabs = []
      res.data.reviewFormStandardDimList.forEach((datas, indexs) => {
        const index = String(indexs + 1)
        let d1 = {
          title: datas.dimName,
          name: index,
          content: []
        }
        this.tabIndex = index
        let attr = []
        datas.reviewFormStandardDimFieldList.forEach(datas2 => {
          let d2 = {
            standardId: datas2.standardId,
            standardDimId: datas2.standardDimId,
            fieldCode: datas2.fieldCode,
            fieldName: datas2.fieldName,
            fieldType: datas2.fieldType,
            fieldContent: datas2.fieldContent
          }
          attr.push(d2)
        })
        d1.content = attr
        editableTabs.push(d1)
      })
      this.editableTabs = editableTabs
      console.log(this.editableTabs)
    },
    // questSupplierResultView(row) {
    //   this.$emit("tab-add", {
    //     component: questSupplierResultView,
    //     params: {
    //       flag: "view",
    //       row: row,
    //       tabName: "questSupplierResultView" + row.questSupId
    //     },
    //     title: "调查表" + row.questNo,
    //     name: "questSupplierResultView" + row.questSupId
    //   });
    // },
    qualifications (scope) {
      const categoryId = scope.row.categoryId

      quaApi.getByCategoryId(categoryId).then(res => {
        console.log(res)
        if (!res.data.reviewFormStandardDimList || res.data.reviewFormStandardDimList.length < 1) {
          this.$message.error(this.$t('vendorMod.noQualificationStandardForCategory'))
        } else {
          this.getList(res)
          this.previewBol = true
        }
      })
    },
    formatApprovalStatus (row, column, cellValue, index) {
      if (!this.questSupplierApproveStatusList.filter(v => v.value === cellValue)[0]) {
        return null
      }
      return cellValue
        ? this.questSupplierApproveStatusList.filter(v => v.value === cellValue)[0].label
        : ''
    },
    // 分页银行
    handleSizeChange (val) {
      console.log(`每页 ${val} 条`)
      this.currentPage = 1
      this.pageSize_approvalBiddingItemLis = val
    },
    handleCurrentChange (val) {
      console.log(`当前页: ${val}`)
      this.currentPage = val
    },
    // 分页供应商
    handleSizeChangeA (val) {
      console.log(`每页 ${val} 条`)
      this.currentPageA = 1
      this.pageSize_approvalBiddingItemLisA = val
    },
    handleCurrentChangeA (val) {
      console.log(`当前页: ${val}`)
      this.currentPageA = val
      console.log('this.allParams.siteJournalsA', this.allParams.siteJournals)
    },
    showLogger () {
      this.loggerVisible = true
    },
    // 获取数据字典
    fatchDictData () {
      this.$http({
        url: '/api-base/organization/organization/listAllOrganization',
        method: 'POST',
        data: {
          pageNum: 1,
          pageSize: 9999,
          organizationTypeCode: 'OU'
        },
        loading: true
      })
        .then(data => {
          this.orgInfoList = data.data.list
          console.log(this.orgInfoList)
        })
        .catch(err => {
          console.log(err)
        })
    },
    // 查询维度的配置信息
    getFieldDimConf () {
      vendorAttributeComApi.getFieldDim().then(res => {
        this.fieldDimConf = res.data
      })
    },
    // 选择供应商回调
    getCompanyObj (val, data) {
      this.allParams.reviewForm.vendorId = val ? val.companyId : ''
      this.allParams.reviewForm.vendorCode = val ? val.companyCode : ''
      this.allParams.reviewForm.vendorName = val ? val.companyName : ''
      this.getFinBankInfo()
      if (val) {
        this.allParams.siteJournals = []
        this.getSiteList(val.companyId, true)
        this.getQuestSupplierList(val.companyId)
      }
    },
    getQuestSupplierList (vendorId) {
      this.$http({
        url: '/api-sup/quest/questSupplier/listPageByParm',
        method: 'POST',
        data: {
          companyIdForQuery: vendorId,
          approvalStatusList: 'APPROVED',
          orgCondition: 'Y'
        }
      })
        .then(data => {
          this.$set(this.allParams, 'questSupplierList', data.data.list)
          console.log('questSupplierList:', this.allParams.questSupplierList)
        })
        .catch(err => {
          console.log(err)
        })
    },
    getSiteList (vendorId, fromQuicksearch = false) {
      this.$http({
        url: '/api-sup/organization/site-journal/listSiteJournal',
        method: 'GET',
        params: { vendorId: vendorId },
        loading: true
      })
        .then(data => {
          this.allParams.siteJournals = this.allParams.siteJournals.concat(data.data)
          if (data.data.length === 0 && fromQuicksearch) {
            this.isNewVendor = true
          }
          this.siteJournalsndex()
        })
        .catch(err => {
          console.log(err)
        })
    },
    // 选择dept回调
    getDeptObj (val, data) {
      this.allParams.reviewForm.ceeaDeptId = val ? val.id : null
      this.allParams.reviewForm.ceeaDeptName = val ? val.descr : ''
      // this.getFinBankInfo();
    },
    // 根据资质审查单查询 查询财务信息 银行信息
    getFinBankInfo () {
      let params = {}
      params = {
        vendorId: this.allParams.reviewForm.vendorId
        // reviewFormId: this.allParams.reviewForm.reviewFormId
      }
      this.getBankData(params) // 银行
    },
    bankJournalsIndex () {
      this.allParams.bankJournals.forEach((item, index) => {
        item.getIndex = index
      })
    },
    siteJournalsndex () {
      this.allParams.siteJournals.forEach((item, index) => {
        item.getIndex = index
      })
    },

    getBankData (params) {
      // 银行信息
      // 根据供应商ID和资质审查单号获取银行信息
      accessCommonApi.getBankJournaByReviewId(params).then(res => {
        if (res.data) {
          this.allParams.bankJournals = res.data
          this.handleCurrentChange(1)
          this.bankJournalsIndex()
        }
      })
    },
    // 资质审查单切换
    getQuaReviewType (quaReviewType) {
      console.log(quaReviewType)
      if (quaReviewType) {
        if (quaReviewType === 'ONETIME_VENDOR') {
          this.allParams.reviewForm.ceeaIfVendorAuth = 'N'
        } else {
          this.allParams.reviewForm.ceeaIfVendorAuth = 'Y'
        }
        // 切换资质审查类型 清空品类信息
        // this.allParams.cateJournals = []
        // this.globalCategoryName = []
      }
    },
    // 确认选中的品类
    comfirmSelect (scope) {
      // let categoryIdArr = this.allParams.cateJournals.map(v => v.categoryId)
      // if (categoryIdArr.length > 0 && categoryIdArr.includes(node.categoryId)) {
      //   this.$message.warning(this.$t('vendorMod.msgCateRepeat')) // 品类不能重复选择
      //   scope.categoryId = null
      //   scope.categoryCode = null
      //   scope.categoryName = null
      //   return
      // }
      // scope.categoryId = node ? node.categoryId : null
      // scope.categoryCode = node ? node.categoryCode : ''
      // scope.categoryName = node ? node.categoryName : ''
      quaApi.getCategoryInfoById(scope.categoryId).then(res => {
        scope.existCountOfCompany = res.data.existCountOfCompany
        scope.supplierCountLimit = res.data.supplierCountLimit
        scope.supplierCountLimitFlag = res.data.supplierCountLimitFlag
        this.allParams.cateJournals.push({})
        this.allParams.cateJournals.pop()
      })
      // if (this.allParams.cateJournals.length === 1) {
      //   // 在增加第一条数据
      //   let categoryId = this.allParams.cateJournals[0].categoryId
      //   let quaReviewType = this.allParams.reviewForm.quaReviewType
      //   let query = { quaReviewType: quaReviewType, categoryId }
      //   // let query = 310627263801472;
      //   this.fatchQuaFileConfig(query)
      // }
    },
    fatchQuaFileConfig (query) {
      quaApi.getTemplateFilesByReviewCreate(query).then(res => {
        if (res.data && Object.keys(res.data).length > 0) {
          if (res.data.entryFileConfigList.length > 0) {
            this.allParams.fileRecords = res.data.entryFileConfigList.map(i => ({
              ...i,
              fileId: '',
              fileName: ''
            }))
          } else this.allParams.fileRecords = []

          console.log(this.allParams.fileRecords)
        } else {
          this.allParams.fileRecords = []
          // 该品类没有配置准入流程，请先去配置品类准入配置！
          this.$message.error(this.$t('vendorMod.msgConfCate'))
          // this.globalCategoryName.pop()
        }
      })
    },
    delOrgCateJournals2 (index, row) {
      this.allParams.cateJournals.splice(index, 1)
      // this.globalCategoryName = this.allParams.cateJournals
      // 品类清空完后
      if (this.allParams.cateJournals.length === 0) {
        this.allParams.fileRecords = []
      }
    },

    // 查询资质审查单据详情
    getreviewFormDetail (reviewFormId) {
      if (!reviewFormId) return
      this.$http({
        url: '/api-sup/review/reviewForm/getReviewFormDTO',
        method: 'GET',
        params: { reviewFormId },
        loading: true
      })
        .then(res => {
          this.allParams = res.data
          this.bankSelectData = res.data.bankJournals
          this.orderStatus = res.data.reviewForm.approveStatus
          console.log('orderStatus', this.orderStatus)
          let quaReviewType = res.data.reviewForm.quaReviewType
          // this.globalCategoryName = this.allParams.cateJournals
          let parame = {
            vendorId: res.data.reviewForm.vendorId
          }
          this.getQuestSupplierList(parame.vendorId)
          if (this.orderStatus == 'SUBMITTED') {
            // 切换到工作流tab页
            console.log(this.workflowParamsInfo.integrationMode)
            var workflowMode = this.workflowParamsInfo.integrationMode !== 'None'
            if (workflowMode) {
              this.activeTabName = 'workflowTab'
            }
          }
          let fdId = res.data.reviewForm.fdId
          if (fdId) {
            this.openWorkFlow = true
            this.flowParams = {
              fdId
            }
          }
        })
        .catch(err => {
          console.log(err)
        })
    },

    // 查看供应商信息
    toVendorProfile () {
      // 打开供应商详情
      let tab = {}
      let companyId = this.allParams.reviewForm.vendorId
      tab = {
        component: vendorProfileDetailRead,
        params: {
          flag: 'view',
          companyId: companyId,
          tabName: 'vendorProfileDetailRead'
        },
        title: this.allParams.reviewForm.vendorName,
        name: 'vendorProfileDetailRead'
      }
      this.$emit('tab-add', tab)
    },
    handleDelClickBank (index, row) {
      this.allParams.bankJournals.splice(row.getIndex, 1)
      this.bankJournalsIndex()

      // 当删除整一页的内容时触发，更改当前页 change by liwenhong
      if (this.allParams.bankJournals.length % this.pageSize_approvalBiddingItemLis === 0) {
        // 判断是最后一页就往前走一页
        if (
          this.currentPage * this.pageSize_approvalBiddingItemLis ===
          this.allParams.bankJournals.length + this.pageSize_approvalBiddingItemLis
        ) {
          console.log('this.currentPage', this.currentPage)
          this.currentPage = this.currentPage - 1
          this.handleCurrentChange(this.currentPage)
        }
      }
    },
    // 删除组织品类
    delOrgCateJournals (index, row) {
      this.allParams.financeInfoList.forEach((e, index2) => {
        if (e.orgId == row.orgId) {
          this.allParams.financeInfoList.splice(index2, 1)
        }
      })
      this.allParams.orgJournals.splice(index, 1)
    },
    // 删除供应商地点信息
    handleDelClickSite (index, row) {
      console.log('row.siteJournalId', row.siteJournalId)
      if (row.siteJournalId) {
        this.$http({
          url: '/api-sup/organization/site-journal/deleteSiteJournal',
          method: 'GET',
          params: { siteJournalId: row.siteJournalId },
          loading: true
        })
          .then(data => {
            this.$message({
              message: this.$t('common.successDelete'), // '删除成功'
              type: 'success'
            })
            this.allParams.siteJournals.splice(row.getIndex, 1)
            // 当删除整一页的内容时触发
            if (this.allParams.siteJournals.length % this.pageSize_approvalBiddingItemLisA === 0) {
              // 判断是最后一页就往前走一页
              if (
                this.currentPageA * this.pageSize_approvalBiddingItemLisA ===
                this.allParams.siteJournals.length + this.pageSize_approvalBiddingItemLisA
              ) {
                this.currentPageA = this.currentPageA - 1

                this.handleCurrentChangeA(this.currentPageA)
              }
            }
          })
          .catch(err => {
            console.log(err)
          })
      } else {
        this.allParams.siteJournals.splice(row.getIndex, 1)
        this.siteJournalsndex()
        // 当删除整一页的内容时触发
        if (this.allParams.siteJournals.length % this.pageSize_approvalBiddingItemLisA === 0) {
          // 判断是最后一页就往前走一页
          if (
            this.currentPageA * this.pageSize_approvalBiddingItemLisA ===
            this.allParams.siteJournals.length + this.pageSize_approvalBiddingItemLisA
          ) {
            this.currentPageA = this.currentPageA - 1
            this.handleCurrentChangeA(this.currentPageA)
          }
        }
      }
    },

    // 删除资质审查原因
    delReviewFormExps (index, row) {
      this.allParams.reviewFormExps.splice(index, 1)
    },
    addBankInfo () {
      this.allParams.bankJournals.push({
        bankInfoId: null,
        companyId: null,
        bankCode: '',
        bankName: '',
        openingBank: '',
        bankAccountName: '',
        bankAccount: '',
        currencyCode: '',
        currencyName: '',
        ceeaMainAccount: '',
        ceeaEnabled: 'Y'
      })
      console.log('this.allParams.bankJournals', this.allParams.bankJournals)
      this.bankJournalsIndex()
      if (this.allParams.bankJournals.length === 1) {
        this.handleCurrentChange(1)
      }
    },
    selectHandler (node, value, scope) {
      scope.orgId = node ? node.organizationId : null
      scope.orgCode = node ? node.organizationCode : null
      scope.orgName = node ? node.organizationName : null
      if (node) {
        this.$http({
          url: '/api-base/organization/organization/get',
          method: 'GET',
          params: { organizationId: node.organizationId },
          loading: true
        }).then(res => {
          if (res.data) {
            scope.erpOrgId = res.data.erpOrgId
          }
        })
      }
    },
    batchSelectCountry () {
      this.allParams.siteJournals.forEach((val, index) => {
        val.country = this.globalCountry
        val.addressDetail = this.globalAddressDetail
        // 选择国外就清理省市区，并且禁用
        if (this.globalCountry !== 'CN') {
          val.province = null
          val.city = null
        }
        if (this.isNewVendor) {
          val.vendorSiteCode = this.globalAddress
        }
      })
      this.countryListVisible = false
    },
    addSiteInfo () {
      this.allParams.siteJournals.push({
        siteInfoId: null,
        companyId: null,
        erpVendorId: null,
        erpVendorCode: null,
        addressName: null,
        country: null,
        province: null,
        city: null,
        'addressDetail ': null,
        purchaseFlag: null,
        paymentFlag: null,
        rfqOnlyFlag: null,
        enabledFlag: 'Y',
        enabledDeleteFlag: 'Y',
        orgId: null,
        orgCode: null,
        orgName: null,
        orgJournalPayPlanList: null,
        vendorSiteId: null,
        vendorSiteCode: null,
        disableDate: null,
        postCode: null,
        siteComment: null
      })
      this.siteJournalsndex()
      if (this.allParams.siteJournals.length === 1) {
        this.handleCurrentChangeA(1)
      }
    },
    // 新增资质审查原因
    addReviewReason () {
      this.allParams.reviewFormExps.push({
        reviewReason: '',
        reasonExplain: ''
      })
    },
    reset () {
      // 重置所有过滤条件
      for (let i in this.form) {
        this.form[i] = ''
      }
    },
    getBankObj (val, scope) {
      scope.branchBankId = val ? val.branchBankId : ''
      scope.bankCode = val ? val.bankNum : '' // 银行编号
      scope.bankName = val ? val.bankName : '' // 银行名称
      scope.unionCode = val ? val.branchBankNum : '' // 分行编号
      scope.openingBank = val ? val.branchBankName : '' // 分行名称[开户行名称]
    },
    setRowAmount (row) {
      if (row.postCode.length > 6) {
        return this.$message.error(this.$t('components.address.msgPostalError')) // 邮政编码不应超过6位!
      }
    },
    getCountry (row) {
      // 选择国外就清理省市区，并且禁用
      if (row.country !== 'CN') {
        row.province = null
        row.city = null
      }
    },
    // 起草人意见确认提交
    commentForm () {
      // 用于没有工作流页面模式下提交
      this.saveDataHandle('SUBMISSION')
      this.loggerComment = false
    },
    formValidate () {
      return new Promise(resolve => {
        this.$refs.quaForm.validate((valid, object) => {
          if (!valid) {
            this.__focus_error__(this.$t('vendorMod.pleasefinishRequired')) // '请输入单据必填信息'
          }
          resolve(valid)
        })
      })
    },

    async validateForm () {
      let fromFlag = await this.formValidate() // 表单验证
      if (fromFlag) {
        // 组织信息
        let orgJournals = this.allParams.orgJournals || []
        if (orgJournals.length == 0) {
          return this.__jump_error__('orgCateJournals', null, this.$t('vendorMod.pleaseAddAnImportOrganization'))
        }
        // 品类信息校验
        const cateJournals = this.allParams.cateJournals
        cateJournals.forEach(datas => {
          if (datas.supplierCountLimitFlag === 'Y') {
            if (datas.existCountOfCompany >= datas.supplierCountLimit) {
              this.$message.error(this.$t('vendorMod.suppliersCategoryHasExceededTheUpperLimit'))
              return false
            }
          }
        })
        // 银行信息校验
        let bankJournals = this.allParams.bankJournals || []
        let ceeaEnabled = bankJournals.filter(v => v.ceeaEnabled === 'Y')
        if (!ceeaEnabled.length) {
          // 供应商银行信息至少需要启用一个
          return this.__jump_error__('bankJournals', null, this.$t('vendorMod.msgAtLeastVBank'))
        }
        if (bankJournals.length > 0) {
          for (let item of bankJournals) {
            if (!item.bankCode) {
              // 银行代码是必填项
              return this.__jump_error__('bankJournals', null, this.$t('vendorMod.msgBankCodeRequired'))
            }
          }
        }

        // 地点信息校验 siteJournals
        let siteJournals = this.allParams.siteJournals || []
        let enabledFlag = siteJournals.filter(v => v.enabledFlag === 'Y')
        if (!enabledFlag.length) {
          // 供应商地点信息至少需要启用一个
          return this.__jump_error__('siteJournals', null, this.$t('vendorMod.msgAtLeastVAddress'))
        }
        if (siteJournals.length > 0) {
          let buIdArr = siteJournals.map(v => v.orgId + '-' + v.vendorSiteCode)
          let newArr = Array.from(new Set(buIdArr))
          if (buIdArr.length !== newArr.length) {
            this.$message.warning(this.$t('vendorMod.msgVAddressRepeat')) // 供应商地点信息，请选择不重复的业务实体 + 地点名称!
            return false
          }
          for (let item of siteJournals) {
            if (!item.orgId) {
              this.$message.error(this.$t('vendorMod.msgVOrgRequired')) // 供应商地点下的业务实体是必填项!
              return false
            }
            if (!item.vendorSiteCode) {
              // 供应商地点下的地点名称是必填项!
              this.$message.error(this.$t('vendorMod.msgVAddressRequired'))
              return false
            }
            if (!item.country) {
              // 供应商地点下的国家是必填项!
              this.$message.error(this.$t('vendorMod.msgVCountryRequired'))
              return false
            }
            if (!item.addressDetail) {
              // 供应商地点下的详细地址是必填项!
              this.$message.error(this.$t('vendorMod.msgVAddressDetailRequired'))
              return false
            }
          }
        }
        // 资质审查原因
        let reviewFormExps = this.allParams.reviewFormExps || []
        if (reviewFormExps.length == 0) {
          // '请选择资质审查原因'
          return this.__jump_error__('reviewFormExps', null, this.$t('vendorMod.msgQuaReviewReason'))
        } else {
          reviewFormExps.forEach(e => {
            if (!e.reviewReason) {
              this.$message.error(this.$t('vendorMod.msgQuaReviewReason'))
              return false
            }
          })
        }

        // 判断附件是否上传
        if (!this.$refs.sceneAttachment.validRequired()) {
          return false
        }
      } else {
        return false
      }
      return true
    },
    async save (type) {
      let validFlag = await this.validateForm() // 验证
      // 校验财务信息
      const financeInfoList = this.allParams.financeInfoList
      let financeInfoIds = []
      let financeInfoBol = false
      financeInfoList.forEach(e => {
        financeInfoIds.push(e.orgId)
        if (!e.orgId || !e.clearCurrency || !e.paymentMethod || !e.paymentTerms) {
          financeInfoBol = true
        }
      })
      if (financeInfoBol) {
        this.__jump_error__(
          'financeTable',
          null,
          this.$t('vendorMod.msgFinanceInfo')
        )
        return false
      }

      let bols = true
      this.allParams.orgJournals.forEach(e => {
        bols = financeInfoIds.includes(e.orgId)
        if (!bols) {
          this.__jump_error__(
            'financeTable',
            null,
            this.$t('一个组织至少需要有一个账期')
          )
          return false
        }
      })

      if (type !== 'SUBMISSION' && validFlag) {
        this.saveDataHandle(type)
      }
    },
    // 保存数据操作
    saveDataHandle (type) {
      let submitData = this.allParams
      if (type == 'SAVE') {
        submitData.opType = 'TEMPORARY_STORAGE'
      } else {
        submitData.opType = 'SUBMISSION'
      }

      submitData.menuId = this.menuId // 菜单Id
      if (type === 'SUBMISSION') {
        submitData.reviewForm.ceeaDrafterOpinion =
          this.inputComment || this.$t('vendorMod.pleaseApproval')
      }
      return new Promise(resolve => {
        this.$http({
          url: '/api-sup/review/reviewForm/saveOrUpdateReviewForm',
          method: 'POST',
          data: submitData,
          loading: true
        })
          .then(async res => {
            resolve(true)
            let resData = res.data
            const params = {
              businessTab: this.$t('vendorMod.qua'), // 资质审查
              model: 'api-sup',
              businessId: submitData.reviewForm.reviewFormId,
              businessNo: submitData.reviewForm.reviewFormNumber
            }
            console.log(res.data)
            this.curOrderId = resData.businessId // 单据Id
            if (type === 'SUBMIT') {
              params.operateType = this.$t('vendorMod.submitReciept') // 提交单据
              // 提交
              // 走工作流
              await this.getreviewFormDetail(this.curOrderId)
              await this.handlerAfter(type)
            } else {
              if (!this.curOrderId) {
                this.$emit('tab-remove', 'quaOfReviewDetail')
                this.__setTabTodo('quaOfReviewList.getQuerydata') // 查询列表数据
              }
              params.operateType = this.$t('vendorMod.saveReciept') // 暂存单据
              // 暂存
              this.curOpt = 'edit'
              this.fileRefresh = true
              this.getreviewFormDetail(this.curOrderId)
              this.$message({
                message: res.message,
                type: 'success'
              })
            }
            this.__setTabTodo('quaOfReviewList.getQuerydata') // 查询列表数据

            return params
          })
          .catch(err => {
            resolve(false)
            console.log(err)
          })
      })
    },
    back () {
      this.$emit('tab-remove', this.$attrs.tabName)
      this.__setTabTodo('quaOfReviewList.getQuerydata') // 查询列表数据
    },
    afterProcessActionSuccess (data) {
      console.log('流程回调')
      console.log(data.btnMsg)
    }
  }
}
</script>
<style scoped lang="scss">
.el-link {
  font-size: 13px;
}

.topComment {
  text-align: center;
  margin-top: 10px;
}

.toRequired {
  color: #ff4949;
  padding-right: 4px;
  margin-left: 6px;
}

.the-quaOfReviewDetail-detail {
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }

  .el-table .el-date-editor {
    width: 135px;
  }

  .el-collapse-item__content>.el-button {
    margin-bottom: 5px;
  }

  .vendorAccessSteps {
    padding: 12px 5px;
    // border: 1px solid #efefef;
    // border-top: 0;
    // border-bottom: 1px solid #e6ebf5;
  }

  .commonPad {
    padding-bottom: 5px;
  }

  .catOpration {
    padding-left: 70px;
    position: relative;
    padding-top: 10px;

    .el-button {
      position: absolute;
      top: 12px;
      left: 0;
    }
  }
}
</style>
