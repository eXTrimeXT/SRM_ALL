<template>
  <el-container
    class="the_contractTemplatePreview_wrapper"
    direction="vertical"
  >
    <el-main>
      <vue-ueditor-wrap
        v-model="html"
        :destroy="true"
        style="display: none"
        :config="customConfig"
        @ready="ready"
      />
      <div class="button-group">
        <el-button
          type="primary"
          @click="edit"
        >
          编辑
        </el-button>
        <el-button
          type="primary"
          @click="preview"
        >
          预览
        </el-button>
        <el-button
          type="primary"
          @click="print"
        >
          打印
        </el-button>
      </div>
      <div
        id="preview_wrapper"
        class="preview_wrapper"
      >
        <div ref="markedContent" />
      </div>
    </el-main>
  </el-container>
</template>

<script>
import Vue from 'vue'
import VueUeditorWrap from 'vue-ueditor-wrap'
import OrganizationSelectTree from 'lib@/components/organization-cascader'
import materialList from '../contractDemo/material-list'
import payPlan from '../contractDemo/pay-plan'
import { getStore, getRouter, geti18n } from '@/main'

export default {
  name: 'Preview',
  components: { VueUeditorWrap },
  data () {
    return {
      editorInstance: null,
      customConfig: {
        // 编辑器不自动被内容撑高
        autoHeightEnabled: false,
        // 初始容器高度
        initialFrameHeight: 240,
        // 初始容器宽度
        initialFrameWidth: '100%',
        // 上传文件接口（这个地址是我为了方便各位体验文件上传功能搭建的临时接口，请勿在生产环境使用！！！）
        // serverUrl: "http://35.201.165.105:8000/controller.php",
        // UEditor 资源文件的存放路径，如果你使用的是 vue-cli 生成的项目，通常不需要设置该选项，vue-ueditor-wrap 会自动处理常见的情况，如果需要特殊配置，参考下方的常见问题2
        UEDITOR_HOME_URL: '/UEditor/'
      },
      childContext: null,
      flag: 'preview',
      html: null
    }
  },
  computed: {
    visible () {
      return this.flag === 'preview'
    }
  },
  mounted () {
    this.compile()
    const { flag, row } = this.$attrs.params
    if (!row.modelHeadId) {
      return
    }
    this.$api.cm.modelLine.getModelLine(row.modelHeadId).then(res => {
      this.childContext.form = res.data.reduce((obj, i) => {
        const { modelKey, modelValue } = i
        if (['materialList', 'payPlan'].includes(modelKey)) {
          let value = []
          try {
            value = JSON.parse(modelValue)
          } catch (e) {
            console.warn('JSON转换出错')
          }
          obj[modelKey] = value
        } else {
          obj[modelKey] = modelValue
        }
        return obj
      }, {})
    })
  },
  methods: {
    ready (editorInstance) {
      console.log(editorInstance)
      this.editorInstance = editorInstance
    },
    edit () {
      this.childContext.visible = true
    },
    preview () {
      this.childContext.visible = false
    },
    print () {
      const bdhtml = this.$refs['markedContent'].innerHTML
      // 设置打印内容
      this.editorInstance.setContent(bdhtml)
      // 打印
      this.editorInstance.execCommand('print')
    },
    getElement (type, key) {
      const style = 'style="width: 150px;border: 1px solid #23adf4;"'
      const span = `<span data-key="${key}" v-else v-html="form.${key}" />`
      const vIf = 'v-if="visible"'
      const vModel = `v-model="form.${key}"`
      const common = `${style} ${vIf} ${vModel}`
      const type2Element = {
        'el-input': `<el-input ${common} />${span}`,
        'el-date-picker': `<el-date-picker ${common} value-format="yyyy-MM-dd" type="date" />${span}`,
        OrganizationSelectTree: `<OrganizationSelectTree ${common} @select="selectChange" /><span data-key="${key}" v-else v-html="form.organizationName" />`,
        'material-list': `<material-list :fields="materialListFields" @deleteItem="deleteItem" :visible="visible" v-model="form.${key}" />`,
        'pay-plan': `<pay-plan :fields="payPlanFields" :visible="visible" v-model="form.${key}" />`
      }
      return type2Element[type]
    },
    compile () {
      const { content } = this.$attrs.params.row
      const that = this
      const keys = {}
      const reg = /\$\{(.*?)\}/g
      const html = `${content}`
      const payPlanFields = []
      const materialListFields = []
      const v_html = html.replace(reg, (a, b) => {
        const [type, key] = b.split(':')
        keys[key] = null
        if (type === 'material-list-column') {
          materialListFields.push(key)
        }
        if (type === 'pay-plan-column') {
          payPlanFields.push(key)
        }
        if (['material-list-column', 'pay-plan-column'].includes(type)) {
          return ''
        }
        const element = this.getElement(type, key)
        return element
      })
      const tableReg = /\<table\swidth\=\"100%\"\sdata\-table\=(.*?)\<\/table>/g
      const finalHTML = v_html.replace(tableReg, (a, b) => {
        const r = /data\-table\=\"(.*?)\"\>/g
        const execArr = Array.from(r.exec(a))
        const type = execArr[1]
        if (['material-list', 'pay-plan'].includes(type)) {
          const key = type === 'material-list' ? 'materialList' : 'payPlan'
          return this.getElement(type, key)
        }
      })
      const Component = Vue.extend({
        store: getStore(),
        router: getRouter(),
        i18n: geti18n(),
        components: { OrganizationSelectTree, materialList, payPlan },
        data () {
          return {
            form: {
              ...keys
            },
            visible: false,
            context: that,
            materialListFields: materialListFields,
            payPlanFields: payPlanFields
          }
        },
        mounted () {
          that.childContext = this
          this.$nextTick(() => {
            const preview_wrapper = document.getElementById('preview_wrapper')
            const tables = preview_wrapper.getElementsByTagName('table')
            console.log(tables)
            for (let i = 0; i < tables.length; i++) {
              const classes = Array.from(tables[i].classList)
              if (
                classes.includes('el-table__header') ||
                classes.includes('el-table__body')
              ) {
                continue
              }
              tables[i].setAttribute('border', '1')
            }
          })
        },
        methods: {
          deleteItem (scope) {
            console.log('preview: deleteItem', scope)
            this.form.materialList.splice(scope.$index, 1)
            console.log(this.form.materialList)
          },
          selectChange (node, fullPathId) {
            const { organizationId, organizationCode, organizationName } = node
            this.form.organizationId = organizationId
            this.form.organizationCode = organizationCode
            this.form.organizationName = organizationName
          }
        },
        template: `<div>${finalHTML}</div>`
      })
      const markedComponent = new Component().$mount()
      this.$refs['markedContent'].appendChild(markedComponent.$el)
    }
  }
}
</script>
<style scoped lang="scss">
.the_contractTemplatePreview_wrapper {
  .button-group {
    padding: 10px;
  }
  .preview_wrapper {
    padding: 10px;
  }
}
</style>
