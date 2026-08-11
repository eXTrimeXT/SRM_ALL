import Vue from 'vue'
import SrmInput from './srm-input'
import SrmDialog from './srm-dialog'
import SrmCol from './srm-col'
import SrmRow from './srm-row'
import SrmCommonFile from './srm-common-file'

// 手动注册全局组件方式，让编辑器可以检查组件入参和点击跳转
Vue.component(SrmInput.name, SrmInput)
Vue.component(SrmDialog.name, SrmDialog)
Vue.component(SrmCol.name, SrmCol)
Vue.component(SrmRow.name, SrmRow)
Vue.component(SrmCommonFile.name, SrmCommonFile)
