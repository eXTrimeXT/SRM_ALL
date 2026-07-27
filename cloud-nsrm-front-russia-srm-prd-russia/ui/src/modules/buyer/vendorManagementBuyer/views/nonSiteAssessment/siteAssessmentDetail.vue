<template>
  <el-container
    class="flex-container the-siteAssessmentDetail-detail"
    direction="vertical"
  >
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
      >
        <div class="vendorAccessSteps">
          <VendorAccessSteps
            :current-status="vendorAccessStatus"
            :access-type="accessType"
            :approve-status="orderStatus"
          />
        </div>
        <el-collapse
          v-model="activeDims"
          class="tab-form-style"
        >
          <!-- 现场评审单 -->
          <el-collapse-item
            :title="$t('route.siteAssessment')"
            name="1"
          >
            <el-form
              ref="siteForm"
              :model="allParams.siteForm"
              :rules="rules"
              class="form-fill-style"
              :disabled="curOpt === 'view' || orderStatus == 'PUBLISH' || orderStatus == 'SUBMITTED'"
            >
              <srm-row :gutter="32">
                <srm-col :span="6">
                  <!-- 供应商评审类型 -->
                  <el-form-item
                    ref="assessmentType"
                    :label="$t('vendorMod.siteType')"
                    prop="assessmentType"
                  >
                    <DictSelect
                      v-model="allParams.siteForm.assessmentType"
                      code="CEEA_ASSESSMENT_TYPE"
                      @change="assessmentTypeChange"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :span="6">
                  <!-- 供应商名称 -->
                  <el-form-item
                    ref="vendorName"
                    :label="$t('common.vendorName')"
                    prop="vendorName"
                  >
                    <QuickSearch
                      :show-input="allParams.siteForm.vendorName"
                      show-key="companyName"
                      :scope-data="allParams.siteForm"
                      :disabled="bol1 != 1 || disVendorName"
                      name="scc_sup_company_info_display"
                      @close-quicksearch="getCompanyObj"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :span="6">
                  <!-- 资质审查单号 -->
                  <el-form-item
                    ref="reviewFormNumber"
                    :label="$t('vendorMod.quaNum')"
                    prop="reviewFormNumber"
                  >
                    <QuickSearch
                      :disabled="isQuaFormIdDisabled || bol1 != 1"
                      :show-input="allParams.siteForm.reviewFormNumber"
                      :pre-query-data="queryReviewFormParame"
                      show-key="reviewFormNumber"
                      :scope-data="allParams.siteForm"
                      name="scc_sup_service_review_form"
                      @close-quicksearch="getReviewFormObj"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :span="6">
                  <!-- 供应商评审单号 -->
                  <el-form-item
                    :label="$t('vendorMod.siteOrderInfo')"
                    prop="siteFormNumber"
                  >
                    <el-input
                      v-model="allParams.siteForm.siteFormNumber"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :span="6">
                  <!-- 审批状态 -->
                  <el-form-item :label="$t('vendorMod.approveStatus')">
                    <DictSelect
                      v-model="allParams.siteForm.approveStatus"
                      code="SUPPLIER_APPROVE_STATUS_TYPE"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :span="6">
                  <!-- 创建人 -->
                  <el-form-item :label="$t('common.creator')">
                    <el-input
                      v-model="allParams.siteForm.createdUserName"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :span="6">
                  <!-- 创建时间 -->
                  <el-form-item :label="$t('common.creationTime')">
                    <el-input
                      v-model="allParams.siteForm.creationDate"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :span="6">
                  <!-- 评审模板 -->
                  <el-form-item :label="$t('vendorMod.reviewTemplate')">
                    <el-select
                      v-model="allParams.siteForm.reviewModelId"
                      filterable
                      :placeholder="$t('common.pleaseSelect')"
                      :disabled="bol1 != 1"
                      clearable
                      @change="modelAllChange"
                    >
                      <el-option
                        v-for="item in modelList"
                        :key="item.reviewModelId"
                        :label="item.reviewModelName"
                        :value="item.reviewModelId"
                      />
                    </el-select>
                  </el-form-item>
                </srm-col>
                <srm-col :span="24">
                  <!-- 供应商评审说明 -->
                  <el-form-item :label="$t('vendorMod.siteExplain')">
                    <el-input
                      v-model="allParams.siteForm.siteFormExplain"
                      type="textarea"
                      :disabled="bol1 != 1"
                      :rows="2"
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-form>
          </el-collapse-item>
          <!-- 认证基本信息 -->
          <el-collapse-item
            :title="$t('vendorMod.authBaseInfo')"
            name="2"
          >
            <el-button
              class="detail-pbtn"
              style="margin-bottom:10px"
              type="primary"
              @click="addCountryList"
            >
              {{ $t('common.add') }}
            </el-button>
            <el-table
              :data="siteFormAddressList"
              border
            >
              <el-table-column
                prop="authFlag"
                :label="$t('vendorMod.authFlag')"
                width="150"
                align="center"
              >
                <template slot-scope="scope">
                  <el-checkbox
                    v-model="scope.row.authFlag"
                    :disabled="addressReadOnly || bol1 == 4 || orderStatus == 'PUBLISH' || orderStatus == 'SUBMITTED'"
                  />
                </template>
              </el-table-column>
              <!-- 国家/地区 -->
              <el-table-column
                prop="country"
                :label="$t('components.address.country')"
              >
                <template slot-scope="scope">
                  <DictSelect
                    v-model="scope.row.country"
                    code="country"
                    :placeholder="$t('common.pleaseSelect')"
                    :disabled="addressReadOnly || bol1 === 4 || orderStatus === 'PUBLISH' || orderStatus === 'SUBMITTED'"
                    @change="countryChange(scope.row)"
                  />
                </template>
              </el-table-column>
              <!-- 地区 -->
              <el-table-column
                prop="province"
                :label="$t('components.address.area')"
              >
                <template slot-scope="scope">
                  <DictSelect
                    v-model="scope.row.province"
                    code="PROVINCE"
                    custom-select-type="PROVINCE"
                    :placeholder="$t('common.pleaseSelect')"
                    :disabled="addressReadOnly || bol1 === 4 || orderStatus === 'PUBLISH' || orderStatus === 'SUBMITTED'||scope.row.country!='CN'"
                  />
                </template>
              </el-table-column>
              <!-- 城市 -->
              <el-table-column
                prop="city"
                :label="$t('components.address.city')"
              >
                <template slot-scope="scope">
                  <DictSelect
                    v-model="scope.row.city"
                    :code="scope.row.province"
                    custom-select-type="CITY"
                    :placeholder="$t('common.pleaseSelect')"
                    :disabled="addressReadOnly || bol1 === 4 || orderStatus === 'PUBLISH' || orderStatus === 'SUBMITTED'||scope.row.country!='CN'"
                  />
                </template>
              </el-table-column>
              <!-- 详细地址 -->
              <el-table-column
                prop="addressDetail"
                :label="$t('components.address.detailAddress')"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.addressDetail"
                    :disabled="addressReadOnly || bol1 == 4 || orderStatus == 'PUBLISH' || orderStatus == 'SUBMITTED'"
                  />
                </template>
              </el-table-column>
              <!-- 邮政编码 -->
              <el-table-column
                prop="postCode"
                :label="$t('components.address.postalCode')"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.postCode"
                    :disabled="addressReadOnly || bol1 == 4 || orderStatus == 'PUBLISH' || orderStatus == 'SUBMITTED'"
                  />
                </template>
              </el-table-column>
              <!-- 地址备注 -->
              <el-table-column
                prop="siteComment"
                :label="$t('components.address.remark')"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.siteComment"
                    :disabled="addressReadOnly || bol1 == 4 || orderStatus == 'PUBLISH' || orderStatus == 'SUBMITTED'"
                  />
                </template>
              </el-table-column>
              <!-- 是否启用 -->
              <el-table-column
                prop="enableFlag"
                :label="$t('vendorMod.enableFlag')"
                width="90"
                align="center"
              >
                <template slot-scope="scope">
                  <el-checkbox
                    v-model="scope.row.enableFlag"
                    :disabled="addressReadOnly || bol1 == 4 || orderStatus == 'PUBLISH' || orderStatus == 'SUBMITTED'"
                  />
                </template>
              </el-table-column>
              <!-- 操作 -->
              <el-table-column
                :label="$t('vendorMod.relegation.operation')"
                width="90"
                align="center"
              >
                <template slot-scope="scope">
                  <el-link
                    type="primary"
                    :underline="false"
                    @click="countryDelete(scope)"
                  >
                    {{ $t('vendorMod.relegation.delete') }}
                  </el-link>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
          <!-- 认证组织和品类 -->
          <el-collapse-item
            :title="$t('vendorMod.authOrganization')"
            name="3"
          >
            <!-- <el-form :model="form" label-position="left" label-width="100"> -->
            <el-table
              ref="orgDataTable"
              :data="orgData"
              style="width: 100%"
              border
              max-height="250px"
              @selection-change="orgSelectionChange"
            >
              <el-table-column
                align="center"
                type="selection"
                width="50"
                :selectable="checkboxT"
              />
              <!-- 合作组织 -->
              <el-table-column
                align="center"
                prop="orgName"
                :label="$t('vendorMod.cooOrg')"
                min-width="200"
              >
                <template slot-scope="scope">
                  <span>{{ scope.row.orgName }}</span>
                </template>
              </el-table-column>
              <!-- 事业部 -->
              <el-table-column
                align="center"
                prop="buName"
                :label="$t('vendorMod.buName')"
                min-width="200"
              />
            </el-table>
            <!-- </el-form> -->

            <el-table
              ref="catDataTable"
              :data="catData"
              style="width: 100%"
              border
              max-height="250px"
              @selection-change="catSelectionChange"
            >
              <el-table-column
                align="center"
                type="selection"
                width="50"
                :selectable="checkboxT"
              />
              <!-- 采购品类 -->
              <el-table-column
                align="center"
                prop="categoryName"
                :label="$t('vendorMod.category')"
                min-width="200"
              >
                <template slot-scope="scope">
                  <span>{{ scope.row.categoryName }}</span>
                </template>
              </el-table-column>
              <!-- 品类状态 -->
              <!--<el-table-column
            align="center"
            prop="serviceStatus"
            :label="$t('vendorMod.catServiceStatus')"
            min-width="150"
            :formatter="filterCatHandler"
          />-->
            </el-table>
          </el-collapse-item>

          <!-- 工作小组人员 -->
          <el-collapse-item
            :title="$t('vendorMod.workingGroupStaff')"
            name="1"
          >
            <el-button
              v-if="!addressReadOnly"
              type="primary"
              class="detail-pbtn"
              style="margin:0 0 10px 0"
              @click="addDisplayItem"
            >
              {{ $t('common.new') }}
            </el-button>
            <el-table
              :data="siteFormPersonList"
              style="width: 100%"
              border
              height="250px"
              highlight-current-row
            >
              <el-table-column
                align="center"
                type="index"
                :label="$t('contractMod.tabindex')"
                width="60"
              />
              <!-- 成员账号 -->
              <el-table-column
                align="center"
                prop="userAccount"
                :label="$t('vendorMod.userAccount')"
                min-width="100"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <QuickSearch
                    :show-input="scope.row.userAccount"
                    show-key="username"
                    :scope-data="scope.row"
                    name="scc_rbac_user_display"
                    :disabled="addressReadOnly || bol1 == 4 || orderStatus == 'PUBLISH' || orderStatus == 'SUBMITTED'"
                    @close-quicksearch="getCategoryObj"
                  />
                </template>
              </el-table-column>
              <!-- 成员名称 -->
              <el-table-column
                align="center"
                prop="userName"
                :label="$t('vendorMod.memberName')"
                min-width="100"
                :show-overflow-tooltip="true"
              />
              <!-- 手机号码 -->
              <el-table-column
                align="center"
                prop="userTel"
                :label="$t('vendorMod.mobilePhone')"
                min-width="100"
                :show-overflow-tooltip="true"
              />
              <!-- 电子邮箱 -->
              <el-table-column
                align="center"
                prop="userEmail"
                :label="$t('vendorMod.emailAddress')"
                min-width="100"
                :show-overflow-tooltip="true"
              />
              <!-- 岗位 -->
              <el-table-column
                align="center"
                prop="userPost"
                :label="$t('bidMod.position')"
                min-width="100"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.userPost"
                    :disabled="addressReadOnly || bol1 == 4 || orderStatus == 'PUBLISH' || orderStatus == 'SUBMITTED'"
                  />
                </template>
              </el-table-column>
              <!-- 评审模板 -->
              <el-table-column
                align="center"
                prop="reviewModelId"
                :label="$t('vendorMod.reviewTemplate')"
                min-width="100"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <el-select
                    v-model="scope.row.reviewModelId"
                    filterable
                    :placeholder="$t('common.pleaseSelect')"
                    :disabled="addressReadOnly || bol1 != 1 || orderStatus == 'PUBLISH' || orderStatus == 'SUBMITTED'"
                    clearable
                  >
                    <el-option
                      v-for="item in modelList"
                      :key="item.reviewModelId"
                      :label="item.reviewModelName"
                      :value="item.reviewModelId"
                    />
                  </el-select>
                </template>
              </el-table-column>
              <!-- 是否到现场 -->
              <el-table-column
                align="center"
                prop="onSiteFlag"
                :label="$t('vendorMod.onSiteFlag')"
                min-width="100"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <el-checkbox
                    v-model="scope.row.onSiteFlag"
                    true-label="true"
                    :disabled="addressReadOnly || bol1 == 4 || orderStatus == 'PUBLISH' || orderStatus == 'SUBMITTED'"
                  />
                </template>
              </el-table-column>
              <!-- 操作 -->
              <el-table-column
                v-if="bol1 == 1"
                :label="$t('common.operation')"
                width="60"
              >
                <template slot-scope="scope">
                  <!-- 删除 -->
                  <el-button
                    type="text"
                    @click="deleteOneContent(scope.$index, scope.row)"
                  >
                    {{
                      $t('common.delete')
                    }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>

          <el-collapse-item
            :title="$t('vendorMod.attachment')"
            name="6"
          >
            <vendorAccessAttachment
              ref="sceneAttachment"
              v-model="fileList"
              sence-code="QUA"
              :up-file-info="fileInfo"
            />
          </el-collapse-item>

          <!-- 认证结果 -->
          <el-collapse-item
            :title="$t('vendorMod.certificationResult')"
            name="4"
          >
            <!-- <el-button
              type="primary"
              class="detail-pbtn"
              style="margin:0 0 10px 0"
              @click="addFileRecords"
              v-if="!addressReadOnly"
              >{{ $t('common.new') }}</el-button
            > -->
            <el-table
              :data="fileRecords"
              style="width: 100%"
              border
              height="250px"
              highlight-current-row
            >
              <el-table-column
                align="center"
                type="index"
                :label="$t('contractMod.tabindex')"
                width="60"
              />
              <!-- 评审模板 -->
              <el-table-column
                align="center"
                prop="reviewModelId"
                :label="$t('vendorMod.reviewTemplate')"
                min-width="100"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <el-select
                    v-if="bol1 == 1"
                    v-model="scope.row.reviewModelId"
                    filterable
                    :placeholder="$t('common.pleaseSelect')"
                    :disabled="addressReadOnly"
                    clearable
                  >
                    <el-option
                      v-for="item in modelList"
                      :key="item.reviewModelId"
                      :label="item.reviewModelName"
                      :value="item.reviewModelId"
                    />
                  </el-select>

                  <!-- 评审与结论阶段显示该模板 -->
                  <el-button
                    v-if="bol1 == 2 || bol1 == 3 || bol1 == 4"
                    type="text"
                    @click="clickModelShow(scope.row.reviewModelId)"
                  >
                    {{ modelShow(scope.row.reviewModelId, modelList) }}
                  </el-button>
                </template>
              </el-table-column>
              <!-- 附件上传 -->
              <SrmCommonFile
                type="table-column"
                :extra-data="fileInfo"
                :table-column-options="{
                  label: $t('vendorMod.attachmentUpload'),
                  prop: 'fileId',
                  nameProp: 'fileName',
                  minWidth: '150px'
                }"
                :readonly="curOpt == 'view'"
                @on-change="filesUploadSuccess"
              />
              <!-- 评审人员 -->
              <el-table-column
                align="center"
                prop="reviewPeopleName"
                :label="$t('vendorMod.reviewPeopleName')"
                min-width="100"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.reviewPeopleName"
                    disabled
                  />
                  <!-- <el-select
                    filterable
                    v-model="scope.row.reviewPeopleName"
                    disabled
                    clearable
                  >
                    <el-option
                      v-for="item in siteFormPersonList"
                      :key="item.userId"
                      :label="item.userName"
                      :value="item.userId"
                    >
                    </el-option>
                  </el-select> -->
                </template>
              </el-table-column>
              <!-- 评审时间 -->
              <el-table-column
                align="center"
                prop="reviewDate"
                :label="$t('vendorMod.reviewTime')"
                min-width="100"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <el-date-picker
                    v-model="scope.row.reviewDate"
                    type="date"
                    :placeholder="$t('common.pleaseSelectDate')"
                    format="yyyy-MM-dd"
                    value-format="yyyy-MM-dd"
                    disabled
                  />
                </template>
              </el-table-column>
              <!-- 得分 -->
              <el-table-column
                align="center"
                prop="score"
                :label="$t('vendorMod.score')"
                min-width="100"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.score"
                    :disabled="bol1 == 3 || bol1 == 4"
                  />
                </template>
              </el-table-column>
              <!-- 结果 -->
              <el-table-column
                align="center"
                prop="authResult"
                :label="$t('vendorMod.authResult')"
                min-width="100"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.authResult"
                    :disabled="bol1 == 3 || bol1 == 4"
                  />
                </template>
              </el-table-column>
              <!-- 备注 -->
              <el-table-column
                align="center"
                prop="remark"
                :label="$t('vendorMod.remark')"
                min-width="100"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.remark"
                    :disabled="bol1 == 3 || bol1 == 4"
                  />
                </template>
              </el-table-column>
            </el-table>

            <!-- <vendorAccessAttachment
              ref="sceneAttachment"
              senceCode="AUTH"
              :data="allParams.fileRecords"
              :businessId="curOrderId"
              :attOpt="curOpt"
              :upFileInfo="fileInfo"
            /> -->

            <el-form
              ref="form"
              :model="allParams.siteForm"
              :rules="rules"
              class="form-fill-style"
              :disabled="curOpt === 'view'"
            >
              <srm-row
                :gutter="32"
                style="padding-top: 15px"
              >
                <srm-col
                  :span="8"
                  style="padding-right: 11px"
                >
                  <!-- 最终结论 -->
                  <el-form-item
                    :label="$t('vendorMod.finalResult')"
                    prop="reviewResult"
                  >
                    <DictSelect
                      v-model="allParams.siteForm.reviewResult"
                      :disabled="(bol1 == 2 && accountNameId != allParams.siteForm.createdId) || bol1 == 4"
                      code="CEEA_RESULT_TYPE"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :span="16">
                  <!-- 结论说明 -->
                  <el-form-item :label="$t('vendorMod.resultExplain')" prop="ceeaResultExplain">
                    <el-input
                      v-model="allParams.siteForm.ceeaResultExplain"
                      type="textarea"
                      :rows="2"
                      :disabled="(bol1 == 2 && accountNameId != allParams.siteForm.createdId) || bol1 == 4"
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-form>
          </el-collapse-item>
        </el-collapse>
        <CToolbar v-if="curOpt !== 'view'">
          <template slot="right">
            <el-button
              v-if="orderStatus === 'DRAFT' || orderStatus === ''"
              @click="save('TEMPORARY_STORAGE')"
            >
              {{ $t('common.staging') }}
            </el-button>
            <!-- <el-button
              v-if="
                orderStatus === 'DRAFT' ||
                orderStatus === 'WITHDRAW' ||
                orderStatus === 'REJECTED'
              "
              type="primary"
              @click="save('SUBMISSION')"
              >{{ $t("common.submit") }}tj</el-button
            > -->

            <!-- 新增提交跳转到流程 -->
            <!-- orderStatus === 'REJECTED'  状态判定 -->
            <!--              <workflow-button-->
            <!--                v-if="bol1 == 3"-->
            <!--                :temporary-storage="() => save('')"-->
            <!--                business-type="SUPPLIER"-->
            <!--                :business-id="curOrderId"-->
            <!--                @tabFlowHandler="tabFlowHandler"-->
            <!--              />-->
            <!--              <workflow-button-->
            <!--                v-if="bol1 == 2"-->
            <!--                :temporary-storage="() => save('')"-->
            <!--                business-type="SUPPLIER"-->
            <!--                :business-id="curOrderId"-->
            <!--                @tabFlowHandler="save('')"-->
            <!--              />-->
            <!--              <workflow-button-->
            <!--                v-if="bol1 == 1"-->
            <!--                :temporary-storage="() => save('TEMPORARY_STORAGE')"-->
            <!--                label="发布"-->
            <!--                business-type="SUPPLIER"-->
            <!--                :business-id="curOrderId"-->
            <!--                @tabFlowHandler="save('')"-->
            <!--              />-->
            <!-- TEMPORARY_STORAGE  -->
          </template>
        </CToolbar>
        <!-- 起草人意见 -->
        <srm-dialog
          :title="$t('vendorMod.loggerComment')"
          :visible.sync="loggerComment"
          size="middle"
          style="text-align: center"
        >
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
            <el-button
              type="primary"
              @click="commentForm"
            >
              {{ $t('common.confirm') }}
            </el-button>
          </div>
        </srm-dialog>

        <!-- 填写模板 -->
        <srm-dialog
          :visible.sync="previewBol"
          size="middle"
          :title="modelData.reviewModelName"
          :append-to-body="true"
        >
          <Printer
            :editable-tabs="editableTabs"
            :read-only="false"
            :table-data="tableData"
          />
          <div slot="footer">
            <el-button @click="previewBol = false">
              {{ $t('common.backTo') }}
            </el-button>
            <el-button
              type="primary"
              @click="clickModelSave"
            >
              {{ $t('common.affirm') }}
            </el-button>
          </div>
        </srm-dialog>
      </CWorkflowMulti>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch'
import VendorAccessSteps from 'modb@/vendorManagementBuyer/components/VendorAccessSteps'
import vendorAccessAttachment from 'modb@/vendorManagementBuyer/components/vendorAccessAttachment'
import { getDictItem, getDictItemList, getRegion } from '@/api/common'
import { adaptDictData } from '@/utils'
import WorkflowCommon from '@/library/mixins/workflow-common'
import Printer from 'modb@/vendorManagementBuyer/views/siteReviewModel/printer'
import { accessCommonApi, quaApi, siteReviewModel } from 'modb@/vendorManagementBuyer/api/vendorManagement'

export default {
  name: 'SiteAssessmentDetail',
  components: {
    MainHeader,
    QuickSearch,
    CToolbar,
    VendorAccessSteps,
    vendorAccessAttachment,
    Printer
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      disVendorName: false,
      addBol: false,
      fileList: [],
      tableData: [{}],
      editableTabs: [], // 模板填写信息数据
      modelData: [], // 模板填写信息数据
      previewBol: false, // 是否开启模板填写
      accountId: null, // 当前登录账号
      accountNameId: null, // 当前登录账号NAME ID
      bol1: 1, // 状态如果为1的话就是制定评审，2为评审中的状态，3为确认
      addressReadOnly: false, // 地址是否只读
      quaActiveInfo: 'tab1', // 新增
      inputComment: '',
      loggerComment: false,
      fileInfo: {
        uploadType: 'DEF',
        sourceType: 'WEB_APP',
        fileModular: 'sup',
        fileFunction: 'siteAssessment',
        fileType: 'images'
      },
      fileRefresh: false,
      vendorAccessStatus: 'site',
      accessType: 'daySiteAssessment',
      simpleAccessType: 'NON',
      orderStatus: 'DRAFT', // 单据状态
      curOrderId: null, // 单据ID
      isQuaFormIdDisabled: false,
      approveStatusList: [], // 审批状态
      quaReviewTypeList: [], // 资质审查类型
      assessmentTypeList: [], // 现场评审类型
      reviewResultList: [], // 评审结果
      vendorList: [], // 供应商列表
      catStatus: [], // 品类状态
      orgStatus: [], // 组织服务状态
      ratingField: [], // 评分领域
      ceeaReviewLinkList: [], // 评分领域
      modelList: [], // 模板列表
      rules: {
        assessmentType: [{ required: true, message: this.$t('vendorMod.msgAssessmentType') }], // "请选择现场评审类型"
        vendorName: [{ required: true, message: this.$t('vendorMod.msgVendorName') }], // "请输入供应商名称"
        // reviewResult: [{ required: true, message: this.$t('vendorMod.msgReviewResult') }], //"请选择最终结论"
        // ceeaResultExplain: [{ required: true, message: this.$t('vendorMod.msgCeeaResultExplain') }], //"请输入结论说明"
        reviewFormNumber: [{ required: true, message: this.$t('vendorMod.msgReviewFormNumber') }], // 请选择资质审查单
        siteDate: [{ required: true, message: this.$t('vendorMod.msgSiteDate') }], // "请选择评审日期"
        siteMember: [{ required: true, message: this.$t('vendorMod.msgSiteMember') }], // "请输入评审成员"
        vendorAssessor: [{ required: true, message: this.$t('vendorMod.msgVendorAssessor') }] // "请输入供方评审成员"
      },
      reviewFormList: [],

      allParams: {
        siteForm: {
          reviewResult: '', // 评审结果
          assessmentType: '', // 现场评审类型
          quaReviewType: '', // 资质审查类型
          vendorId: '',
          vendorCode: '', // 供应商code
          vendorName: '', // 供应商名称
          siteFormNumber: '', // 现场评审单号
          reviewFormId: '', // 资质审查id
          reviewFormNumber: '', // 资质审查单号
          ifDevelop: '',
          ifSiteForm: '',
          approveStatus: '',
          reviewCycle: '',
          siteFormExplain: '',
          siteProvince: '',
          siteCity: '',
          siteAdress: '',
          siteDate: '',
          siteMember: '',
          vendorAssessor: '',
          lastSiteMember: '',
          lastSiteDate: '',
          createdUserName: '',
          createdId: '',
          creationDate: '',
          siteReviewPlanId: ''
        },
        orgJournals: [],
        cateJournals: [],
        siteAttaches: [],
        fileRecords: [],
        siteFormAddressList: [],
        opType: ''
      },
      siteFormPersonList: [{}], // 人员列表
      siteFormAddressList: [{
        province: null,
        city: null
      }], // 基本信息表格填写
      fileRecords: [], // 评审模板列表
      orgData: [], // 组织
      catData: [], // 品类
      orgJournals: [], // 组织选择
      cateJournals: [], // 品类选择
      selectedOrg: [], // 选中的组织数据
      selectedCat: [], // 选中的品类数据
      isDisabled: this.$attrs.params.flag === 'edit',
      curOpt: 'add',
      orgDialog: false,
      activeDims: ['1', '2', '3', '4', '5', '6'],
      queryReviewFormParame: {
        't.vendor_id': null,
        't.approve_status': 'APPROVED',
        't.CEEA_IF_VENDOR_AUTH': 'Y'
      }, // 查询资质审查单据入参
      isOutside: '',
      openWorkFlow: false, // 暂时开启
      flowParams: {}, // 流程参数
      integrationModeFlow: '', // 工作流集成模式
      Eparames: []
    }
  },
  computed: {
    viewUpdateButton () { // 用来控制保存、提交按钮是否可见。如果自定义按钮则无需添加
      if (this.workflowParamsInfo.integrationMode !== 'None') {
        return this.curOpt != 'view'
      } else {
        if (this.curOpt != 'view') {
          return true
        } else if (this.curOpt == 'view' && this.allParams.siteForm.approveStatus == 'PUBLISH') {
          console.log(this.allParams.siteForm.approveStatus)
          return true
        } else {
          return false
        }
      }
    },
    workflowBusinessId () { // 用来指定工作流的业务ID
      return this.$attrs.params.siteFormId ? this.$attrs.params.siteFormId : this.curOrderId
    },
    // 展示工作流tab页
    workflowTabDisabled () {
      let bol = true
      if (this.allParams.siteForm.reviewResult && this.orderStatus != 'DRAFT') {
        bol = false
      }
      return bol
    }
  },
  watch: {
    viewUpdateButton () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
    }
  },
  created () {
    this.buttonConfigInfo.save.view = this.viewUpdateButton
    this.buttonConfigInfo.submit.view = this.viewUpdateButton
    this.buttonConfigInfo.save.name = this.$t('common.staging')
    this.buttonConfigInfo.submit.name = this.$t('common.submit')
    this.buttonConfigInfo.cancel.view = false
    this.buttonConfigInfo.close.view = this.isReadOnly
    this.Eparames = this.$attrs.params
    this.fatchDictData() // 字典
    this.curOpt = this.$attrs.params.flag
    if (this.$attrs.params.flag == 'adds') {
      // this.allParams.siteForm.vendorId = this.$attrs.params.row.vendorId
      console.log(this.$attrs.params.row)
      let { vendorId, vendorCode, vendorName, planName } = this.$attrs.params.row
      this.$set(this.allParams.siteForm, 'vendorId', vendorId)
      this.$set(this.allParams.siteForm, 'vendorCode', vendorCode)
      this.$set(this.allParams.siteForm, 'vendorName', vendorName)
      this.$set(this.allParams.siteForm, 'planName', planName)
      this.$set(this.queryReviewFormParame, 't.vendor_id', vendorId)
      this.addBol = true
      const obj = {
        siteReviewPlanId: this.$attrs.params.row.siteReviewPlanId
      }
      this.getCategoryObj2(obj)
    }
    if (this.$attrs.params.flag === 'edit' || this.$attrs.params.flag === 'view') {
      let siteFormId = this.$attrs.params.siteFormId
      this.curOrderId = this.$attrs.params.siteFormId
      this.getSiteFormDetail(siteFormId) // 查询单据数据
    }
  },
  mounted () {
    this.modelListAdd()
    this.accountId = this.$store.getters.userId
    this.accountNameId = this.$store.getters.userId
  },
  methods: {
    back () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('SiteAssessmentList.getQuerydata')
    },
    countryChange (row) {
      if (row.country != 'CN') {
        row.city = ''
        row.province = ''
      }
    },
    checkboxT (row, rowIndex) {
      if (this.addressReadOnly || this.bol1 == 4 || this.orderStatus == 'PUBLISH' || this.orderStatus == 'SUBMITTED') {
        return false
      } else {
        return true
      }
    },
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'SERVICESUPPLIER'
    },
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    // 删除评审工作人员
    deleteOneContent (index) {
      this.siteFormPersonList.splice(index, 1)
    },
    // 修改头部的评审模板
    modelAllChange (val) {
      // this.$set(this.siteFormPersonList[0],reviewModelId,val)
      this.siteFormPersonList.forEach(res => {
        res.reviewModelId = val
      })
      this.siteFormPersonList.push({})
      this.siteFormPersonList.pop()
      console.log(this.siteFormPersonList)
      //
    },
    getCategoryObj2 (val, scope) {
      this.$set(this.allParams.siteForm, 'siteReviewPlanId', val.siteReviewPlanId)
      console.log(val)
      if (!val) {
        return false
      }
      let id = val.siteReviewPlanId
      siteReviewModel.address(id).then(res => {
        const addressList = res.data.addressList
        const personList = res.data.personList
        console.log(res.data)

        this.siteFormPersonList = personList
// reviewModelId
        this.siteFormPersonList.forEach(datas => {
          datas.reviewModelId = this.allParams.siteForm.reviewModelId
        })
        console.log(this.siteFormPersonList)

        addressList.forEach(datas => {
          const obj = {
            city: datas.city,
            addressDetail: datas.addressDetail,
            country: datas.country,
            province: datas.province,
            postCode: datas.postCode
          }
          if (!this.siteFormAddressList[0].country) {
            this.siteFormAddressList.splice(0, 1)
            this.siteFormAddressList.push(obj)
          } else {
            if (datas.addressDetail != this.siteFormAddressList[0].addressDetail) {
              this.siteFormAddressList.push(obj)
            }
          }
        })
      })
    },
    clickModelSave () {
      let reviewFormStandardDimList = []
      let _this = this
      let tableData = JSON.stringify(this.tableData)
      this.editableTabs.forEach(datas1 => {
        let d1 = {
          dimName: datas1.title,
          dimType: datas1.type,
          dimId: datas1.dimId,
          siteReviewModelId: datas1.siteReviewModelId
        }
        let attr = []
        datas1.content.forEach(datas2 => {
          let d2
          if (datas1.type == 'TABLE') {
            d2 = {
              fieldName: datas2.fieldName,
              fieldCode: datas2.fieldCode,
              fieldValue: tableData,
              dictCode: datas2.dictCode,
              fieldType: datas2.fieldType,
              assemblyType: datas2.assemblyType,
              necessaryFlag: datas2.necessaryFlag,
              dimFieldId: datas2.dimFieldId,
              siteReviewModelId: datas2.siteReviewModelId,
              dimId: datas2.dimId
            }
          } else {
            d2 = {
              fieldName: datas2.fieldName,
              fieldValue: datas2.fieldContent,
              fieldCode: datas2.fieldCode,
              dictCode: datas2.dictCode,
              fieldType: datas2.fieldType,
              assemblyType: datas2.assemblyType,
              necessaryFlag: datas2.necessaryFlag,
              dimFieldId: datas2.dimFieldId,
              siteReviewModelId: datas2.siteReviewModelId,
              dimId: datas2.dimId
            }
          }

          attr.push(d2)
        })

        d1.fieldList = attr
        reviewFormStandardDimList.push(d1)
      })
      // datas.dimList = reviewFormStandardDimList

      this.fileRecords.forEach((data00, index) => {
        if (_this.modelData.reviewModelId == data00.reviewModelId) {
          console.log(_this.modelData.reviewModelId)
          _this.fileRecords[index].dimList = reviewFormStandardDimList
        }
      })
      console.log(this.fileRecords)
      this.previewBol = false
    },
    // 点击某个模板显示弹窗
    clickModelShow (id) {
      let bol = 0 // 等于0是请求详情接口
      console.log(this.fileRecords)
      let num = 0
      // 检测是否以前数据，如果是已有数据直接引用，没有数据请求接口
      this.fileRecords.forEach((datas, index) => {
        if (id == datas.reviewModelId) {
          num = index
          if (datas.dimList.length > 0) {
            bol = 1
          } else {
            bol = 0
          }
        }
      })
      if (bol == 0) {
        siteReviewModel.getDetail(id).then(res => {
          this.modelData = res.data
          this.getList(res.data.dimList)
          this.previewBol = true
        })
      } else {
        this.modelData.reviewModelId = id
        this.getList(this.fileRecords[num].dimList)
        this.previewBol = true
      }
    },
    getList (res) {
      let editableTabs = []
      let _this = this
      res.forEach((datas, indexs) => {
        // console.log(datas)
        const index = String(indexs + 1)
        let d1 = {
          dimId: datas.dimId,
          siteReviewModelId: datas.siteReviewModelId,
          title: datas.dimName,
          name: index,
          type: datas.dimType,
          content: []
        }
        this.tabIndex = index
        let attr = []
        if (datas.dimType == 'TABLE') {
          datas.fieldList.forEach(datas2 => {
            let d2 = {
              fieldCode: datas2.fieldCode,
              fieldName: datas2.fieldName,
              dictCode: datas2.dictCode,
              fieldType: datas2.fieldType,
              assemblyType: datas2.assemblyType,
              necessaryFlag: datas2.necessaryFlag,
              dimFieldId: datas2.dimFieldId,
              siteReviewModelId: datas2.siteReviewModelId,
              dimId: datas2.dimId
            }
            attr.push(d2)
          })
          try {
            _this.tableData = JSON.parse(datas.fieldList[0].fieldValue)
            console.log(_this.tableData)
          } catch (error) {
            console.log(error)
          }
        } else {
          datas.fieldList.forEach(datas2 => {
            let d2 = {
              fieldCode: datas2.fieldCode,
              fieldName: datas2.fieldName,
              dictCode: datas2.dictCode,
              fieldContent: datas2.fieldValue,
              fieldType: datas2.fieldType,
              assemblyType: datas2.assemblyType,
              necessaryFlag: datas2.necessaryFlag
            }
            attr.push(d2)
          })
        }

        d1.content = attr
        editableTabs.push(d1)
      })
      this.editableTabs = editableTabs
    },
    // 过滤模板名称出来
    modelShow (id, list) {
      let obj = ''
      list.forEach(datas => {
        if (datas.reviewModelId == id) {
          obj = datas.reviewModelName
        }
      })
      return obj
    },
    // 初始化的时候判定状态
    stateDetermination () {
      // console.log(this.curOpt)
      let cMen = 0// 是否创建人
      let pMen = 0// 是否评审人
      if (this.accountId == this.allParams.siteForm.createdId) {
        cMen = 1
      }
      let fileRecords = []
      this.fileRecords.forEach(datas => {
        if (cMen == 1) {
          if (datas.score && datas.authResult) {
            fileRecords.push(datas)
          } else {
            if (this.accountId == datas.reviewPeople) {
              fileRecords.push(datas)
            }
          }
        } else if (datas.reviewPeople == this.accountId) {
          fileRecords.push(datas)
        }
      })
      this.fileRecords = fileRecords

      if (this.curOpt == 'add') {
        this.bol1 = 1
      } else if (this.curOpt == 'edit') {
        console.log(this.accountId)
        this.siteFormPersonList.forEach(datas => {
          console.log(datas.userId)
          if (datas.userId == this.accountId) {
            this.bol1 = 2
            this.addressReadOnly = true
          }
        })

        let scoreBol = 0 // 检查是否有写得分
        let authResultBol = 0 // 检查是否有写结果
        this.fileRecords.forEach(datas => {
          if (datas.score) {
            scoreBol = 1
          }
          if (datas.authResult) {
            authResultBol = 1
          }
        })
        console.log(this.allParams.siteForm)

        if (
          this.accountNameId == this.allParams.siteForm.createdId &&
          scoreBol == 1 &&
          authResultBol == 1
        ) {
          this.bol1 = 3
        }
      } else {
        this.bol1 = 4
      }
    },
    // 加载模板列表
    modelListAdd () {
      siteReviewModel.listAll().then(res => {
        let attr = []
        res.data.forEach(datas => {
          if (datas.approveStatus == 'ENABLE') {
            attr.push(datas)
          }
        })
        this.modelList = attr
      })
    },
    addFileRecords () {
      this.fileRecords.push({})
    },
    // 上传附件成功
    filesUploadSuccess ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.fileRecords[$index].fileId = fileId
      this.fileRecords[$index].fileName = fileName
    },
    getCategoryObj (val, scope) {
      scope.userAccount = val ? val.username : ''
      scope.userName = val ? val.nickname : ''
      scope.userId = val ? val.userId : ''
      scope.userTel = val ? val.phone : ''
      scope.userEmail = val ? val.email : ''
      this.siteFormPersonList.push({})
      this.siteFormPersonList.pop()
    },
    addDisplayItem () {
      this.siteFormPersonList.push({
        reviewModelId: this.allParams.siteForm.reviewModelId
      })
    },
    countryDelete (row) {
      this.siteFormAddressList.splice(row.$index, 1)
    },
    // 点击增加一条地址
    addCountryList () {
      this.siteFormAddressList.push({})
    },
    filterOrgHandler (row, column, cellValue, index) {
      let orgs = this.dictDataFarmat(this.orgStatus, cellValue)
      return orgs
    },
    filterCatHandler (row, column, cellValue, index) {
      let cats = this.dictDataFarmat(this.catStatus, cellValue)
      return cats
    },
    dictDataFarmat (arr, dictVal) {
      let dictArr = arr || []
      let pattern = new RegExp('[\u4E00-\u9FA5]+')
      if (dictArr.length > 0 && dictVal) {
        if (dictVal && pattern.test(dictVal)) {
          // 如果是中文直接返回值
          return dictVal
        } else {
          const dicRow = dictArr.find(item => item.value === dictVal)
          if (dicRow) {
            return dicRow.label
          } else {
            return dictVal
          }
        }
      } else {
        return dictVal
      }
    },
    // 获取数据字典
    fatchDictData () {
      // 批量查询字典
      let dictParamsArr = [
        { dictCode: 'APPROVE_STATUS_TYPE' }, // 审批状态
        { dictCode: 'QUA_REVIEW_TYPE' }, // 供方准入类型
        { dictCode: 'CEEA_ASSESSMENT_TYPE' }, // 现场评审类型
        { dictCode: 'CEEA_RESULT_TYPE' }, // 评审结果
        { dictCode: 'CATEGORY_STATUS' }, // 品类状态
        { dictCode: 'ORG_STATUS' }, // 组织服务状态
        { dictCode: 'CEEA_AUTH_RESULT' }, // 认证结果
        { dictCode: 'CEEA_REVIEW_LINK' } // 评审环节
      ]
      getDictItemList(dictParamsArr).then(res => {
        this.approveStatusList = adaptDictData(res.data[0].APPROVE_STATUS_TYPE, 'dict')
        this.quaReviewTypeList = adaptDictData(res.data[1].QUA_REVIEW_TYPE, 'dict')
        this.assessmentTypeList = adaptDictData(res.data[2].CEEA_ASSESSMENT_TYPE, 'dict')
        this.reviewResultList = adaptDictData(res.data[3].CEEA_RESULT_TYPE, 'dict')
        this.catStatus = adaptDictData(res.data[4].CATEGORY_STATUS, 'dict')
        this.orgStatus = adaptDictData(res.data[5].ORG_STATUS, 'dict')
        this.ratingField = adaptDictData(res.data[6].CEEA_AUTH_RESULT, 'dict')
        this.ceeaReviewLinkList = adaptDictData(res.data[7].CEEA_REVIEW_LINK, 'dict')
      })
    },
    // 加载供应商
    getvendorList () {
      this.$http({
        url: '/api-sup/info/companyInfo/listPageByDTO',
        method: 'POST',
        data: {},
        loading: true
      })
        .then(data => {
          this.vendorList = data.data.list
        })
        .catch(err => {
          console.log(err)
        })
    },
    // 选择供应商回调
    getCompanyObj (val, data) {
      if (val) {
        console.log(val)
        this.allParams.siteForm.vendorId = val.companyId
        this.allParams.siteForm.vendorCode = val.companyCode
        this.allParams.siteForm.vendorName = val.companyName
        this.allParams.siteForm.siteProvince = val.companyProvince
        this.allParams.siteForm.siteCity = val.companyCity
        this.allParams.siteForm.siteAdress = val.companyAddress
        this.getFininfo(this.allParams.siteForm.vendorId) // 获取上一个评审人
        // this.getorgCateJournal(this.allParams.siteForm.vendorId) // 根据供应商ID查询供应商档案组织与品类状态
        this.siteFormAddressList = []
        // 将地址带到下面
        let address = {
          country: val.companyCountry,
          addressDetail: val.companyAddress,
          city: val.companyCity,
          province: val.companyProvince
        }
        // if (!this.siteFormAddressList[0].country) {
        //   this.siteFormAddressList.splice(0, 1)
        //   this.siteFormAddressList.push(address)
        // } else {
        this.siteFormAddressList.push(address)
        // }

        console.log(this.siteFormAddressList)

        this.queryReviewFormParame = {
          // 资质审查单入参
          't.vendor_id': this.allParams.siteForm.vendorId,
          't.approve_status': 'APPROVED',
          't.CEEA_IF_VENDOR_AUTH': 'Y'
        }
        this.isOutside = val.overseasRelation
      }
    },
    // 选择资质审查单据回调 (快速查询回调)
    getReviewFormObj (val, data) {
      let quaReviewType = val ? val.quaReviewType : ''
      this.allParams.siteForm.quaReviewType = quaReviewType
      this.allParams.siteForm.reviewFormNumber = val ? val.reviewFormNumber : ''
      this.allParams.siteForm.reviewFormId = val ? val.reviewFormId : ''
      // 获取组织品类关系
      if (val) {
        this.allParams.siteForm.vendorCode = val.vendorCode
        this.allParams.siteForm.vendorId = val.vendorId
        this.disVendorName = true
        this.$set(this.allParams.siteForm, 'vendorName', val.vendorName)
        this.getorgCateJournalByFormId(val.reviewFormId) // 根据资质审查单Id查询组织和品类信息
        // this.fatchAccessFlow(quaReviewType); // 通过资质审查类型返回流程类型
        let query = { reviewFormId: val.reviewFormId, type: 'AUTH' }
        this.fatchQuaFileConfig(query)
      } else {
        this.allParams.fileRecords = []
        this.disVendorName = false
      }
    },
    fatchQuaFileConfig (query) {
      quaApi.getTemplateFilesByReviewFormId(query).then(res => {
        if (res.data && res.data.length > 0) {
          this.fileList = res.data.map(i => ({
            ...i,
            fileId: '',
            fileName: ''
          }))
        } else {
          this.fileList = []
        }
        console.log(this.fileList)
      })
    },
    // 资质审查单据选择 (select下拉)
    getReviewFormType (val) {
      let row = this.reviewFormList.find(item => {
        return item.reviewFormId === val
      })
      let quaReviewType = row.quaReviewType
      this.allParams.siteForm.quaReviewType = quaReviewType
      this.allParams.siteForm.reviewFormNumber = row.reviewFormNumber
      // 获取组织品类关系
      this.getorgCateJournalByFormId(val) // 根据资质审查单Id查询组织和品类信息
      this.fatchAccessFlow(quaReviewType) // 通过资质审查类型返回流程类型
    },
    // 获取上一次评审人
    getFininfo (vendorId) {
      // 根据供应商ID获取上次评审人员和时间
      accessCommonApi.getLastSiteFormMessage({ vendorId }).then(res => {
        if (res.data) {
          this.allParams.siteForm.lastSiteDate = res.data.lastSiteDate
          this.allParams.siteForm.lastSiteMember = res.data.lastSiteMember
        }
      })
      // 根据供应商ID查询供应商档案组织与品类状态
      this.orgData = []
      // this.getorgCateJournal(vendorId)
    },
    // 根据供应商ID查询供应商档案组织与品类状态
    getorgCateJournal (companyId) {
      this.$http({
        url: '/api-sup/info/companyInfo/listOrgAndCategoryByCompanyId',
        method: 'GET',
        params: { companyId },
        loading: true
      })
        .then(res => {
          this.orgData = res.data.orgInfos
          this.catData = res.data.orgCategorys
          if (this.selectedOrg && this.selectedOrg.length > 0 && res.data.orgInfos.length > 0) {
            let _this = this
            _this.$nextTick(() => {
              _this.selectedOrg.forEach(selected => {
                // 反选table操作
                res.data.orgInfos.forEach(item => {
                  if (selected.orgId == item.orgId) {
                    // item.id 类型为数字 selected为字符串用==
                    _this.$refs.orgDataTable.toggleRowSelection(item, true)
                  }
                })
              })
            })
          }
          if (this.selectedCat && this.selectedCat.length > 0 && res.data.orgInfos.length > 0) {
            let _this = this
            _this.$nextTick(() => {
              _this.selectedCat.forEach(selected => {
                // 反选table操作
                res.data.orgCategorys.forEach(item => {
                  if (selected.categoryId == item.categoryId) {
                    _this.$refs.catDataTable.toggleRowSelection(item, true)
                  }
                })
              })
            })
          }
        })
        .catch(err => {
          console.log(err)
        })

      /* listOrgCateServiceStatusByCompanyId({ companyId }).then(res => {
        let arr = [];
        if (res.data) {
          res.data.map(v => {
            arr.push({
              orgId: v.orgInfo ? v.orgInfo.orgId : null,
              orgCode: v.orgInfo ? v.orgInfo.orgCode : "",
              orgName: v.orgInfo ? v.orgInfo.orgName : "",
              orgServiceStatus: v.orgInfo ? v.orgInfo.serviceStatus : "",
              categoryId: v.orgCategory ? v.orgCategory.categoryId : null,
              categoryCode: v.orgCategory ? v.orgCategory.categoryCode : "",
              categoryName: v.orgCategory ? v.orgCategory.categoryName : "",
              catServiceStatus: v.orgCategory ? v.orgCategory.serviceStatus : ""
            });
          });
        }
        this.orgCatData = arr; // 重新赋值
        if (
          this.selectedOrg &&
          this.selectedOrg.length > 0 &&
          arr.length > 0
        ) {
          let _this = this;
          _this.$nextTick(() => {
            _this.selectedOrg.forEach(selected => {
              // 反选table操作
              arr.forEach(item => {
                if (
                  selected.orgId == item.orgId &&
                  selected.categoryId == item.categoryId
                ) {
                  // item.id 类型为数字 selected为字符串用==
                  _this.$refs.orgDataTable.toggleRowSelection(item, true);
                }
              });
            });
          });
        }
      }); */
    },
    // 根据资质审查单Id查询组织和品类信息
    getorgCateJournalByFormId (reviewFormId) {
      this.$http({
        url: '/api-sup/review/serviceReviewForm/listOrgAndCategoryByReviewId',
        method: 'GET',
        params: { reviewFormId },
        loading: true
      })
        .then(res => {
          if (res.data) {
            this.orgData = res.data.orgInfos
            this.catData = res.data.orgCategorys
            if (this.selectedOrg && this.selectedOrg.length > 0 && res.data.orgInfos.length > 0) {
              let _this = this
              _this.$nextTick(() => {
                _this.selectedOrg.forEach(selected => {
                  // 反选table操作
                  res.data.orgInfos.forEach(item => {
                    if (selected.orgId == item.orgId) {
                      // item.id 类型为数字 selected为字符串用==
                      _this.$refs.orgDataTable.toggleRowSelection(item, true)
                    }
                  })
                })
              })
            }
            if (this.selectedCat && this.selectedCat.length > 0 && res.data.orgInfos.length > 0) {
              let _this = this
              _this.$nextTick(() => {
                _this.selectedCat.forEach(selected => {
                  // 反选table操作
                  res.data.orgCategorys.forEach(item => {
                    if (selected.categoryId == item.categoryId) {
                      _this.$refs.catDataTable.toggleRowSelection(item, true)
                    }
                  })
                })
              })
            }
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    // 根据资质审查单Id查询组织和品类信息
    /* getorgCateJournalByFormId(reviewFormId) {
      getOrgCatByReviewId({ reviewFormId }).then(res => {
        let arr = [];
        if (res.data) {
          res.data.map(v => {
            arr.push({
              orgId: v.orgInfo ? v.orgInfo.orgId : null,
              orgCode: v.orgInfo ? v.orgInfo.orgCode : "",
              orgName: v.orgInfo ? v.orgInfo.orgName : "",
              orgServiceStatus: v.orgInfo ? v.orgInfo.serviceStatus : "",
              categoryId: v.orgCategory ? v.orgCategory.categoryId : null,
              categoryCode: v.orgCategory ? v.orgCategory.categoryCode : "",
              categoryName: v.orgCategory ? v.orgCategory.categoryName : "",
              catServiceStatus: v.orgCategory ? v.orgCategory.serviceStatus : ""
            });
          });
        }
        this.orgData = arr;
        if (
          this.selectedOrg &&
          this.selectedOrg.length > 0 &&
          arr.length > 0
        ) {
          let _this = this;
          _this.$nextTick(() => {
            _this.selectedOrg.forEach(selected => {
              // 反选table操作
              arr.forEach(item => {
                if (
                  selected.orgId == item.orgId &&
                  selected.categoryId == item.categoryId
                ) {
                  // item.id 类型为数字 selected为字符串用==
                  _this.$refs.orgDataTable.toggleRowSelection(item, true);
                }
              });
            });
          });
        }
      });
    }, */
    // 现场评审选择
    assessmentTypeChange (val) {
      if (val === 'ANNUAL_REVIEW') {
        // 日常评审
        this.isQuaFormIdDisabled = true
        this.allParams.siteForm.reviewFormId = '' // 资质审查id
        this.allParams.siteForm.quaReviewType = ''
        this.allParams.siteForm.reviewFormNumber = '' // 资质审查单号
        this.getorgCateJournal(this.allParams.siteForm.vendorId) // 根据供应商ID查询供应商档案组织与品类状态
      } else {
        // 准入评审 ACCESS_ASSESSMENT
        this.isQuaFormIdDisabled = false
      }
    },
    // 查询是否物料试用
    fatchAccessFlow (quaReviewType) {
      accessCommonApi.getEntryConfigByQuaReviewType({ quaReviewType }).then(res => {
        if (res) {
          let accessProcess = res.data.accessProcess // 准入流程
          this.accessType = accessProcess || 'daySiteAssessment'
        }
      })
    },
    // 查询单据信息
    getSiteFormDetail (siteFormId) {
      if (!siteFormId) return
      this.$http({
        url: '/api-sup/serviceSiteForm/getSiteFormDTO',
        method: 'GET',
        params: { siteFormId },
        loading: true
      })
        .then(res => {
          this.allParams = res.data
          console.log(this.allParams)
          if (res.data.siteForm) {
            this.orderStatus = res.data.siteForm.approveStatus
            console.log(this.orderStatus)
            // if (res.data.siteForm.assessmentType === 'ACCESS_ASSESSMENT') {
              // 准入类型的单据 查询资质审查单据列表
              this.isQuaFormIdDisabled = false
              this.queryReviewFormParame = {
                // 资质审查单入参
                't.vendor_id': this.allParams.siteForm.vendorId,
                't.approve_status': 'APPROVED',
                't.CEEA_IF_VENDOR_AUTH': 'Y'
              }
              let reviewFormId = res.data.siteForm.reviewFormId
              let quaReviewType = res.data.siteForm.quaReviewType
              this.getorgCateJournalByFormId(reviewFormId) // 根据资质审查单Id查询组织和品类信息
              // this.fatchAccessFlow(quaReviewType); // 通过资质审查类型返回流程类型
            // } else {
            //   // 日常评审通过供应商ID 查询组织品类
            //   this.isQuaFormIdDisabled = true
            //   this.getorgCateJournal(res.data.siteForm.vendorId) // 根据供应商ID查询组织品类
            // }
            if (res.data.orgJournals) {
              this.selectedOrg = res.data.orgJournals // 勾选保存的数据
            }
            if (res.data.cateJournals) {
              this.selectedCat = res.data.cateJournals // 勾选保存的数据
            }
            if (res.data.siteFormAddressList) {
              this.siteFormAddressList = res.data.siteFormAddressList // 勾选保存的数据
            }
            if (res.data.siteFormPersonList) {
              this.siteFormPersonList = res.data.siteFormPersonList // 勾选保存的数据
            }
            if (res.data.fileRecords) {
              this.fileRecords = res.data.fileRecords // 勾选保存的数据
            }
            if (res.data.fileList) {
              this.fileList = res.data.fileList // 勾选保存的数据
            }
            this.$forceUpdate()
            this.stateDetermination()
            if ((this.allParams.siteForm.approveStatus == 'PUBLISH' && this.allParams.siteForm.reviewResult) || this.allParams.siteForm.approveStatus == 'SUBMITTED' || this.allParams.siteForm.approveStatus == 'APPROVED') {
              // 切换到工作流tab页
              var workflowMode = this.workflowParamsInfo.integrationMode === 'Product' || this.workflowParamsInfo.integrationMode === 'Iframe' || this.workflowParamsInfo.integrationMode === 'Self'
              if (workflowMode) {
                this.activeTabName = 'workflowTab'
              }
            }
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    // 选择组织
    addOrgHandle (e, dd, scope) {
      scope.orgId = e ? e.organizationId : ''
      scope.parentOrgId = e ? e.parentOrganizationId : ''
      scope.orgName = e ? e.organizationName : ''
    },
    // 选择品类
    comfirmSelect (node, scope) {
      scope.categoryId = node ? node.categoryId : ''
      scope.categoryName = node ? node.categoryName : ''
    },

    // table selection
    orgSelectionChange (val) {
      this.orgJournals = val
    },
    catSelectionChange (val) {
      this.cateJournals = val
    },
    reset () {
      // 重置所有过滤条件
      for (let i in this.form) {
        this.form[i] = ''
      }
    },
    // 起草人意见确认提交
    commentForm () {
      // 用于没有工作流页面模式下提交
      if (this.bol1 == 2) {
        this.saveDataHandle('')
      } else if (this.bol1 == 3) {
        this.saveDataHandle('SUBMISSION')
      }

      this.loggerComment = false
    },
    tabFlowHandler () {
      console.log('[tabFlowHandler]')
      this.quaActiveInfo = 'tabFlow'
      // this.tabClick({ name: 'tabFlow' })
    },
    // 改造save方法
    async save (type) {
      this.$refs['siteForm'].validate((valid, object) => {
        if (valid) {
          if (type !== 'SUBMISSION') {
            this.$refs['form'].validate((valid2, object2) => {
              if (!valid2) {
                return false
              }
              let bol = false
              this.fileList.forEach(e => {
                if (e.ifRequired == 'Y' && !e.fileId) {
                  bol = true
                }
              })
              if (bol) {
                this.$message.error(this.$t('bidMod.pleaseUploadFile'))
                return false
              }
              return this.saveDataHandle(type)
            })
          }
        } else {
          this.__focus_error__()
          return false
        }
      })
    },
    validate () {
      return new Promise(rs => {
        this.$refs.form.validate(b => {
          if (!b) {
            this.$message({
              message: this.$t('vendorMod.pleasefinishRequired'), // '请输入单据必填信息'
              type: 'error'
            })
          }
          rs(b)
        })
      })
    },
    async validateForm () {
      console.log('[validateForm]')
      const flag = await this.validate()
      if (!flag) return false
      if (this.orgJournals.length === 0) {
        this.$message({
          message: this.$t('vendorMod.msgOrgCateJournals'), // "请选择评审组织和品类",
          type: 'error'
        })
        return false
      }
      // 判断附件是否上传
      // if (!this.$refs.sceneAttachment.validRequired()) {
      //   return false
      // }
      return true
    },

    // 保存数据操作-start
    saveDataHandle (type) {
      console.log('type-' + type)
      this.allParams.siteForm.ceeaDrafterOpinion = ''
      if (type === 'SUBMISSION') {
        this.allParams.siteForm.ceeaDrafterOpinion =
          this.inputComment || this.$t('vendorMod.pleaseApproval') // 请审批，谢谢！
      }
      let sceneType = ''
      const bol1Save = this.bol1
      if ((bol1Save == 1 && this.allParams.siteForm.reviewResult == '') && type == 'SAVE') {
        sceneType = 'CREATE'
      } else if (bol1Save == 2) {
        sceneType = 'REVIEW'
      } else if (bol1Save == 3 || (bol1Save == 1 && this.allParams.siteForm.reviewResult != '')) {
        sceneType = 'SUBMIT'
      }
      console.log(bol1Save)
      // if(bol1Save != 3){
      //   this.allParams.siteForm.reviewResult = null
      // }
      this.allParams.sceneType = sceneType
      if (type == 'SAVE') {
        this.allParams.opType = 'TEMPORARY_STORAGE'
      }
      this.allParams.orgJournals = this.orgJournals
      this.allParams.siteFormAddressList = this.siteFormAddressList
      this.allParams.fileRecords = this.fileRecords
      this.allParams.fileList = this.fileList
      this.allParams.siteFormPersonList = this.siteFormPersonList
      this.allParams.cateJournals = this.cateJournals
      const { department } = this.$store.getters.userInfo
      this.allParams.siteForm.ceeaDeptName = department || ''
      if (this.allParams.orgJournals.length < 1 || this.allParams.cateJournals.length < 1) {
        this.$message.error(this.$t('vendorMod.msgOrgCatInfo'))
        return false
      }
      return new Promise(rs => {
        this.$http({
          url: '/api-sup/serviceSiteForm/saveOrUpdateSiteForm',
          method: 'POST',
          data: this.allParams,
          loading: true
        })
          .then(async res => {
            let resData = res.data
            if (bol1Save == 1 && this.allParams.siteForm.reviewResult != '') {
              this.curOrderId = resData.formId
              if (type != 'SAVE') {
                this.getSiteFormDetail(this.curOrderId)
                await this.handlerAfter(type)
              }
            }
            console.log(type)
            console.log(bol1Save)
            if (type == 'SAVE' || bol1Save == 2 || bol1Save == 1) {
              this.$emit('tab-remove', this.$attrs.params.tabName)
              this.__setTabTodo('SiteAssessmentList.getQuerydata')
            }
            if (bol1Save == 3) {
              if (this.allParams.siteForm.reviewResult == '') {
                this.$emit('tab-remove', this.$attrs.params.tabName)
                this.__setTabTodo('SiteAssessmentList.getQuerydata')
                return false
              }
              // 提交
              // 走工作流 [[
              this.__setTabTodo('SiteAssessmentList.getQuerydata')
            } else if (bol1Save == 3 || (bol1Save == 1 && this.allParams.siteForm.reviewResult != '')) {
              // 新增暂存的时候查询旧数据
              this.curOrderId = resData.formId // 单据Id
              this.curOpt = 'edit'
              this.fileRefresh = true
              this.getSiteFormDetail(this.curOrderId)
            } else {
              this.$message({
                message: res.message,
                type: 'success'
              })
            }

            // this.__setTabTodo('SiteAssessmentList.getQuerydata')
          })
          .catch(err => {
            rs(false)
            console.log(err)
          })
      })
    }

  }
}
</script>
<style scoped lang="scss">
.topComment {
  text-align: center;
  margin-top: 10px;
}
.the-siteAssessmentDetail-detail {
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
  .the_first_col .el-form-item__content > div {
    width: 155px;
    margin-right: 11px;
  }
  .the_first_col .el-form-item__content > div:last-child {
    width: 255px;
  }
  .el-collapse-item__content > .el-button {
    margin-bottom: 5px;
  }
  .vendorAccessSteps {
    padding: 12px 5px;
    // border: 1px solid #efefef;
    // border-top: 0;
    // border-bottom: 1px solid #e6ebf5;
  }
}
</style>
