import { flatten } from 'lodash'
// 此文件放路由操作相关工具类方法

/**
 *  路由合并策略，routes数组中靠前的重复路由会被优先引用(所以modulesCus要比modules路由引入靠前)
 *  @param  routes 待合并处理的路由
 */
export const mergeRoutes = (routes) => {
  if (!routes || !Array.isArray(routes)) return []
  let pathList = routes.map(item => item.path)
  let obj = {}
  for (let key of pathList) {
    if (Object.keys(obj).includes(key)) {
      obj[key]++
    } else {
      obj[key] = 1
    }
  }
  let repeatPathArr = []
  Object.keys(obj).forEach(key => {
    if (obj[key] > 1) repeatPathArr.push(key)
  })
  // 合并重复的路由的children
  let singleRoutes = routes.filter(item => !repeatPathArr.includes(item.path))
  let doubleRoutes = []
  for (let item of repeatPathArr) {
    let doubelRouteItem = routes.filter(innerItem => innerItem.path === item)
    doubleRoutes.push(doubelRouteItem)
  }
  let mergeDoubleRoutes = doubleRoutes.reduce((pre, cur) => {
    let obj = {}
    let children = []
    for (let item of cur) {
      (item.children && item.children.length) && children.push(item.children)
    }
    obj = {
      ...cur[0],
      children: flatten(children)
    }
    pre.push(obj)
    return pre
  }, [])
  // mergeDoubleRoutes会有重复并且多层级children
  for (let item of mergeDoubleRoutes) {
    if (item.children && item.children.length) item.children = mergeRoutes(item.children)
  }
  return [
    ...singleRoutes,
    ...mergeDoubleRoutes
  ]
}
