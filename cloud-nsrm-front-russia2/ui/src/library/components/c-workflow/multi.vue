<template>
  <div
    v-if="workflow"
    ref="workflowTabs"
    :class="['order-and-flow-tab level3Tab workflowTabsWrap', {'flow-open-mode': workflowMode}]"
    style="position: relative;width: 100%"
  >
<!--    {{showWorkflow}}-->
<!--    {{workflowMode}}-->
<!--    {{!workflowParamsInfo.tabDisabled}}-->
<!--    {{workflowParamsInfo.businessId}}-->
<!--    !workflowParamsInfo.tabDisabled-->
<!--    {{showWorkflowNew}}-->
<!--    {{buttonList.length > 0}}-->
    <WorkflowTopBottom
      ref="workflowTopBottom"
      style="width: 100%"
      v-if="showTopBtn && ((showWorkflowNew && buttonList.length > 0) || (showWorkflow && workflowMode && workflowParamsInfo.businessId))"
      :buttonList="buttonList"
      :showUnpass="showUnpass"
      :businessType="workflowParamsInfo.businessType"
      :businessId="workflowParamsInfo.businessId"
      :metadataList="metadataList"
      :myInitData="myInitData"
      @clickButtom="clickButtom"
      @goBack="goBack"
    />
    <div v-if="showSlot" :class="['slot']" style="position: relative">
      <slot />
    </div>
    <el-collapse class="workflow-server" v-model="flowSctiveLine" v-if="showFlowHistory || (showWorkflowNew || (showWorkflow && workflowMode && workflowParamsInfo.businessId))">
      <el-collapse-item :title="$t('components.flownode.approvalInfo')" name="flowHis">
        <el-tabs v-model="activeName">
          <el-tab-pane v-if="showWorkflowNew || (showWorkflow && workflowMode && workflowParamsInfo.businessId)" :label="$t('flowMod.approvers')" name="first">
            <WorkflowReportSelf
              ref="targetWorkflow"
              v-if="showWorkflowNew || (showWorkflow && workflowMode && workflowParamsInfo.businessId)"
              need-init
              :is-nested="isNested"
              :fun-params="workflowParamsInfo"
              :isShowButton="isShowButton('start')"
              @afterProcessAction="multiAfterProcessActionHandel"
              @changeMetadataList="changeMetadataList"
            />
          </el-tab-pane>
          <el-tab-pane :label="$t('components.flownode.history')" name="second" v-if="showFlowHistory">
            <FlowHistory
              v-if="showFlowHistory && workflowMode"
              ref="flowHistory"
              :businessType="workflowParamsInfo.businessType"
              :businessId="workflowParamsInfo.businessId"
              @updateFlowData="updateFlowData"
              @returnAssgines="returnAssgines"
            />
          </el-tab-pane>
        </el-tabs>
      </el-collapse-item>
    </el-collapse>

    <!-- 底部按钮区域 -->
    <CToolbar>
      <template slot="right">
        <slot name="buttonOne" />
        <el-button
          v-if="buttonConfigInfo.cancel.view && flowType=='orderForm'"
          @click="cancel"
        >
          {{ buttonConfigInfo.cancel.name }}
        </el-button>
        <el-button
          v-if="buttonConfigInfo.close.view"
          @click="close"
        >
          {{ buttonConfigInfo.close.name }}
        </el-button>
        <slot name="buttonTwo" />
        <CWorkflowButton
          v-if="(buttonConfigInfo.save.view || buttomMustShow) && flowType=='orderForm' && hasPermission(buttonConfigInfo.save.code)"
          ref="workflowButtonSAVE"
          :disabled="buttonConfigInfo.save.disabled"
          :button-name="buttonConfigInfo.save.name"
          :integration-mode="workflowParamsInfo.integrationMode"
          @click-handler="clickHandler('SAVE')"
          @submit-direct="submitDirect('SAVE')"
          @confirm="comment => confirm('SAVE', comment)"
          @workflow-handler="workflowHandler('SAVE')"
          @workflow-cancel="flowCancelHandler('SAVE')"
          @close-tab="close"
        />
        <slot name="buttonThree" />
        <CWorkflowButton
          v-if="(buttonConfigInfo.submit.view || buttomMustShow) && flowType=='orderForm' && hasPermission(buttonConfigInfo.submit.code)"
          ref="workflowButtonSUBMIT"
          type="primary"
          :disabled="buttonConfigInfo.submit.disabled"
          :button-name="buttonConfigInfo.submit.name"
          :integration-mode="workflowParamsInfo.integrationMode"
          @click-handler="clickHandler('SUBMIT')"
          @submit-direct="submitDirect('SUBMIT')"
          @confirm="comment => confirm('SUBMIT', comment)"
          @workflow-handler="workflowHandler('SUBMIT')"
          @workflow-cancel="flowCancelHandler('SUBMIT')"
          @close-tab="close"
        />
        <slot name="buttonFour" />
        <div
          v-for="(item, itemKey) in buttonCustom"
          :key="itemKey"
        >
          <CWorkflowButton
            v-if="item.view && flowType=='orderForm' && hasPermission(item.code)"
            :ref="'workflowButton' + itemKey"
            :disabled="item.disabled"
            :button-name="item.name"
            :type="item.type || 'default'"
            :integration-mode="workflowParamsInfo.integrationMode"
            @click-handler="clickHandler(itemKey)"
            @submit-direct="submitDirect(itemKey)"
            @confirm="comment => confirm(itemKey, comment)"
            @workflow-handler="workflowHandler(itemKey)"
            @close-tab="close"
          />
        </div>
        <slot name="buttonFive" />
      </template>
    </CToolbar>
    <!--弹窗-->
    <srm-dialog
      :title="dialogTitle"
      :visible.sync="dialogFormVisible"
      :close-on-click-modal="false"
      size="middle"
    >
      <el-form
        ref="orgform"
        label-position="top"
        v-model="form"
      >
        <srm-row>
          <srm-col v-if="buttonType == 'overrule'" :init-col="1">
            <!-- 驳回至 -->
            <el-form-item
              :label="$t('cusEntry.supplement20250121.rejectedTo')"
              prop="overruleToValue"
            >
              <el-select v-model="form.overruleToValue" :placeholder="$t('common.pleaseSelect')">
                <el-option
                  v-for="item in overruleOptions"
                  :key="item.taskKey"
                  :label="item.taskName"
                  :value="item.taskKey">
                </el-option>
              </el-select>
            </el-form-item>
          </srm-col>
          <srm-col v-if="buttonType == 'transfer'" :init-col="1">
            <!-- 转办至 -->
            <el-form-item
              :label="$t('cusEntry.supplement20250121.transferTo')"
              prop="categoryCode"
            >
              <PeopleSelector
                v-model="popupPersonnel"
                :flag="true"
                :searchData="{taskKey: flowInfomation?.taskKey,formDataId: workflowParamsInfo?.businessId, actionCode: workflowParamsInfo?.businessType}"
                @on-confirm="selectedData => personnelChange(selectedData)"
              />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="1">
            <!-- 描述 -->
            <el-form-item
              :label="$t('dataConfMod.description')"
              prop="categoryCode"
            >
              <el-input
                type="textarea"
                :rows="5"
                :placeholder="$t('common.pleaseTypeContents')"
                v-model="comments">
              </el-input>
            </el-form-item>
          </srm-col>
          <srm-col :init-col="1" v-if="buttonType != 'recall'">
            <!-- 附件 -->
            <el-form-item
              :label="$t('bidMod.attachment')"
              prop="categoryCode"
            >
<!--              actionUrl="/mflow/be/api/v4/approval-process/task/file/upload"-->
<!--              :default-file="{-->
<!--              fileId: attachs[0]?.filePath,-->
<!--              fileName: attachs[0]?.fileName-->
<!--              }"-->
              <SrmCommonFile
                :readonly="false"
                :extra-data="fileInfo"
                multiple
                :fileListFlag="true"
                limit="20"
                @on-change="file => uploadSuccess(file)"
              />
              <!-- <span>请添加50M以内大小的附件（大小根据后端限制修改）</span> -->
            </el-form-item>
          </srm-col>
        </srm-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogFormVisible = false">
          <!-- 取 消 -->
          {{ $t('common.cancel') }}
        </el-button>
        <el-button
          type="primary"
          @click="dialogClick"
        >
          {{ $t('common.confirm') }}
        </el-button>
      </template>
    </srm-dialog>
  </div>
  <div v-else>
    <slot />
  </div>
</template>

<script>
import { mapGetters, mapState } from 'vuex'
import { toTreeArray } from 'xe-utils'
import WorkflowReport from './workflowReport'
import WorkflowReportSelf from './workflowSelf'
import CToolbar from 'lib@/components/c-toolbar'
import CWorkflowButton from 'lib@/components/c-workflow-button'
import FlowHistory from './flowHistory'
import i18n from '@/lang'
import WorkflowTopBottom from 'lib@/components/c-workflow/workflowSelf/components/workflowTopBottom.vue'
import QuickSearch from 'lib@/components/QuickSearch.vue'
import PeopleSelector from 'lib@/components/c-workflow/workflowSelf/components/peopleSelector/index.vue'

export default {
  name: 'CWorkflow',
  components: {
    PeopleSelector,
    QuickSearch,
    CToolbar,
    WorkflowReport,
    WorkflowReportSelf,
    CWorkflowButton,
    FlowHistory,
    WorkflowTopBottom
  },
  model: {
    event: 'change',
    value: 'value'
  },
  props: {
    isSrmSelf: { // 是否启用srm自定义审批流
      type: Boolean,
      default: () => {
        return false
      }
    },
    value: {
      type: String,
      default: () => {
        return 'bizTab'
      }
    },
    viewType: {
      type: String,
      default: () => {
        return 'WORKFLOW' // 取值  WORKFLOW(含有工作流，默认)/SINGLE (仅有原业务)
      }
    },
    isNested: {
      type: Boolean,
      default: () => {
        return true
      }
    },
    showFlowHistory: {
      type: Boolean,
      default: () => {
        return true
      }
    },
    tabConfigInfo: {
      type: Object,
      default: () => {
        return {
          biz: {
            label: i18n.t('vendorMod.receiptInfo'), // 单据信息
            name: 'bizTab'
          },
          workflow: {
            label: i18n.t('bidMod.processApproval'), // 流程审批
            name: 'workflowTab'
          }
        }
      }
    },
    buttonConfigInfo: {
      type: Object,
      default: () => {
        return {
          cancel: {
            name: i18n.t('common.cancel'), // 取消
            view: true,
            disabled: false
          },
          close: {
            name: i18n.t('common.close'), // 关闭
            view: true,
            disabled: false
          },
          save: {
            name: i18n.t('flowMod.temporaryView'), // 暂存
            view: false,
            disabled: false,
            code: ''
          },
          submit: {
            name: i18n.t('bidMod.submitapprovlaFlowing'), // 提交
            view: false,
            disabled: false,
            code: ''
          }
        }
      }
    },
    // 是否显示审批流相关按钮
    showTopBtn: {
      type: Boolean,
      default: () => {
        return true
      }
    },
    // 是否显示不通过按钮，实际是调用通过接口，只是传入参数不同，不影响审批流进度（eg: 供应商库）
    showUnpass: {
      type: Boolean,
      default: () => {
        return false
      }
    },
    buttonCustom: {
      type: Object,
      default: () => {
        /**
         * type 比如为[SUBMIT]  限制：不能定义type类型：
         * 格式 {`type`: {
         *      name: '提交',
         *      view: true,
         *      disabled: false
         * }}
         */
        return {}
      }
    },
    funParams: {
      type: Object,
      default: function () {
        return {}
      }
    },
    // 流程审批类型 flowType=='orderForm'
    flowType: {
      type: String,
      default: () => {
        return 'orderForm' // orderForm 单据详情 listForm 列表多单类型
      }
    },
    // 审批通过前置
    beforeApprove: {
      type: Function,
      default: (data, type) => {
        return true
      }
    }
  },
  data () {
    return {
      workflowParamsInfo: {
        businessType: '',
        businessId: null,
        businessVariables: {},
        integrationMode: null,
        tabDisabled: false,
        version: new Date(),
        workflowActive: false,
      },
      fileInfo: {
        fileModular: 'flow',
        fileFunction: 'approveFile'
      },
      rules: {
        overruleToValue: [{ required: true, message: this.$t('common.pleaseInput') }], // 请输入
      },
      flowSctiveLine: ['flowHis'],
      activeName: 'first',
      attachs: [], // 附件上传
      dialogTitle: null,
      popupPersonnel: [],
      form: {
        overruleToValue: null,
      },
      overruleOptions: [],
      dialogFormVisible: false,
      buttonShow: false,
      buttonList: [],
      buttonType: '',
      metadataList: [],
      showWorkflow: false,
      showWorkflowNew: false,
      buttomMustShow: false,
      showSlot: true,
      comments: '',
      workflowMode: false, // 是否需要显示审批流信息
      myInitData: {},
      flowInfomation: {},
      approveFormData: {}
    }
  },
  computed: {
    innerValue: {
      get () {
        return this.value
      },
      set (v) {
        this.$emit('change', v)
      }
    },
    userType () {
      return this.$store.getters.userInfo.userType
    },
    workflow () {
      return this.viewType === 'WORKFLOW'
    },
    ...mapState({
      visitedViews: (state) => state.tagsView.visitedViews
    })
  },
  watch: {
    funParams: {
      handler (data) {
        this.freshParam()
        this.$nextTick(() => {
          this.configList()
        })
      },
      deep: true
    },
    value () {
      if (!this.showWorkflow) {
        this.showWorkflow = this.isWorkflowTab()
      }
    }
  },
  async mounted () {
    this.value = this.tabConfigInfo.biz.name
    if (!this.showWorkflow) {
      this.showWorkflow = this.isWorkflowTab()
    }
    this.freshParam()
  },
  created () {
  },
  activated () {
    if (this.funParams.businessId) {
      this.$emit('workflow-handler', 'update')
    }
  },
  methods: {
    // 返回Assgines后判断是否显示历史信息
    returnAssgines (assgineeList) {
      if (!assgineeList || assgineeList.length <= 0) {
        this.showFlowHistory = false
      }
    },
    // 上传成功后的方法
    uploadSuccess ({ fileList }) {
      let attr = []
      fileList.forEach(item => {
        const obj = {fileName: item.fileName, filePath: item.fileId}
        attr.push(obj)
      })
      this.attachs = attr
    },
    changeMetadataList (data) {
      console.log(data, 'metadataList')
      this.metadataList = data
    },
    updateFlowData (data) {
      this.flowInfomation = data
      this.$emit('updateFlowData', data)
    },
    goBack () {
      this.showWorkflowNew = false
      this.showWorkflow = false
      this.showSlot = true
      this.buttomMustShow = true
      this.activeName = 'second'
      this.$emit('submitGoBack')
    },
    personnelChange (data) {
      this.popupPersonnel = data
    },
    // 点击弹窗确定按钮
    dialogClick () {
      let url = null
      // 如果是有子节点的人员选择需要平铺到父节点
      const metadataListOld = this.$refs.targetWorkflow?.metadataList
      let metadataListNew = []
      metadataListOld.forEach((item) => {
        if (item.childTasks.length > 0) { // 有子节点
          item.childTasks.forEach((task) => {
            metadataListNew.push(task)
          })
        } else {
          metadataListNew.push(item)
        }
      })
      let obj = {
        "actionCode": this.workflowParamsInfo?.businessType,
        "formDataId": this.workflowParamsInfo?.businessId, // 业务单据ID(如果不传递，则是默认流程配置数据)
        "approveNodes": metadataListNew,
        "attachs": this.attachs,
        "businessExtData": "",
        "businessKey": "",
        "comment": this.comments,
        "formData": JSON.stringify(this.workflowParamsInfo?.businessVariables),
        "processInstanceId": "",
        "templateCode": "",
        "tenantId": "",
        "title": this.myInitData.title,
        "userId": ""
      }
      if (this.buttonType === 'start') { // 发起审批流
        url = '/api-base/flow/event/v2/instance/start'
        // obj.comment = '通过'
      } else if (this.buttonType === 'staging') { // 暂存
        url = '/api-base/flow/event/v2/instance/hold'
      } else if (this.buttonType == 'recall') { // 撤回
        url = '/api-base/flow/event/v2/task/recall'
      } else if (this.buttonType == 'discard') { // 作废
        url = '/api-base/flow/event/v2/instance/discard'
      } else if (this.buttonType == 'overrule') { // 驳回
        url = '/api-base/flow/event/v2/task/overrule'
        obj = {
          "actionCode": this.workflowParamsInfo?.businessType,
          "formDataId": this.workflowParamsInfo?.businessId, // 业务单据ID(如果不传递，则是默认流程配置数据)
          "approveNodes": this.$refs.targetWorkflow?.metadataList,
          "attachs": this.attachs,
          "businessExtData": "",
          "businessKey": "",
          "comment": this.comments,
          "formData": "{}",
          "processInstanceId": "",
          "templateCode": "",
          "tenantId": "",
          "title": this.myInitData.title,
          "userId": "",
          "targetTaskKey": this.form.overruleToValue
        }
      } else if (['approve', 'approveNo'].includes(this.buttonType)) { // 通过、不通过
        let btnRes = this.buttonType == 'approve' ? this.$t('components.approvalHead.headers.pass') : this.$t('cusEntry.supplement20250211.unPass')
        url = '/api-base/flow/event/v2/task/approve'
        obj = {
          "actionCode": this.workflowParamsInfo?.businessType,
          "formDataId": this.workflowParamsInfo?.businessId,
          "attachs": this.attachs,
          "comment": `${btnRes}${this.comments ? (', ' + this.comments) : ''}`
        }
      } else if (this.buttonType == 'transfer') { // 转单
        url = '/api-base/flow/event/v2/task/transfer'
        obj = {
          "actionCode": this.workflowParamsInfo?.businessType,
          "formDataId": this.workflowParamsInfo?.businessId, // 业务单据ID(如果不传递，则是默认流程配置数据)
          "attachs": this.attachs,
          "businessExtData": "",
          "businessKey": "",
          "comment": this.comments,
          "formData": "{}",
          "processInstanceId": "",
          "templateCode": "",
          "tenantId": "",
          "targetUserId": this.popupPersonnel?.[0]?.assigneeId,
          "title": this.myInitData.title,
          "userId": ""
        }
      }
      this.$http({
        url,
        method: 'POST',
        loading: true,
        data: obj
      }).then(response => {
        if (response.code === '0')
        this.$message.success(response.message)
        this.cancel()
      })
    },
    isShowButton (type) {
      if (this.buttonList?.length) {
        return this.buttonList.some(item => type == item.type)
      } else {
        return false
      }
    },
    // 发起审批的按钮
    async clickButtom (type) {
      this.buttonType = type
      if (type == 'start') {
        this.dialogTitle = this.$t('common.submit')
        const metadataListOld = this.$refs.targetWorkflow?.metadataList
        let bol = true
        metadataListOld.forEach((item) => {
          if (item.childTasks.length > 0) {
            item.childTasks.forEach((task) => {
              if (task.required == true && task.assignees.length <= 0) {
                bol = false
              }
            })
          } else {
            if (item.required == true && item.assignees.length <= 0) {
              bol = false
            }
          }
        })
        if (bol) { // 判断是否所有审批人都有选择
          this.dialogFormVisible = true
        } else {
          this.$message.error(this.$t('components.approvalHead.tips.selectApprovalPerson'))
        }
      } else if (type == 'staging') { // 暂存
        this.dialogClick()
      } else if (type == 'overrule') { // 驳回
        this.dialogTitle = this.$t('common.toRefuse')
        this.dialogFormVisible = true
        this.$http({
          url: '/api-base/flow/event/v2/task/overrule-nodes/list',
          method: 'POST',
          data: {
            "actionCode": this.workflowParamsInfo?.businessType,
            "formDataId": this.workflowParamsInfo?.businessId, // 业务单据ID(如果不传递，则是默认流程配置数据)
          }
        }).then(({ data }) => {
          this.overruleOptions = data
          this.form.overruleToValue = this.overruleOptions[0]?.taskKey
          if (!data.length) {
            this.$message.warning(this.$t('cusEntry.library.noRrejectNodeData')); // 暂无驳回节点数据
          }
        })
      } else if (type == 'discard') { // 作废
        this.dialogClick()
      } else if (type == 'transfer') {
        this.dialogTitle = this.$t('common.transferOrder')
        this.dialogFormVisible = true
      } else if (['approve', 'approveNo'].includes(type)) {
        let res = await this.beforeApprove(this.flowInfomation, type)
        if (!res) return
        if (res instanceof Object) {
          this.approveFormData = res
        }
        this.dialogTitle = type == 'approve' ? this.$t('components.approvalHead.headers.pass') : this.$t('cusEntry.supplement20250211.unPass')
        this.dialogFormVisible = true
      } else if (type == 'recall') { // 撤回
        this.dialogTitle = this.$t('common.recall')
        this.dialogFormVisible = true
      }
    },
    // 返回显示按钮的权限
    configList () {
      if (this.workflowParamsInfo.integrationMode !== 'IdeFlow') {
        return false
      }
      if(!this.workflowParamsInfo.businessId) {
        return false
      }
      this.$http({
        url: '/api-base/ext/flow/event/v2/task/config/list',
        method: 'POST',
        data: {
          "actionCode": this.workflowParamsInfo?.businessType,
          "formDataId": this.workflowParamsInfo?.businessId, // 业务单据ID(如果不传递，则是默认流程配置数据)
        }
      }).then(({ data }) => {
        this.buttonList = data?.buttons
        this.showWorkflowNew = !this.isShowButton('start')
        if (!(this.showWorkflowNew || (this.showWorkflow && this.workflowMode && this.workflowParamsInfo.businessId))) {
          this.activeName = 'second'
        }
      })
      // title详情
      let obj = {
        "actionCode": this.workflowParamsInfo?.businessType,
        "formDataId": this.workflowParamsInfo?.businessId, // 业务单据ID(如果不传递，则是默认流程配置数据)
        "approveNodes": this.$refs.targetWorkflow?.metadataList,
        "attachs": this.attachs,
        "businessExtData": "",
        "businessKey": "",
        "comment": this.comments,
        "formData": JSON.stringify(this.workflowParamsInfo?.businessVariables),
        "processInstanceId": "",
        "templateCode": "",
        "tenantId": "",
        "title": this.myInitData.title,
        "userId": ""
      }
      if (this.workflowParamsInfo?.businessId) {
        this.$http({
          url: '/api-base/flow/event/v2/instance/detailInfo',
          method: 'POST',
          data: obj
        }).then(({ data }) => {
          this.myInitData = data
        })
      }
    },
    // 根据code判断权限
    hasPermission (code) {
      if (!code) {
        return true
      }
      const userInfo = this.$store.getters.user.userInfo
      const { buttonPermission = [] } = userInfo
      if (buttonPermission[code]) {
        return buttonPermission[code] == 'Y'
      } else {
        return true
      }
    },
    // viewType 的作用不一样，是用于处在动态切换 workflow 状态时的 UED 需求，不是用于控制是否显示工作流
    // 虽然也可以用 viewType 切换，但是如果下级一颗很大的树，会导致请求回来的状态做关闭/开关的处理的话，会重复销毁重建下级
    // 所以这里用样式来控制以达到 UED 需求
    toggleTabHeaderShow () {
      if (!this.workflow) {
        return
      }

      const el = this.$refs.workflowTabs?.$el
      if (el) {
        const headerEl = el.querySelector('.el-tabs__header')

        if (headerEl) {
          headerEl.style.display = this.workflowMode ? 'block' : 'none'
        }
      }
    },
    freshParam () {
      this.workflowParamsInfo.businessType = this.funParams.businessType
      this.fileInfo.fileFunction = this.funParams.businessType
      this.workflowParamsInfo.businessId = this.funParams.businessId
      this.workflowParamsInfo.businessVariables = this.funParams.businessVariables
      this.workflowParamsInfo.integrationMode = this.funParams.integrationMode
      // this.workflowParamsInfo.integrationMode = 'Self'
      this.workflowParamsInfo.tabDisabled = this.funParams.tabDisabled
      this.workflowParamsInfo.workflowActive = this.funParams.workflowActive

      this.workflowParamsInfo.version = new Date()
      // 显示tab 模式
      this.workflowMode = this.flowWithTabMode.includes(this.workflowParamsInfo.integrationMode)

      this.toggleTabHeaderShow()
    },
    tabClick () {
      this.$emit('input', this.value)
      // this.$emit('change', this.value)
      this.$emit('tab-click', this.isWorkflowTab())
    },
    // 取消
    cancel () {
      if (['flowTaskView', 'approvalFormDetailsInner'].includes(this.$route.name)) {
        this.tabRemoveHandle(this.$route.fullPath)
      } else {
        this.$emit('close-tab')
      }
    },
    // 关闭
    close () {
      if (['flowTaskView', 'approvalFormDetailsInner'].includes(this.$route.name)) {
        this.tabRemoveHandle(this.$route.fullPath)
      } else {
        this.$emit('close-tab')
      }
    },
    clickHandler (operationType) {
      this.$emit('click-handler', operationType)
    },
    submitDirect (operationType) {
      this.$emit('submit-direct', operationType)
    },
    confirm (operationType, comment) {
      this.$emit('confirm', operationType, comment)
    },
    workflowHandler (operationType) {
      this.$emit('workflow-handler', operationType)
    },
    // 流程取消提交
    flowCancelHandler (type) {
      // 取消提交 按钮不置灰
      this.buttonConfigInfo.save.disabled = false
      this.buttonConfigInfo.submit.disabled = false
    },
    isWorkflowTab () {
      console.log(this.value === this.tabConfigInfo.workflow.name, 'isWorkflowTab')
      console.log(this.value, 'this.value')
      console.log(this.tabConfigInfo.workflow.name, 'name')
      return this.value === this.tabConfigInfo.workflow.name
    },
    handlerAfter (operationType, flowData = {}, callback = null) {
      var buttonRefs = this.$refs['workflowButton' + operationType]
      if (!buttonRefs) {
        this.$message({
          message: this.$t('flowMod.incorrectButtonConf'), // 按钮配置有误
          type: 'error'
        })
      }
      var buttonElement = buttonRefs
      if (buttonRefs instanceof Array) {
        buttonElement = buttonRefs[0]
      }
      // 执行按钮的 handlerAfter 事件
      buttonElement.handlerAfter(flowData, callback) // this.workflowParamsInfo
      // 如果模式没有开启中间内容不作隐藏
      if (this.workflowParamsInfo.integrationMode !== 'IdeFlow') {
        return false
      }
      this.showSlot = false
      this.showWorkflow = true
      this.buttomMustShow = false
      setTimeout(() => {
        this.$set(this.buttonConfigInfo.submit, 'view', false)
        this.$set(this.buttonConfigInfo.save, 'view', false)
        this.activeName = 'first'
      }, 500)
    },
    multiAfterProcessActionHandel (data) {
      if (this.$refs.flowHistory) {
        this.$refs.flowHistory.getInpormation()
      }
      this.$emit('afterProcessActionSuccess', data)
    },
    /* 取消/关闭 点击如果当前页打是 flowTaskView 页面的就关闭当前页 */
    tabRemoveHandle (tabName) {
      // 找到tab对象
      const findTab = this.visitedViews.find(tag => tag.fullPath === tabName)
      if (findTab) {
        this.$store.dispatch('tagsView/delView', findTab).then(({ visitedViews }) => {
          // 如果不是当前
          if (this.isCurrentTab({ fullPath: tabName })) {
            this.toLastTabView(visitedViews)
          }
        })
      }
    },
    /* 判断tab是否是当前路由页面 */
    isCurrentTab (tab) {
      return tab.fullPath === this.$route.fullPath
    },
    /* 移动到最后一个tab */
    toLastTabView (visitedViews) {
      const latestView = visitedViews.slice(-1)[0]
      if (latestView) {
        this.$router.push(latestView.fullPath)
      } else {
        this.$router.push('/dashboard')
      }
    }
  }
}
</script>

<style lang="scss">
//.tabs-nav.el-tabs.el-tabs--card > .el-tabs__content > .el-tab-pane{
//  padding: 0 !important;
//}
.workflow-server{
  margin-top: 12px;
}
.workflowTabsWrap{
  height: 100%;
  overflow: auto;
  margin-bottom: 26px;
  padding-bottom: 12px;
  .colorC{
    height: 15px;
    background-color: #edeff2;
  }
  //.slot{
  //  .el-collapse{
  //    border-bottom: none;
  //  }
  //}
}
.el-tabs{
  &.order-and-flow-tab {
    >.el-tabs__content{
      height:calc(100vh - 153px);
      overflow-y: auto;
      padding-bottom: 12px;
    }
    &.flow-open-mode{
      >.el-tabs__content{
        height:calc(100vh - 210px);
        overflow-y: auto;
        padding-bottom: 12px;
      }
    }
  }
}
.order-form-contain {
  .el-tabs{
    &.order-and-flow-tab {
      >.el-tabs__content{
        height:calc(100vh - 140px);
        overflow-y: auto;
        padding-bottom: 12px;
      }
    }
  }
}
</style>
<style scoped>
.common-file-wrap {
  width: 94px;
  margin-right: 12px;
}
</style>
