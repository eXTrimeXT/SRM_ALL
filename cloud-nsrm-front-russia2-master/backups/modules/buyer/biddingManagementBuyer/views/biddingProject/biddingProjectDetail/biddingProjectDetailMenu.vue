<template>
  <el-aside
    width="155px"
    class="detail-menu-aside"
    style="padding: 0"
  >
    <el-container
      class="flex-container"
      direction="vertical"
    >
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
              <template slot="title">
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
  name: 'BiddingProjectDetailMenu',

  props: {
    defaultCheckedKeys: {
      type: String,
      required: true
    },
    bidProcessConfigIdList: {
      type: Array,
      required: true
    },
    pageFlag: {
      type: Object,
      required: true
    }
  },

  data () {
    return {
      defaultOpeneds: ['t1', 't2', 't6', 't7'],
      treeData: [
        {
          id: 't1',
          label: this.$t('bidMod.addNewProj'), // 招标立项
          isSubmenu: true,
          children: [
            // 项目信息
            {
              id: 't11',
              label: this.$t('bidMod.projectInformation'),
              key: 'projectInformation'
            },
            // 项目需求
            {
              id: 't12',
              label: this.$t('bidMod.projectRequirement'),
              key: 'projectRequirement'
            },
            // 邀请供应商
            {
              id: 't13',
              label: this.$t('bidMod.inviteSupplier'),
              key: 'inviteSupplier'
            },
            // 评分规则
            {
              id: 't14',
              label: this.$t('bidMod.scoringRule'),
              key: 'scoringRule'
            },
            // 流程审批
            {
              id: 't15',
              label: this.$t('bidMod.processApproval'),
              key: 'processApproval'
            }
          ]
        },
        // 保证金管理
        {
          id: 't2',
          label: '保证金管理',
          key: 'bondManagement'
        },
        // 报名管理
        {
          id: 't4',
          label: this.$t('bidMod.entryManagement'),
          key: 'entryManagement'
        },
        // 投标控制
        {
          id: 't5',
          label: this.$t('bidMod.bidingControl'),
          key: 'bidingControl'
        },
        // 开/评标
        {
          id: 't6',
          label: this.$t('bidMod.openEvalateBid'),
          isSubmenu: true,
          children: [
            // 技术标管理
            {
              id: 't62',
              label: this.$t('bidMod.technicalManagement'),
              key: 'technicalManagement'
            },
            // 商务标管理
            {
              id: 't63',
              label: this.$t('bidMod.commercialManagement'),
              key: 'commercialManagement'
            },
            // 评选
            {
              id: 't64',
              label: this.$t('bidMod.bidEvaluation'),
              key: 'bidEvaluation'
            }
          ]
        }
      ],
      processTreeMenu: []
    }
  },

  watch: {
    pageFlag: {
      handler (newValue) {
        if (newValue.isView || newValue.isApproval) {
          // 设置只读查看页面的菜单
          const viewIdList = ['t11', 't12', 't13', 't14', 't15']
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
    /* 菜单点击 */
    menuClick (val) {
      this.$emit('menuClick', val)
    },

    /* 设置更新菜单节点 只在选择流程模板初始化时候用到 */
    setMenuNodeConfig (processId, type) {
      const process = this.bidProcessConfigIdList.filter(v => v.processConfigId === processId)
      let row = process ? process[0] : ''
      if (!row) return

      let keyList = []
      for (let i in row) {
        if (row[i] === 'Y') {
          keyList.push(i)
        }
      }
      for (let i of this.treeData) {
        if (!i.children) {
          i.disabled = !keyList.includes(i.key)
          if (type === 'add') {
            i.iconClass = 'el-icon-circle-check'
          }
        } else {
          for (let j of i.children) {
            j.disabled = !keyList.includes(j.key)
            if (type === 'add') {
              j.iconClass = 'el-icon-circle-check'
            }
          }
        }
      }
      this.extractEnabledAndFlagList()
      this.$forceUpdate()
    },

    /* 更新左侧菜单 */
    updateTreeMenuData (data) {
      // 启用的节点
      let nodeList = data.map(x => x.nodeCode)
      // 已完成的节点
      let keyList = data.filter(v => v.dataFlag === 'Y').map(k => k.nodeCode)

      for (let i of this.treeData) {
        if (!i.children) {
          i.disabled = !nodeList.includes(i.key)
          if (keyList.includes(i.key)) {
            i.iconClass = 'el-icon-success'
          } else {
            i.iconClass = 'el-icon-circle-check'
          }
        } else {
          for (let j of i.children) {
            j.disabled = !nodeList.includes(j.key)
            if (keyList.includes(j.key)) {
              j.iconClass = 'el-icon-success'
            } else {
              j.iconClass = 'el-icon-circle-check'
            }
          }
        }
      }

      this.extractEnabledAndFlagList()
      this.$forceUpdate()
    },

    /* 提取并返回启用和已完成的节点列表 */
    extractEnabledAndFlagList () {
      // 启用的, 已完成的
      const [enabledList, flagList] = [[], []]
      this.treeData.forEach(item => {
        if (item.disabled !== undefined && !item.disabled) {
          enabledList.push(item.id)
          if (item.iconClass === 'el-icon-success') {
            flagList.push(item.id)
          }
        }
        if (item.children && Array.isArray(item.children) && item.children.length) {
          // 只有两级，无需递归遍历了
          item.children.forEach(itemChildren => {
            if (itemChildren.disabled !== undefined && !itemChildren.disabled) {
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
