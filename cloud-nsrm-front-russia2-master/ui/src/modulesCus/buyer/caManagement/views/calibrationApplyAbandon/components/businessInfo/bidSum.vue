<template>
  <div class="wrapper">
    <el-table
      ref="bidSumTable"
      :data="bidSumData"
      border
    >
      <!-- <el-table-column
        align="center"
        prop="reviewItem"
        label="详情"
        show-overflow-tooltip
      /> -->
      <el-table-column
        align="center"
        prop="reviewItem"
        :label="$t('vendorMod.particulars')"
        show-overflow-tooltip
      />
      <el-table-column
        v-for="(item,index) in bidSumData[0]?.summaryList"
        :key="index"
        :label="item.vendorName"
        :prop="`comment${item.vendorId}`"
        align="center"
        min-width="150"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <!-- <el-input
            v-model="scope.row.summaryList[index].comment"
            placeholder="请填写"
            :disabled="readonly"
          /> -->
          <el-input
            v-model="scope.row.summaryList[index].comment"
            :placeholder="$t('contractMod.pleaseFillIn')"
            :disabled="readonly"
          />
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>

export default {
  components: {},
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
    return {}
  },
  computed: {
    bidSumData: {
      get () {
        return this.value
      },
      set (val) {
        this.$emit('update:value', val)
      }
    }
  },
  methods: {}
}
</script>
