import Vue from 'vue'

export default Vue.extend({
  data () {
    return {
      states: {}
    }
  },
  methods: {
    addDictionary (code, dictionary) {
      this.$set(this.states, code, dictionary)
    }
  }
})
