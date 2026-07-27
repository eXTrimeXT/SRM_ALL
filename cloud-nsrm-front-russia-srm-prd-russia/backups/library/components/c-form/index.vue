<template>
  <el-form
    ref="cForm"
    :model="formModel"
    :rules="rules"
    :show-message="showMessage"
  >
    <el-row :gutter="gutter">
      <el-col
        v-for="(item,index) in formConfData"
        :key="index"
        :span="colSpan"
      >
        <el-form-item
          :prop="item.fieldCode"
          :label="item.fieldName"
        >
          <el-input v-model="formModel[item.fieldCode]" />
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>
<script>
export default {
  name: 'CForm',
  model: {
    prop: 'data',
    event: 'change'
  },
  props: {
    gutter: { // 间距
      type: Number,
      default: 50
    },
    colSpan: { // colspan
      type: Number,
      default: 6
    },
    showMessage: { // 显示信息
      type: Boolean,
      default: true
    },
    rules: { // 规则
      type: Object,
      default: () => {}
    },
    formConfData: { // form 配置数组信息
      type: Array,
      default: () => []
    }
  },
  data () {
    return {

    }
  },
  created () {

  },
  methods: {

  }
}
</script>
