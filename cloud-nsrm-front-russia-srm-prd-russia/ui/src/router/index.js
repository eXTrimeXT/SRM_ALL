import { changeFavicon, getSystemTheme } from '@/config/logo-config'
import { isPortalSourcing } from '@/config/sysConfig'
import { getRedirectUrl, getToken, removeRedirectUrl } from '@/utils/auth' // get token from cookie
import getPageTitle from '@/utils/get-page-title'
import NProgress from 'nprogress' // progress bar
import 'nprogress/nprogress.css' // progress bar style
import Vue from 'vue'
import Router from 'vue-router'
import routes from './routes'

Vue.use(Router)
NProgress.configure({ showSpinner: false }) // NProgress 配置

/* Router Modules */
export const constantRoutes = routes

const createRouter = (_routes) =>
  new Router({
    // mode: 'history', // require service support
    mode: 'hash',
    routes: _routes ?? routes,
    scrollBehavior: () => ({ y: 0 })
  })

const router = createRouter()
// 设置白名单
const whiteList = [
  '/login',
  '/registered',
  '/forgetPassword',
  '/find', // 单点登录跳转页面
  '/exception',
  '/exception/404',
  '/redirect',
  '/userProtocol',
  '/privacyProtocol',
  '/saasUserProtocol',
  '/saasPrivacyProtocol',
  '/onlyoffice',
  '/noUser',
  '/page401',
  '/portal',
  '/portalBidding/vendorBiddingDetail',
  '/sourcing/sourcingApplicationDetail', // 用户门户-寻源详情
  '/baseSetting/accessFlowSetting_backups',
  '/demoCarBuyer/demoSetBuyerCar1',
  // '/competition/competitionManageBuyer', // 竞价项目管理(新)
  // '/competitionSupplier/competitionManageVendor' // 竞价项目管理(新)
]

// 一下这些页面在没有token时页面刷新需要跳回登录页
const tokenWhiteList = [
  '/userManage/accountSecurity',
  '/mobile',
  '/mobileList',
  '/agentCenter/approval',
  '/document',
  '/pdfPrint',
  '/userManage/profile',
  '/userManage/resetPwd', // 密码过期，重置密码
  '/baseSettingCommon/dynamicReportPage',
  '/demo/flowDemo', // 学习用
  '/demo/conditionDemo',
  '/demo/demoSet', // 学习用
  '/demo/demoOrderTab', // 学习用
  '/demo/demoOrderPop', // 学习用
  '/demo/demoOrderAppend', // 学习用
  '/demo/demoOrderMultiple', // 学习用
  '/demo/dashboardDemo', // 新工作台demo
  '/demo/vxeTableDemo', // vxe-table demo
  '/baseSetting/multipleVideo', // 多人视频
  '/demoCus/demoSetNew',
  '/baseSetting/tableDemo',
  '/demo/orderProgressReport',
  '/demo/categorySourcing',
  '/demo/cateReport',
  '/yearReductionReport',
  '/monthReductionReport',
  '/monitoringReport',
  '/agentCenter/flowTaskView',
  '/mflowTaskView',
  '/exception/401',
  '/exception/402',
  '/exception/500',
  '/marketManagement/marketAndMaterialCar',
  '/demoCarBuyer/demoSetBuyerCar1',
  '/agentCenter/IAMOrgSetting' // 测试
]

// 不用重定向跳的页面
let unRedirect = [
  '/',
  '/login',
  '/dashboard',
  '/registered',
  '/forgetPassword',
  '/find', // 单点登录跳转页面
  '/exception',
  '/exception/404',
  '/redirect',
  '/noUser',
  '/page401',
  '/portal',
  '/exception/401',
  '/exception/402',
  '/exception/500'
]

// init store
let store = null
export function init (param) {
  store = param
}

const findPathByLeafId = (leafId, nodes, path = []) => {
  for (let i = 0; i < nodes.length; i++) {
    const tmpPath = path.concat()
    tmpPath.push(nodes[i].functionAddress)
    if (leafId === nodes[i].functionAddress) {
      return tmpPath
    }
    if (nodes[i].childPermissions) {
      const findResult = findPathByLeafId(
        leafId,
        nodes[i].childPermissions,
        tmpPath
      )
      if (findResult) {
        return findResult
      }
    }
  }
}

const checkPrower = (to, menus = [], next, NProgress, data = null) => {
  const { registerVersion = 'new' } = store.getters.sysOpenConfig
  if (
    (findPathByLeafId(to.path, menus) || []).includes(to.path) ||
    to.path.indexOf('/dashboard') !== -1
  ) {
    if (to.path == '/dashboard') {
      let firstLogin = data?.firstLogin || null
      if (['', null].includes(firstLogin) && data && data?.userType !== 'BUYER') {
        if (registerVersion == 'old') {
          // next('/userManage/companyInfoMaintain')
          next()
        } else {
          next('/registered')
        }
      } else {
        //  如果有重定向跳转就跳过去重定向页面
        let redirect = getRedirectUrl()
        if (redirect && !unRedirect.includes(redirect)) {
          next(redirect)
        } else {
          next()
        }
        // next()
      }
    } else {
      next()
    }
  } else {
    if (to.fullPath.indexOf('redirect') > -1) {
      next()
    } else {
      next({ path: '/exception/401' })
      NProgress.done()
    }
  }
}

const checkIsWhite = to => {
  return (
    whiteList.indexOf(to.path) !== -1 ||
    tokenWhiteList.indexOf(to.path) !== -1 ||
    to.path.includes('/flowTaskViewBase') ||
    to.path.includes('/flowTaskToken')
  )
}

/**
  * @type {boolean} true | false
  * @description 判断是否是PC端
  */
const isPC = () => {
  var userAgentInfo = navigator.userAgent
  var Agents = ['Android', 'iPhone', 'SymbianOS', 'Windows Phone', 'iPod']
  var flag = true
  for (var v = 0; v < Agents.length; v++) {
    if (userAgentInfo.indexOf(Agents[v]) > 0) {
      flag = false
      break
    }
  }
  return flag
}

// 登录以后查询用户信息
const initSystem = (to, from, next, NProgress) => {
  store.dispatch('user/initSystem').then(
    res => {
      const data = res ? res.data || {} : {}
      const { menus } = data
      console.log(data, menus, 'menus')
      if (checkIsWhite(to)) {
        next()
      } else {
        if (store.getters?.sysOpenConfig?.iamBaseUrl) {
          checkPrower(to, menus, next, NProgress, data)
        } else {
          setTimeout(checkPrower(to, menus, next, NProgress, data), 2000)
        }
      }
    },
    err => {
      next()
      NProgress.done()
      console.log(err)
    }
  )
}

// 查询主题数据
const fetchSysTheme = async (to) => {
  let resTheme = await store.dispatch('app/setSystemTheme')
  changeFavicon(resTheme?.favicon) // 修改浏览器 icon favicon
  let systemTheme = getSystemTheme()
  document.title = getPageTitle(to.meta.title, systemTheme.webTitle) // i18n.t('route.' + to.name)
}

router.beforeEach(async (to, from, next) => {
  const isFirstAppear = !from.name
  NProgress.start()
  const hasToken = getToken()
  // store存储pc还是移动端状态
  store.dispatch('settings/changeIsPC', isPC())
  if (isFirstAppear) {
    if (hasToken) {
      // 有token就初始化用户数据 并判断是否有当前路由访问权限
      fetchSysTheme(to) // 查询主题信息
      initSystem(to, from, next, NProgress) // 查询用户信息
    } else {
      // 没有token
      store.dispatch('app/getServeLang')
      await fetchSysTheme(to) // 查询主题信息
      if (checkIsWhite(to)) {
        if ((tokenWhiteList.includes(to.path)) && !hasToken) {
          next('/login')
        } else {
          next()
        }
      } else {
        next('/login')
        NProgress.done()
      }
    }
  } else {
    // 非第一次进入页面 停留在页面的路由跳转判断
    if (!hasToken) {
      // 没有token
      if (checkIsWhite(to)) {
        if ((tokenWhiteList.includes(to.path)) && !hasToken) {
          next('/login')
        } else {
          next()
        }
      } else {
        next('/login')
        NProgress.done()
      }
    } else {
      // 有token 就判断路由是否有权限
      if (to.path === '/login') {
        if (isPortalSourcing === 'Y') {
          next()
        } else {
          // 已经登录 没有开启显示公开寻源信息的 直接跳转主界面
          next({ path: '/' }) // 工作台
        }
        NProgress.done()
      } else {
        if (checkIsWhite(to)) {
          next()
        } else {
          const { menus } = store.getters.userInfo
          if (store.getters?.sysOpenConfig?.iamBaseUrl) {
            checkPrower(to, menus, next, NProgress, store.getters.userInfo)
          } else {
            setTimeout(checkPrower(to, menus, next, NProgress, store.getters.userInfo), 2000)
          }
        }
      }
    }
  }
})

router.afterEach((to) => {
  // finish progress bar
  if (whiteList.includes(to.path)) { // 给白名单页面设置title
    let systemTheme = getSystemTheme()
    document.title = getPageTitle('route.' + to.name, systemTheme.webTitle) // i18n.t('route.' + to.name)
  }
  NProgress.done()
  setTimeout(() => {
    const vxeTabletips = document.querySelectorAll('.vxe-table--tooltip-wrapper')
    const elTabletips = document.querySelectorAll('.el-tooltip__popper')
    const elSelectDropdown = document.querySelectorAll('.el-select-dropdown')
    // vxeTable
    if (vxeTabletips.length) {
      Array.from(vxeTabletips).map((node) => document.body.removeChild(node))
    }
    // elTable
    if (elTabletips.length) {
      Array.from(elTabletips).map((node) => document.body.removeChild(node))
    }
    if (elSelectDropdown.length) {
      Array.from(elSelectDropdown).map((node) => document.body.removeChild(node))
    }
  }, 1100)
  // 页面打开以后如果有
  if (to.name !== 'login') {
    let redirect = getRedirectUrl()
    if (redirect) {
      removeRedirectUrl()
    }
  }
})

// 重置路由
export function resetRouter (_routes) {
  const newRouter = createRouter(_routes)
  router.matcher = newRouter.matcher // reset router

  return router
}

export default router
