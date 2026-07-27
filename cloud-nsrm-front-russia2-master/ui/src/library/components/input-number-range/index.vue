<template>
  <div class="srm-input-number-range">
    <div class="input-number-start">
      <el-input
        v-model="startValue"
        :placeholder="$t('common.startValue')"
        @change="changeStartValue"
      />
    </div>
    <div class="input-number-content">
      <span>{{ connectText }}</span>
    </div>
    <div class="input-number-end">
      <el-input
        v-model="endValue"
        :placeholder="$t('common.endValue')"
        @change="changeEndValue"
      />
    </div>
  </div>
</template>

<script>
import { isNull } from '@/utils'
export default {
  name: 'InputNumberRange',
  props: {
    // 中间显示连接符
    connectText: {
      type: String,
      default: '~'
    },
    // 字段映射关系
    propsMap: {
      type: [Array, Object],
      default: () => ({})
    }
  },
  data () {
    return {
      startValue: null, // 起始值
      endValue: null // 截止值
    }
  },
  methods: {
    // 起始值变更
    changeStartValue (value) {
      const endValue = this.endValue
      // 判断是否非数字
      if (!isNaN(parseFloat(value)) && isFinite(value)) {
        // 如果起初和截止值同时存在，截止值不能小于起始值
        if (value && !isNull(endValue)) {
          if (Number(value) > Number(endValue)) {
            this.startValue = null
          }
        }
      } else {
        this.startValue = null
      }
      this.$emit('update-value', { startValue: isNull(this.startValue) ? this.startValue : Number(this.startValue), endValue: isNull(endValue) ? endValue : Number(endValue) }, this.propsMap)
    },
    // 截止值变更
    changeEndValue (value) {
      const startValue = this.startValue
      if (!isNaN(parseFloat(value)) && isFinite(value)) {
        // 如果起初和截止值同时存在，截止值不能小于起始值
        if (value && !isNull(startValue)) {
          if (Number(value) < Number(startValue)) {
            this.endValue = null
          }
        }
      } else {
        this.endValue = null
      }
      this.$emit('update-value', { startValue: isNull(startValue) ? startValue : Number(startValue), endValue: isNull(this.endValue) ? this.endValue : Number(this.endValue) }, this.propsMap)
    },
    // 重置
    clearValue () {
      this.startValue = null
      this.endValue = null
    }
  }
}
</script>

<style lang="scss" scoped>
.srm-input-number-range {
  display: flex;
  .input-number-content {
    margin: 0 5px;
  }
}
</style>
