<template>
  <div class="wrapper">
    <div class="header">
      <!-- <span class="title">投标供应商</span> -->
      <span class="title">{{ $t("bidMod.bidingvendorName") }}</span>
      <!-- <span class="desc ml-20">
        本项目共发标<span class="red">3</span>家，实际投标<span class="red">5</span>家，具体信息如下
      </span> -->
    </div>
    <BaseTable
      stripe
      index
      :data="tableData"
      :columns="tableColumns"
      :empty-text="$t('components.noData')"
      border
    >
      <!-- 投标状态 -->
      <template #orderStatus="scope">
        <span>{{ $getDictLabel('SOU_ORDER_STATUS',scope.row.orderStatus) }}</span>
      </template>
      <!-- 供应商属性 -->
      <template #extVendorAttr="scope">
        <template v-if="scope.row.extVendorAttr">
          <span v-for="(item,index) in scope.row.extVendorAttr.split(';')" :key="index">{{ $getDictLabel('SOU_RECOMM_VENDOR_NATRUE',item) }};</span>
        </template>
      </template>
    </BaseTable>
  </div>
</template>
<script>
import BaseTable from 'lib@/components/BaseTable'
import QuickSearch from 'lib@/components/QuickSearch'
export default {
  components: {
    BaseTable,
    QuickSearch
  },
  props: {
    readonly: {
      type: Boolean,
      default: false
    },
    value: {
      type: Array,
      default: () => []
    },
    applicantNo: {
      type: String,
      default: ''
    }
  },
  data () {
    return {
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
            prop: 'extVendorAttr',
            // label: '供应商属性'
            label: () => this.$t('cusEntry.supplement20250121.supplierAttributes')
          },
          slot: 'extVendorAttr'
        },
        {
          attrs: {
            prop: 'orderStatus',
            // label: '投标状态'
            label: () => this.$t('bidMod.orderStatus')
          },
          slot: 'orderStatus'
        },
        {
          attrs: {
            prop: 'extNotjoinReason',
            // label: '不参与原因'
            label: () => this.$t('cusEntry.bidMod.withdrawReason')
          }
        },
        {
          attrs: {
            prop: 'rejectReason',
            // label: '废标说明'
            label: () => this.$t('cusEntry.supplement20250205.bidCancellationDescription')
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
  }
}
</script>
<style lang="scss" scoped>
.red {
  color: red;
}
.ml-20 {
  margin-left: 20px;
}
.header {
  margin: 10px 0;
  .title {
    font-weight: bold;
  }
}
</style>
