<template>
  <el-container class="flex-container" direction="vertical">
    <el-main style="position:relative;height:100%">
      <iframe
        name="iframeModel"
        style="border:none;height:100%;position:absolute;width:100%;"
        :width="iframeWidth"
        :src="iframeUrl"
        title=""
      />
    </el-main>
  </el-container>
</template>

<script>
import { getDictItem } from '@/api/common'
export default {
  name: 'CrystalBallReport',

  data () {
    return {
      iframeUrl: '',
      iframeWidth: '100%',
      ballRrl: ''
    }
  },
  async created () {
    let res = await getDictItem('CRYSTAL_BALL_URL')
    if (res.data[0]) {
      this.ballRrl = res.data[0].dictItemCode
      this.loadIframe()
    }
  },
  methods: {
    // 加载iframe
    loadIframe () {
      let token = this.$store.getters.token
      let url = this.ballRrl + token
      this.iframeUrl = url
    }
  }
}
</script>
