<template>
  <div class="dynamic-cutoff-time">
    <span
      v-if="label"
      class="label"
    >
      {{ label }}
    </span>
    <span class="time">{{ showTimeText }}</span>
  </div>
</template>

<script>
/**
 * 动态截止时间，天-时-分-秒，并且能动态返回是否已截止
 */
import { dynamicCutoffTimeDiffToString } from 'lib@/components/dynamic-cutoff-time'
import dayjs from 'dayjs'

export default {
  name: 'DynamicCutoffTime',

  props: {
    // 提示文本
    label: {
      type: String,
      default: ''
    },

    // 剩余时间 单位毫秒 与deadlineTime二选一
    remainingTime: {
      type: Number,
      default: 0
    },

    // 截止时间 与remainingTime二选一
    deadlineTime: {
      type: [String, Number],
      required: false
    },

    // 截止时间对比的时间，默认当前时间，要比deadlineTime小，否则返回已截止
    deadlineDiffTime: {
      type: [String, Number],
      required: false
    },

    // 已截止文本
    deadlineText: {
      type: String,
      default: ''
    },

    // 单位
    timeUnit: {
      type: String,
      default: '',
      validator: value => {
        if (value) {
          return ['years', 'months', 'days', 'hours', 'minutes', 'seconds'].includes(value)
        }
        return true
      }
    }
  },

  data () {
    return {
      // 显示文本
      showTimeText: '',
      // 定时器
      timer: null,
      // 入参
      diffParams: {}
    }
  },
  computed: {
    deadlineTextVal () {
      return this.deadlineText || this.$t('bidMod.competitionLts.expired')
    }
  },
  watch: {
    remainingTime: {
      handler (newValue) {
        // 二选一
        if (newValue && newValue > 0 && !this.deadlineTime) {
          // 再启动新的
          this.startDynamicCutoffTime('remainingTime')
        }
      },
      immediate: true
    },

    deadlineTime: {
      handler (newValue) {
        // 二选一
        if (newValue && !this.remainingTime) {
          this.startDynamicCutoffTime('deadlineTime')
        }
      },
      immediate: true
    }
  },

  beforeDestroy () {
    // 销毁定时器
    clearInterval(this.timer)
  },

  methods: {
    /* 启动倒计时 */
    startDynamicCutoffTime (type) {
      // 先清除旧的定时器
      if (this.timer) {
        clearInterval(this.timer)
      }

      // 启动倒计时 刷新一下过时状态为否
      this.$emit('isDeadline', false)

      this.diffParams = {
        time1: '',
        time2: '',
        params: {
          unit: this.timeUnit
        }
      }
      // 编排计算参数
      if (type === 'remainingTime') {
        // 剩余时间
        this.diffParams.time1 = this.remainingTime
        this.diffParams.params = {
          ...this.diffParams.params,
          isRemainingTime: true
        }
      } else if (type === 'deadlineTime') {
        // 截止时间
        this.diffParams.time1 = this.deadlineTime
        this.diffParams.time2 = this.deadlineDiffTime
      }

      // 计算结果
      const calculateResult = dynamicCutoffTimeDiffToString(
        this.diffParams.time1,
        this.diffParams.time2,
        this.diffParams.params
      )

      if (calculateResult.status) {
        this.timer = setInterval(() => {
          // 执行减1秒
          if (type === 'remainingTime') {
            this.diffParams.time1 -= 1000
          } else if (type === 'deadlineTime') {
            // 浅拷贝，会影响原对象
            dayjs(this.diffParams.time1).subtract(1, 'second')
          }

          const result = dynamicCutoffTimeDiffToString(
            this.diffParams.time1,
            this.diffParams.time2,
            this.diffParams.params
          )
          if (result.status) {
            // 未截止
            this.showTimeText = result.value
            if (this.deadlineDiffTime) {
              // 自定义对比时间是静态的，不需要定时器计算
              clearInterval(this.timer)
            }
          } else {
            this.showTimeText = this.deadlineTextVal
            // 已截止
            clearInterval(this.timer)
            // 发起已截止回调
            this.$emit('isDeadline', true)
          }
        }, 1000)
      } else {
        this.showTimeText = this.deadlineTextVal
        // 发起已截止回调
        this.$emit('isDeadline', true)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.dynamic-cutoff-time {
  display: inline-block;
  font-size: 15px;
  font-weight: bold;
  height: 40px;
  line-height: 40px;
  .time {
    margin-left: 10px;
    color: #f44;
  }
}
</style>
