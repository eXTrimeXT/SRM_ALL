<template>
  <div class="regFormInputFace">
    <el-form
      ref="verifyFaceForm"
      :model="userSecurity"
      :rules="rules"
      status-icon
      class="form"
      label-width="80px"
    >
      <el-row>
        <el-col
          :span="5"
          class="el-col"
        >
          <el-button
            type="primary"
            :loading="loading"
            @click="submitForm"
          >
            {{ $t('common.save') }}
          </el-button>
        </el-col>
        <el-col
          :span="5"
          class="el-col"
        >
          <el-button
            @click="cancle"
          >
            {{ $t('common.cancel') }}
          </el-button>
        </el-col>
      </el-row>
    </el-form>
    <video
      ref="videoVerifyFace"
      width="560"
      height="420"
      autoplay="autoplay"
      class="el-video"
    >{{ $t('announcements.video') }}</video>
    <canvas
      id="canvasVerifyFace"
      ref="canvasVerifyFace"
      width="560"
      height="420"
    />
  </div>
</template>

<script>
import http from '@/utils/axios/http'

export default {
  name: 'RecordFaceDialog',
  components: {}, // Message
  data () {
    return {
      loading: false,
      userSecurity: {
        username: '',
        faceFileBase64: ''
      },
      rules: {
      }
    }
  },
  mounted () {
    // 初始化摄像头
        let _this = this
        let video = this.$refs.videoVerifyFace

        let constraints = {
            // video: {width: 500, height: 500},
            video: true,
            audio: false
        }
        /*
        这里介绍新的方法:H5新媒体接口 navigator.mediaDevices.getUserMedia()
        这个方法会提示用户是否允许媒体输入,(媒体输入主要包括相机,视频采集设备,屏幕共享服务,麦克风,A/D转换器等)
        返回的是一个Promise对象。
        如果用户同意使用权限,则会将 MediaStream 对象作为resolve()的参数传给then()
        如果用户拒绝使用权限,或者请求的媒体资源不可用,则会将 PermissionDeniedError 作为 reject()的参数传给catch()
        */
        let promise = navigator.mediaDevices.getUserMedia(constraints)
        promise.then(function (MediaStream) {
			    _this.mediaStreamTrack = typeof MediaStream.stop === 'function' ? MediaStream : MediaStream.getTracks()[0]
            video.srcObject = MediaStream
            video.play()
        }).catch(function (PermissionDeniedError) {
            console.log(PermissionDeniedError)
        })
  },
  methods: {
    initCamDEL () {
        let _this = this
        let video = this.$refs.videoVerifyFace

        let constraints = {
            video: { width: 500, height: 500 },
            audio: false
        }
        /*
        这里介绍新的方法:H5新媒体接口 navigator.mediaDevices.getUserMedia()
        这个方法会提示用户是否允许媒体输入,(媒体输入主要包括相机,视频采集设备,屏幕共享服务,麦克风,A/D转换器等)
        返回的是一个Promise对象。
        如果用户同意使用权限,则会将 MediaStream 对象作为resolve()的参数传给then()
        如果用户拒绝使用权限,或者请求的媒体资源不可用,则会将 PermissionDeniedError 作为 reject()的参数传给catch()
        */
        let promise = navigator.mediaDevices.getUserMedia(constraints)
        promise.then(function (MediaStream) {
			    _this.mediaStreamTrack = typeof MediaStream.stop === 'function' ? MediaStream : MediaStream.getTracks()[0]
            video.srcObject = MediaStream
            video.play()
        }).catch(function (PermissionDeniedError) {
            console.log(PermissionDeniedError)
        })
    },
    initCam01DEL () {
    let _this = this
    // 初始化摄像头
    // let video = document.getElementById("videoVerifyFace");
    let video = this.$refs.videoVerifyFace

    // 如果已经打开过的，直接判断打开
    if (this.mediaStreamTrack) {
      this.mediaStreamTrack && this.mediaStreamTrack.play()
      return
    }

    // 老的浏览器可能根本没有实现 mediaDevices，所以我们可以先设置一个空的对象
    if (navigator.mediaDevices === undefined) {
        navigator.mediaDevices = {}
    }
    // 一些浏览器部分支持 mediaDevices。我们不能直接给对象设置 getUserMedia
    // 因为这样可能会覆盖已有的属性。这里我们只会在没有getUserMedia属性的时候添加它。
    if (navigator.mediaDevices.getUserMedia === undefined) {
        navigator.mediaDevices.getUserMedia = function (constraints) {
            // 首先，如果有getUserMedia的话，就获得它
            var getUserMedia = navigator.webkitGetUserMedia || navigator.mozGetUserMedia
            // 一些浏览器根本没实现它 - 那么就返回一个error到promise的reject来保持一个统一的接口
            if (!getUserMedia) {
                return Promise.reject(new Error('getUserMedia is not implemented in this browser'))
            }
            // 否则，为老的navigator.getUserMedia方法包裹一个Promise
            return new Promise(function (resolve, reject) {
                getUserMedia.call(navigator, constraints, resolve, reject)
            })
        }
    }

    // 默认使用前摄像头，强制使用后置摄像头如下设置
    // let constraints = {video: { facingMode: { exact: "environment" } }};
    let constraints = { video: true }
    navigator.mediaDevices.getUserMedia(constraints)
        .then(function (stream) {
          _this.mediaStreamTrack = typeof stream.stop === 'function' ? stream : stream.getTracks()[0]
            // 旧的浏览器可能没有srcObject
            if ('srcObject' in video) {
                video.srcObject = stream
            } else {
                // 防止在新的浏览器里使用它，应为它已经不再支持了
                video.src = window.URL.createObjectURL(stream)
            }
            video.onloadedmetadata = function (e) {
                video.play()
            }
        })
        .catch(function (err) {
            console.log(err.name + ': ' + err.message)
        })
    },

    closeCam () {
      this.mediaStreamTrack && this.mediaStreamTrack.stop()
    },
    cancle () {
      // 关闭摄像头
      this.closeCam()
      this.$emit('visible', false)
    },
    // 提交
    submitForm () {
      this.$refs.verifyFaceForm.validate(valid => {
        this.loading = true
        if (!valid) {
          this.loading = false
          return false
        }

        // 获取摄像头图片
        // 绘制画面
        // let canvas = document.getElementById("canvasVerifyFace");
        let canvas = this.$refs.canvasVerifyFace
        let context = canvas.getContext('2d')
        let video = this.$refs.videoVerifyFace
        context.drawImage(video, 0, 0, 560, 420)

		    // 从画布上获取照片数据
		    var imgData = canvas.toDataURL()
		    // 将图片转换为Base64
		    var base64Data = imgData.substr(22)
		    // console.log("base64Data:"+base64Data);
        this.userSecurity.faceFileBase64 = base64Data

        http({
          url: '/api-rbac/user-security/modifyFace',
          method: 'POST',
          data: {
            faceFileBase64: this.userSecurity.faceFileBase64
          }
        }).then(res => {
            if (res.success) {
              this.$message({ message: res.message, type: 'success' })
              this.$emit('visible', false)
            } else {
              this.$message({ message: res.message, type: 'error' })
            }
            this.loading = false
          }).catch(() => {
            this.loading = false
          })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.regFormInputFace {
  padding: 0 0 10px 0;
  .tip {
    display: flex;
    align-items: center;
    justify-content: center;
    padding-top: 10px;
    color: red;
  }
  .form {
    padding: 15px;
    width: 100%;
  }
}
</style>
<style>
.regFormInputFace .el-tabs__header {
  padding: 0 100px;
}
.regFormInputFace .el-col {
  padding-left: 20px;
}
.regFormInputFace .el-video {
  border-style: dotted;
}
</style>
