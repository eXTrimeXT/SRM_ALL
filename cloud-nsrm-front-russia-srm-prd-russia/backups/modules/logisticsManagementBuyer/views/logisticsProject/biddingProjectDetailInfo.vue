<template>
  <el-form
    ref="form"
    :model="allParams.biding"
    label-width="80px"
    label-position="top"
    class="form-incontainer"
    :rules="rules"
    :disabled="isdisabledTab"
  >
    <el-collapse
      v-model="activeDims"
      class="tab-form-style"
    >
      <el-collapse-item
        :title="$t('bidMod.projectInformation')"
        name="1"
      >
        <el-row>
          <el-col :span="8">
            <el-form-item
              :label="$t('bidMod.processConfigId')"
              :label-width="formLabelWidth"
              prop="processConfigId"
            >
              <el-select
                v-model="allParams.biding.processConfigId"
                :disabled="!!allParams.biding.processConfigId"
                @change="getprocessConfigId"
              >
                <el-option
                  v-for="item in bidProcessConfigIdList"
                  :key="item.processConfigId"
                  :label="item.processConfigName"
                  :value="item.processConfigId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item
              :label="$t('bidMod.bidingNum')"
              :label-width="formLabelWidth"
            >
              <el-input
                v-model="allParams.biding.bidingNum"
                disabled
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <!-- 申请模板 -->
            <el-form-item
              :label="$t('logisticsMod.applyTemplate')"
              :label-width="formLabelWidth"
              prop="templateCode"
            >
              <el-select
                v-model="allParams.biding.templateCode"
                :disabled="allParams.biding.templateCode"
                @change="changeTemplateCode"
              >
                <el-option
                  v-for="item in templateAllList"
                  :key="item.templateHeadId"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <!-- 业务模式 -->
            <el-form-item
              :label="$t('logisticsMod.businessMode')"
              :label-width="formLabelWidth"
              prop="businessModeCode"
            >
              <DictSelect
                v-model="allParams.biding.businessModeCode"
                code="BUSINESS_MODE"
                disabled
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <!-- 运输方式 -->
            <el-form-item
              :label="$t('bid_mod.transportType')"
              :label-width="formLabelWidth"
              prop="transportModeCode"
            >
              <DictSelect
                v-model="allParams.biding.transportModeCode"
                code="TRANSPORT_MODE"
                disabled
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <!-- 业务类型 -->
            <el-form-item
              :label="$t('bidMod.businessType')"
              :label-width="formLabelWidth"
              prop="contractType"
            >
              <el-select
                v-model="allParams.biding.contractType"
                :disabled="allParams.biding.sourceFrom == 'PURCHASE_REQUEST'"
                @change="changeBusinessType"
              >
                <el-option
                  v-for="item in contractTypeList"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <!-- <el-col :span="8">
            <el-form-item
              label="服务项目名称"
              :label-width="formLabelWidth"
              prop="serviceProjectName"
              :rules='businessTypeRules'
            >
              <QuickSearch
                :showInput="allParams.biding.serviceProjectName"
                :disabled="allParams.biding.businessType!='PROJECT' || allParams.biding.sourceFrom == 'PURCHASE_REQUEST'"
                show-key="projectName"
                :scope-data="allParams.biding"
                name="ceea_logistics_project_info"
                @close-quicksearch="getProjectObj"
              />
            </el-form-item>
          </el-col> -->
          <el-col :span="8">
            <!-- 单位 -->
            <el-form-item
              :label="$t('bid_mod.unit')"
              :label-width="formLabelWidth"
            >
              <el-input
                v-model="allParams.biding.unitCode"
                :disabled="
                  allParams.biding.businessType == 'PROJECT' ||
                    allParams.biding.sourceFrom == 'PURCHASE_REQUEST'
                "
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <!-- 项目总量 -->
            <el-form-item
              :label="$t('logisticsMod.projectTotal')"
              :label-width="formLabelWidth"
            >
              <el-input
                v-model="allParams.biding.projectTotal"
                type="number"
                :disabled="
                  allParams.biding.businessType == 'PROJECT' ||
                    allParams.biding.sourceFrom == 'PURCHASE_REQUEST'
                "
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item
              :label="$t('bidMod.bidingName')"
              :label-width="formLabelWidth"
              prop="bidingName"
            >
              <el-input
                v-model="allParams.biding.bidingName"
                :disabled="allParams.biding.sourceFrom == 'PURCHASE_REQUEST'"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <!-- 需求日期 -->
            <el-form-item
              :label="$t('bidMod.ceeaDemandDate')"
              :label-width="formLabelWidth"
              prop="demandDate"
            >
              <el-date-picker
                v-model="allParams.biding.demandDate"
                :picker-options="pickerOptions1"
                :disabled="allParams.biding.sourceFrom == 'PURCHASE_REQUEST'"
                type="date"
                value-format="yyyy-MM-dd"
                :placeholder="$t('bidMod.datePicker')"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <!-- 预算金额 -->
            <el-form-item
              :label="$t('bidMod.budgetAmount2')"
              :label-width="formLabelWidth"
              prop="budgetAmount"
            >
              <el-input
                v-model="allParams.biding.budgetAmount"
                v-input-format="{ type: 'float' }"
                :disabled="allParams.biding.sourceFrom == 'PURCHASE_REQUEST'"
              />
            </el-form-item>
          </el-col>
          <!-- <el-col :span="8">
            <el-form-item
              label="币种"
              :label-width="formLabelWidth"
              prop="currencyCode"
            >
              <RenderSelect
                code="currency"
                :disabled="allParams.biding.sourceFrom == 'PURCHASE_REQUEST'"
                v-model="allParams.biding.currencyCode"
              />
            </el-form-item>
          </el-col> -->
          <el-col :span="8">
            <!-- 项目地可进最大车型 -->
            <el-form-item
              :label="$t('logisticsMod.allowedVehicleRules')"
              :label-width="formLabelWidth"
              prop="allowedVehicle"
              :rules="allowedVehicleRules"
            >
              <DictSelect
                v-model="allParams.biding.allowedVehicle"
                code="ALLOWED_VEHICLE"
                :disabled="
                  allParams.biding.businessModeCode != 'I' ||
                    allParams.biding.sourceFrom == 'PURCHASE_REQUEST'
                "
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item
              :label="$t('bid_mod.defaultPriceValidFrom')"
              :label-width="formLabelWidth"
              prop="priceTimeStart"
            >
              <el-date-picker
                v-model="allParams.biding.priceTimeStart"
                :disabled="allParams.biding.sourceFrom == 'PURCHASE_REQUEST'"
                type="date"
                :picker-options="pickerOptions1"
                value-format="yyyy-MM-dd"
                :placeholder="$t('bidMod.datePicker')"
                @change="chengeTimeStart"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item
              :label="$t('bid_mod.defaultPriceValidTo')"
              :label-width="formLabelWidth"
              prop="priceTimeEnd"
            >
              <el-date-picker
                v-model="allParams.biding.priceTimeEnd"
                :disabled="allParams.biding.sourceFrom == 'PURCHASE_REQUEST'"
                type="date"
                value-format="yyyy-MM-dd"
                :picker-options="pickerOptions2"
                :placeholder="$t('bidMod.datePicker')"
              />
            </el-form-item>
          </el-col>
          <!-- <el-col :span="8">
            <el-form-item
              label="装载量"
              :label-width="formLabelWidth"
              prop='loadNumber'
            >
              <el-input
                type="number"
                v-model="allParams.biding.loadNumber"
                :disabled="allParams.biding.sourceFrom == 'PURCHASE_REQUEST'"
              />
            </el-form-item>
          </el-col> -->
          <el-col :span="8">
            <el-form-item
              :label="$t('bidMod.enrollEndDatetime')"
              :label-width="formLabelWidth"
              prop="enrollEndDatetime"
            >
              <el-date-picker
                v-model="allParams.biding.enrollEndDatetime"
                type="datetime"
                value-format="timestamp"
                :default-value="new Date()"
                :picker-options="pickerOptions1"
                :placeholder="$t('bidMod.datePicker')"
              />
            </el-form-item>
          </el-col>
          <!-- <el-col :span="8">
            <el-form-item
              label="税率"
              :label-width="formLabelWidth"
              prop='taxKey'
            >
              <RenderSelect
                code="tax"
                @change="val => taxRateChangeHandel(val, allParams.biding)"
                v-model="allParams.biding.taxKey"
              />
            </el-form-item>
          </el-col> -->
          <el-col :span="8">
            <!-- 是否含技术标 -->
            <el-form-item
              :label="$t('logisticsMod.ifTechnicaclBId1')"
              :label-width="formLabelWidth"
              prop="ifVendorSubmitShipDate"
            >
              <!-- <RenderSelect
                code="yesNoOptions"
                disabled
                v-model="allParams.biding.ifVendorSubmitShipDate"
              /> -->
              <DictSelect
                v-model="allParams.biding.ifVendorSubmitShipDate"
                code="YES_OR_NO"
                disabled
              />
            </el-form-item>
          </el-col>
          <!-- <el-col :span="8">
            <el-form-item
              label="是否供应商确认"
              :label-width="formLabelWidth">
              <RenderSelect
                code="yesNoOptions"
                :disabled="allParams.biding.sourceFrom != 'PURCHASE_REQUEST'"
                v-model="allParams.biding.ifNeedVendorComfirm"
              />
            </el-form-item>
          </el-col> -->
          <!-- <el-col :span="8">
            <el-form-item
              label="决标方式"
              :label-width="formLabelWidth"
              prop="bidingAwardWay"
            >
              <RenderSelect
                code="BID_DECIDE_METHOD"
                v-model="allParams.biding.bidingAwardWay"
              />
            </el-form-item>
          </el-col> -->
          <el-col :span="8">
            <!-- 指定供应商 -->
            <el-form-item
              :label="$t('purchaseDemand.awardedSupplierName')"
              :label-width="formLabelWidth"
              prop="companyName"
            >
              <quick-search
                :show-input="allParams.biding.companyName"
                show-key="companyName"
                :disabled="allParams.biding.sourceFrom == 'PURCHASE_REQUEST'"
                :scope-data="allParams.biding"
                name="scc_sup_company_info"
                @close-quicksearch="getVendorObj4"
              />
            </el-form-item>
          </el-col>
          <el-col :span="16">
            <!-- 指定供应商原因 -->
            <el-form-item
              :label="$t('logisticsMod.speicfySupplierReason')"
              :label-width="formLabelWidth"
              prop="specifySupReason"
              :rules="vendorReasonRules"
            >
              <el-input
                v-model="allParams.biding.specifySupReason"
                :disabled="allParams.biding.sourceFrom == 'PURCHASE_REQUEST'"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item
              :label="$t('common.remark')"
              :label-width="formLabelWidth"
              prop="comments"
            >
              <el-input v-model="allParams.biding.comments" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <!-- 申请单号 -->
            <el-form-item
              :label="$t('contractMod.applicationOrderNum')"
              :label-width="formLabelWidth"
              prop="requirementHeadNum"
            >
              <el-input
                v-model="allParams.biding.requirementHeadNum"
                disabled
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <!-- 申请人 -->
            <el-form-item
              :label="$t('purchaseDemand.applicant')"
              :label-width="formLabelWidth"
              prop="applyBy"
            >
              <el-input
                v-model="allParams.biding.applyBy"
                disabled
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <!-- 申请部门 -->
            <el-form-item
              :label="$t('purchaseDemand.ceeaDepartment')"
              :label-width="formLabelWidth"
              prop="applyDepartmentName"
            >
              <el-input
                v-model="allParams.biding.applyDepartmentName"
                disabled
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-collapse-item>
      <el-collapse-item
        :title="$t('bidMod.workGroupList')"
        name="2"
      >
        <p style="margin: 0 0 10px 0">
          <el-button
            type="primary"
            class="detail-pbtn"
            @click="addNewOne"
          >
            {{ $t("bidMod.addByHand") }}
          </el-button>
          <el-button
            type="primary"
            class="detail-pbtn"
            @click="delExpertList"
          >
            {{ $t("common.delete") }}
          </el-button>
          <!-- <el-button
            type="primary"
            @click="importExpertList"
            >从专家库选择</el-button
          > -->
        </p>
        <el-form
          ref="groupForm"
          :model="allParams"
          label-width="80px"
          label-position="top"
          class="form-incontainer"
          :rules="rules"
          :disabled="isdisabledTab"
        >
          <el-table
            :data="allParams.groupList"
            style="width: 100%"
            border
            max-height="250px"
            @selection-change="checkExpertList"
          >
            <el-table-column type="selection" />
            <!-- 序号 -->
            <el-table-column
              :label="$t('common.sort')"
              align="center"
              type="index"
              width="50"
            />
            <el-table-column
              align="center"
              prop="userName"
              :label="$t('bidMod.userName')"
              width="150"
            >
              <template slot-scope="scope">
                <el-form-item
                  :prop="'groupList.' + scope.$index + '.userName'"
                  :rules="rules.userName"
                >
                  <quick-search
                    :show-input="scope.row.userName"
                    show-key="username"
                    :scope-data="scope.row"
                    name="scc_rbac_user_display"
                    @close-quicksearch="getUserObj"
                  />
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="fullName"
              :label="$t('bidMod.fullName')"
              width="150"
              :show-overflow-tooltip="true"
            />
            <el-table-column
              align="center"
              prop="phone"
              :label="$t('bidMod.phone')"
              width="150"
              :show-overflow-tooltip="true"
            />
            <el-table-column
              align="center"
              prop="email"
              :label="$t('bidMod.email')"
              min-width="200"
              :show-overflow-tooltip="true"
            />
            <!-- 岗位 -->
            <el-table-column
              align="center"
              prop="position"
              :label="$t('bidMod.position')"
              width="100"
              :show-overflow-tooltip="true"
            />
            <el-table-column
              align="center"
              prop="judgeFlag"
              :label="$t('bidMod.judgeFlag')"
              width="100"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <el-checkbox
                  v-model="scope.row.judgeFlag"
                  true-label="Y"
                  false-label="N"
                />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="lastUpdatedUserName"
              :label="$t('bidMod.lastUpdatedBy')"
              width="100"
              :show-overflow-tooltip="true"
            />
            <el-table-column
              fixed="right"
              :label="$t('common.operation')"
              width="80"
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
        </el-form>
      </el-collapse-item>
      <!-- 附件信息 -->
      <el-collapse-item
        :title="$t('bidMod.fileInfo')"
        name="3"
      >
        <p style="margin: 0 10px 10px 0;">
          <el-button
            type="primary"
            class="detail-pbtn"
            @click="addOne"
          >
            {{ $t("common.add") }}
          </el-button>
          <el-button
            type="primary"
            class="detail-pbtn"
            @click="delFile1"
          >
            {{ $t("common.delete") }}
          </el-button>
          <span style="vertical-align: bottom; font-size: 12px">{{
            $t("logisticsMod.buyerAttach")
          }}</span>
        </p>
        <el-table
          :data="innerFiles"
          style="width: 100%"
          border
          height="133px"
          @selection-change="checkFileList1"
        >
          <el-table-column type="selection" />
          <!-- 序号 -->
          <el-table-column
            :label="$t('common.sort')"
            align="center"
            type="index"
            width="70"
          />
          <el-table-column
            align="center"
            prop="fileName"
            :label="$t('bidMod.fileName')"
            min-width="150"
          >
            <template slot-scope="scope">
              <SrmCommonFile
                :extra-data="fileInfo"
                :default-file="{
                  fileId: scope.row.docId,
                  fileName: scope.row.fileName
                }"
                :readonly="false"
                @on-change="({file}) => innerHandleUploadSuccess(file,scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="comments"
            :label="$t('bidMod.remark')"
            min-width="100"
          >
            <template slot-scope="scope">
              <el-input v-model="scope.row.comments" />
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="operation"
            :label="$t('bidMod.operation')"
            width="80"
          >
            <template slot-scope="scope">
              <el-button
                type="text"
                @click="deleOne(scope.$index, scope.row)"
              >
                {{ $t("common.delete") }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <p style="margin: 10px 0;">
          <el-button
            type="primary"
            class="detail-pbtn"
            @click="addOne2"
          >
            {{ $t("common.add") }}
          </el-button>
          <el-button
            type="primary"
            class="detail-pbtn"
            @click="delFile2"
          >
            {{ $t("common.delete") }}
          </el-button>
          <span style="vertical-align: bottom; font-size: 12px">{{
            $t("logisticsMod.vendorAttach")
          }}</span>
        </p>
        <el-table
          :data="outerFiles"
          style="width: 100%"
          border
          height="133px"
          @selection-change="checkFileList2"
        >
          <el-table-column type="selection" />
          <!-- 序号 -->
          <el-table-column
            :label="$t('common.sort')"
            align="center"
            type="index"
            width="70"
          />
          <el-table-column
            align="center"
            prop="fileName"
            :label="$t('bidMod.fileName')"
            min-width="150"
          >
            <template slot-scope="scope">
              <SrmCommonFile
                :extra-data="fileInfo"
                :default-file="{
                  fileId: scope.row.docId,
                  fileName: scope.row.fileName
                }"
                :readonly="false"
                @on-change="({file}) => outerHandleUploadSuccess(file,scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="comments"
            :label="$t('bidMod.remark')"
            min-width="100"
          >
            <template slot-scope="scope">
              <el-input v-model="scope.row.comments" />
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="operation"
            :label="$t('bidMod.operation')"
            width="80"
          >
            <template slot-scope="scope">
              <el-button
                type="text"
                @click="deleOne2(scope.$index, scope.row)"
              >
                {{ $t("common.delete") }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <p style="margin: 10px 0;">
          <el-button
            type="primary"
            class="detail-pbtn"
            @click="addOneTem"
          >
            {{ $t("common.add") }}
          </el-button>
          <el-button
            type="primary"
            class="detail-pbtn"
            @click="delFile3"
          >
            {{ $t("common.delete") }}
          </el-button>
          <span style="vertical-align: bottom; font-size: 12px">{{
            $t("logisticsMod.vendorUploadAttach")
          }}</span>
        </p>
        <el-table
          :data="allParams.bidFileConfigList"
          style="width: 100%"
          border
          max-height="250px"
          @selection-change="checkFileList3"
        >
          <el-table-column type="selection" />
          <el-table-column
            :label="$t('common.sort')"
            align="center"
            type="index"
            width="70"
          />
          <!-- 新增文件类型和参考文件上传 [start] by chenzp20 -->
          <el-table-column
            align="center"
            prop="referenceFile"
            :label="$t('bid_mod.referenceFile')"
            width="150"
          >
            <template slot-scope="scope">
              <SrmCommonFile
                :extra-data="fileInfo"
                :default-file="{
                  fileId: scope.row.referenceFileId,
                  fileName: scope.row.referenceFileName
                }"
                :readonly="false"
                @on-change="({file}) => referenceFileUploadSuccess(file,scope.row)"
              />
            </template>
          </el-table-column>
          <!-- <el-table-column
            align="center"
            prop="fileName"
            :label="$t('bidMod.fileQualify')"
            min-width="150"
          >
            <template slot-scope="scope">
              <el-input v-model="scope.row.fileName" />
            </template>
          </el-table-column> -->
          <el-table-column
            align="center"
            prop="referenceFileType"
            :label="$t('bid_mod.referenceFileType')"
            width="100"
          >
            <template slot-scope="scope">
              <DictSelect
                v-model="scope.row.referenceFileType"
                code="REFERENCE_FILE_TYPE"
              />
            </template>
          </el-table-column>
          <!-- 新增文件类型和参考文件上传 [end] by chenzp20 -->
          <el-table-column
            align="center"
            prop="comments"
            :label="$t('bidMod.remark')"
            min-width="150"
          >
            <template slot-scope="scope">
              <el-input v-model="scope.row.comments" />
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="operation"
            :label="$t('bidMod.operation')"
            width="80"
          >
            <template slot-scope="scope">
              <el-button
                type="text"
                @click="deleOneTem2(scope.$index, scope.row)"
              >
                {{ $t("common.delete") }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-collapse-item>
      <!-- 特别说明信息 -->
      <el-collapse-item
        :title="$t('logisticsMod.specialInstructionInfo')"
        name="4"
      >
        <el-row>
          <el-col>
            <!-- 招标说明（内部可见） -->
            <el-form-item
              :label="$t('logisticsMod.bidInstructionInner')"
              :label-width="formLabelWidth"
              prop="internalDesc"
            >
              <el-input
                v-model="allParams.biding.internalDesc"
                type="textarea"
                :autosize="{ minRows: 2, maxRows: 5 }"
              />
            </el-form-item>
          </el-col>
          <el-col>
            <!-- 招标说明（供方可见） -->
            <el-form-item
              :label="$t('logisticsMod.bidInstructionVendor')"
              :label-width="formLabelWidth"
              prop="supplierDesc"
            >
              <el-input
                v-model="allParams.biding.supplierDesc"
                type="textarea"
                :autosize="{ minRows: 2, maxRows: 5 }"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-collapse-item>
      <!-- 报价币种设置 [start] by chnezp20 -->
      <el-collapse-item
        :title="$t('bid_mod.quoteCurrency')"
        name="6"
      >
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item
              :label="$t('bid_mod.standardCurrency')"
              :label-width="formLabelWidth"
              prop="standardCurrency"
            >
              <DictSelect
                v-model="allParams.biding.standardCurrency"
                code="currency"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item
              :label="$t('bid_mod.pricePrecision')"
              :label-width="formLabelWidth"
              prop="pricePrecision"
            >
              <DictSelect
                v-model="allParams.biding.pricePrecision"
                code="PRICE_PRECISION"
                :dict-class="dictClass"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item
              :label="$t('bid_mod.exchangeRateType')"
              :label-width="formLabelWidth"
              prop="exchangeRateType"
            >
              <DictSelect
                v-model="allParams.biding.exchangeRateType"
                code="EXCHANGE_RATE_TYPE"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item
              :label="$t('bid_mod.currencyChangeDate')"
              :label-width="formLabelWidth"
              prop="currencyChangeDate"
            >
              <el-date-picker
                v-model="allParams.biding.currencyChangeDate"
                type="date"
                value-format="timestamp"
                :picker-options="endTiumePickerOptions3"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-collapse-item>
      <!-- 报价币种设置 [end] by chnezp20 -->
      <!-- 向供应商展示的联系方式 -->
      <el-collapse-item
        :title="$t('bidMod.showVendorContactInfo')"
        name="7"
      >
        <el-row type="flex">
          <el-col>
            <el-form-item
              :label="$t('bidMod.bidContactName')"
              :label-width="formLabelWidth"
              prop="bidUserName"
            >
              <el-input v-model="allParams.biding.bidUserName" />
            </el-form-item>
          </el-col>
          <el-col>
            <el-form-item
              :label="$t('bidMod.bidMobilePhone')"
              :label-width="formLabelWidth"
              prop="bidUserPhone"
            >
              <el-input v-model="allParams.biding.bidUserPhone" />
            </el-form-item>
          </el-col>
          <el-col>
            <el-form-item
              :label="$t('bidMod.email')"
              :label-width="formLabelWidth"
              prop="bidUserEmail"
            >
              <el-input v-model="allParams.biding.bidUserEmail" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-collapse-item>
      <!-- 投标控制 -->
      <el-collapse-item
        :title="$t('bidMod.bidingControl')"
        name="8"
      >
        <div class="the_check_groups">
          <h3>{{ $t("bidMod.bidingControl") }}</h3>
          <el-row>
            <el-col
              :span="8"
            >
              <el-checkbox
                v-model="allParams.biding.withdrawBiding"
                true-label="Y"
                false-label="N"
              >
                {{ $t("bidMod.withdrawBiding") }}
              </el-checkbox>
            </el-col>
            <!-- <el-col :span="8"
              ><el-checkbox
                true-label="Y"
                false-label="N"
                v-model="allParams.biding.publicLowestPrice"
                >{{ $t("bidMod.publicLowestPrice") }}</el-checkbox
              ></el-col
            >
            <el-col :span="8"
              ><el-checkbox
                true-label="Y"
                false-label="N"
                v-model="allParams.biding.publicTotalRank"
                >{{ $t("bidMod.publicTotalRank") }}</el-checkbox
              ></el-col
            > -->
            <el-col :span="8" />
          </el-row>
          <h3>{{ $t("bidMod.bidOpenControl") }}</h3>
          <el-row>
            <!-- <el-col :span="8"
              ><el-checkbox
                true-label="Y"
                false-label="N"
                v-model="allParams.biding.visibleRankResult"
                >{{ $t("bidMod.visibleCodeRanking") }}</el-checkbox
              ></el-col
            > -->
            <el-col
              :span="8"
            >
              <el-checkbox
                v-model="allParams.biding.visibleFinalPrice"
                true-label="Y"
                false-label="N"
              >
                {{ $t("bidMod.visibleLowestPrice") }}
              </el-checkbox>
            </el-col>
            <el-col
              :span="8"
            >
              <el-checkbox
                v-model="allParams.biding.visibleWinVendor"
                true-label="Y"
                false-label="N"
              >
                {{ $t("bidMod.visibleWinVendor") }}
              </el-checkbox>
            </el-col>
          </el-row>
        </div>
      </el-collapse-item>
    </el-collapse>
    <!-- 从专家库选择工作小组成员 -->
    <expert-database-dialog
      :expert-database-visible.sync="expertDatabaseVisible"
      :origin-list="allParams.groupList"
      @save="saveExpertToGroupList"
    />
  </el-form>
</template>
<script>
import ExpertDatabaseDialog from './expert-database-dialog'
import QuickSearch from 'lib@/components/QuickSearch'
import { isMobile, isEmail } from 'lib@/utils/validate'
import {
  getDictItemList
} from '@/api/common'
import { adaptDictData, parseTime } from '@/utils'
import { geti18n } from '@/main'
import { createDictClass } from '@/library/utils/dict/dict-utils'
const dictClass = createDictClass({
  'PRICE_PRECISION': [
    { id: 0, label: '0', value: 0 },
    { id: 1, label: '1', value: 1 },
    { id: 2, label: '2', value: 2 },
    { id: 3, label: '3', value: 3 },
    { id: 4, label: '4', value: 4 },
    { id: 5, label: '5', value: 5 },
    { id: 6, label: '6', value: 6 }
  ]
})

const i18n = geti18n()

export default {
  name: 'BiddingProjectDetailInfo',
  components: {
    ExpertDatabaseDialog,
    QuickSearch
  },
  props: [
    'allParams',
    'isdisabledTab',
    'bidProcessConfigIdList',
    'innerFiles',
    'outerFiles'
  ],
  data () {
    return {
      dictClass: dictClass,
      templateAllList: [],
      formLabelWidth: '120px',
      // 文件上传配置信息
      fileInfo: {
        uploadType: 'FASTDFS', // 固定参数
        sourceType: 'WEB_APP', // 固定参数
        fileModular: 'sup', // 文件所属模块 -》基础模块
        fileFunction: 'biddingProject', // 文件所属功能
        fileType: 'images' // 文件所属类型
      },
      taxList: [],
      contractTypeListAll: [],
      contractTypeList: [],
      expertDatabaseVisible: false,
      activeDims: ['1', '2', '3', '4', '5', '6', '7', '8'],
      businessTypeRules: [
        {
          required: false,
          message: this.$t('logisticsMod.msgPurchaseApply[25]')
        }
      ], // 请选择服务项目名称
      allowedVehicleRules: [
        {
          required: false,
          message: this.$t('logisticsMod.msgPurchaseApply[0]')
        } // 请选择项目地可进最大车型
      ],
      vendorReasonRules: [
        {
          required: false,
          message: this.$t('logisticsMod.msgSpeicfySupplierReason')
        }
      ], // 请输入指定供应商原因
      checkExpert: [],
      checkFile1: [],
      checkFile2: [],
      checkFile3: [],
      pickerOptions1: {
        disabledDate: time => {
          const start = new Date()
          return time.getTime() <= start.getTime() - 24 * 60 * 60 * 1000
        }
      },
      pickerOptions2: {
        disabledDate: time => {
          const start = new Date(this.allParams.biding.priceTimeStart)
          return time.getTime() <= start.getTime()
        }
      },
      rules: {
        processConfigId: [
          { required: true, message: this.$t('bidMod.bidMsgList[0]') }
        ], // 请选择模板
        templateCode: [
          {
            required: true,
            message: this.$t('logisticsMod.msgPurchaseApply[1]')
          }
        ], // 请选择申请模板
        businessType: [
          { required: true, message: this.$t('dataConfMod.msgBusinessType') }
        ], // 请选择业务类型
        unitCode: [
          {
            required: true,
            message: this.$t('logisticsMod.msgPurchaseApply[3]')
          }
        ], // 请选择单位
        projectTotal: [
          {
            required: true,
            message: this.$t('logisticsMod.msgPurchaseApply[26]')
          }
        ], // 请输入项目总量
        bidingName: [
          { required: true, message: this.$t('bidMod.bidMsgList[1]') }
        ], // 请输入项目名称
        contractType: [
          {
            required: true,
            message: this.$t('logisticsMod.msgPurchaseApply[4]')
          }
        ], // 请选择合同类型
        priceTimeStart: [
          { required: true, message: this.$t('bidMod.bidMsgList[34]') } // 请选择价格有效期自
        ],
        priceTimeEnd: [
          { required: true, message: this.$t('bidMod.bidMsgList[35]') }
        ],
        enrollEndDatetime: [
          { required: true, message: this.$t('bidMod.bidMsgList[36]') }
        ], // 请选择投标截止时间
        taxKey: [
          {
            required: true,
            message: this.$t('logisticsMod.msgPurchaseApply[21]')
          }
        ],
        bidingAwardWay: [
          { required: true, message: this.$t('bidMod.bidMsgList[8]') }
        ], // 请选择决标方式
        budgetAmount: [
          { required: true, message: this.$t('bidMod.bidMsgList[4]') }
        ], // 请输入预算金额
        targetType: [
          { required: true, message: this.$t('bidMod.bidMsgList[5]') }
        ], // 请选择标的类型
        standardCurrency: [
          { required: true, message: this.$t('bidMod.bidMsgList[10]') }
        ], // 请填写本位币
        exchangeRateType: [
          {
            required: true,
            message: this.$t('logisticsMod.msgPurchaseApply[27]')
          }
        ], // 请选择汇率类型
        pricePrecision: [
          { required: true, message: this.$t('bidMod.bidMsgList[11]') }
        ], // 请填写价格精度
        bondAmount: [
          { required: true, message: this.$t('bidMod.bidMsgList[12]') }
        ], // 请输入保证金金额
        currencyChangeDate: [
          {
            required: true,
            message: this.$t('logisticsMod.msgPurchaseApply[28]')
          }
        ], // 请选择币种转换日期
        userName: [{ required: true }],
        bondEndDatetime: [
          { required: false, message: this.$t('bidMod.bidMsgList[13]') } // 请选择保证金提交截止时间
        ],
        bondMethod: [
          { required: false, message: this.$t('bidMod.bidMsgList[14]') }
        ], // 请选择保证金提交方式
        bankAccountNum: [
          { required: false, message: this.$t('bidMod.bidMsgList[15]') }
        ], // 请输入保证金缴纳账号
        bankAccountName: [
          { required: false, message: this.$t('bidMod.bidMsgList[16]') }
        ], // 请输入账号名称
        bankBranchName: [
          { required: false, message: this.$t('bidMod.bidMsgList[17]') }
        ], // 请输入开户支行
        taxInclusivePrice: [
          {
            required: true,
            message: this.$t('logisticsMod.msgPurchaseApply[29]')
          }
        ], // 请选择投标是否含税
        bidingCurrency: [
          { required: true, message: this.$t('bidMod.bidMsgList[19]') }
        ], // 请选择招标币种
        decimalAccuracy: [
          {
            required: true,
            message: this.$t('logisticsMod.msgPurchaseApply[30]')
          }
        ], // 请输入投标最多保留
        bidUserName: [
          { required: true, message: this.$t('bidMod.bidMsgList[21]') }
        ], // 请输入姓名
        bidingStartDatetime: [
          { required: false, message: this.$t('bidMod.bidMsgList[22]') }
        ],
        orgName: [
          { required: true, message: this.$t('bidMod.bidMsgList[23]') }
        ], // 请选择采购组织
        bidUserEmail: [
          { required: true, message: this.$t('bidMod.bidMsgList[24]') },
          {
            validator: (rule, value, callback) => {
              if (!value) {
                callback(new Error(this.$t('bidMod.bidMsgList[24]')))
              } else if (!isEmail(value)) {
                callback(new Error(this.$t('bidMod.bidMsgList[25]')))
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ],
        bidUserPhone: [
          {
            validator: (rule, value, callback) => {
              if (!value) {
                callback()
              } else if (!isMobile(value)) {
                callback(new Error(this.$t('bidMod.bidMsgList[26]'))) // 手机格式不合法
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ]
      }
    }
  },
  watch: {
    allParams: {
      deep: true,
      immediate: true,
      handler () {
        if (this.allParams.biding.businessModeCode == 'I') {
          this.allowedVehicleRules = [
            {
              required: true,
              message: this.$t('logisticsMod.msgPurchaseApply[0]')
            } // 请选择项目地可进最大车型
          ]
        } else {
          this.allowedVehicleRules = [
            {
              required: false,
              message: this.$t('logisticsMod.msgPurchaseApply[0]')
            } // 请选择项目地可进最大车型
          ]
        }
      }
    }
  },
  mounted () {
    let dictParamsArr = [{ dictCode: 'lgt_contract_type' }]
    getDictItemList(dictParamsArr).then(res => {
      const [lgt_contract_type] = res.data
      this.contractTypeListAll = adaptDictData(
        lgt_contract_type.lgt_contract_type
      )
      // 国内---I,国际---E
      if (this.allParams.biding.businessModeCode === 'I') {
        this.contractTypeList = this.contractTypeListAll.filter(v =>
          ['A', 'B', 'D', 'E', 'F'].includes(v.value)
        )
      } else if (this.allParams.biding.businessModeCode === 'E') {
        this.contractTypeList = this.contractTypeListAll.filter(v =>
          ['A', 'B', 'D', 'G', 'H'].includes(v.value)
        )
      }
    })
    this.$http({
        url:
          '/api-pd/logistics/logistics-template-head/listPageByParam',
        method: 'POST',
        data: {
          pageNum: 1,
          pageSize: 15,
          status: 'EFFECTIVE'
        },
        loading: true
      })
        .then(res => {
          this.templateAllList = res.data.list.map(i => ({
            id: i.templateCode,
            label: i.templateName,
            value: i.templateCode,
            businessModeCode: i.businessModeCode,
            transportModeCode: i.transportModeCode,
            ifVendorSubmitShipDate: i.vendorIfSubmitShip,
            templateHeadId: i.templateHeadId
          }))
        })
        .catch(err => {
          console.log(err)
        })
    this.allParams.biding.pricePrecision = 2
    this.allParams.biding.exchangeRateType = 'COMPANY'
    this.allParams.biding.bidingAwardWay = 'INDIVIDUAL_DECISION'
  },
  methods: {
    chengeTimeStart () {
      this.allParams.biding.priceTimeEnd = null
    },
    validate (callback) {
      this.$refs.form.validate(callback)
    },
    validate1 (callback) {
      this.$refs.groupForm.validate(callback)
    },
    getprocessConfigId (val) {
      this.$emit('getprocessConfigId', val)
    },
    changeTemplateCode (val) {
      const target = this.templateAllList.find(i => i.value == val) || {} // this.dictClass.getDictDetail("TEMPLATE_ALL_LIST", val)
      this.allParams.biding.templateHeadId = target.templateHeadId
      this.allParams.biding.templateName = target.label
      this.allParams.biding.businessModeCode = target.businessModeCode
      this.allParams.biding.transportModeCode = target.transportModeCode
      this.allParams.biding.ifVendorSubmitShipDate =
        target.ifVendorSubmitShipDate
      this.$emit('getTemplateLines', target.templateHeadId)
      // 国内---I,国际---E
      if (this.allParams.biding.businessModeCode === 'I') {
        this.contractTypeList = this.contractTypeListAll.filter(v =>
          ['A', 'B', 'D', 'E', 'F'].includes(v.value)
        )
      } else if (this.allParams.biding.businessModeCode === 'E') {
        this.contractTypeList = this.contractTypeListAll.filter(v =>
          ['A', 'B', 'D', 'G', 'H'].includes(v.value)
        )
      }
      // 12001 工作小组在创建的时候默认添加当前单据创建人，不勾技术评委按钮
      if (this.allParams.groupList.length === 0) {
        const {
          username,
          phone,
          nickname,
          email,
          department
        } = this.$store.state.user.userInfo
        this.allParams.groupList.push({
          userName: username,
          fullName: nickname,
          phone: phone,
          email: email,
          position: department
        })
      }
    },
    changeBusinessType (val) {
      this.allParams.biding.businessType =
        val == 'A' ? 'PROJECT' : 'NOT_PROJECT'
      if (val !== 'A') {
        this.allParams.biding.serviceProjectName = null
        this.allParams.biding.unitCode = null
        this.allParams.biding.projectTotal = null
        this.allParams.biding.bidingName = null
      }
    },
    getProjectObj (val, scope) {
      scope.serviceProjectCode = val ? val.projectCode : ''
      scope.serviceProjectName = val ? val.projectName : ''
      scope.bidingName = val ? val.projectName : ''
      scope.unitCode = val ? val.unit : ''
      scope.projectTotal = val ? val.projectTotal : ''
    },
    taxRateChangeHandel (val, row) {
      const target = this.taxList.find(i => i.key === val)
      row.taxRate = target ? target.value : ''
    },
    getVendorObj4 (val, scope) {
      scope.companyId = val ? val.companyId : ''
      scope.companyCode = val ? val.companyCode : ''
      scope.companyName = val ? val.companyName : ''
      if (scope.companyId) {
        this.vendorReasonRules = [
          {
            required: true,
            message: this.$t('logisticsMod.msgSpeicfySupplierReason')
          } // 请输入指定供应商原因
        ]
      } else {
        this.vendorReasonRules = [
          {
            required: false,
            message: this.$t('logisticsMod.msgSpeicfySupplierReason')
          } // 请输入指定供应商原因
        ]
      }
    },
    addNewOne () {
      this.allParams.groupList.push({
        id: Math.floor(Math.random() * 1000000),
        userName: '',
        fullName: '',
        phone: '',
        email: '',
        position: '',
        maxEvaluateScore: 100,
        confirmedFlag: 'Y',
        confirmeDatetime: new Date(Date.now())
      })
    },
    delExpertList () {
      if (!this.checkExpert.length) {
        this.$message({
          message: this.$t('logisticsMod.msgPurchaseApply[10]'), // 请选择要删除的行
          type: 'error'
        })
      }
      let arr = []
      this.allParams.groupList.map(i => {
        if (
          !this.checkExpert.includes(i.id) &&
          !this.checkExpert.includes(i.groupId)
        ) {
          arr.push(i)
        }
      })
      this.allParams.groupList = arr
    },
    checkExpertList (data) {
      this.checkExpert = data.map(i => i.id || i.groupId)
    },
    // 工作小组从专家库选择 [start] by chezp20
    importExpertList () {
      this.expertDatabaseVisible = true
    },
    saveExpertToGroupList (list) {
      list.forEach(item => {
        this.allParams.groupList.push(item)
      })
    },
    getUserObj (val, scope) {
      scope.userName = val ? val.username : ''
      scope.fullName = val ? val.nickname : ''
      scope.phone = val ? val.phone : ''
      scope.email = val ? val.email : ''
      scope.position = val ? val.department : ''
    },
    // 工作小组从专家库选择 [end] by chezp20
    // 行删除---
    handleDelClick (index, row) {
      this.allParams.groupList.splice(index, 1)
    },
    addOne () {
      this.innerFiles.push({
        id: Math.floor(Math.random() * 1000000),
        fileType: 'Enterprise',
        docId: '',
        fileName: '',
        comments: ''
      })
    },
    addOne2 () {
      this.outerFiles.push({
        id: Math.floor(Math.random() * 1000000),
        fileType: 'Supplier',
        docId: '',
        fileName: '',
        comments: ''
      })
    },
    delFile1 () {
      if (!this.checkFile1.length) {
        this.$message({
          message: this.$t('logisticsMod.msgPurchaseApply[10]'), // 请选择要删除的行
          type: 'error'
        })
      }
      let arr = []
      this.innerFiles.map(i => {
        if (
          !this.checkFile1.includes(i.id) &&
          !this.checkFile1.includes(i.fileId)
        ) {
          arr.push(i)
        }
      })
      this.innerFiles = arr
    },
    delFile2 () {
      if (!this.checkFile2.length) {
        this.$message({
          message: this.$t('logisticsMod.msgPurchaseApply[10]'), // 请选择要删除的行
          type: 'error'
        })
      }
      let arr = []
      this.outerFiles.map(i => {
        if (
          !this.checkFile2.includes(i.id) &&
          !this.checkFile2.includes(i.fileId)
        ) {
          arr.push(i)
        }
      })
      this.outerFiles = arr
    },
    delFile3 () {
      if (!this.checkFile3.length) {
        this.$message({
          message: this.$t('logisticsMod.msgPurchaseApply[10]'),
          type: 'error'
        })
      }
      let arr = []
      this.allParams.bidFileConfigList.map(i => {
        if (
          !this.checkFile3.includes(i.id) &&
          !this.checkFile3.includes(i.requireId)
        ) {
          arr.push(i)
        }
      })
      this.allParams.bidFileConfigList = arr
    },
    checkFileList1 (data) {
      this.checkFile1 = data.map(i => i.id || i.fileId)
    },
    checkFileList2 (data) {
      this.checkFile2 = data.map(i => i.id || i.fileId)
    },
    checkFileList3 (data) {
      this.checkFile3 = data.map(i => i.id || i.requireId)
    },
    innerHandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.docId = fileId.toString()
      row.fileName = fileName
    },
    outerHandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.docId = fileId.toString()
      row.fileName = fileName
    },
    deleOne (index, row) {
      this.innerFiles.splice(index, 1)
    },
    deleOne2 (index, row) {
      this.outerFiles.splice(index, 1)
    },
    addOneTem () {
      this.allParams.bidFileConfigList.push({
        id: Math.floor(Math.random() * 1000000),
        fileName: '',
        comments: '',
        referenceFileType: 'TECHNICAL_BID',
        referenceFileId: '',
        referenceFileName: ''
      })
    },
    // 项目信息 投标附件上传 处理 [start] by chenzp20
    referenceFileUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.referenceFileId = fileId.toString()
      row.referenceFileName = fileName
    },
    deleOneTem2 (index, row) {
      this.allParams.bidFileConfigList.splice(index, 1)
    },
    endTiumePickerOptions3: {
      disabledDate (time) {
        const today = new Date()
        today.setHours(0)
        today.setMinutes(0)
        today.setSeconds(0)
        today.setMilliseconds(0)
        return time.getTime() > today.getTime()
      }
    }
  }
}
</script>
<style scoped></style>
