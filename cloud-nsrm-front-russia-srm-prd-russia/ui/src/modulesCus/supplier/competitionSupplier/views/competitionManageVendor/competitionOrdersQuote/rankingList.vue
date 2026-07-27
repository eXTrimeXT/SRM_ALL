<!--
 * @Author: linyk7 linyk7@meicloud.com
 * @Date: 2023-06-29 16:40:02
 * @LastEditors: linyk7 linyk7@meicloud.com
 * @LastEditTime: 2023-06-29 17:13:08
 * @FilePath: \ui\src\modules\supplier\competitionSupplier\views\competitionManageVendor\competitionOrdersQuote\rankingList.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<template>
  <div class="competition-ranking-list">
    <div class="ranking-header">
      <span>单位: {{ $getDictLabel('unit',info.unit) }}&nbsp;|&nbsp;数量: {{ info.requireQuantity }}&nbsp;|&nbsp;定价开始: {{ $dayjsParse(baseInfo.orderStartTime) }}&nbsp;|&nbsp;定价结束: {{ $dayjsParse(baseInfo.orderEndTime) }}&nbsp;|&nbsp;</span>
      <span class="ranking-tax">税率：</span><dict-select v-model="info.taxKey" :disabled="!!info.taxKey && !!orderItemInfo.souItemId" code="tax" />
      <!-- <em style="margin: 0 4px 0 8px;">|</em> -->
      <!-- <span>行附件：<em class="ranking-upload" @click="uploadFileChange">上传附件</em></span> -->
    </div>
  </div>
</template>

<script>
import baseInfo from '@/modules/buyer/orderManagementBuyer/views/buyerPurchaseOrderNewEngine/components/collapseItem/baseInfo'
export default {
  name: 'RankingList',
  props: {
    info: {
      type: Object,
      default: () => {}
    },
    orderItemInfo: {
      type: Object,
      default: () => {}
    },
    baseInfo: {
      type: Object,
      default: () => {}
    }
  },
  data () {
    return {
    }
  },
  methods: {
    uploadFileChange () {

    }
  }
}
</script>

<style scoped lang="scss">
.competition-ranking-list {
  .ranking-header {
    color: #8484A4;
    display: flex;
    align-items: center;
    padding: 15px 0;
    border-bottom: 1px solid #E4E4E4;
    .ranking-tax {
      position: relative;
      margin-left: 12px;
      &::before {
        content: "*";
        position: absolute;
        top: 0;
        left: -6px;
        color: red;
      }
    }
    .ranking-tax-input {
      width: 150px;
      ::v-deep .el-input__inner {
        width: 150px;
      }
    }

    .ranking-upload {
      font-style: normal;
      color: #02AFF4;
    }
  }
}
</style>
