<template>
  <div>
    <div
      v-if="!videoVisible"
      class="join"
    >
      <div class="content">
        <img
          src="../../../../assets/img/logo.png"
          alt=""
          class="logo"
        >
        <input
          v-model.trim="channelName"
          type="text"
          placeholder="请输入房间号"
        >
        <button
          :disabled="!isSupport"
          class="submit-btn"
          @click="handleSubmit"
        >
          加入房间
        </button>
        <button
          style="margin-top: 20px;"
          class="submit-btn"
          @click="downloadRecord"
        >
          视频会议下载
        </button>

        <div
          v-show="!isSupport"
          class="errorMsg"
        >
          当前浏览器不支持体验，建议下载安装最新chrome浏览器
        </div>
      </div>
    </div>
    <mutiple
      v-else
      ref="mutiple"
      :channel-name="channelName"
      @returnJoin="returnJoin"
      @getChannelInfoCid="getChannelInfoCid"
    />
    <srm-dialog
      title="查看视频"
      size="middle"
      :visible.sync="dialogFormVisible"
      :close-on-click-modal="false"
    >
      <el-table
        :data="fileInfos"
        style="width: 100%"
      >
        <el-table-column
          prop="createTime"
          label="日期"
          width="180"
          :formatter="getFormatTimeToDate"
        />
        <el-table-column
          prop="objectName"
          label="文件名称"
        />
        <el-table-column
          prop="operation"
          label="操作"
          width="140"
        >
          <template slot-scope="scope">
            <el-button
              type="primary"
              @click="viewVideo(scope.row)"
            >
              查看视频
            </el-button>
            <el-button
              type="primary"
              @click="downloadVideo(scope.row)"
            >
              下载视频
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </srm-dialog>
  </div>
</template>

<script>
import {
  checkBrowser,
  rooms,
  queryMediaFileByChannelId
} from './mutiple/tools'
import Mutiple from './mutiple'
import config from './mutiple/config.js'
import { formatTimeToDate } from '@/utils'
export default {
  name: 'Join',
  components: { Mutiple },
  data () {
    return {
      channelName: '',
      isSupport: true,
      videoVisible: false,
      channelId: '',
      fileInfos: [],
      dialogFormVisible: false
    }
  },
  mounted () {
    if (!checkBrowser('chrome') && !checkBrowser('safari')) {
      this.isSupport = false
    } else {
      if (this.$route.params && this.$route.params.answerNum) {
        let answerNum = this.$route.params.answerNum
        answerNum = answerNum.replace(/[^0-9]/ig, '')
        this.channelName = answerNum
        this.handleSubmit()
      }
    }
  },
  methods: {
    downloadRecord () {
      // this.$prompt("请输入要下载的视频会议房间号ID", "视频会议下载", {
      //   confirmButtonText: this.$t("common.confirm"),
      //   cancelButtonText: this.$t("common.cancel")
      // })
      //   .then(async ({ value }) => {
      //     const res = await queryMediaFileByChannelId({
      //       appkey: config.appkey,
      //       appSecret: config.appSecret,
      //       channelId: value
      //     });
      //     console.log("[downloadRecord res]", res);
      //   })
      //   .catch(() => {});
      if (this.channelId) {
        queryMediaFileByChannelId({
            appkey: config.appkey,
            appSecret: config.appSecret,
            channelId: this.channelId || ''
          }).then(res => {
            if (res.fileInfos && res.fileInfos.length > 0) {
              this.fileInfos = res.fileInfos
              this.dialogFormVisible = true
            } else {
              this.$message.warning(this.$t('bidMod.notLoaded'))
            }
          })
      }
    },
    getFormatTimeToDate (row, column) {
      if (row.createTime) {
        // return new Date(parseInt(row.createTime) * 1000).toLocaleString().replace(/:\d{1,2}$/,' ')
        return formatTimeToDate(row.createTime, 'Y-M-D h:m:s')
      }
    },
    returnJoin () {
      this.videoVisible = false
    },
    getChannelInfoCid (value) {
      if (value) {
        this.channelId = value
      }
    },
    handleSubmit () {
      const { channelName } = this
      if (!channelName) {
        this.$message.info('请输入房间号')
        return
      } else if (!/^[0-9]{1,12}$/.test(channelName)) {
        this.$message.info('房间号为12位以内的数字')
        return
      }
      this.videoVisible = true
    },
    viewVideo (row) {
      if (row && row.url) {
        window.open(row.url)
      }
    },
    downloadVideo (row) {
      if (row && row.url) {
        var ele = document.createElement('a')// 创建下载链接
        ele.download = row.objectName// 设置下载的名称
        ele.style.display = 'none'// 隐藏的可下载链接
        // 字符内容转变成blob地址
        var blob = new Blob([row.url])
        ele.href = URL.createObjectURL(blob)
        // 绑定点击时间
        document.body.appendChild(ele)
        ele.click()
        // 然后移除
        document.body.removeChild(ele)
      }
    }
  }
}
</script>

<style scoped lang="scss">
.join {
  height: 100vh;
  background: #f7f8fa;
  display: flex;
  align-items: center;
  justify-content: center;
  .content {
    width: 400px;
    height: 400px;
    padding-top: 60px;
    background: #fff;
    box-shadow: 0 4px 10px 0 rgba(47, 56, 111, 0.1);
    border-radius: 8px;
    .logo {
      display: block;
      height: 55px;
      margin: 0 auto;
    }

    input {
      display: block;
      width: 315px;
      height: 44px;
      margin: 50px auto 40px;
      border: none;
      outline: medium;
      border-bottom: 1px solid #dcdfe5;
      font-size: 17px;

      &::placeholder {
        color: #b0b6be;
      }
    }

    .submit-btn {
      display: block;
      width: 315px;
      height: 50px;
      margin: 0 auto;
      border: none;
      outline: medium;
      background: #337eff;
      border-radius: 25px;
      font-size: 16px;
      color: #ffffff;
      cursor: pointer;
      &:active {
        background: darken(#337eff, 5%);
      }
      &:disabled {
        background: #dddddd;
        cursor: not-allowed;
      }
    }

    .errorMsg {
      font-size: 14px;
      text-align: center;
      color: red;
      margin-top: 10px;
    }
  }
}

.mb20 {
  margin-bottom: 20px;
}
.mr10 {
  margin-right: 10px;
}
.pl20 {
  padding-left: 20px;
}
.pr20 {
  padding-right: 20px;
}
.t-center {
  text-align: center;
}
.flex {
  display: flex;
}
.jcb {
  justify-content: space-between;
}
.f1 {
  flex: 1;
}
</style>
