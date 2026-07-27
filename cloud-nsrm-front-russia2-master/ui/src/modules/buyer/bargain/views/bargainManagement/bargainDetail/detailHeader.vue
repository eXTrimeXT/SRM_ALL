<template>
  <el-header
    v-if="bargainBase.souNo"
    height="35"
    class="header-info"
  >
    <ul class="the_top_item">
      <li style="flex: 2">
        <span>{{ $t('bidMod.bidingNum') }}:</span>
        <span :title="bargainBase.souNo">{{ bargainBase.souNo }}</span>
      </li>
      <li style="flex: 2">
        <span>{{ $t('bidMod.bidingName') }}:</span>
        <span :title="bargainBase.souName">{{ bargainBase.souName }}</span>
      </li>
      <!--项目状态-->
      <li style="flex: 2">
        <span>{{ $t('bidMod.bidingStatus') }}:</span>
        <span>{{ $getDictLabel("SOU_PROJECT_STATUS", bargainBase.projectStatus ) }}</span>
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
  name: 'DetailHeader',

  props: {
    bargainBase: {
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
      if (this.bargainBase && this.bargainBase.souNo) {
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
