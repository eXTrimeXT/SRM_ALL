// TODO 提供便捷的助手方法
/**
 * 解析路由配置文件
 * 可以在这里新增、修改、删除路由，可以异步去处理一些事情
 */
export const resolveRoutes = async (routes) => {
  console.log('==routes==', routes);
  for (let item of routes) {
    if (item.name === "contractManagement") {
      item.children.push({
        path: 'industry',
        name: 'IndustryIndex',
        component: () => import('./pages/mock/index.js'),
        meta: {
          title: '行业包新增页面',
        }
      })
    }
  }
  return [
    ...routes
  ]
}
