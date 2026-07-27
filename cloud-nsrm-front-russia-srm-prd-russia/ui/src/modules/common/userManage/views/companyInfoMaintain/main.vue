<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main class="el-main">
      <div :class="['companyInfo-sec', { widthCtrl: stepsActive === 2 }]">
        <!--头部组件-->
        <MainHerder
          :stepsActive="stepsActive"
          :status="allParam.companyInfo.status"
          :flowRemark="allParam.companyInfo.flowRemark"
        />
        <!-- 信息填写区域 -->
        <div class="info-fill-area">
          <!-- 基本信息填写 -->
          <div
            class="companyInfoFill"
          >
            <el-form
              ref="baseInfoForm"
              class="base-form-info form-fill-style"
              :model="allParam.companyInfo"
              :rules="allParam.rules"
              :disabled="allParam.companyInfo.status ==='APPROVED' || allParam.companyInfo.status ==='SUBMITTED'"
              :show-message="true"
            >
              <el-collapse v-model="activeDims">
                <!--企业性质-->
                <el-collapse-item
                  ref="companyType"
                  :title="$t('vendorMod.companyType')"
                  name="1"
                >
                  <srm-row>
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
                        <!-- :disabled="curRel === 'INSIDE'" -->
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
                        <el-radio v-model="allParam.companyInfo.ifLongPeriod" :label="$t('common.Y')" class="formCheckbox">
                          {{ $t('common.yes') }}
                        </el-radio>
                        <el-radio v-model="allParam.companyInfo.ifLongPeriod" :label="$t('common.N')" class="formCheckbox">
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
                        <el-select
                          v-model="allParam.companyInfo.ceeaSupBusinessType"
                          clearable
                          @change="setceeaSupBusinessType"
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
                  name="2"
                >
                  <div style="display: flex;flex-flow: wrap">
                    <!--营业执照上传-->
                    <div style="width: 33%;padding-left: 25px;padding-right: 25px;">
                      <srm-common-file
                        :default-file="{ fileId: allParam.companyInfo.businessLicenseFileId, fileName: allParam.companyInfo.businessLicense }"
                        drag
                        :limit="1"
                        :dragger-options="{
                          width: '100%',
                          height: '312px'
                        }"
                        list-type="picture-card"
                        @on-change="handleUploadSuccess"
                      />
                    </div>
                    <div style="width: 67%">
                      <srm-row>
                        <srm-col :initCol="2">
                          <!-- 企业名称 -->
                          <el-form-item
                            prop="companyName"
                            :label="$t('vendorMod.companyName')"
                          >
                            <!-- :disabled="curRel === 'INSIDE'" -->
                            <el-input v-model="allParam.companyInfo.companyName" />
                          </el-form-item>
                        </srm-col>
                        <srm-col :initCol="2">
                          <!-- 法人代表 -->
                          <el-form-item
                            prop="legalPerson"
                            :label="$t('vendorMod.legalPerson')"
                          >
                            <!-- :disabled="curRel === 'INSIDE'" -->
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
                            <!-- disabled -->
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
                            <!-- :disabled="curRel === 'INSIDE'" -->
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
                            <!-- :disabled="curRel === 'INSIDE'" -->
                            <el-date-picker
                              v-model="allParam.companyInfo.companyCreationDate"
                              type="date"
                              :placeholder="$t('common.pleaseSelectDate')"
                              format="yyyy-MM-dd"
                              value-format="timestamp"
                            />
                          </el-form-item>
                        </srm-col>
                        <srm-col
                          v-if="curType !== 'GETI'"
                          :initCol="2"
                        >
                          <!-- 营业期限 -->
                          <el-form-item
                            prop="businessDate"
                            :label="$t('vendorMod.dateBusiness')"
                          >
                            <!-- :disabled="curRel === 'INSIDE'" -->
                            <!--allParam.companyInfo.businessStartDate-->
                            <!--allParam.companyInfo.businessEndDate-->
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
                            <!-- :disabled="curRel === 'INSIDE'" -->
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
                    </div>
                  </div>
                </el-collapse-item>
                <!--基本信息-->
                <el-collapse-item
                  ref="base"
                  :title="$t('vendorMod.companyBaseInfo2')"
                  name="3"
                >
                  <srm-row>
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
                        <el-input
                          v-model="allParam.companyInfo.ceeaAgentBrand"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :initCol="3">
                      <!-- 主营品类 -->
                      <el-form-item
                        :label="$t('vendorMod.mainCategory')"
                        class="is-required"
                      >
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
                        <el-radio v-model="allParam.companyInfo.ceeaIfListed" :label="$t('common.Y')" class="formCheckbox">
                          {{ $t('common.yes') }}
                        </el-radio>
                        <el-radio v-model="allParam.companyInfo.ceeaIfListed" :label="$t('common.N')" class="formCheckbox">
                          {{ $t('common.no') }}
                        </el-radio>
                      </el-form-item>
                    </srm-col>
                    <srm-col
                      v-if="allParam.companyInfo.ceeaIfListed == 'Y'"
                      :initCol="3"
                    >
                      <!-- 上市时间 -->
                      <el-form-item
                        :label="$t('vendorMod.listedDate')"
                        prop="ceeaListedTime"
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
                        <el-radio v-model="allParam.companyInfo.ceeaHasParentCompany" :label="$t('common.Y')" class="formCheckbox">
                          {{ $t('common.yes') }}
                        </el-radio>
                        <el-radio v-model="allParam.companyInfo.ceeaHasParentCompany" :label="$t('common.N')" class="formCheckbox">
                          {{ $t('common.no') }}
                        </el-radio>
                      </el-form-item>
                    </srm-col>
                    <srm-col
                      v-if="allParam.companyInfo.ceeaHasParentCompany == 'Y'"
                      :initCol="3"
                    >
                      <!-- 母公司名称 -->
                      <el-form-item
                        :label="$t('vendorMod.parentCompanyName')"
                        prop="ceeaParentCompanyName"
                      >
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
                      <el-form-item
                        :label="$t('vendorMod.parentCompanyLcCode')"
                        prop="ceeaParentCompanyLcCode"
                      >
                        <el-input
                          v-model="allParam.companyInfo.ceeaParentCompanyLcCode"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col v-if="allParam.companyInfo.overseasRelation !== 'INSIDE'" :initCol="3">
                      <el-form-item label="D-U-N-S">
                        <el-input v-model="allParam.companyInfo.dunsCode" />
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
                        <el-input
                          v-model="allParam.companyInfo.ceeaCompanyIntro"
                        />
                      </el-form-item>
                    </srm-col>

                    <srm-col :initCol="1">
                      <!-- 拓展字段显示 -->
                      <FormExtend
                        ref="baseDimForm"
                        :form-data="baseInfoDimFieldContexts"
                        :model="baseDimModel"
                        :rules="baseDimRules"
                        :disabled="allParam.companyInfo.status ==='APPROVED' || allParam.companyInfo.status ==='SUBMITTED'"
                      />
                    </srm-col>
                  </srm-row>
                </el-collapse-item>

                <!-- 联系人信息 -->
                <el-collapse-item
                  ref="contact"
                  name="4"
                >
                  <template slot="title">
                    <i class="redFont">*</i>{{ $t('vendorMod.contactInfoRequired') }}
                  </template>
                  <div class="left_div">
                    <p>
                      <el-button
                        type="primary"
                        class="detail-pbtn"
                        @click="addOne"
                      >
                        {{ $t("common.new") }}
                      </el-button>
                    </p>
                    <el-table
                      :data="allParam.contactInfos"
                      style="width: 100%"
                      border
                      max-height="250px"
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
                        width="100"
                        :show-overflow-tooltip="true"
                      >
                        <template #header>
                          <i class="required">*</i>
                          <span>{{ $t("vendorMod.nickname") }}</span>
                        </template>
                        <template slot-scope="scope">
                          <el-input v-model="scope.row.contactName" />
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
                          <el-select v-model="scope.row.ceeaGender" clearable>
                            <el-option
                              v-for="item in genderList"
                              :key="item.id"
                              :label="item.label"
                              :value="item.value"
                            />
                          </el-select>
                        </template>
                      </el-table-column>
                      <!-- 部门 -->
                      <el-table-column
                        align="center"
                        prop="ceeaDeptName"
                        :label="$t('vendorMod.department')"
                        width="100"
                        :show-overflow-tooltip="true"
                      >
                        <template slot-scope="scope">
                          <el-input v-model="scope.row.ceeaDeptName" />
                        </template>
                      </el-table-column>
                      <!-- 职位 -->
                      <el-table-column
                        align="center"
                        prop="position"
                        :label="$t('dataConfMod.position')"
                        width="100"
                        :show-overflow-tooltip="true"
                      >
                        <template slot-scope="scope">
                          <el-input v-model="scope.row.position" />
                        </template>
                      </el-table-column>
                      <!-- 联系方式 -->
                      <el-table-column
                        align="center"
                        prop="ceeaContactMethod"
                        :label="$t('vendorMod.contactMethod')"
                        width="150"
                        :show-overflow-tooltip="true"
                      >
                        <template #header>
                          <i class="required">*</i>
                          <span>{{ $t("vendorMod.contactMethod") }}</span>
                        </template>
                        <template slot-scope="scope">
                          <el-input v-model="scope.row.ceeaContactMethod" @blur="phoneChange(scope.row.ceeaContactMethod)" />
                        </template>
                      </el-table-column>
                      <!-- 邮箱 -->
                      <el-table-column
                        align="center"
                        prop="email"
                        :label="$t('vendorMod.email')"
                        min-width="200"
                        :show-overflow-tooltip="true"
                      >
                        <template #header>
                          <i class="required">*</i>
                          <span>{{ $t("vendorMod.email") }}</span>
                        </template>
                        <template slot-scope="scope">
                          <el-input v-model="scope.row.email" @blur="emailChange(scope.row.email)" />
                        </template>
                      </el-table-column>
                      <!-- 默认联系人 -->
                      <el-table-column
                        align="center"
                        prop="ceeaDefaultContact"
                        :label="$t('dataConfMod.isDefault')"
                        width="100"
                        :show-overflow-tooltip="true"
                      >
                        <template slot-scope="scope">
                          <el-checkbox
                            v-model="scope.row.ceeaDefaultContact"
                            true-label="Y"
                            false-label="N"
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
                          <el-input v-model="scope.row.ceeaComments" />
                        </template>
                      </el-table-column>
                      <!-- 拓展字段 [[-->
                      <template v-if="linkManDimFieldContexts.length > 0">
                        <el-table-column
                          v-for="col in linkManDimFieldContexts"
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
                      <el-table-column
                        fixed="right"
                        :label="$t('common.operation')"
                        width="60"
                      >
                        <template slot-scope="scope">
                          <el-button
                            type="text"
                            @click="handleDelClick(scope.$index, scope.row)"
                          >
                            {{ $t("common.delete") }}
                          </el-button>
                        </template>
                      </el-table-column>
                    </el-table>
                  </div>
                </el-collapse-item>
                <!-- 银行信息 -->
                <el-collapse-item
                  ref="bank"
                  name="5"
                >
                  <template slot="title">
                    <i class="redFont">*</i>{{ $t('vendorMod.bankInfoRequired') }}
                  </template>
                  <div class="left_div">
                    <p>
                      <el-button
                        type="primary"
                        class="detail-pbtn"
                        @click="addOneBank"
                      >
                        {{ $t("common.new") }}
                      </el-button>
                    </p>
                    <el-table
                      :data="allParam.bankInfos"
                      style="width: 100%"
                      border
                      max-height="250px"
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
                        min-width="150"
                        :show-overflow-tooltip="true"
                      >
                        <template #header>
                          <i class="required">*</i>
                          <span>{{ $t("components.bank.bankCode") }}</span>
                        </template>
                        <template slot-scope="scope">
                          <QuickSearch
                            :show-input="scope.row.bankCode"
                            propKey="bankNum"
                            show-key="branchBankNum"
                            :pre-query-data="{ 't.attr1': 'Y' }"
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
                        :label="$t('components.bank.bankName')"
                        min-width="150"
                        :show-overflow-tooltip="true"
                      />
                      <!-- 开户行名称 -->
                      <el-table-column
                        align="center"
                        prop="openingBank"
                        :label="$t('components.bank.branchBankName')"
                        min-width="150"
                        :show-overflow-tooltip="true"
                      />
                      <!-- 分行编码 -->
                      <el-table-column
                        align="center"
                        prop="unionCode"
                        :label="$t('components.bank.unionCode')"
                        min-width="150"
                        :show-overflow-tooltip="true"
                      />
                      <!-- 账户名称 -->
                      <el-table-column
                        align="center"
                        prop="bankAccountName"
                        :label="$t('components.bank.accountName')"
                        min-width="120"
                        :show-overflow-tooltip="true"
                      >
                        <template #header>
                          <i class="required">*</i>
                          <span>{{ $t("components.bank.accountName") }}</span>
                        </template>
                        <template slot-scope="scope">
                          <el-input v-model="scope.row.bankAccountName" />
                        </template>
                      </el-table-column>
                      <!-- 银行账号 -->
                      <el-table-column
                        align="center"
                        prop="bankAccount"
                        :label="$t('components.bank.bankAccount')"
                        min-width="120"
                        :show-overflow-tooltip="true"
                      >
                        <template #header>
                          <i class="required">*</i>
                          <span>{{ $t("components.bank.bankAccount") }}</span>
                        </template>
                        <template slot-scope="scope">
                          <el-input v-model="scope.row.bankAccount" />
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
                          <i class="required">*</i>
                          <span>{{ $t("vendorMod.currencyCode") }}</span>
                        </template>
                        <template slot-scope="scope">
                          <dict-select
                            v-model="scope.row.currencyCode"
                            code="currency"
                          />
                        </template>
                      </el-table-column>
                      <!-- 主账户 -->
                      <el-table-column
                        align="center"
                        prop="ceeaMainAccount"
                        :label="$t('components.bank.isMain')"
                        min-width="120"
                        :show-overflow-tooltip="true"
                      >
                        <template slot-scope="scope">
                          <el-checkbox
                            v-model="scope.row.ceeaMainAccount"
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
                          />
                        </template>
                      </el-table-column>
                      <template v-if="bankDimFieldContexts.length > 0">
                        <!-- 拓展字段 [[-->
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
                              handleDelClickBank(scope.$index, scope.row)
                            "
                          >
                            {{ $t("common.delete") }}
                          </el-button>
                        </template>
                      </el-table-column>
                    </el-table>
                  </div>
                </el-collapse-item>
                <!-- 厂房信息 -->
                <el-collapse-item
                  ref="plantInfos"
                  :title="$t('vendorMod.factoryInfo')"
                  name="6"
                >
                  <div class="left_div">
                    <p>
                      <el-button
                        type="primary"
                        class="detail-pbtn"
                        @click="addOnePlant"
                      >
                        {{ $t("common.new") }}
                      </el-button>
                    </p>
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
                      <!-- 所在国、地区/省/市 -->
                      <el-table-column
                        align="center"
                        prop="address"
                        :label="$t('components.address.addressTitle')"
                        min-width="150"
                        :show-overflow-tooltip="true"
                      >
                        <template #header>
                          <i class="required">*</i>
                          <span>{{ $t("components.address.addressTitle") }}</span>
                        </template>
                        <template slot-scope="scope">
                          <CAddress
                            :ref="'address'+scope.$index"
                            v-model="scope.row.address"
                            style="width: 100%"
                            @change-value="addressChangeList($event,scope.row)"
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
                    :disabled="allParam.companyInfo.status ==='APPROVED' || allParam.companyInfo.status ==='SUBMITTED'"
                    class="rel-form-select"
                  >
                    <srm-row>
                      <srm-col :initCol="3">
                        <!-- 是否通过ISO9001质量体系认证(如是请上传附件) -->
                        <el-form-item>
                          <template #label>
                            {{ $t('vendorMod.msgIfPass1') }}
                            <el-tooltip class="item" effect="dark" :content="$t('vendorMod.msgIfPass0')">
                              <i class="el-icon-warning-outline" />
                            </el-tooltip>
                          </template>
                          <el-radio-group
                            v-model="allParam.managementInfo.ifIsoQuality"
                            @change="managementChange($event, '是否通过ISO9001质量体系认证')"
                          >
                            <el-radio :label="$t('common.Y')">
                              {{
                                $t("common.yes")
                              }}
                            </el-radio>
                            <el-radio :label="$t('common.N')">
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
                              <i class="el-icon-warning-outline" />
                            </el-tooltip>
                          </template>
                          <el-radio-group
                            v-model="allParam.managementInfo.ifIsoEnviron"
                            @change="managementChange($event, '是否通过ISO14001环境体系认证')"
                          >
                            <el-radio :label="$t('common.Y')">
                              {{
                                $t("common.yes")
                              }}
                            </el-radio>
                            <el-radio :label="$t('common.N')">
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
                              <i class="el-icon-warning-outline" />
                            </el-tooltip>
                          </template>
                          <el-radio-group
                            v-model="allParam.managementInfo.ifOhsasSafe"
                            @change="managementChange($event, '是否通过OHSAS18000职业、健康安全体系认证')"
                          >
                            <el-radio :label="$t('common.Y')">
                              {{
                                $t("common.yes")
                              }}
                            </el-radio>
                            <el-radio :label="$t('common.N')">
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
                              <i class="el-icon-warning-outline" />
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
                    <p>
                      <el-button
                        type="primary"
                        class="detail-pbtn"
                        @click="addOneAuth"
                      >
                        {{ $t("common.new") }}
                      </el-button>
                    </p>
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
                  name="7"
                >
                  <FileDynamic
                    ref="sceneAttachment"
                    v-model="companyInfoFileList"
                    scene-module-code="SCENE_SUPPLIER_ATTACHMENT"
                    :business-id="companyId"
                    :editable="allParam.companyInfo.status !=='APPROVED' && allParam.companyInfo.status !=='SUBMITTED'"
                  />
                </el-collapse-item>
                <!-- 付款计划 -->
                <el-collapse-item
                  v-if="false"
                  :title="$t('contractMod.paymentPlan')"
                  name="9"
                >
                  <!-- :illegal="illegal" -->
                  <!-- :contractType="mergeForm.contractType" -->
                  <pay-plan
                    ref="payList"
                    v-model="payPlanData"
                    :context="this"
                    :is-buyer="true"
                    visible
                    @change="payPlanDatachange"
                  />
                </el-collapse-item>

                <!-- 供应商地点信息 -->
                <el-collapse-item
                  v-if="false"
                  :title="$t('vendorMod.vendorSiteInfos')"
                  name="5"
                >
                  <div class="commonPad">
                    <el-button
                      type="primary"
                      class="detail-pbtn"
                      style="margin-right:5px;"
                      :disabled="curOpt === 'view'"
                      @click="addSiteInfo"
                    >
                      {{ $t("common.new") }}
                    </el-button>
                    <el-popover
                      v-model="countryListVisible"
                      placement="top"
                      width="400"
                    >
                      <div style="padding:11px;">
                        <p>
                          <!-- 请选择国家 -->
                          <span style="padding-right:5px;">{{
                            $t("vendorMod.msgSelCountry")
                          }}</span>
                          <DictSelect
                            v-model="globalCountry"
                            code="country"
                          />
                        </p>
                        <p>
                          <!-- 请选择地点名称 -->
                          <span style="padding-right:5px;">{{
                            $t("vendorMod.msgAddressName")
                          }}</span>
                          <el-select
                            v-model="globalAddress"
                            filterable
                            clearable
                          >
                            <el-option
                              v-for="item in addressList"
                              :key="item.id"
                              :label="item.label"
                              :value="item.label"
                            />
                          </el-select>
                        </p>
                        <p>
                          <!-- 请输入详细地址 -->
                          <span style="padding-right:5px;">{{
                            $t("vendorMod.msgDetailAddr")
                          }}</span>
                          <el-input
                            v-model="globalAddressDetail"
                            clearable
                            style="display:block;"
                          />
                        </p>
                      </div>
                      <div style="text-align: right; margin: 0">
                        <el-button
                          type="text"
                          @click="countryListVisible = false"
                        >
                          {{ $t("common.cancel") }}
                        </el-button>
                        <el-button
                          type="primary"
                          @click="batchSelectCountry"
                        >
                          {{ $t("common.confirm") }}
                        </el-button>
                      </div>
                      <!-- 批量维护 -->
                      <el-button
                        slot="reference"
                        type="primary"
                        class="detail-pbtn"
                        :disabled="curOpt === 'view'"
                      >
                        {{ $t("vendorMod.batchMaintain") }}
                      </el-button>
                    </el-popover>
                  </div>
                  <el-table
                    ref="bankTable"
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
                    <el-table-column
                      align="center"
                      type="index"
                      width="50"
                    />
                    <!-- 业务实体 -->
                    <el-table-column
                      align="center"
                      prop="orgId"
                      width="150"
                      :show-overflow-tooltip="true"
                    >
                      <template slot="header">
                        <i class="toRequired">*</i>{{ $t("dataConfMod.orgId") }}
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
                    <el-table-column
                      align="center"
                      prop="vendorSiteCode"
                      min-width="150"
                      :show-overflow-tooltip="true"
                    >
                      <template slot="header">
                        <i class="toRequired">*</i>{{ $t("vendorMod.siteName") }}
                      </template>
                      <template slot-scope="scope">
                        <el-select
                          v-model="scope.row.vendorSiteCode"
                          clearable
                          :disabled="curOpt === 'view'"
                        >
                          <el-option
                            v-for="item in addressList"
                            :key="item.id"
                            :label="item.label"
                            :value="item.label"
                          />
                        </el-select>
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
                        <i class="toRequired">*</i>{{ $t("components.address.country") }}
                      </template>
                      <template slot-scope="scope">
                        <DictSelect
                          v-model="scope.row.country"
                          code="country"
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
                          :code="allParam.companyInfo.companyProvince"
                          custom-select-type="CITY"
                          :disabled="curOpt === 'view' || scope.row.country !== 'CN'"
                        />
                      </template>
                    </el-table-column>
                    <!-- 详细地址 -->
                    <el-table-column
                      align="center"
                      prop="addressDetail"
                      width="150"
                      :show-overflow-tooltip="true"
                    >
                      <template slot="header">
                        <i class="toRequired">*</i>{{ $t("components.address.detailAddress") }}
                      </template>
                      <template slot-scope="scope">
                        <el-input
                          v-model="scope.row.addressDetail"
                          :disabled="curOpt === 'view'"
                        />
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
                        <el-input
                          v-model="scope.row.siteComment"
                          :disabled="curOpt === 'view'"
                        />
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
                    <el-table-column
                      fixed="right"
                      :label="$t('common.operation')"
                      width="60"
                    >
                      <template slot-scope="scope">
                        <el-button
                          v-if="scope.row.enabledDeleteFlag == 'Y'"
                          type="text"
                          :disabled="curOpt === 'view'"
                          @click="handleDelClickSite(scope.$index, scope.row)"
                        >
                          {{ $t("common.delete") }}
                        </el-button>
                      </template>
                    </el-table-column>
                  </el-table>
                  <div style="width: 100%; margin-bottom: 50px">
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
              </el-collapse>
            </el-form>
            <!--            <el-collapse v-model="activeDims">-->
            <el-collapse :value="opened">
              <!-- 维度拓展组 -->
              <el-collapse-item
                v-for="(item, index) in dimensionData"
                :ref="item.dimCode"
                :key="index"
                :class="associationDimension(item.relateDimCode)"
                :title="item.dimName"
                :name="associationDimension2(item.relateDimCode,item.dimCode)"
              >
                <!-- 如果是表格的时候显示 -->
                <TableExtend
                  v-if="item.dimShowType == 'TABLE' && evalF(item.showDimCondition)"
                  :ref="'extendData' + index"
                  :table-data="item.dimFieldConfigS"
                  :index="index"
                  :table-extend-list="tableExtendList"
                  :add-one-table-data="addOneTableData"
                  :model2="Emodel2"
                  :model="item.model"
                  :rules="baseDimRules"
                />
                <!-- 如果是表单的话显示 -->
                <FormExtend2
                  v-if="item.dimShowType == 'FORM'"
                  :ref="'extendData' + index"
                  :form-data="item.dimFieldConfigS"
                  :model="item.model"
                  :model2="Emodel"
                  :rules="baseDimRules"
                  @selectChange="selectChanges($event)"
                />
                <!-- TABLE -->
              </el-collapse-item>
            </el-collapse>
            <el-collapse value="16">
              <!--操作记录-->
              <el-collapse-item
                ref="operationRecord"
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
          </div>
        </div>
        <!-- 填写进度调区域 -->
        <!-- 企业登记节点 -->
        <CFillProgress
          node-name=""
          :data="nodeData"
          :percentage="true"
          @index-click="indexClickTo"
        />
        <CToolbar>
          <template slot="right">
            <el-button
              v-if="allParam.companyInfo.status == '' || allParam.companyInfo.status == 'DRAFT' || allParam.companyInfo.status == 'WITHDRAW' || allParam.companyInfo.status == 'REJECTED'"
              type="default"
              @click="prev"
            >
              {{ $t("common.prevOne") }}
            </el-button>
            <el-button
              v-if="
                allParam.companyInfo.status == '' || allParam.companyInfo.status == 'DRAFT' || allParam.companyInfo.status == 'WITHDRAW'
              "
              @click="stagingHandle"
            >
              {{ $t("common.staging") }}
            </el-button>
            <el-button
              v-if="
                allParam.companyInfo.status == '' || allParam.companyInfo.status == 'DRAFT' || allParam.companyInfo.status == 'WITHDRAW'
              "
              type="primary"
              @click="submitHandle"
            >
              {{ $t("common.submit") }}
            </el-button>
            <el-button
              v-if="
                allParam.companyInfo.status == 'REJECTED'
              "
              type="primary"
              @click="submitHandle"
            >
              {{ $t("common.submit") }}
            </el-button>
            <el-button
              v-if="
                allParam.companyInfo.status == 'SUBMITTED'
              "
              type="primary"
              @click="recallHandle"
            >
              {{ $t("common.recall") }}
            </el-button>
          </template>
        </CToolbar>
      </div>
    </el-main>
  </el-container>
</template>
<script>
import ModelConfigForm from 'mod@/common/userManage/views/ModelConfig/ModelConfigForm'
import ModelConfigTable from 'mod@/common/userManage/views/ModelConfig/ModelConfigTable'
import OrganizationSelector from 'lib@/components/organization-selector'
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
import CToolbar from 'lib@/components/c-toolbar'
import CFillProgress from 'lib@/components/c-fill-progress'
import CAddress from 'lib@/components/c-address'
import Configurationization from 'lib@/components/configurationization'
import CCategorySelect from 'lib@/components/c-category-select'
import COcr from 'lib@/components/c-ocr'
import payPlan from './pay-plan'
import FileDynamic from '@/library/components/c-file-management/file-dynamic'
import MainHerder from './mainHeater'
import { vendorRegisterApi, vendorOptCommonApi } from 'mod@/common/userManage/api'
import { modelConfigApi } from '@/api/modelConfig'
import {
  getDictItem,
  getDictItemList,
  getRegion
} from '@/api/common'
import { adaptDictData } from '@/utils'
import _omit from 'lodash/omit'
import FormExtend from './formExtend'
import FormExtend2 from './formExtend2'
import TableExtend from './tableExtend'
import { setCompanyId } from '@/utils/auth'

export default {
  name: 'CompanyInfoMaintain',
  components: {
    CFillProgress,
    CToolbar,
    COcr,
    FileDynamic,
    QuickSearch,
    OrganizationSelector,
    payPlan,
    FormExtend,
    FormExtend2,
    TableExtend,
    MainHerder,
    Configurationization,
    CAddress,
    CCategorySelect,
    ModelConfigForm,
    ModelConfigTable
  },
  props: {
    allParam: {
      type: Object,
      default: () => {}
    }
  },
  data () {
    return {
      dimDataValue: [],
      modelConfig: {
        dimConfigMap: {},
        formDimVOList: []
      },
      testBol8: true,
      testBol9: true,
      testBol10: true,
      testBol11: true,
      testBol12: true,
      testBol13: true,
      testBol14: true,
      testBol15: true,
      address: [],
      selectChangeData: {},
      pageSize_approvalBiddingItemLisA: 10,
      currentPageA: 1,
      Emodel: [],
      Emodel2: [],
      addressList: [],
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
        reviewAttaches: [],
        reviewFormExps: [],
        fileRecords: [],
        questSupplierList: [],
        opType: ''
      },
      globalAddressDetail: null,
      globalAddress: null,
      globalCountry: null,
      countryListVisible: null,
      payPlanData: [],
      tableExtendList: [], // 扩展table类型数据
      dimensionData: [],
      businessLicenseFileIdNull: '',
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
      stepsActive: 2,
      activeDims: ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16'],
      nodeData: [
        // 进度条节点信息
        { code: 'companyType', name: this.$t('vendorMod.companyType'), percentage: 0 }, // '企业性质'
        { code: 'enterpriseThreeCertificates', name: this.$t('vendorMod.enterpriseThreeCertificates'), percentage: 0 }, // '企业三证'
        { code: 'base', name: this.$t('vendorMod.companyBaseInfo2'), percentage: 0 }, // '基本信息'
        {
          code: 'contact',
          name: this.$t('vendorMod.contactInfo'),
          percentage: 0
        }, // '联系人信息'
        { code: 'bank', name: this.$t('vendorMod.bankInfo'), percentage: 0 }, // '银行信息'
        {
          code: 'plantInfos',
          name: this.$t('vendorMod.factoryInfo'),
          percentage: 0
        }, // '厂房信息'
        {
          code: 'manageSystem',
          name: this.$t('vendorMod.managementSystemInfo'),
          percentage: 0
        }, // '管理体系信息信息'
        {
          code: 'otherFile',
          name: this.$t('vendorMod.otherAttachInfo'),
          percentage: 0
        }, // '其他信息'
        {
          code: 'operationRecord',
          name: this.$t('vendorMod.operationRecord'),
          percentage: 0
        } // '操作记录'
      ],
      relations: [], // 境内外管理
      orgIdList: [],
      natureList: [], // 企业性质
      genderList: [],
      industryTypeList: [],
      businessTypeList: [],
      factoryType: [], // 厂房性质
      bizModel: [], // 商业规模
      businessMode: [], // 商业模式
      employeeQyt: [], // 员工规模
      companyStatus: [], // 营业状态
      relModel: {
        // 境内外关系
        relform: {
          overseasRelation: '',
          companyType: '',
          orgId: '',
          orgCode: '',
          orgName: ''
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
          // orgId: [{ required: true, message: "请选择业务实体" }]
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
        ], // '请输入地址'
        ceeaBusinessModel: [
          { required: true, message: this.$t('vendorMod.msgBizeModel') }
        ], // 请选择商业模式
        ceeaSupBusinessType: [
          { required: true, message: this.$t('vendorMod.msgVBusinessType') } // 请选择供应商业务类型
        ],
        ceeaParentCompanyName: [
          { required: true, message: this.$t('vendorMod.msgPCompany') } // 请输入母公司名称
        ],
        ceeaParentCompanyLcCode: [
          { required: true, message: this.$t('vendorMod.msgPCompanyLcCode') } // 请输入母公司统一信用代码
        ],
        ceeaListedTime: [
          { required: true, message: this.$t('vendorMod.msgListedDate') }
        ], // 请输入上市时间
        ceeaYearTurnover: [
          { required: true, message: this.$t('vendorMod.msgAnnualTurnOver') }
        ], // 请输入年营业额
        ceeaPreThreeYearsSale: [
          { required: true, message: this.$t('vendorMod.msgFirst3YSales') } // 请输入前三年销售额
        ]
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
        businessDate: [
          { required: true, message: this.$t('vendorMod.msgBusinessStartFrom') }
        ],
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
        ]
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
      bankAccountType: [], // 账户类型
      baseDimModel: {}, // 基础信息拓展字段
      baseDimRules: {}, // 基础信息拓展字段规则
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
      // 银行信息 拓展字段
      bankDimFieldContexts: [],
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
      otherDimModel: {},
      certifiDimFieldContexts: [], // 认证情况
      // 附件信息
      attachTableData: [],
      // 文件上传配置信息
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'companyInfoMaintain',
        fileType: 'images'
      },
      curOpt: 'add',
      companyInfoFileList: [], // 绑定子组件的数据
      itemisRequired: false,
      itemisRequired2: false,
      itemisRequired3: false,
      itemisRequired4: false,
      itemisRequired5: false,
      itemisRequired6: false,
      itemisRequired7: false,
      itemisRequired8: false
    }
  },
  computed: {
    opened () {
      return this.dimensionData.map((i) => {
        return i.dimCode
      })
    },
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
  async created () {
    if (this.$store.getters.companyId) {
      this.companyId = this.$store.getters.companyId
    } else {
      this.companyId = this.getcookie('CompanyId')
    }
    this.getDimDataById(this.companyId)
    // 获取动态配置(复制粘贴即可)
    this.getConfig()
    // 以防第一次提交无this.$store.getters.companyId获取
    // let userCompanyId = await getUserInfo()
    // if (userCompanyId && userCompanyId.data){
    //   this.companyId = userCompanyId.data.companyId
    // }
  },
  mounted () {
    this.curOpt = 'view'
    this.fatchDictData() // 字典信息
    this.fatchOldData() // 查询旧数据
    this.switchBaseRules()
  },
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
        this.nodeData.splice(7, 1)
      }
      if (!this.testBol9) {
        this.nodeData.splice(8, 1)
      }
      if (!this.testBol10) {
        this.nodeData.splice(9, 1)
      }
      if (!this.testBol11) {
        this.nodeData.splice(10, 1)
      }
      if (!this.testBol12) {
        this.nodeData.splice(11, 1)
      }
      if (!this.testBol13) {
        this.nodeData.splice(12, 1)
      }
      if (!this.testBol14) {
        this.nodeData.splice(13, 1)
      }
      if (!this.testBol15) {
        this.nodeData.splice(14, 1)
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
    addOnePlant () {
      this.allParam.plantInfos.push({})
    },
    addressChangeList (value, row) {
      row.address = value
    },
    addressChange (value) {
      this.address = value
      this.allParam.companyInfo.companyCountry = value[0]
    },
    // 撤回
    recallHandle () {
      this.$prompt('', '撤回原因', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputType: 'textarea'
      }).then(({ value }) => {
        let obj = {
          companyId: this.allParam.companyInfo.companyId,
          flowRemark: value
        }
        vendorRegisterApi.companyWithdraw(obj).then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.fatchOldData() // 查询旧数据
        })
      })
    },
    // 选择公司的类型
    companyTypeChangeHandle (val) {
      this.allParam.companyInfo.companyType = val
      this.curType = val // 当前公司属性
      this.switchBaseRules()
    },
    phoneChange (data) {
      var reg = /^((0\d{2,3}-\d{7,8})|(1[358479]\d{9}))$/
      if (!reg.test(data)) {
        this.$message.error('请输入正确的手机号码格式')
      }
    },
    emailChange (data) {
      var reg = /\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*/
      if (!reg.test(data)) {
        this.$message.error('请输入正确的电子邮箱格式')
      }
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
    // 判断是否有关联维度
    associationDimension2 (val1, val2) {
      if (val1) {
        return val1
      }
      return val2
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
    getCountry (row) {
      // 选择国外就清理省市区，并且禁用
      if (row.country !== 'CN') {
        row.province = null
        row.city = null
      }
    },
    getCompanyCountry () {
      // 选择国外就清理省市区，并且禁用
      if (this.allParam.companyInfo.companyCountry !== 'CN') {
        this.allParam.companyInfo.companyProvince = null
        this.allParam.companyInfo.companyCity = null
      }
    },
    siteJournalsndex () {
      this.allParams.siteJournals.forEach((item, index) => {
        item.getIndex = index
      })
    },
    handleCurrentChangeA (val) {
      this.currentPageA = val
    },
    // 分页供应商
    handleSizeChangeA (val) {
      this.currentPageA = 1
      this.pageSize_approvalBiddingItemLisA = val
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
        vendorSiteCode: null,
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
        vendorSiteId: null,
        disableDate: null,
        postCode: null,
        siteComment: null
      })
      this.siteJournalsndex()
      if (this.allParams.siteJournals.length === 1) {
        this.handleCurrentChangeA(1)
      }
    },
    payPlanDatachange () {},
    // 扩展表单点击新增
    addOneTableData (indexs) {
      this.tableExtendList[indexs].push({})
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
        } catch (e) {}
      }
    },
    // 计算基本信息字段
    computedBaseFields () {
      let overseasRelation = this.allParam.companyInfo.overseasRelation // 境内外类型
      let companyType = this.allParam.companyInfo.companyType // 公司属性
      let resArr = []
      if (overseasRelation === 'OUT') {
        resArr = [
          'overseasRelation',
          'businessLicenseFileId',
          'companyName',
          'registeredCapital', // 个体户 无
          'registCurrency', // 个体户
          'companyCreationDate',
          'companyShortName',
          'legalPerson',
          'businessStartDate', // 个体户 无
          'ifLongPeriod',
          'businessEndDate', // 个体户 和ifLongPeriod =='N'无
          'companyCountry',
          'companyAddress',
          'dunsCode',
          'ceeaBusinessModel',
          'ceeaSupBusinessType',
          'ceeaPlantType',
          'ceeaPlantArea',
          'ceeaCompanyIntro',
          'businessScope',
          'categoryName',
          'ceeaAgentBrand',
          'ceeaIfListed',
          // 'ceeaListedTime', // ceeaIfListed==Y 有
          'ceeaHasParentCompany',
          // 'ceeaParentCompanyName',// ceeaHasParentCompany==Y 有
          // 'ceeaParentCompanyLcCode',// ceeaHasParentCompany==Y 有
          'ifRad',
          // 'radStaffQuantity',//ifRad=="y" 有
          'staffQuantity',
          'managerQuantity',
          'technicistQuantity',
          'productorQuantity',
          'businessRank',
          'marketShare',
          'internationalTopFive'
        ]
      } else {
        // INSIDE
        if (companyType === 'GETI') {
          // 个体户
          resArr = [
            'overseasRelation',
            'companyType', // 境内 有
            'businessLicenseFileId',
            'companyName',
            'companyCreationDate',
            'companyShortName',
            'lcCode', // 境内 有
            'legalPerson',
            'ifLongPeriod',
            'businessEndDate', // 个体户 和ifLongPeriod =='N'无
            'companyCountry',
            'companyProvince', // 境内 有
            'companyCity', // 境内 有
            'companyAddress',
            'dunsCode',
            'ceeaBusinessModel',
            'ceeaSupBusinessType',
            'ceeaPlantType',
            'ceeaPlantArea',
            'ceeaCompanyIntro',
            'businessScope',
            'categoryName',
            'ceeaAgentBrand',
            'ceeaIfListed',
            // 'ceeaListedTime', // ceeaIfListed==Y 有
            'ceeaHasParentCompany',
            // 'ceeaParentCompanyName',// ceeaHasParentCompany==Y 有
            // 'ceeaParentCompanyLcCode',// ceeaHasParentCompany==Y 有
            'staffQuantity',
            'managerQuantity',
            'technicistQuantity',
            'productorQuantity',
            'ifRad',
            // 'radStaffQuantity',//ifRad=="y" 有
            'businessRank',
            'marketShare',
            'internationalTopFive'
          ]
        } else {
          resArr = [
            'overseasRelation',
            'companyType', // 境内 有
            'businessLicenseFileId',
            'companyName',
            'registeredCapital', // 个体户 无
            'registCurrency', // 个体户
            'companyCreationDate',
            'companyShortName',
            'lcCode', // 境内 有
            'legalPerson',
            'businessStartDate', // 个体户 无
            'ifLongPeriod',
            'businessEndDate', // 个体户 和ifLongPeriod =='N'无
            'companyCountry',
            'companyProvince', // 境内 有
            'companyCity', // 境内 有
            'companyAddress',
            'dunsCode',
            'ceeaBusinessModel',
            'ceeaSupBusinessType',
            'ceeaPlantType',
            'ceeaPlantArea',
            'ceeaCompanyIntro',
            'businessScope',
            'categoryName',
            'ceeaAgentBrand',
            'ceeaIfListed',
            // 'ceeaListedTime', // ceeaIfListed==Y 有
            'ceeaHasParentCompany',
            // 'ceeaParentCompanyName',// ceeaHasParentCompany==Y 有
            // 'ceeaParentCompanyLcCode',// ceeaHasParentCompany==Y 有
            'staffQuantity',
            'managerQuantity',
            'technicistQuantity',
            'productorQuantity',
            'ifRad',
            // 'radStaffQuantity',//ifRad=="y" 有
            'businessRank',
            'marketShare',
            'internationalTopFive'
          ]
        }
      }
      if (this.allParam.companyInfo.ceeaIfListed === 'N') {
        resArr.pop('ceeaListedTime')
      } else {
        resArr.push('ceeaListedTime')
      }
      if (this.allParam.companyInfo.ceeaHasParentCompany === 'N') {
        resArr.pop('ceeaParentCompanyName')
        resArr.pop('ceeaParentCompanyLcCode')
      } else {
        resArr.push('ceeaParentCompanyName')
        resArr.push('ceeaParentCompanyLcCode')
      }
      if (this.allParam.companyInfo.ifRad === 'N') {
        resArr.pop('radStaffQuantity')
      } else {
        resArr.push('radStaffQuantity')
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
    switchBaseRules () {
      this.curRel = this.allParam.curRel
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
        { dictCode: 'BUSINESS_MODEL' }, // 商业模式2
        { dictCode: 'FACTORY_TYPE' }, // 厂房性质
        { dictCode: 'EMPLOYEE_QTY' }, // 员工规模
        { dictCode: 'BANK_ACCOUNT_TYPE' }, // 账户类型
        { dictCode: 'INDUSTRY_TYPE' }, // 行业类型
        { dictCode: 'SUP_BUSINESS_TYPE' },
        { dictCode: 'VENDOR_SITE_CODE' }, // 地址信息
        { dictCode: 'GENDER' },
        { dictCode: 'OU_ROOT_ID' }
      ]
      getDictItemList(dictParamsArr).then(async res => {
        const [
          RELATION,
          COMPANY_NATURE,
          COMPANY_STATUS,
          BIZ_MODEL,
          BUSINESS_MODEL,
          FACTORY_TYPE,
          EMPLOYEE_QTY,
          BANK_ACCOUNT_TYPE,
          INDUSTRY_TYPE,
          SUP_BUSINESS_TYPE,
          VENDOR_SITE_CODE,
          GENDER,
          OU_ROOT_ID
        ] = res.data
        this.addressList = adaptDictData(
          VENDOR_SITE_CODE.VENDOR_SITE_CODE,
          'dict'
        )

        this.relations = adaptDictData(RELATION.RELATION, 'dict')
        this.natureList = adaptDictData(COMPANY_NATURE.COMPANY_NATURE, 'dict')
        this.companyStatus = adaptDictData(
          COMPANY_STATUS.COMPANY_STATUS,
          'dict'
        )
        this.bizModel = adaptDictData(BIZ_MODEL.BIZ_MODEL, 'dict')
        this.businessMode = adaptDictData(
          BUSINESS_MODEL.BUSINESS_MODEL,
          'dict'
        )
        this.factoryType = adaptDictData(FACTORY_TYPE.FACTORY_TYPE, 'dict')
        this.employeeQyt = adaptDictData(EMPLOYEE_QTY.EMPLOYEE_QTY, 'dict')
        this.bankAccountType = adaptDictData(
          BANK_ACCOUNT_TYPE.BANK_ACCOUNT_TYPE,
          'dict'
        )
        this.industryTypeList = adaptDictData(
          INDUSTRY_TYPE.INDUSTRY_TYPE,
          'dict'
        )
        this.businessTypeList = adaptDictData(
          SUP_BUSINESS_TYPE.SUP_BUSINESS_TYPE,
          'dict'
        )
        this.genderList = adaptDictData(GENDER.GENDER, 'dict')

        let orgIdList = []
        let getorgIdListData = await this.$bus.$emit('getorgIdList')

        if (getorgIdListData) {
          orgIdList = getorgIdListData.data || []
        }
        let getOuRootID = adaptDictData(OU_ROOT_ID.OU_ROOT_ID, 'dict')
        if (getOuRootID && getOuRootID.length > 0) {
          getOuRootID.map(item => {
            orgIdList.push({
              organizationId: Number(item.value),
              organizationName: item.label
            })
          })
        }
        this.orgIdList = orgIdList
      })

      // 是否启用OCR
      getDictItem('ENABLE_OCR').then(res => {
        this.isEnableOcr = res.data[0].dictItemCode
      })
    },
    prev () {
      // 上一步
      this.$emit('goToWhere', 'company-nature')
    },
    next () {
      // 下一步
      let _this = this
      _this.$refs.relModel.validate(valid => {
        if (valid) {
          // 查询配置的拓展字段信息
          let pareme = _this.relModel.relform
          if (pareme.overseasRelation === 'OUT') {
            pareme.companyType = ''
          }
          vendorOptCommonApi.getConfigByTemplate(pareme).then(res => {
            if (res && res.data && res.data.length > 0) {
              _this.adaptDimFieldsHandle(res.data) // 适配拓展字段
              _this.dimensionExtension(res.data) // 适配维度扩展字段
            }
          })
          if (_this.stepsActive++ > 2) _this.stepsActive = 1
        } else {
          return false
        }
      })
    },
    addOneBank () {
      this.allParam.bankInfos.unshift({
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
      this.allParam.contactInfos.unshift({
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
    handleDelClickPlant (index, row) {
      this.allParam.plantInfos.splice(index, 1)
    },
    addOneFile () {
      this.otherFileList.push({
        inquiryId: ''
      })
    },
    addOneAuth () {
      this.allParam.managementAttaches.unshift({
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
      this.allParam.businessInfos.unshift({
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
      this.allParam.operationEquipments.unshift({
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
      this.allParam.operationProducts.unshift({
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
      this.allParam.operationQualities.unshift({
        opQualityId: null,
        companyId: null,
        opInfoId: null,
        mainTestEquipment: '',
        mainTestProject: ''
      })
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
      let certify = []
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
                      message: this.$t('common.pleaseInput') + item.fieldName
                    }
                  ]
                }
              })
            }
          } else if (element.dimCode === 'bankInfo') {
            bankInfo = element.dimFieldConfigS
          } else if (element.dimCode === 'linkMan') {
            contactInfo = element.dimFieldConfigS
          } else if (element.dimCode === 'otherInfo') {
            otherInfo = element.dimFieldConfigS
          } else if (element.dimCode === 'certificationInfo') {
            certify = element.dimFieldConfigS
          }
        })
      }
      this.baseInfoDimFieldContexts = companyInfo
      this.bankDimFieldContexts = bankInfo
      this.linkManDimFieldContexts = contactInfo
      this.otherDimFieldContexts = otherInfo
      this.certifiDimFieldContexts = certify
    },
    // 营业执照 上传附件成功
    handleUploadSuccess (file) {
      if (!file.file) {
        this.allParam.companyInfo.businessLicenseFileId = null
        this.allParam.companyInfo.businessLicense = null
        return false
      }
      const { fileId, fileName } = file.file || {}
      let fieldName = this.field
      let fieldID = this.field + 'FileId'
        // 营业执照
      this.allParam.companyInfo.businessLicenseFileId = fileId.toString()
      this.allParam.companyInfo.businessLicense = fileName
        this.allParam.companyInfo[fieldID] = fileId.toString()
        this.allParam.companyInfo[fieldName] = fileName
        // 判断是否需要OCR识别 境内供应商 && 开启OCR
        if (
          this.allParam.companyInfo.overseasRelation === 'INSIDE' &&
          this.isEnableOcr === 'Y'
        ) {
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
              this.allParam.companyInfo.registeredCapital =
                licenseData.registeredCapital
              this.allParam.companyInfo.registCurrency = licenseData.registCurrency
              this.allParam.companyInfo.companyAddress = licenseData.companyAddress
              this.allParam.companyInfo.businessScope = licenseData.businessScope
              this.allParam.companyInfo.registrationAuthority = licenseData.registrationAuthority
              this.$set(this.allParam.companyInfo, 'businessStartDate', this.$dayjs(licenseData.businessStartDate).valueOf())
              this.$set(this.allParam.companyInfo, 'businessEndDate', this.$dayjs(licenseData.businessEndDate).valueOf())
              this.$set(this.allParam.companyInfo, 'companyCreationDate', this.$dayjs(licenseData.companyCreationDate).valueOf())
              this.$forceUpdate()
            })
            .catch(err => {
              console.log(err)
            })
        }
    },
    // 只允许允许输入数字和字母
    setFormatValue (row) {
      row.authNum = row.authNum.replace(/[\W]/g, '')
    },
    // 删除文件
    handleAttachmentRemove () {
      this.allParam.companyInfo.businessLicenseFileId = ''
      this.allParam.companyInfo.businessLicense = ''
    },
    // 查询旧数据
    fatchOldData () {
      let companyId = this.companyId
      if (companyId) {
        this.curOpt = 'edit'
        let overseasRelation = JSON.parse(JSON.stringify(this.allParam.companyInfo.overseasRelation))
        let companyType = JSON.parse(JSON.stringify(this.allParam.companyInfo.companyType))
        vendorOptCommonApi.getCompanyForEdit({ companyId }).then(res => {
          if (res) {
            let p = JSON.parse(JSON.stringify(this.allParam.companyInfo.companyType))
            if (res.data.companyInfo) {
              let companyInfo = res.data.companyInfo
              this.dataStatus = companyInfo.status // 单据状态
              // 拟定 驳回 撤回 下可以编辑
              if (['REJECTED', 'DRAFT', 'WITHDRAW'].includes(this.dataStatus)) {
                this.curOpt = 'edit'
              } else {
                this.curOpt = 'view'
              }

              // 信息状态为提交显示到第四步 ///调试完以后再打开
              if (this.dataStatus === 'SUBMITTED') {
                this.stepsActive = 3
              }
              if (res.data.orgInfos && res.data.orgInfos[0]) {
                this.relModel.relform.orgId = res.data.orgInfos[0].orgId
                this.relModel.relform.orgCode = res.data.orgInfos[0].orgCode
                this.relModel.relform.orgName = res.data.orgInfos[0].orgName
              }
              this.relModel.relform.overseasRelation =
                companyInfo.overseasRelation
              this.relModel.relform.companyType = companyInfo.companyType
              this.curRel = companyInfo.overseasRelation
              this.curType = companyInfo.companyType

              this.allParam = res.data // yuyue3

              // 如果OPENAPI管理体系传入null,处理
              if (!res.data.managementInfo || res.data.managementInfo == '') {
                let objManage = {
                  managementInfoId: null,
                  companyId: null,
                  ifIsoQuality: 'N',
                  ifIsoEnviron: 'N',
                  ifOhsasSafe: 'N',
                  dataSources: '',
                  otherAuthSit: ''
                }
                this.allParam.managementInfo = objManage
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
              this.$refs.address.init()

              this.allParam.companyInfo = _omit(companyInfo, [
                'applicationDate',
                'creationDate',
                'lastUpdateDate'
              ])
              this.allParam.companyInfo.companyCreationDate = companyInfo.companyCreationDate
                ? this.$dayjs(companyInfo.companyCreationDate).valueOf()
                : ''
              this.allParam.companyInfo.businessStartDate = companyInfo.businessStartDate
                ? this.$dayjs(companyInfo.businessStartDate).valueOf()
                : '' // 开始时间
              this.allParam.companyInfo.businessEndDate = companyInfo.businessEndDate
                ? this.$dayjs(companyInfo.businessEndDate).valueOf()
                : ''
              this.baseDimModel = companyInfo.dimFieldContexts // 拓展字段值
              // this.categoryRels = companyInfo.categoryRels
              this.allParam.operationInfo = res.data.operationInfo || {}
              this.allParam.managementInfo = res.data.managementInfo || {}
              this.allParam.companyInfoDetail =
                res.data.companyInfoDetail || {}
              let objAssign1 = Object.assign({}, res.data.operationInfo)
              let objAssign2 = Object.assign({}, res.data.companyInfoDetail)
              this.allParam.companyInfo = Object.assign(
                objAssign1,
                objAssign2,
                this.allParam.companyInfo
              )
              this.setceeaSupBusinessType(
                this.allParam.companyInfo.ceeaSupBusinessType
              )
              this.switchBaseRules()
              if (res.data.cateJournalList) {
                this.$set(
                  this.allParam.companyInfo,
                  'categoryName',
                  res.data.cateJournalList.map(v => v.categoryName).join(',')
                )
              }
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
            this.$nextTick(() => {
              this.$refs.sceneAttachment.loadFileInfo()
            })
            if (res.data.attachFiles) { this.attachTableData = res.data.attachFiles }
            // 查询魔板配置信息
            let pareme = this.relModel.relform
            pareme.companyType = p
            vendorOptCommonApi.getConfigByTemplate(pareme).then(res2 => {
              if (res) {
                this.adaptDimFieldsHandle(res2.data) // 适配拓展字段
                this.dimensionExtension(res2.data, res)
              }
            })

            this.allParam.bankInfos = res.data.bankInfos
              ? this.adaptResutData(res.data.bankInfos)
              : [] // 银行
            this.allParam.contactInfos = res.data.contactInfos
              ? this.adaptResutData(res.data.contactInfos)
              : [] // 联系人
            this.allParam.managementAttaches = res.data.managementAttaches
              ? this.adaptResutData(res.data.managementAttaches)
              : [] // 认证情况
          }
          if (overseasRelation || companyType) {
            this.$set(this.allParam.companyInfo, 'overseasRelation', overseasRelation)
            this.$set(this.allParam.companyInfo, 'companyType', companyType)
          }
        })
      } else {
        let overseasRelation = JSON.parse(JSON.stringify(this.allParam.companyInfo.overseasRelation))
        let companyType = JSON.parse(JSON.stringify(this.allParam.companyInfo.companyType))
        this.relModel.relform.overseasRelation = overseasRelation
        this.relModel.relform.companyType = companyType
        let pareme = this.relModel.relform
        vendorOptCommonApi.getConfigByTemplate(pareme).then(res2 => {
            this.adaptDimFieldsHandle(res2.data) // 适配拓展字段
            this.dimensionExtension(res2.data, null)
        })
        this.$nextTick(() => {
          this.$refs.sceneAttachment.loadFileInfo()
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
            val.map(v => v.categoryName).join(',')
          )
        } else {
          this.allParam.cateJournalList = [
            {
              categoryId: val.categoryId,
              categoryCode: val.categoryCode,
              categoryName: val.categoryName
            }
          ]
          this.$set(
            this.allParam.companyInfo,
            'categoryName',
            val.categoryName
          )
        }
      }
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
          this.businessLicenseFileIdNull = '' // 营业执照样式
          // 校验联系人信息
          if (this.allParam.contactInfos.length === 0) {
            this.$message.warning(this.$t('dashboard.addContactInformation'))
            return
          }
          // 需要校验数据
          const contactInfosRequiredKeys = [
            { key: 'contactName', message: '第$index行缺少姓名' },
            { key: 'ceeaContactMethod', message: '第$index行缺少联系方式' },
            { key: 'email', message: '第$index行缺少邮箱' }
          ]
          for (const [index, item] of new Map(this.allParam.contactInfos.map((item, index) => [index, item]))) {
            const errorItem = contactInfosRequiredKeys.find(keyItem => !item[keyItem.key])
            if (errorItem) {
              // 替换提示行字符
              this.$message.warning(`联系人信息${errorItem.message.replace('$index', index + 1)}`)
              return
            }
          }

          // 校验厂房信息
          if (this.allParam.plantInfos.length == 0) {
            this.$message({
              message: this.$t('vendorMod.msgAtLeastPlantInfos'),
              type: 'error'
            })
            return false
          } else {
            let bol = false
            this.allParam.plantInfos.forEach((e) => {
              console.log(e)
              if (!e.plantName || !e.plantNature || !e.plantArea || !e.address || !e.plantAddress) {
                bol = true
              }
            })
            if (bol) {
              this.$message({
                message: this.$t('vendorMod.msgAtLeastPlantInfos2'),
                type: 'error'
              })
              return false
            }
          }

          // 校验银行信息
          if (this.allParam.bankInfos.length === 0) {
            this.$message.warning('请添加银行信息')
            return
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

          // 校验管理体系信息
          let bolManage = false
          this.allParam.managementAttaches.forEach((e) => {
            if (e.documentInspection && !e.fileuploadId) {
              bolManage = e.documentInspection
            }
          })
          if (bolManage) {
            this.$message.error('请上传' + bolManage + '附件')
            return false
          }

          // 固定维度做校验(此处是售后服务维度)
          // if (this.$refs.test14.check()) {
          //   this.$message.error(this.$t('common.pleasefinishRequired'))
          //   return false
          // }

          this.dataHandle('submit') // 提交数据
        } else {
          this.$message({
            message: this.$t('common.pleasefinishRequired'), // '请输入必填项'
            type: 'error'
          })
          if (!this.allParam.companyInfo.businessLicenseFileId) {
            this.businessLicenseFileIdNull = 'businessLicenseFileIdNull'
          } else {
            this.businessLicenseFileIdNull = ''
          }
          return false
        }
      })
    },
    setceeaSupBusinessType (val) {
      this.itemisRequired = !![
        'PRODUCTION',
        'DEVICE',
        'GENERAL',
        'SPARE_PART',
        'SERVICE'
      ].includes(val)
      this.itemisRequired2 = !!['PRODUCTION', 'DEVICE'].includes(val)
      this.itemisRequired3 = !!['PRODUCTION', 'DEVICE', 'GENERAL'].includes(val)
      this.itemisRequired4 = !!['PRODUCTION', 'DEVICE', 'SPARE_PART'].includes(
        val
      )
      this.itemisRequired5 = !![
        'PRODUCTION',
        'DEVICE',
        'SPARE_PART',
        'SERVICE'
      ].includes(val)
      this.itemisRequired6 = !!['PRODUCTION', 'DEVICE', 'LOGISTIC'].includes(val)
      this.itemisRequired7 = !!['LOGISTIC'].includes(val)
      this.itemisRequired8 = !![
        'PRODUCTION',
        'DEVICE',
        'GENERAL',
        'SPARE_PART',
        'SERVICE',
        'LOGISTIC'
      ].includes(val)
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
    // 数据处理
    dataHandle (type) {
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

      let objAssign1 = {
        ceeaCompanyCreationDate: this.allParam.companyInfo
          .ceeaCompanyCreationDate,
        ceeaRegisteredCapital: this.allParam.companyInfo.ceeaRegisteredCapital,
        ceeaYearTurnover: this.allParam.companyInfo.ceeaYearTurnover,
        ceeaPreThreeYearsSale: this.allParam.companyInfo.ceeaPreThreeYearsSale,
        ceeaPreThreeYearsProfit: this.allParam.companyInfo
          .ceeaPreThreeYearsProfit,
        ceeaPreThreeYearsAal: this.allParam.companyInfo.ceeaPreThreeYearsAal,
        ceeaScopeBusinessRatio: this.allParam.companyInfo
          .ceeaScopeBusinessRatio,
        ceeaIfHasSolarPower: this.allParam.companyInfo.ceeaIfHasSolarPower,
        ceeaUpDownLayout: this.allParam.companyInfo.ceeaUpDownLayout,
        ceeaThreeScaleChangeExp: this.allParam.companyInfo
          .ceeaThreeScaleChangeExp,
        ceeaReducePurCostAdvise: this.allParam.companyInfo
          .ceeaReducePurCostAdvise,
        ceeaProCostPlanStrategy: this.allParam.companyInfo
          .ceeaProCostPlanStrategy,
        ceeaRdSaleRate: this.allParam.companyInfo.ceeaRdSaleRate,
        ceeaProGoodBad: this.allParam.companyInfo.ceeaProGoodBad,
        ceeaProTechRoute: this.allParam.companyInfo.ceeaProTechRoute,
        ceeaTeamShapeAbility: this.allParam.companyInfo.ceeaTeamShapeAbility,
        ceeaProPriceInscapeRate: this.allParam.companyInfo
          .ceeaProPriceInscapeRate,
        ceeaReduceCostFactor: this.allParam.companyInfo.ceeaReduceCostFactor,
        ceeaHowUpgradePrice: this.allParam.companyInfo.ceeaHowUpgradePrice,
        ceeaAfterSalesAbility: this.allParam.companyInfo.ceeaAfterSalesAbility
      }
      this.allParam.operationInfo = Object.assign(
        this.allParam.operationInfo,
        objAssign1
      )
      let objAssign2 = {
        staffQuantity: this.allParam.companyInfo.staffQuantity,
        technicistQuantity: this.allParam.companyInfo.technicistQuantity,
        productorQuantity: this.allParam.companyInfo.productorQuantity,
        ifRad: this.allParam.companyInfo.ifRad,
        radStaffQuantity: this.allParam.companyInfo.radStaffQuantity,
        managerQuantity: this.allParam.companyInfo.managerQuantity,
        businessRank: this.allParam.companyInfo.businessRank,
        marketShare: this.allParam.companyInfo.marketShare,
        internationalTopFive: this.allParam.companyInfo.internationalTopFive
      }
      this.allParam.companyInfoDetail = Object.assign(
        this.allParam.companyInfoDetail,
        objAssign2
      )

      let baseDimForm = this.$refs.baseDimForm.saveAndSubmit() // 企业信息扩展
      this.allParam.companyInfo.dimFieldContexts = baseDimForm

      let _this = this
      let baseData = []
      this.dimensionData.forEach((elemnt, i) => {
        let extendData = 'extendData' + i
        baseData = [...baseData, ..._this.$refs[extendData][0].model]
      })
      this.allParam.dimFieldResultList = baseData
      // let dimensionsForm = this.$refs.dimensionsForm.saveAndSubmit();

      // 修改营业期限的数据格式
      if (this.allParam.companyInfo.businessDate !== '') {
        this.allParam.companyInfo.businessStartDate = this.allParam.companyInfo.businessDate[0]
        this.allParam.companyInfo.businessEndDate = this.allParam.companyInfo.businessDate[1]
      }

      // 营业地址组件修改后适配后端接口
      if (this.address !== '') {
        let address = this.address
        this.allParam.companyInfo.companyCountry = address[0]
        this.allParam.companyInfo.companyProvince = address[1]
        this.allParam.companyInfo.companyCity = address[2]
      }

      if (this.allParam.plantInfos.length > 0) {
        this.allParam.plantInfos.forEach((e) => {
          if (e.address) {
            e.plantCountry = e.address[0]
            e.plantProvince = e.address[1]
            e.plantCity = e.address[2]
          }
        })
      }

      this.submitData = this.allParam

      let contactInfo = this.formatDimFields(
        this.linkManDimFieldContexts,
        this.allParam.contactInfos
      ) // 联系人处理拓展字段
      let bankInfos = this.formatDimFields(
        this.bankDimFieldContexts,
        this.allParam.bankInfos
      ) // 银行信息处理字段
      let managementAttaches = this.formatDimFields(
        this.certifiDimFieldContexts,
        this.allParam.managementAttaches
      ) // 认证状况处理字段
      // let financeInfos = this.formatDimFields(this.financeDimFieldContexts, financeInfoData) //财务信息处理字段

      this.submitData.bankInfos = bankInfos
      this.submitData.contactInfos = contactInfo
      this.submitData.managementAttaches = managementAttaches
      this.submitData.fileUploads = this.companyInfoFileList // 附件对象
      this.submitData.companyPayPlanList = this.payPlanData
      this.submitData.siteInfos = this.allParams.siteJournals

      if (type === 'submit') {
        // 必填附件做校验
        vendorOptCommonApi.submitCompany(this.submitData).then(res => {
          if (res) {
            if (res.code === '0') {
              this.companyId = res.data
              this.$store.commit('user/SET_COMPANYID', this.companyId)
              this.$emit('goToWhere', 'success')
              let dimData = this.getDimDataFromVue(this.companyId)
              modelConfigApi.saveFormResutlForBusiness(dimData)
            } else {
              this.$message(res.message)
            }
          }
        })
      } else {
        vendorOptCommonApi.saveOrUpdateCompany(this.submitData).then(res => {
          if (res) {
            if (res.code === '0') {
              this.$message({
                message: res.message,
                type: 'success'
              })
              this.companyId = res.data
              setCompanyId(this.companyId)
              this.$store.commit('user/SET_COMPANYID', this.companyId)
              this.fatchOldData() // 暂存后查询旧数据
              let dimData = this.getDimDataFromVue(this.companyId)
              modelConfigApi.saveFormResutlForBusiness(dimData)
            }
          } else {
            this.$message(res.message)
          }
        })
      }
    },
    saveConfig () {
      let dimData7 = this.$refs.test7.save(this.companyId)
      let dimData8 = this.$refs.test8.save(this.companyId)
      let dimData9 = this.$refs.test9.save(this.companyId)
      let dimData10 = this.$refs.test10.save(this.companyId)
      let dimData101 = this.$refs.test101.save(this.companyId)
      let dimData11 = this.$refs.test11.save(this.companyId)
      let dimData12 = []
      try {
        dimData12 = this.$refs.test20.save(this.companyId)
      } catch (e) {

      }
      let dimData13 = this.$refs.test13.save(this.companyId)
      let dimData14 = this.$refs.test14.save(this.companyId)
      let dimData15 = this.$refs.test15.save(this.companyId)
      let dimDataAll = [...dimData7, ...dimData8, ...dimData9, ...dimData10, ...dimData101, ...dimData11, ...dimData12, ...dimData13, ...dimData14, ...dimData15]
      modelConfigApi.saveFormResutlForBusiness(dimDataAll)
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
      this.allParam.companyInfo.registrationAuthority = licenseData.registrationAuthority
      this.$set(this.allParam.companyInfo, 'businessStartDate', this.$dayjs(licenseData.businessStartDate).valueOf())
      this.$set(this.allParam.companyInfo, 'businessEndDate', this.$dayjs(licenseData.businessEndDate).valueOf())
      this.$set(this.allParam.companyInfo, 'companyCreationDate', this.$dayjs(licenseData.companyCreationDate).valueOf())
      this.ocrVisible = false
      this.$forceUpdate()
    },
    getBankObj (val, scope) {
      scope.branchBankId = val ? val.branchBankId : ''
      scope.bankCode = val ? val.bankNum : '' // 银行编号
      scope.bankName = val ? val.bankName : '' // 银行名称
      scope.unionCode = val ? val.branchBankNum : '' // 分行编号
      scope.openingBank = val ? val.branchBankName : '' // 分行名称[开户行名称]
    },
    // 关掉OCR弹框
    ocrClose () {
      this.ocrVisible = false
    },
    getcookie (objname) { // 获取指定名称的cookie的值
      var arrstr = document.cookie.split('; ')
      for (var i = 0; i < arrstr.length; i++) {
        var temp = arrstr[i].split('=')
        if (temp[0] == objname) return unescape(temp[1])
      }
    }
  }
}
</script>
<style lang="scss" scoped>
:deep(.rel-form-select .el-form-item__label){
  float: initial;
}
:deep(.comInfosteps){
  margin-right: 18%;
}
.companyInfo-sec {
  min-height: 100%;
  .comInfosteps {
    padding: 12px 4%;
  }
  .info-fill-area {
    padding: 20px 220px 20px 0;
  }
  .overseasRelation {
    padding-top: 100px;
    width: 50%;
    margin: 0 auto;
  }
  .companyInfoFill {
    padding-right: 0;
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
    top: 0;
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
  &.widthCtrl {
    .comInfosteps {
      width: 75.5%;
      // width: 100%;
    }
  }
  .opt-row {
    margin-bottom: 10px;
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
.left_div p {
  margin: 0 0 8px 0 !important;
}

.businessLicenseFileIdNull {
  .el-upload {
    .el-button.upload-file-btn {
      border-color: red;
    }
  }
}
.required {
  color: #ff4949;
  padding-right: 2px;
}
.dimension>:first-child{
  display: none;
}
.app-main-container{
  padding: 0 0 0 10px;
}
.redFont{
  color: red;
  margin-right: 3px;
}
:deep(.categoryName .el-input__inner::-webkit-input-placeholder){
  color: #303133 !important;
}
</style>
