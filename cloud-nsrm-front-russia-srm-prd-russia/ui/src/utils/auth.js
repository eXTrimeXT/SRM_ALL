import Cookies from 'js-cookie'
import { isSetDomain, domain } from '@/config/sysConfig'

const TokenKey = 'KeyToken'
const UserId = 'UserId'
const CompanyId = 'CompanyId'
const Name = 'UserName'
const RedirectUrl = 'RedirectUrl'
const LoginType = "LoginType";

// 设置cookie
export const setCookie = (name, value) => {
  Cookies.set(name, value)
  // document.cookie = name + '=' + value + ';domain=' + domain()
}

/**
 * 1、重定向值
*/
// 设置重定向值
export function setRedirectUrl (url) {
  return setCookie(RedirectUrl, url)
}
// 取重定向值
export function getRedirectUrl () {
  return Cookies.get(RedirectUrl)
}
// 重定向值
export function removeRedirectUrl () {
  return Cookies.remove(RedirectUrl, { domain: domain() })
}

/**
 * 2、设置token值
*/
// 设置token值
export function setToken (token) {
  setCookie(TokenKey, token)
}
// 取token值
export function getToken () {
  return Cookies.get(TokenKey, { domain: domain() })
}
// 删除
export function removeToken () {
  return Cookies.remove(TokenKey, { domain: domain() })
}

/**
 * 3、用户名
*/
// 设置用户名
export function setName (name) {
  setCookie(Name, name)
}
export function removeName () {
  return Cookies.remove(Name, { domain: domain() })
}

/**
 * 3、用户Id
*/
// 取Id值
export function getUserId () {
  return Cookies.get(UserId, { domain: domain() })
}
// 设置ID值
export function setUserId (Id) {
  setCookie(UserId, Id)
  // return Cookies.set(UserId, Id);
}
// 删除
export function removeUserID () {
  return Cookies.remove(UserId, { domain: domain() })
}

/**
 * 4、公司id值
*/
// 设置公司id值
export function setCompanyId (Id) {
  setCookie(CompanyId, Id)
}
// 取公司id值
export function getCompanyId () {
  return Cookies.get(CompanyId, { domain: domain() })
}
export function removeCompanyId () {
  return Cookies.remove(CompanyId, { domain: domain() })
}

/**
 * 5、登录方式
*/
export function setEntranceType (type) {
  setCookie('entrance', type)
}
export function getEntranceType () {
  return Cookies.get('entrance', { domain: domain() })
}
/**
 * 清除所有cookie
 */
export const clearCookie = () => {
  let keys = document.cookie.match(/[^ =;]+(?=\=)/g)
  let domainVal = domain()
  if (keys) {
      for (let i = keys.length; i--;) {
        if (keys[i] != 'language') { // 多语言不用清
          Cookies.remove(keys[i], {
            expires: new Date(0).toUTCString(),
            domain: domainVal
          })
          Cookies.remove(keys[i], {
            expires: new Date(0).toUTCString()
          })
        }
      }
  }
}

/**
* 获取主域
* @param weburl
* @returns {string}
*/
export const getDomain = (host = domain()) => {
  if (isSetDomain) { // 开启域名设置
    let hostExts = ['.com', '.cn', '.net', '.cc', '.sh', '.org']
    let ext = []
    let reExt
    let exist = false
    for (var i = 0; i < hostExts.length; i++) {
        if (host.indexOf(hostExts[i]) !== -1) {
            ext[ext.length] = hostExts[i]
            reExt = new RegExp('\\' + hostExts[i])
            host = host.replace(reExt, '{' + (ext.length - 1) + '}')
            exist = true
        } else {
            break
        }
    }
    if (!exist) {
        return host
    }
    var hostarray = host.split('.')
    host = hostarray[hostarray.length - 1]
    for (let i = 0; i < ext.length; i++) {
        reExt = new RegExp('\\{' + i + '\\}')
        host = host.replace(reExt, ext[i])
    }
  }
  return host
}

// 取 LoginType 值:MOBILE/IDM/COMMON
export function getLoginType() {
  return Cookies.get(LoginType);
}
// 设置token值
export function setLoginType(loginType) {
  return Cookies.set(LoginType, loginType);
}
export function removeLoginType() {
  return Cookies.remove(LoginType);
}

