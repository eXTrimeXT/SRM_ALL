import Watcher from './watcher'
import store from '@/store'
import { STORE_COMMON_CACHE } from '@/config/store-config'
import DICT_CONFIG from './dict-config'

Watcher.prototype.mutation = {
  setDictionary (dictStates, code, dictionary, force = true) {
    if (force) {
      this.$set(dictStates, code, dictionary)
    } else {
      if (!dictStates[code]) {
        this.$set(dictStates, code, dictionary)
      }
    }
  },

  setDictMap (dictStates, code, dictionary, force = true) {
    if (force) {
      if (!dictStates[code]) {
        dictStates[code] = {}
      }
      for (let key in dictionary) {
        dictStates[code][key] = dictionary[key]
      }
    } else {
      if (!dictStates[code]) {
        dictStates[code] = {}
      }
      for (let key in dictionary) {
        if (!dictStates[code][key]) {
          dictStates[code][key] = dictionary[key]
        }
      }
    }
  },

  async loadDictionary (dictStates, code, force = true) {
    if (typeof code === 'string') {
      const dictionary = await store.dispatch(STORE_COMMON_CACHE.LIST_DICT_DETAIL, { dictCode: code })
      this.commit(DICT_CONFIG.SET_DICTIONARY, code, dictionary, force)
    }
    if (Array.isArray(code)) {
      const dictionaryArray = await store.dispatch(STORE_COMMON_CACHE.LIST_DICT_LIST, { dictCodeList: code })
      code.forEach((item, index) => {
        const dictionary = dictionaryArray[item]
        if (!dictionary || dictionary.length === 0) {
          this.commit(DICT_CONFIG.SET_DICTIONARY, item, [], force)
        } else {
          this.commit(DICT_CONFIG.SET_DICTIONARY, item, dictionary, force)
        }
      })
    }
  },
  async remote (dictStates, name, axios, force = true) {
    // 检查axios是否返回Promise对象
    if (typeof axios === 'function') {
      const res = axios()
      if (res && res.then) {
        res.then(dictionary => {
          this.commit(DICT_CONFIG.SET_DICTIONARY, name, dictionary, force)
        })
      }
    }
  }
}

Watcher.prototype.commit = function (name, ...args) {
  const mutations = this.mutation
  if (mutations[name]) {
    mutations[name].apply(this, [this.dictStates].concat(args))
  } else {
    throw new Error(`Action not found: ${name}`)
  }
}

Watcher.prototype.getLabel = function (code, value, options) {
  const {
    // 是否多选
    multiple = false,
    // 字符串分割符
    splitString = ', '
  } = options || {}

  const dictionary = this.dictStates[code]
  if (dictionary) {
    if (
      multiple &&
      // 校验多选值是否合法，只能是数组或逗号分割的字符串
      (Array.isArray(value) || typeof value === 'string')
    ) {
      return (typeof value === 'string' ? value.split(',') : value)
        .map(item => dictionary.find(itemOption => itemOption.value === item)?.label || item)
        .join(splitString)
    }

    const target = dictionary.find(i => i.value === value)
    return target ? target.label : value
  }
  return value
}

export default Watcher
