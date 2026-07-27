/**
 * @description 供应商 - 物料图纸 /api-sup/vendor/drawing
 * @description 路径：$api.sup.vendor.drawing
 * @author 伟龙
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
    })
}

export default {
  drawingshead
}
