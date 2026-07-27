/* 采购需求 */

import http from '@/utils/axios/http'
const getUrl = path => `${path}`

/* 采购目录 */
export const purchaseCatalogApi = {
  // 获取下一级节点
  getCatChildrenData: async params =>
    http({
      url: getUrl('/api-base/purchase/purchaseCategory/listChildren'),
      method: 'POST',
      params,
      loading: true
    }),
  // 查询物料
  purCatlistPage: async data =>
    http({
      url: getUrl('/api-sup-ce/pr/purchaseCatalog/listPageForPurchaseCatalog'),
      method: 'POST',
      loading: true,
      data
    }),
  // 加入购物车
  addToShoppingCart: async params =>
    http({
      url: getUrl('/api-sup-ce/pr/purchaseCatalog/addToShoppingCart'),
      method: 'GET',
      params
    })
}

// 购物车
export const shoppingCartApi = {
  // 购物车 创建申请单前校验“采购类型”、“需求时间”、“数量”是否已维护
  ceeaValidRequiredInfo: async data =>
    http({
      url: getUrl('/api-sup-ce/pr/shopCart/ceeaValidRequiredInfo'),
      method: 'POST',
      data
    }),
  // 购物车 选择汇总
  ceeaSetSummaryAndNoticeUser: async data =>
    http({
      url: getUrl('/api-sup-ce/pr/shopCart/ceeaSetSummaryAndNoticeUser'),
      method: 'POST',
      data
    }),
  // 购物车 提交/退回需求 status/取“SUBMITTED”表示提交需求，取“DRAFT”表示退回需求
  ceeaChangeShopCartStatus: async data =>
    http({
      url: getUrl('/api-sup-ce/pr/shopCart/ceeaChangeShopCartStatus'),
      method: 'POST',
      data
    }),
  // 购物车 勾选保存
  ceeaUpdateShopCarts: async data =>
    http({
      url: getUrl('/api-sup-ce/pr/shopCart/ceeaUpdateShopCarts'),
      method: 'POST',
      data
    }),
  // 购物车 创建申请单
  ceeaCreateRequirements: async data =>
    http({
      url: getUrl('/api-sup-ce/pr/shopCart/ceeaCreateRequirements'),
      method: 'POST',
      data
    }),
  // 购物车 查询申请单详情 requirementHeadId
  requirementGetData: async params =>
    http({
      url: getUrl('/api-sup-ce/pr/requirementHead/getByHeadId'),
      method: 'GET',
      params
    }),
  // 购物车 批量维护
  ceeaBatchUpdateShopCarts: async data => {
    http({
      url: getUrl('/api-sup-ce/pr/shopCart/ceeaBatchUpdateShopCarts'),
      method: 'POST',
      data
    })
  },
  // 购物车 保存前校验
  ceeaCheckUpdateShopCarts: async data => {
    http({
      url: getUrl('/api-sup-ce/pr/shopCart/ceeaCheckUpdateShopCarts'),
      method: 'POST',
      data
    })
  },
  // 上架接口 价格目录列表勾选数据列表数组(价格管理 价格目录)
  putOnShelves: async data => {
    // console.log('[putOnShelves]', data);
    return http({
      url: getUrl('/api-inq/price/priceLibrary/putOnShelves'),
      method: 'POST',
      data
    })
  },
  // 下架接口 价格目录列表勾选数据列表数组(价格管理 价格目录)
  pullOffShelves: async data =>
    http({
      url: getUrl('/api-inq/price/priceLibrary/pullOffShelves'),
      method: 'POST',
      data
    }),

  // 物料详情接口
  materialItemGet: async params =>
    http({
      url: getUrl('/api-base/material/materialItem/ceeaGet'),
      method: 'GET',
      params,
      loading: true
    }),
  // 物料信息维护接口
  materialItemModify: async data =>
    http({
      url: getUrl('/api-base/material/materialItem/ceeaSaveOrUpdate'),
      method: 'POST',
      data
    }),
  // 物料保存（保存页面上编辑的供应商）
  ceeaUpdateSupplier: async data =>
    http({
      url: getUrl('/api-base/material/materialItem/ceeaUpdateSupplier'),
      method: 'POST',
      data
    }),
  // 通知供应商
  ceeaNotifyVendor: async data =>
    http({
      url: getUrl('/api-base/material/materialItem/ceeaNotifyVendor'),
      method: 'POST',
      data
    }),
  // 购物车 删除未提交状态(DRAFT)的数据
  ceeaDeleteByIds: async data =>
    http({
      url: getUrl('/api-sup-ce/pr/shopCart/ceeaDeleteByIds'),
      method: 'POST',
      data
    })
}
