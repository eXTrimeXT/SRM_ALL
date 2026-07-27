<template>
  <el-col
    :xs="rxs"
    :sm="rsm"
    :md="rmd"
    :lg="rlg"
    :xl="rxl"
    v-bind="attrs"
    v-on="$listeners"
  >
    <slot />
  </el-col>
</template>

<script>
import omit from 'lodash/omit'
export default {
  name: 'SrmCol',
  props: {
    initCol: { // 显示列数
      type: Number,
      default: () => { // 默认4列
        return null
      },
      validator: val => {
        if (val) {
          return parseInt(val)
        } else {
          return null
        }
      }
    },
    xs: {
      type: [Number, Object],
      default: () => {
        return 24
      }
    },
    sm: {
      type: [Number, Object],
      default: () => {
        return 12
      }
    },
    md: {
      type: [Number, Object],
      default: () => {
        return 8
      }
    },
    lg: {
      type: [Number, Object],
      default: () => {
        return 6
      }
    },
    xl: {
      type: [Number, Object],
      default: () => {
        return 6
      }
    }
  },
  computed: {
    attrs () {
      return omit(this.$attrs, ['xs', 'sm', 'md', 'lg', 'xl'])
    },
    rxs () {
      const col = { // 预设值
        1: 24,
        2: 24,
        3: 24,
        4: 24,
        6: 4,
        8: 3,
        12: 2,
        24: 1
      }
      return this.initCol ? col[this.initCol] : this.xs
    },
    rsm () {
      const col = {
        1: 24,
        2: 24,
        3: 12,
        4: 12,
        6: 4,
        8: 3,
        12: 2,
        24: 24
      }
      return this.initCol ? col[this.initCol] : this.sm
    },
    rmd () {
      const col = {
        1: 24,
        2: 12,
        3: 8,
        4: 6,
        6: 4,
        8: 3,
        12: 2,
        24: 1
      }
      return this.initCol ? col[this.initCol] : this.md
    },
    rlg () {
      const col = {
        1: 24,
        2: 12,
        3: 8,
        4: 6,
        6: 4,
        8: 3,
        12: 2,
        24: 1
      }
      return this.initCol ? col[this.initCol] : this.lg
    },
    rxl () {
      const col = {
        1: 24,
        2: 12,
        3: 8,
        4: 6,
        6: 4,
        8: 3,
        12: 2,
        24: 1
      }
      return this.initCol ? col[this.initCol] : this.xl
    }
  }
}
</script>
