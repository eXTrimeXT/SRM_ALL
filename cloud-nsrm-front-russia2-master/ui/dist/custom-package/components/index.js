import { Car } from './car.js'

const components = [Car]

/**
 * 全局组件注册
 */
export const install = (Vue) => {
  components.forEach((component) => {
    Vue.component(component.name, component)
  })
}
