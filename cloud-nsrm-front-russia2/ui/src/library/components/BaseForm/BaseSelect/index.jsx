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
      const list = this.optionComponents || []
      return list.map(option => (
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
      if (typeof options === 'function') {
        this.optionComponents = options()
      } else {
        this.optionComponents = options
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
