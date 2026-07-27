<template>
  <el-container
    class="the-vendorInfoChangeDetail-detail"
    direction="vertical"
  >
    <CWorkflowMulti
      ref="workflowMulti"
      v-model="activeTabName"
      :fun-params="workflowParamsInfo"
      :button-config-info="buttonConfigInfo"
      @tab-click="workflowView"
      @workflow-handler="workflowHandler"
      @click-handler="(type) => submitHandleFuntion(type)"
      @submit-direct="(type) => submitHandleFuntion(type)"
      @confirm="(type, comment) => submitHandleFuntion(type, comment)"
      @close-tab="back"
    >
      <el-main ref="companyChangeFill">
        <div class="companyInfoFillChange">
          <el-collapse
            v-model="activeDims"
            class="tab-form-style"
          >
            <!-- 供应商信息 -->
            <el-collapse-item
              ref="infoChange"
              :title="$t('vendorMod.vendorInfo')"
              name="1"
            >
              <el-form
                ref="changeInfoForm"
                class="form-fill-style"
                :model="infoChangeModel.infoChange"
                :rules="infoChangeModel.infoChangeRules"
                :disabled="disabledBol"
                :show-message="false"
              >
                <srm-row :gutter="32">
                  <srm-col :init-col="4">
                    <!-- 变更单号 -->
                    <el-form-item :label="$t('vendorMod.changeApplyNo')">
                      <el-input
                        v-model="infoChangeModel.infoChange.changeApplyNo"
                        disabled
                      />
                    </el-form-item>
                  </srm-col>
                  <!-- 采购商可以选择供应商 -->
                  <!-- 供应商名称 -->
                  <srm-col
                    v-if="curRole === 'BUYER'"
                    :init-col="4"
                  >
                    <el-form-item
                      prop="companyId"
                      :label="$t('common.vendorName')"
                    >
                      <QuickSearch
                        :show-input="infoChangeModel.infoChange.companyName"
                        show-key="companyName"
                        :scope-data="infoChangeModel.infoChange"
                        name="scc_sup_company_info_display_buyer"
                        @close-quicksearch="getCompanyObj"
                      />
                    </el-form-item>
                  </srm-col>
                  <!-- 供应商可以选择通知的业务人员 -->
                  <srm-col
                    v-if="curRole === 'VENDOR'"
                    :init-col="4"
                  >
                    <!-- 通知业务人员 -->
                    <el-form-item
                      prop="noticeByName"
                      :label="$t('vendorMod.noticeByName')"
                    >
                      <el-input
                        v-model="infoChangeModel.infoChange.noticeByName"
                        disabled
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col :init-col="4">
                    <!-- 是否是4M变更 -->
                    <el-form-item
                      prop="enable4MChange"
                      :label="$t('vendorMod.enable4MChange')"
                      class="the_info_message_wrapper"
                    >
                      <DictSelect
                        v-model="infoChangeModel.infoChange.enable4MChange"
                        code="YES_OR_NO"
                      />
                      <el-tooltip class="enable4MChangeDetail" effect="dark" :content="$t('vendorMod.tipsOf4M')" placement="top">
                        <i class="el-icon-question" />
                      </el-tooltip>
                    </el-form-item>
                  </srm-col>
                  <srm-col :init-col="4">
                    <!-- 变更附件 -->
                    <el-form-item
                      prop="changeFileId"
                      :label="$t('vendorMod.changeFile')"
                    >
                      <div>
                        <srm-common-file
                          :extra-data="fileInfo"
                          :default-file="{
                            fileId: infoChangeModel.infoChange.changeFileId,
                            fileName: infoChangeModel.infoChange.changeFileName
                          }"
                          @on-change="({file}) => changeFileUploadSuccess(file)"
                        />
                      </div>
                    </el-form-item>
                  </srm-col>
                  <srm-col :init-col="1">
                    <!-- 变更说明 -->
                    <el-form-item
                      prop="changeExplain"
                      :label="$t('vendorMod.changeExplain')"
                    >
                      <el-input
                        v-model="infoChangeModel.infoChange.changeExplain"
                        :autosize="{ minRows: 3 }"
                        type="textarea"
                      />
                    </el-form-item>
                  </srm-col>
                </srm-row>
              </el-form>
            </el-collapse-item>
          </el-collapse>
          <el-tabs v-model="activeName" type="card" class="changeTab">
            <el-tab-pane ref="companyType" :label="$t('vendorMod.companyType')" name="companyType" class="formClassWrap">
              <!--企业性质-->
              <CompanyType
                :cur-type="curType"
                :cur-rel="curRel"
                :base-info-model="baseInfoModel"
                :rules="{}"
                :base-info-dim-field-contexts="baseInfoDimFieldContexts"
                :base-dim-model="baseDimModel"
                :base-dim-rules="baseDimRules"
                :city-list="cityList"
                :province-list="provinceList"
                :info-change-model="infoChangeModel"
                class="formClassAll"
              />
              <!--企业性质变更后-->
              <div class="formClassAll">
                <div class="changeTitle">
                  <i />{{ $t("supplierChange.afterChange") }}
                </div>
                <el-form
                  ref="baseInfoForm"
                  class="base-form-info form-fill-style"
                  :model="baseInfoModelChange.baseInfoForm"
                  :rules="baseInfoModelChange.companyInfoRules"
                  :show-message="false"
                  :disabled="disabledBol"
                >
                  <srm-row :gutter="32">
                    <srm-col :init-col="1">
                      <!-- 境内外关系 -->
                      <el-form-item
                        prop="overseasRelation"
                        :label="$t('vendorMod.overseasRelation')"
                        :required="true"
                      >
                        <DictSelect
                          v-model="baseInfoModelChange.baseInfoForm.overseasRelation"
                          code="RELATION"
                          :disabled="true"
                          @change="overseasChangeHandle"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col
                      v-if="curRel === 'INSIDE'"
                      :init-col="1"
                    >
                      <!-- 企业性质 -->
                      <el-form-item
                        prop="companyType"
                        :label="$t('vendorMod.companyType')"
                        :required="curRel === 'INSIDE'"
                        :class="baseInfoModelChange.baseInfoForm.companyType!=baseInfoModel.baseInfoForm.companyType?'redColorFont':null"
                      >
                        <DictSelect
                          v-model="baseInfoModelChange.baseInfoForm.companyType"
                          code="COMPANY_NATURE"
                          :class="baseInfoModelChange.baseInfoForm.companyType!=baseInfoModel.baseInfoForm.companyType?'redColorFont':null"
                          @change="companyTypeChangeHandle"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :init-col="1">
                      <!-- 是否长期供应商 -->
                      <el-form-item :label="$t('vendorMod.ifLongTermSupplier')">
                        <el-radio v-model="baseInfoModelChange.baseInfoForm.ifLongPeriod" label="Y" class="formCheckbox">
                          {{ $t('common.yes') }}
                        </el-radio>
                        <el-radio v-model="baseInfoModelChange.baseInfoForm.ifLongPeriod" label="N" class="formCheckbox">
                          {{ $t('common.no') }}
                        </el-radio>
                      </el-form-item>
                    </srm-col>
                    <srm-col :init-col="1">
                      <!-- 供应商业务类型 -->
                      <el-form-item
                        :label="$t('vendorMod.vendorBusinessType')"
                        prop="ceeaSupBusinessType"
                        :class="baseInfoModelChange.baseInfoForm.ceeaSupBusinessType!=baseInfoModel.baseInfoForm.ceeaSupBusinessType?'redColorFont':null"
                      >
                        <DictSelect
                          v-model="baseInfoModelChange.baseInfoForm.ceeaSupBusinessType"
                          code="SUP_BUSINESS_TYPE"
                          :disabled="true"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :init-col="1">
                      <!-- 供应商类型 -->
                      <el-form-item
                        :rules="{
                          required: true,
                          message: $t('supplierRating.supplierTypeMgs')
                        }"
                        :label="$t('supplierRating.supplierType')"
                        prop="supplierType"
                        :class="baseInfoModelChange.baseInfoForm.supplierType!=baseInfoModel.baseInfoForm.supplierType?'redColorFont':null"
                      >
                        <DictSelect
                          v-model="baseInfoModelChange.baseInfoForm.supplierType"
                          code="SUPPLIER_TYPE"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :init-col="1">
                      <!-- 商业模式 -->
                      <el-form-item
                        :label="$t('vendorMod.bizModel')"
                        :class="baseInfoModelChange.baseInfoForm.ceeaBusinessModel!=baseInfoModel.baseInfoForm.ceeaBusinessModel?'redColorFont':null"
                      >
                        <DictSelect
                          v-model="baseInfoModelChange.baseInfoForm.ceeaBusinessModel"
                          code="BIZ_MODEL"
                        />
                      </el-form-item>
                    </srm-col>
                  </srm-row>
                </el-form>
              </div>
            </el-tab-pane>
            <el-tab-pane ref="enterpriseThreeCertificates" :label="$t('vendorMod.enterpriseThreeCertificates')" name="enterpriseThreeCertificates" class="formClassWrap">
              <!--企业三证-->
              <CompanyEnterpriseThreeCertificates
                :cur-type="curType"
                :cur-rel="curRel"
                :base-info-model="baseInfoModel"
                :rules="{}"
                :base-info-dim-field-contexts="baseInfoDimFieldContexts"
                :base-dim-model="baseDimModel"
                :base-dim-rules="baseDimRules"
                :city-list="cityList"
                :province-list="provinceList"
                :info-change-model="infoChangeModel"
                class="formClassAll"
              />
              <!--企业三证变更后-->
              <div class="formClassAll">
                <div class="changeTitle">
                  <i />{{ $t("supplierChange.afterChange") }}
                </div>
                <el-form
                  ref="baseInfoForm"
                  class="base-form-info form-fill-style"
                  :model="baseInfoModelChange.baseInfoForm"
                  :rules="baseInfoModelChange.companyInfoRules"
                  :show-message="false"
                  :disabled="disabledBol"
                >
                  <srm-row :gutter="32">
                    <srm-col :init-col="1">
                      <!-- 营业执照 -->
                      <el-form-item
                        v-if="infoChangeModel.infoChange.companyName"
                        prop="businessLicenseFileId"
                        :label="$t('vendorMod.businessLicense')"
                        :required="curType === 'FEIYINGLI' || curRel === 'OUT' ? false : true"
                      >
                        <div>
                          <srm-common-file
                            :extra-data="fileInfo"
                            :default-file="{
                              fileId: baseInfoModelChange.baseInfoForm.businessLicenseFileId,
                              fileName: baseInfoModelChange.baseInfoForm.businessLicense
                            }"
                            :readonly="disabledBol"
                            @on-change="({file}) => handleUploadSuccess(file)"
                          />
                        </div>
                      </el-form-item>
                    </srm-col>
                    <srm-col :init-col="1">
                      <!-- 企业名称 -->
                      <el-form-item
                        prop="companyName"
                        :label="$t('vendorMod.companyName')"
                        :required="true"
                        :class="baseInfoModelChange.baseInfoForm.companyName!=baseInfoModel.baseInfoForm.companyName?'redColorFont':null"
                      >
                        <el-input
                          v-model="baseInfoModelChange.baseInfoForm.companyName"
                          class="el-input-all"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :init-col="1">
                      <!-- 法人代表 -->
                      <el-form-item
                        prop="legalPerson"
                        :label="$t('vendorMod.legalPerson')"
                        :required="curRel === 'INSIDE' ? true : false"
                        :class="baseInfoModelChange.baseInfoForm.legalPerson!=baseInfoModel.baseInfoForm.legalPerson?'redColorFont':null"
                      >
                        <el-input v-model="baseInfoModelChange.baseInfoForm.legalPerson" class="el-input-all" />
                      </el-form-item>
                    </srm-col>
                    <!-- 注册资本(万元) -->
                    <srm-col
                      v-if="curType !== 'GETI'"
                      :init-col="1"
                    >
                      <el-form-item
                        prop="registeredCapital"
                        :label="$t('vendorMod.registeredCapital')"
                        :required="curType === 'GETI' || curType === 'FEIYINGLI' ? false : true"
                        :class="baseInfoModelChange.baseInfoForm.registeredCapital!=baseInfoModel.baseInfoForm.registeredCapital?'redColorFont':null"
                      >
                        <el-input
                          v-model="baseInfoModelChange.baseInfoForm.registeredCapital"
                          :placeholder="$t('common.pleaseInput')"
                          class="input-with-select"
                        >
                          <DictSelect
                            slot="append"
                            v-model="baseInfoModelChange.baseInfoForm.registCurrency"
                            code="BID_TENDER_CURRENCY"
                            :placeholder="$t('vendorMod.currencyCode')"
                            style="width: 110px !important;"
                          />
                        </el-input>
                      </el-form-item>
                    </srm-col>
                    <srm-col :init-col="1">
                      <!-- 成立日期 -->
                      <el-form-item
                        prop="companyCreationDate"
                        :label="$t('vendorMod.creationDate')"
                        :required="curType === 'FEIYINGLI' ? false : true"
                        :class="baseInfoModelChange.baseInfoForm.companyCreationDate!=baseInfoModel.baseInfoForm.companyCreationDate?'redColorFont':null"
                      >
                        <el-date-picker
                          v-model="baseInfoModelChange.baseInfoForm.companyCreationDate"
                          type="date"
                          :placeholder="$t('common.pleaseSelectDate')"
                          format="yyyy-MM-dd"
                          value-format="timestamp"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :init-col="1">
                      <!-- 企业简称 -->
                      <el-form-item
                        prop="companyShortName"
                        :label="$t('vendorMod.companyShortName')"
                        :class="baseInfoModelChange.baseInfoForm.companyShortName!=baseInfoModel.baseInfoForm.companyShortName?'redColorFont':null"
                      >
                        <el-input v-model="baseInfoModelChange.baseInfoForm.companyShortName" class="el-input-all" />
                      </el-form-item>
                    </srm-col>
                    <!-- 只有境内供应商有 -->
                    <srm-col
                      v-if="curRel === 'INSIDE'"
                      :init-col="1"
                    >
                      <!-- 统一社会信用代码 -->
                      <el-form-item
                        prop="lcCode"
                        :label="$t('vendorMod.lcCode')"
                        :required="curRel === 'INSIDE' ? true : false"
                        :class="baseInfoModelChange.baseInfoForm.lcCode!=baseInfoModel.baseInfoForm.lcCode?'redColorFont':null"
                      >
                        <el-input v-model="baseInfoModelChange.baseInfoForm.lcCode" class="el-input-all" />
                      </el-form-item>
                    </srm-col>
                    <srm-col :init-col="1">
                      <!-- 登记机关 -->
                      <el-form-item
                        prop="registrationAuthority"
                        :label="$t('vendorMod.registrationAuthority')"
                        :class="baseInfoModelChange.baseInfoForm.registrationAuthority!=baseInfoModel.baseInfoForm.registrationAuthority?'redColorFont':null"
                      >
                        <el-input
                          v-model="baseInfoModelChange.baseInfoForm.registrationAuthority"
                          class="el-input-all"
                        />
                      </el-form-item>
                    </srm-col>
                    <!-- 个体户不用显示 -->
                    <srm-col
                      v-if="curType !== 'GETI'"
                      :init-col="1"
                    >
                      <!-- 营业日期从 -->
                      <el-form-item
                        prop="businessStartDate"
                        :label="$t('vendorMod.businessStartFrom')"
                        :required="curType === 'FEIYINGLI' ? false : true"
                        :class="baseInfoModelChange.baseInfoForm.businessStartDate!=baseInfoModel.baseInfoForm.businessStartDate?'redColorFont':null"
                      >
                        <el-date-picker
                          v-model="baseInfoModelChange.baseInfoForm.businessStartDate"
                          type="date"
                          :placeholder="$t('common.pleaseSelectDate')"
                          format="yyyy-MM-dd"
                          value-format="timestamp"
                        />
                      </el-form-item>
                    </srm-col>
                    <!-- 个体户不用显示 -->
                    <srm-col
                      v-if="curType !== 'GETI'"
                      :init-col="1"
                    >
                      <!-- 营业日期至 -->
                      <el-form-item
                        prop="businessEndDate"
                        :label="$t('vendorMod.businessEndAt')"
                        :required="curType === 'FEIYINGLI' ? false : true"
                        :class="baseInfoModelChange.baseInfoForm.businessEndDate!=baseInfoModel.baseInfoForm.businessEndDate?'redColorFont':null"
                      >
                        <el-date-picker
                          v-model="baseInfoModelChange.baseInfoForm.businessEndDate"
                          type="date"
                          :placeholder="$t('common.pleaseSelectDate')"
                          format="yyyy-MM-dd"
                          value-format="timestamp"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :init-col="1">
                      <!-- 营业地址（国家/地区） -->
                      <el-form-item
                        prop="companyCountry"
                        :label="$t('vendorMod.businessAddr')"
                        :required="true"
                        :class="baseInfoModelChange.baseInfoForm.companyCountry!=baseInfoModel.baseInfoForm.companyCountry?'redColorFont':null"
                      >
                        <DictSelect
                          v-model="baseInfoModelChange.baseInfoForm.companyCountry"
                          code="country"
                          filterable
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col
                      v-if="baseInfoModelChange.baseInfoForm.companyCountry === 'CN'"
                      :init-col="1"
                    >
                      <!-- 省份/州 -->
                      <el-form-item
                        prop="companyProvince"
                        :label="$t('vendorMod.province')"
                        :required="baseInfoModelChange.baseInfoForm.companyCountry === 'CN' ? true : false"
                        :class="baseInfoModelChange.baseInfoForm.companyProvince!=baseInfoModel.baseInfoForm.companyProvince?'redColorFont':null"
                      >
                        <el-select
                          v-model="baseInfoModelChange.baseInfoForm.companyProvince"
                          filterable
                          @change="provinceChangeHandle"
                        >
                          <el-option
                            v-for="item in provinceList"
                            :key="item.id"
                            :label="item.label"
                            :value="item.value"
                          />
                        </el-select>
                      </el-form-item>
                    </srm-col>
                    <srm-col
                      v-if="baseInfoModelChange.baseInfoForm.companyCountry === 'CN'"
                      :init-col="1"
                    >
                      <!-- 城市 -->
                      <el-form-item
                        prop="companyCity"
                        :label="$t('vendorMod.city')"
                        :required="baseInfoModelChange.baseInfoForm.companyCountry === 'CN' ? true : false"
                        :class="baseInfoModelChange.baseInfoForm.companyCity!=baseInfoModel.baseInfoForm.companyCity?'redColorFont':null"
                      >
                        <el-select
                          v-model="baseInfoModelChange.baseInfoForm.companyCity"
                          filterable
                        >
                          <el-option
                            v-for="item in cityList"
                            :key="item.id"
                            :label="item.label"
                            :value="item.value"
                          />
                        </el-select>
                      </el-form-item>
                    </srm-col>
                    <srm-col :init-col="1">
                      <!-- 详细地址 -->
                      <el-form-item
                        prop="businessScope"
                        :label="$t('components.address.detailAddress')"
                        :required="curRel === 'INSIDE' ? true : false"
                        :class="baseInfoModelChange.baseInfoForm.companyAddress!=baseInfoModel.baseInfoForm.companyAddress?'redColorFont':null"
                      >
                        <el-input v-model="baseInfoModelChange.baseInfoForm.companyAddress" class="el-input-all" />
                      </el-form-item>
                    </srm-col>
                    <srm-col :init-col="1">
                      <!-- 营业范围 -->
                      <el-form-item
                        prop="businessScope"
                        :label="$t('vendorMod.businessScope')"
                        :required="curType === 'FEIYINGLI' ? false : true"
                        :class="baseInfoModelChange.baseInfoForm.businessScope!=baseInfoModel.baseInfoForm.businessScope?'redColorFont':null"
                      >
                        <el-input v-model="baseInfoModelChange.baseInfoForm.businessScope" class="el-input-all" />
                      </el-form-item>
                    </srm-col>
                  </srm-row>
                </el-form>
              </div>
            </el-tab-pane>
            <!--基本信息-->
            <!--基本信息交互比较多变更后状态先不拆分子组件-->
            <el-tab-pane ref="companyInfo" :label="$t('vendorMod.baseInfo')" name="companyInfo" class="formClassWrap">
              <!--基本信息变更前-->
              <CompanyInfo
                :cur-type="curType"
                :cur-rel="curRel"
                :base-info-model="baseInfoModel"
                :rules="{}"
                :base-info-dim-field-contexts="baseInfoDimFieldContexts"
                :base-dim-model="baseDimModel"
                :base-dim-rules="baseDimRules"
                :city-list="cityList"
                :province-list="provinceList"
                :info-change-model="infoChangeModel"
                class="formClassAll"
              />
              <!--基本信息变更后-->
              <div class="formClassAll">
                <div class="changeTitle">
                  <i />{{ $t("supplierChange.afterChange") }}
                </div>
                <el-form
                  ref="baseInfoForm"
                  class="base-form-info form-fill-style"
                  :model="baseInfoModelChange.baseInfoForm"
                  :rules="baseInfoModelChange.companyInfoRules"
                  :show-message="false"
                  :disabled="disabledBol"
                >
                  <srm-row :gutter="32">
                    <srm-col :init-col="1">
                      <!-- 代理品牌 -->
                      <el-form-item
                        :label="$t('vendorMod.agencyBrand')"
                        prop="ceeaAgentBrand"
                        :rules="{
                          required: itemisRequired2 ? true : false,
                          message: $t('vendorMod.msgAgencyBrand')
                        }"
                        :class="baseInfoModelChange.baseInfoForm.ceeaAgentBrand!=baseInfoModel.baseInfoForm.ceeaAgentBrand?'redColorFont':null"
                      >
                        <el-input
                          v-model="baseInfoModelChange.baseInfoForm.ceeaAgentBrand"
                          class="el-input-all"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :init-col="1">
                      <!-- 是否上市 -->
                      <el-form-item :label="$t('vendorMod.ifListed')">
                        <el-radio v-model="baseInfoModelChange.baseInfoForm.ceeaIfListed" label="Y" class="formCheckbox">
                          {{ $t('common.yes') }}
                        </el-radio>
                        <el-radio v-model="baseInfoModelChange.baseInfoForm.ceeaIfListed" label="N" class="formCheckbox">
                          {{ $t('common.no') }}
                        </el-radio>
                      </el-form-item>
                    </srm-col>
                    <srm-col
                      v-if="baseInfoModelChange.baseInfoForm.ceeaIfListed == 'Y'"
                      :init-col="1"
                    >
                      <!-- 上市时间 -->
                      <el-form-item
                        :label="$t('vendorMod.listedDate')"
                        prop="ceeaListedTime"
                      >
                        <el-date-picker
                          v-model="baseInfoModelChange.baseInfoForm.ceeaListedTime"
                          :class="baseInfoModelChange.baseInfoForm.ceeaListedTime!=baseInfoModel.baseInfoForm.ceeaListedTime?'redColorFont':null"
                          type="date"
                          format="yyyy-MM-dd"
                          value-format="yyyy-MM-dd"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :init-col="1">
                      <!-- 是否有母公司 -->
                      <el-form-item :label="$t('vendorMod.ifParentCompany')">
                        <el-radio v-model="baseInfoModelChange.baseInfoForm.ceeaHasParentCompany" label="Y" class="formCheckbox">
                          {{ $t('common.yes') }}
                        </el-radio>
                        <el-radio v-model="baseInfoModelChange.baseInfoForm.ceeaHasParentCompany" label="N" class="formCheckbox">
                          {{ $t('common.no') }}
                        </el-radio>
                      </el-form-item>
                    </srm-col>
                    <srm-col
                      v-if="baseInfoModelChange.baseInfoForm.ceeaHasParentCompany == 'Y'"
                      :init-col="1"
                    >
                      <!-- 母公司名称 -->
                      <el-form-item
                        :label="$t('vendorMod.parentCompanyName')"
                        prop="ceeaParentCompanyName"
                        :class="baseInfoModelChange.baseInfoForm.ceeaParentCompanyName!=baseInfoModel.baseInfoForm.ceeaParentCompanyName?'redColorFont':null"
                      >
                        <el-input
                          v-model="baseInfoModelChange.baseInfoForm.ceeaParentCompanyName"
                          class="el-input-all"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col
                      v-if="baseInfoModelChange.baseInfoForm.ceeaHasParentCompany == 'Y'"
                      :init-col="1"
                    >
                      <!-- 母公司统一信用代码 -->
                      <el-form-item
                        :label="$t('vendorMod.parentCompanyLcCode')"
                        prop="ceeaParentCompanyLcCode"
                        :class="baseInfoModelChange.baseInfoForm.ceeaParentCompanyLcCode!=baseInfoModel.baseInfoForm.ceeaParentCompanyLcCode?'redColorFont':null"
                      >
                        <el-input
                          v-model="baseInfoModelChange.baseInfoForm.ceeaParentCompanyLcCode"
                          class="el-input-all"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col v-if="baseInfoModelChange.baseInfoForm.overseasRelation !== 'INSIDE'" :init-col="1">
                      <el-form-item
                        label="D-U-N-S"
                        :class="baseInfoModelChange.baseInfoForm.dunsCode!=baseInfoModel.baseInfoForm.dunsCode?'redColorFont':null"
                      >
                        <el-input
                          v-model="baseInfoModelChange.baseInfoForm.dunsCode"
                          class="el-input-all"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :init-col="1">
                      <!-- 企业简介 -->
                      <el-form-item
                        :label="$t('vendorMod.companyProfile')"
                        prop="ceeaCompanyIntro"
                        :rules="{
                          required: itemisRequired ? true : false,
                          message: this.$t('vendorMod.msgCompanyProfile')
                        }"
                        :class="baseInfoModelChange.baseInfoForm.ceeaCompanyIntro!=baseInfoModel.baseInfoForm.ceeaCompanyIntro?'redColorFont':null"
                      >
                        <el-input
                          v-model="baseInfoModelChange.baseInfoForm.ceeaCompanyIntro"
                          class="el-input-all"
                        />
                      </el-form-item>
                    </srm-col>
                  </srm-row>
                </el-form>
                <el-form
                  v-if="baseInfoDimFieldContexts.length > 0"
                  ref="baseDimForm"
                  class="base-form-info form-fill-style"
                  :model="baseDimModelChange"
                  :rules="baseDimRules"
                  :show-message="false"
                  :disabled="disabledBol"
                >
                  <srm-row :gutter="32">
                    <!-- 拓展字段显示 -->
                    <srm-col
                      v-for="item in baseInfoDimFieldContexts"
                      :key="item.fieldConfigId"
                      :init-col="1"
                    >
                      <el-form-item
                        :prop="item.fieldCode"
                        :label="item.languageCode ? $t(item.languageCode) : item.fieldName"
                        :class="baseDimModelChange[item.fieldCode]!=baseDimModel[item.fieldCode]?'redColorFont':null"
                      >
                        <el-input
                          v-model="baseDimModelChange[item.fieldCode]"
                          class="el-input-all"
                        />
                      </el-form-item>
                    </srm-col>
                  </srm-row>
                </el-form>
              </div>
            </el-tab-pane>
            <!--联系人信息-->
            <el-tab-pane ref="contactData" :label="$t('vendorMod.contactInfo')" name="contactData">
              <!--变更前-->
              <div class="changeTitle changeTitleTop">
                <i />{{ $t("supplierChange.beforeChange") }}
              </div>
              <ContactData
                :contact-data-s="contactData"
                :contact-data="contactData"
                :disabled-bol="true"
                :contact-dim-field-contexts="contactDimFieldContexts"
                :curOpt="curOpt"
              />
              <!--变更后-->
              <div class="changeTitle changeTitleTop margin-title-top">
                <i />{{ $t("supplierChange.afterChange") }}
              </div>
              <ContactData
                ref="contactDataD"
                :contact-data-s="contactDataChange"
                :contact-data="contactData"
                :disabled-bol="disabledBol"
                :contact-dim-field-contexts="contactDimFieldContexts"
                :curOpt="curOpt"
              />
            </el-tab-pane>
            <!--银行信息-->
            <el-tab-pane ref="bankInfo" :label="$t('vendorMod.bankInfo')" name="bankInfo">
              <!--变更前-->
              <div class="changeTitle changeTitleTop">
                <i />{{ $t("supplierChange.beforeChange") }}
              </div>
              <BankInfo
                :disabled-bol="true"
                :bank-data="bankData"
                :bank-data-y="bankData"
                :row-class-name="rowClassName"
                :bank-dim-field-contexts="bankDimFieldContexts"
                :curOpt="curOpt"
              />
              <!--变更后-->
              <div class="changeTitle changeTitleTop margin-title-top">
                <i />{{ $t("supplierChange.afterChange") }}
              </div>
              <BankInfo
                ref="bankInfoD"
                :disabled-bol="disabledBol"
                :bank-data="bankDataChange"
                :bank-data-y="bankData"
                :row-class-name="rowClassName"
                :bank-dim-field-contexts="bankDimFieldContexts"
                :curOpt="curOpt"
              />
            </el-tab-pane>
            <!--财务信息-->
            <el-tab-pane ref="financeInfo" :label="$t('vendorMod.financeInfo')" name="financeInfo">
              <!--变更前-->
              <div class="changeTitle changeTitleTop">
                <i />{{ $t("supplierChange.beforeChange") }}
              </div>
              <FinanceInfo
                :disabled-bol="true"
                :finance-info-data="financeInfoData"
                :finance-info-data-y="financeInfoData"
                :finance-dim-field-contexts="financeDimFieldContexts"
              />
              <!--变更后-->
              <div class="changeTitle changeTitleTop margin-title-top">
                <i />{{ $t("supplierChange.afterChange") }}
              </div>
              <FinanceInfo
                ref="financeInfoD"
                :disabled-bol="disabledBol"
                :finance-info-data="financeInfoDataChange"
                :finance-info-data-y="financeInfoData"
                :finance-dim-field-contexts="financeDimFieldContexts"
              />
            </el-tab-pane>
            <!--供应商地点信息-->
            <el-tab-pane ref="vendorSiteInfos" :label="$t('vendorMod.vendorSiteInfos')" name="vendorSiteInfos">
              <!--变更前-->
              <div class="changeTitle changeTitleTop">
                <i />{{ $t("supplierChange.beforeChange") }}
              </div>
              <VendorSiteInfos
                :disabled-bol="true"
                :site-info-changes="siteInfoChanges"
                :site-info-changes-y="siteInfoChanges"
                :page-size-approval-bidding-item-lis="pageSize_approvalBiddingItemLis"
                :current-page="currentPage"
                :province-list="provinceList"
                :city-list="cityList"
                :curOpt="curOpt"
              />
              <!--变更后-->
              <div class="changeTitle changeTitleTop margin-title-top">
                <i />{{ $t("supplierChange.afterChange") }}
              </div>
              <VendorSiteInfos
                ref="vendorSiteInfosD"
                :disabled-bol="disabledBol"
                :site-info-changes="siteInfoChangesChange"
                :site-info-changes-y="siteInfoChanges"
                :page-size-approval-bidding-item-lis="pageSize_approvalBiddingItemLis"
                :current-page="currentPage"
                :province-list="provinceList"
                :city-list="cityList"
                :curOpt="curOpt"
              />
            </el-tab-pane>
            <!--模板配置配出来-->
            <template v-for="(item,index) in modelConfig.formDimVOList">
              <el-tab-pane
                v-if="item.originalDimFlag === 'N'"
                :ref="item.dimId"
                :label="item.dimName"
                :name="item.dimCode"
                :key="item.dimCode"
              >
                <!--变更前-->
                <div class="changeTitle changeTitleTop">
                  <i />{{ $t("supplierChange.beforeChange") }}
                </div>
                <!-- 如果是表单的话显示 -->
                <model-config-form
                  v-if="item.dimType === 'form' && item.originalDimFlag === 'N'"
                  :dimConfig="modelConfig.dimConfigMap[item.dimCode]"
                  :formValue="beforeDimDataValue"
                  :disabled="true"
                />
                <!-- 如果是表格的话显示 -->
                <model-config-table
                  v-if="item.dimType === 'table'"
                  :dimConfig="modelConfig.dimConfigMap[item.dimCode]"
                  :index="index"
                  :tableValue="beforeDimDataValue"
                  :disabled="true"
                />
                <!--变更后-->
                <div class="changeTitle changeTitleTop margin-title-top">
                  <i />{{ $t("supplierChange.afterChange") }}
                </div>
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
              </el-tab-pane>
            </template>
            <!--相关认证信息-->
            <el-tab-pane ref="sceneAttachment" :label="$t('vendorMod.sceneAttachmentInfo')" name="sceneAttachment">
              <!--变更前-->
              <div class="changeTitle changeTitleTop">
                <i />{{ $t("supplierChange.beforeChange") }}
              </div>
              <SceneAttachment
                :disabled-bol="true"
                :scene-attachment-data="sceneAttachmentData"
                :scene-attachment-data-y="sceneAttachmentData"
                :accept-file-type="acceptFileType"
                :row-class-name="rowClassName"
                :file-info="fileInfo"
                :curOpt="curOpt"
              />
              <!--变更后-->
              <div class="changeTitle changeTitleTop margin-title-top">
                <i />{{ $t("supplierChange.afterChange") }}
              </div>
              <SceneAttachment
                ref="sceneAttachmentD"
                :disabled-bol="disabledBol"
                :scene-attachment-data="sceneAttachmentDataChange"
                :scene-attachment-data-y="sceneAttachmentData"
                :accept-file-type="acceptFileType"
                :row-class-name="rowClassName"
                :file-info="fileInfo"
                :curOpt="curOpt"
              />
            </el-tab-pane>
            <!--其他附件信息-->
            <el-tab-pane ref="attachFile" :label="$t('vendorMod.otherAttachInfo')" name="attachFile">
              <!--变更前-->
              <div class="changeTitle changeTitleTop">
                <i />{{ $t("supplierChange.beforeChange") }}
              </div>
              <FileDynamic
                ref="sceneAttachment2"
                v-model="companyInfoFileList"
                scene-module-code="SCENE_SUPPLIER_ATTACHMENT"
                :business-id="companyId"
                :editable="false"
              />
              <!--变更后-->
              <div class="changeTitle changeTitleTop margin-title-top">
                <i />{{ $t("supplierChange.afterChange") }}
              </div>
              <FileDynamic
                ref="sceneAttachment"
                v-model="companyInfoFileListChange"
                scene-module-code="SCENE_SUPPLIER_ATTACHMENT"
                :business-id="changeId"
                :editable="curOpt === 'add' || curOpt === 'edit'"
              />
            </el-tab-pane>
            <!--扩展-->
            <el-tab-pane
              v-for="(item, index) in dimensionData"
              :ref="item.dimCode"
              :key="index"
              :class="associationDimension(item.relateDimCode)"
              :label="item.dimName"
              :name="item.dimName"
            >
              <!--变更前-->
              <div class="changeTitle changeTitleTop">
                <i />{{ $t("supplierChange.beforeChange") }}
              </div>
              <!-- 如果是表格的时候显示 -->
              <TableExtend
                v-if="item.dimShowType == 'TABLE' && evalF(item.showDimCondition)"
                :ref="'extendData' + index"
                :table-data="item.dimFieldConfigS"
                :index="index"
                :table-extend-list="tableExtendList"
                :add-one-table-data="addOneTableData"
                :model2="Emodel2"
                :disabled="true"
                :rules="baseDimRules"
              />
              <!-- 如果是表单的话显示 -->
              <FormExtend2
                v-if="item.dimShowType == 'FORM'"
                :ref="'extendData' + index"
                :form-data="item.dimFieldConfigS"
                :model2="Emodel"
                :disabled="true"
                :rules="baseDimRules"
                @selectChange="selectChanges($event)"
              />

              <!--变更后-->
              <div class="changeTitle changeTitleTop">
                <i />{{ $t("supplierChange.afterChange") }}
              </div>
              <!-- 如果是表格的时候显示 -->
              <TableExtend
                v-if="item.dimShowType == 'TABLE' && evalF(item.showDimCondition)"
                :ref="'extendData' + index"
                :table-data="item.dimFieldConfigS"
                :index="index"
                :table-extend-list="tableExtendList"
                :add-one-table-data="addOneTableData"
                :model2="Emodel2"
                :disabled="disabledBol"
                :rules="baseDimRules"
              />
              <!-- 如果是表单的话显示 -->
              <FormExtend2
                v-if="item.dimShowType == 'FORM'"
                :ref="'extendData' + index"
                :form-data="item.dimFieldConfigS"
                :model2="Emodel"
                :disabled="disabledBol"
                :rules="baseDimRules"
                @selectChange="selectChanges($event)"
              />
            </el-tab-pane>
          </el-tabs>
        </div>
        <!-- 人员选择 -->
        <CPeopleSelector
          ref="peopleSelector"
          :visible.sync="peopleDialog"
          :multi-select="false"
          @on-confirm="getPeople"
        />

        <!--操作记录-->
        <div class="operating-logs">
          <el-collapse v-model="activeCollapseOperatingLogs">
            <el-collapse-item :title="$t('common.operationRecord')" name="1">
              <el-table
                :data="operatingLogsData"
                style="width: 100%"
                border
                max-height="300px"
              >
                <!--操作时间-->
                <el-table-column
                  align="center"
                  prop="creationDate"
                  :label="$t('common.operationTime')"
                  min-width="120"
                  show-overflow-tooltip
                />
                <!--操作类型-->
                <el-table-column
                  align="center"
                  prop="operation"
                  :label="$t('contractMod.operationType')"
                  min-width="120"
                  :formatter="(row, column, cellValue) => $getDictLabel('OPERATING_TYPE', cellValue)"
                  show-overflow-tooltip
                />
                <!--原因说明-->
                <el-table-column
                  align="center"
                  prop="reason"
                  :label="$t('common.explanationOfReasons')"
                  min-width="120"
                  show-overflow-tooltip
                />
                <!--操作人-->
                <el-table-column
                  align="center"
                  prop="createdFullName"
                  :label="$t('common.operator')"
                  min-width="120"
                  show-overflow-tooltip
                />
              </el-table>
            </el-collapse-item>
          </el-collapse>
        </div>
      </el-main>
      <!--采购商 管理按钮进入-->
      <template v-if="vendorSubmittedButtonVisible" #buttonOne>
        <!--提交-->
        <el-button type="primary" @click="submitHandleFuntion('SUBMIT')">
          {{ $t('common.submit') }}
        </el-button>
      </template>
      <template v-if="vendorSubmittedButtonVisible" #buttonTwo>
        <!--驳回-->
        <el-button @click="buyerReject">
          {{ $t('common.toRefuse') }}
        </el-button>
      </template>
    </CWorkflowMulti>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import CPeopleSelector from '@/library/components/c-people-selector'
import QuickSearch from 'lib@/components/QuickSearch'
import CompanyInfo from './companyInfo'
import CompanyType from './companyType'
import CompanyEnterpriseThreeCertificates from './companyEnterpriseThreeCertificates'
import ContactData from './contactData'
import BankInfo from './bankInfo'
import FinanceInfo from './financeInfo'
import VendorSiteInfos from './vendorSiteInfos'
import SceneAttachment from './sceneAttachment'
import { LOAD_CHILDREN_OPTIONS } from '@riophae/vue-treeselect'
import ModelConfigForm from 'mod@/common/userManage/views/ModelConfig/ModelConfigForm'
import ModelConfigTable from 'mod@/common/userManage/views/ModelConfig/ModelConfigTable'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import _omit from 'lodash/omit'
import { getRegion } from '@/api/common'
import FormExtend2 from 'mod@/common/userManage/views/companyInfoMaintain/formExtend2'
import TableExtend from 'mod@/common/userManage/views/companyInfoMaintain/tableExtend'
import FileDynamic from '@/library/components/c-file-management/file-dynamic'
import { ADD_KEY, EDITABLE_KEY, UPDATE_KEY } from '@/library/components/BaseTable/utils'
import WorkflowCommon from '@/library/mixins/workflow-common'
import { vendorChangeApi, saveOrUpdateOrderByUrl } from 'modb@/vendorManagementBuyer/api/vendorManagement'
import { vendorOptCommonApi } from 'mod@/common/userManage/api'
import { vendorAttributeComApi } from 'modb@/basicSetting/api/basicSetting'
import { modelConfigApi } from '@/api/modelConfig'

export default {
  name: 'VendorInfoChangeDetail',
  components: {
    QuickSearch,
    CPeopleSelector,
    TableExtend,
    FormExtend2,
    FileDynamic,
    CompanyInfo,
    ContactData,
    BankInfo,
    VendorSiteInfos,
    SceneAttachment,
    FinanceInfo,
    CompanyType,
    CompanyEnterpriseThreeCertificates,
    ModelConfigForm,
    ModelConfigTable
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    // 公司基本信息
    const validateValue = (rule, value, callback) => {
      let _this = this
      let oldDataObj = _this.baseInfoOldData
      let curKey = rule.field // 当前字段名称
      if (curKey !== 'categoryRelChanges') {
        if (rule.required && !value) {
          callback(new Error(rule.message))
        } else if (value !== oldDataObj[curKey]) {
          const wrap = _this.$refs.companyInfo.$el
          try {
            wrap.querySelectorAll(`label[for="${curKey}"]`)[0].nextElementSibling.className =
              'el-form-item__content changeStyle'
          } catch (e) {
            console.log(e)
          }
          callback()
        } else {
          const wrap = _this.$refs.companyInfo.$el
          try {
            wrap.querySelectorAll(`label[for="${curKey}"]`)[0].nextElementSibling.className =
              'el-form-item__content'
          } catch (e) {
            console.log(e)
          }
          callback()
        }
      } else {
        callback()
      }
    }
    // 其他信息
    const validateOtherValue = (rule, value, callback) => {
      let _this = this
      let oldDataObj = _this.otherInfoOldData
      let curKey = rule.field // 当前字段名称
      if (rule.required && !value) {
        callback(new Error(rule.message))
      } else if (value !== oldDataObj[curKey]) {
        const wrap = _this.$refs.otherInfo.$el
        try {
          wrap.querySelectorAll(`label[for="${curKey}"]`)[0].nextElementSibling.className =
            'el-form-item__content changeStyle'
        } catch (e) {
          console.log(e)
        }
        callback()
      } else {
        const wrap = _this.$refs.otherInfo.$el
        try {
          wrap.querySelectorAll(`label[for="${curKey}"]`)[0].nextElementSibling.className =
            'el-form-item__content'
        } catch (e) {
          console.log(e)
        }
        callback()
      }
    }
    return {
      dimDataValue: [], // 模板配置扩展的数据
      beforeDimDataValue: [],// 变更前模板配置扩展的数据
      modelConfig: {
        dimConfigMap: {},
        formDimVOList: []
      },
      getModelConfig: {},
      itemisRequired: false,
      itemisRequired2: false,
      beforeChangeJson: '',
      activeName: 'companyType',
      selectChangeData: {},
      disabledBol: false, // 判断时候可编辑
      Emodel: {},
      Emodel2: {},
      dimensionData: [],
      currentPage: 1,
      pageSize_approvalBiddingItemLis: 10,
      loggerComment: false,
      isMounted: false,
      companyInfoFileList: [], // 附件变更数据
      companyInfoFileListChange: [], // 附件变更数据更新后
      acceptFileType: ['jpg', 'png', 'jpeg'],
      fileInfo: {
        uploadType: 'DEF',
        sourceType: 'WEB_APP',
        fileModular: 'sup',
        fileFunction: 'companyInfoMaintain',
        fileType: 'images'
      },
      changeId: null,
      sampleActiveInfo: 'tab1',
      companyId: null,
      curRel: '',
      curType: '',
      curOpt: 'add',
      orgDialog: false,
      activeDims: ['1', '11', '2', '3', '4', '5', '6', '7', '8', '9', '10'],
      relations: [], // 境内外管理
      natureList: [], // 企业性质
      currencyList: [], // 币种列表
      genderList: [],
      addressList: [],
      countryList: [], // 国家
      provinceList: [], // 省
      cityList: [], // 市
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
      approveStatus: [], // 审批状态
      categoryRelChanges: [], // 可供品类
      baseInfoOldData: {}, // 基础信息旧数据
      baseInfoDimOldData: {}, // 基础信息拓展字段
      otherInfoOldData: {}, // 其他信息旧数据
      otherInfoDimOldData: {}, // 其他信息拓展字段
      categoryRelsOldData: [], // 可供品类旧数据
      YesOrNoOptions: [
        { value: 'Y', label: this.$t('common.yes') },
        { value: 'N', label: this.$t('common.no') }
      ],
      infoChangeModel: {
        infoChange: {
          // 变更信息
          changeApplyNo: '', // 变更单号
          companyId: '', // 公司Id
          companyName: '', // 公司名称
          changeFileId: null, // 变更附件Id
          changeFileName: '', // 变更附件名称
          changeStatus: '', // 变更状态
          changeType: '', // 变更类型
          enable4MChange: '', // 是否是4M变更
          changeExplain: '', // 变更说明
          noticeById: null, // 业务人员Id
          noticeByName: '' // 业务人员名称
        },
        infoChangeRules: {
          companyId: [{ required: true, message: this.$t('vendorMod.msgVendorId') }],
          changeExplain: [{ required: true }],
          enable4MChange: [{ required: true }]
        }
      },
      baseInfoModel: {
        // 变更前的信息
        // 基础信息
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
          dimFieldContexts: {},
          categoryRelChanges: [] // 可供品类
        },
        companyInfoRules: {
          overseasRelation: [
            {
              message: this.$t('vendorMod.msgOverseasRelation'), // "请输入境内外关系"
              validator: validateValue,
              trigger: 'change'
            }
          ],
          companyType: [
            {
              message: this.$t('vendorMod.msgCompanyType'), // "请输入公司性质"
              validator: validateValue,
              trigger: 'change'
            }
          ],
          companyName: [
            {
              message: this.$t('vendorMod.msgCompanyName'), // "请输入公司名称"
              validator: validateValue,
              trigger: 'change'
            }
          ],
          businessLicenseFileId: [
            {
              message: this.$t('vendorMod.msgBusinessLicense'), // "请上传营业执照"
              validator: validateValue,
              trigger: 'change'
            }
          ],
          registeredCapital: [
            {
              message: this.$t('vendorMod.msgRegisteredCapital'), // "请输入注册资金"
              validator: validateValue,
              trigger: 'change'
            }
          ],
          companyCreationDate: [
            {
              message: this.$t('vendorMod.msgCreationDate'), // "请输入成立日期"
              validator: validateValue,
              trigger: 'change'
            }
          ],
          legalPerson: [
            {
              message: this.$t('vendorMod.msgLegalPerson'), // "请输入法人代表"
              validator: validateValue,
              trigger: 'change'
            }
          ],
          businessScope: [
            {
              message: this.$t('vendorMod.msgBusinessScope'), // "请输入业务范围"
              validator: validateValue,
              trigger: 'change'
            }
          ],
          companyStatus: [
            {
              message: this.$t('vendorMod.msgBusinessStatus'), // "请输入经营状态"
              validator: validateValue,
              trigger: 'change'
            }
          ],
          businessStartDate: [
            {
              message: this.$t('vendorMod.msgBusinessStartFrom'), // "请输入开始营业日期"
              validator: validateValue,
              trigger: 'change'
            }
          ],
          businessEndDate: [
            {
              message: this.$t('vendorMod.msgBusinessEndAt'), // "请输入结束营业日期"
              validator: validateValue,
              trigger: 'change'
            }
          ],
          lcCode: [
            {
              message: this.$t('vendorMod.msgLcCode'), // "请输入统一社会信用代码"
              validator: validateValue,
              trigger: 'change'
            }
          ],
          companyCountry: [
            {
              message: this.$t('vendorMod.msgBusinessAddr'), // "请输入注册国家"
              validator: validateValue,
              trigger: 'change'
            }
          ],
          companyProvince: [
            {
              message: this.$t('vendorMod.msgpProvince'), // "请输入省"
              validator: validateValue,
              trigger: 'change'
            }
          ],
          companyCity: [
            {
              message: this.$t('vendorMod.msgCity'), // "请输入市"
              validator: validateValue,
              trigger: 'change'
            }
          ],
          companyAddress: [
            {
              message: this.$t('vendorMod.msgDetailAddr'), // "请输入详细地址"
              validator: validateValue,
              trigger: 'change'
            }
          ],
          registrationAuthority: [
            {
              message: this.$t('vendorMod.msgRegistrationAuthority'), // "请输入登记机关"
              validator: validateValue,
              trigger: 'change'
            }
          ],
          companyShortName: [{ required: false, validator: validateValue, trigger: 'change' }], // 公司简称
          dunsCode: [{ required: false, validator: validateValue, trigger: 'change' }], // dunsCode(境外)
          categoryRelChanges: [{ required: true, validator: validateValue, trigger: 'change' }]
        }
      },
      baseInfoModelChange: {
        // 变更前的信息
        // 基础信息
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
          dimFieldContexts: {},
          categoryRelChanges: [] // 可供品类
        },
        companyInfoRules: {
          overseasRelation: [
            {
              message: this.$t('vendorMod.msgOverseasRelation'), // "请输入境内外关系"
              validator: validateValue,
              trigger: 'change'
            }
          ],
          companyType: [
            {
              message: this.$t('vendorMod.msgCompanyType'), // "请输入公司性质"
              validator: validateValue,
              trigger: 'change'
            }
          ],
          companyName: [
            {
              message: this.$t('vendorMod.msgCompanyName'), // "请输入公司名称"
              validator: validateValue,
              trigger: 'change'
            }
          ],
          businessLicenseFileId: [
            {
              message: this.$t('vendorMod.msgBusinessLicense'), // "请上传营业执照"
              validator: validateValue,
              trigger: 'change'
            }
          ],
          registeredCapital: [
            {
              message: this.$t('vendorMod.msgRegisteredCapital'), // "请输入注册资金"
              validator: validateValue,
              trigger: 'change'
            }
          ],
          companyCreationDate: [
            {
              message: this.$t('vendorMod.msgCreationDate'), // "请输入成立日期"
              validator: validateValue,
              trigger: 'change'
            }
          ],
          legalPerson: [
            {
              message: this.$t('vendorMod.msgLegalPerson'), // "请输入法人代表"
              validator: validateValue,
              trigger: 'change'
            }
          ],
          businessScope: [
            {
              message: this.$t('vendorMod.msgBusinessScope'), // "请输入业务范围"
              validator: validateValue,
              trigger: 'change'
            }
          ],
          companyStatus: [
            {
              message: this.$t('vendorMod.msgBusinessStatus'), // "请输入经营状态"
              validator: validateValue,
              trigger: 'change'
            }
          ],
          businessStartDate: [
            {
              message: this.$t('vendorMod.msgBusinessStartFrom'), // "请输入开始营业日期"
              validator: validateValue,
              trigger: 'change'
            }
          ],
          businessEndDate: [
            {
              message: this.$t('vendorMod.msgBusinessEndAt'), // "请输入结束营业日期"
              validator: validateValue,
              trigger: 'change'
            }
          ],
          lcCode: [
            {
              message: this.$t('vendorMod.msgLcCode'), // "请输入统一社会信用代码"
              validator: validateValue,
              trigger: 'change'
            }
          ],
          companyCountry: [
            {
              message: this.$t('vendorMod.msgBusinessAddr'), // "请输入注册国家"
              validator: validateValue,
              trigger: 'change'
            }
          ],
          companyProvince: [
            {
              message: this.$t('vendorMod.msgpProvince'), // "请输入省"
              validator: validateValue,
              trigger: 'change'
            }
          ],
          companyCity: [
            {
              message: this.$t('vendorMod.msgCity'), // "请输入市"
              validator: validateValue,
              trigger: 'change'
            }
          ],
          companyAddress: [
            {
              message: this.$t('vendorMod.msgDetailAddr'), // "请输入详细地址"
              validator: validateValue,
              trigger: 'change'
            }
          ],
          registrationAuthority: [
            {
              message: this.$t('vendorMod.msgRegistrationAuthority'), // "请输入登记机关"
              validator: validateValue,
              trigger: 'change'
            }
          ],
          companyShortName: [{ required: false, validator: validateValue, trigger: 'change' }], // 公司简称
          dunsCode: [{ required: false, validator: validateValue, trigger: 'change' }], // dunsCode(境外)
          categoryRelChanges: [{ required: true, validator: validateValue, trigger: 'change' }]
        }
      },
      ouptCup: '', // 兼容页面禁用
      baseInfoDimFieldContexts: [], // 基础信息 拓展字段
      baseDimRules: {}, // 基础信息拓展字段规则
      financeDimFieldContexts: [], // 财务信息拓展字段
      bankDimFieldContexts: [], // 银行信息拓展字段
      contactDimFieldContexts: [], // 联系人拓展字段
      orgDimFieldContexts: [], // 合作组织拓展字段
      orgCatDimFieldContexts: [], // 组织和品类拓展字段
      baseDimModel: {}, // 基础信息拓展字段
      baseDimModelChange: {}, // 基础信息拓展字段 变更后
      otherDimFieldContexts: [], // 其他信息拓展字段
      otherDimModel: {},
      financeInfoData: [], // 财务信息
      financeInfoDataChange: [], // 财务信息变更后
      bankData: [], // 银行信息
      bankDataChange: [], // 银行信息变更后
      sceneAttachmentData: [], // 认证信息
      sceneAttachmentDataChange: [], // 认证信息更新后
      contactData: [], // 联系人
      contactDataChange: [], // 联系人变更后
      siteInfoChanges: [], // 供应商地点信息
      siteInfoChangesChange: [], // 供应商地点信息变更后
      siteInfoChangesSelect: [], // 删除的供应商地点信息
      orgInfoData: [], // 合作组织
      otherModel: {
        // 其他信息
        otherForm: {
          bizModel: '', // 商业模式
          floorArea: '', // 建筑面积
          factoryType: '', // 厂房性质
          employeeQty: '', // 员工规模
          companySite: '', // 公司网站
          floorSpace: '', // 占地面积
          dimFieldContexts: {}
        }
      },
      otherInfoRules: {
        bizModel: [{ required: false, validator: validateOtherValue, trigger: 'change' }],
        floorArea: [{ required: false, validator: validateOtherValue, trigger: 'change' }],
        factoryType: [{ required: false, validator: validateOtherValue, trigger: 'change' }],
        employeeQty: [{ required: false, validator: validateOtherValue, trigger: 'change' }],
        companySite: [{ required: false, validator: validateOtherValue, trigger: 'change' }],
        floorSpace: [{ required: false, validator: validateOtherValue, trigger: 'change' }]
      },
      otherDimRules: {}, // 其他信息拓展字段规则
      attachTableData: [], // 附件
      fieldDimConf: [], // 字段维度配置信息
      curRole: this.$store.getters.userType, // 用户类型 BUYER || VENDOR
      peopleDialog: false, // 人员选择器
      resultFlow: false,
      resultFlowParams: {}, // 结果审批流程参数
      openWorkFlow: false, // 审批流程相关参数
      activeCollapseOperatingLogs: ['1'],
      operatingLogsData: []
    }
  },
  computed: {
    viewUpdateButton () { // 用来控制保存、提交按钮是否可见。如果自定义按钮则无需添加
      let bol
      if (this.infoChangeModel.infoChange.changeStatus === 'APPROVED' ||
        this.infoChangeModel.infoChange.changeStatus === 'SUBMITTED' ||
        this.infoChangeModel.infoChange.changeStatus === 'ABANDONED') {
        bol = false
      } else if (this.curOpt === 'view') {
        bol = false
      } else {
        bol = true
      }
      return bol
    },
    workflowBusinessId () { // 用来指定工作流的业务ID
      return this.changeId ? this.changeId : null
    },
    // 展示工作流tab页
    workflowTabDisabled () {
      let bol
      if (this.infoChangeModel.infoChange.changeStatus === 'APPROVED' ||
        this.infoChangeModel.infoChange.changeStatus === 'SUBMITTED' ||
        this.infoChangeModel.infoChange.changeStatus === 'REJECTED' ||
        this.infoChangeModel.infoChange.changeStatus === 'ABANDONED') {
        bol = false
      } else {
        bol = true
      }
      return bol
    },
    changeDim () {
      let dimObj = {}
      if (this.fieldDimConf.length > 0) {
        if (this.curRole === 'BUYER') {
          this.fieldDimConf.map((item) => {
            if (item.isBuyer === 'Y') {
              dimObj[item.dimCode] = item.isBuyer
            }
          })
        } else if (this.curRole === 'VENDOR') {
          this.fieldDimConf.map((item) => {
            if (item.isSupply === 'Y') {
              dimObj[item.dimCode] = item.isSupply
            }
          })
        }
      }
      return dimObj
    },
    // 基础信息拓展字段
    validateBaseDimValue () {
      let _this = this
      return function (rule, value, callback) {
        let oldDataObj = _this.baseInfoDimOldData
        let curKey = rule.field // 当前字段名称
        if (rule.required && !value) {
          callback(new Error(rule.message))
        } else if (value) {
          // 有值
          if (Object.keys(oldDataObj).length) {
            if (value !== oldDataObj[curKey]) {
              try {
                _this.$refs.companyInfo.$el.querySelectorAll(
                  `label[for="${curKey}"]`
                )[0].nextElementSibling.className = 'el-form-item__content changeStyle'
              } catch (e) {
                console.log(e)
              }
            }
          } else {
            try {
              _this.$refs.companyInfo.$el.querySelectorAll(
                `label[for="${curKey}"]`
              )[0].nextElementSibling.className = 'el-form-item__content changeStyle'
            } catch (e) {
              console.log(e)
            }
          }
          callback()
        } else {
          try {
            _this.$refs.companyInfo.$el.querySelectorAll(
              `label[for="${curKey}"]`
            )[0].nextElementSibling.className = 'el-form-item__content'
          } catch (e) {
            console.log(e)
          }

          callback()
        }
      }
    },
    // 其他信息拓展字段
    validateOtherDimValue () {
      let _this = this
      return function (rule, value, callback) {
        let oldDataObj = _this.otherInfoDimOldData
        let curKey = rule.field // 当前字段名称
        if (rule.required && !value) {
          callback(new Error(rule.message))
        } else if (value) {
          if (Object.keys(oldDataObj).length) {
            if (value !== oldDataObj[curKey]) {
              _this.$refs.otherInfo.$el.querySelectorAll(
                `label[for="${curKey}"]`
              )[0].nextElementSibling.className = 'el-form-item__content changeStyle'
            }
          } else {
            _this.$refs.otherInfo.$el.querySelectorAll(
              `label[for="${curKey}"]`
            )[0].nextElementSibling.className = 'el-form-item__content changeStyle'
          }
          callback()
        } else {
          _this.$refs.otherInfo.$el.querySelectorAll(
            `label[for="${curKey}"]`
          )[0].nextElementSibling.className = 'el-form-item__content'
          callback()
        }
      }
    },
    // 采购商 待采购商确认 状态显示的按钮
    vendorSubmittedButtonVisible () {
      return this.infoChangeModel.infoChange.changeStatus === 'VENDOR_SUBMITTED' &&
        !this.viewUpdateButton &&
        this.curRole === 'BUYER'
    }
  },
  watch: {
    // 监听保存提交 按钮变更状态，如果自定义按钮则无需添加
    viewUpdateButton () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
    }
  },
  created () {
    this.curOpt = this.$attrs.params.flag
    if (this.$attrs.params.ouptCup) {
      this.ouptCup = this.$attrs.params.ouptCup
    }

    this.companyId = this.$store.getters.companyId
    this.fatchDictData() // 字典
    this.getFieldDimConf() // 查询维度配置信息

    this.disabledBol = this.curOpt === 'view'

    if (this.$attrs.params.flag === 'add') {
      // 新增
      if (this.curRole === 'VENDOR') {
        // 供应商查询自己的信息
        this.companyId = this.$store.getters.companyId
        this.fatchCompanyData() // 查询公司信息
      }
    } else {
      // edit view doApproval 编辑 查看 审批
      this.changeId = this.$attrs.params.changeId
      this.companyId = this.$attrs.params.companyId
      this.fatchCompanyDataOld() // 查询供应商档案数据 和 变更数据
    }

    // 获取动态配置(复制粘贴即可)
    this.getConfig()
    modelConfigApi.getModelConfig('companyInfoMaintain').then(result => {
      this.getModelConfig = result
    })
    modelConfigApi.getDimDataById(this.changeId).then(result => {
      this.dimDataValue = result.data
    })

    // 工作流按钮显示
    this.buttonConfigInfo.save.view = this.viewUpdateButton
    this.buttonConfigInfo.submit.view = this.viewUpdateButton
    this.buttonConfigInfo.cancel.view = false
    this.buttonConfigInfo.close.view = false
    this.buttonConfigInfo.save.name = '暂存'
    this.buttonConfigInfo.submit.name = '提交'

    this.$nextTick(() => {
      if (this.$attrs.params.flag === 'doApproval') {
        this.activeTabName = 'workflowTab'
      }
    })
  },
  mounted () {
    this.isMounted = true
  },
  provide () {
    return {
      addFinance: this.addFinance,
      addContactData: this.addContactData,
      contactRowHandel: this.contactRowHandel,
      contactDel: this.contactDel,
      addBank: this.addBank,
      getBankObj: this.getBankObj,
      bankRowHandel: this.bankRowHandel,
      bankRowDelHandel: this.bankRowDelHandel,
      addSiteInfo: this.addSiteInfo,
      selectHandler: this.selectHandler,
      getCountry: this.getCountry,
      provinceChangeHandle2: this.provinceChangeHandle2,
      setRowAmount: this.setRowAmount,
      vendorDel: this.vendorDel,
      handleSizeChange: this.handleSizeChange,
      handleCurrentChange: this.handleCurrentChange,
      addSceneAttachment: this.addSceneAttachment,
      innerHandleUploadSuccess: this.innerHandleUploadSuccess,
      innerButtonClick: this.innerButtonClick,
      handleAttachmentRemove1: this.handleAttachmentRemove1,
      setFormatValue: this.setFormatValue,
      sceneAttachmentHandel: this.sceneAttachmentHandel,
      relevantRowDelHandel: this.relevantRowDelHandel
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
              if (!item.relateDimCode && item.originalDimFlag === 'N') {
                this.nodeData.splice(-2, 0, obj)
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
    back () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('vendorInfoChangeList.getQuerydata')
    },
    countryChange () {
      if (this.baseInfoModelChange.baseInfoForm.companyCountry !== 'CN') {
        this.baseInfoModelChange.baseInfoForm.companyCity = ''
        this.baseInfoModelChange.baseInfoForm.companyProvince = ''
      }
    },
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'SUPPLIERINFOCHANGE'
    },
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    relevantRowDelHandel (rowIndex, row) {
      this.sceneAttachmentDataChange.splice(rowIndex, 1)
    },
    // 维度间的级联
    selectChanges (val) {
      let selectChangeData = this.selectChangeData
      for (let key in selectChangeData) {
        for (let key2 in val) {
          if (key === key2) {
            this.$set(this.selectChangeData, key, val[key2])
            console.log('1', this.selectChangeData)
            return
          }
        }
      }
      // this.$set()
      this.selectChangeData = Object.assign({}, selectChangeData, val)
      console.log('2', this.selectChangeData)
    },
    evalF (val) {
      try {
        if (val !== '' && val !== null) {
          // eslint-disable-next-line no-eval
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
    // 扩展表单点击新增
    addOneTableData (indexs) {
      this.tableExtendList[indexs].push({})
    },
    // 新增时循环输出维度扩展
    dimensionExtension (data, res, bol) {
      // 如果BOL为1的时候是变更过的，使用dimFieldResultChangeList
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

      let Emodel = []
      let Emodel2 = [] // 表单

      if (bol == '1') {
        res.data.dimFieldResultChangeList.forEach((datas) => {
          let obj = {}
          let obj2 = {
            dimFieldNum: datas.dimFieldNum
          } // 表单
          obj[datas.fieldCode] = datas.fieldValue
          obj2[datas.fieldCode] = datas.fieldValue
          Emodel.push(obj)
          Emodel2.push(obj2)
        })
      } else {
        res.data.dimFieldResultList.forEach((datas) => {
          let obj = {}
          let obj2 = {
            dimFieldNum: datas.dimFieldNum
          } // 表单
          obj[datas.fieldCode] = datas.fieldValue
          obj2[datas.fieldCode] = datas.fieldValue
          Emodel.push(obj)
          Emodel2.push(obj2)
        })
      }

      this.Emodel = Emodel
      this.Emodel2 = Emodel2
      console.log(Emodel2)
    },
    // 获取数据字典
    fatchDictData () {
      // 加载省
      getRegion({ queryType: 'province' }).then((res) => {
        if (res.data) {
          this.provinceList = this.adaptProvinceCity(res.data, 'province')
        }
      })
    },
    // 查询维度的配置信息
    getFieldDimConf () {
      vendorAttributeComApi.getFieldDim().then((res) => {
        this.fieldDimConf = res.data
      })
    },
    // 选择供应商回调
    getCompanyObj (val, data) {
      data.companyId = val ? val.companyId : null
      data.companyCode = val ? val.companyCode : ''
      data.companyName = val ? val.companyName : ''
      this.companyId = val ? val.companyId : null
      this.baseInfoModelChange.baseInfoForm.companyCode = val ? val.companyCode : ''
      if (this.companyId) {
        this.fatchCompanyData() // 查询公司信息
      }
    },
    // 新增通过公司ID查询公司信息
    fatchCompanyData () {
      let companyId = this.companyId
      if (companyId) {
        vendorOptCommonApi.getCompanyForEdit({ companyId }).then((res) => {
          if (res) {
            let beforeChangeJsonVariable = res.data // 获取变更前的值，后续保存在this.beforeChangeJson
            modelConfigApi.getDimDataById(companyId).then(result => { // 模板配置获取原来供应商的值
              this.dimDataValue = result.data
              this.beforeDimDataValue = result.data
              beforeChangeJsonVariable.extend = result.data // 变更前模板配置数据保存在beforeChangeJson.extend
              this.beforeChangeJson = JSON.stringify(beforeChangeJsonVariable)
            }).catch(err => {
              this.beforeChangeJson = JSON.stringify(beforeChangeJsonVariable)
            })
            if (res.data.infoChange) {
              this.infoChangeModel.infoChange.noticeByName = res.data.infoChange.noticeByName
              this.infoChangeModel.infoChange.noticeById = res.data.infoChange.noticeById
            }
            if (res.data.fileUploads) {
              for (let i = 0; i < res.data.fileUploads.length; i++) {
                const fileItem = res.data.fileUploads[i]
                fileItem.originalBusinessId = fileItem.businessId
                fileItem.sceneFileId = null
                fileItem.businessId = null
                fileItem[EDITABLE_KEY] = true
                fileItem[ADD_KEY] = true
                fileItem[UPDATE_KEY] = true
              }
              this.companyInfoFileList = res.data.fileUploads
              this.companyInfoFileListChange = JSON.parse(JSON.stringify(this.companyInfoFileList))
            }
            this.$nextTick(() => {
              this.$refs.sceneAttachment.loadFileInfo()
              this.$refs.sceneAttachment2.loadFileInfo()
            })
            if (res.data.companyInfo) {
              let companyInfo = res.data.companyInfo
              this.curRel = companyInfo.overseasRelation
              this.curType = companyInfo.companyType
              // // 对比旧数据赋值区域[[[ 基础信息
              this.baseInfoOldData = companyInfo
              this.baseInfoOldData.companyCreationDate = companyInfo.companyCreationDate
                ? this.$dayjs(companyInfo.companyCreationDate).valueOf()
                : ''
              this.baseInfoOldData.businessStartDate = companyInfo.businessStartDate
                ? this.$dayjs(companyInfo.businessStartDate).valueOf()
                : '' // 开始时间
              this.baseInfoOldData.businessEndDate = companyInfo.businessEndDate
                ? this.$dayjs(companyInfo.businessEndDate).valueOf()
                : ''
              this.baseInfoDimOldData = companyInfo.dimFieldContexts // 拓展字段
              this.categoryRelsOldData = companyInfo.categoryRels
              // ]]]
              // 查询模板配置信息
              let pareme = {}
              pareme.overseasRelation = companyInfo.overseasRelation
              pareme.companyType = companyInfo.companyType
              if (pareme.overseasRelation === 'OUT') {
                pareme.companyType = ''
              }
              vendorOptCommonApi.getConfigByTemplate(pareme).then((res2) => {
                if (res2) {
                  this.adaptDimFieldsHandle(res2.data) // 适配拓展字段
                  this.dimensionExtension(res2.data, res)
                }
              })

              // 加载市
              if (companyInfo.companyProvince) {
                let companyProvince = companyInfo.companyProvince
                this.getRegionData(companyProvince) // 加载市
              }

              this.baseInfoModel.baseInfoForm = _omit(companyInfo, [
                'applicationDate',
                'creationDate',
                'lastUpdateDate',
                'categoryRels'
              ])
              this.baseInfoModelChange.baseInfoForm = _omit(companyInfo, [
                'applicationDate',
                'creationDate',
                'lastUpdateDate',
                'categoryRels'
              ])
              this.baseInfoModel.baseInfoForm.companyCreationDate = companyInfo.companyCreationDate
                ? this.$dayjs(companyInfo.companyCreationDate).valueOf()
                : ''
              this.baseInfoModelChange.baseInfoForm.companyCreationDate = companyInfo.companyCreationDate
                ? this.$dayjs(companyInfo.companyCreationDate).valueOf()
                : ''
              this.baseInfoModel.baseInfoForm.businessStartDate = companyInfo.businessStartDate
                ? this.$dayjs(companyInfo.businessStartDate).valueOf()
                : '' // 开始时间
              this.baseInfoModelChange.baseInfoForm.businessStartDate = companyInfo.businessStartDate
                ? this.$dayjs(companyInfo.businessStartDate).valueOf()
                : '' // 开始时间
              this.baseInfoModel.baseInfoForm.businessEndDate = companyInfo.businessEndDate
                ? this.$dayjs(companyInfo.businessEndDate).valueOf()
                : ''
              this.baseInfoModelChange.baseInfoForm.businessEndDate = companyInfo.businessEndDate
                ? this.$dayjs(companyInfo.businessEndDate).valueOf()
                : ''
              this.baseInfoModel.baseInfoForm.categoryRelChanges = companyInfo.categoryRels // 可供品类
              this.baseInfoModelChange.baseInfoForm.categoryRelChanges = companyInfo.categoryRels // 可供品类
              this.baseDimModel = companyInfo.dimFieldContexts || {} // 拓展字段值
              this.baseDimModelChange = JSON.parse(JSON.stringify(this.baseDimModel))
            }

            if (res.data.otherInfo) {
              let otherInfo = res.data.otherInfo
              // 对比旧数据赋值区域[[[ 其他信息
              this.otherInfoOldData = otherInfo
              this.otherInfoDimOldData = otherInfo.dimFieldContexts // 拓展字段
              // ]]]]

              this.otherModel.otherForm.companyId = otherInfo.companyId
              this.otherModel.otherForm.otherInfoId = otherInfo.otherInfoId
              this.otherModel.otherForm.bizModel = otherInfo.bizModel
              this.otherModel.otherForm.floorArea = otherInfo.floorArea
              this.otherModel.otherForm.factoryType = otherInfo.factoryType
              this.otherModel.otherForm.employeeQty = otherInfo.employeeQty
              this.otherModel.otherForm.companySite = otherInfo.companySite
              this.otherModel.otherForm.floorSpace = otherInfo.floorSpace
              this.otherDimModel = otherInfo.dimFieldContexts // 拓展字段值
            }

            this.siteInfoChanges = res.data.siteInfos
            this.siteInfoChangesChange = JSON.parse(JSON.stringify(this.siteInfoChanges))
            this.bankData = res.data.bankInfos
              ? this.adaptResutData(res.data.bankInfos) || []
              : []
            this.bankDataChange = JSON.parse(JSON.stringify(this.bankData))
            this.sceneAttachmentData = res.data.managementAttaches
              ? this.adaptResutData(res.data.managementAttaches) || []
              : []
            this.sceneAttachmentDataChange = JSON.parse(JSON.stringify(this.sceneAttachmentData))
            // debugger
            this.contactData = res.data.contactInfos
              ? this.adaptResutData(res.data.contactInfos) || []
              : []
            this.contactDataChange = JSON.parse(JSON.stringify(this.contactData))
            this.financeInfoData = res.data.financeInfos
              ? this.adaptResutData(res.data.financeInfos) || []
              : []
            this.financeInfoDataChange = JSON.parse(JSON.stringify(this.financeInfoData))
            this.orgInfoData = res.data.orgInfos ? this.adaptResutData(res.data.orgInfos) || [] : []
          }
          // debugger
        })
      } else {
        this.$nextTick(() => {
          this.$refs.sceneAttachment.loadFileInfo()
          this.$refs.sceneAttachment2.loadFileInfo()
        })
      }
    },
    // 查询公司档案数据(基础数据)
    async fatchCompanyDataOld () {
      let companyId = this.companyId
      if (companyId) {
        const { data } = await vendorOptCommonApi.getCompanyForEdit({ companyId })
        if (data.companyInfo) {
          let companyInfo = data.companyInfo
          this.curRel = companyInfo.overseasRelation
          this.curType = companyInfo.companyType
          // 对比旧数据赋值区域[[[ 基础信息
          this.baseInfoOldData = companyInfo
          this.baseInfoOldData.companyCreationDate = companyInfo.companyCreationDate
            ? this.$dayjs(companyInfo.companyCreationDate).valueOf()
            : ''
          this.baseInfoOldData.businessStartDate = companyInfo.businessStartDate
            ? this.$dayjs(companyInfo.businessStartDate).valueOf()
            : '' // 开始时间
          this.baseInfoOldData.businessEndDate = companyInfo.businessEndDate
            ? this.$dayjs(companyInfo.businessEndDate).valueOf()
            : ''
          this.baseInfoDimOldData = companyInfo.dimFieldContexts // 拓展字段
          this.categoryRelsOldData = companyInfo.categoryRels

          if (data.fileUploads) {
            for (let i = 0; i < data.fileUploads.length; i++) {
              const fileItem = data.fileUploads[i]
              fileItem.originalBusinessId = fileItem.businessId
              fileItem.sceneFileId = null
              fileItem.businessId = null
              fileItem[EDITABLE_KEY] = true
              fileItem[ADD_KEY] = true
              fileItem[UPDATE_KEY] = true
            }
            this.companyInfoFileList = data.fileUploads
            this.companyInfoFileListChange = JSON.parse(JSON.stringify(this.companyInfoFileList))
          }
          this.$nextTick(() => {
            this.$refs.sceneAttachment.loadFileInfo()
            this.$refs.sceneAttachment2.loadFileInfo()
          })
          // ]]]
        }
        this.fatchOldData() // 查询变更旧数据
      } else {
        this.fatchOldData() // 查询变更旧数据
        this.$nextTick(() => {
          this.$refs.sceneAttachment.loadFileInfo()
          this.$refs.sceneAttachment2.loadFileInfo()
        })
      }
    },
    // 适配省 市
    adaptProvinceCity (data, type) {
      let arr = []
      if (data && data.length > 0) {
        if (type === 'province') {
          // 省
          data.forEach((element) => {
            arr.push({
              id: element.provinceId,
              value: element.provinceId.toString(),
              label: element.province
            })
          })
        } else if (type === 'city') {
          // 市
          data.forEach((element) => {
            arr.push({
              id: element.cityId,
              value: element.cityId.toString(),
              label: element.city
            })
          })
        }
      }
      return arr
    },
    // 加载省
    getRegionData (val) {
      let parame = { queryType: 'city', parentId: val }
      getRegion(parame).then((res) => {
        if (res.data) {
          this.cityList = this.adaptProvinceCity(res.data, 'city')
        }
      })
    },
    // 省下拉加载市
    provinceChangeHandle (val) {
      this.baseInfoModelChange.baseInfoForm.companyCity = ''
      this.getRegionData(val)
    },
    // 省下拉加载市
    provinceChangeHandle2 (row) {
      this.getRegionData(row.province)
    },
    setRowAmount (row) {
      if (row.postCode.length > 6) {
        return this.$message.error(this.$t('components.bank.msgPostalError')) // 邮政编码不应超过6位！
      }
    },
    overseasChangeHandle (val) {
      this.curRel = val // 当前海内外关系
      if (val !== 'OUT') {
        // 境外
        this.baseInfoModelChange.baseInfoForm.companyCountry = 'CN'
      }
      this.getDimAttrConfig() // 属性配置信息
    },
    companyTypeChangeHandle (val) {
      this.curType = val // 当前公司属性
      this.getDimAttrConfig() // 属性配置信息
    },
    // 获取属性拓展字段
    getDimAttrConfig () {
      let parame = {}
      parame.overseasRelation = this.baseInfoModel.baseInfoForm.overseasRelation
      parame.companyType = this.baseInfoModel.baseInfoForm.companyType
      if (parame.overseasRelation === 'OUT') {
        parame.companyType = ''
      }
      vendorOptCommonApi.getConfigByTemplate(parame).then((res) => {
        if (res && res.data) {
          this.adaptDimFieldsHandle(res.data) // 适配拓展字段
          this.dimensionExtension(res.data)
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
            if (element.dimFieldConfigS.length > 0) {
              element.dimFieldConfigS.map((item) => {
                this.baseDimRules[item.fieldCode] = [
                  {
                    required: item.isCheck === 'Y',
                    message: this.$t('common.pleaseInput') + item.fieldName,
                    validator: this.validateBaseDimValue,
                    trigger: 'change'
                  }
                ]
              })
            }
          } else if (element.dimCode === 'bankInfo') {
            bankInfo = element.dimFieldConfigS
          } else if (element.dimCode === 'linkMan') {
            contactInfo = element.dimFieldConfigS
          } else if (element.dimCode === 'otherInfo') {
            otherInfo = element.dimFieldConfigS
            if (element.dimFieldConfigS.length > 0) {
              element.dimFieldConfigS.map((item) => {
                this.otherDimRules[item.fieldCode] = [
                  {
                    required: false,
                    message: this.$t('common.pleaseInput') + item.fieldName,
                    validator: this.validateOtherDimValue,
                    trigger: 'change'
                  }
                ]
              })
            }
          } else if (element.dimCode === 'orgInfo') {
            orgInfo = element.dimFieldConfigS
          } else if (element.dimCode === 'financeInfo') {
            financeInfo = element.dimFieldConfigS
          } else if (element.dimCode === 'orgCategory') {
            orgCategory = element.dimFieldConfigS
          }
        })
      }

      if (companyInfo.length !== 0) {
        this.baseInfoDimFieldContexts = companyInfo
      }
      this.financeDimFieldContexts = financeInfo
      this.bankDimFieldContexts = bankInfo
      this.contactDimFieldContexts = contactInfo
      this.orgDimFieldContexts = orgInfo
      this.orgCatDimFieldContexts = orgCategory
      this.otherDimFieldContexts = otherInfo
    },
    // 分页供应商
    handleSizeChange (val) {
      console.log(`每页 ${val} 条`)
      this.siteInfoChangesSelect = []
      this.currentPage = 1
      this.pageSize_approvalBiddingItemLis = val
    },
    handleCurrentChange (val) {
      console.log(`当前页: ${val}`)
      this.siteInfoChangesSelect = []
      this.currentPage = val
    },
    // 信息变更附件上传功能
    changeFileUploadSuccess (file) {
      if (file) {
        const { fileId, fileName } = file
        this.infoChangeModel.infoChange.changeFileId = fileId.toString()
        this.infoChangeModel.infoChange.changeFileName = fileName
      } else {
        this.infoChangeModel.infoChange.changeFileId = ''
        this.infoChangeModel.infoChange.changeFileName = ''
      }
    },
    // 基础信息form 上传附件成功
    handleUploadSuccess (file) {
      const { fileId, fileName } = file || {}
      this.baseInfoModelChange.baseInfoForm.businessLicenseFileId = fileId.toString()
      this.baseInfoModelChange.baseInfoForm.businessLicense = fileName
    },
    // 删除文件
    handleAttachmentRemove () {
      this.baseInfoModelChange.baseInfoForm.businessLicenseFileId = ''
      this.baseInfoModelChange.baseInfoForm.businessLicense = ''
    },
    // 变更附件
    changeAttachmentRemove () {
      this.infoChangeModel.infoChange.changeFileId = ''
      this.infoChangeModel.infoChange.changeFileName = ''
    },
    // 证明文件
    handleAttachmentRemove1 (row) {
      row.fileuploadId = ''
      row.authType = ''
    },
    // 银行文件上传】】
    // 附件上传【【
    // table文件上传
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

    // 文件上传========]
    // 附件上传】】
    // 选择品类
    catButtonClick (index) {
      this.catRowIndex = index
    },
    getUserObj (val, scope) {
      scope.noticeById = val ? val.userId : ''
      scope.noticeByName = val ? val.nickname : ''
    },
    // 确认选择品类
    comfirmSelect (node, scope) {
      scope.categoryId = node ? node.categoryId : null
      scope.categoryName = node ? node.categoryName : ''
      scope.categoryCode = node ? node.categoryCode : ''
    },
    // 删除行操作1
    bankRowDelHandel (index, row) {
      if (row.bankChangeId) {
        this.$http({
          url: '/api-sup/change/infoChange/deleteBankChange',
          method: 'GET',
          params: { bankChangeId: row.bankChangeId },
          loading: true
        }).then((res) => {
          this.bankDataChange.splice(index, 1)
        })
      } else {
        this.bankDataChange.splice(index, 1)
      }
    },
    bankRowHandel (index, row, type) {
      let bankInfoId = row.bankInfoId
      if (type === 'delete') {
        // 删除
        if (bankInfoId) {
          if (row.opType === 'add') {
            this.bankDataChange.splice(index, 1)
          } else {
            this.bankDataChange[index].opType = 'delete'
          }
        } else {
          if (row.opType === 'add') {
            this.bankDataChange.splice(index, 1)
          }
        }
      } else if (type === 'update') {
        // 编辑
        this.bankDataChange[index].opType = 'update'
      } else if (type === 'cancel') {
        // 取消
        this.bankDataChange[index].opType = ''
      }
    },
    sceneAttachmentHandel (index, row, type) {
      let managementInfoId = row.managementInfoId
      if (type === 'delete') {
        // 删除
        if (managementInfoId) {
          if (row.opType === 'add') {
            this.sceneAttachmentDataChange.splice(index, 1)
          } else {
            this.sceneAttachmentDataChange[index].opType = 'delete'
          }
        } else {
          if (row.opType === 'add') {
            this.sceneAttachmentDataChange.splice(index, 1)
          }
        }
      } else if (type === 'update') {
        // 编辑
        this.sceneAttachmentDataChange[index].opType = 'update'
      } else if (type === 'cancel') {
        // 取消
        this.sceneAttachmentDataChange[index].opType = ''
      }
    },
    // 联系人信息行操作
    contactRowHandel (index, row, type) {
      let contactInfoId = row.contactInfoId
      if (type === 'delete') {
        // 删除
        if (contactInfoId) {
          this.contactDataChange[index].opType = 'delete'
        } else {
          this.contactDataChange.splice(index, 1)
        }
      } else if (type === 'update') {
        // 编辑
        this.contactDataChange[index].opType = 'update'
      } else if (type === 'cancel') {
        // 取消
        this.contactDataChange[index].opType = ''
      }
    },
    // 合作组织信息行操作
    orgInfoRowHandel (index, row, type) {
      let orgInfoId = row.orgInfoId
      if (type === 'delete') {
        // 删除
        if (orgInfoId) {
          this.orgInfoData[index].opType = 'delete'
        } else {
          this.orgInfoData.splice(index, 1)
        }
      } else if (type === 'update') {
        // 编辑
        this.orgInfoData[index].opType = 'update'
      } else if (type === 'cancel') {
        // 取消
        this.orgInfoData[index].opType = ''
      }
    },
    // 附件信息行操作
    attRowHandel (index, row, type) {
      let categoryId = row.categoryId
      if (type === 'delete') {
        // 删除
        if (categoryId) {
          this.attachTableData[index].opType = 'delete'
        } else {
          this.attachTableData.splice(index, 1)
        }
      } else if (type === 'update') {
        // 编辑
        this.attachTableData[index].opType = 'update'
      } else if (type === 'cancel') {
        // 取消
        this.attachTableData[index].opType = ''
      }
    },
    // 行删除
    handleDelClick (index, row) {
      let attachFileId = row.attachFileId
      if (attachFileId) {
        vendorChangeApi.deleteAttachById({ attachFileId }).then((res) => {
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
        }).then((res) => {
          if (res.data) {
            scope.belongOprId = res.data.erpOrgId
          }
        })
      }
    },
    getCountry (row) {
      // 选择国外就清理省市区，并且禁用
      if (row.country !== 'CN') {
        row.province = null
        row.city = null
      }
    },
    // 加载所有的城市
    getAllRegionData () {
      this.$http({
        url: '/api-base/region/queryRegionByParam',
        method: 'POST',
        params: { queryType: 'city' },
        loading: true
      }).then((res) => {
        if (res.data) {
          this.cityList = this.adaptProvinceCity(res.data, 'city')
        }
      })
    },
    addSiteInfo () {
      this.siteInfoChangesChange.unshift({
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
        orgId: null,
        orgCode: null,
        orgName: null,
        vendorSiteId: null,
        vendorSiteCode: null,
        disableDate: null,
        postCode: null,
        siteComment: null
      })
    },
    handleDelClickSite (index, row) {
      if (row.siteChangeId) {
        this.$http({
          url: '/api-sup/change/infoChange/deleteSiteChange',
          method: 'GET',
          params: { siteChangeId: row.siteChangeId },
          loading: true
        }).then((res) => {
          this.siteInfoChangesChange.splice(index, 1)
        })
      } else {
        this.siteInfoChangesChange.splice(index, 1)
      }
    },
    contactDel (index, row, type) {
      // 删除联系人---contactData
      if (row.contactChangeId) {
        this.$http({
          url: '/api-sup/change/infoChange/deleteContactChange',
          method: 'GET',
          params: { contactChangeId: row.contactChangeId },
          loading: true
        }).then((res) => {
          this.contactDataChange.splice(index, 1)
          this.contactDataChange[index].opType = 'delete'
        })
      } else {
        this.contactDataChange.splice(index, 1)
        // this.contactDataChange[index].opType = 'delete'
      }
    },
    vendorDel (index, row) {
      row.opType = 'Y'
      this.siteInfoChangesSelect.push(row)
      this.siteInfoChangesChange.splice(index, 1)
    },
    orgDel (index, row) {
      // 删除合作组织
      let orgInfoId = row.orgInfoId
      if (orgInfoId) {
        this.orgInfoData[index].opType = 'delete'
      } else {
        this.orgInfoData.splice(index, 1)
      }
    },
    // 查询旧数据
    fatchOldData () {
      let changeId = this.changeId
      if (changeId) {
        vendorChangeApi.getInfoByChangeId({ changeId }).then((res) => {
          console.log(res.data.infoChange)
          if (res) {
            if (res.data.infoChange) {
              const {
                noticeById,
                noticeByName,
                changeId,
                changeApplyNo,
                companyId,
                companyName,
                changeFileId,
                changeFileName,
                changeStatus,
                changeType,
                changeExplain,
                enable4MChange
              } = res.data.infoChange

              this.infoChangeModel.infoChange.changeId = changeId // 变更Id
              this.infoChangeModel.infoChange.changeApplyNo = changeApplyNo // 变更单号
              this.infoChangeModel.infoChange.companyId = companyId // 公司Id
              this.infoChangeModel.infoChange.companyName = companyName // 公司名称
              this.infoChangeModel.infoChange.changeFileId = changeFileId // 变更附件Id
              this.infoChangeModel.infoChange.changeFileName = changeFileName // 变更附件名称
              this.infoChangeModel.infoChange.changeStatus = changeStatus // 变更状态
              this.infoChangeModel.infoChange.changeType = changeType // 变更类型
              this.infoChangeModel.infoChange.changeExplain = changeExplain // 变更说明
              this.infoChangeModel.infoChange.noticeById = noticeById
              this.infoChangeModel.infoChange.noticeByName = noticeByName
              this.infoChangeModel.infoChange.enable4MChange = enable4MChange
              this.companyId = companyId
            }
            if (res.data.infoChange.beforeChangeJson) {
              this.beforeChangeJson = res.data.infoChange.beforeChangeJson
              this.siteInfoChanges = JSON.parse(res.data.infoChange.beforeChangeJson).siteInfoChanges
              this.siteInfoChangesChange = res.data.siteInfoChanges
            } else {
              this.siteInfoChanges = res.data.siteInfoChanges
              this.siteInfoChangesChange = JSON.parse(JSON.stringify(this.siteInfoChanges))
            }
            if (res.data.siteInfoChanges && res.data.siteInfoChanges.length != 0) {
              // 省下拉加载市
              this.getAllRegionData()
            }
            if (res.data.companyInfoChange) {
              let companyInfoNoChange
              let companyInfoChange
              if (res.data.infoChange.beforeChangeJson) {
                if (JSON.parse(res.data.infoChange.beforeChangeJson).companyInfo) {
                  companyInfoNoChange = JSON.parse(res.data.infoChange.beforeChangeJson).companyInfo
                } else {
                  companyInfoNoChange = JSON.parse(res.data.infoChange.beforeChangeJson).companyInfoChange
                }
                companyInfoChange = res.data.companyInfoChange
              } else {
                companyInfoNoChange = res.data.companyInfoChange
                companyInfoChange = JSON.parse(JSON.stringify(companyInfoNoChange))
              }

              this.curRel = companyInfoChange.overseasRelation
              this.curType = companyInfoChange.companyType
              this.infoChangeModel.infoChange.companyName = companyInfoChange.companyName
              // 查询模板配置信息
              let pareme = {}
              pareme.overseasRelation = companyInfoChange.overseasRelation
              pareme.companyType = companyInfoChange.companyType
              if (pareme.overseasRelation === 'OUT') {
                pareme.companyType = ''
              }
              vendorOptCommonApi.getConfigByTemplate(pareme).then((res2) => {
                if (res2) {
                  this.adaptDimFieldsHandle(res2.data) // 适配拓展字段
                  this.dimensionExtension(res2.data, res, '1')
                }
              })

              // 加载市
              if (companyInfoChange.companyProvince) {
                let companyProvince = companyInfoChange.companyProvince
                this.getRegionData(companyProvince) // 加载市
              }

              this.baseInfoModel.baseInfoForm = _omit(companyInfoNoChange, [
                'lastUpdateDate',
                'tenantId',
                'version',
                'lastUpdatedByIp',
                'lastUpdatedBy',
                'isNeedTotal',
                'creationDate',
                'createdByIp',
                'createdBy'
              ])
              this.baseInfoModelChange.baseInfoForm = _omit(companyInfoChange, [
                'lastUpdateDate',
                'tenantId',
                'version',
                'lastUpdatedByIp',
                'lastUpdatedBy',
                'isNeedTotal',
                'creationDate',
                'createdByIp',
                'createdBy'
              ])
              this.baseInfoModel.baseInfoForm.companyCreationDate =
                companyInfoNoChange.companyCreationDate
                  ? this.$dayjs(companyInfoNoChange.companyCreationDate).valueOf()
                  : ''
              this.baseInfoModelChange.baseInfoForm.companyCreationDate = companyInfoChange.companyCreationDate
                ? this.$dayjs(companyInfoChange.companyCreationDate).valueOf()
                : ''
              this.baseInfoModel.baseInfoForm.businessStartDate = companyInfoNoChange.businessStartDate
                ? this.$dayjs(companyInfoNoChange.businessStartDate).valueOf()
                : '' // 开始时间
              this.baseInfoModelChange.baseInfoForm.businessStartDate = companyInfoChange.businessStartDate
                ? this.$dayjs(companyInfoChange.businessStartDate).valueOf()
                : '' // 开始时间
              this.baseInfoModel.baseInfoForm.businessEndDate = companyInfoNoChange.businessEndDate
                ? this.$dayjs(companyInfoNoChange.businessEndDate).valueOf()
                : ''
              this.baseInfoModelChange.baseInfoForm.businessEndDate = companyInfoChange.businessEndDate
                ? this.$dayjs(companyInfoChange.businessEndDate).valueOf()
                : ''

              this.baseDimModel = companyInfoNoChange.dimFieldContexts || {} // 拓展字段值
              this.baseDimModelChange = companyInfoChange.dimFieldContexts
              this.baseInfoModel.baseInfoForm.categoryRelChanges = companyInfoNoChange.categoryRelChanges
              this.baseInfoModelChange.baseInfoForm.categoryRelChanges = companyInfoChange.categoryRelChanges
            }
            if (res.data.otherInfoChange) {
              let otherInfoChange = res.data.otherInfoChange
              this.otherModel.otherForm.companyId = otherInfoChange.companyId
              this.otherModel.otherForm.changeId = otherInfoChange.changeId
              this.otherModel.otherForm.otherChangeId = otherInfoChange.otherChangeId
              this.otherModel.otherForm.otherInfoId = otherInfoChange.otherInfoId
              this.otherModel.otherForm.bizModel = otherInfoChange.bizModel
              this.otherModel.otherForm.floorArea = otherInfoChange.floorArea
              this.otherModel.otherForm.factoryType = otherInfoChange.factoryType
              this.otherModel.otherForm.employeeQty = otherInfoChange.employeeQty
              this.otherModel.otherForm.companySite = otherInfoChange.companySite
              this.otherModel.otherForm.floorSpace = otherInfoChange.floorSpace
              this.otherDimModel = otherInfoChange.dimFieldContexts // 拓展字段值
            }

            if (res.data.infoChange.beforeChangeJson) {
              this.beforeDimDataValue = JSON.parse(res.data.infoChange.beforeChangeJson)?.extend // 变更前模板配置存在extend的数据绑定
              if (JSON.parse(res.data.infoChange.beforeChangeJson).bankInfoChanges) {
                this.bankData = JSON.parse(res.data.infoChange.beforeChangeJson).bankInfoChanges
                  ? this.adaptResutData(JSON.parse(res.data.infoChange.beforeChangeJson).bankInfoChanges) || []
                  : []
              } else {
                this.bankData = JSON.parse(res.data.infoChange.beforeChangeJson).bankInfos
                  ? this.adaptResutData(JSON.parse(res.data.infoChange.beforeChangeJson).bankInfos) || []
                  : []
              }
              this.bankDataChange = res.data.bankInfoChanges
                ? this.adaptResutData(res.data.bankInfoChanges) || []
                : []

              if (JSON.parse(res.data.infoChange.beforeChangeJson).managementAttachChanges) {
                this.sceneAttachmentData = JSON.parse(res.data.infoChange.beforeChangeJson).managementAttachChanges
                  ? this.adaptResutData(JSON.parse(res.data.infoChange.beforeChangeJson).managementAttachChanges) || []
                  : []
              } else {
                this.sceneAttachmentData = JSON.parse(res.data.infoChange.beforeChangeJson).managementAttaches
                  ? this.adaptResutData(JSON.parse(res.data.infoChange.beforeChangeJson).managementAttaches) || []
                  : []
              }
              this.sceneAttachmentDataChange = res.data.managementAttachChanges
                ? this.adaptResutData(res.data.managementAttachChanges) || []
                : []

              if (JSON.parse(res.data.infoChange.beforeChangeJson).contactInfoChanges) {
                this.contactData = JSON.parse(res.data.infoChange.beforeChangeJson).contactInfoChanges
                  ? this.adaptResutData(JSON.parse(res.data.infoChange.beforeChangeJson).contactInfoChanges) || []
                  : []
              } else {
                this.contactData = JSON.parse(res.data.infoChange.beforeChangeJson).contactInfos
                  ? this.adaptResutData(JSON.parse(res.data.infoChange.beforeChangeJson).contactInfos) || []
                  : []
              }
              this.contactDataChange = res.data.contactInfoChanges
                ? this.adaptResutData(res.data.contactInfoChanges) || []
                : []

              if (JSON.parse(res.data.infoChange.beforeChangeJson).fileuploadChanges) {
                this.companyInfoFileList = JSON.parse(res.data.infoChange.beforeChangeJson).fileuploadChanges
                  ? this.adaptFiles(JSON.parse(res.data.infoChange.beforeChangeJson).fileuploadChanges)
                  : []
              } else {
                this.companyInfoFileList = JSON.parse(res.data.infoChange.beforeChangeJson).fileuploads
                  ? this.adaptFiles(JSON.parse(res.data.infoChange.beforeChangeJson).fileuploads)
                  : []
              }
              this.companyInfoFileListChange = res.data.fileuploadChanges
                ? this.adaptFiles(res.data.fileuploadChanges)
                : []
              // 财务信息获取数据
              if (JSON.parse(res.data.infoChange.beforeChangeJson).financeInfos) {
                this.financeInfoData = JSON.parse(res.data.infoChange.beforeChangeJson).financeInfos
                  ? this.adaptResutData(JSON.parse(res.data.infoChange.beforeChangeJson).financeInfos)
                  : []
              } else {
                this.financeInfoData = JSON.parse(res.data.infoChange.beforeChangeJson).financeInfoChanges
                  ? this.adaptResutData(JSON.parse(res.data.infoChange.beforeChangeJson).financeInfoChanges)
                  : []
              }
              this.financeInfoDataChange = res.data.financeInfoChanges
                ? this.adaptResutData(res.data.financeInfoChanges) || []
                : []
            } else {
              this.bankData = res.data.bankInfoChanges
                ? this.adaptResutData(res.data.bankInfoChanges) || []
                : []
              this.bankDataChange = JSON.parse(JSON.stringify(this.bankData))

              // 财务信息获取数据
              this.financeInfoData = res.data.financeInfoChanges
                ? this.adaptResutData(res.data.financeInfoChanges) || []
                : []
              this.financeInfoDataChange = JSON.parse(JSON.stringify(this.financeInfoData))

              this.sceneAttachmentData = res.data.managementAttachChanges
                ? this.adaptResutData(res.data.managementAttachChanges) || []
                : []
              this.sceneAttachmentDataChange = JSON.parse(JSON.stringify(this.sceneAttachmentData))

              this.contactData = res.data.contactInfoChanges
                ? this.adaptResutData(res.data.contactInfoChanges) || []
                : []
              this.contactDataChange = JSON.parse(JSON.stringify(this.contactData))

              this.companyInfoFileList = res.data.fileuploadChanges
                ? this.adaptFiles(res.data.fileuploadChanges)
                : []
              this.companyInfoFileListChange = JSON.parse(JSON.stringify(this.companyInfoFileList))
            }

            this.orgInfoData = res.data.orgInfoChanges
              ? this.adaptResutData(res.data.orgInfoChanges) || []
              : []

            // 操作记录
            this.operatingLogsData = res.data.operatingLogs || []

            this.$nextTick(() => {
              this.$refs.sceneAttachment.loadFileInfo()
              this.$refs.sceneAttachment2.loadFileInfo()
            })

            // 流程ID
            let cbpmInstaceId = res.data.infoChange.cbpmInstaceId
            if (cbpmInstaceId && this.curRole === 'BUYER') {
              this.openWorkFlow = true
              this.resultFlow = true
              this.resultFlowParams = {
                fdId: cbpmInstaceId
              }
              if (this.curOpt === 'doApproval') {
                this.$nextTick(() => {
                  this.sampleActiveInfo = 'tab2' // 切换到流程卡
                })
              }
            }
          }
        })
      }
    },
    // 返回数据适配
    adaptResutData (data) {
      if (data.length > 0) {
        data.forEach((elm) => {
          let dimFieldData = elm.dimFieldContexts
          if (dimFieldData) {
            Object.keys(dimFieldData).forEach((key) => {
              elm[key] = dimFieldData[key]
            })
          }
          delete elm.dimFieldContexts
          delete elm.creationDate // 去掉创建日期
          delete elm.lastUpdateDate // 去掉更新日期
          elm.opType = elm.opType ? elm.opType : ''
        })
        return data
      }
    },
    // 附件适配
    adaptFiles (data) {
      let arr = []
      if (data) {
        data.map((item) => {
          arr.push({
            fileuploadChangeId: item.fileuploadChangeId, // 附件变更ID
            filePureName: item.filePureName, // 模板名称
            sceneFileSourceName: item.sceneFileSourceName, // 模板附件名称
            sceneFileUploadId: item.sceneFileUploadId, // 模板附件Id
            fileuploadId: item.fileuploadId, // 用户上传的附件Id
            fileSourceName: item.fileSourceName, // 用户上传的附件名称
            expireTime: item.expireTime,
            sceneAttachmentId: item.sceneAttachmentId,
            opType: item.opType
          })
        })
      }
      return arr
    },
    selectHandler2 (e, value, scope) {
      scope.orgId = e ? e.organizationId : null
      scope.orgCode = e ? e.organizationCode : ''
      scope.orgName = e ? e.organizationName : null
    },
    // 只允许允许输入数字和字母
    setFormatValue (row) {
      row.authNum = row.authNum.replace(/[\W]/g, '')
    },
    // 选择银行快查
    getBankObj (val, scope) {
      scope.branchBankId = val ? val.branchBankId : ''
      scope.bankCode = val ? val.bankNum : '' // 银行编号
      scope.bankName = val ? val.bankName : '' // 银行名称
      scope.unionCode = val ? val.branchBankNum : '' // 分行编号
      scope.openingBank = val ? val.branchBankName : '' // 分行名称[开户行名称]
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
      this.dataHandle('saveTemporary')
    },
    // 供应商提交
    stagingHandleVENDOR () {
      this.dataHandle('VENDORsubmit')
    },
    // 点击提交
    submitHandle () {
      this.$refs.changeInfoForm.validate((valid) => {
        if (valid) {
          this.$refs.baseInfoForm.validate((valid) => {
            if (valid) {
              this.submitHandleFuntion()
            } else {
              this.$message({
                message: this.$t('vendorMod.enterRequired'), // '请输入必填项',
                type: 'error'
              })
              return false
            }
          })
        } else {
          this.$message({
            message: this.$t('vendorMod.enterRequired'), // '请输入必填项',
            type: 'error'
          })
          return false
        }
      })
    },
    // 提交函数
    submitHandleFuntion (types) {
      if (!this.infoChangeModel.infoChange.enable4MChange) {
        return this.$message.error(this.$t('vendorMod.msgSelect4M')) // 请选择是否是4M变更
      }

      if (this.baseInfoModelChange.baseInfoForm.registCurrency == '' || this.baseInfoModelChange.baseInfoForm.registCurrency == null) {
        this.$message.error('请输入三证信息内的币种')
        return false
      }

      // 校验一个组织至少需要有一个账期
      let orgIdArr = []
      this.financeInfoDataChange.forEach(e => {
        orgIdArr.push(e.orgId)
      })
      let orgBol = true
      if (orgIdArr.length > 0) {
        this.orgInfoData.forEach((items) => {
          if (!orgIdArr.includes(items.orgId)) {
            orgBol = false
          }
        })
      } else {
        if (this.orgInfoData.length > 0) {
          orgBol = false
        }
      }
      if (!orgBol) {
        this.$message.error(this.$t('vendorMod.msgDel1'))
        return false
      }

      let checkSiteInfoChangesOrgid = false
      let checkSiteInfoChangesvendorSiteCode = false
      let checkSiteInfoChangescountry = false
      let checkSiteInfoChangesaddressDetail = false
      if (this.siteInfoChangesChange) {
        this.siteInfoChangesChange.forEach((item) => {
          if (!item.orgId) {
            checkSiteInfoChangesOrgid = true
          }
          if (!item.vendorSiteCode) {
            checkSiteInfoChangesvendorSiteCode = true
          }
          if (!item.country) {
            checkSiteInfoChangescountry = true
          }
          if (!item.addressDetail) {
            checkSiteInfoChangesaddressDetail = true
          }
        })
      }
      if (checkSiteInfoChangesOrgid) {
        this.$message({
          type: 'warning',
          message: this.$t('vendorMod.msgWriteVOrg')
        }) // 请填写供应商地点的业务实体
        return false
      } else if (checkSiteInfoChangesvendorSiteCode) {
        this.$message({
          type: 'warning',
          message: this.$t('vendorMod.msgWriteVAddressName')
        }) // 请填写供应商地点的地点名称
        return false
      } else if (checkSiteInfoChangescountry) {
        this.$message({
          type: 'warning',
          message: this.$t('vendorMod.msgWriteVCountry')
        }) // 请填写供应商地点的国家
        return false
      } else if (checkSiteInfoChangesaddressDetail) {
        this.$message({
          type: 'warning',
          message: this.$t('vendorMod.msgWriteVAddressDetail')
        }) // 请填写供应商地点的详细地址
        return false
      }
      let _this = this
      // 基础信息
      if (_this.$refs.baseInfoForm) {
        _this.$refs.baseInfoForm.validate((valid) => {
          if (valid) {
            if (_this.baseInfoDimFieldContexts.length > 0) {
              let dValid = false
              _this.$refs.baseDimForm.validate((dimValid) => {
                dValid = dimValid
              })
              if (!dValid) {
                this.$message({
                  message: this.$t('vendorMod.msgCompanyBaseInfo'), // "请输入企业基本信息必填项",
                  type: 'error'
                })
                return false
              }
            }
            let changeValid = false
            _this.$refs.changeInfoForm.validate((cValid) => {
              changeValid = cValid
            })
            if (!changeValid) {
              this.$message({
                message: this.$t('vendorMod.msgVendorInfoChange'), // "请输供应商变更说明！",
                type: 'error'
              })
              return false
            }
            this.dataHandle(types)
          } else {
            this.$message({
              message: this.$t('vendorMod.msgCompanyBaseInfo'), // "请输入企业基本信息必填项",
              type: 'error'
            })
            return false
          }
        })
      } else {
        if (_this.baseInfoDimFieldContexts.length > 0) {
          let dValid = false
          _this.$refs.baseDimForm.validate((dimValid) => {
            dValid = dimValid
          })
          if (!dValid) {
            this.$message({
              message: this.$t('vendorMod.msgCompanyBaseInfo'), // "请输入企业基本信息必填项",
              type: 'error'
            })
            return false
          }
        }
        let changeValid = false
        _this.$refs.changeInfoForm.validate((cValid) => {
          changeValid = cValid
        })
        if (!changeValid) {
          this.$message({
            message: this.$t('vendorMod.msgVendorInfoChange'), // "请输供应商变更说明！",
            type: 'error'
          })
          return false
        }
        this.dataHandle(types)
        // this.loggerComment = true
      }
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
        data.map((item) => {
          catArr.push({
            companyId: this.companyId,
            changeId: this.changeId,
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
      console.log(this.financeInfoDataChange,'financeInfoDataChange')
      const submitData = {}
      const companyInfo = { ...this.baseInfoModelChange.baseInfoForm }
      const companyInfoDimFields = { ...this.baseDimModelChange }
      companyInfo.dimFieldContexts = companyInfoDimFields // 拓展字段赋值
      if (type === 'submitted') {
        companyInfo.ceeaDraftsmanOpinion = this.$t('vendorMod.pleaseApproval') // 请审批，谢谢！
      }
      companyInfo.categoryRelChanges = this.categoryRelFormat(companyInfo.categoryRelChanges) // 可供品类
      const otherInfo = { ...this.otherModel.otherForm } // 其他信息
      otherInfo.dimFieldContexts = { ...this.otherDimModel } // 其他信息拓展字段
      const contactInfo = this.formatDimFields(this.contactDimFieldContexts, this.contactDataChange) // 联系人信息
      const financeInfos = this.formatDimFields(this.financeDimFieldContexts, this.financeInfoDataChange, 'finance') // 财务信息
      console.log(this.sceneAttachmentDataChange)
      let bolFileuploadId = false
      this.sceneAttachmentDataChange.forEach(data01 => {
        if (!data01.fileuploadId) {
          bolFileuploadId = true
        }
      })
      if (bolFileuploadId) {
        this.$message.error('请输入相关认证信息必填数据')
        return false
      }
      const orgInfos = this.formatDimFields(this.orgDimFieldContexts, this.orgInfoData) // 组织信息
      submitData.infoChange = { ...this.infoChangeModel.infoChange } // 变更信息
      submitData.infoChange.beforeChangeJson = this.beforeChangeJson
      submitData.companyInfoChange = companyInfo
      submitData.siteInfoChanges = [...this.siteInfoChangesChange, ...this.siteInfoChangesSelect]
      submitData.bankInfoChanges = this.bankDataChange
      submitData.managementAttachChanges = this.sceneAttachmentDataChange
      submitData.contactInfoChanges = contactInfo
      submitData.financeInfoChanges = financeInfos
      submitData.orgInfoChanges = orgInfos
      submitData.otherInfoChange = otherInfo
      submitData.fileuploadChanges = this.companyInfoFileListChange
      console.log(submitData)
      console.log(type)
      let url = ''
      if (type === 'SUBMIT') {
        // 提交
        url = `/api-sup/change/infoChange/${this.curRole === 'VENDOR' ? 'vendorSubmitted' : 'submitted'}`
      } else if (type === 'approved') {
        // 审批
        url = '/api-sup/change/infoChange/approve'
      } else if (type === 'rejected') {
        // 驳回
        url = '/api-sup/change/infoChange/rejected'
      } else if (type === 'VENDORsubmit') {
        // 供应商提交
        url = '/api-sup/change/infoChange/submitted'
      } else {
        // 暂存
        url = '/api-sup/change/infoChange/saveTemporary'
      }
      // 供应商的情况下新增 change by liwenhong
      this.infoChangeModel.infoChange.companyName =
        this.$attrs.params.companyNameAdd || this.infoChangeModel.infoChange.companyName
      if (type === 'SAVE' && this.infoChangeModel.infoChange.companyName === '') {
        this.$message({
          type: 'warning',
          message: this.$t('vendorMod.msgVendorReInput')
        }) // 您输入的供应商不存在，或该供应商存在正在变更的单据，请重新输入。
        return false
      }

      // 扩展提交
      let _this = this
      let baseData = []
      this.dimensionData.forEach((elemnt, i) => {
        let extendData = 'extendData' + i
        baseData = [...baseData, ..._this.$refs[extendData][0].model]
      })
      submitData.dimFieldResultChangeList = baseData

      saveOrUpdateOrderByUrl(url, submitData).then(async res => {
        if (res) {
          if (res.code == '0') {
            this.$message({
              message: res.message,
              type: 'success'
            })
          } else {
            this.$message.error(res.message)
            return false
          }

          if (type === 'SAVE') {
            // 暂存的时候返回公司Id查询一次旧数据
            this.changeId = res.data.formId
            this.$emit('tab-remove', this.$attrs.params.tabName)
            this.__setTabTodo('vendorInfoChangeList.getQuerydata')
          } else if (type === 'SUBMIT') {
            // 提交
            this.changeId = res.data.formId
            this.curOpt = 'edit'
            if (this.curRole === 'VENDOR') {
              // 供应商
              this.$emit('tab-remove', this.$attrs.params.tabName)
              this.__setTabTodo('vendorInfoChangeList.getQuerydata')
            } else {
              // 采购商
              await this.fatchOldData() // 查询旧数据
              await this.handlerAfter(type)
            }
          } else {
            this.$emit('tab-remove', this.$attrs.params.tabName)
            this.__setTabTodo('vendorInfoChangeList.getQuerydata')
          }
          let dimData = this.getDimDataFromVue(res?.data?.formId)
          modelConfigApi.saveFormResutlForBusiness(dimData)
        }
      })
    },
    // 拓展字段维度信息值转换
    formatDimFields (dimFieldArr, modelData, type = 'null') {
      let arr = []
      if (dimFieldArr && dimFieldArr.length > 0) {
        // 有拓展字段
        modelData.forEach((item, index) => {
          if (item.opType === 'add' || item.opType === 'update' || item.opType === 'delete' || type != 'null') {
            let dyObj = {}
            dimFieldArr.forEach((elm) => {
              let key = elm.fieldCode
              let val = item[key]
              dyObj[key] = val
              // delete modelData[elm.fieldCode]
            })
            item.dimFieldContexts = dyObj
            arr.push(item)
          }
        })
      } else {
        // 无拓展字段
        modelData.forEach((item, index) => {
          item.dimFieldContexts = {}
          arr.push(item)
        })
      }
      return arr
    },
    addFinance () {
      // 添加财务信息
      this.financeInfoDataChange.push({
        opType: 'add',
        orgId: null,
        companyId: this.companyId
      })
    },
    addContactData () {
      // 新增联系人
      this.contactDataChange.push({
        opType: 'add',
        orgId: null,
        companyId: this.companyId
      })
    },
    addBank () {
      // 添加银行
      this.bankDataChange.push({
        opType: 'add',
        orgId: null,
        bankName: null,
        openingBank: null,
        unionCode: null,
        ceeaMainAccount: 'Y',
        ceeaEnabled: 'N',
        companyId: this.companyId
      })
    },
    addSceneAttachment () {
      // 认证信息
      this.sceneAttachmentDataChange.push({
        opType: 'add',
        companyId: this.companyId
      })
    },
    addContact () {
      // 新增联系人
      this.contactDataChange.push({
        opType: 'add',
        companyId: this.companyId
      })
    },
    addOrg () {
      // 新增合作组织
      this.orgInfoData.unshift({
        opType: 'add',
        orgId: null,
        companyId: this.companyId
      })
    },
    // 新增附件
    addFile () {
      this.attachTableData.push({
        add: true,
        opType: 'add',
        companyId: this.companyId,
        attachmentDiscription: '',
        attachmentPic: '',
        attachmentPicFileId: null,
        attachmentValidDate: ''
      })
    },
    // 表格样式处理
    rowClassName ({ row, rowIndex }) {
      if (row.opType === 'add' || row.opType === 'update' || row.opType === 'delete') {
        return 'update-row'
      }
      return ''
    },
    // 选择业务人员
    selectPeople () {
      this.peopleDialog = true
    },
    // 获取选择器
    getPeople (data) {
      let user = data[0]
      this.infoChangeModel.infoChange.noticeById = user.userId
      this.infoChangeModel.infoChange.noticeByName = user.nickname || user.username
    },
    innerHandleUploadSuccess (file) {
      const { id, name } = file
      this.sceneAttachmentDataChange[this.bankRowIndex2].fileuploadId = id.toString()
      this.sceneAttachmentDataChange[this.bankRowIndex2].authType = name
      this.sceneAttachmentDataChange.push({})
      this.sceneAttachmentDataChange.splice(this.sceneAttachmentDataChange.length - 1, 1)
    },
    innerButtonClick (index) {
      this.bankRowIndex2 = index
    },

    /* 驳回 */
    buyerReject () {
      this.$prompt('', '驳回原因', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputValidator: value => !(!value || value.length > 500),
        inputErrorMessage: '驳回原因必填并且长度不能超过500字符！'
      }).then(({ value }) => {
        this.$http({
          url: '/api-sup/change/infoChange/buyerReject',
          method: 'POST',
          data: {
            changeId: this.changeId,
            flowRemark: value
          },
          loading: true
        }).then(() => {
          this.$message.success(this.$t('common.success'))
          this.$emit('tab-remove', this.$attrs.params.tabName)
          this.__setTabTodo('vendorInfoChangeList.getQuerydata')
        })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
:deep(.el-tab-pane){
  overflow-y: auto;
  overflow-x: hidden;
}
:deep(.redColorFont input){
  color: red !important;
}
.app-main .el-container{
  overflow: auto;
}
.changeTab{
  margin-top: 18px;
}

.formClassAll{
  width: 50%;
  padding: 10px 10px 10px 0;
}
.formClassAll form{
  padding-left: 18px
}
.changeTitle{
  background-color: #F6F6F6;
  font-size: 14px ;
  color: #393E45 ;
  overflow: hidden;
  line-height: 40px;
  margin-bottom:20px;
  font-weight: 400;
}
.changeTitleTop{
  margin-top: 10px;
}
.changeTitle i{
  width: 4px;
  height: 18px;
  background-color: #0077FF;
  margin: 11px 10px 11px 16px;
  display: block;
  float: left;
}
:deep(.formClassAll .el-select),
:deep(.formClassAll .el-input-all),
:deep(.formClassAll .el-date-editor),
:deep(.formClassAll .el-input-group){
  width: 60%;
}
.enable4MChangeDetail{
  position: absolute;
  top: 5px;
  left: 89px;
}
.formClassWrap{
  display: flex;
}
.tab-form-style{
  // padding-bottom: 1px
}
:deep(.topComment) {
  text-align: center;
  margin-top: 10px;
}
:deep(.toRequired)  {
  color: #ff4949;
  padding-right: 4px;
  margin-left: 6px;
}
:deep(.the-vendorInfoChangeDetail-detail) {
  .sub_header {
    margin: 0 0 10px;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
}
:deep(.companyInfoFillChange) {
  padding-bottom: 40px;
}
:deep(.the_info_message_wrapper .el-form-item__content) {
  line-height: 14px;
}
:deep(.vue-treeselect__multi-value-item.vue-treeselect__multi-value-item-new) {
  background: #efd9df;
}
:deep(.el-form .vue-treeselect__control) {
  height: 30px !important;
}
:deep(.el-checkbox:last-of-type){
  height: 30px !important;
}
.operating-logs{
  margin-bottom:20px
}
</style>
