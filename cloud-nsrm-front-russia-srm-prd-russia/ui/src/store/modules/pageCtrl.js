
const state = {
  iamSecurityPageType: 'updatePassword' // 默认修改密码 iam账号安全页面类型 // updatePassword safety thirdAccount
}

const mutations = {
  // iamSecurityPageType
  SET_IAM_PAGE: (state, type) => {
    state.iamSecurityPageType = type
  }
}

const actions = {
  // iamSecurityPageType
  setIamPage ({ commit }, type) {
    commit('SET_IAM_PAGE', type)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
