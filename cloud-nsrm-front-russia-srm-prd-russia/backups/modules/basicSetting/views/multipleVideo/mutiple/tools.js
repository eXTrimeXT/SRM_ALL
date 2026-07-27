import axios from 'axios'
// import JsSha from "jssha";
import sha1 from 'js-sha1'
/**
 * interface Options {
 *  uid: string // 用户账号
 *  channelName: string // 频道名称
 *  appkey: string // 你的 appkey
 *  appSecret: string // 你的 appsecret
 * }
 */
export const getToken = ({ uid, channelName, appkey, appSecret }) => {
  const getTokenUrl = 'https://api.netease.im/nimserver/user/getToken.action'
  const Nonce = Math.ceil(Math.random() * 1e9)
  const CurTime = Math.ceil(Date.now() / 1000)
  const aaa = `${appSecret}${Nonce}${CurTime}`
  // const _sha1 = new JsSha("SHA-1", "TEXT", { encoding: "UTF8" });
  // _sha1.update(aaa);
  // const CheckSum = _sha1.getHash("HEX");

  var hash = sha1.create()
  hash.update(aaa)
  const CheckSum = hash.hex()

  return new Promise((resolve, reject) => {
    axios
      .post(getTokenUrl, `uid=${uid}&channelName=${channelName}`, {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8',
          AppKey: appkey,
          Nonce,
          CurTime,
          CheckSum
        }
      })
      .then(function (data) {
        var d = data.data
        if (d.code !== 200) {
          reject(new Error('getChecksum code: ' + d.code))
          return
        }
        resolve(d.token)
      })
      .catch(error => {
        reject(new Error('getChecksum error: ', error))
      })
  })
}

export const rooms = ({ channelName, appkey, appSecret }) => {
  const roomsUrl = `https://logic-dev.netease.im/v3/api/rooms?cname=${channelName}`
  const Nonce = Math.ceil(Math.random() * 1e9)
  const CurTime = Math.ceil(Date.now() / 1000)
  const aaa = `${appSecret}${Nonce}${CurTime}`
  const sha1 = new JsSha('SHA-1', 'TEXT', { encoding: 'UTF8' })
  sha1.update(aaa)
  const CheckSum = sha1.getHash('HEX')
  return new Promise((resolve, reject) => {
    axios
      .get(roomsUrl, {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8',
          AppKey: appkey,
          Nonce,
          CurTime,
          CheckSum
        }
      })
      .then(function (data) {
        var d = data.data
        if (d.code !== 200) {
          reject(new Error('getChecksum code: ' + d.code))
          return
        }
        resolve(d)
      })
      .catch(error => {
        reject(new Error('getChecksum error: ', error))
      })
  })
}

export const queryMediaFileByChannelId = ({ channelId, appkey, appSecret }) => {
  const queryMediaFileByChannelIdUrl = 'https://api.netease.im/nimserver/history/queryMediaFileByChannelId.action'
  const Nonce = Math.ceil(Math.random() * 1e9)
  const CurTime = Math.ceil(Date.now() / 1000)
  const aaa = `${appSecret}${Nonce}${CurTime}`
  const sha1 = new JsSha('SHA-1', 'TEXT', { encoding: 'UTF8' })
  sha1.update(aaa)
  const CheckSum = sha1.getHash('HEX')
  return new Promise((resolve, reject) => {
    axios
      .post(queryMediaFileByChannelIdUrl, `channelId=${channelId}`, {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8',
          AppKey: appkey,
          Nonce,
          CurTime,
          CheckSum
        }
      })
      .then(function (data) {
        var d = data.data
        if (d.code !== 200) {
          reject(new Error('getChecksum code: ' + d.code))
          return
        }
        resolve(d)
      })
      .catch(error => {
        reject(new Error('getChecksum error: ', error))
      })
  })
}

export const record = ({ channelName, appkey, appSecret }) => {
  const recordUrl = `https://logic-dev.netease.im/v3/api/record?cname=${channelName}`
  const Nonce = Math.ceil(Math.random() * 1e9)
  const CurTime = Math.ceil(Date.now() / 1000)
  const aaa = `${appSecret}${Nonce}${CurTime}`
  const sha1 = new JsSha('SHA-1', 'TEXT', { encoding: 'UTF8' })
  sha1.update(aaa)
  const CheckSum = sha1.getHash('HEX')
  return new Promise((resolve, reject) => {
    axios
      .post(recordUrl, 'a_record=true&v_record=true&type=0', {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8',
          AppKey: appkey,
          Nonce,
          CurTime,
          CheckSum
        }
      })
      .then(function (data) {
        var d = data.data
        if (d.code !== 200) {
          reject(new Error('getChecksum code: ' + d.code))
          return
        }
        resolve(d)
      })
      .catch(error => {
        reject(new Error('getChecksum error: ', error))
      })
  })
}

export const checkBrowser = type => {
  const ua = navigator.userAgent.toLowerCase()
  const info = {
    ie: /msie/.test(ua) && !/opera/.test(ua),
    opera: /opera/.test(ua),
    safari: /version.*safari/.test(ua),
    chrome: /chrome/.test(ua),
    firefox: /gecko/.test(ua) && !/webkit/.test(ua)
  }
  return info[type] || false
}
