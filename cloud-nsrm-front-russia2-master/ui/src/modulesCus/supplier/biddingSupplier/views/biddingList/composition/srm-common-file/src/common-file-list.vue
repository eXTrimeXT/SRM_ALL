<template>
  <div :class="fileListWrapClass">
    <template v-if="fileListData.length > 0">
      <!--文本列表-->
      <template v-if="listTypeMap.TEXT">
        <div
          v-for="(item, index) in fileListData"
          :key="`fileItem-${index}`"
          :class="fileItemWrapClass(item)"
        >
          <!--图标-->
          <div class="file-item-icon-wrap">
            <em
              class="el-icon-paperclip file-item-icon themeColor"
              @click="showFilePreview(item)"
            />
          </div>

          <!--附件名称-->
          <div
            class="file-item-name themeColor"
            @click="showFilePreview(item)"
          >
            {{ item.fileName }}
          </div>

          <!--操作按钮-->
          <div class="file-item-operation">
            <!--下载 禁用不能下载-->
            <em
              v-if="!fileDisabled"
              class="el-icon-download operation-icon"
              @click="downloadFile(item)"
            />
            <!--删除 禁用 / 只读不能删除-->
            <em
              v-if="showClose"
              class="el-icon-close operation-icon"
              @click="removeFile(item, index)"
            />
          </div>
        </div>
      </template>

      <!--图片卡片列表-->
      <template v-if="listTypeMap.PICTURE_CARD">
        <div
          v-for="(item, index) in fileListData"
          :key="`fileItem-${index}`"
          class="file-item-picture-card-wrap"
          :style="draggerWrapSize || pictureStyleOptions"
        >
          <!--getImgSrc(item.fileId, item.fileName)-->
          <el-image
            :src="item.pictureCardImgSrc"
            fit="contain"
            lazy
            class="picture-card-img"
          />

          <!--遮罩功能层 禁用状态不显示-->
          <div
            v-if="!fileDisabled"
            class="picture-card-modal"
          >
            <template v-for="(itemIcon, indexIcon) in pictureCardModalOperationIcon">
              <el-tooltip
                :key="`icon-tooltip-${indexIcon}`"
                effect="dark"
                :content="itemIcon.content"
                placement="top"
                :class="['icon-tooltip', {'view-icon-hidden': itemIcon.icon == 'el-icon-view' && !item.preview}]"
              >
                <em
                  :class="['modal-operation-icon', itemIcon.icon]"
                  @click="pictureCardModalOperationClick(itemIcon, item, index)"
                />
              </el-tooltip>
            </template>
          </div>
        </div>
      </template>
    </template>

    <p
      v-else-if="emptyText"
      class="empty-tip"
    >
      <em class="el-icon-folder-delete" />
      {{ emptyText }}
    </p>

    <!--附件预览-->
    <FilePreview
      v-if="previewInfo.visible"
      :visible.sync="previewInfo.visible"
      :fileupload-id="previewInfo.fileId"
      :file-name="previewInfo.fileName"
      v-bind="filePreviewOptions"
      @cancel="previewInfo.visible = false"
    />
  </div>
</template>

<script>
/**
 * 文件列表
 */
import {
  COMPONENT_TYPE,
  LIST_TYPE,
  propsMixin,
  listPropsMixin,
  uploadPropsMixin,
  fileHasPreview,
  getImgSrc,
  fileListAttrsKeyConversion
} from './util'
import { downloadWithParam } from 'lib@/utils/file'
import FilePreview from 'lib@/components/filePreview'

export default {
  name: 'CommonFileList',
  components: { FilePreview },

  mixins: [propsMixin, listPropsMixin],

  // 该props入参的都来源于upload组件，外部直接调用时请忽略
  props: {
    // 可继承拖拽上传组件容器大小
    draggerWrapSize: {
      type: Object,
      required: false
    },
    // 最大可上传数量，继承于上传组件
    limit: uploadPropsMixin.props.limit,
    // 标记数据是否来源于上传组件
    fileFromUpload: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      fileListData: [],
      previewInfo: {
        visible: false,
        fileId: '',
        fileName: ''
      },
      // imgSrc: '',
      getImgSrc: getImgSrc
    }
  },
  computed: {
    // 列表样式
    listTypeMap () {
      return {
        TEXT: this.listType === LIST_TYPE.TEXT,
        PICTURE_CARD: this.listType === LIST_TYPE.PICTURE_CARD
      }
    },

    // 图片卡片遮罩层操作图标
    pictureCardModalOperationIcon () {
      // 禁用时遮罩层不出现，所以不用判断
      return [
        {
          icon: 'el-icon-upload',
          content: this.$t('components.upload.reUpload'),
          // 只有上传一个时候才显示
          visible: !this.readonly && this.limit === 1
        },
        {
          icon: 'el-icon-view',
          content: this.$t('components.upload.previewFile'),
          // 是否需要预览 icon
          visible: this.filePreview
        },
        { icon: 'el-icon-download', content: this.$t('components.upload.downloadFile'), visible: true },
        {
          icon: 'el-icon-delete',
          content: this.$t('components.upload.deleteFile'),
          visible: !this.readonly
        }
      ].filter(item => item.visible)
    },

    // 是否显示删除按钮
    showClose () {
      return this.type !== COMPONENT_TYPE.LINK && !this.fileDisabled && !this.readonly
    },

    // 列表样式绑定
    fileListWrapClass () {
      return {
        'file-list-wrap': true,
        'disabled': this.fileDisabled,
        'is-picture-card': this.listTypeMap.PICTURE_CARD
      }
    },

    // 列表项样式绑定
    fileItemWrapClass () {
      return item => ({
        'file-item-wrap': true,
        preview: this.filePreview && !this.fileDisabled && item.preview
      })
    }
  },

  watch: {
    defaultFile: {
      handler (newVal) {
        if (newVal) {
          const { fileId = '', fileName = '' } = newVal
          if (fileId && fileName) {
            this.transformerFileListData([
              {
                fileId,
                fileName,
                preview: fileHasPreview(fileName)
              }
            ])
          } else {
            if (!this.fileList.length) {
              this.fileListData = []
            }
          }
        }
      },
      deep: true,
      immediate: true
    },
    fileList: {
      handler (newVal = []) {
        // defaultFile优先
        if (!this.defaultFile || typeof (this.defaultFile || {}).fileId === 'undefined') {
          // 如果来源于upload组件，字段已经映射了
          const [idKey, nameKey] = [
            this.fileFromUpload ? 'fileId' : this.fileKeyOptions.idKey,
            this.fileFromUpload ? 'fileName' : this.fileKeyOptions.nameKey
          ]

          this.transformerFileListData(fileListAttrsKeyConversion(newVal, this.fileListKey)
            .concat()
            .map(item => {
              return {
                fileId: item[idKey],
                fileName: item[nameKey],
                preview: fileHasPreview(item[nameKey])
              }
            })
          )
        }
      },
      immediate: true
    }
  },

  methods: {
    /* 移除文件 */
    removeFile (file, $index) {
      this.$emit('on-remove', { file, $index })
    },

    /* 文件预览 */
    showFilePreview (file) {
      if (this.fileDisabled || !this.filePreview || !file.preview) {
        return
      }

      this.previewInfo.visible = true
      this.previewInfo.fileId = file.fileId
      this.previewInfo.fileName = file.fileName
    },

    /* 文件下载 */
    downloadFile (file) {
      if (!file.fileId) {
        return
      }

      downloadWithParam(
        file.fileId,
        file.fileName
      ).then(() => {
        this.$bus.$emit('downLoadHandle')
      }).catch(() => {
        // 下载失败
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },

    /* 图片卡片遮罩层操作按钮点击 */
    pictureCardModalOperationClick (itemIcon, itemFile, $index) {
      switch (itemIcon.icon) {
        case 'el-icon-upload':
          // 重新上传 发起回调
          this.$emit('on-trigger', itemFile, $index)
          break
        case 'el-icon-view':
          // 预览文件
          this.showFilePreview(itemFile)
          break
        case 'el-icon-download':
          // 下载文件
          this.downloadFile(itemFile)
          break
        case 'el-icon-delete':
          // 删除文件
          this.removeFile(itemFile, $index)
          break
      }
    },

    /* 转化赋值文件列表 */
    transformerFileListData (list) {
      let newList = [].concat(list)
      if (this.listTypeMap.PICTURE_CARD) {
        // 图片路径缓存 避免触发重绘
        newList = newList.map((item, index) => {
          const oldItem = this.fileListData[index]
          return {
            ...item,
            preview: fileHasPreview(item.fileName),
            pictureCardImgSrc: oldItem?.fileId === item.fileId && oldItem?.pictureCardImgSrc
              ? oldItem.pictureCardImgSrc
              : getImgSrc(item.fileId, item.fileName)
          }
        })
      }

      this.fileListData = newList
    }
  }
}
</script>

<style lang="scss" scoped>
@import "./common-file.scss";
.file-list-wrap {
  // 默认行内块级元素，宽度100%
  display: block;
  width: 100%;
}

// 图片卡片样式
.file-list-wrap.is-picture-card {
  display: flex;
  .file-item-picture-card-wrap {
    position: relative;
    @include picture-card-wrap;
    &:not(:last-child) {
      margin-right: 10px;
    }
    .picture-card-img {
      width: 100%;
      height: 100%;
    }

    &:hover .picture-card-modal {
      display: flex;
    }

    .picture-card-modal {
      position: absolute;
      display: none;
      justify-content: center;
      align-items: center;
      overflow: hidden;
      flex-wrap: wrap;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background-color: rgba(22, 28, 36, .7);
      border-radius: $picture-card-border-radius;
      .modal-operation-icon {
        font-size: 19px;
        color: #fff;
        margin: 0 10px;
        cursor: pointer;
      }
      .icon-tooltip{
        &.view-icon-hidden{
          display: none;
        }
      }
    }
  }
}

// 空列表提示
.file-list-wrap .empty-tip {
  margin: 0;
  color: #7f8083;
  line-height: $file-wrap-height;
  font-size: 12px;
  .el-icon-folder-delete {
    font-size: 14px;
    margin-right: 4px;
  }
}

// 预览样式
.file-list-wrap:not(.disabled) .file-item-wrap.preview {
  .file-item-icon,
  .file-item-name {
    // color: $link-color; // themeColor 跟着主题色变
    cursor: pointer;
  }
}
</style>
