<template>
  <el-container
    class="the-vendorGreenChannelDetail-detail"
    direction="vertical"
  >
    <el-main>
      <div class="companyInfoFill">
        <el-collapse
          v-model="activeDims"
          class="tab-form-style"
        >
          <!-- 企业基本信息 -->
          <el-collapse-item
            ref="base"
            :title="$t('vendorMod.baseInfo')"
            name="2"
          >
            <el-form
              class="base-form-info form-fill-style"
              :model="baseInfoModel.baseInfoForm"
              :rules="baseInfoModel.rules"
              :show-message="false"
              :disabled="curOpt === 'view'"
            >
              <srm-row>
                <srm-col :initCol="3">
                  <!-- 境内外关系 -->
                  <el-form-item
                    prop="overseasRelation"
                    :label="$t('vendorMod.overseasRelation')"
                  >
                    <DictSelect
                      v-model="baseInfoModel.baseInfoForm.overseasRelation"
                      code="RELATION"
                      @change="overseasChangeHandle"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col
                  v-if="curRel === 'INSIDE'"
                  :initCol="3"
                >
                  <!-- 企业性质 -->
                  <el-form-item
                    prop="companyType"
                    :label="$t('vendorMod.companyType')"
                  >
                    <DictSelect
                      v-model="baseInfoModel.baseInfoForm.companyType"
                      code="COMPANY_NATURE"
                      @change="companyTypeChangeHandle"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="3">
                  <!-- 营业执照 -->
                  <el-form-item
                    prop="businessLicenseFileId"
                    :label="$t('vendorMod.businessLicense')"
                  >
                    <div style="margin-top:24px;">
                      <SrmCommonFile
                        :extra-data="fileInfo"
                        :default-file="{
                          fileId: baseInfoModel.baseInfoForm.businessLicenseFileId,
                          fileName: baseInfoModel.baseInfoForm.businessLicense
                        }"
                        :readonly="false"
                        @on-change="({file}) => handleUploadSuccess(file)"
                      />
                    </div>
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="3">
                  <!-- 企业名称 -->
                  <el-form-item
                    prop="companyName"
                    :label="$t('vendorMod.companyName')"
                  >
                    <el-input v-model="baseInfoModel.baseInfoForm.companyName" />
                  </el-form-item>
                </srm-col>
                <!-- 个体户不用显示 -->
                <srm-col
                  v-if="curType !== 'GETI'"
                  :initCol="3"
                >
                  <!-- 注册资本(万元) -->
                  <el-form-item
                    prop="registeredCapital"
                    :label="$t('vendorMod.registeredCapital')"
                  >
                    <el-input
                      v-model="baseInfoModel.baseInfoForm.registeredCapital"
                      v-input-format="{ type: 'float' }"
                      :placeholder="$t('vendorMod.pleaseEnter')"
                      class="input-with-select"
                    >
                      <!-- 币种 -->
                      <dict-select
                        slot="append"
                        v-model="baseInfoModel.baseInfoForm.registCurrency"
                        code="currency"
                        :placeholder="$t('vendorMod.currencyCode')"
                        style="width: 110px"
                      />
                    </el-input>
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="3">
                  <!-- 成立日期 -->
                  <el-form-item
                    prop="companyCreationDate"
                    :label="$t('vendorMod.creationDate')"
                  >
                    <el-date-picker
                      v-model="baseInfoModel.baseInfoForm.companyCreationDate"
                      type="date"
                      :placeholder="$t('common.pleaseSelectDate')"
                      :format="$formatDatePicker"
                      value-format="timestamp"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="3">
                  <!-- 企业简称 -->
                  <el-form-item
                    prop="companyShortName"
                    :label="$t('vendorMod.companyShortName')"
                  >
                    <el-input v-model="baseInfoModel.baseInfoForm.companyShortName" />
                  </el-form-item>
                </srm-col>
                <!-- 只有境内供应商有 -->
                <srm-col
                  v-if="curRel === 'INSIDE'"
                  :initCol="3"
                >
                  <!-- 统一社会信用代码 -->
                  <el-form-item
                    prop="lcCode"
                    :label="$t('vendorMod.lcCode')"
                  >
                    <el-input v-model="baseInfoModel.baseInfoForm.lcCode" />
                  </el-form-item>
                </srm-col>
                <!-- 只有境外供应商有 -->
                <srm-col
                  v-if="curRel === 'OUT'"
                  :initCol="3"
                >
                  <!-- DUNS编号 -->
                  <el-form-item
                    prop="dunsCode"
                    :label="$t('vendorMod.dunsCode')"
                  >
                    <el-input v-model="baseInfoModel.baseInfoForm.dunsCode" />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="3">
                  <!-- 法人代表 -->
                  <el-form-item
                    prop="legalPerson"
                    :label="$t('vendorMod.legalPerson')"
                  >
                    <el-input v-model="baseInfoModel.baseInfoForm.legalPerson" />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="3">
                  <!-- 登记机关 -->
                  <el-form-item
                    prop="registrationAuthority"
                    :label="$t('vendorMod.registrationAuthority')"
                  >
                    <el-input v-model="baseInfoModel.baseInfoForm.registrationAuthority" />
                  </el-form-item>
                </srm-col>
                <!-- 个体户不用显示 -->
                <srm-col
                  v-if="curType !== 'GETI'"
                  :initCol="3"
                >
                  <!-- 营业日期从 -->
                  <el-form-item
                    prop="businessStartDate"
                    :label="$t('vendorMod.businessStartFrom')"
                  >
                    <el-date-picker
                      v-model="baseInfoModel.baseInfoForm.businessStartDate"
                      type="date"
                      :placeholder="$t('common.pleaseSelectDate')"
                      :format="$formatDatePicker"
                      value-format="timestamp"
                    />
                  </el-form-item>
                </srm-col>
                <!-- 个体户不用显示 -->
                <srm-col
                  v-if="curType !== 'GETI'"
                  :initCol="3"
                >
                  <!-- 营业日期至 -->
                  <el-form-item
                    prop="businessEndDate"
                    :label="$t('vendorMod.businessEndAt')"
                  >
                    <el-date-picker
                      v-model="baseInfoModel.baseInfoForm.businessEndDate"
                      type="date"
                      :placeholder="$t('common.pleaseSelectDate')"
                      :format="$formatDatePicker"
                      value-format="timestamp"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="3">
                  <!-- 营业地址（国家/地区） -->
                  <el-form-item
                    prop="companyCountry"
                    :label="$t('vendorMod.businessAddr')"
                  >
                    <DictSelect
                      v-model="baseInfoModel.baseInfoForm.companyCountry"
                      code="country"
                      :disabled="curRel === 'INSIDE'"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col
                  v-if="curRel === 'INSIDE'"
                  :initCol="3"
                >
                  <!-- 省份/州 -->
                  <el-form-item
                    prop="companyProvince"
                    :label="$t('vendorMod.province')"
                  >
                    <DictSelect
                      v-model="baseInfoModel.baseInfoForm.companyProvince"
                      code="PROVINCE"
                      custom-select-type="PROVINCE"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col
                  v-if="curRel === 'INSIDE'"
                  :initCol="3"
                >
                  <!-- 城市 -->
                  <el-form-item
                    prop="companyCity"
                    :label="$t('vendorMod.city')"
                  >
                    <DictSelect
                      v-model="baseInfoModel.baseInfoForm.companyCity"
                      :code="baseInfoModel.baseInfoForm.companyProvince"
                      custom-select-type="CITY"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="1">
                  <!-- 详细地址 -->
                  <el-form-item
                    prop="companyAddress"
                    :label="$t('components.address.detailAddress')"
                  >
                    <el-input v-model="baseInfoModel.baseInfoForm.companyAddress" />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="2">
                  <!-- 营业范围 -->
                  <el-form-item
                    prop="businessScope"
                    :label="$t('vendorMod.businessScope')"
                  >
                    <el-input v-model="baseInfoModel.baseInfoForm.businessScope" />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="2">
                  <!-- 营业状态 -->
                  <el-form-item
                    prop="companyStatus"
                    :label="$t('vendorMod.businessStatus')"
                  >
                    <DictSelect
                      v-model="baseInfoModel.baseInfoForm.companyStatus"
                      code="COMPANY_STATUS"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="2">
                  <!-- 准入日期 -->
                  <el-form-item
                    prop="approvedDate"
                    :label="$t('vendorMod.permitDate')"
                  >
                    <el-date-picker
                      v-model="baseInfoModel.baseInfoForm.approvedDate"
                      type="date"
                      :placeholder="$t('common.pleaseSelectDate')"
                      :format="$formatDatePicker"
                      value-format="timestamp"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="2">
                  <el-form-item
                    :label="$t('vendorMod.categoryRels')"
                    prop="categoryRels"
                  >
                    <Treeselect
                      v-model="baseInfoModel.baseInfoForm.categoryRels"
                      :disabled="curOpt === 'view'"
                      :max-height="220"
                      :normalizer="categoryNormalizer"
                      :no-children-text="$t('vendorMod.noChildrenText')"
                      :placeholder="$t('vendorMod.msgCategoryNormalizer')"
                      :append-to-body="true"
                      value-format="object"
                      :options="categoryTreeOptions"
                      :multiple="true"
                      flatten-search-results
                      flat
                      :load-options="catLoadOptions"
                      @input="treeSelectChange"
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-form>
            <el-form v-if="baseInfoDimFieldContexts.length > 0">
              <srm-row>
                <!-- 拓展字段显示 -->
                <srm-col
                  v-for="item in baseInfoDimFieldContexts"
                  :key="item.fieldConfigId"
                  :initCol="3"
                >
                  <el-form-item
                    :prop="item.fieldCode"
                    :label="item.fieldName"
                  >
                    <el-input v-model="baseDimModel[item.fieldCode]" />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-form>
          </el-collapse-item>
          <!-- 财务信息 -->
          <el-collapse-item
            ref="finance"
            name="3"
            :title="$t('vendorMod.financeInfo')"
          >
            <p
              v-if="curOpt !== 'view'"
              class="sub_header"
            >
              <el-button
                type="primary"
                @click="addFinance"
              >
                <!-- 添加 -->
                {{ $t('common.new') }}
              </el-button>
            </p>
            <el-table
              ref="financeTable"
              :data="financeInfoData"
              style="width: 100%"
              border
              max-height="250px"
            >
              <!-- 分配组织 -->
              <el-table-column
                align="center"
                prop="orgId"
                :label="$t('vendorMod.assignOrg')"
                min-width="200"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <OrganizationSelectTree
                    v-model="scope.row.orgId"
                    :scope="scope.row"
                    @select="addOrgHandle"
                  />
                </template>
              </el-table-column>
              <!-- 结算币种 -->
              <el-table-column
                align="center"
                prop="clearCurrency"
                :label="$t('vendorMod.clearCurrency')"
                width="150"
              >
                <template slot-scope="scope">
                  <dict-select
                    v-model="scope.row.clearCurrency"
                    code="currency"
                  />
                </template>
              </el-table-column>
              <!-- 付款方式 -->
              <el-table-column
                align="center"
                prop="paymentMethod"
                :label="$t('vendorMod.paymentMethod')"
                width="150"
              >
                <template slot-scope="scope">
                  <DictSelect
                    v-model="scope.row.paymentMethod"
                    code="PAYMENT_METHOD"
                  />
                </template>
              </el-table-column>
              <!-- 付款条件 -->
              <el-table-column
                align="center"
                prop="paymentTerms"
                :label="$t('vendorMod.paymentTerms')"
                width="150"
              >
                <template slot-scope="scope">
                  <DictSelect
                    v-model="scope.row.paymentTerms"
                    code="PAYMENT_TERMS"
                  />
                </template>
              </el-table-column>
              <!-- 税率（％） -->
              <el-table-column
                align="center"
                prop="taxKey"
                :label="$t('vendorMod.taxRate')"
                width="150"
              >
                <template slot-scope="scope">
                  <dict-select
                    v-model="scope.row.taxKey"
                    code="tax"
                    @change-value="(val,dictItem) => getTaxRateObj(val,dictItem,scope.row)"
                  />
                </template>
              </el-table-column>
              <!-- 发票限额（单位：万元） -->
              <el-table-column
                align="center"
                prop="limitAmount"
                :label="$t('vendorMod.limitAmount')"
                width="180"
              >
                <template slot-scope="scope">
                  <DictSelect
                    v-model="scope.row.limitAmount"
                    code="INVOICE_LIMIT"
                  />
                </template>
              </el-table-column>
              <!-- 拓展字段 [[-->
              <el-table-column
                v-for="col in financeDimFieldContexts"
                v-if="financeDimFieldContexts.length > 0"
                :key="col.fieldId"
                :prop="col.fieldCode"
                :label="col.fieldName"
                width="110px"
              >
                <template slot-scope="scope">
                  <el-input v-model="scope.row[col.fieldCode]" />
                </template>
              </el-table-column>
              <!-- 拓展字段 ]]]-->
              <el-table-column
                align="center"
                prop="operation"
                :label="$t('common.operation')"
                width="100"
                fixed="right"
              >
                <template slot-scope="scope">
                  <!-- 删除 -->
                  <el-button
                    :disabled="curOpt === 'view'"
                    type="text"
                    @click="financeDel(scope.$index, scope.row)"
                  >
                    {{ $t('common.delete') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
          <!-- 银行信息 -->
          <el-collapse-item
            ref="bank"
            :title="$t('components.bank.accountInfo')"
            name="4"
          >
            <p
              v-if="curOpt !== 'view'"
              class="sub_header"
            >
              <el-button
                type="primary"
                @click="addBank"
              >
                <!-- 添加 -->
                {{ $t('common.new') }}
              </el-button>
            </p>
            <el-table
              ref="bankTable"
              :data="bankData"
              style="width: 100%"
              border
              max-height="250px"
            >
              <!-- 分配组织 -->
              <el-table-column
                align="center"
                prop="orgId"
                :label="$t('vendorMod.assignOrg')"
                min-width="200"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <OrganizationSelectTree
                    v-model="scope.row.orgId"
                    :scope="scope.row"
                    @select="addOrgHandle"
                  />
                </template>
              </el-table-column>
              <!-- 开户行名称 -->
              <el-table-column
                align="center"
                prop="openingBank"
                :label="$t('components.bank.branchBankName')"
                width="200"
              >
                <template slot-scope="scope">
                  <el-input v-model="scope.row.openingBank" />
                </template>
              </el-table-column>
              <!-- 联行编码 -->
              <el-table-column
                align="center"
                prop="unionCode"
                :label="$t('vendorMod.unionCode')"
                width="200"
              >
                <template slot-scope="scope">
                  <el-input v-model="scope.row.unionCode" />
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="swiftCode"
                label="SWIFT CODE"
                width="200"
              >
                <template slot-scope="scope">
                  <el-input v-model="scope.row.swiftCode" />
                </template>
              </el-table-column>
              <!-- 账户名称 -->
              <el-table-column
                align="center"
                prop="bankAccountName"
                :label="$t('components.bank.accountName')"
                width="200"
              >
                <template slot-scope="scope">
                  <el-input v-model="scope.row.bankAccountName" />
                </template>
              </el-table-column>
              <!-- 银行账号 -->
              <el-table-column
                align="center"
                prop="bankAccount"
                :label="$t('components.bank.bankAccount')"
                width="200"
              >
                <template slot-scope="scope">
                  <el-input v-model="scope.row.bankAccount" />
                </template>
              </el-table-column>
              <!-- 币种 -->
              <el-table-column
                align="center"
                prop="currencyCode"
                :label="$t('vendorMod.currencyCode')"
                width="200"
              >
                <template slot-scope="scope">
                  <dict-select
                    v-model="scope.row.currencyCode"
                    code="currency"
                  />
                </template>
              </el-table-column>
              <!-- 账号类型 -->
              <el-table-column
                align="center"
                prop="accountType"
                :label="$t('vendorMod.accountType')"
                width="200"
              >
                <template slot-scope="scope">
                  <DictSelect
                    v-model="scope.row.accountType"
                    code="BANK_ACCOUNT_TYPE"
                  />
                </template>
              </el-table-column>
              <!-- 上传开户证明 -->
              <el-table-column
                :label="$t('vendorMod.proof')"
                width="180"
              >
                <template slot-scope="scope">
                  <SrmCommonFile
                    :extra-data="fileInfo"
                    :default-file="{
                      fileId: scope.row.proofFileId,
                      fileName: scope.row.proof
                    }"
                    :readonly="false"
                    @on-change="({file}) => bankHandleUploadSuccess(file,scope.row)"
                  />
                </template>
              </el-table-column>
              <!-- 拓展字段 [[-->
              <el-table-column
                v-for="col in bankDimFieldContexts"
                v-if="bankDimFieldContexts.length > 0"
                :key="col.fieldId"
                :prop="col.fieldCode"
                :label="col.fieldName"
                width="110px"
              >
                <template slot-scope="scope">
                  <el-input v-model="scope.row[col.fieldCode]" />
                </template>
              </el-table-column>
              <!-- 拓展字段 ]]]-->
              <el-table-column
                align="center"
                prop="operation"
                :label="$t('common.operation')"
                width="100"
                fixed="right"
              >
                <template slot-scope="scope">
                  <el-button
                    :disabled="curOpt === 'view'"
                    type="text"
                    @click="bankeDel(scope.$index, scope.row)"
                  >
                    <!-- 删除 -->
                    {{ $t('common.delete') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
          <!-- 联系人信息 -->
          <el-collapse-item
            ref="contact"
            :title="$t('dataConfMod.contactInfo')"
            name="5"
          >
            <p
              v-if="curOpt !== 'view'"
              class="sub_header"
            >
              <el-button
                type="primary"
                @click="addContact"
              >
                {{ $t('common.new') }}
              </el-button>
            </p>
            <el-table
              ref="contactTable"
              :data="contactData"
              style="width: 100%"
              border
              max-height="250px"
            >
              <!-- 联系人姓名 -->
              <el-table-column
                align="center"
                prop="contactName"
                :label="$t('dataConfMod.contactName')"
                min-width="100"
              >
                <template slot-scope="scope">
                  <el-input v-model="scope.row.contactName" />
                </template>
              </el-table-column>
              <!-- 手机号码 -->
              <el-table-column
                align="center"
                prop="mobileNumber"
                :label="$t('vendorMod.mobilePhone')"
                min-width="150"
              >
                <template slot-scope="scope">
                  <el-input v-model="scope.row.mobileNumber" />
                </template>
              </el-table-column>
              <!-- 座机号码 -->
              <el-table-column
                align="center"
                prop="phoneNumber"
                :label="$t('vendorMod.telPhone')"
                min-width="150"
              >
                <template slot-scope="scope">
                  <el-input v-model="scope.row.phoneNumber" />
                </template>
              </el-table-column>
              <!-- 邮件地址 -->
              <el-table-column
                align="center"
                prop="email"
                :label="$t('vendorMod.email')"
                min-width="150"
              >
                <template slot-scope="scope">
                  <el-input v-model="scope.row.email" />
                </template>
              </el-table-column>
              <!-- 联系人地址 -->
              <el-table-column
                align="center"
                prop="contactAddress"
                :label="$t('vendorMod.contactAddr')"
                min-width="150"
              >
                <template slot-scope="scope">
                  <el-input v-model="scope.row.contactAddress" />
                </template>
              </el-table-column>
              <!-- 人员职务 -->
              <el-table-column
                align="center"
                prop="position"
                :label="$t('vendorMod.position')"
                min-width="150"
              >
                <template slot-scope="scope">
                  <el-input v-model="scope.row.position" />
                </template>
              </el-table-column>
              <!-- 传真号码 -->
              <el-table-column
                align="center"
                prop="taxNumber"
                :label="$t('vendorMod.taxNumber')"
                min-width="150"
              >
                <template slot-scope="scope">
                  <el-input v-model="scope.row.taxNumber" />
                </template>
              </el-table-column>
              <!-- 拓展字段 [[-->
              <el-table-column
                v-for="col in contactDimFieldContexts"
                v-if="contactDimFieldContexts.length > 0"
                :key="col.fieldId"
                :prop="col.fieldCode"
                :label="col.fieldName"
                min-width="110px"
              >
                <template slot-scope="scope">
                  <el-input v-model="scope.row[col.fieldCode]" />
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
                  <el-button
                    :disabled="curOpt === 'view'"
                    type="text"
                    @click="contactDel(scope.$index, scope.row)"
                  >
                    {{ $t('common.delete') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
          <!-- 合作组织 -->
          <el-collapse-item
            ref="org"
            :title="$t('vendorMod.cooOrg')"
            name="6"
          >
            <p
              v-if="curOpt !== 'view'"
              class="sub_header"
            >
              <el-button
                type="primary"
                @click="addOrg"
              >
                <!-- 添加 -->
                {{ $t('common.new') }}
              </el-button>
            </p>
            <el-table
              ref="orgTable"
              :data="orgInfoData"
              style="width: 100%"
              border
              max-height="250px"
            >
              <!-- 合作组织 -->
              <el-table-column
                align="center"
                prop="orgId"
                :label="$t('vendorMod.cooOrg')"
                min-width="200"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <OrganizationSelectTree
                    v-model="scope.row.orgId"
                    :scope="scope.row"
                    @select="addOrgHandle"
                  />
                </template>
              </el-table-column>
              <!-- 组织状态 -->
              <el-table-column
                align="center"
                prop="serviceStatus"
                :label="$t('vendorMod.orgServiceStatus')"
                width="200"
              >
                <template slot-scope="scope">
                  <DictSelect
                    v-model="scope.row.serviceStatus"
                    code="ORG_STATUS"
                  />
                </template>
              </el-table-column>
              <!-- 生效日期 -->
              <el-table-column
                align="center"
                prop="startDate"
                :label="$t('vendorMod.startDate')"
                width="160"
              >
                <template slot-scope="scope">
                  <el-date-picker
                    v-model="scope.row.startDate"
                    type="date"
                    :format="$formatDatePicker"
                    value-format="yyyy-MM-dd"
                    :placeholder="$t('common.pleaseSelectDate')"
                  />
                </template>
              </el-table-column>
              <!-- 失效日期 -->
              <el-table-column
                align="center"
                prop="endDate"
                :label="$t('vendorMod.endDate')"
                width="160"
              >
                <template slot-scope="scope">
                  <el-date-picker
                    v-model="scope.row.endDate"
                    type="date"
                    :format="$formatDatePicker"
                    value-format="yyyy-MM-dd"
                    :placeholder="$t('common.pleaseSelectDate')"
                  />
                </template>
              </el-table-column>
              <!-- 拓展字段 [[-->
              <el-table-column
                v-for="col in orgDimFieldContexts"
                v-if="orgDimFieldContexts.length > 0"
                :key="col.fieldId"
                :prop="col.fieldCode"
                :label="col.fieldName"
                width="110px"
              >
                <template slot-scope="scope">
                  <el-input v-model="scope.row[col.fieldCode]" />
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
                  <el-button
                    :disabled="curOpt === 'view'"
                    type="text"
                    @click="orgDel(scope.$index, scope.row)"
                  >
                    <!-- 删除 -->
                    {{ $t('common.delete') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
          <!-- 组织与品类 -->
          <el-collapse-item
            ref="orgCat"
            :title="$t('vendorMod.orgCat')"
            name="7"
          >
            <p
              v-if="curOpt !== 'view'"
              class="sub_header"
            >
              <el-button
                type="primary"
                @click="addOrgCategory"
              >
                <!-- 添加 -->
                {{ $t('common.new') }}
              </el-button>
            </p>
            <el-table
              ref="orgCatTable"
              :data="orgCategoryData"
              style="width: 100%"
              border
              max-height="250px"
            >
              <!-- 合作组织 -->
              <el-table-column
                align="center"
                prop="orgId"
                :label="$t('vendorMod.cooOrg')"
                min-width="200"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <OrganizationSelectTree
                    v-model="scope.row.orgId"
                    :scope="scope.row"
                    @select="addOrgHandle"
                  />
                </template>
              </el-table-column>
              <!-- 采购品类 -->
              <el-table-column
                prop="categoryName"
                :label="$t('vendorMod.category')"
                width="150"
              >
                <template slot-scope="scope">
                  <template>
                    <CCategorySelect
                      v-model="scope.row.categoryName"
                      :scope="scope.row"
                      show-key="categoryName"
                      @select="comfirmSelect"
                    />
                  </template>
                </template>
              </el-table-column>
              <!-- 品类状态 -->
              <el-table-column
                align="center"
                prop="serviceStatus"
                :label="$t('vendorMod.catServiceStatus')"
                width="150"
              >
                <template slot-scope="scope">
                  <DictSelect
                    v-model="scope.row.serviceStatus"
                    code="CATEGORY_STATUS"
                  />
                </template>
              </el-table-column>
              <!-- 生效日期 -->
              <el-table-column
                align="center"
                prop="startDate"
                :label="$t('vendorMod.startDate')"
                width="160"
              >
                <template slot-scope="scope">
                  <el-date-picker
                    v-model="scope.row.startDate"
                    type="date"
                    :format="$formatDatePicker"
                    value-format="yyyy-MM-dd"
                    :placeholder="$t('common.pleaseSelectDate')"
                  />
                </template>
              </el-table-column>
              <!-- 失效日期 -->
              <el-table-column
                align="center"
                prop="endDate"
                :label="$t('vendorMod.endDate')"
                width="160"
              >
                <template slot-scope="scope">
                  <el-date-picker
                    v-model="scope.row.endDate"
                    type="date"
                    :format="$formatDatePicker"
                    value-format="yyyy-MM-dd"
                    :placeholder="$t('common.pleaseSelectDate')"
                  />
                </template>
              </el-table-column>
              <!-- 拓展字段 [[-->
              <el-table-column
                v-for="col in orgCatDimFieldContexts"
                v-if="orgCatDimFieldContexts.length > 0"
                :key="col.fieldId"
                :prop="col.fieldCode"
                :label="col.fieldName"
                width="110px"
              >
                <template slot-scope="scope">
                  <el-input v-model="scope.row[col.fieldCode]" />
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
                  <el-button
                    :disabled="curOpt === 'view'"
                    type="text"
                    @click="orgCategoryDel(scope.$index, scope.row)"
                  >
                    <!-- 删除 -->
                    {{ $t('common.delete') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
          <!-- 其他信息 -->
          <el-collapse-item
            ref="other"
            :title="$t('vendorMod.otherInfo')"
            name="8"
          >
            <el-form
              ref="oterForm"
              :model="otherModel.otherForm"
              :disabled="curOpt !== 'view'"
            >
              <srm-row>
                <srm-col :initCol="3">
                  <!-- 商业模式 -->
                  <el-form-item :label="$t('vendorMod.bizModel')">
                    <DictSelect
                      v-model="otherModel.otherForm.bizModel"
                      code="BIZ_MODEL"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="3">
                  <!-- 厂房性质 -->
                  <el-form-item :label="$t('vendorMod.factoryType')">
                    <DictSelect
                      v-model="otherModel.otherForm.factoryType"
                      code="FACTORY_TYPE"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="3">
                  <!-- 建筑面积（平方米） -->
                  <el-form-item :label="$t('vendorMod.floorArea')">
                    <el-input v-model="otherModel.otherForm.floorArea" />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="3">
                  <!-- 员工人数 -->
                  <el-form-item :label="$t('vendorMod.employeeQty')">
                    <DictSelect
                      v-model="otherModel.otherForm.employeeQty"
                      code="EMPLOYEE_QTY"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="3">
                  <!-- 公司网站 -->
                  <el-form-item :label="$t('vendorMod.companySite')">
                    <el-input v-model="otherModel.otherForm.companySite" />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="3">
                  <!-- 占地面积（平方米） -->
                  <el-form-item :label="$t('vendorMod.floorSpace')">
                    <el-input v-model="otherModel.otherForm.floorSpace" />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-form>
            <el-form v-if="otherDimFieldContexts.length > 0">
              <srm-row>
                <!-- 拓展字段显示 -->
                <srm-col
                  v-for="item in otherDimFieldContexts"
                  :key="item.fieldConfigId"
                  :initCol="3"
                >
                  <el-form-item
                    :prop="item.fieldCode"
                    :label="item.fieldName"
                  >
                    <el-input v-model="otherDimModel[item.fieldCode]" />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-form>
          </el-collapse-item>
          <!-- 相关认证信息 -->
          <el-collapse-item
            ref="attach"
            :title="$t('vendorMod.sceneAttachmentInfo')"
            name="9"
          >
            <FileDynamic
              ref="sceneAttachment"
              v-model="companyInfoFileList"
              scene-module-code="SCENE_SUPPLIER_ATTACHMENT"
              :business-id="companyId"
              :editable="curOpt === 'add' || curOpt === 'edit'"
            />
          </el-collapse-item>
        </el-collapse>
      </div>
      <!-- 进度条信息 -->
      <!-- 企业登记节点 -->
      <CFillProgress
        :node-name="$t('vendorMod.companyRegisterNode')"
        :data="computedNode"
        :percentage="true"
        @index-click="indexClickTo"
      />
      <!-- 操作按钮 -->
      <CToolbar>
        <template slot="right">
          <el-button
            v-if="curOpt !== 'view'"
            @click="stagingHandle"
          >
            <!-- 暂存 -->
            {{ $t('common.staging') }}
          </el-button>
          <el-button
            v-if="curOpt !== 'view'"
            type="primary"
            @click="submitHandle"
          >
            <!-- 提交 -->
            {{ $t('common.submit') }}
          </el-button>
          <el-button
            v-if="curOpt === 'view' && curRole === 'VENDOR'"
            type="primary"
            @click="changeApply"
          >
            <!-- 申请变更 -->
            {{ $t('vendorMod.applyForChange') }}
          </el-button>
        </template>
      </CToolbar>
      <!-- OCR 弹框 -->
      <COcr
        :visible.sync="ocrVisible"
        :file-upload-id="fileUploadId"
        @on-confirm="ocrConfirm"
        @close="ocrClose"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import CFillProgress from 'lib@/components/c-fill-progress'
import OrganizationSelectTree from 'lib@/components/organization-selector'
import CCategorySelect from 'lib@/components/c-category-select'
import vendorInfoChangeDetail from 'modb@/vendorManagementBuyer/views/vendorInfoChange/vendorInfoChangeDetail'
import COcr from 'lib@/components/c-ocr'
import Treeselect, { LOAD_CHILDREN_OPTIONS } from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import FileDynamic from '@/library/components/c-file-management/file-dynamic'
import { vendorArchival } from 'mods@/vendorManagementSupplier/api/index'

import _omit from 'lodash/omit'
export default {
  name: 'VendorGreenChannelDetail',
  components: {
    CToolbar,
    CFillProgress,
    Treeselect,
    COcr,
    OrganizationSelectTree,
    CCategorySelect,
    FileDynamic
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      companyInfoFileList: [],
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'vendorArchivalInfo',
        fileType: 'images'
      },
      companyId: null,
      curRel: '',
      curType: '',
      isEnableOcr: 'N', // 是否启用OCR营业执照识别
      fileUploadId: null, // ocr 文件入参ID
      ocrVisible: false, // ocr 弹窗
      curOpt: 'view',
      fileRefresh: false, // 刷新附件
      orgDialog: false,
      activeDims: ['1', '11', '2', '3', '4', '5', '6', '7', '8', '9'],
      nodeData: [
        // 进度条节点信息
        {
          code: 'base',
          name: this.$t('vendorMod.companyBaseInfo'),
          percentage: 0
        }, // 企业基本信息
        {
          code: 'finance',
          name: this.$t('vendorMod.financeInfo'),
          percentage: 0
        }, // 财务信息
        { code: 'bank', name: this.$t('vendorMod.bankInfo'), percentage: 0 }, // 银行信息
        {
          code: 'contact',
          name: this.$t('vendorMod.contactInfo'),
          percentage: 0
        }, // 联系人信息
        { code: 'org', name: this.$t('vendorMod.cooOrg'), percentage: 0 }, // 合作组织
        { code: 'orgCat', name: this.$t('vendorMod.orgCat'), percentage: 0 }, // 组织与品类
        { code: 'other', name: this.$t('vendorMod.otherInfo'), percentage: 0 }, // 其他信息
        {
          code: 'attach',
          name: this.$t('vendorMod.sceneAttachmentInfo'),
          percentage: 0
        } // 相关认证信息
      ],
      relations: [], // 境内外管理
      natureList: [], // 企业性质
      factoryType: [], // 厂房性质
      bizModel: [], // 商业规模
      employeeQyt: [], // 员工规模
      companyStatus: [], // 营业状态
      bankAccountType: [], // 账户类型
      paymantType: [], // 付款类型
      invoiceLimit: [], // 发票限额
      paymantTerms: [], // 付款条件
      catStatus: [], // 品类状态
      orgStatus: [], // 组织服务状态
      categoryTreeOptions: [],
      baseInfoModel: {
        // 基础信息 baseInfoModel.rules
        baseInfoForm: {
          overseasRelation: '', // 海内外
          companyType: '', // 性质
          companyName: '', // 企业名称
          businessLicenseFileId: '', // 营业执照
          businessLicense: '',
          registeredCapital: '', // 注册资金
          registCurrency: '', // 币种
          companyCreationDate: '', // 成立日期
          companyShortName: '', // 企业简称
          lcCode: '', // 统一社会信用代码
          dunsCode: '', // dunsCode
          legalPerson: '', // 法定代表人
          registrationAuthority: '', // 登记机关
          businessStartDate: '', // 营业日期 开始
          businessEndDate: '', // 营业日期 结束
          companyCountry: '', // 国家
          companyProvince: '', // 省
          companyCity: '', // 市
          companyAddress: '', // 详细地址
          businessScope: '', // 业务范围
          companyStatus: '', // 经营状态
          approvedDate: '', // 准入日期
          isBacklist: 'N', // 是否黑名单
          categoryRels: [], // 可供品类
          dimFieldContexts: {}
        },
        rules: {}
      },
      baseRules: {
        // 请输入
        overseasRelation: [{ required: true, message: this.$t('common.pleaseInput') }],
        companyName: [{ required: true, message: this.$t('common.pleaseInput') }],
        companyCountry: [{ required: true, message: this.$t('common.pleaseInput') }],
        companyAddress: [{ required: true, message: this.$t('common.pleaseInput') }],
        categoryRels: [{ required: true, message: this.$t('common.pleaseInput') }]
      },
      outRules: {
        registeredCapital: [{ required: true, message: this.$t('common.pleaseInput') }],
        companyCreationDate: [{ required: true, message: this.$t('common.pleaseInput') }],
        legalPerson: [{ required: true, message: this.$t('common.pleaseInput') }],
        businessScope: [{ required: true, message: this.$t('common.pleaseInput') }],
        companyStatus: [{ required: true, message: this.$t('common.pleaseInput') }]
      },
      sosoRules: {
        overseasRelation: [{ required: true, message: this.$t('common.pleaseInput') }],
        companyType: [{ required: true, message: this.$t('common.pleaseInput') }],
        businessLicenseFileId: [{ required: true, message: this.$t('common.pleaseInput') }],
        companyCreationDate: [{ required: true, message: this.$t('common.pleaseInput') }],
        businessScope: [{ required: true, message: this.$t('common.pleaseInput') }],
        companyStatus: [{ required: true, message: this.$t('common.pleaseInput') }]
      },
      innerRules: {
        businessStartDate: [{ required: true, message: this.$t('common.pleaseInput') }],
        businessEndDate: [{ required: true, message: this.$t('common.pleaseInput') }],
        companyType: [{ required: true, message: this.$t('common.pleaseInput') }],
        lcCode: [{ required: true, message: this.$t('common.pleaseInput') }],
        legalPerson: [{ required: true, message: this.$t('common.pleaseInput') }],
        businessLicenseFileId: [{ required: true, message: this.$t('common.pleaseInput') }],
        registeredCapital: [{ required: true, message: this.$t('common.pleaseInput') }],
        companyCreationDate: [{ required: true, message: this.$t('common.pleaseInput') }],
        businessScope: [{ required: true, message: this.$t('common.pleaseInput') }],
        companyStatus: [{ required: true, message: this.$t('common.pleaseInput') }],
        companyProvince: [{ required: true, message: this.$t('common.pleaseInput') }],
        companyCity: [{ required: true, message: this.$t('common.pleaseInput') }],
        registrationAuthority: [{ required: true, message: this.$t('common.pleaseInput') }]
      },
      unProfitRules: {
        companyType: [{ required: true, message: this.$t('common.pleaseInput') }],
        companyProvince: [{ required: true, message: this.$t('common.pleaseInput') }],
        companyCity: [{ required: true, message: this.$t('common.pleaseInput') }]
      },
      baseInfoDimFieldContexts: [], // 基础信息 拓展字段
      financeDimFieldContexts: [], // 财务信息拓展字段
      bankDimFieldContexts: [], // 银行信息拓展字段
      contactDimFieldContexts: [], // 联系人拓展字段
      orgDimFieldContexts: [], // 合作组织拓展字段
      orgCatDimFieldContexts: [], // 组织和品类拓展字段
      baseDimModel: {}, // 基础信息拓展字段
      otherDimFieldContexts: [], // 其他信息拓展字段
      otherDimModel: {},
      financeInfoData: [], // 财务信息
      bankData: [], // 银行信息
      contactData: [], // 联系人
      orgInfoData: [], // 合作组织
      orgCategoryData: [], // 组织和品类
      otherModel: {
        // 其他信息
        otherForm: {
          bizModel: '',
          floorArea: '',
          factoryType: '',
          employeeQty: '',
          companySite: '',
          floorSpace: '',
          dimFieldContexts: {}
        }
      },
      curRole: this.$store.getters.userType, // 用户类型 BUYER || VENDOR
      attachTableData: [] // 附件
    }
  },
  computed: {
    computedNode: function () {
      return this.nodeData
    }
  },
  created () {
    this.companyId = this.$store.getters.companyId
    this.fatchOldData() // 查询旧数据
    this.switchBaseRules() // 切换基础信息的必填项
    // 加载一级可选品类
    vendorArchival.getCatChildrenData({ categoryId: -1 }).then((response) => {
      this.categoryTreeOptions = response.data.forEach((i) => ({
        ...i,
        children: null
      }))
    })
  },
  methods: {
    getTaxRateObj (val, dictItem, row) {
      row.taxRate = dictItem.key
    },
    // 品类
    categoryNormalizer (node) {
      const result = {
        id: node.categoryId,
        label: node.categoryName
      }
      return result
    },
    // 品类选择值修改
    treeSelectChange (nodes) {},
    // 品类加载
    catLoadOptions ({ action, parentNode, callback }) {
      if (action === LOAD_CHILDREN_OPTIONS) {
        vendorArchival.getCatChildrenData({ categoryId: parentNode.categoryId })
          .then((res) => {
            parentNode.children = res.data.map((i) => ({ ...i, children: null }))
            callback()
          })
          .catch((err) => {
            parentNode.children = null
            callback(new Error(err.message))
          })
      }
    },
    // 计算字段
    computedBaseFields () {
      let overseasRelation = this.baseInfoModel.baseInfoForm.overseasRelation // 境内外类型
      let companyType = this.baseInfoModel.baseInfoForm.companyType // 公司属性
      let resArr = []
      if (overseasRelation === 'OUT') {
        resArr = [
          'overseasRelation',
          'companyName',
          'businessLicenseFileId',
          'registeredCapital',
          'registCurrency',
          'companyCreationDate',
          'companyShortName',
          'dunsCode',
          'legalPerson',
          'registrationAuthority',
          'businessStartDate',
          'businessEndDate',
          'companyCountry',
          'companyAddress',
          'businessScope',
          'companyStatus',
          'approvedDate',
          'categoryRels'
        ]
      } else {
        // INSIDE
        if (companyType === 'GETI') {
          // 个体户
          resArr = [
            'overseasRelation',
            'companyType',
            'companyName',
            'businessLicenseFileId',
            'companyCreationDate',
            'companyShortName',
            'lcCode',
            'legalPerson',
            'registrationAuthority',
            'companyCountry',
            'companyProvince',
            'companyCity',
            'companyAddress',
            'businessScope',
            'companyStatus',
            'approvedDate',
            'categoryRels'
          ]
        } else {
          resArr = [
            'overseasRelation',
            'companyType',
            'companyName',
            'businessLicenseFileId',
            'registeredCapital',
            'registCurrency',
            'companyCreationDate',
            'companyShortName',
            'lcCode',
            'legalPerson',
            'registrationAuthority',
            'businessStartDate',
            'businessEndDate',
            'companyCountry',
            'companyProvince',
            'companyCity',
            'companyAddress',
            'businessScope',
            'companyStatus',
            'approvedDate',
            'categoryRels'
          ]
        }
      }
      return resArr
    },
    // 计算拓展字段
    computedDimFields (dimdata) {
      let arr = []
      dimdata.forEach((i) => {
        arr.push(i.fieldCode)
      })
      return arr
    },
    // 切换必填规则
    switchBaseRules () {
      this.baseInfoModel.rules = {}
      if (this.curRel === 'OUT') {
        // 境外
        this.baseInfoModel.rules = Object.assign(this.baseRules, this.outRules)
      } else {
        if (this.curType === 'GETI') {
          // 个体户
          this.baseInfoModel.rules = Object.assign(this.baseRules, this.sosoRules)
        } else if (this.curType === 'FEIYINGLI') {
          // 非盈利
          this.baseInfoModel.rules = Object.assign(this.baseRules, this.unProfitRules)
        } else {
          // 其他内部
          this.baseInfoModel.rules = Object.assign(this.baseRules, this.innerRules)
        }
      }
    },
    overseasChangeHandle (val) {
      this.curRel = val // 当前海内外关系
      if (val !== 'OUT') {
        // 境外
        this.baseInfoModel.baseInfoForm.companyCountry = 'CN'
      }
      this.switchBaseRules() // 切换基础信息必填规则
      this.getDimAttrConfig() // 属性配置信息
    },
    companyTypeChangeHandle (val) {
      this.curType = val // 当前公司属性
      this.switchBaseRules() // 切换基础信息必填规则
      this.getDimAttrConfig() // 属性配置信息
    },
    // 获取属性拓展字段
    getDimAttrConfig () {
      let parame = {}
      parame.overseasRelation = this.baseInfoModel.overseasRelation
      parame.companyType = this.baseInfoModel.companyType
      if (parame.overseasRelation === 'OUT') {
        parame.companyType = ''
      }
      vendorArchival.getConfigByTemplate(parame).then((res) => {
        if (res && res.data) {
          this.adaptDimFieldsHandle(res.data) // 适配拓展字段
        }
      })
    },
    // 适配拓展字段处理
    adaptDimFieldsHandle (data) {
      let companyInfo = []
      let bankInfo = []
      let contactInfo = []
      let otherInfo = []
      let orgInfo = []
      let financeInfo = []
      let orgCategory = []

      if (data && data.length > 0) {
        data.forEach((element) => {
          if (element.dimCode === 'companyInfo') {
            companyInfo = element.dimFieldConfigS
          } else if (element.dimCode === 'bankInfo') {
            bankInfo = element.dimFieldConfigS
          } else if (element.dimCode === 'contactInfo') {
            contactInfo = element.dimFieldConfigS
          } else if (element.dimCode === 'otherInfo') {
            otherInfo = element.dimFieldConfigS
          } else if (element.dimCode === 'orgInfo') {
            orgInfo = element.dimFieldConfigS
          } else if (element.dimCode === 'financeInfo') {
            financeInfo = element.dimFieldConfigS
          } else if (element.dimCode === 'orgCategory') {
            orgCategory = element.dimFieldConfigS
          }
        })
      }

      this.baseInfoDimFieldContexts = companyInfo
      this.financeDimFieldContexts = financeInfo
      this.bankDimFieldContexts = bankInfo
      this.contactDimFieldContexts = contactInfo
      this.orgDimFieldContexts = orgInfo
      this.orgCatDimFieldContexts = orgCategory
      this.otherDimFieldContexts = otherInfo

      this.$nextTick(() => {
        this.$refs.financeTable.doLayout()
      })
      this.$nextTick(() => {
        this.$refs.bankTable.doLayout()
      })
      this.$nextTick(() => {
        this.$refs.contactTable.doLayout()
      })
      this.$nextTick(() => {
        this.$refs.orgTable.doLayout()
      })
      this.$nextTick(() => {
        this.$refs.orgCatTable.doLayout()
      })
    },
    // 营业执照 上传附件成功
    handleUploadSuccess (file) {
      const { fileId = '', fileName = '' } = file || {}
      // 判断是否需要OCR识别 境内供应商 && 开启OCR
      if (fileId) {
        if (
          this.baseInfoModel.baseInfoForm.overseasRelation === 'INSIDE' &&
          this.isEnableOcr === 'Y'
        ) {
          this.fileUploadId = fileId // ocr 文件入参ID
          this.ocrVisible = true
        }
      }
      this.baseInfoModel.baseInfoForm.businessLicenseFileId = fileId.toString()
      this.baseInfoModel.baseInfoForm.businessLicense = fileName
    },
    // 附件删除
    handleRemove (fileId) {
      // this.materialModle.tableData[this.rowIndex].ctcAttachmentDto = {}
    },
    // 删除文件
    handleAttachmentRemove () {
      this.baseInfoModel.baseInfoForm.businessLicenseFileId = ''
      this.baseInfoModel.baseInfoForm.businessLicense = ''
    },
    // 银行文件上传【【
    // 附件删除
    bankButtonClick (index) {
      this.bankRowIndex = index
    },
    bankHandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.proofFileId = fileId.toString()
      row.proof = fileName
    },
    // 移除
    bankHandleRemove (fileId) {},
    // 删除银行证明文件
    bankHandleAttachmentRemove (row) {
      row.proofFileId = ''
      row.proof = ''
    },
    handleScriptProgress (percent) {},
    // 银行文件上传】】
    // 附件上传【【
    // table文件上传
    addFile () {
      this.attachTableData.unshift({
        add: true,
        attachmentPicFileId: null,
        attachmentPic: '',
        attachmentDiscription: '',
        attachmentValidDate: ''
      })
    },
    // 文件上传========[
    attachButtonClick (index) {
      this.attRowIndex = index
    },
    // 上传附件成功
    tableHandleUploadSuccess (file) {
      const { id, name } = file
      this.attachTableData[this.attRowIndex].attachmentPicFileId = id.toString()
      this.attachTableData[this.attRowIndex].attachmentPic = name
      this.$nextTick(() => {
        this.$refs.attachTable.doLayout()
      })
    },
    // 附件删除
    tableHandleRemove (fileId) {
      // this.attachTableData[this.rowIndex].ctcAttachmentDto = {}
    },
    tableHandleScriptProgress (percent) {},
    // 删除文件
    tableHandleAttachmentRemove (row) {
      row.attachmentPicFileId = ''
      row.attachmentPic = ''
    },
    // 行删除
    handleDelClick (index, row) {
      let attachFileId = row.attachFileId
      if (attachFileId) {
        vendorArchival.deleteAttachById({ attachFileId }).then((res) => {
          this.attachTableData.splice(index, 1)
          this.$message({
            message: res.message,
            type: 'success'
          })
        })
      } else {
        this.attachTableData.splice(index, 1)
      }
    },
    // 文件上传========]
    // 附件上传】】
    // 选择品类
    catButtonClick (index) {
      this.catRowIndex = index
    },
    // 确认选择品类
    comfirmSelect (node, scope) {
      scope.categoryId = node ? node.categoryId : null
      scope.categoryName = node ? node.categoryName : ''
      scope.categoryCode = node ? node.categoryCode : ''
    },
    // 删除操作
    financeDel (index, row) {
      // 深处财务信息
      let financeInfoId = row.financeInfoId
      if (financeInfoId) {
        vendorArchival.financeInfoDel({ financeInfoId }).then((res) => {
          this.financeInfoData.splice(index, 1)
          this.$message({
            message: res.message,
            type: 'success'
          })
        })
      } else {
        this.financeInfoData.splice(index, 1)
      }
    },
    bankeDel (index, row) {
      // 删除银行信息
      let bankInfoId = row.bankInfoId
      if (bankInfoId) {
        vendorArchival.bankInfoDel({ bankInfoId }).then((res) => {
          this.bankData.splice(index, 1)
          this.$message({
            message: res.message,
            type: 'success'
          })
        })
      } else {
        this.bankData.splice(index, 1)
      }
    },
    contactDel (index, row) {
      // 删除联系人
      let contactInfoId = row.contactInfoId
      if (contactInfoId) {
        vendorArchival.contactInfoDel({ contactInfoId }).then((res) => {
          this.contactData.splice(index, 1)
          this.$message({
            message: res.message,
            type: 'success'
          })
        })
      } else {
        this.contactData.splice(index, 1)
      }
    },
    orgDel (index, row) {
      // 删除合作组织
      let orgInfoId = row.orgInfoId
      if (orgInfoId) {
        vendorArchival.orgInfoDel({ orgInfoId }).then((res) => {
          this.orgInfoData.splice(index, 1)
          this.$message({
            message: res.message,
            type: 'success'
          })
        })
      } else {
        this.orgInfoData.splice(index, 1)
      }
    },
    orgCategoryDel (index, row) {
      // 删除组织和品类
      let categoryId = row.categoryId
      if (categoryId) {
        vendorArchival.orgCategoryDel({ categoryId }).then((res) => {
          this.orgCategoryData.splice(index, 1)
          this.$message({
            message: res.message,
            type: 'success'
          })
        })
      } else {
        this.orgCategoryData.splice(index, 1)
      }
    },

    // 查询旧数据
    fatchOldData () {
      let companyId = this.companyId
      if (companyId) {
        vendorArchival.getCompanyForEdit({ companyId }).then((res) => {
          if (res) {
            if (res.data.companyInfo) {
              let companyInfo = res.data.companyInfo
              this.curRel = companyInfo.overseasRelation
              this.curType = companyInfo.companyType

              // 查询模板配置信息
              let pareme = {}
              pareme.overseasRelation = companyInfo.overseasRelation
              pareme.companyType = companyInfo.companyType
              if (pareme.overseasRelation === 'OUT') {
                pareme.companyType = ''
              }
              vendorArchival.getConfigByTemplate(pareme).then((res) => {
                if (res) {
                  this.adaptDimFieldsHandle(res.data) // 适配拓展字段
                }
              })

              this.baseInfoModel.baseInfoForm = companyInfo
              this.baseInfoModel.baseInfoForm = _omit(companyInfo, [
                'applicationDate',
                'creationDate',
                'lastUpdateDate'
              ])
              this.baseInfoModel.baseInfoForm.businessStartDate = companyInfo.businessStartDate
                ? this.$dayjs(companyInfo.businessStartDate).valueOf()
                : '' // 开始时间
              this.baseInfoModel.baseInfoForm.businessEndDate = companyInfo.businessEndDate
                ? this.$dayjs(companyInfo.businessEndDate).valueOf()
                : ''
              this.baseInfoModel.baseInfoForm.approvedDate = companyInfo.approvedDate
                ? this.$dayjs(companyInfo.approvedDate).valueOf()
                : ''
              this.baseInfoModel.baseInfoForm.backlistUpdatedDate = companyInfo.backlistUpdatedDate
                ? this.$dayjs(companyInfo.backlistUpdatedDate).valueOf()
                : ''

              this.baseDimModel = companyInfo.dimFieldContexts // 拓展字段值
              if (companyInfo.status === 'APPROVED') {
                this.curOpt = 'view'
              } else {
                this.curOpt = 'edit'
              }
            }
            if (res.data.otherInfo) {
              let otherInfo = res.data.otherInfo
              this.otherModel.otherForm.otherInfoId = otherInfo.otherInfoId
              this.otherModel.otherForm.bizModel = otherInfo.bizModel
              this.otherModel.otherForm.floorArea = otherInfo.floorArea
              this.otherModel.otherForm.factoryType = otherInfo.factoryType
              this.otherModel.otherForm.employeeQty = otherInfo.employeeQty
              this.otherModel.otherForm.companySite = otherInfo.companySite
              this.otherModel.otherForm.floorSpace = otherInfo.floorSpace
              this.otherDimModel = otherInfo.dimFieldContexts // 拓展字段值
            }
            if (res.data.fileUploads) {
              this.companyInfoFileList = res.data.fileUploads
            }
            this.$nextTick(() => {
              this.$refs.sceneAttachment.loadFileInfo()
            })
            this.bankData = res.data.bankInfos ? this.adaptResutData(res.data.bankInfos) || [] : []
            this.contactData = res.data.contactInfos
              ? this.adaptResutData(res.data.contactInfos) || []
              : []
            this.financeInfoData = res.data.financeInfos
              ? this.adaptResutData(res.data.financeInfos) || []
              : []
            this.orgInfoData = res.data.orgInfos ? this.adaptResutData(res.data.orgInfos) || [] : []
            this.orgCategoryData = res.data.orgCategorys
              ? this.adaptResutData(res.data.orgCategorys) || []
              : []
            this.attachTableData = res.data.attachFiles ? res.data.attachFiles : []
          }
        })
      }
    },
    // 返回数据适配
    adaptResutData (data) {
      if (data.length > 0) {
        data.forEach((elm) => {
          let dimFieldData = elm.dimFieldContexts || {}
          Object.keys(dimFieldData).forEach((key) => {
            elm[key] = dimFieldData[key]
          })
          delete elm.dimFieldContexts
          // if(elm.)
        })
        return data
      }
    },
    // 财务选择组织
    selectOrgHandle (index, data) {
      this.currentRowIndex = index
      this.curData = this[data]
      this.orgDialog = true
    },
    // 选择组织
    addOrgHandle (e, dd, scope) {
      scope.orgId = e ? e.organizationId : null
      scope.orgCode = e ? e.organizationCode : ''
      scope.orgName = e ? e.organizationName : ''
    },
    // 选择组织触发
    responsibleFocus (index) {
      this.currentRowIndex = index
      this.orgDialog = true
    },
    // 暂存
    stagingHandle () {
      this.dataHandle('stage')
    },
    // 提交
    submitHandle () {
      this.dataHandle('submit')
    },
    // 拓展字段塞值处理
    dimFieldsValHandel (odlArr, formObj) {
      Object.keys(formObj).forEach((key) => {
        odlArr.find((item) => {
          if (item.fieldCode === key) {
            item.fieldValue = formObj[key]
          }
        })
      })
      return odlArr
    },
    // 品类数据提交值处理
    categoryRelFormat (data) {
      let catArr = []
      if (data && data.length > 0) {
        data.forEach((item) => {
          catArr.push({
            companyId: this.companyId,
            categoryId: item.categoryId,
            categoryName: item.categoryName,
            categoryCode: item.categoryCode,
            categoryFullName: item.categoryFullName
          })
        })
      }
      return catArr
    },
    // 数据处理
    dataHandle (type) {
      let submitData = {}
      let companyInfo = this.baseInfoModel.baseInfoForm
      let companyInfoDimFields = this.baseDimModel
      companyInfo.dimFieldContexts = companyInfoDimFields // 拓展字段赋值
      companyInfo.categoryRels = this.categoryRelFormat(
        this.baseInfoModel.baseInfoForm.categoryRels
      ) // 可供品类
      let otherInfo = this.otherModel.otherForm
      otherInfo.dimFieldContexts = this.otherDimModel
      let bankInfos = this.formatDimFields(this.bankDimFieldContexts, this.bankData)
      let contactInfo = this.formatDimFields(this.contactDimFieldContexts, this.contactData) // 处理拓展字段
      const __s = this.financeInfoData
      let financeInfos = this.formatDimFields(this.financeDimFieldContexts, __s)
      let orgInfos = this.formatDimFields(this.orgDimFieldContexts, this.orgInfoData)
      let orgCategorys = this.formatDimFields(this.orgCatDimFieldContexts, this.orgCategoryData)
      let attachFiles = this.attachTableData

      submitData.companyInfo = companyInfo
      submitData.bankInfos = bankInfos
      submitData.contactInfos = contactInfo
      submitData.financeInfos = financeInfos
      submitData.orgInfos = orgInfos
      submitData.orgCategorys = orgCategorys
      submitData.otherInfo = otherInfo
      submitData.attachFiles = attachFiles
      submitData.fileUploads = this.companyInfoFileList
      let url = ''
      if (type === 'submit') {
        // 提交
        url = '/api-sup/info/companyInfo/submitInfo'
      } else {
        // 暂存
        url = '/api-sup/info/companyInfo/saveOrUpdateInfo'
      }
      vendorArchival.saveOrUpdatuGreenChannelInfo(url, submitData).then((res) => {
        if (res) {
          this.$message({
            message: res.message,
            type: 'success'
          })
          if (type === 'stage') {
            // 暂存的时候返回公司Id查询一次旧数据
            this.companyId = res.data
            this.curOpt = 'edit'
            this.fileRefresh = true
            this.userRules = this.userInfoRulesEdit
            this.fatchOldData() // 查询旧数据
          } else {
            this.$emit('tab-remove', this.$attrs.params.tabName)
            this.__setTabTodo('vendorGreenChannelList.getQuerydata')
          }
        }
      })
    },
    // 拓展字段维度信息值转换
    formatDimFields (dimFieldArr, modelData) {
      if (dimFieldArr && dimFieldArr.length > 0) {
        modelData.forEach((item, index) => {
          let dyObj = {}
          dimFieldArr.forEach((elm) => {
            let key = elm.fieldCode
            let val = item[key]
            dyObj[key] = val
            // delete modelData[elm.fieldCode]
          })
          modelData[index].dimFieldContexts = dyObj
        })
      }
      return modelData
    },
    addFinance () {
      // 添加财务信息
      this.financeInfoData.unshift({
        add: true,
        orgId: null,
        taxRate: '',
        taxKey: ''
      })
    },
    addBank () {
      // 添加银行
      this.bankData.unshift({
        add: true,
        orgId: null
      })
    },
    addContact () {
      // 新增联系人
      this.contactData.unshift({
        add: true
      })
    },
    addOrg () {
      // 新增合作组织
      this.orgInfoData.unshift({
        add: true,
        orgId: null
      })
    },
    addOrgCategory () {
      // 新增组织品类
      this.orgCategoryData.unshift({
        add: true,
        orgId: null
      })
    },
    // 点击右边进度调跳转到对应的区域
    indexClickTo (code) {
      let anchorEle = this.$refs[code].$el
      if (anchorEle) {
        anchorEle.scrollIntoView(true)
      }
    },
    // OCr 确认数据回写
    ocrConfirm (data) {
      // 回写数据
      let licenseData = data
      this.baseInfoModel.baseInfoForm.companyName = licenseData.companyName
      this.baseInfoModel.baseInfoForm.legalPerson = licenseData.legalPerson
      this.baseInfoModel.baseInfoForm.lcCode = licenseData.lcCode
      this.baseInfoModel.baseInfoForm.registeredCapital = licenseData.registeredCapital
      this.baseInfoModel.baseInfoForm.registCurrency = licenseData.registCurrency
      this.baseInfoModel.baseInfoForm.companyAddress = licenseData.companyAddress
      this.baseInfoModel.baseInfoForm.businessScope = licenseData.businessScope
      this.baseInfoModel.baseInfoForm.registrationAuthority = licenseData.registrationAuthority
      this.baseInfoModel.baseInfoForm.businessStartDate = licenseData.businessStartDate
      this.baseInfoModel.baseInfoForm.businessEndDate = licenseData.businessEndDate
      this.baseInfoModel.baseInfoForm.companyCreationDate = licenseData.companyCreationDate
      this.ocrVisible = false
    },
    // 关掉OCR弹框
    ocrClose () {
      this.ocrVisible = false
    },
    // 发起变更申请
    changeApply () {
      let tab = {
        component: vendorInfoChangeDetail,
        params: {
          flag: 'add',
          companyId: this.companyId
        },
        title: this.$t('vendorMod.infoChange'),
        name: 'vendorInfoChangeDetail' + this.baseInfoModel.baseInfoForm.companyName
      }
      this.$emit('tab-add', tab)
    }
  }
}
</script>
<style scoped lang="scss">
.the-vendorGreenChannelDetail-detail {
  .sub_header {
    margin: 0 0 10px;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
}
.companyInfoFill {
  padding-right: 24%;
  padding-bottom: 40px;
}
.download-link-wrap .close-icon {
  cursor: pointer;
  display: inline-block;
  vertical-align: middle;
}
:deep(.vue-treeselect--disabled .vue-treeselect__control) {
  border-color: #DCDDDE !important;
  border-radius: 5px;
}
:deep(.vue-treeselect--disabled .vue-treeselect__control-arrow-container) {
  display: none;
}
 :deep(.vue-treeselect--disabled .vue-treeselect__control:before) {
   content: "\e63f";
   position: absolute;
   cursor: not-allowed;
   color: #96999C;
   font-size: 14px;
   font-family: "pix-iconfont";
   right: 10px;
 }
</style>
