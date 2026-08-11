<template>
  <el-tabs
    v-if="workflow"
    ref="workflowTabs"
    v-model="value"
    :class="['order-and-flow-tab level3Tab', {'flow-open-mode': workflowMode}]"
    @tab-click="tabClick"
  >
    <!-- 单据信息 -->
    <el-tab-pane
      :label="tabConfigInfo.biz.label"
      :name="tabConfigInfo.biz.name"
      class="flow-order-info"
    >
      <slot />
      <FlowHistory
        v-if="showFlowHistory && !workflowParamsInfo.tabDisabled && workflowMode"
        ref="flowHistory"
        :businessType="workflowParamsInfo.businessType"
        :businessId="workflowParamsInfo.businessId"
      />
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
            v-if="buttonConfigInfo.save.view && flowType=='orderForm' && hasPermission(buttonConfigInfo.save.code)"
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
            v-if="buttonConfigInfo.submit.view && flowType=='orderForm' && hasPermission(buttonConfigInfo.submit.code)"
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
    </el-tab-pane>
    <slot name="otherTab" />
    <!-- 流程审批 && userType === 'BUYER'-->
    <el-tab-pane
      v-if="workflowMode"
      :disabled="
        workflowParamsInfo.tabDisabled || !workflowParamsInfo.businessId
      "
      :label="tabConfigInfo.workflow.label"
      :name="tabConfigInfo.workflow.name"
      style="height: 100%;"
    >
      <WorkflowReport
        v-if="showWorkflow && !isSrmSelf"
        need-init
        :is-nested="isNested"
        :fun-params="workflowParamsInfo"
        @afterProcessAction="multiAfterProcessActionHandel"
      />
      <WorkflowReportSelf
        v-if="showWorkflow && isSrmSelf"
        need-init
        :is-nested="isNested"
        :fun-params="workflowParamsInfo"
        @afterProcessAction="multiAfterProcessActionHandel"
      />
    </el-tab-pane>
  </el-tabs>
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

export default {
  name: 'CWorkflow',
  components: {
    CToolbar,
    WorkflowReport,
    WorkflowReportSelf,
    CWorkflowButton,
    FlowHistory
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
    }
  },
  data () {
    return {
      workflowParamsInfo: {
        businessType: '',
        businessId: null,
        businessVariables: {},
        integrationMode: null,
        tabDisabled: true,
        version: new Date(),
        workflowActive: false
      },
      showWorkflow: false,
      workflowMode: false // 是否需要显示审批流信息
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
  activated () {
    if (this.funParams.businessId) {
      this.$emit('workflow-handler', 'update')
    }
  },
  methods: {
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
      if (this.$route.name == 'flowTaskView') {
        this.tabRemoveHandle(this.$route.fullPath)
      } else {
        this.$emit('close-tab')
      }
    },
    // 关闭
    close () {
      if (this.$route.name == 'flowTaskView') {
        this.tabRemoveHandle(this.$route.fullPath)
      } else {
        this.buttonConfigInfo.save.disabled = true
        this.buttonConfigInfo.submit.disabled = true
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
