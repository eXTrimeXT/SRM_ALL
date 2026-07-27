<template>
  <el-dialog
             :visible.sync="dialogVisible"
             :width="vWidth"
             class="inportAbcd"
    :title="fileName+'-'+$t('preview')"
             :append-to-body="true"
             :close-on-click-modal="false"
             @close="cancel"
    @closed="cancel"
  >
    <iframe
            v-if="dialogVisible"
            :name="iframeName"
            style="border:none; width: 100%;"
            :height="vHeight"
            :src="iframeUrl"
      :title="iframeName"
    />

    <div slot="footer">
      <el-button @click="cancel">
        {{ $t("common.cancel") }}
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import { getToken } from '@/utils/auth'
import { sysPrefix } from '@/config/ipConfig'

export default {
  name: 'FilePreview',
  props: {
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
    visible: {
      type: Boolean,
      default: () => {
        return false
      }
    },
    fileuploadId: { // 附件ID
      type: [Number, String],
      default: function () {
        return null
      }
    },
    fileName: { // 附件名称
      type: String,
      default: ''
    },
    iframeName: { // iframe 传参
      type: String,
      default: 'previewIframe'
    },
    target: { //  dialog 弹框预览 | blank 新窗口预览
      type: String,
      default: 'dialog'
    }
  },

  data () {
    return {
      iframeUrl: null,
      dialogVisible: false
    }
  },

  watch: {
    visible: {
      handler (val) {
        if (val) {
          let origin = this.$systemUrl
          // this.iframeUrl = `${origin}${sysPrefix()}/api-onlineview/onlinePreview?access_token=${getToken()}&fileuploadId=${this.fileuploadId}`
          this.iframeUrl = `${origin}${sysPrefix()}/api-onlineview/onlinePreview?fileuploadId=${this.fileuploadId}`

          // 新窗口预览不必打开弹窗
          if (this.target.toLowerCase() === 'blank') {
            window.open(this.iframeUrl, '_blank', 'noopener,noreferrer')
            this.cancel()
          } else {
            this.dialogVisible = true
          }
        } else {
          this.dialogVisible = false
        }
      },
      immediate: true
    }
  },

  methods: {
    cancel () {
      this.$emit('cancel')
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
