<template>
  <FormWrapper
    ref="searchForm"
    :form-array="searchFormConfig"
    form-label-width="120px"
    @getFormData="getQueryData"
  />
</template>

<script>
/**
 * 评选查询表单
 */
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { maxNumberOption } from 'lib@/composition/commonComposition'

export default {
  name: 'SearchForm',
  components: {
    FormWrapper
  },
  props: {
    preFormObj: {
      type: Object
    }
  },
  data () {
    return {
      searchFormConfig: [
        // 报价轮次
        {
          prop: 'round',
          label: this.$t('bidMod.preformround'),
          type: 'select',
          options: () => this.roundOption
        },
        // 物料编码
        { prop: 'targetNum', label: this.$t('bidMod.targetNum') },
        // 物料名称
        { prop: 'targetDesc', label: this.$t('bidMod.targetDesc') },
        // 业务实体
        { prop: 'orgOuName', label: '业务实体' },
        // 组合
        { prop: 'itemGroup', label: this.$t('bidMod.itemGroup') },
        // 供应商名称
        { prop: 'vendorName', label: this.$t('bidMod.vendorName') },
        // 评选结果
        {
          prop: 'selectionStatus',
          label: this.$t('bidMod.selectionStatus'),
          type: 'dict',
          code: 'BARGAIN_SELECT_STATES'
        },
        // 排名
        { prop: 'rank', label: this.$t('bidMod.rank') }
      ]
    }
  },
  computed: {
    roundOption () {
      return maxNumberOption((this.preFormObj || {}).round || 1)
    }
  },
  watch: {
    preFormObj: {
      handler (newVal, oldVal) {
        if (this.$refs.searchForm && newVal.round && newVal.round !== oldVal.round) {
          // 动态设置查询参数
          this.$refs.searchForm.setValue('round', newVal.round)
        }
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    getQueryData (payload) {
      this.$emit('getQueryData', payload)
    }
  }
}
</script>
