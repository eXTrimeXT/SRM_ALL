<template>
  <div>
    <el-cascader
      ref="cascader"
      :value="value"
      style="width: 100%"
      :props="props"
      :placeholder="placeholder"
      :options="dataList"
      :disabled="disabled"
      @change="handleChange"
    />
  </div>
</template>

<script>

import {
  getDictItem,
  getRegion
} from '@/api/common'

import { adaptDictData } from '@/utils'

export default {
  name: 'Address',
  props: {
    value: {
      type: Array,
      default: () => {
        return ['']
      }
    },
    disabled: {
      type: Boolean
    }
  },
  data () {
    return {
      placeholder: '',
      provinceList: [], // 省数据
      countyList: [], // 区县数据
      dataList: [],
      provinceCode: '',
      cityCode: '',
      props: {
        lazy: true, // 此处必须为true
        lazyLoad: (node, resolve) => {
          if (node.data) {
            if (node.data.value !== 'CN' && node.level == 1) {
              resolve()
            } else {
              this.getChildData(node.data, resolve, node.level)
            }
          }
        }
      }
    }
  },
  created () {
    this.getCountry()
  },
  methods: {
    init () {
      console.log(this.value[0])
      let countryName = ''
      let provinceName = ''
      let cityName = ''
      if (this.value[0]) {
        getDictItem('country').then(res => {
          let dataCountry = adaptDictData(res.data, 'dict')
          dataCountry.forEach(e => {
            if (e.value == this.value[0]) {
              countryName = e?.label
            }
          })
          if (this.value[1] && this.value[0] == 'CN') {
            getRegion({ queryType: 'province' }).then(res => {
              if (res.data) {
                this.provinceList = this.adaptProvinceCity(res.data, 'province')
                provinceName = this.provinceList.find(i => i.value == this.value[1])?.label
                let parame = { queryType: 'city', parentId: this.value[1] }
                getRegion(parame).then(res => {
                  if (res.data) {
                    this.cityList = this.adaptProvinceCity(res.data, 'city')
                    cityName = this.cityList.find(i => i.value == this.value[2])?.label || ''
                    this.placeholder = `${countryName}/${provinceName}/${cityName}`
                  }
                })
              }
            })
          } else {
            this.$nextTick(() => {
              this.placeholder = countryName
              this.$forceUpdate()
            })
          }
        })
      }
    },
    getChildData (data, resolve, level) {
      if (level == 1) {
        // 加载省份
        this.provinceCode = data.value
        this.getProvince(this.provinceCode, resolve)
      } else if (level == 2) {
        // 市
        this.cityCode = data.value
        this.getCity(this.cityCode, resolve)
      }
    },
    handleChange (value) {
      this.$emit('change-value', value)
    },
    getProvince (code, resolve) {
      getRegion({ queryType: 'province' }).then(res => {
        if (res.data) {
          this.provinceList = this.adaptProvinceCity(res.data, 'province')
          resolve(this.provinceList)
        }
      })
    },
    getCity (code, resolve) {
      let parame = { queryType: 'city', parentId: code }
      getRegion(parame).then(res => {
        if (res.data) {
          this.cityList = this.adaptProvinceCity(res.data, 'city')
          this.cityList.forEach(e => {
            e.leaf = true
          })
          resolve(this.cityList)
        }
      })
    },
    getCountry () {
      // 国家
      getDictItem('country').then(res => {
        this.dataList = adaptDictData(res.data, 'dict')
        this.dataList.forEach(e => {
          if (e.value !== 'CN') {
            e.leaf = true
          }
        })
      })
    },
    // 适配省 市
    adaptProvinceCity (data, type) {
      let arr = []
      if (data && data.length > 0) {
        if (type === 'province') {
          // 省
          data.forEach(element => {
            arr.push({
              id: element.provinceId,
              value: element.provinceId.toString(),
              label: element.province
            })
          })
        } else if (type === 'city') {
          // 市
          data.forEach(element => {
            arr.push({
              id: element.cityId,
              value: element.cityId.toString(),
              label: element.city
            })
          })
        }
      }
      return arr
    }
  }
}
</script>

<style lang="scss" scoped>
:deep(.el-input__inner::-webkit-input-placeholder) {
  color: #303133 !important;
}
:deep(.el-input__inner) {
  color: #303133 !important;
}
</style>
