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
  fileListAttrsKeyConversion
} from './util'
import { uploadConfig } from '@/config/sysConfig'
import CommonFileList from './common-file-list'
import CommonFileDragger from './common-file-dragger'

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
      fileListData: []
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
        )
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
    handleBeforeUpload (file) {
      const { name, size } = file
      // 强制校验
      if (countFileNameLength(name) > 100) {
        // 文件名字符长度不可超过100
        this.$message.warning(this.$t('components.fileupload.fileNameLengthErr'))
        return false
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
        if (this.validateOptionsDefault.size && (size / 1024) > this.validateOptionsDefault.size) {
          valid = false
        }

        if (!valid) {
          // 不通过校验
          this.$message.warning(this.validateOptionsString)
          return false
        }
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
      this.fileListData.splice($index, 1)
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
