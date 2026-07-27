<template>
  <div>
<!--    globalization-->
    <div v-if="globalization != 'true'">
      <el-input
        v-if="readOnly === false"
        v-model="internalValue"
        :disabled="disabled"
        :class="className"
        :min="min"
        :max="max"
        @focus="focus"
      >
        <template slot="append">
          <slot name="append"></slot>
        </template>
      </el-input>
      <span v-else>{{ (value && value !== '') ? value : '-' }}</span>
    </div>
    <div v-else>
      <el-input
        v-if="readOnly === false"
        v-model="formattedNum"
        :disabled="disabled"
        :class="className"
        :min="min"
        :max="max"
        @blur="inputBlur"
        @input="inputChange"
        @focus="focus"
      >
        <template slot="append">
          <slot name="append"></slot>
        </template>
      </el-input>
      <span v-else>{{ (formattedNum && formattedNum !== '') ? formattedNum : '-' }}</span>
    </div>
  </div>
</template>

<script>

export default {
  name: 'AmountDisplay',
  inheritAttrs: false,
  components: {},
  props: {
    disabled: {
      type: Boolean,
      default: false
    },
    readOnly: {
      type: Boolean,
      default: false
    },
    value: {
      type: [String, Number],
    },
    className: {
      type: String,
      default: ''
    },
    min: {
      type: Number,
      default: null
    },
    max: {
      type: Number,
      default: 9899999999999999
    }
  },
  data () {
    return {
      formattedNum: '',
      globalization: localStorage.getItem('globalization'),
      internalValue: this.value
    }
  },
  computed: {

  },
  watch: {
    value: {
      deep: true,
      handler (val) {
        if (this.globalization === 'true') {
          this.changeFormat(val)
        }
      }
    },
    internalValue: {
      deep: true,
      handler (newValue) {
        this.$emit('change', newValue)
        this.$emit('input', newValue)
      }
    }
  },
  created () {

  },
  mounted () {
    setTimeout(()=>{
      this.changeFormat(this.value)
    })
  },
  destroy () {

  },
  methods: {
    focus () {
      this.$emit('focus')
    },
    inputBlur () {
      const value = this.formattedNum
      let returnValue = 0
      if (value) {
        const language = localStorage.getItem('cloudNumberFormat').replace("_", "-")
        let number = ''
        if (language === 'ru-RU') {
          number = value.replace(/\s/g, '').replace(/,/g, '.')
        } else {
          number = value.replace(/,/g, '').replace(/\s/g, '')
        }
        returnValue = parseFloat(number)
      } else {
        returnValue = '0'
      }
      this.$emit('change', returnValue)
      this.$emit('input', returnValue)
    },
    inputChange () {
      this.inputBlur()
    },
    changeFormat (val) {
      if (!val || val === '') {
        return false
      }
      const language = localStorage.getItem('cloudNumberFormat').replace("_", "-")
      const formatter = new Intl.NumberFormat(language)
      this.formattedNum = formatter.format(val);
    }
  }
}
</script>

<style lang="scss" scoped>

</style>
