<template>
  <FormWrapper
    ref="searchForm"
    :form-array="searchFormConfig"
    form-label-width="90px"
    @getFormData="getQueryData"
  />
</template>

<script>
/**
 * 评选查询表单
 */
import { maxNumberOption } from 'lib@/composition/commonComposition'
import FormWrapper from 'lib@/components/Table/FormWrapper'

export default {
  name: 'SearchForm',

  components: {
    FormWrapper
  },

  props: {
    preFormObj: {
      type: Object,
      default: () => { /* nothing */ }
    },
    // 物料需求数据
    requireInfoData: {
      type: Array,
      default: () => []
    },
    // 邀请供应商数据
    vendorInfoData: {
      type: Array,
      default: () => []
    }
  },

  computed: {
    searchFormConfig () {
      return [
        // 物料名称
        // {
        //   prop: 'souItemId',
        //   label: this.$t('bidMod.targetDesc'),
        //   type: 'select',
        //   options: () => this.requireInfoData.map(item => {
        //     return {
        //       value: item.souItemId,
        //       label: item.itemDesc
        //     }
        //   })
        // },
        {
          prop: 'itemDesc',
          label: this.$t('bidMod.targetDesc')
        },
        // 供应商
        {
          prop: 'vendorId',
          label: this.$t('bidMod.provider'),
          type: 'select',
          options: () => this.vendorInfoData.map(item => {
            return {
              value: item.vendorId,
              label: item.vendorName
            }
          })
        },
        // 轮次
        {
          prop: 'round',
          label: this.$t('bidMod.bidingRound'),
          type: 'select',
          options: () => maxNumberOption((this.preFormObj || {}).round || 1)
        }
      ]
    }
  },

  methods: {
    getQueryData (payload) {
      this.$emit('search', payload)
    }
  }
}
</script>
