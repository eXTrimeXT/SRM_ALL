import {getCurrentKey} from "@/api/common";
import JSEncrypt from "jsencrypt";
import Base64 from "jszip/lib/base64";

const state = {
  currentKey: ''
}

const mutations = {
  SET_CURRENT_KEY: (state, currentKey) => {
    state.currentKey = currentKey
  }
}

const actions = {
  initCurrentKey({ commit, state }) {
    return new Promise((resolve, reject) => {
      getCurrentKey().then(res => {
        commit('SET_CURRENT_KEY', res.data)
        resolve(res.data)
      }).catch(error => {
        reject()
      })
    })
  },
  getValue({ commit, state, dispatch }, srcValue) {
    return new Promise(async (resolve, reject) => {
      if (!state.currentKey) {
        await dispatch('initCurrentKey')
      }
      if (!state.currentKey) {
        reject()
      }
      const encryptor = new JSEncrypt()
      encryptor.setPublicKey('-----BEGIN PUBLIC KEY-----' + state.currentKey + '-----END PUBLIC KEY-----')//设置公钥
      const value = encryptor.encrypt(srcValue + '')
      console.log('value:' + value)
      resolve(Base64.encode(value))
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
