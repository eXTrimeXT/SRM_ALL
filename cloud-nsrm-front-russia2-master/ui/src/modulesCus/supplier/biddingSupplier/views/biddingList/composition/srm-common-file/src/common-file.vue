<template>
  <!--$attrs.disabled响应外部禁用入参，现阶段用于场景附件，只作额外参数接收，不作为标准入参-->
  <component
    :is="componentName"
    v-bind="$props"
    :readonly="readonly || !!$attrs.disabled"
    v-on="onEvent"
  >
    <!--提供default插槽-->
    <slot />
  </component>
</template>

<script>
/**
 * 组件封装，提供四种模式。
 * default 默认模式 只提供上传组件
 * form-item 表单 封装了表单块组件
 * table-column 表格 封装表格列组件
 * link 列表功能
 */
import {
  COMPONENT_TYPE,
  SCENE_TYPE,
  propsMixin,
  uploadPropsMixin,
  formItemPropsMixin,
  tableColumnPropsMixin,
  listPropsMixin,
  fileKeyOptionsConversion
} from './util'
import CommonFileUpload from './common-file-upload'
import CommonFileFormItem from './common-file-form-item'
import CommonFileTableColumn from './common-file-table-column'
import CommonFileList from './common-file-list'

export default {
  name: 'SrmCommonFile',

  // 当前入口组件包含所有入参
  mixins: [
    propsMixin,
    uploadPropsMixin,
    formItemPropsMixin,
    tableColumnPropsMixin,
    listPropsMixin
  ],

  data () {
    return {
      // 响应CommonFile事件
      onEvent: {
        'on-change': this.onChange,
        'on-standby': value => this.$emit('on-standby', value),
        'on-success': value => this.$emit('on-success', value),
        'on-error': value => this.$emit('on-error', value),
        'on-progress': value => this.$emit('on-progress', value),
        'on-remove': value => this.$emit('on-remove', value),
        'on-exceed': value => this.$emit('on-exceed', value)
      }
    }
  },

  computed: {
    // 使用组件
    componentName () {
      let component = null
      switch (this.type) {
        case COMPONENT_TYPE.DEFAULT:
          component = CommonFileUpload
          break
        case COMPONENT_TYPE.FORM_ITEM:
          component = CommonFileFormItem
          break
        case COMPONENT_TYPE.TABLE_COLUMN:
          component = CommonFileTableColumn
          break
        case COMPONENT_TYPE.LINK:
          component = CommonFileList
      }

      return component
    }
  },

  methods: {
    /* 文件改变回调 */
    onChange (value) {
      this.$emit('on-change', value)

      if (this.sceneType === SCENE_TYPE.SCENE_FILE) {
        // 额外响应场景附件功能，其他正常调用的组件无需理会
        this.changeToSceneFile(value)
      }
    },

    /* 额外处理场景附件回调 */
    changeToSceneFile (value) {
      // 额外响应场景附件功能，其他正常调用的组件无需理会
      const { file = {}, fileList = [] } = value || {}
      const keyOptions = fileKeyOptionsConversion(this.fileKeyOptions)

      if (!this.multiple) {
        // 单文件
        const { fileId = '', fileName } = file || {}
        this.$emit('input', fileId)
        this.$emit('change', fileId, fileId ? {
          [keyOptions.idKey]: fileId,
          [keyOptions.nameKey]: fileName,
          fileSourceName: fileName
        } : null)

        this.$emit('on-change-src', fileId, value, keyOptions)
      }
      if (this.multiple) {
        // 多文件
        const multiData = fileList.map(item => {
          return {
            ...item,
            [keyOptions.idKey]: item.fileId,
            [keyOptions.nameKey]: item.fileName,
            fileSourceName: item.fileName
          }
        })
        this.$emit('change', multiData)

        this.$emit('on-change-list', multiData, keyOptions)
      }
    }
  }
}
</script>
