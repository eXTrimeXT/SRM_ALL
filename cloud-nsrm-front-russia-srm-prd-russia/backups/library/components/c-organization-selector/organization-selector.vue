<template>
  <div class="organization-selector">
    <!-- 部门选择器 -->
    <section
      :key="`department_${domKey}`"
      class="department-selector selector-section"
    >
      <div class="section-content">
        <!-- 输入关键字进行过滤 -->
        <el-input
          v-model.trim="searchFilter"
          :placeholder="$t('common.placeholder')"
          class="section-search"
          clearable
          @keyup.enter.native="searchFilterEnter"
        >
          <el-button
            slot="append"
            icon="el-icon-search"
            @click="searchFilterEnter"
          />
        </el-input>
        <div class="overflow-box">
          <el-tree
            ref="orgTree"
            v-loading="departmentLoading"
            lazy
            node-key="id"
            :highlight-current="true"
            :data="departmentTreeData"
            :props="departmentTreeProps"
            :load="loadNode"
            :filter-node-method="filterNode"
            @current-change="handleNodeClick"
          />
        </div>
      </div>
    </section>
    <!-- END -->
  </div>
</template>

<script>
import { getListChildrenData } from 'mod@/basicSetting/api/baseSetting'

import { generateUid } from 'lib@/utils/generator'

export default {
  name: 'OrganizationSelectorCore',
  props: {
    multiSelect: {
      type: Boolean,
      default: true
    },
    resetSelect: {
      type: Boolean,
      default: true
    },
    dialogVisible: {
      type: Boolean,
      default: false
    },
    defaultValue: {
      type: Array,
      default: () => {
        return []
      }
    },
    filterInput: {
      // 父页面传值input
      type: String,
      default: ''
    }
  },
  data () {
    return {
      firstLoad: true,
      domKey: generateUid(),
      departmentLoading: false,
      employeeLoading: false,
      // 搜索数据框
      searchFilter: '',
      // 部门树数据
      selectedOrg: [],
      // 部门树数据
      departmentTreeData: [
        {
          childrens: [],
          organizationName: ''
        }
      ],
      // 部门树配置选项
      departmentTreeProps: {
        children: 'childrens',
        label: 'organizationName',
        isLeaf: data => {
          return data.isLeaf
        }
      }
    }
  },
  watch: {
    dialogVisible: {
      immediate: true,
      handler (visible) {
        if (visible) {
          this.employeeChosenData = [].concat(this.defaultValue)
          if (this.resetSelect) {
            this.resetData()
            this.domKey = generateUid()
            this.firstLoad = true
          } else {
            this.syncEmployeeTableSelection()
          }
        }
      }
    },
    searchFilter (val) {
      this.$refs.orgTree.filter(val)
    }
  },
  created () {
    this.resetData()
  },
  methods: {
    // 模糊查询
    searchFilterEnter () {},
    filterNode (value, data) {
      if (!value) return true
      return data.organizationName.indexOf(value) !== -1
    },
    // 异步树叶子节点懒加载逻辑
    loadNode (node, resolve) {
      // 一级节点处理
      if (node.level === 0) {
        let queryParma = { organizationId: -1 } // relId: -1,
        this.getDepartmentTree(queryParma, resolve) // 查询一级节点
      } else if (node.level >= 1) {
        // 注意！把resolve传到你自己的异步中去
        let nodeParme = {}
        nodeParme.organizationId = node.data.organizationId
        // nodeParme.relId = node.data.relId
        this.getDepartmentTree(nodeParme, resolve)
      }
    },
    // 加载子节点
    getDepartmentTree (parmes, resolve) {
      if (this.firstLoad) {
        this.departmentLoading = true
      }
      getListChildrenData(parmes)
        .then(response => {
          if (response && response.data) {
            resolve(response.data)
          } else {
            this.$message({
              message: this.$t('dataConfMod.loadDataFail') + response.msg, // 数据获取失败：
              type: 'error'
            })
          }
        })
        .finally(() => {
          this.firstLoad = false
          this.departmentLoading = false
        })
    },
    // 加载一级节点
    loasFirstNode () {
      this.orgTreeData = []
      let queryParma = { relId: -1, organizationId: -1 }
      getListChildrenData(queryParma)
        .then(response => {
          if (response && response.data) {
            let resData = response.data
            resData.forEach((item, index) => {
              this.orgTreeData.push(item)
              this.orgTreeData[index].childrens = []
            })
          } else {
            this.$message({
              message: this.$t('dataConfMod.loadDataFail') + response.msg, // 数据获取失败：
              type: 'error'
            })
          }
        })
        .finally(() => {
          this.firstLoad = false
          this.departmentLoading = false
        })
    },
    handleNodeClick () {
      let curNode = this.$refs.orgTree.getCurrentNode()
      this.selectedOrg = curNode
    },
    // 重置数据
    resetData () {
      this.searchFilter = ''
      this.departmentTreeData = []
    }
  }
}
</script>

<style lang="scss" scoped>
/* 清除浮动 */
.clear() {
  &:after {
    content: "\20";
    display: block;
    height: 0;
    clear: both;
  }
}

/* 单行文字溢出虚点显示 */
.ell() {
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
}

.organization-selector {
  height: 330px;
  .selector-section {
    top: 1px;
    position: relative;
    height: 100%;
    box-sizing: border-box;
  }

  .section-header {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 30px;
    padding-top: 2px;
    padding-bottom: 2px;
    font-size: 12px;
    font-weight: 600;
    line-height: 26px;
    box-sizing: border-box;
    overflow: hidden;
    .clear;
  }

  .section-content {
    position: relative;
    height: 100%;
    border: 1px solid #eaeaea;
    box-sizing: border-box;
    overflow: hidden;
  }

  .department-selector {
    .section-search {
      margin-bottom: 10px;
      .el-input__inner {
        border-top: none;
        border-left: none;
        border-right: none;
        border-bottom-color: #eaeaea;
        border-radius: 0;
      }
      .el-input-group__append {
        position: relative;
        border-top: none;
        border-right: none;
        border-left: 1px solid #eaeaea;
        border-bottom-color: #eaeaea;
        border-radius: 0;
      }
    }
  }

  .overflow-box {
    width: 100%;
    height: 100%;
    max-width: 100%;
    max-height: 100%;
    overflow: auto;
  }

  .fl {
    float: left;
  }

  .fr {
    float: right;
  }

  .el-tree {
    left: 1px;
  }

  .el-table-column--selection .cell {
    padding: 0;
  }

  .el-tree-node__label {
    font-size: 12px;
  }

  .el-dialog__body {
    padding-top: 10px;
    padding-bottom: 10px;
  }
}
</style>
