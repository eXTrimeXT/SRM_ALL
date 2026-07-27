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
    }
  },

  data () {
    return {
      searchFormConfig: [
        // 物料编码
        { prop: 'itemCode', label: this.$t('bidMod.targetNum') },
        // 物料名称
        { prop: 'itemDesc', label: this.$t('bidMod.targetDesc') },
        // 排名
        { prop: 'rank', label: this.$t('bidMod.rank') },
        // 评选结果
        {
          prop: 'selectionStatus',
          label: this.$t('bidMod.selectionStatus'),
          type: 'dict',
          code: 'SOU_COMP_SELECTION_STATUS'
        },
        // 供应商名称
        { prop: 'vendorName', label: this.$t('bidMod.vendorName') },
        // 业务实体
        {
          prop: 'orgOuId',
          label: this.$t('bidMod.businessEntity'),
          type: 'OUorganizationSelector'
        }
      ]
    }
  },

  methods: {
    getQueryData (payload) {
      this.$emit('getQueryData', payload)
    }
  }
}
</script>
