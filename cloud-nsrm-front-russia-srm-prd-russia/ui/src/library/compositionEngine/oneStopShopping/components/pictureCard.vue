<template>
  <div class="picture-card-upload">
    <!-- :style="{ '--bottomHeight': fileList.length > 0 ? '60px' : '20px' }" -->
    <el-upload
      class="upload-component"
      :disabled="isReadOnly"
      :multiple="true"
      :limit="limit"
      :on-exceed="handleExceed"
      :file-list="fileListReal"
      :data="extraData"
      :action="actionUrl"
      list-type="picture-card"
      :headers="headers"
      accept=".png,.jpg,.jpeg"
      :on-error="handleError"
      :on-success="handleSuccess"
      :on-change="handleChange"
      :before-upload="beforeUpload"
      v-bind="$attrs"
      v-on="$listeners"
    >
      <div slot="default" class="add-symble">
        <em class="el-icon-plus add-icon" />
        <span class="add-text">{{ $t('contractMod.uploadPic') }}</span>
      </div>

      <div slot="file" slot-scope="{ file }" class="img-list">
        <img
          width="100%"
          :src="isOk(file) ? getImgList(file.response.data.fileuploadId) : ''"
          :alt="isOk(file) ? file.response.data.filePureName : ''"
        >
        <span class="el-upload-list__item-actions">
          <!-- 放大 -->
          <span class="el-upload-list__item-preview" @click="handlePictureCardPreview(file)">
            <em class="el-icon-zoom-in" />
          </span>
          <!-- 下载 -->
          <span class="el-upload-list__item-delete" @click="handleDownload(file)">
            <em class="el-icon-download" />
          </span>
          <!-- 删除 -->
          <span v-if="!isReadOnly" class="el-upload-list__item-delete" @click="handleRemove(file)">
            <em class="el-icon-delete" />
          </span>
        </span>
        <div class="default-btn">
          <el-button
            v-if="file.response && file.ifDefaultPicture === 'Y'"
            type="text"
            class="set-btn-default"
          >
            {{ $t('dataConfMod.defaultPicture') }}
          </el-button>
          <el-button v-else :disabled="!isOk(file) || isReadOnly" class="set-btn-default" @click="setDefaultPic(file)">
            {{ $t('dataConfMod.setDefault') }}
          </el-button>
        </div>
      </div>
    </el-upload>
    <div class="text-tip">
      {{ $t('dataConfMod.uploadCondition') }}
    </div>

    <div v-if="dialogVisible">
      <filePreview
        vWidth="60%"
        vHeight="400"
        :visible="dialogVisible"
        :fileupload-id="dialogObj.response.data.fileuploadId"
        :file-name="dialogObj.response.data.fileSourceName"
        @cancel="dialogVisible = false"
      />
    </div>
  </div>
</template>

<script>
import { getImgSrc, downloadWithParam } from 'lib@/utils/file'
import { FILE_UPLOAD } from '@/api/common'
import { sysPrefix } from '@/config/ipConfig'
import { getToken } from '@/utils/auth'
import { isInteger } from 'lodash'
import filePreview from 'lib@/components/filePreview'

export default {
  name: 'PictureCard',
  components: {
    filePreview
  },
  props: {
    extraData: {
      type: Object,
      default: () => {
        return {
          uploadType: 'DEF',
          sourceType: 'WEB_APP',
          fileModular: 'base',
          fileFunction: 'commonFile',
          fileType: 'images'
        }
      }
    },
    // 上传API路径
    actionUrl: {
      type: String,
      default: sysPrefix() + FILE_UPLOAD
    },
    // 上传头
    headers: {
      type: Object,
      default: () => {
        return {
          Authorization: `Bearer ${getToken()}`
        }
      }
    },
    // 最大允许上传个数
    limit: {
      type: Number,
      default: 5,
      validator: value => {
        return isInteger(value) && value > 0
      }
    },
    fileList: {
      type: Array,
      default: () => {
        return []
      }
    },
    isReadOnly: {
      type: Boolean,
      default: () => {
        return false
      }
    }
  },
  data () {
    return {
      fileListReal: [],
      dialogObj: {},
      dialogVisible: false,
      disabled: false,
      imgList: []
    }
  },
  computed: {
    isOk () {
      return (file) => file.status === 'success'
    }
  },
  watch: {
    fileList: {
      handler (data) {
        this.fileListReal = data
        if (this.fileListReal.length > 0) {
          this.watchImgList(this.fileListReal)
        }
      },
      deep: true,
      immediate: true
    }
  },
  methods: {
    // 文件上传限制
    handleExceed (files, fileList) {
      this.$message.warning(`当前限制为 ${this.limit} 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`)
    },
    watchImgList (data) {
      const sign = data.every(file => file.response)
      if (sign) {
        this.imgList = data.map(item => {
          return {
            fileuploadId: item.response.data.fileuploadId,
            url: getImgSrc(item.response.data.fileuploadId)
          }
        })
      }
    },
    getImgList (fileuploadId) {
      let index = this.imgList.findIndex(item => item.fileuploadId === fileuploadId)
      if (index > -1) return this.imgList[index].url
    },
    // 设置默认图片
    setDefaultPic (file) {
      this.$emit('setDefaultPic', file.response.data)
    },
    // 删除
    handleRemove (file) {
      this.fileListReal = this.fileList.filter(item => item.uid !== file.uid)
      this.$emit('handleRemove', this.fileListReal, file)
    },
    // 预览
    handlePictureCardPreview (file) {
      this.dialogObj = file
      this.dialogVisible = true
    },
    // 下载
    handleDownload ({ response }) {
      downloadWithParam(response.data.fileuploadId, response.data.fileSourceName).catch(() => {
        // 下载失败
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },
    beforeUpload (file) {
      const fileType = file.name.split('.')[1]
      if (!['jpg', 'jpeg', 'png'].includes(fileType)) {
        this.$message.warning(this.$t('dataConfMod.onlyUpload'))
        return false
      }
      if (file.size / 1024 / 1024 > 1) {
        this.$message.warning(this.$t('oneStopShopping.uploadSizeTip')) // 上传文件大小不能超过 1MB!
        return false
      }
      this.$emit('beforeUpload', file)
    },
    handleChange (file, fileList) {
      this.$emit('handleChange', file, fileList)
    },
    handleSuccess (res, file, fileList) {
      this.$emit('handleSuccess', res, file, fileList)
    },
    handleError () {
      this.$message.warning(this.$t('dataConfMod.uploadError'))
    }
  }
}
</script>

<style lang="scss" scoped>
.picture-card-upload {
  padding-bottom: 80px;
  .upload-component {
    display: flex;
    padding-bottom: 60px;
    // padding-bottom: var(--bottomHeight);
    :deep(.el-upload-list--picture-card) {
      display: flex;
    }
    :deep(.el-upload-list__item) {
      overflow: visible;
    }
    .add-symble {
      color: #96999C;
      position: relative;
      &:focus {
        color: #0077FF;
      }
      .add-icon {
        position: absolute;
        top: 50px;
        left: 0;
        right: 0;
        margin: auto;
      }
      .add-text {
        position: absolute;
        top: 10px;
        left: 0;
        right: 0;
        margin: auto;
      }
    }
    .text-tip {
      width: 100%;
      margin: 24px 0 48px 0;
      display: flex;
    }
    .set-btn-default {
      position: absolute;
      bottom: -50px;
      left: 0px;
      right: 0;
      margin: auto;
    }
    .img-list {
      width: 100%;
      height: 100%;
      overflow: hidden;
    }
  }
}
</style>
