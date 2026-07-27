import QueryFormButton from './query-form-button'
import QueryFormItem from './query-form-item'

import _chunk from 'lodash/chunk'

export default {
  name: 'CQueryForm',
  props: {
    form: {
      type: Array,
      default () {
        return []
      }
    },
    // 列数
    queryFormCols: {
      type: Number,
      default: 3
    },
    queryParams: {
      type: Object,
      default () {
        return {}
      }
    },
    formModel: {
      type: Object,
      default () {
        return {}
      }
    },
    showQueryBtn: {
      type: Boolean,
      default: true
    },
    showResetBtn: {
      type: Boolean,
      default: true
    }
  },
  data () {
    return {
      expand: false,
      formBtn: {
        formType: 'formBtn'
      }
    }
  },
  computed: {
    allExpand () {
      return this.form.every((formItem) => {
        return formItem.defaultExpand === true
      })
    },
    allForm () {
      const all = [].concat(this.form)
      // all.push(this.formBtn)
      return _chunk(all, this.queryFormCols)
    },
    showExpand () {
      return !this.allExpand
    },
    span () {
      return 24 / this.queryFormCols
    }
  },
  methods: {
    handleExpand () {
      this.expand = !this.expand
      this.$emit('expand', this.expand)
    }
  },
  render (h) {
    let defaultExpandForm = this.form.filter(item => item.defaultExpand === true)
    // defaultExpandForm.push(this.formBtn)
    // 切割成 cols 列
    defaultExpandForm = _chunk(defaultExpandForm, this.queryFormCols)

    const getQueryBtnRender = () => {
      if (this.formBtn.formType === 'formBtn' && (this.showExpand || this.showQueryBtn || this.showResetBtn)) {
        return (
          <QueryFormButton
            showExpand={ this.showExpand }
            showQueryBtn={ this.showQueryBtn }
            showResetBtn={ this.showResetBtn }
            expand={ this.expand }
            onExpand={ this.handleExpand }
            onQuery={ () => this.$emit('query') }
            onReset={ () => this.$emit('reset') }
          />
        )
      }
    }

    const getFormItemRender = (row) => {
      return row.map((row) => {
        const col = row.map((item) => {
          return (
            <ElCol span={ this.span }>
              <QueryFormItem { ...{
                props: {
                  ...item,
                  queryParams: this.queryParams,
                  formModel: this.formModel
                },
                attrs: item,
                on: {
                  inputEnter: () => this.$emit('query')
                }
              }}
              />
            </ElCol>
          )
        })
        return (
          <ElRow type="flex" gutter={ 24 }>
            { col }
          </ElRow>
        )
      })
    }

    const defaultExpandFormRender = getFormItemRender(defaultExpandForm)
    const allRender = getFormItemRender(this.allForm)
    const queryBtnRender = getQueryBtnRender()

    return (
      <div class="c-query-form">
        <div class="c-query-form-sec">
          { this.expand ? allRender : defaultExpandFormRender }
        </div>
        <div class="c-query-btn-sec">
          {this.formBtn.formType === 'formBtn' ? queryBtnRender : '' }
        </div>
      </div>
    )
  }
}
