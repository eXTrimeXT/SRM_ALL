<template>
  <div class="slide-verify" :style="widthlable" onselectstart="return false;">
    <div v-loading="loading">
      <canvas ref="canvas" :width="w" :height="h" />
      <canvas ref="block" class="slide-verify-block" :width="w" :height="h" />
    </div>
    <div v-if="infoText" class="slide-verify-info">
      {{ infoText }}
    </div>
    <div
      class="slide-verify-slider"
      :style="widthlable"
      :class="{
        'container-active': containerActive,
        'container-success': containerSuccess,
        hidden: !imgurl,
      }"
    >
      <div class="slide-verify-slider-mask" :style="{ width: sliderMaskWidth }">
        <!-- slider -->
        <div
          class="slide-verify-slider-mask-item"
          :style="{ left: sliderLeft }"
          @mousedown="sliderDown"
          @touchstart="touchStartEvent"
          @touchmove="touchMoveEvent"
          @touchend="touchEndEvent"
        >
          <div class="slide-verify-slider-mask-item-icon" />
        </div>
      </div>
      <span class="slide-verify-slider-text">{{ sliderText }}</span>
    </div>
    <div class="bottom">
      <div style="cursor: pointer" @click="refresh">
        <span class="slide-verify-refresh-icon el-icon-refresh" />
        <span>{{ $t('base.tagsView.refresh') }}</span>
      </div>
    </div>
  </div>
</template>
<script>
import forgetPwdApi from './api'

export default {
  name: 'DragCode',
  props: {
    // 大图的宽
    w: {
      type: [Number, String],
      default: 300
    },
    // 大图的高
    h: {
      type: [Number, String],
      default: 150
    },
    // 小图宽
    sw: {
      type: [Number, String],
      default: 45
    },
    // 小图高
    sh: {
      type: [Number, String],
      default: 55
    },
    sliderText: {
      type: String,
      default () {
        return this.$t('login.sliderText')
      }
    },
    identifier: {
      type: String,
      default: ''
    }
  },

  data () {
    return {
      loading: false,
      miniimgurl: '',
      imgurl: '',
      blocky: '',
      containerActive: false,
      containerSuccess: false,
      canvasCtx: null,
      blockCtx: null,
      block: null,
      canvasStr: null,
      originX: undefined,
      isMouseDown: false,
      widthlable: '',
      sliderLeft: 0,
      sliderMaskWidth: 0,
      dialogVisible: false,
      infoText: '',
      fail: false,
      captchaId: '',
      answersForm: {
        x: ''
      }
    }
  },
  async mounted () {
    this.init()
    await this.getSliders()
    this.dragImg()
  },
  methods: {
    async getSliders () {
      this.loading = true
      let siders = await forgetPwdApi.getImgCode()
      this.loading = false
      let {
        captchaId,
        question: { backImage, slideImage, y }
      } = siders.data
      this.imgurl = 'data:image/png;base64,' + backImage
      this.miniimgurl = 'data:image/png;base64,' + slideImage
      this.blocky = y
      this.captchaId = captchaId
      console.log('captchaId', captchaId)
    },
    init () {
      this.initDom()
      this.bindEvents()
      this.widthlable = 'width:' + this.w + 'Px;'
    },
    initDom () {
      this.block = this.$refs.block
      this.canvasStr = this.$refs.canvas
      this.canvasCtx = this.canvasStr.getContext('2d')
      this.blockCtx = this.block.getContext('2d')
    },
    dragImg () {
      const img = document.createElement('img')
      img.src = this.imgurl
      img.onload = () => {
        this.canvasCtx.drawImage(img, 0, 0)
      }
      const miniimgurl = document.createElement('img')
      miniimgurl.src = this.miniimgurl
      miniimgurl.onload = () => {
        this.blockCtx.drawImage(miniimgurl, 0, this.blocky)
      }
    },
    // 刷新
    async refresh () {
      this.reset()
      await this.getSliders()
      this.dragImg()
    },
    sliderDown (event) {
      this.originX = event.clientX
      this.isMouseDown = true
    },
    touchStartEvent (e) {
      this.originX = e.changedTouches[0].pageX
      this.isMouseDown = true
    },
    bindEvents () {
      document.addEventListener('mousemove', (e) => {
        if (!this.isMouseDown) return false
        const moveX = e.clientX - this.originX
        if (moveX < 0 || moveX + 50 >= this.w) return false
        this.sliderLeft = moveX + 'px'
        this.block.style.left = moveX + 'px'
        this.sliderMaskWidth = moveX + 'px'
        this.containerActive = true
      })
      document.addEventListener('mouseup', (e) => {
        if (!this.isMouseDown) return false
        this.isMouseDown = false
        if (e.clientX === this.originX) return false
        this.containerActive = false
        this.verify()
      })
    },
    touchMoveEvent (e) {
      if (!this.isMouseDown) return false
      const moveX = e.changedTouches[0].pageX - this.originX
      if (moveX < 0 || moveX + 38 >= this.w) return false
      this.sliderLeft = moveX + 'px'
      this.block.style.left = moveX + 'px'
      this.sliderMaskWidth = moveX + 'px'
      this.containerActive = true
    },
    touchEndEvent (e) {
      if (!this.isMouseDown) return false
      this.isMouseDown = false
      if (e.changedTouches[0].pageX === this.originX) return false
      this.containerActive = false
      this.verify()
    },
    // 验证滑块
    async valideSlider (x) {
      const sliderParams = {
        answer: { x },
        captchaId: this.captchaId,
        identifier: this.identifier
      }
      let res = await forgetPwdApi.sliderCheck(sliderParams)
      this.captchaId = res.data.captchaId
      let userParams = {
        captchaId: this.captchaId,
        identifier: this.identifier
      }
      return forgetPwdApi.getUser(userParams)
    },
    async verify () {
      const left = parseInt(this.block.style.left)
      if (!this.identifier) {
        this.$message.error(this.$t('vendorMod.enterAccount'))
        this.refresh()
        return
      }
      const res = await this.valideSlider(left).catch(() => { this.refresh() })
      const { question, captchaId } = res.data
      if (res.code === '0' && question) {
        this.$emit('success', { ...question, captchaId })
        this.containerSuccess = true
        this.infoText = this.$t('quality.validateSuccess')
      } else {
        this.$message.error(this.$t('login.sliderError'))
        this.refresh()
      }
    },

    reset () {
      this.containerActive = false
      this.containerSuccess = false
      this.sliderLeft = 0
      this.block.style.left = 0
      this.sliderMaskWidth = 0
      this.canvasCtx.clearRect(0, 0, this.w, this.h)
      this.blockCtx.clearRect(0, 0, this.w, this.h)
      this.fail = false
      this.infoText = ''
    }
  }
}
</script>
<style lang="scss" scoped>
.slide-verify {
  position: relative;
  width: 310px;
  overflow: hidden;
}

.slide-verify-block {
  position: absolute;
  top: 0;
  left: 0;
}

.bottom {
  display: flex;
  align-items: center;
  margin-top: 10px;
  color: rgb(126 126 126 / 100%);

  .slide-verify-refresh-icon {
    top: 0;
    right: 0;
    width: 34px;
    height: 34px;
    font-size: 17px;
    font-weight: bold;
    line-height: 34px;
    color: rgb(126 126 126 / 100%);
    text-align: center;
    cursor: pointer;
    background-size: 34px 471px;
  }

  .slide-verify-refresh-icons {
    top: 0;
    right: 0;
    width: 34px;
    height: 34px;
    font-size: 17px;
    font-weight: bold;
    line-height: 34px;
    color: rgb(126 126 126 / 100%);
    text-align: center;
    cursor: pointer;
    background-size: 34px 471px;
  }

  .slide-verify-refresh-icon:hover {
    transition: all 0.2s ease-in-out;
    transform: rotate(180deg);
  }
}

.slide-verify-slider {
  position: relative;
  width: 310px;
  height: 40px;
  line-height: 40px;
  color: #cacacb;
  text-align: center;
  background: #f4f5f6;
  border: 1px solid #e4e7eb;

  &.hidden {
    visibility: hidden;
  }
}

.slide-verify-slider-mask {
  position: absolute;
  top: 0;
  left: 0;
  height: 40px;
  background: #d1e9fe;
  border: 0 solid #1991fa;
}

.slide-verify-slider-mask-item {
  position: absolute;
  top: 0;
  left: 0;
  width: 56px;
  height: 40px;
  cursor: pointer;
  background: #fff;
  border-radius: 3px;
  box-shadow: 0 0 3px rgb(0 0 0 / 30%);
  transition: background 0.2s linear;
}

.slide-verify-slider-mask-item:hover {
  background: #1991fa;
}

.slide-verify-slider-mask-item-icon {
  position: absolute;
  top: 8px;
  left: 16px;
  width: 28px;
  height: 24px;
  font-size: 22px;
  line-height: 12px;
  color: #ddd;
  text-align: center;
  background: url('@/assets/images/login-drag.png');
  background-position: center;
  background-size: 100% 100%;
}

.slide-verify-info {
  position: absolute;
  top: 0;
  left: 0;
  width: 300px;
  height: 30px;
  line-height: 30px;
  color: #fff;
  text-align: center;
  background-color: #52ccba;
}

.container-active .slide-verify-slider-mask-item {
  top: -1px;
  height: 38px;
  border: 1px solid #1991fa;
}

.container-active .slide-verify-slider-mask {
  height: 38px;
  border-width: 1px;
}

.container-success .slide-verify-slider-mask-item {
  top: -1px;
  height: 38px;
  background-color: #52ccba !important;
  border: 1px solid #52ccba;
}

.container-success .slide-verify-slider-mask {
  height: 38px;
  background-color: #d2f4ef;
  border: 1px solid #52ccba;
}

.container-success .slide-verify-slider-mask-item-icon {
  background-position: 0 0 !important;
}

.slide-verify-slider-text {
  font-size: 12px;
}

.container-active .slide-verify-slider-text,
.container-success .slide-verify-slider-text {
  display: none;
}
</style>
