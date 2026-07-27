// by-zhaomz1
<template>
  <el-container
    class="the-vendorGreenChannelDetail-detail"
    direction="vertical"
  >
    <el-main class="el-main">
      <div class="companyInfoFill">
        <el-collapse
          v-model="activeDims"
          class="tab-form-style"
        >
          <!-- 供应商账号信息 -->
          <el-collapse-item
            :title="$t('vendorMod.vendorUserInfo')"
            name="1"
          >
            <el-form
              ref="form1"
              :model="userInfo"
              class="form-fill-style"
              disabled
            >
              <srm-row :gutter="50">
                <srm-col :initCol="3">
                  <!-- 用户名 -->
                  <el-form-item :label="$t('vendorMod.userName')">
                    <el-input v-model="userInfo.username" />
                  </el-form-item>
                </srm-col>
                <srm-col
                  v-if="curOpt === 'add'"
                  :initCol="3"
                >
                  <!-- 密码 -->
                  <el-form-item :label="$t('vendorMod.pass')">
                    <el-input v-model="userInfo.password" />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-form>
          </el-collapse-item>
          <!-- 黑名单信息 -->
          <el-collapse-item
            :title="$t('vendorMod.blackListInfo')"
            name="11"
          >
            <el-form
              ref="form1"
              :model="baseInfoModel.baseInfoForm"
              class="form-fill-style"
              disabled
            >
              <srm-row :gutter="50">
                <srm-col :initCol="3">
                  <!-- 是否黑名单 -->
                  <el-form-item :label="$t('vendorMod.isBacklist')">
                    <el-switch
                      v-model="baseInfoModel.baseInfoForm.isBacklist"
                      active-value="Y"
                      inactive-value="N"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="3">
                  <!-- 黑名单更新日期 -->
                  <el-form-item :label="$t('vendorMod.blackListUpdateDate')">
                    <el-date-picker
                      v-model="baseInfoModel.baseInfoForm.backlistUpdatedDate"
                      type="date"
                      :placeholder="$t('common.pleaseSelectDate')"
                      format="yyyy-MM-dd"
                      value-format="timestamp"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="3">
                  <!-- 黑名单更新人 -->
                  <el-form-item :label="$t('vendorMod.blackListUpdatePerson')">
                    <el-input v-model="baseInfoModel.baseInfoForm.backlistUpdatedBy" />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-form>
          </el-collapse-item>
          <!-- 企业基本信息 -->
          <el-collapse-item
            ref="base"
            :title="$t('vendorMod.companyBaseInfo')"
            name="2"
          >
            <el-form
              class="base-form-info form-fill-style"
              :model="baseInfoModel.baseInfoForm"
              :rules="baseInfoModel.rules"
              :show-message="false"
              disabled
            >
              <srm-row :gutter="50">
                <srm-col :initCol="3">
                  <!-- 境内外关系 -->
                  <el-form-item
                    prop="overseasRelation"
                    :label="$t('vendorMod.overseasRelation')"
                  >
                    <el-select
                      v-model="baseInfoModel.baseInfoForm.overseasRelation"
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
                <srm-col
                  v-if="curRel ==='INSIDE'"
                  :initCol="3"
                >
                  <!-- 企业性质 -->
                  <el-form-item
                    prop="companyType"
                    :label="$t('vendorMod.companyType')"
                  >
                    <el-select
                      v-model="baseInfoModel.baseInfoForm.companyType"
                      @change="companyTypeChangeHandle"
                    >
                      <el-option
                        v-for="item in natureList"
                        :key="item.id"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="3">
                  <!-- 营业执照 -->
                  <el-form-item
                    prop="businessLicenseFileId"
                    :label="$t('vendorMod.businessLicense')"
                  >
                    <div>
                      <div class="download-link-wrap">
                        <c-download-link
                          :id="baseInfoModel.baseInfoForm.businessLicenseFileId"
                          :name="baseInfoModel.baseInfoForm.businessLicense"
                          ellipsis
                          class="download-link-item"
                        />
                      </div>
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
                  v-if="curType!=='GETI'"
                  :initCol="3"
                >
                  <!-- 注册资本(万元) -->
                  <el-form-item
                    prop="companyName"
                    :label="$t('vendorMod.registeredCapital')"
                  >
                    <el-input
                      v-model="baseInfoModel.baseInfoForm.registeredCapital"
                      class="input-with-select"
                    >
                      <dict-select
                        slot="append"
                        v-model="baseInfoModel.baseInfoForm.registCurrency"
                        code="currency"
                        :placeholder="$t('vendorMod.currencyCode')"
                        style="width: 110px;"
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
                      format="yyyy-MM-dd"
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
                  v-if="curRel ==='INSIDE'"
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
                  v-if="curRel ==='OUT'"
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
                  v-if="curType!=='GETI'"
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
                      format="yyyy-MM-dd"
                      value-format="timestamp"
                    />
                  </el-form-item>
                </srm-col>
                <!-- 个体户不用显示 -->
                <srm-col
                  v-if="curType!=='GETI'"
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
                      format="yyyy-MM-dd"
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
                    <el-select
                      v-model="baseInfoModel.baseInfoForm.companyCountry"
                      :disabled="curRel ==='INSIDE'"
                    >
                      <el-option
                        v-for="item in countryList"
                        :key="item.id"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </el-form-item>
                </srm-col>
                <srm-col
                  v-if="curRel ==='INSIDE'"
                  :initCol="3"
                >
                  <!-- 省份/州 -->
                  <el-form-item
                    prop="companyProvince"
                    :label="$t('vendorMod.province')"
                  >
                    <el-select
                      v-model="baseInfoModel.baseInfoForm.companyProvince"
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
                  v-if="curRel === 'INSIDE'"
                  :initCol="3"
                >
                  <!-- 城市 -->
                  <el-form-item
                    prop="companyCity"
                    :label="$t('vendorMod.city')"
                  >
                    <el-select v-model="baseInfoModel.baseInfoForm.companyCity">
                      <el-option
                        v-for="item in cityList"
                        :key="item.id"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </el-form-item>
                </srm-col>
                <srm-col :span="24">
                  <!-- 详细地址 -->
                  <el-form-item
                    prop="companyAddress"
                    :label="$t('components.address.detailAddress')"
                  >
                    <el-input v-model="baseInfoModel.baseInfoForm.companyAddress" />
                  </el-form-item>
                </srm-col>
                <srm-col :span="16">
                  <!-- 营业范围 -->
                  <el-form-item
                    prop="businessScope"
                    :label="$t('vendorMod.businessScope')"
                  >
                    <el-input v-model="baseInfoModel.baseInfoForm.businessScope" />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="3">
                  <!-- 营业状态 -->
                  <el-form-item
                    prop="companyStatus"
                    :label="$t('vendorMod.businessStatus')"
                  >
                    <el-select v-model="baseInfoModel.baseInfoForm.companyStatus">
                      <el-option
                        v-for="item in companyStatus"
                        :key="item.id"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="3">
                  <!-- 准入日期 -->
                  <el-form-item
                    prop="approvedDate"
                    :label="$t('vendorMod.permitDate')"
                  >
                    <el-date-picker
                      v-model="baseInfoModel.baseInfoForm.approvedDate"
                      type="date"
                      :placeholder="$t('common.pleaseSelectDate')"
                      format="yyyy-MM-dd"
                      value-format="timestamp"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :span="16">
                  <!-- 可供品类 -->
                  <el-form-item :label="$t('vendorMod.categoryRels')">
                    <treeselect
                      v-model="baseInfoModel.baseInfoForm.categoryRels"
                      disabled
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
            <el-form v-if="baseInfoDimFieldContexts.length>0">
              <srm-row :gutter="50">
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
          <!-- 银行信息 -->
          <el-collapse-item
            ref="bank"
            :title="$t('vendorMod.bankInfo')"
            name="4"
          >
            <el-table
              ref="bankTable"
              :data="bankData"
              style="width: 100%"
              border
              max-height="250"
            >
              <!-- 分配组织 -->
              <el-table-column
                align="center"
                prop="orgName"
                :label="$t('vendorMod.assignOrg')"
                min-width="200"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <span>{{ scope.row.orgName }}</span>
                </template>
              </el-table-column>
              <!-- 开户行 -->
              <el-table-column
                align="center"
                prop="openingBank"
                :label="$t('vendorMod.openingBank')"
                width="200"
              >
                <template slot-scope="scope">
                  <span>{{ scope.row.openingBank }}</span>
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
                  <span>{{ scope.row.unionCode }}</span>
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="swiftCode"
                label="SWIFT CODE"
                width="200"
              >
                <template slot-scope="scope">
                  <span>{{ scope.row.swiftCode }}</span>
                </template>
              </el-table-column>
              <!-- 账户名称 -->
              <el-table-column
                align="center"
                prop="bankAccountName"
                :label="$t('vendorMod.bankAccountName')"
                width="200"
              >
                <template slot-scope="scope">
                  <span>{{ scope.row.bankAccountName }}</span>
                </template>
              </el-table-column>
              <!-- 银行账号 -->
              <el-table-column
                align="center"
                prop="bankAccount"
                :label="$t('vendorMod.bankAccount')"
                width="200"
              >
                <template slot-scope="scope">
                  <span>{{ scope.row.bankAccount }}</span>
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
                    disabled
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
                  <el-select
                    v-model="scope.row.accountType"
                    disabled
                  >
                    <el-option
                      v-for="item in bankAccountType"
                      :key="item.id"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </template>
              </el-table-column>
              <!-- 上传开户证明 -->
              <el-table-column
                :label="$t('vendorMod.proof')"
                width="180"
              >
                <template slot-scope="scope">
                  <div
                    class="download-link-wrap"
                  >
                    <c-download-link
                      :id="scope.row.proofFileId"
                      :name="scope.row.proof"
                      ellipsis
                      class="download-link-item"
                    />
                  </div>
                </template>
              </el-table-column>
              <!-- 拓展字段 [[-->
              <el-table-column
                v-for="col in bankDimFieldContexts"
                v-if="bankDimFieldContexts.length>0"
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
            </el-table>
          </el-collapse-item>
          <!-- 联系人信息 -->
          <el-collapse-item
            ref="contact"
            :title="$t('vendorMod.contactInfo')"
            name="5"
          >
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
                :label="$t('vendorMod.contactName')"
                width="100"
              >
                <template slot-scope="scope">
                  <span>{{ scope.row.contactName }}</span>
                </template>
              </el-table-column>
              <!-- 手机号码 -->
              <el-table-column
                align="center"
                prop="mobileNumber"
                :label="$t('vendorMod.mobilePhone')"
                width="150"
              >
                <template slot-scope="scope">
                  <span>{{ scope.row.mobileNumber }}</span>
                </template>
              </el-table-column>
              <!-- 座机号码 -->
              <el-table-column
                align="center"
                prop="phoneNumber"
                :label="$t('vendorMod.telPhone')"
                width="150"
              >
                <template slot-scope="scope">
                  <span>{{ scope.row.phoneNumber }}</span>
                </template>
              </el-table-column>
              <!-- 邮件地址 -->
              <el-table-column
                align="center"
                prop="email"
                :label="$t('vendorMod.email')"
                width="150"
              >
                <template slot-scope="scope">
                  <span>{{ scope.row.email }}</span>
                </template>
              </el-table-column>
              <!-- 联系人地址 -->
              <el-table-column
                align="center"
                prop="contactAddress"
                :label="$t('vendorMod.contactAddr')"
                width="150"
              >
                <template slot-scope="scope">
                  <span>{{ scope.row.contactAddress }}</span>
                </template>
              </el-table-column>
              <!-- 人员职务 -->
              <el-table-column
                align="center"
                prop="position"
                :label="$t('vendorMod.position')"
                width="150"
              >
                <template slot-scope="scope">
                  <span>{{ scope.row.position }}</span>
                </template>
              </el-table-column>
              <!-- 传真号码 -->
              <el-table-column
                align="center"
                prop="taxNumber"
                :label="$t('vendorMod.taxNumber')"
                width="150"
              >
                <template slot-scope="scope">
                  <span>{{ scope.row.taxNumber }}</span>
                </template>
              </el-table-column>
              <!-- 拓展字段 [[-->
              <el-table-column
                v-for="col in contactDimFieldContexts"
                v-if="contactDimFieldContexts.length>0"
                :key="col.fieldId"
                :prop="col.fieldCode"
                :label="col.fieldName"
                min-width="140px"
              >
                <template slot-scope="scope">
                  <span>{{ scope.row[col.fieldCode] }}</span>
                </template>
              </el-table-column>
              <!-- 拓展字段 ]]]-->
            </el-table>
          </el-collapse-item>
          <!-- 合作组织 -->
          <el-collapse-item
            ref="org"
            :title="$t('vendorMod.cooOrg')"
            name="6"
          >
            <el-table
              ref="orgTable"
              :data="orgInfoData"
              style="width: 100%"
              border
              max-height="250"
            >
              <!-- 合作组织 -->
              <el-table-column
                align="center"
                prop="orgName"
                :label="$t('vendorMod.cooOrg')"
                min-width="200"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <span> {{ scope.row.orgName }}</span>
                </template>
              </el-table-column>
              <!-- 组织状态 -->
              <el-table-column
                align="center"
                prop="serviceStatus"
                :label="$t('vendorMod.orgServiceStatus')"
                width="200"
                :formatter="filterOrgHandler"
              />
              <!-- 生效日期 -->
              <el-table-column
                align="center"
                prop="startDate"
                :label="$t('vendorMod.startDate')"
                width="160"
              >
                <template slot-scope="scope">
                  <span>{{ scope.row.startDate }}</span>
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
                  <span>{{ scope.row.endDate }}</span>
                </template>
              </el-table-column>
              <!-- 拓展字段 [[-->
              <el-table-column
                v-for="col in orgDimFieldContexts"
                v-if="orgDimFieldContexts.length>0"
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
            </el-table>
          </el-collapse-item>
          <!-- 组织与品类 -->
          <el-collapse-item
            ref="orgCat"
            :title="$t('vendorMod.orgCat')"
            name="7"
          >
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
                prop="orgName"
                :label="$t('vendorMod.cooOrg')"
                min-width="200"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <span> {{ scope.row.orgName }}</span>
                </template>
              </el-table-column>
              <!-- 采购品类 -->
              <el-table-column
                prop="categoryName"
                :label="$t('vendorMod.category')"
              >
                <template slot-scope="scope">
                  <span>{{ scope.row.categoryName }}</span>
                </template>
              </el-table-column>
              <!-- 品类状态 -->
              <el-table-column
                align="center"
                prop="serviceStatus"
                :label="$t('vendorMod.catServiceStatus')"
                width="150"
                :formatter="filterCatHandler"
              />
              <!-- 生效日期 -->
              <el-table-column
                align="center"
                prop="startDate"
                :label="$t('vendorMod.startDate')"
                width="160"
              >
                <template slot-scope="scope">
                  <span>{{ scope.row.startDate }}</span>
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
                  <span>{{ scope.row.endDate }}</span>
                </template>
              </el-table-column>
              <!-- 拓展字段 [[-->
              <el-table-column
                v-for="col in orgCatDimFieldContexts"
                v-if="orgCatDimFieldContexts.length>0"
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
              disabled
            >
              <srm-row :gutter="50">
                <srm-col :initCol="3">
                  <!-- 商业模式 -->
                  <el-form-item :label="$t('vendorMod.bizModel')">
                    <el-select v-model="otherModel.otherForm.bizModel">
                      <el-option
                        v-for="item in bizModel"
                        :key="item.id"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="3">
                  <!-- 厂房性质 -->
                  <el-form-item :label="$t('vendorMod.factoryType')">
                    <el-select v-model="otherModel.otherForm.factoryType">
                      <el-option
                        v-for="item in factoryType"
                        :key="item.id"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
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
                    <el-select v-model="otherModel.otherForm.employeeQty">
                      <el-option
                        v-for="item in employeeQyt"
                        :key="item.id"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
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
            <el-form v-if="otherDimFieldContexts.length>0">
              <srm-row :gutter="50">
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
            <scene-attachment
              ref="sceneAttachment"
              sence-code="companyInfoMaintain"
              :business-id="companyId"
              :att-opt="curOpt"
              :up-file-info="fileInfo"
              :file-refresh="fileRefresh"
            />
          </el-collapse-item>
          <!-- 操作历史 -->
          <el-collapse-item
            ref="actLog"
            :title="$t('vendorMod.actLog')"
            name="10"
          >
            <el-table
              ref="changeLogData"
              stripe
              border
              :data="changeLogData"
              width="100%"
              max-height="250px"
            >
              <!-- 处理人 -->
              <el-table-column
                :label="$t('vendorMod.operator')"
                prop="operatorName"
                width="180"
              />
              <!-- :formatter="logType" -->
              <!-- 操作类型 -->
              <el-table-column
                prop="operationType"
                :label="$t('vendorMod.operationType')"
              />
              <!-- 状态 -->
              <el-table-column
                prop="operationStatus"
                :label="$t('vendorMod.operationStatus')"
                width="180"
                :formatter="statusType"
              />
              <!-- 处理时间 -->
              <el-table-column
                prop="operationDate"
                :label="$t('vendorMod.operationDate')"
              />
              <!-- 说明 -->
              <el-table-column
                prop="operationMemo"
                :label="$t('vendorMod.operationMemo')"
              />
            </el-table>
          </el-collapse-item>
        </el-collapse>
      </div>
      <!-- 企业登记节点 -->
      <c-fill-progress
        ref="profileProgress"
        :node-name="$t('vendorMod.companyRegisterNode')"
        :data="nodeData"
        :percentage="false"
        @index-click="indexClickTo"
      />
      <c-toolbar v-if="dataStatus==='SUBMITTED'">
        <template slot="right">
          <el-button
            v-if="dataStatus==='SUBMITTED'"
            type="primary"
            @click="rejectedHandle"
          >
            {{ $t('common.toRefuse') }}
          </el-button>
        </template>
      </c-toolbar>
      <!-- 驳回弹框 -->
      <!-- 驳回说明 -->
      <srm-dialog
        :title="$t('vendorMod.refuseMemo')"
        :visible.sync="rejectedDialog"
        size="small"
      >
        <el-form
          ref="rejectedForm"
          :model="rejectedModel.rejectedForm"
          :rules="rejectedModel.rules"
        >
          <el-form-item
            :label="$t('vendorMod.refuseMemo')"
            prop="operationMemo"
          >
            <el-input
              v-model="rejectedModel.rejectedForm.operationMemo"
              type="textarea"
            />
          </el-form-item>
        </el-form>
        <div
          slot="footer"
          class="dialog-footer"
        >
          <el-button @click="rejectedDialog = false">
            {{ $t('common.cancel') }}
          </el-button>
          <el-button
            type="primary"
            @click="rejectedComfirm"
          >
            {{ $t('common.confirm') }}
          </el-button>
        </div>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import CFillProgress from 'lib@/components/c-fill-progress'
import CDownloadLink from 'lib@/components/c-download-link'
import CToolbar from 'lib@/components/c-toolbar'
import SceneAttachment from 'mod@/vendorManagementBuyer/components/SceneAttachment'
import Treeselect, { LOAD_CHILDREN_OPTIONS } from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import { adaptDictData } from '@/utils'
import { getDictItem, getDictItemList, getRegion } from '@/api/common'
import { getConfigByTemplate, getCompanyForEdit, saveOrUpdateCompany, submitCompany } from 'mod@/userManage/api/userManage'
import { rejectCompanyInfo, getCompanyStatusLog } from 'mod@/vendorManagementBuyer/api/vendorApi'
import { getCatChildrenData } from 'mod@/basicSetting/api/baseSetting'
import _omit from 'lodash/omit'

export default {
  name: 'VendorProfileDetail',
  components: {
    CFillProgress,
    CDownloadLink,
    CToolbar,
    Treeselect,
    SceneAttachment
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      companyId: null,
      curRel: '',
      curType: '',
      curOpt: 'view',
      dataStatus: '',
      orgDialog: false,
      activeDims: ['1', '11', '2', '3', '4', '5', '6', '7', '8', '9', '10'],
      nodeData: [ // 进度条节点信息
        { code: 'base', name: this.$t('vendorMod.companyBaseInfo') }, // '企业基本信息'
        { code: 'finance', name: this.$t('vendorMod.financeInfo') }, // '财务信息'
        { code: 'bank', name: this.$t('vendorMod.bankInfo') }, // '银行信息'
        { code: 'contact', name: this.$t('vendorMod.contactInfo') }, // '联系人信息'
        { code: 'org', name: this.$t('vendorMod.cooOrg') }, // '合作组织'
        { code: 'orgCat', name: this.$t('vendorMod.orgCat') }, // '组织与品类'
        { code: 'other', name: this.$t('vendorMod.otherInfo') }, // '其他信息'
        { code: 'attach', name: this.$t('vendorMod.sceneAttachmentInfo') }, // '相关认证信息'
        { code: 'actLog', name: this.$t('vendorMod.actLog') }// '操作历史'
      ],
      relations: [], // 境内外管理
      natureList: [], // 企业性质
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
      approveStatus: [], // 操作状态
      categoryTreeOptions: [],
      userInfo: { // 账号信息
        username: '',
        password: ''
      },
      baseInfoModel: {// 基础信息 baseInfoModel.rules
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
        overseasRelation: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        companyName: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        companyCountry: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        companyAddress: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }]
      },
      outRules: {
        registeredCapital: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        companyCreationDate: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        legalPerson: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        businessScope: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        companyStatus: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }]
      },
      sosoRules: {
        overseasRelation: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        companyType: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        businessLicenseFileId: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        companyCreationDate: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        businessScope: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        companyStatus: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }]
      },
      innerRules: {
        businessStartDate: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        businessEndDate: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        companyType: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        lcCode: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        legalPerson: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        businessLicenseFileId: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        registeredCapital: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        companyCreationDate: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        businessScope: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        companyStatus: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        companyProvince: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        companyCity: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        registrationAuthority: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }]
      },
      unProfitRules: {
        companyType: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        companyProvince: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        companyCity: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }]
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
      otherModel: { // 其他信息
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
      attachTableData: [], // 附件
      rejectedDialog: false,
      rejectedModel: { // 驳回信息
        rejectedForm: {
          operationMemo: ''
        },
        rules: {
          operationMemo: [{ required: true, message: this.$t('vendorMod.msgRefuseMemo') }]// '请输入驳回说明'
        }
      },
      changeLogData: [], // 操作记录
      // 文件上传配置信息
      fileInfo: {
        uploadType: 'FASTDFS', // 固定参数
        sourceType: 'WEB_APP', // 固定参数
        fileModular: 'sup', // 文件所属模块 -》基础模块
        fileFunction: 'companyInfoMaintain', // 文件所属功能
        fileType: 'images' // 文件所属类型
      },
      fileRefresh: false // 刷新附件
    }
  },
  computed: {},
  created () {
    this.fatchDictData() // 字典
    this.switchBaseRules() // 切换基础信息的必填项
    if (this.$attrs.params.flag === 'edit') {
      this.curOpt = 'view'
      this.companyId = this.$attrs.params.companyId
      this.fatchOldData() // 查询旧数据
      this.fatchChangeLog() // 查询操作记录
    }
    // 加载一级可选品类
    getCatChildrenData({ categoryId: -1 }).then(response => {
      this.categoryTreeOptions = response.data.map(i => ({ ...i, children: null }))
    })
  },
  methods: {
    logType (row, column, cellValue, index) {
      if (row.operatorType === 'VENDOR') {
        return this.$t('common.vendor')// '供应商'
      } else { // BUYER
        return this.$t('common.buyer')// '采购商'
      }
    },
    statusType (row, column, cellValue, index) {
      let rowDict = this.approveStatus.find(item => {
        return item.value === cellValue
      })
      if (rowDict) {
        return rowDict.label
      } else {
        return cellValue
      }
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
        getCatChildrenData({ categoryId: parentNode.categoryId }).then(res => {
          parentNode.children = res.data.map(i => ({ ...i, children: null }))
          callback()
        }).catch(err => {
          parentNode.children = null
          callback(new Error(err.message))
        })
      }
    },
    // 切换必填规则
    switchBaseRules () {
      if (this.curRel === 'OUT') { // 境外
        this.baseInfoModel.rules = Object.assign(this.baseRules, this.outRules)
      } else {
        if (this.curType === 'GETI') { // 个体户
          this.baseInfoModel.rules = Object.assign(this.baseRules, this.sosoRules)
        } else if (this.curType === 'FEIYINGLI') { // 非盈利
          this.baseInfoModel.rules = Object.assign(this.baseRules, this.unProfitRules)
        } else { // 其他内部
          this.baseInfoModel.rules = Object.assign(this.baseRules, this.innerRules)
        }
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
        { dictCode: 'PAYMENT_METHOD' }, // 付款方式
        { dictCode: 'INVOICE_LIMIT' }, // 发票限额
        { dictCode: 'PAYMENT_TERMS' }, // 付款条件
        { dictCode: 'CATEGORY_STATUS' }, // 品类状态
        { dictCode: 'ORG_STATUS' }, // 组织服务状态
        { dictCode: 'APPROVE_STATUS_TYPE' } // 审批状态
      ]
      getDictItemList(dictParamsArr).then(res => {
        const [RELATION, COMPANY_NATURE, COMPANY_STATUS, BIZ_MODEL, FACTORY_TYPE, EMPLOYEE_QTY, BANK_ACCOUNT_TYPE, PAYMENT_METHOD,
          INVOICE_LIMIT, PAYMENT_TERMS, CATEGORY_STATUS, ORG_STATUS, APPROVE_STATUS_TYPE] = res.data
        this.relations = adaptDictData(RELATION.RELATION, 'dict')
        this.natureList = adaptDictData(COMPANY_NATURE.COMPANY_NATURE, 'dict')
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
        this.approveStatus = adaptDictData(APPROVE_STATUS_TYPE.APPROVE_STATUS_TYPE, 'dict')
      })

      // 国家
      getDictItem('country').then(res => {
        this.countryList = adaptDictData(res.data, 'dict')
      })
      // 加载省
      getRegion({ queryType: 'province' }).then(res => {
        if (res.data) {
          this.provinceList = this.adaptProvinceCity(res.data, 'province')
        }
      })
    },
    // 组织状态显示
    filterOrgHandler (row, column, cellValue, index) {
      let rowDict = this.orgStatus.find(item => {
        return item.value === row.serviceStatus
      })
      if (rowDict) {
        return rowDict.label
      } else {
        return cellValue
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
    // 适配省 市
    adaptProvinceCity (data, type) {
      let arr = []
      if (data && data.length > 0) {
        if (type === 'province') { // 省
          data.forEach(element => {
            arr.push({
              id: element.provinceId,
              value: (element.provinceId).toString(),
              label: element.province
            })
          })
        } else if (type === 'city') { // 市
          data.forEach(element => {
            arr.push({
              id: element.cityId,
              value: (element.cityId).toString(),
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
      getRegion(parame).then(res => {
        if (res.data) {
          this.cityList = this.adaptProvinceCity(res.data, 'city')
        }
      })
    },
    // 省下拉加载市
    provinceChangeHandle (val) {
      this.getRegionData(val)
    },
    overseasChangeHandle (val) {
      this.curRel = val // 当前海内外关系
      if (val !== 'OUT') { // 境外
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
      getConfigByTemplate(parame).then(res => {
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
        data.forEach(element => {
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
    // 查询旧数据
    fatchOldData () {
      let companyId = this.companyId
      if (companyId) {
        getCompanyForEdit({ companyId }).then(res => {
          if (res) {
            if (res.data.companyInfo) {
              let companyInfo = res.data.companyInfo
              this.curRel = companyInfo.overseasRelation
              this.curType = companyInfo.companyType
              this.dataStatus = companyInfo.status

              // 加载市
              if (companyInfo.companyProvince) {
                let companyProvince = companyInfo.companyProvince
                this.getRegionData(companyProvince) // 加载市
              }

              // 查询模板配置信息
              let pareme = {}
              pareme.overseasRelation = companyInfo.overseasRelation
              pareme.companyType = companyInfo.companyType
              if (pareme.overseasRelation === 'OUT') {
                parame.companyType = ''
              }
              getConfigByTemplate(pareme).then(res => {
                if (res) {
                  this.adaptDimFieldsHandle(res.data) // 适配拓展字段
                }
              })

              this.baseInfoModel.baseInfoForm = _omit(companyInfo, ['applicationDate', 'creationDate', 'lastUpdateDate'])
              this.baseInfoModel.baseInfoForm.companyCreationDate = companyInfo.companyCreationDate ? this.$dayjs(companyInfo.companyCreationDate).valueOf() : ''
              this.baseInfoModel.baseInfoForm.businessStartDate = companyInfo.businessStartDate ? this.$dayjs(companyInfo.businessStartDate).valueOf() : '' // 开始时间
              this.baseInfoModel.baseInfoForm.businessEndDate = companyInfo.businessEndDate ? this.$dayjs(companyInfo.businessEndDate).valueOf() : ''
              this.baseInfoModel.baseInfoForm.approvedDate = companyInfo.approvedDate ? this.$dayjs(companyInfo.approvedDate).valueOf() : ''
              this.baseInfoModel.baseInfoForm.backlistUpdatedDate = companyInfo.backlistUpdatedDate ? this.$dayjs(companyInfo.backlistUpdatedDate).valueOf() : ''
              this.baseDimModel = companyInfo.dimFieldContexts // 拓展字段值
              this.baseInfoModel.baseInfoForm.categoryRels = companyInfo.categoryRels
            }
            if (res.data.otherInfo) {
              let otherInfo = res.data.otherInfo
              this.otherModel.otherForm.bizModel = otherInfo.bizModel
              this.otherModel.otherForm.floorArea = otherInfo.floorArea
              this.otherModel.otherForm.factoryType = otherInfo.factoryType
              this.otherModel.otherForm.employeeQty = otherInfo.employeeQty
              this.otherModel.otherForm.companySite = otherInfo.companySite
              this.otherModel.otherForm.floorSpace = otherInfo.floorSpace
              this.otherDimModel = otherInfo.dimFieldContexts // 拓展字段值
            }
            if (res.data.userInfo) {
              this.userInfo.username = res.data.userInfo.username
            }
            // this.bankData = res.data.bankInfos ? this.adaptResutData(res.data.bankInfos) : []
            // this.contactData = res.data.contactInfos ? this.adaptResutData(res.data.contactInfos) : []
            // this.financeInfoData = res.data.financeInfos ? this.adaptResutData(res.data.financeInfos) : []
            this.bankData = res.data.bankInfos || []
            this.contactData = res.data.contactInfos || []
            this.orgInfoData = res.data.orgInfos || []
            this.orgCategoryData = res.data.orgCategorys || []
            // this.orgInfoData = res.data.orgInfos ? this.adaptResutData(res.data.orgInfos) : []
            // this.orgCategoryData = res.data.orgCategorys ? this.adaptResutData(res.data.orgCategorys) : []
            this.attachTableData = res.data.attachFiles ? res.data.attachFiles : []
          }
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
        })
        return data
      }
    },
    // 查询操作记录
    fatchChangeLog () {
      let companyId = this.companyId
      getCompanyStatusLog({ companyId }).then(res => {
        if (res) {
          this.changeLogData = res.data
        }
      })
    },
    // 财务选择组织
    selectOrgHandle (index, data) {
      this.currentRowIndex = index
      this.curData = this[data]
      this.orgDialog = true
    },
    // 选择组织
    addOrgHandle (e, dd, scope) {
      scope.organizationId = e ? e.organizationId : ''
      scope.orgCode = e ? e.organizationCode : ''
      scope.orgName = e ? e.organizationName : ''
      scope.parentOrgId = e ? e.parentOrganizationId : ''
      scope.parentOrgCode = e ? e.parentOrganizationCode : ''
      scope.parentOrgName = e ? e.parentOrganizationName : ''
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
      Object.keys(formObj).forEach(key => {
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
        data.forEach(item => {
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
      companyInfo.categoryRels = this.categoryRelFormat(this.categoryRels) // 可供品类
      let otherInfo = this.otherModel.otherForm
      let bankInfos = this.bankData
      let contactInfo = this.formatDimFields(this.linkManDimFieldContexts, this.contactData) // 处理拓展字段
      let financeInfos = this.financeInfoData
      let orgInfos = this.orgInfoData
      let orgCategorys = this.orgCategoryData
      let attachFiles = this.attachTableData

      submitData.userInfo = this.userInfo
      submitData.companyInfo = companyInfo
      submitData.bankInfos = bankInfos
      submitData.contactInfos = contactInfo
      submitData.financeInfos = financeInfos
      submitData.orgInfos = orgInfos
      submitData.orgCategorys = orgCategorys
      submitData.otherInfo = otherInfo
      submitData.attachFiles = attachFiles
      if (type === 'submit') {
        submitCompany(submitData).then(res => {
          if (res) { // 提交
            this.$message({
              message: res.message,
              type: 'success'
            })
          }
        })
        this.$emit('tab-remove', this.$attrs.params.tabName)
        this.__setTabTodo('vendorProfileList.getQuerydata')
      } else { // 暂存
        saveOrUpdateCompany(submitData).then(res => {
          if (res) {
            this.$message({
              message: res.message,
              type: 'success'
            })
          }
        })
      }
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
          })
          modelData[index].dimFieldContexts = dyObj
        })
      }
      return modelData
    },
    // 驳回操作
    rejectedHandle () {
      this.rejectedDialog = true
    },
    // 确认驳回
    rejectedComfirm () {
      this.$refs.rejectedForm.validate(valid => {
        let _this = this
        if (valid) {
          let parame = {}
          parame.companyId = _this.companyId
          parame.operationMemo = _this.rejectedModel.rejectedForm.operationMemo
          rejectCompanyInfo(parame).then(res => {
            _this.$message({
              message: res.message,
              type: 'success'
            })
            _this.rejectedDialog = false
            _this.$emit('tab-remove', _this.$attrs.params.tabName)
          })
        }
      })
    },
    // 点击右边进度调跳转到对应的区域
    indexClickTo (code) {
      let anchorEle = this.$refs[code].$el
      if (anchorEle) {
        anchorEle.scrollIntoView(true)
      }
    }
  }
}
</script>
<style scoped lang="scss">
  .the-vendorGreenChannelDetail-detail {
    .sub_header {
      margin: 0 0 10px;
    }
    .el-table .el-date-editor {width:135px}
  }
  .companyInfoFill{
    padding-right:24%;
    padding-bottom: 40px;
  }
  .download-link-wrap .close-icon{
    cursor: pointer;
    display: inline-block;
    vertical-align: middle;
  }
</style>
