<template>
  <el-form-item
    :prop="formItemOptionsDefault.prop"
    :class="['form-item-wrap', inlineForm ? 'inline' : '']"
  >
    <template #label>
      <div class="label-slot">
        <div class="label-slot-left">
          <span>{{ formItemOptionsDefault.label }}</span>
          <el-tooltip
            v-if="uploadTips"
            effect="dark"
            :content="uploadTips"
            placement="top"
          >
            <em class="el-icon-warning-outline tip-icon" />
          </el-tooltip>
        </div>
        <!--上传成功、上传失败-->
        <div
          v-if="showUploadStatusLabel"
          :class="['label-slot-right', 'upload-status-color', uploadStatus.toLowerCase()]"
        >
          {{ uploadStatusLabel }}
        </div>
      </div>
    </template>

    <!--非只读 非禁用 调用上传组件-->
    <CommonFileUpload
      v-if="!readonly && !fileDisabled"
      v-bind="$props"
      v-on="onEvent"
    />

    <!--只读 禁用 调用列表组件-->
    <CommonFileList
      v-else
      v-bind="$props"
    />
  </el-form-item>
</template>

<script>
/**
 * 表单模式
 */
import { propsMixin, uploadPropsMixin, listPropsMixin, formItemPropsMixin } from './util'
import CommonFileUpload from './common-file-upload'
import CommonFileList from './common-file-list'

export default {
  name: 'CommonFileFormItem',

  components: {
    CommonFileUpload,
    CommonFileList
  },

  mixins: [propsMixin, uploadPropsMixin, listPropsMixin, formItemPropsMixin],

  data () {
    return {
      // 上传状态：[STANDBY待机、PROGRESS上传中、SUCCESS成功、ERROR错误失败]
      uploadStatus: 'STANDBY',
      // 响应CommonFile事件
      onEvent: {
        'on-change': this.uploadFileChange,
        'on-standby': this.uploadFileStandby,
        'on-success': this.uploadFileSuccess,
        'on-error': this.uploadFileError,
        'on-progress': this.uploadFileOnProgress,
        'on-exceed': this.uploadFileOnExceed
      },
      inlineForm: false
    }
  },

  computed: {
    // 表单配置混入默认配置
    formItemOptionsDefault () {
      return {
        label: this.$t('components.upload.fileUpload'),
        prop: this.fileKeyOptions.idKey,
        showUploadStatus: false,
        ...(this.formItemOptions || {})
      }
    },

    // 组件状态
    uploadStatusMap () {
      return {
        STANDBY: this.uploadStatus === 'STANDBY',
        PROGRESS: this.uploadStatus === 'PROGRESS',
        SUCCESS: this.uploadStatus === 'SUCCESS',
        ERROR: this.uploadStatus === 'ERROR'
      }
    },

    /* 上传状态文本 */
    uploadStatusLabel () {
      if (this.uploadStatusMap.SUCCESS) {
        return this.$t('components.upload.uploadSuccess')
      }
      if (this.uploadStatusMap.ERROR) {
        return this.$t('components.upload.uploadErr')
      }
      if (this.uploadStatusMap.PROGRESS) {
        return this.$t('components.upload.uploading')
      }
      return ''
    },

    // 是否显示上传提示
    showUploadStatusLabel () {
      return this.formItemOptionsDefault.showUploadStatus &&
        // 非行内表单
        !this.inlineForm &&
        (this.uploadStatusMap.SUCCESS || this.uploadStatusMap.ERROR)
    }
  },

  created () {
    // 根据表单组件的配置进行继承
    if ((this.$parent || {}).$parent) {
      const { inline, labelPosition } = this.$parent.$parent
      if (inline && labelPosition !== 'top') {
        // 行内表单并且label标签不在上面
        this.inlineForm = true
      }
    }
  },

  methods: {
    /* 文件变更 */
    uploadFileChange (value) {
      this.$emit('on-change', value)
    },

    /* 待机 */
    uploadFileStandby (value) {
      this.uploadStatus = 'STANDBY'
      this.$emit('on-standby', value)
    },

    /* 成功 */
    uploadFileSuccess (value) {
      this.uploadStatus = 'SUCCESS'
      this.$emit('on-success', value)
    },

    /* 失败 */
    uploadFileError (value) {
      this.uploadStatus = 'ERROR'
      this.$emit('on-error', value)
    },

    /* 上传中 */
    uploadFileOnProgress (value) {
      this.uploadStatus = 'PROGRESS'
      this.$emit('on-progress', value)
    },

    /* 文件数量超出 */
    uploadFileOnExceed (value) {
      this.$emit('on-progress', value)
    }
  }
}
</script>

<style lang="scss" scoped>
@import "./common-file.scss";

// 表单样式
.form-item-wrap {
  :deep(.el-form-item__label) {
    display: flex;
    .label-slot {
      display: flex;
      flex: 1;
      justify-content: space-between;
      .label-slot-left {
        .tip-icon {
          vertical-align: inherit;
          font-size: 12px;
          color: #96999C;
          margin-left: 5px;
        }
      }
      .label-slot-right {
        font-size: 12px;
      }
    }
  }

  .upload-status-color {
    &.success {
      color: $success-color;
    }
    &.error {
      color: $error-color;
    }
  }

  // 继承表单样式
  .el-form-item:first-child {
    margin-bottom: 12px;
    padding-left: 16px;
    padding-right: 16px;
  }
}

.form-item-wrap.inline {
  :deep(.el-form-item__label) {
    display: inline-block;
    .label-slot {
      display: inline-block;
    }
  }
}
</style>
