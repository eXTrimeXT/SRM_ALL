<!-- 工作流提交按钮组件 -->
<template>
  <el-button
    v-bind="$attrs"
    :disabled="disabled"
    :type="type"
    v-on="$listeners"
    @click="clickHandler"
  >
    {{ buttonName || $t("common.submit") }}
  </el-button>
</template>

<script>
export default {
  name: 'ListFlowBtn',
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
    // 流程模式
    integrationMode: {
      type: String,
      default: () => {
        return ''
      }
    },
    // 业务流程类型code 例如黑名单 是black
    businessType: {
      type: String,
      default: () => {
        return ''
      }
    },
    disabled: {
      type: Boolean,
      default: () => {
        return false
      }
    },
    beforeOpen: {
      type: Function,
      default: () => {
        return true
      }
    },
    // 单据信息
    getOrderData: {
      type: Function,
      default: () => {
        return {
          businessData: [], // 选中的多个单据集合
          fileuploadIds: [], // 附件信息
          businessType: '',
          businessIds: []
        }
      }
    }
  },
  data () {
    return {
      flowMode: {}
    }
  },
  async created () {
    this.flowMode = await this.getFlowIntegrationMode()
  },
  methods: {
    async getFlowIntegrationMode () {
      if (this.businessType) {
        if (!this.integrationMode) {
          const res = await this.$api.base.flowAPI.getFlowIntegrationMode({ businessType: this.businessType })
          return (res.data || '')
        } else {
          return this.integrationMode
        }
      }
    },
    async clickHandler () {
      let isTodoClick = await this.beforeOpen()
      if (!isTodoClick) return // 前置判断条件
      let orderData = await this.getOrderData()
      if (orderData.businessData.length > 0) {
        const { data } = await this.$http({
          url: '/api-base/flow/event/table/submitEngine',
          method: 'POST',
          data: orderData,
          loading: true
        })
        this.$emit('click-handler', data)
      } else {
        this.$message({
          type: 'warning',
          message: this.$t('components.flownode.selectOrder')
        })
      }
    }
  }
}
</script>
<style scoped lang="scss"></style>
