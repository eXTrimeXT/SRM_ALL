import Cookies from 'js-cookie'
import i18n from '@/lang'
import { initLanguage } from '@/lang/index'
import { getLang, modifyLang } from '@/api/user'
import { getOpenConfigBeforeLogin, getSystemTheme } from '@/api/common'
import { systemThemeFormat, systemThemeFromServe, getLogoInfo } from '@/config/logo-config'

const state = {
  appRegisterCode: '', // 注册应用
  sidebar: {
    opened: Cookies.get('sidebarStatus')
      ? !!+Cookies.get('sidebarStatus')
      : true,
    withoutAnimation: false
  },
  navCollapse: {
    opened: Cookies.get('navCollapseStatus')
      ? !!+Cookies.get('navCollapseStatus')
      : true
  },
  device: 'desktop',
  size: Cookies.get('size') || 'medium',
  language: initLanguage(),
  // 支持的语言列表，
  languageList: [
    {
      label: '中文',
      value: 'zh_CN'
    },
    {
      label: 'English',
      value: 'en_US'
    }
  ],
  loginFailure: false,
  sysHeaderHeight: '',
  modelConfig: '', // 供应商模块模板配置优化缓存
  modelData: '', // 供应商模块模板数据优化缓存
  sysOpenConfig: {}, // 系统公共配置后端返回
  systemTheme: {}
}

const mutations = {
  APP_REGISTER: (state, regCode) => {
    state.appRegisterCode = regCode
  },
  TOGGLE_SIDEBAR: state => {
    state.sidebar.opened = !state.sidebar.opened
    state.sidebar.withoutAnimation = false
    if (state.sidebar.opened) {
      Cookies.set('sidebarStatus', 1)
      document.querySelector('#app .main-container').style.marginLeft = '190px'
    } else {
      Cookies.set('sidebarStatus', 0)
      document.querySelector('#app .main-container').style.marginLeft = '52px'
    }
  },
  TOGGLE_NAV: state => {
    state.navCollapse.opened = !state.navCollapse.opened
    if (state.navCollapse.opened) {
      Cookies.set('navCollapseStatus', 1)
    } else {
      Cookies.set('navCollapseStatus', 0)
    }
  },
  CLOSE_SIDEBAR: (state, withoutAnimation) => {
    Cookies.set('sidebarStatus', 0)
    state.sidebar.opened = false
    state.sidebar.withoutAnimation = withoutAnimation
  },
  TOGGLE_DEVICE: (state, device) => {
    state.device = device
  },
  SETLOGINFAILURE: (state, loginFailure) => {
    state.loginFailure = loginFailure
  },
  SET_SIZE: (state, size) => {
    state.size = size
    Cookies.set('size', size)
  },
  // 设置语言
  SET_LANGUAGE: (state, language) => {
    state.language = language
    i18n.locale = language
    Cookies.set('language', language)
  },
  SET_SYS_HEADER_HEIGHT: (state, height) => {
    state.sysHeaderHeight = height
  },
  // 设置modelConfig
  SET_MODEL_CONFIG: (state, modelConfig) => {
    state.modelConfig = modelConfig
  },
  // 设置modelData
  SET_MODEL_DATA: (state, modelData) => {
    state.modelData = modelData
  },
  // 设置sysOpenConfig
  SET_SYS_OPEN_CONFIG: (state, sysOpenConfig) => {
    state.sysOpenConfig = sysOpenConfig
  },
  // 系统主题 systemTheme
  SET_SYSTEM_THEME: (state, systemTheme) => {
    state.systemTheme = systemTheme
    // JSON.parse(JSON.stringify(this.originModel))
    sessionStorage.setItem('systemTheme', JSON.stringify(systemTheme))
  }
}

const actions = {
  appRegisterFn ({ commit }, toReg) {
    commit('APP_REGISTER', toReg)
  },
  toggleSideBar ({ commit }) {
    commit('TOGGLE_SIDEBAR')
  },
  toggleNav ({ commit }) {
    commit('TOGGLE_NAV')
  },
  closeSideBar ({ commit }, { withoutAnimation }) {
    commit('CLOSE_SIDEBAR', withoutAnimation)
  },
  toggleDevice ({ commit }, device) {
    commit('TOGGLE_DEVICE', device)
  },
  setSize ({ commit }, size) {
    commit('SET_SIZE', size)
  },
  // 获取服务端语言类型
  getServeLang ({ commit }) {
    return new Promise((resolve, reject) => {
      getLang().then(res => {
        commit('SET_LANGUAGE', res.data || 'zh_CN')
        resolve(res.data)
      }).catch(error => {
        // 接口报错也设置默认语言
        commit('SET_LANGUAGE', 'zh_CN')
        reject(error)
      })
    })
  },
  // 设置语言类型
  setLanguage ({ commit }, language) {
    return new Promise((resolve, reject) => {
      modifyLang({ locale: language })
        .then(res => {
          commit('SET_LANGUAGE', language)
          resolve(res)
        }).catch(error => {
          i18n.locale = language
          reject(error)
        })
    })
  },
  // 设置语言
  setLang ({ commit }, language) {
    commit('SET_LANGUAGE', language)
  },
  setHeaderHeight ({ commit }, height) {
    commit('SET_SYS_HEADER_HEIGHT', height)
  },
  // 系统设置
  setSysOpenConfig ({ commit }) {
    return new Promise((resolve, reject) => {
      getOpenConfigBeforeLogin().then(res => {
        // registerVersion 默认旧版(弹框)
        const { iamBaseUrl, iamContextPath = '', registerVersion = 'old', supplierAutoAuth = false, ...rest } = res.data
        let iamSysBaseUrl = iamBaseUrl + iamContextPath
        commit('SET_SYS_OPEN_CONFIG', {
          iamBaseUrl,
          iamContextPath,
          iamSysBaseUrl,
          registerVersion,
          supplierAutoAuth,
          ...rest
        })
        resolve(res.data)
      }).catch(error => {
        reject(error)
      })
    })
  },
  /**
   * 系统主题
  */
  setSystemTheme ({ commit }) {
    return new Promise((resolve, reject) => {
      let systemTheme = sessionStorage.getItem('systemTheme')
      let systemThemeJson = JSON.parse(systemTheme)
      // 取服务配置
      if (systemThemeFromServe) {
        // 先读本地缓存
        if (systemTheme) {
          commit('SET_SYSTEM_THEME', systemThemeJson)
          resolve(systemThemeJson)
        } else {
          getSystemTheme().then(res => {
            let themeObj = systemThemeFormat(res.data)
            console.log('themeObj')
            console.log(themeObj)
            commit('SET_SYSTEM_THEME', themeObj)
            resolve(themeObj)
          }).catch(error => {
            reject(error)
          })
        }
      } else { // 取代码配置
        let logoInfo = getLogoInfo()
        commit('SET_SYSTEM_THEME', logoInfo)
        resolve(systemThemeJson)
      }
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
