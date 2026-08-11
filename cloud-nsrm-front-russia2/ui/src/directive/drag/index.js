import drag from './drag'

const install = function (Vue) {
  Vue.directive('drag-div', drag)
}

if (window.Vue) {
  window['drag-div'] = drag
  Vue.use(install); // eslint-disable-line
}

drag.install = install
export default drag
