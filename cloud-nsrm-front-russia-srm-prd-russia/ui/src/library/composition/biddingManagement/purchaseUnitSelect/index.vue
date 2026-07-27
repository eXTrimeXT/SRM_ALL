<template>
  <div class="purchase-unit-select">
    <el-select
      v-if="!showLabelVisible"
      v-model="unitValue"
      clearable
    >
      <el-option
        v-for="(item, index) in purchaseUnitList"
        :key="`purchaseUnit-${index}`"
        :label="item.label"
        :value="item.value"
      />
    </el-select>
    <span v-else>{{ $getDictLabelByValue(purchaseUnitList, unitValue) }}</span>
  </div>
</template>

<script>
/**
 * 单位下拉框
 */
export default {
  name: 'PurchaseUnitSelect',
  props: {
    value: {
      type: [String, Number]
    },
    showLabelVisible: {
      type: Boolean,
      default: function () {
        return false
      }
    },
    options: {
      type: Array
    }
  },
  data () {
    return {
      purchaseUnitList: []
    }
  },
  computed: {
    unitValue: {
      get: function () {
        return this.value
      },
      set: function (val) {
        return this.$emit('update:value', val)
      }
    }
  },
  mounted () {
    if (!this.options || this.options.length === 0) {
      this.getPurchaseUnitData()
    } else {
      this.purchaseUnitList = this.options
    }
  },
  methods: {
    /* 查询单位列表 */
    async getPurchaseUnitData () {
      const data = await this.$http({
        url: '/api-base/purchase/purchaseUnit/listPage',
        method: 'POST',
        data: {
          pageNum: 1,
          pageSize: 1000
        },
        loading: true
      })
      const list = data.data.list
      if (data && data.data && Array.isArray(list)) {
        this.purchaseUnitList = list.map(item => {
          return {
            id: item.unitId,
            label: item.unitName,
            value: item.unitCode
          }
        })
      }
    }
  }
}
</script>
