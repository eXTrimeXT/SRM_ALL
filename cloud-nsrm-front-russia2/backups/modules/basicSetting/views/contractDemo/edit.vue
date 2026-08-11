<template>
  <el-container
    class="the_contractTemplateEdit_wrapper"
    direction="vertical"
  >
    <vue-ueditor-wrap
      :destroy="true"
      style="display: none"
      :config="customConfig"
      @ready="ready"
    />
    <el-main>
      <base-form
        ref="form"
        class="base-form"
        :form-items="formItems"
        :merge-form="mergeForm"
        :inline="false"
        :status-icon="false"
        :show-message="true"
      />
      <!-- <el-form
        ref="mode"
        :rules="rules"
        :model="form"
        label-width="80px"
        label-position="top"
        class="form-incontainer"
      >
        <el-row :gutter="15">
          <el-col :span="6">
            <el-form-item
              label="合同名称"
              prop="orderName"
              :label-width="formLabelWidth"
            >
              <el-input v-model="form.orderName" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item
              label="合同模板"
              prop="modelHeadId"
              :label-width="formLabelWidth"
            >
              <el-select
                clearable
                v-model="form.modelHeadId"
                @change="changeHandle"
              >
                <el-option
                  v-for="item in modelList"
                  :key="item.id"
                  :value="item.value"
                  :label="item.label"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item
              label="标准合同"
              prop="enable"
              :label-width="formLabelWidth"
            >
              <el-select disabled v-model="form.enable">
                <el-option label="是" value="Y" key="Y" />
                <el-option label="否" value="N" key="N" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form> -->
      <!-- <div class="button_group" v-if="!!form.modelHeadId">

      </div> -->
      <div
        id="preview_wrapper"
        class="preview_wrapper"
      >
        <div
          ref="markedContent"
          :contenteditable="contenteditable"
          class="paper"
          style="width: 100%;"
        />
      </div>
      <div
        id="template"
        class="template"
        style="display: none"
      >
        <div ref="template" />
      </div>
      <c-toolbar>
        <template slot="right">
          <el-button
            v-if="!!mergeForm.modelHeadId"
            type="primary"

            @click="submitHandle"
          >
            保存
          </el-button>
          <el-button
            v-if="!!mergeForm.modelHeadId"
            type="primary"
            @click="edit"
          >
            编辑
          </el-button>
          <el-button
            v-if="!!mergeForm.modelHeadId && mergeForm.enable === 'N'"
            type="primary"
            @click="html2diff"
          >
            对比更改
          </el-button>
          <el-button
            v-if="!!mergeForm.modelHeadId"
            @click="preview"
          >
            预览
          </el-button>
          <el-button
            v-if="!!mergeForm.modelHeadId"
            @click="print"
          >
            打印
          </el-button>
          <el-button

            @click="cancel"
          >
            返回
          </el-button>
        </template>
      </c-toolbar>
    </el-main>
    <srm-dialog
      title="对比更改"
      size="large"
      :visible.sync="diffVisible"
    >
      <div style="overflow: hidden;">
        <!-- <div ref="diffhtml" class="conetnt paper"></div> -->
        <div
          class="conetnt paper"
          v-html="diffhtml"
        />
      </div>
    </srm-dialog>
  </el-container>
</template>

<script>
import Vue from 'vue'
import VueUeditorWrap from 'vue-ueditor-wrap'
import { parseTime, adaptDictData, loadJS } from '@/utils'
import { getDictItemList } from '@/api/common'
import { tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import cloneDeep from 'lodash/cloneDeep'
import { getStore, getRouter } from '@/main'
import OrganizationSelectTree from 'lib@/components/organization-cascader'
import BaseForm from 'lib@/components/BaseForm'

import materialList from './material-list'
import payPlan from './pay-plan'

export default {
  name: 'Edit',
  components: { VueUeditorWrap, CToolbar, BaseForm },
  mixins: [tabTodoMixin],
  data () {
    return {
      diffhtml: null,
      diffVisible: false,
      contenteditable: false,
      editorInstance: null,
      formLabelWidth: '100px',
      placeholder2html: {},
      modelList: [],
      modelLine: [],
      mergeForm: {},
      formItems: [
        {
          itemAttrs: {
            label: '合同名称',
            rules: [{ required: true, message: '请填写', type: 'string' }]
          },
          uiAttrs: {
            key: 'orderName'
          }
        },
        {
          tag: 'select',
          itemAttrs: {
            label: '合同模板',
            rules: [{ required: true, message: '请填写', type: 'number' }]
          },
          uiAttrs: {
            key: 'modelHeadId',
            remote: this.fetchModels
          },
          listeners: {
            change: this.changeHandle
          }
        },
        {
          tag: 'select',
          itemAttrs: { label: '标准合同' },
          uiAttrs: {
            key: 'enable',
            disabled: true,
            options: [
              { id: 'N', value: 'N', label: this.$t('common.no') },
              { id: 'Y', value: 'Y', label: this.$t('common.yes') }
            ]
          }
        }
      ],
      rules: {
        orderName: [{ required: true, message: '请填写' }],
        modelHeadId: [{ required: true, message: '请选择' }]
      },
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
      visible: true
    }
  },
  computed: {
    editable () {
      if (this.$attrs.params.flag === 'add') {
        return false
      }
      return this.$attrs.params.row.status !== 'DRAFT'
    }
  },
  mounted () {
    this.initDictionary()
    const { flag, row } = this.$attrs.params
    if (flag === 'edit') {
      this.$api.cm.modelLine.queryById(row.orderId).then(res => {
        const { orderName, orderId, modelHeadId } = row
        const { order } = res.data
        Object.assign(this.mergeForm, order)
        this.compile(order.content)
        this.modelLine = res.data.modelLines
        this.childContext.form = this.modelLine.reduce((obj, i) => {
          const { modelKey, modelValue } = i
          obj[modelKey] = modelValue
          return obj
        }, {})
      })
    }
  },
  methods: {
    fetchModels () {
      return this.$api.cm.modelList().then(res =>
        res.data.map(i => ({
          id: i.modelCode,
          label: i.modelName,
          value: i.modelHeadId
        }))
      )
    },
    html2diff () {
      this.preview()
      this.$nextTick(async () => {
        const oldContent = await this.buildOldContent()
        const newContent = this.$refs['markedContent'].innerHTML
        console.log(oldContent)
        if (typeof Worker === 'undefined') {
          loadJS('./htmldiff.js', () => {
            this.diffhtml = getHTMLDiff(oldContent, newContent)
            this.diffVisible = true
          })
        } else {
          const worker = new Worker('./htmldiff.js')
          worker.postMessage({
            newVersion: newContent,
            oldVersion: oldContent
          })
          worker.onmessage = evt => {
            this.diffhtml = evt.data
            this.diffVisible = true
          }
        }
      })
    },
    async buildOldContent () {
      const res1 = await this.$api.cm.getById(this.mergeForm.modelHeadId)
      const res2 = await this.$api.cm.modelLine.getModelLine(
        this.mergeForm.modelHeadId
      )
      const { flag } = this.$attrs.params
      const arr = flag === 'add' ? res2.data : this.modelLine
      const initialModelValue = arr.reduce((obj, i) => {
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
      const content = res1.data.content
      this.templateCompile(content, initialModelValue)
      return this.$refs.template.innerHTML
    },
    initData () {
      this.$api.cm.modelList().then(res => {
        this.modelList = res.data.map(i => ({
          id: i.modelCode,
          label: i.modelName,
          value: i.modelHeadId
        }))
      })
    },
    print () {
      this.preview()
      this.$nextTick(() => {
        const bdhtml = this.$refs['markedContent'].innerHTML
        // 设置打印内容
        this.editorInstance.setContent(bdhtml)
        // 打印
        this.editorInstance.execCommand('print')
      })
    },
    edit () {
      this.childContext.visible = true
      if (this.mergeForm.enable === 'N') {
        this.contenteditable = true
      }
    },
    preview () {
      this.childContext.visible = false
      this.contenteditable = false
    },
    changeHandle (val) {
      this.mergeForm.modelHeadId = val
      if (!val) return
      this.compile()
    },
    submitHandle () {
      this.preview()
      this.$nextTick(() => this.submit())
    },
    submit () {
      const { row, flag } = this.$attrs.params
      const cloneFrom = cloneDeep(this.childContext.form)
      const modelLines = []
      const form = this.mergeForm
      const { orderId, orderNo, modelHeadId } = form
      for (const [key, value] of Object.entries(cloneFrom)) {
        const modelLineId = (this.modelLine.find(i => key === i.modelKey) || {})
          .modelLineId
        modelLines.push({
          orderId,
          modelLineId: modelLineId || null,
          modelKey: key,
          modelValue: value,
          modelHeadId
        })
      }
      const bdhtml = this.$refs['markedContent'].innerHTML
      const reg = /\<span\sdata-key(.*?)\<\/span\>/g
      const content = bdhtml.replace(reg, (a, b) => {
        const key = b.split('"')[1]
        return this.placeholder2html[key]
      })
      const data = {
        modelLines,
        order: { ...form, content }
      }
      data.order.payPlan = JSON.stringify(data.order.payPlan)
      data.order.materialList = JSON.stringify(data.order.materialList)
      if (flag === 'add') {
        this.$api.cm.modelLine.add(data).then(res => {
          this.$message({
            type: 'success',
            message: res.message
          })
          this.cancel()
        })
      } else {
        this.$api.cm.modelLine.modify(data).then(res => {
          this.$message({
            type: 'success',
            message: res.message
          })
          this.cancel()
        })
      }
    },
    cancel () {
      const { row, flag } = this.$attrs.params
      if (flag == 'add') {
        this.$emit('tab-remove', 'add')
      } else {
        this.$emit(
          'tab-remove',
          `edit_${row.modelHeadId ? row.modelHeadId : ''}`
        )
      }
      this.__setTabTodo('contractDemoList.getQuerydata')
    },
    initDictionary () {
      // const codes = ["CONTRACT_MODEL_STATUS"];
      // const params = codes.map(i => ({ dictCode: i }));
      // getDictItemList(params).then(res => {
      //   const [CONTRACT_MODEL_STATUS] = res.data;
      //   this.contractModelStatus = adaptDictData(
      //     CONTRACT_MODEL_STATUS.CONTRACT_MODEL_STATUS
      //   );
      //   this.selectDictionary = {
      //     returnStatus: this.returnStatus
      //   };
      // });
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
        'material-list': `<material-list @deleteItem="deleteItem" :visible="visible" v-model="form.${key}" />`,
        'pay-plan': `<pay-plan :visible="visible" v-model="form.${key}" />`
      }
      return type2Element[type]
    },
    templateCompile (modeContent, initialModelValue) {
      this.$refs['template'].innerHTML = ''
      const reg = /\$\{(.*?)\}/g
      const html = `${modeContent}`
      const v_html = html.replace(reg, (a, b) => {
        const key = b.split(':')[1]
        if (key === 'fullPathId') {
          return '<span data-key="fullPathId" v-model="form.organizationName" />'
        }
        const element = `<span data-key="${key}" v-html="form.${key}" />`
        return element
      })
      const Component = Vue.extend({
        store: getStore(),
        router: getRouter(),
        components: { OrganizationSelectTree, materialList, payPlan },
        data () {
          return { form: { ...initialModelValue } }
        },
        mounted () {
          this.$nextTick(() => {
            const template = document.getElementById('template')
            const tables = template.getElementsByTagName('table')
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
        template: `<div>${v_html}</div>`
      })
      const template = new Component().$mount()
      this.$refs['template'].appendChild(template.$el)
    },
    async compile (modeContent) {
      const { modelHeadId } = this.mergeForm
      if (!modelHeadId) {
        return
      }

      this.$refs['markedContent'].innerHTML = ''

      let content = modeContent
      if (!modeContent) {
        const res = await this.$api.cm.getById(modelHeadId)
        const { enable, content: _content } = res.data
        content = _content
        this.mergeForm.enable = enable
      }

      const that = this
      const keys = {}
      const placeholder2html = {}
      const reg = /\$\{(.*?)\}/g
      const html = `${content}`
      const v_html = html.replace(reg, (a, b) => {
        const [type, key] = b.split(':')
        keys[key] = null
        if (['materialList', 'payPlan'].includes(key)) {
          keys[key] = []
        }
        const element = this.getElement(type, key)
        placeholder2html[key] = a
        return element
      })
      console.log(keys)
      this.placeholder2html = placeholder2html
      const { flag } = this.$attrs.params
      const Component = Vue.extend({
        store: getStore(),
        router: getRouter(),
        components: { OrganizationSelectTree, materialList, payPlan },
        data () {
          return { form: { ...keys }, visible: false, context: that }
        },
        mounted () {
          that.childContext = this
          if (flag === 'add') {
            this.$api.cm.modelLine.getModelLine(modelHeadId).then(res => {
              const form = res.data.reduce((obj, i) => {
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
              Object.assign(this.form, form)
            })
          }
          this.$nextTick(() => {
            const preview_wrapper = document.getElementById('preview_wrapper')
            const tables = preview_wrapper.getElementsByTagName('table')
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
          selectChange (node, fullPathId) {
            const { organizationId, organizationCode, organizationName } = node
            this.mergeForm.organizationId = organizationId
            this.mergeForm.organizationCode = organizationCode
            this.mergeForm.organizationName = organizationName
          }
        },
        template: `<div>${v_html}</div>`
      })
      const markedComponent = new Component().$mount()
      this.$refs['markedContent'].appendChild(markedComponent.$el)
    },
    ready (editorInstance) {
      this.editorInstance = editorInstance
    }
  }
}
</script>
<style scoped lang="scss">
.the_contractTemplateEdit_wrapper {
  .form-incontainer {
    padding: 15px 0 0;
  }
  .button_group {
    padding: 10px 25px;
  }
  .preview_wrapper {
    padding: 15px;
  }
  .conetnt {
    ins {
      background-color: #cfc;
      text-decoration: none;
    }
    del {
      color: #999;
      background-color: #fec8c8;
    }
  }
  .paper {
    position: relative;
    padding: 0 40px 100px 40px;
    background: #fff;
    border: 1px solid #eee;
    float: left;
    margin: 10px;
    box-shadow: 0 0 12px 0 rgba(0, 0, 0, 0.06), 0 0 0 1px rgba(0, 0, 0, 0.04);
  }
  .paper::after,
  .paper::before {
    content: "";
    position: absolute;
    bottom: 6px;
    width: 100px;
    height: 1px;
    z-index: -1;
    box-shadow: 0 2px 12px 5px rgba(0, 0, 0, 0.3);
  }
  .paper::after {
    left: 4px;
    transform: rotate(-6deg);
  }
  .paper::before {
    right: 4px;
    transform: rotate(6deg);
  }
  .base-form {
    padding: 15px 30px 0;
  }
}
</style>
