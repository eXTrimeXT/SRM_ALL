<template>
  <div>
    <div v-if="readPretty">{{ inputValue }}</div>
    <el-input
      v-if="!readPretty"
      class="c-currency-input"
      :value="inputValue"
      :readonly="readonly"
      :maxlength="maxlength"
      :placeholder="placeholder"
      :disabled="disabled"
      v-on="eventListeners"
    >
      <template slot="append">
        <slot name="append" />
      </template>
    </el-input>
  </div>
</template>

<script>
import { comma } from 'lib@/utils/number'

export default {
  name: 'CCurrencyInput',
  props: {
    value: {
      type: [Number, String]
    },
    readPretty: {
      type: Boolean,
      default: false
    },
    readonly: {
      type: Boolean,
      default: false
    },
    placeholder: {
      type: String,
      default: ''
    },
    disabled: {
      type: Boolean,
      default: false
    },
    maxlength: {
      type: String,
      default: ''
    },
    // 小数位前限制位数
    precisionPrefixLimit: {
      type: Number,
      default: 10
    },
    // 保留小数位
    precision: {
      type: Number,
      default: 2
    },
    // 是否允许负数
    allowNegativeNumbers: {
      type: Boolean,
      default: false
    },
    // 是否显示验证信息
    showValidateMessage: {
      type: Boolean,
      default: true
    },
    isReplenishPrecision: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      inputValue: '',
      focused: false
    }
  },
  computed: {
    eventListeners () {
      return Object.assign({}, this.$listeners, {
        input: this.handleInput,
        blur: this.handleBlur,
        focus: this.handleFocus
      })
    }
  },
  watch: {
    value: {
      immediate: true,
      handler () {
        const formatValue = comma(this.value)
        if (formatValue !== this.inputValue) {
          if (this.focused) {
            this.inputValue = formatValue
          } else {
            this.inputValue = this.isReplenishPrecision
              ? this.handlerZero(formatValue)
              : formatValue
          }
        }
      }
    }
  },
  methods: {
    // 数字 . 符号验证器
    dotChatValidator (value) {
      if (this.precision === 0 && value.indexOf('.') > -1) {
        return false
      }

      if (value) {
        const splitArr = value.split('.')

        return splitArr[0] !== '' && value.split('.').length <= 2
      }

      return true
    },
    // 小数位前限制验证器
    precisionPrefixLimitValidator (value, limit) {
      value = value.replace(/-/g, '')
      if (limit && limit > 0) {
        const splitArr = value.split('.')
        // 小数位前最多只能输入 ${limit} 位
        return this.handlerValidateError(
          splitArr[0].length <= limit,
          this.$t('components.msgDecimal')[0] +
            ` ${limit} ` +
            this.$t('components.msgDecimal')[1]
        )
      }
      return true
    },
    // 小数位验证器
    precisionValidator (value, precision) {
      const splitArr = value.split('.')
      if (splitArr.length === 2) {
        const precisionNum = splitArr[1]
        return this.handlerValidateError(
          precisionNum.length <= precision,
          this.$t('components.msgDecimal')[2] +
            ` ${precision} ` +
            this.$t('components.msgDecimal')[3]
        )
      }

      return true
    },
    // 负数验证器
    negativeValidator (value, allowNegative) {
      const splitArr = value.split('-')
      if (allowNegative && splitArr[0] === '' && splitArr.length <= 2) {
        return true
      } else {
        return value.indexOf('-') === -1
      }
    },
    // 处理验证错误
    handlerValidateError (expression, errorMessage) {
      if (!expression) {
        if (this.showValidateMessage) {
          this.$message({
            type: 'warning',
            message: errorMessage
          })
        }
        this.$emit('validate-error', errorMessage)
      }
      return expression
    },
    // 处理输入 0 的处理
    handlerZero (value) {
      const notZeroReg = /[1-9]/g
      let formatValue = this.formatValue(value)
      if (!notZeroReg.test(formatValue)) {
        formatValue = '0'
        // 重置正则 lastIndex 值
        notZeroReg.lastIndex = 0
      }
      formatValue = this.replenishPrecision(formatValue, this.precision)

      return comma(formatValue)
    },
    // 补充小数位
    replenishPrecision (value, precision) {
      if (precision === 0) {
        return value
      }

      const splitArr = value.split('.')
      const precisionNum = splitArr[1] || ''
      let str = ''

      for (let i = 0; i < precision; i++) {
        str += precisionNum[i] || 0
      }
      splitArr[1] = str
      return splitArr.join('.')
    },
    formatValue (value) {
      if (!value) value = ''

      return value.replace(/,/g, '')
    },
    // 输入框事件
    handleInput (value) {
      // 格式化千分号
      let formatValue = this.formatValue(value)
      if (
        this.dotChatValidator(formatValue) &&
        this.negativeValidator(formatValue, this.allowNegativeNumbers) &&
        this.precisionPrefixLimitValidator(
          formatValue,
          this.precisionPrefixLimit
        ) &&
        this.precisionValidator(formatValue, this.precision)
      ) {
        // 格式化除数字相关的符号
        formatValue = formatValue.replace(/[^0-9.-]/g, '')

        this.inputValue = comma(formatValue)
        // 清空输入框的时候，返回空不能返回0
        this.$emit('input', formatValue ? Number(formatValue) : '')
      }
    },
    handleFocus () {
      this.focused = true
      this.$emit('focus')
    },
    handleBlur () {
      this.focused = false
      if (this.isReplenishPrecision) {
        this.inputValue = this.handlerZero(this.inputValue)
      }
      const inputValue = this.inputValue
      this.$emit('blur', inputValue ? Number(this.formatValue(inputValue)) : 0)
    }
  }
}
</script>
