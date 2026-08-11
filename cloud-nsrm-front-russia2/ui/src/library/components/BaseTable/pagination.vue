/*
 * @Author: chenzp20
 * @Date: 2020-12-17 15:18:02
 * @Last Modified by: mikey.zhaopeng
 * @Last Modified time: 2022-02-25 17:11:53
 */
<template>
  <el-row class="pagination-wrapper">
    <el-col :span="24">
      <el-pagination
        class="pagination"
        :current-page="currentPage"
        :page-sizes="pageSizes"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-col>
  </el-row>
</template>

<script>

export default {
  name: 'Pagination',
  props: {
    total: Number,
    tableConfigChange: Function,
    pageSize: {
      default: 15,
      type: Number
    },
    pageSizes: {
      default: () => [15, 30, 45, 60],
      type: Array
    },
    currentPage: {
      default: 1,
      type: Number
    },
    customTable: {
      type: Boolean,
      default: true
    }
  },
  data () {
    return {}
  },
  computed: {},
  watch: {},
  created () {},
  mounted () {},
  methods: {
    handleCurrentChange (...args) {
      this.$emit('currentChange', ...args)
    },
    handleSizeChange (...args) {
      this.$emit('sizeChange', ...args)
    }
  }
}
</script>
<style scoped lang="scss">
.pagination {
  position: relative;
}
.setting-button {
  position: absolute;
  right: 0;
  bottom: 8px;
  overflow: hidden;
}
</style>
