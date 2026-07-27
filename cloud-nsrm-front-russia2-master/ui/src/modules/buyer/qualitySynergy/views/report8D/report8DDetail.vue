<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <div class="form-container2">
        <div class="basic">
          <el-form
            ref="basicForm"
            :model="basicForm"
            label-width="80px"
            label-position="top"
            class="form-fill-style"
            :disabled="isReadOnly || basicForm.reportStatus !== 'DRAFT'"
            :rules="basicFormRules"
          >
            <!-- 第一个表单 -->
            <el-row :gutter="32">
              <el-col :span="6">
                <!-- 报告编号 -->
                <el-form-item
                  :label="$t('qualitySynergy.reportNo')"
                  :label-width="formLabelWidth"
                  prop="reportId"
                >
                  <el-input v-model="basicForm.reportId" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <!-- 报告状态 -->
                <el-form-item
                  :label="$t('qualitySynergy.reportStatus')"
                  :label-width="formLabelWidth"
                  prop="reportStatus"
                >
                  <el-select v-model="basicForm.reportStatus" disabled>
                    <el-option
                      v-for="(item, index) in dict['REPORT_STATUS']"
                      :key="index"
                      :value="item.value"
                      :label="item.label"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <!-- 打印选项 -->
                <el-form-item
                  :label="$t('qualitySynergy.printOptions')"
                  :label-width="formLabelWidth"
                  prop="printOptions"
                >
                  <el-select v-model="basicForm.printOptions" :disabled="isVendor">
                    <el-option value="pdf" label="PDF" />
                    <el-option value="rtf" label="RTF" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <!-- 来源单据类型 -->
                <el-form-item
                  :label="$t('qualitySynergy.sourceDocumentType')"
                  :label-width="formLabelWidth"
                  prop="documentType"
                >
                  <DictSelect
                    v-model="basicForm.documentType"
                    code="QUA_8D_REPORT_DOCUMENT_TYPE"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <!-- 来源单号 -->
                <el-form-item
                  :label="$t('contractMod.sourceNumber')"
                  :label-width="formLabelWidth"
                  prop="sourceOrderId"
                >
                  <el-input v-model="basicForm.sourceOrderId" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <!-- 异常负责人 -->
                <el-form-item
                  :label="$t('qualitySynergy.exceptionAgent')"
                  :label-width="formLabelWidth"
                  prop="exceptionOrderAgent"
                >
                  <el-input v-model="basicForm.exceptionOrderAgent" :disabled="isVendor" />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <!-- 发出日期 -->
                <el-form-item :label="$t('qualitySynergy.sendDate')" :label-width="formLabelWidth">
                  <el-date-picker
                    v-model="basicForm.sendDate"
                    type="date"
                    :format="$formatDatePicker"
                    value-format="yyyy-MM-dd"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <!-- 回复日期 -->
                <el-form-item
                  :label="$t('qualitySynergy.responseDate')"
                  :label-width="formLabelWidth"
                >
                  <el-date-picker
                    v-model="basicForm.responseDate"
                    type="date"
                    :format="$formatDatePicker"
                    value-format="yyyy-MM-dd"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <!-- 发出单位 -->
                <el-form-item
                  :label="$t('qualitySynergy.sendDepartment')"
                  :label-width="formLabelWidth"
                  prop="sendDepartment"
                >
                  <el-input v-model="basicForm.sendDepartment" :disabled="isVendor" />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <!-- 接收单位 -->
                <el-form-item
                  :label="$t('qualitySynergy.receiveDepartment')"
                  :label-width="formLabelWidth"
                  prop="receiveDepartment"
                >
                  <el-input v-model="basicForm.receiveDepartment" :disabled="isVendor" />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <!-- 创建人 -->
                <el-form-item
                  :label="$t('common.creator')"
                  :label-width="formLabelWidth"
                  prop="createdUserName"
                >
                  <el-input v-model="basicForm.createdUserName" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <!-- 创建日期 -->
                <el-form-item
                  :label="$t('qualitySynergy.creationDate')"
                  :label-width="formLabelWidth"
                >
                  <el-date-picker
                    v-model="basicForm.creationDate"
                    type="date"
                    disabled
                    :format="$formatDatePicker"
                    value-format="yyyy-MM-dd"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <!-- 更新人 -->
                <el-form-item
                  :label="$t('common.updatePeople')"
                  :label-width="formLabelWidth"
                  prop="updateBy"
                >
                  <el-input v-model="basicForm.updateBy" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <!-- 更新日期 -->
                <el-form-item
                  :label="$t('qualitySynergy.updateDate')"
                  :label-width="formLabelWidth"
                >
                  <el-date-picker
                    v-model="basicForm.updateDate"
                    type="date"
                    disabled
                    :format="$formatDatePicker"
                    value-format="yyyy-MM-dd"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <!-- 品质审核人 -->
                <el-form-item
                  :label="$t('qualitySynergy.qualityReviewer')"
                  :label-width="formLabelWidth"
                  prop="qualityChecker"
                >
                  <QuickSearch
                    :show-input="basicForm.qualityChecker"
                    show-key="nickname"
                    :scope-data="basicForm"
                    name="scc_rbac_user_display"
                    :disabled="isReadOnly || isVendor || basicForm.reportStatus !== 'DRAFT'"
                    @close-quicksearch="getUserObj"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </div>
        <!-- 收展列 -->
        <el-collapse v-model="activeDims" class="tab-form-style">
          <!-- 问题描述 -->
          <el-collapse-item :title="$t('qualitySynergy.problemDesc')" name="1">
            <el-form
              ref="problemDesForm"
              :model="problemDesForm"
              label-width="80px"
              label-position="top"
              class="form-fill-style"
              :disabled="isReadOnly || isVendor || basicForm.reportStatus !== 'DRAFT'"
              :rules="problemDesFormRules"
            >
              <el-row :gutter="32">
                <el-col :span="6">
                  <el-form-item prop="materialCode" :label="$t('common.materialCode')">
                    <QuickSearch
                      :show-input="problemDesForm.materialCode"
                      show-key="materialCode"
                      :scope-data="problemDesForm"
                      :disabled="isReadOnly || basicForm.documentType == 'ITEM_EXCEPTION' || basicForm.documentType == 'PROCESS_EXCEPTION'"
                      name="scc_base_material_item_display"
                      @close-quicksearch="getItemObj"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 物料状态 -->
                  <el-form-item prop="materialStatus" :label="$t('qualitySynergy.materialStatus')">
                    <el-input
                      v-model="problemDesForm.materialStatus"
                      :disabled="basicForm.documentType == 'ITEM_EXCEPTION' || basicForm.documentType == 'PROCESS_EXCEPTION'"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 物料名称 -->
                  <el-form-item prop="materialName" :label="$t('qualitySynergy.materialName')">
                    <el-input
                      v-model="problemDesForm.materialName"
                      :disabled="basicForm.documentType == 'ITEM_EXCEPTION' || basicForm.documentType == 'PROCESS_EXCEPTION'"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 3个月内发生次数 -->
                  <el-form-item
                    prop="threeMonthsHappens"
                    :label="$t('qualitySynergy.threeMonthsHappens')"
                  >
                    <el-input v-model="problemDesForm.threeMonthsHappens" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 供应商 -->
                  <el-form-item prop="vendorName" :label="$t('common.vendor')">
                    <QuickSearch
                      :show-input="problemDesForm.vendorName"
                      show-key="companyName"
                      :disabled="isReadOnly || basicForm.documentType == 'ITEM_EXCEPTION' || basicForm.documentType == 'PROCESS_EXCEPTION'"
                      :scope-data="problemDesForm"
                      name="scc_sup_company_info_display"
                      @close-quicksearch="getCompanyObj"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item prop="dateCode" label="DATE CODE">
                    <el-input v-model="problemDesForm.dateCode" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item prop="lotNo" label="LOT NO">
                    <el-input v-model="problemDesForm.lotNo" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <!-- 不良描述 -->
                  <el-form-item prop="ngDescribe" :label="$t('qualitySynergy.ngDescribe')">
                    <el-input v-model="problemDesForm.ngDescribe" type="textarea" />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
          </el-collapse-item>
          <el-form
            ref="form"
            :model="form"
            label-width="80px"
            label-position="top"
            class="form-fill-style"
            :disabled="isReadOnly"
            :rules="rules"
          >
            <el-divider />
            <!-- 以下都是供应商填写 -->
            <!-- 组织成员 -->
            <el-collapse-item :title="$t('qualitySynergy.organizationMember')" name="2">
              <el-row>
                <el-col>
                  <!-- 组长(部门/职位/姓名) -->
                  <el-form-item :label="$t('qualitySynergy.groupLeader1')">
                    <el-input v-model="form.groupLeader" type="textarea" :disabled="isBuyer" />
                  </el-form-item>
                </el-col>
                <el-col>
                  <!-- 成员(部门/职位/姓名) -->
                  <el-form-item :label="$t('qualitySynergy.groupMember1')">
                    <el-input v-model="form.groupMember" type="textarea" :disabled="isBuyer" />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-collapse-item>
            <!-- 原因分析 -->
            <el-collapse-item
              ref="causeAnalysis"
              :title="$t('qualitySynergy.causeAnalysis')"
              name="3"
            >
              <el-row :gutter="32">
                <el-col :span="6">
                  <!-- 根本原因追查why1 -->
                  <el-form-item :label="$t('qualitySynergy.why1')" prop="why1">
                    <el-input v-model="form.why1" :disabled="isBuyer" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="why2" prop="why2">
                    <el-input v-model="form.why2" :disabled="isBuyer" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="why3">
                    <el-input v-model="form.why3" :disabled="isBuyer" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="why4">
                    <el-input v-model="form.why4" :disabled="isBuyer" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="why5">
                    <el-input v-model="form.why5" :disabled="isBuyer" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <!-- 制程流出原因 -->
                  <el-form-item
                    :label="$t('qualitySynergy.processOutflowReason')"
                    prop="processOutflowReason"
                  >
                    <el-input
                      v-model="form.processOutflowReason"
                      type="textarea"
                      :disabled="isBuyer"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <!-- 品质流出原因 -->
                  <el-form-item
                    :label="$t('qualitySynergy.qualityOutflowReason')"
                    prop="qualityOutflowReason"
                  >
                    <el-input
                      v-model="form.qualityOutflowReason"
                      type="textarea"
                      :disabled="isBuyer"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-collapse-item>
            <!-- 临时措施及对策 -->
            <el-collapse-item :title="$t('qualitySynergy.tempMeasureAndCounter')" name="4">
              <el-row :gutter="32">
                <el-col :span="6">
                  <!-- 客户端确认数量 -->
                  <el-form-item
                    :label="$t('qualitySynergy.clientConfirmationTotal')"
                    prop="clientConfirmationTotal"
                  >
                    <el-input v-model="form.clientConfirmationTotal" :disabled="isBuyer" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 在途确认数量 -->
                  <el-form-item
                    :label="$t('qualitySynergy.transitConfirmationTotal')"
                    prop="transitConfirmationTotal"
                  >
                    <el-input v-model="form.transitConfirmationTotal" :disabled="isBuyer" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 仓库库存确认数量 -->
                  <el-form-item
                    :label="$t('qualitySynergy.organizationConfirmationTotal')"
                    prop="organizationConfirmationTotal"
                  >
                    <el-input v-model="form.organizationConfirmationTotal" :disabled="isBuyer" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 客户端确认处理方式 -->
                  <el-form-item
                    :label="$t('qualitySynergy.clientHandleWay')"
                    prop="clientHandleWay"
                  >
                    <el-input v-model="form.clientHandleWay" :disabled="isBuyer" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 在途确认处理方式 -->
                  <el-form-item
                    :label="$t('qualitySynergy.transitHandleWay')"
                    prop="transitHandleWay"
                  >
                    <el-input v-model="form.transitHandleWay" :disabled="isBuyer" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 仓库库存确认处理方式 -->
                  <el-form-item
                    :label="$t('qualitySynergy.organizationHandleWay1')"
                    prop="organizationHandleWay"
                  >
                    <el-input v-model="form.organizationHandleWay" :disabled="isBuyer" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 责任人 -->
                  <el-form-item
                    :label="$t('qualitySynergy.responsible')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="form.interimMeasuresAgent" :disabled="isBuyer" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 临时措施处理日期 -->
                  <el-form-item
                    :label="$t('qualitySynergy.interimMeasuresDate')"
                    :label-width="formLabelWidth"
                    prop="interimMeasuresDate"
                  >
                    <el-date-picker
                      v-model="form.interimMeasuresDate"
                      type="date"
                      :format="$formatDatePicker"
                      value-format="yyyy-MM-dd"
                      :disabled="isBuyer"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 临时措施完成日期 -->
                  <el-form-item
                    :label="$t('qualitySynergy.interimMeasuresFinishDate')"
                    :label-width="formLabelWidth"
                    prop="interimMeasuresFinishDate"
                  >
                    <el-date-picker
                      v-model="form.interimMeasuresFinishDate"
                      type="date"
                      :format="$formatDatePicker"
                      value-format="yyyy-MM-dd"
                      :disabled="isBuyer"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-collapse-item>
            <!-- 永久对策 -->
            <el-collapse-item :title="$t('qualitySynergy.countermeasure')" name="5">
              <el-row :gutter="32">
                <el-col :span="6">
                  <!-- 永久对策者 -->
                  <el-form-item
                    :label="$t('qualitySynergy.countermeasurePerson')"
                    prop="countermeasurePerson"
                  >
                    <el-input v-model="form.countermeasurePerson" :disabled="isBuyer" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 永久对策执行部门 -->
                  <el-form-item
                    :label="$t('qualitySynergy.excuteDepartment')"
                    prop="excuteDepartment"
                  >
                    <el-input v-model="form.excuteDepartment" :disabled="isBuyer" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 永久对策完成日期 -->
                  <el-form-item
                    :label="$t('qualitySynergy.countermeasureFinishDate')"
                    :label-width="formLabelWidth"
                    prop="countermeasureFinishDate"
                  >
                    <el-date-picker
                      v-model="form.countermeasureFinishDate"
                      type="date"
                      :format="$formatDatePicker"
                      value-format="yyyy-MM-dd"
                      :disabled="isBuyer"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 永久对策执行者 -->
                  <el-form-item :label="$t('qualitySynergy.excutePerson')" prop="excutePerson">
                    <el-input v-model="form.excutePerson" :disabled="isBuyer" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <!-- 根本原因对策 -->
                  <el-form-item
                    :label="$t('qualitySynergy.countermeasureRootReason')"
                    prop="countermeasureRootReason"
                  >
                    <el-input
                      v-model="form.countermeasureRootReason"
                      type="textarea"
                      :disabled="isBuyer"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <!-- 流出原因对策 -->
                  <el-form-item
                    :label="$t('qualitySynergy.countermeasureOutflowReason')"
                    prop="countermeasureOutflowReason"
                  >
                    <el-input
                      v-model="form.countermeasureOutflowReason"
                      type="textarea"
                      :disabled="isBuyer"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-collapse-item>
            <!-- 效果确认 -->
            <el-collapse-item :title="$t('qualitySynergy.effectConfirm')" name="6">
              <el-row :gutter="32">
                <el-col :span="12">
                  <!-- 来料检验报告单号1 -->
                  <el-form-item :label="$t('qualitySynergy.itemExceptionId1')">
                    <el-input v-model="form.itemExceptionId1" :disabled="isBuyer" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <!-- 检验结果1 -->
                  <el-form-item :label="$t('qualitySynergy.checkResult1')">
                    <el-input v-model="form.checkResult1" :disabled="isBuyer" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <!-- 来料检验报告单号2 -->
                  <el-form-item :label="$t('qualitySynergy.itemExceptionId2')">
                    <el-input v-model="form.itemExceptionId2" :disabled="isBuyer" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <!-- 检验结果2 -->
                  <el-form-item :label="$t('qualitySynergy.checkResult2')">
                    <el-input v-model="form.checkResult2" :disabled="isBuyer" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <!-- 来料检验报告单号3 -->
                  <el-form-item :label="$t('qualitySynergy.itemExceptionId3')">
                    <el-input v-model="form.itemExceptionId3" :disabled="isBuyer" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <!-- 检验结果3 -->
                  <el-form-item :label="$t('qualitySynergy.checkResult3')">
                    <el-input v-model="form.checkResult3" :disabled="isBuyer" />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-collapse-item>
            <!-- 预防再发生或标准化 -->
            <el-collapse-item :title="$t('qualitySynergy.preventRecurrence')" name="7">
              <el-row :gutter="32">
                <el-col :span="6">
                  <!-- 改善前流程 -->
                  <el-form-item :label="$t('qualitySynergy.beforeImprovement')">
                    <el-input v-model="form.beforeImprovement" :disabled="isBuyer" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 改善后流程 -->
                  <el-form-item :label="$t('qualitySynergy.afterImprovement')">
                    <el-input v-model="form.afterImprovement" :disabled="isBuyer" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 旧流程废除日期 -->
                  <el-form-item
                    :label="$t('qualitySynergy.oldProcessAbolishDate')"
                    :label-width="formLabelWidth"
                  >
                    <el-date-picker
                      v-model="form.oldProcessAbolishDate"
                      type="date"
                      :format="$formatDatePicker"
                      value-format="yyyy-MM-dd"
                      :disabled="isBuyer"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 新流程执行日期 -->
                  <el-form-item
                    :label="$t('qualitySynergy.newProcessAbolishDate')"
                    :label-width="formLabelWidth"
                  >
                    <el-date-picker
                      v-model="form.newProcessAbolishDate"
                      type="date"
                      :format="$formatDatePicker"
                      value-format="yyyy-MM-dd"
                      :disabled="isBuyer"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-collapse-item>
            <!-- 小组总结 -->
            <el-collapse-item :title="$t('qualitySynergy.groupSummary')" name="8">
              <el-row :gutter="32">
                <el-col>
                  <!-- 平行展开 -->
                  <el-form-item :label="$t('qualitySynergy.parallelExpansion')">
                    <el-input
                      v-model="form.parallelExpansion"
                      type="textarea"
                      :disabled="isBuyer"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <!-- 制定人 -->
                  <el-form-item :label="$t('qualitySynergy.madeBy')" :label-width="formLabelWidth">
                    <el-input v-model="form.madeBy" :disabled="isBuyer" />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <!-- 审核人 -->
                  <el-form-item
                    :label="$t('qualitySynergy.reviewer')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="form.groupSummaryChecker" :disabled="isBuyer" />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <!-- 批准人 -->
                  <el-form-item
                    :label="$t('qualitySynergy.approvedBy')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="form.approvedBy" :disabled="isBuyer" />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-collapse-item>
            <!-- 佐证资料上传 v-if="isReadOnly || isEdit"-->
            <el-collapse-item :title="$t('qualitySynergy.uploadSupportInfo')" name="9">
              <FileDynamic
                ref="sceneAttachment"
                v-model="fileuploads"
                scene-module-code="SCENE_8D_REPORT_ATTACHMENT"
                :business-id="curReport8DId"
                :editable="isVendor && !isReadOnly"
              />
            </el-collapse-item>
            <!-- 发出单位审核结论 -->
            <el-collapse-item :title="$t('qualitySynergy.issueUnitAuditConclusion')" name="10">
              <el-row :gutter="32">
                <el-col :span="4" :offset="2">
                  <div class="colStyle">
                    <el-radio
                      v-model="form.isQualified"
                      :label="$t('qualitySynergy.unqualified')"
                      :disabled="isVendor || basicForm.reportStatus !== &quot;SUPPLIER_HAVE_FEEDBACK&quot;"
                    >
                      {{
                        $t('qualitySynergy.unqualified')
                      }}
                    </el-radio>
                    <el-radio
                      v-model="form.isQualified"
                      :label="$t('qualitySynergy.qualified')"
                      :disabled="isVendor || basicForm.reportStatus !== &quot;SUPPLIER_HAVE_FEEDBACK&quot;"
                    >
                      {{
                        $t('qualitySynergy.qualified')
                      }}
                    </el-radio>
                  </div>
                </el-col>
                <el-col :span="12">
                  <!-- 审核备注 -->
                  <el-form-item prop="checkComment" :label="$t('qualitySynergy.reviewNote')">
                    <el-input
                      v-model="form.checkComment"
                      type="textarea"
                      :disabled="isVendor || basicForm.reportStatus !== &quot;SUPPLIER_HAVE_FEEDBACK&quot;"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-collapse-item>
            <!-- 关闭报告 -->
            <el-collapse-item :title="$t('qualitySynergy.closeReport')" name="11">
              <el-row :gutter="32">
                <el-col :span="12">
                  <!-- 关闭原因 -->
                  <el-form-item prop="closeReason" :label="$t('qualitySynergy.closeReason')">
                    <el-input v-model="form.closeReason" :disabled="['DRAFT'].includes(basicForm.reportStatus) || isVendor" type="textarea" />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-collapse-item>
          </el-form>
          <!-- 审核记录 -->
          <el-collapse-item :title="$t('common.operationRecord')" name="12">
            <el-main style="min-height: 150px;">
              <el-table :data="examineList" border stripe>
                <el-table-column
                  v-for="(item, index) in examineColums"
                  :key="index"
                  :align="item.align"
                  :label="item.label"
                  :prop="item.prop"
                />
              </el-table>
            </el-main>
          </el-collapse-item>
        </el-collapse>
      </div>
      <CToolbar>
        <template slot="right">
          <template v-if="isBuyer">
            <el-button v-if="!isReadOnly && !['DRAFT'].includes(basicForm.reportStatus)" type="primary" @click="close">
              {{ $t('common.close') }}
            </el-button>
            <el-button
              v-if="!isReadOnly && ['DRAFT'].includes(basicForm.reportStatus)"
              @click="save('staging')"
            >
              {{ $t('common.staging') }}
            </el-button>
            <el-button
              v-if="!isReadOnly && ['DRAFT'].includes(basicForm.reportStatus)"
              type="primary"
              @click="save('submit')"
            >
              {{ $t('common.publish') }}
            </el-button>
            <el-button
              v-if="!isReadOnly && ['SUPPLIER_HAVE_FEEDBACK'].includes(basicForm.reportStatus) && basicForm.qualityCheckerBy === user.username"
              type="primary"
              @click="save('approval')"
            >
              {{ $t('qualitySynergy.submitApprovalResults') }}
            </el-button>
          </template>
          <template v-if="isVendor">
            <el-button @click="cancelBill">
              {{ $t('common.cancel') }}
            </el-button>
            <el-button
              v-if="!isReadOnly && ['PUBLISHED'].includes(basicForm.reportStatus)"
              @click="save('staging')"
            >
              {{ $t('common.staging') }}
            </el-button>
            <el-button
              v-if="!isReadOnly && ['PUBLISHED'].includes(basicForm.reportStatus)"
              type="primary"
              @click="save('feedback')"
            >
              {{ $t('qualitySynergy.submitReply') }}
            </el-button>
          </template>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import CToolbar from 'lib@/components/c-toolbar'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import QuickSearch from 'lib@/components/QuickSearch'
import MainHeader from 'lib@/components/Table/MainHeader'
import { getDictItemList } from '@/api/common'
import { adaptDictData } from '@/utils'
import FileDynamic from '@/library/components/c-file-management/file-dynamic'
import { inspectionStandard } from 'modb@/qualitySynergy/api'
export default {
  name: 'Report8DDetail',
  components: {
    MainHeader,
    CToolbar,
    QuickSearch,
    FileDynamic
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      fileuploads: [],
      user: this.$store.getters.user,
      dict: {
        'REPORT_STATUS': []
      },
      formLabelWidth: '120',
      curReport8DId: null,
      activeDims: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'],
      basicForm: {
        reportId: null,
        reportStatus: null,
        printOptions: null,
        documentType: null,
        sourceOrderId: null,
        exceptionOrderAgent: null,
        sendDate: null,
        responseDate: null,
        sendDepartment: null,
        receiveDepartment: null,
        createdBy: null,
        creationDate: null,
        updateBy: null,
        updateDate: null,
        qualityCheckerId: null,
        qualityChecker: null
      },
      basicFormRules: {
        qualityChecker: [{ required: true, message: this.$t('qualitySynergy.pleaseSelectQualityReviewer'), trigger: 'blur' }]
      },
      rules: {
        why1: [{ required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }],
        why2: [{ required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }],
        processOutflowReason: [{ required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }],
        qualityOutflowReason: [{ required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }],
        clientConfirmationTotal: [{ required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }],
        transitConfirmationTotal: [{ required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }],
        organizationConfirmationTotal: [{ required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }],
        clientHandleWay: [{ required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }],
        transitHandleWay: [{ required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }],
        organizationHandleWay: [{ required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }],
        interimMeasuresAgent: [{ required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }],
        interimMeasuresDate: [{ required: true, message: this.$t('qualitySynergy.msgSelDate'), trigger: 'change' }],
        interimMeasuresFinishDate: [
          { required: true, message: this.$t('qualitySynergy.msgSelDate'), trigger: 'change' }
        ],
        countermeasurePerson: [{ required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }],
        excuteDepartment: [{ required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }],
        countermeasureFinishDate: [
          { required: true, message: this.$t('qualitySynergy.msgSelDate'), trigger: 'change' }
        ],
        excutePerson: [{ required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }],
        countermeasureRootReason: [{ required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }],
        countermeasureOutflowReason: [{ required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }],
        madeBy: [{ required: true, message: this.$t('common.pleaseSelect'), trigger: 'change' }]
      },
      problemDesForm: {
        checkStandard: null,
        materialName: '',
        materialCode: '',
        materialStatus: null,
        materialId: null,
        threeMonthsHappens: null,
        vendorName: '',
        dateCode: null,
        lotNo: null,
        ngDescribe: ''
      },
      problemDesFormRules: {
        materialCode: [{ required: true, message: this.$t('common.pleaseSelect') }],
        materialName: [{ required: true, message: this.$t('common.pleaseInput') }],
        vendorName: [{ required: true, message: this.$t('common.pleaseSelect') }],
        ngDescribe: [{ required: true, message: this.$t('common.pleaseInput') }]
      },
      form: {
        groupLeader: null,
        groupMember: null,
        why1: null,
        why2: null,
        why3: null,
        why4: null,
        why5: null,
        processOutflowReason: null,
        qualityOutflowReason: null,
        clientConfirmationTotal: null,
        transitConfirmationTotal: null,
        organizationConfirmationTotal: null,
        clientHandleWay: null,
        transitHandleWay: null,
        organizationHandleWay: null,
        interimMeasuresAgentID: null,
        interimMeasuresAgent: null,
        interimMeasuresDate: null,
        interimMeasuresFinishDate: null,
        countermeasurePerson: null,
        excuteDepartment: null,
        countermeasureFinishDate: null,
        excutePerson: null,
        countermeasureRootReason: null,
        countermeasureOutflowReason: null,
        itemExceptionId1: null,
        itemExceptionId2: null,
        itemExceptionId3: null,
        checkResult1: null,
        checkResult2: null,
        checkResult3: null,
        beforeImprovement: null,
        afterImprovement: null,
        oldProcessAbolishDate: null,
        newProcessAbolishDate: null,
        parallelExpansion: null,
        madeBy: null,
        madeByID: null,
        groupSummaryChecker: null,
        groupSummaryCheckerID: null,
        approvedBy: null,
        approvedByID: null,
        isQualified: null,
        checkComment: null,
        closeReason: null
      },
      examineList: [],
      examineColums: [
        {
          align: 'center',
          label: this.$t('common.operator'),
          prop: 'lastUpdatedFullName'
        },
        {
          align: 'center',
          label: this.$t('qualitySynergy.operationMode'),
          prop: 'results'
        },
        {
          align: 'center',
          label: this.$t('common.remark'),
          prop: 'remarks'
        }
      ],
      viewflag: '',
      isReadOnly: this.$attrs.params.flag == 'readOnly',
      isEdit: this.$attrs.params.flag == 'edit',
      qua8DProblemList: [],
      realQua8DProblemList: [],
      realExamineList: [],
      report8DAttaches: []
    }
  },
  computed: {
    isBuyer () {
      return this.$store.getters.userType === 'BUYER'
    },
    isVendor () {
      return this.$store.getters.userType === 'VENDOR'
    }
  },
  async created () {
    const { flag, row } = this.$attrs.params
    this.viewflag = flag
    if (flag == 'incomingExceptionAdd') {
      // 从来料异常单或制程异常单来创建8D报告
      this.qua8DProblemList = row.quaItemNgDescList
      this.basicForm = row
      this.basicForm.reportStatus = 'DRAFT'
      this.basicForm.documentType = 'ITEM_EXCEPTION' // 来料异常
      this.basicForm.sourceOrderId = row.itemExceptionHeadId
      this.problemDesForm = row
      this.form = row
      this.examineList[0] = row

      this.$nextTick(() => {
        this.$refs.sceneAttachment.loadFileInfo()
      })
    } else if (flag == 'processExceptionAdd') {
      this.qua8DProblemList[0] = row
      this.basicForm = row
      this.basicForm.reportStatus = 'DRAFT'
      this.basicForm.documentType = 'PROCESS_EXCEPTION' // 制程异常
      this.basicForm.sourceOrderId = row.billCode
      this.problemDesForm = row
      this.problemDesForm.ngDescribe = row.unqualifiedDesc
      this.form = row
      this.examineList[0] = row

      this.$nextTick(() => {
        this.$refs.sceneAttachment.loadFileInfo()
      })
    } else if (flag == 'add') {
      this.basicForm.documentType = 'MANUALLY_CREATE'
      this.basicForm.reportStatus = 'DRAFT'

      this.$nextTick(() => {
        this.$refs.sceneAttachment.loadFileInfo()
      })
    } else { // edit readOnly
      this.curReport8DId = row.reportId
      this.getFormDetail(row.reportId)
    }
    await this.fetchDictData()
  },
  methods: {
    getItemObj (val, data) {
      this.problemDesForm.materialId = val ? val.materialId : null
      this.problemDesForm.materialCode = val ? val.materialCode : ''
      this.problemDesForm.materialName = val ? val.materialName : ''
      this.problemDesForm.materialStatus = val ? (val.materialStatus || '') : ''
    },
    async fetchDictData () {
      let keyList = Object.keys(this.dict)
      let res = await getDictItemList(
        keyList.map(key => {
          return { dictCode: key }
        })
      )
      if (res.data) {
        keyList.forEach((key, index) => {
          this.dict[key] = adaptDictData(res.data[index][key])
        })
      }
    },
    getUserObj (val, scope) {
      console.log('val:::', val)
      scope.qualityCheckerId = val ? val.userId : ''
      scope.qualityCheckerBy = scope.qualityChecker = val ? val.nickname : ''
    },
    getCompanyObj (val, scope) {
      console.log('company:::', val)
      scope.vendorName = val ? val.companyName : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorId = val ? val.companyId : ''
    },
    cancelBill () {
      const { flag } = this.$attrs.params
      if (flag == 'add') {
        this.$emit('tab-remove', 'report8DDetail')
      } else {
        this.$emit('tab-remove', this.$attrs.params.tabName)
      }
      this.__setTabTodo('Report8DList.getQuerydata')
    },
    getFormDetail (reportId) {
      inspectionStandard.reportDetail({ reportId }).then(res => {
        if (res.data) {
          console.log('records:::', res.data.records)
          this.qua8DProblemList = res.data.qua8DProblemList
          this.basicForm = res.data
          this.problemDesForm = res.data
          this.fileuploads = res.data.fileUploads
          this.form = res.data
          this.examineList = res.data.records
          this.$nextTick(() => {
            this.$refs.sceneAttachment.loadFileInfo()
          })
        }
      })
    },
    checkForm (formName) {
      return new Promise((resolve, reject) => {
        this.$refs[formName].validate(valid => {
          if (valid) {
            resolve()
            // eslint-disable-next-line prefer-promise-reject-errors
          } else reject()
        })
      })
    },
    save (type) {
      if (type !== 'staging') {
        let list = []
        list.push(
          this.checkForm('basicForm'), // 表单
          this.checkForm('problemDesForm'), // 问题描述
        )
        if (this.isVendor) list.push(this.checkForm('form')) // 问题描述之下所有

        Promise.all(list)
          .then(() => {
            this.saveFetch(type)
          })
      } else {
        this.saveFetch(type)
      }
    },
    saveFetch (type) {
      const { flag } = this.$attrs.params
      let data = {
        ...this.basicForm,
        ...this.form,
        ...this.problemDesForm,
        fileUploads: this.fileuploads
      }
      if (flag == 'edit') {
        let url = type === 'staging' ? '/api-pef/report8D/saveOrUpdateQua8dReport' : '/api-pef/report8D/publishQua8dReport'
        if (type === 'feedback') url = '/api-pef/report8D/vendorFeedbacKQua8dReport'
        if (type === 'approval') url = '/api-pef/report8D/approvalQua8dReport'

        inspectionStandard.reportSave(url, data).then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.cancelBill()
        })
      } else { // 手动创建
        let url = type === 'staging' ? '/api-pef/report8D/saveOrUpdateQua8dReport' : '/api-pef/report8D/publishQua8dReport'
        inspectionStandard.reportSave(url, data).then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.cancelBill()
        })
      }
    },
    close () {
      if (!this.form.closeReason) {
        this.$message.warning(this.$t('qualitySynergy.pleaseFillInTheReasonForClosing'))
        return
      }
      let data = {
        ...this.basicForm,
        ...this.form,
        ...this.problemDesForm,
        fileUploads: this.fileuploads
      }
      inspectionStandard.reportClose2(data).then(() => {
        this.$message({
          message: this.$t('contractMod.closeSuccess'),
          type: 'success'
        })
        this.cancelBill()
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.table-wrapper {
  padding-left: 0px;
}

.basic {
  padding-left: 11px;
}

.btn_line {
  margin: 0 0 8px 0;
}

.colStyle {
  padding-top: 25px;
}

:deep(.table-wrapper) {
  padding-left: 0px;
}
</style>
