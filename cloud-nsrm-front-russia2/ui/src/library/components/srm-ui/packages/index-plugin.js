/**
 * 插件形式引入全局组件，但是会导致编辑器无法可以检查组件入参和点击跳转，故暂不使用
 */
import SrmInput from './srm-input'
import SrmDialog from './srm-dialog'
import SrmCol from './srm-col'
import SrmRow from './srm-row'
import SrmCommonFile from './srm-common-file'

// 组件列表
const components = [
  SrmInput,
  SrmDialog,
  SrmRow,
  SrmCol,
  SrmCommonFile
]

// 定义 install 方法，接收 Vue 作为参数。如果使用 use 注册插件，那么所有的组件都会被注册
const install = function (Vue) {
  // 判断是否安装
  if (install.installed) return
  // 遍历注册全局组件
  components.forEach(component => Vue.component(component.name, component))
}

// 判断是否是直接引入文件
if (typeof window !== 'undefined' && window.Vue) {
  install(window.Vue)
}

export default {
  // 导出的对象必须具有 install，才能被 Vue.use() 方法安装
  install,
  // 以下是具体的组件列表
  SrmInput,
  SrmDialog,
  SrmRow,
  SrmCol,
  SrmCommonFile
}
