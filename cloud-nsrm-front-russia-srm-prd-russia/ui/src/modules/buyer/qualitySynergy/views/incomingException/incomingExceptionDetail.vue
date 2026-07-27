<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <div class="form-container2">
        <div class="basic">
          <el-form
            ref="basic"
            :model="basic"
            label-width="80px"
            label-position="top"
            class="form-fill-style"
            :disabled="isReadOnly"
            :rules="rules"
          >
            <!-- 第一行 -->
            <srm-row>
              <srm-col>
                <!-- 业务实体 -->
                <el-form-item
                  :label="$t('bid_mod.businessEntity')"
                  :label-width="formLabelWidth"
                  prop="orgId"
                >
                  <el-input v-if="Viewflag == 'readOnly'" v-model="basic.orgName" />
                  <template v-else>
                    <OrganizationSelector
                      ref="organizationSelector"
                      v-model="basic.orgId"
                      :parent-id="-1"
                      :placeholder="$t('common.pleaseSelect')"
                      node-type="OU"
                      @select="selectHandler"
                    />
                  </template>
                </el-form-item>
              </srm-col>

              <!-- 库存组织 -->
              <srm-col>
                <el-form-item
                  :label="$t('qualitySynergy.organizationName')"
                  :label-width="formLabelWidth"
                  prop="organizationId"
                >
                  <el-input v-if="isReadOnly" v-model="basic.organizationName" disabled />
                  <OrganizationSelector
                    v-else
                    ref="organizationSelector2"
                    v-model="basic.organizationId"
                    :parent-id="basic.orgId"
                    node-type="INV"
                    :placeholder="$t('common.pleaseSelect')"
                    @select="selectHandler2"
                  />
                </el-form-item>
              </srm-col>
              <!-- 异常单号 -->
              <srm-col>
                <el-form-item
                  :label="$t('qualitySynergy.exceptionOrderId')"
                  :label-width="formLabelWidth"
                >
                  <el-input v-model="basic.itemExceptionHeadId" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <!-- 单据日期 -->
                <el-form-item :label="$t('qualitySynergy.orderDate')" :label-width="formLabelWidth">
                  <el-date-picker
                    v-model="basic.orderDate"
                    disabled
                    type="date"
                    value-format="yyyy-MM-dd"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
        </div>

        <el-collapse v-model="activeDims" class="tab-form-style">
          <!-- 不合格内容 -->
          <el-form
            ref="unqualifiedContent"
            :model="unqualifiedContent"
            label-width="80px"
            label-position="top"
            class="form-fill-style"
            :disabled="isReadOnly"
            :rules="unqualifiedContentRules"
          >
            <!-- 不合格内容 -->
            <el-collapse-item :title="$t('qualitySynergy.unqualifiedContent')" name="1">
              <srm-row>
                <!-- 供应商 -->
                <srm-col>
                  <el-form-item :label="$t('common.vendorName')" prop="vendorName">
                    <QuickSearch
                      :show-input="unqualifiedContent.vendorName"
                      show-key="companyName"
                      :scope-data="unqualifiedContent"
                      name="scc_sup_company_info_display_buyer"
                      :disabled="isReadOnly"
                      @close-quicksearch="getCompanyObj"
                    />
                  </el-form-item>
                </srm-col>
                <!-- 送货单号 -->
                <srm-col>
                  <el-form-item :label="$t('qualitySynergy.deliveryNumber')">
                    <el-input
                      v-model="unqualifiedContent.deliveryNumber"
                      clearable
                      :disabled="isReadOnly || !unqualifiedContent.vendorName || !basic.orgId"
                      suffix-icon="el-icon-search"
                      @click.native="getDeliveryNumber"
                    />
                  </el-form-item>
                </srm-col>

                <srm-col>
                  <el-form-item prop="materialCode" :label="$t('common.materialCode')">
                    <QuickSearch
                      :show-input="unqualifiedContent.materialCode"
                      show-key="materialCode"
                      :scope-data="unqualifiedContent"
                      name="scc_base_material_item_display"
                      :disabled="isReadOnly"
                      @close-quicksearch="getItemObj"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item prop="materialName" :label="$t('common.materialName')">
                    <el-input v-model="unqualifiedContent.materialName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 本次送货数量 -->
                  <el-form-item prop="itemTotal" :label="this.$t('qualitySynergy.itemTotal1')">
                    <el-input-number
                      v-model="unqualifiedContent.itemTotal"
                      style="width:100%"
                      :controls="false"
                      :min="0"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 检验单编号 -->
                  <el-form-item
                    :label="$t('qualitySynergy.checkListId')"
                    prop="checkListId"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="unqualifiedContent.checkListId" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 检验单类型 -->
                  <el-form-item
                    :label="$t('qualitySynergy.checkListType')"
                    prop="checkListType"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="unqualifiedContent.checkListType" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 检验单标准 -->
                  <el-form-item
                    :label="$t('qualitySynergy.inspectionOrderStandard')"
                    :label-width="formLabelWidth"
                    prop="checkStandard"
                  >
                    <el-input v-model="unqualifiedContent.checkStandard" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 工厂 -->
                  <el-form-item
                    :label="$t('qualitySynergy.factory')"
                    :label-width="formLabelWidth"
                    prop="factoryName"
                  >
                    <el-input v-model="unqualifiedContent.factoryName" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 采购员 -->
                  <el-form-item
                    :label="$t('qualitySynergy.purchaseAgent1')"
                    :label-width="formLabelWidth"
                    prop="purchaseAgent"
                  >
                    <QuickSearch
                      :show-input="unqualifiedContent.purchaseAgent"
                      show-key="nickname"
                      :scope-data="unqualifiedContent"
                      :disabled="isReadOnly"
                      name="scc_rbac_user_display"
                      @close-quicksearch="getUserObj1"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item label="lotId" :label-width="formLabelWidth" prop="lotId">
                    <el-input-number
                      v-model="unqualifiedContent.lotId"
                      style="width:100%"
                      :controls="false"
                      :min="0"
                      :disabled="isReadOnly"
                      :precision="0"
                      :maxlength="50"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 材料负责人 -->
                  <el-form-item
                    :label="$t('qualitySynergy.itemAgent1')"
                    :label-width="formLabelWidth"
                    prop="itemAgent"
                  >
                    <QuickSearch
                      :show-input="unqualifiedContent.itemAgent"
                      show-key="nickname"
                      :scope-data="unqualifiedContent"
                      :disabled="isReadOnly"
                      name="scc_rbac_user_display"
                      @close-quicksearch="getUserObj2"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 环保负责人 -->
                  <el-form-item
                    :label="$t('qualitySynergy.epAgent1')"
                    :label-width="formLabelWidth"
                    prop="epAgent"
                  >
                    <QuickSearch
                      :show-input="unqualifiedContent.epAgent"
                      show-key="nickname"
                      :scope-data="unqualifiedContent"
                      :disabled="isReadOnly"
                      name="scc_rbac_user_display"
                      @close-quicksearch="getUserObj3"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item label="D/C" :label-width="formLabelWidth" prop="dateCode">
                    <el-input-number
                      v-model="unqualifiedContent.dateCode"
                      style="width:100%"
                      :controls="false"
                      :min="0"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 保税物料 -->
                  <el-form-item
                    :label="$t('qualitySynergy.freeTaxItem')"
                    :label-width="formLabelWidth"
                    prop="freeTaxItem"
                  >
                    <el-input v-model="unqualifiedContent.freeTaxItem" />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>
          </el-form>
          <!-- 材料不合格描述 -->
          <el-collapse-item :title="$t('qualitySynergy.materialFailDesc')" name="2">
            <el-main style="min-height: 200px;">
              <p class="btn_line">
                <el-button
                  class="detail-pbtn"
                  type="primary"
                  :disabled="isReadOnly"
                  @click="addUqMaterial"
                >
                  {{ $t("common.add") }}
                </el-button>
              </p>
              <BaseTable
                ref="uqMaterialColumns"
                stripe
                :data-source="quaItemNgDescList"
                :columns="uqMaterialColumns"
                columns-name="uqMaterialColumns"
                :empty-text="$t('components.noData')"
                border
                :initialize="false"
                row-key="itemNgDescLineId"
                @asyncGetRealDataSource="
                  data => asyncGetRealDataSource('quaItemNgDescList', data)
                "
              >
                <template #checkList="{ scope }">
                  <el-input v-model="scope.row.checkList" />
                </template>
                <template #checkInfor="{ scope }">
                  <el-input v-model="scope.row.checkInfor" />
                </template>
                <template #itemSpecificCheckList="{ scope }">
                  <el-input v-model="scope.row.itemSpecificCheckList" />
                </template>
                <template #aqlStandardNum="{ scope }">
                  <el-input-number
                    v-model="scope.row.aqlStandardNum"
                    style="width:100%"
                    :controls="false"
                    :min="0"
                  />
                </template>
                <template #freeCheck="{ scope }">
                  <el-select v-model="scope.row.freeCheck">
                    <el-option
                      v-for="item in YesOrNoOptions"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </template>
                <template #fixedCheckTotal="{ scope }">
                  <el-input-number
                    v-model="scope.row.fixedCheckTotal"
                    style="width:100%"
                    :controls="false"
                    :min="0"
                  />
                </template>
                <template #checkTool="{ scope }">
                  <el-input v-model="scope.row.checkTool" />
                </template>
                <template #sampleTotal="{ scope }">
                  <el-input-number
                    v-model="scope.row.sampleTotal"
                    style="width:100%"
                    :controls="false"
                    :min="0"
                  />
                </template>
                <template #ac="{ scope }">
                  <el-input v-model="scope.row.ac" />
                </template>
                <template #re="{ scope }">
                  <el-input v-model="scope.row.re" />
                </template>
                <template #unqualifiedTotal="{ scope }">
                  <el-input-number
                    v-model="scope.row.unqualifiedTotal"
                    style="width:100%"
                    :controls="false"
                    :min="0"
                  />
                </template>
                <template #checkRecord="{ scope }">
                  <el-input v-model="scope.row.checkRecord" />
                </template>
              </BaseTable>
            </el-main>
          </el-collapse-item>
          <!-- 环保不合格描述 -->
          <el-collapse-item :title="$t('qualitySynergy.environmentalFailDesc')" name="3">
            <el-main style="min-height: 200px;">
              <p class="btn_line">
                <el-button
                  class="detail-pbtn"
                  :disabled="isReadOnly"
                  type="primary"
                  @click="addUqEnv"
                >
                  {{ $t("common.add") }}
                </el-button>
              </p>
              <BaseTable
                ref="uqEvColumns"
                stripe
                :data-source="quaEPNgDescList"
                :columns="uqEvColumns"
                columns-name="uqEvColumns"
                :empty-text="$t('components.noData')"
                :index="false"
                border
                :initialize="false"
                row-key="epNgDescLineId"
                @asyncGetRealDataSource="
                  data => asyncGetRealDataSource('quaEPNgDescList', data)
                "
              >
                <template #checkList="{ scope }">
                  <el-input v-model="scope.row.checkList" />
                </template>
                <template #testProjectName="{ scope }">
                  <el-input v-model="scope.row.testProjectName" />
                </template>
                <template #epNgAssay="{ scope }">
                  <el-input-number
                    v-model="scope.row.epNgAssay"
                    style="width:100%"
                    :controls="false"
                    :min="0"
                  />
                </template>
                <template #fixedNum="{ scope }">
                  <el-input-number
                    v-model="scope.row.fixedNum"
                    style="width:100%"
                    :controls="false"
                    :min="0"
                  />
                </template>
                <template #actualNum="{ scope }">
                  <el-input-number
                    v-model="scope.row.actualNum"
                    style="width:100%"
                    :controls="false"
                    :min="0"
                  />
                </template>
                <template #checkTool="{ scope }">
                  <el-input v-model="scope.row.checkTool" />
                </template>
                <template #checkRecord="{ scope }">
                  <el-input v-model="scope.row.checkRecord" />
                </template>
                <template #checkResult="{ scope }">
                  <el-input v-model="scope.row.checkResult" />
                </template>
                <template #ngDescribe="{ scope }">
                  <el-input v-model="scope.row.ngDescribe" />
                </template>
              </BaseTable>
            </el-main>
          </el-collapse-item>
          <!-- 材料问题状态 -->
          <el-form
            ref="problemStatus"
            :model="problemStatus"
            label-width="80px"
            label-position="top"
            class="form-fill-style"
            :disabled="isReadOnly"
            :rules="rules"
          >
            <!-- 材料问题状态 -->
            <el-collapse-item :title="$t('qualitySynergy.materialProblemStatus')" name="4">
              <srm-row>
                <srm-col>
                  <!-- 材料问题状态 -->
                  <el-form-item
                    :label="$t('qualitySynergy.materialProblemStatus')"
                    :label-width="formLabelWidth"
                    prop="itemStatus"
                  >
                    <dict-select
                      v-model="problemStatus.itemStatus"
                      code="INS_ITEM_PROBLEM_STATUS"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 材料异常等级判定 -->
                  <el-form-item
                    :label="$t('qualitySynergy.itemExceptionLevel')"
                    :label-width="formLabelWidth"
                    prop="itemExceptionLevel"
                  >
                    <dict-select
                      v-model="problemStatus.itemExceptionLevel"
                      code="INS_ITEM_ABNORMAL_LEVEL"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 材料异常责任备注 -->
                  <el-form-item
                    :label="$t('qualitySynergy.itemExceptionComment')"
                    :label-width="formLabelWidth"
                    prop="itemExceptionComment"
                  >
                    <el-input v-model="problemStatus.itemExceptionComment" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 材料异常责任方 -->
                  <el-form-item
                    :label="$t('qualitySynergy.itemExceptionName')"
                    :label-width="formLabelWidth"
                    prop="itemExceptionName"
                  >
                    <el-input v-model="problemStatus.itemExceptionName" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('qualitySynergy.ifCreate8DReport')"
                    :label-width="formLabelWidth"
                    prop="report8D"
                  >
                    <el-select v-model="problemStatus.report8D">
                      <el-option
                        v-for="item in YesOrNoOptions"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 是否创建绩效考核 -->
                  <el-form-item
                    :label="$t('qualitySynergy.isPerf')"
                    :label-width="formLabelWidth"
                    prop="isPerf"
                  >
                    <dict-select v-model="problemStatus.isPerf" code="INS_ITEM_ENABLE_PA" />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>
            <!-- 环保问题状态 -->
            <el-collapse-item :title="$t('qualitySynergy.epStatus')" name="5">
              <srm-row>
                <srm-col>
                  <!-- 环保问题状态 -->
                  <el-form-item
                    :label="$t('qualitySynergy.epStatus')"
                    :label-width="formLabelWidth"
                    prop="epStatus"
                  >
                    <dict-select v-model="problemStatus.epStatus" code="INS_ITEM_ENVI_P_S" />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>
          </el-form>
          <!-- 问题备注 -->
          <el-collapse-item :title="$t('qualitySynergy.questionNote')" name="6">
            <el-main style="min-height: 200px;">
              <p class="btn_line">
                <el-button
                  class="detail-pbtn"
                  type="primary"
                  :disabled="isReadOnly"
                  @click="addProblemComment"
                >
                  {{ $t("common.add") }}
                </el-button>
              </p>
              <BaseTable
                ref="problemCommentColums"
                stripe
                :data-source="quaProblemCommentsList"
                :columns="problemCommentColums"
                columns-name="problemCommentColums"
                :empty-text="$t('components.noData')"
                :index="false"
                border
                :initialize="false"
                row-key="problemCommentsLineId"
                @asyncGetRealDataSource="
                  data => asyncGetRealDataSource('quaProblemCommentsList', data)
                "
              >
                <template #judgeConclusion="{ scope }">
                  <el-input v-model="scope.row.judgeConclusion" />
                </template>
                <template #judgeName="{ scope }">
                  <el-input v-model="scope.row.judgeName" />
                </template>
                <template #judgeDate="{ scope }">
                  <el-date-picker
                    v-model="scope.row.judgeDate"
                    type="date"
                    value-format="yyyy-MM-dd"
                  />
                </template>
              </BaseTable>
            </el-main>
          </el-collapse-item>
          <!-- 返工信息 -->
          <el-form
            ref="lastForm"
            :model="lastForm"
            label-width="80px"
            label-position="top"
            class="form-fill-style"
            :disabled="isReadOnly"
            :rules="rules"
          >
            <!-- 返工信息 -->
            <el-collapse-item :title="$t('qualitySynergy.reworkInfo')" name="7">
              <srm-row>
                <srm-col>
                  <!-- 返工数量 -->
                  <el-form-item
                    :label="$t('qualitySynergy.reworkTotal')"
                    :label-width="formLabelWidth"
                    prop="reworkTotal"
                  >
                    <el-input-number
                      v-model="lastForm.reworkTotal"
                      style="width:100%"
                      :controls="false"
                      :min="0"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 合格数量 -->
                  <el-form-item
                    :label="$t('qualitySynergy.qualifiedTotal')"
                    :label-width="formLabelWidth"
                    prop="qualifiedTotal"
                  >
                    <el-input-number
                      v-model="lastForm.qualifiedTotal"
                      style="width:100%"
                      :controls="false"
                      :min="0"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 不合格数量 -->
                  <el-form-item
                    :label="$t('qualitySynergy.unqualifiedTotal')"
                    :label-width="formLabelWidth"
                    prop="unqualifiedTotal"
                  >
                    <el-input-number
                      v-model="lastForm.unqualifiedTotal"
                      style="width:100%"
                      :controls="false"
                      :min="0"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 返工方案 -->
                  <el-form-item
                    :label="$t('qualitySynergy.reworkPlan')"
                    :label-width="formLabelWidth"
                    prop="reworkPlan"
                  >
                    <el-input v-model="lastForm.reworkPlan" type="textarea" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 返工结论 -->
                  <el-form-item
                    :label="$t('qualitySynergy.reworkConclusion')"
                    :label-width="formLabelWidth"
                    prop="reworkConclusion"
                  >
                    <dict-select
                      v-model="lastForm.reworkConclusion"
                      code="INS_ITEM_REWORK_CONCLUSION"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 异常工时 -->
                  <el-form-item
                    :label="$t('qualitySynergy.exceptionWorkHour')"
                    :label-width="formLabelWidth"
                    prop="exceptionWorkHour"
                  >
                    <el-input-number
                      v-model="lastForm.exceptionWorkHour"
                      style="width:100%"
                      :controls="false"
                      :min="0"
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>
            <!-- 退货/入库判定 -->
            <el-collapse-item :title="$t('qualitySynergy.returnWarehouseJudge')" name="8">
              <!-- 材料异常时，“材料问题状态” 需要是 “已解决” 或 “结案” 或 “关闭” ，才能判定结果；环保异常时，“环保问题状态” 需要时 “已解决” ，才能判定结果 -->
              <el-alert
                :closable="false"
                :title="$t('qualitySynergy.msgMaterialException')"
                type="warning"
              />
              <srm-row>
                <srm-col>
                  <!-- 不合格处理结果 -->
                  <el-form-item
                    :label="$t('qualitySynergy.ngHandleResult')"
                    :label-width="formLabelWidth"
                    prop="ngHandleResult"
                  >
                    <dict-select v-model="lastForm.ngHandleResult" code="INS_ITEM_NTR" />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>
          </el-form>
        </el-collapse>
      </div>
      <CToolbar>
        <template slot="right">
          <el-button @click="cancelBill">
            {{ isReadOnly ? $t("common.close") : $t("common.cancel") }}
          </el-button>
          <el-button
            v-if="problemStatus.report8D === '是' && !isReadOnly"
            type="primary"
            @click="createReport8D"
          >
            {{ $t("qualitySynergy.create8DReport") }}
          </el-button>
          <el-button v-if="!isReadOnly" type="primary" @click="submit">
            {{ $t("common.staging") }}
          </el-button>
          <el-button v-if="!isReadOnly" type="primary" @click="publish">
            {{ $t('common.publish') }}
          </el-button>
        </template>
      </CToolbar>
      <VendorDeliveryDialog
        ref="dialog"
        :visible.sync="visible"
        @getSelectedRow="getSelectedRow"
      />
    </el-main>
  </el-container>
</template>
<script>
import CToolbar from 'lib@/components/c-toolbar'
import OrganizationSelector from 'lib@/components/organization-selector'
import { tabTodoMixin } from '@/utils/mixins'
import QuickSearch from 'lib@/components/QuickSearch'
import { parseTime } from '@/utils'
import CPagination from 'lib@/components/c-pagination'
import MImport from 'lib@/components/import'
import CCategorySelect from 'lib@/components/c-category-select'
import BaseTable from '@/library/components/BaseTable/baseTable.vue'
import VendorDeliveryDialog from './vendorDeliveryDialog'
import { excHandlingNotice } from 'modb@/qualitySynergy/api'
export default {
  name: 'IncomingExceptionDetail',
  components: {
    CToolbar,
    CCategorySelect,
    MImport,
    QuickSearch,
    CPagination,
    OrganizationSelector,
    BaseTable,
    VendorDeliveryDialog
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      visible: false,
      quaItemNgDescList: [],
      realQuaItemNgDescList: [],
      basic: {
        createdId: this.globalUserId,
        orgId: '',
        orgName: '',
        orgCode: null,
        organizationName: '',
        organizationCode: '',
        organizationId: '',
        itemExceptionHeadId: null,
        orderDate: parseTime(new Date(), '{y}-{m}-{d}')
      },
      unqualifiedContent: {
        unqualifiedContentId: null,
        deliveryNumber: null,
        materialCode: null,
        materialId: null,
        checkListId: null,
        materialName: null,
        itemTotal: undefined,
        checkListType: null,
        checkStandard: null,
        factoryName: null,
        vendorId: null,
        vendorCode: null,
        vendorName: null,
        purchaseAgent: null,
        itemAgent: null,
        epAgent: null,
        lotId: undefined,
        dateCode: undefined,
        freeTaxItem: null,
        orderDate: parseTime(new Date(), '{y}-{m}-{d}')
      },
      unqualifiedContentRules: {
        materialCode: [
          { required: true, message: this.$t('vendorMod.pleaseEnter'), trigger: 'change' }
        ], // '请输入
        materialName: [
          { required: true, message: this.$t('vendorMod.pleaseEnter'), trigger: 'blur' }
        ], // '请输入
        itemTotal: [{ required: true, message: this.$t('vendorMod.pleaseEnter'), trigger: 'blur' }],
        checkListId: [
          { required: true, message: this.$t('vendorMod.pleaseEnter'), trigger: 'blur' }
        ],
        checkListType: [
          { required: true, message: this.$t('vendorMod.pleaseEnter'), trigger: 'blur' }
        ],
        vendorName: [
          { required: true, message: this.$t('vendorMod.pleaseEnter'), trigger: 'blur' }
        ],
        checkStandard: [
          { required: true, message: this.$t('vendorMod.pleaseEnter'), trigger: 'blur' }
        ],
        factoryName: [
          { required: true, message: this.$t('vendorMod.pleaseEnter'), trigger: 'blur' }
        ],
        purchaseAgent: [
          { required: true, message: this.$t('common.pleaseSelect'), trigger: 'change' }
        ],
        lotId: [{ required: true, message: this.$t('vendorMod.pleaseEnter'), trigger: 'blur' },
          { pattern: /^[1-9][0-9]{0,49}$/, message: this.$t('qualitySynergy.fillNumber'), trigger: 'blur' }
        ],
        itemAgent: [
          { required: true, message: this.$t('vendorMod.pleaseEnter'), trigger: 'change' }
        ],
        epAgent: [{ required: true, message: this.$t('common.pleaseSelect'), trigger: 'change' }],
        dateCode: [{ required: true, message: this.$t('vendorMod.pleaseEnter'), trigger: 'blur' }],
        freeTaxItem: [
          { required: true, message: this.$t('vendorMod.pleaseEnter'), trigger: 'blur' }
        ]
      },
      problemStatus: {
        itemStatus: '',
        itemExceptionLevel: '',
        itemExceptionComment: '',
        itemExceptionName: '',
        report8D: '',
        epStatus: '',
        isPerf: ''
      },
      lastForm: {
        reworkTotal: undefined,
        qualifiedTotal: undefined,
        unqualifiedTotal: undefined,
        reworkPlan: '',
        reworkConclusion: '',
        exceptionWorkHour: undefined,
        ngHandleResult: ''
      },
      activeDims: ['1', '2', '3', '4', '5', '6', '7', '8'],
      rules: {
        orgId: [
          { required: true, message: this.$t('purchaseDemand.orgIdTips'), trigger: 'change' }
        ],
        organizationId: [{ required: true, message: this.$t('qualitySynergy.organizationRequired') }],
        itemStatus: [{ required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }],
        itemExceptionLevel: [
          { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        ],
        itemExceptionComment: [
          { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        ],
        itemExceptionName: [
          { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        ],
        report8D: [{ required: true, message: this.$t('common.pleaseSelect'), trigger: 'change' }],
        epStatus: [{ required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }],
        reworkTotal: [{ required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }],
        qualifiedTotal: [
          { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        ],
        unqualifiedTotal: [
          { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        ],
        ngHandleResult: [
          { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        ],
        exceptionWorkHour: [
          { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        ],
        reworkPlan: [{ required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }],
        reworkConclusion: [
          { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        ]
      },
      YesOrNoOptions: [
        { value: '是', label: this.$t('common.yes') },
        { value: '否', label: this.$t('common.no') }
      ],
      isReadOnly: this.$attrs.params.flag == 'readOnly',
      formLabelWidth: '120px',
      globalUserId: null,
      Viewflag: '',
      uqMaterialColumns: [
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: this.$t('qualitySynergy.checkList'), // 检规项
            prop: 'checkList'
          },
          slot: 'checkList',
          rules: { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: this.$t('qualitySynergy.checkInfor'), // 检规内容
            prop: 'checkInfor'
          },
          slot: 'checkInfor',
          rules: { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '130',
            label: this.$t('qualitySynergy.itemSpecificCheckList'), // 物料专有检规项
            prop: 'itemSpecificCheckList'
          },
          slot: 'itemSpecificCheckList',
          rules: { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        },
        {
          attrs: {
            align: 'center',
            width: '120',
            label: this.$t('qualitySynergy.aqlStandardNum'), // AQL标准值
            prop: 'aqlStandardNum'
          },
          slot: 'aqlStandardNum',
          rules: { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        },
        {
          attrs: {
            align: 'center',
            width: '120',
            label: this.$t('qualitySynergy.freeCheck'), // 是否免检
            prop: 'freeCheck'
          },
          slot: 'freeCheck',
          rules: { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        },
        {
          attrs: {
            align: 'center',
            width: '120',
            label: this.$t('qualitySynergy.fixedCheckTotal'), // 固定抽检数量
            prop: 'fixedCheckTotal'
          },
          slot: 'fixedCheckTotal',
          rules: { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: this.$t('qualitySynergy.checkTool'), // 检验工具
            prop: 'checkTool'
          },
          slot: 'checkTool',
          rules: { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: this.$t('qualitySynergy.sampleTotal'), // 抽样数
            prop: 'sampleTotal'
          },
          slot: 'sampleTotal',
          rules: { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '80',
            label: 'AC',
            prop: 'ac'
          },
          slot: 'ac',
          rules: { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '80',
            label: 'RE',
            prop: 're'
          },
          slot: 're',
          rules: { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '80',
            label: this.$t('qualitySynergy.adverseNumber'), // 不良数
            prop: 'unqualifiedTotal'
          },
          slot: 'unqualifiedTotal',
          rules: { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: this.$t('qualitySynergy.checkRecord'), // 检验记录
            prop: 'checkRecord'
          },
          slot: 'checkRecord',
          rules: { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        },
        {
          attrs: {
            align: 'center',
            width: '100',
            label: this.$t('orderMod.buyerOrderSynergy.creationDate'), // 创建日期
            prop: 'creationDate'
          },
          slot: 'creationDate'
        },
        {
          attrs: {
            align: 'center',
            width: '100',
            label: this.$t('common.creator'), // 创建人
            prop: 'createdUserName'
          },
          slot: 'createdUserName'
        },
        {
          attrs: {
            align: 'center',
            width: '80',
            label: this.$t('priceModel.costElement.lastUpdateDate'), // 更新日期
            prop: 'lastUpdateDate'
          },
          slot: 'lastUpdateDate'
        },
        {
          attrs: {
            align: 'center',
            width: '80',
            label: this.$t('common.updatePeople'), // 更新人
            prop: 'lastUpdatedUserName'
          },
          slot: 'lastUpdatedUserName'
        },
        {
          attrs: {
            align: 'center',
            label: this.$t('common.operation'), // 操作
            fixed: 'right',
            prop: 'operation',
            width: 80
          },
          operations: [
            {
              event: 'deleteItem',
              name: this.$t('common.delete'),
              func: this.deleteItem,
              show: () => !this.isReadOnly
            }
          ]
        }
      ],
      quaEPNgDescList: [],
      realQuaEPNgDescList: [],
      uqEvColumns: [
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: this.$t('qualitySynergy.checkList'), // 检规项
            prop: 'checkList'
          },
          slot: 'checkList',
          rules: { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: this.$t('qualitySynergy.testProjectName'), // 测试项目
            prop: 'testProjectName'
          },
          slot: 'testProjectName',
          rules: { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '80',
            label: this.$t('qualitySynergy.epNgAssay'), // 含量
            prop: 'epNgAssay'
          },
          slot: 'epNgAssay',
          rules: { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        },
        {
          attrs: {
            align: 'center',
            width: '100',
            label: this.$t('qualitySynergy.fixedValue'), // 固定值
            prop: 'fixedNum'
          },
          slot: 'fixedNum',
          rules: { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        },
        {
          attrs: {
            align: 'center',
            width: '100',
            label: this.$t('qualitySynergy.actualValue'), // 实际值
            prop: 'actualNum'
          },
          slot: 'actualNum',
          rules: { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        },
        {
          attrs: {
            align: 'center',
            width: '100',
            label: this.$t('qualitySynergy.checkTool'), // 检验工具
            prop: 'checkTool'
          },
          slot: 'checkTool',
          rules: { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: this.$t('qualitySynergy.testRecord'), // 测试记录
            prop: 'checkRecord'
          },
          slot: 'checkRecord',
          rules: { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        },
        {
          attrs: {
            align: 'center',
            width: '100',
            label: this.$t('qualitySynergy.checkResult'), // 检验结果
            prop: 'checkResult'
          },
          slot: 'checkResult',
          rules: { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '120',
            label: this.$t('qualitySynergy.ngDescribe'), // 不良描述
            prop: 'ngDescribe'
          },
          slot: 'ngDescribe',
          rules: { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        },
        {
          attrs: {
            align: 'center',
            width: '100',
            label: this.$t('qualitySynergy.creationDate'), // 创建日期
            prop: 'creationDate'
          },
          slot: 'creationDate'
        },
        {
          attrs: {
            align: 'center',
            width: '100',
            label: this.$t('qualitySynergy.creator'), // 创建人
            prop: 'createdUserName'
          },
          slot: 'createdUserName'
        },
        {
          attrs: {
            align: 'center',
            width: '80',
            label: this.$t('qualitySynergy.updateDate'), // 更新日期
            prop: 'lastUpdateDate'
          },
          slot: 'lastUpdateDate'
        },
        {
          attrs: {
            align: 'center',
            width: '100',
            label: this.$t('qualitySynergy.updateBy'), // 更新人
            prop: 'lastUpdatedUserName'
          },
          slot: 'lastUpdatedUserName'
        },
        {
          attrs: {
            align: 'center',
            label: this.$t('common.operation'),
            fixed: 'right',
            prop: 'operation',
            width: 80
          },
          operations: [
            {
              event: 'deleteItem',
              name: this.$t('common.delete'),
              func: this.deleteItem,
              show: () => !this.isReadOnly
            }
          ]
        }
      ],
      quaProblemCommentsList: [],
      realQuaProblemCommentsList: [],
      problemCommentColums: [
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: this.$t('qualitySynergy.judgeConclusion'), // 判定结论
            prop: 'judgeConclusion'
          },
          slot: 'judgeConclusion',
          rules: { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: this.$t('qualitySynergy.judgeName'), // 判定人
            prop: 'judgeName'
          },
          slot: 'judgeName',
          rules: { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: this.$t('qualitySynergy.judgeDate'), // 判定日期
            // type: "date",
            prop: 'judgeDate'
          },
          slot: 'judgeDate',
          rules: { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: this.$t('qualitySynergy.creationDate'), // 创建日期
            prop: 'creationDate'
          },
          slot: 'creationDate'
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: this.$t('qualitySynergy.creator'), // 创建人
            prop: 'createdUserName'
          },
          slot: 'createdUserName'
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: this.$t('qualitySynergy.updateDate'), // 更新日期
            prop: 'lastUpdatedDate'
          },
          slot: 'lastUpdatedDate'
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: this.$t('qualitySynergy.updateBy'), // 更新人
            prop: 'lastUpdatedUserName'
          },
          slot: 'lastUpdatedUserName'
        },
        {
          attrs: {
            align: 'center',
            label: this.$t('common.operation'),
            fixed: 'right',
            prop: 'operation',
            width: 80
          },
          operations: [
            {
              event: 'deleteItem',
              name: this.$t('common.delete'),
              func: this.deleteItem,
              show: () => !this.isReadOnly
            }
          ]
        }
      ]
    }
  },
  created () {
    this.globalUserId = this.$store.getters.userInfo.userId
    this.Viewflag = this.$attrs.params.flag
    if (this.$attrs.params.flag !== 'add') {
      this.getFormDetail(this.$attrs.params.row.itemExceptionHeadId)
    }
  },
  methods: {
    asyncGetRealDataSource (code, data) {
      if (code === 'quaItemNgDescList') {
        this.realQuaItemNgDescList = data
      }
      if (code === 'quaEPNgDescList') {
        this.realQuaEPNgDescList = data
      }
      if (code === 'quaProblemCommentsList') {
        this.realQuaProblemCommentsList = data
      }
      console.log(code, data)
    },
    // 确认选择物料
    getItemObj (val, data) {
      this.unqualifiedContent.materialId = val ? val.materialId : null
      this.unqualifiedContent.materialCode = val ? val.materialCode : ''
      this.unqualifiedContent.materialName = val ? val.materialName : ''
    },
    // 选择供应商
    getCompanyObj (val, data) {
      this.unqualifiedContent.vendorId = val ? val.companyId : ''
      this.unqualifiedContent.vendorCode = val ? val.companyCode : ''
      this.unqualifiedContent.vendorName = val ? val.companyName : ''
    },
    getDeliveryNumber () {
      if (this.isReadOnly) return
      if (this.unqualifiedContent.vendorName && this.basic.orgId) {
        this.visible = true
        this.$refs.dialog.init(this.basic.orgId, this.unqualifiedContent.vendorName)
      } else {
        this.$message.info(this.$t('qualitySynergy.selectOUandVendor'))
      }
    },
    getSelectedRow (row) {
      this.unqualifiedContent.deliveryNumber = row.deliveryNumber
      this.unqualifiedContent.materialId = row.materialId
      this.unqualifiedContent.materialCode = row.materialCode
      this.unqualifiedContent.materialName = row.materialName
      this.unqualifiedContent.itemTotal = row.deliveryQuantity
    },
    getUserObj1 (val, scope) {
      scope.purchaseAgent = val ? val.nickname : ''
    },
    getUserObj2 (val, scope) {
      scope.itemAgent = val ? val.nickname : ''
    },
    getUserObj3 (val, scope) {
      scope.epAgent = val ? val.nickname : ''
    },
    addUqMaterial () {
      this.$refs.uqMaterialColumns.add({})
    },
    addUqEnv () {
      this.$refs.uqEvColumns.add({})
    },
    addProblemComment () {
      this.$refs.problemCommentColums.add({})
    },
    deleteItem (scope, data) {
      data.splice(scope.$index, 1)
    },
    cancelBill () {
      const { flag } = this.$attrs.params
      if (flag == 'add') {
        this.$emit('tab-remove', 'incomingExceptionDetail')
      } else {
        this.$emit('tab-remove', this.$attrs.params.tabName)
      }
      this.__setTabTodo('IncomingExceptionList.getQuerydata')
    },
    selectHandler (node, value, scope) {
      this.basic.orgId = node ? node.organizationId : null
      this.basic.orgCode = node ? node.organizationCode : null
      this.basic.orgName = node ? node.organizationName : null
    },
    // 库存组件
    selectHandler2 (node, value, scope) {
      this.basic.organizationId = node ? node.organizationId : null
      this.basic.organizationCode = node ? node.organizationCode : null
      this.basic.organizationName = node ? node.organizationName : null
    },
    submit () {
      let _this = this
      const { flag } = this.$attrs.params
      let data = {
        ..._this.basic,
        ..._this.unqualifiedContent,
        ..._this.problemStatus,
        ..._this.lastForm,
        quaEPNgDescList: _this.realQuaEPNgDescList,
        quaItemNgDescList: _this.realQuaItemNgDescList,
        quaProblemCommentsList: _this.realQuaProblemCommentsList
      }
      // 新增时不用提交主键值
      const { itemExceptionHeadId, ...rest } = data
      if (flag === 'add') {
        excHandlingNotice.incomingExceptionAdd(rest).then(res => {
          this.$message({
            message: this.$t('common.success'),
            type: 'success'
          })
          this.cancelBill()
        })
      } else if (flag === 'edit') {
        excHandlingNotice.incomingExceptionModify(data).then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.cancelBill()
        })
      }
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
    publish () {
      let list = []
      list.push(
        this.checkForm('basic'),
        this.checkForm('unqualifiedContent'),
        this.checkForm('problemStatus'),
        this.checkForm('lastForm'),
      )
      Promise.all(list)
        .then(() => {
          let data = {
            ...this.basic,
            ...this.unqualifiedContent,
            ...this.problemStatus,
            ...this.lastForm,
            quaEPNgDescList: this.realQuaEPNgDescList,
            quaItemNgDescList: this.realQuaItemNgDescList,
            quaProblemCommentsList: this.realQuaProblemCommentsList
          }
          excHandlingNotice.incomingExceptionPublish(data).then(res => {
            this.$message({
              message: this.$t('common.success'),
              type: 'success'
            })
            this.cancelBill()
          })
        })
        .catch(() => {
          this.__focus_error__(this.$t('contractMod.msgContractManage[14]'))
        })
    },
    getFormDetail (itemExceptionHeadId) {
      excHandlingNotice.incomingExceptionDetail({ itemExceptionHeadId }).then(res => {
        if (res.data) {
          /* 为了能让el-input-number的值为null时置空 */
          res.data.itemTotal ?? (res.data.itemTotal = undefined)
          res.data.exceptionWorkHour ?? (res.data.exceptionWorkHour = undefined)
          res.data.qualifiedTotal ?? (res.data.qualifiedTotal = undefined)
          res.data.reworkTotal ?? (res.data.reworkTotal = undefined)
          res.data.unqualifiedTotal ?? (res.data.unqualifiedTotal = undefined)
          this.quaItemNgDescList = res.data.quaItemNgDescList
          this.quaEPNgDescList = res.data.quaEPNgDescList
          this.quaProblemCommentsList = res.data.quaProblemCommentsList
          this.basic = res.data
          this.unqualifiedContent = res.data
          this.problemStatus = res.data
          this.lastForm = res.data
        }
      })
    },
    createReport8D () {
      if (!this.basic.itemExceptionHeadId) {
        this.$message.warning(
          this.$t('qualitySynergy.pleaseHoldItTemporarilyBefore8DReportCanBeCreated'),
        )
        return
      }
      let row = {
        ...this.basic,
        ...this.unqualifiedContent,
        ...this.problemStatus,
        ...this.lastForm,
        quaEPNgDescList: this.realQuaEPNgDescList,
        quaItemNgDescList: this.realQuaItemNgDescList,
        quaProblemCommentsList: this.realQuaProblemCommentsList
      }
      this.$router.push({
        name: 'report8D',
        params: {
          from: 'incomingException',
          funName: 'report8D',
          fdSubject: row
        }
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.basic {
  padding-left: 11px;
}
.btn_line {
  margin: 0 0 10px 0;
}
:deep(.table-wrapper) {
  padding-left: 0px;
}
</style>
