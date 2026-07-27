<template>
  <div
    v-if="hasPermission"
    class="base-import"
  >
    <!--外部按钮-->
    <AuthorityButton
      :code="code"
      :type="type"
      :class="btnClassName"
      :disabled="disabled"
      @click.prevent="openUploadDialog"
    >
      {{ titleText }}
    </AuthorityButton>

    <SrmDialog
      v-if="importDialogVisible"
      :visible.sync="importDialogVisible"
      :title="importDialogTitle || titleText"
      size="middle"
      append-to-body
      destroy-on-close
      close-on-press-escape
      :close-on-click-modal="false"
      class="base-import-dialog"
      @close="handleDialogClose"
    >
      <div
        v-if="showTemplateDownLoad"
        class="import-primary-alert"
      >
        <div class="alert-icon">
          <em class="el-icon-info" />
        </div>
        <div class="alert-info">
          {{ $t('components.import.templateTip') }}
        </div>
        <div v-if="showTemplate" class="alert-operation">
          <el-button
            type="text"
            @click="downloadTemplate"
          >
            {{ $t('components.eio.template') }}
          </el-button>
        </div>
      </div>

      <el-upload
        ref="upload"
        :class="['import-upload', { 'import-upload-disabled': !allowUpload }]"
        :show-file-list="false"
        :headers="headers"
        with-credentials
        drag
        :disabled="!allowUpload"
        :data="uploadExtraData"
        :action="upLoadUrl"
        :http-request="uploadHttpRequest"
        :on-error="handleUploadError"
        :before-upload="handleBeforeUpload"
        :on-progress="handleOnProgress"
      >
        <em class="import-upload-icon" :class="uploadContent.icon" />
        <p class="import-upload-text">
          {{ uploadContent.text }}
        </p>

        <template v-if="uploadContent.tips">
          <p
            v-if="uploadStatus !== 'INFO_ERROR' && uploadStatus !== 'INFO_ASYNC_IMPORT'"
            class="import-upload-tips"
            v-html="uploadContent.tips"
          />
          <!--信息错误，需要下载错误文件-->
          <p v-else class="import-upload-tips err-with-file">
            <el-tooltip
              :disabled="isDisabledToolTip"
              :class="{'with-file-style': uploadErrorFile}"
              effect="dark"
              popper-class="err-info-tooltip"
              :content="resErrorMessage"
              placement="top"
            >
              <span>{{ resErrorMessage }}</span>
            </el-tooltip>
            <template v-if="uploadErrorFile">
              <span>{{ $t('components.import.dianji') }}</span>
              <span
                class="tips-primary themeColor"
                @click.prevent.stop="downloadUploadErrorFile"
              >
                {{ $t('components.import.downloaErrFile') }} </span>
            </template>
          </p>
        </template>
      </el-upload>
      <!-- 下载提示 -->
      <div v-if="isShowTip" class="downLoad-tips">
        {{ $t('components.import.asyncTaskTip') }} <span
          class="tips-primary themeColor"
          @click.prevent.stop="downloadCenter"
        >
          {{ $t('route.exportCenter') }} </span>
      </div>

      <!--上传进度条-->
      <el-progress
        v-if="showProgress && progressPercent > 0 && progressPercent < 100"
        :text-inside="true"
        :stroke-width="14"
        :percentage="progressPercent"
        class="progress"
      />

      <div slot="footer">
        <!--重新上传 失败可重新上传-->
        <el-button
          v-if="['INFO_ERROR', 'ACCEPT_ERROR'].includes(uploadStatus)"
          @click="handleReupload"
        >
          {{ $t('components.eio.reupload') }}
        </el-button>

        <!--完成-->
        <el-button
          type="primary"
          :disabled="['STANDBY', 'PROGRESS'].includes(uploadStatus)"
          @click="handleComplete('complete')"
        >
          {{ $t('common.confirm') }}
        </el-button>
      </div>
    </SrmDialog>
  </div>
</template>

<script>
import { getToken } from '@/utils/auth'
import { downloadFileLink, downloadWithParam } from 'lib@/utils/file'
import { sysPrefix } from '@/config/ipConfig'
import { uploadConfig } from '@/config/sysConfig'
import axios from 'axios'
import { getMenuInfo } from '@/utils/menu-auth'

export default {
  name: 'BaseImport',

  props: {
    // 是否展示跳转导出中心提示
    isShowTip: {
      type: Boolean,
      default: true
    },
    // 上传API路径
    upLoadUrl: {
      type: String,
      required: true
    },
    // 是否显示模板下载
    showTemplateDownLoad: {
      type: Boolean,
      default: true
    },
    // 按钮样式
    btnClassName: {
      type: [String, Object, Array],
      default: () => ''
    },
    // 权限控制按钮显示
    code: {
      type: String,
      default: ''
    },
    // 按钮类型
    type: {
      type: String,
      default: 'primary'
    },
    // 按钮文案
    title: {
      type: String,
      default: ''
    },
    // 文件上传附带参数
    extraData: {
      type: Object,
      default: () => null
    },
    // 上传按钮是否禁用
    disabled: {
      type: Boolean,
      default: false
    },
    // 是否显示文件上传过程
    showProgress: {
      type: Boolean,
      default: true
    },
    // 上传超时 默认35000毫秒
    timeout: {
      type: Number,
      default: 350000
    },
    // 弹窗标题
    importDialogTitle: {
      type: String,
      default: ''
    },
    /**
     * 上传前文件校验配置
     * accept {Array} 文件类型只判断文件后缀 ['.doc', '.xlsx']
     * size {Number} 文件大小 单位KB 与MB换算 KB / 1024 = MB
     */
    validateOptions: {
      type: Object,
      // validator: value => {
      //   if (!value || typeof value !== 'object') {
      //     return false
      //   }
      //   if (value.accept && !Array.isArray(value.accept)) {
      //     return false
      //   }
      //   return !(value.size && !isNumber(value.size))
      // },
      default: function () {
        return {
          accept: uploadConfig.accept.import,
          size: uploadConfig.size.import
        }
      }
    },
    /**
     * 模板下载配置
     * downloadUrl {String} 下载地址
     * fileName {String} 文件名称
     */
    downloadTemplateOptions: {
      type: Object,
      validator: value => {
        if (value) {
          if (
            !value.downloadUrl ||
            !value.fileName ||
            typeof value.downloadUrl !== 'string' ||
            typeof value.fileName !== 'string'
          ) {
            return false
          }
        }
        return true
      },
      default: () => null
    },
    // 是否展示模板下载
    showTemplate: {
      type: Boolean,
      default: true
    }
  },

  data () {
    return {
      headers: {
        Authorization: `Bearer ${getToken()}`
      },
      uploadErrorFile: null,
      importDialogVisible: false,
      progressPercent: 0,
      uploadLoading: null,
      // 上传状态：[STANDBY待机、PROGRESS上传中、SUCCESS成功、INFO_ERROR内容信息错误、ACCEPT_ERROR类型错误]
      uploadStatus: 'STANDBY',
      uploadResponse: null,
      hasClickComplete: false,
      resErrorMessage: this.$t('components.import.filInfoError'),
      isDisabledToolTip: true // 默认禁用
    }
  },

  computed: {
    titleText () {
      return this.title || this.$t('common.import')
    },
    // 校验提示文本
    validateOptionsString () {
      let string = ''
      if (this.validateOptions) {
        const { accept, size } = this.validateOptions
        if (accept) {
          string = this.$t('components.import.fileType') + `${this.validateOptions.accept.map(item => item.replace(/\./, '')).join('/')}`
        }
        if (size) {
          // 文件大小暂时统一显示MB
          const sizeMb = `${(size / 1024).toFixed(2)}MB`
          if (accept) {
            string += this.$t('components.import.maxSize') + `${sizeMb}`
          } else {
            string += this.$t('components.import.theMaxSize') + `${sizeMb}` + this.$t('components.import.file')
          }
        }
      }
      return string
    },

    // 导入组件图标、文本、提示
    uploadContent () {
      // 默认待机状态
      let content = {
        icon: 'el-icon-upload',
        text: this.$t('components.import.dragFileUpload'),
        tips: this.validateOptionsString
      }
      switch (this.uploadStatus) {
      case 'STANDBY':
        // 待机
        if (!this.validateOptions) {
          // 没配置，不显示提示
          content.tips = this.validateOptionsString
        }
        break
      case 'PROGRESS':
        // 上传中
        content.icon = 'el-icon-loading'
        content.text = this.$t('components.eio.importing')
        break
      case 'SUCCESS':
        // 成功
        content = {
          ...content,
          icon: 'el-icon-success',
          text: this.$t('components.import.fileImportSuccess'),
          tips: this.$t('components.import.comfirmDataImport')
        }
        break
      case 'INFO_ERROR':
        // 内容信息错误
        content = {
          ...content,
          icon: 'el-icon-error',
          text: this.$t('components.import.importErr'),
          tips: this.$t('components.import.importErr')
        }
        break
      case 'INFO_ASYNC_IMPORT':
        // 内容信息错误
        content = {
          ...content,
          icon: 'el-icon-warning',
          text: this.$t('components.import.swithBackImport'),
          tips: this.$t('components.import.maxDataTip')
        }
        break
      case 'ACCEPT_ERROR':
        // 类型错误。需要配置
        content = {
          ...content,
          icon: 'el-icon-error',
          text: this.$t('components.import.importErr'),
          tips: this.$t('components.import.fileTypeUnfix') + `${this.validateOptionsString}`
        }
        break
      default:
      }
      return content
    },

    // 根据code判断权限
    hasPermission () {
      if (!this.code) {
        return true
      }
      const userInfo = this.$store.getters.user.userInfo
      const { buttonPermission = {} } = userInfo
      const permissions = buttonPermission
      if (permissions[this.code]) {
        return permissions[this.code] == 'Y'
      } else {
        return true
      }
    },

    // 允许上传
    allowUpload () {
      return this.uploadStatus === 'STANDBY'
    },

    // 混合上传入参，提供默认参数
    uploadExtraData () {
      return {
        uploadType: 'DEF',
        sourceType: 'WEB_APP',
        fileModular: 'base',
        fileFunction: 'import',
        fileType: 'excel',
        ...(this.extraData || {})
      }
    }
  },

  watch: {
    importDialogVisible (val) {
      if (val) {
        // 打开重置数据
        this.handleReupload()
      }
    }
  },

  methods: {
    /* 上传前钩子 */
    handleBeforeUpload (file) {
      if (this.validateOptions) {
        // 上传校验
        let valid = true
        if (this.validateOptions.accept && !this.validateOptions.accept.find(item => file.name.endsWith(item))) {
          valid = false
        }
        if (this.validateOptions.size && file.size / 1024 > this.validateOptions.size) {
          valid = false
        }
        if (!valid) {
          // 不通过校验
          this.uploadStatus = 'ACCEPT_ERROR'
          return false
        }
      }

      // 上传中
      this.uploadStatus = 'PROGRESS'
      // 打开loading
      this.uploadLoading = this.$pageLoading.open()
    },

    /* 覆盖el-upload上传方式 */
    uploadHttpRequest (fileObj) {
      const { headers, data } = fileObj
      const formData = new FormData()
      formData.append('file', fileObj.file)

      if (data) {
        for (const i in data) {
          if (data[i]) {
            formData.append(i, data[i])
          }
        }
      }
      let menuInfo = getMenuInfo()
      axios({
        headers: {
          ...headers,
          'X-Fun-Info': menuInfo.secretKey
        },
        timeout: this.timeout,
        url: sysPrefix() + this.upLoadUrl,
        method: 'post',
        data: formData,
        // 处理原生进度事件
        onUploadProgress: progressEvent => {
          // 触发onProgress钩子，计算并显示进度条
          fileObj.onProgress({
            percent: ((progressEvent.loaded / progressEvent.total) * 100) | 0
          })
        }
      }).then(res => {
        this.handleUploadSuccess(res.data)
      }).catch(error => {
        this.handleUploadError(error)
      })
    },

    /* 文件上传时的钩子 */
    handleOnProgress (event) {
      this.progressPercent = Math.abs(event.percent.toFixed(0))
    },

    /* 上传请求成功 还需要进一步判断接口返回状态 */
    handleUploadSuccess ({ code, message, data }) {
      this.uploadLoading.close()
      const isError = code !== '0'
      const resStatus = data.status || 'Y'
      const mesByResStatus = data.message || message
      const asyncImport = data.asyncImport || 'N' // 异步导入
      const asyncNew = data.async || false // 新版异步标识，统一了异步导入导出

      // this.$message({
      //   type: isError || resStatus === 'N' ? 'error' : 'success',
      //   // message: isError ? message : mesByResStatus
      //   message: isError ? '导入失败' : '导入成功'
      // })

      if (isError || resStatus === 'N') {
        if (asyncImport == 'Y' || asyncNew) {
          this.uploadStatus = 'INFO_ASYNC_IMPORT'
        } else {
          this.uploadStatus = 'INFO_ERROR'
        }
        this.$refs.upload.clearFiles()
        this.resErrorMessage = mesByResStatus
        let textWidth = this.getTextWidth(mesByResStatus)
        // 报错文字长度超过 550
        if (textWidth > 550) {
          this.isDisabledToolTip = false
        } else {
          this.isDisabledToolTip = true
        }
      } else {
        // 无错误，成功
        this.uploadResponse = data
        this.uploadStatus = 'SUCCESS'
      }

      if (isError) {
        // code错误 暂时没发现这种场景
        try {
          const errorMsg = JSON.parse(message)
          if (errorMsg.fileuploadId) {
            this.resErrorMessage = mesByResStatus == 'error' ? this.$t('components.import.importFillErr') : mesByResStatus
            this.uploadErrorFile = errorMsg
          }
        } catch (e) {
          console.log(e)
        }
      } else if (resStatus === 'N' && data.fileuploadId) {
        this.resErrorMessage = mesByResStatus == 'error' ? this.$t('components.import.importFillErr') : mesByResStatus
        // 文件信息错误
        this.uploadErrorFile = {
          fileuploadId: data.fileuploadId,
          fileSourceName: data.fileName
        }
      }
    },

    /* 上传失败 请求错误 */
    handleUploadError () {
      this.uploadLoading.close()
      this.uploadStatus = 'INFO_ERROR'
      // 附件上传失败，请检查附件后重新上传
      this.$message.error(this.$t('components.eio.msgUploadFail'))
    },

    /* 点击导入按钮 */
    openUploadDialog () {
      this.$emit('beforeUpload')
      this.importDialogVisible = true
    },

    /* 下载模板 */
    downloadTemplate () {
      // 存在下载配置就不需要回调直接发起下载
      if (this.downloadTemplateOptions && this.downloadTemplateOptions.downloadUrl) {
        downloadFileLink(
          this.downloadTemplateOptions.downloadUrl,
          this.downloadTemplateOptions.fileName
        ).catch(() => {
          this.$message.error(this.$t('components.eio.downloadFail'))
        })
      } else {
        this.$emit('downloadTemplate')
      }
    },

    /* 导出错误，下载错误结果文件 */
    downloadUploadErrorFile () {
      downloadWithParam(
        this.uploadErrorFile.fileuploadId,
        this.uploadErrorFile.fileSourceName
      ).catch(() => {
        // 下载失败
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
      this.$emit('downLoadResult', this.uploadErrorFile)
    },
    // 跳转到下载中心
    downloadCenter () {
      this.$router.push({ name: 'exportCenter' })
      this.importDialogVisible = false
    },

    /* 重新上传 */
    handleReupload () {
      this.uploadStatus = 'STANDBY'
      this.uploadErrorFile = null
      this.progressPercent = 0
    },

    /* 完成 */
    handleComplete () {
      // 标记点击完成按钮，避免在弹窗关闭回调重复发起回调
      this.hasClickComplete = true
      this.importDialogVisible = false
      this.handleEmit()
    },

    /* 关闭弹窗 */
    handleDialogClose () {
      // 关闭弹窗，如果非点击完成按钮，发起回调
      if (!this.hasClickComplete) {
        this.handleEmit()
      }
    },

    /* 处理成功失败回调 */
    handleEmit () {
      if (this.uploadStatus === 'SUCCESS') {
        // 成功
        this.$emit('handleSuccess', this.uploadResponse)
      } else if (this.uploadStatus !== 'STANDBY') {
        // 错误
        this.$emit('handleError')
      }
    },
    // 获取提示文字长度
    getTextWidth (str) {
      let canvas = document.createElement('canvas')
      let context = canvas.getContext('2d')
      context.font = '12px Arial'
      let metrics = context.measureText(str)
      return metrics.width
    }
  }
}
</script>

<style lang="scss" src="./import.scss" scoped></style>
