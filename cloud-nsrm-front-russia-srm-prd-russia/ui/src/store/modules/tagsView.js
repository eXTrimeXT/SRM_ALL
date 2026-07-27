import lang from '@/lang'

const charAt0ToUpperCase = (char) => {
  return char.charAt(0).toUpperCase() + char.slice(1)
}

const state = {
  visitedViews: [
    // 默认首页一直显示
    {
      fullPath: '/dashboard',
      hash: '',
      name: 'dashboard',
      path: '/dashboard',
      title: lang.t('route.dashboard'),
      query: {},
      meta: {
        affix: true,
        icon: 'dashboard',
        title: 'route.dashboard'
      }
    }
  ],
  // 首页默认缓存
  cachedViews: ['Dashboard']
}
// 没有配菜单的对应显示的名字 配置对应的语言词条
// 针对配白名单的菜单显示
const noName = {
  accountSecurity: 'route.accountSecurity',
  profile: 'route.profile',
  Exception401: '401',
  Exception402: '402',
  Exception404: '404',
  Exception500: '500',
  categorySourcing: '品类寻源演示',
  yearReductionReport: '年降达成',
  monthReductionReport: '月降达成',
  monitoringReport: '品类寻源监控报表',
  dashboardDemo: '首页演示页面',
  cateReport: '寻源监控报表',
  orderProgressReport: '采购计划执行报表',
  flowDemo: '流程、条件配置',
  vxeTableDemo: '开发demo',
  conditionDemo: '条件配置'
}

const mutations = {
  ADD_VISITED_VIEW: (state, view) => {
    if (typeof view.meta.visited === 'boolean' && !view.meta.visited) {
      // 路由元信息配置visited: false
      return
    }
    if (state.visitedViews.some(v => v.fullPath === view.fullPath)) {
      return
    }
    if (noName[view.name]) { // 添加判断
      view.meta.title = noName[view.name]
    }
    state.visitedViews.push(
      Object.assign({}, view, {
        title: view.meta.title || 'no-name'
      })
    )
  },
  ADD_CACHED_VIEW: (state, view) => {
    if (typeof view.meta.cached === 'boolean' && !view.meta.cached) {
      // 路由元信息配置cached: false
      return
    }
    const name = charAt0ToUpperCase(view.name)
    if (state.cachedViews.includes(name)) return

    if (!view.meta.noCache) {
      // 根据组件首字母大写原则，强制格式化所有组件名称为首字母大写缓存
      state.cachedViews.push(name)
    }
  },

  DEL_VISITED_VIEW: (state, view) => {
    for (const [i, v] of state.visitedViews.entries()) {
      if (v.fullPath === view.fullPath) {
        state.visitedViews.splice(i, 1)
        break
      }
    }
  },
  DEL_CACHED_VIEW: (state, view) => {
    // 根据组件首字母大写原则，强制格式化所有组件名称为首字母大写缓存
    const index = state.cachedViews.indexOf(charAt0ToUpperCase(view.name))
    index > -1 && state.cachedViews.splice(index, 1)
  },

  DEL_OTHERS_VISITED_VIEWS: (state, view) => {
    state.visitedViews = state.visitedViews.filter(v => {
      return v.meta.affix || v.fullPath === view.fullPath
    })
  },
  DEL_OTHERS_CACHED_VIEWS: (state, view) => {
    const index = state.cachedViews.indexOf(charAt0ToUpperCase(view.name))
    if (index > -1) {
      state.cachedViews = state.cachedViews.slice(index, index + 1)
    } else {
      // if index = -1, there is no cached tags
      state.cachedViews = []
    }
  },

  DEL_ALL_VISITED_VIEWS: state => {
    // keep affix tags
    const affixTags = state.visitedViews.filter(tag => tag.meta.affix)
    state.visitedViews = affixTags
  },
  DEL_ALL_CACHED_VIEWS: state => {
    state.cachedViews = []
  },

  UPDATE_VISITED_VIEW: (state, view) => {
    for (let v of state.visitedViews) {
      if (v.fullPath === view.fullPath) {
        v = Object.assign(v, view)
        break
      }
    }
  },

  // 更新全部的标签页, 覆盖性更新
  UPDATE_ALL_VISITED_VIEWS: (state, views) => {
    state.visitedViews = views || state.visitedViews.filter(tag => tag.meta.affix)
  },

  UPDATE_ALL_CACHED_VIEWS: (state, views) => {
    state.cachedViews = (views || state.visitedViews.filter(tag => tag.meta.affix))
      .map(item => charAt0ToUpperCase(item.name))
  }
}

const actions = {
  addView ({ dispatch, state }, view) {
    dispatch('addVisitedView', view)
    dispatch('addCachedView', view)
  },
  addVisitedView ({ commit }, view) {
    commit('ADD_VISITED_VIEW', view)
  },
  addCachedView ({ commit }, view) {
    commit('ADD_CACHED_VIEW', view)
  },

  delView ({ dispatch, state }, view) {
    return new Promise(resolve => {
      dispatch('delVisitedView', view)
      dispatch('delCachedView', view)
      resolve({
        visitedViews: [...state.visitedViews],
        cachedViews: [...state.cachedViews]
      })
    })
  },
  delVisitedView ({ commit, state }, view) {
    return new Promise(resolve => {
      commit('DEL_VISITED_VIEW', view)
      resolve([...state.visitedViews])
    })
  },
  delCachedView ({ commit, state }, view) {
    return new Promise(resolve => {
      commit('DEL_CACHED_VIEW', view)
      resolve([...state.cachedViews])
    })
  },

  delOthersViews ({ dispatch, state }, view) {
    return new Promise(resolve => {
      dispatch('delOthersVisitedViews', view)
      dispatch('delOthersCachedViews', view)
      resolve({
        visitedViews: [...state.visitedViews],
        cachedViews: [...state.cachedViews]
      })
    })
  },
  delOthersVisitedViews ({ commit, state }, view) {
    return new Promise(resolve => {
      commit('DEL_OTHERS_VISITED_VIEWS', view)
      resolve([...state.visitedViews])
    })
  },
  delOthersCachedViews ({ commit, state }, view) {
    return new Promise(resolve => {
      commit('DEL_OTHERS_CACHED_VIEWS', view)
      resolve([...state.cachedViews])
    })
  },

  delAllViews ({ dispatch, state }, view) {
    return new Promise(resolve => {
      dispatch('delAllVisitedViews', view)
      dispatch('delAllCachedViews', view)
      resolve({
        visitedViews: [...state.visitedViews],
        cachedViews: [...state.cachedViews]
      })
    })
  },
  delAllVisitedViews ({ commit, state }) {
    return new Promise(resolve => {
      commit('DEL_ALL_VISITED_VIEWS')
      resolve([...state.visitedViews])
    })
  },
  delAllCachedViews ({ commit, state }) {
    return new Promise(resolve => {
      commit('DEL_ALL_CACHED_VIEWS')
      resolve([...state.cachedViews])
    })
  },

  updateVisitedView ({ commit }, view) {
    commit('UPDATE_VISITED_VIEW', view)
  },

  // 更新全部的标签页, 覆盖性更新
  updateAllVisitedView ({ commit }, views) {
    commit('UPDATE_ALL_VISITED_VIEWS', views)
    commit('UPDATE_ALL_CACHED_VIEWS', views)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
