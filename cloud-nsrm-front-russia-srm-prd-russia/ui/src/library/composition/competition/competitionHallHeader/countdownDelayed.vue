<template>
  <div class="countdown-delayed-wrap countdown-wrap">
    <p class="title">
      {{ title }}
    </p>
    <div class="countdown-time-wrap right-divider">
      <div
        v-for="(item, index) in countdownTimeList"
        :key="`countdown-time-item-${index}`"
        :class="item.class"
      >
        {{ item.value }}
      </div>
    </div>
    <p class="desc" />
  </div>
</template>

<script>
/**
 * 延时竞价倒计时
 */
import { DynamicCutoffTimeClass } from '@/library/components/dynamic-cutoff-time'

export default {
  name: 'CountdownDelayed',

  props: {
    deadlineTime: {
      type: [String, Number],
      default: ''
    },
    title: {
      type: String,
      default () {
        // 延时竞价仅剩：
        return this.$t('bidMod.common.coundDownTitle')
      }
    }
  },

  data () {
    return {
      dynamicCutoffTime: null
    }
  },

  computed: {
    countdownTimeList () {
      // 需要判断年，月是否大于0
      const {
        years = 0,
        months = 0,
        days = 0,
        hours = 0,
        minutes = 0,
        seconds = 0
      } = this.dynamicCutoffTime?.diffValue || {}

      return [
        // 年
        ...(
          years > 0
            ? [
              { class: 'number', value: years },
              // 年
              { class: 'unit', value: this.$t('time.years') }
            ] : []
        ),
        // 月
        ...(
          years > 0 || months > 0
            ? [
              { class: 'number', value: months },
              // 月
              { class: 'unit', value: this.$t('time.months') }
            ] : []
        ),
        // 天
        ...(
          years > 0 || months > 0 || days > 0
            ? [
              { class: 'number', value: days },
              { class: 'unit', value: this.$t('time.days') }
            ] : []
        ),
        // 时
        ...(
          years > 0 || months > 0 || days > 0 || hours > 0
            ? [
              { class: 'number', value: hours },
              { class: 'unit', value: this.$t('time.tense') }
            ] : []
        ),
        // 默认从分开始
        { class: 'number', value: minutes },
        // 分
        { class: 'unit', value: this.$t('time.min') },
        { class: 'number red', value: seconds },
        // 秒
        { class: 'unit', value: this.$t('time.seconds') }
      ]
    }
  },

  watch: {
    'dynamicCutoffTime.isDeadline': {
      handler (newVal, oldVal) {
        if (newVal && !oldVal) {
          // 触发已截止
          this.$emit('deadline')
        }
      },
      immediate: true,
      deep: true
    }
  },

  beforeDestroy () {
    this.clearTimer()
  },

  methods: {
    /* 手动启用倒计时 */
    delayed () {
      if (this.dynamicCutoffTime) {
        this.dynamicCutoffTime.setOption({
          deadlineTime: this.deadlineTime
        })
      } else {
        this.dynamicCutoffTime = new DynamicCutoffTimeClass({
          deadlineTime: this.deadlineTime
        })
      }
    },

    /* 取消延时 */
    clearTimer () {
      if (this.dynamicCutoffTime) {
        this.dynamicCutoffTime.clearTimer()
        this.dynamicCutoffTime = null
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import "countdownWrap";
.right-divider {
  border-right: 1px solid #D7D7D7;
}
</style>
