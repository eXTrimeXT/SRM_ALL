<template>
  <el-container
    direction="vertical"
  >
    <el-main>
      <div class="form-container">
        <el-form
          ref="funForm"
          :model="form"
          class="form-roleContainer"
          :rules="rules"
          label-width="70px"
        >
          <el-row>
            <el-col :span="8">
              <!-- 角色编码 -->
              <el-form-item :label="$t('dataConfMod.roleCode')" prop="roleCode">
                <el-input v-model="form.roleCode" :disabled="curOpt === 'edit'" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <!-- 生效日期 -->
              <el-form-item :label="$t('dataConfMod.startDate')" prop="startDate">
                <el-date-picker
                  v-model="form.startDate"
                  type="date"
                  :placeholder="$t('common.pleaseSelectDate')"
                  format="yyyy-MM-dd"
                  value-format="timestamp"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <!-- 失效日期 -->
              <el-form-item :label="$t('dataConfMod.endDate')" prop="endDate">
                <el-date-picker
                  v-model="form.endDate"
                  type="date"
                  :placeholder="$t('common.pleaseSelectDate')"
                  format="yyyy-MM-dd"
                  value-format="timestamp"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="20">
              <!-- 角色类型 -->
              <el-form-item :label="$t('dataConfMod.roleType')" prop="roleType">
                <DictSelect
                  v-model="form.roleType"
                  code="ROLE_TYPE"
                  multiple
                  :placeholder="$t('dataConfMod.roleTypeTips')"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20" class-name="roleLanguages">
            <el-col :span="20">
              <!-- 角色名称 -->
              <el-form-item :label="$t('dataConfMod.roleName')" prop="roleLanguages">
                <el-input
                  v-model="roleLanguages[0].roleName"
                  :placeholder="$t('dataConfMod.roleName')"
                  class="input-with-select"
                >
                  <el-select
                    slot="prepend"
                    v-model="roleLanguages[0].language"
                    :placeholder="$t('common.language')"
                    style="width: 70px;"
                  >
                    <el-option
                      v-for="item in langList"
                      :key="item.language"
                      :label="item.languageName"
                      :value="item.language"
                    />
                  </el-select>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <i class="el-icon-circle-plus-outline buttonAddAndReduce" @click="addLanguage()" />
            </el-col>
          </el-row>
          <template v-for="(roleItem, index) in roleLanguages">
            <!-- 第二个开始渲染 -->
            <el-row
              v-if="index > 0"
              :key="'roleItem_'+index"
              class="roleLanguages"
              :gutter="20"
            >
              <el-col :span="20">
                <div>
                  <el-form-item :key="index" prop="roleLanguages">
                    <el-input
                      v-model="roleItem.roleName"
                      :placeholder="$t('dataConfMod.roleName')"
                      class="input-with-select"
                    >
                      <el-select
                        slot="prepend"
                        v-model="roleItem.language"
                        :placeholder="$t('common.language')"
                        style="width: 70px;"
                      >
                        <el-option
                          v-for="item in langList"
                          :key="item.language"
                          :label="item.languageName"
                          :value="item.language"
                        />
                      </el-select>
                    </el-input>
                  </el-form-item>
                </div>
              </el-col>
              <el-col :span="4">
                <i class="el-icon-circle-plus-outline buttonAddAndReduce" @click="addLanguage()" />
                <i class="el-icon-remove-outline buttonAddAndReduce" @click="removeLanguage(roleItem, index)" />
              </el-col>
            </el-row>
          </template>
        </el-form>
        <div style="position: relative">
          <el-row :gutter="20">
            <!-- <el-col :span="12">
              <div class="tree-title">{{ $t('dataConfMod.interfaceAuthority') }}</div>
              <el-input
                v-model="filterTextInterface"
                :placeholder="$t('common.placeholder')"
                @keyup.enter.native="interfaceTreeQuery"
              >
                <el-button slot="append" type="primary" @click="interfaceTreeQuery"> 查询</el-button>
              </el-input>
              <div :id="interfaceTreeId" class="allTree">
                <el-tree
                  ref="interfaceRoleSelectTree"
                  :class="interfaceTreeId"
                  :props="treeProps"
                  show-checkbox
                  :data="interfaceTreeData"
                  :default-expanded-keys="interFaceExpandedId"
                  :filter-node-method="filterNode"
                  node-key="permissionId"
                  :check-strictly="false"
                  @check-change="interFaceTreeCheckChange"
                />
              </div>
            </el-col> -->
            <el-col :span="20">
              <div class="tree-title">
                {{ $t('dataConfMod.functionButton') }}
              </div>
              <el-input
                v-model="filterTextButton"
                :placeholder="$t('common.placeholder')"
                @keyup.enter.native="buttonTreeQuery"
              >
                <el-button slot="append" type="primary" @click="buttonTreeQuery">
                  {{ $t("common.search") }}
                </el-button>
              </el-input>
              <div :id="buttonTreeId" class="allTree">
                <el-tree
                  ref="buttonRoleSelectTree"
                  :class="buttonTreeId"
                  :props="treeProps"
                  show-checkbox
                  :data="buttonTreeData"
                  :default-expanded-keys="buttonExpandedId"
                  :filter-node-method="filterNode"
                  node-key="permissionId"
                  :check-strictly="false"
                />
                <!-- @check-change="buttonTreeCheckChange" -->
              </div>
            </el-col>
          </el-row>
        </div>
      </div>
      <CToolbar>
        <template #right>
          <el-button
            type="primary"
            @click="saveHandle"
          >
            {{ $t('common.affirm') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import { debounce } from '@/utils'
import { tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import { roleApi, accessApi } from 'modb@/accountAccess/api'

const getChildMenuId = (nodes = [], resArr = []) => {
  let temArr = []
  nodes.forEach(item => {
    if (item.permissionType == 'MENU') {
      temArr.push(item.permissionId)
      if (item.childPermissions) {
        getChildMenuId(item.childPermissions, resArr)
      }
    }
  })
  return temArr
}

const getChildMenu = (permissionId = null, nodes = [], resArr = []) => {
  if (!permissionId) return []
  for (let i = 0; i < nodes.length; i++) {
    const tmpArr = []
    if (permissionId === nodes[i].permissionId) {
      return nodes[i].childPermissions
    }
    if (nodes[i].childPermissions) {
      const findResult = getChildMenu(permissionId, nodes[i].childPermissions, tmpArr)
      if (findResult) {
        return findResult
      }
    }
  }
}
export default {
  name: 'RoleMaintenanceEdit',
  components: {
    CToolbar
  },
  mixins: [tabTodoMixin],
  data () {
    const languageNameCkeck = (rule, value, callback) => {
      const roleLanguages = this.roleLanguages
      if (!roleLanguages.length) {
        callback(new Error(this.$t('dataConfMod.msgRoleName'))) // '请填写角色名称'
      } else if (roleLanguages.filter(i => !i.language || !i.roleName).length) {
        callback(new Error(this.$t('dataConfMod.msgRoleName'))) // '请填写角色名称'
      } else {
        callback()
      }
    }
    return {
      interfaceTreeId: 'interfaceTreeId',
      buttonTreeId: 'buttonTreeId',
      activeCollapse: ['1'],
      roleConfigDetail: {}, // 角色配置详情
      buttonExpandedId: [], // 按钮树形展开ID
      interFaceExpandedId: [], // 接口树形展开ID
      buttonTreeData: [],
      interfaceTreeData: [],
      treeProps: {
        children: 'childPermissions',
        label: 'permissionName'
      },
      filterTextButton: '',
      filterTextInterface: '',
      activeName: 'first',
      langList: [],
      form: {
        roleCode: null,
        startDate: null,
        endDate: null,
        roleType: null
      },
      rules: {
        roleCode: [
          { required: true, message: this.$t('dataConfMod.msgRoleCode') }
        ], // "请输入角色编码"
        startDate: [
          { required: true, message: this.$t('dataConfMod.msgStartDate') }
        ], // "请输入生效日期"
        // endDate: [{ required: true, message: "请输入失效日期" }],
        roleType: [
          { required: true, message: this.$t('dataConfMod.msgRoleType') }
        ], // "请输入角色类型"
        roleLanguages: [
          { validator: languageNameCkeck, trigger: ['blur', 'change'] },
          { required: false, message: this.$t('dataConfMod.msgRoleName') } // "请输入角色名称"
        ]
      },
      curOpt: 'add',
      roleId: '',
      roleLanguages: [{ language: this.$store.getters.language, roleName: '' }]
    }
  },
  computed: {},
  watch: {},
  async created () {
    const { flag, row = {} } = this.$attrs.params
    this.curOpt = flag
    this.getLanguage()
    if (flag === 'add') {
      this.form = {
        startDate: new Date().getTime()
      }
    } else {
      this.roleId = row.roleId
    }
    await this.queryTreeData() // 查询 按钮和接口数据
  },
  // 2023-01-14 调整方案
  // 1、调整接口、按钮全面页面的位置（接口放左边，按钮放右边）；
  // 2、前端新增loading，接口和按钮的查询结果返回成功才允许用户操作；
  // 3、选择菜单，当前tab页的儿子被全选，两边只同步菜单，但儿子不同步（前提：所有（当前勾选）菜单需要全部展示但非勾选状态）；
  //    取消：取消父级，当前tab页子级全部取消，第二个tab也是父级子级全部取消；
  // 4、对菜单进行勾选的时候，需要左右两边的菜单进行对齐优化；
  methods: {
    buttonTreeQuery () {
      this.$refs.buttonRoleSelectTree.filter(this.filterTextButton)
    },
    interfaceTreeQuery () {
      this.$refs.interfaceRoleSelectTree.filter(this.filterTextInterface)
    },
    // 保存数据
    saveHandle () {
      this.$refs.funForm.validate(valid => {
        if (valid) {
          // 按钮权限
          const checkedNodes = this.$refs.buttonRoleSelectTree.getCheckedNodes(false, true)
          // 接口权限
          // const checkedNodes2 = this.$refs.interfaceRoleSelectTree.getCheckedNodes(false, true)
          let roleMenusAll = [] // 菜单
          let roleButtons = [] // 按钮
          let roleInterface = [] // 接口
          checkedNodes.forEach(e => {
            if (e.permissionType == 'MENU') {
              roleMenusAll.push(e.permissionId)
              // 把菜单下面的接口都拿到
              let menuChildPermissions = getChildMenu(e.permissionId, this.interfaceTreeData) || []
              // console.log(e.permissionName + 'childPermissions-数据')
              // console.log(menuChildPermissions)
              menuChildPermissions.forEach(item => {
                if (item.permissionType == 'INTERFACE') {
                  roleInterface.push({ permissionId: item.permissionId })
                }
              })
              // console.log('roleInterfaceData')
              // console.log(roleInterface)
            } else if (e.permissionType == 'BUTTON') {
              // 按钮权限
              roleButtons.push({ permissionId: e.permissionId })
              // 把按钮下面的接口都拿到
              let btnChildPermissions = getChildMenu(e.permissionId, this.interfaceTreeData) || []
              btnChildPermissions.forEach(item => {
                if (item.permissionType == 'INTERFACE') {
                  roleInterface.push({ permissionId: item.permissionId })
                }
              })
            }
          })
          // checkedNodes2.forEach(e => {
          //   if (e.permissionType == 'MENU') {
          //     roleMenusAll.push(e.permissionId)
          //   } else if (e.permissionType == 'INTERFACE') {
          //     roleInterface.push({ permissionId: e.permissionId })
          //   }
          // })

          // 菜单去重
          roleMenusAll = Array.from(new Set(roleMenusAll))
          let roleMenus = roleMenusAll.map(i => ({
            permissionId: i
          }))

          const { roleType, startDate, endDate, ...rest } = this.form
          const submitData = {
            role: {
              ...rest,
              roleType: (roleType || []).join(','),
              startDate: startDate ? new Date(startDate).getTime() : '',
              endDate: endDate ? new Date(endDate).getTime() : ''
            },
            roleLanguages: this.roleLanguages,
            roleButtons,
            roleMenus,
            roleInterface
          }
          if (this.curOpt === 'add') {
            // 新增
            roleApi.roleAddHttp(submitData).then(res => {
              this.$message({
                message: res.message,
                type: 'success'
              })
              this.back()
            })
          } else if (this.curOpt === 'copy') {
            roleApi.roleCopyHttp(submitData).then(res => {
              this.$message({
                message: res.message,
                type: 'success'
              })
              this.back()
            })
          } else {
            // 编辑
            roleApi.roleModifyHttp(submitData).then(res => {
              this.$message({
                message: res.message,
                type: 'success'
              })
              this.back()
            })
          }
        } else {
          return false
        }
      })
    },
    filterNode (value, data, node) {
      if (!value) {
        node.expanded = false
        return true
      }
      return this.checkBelongToChooseNode(value, data, node)
    },
    // 判断传入的节点是不是选中节点的子节点
    checkBelongToChooseNode (value, data, node) {
      if (data.permissionName.indexOf(value) !== -1) {
        return true
      }
      const level = node.level
      // 如果传入的节点本身就是一级节点就不用校验了
      if (level === 1) {
        return false
      }
      // 先取当前节点的父节点
      let parentData = node.parent
      // 遍历当前节点的父节点
      let index = 0
      while (index < level - 1) {
        // 如果匹配到直接返回
        if (parentData.data.permissionName.indexOf(value) != -1) {
          return true
        }
        // 否则的话再往上一层做匹配
        parentData = parentData.parent
        index++
      }
      // 没匹配到返回false
      return false
    },
    // 接口树 选中菜单
    interFaceTreeCheckChange (node, isChecked, isChild) {
      if (node.permissionType == 'MENU') {
        if (isChecked == false) { // 去掉勾选
          if (isChild == false) {
            this.$refs.buttonRoleSelectTree.setChecked(node.permissionId, isChecked, true)
          }
        } else { // 勾选菜单类型时
          // 获取这个节点下面的所有菜单节点
          let childPermissions = node.childPermissions
          let childPermissionsMenuIds = getChildMenuId(childPermissions)
          if (childPermissionsMenuIds.length > 0) {
            // 勾上当前节点下面的菜单按钮不用勾上
            childPermissionsMenuIds.forEach(i => {
              this.$refs.buttonRoleSelectTree.setChecked(i, isChecked, false)
              if (this.$refs.buttonRoleSelectTree.store.nodesMap[i]) {
                this.$refs.buttonRoleSelectTree.store.nodesMap[i].expanded = true
              }
            })
            // 当前对应节点勾上，按钮树对应节点展开
            if (this.$refs.buttonRoleSelectTree.store.nodesMap[node.permissionId]) {
              this.$refs.buttonRoleSelectTree.store.nodesMap[node.permissionId].expanded = true
            }
          } else {
            this.$refs.buttonRoleSelectTree.setChecked(node.permissionId, isChecked, false)
            if (this.$refs.buttonRoleSelectTree.store.nodesMap[node.permissionId]) {
              this.$refs.buttonRoleSelectTree.store.nodesMap[node.permissionId].expanded = true
            }
          }
          // 展开按钮树对应选中的节点
          if (this.$refs.buttonRoleSelectTree.store.nodesMap[node.parentPermissionId]) {
            this.$refs.buttonRoleSelectTree.store.nodesMap[node.parentPermissionId].expanded = true
          }
        }
      }
      // 选中的是接口类型数据, 把接口树对应的菜单勾上，但是不勾菜单下的子节点
      if (node.permissionType == 'INTERFACE') {
        if (isChecked == true) {
          if (isChild == false) {
            this.$refs.buttonRoleSelectTree.setChecked(node.parentPermissionId, isChecked, false)
          }
        } else {
          // 判断父节点是否还有被选中
          // 判断父节点是否还有被选中
          let parentPermissionId = node.parentPermissionId
          const HalfCheckedNodes = this.$refs.interfaceRoleSelectTree.getHalfCheckedNodes()
          let hasNode = HalfCheckedNodes.find(item => (item.permissionId == parentPermissionId))
          if (!hasNode) {
            this.$refs.buttonRoleSelectTree.setChecked(node.parentPermissionId, isChecked, true)
          }
        }
      }
    },
    // permissionType 类型  菜单 MENU | 按钮 BUTTON | 接口 INTERFACE
    // 按钮树 选中切换
    buttonTreeCheckChange (node, isChecked, isChild) {
      if (node.permissionType == 'MENU') {
        // 去掉勾选
        if (isChecked == false) {
          if (isChild == false) {
            this.$refs.interfaceRoleSelectTree.setChecked(node.permissionId, isChecked, true)
          }
        } else {
          let childPermissions = node.childPermissions
          let childPermissionsMenuIds = getChildMenuId(childPermissions)
          if (childPermissionsMenuIds.length > 0) {
            // 勾上当前节点下面的菜单且接口不用勾上
            childPermissionsMenuIds.forEach(i => {
              this.$refs.interfaceRoleSelectTree.setChecked(i, isChecked, false)
              if (this.$refs.interfaceRoleSelectTree.store.nodesMap[i]) {
                this.$refs.interfaceRoleSelectTree.store.nodesMap[i].expanded = true
              }
            })
            // 当前对应节点勾上，接口树对应节点展开
            if (this.$refs.interfaceRoleSelectTree.store.nodesMap[node.permissionId]) {
              this.$refs.interfaceRoleSelectTree.store.nodesMap[node.permissionId].expanded = true
            }
          } else {
            this.$refs.interfaceRoleSelectTree.setChecked(node.permissionId, isChecked, false)
            if (this.$refs.interfaceRoleSelectTree.store.nodesMap[node.permissionId]) {
              this.$refs.interfaceRoleSelectTree.store.nodesMap[node.permissionId].expanded = true
            }
          }
          // 展开接口树对应选中的节点
          if (this.$refs.interfaceRoleSelectTree.store.nodesMap[node.parentPermissionId]) {
            this.$refs.interfaceRoleSelectTree.store.nodesMap[node.parentPermissionId].expanded = true
          }
        }
      }
      // 选中的是按钮类型数据, 把接口树对应的菜单勾上，但是不勾菜单下的子节点
      if (node.permissionType == 'BUTTON') {
        if (isChecked == true) {
          if (isChild == false) {
            this.$refs.interfaceRoleSelectTree.setChecked(node.parentPermissionId, isChecked, false)
          }
        } else {
          // 判断父节点是否还有被选中
          let parentPermissionId = node.parentPermissionId
          const HalfCheckedNodes = this.$refs.buttonRoleSelectTree.getHalfCheckedNodes()
          let hasNode = HalfCheckedNodes.find(item => (item.permissionId == parentPermissionId))
          if (!hasNode) { // 没有被选中就把接口树对应的菜单勾掉
            this.$refs.interfaceRoleSelectTree.setChecked(node.parentPermissionId, isChecked, true)
          }
        }
      }
    },
    async queryTreeData () {
      // 按钮权限
      let roleData = await roleApi.getbuttonTree({ parentPermissionId: -1 })
      // 接口权限
      let interfaceData = await accessApi.getInterfaceTree({ parentPermissionId: -1 })
      this.buttonTreeData = roleData.data || []
      this.interfaceTreeData = interfaceData.data || []
      // 编辑 查看 查询接口配置信息
      if (this.curOpt !== 'add') {
        this.interfaceTreeId = `interfaceTreeId-${this.roleId}`
        this.buttonTreeId = `buttonTreeId-${this.roleId}`
        let configDetail = await roleApi.queryRoleInfoById(this.roleId)
        this.roleConfigDetail = configDetail.data || {}
      }

      // 按钮数据处理
      this.buttonExpandedId = []
      this.buttonTreeData.forEach(i => {
        this.buttonExpandedId.push(i.permissionId)
      })
      // 接口数据处理
      this.interFaceExpandedId = []
      this.interfaceTreeData.forEach(i => {
        this.interFaceExpandedId.push(i.permissionId)
      })
      // 编辑 、查看、复制
      if (this.curOpt !== 'add') {
        const { role, roleLanguages, roleButtons, roleInterface, roleMenus } = this.roleConfigDetail
        const { roleType, startDate, endDate, ...rest } = role // 角色数据
        this.form = {
          ...rest,
          roleType: roleType.indexOf(',') ? roleType.split(',') : roleType,
          startDate: new Date(startDate),
          endDate: endDate ? new Date(endDate) : ''
        }
        if (roleLanguages && roleLanguages.length) {
          this.roleLanguages = roleLanguages
        } else {
          this.roleLanguages = [
            {
              language: 'zh_CN',
              roleName: ''
            }
          ]
        }
      }

      // 复制
      if (this.curOpt == 'copy') {
        this.form.roleCode = ''
        this.form.startDate = new Date().getTime()
        this.form.roleLanguages = [{ language: this.$store.getters.language, roleName: '' }]
      }

      this.$nextTick(() => {
        this.layoutTree() // 勾选展开树形
      })
    },
    // 表单和树结构处理
    layoutTree () {
      // 编辑 、查看
      if (this.curOpt !== 'add') {
        const { roleButtons, roleInterface, roleMenus } = this.roleConfigDetail
        let rolePermissions = [...roleButtons, ...roleMenus]
        rolePermissions.forEach(node => {
          this.$refs.buttonRoleSelectTree.setChecked(node.permissionId, true)
        })

        // let roleInterfaces = [...roleMenus, ...roleInterface]
        // roleInterfaces.forEach(node => {
        //   this.$refs.interfaceRoleSelectTree.setChecked(node.permissionId, true)
        // })
      }
    },
    getLanguage () {
      // 获取语言列表
      accessApi.getLanguageList().then(data => {
        this.langList = data.data
      })
        .catch(err => {
          console.log(err)
        })
    },
    addLanguage () {
      this.roleLanguages.push({
        language: 'zh_CN',
        roleName: ''
      })
    },
    removeLanguage (item, index) {
      this.roleLanguages.splice(index, 1)
    },
    back () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('RoleMaintenanceList.getQuerydata')
    }
  }
}
</script>
<style scoped lang="scss">
.subtitle{
  // position: absolute;
  // top: 5px;
  // right: 0px;
  display: block;
  line-height: 30px;
}
.form-container{
  padding: 15px;
  height: 100%;
  overflow: auto;
}
.allTree{
  height: calc(57vh);
  overflow: auto;
}
.buttonAddAndReduce{
  color: #96999C;
  font-size: 18px;
  line-height: 28px;
  margin: 0 5px;
  cursor: pointer;
}
.tree-title{
  padding: 5px 0;
  font-weight: bold;
}
</style>
