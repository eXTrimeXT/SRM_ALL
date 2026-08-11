<template>
  <div class="regFormInput-face">
    <el-form
      ref="verifyFaceForm"
      :model="userSecurity"
      :rules="rules"
      status-icon
      class="form"
      label-width="80px"
    >
      <el-form-item
        prop="username"
        :label="$t('vendorMod.account')"
      >
        <el-row>
          <el-col
            :span="8"
            class="el-col"
          >
            <el-input v-model="userSecurity.username" />
          </el-col>
          <el-col
            :span="5"
            class="el-col"
          >
            <el-button
              type="primary"
              :loading="loading"
              @click="submitForm"
            >
              {{ $t('vendorMod.login') }}
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
      </el-form-item>
    </el-form>
    <video
      ref="videoVerifyFace"
      width="560"
      height="420"
      autoplay="autoplay"
      class="el-video"
    >
      {{ $t('login.videoNotSupport') }}
    </video>
    <canvas
      id="canvasVerifyFace"
      ref="canvasVerifyFace"
      width="560"
      height="420"
    />
  </div>
</template>

<script>
import { randomLenNum } from 'lib@/utils/util'
import http from '@/utils/axios/http'
import { getToken, removeRedirectUrl, getRedirectUrl } from '@/utils/auth'

export default {
  name: 'VerifyFaceDialog',
  components: {}, // Message
  data () {
    // 账号校验
    var checkUsername = (rule, value, callback) => {
      if (!value) {
        return callback(new Error(this.$t('vendorMod.enterAccount')))
      } else {
        callback()
      }
    }
    return {
      loading: false,
      userSecurity: {
        username: '',
        faceFileBase64: ''
      },
      rules: {
        username: [{ required: true, validator: checkUsername }]
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
        const _this = this
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

        this.$store.dispatch('user/loginFace', this.userSecurity).then(res => {
            if (res.success) {
              // this.$message({message: res.message,type: "success"});
              // this.$emit("visible", false);
                // 获取用户信息
                _this.$store.dispatch('user/initSystem').then(
                  res02 => {
                    if (res02) {
                      let mainType = res02.data.mainType
                      let isConfirm = res02.data.isConfirm
                      let userType = res02.data.userType
                      if (
                        userType === 'BUYER' &&
                        mainType === 'Y' &&
                        (!isConfirm || isConfirm === 'N')
                      ) {
                        _this.tipDialogVisible = true
                      } else {
                        let redirectUrl = getRedirectUrl()
                        // 先去拿cookie的redirectUrl，有的话，就跳转这个地址，
                        // 同时删除cookie中的redirectUrl，没有就跳dashboard
                        if (redirectUrl) {
                          location.href = redirectUrl
                          removeRedirectUrl()
                        } else {
                          _this.$router.push({
                            path: _this.redirect || '/',
                            query: _this.otherQuery
                          })
                        }
                      }
                      _this.loading = false
                    }
                  },
                  err => {
                    console.log(err)
                  }
                )
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
.regFormInput-face {
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
.regFormInput-face .el-tabs__header {
  padding: 0 100px;
}
.regFormInput-face .el-col {
  padding-left: 20px;
}
.regFormInput-face .el-video {
  border-style: dotted;
}
</style>
