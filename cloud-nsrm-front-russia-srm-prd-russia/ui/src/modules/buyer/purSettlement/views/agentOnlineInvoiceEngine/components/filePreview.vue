<template>
  <iframe
    :name="iframeName"
    style="border:none; width: 100%;"
    :height="vHeight"
    :src="iframeUrl"
    :title="iframeName"
  />
</template>

<script>
import { getToken } from '@/utils/auth'
import { sysPrefix } from '@/config/ipConfig'

export default {
  name: 'FilePreview',
  props: {
    init: {
      type: Boolean
    },
    vWidth: {
      type: String,
      default: () => {
        return '88%'
      }
    },
    vHeight: {
      type: String,
      default: () => {
        return '500'
      }
    },
    fileuploadId: { // 附件ID
      type: [Number, String],
      default: function () {
        return null
      }
    },
    iframeName: { // iframe 传参
      type: String,
      default: 'previewIframe'
    }
  },

  data () {
    return {
      iframeUrl: null
    }
  },

  watch: {
    init: {
      handler (val) {
        if (val) {
          let origin = this.$systemUrl
          this.iframeUrl = `${origin}${sysPrefix()}/api-onlineview/onlinePreview?fileuploadId=${this.fileuploadId}`
        }
      },
      immediate: true
    }
  }
}
</script>

<style lang="scss" scoped>
.preview {
    display: inline-block;
    cursor: pointer;
    padding: 2px;
}
</style>
