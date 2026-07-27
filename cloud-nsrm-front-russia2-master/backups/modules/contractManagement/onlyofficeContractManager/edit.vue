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
        @click-handler="(type) => saveBill(type)"
        @submit-direct="(type) => saveBill(type)"
        @confirm="(type, comment) => saveBill(type, comment)"
        @close-tab="back"
      >
        <el-collapse
          v-model="activeDims"
          class="tab-form-style"
        >
          <el-collapse-item
            v-if="IS_BUYER()"
            :title="$t('contractMod.createContract')"
            name="1"
          >
            <base-form
              ref="mainForm"
              class="base-form"
              :form-items="mainFormItems"
              :merge-form.sync="mergeForm"
              :inline="false"
              :status-icon="true"
              :show-message="true"
              :disabled="IS_READ_ONLY || !IS_BUYER()"
            >
              <!-- 框架协议编号 -->
              <template #frameworkAgreementCode>
                <el-input v-model="mergeForm.frameworkAgreementCode">
                  <template #append>
                    <el-button
                      type="primary"
                      icon="el-icon-search"
                      @click="showFrameworkAgreement"
                    />
                  </template>
                </el-input>
              </template>
              <!-- 合同类型 -->
              <template #contractType>
                <dict-select
                  v-model="mergeForm.contractClass"
                  code="ELEM_CONTRACT_TYPE"
                  :disabled="getEditable()"
                  @change="elemContractTypeHandler"
                />
              </template>
              <!--合同级别 @change="elemContractTypeHandler" 去掉提示zhaomz-->
              <template #contractLevel>
                <dict-select
                  v-model="mergeForm.contractLevel"
                  disabled
                  code="CONTARCT_LEVEL"
                />
              </template>
              <template #operationType>
                <dict-select
                  v-model="mergeForm.contractType"
                  code="CONTRACT_TYPE"
                  disabled
                  @change="elemContractTypeHandler"
                />
              </template>
              <template #modelHeadId>
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
              </template>
              <template #buId>
                <organization-selector
                  ref="m_ou"
                  v-model="mergeForm.buId"
                  :parent-id="-1"
                  :jump-login="jumpLogin"
                  :placeholder="$t('common.pleaseSelect')"
                  :disabled="mergeForm.contractType !== 'MIAN_CONTRACT_ADD'"
                  node-type="OU"
                  @select="buHandler"
                />
              </template>
              <template #vendorId>
                <quick-search
                  :show-input="mergeForm.vendorName"
                  show-key="companyName"
                  :scope-data="mergeForm"
                  name="scc_sup_company_info_new"
                  :disabled="isFromPriceApproval || mergeForm.contractType !== 'MIAN_CONTRACT_ADD'"
                  @close-quicksearch="getVendorObj"
                />
              </template>
            </base-form>
          </el-collapse-item>
          <!-- 物料明细 -->
          <el-collapse-item
            v-if="IS_BUYER()"
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
              :show-plus="!IS_READ_ONLY"
              :is-buyer="IS_BUYER()"
              :is-framework-agreement="mergeForm.isFrameworkAgreement"
              @change="materialDataChange"
              @select="materialHandleSelectionChange"
            >
              <template #btns>
                <el-button
                  :disabled="illegal == 'view'"
                  type="primary"
                  style="margin: 0 0 10px 0"
                  class="detail-pbtn"
                  @click="priceChange"
                >
                  <!-- 价格变更 -->
                  {{ $t("contractMod.priceChange") }}
                </el-button>
              </template>
            </material-list>
          </el-collapse-item>
          <!-- 付款计划 -->
          <el-collapse-item
            v-if="IS_BUYER()"
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
              @change="payPlanDatachange"
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
            v-if="IS_BUYER()"
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
          <!-- 附件信息 -->
          <el-collapse-item
            ref="file-area"
            :title="$t('bidMod.fileInfo')"
            name="7"
          >
            <el-button
              v-if="!IS_READ_ONLY && IS_BUYER()"
              style="margin-bottom: 10px;"
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
              <!-- 附件名称 -->
              <el-table-column
                align="center"
                prop="fileSourceName"
                :label="$t('bidMod.fileName')"
              >
                <template slot-scope="scope">
                  <SrmCommonFile
                    :extra-data="fileInfo"
                    :default-file="{
                      fileId: scope.row.fileuploadId,
                      fileName: scope.row.fileSourceName
                    }"
                    :readonly="illegal == 'view'"
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
              />
              <!-- 操作 -->
              <el-table-column
                v-if="IS_BUYER()"
                :label="$t('common.operation')"
                width="60"
              >
                <template slot-scope="scope">
                  <el-button
                    v-if="!scope.row.sourceId"
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
          <el-collapse-item
            name="6"
            title="占位符编辑"
          >
            <el-table
              height="150"
              :data="placeholderTableInfo"
              border
            >
              <el-table-column
                align="center"
                prop="elemCode"
                label="占位符"
                :formatter="(row, column, cellValue) => `\$\{${cellValue}\}`"
              />
              <el-table-column
                align="center"
                prop="elemName"
                :label="$t('bidMod.designation')"
              />
              <el-table-column
                align="center"
                prop="elemValue"
                label="替换值"
              >
                <template slot-scope="scope">
                  <el-input
                    v-if="scope.row.addMethod === 'INPUT'"
                    v-model="scope.row.elemValue"
                  />
                  <el-select
                    v-else-if="scope.row.addMethod === 'SELECT'"
                    v-model="scope.row.elemValue"
                  >
                    <el-option
                      v-for="item in scope.row.elemRanges"
                      :key="item.elemRangeId"
                      :label="item.elemValue"
                      :value="item.elemValue"
                    />
                  </el-select>
                  <!-- NOTE: 缓存数据保存时需要那到头部对应字段值，填入elemValue中,以供预览时替换占位符 -->
                  <span
                    v-else-if="scope.row.addMethod === 'REFERENCE'"
                    style="color: red;"
                  >引用头部对应字段</span>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
          <el-collapse-item
            name="7"
            :title="$t('contractMod.contractContent')"
          >
            <iframe
              v-if="iframeSrc"
              width="100%"
              height="800px"
              align="top"
              frameborder="0"
              allowfullscreen=""
              onmousewheel=""
              allow="autoplay; camera; microphone; display-capture"
              :src="iframeSrc"
            />
          </el-collapse-item>
        </el-collapse>
        <div
          id="template"
          class="template"
          style="display: none"
        >
          <div ref="template" />
        </div>
        <template slot="buttonOne">
          <!-- TODO: 下载服务器 -->
          <!-- <el-button v-if="!!mergeForm.modelHeadId && illegal != 'view'" @click="print">{{ $t("route.pdfPrint") }}</el-button> -->
          <!-- 编辑合同详情 -->
          <!-- <el-button v-if="!!mergeForm.modelHeadId && IS_BUYER() && mergeForm.contractType === 'MIAN_CONTRACT_ADD' && illegal != 'view'" type="primary" @click="edit" :disabled="SUBMIT_STATUS()">
            {{ $t("contractMod.editContractDetail") }}
          </el-button> -->
          <!-- 预览合同详情 -->
          <el-button
            v-if="!!mergeForm.modelHeadId && !!mergeForm.contractHeadId && mergeForm.contractType === 'MIAN_CONTRACT_ADD' && illegal != 'view'"
            @click="preview"
          >
            {{ $t("contractMod.previewContractDetail") }}
          </el-button>
          <!-- 发布签章平台 -->
          <el-button
            v-if="mergeForm.contractStatus === 'ARCHIVED'"
            type="primary"
            @click="fillReleaseParams"
          >
            {{ $t("contractMod.releaseSignPlatform") }}
          </el-button>
        </template>
      </CWorkflowMulti>

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
              <organization-selector
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
                {{ $t("common.search") }}
              </el-button>
            </el-form-item>
          </el-form>
        </div>
        <div style="max-height: 300px;overflow: auto">
          <base-table
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
            {{ $t("common.cancel") }}
          </el-button>
          <el-button
            type="primary"
            @click="comfirmSelect"
          >
            {{ $t("common.confirm") }}
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
          <el-row type="flex">
            <el-col :span="12">
              <!-- 供应商 -->
              <el-form-item :label="$t('common.vendor')">
                <el-input
                  v-model="sumForm2.vendorName"
                  disabled
                />
              </el-form-item>
            </el-col>
            <el-col
              :span="8"
              style="padding-left:22px"
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
            </el-col>
            <el-col
              :span="4"
              style="text-align:right;"
            >
              <el-button
                type="primary"
                @click="queryContractData2"
              >
                {{ $t("common.search") }}
              </el-button>
            </el-col>
          </el-row>
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
            label="$t('contractMod.contractName')"
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
        <p style="color: red;">
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
            {{ $t("common.cancel") }}
          </el-button>
          <el-button
            type="primary"
            @click="release"
          >
            {{ $t("common.confirm") }}
          </el-button>
        </div>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import BaseForm from 'lib@/components/BaseForm'
import BaseTable from 'lib@/components/BaseTable'
import QuickSearch from 'lib@/components/QuickSearch'
import { getDictItem, getAllPurCurrency } from '@/api/common'
import { adaptDictData } from '@/utils'
import materialList from './material-list'
import payPlan from './pay-plan'
import partner from './partner'
import cloneDeep from 'lodash/cloneDeep'
import CToolbar from 'lib@/components/c-toolbar'
import { tabTodoMixin } from '@/utils/mixins'
import OrganizationSelector from 'lib@/components/organization-selector'
import { getToken } from '@/utils/auth'

import { isMobile, isEmail } from 'lib@/utils/validate'
import WorkflowCommon from '@/library/mixins/workflow-common'
import axios from 'axios'
import fixedElement from './fixedElement.js'
import getCompanyDetail from './companyCache'
import { sysPrefix } from '@/config/ipConfig'

export default {
  name: 'ContractInformation',
  components: {
    CToolbar,
    BaseForm,
    BaseTable,
    payPlan,
    materialList,
    partner,
    QuickSearch,
    OrganizationSelector
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      // 模板中占位符信息，用于初次渲染占位符编辑区域表格，合同暂存或者提交之后使用合同详情接口返回的字段cache
      placeholderTableInfo: [],
      iframeSrc: '',
      releaseParamsVisible: false,
      releaseParams: {
        name: '',
        phone: '',
        email: ''
      },
      rules: {
        name: [{ required: true, message: this.$t('dataConfMod.msgUserName') }], // "请输入姓名"
        // fileuploadId: [{ required: true, message: "请上传合同" }], //"请上传合同"
        email: [
          { required: true, message: this.$t('dataConfMod.msgMail') }, // "请输入邮箱"
          {
            validator: (rule, value, callback) => {
              if (!value) {
                callback(new Error(this.$t('dataConfMod.msgMail'))) // "请输入邮箱"
              } else if (!isEmail(value)) {
                callback(new Error(this.$t('dataConfMod.msgIllegalMail'))) // "邮箱格式不合法"
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ],
        phone: [
          { required: true, message: this.$t('dataConfMod.msgUserName') },
          {
            validator: (rule, value, callback) => {
              if (!isMobile(value) && value) {
                callback(new Error(this.$t('dataConfMod.msgIllegalPhone'))) // "手机格式不合法"
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ]
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
      materialListData: [],
      IS_READ_ONLY: false,
      sourceList: [],
      queryParams: {},
      menuInfo: null,
      selectionVisible: false,
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'vendorBiddingManagement',
        fileType: 'images'
      },
      activeDims: ['1', '2', '3', '4', '5', '6', '7', '8'],
      userType: this.$store.getters.userType,
      contenteditable: false,
      mergeForm: {
        isFrameworkAgreement: 'Y',
        enable: 'Y',
        ceeaIfVirtual: 'N',
        currencyCode: 'CNY',
        currencyId: '7007437216088064',
        includeTaxAmount: 0,
        currencyName: '人民币'
      },
      fileuploads: [],
      visible: true,
      vendorIdList: [],
      currencyList: [],
      originMaterialTable: [],
      originPayPlanTable: [],
      bankRowIndex: null,
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
            label: (context) => context.$t('contractMod.order')
          }
        },
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'materialCode',
            label: (context) => context.$t('contractMod.materialCode')
          }
        },
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'materialName',
            label: (context) => context.$t('contractMod.materialName')
          }
        },
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'categoryName',
            label: (context) => context.$t('contractMod.categoryName')
          }
        },
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'taxedPrice',
            label: (context) => context.$t('contractMod.taxedPrice')
          }
        },
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'contractQuantity',
            label: (context) => context.$t('contractMod.contractQuantity')
          }
        },
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'unitName',
            label: (context) => context.$t('contractMod.unitName')
          }
        },
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'buName',
            label: (context) => context.$t('contractMod.buId')
          }
        }
      ],
      mainFormItems: [
        {
          itemAttrs: {
            label: () => this.$t('contractMod.contractName'),
            rules: [{ required: true, message: this.$t('dataConfMod.required') }]
          },
          uiAttrs: {
            key: 'contractName',
            maxlength: 100,
            showWordLimit: true
          }
        },
        {
          slot: 'contractType',
          itemAttrs: {
            label: () => this.$t('contractMod.contractType'),
            rules: [{ required: true, message: this.$t('dataConfMod.required') }]
          },
          uiAttrs: {
            key: 'contractClass'
          },
          computedUIAttrs: (Model) => {
            const disabled = Model.contractType !== 'MIAN_CONTRACT_ADD'
            return { disabled }
          }
        },
        {
          itemAttrs: {
            label: () => this.$t('contractMod.templHeadId')
          },
          computedItemAttrs: (Model) => {
            if (Model.ceeaIsPortableContract === 'Y' || this.mergeForm.ceeaIfVirtual === 'Y') {
              return {
                rules: [{ required: false }]
              }
            }
            return Model.enable === 'Y'
              ? {
                  rules: [{ required: true, message: this.$t('dataConfMod.required') }]
                }
              : {
                  rules: [{ required: false }]
                }
          },
          computedUIAttrs: (Model) => {
            const disabled = Model.contractType !== 'MIAN_CONTRACT_ADD'
            return { disabled }
          },
          uiAttrs: {
            key: 'modelHeadId'
          },
          slot: 'modelHeadId'
        },
        {
          tag: 'input',
          itemAttrs: { label: () => this.$t('vendorMod.controlType') },
          uiAttrs: {
            key: 'ceeaControlMethod',
            disabled: true
          }
        },
        {
          itemAttrs: {
            label: () => this.$t('contractMod.contractNo')
          },
          uiAttrs: {
            key: 'contractCode',
            disabled: true
          }
        },
        {
          tag: 'select',
          itemAttrs: {
            label: () => this.$t('contractMod.ifVirtual') // "是否虚拟合同"
          },
          computedUIAttrs: (Model) => {
            const disabled = Model.contractType !== 'MIAN_CONTRACT_ADD'
            return { disabled }
          },
          uiAttrs: {
            key: 'ceeaIfVirtual',
            options: [
              { id: 'N', value: 'N', label: this.$t('common.no') },
              { id: 'Y', value: 'Y', label: this.$t('common.yes') }
            ]
          }
        },
        {
          itemAttrs: {
            label: () => this.$t('contractMod.frameworkAgreementCode') // 框架协议编号
          },
          uiAttrs: {
            key: 'frameworkAgreementCode'
          },
          slot: 'frameworkAgreementCode'
        },
        {
          tag: 'input',
          itemAttrs: {
            label: () => this.$t('contractMod.frameworkAgreementName')
          }, // 框架协议名称
          uiAttrs: {
            key: 'frameworkAgreementName',
            disabled: true
          }
        },
        {
          tag: 'select',
          itemAttrs: { label: () => this.$t('contractMod.standardContract') }, // 标准合同
          computedUIAttrs: (Model) => {
            const disabled = !['MIAN_CONTRACT_ALTER', 'MIAN_CONTRACT_ADD'].includes(Model.contractType)
            return { disabled }
          },
          uiAttrs: {
            key: 'enable',
            options: [
              { id: 'N', value: 'N', label: this.$t('common.no') },
              { id: 'Y', value: 'Y', label: this.$t('common.yes') }
            ]
          },
          listeners: {
            change: () => {
              this.contenteditable = false
              this.templateChange(this.mergeForm.modelHeadId, false)
            }
          }
        },

        {
          itemAttrs: {
            label: () => this.$t('contractMod.vendorName'),
            rules: [{ required: true, message: this.$t('dataConfMod.required') }]
          },
          uiAttrs: {
            key: 'vendorName'
          },
          slot: 'vendorId'
        },
        {
          tag: 'date',
          itemAttrs: { label: () => this.$t('contractMod.contractValidFrom') }, // 合同有效期从
          uiAttrs: {
            key: 'effectiveDateFrom',
            pickerOptions: {
              disabledDate: (time) => {
                const start = new Date().getTime() - 24 * 60 * 60 * 1000
                return time.getTime() <= start
              }
            }
          }
        },
        {
          tag: 'date',
          itemAttrs: { label: () => this.$t('contractMod.contractValidTo') }, // 合同有效期至
          uiAttrs: {
            key: 'effectiveDateTo',
            pickerOptions: {
              disabledDate: (time) => {
                const start = new Date().getTime() - 24 * 60 * 60 * 1000
                return time.getTime() <= start
              }
            }
          }
        },
        {
          itemAttrs: {
            label: () => this.$t('contractMod.contractOldCode')
          },
          uiAttrs: {
            key: 'contractOldCode',
            disabled: true
          }
        },
        {
          tag: 'select',
          itemAttrs: {
            label: () => this.$t('oneStopShopping.ifHeadquarters'), // 是否总部
            rules: [{ required: true, message: this.$t('dataConfMod.required') }]
          },
          /* computedItemAttrs: Model => {
            const required = Model.buId == "7665554795921408";
            return { rules: [{ required, message: "必填" }] };
          }, */
          computedUIAttrs: (Model) => {
            const disabled = Model.contractType !== 'MIAN_CONTRACT_ADD'
            return { disabled }
          },
          uiAttrs: {
            key: 'isHeadquarters',
            options: [
              { id: 'N', value: 'N', label: this.$t('common.no') },
              { id: 'Y', value: 'Y', label: this.$t('common.yes') }
            ]
          }
        },
        {
          tag: 'select',
          itemAttrs: {
            label: () => this.$t('contractMod.isFrameworkAgreement'), // 是否框架协议
            rules: [{ required: true, message: this.$t('dataConfMod.required') }]
          },
          computedUIAttrs: (Model) => {
            const disabled = Model.contractType !== 'MIAN_CONTRACT_ADD'
            return { disabled }
          },
          uiAttrs: {
            key: 'isFrameworkAgreement',
            options: [
              { id: 'N', value: 'N', label: this.$t('common.no') },
              { id: 'Y', value: 'Y', label: this.$t('common.yes') }
            ]
          }
        },
        {
          tag: 'select',
          itemAttrs: {
            label: () => this.$t('contractMod.isPortableContract') // 是否便捷合同
          },
          computedUIAttrs: (Model) => {
            const disabled = Model.includeTaxAmount > 20000
            return { disabled }
          },
          uiAttrs: {
            key: 'ceeaIsPortableContract',
            options: [
              { id: 'N', value: 'N', label: this.$t('common.no') },
              { id: 'Y', value: 'Y', label: this.$t('common.yes') }
            ]
          }
        },
        {
          slot: 'contractLevel',
          itemAttrs: {
            label: () => this.$t('contractMod.contractLevel') // 合同级别
          },
          uiAttrs: {
            disabled: true,
            key: 'contractLevel'
          }
        },
        {
          itemAttrs: {
            label: () => this.$t('contractMod.submissionBusinessEnity'), // 送审业务实体
            rules: [{ required: true, message: this.$t('dataConfMod.required') }]
          },
          computedUIAttrs: (Model) => {
            const disabled = Model.contractType !== 'MIAN_CONTRACT_ADD'
            return { disabled }
          },
          uiAttrs: {
            key: 'buId'
          },
          slot: 'buId'
        },
        {
          tag: 'select',
          itemAttrs: { label: () => this.$t('contractMod.currencyCode') },
          uiAttrs: {
            key: 'currencyCode',
            disabled: false,
            axios: () =>
              getAllPurCurrency().then((res) => {
                this.currencyList = adaptDictData(res.data, 'currency')
                return this.currencyList
              })
          },
          computedUIAttrs: (Model) => {
            const disabled = Model.contractType !== 'MIAN_CONTRACT_ADD'
            return { disabled }
          },
          listeners: {
            change: this.currencyHandler
          }
        },
        // 操作类型
        {
          slot: 'operationType',
          itemAttrs: {
            label: () => this.$t('contractMod.operationType'),
            rules: [{ required: true, message: this.$t('dataConfMod.required') }]
          },
          uiAttrs: {
            key: 'contractType'
          }
        },
        // 状态
        {
          tag: 'select',
          itemAttrs: {
            label: () => this.$t('contractMod.contractStatus')
          },
          uiAttrs: {
            key: 'contractStatus',
            disabled: true,
            axios: () => getDictItem('CONTRACT_STATUS').then((res) => adaptDictData(res.data, 'dict'))
          }
        },
        {
          itemAttrs: { label: () => this.$t('contractMod.totalAmountTax1') }, // 合同总金额(含税)
          uiAttrs: {
            key: 'includeTaxAmount',
            disabled: true
          }
        },
        {
          itemAttrs: { label: () => this.$t('common.creator') },
          uiAttrs: {
            key: 'createdUserName',
            disabled: true
          }
        },
        {
          tag: 'select',
          itemAttrs: {
            label: () => this.$t('contractMod.controlMethod'),
            rules: [{ required: true, message: this.$t('dataConfMod.required') }]
          },
          uiAttrs: {
            key: 'ceeaControlMethod',
            // disabled: true,
            axios: () => getDictItem('MANAGEMENT_CONTROL_MODEL').then((res) => adaptDictData(res.data, 'dict'))
          }
        },
        {
          itemAttrs: { label: () => this.$t('bid_mod.remark') },
          uiAttrs: {
            key: 'contractRemark',
            type: 'textarea',
            span: 24
          }
        },
        {
          itemAttrs: { label: () => this.$t('vendorMod.loggerComment') },
          uiAttrs: {
            key: 'drafterOpinion',
            type: 'textarea',
            span: 24
          }
        }
      ]
    }
  },
  computed: {
    workflowBusinessId () {
      // 用来指定工作流的业务ID
      console.log('workflowBusinessId', this.mergeForm)
      return this.mergeForm ? this.mergeForm.contractHeadId : null
    },
    workflowTabDisabled () {
      console.log('workflowTabDisabled', this.mergeForm)
      return this.mergeForm.contractStatus === 'DRAFT'
    },
    viewUpdateButtonsubmit () {
      return this.IS_BUYER() && (this.mergeForm.contractStatus === 'DRAFT' || (this.$attrs.params ? this.$attrs.params.flag === 'add' : null))
    },
    viewUpdateButtonSave () {
      return this.mergeForm.contractStatus === 'DRAFT' || (this.$attrs.params ? this.$attrs.params.flag === 'add' : null)
    },
    disabledUpdateButton () {
      return this.SUBMIT_STATUS()
    },
    isFromPriceApproval () {
      return this.mergeForm.sourceType === 'PRICE_APPROVAL'
    },
    materialEditableRows () {
      return this.materialListData.filter((i) => {
        if (i.handleMark === undefined || i.handleMark === 0) {
          return true
        }
        return false
      })
    },
    viewUpdateButton () {
      return this.curRole === 'BUYER' && !this.isReadOnly && this.requirementHead.auditStatus !== 'APPROVED'
    }
  },
  watch: {
    // 如果占位符信息变化，更新缓存
    placeholderTableInfo: {
      handler () {
        this.saveCache()
      },
      deep: true
    },
    mergeForm: {
      handler () {
        this.saveCache()
      },
      deep: true
    },

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
    this.buttonConfigInfo.save.view = this.viewUpdateButtonSave
    this.buttonConfigInfo.submit.view = this.viewUpdateButtonsubmit
    this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
    this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton
    this.buttonConfigInfo.cancel.view = !this.isReadOnly
    this.buttonConfigInfo.close.view = this.isReadOnly

    this.illegal = this.$attrs.params.illegal
    this.jumpLogin = this.$attrs.params.jumpLogin
    if (this.illegal === 'view') {
      this.userType = 'BUYER'
    }
    // change by liwenhong
  },
  mounted () {
    this.curOpt = this.$attrs.params.flag
    this.initData()
  },
  methods: {
    // 保存单据缓存数据
    saveCache () {
      if (!this.mergeForm.modelHeadId) return
      const data = this.getParams()
      this.$api.cm.buyer.main.contract.saveCache(data)
    },
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'CONTRACT'
    },
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    fillReleaseParams () {
      this.releaseParamsVisible = true
    },
    async uplaodPDF () {
      // TODO: 获取pdf格式的合同文件，上传到文件中心
      // const blob = await this.getPdfFile()
      // const file = new window.File([blob], "myfile.pdf", {
      //   type: "application/pdf",
      // })
      // const data = {
      //   file,
      //   uploadType: "PAAS_MINIO",
      //   sourceType: "WEB_APP",
      //   fileModular: "api-cm",
      //   fileFunction: "contractInformation",
      //   fileType: "pdf",
      // }
      // const formData = new FormData()
      // formData.append("file", file)
      // for (const [key, value] of Object.entries(data)) {
      //   formData.append(key, value)
      // }
      // const { data: file_data } = await this.$http({
      //   url: `${sysPrefix()}${FILE_UPLOAD}`,
      //   method: "POST",
      //   data: formData,
      //   headers: {
      //     Authorization: "Bearer " + getToken(),
      //     contentType: "form-data",
      //   },
      //   loading: true,
      // })
      // return file_data
    },
    release () {
      this.$refs.releaseParams.validate(async (boolean) => {
        // if (boolean) {
        //   this.$pageLoading.open()
        //   const file_data = await this.uplaodPDF()
        //   const { fileuploadId } = file_data
        //   const { contractHeadId } = this.mergeForm
        //   const data = {
        //     contractHeadId,
        //     fileuploadId,
        //     ...this.releaseParams,
        //   }
        //   this.$api.cm.buyer.main.contract.release(data).then((res) => {
        //     this.releaseParamsVisible = false
        //     this.$pageLoading.close()
        //     this.$message.success(this.$t("contractMod.successPostSignPlatform")) // 发布到签章平台成功！
        //     this.cancel()
        //   })
        // }
      })
    },
    queryContractData2 () {
      this.$http({
        url: '/api-cm/contract/contractHead/listContractHeadByIsMainAndVendorId',
        method: 'POST',
        data: this.sumForm2,
        loading: true
      })
        .then((res) => {
          this.contractDataList2 = res.data.list
          this.frameworkAgreementVisible = true
        })
        .catch((err) => {
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
      // frameworkAgreementId
      console.log(row)
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
    partnerAdd () {
      this.$refs.partnerList.addRow()
    },
    payAdd () {
      this.$refs.payList.addRow()
    },
    materialAdd () {
      this.$refs.materialList.addRow()
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
        this.$api.cm.buyer.main.contract.cratePriceChangeSource(materialSelection).then((res) => {
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
      const { organizationCode, organizationName, fullPathId } = node
      this.mergeForm.buCode = organizationCode
      // this.mergeForm.buName = organizationName;
      this.$set(this.mergeForm, 'buName', organizationName)
      this.mergeForm.buFullPathId = fullPathId
    },
    querySource () {
      if (!this.mergeForm.vendorId) {
        this.$message.warning(this.$t('bid_mod.setPermissionError')) // 请先选择供应商
        return
      }
      this.selectionVisible = true
    },
    queryHandler () {
      const { organizationId, sourceNumber } = this.queryParams
      const params = { vendorId: this.mergeForm.vendorId }
      if (organizationId) params.organizationId = organizationId
      if (sourceNumber) params.sourceNumber = sourceNumber
      this.$api.cm.buyer.main.contract.getMaterialsBySource(params).then((res) => {
        this.sourceList = res.data
      })
    },
    comfirmSelect () {
      this.currenRows.forEach((i) => {
        const flag = (this.materialListData || []).findIndex((j) => j.approvalBiddingItemId === i.approvalBiddingItemId) === -1
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
    async payPlanDatachange (data) {
      const totalPercent = this.payPlanData.reduce((sum, item) => {
        return sum + Number(item.paymentRatio)
      }, 0)
      if (totalPercent > 100) {
        this.$message.error(this.$t('contractMod.msgContractManage[7]')) // 付款比例之和不能大于100！
      }
    },
    getCheckedResults () {
      const hadOuIds = this.partnerData.filter((i) => i.ouId).map((i) => i.ouId)
      const ouIdList = []
      const checkedResults = this.materialEditableRows
        .reduce((unique, item) => {
          if (!unique.map((i) => i.buId).includes(item.buId)) {
            unique.push(item)
          }
          return unique
        }, [])
        .filter((i) => {
          if (!hadOuIds.includes(i.buId) && i.buId) {
            return true
          }
          if (i.ceeaOuId) {
            ouIdList.push(i.ceeaOuId)
          }
          return false
        })
      return { ouIdList, hadOuIds, checkedResults }
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
      if (!isNaN(totalAmount)) {
        this.mergeForm.includeTaxAmount = Number(totalAmount).toFixed(2)
      }
    },
    async materialDataChange (value) {
      console.log('[materialDataChange]', value)
      this.calcIncludeTaxAmount(value)
      // 检查是否有新增的业务实体，有则自动添加到合作伙伴甲方
      const { ouIdList, hadOuIds, checkedResults } = this.getCheckedResults()
      for (let i = 0; i < checkedResults.length; i++) {
        const { buName, buId, materialId } = checkedResults[i] || {}
        if (!buId) continue
        const { data } = await getCompanyDetail(buId)
        const { partnerName, partnerType, ...rest } = data
        if (!partnerName) {
          this.$message.warning(this.$t('contractMod.msgContractManage[8]') + `${buName}` + this.$t('contractMod.msgContractManage[9]'))
          return
        }
        const list = this.getCheckedResults().hadOuIds
        if (!list.includes(buId)) {
          this.partnerData.push({
            partnerType: '甲方',
            partnerName,
            ouId: buId,
            materialId: materialId,
            ...rest
          })
        }
      }
      let ouList = []
      for (const ouId of ouIdList) {
        const { data } = await this.queryOuDetail(ouId)
        const details = [].concat(data.details)
        ouList = ouList.concat(details)
      }
      const finallyList = ouList.filter((i) => !hadOuIds.includes(i.ouId) && i.ouId)
      for (let i = 0; i < finallyList.length; i++) {
        const { ouName, ouId } = checkedResults[i] || {}
        if (!ouId) continue
        const { data } = await getCompanyDetail(ouId)
        const { partnerName, partnerType, ...rest } = data
        if (!partnerName) {
          this.$message.warning(this.$t('contractMod.msgContractManage[8]') + `${ouName}` + this.$t('contractMod.msgContractManage[9]'))
          return
        }
        const list = this.getCheckedResults().hadOuIds
        if (!list.includes(ouId)) {
          this.partnerData.push({
            partnerType: '甲方',
            partnerName,
            ouId,
            materialId: '',
            ...rest
          })
        }
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
    currencyHandler (value) {
      const currency = this.currencyList.find((i) => i.value === value)
      console.log(currency)
      // this.mergeForm.currencyName = currency.label;
      this.$set(this.mergeForm, 'currencyName', currency.label)
      this.mergeForm.currencyId = currency.id
    },
    elemContractTypeHandler (value) {
      this.queryModelHeadList(value)
    },
    queryModelHeadList (value) {
      this.$api.cm.buyer.main.modelListByType(value).then((res) => {
        this.modelHeadIdList = res.data.map((i) => ({
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
      return !formData.contractType || formData.contractType === 'MIAN_CONTRACT_ADD'
    },
    SUBMIT_STATUS () {
      if (this.$attrs.params.isReadOnly) {
        return true
      }
      if (this.$attrs.params.flag === 'add') {
        return false
      }
      const state = this.mergeForm.contractStatus
      if (!this.IS_BUYER() && state === 'SUPPLIER_CONFIRMING') {
        return false
      }
      if (state && ['DRAFT', 'REFUSED', 'WITHDRAW'].includes(state)) {
        return false
      }
      console.log('state', state)
      return true
    },
    addUploadOne () {
      this.fileuploads.push({
        fileuploadId: null,
        fileSourceName: ''
      })
    },
    outerHandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.fileuploadId = fileId.toString()
      row.fileSourceName = fileName
    },
    handleDelClick (index, row) {
      this.fileuploads.splice(index, 1)
    },
    IS_BUYER () {
      return this.userType === 'BUYER'
    },
    getEditable () {
      const flag = this.mergeForm.contractType !== 'MIAN_CONTRACT_ADD'
      return flag ? !!this.mergeForm.contractClass : false
    },
    getEditableA () {
      // [start] by liwenhong
      if (this.$attrs.params.contractType === 'MIAN_CONTRACT_ALTER') {
        return false
      } else {
        const flag = this.mergeForm.contractType !== 'MIAN_CONTRACT_ADD'
        return flag ? !!this.mergeForm.contractClass : false
      }
      // [end] by liwenhong
      //  const flag = this.mergeForm.contractType !== 'MIAN_CONTRACT_ADD'
      // return flag ? !!this.mergeForm.contractClass : false
    },
    IS_MAIN_CONTRACT_ADD () {
      if (!this.$refs.mainForm) {
        return false
      }
      const mainData = this.mergeForm
      return mainData.contractType !== 'MIAN_CONTRACT_ADD'
    },
    async initData () {
      this.IS_READ_ONLY = this.$attrs.params.isReadOnly
      if (this.$attrs.params.flag === 'add') {
        const contractType = this.$attrs.params.contractType // 合同类型
        // this.$nextTick(() => {
        //   this.mergeForm.contractType = contractType;
        //   console.log("--------nextTick-----------", this.mergeForm.contractType);
        // });
        // MIAN_CONTRACT_ALTER 变更
        if (['MIAN_CONTRACT_ALTER', 'SUPPLEMENTAL_AGREEMENT'].includes(contractType)) {
          const { rowId } = this.$attrs.params
          this.setContractInfo(rowId, contractType, 'get')
          // console.log(2222222);
        } else {
          console.log(`[add] contractType : [${contractType}]`)
          this.$nextTick(() => {
            this.$set(this.mergeForm, 'contractType', contractType)
          })
        }
      }
      if (this.$attrs.params.flag !== 'add') {
        const { contractHeadId, contractType } = this.$attrs.params.row

        await this.setContractInfo(contractHeadId, contractType)
      }
      this.saveCache()
    },
    async setContractInfo (contractHeadId, contractType, sourceId = '', isInit = true) {
      const { data } = await this.$api.cm.buyer.main.contract.getInfoById(contractHeadId, sourceId)

      const { contractHead, modelLines, annexes, payPlans, contractMaterials, contractPartners } = data
      this.fileuploads = annexes
      this.payPlanData = payPlans.map((i) => ({
        ...i,
        payExplain: Number(i.payExplain)
      }))
      this.partnerData = contractPartners
      if (contractType === 'MIAN_CONTRACT_ADD') {
        // 新增
        this.mergeForm = Object.assign(this.mergeForm, contractHead)
      } else {
        const { contractType, contractCode, contractHeadId, ...rest } = contractHead
        if (this.$attrs.params.flag === 'add') {
          if (isInit) {
            this.mergeForm = Object.assign(this.mergeForm, { ...rest })
            this.$set(
              this.mergeForm,
              'contractOldCode', // 原合同编号更改byEasion
              contractCode
              // this.$attrs.params.contractOldCode
            )
            this.$set(
              this.mergeForm,
              'ceeaContractOldId', // 原合同id
              contractHeadId
              // this.$attrs.params.contractOldCode
            )
            this.$set(this.mergeForm, 'contractType', this.$attrs.params.contractType)
            // 合同变更 补充协议
            if (this.$attrs.params.contractType === 'MIAN_CONTRACT_ALTER' || this.$attrs.params.contractType === 'SUPPLEMENTAL_AGREEMENT') {
              this.mergeForm.contractStatus = 'DRAFT'
            }
          } else {
            this.mergeForm = Object.assign(this.mergeForm, contractHead)
          }
        } else {
          this.mergeForm = Object.assign(this.mergeForm, contractHead)
        }
      }
      this.materialListData = contractMaterials.map((item) => {
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
      this.compile(isInit)
      this.elemContractTypeHandler(this.mergeForm.contractClass)
    },
    preview () {
      const accessToken = getToken()
      const contractHeadId = this.mergeForm.contractHeadId
      const fileuploadId = this.mergeForm.fileuploadId
      window.open(`${sysPrefix()}/api-onlineview/EditorServlet?uploadId=${fileuploadId}&type=desktop&mode=edit&read=Y&access_token=${accessToken}&shouldReplace=true&contract_head_id=${contractHeadId}`)
    },
    changeHandle (val) {
      this.mergeForm.modelHeadId = val
      if (!val) return
      this.compile()
    },
    reject (type) {
      // 请输入驳回原因
      this.$prompt(this.$t('bidMod.msgRejectReason'), this.$t('common.toRefuse'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel')
      })
        .then(({ value }) => {
          let urlBy = '/api-cm/contract/contractHead/vendorReject'
          this.$http({
            url: urlBy,
            method: 'POST',
            data: { ...this.mergeForm, vendorRejectReason: value },
            loading: true
          })
            .then((data) => {
              this.$message.success(data.message)
              this.cancel()
            })
            .catch((err) => {
              console.log(err)
            })
        })
        .catch(() => {})
    },
    saveBill (type) {
      if (type === 'SUBMIT') {
        // this.preview()
        this.$nextTick(() => this.submit('approval'))
      } else if (type === 'SAVE') {
        // this.preview()
        this.$nextTick(() => this.submit())
      }
    },
    approval () {
      // console.log(123)
      console.log(this.materialListData)
      // this.preview()
      this.$nextTick(() => this.submit('approval'))
    },
    submitHandle () {
      // this.preview()
      this.$nextTick(() => this.submit())
    },
    getParams () {
      const { effectiveDateTo, effectiveDateFrom, ...rest } = this.mergeForm
      const data = {
        modelLines: [],
        annexes: this.fileuploads,
        payPlans: this.payPlanData,
        contractMaterials: this.materialEditableRows,
        contractPartners: this.partnerData,
        contractHead: {
          ...rest,
          content: ''
        }
      }
      if (effectiveDateTo) {
        data.contractHead.effectiveDateTo = this.$dayjs(effectiveDateTo).format('YYYY-MM-DD')
      }
      if (effectiveDateFrom) {
        data.contractHead.effectiveDateFrom = this.$dayjs(effectiveDateFrom).format('YYYY-MM-DD')
      }
      const cache = cloneDeep(this.placeholderTableInfo)
      const alias = {
        amountUpperCase: 'includeTaxAmount'
      }
      cache.forEach((item, index) => {
        const { addMethod, elemCode, elemValue } = item
        if (addMethod === 'REFERENCE') {
          if (alias[elemCode]) {
            cache[index].elemValue = data.contractHead[alias[elemCode]]
          } else {
            cache[index].elemValue = data.contractHead[elemCode]
          }
        }
        if (!cache[index].elemValue) {
          cache[index].elemValue = ''
        }
      })
      // 序列化占位符信息，存入onlyofficeCache字段
      data.contractHead.onlyofficeCache = JSON.stringify(cache)
      return data
    },
    async submit (type = 'submit') {
      this.saveCache()
      // 计算合同总金额
      this.calcIncludeTaxAmount()
      if (this.mergeForm.ceeaIsPortableContract === 'Y') {
        if (this.mergeForm.includeTaxAmount > 20000) {
          // "合同金额大于2万，不能设置为便捷合同，已自动帮您修改为非便捷合同！"
          this.$message.warning(this.$t('contractMod.msgContractManage[10]'))
          this.mergeForm.ceeaIsPortableContract = 'N'
          this.__jump_error__('mainForm')
          return
        }
      }
      // 校验文件是否上传
      let isNull = this.fileuploads.some((i) => !i.fileuploadId)
      if (!this.fileuploads.length) isNull = true
      if (isNull) {
        return this.__jump_error__('file-area', null, this.$t('contractMod.msgContractManage[11]'))
      }
      const form = this.mergeForm

      // debugger
      // 提交时，价格审批单转过来的合同校验 合同级别和有效期
      // if (this.isFromPriceApproval && type === "approval") {
      //   if (!form.contractLevel) {
      //     return this.$message.error(
      //       "合同级别为空，请先暂存，以便系统自动维护合同等级！"
      //     );
      //   }
      // }
      if (type === 'approval' && this.mergeForm.ceeaIfVirtual === 'Y') {
        if (!this.mergeForm.frameworkAgreementCode) {
          return this.__jump_error__('mainForm', null, this.$t('contractMod.msgContractManage[12]'))
        }
      }
      if (type === 'approval' && (!form.effectiveDateTo || !form.effectiveDateFrom)) {
        return this.__jump_error__('mainForm', null, this.$t('contractMod.msgContractManage[13]'))
      }
      if (this.IS_BUYER()) {
        const data = this.getParams()

        try {
          await this.$refs.mainForm.validate()
        } catch (e) {
          this.__focus_error__(this.$t('contractMod.msgContractManage[14]'))
          return
        }

        if (type === 'approval') {
          // 提交审批之后就不能修改
          this.$api.cm.buyer.main.contract.approval(data).then(async (res) => {
            this.$message({
              type: 'success',
              message: res.message
            })
            let contractHeadId = res.data
            await this.setContractInfo(contractHeadId, type, '', false)
            await this.handlerAfter('SUBMIT')
            // this.cancel();
          })
        } else {
          // 提交审批之后就不能修改
          const { contractType, mainContractNo } = this.$attrs.params
          if (contractType !== 'MIAN_CONTRACT_ADD') {
            // 非主合同新增 添加原合同编码
            // data.contractHead.contractOldCode = contractOldCode;
            data.contractHead.mainContractNo = mainContractNo
          }
          this.$api.cm.buyer.main.contract.saveContractOnlyOffice(data).then((res) => {
            this.$message({
              type: 'success',
              message: res.message
            })
            const { contractHeadId } = res.data
            const { contractType } = this.$attrs.params.row || {}
            const type = this.$attrs.params.contractType || contractType
            this.setContractInfo(contractHeadId, type, '', false)
            // this.cancel();
          })
        }
      } else {
        const data = {
          ...form,
          content: ''
        }
        if (data.effectiveDateTo) {
          data.contractHead.effectiveDateTo = this.$dayjs(data.effectiveDateTo).format('YYYY-MM-DD')
        }
        if (data.effectiveDateFrom) {
          data.contractHead.effectiveDateFrom = this.$dayjs(data.effectiveDateFrom).format('YYYY-MM-DD')
        }
        if (form.isFrameworkAgreement === 'N') {
          const len = this.payPlanData.length
          if (!len) {
            return this.__jump_error__('payList', null, this.$t('contractMod.msgContractManage[15]'))
          }
        }
        this.$api.cm.buyer.main.contract.vendorConfirm(data).then((res) => {
          this.$message({
            type: 'success',
            message: res.message
          })
          this.initData()
          // this.cancel();
        })
      }
    },
    cancel () {
      const { row, flag } = this.$attrs.params
      if (flag === 'add') {
        this.$emit('tab-remove', 'contractInformation')
      } else {
        this.$emit('tab-remove', 'contractInformation' + row.contractName)
      }
      this.__setTabTodo('contractList.getQuerydata')
    },
    templateChange (val, isInit = true) {
      this.mergeForm.modelHeadId = val
      const model = this.modelHeadIdList.find((i) => i.value === val)
      this.mergeForm.modelName = model ? model.label : ''
      this.mergeForm.ceeaControlMethod = model ? model.type : ''
      if (!val) return
      this.compile(isInit)
      console.log('modelHeadIdList', this.modelHeadIdList, 'val', val)
    },
    getVendorObj (val, scope) {
      this.mergeForm.vendorId = val ? val.companyId : ''
      this.mergeForm.vendorName = val ? val.companyName : ''
      this.mergeForm.vendorCode = val ? val.companyCode : ''
      this.mergeForm.erpVendorCode = val ? val.erpVendorCode : ''
      this.mergeForm.erpVendorId = val ? val.erpVendorId : ''
      if (val.companyName) {
        const flag = this.partnerData.some((i) => i.partnerType === '乙方')
        if (!flag) {
          this.partnerData.push({
            partnerType: '乙方',
            partnerName: val.companyName
          })
        }
      }
    },
    async compile (isInit = true) {
      const { modelHeadId, fileuploadId, contractStatus } = this.mergeForm
      if (!modelHeadId) {
        return
      }
      const res = await this.$api.cm.buyer.main.getById(modelHeadId)
      const { enable, ceeaControlMethod } = res.data
      if (isInit) {
        this.mergeForm.enable = enable
        this.mergeForm.ceeaControlMethod = ceeaControlMethod
      }
      if (fileuploadId) {
        const accessToken = getToken()
        if (contractStatus === '     ') {
          // 可编辑文档

          this.iframeSrc = `${sysPrefix()}/api-onlineview/EditorServlet?uploadId=${fileuploadId}&type=desktop&mode=edit&access_token=${accessToken}&shouldReplace=false`
        } else {
          // 预览文档
          this.iframeSrc = `${sysPrefix()}/api-onlineview/EditorServlet?uploadId=${fileuploadId}&mode=embedded&access_token=${accessToken}&shouldReplace=false`
        }
      }

      if (!this.mergeForm.contractHeadId) {
        // TIPS: 如果合同还没有暂存/提交，用模板中原始的占位符信息渲染占位符编辑区域。暂存之后使用合同详情接口中contractHead.onlyofficeCache字段
        // 如果是切换模板，直接使用模板中存储的占位符信息渲染编辑区域
        const { data } = await this.$api.cm.buyer.main.contract.getModelElement(modelHeadId)
        this.placeholderTableInfo = data.map((item) => {
          // 如果没有元素id，说明是固定元素，用elemCode去寻找对应的固定元素信息
          if (!item.elemMaintainId) {
            return fixedElement.find((i) => i.elemCode === item.elemCode) || {}
          } else {
            return item
          }
        })
      } else {
        let data = []
        try {
          data = JSON.parse(this.mergeForm.onlyofficeCache)
        } catch (e) {
          console.log('')
        }
        this.placeholderTableInfo = data
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
