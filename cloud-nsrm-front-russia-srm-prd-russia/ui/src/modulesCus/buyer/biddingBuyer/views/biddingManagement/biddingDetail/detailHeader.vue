<template>
  <el-header
    height="35"
    class="header-info"
  >
    <ul class="the_top_item">
      <li style="flex: 2">
        <span>{{ $t('logisticsMod.bidingCode') }}:</span>
        <span :title="biddingBase.souNo">{{ biddingBase.souNo }}</span>
      </li>
      <li style="flex: 2">
        <span>{{ $t('bidMod.bidingName') }}:</span>
        <span :title="biddingBase.souName">{{ biddingBase.souName }}</span>
      </li>
      <!--招标项目状态-->
      <li style="flex: 2">
        <span>{{ $t('cusEntry.biddingSettings.projectStatus') }}:</span>
        <span>{{ $getDictLabel("SOU_BIDDING_PRO_STATUS", biddingBase.projectStatus ) }}</span>
      </li>
      <li>
        <span>{{ $t('bidMod.bidingCreatedBy') }}:</span>
        <span>{{ biddingBase.createdFullName }}</span>
      </li>
      <li>
        <span>{{ $t('bidMod.bidingRound') }}:</span>
        <span>{{ biddingBase.currentRound }}</span>
      </li>
    </ul>
  </el-header>
</template>

<script>
/**
 * 头部信息
 */
export default {
  name: 'DetailHeader',

  props: {
    biddingBase: {
      type: Object,
      default: () => ({})
    }
  },

  computed: {
    headerData () {
      const data = {
        souNo: '',
        souName: '',
        createdBy: '',
        projectStatus: '',
        currentRound: ''
      }
      if (this.biddingBase && this.biddingBase.souNo) {
        for (let i in data) {
          data[i] = this.biddingBase[i] || ''
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
