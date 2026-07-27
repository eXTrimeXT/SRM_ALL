<template>
  <div class="wrapper">
    <div class="btns mb-10">
      <!-- 汽柴油 -->
      <span class="title">{{ $t('cusEntry.supplement20250121.gasOil') }}</span>
      <el-button v-if="!readonly" type="primary" @click="addNew">
        {{ $t('common.add') }}
      </el-button>
    </div>
    <el-table
      border
      stripe
      :data="tableData"
      max-height="250px"
    >
    <!-- 招标区域 -->
      <el-table-column
        prop="bidArea"
        :label="$t('cusEntry.supplement20250121.bidArea')"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <el-input v-if="!readonly" v-model="row.bidArea" :disabled="readonly" />
          <span v-else>{{ row.bidArea }}</span>
        </template>
      </el-table-column>
      <!-- 油品型号 -->
      <el-table-column
        prop="ypType"
        :label="$t('cusEntry.supplement20250121.ypType')"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <el-input v-if="!readonly" v-model="row.ypType" :disabled="readonly" />
          <span v-else>{{ row.ypType }}</span>
        </template>
      </el-table-column>
      <!-- 采购量 -->
      <el-table-column
        prop="buyNum"
        :label="$t('cusEntry.supplement20250121.buyNum')"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <el-input v-if="!readonly" v-model="row.buyNum" :disabled="readonly" />
          <span v-else>{{ row.buyNum }}</span>
        </template>
      </el-table-column>
      <!-- 采购额 -->
      <el-table-column
        prop="buyMoney"
        :label="$t('cusEntry.supplement20250121.buyAmount')"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <el-input v-if="!readonly" v-model="row.buyMoney" :disabled="readonly" />
          <span v-else>{{ row.buyMoney }}</span>
        </template>
      </el-table-column>
      <!-- 现行政策 -->
      <el-table-column
        prop="curPol"
        :label="$t('cusEntry.supplement20250121.curPol')"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <el-input v-if="!readonly" v-model="row.curPol" :disabled="readonly" />
          <span v-else>{{ row.curPol }}</span>
        </template>
      </el-table-column>
      <!-- 拟询价供方 -->
      <el-table-column
        prop="selSup"
        :label="$t('cusEntry.supplement20250121.selSup')"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <el-input v-if="!readonly" v-model="row.selSup" :disabled="readonly" />
          <span v-else>{{ row.selSup }}</span>
        </template>
      </el-table-column>
      <!-- 招标策略 -->
      <el-table-column
        prop="bidStr"
        :label="$t('cusEntry.supplement20250121.bidStr')"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <el-input v-if="!readonly" v-model="row.bidStr" :disabled="readonly" />
          <span v-else>{{ row.bidStr }}</span>
        </template>
      </el-table-column>
      <!-- 目标设定 -->
      <el-table-column
        prop="tarSet"
        :label="$t('cusEntry.supplement20250121.tarSet')"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <el-input v-if="!readonly" v-model="row.tarSet" :disabled="readonly" />
          <span v-else>{{ row.tarSet }}</span>
        </template>
      </el-table-column>
      <!-- 操作 -->
      <el-table-column
        v-if="!readonly"
        prop="operation"
        :label="$t('common.operation')"
        width="100"
      >
        <template v-slot="{row,$index}">
          <el-button type="text" @click="deleteRow($index)">
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
export default {
  props: {
    value: {
      type: Array,
      default: () => []
    },
    readonly: {
      type: Boolean,
      default: false
    }
  },
  emits: ['update:value'],
  data () {
    return {

    }
  },
  computed: {
    tableData: {
      get () {
        return this.value
      },
      set (value) {
        this.$emit('update:value', value)
      }
    }
  },
  methods: {
    addNew () {
      this.tableData.push({})
    },
    deleteRow (index) {
      this.tableData.splice(index, 1)
    }
  }
}
</script>
<style lang="scss" scoped>
.mt-10 {
  margin-top: 10px;
}
.mb-10 {
  margin-bottom: 10px;
}
.btns {
  .title {
    margin-right:20px;
  }
}
</style>
