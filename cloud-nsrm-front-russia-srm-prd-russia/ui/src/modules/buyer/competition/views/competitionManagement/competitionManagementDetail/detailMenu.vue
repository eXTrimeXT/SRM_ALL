<template>
  <el-aside
    width="155px"
    class="detail-menu-aside competitionDetail"
    style="padding: 0"
  >
    <el-container class="flex-container" direction="vertical">
      <el-main style="border-right: 1px solid #dfe4ed">
        <el-menu
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
                @click="$emit('menuClick', node)"
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
              @click="$emit('menuClick', item)"
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
    pageFlag: {
      type: Object,
      required: true
    }
  },

  data () {
    return {
      defaultOpeneds: ['t1', 't4'],
      treeData: [
        {
          id: 't1',
          label: '竞价项目管理',
          disabled: false,
          children: [
            // 项目信息
            {
              id: 't1-1',
              key: 'projectInfo',
              label: this.$t('bidMod.projectInformation'),
              iconClass: 'el-icon-circle-check',
              disabled: false
            },
            // 项目需求
            {
              id: 't1-2',
              key: 'requireInfo',
              label: this.$t('bidMod.projectRequire'),
              iconClass: 'el-icon-circle-check',
              disabled: false
            },
            // 邀请供应商
            {
              id: 't1-3',
              key: 'inviteVendor',
              label: this.$t('bidMod.bidVendorCount'),
              iconClass: 'el-icon-circle-check',
              disabled: false
            }
          ]
        },
        // 报名管理 t2
        {
          id: 't2',
          key: 'signUpManagement',
          label: this.$t('bidMod.registManagement'),
          iconClass: 'el-icon-circle-check',
          disabled: false
        },
        // 报价控制 t5
        {
          id: 't3',
          key: 'bidingControl',
          label: this.$t('bidMod.priceControl'),
          iconClass: 'el-icon-circle-check',
          disabled: false
        },
        // 开/评标
        {
          id: 't4',
          label: this.$t('bidMod.evaluationBid'),
          disabled: false,
          children: [
            // 商务标管理
            {
              id: 't4-1',
              key: 'businessManagement',
              label: this.$t('bidMod.businessManagement'),
              iconClass: 'el-icon-circle-check',
              disabled: false
            },
            // 竞价大厅
            {
              id: 't4-2',
              key: 'compHall',
              label: this.$t('bidMod.hall'),
              iconClass: 'el-icon-circle-check',
              disabled: false
            },
            // 评选
            {
              id: 't4-3',
              key: 'evaluation',
              label: this.$t('bidMod.appraise'),
              iconClass: 'el-icon-circle-check',
              disabled: false
            }
          ]
        }
      ]
    }
  },

  watch: {
    pageFlag: {
      handler (newValue) {
        if (newValue.isView || newValue.isApproval) {
          // 设置只读查看页面的菜单
          const viewIdList = ['t1-1', 't1-2', 't1-3']
          this.treeData = this.treeData.filter(item => item.id === 't1').map(item => {
            return {
              ...item,
              children: item.children.filter(itemChildren => viewIdList.includes(itemChildren.id))
            }
          })
        }
      },
      immediate: true
    }
  },

  methods: {
    /* 设置菜单节点 根据节点启用信息编排得到启用节点列表 */
    setMenuNodeConfig (process) {
      console.log('process:::', process)
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
            disabled: !enabledList.includes(item.key)
          }
        } else {
          resultItem = {
            ...resultItem,
            children: item.children.map(itemChild => {
              return {
                ...itemChild,
                disabled: !enabledList.includes(itemChild.key)
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
      this.treeData.forEach(item => {
        if (!item.disabled) {
          enabledList.push(item.id)
          if (item.iconClass === 'el-icon-success') {
            flagList.push(item.id)
          }
        }
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
      this.$emit('setEnabledNodeAndFlagList', {
        enabledList,
        flagList
      })
    }
  }
}
</script>

<style lang="scss">
.competitionDetail.detail-menu-aside {
    .detail-menu {
      border: 0 solid #e6e6e6;
      background: #fff;

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
        color: #393E45;
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
