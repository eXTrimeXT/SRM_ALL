<!-- 工作流提交按钮组件 -->
<template>
  <!-- <el-button
    v-bind="$attrs"
    :disabled="disabled"
    :type="type"
    style="margin-left: 10px;"
    v-on="$listeners"
    @click="clickHandler"
  >
    {{ buttonName || $t("common.submit") }}
  </el-button> -->
  <RButton
    v-bind="$attrs"
    :disabled="disabled"
    :type="type"
    style="margin-left: 10px;"
    v-on="$listeners"
    @click="clickHandler"
  >
    {{ buttonName || $t("common.submit") }}
  </RButton>
</template>

<script>
import { RButton } from '@/library/components/srm-components/button'
const WORK_FLOW_MODE = {
  PRODUCT: 'Product',
  IFRAME: 'Iframe',
  SELF: 'Self',
  PUSH: 'Push',
  NONE: 'None',
  IDEFLOW: 'IdeFlow',
  IDESDK: 'IdeSdk'
}
export default {
  name: 'CWorkflowButton',
  components: {
    RButton
  },
  props: {
    // 按钮文案
    buttonName: {
      type: String
    },
    type: {
      type: String,
      default: () => {
        return 'primary'
      }
    },
    disabled: {
      type: Boolean,
      default: () => {
        return false
      }
    },
    integrationMode: {
      type: String
    }
  },
  data () {
    return {}
  },
  created () {},
  methods: {
    async clickHandler () {
      // step2: 根据工作模式，分别处理
      switch (this.integrationMode) {
      // 产品工作流模式，跳转到工作流tab页面
      case WORK_FLOW_MODE.PRODUCT:
        this.$emit('click-handler')
        break
        // IDE工作流
      case WORK_FLOW_MODE.IDEFLOW:
        this.$emit('click-handler')
        break
        // iframe嵌入页面模式，跳转到工作流tab页面
      case WORK_FLOW_MODE.IFRAME:
        this.$emit('click-handler')
        break
        // 自带页面模式，跳转到工作流tab页面
      case WORK_FLOW_MODE.SELF:
        this.$emit('click-handler')
        break
        // 无页面推送模式，填写提交人意见后调用submitEngine
      case WORK_FLOW_MODE.PUSH:
        // this.loggerComment = true
        this.$emit('submit-direct')
        break
        // 无工作流，直接调用提交数据接口
      case WORK_FLOW_MODE.NONE:
        this.$emit('submit-direct')
        break
        // IDESDK推送模式
      case WORK_FLOW_MODE.IDESDK:
        this.$emit('submit-direct')
        break
      default:
      }
    },
    // 点击(click)/确认(confirm) 触发操作之后，父组件调用
    handlerAfter (submitData, callback = null) {
      // step2: 根据工作模式，分别处理
      switch (this.integrationMode) {
      // 产品工作流模式，跳转到工作流tab页面
      case WORK_FLOW_MODE.PRODUCT:
        this.$emit('workflow-handler', '', callback)
        break
        // IDE工作流
      case WORK_FLOW_MODE.IDEFLOW:
        this.$emit('workflow-handler', '', callback)
        break
        // iframe嵌入页面模式，跳转到工作流tab页面
      case WORK_FLOW_MODE.IFRAME:
        this.$emit('workflow-handler', '', callback)
        break
        // 自带页面模式，跳转到工作流tab页面
      case WORK_FLOW_MODE.SELF:
        this.$emit('workflow-handler', '', callback)
        break
        // 无页面推送模式，填写提交人意见后调用submitEngine
      case WORK_FLOW_MODE.PUSH:
        this.submitEngine(submitData, callback)
        break
        // 无工作流，直接调用提交数据接口，无需执行
      case WORK_FLOW_MODE.NONE:
        this.submitEngine(submitData, callback)
        break
        // IDESDK推送模式
      case WORK_FLOW_MODE.IDESDK:
        this.submitEngine(submitData, callback)
        break
      default:
      }
    },
    submitEngine (submitData, callback = null) {
      this.$confirm(this.$t('common.sureSubmit'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/api-base/flow/event/submitEngine',
          method: 'POST',
          data: submitData,
          loading: true
        }).then(res => {
          console.log('[submitEngine]', res)
          this.$message({
            type: 'success',
            message: this.$t('components.approvalHead.tips.approvalCompletion')
          })
          this.$emit('close-tab')
          callback && callback()
        })
      }).catch(() => {
        this.$emit('workflow-cancel')
      })
    }
  }
}
</script>
<style scoped lang="scss"></style>
