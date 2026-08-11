import { focusError, jumpToTarget } from '@/utils'

export default {
  methods: {
    __focus_error__ (message = this.$t('qualitySynergy.checkFilledIn'), flag = true) {
      focusError.call(this)
      if (flag) this.$message.error(message)
    },
    __jump_error__ (ref, type = 'component', message = this.$t('qualitySynergy.checkFilledIn')) {
      console.log('[__jump_error__]', ref, type)
      jumpToTarget.call(this, ref, type)
      this.$message.error(message)
    }
  }
}
