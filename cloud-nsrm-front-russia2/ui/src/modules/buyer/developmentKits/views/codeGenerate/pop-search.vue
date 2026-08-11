<template>
  <!-- 如果用在el-form里面需要加style="margin-top:28px;" -->
  <!--
    <PopSearch
      style="margin-top:28px;"
      v-model="mergeForm.orderNum"
      :inputDisabled="true"
      @popSearch="orderSearchHandel"
      @input="orderInfoClear"
      :disabled="abc"
    /> -->
  <div class="c-pop-search">
    <el-input
      ref="CPopSearch"
      :value="value"
      v-bind="$attrs"
      :disabled="inputDisabled"
      v-on="$listeners"
      @input="handleInput"
      @blur="handleBlur"
      @change="inputChange"
    />
    <!-- input框禁用 可编辑状态下才需要显示 -->
    <em
      v-if="inputDisabled && !disabled && value"
      class="el-icon-circle-close selectBtn clearableBtn"
      @click="inputClearHandel"
    />
    <el-button
      icon="iconfont iconselect"
      class="selectBtn"
      @click="searchBtnClick"
    />
  </div>
</template>

<script>
export default {
  name: 'PopSearch',
  props: {
    value: {
      type: String
    },
    name: {
      type: String
    },
    inputDisabled: { // 只有按钮触发弹框查询的时候设置为true
      type: Boolean,
      default: false
    },
    disabled: {
      type: Boolean,
      default: false
    },
    cusClear: { // 显示自定义的清除按钮
      type: Boolean,
      default: false
    }
  },
  methods: {
    handleInput (value) {
      this.$emit('input', value)
    },
    inputChange (value) {
      this.$emit('input', value)
    },
    handleBlur () {
      this.$emit('blur')
    },
    blur () {
      this.$refs.input.blur()
    },
    // 点击input自带的清楚按钮
    clear () {
      this.$emit('input', '')
    },
    // 按钮清空
    inputClearHandel () {
      this.$emit('input', '')
    },
    // 点击搜索按钮
    searchBtnClick () {
      this.$emit('popSearch')
    }
  }
}
</script>
<style>
.c-pop-search{
  position: relative;
}
.c-pop-search .el-input__inner{
  padding-right: 28px;
}
.c-pop-search .selectBtn{
  position: absolute;
  width: 24px;
  top: 2px;
  bottom: 2px;
  right: 2px;
  z-index: 100;
  line-height: 26px;
  padding: 0;
  cursor: pointer;
  min-width: 24px;
  border: 0;
}
.c-pop-search .selectBtn.clearableBtn{
  right: 26px !important;
  font-size: 14px;
  text-align: center;
  color: #999;
}
</style>
