<template>
  <el-aside
    width="155px"
    class="detail-menu-aside"
    style="padding: 0"
  >
    <el-container class="flex-container" direction="vertical">
      <el-main style="border-right: 1px solid #dfe4ed">
        <el-menu
          ref="menu"
          :default-openeds="defaultOpeneds"
          :default-active="defaultCheckedKeys"
          class="detail-menu"
        >
          <template v-for="item in treeData">
            <!--有子菜单 且要存在没禁用的子菜单-->
            <el-submenu
              v-if="item.children && item.children.find(itemNode => !itemNode.disabled)"
              :key="item.id"
              :index="item.id"
            >
              <template #title>
                {{ item.label }}
              </template>

              <el-menu-item
                v-for="node in item.children"
                :key="node.id"
                :index="node.id"
                :disabled="node.disabled"
                @click="menuClick(node)"
              >
                <em :class="node.iconClass" />
                <span slot="title">{{ node.label }}</span>
              </el-menu-item>
            </el-submenu>

            <!--没有子菜单，不显示submenu-->
            <el-menu-item
              v-else-if="!item.isSubmenu"
              :key="item.id"
              :index="item.id"
              :disabled="item.disabled"
              @click="menuClick(item)"
            >
              <em :class="item.iconClass" />
              <span slot="title">{{ item.label }}</span>
            </el-menu-item>
          </template>
        </el-menu>
      </el-main>
    </el-container>
  </el-aside>
</template>

<script>
/**
 * 左侧菜单栏
 */
export default {
  name: 'DetailMenu',

  props: {
    defaultCheckedKeys: {
      type: String,
      required: true
    },
    processList: {
      type: Array,
      required: true
    },
    pageFlag: {
      type: Object,
      required: true
    },
    menuData: {
      type: Array,
      required: true
    },
    defaultOpeneds: {
      type: Array,
      required: false
    },
    viewMenuList: {
      type: Array,
      default: () => ['projectInfo', 'requireInfo', 'inviteVendor', 'createApproval']
    }
  },

  data () {
    return {
      treeData: []
    }
  },

  mounted () {
    this.treeData = JSON.parse(JSON.stringify(this.menuData))

    if (this.pageFlag.isView || this.pageFlag.isApproval) {
      this.treeData = this.treeData.filter(item => item.id === 'projectInitiation').map(item => {
        return {
          ...item,
          children: item.children.filter(itemChildren => this.viewMenuList.includes(itemChildren.id))
        }
      })
    }
  },

  methods: {
    /* 菜单点击 */
    menuClick (val) {
      this.$emit('menu-item-click', val)
    },

    /* 设置菜单节点 根据节点启用信息编排得到启用节点列表 */
    setMenuNodeConfig (process, type) {
      if (!process) {
        return
      }

      // 启用列表
      const enabledList = process
        .filter(item => item.enabled === 'Y')
        .map(item => item.processNode)

      // 编排节点启用开关
      this.treeData = this.treeData.map(item => {
        let resultItem = {
          ...item
        }
        if (!item.children) {
          resultItem = {
            ...resultItem,
            disabled: !enabledList.includes(item.key),
            iconClass: 'el-icon-circle-check'
          }
        } else {
          resultItem = {
            ...resultItem,
            children: item.children.map(itemChild => {
              return {
                ...itemChild,
                disabled: !enabledList.includes(itemChild.key),
                iconClass: 'el-icon-circle-check'
              }
            })
          }
        }
        return resultItem
      })

      this.extractEnabledAndFlagList()
      this.$forceUpdate()
    },

    /* 更新左侧菜单完成情况 */
    updateTreeMenuData (data) {
      // 已完成的节点
      const keys = data.filter(v => v.nodeStatus === 'Y').map(k => k.processNode)

      if (keys.length === 0) {
        return
      }

      this.treeData = this.treeData.map(item => {
        let resultItem = {
          ...item
        }
        if (!item.children) {
          resultItem = {
            ...resultItem,
            iconClass: keys.includes(item.key) ? 'el-icon-success' : 'el-icon-circle-check'
          }
        } else {
          resultItem = {
            ...resultItem,
            children: item.children.map(itemChild => {
              return {
                ...itemChild,
                iconClass: keys.includes(itemChild.key) ? 'el-icon-success' : 'el-icon-circle-check'
              }
            })
          }
        }
        return resultItem
      })

      this.extractEnabledAndFlagList()
      this.$forceUpdate()
    },

    /* 提取并返回启用和已完成的节点列表 */
    extractEnabledAndFlagList () {
      // 启用的, 已完成的
      const [enabledList, flagList] = [[], []]
      console.log('treeData', this.treeData)
      this.treeData.forEach(item => {
        // if (!item.disabled) {
        //   enabledList.push(item.id)
        //   if (item.iconClass === 'el-icon-success') {
        //     flagList.push(item.id)
        //   }
        // }
        if (item.children && Array.isArray(item.children) && item.children.length) {
          // 只有两级，无需递归遍历了
          item.children.forEach(itemChildren => {
            if (!itemChildren.disabled) {
              enabledList.push(itemChildren.id)
              if (itemChildren.iconClass === 'el-icon-success') {
                flagList.push(itemChildren.id)
              }
            }
          })
        }
      })
      // 返回节点列表
      this.$emit('set-enabled-flag-list', {
        enabledList,
        flagList
      })
    }
  }
}
</script>

<style lang="scss">
.detail-menu-aside {
  padding-bottom: 10px !important;
  .detail-menu {
    border: 0 solid #e6e6e6;
    background: #fff;
    .el-menu-item:not(.is-disabled):hover {
      color: #393E45 !important;
    }

    .el-menu .el-menu-item:not(.is-disabled):focus,
    .el-menu .el-menu-item:not(.is-disabled):hover {
      color: #393E45 !important;
    }

    &>.el-menu-item {
      height: 30px;
      line-height: 30px;
      padding: 0 20px !important;
      color: #393E45;
    }

    .el-submenu {
      .el-menu {
        background: #fff;

        .el-menu-item {
          height: 26px;
          line-height: 25px;
          padding: 0 20px !important;
          min-width: 150px;
          font-size: 12px;
          color: #393E45;
        }
      }
    }

    .el-submenu__title {
      height: 30px;
      line-height: 30px;
      color: #393E45 !important;
    }

    .el-menu-item.is-active {
      background-color: #88c1f4;

      &::before {
        height: 100% !important;
        display: none !important;
      }
    }

    .el-menu-item.is-disabled {
      display: none;
    }

    .el-menu-item .el-icon-success {
      color: #13cc33;
    }
  }
}
</style>
