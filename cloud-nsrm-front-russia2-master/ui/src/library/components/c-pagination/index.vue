/* 深圳美云智数科技有限公司 Modified By: zhaomz1 */
<template>
  <div :class="['c-pagination',device]">
    <el-pagination
      class="pagination"
      popper-class="c-pagination-dropdown"
      :layout="layoutRes"
      :current-page="pageNum"
      :page-size="pageSize"
      :page-sizes="pageSizes"
      :small="small"
      :total="pageTotal"
      :pager-count="pagerCount"
      @size-change="changeCurrentSize"
      @current-change="changeCurrentIndex"
    />
    <slot />
  </div>
</template>

<script>
import _isNaN from 'lodash/isNaN'

const DEFAULT_PAGE_NUM = 1
const DEFAULT_PAGE_SIZE = 15
const DEFAULT_PAGE_SIZES = [15, 30, 60, 120, 300, 600, 1000, 1500]

export default {
  name: 'CPagination',
  props: {
    data: {
      type: Object
    },
    total: {
      type: [Number, String]
    },
    pageNum: {
      type: Number,
      default: DEFAULT_PAGE_NUM
    },
    pageSize: {
      type: Number,
      default: DEFAULT_PAGE_SIZE
    },
    pageSizes: {
      type: Array,
      default: () => {
        return DEFAULT_PAGE_SIZES
      }
    },
    small: {
      type: Boolean,
      default: false
    },
    layout: {
      type: String,
      default: 'total, prev, pager, next,sizes, jumper'
    },
    pagerCount: {
      type: Number,
      default: () => {
        return 7
      }
    }
  },
  computed: {
    pageTotal () {
      if (typeof this.total !== 'undefined') {
        return Number(this.total)
      }
      return Number(this.data.total) || 0
    },
    device () {
      return this.$store.state.app.device
    },
    layoutRes () {
      if (this.device === 'device-xs') {
        return 'total, prev, next,sizes, jumper'
      } else {
        return this.layout
      }
    }
  },
  watch: {
    pageSize (newValue, oldValue) {
      // 改变分页的时候触发两次请求 暂时注释掉
      // if (newValue <= 0 || _isNaN(newValue)) {
      //   this.changeCurrentSize(DEFAULT_PAGE_SIZE)
      // } else if (newValue !== oldValue) {
      //   this.changeCurrentSize(newValue)
      // }
    }
  },
  methods: {
    // 改变 currentNum
    changeCurrentIndex (value) {
      console.log(value)
      this.$emit('update:pageNum', value)
      this.$emit('current-change', value)
    },
    // 改变 currentSize
    changeCurrentSize (value) {
      this.$emit('update:pageSize', value)
      this.$emit('size-change', value)
      // this.changeCurrentIndex(1)
    }
  }
}
</script>

<style lang="scss" scoped>
// $primary-color: #409EFF;
.c-pagination {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding-top: 16px;
  padding-bottom: 16px;
  width: 100%;
}

.c-pagination-dropdown {
  .el-select-dropdown__item {
    font-size: 12px;
    font-weight: normal;
  }
}
</style>
