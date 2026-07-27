<!-- 订单明细 -->
<script lang="jsx">
export default {
  name: 'IFieldView',
  props: ['data', 'disabled'],
  data () {
    return {
      form: {}
    }
  },
  computed: {},
  watch: {
    form: {
      deep: true,
      handler (value) {
        this.data.forEach(i => {
          i.filedValue = value[i.fieldCode]
        })
        console.log(this.data)
      }
    },
    data: {
      immediate: true,
      deep: true,
      handler (nVal) {
         this.form = this.data.reduce((last, item) => {
          last[item.fieldCode] = item.filedValue || ''
          return last
        }, {})
      }
    }
  },
  created () {
    console.log('data:::', this.data)
    // this.form = this.data.reduce((last, item) => {
    //   last[item.fieldCode] = item.filedValue || ''
    //   return last
    // }, {})
  },
  mounted () {},
  methods: {
    validate () {
      return new Promise((resolve) => {
        this.$refs.form.validate((flag) => resolve(flag))
      })
    },
    renderFormItems (h, item) {
      const validator = { required: item.required, message: this.$t('contract_mod.required') }
      return (
        <el-col span={6} key={item.perAcceptanceId}>
          <el-form-item
            label={item.fieldName}
            prop={item.fieldCode}
            rules={[validator]}
          >
            {this.renderComponent(h, item)}
          </el-form-item>
        </el-col>
      )
    },
    renderComponent (h, item) {
      const opts = JSON.parse(item.fieldOptions || '{}')
      if (item.fieldType === 'el-select') {
        const { options = '', ...rest } = opts
        return (
          <el-select
            key={item.perAcceptanceId}
            disabled={this.disabled}
            v-model={this.form[item.fieldCode]}
            {...{ attrs: { ...rest } }}
          >
            {options.split(',').map((i) => (
              <el-option label={i} value={i} />
            ))}
          </el-select>
        )
      }
      if (item.fieldType === 'el-input') {
        return (
          <el-input
            key={item.perAcceptanceId}
            disabled={this.disabled}
            v-model={this.form[item.fieldCode]}
            {...{ attrs: { ...opts } }}
          />
        )
      }
      if (item.fieldType === 'el-checkbox') {
        return (
          <el-checkbox
            key={item.perAcceptanceId}
            class="form-item-class"
            disabled={this.disabled}
            v-model={this.form[item.fieldCode]}
            {...{ attrs: { ...opts } }}
          />
        )
      }
      if (item.fieldType === 'el-date-picker') {
        return (
          <el-date-picker
            key={item.perAcceptanceId}
            disabled={this.disabled}
            value-format="yyyy-MM-dd"
            v-model={this.form[item.fieldCode]}
            {...{ attrs: { ...opts } }}
          />
        )
      }
      return null
    }
  },
  render (h) {
    return (
      <el-form props = {{ model: this.form }} ref="form">
        <el-row type="flex" style="flex-wrap: wrap;" gutter={27}>
          {this.data.map((item) => this.renderFormItems(h, item))}
        </el-row>
      </el-form>
    )
  }
}
</script>
<style scoped lang="scss">
.form-item-class {
  position: relative;
  display: inline-block;
  width: 100%;
}
</style>
