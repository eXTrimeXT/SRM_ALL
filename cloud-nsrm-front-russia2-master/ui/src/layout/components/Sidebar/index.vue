<template>
  <div :class="{ 'has-logo': isCollapse }">
    <Logo :collapse="isCollapse" />

    <div
      v-if="!isCollapse"
      class="menuSearch"
    >
      <el-input
        v-model="menuKeyWord"
        :class="['menu-search-input',{'hasVal': !!menuKeyWord}]"
        clearable
        :placeholder="$t('common.msgSearchKeywords')"
        suffix-icon="el-icon-search"
        :showWordLimit="false"
        @input="searchHandlerLoad"
      />
    </div>
    <el-scrollbar wrap-class="scrollbar-wrapper" :class="{ 'unZhLange': $store.getters.language != 'zh_CN' }">
      <!-- :background-color="variables.menuBg"
        :text-color="variables.menuText"
        :active-text-color="variables.menuActiveText" -->
      <el-menu
        :default-active="defaultActive"
        :collapse="isCollapse"
        :collapse-transition="false"
        :unique-opened="true"
        mode="vertical"
        :default-openeds="defaultOpeneds"
        @open="open"
      >
        <SidebarItemMidea
          v-for="route in menuData"
          :key="route.permissionCode"
          :item="route"
          :base-path="route.functionAddress"
        />
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script>
import { mapGetters, mapState } from 'vuex'
import Logo from './Logo'
import SidebarItemMidea from './SidebarItemMidea'
import variables from '@/styles/variables.scss?inline'
import routesMock from '@/router/routesMock'
import { ConvertPinyin } from '@/utils/pinyin.js'

const sortMethod = (a, b) => {
  const { sort: c = 0 } = a
  const { sort: d = 0 } = b
  return c - d
}
function loadCurryFn (cb) {
  let flag = false
  return function () {
    if (flag) {
      return
    }
    flag = true
    setTimeout(() => {
      cb()
      flag = false
    }, 500)
  }
}

const sortMenuTree = menus => {
  const sortList = menus.sort(sortMethod)
  sortList.forEach((item, index) => {
    const childPermissions = item.childPermissions
    if (childPermissions.length) {
      sortList[index].childPermissions = sortMenuTree(childPermissions)
    }
  })
  return menus
}

// 菜单过滤 (通过关键字搜索)
const filterMenu = (nodes, value) => {
  let newarr = []
  nodes.forEach(element => {
    const resourceNamePinyin = ConvertPinyin(element.permissionName)
    // if (element.permissionName.indexOf(value) > -1) { // 原来不支持拼音写法
    if ([element.permissionName, resourceNamePinyin].toString().toLowerCase().includes(value.toLowerCase())) {
      newarr.push(element)
    } else {
      if (element.childPermissions && element.childPermissions.length > 0) {
        const childArr = filterMenu(element.childPermissions, value)
        const obj = {
          ...element,
          childPermissions: childArr
        }
        if (childArr && childArr.length > 0) {
          newarr.push(obj)
        }
      }
    }
  })
  return newarr
}
export default {
  components: { SidebarItemMidea, Logo },
  data () {
    return {
      navData: routesMock, // mock 数据
      menuData: [], // 菜单数据
      menuKeyWord: '',
      defaultOpeneds: []
    }
  },
  computed: {
    ...mapGetters(['slideMenu', 'sidebar', 'userTime']),
    ...mapState({
      userMenu: state => {
        const userInfo = state.user.userInfo
        const list = userInfo.menus || []
        const menus = sortMenuTree(list)
        return menus
      }
    }),
    defaultActive () {
      const route = this.$route
      const { name, path, meta, params } = route
      console.log('!!!!!!!!!  defaultActive route  :', route)
      // 如果菜单编码跟路由名称不一致，在路由配置元数据添加defaultActive对应菜单编码
      if (name == 'dynamicReportPage') {
        let sqlCode = params.sqlCode
        let defaultActive = this.getReportDefaultActive(sqlCode)
        return defaultActive
      }
      if (meta.defaultActive) {
        return meta.defaultActive
      }
      return name
    },
    showLogo () {
      return this.$store.state.settings.sidebarLogo
    },
    variables () {
      return variables
    },
    isCollapse () {
      return !this.sidebar.opened
    }
  },
  watch: {
    userTime: {
      // 监听是否有切换语言的
      deep: true,
      handler (userTime) {
        if (userTime) {
          this.menuData = this.userMenu
        }
      }
    }
  },
  created () {
    this.menuData = this.userMenu
    this.searchHandlerLoad = loadCurryFn(this.searchHandler)
  },
  methods: {
    getReportDefaultActive (sqlCode) {
      // 新增报表需要维护sqlCode和对应的菜单编码，两者一致则不需要配
      let map = {
        'reduceVendorReport': 'reduceReportvendor',
        'reduceCarReport': 'reduceReportCar',
        'reduceMaterialReport': 'reduceReportPart',
        'scc_base_email': 'Scc_Base_Email'
      }
      return map[sqlCode] || sqlCode || ''
    },
    searchHandler () {
      let menuKey = this.menuKeyWord
      let myMenu = []
      if (menuKey) {
        myMenu = filterMenu(this.userMenu, menuKey)
      } else {
        myMenu = this.userMenu
      }
      this.menuData = myMenu
      if (menuKey === '') {
        this.defaultOpeneds = []
      } else {
        this.getOpeneds()
      }
    },
    open (index) {},
    treeToArr (arr) {
      return arr.map(item => {
        let arr1 = []
        if (item.childPermissions.length > 0) {
          arr1 = this.treeToArr(item.childPermissions)
        }
        return [ item.permissionCode, ...arr1 ]
      })
    },
    getOpeneds () {
      this.defaultOpeneds = this.treeToArr(this.menuData).flat(Infinity)
    },
    searchHandlerLoad () { }
  }
}
</script>
<style lang="scss" scope>
.menu-search-input {
  &.hasVal{
    .el-input__suffix{
      .el-input__suffix-inner{
        .el-icon-search{display: none;}
      }
    }
  }
}
.menuSearch {
  padding: 16px;
}
.scrollbar-wrapper {
  .el-submenu {
    .el-submenu__title {
      .iconfont {
        // color: #aab0b7;
      }
    }
  }
}
</style>
<style>
.menuSearch .el-input__inner {
  background: #2A2E40;
  border-radius: 4px;
  border: none;
  font-size: 12px;
  color: #BFBFBF;
  height: 28px !important;
  line-height: 28px !important;
  font-weight: 400;
}
.menuSearch .el-input__inner:focus{
  box-shadow: none;
}
.sidebar-container .unZhLange .menu-wrapper .menuName {
  display: -webkit-box !important;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  white-space: normal;
  word-break: break-all;
  line-height: 18px;
}
</style>
