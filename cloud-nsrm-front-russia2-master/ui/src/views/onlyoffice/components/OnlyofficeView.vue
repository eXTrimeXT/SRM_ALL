<template>
  <Workbook
    ref="workbook"
    @workbookCreateAfter="workbookCreateAfter"
    @saveDocument="saveDocument"
    @onOutdatedVersion="onOutdatedVersion"
    class="workbook"
  />
</template>

<script>
import Secret from '@/utils/secret'

export default {
  name: 'OnlyOffice',
  props: {
    title: {
      type: String,
      default: ''
    },
    url: {
      type: String,
      default: ''
    },
    fileType: {
      type: String,
      default: ''
    },
    lang: {
      type: String,
      default: 'zh-CN'
    },
    fileKey: {
      type: String,
      default: ''
    },
    callbackUrl: {
      type: String,
      default: ''
    },
    storage: {
      type: String,
      default: ''
    },
    comment: {
      type: String,
      default: 'open'
    },
    // 开启修订功能 open 关闭修订功能 close
    revision: {
      type: String,
      default: 'open'
    },
    hideRevision: {
      type: Boolean,
      default: false
    },
    edit: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      config: {
        document: {
          title: '',
          key: '',
          fileType: '',
          url: '',
          storage: ''
        },
        documentType: 'word',
        editorConfig: {
          lang: 'zh-CN', // 界面语言：zh、en等，默认为zh
          region: 'zh-CN', // 文档类型为cell时生效，货币和日期格式：zh-CN、en-US等，默认为zh-CN
          callbackUrl: '',
          user: {
            id: '1',
            name: 'admin'
          }
        }
      }
    }
  },

  methods: {
    workbookCreateAfter(docEditor) {
      console.log('Document is loaded')
    },
    saveDocument(frameEditorId) {
      console.log('Document is going to be saved')
    },
    // 文档版本已过期，需重新生成key
    onOutdatedVersion() {
      console.log('Document is outdated')
    },

    async init() {
      const { username, nickname } = this.$store.getters.userInfo
      const user = { id: username, name: nickname }
      const adapter = this.$refs.workbook.getSettingAdapter(this.config, user)
      // 开启修订功能（只能接受或拒绝本人的修订）
      if (this.revision === 'open') {
        adapter.setRevision()
      }
      // 开启修订功能（能够接受或拒绝任何人的修订）
      if (this.revision === 'clear') {
        adapter.setRevisionClear()
      }

      // 可以留痕，不能接受自己的修订了
      if (this.revision === 'noSelf') {
        adapter.setRevision(true, false);
      }
      // 可以留痕，只能接受自己的修订
      if (this.revision === 'self') {
        adapter.setRevision(true, true);
      }
      // 禁止取消修订状态
      if (this.hideRevision) {
        adapter.setHideRevisionSwitch()
      }
      // 开启批注，只能修改自己的批注内容
      if (this.comment === 'open') {
        adapter.setComment();
      }
      if (this.comment === 'clear') {
        adapter.setCommentClear();
      }
      if (!this.edit) {
        adapter.setEditable(false);
      }
      this.$refs.workbook.initEditor(adapter)
    },
    getDocumentType(type) {
      const documentType = {
        word: [
          'djvu',
          'doc',
          'docm',
          'docx',
          'docxf',
          'dot',
          'dotm',
          'dotx',
          'epub',
          'fb2',
          'fodt',
          'htm',
          'html',
          'mht',
          'mhtml',
          'odt',
          'oform',
          'ott',
          'oxps',
          'pdf',
          'rtf',
          'stw',
          'sxw',
          'txt',
          'wps',
          'wpt',
          'xml',
          'xps'
        ],
        cell: [
          'csv',
          'et',
          'ett',
          'fods',
          'ods',
          'ots',
          'sxc',
          'xls',
          'xlsb',
          'xlsm',
          'xlsx',
          'xlt',
          'xltm',
          'xltx',
          'xml'
        ],
        slide: [
          'dps',
          'dpt',
          'fodp',
          'odp',
          'otp',
          'pot',
          'potm',
          'potx',
          'pps',
          'ppsm',
          'ppsx',
          'ppt',
          'pptm',
          'pptx',
          'sxi'
        ]
      }
      for (const item of Object.keys(documentType)) {
        if (documentType[item].includes(type)) {
          return item
        }
      }
      return void 0
    },
    destroy() {
      this.$refs.workbook.destroyEditor()
    }
  },
  watch: {
    url: {
      handler(val) {
        if (!val) {
          return
        }
        this.config.document.key = this.key
        this.config.document.url = this.url
        this.config.document.title = this.title
        this.config.document.fileType = this.fileType
        this.config.documentType = this.getDocumentType(this.fileType) || 'word'
        this.config.editorConfig.lang = this.lang
        this.config.editorConfig.region = this.lang
        this.config.editorConfig.callbackUrl = this.callbackUrl
        this.config.editorConfig.key = this.fileKey
        this.config.editorConfig.storage = this.storage
        this.$nextTick(() => {
          console.log(this.config)
          this.init()
        })
      },
      immediate: true
    }
  }
}
</script>

<style>
.workbook {
  position: absolute;
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;
}
</style>
