<template>
  <div>
    <template v-if="addMethod === 'IMAGE'">
      <el-button
        v-if="!value"
        icon="el-icon-upload"
        @click="uploadImage()"
      >
        {{ $t('contractMod.uploadPic') }}
      </el-button>
      <template v-else>
        <el-popover placement="top" width="300" trigger="hover">
          <el-image
            v-if="value"
            style="max-width: 300px"
            :src="value"
            fit="contain"
          />
          <span v-else>{{ $t('contractMod.noUploadPicture') }}</span>
          <el-button slot="reference" icon="el-icon-picture" type="text">
            {{ $t('common.preview') }}
          </el-button>
        </el-popover>
        <i
          style="cursor: pointer; margin-left: 5px"
          class="el-icon-close"
          @click="deleteImage()"
        />
      </template>
    </template>
    <el-input
      v-else-if="!!addMethod"
      v-model="inputValue"
      type="string"
    />
    <span v-else></span>

    <srm-dialog
      :title="$t('contractMod.uploadPic')"
      :visible.sync="uploadImageVisible"
      size="small"
    >
      <input
        ref="uploader"
        type="file"
        @change="transform2base64"
      >
      <template
        #footer
        class="dialog-footer"
      >
        <el-button @click="uploadImageVisible = false">
          {{ $t('common.cancel') }}
        </el-button>
        <el-button
          type="primary"
          @click="saveImage"
        >
          {{ $t('common.submit') }}
        </el-button>
      </template>
    </srm-dialog>
  </div>
</template>

<script>
export default {
  name: 'InitEditComponent',
  props: {
    addMethod: {
      type: String,
      default: ''
    },
    value: {
      type: String,
      default: ''
    }
  },
  data () {
    return {
      uploadImageVisible: false,
      currentBase64: null,
      // currentScope: null,
      inputValue: null
    }
  },
  watch: {
    value: {
      handler: function (newV) {
        this.inputValue = newV
      },
      immediate: true
    },
    inputValue: {
      handler: function (newV) {
        this.$emit('setValue', newV)
      },
      immediate: true
    }
  },
  mounted () {},
  methods: {
    uploadImage () {
      this.uploadImageVisible = true
      // this.currentScope = scope
    },
    saveImage () {
      this.$emit('setValue', this.currentBase64)
      this.uploadImageVisible = false
    },
    deleteImage () {
      this.inputValue = ''
    },
    transform2base64 () {
      if (typeof FileReader === 'undefined') {
        this.$message.error(this.$t('dataConfMod.msgNotSupport'))
      } else {
        try {
          /* 图片转Base64 核心代码 */
          const uploader = this.$refs.uploader
          console.log('uploader', uploader.files)
          var file = uploader.files[0]
          // 这里我们判断下类型如果不是图片就返回 去掉就可以上传任意文件
          if (!/image\/\w+/.test(file.type)) {
            this.$message.error(this.$t('dataConfMod.msgMakeSurePicType'))
            return false
          }
          var reader = new FileReader()
          reader.onload = () => {
            this.currentBase64 = reader.result
          }
          reader.readAsDataURL(file)
        } catch (e) {
          this.$message.error(this.$t('dataConfMod.msgBase64Error') + e.toString())
        }
      }
    }
  }
}
</script>
