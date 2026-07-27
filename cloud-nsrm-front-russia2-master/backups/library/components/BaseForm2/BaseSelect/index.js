export default {
  name: 'base-select',
  props: {
    options: {},
    axios: {
      type: Function
    }
  },
  data () {
    return { optionComponents: [] }
  },
  methods: {
    renderOptions (h) {
      return this.optionComponents.map(option => (
        <ElOption
          label={option.label}
          value={option.value}
          key={option.value}
        />
      ))
    }
  },
  mounted () {
    const { axios, options } = this
    let lock = false
    if (options && !lock) {
      if (Array.isArray(options)) {
        this.optionComponents = options
      }
      if (typeof options === 'function') {
        this.optionComponents = options()
      }
      lock = true
    }
    if (axios && typeof axios === 'function' && !lock) {
      axios().then(options => {
        this.optionComponents = options
      })
    }
  },
  render (h) {
    return (
      <ElSelect value={this.value} on={this.$listeners} attrs={this.$attrs}>
        {this.renderOptions(h)}
      </ElSelect>
    )
  }
}
