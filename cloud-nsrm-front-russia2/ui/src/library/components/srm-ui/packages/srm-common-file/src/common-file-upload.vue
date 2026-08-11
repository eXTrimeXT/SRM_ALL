<template>
  <div
    :class="['common-file-wrap', { 'is-dragger': drag }, { 'align-left': isSceneFile && multiple && !readonly }, { 'multiple-picture-card': multiplePicture }]"
    :style="drag && !isSeveral ? draggerWrapSize : ''"
  >
    <!--上传组件-->
    <el-upload
      v-if="showUploadWrap"
      ref="upload"
      :show-file-list="false"
      :headers="headers"
      :multiple="multiple"
      :drag="drag"
      :limit="limitConversion"
      :data="uploadExtraData"
      :action="actionUrl"
      :file-list="fileListData"
      :disabled="fileDisabled"
      :on-progress="handleOnProgress"
      :on-change="onChange"
      :on-error="handleUploadError"
      :on-success="handleUploadSuccess"
      :before-upload="handleBeforeUpload"
      :on-exceed="handleOnExceed"
      :style="draggerUploadWrapStyle"
      class="upload-wrap"
    >
      <!--提供default插槽，可自定义触发上传标签-->
      <slot>
        <!--拖拽组件-->
        <CommonFileDragger
          v-if="drag"
          ref="commonFileDragger"
          :dragger-options-default="draggerOptionsDefault"
        />
        <!-- 多个图片展示 -->
        <i
          v-if="multiplePicture"
          class="el-icon-plus el-upload--picture-card multiple-picture-upload-btn"
          :style="[pictureStyleOptions, {'line-height': pictureStyleOptions.height} ]"
        />

        <el-button
          v-else
          ref="uploadButton"
          :icon="buttonOptionsDefault.icon"
          :type="buttonOptionsDefault.type"
          :disabled="fileDisabled || buttonOptionsDefault.disabled"
          class="upload-button"
        >
          {{ buttonOptionsDefault.title }}
        </el-button>
      </slot>
    </el-upload>

    <!--文件列表-->
    <CommonFileList
      v-if="showFileList"
      v-bind="$props"
      :file-list="fileListData"
      :dragger-wrap-size="draggerWrapSize"
      file-from-upload
      :style="fileListStyle"
      @on-remove="removeFile"
      @on-trigger="triggerUpload"
    />

    <!--上传列表进度条 上传中、上传成功、上传失败-->
    <div
      v-if="showProgressWrap"
      :class="['progress-wrap', { 'is-picture-card': listTypeMap.PICTURE_CARD }]"
      :style="draggerWrapSize"
    >
      <!--文本-->
      <div
        v-if="listTypeMap.TEXT"
        :class="['upload-files-wrap', { 'is-progress': progress }]"
      >
        <div
          v-for="(item, index) in uploadFileList"
          :key="`fileItem-${index}`"
          class="file-item-wrap"
        >
          <!--loading 图标-->
          <div class="file-item-icon-wrap">
            <em :class="['file-item-icon', 'el-icon-loading', { 'is-loading': uploadLoading }]" />
          </div>

          <!--附件名称-->
          <div class="file-item-name themeColor">
            {{ item.name }}
          </div>

          <!--操作按钮 与 状态图标-->
          <div class="file-item-operation">
            <!--删除 只有错误才删除-->
            <em
              v-if="uploadStatusMap.INFO_ERROR"
              class="el-icon-close operation-icon"
              @click="removeFileInUploadList(index)"
            />

            <!--状态图标-->
            <!--成功-->
            <em
              v-if="progressInfo.percent === 100 && !uploadStatusMap.INFO_ERROR"
              class="el-icon-success status-icon"
            />
            <!--失败-->
            <em
              v-if="uploadStatusMap.INFO_ERROR"
              class="el-icon-error status-icon"
            />
          </div>
        </div>
      </div>

      <!--图片卡片列表-->
      <div
        v-if="listTypeMap.PICTURE_CARD"
        class="upload-files-picture-card-wrap"
      >
        <p
          v-if="uploadLoading && progressInfo.percent !== 100"
          class="picture-card-upload-status"
        >
          {{ $t('components.fileupload.uploadLoading') }} </p>
        <p
          v-if="progressInfo.percent === 100 && !uploadStatusMap.INFO_ERROR"
          class="picture-card-upload-status is-success"
        >
          <em class="el-icon-success status-icon" />
          {{ $t('components.upload.uploadSuccess') }} </p>
        <p
          v-if="uploadStatusMap.INFO_ERROR"
          class="picture-card-upload-status is-error"
        >
          <em class="el-icon-error status-icon" />
          {{ $t('components.upload.uploadErr') }} <span
            class="trigger-upload themeColor"
            @click="triggerFromUploadList"
          >
            {{ $t('components.upload.clickReUpload') }} </span>
        </p>
      </div>

      <!--上传进度条-->
      <el-progress
        v-if="progress && progressInfo.view"
        :stroke-width="8"
        :percentage="progressInfo.percent"
        :color="uploadStatusMap.INFO_ERROR ? '#FF4A4D' : '#52C718'"
        :show-text="false"
        class="progress-bar"
      />
    </div>
  </div>
</template>

<script>
/**
 * 上传组件，可以单独调用
 */
import {
  LIST_TYPE,
  SCENE_TYPE,
  propsMixin,
  uploadPropsMixin,
  listPropsMixin,
  fileHasPreview,
  countFileNameLength,
  fileKeyOptionsConversion,
  fileListAttrsKeyConversion,
  checkMD5,
  chunkUpload,
  checkChunk,
  mergeFile
} from './util'
import { uploadConfig } from '@/config/sysConfig'
import CommonFileList from './common-file-list'
import CommonFileDragger from './common-file-dragger'
import SparkMD5 from 'spark-md5'
import { getDictItem } from '@/api/common'
import axios from 'axios'
export default {
  name: 'CommonFileUpload',

  components: { CommonFileList, CommonFileDragger },

  mixins: [
    propsMixin,
    uploadPropsMixin,
    listPropsMixin
  ],

  data () {
    return {
      // 上传状态：[STANDBY待机、PROGRESS上传中、VIEW查看、INFO_ERROR内容信息错误]
      uploadStatus: 'STANDBY',
      uploadLoading: false,
      progressInfo: {
        view: false,
        percent: 0
      },
      // 上传列表
      uploadFileList: [],
      // 文件列表
      fileListData: [],
      chunkSize: 2 * 1024 * 1024, // 分片大小20MB
      maxConcurrent: 3, // 最大并发数
      uploadQueue: [], // 上传队列
      activeUploads: 0, // 当前活跃上传数
      cancelToken: null, // 取消令牌
    }
  },

  computed: {
    // 混合上传入参，提供默认参数
    uploadExtraData () {
      return {
        uploadType: 'DEF',
        sourceType: 'WEB_APP',
        fileModular: 'base',
        fileFunction: 'commonFile',
        fileType: 'excel',
        ...(this.extraData || {})
      }
    },

    // 按钮配置混入默认配置
    buttonOptionsDefault () {
      return {
        title: this.$t('components.upload.uploadFile'),
        type: 'default',
        icon: 'el-icon-upload',
        disabled: false,
        ...(this.buttonOptions || {})
      }
    },

    // 拖拽上传配置混入默认配置
    draggerOptionsDefault () {
      return {
        width: '200px',
        height: '200px',
        title: this.$t('components.upload.uploadFile'),
        icon: 'el-icon-upload',
        tips: this.$t('components.import.dragFileUpload'),
        ...(this.draggerOptions || {})
      }
    },

    /* 支持字符串格式，格式化参数 */
    limitConversion () {
      return Number(this.limit)
    },

    // 上传文件校验配置混入默认配置
    validateOptionsDefault () {
      return {
        size: uploadConfig.size.default,
        // 为图片卡片列表样式配置默认值
        accept: this.listTypeMap.PICTURE_CARD ? uploadConfig.accept.picture : uploadConfig.accept.default,
        ...(this.validateOptions || {})
      }
    },

    // 文件字段映射转化
    fileKeyOptionsDefault () {
      return fileKeyOptionsConversion(this.fileKeyOptions)
    },

    // 拖拽根组件容器大小
    draggerWrapSize () {
      if (this.drag) {
        return {
          width: this.draggerOptionsDefault.width,
          height: this.draggerOptionsDefault.height
        }
      }
      return null
    },

    // 拖拽上传组件上传样式
    draggerUploadWrapStyle () {
      if (this.drag) {
        return {
          ...(this.draggerWrapSize || {}),
          order: this.isSeveral ? this.limitConversion : 0
        }
      }
      return null
    },

    // 文件列表样式
    fileListStyle () {
      return {
        'margin-top': this.showUploadWrap && this.listTypeMap.TEXT ? '0px' : '0',
        ...(this.drag && !this.isSeveral ? this.draggerWrapSize : {})
      }
    },

    // 状态
    uploadStatusMap () {
      return {
        STANDBY: this.uploadStatus === 'STANDBY',
        PROGRESS: this.uploadStatus === 'PROGRESS',
        VIEW: this.uploadStatus === 'VIEW',
        INFO_ERROR: this.uploadStatus === 'INFO_ERROR'
      }
    },

    // 列表样式
    listTypeMap () {
      return {
        TEXT: this.listType === LIST_TYPE.TEXT,
        PICTURE_CARD: this.listType === LIST_TYPE.PICTURE_CARD
      }
    },

    // 校验提示文本
    validateOptionsString () {
      let string = this.$t('components.upload.uploadFillErr')
      if (this.validateOptionsDefault) {
        const { accept, size } = this.validateOptionsDefault
        if (accept) {
          let canUploadFiltType = this.$t('components.upload.canUploadFiltType')
          string += `${canUploadFiltType}${this.validateOptionsDefault.accept.map(item => item.replace(/\./, '')).join('/')}`
        }
        if (size) {
          // 文件大小暂时统一显示MB
          let sizeMb = ''
          if (this.fileSizeTipWithMb) {
            sizeMb = `${(size / 1024).toFixed(2)}MB`
          } else {
            sizeMb = `${size}KB`
          }
          string += this.$t('components.upload.uploadMaxSize') + `${sizeMb}` + this.$t('components.import.file') // `，最大可上传${sizeMb}文件`
        }
      }
      return string
    },

    // 是否支持多文件上传
    isSeveral () {
      return this.limitConversion > 1
    },

    // 是否显示上传组件
    showUploadWrap () {
      return !this.fileDisabled &&
        !this.readonly &&
        (
          this.uploadStatusMap.STANDBY ||
          (this.isSeveral && this.fileListData.length < this.limitConversion)
        ) && !this.uploadStatusMap.PROGRESS
    },

    // 是否显示文件列表
    showFileList () {
      return this.uploadStatusMap.VIEW || this.isSeveral
    },

    // 是否显示进度条
    showProgressWrap () {
      return !this.fileDisabled && !this.readonly && (this.uploadStatusMap.PROGRESS || this.uploadStatusMap.INFO_ERROR)
    },

    // 是否是场景附件类型
    isSceneFile () {
      return this.sceneType === SCENE_TYPE.SCENE_FILE
    }
  },

  watch: {
    defaultFile: {
      handler (newVal = {}) {
        // 表格中使用的话，列表重新渲染会导致参数混乱，所有行都会触发一次defaultFile watch，旧数据只能跟当前组件内的对比
        const oldVal = this.fileListData[0] || { fileId: '', fileName: '' }
        if (
          newVal[this.fileKeyOptionsDefault.idKey] === oldVal.fileId &&
          newVal[this.fileKeyOptionsDefault.nameKey] === oldVal.fileName
        ) {
          // 没有变更，不处理
          return
        }
        this.initFileListData([newVal])
      },
      deep: true,
      immediate: true
    },
    fileList: {
      handler (newVal = []) {
        // defaultFile优先
        if (!this.defaultFile) {
          this.initFileListData(fileListAttrsKeyConversion(newVal, this.fileListKey), true)
        }
      },
      immediate: true
    }
  },

  created () {
    // 同时监听readonly fileDisabled，变更为查看状态
    this.$watch(
      function () {
        return this.readonly || this.fileDisabled
      },
      function (newVal) {
        if (newVal) {
          this.uploadStatus = 'VIEW'
        } else {
          if (!this.fileListData.length) {
            this.uploadStatus = 'STANDBY'
          }
        }
      },
      { immediate: true }
    )
  },

  methods: {
    /**
     * 校验文件碎片是否已经上传过
     * @param {String} md5 文件指纹
     * @param {String} uploadId 用于合并文件碎片的唯一标识
     * @param {file} 文件流
     * @return {Boolean} true为上传过，否则为false
    */
    async checkChunk (md5, uploadId, file) {
      const params = {
        ...this.uploadExtraData,
        fingerprint: md5,
        uploadId,
        fileSourceName: file.nmae
      }
      const { data } = await checkChunk(params)
      return data.map(chunk => chunk.chunkNum)
    },
    /**
     * 文件上传
     * @param { Array } - chunkList 文件碎片集
     * @param { String } - md5文件指纹
     * @param { String } - 用于合并文件碎片的唯一标识
     * @param { file } 原始文件
     * @param { Array } - 已经上传成功的片
     * @return { Promise } - 文件碎片上传集合
    */
    async handleUpload (chunkList, md5, uploadId, fileFullname, sourceFile, filterChunks = []) {
      // 构造切片后端接口的数据结构
      const uploadChunkList = chunkList.map(({ file }, index) => {
        return {
          file,
          fingerprint: md5,
          chunkNum: index + 1,
          uploadId,
          chunkAmount: chunkList.length,
          chunkSize: file.size,
          fileSize: sourceFile.size,
          fileFullname,
          fileSourceName: sourceFile.name,
          ...this.uploadExtraData
        }
      })
      const resultChunkList = uploadChunkList.filter(chunkItem => !filterChunks.includes(chunkItem.chunkNum))
      const uploadRequestList = resultChunkList.map(chunk => {
        // 将对象转换为二进制表单数据进行传输
        const formData = new FormData()
        for (let key in chunk) {
          if (chunk[key]) {
            if (key === 'file') {
              formData.append(key, chunk[key], sourceFile.name)
            } else {
              formData.append(key, chunk[key])
            }
          }
        }
        return () => chunkUpload(formData)
      })
      let startIndex = 0
      while (startIndex < uploadRequestList.length) {
        const endIndex = startIndex + 3
        const res = await Promise.all(uploadRequestList.slice(startIndex, endIndex).map(item => item()))
        if (res.filter(resItem => resItem.code != 0).length) {
          throw new Error(this.$t('components.fileupload.uploadFailed'))
        } else {
          startIndex = endIndex
          const percent = this.progressInfo.percent
          if (percent < 100) {
            this.progressInfo.percent = (percent + Number((3 / uploadRequestList.length).toFixed(2)) * 100) > 100 ? 100 : (percent + Number((3 / uploadRequestList.length).toFixed(2)) * 100)
            console.log(this.progressInfo.percent, 'this.progressInfo.percent')
          }
        }
      }
      return true
    },
    /**
     * 文件切片
     * @param {file} - 文件
     * @param { Number } - chunkSize 文件片大小, 默认大小为5M
     * @return { Array } - chunkList 文件切片集
    */
    createChunk (file, size = 5 * 1024 * 1024) {
      const chunkList = []
      let curIndex = 0
      while (curIndex < file.size) {
        chunkList.push({
          file: file.slice(curIndex, curIndex + size)
        })
        curIndex += size
      }
      return chunkList
    },
    /**
     * 获取文件的MD5, 注意这里谷歌浏览器有最大文件限制当文件大于2G时浏览器无法读取文件
     * @param { file } 文件
     * @return { Promise }
    */
    getFileMD5 (file) {
      const fileReader = new FileReader()
      const spark = new SparkMD5.ArrayBuffer()
      return new Promise((resolve, reject) => {
        const chunkSize = 5 * 1024 * 1024
        const chunks = Math.ceil(file.size / chunkSize)
        let currentChunk = 0
        fileReader.onload = e => {
          spark.append(e.target.result)
          currentChunk++
          if (currentChunk < chunks) {
            loadNextChunk()
          } else {
            const fileMD5 = spark.end()
            resolve(fileMD5)
          }
        }
        fileReader.onerror = e => {
          reject(new Error(this.$t('components.fileupload.readFileErr')), e)
        }
        const loadNextChunk = () => {
          const start = currentChunk * chunkSize
          const end = Math.min(start + chunkSize, file.size)
          const readChunk = file.slice(start, end)
          fileReader.readAsArrayBuffer(readChunk)
        }
        loadNextChunk()
      })
    },
    /**
     * 校验MD5是否已经存在，判断是否还需要上传
     * @param {String} md5 - 文件MD5
     * @param {file} - 文件流
     * @return {Object} - { isUpfile: '***', fileUploadId: '***', uploadId: '***'} 返回对应文件的相应信息，根据对应的响应判断是否需要重传
     *
    */
    async checkMD5 (md5, file) {
      const params = {
        fingerprint: md5,
        ...this.uploadExtraData,
        fileSourceName: file.name
      }
      const { data } = await checkMD5(params)
      return data
    },
    /**
     * 更新文件视图
     * @param { Object } 附件信息
    */
    updateFileView (fileInfo) {
      setTimeout(() => {
        this.uploadStatus = 'VIEW'
        this.progressInfoUpdate({ view: false })
        // 清空上传列表
        this.uploadFileList = []
        if (this.isSeveral) {
          // 多文件
          this.fileListData.push(fileInfo)
        } else {
          // 单个
          this.fileListData.splice(0, 1, fileInfo)
        }

        this.emitFileChange()
        this.$emit('on-success', { ...this.emitFile() })
      }, 400)
    },
    // 创建loading效果
    createLoading (msg = 'Loading') {
      return this.$loading({
        lock: true,
        text: msg,
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.1)'
      })
    },
    /* 初始化数据 */
    initFileListData (fileList = [], isList = false) {
      // 编排映射文件对象
      const conversionFileList = fileList.map(item => {
        return {
          fileId: item[this.fileKeyOptionsDefault.idKey],
          fileName: item[this.fileKeyOptionsDefault.nameKey]
        }
      })

      if (!isList) {
        // 单文件
        const { fileId = '', fileName = '' } = conversionFileList[0] || {}

        if (!fileId) {
          // 文件错误，清空
          this.fileListData = []
          if (!this.fileDisabled && !this.readonly) {
            this.uploadStatus = 'STANDBY'
          }
          return
        }
        // 一个
        this.fileListData.splice(0, 1, {
          fileId,
          fileName,
          preview: fileHasPreview(fileName)
        })
      } else {
        // 多文件
        this.fileListData = conversionFileList
          .concat()
          .map(item => {
            return {
              fileId: item.fileId,
              fileName: item.fileName,
              preview: fileHasPreview(item.fileName)
            }
          })
      }

      // 非只读
      if (!this.fileDisabled && !this.readonly) {
        this.uploadStatus = 'VIEW'
      }
    },

    /* 上传前钩子 */
    async handleBeforeUpload (file) {
      const { name, size } = file
      // 强制校验
      if (countFileNameLength(name) > 100) {
        // 文件名字符长度不可超过100
        this.$message.warning(this.$t('components.fileupload.fileNameLengthErr'))
        return Promise.reject(new Error('bigFileUpload'))
      }

      if (this.validateOptionsDefault) {
        // 上传校验
        let valid = true
        if (
          this.validateOptionsDefault.accept &&
          !this.validateOptionsDefault.accept.find(item => {
            // 大小写不敏感
            return name.toUpperCase().endsWith(item.toUpperCase())
          })
        ) {
          valid = false
        }
        // if (this.validateOptionsDefault.size && (size / 1024) > this.validateOptionsDefault.size) {
        //   valid = false
        // }
        if (!valid) {
          // 不通过校验
          this.$message.warning(this.validateOptionsString)
          return Promise.reject(new Error('bigFileUpload'))
        }
      }
      // 获取大文件界限
      const { data } = await getDictItem('FILE_SIZE')
      const largeFileMinSize = data.find(item => item.dictItemCode === 'LARGE_FILE_MIN_SIZE')?.dictItemMark
      // 判断是否大于200MB
      if (size / 1024 / 1024 > largeFileMinSize) {
        // 获取文件MD5
        await Promise.reject(new Error('bigFileUpload'))
      }
      // 重置进度条
      this.progressInfo.percent = 0
      this.progressInfo.view = true

      // 文件添加到上传列表
      this.uploadFileList = [file].concat()

      // 上传中
      this.uploadStatus = 'PROGRESS'

      // 打开loading
      this.uploadLoading = true
    },
    /* 并发处理队列 */
    async processQueue(fileKey, file, chunks, totalChunks) {
      return new Promise((resolve) => {
        const uploadNext = async () => {
          if (this.activeUploads >= this.maxConcurrent) return
          if (chunks.length === 0) {
            if (this.activeUploads === 0) resolve();
            return;
          }

          const { index } = chunks.shift();
          this.activeUploads++;
          
          try {
            await this.uploadChunk(fileKey, file, index, totalChunks);
            this.progressInfo.percent = Math.round(
              ((totalChunks - chunks.length) / totalChunks) * 100
            );
          } finally {
            this.activeUploads--;
            uploadNext();
          }
        };

        // 启动并发
        for (let i = 0; i < this.maxConcurrent; i++) {
          uploadNext();
        }
      });
    },

    /* 单个分片上传 */
    async uploadChunk(fileKey, file, index, totalChunks) {
      const start = index * this.chunkSize;
      const end = Math.min(start + this.chunkSize, file.size);
      const chunk = file.slice(start, end);
      // 创建带.chunk后缀的文件名
      const chunkFileName = `${file.name}.chunk`

      const formData = new FormData();
      formData.append('fileKey', fileKey)
      formData.append('fileName', file.name)
      formData.append('fileType', file.type)
      formData.append('fileSize', file.size)
      formData.append('chunkIndex', index);
      formData.append('totalChunks', totalChunks);
      // 第三个参数指定上传文件的文件名
      formData.append('file', chunk, chunkFileName);
      
      await this.$http.post('/api-file/largerfile/uploadChunk', formData, {
        cancelToken: this.cancelToken.token,
        onUploadProgress: e => {
          // 精细进度计算（当前分片进度+已完成分片）
          const chunkProgress = e.loaded / e.total;
          const overallProgress = (
            (index + chunkProgress) / totalChunks * 100
          );
          this.progressInfo.percent = Math.round(overallProgress);
        }
      });
    },
    async onChange ({ raw: file }) {
      // 获取大文件界限
      const { data } = await getDictItem('FILE_SIZE')
      const largeFileMinSize = data.find(item => item.dictItemCode === 'LARGE_FILE_MIN_SIZE')?.dictItemMark
      const largeFileMaxSize = data.find(item => item.dictItemCode === 'LARGE_FILE_MAX_SIZE')?.dictItemMark
      if (file.size / 1024 / 1024 > largeFileMaxSize) {
        this.$message.warning(this.$t('cusEntry.tipMessage.overMaxSize', { size: `${largeFileMaxSize}M` }))
        return false
      }
      if (file.size / 1024 / 1024 > largeFileMinSize) {
        // 判断功能是否支持大附件
        const { data } = await getDictItem('LARGE_FILE_SCENE')
        const funCodeList = data.map(item => item.dictItemCode)
        if (!funCodeList.includes(this.extraData.fileFunction)) {
          this.$message.warning(this.$t('cusEntry.tipMessage.overTwoHundredTrillion', { size: `${largeFileMinSize}M` }))
          return false
        }
        // 文件切片添加loading效果
        const loadingIns = this.createLoading(this.$t('components.fileupload.uploading'))
        // 文件添加到上传列表
        // const fileMD5 = await this.getFileMD5(file)
        this.progressInfo.percent = 0
        this.progressInfo.view = true
        this.uploadStatus = 'PROGRESS'
        this.uploadFileList = [file].concat()
        // 校验MD5
        // const res = await this.checkMD5(fileMD5, file)
        // const {
        //   isUpfile,
        //   fileUploadId,
        //   uploadId,
        //   fileFullname
        // } = res
        // const newFileInfo = {
        //   fileId: fileUploadId,
        //   fileName: file.name
        // }
        // if (isUpfile === 'Y') {
        //   // 不需要重传
        //   // 延迟展示已上传文件
        //   this.$message.success(this.$t('purSettlementMod.uploadedSuccessfully'))
        //   this.progressInfo.percent = 100
        //   this.updateFileView(newFileInfo)
        //   loadingIns.close()
        // } else {
        //   // 获取当前MD5已经上传的成功的片
        //   const chunkNumList = await this.checkChunk(fileMD5, uploadId, file)
        //   // 正常分片上传
        //   // 获取需要切片的大小
        //   const { data } = await getDictItem('FILE_SIZE')
        //   const chunkSize = data.find(item => item.dictItemCode === 'CHUNK_FILE_SIZE')?.dictItemMark
        //   const chunkList = this.createChunk(file, chunkSize * 1024 * 1024)
        //   this.handleUpload(chunkList, fileMD5, uploadId, fileFullname, file, chunkNumList).then(async res => {
        //     const { data } = await mergeFile({ fingerprint: fileMD5, uploadId, fileFullname })
        //     // 延迟展示已上传文件
        //     newFileInfo.fileId = data
        //     this.$message.success(this.$t('purSettlementMod.uploadedSuccessfully'))
        //     this.updateFileView(newFileInfo)
        //     loadingIns.close()
        //   }).catch(err => {
        //     this.$message.warning(this.$t('components.eio.msgUploadFail'))
        //     this.uploadLoading = false
        //     this.uploadStatus = 'INFO_ERROR'
        //     this.$emit('on-error', { ...this.emitFile() })
        //     loadingIns.close()
        //     console.log(err)
        //   })
        // }

        // 大文件分片上传逻辑
        // 替代 MD5 的唯一标识（文件名 + 大小）
        const { name, type, size } = file
        const timestamp = new Date().getTime()
        let fileKey = `${name}-${size}-${timestamp}`
        // 简单替换特殊字符，避免文件系统问题
        fileKey = fileKey.replace(/[^a-zA-Z0-9_-]/g, '_')

        const checkResult = await this.$http({
          url: `/api-file/largerfile/checkFile?fileKey=${fileKey}`,
          method: 'GET',
          loading: true
        })
        const checkData = checkResult.data
        if (checkData.exists) {
          this.$message.warning('文件已存在，无需重复上传')
          return
        }
        // 计算切片总数
        const totalChunks = Math.ceil(size / this.chunkSize)
        const uploadAbortController = new AbortController()
        this.cancelToken = axios.CancelToken.source()
        try {
          // 1、检查已上传的切片
          const chunksResult = await this.$http({
            url: `/api-file/largerfile/checkChunks?fileKey=${fileKey}&totalChunks=${totalChunks}`,
            method: 'GET',
            cancelToken: this.cancelToken.token,
            loading: true
          })
          const chunksData = chunksResult.data
          const uploadedChunks = chunksData.uploadedChunks || []
          // 2. 创建分片任务队列
          const chunksToUpload = [];
          for (let i = 0; i < totalChunks; i++) {
            if (!uploadedChunks?.includes(i)) {
              chunksToUpload.push({ index: i });
            }
          }
          // 3. 并发控制上传
          await this.processQueue(fileKey, file, chunksToUpload, totalChunks);
          // 4. 合并分片
          if (chunksToUpload.length == 0) {
            const mergeResponse = await this.$http({
              url: `/api-file/largerfile/mergeChunks?fileKey=${fileKey}&fileName=${name}&fileType=${type}&fileSize=${size}&totalChunks=${totalChunks}`,
              method: 'POST',
              data: {},
              signal: uploadAbortController.signal,
              cancelToken: this.cancelToken.token,
              loading: true
            })
            const newFileInfo = { fileId: mergeResponse.data.fileuploadId, fileName: name }
            this.$message.success(this.$t('purSettlementMod.uploadedSuccessfully'))
            this.updateFileView(newFileInfo)
            loadingIns.close()
          }
        } catch (err) {
          this.$message.warning(this.$t('components.eio.msgUploadFail'))
          this.uploadLoading = false
          this.uploadStatus = 'INFO_ERROR'
          this.$emit('on-error', { ...this.emitFile() })
          loadingIns.close()
          console.log(err)
        }
      }
    },
    /* 上传文件超过个数 */
    handleOnExceed (file, fileList) {
      this.$message.warning(this.$t('components.upload.onlyUpload') + `${this.limitConversion}` + this.$t('components.upload.fileCount'))

      this.$emit('on-exceed', { file, fileList })
    },

    /* 上传过程事件 */
    handleOnProgress (event) {
      this.progressInfo.percent = Math.abs(event.percent.toFixed(0))

      this.$emit('on-progress', { event })
    },

    /* 上传失败 请求错误 */
    handleUploadError (message) {
      this.uploadLoading = false
      this.uploadStatus = 'INFO_ERROR'
      // 附件上传失败，请检查附件后重新上传
      this.$message.error((!message || typeof message === 'object') ? this.$t('components.eio.msgUploadFail') : message)
      this.$emit('on-error', { ...this.emitFile() })
    },

    /* 上传成功 但后端可能会返回错误 */
    handleUploadSuccess (res) {
      console.log(document.getElementsByClassName('el-upload__input'))
      this.uploadLoading = false

      const { code, data, message } = res
      if (code !== '0' || !data) {
        // 上传失败
        this.handleUploadError(message)
        return
      }

      this.$message.success(this.$t('components.upload.uploadSuccess'))

      // 延迟展示已上传文件
      setTimeout(() => {
        this.uploadStatus = 'VIEW'
        this.progressInfoUpdate({ view: false })
        // 清空上传列表
        this.uploadFileList = []

        const file = {
          // 冗余所有字段
          ...data,
          fileId: data.fileuploadId,
          fileName: data.fileSourceName,
          // 判断能否使用文件预览功能
          preview: fileHasPreview(data.fileSourceName)
        }

        if (this.isSeveral) {
          // 多文件
          this.fileListData.push(file)
        } else {
          // 单个
          this.fileListData.splice(0, 1, file)
        }

        this.emitFileChange()
        this.$emit('on-success', { ...this.emitFile() })
      }, 400)
    },

    /* 上传进度条数据变更统一判断 */
    progressInfoUpdate (values) {
      if (!this.progress) {
        return
      }

      this.progressInfo = {
        ...this.progressInfo,
        ...values
      }
    },

    /* 上传列表移除文件 用于文件上传错误 */
    removeFileInUploadList (index) {
      this.uploadFileList.splice(index, 1)
      // 手动更新一下el-upload组件的file-list
      this.fileListData = this.fileListData.concat()
      this.uploadStatus = 'STANDBY'
      this.$emit('on-standby', { ...this.emitFile() })
    },

    /* 移除文件 */
    removeFile (_file, $index) {
      this.fileListData.splice(_file.$index, 1)
      this.uploadStatus = 'STANDBY'
      this.$emit('on-remove', { ...this.emitFile() })
      // 回调文件变更
      this.emitFileChange()
    },

    /* 手动触发文件选择，重新上传 */
    triggerUpload (file, $index) {
      // 移除旧文件
      this.removeFile(file, $index)
      this.$nextTick(() => {
        // 手动触发标签点击事件
        if (this.drag) {
          this.$refs.commonFileDragger.$el.click()
        } else {
          this.$refs.uploadButton.$el.click()
        }
      })
    },

    /* 图片卡片上传列表重新上传 */
    triggerFromUploadList () {
      this.uploadStatus = 'STANDBY'
      this.$emit('on-standby', { ...this.emitFile() })
    },

    /* 文件变更回调 */
    emitFileChange () {
      // 返回整个文件
      this.$emit('on-change', { ...this.emitFile() })
    },

    /* 判断返回文件，单文件返回对象，多文件返回数组 */
    emitFile () {
      if (this.isSeveral) {
        return {
          fileList: this.fileListData
        }
      }
      return {
        file: this.fileListData[0] || null
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import "./common-file.scss";

.common-file-wrap {
  // 默认行内块级元素，宽度100%
  display: inline-block;
  width: 100%;
  min-height: $file-wrap-height;
  // 拖拽上传
  &.is-dragger {
    display: flex;
    .upload-wrap {
      width: 100%;
      height: 100%;
      :deep(.el-upload) {
        width: 100%;
        height: 100%;
        .el-upload-dragger {
          width: 100%;
          height: 100%;
        }
      }
    }
  }
  // 场景附件
  &.align-left {
    // 强制左对齐
    text-align: left;
    padding: 10px 0;
  }
  // 多张图片展示
  &.multiple-picture-card{
    display: flex;
    .multiple-picture-upload-btn{
      margin-right: 10px;
      &.el-icon-plus:before {
        font-size: 30px;
      }
    }
  }
}

//上传组件
.upload-wrap {
  .upload-button {
    height: $file-wrap-height;
    padding: 7px;
  }
}

// 上传过程
.progress-wrap {
  display: flex;
  flex-direction: column;
  height: $file-wrap-height;
  //文本列表
  .upload-files-wrap {
    width: 100%;
    height: $file-wrap-height;
    // 有进度条模式
    &.is-progress {
      height: $progress-file-wrap-height;
      .file-item-wrap {
        height: $progress-file-wrap-height;
        .file-item-name {
          line-height: $progress-file-wrap-height;
        }
      }
    }
    &:hover {
      // 显示删除按钮
      .operation-icon.el-icon-close {
        display: inline-block;
      }
      // 隐藏失败图标
      .status-icon.el-icon-error {
        display: none;
      }
    }
    // 删除按钮默认隐藏
    .operation-icon.el-icon-close {
      display: none;
    }
  }

  // 图片卡片列表
  .upload-files-picture-card-wrap {
    .picture-card-upload-status {
      &.is-success {
        color: $success-color;
      }
      &.is-error {
        color: $error-color;
        .trigger-upload {
          // color: $link-color;
          cursor: pointer;
        }
      }
    }
  }
  &.is-picture-card {
    @include picture-card-wrap;
    justify-content: center;
    .progress-bar {
      padding-left: 0;
    }
  }

  // 进度条
  .progress-bar {
    width: 100%;
    padding-left: 13px;
    :deep(.el-progress-bar) {
      padding-right: 0;
      margin-right: 0;
    }
    :deep(.el-progress__text i) {
      font-size: 12px;
    }
  }
}
.cell {
  .common-file-wrap{
    &.align-left{
      padding: 0 !important;
    }
  }
}
</style>
