/**
 * 切换log配置,演示切换logo,中英切换语言logo跟着切换
 * 参数: logoType style1 | style2
 * author: zhaomz1
 * **/
import Cookies from 'js-cookie'
import lang from '@/lang'
import store from '@/store'
import { systemUrl } from '@/config/sysConfig'
import { sysPrefix } from '@/config/ipConfig'

import favicon from '@/assets/favicon.ico'
import mainLogoZh from '@/assets/logo/srm-logo-white-zh.svg'
import mainLogoEn from '@/assets/logo/srm-logo-white-en.svg'
import subLogoZh from '@/assets/logo/srm-logo-blue-zh.svg'
import subLogoEn from '@/assets/logo/srm-logo-blue-en.svg'
import placeholderLogo from '@/assets/logo/portalItem.png'

// 演示转化需要
import mainLogo1 from '@/assets/logo/srm-logo-bak.svg'
import subLogo1 from '@/assets/logo/MClogo-bak.svg'
import placeholderLogo1 from '@/assets/logo/portalItem-bak.png'
// 登录框
import banner1 from '@/views/login/img/banner1.png'
import banner2 from '@/views/login/img/banner2.png'
import banner3 from '@/views/login/img/banner3.png'
import banner4 from '@/views/login/img/banner4.png'
import banner5 from '@/views/login/img/banner5.png'

// logo模式
export const logoStyle = import.meta.env.VUE_APP_LOGO_TYPE || 'style1'

export const getLogoInfo = () => {
  let logoType = logoStyle
  let loginBanner = [banner1, banner2, banner3, banner4, banner5]
  const sysConfInfo = {
    loginBanner: loginBanner, // 登录页背景图
    favicon: favicon,
    mainLogo: mainLogoZh,
    subLogo: subLogoZh,
    menuMainLogo: mainLogoZh,
    menuSubLogo: mainLogoZh,
    placeholderLogo: placeholderLogo, // 占位符logo
    copyright: lang.t('vendorMod.copyright'), // 版权信息
    webName: { // 系统名称，登录框大文字
      zh_CN: lang.t('loginBoxTitle'),
      en_US: lang.t('loginBoxTitle'),
      ja_JP: lang.t('loginBoxTitle')
    },
    webDes: { // 登录页logo旁边的文字描述
      zh_CN: lang.t('loginLogoDes'),
      en_US: lang.t('loginLogoDes'),
      ja_JP: lang.t('loginLogoDes')
    },
    webTitle: { // 浏览器tab title 后缀
      zh_CN: lang.t('proName'),
      en_US: lang.t('proName'),
      ja_JP: lang.t('proName')
    }
  }
  // 默认场景 适配多语言切换
  if (logoType == 'style1') {
    return sysConfInfo // sysConfInfo[language]
  } else {
    // 演示场景
    return {
      loginBanner: loginBanner, // 登录页背景图
      favicon: favicon,
      mainLogo: mainLogo1,
      subLogo: subLogo1,
      menuMainLogo: subLogo1,
      menuSubLogo: subLogo1,
      placeholderLogo: placeholderLogo1, // 占位符logo
      copyright: lang.t('vendorMod.copyrightCus'),
      webName: {
        zh_CN: lang.t('loginBoxTitleDemo'),
        en_US: lang.t('loginBoxTitleDemo'),
        ja_JP: lang.t('loginBoxTitleDemo')
      },
      webDes: { // 登录页logo旁边的文字描述
        zh_CN: lang.t('loginLogoDes'),
        en_US: lang.t('loginLogoDes'),
        ja_JP: lang.t('loginLogoDes')
      },
      webTitle: {
        zh_CN: lang.t('proName'),
        en_US: lang.t('proName'),
        ja_JP: lang.t('proName')
      }
    }
  }
}

/**
 * 开启系统主题配置
*/
export const systemThemeFromServe = true

/**
 *  根据文件id 返回img 主题图片url,这个接口只针对主题表里面的附件使用
 * @param fileId
 * @returns {`${string}${string}/api-base/systemTheme/file/download?fileUploadId=${string}`}
 */
export const getThemeImgSrc = (fileId) => {
  return `${systemUrl}${sysPrefix()}/api-base/systemTheme/file/download?fileUploadId=${fileId}`
}

/**
 * 系统主题设置数据转换
 * */
export const systemThemeFormat = (theme) => {
  let logoInfo = getLogoInfo() // 本地设置默认配置
  const {
    webName = [], webDes = [], webTitle = [],
    favicon = [], mainLogo = [], subLogo = [],
    menuMainLogo = [], menuSubLogo = [], loginBanner = []
  } = theme
  // 查询数字里面对应语言的条目
  const getLangItem = (arr = [], lang) => {
    let rowData = arr.find(i => (i.language == lang))
    if (rowData) {
      return rowData.themeValue
    } else {
      return ''
    }
  }

  // 获取单个图片路径
  const getThemePic = (arr = []) => {
    let picFileId = ''
    if (arr.length > 0) {
      picFileId = arr[0].fileId
    }
    // 开启读后台接口返回
    if (picFileId) {
      return getThemeImgSrc(picFileId)
    } else {
      return ''
    }
  }

  // 登录页面banner
  const getLoginBanner = (arr = []) => {
    let bannerArr = []
    if (arr.length > 0) {
      bannerArr = arr.map(i => (getThemeImgSrc(i.fileId)))
      return bannerArr
    } else {
      return null
    }
  }

  let themeObj = {
    loginBanner: getLoginBanner(loginBanner) || logoInfo.loginBanner,
    favicon: getThemePic(favicon) || logoInfo.favicon, // favicon
    mainLogo: getThemePic(mainLogo) || logoInfo.mainLogo,
    subLogo: getThemePic(subLogo) || logoInfo.subLogo,
    menuMainLogo: getThemePic(menuMainLogo) || logoInfo.mainLogo,
    menuSubLogo: getThemePic(menuSubLogo) || logoInfo.mainLogo,
    placeholderLogo: placeholderLogo, // 占位符logo
    webName: {
      zh_CN: getLangItem(webName, 'zh_CN') || logoInfo.webName['zh_CN'],
      en_US: getLangItem(webName, 'en_US') || logoInfo.webName['en_US'],
      ja_JP: getLangItem(webName, 'ja_JP') || logoInfo.webName['ja_JP']
    },
    webDes: {
      zh_CN: getLangItem(webDes, 'zh_CN') || logoInfo.webDes['zh_CN'],
      en_US: getLangItem(webDes, 'en_US') || logoInfo.webDes['en_US'],
      ja_JP: getLangItem(webDes, 'ja_JP') || logoInfo.webDes['ja_JP']
    },
    webTitle: {
      zh_CN: getLangItem(webTitle, 'zh_CN') || logoInfo.webTitle['zh_CN'],
      en_US: getLangItem(webTitle, 'en_US') || logoInfo.webTitle['en_US'],
      ja_JP: getLangItem(webTitle, 'ja_JP') || logoInfo.webTitle['ja_JP']
    },
    copyright: lang.t('vendorMod.copyright') // 版权信息
  }
  return themeObj
}

/**
 * 获取系统主题信息
 * */
export const getSystemTheme = () => {
  let logoInfo = getLogoInfo() // 本地设置默认配置
  let language = Cookies.get('language') || 'zh_CN'
  let systemThemeLocal = sessionStorage.getItem('systemTheme')
  let systemThemeJson = JSON.parse(systemThemeLocal)
  let systemTheme = null
  if (systemThemeFromServe) {
    systemTheme = systemThemeJson
  } else {
    systemTheme = logoInfo
  }
  const { webName, webDes, webTitle, ...rest } = systemTheme
  return {
    webName: webName[language] || logoInfo.loginBoxTitle,
    webDes: webDes[language] || logoInfo.loginLogoDes,
    webTitle: webTitle[language] || logoInfo.webTitle,
    ...rest
  }
}

/**
 * 通过主题配置设置浏览器tab favicon
*/
export const changeFavicon = (link) => {
  if (!link) {
    return
  }
  let $favicon = document.querySelector('link[rel="icon"]')
  if ($favicon !== null) {
    $favicon.href = link
  } else {
    $favicon = document.createElement('link')
    $favicon.rel = 'icon'
    $favicon.href = link
    document.head.appendChild($favicon)
  }
}

export const themeDefault = {
  theme: '#0077FF', // 主题色
  leftMenuContent: {
    bgColor: '#1E212E', // 背景颜色
    unfoldColor: '#13151D', // 展开背景颜色 #2A2E40
    topMenuFontColor: '#D9D9D9', // 一级菜单字体颜色
    childMenuFontColor: '#D9D9D9', // 子级菜单字体
    activeLeftBorderColor: '#0077FF', // 选中时边框色 +
    activeBgColor: '#0077FF', // 选中时背景色 +
    activeFontColor: '#FFFFFF', // 字体active颜色 +
    hoverFontColor: '#FFFFFF' // 字体hover颜色 +
  },
  pageContent: {
    buttonBorderColor: '#B9BABD',// 按钮边框颜色 #96999C
    dividerColor: '#DCDDDE', // 分割线颜色
    inputColor: '#B9BABD', // 输入框颜色 #96999C
    tableBorderColor: '#DCDDDE', // 表格边框颜色
    tableHeaderBgColor: '#F6F6F6', // 表头背景颜色 #F1F2F2
    tableTitleColor: '#161C24', // 表头标题颜色 #393E45
    tableRowCurrentBgColor: '#E7F2FF', // 当前选中背景色 #E6F6FF
    tableRowHoverBgColor: '#E7F2FF', // hover颜色 #a3daff
    tableRowStripedBgColor: '#FAFBFB' // 隔行颜色 +
  }
}
