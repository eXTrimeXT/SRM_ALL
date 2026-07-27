<template>
  <el-container class="pdf-contain">
    <!-- <div class="wrapper">
      <el-form inline>
        <el-form-item v-if="flag">
          <template #label>
            <span class="watermark">水印值</span>
          </template>
          <el-input v-model="watermark" />
        </el-form-item>
        <el-form-item>
          <el-button v-if="!flag" type="primary" @click="openWatermark"
            >开启水印</el-button
          >
          <el-button v-else @click="closeWatermark">关闭水印</el-button>
          <el-button type="primary" @click="setPath">加载PDF</el-button>
        </el-form-item>
      </el-form>
    </div> -->
    <iframe
      width="100%"
      height="100%"
      :src="documentPath"
      frameborder="0"
    ></iframe>
  </el-container>
</template>
<script>
import { getToken } from '@/utils/auth'
import { isIE } from 'lib@/utils/validate'
import axios from 'axios'
import { sysPrefix } from '@/config/ipConfig'

// 指定长度和基数
function uuid (len, radix) {
    var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('')
    var uuid = []
        var i
    radix = radix || chars.length

    if (len) {
        // Compact form
        for (i = 0; i < len; i++) uuid[i] = chars[0 | Math.random() * radix]
    } else {
        // rfc4122, version 4 form
        var r

        // rfc4122 requires these characters
        uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-'
        uuid[14] = '4'

        // Fill in random data.  At i==19 set the high bits of clock sequence as
        // per rfc4122, sec. 4.1.5
        for (i = 0; i < 36; i++) {
            if (!uuid[i]) {
                r = 0 | Math.random() * 16
                uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r]
            }
        }
    }

    return uuid.join('')
}

export default {
  name: 'PdfPrint',
  props: ['query'],
  data () {
    return {
      documentPath: '',
      watermark: '',
      flag: false
    }
  },
  created () {
    this.setPath()
  },
  methods: {
    closeWatermark () {
      this.flag = false
      this.setPath()
    },
    openWatermark () {
      this.flag = true
      const orderNumber = this.orderNumber
      this.setPath()
    },
    async useBrowserOpen (url) {
      const { data: pdf } = await axios({
        url,
        method: 'POST',
        responseType: 'arraybuffer',
        loading: true
      })
      console.log('[pdf]', pdf instanceof ArrayBuffer)
      let blobs = new Blob([pdf], { type: 'application/pdf' })
      if (isIE()) { // IE只能下载下来才能打开预览
        navigator.msSaveBlob(blobs, uuid(16, 16) + '.pdf')
      } else {
        const pdfUrl = URL.createObjectURL(blobs)
        this.documentPath = pdfUrl
      }
    },
    async printPdfUseBrowser (url) {
      const pdf = await axios({
        url,
        method: 'GET',
        loading: true,
        responseType: 'arraybuffer'
      })
      console.log('[pdf]', pdf.data instanceof ArrayBuffer)
      let blobs = new Blob([pdf.data], { type: 'application/pdf' })
      var filename = '标签'
      if (isIE()) { // ID只能下载下来才能打开预览
        navigator.msSaveBlob(blobs, filename + '.pdf')
      } else {
        this.documentPath = URL.createObjectURL(blobs)
        // setTimeout(() => {
        //   // this.$refs.iframe.contentWindow.print(); // 谷歌可以用 (火狐版本不可以用)
        //   window.open(this.documentPath,'_blank','noopener,noreferrer') // 谷歌火狐都支持
        // }, 1000)
      }
    },
    setPath () {
      const origin = this.$systemUrl
      // 示例 database:采购订单打印示例.ureport.xml
      console.log('[query]', this.query)
      const xml = this.query.xml
      // const watermark = this.flag ? this.watermark : "空";
      const watermark = ''
      // const params = this.query.params;
      const params = this.query.params.replaceAll('|', encodeURIComponent('|'))// 条码存在|符号，需要转义
      const fileDowloadUrl = `${sysPrefix()}/api-base/ureport/preview?_u=${xml}&${params}`
      // ${origin} &bgText=${watermark}
      console.log(fileDowloadUrl)
      if (this.query.useBrowserOpen === 'Y') {
        this.useBrowserOpen(fileDowloadUrl)
        return
      }
      if (this.query.isBarcode === 'Y') {
        this.printPdfUseBrowser(fileDowloadUrl)
        return
      }

      // const finallyUrl = `./pdfjs/web/viewer.html?file=${encodeURIComponent(
      //   fileDowloadUrl
      // )}`
      this.documentPath = fileDowloadUrl
    }
  }
}
</script>
<style scoped>
.pdf-contain {
  width: 100%;
  padding: 0;
  margin: 0;
  min-width: 1110px;
  height: calc(100% - 5px);
}
.wrapper {
  position: absolute;
  left: 300px;
}
.watermark {
  color: #fff;
}
</style>
