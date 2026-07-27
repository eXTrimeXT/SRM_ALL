<template>
  <el-container class="box">
    <!--左侧树形组织结构-->
    <el-aside class="left">
      <el-scrollbar style="height: 100%">
        <el-tree
          ref="tree"
          v-loading="orgLoading"
          element-loading-background="rgba(0, 0, 0, 0.4)"
          lazy
          node-key="id"
          :data="orgTreeData"
          :props="orgTreeProps"
          :load="loadNode"
          @node-click="nodeClick"
        />
      </el-scrollbar>
      <div class="resize" :title="$t('dataConfMod.shrinkSidebar')">
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
      <FormWrapper :form-array="searchFormConfig" @getFormData="getQuerydata" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton type="primary" @click="openDialog('add')">
            添加角色
          </AuthorityButton>
          <MImport
            ref="import"
            title="导入"
            up-load-url="/api-pj/organizationRole/importExcel"
            type="primary"
            code="processRole:import"
            :extra-data="extraData"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
          <AuthorityButton type="primary" @click="pushBpm('ADD',currentRows)">
            手动同步BPM
          </AuthorityButton>
          <AuthorityButton type="primary" @click="operation('useBatch')">
            启用
          </AuthorityButton>
          <AuthorityButton type="primary" @click="operation('nouseBatch')">
            禁用
          </AuthorityButton>
        </template>
      </MainHeader>

      <TableView
        :ref="gridId"
        :table-data="tableList"
        :table-header="tableHeader"
        :page-size="pageSize"
        :checkbox="true"
        :check-change="handleCheckChange"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        url="/api-pj/organizationRole/listPage"
      />
      <!-- 弹框区域 -->
      <srm-dialog
        title="新增角色"
        size="large"
        :destroy-on-close="true"
        :visible.sync="visible"
        :close-on-click-modal="false"
      >
        <el-form
          ref="submitFrom"
          :model="submitFrom.organizationRole"
          label-width="100"
          :rules="rules"
        >
          <el-row :gutter="32">
            <el-col :span="8">
              <!-- 上级流程角色 -->
              <el-form-item label="上级流程角色" prop="parentRoleName" label-width="100px">
                <!-- :preQueryData="{ 't.SRM_ORGNIZATION_ID': this.PFormData.srmOrgnizationId }" -->
                <QuickSearch
                  name="scc_pj_organization_role"
                  :scope-data="submitFrom"
                  show-key="nickname"
                  :show-input="submitFrom.organizationRole.parentRoleName"
                  :disabled="submitFromType == 'view'"
                  @close-quicksearch="(val,scope) => {
                    scope.organizationRole.parentRoleCode=val?.roleCode||''
                    scope.organizationRole.parentRoleName=val?.roleName||''
                  }"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <!-- 流程角色编码 -->
              <el-form-item label="流程角色编码" prop="roleCode" label-width="100px">
                <el-input
                  v-model="submitFrom.organizationRole.roleCode"
                  :disabled="submitFromType !== 'add'"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <!-- 流程角色 -->
              <el-form-item label="流程角色" prop="roleName" label-width="100px">
                <el-input v-model="submitFrom.organizationRole.roleName" :disabled="submitFromType == 'view'" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <p>
          <el-button
            type="primary"
            class="detail-pbtn"
            :disabled="submitFromType == 'view'"
            @click="submitFrom.organizationRoleUsers.push({userName:null,userNickName:null})"
          >
            {{ $t('common.new') }}
          </el-button>
        </p>
        <el-table
          ref="parentOrgTable"
          max-height="300px"
          border
          :data="submitFrom.organizationRoleUsers"
          :disabled="submitFromType == 'view'"
          tooltip-effect="dark"
          style="width: 100%"
        >
          <el-table-column type="selection" :disabled="submitFromType == 'view'" width="55" />
          <el-table-column
            key="userName"
            align="center"
            min-width="150"
            prop="userName"
            label="员工工号"
            show-overflow-tooltip
            _addStarToColumn
          >
            <template slot-scope="scope">
              <QuickSearch
                name="scc_rbac_user_display"
                :scope-data="scope.row"
                show-key="nickname"
                :show-input="scope.row.userName"
                :disabled="submitFromType == 'view'"
                @close-quicksearch="val => {
                  scope.row.userName=val?.username||''
                  scope.row.userNickName=val?.nickname||''
                  scope.row.srmUserId=val?.userId||''
                  scope.row.hrUserId= 0
                }"
              />
            </template>
          </el-table-column>
          <!-- 员工名称 -->
          <el-table-column prop="userNickName" label="员工名称" />
          <!-- 操作 -->
          <el-table-column
            align="center"
            prop="operation"
            :label="$t('common.operation')"
            width="100"
            fixed="right"
          >
            <template slot-scope="scope">
              <el-button type="text" :disabled="submitFromType == 'view'" @click="delRow(scope.$index)">
                {{ $t('common.delete') }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <div slot="footer" class="dialog-footer">
          <el-button @click="visible = false">
            {{ $t('common.cancel') }}
          </el-button>
          <el-button type="primary" @click="saveOrUpdate">
            {{ $t('common.confirm') }}
          </el-button>
        </div>
      </srm-dialog>
    </el-main>
  </el-container>
</template>

<script>
import ExportExcel from 'lib@/components/export-excel'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import QuickSearch from 'lib@/components/QuickSearch'
import { FormCollapseItem } from '@meicloud/render-pix'
import { organizationSetting } from 'modb@/basicSetting/api/basicSetting'

export default {
  name: 'OrgList',
  components: {
    TableView,
    MainHeader,
    MImport,
    FormWrapper,
    ExportExcel,
    QuickSearch
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      extraData: {
        fileModular: 'pj',
        fileFunction: 'processRole',
        fileType: 'excel'
      },
      queryForm: [], // 查询条件
      // 左边树形相关变量[[
      firstLoad: true,
      orgLoading: false,
      departmentLoading: false,
      // 部门树数据
      orgTreeData: [
        {
          childrens: [],
          organizationName: ''
        }
      ],
      // 部门树配置选项
      orgTreeProps: {
        children: 'childrens',
        label: 'organizationName',
        isLeaf: data => {
          return data.isLeaf
        }
      },
      tableHeader: [], // 表格列数据
      tableList: [],
      rules: {
        roleCode: [{ required: true, message: '必填' }],
        roleName: [{ required: true, message: '必填' }]
      },
      currentRows: [],
      queryParam: {},
      pageSize: 15,
      visible: false,
      submitFromType: null,
      PFormData: {
        srmOrgnizationId: null,
        hrOrgnizationId: 0
      },
      submitFrom: {
        organizationRole: {
          parentRoleCode: null,
          parentRoleName: null,
          srmOrgnizationId: null,
          hrOrgnizationId: 0,
          useFlag: 'Y',
          roleCode: null,
          roleName: null
        },
        organizationRoleUsers: []
      },
      gridId: 'list',
      searchFormConfig: [
        {
          prop: 'groupName',
          label: '组织/部门'
        },
        {
          prop: 'roleCode',
          label: '流程角色编码'
        },
        {
          prop: 'roleName',
          label: '流程角色名称'
        },
        {
          prop: 'parentRoleCode',
          label: '上级流程角色编码'
        },
        {
          prop: 'parentRoleName',
          label: '上级流程角色名称'
        },
        {
          prop: 'useFlag',
          label: '状态',
          type: 'dict',
          code: 'START_USING'
        }
      ]
    }
  },
  computed: {},
  created () {
    this.tableHeader = [
      {
        prop: 'groupName',
        label: () => this.$t('cusEntry.processRole.groupName'),
        minWidth: '160'
      },
      {
        prop: 'parentRoleCode',
        label: () => this.$t('cusEntry.processRole.parentRoleCode'),
        minWidth: '160'
      },
      {
        prop: 'parentRoleName',
        label: () => this.$t('cusEntry.processRole.parentRoleName'),
        minWidth: '160'
      },
      { prop: 'roleCode', label: () => this.$t('cusEntry.processRole.roleCode'), minWidth: '160' },
      { prop: 'roleName', label: () => this.$t('cusEntry.processRole.roleName'), minWidth: '160' },
      {
        prop: 'useFlag',
        label: () => '状态',
        minWidth: '160',
        formattor: val => this.$getDictLabel('START_USING', val)
      },
      {
        prop: 'operation',
        label: this.$t('bidMod.operation'),
        width: 120,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          // 编辑
          {
            // show: row => ['N'].includes(row.useFlag),
            formattor: () => this.$t('common.edit'),
            callback: row => this.getDialogdetail('edit', row)
          },
          // 删除
          {
            show: row => ['N'].includes(row.useFlag),
            formattor: () => this.$t('common.delete'),
            callback: row => this.deleteRow(row)
          }
          // // 查看
          // {
          //   show: row => ['Y'].includes(row.useFlag),
          //   formattor: () => '查看',
          //   callback: row => this.getDialogdetail('view', row)
          // }
        ]
      }
    ]
    // 查询列表页
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  mounted () {
    this.dragControllerDiv()
  },
  methods: {
    handleSuccess () {
      this.getQuerydata()
    },
    downloadTemplate (type) {
      downloadFileLink(
        '/api-pj/organizationRole/importExcelTemplate'
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    },
    getDialogdetail (type, row) {
      console.log('row:', row)
      this.$http({
        url: '/api-pj/organizationRole/getDetail',
        method: 'GET',
        params: {
          rowId: row.rowId
        },
        loading: true
      })
        .then(data => {
          console.log('data:', data)
          this.submitFrom = data.data
          this.submitFromType = type
          this.visible = true
        })
        .catch(err => {
          console.log(err)
        })
    },
    deleteRow (row) {
      this.$http({
        url: '/api-pj/organizationRole/deleteBatch',
        method: 'POST',
        data: [row.rowId],
        loading: true
      })
        .then(data => {
          this.$message.success(this.$t('common.success'))
          this.pushBpm('DELETE', [row])
          this.getQuerydata()
        })
        .catch(err => {
          console.log(err)
        })
    },
    nodeClick ({ organizationName, organizationId, organizationCode }) {
      if (this.PFormData.srmOrgnizationId !== organizationId) {
        this.PFormData.srmOrgnizationId = organizationId
        this.PFormData.groupName = organizationName
        this.PFormData.organizationCode = organizationCode
        this.getQuerydata({
          groupName: organizationName
        })
      }
    },
    delRow (index) {
      this.submitFrom.organizationRoleUsers.splice(index, 1)
    },
    openDialog (type) {
      const { srmOrgnizationId, hrOrgnizationId, groupName, organizationCode } = this.PFormData
      console.log('organizationCode:', organizationCode)
      // if (organizationCode && !organizationCode.startsWith("HR") ) {
      //   this.$message.error('该组织非HR数据，不允许新增角色')
      //   return
      // }
      if (!srmOrgnizationId) {
        this.$message.error('请先选择部门')
        return
      }
      this.clearSubmitData()
      this.submitFrom.organizationRole.srmOrgnizationId = srmOrgnizationId
      this.submitFrom.organizationRole.groupName = groupName
      this.submitFrom.organizationRole.useFlag = 'N'
      this.submitFromType = type
      this.visible = true
    },
    clearSubmitData () {
      this.submitFrom = {
        organizationRole: {
          parentRoleCode: null,
          parentRoleName: null,
          srmOrgnizationId: null,
          hrOrgnizationId: 0,
          useFlag: 'Y',
          roleCode: null,
          roleName: null
        },
        organizationRoleUsers: []
      }
    },
    // 异步树叶子节点懒加载逻辑
    loadNode (node, resolve) {
      // 一级节点处理
      if (node.level === 0) {
        const queryParma = { organizationId: -1 }
        // 查询一级节点
        this.getDepartmentTree(queryParma, resolve)
      } else if (node.level >= 1) {
        // 注意！把resolve传到你自己的异步中去
        const nodeParme = {}
        nodeParme.organizationId = node.data.organizationId
        this.getDepartmentTree(nodeParme, resolve)
      }
    },
    // 加载子节点
    getDepartmentTree (parmes, resolve) {
      if (this.firstLoad) {
        this.departmentLoading = true
      }
      this.getListChildrenData(parmes, false)
        .then(response => {
          if (response && response.data) {
            resolve(response.data)
          } else {
            this.$message({
              // '数据获取失败：'
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
    // 获取下一级节点
    async getListChildrenData (params, loading = true) {
      return this.$http({
        url: '/api-base/orgQuery/listChildrenOrganization',
        method: 'POST',
        params,
        loading
      })
    },
    operation (type) {
      if (this.currentRows.length === 0) {
        this.$message.error('请至少勾选一行数据')
        return
      }
      this.$http({
        url: `/api-pj/organizationRole/${type}`,
        method: 'POST',
        data: this.currentRows.map(item => item.rowId),
        loading: true
      })
        .then(data => {
          this.$message.success(this.$t('common.success'))
          this.pushBpm('ADD', this.currentRows)
          this.currentRows = []
          this.$refs[this.gridId].query()
        })
        .catch(err => {
          console.log(err)
        })
    },
    saveOrUpdate () {
      this.$http({
        url: '/api-pj/organizationRole/saveOrUpdate',
        method: 'POST',
        data: this.submitFrom,
        loading: true
      })
        .then(data => {
          this.$message.success(this.$t('common.success'))
          this.currentRows = []
          this.closeDialog()
          this.$refs[this.gridId].query()
        })
        .catch(err => {
          console.log(err)
        })
    },
    closeDialog () {
      this.visible = false
      this.clearSubmitData()
    },
    pushBpm (type, row) {
      if (row.length === 0) {
        this.$message.error('请至少勾选一行数据')
        return
      }
      this.$http({
        url: '/api-pj/organizationRole/pushBpm',
        method: 'POST',
        data: {
          ids: row.map(item => item.rowId),
          operation: type
        },
        loading: true
      })
        .then(data => {
          this.currentRows = []
          this.$refs[this.gridId].query()
        })
        .catch(err => {
          console.log(err)
        })
      // this.pushEas(type, row)
    },
    pushEas (type, row) {
      this.$http({
        url: '/api-pj/organizationRole/pushEas',
        method: 'POST',
        data: {
          ids: row.map(item => item.rowId)
        },
        loading: true
      })
        .then(data => {
          this.currentRows = []
          this.$refs[this.gridId].query()
        })
        .catch(err => {
          console.log(err)
        })
    },
    handleCheckChange (val) {
      this.currentRows = val
      // console.log("val", val);
    },
    getQuerydata (v = {}) {
      this.queryParam = v
      let self = this
      this.$nextTick(() => {
        self.$refs[self.gridId].query()
      })
    },

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
    }
  }
}
</script>

<style lang="scss" scoped>
.my-autocomplete {
  li {
    line-height: normal;
    padding: 7px;

    .name {
      text-overflow: ellipsis;
      overflow: hidden;
    }
    .addr {
      font-size: 12px;
      color: #b4b4b4;
    }

    .highlighted .addr {
      color: #ddd;
    }
  }
}
.the_follow_tender_dialog .el-row {
  margin-bottom: 41px;
  .el-col > span {
    padding-right: 11px;
  }
}
.cursor-text {
  color: #1890ff;
  margin-top: 5px;
  cursor: pointer;
}
.btn_line {
  margin: 0 0 10px 0;
}
.site-line {
  height: 250px;
  padding-bottom: 30px;
  :deep(.the_TableView) {
    padding-left: 0;
    padding-right: 0;
  }
}

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

.el-button.pSearchBtn {
  padding-left: 10px;
  padding-right: 10px;
}
</style>
<style>
.el-input.porgInput .el-input-group__append {
  padding-right: 15px !important;
}
.el-input.porgInput .el-input__suffix {
  z-index: 5;
}
.parent-search {
  position: relative;
}
.parent-search .el-input__inner {
  padding-right: 28px;
}
.parent-search .selectBtn {
  position: absolute;
  width: 24px;
  top: 1px;
  bottom: 1px;
  right: 1px;
  z-index: 100;
  line-height: 26px;
  padding: 0;
  cursor: pointer;
  min-width: 24px;
  border: 0;
  border-radius: 0 4px 4px 0;
}
</style>
