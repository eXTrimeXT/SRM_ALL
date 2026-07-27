import vue from 'vue'
import { newOrganaztionTreehttp, organaztionTreehttp, getInfoByParam, orgCategorySupplierTree } from '@/api/common'
import http from '@/utils/axios/http'

const fullPathNameMap = new Map()

let fetchCount = 0
let fetchKey = []
async function delayFn (ms = 200) {
  return new Promise(resolve => setTimeout(resolve, ms))
}

export const store = vue.observable({
  tree: [], // 没有fullPathId
  treeWithFullPathId: [], // 带有fullPathId
  vendorPermission: {},
  buyerPermission: {},
  fullPathNameMap: fullPathNameMap
})

export const mutations = {
  async fetchTree () {
    if (!store.tree.length) {
      const { data } = await organaztionTreehttp({})
      store.tree = data
    }
    return store.tree
  },
  async fetchTreeWithFullPathId () {
    if (!store.treeWithFullPathId.length) {
      const { data } = await newOrganaztionTreehttp({})
      store.treeWithFullPathId = data
    }
    return store.treeWithFullPathId
  },
  async fetchVendorPermission (companyId, userId) {
    const key = `${companyId}_${userId}`
    if (
      !store.vendorPermission[key] ||
      (!Object.keys(store.vendorPermission[key]).length && companyId)
    ) {
      const { data } = await getInfoByParam({ companyId })
      store.vendorPermission[key] = data
    }
    return store.vendorPermission[key]
  },
  async fetchBuyerPermission (organizationTypeCode, userId) {
    const key = `${organizationTypeCode}_${userId}`
    if (
      !store.buyerPermission[key] ||
      (!store.buyerPermission[key].length && organizationTypeCode)
    ) {
      const { data } = await http({
        url: '/api-base/organization/relation/selectTreeByType',
        method: 'GET',
        params: { organizationTypeCode }
      })
      store.buyerPermission[key] = data
    }
    return store.buyerPermission[key]
  },
  async fetchSupPermission (organizationTypeCode, parentOrganizationIds) {
    const key = `${organizationTypeCode}_${parentOrganizationIds}`
    // 有缓存返回
    let hasValue = store.vendorPermission[key] && store.vendorPermission[key].length > 0
    if (hasValue) {
      return store.vendorPermission[key]
    }
    let toGetValue = !store.vendorPermission[key] || (!store.vendorPermission[key].length && organizationTypeCode)
    if (toGetValue) {
      let isSameFetch = fetchKey.includes(key)
      // 多次进来判断是否已经请求过
      if (fetchCount++ && isSameFetch) {
        while (!store.vendorPermission[key]) await delayFn()
      } else {
        fetchKey.push(key)
        let queryObj = {
          organizationTypeCode: organizationTypeCode,
          parentOrganizationIds: parentOrganizationIds
        }
        const { data } = await orgCategorySupplierTree(queryObj)
        store.vendorPermission[key] = data
        return store.vendorPermission[key]
      }
    }
  }
}
