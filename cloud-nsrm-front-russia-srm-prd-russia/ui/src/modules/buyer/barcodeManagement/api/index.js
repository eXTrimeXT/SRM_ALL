/* 条码管理 */

import http from '@/utils/axios/http'

/* 条码生成规则配置 */
export const boxBarCodeRuleApi = {
  delete: async id =>
    http({
      url: '/api-base/base/barcoderulehead/delete',
      method: 'GET',
      params: { id },
      loading: true
    }),

  getById: async id =>
    http({
      url: '/api-base/base/barcoderulehead/get',
      method: 'GET',
      params: { id },
      loading: true
    }),

  addOrUpdate: async data =>
    http({
      url: '/api-base/base/barcoderulehead/addOrUpdate',
      method: 'POST',
      data,
      loading: true
    })
}

/* 装箱标签配置 */
export const boxtagconfigApi = {
  add: async data =>
    http({
      url: '/api-base/base/boxtagconfig/add',
      method: 'POST',
      data,
      loading: true
    }),

  update: async data =>
    http({
      url: '/api-base/base/boxtagconfig/modify',
      method: 'POST',
      data,
      loading: true
    }),

  delete: async id =>
    http({
      url: '/api-base/base/boxtagconfig/delete',
      method: 'GET',
      params: { id },
      loading: true
    }),

  batchSaveOrUpdate: async data =>
    http({
      url: '/api-base/base/boxtagconfig/batchSaveOrUpdate',
      method: 'POST',
      data,
      loading: true
    }),
  getByCategoryId: async categoryId =>
    http({
      url: '/api-base/material/materialItem/get',
      method: 'GET',
      params: { categoryId },
      loading: true
    }),
  getByCategoryIdAndBusinessBoxType: async data =>
    http({
      url: '/api-base/base/boxtagconfig/listByCategoryIdAndBusinessBoxType',
      method: 'POST',
      data,
      loading: true
    })
}

/* 内箱条码 */
export const innerBoxCodeApi = {
  add: async data =>
    http({
      url: '/api-base/base/innerboxcode/generateInnerBoxCode',
      method: 'POST',
      data,
      loading: true
    }),

  getById: async id =>
    http({
      url: '/api-base/base/innerboxcode/get',
      method: 'GET',
      params: { id },
      loading: true
    }),

  update: async data =>
    http({
      url: '/api-base/base/innerboxcode/modify',
      method: 'POST',
      data,
      loading: true
    }),

  delete: async id =>
    http({
      url: '/api-base/base/innerboxcode/delete',
      method: 'GET',
      params: { id },
      loading: true
    })
}

/**
 * 内外箱关联
 */
export const innerOuterRelationApi = {
  delete: async data =>
    http({
      url: '/api-base/base/innerouterrelation/delete',
      method: 'POST',
      data,
      loading: true
    })
}

/**
 * 外箱条码
 */
export const outerBoxCodeApi = {
  generateAutoCode: async data =>
    http({
      url: '/api-base/base/outerboxcode/generateAutoCode',
      method: 'POST',
      data,
      loading: true
    }),
  delete: async id =>
    http({
      url: '/api-base/base/outerboxcode/delete',
      method: 'GET',
      params: { id },
      loading: true
    }),

  unbindByDelivery: async id =>
    http({
      url: '/api-base/base/outerboxcode/unbindByDelivery',
      method: 'GET',
      params: { id },
      loading: true
    }),

  bindByDelivery: async data =>
    http({
      url: '/api-base/base/outerboxcode/bindByDelivery',
      method: 'POST',
      data,
      loading: true
    }),

  getById: async id =>
    http({
      url: '/api-base/base/outerboxcode/get',
      method: 'GET',
      params: { id },
      loading: true
    }),

  update: async data =>
    http({
      url: '/api-base/base/outerboxcode/update',
      method: 'POST',
      data,
      loading: true
    }),

  generateOuterBoxCode: async data =>
    http({
      url: '/api-base/base/outerboxcode/generateOuterBoxCode',
      method: 'POST',
      data,
      loading: true
    })
}
