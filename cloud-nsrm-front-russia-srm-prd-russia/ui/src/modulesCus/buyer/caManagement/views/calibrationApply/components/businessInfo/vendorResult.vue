<template>
  <div class="wrapper">
    <div class="header">
      <span class="title">供应商选定结果</span>
      <el-button type="primary" @click="viewQuote">
        查看历史合作信息
      </el-button>
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
        <DictSelect
          v-if="!readonly || (flag === 'approval' && !isWrite && inputFlag)"
          v-model="scope.row.isWin"
          code="YES_OR_NO"
          @change="changIsWin(scope.row.isWin,scope.$index)"
        />
        <span v-else>{{ $getDictLabel('YES_OR_NO',scope.row.isWin) }}</span>
      </template>
      <!-- 中标范围 -->
      <template #winRange="scope">
        <el-select
          v-if="!readonly || (flag === 'approval' && !isWrite && inputFlag)"
          v-model="scope.row.winRange"
          :disabled="scope.row.isWin === 'N'"
          clearable
          filterable
          multiple
          @change="(val) => winRangeChange(val,scope.row)"
        >
          <el-option v-for="(item,index) in winOptions" :key="index" :label="item.label" :value="item.value" />
        </el-select>
        <span v-else>{{ scope.row.winRange.join(';') }}</span>
      </template>
      <!-- 中/落标原因 -->
      <template #winReason="scope">
        <DictSelect
          v-if="!readonly || (flag === 'approval' && !isWrite && inputFlag)"
          v-model="scope.row.winReason"
          allow-create
          code="CA_WIN_BID_REASON"
        />
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
    },
    isWrite: {
      type: Boolean,
      default: true
    },
    inputFlag: {
      type: Boolean,
      default: true
    },
    flag: {
      type: String,
      default: ''
    },
    winOptions: {
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
            label: '是否中标',
            renderHeader: this._addStarToColumn
          },
          slot: 'isWin'
        },
        {
          attrs: {
            prop: 'winRange',
            label: '中标范围',
            renderHeader: this._addStarToColumn
          },
          slot: 'winRange'
        },
        {
          attrs: {
            prop: 'winReason',
            label: '中/落标原因',
            renderHeader: this._addStarToColumn
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
  },
  methods: {
    changIsWin (val, i) {
      val == 'N' && (this.tableData[i].winRange = [])
    },
    viewQuote () {
      this.$emit('viewQuote')
    },
    winRangeChange (val, row) {
      let orderItemId = null
      // console.log('val:::', val)
      if (val.length) {
        let arr = []
        for (let item of val) {
          let obj = this.winOptions.find(innerItem => innerItem.value === item)
          if (obj) {
            arr.push(obj.id)
          }
        }
        orderItemId = arr.length ? arr.join(';') : null
      }
      row.orderItemId = orderItemId
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
    margin-right: 20px;
  }
}
</style>
