import Vue from 'vue'
import Vuex from 'vuex'
import getters from './getters'
// import CommonConfig from '@/config/common-config'

Vue.use(Vuex)
/// ///////////////
/// //////////////

// https://webpack.js.org/guides/dependency-management/#requirecontext
// const modulesFiles = require.context('./modules', true, /\.js$/)

// // you do not need `import app from './modules/app'`
// // it will auto require all vuex module from modules file

const modulesFiles = import.meta.glob('./modules/*.js', { eager: true })

const modules = Object.keys(modulesFiles).reduce(
  (modules, modulePath) => {
    // set './modules/app.js' => 'app'
    const moduleName = modulePath.replace(/^\.\/modules\/(.*)\.\w+$/, '$1')

    return { ...modules, [moduleName]: modulesFiles[modulePath].default }
  },
  {}
)

// for (var key in CommonConfig) {
//   var item = CommonConfig[key]
//   modules[item['STORE_PREFIX']] = item['STORE_JSON']
// }

const store = new Vuex.Store({
  modules,
  getters
})

export default store
