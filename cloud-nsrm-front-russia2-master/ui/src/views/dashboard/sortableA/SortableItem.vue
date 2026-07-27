<template>
  <el-col
    :xs="12"
    :sm="6"
    :md="4"
    :lg="3"
    :xl="3"
  >
    <div class="list-item" @click="task(item.routeName, item.params)">
      <div :title="item.title">
        <span class="item">{{ item.title }}</span>
      </div>
      <div>
        <span class="count">{{ item.count }}</span>
      </div>
    </div>
  </el-col>
</template>
<script>
import { ElementMixin } from 'vue-slicksort'

export default {
  name: 'SortableList',
  mixins: [ElementMixin],
  props: {
    item: {
      type: Object,
      default: null
    }
  },
  methods: {
    // 任务跳转到对应的功能
    task (name, params) {
      if (!name) return
      let types = {
        [name]: name,
        'vendorBiddingList_new': 'inquiryBySimpleListVendor', // 询比价
        'supOnlineInvoice': 'purInvoiceSupplier', // 开票协同
        'purInvoice': 'purInvoiceSupplier', // 开票通知
        'deliveryOrder': 'vendorDeliveryOrder', // 送货单
        'vendorBidding': 'vendorBiddingList', // 招标
        'performanceAssessment': 'performanceAssessment' // 绩效考核
      }
      let routeName = ''
      const typeSearch = new Map(Object.entries(types))
      if (typeSearch.get(name)) routeName = typeSearch.get(name)
      this.$router.push({ name: routeName, params: params })
    },

    remove () {
      this.$emit('on-remove', this.index, this.item.id, this.item.parentId)
    }
  }
}
</script>
<style lang="scss" scoped>
.list-item{
  position: relative;
  z-index: 20;
  font-size: 14px;
  line-height: 30px;
  color: #595959;
  padding-left: 30px;
  // display: inline-block;
  // width: 30%;
  box-sizing: border-box;
  // position: relative;
  // overflow: hidden;
  .item {
    display: block;
    cursor: pointer;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .count {
    cursor: pointer;
    font-size: 24px;
    font-weight: bold;
    transition: color .3s;
    &:hover {
      color: #0077ff;
    }
  }
}
// .list-item {
//   display: flex;
//   justify-content: center;
//   align-items: center;
//   text-align: center;
//   list-style: none;
//   padding: 10px 0;
//   min-width: 100px;
//   z-index: 9999;
//   position: relative;
//   cursor: pointer;
//   font-size: 14px;
//   color: #292929;
//   i {
//     color: red;
//     font-style: normal;
//   }
// }
// .title {
// margin-top: 10px;
// max-width: 90px;
// overflow: hidden;
// text-overflow: ellipsis;
// white-space: nowrap;
// }
// .remove {
// }
// .icon-wrap {
//   position: relative;
// }
// .hot-area {
//   display: flex;
//   justify-content: center;
//   align-items: center;
//   width: 20px;
//   height: 20px;
//   position: absolute;
//   cursor: pointer;
//   right: -12px;
//   top: -10px;
// }
</style>
