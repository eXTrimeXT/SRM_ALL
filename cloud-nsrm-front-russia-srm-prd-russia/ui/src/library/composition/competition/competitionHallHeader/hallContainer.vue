<template>
  <SrmRow :gutter="0" class="hall-header-row">
    <!--本轮竞价倒计时-->
    <SrmCol v-bind="countdownRoundColSpan" class="hall-header-col">
      <div class="hall-header-col-wrap">
        <slot name="countdownRound" />
      </div>
    </SrmCol>

    <!--延时竞价倒计时-->
    <SrmCol
      v-if="showCountdownDelayed"
      :xs="24"
      :sm="4"
      :md="12"
      :lg="4"
      :xl="4"
      class="hall-header-col"
    >
      <div class="hall-header-col-wrap">
        <slot name="countdownDelayed" />
      </div>
    </SrmCol>

    <!--竞价单信息-->
    <SrmCol v-bind="hallDetailInfoColSpan" class="hall-header-col">
      <div class="hall-header-col-wrap">
        <slot name="hallDetailInfo" />
      </div>
    </SrmCol>
  </SrmRow>
</template>

<script>
/**
 * 竞价大厅头布局容器
 */
export default {
  name: 'HallContainer',

  props: {
    // 是否显示延时竞价
    showCountdownDelayed: {
      type: Boolean,
      default: true
    }
  },

  computed: {
    countdownRoundColSpan () {
      const flag = this.showCountdownDelayed
      return {
        xs: 24,
        sm: flag ? 16 : 24,
        md: flag ? 12 : 12,
        lg: flag ? 6 : 10,
        xl: flag ? 6 : 10
      }
    },
    hallDetailInfoColSpan () {
      const flag = this.showCountdownDelayed
      return {
        xs: 24,
        sm: 24,
        md: flag ? 24 : 12,
        lg: flag ? 14 : 14,
        xl: flag ? 14 : 14
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.hall-header-col {
  &:not(:last-child) {
  }
}
.hall-header-row {
  border: 1px solid rgba(170, 170, 170, 1);
  border-radius: 4px;
}
.hall-header-col-wrap {
  width: 100%;
  height: 100%;
  overflow-x: auto;
  padding: 10px 15px;
  box-sizing: border-box;
}
</style>
