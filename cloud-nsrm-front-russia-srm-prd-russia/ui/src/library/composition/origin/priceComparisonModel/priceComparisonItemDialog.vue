<template>
  <SrmDialog
    :title="title"
    :visible.sync="dialogVisible"
    append-to-body
    size="large"
    :close-on-click-modal="false"
  >
    <BaseTable
      ref="table"
      stripe
      :data="tableData"
      :columns="tableColumns"
      :empty-text="$t('components.noData')"
      border
      height="100%"
    >
      <template #subItemName="{row}">
        <el-button type="text" @click="openSubItemDialog(row)">
          {{ row.subItemName }}
        </el-button>
      </template>
    </BaseTable>
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t('common.cancel') }}
      </el-button>
    </div>
  </SrmDialog>
</template>
<script>
import BaseTable from 'lib@/components/BaseTable'

export default {
  name: 'PriceComparisonItemDialog',
  components: {
    BaseTable
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    data: {
      type: Object,
      default: () => {}
    }
  },
  data () {
    return {
      title: '',
      tableData: [],
      tableColumns: []
    }
  },
  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    }
  },
  watch: {
    data: {
      handler (nVal) {
        if (nVal) {
          console.log('row', nVal)
          let { quoteFieldName = '', souTempSelectDetailDynamicColList = [], souTempSelectDetailDataVOList = [] } = nVal
          this.title = quoteFieldName
          this.arrangeTableColumns(souTempSelectDetailDynamicColList)
          this.arrangeTableData(souTempSelectDetailDataVOList)
        }
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    arrangeTableColumns (dynamicColumns) {
      let columns = [
        {
          attrs: {
            prop: 'vendorName',
            label: this.$t('bidMod.vendorName'),
            minWidth: '130',
            showOverflowTooltip: true
          }
        }
      ]
      for (let item of dynamicColumns) {
        columns.push({
          attrs: {
            prop: item.prop,
            label: item.label,
            minWidth: '120',
            showOverflowTooltip: true
          }
        })
      }
      this.tableColumns = columns
    },
    arrangeTableData (dynamicData) {
      this.tableData = dynamicData.map(item => {
        return {
          ...item,
          ...item.dynamicColDataMap
        }
      })
    }
  }
}
</script>
