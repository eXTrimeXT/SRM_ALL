/**
 * 自动生成的API
 */

import http from '@/utils/axios/http'
const prefix = '/api-base/base/drawingshead'
const getUrl = path => prefix + '/' + path

export const drawingshead = {
  list: async data =>
    http({
      url: getUrl('listPage'),
      method: 'POST',
      data,
      loading: true
    }),

    getById: async id =>
    http({
      url: getUrl('get'),
      method: 'GET',
      params: { id },
      loading: true
    }),

    add: async data =>
      http({
        url: getUrl('add'),
        method: 'POST',
        data,
        loading: true
      }),
    update: async data =>
    http({
      url: getUrl('update'),
      method: 'POST',
      data,
      loading: true
    }),

    updateStatus: async data =>
    http({
      url: getUrl('update/status'),
      method: 'POST',
      data,
      loading: true
    }),

  delete: async id =>
    http({
      url: getUrl('delete'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  bathDelete: async data =>
  http({
    url: getUrl('bathDelete'),
    method: 'POST',
    data,
    loading: true
  }),

  cancelEdit: async data => {

  }
}

/**
 * 装箱标签配置
 */
 const boxTagPrefix = '/api-base/base/boxtagconfig'
 const getboxTagUrl = path => boxTagPrefix + '/' + path

 export const boxtagconfig = {
   list: async data =>
     http({
       url: getboxTagUrl('listPage'),
       method: 'POST',
       data,
       loading: true
     }),

   add: async data =>
     http({
       url: getboxTagUrl('add'),
       method: 'POST',
       data,
       loading: true
     }),

   update: async data =>
     http({
       url: getboxTagUrl('modify'),
       method: 'POST',
       data,
       loading: true
     }),

   delete: async id =>
     http({
       url: getboxTagUrl('delete'),
       method: 'GET',
       params: { id },
       loading: true
     }),

   batchSaveOrUpdate: async data =>
     http({
       url: getboxTagUrl('batchSaveOrUpdate'),
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
       url: getboxTagUrl('listByCategoryIdAndBusinessBoxType'),
       method: 'POST',
       data,
       loading: true
     })
 }

/**
 * 条码规则配置
 */
const barcodeRulePrefix = '/api-base/base/barcoderulehead'
const getBarcodeRuleUrl = path => barcodeRulePrefix + '/' + path
export const barcoderulehead = {
  list: async data =>
    http({
      url: getBarcodeRuleUrl('listPage'),
      method: 'POST',
      data,
      loading: true
    }),

  delete: async id =>
    http({
      url: getBarcodeRuleUrl('delete'),
      method: 'GET',
      params: { id },
      loading: true
    }),

    getById: async id =>
    http({
      url: getBarcodeRuleUrl('get'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  addOrUpdate: async data =>
    http({
      url: getBarcodeRuleUrl('addOrUpdate'),
      method: 'POST',
      data,
      loading: true
    })

}

/**
 * 内箱条码
 */
const innerBoxCodePrefix = '/api-base/base/innerboxcode'
const getInnerBoxCodeUrl = path => innerBoxCodePrefix + '/' + path
export const innerboxcode = {
  list: async data =>
    http({
      url: getInnerBoxCodeUrl('listPage'),
      method: 'POST',
      data,
      loading: true
    }),

  add: async data =>
    http({
      url: getInnerBoxCodeUrl('generateInnerBoxCode'),
      method: 'POST',
      data,
      loading: true
    }),

    getById: async id =>
    http({
      url: getInnerBoxCodeUrl('get'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  update: async data =>
    http({
      url: getInnerBoxCodeUrl('modify'),
      method: 'POST',
      data,
      loading: true
    }),

  delete: async id =>
    http({
      url: getInnerBoxCodeUrl('delete'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  batchSaveOrUpdate: async data =>
    http({
      url: getInnerBoxCodeUrl('batchSaveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    })

}

/**
 * 外箱条码
 */
const outerBoxCodePrefix = '/api-base/base/outerboxcode'
const getOutgerBoxCodeUrl = path => outerBoxCodePrefix + '/' + path

export const outerboxcode = {
  list: async data =>
    http({
      url: getOutgerBoxCodeUrl('listPage'),
      method: 'POST',
      data,
      loading: true
    }),

  delete: async id =>
    http({
      url: getOutgerBoxCodeUrl('delete'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  unbindByDelivery: async id =>
    http({
      url: getOutgerBoxCodeUrl('unbindByDelivery'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  bindByDelivery: async data =>
    http({
      url: getOutgerBoxCodeUrl('bindByDelivery'),
      method: 'POST',
      data,
      loading: true
    }),

  getById: async id =>
    http({
      url: getOutgerBoxCodeUrl('get'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  update: async data =>
    http({
      url: getOutgerBoxCodeUrl('update'),
      method: 'POST',
      data,
      loading: true
    }),

  generateOuterBoxCode: async data =>
    http({
      url: getOutgerBoxCodeUrl('generateOuterBoxCode'),
      method: 'POST',
      data,
      loading: true
    })

}

/**
 * 内外箱关联
 */
const boxRelationPrefix = '/api-base/base/innerouterrelation'
const getBoxRelationUrl = path => boxRelationPrefix + '/' + path

export const innerouterrelation = {
  list: async data =>
    http({
      url: getBoxRelationUrl('listPage'),
      method: 'POST',
      data,
      loading: true
    }),

  add: async data =>
    http({
      url: getBoxRelationUrl('add'),
      method: 'POST',
      data,
      loading: true
    }),

  update: async data =>
    http({
      url: getBoxRelationUrl('modify'),
      method: 'POST',
      data,
      loading: true
    }),

  delete: async data =>
    http({
      url: getBoxRelationUrl('delete'),
      method: 'POST',
      data,
      loading: true
    }),

  batchSaveOrUpdate: async data =>
    http({
      url: getBoxRelationUrl('batchSaveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    })

}
