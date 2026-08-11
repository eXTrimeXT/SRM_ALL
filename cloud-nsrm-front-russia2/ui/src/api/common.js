import http from '@/utils/axios/http'

// 文件上传
export const FILE_UPLOAD = '/api-file/file/fileupload/upload'

// 文件下载
export const FILE_DOWNLOAD = '/api-file/file/fileupload/download'

// 文件删除 用于日志审计
export const FILE_DELETE = '/api-file/file/fileupload/dismiss'

// IDE 文件服务 暂用于服务分流判断
export const IDE_FILE_UPLOAD = '/ide/preview/attachment/upload'

// IDE 文件下载服务 暂用于服务分流判断
export const IDE_FILE_DOWNLOAD_PATTERN = /^\/ide\/preview\/attachment\/download\/(.+)$/

// 文件删除
export function fileDel (id) {
  return http({
    url: '/api-file/file/fileupload/delete',
    method: 'POST',
    params: { id }
  })
}

// 文件删除
export function fileDismiss (fileKey) {
  return http({
    url: FILE_DELETE,
    method: 'POST',
    returnDirectly: true,
    data: { fileKey }
  })
}

export function getFileInfo (id) {
  return http({
    url: '/api-file/file/fileupload/get',
    method: 'Get',
    params: { id }
  })
}

export function getCurrentKey () {
  return http({
    url: '/api-base/common/currentKey',
    method: 'GET',
    params: {}
  })
}

// 获取文件列表 根据业务单据ID查询对应附件  businessId
export function apiGetFileList (params) {
  return http({
    url: '/api-file/file/fileupload/listPageByBusinessId',
    method: 'POST',
    params: params
  })
}

// 获取数据字典接口
export function getDictItem (dictCode) {
  return http({
    url: '/api-base/dict/base-dict-item/listAllByParam',
    method: 'POST',
    data: { dictCode }
  })
}
// 批量查询字典 //入参 [{“dictCode”:”FILE_TYPE”},{“dictCode”:”FILE_LIMIT”}]
export function getDictItemList (dictCodeArr) {
  return http({
    url: '/api-base/dict/base-dict-item/listAllByListParam',
    method: 'POST',
    data: dictCodeArr
  })
}

// 查询所有单位
export function getAllPurUnit (data) {
  return http({
    url: '/api-base/purchase/purchaseUnit/listAll',
    method: 'POST',
    data: data
  })
}
// 查询所有币种
export function getAllPurCurrency (data) {
  return http({
    url: '/api-base/purchase/purchaseCurrency/listAll',
    method: 'POST',
    data: data
  })
}
// 查询所有税率
export function getAllPurTax (data) {
  return http({
    url: '/api-base/purchase/purchaseTax/listAll',
    method: 'POST',
    data: data
  })
}

// 查询省市区域
export function getRegion (data) {
  return http({
    url: '/api-base/base/region/queryBaseRegion',
    method: 'POST',
    params: data
  })
}
// 查询国家省市区域
export function getRegionBy (data) {
  return http({
    url: '/api-pd/logistics/region/queryRegionBy',
    method: 'get',
    params: data,
    loading: false
  })
}
// 获取语言列表
export function getAllLangList (data) {
  return http({
    url: '/api-base/dict/base-dict-language/listAll',
    method: 'POST',
    params: data
  })
}

// 查询全部有效期内的品类
export function purchaseCategoryTree (data) {
  return http({
    url: '/api-base/purchase/purchaseCategory/tree',
    method: 'GET',
    params: data
  })
}

// 查询全部有效期内的品类
export function getAllRule (data) {
  return http({
    url: '/api-bid/evaluation/ruleConfig/listPage',
    method: 'POST',
    data
  })
}

// 组织树结构 新增虚拟ID
export function newOrganaztionTreehttp (data) {
  return http({
    url: '/api-base/organization/relation/treeNew',
    method: 'POST',
    data
  })
}

// 组织树结构
export function organaztionTreehttp (data) {
  return http({
    url: '/api-base/organization/relation/tree',
    method: 'POST',
    data
  })
}

// 供应商组织权限数据
export function orgCategorySupplierTree (data) {
  return http({
    url: '/api-sup/info/orgCategory/supplierTree',
    method: 'POST',
    data
  })
}

// 根据供应商公司ID查询合作组织
export function getInfoByParam (params) {
  http({
    url: '/api-sup/info/companyInfo/getInfoByParam',
    method: 'POST',
    params
  })
}

// 功能图标列表
export function funIconListPage (params) {
  http({
    url: '/api-file/file/fileupload/listIcon',
    method: 'POST',
    params: params,
    loading: true
  })
}

// 角色列表
export const listRoleInfo = async data =>
  http({
    url: '/api-rbac/role/roleInfo/listRole?timestamp=' + new Date().getTime(),
    method: 'POST',
    loading: true,
    data
  })

// 配置导引查询
export const getConfigGuide = async data =>
  http({
    url: '/api-base/configGuide/getInfoByUser',
    method: 'GET',
    data,
    loading: true
  })

// 登录之前通过接口获取获取公共配置, 后面如果有其他配置也可以在这个接口返回来
export const getOpenConfigBeforeLogin = async data =>
  http({
    url: '/sys/openConfig',
    unToken: true,
    method: 'GET',
    data
  })

// 登录之后通过接口获取公共配置, 后面如果有其他配置也可以在这个接口返回来
export const getOpenConfigAfterLogin = async data =>
  http({
    url: '/sys/loginConfig',
    method: 'GET',
    data
  })

// 获取系统主题配置信息
export const getSystemTheme = async data =>
  http({
    url: '/api-base/systemTheme/get',
    unToken: true,
    method: 'GET',
    data
  })
