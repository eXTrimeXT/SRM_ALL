<template>
  <div>
    <el-progress
      v-if="showProgress && progressInfo.view"
      :text-inside="true"
      :stroke-width="14"
      :percentage="progressInfo.percent"
      class="progress"
    />
    <el-upload
      :ref="refName"
      v-bind="$attrs"
      :multiple="multiple"
      class="file-uploader"
      :action="uploadUrl"
      :headers="headers"
      :data="data"
      :accept="acceptCurrent"
      :before-upload="handleBeforeUpload"

      :file-list="viewFileList"
      :show-file-list="showFileList"
      :list-type="listType"
      :disabled="disabled"
      :limit="limit"
      :on-progress="handleProgress"
      :on-success="handleSuccess"
      :on-error="onError"
      :on-preview="onPreview"
      :on-remove="handleRemove"
      v-on="$listeners"
    >
      <slot name="content">
        <template v-if="isAvatar">
          <template v-if="!multiple">
            <img
              v-if="currentFile && currentFile.url"
              :src="currentFile.url"
              class="image"
              @click="buttonClick"
            >
            <i
              v-else
              class="el-icon-plus image-uploader-icon"
              @click="buttonClick"
            />
          </template>
          <template v-else>
            <template v-if="currentFileList && currentFileList.length > 0">
              <img
                v-for="fileItem in currentFileList"
                :key="fileItem.url"
                :src="fileItem.url"
                class="image"
              >
            </template>
            <i
              class="el-icon-plus image-uploader-icon"
              @click="buttonClick"
            />
          </template>
        </template>
        <el-input
          v-else
          v-model="inputFileName"
          clearable
          readonly
          :disabled="disabled"
        >
          <div
            slot="suffix"
            class="el-input__icon input-suffix-icon"
          >
            <i
              v-if="(!multiple && currentFile && !disabled)"
              class="el-icon-circle-close suffix-icon"
              @click="clearUpload"
            />
            <i
              v-if="!multiple && currentFile && isPreview"
              class="el-icon-view suffix-icon"
              @click="previewHandle"
            />
            <i
              v-if="!multiple && currentFile"
              class="el-icon-bottom suffix-icon"
              @click="downloadHandle"
            />
            <i
              v-if="(multiple || !currentFile) && !disabled"
              class="el-icon-upload2 suffix-icon"
              @click="buttonClick"
            />
          </div>
        </el-input>
      </slot>
      <div slot="trigger" />
      <div
        v-if="tipMsg.length > 0"
        slot="tip"
        class="tip-msg"
      >
        {{ tipMsg }}
      </div>
    </el-upload>

    <file-preview
      :visible="previewInfo.visible"
      :fileupload-id="previewInfo.fileuploadId"
      :file-name="previewInfo.fileName"
      @cancel="previewInfo.visible = false"
    />
  </div>
</template>

<script>
import { FILE_UPLOAD, FILE_DOWNLOAD } from '@/api/common'
import StringUtils from '@/utils/string-utils'
import { getFileExt, downloadFileLink } from 'lib@/utils/file'
import FilePreview from '@/library/components/filePreview'
import { getToken } from '@/utils/auth'
import { sysPrefix } from '@/config/ipConfig'
import { geti18n } from '@/main'
const i18n = geti18n()

export default {
  /* eslint-disable */
  name: 'ChickUpload',
  mixins: [],
  components: {FilePreview},
  props: {
    refName: {
      type: String,
      default() {
        return 'upload'
      }
    },
    multiple: {
      type: Boolean,
      default: false
    },
    action: {
      type: String,
      required: true,
      default: () => {
        return FILE_UPLOAD
      }
    },
    headers: {
      type: Object,
      default() {
        return {
          Authorization: "Bearer " + getToken(),
          contentType: "form-data"
        }
      }
    },
    data: {
      type: Object,
      default: () => {
        return {
          // 固定参数
          uploadType: "FASTDFS",
          // 固定参数
          sourceType: "WEB_APP",
          // 文件所属模块
          fileModular: null,
          // 文件所属功能
          fileFunction: null,
          // 文件所属类型
          fileType: null
        }
      }
    },
    accept: {
      // image/png, image/jpeg
      type: String,
      default: () => {
        return null
      }
    },
    beforeUpload: Function,
    onRemove: {
      type: Function
    },
    onChange: {
      type: Function
    },
    onPreview: {
      type: Function
    },
    onSuccess: {
      type: Function
    },
    onProgress: {
      type: Function
    },
    onError: {
      type: Function
    },
    fileList: {
      type: Array,
      default () {
        return []
      }
    },
    listType: {
      type: String,
      default: 'text' // text,picture,picture-card
    },
    disabled: {
      type: Boolean,
      default: false
    },
    limit: Number,
    maxSize: {
      type: Number,
      default () {
        return 20480 // 单位：kb
      }
    },
    isImage: {
      type: Boolean,
      default: false
    },
    isAvatar: {
      type: Boolean,
      default: false
    },
    isPreview:{
      type: Boolean,
      default: false
    },
    outMaxSizeTip: {
      type: String,
      default: () => {
        return i18n.t("components.fileupload.maxSizeUploadError")
      }
    },
    nameMaxLength: {
      type: Number,
      default: () => {
        return 100
      }
    },
    outNameMaxLengthTip: {
      type: String,
      default: () => {
        return i18n.t("components.fileupload.fileNameLengthErr")
      }
    },
    fileTypeUnSupportTip: {
      type: String,
      default: () => {
        return i18n.t("components.fileupload.msgUploadFormat")
      }
    },
    uploadFailedTip: {
      type: String,
      default: () => {
        return i18n.t("components.fileupload.uploadFailed")
      }
    },
    tipMsg: {
      type: String,
      default: () => {
        return ''
      }
    },
    filePrefix: {
      type: String,
      default: () => {
        return FILE_DOWNLOAD
      }
    },
    value: { // fileuploadId array。
      type: [Object, Array],
      default: function () {
        return null
      }
    },
    fileDictCode: {
      type: String,
      default: () => {
        return StringUtils.guid();
      }
    },
    initFileInfo: {
      type: [Object, Array],
      default: () => {
        return null
      }
    },
    dictClass: {
      default: () => {
        return null
      }
    },
    showProgress: {
      type: Boolean,
      default: () => {
        return true
      }
    },
    showFileList: {
      type: Boolean,
      default: () => {
        return false
      }
    },
    eventName: {
      type: String,
      default: () => {
        return 'input'
      }
    },
    transferIn: { // 传入参数字段转化
      type: Function
    },
    transferOut: { // 传出参数字段转化
      type: Function
    },
    /* eslint-enable */
  },
  data () {
    return {
      uploadUrl: `${sysPrefix()}${this.action}`, // 上传文件服务器地址
      inputFileName: '',
      currentFileList: [],
      currentFile: null,
      downloadPrefix: null,
      viewFileList: [],
      progressInfo: {
        view: false,
        percent: 0
      },
      previewInfo: {
        visible: false,
        fileuploadId: null,
        fileName: null
      }
    }
  },
  computed: {
    acceptCurrent () {
      if (this.accept) {
        return this.accept
      }
      if (this.isImage || this.isAvatar) {
        return 'image/*'
      }
      return null
    },
    dictionary () {
      if (this.dictClass) {
        console.log('this.dictionary===', this.dictClass.dictStore.dictStates)
        return this.dictClass.dictStore.dictStates
      }
      return null
    }
  },
  watch: {
    value: {
      handler () {
        this.initValue()
      },
      deep: true
    },
    currentFile: {
      handler () {
        if (!this.multiple) {
          this.inputFileName = this.getInputFileName()

          console.log('this handler currentFile', this.currentFile)

          this.$emit('input', this.currentFile)
          this.$emit(this.eventName, this.currentFile, this.getFileInfo())
        }
      },
      deep: true
    },
    currentFileList: {
      handler () {
        if (this.multiple) {
          this.inputFileName = this.getInputFileName()
          this.$emit('input', this.currentFileList)
          this.$emit(this.eventName, this.currentFileList, this.getFileInfo())
        }
      },
      deep: true
    }
  },
  mounted () {
    this.downloadPrefix = FILE_DOWNLOAD
    this.initValue(true)
  },
  methods: {
    getUrl (fileItem) {
      return `${this.downloadPrefix}?fileuploadId=${fileItem.fileuploadId}`
    },
    initValue (init) {
      if (!this.dictClass) {
        return
      }
      const viewList = []
      if (this.multiple) {
        if (this.initFileInfo && this.initFileInfo.length > 0) {
          const fileMap = {}
          for (let i = 0; i < this.initFileInfo.length; i++) {
            const currentFileItem = this.setFileDict(this.initFileInfo[i], 'IN')

            viewList.push({ id: currentFileItem.id, name: currentFileItem.name, url: currentFileItem.url })
          }

          this.dictClass.setDictMap(this.fileDictCode, fileMap)
        }
        this.currentFileList = this.value
      } else {
        if (!this.value) {
          this.currentFile = this.value
          this.currentFileList = []
        } else {
          let currentFileItem = this.setFileDict(this.initFileInfo, 'IN')

          this.currentFile = this.value
          this.currentFileList = []
          this.currentFileList.push(this.value)
          viewList.push({ id: currentFileItem.id, name: currentFileItem.name, url: currentFileItem.url })
        }
      }
      if (init) {
        this.viewFileList = viewList
      }
      this.inputFileName = this.getInputFileName()
    },
    abortUpload () {
      this.$refs[this.refName].abort()
    },
    clearFiles () {
      // this.$refs[this.refName].clearFiles()
    },
    handleProgress (event, file, fileList) {
      if (this.onProgress) {
        this.onProgress(event, file, fileList)
      }

      console.log('event.percent', event.percent)
      this.progressInfo.percent = Math.abs(event.percent.toFixed(0))
      console.log('event. progressInfo', this.progressInfo.percent)
      if (event.percent !== 100) {
        this.progressInfo.view = true
      } else {
        this.progressInfo.percent = 0
      }

      this.$emit('on-progress-after', event)
    },
    handleBeforeUpload (file) {
      // 文件大小校验
      const standardMaxSize = file.size / 1024 < this.maxSize
      if (!standardMaxSize) {
        this.$message.error(this.outMaxSizeTip + this.maxSize + 'kb')
      }

      // 文件名校验
      let nameLength = name.split('.')
      nameLength = nameLength.reverse().slice(1).join('.')
      let inputLength = StringUtils.countLength(nameLength)
      const standardMaxNameLength = inputLength <= this.nameMaxLength
      if (!standardMaxNameLength) {
        this.$message.warning(this.outNameMaxLengthTip)
      }

      // 文件类型校验
      let accept = this.acceptCurrent
      accept = StringUtils.trim(accept)
      let standardFileType = true
      if (accept && accept.length > 0) {
        const acceptArray = accept.split(',')
        const checkAcceptArray = []
        const checkAcceptRegex = []
        for (let i = 0; i < acceptArray.length; i++) {
          let acceptItem = StringUtils.trim(acceptArray[i])
          if (acceptItem.length > 0) {
            checkAcceptArray.push(acceptItem)

            // 如果是.*,则直接放空
            // eslint-disable-next-line no-useless-escape
            const acceptRegex = acceptItem.replace('/\*/g', '.*')
            checkAcceptRegex.push(new RegExp(acceptRegex))
          }
        }
        const mimeType = file.type
        let fileExt = '.' + getFileExt(file.name)
        if (checkAcceptArray.indexOf(mimeType) < 0 || checkAcceptArray.indexOf(fileExt) < 0) {
          standardFileType = false
        }
        if (!standardFileType && checkAcceptRegex.length > 0) {
          for (let i = 0; i < checkAcceptRegex.length; i++) {
            const acceptRegex = checkAcceptRegex[i]
            if (acceptRegex.test(mimeType) || acceptRegex.test(fileExt)) {
              standardFileType = true
              break
            }
          }
        }
      }
      if (!standardFileType) {
        this.$message.warning(
          this.fileTypeUnSupportTip + accept
        )
      }

      let standard = standardMaxSize && standardMaxNameLength && standardFileType

      if (standard) {
        if (this.beforeUpload) {
          standard = this.beforeUpload(file)
        }
        if (standard) {
          this.$emit('before-upload-success')
        }
      }

      return standard
    },
    setFileDict (data, transferType) {
      let currentFileItem = {
        id: data.fileuploadId,
        name: data.fileSourceName,
        url: null,

        lastUpdateDate: data.lastUpdateDate,
        base64: data.base64,
        businessId: data.businessId,
        createdId: data.createdId,
        pageSize: data.pageSize,
        creationDateBegin: data.creationDateBegin,
        isNeedTotal: data.isNeedTotal,
        fileFunction: data.fileFunction,
        createdUserName: data.createdUserName,
        fingerprint: data.fingerprint,
        uploadType: data.uploadType,
        searchUrl: data.searchUrl,
        filePureName: data.filePureName,
        sceneFileSourceName: data.sceneFileSourceName,
        lastUpdatedId: data.lastUpdatedId,
        sceneAttachmentId: data.sceneAttachmentId,
        createdFullName: data.createdFullName,
        lastUpdatedUserName: data.lastUpdatedUserName,
        lastUpdatedBy: data.lastUpdatedBy,
        createdByIp: data.createdByIp,
        creationDateEnd: data.creationDateEnd,
        lastUpdatedByIp: data.lastUpdatedByIp,
        filePath: data.filePath,
        creationDate: data.creationDate,
        lastUpdatedFullName: data.lastUpdatedFullName,
        pageNum: data.pageNum,
        version: data.version,
        fileuploadId: data.fileuploadId,
        sceneFileUploadId: data.sceneFileUploadId,
        expireTime: data.expireTime,
        fileExtendType: data.fileExtendType,
        createdBy: data.createdBy,
        fileSize: data.fileSize,
        fileSourceName: data.fileSourceName,
        sourceType: data.sourceType,
        tenantId: data.tenantId,
        comment: data.comment,
        fileFullname: data.fileFullname,
        fileModular: data.fileModular,
        fileType: data.fileType
      }
      if (transferType === 'IN') {
        if (this.transferIn && typeof this.transferIn === 'function') {
          this.transferIn(data, currentFileItem)
        }
      }
      if (transferType === 'OUT') {
        if (this.transferOut && typeof this.transferOut === 'function') {
          this.transferOut(data, currentFileItem)
        }
      }
      currentFileItem.url = this.getUrl(currentFileItem)

      const fileMap = {}
      fileMap[currentFileItem.fileuploadId] = currentFileItem
      this.dictClass.setDictMap(this.fileDictCode, fileMap)

      return currentFileItem
    },
    handleSuccess (res, file, fileList) {
      if (this.onSuccess) {
        this.onSuccess(res, file, fileList)
      }

      const { code, data, message } = res
      if (code === 'R000') {
        const fileObj = data

        if (this.multiple) {
          const currentFileItem = this.setFileDict(fileObj, 'OUT')
          this.currentFileList.push(currentFileItem.fileuploadId)
        } else {
          const currentFileItem = this.setFileDict(fileObj, 'OUT')

          this.currentFile = currentFileItem.fileuploadId
          this.currentFileList = []
          this.currentFileList.push(currentFileItem.fileuploadId)
        }

        this.$emit('upload-success', fileObj, fileList)
      } else if (code === 'R041') {
        this.$message.error(message) // 上传失败
        this.$emit('on-error')
      } else {
        this.$message.error(this.$t('components.fileupload.uploadFailed')) // 上传失败
        this.$emit('on-error')
      }
      this.clearFiles()
    },
    clearUpload () {
      this.currentFile = null
      this.currentFileList = []
      this.viewFileList = []
    },
    handleRemove (file, fileList) {
      if (this.onRemove) {
        this.onRemove(file, fileList)
      }
      if (!this.multiple) {
        this.currentFile = null
        this.currentFileList = []
      } else {
        this.currentFile = null
        this.removeFile(file)
      }
    },
    getFileInfoByFileId (fileuploadId) {
      if (fileuploadId) {
        const fileObject = this.dictionary[this.fileDictCode] ? this.dictionary[this.fileDictCode][fileuploadId] : null
        return fileObject
      }
      return null
    },
    getFileInfo () {
      if (!this.multiple) { // 单文件
        return this.getFileInfoByFileId(this.currentFile)
      }
      let tempFileList = []
      for (let i = 0; i < this.currentFileList.length; i++) {
        tempFileList.push(this.getFileInfoByFileId(this.currentFileList[i]))
      }
      return tempFileList
    },
    getFileNameByFileId (fileuploadId) {
      if (fileuploadId) {
        const fileObject = this.dictionary[this.fileDictCode] ? this.dictionary[this.fileDictCode][fileuploadId] : null
        if (fileObject) {
          return fileObject.fileSourceName
        }
      }
      return ''
    },
    getInputFileName () {
      if (!this.multiple) { // 单文件
        console.log('this.dictionary', this.dictionary)
        console.log('this.input file name currentFile', this.currentFile)
        if (this.currentFile) {
          const fileObject = this.dictionary[this.fileDictCode] ? this.dictionary[this.fileDictCode][this.currentFile] : null
          if (fileObject) {
            return fileObject.fileSourceName
          }
        }
        return ''
      }
      let inputFileNames = ''
      for (let i = 0; i < this.currentFileList.length; i++) {
        inputFileNames += this.getFileNameByFileId(this.currentFileList[i]) + ','
      }
      return inputFileNames
    },
    removeFile (file) {
      for (let i = 0; i < this.currentFileList.length; i++) {
        if (file.fileuploadId === this.currentFileList[i] || file.response.data.fileuploadId === this.currentFileList[i]) {
          this.currentFileList.splice(i, 1)
          break
        }
      }
    },
    buttonClick () {
      this.clearFiles()
      this.$refs[this.refName].$refs['upload-inner'].handleClick()
      this.$emit('button-click')
    },
    previewHandle () {
      if (!this.currentFile) {
        this.$message.warning('请先选择文件！')
        return
      }
      this.previewInfo.visible = true
      this.previewInfo.fileuploadId = this.currentFile
      this.previewInfo.fileName = this.getInputFileName()

      this.$emit('on-preview', '')
    },
    downloadHandle () {
      if (!this.currentFile) {
        this.$message.warning('请先选择文件！')
        return
      }
      const url = `${this.downloadPrefix}?fileuploadId=` + this.currentFile
      downloadFileLink(
        url,
        this.getInputFileName()
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })

      this.$emit('on-download', '')
    }
  }
}
</script>

<style lang="scss">
.file-uploader {
  .el-button {
    min-width: 0px;
  }
  .el-input-group__append button{
    border-right: 1px solid #dfe4ed;
  }
}
</style>

<style lang="scss" scoped>
.file-uploader {
  .image-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 60px;
    height: 60px;
    line-height: 60px;
    text-align: center;
    border: 1px dashed #d9d9d9;
    border-radius: 100%;
  }
  .image {
    width: 60px;
    height: 60px;
    display: block;
    object-fit: cover;
    border-radius: 100em;
    border: 1px solid #DDDDDD;
  }
  .input-suffix-icon {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    width: 75px;
    .suffix-icon {
      padding: 0 5px;
      font-size: 22px;
      cursor: pointer;
    }
  }
}
</style>
