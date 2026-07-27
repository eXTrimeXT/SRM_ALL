<template>
  <el-container class="flex-container-notab the_functionMaintenance_wrapper" direction="vertical">
    <el-main class="deepClass">
      <FormWrapper :form-array="preArr" @getFormData="getQuerydata" />
      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton
            type="primary"
            code="rbac:roleMaintenance:add"
            @click="addOne"
          >
            {{ $t("common.add") }}
          </AuthorityButton>
          <!-- 导入角色 -->
          <MImport
            ref="import1"
            :title="$t('dataConfMod.importRole')"
            up-load-url="/api-rbac/ext/role/role/importExcel"
            type="default"
            code="roleMaintenance:import"
            :extra-data="extraData"
            @downloadTemplate="downloadTemplate2"
            @handleSuccess="handleSuccess"
          />
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
        url="/api-rbac/role/role/listPage"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime } from '@/utils'
import roleEdit from './edit'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'

export default {
  name: 'RoleMaintenanceList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    MImport
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      extraData: {
        fileModular: 'base',
        fileFunction: 'accountAccess',
        fileType: 'excel'
      },
      gridId: 'roleMananger',
      curOpt: 'add',
      pageSize: 15,
      roleTitle: this.$t('dataConfMod.addRole'), // "新增角色"
      tableData: [],
      tableHeader: [],
      queryParam: {},
      preArr: [
        { prop: 'roleCode', label: () => this.$t('dataConfMod.roleCode') }, // "角色编码"
        { prop: 'roleName', label: () => this.$t('dataConfMod.roleName') }, // "角色名称"
        {
          prop: 'startDate',
          label: () => this.$t('dataConfMod.startDate'),
          type: 'date'
        } // "生效日期"
      ],
      langList: [],
      dialogFormVisible: false,
      ROLE_TYPE: {}
    }
  },
  watch: {
    filterText (val) {
      this.$refs.roleSelectTree.filter(val)
    },
    filterTextSecond (val) {
      this.$refs.roleSelectTree2.filter(val)
    }
  },
  created () {
    var _this = this
    this.tableHeader = [
      {
        prop: 'roleCode',
        label: () => this.$t('dataConfMod.roleCode'),
        width: 150
      }, // "角色编码"
      {
        prop: 'roleName',
        label: () => this.$t('dataConfMod.roleName'),
        width: 150
      }, // "角色名称"
      {
        prop: 'roleType',
        label: () => this.$t('dataConfMod.roleType'), // "角色类型"
        formattor: val =>
          val
            .split(',')
            .map(i => this.$getDictLabel('ROLE_TYPE', i))
            .join(',')
      },
      {
        prop: 'startDate',
        label: () => this.$t('dataConfMod.startDate'), // "生效日期"
        width: 150,
        editType: 'none',
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'endDate',
        label: () => this.$t('dataConfMod.endDate'), // "失效日期"
        editType: 'none',
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'), // "操作"
        width: 150,
        btnStyle: 'text',
        fixed: 'right',
        showType: 'buttons',
        buttons: [
          {
            callback: function (row) {
              this.editOne(row)
            }.bind(this),
            code: 'rbac:roleMaintenance:edit',
            formattor (val) {
              return _this.$t('common.edit') // "编辑"
            }
          },
          {
            callback: function (row) {
              this.editOne(row, true)
            }.bind(this),
            code: 'rbac:roleMaintenance:add',
            formattor (val) {
              return _this.$t('common.copy') // "复制"
            }
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  activated () {
    this.$refs[this.gridId].doLayout()
  },
  updated () {
    const el = document.querySelectorAll(
      '.el-form-item__label[for="roleLanguages"]'
    )[0]
    if (el && el.className.indexOf(' roleLanguages-required') === -1) {
      el.className = `${el.className} roleLanguages-required`
    }
  },
  methods: {
    handleSuccess () {
      this.getQuerydata()
    },
    downloadTemplate2 () {
      // 用户-组织权限导入模板.xlsx
      downloadFileLink(
        '/api-rbac/ext/role/role/exportExcelTemplate',
        parseTime(new Date()) + this.$t('dataConfMod.userOrgAccessImpTemXLSX')
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    },
    handleCheckChange () { },
    getQuerydata (v) {
      this.queryParam = v
      // if (v && v.startDate) this.queryParam.startDate = new Date(v.startDate).getTime();
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 新增
    addOne () {
      this.curOpt = 'add'
      let tab = {
        component: roleEdit,
        ctrlHeight: true,
        params: {
          flag: this.curOpt,
          tabName: 'roleMaintenanceEdit'
        },
        title: this.$t('dataConfMod.addRole'),
        name: 'roleMaintenanceEdit'
      }
      this.$emit('tab-add', tab)
    },
    // 编辑
    editOne (row, copy = false) {
      let name = null
      if (copy == true) {
        this.curOpt = 'copy'
        name = this.$t('dataConfMod.copyRole') + row.roleName // 复制角色
      } else {
        this.curOpt = 'edit'
        name = this.$t('dataConfMod.editRole') + row.roleName // "编辑角色"
      }
      let tab = {
        component: roleEdit,
        ctrlHeight: true,
        params: {
          flag: this.curOpt,
          row,
          tabName: 'roleMaintenanceEdit' + row.roleCode + this.curOpt
        },
        title: name,
        name: 'roleMaintenanceEdit' + row.roleCode + this.curOpt
      }
      this.$emit('tab-add', tab)
    },
    addLanguage () {
      this.roleLanguages.push({
        language: 'zh_CN',
        roleName: ''
      })
    },
    removeLanguage (item, index) {
      this.roleLanguages.splice(index, 1)
    }
  }
}
</script>
<style scoped lang="scss">
::v-deep .el-input-group__prepend{
  background-color: #FFFFFF;
}
::v-deep .el-tabs__content{
  padding: 16px;
  border: 1px solid #cccccc;
}
::v-deep .el-tabs__header{
  margin: 0;
}
.removeLanguage {
  margin: -10px 0 !important;
}
.the_functionMaintenance_wrapper {
  .menuAccessCtrl {
    border: 1px solid #dfe6ec;
  }
}
.form-roleContainer {
  .main-header {
    background-color: #f2f2f2;
  }
  .roleNames {
    .el-row .el-form-item:first-child {
      margin-bottom: 10px !important;
    }
    .el-col {
      padding-left: 5px !important;
      padding-right: 5px !important;
    }
  }
}
.menuAccessCtrl ::v-deep {
  .el-tree {
    overflow-y: auto;
    max-height: 222px;
  }
}
.form-roleContainer .el-row.roleNames .el-form-item:first-child {
  margin-bottom: 10px !important;
}
.roleLanguages-required:before {
  content: '*';
  color: #ff4949;
  margin-right: 4px;
}
.borderStyle {
  border: 1px solid #ebebeb;
  border-top: 0;
  border-bottom: 0;
}
.buttonAddAndReduce{
  color: #96999C;
  font-size: 18px;
  margin: 5px 0 0 12px;
  cursor: pointer;
}
</style>
