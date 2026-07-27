
const state = {
  attPercent: '' // 供应商填写附件组件进度

}

const mutations = {
  // 设置attPercent
  SET_ATT_PC: (state, attPercent) => {
    state.attPercent = attPercent
  }
}

const actions = {
  // setAttPercent
  setAttPercent ({ commit }, attPercent) {
    commit('SET_TOKEN', attPercent)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
