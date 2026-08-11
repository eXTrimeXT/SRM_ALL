<template>
  <el-container class="flex-container blackEdit" direction="vertical">
    <el-main>
      <CWorkflowMulti
        ref="workflowMulti"
        v-model="activeTabName"
        :fun-params="workflowParamsInfo"
        :button-config-info="buttonConfigInfo"
        :button-custom="buttonCustom"
        :showTopBtn="curOpt != 'view'"
        @tab-click="workflowView"
        @workflow-handler="workflowHandler"
        @click-handler="type => saveBill(type)"
        @submit-direct="type => saveBill(type)"
        @confirm="(type, comment) => saveBill(type, comment)"
        @close-tab="back"
      >
<!--      <ApprovalProcess-->
<!--        :business-id="workflowBusinessId"-->
<!--        business-type="bidDataSubmit"-->
<!--        :approval-status="form.status"-->
<!--        :status-map="statusMap"-->
<!--        :readonly="$attrs.params.flag === 'view'"-->
<!--        :operation-pre-options="operationPreOptions"-->
<!--        @approval-handler-callback="approvalHandlerCallback"-->
<!--      >-->
        <div class="form-container1">
          <el-collapse v-model="activeLine">
            <el-collapse-item :title="$t('common.baseInfo')" name="4">
              <el-form ref="blackForm" :model="form" :rules="rules" :disabled="readOnly">
                <srm-row>
                  <srm-col v-if="!approvalFlag">
                    <el-form-item prop="requirementHeadNum" :label="$t('contractMod.applicationOrderNum')">
                      <QuickSearch
                        :show-input="form.requirementHeadNum"
                        show-key="nickname"
                        :disabled="readOnly"
                        :scope-data="form"
                        name="pr_requirement_head"
                        @close-quicksearch="getPlanNo"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 板块 -->
                    <el-form-item prop="orgBuName" :label="$t('cusEntry.bidSuperviseReport.extOrgBuName')">
                      <el-input v-model="form.orgBuName" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 公司 -->
                    <el-form-item prop="orgName" :label="$t('cusEntry.orderMod.companyName')">
                      <el-input v-model="form.orgName" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 需求部门 -->
                    <el-form-item prop="ceeaDepartmentName" :label="$t('purchaseDemand.requirementDepartment')">
                      <el-input v-model="form.ceeaDepartmentName" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="!approvalFlag">
                    <!-- 单据状态 -->
                    <el-form-item prop="status" :label="$t('purchaseDemand.applyStatus')">
                      <DictSelect
                        v-model="form.status"
                        code="BID_DATA_SUBMIT_STATUS"
                        :disabled="true"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item prop="createdFullName" :label="$t('purchaseDemand.createdBy1')">
                      <el-input v-model="form.createdFullName" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="!approvalFlag">
                    <el-form-item prop="creationDate" :label="$t('common.creationDate')">
                      <el-date-picker
                        v-model="form.creationDate"
                        type="date"
                        disabled
                        :format="$formatDatePickerTime"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="!approvalFlag">
                    <el-form-item prop="lastUpdateDate" :label="$t('contractMod.lastUpdateDate')">
                      <el-date-picker
                        v-model="form.creationDate"
                        type="date"
                        disabled
                        :format="$formatDatePickerTime"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="!approvalFlag">
                    <!-- 需求人 -->
                    <el-form-item prop="reqUserName" :label="$t('cusEntry.supplement20250121.reqUserName')">
                      <el-input v-model="form.reqUserName" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 招标专家 -->
                    <el-form-item prop="souPersonName" :label="$t('cusEntry.bidSuperviseReport.souPrincipal')">
                      <el-input v-model="form.souPersonName" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="form.status==='ABANDON'" :init-col="1">
                    <!-- 废弃说明 -->
                    <el-form-item prop="reasonDesc" :label="$t('cusEntry.supplement20250121.reasonDesc')">
                      <el-input v-model="form.reasonDesc" disabled />
                    </el-form-item>
                  </srm-col>
                </srm-row>
              </el-form>
            </el-collapse-item>
            <!-- 计划信息 -->
            <el-collapse-item :title="$t('cusEntry.supplement20250121.planInfo')" name="4">
              <el-form ref="blackForm2" :model="form" :rules="rules">
                <srm-row>
                  <srm-col v-if="!approvalFlag">
                    <!-- 资料递交单号 -->
                    <el-form-item prop="dataSubmitNo" :label="$t('cusEntry.supplement20250121.dataSubmitNo')">
                      <el-input v-model="form.dataSubmitNo" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item prop="projectName" :label="$t('bidMod.bidingName')">
                      <el-input v-model="form.projectName" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 需求来源 -->
                    <el-form-item prop="sourceFrom" :label="$t('cusEntry.supplement20250121.sourceFrom')">
                      <dict-select
                        v-model="form.sourceFrom"
                        disabled
                        code="PR_SOU_REQUIREMENT_FROM"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 预算（卢布） -->
                    <el-form-item prop="totalBudget" :label="$t('cusEntry.bidMod.budget')">
                      <el-input v-model="form.totalBudget" :disabled="readOnly" />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item prop="categoryName" :label="$t('common.category')">
                      <el-input v-model="form.categoryName" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 规模数量 -->
                    <el-form-item prop="requireQuantity" :label="$t('cusEntry.bidMod.scaleQuantity')">
                      <el-input v-model="form.requireQuantity" disabled />
                    </el-form-item>
                  </srm-col>
                  <!-- <srm-col>
                    <el-form-item prop="investNo" label="投资编号">
                      <el-input v-model="form.investNo" :disabled="readOnly" />
                    </el-form-item>
                  </srm-col> -->
                  <srm-col>
                    <!-- 招标流程 -->
                    <el-form-item prop="bidFlow" :label="$t('cusEntry.biddingSettings.bidProcess')">
                      <DictSelect
                        v-model="form.bidFlow"
                        code="SOU_BID_PROCCESS2"
                        :disabled="readOnly"
                        @change-value="(value, dictItem) => bidFlowChange(value, dictItem)"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="!['JINGJIA', 'INQUIRY'].includes(form.bidFlow)">
                    <!-- 评标委员会主席 -->
                    <el-form-item prop="bidEvalLeaderName" :label="$t('cusEntry.bidSuperviseReport.leaderPrincipal')">
                      <template #label="{ column, $index }">
                        <div data-property="attributeValue" class="required-tag">
                          <span>{{ $t('cusEntry.bidSuperviseReport.leaderPrincipal') }}</span>
                          <!-- 预算金额≤1000卢布，由部长级以上人员担任（含与之对应的技术管理和项目管理职务）；预算金额＞1000卢布，由高层领导（含与之对应的技术管理和项目管理职务）担任 -->
                          <el-tooltip
                            effect="dark"
                            :content="$t('cusEntry.supplement20250121.promptTips2')"
                            placement="top"
                            class="table-column-tooltip"
                          >
                            <em class="el-icon-warning tip-icon" />
                          </el-tooltip>
                        </div>
                      </template>
                      <QuickSearch
                        :show-input="form.bidEvalLeaderName"
                        :disabled="readOnly"
                        show-key="nickname"
                        name="scc_npm_sou_expert_senior"
                        @close-quicksearch="getCompanyObj2"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 技术专家 -->
                    <el-form-item prop="techPrincipal" :label="$t('cusEntry.bidSuperviseReport.extTechPrincipal')">
                      <QuickSearch
                        :show-input="form.techPrincipal"
                        :disabled="readOnly"
                        show-key="nickname"
                        name="scc_npm_sou_expert"
                        @close-quicksearch="getCompanyObj3"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item prop="phone" :label="$t('vendorMod.contactMethod')">
                      <el-input v-model="form.phone" :disabled="readOnly" />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 工作年限 -->
                    <el-form-item
                      prop="workYears"
                      :label="$t('cusEntry.supplement20250121.workYears')"
                    >
                      <template #label="{ column, $index }">
                        <div data-property="attributeValue" class="required-tag">
                          <span>{{ $t('cusEntry.supplement20250121.workYears') }}</span>
                          <!-- 技术专家工作年限应≥3年；技术专家不允许参与评标。 -->
                          <el-tooltip
                            effect="dark"
                            :content="$t('cusEntry.supplement20250121.promptTips3')"
                            placement="top"
                            class="table-column-tooltip"
                          >
                            <em class="el-icon-warning tip-icon" />
                          </el-tooltip>
                        </div>
                      </template>
                      <el-input v-model="form.workYears" :disabled="readOnly" />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="form.bidFlow == 'COMPETE'">
                    <!-- 竞争性谈判签批附件 -->
                    <el-form-item
                      prop="competeFileId"
                      :label="$t('cusEntry.supplement20250121.competeFileName')"
                      :rules="{
                        required: true,
                        message: $t('bidMod.msgUpload1'),
                      }"
                    >
                      <SrmCommonFile
                        :default-file="{
                          fileId: form.competeFileId,
                          fileName: form.competeFileName
                        }"
                        :readonly="readOnly"
                        @on-change="({file}) => outerHandleUploadSuccess2(file)"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="form.bidFlow == 'COMPETE'">
                    <!-- 评标副组长 -->
                    <el-form-item
                      prop="bidEvalDeputyLeaderName"
                      :label="$t('cusEntry.supplement20250121.bidEvalDeputyLeaderName')"
                      :rules="{
                        required: true,
                        message: $t('contractMod.pleaseFillIn'),
                      }"
                    >
                      <QuickSearch
                        :show-input="form.bidEvalDeputyLeaderName"
                        :disabled="readOnly"
                        show-key="nickname"
                        name="scc_rbac_user_display"
                        @close-quicksearch="getCompanyObj"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="1">
                    <!-- 合同签订单位 -->
                    <el-form-item
                      prop="contractSignUnit"
                      :label="$t('cusEntry.supplement20250121.contractSignUnit')"
                    >
                      <QuickSearch
                        :disabled="readOnly"
                        :show-input="form.contractSignUnit"
                        :scope-data="form"
                        name="scc_base_organization_ou"
                        multiSelect
                        @close-quicksearch="getCompany"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="1">
                    <!-- 未提报月度计划原因 -->
                    <el-form-item
                      prop="notMonthlyPlanReason"
                      :label="$t('cusEntry.supplement20250121.notMonthlyPlanReason')"
                    >
                      <el-input
                        v-model="form.notMonthlyPlanReason"
                        type="textarea"
                        :rows="2"
                        disabled
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="1">
                    <el-form-item prop="remark" :label="$t('common.remark')">
                      <el-input
                        v-model="form.remark"
                        :disabled="readOnly"
                        type="textarea"
                        maxlength="3000"
                        :rows="2"
                      />
                    </el-form-item>
                  </srm-col>
                </srm-row>
              </el-form>
            </el-collapse-item>
            <!-- 招标评审信息 -->
            <el-collapse-item
              v-if="form.bidFlow !== 'JINGJIA' && form.bidFlow !== 'INQUIRY'"
              :title="$t('cusEntry.supplement20250121.bidReviewInfo')"
              name="4"
            >
              <el-form ref="blackForm3" :model="form" :rules="rules">
                <srm-row>
                  <!-- 是否指定评标人 -->
                  <!-- <srm-col :initCol="1">
                    <el-form-item prop="isAppointEvaluator" :label="$t('cusEntry.bidMod.bidderOrNot')">
                      <el-switch
                        v-model="form.isAppointEvaluator"
                        disabled
                        active-color="#13ce66"
                        active-value="Y"
                        inactive-value="N"
                      />
                    </el-form-item>
                  </srm-col> -->
                  <srm-col :initCol="3">
                    <!-- 查看专家评标标准 -->
                    <el-form-item prop="blackCode" :label="$t('cusEntry.supplement20250121.blackCode')">
                      <el-tooltip effect="dark" placement="top" class="table-column-tooltip">
                        <div slot="content">
                          <span>{{ $t('cusEntry.supplement20250121.blackCode') + ': ' }}</span><br>
                          <span>{{ $t('cusEntry.supplement20250121.promptTips4') }}</span><br>
                          <span>{{ $t('cusEntry.supplement20250121.promptTips5') }}</span><br>
                        </div>
                        <em class="el-icon-warning tip-icon" />
                      </el-tooltip>
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="3">
                    <!-- 评标总人数 -->
                    <el-form-item
                      prop="bidEvaluatorNum"
                      :label="$t('cusEntry.bidMod.extBidEvaluatorNum')"
                    >
                      <el-input-number
                        v-model="form.bidEvaluatorNum"
                        :precision="0"
                        :min="1"
                        disabled
                        :controls="false"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="3">
                    <!-- 要求高级专家最少人数 -->
                    <el-form-item
                      prop="askSeniorExpertNum"
                      :label="$t('cusEntry.supplement20250121.askSeniorExpertNum')"
                    >
                      <el-input-number
                        v-model="form.askSeniorExpertNum"
                        :precision="0"
                        :max="form.bidEvaluatorNum"
                        :controls="false"
                        disabled
                      />
                    </el-form-item>
                  </srm-col>
                  <!-- <srm-col :init-col="1">
                    <el-form-item
                      v-if="form.isAppointEvaluator == 'Y'"
                      prop="appointEvaluatorReason"
                      label="指定评标委员会的原因"
                      :rules="{
                        required: true,
                        message: '请填写',
                      }"
                    >
                      <el-input
                        v-model="form.appointEvaluatorReason"
                        type="textarea"
                        :disabled="readOnly"
                      />
                    </el-form-item>
                  </srm-col> -->
                </srm-row>
              </el-form>

              <p v-if="form.isAppointEvaluator == 'Y'" class="btn_line">
                <!-- 手工指定 -->
                <el-button
                  :disabled="readOnly"
                  type="primary"
                  class="detail-pbtn"
                  @click="addUploadOne2"
                >
                  {{ $t('cusEntry.supplement20250121.manualSpecify') }}
                </el-button>
                <!-- <el-button
                  :disabled="readOnly"
                  type="primary"
                  class="detail-pbtn"
                  @click="addUploadOne3"
                >
                  专家库指定
                </el-button> -->
                <!-- <QuickSearch
                  style="margin-left: 10px"
                  :disabled="readOnly"
                  :pre-query-data="{ 't.CATEGORY_ID': form.categoryId }"
                  show-key="username"
                  name="scc_npm_sou_expert"
                  :multiSelect="true"
                  :showButton="true"
                  btnTitle="专家库指定"
                  @close-quicksearch="addUploadOne3"
                /> -->
              </p>
              <el-table
                v-if="form.isAppointEvaluator == 'Y'"
                :data="submitEvaluatorList"
                style="width: 100%"
                border
                max-height="250px"
              >
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('purSettlementMod.tabindex')"
                  width="50"
                />
                <el-table-column align="center" prop="evaluatorRole" :label="$t('meeting.role')">
                  <template slot="header">
                    <em class="toRequired">*</em>{{ $t('meeting.role') }}
                  </template>
                  <template v-slot="scope">
                    <DictSelect
                      v-model="scope.row.evaluatorRole"
                      code="SOU_GROUP_ROLE2"
                      disabled
                    />
                  </template>
                </el-table-column>
                <el-table-column align="center" prop="ceeaEmpNo" :label="$t('purchaseDemand.erpNum')">
                  <template slot-scope="scope">
                    <QuickSearch
                      :disabled="readOnly || ['LEADER', 'FUZUZHANG'].includes(scope.row.evaluatorRole)"
                      :show-input="scope.row.ceeaEmpNo"
                      show-key="ceeaEmpNo"
                      :scope-data="scope"
                      name="scc_npm_sou_expert"
                      @close-quicksearch="getUserObj"
                    />
                  </template>
                </el-table-column>
                <el-table-column align="center" prop="evaluatorName" :label="$t('meeting.fullName')" />
                <el-table-column align="center" prop="phone" :label="$t('meeting.mobileNo')" />
                <el-table-column align="center" prop="email" :label="$t('components.approvalHead.headers.email')" />
                <el-table-column align="center" prop="extPosition" :label="$t('components.orgPositionSel.position')" />
                <!-- <el-table-column align="center" prop="workYears" label="工作年限">
                  <template slot="header">
                    <em class="toRequired">*</em>工作年限
                  </template>
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.workYears"
                      :disabled="readOnly"
                    />
                  </template>
                </el-table-column> -->
                <!-- 专家等级 -->
                <el-table-column align="center" prop="expertLevel" :label="$t('cusEntry.bidMod.expertLevel')">
                  <template slot="header">
                    <em class="toRequired">*</em>{{ $t('cusEntry.bidMod.expertLevel') }}
                  </template>
                  <template slot-scope="scope">
                    <DictSelect
                      v-model="scope.row.expertLevel"
                      code="EXT_SOU_EXPERT_LEVEL"
                      :disabled="readOnly || ['LEADER', 'FUZUZHANG'].includes(scope.row.evaluatorRole)"
                    />
                  </template>
                </el-table-column>
                <!-- <el-table-column :label="$t('vendorMod.post')" prop="extPosition" align="center">
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.extPosition"
                      :disabled="readOnly || ['LEADER', 'FUZUZHANG'].includes(scope.row.evaluatorRole)"
                    />
                  </template>
                </el-table-column> -->
                <el-table-column :label="$t('common.operation')" width="60">
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      :disabled="readOnly || ['LEADER', 'FUZUZHANG'].includes(scope.row.evaluatorRole)"
                      @click="handleDelClick2(scope.$index, scope.row)"
                    >
                      {{ $t('common.delete') }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <!--附件上传-->
            <el-collapse-item :title="$t('purSettlementMod.addUploadFile')" name="3">
              <p class="btn_line">
                <el-button
                  :disabled="readOnly"
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
              </p>
              <el-table
                :data="fileUploads"
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
                <el-table-column
                  align="center"
                  prop="attachmentType"
                  :label="$t('components.fileupload.type')"
                  show-overflow-tooltip
                >
                  <!-- <template slot-scope="scope">
                    <DictSelect
                      v-model="scope.row.attachmentType"
                      :disabled="readOnly || form.bidFlow == 'JINGJIA'"
                      code="BID_DATA_SUBMIT_FILE_TYPE"
                    />
                  </template> -->
                </el-table-column>
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
                        fileId: scope.row.fileuploadId,
                        fileName: scope.row.fileName
                      }"
                      :readonly="readOnly"
                      @on-change="({file}) => outerHandleUploadSuccess(file,scope.row)"
                    />
                  </template>
                </el-table-column>
                <!-- 上传人 -->
                <!-- <el-table-column
                  align="center"
                  prop="createdBy"
                  :label="$t('purchaseDemand.attachmentCreatedBy')"
                  :show-overflow-tooltip="false"
                /> -->
                <!-- 备注 -->
                <el-table-column align="center" prop="remark" :label="$t('common.remark')">
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.remark" :disabled="readOnly" />
                  </template>
                </el-table-column>
                <!-- <el-table-column :label="$t('common.operation')" width="60">
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      :disabled="readOnly"
                      @click="handleDelClick(scope.$index, scope.row)"
                    >
                      {{ $t('common.delete') }}
                    </el-button>
                  </template>
                </el-table-column> -->
              </el-table>
            </el-collapse-item>
            <!-- <el-collapse-item
              v-if="form.bidFlow == 'JINGJIA'"
              ref="itemInfo"
              title="竞价明细"
              name="2"
            >
              <div class="btn_line">
                <el-button
                  v-if="!readOnly"
                  type="primary"
                  class="detail-pbtn"
                  @click="submitDetailsListAdd"
                >
                  {{ $t('common.add') }}
                </el-button>
                <MImport
                  ref="import"
                  style="display: inline-block; margin: 0 10px;"
                  title="导入"
                  :disabled="readOnly"
                  :up-load-url="iModal.upLoadUrl"
                  :extra-data="extraData"
                  @beforeUpload="beforeUpload"
                  @downloadTemplate="downloadTemplate"
                  @handleSuccess="handleSuccess"
                />
              </div>

              <el-table
                ref="materialDetailRef"
                :data="submitDetailsList"
                style="width: 100%"
                border
                :row-height="38"
                max-height="390px"
                highlight-current-row
              >
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('contractMod.tabindex')"
                  width="50"
                />
                <el-table-column align="center" prop="materialName" label="物资名称" width="120">
                  <template slot="header" slot-scope="scope">
                    <em class="toRequired">*</em>物资名称
                  </template>
                  <template slot-scope="scope">
                    <template>
                      <el-input v-model="scope.row.materialName" :disabled="readOnly" />
                    </template>
                  </template>
                </el-table-column>
                <el-table-column align="center" prop="combination" label="组合" width="120">
                  <template slot-scope="scope">
                    <DictSelect
                      v-model="scope.row.combination"
                      code="JINJIA"
                      :disabled="readOnly"
                    />
                  </template>
                </el-table-column>
                <el-table-column align="center" prop="affiliatedUnit" label="所属单位" width="120">
                  <template slot="header" slot-scope="scope">
                    <em class="toRequired">*</em>所属单位
                  </template>
                  <template slot-scope="scope">
                    <template>
                      <el-input v-model="scope.row.affiliatedUnit" :disabled="readOnly" />
                    </template>
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="performDeposit"
                  label="履约保证金（卢布）"
                  width="120"
                >
                  <template slot="header" slot-scope="scope">
                    <em class="toRequired">*</em>履约保证金（卢布）
                  </template>
                  <template slot-scope="scope">
                    <template>
                      <el-input v-model="scope.row.performDeposit" :disabled="readOnly" />
                    </template>
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="advanceAmount"
                  label="预付款（卢布）"
                  width="120"
                >
                  <template slot="header" slot-scope="scope">
                    <em class="toRequired">*</em>预付款（卢布）
                  </template>
                  <template slot-scope="scope">
                    <template>
                      <el-input v-model="scope.row.advanceAmount" :disabled="readOnly" />
                    </template>
                  </template>
                </el-table-column>
                <el-table-column align="center" prop="monthProduction" label="月约产量" width="120">
                  <template slot="header" slot-scope="scope">
                    <em class="toRequired">*</em>月约产量
                  </template>
                  <template slot-scope="scope">
                    <template>
                      <el-input v-model="scope.row.monthProduction" :disabled="readOnly" />
                    </template>
                  </template>
                </el-table-column>
                <el-table-column align="center" prop="meteringUnit" label="计量单位" width="120">
                  <template slot="header" slot-scope="scope">
                    <em class="toRequired">*</em>计量单位
                  </template>
                  <template slot-scope="scope">
                    <template>
                      <el-input v-model="scope.row.meteringUnit" :disabled="readOnly" />
                    </template>
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="startBidPrice"
                  label="起拍价格（卢布）"
                  width="120"
                >
                  <template slot="header" slot-scope="scope">
                    <em class="toRequired">*</em>起拍价格（卢布）
                  </template>
                  <template slot-scope="scope">
                    <template>
                      <el-input v-model="scope.row.startBidPrice" :disabled="readOnly" />
                    </template>
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="echelonBidPrice"
                  label="梯次价格（卢布）"
                  width="120"
                >
                  <template slot="header" slot-scope="scope">
                    <em class="toRequired">*</em>梯次价格（卢布）
                  </template>
                  <template slot-scope="scope">
                    <template>
                      <el-input v-model="scope.row.echelonBidPrice" :disabled="readOnly" />
                    </template>
                  </template>
                </el-table-column>
                <el-table-column :label="$t('common.operation')" width="60" fixed="right">
                  <template slot-scope="scope">
                    <el-button
                      :disabled="readOnly"
                      type="text"
                      @click="deleteOneContent(scope.$index, scope.row)"
                    >
                      {{ $t('common.delete') }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item> -->
          </el-collapse>
        </div>
<!--      </ApprovalProcess>-->
       </CWorkflowMulti>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import MImport from 'lib@/components/import'
import { downloadFileLink, downloadFileLinkByPost } from 'lib@/utils/file'
import QuickSearch from 'lib@/components/QuickSearch'
import WorkflowCommon from '@/library/mixins/workflow-common'
import { blackApi } from 'modb@/vendorManagementBuyer/api/black'
import { transformMQL } from '@/library/utils/util'
import { biddingDocuments } from '@/modulesCus/buyer/purchasingDemand/api'
import { purchaseApplicationApi } from 'modc@/buyer/purchasingDemand/api'
import returnedGoodsNoticeDetailVue from '@/modules/buyer/orderManagementBuyer/views/returnedGoodsNotice/returnedGoodsNoticeDetail.vue'
import FileDynamic from 'lib@/components/c-file-management/file-dynamic.vue'
import { expInfoHttp } from 'modcb@/expertLibrary/api'
import ApprovalProcess from 'modc@/components/approval-process'
import AmountDisplay from 'lib@/components/amountDisplay'

export default {
  name: 'BlackEdit',
  components: {
    MImport,
    QuickSearch,
    ApprovalProcess,
    FileDynamic,
    AmountDisplay
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      selectFileData: [],
      submitDetailsListDelete: [],
      submitDetailsList: [],
      submitAttachList: [],
      submitEvaluatorListDelete: [],
      fileUploadsDelete: [],
      fileUploads: [],
      companyInfo: [],
      biddingDetails: [],
      iModal: {
        title: this.$t('common.excelImport'),
        upLoadUrl: '/api-sou/bidDataSubmit/import'
      },
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'BIDDING_DOCUMENTS',
        fileType: 'images'
      },
      quaActiveInfo: 'tab1',
      // 文件上传配置信息
      openWorkFlow: true, // 审批流程相关参数
      integrationModeFlow: '', // 工作流集成模式
      flowParams: {}, // 流程参数
      queryForm: {},
      curOpt: '',
      companyInfos: {
        configForm: {
          companyId: '',
          companyName: '',
          companyCode: '',
          companyType: '',
          socialCreditCode: '',
          legalPerson: '',
          registeredCapital: '',
          companyCountry: '',
          companyProvince: '',
          companyCity: '',
          companyCreationDate: ''
        }
      },
      isSrmCompany: false,
      realDataSource: [],
      dataSource: [],
      activeLine: ['1', '2', '3', '4'],
      riskControl: [], // 风险控制
      dialogVisible: false,
      riskControlType: [],
      extraPostData: {},
      extraData: {
        fileModular: 'base',
        fileFunction: 'quotalinetest',
        fileType: 'excel'
      },
      submitEvaluatorList: [],
      form: {
        investNo: null,
        appointEvaluatorReason: null,
        status: 'DRAFT',
        askSeniorExpertNum: null,
        bidEvaluatorNum: null,
        isAppointEvaluator: 'Y',
        notMonthlyPlanReason: null,
        contractSignUnit: '',
        contractSignUnitId: '',
        contractSignUnitCode: '',
        contractSignUnitCredit: '',
        bidEvalDeputyLeaderName: null,
        competeFileName: null,
        competeFileId: null,
        techPrincipal: null,
        techPrincipalId: null,
        techPrincipalCode: null,
        bidEvalLeaderName: null,
        bidFlow: null,
        depositAmount: null,
        requireQuantity: null,
        totalBudget: null,
        sourceFrom: null,
        dataSubmitNo: null,
        souPersonName: null,
        reasonDesc: null,
        reqUserName: null,
        dataSubmitId: null,
        blackCode: null,
        blackType: null,
        blackDescription: null,
        isAllowSourcing: 'N',
        isAllowCreateOrder: 'N',
        isAllowWarehousing: 'N',
        isAllowFinance: 'N',
        isAllowPayment: 'N',
        isImmediately: 'N',
        excessiveTime: 30,
        creationDate: null,
        createdBy: null,
        lastUpdatedBy: null,
        workYears: null
      },
      rules: {
        requirementHeadNum: [{ required: true, message: this.$t('contractMod.pleaseFillIn') }],
        bidFlow: [{ required: true, message: this.$t('contractMod.pleaseFillIn') }],
        bidEvalLeaderName: [{ required: true, message: this.$t('contractMod.pleaseFillIn') }],
        bidEvalDeputyLeaderName: [{ required: true, message: this.$t('contractMod.pleaseFillIn') }],
        contractSignUnit: [{ required: true, message: this.$t('contractMod.pleaseFillIn') }],
        totalBudget: [{ required: true, message: this.$t('contractMod.pleaseFillIn') }]
      },
      readOnly: false,
      dataSubmitId: '',
      orderData: null,
      flowBusinessId: '',
      operationPreOptions: {
        nextStep: this.preNextStepHandler
      },
      statusMap: {
        DRAFT: 'DRAFT', // 拟定
        SUBMITTED: 'APPROVING', // 已提交
        APPROVED: 'APPROVED', // 审批通过
        REJECTED: 'REJECTED', // 已驳回
        WITHDRAW: 'WITHDRAW', // 已撤回
        ABANDONED: 'ABANDON' // 已废弃
      }
    }
  },
  computed: {
    approvalFlag () { // 审批流页面字段展示标识
      return this.$attrs.params.approvalFlag || false
    },
    // 用来控制保存、提交按钮是否可见。如果自定义按钮则无需添加
    viewUpdateButton () {
      return ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(this.form.status) && !this.readOnly
    },
    disabledUpdateButton () {
      return ['SUBMITTED', 'APPROVING'].includes(this.form.status) || this.readOnly
    },
    viewWithDrawButton () {
      console.log(this.form.status, 'status')
      return ['SUBMITTED', 'APPROVING'].includes(this.form.status)
    },
    // 用来指定工作流的业务ID 单据ID 注意每一个单的ID不一样
    workflowBusinessId () {
      return this.form?.dataSubmitId
    },
    // 禁用流程tab状态
    workflowTabDisabled () {
      return (
        (!this.form.dataSubmitId &&
          (['DRAFT', 'REJECTED', 'WITHDRAW'].includes(this.form.status) || this.readOnly)) ||
        !this.form.dataSubmitId
      )
    }
  },
  watch: {
    // 监听保存提交 按钮变更状态，如果自定义按钮则无需添加
    viewUpdateButton () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
      this.buttonConfigInfo.withdraw.view = this.viewWithDrawButton
    },
    // 按钮禁用状态控制
    disabledUpdateButton () {
      this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton
    }
  },
  async created () {
    const { flag, readOnly = false } = this.$attrs.params
    this.curOpt = flag
    this.readOnly = readOnly
    if (flag !== 'add') {
      this.dataSubmitId = this.$attrs.params.row.dataSubmitId
      // 查询是否有多单流程ID
      // const { data } = await this.$api.base.flowAPI.getFlowMainId(this.dataSubmitId)
      // if (data) {
      //   this.flowBusinessId = data
      // } else {
      // this.flowBusinessId = this.dataSubmitId
      // }
      this.flowBusinessId = this.dataSubmitId
      this.getDetail(this.dataSubmitId)
    }
    if (flag === 'add') {
      this.fileUploads = [
        { attachmentType: 'Бюджет проекта (Budget application)1' },
        { attachmentType: 'Техническое задание (Technical task)' },
        { attachmentType: 'Форма КП-общая-Commercial offer form' },
        { attachmentType: 'Критерии технической оценки-Technical evaluation criteria Форма' }
      ]
    }
    // this.getOrgInfoList()
    // 以下内容可控制取消、关闭、保存、提交是否显示。如果自定义按钮，则无需添加
    // this.buttonConfigInfo.save.code = 'XXX' // 保存按钮权限
    // this.buttonConfigInfo.submit.code = 'XXX' // 提交按钮权限
    this.buttonConfigInfo.save.view = this.viewUpdateButton
    this.buttonConfigInfo.submit.view = this.viewUpdateButton
    this.buttonConfigInfo.withdraw.view = this.viewWithDrawButton
    this.buttonConfigInfo.cancel.view = this.readOnly
    this.buttonConfigInfo.close.view = !this.readOnly
  },
  mounted () {},
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
      // 文件已开始下载，请耐心等待，或进入浏览器的下载界面查看下载进度。
      this.$confirm(this.$t('cusEntry.supplement20250121.promptTips6'), this.$t('common.tips'), {
        showCancelButton: false
      }).then(() => {})
      downloadFileLinkByPost('/api-file/extfileupload/batch-download', null, { fileIdList: selectData.map(item => item.fileuploadId) }).then(res => {
        this.$message.success(this.$t('cusEntry.tipMessage.dowmloadSuccess'))
      }).catch(() => {
        this.$message.warning(this.$t('cusEntry.tipMessage.dowmloadError'))
      })
    },
    // 下一步前置处理
    async preNextStepHandler () {
      let allparam = {
        ...this.form,
        totalBudget: this.form.totalBudget ? Number(this.form.totalBudget) : null,
        submitDetailsList: this.submitDetailsList,
        submitEvaluatorList: this.submitEvaluatorList,
        fileUploads: this.fileUploads || []
      }

      let flag = false
      let i = 0
      for (let item of this.submitEvaluatorList) {
        item.evaluatorRole == 'LEADER' && i++
      }
      if (i > 1) {
        flag = true
      }
      if (flag) {
        // 指定评标人只能设置一位评标委员会主席
        this.$message.error(this.$t('cusEntry.supplement20250121.promptTips7'))
        return false
      }
      allparam.submitDetailsList = allparam.submitDetailsList.concat(this.submitDetailsListDelete)
      allparam.submitEvaluatorList = allparam.submitEvaluatorList.concat(
        this.submitEvaluatorListDelete,
      )

      let bol = false
      // if (
      //   !['JINGJIA', 'INQUIRY'].includes(this.form.bidFlow) &&
      //   this.form.isAppointEvaluator == 'Y' &&
      //   (!this.form.appointEvaluatorReason || this.form.appointEvaluatorReason == '')
      // ) {
      //   // 请填写指定评标委员会的原因
      //   this.$message.error(this.$t('cusEntry.supplement20250121.promptTips8'))
      //   bol = true
      //   return false
      // }
      if (
        !['JINGJIA', 'INQUIRY'].includes(this.form.bidFlow) &&
        this.form.isAppointEvaluator == 'Y'
      ) {
        let attr = []
        if (this.form.bidEvaluatorNum < this.submitEvaluatorList.length) {
          // 指定人员数不可超过评标总人数
          this.$message.error(this.$t('cusEntry.supplement20250121.promptTips9'))
          return false
        }
        this.submitEvaluatorList.forEach(e => {
          attr.push(e.ceeaEmpNo)
          if (
            !e.expertLevel ||
            e.expertLevel == null ||
            !e.evaluatorRole ||
            e.evaluatorRole == null
          ) {
            // 请输入专家等级与角色
            this.$message.error(this.$t('cusEntry.supplement20250121.promptTips10'))
            bol = true
            return false
          }
        })
        const bolNew = new Set(attr).size !== attr.length
        if (bolNew == true && bol != true) {
          bol = true
          // 招标评审评审人信息重复
          this.$message.error(this.$t('cusEntry.supplement20250121.promptTips11'))
          return false
        }
      }
      if (bol) {
        return false
      }

      let validForm = false
      let validForm2 = false
      await this.$refs.blackForm.validate(valid => {
        validForm = valid
      })
      await this.$refs.blackForm2.validate(valid => {
        validForm2 = valid
      })
      if (!validForm || !validForm2) {
        this.$message.warning(this.$t('orderMod.pleaseFillrequired'))
        return false
      }

      let bol1 = false
      allparam.fileUploads.forEach(e => {
        if (!e.fileuploadId) {
          bol1 = true
        }
      })
      if (bol1) {
        // 请上传附件
        this.$message.error(this.$t('cusEntry.supplement20250121.promptTips12'))
        return false
      }

      allparam.fileUploads = allparam.fileUploads.concat(this.fileUploadsDelete)
      // 调用校验接口
      const saveData = transformMQL.save('SubmitBuyer', [allparam], 'submit')
      const datas = await biddingDocuments.submit(saveData)
      const dataSubmitId = datas.data[0]?.dataSubmitId
      this.$set(this.form, 'dataSubmitId', dataSubmitId)
      this.submitDetailsListDelete = []
      return true
    },
    // 审批流操作回调
    approvalHandlerCallback (type) {
      switch (type) {
      case 'save':
        this.saveBill('SAVE')
        break
      case 'submit':
        this.back()
        break
      default:
        break
      }
    },
    getCompany (val) {
      if (val) {
        let contractSignUnitId = []
        let contractSignUnitCode = []
        let contractSignUnit = []
        // let contractSignUnitCredit = []
        for (let item of val) {
          contractSignUnitId.push(item.organizationId)
          contractSignUnitCode.push(item.organizationCode)
          contractSignUnit.push(item.organizationName)
          // contractSignUnitCredit.push(item.creditCode)
        }
        this.form.contractSignUnitId = contractSignUnitId.join(',')
        this.form.contractSignUnitCode = contractSignUnitCode.join(',')
        this.form.contractSignUnit = contractSignUnit.join(',')
        // this.form.contractSignUnitCredit = contractSignUnitCredit.join(',')
      } else {
        this.form.contractSignUnitId = null
        this.form.contractSignUnitCode = ''
        this.form.contractSignUnit = ''
        // this.form.contractSignUnitCredit = ''
      }
    },
    handleBidEvaluatorNumBlur () {
      if (this.form.bidEvaluatorNum % 2 == 0) {
        // 评标总人数仅限填写奇数,已自动填写为:${this.form.bidEvaluatorNum - 1}
        this.$message.warning(this.$t('cusEntry.supplement20250121.promptTips13', { num: (this.form.bidEvaluatorNum - 1) }))
        this.form.bidEvaluatorNum = this.form.bidEvaluatorNum - 1
      }
      if (
        this.form.askSeniorExpertNum &&
        this.form.bidEvaluatorNum < this.form.askSeniorExpertNum
      ) {
        this.form.askSeniorExpertNum = this.form.bidEvaluatorNum
      }
    },
    bidFlowChange (value, dictItem) {
      const submitEvaluatorListAll = this.submitEvaluatorList
      submitEvaluatorListAll.forEach(e => {
        if (e?.submitEvaluatorId) {
          this.submitEvaluatorListDelete.push({ $delete: e.submitEvaluatorId })
        }
      })
      // 清空评标组成员
      this.submitEvaluatorList = []
      this.form.bidEvalLeaderName = null
      this.form.bidEvalDeputyLeaderName = null
      this.form.competeFileId = null
      this.form.competeFileName = null

      if (value === 'INQUIRY') {
        this.fileUploads = [
          { attachmentType: 'Бюджет проекта (Budget application)1' },
          { attachmentType: 'Техническое задание (Technical task)' }
        ]
      } else {
        this.fileUploads = [
          { attachmentType: 'Бюджет проекта (Budget application)1' },
          { attachmentType: 'Техническое задание (Technical task)' },
          { attachmentType: 'Форма КП-общая-Commercial offer form' },
          { attachmentType: 'Критерии технической оценки-Technical evaluation criteria Форма' }
        ]
      }
    },
    getUserObj (node, scope) {
      this.$set(scope.row, 'userId', node?.expertUserId)
      this.$set(scope.row, 'username', node?.expertUsername)
      this.$set(scope.row, 'ceeaEmpNo', node?.expertUsername)
      this.$set(scope.row, 'evaluatorName', node?.expertFullName)
      this.$set(scope.row, 'phone', node?.phone)
      this.$set(scope.row, 'email', node?.email)
      this.$set(scope.row, 'extPosition', node?.ceeajobcodedescr)
      this.$set(scope.row, 'expertLevel', node?.expertLevel)
      // this.getHrUserInfoFun(node?.expertUsername, scope.row)
    },
    getHrUserInfoFun (personnelNo, row) {
      if (personnelNo) {
        expInfoHttp.getHrUserInfo({ personnelNo: personnelNo }).then(data => {
          console.log(data, 'data')
          if (data.data?.graduateTime && data.data?.graduateTime != '') {
            this.$set(
              row,
              'workYears',
              new Date().getFullYear() - new Date(data.data?.graduateTime).getFullYear(),
            )
          }
        })
      }
    },
    getOrgInfoList () {
      this.$http({
        url: '/api-pj/organization/organization/listAllOrganization',
        method: 'POST',
        data: {
          organizationTypeCode: 'OU',
          pageNum: 1,
          pageSize: 10000
        },
        loading: false
      })
        .then(res => {
          console.log(res, 'organizationName')
          const companys = []
          res.data.list.forEach(v => {
            companys.push({
              value: v.organizationId.toString(),
              label: v.organizationName
            })
          })
          console.log(companys, 'companys')
          this.companyInfo = companys
        })
        .catch(err => {
          console.log(err)
        })
    },
    getPlanNo (val, scope) {
      console.log(val, 'val')
      this.$set(this.form, 'requirementHeadNum', val?.requirementHeadNum)
      const query = {
        '*': {},
        souReqHead: { '*': {} },
        souGroupList: { '*': {} },
        souVendorList: { '*': {} },
        souAttachList: { '*': {} }
      }
      const searchData = transformMQL.save(
        'PrRequirementForBuyer',
        [{ requirementHeadId: val.requirementHeadId }],
        'getRequirementInfo',
        query,
      )
      // this.$http({
      //   url: '/api-sup-ce/api-ql/PrSouRequirementPoolForBuyer/querySouPool',
      //   method: 'POST',
      //   data: searchData
      // }).then(res => {
      //   this.loading = false
      //   this.lists = res.data.list
      //   this.total = res.data.total
      // })
      purchaseApplicationApi.getRequirementInfo(searchData).then(datas => {
        let data = datas.data.records[0]
        console.log(data)
        let souReqHead = data.souReqHead
        const souGroupList = data.souGroupList
        let sou = {}
        let tech = {}
        let ven = {}
        souGroupList.forEach(datas1 => {
          if (datas1.groupType == 'SOU') {
            sou = datas1
          }
          if (datas1.groupType == 'TECH') {
            tech = datas1
          }
          if (datas1.groupType == 'VENDOR') {
            ven = datas1
          }
        })
        this.form.orgBuId = val.orgBuId
        this.form.orgBuCode = val.orgBuCode
        this.form.orgBuName = val.orgBuName
        this.form.requireFrom = val.requireFrom
        this.form.projectName = val.projectName
        this.form.orgId = val.orgId
        this.form.orgCode = val.orgCode
        this.form.orgName = val.orgName
        this.form.ceeaDepartmentId = val.ceeaDepartmentId
        this.form.ceeaDepartmentCode = val.ceeaDepartmentCode
        this.form.ceeaDepartmentName = val.ceeaDepartmentName
        this.form.reqUserId = val.applyById
        this.form.reqUserName = val?.applyByNickname
        this.form.categoryId = val?.categoryId
        this.form.categoryCode = val?.categoryCode
        this.form.categoryName = val?.categoryName
        this.form.totalBudget = souReqHead?.totalAmountByTenKilo
        this.form.requireQuantity = souReqHead?.requireQuantity
        this.form.sourceFrom = souReqHead?.requireFrom
        this.form.investNo = val?.investNo
        this.form.depositAmount = souReqHead?.souReqHead
        this.form.souPersonId = sou?.userId
        this.$set(this.form, 'souPersonName', sou?.fullName)
        this.form.techPrincipal = tech?.fullName
        this.form.techPrincipalId = tech?.userId
        this.form.techPrincipalCode = tech?.username
        this.form.phone = tech?.phone
        this.form.workYears = tech?.workYear
        this.form.notMonthlyPlanReason = val.noReportMonthPlanReason
      })
    },
    submitDetailsListAdd () {
      this.submitDetailsList.push({})
    },
    // 删除
    deleteOneContent (index, row) {
      if (row?.submitDetailsId) {
        this.submitDetailsListDelete.push({ $delete: row.submitDetailsId })
      }
      this.submitDetailsList.splice(index, 1)
    },
    getDetail (id) {
      const query = {
        '*': {},
        fileUploads: { '*': {} },
        submitDetailsList: { '*': {} },
        submitEvaluatorList: { '*': {} }
      }
      const searchData = transformMQL.save('SubmitBuyer', [{ dataSubmitId: id }], 'read', query)
      biddingDocuments.read(searchData).then(datas => {
        let data = datas.data[0]
        let { fileUploads, submitDetailsList, submitEvaluatorList, ...res } = data
        this.submitDetailsList = submitDetailsList
        this.submitEvaluatorList = submitEvaluatorList
        this.fileUploads = fileUploads
        this.form = {
          ...res
        }
        console.log(this.form)
        this.buttonConfigInfo.withdraw.view = this.viewWithDrawButton
        this.submitEvaluatorListDelete = []
      })
    },
    // 选择供应商回调
    async getCompanyObj (val, scope) {
      console.log(val, 'val')
      this.$set(this.form, 'bidEvalDeputyLeaderName', val.nickname)
      let workYears = null
      if (val?.username) {
        const datas = await expInfoHttp.getHrUserInfo({ personnelNo: val?.username })
        if (datas.data?.graduateTime && datas.data?.graduateTime != '') {
          workYears = new Date().getFullYear() - new Date(datas.data?.graduateTime).getFullYear()
        }
      }
      const obj = {
        userId: val?.userId,
        username: val?.username,
        ceeaEmpNo: val?.username,
        evaluatorName: val?.nickname,
        phone: val?.phone,
        email: val?.email,
        workYears,
        expertLevel: 'SENIOR',
        evaluatorRole: 'FUZUZHANG'
      }
      let bol = false // 是否有评标委员会主席
      this.submitEvaluatorList.forEach((datas, index) => {
        if (datas.evaluatorRole == 'FUZUZHANG') {
          bol = true
          const objNew = {
            ...obj,
            submitEvaluatorId: datas.submitEvaluatorId
          }
          this.$set(this.submitEvaluatorList, index, objNew)
        }
      })
      if (!bol) {
        this.submitEvaluatorList.push(obj)
      }
    },
    async getCompanyObj2 (val, scope) {
      this.$set(this.form, 'bidEvalLeaderName', val?.expertFullName)
      if (val?.expertUsername) {
        const obj = {
          userId: val?.expertUserId,
          username: val?.expertUsername,
          ceeaEmpNo: val?.expertUsername,
          evaluatorName: val?.expertFullName,
          phone: val?.phone,
          email: val?.email,
          extPosition: val?.ceeajobcodedescr,
          expertLevel: val?.expertLevel,
          evaluatorRole: 'LEADER'
        }
        let bol = false // 是否有评标委员会主席
        this.submitEvaluatorList.forEach((datas, index) => {
          if (datas.evaluatorRole == 'LEADER') {
            bol = true
            const objNew = {
              ...obj,
              submitEvaluatorId: datas.submitEvaluatorId
            }
            this.$set(this.submitEvaluatorList, index, objNew)
          }
        })
        if (!bol) {
          this.submitEvaluatorList.push(obj)
        }
      }
    },
     async getCompanyObj3 (val, scope) {
      this.$set(this.form, 'techPrincipal', val?.expertFullName)
      this.$set(this.form, 'techPrincipalId', val?.expertUserId)
      this.$set(this.form, 'techPrincipalCode', val?.expertUsername)
    },
    addCompanyOneItem () {
      this.$refs.queryForm.validate(valid => {
        var distinct = false
        if (valid) {
          this.realDataSource.forEach((val, index) => {
            if (val.socialCreditCode === this.companyInfos.configForm.socialCreditCode) {
              this.$message({
                type: 'warning',
                message: this.$t('black.msgLcCode')
              })
              distinct = true
            }
          })
          if (distinct) {
            return false
          }
          var checkCompany = []
          checkCompany.push({
            ...this.companyInfos.configForm
          })

          var checkData = {
            ...this.form,
            blackCompanyList: checkCompany
          }
          blackApi.checkSubmitData(checkData).then(res => {
            var companyCreationDate
            if (this.companyInfos.configForm.companyId) {
              companyCreationDate = this.companyInfos.configForm.companyCreationDate
            } else {
              companyCreationDate = this.$dayjs(
                this.companyInfos.configForm.companyCreationDate,
              ).format('YYYY-MM-DD')
            }
            this.realDataSource.push({
              companyId: this.companyInfos.configForm.companyId,
              companyName: this.companyInfos.configForm.companyName,
              companyCode: this.companyInfos.configForm.companyCode,
              companyType: this.companyInfos.configForm.companyType,
              socialCreditCode: this.companyInfos.configForm.socialCreditCode,
              legalPerson: this.companyInfos.configForm.legalPerson,
              registeredCapital: this.companyInfos.configForm.registeredCapital,
              companyCountry: this.companyInfos.configForm.companyCountry,
              companyProvince: this.companyInfos.configForm.companyProvince,
              companyCity: this.companyInfos.configForm.companyCity,
              companyCreationDate: companyCreationDate
            })
            this.dialogVisible = false
          })
        } else {
          return false
        }
      })
    },
    downloadTemplate () {
      downloadFileLink('/api-sou/bidDataSubmit/downloadTemplate', this.$t('logisticsMod.importTemplateXLSX')).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },
    addUploadOne () {
      this.fileUploads.push({attachmentType: 'Другое (Others)'})
    },
    addUploadOne2 () {
      this.submitEvaluatorList.push({
        evaluatorRole: 'MEMBER'
      })
    },
    addUploadOne3 (values = []) {
      console.log(values, 'values')
      values?.forEach(data => {
        const obj = {
          evaluatorName: data.expertFullName,
          expertLevel: data.expertLevel,
          ceeaEmpNo: data.applyBy,
          phone: data.phone,
          workYears: new Date().getFullYear() - new Date(data?.creationDate).getFullYear()
        }
        this.submitEvaluatorList.push(obj)
      })

      // const saveData = transformMQL.save('ExtSouExpertForBuyer',[{page: {}, pageSize: 15, pageNum: 1}],'queryExperts')
      // biddingDocuments.extSouExpertForBuyer(saveData).then((datas) => {
      //   // TODO
      //   console.log(datas)
      //   const data = datas.data.records
      //   if (data.length == 0) {
      //     this.$message.error('暂无专家库信息')
      //     return false
      //   }
      //   console.log(data, 'data')
      //   data.forEach(e => {
      //     let obj = {
      //       ceeaEmpNo: e.applyInfo.applyBy,
      //       evaluatorName: e.applyInfo.applyByNickname,
      //       phone: e.applyInfo.phone
      //     }
      //     console.log(obj)
      //     this.submitEvaluatorList.push(obj)
      //   })
      // })
    },
    handleCheckedRisk (checked, val) {
      if (checked) {
        this.$set(this.form, val, 'Y')
      } else {
        this.$set(this.form, val, 'N')
      }
    },
    // 行删除
    handleDelClick (index, row) {
      // if (row?.sceneFileId) {
      //   this.fileUploadsDelete.push({'$delete': row.sceneFileId})
      // }
      this.fileUploads.splice(index, 1)
    },
    handleDelClick2 (index, row) {
      if (row?.submitEvaluatorId) {
        this.submitEvaluatorListDelete.push({ $delete: row.submitEvaluatorId })
      }
      this.submitEvaluatorList.splice(index, 1)
    },
    // 移除
    outerHandleRemove (fileuploadId) {},
    outerButtonClick (index) {
      this.bankRowIndex = index
    },
    handleScriptProgress (percent) {},
    outerHandleUploadSuccess (file, row) {
      const { fileId, fileName } = file || {}
      row.fileuploadId = fileId?.toString()
      row.fileName = fileName
    },
    outerHandleUploadSuccess2 (file) {
      const { fileId, fileName } = file
      this.form.competeFileId = fileId
      this.form.competeFileName = fileName
    },
    beforeUpload () {
      this.extraPostData.dataSubmitId = this.form.dataSubmitId
    },
    handleSuccess (value) {
      const data = value.data
      data.forEach(e => {
        this.submitDetailsList.push(e)
      })
    },
    asyncGetRealDataSource (data) {
      this.realDataSource = data
    },
    addCompany (data) {
      if (data === 'system') {
        this.isSrmCompany = true
      } else {
        this.isSrmCompany = false
      }
      this.companyInfos.configForm.companyId = ''
      this.companyInfos.configForm.companyCode = ''
      this.companyInfos.configForm.companyName = ''
      this.companyInfos.configForm.companyType = ''
      this.companyInfos.configForm.socialCreditCode = ''
      this.companyInfos.configForm.legalPerson = ''
      this.companyInfos.configForm.registeredCapital = ''
      this.companyInfos.configForm.companyCountry = ''
      this.companyInfos.configForm.companyProvince = ''
      this.companyInfos.configForm.companyCity = ''
      this.companyInfos.configForm.companyCreationDate = ''
      this.dialogVisible = true
    },
    deleteItem (index, row) {
      this.realDataSource.splice(index, 1)
    },
    cancelBill () {
      const { tabName } = this.$attrs.params
      this.$emit('tab-remove', tabName)
      this.__setTabTodo('blackList.getQuerydata')
    },
    contractSignUnitChange (val) {
      // debugger
    },
    // 上传附件成功
    handleUploadSuccess (file, row, key) {
      const { id } = file
      row[key] = id.toString()
    },
    // 删除文件
    handleAttachmentRemove (row, key) {
      row[key] = ''
    },
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'bidDataSubmit' // 调试产品
    },
    // 获取ref值
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    // 定义流程额外变量，如果没有就不用添加这个函数
    async getWorkflowBusinessVariables () {
      const procTitleObj = {
        ifBid: this.form.ifBid,
        totalBudget: this.form.totalBudget,
        contractSignUnitCode: this.form.contractSignUnitCode,
        dataSubmitNo: this.form.dataSubmitNo
      }
      return {
        procTitleObj
      }
    },
    /**
     * 获取单据信息
     */
    async getOrderData () {
      return this.orderData ? this.orderData : {}
    },

    /**
     * 获取单据附件ID
     */
    async getFileIds () {
      let fileList = this.orderData ? this.orderData.fileUploads : []
      let fileIds = []
      fileList.forEach(i => {
        if (i.fileuploadId && i.fileuploadId != null && i.fileuploadId != undefined) {
          fileIds.push(i.fileuploadId)
        }
      })
      return fileIds
    },
    // 保存或提交 SAVE SUBMIT
    async saveBill (type, comment) {
      let sum = 0
      this.submitEvaluatorList.map(item => {
        if (item.expertLevel === 'SENIOR') {
          sum++
        }
      })
      this.form.bidEvaluatorNum = this.submitEvaluatorList.length
      this.form.askSeniorExpertNum = sum
      let allparam = {
        ...this.form,
        totalBudget: this.form.totalBudget ? Number(this.form.totalBudget) : null,
        submitDetailsList: this.submitDetailsList,
        submitEvaluatorList: this.submitEvaluatorList,
        fileUploads: this.fileUploads || []
      }
      let flag = false
      if (type == 'SAVE' || type == 'SUBMIT') {
        let i = 0
        for (let item of this.submitEvaluatorList) {
          item.evaluatorRole == 'LEADER' && i++
        }
        i > 1 && (flag = true)
      }
      if (flag) {
        // 指定评标人只能设置一位评标委员会主席
        this.$message({
          message: this.$t('cusEntry.supplement20250121.promptTips7'),
          type: 'error'
        })
        return
      }
      allparam.submitDetailsList = allparam.submitDetailsList.concat(this.submitDetailsListDelete)
      allparam.submitEvaluatorList = allparam.submitEvaluatorList.concat(
        this.submitEvaluatorListDelete,
      )
      if (type == 'SAVE') {
        allparam.fileUploads = allparam.fileUploads.concat(this.fileUploadsDelete)
        const saveData = transformMQL.save('SubmitBuyer', [allparam], 'saveOrUpdate')
        biddingDocuments.save(saveData).then(datas => {
          const dataSubmitId = datas.data[0]?.dataSubmitId
          this.$set(this.form, 'dataSubmitId', dataSubmitId)
          this.getDetail(dataSubmitId)
          this.__setTabTodo('BlackList.getQuerydata')
          this.$message({
            message: this.$t('common.successSave'),
            type: 'success'
          })
          // this.back()
        })
      } else if (type == 'SUBMIT') {
        let bol = false
        // if (
        //   !['JINGJIA', 'INQUIRY'].includes(this.form.bidFlow) &&
        //   this.form.isAppointEvaluator == 'Y' &&
        //   (!this.form.appointEvaluatorReason || this.form.appointEvaluatorReason == '')
        // ) {
        //   // 请填写指定评标委员会的原因
        //   this.$message.error(this.$t('cusEntry.supplement20250121.promptTips8'))
        //   bol = true
        //   return false
        // }
        if (
          !['JINGJIA', 'INQUIRY'].includes(this.form.bidFlow) &&
          this.form.isAppointEvaluator == 'Y'
        ) {
          let attr = []
          this.submitEvaluatorList.forEach(e => {
            attr.push(e.ceeaEmpNo)
            if (
              !e.expertLevel ||
              e.expertLevel == null ||
              !e.evaluatorRole ||
              e.evaluatorRole == null
            ) {
              // 请输入专家等级与角色
              this.$message.error(this.$t('cusEntry.supplement20250121.promptTips10'))
              bol = true
              return false
            }
          })
          const bolNew = new Set(attr).size !== attr.length
          if (bolNew == true && bol != true) {
            bol = true
            // 招标评审评审人信息重复
            this.$message.error(this.$t('cusEntry.supplement20250121.promptTips11'))
          }
          // ①预算（卢布）<= 10000万，评标组人员数量3个及以上（含评标委员会主席），高级专家>=1;
          // ②预算（卢布）> 10000万，评标组人员数量5个及以上（含评标委员会主席），高级专家>=1;
          if (this.form.totalBudget > 100000000) {
            if (this.form.askSeniorExpertNum < 1 || this.form.bidEvaluatorNum < 5) {
              bol = true
              this.$message.error(this.$t('cusEntry.supplement20250121.promptTips5'))
            }
          } else {
            if (this.form.askSeniorExpertNum < 1 || this.form.bidEvaluatorNum < 3) {
              bol = true
              this.$message.error(this.$t('cusEntry.supplement20250121.promptTips4'))
            }
          }
        }
        if (bol) {
          return false
        }
        this.$refs.blackForm.validate(valid1 => {
          if (valid1) {
            this.$refs.blackForm2.validate(valid2 => {
              if (valid2) {
                let bol1 = false
                let bol2 = false
                allparam.fileUploads.forEach((e,index) => {
                  if (!e.fileuploadId && [0, 1, 2].includes(index)) {
                    bol1 = true
                  }
                  if (!e.fileuploadId && [0, 1].includes(index)) {
                    bol2 = true
                  }
                })
                if (['STANDARD', 'SIMPLE'].includes(this.form.bidFlow) && bol1){
                  // 标准招标和简易招标，附件1、附件2、附件3必填
                  this.$message.error(this.$t('cusEntry.supplement20250121.promptTips14'))
                  return false
                }
                if (this.form.bidFlow === 'INQUIRY' && bol2){
                  // 询比价招标，附件1、附件2必填
                  this.$message.error(this.$t('cusEntry.supplement20250121.promptTips15'))
                  return false
                }
                allparam.fileUploads = allparam.fileUploads.concat(this.fileUploadsDelete)
                const saveData = transformMQL.save('SubmitBuyer', [allparam], 'submit')
                biddingDocuments.submit(saveData).then(async datas => {
                  this.$message({
                    message: this.$t('common.successSave'),
                    type: 'success'
                  })
                  const dataSubmitId = datas.data[0]?.dataSubmitId
                  const dataSubmitNo = datas.data[0]?.dataSubmitNo
                  this.$set(this.form, 'dataSubmitId', dataSubmitId)
                  this.$set(this.form, 'dataSubmitNo', dataSubmitNo)
                  await this.getDetail(dataSubmitId)
                  await this.handlerAfter('SUBMIT')
                })
              } else {
                this.$message({
                  type: 'warning',
                  message: this.$t('orderMod.pleaseFillrequired')
                })
                return false
              }
            })
          } else {
            this.$message({
              type: 'warning',
              message: this.$t('orderMod.pleaseFillrequired')
            })
            return false
          }
        })
      }
    },
    back () {
      this.$emit('tab-remove', this.$attrs.tabName)
      this.__setTabTodo('BlackList.getQuerydata')
    }
  }
}
</script>
<style scoped lang="scss">
.btn_line {
  display: flex;
  margin: 0 0 8px 0;
}
.required-tag {
  z-index: 99;
}
:deep(.el-form-item__label) {
  display: flex;
  text-align: right !important;
}
:deep(.el-form-item__content) {
  position: initial;
}
.el-tooltip :deep(.el-button) {
  min-width: 56px;
  font-size: 14px;
  border-radius: 2px;
  padding: 8px 16px;
}
.list-page-query :deep(.el-form-item__label) {
  text-align: right !important;
}
.blackEdit {
  // padding-bottom: 50px;
  // :deep(.table-wrapper) {
  //   padding-left: 0;
  //   padding-right: 0;
  // }
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
  .base-form {
    padding: 15px 30px 0;
  }
  .toRequired {
    color: #ff4949;
    padding-right: 2px;
  }
  .edit_cond {
    color: #23adf4;
    cursor: pointer;
  }
  .form-container1 {
    // margin-bottom: 16px;
  }

}
</style>
