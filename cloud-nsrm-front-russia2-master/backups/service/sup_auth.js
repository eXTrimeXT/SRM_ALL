/**
 * API
 */

import http from '@/utils/axios/http'
const getUrl = path => `${path}`

export const getEntryConfigRecord = async params =>
  http({
    url: getUrl('/api-sup/entry/entryConfig/getEntryConfigRecord'),
    method: 'GET',
    params,
    loading: true
})
