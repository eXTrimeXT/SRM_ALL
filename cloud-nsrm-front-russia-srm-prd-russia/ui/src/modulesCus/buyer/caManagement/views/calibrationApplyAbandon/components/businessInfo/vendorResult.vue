<template>
  <div class="wrapper">
    <div class="header">
      <span class="title">供应商选定结果</span>
    </div>
    <BaseTable
      stripe
      index
      :data="tableData"
      :columns="tableColumns"
      :empty-text="$t('components.noData')"
      border
    >
      <!-- 是否中标 -->
      <template #isWin="scope">
        <DictSelect v-if="!readonly" v-model="scope.row.isWin" code="YES_OR_NO" />
        <span v-else>{{ $getDictLabel('YES_OR_NO',scope.row.isWin) }}</span>
      </template>
      <!-- 中标范围 -->
      <template #winRange="scope">
        <el-input v-if="!readonly" v-model="scope.row.winRange" />
        <span v-else>{{ scope.row.winRange }}</span>
      </template>
      <!-- 中/落标原因 -->
      <template #winReason="scope">
        <DictSelect v-if="!readonly" v-model="scope.row.winReason" allow-create code="CA_WIN_BID_REASON" />
        <span v-else>{{ $getDictLabel('CA_WIN_BID_REASON',scope.row.winReason) }}</span>
      </template>
    </BaseTable>
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
            prop: 'vendorCode',
            label: '供应商编码',
            showOverflowTooltip: true
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
            prop: 'isWin',
            label: '是否中标'
          },
          slot: 'isWin'
        },
        {
          attrs: {
            prop: 'winRange',
            label: '中标范围'
          },
          slot: 'winRange'
        },
        {
          attrs: {
            prop: 'winReason',
            label: '中/落标原因'
          },
          slot: 'winReason'
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
.header {
  margin:10px 0;
  .title {
    font-weight:bold;
  }
}
</style>
