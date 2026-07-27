<template>
  <el-contain class="flex-container">
    <el-mian class="el-main">
      <div class="companyInfo-sec widthCtrl">
        <!-- 信息填写区域 -->
        <div class="info-fill-area">
          <!-- 基本信息填写 -->
          <div class="companyInfoFill">
            <el-collapse v-model="activeDims">
              <!-- 企业基本信息 -->
              <el-form
                ref="baseInfoForm"
                style="padding:15px"
                class="base-form-info form-fill-style"
                :model="allParam.companyInfo"
                :rules="allParam.rules"
                :show-message="false"
              >
                <srm-row :gutter="50">
                  <el-collapse-item
                    ref="base"
                    :title="$t('vendorMod.companyBaseInfo')"
                    name="1"
                  >
                    <!-- 状态更新日期 -->
                    <srm-col :span="8">
                      <el-form-item :label="$t('vendorMod.statusUpdateDate')">
                        <el-input
                          v-model="allParam.companyInfo.lastUpdateDate"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <!-- 状态更新人 -->
                    <srm-col :span="8">
                      <el-form-item :label="$t('vendorMod.statusUpdateBy')">
                        <el-input
                          v-model="allParam.companyInfo.lastUpdatedBy"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :span="8">
                      <!-- 企业名称 -->
                      <!-- 供应商分级 -->
                      <el-form-item :label="$t('vendorMod.vendorClassification')">
                        <el-select v-model="allParam.companyInfo.vendorClassification">
                          <el-option
                            v-for="item in vendorClassificationList"
                            :key="item.id"
                            :label="item.label"
                            :value="item.value"
                          />
                        </el-select>
                      </el-form-item>
                    </srm-col>
                    <srm-col :span="8">
                      <!-- 境内外关系 -->
                      <el-form-item
                        prop="overseasRelation"
                        :label="$t('vendorMod.overseasRelation')"
                      >
                        <el-select
                          v-model="allParam.companyInfo.overseasRelation"
                          disabled
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
                      :span="8"
                    >
                      <!-- 企业性质 -->
                      <el-form-item
                        prop="companyType"
                        :label="$t('vendorMod.companyType')"
                      >
                        <el-select
                          v-model="allParam.companyInfo.companyType"
                          disabled
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
                    <srm-col :span="8">
                      <!-- 营业执照 -->
                      <el-form-item
                        prop="businessLicenseFileId"
                        :label="$t('vendorMod.businessLicense')"
                      >
                        <c-upload-file
                          v-if="!allParam.companyInfo.businessLicenseFileId"
                          :limit="1"
                          :show-file-list="false"
                          :cus-data="fileInfo"
                          :accept-file-type="acceptFileType"
                          @upload-success="handleUploadSuccess"
                          @upload-progress="handleScriptProgress"
                          @button-click="buttonClick('baseInfo','businessLicense')"
                        />
                        <div
                          v-else
                          class="download-link-wrap"
                        >
                          <c-download-link
                            :id="allParam.companyInfo.businessLicenseFileId"
                            :name="allParam.companyInfo.businessLicense"
                            class="download-link-item"
                          />
                          <i
                            class="el-icon-close close-icon"
                            @click="handleAttachmentRemove()"
                          />
                        </div>
                      </el-form-item>
                    </srm-col>
                    <srm-col :span="8">
                      <!-- 企业名称 -->
                      <el-form-item
                        prop="companyName"
                        :label="$t('vendorMod.companyName')"
                      >
                        <el-input
                          v-model="allParam.companyInfo.companyName"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <!-- 个体户不用显示 -->
                    <srm-col
                      v-if="curType!=='GETI'"
                      :span="8"
                    >
                      <!-- 注册资本(万元) -->
                      <el-form-item
                        prop="companyName"
                        :label="$t('vendorMod.registeredCapital')"
                      >
                        <el-input
                          v-model="allParam.companyInfo.registeredCapital"
                          :placeholder="$t('vendorMod.pleaseEnter')"
                          class="input-with-select"
                          disabled
                        >
                          <dict-select
                            slot="append"
                            v-model="allParam.companyInfo.registCurrency"
                            code="currency"
                            :placeholder="$t('vendorMod.currencyCode')"
                            style="width: 110px;"
                          />
                        </el-input>
                      </el-form-item>
                    </srm-col>
                    <srm-col :span="8">
                      <!-- 成立日期 -->
                      <el-form-item
                        prop="companyCreationDate"
                        :label="$t('vendorMod.creationDate')"
                      >
                        <el-date-picker
                          v-model="allParam.companyInfo.companyCreationDate"
                          type="date"
                          disabled
                          :placeholder="$t('common.pleaseSelectDate')"
                          format="yyyy-MM-dd"
                          value-format="yyyy-MM-dd"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :span="8">
                      <!-- 企业简称 -->
                      <el-form-item
                        prop="companyShortName"
                        :label="$t('vendorMod.companyShortName')"
                      >
                        <el-input
                          v-model="allParam.companyInfo.companyShortName"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <!-- 只有境内供应商有 -->
                    <srm-col
                      v-if="curRel ==='INSIDE'"
                      :span="8"
                    >
                      <!-- 统一社会信用代码 -->
                      <el-form-item
                        prop="lcCode"
                        :label="$t('vendorMod.lcCode')"
                      >
                        <el-input
                          v-model="allParam.companyInfo.lcCode"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <!-- 只有境外供应商有 -->
                    <srm-col
                      v-if="curRel ==='OUT'"
                      :span="8"
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
                    <srm-col :span="8">
                      <!-- 法人代表 -->
                      <el-form-item
                        prop="legalPerson"
                        :label="$t('vendorMod.legalPerson')"
                      >
                        <el-input
                          v-model="allParam.companyInfo.legalPerson"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :span="8">
                      <!-- 登记机关 -->
                      <el-form-item
                        prop="registrationAuthority"
                        :label="$t('vendorMod.registrationAuthority')"
                      >
                        <el-input
                          v-model="allParam.companyInfo.registrationAuthority"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <!-- 个体户不用显示 -->
                    <srm-col
                      v-if="curType!=='GETI'"
                      :span="8"
                    >
                      <!-- 营业日期从 -->
                      <el-form-item
                        prop="businessStartDate"
                        :label="$t('vendorMod.businessStartFrom')"
                      >
                        <el-date-picker
                          v-model="allParam.companyInfo.businessStartDate"
                          type="date"
                          disabled
                          :placeholder="$t('common.pleaseSelectDate')"
                          format="yyyy-MM-dd"
                          value-format="yyyy-MM-dd"
                        />
                      </el-form-item>
                    </srm-col>
                    <!-- 个体户不用显示 -->
                    <srm-col
                      v-if="curType!=='GETI'"
                      :span="8"
                    >
                      <!-- 营业日期至 -->
                      <el-form-item
                        prop="businessEndDate"
                        :label="$t('vendorMod.businessEndAt')"
                      >
                        <el-date-picker
                          v-model="allParam.companyInfo.businessEndDate"
                          type="date"
                          disabled
                          :placeholder="$t('common.pleaseSelectDate')"
                          format="yyyy-MM-dd"
                          value-format="yyyy-MM-dd"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :span="8">
                      <!-- 营业地址（国家/地区） -->
                      <el-form-item
                        prop="companyCountry"
                        :label="$t('vendorMod.businessAddr')"
                      >
                        <el-select
                          v-model="allParam.companyInfo.companyCountry"
                          disabled
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
                      :span="8"
                    >
                      <!-- 省份/州 -->
                      <el-form-item
                        prop="companyProvince"
                        :label="$t('vendorMod.province')"
                      >
                        <el-select
                          v-model="allParam.companyInfo.companyProvince"
                          disabled
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
                      v-if="curRel ==='INSIDE'"
                      :span="8"
                    >
                      <!-- 城市 -->
                      <el-form-item
                        prop="companyCity"
                        :label="$t('vendorMod.city')"
                      >
                        <el-select
                          v-model="allParam.companyInfo.companyCity"
                          disabled
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
                    <srm-col :span="24">
                      <!-- 详细地址 -->
                      <el-form-item
                        prop="companyAddress"
                        :label="$t('components.address.detailAddress')"
                      >
                        <el-input
                          v-model="allParam.companyInfo.companyAddress"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>

                    <srm-col :span="8">
                      <el-form-item label="D-U-N-S">
                        <el-input
                          v-model="allParam.companyInfo.dunsCode"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <!-- 公司网站 -->
                    <srm-col :span="8">
                      <el-form-item :label="$t('vendorMod.companySite')">
                        <el-input
                          v-model="allParam.companyInfo.ceeaCompanyWebsite"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <!-- 商业模式 -->
                    <srm-col :span="8">
                      <el-form-item :label="$t('vendorMod.bizModel')">
                        <el-input
                          v-model="allParam.companyInfo.ceeaBusinessModel"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <!-- 供应商业务类型 -->
                    <srm-col :span="8">
                      <el-form-item :label="$t('vendorMod.vendorBusinessType')">
                        <el-select
                          v-model="allParam.companyInfo.ceeaSupBusinessType"
                          disabled
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
                    <!-- 行业类型 -->
                    <srm-col :span="8">
                      <el-form-item :label="$t('vendorMod.industryType')">
                        <el-select
                          v-model="allParam.companyInfo.ceeaIndustryType"
                          disabled
                        >
                          <el-option
                            v-for="item in industryTypeList"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                          />
                        </el-select>
                      </el-form-item>
                    </srm-col>
                    <!-- 企业简介 -->
                    <srm-col :span="8">
                      <el-form-item :label="$t('vendorMod.companyProfile')">
                        <el-input
                          v-model="allParam.companyInfo.ceeaCompanyIntro"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <!-- 经营范围 -->
                    <srm-col :span="8">
                      <el-form-item :label="$t('vendorMod.businessScope2')">
                        <el-input
                          v-model="allParam.companyInfo.businessScope"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>

                    <srm-col :span="16">
                      <!-- 可供品类 -->
                      <!-- 主营品类 -->
                      <el-form-item
                        :label="$t('vendorMod.mainCategory')"
                        class="is-required"
                      >
                        <treeselect
                          v-model="relModel.relform.categoryRels"
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
                          disabled
                          :load-options="catLoadOptions"
                          @input="treeSelectChange"
                        />
                      </el-form-item>
                    </srm-col>
                    <!-- 厂房性质 -->
                    <srm-col :span="8">
                      <el-form-item :label="$t('vendorMod.factoryType')">
                        <el-select
                          v-model="allParam.companyInfo.ceeaPlantType"
                          disabled
                        >
                          <el-option
                            v-for="item in factoryType"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                          />
                        </el-select>
                      </el-form-item>
                    </srm-col>
                    <!-- 厂房面积 -->
                    <srm-col :span="8">
                      <el-form-item :label="$t('vendorMod.factoryArea')">
                        <el-input
                          v-model="allParam.companyInfo.ceeaPlantArea"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :span="8">
                      <el-form-item label="代理品牌">
                        <el-input
                          v-model="allParam.companyInfo.ceeaAgentBrand"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :span="8">
                      <el-form-item label="是否上市">
                        <el-checkbox
                          v-model="allParam.companyInfo.ceeaIfListed"
                          true-label="Y"
                          false-label="N"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :span="8">
                      <el-form-item label="上市时间">
                        <el-date-picker
                          v-model="allParam.companyInfo.ceeaListedTime"
                          type="date"
                          format="yyyy-MM-dd"
                          disabled
                          value-format="yyyy-MM-dd"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :span="24">
                      <h3>公司规模</h3>
                    </srm-col>
                    <srm-col :span="8">
                      <el-form-item label="员工数量">
                        <el-input
                          v-model="allParam.companyInfoDetail.staffQuantity"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :span="8">
                      <el-form-item label="管理人员数量">
                        <el-input
                          v-model="allParam.companyInfoDetail.managerQuantity"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :span="8">
                      <el-form-item label="技术人员数量">
                        <el-input
                          v-model="allParam.companyInfoDetail.technicistQuantity"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :span="8">
                      <el-form-item label="生产人员数量">
                        <el-input
                          v-model="allParam.companyInfoDetail.productorQuantity"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :span="8">
                      <el-form-item label="是否有研发部">
                        <el-checkbox
                          v-model="allParam.companyInfoDetail.ifRad"
                          true-label="Y"
                          false-label="N"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :span="8">
                      <el-form-item label="研发人员数量">
                        <el-input
                          v-model="allParam.companyInfoDetail.radStaffQuantity"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :span="24">
                      <h3>整体实力</h3>
                    </srm-col>
                    <srm-col :span="8">
                      <el-form-item label="行业排名">
                        <el-input
                          v-model="allParam.companyInfoDetail.businessRank"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :span="8">
                      <el-form-item label="市场份额">
                        <el-input
                          v-model="allParam.companyInfoDetail.marketShare"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :span="24">
                      <el-form-item label="国际同行前五">
                        <el-input
                          v-model="allParam.companyInfoDetail.internationalTopFive"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :span="24">
                      <el-form-item label="国内同行前五">
                        <el-input
                          v-model="allParam.companyInfoDetail.internalTopFive"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                  </el-collapse-item>
                  <!-- 联系人信息 -->
                  <el-collapse-item
                    ref="contact"
                    title="联系人信息"
                    name="2"
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
                        <el-table-column
                          align="center"
                          prop="contactName"
                          label="姓名"
                          width="100"
                          :show-overflow-tooltip="true"
                        />
                        <el-table-column
                          align="center"
                          prop="ceeaGender"
                          label="性别"
                          width="100"
                          :show-overflow-tooltip="true"
                        />
                        <el-table-column
                          align="center"
                          prop="ceeaDeptName"
                          label="部门"
                          width="100"
                          :show-overflow-tooltip="true"
                        />
                        <el-table-column
                          align="center"
                          prop="position"
                          label="职位"
                          width="100"
                          :show-overflow-tooltip="true"
                        />
                        <el-table-column
                          align="center"
                          prop="ceeaContactMethod"
                          label="联系方式"
                          width="100"
                          :show-overflow-tooltip="true"
                        />
                        <el-table-column
                          align="center"
                          prop="email"
                          label="邮箱"
                          width="100"
                          :show-overflow-tooltip="true"
                        />
                        <el-table-column
                          align="center"
                          prop="ceeaDefaultContact"
                          label="默认联系人"
                          width="100"
                          :show-overflow-tooltip="true"
                        />
                        <el-table-column
                          align="center"
                          prop="ceeaComments"
                          label="备注"
                          width="100"
                          :show-overflow-tooltip="true"
                        />
                      </el-table>
                    </div>
                  </el-collapse-item>
                  <!-- 银行信息 -->
                  <el-collapse-item
                    ref="bank"
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
                        <el-table-column
                          align="center"
                          prop="bankCode"
                          label="银行代码"
                          width="100"
                          :show-overflow-tooltip="true"
                        />
                        <el-table-column
                          align="center"
                          prop="bankName"
                          label="银行名称"
                          width="100"
                          :show-overflow-tooltip="true"
                        />
                        <el-table-column
                          align="center"
                          prop="openingBank"
                          label="开户行名称"
                          width="100"
                          :show-overflow-tooltip="true"
                        />
                        <el-table-column
                          align="center"
                          prop="bankAccountName"
                          label="账户名称"
                          width="100"
                          :show-overflow-tooltip="true"
                        />
                        <el-table-column
                          align="center"
                          prop="bankAccount"
                          label="银行账号"
                          width="100"
                          :show-overflow-tooltip="true"
                        />
                        <el-table-column
                          align="center"
                          prop="currencyCode"
                          label="币种"
                          width="100"
                          :show-overflow-tooltip="true"
                        >
                          <template slot-scope="scope">
                            <dict-select
                              v-model="scope.row.currencyCode"
                              code="currency"
                              disabled
                            />
                          </template>
                        </el-table-column>
                        <el-table-column
                          align="center"
                          prop="ceeaMainAccount"
                          label="主账户"
                          width="100"
                          :show-overflow-tooltip="true"
                        />
                        <el-table-column
                          align="center"
                          prop="ceeaEnabled"
                          label="启用"
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
                      </el-table>
                    </div>
                  </el-collapse-item>
                  <el-collapse-item
                    ref="businessScale"
                    title="经营信息"
                    name="4"
                  >
                    <h3>经营规模</h3>
                    <srm-col :span="8">
                      <el-form-item label="成立时间">
                        <el-date-picker
                          v-model="allParam.operationInfo.ceeaCompanyCreationDate"
                          type="date"
                          format="yyyy-MM-dd"
                          disabled
                          value-format="yyyy-MM-dd"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :span="8">
                      <el-form-item label="注册资金">
                        <el-input
                          v-model="allParam.operationInfo.ceeaRegisteredCapital"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :span="8">
                      <el-form-item label="年营业额">
                        <el-input
                          v-model="allParam.operationInfo.ceeaYearTurnover"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :span="24">
                      <el-form-item label="前三年销售额">
                        <el-input
                          v-model="allParam.operationInfo.ceeaPreThreeYearsSale"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :span="24">
                      <el-form-item label="前三年净利润">
                        <el-input
                          v-model="allParam.operationInfo.ceeaPreThreeYearsProfit"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :span="24">
                      <el-form-item label="前三年资产负债率">
                        <el-input
                          v-model="allParam.operationInfo.ceeaPreThreeYearsAal"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <h3>经营业绩</h3>
                    <srm-col :span="24">
                      <el-form-item label="经营范围和各项业务所占比重">
                        <el-input
                          v-model="allParam.operationInfo.ceeaScopeBusinessRatio"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :span="24">
                      <el-form-item label="是否具有供应太阳能行业相关物资的经验">
                        <el-input
                          v-model="allParam.operationInfo.ceeaIfHasSolarPower"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :span="24">
                      <el-form-item label="贵司的上下游产业布局">
                        <el-input
                          v-model="allParam.operationInfo.ceeaUpDownLayout"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <h3>发展规划</h3>
                    <srm-col :span="24">
                      <el-form-item label="3年内经营规模变化的预期">
                        <el-input
                          v-model="allParam.operationInfo.ceeaThreeScaleChangeExp"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :span="24">
                      <el-form-item label="降低客户采购成本的建议和要求">
                        <el-input
                          v-model="allParam.operationInfo.ceeaReducePurCostAdvise"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :span="24">
                      <el-form-item label="未来控制产品成本的计划和策略">
                        <el-input
                          v-model="allParam.operationInfo.ceeaProCostPlanStrategy"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <h3>研发能力</h3>
                    <srm-col :span="24">
                      <el-form-item label="年研发投入占销售额的比例">
                        <el-input
                          v-model="allParam.operationInfo.ceeaRdSaleRate"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :span="24">
                      <el-form-item label="请从技术角度解析贵司产品的优劣性">
                        <el-input
                          v-model="allParam.operationInfo.ceeaProGoodBad"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :span="24">
                      <el-form-item label="描述该产品后期研发方向(技术路线图)">
                        <el-input
                          v-model="allParam.operationInfo.ceeaProTechRoute"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :span="24">
                      <el-form-item label="描述贵公司研发团队构成情况及研发能力评估">
                        <el-input
                          v-model="allParam.operationInfo.ceeaTeamShapeAbility"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <h3>产品说明</h3>
                    <srm-col :span="24">
                      <el-form-item label="请提供我们所采购物资的价格构成要素,比例">
                        <el-input
                          v-model="allParam.operationInfo.ceeaProPriceInscapeRate"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :span="24">
                      <el-form-item label="哪些要素对于今后成本降低有较大推动作用">
                        <el-input
                          v-model="allParam.operationInfo.ceeaReduceCostFactor"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :span="24">
                      <el-form-item label="贵司将怎样迎合我司对于价格持续优化的需求">
                        <el-input
                          v-model="allParam.operationInfo.ceeaHowUpgradePrice"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>

                    <div class="left_div">
                      <h3>质量管控</h3>
                      <el-table
                        :data="allParam.operationQualities"
                        style="width: 100%"
                        border
                        max-height="251px"
                      >
                        <el-table-column
                          align="center"
                          type="index"
                          width="50"
                        />
                        <el-table-column
                          align="center"
                          prop="mainTestEquipment"
                          label="主要检验设备"
                          min-width="200"
                          :show-overflow-tooltip="true"
                        />
                        <el-table-column
                          align="center"
                          prop="mainTestProject"
                          label="主要检验项目"
                          min-width="200"
                          :show-overflow-tooltip="true"
                        />
                      </el-table>
                    </div>
                    <div class="left_div">
                      <h3>设备信息</h3>
                      <el-table
                        :data="allParam.operationEquipments"
                        style="width: 100%"
                        border
                        max-height="251px"
                      >
                        <el-table-column
                          align="center"
                          type="index"
                          width="50"
                        />
                        <el-table-column
                          align="center"
                          prop="equipmentType"
                          label="设备类型"
                          width="100"
                          :show-overflow-tooltip="true"
                        />
                        <el-table-column
                          align="center"
                          prop="equipmentName"
                          label="设备名称"
                          width="100"
                          :show-overflow-tooltip="true"
                        />
                        <el-table-column
                          align="center"
                          prop="specification"
                          label="规格型号"
                          width="100"
                          :show-overflow-tooltip="true"
                        />
                        <el-table-column
                          align="center"
                          prop="quantity"
                          label="数量"
                          width="100"
                          :show-overflow-tooltip="true"
                        />
                        <el-table-column
                          align="center"
                          prop="equipmentCapacity"
                          label="单位设备产能"
                          width="100"
                          :show-overflow-tooltip="true"
                        />
                        <el-table-column
                          align="center"
                          prop="manufacturer"
                          label="生产厂家"
                          min-width="150"
                          :show-overflow-tooltip="true"
                        />
                        <el-table-column
                          align="center"
                          prop="serviceYear"
                          label="已服役年限"
                          width="100"
                          :show-overflow-tooltip="true"
                        />
                      </el-table>
                    </div>
                    <div class="left_div">
                      <h3>产品能力信息</h3>
                      <el-table
                        :data="allParam.operationProducts"
                        style="width: 100%"
                        border
                        max-height="251px"
                      >
                        <el-table-column
                          align="center"
                          type="index"
                          width="50"
                        />
                        <el-table-column
                          align="center"
                          prop="proBase"
                          label="生产基地"
                          min-width="150"
                          :show-overflow-tooltip="true"
                        />
                        <el-table-column
                          align="center"
                          prop="proName"
                          label="产品名称/型号"
                          min-width="150"
                          :show-overflow-tooltip="true"
                        />
                        <el-table-column
                          align="center"
                          prop="proBrand"
                          label="产品品牌"
                          width="150"
                          :show-overflow-tooltip="true"
                        />
                        <el-table-column
                          align="center"
                          prop="mainTechnics"
                          label="主要工艺"
                          width="100"
                          :show-overflow-tooltip="true"
                        />
                        <el-table-column
                          align="center"
                          prop="yearOutput"
                          label="年产量"
                          width="100"
                          :show-overflow-tooltip="true"
                        />
                        <el-table-column
                          align="center"
                          prop="supplyCapacityRate"
                          label="可提供给美云的供应产能比例"
                          width="200"
                          :show-overflow-tooltip="true"
                        />
                        <el-table-column
                          align="center"
                          prop="proQualifiedRate"
                          label="产品合格率（%）"
                          width="120"
                          :show-overflow-tooltip="true"
                        />
                        <el-table-column
                          align="center"
                          prop="yearTurnover"
                          label="年营业额（万元）"
                          width="120"
                          :show-overflow-tooltip="true"
                        />
                        <el-table-column
                          align="center"
                          prop="comment"
                          label="备注"
                          min-width="100"
                          :show-overflow-tooltip="true"
                        />
                      </el-table>
                    </div>
                    <h3>售后服务</h3>
                    <srm-col :span="24">
                      <el-form-item label="售后能力">
                        <el-input
                          v-model="allParam.operationInfo.ceeaAfterSalesAbility"
                          disabled
                        />
                      </el-form-item>
                    </srm-col>
                    <div class="left_div">
                      <h3>客户情况</h3>
                      <el-table
                        :data="allParam.businessInfos"
                        style="width: 100%"
                        border
                        max-height="251px"
                      >
                        <el-table-column
                          align="center"
                          type="index"
                          width="50"
                        />
                        <el-table-column
                          align="center"
                          prop="customer"
                          label="客户名称"
                          min-width="150"
                          :show-overflow-tooltip="true"
                        />
                        <el-table-column
                          align="center"
                          prop="ceeaWorkFrequency"
                          label="合作频率"
                          width="150"
                          :show-overflow-tooltip="true"
                        />
                        <el-table-column
                          align="center"
                          prop="ceeaArea"
                          label="所属区域"
                          min-width="150"
                          :show-overflow-tooltip="true"
                        />
                        <el-table-column
                          align="center"
                          prop="preSalesVol"
                          label="销售数量"
                          width="100"
                          :show-overflow-tooltip="true"
                        />
                        <el-table-column
                          align="center"
                          prop="preSalesAmount"
                          label="销售额（万元）"
                          width="120"
                          :show-overflow-tooltip="true"
                        />
                        <el-table-column
                          align="center"
                          prop="ceeaComment"
                          label="备注"
                          min-width="100"
                          :show-overflow-tooltip="true"
                        />
                      </el-table>
                    </div>
                  </el-collapse-item>
                </srm-row>
              </el-form>
              <!-- 其他信息 -->
              <el-collapse-item
                ref="otherFile"
                title="其他附件信息"
                name="5"
              >
                <scene-attachment
                  ref="sceneAttachment"
                  v-model="dataAtt"
                  disabled
                  sence-code="companyInfoMaintain"
                  :business-id="companyId"
                  :att-opt="curOpt"
                  :up-file-info="fileInfo"
                  :file-refresh="fileRefresh"
                />
              </el-collapse-item>
              <el-collapse-item
                ref="vendorUserinfo"
                title="供应商账号信息"
                name="6"
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
                    <el-table-column
                      align="center"
                      prop="username"
                      label="账号"
                      min-width="150"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="nickname"
                      label="联系人"
                      min-width="150"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="phone"
                      label="联系方式"
                      min-width="150"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="email"
                      label="邮箱"
                      min-width="150"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="ceeaJobcodeDescr"
                      label="职位"
                      min-width="150"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="username"
                      label="主账号"
                      min-width="150"
                      :show-overflow-tooltip="true"
                    />
                  </el-table>
                </div>
              </el-collapse-item>
              <el-collapse-item
                ref="ouInfo"
                title="合作品类"
                name="7"
              >
                <div class="left_div">
                  <el-table
                    :data="allParam.orgCategorys"
                    style="width: 100%"
                    border
                    max-height="251px"
                  >
                    <el-table-column
                      align="center"
                      type="index"
                      width="50"
                    />
                    <el-table-column
                      align="center"
                      prop="orgName"
                      label="OU"
                      min-width="150"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="categoryName"
                      label="品类"
                      min-width="150"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="serviceStatus"
                      label="品类状态"
                      min-width="150"
                      :formatter="filterCatHandler"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="customer"
                      label="更新日期"
                      min-width="150"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="ifSingleSupplier"
                      label="是否单一供方"
                      min-width="150"
                      :show-overflow-tooltip="true"
                    >
                      <template slot-scope="scope">
                        <el-select v-model="scope.row.ifSingleSupplier">
                          <el-option
                            v-for="item in yesNoOptions"
                            :key="item.id"
                            :label="item.label"
                            :value="item.value"
                          />
                        </el-select>
                      </template>
                    </el-table-column>
                  </el-table>
                </div>
              </el-collapse-item>
              <el-collapse-item
                ref="operationHistory"
                title="操作历史"
                name="8"
              >
                <div class="left_div">
                  <el-table
                    :data="allParam.companyStatusLogs"
                    style="width: 100%"
                    border
                    max-height="251px"
                  >
                    <el-table-column
                      align="center"
                      type="index"
                      width="50"
                    />
                    <el-table-column
                      align="center"
                      prop="operatorName"
                      label="处理人"
                      min-width="150"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="operationType"
                      label="处理类型"
                      min-width="150"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="operationStatus"
                      label="状态"
                      min-width="150"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="operationDate"
                      label="处理时间"
                      min-width="150"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="operationMemo"
                      label="说明"
                      min-width="150"
                      :show-overflow-tooltip="true"
                    />
                  </el-table>
                </div>
              </el-collapse-item>
            </el-collapse>
          </div>
        </div>
        <!-- 填写进度调区域 -->
        <!-- 企业登记节点 -->
        <c-fill-progress
          node-name=""
          :data="computedNode"
          :percentage="true"
          @index-click="indexClickTo"
        />
        <c-toolbar>
          <template slot="right">
            <el-button @click="backToHandle">
              {{ $t('common.cancel') }}
            </el-button>
            <el-button
              type="primary"
              @click="saveHandle"
            >
              {{ $t('common.submit') }}
            </el-button>
          </template>
        </c-toolbar>
        <!-- OCR 弹框 -->
        <c-ocr
          :visible.sync="ocrVisible"
          :file-upload-id="fileUploadId"
          @on-confirm="ocrConfirm"
          @close="ocrClose"
        />
      </div>
    </el-mian>
  </el-contain>
</template>
<script>
import CToolbar from 'lib@/components/c-toolbar'
import CFillProgress from 'lib@/components/c-fill-progress'
import CUploadFile from '@/library/components/c-upload-file'
import CDownloadLink from 'lib@/components/c-download-link'
import COcr from 'lib@/components/c-ocr'
import SceneAttachment from 'mod@/vendorManagementBuyer/components/SceneAttachment'
import Treeselect, { LOAD_CHILDREN_OPTIONS } from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import { getDictItem, getDictItemList, getRegion } from '@/api/common'
import { getConfigByTemplate, getCompanyForEdit, saveOrUpdateCompany, submitCompany } from 'mod@/userManage/api/userManage'
import { getCatChildrenData } from 'mod@/basicSetting/api/baseSetting'
import { deleteAttachById } from 'mod@/vendorManagementBuyer/api/vendorApi'
import { adaptDictData } from '@/utils'
import _omit from 'lodash/omit'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'

export default {
  name: 'VendorProfileDetailRead',
  components: {
    CFillProgress, CToolbar, CUploadFile, CDownloadLink, Treeselect, COcr, SceneAttachment
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      lock: true,
      companyId: '',
      dataStatus: '',
      curRel: '',
      curType: '',
      isEnableOcr: 'N', // 是否启用OCR营业执照识别
      fileUploadId: null, // ocr 文件入参ID
      ocrVisible: false, // ocr 弹窗
      // 营业执照接收的文件类型
      acceptFileType: [ 'jpg', 'png', 'jpeg' ],
      activeDims: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'],
      nodeData: [ // 进度条节点信息
        { code: 'base', name: '基本信息', percentage: 0 }, // '企业基本信息'
        { code: 'contact', name: this.$t('vendorMod.contactInfo'), percentage: 0 }, // '联系人信息'
        { code: 'bank', name: this.$t('vendorMod.bankInfo'), percentage: 0 }, // '银行信息'
        { code: 'businessScale', name: '经营信息', percentage: 0 }, // '经营信息信息'
        { code: 'otherFile', name: '其他附件信息', percentage: 0 }, // '其他信息'
        { code: 'vendorUserinfo', name: '供应商账号信息', percentage: 0 },
        { code: 'ouInfo', name: '合作品类', percentage: 0 },
        { code: 'operationHistory', name: '操作历史', percentage: 0 }
      ],
      vendorClassificationList: [],
      relations: [], // 境内外管理
      natureList: [], // 企业性质
      countryList: [], // 国家
      provinceList: [], // 省
      cityList: [], // 市
      industryTypeList: [],
      businessTypeList: [],
      factoryType: [], // 厂房性质
      bizModel: [], // 商业规模
      employeeQyt: [], // 员工规模
      companyStatus: [], // 营业状态
      categoryTreeOptions: [],
      relModel: { // 境内外关系
        relform: {
          overseasRelation: '',
          companyType: ''
        },
        rules: {
          overseasRelation: [{ required: true, message: this.$t('vendorMod.msgOverseasRelation') }], // '请选择境内外关系'
          companyType: [{ required: true, message: this.$t('vendorMod.msgCompanyType') }]// '请选择企业性质'
        }
      },
      outerField: ['overseasRelation', 'companyType', 'companyName', 'businessLicenseFileId', 'registeredCapital', 'companyCreationDate', 'dunsCode', 'legalPerson', 'companyCountry'],
      baseRules: {
        overseasRelation: [{ required: true, message: this.$t('vendorMod.msgOverseasRelation') }], // '请输入境内外关系'
        companyName: [{ required: true, message: this.$t('vendorMod.msgCompanyName') }], // '请输入公司名称'
        companyCountry: [{ required: true, message: this.$t('vendorMod.msgBusinessAddr') }], // '请输入国家'
        companyAddress: [{ required: true, message: this.$t('vendorMod.msgDetailAddr') }]// '请输入地址'
        // categoryRels: [{ required: true, message: '请选择可供品类' }]
      },
      outRules: {
        registeredCapital: [{ required: true, message: this.$t('vendorMod.msgRegisteredCapital') }], // '请输入注册资金'
        companyCreationDate: [{ required: true, message: this.$t('vendorMod.msgCreationDate') }], // '请输入成立日期'
        legalPerson: [{ required: true, message: this.$t('vendorMod.msgLegalPerson') }], // '请输入法人代表'
        businessScope: [{ required: true, message: this.$t('vendorMod.msgBusinessScope') }], // '请输入业务范围'
        companyStatus: [{ required: true, message: this.$t('vendorMod.msgBusinessStatus') }]// '请输入经营状态'
      },
      sosoRules: {
        companyType: [{ required: true, message: this.$t('vendorMod.msgCompanyType') }], // '请输入公司性质'
        businessLicenseFileId: [{ required: true, message: this.$t('vendorMod.msgBusinessLicense') }], // '请上传营业执照'
        companyCreationDate: [{ required: true, message: this.$t('vendorMod.msgCreationDate') }], // '请输入成立日期'
        businessScope: [{ required: true, message: this.$t('vendorMod.msgBusinessScope') }], // '请输入业务范围'
        companyStatus: [{ required: true, message: this.$t('vendorMod.msgBusinessStatus') }]// '请输入经营状态'
      },
      innerRules: {
        businessStartDate: [{ required: true, message: this.$t('vendorMod.msgBusinessStartFrom') }], // '请输入开始营业日期'
        businessEndDate: [{ required: true, message: this.$t('vendorMod.msgBusinessEndAt') }], // '请输入结束营业日期'
        companyType: [{ required: true, message: this.$t('vendorMod.msgCompanyType') }], // '请输入公司性质'
        lcCode: [{ required: true, message: this.$t('vendorMod.msgLcCode') }], // '请输入统一社会信用代码'
        legalPerson: [{ required: true, message: this.$t('vendorMod.msgLegalPerson') }], // '请输入法人代表'
        businessLicenseFileId: [{ required: true, message: this.$t('vendorMod.msgBusinessLicense') }], // '请上传营业执照'
        registeredCapital: [{ required: true, message: this.$t('vendorMod.msgRegisteredCapital') }], // '请输入注册资金'
        companyCreationDate: [{ required: true, message: this.$t('vendorMod.msgCreationDate') }], // '请输入成立日期'
        businessScope: [{ required: true, message: this.$t('vendorMod.msgBusinessScope') }], // '请输入业务范围'
        companyStatus: [{ required: true, message: this.$t('vendorMod.msgBusinessStatus') }], // '请输入经营状态'
        companyProvince: [{ required: true, message: this.$t('vendorMod.msgpProvince') }], // '请输入省'
        companyCity: [{ required: true, message: this.$t('vendorMod.msgCity') }]// '请输入市'
        // registrationAuthority: [{ required: true, message: '请输入登记机关' }],
      },
      unProfitRules: {
        companyType: [{ required: true, message: this.$t('vendorMod.msgCompanyType') }], // '请输入公司性质'
        companyProvince: [{ required: true, message: this.$t('vendorMod.msgpProvince') }], // '请输入省'
        companyCity: [{ required: true, message: this.$t('vendorMod.msgCity') }]// '请输入市'
      },
      // 基础信息 拓展字段
      baseInfoDimFieldContexts: [],
      bankAccountType: [], // 账户类型
      baseDimModel: {}, // 基础信息拓展字段
      baseDimRules: {}, // 基础信息拓展字段规则
      // 银行信息
      bankFormItem: [
        { label: this.$t('vendorMod.openingBank'), prop: 'openingBank', required: true, type: 'text' }, // '开户行'
        { label: this.$t('vendorMod.unionCode'), prop: 'unionCode', required: true, type: 'text' }, // '银行联行号'
        { label: 'SWIFT CODE', prop: 'swiftCode', required: false, type: 'text' },
        { label: this.$t('vendorMod.bankAccountName'), prop: 'bankAccountName', required: true, type: 'text' }, // '开户户名'
        { label: this.$t('vendorMod.bankAccount'), prop: 'bankAccount', required: true, type: 'text' }// '开户账号'
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
          openingBank: [{ required: true, message: this.$t('vendorMod.msgOpeningBank') }], // '请输入开户行'
          unionCode: [{ required: true, message: this.$t('vendorMod.msgUnionCode') }], // '请输入银行联行号'
          bankAccountName: [{ required: true, message: this.$t('vendorMod.msgBankAccountName') }], // '请输入开户户名'
          bankAccount: [{ required: true, message: this.$t('vendorMod.msgBankAccount') }], // '请输入开户账号'
          accountType: [{ required: true, message: this.$t('vendorMod.msgAccountType') }], // '请选择账号类型'
          currencyCode: [{ required: true, message: this.$t('vendorMod.msgCurrencyCode') }]// '请选择币种'
        }
      },
      yesNoOptions: [{ value: 'Y', label: '是' }, { value: 'N', label: '否' }],
      // 银行信息 拓展字段
      bankDimFieldContexts: [],
      bankDimModel: {},
      // 联系人信息
      linkManFormItem: [
        { label: this.$t('vendorMod.contactName'), prop: 'contactName', required: true, type: 'text' }, // '联系人'
        { label: this.$t('vendorMod.position'), prop: 'position', required: true, type: 'text' }, // '公司职务'
        { label: this.$t('vendorMod.mobilePhone'), prop: 'mobileNumber', required: true, type: 'text' }, // '手机号码'
        { label: this.$t('vendorMod.email'), prop: 'email', required: true, type: 'text' }, // '邮箱'
        { label: this.$t('vendorMod.telPhone'), prop: 'phoneNumber', required: false, type: 'text' }, // '座机号码'
        { label: this.$t('vendorMod.contactAddr'), prop: 'contactAddress', required: false, type: 'text' }// '联系地址'
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
          contactName: [{ required: true, message: this.$t('vendorMod.MsgContactName') }], // '请输入联系人'
          position: [{ required: true, message: this.$t('vendorMod.msgPosition') }], // '请输入公司职务'
          mobileNumber: [{ required: true, message: this.$t('vendorMod.msgMobilePhone') }], // '请输入手机号码'
          email: [{ required: true, message: this.$t('vendorMod.msgEmail') }]// '请输入邮箱'
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
      // 附件信息
      attachTableData: [],
      // 文件上传配置信息
      fileInfo: {
        uploadType: 'FASTDFS', // 固定参数
        sourceType: 'WEB_APP', // 固定参数
        fileModular: 'sup', // 文件所属模块 -》基础模块
        fileFunction: 'companyInfoMaintain', // 文件所属功能
        fileType: 'images' // 文件所属类型
      },
      curOpt: 'add',
      fileRefresh: false, // 刷新附件
      dataAtt: [], // 绑定子组件的数据
      allParam: {
          rules: {},
          'companyInfo': {
              'companyId': null,
              'status': '',
              'overseasRelation': 'INSIDE',
              'companyType': 'GUOYOU',
              'businessLicenseFileId': '',
              'businessLicense': '',
              'companyName': '',
              'registeredCapital': '',
              'registCurrency': '',
              'registCurrencyName': '',
              'companyCreationDate': '',
              'companyShortName': '',
              'lcCode': '',
              'legalPerson': '',
              'registrationAuthority': '',
              'businessStartDate': '',
              'businessEndDate': '',
              'companyCountry': '',
              'companyProvince': '',
              'companyCity': '',
              'companyAddress': '',
              'dunsCode': '',
              'ceeaCompanyWebsite': '',
              'ceeaBusinessModel': '',
              'ceeaSupBusinessType': '',
              'ceeaIndustryType': '',
              'ceeaCompanyIntro': '',
              'businessScope': '',
              'categoryRels': '',
              'ceeaPlantType': '',
              'ceeaPlantArea': '',
              'ceeaAgentBrand': '',
              'ceeaIfListed': 'Y',
              'ceeaListedTime': ''
          },
          'companyInfoDetail': {
              'companyDetailId': null,
              'companyId': null,
              'staffQuantity': '',
              'managerQuantity': '',
              'technicistQuantity': '',
              'productorQuantity': '',
              'ifRad': 'Y',
              'radStaffQuantity': '',
              'businessRank': '',
              'marketShare': '',
              'internationalTopFive': '',
              'internalTopFive': ''
          },
          'contactInfos': [],
          'bankInfos': [],
          'operationInfo': {
              'opInfoId': null,
              'companyId': null,
              'ceeaCompanyCreationDate': '',
              'ceeaRegisteredCapital': '',
              'ceeaYearTurnover': '',
              'ceeaPreThreeYearsSale': '',
              'ceeaPreThreeYearsProfit': '',
              'ceeaPreThreeYearsAal': '',
              'ceeaScopeBusinessRatio': '',
              'ceeaIfHasSolarPower': 'Y',
              'ceeaUpDownLayout': '',
              'ceeaThreeScaleChangeExp': '',
              'ceeaReducePurCostAdvise': '',
              'ceeaProCostPlanStrategy': '',
              'ceeaRdSaleRate': '',
              'ceeaProGoodBad': '',
              'ceeaProTechRoute': '',
              'ceeaTeamShapeAbility': '',
              'ceeaProPriceInscapeRate': '',
              'ceeaReduceCostFactor': '',
              'ceeaHowUpgradePrice': '',
              'ceeaAfterSalesAbility': ''
          },
          'operationQualities': [],
          'operationProducts': [],
          'operationEquipments': [],
          'businessInfos': [],
          'userInfo': {},
          'managementAttaches': []
      }
    }
  },
  computed: {
    computedNode: function () {
      this.nodeData.forEach(elm => {
        if (elm.code === 'base') {
          let formObj = this.allParam.companyInfo
          let dimObj = this.baseDimModel || {}
          let formKey = this.computedBaseFields() // key
          let dimLength = Object.keys(dimObj).length // .length
          let curp = 0
          let total = formKey.length + dimLength
          formKey.map(item => {
            let key = item
            if (key === 'categoryRels') {
              if (formObj[key] && formObj[key].length > 0) {
                curp++
              }
            } else {
              if (formObj[key]) {
                curp++
              }
            }
          })
          if (dimLength > 0) {
            Object.keys(dimObj).forEach(key => {
              if (dimObj[key]) {
                curp++
              }
            })
          }
          elm.percentage = (Math.round(curp / total * 10000) / 100.00)
        } else if (elm.code === 'bank') {
          let formObj = this.bankModel.bankForm
          let curp = 0
          let total = Object.getOwnPropertyNames(formObj).length - 1
          Object.keys(formObj).forEach(key => {
            if (formObj[key]) {
              curp++
            }
          })
          elm.percentage = (Math.round(curp / total * 10000) / 100.00)
          return elm.percentage
        } else if (elm.code === 'contact') {
          let formObj = this.linkManModel.linkManForm
          let curp = 0
          let total = Object.getOwnPropertyNames(formObj).length - 1
          Object.keys(formObj).forEach(key => {
            if (formObj[key]) {
              curp++
            }
          })
          elm.percentage = (Math.round(curp / total * 10000) / 100.00)
          return elm.percentage
        } else if (elm.code === 'other') {
          let formObj = this.otherModel.otherForm
          let otherDim = this.otherDimModel
          let formKey = ['bizModel', 'companySite', 'employeeQty', 'factoryType', 'floorArea', 'floorSpace']
          let dimLength = Object.keys(otherDim).length // .length
          let curp = 0
          let total = formKey.length + dimLength
          formKey.forEach(item => {
            let key = item
            if (formObj[key]) {
              curp++
            }
          })
          if (dimLength > 0) {
            Object.keys(otherDim).forEach(key => {
              if (otherDim[key]) {
                curp++
              }
            })
          }
          elm.percentage = (Math.round(curp / total * 10000) / 100.00)
        } else if (elm.code === 'attach') {
          if (this.dataAtt.length > 0) {
            let total = this.dataAtt.length
            let curp = 0
            this.dataAtt.forEach(item => {
              if (item.fileuploadId) {
                curp += 1
              }
            })
            elm.percentage = (Math.round(curp / total * 10000) / 100.00)
          } else {
            elm.percentage = 0
          }
        }
      })
      return this.nodeData
    },
    computedBaseRules () {
      let Obj = {}
      if (this.curRel) {
        if (this.curRel === 'OUT' && this.curType === '') { // 境外
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
    // 加载一级可选品类
    getCatChildrenData({ categoryId: -1 }).then(response => {
      this.categoryTreeOptions = response.data.map(i => ({ ...i, children: null }))
    })
  },
  mounted () {},
  updated () {
    if (!this.bankModel.bankForm.bankAccountName && this.allParam.companyInfo.companyName && this.lock) {
      this.bankModel.bankForm.bankAccountName = this.allParam.companyInfo.companyName
      this.lock = false
    }
  },
  methods: {
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
    // 计算字段
    computedBaseFields () {
      let overseasRelation = this.allParam.companyInfo.overseasRelation // 境内外类型
      let companyType = this.allParam.companyInfo.companyType // 公司属性
      let resArr = []
      if (overseasRelation === 'OUT') {
        resArr = ['overseasRelation', 'companyName', 'businessLicenseFileId', 'registeredCapital', 'registCurrency', 'companyCreationDate', 'companyShortName', 'dunsCode', 'legalPerson', 'registrationAuthority', 'businessStartDate', 'businessEndDate', 'companyCountry', 'companyAddress', 'businessScope', 'companyStatus', 'categoryRels']
      } else { // INSIDE
        if (companyType === 'GETI') { // 个体户
          resArr = ['overseasRelation', 'companyType', 'companyName', 'businessLicenseFileId', 'companyCreationDate', 'companyShortName', 'lcCode', 'legalPerson', 'registrationAuthority', 'companyCountry', 'companyProvince', 'companyCity', 'companyAddress', 'businessScope', 'companyStatus', 'categoryRels']
        } else {
          resArr = ['overseasRelation', 'companyType', 'companyName', 'businessLicenseFileId', 'registeredCapital', 'registCurrency', 'companyCreationDate', 'companyShortName', 'lcCode', 'legalPerson', 'registrationAuthority', 'businessStartDate', 'businessEndDate', 'companyCountry', 'companyProvince', 'companyCity', 'companyAddress', 'businessScope', 'companyStatus', 'categoryRels']
        }
      }
      return resArr
    },
    // 切换必填规则
    switchBaseRules () {
      if (this.curRel) {
        let rules = {}
        if (this.curRel === 'OUT') { // 境外
          rules = Object.assign(this.baseRules, this.outRules)
        } else if (this.curRel === 'INSIDE') {
          if (this.curType === 'GETI') { // 个体户
            rules = Object.assign(this.baseRules, this.sosoRules)
          } else if (this.curType === 'FEIYINGLI') { // 非盈利
            rules = Object.assign(this.baseRules, this.unProfitRules)
          } else { // 其他内部
            rules = Object.assign(this.baseRules, this.innerRules)
          }
        } else {
          rules = this.baseRules
        }
        // console.log(rules)
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
        { dictCode: 'SUPPLIER_CLASSIFICATION' }
      ]
      getDictItemList(dictParamsArr).then(res => {
        const [RELATION, COMPANY_NATURE, COMPANY_STATUS, BIZ_MODEL, FACTORY_TYPE, EMPLOYEE_QTY, BANK_ACCOUNT_TYPE, INDUSTRY_TYPE, SUP_BUSINESS_TYPE, CATEGORY_STATUS, SUPPLIER_CLASSIFICATION] = res.data
        this.relations = adaptDictData(RELATION.RELATION, 'dict')
        this.natureList = adaptDictData(COMPANY_NATURE.COMPANY_NATURE, 'dict')
        this.companyStatus = adaptDictData(COMPANY_STATUS.COMPANY_STATUS, 'dict')
        this.bizModel = adaptDictData(BIZ_MODEL.BIZ_MODEL, 'dict')
        this.factoryType = adaptDictData(FACTORY_TYPE.FACTORY_TYPE, 'dict')
        this.employeeQyt = adaptDictData(EMPLOYEE_QTY.EMPLOYEE_QTY, 'dict')
        this.bankAccountType = adaptDictData(BANK_ACCOUNT_TYPE.BANK_ACCOUNT_TYPE, 'dict')
        this.industryTypeList = adaptDictData(INDUSTRY_TYPE.INDUSTRY_TYPE, 'dict')
        this.businessTypeList = adaptDictData(SUP_BUSINESS_TYPE.SUP_BUSINESS_TYPE, 'dict')
        this.catStatus = adaptDictData(CATEGORY_STATUS.CATEGORY_STATUS, 'dict')
        this.vendorClassificationList = adaptDictData(SUPPLIER_CLASSIFICATION.SUPPLIER_CLASSIFICATION, 'dict')
      })

      // 国家
      getDictItem('country').then(res => {
        this.countryList = adaptDictData(res.data, 'dict')
      })
      // 是否启用OCR
      getDictItem('ENABLE_OCR').then(res => {
        this.isEnableOcr = res.data[0].dictItemCode
      })
      // 加载省
      getRegion({ queryType: 'province' }).then(res => {
        if (res.data) {
          this.provinceList = this.adaptProvinceCity(res.data, 'province')
        }
      })
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
      this.allParam.companyInfo.overseasRelation = val
      this.curRel = val // 当前海内外关系
      if (val === 'INSIDE') { // 境内
        this.allParam.companyInfo.companyCountry = 'CN'
      } else { // 境外
        this.allParam.companyInfo.companyType = ''
      }
      this.switchBaseRules()
    },
    companyTypeChangeHandle (val) {
      this.allParam.companyInfo.companyType = val
      this.curType = val // 当前公司属性
      this.switchBaseRules()
    },
    addOneBank () {
      this.allParam.bankInfos.push({
        'bankInfoId': null,
        'companyId': null,
        'bankCode': '',
        'bankName': '',
        'openingBank': '',
        'bankAccountName': '',
        'bankAccount': '',
        'currencyCode': '',
        'currencyName': '',
        'ceeaMainAccount': '',
        'ceeaEnabled': 'Y'
      })
    },
    // 行删除
    handleDelClickBank (index, row) {
      this.allParam.bankInfos.splice(index, 1)
    },
    addOne () {
      this.allParam.contactInfos.push({
        'contactInfoId': null,
        'companyId': null,
        'ceeaGender': 'M',
        'ceeaDeptId': null,
        'ceeaDeptName': '',
        'ceeaContactMethod': '',
        'ceeaComments': '',
        'contactName': '',
        'ceeaDefaultContact': '',
        'mobileNumber': '',
        'phoneNumber': '',
        'email': '',
        'contactAddress': '',
        'position': '',
        'taxNumber': ''
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
    // 行删除
    handleDelClickFile (index, row) {
      this.otherFileList.splice(index, 1)
    },
    addOneAuth () {
      this.allParam.managementAttaches.push({
         'managementAttachId': null,
          'managementInfoId': null,
          'companyId': null,
          'fileuploadId': null,
          'authType': '',
          'authDescription': '',
          'authNum': '',
          'authDate': '',
          'authOrg': '',
          'endDate': ''
      })
    },
    // 行删除
    handleDelClickAuth (index, row) {
      this.allParam.managementAttaches.splice(index, 1)
    },
    addOneCustomer () {
      this.allParam.businessInfos.push({
        'businessInfoId': null,
        'companyId': null,
        'customer': '',
        'ceeaWorkFrequency': '',
        'ceeaArea': '',
        'ceeaComment': '',
        'preSalesVol': '',
        'preSalesAmount': ''
      })
    },
    // 行删除
    handleDelClickCustomer (index, row) {
      this.allParam.businessInfos.splice(index, 1)
    },
    addOneEquipment () {
      this.allParam.operationEquipments.push({
        'opEquipmentId': null,
        'companyId': null,
        'opInfoId': null,
        'equipmentType': '',
        'equipmentName': '',
        'specification': '',
        'quantity': '',
        'equipmentCapacity': '',
        'manufacturer': '',
        'serviceYear': ''
      })
    },
    // 行删除
    handleDelClickEquipment (index, row) {
      this.allParam.operationEquipments.splice(index, 1)
    },
    addOneProInfo () {
      this.allParam.operationProducts.push({
        'opProductId': null,
        'companyId': null,
        'opInfoId': null,
        'proBase': '',
        'proName': '',
        'proBrand': '',
        'mainTechnics': '',
        'yearOutput': '',
        'supplyCapacityRate': '',
        'proQualifiedRate': '',
        'yearTurnover': '',
        'comment': ''
      })
    },
    // 行删除
    handleDelClickProInfo (index, row) {
      this.allParam.operationProducts.splice(index, 1)
    },
    addOneQuality () {
      this.allParam.operationQualities.push({
        'opQualityId': null,
        'companyId': null,
        'opInfoId': null,
        'mainTestEquipment': '',
        'mainTestProject': ''
      })
    },
    // 行删除
    handleDelClickQuality (index, row) {
      this.allParam.operationQualities.splice(index, 1)
    },
    innerButtonClick (index) {
      this.bankRowIndex = index
    },
    innerHandleUploadSuccess (file) {
      const { id, name } = file
      this.allParam.managementAttaches[this.bankRowIndex].fileuploadId = id.toString()
      this.allParam.managementAttaches[this.bankRowIndex].authType = name
    },
    innerButtonClick2 (index) {
      this.bankRowIndex2 = index
    },
    innerHandleUploadSuccess2 (file) {
      const { id, name } = file
      this.otherFileList[this.bankRowIndex2].fileRelationId = id.toString()
      this.otherFileList[this.bankRowIndex2].fileName = name
    },
    // 移除
    innerHandleRemove (fileRelationId) {},
    // 删除银行证明文件
    innerHandleAttachmentRemove (row) {
      row.fileRelationId = ''
      row.fileName = ''
    },
    handleScriptProgress (percent) {},
    // 适配拓展字段处理
    adaptDimFieldsHandle (data) {
      let companyInfo = []
      let bankInfo = []
      let contactInfo = []
      let otherInfo = []
      if (data && data.length > 0) {
        data.forEach(element => {
          if (element.dimCode === 'companyInfo') {
            companyInfo = element.dimFieldConfigS
            if (element.dimFieldConfigS.length > 0) {
              element.dimFieldConfigS.map(item => {
                if (item.isCheck === 'Y') {
                  this.baseDimRules[item.fieldCode] = [{ required: true, message: '请输入' + item.fieldName }]
                }
              })
            }
          } else if (element.dimCode === 'bankInfo') {
            bankInfo = element.dimFieldConfigS
          } else if (element.dimCode === 'contactInfo') {
            contactInfo = element.dimFieldConfigS
          } else if (element.dimCode === 'otherInfo') {
            otherInfo = element.dimFieldConfigS
          }
        })
      }
      this.baseInfoDimFieldContexts = companyInfo
      this.bankDimFieldContexts = bankInfo
      this.linkManDimFieldContexts = contactInfo
      this.otherDimFieldContexts = otherInfo
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
      if (this.uploadSec === 'baseInfo') { // 营业执照
        this.allParam.companyInfo[fieldID] = id.toString()
        this.allParam.companyInfo[fieldName] = name
        // 判断是否需要OCR识别 境内供应商 && 开启OCR
        if (this.allParam.companyInfo.overseasRelation === 'INSIDE' && this.isEnableOcr === 'Y') {
          this.fileUploadId = id // ocr 文件入参ID
          this.ocrVisible = true
        }
      } else if (this.uploadSec === 'bank') { // 银行凭证
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
        this.fileRefresh = true
        getCompanyForEdit({ companyId }).then(res => {
          if (res) {
            if (res.data.companyInfo) {
              let companyInfo = res.data.companyInfo
              this.dataStatus = companyInfo.status // 单据状态
              // 信息状态为提交显示到第四步 ///调试完以后再打开
              this.relModel.relform.categoryRels = companyInfo.categoryRels
              this.relModel.relform.overseasRelation = companyInfo.overseasRelation
              this.relModel.relform.companyType = companyInfo.companyType
              this.curRel = companyInfo.overseasRelation
              this.curType = companyInfo.companyType

              // 加载市
              if (companyInfo.companyProvince) {
                let companyProvince = companyInfo.companyProvince
                this.getRegionData(companyProvince) // 加载市
              }
              debugger
              this.allParam.companyInfo = _omit(companyInfo, ['applicationDate', 'creationDate', 'lastUpdateDate'])
              this.allParam.companyInfo.companyCreationDate = companyInfo.companyCreationDate ? this.$dayjs(companyInfo.companyCreationDate).valueOf() : ''
              this.allParam.companyInfo.businessStartDate = companyInfo.businessStartDate ? this.$dayjs(companyInfo.businessStartDate).valueOf() : '' // 开始时间
              this.allParam.companyInfo.businessEndDate = companyInfo.businessEndDate ? this.$dayjs(companyInfo.businessEndDate).valueOf() : ''

              this.baseDimModel = companyInfo.dimFieldContexts // 拓展字段值
              // this.categoryRels = companyInfo.categoryRels
              this.allParam = res.data// yuyue3
              this.allParam.userInfo = res.data.userInfo || {}
              this.allParam.companyInfoDetail = res.data.companyInfoDetail || {}
              this.switchBaseRules() // 切换必填规则
            }
            if (res.data.bankInfos && res.data.bankInfos[0]) {
              let bankInfos = res.data.bankInfos[0]
              this.bankModel.bankForm.bankInfoId = bankInfos.bankInfoId
              this.bankModel.bankForm.openingBank = bankInfos.openingBank
              this.bankModel.bankForm.unionCode = bankInfos.unionCode
              this.bankModel.bankForm.swiftCode = bankInfos.swiftCode
              this.bankModel.bankForm.bankAccountName = bankInfos.bankAccountName
              this.bankModel.bankForm.bankAccount = bankInfos.bankAccount
              this.bankModel.bankForm.proof = bankInfos.proof
              this.bankModel.bankForm.proofFileId = bankInfos.proofFileId
              this.bankDimModel = bankInfos.dimFieldContexts
            }
            if (res.data.contactInfos && res.data.contactInfos[0]) {
              let contactInfos = res.data.contactInfos[0]
              this.linkManModel.linkManForm.contactInfoId = contactInfos.contactInfoId
              this.linkManModel.linkManForm.contactName = contactInfos.contactName
              this.linkManModel.linkManForm.mobileNumber = contactInfos.mobileNumber
              this.linkManModel.linkManForm.position = contactInfos.position
              this.linkManModel.linkManForm.phoneNumber = contactInfos.phoneNumber
              this.linkManModel.linkManForm.email = contactInfos.email
              this.linkManModel.linkManForm.contactAddress = contactInfos.contactAddress
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
            if (res.data.attachFiles) this.attachTableData = res.data.attachFiles
            // 查询魔板配置信息
            let pareme = this.relModel.relform
            getConfigByTemplate(pareme).then(res => {
              if (res) {
                this.adaptDimFieldsHandle(res.data) // 适配拓展字段
              }
            })
          }
        })
      }
    },
    saveHandle () {
      this.$http({
        url: '/api-sup/info/vendorInformation/updateInformation',
        method: 'POST',
        data: this.allParam,
        loading: true
      }).then(data => {
        debugger
        this.backToHandle()
      })
      .catch(err => {
        console.log(err)
      })
    },
    rejectHandle () {},
    backToHandle () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('vendorProfileList.getQuerydata')
    },
    // 暂存
    stagingHandle () {
      console.log('this.allParam---', this.allParam)
      debugger
      // return
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
          /* if (_this.baseInfoDimFieldContexts.length > 0) {
            let dValid = false
            _this.$refs.baseDimForm.validate(dimValid => {
              dValid = dimValid
            })
            if (!dValid) {
              this.$message({
                message: this.$t('common.pleasefinishRequired'),//'请输入必填项'
                type: 'error'
              })
              return false
            }
          } */
          this.dataHandle('submit') // 提交数据
          return

          _this.$refs.bankForm.validate(valid1 => {
            if (valid1) {
              _this.$refs.linkManForm.validate(valid2 => {
                if (valid2) {
                  // 判断附件是否上传
                  if (!this.$refs.sceneAttachment.validRequired()) {
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
            } else {
              this.$message({
                message: this.$t('common.pleasefinishRequired'), // '请输入必填项'
                type: 'error'
              })
              return false
            }
          })
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
      let submitData = {}
      submitData = this.allParam
      submitData.companyInfo.categoryRels = this.relModel.relform.categoryRels
      submitData.fileUploads = this.$refs.sceneAttachment.sceneAttaches // 附件对象
      if (type === 'submit') {
        submitCompany(submitData).then(res => {
          if (res) {

          }
        })
      } else {
        saveOrUpdateCompany(submitData).then(res => {
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
      this.allParam.companyInfo.businessStartDate = licenseData.businessStartDate
      this.allParam.companyInfo.businessEndDate = licenseData.businessEndDate
      this.allParam.companyInfo.companyCreationDate = licenseData.companyCreationDate
      this.ocrVisible = false
    },
    // 关掉OCR弹框
    ocrClose () {
      this.ocrVisible = false
    }
  }
}
</script>
<style lang="scss" scoped>
.companyInfo-sec{
  min-height: 100%;
  .comInfosteps{
    padding: 12px 4%;
  }
  .info-fill-area{
    padding: 20px 10px;
  }
  .overseasRelation{
    padding-top: 100px;
    width: 50%;
    margin: 0 auto;
  }
  .companyInfoFill{
    padding-right:24%;
    padding-bottom: 40px;
  }
  .submitSuccess{
    padding-top: 100px;
    text-align: center;
    .success-icon-tip{
      font-size: 28px;
      color: #13ce66;
    }
    .success-icon-word{
      font-size: 16px;
      line-height: 40px;
    }
    .success-icon-des{
      font-size: 14px;
      color: #666;
    }
  }
  .info-fill-progress{
    position: absolute;
    width: 24%;
    top: 0;
    right: 0;
    bottom: 0px;
    background: #fff;
    .progressTitle {
      height: 40px;
      line-height: 40px;
      padding-left: 30px;
      background: #f7f9fa;
      font-size: 14px;
    }
    .progressCont{
      padding: 6px;
      .progressItem{
        padding: 8px 10px;
        border-radius: 3px;
        .progress-title{
          font-size: 14px;
        }
        .progress-bar{
          height: 15px;
        }
        &.current{
          background: #f5f5f5;
        }
      }
    }
  }
  &.widthCtrl{
    .comInfosteps{
      width: 75.5%;
    }
  }
  .opt-row{
    margin-bottom: 10px;
  }
}
.companyInfo-sec .el-steps.comInfosteps .el-step__title{
  font-size: 14px;
}
.download-link-wrap .close-icon{
  cursor: pointer;
  display: inline-block;
  vertical-align: middle;
}
.info-fill-progress .el-progress-bar {
  padding-right: 70px;
  margin-right: -70px;
}
.left_div>p{
  margin: 0;
}
.sceneAttachment{
  pointer-events: none;
  opacity: 0.6;
}

</style>
