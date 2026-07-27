<template>
  <el-form
    :ref="form"
    v-bind="$attrs"
    :model="mergeForm"
    :show-message="showMessage"
    :status-icon="statusIcon"
  >
    <srm-row>
      <template v-for="(item, index) in _formItems">
        <srm-col
          v-if="item._ifRender"
          :key="`${index}_${item.uiAttrs.key}`"
          :initCol="24 / item.uiAttrs.span || 24 / wrapperCol.span"
        >
          <el-form-item
            :class="item.itemAttrs.className"
            v-bind="item.itemAttrs || {}"
            :prop="item.uiAttrs.key"
          >
            <!--将表单内部的数据通过作用域插槽传给外部-->
            <slot
              v-if="item.slot"
              :name="item.slot"
              :scope="mergeForm"
            />
            <component
              :is="item.tag"
              v-else
              v-model="mergeForm[item.uiAttrs.key]"
              :class="item.itemAttrs.className"
              v-bind="item.uiAttrs || {}"
              v-on="item.listeners || {}"
              @change="$forceUpdate()"
            />
          </el-form-item>
        </srm-col>
      </template>
    </srm-row>
  </el-form>
</template>

<script>
import extraComponent, { basic } from './extraComponent'
import { findComponentUpwardByProp, proxyProp } from '@/utils/util'
import { uniqueId } from 'lodash'

const form = uniqueId('srm_form') // 保证每个实例有独一无二的标志位
export default {
  name: 'BaseForm',
  components: { ...extraComponent },
  props: {
    formName: {
      type: String,
      default: 'mergeForm'
    },
    formItems: {
      type: Array,
      required: true
    },
    submit: {
      type: Boolean,
      default: true
    },
    reset: {
      type: Boolean,
      default: true
    },
    // 传入mergeForm允许父组件修改内部Model对象
    mergeForm: {
      type: Object,
      default: () => {}
    },
    wrapperCol: {
      type: Object,
      default: () => ({
        span: 6,
        gutter: 40
      })
    }
  },
  data () {
    return {
      originModel: {},
      form
    }
  },
  computed: {
    // 根据formItem计算出实际需要让页面渲染的真正的_formItem数据
    _formItems () {
      // this.Model中的值改变触发computed
      let _formItems = []
      _formItems = this.formItems.map(item =>
        this.computeFormItem(item, this.mergeForm)
      )
      return _formItems
    },
    showMessage () {
      return this.$attrs['show-message'] && this.$attrs['show-message'] !== false
    },
    statusIcon () {
      return this.$attrs['status-icon'] && this.$attrs['status-icon'] !== false
    }
  },
  watch: {
    // 使用watch观察父组件传入的formItems,初始化Model对象(只调用一次)
    formItems: {
      handler () {
        this.formItems.forEach(formItem => {
          if (!formItem.attrs || !formItem.attrs.key) return // 跳过没有key的属性(插槽)
          this.$set(
            this.mergeForm,
            formItem.attrs.key,
            formItem.attrs.value ? formItem.attrs.value : ''
          )
        })
        this.originModel = JSON.parse(JSON.stringify(this.mergeForm))
      },
      deep: true,
      immediate: true
    },
    mergeForm: {
      handler () {
        this.$emit('update:mergeForm', this.mergeForm)
      },
      deep: true,
      immediate: true
    }
  },
  mounted () {
    // 代理父组件的mergeForm属性
    let parentComponent = findComponentUpwardByProp(this, this.formName)
    if (parentComponent) {
      parentComponent[this.formName] = proxyProp(parentComponent[this.formName])
    } else {
      throw new Error('can not find parentComponent')
    }
    // mounted钩子中formItems是空数组,所以不在mounted里面操作formItems
  },
  methods: {
    validate () {
      return new Promise((resolve, reject) => {
        this.$refs[form].validate((flag, obj) => {
          resolve({ flag, obj })
        })
      })
    },
    // 清除校验，供外部调用需要清除爆红使用
    clearValidate () {
      this.$refs[form].clearValidate()
    },
    computeFormItem (formItem, Model) {
      let itemAttrs = formItem.itemAttrs
      if (itemAttrs.label && typeof itemAttrs.label === 'function') {
        itemAttrs = Object.assign(itemAttrs, { label: itemAttrs.label() })
      }
      const item = { ...formItem }
      // 表单控件的类型
      let tag = item.tag || 'input'
      // 对应到组件映射表
      let basicItem = basic[tag]
      item.tag = basicItem.component
      // 继承基类的属性
      item.uiAttrs = Object.assign({}, basicItem.attrs, item.uiAttrs)
      // 获取动态 Attributes
      if (item.computedUIAttrs) {
        item.uiAttrs = Object.assign(item.uiAttrs, item.computedUIAttrs(Model))
      }
      // 获取动态 itemAttrs
      if (item.computedItemAttrs) {
        item.itemAttrs = Object.assign(
          item.itemAttrs,
          item.computedItemAttrs(Model)
        )
      }
      // 条件渲染
      item._ifRender = item.ifRender ? item.ifRender(Model) : true
      // 防止表单提交时存在多余 key
      if (!item._ifRender) {
        delete Model[item.uiAttrs.key]
      }
      // console.log(item);
      // form-item 配置
      return item
    },
    mergeModel () {
      Object.assign(this.mergeForm, this.mergeForm)
    },
    handleReset () {
      this.mergeForm = JSON.parse(JSON.stringify(this.originModel))
    }
  }
}
</script>
