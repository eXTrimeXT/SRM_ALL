<template>
  <el-header
    v-if="headerData.souNo"
    height="35"
    class="header-info"
  >
    <ul class="the_top_item">
      <!--项目编号-->
      <li>
        <span>{{ $t('bidMod.bidingNum') }}:</span>
        <span :title="headerData.souNo">{{ headerData.souNo }}</span>
      </li>
      <!--项目名称-->
      <li>
        <span>{{ $t('bidMod.bidingName') }}:</span>
        <span :title="headerData.souName">{{ headerData.souName }}</span>
      </li>
      <!--创建人-->
      <li>
        <span>{{ $t('bidMod.bidingCreatedBy') }}:</span>
        <span>{{ headerData.createdBy }}</span>
      </li>
      <li>
        <span>{{ $t('bidMod.bidingStatus') }}:</span>
        <span>{{ $getDictLabel('SOU_PROJECT_STATUS', headerData.projectStatus) }}</span>
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
    baseInfo: {
      type: Object,
      default: () => { /* nothing */ }
    }
  },

  computed: {
    headerData () {
      const data = {
        souNo: '',
        souName: '',
        createdBy: '',
        projectStatus: ''
      }
      if (this.baseInfo && this.baseInfo.souNo) {
        for (let i in data) {
          data[i] = this.baseInfo[i] || ''
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
  .the_top_item {
    margin: 0;
    padding-left: 5px;
    display: flex;
    > li {
      flex: 1;
      list-style: none;
      line-height: 30px;
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
