<template>
  <div class="wrapper">
    <div class="header">
      <!-- <span class="title">供应商总体情况</span> -->
      <span class="title">{{ $t("cusEntry.supplement20250205.overallSupplierInformation") }}</span>
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
            // label: '序号',
            label: () => this.$t('components.common.sort'),
            type: 'index',
            width: 60
          }
        },
        {
          attrs: {
            prop: 'vendorName',
            // label: '供应商名称',
            label: () => this.$t('common.companyName'),
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'bidTotalPrice',
            // label: '投标含税总价（万元）',
            label: () => this.$t('cusEntry.supplement20250205.bidTotalPriceIncludingTax'),
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'techScore',
            // label: '技术得分',
            label: () => this.$t('bidMod.technicalMerit'),
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'priceScore',
            // label: '价格得分',
            label: () => this.$t('bid_mod.priceScore'),
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'compositeScore',
            // label: '综合得分',
            label: () => this.$t('bidMod.compositeScore'),
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'comprehensiveEvaluation',
            // label: '综合评定'
            label: () => this.$t('cusEntry.supplement20250205.comprehensiveEvaluation')
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
