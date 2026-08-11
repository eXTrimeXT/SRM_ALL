<template>
  <el-header
    v-if="bargainBase.bargainNum"
    :height="35"
    class="header-info"
  >
    <ul class="the_top_item">
      <li style="flex: 2">
        <span>{{ $t('bidMod.bidingNum') }}:</span>
        <span :title="bargainBase.bargainNum">{{ bargainBase.bargainNum }}</span>
      </li>
      <li style="flex: 2">
        <span>{{ $t('bidMod.bidingName') }}:</span>
        <span :title="bargainBase.bargainName">{{ bargainBase.bargainName }}</span>
      </li>
      <!--项目状态-->
      <li style="flex: 2">
        <span>{{ $t('bidMod.bidingStatus') }}:</span>
        <span>{{ $getDictLabel("BRG_PROJECT_STATUS", bargainBase.bargainStatus ) }}</span>
      </li>
      <li>
        <span>{{ $t('bidMod.bidingCreatedBy') }}:</span>
        <span>{{ bargainBase.createdBy }}</span>
      </li>
      <li>
        <span>{{ $t('bidMod.bidingRound') }}:</span>
        <span>{{ bargainBase.currentRound }}</span>
      </li>
    </ul>
  </el-header>
</template>

<script>
/**
 * 头部信息
 */
export default {
  name: 'BargainDetailHeader',
  props: {
    bargainBase: {
      type: Object,
      default () {
        return {}
      }
    }
  },
  computed: {
    headerData () {
      const data = {
        bargainNum: '',
        bargainName: '',
        createdBy: '',
        bargainStatus: '',
        currentRound: ''
      }
      if (this.bargainBase && this.bargainBase.bargainNum) {
        for (let i in data) {
          data[i] = this.bargainBase[i] || ''
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
