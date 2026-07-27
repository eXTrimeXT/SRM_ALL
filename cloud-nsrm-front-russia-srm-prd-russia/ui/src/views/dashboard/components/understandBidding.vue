<template>
  <div class="notice-wrapper">
    <div class="grid-content">
      <h3 class="grid-title">
        <!-- 公告懂招标信息  -->
        懂招标
        <span
          class="info-more"
          @click="moreNews"
        >
          <!-- 更多 -->
          {{ $t("common.more") }}
          <i class="el-icon-arrow-right" />
        </span>
      </h3>
      <div class="content">
        <div class="notice-list">
          <div v-for="(item,index) in noticeList" :key="item.noticeId + index" class="notice-item" @click="goToNotice(item)">
            <div class="icon" :class="computedClass(item)">
              {{ computeName(item) }}
            </div>
            <div class="info themeLink">
              {{ item.title }}
            </div>
            <div class="time">
              {{ item.publishTime }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { noticeApi } from 'modcb@/userManage/api'
export default {
  name: 'NoticeInfo',
  data () {
    return {
      pageNum: 1,
      pageSize: 10,
      noticeList: [11],
      notcieType: [],
      applyArr: [
        { value: '1', label: 'test', text: '测试' },
        { value: '2', label: 'vacation', text: '假期' },
        { value: '120', label: 'purchase', text: '采购' },
        { value: '23', label: 'up', text: '升级' },
        { value: '232', label: 'notice', text: '通知' },
        { value: '2323', label: 'warning', text: '告警' }
      ]
    }
  },
  async created () {
    this.getNewsList()
  },
  methods: {
    // 更多
    moreNews () {
      this.$router.push({ name: 'checkUnderstandBidding' })
    },
    // 获取公告列表
    getNewsList () {
      noticeApi.noticeList({
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        noticeStatus: 'PUBLISHED'
      }).then(res => {
        this.noticeList = res.data.list.map(({ publishTime, ...rest }) => ({
          ...rest,
          publishTime: this.$dayjs(publishTime).format('YYYY-MM-DD')
        }))
        let endIndex = 5
        this.noticeList = this.noticeList.slice(0, endIndex)
      })
    },
    computedClass (item) {
      let flagIndex = this.applyArr.findIndex(i => i.value === item.noticeType)
      flagIndex = (flagIndex > -1) ? flagIndex : '4'
      return this.applyArr[flagIndex].label
    },
    computeName (item) {
      let flagIndex = this.applyArr.findIndex(i => i.value === item.noticeType)
      flagIndex = (flagIndex > -1) ? flagIndex : '4'
      return this.applyArr[flagIndex].text
    },
    goToNotice (row) {
      this.$router.push({ name: 'checkUnderstandBidding', params: { id: row.noticeId } })
    }
  }
}
</script>
<style scoped lang="scss">
.content {
  margin: 2px 0;
  min-height: 166px;
}
.notice-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
  .icon {
    width: 40px;
    height: 22px;
    border-radius: 4px;
    text-align: center;
    line-height: 20px;
    font-size: 12px;
    box-sizing: border-box;
    &.up { //升级
      border: 1px solid rgba(168,221,146,1);
      background:#F6FBF4;
      color: #52C718;
    }
    &.notice { //通知
      border: 1px solid #2994FF;
      background:#E6F6FF;
      color: #0077FF;
    }
    &.warning { //告警
      border: 1px solid rgba(250,210,149,1);
      background:#FEFAF4;
      color:#FAAE16;
    }
    &.test { //测试
      border: 1px solid #2994FF;
      background:#E6F6FF;
      color: #0077FF;;
    }
    &.vacation { //假期公告
      border: 1px solid #2994FF;
      background:#E6F6FF;
      color: #0077FF;;
    }
    &.purchase { //采购公告
      border: 1px solid #2994FF;
      background:#E6F6FF;
      color: #0077FF;;
    }
  }
  .info {
    flex: 1;
    margin-left: 8px;
    font-size: 14px;
    color: #51555B;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    height: 22px;
    line-height: 22px;
  }
  .time {
    width:100px;
    text-align: right;
    font-size: 14px;
    color: #96999C;
  }
  &+.notice-item {
    margin-top: 14px;
  }
  &:hover{
    .info{
      // color:#0077FF; // hover 跟主题色变化，此处去掉
    }
  }
}
</style>
