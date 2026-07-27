<template>
  <div class="inquiry-detail-workflow">
    <WorkflowReport
      v-if="workflowEnable"
      need-init
      :fun-params="workflowParamsInfo"
      @afterProcessAction="afterProcessAction"
    />
    <!--审批流还不能展示-->
    <h2 v-else>
      {{ $t('bidMod.common.approvalMsg') }}
    </h2>

    <!--底部按钮区域 关闭审批流才显示这个-->
    <CToolbar v-if="workflowEnable && workflowClose && showToolbar">
      <template slot="right">
        <CWorkflowButton
          ref="workflowButtonSUBMIT"
          type="primary"
          :integration-mode="workflowParamsInfo.integrationMode"
          @submit-direct="submitDirect('SUBMIT')"
          @close-tab="workflowButtonSubmitSuccess"
        />
      </template>
    </CToolbar>
  </div>
</template>

<script>
/**
 * 寻源模块审批流
 */
import WorkflowReport from 'lib@/components/c-workflow/workflowReport'
import WorkflowCommon from 'lib@/mixins/workflow-common'
import CToolbar from 'lib@/components/c-toolbar'
import CWorkflowButton from 'lib@/components/c-workflow-button'

export default {
  name: 'WorkflowTab',

  components: {
    WorkflowReport,
    CToolbar,
    CWorkflowButton
  },

  mixins: [WorkflowCommon],

  props: {
    // 业务ID
    scopeId: {
      type: [Number, String],
      required: true
    },
    // 绑定的审批流模板ID
    workflowModelId: {
      type: String,
      required: true
    },
    // 审批流是否可访问
    workflowEnable: {
      type: Boolean,
      default: true
    },
    // 是否显示底部按钮操作栏
    showToolbar: {
      type: Boolean,
      default: false
    }
    // 隐式参数，WorkflowReport组件需要用
    // :params="{ activeWorkflowTab: true }"
  },

  data () {
    return {
      showWorkflow: false,
      workflowClose: false
    }
  },

  computed: {
    // 审批流获取业务ID
    workflowBusinessId () {
      return this.scopeId || null
    },

    // 定义审批Tab是否可以点击 流程组件使用
    workflowTabDisabled () {
      return false
    }
  },

  watch: {
    'workflowParamsInfo.integrationMode': {
      handler (val) {
        // None
        this.workflowClose = val === 'None'
      },
      immediate: true,
      deep: true
    }
  },

  methods: {
    /* 指定工作流的业务类型，在定义工作流时指定 */
    async getWorkflowBusinessType () {
      return this.workflowModelId
    },

    /* 审批流关闭的情况下，点击提交按钮 */
    async submitDirect (operationType) {
      const flowDataInfo = await this.handlerAfter(operationType, 'N')
      this.$refs.workflowButtonSUBMIT.submitEngine(flowDataInfo)
    },

    /* 审批流关闭的情况下，提交成功 */
    workflowButtonSubmitSuccess () {
      this.$emit('workflow-success')
    },

    /* 审批流进行操作后的回调 */
    afterProcessAction (data) {
      // [提交、撤回、驳回、审批]
      const FETCH_KEY = ['submit', 'recall', 'reject', 'examine']
      if (data.success && FETCH_KEY.includes(data.btnMsg.key)) {
        // 刷新单据信息
        this.$emit('afterProcessActionSuccess')
      }
    }
  }
}
</script>
