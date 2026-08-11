import cloneDeep from 'lodash/cloneDeep'
import extraComponent, { basic } from './extraComponent'

const form = Symbol('form')

export default {
  name: 'BaseForm',
  components: { ...extraComponent },
  props: {
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
    // 接口函数
    api: {
      type: Function
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
      Model: {},
      originModel: {},
      form
    }
  },
  mounted () {},
  computed: {
    // 根据formItem计算出实际需要让页面渲染的真正的_formItem数据
    _formItems () {
      // this.Model中的值改变触发computed
      let _formItems = []
      _formItems = this.formItems.map(item =>
        this.computeFormItem(item, this.Model)
      )
      return _formItems
    },
    showMessage () {
      return this.$attrs['show-message'] !== false
    },
    statusIcon () {
      return this.$attrs['status-icon'] !== false
    },
    inline () {
      return this.$attrs.inline !== false
    },
    disabled () {
      return this.$attrs.disabled !== false
    }
  },
  watch: {
    // 使用watch观察父组件传入的formItems,初始化Model对象(只调用一次)
    formItems: {
      handler () {
        this.formItems.forEach(formItem => {
          if (!formItem.uiAttrs || !formItem.uiAttrs.key) return // 跳过没有key的属性(插槽)
          this.$set(
            this.Model,
            formItem.uiAttrs.key,
            formItem.uiAttrs.value ? formItem.uiAttrs.value : ''
          )
        })
        this.originModel = cloneDeep(this.Model)
      },
      deep: true,
      immediate: true
    },
    mergeForm: {
      handler () {
        Object.assign(this.Model, this.mergeForm)
        console.log('mergeForm change: ', this.Model)
        this.$emit('update:mergeForm', this.Model)
      },
      deep: true,
      immediate: true
    },
    Model: {
      handler () {
        console.log('Model change: ', this.Model)
        this.$emit('update:mergeForm', this.Model)
      },
      deep: true,
      immediate: true
    }
  },
  methods: {
    computeFormItem (formItem, Model) {
      let itemAttrs = formItem.itemAttrs
      if (itemAttrs.label && typeof itemAttrs.label === 'function') {
        itemAttrs = Object.assign(itemAttrs, { label: itemAttrs.label() })
      }
      const item = { ...formItem, itemAttrs }
      // 表单控件的类型
      let tag = item.tag || 'input'
      // 对应到组件映射表
      let basicItem = basic[tag]
      item.tag = basicItem.component
      // 继承基类的属性
      item.uiAttrs = Object.assign({}, basicItem.uiAttrs, item.uiAttrs)
      // 获取动态 Attributes
      if (item.computedUIAttrs) {
        item.uiAttrs = Object.assign(item.uiAttrs, item.computedUIAttrs(Model))
      }
      // 条件渲染
      item._ifRender = item.ifRender ? item.ifRender(Model) : true
      // 防止表单提交时存在多余 key
      if (!item._ifRender) {
        delete Model[item.uiAttrs.key]
      }
      // form-item 配置
      return item
    },
    mergeModel () {
      Object.assign(this.Model, this.mergeForm)
      console.log('mergeForm', this.Model, this.mergeForm)
    },
    // 提交按钮
    handleSubmit () {
      this.$refs[form].validate(async valid => {
        if (valid) {
          try {
            let res = await this.api(this.Model)
            this.$emit('after-submit', res)
          } catch (e) {
            console.log(e)
          }
        }
      })
    },
    handleReset () {
      this.Model = cloneDeep(this.originModel)
    },
    renderFormItem (h, formItem, index) {
      const { itemAttrs, uiAttrs, _ifRender, listeners, slot, tag } = formItem
      const span = itemAttrs.span || this.wrapperCol.span
      if (slot) {
        const scopedSlots = {
          default: scope => this.$scopedSlots[slot](scope)
        }
        return _ifRender ? (
          <ElCol span={span}>
            <ElFormItem
              key={`${index}_${uiAttrs.key}`}
              prop={uiAttrs.key}
              {...{ scopedSlots }}
              {...{ props: itemAttrs }}
            />
          </ElCol>
        ) : null
      }
      return _ifRender ? (
        <ElCol span={span}>
          <ElFormItem
            key={`${index}_${uiAttrs.key}`}
            prop={uiAttrs.key}
            {...{ props: itemAttrs }}
          >
            {this.isBaseComponent(tag) ? (
              <tag
                vModel={this.Model[uiAttrs.key]}
                {...{ attrs: uiAttrs }}
                {...{ on: listeners || {} }}
              />
            ) : (
              <tag
                vModel={this.Model[uiAttrs.key]}
                {...{ props: uiAttrs }}
                {...{ on: listeners || {} }}
              />
            )}
          </ElFormItem>
        </ElCol>
      ) : null
    },
    isBaseComponent (tag) {
      return tag.indexOf('base') > -1
    },
    getModel () {
      return cloneDeep(this.Model)
    },
    validate () {
      return new Promise((rs, rj) => {
        this.$refs[form].validate(flag => {
          if (flag) {
            rs(flag)
          } else {
            rj(flag)
          }
        })
      })
    }
  },
  render (h) {
    const { gutter } = this.wrapperCol
    return (
      <ElForm
        on={this.$listeners}
        {...{ attrs: { ...this.attrs, model: this.Model } }}
        ref={form}
        api={this.api}
        show-message={this.showMessage}
        statusIcon={this.statusIcon}
        inline={this.inline}
        disabled={this.disabled}
      >
        <ElRow gutter={gutter}>
          {this._formItems.map((formItem, index) =>
            this.renderFormItem(h, formItem, index)
          )}
        </ElRow>
      </ElForm>
    )
  }
}
