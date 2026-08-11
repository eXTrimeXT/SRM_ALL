<template>
  <el-header
    v-if="bargainBase.souNo"
    height="35"
    class="header-info"
  >
    <ul class="header-info-warap">
      <li style="flex: 2">
        <!-- 竞价单号 -->
        <span>{{ $t('bidMod.competitionLts.souNo') }}:</span>
        <span :title="bargainBase.souNo">{{ bargainBase.souNo }}</span>
      </li>
      <li style="flex: 2">
        <!-- 竞价标题 -->
        <span>{{ $t('bidMod.competitionLts.souName') }}:</span>
        <span :title="bargainBase.souName">{{ bargainBase.souName }}</span>
      </li>
      <li>
        <!-- 创建人 -->
        <span>{{ $t('bidMod.bidingCreatedBy') }}:</span>
        <span>{{ bargainBase.createdBy }}</span>
      </li>
      <!--竞价状态-->
      <li style="flex: 2">
        <span>{{ $t('bidMod.competitionLts.souStatus') }}:</span>
        <span>{{ $getDictLabel(projectStatusDictCode, bargainBase.projectStatus ) }}</span>
      </li>
      <li>
        <!-- 轮次 -->
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
    },
    projectStatusDictCode: {
      type: String,
      required: true
    }
  },

  computed: {
    headerData () {
      const data = {
        souNo: '',
        souName: '',
        createdBy: '',
        extProjectStatus: '',
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

  .header-info-warap {
    margin: 0;
    padding-left: 5px;
    display: flex;

    >li {
      line-height: 30px;
      flex: 1;
      list-style: none;
      white-space: nowrap;
      text-overflow: ellipsis;
      overflow: hidden;

      span:first-child {
        font-weight: bolder;
      }

      span:last-child {
        padding: 0 5px;
      }
    }
  }
}
</style>
