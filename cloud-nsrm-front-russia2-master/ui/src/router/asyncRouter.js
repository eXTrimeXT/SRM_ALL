import { requireAll } from 'lib@/utils/require'
import { mergeRoutes } from './utils'
import asyncModRouter from './asyncModRouter'
// 打包模式
let projectMode = import.meta.env.VUE_APP_MODEL
let appRouters = []

// 公共模块
export const commonRoutes = requireAll(
  import.meta.glob('../modules/common/**/router/index.js', { eager: true })
)
export const commonRoutesCus = requireAll(
  import.meta.glob('../modulesCus/common/**/router/index.js', { eager: true })
)

if (projectMode === 'buyer') {
  const buyerRouter = requireAll(
    import.meta.glob('../modules/buyer/**/router/index.js', { eager: true })
  )
  const buyerRouterCus = requireAll(
    import.meta.glob('../modulesCus/buyer/**/router/index.js', { eager: true })
  )
  appRouters = [
    ...buyerRouterCus,
    ...commonRoutesCus,
    ...buyerRouter,
    ...commonRoutes,
    ...asyncModRouter
  ]
} else if (projectMode === 'supplier') {
  const supplierRouter = requireAll(
    import.meta.glob('../modules/supplier/**/router/index.js', { eager: true })
  )
  const supplierRouterCus = requireAll(
    import.meta.glob('../modulesCus/supplier/**/router/index.js', { eager: true })
  )

  appRouters = [
    ...supplierRouterCus,
    ...commonRoutesCus,
    ...supplierRouter,
    ...commonRoutes,
    ...asyncModRouter
  ]
} else {
  const buyerRouter = requireAll(
    import.meta.glob('../modules/buyer/**/router/index.js', { eager: true })
  )
  const buyerRouterCus = requireAll(
    import.meta.glob('../modulesCus/buyer/**/router/index.js', { eager: true })
  )
  const supplierRouter = requireAll(
    import.meta.glob('../modules/supplier/**/router/index.js', { eager: true })
  )
  const supplierRouterCus = requireAll(
    import.meta.glob('../modulesCus/supplier/**/router/index.js', { eager: true })
  )

  appRouters = [
    ...buyerRouterCus,
    ...supplierRouterCus,
    ...commonRoutesCus,
    ...buyerRouter,
    ...supplierRouter,
    ...commonRoutes,
    ...asyncModRouter
  ]
}

appRouters = mergeRoutes(appRouters)
console.log('appRouters')
console.log(appRouters)

export default appRouters
