<template>
  <!-- 基于美的平台返回数据 -->
  <div
    v-if="!item.hidden"
    class="menu-wrapper"
  >
    <!-- 只有单个 -->
    <template v-if="hasOneShowingChild(item.childPermissions,item) && (!onlyOneChild.childPermissions||onlyOneChild.noShowingChildren)">
      <AppLink
        v-if="onlyOneChild"
        :to="resolvePath(onlyOneChild.functionAddress)"
      >
        <el-menu-item
          :index="onlyOneChild.permissionCode"
          :class="{'submenu-title-noDropdown':!isNest}"
          @click="routeClick(item)"
        >
          <i
            v-if="!!item.iconPath"
            :class="[iconfamily, item.iconPath]"
          />
          <span
            v-if="item.permissionName"
            slot="title"
            class="menuName"
            :title="item.permissionName"
          >{{ item.permissionName }}</span>
        </el-menu-item>
      </AppLink>
    </template>
    <!-- 多个 -->
    <el-submenu
      v-else
      ref="subMenu"
      :index="item.permissionCode"
      popper-append-to-body
    >
      <template slot="title">
        <i
          v-if="!!item.iconPath"
          :class="[iconfamily, item.iconPath]"
        />
        <span
          v-if="item.permissionName"
          slot="title"
          class="menuName"
          :title="item.permissionName"
        >{{ item.permissionName }}</span>
      </template>
      <sidebar-item-midea
        v-for="child in item.childPermissions"
        :key="child.permissionCode"
        :is-nest="true"
        :item="child"
        :base-path="resolvePath(child.functionAddress)"
        class="nest-menu"
      />
    </el-submenu>
  </div>
</template>

<script>
import * as path from '@/utils/path'
import { isExternal } from '@/utils/validate'
import AppLink from './Link'
import FixiOSBug from './FixiOSBug'

export default {
  name: 'SidebarItemMidea',
  components: { AppLink },
  mixins: [FixiOSBug],
  props: {
    // route object
    item: {
      type: Object,
      required: true
    },
    isNest: {
      type: Boolean,
      default: false
    },
    basePath: {
      type: String,
      default: ''
    }
  },
  data () {
    this.onlyOneChild = null
    return {
      iconfamily: 'iconfont'
    }
  },
  created () {
    // console.log("[item]",this.item)
  },
  methods: {
    hasOneShowingChild (children = [], parent) {
      // 显示的子节点
      const showingChildren = children.filter(item => {
        if (item.hidden) { // 节点隐藏
          return false
        } else {
          // Temp set(will be used if only has one showing child)
          this.onlyOneChild = item
          return true
        }
      })

      // When there is only one child router, the child router is displayed by default
      if (showingChildren.length === 1) {
        return true
      }

      // Show parent if there are no child router to display
      if (showingChildren.length === 0) {
        this.onlyOneChild = { ...parent, path: '', noShowingChildren: true }
        return true
      }
      return false
    },
    resolvePath (resRoutePath = '') {
      let routePath = !resRoutePath ? '' : resRoutePath
      let basePath = !this.basePath ? '' : this.basePath
      // 外链、邮箱、电话
      if (isExternal(routePath)) {
        return routePath
      }
      if (isExternal(basePath)) {
        return basePath
      }
      return path.resolve(basePath, routePath)
    },
    routeClick (route) {
      // 后续做读取菜单Id的功能

    }
  }
}
</script>
