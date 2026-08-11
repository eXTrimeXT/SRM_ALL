import * as locale from './locales/index.js'
import * as router from './router.js'
import * as globalComponents from './components/index.js'
import * as globalScope from './global-scope/index.js'
import { resolveRoutes } from './resolve-routes.js'
import patches from './patches/index.js'

// TODO 支持编译产物
// TODO 支持微模块
// TODO auto loader
export default () => {
  return {
    /**
     * 解析路由配置文件
     * 可以在这里新增、修改、删除路由
     */
    resolveRoutes: resolveRoutes,

    getPatchConfigBySchemaKey: patches,

    /**
     * 整个应用初始化钩子，可以在这里
     *  1、自定义全局处理
     *  2、自定义全局状态
     *  3、注入 render-engine 全局作用域
     *  4、挂载全局组件
     *  ...
     */
    created: async (appInstances) => {
      // 全局组件
      globalComponents.install(appInstances.app)

      // 渲染引擎全局作用域，也可以挂载全局状态
      globalScope.install(appInstances)

      if (appInstances.i18n) {
        // 新增国际化
        // 监听国际化
        // 切换国际化
        locale.install(appInstances.i18n, appInstances)
      }

      if (appInstances.router) {
        // 监听路由相关钩子
        router.install(appInstances.router, appInstances)
      }
    },
  }
}
