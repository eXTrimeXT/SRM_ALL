<template>
  <div class="wrapper">
    <!-- <div class="btns mb-10">
      <el-button v-if="!readonly" type="primary" @click="addNew">
        {{ $t('common.add') }}
      </el-button>
    </div> -->
    <el-table
      border
      stripe
      :data="tableData"
      max-height="250px"
    >
    <!-- 供方名称 -->
      <el-table-column
        prop="supName"
        :label="$t('cusEntry.supplement20250121.supName')"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <el-input v-if="false" v-model="row.supName" :disabled="readonly" />
          <span v-else>{{ row.supName }}</span>
        </template>
      </el-table-column>
      <!-- 经验性质 -->
      <el-table-column
        prop="nature"
        :label="$t('cusEntry.supplement20250121.empiricalNature')"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <el-input v-if="!readonly" v-model="row.nature" :disabled="readonly" />
          <span v-else>{{ row.nature }}</span>
        </template>
      </el-table-column>
      <!-- 品类 -->
      <el-table-column
        prop="categoryName"
        :label="$t('ccommon.category')"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <el-input v-if="!readonly" v-model="row.categoryName" :disabled="readonly" />
          <span v-else>{{ row.categoryName }}</span>
        </template>
      </el-table-column>
      <!-- 行业客户 -->
      <el-table-column
        prop="customers"
        :label="$t('cusEntry.supplement20250121.customers')"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <el-input v-if="!readonly" v-model="row.customers" :disabled="readonly" />
          <span v-else>{{ row.customers }}</span>
        </template>
      </el-table-column>
      <!-- 是否新引进 -->
      <el-table-column
        prop="isNew"
        :label="$t('cusEntry.supplement20250121.isNew')"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <DictSelect
            v-if="!readonly"
            v-model="row.isNew"
            code="YES_OR_NO"
          />
          <span v-else>{{ $getDictLabel('YES_OR_NO',row.isNew) }}</span>
        </template>
      </el-table-column>
      <!-- 备注 -->
      <el-table-column
        prop="remark"
        :label="$t('common.remark')"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <el-input v-if="!readonly" v-model="row.remark" :disabled="readonly" />
          <span v-else>{{ row.remark }}</span>
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
