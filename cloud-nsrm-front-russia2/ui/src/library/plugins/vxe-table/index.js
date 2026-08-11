import Vue from 'vue'
import XEUtils from 'xe-utils'
import VXETable from 'vxe-table'
import { VXETablePluginElement } from 'vxe-table-plugin-element'

import './vxe-table.scss'

import 'vxe-table-plugin-element/dist/style.css'
import 'vxe-table/lib/style.css'

// 设置全局参数
VXETable.setup({
  // 只使用表格组件，mini尺寸已经改造好
  size: 'mini',
  table: {
    rowConfig: {
      height: 40 // 行高
    }
  }
})

// 往vue原型链添加全局方法
Vue.prototype.$XEUtils = XEUtils
Vue.use(VXETable)
// vxe-table的适配插件，用于兼容element-ui组件库
VXETable.use(VXETablePluginElement)
