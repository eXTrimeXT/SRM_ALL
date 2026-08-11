<template>
  <el-cascader
    filterable
    clearable
    :disabled="disabled"
    :value="value"
    :props="props"
    @change="change"
  />
</template>
<script>
import { store, mutations } from '@/library/components/provice-city/store'

export default {
  name: 'ProviceCity',
  model: {
    prop: 'value',
    event: 'change'
  },
  props: {
    // eslint-disable-next-line vue/require-prop-types
    value: {},
    disabled: {
      default: false,
      type: Boolean
    }
  },
  data () {
    return {
      props: {
        lazy: true,
        lazyLoad: (node, resolve) => {
          setTimeout(async () => {
            const { level, value } = node
            if (level === 0) {
              if (!store.province.length) {
                await mutations.fetchProvince()
              }
              const nodes = store.province.map(i => ({
                id: i.provinceId,
                value: i.provinceId.toString(),
                label: i.province,
                leaf: false,
                children: []
              }))
              resolve(nodes)
            }
            if (level > 0) {
              if (!store.city[value]) {
                await mutations.fetchCity(value)
              }
              const nodes = store.city[value].map(i => ({
                id: i.cityId,
                leaf: true,
                value: i.cityId.toString(),
                label: i.city
              }))
              resolve(nodes)
            }
          }, 100)
        }
      }
    }
  },
  methods: {
    change (value) {
      this.$emit('change', value)
    }
  }
}
</script>
