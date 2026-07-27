<template>
  <div class="commonMenuSetting">
    <div class="grid-content">
      <h3 class="grid-title">
        <!-- 常用功能  -->
        {{ $t("dashboard.commonFunction") }}
        <span
          class="info-more"
          @click="setting"
        >
          <!-- 设置 -->
          {{ $t("common.more") }}
          <i class="el-icon-arrow-right" />
        </span>
      </h3>
      <el-row class="commonMenu">
        <el-col
          v-for="(item, k) in showItemList"
          :key="k"
          :xs="12"
          :sm="8"
          :md="4"
          :lg="4"
          :xl="3"
          class="menu-col"
          @click.native="toRoute(item.path)"
        >
          <img v-if="item.icon" :src="item.icon">
          <div v-else class="no-img" />
          <p class="the_sub_route" :title="item.title">
            {{ item.title }}
          </p>
        </el-col>
      </el-row>
    </div>
    <!-- 设置常用功能弹窗 -->
    <srm-dialog
      v-if="dialogVisible"
      :visible.sync="dialogVisible"
      :title="$t('dashboard.setCommonFunc')"
      size="large"
      :close-on-click-modal="false"
      @close="closeCallback"
    >
      <div class="select-wrapper">
        <div class="selected-list">
          <SortableList
            v-model="menuModel"
            :press-delay="200"
            axis="x"
          >
            <SortableItem
              v-for="(item, index) in menuModel"
              :key="index"
              :index="index"
              :item="item"
              @on-remove="removeItem"
            />
          </SortableList>
        </div>
        <div v-if="dialogVisible" class="select-list">
          <!-- 一级 -->
          <div v-for="(item, index) in menus" :key="index">
            <div class="checkBox-title">
              {{ item.permissionName }}
            </div>
            <div class="menus">
              <!-- 二级 -->
              <template v-for="menu in item.childPermissions">
                <el-checkbox
                  v-if="menu.childPermissions.length == 0"
                  :key="menu.permissionId"
                  v-model="checkboxModel[menu.permissionId]"
                  true-label="Y"
                  false-label="N"
                  @change="(val, e) => checkboxChange(val,menu,e)"
                >
                  {{ menu.permissionName }}
                </el-checkbox>
                <div v-else :key="menu.permissionId" class="subMenu">
                  <div class="checkBox-title-sub">
                    {{ menu.permissionName }}
                  </div>
                  <div class="subMenu-content">
                    <!-- 三级 -->
                    <template v-for="submenu in menu.childPermissions">
                      <el-checkbox
                        v-if="submenu.childPermissions.length == 0"
                        :key="submenu.permissionId"
                        v-model="checkboxModel[submenu.permissionId]"
                        true-label="Y"
                        false-label="N"

                        @change="(val, e) => checkboxChange(val,submenu,e)"
                      >
                        {{ submenu.permissionName }}
                      </el-checkbox>
                      <div v-else :key="submenu.permissionId" class="subMenu">
                        <!-- 四级 -->
                        <el-checkbox
                          v-for="threemenu in menu.childPermissions"
                          :key="threemenu.permissionId"
                          v-model="checkboxModel[threemenu.permissionId]"
                          true-label="Y"
                          false-label="N"
                          @change="(val, e) => checkboxChange(val,threemenu,e)"
                        >
                          {{ threemenu.permissionName }}
                        </el-checkbox>
                      </div>
                    </template>
                  </div>
                </div>
              </template>
            </div>
          </div>
        </div>
      </div>
      <div slot="footer">
        <el-button @click="cancle">
          {{ $t("common.cancel") }}
        </el-button>
        <el-button
          type="primary"
          @click="submit"
        >
          {{ $t("common.confirm") }}
        </el-button>
      </div>
    </srm-dialog>
  </div>
</template>

<script>
import { SortableList, SortableItem } from '../sortable'
import { mapState } from 'vuex'
import { findAll, save } from '../api'
import { getFileUrl } from '@/library/utils/file'

export default {
  name: 'CommonMenuSetting',

  components: { SortableList, SortableItem },

  data () {
    return {
      itemList: [],
      menuModel: [],
      dialogVisible: false,
      editableTabsValue: 'handleInfoTab',
      checkboxModel: {}
    }
  },

  computed: {
    ...mapState({
      menus: state => state.user.userInfo.menus || [],
      menuIds: state => state.user.userInfo.menus.map(i => i.permissionId),
      device: state => state.app.device,
      userType: state => state.user.userType
    }),
    // 显示的数据
    showItemList () {
      if (this.device === 'device-xl') {
        if (this.userType === 'BUYER') {
          return this.itemList.slice(0, 8)
        } else {
          return this.itemList.slice(0, 16)
        }
      } else {
        if (this.userType === 'BUYER') {
          return this.itemList.slice(0, 6)
        } else {
          return this.itemList.slice(0, 12)
        }
      }
    }
  },
  watch: {
    dialogVisible: {
      handler (data) { // 打开配置重新设置选中框
        if (data) {
          let ids = this.itemList.map(i => (i.id))
          for (let k in this.checkboxModel) {
            if (ids.includes(parseInt(k))) {
              this.checkboxModel[k] = 'Y'
            } else {
              this.checkboxModel[k] = 'N'
            }
          }
        }
      },
      immediate: true,
      deep: true
    }
  },

  mounted () {
    this.getIconList() // 查询图标
  },

  methods: {
    // 查询图标
    async getIconList () {
      const parmas = {
        fileModular: 'rbac',
        fileFunction: 'function',
        fileType: 'icon',
        convertBase64: 'Y',
        pageNum: 1,
        pageSize: 200
      }
      let iconList = []

      findAll().then(res => {
        this.itemList = res.data
          .map(item => {
            const {
              sort,
              functionIcon,
              functionAddress,
              permissionName,
              permissionId,
              parentPermissionId
            } = item
            let icon
            if (functionIcon) {
              try {
                icon = getFileUrl('function-icon/' + functionIcon)
              } catch (e) {
                icon = ''
              }
            }

            return {
              title: permissionName,
              icon,
              path: functionAddress,
              id: permissionId,
              parentId: parentPermissionId,
              sort
            }
          })
          .sort((a, b) => {
            const { sort: c = 0 } = a
            const { sort: d = 0 } = b
            return c - d
          })

        this.menuModel = [...this.itemList]
        this.menuModel.forEach(element => {
          this.checkboxModel[element.id] = 'Y'
        })
      })
    },
    checkboxChange (value, row, e) {
      let permissionId = row.permissionId
      let count = this.menuModel.length
      if (value === 'Y') { // 勾上
        if (count > 16) { // 最多给16个不给增加
          this.limitStatus = true
          this.checkboxModel[permissionId] = 'N'
          return this.$message({
            type: 'warning',
            message: this.$t('dashboard.msgSel9cm') // 最多只能选择9个常用功能
          })
        } else {
          this.limitStatus = false
          let pIndex = this.menuModel.findIndex(i => i.id == permissionId)
          if (pIndex < 0) {
            let icon
            if (row.functionIcon) {
              try {
                icon = getFileUrl('function-icon/' + row.functionIcon)
              } catch (e) {
                icon = ''
              }
            }

            this.menuModel.push({
              icon,
              path: row.functionAddress,
              title: row.permissionName,
              id: row.permissionId,
              parentId: row.parentPermissionId
            })
          }
        }
      } else { // 去掉勾选
        this.checkboxModel[permissionId] = 'N'
        let pIndex = this.menuModel.findIndex(i => i.id == permissionId)
        if (pIndex > -1) {
          this.menuModel.splice(pIndex, 1)
        }
      }
    },
    // 关闭设置窗口
    closeCallback () {
      this.menuModel = [...this.itemList]
    },
    // 删除
    removeItem (index, id, parentId) {
      this.menuModel.splice(index, 1)
    },

    // 点击设置
    setting () {
      this.dialogVisible = true
    },

    toRoute (path) {
      if (!path) return
      this.$router.push(path)
    },
    // 取消
    cancle () {
      this.menuModel = [...this.itemList]
      this.dialogVisible = false
    },
    // 确认
    submit () {
      const params = this.menuModel.map(i => ({ permissionId: i.id }))
      save(params).then(res => {
        this.$message({
          type: 'success',
          message: res.message
        })
        this.itemList = [...this.menuModel].map((item, index) => ({
          ...item,
          sort: index
        }))
        this.getIconList()
        this.dialogVisible = false
      })
    }

  }
}
</script>

<style lang="scss" scoped>
.commonMenu {
  min-height: 88px;
  .menu-col {
    cursor:pointer;
    margin-top: 10px;
    z-index:9999;
    img {
      display: block;
      width: 38px;
      height: 38px;
      margin: 11px auto 7px;
    }
    .no-img {
      width:38px;
      height: 38px;
      border-radius: 10px;
      background: #ddd;
      margin:0 auto;
      margin: 11px auto 7px;
    }
  }
}
.grid-content {
  .the_sub_route {
    font-size: 14px;
    color:#393E45;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    height: 22px;
    line-height: 22px;
    margin:0;
    margin-bottom: 10px;
    text-align: center;
  }
}
.select-wrapper {
  height: 430px;
  overflow: auto;
  position: relative;
  .checkBox-title {
    font-weight: bolder;
    margin-bottom: 10px;
  }
  .selected-list {
    height: 100px;
    width: 100%;
    border-bottom: 1px solid #ddd;
    position: absolute;
    top: 0;
    left: 0;
  }
  .select-list {
    position: absolute;
    top: 100px;
    left: 0;
    width: 100%;
    padding: 15px 0;
    height: 330px;
    overflow: auto;
    .el-checkbox {
      padding-bottom: 10px;
    }
  }
  .subMenu{
    padding-bottom: 10px;
    padding-left: 16px;
    .checkBox-title-sub{
      font-size: 14px;
      line-height: 22px;
      font-weight: bold;
    }
  }
}
</style>
