import JSEncrypt from "jsencrypt";
import Base64 from "jszip/lib/base64";
import store from '@/store'

const getValue = (srcValue) => {
  const encryptor = new JSEncrypt()
  encryptor.setPublicKey('-----BEGIN PUBLIC KEY-----' + store.state.secret.currentKey + '-----END PUBLIC KEY-----')//设置公钥
  const value = encryptor.encrypt(srcValue + '')
  // console.log('value:' + value)
  return Base64.encode(value)
}

// 数据值加密处理方法
export const dataEncryption = (enterVal, publickKey) => {
  const encryptor = new JSEncrypt()
  encryptor.setPublicKey('-----BEGIN PUBLIC KEY-----' + publickKey + '-----END PUBLIC KEY-----')
  const value = encryptor.encrypt(enterVal + '')
  return Base64.encode(value)
}

export default {
  getValue
}
