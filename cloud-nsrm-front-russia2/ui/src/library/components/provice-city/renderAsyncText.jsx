// 主要用于渲染非编辑情况 省市组件
import { store, mutations } from './store'
export default {
  name: 'render-async-text',
  data () {
    const cache = new Map()
    return {
      cache,
      text: null,
      provice: []
    }
  },
  props: ['cellValue'],
  watch: {
    cellValue: {
      handler (value) {
        if (value) {
          this.queryThenFormat()
        } else {
          this.text = ''// 解决翻页时，无数据的单元格被上一页数据填充的问题--pl 2021-2-19
        }
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    // 查询并且格式化省市
    async queryThenFormat () {
      const value = this.cellValue
      let paseredValue
      if (Array.isArray(value)) {
        paseredValue = value
      } else if (/^\[\"[0-9]*\",{1}\"[0-9]*\"\]$/.test(value)) {
        try {
          paseredValue = JSON.parse(value)
        } catch (e) {
          // console.log(e);
        }
      }
      // console.log("[paseredValue]", paseredValue);
      if (paseredValue) {
        const [parentId, cityId] = paseredValue
        if (!store.province.length) {
          await mutations.fetchProvince()
        }
        await mutations.fetchCity(parentId)
        const city = store.city[parentId].find(i => i.cityId == cityId)
        if (city) {
          const province =
            store.province.find(i => i.provinceId == parentId) || {}
          // console.log(city, province);
          this.text = `${`${province.province} / ` || ''}${city.city || ''}`
          return
        }
      }
      this.text = value
    }
  },
  render (h) {
    return <span>{this.text}</span>
  }
}
