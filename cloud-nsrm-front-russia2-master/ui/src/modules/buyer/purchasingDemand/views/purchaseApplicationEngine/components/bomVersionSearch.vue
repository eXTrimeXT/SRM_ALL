<template>
  <div class="bom-version-search">
    <el-input
      v-model="value"
      clearable
      :disabled="disabled"
      @input="inputChange"
      @clear="clear"
    />
    <el-button
      icon="iconfont iconselect"
      class="bom-version-search__btn"
      :disabled="disabled"
      @click="openDialog"
    />
  </div>
</template>
<script>
export default {
  name: 'BomVersionSearch',
  props: {
    disabled: {
      type: Boolean,
      default: false
    },
    inputModel: {
      type: [String, Number],
      default: ''
    }
  },
  data () {
    return {
      value: ''
    }
  },
  watch: {
    inputModel: {
      handler (val) {
        this.value = val
      },
      deep: true,
      immediate: true
    }
  },
  methods: {
    openDialog () {
      this.$emit('openDialog')
    },
    inputChange () {
      if (this.value) {
        this.value = this.inputModel
      }
    },
    clear () {
      this.$emit('clear')
    }

  }
}
</script>
<style lang="scss" scoped>
.bom-version-search {
    position: relative;

    &__btn {
        position: absolute;
        top: 1px;
        bottom: 1px;
        right: 1px;
        border: none;
        min-width: 20px;
        border-radius: 0 4px 4px 0;
        padding: 4px !important;
        color: #96999c;

        &:hover {
            color: #0077ff;
            border-color: #2994ff;
            background-color: #FFF;
        }
    }
}
</style>
<style>
.bom-version-search .el-input__suffix {
    right: 20px;
}
</style>
