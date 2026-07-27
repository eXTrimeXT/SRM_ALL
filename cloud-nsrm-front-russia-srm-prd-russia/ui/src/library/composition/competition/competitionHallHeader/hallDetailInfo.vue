<template>
  <div class="hall-detail-info countdown-wrap">
    <p class="title">
      {{ $t('bidMod.competitionLts.competitionRules') }}：
    </p>
    <ul class="hall-detail-info-ul">
      <li
        v-for="(item, index) in infoListData"
        :key="`info-${index}`"
        :title="item.value"
      >
        <span>{{ item.label }}: </span>
        <span>{{ item.value }}</span>
      </li>
    </ul>
  </div>
</template>

<script>
/**
 * 竞价大厅信息栏
 */
export default {
  name: 'HallDetailInfo',

  props: {
    infoList: {
      type: Array,
      default: () => []
    }
  },

  computed: {
    infoListData () {
      // 过滤启用的
      return this.infoList.filter(item => item.enable)
    }
  }
}
</script>

<style lang="scss" scoped>
@import "countdownWrap";
.hall-detail-info {
  width: 100%;
  .hall-detail-info-ul {
    display: flex;
    flex-wrap: wrap;
    margin: 0;
    padding: 0;
    li {
      list-style: none;
      // width: 50%;
      flex: 1 0 33.33%;
      height: 28px;
      line-height: 28px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      padding-right: 5px;
      box-sizing: border-box;
      color: #8484A4;
    }
  }
}
</style>
