import store from '@/store'
import { findMenuInfoByPath } from '@/utils'
import Base64 from 'jszip/lib/base64'

// 获取菜单ID
export function getMenuInfo (url) {
  let curMenuInfo = {}
  let curMenuId = '-1'
  if (fixedDesInterface.includes(url)) {
    curMenuInfo = commonInterfaceDes[url]
  } else {
    const menus = store.getters.userInfo.menus || {} // 当前用户下面的菜单
    let fnPath = window.location.hash || ''
    let curRouter = fnPath.replace(/#/g, '')// 当前路由路径
    curMenuInfo = findMenuInfoByPath(curRouter, menus) || {}
    curMenuId = curMenuInfo.permissionId ? curMenuInfo.permissionId : '-1' // 没找到的返回-1
    if (curMenuId == '-1') {
      curMenuInfo = urlWhiteListFunction.find(i => (i.path == curRouter)) || {}
    }
  }

  let secretJson = {
    functionId: curMenuInfo.functionId ? curMenuInfo.functionId : '-1',
    functionCode: curMenuInfo.functionCode || '',
    functionName: curMenuInfo.functionName || '',
    permissionId: curMenuInfo.permissionId ? curMenuInfo.permissionId : '-1',
    permissionName: curMenuInfo.permissionName || ''
  }
  const secretJsonKey = Base64.encode(encodeURI(JSON.stringify(secretJson)))
  return {
    menuId: curMenuId,
    secretKey: secretJsonKey
  }
}

// 不需要添加菜单id的URL
export const urlWhiteList = [
  '/api-rbac/user/current',
  '/locale/get',
  '/locale/modify',
  '/api-base/organization/relation/tree',
  '/api-base/organization/relation/treeNew',
  '/api-base/common/currentKey',
  '/api-rbac/systemStyle/getSystemStyle',
  '/api-base/dict/base-dict-item/listAllByParam',
  '/api-base/dict/base-dict-item/listByDictCode',
  '/api-base/base/page_view_config/getCurrent'
]

// 白名单功能信息
export const urlWhiteListFunction = [
  { path: '/login', functionName: '登录' },
  { path: '/registered', functionName: '注册' },
  { path: '/portalBidding/vendorBiddingDetail', functionName: '门户招标详情' },
  { path: '/sourcing/sourcingApplicationDetail', functionName: '门户寻源详情' },
  { path: '/userManage/profile', functionName: '个人资料' },
  { path: '/userManage/accountSecurity', functionName: '账号安全中心' },
  { path: '/baseSettingCommon/dynamicReportPage', functionName: '报表页面' },
  { path: '/agentCenter/flowTaskView', functionName: '单据审批' },
  { path: '/mflowTaskView', functionName: '单据审批' },
  { path: '/forgetPassword', functionName: '忘记密码' },
  { path: '/userManage/updatePassword', functionName: '修改密码' },
  { path: '/baseSetting/businessOperationLog', functionName: '业务监控日志' }
]

export const fixedDesInterface = [
  '/sys/logout',
  '/api-rbac/user/current',
  '/sys/login'
]

// 固定入参的接口，不根据菜单改变而调整
export const commonInterfaceDes = {
  '/sys/logout': {
    functionId: '-1',
    functionCode: '',
    functionName: '',
    permissionId: '-1',
    permissionName: '退出登录'
  },
  '/api-rbac/user/current': {
    functionId: '-1',
    functionCode: '',
    functionName: '',
    permissionId: '-1',
    permissionName: '查询用户信息'
  },
  '/sys/login': {
    functionId: '-1',
    functionCode: '',
    functionName: '',
    permissionId: '-1',
    permissionName: '登录'
  }
}
