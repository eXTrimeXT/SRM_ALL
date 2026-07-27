<template>
  <el-container
    class="flex-container workflowReport_wrapper"
    direction="vertical"
  >
    <el-main
      style="position:relative;height:400px;"
    >
      <!-- 产品模式 -->
      <ProductMode
        v-if="flowIntegrationMode==='Product'"
        :funParams="funParams"
        :is-nested="isNested"
        @afterProcessAction="afterProcessActionHandel"
      />
      <!-- IDE流程模式 -->
      <IdeMode
        v-if="flowIntegrationMode==='IdeFlow'"
        :is-nested="isNested"
        :funParams="funParams"
        @flowAfterProcessAction="afterProcessActionHandel"
      />
      <!-- 客户iframe模式 需要项目额外开发实现 此处预留位置 -->
      <div v-if="flowIntegrationMode==='Iframe'">
        {{ $t('flowMod.explanation[0]') }}
      </div>
      <!-- 推送模式 -->
      <div v-if="flowIntegrationMode==='Push'">
        {{ $t('flowMod.explanation[1]') }}
      </div>
      <!-- 无工作流模式 文字提示 -->
      <div v-if="flowIntegrationMode==='None'">
        {{ $t('flowMod.explanation[2]') }}
      </div>
      <!-- IdeSdk 推送模式 -->
      <div v-if="flowIntegrationMode==='IdeSdk'">
        数据已推送，请前往OA审批
      </div>
      <!-- 自定义模式 当前代码为原来2020-0430版本代码-->
      <SelfMode
        v-if="flowIntegrationMode==='Self'"
        :fun-params="funParams"
        :is-nested="isNested"
        :need-init="needInit"
      />
    </el-main>
  </el-container>
</template>

<script>
import SelfMode from './selfMode'
import ProductMode from './productMode/productMode'
import IdeMode from './ideMode/ideMode'
// 工作流的集成模式
// flowIntegrationMode 模式值备注
// Product ->产品工作流模式
// IdeFlow ->IDE工作流模式
// Iframe ->iframe嵌入页面模式
// Self ->自带页面模式
// Push ->无页面推送模式
// None ->无工作流
// IdeSdk -> IdeSdk 推送模式

export default {
  name: 'WorkflowReport',
  components: {
    SelfMode,
    ProductMode,
    IdeMode
  },
  props: {
    needInit: {
      type: Boolean,
      default: false
    },
    isNested: {
      type: Boolean,
      default: false
    },
    funParams: {
      type: Object,
      default: function () {
        return {}
      }
    }
  },
  data () {
    return {
      flowIntegrationMode: '' // 工作流模式
    }
  },
  watch: {
    funParams: {
      handler (data) {
        this.flowIntegrationMode = data.integrationMode
      },
      immediate: true,
      deep: true
    }
  },
  created () {
    this.flowIntegrationMode = this.funParams.integrationMode // 流程模式
  },
  methods: {
    /* 操作流程后触发组件回调 */
    afterProcessActionHandel (result, eveName) {
      this.$emit('afterProcessAction', result)
    }
  }
}
</script>
