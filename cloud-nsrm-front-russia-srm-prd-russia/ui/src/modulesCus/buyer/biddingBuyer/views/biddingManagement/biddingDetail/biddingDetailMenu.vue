<template>
  <el-aside
    width="155px"
    class="detail-menu-aside"
    style="padding: 0"
  >
    <el-container
      class="flex-container"
      direction="vertical"
      style="padding-bottom: 50px"
    >
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
  name: 'BiddingDetailMenu',

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
    }
  },

  data () {
    return {
      defaultOpeneds: ['projectInitiation', 'bidManagement', 'openingEvaluation', 'calibration'],
      treeData: [
        // 招标立项
        {
          id: 'projectInitiation',
          label: '标前准备',
          isSubmenu: true,
          children: [
            // 项目信息
            {
              id: 'projectInfo',
              label: this.$t('bidMod.projectInformation'),
              key: 'projectInfo'
            },
            // 项目需求--报价信息
            {
              id: 'requireInfo',
              label: this.$t('cusEntry.bidMod.biddingInfo'),
              key: 'requireInfo'
            },
            // 评分规则
            {
              id: 'scoreRule',
              label: this.$t('bidMod.scoringRule'),
              key: 'scoreRule'
            },
            // 邀请供应商--供应商清单
            {
              id: 'inviteVendor',
              label: this.$t('cusEntry.bidMod.vendorList'),
              key: 'inviteVendor'
            },
            // 流程审批
            {
              id: 'createApproval',
              label: this.$t('bidMod.processApproval'),
              key: 'createApproval'
            }
          ]
        },
        // 投标管理
        {
          id: 'bidManagement',
          label: this.$t('cusEntry.bidMod.bidManagement'),
          isSubmenu: true,
          children: [
            // 保证金管理
            {
              id: 'bondManagement',
              label: this.$t('other.key29'),
              key: 'bondManagement'
            },
            // 投标控制
            {
              id: 'bidingControl',
              label: this.$t('bidMod.bidingControl'),
              key: 'bidingControl'
            }
          ]
        },
        // 开/评标
        {
          id: 'openingEvaluation',
          label: this.$t('bidMod.openEvalateBid'),
          isSubmenu: true,
          children: [
            // 技术标管理
            {
              id: 'techManagement',
              label: this.$t('bidMod.technicalManagement'),
              key: 'techManagement'
            },
            // 商务标管理
            {
              id: 'businessManagement',
              label: this.$t('bidMod.commercialManagement'),
              key: 'businessManagement'
            }
          ]
        },
        // 定标/归档
        {
          id: 'calibration',
          label: this.$t('cusEntry.bidMod.calibration'),
          isSubmenu: true,
          children: [
            // 编制定标结果
            {
              id: 'bidReuslt',
              label: this.$t('cusEntry.bidMod.bidReuslt'),
              key: 'bidReuslt'
            },
            // 中/落标通知
            {
              id: 'bidWinOrLoss',
              label: this.$t('cusEntry.bidMod.bidWinOrLoss'),
              key: 'bidWinOrLoss'
            },
            // 归档
            {
              id: 'bidArchive',
              label: this.$t('cusEntry.bidMod.bidArchive'),
              key: 'bidArchive'
            }
          ]
        }
      ]
    }
  },

  watch: {
    pageFlag: {
      handler (newValue) {
        if (newValue.isApproval) {
          // 查看 || 审批 设置只读查看页面的菜单
          const viewIdList = ['projectInfo', 'requireInfo', 'inviteVendor', 'scoreRule', 'createApproval']
          this.treeData = this.treeData.filter(item => item.id === 'projectInitiation').map(item => {
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
      this.$emit('menu-item-click', val)
    },

    /* 设置更新菜单节点 只在选择流程模板初始化时候用到 */
    setMenuNodeConfig (processId, type) {
      const process = this.processList.filter(v => v.processConfigId === processId)
      let row = process ? process[0] : ''
      if (!row) {
        return
      }

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
      const enabledList = data.filter(item => item.enabled === 'Y')
      // 启用的节点
      let nodeList = enabledList.map(x => x.processNode)
      // 已完成的节点
      let keyList = enabledList.filter(v => v.nodeStatus === 'Y').map(k => k.processNode)

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
      // 编排启用的
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
      // 返回节点列表 set-enabled-flag-list
      this.$emit('set-enabled-flag-list', {
        enabledList,
        flagList
      })
    }
  }
}
</script>
