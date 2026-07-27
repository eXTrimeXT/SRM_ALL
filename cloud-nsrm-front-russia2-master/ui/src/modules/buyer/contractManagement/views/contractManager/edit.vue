<template>
  <el-container
    direction="vertical"
    class="flex-container contractInformation"
  >
    <el-main>
      <CWorkflowMulti
        ref="workflowMulti"
        v-model="activeTabName"
        :fun-params="workflowParamsInfo"
        :button-config-info="buttonConfigInfo"
        :button-custom="buttonCustom"
        @tab-click="workflowView"
        @workflow-handler="workflowHandler"
        @click-handler="type => saveBill(type)"
        @submit-direct="type => saveBill(type)"
        @confirm="(type, comment) => saveBill(type, comment)"
        @close-tab="back"
      >
        <div class="stepDiv">
          <el-steps
            :active="curStatus"
            :align-center="true"
            finish-status="success"
          >
            <el-step :title="$t('contractMod.contractRelease')" />
            <el-step v-if="mergeForm.needVendorConfirm == 'Y'" :title="$t('contractMod.contractConfirmation')" />
            <el-step :title="$t('contractMod.contractApproval')" />
            <el-step :title="$t('contractMod.contractSigning')" />
            <el-step :title="$t('contractMod.contractFiling')" />
          </el-steps>
        </div>
        <el-collapse
          v-model="activeDims"
          class="tab-form-style"
        >
          <!--合同信息-->
          <el-collapse-item
            v-if="IS_BUYER()"
            :title="$t('logisticsMod.contractInfo')"
            name="1"
          >
            <el-form
              ref="mainForm"
              :model="mergeForm"
              :rules="rules"
              :disabled="IS_READ_ONLY || !IS_BUYER() || getEditable ()"
            >
              <srm-row>
                <!--合同编号-->
                <srm-col
                  :initCol="4"
                >
                  <el-form-item
                    :label="$t('contractMod.contractNo')"
                  >
                    <el-input v-model="mergeForm.contractNo" disabled />
                  </el-form-item>
                </srm-col>
                <!--状态-->
                <srm-col
                  :initCol="4"
                >
                  <el-form-item
                    :label="$t('contractMod.status')"
                  >
                    <DictSelect
                      v-model="mergeForm.contractStatus"
                      code="CONTRACT_STATUS"
                      :disabled="true"
                    />
                  </el-form-item>
                </srm-col>
                <!--创建人-->
                <srm-col
                  :initCol="4"
                >
                  <el-form-item
                    :label="$t('contractMod.createdBy')"
                  >
                    <el-input v-model="mergeForm.createdFullName" disabled />
                  </el-form-item>
                </srm-col>
                <!--创建时间-->
                <srm-col
                  :initCol="4"
                >
                  <el-form-item
                    :label="$t('contractMod.creationDate')"
                  >
                    <el-date-picker
                      v-model="mergeForm.creationDate"
                      :format="$formatDatePickerTime"
                      value-format="yyyy-MM-dd HH:mm:ss"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <!--合同名称-->
                <srm-col
                  :initCol="4"
                >
                  <el-form-item
                    :label="$t('contractMod.contractName')"
                    prop="contractName"
                  >
                    <el-input
                      v-model="mergeForm.contractName"
                      maxlength="100"
                      show-word-limit
                    />
                  </el-form-item>
                </srm-col>
                <!--业务实体-->
                <srm-col
                  :initCol="4"
                >
                  <el-form-item
                    :label="$t('contractMod.fullPathId')"
                    prop="buId"
                  >
                    <OrganizationSelector
                      ref="m_ou"
                      v-model="mergeForm.buId"
                      :parent-id="-1"
                      :jump-login="jumpLogin"
                      :placeholder="$t('common.pleaseSelect')"
                      :disabled="mergeForm.contractType !== 'MIAN_CONTRACT_ADD'"
                      node-type="OU"
                      @select="buHandler"
                    />
                  </el-form-item>
                </srm-col>
                <!--供应商名称-->
                <srm-col
                  :initCol="4"
                >
                  <el-form-item
                    :label="$t('contractMod.vendorName')"
                    prop="vendorName"
                  >
                    <QuickSearch
                      :show-input="mergeForm.vendorName"
                      show-key="companyName"
                      :scope-data="mergeForm"
                      name="scc_sup_company_info_new"
                      :disabled="
                        isFromPriceApproval ||
                          mergeForm.contractType !== 'MIAN_CONTRACT_ADD'
                      "
                      @close-quicksearch="getVendorObj"
                    />
                  </el-form-item>
                </srm-col>
                <!--合同形式-->
                <srm-col
                  :initCol="4"
                >
                  <el-form-item
                    :label="$t('contractMod.contractForm')"
                    prop="formal"
                  >
                    <DictSelect
                      v-model="mergeForm.formal"
                      code="CONTRACT_FORM"
                    />
                  </el-form-item>
                </srm-col>
                <!--合同有效期从-->
                <srm-col
                  :initCol="4"
                >
                  <el-form-item
                    :label="$t('contractMod.contractValidFrom')"
                    prop="effectiveDateFrom"
                  >
                    <el-date-picker
                      v-model="mergeForm.effectiveDateFrom"
                      type="date"
                      :end-placeholder="mergeForm.effectiveDateTo"
                      :placeholder="$t('common.pleaseSelectDate')"
                      :format="$formatDatePicker"
                      value-format="yyyy-MM-dd"
                    />
                  </el-form-item>
                </srm-col>
                <!--合同有效期至-->
                <srm-col
                  :initCol="4"
                >
                  <el-form-item
                    :label="$t('contractMod.contractValidTo')"
                    prop="effectiveDateTo"
                  >
                    <el-date-picker
                      v-model="mergeForm.effectiveDateTo"
                      :picker-options="endTiumePickerOptions"
                      :start-placeholder="mergeForm.effectiveDateFrom"
                      type="date"
                      :placeholder="$t('common.pleaseSelectDate')"
                      :format="$formatDatePicker"
                      value-format="yyyy-MM-dd"
                    />
                  </el-form-item>
                </srm-col>
                <!--合同类型-->
                <srm-col
                  :initCol="4"
                >
                  <el-form-item
                    :label="$t('contractMod.mgsContractType')"
                    prop="contractClass"
                  >
                    <DictSelect
                      v-model="mergeForm.contractClass"
                      code="ELEM_CONTRACT_TYPE"
                      :disabled="getEditable()"
                      @change="elemContractTypeHandler"
                    />
                  </el-form-item>
                </srm-col>
                <!--合同模式-->
                <srm-col
                  :initCol="4"
                >
                  <el-form-item
                    :label="$t('vendorMod.controlMethod')"
                  >
                    <DictSelect
                      v-model="mergeForm.ceeaControlMethod"
                      code="MANAGEMENT_CONTROL_MODEL"
                    />
                  </el-form-item>
                </srm-col>
                <!--签约地址-->
                <srm-col
                  :initCol="2"
                >
                  <el-form-item
                    :label="$t('contractMod.signingAddress')"
                  >
                    <el-input
                      v-model="mergeForm.signingAddress"
                      type="textarea"
                      :rows="2"
                      :placeholder="$t('common.pleaseTypeContents')"
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-form>
          </el-collapse-item>
          <!--其他信息-->
          <el-collapse-item
            v-if="IS_BUYER()"
            :title="$t('vendorMod.otherInfo')"
            name="2"
          >
            <el-form
              ref="formHeader2"
              :model="mergeForm"
              :rules="rules"
              :disabled="IS_READ_ONLY || !IS_BUYER() || getEditable()"
            >
              <srm-row>
                <!--是否需要供应商确认-->
                <srm-col
                  :initCol="4"
                >
                  <el-form-item
                    :label="$t('contractMod.supplierConfirmation')"
                    prop="needVendorConfirm"
                  >
                    <el-radio-group
                      v-model="mergeForm.needVendorConfirm"
                      style="width: 100%"
                    >
                      <el-radio label="Y">
                        {{ $t('common.yes') }}
                      </el-radio>
                      <el-radio label="N">
                        {{ $t('common.no') }}
                      </el-radio>
                    </el-radio-group>
                  </el-form-item>
                </srm-col>
                <!--是否启用模板-->
                <srm-col
                  :initCol="4"
                >
                  <el-form-item
                    :label="$t('vendorMod.enableFlagModel')"
                    prop="modelEnable"
                  >
                    <el-radio-group
                      v-model="mergeForm.modelEnable"
                      style="width: 100%"
                      @change="formalChange"
                    >
                      <el-radio label="Y">
                        {{ $t('common.yes') }}
                      </el-radio>
                      <el-radio label="N">
                        {{ $t('common.no') }}
                      </el-radio>
                    </el-radio-group>
                  </el-form-item>
                </srm-col>
                <!--模板名称-->
                <srm-col
                  v-if="mergeForm.modelEnable == 'Y'"
                  :initCol="4"
                >
                  <el-form-item
                    :label="$t('dataConfMod.templateName')"
                    prop="modelHeadId"
                  >
                    <el-select
                      v-model="mergeForm.modelHeadId"
                      :disabled="getEditableA()"
                      @change="templateChange"
                    >
                      <el-option
                        v-for="item in modelHeadIdList"
                        :key="item.id"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </el-form-item>
                </srm-col>
                <!--标准合同-->
                <srm-col
                  :initCol="4"
                >
                  <el-form-item
                    :label="$t('contractMod.standardContract')"
                    prop="enable"
                  >
                    <DictSelect
                      v-model="mergeForm.enable"
                      :disabled="!['MIAN_CONTRACT_ALTER','MIAN_CONTRACT_ADD'].includes(mergeForm.contractType)"
                      code="YES_OR_NO"
                    />
                  </el-form-item>
                </srm-col>
                <!--是否失效原合同-->
                <srm-col
                  v-if="mergeForm.contractType === 'MIAN_CONTRACT_ALTER'"
                  :initCol="4"
                >
                  <el-form-item
                    :label="$t('vendorMod.enableFlagInvalid')"
                    prop="isInvalidOldContract"
                  >
                    <el-radio-group
                      v-model="mergeForm.isInvalidOldContract"
                      style="width: 100%"
                      @change="isInvalidOldContract"
                    >
                      <el-radio label="Y">
                        {{ $t('common.yes') }}
                      </el-radio>
                      <el-radio label="N">
                        {{ $t('common.no') }}
                      </el-radio>
                    </el-radio-group>
                  </el-form-item>
                </srm-col>
                <!--备注-->
                <srm-col
                  :initCol="1"
                >
                  <el-form-item
                    :label="$t('bid_mod.remark')"
                  >
                    <el-input
                      v-model="mergeForm.contractRemark"
                      type="textarea"
                      :rows="2"
                      :placeholder="$t('common.pleaseTypeContents')"
                    />
                  </el-form-item>
                </srm-col>
                <!--起草人意见-->
                <srm-col
                  :initCol="1"
                >
                  <el-form-item
                    :label="$t('vendorMod.loggerComment')"
                  >
                    <el-input
                      v-model="mergeForm.drafterOpinion"
                      type="textarea"
                      :rows="2"
                      :placeholder="$t('common.pleaseTypeContents')"
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-form>
          </el-collapse-item>
          <!--合同财务信息-->
          <el-collapse-item
            v-if="IS_BUYER()"
            :title="$t('contractMod.contractFinancialInformation')"
            name="9"
          >
            <el-form
              ref="formHeader3"
              :model="mergeForm"
              :rules="rules"
              :disabled="IS_READ_ONLY || !IS_BUYER() || getEditable()"
            >
              <srm-row>
                <!--合同总金额-->
                <srm-col
                  :initCol="4"
                >
                  <el-form-item
                    :label="$t('contractMod.totalAmountTax1')"
                  >
                    <el-input
                      v-model="mergeForm.includeTaxAmount"
                    />
                  </el-form-item>
                </srm-col>
                <!--币种-->
                <srm-col
                  :initCol="4"
                >
                  <el-form-item
                    :label="$t('contractMod.currencyCode')"
                    prop="currencyCode"
                  >
                    <DictSelect
                      v-model="mergeForm.currencyCode"
                      code="currency"
                      @change-value="currencyHandler"
                    />
                  </el-form-item>
                </srm-col>
                <!--是否框架协议-->
                <srm-col
                  :initCol="4"
                >
                  <el-form-item
                    :label="$t('contractMod.isFrameworkAgreement')"
                    prop="isFrameworkAgreement"
                  >
                    <el-radio-group
                      v-model="mergeForm.isFrameworkAgreement"
                      style="width: 100%"
                    >
                      <el-radio label="Y">
                        {{ $t('common.yes') }}
                      </el-radio>
                      <el-radio label="N">
                        {{ $t('common.no') }}
                      </el-radio>
                    </el-radio-group>
                  </el-form-item>
                </srm-col>
                <!--框架协议编号-->
                <!--                v-if="mergeForm.isFrameworkAgreement == 'Y'"-->
                <srm-col
                  :initCol="4"
                >
                  <el-form-item
                    :label="$t('contractMod.frameworkAgreementCode')"
                  >
                    <el-input v-model="mergeForm.frameworkAgreementCode">
                      <template #append>
                        <el-button
                          type="primary"
                          icon="el-icon-search"
                          @click="showFrameworkAgreement"
                        />
                      </template>
                    </el-input>
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-form>
          </el-collapse-item>
          <!-- 物料明细 -->
          <el-collapse-item
            :title="$t('purchaseDemand.itemInfo')"
            name="3"
          >
            <material-list
              ref="materialList"
              :jump-login="jumpLogin"
              :illegal="illegal"
              :value.sync="materialListData"
              :start-date="mergeForm.effectiveDateFrom"
              :end-date="mergeForm.effectiveDateTo"
              :contract-type="mergeForm.contractType"
              :buId="mergeForm.buId"
              :show-plus="!IS_READ_ONLY"
              :is-buyer="IS_BUYER()"
              :is-framework-agreement="mergeForm.isFrameworkAgreement"
              @change="materialDataChange"
              @select="materialHandleSelectionChange"
            />
          </el-collapse-item>
          <!-- 付款计划 -->
          <el-collapse-item
            :title="$t('contractMod.paymentPlan')"
            name="4"
          >
            <pay-plan
              ref="payList"
              v-model="payPlanData"
              :illegal="illegal"
              :contract-type="mergeForm.contractType"
              :show-plus="!IS_READ_ONLY"
              :context="this"
              :is-buyer="IS_BUYER()"
              visible
            />
          </el-collapse-item>
          <!-- 1、合同头表信息“是否框架协议”为是时
          合作伙伴为手工新增，可新增甲方，乙方，丙方
          物料明细及付款计划不做强制校验是否录入，物料明细无需显示寻源单查询按钮，新增物料明细，不影响合作伙伴的内容显示

          2、合同头表信息“是否框架协议”为否，
          合作伙伴信息仅可手工添加丙方，且伙伴名称数据来源为除甲方外的业务实体进行选择
          甲方、乙方均不可手工新增，
          甲方仅为物料明细行所包含的所有业务实体
          乙方为头表信息上的供应商信息
          3、甲方/丙方所显示的伙伴名称，修改为业务实体对应的公司名称，但仍需记录对应的OU ID
        -->
          <!-- 合作伙伴 -->
          <el-collapse-item
            :title="$t('contractMod.partner')"
            name="5"
          >
            <partner
              ref="partnerList"
              v-model="partnerData"
              :illegal="illegal"
              :vendor-name="mergeForm.vendorName"
              :material-list-data="materialListData"
              :contract-type="mergeForm.contractType"
              :show-plus="!IS_READ_ONLY"
              :is-buyer="IS_BUYER()"
              :is-framework-agreement="mergeForm.isFrameworkAgreement"
              visible
            />
          </el-collapse-item>
          <!-- 合同附件信息 -->
          <el-collapse-item
            ref="file-area"
            :title="$t('contractMod.fileInfo')"
            name="7"
          >
            <el-button
              v-if="!IS_READ_ONLY && IS_BUYER()"
              style="margin-bottom: 10px"
              type="primary"
              class="detail-pbtn"
              @click="addUploadOne"
            >
              {{ $t("common.add") }}
            </el-button>
            <el-table
              :data="fileuploads"
              style="width: 100%"
              border
              max-height="250px"
            >
              <el-table-column
                align="center"
                type="index"
                :label="$t('common.sort')"
                width="50"
              />
              <!--协议类型-->
              <el-table-column
                align="center"
                prop="fileType"
                :label="$t('dataConfMod.attachmentType')"
              >
                <template slot-scope="scope">
                  {{ $getDictLabel('CONTRACT_AGREEMENT_ATTACHMENT', scope.row.fileType) }}
                </template>
              </el-table-column>
              <!-- 附件名称 -->
              <el-table-column
                align="center"
                prop="fileSourceName"
                :label="$t('bidMod.fileName')"
                :render-header="_addStarToColumn"
              >
                <template slot-scope="scope">
                  <SrmCommonFile
                    :extra-data="fileInfo"
                    :default-file="{
                      fileId: scope.row.fileuploadId,
                      fileName: scope.row.fileSourceName
                    }"
                    :readonly="IS_READ_ONLY"
                    @on-change="({file}) => outerHandleUploadSuccess(file,scope.row)"
                  />
                </template>
              </el-table-column>
              <!-- 上传人 -->
              <el-table-column
                align="center"
                prop="createdUserName"
                :label="$t('purchaseDemand.attachmentCreatedBy')"
              />
              <!-- 上传时间 -->
              <el-table-column
                align="center"
                prop="creationDate"
                :label="$t('purchaseDemand.attachmentCreatedDate')"
                :formatter="(row, column, cellValue) => $parseTime(cellValue)"
              />
              <!-- 操作 -->
              <el-table-column
                v-if="IS_BUYER()"
                :label="$t('common.operation')"
                width="60"
              >
                <template slot-scope="scope">
                  <el-button
                    v-if="!scope.row.sourceId && scope.row.del !== 'N' && mergeForm.contractStatus !== 'ARCHIVED' && !IS_READ_ONLY && scope.row.fileType != 'SEAL_AGREEMENT'"
                    :disabled="illegal == 'view'"
                    type="text"
                    @click="handleDelClick(scope.$index, scope.row)"
                  >
                    {{ $t("common.delete") }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
          <!--补充协议说明-->
          <el-collapse-item
            v-if="mergeForm.contractType == 'SUPPLEMENTAL_AGREEMENT'"
            name="8"
            :title="$t('contractMod.supplementaryAgreement')"
          >
            <el-form ref="agreement" :model="mergeForm" :rules="rules">
              <el-form-item :label="$t('contractMod.supplementaryAgreement')" prop="supplementAgreementExplain">
                <el-input
                  v-model="mergeForm.supplementAgreementExplain"
                  type="textarea"
                  :rows="4"
                  maxlength="200"
                  show-word-limit
                  :placeholder="$t('common.pleaseTypeContents')"
                />
              </el-form-item>
            </el-form>
          </el-collapse-item>
          <!--合同详情-->
          <el-collapse-item
            v-if="mergeForm.modelEnable === 'Y'"
            name="6"
            :title="$t('contractMod.contractContent')"
          >
            <!-- 标准合同：只能编辑合同模板带出来的元素值 -->
            <!-- 非标准合同：能编辑合同模板带出来的元素值、和合同模板内容 -->
            <div
              id="preview_wrapper"
              ref="printContent"
              :style="preview_wrapper_style"
            >
              <div
                ref="markedContent"
                :contenteditable="contenteditable"
                style="width: 100%"
              />
            </div>
          </el-collapse-item>
        </el-collapse>
        <div
          id="template"
          class="template"
          style="display: none"
        >
          <div ref="template" />
        </div>
        <!-- 底部按钮定义 -->
        <template slot="buttonOne" class="buttonOneClass">
          <el-button
            v-if="!!mergeForm.modelHeadId && illegal != 'view'"
            @click="print"
          >
            {{ $t("route.pdfPrint") }}
          </el-button>
          <el-button
            v-if="
              !!mergeForm.modelHeadId &&
                IS_BUYER() &&
                mergeForm.contractType === 'MIAN_CONTRACT_ADD' &&
                childContext &&
                !childContext.editable &&
                mergeForm.contractStatus === 'DRAFT' &&
                illegal != 'view' && mergeForm.modelEnable === 'Y'
            "
            type="primary"
            :disabled="SUBMIT_STATUS()"
            @click="edit"
          >
            <!-- 编辑合同详情 -->
            {{ $t("contractMod.editContractDetail") }}
          </el-button>
          <el-button
            v-else-if="
              !!mergeForm.modelHeadId &&
                childContext &&
                childContext.editable &&
                mergeForm.contractType === 'MIAN_CONTRACT_ADD' &&
                illegal != 'view' && mergeForm.modelEnable === 'Y'
            "
            @click="preview"
          >
            <!-- 预览合同详情 -->
            {{ $t("contractMod.previewContractDetail") }}
          </el-button>
          <el-button
            v-if="
              ((!!mergeForm.modelHeadId && mergeForm.enable === 'N') ||
                illegal === 'view') && mergeForm.modelEnable === 'Y'
            "
            type="primary"
            @click="html2diff"
          >
            <!-- 对比更改 -->
            {{ $t("contractMod.compareChange") }}
          </el-button>
          <el-button
            v-if="
              mergeForm.contractStatus === 'APPROVAL' &&
                IS_BUYER() &&
                ['MIAN_CONTRACT_ADD', 'MIAN_CONTRACT_ALTER', 'SUPPLEMENTAL_AGREEMENT'].includes(mergeForm.contractType) &&
                mergeForm.formal === 'ELECTRONIC_CONTRACT'
            "
            type="primary"
            @click="fillReleaseParams"
          >
            <!-- 发布签章平台 -->
            {{ $t("contractMod.releaseSignPlatform") }}
          </el-button>
          <!--发布供应商暂存-->
          <el-button
            v-if="mergeForm.needVendorConfirm == 'Y' && IS_BUYER() &&
              (['DRAFT', 'SUPPLIER_REJECTED', 'WITHDRAW', 'REJECTED'].includes(mergeForm.contractStatus) ||
              ($attrs.params ? $attrs.params.flag === 'add' : null))"
            type="primary"
            @click="submit('savePublish')"
          >
            {{ $t("common.staging") }}
          </el-button>
          <!--发布供应商-->
          <el-button
            v-if="mergeForm.needVendorConfirm == 'Y' && IS_BUYER() &&
              (['DRAFT', 'SUPPLIER_REJECTED', 'WITHDRAW', 'REJECTED'].includes(mergeForm.contractStatus) ||
              ($attrs.params ? $attrs.params.flag === 'add' : null))"
            type="primary"
            @click="submit('publish')"
          >
            {{ $t("contractMod.releaseSupplier") }}
          </el-button>
          <!--供应商确认按钮-->
          <el-button
            v-if="!this.IS_BUYER() && this.mergeForm.contractStatus === 'SUPPLIER_CONFIRMING'"
            type="primary"
            @click="submit('SUPPLIER_CONFIRMING')"
          >
            {{ $t("orderMod.buyerOrderSynergy.confirm") }}
          </el-button>
          <!--供应商驳回按钮-->
          <el-button
            v-if="!this.IS_BUYER() && this.mergeForm.contractStatus === 'SUPPLIER_CONFIRMING'"
            type="primary"
            @click="submit('SUPPLIER_REFUSE')"
          >
            {{ $t("components.approvalHead.headers.refuse") }}
          </el-button>
        </template>
      </CWorkflowMulti>

      <!-- 对比更改 -->
      <srm-dialog
        :title="$t('contractMod.compareChange')"
        size="large"
        :visible.sync="diffVisible"
      >
        <div style="overflow: hidden">
          <div
            style="width: 98%"
            class="conetnt paper"
            v-html="diffhtml"
          />
        </div>
      </srm-dialog>
      <!-- 寻源单查询 -->
      <srm-dialog
        :title="$t('contractMod.sourceOrderQuery')"
        size="large"
        :visible.sync="selectionVisible"
      >
        <div>
          <el-form
            :model="queryParams"
            inline
          >
            <!-- 寻源单号 -->
            <el-form-item
              prop="sourceNumber"
              :label="$t('purchaseDemand.sourceTNum')"
            >
              <el-input v-model="queryParams.sourceNumber" />
            </el-form-item>
            <!-- 业务实体 -->
            <el-form-item
              prop="organizationId"
              ;:label="$t('purchaseDemand.businessEntity')"
            >
              <OrganizationSelector
                ref="ou"
                v-model="queryParams.organizationId"
                :parent-id="-1"
                :placeholder="$t('common.pleaseSelect')"
                node-type="OU"
              />
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                @click="queryHandler"
              >
                {{
                  $t("common.search")
                }}
              </el-button>
            </el-form-item>
          </el-form>
        </div>
        <div style="max-height: 300px; overflow: auto">
          <BaseTable
            stripe
            :data="sourceList"
            :columns="columns"
            border
            @selection-change="selectionChange"
          />
        </div>
        <div
          slot="footer"
          class="dialog-footer"
        >
          <el-button @click="selectionVisible = false">
            {{
              $t("common.cancel")
            }}
          </el-button>
          <el-button
            type="primary"
            @click="comfirmSelect"
          >
            {{
              $t("common.confirm")
            }}
          </el-button>
        </div>
      </srm-dialog>
      <!-- 维护框架协议 -->
      <srm-dialog
        :title="$t('contractMod.maintainFrameworkAgreement')"
        size="middle"
        :visible.sync="frameworkAgreementVisible"
      >
        <el-form
          ref="sumForm2"
          :model="sumForm2"
          label-width="80px"
        >
          <srm-row>
            <srm-col :initCol="2">
              <!-- 供应商 -->
              <el-form-item :label="$t('common.vendor')">
                <el-input
                  v-model="sumForm2.vendorName"
                  disabled
                />
              </el-form-item>
            </srm-col>
            <srm-col
              :initCol="3"
              style="padding-left: 22px"
            >
              <!-- 是否协议框架 -->
              <el-form-item :label="$t('bidMod.isFrameworkAgreement')">
                <el-checkbox
                  v-model="sumForm2.isFrameworkAgreement"
                  true-label="Y"
                  false-label="N"
                  disabled
                />
              </el-form-item>
            </srm-col>
            <srm-col
              style="text-align: right"
            >
              <el-button
                type="primary"
                @click="queryContractData2"
              >
                {{ $t("common.search") }}
              </el-button>
            </srm-col>
          </srm-row>
        </el-form>
        <el-table
          ref="catSelector2"
          style="width: 100%"
          height="311px"
          border
          highlight-current-row
          :data="contractDataList2"
        >
          <el-table-column
            align="center"
            type="index"
            width="50"
          />
          <!-- 合同编码 -->
          <el-table-column
            prop="contractCode"
            min-width="200"
            align="center"
            :label="$t('contractMod.contractCode')"
            :show-overflow-tooltip="true"
          />
          <!-- 合同名称 -->
          <el-table-column
            prop="contractName"
            min-width="200"
            align="center"
            :label="$t('contractMod.contractName')"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            :label="$t('common.operation')"
            width="60"
            align="center"
          >
            <template slot-scope="scope">
              <el-button
                type="text"
                @click="frameworkAgreementCodeHandle(scope.$index, scope.row)"
              >
                {{ $t("common.save") }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </srm-dialog>
      <!-- 发布到签章平台 -->
      <srm-dialog
        :title="$t('contractMod.releaseSignPlatform1')"
        size="midden"
        :visible.sync="releaseParamsVisible"
      >
        <p style="color: red">
          * {{ $t("contractMod.requiedMessge") }}
        </p>
        <el-form
          ref="releaseParams"
          :model="releaseParams"
          :rules="rules"
        >
          <el-form-item
            prop="name"
            :label="$t('dataConfMod.userName')"
          >
            <el-input v-model="releaseParams.name" />
          </el-form-item>
          <el-form-item
            prop="phone"
            :label="$t('contractMod.phone')"
          >
            <el-input v-model="releaseParams.phone" />
          </el-form-item>
          <el-form-item
            prop="email"
            :label="$t('dataConfMod.email')"
          >
            <el-input v-model="releaseParams.email" />
          </el-form-item>
        </el-form>
        <div
          slot="footer"
          class="dialog-footer"
        >
          <el-button @click="releaseParamsVisible = false">
            {{
              $t("common.cancel")
            }}
          </el-button>
          <el-button
            type="primary"
            @click="release"
          >
            {{
              $t("common.confirm")
            }}
          </el-button>
        </div>
      </srm-dialog>
    </el-main>
    <iframe
      ref="iframe"
      style="display: none"
      :src="pdfUrl"
    />
  </el-container>
</template>
<script>
import BaseForm from 'lib@/components/BaseForm'
import BaseTable from 'lib@/components/BaseTable'
import QuickSearch from 'lib@/components/QuickSearch'
import { FILE_UPLOAD } from '@/api/common'
import { loadJS } from '@/utils'

import materialList from './material-list'
import payPlan from './pay-plan'
import partner from './partner'
import cloneDeep from 'lodash/cloneDeep'
import CToolbar from 'lib@/components/c-toolbar'
import { tabTodoMixin } from '@/utils/mixins'
import Parser from 'modb@/contractManagement/views/contractManager/parser'
import OrganizationSelector from 'lib@/components/organization-selector'
import getCompanyDetail from './companyCache'
import WaterMark from '@/assets/images/watermark2.png'
import { getToken } from '@/utils/auth'

import WorkflowCommon from '@/library/mixins/workflow-common'
import axios from 'axios'
import { numericUppercase } from 'lib@/utils/number'
import DictSelect from '@/library/components/c-select/dict-select'
import { sysPrefix } from '@/config/ipConfig'
import { contractManagement } from 'modb@/contractManagement/api/index'

import { bus } from 'lib@/components/render-engine/components/bus'
import { getMenuInfo } from '@/utils/menu-auth'

export default {
  name: 'ContractInformation',
  components: {
    CToolbar,
    BaseForm,
    BaseTable,
    DictSelect,
    payPlan,
    materialList,
    partner,
    QuickSearch,
    OrganizationSelector
  },
  directives: {
    // 使用局部注册指令的方式
    resize: {
      // 指令的名称
      bind (el, binding) {
        // el为绑定的元素，binding为绑定给指令的对象
        let width = ''
        let height = ''
        function isReize () {
          const style = document.defaultView.getComputedStyle(el)
          if (width !== style.width || height !== style.height) {
            binding.value() // 关键
          }
          width = style.width
          height = style.height
        }
        el.__vueSetInterval__ = setInterval(isReize, 300)
      },
      unbind (el) {
        clearInterval(el.__vueSetInterval__)
      }
    }
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      // 限制日期
      endTiumePickerOptions: {
        disabledDate: time => {
          let entTime = new Date(this.mergeForm.effectiveDateFrom)
          return time.getTime() < entTime.getTime()
        }
      },
      integrationMode: '',
      bolClick: true,
      waterBol: 'Y', // 是否开启水印功能，Y为开启
      supplementaryAgreement: '',
      materialDataChangeBol: 0,
      pdfUrl: '',
      releaseParamsVisible: false,
      releaseParams: {
        name: '',
        phone: '',
        email: ''
      },
      rules: {
        contractName: [{ required: true, message: this.$t('common.pleaseInput') }],
        buId: [{ required: true, message: this.$t('common.pleaseInput') }],
        vendorName: [{ required: true, message: this.$t('common.pleaseInput') }],
        formal: [{ required: true, message: this.$t('common.pleaseInput') }],
        contractClass: [{ required: true, message: this.$t('common.pleaseInput') }],
        effectiveDateTo: [{ required: true, message: this.$t('common.pleaseInput') }],
        effectiveDateFrom: [{ required: true, message: this.$t('common.pleaseInput') }],
        needVendorConfirm: [{ required: true, message: this.$t('common.pleaseInput') }],
        modelEnable: [{ required: true, message: this.$t('common.pleaseInput') }],
        modelHeadId: [{ required: true, message: this.$t('common.pleaseInput') }],
        enable: [{ required: true, message: this.$t('common.pleaseInput') }],
        currencyCode: [{ required: true, message: this.$t('common.pleaseInput') }],
        isFrameworkAgreement: [{ required: true, message: this.$t('common.pleaseInput') }],
        isInvalidOldContract: [{ required: true, message: this.$t('common.pleaseInput') }],
        supplementAgreementExplain: [{ required: true, message: this.$t('common.pleaseInput') }]
      },
      watermark_wrapper_style: {
        position: 'absolute',
        top: 0,
        left: 0,
        zIndex: 0,
        width: '100%',
        height: '100%'
      },
      watermark_style: {
        display: 'inline-block',
        width: '650px',
        height: '247px',
        opacity: 0.3,
        margin: '0 auto'
      },
      preview_wrapper_style: {
        // IE浏览器 649px x 978px
        width: '794px',
        // height: '1123px',
        margin: '0 auto',
        position: 'relative'
      },
      contractDataList2: [],
      frameworkAgreementVisible: false,
      sumForm2: {
        vendorId: null,
        vendorCode: null,
        vendorName: null,
        isFrameworkAgreement: 'Y',
        pageSize: 9999,
        pageNum: 1
      },
      jumpLogin: true, // BPM在无登录情况下执行
      materialSelection: [], // 选择的物料
      illegal: '', // 兼容禁用
      curOpt: 'add', // 默认
      payPlanData: [],
      partnerData: [],
      watermark_base64: null,
      lastElemContractType: null,
      materialListData: [],
      IS_READ_ONLY: false,
      sourceList: [],
      queryParams: {},
      originCustomTable: {},
      menuInfo: null,
      selectionVisible: false,
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'vendorBiddingManagement',
        fileType: 'images'
      },
      activeDims: ['1', '2', '3', '4', '5', '6', '7', '8', '9'],
      userType: this.$store.getters.userType,
      diffhtml: null,
      diffVisible: false,
      contenteditable: false,
      editorInstance: null,
      placeholder2html: {},
      modelList: [],
      modelLine: [],
      mergeForm: {
        ceeaControlMethod: '',
        modelHeadId: '',
        isInvalidOldContract: 'N',
        contractClass: '',
        vendorName: '',
        buId: '',
        contractName: '',
        contractHeadId: null,
        contractType: '',
        contractStatus: '',
        drafterOpinion: '',
        supplementAgreementExplain: '',
        contractRemark: '',
        signingAddress: '',
        effectiveDateFrom: '',
        effectiveDateTo: '',
        modelEnable: 'Y',
        needVendorConfirm: 'Y',
        formal: '',
        isFrameworkAgreement: 'Y',
        enable: 'Y',
        ceeaIfVirtual: 'N',
        currencyCode: 'CNY',
        currencyId: '39',
        includeTaxAmount: 0,
        currencyName: '人民币',
        contractCode: '', // 合同编码
        partyContacts: '', // 甲方联系人
        partyPhone: '', // 甲方手机号
        partyAddress: '', // 甲方地址
        partyFax: '', // 甲方传真
        secondPartyContacts: '', // 乙方联系人
        secondPartyPhone: '', // 乙方手机号
        secondPartyAddress: '', // 乙方地址
        secondPartyFax: '', // 乙方传真
        thirdPartyContacts: '', // 丙方联系人
        thirdPartyPhone: '', // 丙方手机号
        thirdPartyAddress: '', // 丙方地址
        thirdPartyFax: '', // 丙方传真
        totalItems: '0', // 物料合计
        totalMaterialAmount: '0', // 物料金额合计(大写)
        partyTaxpayer: '', // 甲方-纳税人识别号
        partyBank: '', // 甲方-开户行
        partyBankAccount: '', // 甲方-银行账号
        partyTax: '', // 甲方-税号
        secondPartyTaxpayer: '', // 乙方-纳税人识别号
        secondPartyBank: '', // 乙方-开户行
        secondPartyBankAccount: '', // 乙方-银行账号
        secondPartyTax: '', // 乙方-税号
        thirdPartyTaxpayer: '', // 丙方-纳税人识别号
        thirdPartyBank: '', // 丙方-开户行
        thirdPartyBankAccount: '', // 丙方-银行账号
        thirdPartyTax: '' // 丙方-税号
      },
      fileuploads: [],
      childContext: null,
      visible: true,
      vendorIdList: [],
      currencyList: [],
      currencyList2: [],
      originMaterialTable: [],
      originPayPlanTable: [],
      bankRowIndex: null,
      taxList: [],
      currenRows: [],
      modelHeadIdList: [],
      columns: [
        {
          attrs: {
            width: '50',
            align: 'center',
            type: 'selection'
          }
        },
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            type: 'index',
            label: context => context.$t('contractMod.order')
          }
        },
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'materialCode',
            label: context => context.$t('contractMod.materialCode')
          }
        },
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'materialName',
            label: context => context.$t('contractMod.materialName')
          }
        },
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'categoryName',
            label: context => context.$t('contractMod.categoryName')
          }
        },
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'taxedPrice',
            label: context => context.$t('contractMod.taxedPrice')
          }
        },
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'contractQuantity',
            label: context => context.$t('contractMod.contractQuantity')
          }
        },
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'unitName',
            label: context => context.$t('contractMod.unitName')
          }
        },
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'buName',
            label: context => context.$t('contractMod.buId')
          }
        }
      ]
    }
  },
  computed: {
    workflowBusinessId () {
      // 用来指定工作流的业务ID
      return this.mergeForm.contractHeadId ? this.mergeForm.contractHeadId : null
    },
    acceptFileType () {
      if (this.mergeForm.modelEnable == 'N' && this.mergeForm.formal == 'ELECTRONIC_CONTRACT') {
        return ['pdf']
      } else {
        return ['jpeg', 'jpg', 'png', 'xls', 'xlsx', 'docx', 'pdf']
      }
    },
    workflowTabDisabled () {
      return !['SUPPLIER_CONFIRMED', 'REJECTED', 'WITHDRAW', 'APPROVAL', 'UNDER_REVIEW', 'UN_ARCHIVED', 'SIGNATUREING', 'ARCHIVED', 'TERMINATED', 'ABANDONED'].includes(this.mergeForm.contractStatus) || this.mergeForm.contractStatus === 'DRAFT'
    },
    viewUpdateButtonsubmit () {
      if (this.workflowParamsInfo.integrationMode !== 'None') { // 有工作流
        return (
          this.mergeForm.needVendorConfirm != 'Y' && this.IS_BUYER() &&
          (['DRAFT', 'WITHDRAW', 'REJECTED'].includes(this.mergeForm.contractStatus) ||
            (this.$attrs.params ? this.$attrs.params.flag === 'add' : null))
        )
      } else {
        if (this.mergeForm.needVendorConfirm != 'Y') {
          if (this.IS_BUYER() &&
            (['DRAFT', 'WITHDRAW', 'REJECTED', 'SUPPLIER_CONFIRMED'].includes(this.mergeForm.contractStatus) ||
              (this.$attrs.params ? this.$attrs.params.flag === 'add' : null))) {
            return true
          } else {
            return false
          }
        } else {
          if (this.IS_BUYER() &&
            (['SUPPLIER_CONFIRMED'].includes(this.mergeForm.contractStatus))) {
            return true
          } else {
            return false
          }
        }
      }
    },
    viewUpdateButtonSave () {
      return (
        this.mergeForm.needVendorConfirm != 'Y' && (['DRAFT', 'WITHDRAW', 'REJECTED'].includes(this.mergeForm.contractStatus) ||
          (this.$attrs.params ? this.$attrs.params.flag === 'add' : null))
      )
    },
    disabledUpdateButton () {
      return this.SUBMIT_STATUS()
    },
    isFromPriceApproval () {
      return this.mergeForm.sourceType === 'PRICE_APPROVAL'
    },
    materialEditableRows () {
      return this.materialListData.filter(i => {
        if (i.handleMark === undefined || i.handleMark === 0) {
          return true
        }
        return false
      })
    },
    viewUpdateButton () {
      return (
        this.curRole === 'BUYER' &&
        !this.isReadOnly &&
        this.requirementHead.auditStatus !== 'APPROVED'
      )
    },
    curStatus: function () {
      if (this.mergeForm.contractStatus === 'DRAFT' || this.mergeForm.contractStatus === '' || this.ARCHIVED()) {
        return 0
      } else if (['SUPPLIER_CONFIRMED', 'SUPPLIER_REJECTED', 'SUPPLIER_CONFIRMED'].includes(this.mergeForm.contractStatus)) {
        return 1
      } else if (['UNDER_REVIEW', 'REFUSED', 'SIGNATUREING'].includes(this.mergeForm.contractStatus)) {
        return 2
      } else if (this.mergeForm.contractStatus === 'APPROVAL') {
        return 3
      } else if (this.mergeForm.contractStatus === 'ARCHIVED') {
        // 已审批
        return 4
      } else {
        return 0
      }
    }
  },
  watch: {
    // 监听保存提交 按钮变更状态，如果自定义按钮则无需添加
    viewUpdateButtonsubmit () {
      this.buttonConfigInfo.submit.view = this.viewUpdateButtonsubmit
    },
    viewUpdateButtonSave () {
      this.buttonConfigInfo.save.view = this.viewUpdateButtonSave
    },
    disabledUpdateButton () {
      this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton
    }
  },
  created () {
    this.buttonConfigInfo.save.name = '暂存'
    this.buttonConfigInfo.save.view = this.viewUpdateButtonSave
    this.buttonConfigInfo.submit.view = this.viewUpdateButtonsubmit
    this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
    this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton
    this.buttonConfigInfo.cancel.view = false
    this.buttonConfigInfo.close.view = false
    this.loadBase64(WaterMark)
    this.illegal = this.$attrs.params.illegal
    this.jumpLogin = this.$attrs.params.jumpLogin
    if (this.illegal == 'view') {
      this.userType = 'BUYER'
    }
  },
  mounted () {
    this.curOpt = this.$attrs.params.flag
    this.initData()
  },
  methods: {
    release () {
      this.$refs.releaseParams.validate(async boolean => {
        if (boolean) {
          if (this.bolClick) { // 防双击
            this.bolClick = false
            const _this = this
            setTimeout(function () {
              _this.bolClick = true
            }, 1500)
            let data = {}
            if (this.mergeForm.modelEnable === 'Y') {
              const fileData = await this.uplaodPDF()
              const { fileuploadId } = fileData
              const { contractHeadId } = this.mergeForm
              data = {
                contractHeadId,
                fileuploadId,
                ...this.releaseParams
              }
            } else {
              const { contractHeadId } = this.mergeForm
              data = {
                contractHeadId,
                ...this.releaseParams
              }
            }

            contractManagement.contract.release(data).then(res => {
              this.releaseParamsVisible = false
              this.$message.success(
                this.$t('contractMod.successPostSignPlatform')
              ) // 发布到签章平台成功！
              this.cancel()
            })
          }
        }
      })
    },
    outerHandleAttachmentRemove (row) {
      row.fileSourceName = ''
      row.fileuploadId = ''
    },
    formalChange (value) {
      if (value === 'N') {
        let attr = []
        this.fileuploads.forEach((e) => {
          attr.push(e.fileType)
        })
        let bol = attr.includes('CONTRACT_AGREEMENT')
        if (!bol) {
          this.fileuploads.push({
            fileuploadId: null,
            fileSourceName: '',
            fileType: 'CONTRACT_AGREEMENT',
            del: 'N'
          })
        }
      } else {
        this.fileuploads.forEach((e, index) => {
          if (e.fileType == 'CONTRACT_AGREEMENT') {
            this.fileuploads.splice(index, 1)
          }
        })
      }
    },
    isInvalidOldContract (value) {
      if (value === 'Y') {
        let attr = []
        this.fileuploads.forEach((e) => {
          attr.push(e.fileType)
        })
        let bol = attr.includes('TERMINATION_AGREEMENT')
        if (!bol) {
          this.fileuploads.push({
            fileuploadId: null,
            fileSourceName: '',
            fileType: 'TERMINATION_AGREEMENT',
            del: 'N'
          })
        }
      } else {
        this.fileuploads.forEach((e, index) => {
          if (e.fileType == 'TERMINATION_AGREEMENT') {
            this.fileuploads.splice(index, 1)
          }
        })
      }
    },
    ARCHIVED () {
      let bol = false
      if (this.mergeForm.contractStatus === 'ARCHIVED' && (this.mergeForm.contractType == 'MIAN_CONTRACT_ALTER' || this.mergeForm.contractType == 'SUPPLEMENTAL_AGREEMENT')) {
        bol = true
      }
      return bol
    },
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'CONTRACT'
    },
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    // async getWorkflowBusinessVariables() { // 定义流程变量，如果没有可以不添加
    //   return {
    //     Amount: this.requirementHead.ceeaTotalBudget
    //   }
    // },

    fillReleaseParams () {
      this.releaseParamsVisible = true
    },
    async getPdfFile (flag = false) {
      // let htmlBody = this.$refs.printContent.innerHTML
      let htmlBody1 = this.$refs.printContent.innerHTML
      let htmlBody = htmlBody1.replace('disabled="disabled"', ' ')
      const res = await axios({
        url: '/egg/upload',
        method: 'POST',
        loading: true,
        data: {
          options: {
            format: 'a4',
            margin: {
              left: '1cm',
              top: '1cm',
              right: '1cm',
              bottom: '1cm'
            }
          },
          htmlString: '<div style="page-break-inside: avoid;overflow: hidden;font-family: simsun;">' + htmlBody + '</div>'
        },
        responseType: 'arraybuffer'
      })
      const blob = new Blob([res.data], { type: 'application/pdf' })

      if (this.waterBol == 'Y') {
        const formData = new FormData()
        formData.append('file', blob, 'myfile.pdf')
        let menuInfo = getMenuInfo()
        const pdf = await axios({
          url: `${sysPrefix()}/api-base/pdf/pdfAddWatermark`,
          method: 'POST',
          data: formData,
          headers: {
            Authorization: 'Bearer ' + getToken(),
            contentType: 'form-data',
            'X-Fun-Info': menuInfo.secretKey
          },
          responseType: 'arraybuffer',
          loading: true
        })
        console.log('[pdf]', pdf.data instanceof ArrayBuffer)
        let blobs = new Blob([pdf.data], { type: 'application/pdf' })
        if (flag) {
          this.pdfUrl = URL.createObjectURL(blobs)
          setTimeout(() => {
            this.$refs.iframe.contentWindow.print()
          }, 1000)
        }
        return blobs
      } else {
        if (flag) {
          this.pdfUrl = URL.createObjectURL(blob)
          setTimeout(() => {
            this.$refs.iframe.contentWindow.print()
          }, 1000)
        }
        return blob
      }
    },
    async uplaodPDF () {
      const blob = await this.getPdfFile()
      const file = new window.File([blob], 'myfile.pdf', {
        type: 'application/pdf'
      })
      const data = {
        file,
        uploadType: 'DEF',
        sourceType: 'WEB_APP',
        fileModular: 'api-cm',
        fileFunction: 'contractInformation',
        fileType: 'pdf'
      }
      const formData = new FormData()
      formData.append('file', file)
      for (const [key, value] of Object.entries(data)) {
        formData.append(key, value)
      }
      const { data: file_data } = await this.$http({
        url: FILE_UPLOAD,
        method: 'POST',
        data: formData,
        headers: {
          Authorization: 'Bearer ' + getToken(),
          contentType: 'form-data'
        },
        loading: true
      })
      return file_data
    },
    resizeHandle () {
      this.clearImageChildren()
      const container = this.$refs.printContent
      if (!container) return
      const scrollHeight = container.scrollHeight
      const clientHeight = container.clientHeight
      const offsetHeight = container.offsetHeight
      const height = Math.max(scrollHeight, clientHeight, offsetHeight)
      const width = container.scrollWidth
      const ImageHeight = (944 * width) / 2480
      const image = document.createElement('img')
      const style = image.style
      style.position = 'absolute'
      style.left = 0
      style.width = width + 'px'
      style.height = ImageHeight + 'px'
      style.opacity = '0.1'
      style.zIndex = 2
      style.pointerEvents = 'none'
      image.src = this.watermark_base64
      const count = Math.ceil(height / ImageHeight) * 1.05
      console.log('[count]', count)
      for (let i = 0; i < count; i++) {
        if (i === 0) {
          style.top = 0
          container.appendChild(image)
        } else {
          const node = image.cloneNode(true)
          node.style.top = i * ImageHeight + 'px'
          container.appendChild(node)
        }
      }
    },
    clearImageChildren () {
      const imageChildrens = document.querySelectorAll(
        '#preview_wrapper > img'
      )
      imageChildrens.forEach(node => {
        this.$refs.printContent.removeChild(node)
      })
    },
    queryContractData2 () {
      this.$http({
        url:
          '/api-cm/contract/contractHead/listContractHeadByIsMainAndVendorId',
        method: 'POST',
        data: this.sumForm2,
        loading: true
      })
        .then(res => {
          this.contractDataList2 = res.data.list
          this.frameworkAgreementVisible = true
        })
        .catch(err => {
          console.log(err)
        })
    },
    showFrameworkAgreement () {
      const { vendorId, vendorName } = this.mergeForm
      this.sumForm2.vendorId = vendorId
      this.sumForm2.vendorName = vendorName
      if (!vendorId || !vendorName) {
        return this.$message.error(this.$t('bid_mod.setPermissionError')) // 请先选择供应商
      }
      this.queryContractData2()
    },
    frameworkAgreementCodeHandle (index, row) {
      const { contractCode, contractHeadId, contractName } = row
      this.mergeForm.frameworkAgreementCode = contractCode
      this.mergeForm.frameworkAgreementId = contractHeadId
      this.mergeForm.frameworkAgreementName = contractName
      // 虚拟合同 合同编号和框架协议编号一样
      if (this.mergeForm.ceeaIfVirtual === 'Y') {
        this.mergeForm.contractCode = contractCode
      }
      this.frameworkAgreementVisible = false
    },
    materialHandleSelectionChange (row) {
      this.materialSelection = row
    },
    // 发起价格变更
    priceChange () {
      if (this.materialSelection.length > 0) {
        let materialSelection = this.materialSelection
        let firstRowNum = materialSelection[0].sourceNumber // 选中第一行的寻源编号
        for (let i of materialSelection) {
          if (!i.sourceNumber) {
            this.$message.warning(this.$t('contractMod.msgContractManage[4]')) // 选中的物料必须有寻源单号!
            return
          }
          if (i.sourceNumber !== firstRowNum) {
            this.$message.warning(this.$t('contractMod.msgContractManage[5]')) // 选中的物料必须是同一个寻源单号!
            return
          }
        }
        contractManagement.contract
          .cratePriceChangeSource(materialSelection)
          .then(res => {
            this.$message.success(res.message)
          })
      } else {
        this.$message.warning(this.$t('contractMod.msgContractManage[6]')) // 请先选择需要变更的物料!
      }
    },
    back () {
      this.cancel()
    },
    buHandler (node, value) {
      const { organizationCode, organizationName, fullPathId, organizationId } = node
      this.mergeForm.buCode = organizationCode
      // this.mergeForm.buName = organizationName;
      this.$set(this.mergeForm, 'buName', organizationName)
      this.mergeForm.buFullPathId = fullPathId

      this.partnerData.forEach((e, index) => {
        if (e.partnerType == '甲方') {
          this.partnerData.splice(index, 1)
        }
      })
      if (this.materialDataChangeBol == 1) {
        return false
      }
      this.materialDataChangeBol = 1
      this.$http({
        url: `/api-base/organization/organization/getCompanyByOuId?organizationId=${organizationId}`,
        method: 'GET',
        loading: true
      }).then(res => {
        if (res.code != '0') {
          this.mergeForm.buId = ''
        }
        this.materialDataChangeBol = 0
        if (res.data.orgCompanyBankList.length > 0) {
          this.partnerData.push({
            partnerType: '甲方',
            ouId: res.data.organization.organizationId,
            partnerName: res.data.organization.organizationName,
            bankAccount: res.data.orgCompanyBankList[0].bankAccount,
            bankName: res.data.orgCompanyBankList[0].bankName
          })
        } else {
          this.partnerData.push({
            partnerType: '甲方',
            ouId: res.data.organization.organizationId,
            partnerName: res.data.organization.organizationName
          })
        }
      }, (resOrr) => {
        this.materialDataChangeBol = 0
        this.mergeForm.buId = ''
        this.mergeForm.buCode = ''
        this.$set(this.mergeForm, 'buName', '')
        this.mergeForm.buFullPathId = ''
      })
    },
    queryHandler () {
      const { organizationId, sourceNumber } = this.queryParams
      const params = { vendorId: this.mergeForm.vendorId }
      if (organizationId) params.organizationId = organizationId
      if (sourceNumber) params.sourceNumber = sourceNumber
      contractManagement.contract.getMaterialsBySource(params).then(res => {
        this.sourceList = res.data
      })
    },
    comfirmSelect () {
      this.currenRows.forEach(i => {
        const flag =
          (this.materialListData || []).findIndex(
            j => j.approvalBiddingItemId === i.approvalBiddingItemId
          ) === -1
        if (flag) {
          const item = { ...i }
          const price = this.calcPrice(i)
          if (price) {
            const { amount, unAmount = '' } = price
            item.unAmount = unAmount
            item.amount = amount
            const taxQuota = Number(amount - unAmount)
            if (!isNaN(taxQuota)) {
              item.taxQuota = taxQuota.toFixed(2)
            }
          }
          if (item.tradingLocations) {
            let tradingLocations = item.tradingLocations
            try {
              tradingLocations = JSON.parse(tradingLocations)
            } catch (e) {
              console.log('送货地址转换出错')
            }
            item.tradingLocations = tradingLocations
          }
          this.materialListData.push(item)
        }
      })
      this.selectionVisible = false
      // 计算合同总金额（含税）
      this.materialDataChange()
    },
    calcPrice (data) {
      const price = {}
      const { taxedPrice, contractQuantity, taxRate } = data
      if (taxedPrice && contractQuantity) {
        const amount = parseFloat(taxedPrice) * parseFloat(contractQuantity)
        price.amount = amount
        if (taxRate) {
          const unAmount = Number((amount / (1 + taxRate / 100)).toFixed(2))
          price.unAmount = unAmount
        }
        return price
      }
      return null
    },
    selectionChange (value) {
      this.currenRows = value
    },
    // 付款计划数据变化
    payPlanDatachange () {
      const totalPercent = this.payPlanData.reduce((sum, item) => {
        return sum + Number(item.paymentRatio)
      }, 0)
      if (totalPercent !== 100) {
        // this.$message.error(this.$t('contractMod.msgContractManage[7]')) // 付款比例之和不能大于100！
        this.$message.error('付款比例之和必须等于100！')
        return false
      } else {
        return true
      }
    },
    calcIncludeTaxAmount (value) {
      if (this.mergeForm.contractStatus === 'ARCHIVED') {
        console.log('无须计算')
        return
      }
      const v = value || this.materialEditableRows
      const totalAmount = v.reduce((sum, item) => {
        return Number(sum) + Number(item.amount)
      }, 0)
      console.log('[totalAmount]', totalAmount)
      this.mergeForm.totalItems = totalAmount + '元'
      this.mergeForm.totalMaterialAmount = numericUppercase(totalAmount)
      if (!isNaN(totalAmount)) {
        this.mergeForm.includeTaxAmount = Number(totalAmount).toFixed(2)
      }
    },
    async materialDataChange (value) {
      // 检查是否有新增的业务实体，有则自动添加到合作伙伴甲方
      console.log('[materialDataChange]', value)
      this.calcIncludeTaxAmount(value)
      if (value[0] && !value[0].buId) {
        return false
      }
    },
    queryOuDetail (ceeaOuId) {
      return this.$http({
        url: '/api-base/base/base-ou-group/queryById',
        method: 'GET',
        params: { id: ceeaOuId },
        loading: true
      })
    },
    currencyHandler (value, dictItem) {
      this.mergeForm.currencyId = dictItem.id
      this.mergeForm.currencyName = dictItem.label
    },
    elemContractTypeHandler (value, force = true) {
      if (this.mergeForm.modelHeadId && force) {
        this.$confirm(this.$t('contractMod.clearModelMsg'), {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        })
          .then(() => {
            this.queryModelHeadList(value)
            this.mergeForm.modelHeadId = null
            this.$refs.markedContent.innerHTML = ''
          })
          .catch(() => {
            console.log('点击取消')
            this.mergeForm.contractClass = this.lastElemContractType
          })
      } else {
        this.lastElemContractType = value
        this.queryModelHeadList(value)
      }
    },
    queryModelHeadList (value) {
      if (!value) {
        return false
      }
      contractManagement.modelListByType(value).then(res => {
        this.modelHeadIdList = res.data.map(i => ({
          id: i.modelCode,
          label: i.modelName,
          value: i.modelHeadId,
          type: i.modelType
        }))
      })
    },
    CONTRACT_HEAD_ID_STATUS () {
      if (!this.$refs.mainForm) return false
      const formData = this.mergeForm
      return (
        !formData.contractType || formData.contractType === 'MIAN_CONTRACT_ADD'
      )
    },
    SUBMIT_STATUS () {
      const state = this.mergeForm.contractStatus
      if (this.workflowParamsInfo.integrationMode == 'None' && ['SUPPLIER_CONFIRMED'].includes(state)) {
        return false
      }
      if (this.$attrs.params.isReadOnly) {
        return true
      }
      if (this.$attrs.params.flag === 'add') {
        return false
      }
      if (!this.IS_BUYER() && state === 'SUPPLIER_CONFIRMING') {
        return false
      }
      if (state && ['DRAFT', 'REFUSED', 'WITHDRAW'].includes(state)) {
        return false
      }
      return false
    },
    addUploadOne () {
      this.fileuploads.push({
        fileuploadId: null,
        fileSourceName: '',
        fileType: 'OTHER_AGREEMENT'
      })
    },
    outerHandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '', createdBy = '', creationDate = '' } = file || {}
      row.fileuploadId = fileId.toString()
      row.fileSourceName = fileName
      row.createdBy = createdBy
      row.creationDate = creationDate
    },
    handleDelClick (index, row) {
      this.fileuploads.splice(index, 1)
    },
    IS_BUYER () {
      return this.userType === 'BUYER'
    },
    getEditable () {
      // if (['REJECTED', 'WITHDRAW', 'EXPIRED'].includes(this.mergeForm.contractStatus)) {
      //   this.IS_READ_ONLY = true
      //   return true
      // }
      const flag = this.mergeForm.contractType !== 'MIAN_CONTRACT_ADD'
      if (this.mergeForm.contractType === 'MIAN_CONTRACT_ALTER') {
        return false
      }
      return flag ? !!this.mergeForm.contractClass : false
    },
    getEditableA () {
      if (this.mergeForm.contractType === 'MIAN_CONTRACT_ALTER') {
        return false
      } else {
        const flag = this.mergeForm.contractType !== 'MIAN_CONTRACT_ADD'
        return flag ? !!this.mergeForm.contractClass : false
      }
    },
    IS_MAIN_CONTRACT_ADD () {
      if (!this.$refs.mainForm) {
        return false
      }
      const mainData = this.mergeForm
      return mainData.contractType !== 'MIAN_CONTRACT_ADD'
    },
    initData () {
      this.IS_READ_ONLY = this.$attrs.params.isReadOnly
      if (this.$attrs.params.flag === 'add') {
        const contractType = this.mergeForm.contractType = this.$attrs.params.contractType // 合同类型
        // MIAN_CONTRACT_ALTER 变更
        if (
          ['MIAN_CONTRACT_ALTER', 'SUPPLEMENTAL_AGREEMENT'].includes(
            contractType
          )
        ) {
          const { rowId } = this.$attrs.params
          this.setContractInfo(rowId, contractType, 'get')
        } else {
          console.log(`[add] contractType : [${contractType}]`)
          this.$nextTick(() => {
            this.$set(this.mergeForm, 'contractType', contractType)
          })
        }
      }
      if (this.$attrs.params.flag !== 'add') {
        const { contractHeadId, contractType } = this.$attrs.params.row
        this.mergeForm.contractHeadId = contractHeadId
        this.setContractInfo(contractHeadId, contractType)
      }
    },
    async setContractInfo (
      contractHeadId,
      contractType,
      sourceId = '',
      isInit = true
    ) {
      const { data } = await contractManagement.contract.getInfoById(
        contractHeadId,
        sourceId
      )

      const {
        contractHead,
        modelLines,
        annexes,
        payPlans,
        contractMaterials,
        contractPartners
      } = data
      this.modelLine = modelLines
      this.fileuploads = annexes
      this.payPlanData = payPlans.map(i => ({
        ...i,
        payExplain: Number(i.payExplain)
      }))
      this.partnerData = contractPartners
      if (contractType === 'MIAN_CONTRACT_ADD') {
        // 新增
        this.mergeForm = Object.assign(this.mergeForm, contractHead)
      } else {
        const {
          contractType,
          contractCode,
          contractHeadId,
          ...rest
        } = contractHead
        if (this.$attrs.params.flag === 'add') {
          if (isInit) {
            this.fileuploads = []
            this.mergeForm = Object.assign(this.mergeForm, { ...rest })
            this.$set(
              this.mergeForm,
              'contractOldCode', // 原合同编号更改byEasion
              contractCode
            )
            this.$set(
              this.mergeForm,
              'ceeaContractOldId', // 原合同id
              contractHeadId
            )
            this.$set(
              this.mergeForm,
              'contractType',
              this.$attrs.params.contractType
            )
            // 合同变更 补充协议
            if (
              this.$attrs.params.contractType === 'MIAN_CONTRACT_ALTER' ||
              this.$attrs.params.contractType === 'SUPPLEMENTAL_AGREEMENT'
            ) {
              this.mergeForm.contractStatus = 'DRAFT'
              this.$set(this.mergeForm, 'contractChangeCode', '')
              this.$set(this.mergeForm, 'contractAgreementCode', '')
            }
          } else {
            this.mergeForm = Object.assign(this.mergeForm, contractHead)
          }
        } else {
          this.mergeForm = Object.assign(this.mergeForm, contractHead)
        }
      }
      this.materialListData = contractMaterials.map(item => {
        const i = { ...item }
        if (i.tradingLocations) {
          let tradingLocations = i.tradingLocations
          try {
            tradingLocations = JSON.parse(tradingLocations)
          } catch (e) {
            console.log('送货地址转换出错')
          }
          i.tradingLocations = tradingLocations
        }
        return i
      })
      this.compile(contractHead.content, isInit)
      this.elemContractTypeHandler(this.mergeForm.contractClass, false)
      if (this.childContext) {
        this.childContext.elemKeys = modelLines.reduce((obj, i) => {
          const { modelKey, modelValue } = i
          let value = modelValue
          try {
            if (isNaN(modelValue)) {
              value = JSON.parse(modelValue)
            }
          } catch (e) {

          }
          obj[modelKey] = value
          return obj
        }, {})
      }
      this.$set(this.mergeForm, 'contractHeadId', contractHeadId)
    },
    html2diff () {
      this.preview()
      this.$nextTick(async () => {
        const oldContent = await this.buildOldContent()
        const newContent = this.$refs.markedContent.innerHTML
        console.log(oldContent)
        if (typeof Worker === 'undefined') {
          loadJS('./htmldiff.js', () => {
            this.diffhtml = getHTMLDiff(oldContent, newContent)
            this.diffVisible = true
          })
        } else {
          const worker = new Worker('./htmldiff.js')
          worker.postMessage({
            newVersion: newContent,
            oldVersion: oldContent
          })
          worker.onmessage = evt => {
            this.diffhtml = evt.data
            this.diffVisible = true
          }
        }
      })
    },

    async buildOldContent () {
      const res1 = await contractManagement.getById(this.mergeForm.modelHeadId)
      const res2 = await contractManagement.modelLine.getModelLine(
        this.mergeForm.modelHeadId
      )
      const { flag } = this.$attrs.params
      const arr = flag === 'add' ? res2.data : this.modelLine
      const initialModelValue = arr.reduce((obj, i) => {
        const { modelKey, modelValue } = i
        let value = modelValue
        try {
          value = JSON.parse(modelValue)
        } catch (e) {

        }
        obj[modelKey] = value
        return obj
      }, {})
      let content = res1.data.content
      // 替换分页符
      const breakPageMatcher = /_ueditor_page_break_tag_/g // 匹配分页符号
      content = content.replace(breakPageMatcher, ($0, $1) => {
        return '<div class="breakPage" style="break-after: page;"></div>'
      })

      this.templateCompile(content, initialModelValue)
      return this.$refs.template.innerHTML
    },
    print () {
      this.preview()
      this.getPdfFile(true)
      // this.$nextTick(() => {
      //   const bdhtml = this.$refs.printContent.outerHTML;
      //   // 设置打印内容
      //   this.editorInstance.setContent(bdhtml);
      //   // 打印
      //   this.editorInstance.execCommand("print");
      // });
    },
    loadBase64 (src) {
      const image = new Image()
      image.src = src
      image.onload = () => {
        this.watermark_base64 = this.getBase64Image(image)
        this.resizeHandle()
        console.log('[set watermark_base64 success]')
      }
    },
    getBase64Image (img) {
      var canvas = document.createElement('canvas')
      canvas.width = img.width
      canvas.height = img.height
      var ctx = canvas.getContext('2d')
      ctx.drawImage(img, 0, 0, img.width, img.height)
      var ext = img.src.substring(img.src.lastIndexOf('.') + 1).toLowerCase()
      var dataURL = canvas.toDataURL('image/' + ext)
      return dataURL
    },

    edit () {
      if (this.childContext) {
        this.childContext.editable = true
      }
      if (this.mergeForm.enable === 'N') {
        this.contenteditable = true
      }
    },
    preview () {
      if (this.childContext) {
        this.childContext.editable = false
      }
      this.contenteditable = false
    },
    saveBill (type) {
      console.log(type)
      if (type === 'SUBMIT') {
        this.preview()
        this.$nextTick(() => this.submit('approval'))
      } else if (type === 'SAVE') {
        this.preview()
        this.$nextTick(() => this.submit())
      }
    },
    submitHandle () {
      this.preview()
      this.$nextTick(() => this.submit())
    },
    async submit (type = 'submit') {
      // 计算合同总金额
      this.calcIncludeTaxAmount()
      if (this.mergeForm.ceeaIsPortableContract === 'Y' && (type === 'approval' || type === 'publish')) {
        if (this.mergeForm.includeTaxAmount > 20000) {
          // "合同金额大于2万，不能设置为便捷合同，已自动帮您修改为非便捷合同！"
          this.$message.warning(this.$t('contractMod.msgContractManage[10]'))
          this.mergeForm.ceeaIsPortableContract = 'N'
          this.__jump_error__('mainForm')
          return false
        }
      }
      const { row, flag } = this.$attrs.params
      let cloneFrom
      if (this.childContext) {
        cloneFrom = cloneDeep(this.childContext.elemKeys)
      }
      // 校验是否有多个甲方
      let bolpartnerType = 0
      let bolpartnerType2 = 0
      this.partnerData.forEach(data09 => {
        if (data09.partnerType == '甲方') {
          bolpartnerType++
        }
        if (data09.partnerType == '乙方') {
          bolpartnerType2++
        }
      })
      if (bolpartnerType > 1) {
        return this.__jump_error__(
          'partnerList',
          null,
          this.$t('只能有一个甲方')
        )
      }
      if (bolpartnerType2 > 1) {
        return this.__jump_error__(
          'partnerList',
          null,
          this.$t('只能有一个乙方')
        )
      }

      // 校验文件是否上传
      let isNull = this.fileuploads.some(i => !i.fileuploadId)
      if (!this.fileuploads.length) isNull = true
      if (isNull && (type === 'approval' || type === 'publish') && this.mergeForm.modelEnable == 'N') {
        return this.__jump_error__(
          'file-area',
          null,
          this.$t('contractMod.msgContractManage[11]')
        )
      }
      const modelLines = []
      const form = this.mergeForm
      if (cloneFrom) {
        for (const [key, value] of Object.entries(cloneFrom)) {
          try {
            const modelLineId = (
              this.modelLine.find(i => key === i.modelKey) || {}
            ).modelLineId
            if (Array.isArray(value)) {
              modelLines.push({
                modelLineId: modelLineId || null,
                modelKey: key,
                modelValue: JSON.stringify(value)
              })
            } else {
              modelLines.push({
                modelLineId: modelLineId || null,
                modelKey: key,
                modelValue: value
              })
            }
          } catch (e) {}
        }
      }
      let finalHTML = null
      try {
        const bdhtml = this.$refs.markedContent.innerHTML
        finalHTML = Parser.unReplacer(bdhtml)
      } catch (e) {

      }

      const { effectiveDateTo, effectiveDateFrom, ...rest } = form
      if ((type === 'approval' || type === 'publish') && this.mergeForm.ceeaIfVirtual === 'Y') {
        if (!this.mergeForm.frameworkAgreementCode) {
          return this.__jump_error__(
            'mainForm',
            null,
            this.$t('contractMod.msgContractManage[12]')
          )
        }
      }
      if (type === 'approval' && (!effectiveDateTo || !effectiveDateFrom) && type === 'publish') {
        return this.__jump_error__(
          'mainForm',
          null,
          this.$t('contractMod.msgContractManage[13]')
        )
      }
      let effectiveDateFrom2 = this.mergeForm.effectiveDateFrom
      let effectiveDateTo2 = this.mergeForm.effectiveDateTo
      if (new Date(effectiveDateFrom2.replace(/-/g, '/')) > new Date(effectiveDateTo2.replace(/-/g, '/'))) {
        return this.__jump_error__(
          'mainForm',
          null,
          this.$t('合同有效期有误')
        )
      }

      if (form.isFrameworkAgreement === 'N' && (type === 'approval' || type === 'publish')) {
        let payPlanDatachange = this.payPlanDatachange()
        if (!payPlanDatachange) {
          return false
        }
        const len2 = this.materialListData.length
        if (!len2) {
          return this.__jump_error__(
            'materialList',
            null,
            this.$t('contractMod.msgContractManage[27]')
          )
        }
        const len = this.payPlanData.length
        if (!len && (type === 'approval' || type === 'publish')) {
          return this.__jump_error__(
            'payList',
            null,
            this.$t('contractMod.msgContractManage[15]')
          )
        }
        let payPlanDataBol = 1
        this.payPlanData.forEach(e => {
          if (e.paymentPeriod && e.paymentStage && e.payExplain && e.dateNum && e.paymentRatio && e.plannedPaymentDate && e.payMethod) {

          } else {
            payPlanDataBol = 0
          }
        })
        if (payPlanDataBol == 0 && (type === 'approval' || type === 'publish')) {
          this.__focus_error__(this.$t('contractMod.payPlanDataBol'))
          return false
        }

        let materialListDataBol = 1
        this.materialListData.forEach(e => {
          if (e.invId && e.tradingLocations && e.materialCode && e.untaxedPrice && e.contractQuantity && e.taxRate) {

          } else {
            materialListDataBol = 0
          }
        })
        if (materialListDataBol == 0 && (type === 'approval' || type === 'publish')) {
          this.__focus_error__(this.$t('contractMod.materialListDataBol'))
          return false
        }
      }

      if (this.IS_BUYER()) {
        let data = null
        if (finalHTML) {
          data = {
            modelLines,
            annexes: this.fileuploads,
            payPlans: this.payPlanData,
            contractMaterials: this.materialEditableRows,
            contractPartners: this.partnerData,
            contractHead: {
              ...rest,
              content: finalHTML
            }
          }
        } else {
          data = {
            modelLines,
            annexes: this.fileuploads,
            payPlans: this.payPlanData,
            contractMaterials: this.materialEditableRows,
            contractPartners: this.partnerData,
            contractHead: {
              ...rest
            }
          }
        }

        data.contractHead.isDeleteLine = 'Y'
        data.contractHead.isSavePerCheck = 'Y'
        if (effectiveDateTo) {
          data.contractHead.effectiveDateTo = this.$dayjs(
            effectiveDateTo
          ).format('YYYY-MM-DD')
        }
        if (effectiveDateFrom) {
          data.contractHead.effectiveDateFrom = this.$dayjs(
            effectiveDateFrom
          ).format('YYYY-MM-DD')
        }
        try {
          if (type === 'approval' || type === 'publish') {
            await this.$refs.mainForm.validate()
            if (this.mergeForm.modelEnable == 'Y') {
              await this.$refs.formHeader2.validate()
            }
          }
        } catch (e) {
          this.__focus_error__(this.$t('contractMod.msgContractManage[14]'))
          return
        }
        try {
          if ((type === 'approval' || type === 'publish') && this.mergeForm.contractType == 'SUPPLEMENTAL_AGREEMENT') {
            await this.$refs.agreement.validate()
          }
        } catch (e) {
          this.__focus_error__(this.$t('contractMod.msgContractManage[14]'))
          return
        }
        if (['MIAN_CONTRACT_ALTER', 'SUPPLEMENTAL_AGREEMENT', 'TERMINATION'].includes(this.$attrs.params.contractType)) {
          const { mainContractNo } = this.$attrs.params
          data.contractHead.mainContractNo = mainContractNo
          if (this.$attrs.params.flag !== 'edit') {
            data.contractHead.contractOldCode = this.mergeForm.contractNo
            data.contractHead.ceeaContractOldId = this.mergeForm.contractHeadId
            data.contractHead.contractHeadId = ''
          }
        }
        if (type === 'approval') {
          // 提交审批之后就不能修改
          // 合同变更、补充协议 直接提交审批把原来主合同号赋值
          contractManagement.contract.approval(data).then(async res => {
            this.$message({
              type: 'success',
              message: res.message
            })
            let contractHeadId = res.data
            await this.setContractInfo(contractHeadId, type, '', false)
            await this.handlerAfter('SUBMIT')
          })
        } else if (type === 'savePublish') {
          // 发布到供应商暂存
          // 发布供应商暂存
          contractManagement.contract.savePublish(data).then(res => {
            this.$message({
              type: 'success',
              message: res.message
            })
            this.cancel()
          })
        } else if (type === 'publish') {
          // 发布到供应商
          // 发布供应商提交
          contractManagement.contract.publish(data).then(res => {
            this.$message({
              type: 'success',
              message: res.message
            })
            this.cancel()
          })
        } else {
          // 提交审批之后就不能修改
          if (['MIAN_CONTRACT_ALTER', 'SUPPLEMENTAL_AGREEMENT', 'TERMINATION'].includes(this.$attrs.params.contractType)) {
            const { mainContractNo } = this.$attrs.params
            data.contractHead.mainContractNo = mainContractNo
            if (this.$attrs.params.flag !== 'edit') {
              data.contractHead.contractOldCode = this.mergeForm.contractNo
              data.contractHead.ceeaContractOldId = this.mergeForm.contractHeadId
              data.contractHead.contractHeadId = ''
            }
          }
          contractManagement.contract.savePublish(data).then(res => {
            this.$message({
              type: 'success',
              message: res.message
            })
            this.cancel()
          })
        }
      } else {
        const data = {
          ...rest,
          content: finalHTML
        }
        try {
          if (effectiveDateTo) {
            data.contractHead.effectiveDateTo = this.$dayjs(
              effectiveDateTo
            ).format('YYYY-MM-DD')
          }
          if (effectiveDateFrom) {
            data.contractHead.effectiveDateFrom = this.$dayjs(
              effectiveDateFrom
            ).format('YYYY-MM-DD')
          }
        } catch (e) {}

        if (form.isFrameworkAgreement === 'N') {
          const len = this.payPlanData.length
          if (!len) {
            return this.__jump_error__(
              'payList',
              null,
              this.$t('contractMod.msgContractManage[15]')
            )
          }
        }
        if (type === 'SUPPLIER_CONFIRMING') {
          contractManagement.contract.vendorConfirm2(data.contractHeadId).then(res => {
            this.$message({
              type: 'success',
              message: res.message
            })
            this.cancel()
          })
        } else if (type === 'SUPPLIER_REFUSE') {
          contractManagement.contract.reject(data.contractHeadId).then(res => {
            this.$message({
              type: 'success',
              message: res.message
            })
            this.cancel()
          })
        } else {
          contractManagement.contract.vendorConfirm(data).then(res => {
            this.$message({
              type: 'success',
              message: res.message
            })
            this.initData()
            // this.cancel();
          })
        }
      }
    },
    cancel () {
      const { row, flag } = this.$attrs.params
      if (flag == 'add') {
        this.$emit('tab-remove', 'contractInformation')
      } else {
        this.$emit('tab-remove', 'contractInformation' + row.contractName)
      }
      this.__setTabTodo('contractList.getQuerydata')

      bus.$emit('ContractHead')
    },
    templateChange (val, isInit = true) {
      this.mergeForm.modelHeadId = val
      const model = this.modelHeadIdList.find(i => i.value === val)
      this.mergeForm.modelName = model ? model.label : ''
      if (!val) return
      this.compile(null, isInit)
      console.log('modelHeadIdList', this.modelHeadIdList, 'val', val)
    },
    getVendorObj (val, scope) {
      this.mergeForm.vendorId = val ? val.companyId : ''
      this.mergeForm.vendorName = val ? val.companyName : ''
      this.mergeForm.vendorCode = val ? val.companyCode : ''
      this.mergeForm.erpVendorCode = val ? val.erpVendorCode : ''
      this.mergeForm.erpVendorId = val ? val.erpVendorId : ''
      if (val.companyName) {
        const flag = this.partnerData.some(i => i.partnerType === '乙方')
        if (!flag) {
          this.partnerData.push({
            partnerType: '乙方',
            partnerName: val.companyName,
            lcCode: val.lcCode
          })
        }
      }
    },
    templateCompile (modeContent, initialModelValue) {
      this.$refs.template.innerHTML = ''
      const { vueTemplate } = Parser.replacer(modeContent, false)
      const wrapper = this.$refs.template
      const $el = Parser.generateComponent({
        html: vueTemplate,
        elemKeys: initialModelValue,
        context: this,
        wrapper
      })
      this.$refs.template.appendChild($el)
    },
    async compile (modeContent, isInit = true) {
      const { modelHeadId } = this.mergeForm
      if (!modelHeadId) {
        return
      }
      this.$refs.markedContent.innerHTML = ''
      let content = modeContent
      if (!modeContent) {
        const res = await contractManagement.getById(modelHeadId)
        const { enable, content: _content } = res.data
        content = _content
        if (isInit) {
          this.mergeForm.enable = enable
        }
      }
      const { flag } = this.$attrs.params
      let initialize = flag === 'add'
      if (!isInit) {
        initialize = isInit
      }
      // 替换分页符
      const breakPageMatcher = /_ueditor_page_break_tag_/g // 匹配分页符号
      content = content.replace(breakPageMatcher, ($0, $1) => {
        return '<div class="breakPage" style="break-after: page;"></div>'
      })
      if (content) {
        const { vueTemplate, elementCodes } = Parser.replacer(
          content,
          initialize
        )
        const wrapper = this.$refs.markedContent
        const $el = Parser.generateComponent({
          html: vueTemplate,
          elemKeys: elementCodes,
          context: this,
          wrapper
        })
        this.$refs.markedContent.appendChild($el)
      }
    }
  }
}
</script>
<style scoped>
.preview_wrapper {
  padding: 15px;
}
.conetnt .ins {
  background-color: #cfc;
  text-decoration: none;
}
.conetnt .del {
  color: #999;
  background-color: #fec8c8;
}
.paper {
  position: relative;
  padding: 0 40px 100px 40px;
  background: #fff;
  border: 1px solid #eee;
  float: left;
  margin: 10px;
  box-shadow: 0 0 12px 0 rgba(0, 0, 0, 0.06), 0 0 0 1px rgba(0, 0, 0, 0.04);
}
.paper::after,
.paper::before {
  content: "";
  position: absolute;
  bottom: 6px;
  width: 100px;
  height: 1px;
  z-index: -1;
  box-shadow: 0 2px 12px 5px rgba(0, 0, 0, 0.3);
}
.paper::after {
  left: 4px;
  transform: rotate(-6deg);
}
.paper::before {
  right: 4px;
  transform: rotate(6deg);
}
.base-form {
  padding: 15px 30px 0;
}
</style>
