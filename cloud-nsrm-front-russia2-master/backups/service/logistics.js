/**
 * 物流管理API
 */

import http from '@/utils/axios/http'
const getUrl = path => `${path}`

export const listMater = {
  // 模板下载列表
  listPage: async data =>
    http({
      url: getUrl(
        '/api-base/base/material-item-attribute/listMaterialItemAttribute'
      ),
      method: 'POST',
      data,
      loading: true
    })
}
/**
 * 贸易术语组合
 */

export const tradetermscombination = {
  list: async data =>
    http({
      url: getUrl('/api-pd/logistics/tradetermscombination/listPage'),
      method: 'POST',
      data,
      loading: true
    }),

  add: async data =>
    http({
      url: getUrl('/api-pd/logistics/tradetermscombination/add'),
      method: 'POST',
      data,
      loading: true
    }),

  update: async data =>
    http({
      url: getUrl('/api-pd/logistics/tradetermscombination/modify'),
      method: 'POST',
      data,
      loading: true
    }),

  delete: async id =>
    http({
      url: getUrl('/api-pd/logistics/tradetermscombination/delete'),
      method: 'GET',
      params: { id },
      loading: true
    }),
  bathDelete: async id =>
    http({
      url: getUrl('/api-pd/logistics/tradetermscombination/bathDelete'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  batchSaveOrUpdate: async data =>
    http({
      url: getUrl('/api-pd/logistics/tradetermscombination/batchSaveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    })

}

export const bidding = {
  win: async data =>
    http({
      url: getUrl('/api-pd/evaluation/changeWinStatus'),
      method: 'POST',
      data
    }),
  loss: async data =>
    http({
      url: getUrl('/api-pd/evaluation/changeFailStatus'),
      method: 'POST',
      data
    }),
  saveBatchCurrency: async data =>
    http({
      url: getUrl('/api-pd/bidInitiating/biding/saveBatchCurrency'),
      method: 'POST',
      data
    }),
  expertListPage: async data =>
    http({
      url: getUrl('/api-bid/bidExpert/listPage'),
      method: 'POST',
      data
    }),
  getCurrencyByBidingId: async bidingId =>
    http({
      url: getUrl('/api-pd/bidInitiating/biding/getCurrencyByBidingId'),
      method: 'GET',
      params: { bidingId }
    }),
  getRateByCode: async ({ fromCode, toCode }) =>
    http({
      url: getUrl('/api-pd/bidInitiating/biding/getRateByCode'),
      method: 'GET',
      params: { fromCode, toCode }
    })
}
