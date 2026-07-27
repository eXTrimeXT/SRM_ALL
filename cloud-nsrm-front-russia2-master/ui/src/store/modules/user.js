import {
  login,
  loginFace,
  logout,
  getUserInfo,
  getCompanyInfo
} from '@/api/user'
import {
  getToken,
  setToken,
  setUserId,
  setName,
  setCompanyId,
  getUserId,
  getCompanyId,
  removeToken,
  removeName,
  removeUserID,
  clearCookie,
  setEntranceType,
  getEntranceType
} from '@/utils/auth'
import { mutations as store } from 'lib@/components/organization-cascader/store'
import Vue from 'vue'

const state = {
  token: getToken() || '',
  username: '',
  userType: '', // 用户类型
  userId: getUserId() || '',
  avatar: '',
  nickeyName: '',
  slideMenu: [],
  companyId: getCompanyId() || '',
  userInfo: {},
  userTime: 0,
  entrance: getEntranceType(), // inside 内部登录方式 || singlePoint 单点登录方式
  showMyFrame: false,
  isFirstClick: true,
  loadingFlag: false,
  expand: false,
  showPreview: false,
  compareData: '',
  fullSize: false,
  identifyLoad: false
}

const mutations = {
  // showMyFrame
  SET_FRAME: (state, showMyFrame) => {
    state.showMyFrame = showMyFrame
  },
  // isFirstClick
  SET_FIRST_CLICK: (state, isFirstClick) => {
    state.isFirstClick = isFirstClick
  },
  // loadingFlag
  SET_LOAD_FLAG: (state, loadingFlag) => {
    state.loadingFlag = loadingFlag
  },
  SET_EXPAND: (state, expand) => {
    state.expand = expand
  },
  SET_FULLSIZE: (state, fullSize) => {
    state.fullSize = fullSize
  },
  SET_SHOW_PREVIEW: (state, showPreview) => {
    state.showPreview = showPreview
  },
  // 存储对比文件数据
  SET_COMPARE_DATA: (state, compareData) => {
    state.compareData = compareData
  },
  SET_IDENTIFY_LOAD: (state, identifyLoad) => {
    state.identifyLoad = identifyLoad
  },

  // 设置token
  SET_TOKEN: (state, token) => {
    state.token = token
  },
  // 设置用户名
  SET_NAME: (state, username) => {
    state.username = username
  },
  // 设置用户ID
  SET_USERID: (state, userId) => {
    state.userId = userId
  },
  // 设置用户头像
  SET_AVATAR: (state, avatar) => {
    state.avatar = avatar
  },
  // 设置路由
  SET_ROUTES: (state, routes) => {
    state.slideMenu = routes
  },
  // 公司ID
  SET_COMPANYID: (state, companyId) => {
    state.companyId = companyId
  },
  // 用户信息
  SET_USER_INFO: (state, userInfo) => {
    state.userInfo = userInfo
  },
  // 用户类型
  SET_USER_TYPE: (state, userType) => {
    state.userType = userType
  },
  // 刷新用户信息次数
  SET_USER_TIME: (state, userTime) => {
    state.userTime = state.userTime + userTime
  },
  // 设置用户登录方式
  SET_ENTRANCE: (state, entrance) => {
    state.entrance = entrance
  }
}

const actions = {
  loginFace ({ commit }, userInfo) {
    sessionStorage.clear()
    const { username, faceFileBase64 } = userInfo
    return new Promise((resolve, reject) => {
      loginFace({
        username: username.trim(),
        faceFileBase64
      })
        .then(response => {
          const { data } = response
          commit('SET_TOKEN', data.value)
          setToken(data.value)
          resolve(response)
        })
        .catch(error => {
          reject(error)
        })
    })
  },
  // user login
  login ({ commit }, userInfo) {
    sessionStorage.clear()
    const { username, password, verifyCode } = userInfo
    return new Promise((resolve, reject) => {
      login({
        username: username.trim(),
        password: password,
        verifyCode: verifyCode
      })
        .then(response => {
          const { data } = response
          commit('SET_ENTRANCE', 'inside')
          setEntranceType('inside')
          commit('SET_TOKEN', data.value)
          setToken(data.value)
          resolve(response)
        })
        .catch(error => {
          reject(error)
        })
    })
  },
  // 调用iam注册以后返回token
  loginWithToken ({ commit }, token) {
    return new Promise((resolve, reject) => {
      commit('SET_ENTRANCE', 'singlePoint')
      setEntranceType('singlePoint')
      commit('SET_TOKEN', token)
      setToken(token)
      resolve(token)
    })
  },

  // user scan login
  scanLogin ({ commit }, response) {
    sessionStorage.clear()
    return new Promise((resolve, reject) => {
      const { data } = response
      commit('SET_ENTRANCE', 'inside')
      setEntranceType('inside')
      commit('SET_TOKEN', data.value)
      setToken(data.value)
      resolve(response)
    })
  },

  // 加载用户信息
  getUserInfo ({ commit, state, dispatch }) {
    return new Promise((resolve, reject) => {
      getUserInfo(state.token)
        .then(response => {
          const { data } = response
          if (!data) {
            console.log('用户未登录')
          }
          // headImgUrl
          const { nickname, userId, companyId, username, userType } = data
          commit('SET_USERID', userId)
          commit('SET_NAME', nickname || username)
          commit('SET_COMPANYID', companyId)
          commit('SET_USER_TYPE', userType)
          commit('SET_USER_INFO', data)
          // setName(nickname || username);
          setUserId(data.userId)
          if (companyId) {
            setCompanyId(companyId)
          }

          dispatch('secret/initCurrentKey', {}, { root: true })

          resolve(data)
        })
        .catch(error => {
          reject(error)
          commit('SET_USERID', null)
          commit('SET_NAME', null)
          commit('SET_COMPANYID', null)
          commit('SET_USER_TYPE', null)
          commit('SET_USER_INFO', {})
        })
    })
  },

  // 加载公司状态
  getCompanyStatusInfo ({ commit, state }) {
    return new Promise((resolve, reject) => {
      getCompanyInfo(state.companyId)
        .then(response => {
          resolve(response.data)
        })
        .catch(error => {
          reject(error)
        })
    })
  },

  // 退出
  getLogout ({ commit, state, dispatch }) {
    if (!state.token) return Promise.resolve(true)
    return new Promise((resolve, reject) => {
      logout({ accessToken: state.token })
        .then(res => {
          commit('SET_TOKEN', '')
          commit('SET_USER_INFO', {})
          clearCookie()
          // 删除所有打开的页面标签
          dispatch('tagsView/delAllViews', null, { root: true })
          // 退出时清掉流程相关数据
          let currentFrameName = localStorage.getItem('currentActiveFlow')
          localStorage.removeItem(currentFrameName)
          resolve(res)
        })
        .catch(error => {
          reject(error)
        })
    })
  },

  // 删除token
  resetToken ({ commit, dispatch }) {
    return new Promise(resolve => {
      commit('SET_TOKEN', '')
      commit('SET_USER_INFO', {})
      clearCookie()
      // 删除所有打开的页面标签
      dispatch('tagsView/delAllViews', null, { root: true })
      // 退出时清掉流程相关数据
      let currentFrameName = localStorage.getItem('currentActiveFlow')
      localStorage.removeItem(currentFrameName)
      resolve()
    })
  },

  /**
   * --初始化系统信息--
   * 获取用户信息
   * 获取菜单信息
   */
  initSystem ({ commit, dispatch }) {
    return new Promise((resolve, reject) => {
      getUserInfo().then(res => {
        if (res) {
          // 用户信息
          const userInfo = res.data
          // 保存时间格式配置与数字配置
          // 判断是否开启多时区模式
          const globalization = localStorage.getItem('globalization')
          if (globalization == 'true') { // 是否开启多时区
            localStorage.setItem('cloudTimeZoneFormat', userInfo.cloudTimeZoneFormat)
            localStorage.setItem('cloudDateFormat', userInfo.cloudDateFormat)
            localStorage.setItem('cloudDateTimeFormat', userInfo.cloudDateTimeFormat)
            localStorage.setItem('cloudNumberFormat', userInfo.cloudNumberFormat)
            Vue.prototype.$formatDatePicker = localStorage.getItem('cloudDateFormat') + `  [${localStorage.getItem('cloudTimeZoneFormat')}]`
            Vue.prototype.$formatDatePickerTime = localStorage.getItem('cloudDateTimeFormat') + `  [${localStorage.getItem('cloudTimeZoneFormat')}]`
          } else {
            localStorage.setItem('cloudTimeZoneFormat', 'GTM+3')
            localStorage.setItem('cloudDateFormat', 'dd.MM.yyyy')
            localStorage.setItem('cloudDateTimeFormat', 'dd.MM.yyyy HH:mm:ss')
            localStorage.setItem('cloudNumberFormat', userInfo.cloudNumberFormat || 'ru_RU')
            Vue.prototype.$formatDatePicker = 'dd.MM.yyyy [GTM+3]'
            Vue.prototype.$formatDatePickerTime = 'dd.MM.yyyy HH:mm:ss [GTM+3]'
          }
          const { nickname, username, userId, userType, companyId, cloudLocale = 'zh_CN' } = userInfo
          commit('SET_NAME', nickname || username) // 用户昵称
          commit('SET_USERID', userId)
          commit('SET_COMPANYID', companyId)
          commit('SET_USER_TYPE', userType)
          commit('SET_USER_INFO', userInfo)
          commit('SET_USER_TIME', 1)
          // 调用app模块设置语言信息
          dispatch('app/setLang', cloudLocale, { root: true })
          setUserId(userId)
          if (companyId) {
            setCompanyId(companyId)
          }
          // 初始化组织树
          store.fetchTree()
          store.fetchTreeWithFullPathId()
        }

        dispatch('secret/initCurrentKey', {}, { root: true })
        resolve(res)
      }).catch(error => {
        // 调用app模块设置语言信息,报错设置语言
        dispatch('app/setLang', 'zh_CN', { root: true })
        commit('SET_USERID', null)
        commit('SET_NAME', null)
        commit('SET_COMPANYID', null)
        commit('SET_USER_TYPE', null)
        commit('SET_USER_INFO', {})
        reject(error)
      })
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
