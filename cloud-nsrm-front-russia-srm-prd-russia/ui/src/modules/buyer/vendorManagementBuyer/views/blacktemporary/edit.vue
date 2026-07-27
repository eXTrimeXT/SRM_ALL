<template>
  <el-container
    class="blacktemporaryEdit"
    direction="vertical"
  >
    <el-main>
      <CWorkflowMulti
        ref="workflowMulti"
        v-model="activeTabName"
        :fun-params="workflowParamsInfo"
        :button-config-info="buttonConfigInfo"
        @tab-click="workflowView"
        @workflow-handler="workflowHandler"
        @click-handler="(type) => dataHandle(type)"
        @submit-direct="(type) => dataHandle(type)"
        @confirm="(type, comment) => dataHandle(type, comment)"
        @close-tab="back"
      >
        <div class="form-container">
          <el-form
            ref="form"
            :model="form"
            :rules="rules"
          >
            <srm-row>
              <srm-col :init-col="4">
                <!-- 供应商名称 -->
                <el-form-item
                  :label="$t('common.vendorName')"
                  prop="vendorId"
                >
                  <QuickSearch
                    :show-input="form.companyName"
                    show-key="companyName"
                    :scope-data="form"
                    name="scc_base_black_company"
                    @close-quicksearch="getCompanyObj"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="4">
                <el-form-item
                  prop="blackType"
                  :label="$t('black.blackType')"
                  :rules="{
                    required: true,
                    message: $t('vendorMod.msgVendorId'),
                    trigger: 'blur'
                  }"
                >
                  <DictSelect
                    v-model="form.blackType"
                    code="BLACK_TYPE"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="4">
                <el-form-item
                  prop="blackCode"
                  :label="this.$t('black.blacklistApprovalNumber')"
                >
                  <el-input
                    v-model="form.blackTemporaryCode"
                    disabled
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
            <srm-row>
              <srm-col :init-col="4">
                <el-form-item
                  prop="createdBy"
                  :label="this.$t('common.creator')"
                >
                  <el-input
                    v-model="form.createdBy"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="4">
                <el-form-item
                  prop="creationDate"
                  :label="this.$t('common.creationTime')"
                >
                  <el-input
                    v-model="form.creationDate"
                    disabled
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
        </div>
        <el-collapse v-model="activeLine">
          <el-collapse-item
            :title="$t('black.riskControl')"
            name="1"
          >
            <el-form
              ref="risk_form"
              :model="form"
              :rules="rules"
              class="form-fill-style"
              :disabled="curOpt === 'view'"
            >
              <srm-row>
                <srm-col :init-col="1">
                  <el-checkbox-group
                    v-model="riskControlType"
                    :disabled="!isEdit"
                  >
                    <el-checkbox
                      v-for="(item, index) in riskControl"
                      :key="index"
                      :label="item.value"
                      :value="item.value"
                      @change="checked => handleCheckedRisk(checked, item.value)"
                    >
                      {{ item.label }}
                    </el-checkbox>
                  </el-checkbox-group>
                </srm-col>
                <srm-col
                  :init-col="2"
                  style="margin-top: 10px"
                >
                  <el-form-item :label="$t('black.temporaryTransitionTime')">
                    <el-radio
                      v-model="form.excessiveTime"
                      :disabled="!isEdit"
                      :value="30"
                      :label="30"
                    >
                      {{ $t('vendorMod.day30') }}
                    </el-radio>
                    <el-radio
                      v-model="form.excessiveTime"
                      :disabled="!isEdit"
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
          <el-collapse-item
            :title="$t('vendorMod.attachment')"
            name="2"
          >
            <FileDynamic
              ref="sceneAttachment"
              v-model="fileRecords"
              scene-module-code="SCENE_BLACK_TEMP_ATTACHMENT"
              :business-id="form.blackTemporaryId"
              :editable="curOpt === 'add' || curOpt === 'edit'"
            />
          </el-collapse-item>
        </el-collapse>
        <template slot="buttonOne">
          <el-button
            v-if="isEdit"
            type="primary"
            :disabled="readOnly"
            @click="save"
          >
            {{ $t('common.staging') }}
          </el-button>
        </template>
      </CWorkflowMulti>
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoMixin } from '@/utils/mixins'
import QuickSearch from 'lib@/components/QuickSearch'
import FileDynamic from '@/library/components/c-file-management/file-dynamic'
import WorkflowCommon from '@/library/mixins/workflow-common'
import { blackTemporaryApi } from 'modb@/vendorManagementBuyer/api/black'

export default {
  name: 'BlackEdit',

  components: {
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
      isFlow: true,
      isEdit: true,
      queryForm: {},
      fileRecords: [],
      curOpt: '',
      isSrmCompany: false,
      activeLine: ['1', '2', '3'],
      riskControl: [], // 风险控制
      dialogVisible: false,
      riskControlType: [],
      extraPostData: {},
      extraData: {
        sourceType: 'WEB_APP',
        uploadType: 'DEF',
        fileModular: 'base',
        fileFunction: 'quotalinetest',
        fileType: 'excel'
      },
      form: {
        blackTemporaryCode: null,
        blackId: null,
        blackCode: null,
        blackType: null,
        companyId: null,
        companyCode: null,
        companyName: null,
        socialCreditCode: null,
        blackTemporaryDescription: null,
        isAllowSourcing: 'N',
        isAllowCreateOrder: 'N',
        isAllowWarehousing: 'N',
        isAllowFinance: 'N',
        isAllowPayment: 'N',
        excessiveTime: 30,
        approveStatus: null
      },
      rules: {},
      readOnly: false
    }
  },

  computed: {
    viewUpdateButton () { // 用来控制保存、提交按钮是否可见。如果自定义按钮则无需添加
      let bol = false
      if ((this.curOpt === 'add' ||
        this.form.approveStatus === 'DRAFT' ||
        this.form.approveStatus === 'REJECTED' ||
        this.form.approveStatus === 'WITHDRAW') && this.isEdit) {
        bol = true
      }
      return bol
    },
    workflowBusinessId () { // 用来指定工作流的业务ID
      return this.form.blackTemporaryId ? this.form.blackTemporaryId : null
    },
    // 展示工作流tab页
    workflowTabDisabled () {
      return this.openWorkFlow
    }
  },

  watch: {
    // 监听保存提交 按钮变更状态，如果自定义按钮则无需添加
    viewUpdateButton () {
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
    }
  },

  created () {
    this.buttonConfigInfo.save.view = false
    this.buttonConfigInfo.submit.view = this.viewUpdateButton
    this.buttonConfigInfo.submit.name = this.$t('common.submit')
    this.buttonConfigInfo.cancel.view = false
    this.buttonConfigInfo.close.view = false
  },

  mounted () {
    const { flag, readOnly = false } = this.$attrs.params
    this.curOpt = flag
    this.readOnly = readOnly
    if (flag === 'edit' || flag === 'view' || flag === 'doApproval') {
      this.getDetail()
    }
    if (flag === 'add') {
      this.$nextTick(() => {
        this.$refs.sceneAttachment.loadFileInfo()
      })
    }
  },

  methods: {
    back () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('BlacktemporaryList.getQuerydata')
    },
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'BlackTemporary'
    },
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    async getWorkflowBusinessVariables () { // 定义流程变量，如果没有可以不添加
      return {

      }
    },
    getDetail () {
      blackTemporaryApi.getById(this.$attrs.params.row.blackTemporaryId).then(res => {
        const { fileUploads, ...rest } = res.data
        this.form = rest
        if ((res.data.approveStatus == 'SUBMITTED' || res.data.approveStatus == 'APPROVED' || res.data.approveStatus == 'REJECTED') && this.curOpt != 'view') {
          this.openWorkFlow = false
        } else {
          this.openWorkFlow = true
        }
        this.fileRecords = fileUploads
        if (this.form.isAllowSourcing == 'Y') {
          this.riskControlType.push('isAllowSourcing')
        }
        if (this.form.isAllowCreateOrder == 'Y') {
          this.riskControlType.push('isAllowCreateOrder')
        }
        if (this.form.isAllowWarehousing == 'Y') {
          this.riskControlType.push('isAllowWarehousing')
        }
        if (this.form.isAllowFinance == 'Y') {
          this.riskControlType.push('isAllowFinance')
        }
        if (this.form.isAllowPayment == 'Y') {
          this.riskControlType.push('isAllowPayment')
        }
        if (this.form.approveStatus && this.form.approveStatus != 'DRAFT') {
          this.isEdit = false
          this.isFlow = false
        }
        if (this.form.approveStatus == 'REJECTED') {
          this.isEdit = true
          this.isFlow = true
          if (this.curOpt == 'edit') {
            this.openWorkFlow = true
            let _this = this
            setTimeout(function () {
              _this.openWorkFlow = false
            }, 1000)
          }
        }
        this.extraData.blackTemporaryId = this.form.blackTemporaryId
        this.$nextTick(() => {
          this.$refs.sceneAttachment.loadFileInfo()
        })
      })
    },
    // 选择供应商回调
    getCompanyObj (val, scope) {
      if (val) {
        if (val.companyId) {
          scope.companyId = val.companyId
        }
        if (val.companyCode) {
          scope.companyCode = val.companyCode
        }
        scope.companyName = val.companyName
        scope.socialCreditCode = val.socialCreditCode
        scope.blackType = val.blackType
        scope.blackId = val.blackId
        scope.blackCode = val.blackCode
      }
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
    outerButtonClick (index) {
      this.bankRowIndex = index
    },
    outerHandleUploadSuccess (file) {
      const { id, name, fileSize, fileExtendType, creationDate } = file
      this.fileRecords[this.bankRowIndex].fileuploadId = id.toString()
      this.fileRecords[this.bankRowIndex].attachName = name
      this.fileRecords[this.bankRowIndex].fileSize = fileSize
      this.fileRecords[this.bankRowIndex].creationDate = creationDate
      this.fileRecords[this.bankRowIndex].attachType = fileExtendType
    },
    beforeUpload () {
      this.extraPostData.blackTemporaryId = this.form.blackTemporaryId
    },

    save () {
      this.$refs.form.validate(valid => {
        if (valid) {
          const data = {
            ...this.form,
            fileUploads: this.fileRecords
          }
          blackTemporaryApi.addOrUpdate(data).then(res => {
            this.$message({
              type: 'success',
              message: res.message
            })
            this.$emit('tab-remove', this.$attrs.tabName)
            this.__setTabTodo('BlacktemporaryList.getQuerydata')
          })
        } else {
          return false
        }
      })
    },

    cancelBill () {
      const { flag, row } = this.$attrs.params
      if (flag === 'add') {
        this.$emit('tab-remove', 'blacktemporaryEdit')
      } else {
        this.$emit('tab-remove', 'blacktemporaryEdit' + row.blackTemporaryId)
      }
      this.__setTabTodo('BlacktemporaryList.getQuerydata')
    },

    dataHandle (type) {
      this.$refs.form.validate(valid => {
        if (valid) {
          const data = {
            ...this.form,
            fileUploads: this.fileRecords
          }
          blackTemporaryApi.addOrUpdate(data).then(res => {
            this.$message({
              type: 'success',
              message: res.message
            })
            let id = res.data
            blackTemporaryApi.getById(id).then(async res => {
              const { blackTemporaryAttaches, ...rest } = res.data
              this.form = rest
              this.fileRecords = blackTemporaryAttaches
              if (this.form.isAllowSourcing == 'Y') {
                this.riskControlType.push('isAllowSourcing')
              }
              if (this.form.isAllowCreateOrder == 'Y') {
                this.riskControlType.push('isAllowCreateOrder')
              }
              if (this.form.isAllowWarehousing == 'Y') {
                this.riskControlType.push('isAllowWarehousing')
              }
              if (this.form.isAllowFinance == 'Y') {
                this.riskControlType.push('isAllowFinance')
              }
              if (this.form.isAllowPayment == 'Y') {
                this.riskControlType.push('isAllowPayment')
              }
              this.extraData.blackTemporaryId = this.form.blackTemporaryId
              this.isFlow = false
              this.$attrs.params.row.blackTemporaryId = id
              await this.getDetail()
              await this.handlerAfter(type)
              // 切换到工作流tab页
              let workflowMode = ['Product', 'Iframe', 'Self'].includes(this.workflowParamsInfo.integrationMode)
              if (workflowMode) {
                this.activeTabName = 'workflowTab'
              }
              let _this = this
              setTimeout(function () {
                _this.openWorkFlow = false
                _this.isEdit = false
              }, 1000)
            })
          })
        } else {
          return false
        }
      })
    },

    // 工作流tab点击
    tabClick (data) {
      let name = data.name
      let businessId = this.form.blackTemporaryId
      if (name == 'tabFlow') {
        // 判断业务单据是否保存
        if (businessId == null || businessId == 0) {
          this.$message.error(this.$t('vendorMod.msgSaveOrgData')) // 请选择保存业务数据！
          return
        } // 初始化工作流 //业务数据，用于分支，预留分支字段Field01到Field05
        let businessVariables = {
          formNo: this.form.blackCode
        }

        this.flowParams = {
          businessType: 'BlackTemporary', // 单据类型
          businessId: businessId, // 业务ID
          businessVariables: businessVariables, // 业务数据，用于分支
          integrationMode: this.integrationModeFlow,
          version: new Date() // 用于改变数据触发刷新工作流
        }
      }
    }
  }
}
</script>

<style scoped lang="scss">
.blackEdit {
  height: 100%;
  padding-bottom: 50px;
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
}
:deep(.el-radio__label),
:deep(.el-radio) {
  font-size: 12px;
}
</style>
