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
        business-type="REQUIREMENT"
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
              <el-collapse-item ref="aptInfo" :title="$t('purchaseDemand.purAppDetail')" name="1">
                <srm-row>
                  <!-- 需求类型 -->
                  <srm-col>
                    <el-form-item
                      :label="$t('purchaseDemand.demandType')"
                      prop="demandType"
                    >
                      <dict-select
                        v-model="requirementHead.demandType"
                        code="DEMAND_TYPE"
                        :disabled="true"
                        @change="handleTypeChange"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 申请日期 -->
                    <el-form-item
                      prop="applyDate"
                      :label="$t('purchaseDemand.applyDate')"
                    >
                      <el-date-picker
                        v-model="requirementHead.applyDate"
                        disabled
                        type="date"
                        value-format="yyyy-MM-dd"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 申请编号 -->
                    <el-form-item
                      :label="$t('purchaseDemand.requirementHeadNum')"
                    >
                      <el-input v-model="requirementHead.requirementHeadNum" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 申请人 -->
                    <el-form-item
                      :label="$t('purchaseDemand.applicant')"
                    >
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
                  <srm-col>
                    <!-- 申请部门 -->
                    <el-form-item
                      :label="$t('purchaseDemand.ceeaDepartment')"
                      prop="ceeaDepartmentName"
                    >
                      <!--                      <el-input v-model="requirementHead.ceeaDepartmentName" />-->
                      <el-select v-model="requirementHead.ceeaDepartmentName" filterable :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'" @change="setUserObj4">
                        <el-option
                          v-for="item in bumenList2"
                          :key="item.organizationId"
                          :label="item.organizationName"
                          :value="item.organizationName"
                        />
                      </el-select>
                    </el-form-item>
                  </srm-col>
                  <!--                  <srm-col>-->
                  <!--                    <el-form-item-->
                  <!--                      label="是否招标"-->
                  <!--                    >-->
                  <!--                      <DictSelect-->
                  <!--                        v-model="requirementHead.extBidFlag"-->
                  <!--                        code="YES_OR_NO"-->
                  <!--                        disabled-->
                  <!--                      />-->
                  <!--                    </el-form-item>-->
                  <!--                  </srm-col>-->
                  <srm-col v-if="requirementHead.extOrgBuCode == 'JSZX'">
                    <el-form-item
                      label="是否集采物资"
                    >
                      <DictSelect
                        v-model="requirementHead.ceeaPurchaseType"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        code="YES_OR_NO"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="!outerBmpFlag && !$attrs.params.hidden">
                    <el-form-item
                      label="项目编号"
                    >
                      <el-input v-model="requirementHead.purchaseProject" :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'" />
                      <!--                      <QuickSearch-->
                      <!--                        ref="category"-->
                      <!--                        :show-input="requirementHead.purchaseProject"-->
                      <!--                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"-->
                      <!--                        show-key="categoryId"-->
                      <!--                        auto-query-->
                      <!--                        name="scc_base_purchase_category3"-->
                      <!--                        @close-quicksearch="getCategoryObj"-->
                      <!--                      />-->
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      label="板块"
                    >
                      <el-input v-model="requirementHead.extOrgBuName" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      label="公司"
                      prop="orgId"
                    >
                      <OrganizationSelector
                        ref="organizationSelector"
                        v-model="requirementHead.orgId"
                        :parent-id="-1"
                        node-type="OU"
                        :placeholder="$t('common.pleaseSelect')"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        @select="selectHandler"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      label="申请单类型"
                      prop="ceeaPrType"
                    >
                      <DictSelect
                        v-model="requirementHead.ceeaPrType"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        code="application_form_type"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      label="预估总金额"
                      prop="extExpectTotalAmount"
                    >
                      <el-input v-model="requirementHead.extExpectTotalAmount" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="requirementHead.ceeaPrType == 'type2'">
                    <el-form-item
                      label="加急原因"
                      :rules="{
                        required: true,
                        message: '请填写'
                      }"
                    >
                      <el-input v-model="requirementHead.ceeaUrgencyExplain" :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'" />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="!$attrs.params.isMobile" :initCol="1">
                    <!-- 采购申请说明 -->
                    <el-form-item prop="ceeaAppointReason" label="采购申请说明">
                      <!-- <span style="font-size:12px;">
                        <em class="toRequired" style="margin-left:0;">*</em>
                      </span> -->
                      <el-input
                        v-model="requirementHead.ceeaAppointReason"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        type="textarea"
                        :rows="2"
                      />
                    </el-form-item>
                  </srm-col>
                </srm-row>
              </el-collapse-item>
              <!-- 物料明细 -->
              <el-collapse-item ref="itemInfo" :title="$t('purchaseDemand.itemInfo')" name="2">
                <div class="btn_line">
                  <!-- 新增 -->
                  <el-button
                    v-if="requirementHead.auditStatus !== 'APPROVED' && !$attrs.params.isMobile"
                    :disabled="isReadOnly"
                    type="primary"
                    class="detail-pbtn"
                    @click="openDialog"
                  >
                    {{ $t('common.add') }}
                  </el-button>
                  <!-- excel导入 -->
                  <MImport
                    v-if="!$attrs.params.isMobile"
                    ref="import"
                    style="display: inline-block; margin: 0 10px;"
                    title="导入"
                    :disabled="isReadOnly"
                    :up-load-url="iModal.upLoadUrl"
                    :extra-data="extraData"
                    @beforeUpload="beforeUpload"
                    @downloadTemplate="downloadTemplate"
                    @handleSuccess="handleSuccess"
                  />
                  <el-button
                    v-if="requirementHead.auditStatus !== 'APPROVED' && !$attrs.params.isMobile"
                    :disabled="isReadOnly"
                    type="primary"
                    class="detail-pbtn"
                    @click="lookingINV"
                  >
                    查看库存
                  </el-button>
                </div>
                <vxe-table
                  border
                  show-overflow="tooltip"
                  max-height="390"
                  resizable
                  :data="requirementHead.reqLineList"
                  :edit-config="{
                    trigger: 'click',
                    mode: 'cell',
                    autoClear: false
                  }"
                  :scroll-y="{
                    enabled: true,
                    gt: 30
                  }"
                  :edit-rules="validRules"
                >
                  <vxe-column
                    type="seq"
                    width="50"
                    align="center"
                    fixed="left"
                  />
                  <vxe-column
                    v-if="!$attrs.params.isMobile"
                    field="materialCode"
                    title="物资编码"
                    align="center"
                    width="120"
                    fixed="left"
                    :edit-render="{}"
                  >
                    <template #edit="{ row }">
                      <QuickSearch
                        v-if="!isReadOnly && requirementHead.auditStatus !== 'APPROVED'"
                        ref="materialCode"
                        :show-input="row.materialCode"
                        show-key="materialCode"
                        :scope-data="row"
                        name="scc_base_material_item_contract"
                        @close-quicksearch="getCategoryObj"
                      />
                      <span v-else>{{ row.materialCode }}</span>
                    </template>
                    <template #default="{ row }">
                      {{ row.materialCode }}
                    </template>
                  </vxe-column>
                  <vxe-column
                    v-else
                    field="materialCode"
                    title="物资编码"
                    align="center"
                    width="120"
                    :edit-render="{}"
                  >
                    <template #edit="{ row }">
                      <QuickSearch
                        v-if="!isReadOnly && requirementHead.auditStatus !== 'APPROVED'"
                        ref="materialCode"
                        :show-input="row.materialCode"
                        show-key="materialCode"
                        :scope-data="row"
                        name="scc_base_material_item_contract"
                        @close-quicksearch="getCategoryObj"
                      />
                      <span v-else>{{ row.materialCode }}</span>
                    </template>
                    <template #default="{ row }">
                      {{ row.materialCode }}
                    </template>
                  </vxe-column>
                  <vxe-column
                    v-if="!$attrs.params.isMobile"
                    align="center"
                    field="materialName"
                    title="物资名称"
                    min-width="120"
                    :edit-render="{}"
                    fixed="left"
                  >
                    <template #edit="{ row }">
                      {{ row.materialName }}
                    </template>
                    <template #default="{ row }">
                      {{ row.materialName }}
                    </template>
                  </vxe-column>
                  <vxe-column
                    v-else
                    align="center"
                    field="materialName"
                    title="物资名称"
                    min-width="120"
                    :edit-render="{}"
                  >
                    <template #edit="{ row }">
                      {{ row.materialName }}
                    </template>
                    <template #default="{ row }">
                      {{ row.materialName }}
                    </template>
                  </vxe-column>
                  <vxe-column
                    v-if="!$attrs.params.isMobile"
                    align="center"
                    field="categoryName"
                    title="采购品类"
                    min-width="120"
                    :edit-render="{}"
                  >
                    <template #edit="{ row }">
                      {{ row.categoryName }}
                    </template>
                    <template #default="{ row }">
                      {{ row.categoryName }}
                    </template>
                  </vxe-column>
                  <vxe-column
                    align="center"
                    field="extMaterialModel"
                    title="规格型号"
                    min-width="120"
                    :edit-render="{}"
                  >
                    <template #edit="{ row }">
                      {{ row.extMaterialModel }}
                    </template>
                    <template #default="{ row }">
                      {{ row.extMaterialModel }}
                    </template>
                  </vxe-column>
                  <vxe-column
                    align="center"
                    field="brand"
                    title="品牌"
                    min-width="120"
                    :edit-render="{}"
                  >
                    <template #edit="{ row }">
                      <el-input v-if="!isReadOnly && requirementHead.auditStatus !== 'APPROVED'" v-model="row.brand" />
                      <span v-else>{{ row.brand }}</span>
                    </template>
                    <template #default="{ row }">
                      {{ row.brand }}
                    </template>
                  </vxe-column>
                  <vxe-column
                    align="center"
                    field="extUseDepartmentId"
                    title="使用部门"
                    min-width="120"
                    :edit-render="{}"
                  >
                    <template #edit="{ row }">
                      <el-select
                        v-if="!isReadOnly && requirementHead.auditStatus !== 'APPROVED'"
                        v-model="row.extUseDepartmentId"
                        filterable
                        @change="setUserObj3(row)"
                      >
                        <el-option
                          v-for="item in bumenList"
                          :key="item.organizationId"
                          :label="item.organizationName"
                          :value="item.organizationId.toString()"
                        />
                      </el-select>
                      <span v-else>{{ row.extUseDepartmentId ? bumenList.find(item => item.organizationId == row.extUseDepartmentId)?.organizationName : '' }}</span>
                    </template>
                    <template #default="{ row }">
                      {{ row.extUseDepartmentId ? bumenList.find(item => item.organizationId == row.extUseDepartmentId)?.organizationName : '' }}
                    </template>
                  </vxe-column>
                  <vxe-column
                    align="center"
                    field="dmandLineRequest"
                    title="使用人信息"
                    min-width="120"
                    :edit-render="{}"
                  >
                    <template #edit="{ row }">
                      <QuickSearch
                        v-if="!isReadOnly && requirementHead.auditStatus !== 'APPROVED'"
                        :show-input="row.extUserName"
                        show-key="nickname"
                        :scope-data="row"
                        name="scc_rbac_user_display"
                        @close-quicksearch="getCategoryObj1"
                      />
                      <span v-else>{{ row.extUserName }}</span>
                    </template>
                    <template #default="{ row }">
                      {{ row.extUserName }}
                    </template>
                  </vxe-column>
                  <vxe-column
                    align="center"
                    field="extUserPhone"
                    title="使用人联系方式"
                    min-width="120"
                    :edit-render="{}"
                  >
                    <template #edit="{ row }">
                      <el-input v-if="!isReadOnly && requirementHead.auditStatus !== 'APPROVED'" v-model="row.extUserPhone" />
                      <span v-else>{{ row.extUserPhone }}</span>
                    </template>
                    <template #default="{ row }">
                      {{ row.extUserPhone }}
                    </template>
                  </vxe-column>
                  <vxe-column
                    align="center"
                    field="unitCode"
                    title="基本计量单位"
                    min-width="120"
                    :edit-render="{}"
                  >
                    <template #edit="{ row }">
                      {{ row.unitCode ? $getDictLabel('unit', row.unitCode) : '' }}
                    </template>
                    <template #default="{ row }">
                      {{ row.unitCode ? $getDictLabel('unit', row.unitCode) : '' }}
                    </template>
                  </vxe-column>
                  <vxe-column
                    align="center"
                    field="requirementQuantity"
                    :title="$t('purchaseDemand.requirementQuantity')"
                    min-width="90"
                    :edit-render="{}"
                  >
                    <template #edit="{ row, rowIndex }">
                      <el-input-number
                        v-if="!isReadOnly && !(requirementHead.auditStatus === 'APPROVED' && row.applyStatus !== 'RETURNING')"
                        v-model="row.requirementQuantity"
                        :controls="false"
                        :min="0"
                        class="input-number-precision"
                        @change="setTotalAmount2(row, rowIndex)"
                      />
                      <span v-else>{{ row.requirementQuantity }}</span>
                    </template>
                    <template #default="{ row }">
                      {{ row.requirementQuantity }}
                    </template>
                  </vxe-column>
                  <vxe-column
                    v-if="!$attrs.params.isMobile"
                    align="center"
                    field="extPredictPrice"
                    title="预估单价"
                    min-width="100"
                    :edit-render="{}"
                  >
                    <template #edit="{ row, rowIndex }">
                      <el-input
                        v-if="!isReadOnly && row.extProductFlag !== 'Y'"
                        v-model="row.extPredictPrice"
                        @change="setTotalAmount2(row, rowIndex)"
                      />
                      <span v-else>{{ row.extPredictPrice }}</span>
                    </template>
                    <template #default="{ row }">
                      {{ row.extPredictPrice }}
                    </template>
                  </vxe-column>
                  <vxe-column
                    v-if="!$attrs.params.isMobile"
                    align="center"
                    field="extPredictAmount"
                    title="预估总价(CNY)"
                    :edit-render="{}"
                    min-width="150"
                  >
                    <template #edit="{ row, rowIndex }">
                      <span>{{ row.extPredictAmount }}</span>
                    </template>
                    <template #default="{ row }">
                      {{ row.extPredictAmount }}
                    </template>
                  </vxe-column>
                  <vxe-column
                    align="center"
                    field="requirementDate"
                    title="本次需求日期"
                    min-width="150"
                    :edit-render="{}"
                  >
                    <template #edit="{ row, rowIndex }">
                      <el-date-picker
                        v-if="!isReadOnly && !(requirementHead.auditStatus === 'APPROVED' && row.applyStatus !== 'RETURNING')"
                        v-model="row.requirementDate"
                        type="date"
                        format="yyyy-MM-dd"
                        :picker-options="pickerOptions"
                        value-format="yyyy-MM-dd"
                      />
                      <span v-else>{{ row.requirementDate }}</span>
                    </template>
                    <template #default="{ row }">
                      {{ row.requirementDate }}
                    </template>
                  </vxe-column>
                  <vxe-column
                    v-if="!$attrs.params.isMobile"
                    align="center"
                    field="receiveAddress"
                    :title="$t('purchaseDemand.ceeaDeliveryPlaceOut')"
                    min-width="150"
                    :edit-render="{}"
                  >
                    <template #edit="{ row, rowIndex }">
                      <el-select
                        v-if="!isReadOnly && requirementHead.auditStatus !== 'APPROVED'"
                        v-model="row.receiveAddress"
                        filterable
                        @focus="visibleChange(row)"
                        @change="(val) => addressChange(val, row)"
                      >
                        <el-option
                          v-for="item in addressList"
                          :key="item.siteId"
                          :label="item.siteName"
                          :value="item.siteName"
                        />
                      </el-select>
                      <span v-else>{{ row.receiveAddress }}</span>
                    </template>
                    <template #default="{ row }">
                      {{ row.receiveAddress }}
                    </template>
                  </vxe-column>
                  <vxe-column
                    v-if="!$attrs.params.isMobile"
                    align="center"
                    field="extReceiver"
                    title="收货人"
                    min-width="100"
                    :edit-render="{}"
                  >
                    <template #edit="{ row, rowIndex }">
                      <el-input v-if="!isReadOnly && !row.receiveAddress" v-model="row.extReceiver" />
                      <span v-else>{{ row.extReceiver }}</span>
                    </template>
                    <template #default="{ row }">
                      {{ row.extReceiver }}
                    </template>
                  </vxe-column>
                  <vxe-column
                    v-if="!$attrs.params.isMobile"
                    align="center"
                    field="receiveTelephone"
                    title="收货人联系方式"
                    min-width="100"
                    :edit-render="{}"
                  >
                    <template #edit="{ row, rowIndex }">
                      <el-input v-if="!isReadOnly && !row.receiveAddress" v-model="row.receiveTelephone" />
                      <span v-else>{{ row.receiveTelephone }}</span>
                    </template>
                    <template #default="{ row }">
                      {{ row.receiveTelephone }}
                    </template>
                  </vxe-column>
                  <vxe-column
                    v-if="!$attrs.params.isMobile"
                    align="center"
                    field="extAreaCode"
                    title="区域"
                    min-width="100"
                    :edit-render="{}"
                  >
                    <template #edit="{ row, rowIndex }">
                      <dict-select
                        v-if="!isReadOnly && !row.receiveAddress"
                        v-model="row.extAreaCode"
                        code="REGION"
                        @change-value="(value) => extAreaChange(value, row)"
                      />
                      <span v-else>{{ row.extAreaCode ? $getDictLabel('REGION', row.extAreaCode) : '' }}</span>
                    </template>
                    <template #default="{ row }">
                      {{ row.extAreaCode ? $getDictLabel('REGION', row.extAreaCode) : '' }}
                    </template>
                  </vxe-column>
                  <vxe-column
                    align="center"
                    field="extUseTo"
                    title="用途"
                    min-width="100"
                    :edit-render="{}"
                  >
                    <template #edit="{ row, rowIndex }">
                      <el-input v-if="!isReadOnly" v-model="row.extUseTo" />
                      <span v-else>{{ row.extUseTo }}</span>
                    </template>
                    <template #default="{ row }">
                      {{ row.extUseTo }}
                    </template>
                  </vxe-column>
                  <vxe-column
                    align="center"
                    field="extFeeSubject"
                    title="费用科目"
                    min-width="100"
                    :edit-render="{}"
                  >
                    <template #edit="{ row, rowIndex }">
                      <el-input v-if="!isReadOnly" v-model="row.extFeeSubject" />
                      <span v-else>{{ row.extFeeSubject }}</span>
                    </template>
                    <template #default="{ row }">
                      {{ row.extFeeSubject }}
                    </template>
                  </vxe-column>
                  <vxe-column
                    align="center"
                    field="comments"
                    title="备注信息"
                    min-width="100"
                    :edit-render="{}"
                  >
                    <template #edit="{ row, rowIndex }">
                      <el-input v-if="!isReadOnly && requirementHead.auditStatus !== 'APPROVED'" v-model="row.comments" />
                      <span v-else>{{ row.comments }}</span>
                    </template>
                    <template #default="{ row }">
                      {{ row.comments }}
                    </template>
                  </vxe-column>
                  <vxe-column
                    v-if="!$attrs.params.isMobile"
                    align="center"
                    field="extProductFlag"
                    title="是否商品"
                    min-width="100"
                  >
                    <template #default="{ row }">
                      {{ row.extProductFlag ? $getDictLabel('YES_OR_NO', row.extProductFlag) : '' }}
                    </template>
                  </vxe-column>
                  <vxe-column
                    v-if="!$attrs.params.isMobile"
                    align="center"
                    field="extActualStock"
                    title="实时库存"
                    min-width="100"
                  >
                    <template #default="{ row }">
                      {{ row.extActualStock }}
                    </template>
                  </vxe-column>
                  <vxe-column
                    v-if="!$attrs.params.isMobile"
                    align="center"
                    field="extShareStock"
                    title="共享库存数量"
                    min-width="100"
                  >
                    <template #default="{ row }">
                      {{ row.extShareStock }}
                    </template>
                  </vxe-column>
                  <vxe-column
                    v-if="!$attrs.params.isMobile"
                    align="center"
                    field="extAttach"
                    title="附件"
                    min-width="100"
                    :edit-render="{}"
                  >
                    <template #edit="{ row }">
                      <SrmCommonFile
                        :extra-data="fileInfo"
                        :default-file="{
                          fileId: row.extAttachId,
                          fileName: row.extAttachName
                        }"
                        :readonly="isReadOnly"
                        @on-change="({file}) => outerHandleUploadSuccess2(file, row)"
                      />
                    </template>
                    <template #default="{ row }">
                      <SrmCommonFile
                        :extra-data="fileInfo"
                        :default-file="{
                          fileId: row.extAttachId,
                          fileName: row.extAttachName
                        }"
                        :readonly="true"
                      />
                    </template>
                  </vxe-column>
                  <vxe-column
                    v-if="['DRAFT', 'REJECTED', 'WITHDRAW'].includes(requirementHead.auditStatus) && !$attrs.params.isMobile"
                    :title="$t('common.operation')"
                    width="60"
                    fixed="right"
                  >
                    <template #default="{ row, rowIndex }">
                      <el-button :disabled="isReadOnly" type="text" @click="deleteOneContent(rowIndex, row)">
                        {{ $t('common.delete') }}
                      </el-button>
                    </template>
                  </vxe-column>
                </vxe-table>
                <el-table
                  v-if="false"
                  ref="materialDetailRef"
                  v-loading="tableLoading"
                  :data="requirementHead.reqLineList"
                  style="width: 100%"
                  border
                  max-height="390px"
                  highlight-current-row
                  @current-change="handlerCurrentChange"
                  @selection-change="handleItemSelection2"
                >
                  <el-table-column
                    align="center"
                    type="index"
                    :label="$t('contractMod.tabindex')"
                    width="50"
                    fixed="left"
                  />
                  <el-table-column
                    v-if="!$attrs.params.isMobile"
                    align="center"
                    prop="materialCode"
                    label="物资编码"
                    width="120"
                    fixed="left"
                    :show-overflow-tooltip="true"
                  >
                    <template slot="header" slot-scope="scope">
                      <em class="toRequired">*</em>物资编码
                    </template>
                    <template slot-scope="scope">
                      <template v-if="!isReadOnly && requirementHead.auditStatus !== 'APPROVED' && scope.row.editable">
                        <el-form-item
                          prop="materialCode"
                        >
                          <QuickSearch
                            ref="materialCode"
                            :show-input="scope.row.materialCode"
                            show-key="materialCode"
                            :scope-data="scope.row"
                            name="scc_base_material_item_contract"
                            @close-quicksearch="getCategoryObj"
                          />
                        </el-form-item>
                      </template>
                      <span v-else>{{ scope.row.materialCode }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column
                    v-else
                    align="center"
                    prop="materialCode"
                    label="物资编码"
                    width="120"
                    :show-overflow-tooltip="true"
                  >
                    <template slot="header" slot-scope="scope">
                      <em class="toRequired">*</em>物资编码
                    </template>
                    <template slot-scope="scope">
                      <template v-if="!isReadOnly && requirementHead.auditStatus !== 'APPROVED' && scope.row.editable">
                        <el-form-item
                          prop="materialCode"
                        >
                          <QuickSearch
                            ref="materialCode"
                            :show-input="scope.row.materialCode"
                            show-key="materialCode"
                            :scope-data="scope.row"
                            name="scc_base_material_item_contract"
                            @close-quicksearch="getCategoryObj"
                          />
                        </el-form-item>
                      </template>
                      <span v-else>{{ scope.row.materialCode }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column
                    v-if="!$attrs.params.isMobile"
                    align="center"
                    prop="materialName"
                    label="物资名称"
                    min-width="130"
                    fixed="left"
                    :show-overflow-tooltip="true"
                  >
                    <template slot="header" slot-scope="scope">
                      <em class="toRequired">*</em>物资名称
                    </template>
                  </el-table-column>
                  <el-table-column
                    v-else
                    align="center"
                    prop="materialName"
                    label="物资名称"
                    min-width="130"
                    :show-overflow-tooltip="true"
                  >
                    <template slot="header" slot-scope="scope">
                      <em class="toRequired">*</em>物资名称
                    </template>
                  </el-table-column>
                  <el-table-column
                    v-if="!$attrs.params.hidden"
                    align="center"
                    prop="categoryName"
                    label="采购品类"
                    min-width="200"
                  >
                    <template slot="header" slot-scope="scope">
                      <em class="toRequired">*</em>采购品类
                    </template>
                  </el-table-column>
                  <el-table-column
                    align="center"
                    prop="extMaterialModel"
                    width="100"
                    :show-overflow-tooltip="true"
                  >
                    <template #header>
                      <em class="toRequired">*</em>规格型号
                    </template>
                  </el-table-column>
                  <el-table-column
                    align="center"
                    prop="brand"
                    label="品牌"
                    width="100"
                  >
                    <template slot-scope="scope">
                      <el-input
                        v-if="!isReadOnly && requirementHead.auditStatus !== 'APPROVED' && scope.row.editable"
                        v-model="scope.row.brand"
                      />
                      <span v-else>{{ scope.row.brand }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column
                    align="center"
                    prop="extUseDepartmentId"
                    label="使用部门"
                    width="150"
                  >
                    <template slot="header" slot-scope="scope">
                      <em class="toRequired">*</em>使用部门
                    </template>
                    <template slot-scope="scope">
                      <el-select
                        v-if="!isReadOnly && requirementHead.auditStatus !== 'APPROVED' && scope.row.editable"
                        v-model="scope.row.extUseDepartmentId"
                        filterable
                        @change="setUserObj3(scope.row)"
                      >
                        <el-option
                          v-for="item in bumenList"
                          :key="item.organizationId"
                          :label="item.organizationName"
                          :value="item.organizationId.toString()"
                        />
                      </el-select>
                      <span v-else>{{ scope.row.extUseDepartmentId ? bumenList.find(item => item.organizationId == scope.row.extUseDepartmentId)?.organizationName : '' }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column
                    align="center"
                    prop="dmandLineRequest"
                    label="使用人信息"
                    width="150"
                    :show-overflow-tooltip="true"
                  >
                    <template slot="header" slot-scope="scope">
                      <em class="toRequired">*</em>使用人信息
                    </template>
                    <template slot-scope="scope">
                      <QuickSearch
                        v-if="!isReadOnly && requirementHead.auditStatus !== 'APPROVED' && scope.row.editable"
                        :show-input="scope.row.extUserName"
                        show-key="nickname"
                        :scope-data="scope"
                        name="scc_rbac_user_display"
                        @close-quicksearch="getCategoryObj1"
                      />
                      <span v-else> {{ scope.row.extUserName }} </span>
                    </template>
                  </el-table-column>
                  <el-table-column
                    align="center"
                    prop="extUserPhone"
                    label="使用人联系方式"
                    width="130"
                  >
                    <template slot="header" slot-scope="scope">
                      <em class="toRequired">*</em>使用人联系方式
                    </template>
                    <template slot-scope="scope">
                      <el-input
                        v-if="!isReadOnly && requirementHead.auditStatus !== 'APPROVED' && scope.row.editable"
                        v-model="scope.row.extUserPhone"
                      />
                      <span v-else>{{ scope.row.extUserPhone }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column
                    align="center"
                    prop="unitCode"
                    label="基本计量单位"
                    width="130"
                    :formatter="row => {
                      return row.unitCode ? $getDictLabel('unit', row.unitCode) : ''
                    }"
                  >
                    <template slot="header" slot-scope="scope">
                      <em class="toRequired">*</em>基本计量单位
                    </template>
                  </el-table-column>
                  <!-- 需求数量 -->
                  <el-table-column
                    align="center"
                    prop="requirementQuantity"
                    :label="$t('purchaseDemand.requirementQuantity')"
                    width="90"
                  >
                    <template slot="header" slot-scope="scope">
                      <em class="toRequired">*</em>{{ $t('purchaseDemand.requirementQuantity') }}
                    </template>
                    <template slot-scope="scope">
                      <el-form-item
                        :prop="'reqLineList.' + scope.$index + '.requirementQuantity'"
                        :rules="rules.requirementQuantity"
                      >
                        <el-input-number
                          v-if="!isReadOnly && scope.row.editable && !(requirementHead.auditStatus === 'APPROVED' && scope.row.applyStatus !== 'RETURNING')"
                          v-model="scope.row.requirementQuantity"
                          :controls="false"
                          :min="0"
                          class="input-number-precision"
                          @change="setTotalAmount2(scope.row, scope.$index)"
                        />
                        <span v-else>{{ scope.row.requirementQuantity }}</span>
                      </el-form-item>
                    </template>
                  </el-table-column>
                  <el-table-column
                    v-if="!$attrs.params.isMobile"
                    align="center"
                    prop="extPredictPrice"
                    label="预估单价"
                    width="100"
                  >
                    <template slot="header" slot-scope="scope">
                      <em class="toRequired">*</em>预估单价
                    </template>
                    <template slot-scope="scope">
                      <el-input
                        v-if="!isReadOnly && scope.row.extProductFlag !== 'Y' && scope.row.editable"
                        v-model="scope.row.extPredictPrice"
                        @change="setTotalAmount2(scope.row, scope.$index)"
                      />
                      <span v-else>{{ scope.row.extPredictPrice }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column
                    v-if="!$attrs.params.isMobile"
                    align="center"
                    prop="extPredictAmount"
                    label="预估总价(CNY)"
                    width="150"
                    :show-overflow-tooltip="false"
                  >
                    <template slot="header" slot-scope="scope">
                      <em class="toRequired">*</em>预估总价(CNY)
                    </template>
                  </el-table-column>
                  <el-table-column
                    align="center"
                    prop="requirementDate"
                    label="本次需求日期"
                    width="150"
                  >
                    <template slot="header" slot-scope="scope">
                      <em class="toRequired">*</em>本次需求日期
                    </template>
                    <template slot-scope="scope">
                      <el-form-item
                        :prop="'reqLineList.' + scope.$index + '.requirementDate'"
                        :rules="rules.requirementDate"
                      >
                        <el-date-picker
                          v-if="!isReadOnly && scope.row.editable && !(requirementHead.auditStatus === 'APPROVED' && scope.row.applyStatus !== 'RETURNING')"
                          v-model="scope.row.requirementDate"
                          type="date"
                          format="yyyy-MM-dd"
                          :picker-options="pickerOptions"
                          value-format="yyyy-MM-dd"
                        />
                        <span v-else>{{ scope.row.requirementDate }}</span>
                      </el-form-item>
                    </template>
                  </el-table-column>
                  <!-- 收货地址 -->
                  <el-table-column
                    v-if="!$attrs.params.isMobile"
                    align="center"
                    prop="receiveAddress"
                    :label="$t('purchaseDemand.ceeaDeliveryPlaceOut')"
                    width="150"
                  >
                    <template slot="header" slot-scope="scope">
                      <em class="toRequired">*</em>{{ $t('purchaseDemand.ceeaDeliveryPlaceOut') }}
                    </template>
                    <template slot-scope="scope">
                      <el-select
                        v-if="!isReadOnly && requirementHead.auditStatus !== 'APPROVED' && scope.row.editable"
                        v-model="scope.row.receiveAddress"
                        filterable
                        @focus="visibleChange(scope.row)"
                        @change="(val) => addressChange(val, scope.row)"
                      >
                        <el-option
                          v-for="item in addressList"
                          :key="item.siteId"
                          :label="item.siteName"
                          :value="item.siteName"
                        />
                      </el-select>
                      <span v-else>{{ scope.row.receiveAddress }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column
                    v-if="!$attrs.params.isMobile"
                    align="center"
                    prop="extReceiver"
                    label="收货人"
                    width="100"
                  >
                    <template slot="header" slot-scope="scope">
                      <em class="toRequired">*</em>收货人
                    </template>
                    <template slot-scope="scope">
                      <el-input
                        v-if="!isReadOnly && !scope.row.receiveAddress && scope.row.editable"
                        v-model="scope.row.extReceiver"
                      />
                      <span v-else>{{ scope.row.extReceiver }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column
                    v-if="!$attrs.params.isMobile"
                    align="center"
                    prop="receiveTelephone"
                    label="收货人联系方式"
                    width="100"
                  >
                    <template slot="header" slot-scope="scope">
                      <em class="toRequired">*</em>收货人联系方式
                    </template>
                    <template slot-scope="scope">
                      <el-input
                        v-if="!isReadOnly && !scope.row.receiveAddress && scope.row.editable"
                        v-model="scope.row.receiveTelephone"
                      />
                      <span v-else>{{ scope.row.receiveTelephone }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column
                    v-if="!$attrs.params.isMobile"
                    align="center"
                    prop="extAreaCode"
                    label="区域"
                    width="150"
                  >
                    <template slot="header" slot-scope="scope">
                      <em class="toRequired">*</em>区域
                    </template>
                    <template slot-scope="scope">
                      <dict-select
                        v-if="!isReadOnly && !scope.row.receiveAddress && scope.row.editable"
                        v-model="scope.row.extAreaCode"
                        code="REGION"
                        @change-value="(value) => extAreaChange(value, scope.row)"
                      />
                      <span v-else>{{ scope.row.extAreaCode ? $getDictLabel('REGION', scope.row.extAreaCode) : '' }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column
                    align="center"
                    prop="extUseTo"
                    label="用途"
                    width="100"
                    show-overflow-tooltip
                  >
                    <template slot="header" slot-scope="scope">
                      <em class="toRequired">*</em>用途
                    </template>
                    <template slot-scope="scope">
                      <el-input
                        v-if="!isReadOnly && scope.row.editable"
                        v-model="scope.row.extUseTo"
                      />
                      <span v-else>{{ scope.row.extUseTo }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column
                    align="center"
                    prop="extFeeSubject"
                    label="费用科目"
                    width="100"
                    show-overflow-tooltip
                  >
                    <!--                    <template slot="header" slot-scope="scope">-->
                    <!--                      <em class="toRequired">*</em>费用科目-->
                    <!--                    </template>-->
                    <template slot-scope="scope">
                      <el-input
                        v-if="!isReadOnly && scope.row.editable"
                        v-model="scope.row.extFeeSubject"
                      />
                      <span v-else>{{ scope.row.extFeeSubject }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column
                    align="center"
                    prop="comments"
                    label="备注信息"
                    width="150"
                    show-overflow-tooltip
                  >
                    <!-- <template slot="header" slot-scope="scope">
                      <em class="toRequired">*</
                      >{{ $t("purchaseDemand.comments") }}
                    </template> -->
                    <template slot-scope="scope">
                      <el-form-item :prop="'reqLineList.' + scope.$index + '.comments'">
                        <el-input
                          v-if="!isReadOnly && requirementHead.auditStatus !== 'APPROVED' && scope.row.editable"
                          v-model="scope.row.comments"
                        />
                        <span v-else>{{ scope.row.comments }}</span>
                      </el-form-item>
                    </template>
                  </el-table-column>
                  <el-table-column
                    v-if="!$attrs.params.isMobile"
                    align="center"
                    prop="extProductFlag"
                    label="是否商品"
                    width="150"
                    :formatter="row => {
                      return row.extProductFlag ? $getDictLabel('YES_OR_NO', row.extProductFlag) : ''
                    }"
                  />
                  <el-table-column
                    v-if="!$attrs.params.isMobile"
                    align="center"
                    prop="extActualStock"
                    label="实时库存"
                    width="150"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    v-if="!$attrs.params.isMobile"
                    align="center"
                    prop="extShareStock"
                    label="共享库存数量"
                    width="150"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    v-if="!$attrs.params.isMobile"
                    align="center"
                    prop="extAttach"
                    label="附件"
                    min-width="150"
                  >
                    <template slot-scope="scope">
                      <SrmCommonFile
                        :extra-data="fileInfo"
                        :default-file="{
                          fileId: scope.row.extAttachId,
                          fileName: scope.row.extAttachName
                        }"
                        :readonly="isReadOnly || !scope.row.editable"
                        @on-change="({file}) => outerHandleUploadSuccess2(file,scope.row)"
                      />
                    </template>
                  </el-table-column>
                  <!-- 删除 -->
                  <el-table-column
                    v-if="['DRAFT', 'REJECTED', 'WITHDRAW'].includes(requirementHead.auditStatus) && !$attrs.params.isMobile"
                    :label="$t('common.operation')"
                    width="60"
                    fixed="right"
                  >
                    <template slot-scope="scope">
                      <el-button :disabled="isReadOnly" type="text" @click="deleteOneContent(scope.$index, scope.row)">
                        {{ $t('common.delete') }}
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <el-pagination
                  style="margin-top: 2px"
                  :current-page="detailPag.currentPage"
                  :page-size="detailPag.pageSize"
                  :page-sizes="[15, 30, 50, 100, 300, 600]"
                  layout="sizes, total, prev, pager, next"
                  :total="allreqLineList.length"
                  @current-change="handleCurrentChange"
                  @size-change="sizeChangeHandle"
                />
              </el-collapse-item>
              <!--附件上传-->
              <el-collapse-item
                v-if="!$attrs.params.isMobile"
                :title="$t('purSettlementMod.addUploadFile')"
                name="3"
              >
                <p class="btn_line">
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
                </p>
                <el-table
                  :data="reqAttachList"
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
                          fileName: scope.row.attachName
                        }"
                        :readonly="false"
                        @on-change="({file}) => outerHandleUploadSuccess(file,scope.row)"
                      />
                    </template>
                  </el-table-column>
                  <!-- 上传人 -->
                  <el-table-column
                    align="center"
                    prop="createdBy"
                    :label="$t('purchaseDemand.attachmentCreatedBy')"
                    :show-overflow-tooltip="false"
                  />
                  <!-- 账号 -->
                  <el-table-column
                    align="center"
                    prop="createdUserName"
                    :label="$t('vendorMod.account')"
                    :show-overflow-tooltip="false"
                  />
                  <!-- 上传时间 -->
                  <el-table-column
                    align="center"
                    prop="creationDate"
                    :label="$t('purchaseDemand.attachmentCreatedDate')"
                    :show-overflow-tooltip="false"
                  />
                  <el-table-column :label="$t('common.operation')" width="60">
                    <template slot-scope="scope">
                      <el-button
                        type="text"
                        :disabled="isReadOnly || requirementHead.auditStatus === 'APPROVED'"
                        @click="handleDelClick(scope.$index, scope.row)"
                      >
                        {{ $t('common.delete') }}
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
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
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import { getToken } from '@/utils/auth'
import axios from 'axios'
import { sysPrefix } from '@/config/ipConfig'
import { purchaseApplicationApi } from 'modc@/buyer/purchasingDemand/api'
import MaterialSelectDialog from 'modc@/buyer/purchasingDemand/views/purchaseApplication/components/materialSelectDialog'
import CCategorySelect from 'lib@/components/c-category-select'
import { transformMQL } from '@/library/utils/util'
import ApprovalProcess from 'modc@/components/approval-process'
import { getMenuInfo } from '@/utils/menu-auth'

export default {
  name: 'PurchaseApplicationDetail',
  components: {
    MImport,
    QuickSearch,
    CPagination,
    OrganizationSelector,
    MaterialSelectDialog,
    CCategorySelect,
    ApprovalProcess
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      selectFileData: [],
      newReqLineList: [],
      addressList: [],
      bumenList: [],
      bumenList2: [],
      reqLineListDelete: [],
      reqAttachListDelete: [],
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
        upLoadUrl: '/api-sup-ce/purchaseRequirement/import'
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
      requirementHead: {
        extOrgBuCode: '',
        demandDepartmentCode: null, // 部门
        applyByNickname: null, // 申请人
        extBidFlag: 'N',
        budgetManagementNum: null,
        extOrgBuName: null,
        demandType: null,
        reqLineList: [],
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
      allreqLineList: [],
      activeDims: ['1', '2', '3'],
      rules: {
        ceeaAppointReason: [{ required: true, message: this.$t('cusEntry.tipMessage.ceeaAppointReasonMsg') }],
        ceeaDepartmentName: [{ required: true, message: '请填写' }],
        ceeaPrType: [{ required: true, message: '请填写' }],
        vendorName: [{ required: true, message: this.$t('purchaseDemand.selectVendor') }], // 指定供应商
        receiveAddress: [
          { required: true, message: this.$t('purchaseDemand.selectReceiveAddress') }
        ], // 收货地址（仅收货地点）
        requirementDate: [{ required: true, message: this.$t('purchaseDemand.selectRequireDate') }], // 需求日期
        requirementQuantity: [
          { required: true, message: this.$t('purchaseDemand.selectRequireQuantity') }
        ], // 需求数量
        orgId: [{ required: true, message: '请选择公司' }], // 请选择业务实体
        organizationId: [
          {
            required: true,
            message: this.$t('purchaseDemand.organizationIdTips')
          }
        ], // 请选择库存组织
        ceeaPurchaseType: [
          {
            required: true,
            message: this.$t('purchaseDemand.purchaseTypeTips')
          }
        ], // 请输入采购类型
        categoryName: [
          {
            required: true,
            message: this.$t('purchaseDemand.inputCategoryName')
          } // 请输入物料大类
        ],
        acceptUserName: [
          {
            required: true,
            message: this.$t('purchaseDemand.acceptUserNameTips')
          }
        ], // 请输入验收人
        ceeaUrgencyExplain: [
          {
            required: true,
            message: this.$t('purchaseDemand.ceeaUrgencyExplainTips')
          }
        ], // 请输入紧急情况说明
        demandType: [
          {
            required: true,
            message: this.$t('purchaseDemand.selectRequireType')
          }
        ] // 请选择需求类型
        // ceeaAppointReason: [
        //   {
        //     validator: (rule, value, callback) => {
        //       const { ceeaPurchaseType, ceeaAppointReason } = this.requirementHead
        //       if (ceeaPurchaseType === 'APPOINT' && !ceeaAppointReason) {
        //         callback(new Error(this.$t('purchaseDemand.ceeaAppointReasonTips')))
        //       } else {
        //         callback()
        //       }
        //     }
        //   }
        // ]
      },
      isApprovalOnly: this.$attrs.params.flag === 'approvalOnly',
      dialogVisible: false,
      displayItemTable: [],
      reqAttachList: [],
      multipleSelection: [],
      multipleSelection2: [],
      Viewflag: '',
      messagePage: false,
      tableLoading: false,
      operationPreOptions: {
        nextStep: this.preNextStepHandler
      },
      outerBmpFlag: false, // 是否外部链接跳转
      validRules: {
        materialCode: [{ required: true }],
        materialName: [{ required: true }],
        categoryName: [{ required: true }],
        extMaterialModel: [{ required: true }],
        extUseDepartmentId: [{ required: true }],
        dmandLineRequest: [{ required: true }],
        extUserPhone: [{ required: true }],
        unitCode: [{ required: true }],
        requirementQuantity: [{ required: true }],
        extPredictPrice: [{ required: true }],
        extPredictAmount: [{ required: true }],
        requirementDate: [{ required: true }],
        receiveAddress: [{ required: true }],
        extReceiver: [{ required: true }],
        receiveTelephone: [{ required: true }],
        extAreaCode: [{ required: true }],
        extUseTo: [{ required: true }]
      }
    }
  },
  computed: {
    hideReSubmit () {
      return this.allreqLineList.find(v => v.applyStatus === 'RETURNING')
    },
    importAbled () {
      return (
        this.requirementHead.auditStatus === 'APPROVED' ||
        !this.requirementHead.categoryId ||
        !this.requirementHead.organizationId ||
        !this.requirementHead.orgId
      )
    },
    workflowBusinessId () {
      return this.requirementHead ? this.requirementHead.requirementHeadId : null
    },
    isReadOnly () {
      return !['add', 'edit'].includes(this.$attrs.params.flag)
    }
  },
  watch: {
    'requirementHead.reqLineList': {
      handler (value) {
        console.log(value, 'listValue')
        if (!value || value.length == 0) {
          return false
        }
        if (this.allreqLineList.length <= 15) {
          this.allreqLineList = value
        } else if (this.allreqLineList.length > 0) {
          const currentPage = parseInt(this.detailPag.currentPage)
          const hangNum = (currentPage - 1) * 15
          value.forEach((data, index) => {
            this.$set(this.allreqLineList, index + hangNum, data)
          })
        }
      },
      immediate: true,
      deep: true
    }
  },
  created () {
    this.Viewflag = this.$attrs.params.flag
    const { nickname, ceeaDeptId, department, ceeaEmpNo, userId, username } = this.$store.getters.userInfo
    console.log(this.$store.getters.userInfo, 'userInfo')
    this.requirementHead.createdFullName = nickname
    this.requirementHead.applyByNickname = nickname
    this.requirementHead.applyById = userId
    this.requirementHead.applyBy = username
    this.$set(this.requirementHead, 'applyCode', ceeaEmpNo)
    this.$set(this.requirementHead, 'demandType', this.$attrs.params?.demandType)
    if (this.$attrs.params.flag === 'add') {
      this.$http({
        url: `/api-pj/pj-anon/user/getHrUserOrgnizationByUsername?username=${this.$store.getters.userInfo.username}`,
        method: 'GET',
        loading: true
      }).then((res) => {
        const data = res.data
        this.$set(this.requirementHead, 'ceeaDepartmentName', data.departmentOrganization?.organizationName)
        this.$set(this.requirementHead, 'ceeaDepartmentId', data.departmentOrganization?.organizationId)
        this.$set(this.requirementHead, 'ceeaDepartmentCode', data.departmentOrganization?.organizationCode)
        this.$set(this.requirementHead, 'orgName', data.ouOrganization?.organizationName)
        this.$set(this.requirementHead, 'orgId', data.ouOrganization?.organizationId)
        this.$set(this.requirementHead, 'orgCode', data.ouOrganization?.organizationCode)
        this.$set(this.requirementHead, 'extOrgBuName', data.buOrganization?.organizationName)
        this.$set(this.requirementHead, 'extOrgBuId', data.buOrganization?.organizationId)
        this.$set(this.requirementHead, 'extOrgBuCode', data.buOrganization?.organizationCode)
        this.bumenListFun()
      })
    } else {
      this.getFormDetail(this.$attrs.params.row.requirementHeadId)
    }
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
      downloadFileLinkByPost('/api-file/bid/batchDownloadToZip', null, { fileIdList: selectData.map(item => item.fileuploadId) }).then(res => {
        this.$message.success(this.$t('cusEntry.tipMessage.dowmloadSuccess'))
      })
    },
    handlerCurrentChange (currentRow, oldCurrentRow) {
      if (JSON.stringify(currentRow) != JSON.stringify(oldCurrentRow)) {
        this.$set(currentRow, 'editable', true)
        oldCurrentRow && this.$set(oldCurrentRow, 'editable', false)
      }
    },
    addressChange (value, row) {
      const val = this.addressList.find(v => v.siteName === row?.receiveAddress) || {}
      this.$set(row, 'extReceiver', val?.receiver)
      this.$set(row, 'receiveTelephone', val.receiverPhone)
      this.$set(row, 'extAreaCode', val?.addressRegion)
      this.extAreaChange(val?.addressRegion, row)
    },
    lookingINV () {
      let attr = []
      let reqLineList = this.allreqLineList
      reqLineList.forEach(e => {
        attr.push(e.materialCode)
      })
      const obj = {
        orgCode: this.requirementHead?.orgCode,
        materialCodes: attr
      }
      this.$http({
        url: '/api-sup-ce/prShareStock/getStock',
        method: 'POST',
        data: obj,
        loading: true
      })
        .then(data => {
          const datas = data.data
          datas.forEach(e => {
            reqLineList.forEach((u, index) => {
              if (e.materialCode == u.materialCode) {
                this.$set(this.allreqLineList[index], 'extShareStock', e?.shareStock || 0)
                this.$set(this.allreqLineList[index], 'extActualStock', e?.actualStock || 0)
              }
            })
          })
          this.handleCurrentChange(1)
        })
        .catch(err => {
          console.log(err)
        })
    },
    visibleChange (row) {
      const extUseDepartmentId = row.extUseDepartmentId
      if (!extUseDepartmentId) {
        return false
      }
      this.$http({
        url: '/api-base/orgQuery/getOrgAddress?orgId=' + extUseDepartmentId,
        method: 'GET',
        loading: true
      })
        .then(data => {
          const datas = data.data
          this.addressList = datas
        })
        .catch(err => {
          console.log(err)
        })
    },
    bumenListFun () {
      // this.requirementHead.orgId
      // '439621845774337'
      const saveData = {
        existsOwner: true,
        parentId: this.requirementHead.orgId
      }
      this.$http({
        url: '/api-base/orgQuery/getSubOrgs',
        method: 'POST',
        data: saveData,
        loading: true
      })
        .then(res => {
          const data = res.data
          this.bumenList = data
        })
        .catch(err => {
          console.log(err)
        })
      const saveData2 = {
        parentId: this.requirementHead.orgId,
        type: 'DEP'
      }
      this.$http({
        url: '/api-base/orgQuery/getSubOrgs',
        method: 'POST',
        data: saveData2,
        loading: true
      })
        .then(res => {
          const data = res.data
          this.bumenList2 = data
        })
        .catch(err => {
          console.log(err)
        })
    },
    // 确认选择品类
    comfirmSelect (node, scope) {
      this.$set(scope.row, 'categoryId', node ? node.categoryId : '')
      this.$set(scope.row, 'categoryName', node ? node.categoryFullName : '')
      this.$set(scope.row, 'categoryCode', node ? node.categoryCode : '')
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
      return 'REQUIREMENT'
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
      let attr = []
      data?.data.forEach(row => {
        this.setTotalAmount2(row)
        row.extUseDepartmentId = row?.extUseDepartmentId.toString()
        attr.push(row)
      })
      const reqLineList = this.requirementHead.reqLineList
      this.allreqLineList = reqLineList.concat(attr) // 总的数组需要重新渲染
      this.handleCurrentChange()
    },
    downloadTemplate () {
      downloadFileLink(
        '/api-sup-ce/purchaseRequirement/downloadTemplate',
        this.$t('purchaseDemand.importMaterialItemModelDownload'),
      ).catch(() => {
        this.$message.error(this.$t('purchaseDemand.downloadFail'))
      })
    },
    // 每页条数变更
    sizeChangeHandle (pageSize) {
      this.detailPag.pageSize = pageSize
      const pageNum = this.detailPag.currentPage
      this.requirementHead.reqLineList = this.allreqLineList.slice((pageNum - 1) * pageSize, pageNum * pageSize)
      setTimeout(() => {
        this.tableLoading = false
        this.multipleSelection2 = []
      })
      this.$forceUpdate()
    },
    handleCurrentChange (val = 1) {
      this.tableLoading = true
      this.detailPag.currentPage = val
      const pageSize = this.detailPag.pageSize
      this.requirementHead.reqLineList = this.allreqLineList.slice((val - 1) * pageSize, val * pageSize)
      setTimeout(() => {
        this.tableLoading = false
        this.multipleSelection2 = []
      })
      this.$forceUpdate()
    },
    async getFormDetail (requirementHeadId) {
      const searchData = transformMQL.save(
        'PurchaseRequirementHead',
        [requirementHeadId],
        'read',
        { '*': {}, 'reqLineList': { '*': {} }, 'reqAttachList': { '*': {} } }
      )
      purchaseApplicationApi.queryData(searchData).then((datas) => {
        let data = datas.data[0]
        if (data?.reqLineList == null) {
          data['reqLineList'] = []
        }
        this.reqAttachList = data.reqAttachList
        const reqLineList = data.reqLineList
        this.allreqLineList = reqLineList
        delete data.reqLineList
        this.requirementHead = data
        this.handleCurrentChange(1)
        this.bumenListFun()
      })
      // if (data.data) {
      //   this.requirementHead = data.data.requirementHead || {}
      //   this.reqAttachList = data.data.reqAttachList
      //   let list = data.data.reqLineList.map(
      //     ({ ceeaDeliveryPlace, ...rest }) => {
      //       let d = null
      //       try {
      //         if (ceeaDeliveryPlace) {
      //           d = JSON.parse(ceeaDeliveryPlace)
      //         }
      //       } catch (e) {
      //         console.log(e)
      //       }
      //       return {
      //         ...rest,
      //         ceeaDeliveryPlace: d
      //       }
      //     },
      //   )
      //   this.requirementHead.reqLineList = list
      //   this.allreqLineList = list
      //   this.handleCurrentChange(1)
      //   this.lastCategoryId = this.requirementHead.categoryId
      // }
    },
    // 删除
    deleteOneContent (index, row) {
      if (row.requirementLineId) {
        this.reqLineListDelete.push({ '$delete': row.requirementLineId })
      }
      // this.requirementHead.reqLineList.splice(index, 1)
      // 更新总数据
      const {
        currentPage,
        pageSize
      } = this.detailPag
      const curIndex = (currentPage - 1) * pageSize + index
      this.allreqLineList.splice(curIndex, 1)
      this.requirementHead.reqLineList = this.allreqLineList.slice((currentPage - 1) * pageSize, currentPage * pageSize)
      // 是否更新当前页
      if (currentPage > 1 && index === 0 && this.allreqLineList.length <= (currentPage - 1) * pageSize) {
        this.detailPag.currentPage--
        this.requirementHead.reqLineList = this.allreqLineList.slice((this.detailPag.currentPage - 1) * pageSize, this.detailPag.currentPage * pageSize)
      }
    },
    addUploadOne () {
      this.reqAttachList.push({
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
    outerHandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '', createdBy = '', creationDate = '', createdUserName = '' } = file || {}
      row.fileuploadId = fileId.toString()
      row.attachName = fileName
      row.createdBy = createdBy
      row.createdUserName = createdUserName
      row.creationDate = creationDate
    },
    outerHandleUploadSuccess2 (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.extAttachId = fileId.toString()
      row.extAttachName = fileName
    },
    // 行删除
    handleDelClick (index, row) {
      if (row?.attachId) {
        this.reqAttachListDelete.push({ $delete: row?.attachId })
      }
      this.reqAttachList.splice(index, 1)
      // let i = (this.detailPag.currentPage - 1) * 15 + index
      // this.allreqLineList.splice(i, 1)
      // this.handleCurrentChange(this.detailPag.currentPage)
      // this.$forceUpdate()
      // if (row.notaxPrice) {
      //   this.requirementHead.totalBudget = this.requirementHead.totalBudget - row.totalAmount
      // }
    },
    // 重置对象条件
    resetForm (form) {
      for (let i in form) {
        form[i] = ''
      }
    },
    async openDialog () {
      // this.requirementHead.reqLineList.unshift({
      //   materialCode: null,
      //   materialName: null,
      //   categoryName: null,
      //   extProductFlag: 'N',
      //   extUseDepartmentCode: null,
      //   extUseDepartmentName: null,
      //   extUserPhone: null,
      //   requirementDepartment: null,
      //   extShareStock: null,
      //   extActualStock: null,
      //   editable: true
      // })
      // 更新源数据
      const {
        currentPage,
        pageSize
      } = this.detailPag
      const index = (currentPage - 1) * pageSize
      this.allreqLineList.splice(index, 0, {
        materialCode: null,
        materialName: null,
        categoryName: null,
        extProductFlag: 'N',
        extUseDepartmentCode: null,
        extUseDepartmentName: null,
        extUserPhone: null,
        requirementDepartment: null,
        extShareStock: null,
        extActualStock: null,
        _row_id: this.allreqLineList.length + 1
      })
      this.requirementHead.reqLineList = this.allreqLineList.slice((currentPage - 1) * pageSize, currentPage * pageSize)
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
    orgInvIdChange (node, value, scope) {
      const { organizationTypeCode = null, organizationName = null } = node
      scope.extUseDepartmentCode = organizationTypeCode
      scope.extUseDepartmentName = organizationName
      scope.requirementDepartment = organizationName
    },
    selectHandler (node, value, scope) {
      this.requirementHead.orgId = node ? node.organizationId : null
      this.requirementHead.orgCode = node ? node.organizationCode : null
      this.requirementHead.orgName = node ? node.organizationName : null
      // 切换的时候清空申请部门
      this.requirementHead.ceeaDepartmentId = null
      this.requirementHead.ceeaDepartmentCode = null
      this.requirementHead.ceeaDepartmentName = null
      if (this.requirementHead.reqLineList.length) {
        this.requirementHead.reqLineList.forEach(item => {
          item.extUseDepartmentId = null
          item.extUseDepartmentCode = null
          item.extUseDepartmentName = null
        })
      }
      // 板块
      if (node && node.organizationCode) {
        this.$http({
          url:
          '/api-pj/pj-anon/user/getBuOrganizationByOuOrgCode?organizationCode=' +
          node?.organizationCode,
          method: 'GET',
          loading: true
        }).then(res => {
          this.requirementHead.extOrgBuName = res.data?.organizationName
          this.requirementHead.extOrgBuId = res.data?.organizationId
          this.requirementHead.extOrgBuCode = res.data?.organizationCode
        })
        this.bumenListFun()
      } else {
        this.requirementHead.orgId = null
        this.requirementHead.orgCode = null
        this.requirementHead.orgName = null
        this.requirementHead.extOrgBuName = null
        this.requirementHead.extOrgBuId = null
        this.requirementHead.extOrgBuCode = null
        this.bumenList = []
        this.bumenList2 = []
      }
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

      this.allreqLineList.unshift(...newArr)
      this.handleCurrentChange()
      this.dialogVisible = false
    },
    checkMaterialList (categoryId) {
      return new Promise(resolve => {
        if (
          this.allreqLineList.length &&
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
              this.allreqLineList = []
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
    getCategoryObj (val, scope) {
      scope.materialCode = val?.materialCode
      scope.materialName = val?.materialName
      scope.materialId = val?.materialId
      scope.categoryCode = val?.categoryCode
      scope.categoryFullName = val?.categoryFullName
      scope.categoryId = val?.categoryId
      scope.categoryName = val?.categoryName
      this.$set(scope, 'brand', val?.brand)
      this.$set(scope, 'extMaterialModel', val?.materialType)
      this.$set(scope, 'unitCode', val?.unit)
      this.$set(scope, 'unit', val?.unitName)

      // // 看是否商品
      // if (!this.requirementHead.orgId) {
      //   this.$message.error('请选择公司')
      //   return false
      // }
      // // 没有选择区域的时候
      // if (!scope?.extAreaCode) {
      //   return false
      // }
      // const obj = {
      //   materialId: val?.materialId,
      //   orgId: this.requirementHead.orgId,
      //   extAreaCode: scope?.extAreaCode
      // }
      // this.searchCaigouFun(obj, scope)
    },
    extAreaChange (value, row) {
      if (!row?.materialId) {
        // this.$message.error('请选择物料才能查询是否有已上架商品')
        return false
      }
      const obj = {
        materialId: row?.materialId,
        orgId: this.requirementHead.orgId,
        extAreaCode: row?.extAreaCode
      }
      this.searchCaigouFun(obj, row)
    },
    searchCaigouFun (obj, row) {
      const searchData = transformMQL.listPageData({
        type: 'CatalogOnShelves',
        action: 'query',
        params: {
          materialId: obj?.materialId,
          extOrgIdList: obj?.orgId?.toString(),
          extAreaCode: obj?.extAreaCode,
          status: 'ON_SHELVES'
        },
        filterOperator: {
          materialId: 'eq',
          orgId: 'eq',
          status: 'eq',
          extAreaCode: 'eq'
        }
      })
      purchaseApplicationApi.searchCaigou(searchData).then((datas) => {
        const num = datas.data?.records?.length
        if (num == 1) {
          const records = datas.data?.records[0]
          console.log(records, 'records')
          this.$set(row, 'extProductFlag', 'Y')
          this.$set(row, 'extPredictPrice', records?.extReferencePrice)
        } else {
          this.$set(row, 'extProductFlag', 'N')
        }
      })
    },
    async getCategoryObj1 (val, row) {
      row.dmandLineRequest = val?.nickname
      row.extUserName = val?.nickname
      row.extUserCode = val?.username
    },
    getTotalAmount (n) {
      // 存起来一个初始需求数量
      if (this.copyInit.bol) {
        this.copyInit.num = +n
        this.copyInit.bol = false
      }
    },
    setTotalAmount2 (row, index) {
      const total = Number(row.extPredictPrice) * Number(row.requirementQuantity)
      if (total) {
        this.$set(row, 'extPredictAmount', total)
        // this.$set(this.requirementHead.reqLineList[index], 'extPredictAmount', total)
      }
    },
    async setTotalAmount (row, index) {
      const formFiled = await this.formValidate('requirementHeadRef')
      const fieldKeys = Object.keys(formFiled.obj)
      if (!formFiled.flag && fieldKeys.length > 0 && fieldKeys[0].includes('reqLineList')) {
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
      setTimeout(() => {
        const totalAmountArr = this.allreqLineList.map(v => v.totalAmount || 0)
        const totalBudget = totalAmountArr.reduce((p, c) => (Number(p) || 0) + (Number(c) || 0))
        this.$set(this.requirementHead, 'totalBudget', totalBudget)
        this.requirementHead.nextYearBudgetAmount =
          this.requirementHead.totalBudget - this.requirementHead.thisYearBudgetAmount
      }, 100)
    },
    getUserObj (val, scope) {
      scope.createdId = val ? val.userId : ''
      scope.createdFullName = val ? val.nickname : ''
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
    initParams () {
      let allparam = {
        ...this.requirementHead,
        reqAttachList: this.reqAttachList,
        auditStatus: 'DRAFT'
      }
      allparam.reqLineList = this.allreqLineList.concat(this.reqLineListDelete)
      allparam.reqAttachList = allparam.reqAttachList.concat(this.reqAttachListDelete)
      return allparam
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
    // 审批流-下一步
    async preNextStepHandler () {
      let allparam = this.initParams()
      const { flag, message } = await this.getCheckForm()
      if (flag) {
        let bol = false
        allparam.reqLineList.forEach(datas => {
          if (datas.extShareStock == null || datas.extActualStock == null) {
            bol = true
            return false
          }
        })
        if (bol) {
          this.$message.error('请点击查看库存')
          return false
        }
        // 校验物料编码是否有选择
        let bol2 = false
        let bol3 = false
        allparam.reqLineList.forEach(datas => {
          if (datas.materialCode == null) {
            bol2 = true
            return false
          }
          if (datas.categoryName == null || datas.extUseDepartmentId == null || datas.extUserName == null || datas.extUserPhone == null || datas.unitCode == null || datas.requirementQuantity == null || datas.extPredictPrice == null || datas.extPredictAmount == null || datas.requirementDate == null || datas.receiveAddress == null || datas.extReceiver == null || datas.receiveTelephone == null || datas.extAreaCode == null || datas.extUseTo == null) {
            bol3 = true
            return false
          }
        })
        if (bol2) {
          this.$message.error('物料明细行有物料编码没有选')
          return false
        }
        if (bol3) {
          this.$message.error('物料明细行有必填项没有填写')
          return false
        }
        allparam.auditStatus = 'SUBMITTED'
        const saveData = transformMQL.save('PurchaseRequirementHead', [allparam], 'saveOrUpdate')
        const datas = await purchaseApplicationApi.save(saveData)
        const requirementHeadId = datas.data[0].requirementHeadId
        if (!requirementHeadId) {
          this.$message.warning(this.$t('cusEntry.tipMessage.businessIdIsNotExit'))
          return false
        }
        this.requirementHead.requirementHeadId = requirementHeadId
        this.$message({
          message: '保存成功',
          type: 'success'
        })
        await this.getFormDetail(requirementHeadId)
        return true
      } else {
        this.__focus_error__(message)
        return false
      }
    },
    // 提交校验
    async submitCheck () {
      if (this.allreqLineList.length === 0) {
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
      allparam.auditStatus = 'SUBMITTED'
      const saveData = transformMQL.save('PurchaseRequirementHead', [allparam], 'saveOrUpdate')
      purchaseApplicationApi.save(saveData).then((datas) => {
        this.$message({
          message: '保存成功',
          type: 'success'
        })

        const requirementHeadId = datas.data[0].requirementHeadId
        this.requirementHead.requirementHeadId = requirementHeadId
        this.getFormDetail(requirementHeadId)
        this.handlerAfter('SUBMIT')
      })
    },
    setUserObj3 (row) {
      const val = this.bumenList.find(v => v.organizationId.toString() === row.extUseDepartmentId) || {}
      // scope.ceeaStrategyUserId = val ? val.personInChargeUserId : "";
      row.extUseDepartmentName = val.organizationName
      row.extUseDepartmentCode = val.organizationCode
      const extUseDepartmentId = row.extUseDepartmentId
      this.$http({
        url: '/api-base/orgQuery/getOrgAddress?orgId=' + extUseDepartmentId,
        method: 'GET',
        loading: true
      })
        .then(data => {
          const datas = data.data
          this.addressList = datas
          datas.forEach(e => {
            if (e.isDefault == 'Y') {
              this.$set(row, 'extReceiver', e.receiver)
              this.$set(row, 'receiveTelephone', e.receiverPhone)
              this.$set(row, 'receiveAddress', e.siteName)
              this.$set(row, 'extAreaCode', e.addressRegion)
              this.extAreaChange(row.extAreaCode, row)
            }
          })
        })
        .catch(err => {
          console.log(err)
        })
    },
    setUserObj4 () {
      const val = this.bumenList2.find(v => v.organizationName === this.requirementHead.ceeaDepartmentName) || {}
      // scope.ceeaStrategyUserId = val ? val.personInChargeUserId : "";
      this.requirementHead.ceeaDepartmentId = val.organizationId
      this.requirementHead.ceeaDepartmentCode = val.organizationCode
    },
    saveBill (allparam) {
      const saveData = transformMQL.save('PurchaseRequirementHead', [allparam], 'saveOrUpdate')
      purchaseApplicationApi.save(saveData).then((result) => {
        this.$message({
          message: '暂存成功',
          type: 'success'
        })
        if (result.data?.length) {
          const { requirementHeadId } = result.data[0]
          this.getFormDetail(requirementHeadId)
        }
      })
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

      if (this.requirementHead.reqLineList.length == 0) {
        return {
          flag: false,
          message: '请填写物料信息'
        }
      }
      let bol = false
      this.requirementHead.reqLineList.forEach(datas => {
        console.log(datas, 'datas')
        if (!datas.extUserName || !datas.extUserPhone || !datas.categoryName || !datas.extMaterialModel || !datas.extUseDepartmentId || !datas.receiveAddress || !datas.extAreaCode || !datas.extPredictPrice || !datas.extUseTo) {
          bol = true
        }
      })
      if (bol) {
        return {
          flag: false,
          message: '物料信息行有必填字段未填写'
        }
      }

      return { flag: true }
    },
    // 抽离保存，提交方法校验及其他数据处理
    saveOtherDeal () {
      if (this.requirementHead.ceeaPrType === '01' && this.reqAttachList.length === 0) {
        this.$message.error(this.$t('purchaseDemand.saveBillTips3'))
        return
      }
      if (
        Number(this.requirementHead.thisYearBudgetAmount) > Number(this.requirementHead.totalBudget)
      ) {
        return this.$message.error(this.$t('purchaseDemand.saveBillTips4'))
      }
      this.requirementHead.nextYearBudgetAmount =
        this.requirementHead.totalBudget - this.requirementHead.thisYearBudgetAmount

      this.allreqLineList.map(v => {
        v.orgId = this.requirementHead.orgId
        v.orgCode = this.requirementHead.orgCode
        v.orgName = this.requirementHead.orgName
      })
    },
    async saveOrSubmitBill (type) {
      let allparam = {
        ...this.requirementHead,
        reqAttachList: this.reqAttachList,
        auditStatus: 'DRAFT'
      }
      allparam.reqLineList = this.allreqLineList.concat(this.reqLineListDelete)
      allparam.reqAttachList = allparam.reqAttachList.concat(this.reqAttachListDelete)

      if (type === 'SUBMIT') {
        const { flag, message } = await this.getCheckForm()
        if (flag) {
          //   await this.saveOtherDeal()
          // 校验共享库存与实时库存是否有值
          let bol = false
          allparam.reqLineList.forEach(datas => {
            if (datas.extShareStock == null || datas.extActualStock == null) {
              bol = true
              return false
            }
          })
          if (bol) {
            this.$message.error('请点击查看库存')
            return false
          }
          // 校验物料编码是否有选择
          let bol2 = false
          let bol3 = false
          allparam.reqLineList.forEach(datas => {
            if (datas.materialCode == null) {
              bol2 = true
              return false
            }
            if (datas.categoryName == null || datas.extUseDepartmentId == null || datas.extUserName == null || datas.extUserPhone == null || datas.unitCode == null || datas.requirementQuantity == null || datas.extPredictPrice == null || datas.extPredictAmount == null || datas.requirementDate == null || datas.receiveAddress == null || datas.extReceiver == null || datas.receiveTelephone == null || datas.extAreaCode == null || datas.extUseTo == null) {
              bol3 = true
              return false
            }
          })
          if (bol2) {
            this.$message.error('物料明细行有物料编码没有选')
            return false
          }
          if (bol3) {
            this.$message.error('物料明细行有必填项没有填写')
            return false
          }

          this.submitEvent(allparam)
        } else {
          this.__focus_error__(message)
        }
      } else if (type === 'SAVE') {
        this.saveBill(allparam)
      }
    },
    back () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('purchaseApplicationList.getQuerydata')
    }
  }
}
</script>
<style scoped lang="scss">
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
</style>
