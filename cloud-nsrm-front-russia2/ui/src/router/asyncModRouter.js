// 各个包(汽车包、品质云包)路由引入控制
import { requireAll } from 'lib@/utils/require'
// 打包模式
let projectMode = import.meta.env.VUE_APP_MODEL
// 开启汽车包
let openCarPak = import.meta.env.VUE_APP_OPEN_CAR_PAK
// 品质云代码包
let openQaPak = import.meta.env.VUE_APP_OPEN_QA_PAK

let appModRouters = []

// 汽车包公共模块 | 汽车包按需引入
let carPakRouterCommon = []
// 品质云公共模块 | 汽车包按需引入
let qaPakRouterCommon = []
if (openCarPak == 'Y') {
  carPakRouterCommon = requireAll(
    import.meta.glob('../modulesCar/common/**/router/index.js', { eager: true })
  )
}
if (openQaPak == 'Y') {
  qaPakRouterCommon = requireAll(
    import.meta.glob('../modulesQa/common/**/router/index.js', { eager: true })
  )
}

// 采购商部署
if (projectMode === 'buyer') {
  let carPakRouterBuyer = []
  let qaPakRouterBuyer = []
  // 汽车包按需引入
  if (openCarPak == 'Y') {
    carPakRouterBuyer = requireAll(
      import.meta.glob('../modulesCar/buyer/**/router/index.js', { eager: true })
    )
  }
  // 品质云按需引入
  if (openQaPak == 'Y') {
    qaPakRouterBuyer = requireAll(
      import.meta.glob('../modulesQa/buyer/**/router/index.js', { eager: true })
    )
  }
  appModRouters = [
    ...carPakRouterBuyer,
    ...carPakRouterCommon,
    ...qaPakRouterBuyer,
    ...qaPakRouterCommon
  ]
} else if (projectMode === 'supplier') {
  // 供应商部署
  let carPakRouterSupplier = []
  let qaPakRouterSupplier = []
  // 汽车包按需引入
  if (openCarPak == 'Y') {
    carPakRouterSupplier = requireAll(
      import.meta.glob('../modulesCar/supplier/**/router/index.js', { eager: true })
    )
  }
  // 品质云按需引入
  if (openQaPak == 'Y') {
    qaPakRouterSupplier = requireAll(
      import.meta.glob('../modulesQa/supplier/**/router/index.js', { eager: true })
    )
  }
  appModRouters = [
    ...carPakRouterSupplier,
    ...carPakRouterCommon,
    ...qaPakRouterSupplier,
    ...qaPakRouterCommon
  ]
} else {
  // 汽车包按需引入
  let carPakRouterBuyer = []
  let carPakRouterSupplier = []
  let qaPakRouterBuyer = []
  let qaPakRouterSupplier = []
  // 汽车包
  if (openCarPak == 'Y') {
    carPakRouterBuyer = requireAll(
      import.meta.glob('../modulesCar/buyer/**/router/index.js', { eager: true })
    )
    carPakRouterSupplier = requireAll(
      import.meta.glob('../modulesCar/supplier/**/router/index.js', { eager: true })
    )
  }
  // 品质云
  if (openQaPak == 'Y') {
    qaPakRouterBuyer = requireAll(
      import.meta.glob('../modulesQa/buyer/**/router/index.js', { eager: true })
    )
    qaPakRouterSupplier = requireAll(
      import.meta.glob('../modulesQa/supplier/**/router/index.js', { eager: true })
    )
  }
  appModRouters = [
    ...carPakRouterBuyer,
    ...carPakRouterSupplier,
    ...carPakRouterCommon,
    ...qaPakRouterBuyer,
    ...qaPakRouterSupplier,
    ...qaPakRouterCommon
  ]
}

export default appModRouters
