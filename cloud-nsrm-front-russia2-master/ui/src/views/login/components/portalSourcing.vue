<template>
  <div class="page-portal-sourcing">
    <section
      class="portal-header"
    >
      <!-- <div v-if="userInfo && userInfo.userId" class="title">
        {{ $t('common.publicSourcing') }}
      </div> -->
      <div class="search">
        <el-input v-model="search" :placeholder="$t('components.common.enterKeyword')" />
        <i class="el-icon-search" @click="clickSearch" />
      </div>
    </section>
    <section
      v-loading="loading"
      element-loading-background="rgba(0, 0, 0, 0.4)"
      class="portal-main"
    >
      <ul class="portal-main-ul">
        <li v-for="item in lists" :key="'source-item-'+item.reqHeadId" class="portal-main-li" @click="buttonClick(item)">
          <!--          <el-tooltip class="item" effect="dark" :content="item.souReqTitile" placement="top">-->
          <div class="title">
            {{ item.projectName }}
          </div>
          <!--          </el-tooltip>-->
          <div class="subtitle-and-buttom">
            <!--            <el-tooltip class="item" effect="dark" :content="item.categoryFullName" placement="top">-->
            <div class="subtitle">
              <!-- {{ item.categoryFullName }} -->
            </div>
            <!--            </el-tooltip>-->
            <!--            <div class="buttons" @click="buttonClick(item)">-->
            <!--              查看详情-->
            <!--            </div>-->
          </div>
          <section class="main-content">
            <img :src="logoInfo.placeholderLogo">
            <ul>
              <li>
                <span>{{ $t('competition.untilTheDeadline') }}</span>
                <span class="yellow">{{ getDay(item.publicEndTime) }}{{ $t('time.days') }}</span>
              </li>
              <li>
                <!-- 发布时间： -->
                <span>{{ $t('cusEntry.dashboard.publishTime') }}</span>
                <span>{{ $parseTime(item.releaseDate) }}</span>
              </li>
              <li>
                <!-- 截止时间： -->
                <span>{{ $t('cusEntry.dashboard.deadlineTime') }}</span>
                <span>{{ $parseTime(item.publicEndTime) }}</span>
              </li>
              <li>
                <span>{{item.categoryName}}</span>
              </li>
            </ul>
          </section>
        </li>
      </ul>
      <el-pagination
        background
        layout="total,prev, pager, next"
        class="pagination"
        :page-size="8"
        :total="total"
        @current-change="current_change"
      />
    </section>
  </div>
</template>
<script>
import { portalSourcing } from '@/service/modules/base'
import { getSystemTheme } from '@/config/logo-config'
import { transformMQL } from '@/library/utils/util'

export default {
  name: 'PortalSourcing',
  // filters: {
  //   time: function (value) {
  //     let saveValus = value.substr(0, 10)
  //     return saveValus
  //   }
  // },
  props: {
    userInfo: {
      type: Object,
      default: () => {}
    }
  },
  data () {
    return {
      logoInfo: getSystemTheme(),
      loading: false,
      lists: [],
      total: 0,
      search: ''
    }
  },
  created () {
    this.init({ pageNum: 1, pageSize: 8 })
  },
  methods: {
    getDay (newDay) {
      if (!newDay) return null
          let diff = new Date(newDay).getTime() - new Date().getTime()
          return parseInt((diff / (1000 * 60 * 60 * 24)) + 1)
    },
    clickSearch () {
      const search = this.search
      const obj = {
        projectName: search || null,
        pageNum: 1,
        pageSize: 8
      }
      this.init(obj)
    },
    init (obj) {
      this.loading = true
      const data = transformMQL.listPageData({
        params: {projectName: obj?.projectName},
        type: 'SouFirstPage',
        action: 'souReqlistPage',
        pageNum: obj.pageNum,
        pageSize: obj.pageSize
      })
      this.$http({
        url: '/api-sou/sou-firstPage/souReqlistPage',
        method: 'POST',
        data: {
          projectName: obj?.projectName,
          pageNum: obj?.pageNum,
          pageSize: obj?.pageSize
        }
      }).then(res => {
        this.loading = false
        this.lists = res.data.list
        this.total = res.data.total
      })
      // this.$api.base.portalSourcing.listPage(obj).then(data => {
      //   this.loading = false
      //   this.lists = data.data.list
      //   this.total = data.data.total
      // })
    },
    current_change (index) {
      let obj = {
        pageNum: index,
        pageSize: 8
      }
      this.init(obj)
    },
    buttonClick (row) {
      if (this.userInfo && this.userInfo.userId) {
        this.$router.push({ path: '/sourcing/sourcingApplicationDetail?id=' + row.reqHeadId + '&reqHeadNo=' + row.reqHeadNo })
      } else {
        this.$router.push({ path: 'portal?id=' + row.reqHeadId })
      }
    }
  }
}
</script>
<style lang="scss" scoped>

:deep(.el-pagination.is-background .btn-next),
:deep(.el-pagination.is-background .el-pager li),
:deep(.el-pagination.is-background .btn-prev)
{
  background-color: #FFFFFF;
}

:deep(.el-pagination .el-pagination__total){
  float: initial;
}

.page-portal-sourcing{
  width: 85%;
  max-width: 1252px;
  //min-height: 360px;
  margin: 0 auto;
  //height: 400px; // 开发根据情况去掉高度
  .portal-header{
    overflow: hidden;
    margin: 25px 0;
    .title{
      float: left;
      width: 96px;
      height: 24px;
      font-size: 18px;
      color: #161C24;
      line-height: 35px;
      font-weight: 500;
    }
    .search{
      float: right;
      width: 444px;
      height: 32px;
      // background: #FFFFFF;
      // border: 1px solid rgba(220,221,222,1);
      border-radius: 4px;
      position: relative;
      .el-input{
        height: 100%;
        :deep(.el-input__inner){
          height: 30px !important;
          line-height: 30px !important;
          min-height: 30px !important;
          border-radius: 2px;
        }
      }
      :deep(.el-icon-search) {
        position: absolute;
        top: 7px;
        right: 12px;
        font-size: 17px;
        cursor: pointer;
        color: #96999c;
        &:hover{
          color: #0077FF;
        }
      }
    }
  }
  .portal-main{
    min-height: 200px;
    .pagination{
      margin-bottom: 10px;
    }
    .portal-main-ul{
      display: flex;
      flex-flow: wrap;
      align-content: flex-start;
      padding: 0;
      margin-right: -2%;
      .portal-main-li{
        padding: 22px 24px 24px;
        list-style-type: none;
        cursor: pointer;
        width: 48%;
        margin: 0 2% 20px 0;
        background: #FFFFFF;
        box-shadow: 0px 6px 16px -16px rgba(0,0,0,0.08);box-shadow: 0px 9px 28px 0px rgba(0,0,0,0.05);box-shadow: 0px 12px 48px 16px rgba(0,0,0,0.03);
        position: relative;
        border-radius: 4px;
        .title{
          word-break: break-all;
          text-overflow: ellipsis;
          display: -webkit-box;
          -webkit-box-orient: vertical;
          -webkit-line-clamp: 1; /* 这里是超出几行省略 */
          overflow: hidden;
          font-size: 18px;
          color: #393E45;
          line-height: 24px;
          font-weight: 700;
          cursor: pointer;
        }
        .title:hover{
          color: rgb(20,142,245);
        }
        .subtitle-and-buttom{
          margin: 15px 0 24px 0;
          display: flex;
          .subtitle{
            overflow:hidden;
            white-space: nowrap;
            text-overflow: ellipsis;
            font-size: 14px;
            color: #51555B;
            line-height: 14px;
            width: 52%;
          }
          .buttons{
            width: 80px;
            height: 32px;
            border: 1px solid #96999C;
            border-radius: 4px;
            font-size: 12px;
            color: #393E45;
            text-align: center;
            line-height: 32px;
            cursor: pointer;
            margin: -10px 0 0 6%;
          }
          .buttons:hover{
            color:#FFFFFF;
            background-color: #0077FF;
            border:none;
          }
        }
        .main-content{
          display: flex;
          margin-top: 30px;
          img{
            width: 80px;
            height: 80px;
          }
          ul{
            padding: 0;
            display: flex;
            flex-flow: wrap;
            margin-left: 20px;
            height: 80px;
            overflow: hidden;
            li{
              width: 48%;
              list-style-type: none;
              font-size: 12px;
              color: #51555B;
              line-height: 12px;
              margin-bottom: 20px;
              overflow:hidden;
              white-space: nowrap;
              text-overflow: ellipsis;
              .yellow{
                color: #FF9C00
              }
            }
          }
        }
      }
    }
  }
}
</style>
