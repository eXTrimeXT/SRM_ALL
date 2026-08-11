import http from '@/utils/axios/http'

// 字典（字典、币种、单位、税率）地址
const dictURl = {
  COMMON_CACHE_LIST_DICT: '/api-base/dict/base-dict-item/listAllByParam',
  COMMON_CACHE_LIST_DICT_BATCH: '/api-base/dict/base-dict-item/listByDictCode',
  COMMON_CACHE_LIST_UNIT: '/api-base/purchase/purchaseUnit/listAll',
  COMMON_CACHE_LIST_CURRENCY: '/api-base/purchase/purchaseCurrency/listAll',
  COMMON_CACHE_LIST_TAX: '/api-base/purchase/purchaseTax/listAll'
}

export const getApi = (key) => {
  return dictURl[key]
  // return import.meta.env['VUE_APP_API_' + key]
}

const HttpGetAPI = (url, params) => {
  return http({
    url: url,
    method: 'GET',
    params: params,
    loading: false
  })
}

const HttpPostAPI = (url, data) => {
  return http({
    url: url,
    method: 'POST',
    data: data,
    loading: false
  })
}

const LoadHttpGetAPI = (url, params) => {
  return http({
    url: url,
    method: 'GET',
    params: params,
    loading: true
  })
}

const LoadHttpPostAPI = (url, data) => {
  return http({
    url: url,
    method: 'POST',
    data: data,
    loading: true
  })
}

const HttpAPIMap = {
  DIRECT: {
    GET: HttpGetAPI,
    POST: HttpPostAPI
  },
  LOAD: {
    GET: LoadHttpGetAPI,
    POST: LoadHttpPostAPI
  }
}

const createMethod = (type, loadType, apiUrl) => {
  return function (data) {
    let apiName = HttpAPIMap[loadType][type]
    return apiName(apiUrl, data)
  }
}

const createAction = (type, loadType, apiUrl) => {
  return function ({ commit, state }, params) {
    return new Promise((resolve, reject) => {
      let apiName = HttpAPIMap[loadType]
      apiName[type](apiUrl, params).then(response => {
        resolve(response)
      }).catch(error => {
        reject(error)
      })
    })
  }
}

/**
 * @param configAPI
 *  {
 *    key-name: [ api_config, Type:GET,POST, loadType:DIRECT/LOAD ]
 *  }
 *  相关默认值： Type： POST
 *              loadType： LOAD
 *  如：{
 *      LIST_INFO: [ BASE_LIST_INFO_SELECT ]
 *      LIST_INFO: [ BASE_LIST_INFO_SELECT, 'GET' ]
 *      LIST_INFO: [ BASE_LIST_INFO_SELECT, 'GET', 'DIRECT' ]
 *      }
 * @param storePrefix
 */
export const createAPI = (configAPI, storePrefix) => {
  let apiJson = {}
  let actions = {}
  for (let key in configAPI) {
    const type = configAPI[key][0]
    const apiUrl = getApi(configAPI[key][1])
    let loadType
    if (configAPI[key].length >= 3) {
      loadType = configAPI[key][2]
    } else {
      // 设置默认值为需要加载
      loadType = 'LOAD'
    }
    apiJson[key] = {
      STORE_NAME: storePrefix + '/' + key
      // method: createMethod(type, loadType, apiUrl)
    }
    actions[key] = createAction(type, loadType, apiUrl)
  }
  apiJson['STORE_PREFIX'] = storePrefix

  apiJson['STORE_JSON'] = {
    namespaced: true,
    state: {},
    mutations: {},
    actions
  }

  return apiJson
}
