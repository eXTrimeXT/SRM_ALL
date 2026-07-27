<template>
  <el-container class="the-vendorGreenChannelDetail-detail page-ctrl-right" direction="vertical">
    <CWorkflowMulti
      ref="workflowMulti"
      v-model="activeTabName"
      :funParams="workflowParamsInfo"
      :buttonConfigInfo="buttonConfigInfo"
      @tab-click="workflowView"
      @workflow-handler="workflowHandler"
      @click-handler="(type) => saveBill(type)"
      @submit-direct="(type) => saveBill(type)"
      @confirm="(type, comment) => saveBill(type, comment)"
      @close-tab="back"
    >
      <el-main style="position: relative;">
        <div class="stepDiv">
          <el-steps :active="curStatus" :align-center="true" finish-status="success">
            <el-step :title="$t('vendorMod.finishCompanyInfo')" />
            <el-step :title="$t('common.successSubmit')" />
            <el-step :title="$t('vendorMod.approvalSuccess')" />
          </el-steps>
        </div>
        <div class="companyInfoFill">
          <el-collapse v-model="activeDims">
            <!-- 供应商账号信息 -->
            <el-collapse-item ref="vendorUserInfo" :title="$t('vendorMod.vendorUserInfo')" name="1">
              <el-form
                ref="userInfoForm"
                :model="userInfo"
                :rules="userRules"
                :disabled="isReadOnly"
                class="form-fill-style"
              >
                <srm-row>
                  <srm-col :initCol="3">
                    <!-- 用户名 -->
                    <el-form-item prop="username" :label="$t('vendorMod.userName')">
                      <el-input v-model="userInfo.username" :placeholder="$t('key15')" @change="setFormatName" />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="3">
                    <!-- 名称 -->
                    <el-form-item prop="nickname" :label="$t('dataConfMod.userName')">
                      <el-input v-model="userInfo.nickname" />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="3">
                    <!-- 邮箱 -->
                    <el-form-item prop="email" :label="$t('common.email')">
                      <el-input v-model="userInfo.email" @change="setEmailFormatValue" />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="3">
                    <!-- 联系人电话 -->
                    <el-form-item prop="phone" :label="$t('vendorMod.contactPhone')">
                      <el-input
                        v-model="userInfo.phone"
                        :disabled="isReadOnly"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="3">
                    <!-- 岗位 -->
                    <el-form-item
                      prop="positions"
                      :label="$t('components.orgPositionSel.position')"
                    >
                      <el-input v-model="userInfo.position" :disabled="isReadOnly" />
                    </el-form-item>
                  </srm-col>
                </srm-row>
              </el-form>
              <el-form
                ref="userInfoForm2"
                :model="allParam.companyInfo"
                :disabled="isReadOnly"
                class="form-fill-style"
              >
                <srm-row>
                  <srm-col :initCol="1">
                    <!-- 使用绿色通道引入供应商的原因详述 -->
                    <el-form-item
                      :rules="{
                        required: true,
                        message: $t('vendorMod.msgInputReason')
                      }"
                      prop="greenChannelReason"
                      :label="$t('vendorMod.greenChannelReason')"
                    >
                      <el-input
                        v-model="allParam.companyInfo.greenChannelReason"
                        type="textarea"
                        :autosize="{ minRows: 2, maxRows: 4 }"
                      />
                    </el-form-item>
                  </srm-col>
                </srm-row>
              </el-form>
            </el-collapse-item>
            <!--企业性质-->
            <el-collapse-item ref="companyType" :title="$t('vendorMod.companyType')" name="2">
              <el-form
                ref="baseInfoForm3"
                :disabled="isReadOnly"
                class="base-form-info form-fill-style"
                :model="allParam.companyInfo"
                :show-message="false"
              >
                <srm-row :gutter="32">
                  <srm-col :initCol="3">
                    <!-- 境内外关系 -->
                    <!--message: $t('vendorMod.msgOverseasRelation')-->
                    <el-form-item
                      prop="overseasRelation"
                      :label="$t('vendorMod.overseasRelation')"
                      :rules="{
                        required: true
                      }"
                      :show-message="false"
                    >
                      <el-select
                        v-model="allParam.companyInfo.overseasRelation"
                        @change="overseasChangeHandle"
                      >
                        <el-option
                          v-for="item in relations"
                          :key="item.id"
                          :label="item.label"
                          :value="item.value"
                        />
                      </el-select>
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="curRel === 'INSIDE'" :initCol="3">
                    <!-- 企业性质 -->
                    <!--message: $t('vendorMod.msgCompanyType')-->
                    <el-form-item
                      :rules="{
                        required: true
                      }"
                      prop="companyType"
                      :label="$t('vendorMod.companyType')"
                    >
                      <DictSelect
                        v-model="allParam.companyInfo.companyType"
                        code="COMPANY_NATURE"
                        @change="companyTypeChangeHandle"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="3">
                    <!-- 是否长期供应商 -->
                    <el-form-item :label="$t('vendorMod.ifLongTermSupplier')">
                      <el-radio
                        v-model="allParam.companyInfo.ifLongPeriod"
                        :label="$t('common.Y')"
                        class="formCheckbox"
                      >
                        {{ $t('common.yes') }}
                      </el-radio>
                      <el-radio
                        v-model="allParam.companyInfo.ifLongPeriod"
                        :label="$t('common.N')"
                        class="formCheckbox"
                      >
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
                      <DictSelect
                        v-model="allParam.companyInfo.ceeaSupBusinessType"
                        code="SUP_BUSINESS_TYPE"
                        :disabled="isReadOnly"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="3">
                    <!-- 供应商类型 -->
                    <!--message: $t('supplierRating.supplierTypeMgs')-->
                    <el-form-item
                      :rules="{
                        required: true
                      }"
                      :label="$t('supplierRating.supplierType')"
                      prop="supplierType"
                      :show-message="false"
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
                      <DictSelect v-model="allParam.companyInfo.ceeaBusinessModel" code="BIZ_MODEL" />
                    </el-form-item>
                  </srm-col>
                  <!-- 只有境外供应商有 -->
                  <srm-col v-if="curRel === 'OUT'" :initCol="3">
                    <!-- DUNS编号 -->
                    <el-form-item prop="dunsCode" :label="$t('vendorMod.dunsCode')">
                      <el-input v-model="allParam.companyInfo.dunsCode" />
                    </el-form-item>
                  </srm-col>
                </srm-row>
              </el-form>
            </el-collapse-item>
            <!--企业三证-->
            <el-collapse-item
              ref="enterpriseThreeCertificates"
              :title="$t('vendorMod.enterpriseThreeCertificates')"
              name="3"
            >
              <el-form
                :disabled="isReadOnly"
                class="base-form-info form-fill-style"
                :model="allParam.companyInfo"
              >
                <div style="display: flex;flex-flow: wrap">
                  <!--营业执照上传-->
                  <div style="width: 33%;padding-right: 25px;">
                    <srm-common-file
                      :default-file="{ fileId: allParam.companyInfo.businessLicenseFileId, fileName: allParam.companyInfo.businessLicense }"
                      drag
                      :limit="1"
                      :dragger-options="{
                        width: '100%',
                        height: '312px'
                      }"
                      list-type="picture-card"
                      :readonly="isReadOnly"
                      @on-change="handleUploadSuccess"
                    />
                  </div>
                  <div style="width: 67%;padding-left: 20px;">
                    <el-form
                      ref="baseInfoForm2"
                      :disabled="isReadOnly"
                      class="base-form-info form-fill-style"
                      :model="allParam.companyInfo"
                    >
                      <srm-row :gutter="32">
                        <srm-col :initCol="2">
                          <!-- 企业名称 -->
                          <el-form-item
                            prop="companyName"
                            :label="$t('vendorMod.companyName')"
                            :rules="{
                              required: true,
                              message: $t('vendorMod.msgCompanyName')
                            }"
                          >
                            <!--                    :disabled="curRel === 'INSIDE'"-->
                            <el-input v-model="allParam.companyInfo.companyName" />
                          </el-form-item>
                        </srm-col>
                        <srm-col :initCol="2">
                          <!-- 法人代表 -->
                          <!-- required: curRel === 'INSIDE' ? true : false, -->
                          <el-form-item
                            :rules="{
                              required: true,
                              message: $t('vendorMod.msgLegalPerson')
                            }"
                            prop="legalPerson"
                            :label="$t('vendorMod.legalPerson')"
                          >
                            <el-input v-model="allParam.companyInfo.legalPerson" />
                          </el-form-item>
                        </srm-col>
                        <srm-col v-if="curRel === 'INSIDE'" :initCol="2">
                          <!-- 统一社会信用代码 -->
                          <el-form-item
                            :rules="{
                              required: curRel === 'INSIDE' ? true : false,
                              message: $t('vendorMod.msgLcCode')
                            }"
                            prop="lcCode"
                            :label="$t('vendorMod.lcCode')"
                          >
                            <el-input v-model="allParam.companyInfo.lcCode" />
                          </el-form-item>
                        </srm-col>
                        <srm-col v-if="curType !== 'GETI'" :initCol="2">
                          <!-- 注册资本(万) -->
                          <el-form-item
                            :rules="{
                              required: curType === 'GETI' || curType === 'FEIYINGLI' ? false : true,
                              message: $t('vendorMod.msgRegisteredCapital')
                            }"
                            prop="registeredCapital"
                            :label="$t('vendorMod.registeredCapital')"
                          >
                            <!--                    :disabled="curRel === 'INSIDE'"-->
                            <el-input
                              v-model="allParam.companyInfo.registeredCapital"
                              oninput="value=value.replace(/^\.+|[^\d.]/g,'')"
                              class="input-with-select"
                            >
                              <DictSelect
                                slot="append"
                                v-model="allParam.companyInfo.registCurrency"
                                code="BID_TENDER_CURRENCY"
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
                            :rules="{
                              required: curType === 'FEIYINGLI' ? false : true,
                              message: $t('vendorMod.msgCreationDate')
                            }"
                          >
                            <el-date-picker
                              v-model="allParam.companyInfo.companyCreationDate"
                              type="date"
                              :placeholder="$t('common.pleaseSelectDate')"
                              format="yyyy-MM-dd"
                              value-format="timestamp"
                            />
                          </el-form-item>
                        </srm-col>
                        <srm-col v-if="curType !== 'GETI'" :initCol="2">
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
                              @change="businessDateChange"
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
                            :rules="{
                              required: true,
                              message: $t('vendorMod.msgDetailAddr')
                            }"
                            prop="companyAddress"
                            :label="$t('components.address.detailAddress')"
                          >
                            <el-input v-model="allParam.companyInfo.companyAddress" />
                          </el-form-item>
                        </srm-col>
                        <srm-col :initCol="2">
                          <!-- 企业简称 -->
                          <el-form-item
                            prop="companyShortName"
                            :label="$t('vendorMod.companyShortName')"
                          >
                            <el-input v-model="allParam.companyInfo.companyShortName" />
                          </el-form-item>
                        </srm-col>
                        <srm-col :initCol="2">
                          <!-- 登记机关 -->
                          <el-form-item
                            prop="registrationAuthority"
                            :label="$t('vendorMod.registrationAuthority')"
                          >
                            <el-input v-model="allParam.companyInfo.registrationAuthority" />
                          </el-form-item>
                        </srm-col>
                      </srm-row>
                    </el-form>
                  </div>
                  <srm-col :initCol="1">
                    <!-- 营业范围 -->
                    <el-form-item :label="$t('vendorMod.businessScope')">
                      <el-input v-model="allParam.companyInfo.businessScope" type="textarea" />
                    </el-form-item>
                  </srm-col>
                </div>
              </el-form>
            </el-collapse-item>
            <!-- 企业基本信息 -->
            <el-collapse-item
              ref="companyBaseInfo"
              :title="$t('vendorMod.companyBaseInfo')"
              name="4"
            >
              <!-- :show-message="false" :rules="baseInfoModel.rules"-->
              <el-form
                ref="baseInfoForm"
                :disabled="isReadOnly"
                class="base-form-info form-fill-style"
                :model="allParam.companyInfo"
              >
                <srm-row :gutter="32">
                  <srm-col :initCol="3">
                    <!-- 代理品牌 -->
                    <el-form-item
                      :label="$t('vendorMod.agencyBrand')"
                      prop="ceeaAgentBrand"
                      :rules="{
                        required: itemisRequired2 ? true : false,
                        message: $t('vendorMod.msgAgencyBrand')
                      }"
                    >
                      <el-input v-model="allParam.companyInfo.ceeaAgentBrand" />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="3">
                    <!-- 主营品类 -->
                    <el-form-item :label="$t('vendorMod.mainCategory')" class="is-required">
                      <CCategorySelect
                        select-type="input"
                        class="categoryName"
                        :multiple="true"
                        :selected-lines="allParam.cateJournalList"
                        :placeholder="allParam.companyInfo.categoryName"
                        @select="getCategoryObj"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="3">
                    <!-- 是否上市 -->
                    <el-form-item :label="$t('vendorMod.ifListed')">
                      <el-radio
                        v-model="allParam.companyInfo.ceeaIfListed"
                        :label="$t('common.Y')"
                        class="formCheckbox"
                      >
                        {{ $t('common.yes') }}
                      </el-radio>
                      <el-radio
                        v-model="allParam.companyInfo.ceeaIfListed"
                        :label="$t('common.N')"
                        class="formCheckbox"
                      >
                        {{ $t('common.no') }}
                      </el-radio>
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="allParam.companyInfo.ceeaIfListed == 'Y'" :initCol="3">
                    <!-- 上市时间 -->
                    <el-form-item
                      :label="$t('vendorMod.listedDate')"
                      prop="ceeaListedTime"
                      :rules="{
                        required: true,
                        message: $t('请选择上市时间')
                      }"
                    >
                      <el-date-picker
                        v-model="allParam.companyInfo.ceeaListedTime"
                        type="date"
                        format="yyyy-MM-dd"
                        value-format="yyyy-MM-dd"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="3">
                    <!-- 是否有母公司 -->
                    <el-form-item :label="$t('vendorMod.ifParentCompany')">
                      <el-radio
                        v-model="allParam.companyInfo.ceeaHasParentCompany"
                        :label="$t('common.Y')"
                        class="formCheckbox"
                      >
                        {{ $t('common.yes') }}
                      </el-radio>
                      <el-radio
                        v-model="allParam.companyInfo.ceeaHasParentCompany"
                        :label="$t('common.N')"
                        class="formCheckbox"
                      >
                        {{ $t('common.no') }}
                      </el-radio>
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="allParam.companyInfo.ceeaHasParentCompany == 'Y'" :initCol="3">
                    <!-- 母公司名称 -->
                    <el-form-item
                      :label="$t('vendorMod.parentCompanyName')"
                      prop="ceeaParentCompanyName"
                      :rules="{
                        required: true,
                        message: $t('请输入母公司名称')
                      }"
                    >
                      <el-input v-model="allParam.companyInfo.ceeaParentCompanyName" />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="allParam.companyInfo.ceeaHasParentCompany == 'Y'" :initCol="3">
                    <!-- 母公司统一信用代码 -->
                    <el-form-item
                      :label="$t('vendorMod.parentCompanyLcCode')"
                      prop="ceeaParentCompanyLcCode"
                      :rules="{
                        required: true,
                        message: $t('请输入母公司统一信用代码')
                      }"
                    >
                      <el-input v-model="allParam.companyInfo.ceeaParentCompanyLcCode" />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="1">
                    <!-- 企业简介 -->
                    <el-form-item
                      :label="$t('vendorMod.companyProfile')"
                      prop="ceeaCompanyIntro"
                      :rules="{
                        required: itemisRequired ? true : false,
                        message: this.$t('vendorMod.msgCompanyProfile')
                      }"
                    >
                      <el-input v-model="allParam.companyInfo.ceeaCompanyIntro" />
                    </el-form-item>
                  </srm-col>
                </srm-row>
              </el-form>
              <FormExtend
                ref="baseDimForm"
                :formData="baseInfoDimFieldContexts"
                :model="baseDimModel"
                :rules="baseDimRules"
                :disabled="isReadOnly"
              />
            </el-collapse-item>
            <!-- 联系人信息 -->
            <el-collapse-item ref="contactInfo" :title="$t('vendorMod.contactInfo')" name="5">
              <div class="left_div">
                <p class="sub_header">
                  <el-button
                    :disabled="isReadOnly"
                    type="primary"
                    class="detail-pbtn"
                    @click="addOne"
                  >
                    {{ $t('common.new') }}
                  </el-button>
                </p>
                <el-table
                  :data="allParam.contactInfos"
                  style="width: 100%"
                  border
                  max-height="250px"
                >
                  <el-table-column align="center" type="index" width="50" />
                  <!-- 姓名 -->
                  <el-table-column
                    align="center"
                    prop="contactName"
                    :label="$t('vendorMod.nickname')"
                    width="130"
                    :show-overflow-tooltip="true"
                  >
                    <template slot="header">
                      <i class="toRequired">*</i><span>{{ $t('vendorMod.nickname') }}</span>
                    </template>
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.contactName" :disabled="isReadOnly" />
                    </template>
                  </el-table-column>
                  <!-- 性别 -->
                  <el-table-column
                    align="center"
                    prop="ceeaGender"
                    :label="$t('vendorMod.sex')"
                    width="100"
                    :show-overflow-tooltip="true"
                  >
                    <template slot-scope="scope">
                      <DictSelect
                        v-model="scope.row.ceeaGender"
                        code="GENDER"
                        :disabled="isReadOnly"
                      />
                    </template>
                  </el-table-column>
                  <!-- 部门1 -->
                  <el-table-column
                    align="center"
                    prop="ceeaDeptName"
                    :label="$t('vendorMod.department')"
                    min-width="100"
                    :show-overflow-tooltip="true"
                  >
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.ceeaDeptName" :disabled="isReadOnly" />
                    </template>
                  </el-table-column>
                  <!-- 职位 -->
                  <el-table-column
                    align="center"
                    prop="position"
                    :label="$t('dataConfMod.position')"
                    min-width="100"
                    :show-overflow-tooltip="true"
                  >
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.position" :disabled="isReadOnly" />
                    </template>
                  </el-table-column>
                  <!-- 联系方式 -->
                  <el-table-column
                    align="center"
                    prop="ceeaContactMethod"
                    :label="$t('vendorMod.contactMethod')"
                    min-width="100"
                    :show-overflow-tooltip="true"
                  >
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.ceeaContactMethod" :disabled="isReadOnly" />
                    </template>
                  </el-table-column>
                  <!-- 邮箱 -->
                  <el-table-column
                    align="center"
                    prop="email"
                    :label="$t('vendorMod.email')"
                    min-width="180"
                    :show-overflow-tooltip="true"
                  >
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.email" :disabled="isReadOnly" />
                    </template>
                  </el-table-column>
                  <!-- 默认联系人 -->
                  <el-table-column
                    align="center"
                    prop="ceeaDefaultContact"
                    :label="$t('dataConfMod.isDefault')"
                    min-width="100"
                    :show-overflow-tooltip="true"
                  >
                    <template slot-scope="scope">
                      <el-checkbox
                        v-model="scope.row.ceeaDefaultContact"
                        true-label="Y"
                        false-label="N"
                        :disabled="isReadOnly"
                      />
                    </template>
                  </el-table-column>
                  <!-- 备注 -->
                  <el-table-column
                    align="center"
                    prop="ceeaComments"
                    :label="$t('dataConfMod.remark')"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  >
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.ceeaComments" :disabled="isReadOnly" />
                    </template>
                  </el-table-column>
                  <!-- 拓展字段 [[-->
                  <template v-if="contactDimFieldContexts.length > 0">
                    <el-table-column
                      v-for="col in contactDimFieldContexts"
                      :key="col.fieldId"
                      :prop="col.fieldCode"
                      :label="col.languageCode ? $t(col.languageCode) : col.fieldName"
                      min-width="140px"
                    >
                      <template slot-scope="scope">
                        <el-input v-model="scope.row[col.fieldCode]" />
                      </template>
                    </el-table-column>
                  </template>

                  <!-- 拓展字段 ]]]-->
                  <el-table-column fixed="right" :label="$t('common.operation')" width="60">
                    <template slot-scope="scope">
                      <el-button
                        :disabled="isReadOnly"
                        type="text"
                        @click="handleDelClick(scope.$index, scope.row)"
                      >
                        {{ $t('common.delete') }}
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </el-collapse-item>
            <!-- 银行信息 -->
            <el-collapse-item ref="bankInfo" :title="$t('vendorMod.bankInfo')" name="6">
              <div class="left_div">
                <p class="sub_header">
                  <el-button
                    :disabled="isReadOnly"
                    type="primary"
                    class="detail-pbtn"
                    @click="addOneBank"
                  >
                    {{ $t('common.new') }}
                  </el-button>
                </p>
                <el-table :data="allParam.bankInfos" style="width: 100%" border max-height="250px">
                  <!-- 银行代码 -->
                  <el-table-column align="center" type="index" width="50" />
                  <el-table-column
                    align="center"
                    prop="bankCode"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  >
                    <template slot="header">
                      <i class="toRequired">*</i>{{ $t('components.bank.bankCode') }}
                    </template>
                    <template slot-scope="scope">
                      <QuickSearch
                        :disabled="isReadOnly"
                        :showInput="scope.row.bankCode"
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
                  <el-table-column
                    align="center"
                    prop="bankName"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  >
                    <template slot="header">
                      <i class="toRequired">*</i>{{ $t('components.bank.bankName') }}
                    </template>
                  </el-table-column>
                  <!-- 开户行名称 -->
                  <el-table-column
                    align="center"
                    prop="openingBank"
                    min-width="160"
                    :show-overflow-tooltip="true"
                  >
                    <template slot="header">
                      <i class="toRequired">*</i>{{ $t('components.bank.branchBankName') }}
                    </template>
                  </el-table-column>
                  <!-- 分行编码 -->
                  <el-table-column
                    align="center"
                    prop="unionCode"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  >
                    <template slot="header">
                      <i class="toRequired">*</i>{{ $t('components.bank.unionCode') }}
                    </template>
                  </el-table-column>
                  <!-- 账户名称 -->
                  <el-table-column
                    align="center"
                    prop="bankAccountName"
                    :label="$t('components.bank.accountName')"
                    min-width="150"
                    :show-overflow-tooltip="true"
                    :disabled="isReadOnly"
                  >
                    <template #header>
                      <i class="toRequired">*</i>
                      <span>{{ $t("components.bank.accountName") }}</span>
                    </template>
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.bankAccountName" :disabled="isReadOnly" />
                    </template>
                  </el-table-column>
                  <!-- 银行账号 -->
                  <el-table-column
                    align="center"
                    prop="bankAccount"
                    :label="$t('components.bank.bankAccount')"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  >
                    <template #header>
                      <i class="toRequired">*</i>
                      <span>{{ $t("components.bank.bankAccount") }}</span>
                    </template>
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.bankAccount" :disabled="isReadOnly" />
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
                    <template #header>
                      <i class="toRequired">*</i>
                      <span>{{ $t("vendorMod.currencyCode") }}</span>
                    </template>
                    <template slot-scope="scope">
                      <DictSelect
                        v-model="scope.row.currencyCode"
                        code="BID_TENDER_CURRENCY"
                        :disabled="isReadOnly"
                      />
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
                        :disabled="isReadOnly"
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
                        :disabled="isReadOnly"
                        true-label="Y"
                        false-label="N"
                      />
                    </template>
                  </el-table-column>
                  <!-- 拓展字段 [[-->
                  <template v-if="bankDimFieldContexts.length > 0">
                    <el-table-column
                      v-for="col in bankDimFieldContexts"
                      :key="col.fieldId"
                      :prop="col.fieldCode"
                      :label="col.languageCode ? $t(col.languageCode) : col.fieldName"
                      width="110px"
                    >
                      <template slot-scope="scope">
                        <el-input v-model="scope.row[col.fieldCode]" />
                      </template>
                    </el-table-column>
                  </template>
                  <el-table-column fixed="right" :label="$t('common.operation')" width="60">
                    <template slot-scope="scope">
                      <el-button
                        :disabled="isReadOnly"
                        type="text"
                        @click="handleDelClickBank(scope.$index, scope.row)"
                      >
                        {{ $t('common.delete') }}
                      </el-button>
                    </template>
                  </el-table-column>

                  <!-- 拓展字段 ]]]-->
                </el-table>
              </div>
            </el-collapse-item>
            <!-- 合作信息 -->
            <el-collapse-item ref="cooInfo" :title="$t('supRisk.cooInfo')" name="7">
              <template slot="title">
                <i class="toRequired">*</i>{{ $t('supRisk.cooInfo') }}
              </template>
              <p class="sub_header">
                <el-button
                  :disabled="isReadOnly"
                  type="primary"
                  class="detail-pbtn"
                  @click="addOrgCategory"
                >
                  {{ $t('common.new') }}
                </el-button>
              </p>
              <el-table
                ref="orgCatTable"
                :data="allParam.orgCategorys"
                style="width: 100%"
                border
                use-virtual
                :emptyText="$t('common.noData')"
                :row-height="35"
                max-height="250px"
              >
                <el-table-column
                  prop="orgId"
                  align="center"
                  :label="$t('components.userSelection.orgName')"
                  min-width="200"
                >
                  <template #header>
                    <i class="toRequired">*</i>
                    <span>{{ $t("components.userSelection.orgName") }}</span>
                  </template>
                  <template slot-scope="scope">
                    <OrganizationSelector
                      ref="organizationSelector3"
                      v-model="scope.row.orgId"
                      :disabled="isReadOnly"
                      :parentId="-1"
                      nodeType="OU"
                      :placeholder="$t('common.pleaseSelect')"
                      :scope="scope.row"
                      @select="selectHandler2"
                    />
                  </template>
                </el-table-column>
                <!-- 采购品类 -->
                <el-table-column
                  prop="categoryName"
                  align="center"
                  :label="$t('vendorMod.category')"
                  min-width="200"
                >
                  <template #header>
                    <i class="toRequired">*</i>
                    <span>{{ $t("vendorMod.category") }}</span>
                  </template>
                  <template slot-scope="scope">
                    <template>
                      <CCategorySelect
                        v-model="scope.row.categoryName"
                        :disabled="isReadOnly"
                        :scope="scope.row"
                        showKey="categoryName"
                        :placeholder="$t('vendorMod.msgCategoryNormalizer')"
                        @select="comfirmSelect"
                      />
                    </template>
                  </template>
                </el-table-column>
                <!-- 拓展字段 [[-->
                <template v-if="orgCatDimFieldContexts.length > 0">
                  <el-table-column
                    v-for="col in orgCatDimFieldContexts"
                    :key="col.fieldId"
                    :prop="col.fieldCode"
                    :label="col.languageCode ? $t(col.languageCode) : col.fieldName"
                    width="110px"
                  >
                    <template slot-scope="scope">
                      <el-input v-model="scope.row[col.fieldCode]" />
                    </template>
                  </el-table-column>
                </template>
                <!-- 拓展字段 ]]]-->
                <!-- 操作 -->
                <el-table-column
                  align="center"
                  prop="operation"
                  :label="$t('common.operation')"
                  width="60"
                >
                  <template slot-scope="scope">
                    <el-button
                      :disabled="isReadOnly"
                      type="text"
                      @click="orgCategoryDel(scope.$index, scope.row)"
                    >
                      {{ $t('common.delete') }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <!-- 财务信息 -->
            <el-collapse-item ref="financeInfo" name="8" :title="$t('vendorMod.financeInfo')">
              <p class="sub_header">
                <el-button
                  type="primary"
                  :disabled="isReadOnly||allParam.orgCategorys.length==0"
                  @click="addFinance"
                >
                  {{
                    $t('common.new')
                  }}
                </el-button>
              </p>
              <el-table
                ref="financeTable"
                :data="financeInfoData"
                style="width: 100%"
                border
                max-height="250px"
              >
                <!-- 引入组织 -->
                <el-table-column
                  align="center"
                  prop="fullPathId"
                  :label="$t('vendorMod.ceeaOrgName2')"
                  min-width="200"
                >
                  <template slot-scope="scope">
                    <!--                    <organization-selector-->
                    <!--                      v-model="scope.row.orgId"-->
                    <!--                      :parentId="-1"-->
                    <!--                      nodeType="OU"-->
                    <!--                      :placeholder="$t('common.pleaseSelect')"-->
                    <!--                      :scope="scope.row"-->
                    <!--                      :disabled="isReadOnly"-->
                    <!--                      @select="addOrgHandleAccounting"-->
                    <!--                    />-->
                    <el-select
                      v-model="scope.row.orgId"
                      style="width: 100%"
                      :disabled="isReadOnly"
                      @change="orgChange(scope.row.orgId,scope.row)"
                    >
                      <el-option
                        v-for="item in removeDuplicates(allParam.orgCategorys)"
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
                  width="150"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.factoryCode"
                      :disabled="isReadOnly"
                      maxlength="50"
                      show-word-limit
                      onKeyUp="value=value.replace(/[^\w\\/]/ig,'')"
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
                      :disabled="isReadOnly"
                    />
                  </template>
                </el-table-column>
                <!-- 付款方式 -->
                <el-table-column align="center" prop="paymentMethod" width="150">
                  <template slot="header">
                    <i class="toRequired">*</i>{{ $t('vendorMod.paymentMethod') }}
                  </template>
                  <template slot-scope="scope">
                    <el-select v-model="scope.row.paymentMethod" :disabled="isReadOnly">
                      <el-option
                        v-for="item in paymantType"
                        :key="item.id"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </template>
                </el-table-column>
                <!-- 付款账期 -->
                <el-table-column align="center" prop="paymentTerms" width="150">
                  <template slot="header">
                    <i class="toRequired">*</i>{{ $t('vendorMod.paymentTerms') }}
                  </template>
                  <template slot-scope="scope">
                    <el-select v-model="scope.row.paymentTerms" :disabled="isReadOnly">
                      <el-option
                        v-for="item in paymantTerms"
                        :key="item.id"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
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
                      type="text"
                      :disabled="isReadOnly"
                      @click="financeDel(scope.$index, scope.row)"
                    >
                      {{
                        $t('common.delete')
                      }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <!-- 厂房信息 -->
            <el-collapse-item ref="factoryInfo" :title="$t('vendorMod.factoryInfo')" name="9">
              <div class="left_div">
                <p class="sub_header">
                  <el-button type="primary" :disabled="isReadOnly" class="detail-pbtn" @click="addOnePlant">
                    {{ $t("common.new") }}
                  </el-button>
                </p>
                <el-table :data="allParam.plantInfos" style="width: 100%" border max-height="250px">
                  <el-table-column align="center" type="index" width="50" />
                  <!-- 厂房名称 -->
                  <el-table-column
                    align="center"
                    prop="plantName"
                    :label="$t('vendorMod.factoryInfo')"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  >
                    <template #header>
                      <i class="toRequired">*</i>
                      <span>{{ $t("vendorMod.factoryInfo") }}</span>
                    </template>
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.plantName" :disabled="isReadOnly" />
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
                      <i class="toRequired">*</i>
                      <span>{{ $t("vendorMod.factoryType") }}</span>
                    </template>
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.plantNature" :disabled="isReadOnly" />
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
                      <i class="toRequired">*</i>
                      <span>{{ $t("vendorMod.factoryArea") }}</span>
                    </template>
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.plantArea" :disabled="isReadOnly" />
                    </template>
                  </el-table-column>
                  <!-- 所在国、地区/省/市 -->
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
                        @change-value="plantProvinceChange($event, scope)"
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
                      <i class="toRequired">*</i>
                      <span>{{ $t("vendorMod.factoryAddress") }}</span>
                    </template>
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.plantAddress" :disabled="isReadOnly" />
                    </template>
                  </el-table-column>
                  <el-table-column fixed="right" :label="$t('common.operation')" width="60">
                    <template slot-scope="scope">
                      <el-button
                        type="text"
                        :disabled="isReadOnly"
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
            <el-collapse-item
              ref="vendorSiteInfos"
              :title="$t('vendorMod.vendorSiteInfos')"
              name="10"
            >
              <p class="sub_header">
                <el-button
                  type="primary"
                  style="margin-right: 5px"
                  :disabled="curOpt === 'view' || isReadOnly"
                  class="detail-pbtn"
                  @click="addSiteInfo"
                >
                  {{ $t('common.new') }}
                </el-button>
                <el-popover v-model="countryListVisible" placement="top" width="300">
                  <div style="padding: 11px">
                    <div style="margin-top: 6px; margin-bottom: 6px">
                      <span style="padding-right: 5px">
                        <!-- 请选择国家 -->
                        {{ $t('vendorMod.msgSelCountry') }}
                      </span>
                      <DictSelect v-model="globalCountry" code="country" filterable clearable />
                    </div>
                    <div style="margin-top: 12px; margin-bottom: 6px">
                      <span style="padding-right: 5px">
                        <!-- 请输入详细地址 -->
                        {{ $t('vendorMod.msgDetailAddr') }}
                      </span>
                      <el-input v-model="globalAddressDetail" clearable style="display: block" />
                    </div>
                  </div>
                  <div style="text-align: right; margin: 0">
                    <el-button type="text" @click="countryListVisible = false">
                      {{ $t('common.cancel') }}
                    </el-button>
                    <el-button type="primary" @click="batchSelectCountry">
                      {{ $t('common.confirm') }}
                    </el-button>
                  </div>
                  <el-button
                    slot="reference"
                    type="primary"
                    class="detail-pbtn"
                    :disabled="curOpt === 'view' || isReadOnly"
                  >
                    <!-- 批量维护国家 -->
                    {{ $t('vendorMod.batchMaintainCountry') }}
                  </el-button>
                </el-popover>
              </p>
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
                      @select="selectHandler"
                    />
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
                      @change-value="plantProvinceChange2($event, scope)"
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
                <ModelConfigForm
                  v-if="item.dimType === 'form' && item.originalDimFlag === 'N'"
                  :ref="item.dimCode"
                  :dimConfig="modelConfig.dimConfigMap[item.dimCode]"
                  :formValue="dimDataValue"
                />
                <!-- 如果是表格的话显示 -->
                <ModelConfigTable
                  v-if="item.dimType === 'table'"
                  :ref="item.dimCode"
                  :dimConfig="modelConfig.dimConfigMap[item.dimCode]"
                  :index="index"
                  :tableValue="dimDataValue"
                />
              </el-collapse-item>
            </template>
            <!-- 相关认证信息 -->
            <el-collapse-item
              ref="managementSystemInfo"
              :title="$t('vendorMod.managementSystemInfo')"
              name="20"
            >
              <el-form ref="relModel" :model="allParam.managementInfo" class="rel-form-select">
                <srm-row :gutter="32">
                  <srm-col :initCol="1">
                    <!-- 是否通过ISO9001质量体系认证(如是请上传附件) -->
                    <el-form-item :label="$t('vendorMod.msgIfPass1')">
                      <el-radio-group
                        v-model="allParam.managementInfo.ifIsoQuality"
                        :disabled="isReadOnly"
                        @change="managementChange($event, '是否通过ISO9001质量体系认证')"
                      >
                        <el-radio label="Y">
                          {{ $t('common.yes') }}
                        </el-radio>
                        <el-radio label="N">
                          {{ $t('common.no') }}
                        </el-radio>
                      </el-radio-group>
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="1">
                    <!-- 是否通过ISO14001环境体系认证(如是请上传附件) -->
                    <el-form-item :label="$t('vendorMod.msgIfPass2')">
                      <el-radio-group
                        v-model="allParam.managementInfo.ifIsoEnviron"
                        :disabled="isReadOnly"
                        @change="managementChange($event, '是否通过ISO14001环境体系认证')"
                      >
                        <el-radio label="Y">
                          {{ $t('common.yes') }}
                        </el-radio>
                        <el-radio label="N">
                          {{ $t('common.no') }}
                        </el-radio>
                      </el-radio-group>
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="1">
                    <!-- 是否通过OHSAS18000职业、健康安全体系认证(如是请上传附件) -->
                    <el-form-item :label="$t('vendorMod.msgIfPass3')">
                      <el-radio-group
                        v-model="allParam.managementInfo.ifOhsasSafe"
                        :disabled="isReadOnly"
                        @change="managementChange($event, '是否通过OHSAS18000职业、健康安全体系认证')"
                      >
                        <el-radio label="Y">
                          {{ $t('common.yes') }}
                        </el-radio>
                        <el-radio label="N">
                          {{ $t('common.no') }}
                        </el-radio>
                      </el-radio-group>
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="1">
                    <!-- 其他认证情况(如是请上传附件) -->
                    <el-form-item :label="$t('vendorMod.msgIfPass4')">
                      <el-input
                        v-model="allParam.managementInfo.otherAuthSit"
                        :disabled="isReadOnly"
                      />
                    </el-form-item>
                  </srm-col>
                </srm-row>
              </el-form>
              <FormExtend
                ref="certificationInfoForm"
                :formData="certificationInfotexts"
                :model="certificationInfoModel"
                :rules="baseDimRules"
                :disabled="isReadOnly"
              />
              <div class="left_div">
                <!-- 认证情况 -->
                <p>
                  <el-button
                    :disabled="isReadOnly"
                    type="primary"
                    class="detail-pbtn"
                    @click="addOneAuth"
                  >
                    {{ $t('common.new') }}
                  </el-button>
                </p>
                <el-table
                  :data="allParam.managementAttaches"
                  style="width: 100%"
                  border
                  max-height="250px"
                >
                  <el-table-column align="center" type="index" width="50" />
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
                      <el-input v-model="scope.row.authDescription" :disabled="isReadOnly" />
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
                        :disabled="isReadOnly"
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
                        :disabled="isReadOnly"
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
                      <el-input v-model="scope.row.authOrg" :disabled="isReadOnly" />
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
                        :disabled="isReadOnly"
                        format="yyyy-MM-dd"
                        value-format="yyyy-MM-dd"
                      />
                    </template>
                  </el-table-column>
                  <el-table-column fixed="right" :label="$t('common.operation')" width="60">
                    <template slot-scope="scope">
                      <el-button
                        :disabled="isReadOnly"
                        type="text"
                        @click="handleDelClickAuth(scope.$index, scope.row)"
                      >
                        {{ $t('common.delete') }}
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </el-collapse-item>
            <!-- 相关附件信息 -->
            <el-collapse-item
              ref="sceneAttachmentInfo"
              :title="$t('vendorMod.sceneAttachmentInfo2')"
              name="21"
            >
              <FileDynamic
                ref="sceneAttachment"
                v-model="companyInfoFileList"
                scene-module-code="SCENE_SUPPLIER_ATTACHMENT"
                :business-id="companyId"
                :editable="!isReadOnly"
              />
            </el-collapse-item>
          </el-collapse>
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
              <!-- :model2="Emodel2" -->
              <TableExtend
                v-if="item.dimShowType == 'TABLE' && evalF(item.showDimCondition)"
                :ref="'extendData' + index"
                :tableData="item.dimFieldConfigS"
                :index="index"
                :tableExtendList="tableExtendList"
                :model2="Emodel2"
                :addOneTableData="addOneTableData"
                :rules="baseDimRules"
                :disabled="isReadOnly"
              />
              <!-- 如果是表单的话显示 -->
              <FormExtend2
                v-if="item.dimShowType == 'FORM'"
                :ref="'extendData' + index"
                :formData="item.dimFieldConfigS"
                :model2="Emodel"
                :rules="FormExtend2Rules"
                :disabled="isReadOnly"
                @selectChange="selectChanges($event)"
              />
              <!-- TABLE -->
            </el-collapse-item>
          </el-collapse>
        </div>
        <!-- 进度条信息 -->
        <!-- 企业登记节点 -->
        <CFillProgress
          ref="greenProgress"
          :nodeName="$t('vendorMod.companyRegisterNode')"
          :data="nodeData"
          :percentage="true"
          @index-click="indexClickTo"
        />
      </el-main>
      <!-- 起草人意见 -->
      <srm-dialog
        :title="$t('vendor.loggerComment')"
        :visible.sync="loggerComment"
        size="middle"
        style="text-align: center"
      >
        <!-- 请审批，谢谢！ -->
        <el-input
          v-model="inputComment"
          type="textarea"
          :rows="4"
          :placeholder="$t('vendorMod.pleaseApproval')"
        />
        <div class="topComment">
          <el-button @click="loggerComment = false">
            {{ $t('common.cancel') }}
          </el-button>
          <el-button type="primary" @click="commentForm">
            {{ $t('common.confirm') }}
          </el-button>
        </div>
      </srm-dialog>
    </CWorkflowMulti>
  </el-container>
</template>
<script>
import OrganizationSelector from 'lib@/components/organization-selector'
import { tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import CFillProgress from 'lib@/components/c-fill-progress'
import CAddress from 'lib@/components/c-address'
import CCategorySelect from 'lib@/components/c-category-select'
import OrganizationSelectTree from 'lib@/components/organization-cascader'
import ModelConfigForm from 'mod@/common/userManage/views/ModelConfig/ModelConfigForm'
import ModelConfigTable from 'mod@/common/userManage/views/ModelConfig/ModelConfigTable'
import _omit from 'lodash/omit'
import { adaptDictData } from '@/utils'
import { getDictItem, getDictItemList } from '@/api/common'
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
import http from '@/utils/axios/http'
import FormExtend from 'mod@/common/userManage/views/companyInfoMaintain/formExtend'
import FormExtend2 from 'mod@/common/userManage/views/companyInfoMaintain/formExtend2'
import TableExtend from 'mod@/common/userManage/views/companyInfoMaintain/tableExtend'
import FileDynamic from '@/library/components/c-file-management/file-dynamic'
import WorkflowCommon from '@/library/mixins/workflow-common'
import Configurationization from 'lib@/components/configurationization'
import { vendorGreenApi } from 'modb@/vendorManagementBuyer/api/vendorManagement'
import { vendorOptCommonApi } from 'mod@/common/userManage/api'
import { modelConfigApi } from '@/api/modelConfig'
export default {
  name: 'VendorGreenChannelDetail',
  components: {
    CToolbar,
    CFillProgress,
    CCategorySelect,
    OrganizationSelector,
    QuickSearch,
    OrganizationSelectTree,
    TableExtend,
    FormExtend2,
    FormExtend,
    FileDynamic,
    CAddress,
    Configurationization,
    ModelConfigForm,
    ModelConfigTable
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    const validatePassword = (rule, value, callback) => {
      const patrn =
        /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[`~!@#$%^&*()_\-+=<>?:"{}|,.\\/;'\\[\]·~！@#￥%……&*（）——\-+={}|《》？：“”【】、；‘'，。、]).{8,24}$/
      if (!value) {
        callback(new Error(this.$t('vendorMod.enterPass'))) // '请输入密码!'
      } else if (!patrn.test(value) || value.length < 8 || value.length > 24) {
        callback(new Error(this.$t('vendorMod.errorPass'))) // '密码至少包含数字、大小写字母、特殊字符，长度为8~24位'
      } else {
        callback()
      }
    }
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
      itemisRequired: false,
      itemisRequired2: false,
      address: [],
      selectChangeData: {},
      Emodel: [],
      Emodel2: [],
      dimensionData: [],
      tableExtendList: [], // 扩展table类型数据
      inputComment: '',
      loggerComment: false,
      companyInfoFileList: [],
      selectedOrgList: [],
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'companyInfoMaintain',
        fileType: 'images'
      },
      countryListVisible: null,
      globalAddressDetail: null,
      globalCountry: null,
      addressList: [],
      companyId: null,
      curRel: '',
      curType: '',
      isEnableOcr: 'N', // 是否启用OCR营业执照识别
      fileUploadId: null, // ocr 文件入参ID
      ocrVisible: false, // ocr 弹窗
      // 营业执照接收的文件类型
      acceptFileType: ['jpg', 'png', 'jpeg'],
      curOpt: 'add',
      orgDialog: false,
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
        '16',
        '17',
        '18',
        '19',
        '20',
        '21'
      ],
      nodeData: [
        // 进度条节点信息
        {
          code: 'vendorUserInfo',
          name: this.$t('vendorMod.vendorUserInfo'),
          percentage: 0
        },
        {
          code: 'companyType',
          name: this.$t('vendorMod.companyType'),
          percentage: 0
        },
        {
          code: 'enterpriseThreeCertificates',
          name: this.$t('vendorMod.enterpriseThreeCertificates'),
          percentage: 0
        },
        {
          code: 'companyBaseInfo',
          name: this.$t('vendorMod.companyBaseInfo'),
          percentage: 0
        },
        { code: 'contactInfo', name: this.$t('vendorMod.contactInfo'), percentage: 0 },
        {
          code: 'bankInfo',
          name: this.$t('vendorMod.bankInfo'),
          percentage: 0
        },
        { code: 'cooInfo', name: this.$t('supRisk.cooInfo'), percentage: 0 },
        {
          code: 'financeInfo',
          name: this.$t('vendorMod.financeInfo'),
          percentage: 0
        },
        {
          code: 'factoryInfo',
          name: this.$t('vendorMod.factoryInfo'),
          percentage: 0
        },
        {
          code: 'vendorSiteInfos',
          name: this.$t('vendorMod.vendorSiteInfos'),
          percentage: 0
        },
        {
          code: 'managementSystemInfo',
          name: this.$t('vendorMod.managementSystemInfo'),
          percentage: 0
        },
        {
          code: 'sceneAttachmentInfo',
          name: this.$t('vendorMod.sceneAttachmentInfo'),
          percentage: 0
        }
      ],
      relations: [], // 境内外管理
      currencyList: [], // 币种列表
      genderList: [],
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
      userInfo: {
        // 账号信息
        username: '',
        password: 'Longi@123',
        nickname: '',
        email: '',
        position: '',
        phone: ''
      },
      FormExtend2Rules: {},
      userRules: {},
      userInfoRules: {
        username: [{ required: true, message: this.$t('请输入用户名') }],
        nickname: [{ required: true, message: this.$t('vendorMod.msgInputNickname') }],
        email: [{ required: true, message: this.$t('dataConfMod.msgMail') }]
      },
      userInfoRulesEdit: {
        username: [{ required: true, message: this.$t('请输入用户名') }]
      },
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
          ifLongPeriod: 'N',
          companyCountry: '', // 国家
          companyProvince: '', // 省
          companyCity: '', // 市
          companyAddress: '', // 详细地址
          businessScope: '', // 业务范围
          greenChannelReason: '',
          companyStatus: '', // 经营状态
          approvedDate: '', // 准入日期
          categoryRels: [], // 可供品类
          dimFieldContexts: {}
        },
        rules: {}
      },
      baseInfoDimFieldContexts: [], // 基础信息 拓展字段
      certificationInfotexts: [], // 认证信息 扩展字段
      financeDimFieldContexts: [], // 财务信息拓展字段
      bankDimFieldContexts: [], // 银行信息拓展字段
      contactDimFieldContexts: [], // 联系人拓展字段
      orgDimFieldContexts: [], // 合作组织拓展字段
      orgCatDimFieldContexts: [], // 组织和品类拓展字段
      baseDimModel: {}, // 基础信息拓展字段
      baseDimRules: {}, // 基础信息拓展字段规则
      otherDimFieldContexts: [], // 其他信息拓展字段
      otherDimModel: {},
      certificationInfoModel: {}, // 认证信息 扩展字段
      financeInfoData: [], // 财务信息
      bankData: [], // 银行信息
      contactData: [], // 联系人
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
      allParam: {
        plantInfos: [],
        rules: {},
        companyInfo: {
          businessDate: [],
          companyId: null,
          status: 'DRAFT',
          overseasRelation: '',
          companyType: '',
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
          greenChannelReason: '',
          categoryRels: '',
          ceeaPlantType: '',
          ceeaPlantArea: '',
          ceeaAgentBrand: '',
          ceeaIfListed: 'Y'
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
          internationalTopFive: '',
          internalTopFive: ''
        },
        contactInfos: [],
        orgCategorys: [],
        orgInfos: [],
        bankInfos: [],
        siteInfos: [],
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
        managementInfo: {
          managementInfoId: null,
          companyId: null,
          ifIsoQuality: 'N',
          ifIsoEnviron: 'N',
          ifOhsasSafe: 'N',
          otherAuthSit: ''
        },
        managementAttaches: []
      }
    }
  },
  computed: {
    viewUpdateButton () {
      // 用来控制保存、提交按钮是否可见。如果自定义按钮则无需添加
      let bol
      if (
        this.allParam.companyInfo.status === 'DRAFT' ||
        this.allParam.companyInfo.status === 'WITHDRAW' ||
        this.allParam.companyInfo.status === 'REJECTED'
      ) {
        bol = true
      } else {
        bol = false
      }
      return bol
    },
    workflowBusinessId () {
      // 用来指定工作流的业务ID
      return this.companyId ? this.companyId : null
    },
    // 展示工作流tab页
    workflowTabDisabled () {
      let bol
      if (
        this.allParam.companyInfo.status === 'SUBMITTED' ||
        this.allParam.companyInfo.status === 'WITHDRAW' ||
        this.allParam.companyInfo.status === 'APPROVED' ||
        this.allParam.companyInfo.status === 'REJECTED'
      ) {
        bol = false
      } else {
        bol = true
      }
      return bol
    },
    isReadOnly () {
      return (
        this.$attrs.params.flag === 'readOnly' ||
        this.$attrs.params.flag === 'approve' ||
        this.$attrs.params.isReadOnly
      )
    },
    curStatus: function () {
      let status = null
      if (this.allParam.companyInfo.status === 'DRAFT' || this.allParam.companyInfo.status === '') {
        status = 0
      } else if (this.allParam.companyInfo.status === 'SUBMITTED') {
        status = 1
      } else if (this.allParam.companyInfo.status === 'APPROVED') {
        status = 3
      } else {
        status = 2
      }
      return status
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
    this.fatchDictData() // 字典

    if (this.$attrs.params.flag === 'add') {
      // 新增
      this.userRules = this.userInfoRules
      this.curOpt = this.$attrs.params.flag
      this.$nextTick(() => {
        this.$refs.sceneAttachment.loadFileInfo()
      })
    } else {
      // edit | readOnly
      this.userRules = this.userInfoRulesEdit
      this.companyId = this.$attrs.params.companyId
      this.fatchOldData() // 查询旧数据
    }
    this.getDimDataById(this.companyId)
    // 获取动态配置(复制粘贴即可)
    this.getConfig()
    modelConfigApi.getModelConfig('companyInfoMaintain').then(result => {
      this.getModelConfig = result
    })
    modelConfigApi.getDimDataById(this.companyId).then(result => {
      this.dimDataValues = result.data
    })

    this.buttonConfigInfo.save.view = this.viewUpdateButton
    this.buttonConfigInfo.submit.view = this.viewUpdateButton
    this.buttonConfigInfo.cancel.view = false
    this.buttonConfigInfo.close.view = false
    this.buttonConfigInfo.save.name = '暂存'
    this.buttonConfigInfo.submit.name = '提交'
  },
  mounted () {},
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
    removeDuplicates (datas) {
      let attrId = []
      let attr = []
      datas.forEach((dataE) => {
        if (!attrId.includes(dataE.orgId)) {
          attrId.push(dataE.orgId)
          attr.push(dataE)
        }
      })
      return attr
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
    businessDateChange (value) {
      this.$forceUpdate()
      console.log(value)
    },
    plantProvinceChange (value, scope) {
      scope.row.plantCity = ''
    },
    plantProvinceChange2 (value, scope) {
      scope.row.city = ''
    },
    addressChange (value) {
      this.address = value
    },
    addressChangeList (value, row) {
      row.address = value
    },
    back () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('vendorGreenChannelList.getQuerydata')
    },
    getCategoryObj (val, scope) {
      if (val) {
        if (val.length > 3) {
          this.$message.warning(this.$t('vendorMod.msgMost3LittleCate')) // 请选择最多三个小类
          return
        }
        if (val.length > 0) {
          this.allParam.cateJournalList = []
          for (let item of val) {
            this.allParam.cateJournalList.push({
              categoryId: item.categoryId,
              categoryCode: item.categoryCode,
              categoryName: item.categoryName
            })
          }
          this.$set(
            this.allParam.companyInfo,
            'categoryName',
            val.map(v => v.categoryName).join(','),
          )
        } else {
          this.allParam.cateJournalList = []
          this.$set(this.allParam.companyInfo, 'categoryName', '')
        }
      } else {
        this.allParam.cateJournalList = []
      }
    },
    managementChange (value, name) {
      console.log(value + ',' + name)
      if (value == 'Y') {
        this.allParam.managementAttaches.unshift({
          documentInspection: name,
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
      } else {
        let _this = this
        _this.allParam.managementAttaches.forEach((e, index) => {
          if (e.documentInspection == name) {
            _this.allParam.managementAttaches.splice(index, 1)
          }
        })
      }
    },
    handleDelClickPlant (index, row) {
      this.allParam.plantInfos.splice(index, 1)
    },
    addOnePlant () {
      this.allParam.plantInfos.push({ plantCity: '' })
    },
    orgChange (orgId, row) {
      let dictItem = this.allParam.orgInfos.find(i => i.orgId === orgId) || {}
      row.orgCode = dictItem.orgCode
      row.orgName = dictItem.orgName
    },
    saveBill (type, comment) {
      if (type === 'SAVE') {
        this.stagingHandle(type)
      } else {
        this.submitHandle(type)
      }
    },
    async getWorkflowBusinessType () {
      return 'supplierGreenChannel'
    },
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    getTaxRateObj (val, dictItem, row) {
      row.taxRate = dictItem.key
    },
    // 维度间的级联
    selectChanges (val) {
      let selectChangeData = this.selectChangeData
      for (let key in selectChangeData) {
        for (let key2 in val) {
          if (key == key2) {
            this.$set(this.selectChangeData, key, val[key2])
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
    // 扩展表单点击新增
    addOneTableData (indexs) {
      this.tableExtendList[indexs].push({})
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
        if (parme.originalDimFlag == 'N') {
          parme.model = {}
          _this.dimensionData.push(parme)
          _this.tableExtendList.push([{}])
        }
      })

      this.sortDimList() // 重新排序

      console.log(this.dimensionData)

      this.dimensionData.forEach(res => {
        // 显示右边锚点
        const obj = {
          code: res.dimCode,
          name: res.dimName,
          percentage: 'NAN'
        }
        if (!res.relateDimCode) {
          this.nodeData.push(obj)
        }
      })

      if (res) {
        try {
          // 重新赋值
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
          console.log(this.Emodel2)
        } catch (e) {}
      }
    },
    // 计算字段
    computedBaseFields () {
      let overseasRelation = this.allParam.companyInfo.overseasRelation // 境内外类型
      let companyType = this.allParam.companyInfo.companyType // 公司属性
      let resArr = []
      if (overseasRelation === 'OUT') {
        // 境外
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
          'businessScope'
          // "companyStatus",
          // "approvedDate",
          // "categoryRels"
        ]
      } else {
        // INSIDE
        if (companyType === 'GETI') {
          // 个体
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
            'businessScope'
            // "companyStatus",
            // "approvedDate",
            // "categoryRels"
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
            // "companyStatus",
            'greenChannelReason'
            // "approvedDate",
            // "categoryRels"
          ]
        }
      }
      return resArr
    },
    // 品类
    categoryNormalizer (node) {
      const result = {
        id: node.categoryId,
        label: node.categoryName
      }
      return result
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
        { dictCode: 'PAYMENT_METHOD' }, // 付款方式
        { dictCode: 'INVOICE_LIMIT' }, // 发票限额
        { dictCode: 'PAYMENT_TERMS' }, // 付款条件
        { dictCode: 'CATEGORY_STATUS' }, // 品类状态
        { dictCode: 'ORG_STATUS' }, // 组织服务状态
        { dictCode: 'GENDER' }
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
          PAYMENT_METHOD,
          INVOICE_LIMIT,
          PAYMENT_TERMS,
          CATEGORY_STATUS,
          ORG_STATUS,
          GENDER
        ] = res.data
        this.relations = adaptDictData(RELATION.RELATION, 'dict')
        this.companyStatus = adaptDictData(COMPANY_STATUS.COMPANY_STATUS, 'dict')
        this.bizModel = adaptDictData(BIZ_MODEL.BIZ_MODEL, 'dict')
        this.factoryType = adaptDictData(FACTORY_TYPE.FACTORY_TYPE, 'dict')
        this.employeeQyt = adaptDictData(EMPLOYEE_QTY.EMPLOYEE_QTY, 'dict')
        this.bankAccountType = adaptDictData(BANK_ACCOUNT_TYPE.BANK_ACCOUNT_TYPE, 'dict')
        this.paymantType = adaptDictData(PAYMENT_METHOD.PAYMENT_METHOD, 'dict')
        this.invoiceLimit = adaptDictData(INVOICE_LIMIT.INVOICE_LIMIT, 'dict')
        this.paymantTerms = adaptDictData(PAYMENT_TERMS.PAYMENT_TERMS, 'dict')
        this.catStatus = adaptDictData(CATEGORY_STATUS.CATEGORY_STATUS, 'dict')
        this.orgStatus = adaptDictData(ORG_STATUS.ORG_STATUS, 'dict')
        this.genderList = adaptDictData(GENDER.GENDER, 'dict')
      })
      getDictItem('VENDOR_SITE_CODE').then(res => {
        this.addressList = adaptDictData(res.data, 'dict')
      })
      // 是否启用OCR
      getDictItem('ENABLE_OCR').then(res => {
        this.isEnableOcr = res.data[0].dictItemCode
      })
      // 获取所有币种---
      getDictItem('BID_TENDER_CURRENCY').then(res => {
        this.currencyList = adaptDictData(res.data, 'dict')
      })
    },
    setRowAmount (row) {
      if (row.postCode.length > 6) {
        return this.$message.error(this.$t('components.address.msgPostalError')) // 邮政编码不应超过6位！
      }
    },
    getCountry (row) {
      // 选择国外就清理省市区，并且禁用
      if (row.country !== 'CN') {
        row.province = null
        row.city = null
      }
      if (row.plantCountry !== 'CN') {
        row.plantProvince = null
        row.plantCity = null
      }
    },
    // 切换海内外关系
    overseasChangeHandle (val) {
      this.curRel = val // 当前海内外关系
      if (val === 'INSIDE') {
        // 境内
        this.allParam.companyInfo.companyCountry = 'CN'
      } else {
        // 境外
        this.allParam.companyInfo.companyType = ''
      }
      this.getDimAttrConfig() // 属性配置信息
    },
    // 切换公司属性
    companyTypeChangeHandle (val) {
      this.curType = val // 当前公司属性
      this.getDimAttrConfig() // 属性配置信息
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
            scope.belongOprId = res.data.erpOrgId
          }
        })
      }
    },
    selectHandler2 (node, value, scope) {
      scope.orgId = node ? node.organizationId : null
      scope.orgCode = node ? node.organizationCode : null
      scope.orgName = node ? node.organizationName : null
    },
    batchSelectCountry () {
      this.allParam.siteInfos.forEach((val, index) => {
        val.country = this.globalCountry
        val.addressDetail = this.globalAddressDetail
        // 选择国外就清理省市区，并且禁用
        if (this.globalCountry !== 'CN') {
          val.province = null
          val.city = null
        }
      })
      this.countryListVisible = false
    },
    addSiteInfo () {
      this.allParam.siteInfos.unshift({
        siteInfoId: null,
        companyId: null,
        erpVendorId: null,
        erpVendorCode: null,
        vendorSiteCode: null,
        country: null,
        province: null,
        city: null,
        addressDetail: null,
        purchaseFlag: null,
        paymentFlag: null,
        rfqOnlyFlag: null,
        enabledFlag: 'Y',
        enabledDeleteFlag: 'Y',
        orgId: null,
        orgCode: null,
        orgName: null,
        vendorSiteId: null,
        disableDate: null,
        postCode: null,
        siteComment: null
      })
    },
    handleDelClickSite (index, row) {
      this.allParam.siteInfos.splice(index, 1)
    },
    // 行删除
    handleDelClickBank (index, row) {
      this.allParam.bankInfos.splice(index, 1)
    },
    addOne () {
      this.allParam.contactInfos.push({
        contactInfoId: null,
        companyId: null,
        ceeaGender: '',
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
    // 获取属性拓展字段
    getDimAttrConfig () {
      let parame = {}
      parame.overseasRelation = this.allParam.companyInfo.overseasRelation
      parame.companyType = this.allParam.companyInfo.companyType
      if (parame.overseasRelation === 'OUT') {
        parame.companyType = ''
      }
      vendorOptCommonApi.getConfigByTemplate(parame).then(res => {
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
      let certificationInfo = []

      if (data && data.length > 0) {
        data.forEach(element => {
          if (element.dimCode === 'companyInfo') {
            companyInfo = element.dimFieldConfigS
            if (element.dimFieldConfigS.length > 0) {
              element.dimFieldConfigS.map(item => {
                if (item.isCheck === 'Y') {
                  this.baseDimRules[item.fieldCode] = [
                    {
                      required: true,
                      message: this.$t('common.pleaseEnter') + item.fieldName
                    }
                  ] // 请输入
                }
              })
            }
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
            certificationInfo = element.dimFieldConfigS
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
      this.certificationInfotexts = certificationInfo
    },
    // 营业执照 上传附件成功
    handleUploadSuccess (file) {
      if (!file.file) {
        this.allParam.companyInfo.businessLicenseFileId = null
        this.allParam.companyInfo.businessLicense = null
        return false
      }
      const { fileId, fileName } = file.file || {}
      // 判断是否需要OCR识别 境内供应商 && 开启OCR
      if (this.allParam.companyInfo.overseasRelation === 'INSIDE' && this.isEnableOcr === 'Y') {
        this.fileUploadId = fileId // ocr 文件入参ID
        // 读取图片信息
        this.$http({
          url: '/api-base/ocr/recognizeLcImage',
          method: 'GET',
          params: { fileuploadId: this.fileUploadId },
          loading: true
        })
          .then(res => {
            let licenseData = res.data
            this.allParam.companyInfo.companyName = licenseData.companyName
            this.allParam.companyInfo.legalPerson = licenseData.legalPerson
            this.allParam.companyInfo.lcCode = licenseData.lcCode
            this.allParam.companyInfo.registeredCapital = licenseData.registeredCapital
            this.allParam.companyInfo.registCurrency = licenseData.registCurrency
            this.allParam.companyInfo.companyAddress = licenseData.companyAddress
            this.allParam.companyInfo.businessScope = licenseData.businessScope
            this.allParam.companyInfo.registrationAuthority = licenseData.registrationAuthority
            this.$set(
              this.allParam.companyInfo,
              'businessStartDate',
              this.$dayjs(licenseData.businessStartDate).valueOf(),
            )
            this.$set(
              this.allParam.companyInfo,
              'businessEndDate',
              this.$dayjs(licenseData.businessEndDate).valueOf(),
            )
            this.$set(
              this.allParam.companyInfo,
              'companyCreationDate',
              this.$dayjs(licenseData.companyCreationDate).valueOf(),
            )
            this.$forceUpdate()
          })
          .catch(err => {
            console.log(err)
          })
      }
      this.allParam.companyInfo.businessLicenseFileId = fileId.toString()
      this.allParam.companyInfo.businessLicense = fileName
    },
    innerHandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '', fileType = '' } = file || {}
      row.fileuploadId = fileId.toString()
      row.authType = fileName
    },
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
    orgDel (index, row) {
      this.financeInfoData.forEach((e, index2) => {
        if (e.orgId == row.orgId) {
          this.financeInfoData.splice(index2, 1)
        }
      })
      this.allParam.orgInfos.splice(index, 1)
    },
    orgCategoryDel (index, row) {
      // 删除组织和品类
      let orgCategoryId = row.orgCategoryId
      if (orgCategoryId) {
        vendorGreenApi.orgCategoryDel({ orgCategoryId }).then(res => {
          this.allParam.orgCategorys.splice(index, 1)
          this.$message({
            message: res.message,
            type: 'success'
          })
        })
      } else {
        this.allParam.orgCategorys.splice(index, 1)
      }
    },

    // 查询旧数据
    fatchOldData () {
      let companyId = this.companyId
      if (companyId) {
        vendorOptCommonApi.getCompanyForEdit({ companyId }).then(res => {
          if (res) {
            if (res.data.companyInfo) {
              let changeStatus = res.data.companyInfo.status // 状态
              // 拟定 驳回 撤回 下可以编辑
              if (['DRAFT', 'REJECTED', 'WITHDRAW'].includes(changeStatus)) {
                this.curOpt = 'edit'
              } else {
                this.curOpt = 'view'
              }
              if (res.data.companyInfo.businessStartDate == null) {
                res.data.companyInfo.businessStartDate = ''
              }
              let companyInfo = res.data.companyInfo
              this.curRel = companyInfo.overseasRelation
              this.curType = companyInfo.companyType

              // 查询模板配置信息
              let parame = {}
              parame.overseasRelation = companyInfo.overseasRelation
              parame.companyType = companyInfo.companyType
              if (parame.overseasRelation === 'OUT') {
                parame.companyType = ''
              }
              vendorOptCommonApi.getConfigByTemplate(parame).then(res2 => {
                if (res2) {
                  this.adaptDimFieldsHandle(res2.data) // 适配拓展字段
                  this.dimensionExtension(res2.data, res)
                }
              })
              //
              this.allParam = res.data // yuyue3
              this.allParam.managementInfo = this.allParam.managementInfo || {}

              this.allParam.companyInfo = _omit(companyInfo, [
                'applicationDate',
                'creationDate',
                'lastUpdateDate'
              ])
              if (this.allParam.companyInfo.businessStartDate !== '') {
                // this.allParam.companyInfo.businessDate = []
                let dateStart = new Date(companyInfo.businessStartDate).getTime()
                let dateEnd = new Date(companyInfo.businessEndDate).getTime()
                this.$set(this.allParam.companyInfo, 'businessDate', [dateStart, dateEnd])
              }
              this.allParam.companyInfo.companyCreationDate = companyInfo.companyCreationDate
                ? this.$dayjs(companyInfo.companyCreationDate).valueOf()
                : ''
              this.allParam.companyInfo.businessStartDate = companyInfo.businessStartDate
                ? this.$dayjs(companyInfo.businessStartDate).valueOf()
                : '' // 开始时间
              this.allParam.companyInfo.businessEndDate = companyInfo.businessEndDate
                ? this.$dayjs(companyInfo.businessEndDate).valueOf()
                : ''
              this.allParam.companyInfo.approvedDate = companyInfo.approvedDate
                ? this.$dayjs(companyInfo.approvedDate).valueOf()
                : ''
              this.allParam.companyInfo.backlistUpdatedDate = companyInfo.backlistUpdatedDate
                ? this.$dayjs(companyInfo.backlistUpdatedDate).valueOf()
                : ''
              this.financeInfoData = res.data.financeInfos
                ? this.adaptResutData(res.data.financeInfos) || []
                : []
              this.allParam.orgCategorys = res.data.orgCategorys
                ? this.adaptResutData(res.data.orgCategorys) || []
                : []
              this.allParam.bankInfos = res.data.bankInfos
                ? this.adaptResutData(res.data.bankInfos) || []
                : []
              this.allParam.contactInfos = res.data.contactInfos
                ? this.adaptResutData(res.data.contactInfos) || []
                : []
              this.allParam.orgInfos = res.data.orgInfos
                ? this.adaptResutData(res.data.orgInfos) || []
                : []

              if (this.allParam.companyInfo.companyCountry !== '') {
                this.address.push(companyInfo.companyCountry)
              }
              if (this.allParam.companyInfo.companyProvince !== '') {
                this.address.push(companyInfo.companyProvince)
              }
              if (this.allParam.companyInfo.companyCity !== '') {
                this.address.push(companyInfo.companyCity)
              }
              this.$refs.address.init()

              this.baseDimModel = companyInfo.dimFieldContexts || {} // 拓展字段值
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
            this.userInfo = res.data.userInfo || {}
            if (res.data.orgInfos) {
              for (let e of res.data.orgInfos) {
                this.selectedOrgList.push({
                  orgId: e.orgId,
                  orgCode: e.orgCode,
                  orgName: e.orgName
                })
              }
            }
            if (res.data.fileUploads) {
              this.companyInfoFileList = res.data.fileUploads
            }
            if (res.data.cateJournalList) {
              this.$set(
                this.allParam.companyInfo,
                'categoryName',
                res.data.cateJournalList.map(v => v.categoryName).join(',')
              )
            }
            this.$nextTick(() => {
              this.$refs.sceneAttachment.loadFileInfo()
            })

            // console.log(this.allParam.plantInfos)
            // if (this.allParam.plantInfos.length > 0) {
            //   this.allParam.plantInfos.forEach((e, index) => {
            //     if (e.plantCountry) {
            //       this.$set(e, 'address', [e.plantCountry, e.plantProvince, e.plantCity])
            //     }
            //     this.$nextTick(() => {
            //       this.$refs['address' + index].init()
            //     })
            //   })
            // }

            if (
              (this.allParam.companyInfo.status === 'SUBMITTED' ||
                this.allParam.companyInfo.status === 'WITHDRAW' ||
                this.allParam.companyInfo.status === 'APPROVED' ||
                this.allParam.companyInfo.status === 'REJECTED') &&
              this.curOpt !== 'view'
            ) {
              this.activeTabName = 'workflowTab'
            }
          }
        })
      } else {
        this.$nextTick(() => {
          this.$refs.sceneAttachment.loadFileInfo()
        })
      }
    },
    // 返回数据适配
    adaptResutData (data) {
      if (data.length > 0) {
        data.forEach(elm => {
          let dimFieldData = elm.dimFieldContexts || {}
          Object.keys(dimFieldData).forEach(key => {
            elm[key] = dimFieldData[key]
          })
          delete elm.dimFieldContexts
          // if(elm.)
        })
      }
      return data
    },
    // 财务选择组织
    selectOrgHandle (index, data) {
      this.currentRowIndex = index
      this.curData = this[data]
      this.orgDialog = true
    },
    // 选择组织
    addOrgHandle (e, dd, scope) {
      let oldOrgId = scope.orgId
      scope.orgId = e ? e.organizationId : null
      scope.orgCode = e ? e.organizationCode : ''
      scope.orgName = e ? e.organizationName : ''
      console.log(e)
      this.selectedOrgList = []
      if (e) {
        this.selectedOrgList.push({
          orgId: e.organizationId,
          orgCode: e.organizationCode,
          orgName: e.organizationName
        })
        // 添加到财务信息
        let bolId = false
        this.financeInfoData.forEach((q, index) => {
          if (q.orgId == e.organizationId) {
            bolId = true
          }
          if (oldOrgId == q.orgId) {
            this.financeInfoData.splice(index, 1)
          }
        })
        if (!bolId) {
          this.financeInfoData.push({
            orgId: e.organizationId,
            orgCode: e.organizationCode,
            orgName: e.organizationName,
            add: true,
            fullPathId: e.fullPathId
          })
        }
      } else {
        scope.buCode = null
        scope.buName = null
      }
      if (e && e.organizationId) {
        this.$http({
          url: '/api-base/organization/organization/getBuByOrgId',
          method: 'GET',
          params: { organizationId: e.organizationId },
          loading: true
        })
          .then(data => {
            scope.buCode = data.data.organizationCode
            scope.buName = data.data.organizationName
          })
          .catch(err => {
            console.log(err)
          })
      }
    },
    addOrgHandleAccounting (e, dd, scope) {
      scope.orgId = e ? e.organizationId : null
      scope.orgCode = e ? e.organizationCode : ''
      scope.orgName = e ? e.organizationName : ''
      scope.fullPathId = e ? e.fullPathId : ''
      scope.add = true
    },
    // 选择组织触发
    responsibleFocus (index) {
      this.currentRowIndex = index
      this.orgDialog = true
    },
    // 只允许允许输入数字和字母
    setFormatValue (row) {
      row.authNum = row.authNum.replace(/[\W]/g, '')
    },
    // 计算拓展字段
    computedDimFields (dimdata) {
      let arr = []
      dimdata.map(i => {
        arr.push(i.fieldCode)
      })
      return arr
    },
    // 暂存
    stagingHandle (type) {
      // 新增用户名校验必填
      let overseasRelation = this.allParam.companyInfo.overseasRelation
      let supplierType = this.allParam.companyInfo.supplierType
      if (!overseasRelation || overseasRelation == '' || !supplierType || supplierType == '') {
        this.__jump_error__(
          'companyType',
          null,
          this.$t('请输入完整企业性质数据'),
        )
        return false
      }
      if (this.allParam.orgCategorys.length > 0) {
        let orgBol = false
        this.allParam.orgCategorys.forEach(data => {
          console.log(data)
          if (['', null].includes(data.orgId) || ['', null].includes(data.categoryId)) {
            orgBol = true
          }
        })
        if (orgBol == true) {
          this.__jump_error__(
            'orgCatTable',
            null,
            this.$t('vendorMod.msgOrgCatTableInfo'),
          )
          return false
        }
      }
      this.dataHandle(type)
      // if (this.curOpt === 'add') {
      // } else {
      //   this.dataHandle(type)
      // }
    },
    // 提交
    submitHandle (type) {
      let _this = this
      if (
        (this.allParam.managementInfo.ifIsoQuality === 'Y' ||
          this.allParam.managementInfo.ifIsoEnviron === 'Y' ||
          this.allParam.managementInfo.ifOhsasSafe === 'Y') &&
        this.allParam.managementAttaches.length == 0
      ) {
        this.$message.error(this.$t('vendorMod.authenticationMgs'))
        return false
      }
      // 避免没有保存就提交
      if (this.allParam.companyInfo.companyCreationDate != '' && this.allParam.companyInfo.companyCreationDate) {
        if (this.allParam.companyInfo.companyCreationDate.length > 10) {
          this.allParam.companyInfo.companyCreationDate =
            this.allParam.companyInfo.companyCreationDate.replace(' 00:00:00', '')
        }
      }

      if (!this.companyId) {
        return this.$message.error(this.$t('vendorMod.pleaseSaveBeforeSubmitting')) // 请先保存再进行提交
      }
      // 基础信息
      _this.$refs.baseInfoForm.validate(valid => {
        if (valid) {
          // 固定维度做校验
          // if (this.$refs.test14.check()) {
          //   this.__jump_error__('test14', null, this.$t('common.pleasefinishRequired'))
          //   return false
          // }
          if (this.allParam.orgCategorys.length > 0) {
            let orgBol = false
            this.allParam.orgCategorys.forEach(data => {
              console.log(data)
              if (['', null].includes(data.orgId) || ['', null].includes(data.categoryId)) {
                orgBol = true
              }
            })
            if (orgBol == true) {
              this.__jump_error__(
                'orgCatTable',
                null,
                this.$t('vendorMod.msgOrgCatTableInfo'),
              )
              return false
            }
          }
          if (this.allParam.bankInfos.length == 0) {
            this.__jump_error__(
              'bankInfo',
              null,
              this.$t('vendorMod.msgAtLeastBankInfo'), // '请至少维护一条银行信息'
            )
            return false
          }
          if (this.allParam.contactInfos.length == 0) {
            this.__jump_error__(
              'contactInfo',
              null,
              this.$t('vendorMod.msgAtLeastContactInfo'), // '请至少维护一条联系人信息'
            )
            return false
          }
          if (this.allParam.companyInfo.registCurrency == '') {
            this.__jump_error__(
              'enterpriseThreeCertificates',
              null,
              this.$t('vendorMod.msgCurrencyCode'), // '请选择币种'
            )
            return false
          }
          // 校验厂房信息
          if (this.allParam.plantInfos.length == 0) {
            this.__jump_error__('factoryInfo', null, this.$t('vendorMod.msgAtLeastPlantInfos'))
            return false
          } else {
            let bol = false
            this.allParam.plantInfos.forEach(e => {
              console.log(e)
              if (!e.plantName || !e.plantNature || !e.plantArea || !e.plantAddress) {
                bol = true
              }
            })
            if (bol) {
              this.__jump_error__('factoryInfo', null, this.$t('vendorMod.msgAtLeastPlantInfos2'))
              return false
            }
          }
          // 需要校验数据
          const bankInfosRequiredKeys = [
            { key: 'bankCode', message: '第$index行缺少银行代码' },
            { key: 'bankAccountName', message: '第$index行缺少账号名称' },
            { key: 'bankAccount', message: '第$index行缺少银行账号' },
            { key: 'currencyCode', message: '第$index行缺少币种' }
          ]
          for (const [index, item] of new Map(this.allParam.bankInfos.map((item, index) => [index, item]))) {
            const errorItem = bankInfosRequiredKeys.find(keyItem => !item[keyItem.key])
            if (errorItem) {
              // 替换提示行字符
              this.$message.warning(`银行信息${errorItem.message.replace('$index', index + 1)}`)
              return
            }
          }
          this.commentForm(type) // 提交数据
        } else {
          this.__focus_error__(this.$t('vendorMod.msgCompanyBaseInfo'))
          return false
        }
      })
    },
    commentForm (type) {
      this.dataHandle(type)
      this.loggerComment = false
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
    addFinance () {
      // 添加财务信息
      this.financeInfoData.unshift({
        add: true,
        orgId: null,
        taxKey: '',
        taxRate: ''
      })
    },
    // 删除操作
    financeDel (index, row) {
      let bol = false
      let i = 0
      this.allParam.orgInfos.forEach(u => {
        if (u.orgId == row.orgId) {
          bol = true
        }
      })
      this.financeInfoData.forEach(e => {
        if (e.orgId == row.orgId) {
          i++
        }
      })
      if (bol) {
        if (i > 1) {
          this.financeDelDeep(index, row)
        } else {
          this.$message.error(this.$t('vendorMod.msgDel1'))
        }
      } else {
        this.financeDelDeep(index, row)
      }
    },
    financeDelDeep (index, row) {
      this.financeInfoData.splice(index, 1)
    },
    // 数据处理
    dataHandle (type) {
      let bolError = false
      this.$refs.userInfoForm.validate((valid, object) => {
        if (!valid) {
          this.__focus_error__(this.$t('vendorMod.userInfoFormValidate'))
          bolError = true
        } else {
          let bolBaseInfoForm2 = false
          this.$refs.baseInfoForm2.validate(valid => {
            bolBaseInfoForm2 = valid
          })
          if (!bolBaseInfoForm2) {
            this.$message({
              message: this.$t('请输入企业三证信息'),
              type: 'error'
            })
            bolError = true
          }

          let bolBaseInfoForm3 = false
          this.$refs.baseInfoForm3.validate(valid => {
            bolBaseInfoForm3 = valid
          })
          if (!bolBaseInfoForm3) {
            this.$message({
              message: this.$t('请输入企业性质'),
              type: 'error'
            })
            bolError = true
          }
        }
      })
      if (bolError) {
        return false
      }
      if (this.allParam.companyInfo.companyCreationDate.length > 10) {
        this.allParam.companyInfo.companyCreationDate =
          this.allParam.companyInfo.companyCreationDate.replace(' 00:00:00', '')
      }

      try {
        // 修改营业期限的数据格式
        if (this.allParam.companyInfo.businessDate !== '') {
          this.allParam.companyInfo.businessStartDate = this.allParam.companyInfo.businessDate[0]
          this.allParam.companyInfo.businessEndDate = this.allParam.companyInfo.businessDate[1]
        }
      } catch (e) {
        console.log(e)
      }

      // 校验联系人中默认联系人是否唯一
      if (this.allParam.contactInfos.length > 1) {
        let num = 0 // 默认联系人数量
        this.allParam.contactInfos.forEach((e, index) => {
          console.log(e)
          if (e.ceeaDefaultContact == 'Y') {
            num++
          }
        })
        if (num > 1) {
          this.$message.error(this.$t('dataConfMod.isDefaultMsg'))
          return false
        }
      }

      // 校验财务信息
      let financeInfoDataBol = true
      let financeInfoIds = []
      this.financeInfoData.forEach(u => {
        financeInfoIds.push(u.orgId)
        if (!u.orgId || !u.clearCurrency || !u.paymentMethod || !u.paymentTerms) {
          financeInfoDataBol = false
        }
      })
      if (!financeInfoDataBol) {
        this.$message.error(this.$t('vendorMod.msgFinanceInfo'))
        return false
      }
      let bols = true
      const orgJournals = this.allParam.orgInfos
      try {
        orgJournals.forEach(e => {
          bols = financeInfoIds.includes(e.orgId)
          if (!bols) {
            throw Error()
          }
        })
      } catch (e) {}
      if (!bols) {
        this.$message.error('一个组织至少需要有一个账期')
        return false
      }

      if (this.allParam.orgCategorys.length <= 0) {
        this.$message.error('请输入合作信息')
        return false
      }

      let submitData = {}
      let companyInfo = this.allParam.companyInfo
      let companyInfoDimFields = this.baseDimModel
      companyInfo.dimFieldContexts = companyInfoDimFields // 拓展字段赋值
      companyInfo.categoryRels = this.categoryRelFormat(this.allParam.companyInfo.categoryRels) // 可供品类
      let otherInfo = this.otherModel.otherForm
      otherInfo.dimFieldContexts = this.otherDimModel
      let bankInfos = this.formatDimFields(this.bankDimFieldContexts, this.allParam.bankInfos)

      let contactInfo = this.formatDimFields(
        this.contactDimFieldContexts,
        this.allParam.contactInfos,
      ) // 处理拓展字段
      let financeInfos = this.formatDimFields(this.financeDimFieldContexts, this.financeInfoData)
      let orgInfos = this.formatDimFields(this.orgDimFieldContexts, this.allParam.orgInfos)
      let orgCategorys = this.formatDimFields(
        this.orgCatDimFieldContexts,
        this.allParam.orgCategorys,
      )

      if (type === 'SUBMIT') {
        this.allParam.companyInfo.ceeaDraftsmanOpinion =
          this.inputComment || this.$t('vendorMod.pleaseApproval')
      }

      let _this = this
      let baseData = []
      this.dimensionData.forEach((elemnt, i) => {
        let extendData = 'extendData' + i
        console.log(_this.$refs[extendData])
        baseData = [...baseData, ..._this.$refs[extendData][0].model]
      })
      this.allParam.dimFieldResultList = baseData

      // 营业地址组件修改后适配后端接口
      console.log(this.address)
      if (this.address !== '') {
        let address = this.address
        companyInfo.companyCountry = address[0]
        companyInfo.companyProvince = address[1]
        companyInfo.companyCity = address[2]
      }

      if (this.allParam.plantInfos.length > 0) {
        this.allParam.plantInfos.forEach(e => {
          if (e.address) {
            e.plantCountry = e.address[0]
            e.plantProvince = e.address[1]
            e.plantCity = e.address[2]
          }
        })
      }

      submitData = this.allParam
      submitData.companyInfo = companyInfo
      submitData.bankInfos = bankInfos
      submitData.contactInfos = contactInfo
      submitData.financeInfos = financeInfos
      submitData.orgInfos = orgInfos
      submitData.orgCategorys = orgCategorys
      submitData.otherInfo = otherInfo

      submitData.userInfo = this.userInfo
      submitData.fileUploads = this.companyInfoFileList // 附件对象
      let url = ''
      if (type === 'SUBMIT') {
        // 提交
        url = '/api-sup/info/companyInfo/companyGreenChannelSubmit'
      } else {
        // 暂存
        url = '/api-sup/info/companyInfo/saveCompanyGreenChannel'
      }
      http({
        url: url,
        method: 'POST',
        data: submitData,
        loading: true
      })
        .then(async res => {
          if (res) {
            if (res.code == '0') {
              this.$message.success(res.message)
            } else {
              this.$message.error(res.message)
              return false
            }

            this.companyId = res.data

            if (type === 'SAVE') {
              // 暂存的时候返回公司Id查询一次旧数据
              this.$emit('tab-remove', this.$attrs.params.tabName)
              this.__setTabTodo('vendorGreenChannelList.getQuerydata')
            } else {
              await this.fatchOldData()
              await this.handlerAfter(type)
            }
            // this.saveConfig()
            let dimData = this.getDimDataFromVue(this.companyId)
            modelConfigApi.saveFormResutlForBusiness(dimData)
          }
        })
    },

    saveConfig () {
      let dimData7 = this.$refs.test7?.save(this.companyId)
      let dimData8 = this.$refs.test8?.save(this.companyId)
      let dimData9 = this.$refs.test9?.save(this.companyId)
      let dimData10 = this.$refs.test10?.save(this.companyId)
      let dimData101 = this.$refs.test101?.save(this.companyId)
      let dimData11 = this.$refs.test11?.save(this.companyId)
      let dimData12 = this.$refs.test20?.save(this.companyId)
      let dimData13 = this.$refs.test13?.save(this.companyId)
      let dimData14 = this.$refs.test14?.save(this.companyId)
      let dimData15 = this.$refs.test15?.save(this.companyId)
      let dimDataAll = [
        ...dimData7,
        ...dimData8,
        ...dimData9,
        ...dimData10,
        ...dimData101,
        ...dimData11,
        ...dimData12,
        ...dimData13,
        ...dimData14,
        ...dimData15
      ]
      console.log(dimData14, 'dimData14')
      console.log(dimDataAll, 'dimDataAll')
      modelConfigApi.saveFormResutlForBusiness(dimDataAll)
    },
    getBankObj (val, scope) {
      scope.branchBankId = val ? val.branchBankId : ''
      scope.bankCode = val ? val.bankNum : '' // 银行编号
      scope.bankName = val ? val.bankName : '' // 银行名称
      scope.unionCode = val ? val.branchBankNum : '' // 分行编号
      scope.openingBank = val ? val.branchBankName : '' // 分行名称[开户行名称]
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
    addOrg () {
      // 新增合作组织
      this.allParam.orgInfos.push({
        add: true,
        orgId: null,
        orgCode: null,
        orgName: null,
        buName: null
      })
    },
    addOrgCategory () {
      // 新增组织品类
      this.allParam.orgCategorys.push({
        add: true,
        orgId: null,
        orgCode: null,
        orgName: null,
        categoryId: null,
        categoryCode: null,
        categoryName: null
      })
    },
    setOrgObj (row) {
      let obj = this.selectedOrgList.filter(v => v.orgId === row.orgId)
      if (obj) {
        row.orgCode = obj[0].orgCode
        row.orgName = obj[0].orgName
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
      this.allParam.companyInfo.registeredCapital = licenseData.registeredCapital
      this.allParam.companyInfo.registCurrency = licenseData.registCurrency
      this.allParam.companyInfo.companyAddress = licenseData.companyAddress
      this.allParam.companyInfo.businessScope = licenseData.businessScope
      this.allParam.companyInfo.registrationAuthority = licenseData.registrationAuthority

      this.$set(
        this.allParam.companyInfo,
        'businessStartDate',
        this.$dayjs(licenseData.businessStartDate).valueOf(),
      )
      this.$set(
        this.allParam.companyInfo,
        'businessEndDate',
        this.$dayjs(licenseData.businessEndDate).valueOf(),
      )
      this.$set(
        this.allParam.companyInfo,
        'companyCreationDate',
        this.$dayjs(licenseData.companyCreationDate).valueOf(),
      )
      this.ocrVisible = false
      this.$forceUpdate()
    },
    // 关掉OCR弹框
    ocrClose () {
      this.ocrVisible = false
    },
    // 只允许允许输入数字和字母
    setFormatName () {
      this.userInfo.username = this.userInfo.username.replace(/[\W]/g, '')
    },
    setEmailFormatValue () {
      let pattern = /^[A-Za-z\d]+([-_.][A-Za-z\d]+)*@([A-Za-z\d]+[-.]){1,2}[A-Za-z\d]{2,5}$/g
      if (!pattern.test(this.userInfo.email)) {
        this.$message.warning(this.$t('vendorMod.msgEmailErroe'))
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.registCurrency {
  background-color: #f5f7fa;
}
:deep(.el-input-group__append) {
  background-color: #ffffff;
}
:deep(.el-tab-pane) {
  overflow-y: auto;
  overflow-x: hidden;
}
.app-main .el-container {
  overflow: auto;
  padding-right: 181px;
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
.the-vendorGreenChannelDetail-detail {
  position: relative;
  .sub_header {
    margin: 0 0 8px 0;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
  .stepDiv {
    padding: 15px 10px;
  }
}
.info-fill-progress {
  position: fixed;
  width: 210px;
  top: 104px;
  right: 0px;
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

:deep(.companyInfoFill) {
  padding-bottom: 40px;
}
:deep(.dimension > :first-child) {
  display: none;
}
:deep(.el-checkbox:last-of-type) {
  height: 30px !important;
}
:deep(.categoryName .el-input__inner::-webkit-input-placeholder) {
  color: #303133 !important;
}
</style>
