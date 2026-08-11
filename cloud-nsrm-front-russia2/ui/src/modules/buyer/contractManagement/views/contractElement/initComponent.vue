<template>
  <div>
    <div v-if="addMethod === 'IMAGE'">
      <SrmCommonFile
        :default-file="{
          fileId: fileId,
          fileName: fileName
        }"
        :extra-data="{
          ileModular: 'sup',
          fileFunction: 'contractElementInitValue',
          fileType: 'images'
        }"
        :validate-options="{
          accept: ['jpeg', 'png', 'jpg']
        }"
        :readonly="!isEdit"
        @on-change="handleUploadImages"
      />
    </div>
    <div v-else>
      <InitReadComponent v-show="!isEdit" :addMethod="addMethod" :value="value" />
      <InitEditComponent v-show="isEdit" :addMethod="addMethod" :value="value" @setValue="setValue" />
    </div>
  </div>
</template>

<script>
import SrmCommonFile from '@/library/components/srm-ui/packages/srm-common-file'
import InitEditComponent from './initEditComponent.vue'
import InitReadComponent from './initReadComponent.vue'

export default {
  name: 'InitComponent',
  components: {
    InitEditComponent,
    InitReadComponent,
    SrmCommonFile
  },
  props: {
    isEdit: {
      type: Boolean,
      default: false
    },
    addMethod: {
      type: String,
      default: ''
    },
    value: {
      type: String,
      default: ''
    }
  },
  computed: {
    fileId () {
      try {
        return this.value ? (JSON.parse(this.value).fileId || '') : ''
      } catch (e) {
        return ''
      }
    },
    fileName () {
      try {
        return this.value ? (JSON.parse(this.value).fileName || '') : ''
      } catch (e) {
        return ''
      }
    }
  },
  watch: {
    value: {
      handler: function (newV) {
        // console.log('###### 日志 value ######')
        // console.log(newV)
      },
      immediate: true
    },
    isEdit: {
      handler: function (newV) {},
      immediate: true
    }
  },
  mounted () {},
  methods: {
    setValue (value) {
      this.$emit('change', value)
    },
    handleUploadImages ({ file }) {
      const { fileId = '', fileName = '' } = file || {}
      console.log('handleUploadImages', file)
      // this.setValue(fileId.toString(), fileName)
      this.setValue(JSON.stringify({
        fileId: fileId.toString(),
        fileName: fileName
      }))
    }
  }
}
</script>
