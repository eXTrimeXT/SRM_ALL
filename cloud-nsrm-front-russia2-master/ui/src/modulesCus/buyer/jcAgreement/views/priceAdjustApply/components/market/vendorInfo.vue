<template>
  <div class="wrapper">
    <BaseTable
      stripe
      index
      :data="tableData"
      :columns="tableColumns"
      :empty-text="$t('components.noData')"
      border
      max-height="250px"
    />
  </div>
</template>
<script>
import BaseTable from 'lib@/components/BaseTable'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'

export default {
  name: 'AgreeInfo',
  components: {
    BaseTable,
    MImport
  },
  props: {
    form: {
      type: Object,
      default: () => {}
    },
    value: {
      type: Array,
      default: () => []
    },
    readonly: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      tableData: [],
      tableColumns: []
    }
  },
  watch: {
    value: {
      handler (nVal) {
        if (nVal) {
          this.tableData = nVal
        }
      },
      immediate: true,
      deep: true
    }
  },
  created () {
    this.tableColumns = [
      {
        attrs: {
          prop: 'vendorCode',
          label: this.$t('common.vendorCode'), // '供应商编码',
          minWidth: 120
        }
      },
      {
        attrs: {
          prop: 'vendorName',
          label: this.$t('common.vendorName'), // '供应商名称',
          minWidth: 120
        }
      },
      {
        attrs: {
          prop: 'linkmanName',
          label: this.$t('vendorMod.contactPerson'), // '联系人',
          minWidth: 120
        }
      },
      {
        attrs: {
          prop: 'phone',
          label: this.$t('common.phone'), // '电话',
          minWidth: 120
        }
      },
      {
        attrs: {
          prop: 'email',
          label: this.$t('common.email'), // '邮箱',
          minWidth: 120
        }
      },
      {
        attrs: {
          prop: 'brand',
          label: this.$t('dataConfMod.band'), // '品牌',
          minWidth: 120
        }
      },
      {
        attrs: {
          prop: 'remark',
          label: this.$t('common.remark'), // '备注',
          minWidth: 120
        }
      }
    ]
  },
  methods: {

  }
}
</script>
<style lang="scss" scoped>
.mb-10 {
  margin-bottom: 10px;
}
</style>
