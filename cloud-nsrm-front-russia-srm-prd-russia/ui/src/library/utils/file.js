import axios from 'axios'
import { getToken, getEntranceType } from '@/utils/auth'
import { sysPrefix } from '@/config/ipConfig'
import { FILE_DOWNLOAD, FILE_DELETE } from '@/api/common'
import Secret from '@/utils/secret'
import pdfPlaceholder from '@/assets/img/pdf-placeholder.png'
import { systemUrl } from '@/config/sysConfig'
import { getMenuInfo } from '@/utils/menu-auth'

/**
 *  根据文件id 返回img url
 * @param fileId
 * @returns {`${string}${string}/api-file/file/fileupload/download?fileKey=${string}&access_token=${string}`}
 */
export const getImgSrc = (fileId, fileName) => {
  const fileKey = Secret.getValue(fileId)
  let entrance = getEntranceType() // 获取登录类型 // inside 内部登录方式 || singlePoint 单点登录方式
  // pdf 类型的返回pdf占位图
  let fileNameArr = fileName ? fileName.split('.') : []
  let fileType = fileNameArr[fileNameArr.length - 1]
  if (fileType == 'pdf') {
    return pdfPlaceholder
  }
  // iam 单点不需要传 access_token
  return `${systemUrl}${sysPrefix()}${FILE_DOWNLOAD}?fileKey=${fileKey}` +
    `${entrance === 'singlePoint' ? '' : `&access_token=${getToken()}`}`
}

/**
 * 获取文件后缀
 * @param {String} filename 文件名
 * @returns {String}
 */
export function getFileExt (filename) {
  let ext = ''

  if (typeof filename === 'string') {
    const filenameSplit = filename.split('.')
    ext = filenameSplit[filenameSplit.length - 1]
  }

  return ext
}

/**
 * 文件大小转换
 * @param {Number} byte 文件字节数
 * @param {String} unit 转换单位
 */
export function getFileByteFormat (byte, unit) {
  const conversion = {
    K: 1024,
    M: 1024 * 1024,
    G: 1024 * 1024 * 1024,
    T: 1024 * 1024 * 1024 * 1024
  }
  const unitList = ['K', 'M', 'G', 'T']
  let result = 0

  if (typeof byte !== 'number') {
    throw new Error('byte must be a number type.')
  }

  if (unit && unitList.indexOf(unit) === -1) {
    throw new Error('unit must be a string value of [\'K\', \'M\', \'G\', \'T\'].')
  }

  if (typeof unit === 'undefined') {
    let currentUnit
    for (let i = 0, len = unitList.length; i < len; i++) {
      currentUnit = unitList[i]
      result = byte / conversion[currentUnit]
      if (result < 1024) {
        break
      }
    }
    return `${result.toFixed(2)} ${currentUnit}`
  } else {
    result = byte / conversion[unit]
    return `${result.toFixed(2)} ${unit}`
  }
}

/**
 * 浏览器下载文件流
 * @param {String} content 文件流
 * @param {String} filename 文件名
 */
export function downloadFileStream (content, filename = Date.now()) {
  const blob = new Blob([content])
  const $link = document.createElement('a')

  if ('download' in $link) {
    // 非IE下载
    $link.download = filename
    $link.style.display = 'none'
    $link.rel = 'noopener'
    $link.href = URL.createObjectURL(blob)
    document.body.appendChild($link)
    $link.click()
    URL.revokeObjectURL($link.href) // 释放URL 对象
    document.body.removeChild($link)
  } else {
    // IE10+下载
    navigator.msSaveBlob(blob, filename)
  }
}
/**
 * 直接点击下载的文件
 */
export function downloadExtFileLink (downloadLink) {
  const url = downloadLink
  const $link = document.createElement('a')
  $link.style.display = 'none'
  $link.href = url
  $link.rel = 'noopener'
  document.body.appendChild($link)
  $link.click()
  window.URL.revokeObjectURL($link.href)
  document.body.removeChild($link)
}
// 判断IE 10 IE11
function isIE () { //
  // IE 11判断 || IE10判断
  if ((!!window.ActiveXObject || 'ActiveXObject' in window) || window.navigator.userAgent.indexOf('MSIE') >= 1) { return true } else { return false }
}

export const getDownloadFileUrl = (fileId) => `${FILE_DOWNLOAD}?fileKey=${Secret.getValue(fileId)}`

export const getDeleteFileKey = (fileId) => `${Secret.getValue(fileId)}`

export function downloadWithParam (
  fileId,
  filename,
  callback = () => {}) {
  return downloadFileLink(getDownloadFileUrl(fileId), filename, callback)
}

/**
 * 浏览器下载文件链接
 * @param {String} downloadLink 文件下载链接
 * @param {String} filename 文件名
 */
export function downloadFileLink (
  downloadLink,
  filename = Date.now(),
  callback = () => {}
) {
  function getFilename (headers = '') {
    const fileName = headers ? window.decodeURI(headers['content-disposition'].split('=')[1]) : ''
    return fileName
  }
  let Authorization = getToken() ? 'Bearer ' + getToken() : '' // token
  let menuInfo = getMenuInfo()
  return new Promise((resolve, reject) => {
    axios({
      method: 'get',
      url: `${sysPrefix()}${downloadLink}`,
      headers: {
        Authorization: Authorization,
        'X-Fun-Info': menuInfo.secretKey
      },
      responseType: 'arraybuffer'
    })
      .then(response => {
        const { headers, data } = response
        if (response.headers['content-type']) {
          let contentType = (response.headers['content-type']).toString()
          if (contentType.startsWith('application/json')) {
            let enc = new TextDecoder('utf-8')
            let res = JSON.parse(enc.decode(new Uint8Array(data))) // 转化成json对象
            throw new Error(res.message)
          }
        }
        if (data.byteLength != 0) {
          const blob = new Blob([data])
          let resFileName = getFilename(headers) || filename
          if (isIE()) {
            // IE10+下载
            navigator.msSaveBlob(blob, resFileName)
          } else {
            const url = window.URL.createObjectURL(blob) // URL.createObjectURL(object)表示生成一个File对象或Blob对象
            let dom = document.createElement('a') // 设置一个隐藏的a标签，href为输出流，设置download
            dom.style.display = 'none'
            dom.href = url
            dom.rel = 'noopener'
            dom.setAttribute('download', resFileName) // 指示浏览器下载url,而不是导航到它；因此将提示用户将其保存为本地文件
            document.body.appendChild(dom)
            dom.click()
          }
        } else {
          throw new Error('文件已破坏！')
        }
        resolve()
      })
      .catch(error => {
        reject(error)
      })
  })
}

/**
 * 浏览器下载文件链接{post}
 * @param {String} downloadLink 文件下载链接
 * @param {String} filename 文件名
 * @param {Object} data 参数对象
 */
export function downloadFileLinkByPost (
  downloadLink,
  filename = '',
  data = {},
  callback = () => {}
) {
  const Authorization = getToken() ? 'Bearer ' + getToken() : '' // token
  return new Promise((resolve, reject) => {
    axios({
      method: 'POST',
      url: `${sysPrefix()}${downloadLink}`,
      headers: {
        Authorization: Authorization
      },
      data: data,
      responseType: 'blob'
    })
      .then(response => {
        // eslint-disable-next-line no-unused-vars
        const { data, headers } = response
        if (data.type === 'application/json') {
          throw new Error('下载失败.')
        }
        const resName = headers ? window.decodeURI(headers['content-disposition'].split('=')[1]) : ''
        if (data.byteLength != 0) {
          const blob = new Blob([data]) // 创建一个类文件对象：Blob对象表示一个不可变的、原始数据的类文件对象
          let resFileName = resName || filename
          if (isIE()) {
            // IE10+下载
            navigator.msSaveBlob(blob, resFileName)
          } else {
            const url = window.URL.createObjectURL(blob) // URL.createObjectURL(object)表示生成一个File对象或Blob对象
            const dom = document.createElement('a') // 设置一个隐藏的a标签，href为输出流，设置download
            dom.style.display = 'none'
            dom.href = url
            dom.rel = 'noopener'
            dom.setAttribute('download', resFileName) // 指示浏览器下载url,而不是导航到它；因此将提示用户将其保存为本地文件
            document.body.appendChild(dom)
            dom.click()
          }
        } else {
          throw new Error('文件已破坏！')
        }
        resolve()
      })
      .catch(error => {
        reject(error)
      })
  })
}
// 动态引入图片获取图片url
export function getFileUrl (subName) {
  return new URL(`../../assets/${subName}`, import.meta.url).href
}
