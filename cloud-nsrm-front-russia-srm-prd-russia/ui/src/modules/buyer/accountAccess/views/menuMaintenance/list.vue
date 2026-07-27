<template>
  <el-container class="box">
    <!--左侧树形组织结构-->
    <el-aside class="left">
      <el-scrollbar style="height: 100%">
        <el-tree
          ref="tree"
          node-key="id"
          :data="treeData"
          :props="orgTreeProps"
          :default-expanded-keys="expandedList"
          @node-click="nodeClickHandle"
          @node-expand="nodeExpand"
          @node-collapse="nodeCollapse"
        />
      </el-scrollbar>
      <div
        class="resize"
        :title="$t('dataConfMod.shrinkSidebar')"
      >
        ⋮
      </div>
    </el-aside>
    <el-main
      class="mid"
      style="
        flex-grow: 1;
        display: flex;
        flex-direction: column;
        position: relative;
        height: 100%;
        padding-left:16px;
      "
    >
      <FormWrapper
        ref="formWrapper"
        :form-array="preArr"
        @getFormData="getQuerydata"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            type="primary"
            code="rbac:menuMaintenance:add"
            @click="addOne"
          >
            {{ $t("common.add") }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :page-size="pageSize"
        :auto-query="false"
        :comActive="$attrs['changeTab']"
        url="/api-rbac/perm/permission/listPage"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import edit from './edit'
import { menuApi } from 'modb@/accountAccess/api'

const DEFAULT_LANGUAGE = [{ language: 'zh_CN' }]

export default {
  name: 'MenuMaintenance',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  data () {
    return {
      treeData: [],
      parentMenuId: null,
      expandedList: [],
      gridId: 'list',
      pageSize: 15,
      tableData: [],
      tableHeader: [],
      queryParam: {},
      form: {
        permissionCode: '',
        permissionName: '',
        parentPermissionName: '',
        functionName: '',
        startDate: null,
        endDate: null,
        sort: '',
        iconPath: '',
        functionId: '',
        parentPermissionId: ''
      },
      preArr: [
        {
          prop: 'permissionCode',
          label: () => this.$t('dataConfMod.menuCode')
        }, // "菜单编码"
        {
          prop: 'permissionName',
          label: () => this.$t('dataConfMod.menuName')
        }, // "菜单名称"
        {
          prop: 'parentPermissionName',
          label: () => this.$t('dataConfMod.parentMenuId')
        } // "父菜单ID"
      ],
      orgTypeList: [], // 组织类型
      dialogTitle: this.$t('dataConfMod.addOrg'), // "新增组织"
      orgRelDataSelection: [],
      orgRelData: [],
      parentOrgTableData: [],
      parentOrgTableDataPage: {
        total: 0,
        pageNum: 1,
        pageSize: 20
      },
      menuNames: [
        { language: this.$store.getters.language, permissionName: '' }
      ],
      menuSettingList: [],
      accessTypeList: [
        { value: 'v1', label: this.$t('dataConfMod.funcType') + '1' }, // "功能类型1"
        { value: 'v2', label: this.$t('dataConfMod.funcType') + '2' } // "功能类型2"
      ],
      dialogFormVisible: false,
      formLabelWidth: '130px',
      parentOrgdialogVisible: false,
      checkedItemNode: null,
      isActive: false,
      firstLoad: true,
      departmentLoading: false,
      orgTreeData: [
        {
          childrens: [],
          permissionName: ''
        }
      ],
      orgTreeProps: {
        children: 'childPermissions',
        label: 'permissionName',
        isLeaf: data => {
          return data.isLeaf
        }
      }
    }
  },
  updated () {
    const el = document.querySelectorAll(
      '.el-form-item__label[for="menuNames"]'
    )[0]
    if (el && el.className.indexOf(' menuNames-required') === -1) {
      el.className = `${el.className} menuNames-required`
    }
  },
  mounted () {
    this.dragControllerDiv()
  },
  created () {
    var _this = this
    this.tableHeader = [
      {
        prop: 'permissionCode',
        label: () => this.$t('dataConfMod.menuCode') // "菜单编码"
      },
      { prop: 'permissionName', label: () => this.$t('dataConfMod.menuName') }, // "菜单名称"
      {
        prop: 'parentPermissionName',
        label: () => this.$t('dataConfMod.parentMenu') // "父菜单"
      },
      { prop: 'startDate', label: () => this.$t('dataConfMod.startDate') }, // "生效日期"
      { prop: 'endDate', label: () => this.$t('dataConfMod.endDate') }, // "失效日期"
      {
        prop: 'sort',
        label: () => this.$t('dataConfMod.sort') // "菜单顺序"
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'),
        width: 150,
        btnStyle: 'text',
        fixed: 'right',
        showType: 'buttons',
        buttons: [
          {
            callback: row => {
              _this.currentRow = row
              _this.editOneItem(row, 'edit')
            },
            code: 'rbac:menuMaintenance:add',
            formattor: () => _this.$t('common.edit') // "编t辑
          },
          {
            callback: row => _this.deleteOneItem(row),
            code: 'menuMaintenance:delete',
            formattor: () => _this.$t('common.delete')
          },
          {
            formattor: () => _this.$t('common.copy'),
            code: 'rbac:menuMaintenance:add',
            callback: row => {
              _this.currentRow = row
              _this.editOneItem(row, 'copy')
            }
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.queryTreeData()
      this.getQuerydata()
      // 是否
    })
  },
  activated () {
    console.log('[menuMaintenance activated]')
    this.$refs[this.gridId].doLayout()
  },
  methods: {
    // 左右区域 - 拖拽改变宽度
    dragControllerDiv () {
      let resize = document.getElementsByClassName('resize')
      let left = document.getElementsByClassName('left')
      let mid = document.getElementsByClassName('mid')
      let box = document.getElementsByClassName('box')
      for (let i = 0; i < resize.length; i++) {
        // 鼠标按下事件
        resize[i].onmousedown = function (e) {
          // 颜色改变提醒
          resize[i].style.background = '#818181'
          let startX = e.clientX
          resize[i].left = resize[i].offsetLeft
          // 鼠标拖动事件
          document.onmousemove = function (e) {
            let endX = e.clientX
            let moveLen = resize[i].left + (endX - startX) // （endx-startx）=移动的距离。resize[i].left+移动的距离=左边区域最后的宽度
            let maxT = box[i].clientWidth - resize[i].offsetWidth // 容器宽度 - 左边区域的宽度 = 右边区域的宽度

            if (moveLen < 32) moveLen = 32 // 左边区域的最小宽度为32px
            if (moveLen > maxT - 150) moveLen = maxT - 150 // 右边区域最小宽度为150px

            resize[i].style.left = moveLen // 设置左侧区域的宽度

            for (let j = 0; j < left.length; j++) {
              left[j].style.width = moveLen + 'px'
              mid[j].style.width = box[i].clientWidth - moveLen - 10 + 'px'
            }
          }
          // 鼠标松开事件
          document.onmouseup = function () {
            // 颜色恢复
            resize[i].style.background = '#d6d6d6'
            document.onmousemove = null
            document.onmouseup = null
            resize[i].releaseCapture && resize[i].releaseCapture() // 当你不在需要继续获得鼠标消息就要应该调用ReleaseCapture()释放掉
          }
          resize[i].setCapture && resize[i].setCapture() // 该函数在属于当前线程的指定窗口里设置鼠标捕获
          return false
        }
      }
    },
    async deleteOneItem (row) {
      const sign = await this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
      if (sign !== 'confirm') return

      this.$http({
        url: '/api-rbac/perm/permission/deletePermission',
        method: 'POST',
        data: {
          permissionId: row.permissionId
        }
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    nodeExpand (data) {
      this.expandedList.push(data.id) // 在节点展开是添加到默认展开数组
    },
    nodeCollapse (data) {
      this.expandedList.splice(this.expandedList.indexOf(data.id), 1) // 收起时删除数组里对应选项
    },
    queryTreeData () {
      menuApi.getPermissionTree({ parentPermissionId: -1, permissionType: 'MENU' }).then(res => {
        this.treeData = res.data || []
      })
    },
    editOneItem (row, flag) {
      this.isEdit = true
      let tab = {
        component: edit,
        ctrlHeight: true,
        params: {
          flag: flag,
          row,
          tabName: 'menuMaintenanceEdit' + row.permissionCode + flag
        },
        title: row.permissionName,
        name: 'menuMaintenanceEdit' + row.permissionCode + flag
      }
      this.$emit('tab-add', tab)
    },
    addOne () {
      this.isEdit = false
      let tab = {
        component: edit,
        ctrlHeight: true,
        params: {
          flag: 'add',
          tabName: 'menuMaintenanceEdit'
        },
        title: this.$t('dataConfMod.menuManage'),
        name: 'menuMaintenanceEdit'
      }
      this.$emit('tab-add', tab)
    },
    // 异步树叶子节点懒加载逻辑
    loadNode (node, resolve) {
      // 一级节点处理
      if (node.level === 0) {
        const queryParma = { parentPermissionId: -1 }
        this.getDepartmentTree(queryParma, resolve) // 查询一级节点
      } else if (node.level >= 1) {
        // 注意！把resolve传到你自己的异步中去
        const nodeParme = {}
        nodeParme.parentPermissionId = node.data.permissionId
        this.getDepartmentTree(nodeParme, resolve)
      }
    },
    // 加载子节点
    getDepartmentTree (parmes, resolve) {
      if (this.firstLoad) {
        this.departmentLoading = true
      }

      menuApi.getListChildrenData(parmes)
        .then(response => {
          if (response && response.data) {
            resolve(response.data.list)
          } else {
            this.$message({
              message: this.$t('dataConfMod.loadDataFail') + response.msg,
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
      menuApi.getListChildrenData({
        parentPermissionId: -1
      })
        .then(response => {
          if (response && response.data) {
            let resData = response.data.list
            resData.forEach((item, index) => {
              this.orgTreeData.push(item)
              this.orgTreeData[index].childrens = []
            })
          } else {
            this.$message({
              message: this.$t('dataConfMod.loadDataFail') + response.msg,
              type: 'error'
            })
          }
        })
        .finally(() => {
          this.firstLoad = false
          this.departmentLoading = false
        })
    },
    nodeClickHandle (data, node, context) {
      // 记录当前选中节点ID
      this.checkedItemNode = node
      const { permissionId = -1, permissionName } = data
      this.checkedItemId = permissionId
      this.$refs.formWrapper.setValue('permissionCode', '')
      this.$refs.formWrapper.setValue('permissionName', '')
      this.$refs.formWrapper.setValue('parentPermissionName', permissionName)
      this.$nextTick(() => this.getQuerydata({
        parentPermissionName: permissionName,
        parentPermissionId: permissionId
      }))
    },
    getQuerydata (v) {
      this.queryParam = { ...v }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    }
  }
}
</script>

<style lang="scss" scoped>

/* 拖拽相关样式 */
/*包围div样式*/
.box {
  width: 100%;
  height: 100%;
  overflow: hidden;
  //  box-shadow: -1px 9px 10px 3px rgba(0, 0, 0, 0.11);
  ::-webkit-scrollbar {
    width: 7px !important;
  }
}
/*左侧div样式*/
.left {
  width: calc(20% - 10px); /*左侧初始化宽度*/
  height: 100%;
  background: #ffffff;
  position: relative;
  overflow: hidden;
  padding-left: 0;
  padding-right: 7px !important;
}
.left .el-tree {
  overflow-x: hidden;
}
/*拖拽区div样式*/
.resize {
  cursor: col-resize;
  position: absolute;
  top: 36%;
  right: 0px;
  background-color: #d6d6d6;
  border-radius: 5px;
  margin-top: -10px;
  width: 8px;
  height: 42px;
  background-size: cover;
  background-position: center;
  /*z-index: 99999;*/
  font-size: 30px;
  color: white;
}
/*拖拽区鼠标悬停样式*/
.resize:hover {
  color: #444444;
}
/*右侧div'样式*/
.mid {
  width: calc(80%); /*右侧初始化宽度*/
  height: 100%;
  overflow: hidden;
  background: #fff;
  box-shadow: -1px 4px 5px 3px rgba(0, 0, 0, 0.11);
}
</style>
