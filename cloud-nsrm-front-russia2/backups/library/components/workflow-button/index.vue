<!-- 工作流提交按钮组件 -->
<template>
  <div>
    <el-button
      v-bind="attrs"
      type="primary"
      style="margin-left: 10px;"
      v-on="$listeners"
      @click="clickHandler"
    >
      {{ label || $t("common.submit") }}
    </el-button>
    <!-- 起草人意见 -->
    <srm-dialog
      :title="$t('vendorMod.approvalProcess')"
      :visible.sync="loggerComment"
      :modal-append-to-body="false"
      size="middle"
      style="text-align: center"
    >
      <!-- 请审批，谢谢！ -->
      <el-input
        v-model="inputComment"
        type="textarea"
        :rows="4"
        :placeholder="$t('vendorMod.pleaseApproval')"
      />
      <div class="topComment">
        <el-button @click="loggerComment = false">
          {{ $t("common.cancel") }}
        </el-button>
        <el-button
          type="primary"
          @click="commentForm"
        >
          {{ $t("common.confirm") }}
        </el-button>
      </div>
    </srm-dialog>
  </div>
</template>

<script>
const WORK_FLOW_MODE = {
  PRODUCT: 'Product',
  IFRAME: 'Iframe',
  SELF: 'Self',
  PUSH: 'Push',
  NONE: 'None'
}

export default {
  name: 'WorkflowButton',
  props: {
    // 按钮文案
    label: {
      type: String
    },
    // 业务标识，每个功能自己定义
    businessType: {
      type: String,
      required: true
    },
    businessId: {
      type: [String, Number]
    },
    businessData: {
      type: Object
    },
    // 暂存数据的方法，返回Promise对象
    temporaryStorage: {
      type: Function,
      default: () => Promise.resolve()
    },
    // 提交数据的方法，返回Promise对象
    submit: {
      type: Function,
      default: () => Promise.resolve()
    }
  },
  data () {
    return {
      mode: '',
      loggerComment: false,
      inputComment: ''
    }
  },
  created () {
    //  查询流程模式
    this.queryWorkflowMode()
  },
  methods: {
    async clickHandler () {
      console.log('[workflow button click handler]')
      // step1: 暂存数据
      const res = await this.temporaryStorage()
      if (!res) return false
      // step2: 根据工作模式，分别处理
      switch (this.mode) {
        // 产品工作流模式，跳转到工作流tab页面
        case WORK_FLOW_MODE.PRODUCT:
          this.$emit('tabFlowHandler')
          break
        // iframe嵌入页面模式，跳转到工作流tab页面
        case WORK_FLOW_MODE.IFRAME:
          this.$emit('tabFlowHandler')
          break
        // 自带页面模式，跳转到工作流tab页面
        case WORK_FLOW_MODE.SELF:
          this.$emit('tabFlowHandler')
          break
        // 无页面推送模式，填写提交人意见后调用submitEngine
        case WORK_FLOW_MODE.PUSH:
          this.loggerComment = true
          break
        // 无工作流，直接调用提交数据接口
        case WORK_FLOW_MODE.NONE:
          // if (this.submit && typeof this.submit === 'function'){
          //  this.submit();
          // }
          this.loggerComment = true
          break
        default:
      }
    },
    async queryWorkflowMode () {
      const res = await this.$api.base.flowAPI.getFlowIntegrationMode({ businessType: this.businessType })
      this.mode = res.data
    },
    async submitEngine () {
      const res = await this.$http({
        url: '/api-base/flow/event/submitEngine',
        method: 'POST',
        data: {
          businessId: this.businessId,
          businessData: this.businessData,
          businessType: this.businessType
        },
        loading: true
      })
      console.log('[submitEngine]', res)
    },
    // 起草人意见确认提交
    async commentForm () {
      // 暂存单据之后调用submitEngine
      await this.temporaryStorage(this.inputComment)
      await this.submitEngine()
      this.loggerComment = false
    }
  }
}
</script>
<style scoped lang="scss"></style>
