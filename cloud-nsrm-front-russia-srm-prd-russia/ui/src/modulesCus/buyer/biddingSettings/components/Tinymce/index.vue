<template>
  <!-- :style="{width:containerWidth}" -->
  <div :class="{fullscreen:fullscreen}" class="tinymce-container">
    <textarea :id="tinymceId" class="tinymce-textarea" />
    <!-- <div class="editor-custom-btn-container">
      <editorImage color="#1890ff" class="editor-upload-btn" @successCBK="imageSuccessCBK" />
    </div> -->
  </div>
</template>

<script>

import editorImage from './components/EditorImage'
import plugins from './plugins'
import toolbar from './toolbar'
import load from './dynamicLoadScript'
const tinymceCDN = './tinymce/tinymce.min.js'
const INIT = 0
const INPUT = 1
const CHANGED = 2
const status = ['INIT', 'INPUT', 'CHANGED']

export default {
  name: 'Tinymce',
  components: { editorImage },
  model: {
    event: 'change',
    value: 'value'
  },
  props: {
    id: {
      type: String,
      default: function () {
        return 'vue-tinymce-'
      }
    },
    value: {
      type: String,
      default: ''
    },
    toolbar: {
      type: Array,
      required: false,
      default () {
        return []
      }
    },
    menubar: {
      type: String,
      default: '' // file edit insert view format table
    },
    height: {
      type: [Number, String],
      required: false,
      default: 360
    },
    minHeight: {
      type: [Number, String],
      required: false,
      default: 360
    },
    maxHeight: {
      type: [Number, String],
      required: false,
      default: 500
    },
    width: {
      type: [Number, String],
      required: false,
      default: 'auto'
    },
    setup: {
      type: Function,
      default: function () {}
    }
  },
  data () {
    return {
      bookmark: null,
      status: INIT,
      hasChange: false,
      hasInit: false,
      tinymceId: this.id + new Date().getTime() + ((Math.random() * 1000).toFixed(0) + ''),
      fullscreen: false,
      languageTypeList: {
        'en': 'en',
        'zh': 'zh_CN',
        'es': 'es_MX',
        'ja': 'ja'
      }
    }
  },
  computed: {
    containerWidth () {
      const width = this.width
      if (/^[\d]+(\.[\d]+)?$/.test(width)) { // matches `100`, `'100'`
        return `${width}px`
      }
      return width
    }
  },
  watch: {
    value (val, oldVal) {
      // if (!this.hasChange && this.hasInit) {
      //   this.$nextTick(() =>
      //     window.tinymce.get(this.tinymceId).setContent(val || ''))
      // }
      // this.$nextTick(() =>
      //     window.tinymce.get(this.tinymceId).setContent(val || ''))
      // this.$emit("change", val);
      /// //
      if (this.status === INPUT || oldVal === val) return
      if (!window.tinymce.get(this.tinymceId) || !window.tinymce.get(this.tinymceId).initialized) return // fix editor plugin is loading and set content will throw error.
      if (val === null) return this.resetContent('')
      this.setContent(val)
    }
  },
  mounted () {
    this.$nextTick(() => {
      this.init()
    })
  },
  activated () {
    if (window.tinymce) {
      this.initTinymce()
    }
  },
  deactivated () {
    this.destroyTinymce()
  },
  destroyed () {
    this.destroyTinymce()
  },
  methods: {
    init () {
      // dynamic load tinymce from cdn
      load(tinymceCDN, (err) => {
        if (err) {
          this.$message.error(err.message)
          return
        }
        this.initTinymce()
      })
    },
    initTinymce () {
      const _this = this
      window.tinymce.init({
        selector: `#${this.tinymceId}`,
        language_url: '/tinymce/langs/zh_CN.js', // 中文包
        language: this.languageTypeList['zh'], // 中文
        browser_spellcheck: true, // 拼写检查
        branding: false, // 去水印
        elementpath: true, // 禁用编辑器底部的状态栏
        statusbar: true, // 隐藏编辑器底部的状态栏
        toolbar_sticky: false,
        paste_data_images: true, // 是否允许粘贴图像
        auto_focus: false,
        height: this.height,
        max_height: this.maxHeight,
        min_height: this.minHeight,
        body_class: 'panel-body ',
        object_resizing: false,
        toolbar: this.toolbar.length > 0 ? this.toolbar : toolbar,
        menubar: this.menubar,
        plugins: plugins,
        end_container_on_empty_block: true,
        powerpaste_word_import: 'merge',
        // powerpaste_word_import: "clean", // 是否保留word粘贴样式  clean | merge
        // powerpaste_html_import: 'clean', // propmt, merge, clean
        // powerpaste_allow_local_images: true,//
        // powerpaste_keep_unsupported_src:true,
        code_dialog_height: 450,
        code_dialog_width: 1000,
        advlist_bullet_styles: 'default,circle,disc,square',
        advlist_number_styles: 'lower-alpha,lower-roman,upper-alpha,upper-roman',
        imagetools_cors_hosts: ['www.tinymce.com', 'codepen.io'],
        default_link_target: '_blank',
        link_title: false,
        fontsize_formats: '14px 16px 18px 20px 24px 26px 28px 30px 32px 36px', // 字体大小
        font_formats: '微软雅黑=Microsoft YaHei,Helvetica Neue,PingFang SC,sans-serif;苹果苹方=PingFang SC,Microsoft YaHei,sans-serif;宋体=simsun,serif;仿宋体=FangSong,serif;黑体=SimHei,sans-serif;Arial=arial,helvetica,sans-serif;Terminal=terminal;monaco;Times New Roman=times new roman;times',
        file_picker_types: 'image',
        toolbar_mode: 'Sliding',
        entities: '160,nbsp,162,cent,8364,euro,163,pound',
        entity_encoding: 'named',
        fullpage_default_font_family: '宋体',
        nonbreaking_force_tab: true, // inserting nonbreaking space &nbsp; need Nonbreaking Space Plugin
        pagebreak_separator: '_ueditor_page_break_tag_', //
        images_upload_handler: (blobInfo, success, failure) => { // 图片上传处理
          const img = 'data:image/jpeg;base64,' + blobInfo.base64()
          success(img)
        },
        images_dataimg_filter: (img) => {},
        content_css: '/tinymce/style/cus.css',
        init_instance_callback: editor => {
          if (_this.value) {
            editor.setContent(_this.value)
          }
          _this.hasInit = true
          // editor.on('NodeChange Change KeyUp SetContent', () => {
          //   this.hasChange = true
          //   let content = editor.getContent()
          //   this.$emit('input', content)
          //   // this.$emit("change", content);
          // })

          // this.setContent(_this.value, editor)
          if (_this.value) {
            this.setContent(_this.value, editor)
          }
          editor.on('keyup input', e => { // 只在编辑器中打字才会触发
            this.status = INPUT // 编辑器录入文字时标记为`INPUT`状态
          })
          editor.on('Blur', e => {
            this.status = INIT
          })
          editor.on('input keyup Change Undo Redo ExecCommand NodeChange', e => {
            this.onChanged(e, editor)
          })
        },
        setup (editor) {
          editor.on('FullscreenStateChanged', (e) => {
            _this.fullscreen = e.state
          })
          _this.$emit('setup', editor)
        },
        convert_urls: false
      })
    },

    destroyTinymce () {
      const tinymce = window.tinymce.get(this.tinymceId)

      if (tinymce) {
        if (this.fullscreen) {
          tinymce.execCommand('mceFullScreen')
        }

        tinymce.destroy()
      }
    },

    setContent (value) {
      console.log(value)
      window.tinymce.get(this.tinymceId).setContent(value)
      window.tinymce.get(this.tinymceId).selection.moveToBookmark(this.bookmark)
    },
    getContent () {
      window.tinymce.get(this.tinymceId).getContent()
    },
    imageSuccessCBK (arr) {
      arr.forEach(v => window.tinymce.get(this.tinymceId).insertContent(`<img class="wscnph" src="${v.url}" >`))
    },
    onChanged (e, editor) {
      if (!editor) editor = window.tinymce.get(this.tinymceId)
      if (e.type === 'change') this.bookmark = e.level.bookmark
      const content = editor.getContent()
      this.$emit('change', content)
    },
    resetContent (val) {
      let editor = window.tinymce.get(this.tinymceId)
      if (editor.resetContent) return editor.resetContent(val)
      editor.setContent(val)
      editor.setDirty(false)
      editor.undoManager.clear()
    }
  }
}
</script>

<style lang="scss" scoped>
.tinymce-container {
  position: relative;
  line-height: normal;
}

.tinymce-container {
  :deep(.mce-fullscreen) {
    z-index: 10000;
  }
}

.tinymce-textarea {
  visibility: hidden;
  z-index: -1;
}

.editor-custom-btn-container {
  position: absolute;
  right: 4px;
  top: 4px;
  /*z-index: 2005;*/
}

.fullscreen .editor-custom-btn-container {
  z-index: 10000;
  position: fixed;
}

.editor-upload-btn {
  display: inline-block;
}
</style>
