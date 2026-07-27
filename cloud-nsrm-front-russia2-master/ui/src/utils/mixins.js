/*
 * @Author: tim
 * @Date: 2019-07-24 18:45:46
 * @LastEditors: tim
 * @LastEditTime: 2019-08-12 18:07:54
 * @Description:
 */
import { mapState } from 'vuex'

// 设置 __tabTodo 值
export const tabTodoMixin = {
  methods: {
    // 触发事件
    __setTabTodo (data) {
      if (Object.prototype.toString.call(data) === '[object String]') {
        // 组件名.方法名.传参
        let [name, methods, params] = data.split('.')
        name = name.replace(name[0], name[0].toUpperCase())
        data = {
          name,
          methods,
          params
        }
      }
      // data.random = Math.random() // 随机数, 触发改变
      data.random = window.crypto.getRandomValues(new Uint8Array(1)) * 0.001
      this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
    },
    // 关闭页面
    __closeTagsView (route) {
      let tagsView = {
        name: 'tagsView',
        methods: 'closeSelectedTag',
        params: route || this.$route
      }
      this.__setTabTodo(tagsView)
    }
  }
}
// 监听 __tabTodo 值
export const tabTodoWatch = {
  computed: {
    ...mapState({
      __tabTodo: state => state.navTabs.tabTodo
    })
  },
  watch: {
    __tabTodo: function (data) {
      if (data) {
        let { name, methods, params } = data
        if (
          name === this.$options.name &&
          typeof this[methods] === 'function'
        ) {
          this[methods](params) // 执行方法
        }
      }
    }
  }
}
// 设置 state.params.__data 值
export const pageParamsMixin = {
  methods: {
    __setParams (data) {
      this.$store.commit('PARAMS_VALUE', data)
    }
  }
}
// 监听 state.params.__data 值
export const pageParamsWatch = {
  computed: {
    ...mapState({
      __data: state => state.params.__data
    })
  },
  methods: {
    __clearParams () {
      this.$store.commit('PARAMS_VALUE', null)
    }
  },
  watch: {
    __data: function (data) {
      if (data) {
        let { name, methods, params } = data
        // console.log('page options:', this.$options.name, name)
        if (
          name === this.$options.name &&
          typeof this[methods] === 'function'
        ) {
          this[methods](params)
        }
      }
    }
  }
}
