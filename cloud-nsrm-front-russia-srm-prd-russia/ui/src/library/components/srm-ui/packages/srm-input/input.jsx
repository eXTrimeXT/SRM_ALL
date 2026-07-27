export default {
  name: 'SrmInput',
  computed: {
    // 占位符
    placeholder () {
      if (this.$attrs.placeholder) {
        return this.$attrs.placeholder
      } else {
        return this.$t('common.pleaseInput')
      }
    },
    // 文字长度
    maxlength () {
      if (this.$attrs.type === 'textarea') {
        return 1000
      }
      if (this.$attrs.type === 'text') {
        return 50
      }
      return 50
    },
    showWordLimit () {
      if (this.$attrs.showWordLimit) {
        return this.$attrs.showWordLimit
      } else {
        return true
      }
    }
  },
  render (h) {
    return (
      <ElInput on={this.$listeners} attrs={this.$attrs} placeholder={this.placeholder} showWordLimit={this.showWordLimit} maxlength={this.maxlength}>
        {Object.entries(this.$slots).map(([name, slot]) => (
          <template slot={name}>{slot}</template>
        ))}
      </ElInput>
    )
  }
}
