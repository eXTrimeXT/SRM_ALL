<template>
  <el-input
    ref="passwordInput"
    class="passwordInput"
    :value="value"
    v-bind="$attrs"
    :type="passwordType"
    v-on="$listeners"
    @input="handleInput"
    @blur="handleBlur"
  >
    <template slot="suffix">
      <svg-icon
        :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'"
        @click="showPwd"
      />
    </template>
  </el-input>
</template>

<script>
export default {
  name: 'PasswordInput',
  props: {
    value: {
      type: String
    },
    name: {
      type: String
    }
  },
  data () {
    return {
      passwordType: 'password'
    }
  },
  methods: {
    // 显示密码
    showPwd () {
      if (this.passwordType === 'password') {
        this.passwordType = ''
      } else {
        this.passwordType = 'password'
      }
      this.$nextTick(() => {
        this.$refs.passwordInput.focus()
      })
    },
    handleInput (value) {
      this.$emit('input', value)
    },
    handleBlur () {
      this.$emit('blur')
    },
    blur () {
      this.$refs.input.blur()
    }
  }
}
</script>
<style lang="scss">
.passwordInput{
  .el-input__suffix{
    padding: 0 5px;
    cursor: pointer;
  }
}
</style>
