import lang from '@/lang'
import { logoStyle } from '@/config/logo-config'

const title = logoStyle == 'style1' ? lang.t('proName') : lang.t('proNameCus') // lang.t('proName') getSystemTheme().webTitle

export default function getPageTitle (pageTitle, titleSuffix = '') {
  // 后缀系统名称
  let suffix = titleSuffix || title
  if (pageTitle) {
    return `${lang.t(pageTitle)} - ${suffix}`
  }
  return `${suffix}`
}
