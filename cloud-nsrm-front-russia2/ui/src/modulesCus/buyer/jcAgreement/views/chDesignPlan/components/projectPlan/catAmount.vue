<template>
  <div class="wrapper">
    <div v-if="!readonly" class="btns mb-10">
      <el-button type="primary" @click="addNew">
        {{ $t('common.add') }}
      </el-button>
    </div>
    <el-table
      border
      stripe
      :data="tableData"
      max-height="250px"
    >
    <!-- 物资名称 -->
      <el-table-column
        prop="materialName"
        :label="$t('common.materialName')"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <el-input v-if="!readonly" v-model="row.materialName" :disabled="readonly" />
          <span v-else>{{ row.materialName }}</span>
        </template>
      </el-table-column>
      <!-- 金额 -->
      <el-table-column
        prop="matMoney"
        :label="$t('orderMod.amount')"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <el-input v-if="!readonly" v-model="row.matMoney" :disabled="readonly" />
          <span v-else>{{ row.matMoney }}</span>
        </template>
      </el-table-column>
      <!-- 占比 -->
      <el-table-column
        prop="matRate"
        :label="$t('reportMod.percentage')"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <el-input v-if="!readonly" v-model="row.matRate" :disabled="readonly" />
          <span v-else>{{ row.matRate }}</span>
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
</style>
