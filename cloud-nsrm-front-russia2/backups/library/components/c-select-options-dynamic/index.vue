<template>
  <el-select
    v-bind="$attrs"
    :value="value"
    filterable
    clearable
    v-on="$listeners"
  >
    <el-option
      v-for="option in options"
      :key="option.value"
      :label="option.label"
      :value="option.value"
    />
  </el-select>
</template>

<script>
export default {
  name: 'CSelectOptionsDynamic',
  props: {
    value: {
      type: [String, Number, Boolean, Array]
    }
  },
  data () {
    return {
      options: []
    }
  },
  computed: {},
  watch: {
    value: {
      handler (n, o) {
        this.initValue()
      }
    }
  },
  mounted () {
    this.initValue()
  },
  methods: {
    initValue () {
      if (!this.value) {
        return
      }
      var optionsTemp = this.value.map(item =>
        item.replace(/(\r|\n|\\s|\t|\,|\;|\，|\；)+/g, ' ')
      )

      let splited = []
      optionsTemp.forEach(item => {
        var templist = item.split(' ')
        templist = templist.map(i => i.trim())
        templist = templist.filter(i => !!i)
        splited = splited.concat(templist)
      })
      const newSet = new Set(splited)
      const arraySet = [...newSet].sort()
      if (JSON.stringify(optionsTemp) !== JSON.stringify(arraySet)) {
        this.$emit('input', arraySet)
        // console.log('this.options', this.options)
      }
      this.options = arraySet.map(i => {
        return {
          label: i,
          value: i
        }
      })
    }
  }
}
</script>

<style lang="scss">
.c-select-load-more {
  width: 100%;
  padding: 0 8px;

  .el-button {
    margin-left: 0;
  }
}

.c-select-no-more {
  padding: 8px 0;
  text-align: center;
  color: #cccccc;
}
</style>
