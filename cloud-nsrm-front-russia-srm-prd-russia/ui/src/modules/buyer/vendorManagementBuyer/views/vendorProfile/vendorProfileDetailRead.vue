<template>
  <el-container
    class="the-vendorGreenChannelDetail-detail"
    direction="vertical"
  >
    <el-main class="el-main">
      <!-- 基本信息填写 -->
      <div class="companyInfoFill">
        <el-form
          ref="baseInfoForm"
          class="base-form-info form-fill-style"
          :model="allParam.companyInfo"
          :rules="allParam.rules"
          :show-message="false"
          :disabled="curOpt == 'view'"
        >
          <el-collapse v-model="activeDims">
            <!-- 账号信息 -->
            <el-collapse-item
              ref="vendorUserInfo"
              :title="$t('vendorMod.vendorUserInfo')"
              name="1"
            >
              <div class="left_div">
                <el-table
                  :data="[allParam.userInfo]"
                  style="width: 100%"
                  border
                  max-height="251px"
                >
                  <el-table-column
                    align="center"
                    type="index"
                    width="50"
                  />
                  <!-- 账号 -->
                  <el-table-column
                    align="center"
                    prop="username"
                    :label="$t('vendorMod.account')"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 联系人 -->
                  <el-table-column
                    align="center"
                    prop="nickname"
                    :label="$t('bid_mod.linkManName')"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 联系方式 -->
                  <el-table-column
                    align="center"
                    prop="phone"
                    :label="$t('vendorMod.contactMethod')"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 邮箱 -->
                  <el-table-column
                    align="center"
                    prop="email"
                    :label="$t('vendorMod.email')"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 职位 -->
                  <el-table-column
                    align="center"
                    prop="ceeaJobcodeDescr"
                    :label="$t('dataConfMod.position')"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 主账号 -->
                  <el-table-column
                    align="center"
                    prop="username"
                    :label="$t('vendorMod.mainAccount')"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  />
                </el-table>
              </div>
            </el-collapse-item>
            <!--企业性质-->
            <el-collapse-item
              ref="companyType"
              :title="$t('vendorMod.companyType')"
              name="2"
            >
              <srm-row :gutter="32">
                <srm-col :initCol="3">
                  <!-- 境内外关系 -->
                  <el-form-item
                    prop="overseasRelation"
                    :label="$t('vendorMod.overseasRelation')"
                  >
                    <DictSelect
                      v-model="allParam.companyInfo.overseasRelation"
                      code="RELATION"
                      :disabled="true"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col
                  v-if="allParam.companyInfo.overseasRelation === 'INSIDE'"
                  :initCol="3"
                >
                  <!-- 企业性质 -->
                  <el-form-item
                    prop="companyType"
                    :label="$t('vendorMod.companyType')"
                  >
                    <DictSelect
                      v-model="allParam.companyInfo.companyType"
                      code="COMPANY_NATURE"
                      :disabled="true"
                      @change="companyTypeChangeHandle"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="3">
                  <!-- 是否长期供应商 -->
                  <el-form-item :label="$t('vendorMod.ifLongTermSupplier')">
                    <el-radio v-model="allParam.companyInfo.ifLongPeriod" label="Y" class="formCheckbox">
                      {{ $t('common.yes') }}
                    </el-radio>
                    <el-radio v-model="allParam.companyInfo.ifLongPeriod" label="N" class="formCheckbox">
                      {{ $t('common.no') }}
                    </el-radio>
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="3">
                  <!-- 供应商业务类型 -->
                  <el-form-item
                    :label="$t('vendorMod.vendorBusinessType')"
                    prop="ceeaSupBusinessType"
                  >
                    <!-- @change="setceeaSupBusinessType" 未定义-->
                    <el-select
                      v-model="allParam.companyInfo.ceeaSupBusinessType"
                      clearable
                    >
                      <el-option
                        v-for="item in businessTypeList"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="3">
                  <!-- 供应商类型 -->
                  <el-form-item
                    :label="$t('supplierRating.supplierType')"
                    prop="supplierType"
                  >
                    <DictSelect
                      v-model="allParam.companyInfo.supplierType"
                      code="SUPPLIER_TYPE"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="3">
                  <!-- 商业模式 -->
                  <el-form-item :label="$t('vendorMod.bizModel')">
                    <el-select
                      v-model="allParam.companyInfo.ceeaBusinessModel"
                      clearable
                      filterable
                    >
                      <el-option
                        v-for="item in bizModel"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>
            <!--企业三证-->
            <el-collapse-item
              ref="enterpriseThreeCertificates"
              :title="$t('vendorMod.enterpriseThreeCertificates')"
              name="3"
            >
              <div style="display: flex;flex-flow: wrap">
                <!--营业执照上传-->
                <div style="width: 33%;padding-left: 25px;padding-right: 25px;">
                  <srm-common-file
                    :default-file="{
                      fileId: allParam.companyInfo.businessLicenseFileId,
                      fileName: allParam.companyInfo.businessLicense
                    }"
                    drag
                    :limit="1"
                    :readonly="true"
                    :dragger-options="{
                      width: '100%',
                      height: '312px'
                    }"
                    list-type="picture-card"
                    @on-success="handleUploadSuccess"
                  />
                </div>
                <div style="width: 67%">
                  <srm-row :gutter="32">
                    <srm-col :initCol="2">
                      <!-- 企业名称 -->
                      <el-form-item
                        prop="companyName"
                        :label="$t('vendorMod.companyName')"
                      >
                        <el-input v-model="allParam.companyInfo.companyName" />
                      </el-form-item>
                    </srm-col>
                    <srm-col :initCol="2">
                      <!-- 法人代表 -->
                      <el-form-item
                        prop="legalPerson"
                        :label="$t('vendorMod.legalPerson')"
                      >
                        <el-input v-model="allParam.companyInfo.legalPerson" />
                      </el-form-item>
                    </srm-col>
                    <srm-col
                      v-if="allParam.companyInfo.overseasRelation === 'INSIDE'"
                      :initCol="2"
                    >
                      <!-- 统一社会信用代码 -->
                      <el-form-item
                        prop="lcCode"
                        :label="$t('vendorMod.lcCode')"
                      >
                        <el-input v-model="allParam.companyInfo.lcCode" />
                      </el-form-item>
                    </srm-col>
                    <srm-col
                      v-if="curType !== 'GETI'"
                      :initCol="2"
                    >
                      <!-- 注册资本(万元) -->
                      <el-form-item
                        prop="registeredCapital"
                        :label="$t('vendorMod.registeredCapital')"
                      >
                        <el-input
                          v-model="allParam.companyInfo.registeredCapital"
                          v-input-format="{ type: 'float' }"
                          :placeholder="$t('vendorMod.pleaseEnter')"
                          class="input-with-select"
                        >
                          <dict-select
                            slot="append"
                            v-model="allParam.companyInfo.registCurrency"
                            code="currency"
                            :placeholder="$t('vendorMod.currencyCode')"
                            style="width: 110px"
                          />
                        </el-input>
                      </el-form-item>
                    </srm-col>
                    <srm-col :initCol="2">
                      <!-- 成立日期 -->
                      <el-form-item
                        prop="companyCreationDate"
                        :label="$t('vendorMod.creationDate')"
                      >
                        <el-date-picker
                          v-model="allParam.companyInfo.companyCreationDate"
                          type="date"
                          :placeholder="$t('common.pleaseSelectDate')"
                          format="yyyy-MM-dd"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col
                      v-if="curType !== 'GETI'"
                      :initCol="2"
                    >
                      <!-- 营业期限 -->
                      <el-form-item
                        prop="businessStartDate"
                        :label="$t('vendorMod.dateBusiness')"
                      >
                        <el-date-picker
                          v-model="allParam.companyInfo.businessDate"
                          type="daterange"
                          start-placeholder="开始日期"
                          end-placeholder="结束日期"
                          format="yyyy-MM-dd"
                          value-format="timestamp"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :initCol="2">
                      <!-- 营业地址 -->
                      <el-form-item
                        prop="companyCountry"
                        :label="$t('vendorMod.businessAddress')"
                      >
                        <CAddress
                          ref="address"
                          v-model="address"
                          style="width: 100%"
                          @change-value="addressChange"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :initCol="2">
                      <!-- 详细地址 -->
                      <el-form-item
                        prop="companyAddress"
                        :label="$t('components.address.detailAddress')"
                      >
                        <el-input
                          v-model="allParam.companyInfo.companyAddress"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :initCol="2">
                      <!-- 企业简称 -->
                      <el-form-item
                        prop="companyShortName"
                        :label="$t('vendorMod.companyShortName')"
                      >
                        <el-input
                          v-model="allParam.companyInfo.companyShortName"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col
                      :initCol="2"
                    >
                      <!-- 登录机关 -->
                      <el-form-item
                        :label="$t('common.registrationAuthority')"
                        prop="registrationAuthority"
                      >
                        <el-input
                          v-model="allParam.companyInfo.registrationAuthority"
                        />
                      </el-form-item>
                    </srm-col>
                  </srm-row>
                </div>
              </div>
              <srm-row :gutter="32">
                <srm-col :initCol="1">
                  <!-- 营业范围 -->
                  <el-form-item :label="$t('vendorMod.businessScope')">
                    <el-input
                      v-model="allParam.companyInfo.businessScope"
                      type="textarea"
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>
            <!--基础信息-->
            <el-collapse-item
              ref="companyBaseInfo2"
              :title="$t('vendorMod.companyBaseInfo2')"
              name="4"
            >
              <srm-row :gutter="32">
                <srm-col :initCol="3">
                  <!-- 代理品牌 -->
                  <el-form-item :label="$t('vendorMod.agencyBrand')">
                    <el-input
                      v-model="allParam.companyInfo.ceeaAgentBrand"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="3">
                  <!-- 主营品类 -->
                  <el-form-item
                    :label="$t('vendorMod.mainCategory')"
                    class="is-required"
                  >
                    <el-input
                      v-model="allParam.companyInfo.categoryName"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="3">
                  <!-- 是否上市 -->
                  <el-form-item :label="$t('vendorMod.ifListed')">
                    <el-checkbox
                      v-model="allParam.companyInfo.ceeaIfListed"
                      true-label="Y"
                      false-label="N"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="3">
                  <!-- 上市时间 -->
                  <el-form-item :label="$t('vendorMod.listedDate')">
                    <el-date-picker
                      v-model="allParam.companyInfo.ceeaListedTime"
                      type="date"
                      format="yyyy-MM-dd"
                      disabled
                      value-format="yyyy-MM-dd"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="3">
                  <!-- 是否有母公司 -->
                  <el-form-item :label="$t('vendorMod.ifParentCompany')">
                    <el-checkbox
                      v-model="allParam.companyInfo.ceeaHasParentCompany"
                      true-label="Y"
                      false-label="N"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col
                  v-if="allParam.companyInfo.ceeaHasParentCompany == 'Y'"
                  :initCol="3"
                >
                  <!-- 母公司名称 -->
                  <el-form-item :label="$t('vendorMod.parentCompanyName')">
                    <el-input
                      v-model="allParam.companyInfo.ceeaParentCompanyName"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col
                  v-if="allParam.companyInfo.ceeaHasParentCompany == 'Y'"
                  :initCol="3"
                >
                  <!-- 母公司统一信用代码 -->
                  <el-form-item :label="$t('vendorMod.parentCompanyLcCode')">
                    <el-input
                      v-model="allParam.companyInfo.ceeaParentCompanyLcCode"
                    />
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
                    <el-input
                      v-model="allParam.companyInfo.dunsCode"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="1">
                  <!-- 企业简介 -->
                  <el-form-item :label="$t('vendorMod.companyProfile')">
                    <el-input
                      v-model="allParam.companyInfo.ceeaCompanyIntro"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <!-- 拓展字段显示 -->
                <el-form v-if="baseInfoDimFieldContexts.length > 0">
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
                      <el-input
                        v-model="baseDimModel[item.fieldCode]"
                        disabled
                      />
                    </el-form-item>
                  </srm-col>
                </el-form>
              </srm-row>
            </el-collapse-item>
            <!-- 联系人信息 -->
            <el-collapse-item
              ref="contactInfo"
              :title="$t('vendorMod.contactInfo')"
              name="5"
            >
              <div class="left_div">
                <el-table
                  :data="allParam.contactInfos"
                  style="width: 100%"
                  border
                  max-height="251px"
                >
                  <el-table-column
                    align="center"
                    type="index"
                    width="50"
                  />
                  <!-- 姓名 -->
                  <el-table-column
                    align="center"
                    prop="contactName"
                    :label="$t('vendorMod.nickname')"
                    width="100"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 性别 -->
                  <el-table-column
                    align="center"
                    prop="ceeaGender"
                    :label="$t('vendorMod.sex')"
                    width="100"
                    :show-overflow-tooltip="true"
                  >
                    <template slot-scope="scope">
                      <span>{{ $getDictLabel('GENDER', scope.row.ceeaGender) }}</span>
                    </template>
                  </el-table-column>
                  <!-- 部门 -->
                  <el-table-column
                    align="center"
                    prop="ceeaDeptName"
                    :label="$t('vendorMod.department')"
                    min-width="120"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 职位 -->
                  <el-table-column
                    align="center"
                    prop="position"
                    :label="$t('dataConfMod.position')"
                    min-width="120"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 联系方式 -->
                  <el-table-column
                    align="center"
                    prop="ceeaContactMethod"
                    :label="$t('vendorMod.contactMethod')"
                    min-width="120"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 邮箱 -->
                  <el-table-column
                    align="center"
                    prop="email"
                    :label="$t('vendorMod.email')"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 默认联系人 -->
                  <el-table-column
                    align="center"
                    prop="ceeaDefaultContact"
                    :label="$t('dataConfMod.isDefault')"
                    min-width="120"
                    :show-overflow-tooltip="true"
                  >
                    <template slot-scope="scope">
                      <span>{{ $getDictLabel('YES_OR_NO', scope.row.ceeaDefaultContact) }}</span>
                    </template>
                  </el-table-column>
                  <!-- 备注 -->
                  <el-table-column
                    align="center"
                    prop="ceeaComments"
                    :label="$t('dataConfMod.remark')"
                    min-width="120"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 拓展字段 [[-->
                  <template v-if="contactDimFieldContexts.length > 0">
                    <el-table-column
                      v-for="col in contactDimFieldContexts"
                      :key="col.fieldId"
                      :prop="col.fieldCode"
                      :label="col.fieldName"
                      min-width="140px"
                    >
                      <template slot-scope="scope">
                        <span>{{ scope.row[col.fieldCode] }}</span>
                      </template>
                    </el-table-column>
                  </template>

                  <!-- 拓展字段 ]]]-->
                </el-table>
              </div>
            </el-collapse-item>
            <!-- 银行信息 -->
            <el-collapse-item
              ref="bankInfo"
              :title="$t('vendorMod.bankInfo')"
              name="3"
            >
              <div class="left_div">
                <el-table
                  :data="allParam.bankInfos"
                  style="width: 100%"
                  border
                  max-height="251px"
                >
                  <el-table-column
                    align="center"
                    type="index"
                    width="50"
                  />
                  <!-- 银行代码 -->
                  <el-table-column
                    align="center"
                    prop="bankCode"
                    :label="$t('components.bank.bankCode')"
                    min-width="120"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 银行名称 -->
                  <el-table-column
                    align="center"
                    prop="bankName"
                    :label="$t('components.bank.bankName')"
                    min-width="120"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 开户行名称 -->
                  <el-table-column
                    align="center"
                    prop="openingBank"
                    :label="$t('components.bank.branchBankName')"
                    min-width="120"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 账户名称 -->
                  <el-table-column
                    align="center"
                    prop="bankAccountName"
                    :label="$t('components.bank.accountName')"
                    min-width="120"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 银行账号 -->
                  <el-table-column
                    align="center"
                    prop="bankAccount"
                    :label="$t('components.bank.bankAccount')"
                    width="100"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 币种 -->
                  <el-table-column
                    align="center"
                    prop="currencyCode"
                    :label="$t('vendorMod.currencyCode')"
                    width="120"
                    :show-overflow-tooltip="true"
                  >
                    <template slot-scope="scope">
                      <DictSelect
                        v-model="scope.row.currencyCode"
                        code="BID_TENDER_CURRENCY"
                        :disabled="isReadOnly"
                      />
                    </template>
                  </el-table-column>
                  <!-- 主账户 -->
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
                        :disabled="true"
                        true-label="Y"
                        false-label="N"
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
                        disabled
                      />
                    </template>
                  </el-table-column>
                  <template v-if="bankDimFieldContexts.length > 0">
                    <!-- 拓展字段 [[-->
                    <el-table-column
                      v-for="col in bankDimFieldContexts"
                      :key="col.fieldId"
                      :prop="col.fieldCode"
                      :label="col.fieldName"
                      width="110px"
                    >
                      <template slot-scope="scope">
                        <span>{{ scope.row[col.fieldCode] }}</span>
                      </template>
                    </el-table-column>
                    <!-- 拓展字段 ]]]-->
                  </template>
                </el-table>
              </div>
            </el-collapse-item>
            <!-- 财务信息 -->
            <el-collapse-item
              ref="financeInfo"
              :title="$t('vendorMod.financeInfo')"
              name="2"
            >
              <div class="left_div">
                <el-table
                  ref="financeTable"
                  :data="allParam.financeInfos"
                  style="width: 100%"
                  border
                  max-height="250px"
                >
                  <!-- 引入组织 -->
                  <el-table-column
                    align="center"
                    prop="fullPathId"
                    :label="$t('vendorMod.ceeaOrgName2')"
                    min-width="140px"
                  >
                    <template slot-scope="scope">
                      <div v-if="scope.row.orgName != ''">{{ scope.row.orgName }}</div>
                      <el-select
                        v-else
                        v-model="scope.row.orgId"
                        style="width: 100%"
                        :disabled="true"
                      >
                        <el-option
                          v-for="item in allParam.orgCategorys"
                          :key="item.orgId"
                          :label="item.orgName"
                          :value="item.orgId"
                        />
                      </el-select>
                    </template>
                  </el-table-column>
                  <!-- 工厂代码 -->
                  <el-table-column
                    align="center"
                    prop="factoryCode"
                    :label="$t('vendorMod.factoryCode')"
                  >
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.factoryCode" :disabled="isReadOnly || scope.row.enableFlag=='Y'" />
                    </template>
                  </el-table-column>
                  <!-- 结算币种 -->
                  <el-table-column
                    align="center"
                    prop="clearCurrency"
                  >
                    <template slot="header">
                      <i class="toRequired">*</i>{{ $t('vendorMod.clearCurrency') }}
                    </template>
                    <template slot-scope="scope">
                      <DictSelect
                        v-model="scope.row.clearCurrency"
                        code="BID_TENDER_CURRENCY"
                        :disabled="isReadOnly || scope.row.enableFlag=='Y'"
                      />
                    </template>
                  </el-table-column>
                  <!-- 付款方式 -->
                  <el-table-column
                    align="center"
                    prop="paymentMethod"
                  >
                    <template slot="header">
                      <i class="toRequired">*</i>{{ $t('vendorMod.paymentMethod') }}
                    </template>
                    <template slot-scope="scope">
                      <DictSelect
                        v-model="scope.row.paymentMethod"
                        code="PAYMENT_METHOD"
                        :disabled="isReadOnly || scope.row.enableFlag=='Y'"
                      />
                    </template>
                  </el-table-column>
                  <!-- 付款账期 -->
                  <el-table-column
                    align="center"
                    prop="paymentTerms"
                  >
                    <template slot="header">
                      <i class="toRequired">*</i>{{ $t('vendorMod.paymentTerms') }}
                    </template>
                    <template slot-scope="scope">
                      <DictSelect
                        v-model="scope.row.paymentTerms"
                        code="PAYMENT_TERMS"
                        :disabled="isReadOnly || scope.row.enableFlag=='Y'"
                      />
                    </template>
                  </el-table-column>
                  <!-- 拓展字段 [[-->
                  <template v-if="financeDimFieldContexts.length > 0">
                    <el-table-column
                      v-for="col in financeDimFieldContexts"
                      :key="col.fieldId"
                      :prop="col.fieldCode"
                      :label="col.languageCode ? $t(col.languageCode) : col.fieldName"
                      width="110px"
                    >
                      <template slot-scope="scope">
                        <DictSelect
                          v-if="col.fieldTypeCode == 'select'"
                          v-model="scope.row[col.fieldCode]"
                          :code="col.dictCode"
                          :disabled="isReadOnly"
                        />
                        <el-input v-else v-model="scope.row[col.fieldCode]" :disabled="isReadOnly" />
                      </template>
                    </el-table-column>
                  </template>
                </el-table>
              </div>
            </el-collapse-item>
            <!-- 厂房信息 -->
            <el-collapse-item
              ref="factoryInfo"
              :title="$t('vendorMod.factoryInfo')"
              name="3"
            >
              <div class="left_div">
                <el-table
                  :data="allParam.plantInfos"
                  style="width: 100%"
                  border
                  max-height="250px"
                >
                  <el-table-column
                    align="center"
                    type="index"
                    width="50"
                  />
                  <!-- 厂房名称 -->
                  <el-table-column
                    align="center"
                    prop="plantName"
                    :label="$t('vendorMod.factoryInfo')"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  >
                    <template #header>
                      <i class="required">*</i>
                      <span>{{ $t("vendorMod.factoryInfo") }}</span>
                    </template>
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.plantName" />
                    </template>
                  </el-table-column>
                  <!-- 厂房性质 -->
                  <el-table-column
                    align="center"
                    prop="plantNature"
                    :label="$t('vendorMod.factoryType')"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  >
                    <template #header>
                      <i class="required">*</i>
                      <span>{{ $t("vendorMod.factoryType") }}</span>
                    </template>
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.plantNature" />
                    </template>
                  </el-table-column>
                  <!-- 厂房面积 -->
                  <el-table-column
                    align="center"
                    prop="plantArea"
                    :label="$t('vendorMod.factoryArea')"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  >
                    <template #header>
                      <i class="required">*</i>
                      <span>{{ $t("vendorMod.factoryArea") }}</span>
                    </template>
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.plantArea" />
                    </template>
                  </el-table-column>
                  <!-- 国家 -->
                  <el-table-column
                    align="center"
                    prop="plantCountry"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  >
                    <template slot="header">
                      <i class="toRequired">*</i>{{ $t('components.address.country') }}
                    </template>
                    <template slot-scope="scope">
                      <DictSelect
                        v-model="scope.row.plantCountry"
                        code="country"
                        :disabled="isReadOnly"
                        filterable
                        @change="getCountry(scope.row)"
                      />
                    </template>
                  </el-table-column>
                  <!-- 地区 -->
                  <el-table-column
                    align="center"
                    prop="plantProvince"
                    :label="$t('components.address.area')"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  >
                    <template slot-scope="scope">
                      <DictSelect
                        v-model="scope.row.plantProvince"
                        code="PROVINCE"
                        custom-select-type="PROVINCE"
                        :disabled="scope.row.plantCountry !== 'CN' || isReadOnly"
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
                        v-model="scope.row.plantCity"
                        :code="scope.row.plantProvince"
                        custom-select-type="CITY"
                        :disabled="scope.row.plantCountry !== 'CN' || isReadOnly"
                      />
                    </template>
                  </el-table-column>
                  <!-- 厂房地址 -->
                  <el-table-column
                    align="center"
                    prop="plantAddress"
                    :label="$t('vendorMod.factoryAddress')"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  >
                    <template #header>
                      <i class="required">*</i>
                      <span>{{ $t("vendorMod.factoryAddress") }}</span>
                    </template>
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.plantAddress" />
                    </template>
                  </el-table-column>
                  <el-table-column
                    fixed="right"
                    :label="$t('common.operation')"
                    width="60"
                  >
                    <template slot-scope="scope">
                      <el-button
                        type="text"
                        @click="
                          handleDelClickPlant(scope.$index, scope.row)
                        "
                      >
                        {{ $t("common.delete") }}
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </el-collapse-item>
            <!-- 供应商地点信息 -->
            <el-collapse-item ref="vendorSiteInfos" :title="$t('vendorMod.vendorSiteInfos')" name="10">
              <el-table
                ref="bankTable"
                :data="allParam.siteInfos"
                style="width: 100%"
                use-virtual
                :emptyText="$t('common.noData')"
                :row-height="35"
                max-height="390px"
                border
              >
                <el-table-column align="center" type="index" width="50" />
                <!-- 业务实体 -->
                <el-table-column
                  align="center"
                  prop="orgId"
                  width="150"
                  :show-overflow-tooltip="true"
                >
                  <template slot="header">
                    <i class="toRequired">*</i>{{ $t('dataConfMod.orgId') }}
                  </template>
                  <template slot-scope="scope">
                    <OrganizationSelector
                      ref="organizationSelector2"
                      v-model="scope.row.orgId"
                      :disabled="isReadOnly"
                      :parentId="-1"
                      nodeType="OU"
                      :placeholder="$t('common.pleaseSelect')"
                      :scope="scope.row"
                    />
                    <!-- @select="selectHandler" -->
                  </template>
                </el-table-column>
                <!-- 地点名称 -->
                <el-table-column
                  align="center"
                  prop="vendorSiteCode"
                  min-width="150"
                  :show-overflow-tooltip="true"
                >
                  <template slot="header">
                    <i class="toRequired">*</i>{{ $t('vendorMod.siteName') }}
                  </template>
                  <template slot-scope="scope">
                    <DictSelect
                      v-model="scope.row.vendorSiteCode"
                      code="VENDOR_SITE_CODE"
                      :disabled="isReadOnly"
                    />
                  </template>
                </el-table-column>
                <!-- 国家 -->
                <el-table-column
                  align="center"
                  prop="country"
                  min-width="150"
                  :show-overflow-tooltip="true"
                >
                  <template slot="header">
                    <i class="toRequired">*</i>{{ $t('components.address.country') }}
                  </template>
                  <template slot-scope="scope">
                    <DictSelect
                      v-model="scope.row.country"
                      code="country"
                      :disabled="isReadOnly"
                      filterable
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
                      :disabled="scope.row.country !== 'CN' || isReadOnly"
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
                      :disabled="scope.row.country !== 'CN' || isReadOnly"
                    />
                  </template>
                </el-table-column>
                <!-- 详细地址 -->
                <el-table-column
                  align="center"
                  prop="addressDetail"
                  min-width="150"
                  :show-overflow-tooltip="true"
                >
                  <template slot="header">
                    <i class="toRequired">*</i>{{ $t('components.address.detailAddress') }}
                  </template>
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.addressDetail" :disabled="isReadOnly" />
                  </template>
                </el-table-column>
                <!-- 邮政编码 -->
                <el-table-column
                  align="center"
                  prop="postCode"
                  :label="$t('components.address.postalCode')"
                  width="120"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.postCode"
                      v-input-format="{ type: 'number' }"
                      :disabled="isReadOnly"
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
                    <el-input v-model="scope.row.siteComment" :disabled="isReadOnly" />
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
                      :disabled="isReadOnly"
                    />
                  </template>
                </el-table-column>
                <el-table-column fixed="right" :label="$t('common.operation')" width="60">
                  <template slot-scope="scope">
                    <el-button
                      v-if="scope.row.enabledDeleteFlag == 'Y'"
                      :disabled="isReadOnly"
                      type="text"
                      @click="handleDelClickSite(scope.$index, scope.row)"
                    >
                      {{ $t('common.delete') }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <!--模板配置配出来-->
            <template v-for="(item,index) in modelConfig.formDimVOList">
              <el-collapse-item
                v-if="item.originalDimFlag === 'N'"
                :ref="item.dimId"
                :key="item.dimCode"
                :title="item.dimName"
                :name="item.dimCode"
                :class="showTileFlag(item.dimTitleShowFlag)"
              >
                <!-- 如果是表单的话显示 -->
                <model-config-form
                  v-if="item.dimType === 'form' && item.originalDimFlag === 'N'"
                  :ref="item.dimCode"
                  :dimConfig="modelConfig.dimConfigMap[item.dimCode]"
                  :formValue="dimDataValue"
                />
                <!-- 如果是表格的话显示 -->
                <model-config-table
                  v-if="item.dimType === 'table'"
                  :ref="item.dimCode"
                  :dimConfig="modelConfig.dimConfigMap[item.dimCode]"
                  :index="index"
                  :tableValue="dimDataValue"
                />
              </el-collapse-item>
            </template>
            <!-- 管理体系信息 -->
            <el-collapse-item
              ref="manageSystem"
              :title="$t('vendorMod.managementSystemInfo')"
              name="6"
            >
              <el-form
                ref="relModel"
                :model="allParam.managementInfo"
                :disabled="true"
                class="rel-form-select"
              >
                <srm-row :gutter="32">
                  <srm-col :initCol="3">
                    <!-- 是否通过ISO9001质量体系认证(如是请上传附件) -->
                    <el-form-item>
                      <template #label>
                        {{ $t('vendorMod.msgIfPass1') }}
                        <el-tooltip class="item" effect="dark" :content="$t('vendorMod.msgIfPass0')">
                          <em class="el-icon-warning-outline" />
                        </el-tooltip>
                      </template>
                      <el-radio-group
                        v-model="allParam.managementInfo.ifIsoQuality"
                      >
                        <el-radio label="Y">
                          {{
                            $t("common.yes")
                          }}
                        </el-radio>
                        <el-radio label="N">
                          {{
                            $t("common.no")
                          }}
                        </el-radio>
                      </el-radio-group>
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="3">
                    <!-- 是否通过ISO14001环境体系认证(如是请上传附件) -->
                    <el-form-item>
                      <template #label>
                        {{ $t('vendorMod.msgIfPass2') }}
                        <el-tooltip class="item" effect="dark" :content="$t('vendorMod.msgIfPass0')">
                          <em class="el-icon-warning-outline" />
                        </el-tooltip>
                      </template>
                      <el-radio-group
                        v-model="allParam.managementInfo.ifIsoEnviron"
                      >
                        <el-radio label="Y">
                          {{
                            $t("common.yes")
                          }}
                        </el-radio>
                        <el-radio label="N">
                          {{
                            $t("common.no")
                          }}
                        </el-radio>
                      </el-radio-group>
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="3">
                    <!-- 是否通过OHSAS18000职业、健康安全体系认证(如是请上传附件) -->
                    <el-form-item>
                      <template #label>
                        {{ $t('vendorMod.msgIfPass3') }}
                        <el-tooltip class="item" effect="dark" :content="$t('vendorMod.msgIfPass0')">
                          <em class="el-icon-warning-outline" />
                        </el-tooltip>
                      </template>
                      <el-radio-group
                        v-model="allParam.managementInfo.ifOhsasSafe"
                      >
                        <el-radio label="Y">
                          {{
                            $t("common.yes")
                          }}
                        </el-radio>
                        <el-radio label="N">
                          {{
                            $t("common.no")
                          }}
                        </el-radio>
                      </el-radio-group>
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="1">
                    <!-- 其他认证情况(如是请上传附件) -->
                    <el-form-item>
                      <template #label>
                        {{ $t('vendorMod.msgIfPass4') }}
                        <el-tooltip class="item" effect="dark" :content="$t('vendorMod.msgIfPass0')">
                          <em class="el-icon-warning-outline" />
                        </el-tooltip>
                      </template>
                      <el-input
                        v-model="allParam.managementInfo.otherAuthSit"
                      />
                    </el-form-item>
                  </srm-col>
                </srm-row>
              </el-form>
              <div class="left_div">
                <el-table
                  :data="allParam.managementAttaches"
                  style="width: 100%"
                  border
                  max-height="250px"
                >
                  <el-table-column
                    align="center"
                    type="index"
                    width="50"
                  />
                  <!-- 证件要求 -->
                  <el-table-column
                    align="center"
                    prop="documentInspection"
                    :label="$t('vendorMod.certificateRequirements')"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 认证类型 -->
                  <el-table-column
                    align="center"
                    prop="authFile"
                    :label="$t('vendorMod.authType')"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  >
                    <template slot-scope="scope">
                      <srm-common-file
                        :extra-data="fileInfo"
                        :default-file="{
                          fileId: scope.row.fileuploadId,
                          fileName: scope.row.authType
                        }"
                        :validate-options="{
                          accept: acceptFileType
                        }"
                        :readonly="false"
                        @on-change="({file}) => innerHandleUploadSuccess(file, scope.row)"
                      />
                    </template>
                  </el-table-column>
                  <!-- 认证描述 -->
                  <el-table-column
                    align="center"
                    prop="authDescription"
                    :label="$t('vendorMod.authDesc')"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  >
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.authDescription" />
                    </template>
                  </el-table-column>
                  <!-- 认证编号 -->
                  <el-table-column
                    align="center"
                    prop="authNum"
                    :label="$t('vendorMod.authNum')"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  >
                    <template slot-scope="scope">
                      <el-input
                        v-model="scope.row.authNum"
                        @change="setFormatValue(scope.row)"
                      />
                    </template>
                  </el-table-column>
                  <!-- 认证时间 -->
                  <el-table-column
                    align="center"
                    prop="authDate"
                    :label="$t('vendorMod.authDate')"
                    width="160"
                    :show-overflow-tooltip="true"
                  >
                    <template slot-scope="scope">
                      <el-date-picker
                        v-model="scope.row.authDate"
                        type="date"
                        format="yyyy-MM-dd"
                        value-format="yyyy-MM-dd"
                      />
                    </template>
                  </el-table-column>
                  <!-- 认证机构 -->
                  <el-table-column
                    align="center"
                    prop="authOrg"
                    :label="$t('vendorMod.authOrg')"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  >
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.authOrg" />
                    </template>
                  </el-table-column>
                  <!-- 证件有效期至 -->
                  <el-table-column
                    align="center"
                    prop="endDate"
                    :label="$t('vendorMod.certUntil')"
                    width="170"
                  >
                    <template slot-scope="scope">
                      <el-date-picker
                        v-model="scope.row.endDate"
                        type="date"
                        format="yyyy-MM-dd"
                        value-format="yyyy-MM-dd"
                      />
                    </template>
                  </el-table-column>
                  <template v-if="certifiDimFieldContexts.length > 0">
                    <!-- 拓展字段 [[-->
                    <el-table-column
                      v-for="col in certifiDimFieldContexts"
                      :key="col.fieldId"
                      :prop="col.fieldCode"
                      :label="col.languageCode ? $t(col.languageCode) : col.fieldName"
                      width="110px"
                    >
                      <template slot-scope="scope">
                        <el-input v-model="scope.row[col.fieldCode]" />
                      </template>
                    </el-table-column>
                    <!-- 拓展字段 ]]]-->
                  </template>
                  <el-table-column
                    fixed="right"
                    :label="$t('common.operation')"
                    width="60"
                  >
                    <template slot-scope="scope">
                      <el-button
                        type="text"
                        @click="
                          handleDelClickAuth(scope.$index, scope.row)
                        "
                      >
                        {{ $t("common.delete") }}
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </el-collapse-item>
            <!-- 其他附件信息 -->
            <el-collapse-item
              ref="otherFile"
              :title="$t('vendorMod.otherAttachInfo')"
              name="6"
            >
              <FileDynamic
                ref="sceneAttachment"
                v-model="companyInfoFileList"
                scene-module-code="SCENE_SUPPLIER_ATTACHMENT"
                :business-id="companyId"
                disabled
                :editable="curOpt === 'add' || curOpt === 'edit'"
              />
            </el-collapse-item>
            <!-- 调查表清单 -->
            <el-collapse-item
              v-if="allParam.questSupplierList.length>0"
              ref="questSupplierModule"
              :title="$t('quest.questSupplierModule')"
              name="10"
            >
              <div class="left_div">
                <el-table
                  :data="allParam.questSupplierList"
                  style="width: 100%"
                  border
                  max-height="251px"
                >
                  <el-table-column
                    align="center"
                    type="index"
                    width="50"
                  />
                  <!-- 调查表编号 -->
                  <el-table-column
                    align="center"
                    prop="questNo"
                    :label="$t('quest.questNo')"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  >
                    <template slot-scope="scope">
                      <el-button type="text" :disabled="false" @click="questionnaireClick(scope)">
                        {{ scope.row.questNo }}
                      </el-button>
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
                  />
                </el-table>
              </div>
            </el-collapse-item>
            <!-- 供应商信息变更记录 -->
            <el-collapse-item
              v-if="allParam.infoChangeList.length>0"
              ref="changeRecord"
              :title="$t('quest.changeRecord')"
              name="10"
            >
              <div class="left_div">
                <el-table
                  :data="allParam.infoChangeList"
                  style="width: 100%"
                  border
                  max-height="251px"
                >
                  <el-table-column
                    align="center"
                    type="index"
                    width="50"
                  />
                  <!-- 变更状态 -->
                  <el-table-column
                    align="center"
                    prop="changeStatus"
                    :label="$t('quest.changeStatus')"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  >
                    <template slot-scope="scope">
                      {{ $getDictLabel('INFO_CHANGE_STATUS', scope.row.changeStatus) }}
                    </template>
                  </el-table-column>
                  <!-- 变更单号 -->
                  <el-table-column
                    align="center"
                    prop="changeApplyNo"
                    :label="$t('quest.changeApplyNo')"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  >
                    <template slot-scope="scope">
                      <el-button type="text" :disabled="false" @click="infoChangeF(scope.row)">
                        {{ scope.row.changeApplyNo }}
                      </el-button>
                    </template>
                  </el-table-column>
                  <!-- 审批日期 -->
                  <el-table-column
                    align="center"
                    prop="changeApplyDate"
                    :label="$t('quest.changeApplyDate')"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 创建人 -->
                  <el-table-column
                    align="center"
                    prop="createdUserName"
                    :label="$t('quest.createdFullName')"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 创建时间 -->
                  <el-table-column
                    align="center"
                    prop="creationDate"
                    :label="$t('quest.creationDate')"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  />
                </el-table>
              </div>
            </el-collapse-item>
            <!-- 注册操作历史 -->
            <el-collapse-item
              ref="operationHistory"
              :title="$t('common.operationRecord')"
              name="16"
            >
              <el-table
                border
                :data="allParam.operatingLogList"
                style="margin-top:5px"
                max-height="300px"
              >
                <el-table-column
                  prop="creationDate"
                  :label="$t('dataConfMod.operationTime')"
                />
                <el-table-column
                  prop="operation"
                  :label="$t('contractMod.operationType')"
                >
                  <template slot-scope="scope">
                    <dict-select
                      v-model="scope.row.operation"
                      code="OPERATING_TYPE"
                      :disabled="true"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  prop="reason"
                  :label="$t('common.explanationOfReasons')"
                />
                <el-table-column
                  prop="createdFullName"
                  :label="$t('flowMod.operator')"
                />
              </el-table>
            </el-collapse-item>
          </el-collapse>
        </el-form>
        <el-collapse v-model="activeDims">
          <!-- 维度拓展组 -->
          <el-collapse-item
            v-for="(item, index) in dimensionData"
            :ref="item.dimCode"
            :key="index"
            :class="associationDimension(item.relateDimCode)"
            :title="item.dimName"
            name="1"
          >
            <!-- 如果是表格的时候显示 -->
            <TableExtend
              v-if="item.dimShowType == 'TABLE' && evalF(item.showDimCondition)"
              :ref="'extendData' + index"
              :table-data="item.dimFieldConfigS"
              :index="index"
              :table-extend-list="tableExtendList"
              :model2="Emodel2"
              :rules="baseDimRules"
              :disabled="true"
            />
            <!-- 如果是表单的话显示 -->
            <FormExtend2
              v-if="item.dimShowType == 'FORM'"
              :ref="'extendData' + index"
              :form-data="item.dimFieldConfigS"
              :model2="Emodel"
              :rules="baseDimRules"
              :disabled="true"
              @selectChange="selectChanges($event)"
            />
            <!-- TABLE -->
          </el-collapse-item>
        </el-collapse>
      </div>
      <!-- 填写进度调区域 -->
      <!-- 企业登记节点 -->
      <CFillProgress
        :node-name="$t('vendorMod.companyRegisterNode')"
        :data="nodeData"
        :percentage="true"
        @index-click="indexClickTo"
      />

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
import OrganizationSelector from 'lib@/components/organization-selector'
import CToolbar from 'lib@/components/c-toolbar'
import CFillProgress from 'lib@/components/c-fill-progress'
import Configurationization from 'lib@/components/configurationization'
import CAddress from 'lib@/components/c-address'
import COcr from 'lib@/components/c-ocr'
import FileDynamic from '@/library/components/c-file-management/file-dynamic'
import ModelConfigForm from 'mod@/common/userManage/views/ModelConfig/ModelConfigForm'
import ModelConfigTable from 'mod@/common/userManage/views/ModelConfig/ModelConfigTable'
import { vendorOptCommonApi } from 'mod@/common/userManage/api'
import {
  getDictItem,
  getDictItemList,
  getRegion
} from '@/api/common'
import { adaptDictData } from '@/utils'
import _omit from 'lodash/omit'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import FormExtend2 from 'mod@/common/userManage/views/companyInfoMaintain/formExtend2'
import TableExtend from 'mod@/common/userManage/views/companyInfoMaintain/tableExtend'
import vendorInfoChangeDetail from 'modb@/vendorManagementBuyer/views/vendorInfoChange/vendorInfoChangeDetail'
import questManagementDetail from 'modb@/vendorManagementBuyer/views/questManagement/questManagementDetail'
import {modelConfigApi} from "@/api/modelConfig";

export default {
  name: 'VendorProfileDetailRead',
  components: {
    CFillProgress,
    CToolbar,
    COcr,
    OrganizationSelector,
    TableExtend,
    FormExtend2,
    FileDynamic,
    Configurationization,
    CAddress,
    ModelConfigForm,
    ModelConfigTable
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      dimDataValue: [],
      modelConfig: {
        dimConfigMap: {},
        formDimVOList: []
      },
      getModelConfig: {},
      dimDataValues: [],
      testBol8: true,
      testBol9: true,
      testBol10: true,
      testBol11: true,
      testBol12: true,
      testBol13: true,
      testBol14: true,
      testBol15: true,
      isReadOnly: true,
      address: [],
      selectChangeData: {},
      Emodel: {},
      Emodel2: {},
      dimensionData: [],
      currentPageB: 1,
      pageSize_approvalBiddingItemLisB: 10,
      lock: true,
      companyId: '',
      dataStatus: '',
      curRel: '',
      curType: '',
      isEnableOcr: 'N', // 是否启用OCR营业执照识别
      fileUploadId: null, // ocr 文件入参ID
      ocrVisible: false, // ocr 弹窗
      // 营业执照接收的文件类型
      acceptFileType: ['jpg', 'png', 'jpeg'],
      activeDims: [
        '1',
        '2',
        '3',
        '4',
        '5',
        '6',
        '7',
        '8',
        '9',
        '10',
        '11',
        '12',
        '13',
        '14',
        '15',
        '16'
      ],
      nodeData: [
        // 进度条节点信息
        {
          code: 'vendorUserInfo',
          name: this.$t('vendorMod.vendorUserInfo'),
          percentage: 0
        }, // '账号信息'
        { code: 'companyType', name: this.$t('vendorMod.companyType'), percentage: 0 }, // '企业性质'
        {
          code: 'enterpriseThreeCertificates',
          name: this.$t('vendorMod.enterpriseThreeCertificates'),
          percentage: 0
        }, // '企业三证'
        { code: 'companyBaseInfo2', name: this.$t('vendorMod.companyBaseInfo2'), percentage: 0 }, // '基础信息'
        {
          code: 'contactInfo',
          name: this.$t('vendorMod.contactInfo'),
          percentage: 0
        }, // '联系人信息'
        {
          code: 'bankInfo',
          name: this.$t('vendorMod.bankInfo'),
          percentage: 0
        }, // '银行信息'
        {
          code: 'financeInfo',
          name: this.$t('vendorMod.financeInfo'),
          percentage: 0
        }, // '财务信息'
        {
          code: 'factoryInfo',
          name: this.$t('vendorMod.factoryInfo'),
          percentage: 0
        }, // 厂房信息
        {
          code: 'vendorSiteInfos',
          name: this.$t('vendorMod.vendorSiteInfos'),
          percentage: 0
        }, // 供应商地点信息
        {
          code: 'manageSystem',
          name: this.$t('vendorMod.managementSystemInfo'), // 管理体系信息
          percentage: 0
        },
        {
          code: 'otherFile',
          name: this.$t('vendorMod.otherAttachInfo'), // 管理体系信息
          percentage: 0
        },
        {
          code: 'operationHistory',
          name: this.$t('vendorMod.actLog'), // 操作记录
          percentage: 0
        }
      ],
      vendorClassificationList: [],
      approvalStatusList: [],
      questSupplierApproveStatusList: [],
      relations: [], // 境内外管理
      natureList: [], // 企业性质
      currencyList: [], // 币种列表
      industryTypeList: [],
      businessTypeList: [],
      factoryType: [], // 厂房性质
      bizModel: [], // 商业规模
      employeeQyt: [], // 员工规模
      companyStatus: [], // 营业状态
      relModel: {
        // 境内外关系
        relform: {
          overseasRelation: '',
          companyType: ''
        },
        rules: {
          overseasRelation: [
            {
              required: true,
              message: this.$t('vendorMod.msgOverseasRelation')
            }
          ], // '请选择境内外关系'
          companyType: [
            { required: true, message: this.$t('vendorMod.msgCompanyType') }
          ] // '请选择企业性质'
        }
      },
      outerField: [
        'overseasRelation',
        'companyType',
        'companyName',
        'businessLicenseFileId',
        'registeredCapital',
        'companyCreationDate',
        'dunsCode',
        'legalPerson',
        'companyCountry'
      ],
      baseRules: {
        overseasRelation: [
          { required: true, message: this.$t('vendorMod.msgOverseasRelation') }
        ], // '请输入境内外关系'
        companyName: [
          { required: true, message: this.$t('vendorMod.msgCompanyName') }
        ], // '请输入公司名称'
        companyCountry: [
          { required: true, message: this.$t('vendorMod.msgBusinessAddr') }
        ], // '请输入国家'
        companyAddress: [
          { required: true, message: this.$t('vendorMod.msgDetailAddr') }
        ] // '请输入地址'
      },
      outRules: {
        registeredCapital: [
          { required: true, message: this.$t('vendorMod.msgRegisteredCapital') }
        ], // '请输入注册资金'
        companyCreationDate: [
          { required: true, message: this.$t('vendorMod.msgCreationDate') }
        ], // '请输入成立日期'
        legalPerson: [
          { required: true, message: this.$t('vendorMod.msgLegalPerson') }
        ], // '请输入法人代表'
        businessScope: [
          { required: true, message: this.$t('vendorMod.msgBusinessScope') }
        ], // '请输入业务范围'
        companyStatus: [
          { required: true, message: this.$t('vendorMod.msgBusinessStatus') }
        ] // '请输入经营状态'
      },
      sosoRules: {
        companyType: [
          { required: true, message: this.$t('vendorMod.msgCompanyType') }
        ], // '请输入公司性质'
        businessLicenseFileId: [
          { required: true, message: this.$t('vendorMod.msgBusinessLicense') }
        ], // '请上传营业执照'
        companyCreationDate: [
          { required: true, message: this.$t('vendorMod.msgCreationDate') }
        ], // '请输入成立日期'
        businessScope: [
          { required: true, message: this.$t('vendorMod.msgBusinessScope') }
        ], // '请输入业务范围'
        companyStatus: [
          { required: true, message: this.$t('vendorMod.msgBusinessStatus') }
        ] // '请输入经营状态'
      },
      innerRules: {
        businessStartDate: [
          { required: true, message: this.$t('vendorMod.msgBusinessStartFrom') }
        ], // '请输入开始营业日期'
        businessEndDate: [
          { required: true, message: this.$t('vendorMod.msgBusinessEndAt') }
        ], // '请输入结束营业日期'
        companyType: [
          { required: true, message: this.$t('vendorMod.msgCompanyType') }
        ], // '请输入公司性质'
        lcCode: [{ required: true, message: this.$t('vendorMod.msgLcCode') }], // '请输入统一社会信用代码'
        legalPerson: [
          { required: true, message: this.$t('vendorMod.msgLegalPerson') }
        ], // '请输入法人代表'
        businessLicenseFileId: [
          { required: true, message: this.$t('vendorMod.msgBusinessLicense') }
        ], // '请上传营业执照'
        registeredCapital: [
          { required: true, message: this.$t('vendorMod.msgRegisteredCapital') }
        ], // '请输入注册资金'
        companyCreationDate: [
          { required: true, message: this.$t('vendorMod.msgCreationDate') }
        ], // '请输入成立日期'
        businessScope: [
          { required: true, message: this.$t('vendorMod.msgBusinessScope') }
        ], // '请输入业务范围'
        companyStatus: [
          { required: true, message: this.$t('vendorMod.msgBusinessStatus') }
        ], // '请输入经营状态'
        companyProvince: [
          { required: true, message: this.$t('vendorMod.msgpProvince') }
        ], // '请输入省'
        companyCity: [{ required: true, message: this.$t('vendorMod.msgCity') }] // '请输入市'
        // registrationAuthority: [{ required: true, message: '请输入登记机关' }],
      },
      unProfitRules: {
        companyType: [
          { required: true, message: this.$t('vendorMod.msgCompanyType') }
        ], // '请输入公司性质'
        companyProvince: [
          { required: true, message: this.$t('vendorMod.msgpProvince') }
        ], // '请输入省'
        companyCity: [{ required: true, message: this.$t('vendorMod.msgCity') }] // '请输入市'
      },
      // 基础信息 拓展字段
      baseInfoDimFieldContexts: [],
      paymantType: [], // 付款类型
      invoiceLimit: [], // 发票限额
      bankAccountType: [], // 账户类型
      baseDimModel: {}, // 基础信息拓展字段
      baseDimRules: {}, // 基础信息拓展字段规则

      contactDimFieldContexts: [], // 联系人拓展字段
      financeDimFieldContexts: [], // 财务信息拓展字段
      bankDimFieldContexts: [], // 银行信息拓展字段
      orgDimFieldContexts: [], // 合作组织拓展字段

      // 银行信息
      bankFormItem: [
        {
          label: this.$t('vendorMod.openingBank'),
          prop: 'openingBank',
          required: true,
          type: 'text'
        }, // '开户行'
        {
          label: this.$t('vendorMod.unionCode'),
          prop: 'unionCode',
          required: true,
          type: 'text'
        }, // '银行联行号'
        {
          label: 'SWIFT CODE',
          prop: 'swiftCode',
          required: false,
          type: 'text'
        },
        {
          label: this.$t('vendorMod.bankAccountName'),
          prop: 'bankAccountName',
          required: true,
          type: 'text'
        }, // '开户户名'
        {
          label: this.$t('vendorMod.bankAccount'),
          prop: 'bankAccount',
          required: true,
          type: 'text'
        } // '开户账号'
      ],
      bankModel: {
        bankForm: {
          openingBank: '',
          unionCode: '',
          swiftCode: '',
          bankAccountName: '',
          bankAccount: '',
          proof: '',
          proofFileId: '',
          dimFieldContexts: {}
        },
        rules: {
          openingBank: [
            { required: true, message: this.$t('vendorMod.msgOpeningBank') }
          ], // '请输入开户行'
          unionCode: [
            { required: true, message: this.$t('vendorMod.msgUnionCode') }
          ], // '请输入银行联行号'
          bankAccountName: [
            { required: true, message: this.$t('vendorMod.msgBankAccountName') }
          ], // '请输入开户户名'
          bankAccount: [
            { required: true, message: this.$t('vendorMod.msgBankAccount') }
          ], // '请输入开户账号'
          accountType: [
            { required: true, message: this.$t('vendorMod.msgAccountType') }
          ], // '请选择账号类型'
          currencyCode: [
            { required: true, message: this.$t('vendorMod.msgCurrencyCode') }
          ] // '请选择币种'
        }
      },
      yesNoOptions: [
        { value: 'Y', label: this.$t('common.yes') },
        { value: 'N', label: this.$t('common.no') }
      ],
      bankDimModel: {},
      // 联系人信息
      linkManFormItem: [
        {
          label: this.$t('vendorMod.contactName'),
          prop: 'contactName',
          required: true,
          type: 'text'
        }, // '联系人'
        {
          label: this.$t('vendorMod.position'),
          prop: 'position',
          required: true,
          type: 'text'
        }, // '公司职务'
        {
          label: this.$t('vendorMod.mobilePhone'),
          prop: 'mobileNumber',
          required: true,
          type: 'text'
        }, // '手机号码'
        {
          label: this.$t('vendorMod.email'),
          prop: 'email',
          required: true,
          type: 'text'
        }, // '邮箱'
        {
          label: this.$t('vendorMod.telPhone'),
          prop: 'phoneNumber',
          required: false,
          type: 'text'
        }, // '座机号码'
        {
          label: this.$t('vendorMod.contactAddr'),
          prop: 'contactAddress',
          required: false,
          type: 'text'
        } // '联系地址'
      ],
      catStatus: [],
      otherFileList: [],
      authenticationList: [],
      ifIsoQuality: 'N',
      ifIsoEnviron: 'N',
      ifOhsasSafe: 'N',
      linkManModel: {
        linkManForm: {
          contactName: '',
          mobileNumber: '',
          position: '',
          phoneNumber: '',
          email: '',
          contactAddress: '',
          dimFieldContexts: {}
        },
        rules: {
          contactName: [
            { required: true, message: this.$t('vendorMod.MsgContactName') }
          ], // '请输入联系人'
          position: [
            { required: true, message: this.$t('vendorMod.msgPosition') }
          ], // '请输入公司职务'
          mobileNumber: [
            { required: true, message: this.$t('vendorMod.msgMobilePhone') }
          ], // '请输入手机号码'
          email: [{ required: true, message: this.$t('vendorMod.msgEmail') }] // '请输入邮箱'
        }
      },
      // 联系人信息 拓展字段
      linkManDimFieldContexts: [],
      linkManDimModel: {},
      // 其他信息
      otherModel: {
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
      // 其他信息信息 拓展字段
      otherDimFieldContexts: [],
      certifiDimFieldContexts: [],
      orgCatDimFieldContexts: [], // 组织和品类拓展字段
      otherDimModel: {},
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'companyInfoMaintain',
        fileType: 'images'
      },
      curOpt: 'add',
      companyInfoFileList: [], // 绑定子组件的数据
      allParam: {
        rules: {},
        companyInfo: {
          companyId: null,
          status: '',
          overseasRelation: 'INSIDE',
          companyType: 'GUOYOU',
          businessLicenseFileId: '',
          businessLicense: '',
          companyName: '',
          registeredCapital: '',
          registCurrency: '',
          registCurrencyName: '',
          companyCreationDate: '',
          companyShortName: '',
          lcCode: '',
          legalPerson: '',
          registrationAuthority: '',
          businessStartDate: '',
          businessEndDate: '',
          companyCountry: '',
          companyProvince: '',
          companyCity: '',
          companyAddress: '',
          dunsCode: '',
          ceeaCompanyWebsite: '',
          ceeaBusinessModel: '',
          ceeaSupBusinessType: '',
          ceeaIndustryType: '',
          ceeaCompanyIntro: '',
          businessScope: '',
          categoryRels: '',
          ceeaPlantType: '',
          ceeaPlantArea: '',
          ceeaAgentBrand: '',
          ceeaIfListed: 'Y',
          ceeaListedTime: '',
          ceeaHasParentCompany: 'Y',
          ceeaParentCompanyName: '',
          ceeaParentCompanyLcCode: ''
        },
        companyInfoDetail: {
          companyDetailId: null,
          companyId: null,
          staffQuantity: '',
          managerQuantity: '',
          technicistQuantity: '',
          productorQuantity: '',
          ifRad: 'Y',
          radStaffQuantity: '',
          businessRank: '',
          marketShare: '',
          internationalTopFive: ''
        },
        orgCategorys: [],
        contactInfos: [],
        bankInfos: [],
        orgInfos: [],
        infoChangeList: [],
        operationInfo: {
          opInfoId: null,
          companyId: null,
          ceeaCompanyCreationDate: '',
          ceeaRegisteredCapital: '',
          ceeaYearTurnover: '',
          ceeaPreThreeYearsSale: '',
          ceeaPreThreeYearsProfit: '',
          ceeaPreThreeYearsAal: '',
          ceeaScopeBusinessRatio: '',
          ceeaIfHasSolarPower: 'Y',
          ceeaUpDownLayout: '',
          ceeaThreeScaleChangeExp: '',
          ceeaReducePurCostAdvise: '',
          ceeaProCostPlanStrategy: '',
          ceeaRdSaleRate: '',
          ceeaProGoodBad: '',
          ceeaProTechRoute: '',
          ceeaTeamShapeAbility: '',
          ceeaProPriceInscapeRate: '',
          ceeaReduceCostFactor: '',
          ceeaHowUpgradePrice: '',
          ceeaAfterSalesAbility: ''
        },
        operationQualities: [],
        operationProducts: [],
        operationEquipments: [],
        businessInfos: [],
        questSupplierList: [],
        userInfo: {},
        managementInfo: {
          managementInfoId: null,
          companyId: null,
          ifIsoQuality: 'Y',
          ifIsoEnviron: 'Y',
          ifOhsasSafe: 'Y',
          otherAuthSit: ''
        },
        managementAttaches: [],
        fileUploads: []
      }
    }
  },
  computed: {
    computedBaseRules () {
      let Obj = {}
      if (this.curRel) {
        if (this.curRel === 'OUT' && this.curType === '') {
          // 境外
          Obj = Object.assign({}, this.baseRules, this.outRules)
        } else if (this.curRel === 'INSIDE' && this.curType === 'GETI') {
          Obj = Object.assign({}, this.baseRules, this.sosoRules)
        } else if (this.curRel === 'INSIDE' && this.curType === 'FEIYINGLI') {
          Obj = Object.assign({}, this.baseRules, this.unProfitRules)
        } else {
          Obj = Object.assign({}, this.baseRules, this.innerRules)
        }
      }
      return Obj
    }
  },
  created () {
    this.companyId = this.$attrs.params.companyId
    this.fatchDictData() // 字典信息
    this.fatchOldData() // 查询旧数据
    this.switchBaseRules() // 切换基础信息的必填项
    // 获取动态配置(复制粘贴即可)
    this.getDimDataById(this.companyId)
    this.getConfig()
    modelConfigApi.getModelConfig('companyInfoMaintain').then(result => {
      this.getModelConfig = result
    })
    modelConfigApi.getDimDataById(this.companyId).then(result => {
      this.dimDataValues = result.data
    })
  },
  mounted () {},
  updated () {
    if (
      !this.bankModel.bankForm.bankAccountName &&
      this.allParam.companyInfo.companyName &&
      this.lock
    ) {
      this.bankModel.bankForm.bankAccountName = this.allParam.companyInfo.companyName
      this.lock = false
    }
  },
  methods: {
    showTileFlag (dimTitleShowFlag) {
      if (dimTitleShowFlag === 'N') {
        return 'noShow'
      }
      return ''
    },
    getConfig () {
      modelConfigApi.getModelConfig('companyInfoMaintain').then(result => {
        if (result) {
          this.modelConfig = result.data
          if (this.modelConfig.formDimVOList) {
            this.modelConfig.formDimVOList.forEach(item => {
              console.log(item)
              const obj = {
                code: item.dimId,
                name: item.dimName,
                percentage: 'NAN'
              }
              if (!item.relateDimCode) {
                this.nodeData.splice(-3, 0, obj)
              }
              // 默认展示所有动态维度
              this.activeDims.push(item.dimCode)
            })
          }
        }
      })
    },
    getDimDataById (businessId) {
      if (businessId) {
        modelConfigApi.getDimDataById(businessId).then(result => {
          this.dimDataValue = result.data
        })
      }
    },
    getDimDataFromVue (businessId) {
      let that = this
      let dimDataList = []
      this.modelConfig.formDimVOList.forEach(item => {
        // 非固定的,需要加[0]
        if (that.$refs[item.dimCode] instanceof Array) {
          let formData = that.$refs[item.dimCode] ? that.$refs[item.dimCode][0].getDataValue() : null
          if (formData) {
            dimDataList.push(...formData)
          }
        } else {
          let formData = that.$refs[item.dimCode] ? that.$refs[item.dimCode].getDataValue() : null
          if (formData) {
            dimDataList.push(...formData)
          }
        }
      })
      if (dimDataList && dimDataList.length > 0) {
        dimDataList.forEach(item => {
          item.businessId = businessId
        })
      }
      return dimDataList
    },
    showType (type, test) {
      eval(test)
      if (!this.testBol8) {
        this.nodeData.splice(11, 1)
      }
      if (!this.testBol9) {
        this.nodeData.splice(12, 1)
      }
      if (!this.testBol10) {
        this.nodeData.splice(13, 1)
      }
      if (!this.testBol11) {
        this.nodeData.splice(14, 1)
      }
      if (!this.testBol12) {
        this.nodeData.splice(15, 1)
      }
      if (!this.testBol13) {
        this.nodeData.splice(16, 1)
      }
      if (!this.testBol14) {
        this.nodeData.splice(17, 1)
      }
      if (!this.testBol15) {
        this.nodeData.splice(18, 1)
      }
    },
    addressChange (value) {
      this.address = value
    },
    questionnaireClick (scope) {
      let row = scope.row
      let tab = {
        component: questManagementDetail,
        params: {
          flag: 'view',
          row: row,
          tabName: 'questManagementDetail' + row.questSupId
        },
        title: this.$t('dashboard.enumerationForm') + row.questNo,
        name: 'questManagementDetail' + row.questSupId
      }
      this.$emit('tab-add', tab)
    },
    infoChangeF (row) {
      let changeId = row.changeId
      let companyId = row.companyId
      let tab = {
        component: vendorInfoChangeDetail,
        params: {
          flag: 'view',
          changeId: changeId,
          companyId: companyId,
          tabName: 'vendorInfoChangeDetail' + this.allParam.companyInfo.companyName
        },
        title: this.allParam.companyInfo.companyName,
        name: 'vendorInfoChangeDetail' + this.allParam.companyInfo.companyName
      }
      this.$emit('tab-add', tab)
    },
    // 维度间的级联
    selectChanges (val) {
      let selectChangeData = this.selectChangeData
      for (let key in selectChangeData) {
        for (let key2 in val) {
          if (key == key2) {
            this.$set(this.selectChangeData, key, val[key2])
            console.log('1', this.selectChangeData)
            return
          }
        }
      }
      this.selectChangeData = Object.assign({}, selectChangeData, val)
    },
    evalF (val) {
      try {
        if (val !== '' && val !== null) {
          return eval(val)
        } else {
          return true
        }
      } catch (error) {
        return true
      }
    },
    // 判断是否有关联维度
    associationDimension (val) {
      if (val) {
        return 'dimension'
      }
      return ''
    },
    // 重新排序的方法
    sortDimList () {
      // 根据要关联合并的id排序
      let sortList = []
      let tempList = []
      let relateItemMap = {}
      this.dimensionData.forEach(res => {
        if (res.relateDimCode !== null && res.relateDimCode !== '') {
          relateItemMap[res.relateDimCode] = res
        } else {
          tempList.push(res)
        }
      })
      if (tempList.length !== 0) {
        tempList.forEach(res => {
          sortList.push(res)
          if (relateItemMap.hasOwnProperty(res.dimCode)) {
            sortList.push(relateItemMap[res.dimCode])
          }
        })
        this.dimensionData = sortList
      }
    },
    // 循环输出维度扩展
    dimensionExtension (data, res) {
      let _this = this
      this.dimensionData = []
      this.tableExtendList = []
      data.forEach(function (parme) {
        if (parme.originalDimFlag === 'N') {
          parme.model = {}
          _this.dimensionData.push(parme)
          _this.tableExtendList.push([{}])
        }
      })

      this.sortDimList()// 重新排序

      this.dimensionData.forEach(res => {
        const obj = {
          code: res.dimCode,
          name: res.dimName,
          percentage: 'NAN'
        }
        if (!res.relateDimCode) {
          this.nodeData.push(obj)
        }
      })

      let Emodel = []
      let Emodel2 = [] // 表单
      res.data.dimFieldResultList.forEach(datas => {
        let obj = {}
        let obj2 = {
          dimFieldNum: datas.dimFieldNum
        } // 表单
        obj[datas.fieldCode] = datas.fieldValue
        obj2[datas.fieldCode] = datas.fieldValue
        Emodel.push(obj)
        Emodel2.push(obj2)
      })
      this.Emodel = Emodel
      this.Emodel2 = Emodel2
    },
    formatApprovalStatus (row, column, cellValue, index) {
      if (
        !this.questSupplierApproveStatusList.filter(
          v => v.value === cellValue
        )[0]
      ) {
        return null
      }
      return cellValue
        ? this.questSupplierApproveStatusList.filter(
            v => v.value === cellValue
          )[0].label
        : ''
    },
    // 计算字段
    computedBaseFields () {
      let overseasRelation = this.allParam.companyInfo.overseasRelation // 境内外类型
      let companyType = this.allParam.companyInfo.companyType // 公司属性
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
            'categoryRels'
          ]
        }
      }
      return resArr
    },
    // 计算经营信息字段
    computedManagementFields () {
      let resArr = [
        'ceeaYearTurnover',
        'ceeaPreThreeYearsSale',
        'ceeaPreThreeYearsProfit',
        'ceeaPreThreeYearsAal',
        'ceeaScopeBusinessRatio',
        'ceeaIfHasSolarPower',
        'ceeaUpDownLayout',
        'ceeaThreeScaleChangeExp',
        'ceeaReducePurCostAdvise',
        'ceeaProCostPlanStrategy',
        'ceeaRdSaleRate',
        'ceeaProGoodBad',
        'ceeaProTechRoute',
        'ceeaTeamShapeAbility',
        'ceeaProPriceInscapeRate',
        'ceeaReduceCostFactor',
        'ceeaHowUpgradePrice',
        'ceeaAfterSalesAbility',
        'mainTestEquipment',
        'mainTestProject',
        'equipmentType',
        'equipmentName',
        'specification',
        'quantity',
        'equipmentCapacity',
        'manufacturer',
        'serviceYear',
        'proBase',
        'proName',
        'proBrand',
        'mainTechnics',
        'yearOutput',
        'supplyCapacityRate',
        'proQualifiedRate',
        'yearTurnover',
        'comment'
      ]
      return resArr
    },
    // 切换必填规则
    switchBaseRules () {
      if (this.curRel) {
        let rules = {}
        if (this.curRel === 'OUT') {
          // 境外
          rules = Object.assign(this.baseRules, this.outRules)
        } else if (this.curRel === 'INSIDE') {
          if (this.curType === 'GETI') {
            // 个体户
            rules = Object.assign(this.baseRules, this.sosoRules)
          } else if (this.curType === 'FEIYINGLI') {
            // 非盈利
            rules = Object.assign(this.baseRules, this.unProfitRules)
          } else {
            // 其他内部
            rules = Object.assign(this.baseRules, this.innerRules)
          }
        } else {
          rules = this.baseRules
        }
        this.allParam.rules = rules
      } else {
        this.allParam.rules = this.baseRules
      }
    },
    // 获取数据字典
    fatchDictData () {
      // 批量查询字典
      let dictParamsArr = [
        { dictCode: 'RELATION' }, // 境内外关系
        { dictCode: 'COMPANY_NATURE' }, // 企业性质
        { dictCode: 'COMPANY_STATUS' }, // 经营状态
        { dictCode: 'BIZ_MODEL' }, // 商业模式
        { dictCode: 'FACTORY_TYPE' }, // 厂房性质
        { dictCode: 'EMPLOYEE_QTY' }, // 员工规模
        { dictCode: 'BANK_ACCOUNT_TYPE' }, // 账户类型
        { dictCode: 'INDUSTRY_TYPE' }, // 行业类型
        { dictCode: 'SUP_BUSINESS_TYPE' }, // 供应商业务类型
        { dictCode: 'CATEGORY_STATUS' },
        { dictCode: 'SUPPLIER_CLASSIFICATION' },
        { dictCode: 'APPROVE_STATUS_TYPE' },
        { dictCode: 'QUEST_SUPPLIER_APPROVE_STATUS' },
        { dictCode: 'PAYMENT_METHOD' },
        { dictCode: 'PAYMENT_TERMS' },
        { dictCode: 'INVOICE_LIMIT' }
      ]
      getDictItemList(dictParamsArr).then(res => {
        const [
          RELATION,
          COMPANY_NATURE,
          COMPANY_STATUS,
          BIZ_MODEL,
          FACTORY_TYPE,
          EMPLOYEE_QTY,
          BANK_ACCOUNT_TYPE,
          INDUSTRY_TYPE,
          SUP_BUSINESS_TYPE,
          CATEGORY_STATUS,
          SUPPLIER_CLASSIFICATION,
          APPROVE_STATUS_TYPE,
          QUEST_SUPPLIER_APPROVE_STATUS,
          PAYMENT_METHOD,
          PAYMENT_TERMS,
          INVOICE_LIMIT
        ] = res.data
        this.relations = adaptDictData(RELATION.RELATION, 'dict')
        this.natureList = adaptDictData(COMPANY_NATURE.COMPANY_NATURE, 'dict')
        this.companyStatus = adaptDictData(
          COMPANY_STATUS.COMPANY_STATUS,
          'dict'
        )
        this.bizModel = adaptDictData(BIZ_MODEL.BIZ_MODEL, 'dict')
        this.factoryType = adaptDictData(FACTORY_TYPE.FACTORY_TYPE, 'dict')
        this.employeeQyt = adaptDictData(EMPLOYEE_QTY.EMPLOYEE_QTY, 'dict')
        this.bankAccountType = adaptDictData(
          BANK_ACCOUNT_TYPE.BANK_ACCOUNT_TYPE,
          'dict'
        )
        this.paymantTerms = adaptDictData(PAYMENT_TERMS.PAYMENT_TERMS, 'dict')
        this.paymantType = adaptDictData(PAYMENT_METHOD.PAYMENT_METHOD, 'dict')
        this.invoiceLimit = adaptDictData(INVOICE_LIMIT.INVOICE_LIMIT, 'dict')
        this.industryTypeList = adaptDictData(
          INDUSTRY_TYPE.INDUSTRY_TYPE,
          'dict'
        )
        this.businessTypeList = adaptDictData(
          SUP_BUSINESS_TYPE.SUP_BUSINESS_TYPE,
          'dict'
        )
        this.catStatus = adaptDictData(CATEGORY_STATUS.CATEGORY_STATUS, 'dict')
        this.vendorClassificationList = adaptDictData(
          SUPPLIER_CLASSIFICATION.SUPPLIER_CLASSIFICATION,
          'dict'
        )
        this.approvalStatusList = adaptDictData(
          APPROVE_STATUS_TYPE.APPROVE_STATUS_TYPE,
          'dict'
        )
        this.questSupplierApproveStatusList = adaptDictData(
          QUEST_SUPPLIER_APPROVE_STATUS.QUEST_SUPPLIER_APPROVE_STATUS,
          'dict'
        )
      })

      // 是否启用OCR
      getDictItem('ENABLE_OCR').then(res => {
        this.isEnableOcr = res.data[0].dictItemCode
      })
      // 获取所有币种
      getDictItem('BID_TENDER_CURRENCY').then(res => {
        this.currencyList = adaptDictData(res.data, 'dict')
      })
    },
    companyTypeChangeHandle (val) {
      this.allParam.companyInfo.companyType = val
      this.curType = val // 当前公司属性
      this.switchBaseRules()
    },
    addOneBank () {
      this.allParam.bankInfos.push({
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
    },
    // 行删除
    handleDelClickBank (index, row) {
      this.allParam.bankInfos.splice(index, 1)
    },
    addOne () {
      this.allParam.contactInfos.push({
        contactInfoId: null,
        companyId: null,
        ceeaGender: 'M',
        ceeaDeptId: null,
        ceeaDeptName: '',
        ceeaContactMethod: '',
        ceeaComments: '',
        contactName: '',
        ceeaDefaultContact: '',
        mobileNumber: '',
        phoneNumber: '',
        email: '',
        contactAddress: '',
        position: '',
        taxNumber: ''
      })
    },
    // 行删除
    handleDelClick (index, row) {
      this.allParam.contactInfos.splice(index, 1)
    },
    addOneFile () {
      this.otherFileList.push({
        inquiryId: ''
      })
    },
    addOneAuth () {
      this.allParam.managementAttaches.push({
        managementAttachId: null,
        managementInfoId: null,
        companyId: null,
        fileuploadId: null,
        authType: '',
        authDescription: '',
        authNum: '',
        authDate: '',
        authOrg: '',
        endDate: ''
      })
    },
    // 行删除
    handleDelClickAuth (index, row) {
      this.allParam.managementAttaches.splice(index, 1)
    },
    addOneCustomer () {
      this.allParam.businessInfos.push({
        businessInfoId: null,
        companyId: null,
        customer: '',
        ceeaWorkFrequency: '',
        ceeaArea: '',
        ceeaComment: '',
        preSalesVol: '',
        preSalesAmount: ''
      })
    },
    // 行删除
    handleDelClickCustomer (index, row) {
      this.allParam.businessInfos.splice(index, 1)
    },
    addOneEquipment () {
      this.allParam.operationEquipments.push({
        opEquipmentId: null,
        companyId: null,
        opInfoId: null,
        equipmentType: '',
        equipmentName: '',
        specification: '',
        quantity: '',
        equipmentCapacity: '',
        manufacturer: '',
        serviceYear: ''
      })
    },
    addOneProInfo () {
      this.allParam.operationProducts.push({
        opProductId: null,
        companyId: null,
        opInfoId: null,
        proBase: '',
        proName: '',
        proBrand: '',
        mainTechnics: '',
        yearOutput: '',
        supplyCapacityRate: '',
        proQualifiedRate: '',
        yearTurnover: '',
        comment: ''
      })
    },
    addOneQuality () {
      this.allParam.operationQualities.push({
        opQualityId: null,
        companyId: null,
        opInfoId: null,
        mainTestEquipment: '',
        mainTestProject: ''
      })
    },
    innerButtonClick (index) {
      this.bankRowIndex = index
    },
    innerHandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '', fileType = '' } = file || {}
      row.fileuploadId = fileId.toString()
      row.authType = fileName
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
      let certify = []
      if (data && data.length > 0) {
        data.forEach(element => {
          if (element.dimCode === 'companyInfo') {
            companyInfo = element.dimFieldConfigS
          } else if (element.dimCode === 'bankInfo') {
            bankInfo = element.dimFieldConfigS
          } else if (element.dimCode === 'linkMan') {
            contactInfo = element.dimFieldConfigS
          } else if (element.dimCode === 'otherInfo') {
            otherInfo = element.dimFieldConfigS
          } else if (element.dimCode === 'orgInfo') {
            orgInfo = element.dimFieldConfigS
          } else if (element.dimCode === 'financeInfo') {
            financeInfo = element.dimFieldConfigS
          } else if (element.dimCode === 'orgCategory') {
            orgCategory = element.dimFieldConfigS
          } else if (element.dimCode === 'certificationInfo') {
            certify = element.dimFieldConfigS
          }
        })
      }

      this.baseInfoDimFieldContexts = companyInfo
      this.financeDimFieldContexts = financeInfo
      this.bankDimFieldContexts = bankInfo
      this.contactDimFieldContexts = contactInfo
      this.orgDimFieldContexts = orgInfo
      this.certifiDimFieldContexts = certify
    },
    // form文件上传
    buttonClick (sec, field) {
      this.uploadSec = sec
      this.field = field
    },
    // 营业执照 上传附件成功
    handleUploadSuccess (file) {
      const { id, name } = file
      let fieldName = this.field
      let fieldID = this.field + 'FileId'
      if (this.uploadSec === 'baseInfo') {
        // 营业执照
        this.allParam.companyInfo[fieldID] = id.toString()
        this.allParam.companyInfo[fieldName] = name
        // 判断是否需要OCR识别 境内供应商 && 开启OCR
        if (
          this.allParam.companyInfo.overseasRelation === 'INSIDE' &&
          this.isEnableOcr === 'Y'
        ) {
          this.fileUploadId = id // ocr 文件入参ID
          this.ocrVisible = true
        }
      } else if (this.uploadSec === 'bank') {
        // 银行凭证
        this.bankModel.bankForm[fieldID] = id.toString()
        this.bankModel.bankForm[fieldName] = name
      }
    },
    // 品类状态显示
    filterCatHandler (row, column, cellValue, index) {
      let rowDict = this.catStatus.find(item => {
        return item.value === row.serviceStatus
      })
      if (rowDict) {
        return rowDict.label
      } else {
        return cellValue
      }
    },
    // 附件删除
    handleRemove (fileId) {
      // this.materialModle.tableData[this.rowIndex].ctcAttachmentDto = {}
    },
    handleScriptProgress (percent) {},
    // 删除文件
    handleAttachmentRemove () {
      this.allParam.companyInfo.businessLicenseFileId = ''
      this.allParam.companyInfo.businessLicense = ''
    },
    // 查询旧数据
    fatchOldData () {
      let companyId = this.companyId
      if (companyId) {
        this.curOpt = this.$attrs.params.flag || 'edit'
        vendorOptCommonApi.getCompanyForEdit({ companyId }).then(res => {
          if (res) {
            this.allParam.infoChangeList = res.data.infoChangeList
            this.allParam.plantInfos = res.data.plantInfos
            if (res.data.companyInfo) {
              let companyInfo = res.data.companyInfo
              this.dataStatus = companyInfo.status // 单据状态

              // 信息状态为提交显示到第四步 ///调试完以后再打开
              this.relModel.relform.categoryRels = companyInfo.categoryRels
              this.relModel.relform.overseasRelation =
                companyInfo.overseasRelation
              this.relModel.relform.companyType = companyInfo.companyType
              this.curRel = companyInfo.overseasRelation
              this.curType = companyInfo.companyType

              this.allParam.companyInfo = _omit(companyInfo, [
                'applicationDate',
                'creationDate',
                'lastUpdateDate'
              ])
              const companyCreationDates = companyInfo.companyCreationDate
                ? this.$dayjs(companyInfo.companyCreationDate).valueOf()
                : ''
              this.$set(this.allParam.companyInfo, 'companyCreationDate', companyCreationDates)
              console.log(this.allParam.companyInfo.companyCreationDate, 'companyCreationDate')
              this.allParam.companyInfo.businessStartDate = companyInfo.businessStartDate
                ? this.$dayjs(companyInfo.businessStartDate).valueOf()
                : '' // 开始时间
              this.allParam.companyInfo.businessEndDate = companyInfo.businessEndDate
                ? this.$dayjs(companyInfo.businessEndDate).valueOf()
                : ''

              this.baseDimModel = companyInfo.dimFieldContexts || {} // 拓展字段值
              this.allParam = res.data // yuyue3
              this.allParam.userInfo = res.data.userInfo || {}
              this.allParam.managementInfo = res.data.managementInfo || {}
              this.allParam.companyInfoDetail =
                res.data.companyInfoDetail || {}
              this.allParam.operationInfo = res.data.operationInfo || {}
              this.switchBaseRules() // 切换必填规则
            }
            if (res.data.bankInfos && res.data.bankInfos[0]) {
              let bankInfos = res.data.bankInfos[0]
              this.bankModel.bankForm.bankInfoId = bankInfos.bankInfoId
              this.bankModel.bankForm.openingBank = bankInfos.openingBank
              this.bankModel.bankForm.unionCode = bankInfos.unionCode
              this.bankModel.bankForm.swiftCode = bankInfos.swiftCode
              this.bankModel.bankForm.bankAccountName =
                bankInfos.bankAccountName
              this.bankModel.bankForm.bankAccount = bankInfos.bankAccount
              this.bankModel.bankForm.proof = bankInfos.proof
              this.bankModel.bankForm.proofFileId = bankInfos.proofFileId
              this.bankDimModel = bankInfos.dimFieldContexts
            }
            if (res.data.contactInfos && res.data.contactInfos[0]) {
              let contactInfos = res.data.contactInfos[0]
              this.linkManModel.linkManForm.contactInfoId =
                contactInfos.contactInfoId
              this.linkManModel.linkManForm.contactName =
                contactInfos.contactName
              this.linkManModel.linkManForm.mobileNumber =
                contactInfos.mobileNumber
              this.linkManModel.linkManForm.position = contactInfos.position
              this.linkManModel.linkManForm.phoneNumber =
                contactInfos.phoneNumber
              this.linkManModel.linkManForm.email = contactInfos.email
              this.linkManModel.linkManForm.contactAddress =
                contactInfos.contactAddress
              this.linkManDimModel = contactInfos.dimFieldContexts
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
              this.otherDimModel = otherInfo.dimFieldContexts
            }
            if (res.data.fileUploads) {
              this.companyInfoFileList = res.data.fileUploads
            }

            if (this.allParam.companyInfo.businessStartDate !== '') {
              this.allParam.companyInfo.businessDate = []
              let dateStart = new Date(this.allParam.companyInfo.businessStartDate).getTime()
              let dateEnd = new Date(this.allParam.companyInfo.businessEndDate).getTime()
              this.$set(this.allParam.companyInfo, 'businessDate', [dateStart, dateEnd])
            }
            if (this.allParam.companyInfo.companyCountry !== '') {
              this.address.push(this.allParam.companyInfo.companyCountry)
            }
            if (this.allParam.companyInfo.companyProvince !== '') {
              this.address.push(this.allParam.companyInfo.companyProvince)
            }
            if (this.allParam.companyInfo.companyCity !== '') {
              this.address.push(this.allParam.companyInfo.companyCity)
            }

            if (this.allParam.plantInfos.length > 0) {
              this.allParam.plantInfos.forEach((e, index) => {
                if (e.plantCountry) {
                  this.$set(e, 'address', [e.plantCountry, e.plantProvince, e.plantCity])
                }
                this.$nextTick(() => {
                  this.$refs['address' + index].init()
                })
              })
            }

            this.$refs.address.init()

            this.$nextTick(() => {
              this.$refs.sceneAttachment.loadFileInfo()
            })

            this.allParam.cateJournalList = res.data.cateJournalList
            this.$set(
              this.allParam.companyInfo,
              'categoryName',
              this.allParam.cateJournalList.map(v => v.categoryName).join(',')
            )

            // 查询魔板配置信息
            let pareme = this.relModel.relform
            vendorOptCommonApi.getConfigByTemplate(pareme).then(res2 => {
              if (res2) {
                this.dimensionExtension(res2.data, res)
                this.adaptDimFieldsHandle(res2.data) // 适配拓展字段
              }
            })
            this.allParam.bankInfos = res.data.bankInfos
              ? this.adaptResutData(res.data.bankInfos)
              : []
            this.allParam.contactInfos = res.data.contactInfos
              ? this.adaptResutData(res.data.contactInfos)
              : []
            this.financeInfoData = res.data.financeInfos
              ? this.adaptResutData(res.data.financeInfos)
              : []
            this.allParam.orgInfos = res.data.orgInfos
              ? this.adaptResutData(res.data.orgInfos)
              : []
            this.allParam.orgCategorys = res.data.orgCategorys
              ? this.adaptResutData(res.data.orgCategorys)
              : []
            this.allParam.managementAttaches = res.data.managementAttaches
              ? this.adaptResutData(res.data.managementAttaches)
              : [] // 认证情况
          }
        })
      }
    },
    // 返回数据适配
    adaptResutData (data) {
      if (data && data.length > 0) {
        data.forEach(elm => {
          let dimFieldData = elm.dimFieldContexts || {}
          Object.keys(dimFieldData).forEach(key => {
            elm[key] = dimFieldData[key]
          })
        })
      }
      return data
    },
    backToHandle () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('vendorProfileList.getQuerydata')
    },
    // 暂存
    stagingHandle () {
      this.dataHandle('stage')
    },
    // 提交
    submitHandle () {
      let _this = this
      // 基础信息
      _this.$refs.baseInfoForm.validate(valid => {
        if (valid) {
          if (_this.relModel.relform.categoryRels.length == 0) {
            this.$message({
              message: this.$t('vendorMod.msgCategoryRels'), // '请选择可供品类'
              type: 'error'
            })
            return false
          }
          this.dataHandle('submit') // 提交数据
        } else {
          this.$message({
            message: this.$t('common.pleasefinishRequired'), // '请输入必填项'
            type: 'error'
          })
          return false
        }
      })
    },
    // 拓展字段塞值处理
    dimFieldsValHandel (odlArr, formObj) {
      Object.keys(formObj).forEach(key => {
        odlArr.find(item => {
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
        data.map(item => {
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
    // 拓展字段维度信息值转换
    formatDimFields (dimFieldArr, modelData) {
      if (dimFieldArr && dimFieldArr.length > 0) {
        modelData.forEach((item, index) => {
          let dyObj = {}
          dimFieldArr.forEach(elm => {
            let key = elm.fieldCode
            let val = item[key]
            dyObj[key] = val
          })
          modelData[index].dimFieldContexts = dyObj
        })
      }
      return modelData
    },
    // 数据处理
    dataHandle (type) {
      let submitData = {}
      submitData = this.allParam
      let financeInfos = this.financeInfoData.map(i => {
        const taxRate =
          (this.taxList.find(j => j.value === i.taxKey) || {}).key || ''
        return { taxRate, ...i }
      })
      let contactInfo = this.formatDimFields(
        this.contactDimFieldContexts,
        this.allParam.contactInfos
      ) // 联系人处理拓展字段
      let bankInfos = this.formatDimFields(
        this.bankDimFieldContexts,
        this.allParam.bankInfos
      ) // 银行信息处理字段
      // financeInfos = this.formatDimFields(this.financeDimFieldContexts, financeInfoData) //财务信息处理字段
      let orgInfos = this.formatDimFields(
        this.orgDimFieldContexts,
        this.allParam.orgInfos
      ) // 合作组织息处理字段

      submitData.orgInfos = orgInfos
      submitData.bankInfos = bankInfos
      submitData.financeInfos = financeInfos
      submitData.companyInfo.categoryRels = this.relModel.relform.categoryRels
      submitData.contactInfos = contactInfo
      submitData.fileUploads = this.companyInfoFileList // 附件对象
      if (type === 'submit') {
        vendorOptCommonApi.submitCompany(submitData).then(res => {
          if (res) {
            this.$message({
              message: res.message,
              type: 'success'
            })
          }
        })
      } else {
        vendorOptCommonApi.saveOrUpdateCompany(submitData).then(res => {
          if (res) {
            this.$message({
              message: res.message,
              type: 'success'
            })
            this.companyId = res.data
            this.fatchOldData() // 暂存后查询旧数据
          }
        })
      }
    },
    // 点击右边进度调跳转到对应的区域
    indexClickTo (code) {
      let anchorEle = this.$refs[code].$el
      if (!anchorEle) {
        anchorEle = this.$refs[code][0].$el
      }
      if (anchorEle) {
        anchorEle.scrollIntoView(true)
      }
    },
    // OCr 确认数据回写
    ocrConfirm (data) {
      // 回写数据
      let licenseData = data
      this.allParam.companyInfo.companyName = licenseData.companyName
      this.allParam.companyInfo.legalPerson = licenseData.legalPerson
      this.allParam.companyInfo.lcCode = licenseData.lcCode
      this.allParam.companyInfo.registeredCapital =
        licenseData.registeredCapital
      this.allParam.companyInfo.registCurrency = licenseData.registCurrency
      this.allParam.companyInfo.companyAddress = licenseData.companyAddress
      this.allParam.companyInfo.businessScope = licenseData.businessScope
      this.allParam.companyInfo.registrationAuthority =
        licenseData.registrationAuthority
      this.allParam.companyInfo.businessStartDate =
        licenseData.businessStartDate
      this.allParam.companyInfo.businessEndDate = licenseData.businessEndDate
      this.allParam.companyInfo.companyCreationDate =
        licenseData.companyCreationDate
      this.ocrVisible = false
    },
    formatData (row, column, cellValue, index) {
      if (
        cellValue &&
        this.approvalStatusList.filter(v => v.value === cellValue) &&
        this.approvalStatusList.filter(v => v.value === cellValue)[0]
      ) {
        return this.approvalStatusList.filter(v => v.value === cellValue)[0]
          .label
      }
      return null
    },
    // 关掉OCR弹框
    ocrClose () {
      this.ocrVisible = false
    }
  }
}
</script>
<style lang="scss" scoped>
.companyInfo-sec {
  min-height: 100%;
  .comInfosteps {
    padding: 12px 4%;
  }
  .info-fill-area {
    padding: 0 10px 20px 10px;
  }
  .overseasRelation {
    padding-top: 100px;
    width: 50%;
    margin: 0 auto;
  }
  .companyInfoFill {
    padding-right: 210px;
    padding-bottom: 40px;
  }
  .submitSuccess {
    padding-top: 100px;
    text-align: center;
    .success-icon-tip {
      font-size: 28px;
      color: #13ce66;
    }
    .success-icon-word {
      font-size: 16px;
      line-height: 40px;
    }
    .success-icon-des {
      font-size: 14px;
      color: #666;
    }
  }
  .info-fill-progress {
    position: absolute;
    width: 210px;
    top: 15px;
    right: 16px;
    bottom: 0px;
    background: #fff;
    .progressTitle {
      height: 40px;
      line-height: 40px;
      padding-left: 30px;
      background: #f7f9fa;
      font-size: 14px;
    }
    .progressCont {
      padding: 6px;
      .progressItem {
        padding: 8px 10px;
        border-radius: 3px;
        .progress-title {
          font-size: 14px;
        }
        .progress-bar {
          height: 15px;
        }
        &.current {
          background: #f5f5f5;
        }
      }
    }
  }
  &.widthCtrl {
    .comInfosteps {
      width: 75.5%;
    }
  }
  .opt-row {
    margin-bottom: 10px;
  }
}
.info-fill-progress {
  position: absolute;
  padding-top: 13px;
  width: 210px;
  top: 15px;
  right: 16px;
  bottom: 0px;
  background: #fff;
  .progressTitle {
    height: 40px;
    line-height: 40px;
    padding-left: 30px;
    background: #f7f9fa;
    font-size: 14px;
  }
  .progressCont {
    padding: 6px;
    .progressItem {
      padding: 8px 10px;
      border-radius: 3px;
      .progress-title {
        font-size: 14px;
      }
      .progress-bar {
        height: 15px;
      }
      &.current {
        background: #f5f5f5;
      }
    }
  }
}
.companyInfo-sec .el-steps.comInfosteps .el-step__title {
  font-size: 14px;
}
.download-link-wrap .close-icon {
  cursor: pointer;
  display: inline-block;
  vertical-align: middle;
}
.info-fill-progress .el-progress-bar {
  padding-right: 70px;
  margin-right: -70px;
}
.left_div > p {
  margin: 0;
}
.the-vendorGreenChannelDetail-detail {
  .sub_header {
    margin: 0 0 10px;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
}
.companyInfoFill {
  padding-right: 210px;
  padding-bottom: 40px;
}
.download-link-wrap .close-icon {
  cursor: pointer;
  display: inline-block;
  vertical-align: middle;
}
.dimension>:first-child{
  display: none;
}
:deep(.el-checkbox:last-of-type){
  height: 30px !important;
}

</style>
