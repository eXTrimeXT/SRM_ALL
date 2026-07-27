<template>
  <el-container class="flex-container the-purchaseApplicationDetail-detail" direction="vertical">
    <el-main>
      <!-- <CWorkflowMulti
        ref="workflowMulti"
        v-model="activeTabName"
        :fun-params="workflowParamsInfo"
        :button-config-info="buttonConfigInfo"
        :button-custom="buttonCustom"
        @tab-click="workflowView"
        @workflow-handler="workflowHandler"
        @click-handler="type => saveOrSubmitBill(type)"
        @submit-direct="type => saveOrSubmitBill(type)"
        @confirm="(type, comment) => saveOrSubmitBill(type, comment)"
        @close-tab="back"
      > -->
      <ApprovalProcess
        :business-id="workflowBusinessId"
        :business-type="businessType"
        :status-map="{
          DRAFT: 'DRAFT', // 拟定
          SUBMITTED: 'APPROVING', // 已提交
          APPROVED: 'APPROVED', // 审批通过
          REJECTED: 'REJECTED', // 已驳回
          WITHDRAW: 'WITHDRAW', // 已撤回
          ABANDONED: 'ABANDONED' // 已废弃
        }"
        :readonly="$attrs.params.showType === 'readOnly'"
        :approvalStatus="requirementHead.auditStatus"
        :operation-pre-options="operationPreOptions"
        :show-button-config="showButtonConfig"
        @approval-handler-callback="approvalHanlder"
      >
        <div class="form-container2">
          <el-form
            ref="requirementHeadRef"
            :model="requirementHead"
            label-width="80px"
            label-position="top"
            :rules="rules"
          >
            <el-collapse v-model="activeDims" class="tab-form-style">
              <!-- 采购申请详情 -->
              <el-collapse-item ref="aptInfo" title="招标申请说明" name="1">
                <srm-row>
                  <srm-col v-if="!$attrs.params.hidden">
                    <el-form-item label="是否招标">
                      <DictSelect value="Y" code="YES_OR_NO" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 申请人 -->
                    <el-form-item :label="$t('purchaseDemand.applicant')">
                      <!-- :disabled="requirementHead.auditStatus === 'APPROVED'" -->
                      <QuickSearch
                        disabled
                        :show-input="requirementHead.applyByNickname"
                        show-key="nickname"
                        :scope-data="requirementHead"
                        name="scc_rbac_user_display"
                        @close-quicksearch="getUserObj"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="!$attrs.params.hidden">
                    <!-- 申请编号 -->
                    <el-form-item :label="$t('purchaseDemand.requirementHeadNum')">
                      <el-input v-model="requirementHead.requirementHeadNum" disabled />
                    </el-form-item>
                  </srm-col>
                  <!-- 需求类型 -->
                  <srm-col v-if="!$attrs.params.isMobile">
                    <el-form-item :label="$t('purchaseDemand.demandType')" prop="demandType">
                      <dict-select
                        v-model="requirementHead.demandType"
                        code="DEMAND_TYPE"
                        :disabled="true"
                        @change="handleTypeChange"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="!$attrs.params.isMobile">
                    <el-form-item label="所属板块">
                      <el-input v-model="requirementHead.orgBuName" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item label="申请公司" prop="orgId">
                      <template #label="{ column, $index }">
                        <div data-property="attributeValue" class="required-tag">
                          <span>申请公司</span>
                          <el-tooltip
                            effect="dark"
                            content="此处填写该招标项目的申请部门所属公司（即招标服务费的付款单位），而非该项目实际使用公司"
                            placement="top"
                            class="table-column-tooltip"
                          >
                            <em class="el-icon-warning tip-icon" />
                          </el-tooltip>
                        </div>
                      </template>
                      <el-input
                        v-if="isReadOnly"
                        v-model="requirementHead.orgName"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                      />
                      <template v-else>
                        <OrganizationSelector
                          ref="organizationSelector"
                          v-model="requirementHead.orgId"
                          :parent-id="-1"
                          node-type="OU"
                          :placeholder="$t('common.pleaseSelect')"
                          :disabled="
                            isReadOnly ||
                              requirementHead.auditStatus === 'APPROVED'
                          "
                          @select="selectHandler"
                        />
                      </template>
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="!$attrs.params.isMobile">
                    <!-- 申请部门 -->
                    <el-form-item :label="$t('purchaseDemand.ceeaDepartment')">
                      <el-input v-model="requirementHead.ceeaDepartmentName" :disabled="true" />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="!$attrs.params.isMobile">
                    <!-- 申请日期 -->
                    <el-form-item prop="applyDate" :label="$t('purchaseDemand.applyDate')">
                      <el-date-picker
                        v-model="requirementHead.applyDate"
                        disabled
                        type="date"
                        value-format="yyyy-MM-dd"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      label="技术负责人"
                      :rules="{
                        required: true,
                        message: '请填写'
                      }"
                    >
                      <QuickSearch
                        :show-input="user.fullName"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        show-key="nickname"
                        :scope-data="requirementHead"
                        name="scc_rbac_user_display"
                        @close-quicksearch="getUserObj"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="!$attrs.params.isMobile">
                    <el-form-item
                      label="技术负责人联系方式"
                      :rules="{
                        required: true,
                        message: '请填写'
                      }"
                    >
                      <el-input
                        v-model="user.phone"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        @blur="handlePhoneBlur(0 ,{phone: user.phone}, 'userPhone')"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="!$attrs.params.hidden">
                    <el-form-item
                      label="技术负责人工作年限"
                      :rules="{
                        required: true,
                        message: '请填写'
                      }"
                    >
                      <template #label="{ column, $index }">
                        <div data-property="attributeValue" class="required-tag">
                          <span>技术负责人工作年限</span>
                          <el-tooltip
                            effect="dark"
                            content="技术负责人工作年限应≥3年；技术负责人不允许参与评标"
                            placement="top"
                            class="table-column-tooltip"
                          >
                            <em class="el-icon-warning tip-icon" />
                          </el-tooltip>
                        </div>
                      </template>
                      <el-input
                        v-model="user.workYear"
                        type="number"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      label="需求来源"
                      :rules="{
                        required: true,
                        message: '请填写'
                      }"
                      prop="requireFrom"
                    >
                      <dict-select
                        v-model="requirementHead.requireFrom"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        code="PR_SOU_REQUIREMENT_FROM"
                        @change="handleTypeChange2"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="requirementHead.requireFrom == 'WITHOUT_PLAN'">
                    <el-form-item label="未报月度计划原因" prop="noReportMonthPlanReason">
                      <el-input
                        v-model="requirementHead.noReportMonthPlanReason"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      label="项目名称"
                      :rules="{
                        required: true,
                        message: '请填写'
                      }"
                      prop="projectName"
                    >
                      <template #label="{ column, $index }">
                        <div data-property="attributeValue" class="required-tag">
                          <span>项目名称</span>
                          <el-tooltip
                            effect="dark"
                            content="公司名称+项目所在地+第几期（若涉及）+标的物名称
举例：长城汽车徐水分公司二期总装车间新增360环视标定设备项目
若该计划不是第一次提报，请注明（如前期递交招标计划后，取消后重新递交计划的情况，请注明）
举例：XX公司XX设备采购项目（第二次提报计划）"
                            placement="top"
                            class="table-column-tooltip"
                          >
                            <em class="el-icon-warning tip-icon" />
                          </el-tooltip>
                        </div>
                      </template>
                      <el-tooltip class="item" effect="dark" :content="requirementHead.projectName" placement="top-start">
                        <el-input
                          v-model="requirementHead.projectName"
                          :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        />
                      </el-tooltip>
                    </el-form-item>
                  </srm-col>
                  <!-- v-if="['MONTH','YEAR'].includes(requirementHead.requireFrom)" -->
                  <srm-col v-if="!$attrs.params.isMobile">
                    <el-form-item
                      :rules="{
                        required: true,
                        message: '请填写'
                      }"
                      label="月份"
                      prop="projectMonth"
                    >
                      <!-- <el-input-number
                        v-model="requirementHead.projectMonth"
                        :min="1"
                        :max="12"
                        :precision="0"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        controls-position="right"
                      /> -->
                      <el-date-picker
                        v-model="requirementHead.projectMonth"
                        type="month"
                        value-format="yyyy-MM"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      :rules="{
                        required: true,
                        message: '请填写'
                      }"
                      label="所属品类"
                      prop="categoryName"
                    >
                      <CategorySelect
                        v-model="requirementHead.categoryName"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        show-key="categoryName"
                        @select="comfirmSelect"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="!$attrs.params.hidden">
                    <el-form-item label="投资编号">
                      <el-input
                        v-model="requirementHead.investNo"
                        :disabled="$attrs.params.showType == 'readOnly'"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      label="数量/规模"
                      :rules="{
                        required: true,
                        message: '请填写'
                      }"
                      prop="requireQuantity"
                    >
                      <el-input
                        v-model="requirementHead.requireQuantity"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      label="概算金额(万元)"
                      :rules="{
                        required: true,
                        message: '请填写'
                      }"
                      prop="totalAmountByTenKilo"
                    >
                      <el-input-number
                        v-model="requirementHead.totalAmountByTenKilo"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        controls-position="right"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item label="是否公示" prop="needPublic">
                      <DictSelect
                        v-model="requirementHead.needPublic"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED' || requirementHead.requireFrom == 'SPECIAL_SOU'"
                        code="YES_OR_NO"
                        @change="clearGongshi"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="requirementHead.needPublic == 'Y'">
                    <el-form-item label="公示截止时间" prop="publicEndTime">
                      <template #label="{ column, $index }">
                        <div data-property="attributeValue" class="required-tag">
                          <span>公示截止时间</span>
                          <el-tooltip
                            effect="dark"
                            content="	公示周期至少7天（不含法定节假日，含周六日）。若项目无需前置技术交流，则公示截止时间即为递交招标资料时间；合格流程到达招标部第二天开始计算"
                            placement="top"
                            class="table-column-tooltip"
                          >
                            <em class="el-icon-warning tip-icon" />
                          </el-tooltip>
                        </div>
                      </template>
                      <el-date-picker
                        v-model="requirementHead.publicEndTime"
                        type="datetime"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        value-format="yyyy-MM-dd HH:mm:ss"
                        default-time="17:00:00"
                        clearable
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="requirementHead.needPublic == 'N' && requirementHead.requireFrom != 'SPECIAL_SOU'">
                    <el-form-item label="不公示原因" prop="noPublicReasonChoose">
                      <DictSelect
                        v-model="requirementHead.noPublicReasonChoose"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        code="PR_SOU_REQUIREMENT_NO_PUBLIC"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col
                    v-if="requirementHead.needPublic == 'N' && requirementHead.requireFrom != 'SPECIAL_SOU'"
                  >
                    <el-form-item label="具体原因说明" prop="noPublicReason">
                      <template #label="{ column, $index }">
                        <div data-property="attributeValue" class="required-tag">
                          <span>具体原因说明</span>
                          <el-tooltip
                            effect="dark"
                            content="①若因时间紧急，需描述清楚：
需求产生时间，产生后至今做了哪些招标的准备工作，最终要求什么时候完工，厂家的工期
②若因集团战略保密，请描述客观原因
③若因限定供应商范围不公示，请上传总经理签字的限定供应商范围文件"
                            placement="top"
                            class="table-column-tooltip"
                          >
                            <em class="el-icon-warning tip-icon" />
                          </el-tooltip>
                        </div>
                      </template>
                      <el-input
                        v-model="requirementHead.noPublicReason"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="!$attrs.params.isMobile">
                    <el-form-item
                      label="项目所在地"
                      :rules="{
                        required: true,
                        message: '请填写'
                      }"
                      prop="projectAddress"
                    >
                      <template #label="{ column, $index }">
                        <div data-property="attributeValue" class="required-tag">
                          <span>项目所在地</span>
                          <el-tooltip
                            effect="dark"
                            content="省份+城市"
                            placement="top"
                            class="table-column-tooltip"
                          >
                            <em class="el-icon-warning tip-icon" />
                          </el-tooltip>
                        </div>
                      </template>
                      <el-input
                        v-model="requirementHead.projectAddress"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col
                    v-if="requirementHead.requireFrom !== 'SPECIAL_SOU' && requirementHead.needPublic != 'N'"
                  >
                    <el-form-item
                      label="前置技术交流意向"
                      :rules="{
                        required: true,
                        message: '请填写'
                      }"
                      prop="prefixTechDiscussion"
                    >
                      <DictSelect
                        v-model="requirementHead.prefixTechDiscussion"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        code="YES_OR_NO"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col
                    v-if="(requirementHead.prefixTechDiscussion != 'N' && requirementHead.requireFrom == 'MONTH') || (requirementHead.needPublic == 'N' && requirementHead.requireFrom == 'MONTH') || (requirementHead.requireFrom == 'WITHOUT_PLAN' && requirementHead.needPublic == 'N')"
                  >
                    <el-form-item
                      label="递交招标资料时间"
                      :rules="{
                        required: true,
                        message: '请填写'
                      }"
                      prop="sendSouProfileEndDate"
                    >
                      <template #label="{ column, $index }">
                        <div data-property="attributeValue" class="required-tag">
                          <span>递交招标资料时间</span>
                          <el-tooltip
                            effect="dark"
                            content="与公示截止时间间隔最多5天"
                            placement="top"
                            class="table-column-tooltip"
                          >
                            <em class="el-icon-warning tip-icon" />
                          </el-tooltip>
                        </div>
                      </template>
                      <el-date-picker
                        v-model="requirementHead.sendSouProfileEndDate"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        value-format="yyyy-MM-dd"
                        clearable
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="['MONTH','WITHOUT_PLAN'].includes(requirementHead.requireFrom)">
                    <el-form-item
                      label="是否指定品牌"
                      :rules="{
                        required: true,
                        message: '请填写'
                      }"
                      prop="ifAppointBrand"
                    >
                      <DictSelect
                        v-model="requirementHead.ifAppointBrand"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        code="YES_OR_NO"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="['MONTH','WITHOUT_PLAN'].includes(requirementHead.requireFrom) && requirementHead.ifAppointBrand == 'Y'">
                    <el-form-item label="指定品牌文件">
                      <template #label="{ column, $index }">
                        <div data-property="attributeValue" class="required-tag">
                          <span style="color: red; font-size: 12px;">*</span>
                          <span>指定品牌文件</span>
                        </div>
                      </template>
                      <SrmCommonFile
                        :extra-data="fileInfo"
                        :default-file="{
                          fileId: requirementHead.appointBrandFileId,
                          fileName: requirementHead.appointBrandFileName
                        }"
                        :readonly="isReadOnly"
                        @on-change="({file}) => requireProductFileSuccess5(file)"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="['MONTH','WITHOUT_PLAN'].includes(requirementHead.requireFrom)">
                    <el-form-item
                      label="是否限定单位"
                      :rules="{
                        required: true,
                        message: '请填写'
                      }"
                      prop="ifQualifyUnit"
                    >
                      <DictSelect
                        v-model="requirementHead.ifQualifyUnit"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        code="YES_OR_NO"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="['MONTH','WITHOUT_PLAN'].includes(requirementHead.requireFrom) && requirementHead.ifQualifyUnit == 'Y'">
                    <el-form-item label="限定单位文件">
                      <template #label="{ column, $index }">
                        <div data-property="attributeValue" class="required-tag">
                          <span style="color: red; font-size: 12px;">*</span>
                          <span>限定单位文件</span>
                        </div>
                      </template>
                      <SrmCommonFile
                        :extra-data="fileInfo"
                        :default-file="{
                          fileId: requirementHead.qualifyUnitFileId,
                          fileName: requirementHead.qualifyUnitFileName
                        }"
                        :readonly="isReadOnly"
                        @on-change="({file}) => requireProductFileSuccess6(file)"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="requirementHead.orgBuCode == 'JSZX'">
                    <el-form-item label="计划编号">
                      <QuickSearch
                        :show-input="requirementHead.planNo"
                        show-key="planNo"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        :scope-data="requirementHead"
                        name="scc_npm_pr_project_plan_query"
                        @close-quicksearch="getPlanNo"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="requirementHead.requireFrom == 'SPECIAL_SOU'">
                    <el-form-item label="特殊招标类型" prop="specialSouType">
                      <DictSelect
                        v-model="requirementHead.specialSouType"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        code="PR_SOU_REQUIREMENT_SPECIAL_TYPE"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col
                    v-if="requirementHead.requireFrom == 'SPECIAL_SOU' && requirementHead.specialSouType == 'SPECIAL_VENDOR_ONE'"
                  >
                    <el-form-item
                      label="特定原因"
                      :rules="{
                        required: requirementHead.specialSouType === 'SPECIAL_VENDOR_ONE' ? true : false,
                        message: '请填写'
                      }"
                      prop="specialReason"
                    >
                      <DictSelect
                        v-model="requirementHead.specialReason"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        code="PR_SOU_REQUIREMENT_SPECIAL_REASON"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="requirementHead.specialSouType == 'TIME_URGENT'">
                    <el-form-item label="需求产生时间">
                      <template #label="{ column, $index }">
                        <div data-property="attributeValue" class="required-tag">
                          <span style="color: red; font-size: 12px;">*</span>
                          <span>需求产生时间</span>
                          <el-tooltip
                            effect="dark"
                            content="领导什么时间安排的要开展本项目"
                            placement="top"
                            class="table-column-tooltip"
                          >
                            <em class="el-icon-warning tip-icon" />
                          </el-tooltip>
                        </div>
                      </template>
                      <el-date-picker
                        v-model="requirementHead.requireProductDate"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        value-format="yyyy-MM-dd"
                        :picker-options="pickerOptions1"
                        clearable
                        @change="shengyu()"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="requirementHead.specialSouType == 'TIME_URGENT' && !$attrs.params.isMobile">
                    <el-form-item label="需求产生时间附件">
                      <template #label="{ column, $index }">
                        <div data-property="attributeValue" class="required-tag">
                          <span style="color: red; font-size: 12px;">*</span>
                          <span>需求产生时间附件</span>
                          <el-tooltip
                            effect="dark"
                            content="相应权限领导签字的申请、会议纪要、说明等文件均可，此处只能上传一份文件，文件多的请放到一个文件夹压缩后上传"
                            placement="top"
                            class="table-column-tooltip"
                          >
                            <em class="el-icon-warning tip-icon" />
                          </el-tooltip>
                        </div>
                      </template>
                      <SrmCommonFile
                        :extra-data="fileInfo"
                        :default-file="{
                          fileId: requirementHead.requireProductFileId,
                          fileName: requirementHead.requireProductFileName
                        }"
                        :readonly="isReadOnly"
                        @on-change="({file}) => requireProductFileSuccess(file)"
                      />
                    </el-form-item>
                  </srm-col>

                  <srm-col v-if="requirementHead.specialSouType == 'TIME_URGENT'">
                    <el-form-item label="工期交货期">
                      <template #label="{ column, $index }">
                        <div data-property="attributeValue" class="required-tag">
                          <span style="color: red; font-size: 12px;">*</span>
                          <span>工期交货期</span>
                          <el-tooltip
                            effect="dark"
                            content="供应商做这项工作需要几天"
                            placement="top"
                            class="table-column-tooltip"
                          >
                            <em class="el-icon-warning tip-icon" />
                          </el-tooltip>
                        </div>
                      </template>
                      <el-input-number
                        v-model="requirementHead.deliveryDay"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        controls-position="right"
                        @change="shengyu()"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="requirementHead.specialSouType == 'TIME_URGENT' && !$attrs.params.isMobile">
                    <el-form-item label="工期交货期附件">
                      <template #label="{ column, $index }">
                        <div data-property="attributeValue" class="required-tag">
                          <span style="color: red; font-size: 12px;">*</span>
                          <span>工期交货期附件</span>
                          <el-tooltip
                            effect="dark"
                            content="至少提供两家供应商加盖公章的工期文件，国外单位无公章的可以是正式邮件或签字文件，此处只能上传一份文件，文件多的请放到一个文件夹压缩后上传"
                            placement="top"
                            class="table-column-tooltip"
                          >
                            <em class="el-icon-warning tip-icon" />
                          </el-tooltip>
                        </div>
                      </template>
                      <SrmCommonFile
                        :extra-data="fileInfo"
                        :default-file="{
                          fileId: requirementHead.deliveryDayFileId,
                          fileName: requirementHead.deliveryDayFileName
                        }"
                        :readonly="isReadOnly"
                        @on-change="({file}) => requireProductFileSuccess2(file)"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="requirementHead.specialSouType == 'TIME_URGENT'">
                    <el-form-item label="签合同用时">
                      <template #label="{ column, $index }">
                        <div data-property="attributeValue" class="required-tag">
                          <span style="color: red; font-size: 12px;">*</span>
                          <span>签合同用时</span>
                          <el-tooltip
                            effect="dark"
                            content="定厂后签合同大概需要几天"
                            placement="top"
                            class="table-column-tooltip"
                          >
                            <em class="el-icon-warning tip-icon" />
                          </el-tooltip>
                        </div>
                      </template>
                      <el-input-number
                        v-model="requirementHead.signContractDay"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        :min="1"
                        controls-position="right"
                        @change="shengyu()"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="requirementHead.specialSouType == 'TIME_URGENT'">
                    <el-form-item label="投入使用时间">
                      <template #label="{ column, $index }">
                        <div data-property="attributeValue" class="required-tag">
                          <span style="color: red; font-size: 12px;">*</span>
                          <span>投入使用时间</span>
                          <el-tooltip
                            effect="dark"
                            content="什么时间必须投入使用或执行"
                            placement="top"
                            class="table-column-tooltip"
                          >
                            <em class="el-icon-warning tip-icon" />
                          </el-tooltip>
                        </div>
                      </template>
                      <el-date-picker
                        v-model="requirementHead.putIntoUseDate"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        :picker-options="pickerOptions2"
                        value-format="yyyy-MM-dd"
                        clearable
                        @change="shengyu()"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="requirementHead.specialSouType == 'TIME_URGENT' && !$attrs.params.isMobile">
                    <el-form-item label="投入使用时间附件">
                      <template #label="{ column, $index }">
                        <div data-property="attributeValue" class="required-tag">
                          <span style="color: red; font-size: 12px;">*</span>
                          <span>投入使用时间附件</span>
                          <el-tooltip
                            effect="dark"
                            content="相应权限领导签字的申请、会议纪要、说明等文件均可，此处只能上传一份文件，文件多的请放到一个文件夹压缩后上传"
                            placement="top"
                            class="table-column-tooltip"
                          >
                            <em class="el-icon-warning tip-icon" />
                          </el-tooltip>
                        </div>
                      </template>
                      <SrmCommonFile
                        :extra-data="fileInfo"
                        :default-file="{
                          fileId: requirementHead.putIntoUseDateFileId,
                          fileName: requirementHead.putIntoUseDateFileName
                        }"
                        :readonly="isReadOnly"
                        @on-change="({file}) => requireProductFileSuccess3(file)"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="requirementHead.specialSouType == 'TIME_URGENT'">
                    <el-form-item label="剩余时间">
                      <el-input v-model="requirementHead.remainingDay" :disabled="true" />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      v-if="Viewflag == 'change'"
                      label="变更后概算金额（万元）"
                      :rules="{
                        required: true,
                        message: '请填写'
                      }"
                      prop="afterTotalAmountByTenKilo"
                    >
                      <el-input
                        v-model="requirementHead.afterTotalAmountByTenKilo"
                        :disabled="$attrs.params.showType == 'readOnly'"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="1">
                    <el-form-item
                      v-if="Viewflag == 'change'"
                      label="变更原因"
                      :rules="{
                        required: true,
                        message: '请填写'
                      }"
                      prop="changeReason"
                    >
                      <el-input
                        v-model="requirementHead.changeReason"
                        type="textarea"
                        :disabled="$attrs.params.showType == 'readOnly'"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="requirementHead.requireFrom == 'SPECIAL_SOU'" :initCol="1">
                    <el-form-item
                      label="具体特殊招标原因说明"
                      :rules="{
                        required: true,
                        message: '请填写'
                      }"
                      prop="otherSpecialReason"
                    >
                      <el-input
                        v-model="requirementHead.otherSpecialReason"
                        type="textarea"
                        :autosize="{ minRows: 2, maxRows: 5 }"
                        :maxlength="1500"
                        show-word-limit
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                      />
                    </el-form-item>
                  </srm-col>
                </srm-row>

                <div style="margin-bottom: 20px">
                  <p
                    v-if="!$attrs.params.isMobile"
                    class="btn_line"
                  >
                    <el-button
                      v-if="requirementHead.auditStatus !== 'APPROVED'"
                      :disabled="isReadOnly"
                      type="primary"
                      class="detail-pbtn"
                      @click="requireGroupAdd"
                    >
                      {{ $t('common.add') }}
                    </el-button>
                    <el-button
                      type="primary"
                      class="detail-pbtn"
                      :disabled="requireGroup.length == 0"
                      @click="handleExport"
                    >
                      {{ $t('common.export') }}
                    </el-button>
                    <span style="margin-left: 10px">
                      至少推荐2家有效供应商
                    </span>
                  </p>
                  <el-table :data="requireGroup" style="width: 100%" border max-height="250px">
                    <el-table-column align="center" prop="vendorName" label="推荐单位名称">
                      <template slot-scope="scope">
                        <el-input
                          v-if="!$attrs.params.isMobile"
                          v-model="scope.row.vendorName"
                          :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        />
                        <span v-else>{{ scope.row.vendorName }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column
                      align="center"
                      prop="contactName"
                      label="联系人名称"
                    >
                      <template slot-scope="scope">
                        <el-input
                          v-if="!$attrs.params.isMobile"
                          v-model="scope.row.contactName"
                          :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        />
                        <span v-else>{{ scope.row.contactName }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column align="center" prop="phone" label="联系方式">
                      <template slot-scope="scope">
                        <el-input
                          v-if="!$attrs.params.isMobile"
                          v-model="scope.row.phone"
                          :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                          @blur="handlePhoneBlur(scope.$index,scope.row)"
                        />
                        <span v-else>{{ scope.row.phone }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column align="center" prop="email" label="邮箱">
                      <template slot-scope="scope">
                        <el-input
                          v-if="!$attrs.params.isMobile"
                          v-model="scope.row.email"
                          :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                          @blur="handleEmailBlur(scope.$index,scope.row)"
                        />
                        <span v-else>{{ scope.row.email }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column align="center" prop="recommendFrom" label="推荐单位来源">
                      <template slot="header" slot-scope="scope">
                        <div data-property="attributeValue" class="required-tag">
                          <span>推荐单位来源</span>
                          <el-tooltip
                            effect="dark"
                            content="明确到推荐人"
                            placement="top"
                            class="table-column-tooltip"
                          >
                            <em class="el-icon-warning tip-icon" />
                          </el-tooltip>
                        </div>
                      </template>
                      <template slot-scope="scope">
                        <el-input
                          v-if="!$attrs.params.isMobile"
                          v-model="scope.row.recommendFrom"
                          :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        />
                        <span v-else>{{ scope.row.recommendFrom }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column
                      v-if="!$attrs.params.isMobile"
                      :label="$t('common.operation')"
                      width="60"
                    >
                      <template slot-scope="scope">
                        <el-button
                          type="text"
                          :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                          @click="handleDelClick(scope.$index, scope.row, 'list')"
                        >
                          {{ $t('common.delete') }}
                        </el-button>
                      </template>
                    </el-table-column>
                  </el-table>
                </div>

                <srm-row>
                  <srm-col :init-col="1">
                    <el-form-item label="项目概况及范围" prop="projectOverview">
                      <template #label="{ column, $index }">
                        <div data-property="attributeValue" class="required-tag">
                          <span>项目概况及范围</span>
                          <el-tooltip
                            effect="dark"
                            content="	①工程类：本工程采取交钥匙工程形式，主要内容为XXXXX，包括设计、试验、制造、包装、运输、装卸、就位、安装、调试、培训、售后服务、生产陪同等的全部内容。
②设备类：明确需要采购的设备清单
③服务类：项目需要写明服务内容"
                            placement="top"
                            class="table-column-tooltip"
                          >
                            <em class="el-icon-warning tip-icon" />
                          </el-tooltip>
                        </div>
                      </template>
                      <el-input
                        v-model="requirementHead.projectOverview"
                        type="textarea"
                        :autosize="{ minRows: 2, maxRows: 5 }"
                        maxlength="3000"
                        show-word-limit
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col :init-col="1">
                    <el-form-item label="技术要求" prop="techRequire">
                      <template #label="{ column, $index }">
                        <div data-property="attributeValue" class="required-tag">
                          <span>技术要求</span>
                          <el-tooltip
                            effect="dark"
                            content="	1.暂定工期/交货期:
2.填写提示：需要写明项目的核心要求。
① 若为设备类项目需要填写设备的核心参数
② 若为服务类项目需要供应商具备的能力等
③ 若为工程类项目填写项目工程施工要求及主要材料的要求等
④ 其他影响供应商投标的条款
3.若项目涉及限定品牌/供应商，请在此处标明，并提供总经理签字的限定原因说明，随计划流程一并提交
4.技术要求中若有否决项，需标明，若未填写，后期技术任务书中也不允许再设置否决项"
                            placement="top"
                            class="table-column-tooltip"
                          >
                            <em class="el-icon-warning tip-icon" />
                          </el-tooltip>
                        </div>
                      </template>
                      <el-input
                        v-model="requirementHead.techRequire"
                        type="textarea"
                        :autosize="{ minRows: 2, maxRows: 5 }"
                        maxlength="3000"
                        show-word-limit
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col :init-col="1">
                    <el-form-item label="供应商资质要求" prop="vendorQualificationRequire">
                      <template #label="{ column, $index }">
                        <div data-property="attributeValue" class="required-tag">
                          <span>供应商资质要求</span>
                          <el-tooltip
                            effect="dark"
                            content="如基建类资质
注册资金：
填写提示：资质要求必须符合项目实际背景，禁止过度要求或无理要求
招标公告内容不得将投标人的资产总额、营业收入、利润、纳税额等规模条件作为资格要求或者评审因素；
如需签订保密协议，在此处进行体现“该项目需要签订保密协议后方可发放招标文件”，并将保密协议模板传至附件处"
                            placement="top"
                            class="table-column-tooltip"
                          >
                            <em class="el-icon-warning tip-icon" />
                          </el-tooltip>
                        </div>
                      </template>
                      <el-input
                        v-model="requirementHead.vendorQualificationRequire"
                        type="textarea"
                        :autosize="{ minRows: 2, maxRows: 5 }"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col :init-col="1">
                    <el-form-item label="业绩要求" prop="performanceRequire">
                      <template #label="{ column, $index }">
                        <div data-property="attributeValue" class="required-tag">
                          <span>业绩要求</span>
                          <el-tooltip
                            effect="dark"
                            content="	举例：
汽车生产线类：具备X条以上国内外汽车厂大型冲压/涂装/总装生产线（suv年产量大于16万台）的合作业绩；
住宅建筑类：具备X万平米以上的建筑总承包业绩；
汽车厂房基建类：具备X家国内外知名汽车整车厂或零部件的合作业绩"
                            placement="top"
                            class="table-column-tooltip"
                          >
                            <em class="el-icon-warning tip-icon" />
                          </el-tooltip>
                        </div>
                      </template>
                      <el-input
                        v-model="requirementHead.performanceRequire"
                        type="textarea"
                        :autosize="{ minRows: 2, maxRows: 5 }"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                      />
                    </el-form-item>
                  </srm-col>
                </srm-row>
                <div v-if="!$attrs.params.isMobile && requirementHead.requireFrom !== 'SPECIAL_SOU'" style="color: red">
                  <p>注意事项:</p>
                  <p>①项目一经公示不得更改！技术要求中的否决项（若有）、资质、业绩务必注明，招标计划中的否决项、资质、业绩要求即为项目最终招标时的否决项、资质、业绩要求；</p>
                  <p>②同一单位相同或相似项目30天内仅限提报一次！</p>
                  <p>③招标计划递交后不符合要求退回的，不计入当天招标计划，以终版招标计划递交时间为准；</p>
                  <p>④逢节假日前的最后一个工作日递交的计划节假日后公示，请合理安排计划提报时间！</p>
                  <p>⑤若该项目不能对外公示，需在备注中填写不公示，并写明客观原因；</p>
                  <p>⑥公示项目的招标计划中除预算和推荐单位外，其他内容均会对外公示，请确认所填内容适合对外公开。</p>
                </div>
              </el-collapse-item>
              <!--附件上传-->
              <el-collapse-item
                v-if="!$attrs.params.isMobile"
                title="相关附件上传"
                name="3"
              >
                <div style="display: flex">
                  <div class="btn_line" style="height:27px">
                    <el-button
                      v-if="requirementHead.auditStatus !== 'APPROVED'"
                      :disabled="isReadOnly"
                      type="primary"
                      class="detail-pbtn"
                      @click="addUploadOne"
                    >
                      {{ $t('common.add') }}
                    </el-button>
                    <el-button
                      type="primary"
                      @click="batchDownload"
                    >
                      {{ $t('cusEntry.common.batchDownload') }}
                    </el-button>
                  </div>
                  <div style="margin-left: 26px;margin-top:-14px; color:red">
                    <!-- <p>1、若存在其他附件，可选择文件类型为其他，并上传附件</p>
                    <p>
                      2、非特殊招标的项目若存在限定品牌或者限定供应商，可选择对应的文件类型，并上传附件
                    </p> -->
                    <p>若有可上传：保密协议、清单明细（无价格内容、无与项目不相关插页等）、固定资产处置审批文件及物资明细表等</p>
                  </div>
                </div>

                <el-table
                  :data="requirementAttaches"
                  style="width: 100%"
                  border
                  max-height="250px"
                  @selection-change="selectFileChange"
                >
                  <el-table-column
                    align="center"
                    type="selection"
                    width="50"
                  />
                  <el-table-column
                    align="center"
                    type="index"
                    :label="$t('purSettlementMod.tabindex')"
                    width="50"
                  />
                  <!-- <el-table-column align="center" prop="fileType" label="文件类型">
                    <template slot-scope="scope">
                      <DictSelect
                        v-model="scope.row.fileType"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        code="PR_SOU_REQUIREMENT_FILE_TYPE"
                      />
                    </template>
                  </el-table-column> -->
                  <!-- 附件 -->
                  <el-table-column
                    align="center"
                    prop="attachName"
                    :label="$t('purchaseDemand.attachment')"
                  >
                    <template slot-scope="scope">
                      <SrmCommonFile
                        :extra-data="fileInfo"
                        :default-file="{
                          fileId: scope.row.fileId,
                          fileName: scope.row.fileName
                        }"
                        :readonly="isReadOnly"
                        @on-change="({file}) => outerHandleUploadSuccess(file,scope.row)"
                      />
                    </template>
                  </el-table-column>
                  <!-- 上传时间 -->
                  <el-table-column
                    align="center"
                    prop="creationDate"
                    :label="$t('purchaseDemand.attachmentCreatedDate')"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column :label="$t('common.operation')" width="60">
                    <template slot-scope="scope">
                      <el-button
                        type="text"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        @click="handleDelClick(scope.$index, scope.row, 'del')"
                      >
                        {{ $t('common.delete') }}
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </el-collapse-item>
              <el-collapse-item
                v-if="!$attrs.params.isMobile"
                title="板块接口人清单"
                name="3"
              >
                <!-- <el-button
                  type="text"
                  @click="downLoadFile"
                >
                  板块接口人名单.xlsx
                </el-button> -->
                <SrmCommonFile
                  :default-file="{
                    fileId: sectionPersonnelId,
                    fileName: sectionPersonnelName
                  }"
                  :readonly="true"
                />
              </el-collapse-item>
              <el-collapse-item
                v-if="requirementHead.requireFrom == 'SPECIAL_SOU' && !$attrs.params.isMobile"
                title="注意事项"
                name="3"
              >
                <p>1、所有上传附件需提供签字扫描版或电子审批流程截图；</p>
                <p>2、本流程不作为该项目定厂的支持性文件，仅表明该项目特殊招标。</p>
              </el-collapse-item>
            </el-collapse>
          </el-form>
        </div>
      <!-- </CWorkflowMulti> -->
      </ApprovalProcess>
    </el-main>

    <!-- 物料明细新增 -->
    <MaterialSelectDialog
      :visible.sync="dialogVisible"
      :requirementHead="requirementHead"
      :queryForm="queryForm"
      :pageInfo="pageInfo"
      :displayItemTable="displayItemTable"
      @queryContent="queryContent"
      @addOneContent="addOneContent"
      @close="dialogVisible = false"
    />
  </el-container>
</template>
<script>
import _pick from 'lodash/pick'
import OrganizationSelector from 'lib@/components/organization-selector'
import { tabTodoMixin } from '@/utils/mixins'
import QuickSearch from 'lib@/components/QuickSearch'
import { parseTime } from '@/utils'
import CPagination from 'lib@/components/c-pagination'
import MImport from 'lib@/components/import'
import { downloadFileLink, downloadFileLinkByPost } from 'lib@/utils/file'
import WorkflowCommon from '@/library/mixins/workflow-common'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import { getToken } from '@/utils/auth'
import axios from 'axios'
import { sysPrefix } from '@/config/ipConfig'
import { purchaseApplicationApi } from 'modc@/buyer/purchasingDemand/api'
import MaterialSelectDialog from 'modc@/buyer/purchasingDemand/views/purchaseApplication/components/materialSelectDialog'
import CategorySelect from 'modc@/buyer/vendorManagementBuyer/views/quaOfReviewEngine/components/categorySelect'
import { transformMQL } from '@/library/utils/util'
import { queryFieldValueBySelfExpression } from '@meicloud/render-engine'
import { getUserInfo } from '@/api/user'
import ApprovalProcess from 'modc@/components/approval-process'
import { sceneFileApi } from 'modb@/basicSetting/api/basicSetting'
import { getMenuInfo } from '@/utils/menu-auth'
export default {
  name: 'PurchaseApplicationDetail',
  components: {
    MImport,
    QuickSearch,
    CPagination,
    OrganizationSelector,
    MaterialSelectDialog,
    CategorySelect,
    ApprovalProcess
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      sectionPersonnelName: '',
      sectionPersonnelId: null,
      fileuploadId: null,
      fileSourceName: null,
      buttonCustom: {
        // preRejectedData: {
        //   name: '审核通过',
        //   view: true,
        //   disabled: false
        // }
      },
      requireGroupDel: [],
      requirementAttachesDel: [],
      user: {
        username: null,
        fullName: null,
        userId: null,
        phone: null,
        workYear: null
      },
      pageInfo: {
        pageTotal: 0,
        pageIndex: 1,
        pageSize: 15
      },
      detailPag: {
        currentPage: 1,
        pageSize: 15
      },
      copyInit: {
        num: 0,
        bol: true
      }, // 需求数量初始值
      materialParam: {},
      supplierParam: {},
      lastCategoryId: '',
      plTableColumnRowStyle: {
        boxSizing: 'border-box',
        overflow: 'hidden',
        whiteSpace: 'nowrap',
        textOverflow: 'ellipsis',
        wordBreak: 'break-all'
      },
      extraData: {
        fileModular: 'pm',
        fileFunction: 'purchaseApplication',
        fileType: 'excel'
      },
      iModal: {
        title: this.$t('common.excelImport'),
        upLoadUrl: '/api-sup-ce/pr/requirementLine/v2/import'
      },
      pickerOptions: {
        disabledDate (time) {
          const today = new Date()
          today.setHours(0)
          today.setMinutes(0)
          today.setSeconds(0)
          today.setMilliseconds(0)
          return time.getTime() < today.getTime()
        }
      },
      curRole: this.$store.getters.userType,
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'vendorBiddingManagement',
        fileType: 'images'
      },
      queryForm: {
        categoryCode: null,
        categoryId: null,
        categoryName: null,
        materialCode: null,
        materialId: null,
        materialName: null,
        orgId: null,
        organizationId: null,
        organizationName: null
      },
      pickerOptions1: {
        disabledDate (time) {
          return time.getTime() > Date.now()
        }
      },
      pickerOptions2: {
        disabledDate (time) {
          return time.getTime() <= Date.now()
        }
      },
      requireGroup: [],
      requirementHead: {
        extBidFlag: 'Y',
        afterTotalAmountByTenKilo: null,
        changeReason: null,
        vendorQualificationRequire: null,
        performanceRequire: null,
        techRequire: null,
        projectOverview: null,
        otherSpecialReason: null,
        putIntoUseDate: null,
        putIntoUseFileId: null,
        putIntoUseFileName: null,
        signContractDay: null,
        deliveryFileId: null,
        deliveryFileName: null,
        deliveryDay: null,
        remainingDay: null,
        requireProductFileName: null,
        requireProductFileId: null,
        requireProductDate: null,
        specialReason: null,
        specialSouType: null,
        planNo: null,
        sendSouProfileEndDate: null,
        publicEndTime: null,
        prefixTechDiscussion: null,
        projectAddress: null,
        noPublicReason: null,
        needPublic: 'Y',
        noPublicReasonChoose: null,
        totalAmountByTenKilo: null,
        requireQuantity: null,
        investNo: null,
        projectMonth: null,
        projectName: null,
        noReportMonthPlanReason: null,
        requireFrom: null,
        orgBuName: null,
        iszhaobiao: 'N',
        budgetManagementNum: null,
        demandType: null,
        requirementLineList: [],
        requirementHeadId: null,
        ceeaPurchaseType: null,
        orgId: null,
        orgCode: null,
        orgName: null,
        createdId: null,
        categoryId: null,
        categoryCode: null,
        categoryName: null,
        organizationId: null,
        organizationCode: null,
        organizationName: null,
        createdFullName: null,
        requirementHeadNum: null,
        auditStatus: 'DRAFT',
        ceeaDepartmentId: null,
        ceeaDepartmentCode: null,
        ceeaDepartmentName: null,
        ceeaPrType: null,
        ceeaProjectName: null,
        ceeaProjectNum: null,
        ceeaProjectUserId: null,
        ceeaProjectUserNickname: null,
        ceeaProjectApprovalNum: null,
        ceeaIfVote: null,
        ceeaVoteProjectName: null,
        ceeaAssetType: null,
        totalBudget: 0,
        usedBudget: 0,
        thisYearBudgetAmount: '0',
        nextYearBudgetAmount: '0',
        ceeaIfHq: null,
        ceeaIfUseLogo: null,
        ceeaUrgencyExplain: null,
        ceeaAppointReason: null,
        comments: null,
        applyDate: parseTime(new Date(), '{y}-{m}-{d}')
      },
      currentPage: 1,
      allRequirementLineList: [],
      activeDims: ['1', '2', '3'],
      rules: {
        orgId: [{ required: true, message: '请填写' }],
        fullName: [{ required: true, message: '请填写' }],
        phone: [{ required: true, message: '请填写' }],
        requireFrom: [{ required: true, message: '请填写' }],
        noReportMonthPlanReason: [{ required: true, message: '请填写' }],
        projectName: [{ required: true, message: '请填写' }],
        projectMonth: [{ required: true, message: '请填写' }],
        categoryName: [{ required: true, message: '请填写' }],
        requireQuantity: [{ required: true, message: '请填写' }],
        totalAmountByTenKilo: [{ required: true, message: '请填写' }],
        noPublicReason: [{ required: true, message: '请填写' }],
        projectAddress: [{ required: true, message: '请填写' }],
        prefixTechDiscussion: [{ required: true, message: '请填写' }],
        sendSouProfileEndDate: [{ required: true, message: '请填写' }],
        specialReason: [{ required: true, message: '请填写' }],
        publicEndTime: [{ required: true, message: '请填写' }],
        needPublic: [{ required: true, message: '请填写' }],
        noPublicReasonChoose: [{ required: true, message: '请填写' }],
        projectOverview: [{ required: true, message: '请填写' }],
        techRequire: [{ required: true, message: '请填写' }],
        performanceRequire: [{ required: true, message: '请填写' }],
        vendorQualificationRequire: [{ required: true, message: '请填写' }],
        specialSouType: [{ required: true, message: '请填写' }]
      },
      isApprovalOnly: this.$attrs.params.flag === 'approvalOnly',
      dialogVisible: false,
      displayItemTable: [],
      requirementAttaches: [],
      multipleSelection: [],
      multipleSelection2: [],
      Viewflag: '',
      ViewflagEdit: false,
      messagePage: false,
      tableLoading: false,
      operationPreOptions: {
        save: this.preSaveHandler, // 暂存
        nextStep: this.preNextStepHandler // 下一步
      },
      outerBmpFlag: false, // 是否外部链接跳转 - 控制显隐
      selectFileData: [] // 勾选的附件信息
    }
  },
  computed: {
    hideReSubmit () {
      return this.allRequirementLineList.find(v => v.applyStatus === 'RETURNING')
    },
    importAbled () {
      return (
        this.requirementHead.auditStatus === 'APPROVED' ||
        !this.requirementHead.categoryId ||
        !this.requirementHead.organizationId ||
        !this.requirementHead.orgId
      )
    },
    viewUpdateButton () {
      console.log(this.ViewflagEdit, 'ViewflagEdit')
      return (
        (this.curRole === 'BUYER' &&
          (!this.isReadOnly || this.Viewflag === 'change') &&
          (this.requirementHead.auditStatus !== 'APPROVED' ||
            (this.Viewflag === 'change' && !this.ViewflagEdit))) ||
        this.$attrs.params.flag == 'change'
      ) && this.$attrs.params.showType !== 'readOnly'
    },
    disabledUpdateButton () {
      return (
        this.requirementHead.auditStatus === 'SUBMITTED' ||
        this.requirementHead.auditStatus === 'APPROVING'
      )
    },
    viewWithDrawButton () {
      return ['SUBMITTED'].includes(this.requirementHead.auditStatus)
    },
    workflowBusinessId () {
      return this.requirementHead ? this.Viewflag !== 'change' ? this.requirementHead.requirementHeadId : this.requirementHead.souReqHead?.changeRequirementHeadId : null
    },
    workflowTabDisabled () {
      return !this.isApprovalOnly && (this.Viewflag !== 'change' || this.ViewflagEdit == false)
    },
    isReadOnly () {
      return !['add', 'edit'].includes(this.$attrs.params.flag)
    },
    businessType () {
      return this.$attrs.params.flag == 'change'
        ? 'MQL_PR_SOU_REQUIREMENT_CHANGE'
        : 'MQL_PR_SOU_REQUIREMENT_INIT'
    },
    showButtonConfig () {
      return this.$attrs.params.flag == 'change' ? { saveAndNextStep: true } : {}
    }
  },
  watch: {
    viewUpdateButton () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
      this.buttonConfigInfo.withdraw.view = this.viewWithDrawButton
    },
    disabledUpdateButton () {
      // this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
      // this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton
    }
  },
  async created () {
    this.Viewflag = this.$attrs.params.flag
    console.log(this.$store.getters, 'getters')
    if (this.Viewflag === 'approveNumber') this.workflowParamsInfo.tabDisabled = false
    if (this.$attrs.params.flag === 'add') {
      const { nickname, ceeaDeptId, department, ceeaEmpNo } = this.$store.getters.userInfo
      this.requirementHead.createdFullName = nickname
      this.requirementHead.applyByNickname = nickname
      this.$set(this.requirementHead, 'applyCode', ceeaEmpNo)
      this.$set(this.requirementHead, 'demandType', this.$attrs.params?.demandType)
      this.$http({
        url: `/api-pj/pj-anon/user/getHrUserOrgnizationByUsername?username=${this.$store.getters.userInfo.username}`,
        method: 'GET',
        loading: true
      }).then(res => {
        const data = res.data
        this.$set(
          this.requirementHead,
          'ceeaDepartmentName',
          data.departmentOrganization?.organizationName,
        )
        this.$set(
          this.requirementHead,
          'ceeaDepartmentId',
          data.departmentOrganization?.organizationId,
        )
        this.$set(this.requirementHead, 'orgName', data.ouOrganization?.organizationName)
        this.$set(this.requirementHead, 'orgId', data.ouOrganization?.organizationId)
        this.$set(this.requirementHead, 'orgCode', data.ouOrganization?.organizationCode)
        this.$set(this.requirementHead, 'orgBuName', data.buOrganization?.organizationName)
        this.$set(this.requirementHead, 'orgBuId', data.buOrganization?.organizationId)
        this.$set(this.requirementHead, 'orgBuCode', data.buOrganization?.organizationCode)
      })
    } else {
      this.getFormDetail(this.$attrs.params.row.requirementHeadId)
    }
    // this.getFormDetail(443392231387136)
    this.getButtonConfig()
    const params = {
      pageSize: 15,
      pageNum: 1,
      sceneCode: 'SCENE_EXT_REQ_SOU_INFER',
      sceneModuleCode: 'SCENE_EXT_REQ_SOU_INFER_ATTACHMENT'
    }
    sceneFileApi.listPage(params).then(res => {
      const {
        attachmentName,
        templateFileId
      } = res.data?.list?.[0]
      this.sectionPersonnelName = attachmentName
      this.sectionPersonnelId = templateFileId
    })
  },
  methods: {
    // 附件选择
    selectFileChange (data) {
      this.selectFileData = data
    },
    // 批量下载附件
    batchDownload () {
      const selectData = this.selectFileData
      if (selectData.length === 0) {
        this.$message.warning(this.$t('cusEntry.tipMessage.pleaseSelectFile'))
        return false
      }
      downloadFileLinkByPost('/api-file/bid/batchDownloadToZip', null, { fileIdList: selectData.map(item => item.fileId) }).then(res => {
        this.$message.success(this.$t('cusEntry.tipMessage.dowmloadSuccess'))
      })
    },
    handleExport () {
      downloadFileLinkByPost(
        '/api-sup-ce/requirement/vendor/exportdata',
        '推荐供应商导出文件.xlsx',
        { requirementHeadId: this.$attrs.params.row.requirementHeadId }
      ).catch(() => {
        this.$message.error('导出失败！')
      })
    },
    downLoadFile () {
      downloadFileLink(
        '/api-sup-ce/npm/pr/requirement/sou/getOrgBuInterfacePersonList',
        '板块接口人名单.xlsx',
      ).catch(() => {
        this.$message.error(this.$t('purchaseDemand.downloadFail'))
      })
    },
    clearGongshi () {
      this.requirementHead.prefixTechDiscussion = null
    },
    requireProductFileSuccess5 (file) {
      const { fileId = '', fileName = '' } = file || {}
      this.requirementHead.appointBrandFileId = fileId.toString()
      this.requirementHead.appointBrandFileName = fileName
    },
    requireProductFileSuccess6 (file) {
      const { fileId = '', fileName = '' } = file || {}
      this.requirementHead.qualifyUnitFileId = fileId.toString()
      this.requirementHead.qualifyUnitFileName = fileName
    },
    handleEmailBlur (index, row) {
      function validateEmail (email) {
        var re =
          /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
        return re.test(email)
      }
      const bool = validateEmail(row.email)
      if (!bool && row.email) {
        this.$message.warning(`第${index + 1}行邮箱格式错误，请修改`)
        return false
      } else {
        return true
      }
    },
    handlePhoneBlur (index, row, type) {
      function validatePhone (phone) {
        var re = /^1[3456789]\d{9}$/
        return re.test(phone)
      }
      const bool = validatePhone(row.phone)
      if (!bool && row.phone) {
        if (type) {
          this.$message.warning('技术负责人联系方式格式错误，请修改')
        } else {
          this.$message.warning(`第${index + 1}行联系方式格式错误，请修改`)
        }
        return false
      } else {
        return true
      }
    },
    // 筛选剩余时间
    shengyu () {
      this.requirementHead.signContractDay = parseInt(this.requirementHead?.signContractDay)
      if (!this.requirementHead?.putIntoUseDate || !this.requirementHead?.requireProductDate) { return null }
      const diff =
        new Date(this.requirementHead?.putIntoUseDate).getTime() -
        new Date(this.requirementHead?.requireProductDate).getTime()
      let shengyus = parseInt(diff / (1000 * 60 * 60 * 24))
      this.requirementHead.remainingDay = shengyus
      if (!this.requirementHead?.deliveryDay || !this.requirementHead?.signContractDay) return null
      shengyus =
        shengyus -
        parseInt(this.requirementHead?.deliveryDay) -
        parseInt(this.requirementHead?.signContractDay)
      console.log(shengyus)
      this.requirementHead.remainingDay = shengyus
    },
    // 确认选择品类
    comfirmSelect (node, scope) {
      this.requirementHead.categoryId = node ? node.categoryId : ''
      this.requirementHead.categoryName = node ? node.categoryName : ''
      this.requirementHead.categoryCode = node ? node.categoryCode : ''
    },
    getButtonConfig () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
      this.buttonConfigInfo.withdraw.view = this.viewWithDrawButton
      this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.cancel.view = true
      this.buttonConfigInfo.close.view = false
    },
    handleTypeChange (val) {
      // 不是非生产性需求清空预算编号
      if (this.requirementHead.demandType !== 'NONPRODUCTIVE_DEMAND') {
        this.requirementHead.budgetManagementNum = null
        this.requirementHead.budgetManagementId = null
        this.requirementHead.totalBudget = null
        this.requirementHead.usedBudget = null
        this.requirementHead.unusedBudget = null
      }
    },
    handleTypeChange2 (val) {
      if (this.requirementHead.requireFrom == 'SPECIAL_SOU') {
        this.requirementHead.needPublic = 'N'
      }
      // 特殊招标设置默认值为空
      if (val === 'SPECIAL_SOU') {
        this.requirementHead.techRequire = '无'
        this.requirementHead.projectOverview = '无'
        this.requirementHead.vendorQualificationRequire = '无'
        this.requirementHead.performanceRequire = '无'
      }
      this.requirementHead.specialSouType = null
    },
    // 收货地点选择
    changeSiteInfo (row, { element }) {
      this.$set(row, 'receiveContact', element ? element.receiver : '')
      this.$set(row, 'receiveTelephone', element ? element.receiverPhone : '')
      this.$set(row, 'receiveAddress', element ? element.siteName : '')
    },
    // 指定供应商绿牌  参数添加
    beforeOpenSuppier (row) {
      this.supplierParam['c.CATEGORY_ID'] = row.categoryId
    },
    // 物料明细导出
    async exportFile () {
      const Authorization = getToken() ? 'Bearer ' + getToken() : '' // token
      let menuInfo = getMenuInfo()
      const res = await axios({
        method: 'POST',
        url: `${sysPrefix()}/api-sup-ce/pr/requirementLine/excelExport?requirementHeadId=${
          this.$attrs.params.row.requirementHeadId
        }`,
        headers: {
          Authorization: Authorization,
          'X-Fun-Info': menuInfo.secretKey
        },
        responseType: 'blob'
      })
      if (!res) return
      if (res.data.type === 'application/json') {
        throw new Error(this.$t('purchaseDemand.downloadFail'))
      }
      const content = res.data
      const blob = new Blob([content])
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.style.display = 'none'
      link.href = url
      link.rel = 'noopener'
      link.setAttribute(
        'download',
        window.decodeURI(res.headers['content-disposition'].split('=')[1]),
      )
      document.body.appendChild(link)
      link.click()
      document.body.removeChild()
    },
    async getWorkflowBusinessType () {
      return this.$attrs.params.flag == 'change'
        ? 'MQL_PR_SOU_REQUIREMENT_CHANGE'
        : 'MQL_PR_SOU_REQUIREMENT_INIT'
    },
    async getWorkflowBusinessVariables () {
      return {
        Amount: this.requirementHead.totalBudget
      }
    },
    getCWorkflowRefName () {
      return 'workflowMulti'
    },
    beforeUpload () {
      const extraData = this.extraData
      const header = _pick(this.requirementHead, [
        'categoryCode',
        'categoryId',
        'categoryName',
        'ceeaAssetType',
        'ceeaProjectUserNickname',
        'ceeaDepartmentName',
        'ceeaPurchaseType',
        'orgId',
        'orgName',
        'orgCode',
        'organizationCode',
        'organizationId',
        'organizationName',
        'requirementHeadId',
        'requirementHeadNum'
      ])
      this.extraData = { ...header, ...extraData }
    },
    handleSuccess (data) {
      // 导入成功就刷新界面
      if (data.status === 'Y') {
        data.data.forEach(row => {
          this.allRequirementLineList.unshift(row)
          this.handleCurrentChange()
          this.setTotalAmount(row)
        })
      }
    },
    downloadTemplate () {
      downloadFileLink(
        '/api-sup-ce/pr/requirementLine/v2/downloadTemplate',
        this.$t('purchaseDemand.importMaterialItemModelDownload'),
      ).catch(() => {
        this.$message.error(this.$t('purchaseDemand.downloadFail'))
      })
    },
    async handleCurrentChange (val = 1) {
      this.tableLoading = true
      this.$set(this.detailPag, 'currentPage', val)
      this.$set(this.detailPag, 'pageSize', 15)

      this.$set(
        this.requirementHead,
        'requirementLineList',
        this.allRequirementLineList.slice((val - 1) * 15, val * 15),
      )
      setTimeout(() => {
        this.tableLoading = false
        this.multipleSelection2 = []
      })
      this.$forceUpdate()
    },
    async getFormDetail (requirementHeadId) {
      const query = {
        '*': {},
        souReqHead: { '*': {} },
        souGroupList: { '*': {} },
        souVendorList: { '*': {} },
        souAttachList: { '*': {} }
      }
      const searchData = transformMQL.save(
        'PrRequirementForBuyer',
        [{ requirementHeadId: requirementHeadId }],
        'getRequirementInfo',
        query,
      )
      purchaseApplicationApi.getRequirementInfo(searchData).then(datas => {
        let data = datas.data.records[0]
        this.requirementHead = { ...data, ...data.souReqHead }
        console.log(this.requirementHead, 'requirementHead')
        if (data.souReqHead.changeRequirementHeadId != null) {
          this.Viewflag = 'change'
          this.$attrs.params.flag = 'change'
          this.ViewflagEdit = true
        }
        data.souGroupList.forEach(e => {
          if (e.groupType == 'TECH') {
            this.user.fullName = e.fullName
            this.user.username = e.username
            this.user.userId = e.userId
            this.user.phone = e.phone
            this.user.workYear = e.workYear
          }
        })
        this.requireGroup = data.souVendorList
        this.requirementAttaches = data.souAttachList
        this.buttonConfigInfo.withdraw.view = this.viewWithDrawButton
      })
    },
    // 删除
    deleteOneContent (index, row) {
      let i = (this.detailPag.currentPage - 1) * 15 + index
      this.allRequirementLineList.splice(i, 1)
      this.handleCurrentChange(this.detailPag.currentPage)
      this.$forceUpdate()
      if (row.notaxPrice) {
        this.requirementHead.totalBudget = this.requirementHead.totalBudget - row.totalAmount
      }
    },
    requireGroupAdd () {
      this.requireGroup.push({
        vendorName: null,
        phone: null,
        email: null,
        recommendFrom: null
      })
    },
    addUploadOne () {
      this.requirementAttaches.push({
        attachId: null,
        fileuploadId: null,
        attachName: ''
      })
    },
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
    },
    handleItemSelection2 (val) {
      this.multipleSelection2 = val
    },
    requireProductFileSuccess (file) {
      const { fileId = '', fileName = '' } = file || {}
      this.requirementHead.requireProductFileId = fileId.toString()
      this.requirementHead.requireProductFileName = fileName
    },
    requireProductFileSuccess2 (file) {
      const { fileId = '', fileName = '' } = file || {}
      this.requirementHead.deliveryDayFileId = fileId.toString()
      this.requirementHead.deliveryDayFileName = fileName
    },
    requireProductFileSuccess3 (file) {
      const { fileId = '', fileName = '' } = file || {}
      this.requirementHead.putIntoUseDateFileId = fileId.toString()
      this.requirementHead.putIntoUseDateFileName = fileName
    },
    outerHandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '', createdBy = '', creationDate = '' } = file || {}
      row.fileId = fileId.toString()
      row.fileName = fileName
      row.createdBy = createdBy
    },
    // 行删除
    handleDelClick (index, row, type) {
      if (type == 'list') {
        // 列表删除
        if (row?.requirementVendorId) {
          this.requireGroupDel.push({ $delete: row.requirementVendorId })
        }
        this.requireGroup.splice(index, 1)
      } else {
        // 附件删除
        if (row?.requirementAttachId) {
          this.requirementAttachesDel.push({ $delete: row.requirementAttachId })
        }
        this.requirementAttaches.splice(index, 1)
      }
    },
    // 重置对象条件
    resetForm (form) {
      for (let i in form) {
        form[i] = ''
      }
    },
    async openDialog () {
      this.requirementHead.requirementLineList.push({})
    },
    queryContent (queryForm) {
      this.$http({
        url: '/api-base/material/materialItem/listMaterialByPurchaseCategory',
        method: 'POST',
        data: {
          categoryId: queryForm.categoryId || this.requirementHead.categoryId,
          materialCode: queryForm.materialCode,
          materialName: queryForm.materialName,
          organizationId: this.requirementHead.organizationId,
          organizationName: this.requirementHead.organizationName,
          ceeaPurchaseType: this.requirementHead.ceeaPurchaseType,
          pageSize: queryForm.pageSize || 15,
          pageNum: queryForm.pageNum || 1
        },
        laoding: true
      }).then(data => {
        if (data && data.data) {
          this.displayItemTable = data.data.list
          this.pageInfo.pageTotal = data.data.total
        }
      })
    },
    selectHandler (node, value, scope) {
      this.requirementHead.orgId = node ? node.organizationId : null
      this.requirementHead.orgCode = node ? node.organizationCode : null
      this.requirementHead.orgName = node ? node.organizationName : null
      // 板块
      this.$http({
        url:
          '/api-pj/pj-anon/user/getBuOrganizationByOuOrgCode?organizationCode=' +
          node?.organizationCode,
        method: 'GET',
        loading: true
      }).then(res => {
        console.log(res)
        this.requirementHead.orgBuName = res.data?.organizationName
        this.requirementHead.orgBuId = res.data?.organizationId
        this.requirementHead.orgBuCode = res.data?.organizationCode
      })
    },
    selectHandler2 (node, value, scope) {
      this.requirementHead.organizationId = node ? node.organizationId : null
      this.requirementHead.organizationCode = node ? node.organizationCode : null
      this.requirementHead.organizationName = node ? node.organizationName : null
    },
    addOneContent (multipleSelection) {
      if (multipleSelection.length === 0) {
        return
      }

      const obj = {}
      this.$refs.materialDetailRef.columns.forEach(headers => {
        // 物料明细表所有属性置空初始化
        obj[headers.property] = null
      })

      const dataMap = multipleSelection.map(item => {
        const selectItem = {
          ...item,
          unit: item.unitName,
          unitCode: item.unit,
          totalAmount: (item.notaxPrice || 0) * (item.requirementQuantity || 0),
          ceeaIe: item.ceeaIfDirectory === 'Y' ? 'true' : 'false',
          businessSmall: this.requirementHead.businessSmall,
          // ceeaBusinessSmallCode: this.requirementHead.ceeaBusinessSmallCode
          requirementQuantity: undefined,
          orgId: this.requirementHead.orgId,
          orgCode: this.requirementHead.orgCode,
          orgName: this.requirementHead.orgName,
          organizationId: this.requirementHead.organizationId,
          organizationCode: this.requirementHead.organizationCode,
          organizationName: this.requirementHead.organizationName
        }
        return selectItem
      })
      // 拿到最终的添加数据数组
      const newArr = dataMap.map(row => {
        const o = JSON.parse(JSON.stringify(obj))
        Object.keys(row).forEach(key => {
          this.$set(o, key, row[key])
        })
        return o
      })

      this.allRequirementLineList.unshift(...newArr)
      this.handleCurrentChange()
      this.dialogVisible = false
    },
    checkMaterialList (categoryId) {
      return new Promise(resolve => {
        if (
          this.allRequirementLineList.length &&
          this.lastCategoryId &&
          this.lastCategoryId !== categoryId
        ) {
          this.$confirm(
            this.$t('purchaseDemand.checkMaterialListConfirm'),
            this.$t('common.tips'),
            {
              confirmButtonText: this.$t('common.confirm'),
              cancelButtonText: this.$t('common.cancel'),
              type: 'warning'
            },
          )
            .then(() => {
              this.allRequirementLineList = []
              this.handleCurrentChange()
              this.$message.info(this.$t('purchaseDemand.checkMaterialListTips1'))
              resolve(true)
            })
            .catch(() => {
              this.$message.info(this.$t('purchaseDemand.checkMaterialListTips2'))
              resolve(false)
            })
        } else {
          resolve(true)
        }
      })
    },
    // 预算编号快查关闭回写值
    getBudgetNumObj (val) {
      this.requirementHead.budgetManagementNum = val ? val.budgetManagementNumber : ''
      this.requirementHead.budgetManagementId = val ? val.budgetManagementId : ''
    },
    async getCategoryObj (val, scope) {
      const flag = await this.checkMaterialList(val ? val.categoryId : '')
      if (flag) {
        this.requirementHead.categoryId = val ? val.categoryId : ''
        this.requirementHead.categoryCode = val ? val.categoryCode : ''
        this.requirementHead.categoryName = val ? val.categoryName : ''
        this.lastCategoryId = val ? val.categoryId : ''
      } else {
        const { categoryId, categoryName, categoryCode } = this.requirementHead
        this.$refs.category.setValue({
          categoryId,
          categoryName,
          categoryCode
        })
      }
    },
    getTotalAmount (n) {
      // 存起来一个初始需求数量
      if (this.copyInit.bol) {
        this.copyInit.num = +n
        this.copyInit.bol = false
      }
    },
    async setTotalAmount (row, index) {
      const formFiled = await this.formValidate('requirementHeadRef')
      const fieldKeys = Object.keys(formFiled.obj)
      if (!formFiled.flag && fieldKeys.length > 0 && fieldKeys[0].includes('requirementLineList')) {
        this.messagePage = true
      } else {
        this.messagePage = false
      }

      if (row.applyStatus === 'RETURNING' && row.requirementQuantity >= this.copyInit.num) {
        row.requirementQuantity = this.copyInit.num
        return this.$message.warning(this.$t('purchaseDemand.mustBeLessEqual'))
      }
      if (row.requirementQuantity <= 0) {
        return this.$message.warning(this.$t('purchaseDemand.setTotalAmountTips1'))
      }
      row.totalAmount = Number(
        Number(row.notaxPrice || 0) * Number(row.requirementQuantity || 0),
      ).toFixed(2)
      console.log(row.totalAmount, 'row.totalAmount')
      setTimeout(() => {
        const totalAmountArr = this.allRequirementLineList.map(v => v.totalAmount || 0)
        const totalBudget = totalAmountArr.reduce((p, c) => (Number(p) || 0) + (Number(c) || 0))
        this.$set(this.requirementHead, 'totalBudget', totalBudget)
        this.requirementHead.nextYearBudgetAmount =
          this.requirementHead.totalBudget - this.requirementHead.thisYearBudgetAmount
      }, 100)
    },
    getUserObj (val, scope) {
      console.log(val, 'val')
      this.user.username = val?.username
      this.user.fullName = val?.nickname
      this.user.userId = val?.userId
      this.user.phone = val?.phone
    },
    getPlanNo (val, scope) {
      console.log(val)
      this.$set(this.requirementHead, 'planNo', val?.planNo)
      this.$set(this.requirementHead, 'projectPlanId', val?.projectPlanId)
    },
    setBudget (val) {
      if (
        Number(this.requirementHead.thisYearBudgetAmount) > Number(this.requirementHead.totalBudget)
      ) {
        return this.$message.error(this.$t('purchaseDemand.setBudgetTips1'))
      }
      setTimeout(() => {
        this.requirementHead.nextYearBudgetAmount =
          this.requirementHead.totalBudget - this.requirementHead.thisYearBudgetAmount
      }, 100)
    },
    formatterStatus (row, column, cellValue, index) {
      return this.$getDictLabel('APPLICATION_STATUS', cellValue)
    },
    formatterIfDirector (row, column, cellValue, index) {
      return cellValue === 'Y' ? this.$t('common.yes') : this.$t('common.no')
    },
    // 提交校验
    async submitCheck () {
      if (this.allRequirementLineList.length === 0) {
        return this.__jump_error__(
          'materialDetailRef',
          null,
          this.$t('purchaseDemand.saveBillTips5'),
        )
      }

      if (this.requirementHead.ceeaPurchaseType === 'APPOINT') {
        let confirmSelectValue = await this.$confirm(
          // 此申请为指定采购，确认则提交采购申请单！
          this.$t('purchaseDemand.saveBillConfirm1'),
          this.$t('common.tips'),
          {
            confirmButtonText: this.$t('common.confirm'),
            cancelButtonText: this.$t('common.cancel'),
            type: 'warning'
          },
        )
        if (confirmSelectValue !== 'confirm') return false
      }

      let isConfirm = await this.$confirm(
        this.$t('purchaseDemand.clickConfirmToApproval'), // 点击确认则跳转到审批
        this.$t('common.tips'),
        {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        },
      )
      return isConfirm === 'confirm'
    },
    async submitEvent (allparam) {
      // const sign = await this.submitCheck()
      // if (!sign) return
      if (this.Viewflag !== 'change') {
        const saveData = transformMQL.save('PrRequirementForBuyer', [allparam], 'submitRequirement')
        purchaseApplicationApi.submitZhaobiao(saveData).then(datas => {
          console.log(datas)
          this.$message({
            message: '保存成功',
            type: 'success'
          })
          const requirementHeadId = datas.data.records[0].requirementHeadId
          this.requirementHead.requirementHeadId = requirementHeadId
          this.handlerAfter('SUBMIT')
        })
      } else {
        allparam.tempSave = false
        this.$refs.requirementHeadRef.validate(valid => {
          if (valid) {
            const saveData = transformMQL.save(
              'PrSouRequirementPoolForBuyer',
              [allparam],
              'changeSouPlan',
            )
            purchaseApplicationApi.changeZhaobiao(saveData).then(datas => {
              this.$message({
                message: '保存成功',
                type: 'success'
              })
              console.log(datas, 'dataschange')
              const requirementHeadId = datas.data.records[0].requirementHeadId
              this.$set(this.requirementHead, 'requirementHeadId', requirementHeadId)
              this.handlerAfter('SUBMIT')
            })
          } else {
            this.$message({
              type: 'warning',
              message: '请填写必填项'
            })
            return false
          }
        })
      }
    },
    saveBill (allparam) {
      if (this.Viewflag !== 'change') {
        const saveData = transformMQL.save(
          'PrRequirementForBuyer',
          [allparam],
          'tempSaveRequirement',
        )
        purchaseApplicationApi.saveZhaobiao(saveData).then(datas => {
          this.$message({
            message: '保存成功',
            type: 'success'
          })
          const requirementHeadId = datas.data.records[0].requirementHeadId
          this.getFormDetail(requirementHeadId)
        })
      } else {
        allparam.tempSave = true
        const saveData = transformMQL.save(
          'PrSouRequirementPoolForBuyer',
          [allparam],
          'changeSouPlan',
        )
        purchaseApplicationApi.changeZhaobiao(saveData).then(datas => {
          this.$message({
            message: '保存成功',
            type: 'success'
          })
          const requirementHeadId = datas.data.records[0].requirementHeadId
          this.requirementHead.requirementHeadId = requirementHeadId
        })
      }
    },
    // form验证返回promise校验返回trun or false
    formValidate (formRef) {
      return new Promise(resolve => {
        this.$refs[formRef].validate((flag, obj) => {
          resolve({ flag, obj })
        })
      })
    },
    /*
     * @Description: 校验表单表格必填项
     * @return: {
     *   flag: true/false,  校验是否通过
     *   message: 返回填写信息
     * }
     */
    async getCheckForm () {
      const formFiled = await this.formValidate('requirementHeadRef')

      if (!formFiled.flag && Object.keys(formFiled.obj).length > 0) {
        const warnObj = Object.keys(formFiled.obj)[0]
        return {
          flag: formFiled.flag,
          message: formFiled.obj[warnObj][0].message
        }
      }
      return { flag: true }
    },
    // 抽离保存，提交方法校验及其他数据处理
    saveOtherDeal () {
      if (this.requirementHead.ceeaPrType === '01' && this.requirementAttaches.length === 0) {
        this.$message.error(this.$t('purchaseDemand.saveBillTips3'))
        return false
      }
      if (
        Number(this.requirementHead.thisYearBudgetAmount) > Number(this.requirementHead.totalBudget)
      ) {
        this.$message.error(this.$t('purchaseDemand.saveBillTips4'))
        return false
      }
      this.requirementHead.nextYearBudgetAmount =
        this.requirementHead.totalBudget - this.requirementHead.thisYearBudgetAmount

      this.allRequirementLineList.map(v => {
        v.orgId = this.requirementHead.orgId
        v.orgCode = this.requirementHead.orgCode
        v.orgName = this.requirementHead.orgName
      })
      return true
    },
    initParams () {
      const tech = [
        {
          fullName: this.user.fullName,
          username: this.user.username,
          userId: this.user.userId,
          phone: this.user.phone,
          workYear: this.user.workYear,
          groupType: 'TECH'
        }
      ]
      const allparam = {
        ...this.requirementHead,
        sourceFromType: 'HAND_MAKE',
        souReqHead: this.requirementHead,
        souGroupList: tech, // 技术负责人
        souVendorList: this.requireGroup, // 推荐供应商
        souAttachList: this.requirementAttaches // 附件
      }
      allparam.souAttachList = allparam.souAttachList.concat(this.requirementAttachesDel)
      allparam.souVendorList = allparam.souVendorList.concat(this.requireGroupDel)
      return allparam
    },
    preSaveHandler () { // 暂存workYear
      return this.saveOtherDeal()
    },
    async preNextStepHandler () { // 下一步
      if (!this.saveOtherDeal()) return false
      if (this.requireGroup && this.requireGroup.length) {
        let bol = false
        this.requireGroup.forEach((data, index) => {
          const email = this.handleEmailBlur(index, data)
          if (!email) { bol = true; return false }
          const phone = this.handlePhoneBlur(index, data)
          if (!phone) {
            bol = true; return false
          }
          const userPhone = this.handlePhoneBlur(0, { phone: this.user.phone }, 'userPhone')
          if (!userPhone) {
            bol = true; return false
          }
        })
        if (bol) {
          return false
        }
      }
      let validFlag
      await this.$refs.requirementHeadRef.validate(valid => {
        validFlag = valid
      })
      if (!validFlag) {
        this.$message({
          type: 'warning',
          message: this.$t('vendorMod.warningRequire')
        })
        return false
      }
      const phone = this.user.phone
      const fullName = this.user.fullName
      const workYear = this.user.workYear
      if (!phone || !fullName || phone == '' || fullName == '' || !workYear || workYear == '') {
        this.$message.error('请输入技术负责人信息')
        return false
      }

      if (workYear < 3) {
        this.$message.error('技术负责人的工作年限需要大于等于3年')
        return false
      }
      let allparam = this.initParams()
      let datas, saveData
      if (this.Viewflag !== 'change') {
        saveData = transformMQL.save('PrRequirementForBuyer', [allparam], 'submitRequirement')
        datas = await purchaseApplicationApi.submitZhaobiao(saveData)
        if (datas && datas.data.records.length) {
          this.$message({
            message: '保存成功',
            type: 'success'
          })
          const requirementHeadId = datas.data.records[0].requirementHeadId
          this.requirementHead.requirementHeadId = requirementHeadId
        } else {
          return false
        }
      } else {
        allparam.tempSave = false
        let validHeadForm
        await this.$refs.requirementHeadRef.validate(valid => {
          validHeadForm = valid
        })
        if (!validHeadForm) {
          this.$message({
            type: 'warning',
            message: '请填写必填项'
          })
          return false
        }
        saveData = transformMQL.save(
          'PrSouRequirementPoolForBuyer',
          [allparam],
          'changeSouPlan',
        )
        datas = await purchaseApplicationApi.changeZhaobiao(saveData)
        if (datas && datas.data.records.length) {
          this.$message({
            message: '保存成功',
            type: 'success'
          })
          const requirementHeadId = datas.data.records[0].requirementHeadId
          this.$set(this.requirementHead, 'requirementHeadId', requirementHeadId)
          this.$set(this.requirementHead, 'souReqHead', datas.data.records[0].souReqHead)
        } else {
          return false
        }
      }
      return true
    },
    approvalHanlder (type) {
      switch (type) {
      case 'save':
        this.saveBill(this.initParams())
        break
      case 'submit':
        this.$emit('tab-remove', this.$attrs.params.tabName)
        this.__setTabTodo('PurchaseApplicationList.getQuerydata')
        break
      case 'abandon':
        this.$emit('tab-remove', this.$attrs.params.tabName)
        this.__setTabTodo('PurchaseApplicationList.getQuerydata')
        break
      case 'recall':
        this.getFormDetail(this.workflowBusinessId)
        break
      case 'pass':
        this.getFormDetail(this.workflowBusinessId)
        break
      default:
        break
      }
    },
    async saveOrSubmitBill (type) {
      // const { flag, message } = await this.getCheckForm()
      // if (flag) {

      await this.saveOtherDeal()
      const tech = [
        {
          fullName: this.user.fullName,
          username: this.user.username,
          userId: this.user.userId,
          phone: this.user.phone,
          workYear: this.user.workYear,
          groupType: 'TECH'
        }
      ]
      const allparam = {
        ...this.requirementHead,
        sourceFromType: 'HAND_MAKE',
        souReqHead: this.requirementHead,
        souGroupList: tech, // 技术负责人
        souVendorList: this.requireGroup, // 推荐供应商
        souAttachList: this.requirementAttaches // 附件
      }
      allparam.souAttachList = allparam.souAttachList.concat(this.requirementAttachesDel)
      allparam.souVendorList = allparam.souVendorList.concat(this.requireGroupDel)
      if (type == 'preRejectedData') {
        allparam.auditStatus = 'APPROVED'
        const saveData = transformMQL.save('PrRequirementForBuyer', [allparam], 'submitRequirement')
        purchaseApplicationApi.submitZhaobiao(saveData).then(datas => {
          console.log(datas)
          this.$message({
            message: '保存成功',
            type: 'success'
          })
          const requirementHeadId = datas.data.records[0].requirementHeadId
          this.requirementHead.requirementHeadId = requirementHeadId
          this.handlerAfter('SUBMIT')
        })
        return false
      }
      if (type === 'SUBMIT') {
        let bol = false
        this.requireGroup?.forEach((data, index) => {
          const email = this.handleEmailBlur(index, data)
          if (!email) { bol = true; return false }
          const phone = this.handlePhoneBlur(index, data)
          if (!phone) {
            bol = true; return false
          }
          const userPhone = this.handlePhoneBlur(0, { phone: this.user.phone }, 'userPhone')
          if (!userPhone) {
            bol = true; return false
          }
        })
        if (bol) {
          return false
        }

        this.$refs.requirementHeadRef.validate(valid => {
          if (valid) {
            const phone = this.user.phone
            const fullName = this.user.fullName
            const workYear = this.user.workYear
            if (!phone || !fullName || phone == '' || fullName == '' || !workYear || workYear == '') {
              this.$message.error('请输入技术负责人信息')
              return false
            }

            if (workYear < 3) {
              this.$message.error('技术负责人的工作年限需要大于等于3年')
              return false
            }
            this.submitEvent(allparam)
          } else {
            this.$message({
              type: 'warning',
              message: this.$t('vendorMod.warningRequire')
            })
            return false
          }
        })
      } else {
        this.saveBill(allparam)
      }
      // } else {
      //   this.__focus_error__(message)
      // }
    },
    back () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('purchaseApplicationList.getQuerydata')
    }
  }
}
</script>
<style scoped lang="scss">
.el-input-number--small {
  width: 100%;
}
.list-page-query :deep(.el-form-item__label) {
  text-align: right !important;
}
.the-purchaseApplicationDetail-detail {
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .input-with-select .el-input-group__prepend {
    background-color: #fff;
  }
  .isDisabledimport {
    pointer-events: none;
    opacity: 0.5;
  }
  .the_btn_wrapper {
    display: inline-block;
    width: 111px;
  }
  .btn_line {
    display: flex;
    margin: 0 0 8px 0;
  }
  .el-tooltip :deep(.el-button) {
    min-width: 56px;
    font-size: 14px;
    border-radius: 2px;
    padding: 8px 16px;
  }
  .topComment {
    margin-top: 15px;
    text-align: right;
  }
  .input-number-precision {
    width: 100%;
    :deep(.el-input__inner) {
      text-align: left;
      padding-left: 8px;
    }
  }
}
:deep(.el-table td.el-table__cell .el-form-item__content) {
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}
.toRequired {
  color: #ff4949;
  padding-right: 2px;
}
.enable4MChangeDetail {
  position: absolute;
  top: 5px;
  left: 89px;
}
:deep(.el-form--label-top .el-form-item .el-form-item__label) {
  display: flex;
}
</style>
