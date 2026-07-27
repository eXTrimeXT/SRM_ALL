<template>
  <el-container
    class="flex-container the-logisticsPurchaseApplyDetail-detail"
    direction="vertical"
  >
    <el-main>
      <div class="form-container2">
        <el-collapse
          v-model="activeDims"
          class="tab-form-style"
        >
          <el-form
            ref="requirementHead"
            :model="requirementHead"
            label-width="80px"
            label-position="top"
            :disabled="isReadOnly"
            :rules="rules"
          >
            <!-- 物流采购申请详情 -->
            <el-collapse-item
              ref="aptInfo"
              :title="$t('logisticsMod.logisticsPurchaseApplyDetail')"
              name="1"
            >
              <el-row :gutter="27">
                <el-col :span="6">
                  <!-- 申请编号 -->
                  <el-form-item
                    :label="$t('oneStopShopping.applyNumber')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="requirementHead.requirementHeadNum"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 申请模板 -->
                  <el-form-item
                    :label="$t('logisticsMod.applyTemplate')"
                    :label-width="formLabelWidth"
                    prop="templateCode"
                  >
                    <el-select
                      v-model="requirementHead.templateCode"
                      @change="setTemplate"
                    >
                      <el-option
                        v-for="item in templateAllList"
                        :key="item.templateCode"
                        :label="item.templateName"
                        :value="item.templateCode"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 业务模式 -->
                  <el-form-item
                    :label="$t('logisticsMod.businessMode')"
                    :label-width="formLabelWidth"
                    prop="businessModeCode"
                  >
                    <el-select
                      v-model="requirementHead.businessModeCode"
                      disabled
                    >
                      <el-option
                        v-for="item in businessTypeList"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 运输方式 -->
                  <el-form-item
                    :label="$t('bid_mod.transportType')"
                    :label-width="formLabelWidth"
                    prop="transportModeCode"
                  >
                    <el-select
                      v-model="requirementHead.transportModeCode"
                      disabled
                    >
                      <el-option
                        v-for="item in transportTypeList"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 业务类型 -->
                  <el-form-item
                    :label="$t('dataConfMod.businessType')"
                    :label-width="formLabelWidth"
                    prop="contractType"
                  >
                    <el-select
                      v-model="requirementHead.contractType"
                      :disabled="!requirementHead.businessModeCode"
                      @change="setcontractType"
                    >
                      <el-option
                        v-for="item in contractTypeList"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </el-form-item>
                  <!-- <el-form-item
                    label="业务类型"
                    :label-width="formLabelWidth"
                    prop="ceeaPurchaseType"
                  >
                    <el-select
                      v-model="requirementHead.businessType"
                      @change="setBusinessType"
                    >
                      <el-option
                        v-for="item in purchaseTypeList"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </el-form-item> -->
                </el-col>
                <el-col :span="6">
                  <!-- 服务项目名称 -->
                  <el-form-item
                    :label="$t('logisticsMod.serviceProjectName')"
                    :label-width="formLabelWidth"
                  >
                    <quick-search
                      :show-input="requirementHead.serviceProjectName"
                      :disabled="requirementHead.businessType == 'NOT_PROJECT'"
                      show-key="projectName"
                      :scope-data="requirementHead"
                      name="ceea_logistics_project_info"
                      @close-quicksearch="getProjectObj"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 单位 -->
                  <el-form-item
                    :label="$t('bid_mod.unit')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="requirementHead.unit" />
                  </el-form-item>
                  <!-- <el-form-item
                    label="单位"
                    :label-width="formLabelWidth"
                    prop="unit"
                  >
                    <el-select v-model="requirementHead.unit">
                      <el-option
                        v-for="item in unitList"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </el-form-item> -->
                </el-col>
                <el-col :span="6">
                  <!-- 项目总量 -->
                  <el-form-item
                    :label="$t('logisticsMod.projectTotal')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="requirementHead.projectTotal" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <!-- 申请主题 -->
                  <el-form-item
                    :label="$t('logisticsMod.requirementTitle')"
                    :label-width="formLabelWidth"
                    prop="requirementTitle"
                  >
                    <el-input v-model="requirementHead.requirementTitle" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 需求日期 -->
                  <el-form-item
                    :label="$t('bidMod.ceeaDemandDate')"
                    :label-width="formLabelWidth"
                    prop="demandDate"
                  >
                    <el-date-picker
                      v-model="requirementHead.demandDate"
                      :picker-options="pickerOptions1"
                      type="date"
                      value-format="yyyy-MM-dd"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 预算金额 -->
                  <el-form-item
                    :label="$t('purchaseDemand.ceeaTotalBudget')"
                    :label-width="formLabelWidth"
                    prop="budgetAmount"
                  >
                    <el-input
                      v-model="requirementHead.budgetAmount"
                      v-input-format="{ type: 'float' }"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 币种 -->
                  <el-form-item
                    :label="$t('bidMod.allAurrency')"
                    :label-width="formLabelWidth"
                    prop="currencyCode"
                  >
                    <dict-select
                      v-model="requirementHead.currencyCode"
                      code="currency"
                      @change-value="currencyHandler"
                    />
                  </el-form-item>
                </el-col>
                <!-- :picker-options="pickerOptions1" -->
                <!-- 价格有效开始日期 -->
                <el-col :span="6">
                  <el-form-item
                    :label="$t('logisticsMod.priceStartDate')"
                    :label-width="formLabelWidth"
                    prop="priceStartDate"
                  >
                    <el-date-picker
                      v-model="requirementHead.priceStartDate"
                      type="date"
                      value-format="yyyy-MM-dd"
                      @change="changeStartDate"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 价格有效结束日期 -->
                  <el-form-item
                    :label="$t('logisticsMod.priceEndDate')"
                    :label-width="formLabelWidth"
                    prop="priceEndDate"
                  >
                    <el-date-picker
                      v-model="requirementHead.priceEndDate"
                      :picker-options="endTiumePickerOptions2"
                      type="date"
                      value-format="yyyy-MM-dd"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 项目地可进最大车型 -->
                  <el-form-item
                    :label="$t('logisticsMod.allowedVehicleRules')"
                    :label-width="formLabelWidth"
                    prop="allowedVehicle"
                    :rules="allowedVehicleRules"
                  >
                    <el-select
                      v-model="requirementHead.allowedVehicle"
                      :disabled="requirementHead.businessModeCode != 'I'"
                      filterable
                    >
                      <el-option
                        v-for="item in allowVehicleList"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
                <!-- <el-col :span="6">
                  <el-form-item label="装载量" :label-width="formLabelWidth">
                    <el-input v-model="requirementHead.loadNumber" />
                  </el-form-item>
                </el-col> -->
                <el-col :span="6">
                  <!-- 是否含技术标 -->
                  <el-form-item
                    :label="$t('logisticsMod.ifTechnicaclBId')"
                    :label-width="formLabelWidth"
                  >
                    <el-select v-model="requirementHead.ifVendorSubmitShipDate">
                      <el-option
                        v-for="item in yesNoOptions"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
                <!-- <el-col :span="6">
                  <el-form-item
                    label="合同类型"
                    :label-width="formLabelWidth"
                    prop="contractType"
                  >
                    <el-select v-model="requirementHead.contractType">
                      <el-option
                        v-for="item in contractTypeList"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </el-form-item>
                </el-col> -->
                <el-col :span="6">
                  <!-- 状态 -->
                  <el-form-item
                    :label="$t('common.status')"
                    :label-width="formLabelWidth"
                  >
                    <el-select
                      v-model="requirementHead.requirementStatus"
                      disabled
                    >
                      <el-option
                        v-for="item in statusList"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 指定供应商 -->
                  <el-form-item
                    :label="$t('purchaseDemand.awardedSupplierName')"
                    :label-width="formLabelWidth"
                  >
                    <quick-search
                      :show-input="requirementHead.vendorName"
                      show-key="companyName"
                      :scope-data="requirementHead"
                      name="scc_sup_company_info"
                      @close-quicksearch="getVendorObj"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <span />
                </el-col>
                <el-col :span="12">
                  <!-- 指定供应商原因 -->
                  <el-form-item
                    :label="$t('logisticsMod.speicfySupplierReason')"
                    prop="vendorReason"
                    :rules="vendorReasonRules"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="requirementHead.vendorReason"
                      type="textarea"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <!-- 备注 -->
                  <el-form-item
                    :label="$t('common.remark')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="requirementHead.comments"
                      type="textarea"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 申请人 -->
                  <el-form-item
                    :label="$t('purchaseDemand.applicant')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="requirementHead.applyBy"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 申请部门 -->
                  <el-form-item
                    :label="$t('purchaseDemand.ceeaDepartment')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="requirementHead.applyDepartmentName"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 申请日期 -->
                  <el-form-item
                    :label="$t('purchaseDemand.applyDate')"
                    :label-width="formLabelWidth"
                  >
                    <el-date-picker
                      v-model="requirementHead.applyDate"
                      type="date"
                      value-format="yyyy-MM-dd"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <span />
                </el-col>
              </el-row>
            </el-collapse-item>
          </el-form>
          <!-- 路线信息 -->
          <el-collapse-item
            ref="itemInfo"
            :title="$t('logisticsMod.routeInfo')"
            name="3"
          >
            <div class="btn_line">
              <el-button
                v-if="requirementHead.requirementStatus !== 'APPROVED'"
                type="primary"
                class="detail-pbtn"
                :disabled="isReadOnly || !requirementHead.templateCode"
                @click="addOneItem"
              >
                {{ $t("common.add") }}
              </el-button>
              <el-button
                v-if="requirementHead.requirementStatus !== 'APPROVED'"
                type="primary"
                class="detail-pbtn"
                :disabled="isReadOnly || !requirementLineList.length"
                @click="delMoreItem"
              >
                {{ $t("common.delete") }}
              </el-button>
              <m-import
                ref="import"
                style="display: inline-block;margin: 0 10px;"
                class="importbtn"
                :title="iModal.title"
                :up-load-url="iModal.upLoadUrl"
                :extra-data="extraData"
                :disabled="isReadOnly || !requirementHead.templateCode"
                @downloadTemplate="downloadTemplate"
                @handleSuccess="handleSuccess"
              />
              <el-button
                type="primary"
                class="detail-pbtn"
                :disabled="!requirementHead.requirementHeadId"
                @click="ExcelOut"
              >
                {{ $t("orderMod.excelExport") }}
              </el-button>
            </div>
            <el-table
              ref="tableGrid"
              :data="requirementLineList"
              style="width: 100%"
              border
              :row-height="30"
              max-height="390px"
              highlight-current-row
              @selection-change="checkChange"
            >
              <el-table-column
                fixed="left"
                type="selection"
              />
              <el-table-column
                fixed="left"
                align="center"
                type="index"
                :label="$t('contractMod.tabindex')"
                width="60"
              />
              <templateList
                ref="templateListId"
                :table-header="tableHeader"
                :requirement-line-list="requirementLineList"
                :requirement-head="requirementHead"
                operate-flag-type="applyOperateFlag"
                visible-flag-type="applyVisibleFlag"
                :is-read-only="requirementHead.requirementStatus != 'DRAFT'"
              />
              <el-table-column
                :label="$t('common.operation')"
                fixed="right"
                width="60"
              >
                <template slot-scope="scope">
                  <el-button
                    type="text"
                    :disabled="isReadOnly"
                    @click="deleteOneContent(scope.$index, scope.row)"
                  >
                    {{ $t("common.delete") }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
          <!-- 附件信息 -->
          <el-collapse-item
            :title="$t('bidMod.fileInfo')"
            name="2"
          >
            <p class="btn_line">
              <el-button
                v-if="requirementHead.requirementStatus !== 'APPROVED'"
                type="primary"
                class="detail-pbtn"
                :disabled="isReadOnly"
                @click="addUploadOne"
              >
                {{ $t("common.add") }}
              </el-button>
              <el-button
                v-if="requirementHead.requirementStatus !== 'APPROVED'"
                type="primary"
                class="detail-pbtn"
                :disabled="isReadOnly"
                @click="delUploadMore"
              >
                {{ $t("common.delete") }}
              </el-button>
            </p>
            <el-table
              :data="requirementAttaches"
              style="width: 100%"
              border
              max-height="250px"
              @selection-change="checkChange1"
            >
              <el-table-column type="selection" />
              <el-table-column
                align="center"
                type="index"
                :label="$t('purSettlementMod.tabindex')"
              />
              <el-table-column
                align="center"
                prop="fileName"
                :label="$t('dataConfMod.attachment')"
              >
                <template slot-scope="scope">
                  <SrmCommonFile
                    :extra-data="fileInfo"
                    :default-file="{
                      fileId: scope.row.fileRelationId,
                      fileName: scope.row.fileName
                    }"
                    :readonly="false"
                    @on-change="({file}) => outerHandleUploadSuccess(file,scope.row)"
                  />
                </template>
              </el-table-column>
              <!-- 上传人 -->
              <el-table-column
                align="center"
                prop="createdUserName"
                :label="$t('purchaseDemand.attachmentCreatedBy')"
                :show-overflow-tooltip="true"
              />
              <!-- 上传时间 -->
              <el-table-column
                align="center"
                prop="creationDate"
                :label="$t('purchaseDemand.attachmentCreatedDate')"
                :show-overflow-tooltip="true"
              />
              <el-table-column :label="$t('common.operation')">
                <template slot-scope="scope">
                  <el-button
                    type="text"
                    :disabled="isReadOnly"
                    @click="handleDelClick(scope.$index, scope.row)"
                  >
                    {{ $t("common.delete") }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
        </el-collapse>
      </div>
      <c-toolbar>
        <template slot="right">
          <el-button
            @click="backTo"
          >
            {{
              $t("common.cancel")
            }}
          </el-button>
          <el-button
            v-if="curRole === 'BUYER' && !isReadOnly"
            type="primary"
            :disabled="disabledButton"
            @click="saveBill('SAVE')"
          >
            {{ $t("common.staging") }}
          </el-button>
          <el-button
            v-if="curRole === 'BUYER' && !isReadOnly"
            type="primary"
            :disabled="disabledButton"
            @click="saveBill('SUBMIT')"
          >
            {{ $t("common.submit") }}
          </el-button>
        </template>
      </c-toolbar>
    </el-main>
  </el-container>
</template>
<script>
import CToolbar from 'lib@/components/c-toolbar'
import _pick from 'lodash/pick'
import {
  getDictItem,
  getDictItemList,
  getRegion
} from '@/api/common'
import TableView from 'lib@/components/Table/TableView'
import OrganizationSelector from 'lib@/components/organization-selector'
import { tabTodoMixin } from '@/utils/mixins'
import QuickSearch from 'lib@/components/QuickSearch'
import MainHeader from 'lib@/components/Table/MainHeader'
import { adaptDictData, parseTime } from '@/utils'
import CPagination from 'lib@/components/c-pagination'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import Treeselect, { LOAD_CHILDREN_OPTIONS } from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import templateList from './templateList'

export default {
  name: 'LogisticsPurchaseApplyDetail',
  components: {
    MainHeader,
    TableView,
    templateList,
    CToolbar,
    MImport,
    Treeselect,
    QuickSearch,
    CPagination,
    OrganizationSelector
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      plTableColumnRowStyle: {
        boxSizing: 'border-box',
        overflow: 'hidden',
        whiteSpace: 'nowrap',
        textOverflow: 'ellipsis',
        wordBreak: 'break-all'
      },
      extraData: {
        sourceType: 'WEB_APP',
        uploadType: 'FASTDFS',
        fileModular: 'pm',
        fileFunction: 'purchaseApplication',
        fileType: 'excel'
      },
      iModal: {
        title: this.$t('common.excelImport'),
        upLoadUrl: '/api-pd/pr/requirement-line/importExcel'
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
      // 文件上传配置信息
      fileInfo: {
        uploadType: 'FASTDFS', // 固定参数
        sourceType: 'WEB_APP', // 固定参数
        fileModular: 'sup', // 文件所属模块 -》基础模块
        fileFunction: 'vendorBiddingManagement', // 文件所属功能
        fileType: 'images' // 文件所属类型
      },
      businessTypeList: [],
      transportTypeList: [],
      receivedFactoryOpts: [],
      queryTotal: -1,
      viewSize: 10,
      viewIndex: 1,
      vendorReasonRules: [
        {
          required: false,
          message: this.$t('logisticsMod.msgSpeicfySupplierReason')
        }
      ],
      queryForm: {
        materialCode: '',
        materialName: '',
        organizationId: null,
        orgId: '',
        bigCategoryName: '',
        bigCategoryId: null,
        middleCategoryName: '',
        middleCategoryId: null,
        categoryId: null,
        categoryName: '',
        inputLevel: '',
        selectLevel: '1'
      },
      requirementHead: {
        requirementHeadId: null,
        templateHeadId: null,
        templateCode: null,
        templateName: null,
        businessType: null,
        businessModeCode: null,
        transportModeCode: null,
        requirementHeadNum: '',
        requirementStatus: 'DRAFT',
        ceeaDepartmentId: '',
        ceeaDepartmentCode: '',
        ceeaDepartmentName: '',
        vendorName: null,
        vendorCode: null,
        vendorReason: null,
        allowedVehicle: null,
        budgetAmount: null,
        comments: null,
        unit: null,
        ifVendorSubmitShipDate: 'N',
        applyId: null,
        applyCode: null,
        applyBy: null,
        applyDepartmentId: null,
        applyDepartmentCode: null,
        applyDepartmentName: null,
        serviceProjectCode: null,
        serviceProjectName: null,
        projectTotal: null,
        requirementTitle: null,
        currencyId: null,
        currencyCode: 'CNY',
        currencyName: null,
        priceStartDate: null,
        priceEndDate: null,
        demandDate: null,
        applyDate: parseTime(new Date(), '{y}-{m}-{d}')
      },
      listCount: 0,
      currentPage1: 1,
      unitList: [],
      tableHeader: [],
      requirementLineList: [],
      allRequirementLineList: [],
      activeDims: ['1', '2', '3'],
      yesNoOptions: [
        { value: 'Y', label: this.$t('common.yes') },
        { value: 'N', label: this.$t('common.no') }
      ],
      allowedVehicleRules: [
        {
          required: false,
          message: this.$t('logisticsMod.msgPurchaseApply[0]')
        } // 请选择项目地可进最大车型
      ],
      rules: {
        templateCode: [
          {
            required: true,
            message: this.$t('logisticsMod.msgPurchaseApply[1]')
          }
        ], // 请选择申请模板
        businessModeCode: [
          { required: true, message: this.$t('logisticsMod.msgBusinessMode') }
        ], // 请选择业务模式
        transportModeCode: [
          { required: true, message: this.$t('logisticsMod.msgTransportWay') }
        ], // 请选择运输方式
        businessType: [
          {
            required: true,
            message: this.$t('logisticsMod.msgPurchaseApply[2]')
          }
        ], // 请选择采购类型
        unit: [
          {
            required: true,
            message: this.$t('logisticsMod.msgPurchaseApply[3]')
          }
        ], // 请选择单位
        contractType: [
          {
            required: true,
            message: this.$t('logisticsMod.msgPurchaseApply[4]')
          }
        ], // 请选择合同类型
        currencyCode: [
          { required: true, message: this.$t('vendorMod.msgCurrencyCode') }
        ], // 请选择币种
        demandDate: [
          {
            required: true,
            message: this.$t('logisticsMod.msgPurchaseApply[5]')
          }
        ], // 请选择需求日期
        budgetAmount: [
          { required: true, message: this.$t('bidMod.bidMsgList[4]') }
        ], // 请输入预算金额
        priceStartDate: [
          {
            required: true,
            message: this.$t('logisticsMod.msgPurchaseApply[6]')
          }
        ], // 请选择价格有效开始日期
        priceEndDate: [
          {
            required: true,
            message: this.$t('logisticsMod.msgPurchaseApply[7]')
          }
        ], // 请选择价格有效结束日期
        categoryName: [
          {
            required: true,
            message: this.$t('logisticsMod.msgPurchaseApply[8]')
          }
        ], // 请输入物料大类
        requirementTitle: [
          {
            required: true,
            message: this.$t('logisticsMod.msgPurchaseApply[9]')
          }
        ], // 请输入申请主题
        acceptUserName: [
          {
            required: true,
            message: this.$t('purchaseDemand.acceptUserNameTips')
          }
        ] // 请输入验收人
      },
      YesOrNoOptions: [
        { value: 'Y', label: this.$t('common.yes') },
        { value: 'N', label: this.$t('common.no') }
      ],
      isDisabled: this.$attrs.params.flag == 'edit',
      isReadOnly: this.$attrs.params.flag == 'readOnly',
      isApprovalOnly: this.$attrs.params.flag == 'approvalOnly',
      formLabelWidth: '120px',
      formLabelWidth2: '110px',
      isModify: false,
      disabledButton: false,
      currencyList: [],
      prTypeList: [],
      contractTypeListAll: [],
      contractTypeList: [],
      statusList: [],
      templateAllList: [],
      fileuploadList: [],
      checkLineList: [],
      checkFileList: [],
      purchaseTypeList: [],
      allowVehicleList: [],
      globalCategoryName: null,
      dialogVisible: false,
      requireDialogVisible: false,
      globalNum1: null,
      globalNum2: null,
      globalNum3: null,
      displayItemTable: [],
      requirementAttaches: [],
      multipleSelection: [],
      multipleSelection2: [],
      addressList: [],
      ceeaIf: true,
      globalUserId: null,
      endTiumePickerOptions2: {
        disabledDate: time => {
          const start = new Date(this.requirementHead.priceStartDate)
          return time.getTime() <= start.getTime()
        }
      },
      pickerOptions1: {
        disabledDate: time => {
          const start = new Date()
          return time.getTime() <= start.getTime()
        }
      }
    }
  },
  computed: {
    hideReSubmit () {
      if (this.requirementLineList.find(v => v.applyStatus === 'RETURNING')) {
        return true
      } else {
        return false
      }
    }
  },
  watch: {
    requirementHead: {
      deep: true,
      immediate: true,
      handler () {
        // 国内---I,国际---E
        if (this.requirementHead.businessModeCode === 'I') {
          this.allowedVehicleRules = [
            {
              required: true,
              message: this.$t('logisticsMod.msgPurchaseApply[0]')
            } // 请选择项目地可进最大车型
          ]
        } else {
          this.requirementHead.allowedVehicle = null
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
  created () {
    this.getTemplateList()
    this.globalUserId = this.$store.getters.userInfo.userId
    if (this.$attrs.params.flag == 'add') {
      getDictItem('LOGISTICS_CONFIG_COLUMNS').then(res => {
        if (this.tableHeader.length === 0) {
          this.tableHeader = adaptDictData(res.data, 'dict').map(val => ({
            fieldCode: val.value,
            fieldName: val.label,
            applyVisibleFlag: 'Y',
            purchaseVisibleFlag: 'Y',
            vendorVisibleFlag: 'Y',
            applyNotEmptyFlag: 'Y',
            applyOperateFlag: 'Y',
            comments: null,
            purchaseNotEmptyFlag: 'Y',
            purchaseOperateFlag: 'Y',
            templateLineId: null,
            vendorNotEmptyFlag: 'Y',
            vendorOperateFlag: 'Y'
          }))
        }
      })
      const {
        companyId,
        userId,
        nickname,
        username,
        ceeaDeptId,
        department
      } = this.$store.getters.user.userInfo
      this.requirementHead.applyId = userId
      this.requirementHead.applyBy = nickname
      this.requirementHead.applyDepartmentId = ceeaDeptId
      // applyCode---applyDepartmentCode
      this.requirementHead.applyDepartmentName = department
    } else {
      this.getFormDetail(this.$attrs.params.row.requirementHeadId)
    }
    // 批量查询字典
    let dictParamsArr = [
      { dictCode: 'BUSINESS_MODE' },
      { dictCode: 'TRANSPORT_MODE' },
      { dictCode: 'LOGISTICS_APPLY_STATUS' },
      { dictCode: 'LOGISTICS_BUSINESS_TYPE' },
      { dictCode: 'ALLOWED_VEHICLE' },
      { dictCode: 'LOGISTICS_UNIT' },
      { dictCode: 'lgt_contract_type' },
      { dictCode: 'BUSINESS_MODE' },
      { dictCode: 'BUSINESS_MODE' }
    ]
    getDictItemList(dictParamsArr).then(res => {
      const [
        BUSINESS_MODE,
        TRANSPORT_MODE,
        LOGISTICS_APPLY_STATUS,
        LOGISTICS_BUSINESS_TYPE,
        ALLOWED_VEHICLE,
        LOGISTICS_UNIT,
        lgt_contract_type
      ] = res.data
      this.businessTypeList = adaptDictData(BUSINESS_MODE.BUSINESS_MODE)
      this.transportTypeList = adaptDictData(TRANSPORT_MODE.TRANSPORT_MODE)
      this.statusList = adaptDictData(
        LOGISTICS_APPLY_STATUS.LOGISTICS_APPLY_STATUS
      )
      this.purchaseTypeList = adaptDictData(
        LOGISTICS_BUSINESS_TYPE.LOGISTICS_BUSINESS_TYPE
      )
      this.allowVehicleList = adaptDictData(ALLOWED_VEHICLE.ALLOWED_VEHICLE)
      this.unitList = adaptDictData(LOGISTICS_UNIT.LOGISTICS_UNIT)
      this.contractTypeListAll = adaptDictData(
        lgt_contract_type.lgt_contract_type
      )
      // 国内---I,国际---E
      if (this.requirementHead.businessModeCode === 'I') {
        this.contractTypeList = this.contractTypeListAll.filter(v =>
          ['A', 'B', 'D', 'E', 'F'].includes(v.value)
        )
      } else if (this.requirementHead.businessModeCode === 'E') {
        this.contractTypeList = this.contractTypeListAll.filter(v =>
          ['A', 'B', 'D', 'G', 'H'].includes(v.value)
        )
      }
    })
  },
  methods: {
    changeStartDate () {
      this.requirementHead.priceEndDate = null
    },
    getTemplateList () {
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
        .then(data => {
          this.templateAllList = data.data.list
        })
        .catch(err => {
          console.log(err)
        })
    },
    indexClickTo (code) {
      let anchorEle = this.$refs[code].$el
      if (anchorEle) {
        anchorEle.scrollIntoView(true)
      }
    },
    handleSuccess ({ data }, file, fileList) {
      // 导入成功就刷新界面
      if (data.length) {
        let list = data.map(i => ({
          ...i,
          expenseItemList: [
            {
              chargeCode: i.expenseItem,
              chargeName: i.expenseItemName
            }
          ],
          unitList: [
            {
              chargeUnit: i.chargeUnit,
              chargeUnitName: i.chargeUnitName
            }
          ],
          provinceList: [
            {
              value: i.fromProvinceCode,
              label: i.fromProvince
            }
          ],
          endProvinceList: [
            {
              value: i.toProvinceCode,
              label: i.toProvince
            }
          ],
          startCityList: [
            {
              value: i.fromCityCode,
              label: i.fromCity
            }
          ],
          startCountyList: [
            {
              value: i.fromCountyCode,
              label: i.fromCounty
            }
          ],
          endCityList: [
            {
              value: i.toCityCode,
              label: i.toCity
            }
          ],
          endCountyList: [
            {
              value: i.toCountyCode,
              label: i.toCounty
            }
          ]
        }))
        this.requirementLineList = [...list, ...this.requirementLineList]
      }
    },
    downloadTemplate () {
      downloadFileLink(
        '/api-pd/pr/requirement-line/importModelDownload2',
        this.$t('logisticsMod.routeDetailImportXLSX')
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },
    ExcelOut () {
      downloadFileLink(
        '/api-pd/pr/requirement-line/export?id=' +
          this.requirementHead.requirementHeadId,
        this.$t('logisticsMod.routeDetailExportXLSX')
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },
    handleCurrentChange (val) {
      this.requirementLineList = this.allRequirementLineList.slice(
        (val - 1) * 50,
        val * 50
      )
    },
    getFormDetail (requirementHeadId) {
      this.$http({
        url: '/api-pd/pr/requirement-head/getByHeadId',
        method: 'GET',
        params: { requirementHeadId: requirementHeadId },
        loading: true
      })
        .then(data => {
          if (data.data) {
            this.requirementHead = data.data.requirementHead || {}
            this.requirementAttaches = data.data.requirementAttaches
            this.requirementLineList = data.data.requirementLineList.map(i => ({
              ...i,
              expenseItemList: [
                {
                  chargeCode: i.expenseItem,
                  chargeName: i.expenseItemName
                }
              ],
              unitList: [
                {
                  chargeUnit: i.chargeUnit,
                  chargeUnitName: i.chargeUnitName
                }
              ],
              provinceList: [
                {
                  value: i.fromProvinceCode,
                  label: i.fromProvince
                }
              ],
              endProvinceList: [
                {
                  value: i.toProvinceCode,
                  label: i.toProvince
                }
              ],
              startCityList: [
                {
                  value: i.fromCityCode,
                  label: i.fromCity
                }
              ],
              startCountyList: [
                {
                  value: i.fromCountyCode,
                  label: i.fromCounty
                }
              ],
              endCityList: [
                {
                  value: i.toCityCode,
                  label: i.toCity
                }
              ],
              endCountyList: [
                {
                  value: i.toCountyCode,
                  label: i.toCounty
                }
              ]
            }))
            let extraData = this.extraData
            if (data.data.requirementHead.templateHeadId) {
              this.getTemplate(data.data.requirementHead.templateHeadId)
            }
            // this.extraData = {...extraData,...data.data.requirementHead}
            let heaader = _pick(data.data.requirementHead, [
              'categoryCode',
              'categoryId',
              'categoryName',
              'ceeaAssetType',
              // "ceeaProjectUserId",
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
            this.extraData = { ...heaader, ...extraData }
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    deleteOneContent (index, row) {
      if (row.requirementLineId) {
        this.$http({
          url: '/api-sup-ce/pr/requirementLine/delete',
          method: 'GET',
          params: { id: row.requirementLineId },
          loading: true
        })
          .then(data => {
            this.$message({
              message: this.$t('common.successDelete'), // '删除成功'
              type: 'success'
            })
            this.requirementLineList.splice(index, 1)
          })
          .catch(err => {
            console.log(err)
          })
      } else {
        this.requirementLineList.splice(index, 1)
      }
    },
    checkChange (val) {
      this.checkLineList = val.map(i => i.id || i.requirementLineId) || []
    },
    delMoreItem () {
      if (!this.checkLineList.length) {
        this.$message({
          message: this.$t('logisticsMod.msgPurchaseApply[10]'), // 请选择要删除的行
          type: 'error'
        })
        return
      }
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          let arr = []
          this.requirementLineList.map(i => {
            if (
              !this.checkLineList.includes(i.id) &&
              !this.checkLineList.includes(i.requirementLineId)
            ) {
              arr.push(i)
            }
          })
          this.requirementLineList = arr
        })
        .catch(() => {})
    },
    addUploadOne () {
      this.requirementAttaches.push({
        id: Math.floor(Math.random() * 1000000),
        attachId: null,
        fileRelationId: null,
        fileName: ''
      })
    },
    delUploadMore () {
      if (!this.checkFileList.length) {
        this.$message({
          message: this.$t('logisticsMod.msgPurchaseApply[10]'),
          type: 'error'
        })
        return
      }
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          let arr = []
          this.requirementAttaches.map(i => {
            if (
              !this.checkFileList.includes(i.id) &&
              !this.checkFileList.includes(i.fileRelationId)
            ) {
              arr.push(i)
            }
          })
          this.requirementAttaches = arr
        })
        .catch(() => {})
    },
    checkChange1 (val) {
      this.checkFileList = val.map(i => i.id || i.fileRelationId) || []
    },
    getcontractObj (val, scope) {
      scope.contractId = val ? val.contractHeadId : ''
      scope.contractNo = val ? val.contractNo : ''
      scope.contractName = val ? val.contractName : ''
    },
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
      if (scope.vendorId) {
        this.vendorReasonRules = [
          {
            required: true,
            message: this.$t('logisticsMod.msgSpeicfySupplierReason')
          }
        ]
      } else {
        this.vendorReasonRules = [
          {
            required: false,
            message: this.$t('logisticsMod.msgSpeicfySupplierReason')
          }
        ]
      }
    },
    getItemObj (val, scope) {
      scope.materialId = val ? val.materialId : ''
      scope.materialCode = val ? val.materialCode : ''
      scope.materialName = val ? val.materialName : ''
    },
    handleItemSelection (val) {
      this.multipleSelection = val
    },
    handleItemDBClick (val) {
      this.multipleSelection = [val]
      this.addOneContent()
    },
    handleItemSelection2 (val) {
      this.multipleSelection2 = val
    },
    outerHandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.fileRelationId = fileId.toString()
      row.fileName = fileName
    },
    // 行删除
    handleDelClick (index, row) {
      if (row.fileRelationId) {
        this.$http({
          url: '/api-file/file/fileupload/delete',
          method: 'POST',
          params: { id: row.fileRelationId },
          loading: true
        }).then(res => {
          this.requirementAttaches.splice(index, 1)
        })
      } else {
        this.requirementAttaches.splice(index, 1)
      }
    },
    openRequireDialog () {
      // 申请状态为已审批时可点击
      if (this.multipleSelection2.length !== 1) {
        this.$message({
          type: 'warning',
          message: this.$t('purchaseDemand.openRequireDialogTips1')
        }) // 请选择一条明细行!
        return
      }
      this.globalNum1 = this.multipleSelection2[0].requirementQuantity
      this.globalNum2 = this.multipleSelection2[0].ceeaExecutedQuantity
      // 【修改后数量】默认为原申请数量-已下单数量
      this.globalNum3 =
        this.multipleSelection2[0].requirementQuantity -
        this.multipleSelection2[0].ceeaExecutedQuantity
      this.requireDialogVisible = true
    },
    requirementChange () {
      if (!this.globalNum3) {
        this.$message({
          type: 'warning',
          message: this.$t('purchaseDemand.requirementChangeTips1')
        }) // 请输入修改后数量!
        return
      }
      if (
        this.globalNum3 > this.multipleSelection2[0].requirementQuantity ||
        this.globalNum3 < this.multipleSelection2[0].ceeaExecutedQuantity
      ) {
        this.$message({
          type: 'warning',
          message: this.$t('purchaseDemand.requirementChangeTips2') // [修改后数量]应该大于[已下单数量]并且小于[原需求数量]!
        })
        return
      }
      this.$http({
        url: '/api-sup-ce/pr/requirementLine/updateNum',
        method: 'POST',
        data: {
          requirementLineId: this.multipleSelection2[0].requirementLineId,
          thisUpdateNum: this.globalNum3
        },
        loading: true
      })
        .then(data => {
          this.$message.success(this.$t('common.success')) // 操作成功!
          this.requireDialogVisible = false
          this.getFormDetail(this.$attrs.params.row.requirementHeadId)
        })
        .catch(err => {
          console.log(err)
        })
    },
    getProjectObj (val, scope) {
      scope.serviceProjectCode = val ? val.projectCode : ''
      scope.serviceProjectName = val ? val.projectName : ''
      scope.unit = val ? val.unit : ''
      scope.projectTotal = val ? val.projectTotal : ''
      scope.requirementTitle = val ? val.projectName : ''
    },
    setCurrencyObj (val) {
      let obj = this.currencyList.find(v => v.value === val) || {}
      this.requirementHead.currencyId = obj.id
      this.requirementHead.currencyName = obj.label
    },
    currencyHandler (value, dictItem) {
      this.requirementHead.currencyId = dictItem.id
      this.requirementHead.currencyName = dictItem.label
    },
    setcontractType (val) {
      this.requirementHead.businessType =
        val == 'A' ? 'PROJECT' : 'NOT_PROJECT'
      if (val !== 'A') {
        this.requirementHead.serviceProjectCode = null
        this.requirementHead.serviceProjectName = null
        this.requirementHead.requirementTitle = null
        this.requirementHead.unit = null
        this.requirementHead.projectTotal = null
      }
    },
    setTemplate (val) {
      let row = this.templateAllList.find(v => v.templateCode === val) || {}
      this.requirementHead.templateHeadId = row.templateHeadId
      this.requirementHead.templateName = row.templateName
      this.requirementHead.ifVendorSubmitShipDate = row.vendorIfSubmitShip
      this.requirementLineList = []
      this.$http({
        url:
          '/api-pd/logistics/logistics-template-head/listTemplateLinesByHeadId',
        method: 'GET',
        params: { headId: row.templateHeadId },
        loading: true
      })
        .then(data => {
          if (data.data) {
            this.requirementHead.businessModeCode = (
              data.data.templateHead || {}
            ).businessModeCode
            // 国内---I,国际---E
            if (this.requirementHead.businessModeCode === 'I') {
              this.contractTypeList = this.contractTypeListAll.filter(v =>
                ['A', 'B', 'D', 'E', 'F'].includes(v.value)
              )
            } else if (this.requirementHead.businessModeCode === 'E') {
              this.contractTypeList = this.contractTypeListAll.filter(v =>
                ['A', 'B', 'D', 'G', 'H'].includes(v.value)
              )
            }
            this.requirementHead.transportModeCode = (
              data.data.templateHead || {}
            ).transportModeCode
            this.tableHeader = data.data.templateLines
            // this.$refs.templateListId.updateTemplate(data.data.templateLines);
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    getTemplate (templateHeadId) {
      this.$http({
        url:
          '/api-pd/logistics/logistics-template-head/listTemplateLinesByHeadId',
        method: 'GET',
        params: { headId: templateHeadId },
        loading: true
      })
        .then(data => {
          if (data.data) {
            this.tableHeader = data.data.templateLines
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    importMaterialItems () {},
    // 改变 currentNum
    changeCurrentIndex (currentNum) {
      this.viewIndex = currentNum
      this.queryContent()
    },
    // 改变 currentSize
    changeCurrentSize (currentSize) {
      this.viewSize = currentSize
      this.queryContent()
    },
    addOneItem () {
      this.requirementLineList.unshift({
        id: Math.floor(Math.random() * 1000000),
        currency: 'CNY'
      })
    },
    getLocationObj (val, scope) {
      scope.ceeaDeliveryPlace = val ? val.locationName : null
    },
    cancelItem () {
      if (this.multipleSelection2.length === 0) {
        this.$message({
          type: 'warning',
          message: this.$t('purchaseDemand.pleaseSelectDetailRow')
        })
        return
      }
      // let params = this.multipleSelection2.map(v=>v.requirementLineId);
      this.$http({
        url: '/api-sup-ce/pr/requirementLine/cancel',
        method: 'POST',
        data: {
          requirementHeadId: this.requirementHead.requirementHeadId,
          requirementLineIds: this.multipleSelection2.map(
            v => v.requirementLineId
          )
        },
        loading: true
      })
        .then(data => {
          this.$message.success(this.$t('common.success'))
          this.getFormDetail(this.$attrs.params.row.requirementHeadId)
        })
        .catch(err => {
          console.log(err)
        })
    },
    reSubmit () {
      if (this.multipleSelection2.length === 0) {
        this.$message({
          type: 'warning',
          message: this.$t('purchaseDemand.pleaseSelectDetailRow')
        })
        return
      }
      for (let item of this.multipleSelection2) {
        if (item.applyStatus !== 'RETURNING') {
          this.$message({
            type: 'warning',
            message: this.$t('purchaseDemand.reSubmitTips1') // 请选择[待退回]状态的明细行!
          })
          return
        }
      }
      let params = this.multipleSelection2.map(v => v.requirementLineId)
      this.$http({
        url: '/api-sup-ce/pr/requirementLine/resubmit',
        method: 'POST',
        data: params,
        loading: true
      })
        .then(data => {
          this.$message.success(this.$t('common.success'))
          this.getFormDetail(this.$attrs.params.row.requirementHeadId)
        })
        .catch(err => {
          console.log(err)
        })
    },
    queryContent () {
      this.$http({
        url:
          '/api-base/material/materialItem/listMaterialByPurchaseCategory',
        method: 'POST',
        data: {
          // "purchaseCategories": res.data,
          categoryId: this.requirementHead.categoryId,
          materialCode: this.queryForm.materialCode,
          materialName: this.queryForm.materialName,
          organizationId: this.requirementHead.organizationId,
          organizationName: this.requirementHead.organizationName,
          ceeaPurchaseType: this.requirementHead.ceeaPurchaseType,
          pageSize: this.viewSize,
          pageNum: this.viewIndex
        },
        laoding: true
      }).then(data => {
        if (data && data.data) {
          this.displayItemTable = data.data.list
          this.queryTotal = data.data.total
        }
      })
      /* this.$http({
        url: "/api-base/purchase/purchaseCategory/queryCategoryByType",
        method: "GET",
        params: {
          enabled: "Y",
          level: 1,     //默认-大类
          param: this.requirementHead.categoryName,
        },
        loading: true
      }).then(res => {

      }); */
    },
    selectHandler (node, value, scope) {
      this.requirementHead.orgId = node ? node.organizationId : null
      this.requirementHead.orgCode = node ? node.organizationCode : null
      this.requirementHead.orgName = node ? node.organizationName : null
      // 清空库存组织
      this.requirementHead.organizationId = null
      this.requirementHead.organizationCode = null
      this.requirementHead.organizationName = null
      // if(node) {
      //   this.getSiteByOrgId(node.organizationId);
      // }
    },
    getSiteByOrgId (organizationId) {
      this.$http({
        url:
          '/api-base/organization/organization/getOrganizationByOrgCode',
        method: 'POST',
        data: {
          organizationTypeCode: 'INV',
          parentOrganizationId: organizationId,
          userId: this.globalUserId
        },
        loading: true
      }).then(res => {
        this.addressList = res.data
      })
    },
    addOneContent () {
      if (this.multipleSelection.length === 0) {
        return
      }
      this.multipleSelection.map(v => {
        this.requirementLineList.push({
          applyStatus: v.applyStatus,
          organizationId: v.organizationId,
          organizationCode: v.organizationCode,
          organizationName: v.organizationName,
          ceeaDeliveryPlace: null,
          categoryName: v.categoryName,
          categoryId: v.categoryId,
          materialId: v.materialId,
          materialCode: v.materialCode,
          materialName: v.materialName,
          unit: v.unitName,
          unitCode: v.unit,
          vendorId: null,
          vendorCode: null,
          vendorName: null,
          requirementQuantity: v.requirementQuantity,
          notaxPrice: v.notaxPrice,
          totalAmount: (v.notaxPrice || 0) * (v.requirementQuantity || 0),
          ceeaIfDirectory: v.ceeaIfDirectory,
          ceeaIe: v.ceeaIfDirectory == 'Y' ? 'true' : 'false',
          requirementDate: v.requirementDate,
          applyReason: v.applyReason,
          ceeaSupUserNickname: v.ceeaSupUserNickname,
          ceeaStrategyUserNickname: v.ceeaStrategyUserNickname,
          ceeaPerformUserNickname: v.ceeaPerformUserNickname,
          ceeaExecutedQuantity: v.ceeaExecutedQuantity,
          rejectReason: v.rejectReason,
          ceeaBusinessSmall: this.requirementHead.ceeaBusinessSmall,
          ceeaBusinessSmallCode: this.requirementHead.ceeaBusinessSmallCode
        })
      })
      this.dialogVisible = false
    },
    getCategoryObj (val, scope) {
      scope.categoryId = val ? val.categoryId : ''
      scope.categoryCode = val ? val.categoryCode : ''
      scope.categoryName = val ? val.categoryName : ''
    },
    setTotalAmount (row) {
      if (row.requirementQuantity <= 0) {
        return this.$message.error(this.$t('purchaseDemand.saveBillTips7')) // 申请数量必须为正数!
      }
      row.totalAmount = Number(
        Number(row.notaxPrice || 0) * Number(row.requirementQuantity || 0)
      ).toFixed(2)
      setTimeout(() => {
        const totalAmountArr = this.requirementLineList.map(
          v => v.totalAmount || 0
        )
        this.requirementHead.budgetAmount = totalAmountArr.reduce(
          (p, c) => (Number(p) || 0) + (Number(c) || 0)
        )
      }, 100)
    },
    copyOneContent () {},
    addOrgObj (e, value, scope) {
      scope.orgId = e ? e.organizationId : ''
      scope.orgCode = e ? e.organizationCode : ''
      scope.orgName = e ? e.organizationName : ''
    },
    getUserObj (val, scope) {
      scope.createdId = val ? val.userId : ''
      scope.createdFullName = val ? val.nickname : ''
    },
    getUserObj2 (val, scope) {
      scope.ceeaProjectUserId = val ? val.userId : ''
      scope.ceeaProjectUserNickname = val ? val.nickname : ''
    },
    getBusinessSmallObj (val, scope) {
      scope.ceeaBusinessSmall = val ? val.dictItemName : ''
      scope.ceeaBusinessSmallCode = val ? val.dictItemCode : ''
      for (let item of this.requirementLineList) {
        item['ceeaBusinessSmall'] = val ? val.dictItemName : ''
        item['ceeaBusinessSmallCode'] = val ? val.dictItemCode : ''
      }
    },
    getLabel (dictionary = [], val) {
      const labelOpt = dictionary.find(i => i.value === val)
      if (labelOpt) return labelOpt.label
      return val
    },
    formatterStatus (row, column, cellValue, index) {
      // return this.getLabel(this.applicationStatusList, cellValue);
    },
    formatterIfDirector (row, column, cellValue, index) {
      if (cellValue === 'Y') {
        this.ceeaIf = true
      } else {
        this.ceeaIf = false
      }
      return cellValue === 'Y' ? this.$t('common.yes') : this.$t('common.no')
    },
    // 品类
    categoryNormalizer (node) {
      const result = {
        id: node.categoryId,
        label: node.categoryName,
        isNew: true
      }
      return result
    },
    // 品类选择值修改
    treeSelectChange (nodes) {
      if (nodes.length > 0) {
      }
    },
    // 品类加载
    catLoadOptions ({ action, parentNode, callback }) {
      if (action === LOAD_CHILDREN_OPTIONS) {
        this.$api.base.basicSetting.getCatChildrenData({ categoryId: parentNode.categoryId })
          .then(res => {
            parentNode.children = res.data.map(i => ({ ...i, children: null }))
            callback()
          })
          .catch(err => {
            parentNode.children = null
            callback(new Error(err.message))
          })
      }
    },
    backTo () {
      if (['edit', 'readOnly'].includes(this.$attrs.params.flag)) {
        this.$emit(
          'tab-remove',
          'logisticsPurchaseApplyDetail' +
            this.$attrs.params.row.requirementHeadNum
        )
      } else {
        this.$emit('tab-remove', 'logisticsPurchaseApplyDetail')
      }
      this.__setTabTodo('logisticsPurchaseApplyList.getQuerydata')
    },
    saveBill (type) {
      // debugger
      // return;
      this.disabledButton = true
      setTimeout(() => {
        this.disabledButton = false
      }, 1000)
      this.$refs.requirementHead.validate(valid => {
        if (valid) {
          if (type === 'SUBMIT') {
            if (this.requirementLineList.length === 0) {
              this.$message.error(this.$t('logisticsMod.msgPurchaseApply[11]')) // 请至少添加一行明细信息!
              this.indexClickTo('itemInfo') // 定位到需求信息栏位
              return
            }
          }
          let allparam = {
            requirementHead: this.requirementHead,
            requirementAttaches: this.requirementAttaches,
            requirementLineList: this.requirementLineList
          }
          let saveUrl = this.requirementHead.requirementHeadId
            ? '/api-pd/pr/requirement-head/modify'
            : '/api-pd/pr/requirement-head/add'
          if (type === 'SUBMIT') {
            saveUrl = '/api-pd/pr/requirement-head/submitApproval'
          }
          this.$http({
            url: saveUrl,
            method: 'POST',
            data: allparam,
            loading: true
          })
            .then(data => {
              this.$message({
                message: this.$t('common.success'),
                type: 'success'
              })
              if (type === 'SUBMIT') {
                this.backTo()
              } else {
                this.getFormDetail(data.data)
              }
            })
            .catch(err => {
              console.log(err) // 预算告警忽略标识---Y
              if (type === 'SUBMIT') {
                this.requirementHead.budgetIgnore = 'Y'
              }
            })
        } else {
          this.indexClickTo('aptInfo') // 定位到申请信息栏位
          return this.$message.error(
            this.$t('logisticsMod.msgPurchaseApply[12]')
          ) // 带*的字段，要求必填，请维护字段值!
        }
      })
    }
  }
}
</script>
<style scoped lang="scss">
.the-logisticsPurchaseApplyDetail-detail {
  .form-container2 {
    padding: 16px 16px 0 16px;
  }
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .el-table .el-date-editor {
    width: 135px;
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
    margin: 0 0 10px 0;
  }
  .the_follow_tender_dialog .el-row {
    margin-bottom: 11px;
    .el-col > span {
      padding-right: 11px;
    }
  }
}
.importbtn /deep/ .el-button {
  min-width: 56px;
  height: 24px;
  line-height: 22px;
  font-size: 14px;
  border-radius: 2px;
  padding: 1px 14px;
}
</style>
