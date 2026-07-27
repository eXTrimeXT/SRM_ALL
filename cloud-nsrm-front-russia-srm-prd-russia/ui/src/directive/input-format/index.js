import inputformat from './input-format'

const install = function (Vue) {
  Vue.directive('input-format', inputformat)
}

if (window.Vue) {
  window['input-format'] = inputformat
  Vue.use(install); // eslint-disable-line
}

inputformat.install = install
export default inputformat
