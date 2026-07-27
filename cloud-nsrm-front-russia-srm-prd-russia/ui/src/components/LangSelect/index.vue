<template>
  <el-dropdown
    trigger="click"
    class="international"
    @command="handleSetLanguage"
  >
    <div class="lang">
      <!-- <svg-icon
        class-name="international-icon"
        icon-class="language"
      /> -->
      <i class="iconfont iconyuyan" />
      <span class="lang-text">{{ langShow }}</span>
    </div>
    <el-dropdown-menu slot="dropdown">
      <el-dropdown-item
        :disabled="language === 'zh_CN'"
        command="zh_CN"
      >
        中文
      </el-dropdown-item>
      <el-dropdown-item
        :disabled="language === 'en_US'"
        command="en_US"
      >
        English
      </el-dropdown-item>
      <!-- <el-dropdown-item :disabled="language === 'ja_JP'" command="ja_JP">
        日本語
      </el-dropdown-item> -->
    </el-dropdown-menu>
  </el-dropdown>
</template>

<script>
export default {
  computed: {
    language () {
      return this.$store.getters.language
    },
    langShow () {
      let lang = this.$store.getters.language.substring(0, 2)
      return lang.toLocaleUpperCase()
    }
  },
  methods: {
    handleSetLanguage (lang) {
      this.$i18n.locale = lang
      // const title = this.$route.meta.title
      // document.title = this.$t(title)
      //   this.$store.dispatch("app/setLanguage", lang); // 设置语言
      //   this.$store.dispatch("user/initSystem"); // 重新获取用户信息
      this.$store.dispatch('app/setLanguage', lang) // 设置语言
      this.refreshSelectedTag(this.$route) // 刷新的时候有重新拿用户数据
    },
    // 刷新
    refreshSelectedTag (view) {
      this.$store.dispatch('tagsView/delCachedView', view).then(() => {
        const { fullPath } = view
        this.$nextTick(() => {
          this.$router.replace({
            path: '/redirect' + fullPath
          })
        })
      })
    }
  }
}
</script>
<style scoped lang="scss">
.international {
  height: 100%;
  .lang {
    height: 100%;
    display: flex;
    align-items: center;
    i {
      margin-right: 2px;
      font-size: 25px;
    }
    span {
      font-size: 14px;
    }
  }
}
</style>
