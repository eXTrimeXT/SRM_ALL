/*
 * File Created: 2021-06-17
 * Author: zhaomz1
 */
<template>
  <el-cascader
    ref="categoryComps"
    style="width: 100%;"
    :options="selectTreeOptions"
    :filterable="filterable"
    :disabled="disabled"
    :placeholder="placeholderText"
    :clearable="clearable"
    :separator="separator"
    :collapse-tags="collapseTags"
    :props="{
      checkStrictly: checkStrictly,
      value: 'categoryId',
      label: 'categoryName',
      children: 'children',
      emitPath: false,
      expandTrigger: expandTrigger,
      multiple: multiple
    }"
    :value="value"
    @change="treeselectChange"
  />
</template>
<script>
import { store, mutations } from './store'

function walk (list, callback, deep = 0) {
  return list.map(it => {
    const result = callback({ ...it }, deep)
    if (it.children && it.children.length > 0) {
      result.children = walk(
        it.children,
        callback,
        deep + 1
      )
    } else {
      delete it.children
    }
    return result
  })
}

export default {
  name: 'CategoryCascader',
  components: {},
  model: {
    event: 'change',
    value: 'value'
  },
  props: {
    disabled: {
      type: Boolean,
      default: false
    },
    value: {
      type: [String, Number],
      default: null
    },
    placeholder: {
      type: String,
      default: ''
    },
    separator: {
      type: String,
      default: '/'
    },
    expandTrigger: {
      type: String,
      default: 'click'
    },
    clearable: {
      type: Boolean,
      default: true
    },
    filterable: {
      type: Boolean,
      default: true
    },
    collapseTags: {
      type: Boolean,
      default: true
    },
    multiple: {
      type: Boolean,
      default: true
    },
    checkStrictly: { // 不关联父级和子集节点 true 不关联 false 关联
      type: Boolean,
      default: true
    },
    scope: {
      type: Object,
      default: () => {}
    },
    categoryData: {
      type: Array,
      default: () => []
    },
    remote: { // 组件内接口查询
      type: Boolean,
      default: true
    }
  },
  data () {
    return {
      selectTreeOptions: [],
      catLavel: this.$store.getters.catLavel
    }
  },
  computed: {
    placeholderText () {
      return this.placeholder || this.$t('dataConfMod.msgCategoryNormalizer')
    }
  },
  async mounted () {
    // 初始化完成品类树
    await this.initSelectTree()
  },
  methods: {
    async initSelectTree () {
      await mutations.fetchCategory()
      if (this.remote) {
        walk(store.category, (data, deep) => {
          return { ...data }
        })
        this.selectTreeOptions = walk(store.category, data => {
          const { level } = data
          const disabled = level != 3 // 不是分支节点的就禁用
          return { ...data, disabled }
        })
      } else {
        // 品类结构
        if (this.categoryData.length > 0) {
          walk(this.categoryData, (data, deep) => {
            return { ...data }
          })
          this.selectTreeOptions = walk(this.categoryData, data => {
            const { level } = data
            const disabled = level != 3 // 不是分支节点的就禁用
            return { ...data, disabled }
          })
        }
      }
    },
    treeselectChange (value) {
      if (this.multiple) {
        const nodes = []
        walk(this.selectTreeOptions, data => {
          if (value.includes(data.categoryId)) {
            nodes.push({ ...data })
          }
          return { ...data }
        })
        this.$emit('select', nodes, value, this.scope)
      } else {
        let node = {}
        walk(this.selectTreeOptions, data => {
          if (data.categoryId === value) {
            node = data
          }
          return { ...data }
        })
        this.$emit('select', node, value, this.scope)
      }
      this.$emit('change', value)
    }
  }
}
</script>
