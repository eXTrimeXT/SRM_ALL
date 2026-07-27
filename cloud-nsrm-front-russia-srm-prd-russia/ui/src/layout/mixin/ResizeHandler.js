import store from '@/store'

const { body } = document
const WIDTH = 768 // refer to Bootstrap's responsive design
// breakpoint  768 992 1200 1920

export default {
  watch: {
    $route (route) {
      if (this.device === 'device-xs') {
        if (this.sidebar && this.sidebar.opened) {
          store.dispatch('app/closeSideBar', { withoutAnimation: false })
        }
      }
    }
  },
  beforeMount () {
    window.addEventListener('resize', this.$_resizeHandler)
  },
  beforeDestroy () {
    window.removeEventListener('resize', this.$_resizeHandler)
  },
  mounted () {
    // this.setRooterFontSize() // 暂时注释
    const device = this.$_getDevice()
    store.dispatch('app/toggleDevice', device)
    const isMobile = this.$_isMobile()
    if (isMobile) {
      // store.dispatch('app/toggleDevice', 'mobile')
      store.dispatch('app/toggleDevice', 'device-xs')
      store.dispatch('app/closeSideBar', { withoutAnimation: true })
    }
  },
  methods: {
    // use $_ for mixins properties
    // https://vuejs.org/v2/style-guide/index.html#Private-property-names-essential
    $_isMobile () {
      const rect = body.getBoundingClientRect()
      return rect.width - 1 < WIDTH
    },
    // 设置根字体
    setRooterFontSize (width) {
      let deviceW = width
      if (!width) {
        const rect = body.getBoundingClientRect()
        deviceW = rect.width
      }
      var docEl = document.documentElement
      if (deviceW < 768) {
        const baseFontsize = 16 // 基础字体大小
        let deviceFontsize = deviceW / baseFontsize + 'px'
        docEl.style.fontSize = deviceFontsize
      } else {
        docEl.style.fontSize = '37.5px'
      }
    },
    $_getDevice () {
      // 获取当前设配宽度
      // const xs = 768 // <768px -> device-xs
      // const sm = 768 // ≥768px -> device-sm
      // const md = 992 // ≥992px -> device-md
      // const lg = 1200 // ≥1200px -> device-lg
      // const xl = 1920 // ≥1920px -> device-xl
      // mobile
      const rect = body.getBoundingClientRect()
      let deviceW = rect.width
      // this.setRooterFontSize(deviceW) // 设置跟节点字体 - 暂时注释
      let deviceType = 'device-lg' // 默认PC
      if (deviceW < 768) deviceType = 'device-xs'
      if (deviceW >= 768) deviceType = 'device-sm'
      if (deviceW >= 992) deviceType = 'device-md'
      if (deviceW >= 1200) deviceType = 'device-lg'
      if (deviceW >= 1920) deviceType = 'device-xl'
      return deviceType
    },
    $_resizeHandler () {
      if (!document.hidden) {
        const isMobile = this.$_isMobile()
        const device = this.$_getDevice()
        // store.dispatch('app/toggleDevice', isMobile ? 'mobile' : 'desktop')
        store.dispatch('app/toggleDevice', device)
        if (isMobile) {
          store.dispatch('app/closeSideBar', { withoutAnimation: true })
        }
      }
    }
  }
}
