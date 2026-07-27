<template>
  <div :class="['fileUpload', { table: table }]">
    <el-progress
      v-if="showProgress && progressFlag"
      :text-inside="true"
      :stroke-width="14"
      :percentage="progressPercent"
      class="progress"
    />
    <!-- 多文件上传 -->
    <div
      v-if="showFileList"
      class="upload-file"
    >
      <el-upload
        v-show="!showProgress || !progressFlag"
        ref="upload"
        multiple
        :show-file-list="showFileList"
        :headers="upHeaders"
        :data="cusData"
        :limit="limit"
        :action="uploadUrl"
        :max-size="fileMaxSize"
        :file-list="fileList"
        :before-upload="beforeUpload"
        :on-progress="onProgress"
        :on-success="onSucess"
        :on-error="onError"
        :on-preview="onPreview"
        :on-remove="onRemove"
        :on-change="onChange"
        :disabled="disabled"
        :class="['upload', { showFileListOnly: disabled }]"
      >
        <slot name="content">
          <el-button
            class="upload-file-btn"
            icon="el-icon-upload2"
            @click="buttonClick"
          >
            {{ $t("dataConfMod.uploadFile") }}
          </el-button>
        </slot>
        <div
          v-if="tipMsg.length > 0"
          slot="tip"
          class="tip-msg"
        >
          {{ tipMsg }}
        </div>
      </el-upload>
    </div>
    <!-- 单文件 -->
    <div
      v-else
      class="upload-file"
    >
      <div class="fileWrap">
        <a
          :class="['download-link', { ellipsis: true }]"
          :title="fileuploadName"
          href="javascript: void(0);"
          @click="handleClick"
        >
          {{ fileuploadName }}
        </a>
      </div>
      <div class="fileOpt">
        <el-upload
          v-show="!showProgress || !progressFlag"
          ref="upload"
          :show-file-list="showFileList"
          :headers="upHeaders"
          :data="cusData"
          :limit="limit"
          :action="uploadUrl"
          :max-size="fileMaxSize"
          :file-list="fileList"
          :before-upload="beforeUpload"
          :on-progress="onProgress"
          :on-success="onSucess"
          :on-error="onError"
          :on-change="onChange"
          :disabled="disabled"
          class="upload"
        >
          <slot name="content">
            <el-button
              class="upload-file-btn"
              icon="el-icon-upload2"
              @click="buttonClick"
            >
              {{ $t("dataConfMod.uploadFile") }}
            </el-button>
          </slot>
          <div
            v-if="tipMsg.length > 0"
            slot="tip"
            class="tip-msg"
          >
            {{ tipMsg }}
          </div>
        </el-upload>
        <el-button
          v-if="isPreview"
          class="viewBtn"
          @click="singlePreViewHandel"
        >
          预览
        </el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { FILE_UPLOAD, FILE_DOWNLOAD } from '@/api/common'
import { getFileExt, downloadFileLink } from 'lib@/utils/file'
import { getToken } from '@/utils/auth'
import Big from 'big.js'
import { sysPrefix } from '@/config/ipConfig'

const FILE_MAX_SIZE = 500 * 1024 * 1024 // 文件上传单个最大为500M
const TOTAL_MAX_SIZE = 500 * 1024 * 1024 // 文件列表的文件最大为500M

export default {
  name: 'UploadFile',
  props: {
    code: {
      type: String
    },
    showProgress: {
      type: Boolean,
      default: true
    },
    // 是否显示文件列表，默认显示 多分文件
    showFileList: {
      type: Boolean,
      default: true
    },
    // 默认文件列表
    fileList: {
      type: Array,
      default: () => []
    },
    // 是否禁用
    disabled: {
      type: Boolean,
      default: false
    },
    // 接受上传的文件类型
    acceptFileType: {
      type: Array,
      default: () => []
    },
    // 点击文件列表是否调用根据id的下载文件接口
    isDownloadById: {
      type: Boolean,
      default: true
    },
    limit: {
      type: Number,
      default: 10
    },
    table: {// 是否表格里面 表格里面的按钮的尺寸较小
      type: Boolean,
      default: false
    },
    tipMsg: {
      type: String,
      default: ''
    },
    cusData: {
      type: Object,
      default: () => {}
    },
    downloadLimit: {// 点击下载需要调用额外方法
      type: Boolean,
      default: false
    },
    // 单文件fileId
    fileId: {
      default: ''
    },
    // 单文件fileName
    fileName: {
      default: ''
    },
    isPreview: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      progressFlag: false,
      progressPercent: 0,
      uploadUrl: `${sysPrefix()}${FILE_UPLOAD}`, // 上传文件服务器地址
      downloadUrl: FILE_DOWNLOAD, // 下载文件地址
      fileMaxSize: FILE_MAX_SIZE,
      totalMaxSize: TOTAL_MAX_SIZE,
      upHeaders: {
        Authorization: 'Bearer ' + getToken(),
        contentType: 'form-data'
      }
    }
  },
  computed: {
    fileuploadId () {
      return this.fileId
    },
    fileuploadName () {
      return this.fileName
    }
  },
  watch: {
    fileId (newValue) {

    }
  },
  methods: {
    onChange (file, fileList) {},
    // 上传文件前
    beforeUpload (file) {
      const { name } = file
      let fileExt = getFileExt(name)

      // 判断文件名的长度不可超过一百
      let nameLen = name.split('.')
      nameLen = nameLen
        .reverse()
        .slice(1)
        .join('.')
      // console.log("nameLen",nameLen)
      let inputLength = this.countLength(nameLen)
      if (inputLength > 100) {
        this.$message.warning(
          this.$t('components.fileupload.fileNameLengthErr')
        ) // 文件名字符长度不可超过100
        return false
      }

      if (
        this.acceptFileType.length > 0 &&
        this.acceptFileType.indexOf(fileExt) === -1
      ) {
        let messageTip = this.acceptFileType.join('、')
        this.$message.warning(
          this.$t('components.fileupload.msgUploadFormat') + `${messageTip}`
        )
        return false
      }
      let isLt2M = new Big(file.size).div(1024).div(1024) < 1500

      if (!isLt2M) {
        // 上传文件大小不能超过 1.5G
        this.$message({
          message:
            this.$t('components.fileupload.maxSizeUploadError') + ' 1.5G ',
          type: 'warning'
        })
        return false
      }
      this.$emit('before-upload')
    },
    countLength (str) {
      if (str == null) return 0
      if (typeof str !== 'string') {
        str += ''
      }
      return str.replace(/[^\x00-\xff]/g, '01').length
    },
    // 上传文件中
    onProgress (event, file, fileList) {
      this.progressPercent = Math.abs(event.percent.toFixed(0))
      if (event.percent !== 100) {
        this.progressFlag = true
      } else {
        // this.progressFlag = false;
      }
      this.$emit('upload-progress', event)
    },
    // 上传文件成功
    onSucess (response, file, fileList) {
      const { size } = file
      const { code, data } = response
      if (code === 'R000') {
        this.$emit('upload-success', {
          id: data.fileuploadId,
          name: data.fileSourceName,
          fileFunction: data.fileFunction,
          fileModular: data.fileModular,
          createdBy: data.createdBy,
          creationDate: data.creationDate,
          fileFullname: data.fileFullname,
          filePath: data.filePath,
          filePureName: data.filePureName,
          fingerprint: data.fingerprint,
          fileExtendType: data.fileExtendType,
          fileType: data.fileType,
          fileSize: data.fileSize
        }, fileList)
      } else {
        this.$message.error(this.$t('components.fileupload.uploadFailed')) // 上传失败
        this.$emit('on-error')
      }
      this.$refs.upload.clearFiles()
    },
    // 上传文件出错
    onError (errData, file, fileLis) {
      this.$message.error(this.$t('components.fileupload.uploadFailed')) // 上传失败
      this.$emit('on-error')
    },
    // 点击文件列表
    onPreview (file) {
      const { name, id, url } = file
      if (this.downloadLimit) {
        this.$emit('on-preview', file) // 触发下载 用其他接口下载
      } else {
        let exportDownloadUrl = ''
        let fileId = ''
        let filePath = ''
        if (file.response) {
          fileId = file.response.data.fileuploadId // 上传后返回的附件ID
          filePath = file.response.data.filePath
        } else {
          fileId = file[this.fileIdProp] || id
          filePath = url
        }
        if (this.isDownloadById) {
          exportDownloadUrl = `${this.downloadUrl}?fileuploadId=${fileId}`
        } else {
          exportDownloadUrl = filePath
        }
        downloadFileLink(exportDownloadUrl, name).catch(() => {
          this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
        })
      }
    },
    // 移除文件列表
    onRemove (file, fileList) {
      const { id, fileuploadId } = file
      let fileId = ''
      if (file.response) {
        fileId = file.response.data.fileuploadId // 上传后返回的附件ID
      } else {
        fileId = fileuploadId || id
      }
      this.$emit('remove-file', fileId)
    },
    buttonClick () {
      this.$emit('button-click')
    },
    // 单个文件预览
    singlePreViewHandel () {
      this.$emit('on-preview', '')
    },
    // 点击文件下载
    handleClick () {
      if (this.fileId) {
        downloadFileLink(
          `${this.downloadUrl}?fileuploadId=${this.fileId}`,
          this.fileName
        ).catch(() => {
          this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
        })
      } else {
        throw new Error('AttachId is null.')
      }
    }
  }
}
</script>

<style lang="scss">
.fileUpload{
  .el-button.viewBtn{
    padding: 0px 5px !important;
    width: auto !important;
    height: 32px;
    min-width: 48px !important;
  }
  .upload-file {
    display: flex;
    justify-content: left;
    .el-upload--text {
      .upload-file-btn {
        min-width: 48px !important;
        width: 100%;
        height: 30px;
        padding: 0px 5px 0 2px !important;
        border-radius: 2px;
        font-size: 12px;
      }
    }
    .el-button [class*="el-icon-"] + span{
      margin-left: 0;
    }

    .tip-msg {
      font-size: 12px;
      color: #b0b9bf;
      margin-top: 7px;
    }

    .progress {
      width: 100%;
    }
    .fileWrap{
      flex: 1;
      padding-right: 2px;
    }
    .fileOpt{
      .upload{
        display: inline-block;
      }
    }
  }
  .download-link {
    &.ellipsis {
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      word-wrap: normal;
      height: 32px;
      line-height: 30px;
      color: #409eff;
      display: block;
      width: 100%;
      border: 1px solid #ddd;
      border-radius: 2px;
      box-sizing: border-box;
      padding-left: 5px;
    }
  }

  &.table{
    .el-button.viewBtn{
      height: 26px;
    }
    .upload-file{
      .el-upload--text {
        .upload-file-btn {
          height: 26px;
        }
      }
    }
    .download-link{
      &.ellipsis{
        height: 26px;
        line-height: 24px;
      }
    }
  }
  // 只显示附件列表 不显示上传按钮
  .showFileListOnly {
    .el-upload.el-upload--text {
      display: none;
    }
    .el-upload-list__item-status-label {
      display: none;
    }
    .el-icon-close {
      display: none;
    }
    .el-icon-close-tip {
      display: none !important;
    }
  }
}

</style>
