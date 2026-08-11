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
        flowType="listForm"
        @tab-click="workflowView"
        @workflow-handler="workflowHandler"
        @close-tab="back"
      >
        <div>{{ $t('black.appMulti') }}</div>
      </CWorkflowMulti>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import WorkflowCommon from '@/library/mixins/workflow-common'

export default {
  name: 'FistApprove',
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      blackId: ''
    }
  },
  computed: {
    // 用来指定工作流的业务ID 单据ID 注意每一个单的ID不一样
    workflowBusinessId () {
      return this.blackId ? this.blackId : null
    },
    // 禁用流程tab状态
    workflowTabDisabled () {
      return !this.blackId
    }
  },
  watch: { },
  created () {
    const { flag, readOnly = false } = this.$attrs.params
    this.blackId = this.$attrs.params.businessId
  },
  mounted () {},
  methods: {
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'black' // 调试产品
    },
    // 获取ref值
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    back () {
      this.$emit('tab-remove', this.$attrs.tabName)
      this.__setTabTodo('BlackList.getQuerydata')
    }
  }
}
</script>
