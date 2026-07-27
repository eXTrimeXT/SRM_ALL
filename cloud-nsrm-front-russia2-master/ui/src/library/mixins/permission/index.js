export default {
  data () {
    const userInfo = this.$store.getters.user.userInfo
    const { buttonPermission = {} } = userInfo
    return { buttonPermission }
  },
  methods: {
    hasPermission (code) {
      // TODO: 正式配置完所有权限之后 需要去掉这个判断
      if (!code) return true
      const permissions = this.buttonPermission
      if (permissions[this.code]) {
        return permissions[this.code] == 'Y'
      } else {
        return true
      }
    }
  }
}
