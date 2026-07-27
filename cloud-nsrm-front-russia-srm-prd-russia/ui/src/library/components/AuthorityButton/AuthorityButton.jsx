export default {
  name: 'AuthorityButton',
  props: {
    code: {
      type: String,
      default: null
    }
  },
  data () {
    const userInfo = this.$store.getters.user.userInfo
    const { buttonPermission = [] } = userInfo
    return { buttonPermission }
  },
  methods: {
    hasPermission () {
      // TODO: 正式配置完所有权限之后 需要去掉这个判断
      if (!this.code) return true
      const permissions = this.buttonPermission
      if (permissions[this.code]) {
        return permissions[this.code] == 'Y' // permissions.findIndex(i => i === this.code) > -1
      } else {
        return true
      }
    }
  },
  render (h) {
    const isShow = this.hasPermission()
    return isShow ? (
      <ElButton on={this.$listeners} attrs={this.$attrs} code={this.code}>
        {Object.entries(this.$slots).map(([name, slot]) => (
          <template slot={name}>{slot}</template>
        ))}
      </ElButton>
    ) : null
  }
}
