import Store from './store'
export function createStore (initialState = {}) {
  const store = new Store()
  Object.keys(initialState).forEach(key => {
    store.$set(store.states, key, initialState[key])
  })
  return store
}

export const RenderSelect = {
  name: 'dictionary-select',
  props: ['code', 'transformOptions', 'store'],
  methods: {
    renderOptions (h) {
      if (!this.options || !this.options.length) {
        return null
      }
      const list = this.options
      return list.map((option, index) => (
        <ElOption
          label={option.label}
          value={option.value}
          key={`${option.value}_${index}`}
          disabled={!!option.disabled}
        />
      ))
    }
  },
  computed: {
    dictionary () {
      return this.store.states
    },
    options () {
      if (!this.dictionary) return []
      const options = this.dictionary[this.code]
      // console.log('[options]', store, this.code, options)
      if (
        this.transformOptions &&
        typeof this.transformOptions === 'function'
      ) {
        return this.transformOptions(options)
      }
      return options
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
