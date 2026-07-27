import vue from 'vue'
import { purchaseCategoryTree } from '@/api/common'
import { getCatLavel } from '@/api/user'

export const store = vue.observable({
  category: [],
  catLavel: 3
})

export const mutations = {
  async fetchCategory () {
    if (!store.category.length) {
      const { data } = await purchaseCategoryTree({ onlyEffective: 'true' }) // onlyEffective 为true 查询有效期内的品类
      store.category = data || []
    }
    return store.category
  },
  // 查询设置的层级数
  async fetchCatLavel () {
    const { data } = await getCatLavel()
    if (data.length > 0) {
      let catLavel = Number(data[0].serviceLevel)
      store.catLavel = catLavel
    }

    return store.catLavel
  }
}
