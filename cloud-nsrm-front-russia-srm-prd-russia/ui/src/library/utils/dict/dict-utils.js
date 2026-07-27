import DictStore from './dict-store'
import DICT_CONFIG, { SELECT_TYPE } from './dict-config'

export function createStore (initialState = {}, init = true) {
  const dictStore = new DictStore()
  Object.keys(initialState).forEach(key => {
    dictStore.$set(dictStore.dictStates, key, initialState[key])
  })
  if (init && Object.keys(initialState)) {
    dictStore.commit(DICT_CONFIG.LOAD_DICTIONARY, Object.keys(initialState))
  }
  return dictStore
}

export function loadCustomSelect (customSelectType, code, callback) {
  const selectConfig = SELECT_TYPE[customSelectType]
  let requestParams = code
  if (typeof code === 'string' || typeof code === 'number') {
    requestParams = { code: code }
  }
  if (selectConfig.transferParams && typeof selectConfig.transferParams === 'function') {
    requestParams = selectConfig.transferParams(code)
  }
  selectConfig.apiFunction(requestParams).then(res => {
    let dictList = res.data.list ? res.data.list : res.data
    let dictionaryArray = dictList
    if (dictionaryArray && dictionaryArray.length > 0) {
      if (selectConfig.transformOptions && typeof selectConfig.transformOptions === 'function') {
        const optionsTemp = []
        for (let i = 0; i < dictionaryArray.length; i++) {
          optionsTemp.push(selectConfig.transformOptions(dictionaryArray[i]))
        }
        if (callback) {
          callback(optionsTemp)
        }
      } else {
        if (callback) {
          callback(dictionaryArray)
        }
      }
    } else {
      // eslint-disable-next-line standard/no-callback-literal
      callback([])
    }
  })
}

export function createDictClass (initialState = {}, init = true) {
  const DictClass = {
    dictStore: new DictStore(),
    customSelectType: null,
    getDictLabel: (dictCode, dictItemCode, options = {}) => {
      if (!DictClass.dictStore.dictStates[dictCode]) {
        DictClass.dictStore.$set(DictClass.dictStore.dictStates, dictCode, [])
        DictClass.dictStore.commit(DICT_CONFIG.LOAD_DICTIONARY, dictCode)
      }
      return DictClass.dictStore.getLabel(dictCode, dictItemCode, options)
    },
    setDictionary: (code, dictionary, force = true) => {
      DictClass.dictStore.commit(DICT_CONFIG.SET_DICTIONARY, code, dictionary, force)

      return DictClass
    },
    setDictMap: (code, dictionary, force = true) => {
      DictClass.dictStore.commit(DICT_CONFIG.SET_DICT_MAP, code, dictionary, force)

      return DictClass
    },
    loadDictionary: (code, force = true) => {
      DictClass.dictStore.commit(DICT_CONFIG.LOAD_DICTIONARY, code, force)

      return DictClass
    },
    getDict: (dictCode) => {
      return DictClass.dictStore.dictStates[dictCode]
    },
    getDictDetail: (dictCode, dictItemCode) => {
      if (!DictClass.dictStore.dictStates[dictCode]) {
        return null
      }
      const dictList = DictClass.dictStore.dictStates[dictCode]
      for (let i = 0; i < dictList.length; i++) {
        if (dictItemCode === dictList[i].value) {
          return dictList[i]
        }
      }

      return null
    },
    setCustomSelectType: (customSelectType) => {
      DictClass.customSelectType = customSelectType

      return DictClass
    },
    loadCustomSelectType: (code) => {
      loadCustomSelect(DictClass.customSelectType, code, dictionaryArray => {
        DictClass.setDictionary(code, dictionaryArray)
      })
      return DictClass
    }
  }
  Object.keys(initialState).forEach(key => {
    DictClass.dictStore.$set(DictClass.dictStore.dictStates, key, initialState[key])
  })
  if (init && Object.keys(initialState)) {
    DictClass.dictStore.commit(DICT_CONFIG.LOAD_DICTIONARY, Object.keys(initialState))
  }
  return DictClass
}

export const DictClass = createDictClass()

export default DictClass
