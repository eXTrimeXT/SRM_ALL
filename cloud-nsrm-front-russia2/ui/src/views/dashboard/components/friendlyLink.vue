<template>
  <div class="grid-content">
    <h3 class="grid-title">
      <!-- 云生态 -->
      {{ $t("dashboard.cloudEcology") }}
    </h3>
    <div class="content">
      <el-row>
        <el-col
          v-for="(item,index) in cloudList"
          :key="index"
          :xs="24"
          :sm="24"
          :md="colSpan"
          :lg="colSpan"
          :xl="colSpan"
        >
          <div class="cloud-item" @click="toUrl(item,index)">
            <img :src="item.imgUrl">
            <div class="info">
              <div class="title themeLink">
                {{ item.title }} <span v-if="index === 0" class="tag">{{ $t('cusEntry.dashboard.free') }}</span>
              </div>
              <div class="introduce">
                {{ item.desc }}
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script>
import { getToken } from '@/utils/auth'
import { mapGetters } from 'vuex'
import cloud1Img from '@/assets/images/cloud1@2x.png'
import cloud2Img from '@/assets/images/cloud2@2x.png'
import cloud3Img from '@/assets/images/cloud3@2x.png'
export default {
  name: 'FriendlyLink',
  data () {
    return {
      // colSpan: null,
      cloudList: [
        {
          imgUrl: cloud1Img,
          title: this.$t('dashboard.sourceCloud'),
          desc: this.$t('cusEntry.dashboard.link1'), // 社交化企业寻源，互动式供需匹配
          linkUrl: 'https://sourcing.meicloud.com'
        },
        // {
        //   imgUrl: cloud2Img,
        //   title: this.$t('dashboard.creditCloud'),
        //   desc: this.$t('cusEntry.dashboard.link2'), // 全景化征信识别，一站式风控服务
        //   linkUrl: 'https://credit-dev.meicloud.com'
        // },
        {
          imgUrl: cloud3Img,
          title: this.$t('dashboard.optiMailCloud'),
          desc: this.$t('cusEntry.dashboard.link3'), // 规范化间接采购，便利式内部商城
          linkUrl: 'https://mro-dev.meicloud.com'
        }
      ]
    }
  },
  computed: {
    ...mapGetters(['userType']),
    colSpan () {
      if (this.userType === 'BUYER') {
        return 24
      }
      return 8
    }
  },
  methods: {
    toUrl (item, index) {
      let url = index === 2 ? item.linkUrl : item.linkUrl + `?KeyToken=${getToken()}`
      window.open(url,'_blank','noopener,noreferrer')
    }
  }
}
</script>
<style lang="scss" scoped>
.grid-content {
  padding-bottom: 24px !important;
}
.cloud-list {
  &.cloud-list-flex {
    display: flex;
    .cloud-item {
      border-right: 1px solid red;
    }
  }
}
.cloud-item {
  display:flex;
  align-items:center;
  margin-top: 16px;
  cursor:pointer;
  img {
    width:60px;
    height:60px;
  }
  .info {
    margin-left:8px;
    .title {
      font-size: 16px;
      line-height: 24px;
      height:24px;
      display: flex;
      align-items: center;
      .tag {
        display:inline-block;
        width: 32px;
        background: #FF4A4D;
        border-radius: 4px;
        height: 20px;
        font-size: 12px;
        color: #FFFFFF;
        text-align: center;
        line-height: 20px;
        font-weight: 400;
        margin-left:6px;
      }
    }
    .introduce {
      font-size:12px;
      color:#96999C;
      height:20px;
      line-height:20px;
      margin-top: 8px;
    }
  }
  .title{
    color: #393E45;
    &:hover{
      // color:#0077FF;
    }
  }
}
</style>
