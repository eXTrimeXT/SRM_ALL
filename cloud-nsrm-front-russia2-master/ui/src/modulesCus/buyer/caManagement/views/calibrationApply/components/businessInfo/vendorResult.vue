<template>
  <div class="wrapper">
    <!-- <div class="header"> -->
      <!-- 查看历史合作信息 -->
      <!-- <el-button type="primary" @click="viewQuote">
        {{ $t("cusEntry.supplement20250205.viewHistoricalCooperationInfo") }}
      </el-button> -->
    <!-- </div> -->
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
          v-if="isWrite"
          v-model="scope.row.isWin"
          code="YES_OR_NO"
          @change="changIsWin(scope.row.isWin,scope.$index)"
        />
        <span v-else>{{ $getDictLabel('YES_OR_NO',scope.row.isWin) }}</span>
      </template>
      <!-- 中标范围 -->
      <template #winRange="scope">
        <div style="display: flex;align-items: center;gap: 5px;">
          <el-select
            v-if="isWrite"
            v-model="scope.row.winRange"
            :disabled="scope.row.isWin === 'N'"
            clearable
            filterable
            multiple
            @change="(val) => winRangeChange(val,scope.row)"
            size="medium"
          >
            <el-option v-for="(item,index) in winOptions" :key="index" :label="item.label" :value="item.value" />
          </el-select>

          <span v-else>{{ scope.row.winRange.join(';') }}</span>
          <el-tag v-if="isWrite" @click="selectAll(scope.row)" style="cursor: pointer;">{{ $t("common.selectAll") }}</el-tag>
       </div>
      </template>
      <!-- 中/落标原因 -->
      <template #winReason="scope">
        <DictSelect
          :key="scope.row.id + '-' + scope.row.isWin"
          v-if="isWrite"
          v-model="scope.row.winReason"
          allow-create
          :transform-options="(options) => transformOptions(options, scope.row.isWin)"
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
            // label: '序号',
            label: () => this.$t('components.common.sort'),
            type: 'index',
            width: 60
          }
        },
        {
          attrs: {
            prop: 'vendorCode',
            // label: '供应商编码',
            label: () => this.$t('common.vendorCode'),
            showOverflowTooltip: true
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
            prop: 'isWin',
            // label: '是否中标',
            label: () => this.$t('cusEntry.bidSuperviseReport.isWin'),
            renderHeader: this._addStarToColumn
          },
          slot: 'isWin'
        },
        {
          attrs: {
            prop: 'winRange',
            // label: '中标范围',
            label: () => this.$t('cusEntry.bidMod.winRange'),
            renderHeader: this._addStarToColumn
          },
          slot: 'winRange'
        },
        {
          attrs: {
            prop: 'winReason',
            // label: '中/落标原因',
            label: () => this.$t('cusEntry.supplement20250205.bidWinningLosingReasons'),
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
      this.tableData[i].winReason = null
      if (val == 'N') {
        this.tableData[i].winRange = []
      }
    },
    viewQuote () {
      this.$emit('viewQuote')
    },
    winRangeChange (val, row) {
      let orderItemId = null
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
    },
    selectAll(row) {
      // 检查是否允许操作
      if (row.isWin === 'N') {
        this.$message.warning('当前不可操作');
        return;
      }
      let arr = []
      this.winOptions.forEach(element => {
        arr.push(element.id)
      });
      let orderItemId = arr.length ? arr.join(';') : null
      row.orderItemId = orderItemId
      // // 将所有 winOptions 的 value 添加到 row.winRange 中
      row.winRange = this.winOptions.map(option => option.value);
      console.log(row.orderItemId)
    },
    /* 编排文件类型 */
    transformOptions (options,isWin) {
      // 字典中中标的条目编号
      let winArr = ['WIN_BID_TECH','WIN_BID_PRICE']
      if(isWin==='Y'){
        // 中标
        return options.filter((item)=>{
          return winArr.includes(item.value)
        })
      }else if(isWin==='N'){
        // 落标
        return options.filter((item)=>{
          return !winArr.includes(item.value)
        })
      }else{
        return options
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
  margin-bottom: 10px;
  .title {
    font-weight:bold;
    margin-right: 20px;
  }
}
</style>
