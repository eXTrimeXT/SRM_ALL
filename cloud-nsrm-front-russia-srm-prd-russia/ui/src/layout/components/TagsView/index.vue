<template>
  <div
    id="tags-view-container"
    class="tags-view-container"
  >
    <ScrollPane
      ref="scrollPane"
      class="tags-view-wrapper"
    >
      <div
        ref="tagViews"
        v-resize="resizeHandle"
        class="tags-view__items"
      >
        <router-link
          v-for="tag in showVisitedViews"
          ref="tag"
          :key="tag.path"
          :class="isActive(tag) ? 'active' : ''"
          :to="{ path: tag.path, query: tag.query, fullPath: tag.fullPath }"
          tag="span"
          class="tags-view-item"
          @click.middle.native="!isAffix(tag) ? closeSelectedTag(tag) : ''"
          @contextmenu.prevent.native="openMenu(tag, $event)"
        >
          <span class="tags-view__point" />
          {{ $t(tag.title) }}
          <i
            v-if="!isAffix(tag)"
            class="el-icon-close icon-close"
            @click.prevent.stop="closeSelectedTag(tag)"
          />
        </router-link>
      </div>
    </ScrollPane>
    <div class="tag-view__tools">
      <!-- <el-popover placement="top-start" trigger="click">
        <div class="tags-view__items hidden-tags">
          <router-link
            v-for="tag in hiddenVisitedViews"
            ref="tag"
            :key="tag.path"
            :class="isActive(tag) ? 'active' : ''"
            :to="{ path: tag.path, query: tag.query, fullPath: tag.fullPath }"
            tag="span"
            class="tags-view-item"
            @click.middle.native="!isAffix(tag) ? closeSelectedTag(tag) : ''"
            @contextmenu.prevent.native="openMenu(tag, $event)"
          >
            {{ $t(tag.title) }}
            <i
              v-if="!isAffix(tag)"
              class="el-icon-close icon-close"
              @click.prevent.stop="closeSelectedTag(tag)"
            />
          </router-link>
        </div>
        <div class="tool-item show-tag" slot="reference"></div>
      </el-popover> -->
      <el-popover
        placement="top-start"
        trigger="click"
      >
        <div class="bacth-handle">
          <div
            class="batch-handle__item"
            @click="refreshSelectedTag(activeTag)"
          >
            <!-- 刷新 -->
            {{ $t("common.refresh") }}
          </div>
          <div
            v-if="!isAffix(activeTag)"
            class="batch-handle__item"
            @click="closeSelectedTag(activeTag)"
          >
            <!-- 关闭 -->
            {{ $t("common.close") }}
          </div>
          <div
            class="batch-handle__item"
            @click="closeOthersTags(activeTag)"
          >
            <!-- 关闭其他 -->
            {{ $t("common.closeOther") }}
          </div>
          <div
            class="batch-handle__item"
            @click="closeAllTags(activeTag)"
          >
            <!-- 关闭全部 -->
            {{ $t("common.closeAll") }}
          </div>
        </div>
        <div
          slot="reference"
          class="tool-item batch"
          @click.prevent.stop=""
        />
      </el-popover>
      <div
        v-if="!navCollapse.opened"
        class="tool-item nav-close"
        @click="navClose"
      />
    </div>
    <ul
      v-show="visible"
      :style="{ left: left + 'px', top: top + 'px' }"
      class="contextmenu"
    >
      <li @click="refreshSelectedTag(selectedTag)">
        {{ $t("common.refresh") }}
      </li>
      <li
        v-if="!isAffix(selectedTag)"
        @click="closeSelectedTag(selectedTag)"
      >
        {{ $t("common.close") }}
      </li>
      <li @click="closeOthersTags(selectedTag)">
        {{ $t("common.closeOther") }}
      </li>
      <li @click="closeAllTags(selectedTag)">
        {{ $t("common.closeAll") }}
      </li>
    </ul>
  </div>
</template>

<script>
import ScrollPane from './ScrollPane'
import * as path from '@/utils/path'
import { mapGetters } from 'vuex'
import { isIE, isIE11 } from 'lib@/utils/validate'
import { findMenuInfoByPath } from '@/utils'

export default {
  components: { ScrollPane },
  directives: {
    // 使用局部注册指令的方式
    resize: {
      // 指令的名称
      bind (el, binding) {
        // el为绑定的元素，binding为绑定给指令的对象
        let width = ''
        let height = ''
        function isReize () {
          const style = document.defaultView.getComputedStyle(el)
          if (width !== style.width || height !== style.height) {
            binding.value() // 关键
          }
          width = style.width
          height = style.height
        }
        el.__vueSetInterval__ = setInterval(isReize, 300)
      },
      unbind (el) {
        clearInterval(el.__vueSetInterval__)
      }
    }
  },
  data () {
    return {
      visible: false,
      top: 0,
      left: 0,
      selectedTag: {},
      affixTags: [],
      showVisitedViews: [],
      hiddenVisitedViews: []
    }
  },
  computed: {
    ...mapGetters(['navCollapse']),
    visitedViews () {
      console.log(this.$store.state.tagsView.visitedViews)
      return this.$store.state.tagsView.visitedViews
    },
    activeTag () {
      return this.visitedViews.find(tag => tag.fullPath === this.$route.fullPath)
    },
    routes () {
      return this.$store.state.permission.routes
    },
    menus () {
      return this.$store.state.user.userInfo.menus
    }
  },
  watch: {
    visitedViews () {
      this.resizeHandle()
    },
    $route () {
      this.addTags()
      this.moveToCurrentTag()
    },
    visible (value) {
      if (value) {
        document.body.addEventListener('click', this.closeMenu)
      } else {
        document.body.removeEventListener('click', this.closeMenu)
      }
    }
  },
  mounted () {
    this.initTags()
    this.addTags()
  },
  methods: {
    resizeHandle () {
      // const tagViews = this.$refs.tagViews;
      // const { width } = tagViews.getBoundingClientRect();
      // // console.log("[tag容器宽度]", width);
      // let sum = 0;
      // let count = 0;
      // const show = [];
      // for (let i = 0; i < this.visitedViews.length; i++) {
      //   const tag = this.visitedViews[i];
      //   sum += this.getTextWidth(this.$t(tag.title));
      //   // console.log("[迭代器]", i, this.$t(tag.title), sum);
      //   if (sum < width) {
      //     show.push(tag);
      //     count++;
      //   } else {
      //     break;
      //   }
      // }
      // // console.log("[显示tag数量]", count);
      // this.showVisitedViews = show;
      // this.hiddenVisitedViews = this.visitedViews.slice(count);
      this.showVisitedViews = this.visitedViews
    },
    removeElement (_element) {
      if (isIE11() || isIE()) {
        _element.removeNode(true)
      } else {
        const _parentElement = _element.parentNode
        if (_parentElement) {
          // console.log('[有父节点]', _element)
          _parentElement.removeChild(_element)
        }
        // console.log('[无父节点]', _element)
      }
    },
    getTextWidth (text) {
      let width = 0
      let html = document.createElement('span')
      html.innerText = text
      html.className = 'getTextWidth'
      document.querySelector('body').appendChild(html)
      width = document.querySelector('.getTextWidth').offsetWidth
      // document.querySelector(".getTextWidth").remove();
      const geTextWidths = document.querySelector('.getTextWidth')
      if (geTextWidths.length) {
        Array.from(geTextWidths).forEach(i => this.removeElement(i))
      } else {
        console.log('[geTextWidths]', geTextWidths)
        this.removeElement(geTextWidths)
      }
      // this.removeElement(html);
      console.log(`[${text}]: ${width}`)
      return +width + 41
    },
    navClose () {
      this.$store.dispatch('app/toggleNav')
    },
    isActive (route) {
      return route.fullPath === this.$route.fullPath
    },
    isAffix (tag) {
      if (!tag) return false
      return tag.meta && tag.meta.affix
    },
    filterAffixTags (routes, basePath = '/') {
      let tags = []
      routes.forEach(route => {
        if (route.meta && route.meta.affix) {
          const tagPath = path.resolve(basePath, route.fullPath)
          tags.push({
            fullPath: tagPath,
            path: tagPath,
            name: route.name,
            meta: { ...route.meta }
          })
        }
        if (route.children) {
          const tempTags = this.filterAffixTags(route.children, route.fullPath)
          if (tempTags.length >= 1) {
            tags = [...tags, ...tempTags]
          }
        }
      })
      return tags
    },
    initTags () {
      const affixTags = (this.affixTags = this.filterAffixTags(this.routes))
      for (const tag of affixTags) {
        // Must have tag name
        if (tag.name) {
          this.$store.dispatch('tagsView/addVisitedView', tag)
        }
      }
    },
    addTags () {
      const { name } = this.$route
      console.log(this.$route)
      if (name) {
        const menu = findMenuInfoByPath(this.$route.fullPath, this.menus) || {}
        const obj = { ...this.$route }
        obj.meta.title = menu.permissionName
        this.$store.dispatch('tagsView/addView', obj)
      }
      return false
    },
    moveToCurrentTag () {
      const tags = this.$refs.tag
      this.$nextTick(() => {
        for (const tag of tags) {
          if (tag.to.path === this.$route.path) {
            this.$refs.scrollPane.moveToTarget(tag)
            // when query is different then update
            if (tag.to.fullPath !== this.$route.fullPath) {
              this.$store.dispatch('tagsView/updateVisitedView', this.$route)
            }
            break
          }
        }
      })
    },
    // 刷新
    refreshSelectedTag (view) {
      this.$store.dispatch('tagsView/delCachedView', view).then(() => {
        const { fullPath } = view
        this.$nextTick(() => {
          this.$router.replace({
            path: '/redirect' + fullPath
          })
        })
      })
    },
    // 关闭选中标签
    closeSelectedTag (view) {
      this.$store
        .dispatch('tagsView/delView', view)
        .then(({ visitedViews }) => {
          if (this.isActive(view)) {
            this.toLastView(visitedViews, view)
          }
        })
    },
    // 关闭其他标签
    closeOthersTags (tag = this.selectedTag) {
      this.$router.push(tag)
      this.$store.dispatch('tagsView/delOthersViews', tag).then(() => {
        this.moveToCurrentTag()
      })
    },
    // 关闭全部标签
    closeAllTags (view) {
      this.$store.dispatch('tagsView/delAllViews').then(({ visitedViews }) => {
        if (this.affixTags.some(tag => tag.path === view.path)) {
          return
        }
        this.toLastView(visitedViews, view)
      })
    },
    toLastView (visitedViews, view) {
      const latestView = visitedViews.slice(-1)[0]
      if (latestView) {
        this.$router.push(latestView.fullPath)
      } else {
        // now the default is to redirect to the home page if there is no tags-view,
        // you can adjust it according to your needs.
        if (view.name === 'Dashboard') {
          // to reload home page
          this.$router.replace({ path: '/redirect' + view.fullPath })
        } else {
          this.$router.push('/')
        }
      }
    },
    openMenu (tag, e) {
      const menuMinWidth = 105
      const offsetLeft = this.$el.getBoundingClientRect().left // container margin left
      const offsetWidth = this.$el.offsetWidth // container width
      const maxLeft = offsetWidth - menuMinWidth // left boundary
      const left = e.clientX - offsetLeft + 15 // 15: margin right

      if (left > maxLeft) {
        this.left = maxLeft
      } else {
        this.left = left
      }

      this.top = e.clientY
      this.visible = true
      this.selectedTag = tag
    },
    closeMenu () {
      this.visible = false
    }
  }
}
</script>

<style lang="scss" scoped>
.tags-view-container {
  /*height: 34px;*/
  /*margin-left: 50px;*/
  /*width: 100%;*/
  background: #fff;
  height: 39px;
  padding: 0 5px;
  box-sizing: border-box;
  display: flex;
  /*border-bottom: 1px solid #d8dce5;*/
  /*box-shadow: 0 1px 3px 0 rgba(0, 0, 0, .12), 0 0 3px 0 rgba(0, 0, 0, .04);*/
  .tags-view__items {
    flex: 1;
    display: flex;
    align-items: center;
    .tags-view-item {
      display: flex;
      align-items: center;
      padding: 5px 10px;
      font-size: 12px;
      border: 1px solid #d3d9de;
      border-radius: 4px;
      color: #666666;
      margin-right: 6px;
      cursor: pointer;
      word-break: keep-all;
      white-space: nowrap;
      .tags-view__point {
        display: inline-block;
        width: 6px;
        height: 6px;
        border-radius: 50%;
        margin-right: 6px;
        background: #b0b9bf;
      }
      .icon-close {
        color: #666666;
        margin-left: 5px;
      }
      &.active {
        color: #409eff;
        border: 1px solid #409eff;
        .tags-view__point {
          background: #409eff;
        }
        .icon-close {
          color: #409eff;
        }
      }
    }
  }

  .tag-view__tools {
    display: flex;
    align-items: center;
    .tool-item {
      width: 53px;
      height: 39px;
      cursor: pointer;
      // padding: 0 15px;
    }
    .nav-close {
      transform: rotate(180deg);
      background: url("../../../assets/logo/navClose/navClose.png") center
        center no-repeat;
      background-size: 22px 22px;
      border-right: 1px solid #dfe3e6;
    }
    .show-tag {
      background: url("../../../assets/images/showTag/showTag@2x.png") center
        center no-repeat;
      background-size: 22px 22px;
      border-left: 1px solid #dfe3e6;
    }
    .batch {
      background: url("../../../assets/images/batch/batch@2x.png") center center
        no-repeat;
      background-size: 22px 22px;
      border-left: 1px solid #dfe3e6;
    }
  }
  .tags-view-wrapper {
    height: 39px;
  }
  .contextmenu {
    margin: 0;
    background: #fff;
    z-index: 3000;
    position: absolute;
    list-style-type: none;
    padding: 5px 0;
    border-radius: 4px;
    font-size: 12px;
    font-weight: 400;
    color: #333;
    box-shadow: 2px 2px 3px 0 rgba(0, 0, 0, 0.3);
    li {
      margin: 0;
      padding: 7px 16px;
      cursor: pointer;
      &:hover {
        background: #eee;
      }
    }
  }
}
</style>

<style lang="scss">
.tags-view-wrapper .el-scrollbar__bar.is-vertical {
  display: none;
}
.tags-view-wrapper .el-scrollbar__bar.is-horizontal .el-scrollbar__thumb {
  width: 84%;
  background-color: rgba(144, 147, 153, 0.5);
}
.bacth-handle {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  .batch-handle__item {
    padding: 10px 0;
    cursor: pointer;
  }
}
.hidden-tags {
  // display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  max-height: 420px;
  max-width: 143px;
  overflow: scroll;
  .tags-view-item {
    font-size: 12px;
    color: #666666;
    padding: 10px 0;
    cursor: pointer;
    word-break: break-all;
    white-space: nowrap;
    text-align: left;
    display: block;
    .icon-close {
      color: #666666;
      margin-left: 5px;
    }
    &.active {
      color: #409eff;
      .icon-close {
        color: #409eff;
      }
    }
  }
}
</style>
