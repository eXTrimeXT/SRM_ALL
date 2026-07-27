<template>
  <img v-if="themeType=='img'" :src="computedSrc" alt="" />
  <span v-else>
    {{ computedText }}
  </span>
</template>
<script>
import { mapState } from 'vuex'
import Cookies from 'js-cookie'
export default {
  // 系统主题配置 logo | 系统文字
  name: 'ThemeConf',
  props: {
    themeType: { // 图片类型 img | 文字类型 text
      type: String,
      default: ''
    },
    // 图片类型
    imgType: { // mainLogo | subLogo | menuMainLogo | menuSubLogo
      type: String,
      default: ''
    },
    // isConfig 为false 外面传进来的值
    themeSrc: {
      type: String,
      default: ''
    },
    textType: { // webName | webDes | webTitle
      type: String,
      default: ''
    },
    // isConfig 为false 外面传进来的值
    themeText: {
      type: String,
      require: true,
      default: ''
    },
    // 是否读取的配置数据
    isConfig: {
      type: Boolean,
      default: true
    }
  },
  computed: {
    ...mapState({
      systemTheme: state => {
        return state.app.systemTheme
      }
    }),
    computedText () {
      let text = ''
      let language = Cookies.get('language') || 'zh_CN'
      // 自动取配置信息
      if (this.textType && this.isConfig) {
        switch (this.textType) {
          case 'webName':
            text = this.systemTheme.webName[language]
            break
          case 'webDes':
            text = this.systemTheme.webDes[language]
            break
          case 'webTitle':
            text = this.systemTheme.webTitle[language]
            break
        }
        return text
      } else {
        return this.themeText
      }
    },
    computedSrc () {
      let imgSrc = ''
      // 自动取配置信息
      if (this.imgType && this.isConfig) {
        switch (this.imgType) {
          case 'mainLogo':
            imgSrc = this.systemTheme.mainLogo
            break
          case 'subLogo':
            imgSrc = this.systemTheme.subLogo
            break
          case 'menuMainLogo':
            imgSrc = this.systemTheme.menuMainLogo
            break
          case 'menuSubLogo':
            imgSrc = this.systemTheme.menuSubLogo
            break
        }
        console.log('imgSrc', imgSrc)
        return imgSrc
      } else {
        return this.themeSrc
      }
    }
  }
}
</script>
<style lang="scss" scope>
  .themConfig{
    img{
      border: 0;
    }
  }
</style>
