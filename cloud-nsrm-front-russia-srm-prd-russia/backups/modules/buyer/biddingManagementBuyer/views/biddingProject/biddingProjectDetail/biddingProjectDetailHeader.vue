<template>
  <el-header
    v-if="bidingBase.bidingNum"
    :height="35"
    class="header-info"
  >
    <ul class="the_top_item">
      <li style="flex: 2">
        <span>{{ $t('bidMod.bidingNum') }}:</span>
        <span :title="bidingBase.bidingNum">{{ bidingBase.bidingNum }}</span>
      </li>
      <li style="flex: 2">
        <span>{{ $t('bidMod.bidingName') }}:</span>
        <span :title="bidingBase.bidingName">{{ bidingBase.bidingName }}</span>
      </li>
      <!--项目状态-->
      <li style="flex: 2">
        <span>{{ $t('bidMod.bidingStatus') }}:</span>
        <span>{{ $getDictLabel("BIDDING_PRO_STATUS", bidingBase.bidingStatus ) }}</span>
      </li>
      <li>
        <span>{{ $t('bidMod.bidingCreatedBy') }}:</span>
        <span>{{ bidingBase.createdBy }}</span>
      </li>
      <li>
        <span>{{ $t('bidMod.bidingRound') }}:</span>
        <span>{{ bidingBase.currentRound }}</span>
      </li>
    </ul>
  </el-header>
</template>

<script>
/**
 * 头部信息
 */
export default {
  name: 'BiddingProjectDetailHeader',
  props: {
    bidingBase: {
      type: Object,
      default () {
        return {}
      }
    }
  },
  computed: {
    headerData () {
      const data = {
        bidingNum: '',
        bidingName: '',
        createdBy: '',
        bidingStatus: '',
        currentRound: ''
      }
      if (this.bidingBase && this.bidingBase.bidingNum) {
        for (let i in data) {
          data[i] = this.bidingBase[i] || ''
        }
      }
      return data
    }
  }
}
</script>

<style lang="scss" scoped>
.header-info {
  background-color: #f5f7fa;
  border-bottom: 1px solid #e6ebf5;
  padding: 0;
  margin: 0;
}
</style>
