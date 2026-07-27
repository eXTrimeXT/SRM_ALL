<template>
  <el-container class="flex-container the_usersAccessList_wrapper" direction="vertical">
    <el-main>
      <FormWrapper :form-array="preArr" @getFormData="getQuerydata" />

      <MainHeader :l-span="23" :r-span="1">
        <template slot="left">
          <!-- 新增用户 -->
          <AuthorityButton
            type="primary"
            code="rbac:usersAccess:add"
            @click="editTab('add')"
          >
            {{ $t("dataConfMod.addUser") }}
          </AuthorityButton>
          <!-- 导入采购商角色组织权限 -->
          <MImport
            ref="import1"
            :title="$t('dataConfMod.importUserRoleAllExcel')"
            up-load-url="/api-rbac/user/import/importAllExcel"
            :extra-data="extraData"
            code="usersAccessUser:import"
            type="default"
            @downloadTemplate="downloadTemplate3"
            @handleSuccess="handleSuccess"
          />
          <ExportExcel
            page-url="/api-rbac/user/listByBuyer"
            export-mode="front"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            :filter-params="queryParams"
            :title="$t('components.eio.customExport')"
            :fileName="$t('dataConfMod.usersAccessExport')"
            code="usersAccessUser:export"
            type="default"
          />
        </template>
      </MainHeader>

      <TableView
        :ref="gridId"
        :pre-query-data="queryParams"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        :com-active="$attrs['changeTab']"
        url="/api-rbac/user/listByBuyer"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime } from '@/utils'
import usersAccessInfo from './usersAccessInfo'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import ExportExcel from 'lib@/components/export-excel'

export default {
  name: 'UsersAccessList',
  components: {
    TableView,
    MainHeader,
    MImport,
    FormWrapper,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      queryParams: {},
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      tableHeader: [],
      tableData: [],
      dictCodes: {
        userType: 'USER_TYPE'
      },
      extraData: {
        fileModular: 'base',
        fileFunction: 'accountAccess',
        fileType: 'excel'
      },
      form: {
        userID: '',
        userName: '',
        telephone: '',
        email: '',
        BU: '',
        userIDType: '',
        password: '',
        passwordConfirm: '',
        enableDate: '',
        disableDate: ''
      },
      userIDList: [
        { label: 'v1', value: this.$t('dataConfMod.flow') + '1' }, // "流程1"
        { label: 'v2', value: this.$t('dataConfMod.flow') + '2' } // "流程2"
      ],
      rules: {
        userID: [{ required: true, message: this.$t('dataConfMod.msgUser') }], // "请输入子账号"
        userName: [
          { required: true, message: this.$t('dataConfMod.msgUserName') }
        ], // "请输入姓名"
        telephone: [
          { required: true, message: this.$t('dataConfMod.msgPhone') }
        ], // "请输入手机"
        email: [{ required: true, message: this.$t('dataConfMod.msgMail') }], // "请输入邮箱"
        BU: [{ required: true, message: this.$t('dataConfMod.msgBU') }], // "请输入部门"
        userIDType: [
          { required: true, message: this.$t('dataConfMod.msgUserType') }
        ], // "请输入账号类型"
        password: [
          { required: true, message: this.$t('dataConfMod.msgPassword') }
        ], // "请输入密码"
        passwordConfirm: [
          { required: true, message: this.$t('dataConfMod.msgPasswordConfirm') }
        ], // "请输入确认密码"
        enableDate: [
          { required: true, message: this.$t('dataConfMod.msgStartDate') }
        ], // "请输入生效日期"
        disableDate: [
          { required: true, message: this.$t('dataConfMod.msgEndDate') }
        ] // "请输入失效日期"
      },
      dialogFormVisible: false,
      formLabelWidth: '100px',
      isActive: false,
      preArr: [
        { prop: 'username', label: () => this.$t('dataConfMod.userID') }, // "账号"
        { prop: 'ceeaEmpNo', label: () => this.$t('dataConfMod.jobNum') }, // "工号"
        { prop: 'nickname', label: () => this.$t('dataConfMod.userName') }, // "姓名"
        {
          prop: 'department',
          label: () => this.$t('dataConfMod.department'),
          type: 'quicksearch',
          showKey: 'descr',
          name: 'ceea_base_dept'
        },
        {
          prop: 'userType',
          label: () => this.$t('dataConfMod.userType'), // "账号类型"
          type: 'dict', // 字典类型
          code: 'USER_TYPE'

        },
        {
          prop: 'startDate',
          label: () => this.$t('vendorMod.startDate'),
          type: 'date'
        }, // "生效日期"
        {
          prop: 'endDate',
          label: () => this.$t('dataConfMod.endDate'),
          type: 'date'
        } // "失效日期"
      ]
    }
  },
  created () {
    var _this = this
    this.tableHeader = [
      {
        prop: 'userType',
        width: 120,
        align: 'center',
        label: () => this.$t('dataConfMod.userType'), // "账号类型"
        dataType: 'dict',
        code: 'USER_TYPE'
      },
      {
        prop: 'username',
        label: () => this.$t('dataConfMod.userID'),
        align: 'center'
        // width: 150
      }, // "账号"
      {
        prop: 'ceeaEmpNo',
        label: () => this.$t('dataConfMod.jobNum'),
        align: 'center'
      }, // 工号
      {
        prop: 'nickname',
        label: () => this.$t('dataConfMod.userName'),
        width: 150,
        align: 'center'
      }, // "姓名"
      {
        prop: 'phone',
        label: () => this.$t('dataConfMod.phone'),
        width: 150,
        align: 'center'
      }, // "手机"
      {
        prop: 'email',
        label: () => this.$t('dataConfMod.email'),
        minWidth: 150,
        align: 'center'
      }, // "邮箱"
      {
        prop: 'department',
        label: () => this.$t('dataConfMod.department'),
        minWidth: 150,
        align: 'center'
      }, // "部门"
      {
        prop: 'startDate',
        align: 'center',
        label: () => this.$t('vendorMod.startDate'), // "生效日期"
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'endDate',
        align: 'center',
        label: () => this.$t('dataConfMod.endDate'), // "失效日期"
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'operation',
        align: 'center',
        label: () => this.$t('common.operation'), // "操作"
        width: 70,
        fixed: 'right',
        showType: 'button',
        btnStyle: 'text',
        code: 'rbac:usersAccess:edit',
        callback: function (row) {
          this.editTab('edit', row)
        }.bind(this),
        formattor (val) {
          return _this.$t('common.edit') // "编辑"
        }
      }
    ]
  },
  mounted () {
    this.$refs[this.gridId].query()
  },
  methods: {
    handleSuccess () {
      this.getQuerydata()
    },
    // downloadTemplate1 () {
    //   // 用户导入模板.xlsx
    //   downloadFileLink(
    //     '/api-rbac/user/import/importUserModelDownload',
    //     parseTime(new Date()) + this.$t('dataConfMod.userImportTempXLSX')
    //   ).catch(() => {
    //     this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
    //   })
    // },
    // downloadTemplate2 () {
    //   // 用户-组织权限导入模板.xlsx
    //   downloadFileLink(
    //     '/api-rbac/user/import/importRoleModelDownload',
    //     parseTime(new Date()) + this.$t('dataConfMod.userOrgAccessImpTemXLSX')
    //   ).catch(() => {
    //     this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
    //   })
    // },
    downloadTemplate3 () {
      // 用户角色权限导入模板.xlsx
      downloadFileLink(
        '/api-file/files-anon/file/fileupload/downloadTemplate/BUYER_USER_IMPORT',
        this.$t('dataConfMod.userRoleAccessImpTemXLSX')
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    },
    getQuerydata (v) {
      this.queryParams = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 编辑tab
    editTab (type, row = {}) {
      let tab = {}
      if (type === 'add') {
        // 新增
        tab = {
          component: usersAccessInfo,
          ctrlHeight: true,
          params: { flag: 'add' },
          title: () => this.$t('dataConfMod.userEdit'),
          name: 'usersAccessInfo'
        }
      } else {
        // 修改
        tab = {
          component: usersAccessInfo,
          ctrlHeight: true,
          params: {
            flag: 'edit',
            row: row
          },
          title: () => this.$t('common.edit') + `-${row.username}`,
          name: 'usersAccessInfo' + row.username
        }
      }
      this.$emit('tab-add', tab)
    }
  }
}
</script>
