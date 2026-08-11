import variables from '@/styles/element-variables.scss?inline'
import defaultSettings from '@/settings'

const { showSettings, tagsView, fixedHeader, sidebarLogo, isPC } = defaultSettings

const state = {
  theme: variables.theme,
  showSettings: showSettings,
  tagsView: tagsView,
  fixedHeader: fixedHeader,
  sidebarLogo: sidebarLogo,
  isPC: isPC
}

const mutations = {
  CHANGE_SETTING: (state, { key, value }) => {
    if (state.hasOwnProperty(key)) {
      state[key] = value
    }
  },
  CHANGE_ISPC: (state, data) => {
    state.isPC = data
  }
}

const actions = {
  changeSetting ({ commit }, data) {
    commit('CHANGE_SETTING', data)
  },
  changeIsPC ({ commit }, data) {
    commit('CHANGE_ISPC', data)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
