/**
 * 报价模板
 */
import http from '@/utils/axios/http'

// 报价属性
const attr = {
  // 列表查询
  listPageUrl: '/api-sou/buyer/quote-temp/attr/page',
  // 删除
  delete: id =>
    http({
      url: `/api-sou/buyer/quote-temp/attr/remove/${id}`,
      method: 'POST',
      loading: true
    }),
  // 生效
  valid: id =>
    http({
      url: `/api-sou/buyer/quote-temp/attr/valid/${id}`,
      method: 'POST',
      loading: true
    }),
  // 失效
  invalid: id =>
    http({
      url: `/api-sou/buyer/quote-temp/attr/invalid/${id}`,
      method: 'POST',
      loading: true
    }),
  // 复制
  copy: id =>
    http({
      url: `/api-sou/buyer/quote-temp/attr/copy/${id}`,
      method: 'POST',
      loading: true
    }),
  // 创建新版本
  newVersion: id =>
    http({
      url: `/api-sou/buyer/quote-temp/attr/new-version/${id}`,
      method: 'POST',
      loading: true
    }),
  // 编辑属性报价保存，包含新增
  edit: data =>
    http({
      url: '/api-sou/buyer/quote-temp/attr/edit',
      method: 'POST',
      data,
      loading: true
    }),
  // 获取报价属性详情信息
  getDetail: id =>
    http({
      url: `/api-sou/buyer/quote-temp/attr/detail/${id}`,
      method: 'GET',
      loading: true
    })
}

// 自定义函数配置
const fun = {
  // 列表查询
  listPageUrl: '/api-sou/buyer/quote-temp/api/page',
  // 保存 / 提交
  edit: data =>
    http({
      url: '/api-sou/buyer/quote-temp/api/edit',
      method: 'POST',
      data,
      loading: true
    }),
  // 查询
  get: id =>
    http({
      url: `/api-sou/buyer/quote-temp/api/get/${id}`,
      method: 'GET',
      loading: true
    }),
  // 测试
  test: (id, data) =>
    http({
      url: `/api-sou/buyer/quote-temp/api/test/${id}`,
      method: 'POST',
      data,
      loading: true
    })
}

// 报价模板
const template = {
  // 列表查询
  listPageUrl: '/api-sou/buyer/quote-temp/page',
  // 删除
  delete: id =>
    http({
      url: `/api-sou/buyer/quote-temp/remove/${id}`,
      method: 'POST',
      loading: true
    }),
  // 生效
  valid: id =>
    http({
      url: `/api-sou/buyer/quote-temp/valid/${id}`,
      method: 'POST',
      loading: true
    }),
  // 失效
  invalid: id =>
    http({
      url: `/api-sou/buyer/quote-temp/invalid/${id}`,
      method: 'POST',
      loading: true
    }),
  // 获取报价模板详情信息
  getDetail: id =>
    http({
      url: `/api-sou/buyer/quote-temp/detail/${id}`,
      method: 'GET',
      loading: true
    }),
  // 编辑报价模板保存，包含新增
  save: data =>
    http({
      url: '/api-sou/buyer/quote-temp/edit',
      method: 'POST',
      data,
      loading: true
    }),
  // 提交报价模板
  submit: data =>
    http({
      url: '/api-sou/buyer/quote-temp/submit',
      method: 'POST',
      data,
      loading: true
    }),
  // 获取报价模板预览数据
  getPreviewData: id =>
    http({
      url: `/api-sou/buyer/quote-temp/preview/${id}`,
      method: 'GET',
      loading: true
    }),
  // 报价模板测算
  previewTest: (id, data) =>
    http({
      url: `/api-sou/buyer/quote-temp/previewTest/${id}`,
      method: 'POST',
      data,
      loading: true
    })
}

export default {
  attr,
  fun,
  template
}
