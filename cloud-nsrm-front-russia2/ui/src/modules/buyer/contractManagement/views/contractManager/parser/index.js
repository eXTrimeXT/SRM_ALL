import Vue from 'vue'
import { getStore, getRouter, geti18n } from '@/main'
import CustomComponent from './custom-component.vue'
import uniqueId from 'lodash/uniqueId'

/**
 * 转换为原始模板（主要是记录非自定义元素修改，用于保存合同）
 * @param {String} template 含有自定义元素组件的字符串模板
 * @returns {Object} 转换后的字符串模板
 */
function unReplacer (template) {
  const wordWrapMatcher = /\n/g
  // 过滤换行符
  const t = template.replace(wordWrapMatcher, '')
  const componentMatch = /\<(span|div)\sdata\-key\=(.*?)\<\/(span|div)\>/g
  const codeMatch = /data\-key\=\"(.*?)\"/
  return t.replace(componentMatch, $0 => {
    const code = codeMatch.exec($0)[1]
    return `\$\{${code}\}`
  })
}

/**
 * 替换原始模板中的自定义元素为对应组件，并记录组件code和替换之前的html字符串
 * @param {String} template 原始字符串模板
 * @param {Boolean} forceUpdate 是否初始化组件值
 * @returns {Object} 替换之后的字符串模板、自定义组件code集合
 */
function replacer (template, initialize = true) {
  const placeholderMatcher = /\$\{(.*?)\}/g
  const wordWrapMatcher = /\n/g
  const elementCodes = {}
  // 过滤换行符
  const t = template.replace(wordWrapMatcher, '')
  const vueTemplate = t.replace(placeholderMatcher, ($0, $1) => {
    const str = $1.replace(/\[(.*?)\]/, '')
    // 获取组件code key
    let [code, key] = str.split(':')
    // 记录模板中组件code，并初始化值
    if (key) {
      elementCodes[key] = null
      console.log('has key:', key, str)
    } else {
      key = uniqueId(`${code}_`)
      console.log('not key:', key, str)
      elementCodes[key] = null
    }
    // 替换占位符为相应组件
    return `<custom-component elemKey="${$1}" :initialize="${initialize}" code="${code}" :context="context" :editable="editable" v-model="elemKeys.${key}" />`
  })
  console.log('[elementCodes]', elementCodes)
  return { vueTemplate, elementCodes }
}

/**
 * 生成合同正文组件，返回dom节点
 * @param {Object} elemKeys 组件中自定义元素code对象 { [code]: [value] }
 * @param {String} html 组件html字符串
 * @param {Object} context 父组件上下文
 * @param {Object} wrapper 组件容器dom节点
 * @returns {Object} 生成后的组件的dom节点
 */
function generateComponent ({ html, elemKeys, context, wrapper, onInit }) {
  const Component = Vue.extend({
    store: getStore(),
    router: getRouter(),
    i18n: geti18n(),
    components: {
      CustomComponent
    },
    data () {
      return {
        elemKeys,
        editable: false,
        context
      }
    },
    created () {
      if (typeof onInit === 'function') {
        onInit(this)
      }
    },
    updated () {
      console.log('generateComponent: ', this.elemKeys)
    },
    mounted () {
      this.context.childContext = this
      // 百度编辑器的html直接渲染成页面之后，表格没有边框，设置表格边框
      this.$nextTick(() => {
        const tables = wrapper.getElementsByTagName('table')
        for (let i = 0; i < tables.length; i++) {
          const classes = Array.from(tables[i].classList)
          if (
            classes.includes('el-table__header') ||
            classes.includes('el-table__body')
          ) {
            continue
          }
          tables[i].setAttribute('border', '1')
        }
      })
    },
    template: `<div id="contract-page" style="position: relative;">${html}</div>`
  })
  const component = new Component().$mount()
  return component.$el
}

export default {
  replacer,
  unReplacer,
  generateComponent
}
