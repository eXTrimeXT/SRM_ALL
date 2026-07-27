<template>
  <el-container
    class="flex-container blackEdit"
    direction="vertical"
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
        <div class="form-container1">
          <el-collapse v-model="activeLine">
            <el-collapse-item :title="$t('vendorMod.receiptInfo')" name="4">
              <el-form ref="blackForm" :model="form" :rules="rules">
                <srm-row>
                  <srm-col>
                    <el-form-item
                      prop="blackType"
                      :label="$t('black.blackType')"
                      :rules="{
                        required: true,
                        message: $t('black.msgBlackType'),
                        trigger: 'blur',
                      }"
                    >
                      <DictSelect v-model="form.blackType" code="BLACK_TYPE" :disabled="readOnly" />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item prop="blackCode" :label="this.$t('black.blacklistApprovalNumber')">
                      <el-input v-model="form.blackCode" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item prop="createdBy" :label="this.$t('common.creator')">
                      <el-input v-model="form.createdBy" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item prop="creationDate" :label="this.$t('common.creationTime')">
                      <el-input v-model="form.creationDate" disabled />
                    </el-form-item>
                  </srm-col>
                </srm-row>
              </el-form>
            </el-collapse-item>
            <el-collapse-item :title="this.$t('black.blacklistSupplierDetails')" name="1">
              <div>
                <div style="padding: 12px 0">
                  <el-button
                    v-if="!readOnly"
                    class="detail-pbtn"
                    type="primary"
                    @click="addCompany('system')"
                  >
                    {{ $t("black.SRMSystemSupplierImport") }}
                  </el-button>
                  <el-button v-if="!readOnly" class="detail-pbtn" type="primary" @click="addCompany">
                    {{ $t("black.NSRMSystemSupplierImport") }}
                  </el-button>
                  <el-tooltip
                    :content="$t('purchaseDemand.itemInfoTooltip')"
                    placement="top"
                    effect="dark"
                  >
                    <MImport
                      v-if="!readOnly"
                      btn-class-name="detail-pbtn"
                      :title="$t('common.import')"
                      up-load-url="/api-sup/sup/black/importBlackCompanyExcel"
                      :extra-data="extraData"
                      :extra-post-data="extraPostData"
                      @beforeUpload="beforeUpload"
                      @downloadTemplate="downloadTemplate"
                      @handleSuccess="handleSuccess"
                    />
                  </el-tooltip>
                </div>
                <el-table
                  ref="realDataSource"
                  :data="realDataSource"
                  style="width: 100%"
                  border
                  use-virtual
                  :row-height="38"
                  max-height="390px"
                  highlight-current-row
                  @asyncGetRealDataSource="asyncGetRealDataSource"
                >
                  <el-table-column
                    align="center"
                    type="index"
                    :label="$t('contractMod.tabindex')"
                  />
                  <el-table-column
                    align="center"
                    prop="companyName"
                    :label="$t('common.vendorName')"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="socialCreditCode"
                    :label="$t('vendorMod.lcCode')"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="companyCreationDate"
                    :label="$t('vendorMod.establishDate')"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="companyName"
                    :label="$t('vendorMod.legalPerson')"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="registeredCapital"
                    :label="$t('vendorMod.registeredCapital')"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="companyCountry"
                    :label="$t('components.address.country')"
                    :show-overflow-tooltip="true"
                  >
                    <template slot-scope="scope">
                      <DictSelect
                        v-model="scope.row.companyCountry"
                        filterable
                        disabled
                        code="country"
                      />
                    </template>
                  </el-table-column>
                  <el-table-column
                    align="center"
                    prop="companyProvince"
                    :label="$t('vendorMod.province')"
                    :show-overflow-tooltip="true"
                  >
                    <template slot-scope="scope">
                      <DictSelect
                        v-model="scope.row.companyProvince"
                        code="PROVINCE"
                        custom-select-type="PROVINCE"
                        disabled
                      />
                    </template>
                  </el-table-column>
                  <el-table-column
                    align="center"
                    prop="companyCity"
                    :label="$t('vendorMod.city')"
                    :show-overflow-tooltip="true"
                  >
                    <template slot-scope="scope">
                      <DictSelect
                        v-model="scope.row.companyCity"
                        :code="scope.row.companyProvince"
                        custom-select-type="CITY"
                        disabled
                      />
                    </template>
                  </el-table-column>
                  <el-table-column
                    align="center"
                    prop="companyType"
                    :label="$t('vendorMod.companyType')"
                    :show-overflow-tooltip="true"
                  >
                    <template slot-scope="scope">
                      <DictSelect
                        v-model="scope.row.companyType"
                        code="COMPANY_NATURE"
                        disabled
                      />
                    </template>
                  </el-table-column>
                  <el-table-column
                    align="center"
                    :label="$t('components.headers.operation')"
                    :show-overflow-tooltip="true"
                  >
                    <template slot-scope="scope">
                      <el-button
                        :disabled="readOnly"
                        type="text"
                        @click="deleteItem(scope.$index, scope.row)"
                      >
                        {{ $t("common.delete") }}
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
              <!-- 供应商选择 -->
              <srm-dialog
                :title="$t('black.impoblackCompanyAddrt')"
                size="large"
                :visible.sync="dialogVisible"
                :close-on-click-modal="false"
              >
                <el-form
                  ref="queryForm"
                  class="list-page-query"
                  :model="companyInfos.configForm"
                >
                  <srm-Row>
                    <srm-col :initCol="3">
                      <el-form-item
                        :label="$t('common.vendorName')"
                        prop="companyName"
                        :rules="{
                          required: true,
                          message: $t('vendorMod.msgVendorId'),
                          trigger: 'blur',
                        }"
                      >
                        <QuickSearch
                          v-if="isSrmCompany"
                          :show-input="companyInfos.configForm.companyName"
                          show-key="companyName"
                          :scope-data="companyInfos.configForm"
                          name="scc_sup_company_info2"
                          @close-quicksearch="getCompanyObj"
                        />
                        <el-input v-else v-model="companyInfos.configForm.companyName" />
                      </el-form-item>
                    </srm-col>
                    <srm-col :initCol="3">
                      <el-form-item
                        :label="$t('vendorMod.socialCreditCode')"
                        prop="socialCreditCode"
                        :rules="{
                          required: !isSrmCompany,
                          message: $t('vendorMod.msgLcCode'),
                          trigger: 'blur',
                        }"
                      >
                        <el-input
                          v-model="companyInfos.configForm.socialCreditCode"
                          :disabled="isSrmCompany"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :initCol="3">
                      <el-form-item
                        :label="$t('bidMod.companyCreationDate')"
                        prop="companyCreationDate"
                        :rules="{
                          required: !isSrmCompany,
                          message: $t('vendorMod.msgCreationDate'),
                          trigger: 'blur',
                        }"
                      >
                        <el-date-picker
                          v-model="companyInfos.configForm.companyCreationDate"
                          type="date"
                          :placeholder="$t('common.pleaseSelectDate')"
                          :disabled="isSrmCompany"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :initCol="3">
                      <el-form-item
                        :label="$t('vendorMod.corporateRepresentative')"
                        prop="legalPerson"
                        :rules="{
                          required: !isSrmCompany,
                          message: $t('vendorMod.msgLegalPerson'),
                          trigger: 'blur',
                        }"
                      >
                        <el-input
                          v-model="companyInfos.configForm.legalPerson"
                          :disabled="isSrmCompany"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :initCol="3">
                      <el-form-item
                        :label="$t('vendorMod.registeredCapital')"
                        prop="registeredCapital"
                        :rules="{
                          required: !isSrmCompany,
                          message: $t('vendorMod.msgRegisteredCapital'),
                          trigger: 'blur',
                        }"
                      >
                        <el-input
                          v-model="companyInfos.configForm.registeredCapital"
                          :disabled="isSrmCompany"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :initCol="3">
                      <el-form-item
                        prop="companyCountry"
                        :label="$t('vendorMod.businessAddr')"
                        :rules="{
                          required: !isSrmCompany,
                          message: $t('vendorMod.msgBusinessAddr'),
                          trigger: 'blur',
                        }"
                      >
                        <DictSelect
                          v-model="companyInfos.configForm.companyCountry"
                          code="country"
                          :disabled="isSrmCompany"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :initCol="3">
                      <!-- 省份/州 -->
                      <el-form-item
                        prop="companyProvince"
                        :label="$t('vendorMod.province')"
                        :rules="{
                          required: companyInfos.configForm.companyCountry === 'CN' && !isSrmCompany,
                          message: $t('vendorMod.msgpProvince'),
                          trigger: 'blur',
                        }"
                      >
                        <DictSelect
                          v-model="companyInfos.configForm.companyProvince"
                          code="PROVINCE"
                          custom-select-type="PROVINCE"
                          :disabled="companyInfos.configForm.companyCountry !== 'CN' || isSrmCompany"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :initCol="3">
                      <!-- 城市 -->
                      <el-form-item
                        prop="companyCity"
                        :label="$t('vendorMod.city')"
                        :rules="{
                          required: companyInfos.configForm.companyCountry === 'CN' && !isSrmCompany,
                          message: $t('vendorMod.msgCity'),
                          trigger: 'blur',
                        }"
                      >
                        <DictSelect
                          v-model="companyInfos.configForm.companyCity"
                          :code="companyInfos.configForm.companyProvince"
                          custom-select-type="CITY"
                          :disabled="companyInfos.configForm.companyCountry !== 'CN' || isSrmCompany"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :initCol="3">
                      <el-form-item
                        :label="$t('vendorMod.companyType')"
                        prop="companyType"
                        :rules="{
                          required: !isSrmCompany,
                          message: $t('vendorMod.msgCompanyType'),
                          trigger: 'blur',
                        }"
                      >
                        <DictSelect
                          v-model="companyInfos.configForm.companyType"
                          code="COMPANY_NATURE"
                          :disabled="isSrmCompany"
                        />
                      </el-form-item>
                    </srm-col>
                  </srm-row>
                </el-form>
                <div slot="footer" class="dialog-footer">
                  <el-button @click="dialogVisible = false">
                    {{ $t("common.cancel") }}
                  </el-button>
                  <el-button type="primary" @click="addCompanyOneItem">
                    {{ $t("common.confirm") }}
                  </el-button>
                </div>
              </srm-dialog>
            </el-collapse-item>
            <el-collapse-item :title="$t('black.riskControl')" name="2">
              <el-form ref="risk_form" class="form-fill-style" :disabled="curOpt === 'view'">
                <srm-row>
                  <srm-col
                    :xs="24"
                    :sm="24"
                    :md="24"
                    :lg="24"
                    :xl="24"
                  >
                    <el-checkbox-group v-model="riskControlType" :disabled="readOnly">
                      <el-checkbox
                        v-for="(item, index) in riskControl"
                        :key="index"
                        :label="item.value"
                        :value="item.value"
                        @change="
                          (checked) => handleCheckedRisk(checked, item.value)
                        "
                      >
                        {{ item.label }}
                      </el-checkbox>
                    </el-checkbox-group>
                  </srm-col>
                  <srm-col
                    :xs="24"
                    :sm="24"
                    :md="12"
                    :lg="12"
                    :xl="12"
                    style="margin-top: 10px"
                  >
                    <el-form-item :label="$t('black.isImmediately')">
                      <el-checkbox
                        v-model="form.isImmediately"
                        :disabled="readOnly"
                        true-label="Y"
                        false-label="N"
                      >
                        {{ $t('black.immediate') }}
                      </el-checkbox>
                      <el-radio
                        v-if="form.isImmediately != 'Y'"
                        v-model="form.excessiveTime"
                        :disabled="readOnly"
                        :value="30"
                        :label="30"
                      >
                        {{ $t('vendorMod.day30') }}
                      </el-radio>
                      <el-radio
                        v-if="form.isImmediately != 'Y'"
                        v-model="form.excessiveTime"
                        :disabled="readOnly"
                        :value="60"
                        :label="60"
                      >
                        {{ $t('vendorMod.day60') }}
                      </el-radio>
                    </el-form-item>
                  </srm-col>
                </srm-row>
              </el-form>
            </el-collapse-item>
            <el-collapse-item :title="$t('vendorMod.attachment')" name="3">
              <FileDynamic
                ref="sceneAttachment"
                v-model="fileRecords"
                scene-module-code="SCENE_BLACK_ATTACHMENT"
                :business-id="form.blackId"
                :editable="!readOnly"
              />
            </el-collapse-item>
          </el-collapse>
        </div>
      </CWorkflowMulti>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import QuickSearch from 'lib@/components/QuickSearch'
import WorkflowCommon from '@/library/mixins/workflow-common'
import FileDynamic from '@/library/components/c-file-management/file-dynamic'
import { blackApi } from 'modb@/vendorManagementBuyer/api/black'

export default {
  name: 'BlackEdit',
  components: {
    MImport,
    QuickSearch,
    FileDynamic
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      quaActiveInfo: 'tab1',
      // 文件上传配置信息
      openWorkFlow: true, // 审批流程相关参数
      integrationModeFlow: '', // 工作流集成模式
      flowParams: {}, // 流程参数
      queryForm: {},
      fileRecords: [],
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
      form: {
        blackId: null,
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
        approveStatus: 'DRAFT'
      },
      rules: {},
      readOnly: false,
      blackId: '',
      orderData: null,
      flowBusinessId: ''
    }
  },
  computed: {
    // 用来控制保存、提交按钮是否可见。如果自定义按钮则无需添加
    viewUpdateButton () {
      return ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(this.form.approveStatus) &&
              !this.readOnly
    },
    disabledUpdateButton () {
      return (['SUBMITTED', 'APPROVING'].includes(this.form.approveStatus) ||
        this.readOnly
      )
    },
    // 用来指定工作流的业务ID 单据ID 注意每一个单的ID不一样
    workflowBusinessId () {
      return this.flowBusinessId ? this.flowBusinessId : (this.form.blackId || null)
    },
    // 禁用流程tab状态
    workflowTabDisabled () {
      return (!this.form.blackId && (['DRAFT', 'REJECTED', 'WITHDRAW'].includes(this.form.approveStatus) || this.readOnly)) ||
      !this.form.blackId
    }
  },
  watch: {
    // 监听保存提交 按钮变更状态，如果自定义按钮则无需添加
    viewUpdateButton () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
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
      this.blackId = this.$attrs.params.row.blackId
      // 查询是否有多单流程ID
      // const { data } = await this.$api.base.flowAPI.getFlowMainId(this.blackId)
      // if (data) {
      //   this.flowBusinessId = data
      // } else {
      // this.flowBusinessId = this.blackId
      // }
      this.flowBusinessId = this.blackId
      this.getDetail()
    }
    if (flag === 'add') {
      this.$nextTick(() => {
        this.$refs.sceneAttachment.loadFileInfo()
      })
    }
    // 以下内容可控制取消、关闭、保存、提交是否显示。如果自定义按钮，则无需添加
    // this.buttonConfigInfo.save.code = 'XXX' // 保存按钮权限
    // this.buttonConfigInfo.submit.code = 'XXX' // 提交按钮权限
    this.buttonConfigInfo.save.view = this.viewUpdateButton
    this.buttonConfigInfo.submit.view = this.viewUpdateButton
    this.buttonConfigInfo.cancel.view = this.readOnly
    this.buttonConfigInfo.close.view = !this.readOnly
  },
  mounted () {},
  methods: {
    getDetail () {
      blackApi.getById(this.blackId)
        .then((res) => {
          const { blackCompanyList, blackAttachList, fileUploads, ...rest } = res.data
          this.orderData = res.data // 所有单据信息
          this.form = rest
          this.realDataSource = blackCompanyList
          this.fileRecords = fileUploads
          if (this.form.isAllowSourcing === 'Y') {
            this.riskControlType.push('isAllowSourcing')
          }
          if (this.form.isAllowCreateOrder === 'Y') {
            this.riskControlType.push('isAllowCreateOrder')
          }
          if (this.form.isAllowWarehousing === 'Y') {
            this.riskControlType.push('isAllowWarehousing')
          }
          if (this.form.isAllowFinance === 'Y') {
            this.riskControlType.push('isAllowFinance')
          }
          if (this.form.isAllowPayment === 'Y') {
            this.riskControlType.push('isAllowPayment')
          }
          this.extraData.blackId = this.form.blackId
          this.$refs.sceneAttachment.loadFileInfo()
        })
    },
    // 选择供应商回调
    getCompanyObj (val, scope) {
      scope.companyId = val ? val.companyId : null
      scope.companyCode = val ? val.companyCode : ''
      scope.companyName = val ? val.companyName : ''
      scope.companyType = val ? val.companyType : ''
      scope.socialCreditCode = val ? val.lcCode : ''
      scope.legalPerson = val ? val.legalPerson : ''
      scope.registeredCapital = val ? val.registeredCapital : ''
      scope.companyCountry = val ? val.companyCountry : ''
      scope.companyProvince = val ? val.companyProvince : ''
      scope.companyCity = val ? val.companyCity : ''
      scope.companyCreationDate = val ? val.companyCreationDate : ''
    },
    addCompanyOneItem () {
      this.$refs.queryForm.validate((valid) => {
        var distinct = false
        if (valid) {
          this.realDataSource.forEach((val, index) => {
            if (
              val.socialCreditCode ===
              this.companyInfos.configForm.socialCreditCode
            ) {
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
          blackApi.checkSubmitData(checkData).then((res) => {
            var companyCreationDate
            if (this.companyInfos.configForm.companyId) {
              companyCreationDate = this.companyInfos.configForm.companyCreationDate
            } else {
              companyCreationDate = this.$dayjs(this.companyInfos.configForm.companyCreationDate).format('YYYY-MM-DD')
            }
            this.realDataSource.push({
              companyId: this.companyInfos.configForm.companyId,
              companyName: this.companyInfos.configForm.companyName,
              companyCode: this.companyInfos.configForm.companyCode,
              companyType: this.companyInfos.configForm.companyType,
              socialCreditCode: this.companyInfos.configForm.socialCreditCode,
              legalPerson: this.companyInfos.configForm.legalPerson,
              registeredCapital:
                this.companyInfos.configForm.registeredCapital,
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
      downloadFileLink(
        '/api-sup/sup/black/exportBlackCompanyExcelTemplate',
        this.$t('black.blackImportTemplate')
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },
    addUploadOne () {
      this.fileRecords.push({
        fileuploadId: null,
        attachName: '',
        fileSize: null,
        attachId: null,
        attachType: null
      })
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
      this.fileRecords.splice(index, 1)
    },
    // 移除
    outerHandleRemove (fileuploadId) { },
    outerButtonClick (index) {
      this.bankRowIndex = index
    },
    handleScriptProgress (percent) { },
    outerHandleUploadSuccess (file) {
      const { id, name, fileSize, fileExtendType, creationDate } = file
      this.fileRecords[this.bankRowIndex].fileuploadId = id.toString()
      this.fileRecords[this.bankRowIndex].attachName = name
      this.fileRecords[this.bankRowIndex].fileSize = fileSize
      this.fileRecords[this.bankRowIndex].creationDate = creationDate
      this.fileRecords[this.bankRowIndex].attachType = fileExtendType
    },
    beforeUpload () {
      this.extraPostData.blackId = this.form.blackId
    },
    handleSuccess (value) {
      let resData = value.data || []
      resData.forEach(item => {
        this.realDataSource.push({
          ...item
        })
      })
      // this.getDetail()
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
      return 'black' // 调试产品
      // return 'Pe54bb9d5a08c45dea1a6a8194be96634' // 调试ide
    },
    // 获取ref值
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    // 定义流程额外变量，如果没有就不用添加这个函数
    async getWorkflowBusinessVariables () {
      return { formNo: this.form.blackCode } // 调试产品
      // return { test1: { checkCount: 2 } } // 调试ide
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
    async saveBill (type, comment) { // 对于CWorkflowMulti中调用的saveBill方法
      this.$refs.blackForm.validate(valid => {
        if (valid) {
          if (this.realDataSource.length === 0) {
            this.__jump_error__(
              'realDataSource',
              null,
              this.$t('vendorMod.msgVendor')
            )
            return false
          }
          const data = {
            ...this.form,
            blackCompanyList: this.realDataSource,
            fileUploads: this.fileRecords
          }
          blackApi.addOrUpdate(data).then(async res => {
            if (type == 'SAVE') {
              this.$emit('tab-remove', this.$attrs.params.tabName)
              this.__setTabTodo('BlackList.getQuerydata')
            }
            this.form.blackId = res.data
            this.blackId = res.data
            await this.getDetail()
            await this.handlerAfter(type)
            this.readOnly = true
          })
        } else {
          this.$message({
            type: 'warning',
            message: this.$t('vendorMod.warningRequire')
          })
          return false
        }
      })
    },
    back () {
      this.$emit('tab-remove', this.$attrs.tabName)
      this.__setTabTodo('BlackList.getQuerydata')
    }
  }
}
</script>
<style scoped lang="scss">
.blackEdit {
  height: 100%;
  // padding-bottom: 50px;
  :deep(.table-wrapper) {
    padding-left: 0;
    padding-right: 0;
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
  .form-container1{
    // margin-bottom: 16px;
  }
}
</style>
