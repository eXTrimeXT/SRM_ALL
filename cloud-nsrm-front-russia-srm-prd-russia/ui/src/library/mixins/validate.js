import { focusError, jumpToTarget } from '@/utils'

export default {
  methods: {
    __focus_error__ (message = '请检查是否正确填写', flag = true) {
      focusError.call(this)
      if (flag) this.$message.error(message)
    },
    __jump_error__ (ref, type = 'component', message = '请检查是否正确填写') {
      console.log('[__jump_error__]', ref, type)
      jumpToTarget.call(this, ref, type)
      this.$message.error(message)
    }
  }
}
