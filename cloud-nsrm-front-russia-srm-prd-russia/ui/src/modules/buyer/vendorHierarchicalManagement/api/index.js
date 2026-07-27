/* 供应商分级管理 */

import http from '@/utils/axios/http'
const getUrl = path => `${path}`

/* 分级项目管理 */
export const hierarchicalProject = {
  // 查询订单详情
  get: async vendorLevelId =>
    http({
      url: getUrl('/api-pef/perf/vendorlevelhead/get'),
      method: 'GET',
      params: { vendorLevelId },
      loading: true
    }),
  save: async (url, data) =>
    http({
      url: getUrl(url),
      method: 'POST',
      data,
      loading: true
    }),
  getAllOrgCategoryByOrgId: async orgId =>
    http({
      url: getUrl('/api-pef/perf/vendorlevelhead/getAllOrgCategoryByOrgId'),
      method: 'GET',
      params: { orgId },
      loading: true
    }),
  getUserByCategoryDto: async data =>
    http({
      url: getUrl('/api-pef/perf/levelman/getUserByCategoryDto'),
      method: 'POST',
      data,
      loading: true
    })
}

/**
   * 供应商分级
* */
export const hierarchicalRatingApi = {
// 分级评级明细---》查询
  addOrUpdateVendorLevel: (data) => {
    return http({
      url: '/api-pef/perf/levellinescore/listPageByParam',
      method: 'POST',
      data
    })
  },

// 分级评级明细---》保存按钮
  saveOrUpdateScoreList: (data) => {
    return http({
      url: '/api-pef/perf/levellinescore/saveOrUpdateScoreList',
      method: 'POST',
      data
    })
  },

// 分级评级明细---》删除/批量删除
  bathDelete: (data) => {
    return http({
      url: '/api-pef/perf/levellinescore/bathDelete',
      method: 'POST',
      data
    })
  },

  // 分级评级明细---》绩效明细弹窗
  listPageOverallScoreByParam: (data) => {
    return http({
      url: '/api-pef/perf/leveloverallscore/listPageOverallScoreByParam',
      method: 'POST',
      data
    })
  },

// 分级评级明细---》提交
  submitScoreList: (data) => {
    return http({
      url: '/api-pef/perf/levellinescore/submitScoreList',
      method: 'POST',
      data
    })
  },

// 分级评级明细---》失效
  inValid: (data) => {
    return http({
      url: '/api-pef/perf/levellinescore/inValid',
      method: 'POST',
      data
    })
  }
}
