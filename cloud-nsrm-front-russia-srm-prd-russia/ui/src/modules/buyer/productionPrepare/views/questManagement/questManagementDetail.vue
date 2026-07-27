<template>
  <el-container class="questManagementDetail" direction="vertical">
    <el-main>
      <div class="form-container">
        <el-collapse v-model="activeDims" class="tab-form-style">
          <!-- 调查表基本信息 -->
          <el-collapse-item ref="questInfo" title="调查表基本信息" name="1">
            <el-form ref="questInfoForm" :model="form" :rules="questInfoRules" :disabled="disabledFlag">
              <srm-row :gutter="32">
                <srm-col>
                  <!-- 调查模板类型 -->
                  <el-form-item label="调查模板类型" prop="questTemplateType">
                    <dict-select
                      v-model="form.questTemplateType"
                      :disabled="curRole === 'VENDOR'"
                      code="QUEST_TEMPLATE_TYPE"
                      @change-value="getQuestTemplateType"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item prop="questTemplateId" label="调查表模板">
                    <QuickSearch
                      :preQueryData="{ 't.QUEST_TEMPLATE_TYPE': form.questTemplateType }"
                      :showInput="form.questTemplateName"
                      show-key="questTemplateName"
                      :scope-data="form"
                      name="scc_ppap_quest_template"
                      :disabled="curRole === 'VENDOR'"
                      @close-quicksearch="getTemplateObj"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item label="业务实体">
                    <el-select
                      v-model="form.questTemplateOrgId"
                      :disabled="curRole === 'VENDOR'"
                      multiple
                      @change="selectHandler"
                    >
                      <el-option
                        v-for="item in orgIdList"
                        :key="item.organizationId"
                        :label="item.organizationName"
                        :value="item.organizationId"
                      />
                    </el-select>
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item prop="questName" label="调查表名称">
                    <el-input v-model="form.questName" :disabled="curRole === 'VENDOR'" />
                  </el-form-item>
                </srm-col>
                <!-- 燕豪修改 -->
                <srm-col>
                  <el-form-item label="车型编码">
                    <el-input v-model="form.modelCode" :disabled="curRole === 'VENDOR'" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item label="阶段">
                    <dict-select v-model="form.stage" code="QUESTIONNAIRE_STAGE" :disabled="curRole === 'VENDOR'" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item prop="recoveryTime" label="回收时间">
                    <el-date-picker
                      v-model="form.recoveryTime"
                      :disabled="curRole === 'VENDOR'"
                      formatter="yyyy-MM-dd"
                      type="date"
                      placeholder="选择回收时间"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item prop="rangType" label="范围类型">
                    <dict-select
                      v-model="form.rangType"
                      :disabled="curRole === 'VENDOR'"
                      code="QuestionnaireRangeType"
                      @change="changeRangType"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :init-col="2">
                  <el-form-item label="调查表说明（对供应商可见）">
                    <el-input v-model="form.comment" type="textarea" :disabled="curRole === 'VENDOR'" />
                  </el-form-item>
                </srm-col>
                <srm-col v-if="curRole === 'BUYER'" :init-col="2">
                  <el-form-item label="备注（对采购商可见）">
                    <el-input v-model="form.remarks" type="textarea" />
                  </el-form-item>
                </srm-col>
                <!-- 填写端 -->
                <template v-if="curRole === 'VENDOR' || $attrs.params.flag === 'vendor'">
                  <srm-col>
                    <el-form-item prop="recyclingTime" label="反馈提交时间">
                      <el-date-picker
                        v-model="form.recyclingTime"
                        value-format="yyyy-MM-dd"
                        type="date"
                        :disabled="$attrs.params.flag === 'vendor'"
                        placeholder="选择日期"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item label="填写人">
                      <el-input v-model="form.questFullName" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col :init-col="2">
                    <el-form-item label="驳回原因">
                      <el-input v-model="form.questFeedback" disabled />
                    </el-form-item>
                  </srm-col>
                </template>
              </srm-row>
            </el-form>
          </el-collapse-item>
          <!-- 调查表进度分析 -->
          <el-collapse-item v-if="showProgress" ref="progress" title="进度分析" name="2">
            <div class="btns">
              <el-button type="primary" @click="handleAnalyse">
                分析
              </el-button>
              <ExportExcel
                :white="true"
                pageUrl="/api-ppap/quest/questProgressReport/getProgressDataByQuestInventory"
                :filterParams="filterParams"
                :tableHeader="tableHeaderExport"
                :dictCodes="dictCodes"
                exportMode="front"
              />
            </div>
            <div class="chart">
              <div id="chart-receive" />
              <div id="chart-feedback" />
            </div>
          </el-collapse-item>
          <!-- 调查表基本信息 -->
          <el-collapse-item ref="file" title="附件" name="3">
            <el-button v-if="showFile" type="primary" :disabled="!form.questNoForQuery" @click="fileAdd">
              {{ $t('common.add') }}
            </el-button>
            <el-table :data="fileData" border style="width:100%;margin-top:10px;">
              <el-table-column label="附件上传" align="center">
                <template slot-scope="scope">
                  <SrmCommonFile
                    :default-file="{
                      fileId: scope.row.fileuploadId,
                      fileName: scope.row.fileName
                    }"
                    :extra-data="upFileInfo"
                    :readonly="disabledFlag"
                    @on-change="value => tableHandleUploadSuccess(value, scope.$index)"
                  />
                </template>
              </el-table-column>
              <el-table-column label="附件名称" prop="fileName" align="center" />
              <el-table-column label="上传人" prop="createdBy" align="center" />
              <el-table-column label="操作" align="center" width="100">
                <template slot-scope="scope">
                  <el-button v-if="showFile" type="text" @click="fileRemove(scope.row, scope.$index)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
          <el-collapse-item ref="supplierInfo" title="明细信息" name="4">
            <el-tabs v-model="activeList">
              <el-tab-pane v-if="curRole === 'BUYER' && $attrs.params.flag !== 'vendor'" name="1" label="调查表范围">
                <el-tooltip content="暂存后可导入" :disabled="!!form.questNoForQuery">
                  <MImport
                    v-if="!disabledFlag"
                    style="display: inline-block;margin: 0 10px;"
                    title="导入"
                    :disabled="!form.questNoForQuery"
                    upLoadUrl="/api-ppap/quest/questSupplier/importScopeOfQuestionnaire"
                    :extraData="{ ...companyInfoExtraData, ...companyInfoExtraPostData }"
                    @downloadTemplate="downloadTemplate"
                    @handleSuccess="handleSuccess"
                  />
                </el-tooltip>
                <el-button v-if="form.rangType" type="primary" @click="exportHandle">
                  导出
                </el-button>
                <TableView
                  v-show="form.rangType"
                  ref="companyInfoListTable"
                  style="margin-top:10px;"
                  :frontPaging="true"
                  :table-header="companyInfoListTableHeader"
                />
              </el-tab-pane>
              <el-tab-pane label="调查明细" name="2">
                <!-- 根据模板配置渲染相关组件 -->
                <renderForm
                  ref="renderFormDom"
                  :tableHeader="renderFormHeader"
                  :rangType="form.rangType"
                  :questNo="form.questNoForQuery"
                  :questTemplateId="form.questTemplateId"
                  :disabled="true"
                  :showImport="showImport"
                  :importDisabled="!form.questNoForQuery"
                  optType="questNew"
                  :frontPaging="true"
                  :num="tempNum"
                  @getTempData="getTempData"
                  @after-import="afterImportFrom"
                />
              </el-tab-pane>
              <el-tab-pane v-if="curRole === 'BUYER' && $attrs.params.flag !== 'vendor'" name="3" label="进度跟踪">
                <FormWrapper
                  :formArray="filterConfig"
                  @getFormData="getQuerydata"
                  @synchronous-value="syncFilterParams"
                />
                <el-button v-if="form.questNoForQuery" type="primary" style="margin:10px;" @click="progressExport">
                  导出
                </el-button>
                <TableView
                  ref="progressTrackingTable"
                  :table-header="progressTrackingTableHeader"
                  :preQueryData="queryParams"
                  :frontPaging="true"
                  :cellClass="processCell"
                  url="/api-ppap/quest/questSupplier/getProgressTracking"
                />
              </el-tab-pane>
            </el-tabs>
          </el-collapse-item>
        </el-collapse>
      </div>
      <CToolbar>
        <template #right>
          <el-button size="mini" @click="cancelBill">
            取消
          </el-button>
          <el-button
            v-if="curRole === 'BUYER' && !disabledFlag"
            type="primary"
            :loading="loadingFlag"
            @click="save('DRAW_UP')"
          >
            暂存
          </el-button>
          <el-button
            v-if="curRole === 'BUYER' && !disabledFlag"
            type="primary"
            :loading="loadingFlag"
            @click="save('PUBLISHED')"
          >
            发布
          </el-button>
          <el-button
            v-if="curRole === 'VENDOR' && !disabledFlag && ['TO_BE_RECYCLED', 'REJECT', ''].includes(vendor_recyclingType)"
            type="primary"
            :loading="loadingFlag"
            @click="save('TO_BE_RECYCLED')"
          >
            保存
          </el-button>
          <el-button
            v-if="curRole === 'VENDOR' && !disabledFlag && ['TO_BE_RECYCLED', 'REJECT', ''].includes(vendor_recyclingType)"
            type="primary"
            :loading="loadingFlag"
            @click="save('RECYCLED')"
          >
            提交
          </el-button>
          <el-button
            v-if="curRole === 'VENDOR' && !disabledFlag && vendor_recyclingType === 'RECYCLED'"
            type="primary"
            :loading="loadingFlag"
            @click="vendorRecall"
          >
            撤回申请
          </el-button>

          <template
            v-if="$attrs.params.flag === 'vendor' && $attrs.params.row.recyclingType === 'RECYCLED' && $attrs.params.row.responsibleForHealth === userInfo.nickname"
          >
            <el-button type="primary" size="mini" :loading="loadingFlag" @click="vendorReject">
              驳回
            </el-button>
            <el-button type="primary" size="mini" :loading="loadingFlag" @click="vendorApprove">
              审核
            </el-button>
          </template>
        </template>
      </CToolbar>
    </el-main>
    <!-- 驳回弹窗 -->
    <el-dialog title="驳回" :visible.sync="dialogFormVisible">
      <el-form :model="rejectForm">
        <el-form-item label="驳回原因" :label-width="120" required>
          <el-input v-model="rejectForm.questFeedback" type="textarea" autocomplete="off" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">
          取 消
        </el-button>
        <el-button type="primary" @click="rejectOk">
          确 定
        </el-button>
      </div>
    </el-dialog>
    <!-- 撤回弹窗 -->
    <el-dialog title="撤回" :visible.sync="recallVisible">
      <el-form :model="recallForm">
        <el-form-item label="撤回原因" :label-width="120" required>
          <el-input v-model="recallForm.recallReason" type="textarea" autocomplete="off" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="recallVisible = false">
          取 消
        </el-button>
        <el-button type="primary" @click="recallOk">
          确 定
        </el-button>
      </div>
    </el-dialog>
    <!-- 撤回原因 -->
    <el-dialog title="撤回原因" :visible.sync="recallReasonVisible">
      <el-table border :data="recallData" :max-height="400">
        <el-table-column prop="recallReason" align="center" label="撤回原因" />
        <el-table-column prop="creationDate" align="center" label="撤回时间" width="160" />
      </el-table>
    </el-dialog>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import QuickSearch from 'lib@/components/QuickSearch'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import CToolbar from 'lib@/components/c-toolbar'
import { parseTime } from '@/utils'
import MImport from 'lib@/components/import'
import questManagementVendor from './questManagementVendor'
import renderForm from 'modb@/productionPrepare/views/questTemplate/renderForm.vue'
import { downloadFileLinkByPost } from 'lib@/utils/file'
import ExportExcel from 'lib@/components/export-excel'
import echarts from 'echarts'
import 'echarts/theme/macarons'
import { questManagement } from 'modb@/productionPrepare/api'

export default {
  name: 'QuestManagementDetail',
  components: {
    CToolbar,
    QuickSearch,
    FormWrapper,
    TableView,
    renderForm,
    MImport,
    ExportExcel
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      activeDims: ['1', '2', '3', '4'],
      form: {
        questTemplateType: null,
        questTemplateTypeName: null,
        questTemplateId: null,
        questTemplateName: null,
        questTemplateCode: null,
        questTemplateOrgId: null,
        questTemplateOrgCode: null,
        questTemplateOrgName: null,
        questNo: null,
        questName: null,
        // 燕豪修改
        modelCode: null,
        stage: null,
        recoveryTime: null,
        rangType: null,
        comment: null,
        remarks: null,
        questNoForQuery: null,
        approvalStatus: 'DRAW_UP',
        createdBy: null,
        creationDate: null,
        lastUpdatedBy: null,
        lastUpdateDate: null,
        createdFullName: null,
        lastUpdatedFullName: null,
        deleteFlag: null,
        version: null,
        opType: null,
        companyInfoList: [], // 调查范围
        progressTracking: [], // 进度跟踪
        progressDetails: [], // 调查明细数据
        status: null,
        // 填写端
        recyclingTime: null,
        questFullName: null,
        questFeedback: null
      },
      recallReasonVisible: false,
      recallData: [],
      recallVisible: false,
      recallForm: {
        recallReason: ''
      },
      fileData: [],
      upFileInfo: {
        fileModular: 'sup',
        fileFunction: 'REVIEW_FORM',
        fileType: 'images'
      },
      rejectForm: {
        questFeedback: ''
      },
      tableHeaderExport: [
        {
          prop: 'questNo',
          label: '调查表编号',
          width: 110
        },
        {
          prop: 'questName',
          label: '调查表名称',
          width: 110
        },
        {
          prop: 'modelCode',
          label: '车型编码',
          width: 100
        },
        {
          prop: 'stage',
          label: '阶段',
          width: 80
        },
        {
          prop: 'creationDate',
          label: '创建时间',
          width: 100
        },
        {
          prop: 'publishTime',
          label: '发布时间',
          width: 100
        },
        {
          prop: 'recoveryTime',
          label: '回收时间',
          width: 100
        },
        {
          prop: 'responsibleForHealth',
          label: '生准担当',
          width: 100
        },
        {
          prop: 'companyCode',
          label: '供应商编码',
          width: 120
        },
        {
          prop: 'companyName',
          label: '供应商名称',
          width: 120
        },
        {
          prop: 'vendorRecycleFlag',
          label: '供应商是否已回收',
          width: 100
        },
        {
          prop: 'vendorReplyFlag',
          label: '供应商是否已回复反馈提交时间',
          width: 130
        }
      ],
      dictCodes: {},
      filterParams: {},
      dialogFormVisible: false,
      dialogRow: {},
      queryParams: {},
      renderFormHeader: [],
      filterConfig: [
        {
          prop: 'companyName',
          label: '供应商名称',
          type: 'quicksearch',
          showKey: 'companyName',
          propKey: 'companyName',
          name: 'scc_sup_company_info_display_buyer'
        },
        {
          prop: 'recyclingType',
          label: '回收状态',
          type: 'dict',
          code: 'QUESTIONNAIRE_RECOVERY_STATUS'
        },
        {
          prop: 'responsibleForHealth',
          label: '生准担当名称',
          type: 'quicksearch',
          showKey: 'nickname',
          propKey: 'nickname',
          name: 'scc_rbac_user_display'
        }
      ],
      questTemplateTabArr: [],
      tempNum: 0,
      activeList: '1',
      orgIdList: [], // 模板所属组织ID
      questInfoRules: {
        questTemplateType: [
          { required: true, message: this.$t('vendorMod.pleaseEnter') }
        ],
        questTemplateId: [
          { required: true, message: this.$t('vendorMod.pleaseEnter') }
        ],
        questTemplateOrgId: [
          { required: true, message: this.$t('vendorMod.pleaseEnter') }
        ],
        questName: [
          { required: true, message: this.$t('vendorMod.pleaseEnter') }
        ],
        status: [
          { required: true, message: '请输入阶段' }
        ],
        recoveryTime: [
          { required: true, message: '请选择回收时间' }
        ],
        rangType: [
          { required: true, message: '请选择范围类型' }
        ],
        recyclingTime: [
          { required: true, message: '请选择反馈时间' }
        ]
      },
      curRole: this.$store.getters.userType,
      userInfo: this.$store.getters.user.userInfo,
      loadingFlag: false,
      progressTrackingTableHeader: [
        {
          prop: 'companyCode',
          label: '供应商编码',
          minWidth: 110
        },
        {
          prop: 'companyName',
          label: '供应商名称',
          minWidth: 150
        },
        {
          prop: 'responsibleForHealth',
          label: '生准担当',
          minWidth: 100
        },
        {
          prop: 'recyclingType',
          label: '回收状态', // TO_BE_RECYCLED 待回收 / RECYCLED 已回收 / REJECT 驳回 / REVIEWED 已审核
          minWidth: 100,
          dataType: 'dict',
          code: 'QUESTIONNAIRE_RECOVERY_STATUS'
        },
        {
          prop: 'vendorReplyCode',
          label: '供应商回复单号',
          minWidth: 150,
          showType: 'button',
          btnStyle: 'text',
          callback: row => {
            this.$emit('tab-add', {
              component: questManagementVendor,
              params: {
                flag: 'vendor',
                row: {
                  ...row,
                  questNo: this.form.questNoForQuery
                },
                tabName: 'questManagementVendor' + row.questSupId
              },
              title: '供应商调查表' + row.vendorReplyCode,
              name: 'questManagementVendor' + row.questSupId
            })
          }
        },
        {
          prop: 'recycleTime',
          label: '供应商反馈回收时间',
          minWidth: 160,
          formattor: val => {
            if (val) {
              return /\d{4}-\d{1,2}-\d{1,2}/.exec(val)
            }
          },
          sortMethod: (a, b) => {
            return new Date(a.recycleTime).getTime() - new Date(b.recycleTime).getTime()
          }
        },
        {
          prop: 'reviewer',
          label: '审核人',
          minWidth: 100
        },
        {
          prop: 'recallReason',
          label: '撤回原因',
          minWidth: 120,
          showType: 'button',
          btnStyle: 'text',
          callback: row => {
            this.recallReasonVisible = true
            questManagement.getQuestSupplierOperator({
              questNo: this.form.questNoForQuery,
              companyCode: row.companyCode
            }).then(res => {
              this.recallData = res.data.list || []
            })
          },
          formattor: val => '查看'
        },
        {
          prop: 'operation',
          label: '操作',
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 200,
          buttons: [
            {
              callback: row => {
                this.dialogFormVisible = true
                this.dialogRow = row
              },
              show: row => {
                return row.recyclingType === 'RECYCLED' && row.responsibleForHealth === this.userInfo.nickname
              },
              formattor: () => {
                return '驳回'
              }
            },
            {
              callback: row => {
                this.$confirm('确定审核？', '提示', {
                  type: 'warning'
                }).then(() => {
                  this.updateQuestRecy(row, 'REVIEWED') // 审核
                }).catch(() => { })
              },
              show: row => {
                return row.recyclingType === 'RECYCLED' && row.responsibleForHealth === this.userInfo.nickname
              },
              formattor: () => {
                return '审核'
              }
            },
            {
              callback: row => {
                this.$confirm('确定同意？', '提示', {
                  type: 'warning'
                }).then(() => {
                  this.updateApprovalStatus(row, 'TO_BE_RECYCLED') // 同意
                }).catch(() => { })
              },
              show: row => {
                return row.recyclingType === 'RECALL'
              },
              formattor: () => {
                return '同意撤回'
              }
            },
            {
              callback: row => {
                this.$confirm('确定拒绝？', '提示', {
                  type: 'warning'
                }).then(() => {
                  this.updateApprovalStatus(row, 'RECYCLED') // 拒绝
                }).catch(() => { })
              },
              show: row => {
                return row.recyclingType === 'RECALL'
              },
              formattor: () => {
                return '拒绝撤回'
              }
            }
          ]
        }

      ],
      companyInfoListTableHeader: [
      ],

      companyInfoListTableHeader1: [
        {
          prop: 'toolingCode',
          label: '零件编号',
          minWidth: 150
        },
        {
          prop: 'toolingName',
          label: '零件名称',
          minWidth: 150
        },
        {
          prop: 'companyCode',
          label: '供应商编码',
          minWidth: 150
        },
        {
          prop: 'companyName',
          label: '供应商名称',
          minWidth: 150
        },
        {
          prop: 'nextLoadingPoint',
          label: '下一落点',
          minWidth: 150
        },
        {
          prop: 'requiredCompletionTime',
          label: '要求达成时间',
          minWidth: 150
        }
      ],

      companyInfoListTableHeader2: [
        {
          prop: 'companyCode',
          label: '供应商编码',
          minWidth: 150
        },
        {
          prop: 'companyName',
          label: '供应商名称',
          minWidth: 150
        }
      ],

      // 燕豪修改导入
      companyInfoExtraData: {
        fileModular: 'suplier',
        fileFunction: 'accountAccess',
        fileType: 'excel'
      },
      vendor_recyclingType: '' // 供应商端标识回收状态
    }
  },
  computed: {
    disabledFlag () {
      let { flag } = this.$attrs.params
      return ['view', 'vendor'].includes(flag) || ['CLOSED'].includes(this.form.approvalStatus)
    },
    showProgress () {
      let { flag } = this.$attrs.params
      return this.curRole === 'BUYER' && flag !== 'vendor' && ['PUBLISHED'].includes(this.form.approvalStatus) && this.form.questNoForQuery
    },
    companyInfoExtraPostData () {
      return {
        questNo: this.form.questNoForQuery,
        rangType: this.form.rangType,
        questTemplateId: this.form.questTemplateId
      }
    },
    showFile () {
      return ((this.curRole === 'BUYER' && this.form.approvalStatus === 'DRAW_UP') || (this.curRole === 'VENDOR' && this.form.approvalStatus === 'PUBLISHED')) && ['edit', 'add'].includes(this.$attrs.params.flag)
    },
    showImport () {
      let viewFlag = ['view', 'vendor'].includes(this.$attrs.params.flag)
      let buyerFlag = this.curRole === 'BUYER' && this.form.approvalStatus === 'DRAW_UP'
      let vendorFlag = this.curRole === 'VENDOR' && this.form.approvalStatus === 'PUBLISHED' && ['', 'TO_BE_RECYCLED', 'REJECT'].includes(this.vendor_recyclingType)
      return !viewFlag && (buyerFlag || vendorFlag)
    }
  },
  mounted () {
    let { flag, row } = this.$attrs.params
    if (flag === 'add') {
      this.form.createdFullName = this.userInfo.nickname
    }
    if (this.curRole === 'VENDOR' || flag === 'vendor') this.activeList = '2'
    if (row && row.activeList === '3') this.activeList = '3'
    if (row && row.questNo) {
      this.getSupplierDetail(row, flag)
      this.getFileList(row)
    }
  },
  methods: {
    getSupplierDetail (row, flag) {
      let data = { questNo: row.questNo }
      if (flag === 'vendor') data.vendorReplyCode = row.vendorReplyCode
      questManagement.getDetailByQuestNo(data).then(res => {
        this.form = res.data
        this.filterParams = { questNo: this.form.questNoForQuery }
        this.getTemplateOrg(this.form.questTemplateId)
        for (let key of ['questTemplateOrgCode', 'questTemplateOrgName', 'questTemplateOrgId']) {
          if (this.form[key]) {
            this.form[key] = this.form[key].split(',')
          }
        }
        if (this.curRole === 'VENDOR' && this.$attrs.params.flag === 'edit' && !this.form.questFullName) this.form.questFullName = this.userInfo.nickname
        this.changeRangType(this.form.rangType)
        let { progressTrackingTable, companyInfoListTable, renderFormDom } = this.$refs
        if (progressTrackingTable) {
          this.getQuerydata()
        }
        if (companyInfoListTable) {
          companyInfoListTable.tableData = this.form.companyInfoList
          companyInfoListTable.queryTotal = this.form.companyInfoList.length
        }
        renderFormDom.resData = {}
        for (let item of this.form.progressDetails) {
          renderFormDom.resData[item.questTemplatePropGroupCode] = item.detailData
        }
        if (this.form.progressTracking && this.form.progressTracking.length) {
          this.vendor_recyclingType = this.form.progressTracking[0].recyclingType
        }
      })
    },
    getFileList (row) {
      questManagement.questinventoryfileList({
        questNo: row.questNo
      }).then(res => {
        if (res.code === '0') {
          this.fileData = res.data.list || []
        }
      })
    },
    // 调查模板类型切换
    getQuestTemplateType (val, dictItem) {
      console.log('dictItem', dictItem)
      if (val) {
        this.form.questTemplateTypeName = dictItem.label
      }
    },
    // 选择模板回调
    getTemplateObj (val, data) {
      data.questTemplateId = val ? val.questTemplateId : ''
      this.form.questTemplateName = val.questTemplateName
      this.form.questTemplateCode = val.questTemplateCode
      this.getTemplateOrg(data.questTemplateId)
    },
    // 通过id查询模板分配的组织
    getTemplateOrg (questTemplateId) {
      questManagement.getTemplateOrg({ questTemplateId }).then(res => {
        this.orgIdList = []
        res.data.list.forEach(result => {
          this.orgIdList.push({
            organizationId: result.orgId.toString(),
            organizationCode: result.orgCode,
            organizationName: result.orgName
          })
        })
      }).catch(err => {
        console.log(err)
      })
    },
    selectHandler (val) {
      this.form.questTemplateOrgCode = []
      this.form.questTemplateOrgName = []
      if (val.length) {
        for (let organizationId of val) {
          let node = this.orgIdList.find(v => v.organizationId == organizationId)
          node && this.form.questTemplateOrgCode.push(node.organizationCode)
          node && this.form.questTemplateOrgName.push(node.organizationName)
        }
      }
    },
    // 燕豪修改
    changeRangType (e) {
      if (e === 'VENDOR_AND_PARTS') {
        this.companyInfoListTableHeader = JSON.parse(JSON.stringify(this.companyInfoListTableHeader1))
      } else if (e === 'VENDOR') {
        this.companyInfoListTableHeader = JSON.parse(JSON.stringify(this.companyInfoListTableHeader2))
      }
      this.renderFormHeader = JSON.parse(JSON.stringify(this.companyInfoListTableHeader))
      this.$refs.companyInfoListTable && (this.$refs.companyInfoListTable.tableData = [])
      this.changeColumns()
    },
    changeColumns () {
      let newArr = JSON.parse(JSON.stringify(this.questTemplateTabArr))
      let newTable = JSON.parse(JSON.stringify(this.companyInfoListTableHeader)).reverse()
      newArr.forEach(item => {
        for (let innerItem of newTable) {
          item.questTemplatePropArr.unshift({
            questTemplatePropField: innerItem.prop,
            questTemplatePropFieldDesc: innerItem.label
          })
        }
      })
      this.$refs.renderFormDom.configData = this.$refs.renderFormDom.adaptConfigData(newArr)
      this.tempNum++
    },
    handleAnalyse () {
      questManagement.getProgressDataPic({ questNo: this.form.questNoForQuery }).then(res => {
        if (res.code === '0') {
          let recoveryRate = res.data.recoveryRate || [] // 回收
          let recycleRate = res.data.recycleRate || [] // 反馈
          let recycleCatList = []; let recycleSeriesList = []; let recoveryCatList = []; let recoverySeriesList = []
          for (let item of recycleRate) {
            recycleCatList.push(item.name)
            recycleSeriesList.push(item.rate * 100)
          }
          for (let item of recoveryRate) {
            recoveryCatList.push(item.name)
            recoverySeriesList.push(item.rate * 100)
          }
          this.$nextTick(() => {
            this.initChart({
              title: '回收情况分析图',
              el: 'chart-receive',
              categoryData: recoveryCatList,
              seriesData: recoverySeriesList
            })
            this.initChart({
              title: '反馈情况分析图',
              el: 'chart-feedback',
              categoryData: recycleCatList,
              seriesData: recycleSeriesList
            })
          })
        }
      })
    },
    initChart ({ el, title, categoryData, seriesData }) {
      let myChart = echarts.init(document.getElementById(el)) // ,'macarons'
      let option = {
        title: {
          text: title,
          left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          },
          formatter: '{b}:{c}%'
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'value',
          axisTick: {
            show: false
          },
          axisLine: {
            show: false
          },
          axisLabel: {
            formatter: function (value, index) {
              return value + '%'
            }
          },
          boundaryGap: [0, 0.01]
        },
        yAxis: {
          type: 'category',
          axisTick: {
            show: false
          },
          data: categoryData
        },
        series: [
          {
            type: 'bar',
            color: '#5a9bd5',
            barWidth: '20%',
            data: seriesData
          }
        ]
      }
      myChart.setOption(option)
    },
    fileAdd () {
      this.fileData.push({
        fileuploadId: '',
        fileName: '',
        questInventortFileId: '',
        createdBy: ''
      })
    },
    tableHandleUploadSuccess ({ file }, index) {
      const { fileId = '', fileName = '' } = file || {}
      this.fileData[index].fileuploadId = fileId
      this.fileData[index].fileName = fileName
      this.fileData[index].createdBy = fileId ? this.userInfo.username : ''
      questManagement.questinventoryfileAdd({
        'questNo': this.form.questNoForQuery,
        'fileType': 'images',
        'fileuploadId': fileId,
        'fileName': fileName,
        'comments': this.fileData[index].comments || ''
      }).then(res => {
        if (res.code === '0') {
          this.fileData[index].questInventortFileId = res.data.questInventortFileId
        }
      })
    },
    fileRemove (row, index) {
      if (!row.questInventortFileId) {
        this.fileData.splice(index, 1)
        return
      }
      questManagement.questinventoryfileDelete({ questInventortFileId: row.questInventortFileId }).then(res => {
        if (res.code === '0') {
          this.fileData.splice(index, 1)
          this.$message.success('删除成功')
        }
      })
    },
    // 模板下载
    downloadTemplate () {
      let params = {
        rangType: this.form.rangType
      }
      downloadFileLinkByPost(
        '/api-ppap/quest/questSupplier/getRangeModel',
        '调查表范围导入模板.xlsx',
        params,
      ).catch(() => {
        this.$message.error('下载失败')
      })
    },
    handleSuccess (result) {
      this.$refs.companyInfoListTable.tableData = result.data
      this.$refs.companyInfoListTable.queryTotal = result.data.length
      let questNo = this.form.questNoForQuery
      if (!questNo) return
      questManagement.getDetailByQuestNo({ questNo }).then(res => {
        let { renderFormDom } = this.$refs
        let progressDetails = res.data.progressDetails || {}
        renderFormDom.resData = {}
        for (let item of progressDetails) {
          renderFormDom.resData[item.questTemplatePropGroupCode] = item.detailData
        }
      })
    },
    exportHandle () {
      const params = {
        rangType: this.form.rangType,
        companyInfoList: this.$refs.companyInfoListTable.tableData
      }
      downloadFileLinkByPost('/api-ppap/quest/questSupplier/sendRangeData', `调查范围导出${parseTime(new Date())}.xlsx`, params)
    },
    getTempData (data) {
      this.questTemplateTabArr = data
    },
    // 导入后触发的事件
    afterImportFrom () {
      let { flag, row } = this.$attrs.params
      this.getSupplierDetail(row, flag)
    },
    getQuerydata (v) {
      this.queryParams = v || this.queryParams
      this.queryParams.questNo = this.form.questNoForQuery
      this.$nextTick(() => {
        this.$refs.progressTrackingTable.query()
      })
    },
    syncFilterParams (values) {
      this.queryParams = values
    },
    progressExport () {
      if (!this.$refs.progressTrackingTable) return
      downloadFileLinkByPost(
        '/api-ppap/quest/questSupplier/exportExcelProgress',
        '调查表进度跟踪导出.xlsx',
        {
          progressTracking: this.$refs.progressTrackingTable.tableData
        }
      ).catch(() => {
        this.$message.error('下载失败')
      })
    },
    processCell ({ row, column }) {
      let recycleTime = row.recycleTime
      let recoveryTime = this.form.recoveryTime
      if (recycleTime && recoveryTime && new Date(recycleTime).getTime() > new Date(recoveryTime).getTime()) {
        if (column.property === 'recycleTime') {
          return 'red-color'
        }
      }
    },
    updateApprovalStatus (row, type) {
      let data = {
        questSupId: row.questSupId,
        approvalStatus: 'PUBLISHED',
        RecyclingType: type
      }
      questManagement.updateQuestSupplierApprovalStatus(data).then(res => {
        if (res.code === '0') {
          this.recallVisible = false
          this.$message.success('操作成功')
          this.cancelBill()
        }
      })
    },

    // 撤回
    vendorRecall () {
      this.recallForm.recallReason = ''
      this.recallVisible = true
    },
    // 撤回弹窗-sure
    recallOk () {
      if (!this.recallForm.recallReason) {
        this.$message.warning('请填写撤回原因！')
        return
      }
      this.recallVisible = false
      this.saveDataHandle('RECALL')
    },
    // 驳回
    vendorReject () {
      this.dialogFormVisible = true
      this.dialogRow = this.$attrs.params.row
    },
    // 审核
    vendorApprove () {
      this.$confirm('确定审核？', '提示', {
        type: 'warning'
      }).then(() => {
        this.updateQuestRecy(this.$attrs.params.row, 'REVIEWED') // 审核
      }).catch(() => { })
    },
    // 驳回弹窗-sure
    rejectOk () {
      if (!this.rejectForm.questFeedback) {
        this.$message.warning('请输入驳回原因')
        return
      }
      this.updateQuestRecy(this.dialogRow, 'REJECT')
    },
    updateQuestRecy (row, type) {
      let data = {
        questSupId: row.questSupId,
        questFeedback: this.rejectForm.questFeedback || '',
        approvalStatus: 'PUBLISHED',
        RecyclingType: type
      }
      questManagement.updateQuestSupplierApprovalStatus(data).then(res => {
        if (res.code === '0') {
          this.dialogFormVisible = false
          let message = type === 'REJECT' ? '驳回成功' : '审核成功'
          this.$message.success(message)
          this.cancelBill()
        }
      })
    },

    // 暂存、发布、保存、提交
    async save (type) {
      let flag = true
      if (['PUBLISHED', 'RECYCLED'].includes(type)) {
        flag = await this.validateForm()
      }
      if (flag) {
        this.saveDataHandle(type)
      }
    },
    async validateForm () {
      const flag = await this.validate()
      if (!flag) {
        this.__jump_error__(
          'questInfo',
          null,
          this.$t('vendorMod.pleasefinishRequired')
        )
        return false
      } else {
        return true
      }
    },
    validate () {
      return new Promise(resolve => {
        this.$refs.questInfoForm.validate(valid => {
          resolve(valid)
        })
      })
    },
    initParams () {
      let params = JSON.parse(JSON.stringify(this.form)) // 表单信息
      params.companyInfoList = this.$refs.companyInfoListTable ? this.$refs.companyInfoListTable.tableData : [] // 调查表范围
      let progressDetails = []
      for (let key in this.$refs.renderFormDom.resData) {
        for (let item of this.questTemplateTabArr) {
          if (item.questTemplatePropGroupCode === key) {
            progressDetails.push({
              questTemplatePropGroupId: item.questTemplatePropGroupId,
              detailData: this.$refs.renderFormDom.resData[key]
            })
          }
        }
      }
      params.progressDetails = progressDetails
      params.progressTracking = this.$refs.progressTrackingTable ? this.$refs.progressTrackingTable.tableData : []
      for (let key of ['questTemplateOrgName', 'questTemplateOrgCode', 'questTemplateOrgId']) {
        params[key] = (params[key] && Array.isArray(params[key])) ? params[key].join() : ''
      }
      return params
    },
    // 保存数据操作
    saveDataHandle (type) {
      let submitData = this.initParams()
      submitData.opType = type
      if (['DRAW_UP', 'PUBLISHED'].includes(type)) submitData.approvalStatus = type
      if (type === 'RECYCLED') { // 供应商提交
        for (let item of submitData.progressDetails) {
          if (!item.detailData || !item.detailData.length) {
            return this.__jump_error__(
              'supplierInfo',
              null,
              '调查明细页签不能为空'
            )
          }
          let propArr = this.questTemplateTabArr.find(j => j.questTemplatePropGroupId === item.questTemplatePropGroupId)
          let questTemplatePropArr = propArr ? propArr.questTemplatePropArr : []
          for (let innerItem of item.detailData) {
            for (let keys of questTemplatePropArr) {
              if (keys.enabledFlag === 'Y' && keys.emptyFlag === 'Y' && !innerItem[keys.questTemplatePropField]) {
                return this.__jump_error__(
                  'supplierInfo',
                  null,
                  '调查明细有带*号必填信息未填写，请填写后再提交！'
                )
              }
            }
          }
        }
      }
      if (type === 'RECALL') submitData.recallReason = this.recallForm.recallReason
      this.loadingFlag = true
      return new Promise(resolve => {
        questManagement.saveOrUpdateQuestSupplierForm(submitData).then(res => {
          resolve(true)
          if (['TO_BE_RECYCLED', 'DRAW_UP', 'RECYCLED'].includes(type)) {
            this.form.questNo = this.form.questNoForQuery = res.data.data.questNoForQuery
            this.loadingFlag = false
            type === 'RECYCLED' ? this.$message.success('提交成功') : this.$message.success('保存成功')
            let { flag, row } = this.$attrs.params
            this.getSupplierDetail(row, flag)
          } else {
            this.$message.success(this.$t('common.successSubmit'))
            this.cancelBill()
          }
        })
          .catch(err => {
            resolve(false)
            this.loadingFlag = false
            console.log(err)
          })
      })
    },
    cancelBill () {
      const { tabName } = this.$attrs.params
      this.$emit('tab-remove', tabName)
      this.__setTabTodo('questManagementList.getQuerydata.queryByParams')
    }
  }
}
</script>
<style scoped lang="scss">
.questManagementDetail {
  height: 100%;

  .el-main {
    padding-bottom: 50px;
  }

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
}

.chart {
  margin-top: 10px;
  display: flex;

  div {
    width: 600px;
    height: 400px;
  }
}
</style>
<style lang="scss">
.red-color {
  span {
    color: red !important;
  }
}
</style>
