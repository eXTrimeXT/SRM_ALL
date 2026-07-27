<template>
  <el-dialog
    :visible.sync="dialogVisible"
    :title="$t('cusEntry.biddingSettings.uploadSignFile')"
    width="50%"
    append-to-body
    :close-on-click-modal="false"
  >
    <div slot="title" class="header-title">
      {{ $t('cusEntry.biddingSettings.uploadSignFile') }}
    </div>
    <div style="font-size: 12px;">
      <span style="color: red; margin-right: 5px;">*</span>
      <span>{{ $t('cusEntry.biddingSettings.signFile') }}</span>
      <span>{{ $t('cusEntry.biddingSettings.uploadTips') }}</span>
      <div style="margin-top: 15px;">
        <SrmCommonFile
          multiple
          :limit="5"
          :file-list="fileList"
          @on-change="uploadFileListChange"
        />
      </div>
    </div>
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t('common.close') }}
      </el-button>
      <!--提交-->
      <el-button
        v-if="!readonly"
        type="primary"
        @click="uploadSignFile"
      >
        {{ $t('cusEntry.biddingSettings.pubEleSign') }}
      </el-button>
    </div>
  </el-dialog>
</template>
<script>
/**
 * 上传签章文件
 */
export default {
  name: 'EleSignDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    // 只读
    readonly: {
      type: Boolean,
      default: false
    },
    orderId: {
      type: Number
    },
    orderType: {
      type: String
    }
  },
  data () {
    return {
      fileList: []
    }
  },
  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    }
  },
  created () {},
  methods: {
    // 文件上传变更
    uploadFileListChange (fileList) {
      this.fileList = fileList
    },
    // 发起电子签章
    uploadSignFile () {
      let fileList = []
      this.fileList.fileList.forEach(element => {
        const obj = {
          orderDocId: element.fileId,
          orderFileName: element.fileName
        }
        fileList.push(obj)
      })
      this.$http({
        url: '/api-sou/ext/vendor/bid/pushSgin',
        method: 'POST',
        data: {
          orderId: this.orderId,
          orderType: this.orderType,
          signFileList: fileList
        },
        loading: true
      }).then(res => {
        this.$message.success(res.message)
        this.$emit('success')
        window.open(res.data)
        // window.location.href = res.data
        this.dialogVisible = false
      })
    }
  }
}
</script>
<style lang="scss" scoped>
</style>
