<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <div class="form-container2">
        <el-form
          ref="form"
          :model="form"
          label-width="80px"
          label-position="top"
          class="form-fill-style"
          :disabled="readOnly"
          :rules="rules"
        >
          <el-collapse v-model="activeDims" class="tab-form-style">
            <!-- 不良基本信息 -->
            <el-collapse-item :title="$t('qualitySynergy.badBasicInfo')" name="1">
              <srm-row>
                <srm-col>
                  <!-- 单据编码 -->
                  <el-form-item prop="billCode" :label="$t('dataConfMod.sequenceCode')">
                    <el-input v-model="form.billCode" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 问题状态 -->
                  <el-form-item prop="problemStatus" :label="$t('qualitySynergy.problemStatus')">
                    <el-input v-model="form.problemStatus" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 处理结果 -->
                  <el-form-item prop="handleResult" :label="$t('qualitySynergy.handleResult')">
                    <el-input v-model="form.handleResult" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 业务实体 -->
                  <el-form-item prop="orgId" :label="$t('dataConfMod.orgId')">
                    <template>
                      <OrganizationSelector
                        ref="organizationSelector"
                        v-model="form.orgId"
                        :parent-id="-1"
                        :disabled="readOnly"
                        node-type="OU"
                        :placeholder="$t('common.pleaseSelect')"
                        @select="selectHandler"
                      />
                    </template>
                  </el-form-item>
                </srm-col>
                <!-- 库存组织 -->
                <srm-col>
                  <el-form-item
                    :label="$t('qualitySynergy.organizationName')"
                    prop="organizationId"
                  >
                    <el-input v-if="readOnly" v-model="form.organizationName" disabled />
                    <OrganizationSelector
                      v-else
                      ref="organizationSelector2"
                      v-model="form.organizationId"
                      :parent-id="form.orgId"
                      :disabled="readOnly"
                      node-type="INV"
                      :placeholder="$t('common.pleaseSelect')"
                      @select="selectHandler2"
                    />
                  </el-form-item>
                </srm-col>

                <srm-col>
                  <!-- 供应商名称 -->
                  <el-form-item prop="vendorName" :label="$t('common.vendorName')">
                    <QuickSearch
                      :show-input="form.vendorName"
                      show-key="companyName"
                      :disabled="readOnly"
                      :scope-data="form"
                      name="scc_sup_company_info_display"
                      @close-quicksearch="getCompanyObj"
                    />
                  </el-form-item>
                </srm-col>

                <!-- 送货单号 -->
                <srm-col>
                  <el-form-item :label="$t('qualitySynergy.deliveryNumber')">
                    <el-input
                      v-model="form.deliveryNumber"
                      clearable
                      :disabled="readOnly || !form.vendorName || !form.orgId"
                      suffix-icon="el-icon-search"
                      @click.native="getDeliveryNumber"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 分属事业部 -->
                  <el-form-item prop="buName" :label="$t('qualitySynergy.buName')">
                    <el-input v-model="form.buName" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 负责人 -->
                  <el-form-item prop="processAgent" :label="$t('dataConfMod.principal')">
                    <QuickSearch
                      :show-input="form.processAgent"
                      show-key="nickname"
                      :disabled="readOnly"
                      :scope-data="form"
                      name="scc_rbac_user_display"
                      @close-quicksearch="getProcessAgent"
                    />
                  </el-form-item>
                </srm-col>

                <!-- 批号 -->
                <srm-col>
                  <el-form-item prop="batchCode" :label="$t('qualitySynergy.batchNum')">
                    <el-input-number
                      v-model="form.batchCode"
                      style="width:100%"
                      :controls="false"
                      :min="0"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 订单数量 -->
                  <el-form-item
                    prop="orderTotal"
                    :label="$t('orderMod.buyerOrderSynergy.orderNum')"
                  >
                    <el-input-number
                      v-model="form.orderTotal"
                      style="width:100%"
                      :controls="false"
                      :min="0"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 工单 -->
                  <el-form-item prop="orderCode" :label="$t('qualitySynergy.workOrder')">
                    <el-input-number
                      v-model="form.orderCode"
                      style="width:100%"
                      :controls="false"
                      :min="0"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 产品代码 -->
                  <el-form-item prop="productionCode" :label="$t('qualitySynergy.productionCode')">
                    <el-input-number
                      v-model="form.productionCode"
                      style="width:100%"
                      :controls="false"
                      :min="0"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 产品描述 -->
                  <el-form-item prop="productionDesc" :label="$t('qualitySynergy.productionDesc')">
                    <el-input v-model="form.productionDesc" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 供应商已回复 -->
                  <el-form-item
                    prop="supplierResponsed"
                    :label="$t('qualitySynergy.supplierResponsed')"
                  >
                    <el-input v-model="form.supplierResponsed" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 投入数量 -->
                  <el-form-item
                    prop="investmentTotal"
                    :label="$t('qualitySynergy.investmentTotal')"
                  >
                    <el-input-number
                      v-model="form.investmentTotal"
                      style="width:100%"
                      :controls="false"
                      :min="0"
                      @change="computeUnqualifiedRate"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 不良数量 -->
                  <el-form-item
                    prop="unqualifiedTotal"
                    :label="$t('qualitySynergy.unqualifiedTotal1')"
                  >
                    <el-input-number
                      v-model="form.unqualifiedTotal"
                      style="width:100%"
                      :controls="false"
                      :min="0"
                      @change="computeUnqualifiedRate"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 不良率% -->
                  <el-form-item
                    prop="unqualifiedRate"
                    :label="$t('qualitySynergy.unqualifiedRate')"
                  >
                    <el-input-number
                      v-model="form.unqualifiedRate"
                      disabled
                      style="width:100%"
                      :controls="false"
                      :min="0"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 外协工厂 -->
                  <el-form-item
                    prop="outsourcingFactory"
                    :label="$t('qualitySynergy.outsourcingFactory')"
                  >
                    <el-input v-model="form.outsourcingFactory" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 工站 -->
                  <el-form-item prop="workStation" :label="$t('qualitySynergy.workStation')">
                    <el-input v-model="form.workStation" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 已包装数量 -->
                  <el-form-item prop="packedTotal" :label="$t('qualitySynergy.packedTotal')">
                    <el-input-number
                      v-model="form.packedTotal"
                      style="width:100%"
                      :controls="false"
                      :min="0"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 线体 -->
                  <el-form-item prop="linearBody" :label="$t('qualitySynergy.linearBody')">
                    <el-input v-model="form.linearBody" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 位号 -->
                  <el-form-item prop="tagCode" :label="$t('qualitySynergy.tagNum')">
                    <el-input-number
                      v-model="form.tagCode"
                      style="width:100%"
                      :controls="false"
                      :min="0"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item prop="dateCode" label="DATE CODE">
                    <el-input-number
                      v-model="form.dateCode"
                      style="width:100%"
                      :controls="false"
                      :min="0"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 3个月内发生次数 -->
                  <el-form-item
                    prop="threeMonthsHappens"
                    :label="$t('qualitySynergy.threeMonthsHappens')"
                  >
                    <el-input-number
                      v-model="form.threeMonthsHappens"
                      style="width:100%"
                      :controls="false"
                      :min="0"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 物料编码 -->
                  <el-form-item prop="materialCode" :label="$t('common.materialCode')">
                    <el-input v-if="readOnly" v-model="form.materialCode" />
                    <QuickSearch
                      v-else
                      :show-input="form.materialCode"
                      :pre-query-data="queryParame"
                      show-key="materialCode"
                      :scope-data="form"
                      name="scc_base_material_item_display"
                      @close-quicksearch="getItemObj"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 物料状态 -->
                  <el-form-item prop="materialStatus" :label="$t('qualitySynergy.materialStatus')">
                    <el-input v-model="form.materialStatus" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 物料名称 -->
                  <el-form-item prop="materialName" :label="$t('common.materialName')">
                    <el-input v-model="form.materialName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 备注 -->
                  <el-form-item :label="$t('common.remark')">
                    <el-input v-model="form.processComments" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 工厂临时措施 -->
                  <el-form-item
                    prop="factoryTemporaryMeasures"
                    :label="$t('qualitySynergy.factoryTemporaryMeasures')"
                  >
                    <el-input v-model="form.factoryTemporaryMeasures" type="textarea" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 创建人 -->
                  <el-form-item prop="createdUserName" :label="$t('qualitySynergy.creator')">
                    <el-input v-model="form.createdUserName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 创建时间 -->
                  <el-form-item prop="creationDate" :label="$t('common.creationTime')">
                    <el-date-picker
                      v-model="form.creationDate"
                      disabled
                      type="date"
                      :format="$formatDatePicker"
                      value-format="yyyy-MM-dd"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 更新人 -->
                  <el-form-item prop="lastUpdatedUserName" :label="$t('common.updatePeople')">
                    <el-input v-model="form.lastUpdatedUserName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 最后更新时间 -->
                  <el-form-item prop="lastUpdatedDate" :label="$t('contractMod.lastUpdateDate')">
                    <el-date-picker
                      v-model="form.lastUpdatedDate"
                      disabled
                      type="date"
                      :format="$formatDatePicker"
                      value-format="yyyy-MM-dd"
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>
            <!-- SQA材料问题处理 -->
            <el-collapse-item :title="$t('qualitySynergy.sqlProblemHandle')" name="2">
              <srm-row>
                <srm-col>
                  <!-- 材料异常等级判定 -->
                  <el-form-item
                    prop="itemExceptionLevel"
                    :label="$t('qualitySynergy.itemExceptionLevel')"
                  >
                    <el-input v-model="form.itemExceptionLevel" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 是否创建8D报告 -->
                  <el-form-item prop="report8D" :label="$t('qualitySynergy.ifCreate8DReport')">
                    <dict-select v-model="form.report8D" code="INS_PROCESS_REPORT_8D" />
                  </el-form-item>
                </srm-col>

                <srm-col>
                  <!-- 是否创建绩效考核 -->
                  <el-form-item
                    :label="$t('qualitySynergy.isPerf')"
                    prop="isPerf"
                  >
                    <dict-select v-model="form.isPerf" code="INS_ITEM_ENABLE_PA" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 材料异常责任方 -->
                  <el-form-item
                    prop="itemExceptionName"
                    :label="$t('qualitySynergy.itemExceptionName')"
                  >
                    <el-input v-model="form.itemExceptionName" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 材料异常责任备注 -->
                  <el-form-item
                    prop="itemExceptionComment"
                    :label="$t('qualitySynergy.itemExceptionComment')"
                  >
                    <el-input v-model="form.itemExceptionComment" type="textarea" />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>
            <!-- 不良现象描述 -->
            <el-collapse-item :title="$t('qualitySynergy.badPhenomenonDesc')" name="3">
              <srm-row>
                <srm-col>
                  <!-- 不良描述 -->
                  <el-form-item prop="unqualifiedDesc" :label="$t('qualitySynergy.ngDescribe')">
                    <el-input v-model="form.unqualifiedDesc" type="textarea" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 不良分类 -->
                  <el-form-item
                    prop="unqualifiedType"
                    :label="$t('qualitySynergy.unqualifiedType')"
                  >
                    <el-input v-model="form.unqualifiedType" />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>
            <!-- 工程分析方法及结果 -->
            <el-collapse-item :title="$t('qualitySynergy.engineerAnalysis')" name="4">
              <srm-row>
                <srm-col>
                  <!-- 复现方法/不良现象复现率 -->
                  <el-form-item
                    prop="processReproduction"
                    :label="$t('qualitySynergy.processReproduction')"
                  >
                    <el-input v-model="form.processReproduction" type="textarea" />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>
            <el-collapse-item ref="problemTypeInfo" :title="$t('qualitySynergy.problemTypeInfo')" name="5">
              <MainHeader v-if="!readOnly" :l-span="22" :r-span="2">
                <template slot="left">
                  <!-- 新增 -->
                  <el-button type="primary" @click="addItem">
                    {{ $t('common.add') }}
                  </el-button>
                </template>
              </MainHeader>

              <BaseTable
                stripe
                :data="form.quaProcessExceptionHandleList"
                :empty-text="$t('components.noData')"
                :columns="columns"
                border
                @deleteItem="deleteItem"
              >
                <!-- 问题类别 -->
                <template #problemCategory="scope">
                  <dict-select
                    v-model="scope.row.problemCategory"
                    code="INS_PROCESS_PROBLEM_CATEGORY"
                    :disabled="readOnly"
                  />
                </template>
                <!-- 不良描述 -->
                <template #unqualifiedDesc="scope">
                  <el-input
                    v-model="scope.row.unqualifiedDesc"
                    type="textarea"
                    :disabled="readOnly"
                  />
                </template>
                <!-- 数量 -->
                <template #quantity="scope">
                  <el-input-number
                    v-model="scope.row.quantity"
                    style="width:100%"
                    :controls="false"
                    :min="0"
                    :disabled="readOnly"
                  />
                </template>
                <!-- 措施 -->
                <template #measures="scope">
                  <dict-select
                    v-model="scope.row.measures"
                    code="INS_PROCESS_MEASURES"
                    :disabled="readOnly"
                  />
                </template>
              </BaseTable>
            </el-collapse-item>

            <!-- 库存处理 -->
            <el-collapse-item :title="$t('qualitySynergy.inventoryHandling')" name="6">
              <srm-row>
                <!-- 未生产品数量及处理 -->
                <srm-col>
                  <el-form-item
                    prop="unproductiveItemHandle"
                    :label="$t('qualitySynergy.unproductiveItemHandle')"
                  >
                    <el-input v-model="form.unproductiveItemHandle" type="textarea" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 涉及半成品数量及处理 -->
                  <el-form-item prop="semiItemHandle" :label="$t('qualitySynergy.semiItemHandle')">
                    <el-input v-model="form.semiItemHandle" type="textarea" />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>
            <!-- 涉及成品数量及处理 -->
            <el-collapse-item :title="$t('qualitySynergy.invovleQuantityAndProcess')" name="7">
              <srm-row>
                <srm-col>
                  <!-- 库存 -->
                  <el-form-item
                    prop="organizationTotal"
                    :label="$t('qualitySynergy.organizationTotal')"
                  >
                    <el-input v-model="form.organizationTotal" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 涉及成品处理 -->
                  <el-form-item
                    prop="finishedProductHandle"
                    :label="$t('qualitySynergy.finishedProductHandle')"
                  >
                    <el-input v-model="form.finishedProductHandle" type="textarea" />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </div>

      <CToolbar>
        <template #right>
          <el-button @click="cancelBill">
            {{
              readOnly ? $t("common.close") : $t("common.cancel")
            }}
          </el-button>
          <el-button
            v-if="form.report8D === 'Y' && !readOnly"
            @click="createReport8D()"
          >
            {{ $t("qualitySynergy.create8DReport") }}
          </el-button>
          <el-button v-if="!readOnly" @click="save">
            {{ $t("common.staging") }}
          </el-button>
          <el-button v-if="!readOnly" type="primary" @click="publish">
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
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import VendorDeliveryDialog from '../incomingException/vendorDeliveryDialog'
import BaseTable from 'lib@/components/BaseTable'
import { inspectionStandard } from 'modb@/qualitySynergy/api'
export default {
  name: 'ProcessExceptionDetail',
  components: {
    MainHeader,
    CToolbar,
    OrganizationSelector,
    QuickSearch,
    BaseTable,
    VendorDeliveryDialog
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      queryParame: {},
      visible: false,
      activeDims: ['1', '2', '3', '4', '5', '6', '7'],
      form: {
        billCode: null,
        problemStatus: null,
        handleResult: null,
        orgId: null,
        orgCode: null,
        orgName: null,
        organizationName: '',
        organizationCode: '',
        organizationId: '',
        buName: null,
        processAgent: null,
        batchCode: undefined,
        orderTotal: undefined,
        orderCode: undefined,
        productionCode: undefined,
        productionDesc: null,
        supplierResponsed: null,
        investmentTotal: undefined,
        unqualifiedTotal: undefined,
        unqualifiedRate: undefined,
        outsourcingFactory: null,
        workStation: null,
        packedTotal: undefined,
        vendorId: null,
        vendorCode: null,
        vendorName: null,
        deliveryNumber: null,
        linearBody: null,
        tagCode: undefined,
        dateCode: undefined,
        threeMonthsHappens: undefined,
        materialCode: null,
        materialStatus: null,
        materialName: null,
        processComments: null,
        factoryTemporaryMeasures: null,
        itemExceptionLevel: null,
        report8D: null,
        isPerf: null,
        itemExceptionName: null,
        itemExceptionComment: null,
        unqualifiedDesc: null,
        unqualifiedType: null,
        processReproduction: null,
        quaProcessExceptionHandleList: [],
        unproductiveItemHandle: null,
        semiItemHandle: null,
        organizationTotal: null,
        finishedProductHandle: null,
        createdId: null,
        createdBy: null,
        creationDate: null,
        lastUpdatedId: null,
        lastUpdatedBy: null,
        lastUpdatedUserName: '',
        lastUpdatedDate: null
      },
      columns: [
        // 问题类别
        {
          attrs: {
            label: () => this.$t('qualitySynergy.problemType'),
            prop: 'problemCategory',
            minWidth: 100,
            renderHeader: this._addStarToColumn
          },
          slot: 'problemCategory'
        },
        // 不良描述
        {
          attrs: {
            label: () => this.$t('qualitySynergy.ngDescribe'),
            prop: 'unqualifiedDesc',
            minWidth: 100,
            renderHeader: this._addStarToColumn
          },
          slot: 'unqualifiedDesc'
        },
        // 数量
        {
          attrs: {
            label: () => this.$t('qualitySynergy.quantity'),
            prop: 'quantity',
            minWidth: 100,
            renderHeader: this._addStarToColumn
          },
          slot: 'quantity'
        },
        // 措施
        {
          attrs: {
            label: () => this.$t('qualitySynergy.measures'),
            prop: 'measures',
            minWidth: 100,
            renderHeader: this._addStarToColumn
          },
          slot: 'measures'
        },
        {
          attrs: {
            align: 'center',
            label: t => t.$t('common.operation'),
            fixed: 'right',
            width: 80
          },
          operations: [
            {
              event: 'deleteItem',
              name: this.$t('common.delete'),
              attrs: { type: 'text' },
              show: () => !this.readOnly
            }
          ]
        }
      ],
      rules: {
        problemStatus: [
          { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        ],
        handleResult: [{ required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }],
        orgId: [
          { required: true, message: this.$t('purchaseDemand.orgIdTips'), trigger: 'change' }
        ],
        organizationId: [{ required: true, message: this.$t('qualitySynergy.organizationRequired') }],
        buName: [{ required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }],
        processAgent: [
          {
            required: true,
            message: this.$t('purchaseDemand.personInChargeNicknameTips'),
            trigger: 'change'
          }
        ],
        batchCode: [{ required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }],
        orderTotal: [{ required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }],
        orderCode: [{ required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }],
        productionCode: [
          { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        ],
        productionDesc: [
          { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        ],
        supplierResponsed: [
          { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        ],
        investmentTotal: [
          { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        ],
        unqualifiedTotal: [
          { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        ],
        unqualifiedRate: [
          { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        ],
        // outsourcingFactory: [
        //   { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        // ],
        workStation: [{ required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }],
        packedTotal: [{ required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }],
        vendorName: [
          { required: true, message: this.$t('componentDoc.msgSelVendor'), trigger: 'change' }
        ],
        linearBody: [{ required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }],
        tagCode: [{ required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }],
        dateCode: [{ required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }],
        threeMonthsHappens: [
          { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        ],
        materialCode: [
          { required: true, message: this.$t('common.pleaseSelect'), trigger: 'change' }
        ],
        materialStatus: [
          { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        ],
        factoryTemporaryMeasures: [
          { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        ],
        itemExceptionLevel: [
          { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        ],
        report8D: [{ required: true, message: this.$t('common.pleaseSelect'), trigger: 'change' }],
        itemExceptionName: [
          { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        ],
        itemExceptionComment: [
          { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        ],
        unqualifiedDesc: [
          { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        ],
        unqualifiedType: [
          { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        ],
        processReproduction: [
          { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        ],
        unproductiveItemHandle: [
          { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        ],
        semiItemHandle: [
          { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        ],
        organizationTotal: [
          { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        ],
        finishedProductHandle: [
          { required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }
        ]
      },
      viewflag: '',
      readOnly: this.$attrs.params.flag == 'readOnly',
      globalUserId: null
    }
  },
  created () {
    this.globalUserId = this.$store.getters.userInfo.userId
    if (this.$attrs.params.flag !== 'add') {
      this.getFormDetail(this.$attrs.params.row.billCode)
    }
  },
  mounted () {
    this.viewflag = this.$attrs.params.flag
  },
  methods: {
    // 计算不良率
    computeUnqualifiedRate () {
      if (this.form.investmentTotal && this.form.unqualifiedTotal) {
        this.form.unqualifiedRate = Number((this.form.unqualifiedTotal / this.form.investmentTotal) * 100).toFixed(2)
      }
    },

    selectHandler (node, value, scope) {
      this.form.orgId = node ? node.organizationId : null
      this.form.orgCode = node ? node.organizationCode : null
      this.form.orgName = node ? node.organizationName : null
    },

    // 库存组件
    selectHandler2 (node, value, scope) {
      this.form.organizationId = node ? node.organizationId : null
      this.form.organizationCode = node ? node.organizationCode : null
      this.form.organizationName = node ? node.organizationName : null
    },
    getProcessAgent (val, scope) {
      scope.processAgent = val ? val.nickname : ''
    },
    // 选择供应商
    getCompanyObj (val, data) {
      this.form.vendorId = val ? val.companyId : ''
      this.form.vendorCode = val ? val.companyCode : ''
      this.form.vendorName = val ? val.companyName : ''
    },
    getDeliveryNumber () {
      if (this.readOnly) return
      if (this.form.vendorName && this.form.orgId) {
        this.visible = true
        this.$refs.dialog.init(this.form.orgId, this.form.vendorName)
      } else {
        this.$message.info(this.$t('qualitySynergy.selectOUandVendor'))
      }
    },
    getSelectedRow (row) {
      this.form.deliveryNumber = row.deliveryNumber
      this.form.materialId = row.materialId
      this.form.materialCode = row.materialCode
      this.form.materialName = row.materialName
    },
    getItemObj (val, data) {
      this.form.materialId = val ? val.materialId : null
      this.form.materialCode = val ? val.materialCode : ''
      this.form.materialName = val ? val.materialName : ''
      this.form.materialStatus = val ? val.materialStatus : ''
    },
    addItem () {
      this.form.quaProcessExceptionHandleList.push({
        problemCategory: '',
        unqualifiedDesc: '',
        quantity: undefined,
        measures: ''
      })
    },
    deleteItem ({ row, $index }) {
      this.form.quaProcessExceptionHandleList.splice($index, 1)
    },
    cancelBill () {
      const { flag, row } = this.$attrs.params
      if (flag === 'add') {
        this.$emit('tab-remove', 'processExceptionDetail')
      } else {
        this.$emit('tab-remove', 'processExceptionDetail' + row.billCode)
      }
      this.__setTabTodo('processExceptionList.getQuerydata')
    },
    save () {
      // 暂存校验 业务实体与供应商 必填
      if (!this.form.orgId) {
        this.$message.error(this.$t('purchaseDemand.orgIdTips'))
        return
      }
      if (!this.form.vendorName) {
        this.$message.error(this.$t('vendorMod.msgVendor'))
        return
      }
      let flag = this.form.billCode ? 'edit' : this.$attrs.params.flag
      // 新增时不用提交主键值
      const { billCode, ...rest } = this.form
      if (flag === 'add') {
        inspectionStandard.processExceptionAdd(rest).then(res => {
          this.form.billCode = res.data
          this.$message({
            type: 'success',
            message: res.message
          })
          this.__setTabTodo('processExceptionList.getQuerydata')
        })
      } else if (flag === 'edit') {
        inspectionStandard.processExceptionAdd(this.form).then(res => {
          this.$message({
            type: 'success',
            message: res.message
          })
          this.__setTabTodo('processExceptionList.getQuerydata')
        })
      }
    },

    publish () {
      this.$refs.form.validate(valid => {
        if (valid) {
          for (const i of this.form.quaProcessExceptionHandleList) {
            if (!i.problemCategory) {
              return this.__jump_error__(
                'problemTypeInfo',
                null,
                this.$t('qualitySynergy.msgSelProblemType'),
              )
            }
            if (!i.unqualifiedDesc) {
              return this.__jump_error__(
                'problemTypeInfo',
                null,
                this.$t('qualitySynergy.unqualifiedDescRequired'),
              )
            }
            if (!i.quantity) {
              return this.__jump_error__(
                'problemTypeInfo',
                null,
                this.$t('qualitySynergy.quantityRequired'),
              )
            }
            if (!i.measures) {
              return this.__jump_error__(
                'problemTypeInfo',
                null,
                this.$t('qualitySynergy.measuresRequired'),
              )
            }
          }

          // 新增时不用提交主键值
          const { billCode, ...rest } = this.form
          inspectionStandard.processExceptionPublish(this.form).then(res => {
            this.$message({
              type: 'success',
              message: res.message
            })
            this.cancelBill()
          })
        } else {
          this.__focus_error__(this.$t('contractMod.msgContractManage[14]'))
        }
      })
    },
    async getFormDetail (billCode) {
      let res = await inspectionStandard.processExceptionDetail({ billCode })
      if (res.data) {
        this.form = res.data
        let resp = await inspectionStandard.processExceptionDetailList({ billCode })
        if (resp.data) {
          this.form.quaProcessExceptionHandleList = resp.data
        }
      }
    },
    createReport8D () {
      if (!this.form.billCode) {
        this.$message.warning(
          this.$t('qualitySynergy.pleaseHoldItTemporarilyBefore8DReportCanBeCreated'),
        )
        return
      }
      let row = this.form
      this.$router.push({
        name: 'report8D',
        params: {
          from: 'processException',
          funName: 'report8D',
          fdSubject: row
        }
      })
    }
  }
}
</script>
