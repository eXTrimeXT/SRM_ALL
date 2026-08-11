<template>
  <div class="competition-item-list">
    <p class="item-title">
      {{ $t('competition.itemList') }} <span>({{ totalPage }})</span>
    </p>
    <!-- 物料列表 -->
    <ul class="item-list">
      <li
        v-for="(item,index) in currentItemList"
        :key="index"
        class="item-li"
        :class="{'item-li-active':activeIndex === index}"
        @click="itemClick(item,index)"
      >
        <div class="left-active" :class="{'left-active-click':activeIndex === index}" />
        <p class="item-li-title">
          {{ item.itemDesc }}
        </p>
        <div class="li-between">
          <span>{{ $t('competition.auctRanking') }}<em>{{ item.auctRanking }}</em></span>
          <!-- <div v-if="item.priceNoBid === 'Y'" class="item-tag-end">
            已结束
          </div>
          <div v-else class="item-tag-process">
            进行中
          </div> -->
        </div>
      </li>
    </ul>
    <CPagination
      :pageNum="pageNum"
      :pageSize="pageSize"
      :pageSizes="pageSizes"
      :total="totalPage"
      :layout="layout"
      @current-change="currentChange"
      @size-change="sizeChange"
    />

    <!-- <div class="item-bottom">
      <span class="bottom-left">共{{ itemList.length }}条</span>
      <div class="bottom-right">
        <span class="icon" style="margin-right: 8px;" @click="handlePageDown"><</span>
        <el-input
          v-model="currentPageNo"
          v-input-format="{type:'integer',min:1}"
          size="mini"
          style="width: 32px; margin-right: 6px;"
          @keyup.enter.native="handlePageChange"
        />
        <em>/</em>
        <span style="margin-left: 4px;">{{ totalPage }}</span>
        <span class="icon" style="margin-left:8px;" @click="handlePageUp"> ></span>
      </div>
    </div> -->
  </div>
</template>

<script>
import CPagination from 'lib@/components/c-pagination'
export default {
  name: 'ItemList',
  components: {
    CPagination
  },
  props: {
    orderItemList: {
      type: Array,
      default: () => []
    },
    souItemId: null,
    orderInfo: {
      type: Object,
      default: () => {}
    }
  },
  data () {
    return {
      pageNum: 1,
      pageSize: 10,
      currentPageNo: 1,
      layout: 'total,prev,sizes,next',
      pageSizes: [10, 15, 20, 30]
    }
  },
  computed: {
    itemList: {
      get: function () {
        return this.orderItemList
      },
      set: function (val) {
        this.$emit('update:orderItemList', val)
      }
    },
    currentItemList () {
      return this.itemList.slice((this.pageNum - 1) * this.pageSize, this.pageNum * this.pageSize)
    },
    totalPage () {
      return this.itemList.length
    },
    activeIndex () {
      let index = this.currentItemList.findIndex(item => item.souItemId === this.souItemId)
      if (index > -1) {
        return index
      } else {
        return 0
      }
    }
  },
  methods: {
    itemClick (row, index) {
      this.$emit('item-click', row)
    },
    currentChange (value) {
      this.pageNum = value
    },
    sizeChange (value) {
      this.pageSize = value
    },
    handlePageDown () {
      if (this.currentPageNo === 1) return
      this.currentPageNo--
      this.pageNum = this.currentPageNo
    },
    handlePageUp () {
      if (this.currentPageNo === this.totalPage) return
      this.currentPageNo++
      this.pageNum = this.currentPageNo
    },
    handlePageChange () {
      let oldPageNo = this.currentPageNo
      if (this.currentPageNo < 1 || this.currentPageNo > this.totalPage) {
        this.currentPageNo = 1
        return
      }
      this.pageNum = this.currentPageNo
    }
  }
}
</script>

<style scoped lang="scss">
.competition-item-list {
  height: 100%;
  overflow: hidden scroll;
  .item-title {
    font-size: 18px;
    font-weight: 500;
  }

  .item-list {
    padding: 0;
    margin: 0 0 8px 0;
    width: 100%;
    max-height: 300px;
    overflow-y: scroll;

    .item-li {
      width: 100%;
      height: 80px;
      list-style: none;
      margin: 0 0 4px 0;
      padding: 8px;
      background-color: #F9F9F9;
      position: relative;
      cursor: pointer;

      .item-li-title {
        width: 100%;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }

      .li-between {
        display: flex;
        justify-content: space-between;

        span {
          color: #F59A23;

          em {
            font-style: normal;
          }
        }

        .item-tag-process {
          color: #0C8460;
          background-color: #BEEAD2;
          padding: 4px 6px;
        }
        .item-tag-end {
          color: #848484;
          background-color: #dddddd;
          padding: 4px 6px;
        }
      }

      .left-active {
        opacity: 0;
        width: 4px;
        height: 100%;
        border-left: 4px solid #0077FF;
        position: absolute;
        top: 0;
        left: 0;
      }

      &:hover {
        background-color: #E5F5FE;

        .left-active {
          opacity: 1;
          transition: opacity 0.3s ease-out 0.2s;
        }
      }
      .left-active-click {
        opacity: 1;
        transition: opacity 0.3s ease-out 0.2s;
      }
      &.item-li-active {
        background-color: #E5F5FE;
      }
    }
  }

  .item-bottom {
    display: flex;
    justify-content: space-between;
    align-items: center;
    .bottom-left {
      font-size: 12px;
      color: #999;
    }
    .bottom-right {
      font-size: 12px;
    }
    .icon {
      cursor: pointer;
    }
    ::v-deep .el-input__inner {
      width: 32px;
      padding: 0 2px !important;
      text-align: center;
    }
  }
}
</style>
<style lang="scss">
.competition-item-list {
  .el-pagination__sizes {
    margin:0 !important;
  }
}
</style>
