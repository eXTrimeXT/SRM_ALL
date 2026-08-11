<template>
  <div class="c-upload-file">
    <el-progress
      v-if="showProgress && progressFlag"
      :text-inside="true"
      :stroke-width="14"
      :percentage="progressPercent"
      class="progress"
    />
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
      class="upload"
    >
      <slot name="content">
        <!-- <el-button
          class="upload-file-btn"
          icon="el-icon-upload"
          @click="buttonClick"
        > -->
        <AuthorityButton
          :code="code"
          class="upload-file-btn"
          icon="el-icon-upload"
          @click="buttonClick"
        >
          <!-- 上传文件 -->
          {{ $t("dataConfMod.uploadFile") }}
        </AuthorityButton>
        <!-- 上传文件
        </el-button> -->
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
</template>

<script>
import { FILE_UPLOAD, FILE_DOWNLOAD } from '@/api/common'
import { getFileExt, downloadFileLink } from 'lib@/utils/file'
import { getToken } from '@/utils/auth'
import { sysPrefix } from '@/config/ipConfig'
import Big from 'big.js'

const FILE_MAX_SIZE = 500 * 1024 * 1024 // 文件上传单个最大为500M
const TOTAL_MAX_SIZE = 500 * 1024 * 1024 // 文件列表的文件最大为500M

export default {
  name: 'CUploadFile',
  props: {
    code: {
      type: String
    },
    showProgress: {
      type: Boolean,
      default: false
    },
    // 是否显示文件列表，默认显示
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
    tipMsg: {
      type: String,
      default: ''
    },
    cusData: {
      type: Object,
      default: () => {}
    }
  },
  data () {
    return {
      progressFlag: false,
      progressPercent: 0,
      uploadUrl: `${sysPrefix()}${FILE_UPLOAD}`, // 上传文件服务器地址
      downloadUrl: `${sysPrefix()}${FILE_DOWNLOAD}`, // 下载文件地址
      fileMaxSize: FILE_MAX_SIZE,
      totalMaxSize: TOTAL_MAX_SIZE,
      upHeaders: {
        Authorization: 'Bearer ' + getToken(),
        contentType: 'form-data'
      }
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
      // eslint-disable-next-line no-control-regex
      // return str.replace(/[^\x00-\xff]/g, '01').length
      return str.length
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
      const { code, data, message } = response

      if (code === '0') {
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
      } else if (code === 'SRM_COMMON_00021') {
        this.$message.error(message) // 上传失败
        this.$emit('on-error')
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
      const { id, url } = file
      let exportDownloadUrl = ''
      if (this.isDownloadById) {
        exportDownloadUrl = this.downloadUrl + `/${id}`
      } else {
        exportDownloadUrl = url
      }

      downloadFileLink(exportDownloadUrl).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    },
    // 移除文件列表
    onRemove (file, fileList) {
      const { id } = file
      this.$emit('remove-file', id)
    },
    buttonClick () {
      console.log('dasd')
      this.$emit('button-click')
    }
  }
}
</script>

<style lang="scss">
.c-upload-file {
  display: flex;
  justify-content: center;
  .el-upload--text {
    width: 88px;

    .upload-file-btn {
      width: 100%;
      height: 30px;
      padding: 0;
      border-radius: 2px;
      font-size: 12px;
    }
  }

  .tip-msg {
    font-size: 12px;
    color: #b0b9bf;
    margin-top: 7px;
  }

  .progress {
    width: 100%;
  }
}
</style>
