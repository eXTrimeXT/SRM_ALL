// 导入组件，组件必须声明 name
import SrmDialog from './src'

// 为组件提供 install 安装方法，供按需引入
SrmDialog.install = function (Vue) {
  Vue.component(SrmDialog.name, SrmDialog)
}

// 导出组件
export default SrmDialog
