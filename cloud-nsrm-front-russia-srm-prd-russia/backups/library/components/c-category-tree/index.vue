<template>
  <div class="catWap">
    <el-popover
      v-model="popVisible"
      width="220"
      trigger="manual"
    >
      <el-input
        slot="reference"
        v-model="inputVal"
        :disabled="disabled"
        class="category-search"
        clearable
        @clear="clearOptions"
      >
        <el-button
          slot="append"
          icon="el-icon-search"
          @click="focusHandle"
        />
      </el-input>
      <div class="cat-tree-select">
        <div class="cat-wrap">
          <el-tree
            ref="catTree"
            v-loading="orgLoading"
            lazy
            node-key="id"
            :highlight-current="true"
            :expand-on-click-node="true"
            :data="catTreeData"
            :props="defaultProps"
            :load="loadNode"
          />
        </div>
        <div class="comfirm-dev">
          <el-button @click="cancelHandle">
            {{ $t("common.cancel") }}
          </el-button>
          <el-button
            type="primary"
            @click="comfirmSelect"
          >
            {{
              $t("common.confirm")
            }}
          </el-button>
        </div>
      </div>
    </el-popover>
  </div>
</template>

<script>
import { getCatChildrenData } from 'mod@/basicSetting/api/baseSetting'

export default {
  name: 'CCategoryTree',
  props: {
    // 禁用
    disabled: {
      type: Boolean,
      default: false
    },
    keyName: {
      type: String,
      default () {
        return ''
      }
    },
    keyRule: {
      type: Object,
      default () {
        return {}
      }
    },
    // tree数据
    treeData: {
      type: Array,
      default () {
        return []
      }
    },
    // 配置tree数据节点属性值、子节点属性值
    defaultProps: {
      type: Object,
      default () {
        return {
          children: 'childrens',
          label: 'categoryName',
          isLeaf: data => {
            return data.isLeaf
          }
        }
      }
    },
    inputName: {
      // 父页面传值input
      type: String,
      default: function () {
        return ''
      }
    },
    // 当前行上绑定的 data 对象
    scopeData: {
      type: Object,
      default: function () {
        return null
      }
    }
  },
  data () {
    return {
      orgLoading: false,
      popVisible: false,
      inputVal: '',
      catTreeData: [
        // 品类树数据
        {
          childrens: [],
          categoryName: ''
        }
      ]
    }
  },
  computed: {
    data () {
      return this.treeData
    }
  },
  watch: {
    inputName: {
      immediate: true,
      handler: function (val) {
        this.inputVal = val
      }
    }
  },
  methods: {
    // 异步树叶子节点懒加载逻辑
    loadNode (node, resolve) {
      // 一级节点处理
      if (node.level === 0) {
        let queryParma = { categoryId: -1 }
        this.getDepartmentTree(queryParma, resolve) // 查询一级节点
      } else if (node.level >= 1) {
        // 注意！把resolve传到你自己的异步中去
        let nodeParme = {}
        nodeParme.categoryId = node.data.categoryId
        this.getDepartmentTree(nodeParme, resolve)
      }
    },
    // 加载子节点
    getDepartmentTree (parmes, resolve) {
      getCatChildrenData(parmes)
        .then(response => {
          if (response && response.data) {
            resolve(response.data)
          } else {
            // 数据获取失败：
            this.$message({
              message: this.$t('components.dataAcquireFail') + response.msg,
              type: 'error'
            })
          }
        })
        .finally(() => {
          this.firstLoad = false
        })
    },
    // 加载一级节点
    loasFirstNode () {
      this.catTreeData = []
      let queryParma = { categoryId: -1 }
      getCatChildrenData(queryParma)
        .then(response => {
          if (response && response.data) {
            let resData = response.data
            resData.forEach((item, index) => {
              this.catTreeData.push(item)
              this.catTreeData[index].childrens = []
            })
          } else {
            // 数据获取失败：
            this.$message({
              message: this.$t('components.dataAcquireFail') + response.msg,
              type: 'error'
            })
          }
        })
        .finally(() => {
          this.firstLoad = false
        })
    },
    // input框获取焦点
    focusHandle () {
      this.popVisible = true
      this.$emit('input-focus')
    },
    inputHandle () {
      // let curNode = this.$refs.catTree.getCurrentNode()
      // // this.inputVal = curNode.categoryName
      // this.$emit('input', curNode)
    },
    clearOptions () {
      this.$emit('comfirm', {}, this.scopeData)
      this.inputVal = ''
    },
    // 确认选择
    comfirmSelect () {
      let curNode = this.$refs.catTree.getCurrentNode()
      this.popVisible = false
      this.inputVal = curNode.categoryName
      this.$emit('comfirm', curNode, this.scopeData)
    },
    cancelHandle () {
      this.popVisible = false
    }
  }
}
</script>

<style lang="scss" scoped>
.cat-tree-select {
  height: 200px;
  padding-bottom: 35px;
  position: relative;
  .cat-wrap {
    height: 100%;
    overflow: hidden;
    overflow-y: auto;
  }
}
.catWap /deep/ {
  .comfirm-dev {
    padding-top: 10px;
    text-align: right;
    position: absolute;
    width: 100%;
    bottom: 0;
    left: 0;
  }
  .category-search .el-input-group__append {
    padding: 0;
    width: 26px !important;
  }
  .category-search .el-input-group__append .el-button {
    position: relative;
    margin: 0 !important;
    border: 0;
    height: 30px;
  }
}
.el-table .cell .category-search .el-input-group__append .el-button {
  height: 24px;
}
</style>
