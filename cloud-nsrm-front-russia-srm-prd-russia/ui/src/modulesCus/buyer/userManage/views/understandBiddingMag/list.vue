<template>
  <el-container style="height: 100%;margin:0; padding:0;">
    <div class="left-container">
      <div class="left-container__title">
        公告查看
      </div>
      <div class="left-container__list">
        <div
          v-for="(item, index) in list"
          :key="item.noticeId"
          :class="['list-item', item.actived ? 'list-item__actived' : '']"
          @click="showDetail(item, index)"
        >
          <div class="list-item__title">
            {{ item.title }}
          </div>
          <div class="list-item__date">
            {{ item.publishTime }}
          </div>
        </div>
      </div>
      <div class="notice-pagination">
        <div class="notice-pagination__left">
          共{{ total }}条
        </div>
        <div class="notice-pagination__right">
          <el-button type="text" icon="el-icon-arrow-left" :disabled="pageNum === 1" @click="currentChange(-1)" />
          <input
            v-model="pageNum"
            type="number"
            class="current-page"
            @input="pageNumInput"
            @keydown="clearInput"
            @change="pageNumChange"
          >
          <span style="margin:0 8px 0 5px">/{{ pageTotal }}</span>
          <el-button type="text" icon="el-icon-arrow-right" :disabled="pageNum === pageTotal" @click="currentChange(1)" />
        </div>
      </div>
    </div>
    <el-container>
      <div v-if="bidingInfo && bidingInfo.bidingId" class="biding-block">
        <div class="biding-block__title">
          {{ bidingInfo.bidingName }}
        </div>
        <div class="biding-block__content" style="margin-bottom:8px;">
          <span class="biding-block__content-title">{{ $t('announcements.title1') }}</span>
          <span class="biding-block__content-text">{{ bidingInfo.bidingName }}</span>
        </div>
        <div class="biding-block__content">
          <span class="biding-block__content-title">{{ $t('announcements.title2') }}</span>
          <span class="biding-block__content-text">{{ bidingInfo.bidingNum }}</span>
        </div>
        <el-button style="width:95px;height:32px;margin-top: 48px;" type="primary" @click="signUp">
          {{ $t('announcements.title3') }}
        </el-button>
      </div>

      <div v-else class="doc-editor" v-html="html" />
      <el-footer style="padding: 0 16px;">
        <div v-if="fileRelationId" class="file-wrapper">
          <div class="file-wrapper__title">
            {{ $t('dataConfMod.attachment') }}
          </div>
          <SrmCommonFile
            :default-file="{
              fileId: fileRelationId,
              fileName: fileName
            }"
            :readonly="true"
          />
        </div>
      </el-footer>
    </el-container>
  </el-container>
</template>
<script>
import { noticeApi } from 'modc@/buyer/userManage/api'
export default {
  name: 'CheckUnderstandBidding',
  components: {},
  data () {
    return {
      list: [],
      pageSize: 15,
      pageNum: 1,
      html: null,
      total: null,
      activeItem: null,
      fileName: null,
      fileRelationId: null,
      bidingInfo: null
    }
  },
  computed: {
    showEmpty () {
      return this.list.length === 0
    },
    pageTotal () {
      return Math.ceil(this.total / this.pageSize)
    }
  },
  mounted () {
    const id = this.$route.params.id
    this.queryList(id)
  },
  activated () {
    const id = this.$route.params.id
    this.queryList(id)
  },
  methods: {
    clearInput (e) {
      let key = e.key
      if (key === 'e' || key === 'E' || key === '.' || key === '-' || key === '+') {
        e.returnValue = false
        return false
      }
      return true
    },
    pageNumInput (e) {
      if (e.target.value > this.pageTotal) {
        this.pageNum = this.pageTotal
      } else if (e.target.value === 0) {
        this.pageNum = 1
      }
    },
    pageNumChange () {
      if (this.pageNum) {
        this.queryList()
      }
    },
    signUp () {
      // 报名/投标
      this.$http({
        url: `/api-bid/supplierCooperate/orderHead/queryBiding/${this.bidingInfo.bidingId}`,
        method: 'GET',
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.toSignUp(res.data)
        } else {
          this.$message.info('招标单据当前状态不支持报名或投标')
        }
      })
    },
    toSignUp (data) {
      if (
        data.bidingStatus === 'ACCEPT_SIGNUP' &&
        ['NO_SIGNUP', 'REJECTED'].includes(data.signUpStatus) &&
        new Date(data.enrollEndDatetime) > new Date()
      ) {
        this.$router.push({
          path: '/vendorBiddingManagement/vendorBiddingSignUp',
          query: {
            bidingId: data.bidingId,
            bidingNum: data.bidingNum,
            bidingName: data.bidingName,
            enrollEndDatetime: data.enrollEndDatetime
          }
        })
      } else if (
        data.bidingStatus === 'ACCEPT_BID' &&
        ['DRAFT', 'WITHDRAW'].includes(data.orderStatus) &&
        data.canOrder === 'Y'
      ) {
        this.$router.push({
          path: '/vendorBiddingManagement/doBidingDetail',
          query: {
            bidingId: data.bidingId,
            bidingNum: data.bidingNum
          }
        })
      } else {
        this.$message.info('招标单据当前状态不支持报名或投标')
      }
    },
    queryList (id) {
      noticeApi.noticeList({
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        noticeStatus: 'PUBLISHED'
      })
        .then(res => {
          if (id) {
            let targetIndex = 0
            this.list = res.data.list.map((item, index) => {
              if (id === item.noticeId) targetIndex = index
              return { ...item, actived: id === item.noticeId }
            })
            this.showDetail(res.data.list[targetIndex], targetIndex)
          } else {
            this.list = res.data.list.map((item, index) => ({
              ...item,
              actived: index === 0
            }))
            this.showDetail(res.data.list[0], 0)
          }
          this.total = res.data.total
        })
    },
    showDetail (item, index) {
      if (!item) return
      noticeApi.getNoticeInfo({ noticeId: item.noticeId }).then(res => {
        if (res.data && res.data.detail) {
          if (res.data.noticeSource === 'BIDING') {
            let detailChange = res.data.detail
            detailChange = detailChange.replace('\n', '')
            detailChange = detailChange.replace('<p>', '')
            detailChange = detailChange.replace('</p>', '')
            let detail = JSON.parse(detailChange)
            this.bidingInfo = detail
            this.html = null
          } else {
            this.html = res.data.detail
            this.bidingInfo = null
          }
        }
      })
      const prevIndex = this.list.findIndex(i => i.actived)
      const prevItem = this.list[prevIndex]
      const nextItem = this.list[index]
      this.fileRelationId = nextItem.fileRelationId
      this.fileName = nextItem.fileName
      this.list.splice(prevIndex, 1, { ...prevItem, actived: false })
      this.list.splice(index, 1, { ...nextItem, actived: true })
    },
    currentChange (num) {
      this.pageNum = +this.pageNum + num
      this.queryList()
    }
  }
}
</script>
<style lang="scss" scoped>
.left-container {
  width: 230px;
  padding-right: 3px;
  border-right: 1px solid #E8E9EA;
  background: #fff;

  &__title {
    height: 46px;
    margin: 0 16px;
    line-height: 46px;
    font-size: 14px;
    color: #161C24;
    font-weight: 600;
    border-bottom: 1px solid #E8E9EA;
  }

  &__list {
    width: 100%;
    height: calc(100vh - 166px);
    padding-left: 8px;
    overflow-y: auto;

  }

  &__list::-webkit-scrollbar-thumb {
    width: 6px;
    background: #C5C6C8;
  }

  .list-item {
    height: 64px;
    padding: 12px 8px;
    cursor: pointer;

    &:hover {
      background: #E7F2FF;
      border-radius: 4px;

      .list-item__title {
        color: #0077FF;
      }

      .list-item__date {
        color: #0077FF;
      }
    }
  }

  .list-item__actived {
    border-radius: 4px;
    background: #E7F2FF;
  }

  .list-item__title {
    font-size: 12px;
    color: #161C24;
    line-height: 20px;
    font-weight: 600;

    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    word-break: break-all;
  }

  .list-item__date {
    font-size: 12px;
    color: #96999C;
    line-height: 20px;
  }

  .list-item__actived {
    background: #E7F2FF;

    .list-item__title {
      color: #0077FF;
    }

    .list-item__date {
      color: #0077FF;
    }

  }

  &__pagination {
    width: 100%;
  }
}

.biding-block {
  text-align: center;
  padding-top: 16px;

  &__title {
    margin-bottom: 48px;
    font-size: 18px;
    color: #161C24;
    line-height: 26px;
    font-weight: 500;
  }

  &__content {
    &-title {
      font-size: 14px;
      color: #96999C;
      line-height: 22px;
    }

    &-text {
      font-size: 14px;
      color: #393E45;
      line-height: 22px;
    }
  }
}
.doc-editor {
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  line-height: 1.42;
  padding: 16px;
  outline: none;
  overflow-y: auto;
  padding: 12px 15px;
  -o-tab-size: 4;
  tab-size: 4;
  -moz-tab-size: 4;
  text-align: left;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.file-wrapper {
  display: flex;
  justify-items: center;

  .file-wrapper__title {
    width: 35px;
    font-size: 12px;
    color: #161C24;
    line-height: 28px;
    font-weight: 500;
  }
}

</style>
<style lang="scss">
.notice-pagination {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 56px;
  padding: 0 16px;

  &__left {
    color: #161C24;
  }

  &__right {
    display: flex;
    align-items: center;

    .el-icon-arrow-left,
    .el-icon-arrow-right {
      color: #51555B;
    }

    .is-disabled {

      .el-icon-arrow-left,
      .el-icon-arrow-right {
        color: #DCDDDE;
      }
    }

    input::-webkit-outer-spin-button,
    input::-webkit-inner-spin-button {
      -webkit-appearance: none;
    }

    input[type='number'] {
      -moz-appearance: textfield;
    }

    .current-page {
      height: 24px;
      width: 24px;
      margin-left: 8px;
      border: 1px solid #B9BABD;
      border-radius: 4px;
      outline: none;
    }
  }

}
</style>
