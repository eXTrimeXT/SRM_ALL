<template>
  <div class="wrapper">
    <div class="header">
      <span class="title">供应商总体情况</span>
    </div>
    <BaseTable
      stripe
      index
      :data="tableData"
      :columns="tableColumns"
      :empty-text="$t('components.noData')"
      border
    />
  </div>
</template>
<script>
import BaseTable from 'lib@/components/BaseTable'

export default {
  components: {
    BaseTable
  },
  props: {
    readonly: {
      type: Boolean,
      default: false
    },
    value: {
      type: Array,
      default: () => []
    }
  },
  data () {
    return {
      vendorRiskVisible: false,
      tableColumns: [
        {
          attrs: {
            label: '序号',
            type: 'index',
            width: 60
          }
        },
        {
          attrs: {
            prop: 'vendorName',
            label: '供应商名称',
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'bidTotalPrice',
            label: '投标含税总价（万元）',
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'techScore',
            label: '技术得分',
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'priceScore',
            label: '价格得分',
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'compositeScore',
            label: '综合得分',
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'comprehensiveEvaluation',
            label: '综合评定'
          }
        }
      ]
    }
  },
  computed: {
    tableData: {
      get () {
        return this.value
      },
      set (val) {
        this.$emit('update:value', val)
      }
    }
  },
  methods: {
    viewRisk () {
      this.vendorRiskVisible = true
    }
  }
}
</script>
<style lang="scss" scoped>
.red {
  color: red;
}
.header {
  margin:10px 0;
  .title {
    font-weight:bold;
  }
}
.ml-20 {
  margin-left: 20px;
}
</style>
