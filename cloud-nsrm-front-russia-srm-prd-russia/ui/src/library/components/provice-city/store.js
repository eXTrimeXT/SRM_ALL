import vue from 'vue'
import { getRegion } from '@/api/common'

export const store = vue.observable({
  province: [],
  city: {}
})

export const mutations = {
  async fetchProvince () {
    if (!store.province.length) {
      const { data } = await getRegion({ queryType: 'province' })
      store.province = data
    }
    return store.province
  },
  async fetchCity (parentId) {
    if (!store.city[parentId]) {
      const { data } = await getRegion({ queryType: 'city', parentId })
      store.city[parentId] = data
    }
    return store.city[parentId]
  }
}
