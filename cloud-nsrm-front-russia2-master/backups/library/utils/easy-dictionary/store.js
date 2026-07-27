import Watcher from './watcher'
import {
  getDictItemList,
  getAllPurCurrency,
  getAllPurTax,
  getAllPurUnit
} from '@/api/common'
import { adaptDictData } from '@/utils'

const baseCodeHandler = {
  currency: () => getAllPurCurrency(),
  tax: () => getAllPurTax(),
  unit: () => getAllPurUnit()
}

const generateCodeParams = code => {
  return code.map(c => ({
    dictCode: c
  }))
}

Watcher.prototype.mutation = {
  setDictionary (states, code, dictionary, force = true) {
    if (force) {
      states[code] = dictionary
    } else {
      if (!states[code]) {
        states[code] = dictionary
      }
    }
  },

  async loadDictionary (states, code, force = true) {
    if (typeof code === 'string') {
      const params = generateCodeParams([code])
      const { data } = await getDictItemList(params)
      const result = data[0][code]
      const dictionary = adaptDictData(result)
      this.commit('setDictionary', code, dictionary, force)
    }
    if (Array.isArray(code)) {
      const params = generateCodeParams(code)
      const { data } = await getDictItemList(params)
      code.forEach((item, index) => {
        const result = data[index][item]
        const dictionary = adaptDictData(result)
        this.commit('setDictionary', item, dictionary, force)
      })
    }
  },
  async loadByBaseCode (states, baseCode, force = true) {
    const legalScene = ['currency', 'tax', 'unit']
    if (legalScene.includes(baseCode)) {
      const { data } = await baseCodeHandler[baseCode]()
      const dictionary = adaptDictData(data, baseCode)
      this.commit('setDictionary', baseCode, dictionary, force)
    }
  },
  async remote (states, name, axios, force = true) {
    // 检查axios是否返回Promise对象
    if (typeof axios === 'function') {
      const res = axios()
      if (res && res.then) {
        res.then(dictionary => {
          this.commit('setDictionary', name, dictionary, force)
        })
      }
    }
  }
}

Watcher.prototype.commit = function (name, ...args) {
  const mutations = this.mutation
  if (mutations[name]) {
    mutations[name].apply(this, [this.states].concat(args))
  } else {
    throw new Error(`Action not found: ${name}`)
  }
}

Watcher.prototype.getLabel = function (code, value) {
  const dictionary = this.states[code]
  if (dictionary) {
    const target = dictionary.find(i => i.value === value)
    return target ? target.label : value
  }
  return value
}

export default Watcher
