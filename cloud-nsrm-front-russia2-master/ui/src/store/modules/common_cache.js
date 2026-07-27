// import { API_COMMON_CACHE } from '@/config/common-config'
import { adaptDictItem } from '@/utils'
import http from '@/utils/axios/http'

// 字典（字典、币种、单位、税率）地址
const dictURl = {
  COMMON_CACHE_LIST_DICT: '/api-base/dict/base-dict-item/listAllByParam',
  COMMON_CACHE_LIST_DICT_BATCH: '/api-base/dict/base-dict-item/listByDictCode',
  COMMON_CACHE_LIST_UNIT: '/api-base/purchase/purchaseUnit/listAll',
  COMMON_CACHE_LIST_CURRENCY: '/api-base/purchase/purchaseCurrency/listAll',
  COMMON_CACHE_LIST_TAX: '/api-base/purchase/purchaseTax/listAll'
}

/**
 * province: { 'CN': []  }
 * city: { 'provinceCode': [] }
 * @type {{dictMap: {}, province: {}, city: {}, dictList: {}}}
 */
const state = {
  dictList: {},
  dictMap: {},
  customLockMap: {},
  customDictMap: {},
  dictLockMap: {},
  selectDictMap: {}
}

const mutations = {
  SET_DICT_LIST: (state, { dictCode, dictDetail }) => {
    state.dictList[dictCode] = dictDetail
    state.dictMap[dictCode] = {}
    for (let i = 0; i < dictDetail.length; i++) {
      state.dictMap[dictCode][dictDetail[i].value] = dictDetail[i].label
    }
  },
  SET_CUSTOM_LOCK: (state, { type, code, lock }) => {
    if (!state.customLockMap[type]) {
      state.customLockMap[type] = {}
    }
    state.customLockMap[type][code] = {
      lock: lock,
      unique: new Date().getTime()
    }
    state.customLockMap = JSON.parse(JSON.stringify(state.customLockMap))

    setTimeout(() => {
      state.customLockMap[type][code] = {
        lock: false,
        unique: new Date().getTime()
      }

      state.customLockMap = JSON.parse(JSON.stringify(state.customLockMap))
    }, 10000)
  },
  SET_DICT_LOCK: (state, { code, lock }) => {
    state.dictLockMap[code] = {
      lock: lock,
      unique: new Date().getTime()
    }
    state.dictLockMap = JSON.parse(JSON.stringify(state.dictLockMap))

    setTimeout(() => {
      state.dictLockMap[code] = {
        lock: false,
        unique: new Date().getTime()
      }

      state.dictLockMap = JSON.parse(JSON.stringify(state.dictLockMap))
    }, 10000)
  },
  SET_CUSTOM_DICT_MAP: (state, { type, code, list }) => {
    if (!state.customDictMap[type]) {
      state.customDictMap[type] = {}
    }
    state.customDictMap[type][code] = list
  },
  SET_SELECT_DICT_MAP: (state, { code, list }) => {
    state.selectDictMap[code] = list
  },
  RESET: state => {
    state.dictList = {}
    state.dictMap = {}
    state.customLockMap = {}
    state.customDictMap = {}
    state.dictLockMap = {}
    state.selectDictMap = {}
  }
}

const listDictBatch = async ({ commit, state, dispatch }, { dictCodeList }) => {
  if (!dictCodeList || dictCodeList.length === 0) {
    return []
  }

  const resultDictList = {} // [{ dictCode: 'tax'/dictCode, dictDetail: list }]
  var needLoadList = []
  var hasTax = false
  var hasUnit = false
  var hasCurrency = false
  for (let i = 0; i < dictCodeList.length; i++) {
    let dictCodeItem = dictCodeList[i]
    let dictDetailList = state.dictList[dictCodeItem]
    if (!dictDetailList || dictDetailList.length === 0) {
      if (dictCodeItem === 'tax') {
        hasTax = true
      } else if (dictCodeItem === 'unit') {
        hasUnit = true
      } else if (dictCodeItem === 'currency') {
        hasCurrency = true
      } else {
        needLoadList.push(dictCodeItem)
      }
    } else {
      resultDictList[dictCodeItem] = dictDetailList
    }
  }

  // 税率(tax)、单位(unit)、币种(currency)
  if (hasTax) {
    let list = []
    // let { data } = await dispatch(API_COMMON_CACHE.LIST_TAX.STORE_NAME, {}, { root: true })
    const { data } = await http({
      url: dictURl.COMMON_CACHE_LIST_TAX,
      method: 'POST',
      data: {},
      loading: false
    })

    for (let i = 0; i < data.length; i++) {
      list.push(adaptDictItem(data[i], 'tax'))
    }
    resultDictList['tax'] = list
  }
  if (hasUnit) {
    let list = []
    // let { data } = await dispatch(API_COMMON_CACHE.LIST_UNIT.STORE_NAME, {}, { root: true })

    const { data } = await http({
      url: dictURl.COMMON_CACHE_LIST_UNIT,
      method: 'POST',
      data: {},
      loading: false
    })

    for (let i = 0; i < data.length; i++) {
      list.push(adaptDictItem(data[i], 'unit'))
    }
    resultDictList['unit'] = list
  }
  if (hasCurrency) {
    let list = []
    // let { data } = await dispatch(API_COMMON_CACHE.LIST_CURRENCY.STORE_NAME, {}, { root: true })

    const { data } = await http({
      url: dictURl.COMMON_CACHE_LIST_CURRENCY,
      method: 'POST',
      data: {},
      loading: false
    })
    console.log(data)

    for (let i = 0; i < data.length; i++) {
      list.push(adaptDictItem(data[i], 'currency'))
    }
    resultDictList['currency'] = list
  }
  if (needLoadList.length >= 1) {
    let tempMap = {}

    // const { data } = await dispatch(API_COMMON_CACHE.LIST_DICT_BATCH.STORE_NAME, needLoadList, {
    //   root: true
    // })

    const { data } = await http({
      url: dictURl.COMMON_CACHE_LIST_DICT_BATCH,
      method: 'POST',
      data: needLoadList,
      loading: false
    })

    for (let i = 0; i < data.length; i++) {
      if (!tempMap[data[i].dictCode]) {
        tempMap[data[i].dictCode] = []
      }
      tempMap[data[i].dictCode].push(adaptDictItem(data[i], data[i].dictCode))
    }

    for (let i = 0; i < needLoadList.length; i++) {
      const needDictCode = needLoadList[i]
      if (!tempMap[needDictCode]) {
        tempMap[needDictCode] = []
      }
    }

    for (let dictCodeItem in tempMap) {
      resultDictList[dictCodeItem] = tempMap[dictCodeItem]
    }
  }

  return resultDictList
}

const actions = {
  listDictBatch ({ commit, state, dispatch }, { dictCodeList }) {
    return new Promise(async resolve => {
      const resultDictList = await listDictBatch({ commit, state, dispatch }, { dictCodeList })

      for (let dictCodeItem in resultDictList) {
        commit('SET_DICT_LIST', {
          dictCode: dictCodeItem,
          dictDetail: resultDictList[dictCodeItem]
        })
      }

      var dictBatchMap = {}
      for (let i = 0; i < dictCodeList.length; i++) {
        var dictCode = dictCodeList[i]
        var dictOneMap = state.dictMap[dictCode]
        if (!dictOneMap) {
          dictBatchMap[dictCode] = {}
        } else {
          dictBatchMap[dictCode] = dictOneMap
        }
      }
      resolve(dictBatchMap)
    })
  },
  listDictList ({ commit, state, dispatch }, { dictCodeList }) {
    return new Promise(async resolve => {
      const resultDictList = await listDictBatch({ commit, state, dispatch }, { dictCodeList })

      for (let dictCodeItem in resultDictList) {
        commit('SET_DICT_LIST', {
          dictCode: dictCodeItem,
          dictDetail: resultDictList[dictCodeItem]
        })
      }

      resolve(resultDictList)
    })
  },
  listDictDetail ({ commit, state, dispatch }, { dictCode, dictItemCode }) {
    return new Promise(async resolve => {
      if (!dictCode) {
        resolve([])
        return
      }
      var dictDetail = state.dictList[dictCode]
      if (!dictDetail) {
        const dictCodeList = []
        dictCodeList.push(dictCode)
        const resultDictList = await listDictBatch({ commit, state, dispatch }, { dictCodeList })

        for (let dictCodeItem in resultDictList) {
          commit('SET_DICT_LIST', {
            dictCode: dictCodeItem,
            dictDetail: resultDictList[dictCodeItem]
          })
        }
        dictDetail = resultDictList[dictCode]
      }
      if (dictItemCode) {
        resolve(state.dictMap[dictCode][dictItemCode])
      } else {
        resolve(dictDetail)
      }
    })
  },

  setCustomDictMap ({ commit, state, dispatch }, { type, code, list }) {
    commit('SET_CUSTOM_DICT_MAP', { type: type, code: code, list: list })
  },

  setLock ({ commit, state, dispatch }, { type, lock }) {
    commit('SET_LOCK', { type: type, lock: lock })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
