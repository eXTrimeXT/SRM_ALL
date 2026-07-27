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
      <!--搜索表单-->
      <FormWrapper
        ref="formWrapper"
        :form-array="queryForm"
        :p-form-data.sync="PFormData"
        @getFormData="getQuerydata"
      >
        <template #parentOrganizationNames="{ scope }">
          <div class="parent-search">
            <el-input
              ref="CPopSearch"
              v-model="scope.parentOrganizationNames"
              clearable
              :disabled="true"
            />
            <el-button
              icon="iconfont iconselect"
              class="selectBtn"
              @click="searchParentOrgList"
            />
          </div>
        </template>
      </FormWrapper>

      <!--按钮功能区-->
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <!-- 新增 -->
          <AuthorityButton
            code="base:organizationSetting:add"
            type="primary"
            @click="editOrgData('add')"
          >
            {{ $t('common.add') }}
          </AuthorityButton>

          <!-- 启用组织类型 -->
          <AuthorityButton
            plain
            code="base:organizationSetting:startOrgType"
            @click="orgTypeHandle"
          >
            {{ $t('dataConfMod.startOrgType') }}
          </AuthorityButton>
          <!-- 导入 -->
          <MImport
            ref="import"
            :title="iModal.title"
            :up-load-url="iModal.upLoadUrl"
            :extra-data="extraData"
            type="default"
            code="organizationSetting:import"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
          <ExportExcel
            page-url="/api-base/organization/organization/listAllOrganization"
            :filter-params="queryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            timeout="1000000"
            export-mode="front"
            type="default"
            code="organizationSetting:export"
          />
        </template>
      </MainHeader>

      <TableView
        :ref="gridId"
        :table-data="tableList"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        url="/api-base/organization/organization/listAllOrganization"
      />

      <!-- 选择上层组织弹框 -->
      <parentOrg
        :visible="parentOrgdialogVisible"
        :organization-id="null"
        @on-ok="comfirmSelect"
        @on-cancle="parentOrgdialogVisible = false"
      />
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
import parentOrg from './parentOrg'
import OrgType from './OrgType'
import deptMaintain from './deptMaintain'
import orgEdit from './orgEdit'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { organizationSetting } from 'modb@/basicSetting/api/basicSetting'

import { createDictClass } from '@/library/utils/dict/dict-utils'
const orgTypeDictClass = createDictClass().setCustomSelectType('ORG_TYPE_ALL').loadCustomSelectType('ORG_TYPE_ALL')

export default {
  name: 'OrgList',
  components: {
    TableView,
    MainHeader,
    MImport,
    FormWrapper,
    ExportExcel,
    parentOrg
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      // Excel导入
      iModal: {
        title: this.$t('components.eio.importTitle'),
        upLoadUrl: '/api-base/organization/file/importExcelOrganization'
      },
      // iModal: {
      //   title: this.$t('components.eio.importTitle'),
      //   upLoadUrl: '/api-base/organization/organization/importExcel'
      // },
      extraData: {
        fileModular: 'basicSetting',
        fileFunction: 'organizationSetting',
        fileType: 'excel'
      },

      gridId: 'OrgList',
      curOpt: 'add',
      pageSize: 15,
      queryParam: {},
      dictCodes: {}, // 导出字典
      orgTypeDictClass: orgTypeDictClass, // 组织类型字典
      PFormData: {
        parentOrganizationIds: ''
      },
      queryForm: [], // 查询条件
      tableHeader: [], // 表格列数据
      tableList: [],
      // 左边树形相关变量[[
      firstLoad: true,
      departmentLoading: false,
      orgLoading: false,
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
      // 左边树形相关变量]]
      parentOrgdialogVisible: false
    }
  },
  computed: {
  },
  created () {
    this.queryForm = [
      // '组织类型'
      {
        prop: 'organizationTypeCode',
        label: () => this.$t('dataConfMod.orgType'),
        type: 'custom-dict',
        code: 'ORG_TYPE_ALL',
        customSelectType: 'ORG_TYPE_ALL'
      },
      // '组织编码'
      {
        prop: 'organizationCode',
        label: () => this.$t('common.orgCode')
      },
      // '组织名称'
      {
        prop: 'organizationName',
        label: () => this.$t('dataConfMod.orgName')
      },
      // '上层组织名称'
      {
        prop: 'parentOrganizationNames',
        label: () => this.$t('dataConfMod.lastOrgName'),
        type: 'slot',
        slot: 'parentOrganizationNames'
      },
      {
        prop: 'parentOrganizationIds',
        label: () => this.$t('dataConfMod.lastOrgName'),
        hidden: true
      },
      // '是否有效'
      {
        prop: 'enabled',
        label: () => this.$t('dataConfMod.enabled'),
        type: 'select',
        options: [
          { label: this.$t('common.yes'), value: 'Y' },
          { label: this.$t('common.no'), value: 'N' }
        ]
      }
    ]
    this.tableHeader = [
      // '组织名称'
      {
        prop: 'organizationName',
        label: () => this.$t('dataConfMod.orgName'),
        minWidth: '160'
      },
      // '上层组织名称'
      {
        prop: 'parentOrganizationNames',
        label: () => this.$t('dataConfMod.lastOrgName'),
        minWidth: '160'
      },
      // '组织编码'
      {
        prop: 'organizationCode',
        label: () => this.$t('common.orgCode'),
        minWidth: '160'
      },
      {
        prop: 'organizationTypeName',
        label: this.$t('dataConfMod.orgTypeName'),
        minWidth: '160'
        // hidden: true
      },
      // '生效日期'
      {
        prop: 'startDate',
        label: () => this.$t('dataConfMod.startDate'),
        minWidth: '130'
      },
      // '失效日期'
      {
        prop: 'endDate',
        label: () => this.$t('dataConfMod.endDate'),
        minWidth: '130'
      },
      // '更新时间'
      {
        prop: 'lastUpdateDate',
        label: () => this.$t('common.updateTime'),
        minWidth: '130'
      },
      // '更新人'
      {
        prop: 'lastUpdatedUserName',
        label: () => this.$t('common.updatePeople'),
        minWidth: '100'
      },
      // '操作'
      {
        prop: 'operation',
        label: () => this.$t('common.operation'),
        width: '150',
        fixed: 'right',
        editType: 'none',
        showType: 'buttons',
        btnStyle: 'text',
        buttons: [
          // 编辑
          {
            code: 'base:organizationSetting:editOrgData',
            callback: row => this.editOrgData('edit', row),
            formattor: () => this.$t('common.edit')
          },
          // 编辑部门
          {
            code: 'base:organizationSetting:editDeptData',
            callback: row => this.editDeptData(row),
            formattor: () => this.$t('dataConfMod.editOrg'),
            show: row => row.organizationTypeCode === 'COMPANY'
          }
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
    // 重新查询数据
    updataPageData () {
      this.getQuerydata() // 查询列表数据
      this.loasFirstNode() // 重新查询树形一级数据
    },
    // 左侧树形节点点击
    nodeClick ({ organizationName, organizationId }) {
      if (this.PFormData.parentOrganizationNames !== organizationName) {
        // this.PFormData.parentOrganizationNames = organizationName
        this.$refs.formWrapper.setValue('parentOrganizationNames', organizationName)
        this.$refs.formWrapper.setValue('parentOrganizationIds', organizationId)
        this.getQuerydata({
          ...this.queryParam,
          parentOrganizationIds: organizationId,
          parentOrganizationNames: organizationName
        })
      }
    },
    getQuerydata (v = {}) {
      this.queryParam = v
      let self = this
      this.$nextTick(() => {
        self.$refs[self.gridId].query()
      })
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
      organizationSetting.getListChildrenData(parmes, false)
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
    // 加载一级节点
    loasFirstNode () {
      this.orgTreeData = []
      const queryParma = { relId: -1, organizationId: -1 }
      organizationSetting.getListChildrenData(queryParma, false)
        .then(response => {
          if (response && response.data) {
            const resData = response.data
            resData.forEach((item, index) => {
              this.orgTreeData.push(item)
              this.orgTreeData[index].childrens = []
            })
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
    // 启用组织管理页面
    orgTypeHandle () {
      // 打开tab页面--',
      this.$emit('tab-add', {
        component: OrgType,
        params: { flag: 'add' },
        title: () => this.$t('dataConfMod.startOrgType'), // '启用组织类型'
        name: 'orgType'
      })
    },

    // 列表页查询上层组织
    searchParentOrgList () {
      this.parentOrgdialogVisible = true
    },

    // 父级组件 确认选择
    comfirmSelect (data) {
      if (data != null) {
        let selectRow = data[0]
        let pName = selectRow.organizationName
        let pIds = selectRow.organizationId
        this.$refs.formWrapper.setValue('parentOrganizationNames', pName) // 名字赋值
        this.$refs.formWrapper.setValue('parentOrganizationIds', pIds) // id赋值
      } else {
        this.$refs.formWrapper.setValue('parentOrganizationNames', '') // 名字赋值
        this.$refs.formWrapper.setValue('parentOrganizationIds', '') // id赋值
      }
      this.parentOrgdialogVisible = false
    },

    // 编辑部门数据
    editDeptData (row) {
      // 打开tab页面--',
      // message: 请先维护公司名称和组织名称
      if (row.organizationTypeCode !== 'COMPANY' || !row.organizationId) {
        this.$message({
          type: 'error',
          message: this.$t('dataConfMod.msgCompanyNameAndOrg')
        })
      } else {
        this.$emit('tab-add', {
          component: deptMaintain,
          ctrlHeight: true,
          params: {
            flag: 'edit',
            organizationId: row.organizationId,
            organizationName: row.organizationName,
            organizationCode: row.organizationCode
          },
          // 编辑部门信息
          title: () => row.organizationName,
          name: 'deptMaintain' + row.organizationId
        })
      }
    },

    // 编辑组织数据
    async editOrgData (type, row) {
      if (type == 'edit') {
        this.orgRowInfo = row
        this.$emit('tab-add', {
          component: orgEdit,
          ctrlHeight: true,
          params: {
            flag: 'edit',
            organizationId: row.organizationId,
            organizationName: row.organizationName,
            organizationCode: row.organizationCode,
            tabName: 'orgEdit' + row.organizationId
          },
          // 编辑部门信息
          title: () => this.$t('common.edit') + '-' + row.organizationName,
          name: 'orgEdit' + row.organizationId
        })
      } else {
        this.$emit('tab-add', {
          component: orgEdit,
          ctrlHeight: true,
          params: {
            flag: 'add',
            tabName: 'orgEdit'
          },
          title: () => this.$t('dataConfMod.addOrg') // 新增组织
        })
      }
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
    },
    // 下载导入模板
    downloadTemplate () {
      downloadFileLink(
        '/api-file/files-anon/file/fileupload/downloadTemplate/ORGANIZATION_IMPORT',
        this.$t('dataConfMod.orgImport') + '.xlsx' // 组织导入模板
      ).catch(() => {
        this.$message.error(this.$t('perfMod.downLoadError')) // "下载失败"
      })
    },
    // downloadTemplate () {
    //   downloadFileLink(
    //     '/api-base/organization/organization/importExcelTemplate',
    //     '组织设置模板.xlsx'
    //   ).catch(() => {
    //     // 下载失败
    //     this.$message.error(this.$t('components.eio.downloadFail'))
    //   })
    // },
    handleSuccess () {
      this.getQuerydata()
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

.el-button.pSearchBtn{
  padding-left: 10px ;
  padding-right: 10px;
}

</style>
<style>
.el-input.porgInput .el-input-group__append{
  padding-right: 15px !important;
}
.el-input.porgInput .el-input__suffix{
  z-index: 5;
}
.parent-search{
  position: relative;
}
.parent-search .el-input__inner{
  padding-right: 28px;
}
.parent-search .selectBtn{
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
