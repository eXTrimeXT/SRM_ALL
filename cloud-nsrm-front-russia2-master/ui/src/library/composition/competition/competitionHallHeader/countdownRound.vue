<template>
  <div class="countdown-round-wrap">
    <div class="left-wrap countdown-wrap">
      <slot name="title">
        <h3 class="title">
          {{ title }}
        </h3>
      </slot>
      <!-- <div :class="['countdown-time-wrap', $slots.right && 'right-divider']"> -->
      <div :class="['countdown-time-wrap', 'right-divider']">
        <div
          v-for="(item, index) in countdownTimeList"
          :key="`countdown-time-item-${index}`"
          :class="item.class"
        >
          {{ item.value }}
        </div>
      </div>
      <!-- <p class="desc">
        {{ descLabel }}{{ deadlineTime }}
      </p> -->
    </div>

    <!--检查是否存在插槽，如果没有隐藏-->
    <div v-if="$slots.right" class="right-wrap">
      <slot name="right" />
    </div>
  </div>
</template>

<script>
/**
 * 本轮竞价倒计时
 */
import { DynamicCutoffTimeClass } from '@/library/components/dynamic-cutoff-time'

export default {
  name: 'CountdownRound',

  props: {
    deadlineTime: {
      type: [String, Number],
      default: ''
    },
    title: {
      type: String,
      default () {
        return this.$t('bidMod.competitionLts.countdownRoundAttrsProps4')
      }
    },
    descLabel: {
      type: String,
      default () {
        return this.$t('bidMod.common.deadlineTime')
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

      // 默认从天开始
      return [
        // 年
        ...(
          years > 0
            ? [
              { class: 'number', value: years },
              { class: 'unit', value: this.$t('time.years') }
            ] : []
        ),
        // 月
        ...(
          years > 0 || months > 0
            ? [
              { class: 'number', value: months },
              { class: 'unit', value: this.$t('time.months') }
            ] : []
        ),
        // 默认从天开始
        { class: 'number', value: days },
        { class: 'unit', value: this.$t('time.days') },
        { class: 'number', value: hours },
        { class: 'unit', value: this.$t('time.tense') },
        { class: 'number', value: minutes },
        { class: 'unit', value: this.$t('time.min') },
        { class: 'number red', value: seconds },
        { class: 'unit', value: this.$t('time.second') }
      ]
    }
  },

  watch: {
    deadlineTime: {
      handler (val) {
        if (this.dynamicCutoffTime) {
          this.dynamicCutoffTime.clearTimer()
        }
        if (val) {
          this.dynamicCutoffTime = new DynamicCutoffTimeClass({
            deadlineTime: this.deadlineTime
          })
        }
      },
      immediate: true
    },

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
    if (this.dynamicCutoffTime) {
      this.dynamicCutoffTime.clearTimer()
    }
  }
}
</script>

<style lang="scss" scoped>
@import "countdownWrap";

.countdown-round-wrap {
  display: flex;
  width: 100%;
  .left-wrap {
    flex: 1;
    display: flex;
    .right-divider {
      border-right: 1px solid #D7D7D7;
    }
  }
  .right-wrap {
    width: 120px;
  }
}
</style>
