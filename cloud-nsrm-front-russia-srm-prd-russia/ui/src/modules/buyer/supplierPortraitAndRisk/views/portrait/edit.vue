<template>
  <el-container class="riskEdit flex-container" direction="vertical">
    <el-main>
      <el-collapse v-model="activeNames" @change="handleChange">
        <el-collapse-item :title="$t('vendorMod.basicInformation')" name="1">
          <srm-row :gutter="32" style="margin-bottom:20px">
            <srm-col>
              <div class="grid-content bg-purple">
                {{ $t("vendorMod.isBacklist") }}:
                {{ dictClass.getDictLabel('YES_OR_NO',title.isBacklist) }}
              </div>
            </srm-col>
            <srm-col>
              <div class="grid-content bg-purple">
                {{ $t("vendorMod.ceeaRegisteredCapital") }}：
                <span>{{ title.registeredCapital }}{{ $t("vendorMod.millionYuan") }}</span>
              </div>
            </srm-col>
            <srm-col>
              <div class="grid-content bg-purple">
                {{ $t("vendorMod.legalPerson") }}：
                <span>{{ title.legalPerson }}</span>
              </div>
            </srm-col>
            <srm-col>
              <div class="grid-content bg-purple">
                {{ $t("vendorMod.supplierNature") }}
                <span>{{ title.companyType }}</span>
              </div>
            </srm-col>
          </srm-row>

          <srm-row :gutter="32" style="margin-bottom:20px">
            <srm-col>
              <div class="grid-content bg-purple">
                {{ $t("vendorMod.establishedTime") }}
                <span style="color:#000000">{{ title.companyCreationDate }}</span>
              </div>
            </srm-col>
            <srm-col>
              <div class="grid-content bg-purple">
                {{ $t("vendorMod.businessType") }}
                <span>{{ dictClass.getDictLabel('SUP_BUSINESS_TYPE',title.ceeaSupBusinessType) }}</span>
              </div>
            </srm-col>
            <srm-col>
              <div class="grid-content bg-purple">
                {{ $t("vendorMod.seniority") }}
                <span>{{ title.serviceLength }}</span>
              </div>
            </srm-col>
            <srm-col>
              <div class="grid-content bg-purple">
                {{ $t("vendorMod.supplierBusinessModel") }}
                <span>{{ dictClass.getDictLabel('BIZ_MODEL',title.ceeaBusinessModel) }}</span>
              </div>
            </srm-col>
          </srm-row>
          <srm-row :gutter="32" style="margin-bottom:20px;">
            <srm-col
              :xs="24"
              :sm="24"
              :md="24"
              :lg="24"
              :xl="24"
            >
              <div class="grid-content bg-purple">
                {{ $t("vendorMod.businessScope") }}:
                <span>{{ title.businessScope }}</span>
              </div>
            </srm-col>
          </srm-row>
        </el-collapse-item>
        <el-collapse-item :title="$t('vendorMod.riskFollow')" name="2">
          <el-table :data="monitoringList" max-height="345px" border stripe>
            <el-table-column type="index" :label="$t('vendorMod.numericalOrder')" width="60" />
            <el-table-column prop="riskCode" :label="$t('supRisk.riskCode')">
              <template slot-scope="scope">
                <el-button type="text" @click="goToRisk(scope.row)">
                  {{ scope.row.riskCode }}
                </el-button>
              </template>
            </el-table-column>
            <el-table-column prop="riskType" :label="$t('supRisk.riskType')">
              <template slot-scope="scope">
                <span>{{ dictClass.getDictLabel('RISK_TYPE',scope.row.riskType) }}</span>
              </template>
            </el-table-column>
            <el-table-column
              prop="riskInfluencesDescription"
              :label="$t('supRisk.riskDescription')"
            />
            <el-table-column prop="categoryName" :label="$t('vendorMod.categoryName')" />
            <el-table-column prop="status" :label="$t('supRisk.status')">
              <template slot-scope="scope">
                <span>{{ dictClass.getDictLabel('RISK_MONITORING_STATUS',scope.row.status) }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
        <el-collapse-item :title="$t('vendorMod.riskRadar')" name="3">
          <el-tabs v-model="activeName" type="card">
            <!-- 企业工商信息 -->
            <el-tab-pane :label="$t('vendorMod.businessInformation')" name="third">
              <p>{{ $t("vendorMod.registerInformation") }}</p>
              <table class="my-table">
                <tbody>
                  <tr>
                    <th v-for="(item,index) in 8" v-show="false" :key="index" scope="col" />
                  </tr>
                  <tr>
                    <td class="grey">
                      {{ $t("vendorMod.corporateName") }}
                    </td>
                    <td>{{ R3_ObjData.registerInfo.enterpriseName }}</td>
                    <td class="grey">
                      {{ $t("vendorMod.socialCreditCode") }}
                    </td>
                    <td>{{ R3_ObjData.registerInfo.unifiedSocialCreditCode }}</td>
                    <td class="grey">
                      {{ $t("vendorMod.enterpriseRegistration") }}
                    </td>
                    <td>{{ R3_ObjData.registerInfo.registerType }}</td>
                    <td class="grey">
                      {{ $t("vendorMod.email") }}
                    </td>
                    <td>{{ R3_ObjData.registerInfo.email }}</td>
                  </tr>
                  <tr>
                    <td class="grey">
                      {{ $t("contractMod.mobileNumber") }}
                    </td>
                    <td>{{ R3_ObjData.registerInfo.telephone }}</td>
                    <td class="grey">
                      {{ $t("vendorMod.website") }}
                    </td>
                    <td>{{ R3_ObjData.registerInfo.website }}</td>
                    <td class="grey">
                      {{ $t("vendorMod.businessAddress") }}
                    </td>
                    <td>{{ R3_ObjData.registerInfo.businessAddress }}</td>
                    <td class="grey">
                      {{ $t("components.address.country") }}
                    </td>
                    <td>{{ R3_ObjData.registerInfo.county }}</td>
                  </tr>
                  <tr>
                    <td class="grey">
                      {{ $t("vendorMod.provinceA") }}
                    </td>
                    <td>{{ R3_ObjData.registerInfo.province }}</td>
                    <td class="grey">
                      {{ $t("components.address.cityName") }}
                    </td>
                    <td>{{ R3_ObjData.registerInfo.city }}</td>
                    <td class="grey">
                      {{ $t("vendorMod.domainName") }}
                    </td>
                    <td>{{ R3_ObjData.registerInfo.areaName }}</td>
                    <td class="grey">
                      {{ $t("vendorMod.location") }}
                    </td>
                    <td>{{ R3_ObjData.registerInfo.address }}</td>
                  </tr>
                  <tr>
                    <td class="grey">
                      {{ $t("vendorMod.legalPerson") }}
                    </td>
                    <td>{{ R3_ObjData.registerInfo.legalRepresentative }}</td>
                    <td class="grey">
                      {{ $t("vendorMod.companyTypeMsg") }}
                    </td>
                    <td>{{ R3_ObjData.registerInfo.enterpriseType }}</td>

                    <td class="grey">
                      {{ $t("vendorMod.registrationAuthority") }}
                    </td>
                    <td>{{ R3_ObjData.registerInfo.registrationInstitution }}</td>
                    <td class="grey">
                      {{ $t("vendorMod.managementForms") }}
                    </td>
                    <td>{{ R3_ObjData.registerInfo.operateStatus }}</td>
                  </tr>
                  <tr>
                    <td class="grey">
                      {{ $t("vendorMod.businessScope") }}
                    </td>
                    <td>
                      <el-tooltip :content="R3_ObjData.registerInfo.businessScope">
                        <div>
                          {{ R3_ObjData.registerInfo.businessScope && R3_ObjData.registerInfo.businessScope.toString().slice(0,10) }}
                        </div>
                      </el-tooltip>
                    </td>
                    <td class="grey">
                      {{ $t("vendorMod.creationDate") }}
                    </td>
                    <td>{{ R3_ObjData.registerInfo.establishmentDate }}</td>
                    <td class="grey">
                      {{ $t("vendorMod.dateIssue") }}
                    </td>
                    <td>{{ R3_ObjData.registerInfo.issueDate }}</td>
                    <td class="grey">
                      {{ $t("vendorMod.registeredAssets") }}
                    </td>
                    <td>{{ R3_ObjData.registerInfo.registeredCapital }}</td>
                  </tr>
                  <tr>
                    <td class="grey">
                      {{ $t("vendorMod.startDateBusiness") }}
                    </td>
                    <td>{{ R3_ObjData.registerInfo.validityFrom }}</td>
                    <td class="grey">
                      {{ $t("vendorMod.businessDeadline") }}
                    </td>
                    <td>{{ R3_ObjData.registerInfo.validityTo }}</td>
                  </tr>
                </tbody>
              </table>
              <p>{{ $t("vendorMod.industryInformation") }}</p>
              <srm-row :gutter="32" style="border: 1px solid #ccc;padding: 5px;">
                <srm-col>
                  <div class="grid-content bg-purple">
                    {{ $t("vendorMod.IndustrySpecific") }}：<span>{{
                      R3_ObjData.industryInfo.industry
                    }}</span>
                  </div>
                </srm-col>
                <srm-col>
                  <div class="grid-content bg-purple">
                    {{ $t("vendorMod.industryCategories") }}：<span>{{
                      R3_ObjData.industryInfo.subIndustry
                    }}</span>
                  </div>
                </srm-col>
                <srm-col>
                  <div class="grid-content bg-purple">
                    {{ $t("vendorMod.industryCategory") }}：<span>{{
                      R3_ObjData.industryInfo.middleCategory
                    }}</span>
                  </div>
                </srm-col>
                <srm-col>
                  <div class="grid-content bg-purple">
                    {{ $t("vendorMod.industrySubCategories") }}：<span>{{
                      R3_ObjData.industryInfo.smallCategory
                    }}</span>
                  </div>
                </srm-col>
              </srm-row>
              <p>{{ $t("vendorMod.shareholderInformation") }}</p>
              <el-table
                :data="R3_ObjData.shareholderInfo"
                border
                stripe
                class="r_table_class"
                max-height="345px"
              >
                <el-table-column type="index" :label="$t('vendorMod.numericalOrder')" width="60" />
                <el-table-column
                  prop="shareholderName"
                  :label="$t('vendorMod.shareholderName')"
                  min-width="150"
                  show-overflow-tooltip
                />
                <el-table-column
                  prop="shareholderType"
                  :label="$t('vendorMod.shareholdersType')"
                  width="150"
                  show-overflow-tooltip
                />
                <el-table-column
                  prop="subscribedCapital"
                  :label="$t('vendorMod.contributionSubscribed')"
                  width="150"
                  show-overflow-tooltip
                />
                <el-table-column
                  prop="subscribedTime"
                  :label="$t('vendorMod.subscribedDate')"
                  width="150"
                  show-overflow-tooltip
                />
                <el-table-column
                  prop="shareRatio"
                  :label="$t('vendorMod.ratioShareHolding')"
                  width="150"
                  show-overflow-tooltip
                />
                <el-table-column
                  prop="actualPaymentAmount"
                  :label="$t('vendorMod.paidAmount')"
                  width="150"
                  show-overflow-tooltip
                />
                <el-table-column
                  prop="actualContributionTime"
                  :label="$t('vendorMod.actualTimeContribution')"
                  width="150"
                  show-overflow-tooltip
                />
              </el-table>
              <srm-row>
                <srm-col
                  :xs="8"
                  :sm="8"
                  :md="8"
                  :lg="8"
                  :xl="8"
                >
                  <p>{{ $t("vendorMod.keyPersonnel") }}</p>
                  <el-table
                    :data="R3_ObjData.keyPersonnel"
                    border
                    stripe
                    class="r_table_class"
                    max-height="345px"
                  >
                    <el-table-column
                      type="index"
                      :label="$t('vendorMod.serialNumber')"
                      width="60"
                    />
                    <el-table-column
                      prop="name"
                      :label="$t('vendorMod.nickname')"
                      min-width="150"
                      show-overflow-tooltip
                    />
                    <el-table-column
                      prop="position"
                      :label="$t('vendorMod.post')"
                      min-width="150"
                      show-overflow-tooltip
                    />
                  </el-table>
                </srm-col>
                <srm-col
                  :xs="16"
                  :sm="16"
                  :md="16"
                  :lg="16"
                  :xl="16"
                >
                  <p>{{ $t("vendorMod.embranchment") }}</p>
                  <el-table
                    :data="R3_ObjData.branchInfo"
                    border
                    class="r_table_class"
                    max-height="345px"
                  >
                    <el-table-column
                      type="index"
                      :label="$t('vendorMod.serialNumber')"
                      width="60"
                    />
                    <el-table-column
                      prop="branchName"
                      :label="$t('vendorMod.branchName')"
                      min-width="150"
                      show-overflow-tooltip
                    />
                    <el-table-column
                      prop="branchPrincipal"
                      :label="$t('vendorMod.headBranch')"
                      width="150"
                      show-overflow-tooltip
                    />
                    <el-table-column
                      prop="unifiedSocialCreditCode"
                      :label="$t('vendorMod.unifiedSocialCreditCode')"
                      width="150"
                      show-overflow-tooltip
                    />
                    <el-table-column
                      prop="registrationInstitution"
                      :label="$t('vendorMod.registrationAuthority')"
                      min-width="150"
                      show-overflow-tooltip
                    />
                  </el-table>
                </srm-col>
              </srm-row>
              <p>{{ $t("vendorMod.changeRecord") }}</p>
              <el-table
                :data="R3_ObjData.changeRegistration"
                border
                class="r_table_class"
                max-height="345px"
              >
                <el-table-column type="index" :label="$t('vendorMod.numericalOrder')" width="60" />
                <el-table-column
                  prop="changeItem"
                  :label="$t('vendorMod.alteration')"
                  min-width="150"
                  show-overflow-tooltip
                />
                <el-table-column
                  prop="beforeChangeContent"
                  :label="$t('vendorMod.contentBeforeChange')"
                  min-width="150"
                  show-overflow-tooltip
                />
                <el-table-column
                  prop="afterChangeContent"
                  :label="$t('vendorMod.contentAfterChange')"
                  min-width="150"
                  show-overflow-tooltip
                />
                <el-table-column
                  prop="changeDate"
                  :label="$t('vendorMod.changeDate')"
                  width="100"
                  show-overflow-tooltip
                />
              </el-table>
              <p>{{ $t("vendorMod.outboundInvestment") }}</p>
              <el-table
                :data="R3_ObjData.enterpriseForeignInvestment"
                border
                stripe
                class="r_table_class"
                max-height="345px"
              >
                <el-table-column type="index" :label="$t('vendorMod.serialNumber')" width="60" />
                <el-table-column
                  prop="enterpriseName"
                  :label="$t('vendorMod.corporateName')"
                  min-width="150"
                  show-overflow-tooltip
                />
                <el-table-column
                  prop="unifiedSocialCreditCode"
                  :label="$t('vendorMod.unifiedSocialCreditCode')"
                  width="150"
                  show-overflow-tooltip
                />
                <el-table-column
                  prop="legalRepresentative"
                  :label="$t('vendorMod.legalPerson')"
                  width="100"
                  show-overflow-tooltip
                />
                <el-table-column
                  prop="enterpriseType"
                  :label="$t('vendorMod.companyTypeMsg')"
                  width="150"
                  show-overflow-tooltip
                />
                <el-table-column
                  prop="registrationInstitution"
                  :label="$t('vendorMod.registrationAuthority')"
                  width="150"
                  show-overflow-tooltip
                />
                <el-table-column
                  prop="operateStatus"
                  :label="$t('vendorMod.managementForms')"
                  width="100"
                  show-overflow-tooltip
                />
                <el-table-column
                  prop="registeredCapital"
                  :label="$t('vendorMod.registeredAssets')"
                  width="150"
                  show-overflow-tooltip
                />
                <el-table-column
                  prop="registeredCapitalCurrency"
                  :label="$t('vendorMod.currencyRegisteredCapital')"
                  width="150"
                  show-overflow-tooltip
                />
                <el-table-column
                  prop="realityCapital"
                  :label="$t('vendorMod.capitalContribution')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 认缴出资币种 -->
                <el-table-column
                  prop="realityCapitalCurrency"
                  :label="$t('vendorMod.subContributeCurrency')"
                  width="120"
                  show-overflow-tooltip
                />
                <!-- 出资方式 -->
                <el-table-column
                  prop="investmentForm"
                  :label="$t('vendorMod.investmentType')"
                  width="100"
                  show-overflow-tooltip
                />
                <!-- 出资比例 -->
                <el-table-column
                  prop="investmentRatio"
                  :label="$t('vendorMod.fundedRatio')"
                  width="100"
                  show-overflow-tooltip
                />
                <!-- 成立日期 -->
                <el-table-column
                  prop="establishmentDate"
                  :label="$t('vendorMod.creationDate')"
                  width="100"
                  show-overflow-tooltip
                />
              </el-table>
              <p>
                <!-- 企业法人代表对外投资信息 -->
                {{ $t("vendorMod.foreignInvestInfo") }}
              </p>
              <el-table
                :data="R3_ObjData.legalRepresentativeForeignInvestment"
                border
                stripe
                class="r_table_class"
                max-height="345px"
              >
                <!-- 序号 -->
                <el-table-column type="index" :label="$t('common.sort')" width="60" />
                <!-- 职位 -->
                <el-table-column
                  prop="position"
                  :label="$t('dataConfMod.position')"
                  min-width="150"
                  show-overflow-tooltip
                />
                <!-- 公司名称 -->
                <el-table-column
                  prop="enterpriseName"
                  :label="$t('dataConfMod.ceeaCompanyName')"
                  min-width="150"
                  show-overflow-tooltip
                />
                <!-- 姓名 -->
                <el-table-column
                  prop="name"
                  :label="$t('dataConfMod.userName')"
                  width="100"
                  show-overflow-tooltip
                />
                <!-- 公司类型 -->
                <el-table-column
                  prop="enterpriseType"
                  :label="$t('vendorMod.companyType1')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 经营状态 -->
                <el-table-column
                  prop="operateStatus"
                  :label="$t('vendorMod.operateStatus')"
                  width="100"
                  show-overflow-tooltip
                />
                <!-- 注册资本 -->
                <el-table-column
                  prop="registeredCapital"
                  :label="$t('bidMod.registeredCapital')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 身份 -->
                <el-table-column
                  prop="identity"
                  :label="$t('vendorMod.identity')"
                  width="100"
                  show-overflow-tooltip
                />
                <!-- 社会统一信用代码 -->
                <el-table-column
                  prop="unifiedSocialCreditCode"
                  :label="$t('vendorMod.lcCode')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 注册资本币种 -->
                <el-table-column
                  prop="registeredCapitalCurrency"
                  :label="$t('vendorMod.registeredCapitalCurrency')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 出资方式 -->
                <el-table-column
                  prop="investmentForm"
                  :label="$t('vendorMod.investmentType')"
                  width="100"
                  show-overflow-tooltip
                />
                <!-- 出资比例 -->
                <el-table-column
                  prop="investmentRatio"
                  :label="$t('vendorMod.fundedRatio')"
                  width="100"
                  show-overflow-tooltip
                />
              </el-table>
              <p>
                <!-- 企业法人代表对外任职信息 -->
                {{ $t("vendorMod.externalEmployInfo") }}
              </p>
              <el-table
                :data="R3_ObjData.legalRepresentativeForeignTenure"
                border
                stripe
                class="r_table_class"
                max-height="345px"
              >
                <!-- 序号 -->
                <el-table-column type="index" :label="$t('common.sort')" width="60" />
                <!-- 职位 -->
                <el-table-column
                  prop="position"
                  :label="$t('dataConfMod.position')"
                  min-width="150"
                  show-overflow-tooltip
                />
                <!-- 公司名称 -->
                <el-table-column
                  prop="enterpriseName"
                  :label="$t('dataConfMod.ceeaCompanyName')"
                  min-width="150"
                  show-overflow-tooltip
                />
                <!-- 姓名 -->
                <el-table-column
                  prop="name"
                  :label="$t('dataConfMod.userName')"
                  width="100"
                  show-overflow-tooltip
                />
                <!-- 公司类型 -->
                <el-table-column
                  prop="enterpriseType"
                  :label="$t('vendorMod.companyType1')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 经营状态 -->
                <el-table-column
                  prop="operateStatus"
                  :label="$t('vendorMod.operateStatus')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 注册资本 -->
                <el-table-column
                  prop="registeredCapital"
                  :label="$t('bidMod.registeredCapital')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 身份 -->
                <el-table-column
                  prop="identity"
                  :label="$t('vendorMod.identity')"
                  width="100"
                  show-overflow-tooltip
                />
                <!-- 社会统一信用代码 -->
                <el-table-column
                  prop="unifiedSocialCreditCode"
                  :label="$t('vendorMod.lcCode')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 注册资本币种 -->
                <el-table-column
                  prop="registeredCapitalCurrency"
                  :label="$t('vendorMod.registeredCapitalCurrency')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 出资方式 -->
                <el-table-column
                  prop="investmentForm"
                  :label="$t('vendorMod.investmentType')"
                  width="100"
                  show-overflow-tooltip
                />
                <!-- 出资比例 -->
                <el-table-column
                  prop="investmentRatio"
                  :label="$t('vendorMod.fundedRatio')"
                  width="100"
                  show-overflow-tooltip
                />
              </el-table>
              <p>
                <!-- 股权出质 -->
                {{ $t("vendorMod.pledgeEquity") }}
              </p>
              <el-table
                :data="R3_ObjData.equityPledge"
                border
                stripe
                class="r_table_class"
                max-height="345px"
              >
                <!-- 序号 -->
                <el-table-column type="index" :label="$t('common.sort')" width="60" />
                <!-- 登记编号 -->
                <el-table-column
                  prop="registrationNum"
                  :label="$t('vendorMod.registrationNum')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 出质人 -->
                <el-table-column
                  prop="pledgor"
                  :label="$t('vendorMod.pledger')"
                  min-width="150"
                  show-overflow-tooltip
                />
                <!-- 出质股权数额 -->
                <el-table-column
                  prop="amount"
                  :label="$t('vendorMod.pledgeEquityAmoun')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 质权人 -->
                <el-table-column
                  prop="pawnee"
                  :label="$t('vendorMod.pledgee')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 股权出质设立登记日期 -->
                <el-table-column
                  prop="registrationDate"
                  :label="$t('vendorMod.equityPledgeEstablishRegisterDate')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 公示时间 -->
                <el-table-column
                  prop="publicityDate"
                  :label="$t('vendorMod.publicityDate')"
                  width="100"
                  show-overflow-tooltip
                />
                <!-- 状态 -->
                <el-table-column
                  prop="status"
                  :label="$t('common.status')"
                  width="100"
                  show-overflow-tooltip
                />
                <!-- 出质人证件号 -->
                <el-table-column
                  prop="pledgorKeyNo"
                  :label="$t('vendorMod.pledgorKeyNo')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 质权人证件号 -->
                <el-table-column
                  prop="pawneeKeyNo"
                  :label="$t('vendorMod.pawneeKeyNo')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 出质股权标的企业 -->
                <el-table-column
                  prop="relatedEnterpriseName"
                  :label="$t('vendorMod.relatedEnterpriseName')"
                  min-width="150"
                  show-overflow-tooltip
                />
              </el-table>
              <p>
                <!-- 动产抵押 -->
                {{ $t("vendorMod.chattelMortgage") }}
              </p>
              <el-table
                :data="R3_ObjData.chattelMortgage"
                border
                stripe
                class="r_table_class"
                max-height="345px"
              >
                <!-- 序号 -->
                <el-table-column type="index" :label="$t('common.sort')" width="60" />
                <!-- 登记编号 -->
                <el-table-column
                  prop="registrationNum"
                  :label="$t('vendorMod.registrationNum')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 登记机关 -->
                <el-table-column
                  prop="registrationOffice"
                  :label="$t('vendorMod.registrationAuthority')"
                  min-width="100"
                  show-overflow-tooltip
                />
                <!-- 登记日期 -->
                <el-table-column
                  prop="registrationDate"
                  :label="$t('bidMod.startDate')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 公示时间 -->
                <el-table-column
                  prop="publicityDate"
                  :label="$t('vendorMod.publicityDate')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 被担保债券数额 -->
                <el-table-column
                  prop="amount"
                  :label="$t('vendorMod.securedBondAmount')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 状态 -->
                <el-table-column
                  prop="status"
                  :label="$t('common.status')"
                  width="100"
                  show-overflow-tooltip
                />
                <!-- 详情 -->
                <el-table-column
                  prop="detail"
                  :label="$t('vendorMod.detail')"
                  min-width="150"
                  show-overflow-tooltip
                />
              </el-table>
              <p>
                <!-- 经营异常 -->
                {{ $t("vendorMod.businessAbnormal") }}
              </p>
              <el-table
                :data="R3_ObjData.abnormalOperation"
                border
                stripe
                class="r_table_class"
                max-height="345px"
              >
                <!-- 序号 -->
                <el-table-column type="index" :label="$t('common.sort')" width="60" />
                <!-- 列入经营异常名录原因 -->
                <el-table-column
                  prop="shiftInReason"
                  :label="$t('vendorMod.shiftExceptionReason')"
                  min-width="150"
                  show-overflow-tooltip
                />
                <!-- 移入机关 -->
                <el-table-column
                  prop="shiftInOffice"
                  :label="$t('vendorMod.shiftInOffice')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 列入日期 -->
                <el-table-column
                  prop="shiftInDate"
                  :label="$t('vendorMod.shiftInDate')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 移出经营异常名录原因 -->
                <el-table-column
                  prop="shiftOutReason"
                  :label="$t('vendorMod.shiftOutReason')"
                  min-width="150"
                  show-overflow-tooltip
                />
                <!-- 移出机关 -->
                <el-table-column
                  prop="shiftOutOffice"
                  :label="$t('vendorMod.shiftOutOffice')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 移出日期 -->
                <el-table-column
                  prop="shiftOutDate"
                  :label="$t('vendorMod.shiftOutDate')"
                  width="150"
                  show-overflow-tooltip
                />
              </el-table>
              <p>
                <!-- 行政处罚 -->
                {{ $t("vendorMod.administrativePenalty") }}
              </p>
              <el-table
                :data="R3_ObjData.administrativePenalty"
                border
                stripe
                class="r_table_class"
                max-height="345px"
              >
                <!-- 序号 -->
                <el-table-column type="index" :label="$t('common.sort')" width="60" />
                <!-- 行政处罚决定书文号 -->
                <el-table-column
                  prop="documentNumber"
                  :label="$t('vendorMod.penaltyDecisionDocNum')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 违法行为类型 -->
                <el-table-column
                  prop="type"
                  :label="$t('vendorMod.violationType')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 违法事实 -->
                <el-table-column
                  prop="illegalFacts"
                  :label="$t('vendorMod.illegalFact')"
                  min-width="150"
                  show-overflow-tooltip
                />
                <!-- 行政处罚内容 -->
                <el-table-column
                  prop="content"
                  :label="$t('vendorMod.administrativePenaltyContent')"
                  min-width="150"
                  show-overflow-tooltip
                />
                <!-- 作出行政处罚决定的机关名称 -->
                <el-table-column
                  prop="office"
                  :label="$t('vendorMod.penaltyDecisionAgencyName')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 作出行政处罚决定的日期 -->
                <el-table-column
                  prop="decisionDate"
                  :label="$t('vendorMod.penaltyDecisionDate')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 行政处罚公示日期 -->
                <el-table-column
                  prop="publicityDate"
                  :label="$t('vendorMod.penaltyPublicityDate')"
                  width="150"
                  show-overflow-tooltip
                />
              </el-table>
              <p>
                <!-- 股权冻结 -->
                {{ $t("vendorMod.equityFreeze") }}
              </p>
              <el-table
                :data="R3_ObjData.equityFreeze"
                border
                stripe
                class="r_table_class"
                max-height="345px"
              >
                <!-- 序号 -->
                <el-table-column type="index" :label="$t('common.sort')" width="60" />
                <!-- 被执行人 -->
                <el-table-column
                  prop="personSubjectToEnforcement"
                  :label="$t('vendorMod.executedPerson')"
                  min-width="150"
                  show-overflow-tooltip
                />
                <!-- 股权数额 -->
                <el-table-column
                  prop="amount"
                  :label="$t('vendorMod.equityAmount')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 被执行人证照种类 -->
                <el-table-column
                  prop="idTypeOfPersonSubjectToEnforcement"
                  :label="$t('vendorMod.typeOfPersonSubjectToEnforcement')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 被执行人证照号码 -->
                <el-table-column
                  prop="idNumOfPersonSubjectToEnforcement"
                  :label="$t('vendorMod.numOfPersonSubjectToEnforcement')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 执行法院 -->
                <el-table-column
                  prop="courtOfExecution"
                  :label="$t('vendorMod.courtOfExecution')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 执行事项 -->
                <el-table-column
                  prop="itemOfExecution"
                  :label="$t('vendorMod.itemOfExecution')"
                  min-width="150"
                  show-overflow-tooltip
                />
                <!-- 执行文号 -->
                <el-table-column
                  prop="documentNumber"
                  :label="$t('vendorMod.executionNum')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 执行裁定书文号 -->
                <el-table-column
                  prop="documentNumberOfWrittenVerdict"
                  :label="$t('vendorMod.executionRulingDocNum')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 执行通知书文号 -->
                <el-table-column
                  prop="documentNumberOfNotice"
                  :label="$t('vendorMod.executeNoticeNum')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 公示时间 -->
                <el-table-column
                  prop="publicityDate"
                  :label="$t('vendorMod.publicityDate')"
                  width="100"
                  show-overflow-tooltip
                />
                <!-- 状态 -->
                <el-table-column
                  prop="status"
                  :label="$t('common.status')"
                  width="100"
                  show-overflow-tooltip
                />
                <!-- 冻结期限自 -->
                <el-table-column
                  prop="beginDateOfFreeze"
                  :label="$t('vendorMod.beginDateOfFreeze')"
                  width="120"
                  show-overflow-tooltip
                />
                <!-- 冻结期限至 -->
                <el-table-column
                  prop="endDateOfFreeze"
                  :label="$t('vendorMod.endDateOfFreeze')"
                  width="120"
                  show-overflow-tooltip
                />
                <!-- 冻结期限 -->
                <el-table-column
                  prop="timeLimitOfFreeze"
                  :label="$t('vendorMod.timeLimitOfFreeze')"
                  width="120"
                  show-overflow-tooltip
                />
              </el-table>
            </el-tab-pane>
            <!-- 企业司法全景 -->
            <el-tab-pane :label="$t('vendorMod.corporateJusticePanorama')" name="forth">
              <p>
                <!-- 司法信息汇总 -->
                {{ $t("vendorMod.judicialInfoSummary") }}
              </p>
              <table class="my-table">
                <tbody>
                  <tr>
                    <th v-for="(item,index) in 8" v-show="false" :key="index" scope="col" />
                  </tr>
                  <tr>
                    <td class="grey">
                      {{ $t("vendorMod.caseTotal[0]") }}
                    </td>
                    <td>{{ R4_ObjData.judicialStatistics.ajzs }}</td>
                    <td class="grey">
                      {{ $t("vendorMod.caseTotal[1]") }}
                    </td>
                    <td>{{ R4_ObjData.judicialStatistics.ygzs }}</td>
                    <td class="grey">
                      {{ $t("vendorMod.caseTotal[2]") }}
                    </td>
                    <td>{{ R4_ObjData.judicialStatistics.bgzs }}</td>
                    <td class="grey">
                      {{ $t("vendorMod.caseTotal[3]") }}
                    </td>
                    <td>{{ R4_ObjData.judicialStatistics.dsrcnt }}</td>
                  </tr>
                  <tr>
                    <td class="grey">
                      {{ $t("vendorMod.caseTotal[5]") }}
                    </td>
                    <td>{{ R4_ObjData.judicialStatistics.bssrcnt }}</td>
                    <td class="grey">
                      {{ $t("vendorMod.caseTotal[6]") }}
                    </td>
                    <td>{{ R4_ObjData.judicialStatistics.zssqrcnt }}</td>
                    <td class="grey">
                      {{ $t("vendorMod.caseTotal[7]") }}
                    </td>
                    <td>{{ R4_ObjData.judicialStatistics.sqzxrcnt }}</td>
                    <td class="grey">
                      {{ $t("vendorMod.caseTotal[8]") }}
                    </td>
                    <td>{{ R4_ObjData.judicialStatistics.bzxrcnt }}</td>
                  </tr>
                  <tr>
                    <td class="grey">
                      {{ $t("vendorMod.caseTotal[9]") }}
                    </td>
                    <td>{{ R4_ObjData.judicialStatistics.bsqrcnt }}</td>
                    <td class="grey">
                      {{ $t("vendorMod.caseTotal[10]") }}
                    </td>
                    <td>{{ R4_ObjData.judicialStatistics.qtcnt }}</td>
                    <td class="grey">
                      {{ $t("vendorMod.caseTotal[11]") }}
                    </td>
                    <td>{{ R4_ObjData.judicialStatistics.zxzs }}</td>
                    <td class="grey">
                      {{ $t("vendorMod.caseTotal[12]") }}
                    </td>
                    <td>{{ R4_ObjData.judicialStatistics.bdze }}</td>
                  </tr>
                  <tr>
                    <td class="grey">
                      {{ $t("vendorMod.caseTotal[13]") }}
                    </td>
                    <td>{{ R4_ObjData.judicialStatistics.wlxrs }}</td>
                  </tr>
                </tbody>
              </table>
              <p>
                <!-- 司法信息年度汇总 -->
                {{ $t("vendorMod.annualSummaryJudicialInfo") }}
              </p>
              <el-table
                :data="R4_ObjData.judicialStatisticsByYear"
                border
                stripe
                class="r_table_class"
                max-height="345px"
              >
                <el-table-column type="index" :label="$t('common.sort')" width="60" />
                <!-- 年份 -->
                <el-table-column
                  prop="nf"
                  :label="$t('dataConfMod.year')"
                  width="100"
                  show-overflow-tooltip
                />
                <!-- 案件总数 -->
                <el-table-column
                  prop="mnajzs"
                  :label="$t('vendorMod.caseTotal[0]')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 当事人地位为原告的案件总数 -->
                <el-table-column
                  prop="mnygzs"
                  :label="$t('vendorMod.caseTotal[1]')"
                  min-width="150"
                  show-overflow-tooltip
                />
                <!-- 当事人地位为被告的案件总数 -->
                <el-table-column
                  prop="mnbgzs"
                  :label="$t('vendorMod.caseTotal[2]')"
                  min-width="150"
                  show-overflow-tooltip
                />
                <!-- 当事人地位为第三人的案件总数 -->
                <el-table-column
                  prop="mndsrcnt"
                  :label="$t('vendorMod.caseTotal[3]')"
                  min-width="150"
                  show-overflow-tooltip
                />
                <!-- 当事人地位为上诉人的案件总数 -->
                <el-table-column
                  prop="mnssrcnt"
                  :label="$t('vendorMod.caseTotal[4]')"
                  min-width="150"
                  show-overflow-tooltip
                />
                <!-- 当事人地位为被上诉人的案件总数 -->
                <el-table-column
                  prop="mnbssrcnt"
                  :label="$t('vendorMod.caseTotal[5]')"
                  min-width="150"
                  show-overflow-tooltip
                />
                <!-- 当事人地位为再审申请人的案件总数 -->
                <el-table-column
                  prop="mnzssqrcnt"
                  :label="$t('vendorMod.caseTotal[6]')"
                  min-width="150"
                  show-overflow-tooltip
                />
                <!-- 当事人地位为申请执行人的案件总数 -->
                <el-table-column
                  prop="sqzxrcnt"
                  :label="$t('vendorMod.caseTotal[7]')"
                  min-width="150"
                  show-overflow-tooltip
                />
                <!-- 当事人地位为被执行人的案件总数 -->
                <el-table-column
                  prop="mnbzxrcnt"
                  :label="$t('vendorMod.caseTotal[8]')"
                  min-width="150"
                  show-overflow-tooltip
                />
                <!-- 当事人地位为被申请人的案件总数 -->
                <el-table-column
                  prop="mnbsqrcnt"
                  :label="$t('vendorMod.caseTotal[9]')"
                  min-width="150"
                  show-overflow-tooltip
                />
                <!-- 当事人地位为其他的案件总数 -->
                <el-table-column
                  prop="mnqtcnt"
                  :label="$t('vendorMod.caseTotal[10]')"
                  min-width="150"
                  show-overflow-tooltip
                />
                <!-- 每年申请执行人总数 -->
                <el-table-column
                  prop="mnbgzs"
                  :label="$t('vendorMod.caseTotal[14]')"
                  min-width="150"
                  show-overflow-tooltip
                />
              </el-table>
              <srm-row>
                <srm-col :initCol="2">
                  <p>
                    <!-- 案由对应的案件数 -->
                    {{ $t("vendorMod.caseTotal[15]") }}
                  </p>
                  <el-table
                    :data="R4_ObjData.caseNumOfCauseAction"
                    border
                    stripe
                    class="r_table_class"
                    max-height="345px"
                  >
                    <el-table-column type="index" :label="$t('common.sort')" width="60" />
                    <!-- 案由 -->
                    <el-table-column
                      prop="causename"
                      :label="$t('vendorMod.causeOfCase')"
                      min-width="150"
                      show-overflow-tooltip
                    />
                    <!-- 企业名称 -->
                    <el-table-column
                      prop="company"
                      :label="$t('vendorMod.companyName')"
                      width="150"
                      show-overflow-tooltip
                    />
                    <!-- 案件个数 -->
                    <el-table-column
                      prop="ayzs"
                      :label="$t('vendorMod.caseNumber')"
                      width="100"
                      show-overflow-tooltip
                    />
                  </el-table>
                </srm-col>
                <srm-col :initCol="2">
                  <p>
                    <!-- 案件地域分布 -->
                    {{ $t("vendorMod.geographicalDistributeofCase") }}
                  </p>
                  <el-table
                    :data="R4_ObjData.lawCaseDistribution"
                    border
                    stripe
                    class="r_table_class"
                    max-height="345px"
                  >
                    <el-table-column type="index" :label="$t('common.sort')" width="60" />
                    <!-- 区域 -->
                    <el-table-column
                      prop="geo"
                      :label="$t('vendorMod.area1')"
                      width="100"
                      show-overflow-tooltip
                    />
                    <!-- 企业名称 -->
                    <el-table-column
                      prop="companyname"
                      :label="$t('vendorMod.companyName')"
                      width="150"
                      show-overflow-tooltip
                    />
                    <!-- 案件个数 -->
                    <el-table-column
                      prop="dqzs"
                      :label="$t('vendorMod.caseNumber')"
                      min-width="150"
                      show-overflow-tooltip
                    />
                  </el-table>
                </srm-col>
              </srm-row>
            </el-tab-pane>
            <!-- 企业司法信息 -->
            <el-tab-pane :label="$t('vendorMod.corporateJudicialInfo')" name="fifth">
              <p>
                <!-- 被执行人 -->
                {{ $t("vendorMod.executedPerson") }}
              </p>
              <el-table
                :data="R5_ObjData.personSubjectToEnforcement"
                border
                stripe
                class="r_table_class"
                max-height="345px"
              >
                <el-table-column type="index" :label="$t('common.sort')" width="60" />
                <!-- 被执行人姓名/名称 -->
                <el-table-column
                  prop="name"
                  :label="$t('vendorMod.executedName')"
                  min-width="150"
                  show-overflow-tooltip
                />
                <!-- 立案时间 -->
                <el-table-column
                  prop="filingTime"
                  :label="$t('vendorMod.filingTime')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 案号 -->
                <el-table-column
                  prop="caseNo"
                  :label="$t('vendorMod.caseNo')"
                  width="200"
                  show-overflow-tooltip
                />
                <!-- 执行法院 -->
                <el-table-column
                  prop="executionCourt"
                  :label="$t('vendorMod.courtOfExecution')"
                  width="200"
                  show-overflow-tooltip
                />
                <!-- 执行标的 -->
                <el-table-column
                  prop="objectOfExecution"
                  :label="$t('vendorMod.objectOfExecution')"
                  width="150"
                  show-overflow-tooltip
                />
              </el-table>
              <p>
                <!-- 失信被执行人 -->
                {{ $t("vendorMod.dishonestPerson") }}
              </p>
              <el-table
                :data="R5_ObjData.defaulter"
                border
                stripe
                class="r_table_class"
                max-height="345px"
              >
                <el-table-column type="index" :label="$t('common.sort')" width="60" />
                <!-- 被执行人姓名/名称 -->
                <el-table-column
                  prop="name"
                  :label="$t('vendorMod.executedName')"
                  min-width="150"
                  show-overflow-tooltip
                />
                <!-- 组织机构代码 -->
                <el-table-column
                  prop="organizationCode"
                  :label="$t('vendorMod.organizationCode')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 法定代表人或者负责人姓名 -->
                <el-table-column
                  prop="legalPerson"
                  :label="$t('vendorMod.legalPerson1')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 执行法院 -->
                <el-table-column
                  prop="executionCourt"
                  :label="$t('vendorMod.courtOfExecution')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 省份 -->
                <el-table-column
                  prop="province"
                  :label="$t('vendorMod.province1')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 执行依据文号 -->
                <el-table-column
                  prop="accordingDocumentNo"
                  :label="$t('vendorMod.accordingDocumentNo')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 立案时间 -->
                <el-table-column
                  prop="filingTime"
                  :label="$t('vendorMod.filingTime')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 案号 -->
                <el-table-column
                  prop="caseNo"
                  :label="$t('vendorMod.caseNo')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 生效法律文书确定的义务 -->
                <el-table-column
                  prop="duty"
                  :label="$t('vendorMod.effectLegalDoc')"
                  min-width="150"
                  show-overflow-tooltip
                />
                <!-- 被执行人的履行情况 -->
                <el-table-column
                  prop="performanceStatus"
                  :label="$t('vendorMod.executedPerformance')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 失信被执行人行为具体情形 -->
                <el-table-column
                  prop="specificCircumstance"
                  :label="$t('vendorMod.specificCircumstance')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 发布时间 -->
                <el-table-column
                  prop="publishDate"
                  :label="$t('qualitySynergy.releaseDate')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 做出执行依据单位 -->
                <el-table-column
                  prop="accordingOrganization"
                  :label="$t('vendorMod.makeImpleBasicUnit')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 详情 -->
                <el-table-column
                  prop="content"
                  :label="$t('vendorMod.detail')"
                  width="150"
                  show-overflow-tooltip
                />
              </el-table>
              <p>
                <!-- 开庭公告 -->
                {{ $t("vendorMod.courtAnnouncement") }}
              </p>
              <el-table
                :data="R5_ObjData.announcementOfCourtSession"
                border
                stripe
                class="r_table_class"
                max-height="345px"
              >
                <el-table-column type="index" :label="$t('common.sort')" width="60" />
                <!-- 开庭时间 -->
                <el-table-column
                  prop="filingTime"
                  :label="$t('vendorMod.courtTime')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 案由 -->
                <el-table-column
                  prop="causeOfAction"
                  :label="$t('vendorMod.causeOfCase')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 当事人 -->
                <el-table-column
                  prop="party"
                  :label="$t('vendorMod.party')"
                  min-width="200"
                  show-overflow-tooltip
                />
                <!-- 法院名称 -->
                <el-table-column
                  prop="courtName"
                  :label="$t('vendorMod.courtName')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 审理法庭 -->
                <el-table-column
                  prop="trialCourt"
                  :label="$t('vendorMod.trialCourt')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 审理法官 -->
                <el-table-column
                  prop="trialJudge"
                  :label="$t('vendorMod.trialJudge')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 案号 -->
                <el-table-column
                  prop="caseNumber"
                  :label="$t('vendorMod.caseNo')"
                  width="200"
                  show-overflow-tooltip
                />
                <!-- 账单信息 -->
                <el-table-column
                  prop="billingInfo"
                  :label="$t('vendorMod.billingInfo')"
                  width="150"
                  show-overflow-tooltip
                />
              </el-table>
            </el-tab-pane>
            <!-- 裁判文书列表 -->
            <el-tab-pane :label="$t('vendorMod.judgmentDocList')" name="sixth">
              <p />
              <el-table
                :data="R6_ObjData.judgmentList"
                border
                stripe
                class="r_table_class"
                max-height="345px"
              >
                <el-table-column type="index" :label="$t('common.sort')" width="60" />
                <!-- 裁判文书ID -->
                <el-table-column
                  prop="judgmentId"
                  :label="$t('vendorMod.judgmentDocId')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 企业名称 -->
                <el-table-column
                  prop="enterpriseName"
                  :label="$t('vendorMod.companyName')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 标题 -->
                <el-table-column
                  prop="title"
                  :label="$t('dataConfMod.title')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 案号 -->
                <el-table-column
                  prop="caseNumber"
                  :label="$t('vendorMod.caseNo')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 案由 -->
                <el-table-column
                  prop="causeAction"
                  :label="$t('vendorMod.causeOfCase')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 法院名称 -->
                <el-table-column
                  prop="courtName"
                  :label="$t('vendorMod.courtName')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 文书类型 -->
                <el-table-column
                  prop="documentType"
                  :label="$t('vendorMod.documentType')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 审级 -->
                <el-table-column
                  prop="trialClass"
                  :label="$t('vendorMod.trialLevel')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 诉讼类型 -->
                <el-table-column
                  prop="litigationType"
                  :label="$t('vendorMod.litigationType')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 当事人地位 -->
                <el-table-column
                  prop="relatedPosition"
                  :label="$t('vendorMod.relatedPosition')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 内容 -->
                <el-table-column
                  prop="content"
                  :label="$t('vendorMod.content')"
                  min-width="150"
                  show-overflow-tooltip
                />
                <!-- 判决日期 -->
                <el-table-column
                  prop="judgmentDate"
                  :label="$t('vendorMod.judgmentDate')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 判决金额 -->
                <el-table-column
                  prop="amountOfJudicialDecision"
                  :label="$t('vendorMod.judgmentAmount')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 胜败诉结果 -->
                <el-table-column
                  prop="resultOfVictoryOrDefeat"
                  :label="$t('vendorMod.resultOfVictoryOrDefeat')"
                  width="150"
                  show-overflow-tooltip
                />
              </el-table>
            </el-tab-pane>
            <!-- 企业舆情信息 -->
            <el-tab-pane :label="$t('vendorMod.corporatePublicOpinionInfo')" name="ninth">
              <p />
              <el-table
                :data="R9_ObjData.results"
                border
                stripe
                class="r_table_class"
                max-height="345px"
              >
                <el-table-column type="index" :label="$t('common.sort')" width="60" />
                <!-- 新闻ID -->
                <el-table-column
                  prop="newsId"
                  :label="$t('vendorMod.newsId')"
                  width="100"
                  show-overflow-tooltip
                />
                <!-- 新闻标题 -->
                <el-table-column
                  prop="title"
                  :label="$t('vendorMod.newsTitle')"
                  width="250"
                  show-overflow-tooltip
                />
                <!-- 发布时间 -->
                <el-table-column
                  prop="pubtime"
                  :label="$t('qualitySynergy.releaseDate')"
                  width="100"
                  show-overflow-tooltip
                />
                <!-- 新闻内容 -->
                <el-table-column
                  prop="content"
                  :label="$t('vendorMod.newsContent')"
                  min-width="100"
                  show-overflow-tooltip
                />
                <!-- 新闻来源 -->
                <el-table-column
                  prop="source"
                  :label="$t('vendorMod.newsSource')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 原文链接 -->
                <el-table-column
                  prop="url"
                  :label="$t('vendorMod.originalLink')"
                  width="150"
                  show-overflow-tooltip
                >
                  <template slot-scope="scope">
                    <a :href="scope.row.url" target="_blank" rel="noopener">{{
                      scope.row.url
                    }}</a>
                  </template>
                </el-table-column>
                <!-- 对应公司名 -->
                <el-table-column
                  prop="newsCompanyName"
                  :label="$t('vendorMod.correspondCompanyName')"
                  width="150"
                  show-overflow-tooltip
                />
                <!-- 新闻标签（多个标签以逗号隔开） -->
                <el-table-column
                  prop="newsTags"
                  :label="$t('vendorMod.newsTags')"
                  width="250"
                  show-overflow-tooltip
                />
                <!-- 账单信息 -->
                <el-table-column
                  prop="billingInfo"
                  :label="$t('vendorMod.billingInfo')"
                  width="150"
                  show-overflow-tooltip
                />
              </el-table>
            </el-tab-pane>
            <!-- 风险评级 -->
            <el-tab-pane :label="$t('vendorMod.extraneousRisks')" name="extraneousRisks">
              <p />
              <el-table :data="riskData" border stripe :span-method="objectSpanMethod">
                <el-table-column prop="label" :label="$t('vendorMod.riskLabel')" width="150" />
                <el-table-column prop="riskName" :label="risk_label" width="150" />
                <el-table-column
                  prop="situation"
                  :label="$t('vendorMod.riskProfile')"
                  min-width="200"
                />
              </el-table>
            </el-tab-pane>
            <!-- 财务状况 -->
            <el-tab-pane :label="$t('vendorMod.financialCondition')" name="financialCondition">
              <srm-row>
                <srm-col :initCol="1">
                  <p />
                  <el-table :data="tableData3" border stripe max-height="345px">
                    <el-table-column
                      min-width="100"
                      prop="category"
                      :label="$t('vendorMod.classHead')"
                      show-overflow-tooltip
                    />
                    <el-table-column :label="$t('vendorMod.particularYear')">
                      <el-table-column
                        v-for="(item,index) in financialColumn"
                        :key="index + item.prop"
                        :prop="item.prop"
                        :label="item.label"
                        width="100"
                        show-overflow-tooltip
                      />
                    </el-table-column>
                    <el-table-column
                      prop="evaluate"
                      :label="$t('vendorMod.evaluate')"
                      min-width="260"
                      show-overflow-tooltip
                    />
                  </el-table>
                </srm-col>
              </srm-row>
            </el-tab-pane>
            <!-- 经营分析 -->
            <el-tab-pane :label="$t('vendorMod.operationAnalysis')" name="operationAnalysis">
              <srm-row>
                <srm-col :initCol="1">
                  <p />
                  <!-- <h3>主体失信(单位：元/次数)</h3> -->
                  <el-table :data="tableData4" border stripe max-height="345px">
                    <el-table-column width="120" prop="categoryA" label show-overflow-tooltip />
                    <el-table-column
                      width="120"
                      prop="category"
                      :label="$t('vendorMod.classHead')"
                      show-overflow-tooltip
                    />
                    <el-table-column :label="$t('vendorMod.particularYear')">
                      <el-table-column
                        v-for="(item,index) in financialRatingColumn"
                        :key="item.prop + index"
                        :prop="item.prop"
                        :label="item.label"
                        width="120"
                        show-overflow-tooltip
                      >
                        <!-- <template slot-scope="scope">
                          {{ scope.row[item.prop] ? `${scope.row[item.prop]}%` : "" }}
                        </template> -->
                      </el-table-column>
                    </el-table-column>
                    <el-table-column
                      prop="evaluate"
                      :label="$t('vendorMod.evaluate')"
                      min-width="260"
                      show-overflow-tooltip
                    />
                  </el-table>
                  <p style="font-size:16px">
                    {{
                      `${this.$t(
                        "vendorMod.businessAnalysisConclusion"
                      )}${financialAssess_2title}`
                    }}
                  </p>
                </srm-col>
              </srm-row>
            </el-tab-pane>
            <!-- 企业失信 -->
            <el-tab-pane :label="$t('vendorMod.businessCreditLacke')" name="businessCreditLacke">
              <div class="the_table1">
                <h3>
                  {{ $t("vendorMod.businessCreditLacke")

                  }}{{ $t("vendorMod.identityElement") }}
                </h3>
                <el-table :data="r8DiscreditDto1" border stripe max-height="345px">
                  <el-table-column
                    width="150"
                    prop="category"
                    :label="$t('vendorMod.classify')"
                    show-overflow-tooltip
                  />
                  <el-table-column :label="$t('vendorMod.particularYear')">
                    <el-table-column
                      v-for="(item,index) in r8Column1"
                      :key="item.prop + index"
                      :prop="item.prop"
                      :label="item.label"
                      show-overflow-tooltip
                    />
                  </el-table-column>
                </el-table>
                <p v-if="mainDiscreditPrompt" style="font-size:10px">
                  {{ `${this.$t("vendorMod.reminder")}${mainDiscreditPrompt}` }}
                </p>
              </div>
              <div class="the_table2" style="margin-top:26px">
                <h3>
                  {{ $t("vendorMod.guaranteedFail")

                  }}{{ $t("vendorMod.identityElement") }}
                </h3>
                <el-table :data="r8DiscreditDto2" border stripe max-height="345px">
                  <el-table-column
                    width="150"
                    prop="category"
                    :label="$t('vendorMod.classify')"
                    show-overflow-tooltip
                  />
                  <el-table-column :label="$t('vendorMod.particularYear')">
                    <el-table-column
                      v-for="(item,index) in r8Column2"
                      :key="item.prop + index"
                      :prop="item.prop"
                      :label="item.label"
                      show-overflow-tooltip
                    />
                  </el-table-column>
                </el-table>
                <p v-if="guaranteeDiscreditPrompt" style="font-size:10px">
                  {{
                    `${this.$t(
                      "vendorMod.reminder"
                    )}${guaranteeDiscreditPrompt}`
                  }}
                </p>
              </div>
              <div class="the_table3" style="margin-top:26px">
                <h3>
                  {{ $t("vendorMod.associatedFaithless")

                  }}{{ $t("vendorMod.identityElement") }}
                </h3>
                <el-table :data="r8DiscreditDto3" border stripe max-height="345px">
                  <el-table-column
                    width="150"
                    prop="category"
                    :label="$t('vendorMod.classify')"
                    show-overflow-tooltip
                  />
                  <el-table-column :label="$t('vendorMod.particularYear')">
                    <el-table-column
                      v-for="(item,index) in r8Column3"
                      :key="item.prop + index"
                      :prop="item.prop"
                      :label="item.label"
                      show-overflow-tooltip
                    />
                  </el-table-column>
                </el-table>
                <p v-if="relationDiscreditPrompt" style="font-size:10px">
                  {{
                    `${this.$t("vendorMod.reminder")}${relationDiscreditPrompt}`
                  }}
                </p>
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-collapse-item>
        <!-- 合作信息 -->
        <el-collapse-item :title="$t('supRisk.cooInfo')" name="4">
          <div class="risk_title mt-0">
            <div class="blue_" />
            <div class="risk_title1">
              {{ $t("vendorMod.singleSource") }}
            </div>
          </div>
          <el-table :data="risk_tableData" max-height="345px" border stripe>
            <el-table-column type="index" :label="$t('vendorMod.numericalOrder')" width="60" />
            <el-table-column prop="orgName" :label="$t('vendorMod.ceeaOrgName')" />
            <el-table-column prop="categoryName" :label="$t('vendorMod.categoryName')" />
          </el-table>
          <div class="risk_title">
            <div class="blue_" />
            <div class="risk_title1">
              {{ $t("vendorMod.categoryRels") }}
            </div>
          </div>
          <el-table :data="shop_tableData" border stripe max-height="345px">
            <el-table-column type="index" :label="$t('common.sort')" width="60" />
            <!-- 业务实体 -->
            <el-table-column
              prop="orgName"
              :label="$t('dataConfMod.orgId')"
              min-width="200"
              show-overflow-tooltip
            />
            <!-- 品类 -->
            <el-table-column
              prop="categoryName"
              :label="$t('dataConfMod.category')"
              min-width="200"
            />
            <!-- 状态 -->
            <el-table-column
              prop="status"
              :label="$t('common.status')"
              min-width="200"
              show-overflow-tooltip
            />
          </el-table>
        </el-collapse-item>
        <!--选择品类-->
        <el-collapse-item :title="$t('dataConfMod.selectCategory')" name="5">
          <srm-row>
            <srm-col
              :xs="16"
              :sm="16"
              :md="16"
              :lg="16"
              :xl="16"
            >
              <el-form :inline="true" class="demo-form-inline">
                <el-form-item :label="$t('orderMod.categoryName')">
                  <dict-select
                    v-model="region"
                    code="categoryList"
                    :dict-class="dictClass"
                    :clearable="false"
                    @change="changeHandle"
                  />
                </el-form-item>
              </el-form>
              <div class="mt-10">
                <el-table :data="total_th" border stripe max-height="345px">
                  <!-- 年度 -->
                  <el-table-column prop="year" :label="$t('supRisk.year')" />
                  <!-- 中标次数 -->
                  <el-table-column prop="frequency" :label="$t('supRisk.winBidsNum')" />
                  <!-- 年采购金额 -->
                  <el-table-column prop="amount" :label="$t('supRisk.annualPurAmount')" />
                </el-table>
              </div>
            </srm-col>
            <srm-col
              :xs="8"
              :sm="8"
              :md="8"
              :lg="8"
              :xl="8"
            >
              <ChartBar :chart-data="totalChartData" />
            </srm-col>
          </srm-row>
        </el-collapse-item>
        <!-- 绩效信息 -->
        <el-collapse-item :title="$t('perfMod.perInformation')" name="6">
          <srm-row>
            <srm-col
              :xs="16"
              :sm="16"
              :md="16"
              :lg="16"
              :xl="16"
            >
              <el-table :data="perfOverallScoreDtos_data" border stripe max-height="345px">
                <el-table-column type="index" :label="$t('common.sort')" width="60" />
                <!-- 模板 -->
                <el-table-column
                  prop="templateName"
                  :label="$t('dataConfMod.templateName')"
                  min-width="180"
                />
                <!-- 绩效开始月份 -->
                <el-table-column
                  prop="perStartMonth"
                  :label="$t('perfMod.perStartMonth')"
                  width="120"
                />
                <!-- 绩效结束月份 -->
                <el-table-column
                  prop="perEndMonth"
                  :label="$t('perfMod.perEndMonth')"
                  width="120"
                />
                <!-- 综合得分 -->
                <el-table-column prop="score" :label="$t('perfMod.scoreAll')" width="100" />
                <!-- 品质 -->
                <el-table-column
                  prop="scoreAttribute1"
                  :label="$t('supRisk.quality')"
                  width="100"
                />
                <!-- 成本 -->
                <el-table-column prop="scoreAttribute2" :label="$t('supRisk.cost')" width="100" />
                <!-- 交付 -->
                <el-table-column
                  prop="scoreAttribute3"
                  :label="$t('supRisk.deliver')"
                  width="100"
                />
                <!-- 服务 -->
                <el-table-column
                  prop="scoreAttribute4"
                  :label="$t('supRisk.service')"
                  width="100"
                />
                <!-- 项目名称 -->
                <el-table-column
                  prop="projectName"
                  :label="$t('perfMod.projectName')"
                  min-width="150"
                />
                <!-- 等级名称 -->
                <el-table-column prop="levelName" :label="$t('perfMod.levelName')" width="100" />
                <!-- 排名 -->
                <el-table-column
                  prop="rank"
                  :label="$t('perfMod.rank')"
                  width="100"
                  :formatter="setRankLabel"
                />
              </el-table>
            </srm-col>
            <srm-col
              :xs="8"
              :sm="8"
              :md="8"
              :lg="8"
              :xl="8"
            >
              <ChartPie :chart-data="chartPerformence" />
            </srm-col>
          </srm-row>
        </el-collapse-item>
        <!-- 考核情况 -->
        <el-collapse-item :title="$t('supRisk.inspectionSituation')" name="7">
          <srm-row>
            <srm-col
              :xs="16"
              :sm="16"
              :md="16"
              :lg="16"
              :xl="16"
            >
              <el-table border stripe :data="assessData" max-height="345px">
                <el-table-column type="index" :label="$t('common.sort')" width="60" />
                <el-table-column
                  prop="assessmentNo"
                  :label="$t('perfMod.assessmentNo')"
                  min-width="150"
                >
                  <template slot-scope="scope">
                    <el-button type="text" @click="assessmentClick(scope.row)">
                      {{ scope.row.assessmentNo }}
                    </el-button>
                  </template>
                </el-table-column>
                <el-table-column prop="status" :label="$t('perfMod.assessmentStatus')">
                  <template slot-scope="scope">
                    <span>{{ dictClass.getDictLabel('VENDOR_ASSES_STATUS',scope.row.status) }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="assessmentDate" :label="$t('perfMod.assessmentDate2')" />
                <el-table-column prop="respFullName" :label="$t('perfMod.respFullName')" />
                <el-table-column
                  prop="indicatorDimension"
                  :label="$t('perfMod.indicatorDimension')"
                >
                  <template slot-scope="scope">
                    <span>{{ dictClass.getDictLabel('INDICATORS_DIM',scope.row.indicatorDimension) }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="indicatorName" :label="$t('perfMod.indicatorName')" />
                <el-table-column prop="organizationName" :label="$t('perfMod.businessEntity')" />
                <el-table-column
                  prop="assessmentPenalty"
                  :label="$t('perfMod.assessmentPenalty')"
                />
                <el-table-column
                  prop="actualAssessmentAmountY"
                  :label="$t('perfMod.actualAssessmentAmountY')"
                />
              </el-table>
            </srm-col>
            <srm-col
              :xs="8"
              :sm="8"
              :md="8"
              :lg="8"
              :xl="8"
            >
              <ChartPie :chart-data="assessChartData" :is-formatter="false" />
            </srm-col>
          </srm-row>
        </el-collapse-item>
        <!-- 待改进项 -->
        <el-collapse-item :title="$t('supRisk.toBeimproved')" name="8">
          <srm-row>
            <srm-col
              :xs="16"
              :sm="16"
              :md="16"
              :lg="16"
              :xl="16"
            >
              <div style="float:right;margin-left:10px">
                <!-- 已关闭总数： -->
                {{ $t("supRisk.totalClosed") }}
                <span style="color:brown">{{ closeSum }}</span>
              </div>
              <div style="float:right">
                <!-- 待改善总数： -->
                {{ $t("supRisk.toBeimprovedTotal") }}
                <span style="color:brown">{{ improveSum }}</span>
              </div>
              <el-table :data="improveFormDtos_tableData" border stripe max-height="345px">
                <el-table-column type="index" :label="$t('common.sort')" width="60" />
                <!-- 业务实体 -->
                <el-table-column
                  prop="organizationName"
                  :label="$t('dataConfMod.orgId')"
                  width="180"
                />
                <!-- 品类 -->
                <el-table-column prop="categoryName" :label="$t('common.category')" />
                <!-- 改善主题 -->
                <el-table-column prop="improveTitle" :label="$t('supRisk.improveTitle')" />
                <!-- 改善项目 -->
                <el-table-column
                  prop="improveProject"
                  :label="$t('supRisk.improveProject')"
                  min-width="150"
                >
                  <template slot-scope="scope">
                    <el-button type="text" @click="goToProject(scope.row)">
                      {{ scope.row.improveProject }}
                    </el-button>
                  </template>
                </el-table-column>
                <!-- 说明 -->
                <el-table-column prop="explanation" :label="$t('supRisk.explanation')" />
                <!-- 责任跟进人 -->
                <el-table-column prop="respFullName" :label="$t('supRisk.respFullName')" />
                <!-- 状态 -->
                <el-table-column prop="respUserName" :label="$t('common.status')" />
              </el-table>
            </srm-col>
            <srm-col
              :xs="8"
              :sm="8"
              :md="8"
              :lg="8"
              :xl="8"
            >
              <ChartPie :chart-data="improveChartData" />
            </srm-col>
          </srm-row>
        </el-collapse-item>
        <!-- 调查表清单 -->
        <el-collapse-item :title="$t('quest.questSupplierModule')" name="9">
          <el-table :data="questSupplierList" border stripe max-height="345px">
            <el-table-column :label="$t('common.sort')" type="index" width="60" />
            <!-- 调查表编号 -->
            <el-table-column
              prop="questNo"
              :label="$t('quest.questNo')"
              min-width="150"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <el-button type="text" @click="questSupplierResultView(scope.row)">
                  {{ scope.row.questNo }}
                </el-button>
              </template>
            </el-table-column>
            <!-- 调查表名称 -->
            <el-table-column
              prop="questName"
              :label="$t('quest.questName')"
              min-width="100"
              :show-overflow-tooltip="true"
            />
            <!-- 调查表状态 -->
            <el-table-column
              prop="approvalStatus"
              :label="$t('quest.approvalStatus')"
              min-width="100"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                {{ dictClass.getDictLabel('QUEST_SUPPLIER_APPROVE_STATUS',scope.row.approvalStatus) }}
              </template>
            </el-table-column>
            <!-- 供应商编码 -->
            <el-table-column
              prop="companyCode"
              :label="$t('quest.companyCode')"
              min-width="100"
              :show-overflow-tooltip="true"
            />
            <!-- 供应商名称 -->
            <el-table-column
              prop="companyName"
              :label="$t('quest.companyName')"
              min-width="150"
              :show-overflow-tooltip="true"
            />
            <!-- 业务组织 -->
            <el-table-column
              prop="questTemplateOrgName"
              :label="$t('quest.questTemplateOrgName')"
              min-width="150"
              :show-overflow-tooltip="true"
            />
            <!-- 调查模板编码 -->
            <el-table-column
              prop="questTemplateCode"
              :label="$t('quest.questTemplateCode')"
              min-width="100"
              :show-overflow-tooltip="true"
            />
            <!-- 调查表模板名称 -->
            <el-table-column
              prop="questTemplateName"
              :label="$t('quest.questTemplateName')"
              min-width="150"
              :show-overflow-tooltip="true"
            />
            <!-- 创建人 -->
            <el-table-column
              prop="createdFullName"
              :label="$t('quest.createdFullName')"
              min-width="100"
              :show-overflow-tooltip="true"
            />
            <!-- 创建时间 -->
            <el-table-column
              prop="creationDate"
              :label="$t('quest.creationDate')"
              min-width="150"
              :show-overflow-tooltip="true"
            />
          </el-table>
        </el-collapse-item>
        <!-- 关联挖掘 -->
      </el-collapse>
    </el-main>
  </el-container>
</template>

<script>
/* eslint-disable camelcase */
import ChartPie from '../components/chart-pie'
import ChartBar from '../components/chart-bar'
import { createDictClass } from '@/library/utils/dict/dict-utils'
import { portraitApi } from 'modb@/supplierPortraitAndRisk/api'

export default {
  filters: {},
  components: {
    ChartPie,
    ChartBar
  },
  data () {
    return {
      dictClass: createDictClass({
        COMPANY_NATURE: [],
        categoryList: [],
        categoryListA: [],
        VENDOR_IMPROVE_STATUS: [],
        VENDOR_ASSES_STATUS: [],
        CATEGORY_STATUS: [],
        QUEST_SUPPLIER_APPROVE_STATUS: [],
        RISK_TYPE: [],
        INDICATORS_DIM: [],
        RISK_MONITORING_STATUS: [],
        YES_OR_NO: [],
        SUP_BUSINESS_TYPE: []
      }),
      frequencyData: [], // 中标次数
      assessData: [],
      test: null,
      mainDiscreditPrompt: '',
      guaranteeDiscreditPrompt: '',
      relationDiscreditPrompt: '',
      financialAssess_2title: '',
      activeNames: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11'],
      title: {
        vendorLevel: '', // 供应商级别(战略级与非战略级)
        registeredCapital: '', // 注册资本
        legalPerson: '', // 法定代表人
        companyType: '', // 供应商性质
        companyCreationDate: '', // 成立时间
        ceeaSupBusinessType: '', // 业务类型
        serviceLength: '', // 服务年限
        ceeaBusinessModel: '', // 商业模式
        businessScope: '' // 经营范围
      },
      activeName: 'third',
      R1_ObjData: [],
      R2_ObjData: [],
      R3_ObjData: {
        industryInfo: {},
        registerInfo: {}
      },
      R4_ObjData: {
        judicialStatistics: {}
      },
      R5_ObjData: [],
      R6_ObjData: [],
      R7_ObjData: [],
      R8_ObjData: [],
      R9_ObjData: [],
      R10_ObjData: [],
      //
      R1_tableData: [],
      R2_tableData: [],
      R3_tableData: [],
      R4_tableData: [],
      R5_tableData: [],
      R6_tableData: [],
      R7_tableData: [],
      R8_tableData: [],
      R9_tableData: [],
      R10_tableData: [],
      monitoringList: [], // 风险跟踪
      risk_tableData: [], // 单一来源
      r8DiscreditDto_tableData: [],
      r8DiscreditDto1: [],
      r8DiscreditDto2: [],
      r8DiscreditDto3: [],
      riskData: [],
      risk_label: '',
      spanArr: [],
      yearA: '',
      yearB: '',
      yearD1: null,
      yearD2: null,
      yearD3: null,
      yearD4: null,
      yearD5: null,
      financialColumn: [],
      financialRatingColumn: [],
      r8Column1: [],
      r8Column2: [],
      r8Column3: [],
      tableData3: [],
      tableData4: [],
      yearC: '',
      yearC_1: '',
      yearD: '',
      yearE: '',
      shop_tableData: [],
      region: '',
      regionA: '',
      shopId_tableData: [], // 选择品类的ID加名字
      total_th: [], // 近三年金额
      bidFrequencies_data: [], // 近三年中标
      perfOverallScoreDtos_data: [], // 绩效数据
      chartPerformence: [], // 绩效图表数据
      get_perfOverallScoreDtos_data: [], // 选择品类获得绩效数据
      questSupplierList: [], // 调查表清单
      assesFormDtos_tableData: [], // 异常跟踪数据
      improveFormDtos_tableData: [], // 待改进项
      exceptionSum: '',
      improveSum: '',
      closeSum: '',
      improveChartData: [],
      assessChartData: [],
      totalChartData: []
    }
  },
  computed: {
    vendorId () {
      return this.$attrs.params.companyId
    }
  },
  async created () {
    const {
      data: { orgCategorys }
    } = await portraitApi.findCategory(this.vendorId)
    const dictMap = orgCategorys.reduce((obj, item) => {
      obj[item.categoryId] = {
        id: item.categoryCode,
        label: item.categoryName,
        value: item.categoryId
      }
      return obj
    }, {})
    this.dictClass.setDictionary('categoryList', Object.values(dictMap))
    const {
      data: { perfOverallScoreDtos }
    } = await portraitApi.listPage({
      vendorId: this.vendorId
    })
    const dic = perfOverallScoreDtos.reduce((obj, item) => {
      obj[item.categoryId] = {
        id: item.categoryCode,
        label: item.categoryName,
        value: item.categoryName
      }
      return obj
    }, {})
    this.dictClass.setDictionary('categoryListA', Object.values(dic))
    this.getQuestSupplierList()
  },

  async mounted () {
    this.getFrameData()
    await this.getdata()
  },
  methods: {
    goToRisk (row) { // 跳转风险详情页
      this.$router.push({
        name: 'risk',
        params: {
          from: 'fromFun',
          funName: 'risk',
          row
        }
      })
    },
    goToProject (row) { // 跳转改善单详情页
    console.log('row:::', row)
      this.$router.push({
        name: 'vendorImprovement',
        params: {
          from: 'portrait',
          row
        }
      })
    },
    assessmentClick (row) { // 跳转考核单
      this.$router.push({
        name: 'performanceAssessment',
        params: {
          from: 'portrait',
          funName: 'performanceAssessment',
          row
        }
      })
    },
    // 调查表清单
    getQuestSupplierList () {
      this.$http({
        url: '/api-sup/quest/questSupplier/listPageByParm',
        method: 'POST',
        data: {
          companyIdForQuery: this.vendorId,
          approvalStatusList: 'APPROVED',
          orgCondition: 'Y'
        },
        loading: true
      })
        .then(data => {
          this.questSupplierList = data.data.list
        })
        .catch(err => {
          console.log(err)
        })
    },
    questSupplierResultView (row) {
      this.$router.push({
        name: 'questManagement',
        params: {
          from: 'portrait',
          row
        }
      })
    },
    async changeHandle (value) {
      // const {data:amountData = []} = await portraitApi.aggregateAmount({
      //   vendorId: this.vendorId,
      //   categoryId: value
      // });
      // this.total_th = [];
      // for(let item of amountData){
      //   for(let innerItem of this.frequencyData){
      //     if(item.year === innerItem.year){
      //       this.total_th.push({
      //         ...item,
      //         ...innerItem
      //       });
      //     };
      //   };
      // };
      // this.totalChartData = this.total_th.map(item => {
      //   return {
      //     year:item.year,
      //     amount:item.amount,
      //     frequency:item.frequency,
      //   }
      // });
      this.$http({
        url: '/api-sup/info/companyInfo/getVendorImageCategoryCount',
        method: 'get',
        params: {
          vendorId: this.vendorId,
          categoryId: value
        }
      }).then(res => {
        console.log('category:::', res)
        if (res.code === '0') {
          let { bidFrequencyList = [], purchaseAmountDtoList = [] } = res.data || {}
          let arr = []
          bidFrequencyList.forEach(item => {
            purchaseAmountDtoList.forEach(innerItem => {
              if (item.year === innerItem.year) {
                arr.push({
                  year: item.year,
                  frequency: item.frequency,
                  amount: innerItem.amount
                })
              }
            })
          })
          this.total_th = this.totalChartData = arr
        }
      })
    },
    rowspan () {
      this.riskData.forEach((item, index) => {
        if (index === 0) {
          this.spanArr.push(1)
          this.position = 0
        } else {
          if (this.riskData[index].type === this.riskData[index - 1].type) {
            this.spanArr[this.position] += 1
            this.spanArr.push(0)
          } else {
            this.spanArr.push(1)
            this.position = index
          }
        }
      })
    },
    objectSpanMethod ({ row, column, rowIndex, columnIndex }) {
      // 表格合并行
      if (columnIndex === 0) {
        if (rowIndex === 0) {
          return {
            rowspan: 10, // 合并十行,保留第一行
            colspan: 1
          }
        }
        return {
          rowspan: 0,
          colspan: 1
        }
      }
      if (columnIndex === 2 || columnIndex === 3) {
        return { rowspan: 1, colspan: 2 }
      }
    },
    getFrameData () {
      // let url2 = "http://10.0.10.48?companyId=8124257151877504"
      this.$http({
        url: '/api-sup/sup/raiderInfo/queryFromEs',
        method: 'GET',
        params: { companyId: this.vendorId },
        loading: true
      })
        .then(data => {
          this.R1_ObjData = data.data.R1 || {}
          this.R2_ObjData = data.data.R2 || {}
          this.R3_ObjData = data.data.R3 || {}
          this.R3_ObjData.registerInfo = this.R3_ObjData.registerInfo || {}
          this.R3_ObjData.industryInfo = this.R3_ObjData.industryInfo || {}
          this.R4_ObjData = data.data.R4 || {}
          this.R4_ObjData.judicialStatistics = this.R4_ObjData.judicialStatistics || {}
          this.R5_ObjData = data.data.R5 || {}
          this.R6_ObjData = data.data.R6 || {}
          this.R7_ObjData = data.data.R7 || {}
          this.R8_ObjData = data.data.R8 || {}
          this.R9_ObjData = data.data.R9 || {}
          this.R10_ObjData = data.data.R10 || {}
        })
        .catch(err => {
          console.log(err)
        })
    },
    setRankLabel (row, column, value, index) {
      if (!row.rank || !row.indicatorCount) return
      return `${row.rank} / ${row.indicatorCount}`
    },
    async getdata () {
      const res = await portraitApi.listPage({
        vendorId: this.vendorId
      })
      let response = res.data || {}
      let attrs = ['exceptionSum', 'improveSum', 'closeSum']
      let titleAttrs = ['vendorLevel', 'registeredCapital', 'legalPerson', 'companyCreationDate', 'ceeaSupBusinessType', 'serviceLength', 'ceeaBusinessModel', 'businessScope', 'isBacklist']
      let { monitoringList = [], singleSourceDtos = [], vendorAssesFormList = [], bidFrequencies = [] } = response
      for (let key of attrs) {
        this[key] = response[key]
      }
      for (let key of titleAttrs) {
        this.title[key] = response[key]
      }
      this.frequencyData = bidFrequencies
      this.improveChartData = [{ name: this.$t('vendorMod.text1'), value: this.improveSum - this.closeSum }, { name: this.$t('vendorMod.text2'), value: this.closeSum }]
      this.title.companyType = this.dictClass.getDictLabel(
        'COMPANY_NATURE',
        response.companyType
      )
      this.monitoringList = []
      this.risk_tableData = []
      monitoringList.forEach(item => {
        this.monitoringList.push({
          riskCode: item.riskCode,
          riskType: item.riskType,
          riskInfluencesDescription: item.riskInfluencesDescription,
          categoryName: item.categoryName,
          riskMonitoringId: item.riskMonitoringId,
          status: item.status
        })
      })
      singleSourceDtos.forEach(item => {
        this.risk_tableData.push({
          orgName: item.orgName,
          categoryName: item.categoryName
        })
      })
      this.assessData = vendorAssesFormList
      let assessNotComplete = 0; let assessComplete = 0
      for (let item of vendorAssesFormList) {
        if (item.status === 'ASSESSED') {
          assessComplete += item.actualAssessmentAmountY
        } else {
          assessNotComplete += item.actualAssessmentAmountY
        }
      }
      this.assessChartData = [{ name: this.$t('vendorMod.text3'), value: assessNotComplete }, { name: this.$t('vendorMod.text4'), value: assessComplete }]
      const arrShop = []
      res.data.categoryRelDtos = res.data.categoryRelDtos || {}
      res.data.categoryRelDtos.forEach(item => {
        arrShop.push({
          orgName: item.orgName,
          categoryName: item.categoryName,
          status: this.dictClass.getDictLabel('CATEGORY_STATUS', item.status),
          check_status: this.$t('supRisk.viewRecords') // 查看记录
        })
      })
      this.shop_tableData = arrShop
      const perfOverallScoreDtosA = []
      res.data.perfOverallScoreDtos = res.data.perfOverallScoreDtos || {}
      res.data.perfOverallScoreDtos.forEach(item => {
        perfOverallScoreDtosA.push({
          categoryName: item.categoryName,
          perStartMonth: item.perStartMonth,
          perEndMonth: item.perEndMonth,
          score: item.score,
          templateName: item.templateName,
          scoreAttribute1: item.scoreAttribute1,
          scoreAttribute2: item.scoreAttribute2,
          scoreAttribute3: item.scoreAttribute3,
          scoreAttribute4: item.scoreAttribute4,
          scoreAttribute5: item.scoreAttribute5,
          rank: item.rank,
          levelName: item.levelName,
          projectName: item.projectName,
          indicatorCount: item.indicatorCount
        })
      })
      this.perfOverallScoreDtos_data = perfOverallScoreDtosA
      let chartPerformence = [{ name: this.$t('vendorMod.text5'), value: 0 }, { name: this.$t('vendorMod.text6'), value: 0 }, { name: this.$t('vendorMod.text7'), value: 0 }, { name: this.$t('vendorMod.text8'), value: 0 }]
      for (let item of this.perfOverallScoreDtos_data) {
        switch (item.levelName) {
          case this.$t('vendorMod.text8'):
            chartPerformence[3].value++
            break
          case this.$t('vendorMod.text7'):
            chartPerformence[2].value++
            break
          case this.$t('vendorMod.text6'):
            chartPerformence[1].value++
            break
          case this.$t('vendorMod.text5'):
            chartPerformence[0].value++
            break
          default:
            break
        }
      }
      this.chartPerformence = chartPerformence
      console.log('chartPerformence:::', this.chartPerformence)
      const assesFormDtosData = []
      res.data.assesFormDtos = res.data.assesFormDtos || {}
      res.data.assesFormDtos.forEach(item => {
        assesFormDtosData.push({
          status: this.dictClass.getDictLabel('VENDOR_ASSES_STATUS', item.status),
          currencyName: item.currencyName,
          indicatorLineDes: item.indicatorLineDes,
          indicatorName: item.indicatorName,
          materialName: item.materialName,
          categoryName: item.categoryName,
          organizationName: item.organizationName,
          indicatorDimension: item.indicatorDimension,
          actualAssessmentAmountY: item.actualAssessmentAmountY
        })
      })
      this.assesFormDtos_tableData = assesFormDtosData

      const improveFormDtosTableData = []
      res.data.improveFormDtos = res.data.improveFormDtos || {}
      res.data.improveFormDtos.forEach(item => {
        improveFormDtosTableData.push({
          organizationName: item.organizationName,
          categoryName: item.categoryName,
          improveTitle: item.improveTitle,
          improveProject: item.improveProject,
          explanation: item.explanation,
          respFullName: item.respFullName,
          vendorImproveId: item.vendorImproveId,
          improveNo: item.improveNo,
          respUserName: this.dictClass.getDictLabel('VENDOR_IMPROVE_STATUS', item.status)
        })
      })
      this.improveFormDtos_tableData = improveFormDtosTableData

      const res2 = await portraitApi.radar({
        vendorId: this.vendorId
      })
      let { riskRating = {}, financialAssess = {}, financialStatuses = [], financialRatRev = {}, financialRating = [], r8DiscreditDto = {} } = res2.data || {}
      this.risk_label = riskRating.label
      const arrA = [
        {
          label: this.$t('supRisk.riskAnalysis'), // 风险分析
          riskName: this.$t('supRisk.businessCircles'), // 工商
          situation: riskRating.business
        },
        {
          label: this.$t('supRisk.riskAnalysis'), // 风险分析
          riskName: this.$t('supRisk.involvedinLawsuit'), // 涉诉
          situation: riskRating.involved
        },
        {
          label: this.$t('supRisk.riskAnalysis'), // 风险分析
          riskName: this.$t('supRisk.publicSentiment'), // 舆情
          situation: riskRating.opinion
        },
        {
          label: this.$t('supRisk.riskAnalysis'), // 风险分析
          riskName: this.$t('supRisk.operate'), // 经营
          situation: riskRating.operating
        },
        {
          label: this.$t('supRisk.riskAnalysis'), // 风险分析
          riskName: this.$t('supRisk.recordOfDishonestySystem'), // 失信系统记录
          situation: riskRating.dishSysLog
        }
      ]
      this.riskData = arrA
      this.rowspan()
      this.financialAssess_2title = financialAssess.title || ''
      let arrB = [
        {
          category: this.$t('supRisk.totalAssets'), // 资产总额
          evaluate: financialAssess.asset
        },
        {
          category: this.$t('supRisk.totalLiabilities'), // 负债总额
          evaluate: financialAssess.liabilities
        },
        {
          category: this.$t('supRisk.totalIncome'), //  营业总收入
          evaluate: financialAssess.business
        },
        {
          category: this.$t('supRisk.netProfit'), // 净利润
          evaluate: financialAssess.netProfit
        },
        {
          category: this.$t('supRisk.totalPayTaxes'), // 纳税总额
          evaluate: financialAssess.payTaxes
        }
      ]
      this.financialColumn = []
      let keyList = ['asset', 'liabilities', 'business', 'netProfit', 'payTaxes']
      financialStatuses.forEach((item) => {
        let obj = {}
        for (let key of keyList) {
          obj[key] = item[key]
        }
        this.financialColumn.push({
          prop: 'year_' + item.year,
          label: item.year + this.$t('time.year'),
          ...obj
        })
      })
      arrB.forEach((item, index) => {
        this.financialColumn.forEach((innerItem) => {
          item[innerItem.prop] = innerItem[keyList[index]]
        })
      })
      this.tableData3 = arrB
      let arrC = [
        {
          categoryA: this.$t('supRisk.solvency'), // 偿债能力
          category: this.$t('supRisk.assetLiabilityRatio') + '(%)', // 资产负债率(%)
          evaluate: financialRatRev.assetLoadRatio
        },
        {
          categoryA: this.$t('supRisk.profitability'), // 盈利能力
          category: this.$t('supRisk.netProfit') + '(%)',
          evaluate: financialRatRev.netProfitRate
        },
        {
          categoryA: this.$t('supRisk.profitability'), // 盈利能力
          category: this.$t('supRisk.returnOnNetAssets') + '(%)',
          evaluate: financialRatRev.netAssetProfitMargin
        },
        {
          categoryA: this.$t('supRisk.operationalCapability'),
          category: this.$t('supRisk.turnoverOfTotalAssets') + '(%)',
          evaluate: financialRatRev.totalAssetTurnover
        }
      ]
      this.financialRatingColumn = []
      let keyListC = ['assetLoadRatio', 'netAssetProfitMargin', 'netProfitRate', 'totalAssetTurnover']
       financialRating.forEach((item) => {
        let obj = {}
        for (let key of keyListC) {
          obj[key] = item[key]
        }
        this.financialRatingColumn.push({
          prop: 'year_' + item.year,
          label: item.year + this.$t('time.year'),
          ...obj
        })
      })
      arrC.forEach((item, index) => {
        this.financialRatingColumn.forEach((innerItem) => {
          item[innerItem.prop] = innerItem[keyListC[index]]
        })
      })
      this.tableData4 = arrC
      let { mainDiscredit = {}, guaranteeDiscredit = {}, relationDiscredit = {} } = r8DiscreditDto
      this.mainDiscreditPrompt = mainDiscredit.mainDiscreditPrompt || ''
      this.guaranteeDiscreditPrompt = guaranteeDiscredit.guaranteeDiscreditPrompt || ''
      this.relationDiscreditPrompt = relationDiscredit.relationDiscreditPrompt || ''
      let { defaultLoanList = [], defaultCreditList = [], executorList = [], discreditExecutorList = [] } = mainDiscredit
      this.r8Column1 = []
      let keyListD = [defaultLoanList, defaultCreditList, executorList, discreditExecutorList]
      defaultLoanList.forEach((item, index) => {
        this.r8Column1.push({
          prop: 'year_' + index,
          label: item.startDate + '~' + item.endDate
        })
      })
      let arrD1 = [
        {
          category: this.$t('vendorMod.delinquentLoan') // 拖欠借款
        },
        {
          category: this.$t('vendorMod.defaultLoan') // 拖欠贷款
        },
        {
          category: this.$t('vendorMod.executedPerson') // 被执行人
        },
        {
          category: this.$t('vendorMod.untrustworthyTIme') // 失信被执行人(次)
        }
      ]
      arrD1.forEach((item, index) => {
        this.r8Column1.forEach((innerItem, innerIndex) => {
          let obj = keyListD[index][innerIndex] || {}
          item[innerItem.prop] = obj.amountOfMoney + '/' + obj.numberOfTimes
        })
      })
      this.r8DiscreditDto1 = arrD1
      let { guaranteeDiscreditList = [] } = guaranteeDiscredit
      guaranteeDiscreditList.forEach((item, index) => {
        this.r8Column2.push({
          prop: 'year_' + index,
          label: item.startDate + '~' + item.endDate
        })
      })
      let arrD2 = [
        {
          category: this.$t('vendorMod.externalGuarantee') // 对外担保
        }
      ]
      arrD2.forEach((item, index) => {
        this.r8Column2.forEach((innerItem, innerIndex) => {
          let obj = guaranteeDiscreditList[innerIndex] || {}
          item[innerItem.prop] = obj.amountOfMoney + '/' + obj.numberOfTimes
        })
      })
      this.r8DiscreditDto2 = arrD2
      let { relationDiscreditList = [] } = relationDiscredit
      relationDiscreditList.forEach((item, index) => {
        this.r8Column3.push({
          prop: 'year_' + index,
          label: item.startDate + '~' + item.endDate
        })
      })
      let arrD3 = [
        {
          category: this.$t('vendorMod.relatedCompanyDebt') // 关联企业债务
        }
      ]
      arrD3.forEach((item, index) => {
        this.r8Column3.forEach((innerItem, innerIndex) => {
          let obj = relationDiscreditList[innerIndex] || {}
          item[innerItem.prop] = obj.amountOfMoney + '/' + obj.numberOfTimes
        })
      })
      this.r8DiscreditDto3 = arrD3
    },
    handleChange (val) {
    }
  }
}
</script>

<style scoped lang="scss">
.pt-10 {
  padding-top: 10px;
}
.mt-10 {
  margin-top: 10px;
}
.my-table {
  border:1px solid #DCDDDE;
  border-collapse: collapse;
  margin-top: 10px;
  width:100%;
  text-align: center;
  th {
    height: 0;
  }
  td {
    border-bottom: 1px solid #DCDDDE;
    border-right: 1px solid #DCDDDE;
    width:12.5%;
    height: 22px;
    line-height: 22px;
    padding:6px 0;
    &.grey {
      background-color: #F1F2F2;
      color:#393E45;
      font-weight: bold;
    }
  }
}
.base {
  min-width: 100px;
  display: flex;
}
.base_more {
  width: 48px;
  height: 12px;
  color: #409eff;
  font-size: 12px;
}

.el-table thead.is-group th {
  background: #fff;
}

.el-table thead.is-group tr:first-of-type th:first-of-type:before {
  content: "日期";
  text-align: center;
  position: absolute;
  width: 152px;
  height: 1px;
  bottom: 30px;
  right: 0;
}

.el-table thead.is-group tr:first-of-type th:first-of-type:after {
  content: "配送新增";
  text-align: center;
  position: absolute;
  width: 152px;
  top: 10px;
  left: 0;
}

.el-table thead.is-group tr:first-of-type th:first-of-type .cell {
  position: absolute;
  top: 0;
  left: 0;
  width: 152px;
  height: 1px;
  background-color: #ebeef5;
  display: block;
  text-align: center;
  transform: rotate(38deg);
  transform-origin: top left;
  -ms-transform: rotate(38deg);
  -ms-transform-origin: top left;
  -webkit-transform: rotate(38deg);
  -webkit-transform-origin: top left;
}

.risk_title {
  margin: 20px 0 10px;
  width: 200px;
  height: 14px;

  .blue_ {
    width: 4px;
    height: 15px;
    background-color: #409eff;
    float: left;
  }
  .risk_title1 {
    // width: 60px;
    height: 15px;
    color: #3d3d3d;
    font-size: 14px;
    float: left;
    text-align: center;
    line-height: 15px;
    margin-left: 10px;
  }
  &.mt-0 {
    margin-top: 0px;
  }
}
.total_three {
  width: 140px;
  height: 30px;
  line-height: 30px;
  text-align: center;
  font-size: 14px;
  font-weight: 200;
  margin-left: 40px;
}
.total_th {
  width: 400px;
  min-height: 400px;
  float: left;
}
.bidFrequencies {
  width: 400px;
  min-height: 400px;
  float: left;
  margin-left: 300px;
}
</style>
