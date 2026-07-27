const minix = {
  props: {
    editable: {
      required: true,
      type: Boolean
    },
    componentInfo: {
      required: true,
      type: Object
    },
    value: {
      required: true
    },
    context: {
      required: true
    }
  },
  model: {
    prop: 'value',
    event: 'change'
  },
  computed: {
    code () {
      const { elemKey } = this.componentInfo
      return elemKey
    }
  },
  methods: {
    change (value) {
      this.$emit('change', value)
    }
  }
}

export default minix
