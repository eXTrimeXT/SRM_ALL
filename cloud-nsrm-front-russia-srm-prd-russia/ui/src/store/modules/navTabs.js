const state = {
  tabTodo: {
    name: null, // 组件名
    methods: null, // 方法名
    params: null // 参数
  }
}
const mutations = {
  SET_NAV_TABS_TODO: (state, data) => {
    state.tabTodo = data
  }
}
const actions = {}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
