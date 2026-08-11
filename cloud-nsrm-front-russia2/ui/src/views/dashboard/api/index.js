import http from '@/utils/axios/http'
const getUrl = path => `${path}`

export const findAll = data => http({
  url: getUrl('/api-rbac/commonFun/findAll'),
  method: 'GET',
  data
})

export const save = data => http({
  url: getUrl('/api-rbac/commonFun/save'),
  method: 'POST',
  data
})

export const vendorTask = data => http({
  url: getUrl('/api-base/work/workCount'),
  method: 'GET',
  data
})
// 供应商排序保存
export const vendorTaskSaveSort = data => http({
  url: getUrl('/api-base/work/saveWorkCountSort'),
  method: 'POST',
  data
})
// 供应商是否需要廉洁考试校验
export const isCheckExam = data => http({
  url: getUrl('/api-pj/sun-honesty/checkExam'),
  method: 'POST',
  data
})
