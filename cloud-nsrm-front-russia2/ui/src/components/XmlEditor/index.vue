<template>
  <div class="json-editor">
    <textarea ref="textarea" />
  </div>
</template>

<script>
import CodeMirror from 'codemirror'
import 'codemirror/addon/lint/lint.css'
import 'codemirror/lib/codemirror.css'
import 'codemirror/theme/rubyblue.css'
import 'codemirror/mode/javascript/javascript'
import 'codemirror/addon/lint/lint'
import 'codemirror/addon/lint/json-lint'
import 'codemirror/mode/xml/xml.js'

import 'codemirror/addon/hint/show-hint.css'
import 'codemirror/addon/hint/show-hint.js'
import 'codemirror/addon/hint/javascript-hint'
import 'codemirror/addon/hint/xml-hint'
import 'codemirror/addon/hint/sql-hint'
import 'codemirror/addon/hint/anyword-hint'
// import 'script-loader!jsonlint'

const endWith = function (src, endStr) {
  const d = src.length - endStr.length
  return (d >= 0 && src.lastIndexOf(endStr) === d)
}

const startWith = function (src, startStr) {
  const index = src.indexOf(startStr)
  return index === 0
}

export default {
  name: 'XmlEditor',
  /* eslint-disable vue/require-prop-types */
  props: {
    value: {
      type: String,
      default: true
    },
    mode: {
      type: [String, Object],
      default: () => {
        return 'application/xml'
      }
    }
  },
  data () {
    return {
      jsonEditor: null
    }
  },
  watch: {
    value (value) {
      const editorValue = this.jsonEditor.getValue()
      if (value !== editorValue) {
        this.setXmlValue(value)
      }
    }
  },
  mounted () {
    this.jsonEditor = CodeMirror.fromTextArea(this.$refs.textarea, {
      lineNumbers: true,
      mode: this.mode,
      gutters: ['CodeMirror-lint-markers'],
      theme: 'rubyblue',
      lint: true,
      hint: 'xml',
      foldGutter: true
    })

    this.jsonEditor.on('change', (cm, change) => {
      console.log('cm', cm)
      this.$emit('changed', cm.getValue())
      this.$emit('input', cm.getValue())
    })
    this.$nextTick(() => {
      this.setXmlValue(this.value)
    })
  },
  methods: {
    transferXml (xml) {
      let realValue = xml
      if (realValue && (startWith(realValue, '\'') || startWith(realValue, '"'))) {
        realValue = realValue.substring(1)
      }
      if (realValue && (endWith(realValue, '\'') || endWith(realValue, '"'))) {
        realValue = realValue.substring(0, realValue.length)
      }
      return realValue
    },
    setXmlValue (value) {
      if (!this.jsonEditor) {
        return
      }
      let realValue = this.transferXml(value)
      if (!realValue) {
        realValue = ''
      }
      this.jsonEditor.setValue(realValue)
    },
    getValue () {
      const currentValue = this.jsonEditor.getValue()
      return this.transferXml(currentValue)
    }
  }
}
</script>

<style scoped>
.json-editor{
  height: 100%;
  position: relative;
}
.json-editor :deep(.CodeMirror) {
  height: auto;
  min-height: 300px;
}
.json-editor :deep(.CodeMirror-scroll){
  min-height: 300px;
}
.json-editor :deep(.cm-s-rubyblue span.cm-string) {
  color: #F08047;
}
</style>
