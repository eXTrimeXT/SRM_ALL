/**
 * API 接口配置
 */

import http from '@/utils/axios/http'
const getUrl = path => `${path}`

export const getHttpColumns = async data =>
  http({
    url: getUrl('/api-ac/interfaceconfig/getHttpColumns'),
    method: 'POST',
    data,
    loading: true
})

export const getColumnBySql = async params =>
  http({
    url: getUrl('/api-ac/interfaceconfig/getColumnBySql'),
    method: 'POST',
    params,
    loading: true
})

export const getSqlResults = async data =>
  http({
    url: getUrl('/api-ac/interfaceconfig/getSqlResults'),
    method: 'POST',
    data,
    loading: true
})

export const showDoc = async data =>
  http({
    url: getUrl('/api-ac/interfaceconfig/showDoc'),
    method: 'POST',
    data,
    loading: true
})

export const testInterface = async data =>
  http({
    url: getUrl('/api-ac/interfaceconfig/testInterface'),
    method: 'POST',
    data,
    loading: true
})

export const getHttpParam = async data =>
  http({
    url: getUrl('/api-ac/interfaceconfig/getHttpParam'),
    method: 'POST',
    data,
    loading: true
})
