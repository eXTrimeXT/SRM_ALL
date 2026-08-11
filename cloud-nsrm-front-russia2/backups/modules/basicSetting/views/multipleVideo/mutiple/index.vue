<template>
  <div class="wrapper">
    <div class="content">
      <!--画面div-->
      <div
        ref="large"
        class="main-window"
      />
      <div class="sub-window-wrapper">
        <!--小画面div-->
        <template v-if="remoteStreams.length">
          <div
            v-for="item in remoteStreams"
            :key="item.getId()"
          >
            <div
              ref="small"
              class="sub-window"
              :data-uid="item.getId()"
            >
            <!-- <span style="color:white">{{item.streamID}}</span> -->
            </div>
          </div>
        </template>
        <div
          v-else
          ref="small"
          class="sub-window"
        >
          <span class="loading-text">等待对方加入…</span>
        </div>
      </div>
    </div>
    <!--底层栏-->
    <ul class="tab-bar">
      <li
        :class="{ silence: true, isSilence }"
        @click="setOrRelieveSilence"
      />
      <li
        class="over"
        @click="handleOver"
      />
      <li
        :class="{ stop: true, isStop }"
        @click="stopOrOpenVideo"
      />
      <template v-if="userTypeShow==='BUYER'">
        <li
          v-if="!flag"
          style="display: flex;align-items: center;"
          :class="{'el-icon-video-play':true,openColor:!flag}"
          @click="record(true)"
        >
          开启录制
        </li>
        <li
          v-if="flag"
          style="display: flex;align-items: center;"
          :class="{'el-icon-circle-close':true,closeColor:flag}"
          @click="record(false)"
        >
          关闭录制
        </li>
      </template>
    </ul>
  </div>
</template>
<script>
import config from './config.js'
import { getToken, record } from './tools.js'
import * as WebRTC2 from './NIM_Web_WebRTC2_v4.0.1.js'

export default {
  name: 'Multiple',
  props: ['channelName'],
  data () {
    return {
      isSilence: false,
      isStop: false,
      client: null,
      localUid: Math.ceil(Math.random() * 1e5),
      localStream: null,
      remoteStreams: [],
      max: 20,
      userTypeShow: false,
      flag: false
    }
  },
  mounted () {
    if (this.$store.getters.userId) {
      this.localUid = Number(String(this.$store.getters.userId).substr(4, 12))
    }
    console.log('[localUid]', this.localUid)
    console.log('[WebRTC2]', WebRTC2)
    console.log('$store', this.$store)
    this.userTypeShow = this.$store.getters.userType || ''
    this.routeParams = this.$route.params || {}
    // 初始化音视频实例
    console.warn('初始化音视频sdk')
    window.self = this
    this.client = WebRTC2.createClient({
      appkey: config.appkey,
      debug: true
    })
    // 监听事件
    this.client.on('peer-online', evt => {
      console.warn(`${evt.uid} 加入房间`)
    })

    this.client.on('peer-leave', evt => {
      console.warn(`${evt.uid} 离开房间`)
      this.remoteStreams = this.remoteStreams.filter(
        item => !!item.getId() && item.getId() !== evt.uid
      )
    })

    this.client.on('stream-added', async evt => {
      const stream = evt.stream
      const userId = stream.getId()

      if (this.remoteStreams.some(item => item.getId() === userId)) {
        console.warn('收到已订阅的远端发布，需要更新', stream)
        this.remoteStreams = this.remoteStreams.map(item =>
          item.getId() === userId ? stream : item
        )
        await this.subscribe(stream)
      } else if (this.remoteStreams.length < this.max - 1) {
        console.warn('收到新的远端发布消息', stream)
        this.remoteStreams = this.remoteStreams.concat(stream)
        await this.subscribe(stream)
      } else {
        console.warn('房间人数已满')
      }
      // if(this.remoteStreams && this.remoteStreams.length>0){
      //   let streamIDArray = this.remoteStreams.map(v=>v.streamID)
      //   this.$http({
      //     url: "/api-rbac/internal/user/getByUserIds",
      //     method: "POST",
      //     data: streamIDArray
      //   }).then(res=>{
      //     console.warn("res",res)
      //   })
      // }

      console.log('remoteStreams', this.remoteStreams)
      console.log('userId', this.userId)
    })

    this.client.on('stream-removed', evt => {
      const stream = evt.stream
      const userId = stream.getId()
      stream.stop()
      this.remoteStreams = this.remoteStreams.map(item =>
        item.getId() === userId ? stream : item
      )
      console.warn('远端流停止订阅，需要更新', userId, stream)
    })

    this.client.on('stream-subscribed', evt => {
      console.warn('收到了对端的流，准备播放')
      const remoteStream = evt.stream
      // 用于播放对方视频画面的div节点
      const div = [...this.$refs.small].find(
        item => Number(item.dataset.uid) === Number(remoteStream.getId())
      )
      remoteStream
        .play(div)
        .then(() => {
          console.warn('播放视频')
          remoteStream.setRemoteRenderMode({
            // 设置视频窗口大小
            width: 160,
            height: 90,
            cut: false // 是否裁剪
          })
        })
        .catch(err => {
          console.warn('播放对方视频失败了: ', err)
        })
    })

    this.getToken()
      .then(token => {
        this.joinChannel(token)
      })
      .catch(e => {
        this.$message.info(e)
        console.error(e)
      })
  },
  destroyed () {
    try {
      const { cid } = this.client.getChannelInfo()
      let bidingAnswerVO = this.routeParams || {}
        bidingAnswerVO.channelId = cid
        this.$http({
          url: '/api-bid/bidingAnswer/modifyChannelId',
          method: 'POST',
          data: bidingAnswerVO
        })
      this.localStream.destroy()
      WebRTC2.destroy()
    } catch (e) {
      // 为了兼容低版本，用try catch包裹一下
    }
  },
  methods: {
    async record (flag) {
      this.flag = flag
      const { cid, uid } = this.client.getChannelInfo()
      console.log('[record cid]', cid)
      console.log('[record uid]', uid)
      const res = await this.$http({
        url: '/api-bid/bidingAnswer/recordVideo',
        method: 'POST',
        data: {
          cid,
          a_record: flag,
          v_record: flag,
          type: 0,
          uid
        }
      })
      if (!flag) {
        let bidingAnswerVO = this.$route.params || {}
          bidingAnswerVO.channelId = cid
          this.$http({
            url: '/api-bid/bidingAnswer/modifyChannelId',
            method: 'POST',
            data: bidingAnswerVO
          })
        this.$emit('getChannelInfoCid', cid)
      }
      console.log('[record res]', res)
      // record({
      //   appkey: config.appkey,
      //   appSecret: config.appSecret,
      //   channelName: this.channelName
      // });
    },
    getToken () {
      return getToken({
        uid: this.localUid,
        appkey: config.appkey,
        appSecret: config.appSecret,
        channelName: this.channelName
      }).then(
        token => {
          return token
        },
        e => {
          throw e
        }
      )
    },
    returnJoin (time = 2000) {
      this.$emit('returnJoin', time)
    },
    joinChannel (token) {
      if (!this.client) {
        this.$message.info('内部错误，请重新加入房间')
        return
      }

      console.info('开始加入房间: ', this.channelName)

      this.client
        .join({
          channelName: this.channelName,
          uid: this.localUid,
          token
        })
        .then(data => {
          console.info('加入房间成功，开始初始化本地音视频流')
          this.initLocalStream()
        })
        .catch(error => {
          console.error('加入房间失败：', error)
          this.$message.info(`${error}: 请检查appkey或者token是否正确`)
          this.returnJoin()
        })
    },
    initLocalStream () {
      // 初始化本地的Stream实例，用于管理本端的音视频流

      this.localStream = WebRTC2.createStream({
        uid: this.localUid,
        audio: true, // 是否启动mic
        video: true, // 是否启动camera
        screen: false // 是否启动屏幕共享
      })
      // 设置本地视频质量
      this.localStream.setVideoProfile({
        resolution: WebRTC2.VIDEO_QUALITY_720p, // 设置视频分辨率
        frameRate: WebRTC2.CHAT_VIDEO_FRAME_RATE_15 // 设置视频帧率
      })
      // 设置本地音频质量
      this.localStream.setAudioProfile('speech_low_quality')
      // 启动媒体，打开实例对象中设置的媒体设备
      this.localStream
        .init()
        .then(() => {
          console.warn('音视频开启完成，可以播放了')
          const div = self.$refs.large
          this.localStream.play(div)
          this.localStream.setLocalRenderMode({
            // 设置视频窗口大小
            width: div.clientWidth,
            height: div.clientHeight,
            cut: true // 是否裁剪
          })
          // 发布
          this.publish()
        })
        .catch(err => {
          console.warn('音视频初始化失败: ', err)
          this.$message.info('音视频初始化失败')
          this.localStream = null
        })
    },
    publish () {
      console.warn('开始发布视频流')
      // 发布本地媒体给房间对端
      this.client
        .publish(this.localStream)
        .then(() => {
          console.warn('本地 publish 成功')
        })
        .catch(err => {
          console.error('本地 publish 失败: ', err)
          this.$message.info('本地 publish 失败')
        })
    },
    subscribe (remoteStream) {
      remoteStream.setSubscribeConfig({
        audio: true,
        video: true
      })
      this.client
        .subscribe(remoteStream)
        .then(() => {
          console.warn('本地 subscribe 成功')
        })
        .catch(err => {
          console.warn('本地 subscribe 失败: ', err)
          this.$message.info('订阅对方的流失败')
        })
    },
    setOrRelieveSilence () {
      const { isSilence } = this
      this.isSilence = !isSilence
      if (this.isSilence) {
        console.warn('关闭mic')
        this.localStream
          .close({
            type: 'audio'
          })
          .then(() => {
            console.warn('关闭 mic sucess')
          })
          .catch(err => {
            console.warn('关闭 mic 失败: ', err)
            this.$message.info('关闭 mic 失败')
          })
      } else {
        console.warn('打开mic')
        if (!this.localStream) {
          this.$message.info('当前不能打开mic')
          return
        }
        this.localStream
          .open({
            type: 'audio'
          })
          .then(() => {
            console.warn('打开mic sucess')
          })
          .catch(err => {
            console.warn('打开mic失败: ', err)
            this.$message.info('打开mic失败')
          })
      }
    },
    stopOrOpenVideo () {
      const { isStop } = this
      this.isStop = !isStop
      if (this.isStop) {
        console.warn('关闭摄像头')
        this.localStream
          .close({
            type: 'video'
          })
          .then(() => {
            console.warn('关闭摄像头 sucess')
          })
          .catch(err => {
            console.warn('关闭摄像头失败: ', err)
            this.$message.info('关闭摄像头失败')
          })
      } else {
        console.warn('打开摄像头')
        if (!this.localStream) {
          this.$message.info('当前不能打开camera')
          return
        }
        this.localStream
          .open({
            type: 'video'
          })
          .then(() => {
            console.warn('打开摄像头 sucess')
            const div = self.$refs.large
            this.localStream.play(div)
            this.localStream.setLocalRenderMode({
              // 设置视频窗口大小
              width: div.clientWidth,
              height: div.clientHeight,
              cut: true // 是否裁剪
            })
          })
          .catch(err => {
            console.warn('打开摄像头失败: ', err)
            this.$message.info('打开摄像头失败')
          })
      }
    },

    handleOver () {
      console.warn('离开房间')
      const { cid } = this.client.getChannelInfo()
      let bidingAnswerVO = this.$route.params || {}
        bidingAnswerVO.channelId = cid
        this.$http({
          url: '/api-bid/bidingAnswer/modifyChannelId',
          method: 'POST',
          data: bidingAnswerVO
        })
      this.$emit('getChannelInfoCid', cid)

      this.client.leave()
      this.returnJoin(1)
    }
  }
}
</script>

<style scoped lang="scss">
.wrapper {
  height: 86vh;
  background-image: linear-gradient(179deg, #141417 0%, #181824 100%);
  display: flex;
  flex-direction: column;

  .content {
    flex: 1;
    display: flex;
    position: relative;

    .main-window {
      height: 100%;
      width: 67vh;
      //width: 37vw;
      //width: 427px;
      margin: 0 auto;
      background: #25252d;
    }

    .sub-window-wrapper {
      overflow:auto;
      position: absolute;
      top: 16px;
      right: 16px;
      z-index: 9;
      width: 165px;
      height: 70vh;
    }

    .sub-window {
      background: #25252d;
      border: 1px solid #ffffff;
      margin-bottom: 20px;

      .loading-text {
        display: block;
        width: 100%;
        text-align: center;
        line-height: 90px;
        font-size: 12px;
        color: #fff;
        font-weight: 400;
      }
    }
  }

  .tab-bar {
    height: 54px;
    background-image: linear-gradient(180deg, #292933 7%, #212129 100%);
    box-shadow: 0 0 0 0 rgba(255, 255, 255, 0.3);
    list-style: none;
    display: flex;
    justify-content: center;
    align-items: center;
    color: #fff;

    li {
      height: 54px;
      width: 125px;
      cursor: pointer;
      //静音
      &.silence {
        background: url("../../../../../assets/img/icon/silence.png") no-repeat
          center;
        background-size: 60px 54px;

        &:hover {
          background: url("../../../../../assets/img/icon/silence-hover.png")
            no-repeat center;
          background-size: 60px 54px;
        }

        &:active {
          background: url("../../../../../assets/img/icon/silence-click.png")
            no-repeat center;
          background-size: 60px 54px;
        }

        &.isSilence {
          //已经开启静音
          background: url("../../../../../assets/img/icon/relieve-silence.png")
            no-repeat center;
          background-size: 60px 54px;

          &:hover {
            background: url("../../../../../assets/img/icon/relieve-silence-hover.png")
              no-repeat center;
            background-size: 60px 54px;
          }

          &:active {
            background: url("../../../../../assets/img/icon/relieve-silence-click.png")
              no-repeat center;
            background-size: 60px 54px;
          }
        }
      }

      //结束按钮
      &.over {
        background: url("../../../../../assets/img/icon/over.png") no-repeat
          center;
        background-size: 68px 36px;

        &:hover {
          background: url("../../../../../assets/img/icon/over-hover.png")
            no-repeat center;
          background-size: 68px 36px;
        }

        &:active {
          background: url("../../../../../assets/img/icon/over-click.png")
            no-repeat center;
          background-size: 68px 36px;
        }
      }

      // 停止按钮
      &.stop {
        background: url("../../../../../assets/img/icon/stop.png") no-repeat
          center;
        background-size: 60px 54px;

        &:hover {
          background: url("../../../../../assets/img/icon/stop-hover.png")
            no-repeat center;
          background-size: 60px 54px;
        }

        &:active {
          background: url("../../../../../assets/img/icon/stop-click.png")
            no-repeat center;
          background-size: 60px 54px;
        }

        //已经是停止状态
        &.isStop {
          background: url("../../../../../assets/img/icon/open.png") no-repeat
            center;
          background-size: 60px 54px;

          &:hover {
            background: url("../../../../../assets/img/icon/open-hover.png")
              no-repeat center;
            background-size: 60px 54px;
          }

          &:active {
            background: url("../../../../../assets/img/icon/open-click.png")
              no-repeat center;
            background-size: 60px 54px;
          }
        }
      }

      &.openColor{
        color:aqua
      }

      &.closeColor{
        color:red
      }
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
