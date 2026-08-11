// 系统配置
import config from '@/config/user.env'
import { uploadTypePj } from './configCus/pjConfig'

/**
 * 系统url
 * 没有二级目录 输出 https://XXX.meicloud.com/
 * 有二级目录 输出 https://XXX.meicloud.com/srm/
 * 主要用于需要拼系统的路径的时候引用，减少每个地方都写，有二级目录的时候漏掉
 * vue 文件里面使用 this.$systemUrl, js文件里面需要引入后调用
 * 调用的地方有地方用接口的 sysPrefix,所以去掉pathname 后面的/,功能里面在处理，用法和原来的 window.location.origin一样
 * **/
const pathname = window.location.pathname
export const systemUrl = window.location.origin + pathname.substring(0, pathname.length - 1)

/**
 * 1、是否开启单点登陆共享 cookie
 * 生产和uat环境开启 dev环境关闭
 */
export const isSetDomain = import.meta.env.VUE_APP_ISSETDOMAIN === 'Y' // 开启域名配置
// 域值
export const domain = () => {
  let initDomain = '.meicloud.com' // 美云
  let winDomain = document.domain // 当前系统的域
  if (winDomain.indexOf('.gcycloud.cn') > -1) { // 博斯商城域 http://mysc.gcycloud.cn/
    initDomain = '.gcycloud.cn'
  }
  let domainVal = isSetDomain ? initDomain : winDomain
  return domainVal
}

/**
 * 2、接口前缀适配
 */
// srm系统
export const sysPrefix = () => {
  return '/cloud-srm'
}

/**
 * 3、设置请求报错提示是否开启提示 traceInfo
 * 值被指 Y表示开启 N表示不开启 默认不开启
 */
export const isShowTraceInfo = 'N'

/**
 * 4、设置是否开启单点登录 isSinglePoint
 * 值被指 Y表示开启 N表示不开启 默认不开启
 * 产品单点接入iam 单点登录，设置开关
 * 单点登录成功后将token写回 cookie的 keyToken值
 */
export const isSinglePoint = config.isSinglePoint

/**
 * 5、单点登录- 登录url
 */
export const singlePointLoginUrl = () => {
  let singleBaseUrl = systemUrl
  let sUrl = `${singleBaseUrl}/cloud-srm/sys/sso/transfer?redirectUri=${singleBaseUrl}/`
  let service = encodeURI(sUrl)
  return `${singleBaseUrl}/cloud-srm/sys/sso/forward?service=${service}`
}

/**
 * 6、单点登录- 退出url
 */
export const singlePointLogoutUrl = () => {
  let singleBaseUrl = systemUrl
  let redirectUri = encodeURI(singleBaseUrl + '/')
  return `${singleBaseUrl}/cloud-srm/sys/iam/logout?redirectUri=${redirectUri}`
}

/**
 * 7、门户页是否开启显示 公开寻源信息
 * 值 Y表示开启 N表示不开启 默认不开启
 */
export const isPortalSourcing = 'Y'
/**
 * 7、门户页是否开启显示 公开招标信息
 * 值 Y表示开启 N表示不开启 默认不开启
 */
export const isPortalBidding = 'Y'

/**
 * 8、文件相关全局配置
 */
// 上传支持的图片格式和大小
const pictureOptions = {
  accept: ['.jpg', '.jpeg', '.png', '.pdf', '.ico', '.svg', ...uploadTypePj.picture.accept]
}
// 导入支持的类型和大小
const importOptions = {
  // 默认最大5M
  size: uploadTypePj.import.size || 1024 * 5,
  accept: ['.xls', '.xlsx', ...uploadTypePj.import.accept]
}
// 默认类型
const defaultOptions = {
  // 默认1.5G (1024 + 1024 / 2) * 1024 = 1572864
  size: uploadTypePj.default.size || (1024 + 1024 / 2) * 1024,
  // 二开
  accept: [
    ...pictureOptions.accept,
    ...importOptions.accept,
    '.doc', '.docx', '.ppt', '.pptx',
    '.zip', '.pdf', '.ofd', '.rar', '.7z',
    '.cad', '.wps', '.gzip', '.md', '.tif',
    '.tiff', '.bmp', '.psd', '.gif', '.docm',
    '.ai', '.MP3', '.WAV', '.WMA', '.wmv', '.rm',
    '.rmvb', '.mp4', '.avi', '.DWG', '.c4d', '.obj',
    '.fbx', '.word', '.tar', '.wim', '.wav', '.wma', '.wma'
  ]
}
/**
 * 对全局上传组件以及文件组件提供默认配置
 * 文件预览支持以下格式:
 * 支持 office, pdf, cad 等办公文档
 * 支持 txt, xml(渲染), md(渲染), java, php, py, js, css 等所有纯文本
 * 支持 zip, rar, jar, tar, gzip 等压缩包
 * 支持 jpg, jpeg, png, gif, tif, tiff 等图片预览（翻转，缩放，镜像）
 * @type {{size: {default: number, import: number, picture: number}, accept: {preview: string[], default: string[], import: string[], picture: string[]}}}
 */
export const uploadConfig = {
  accept: {
    // 图片类型
    picture: pictureOptions.accept,
    // 默认类型
    default: defaultOptions.accept,
    // 导入类型
    import: importOptions.accept,
    // 可在线预览文件类型
    preview: [
      '.xlsx', '.xls', '.doc', '.docx', '.ppt', '.pptx', '.pdf', '.cad',
      '.txt', '.xml', '.md', '.java', '.php', '.py', '.js', '.css', '.scss', '.stylus',
      '.zip', '.rar', '.jar', '.tar', '.gzip',
      '.jpg', '.jpeg', '.png', '.gif', '.tif', '.tiff', '.7z'
    ]
  },
  size: {
    // 图片类型
    picture: defaultOptions.size,
    // 默认类型
    default: defaultOptions.size,
    // 导入类型
    import: importOptions.size
  }
}

/**
 * 9. 全局格式化数字配置
 */
export const FLOAT_FORMAT_MAGIC = {
  // 数字最大 12位 + 4位
  NUMBER: 999999999999.9999,
  // 数字最小 12位 + 4位
  NEGATIVE: -999999999999.9999,
  // 小数位最大4位
  DIGITS: 4
}
