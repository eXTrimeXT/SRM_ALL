<template>
  <el-container class="secondary-authentication-config-wrapper" direction="vertical">
    <el-main class="deepClass">
      <FormWrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
        @reset="queryReset"
      >
        <template #permissionCode="{ scope }">
          <el-select
            v-model="scope.permissionCode"
            filterable
            clearable
            @change="permissionChange"
          >
            <el-option
              v-for="item in permissionList"
              :key="item.value"
              :value="item.value"
              :label="item.label"
            />
          </el-select>
        </template>
        <template #buttonPermissionCode="{ scope }">
          <el-select
            v-model="scope.buttonPermissionCode"
            clearable
            filterable
          >
            <el-option
              v-for="item in buttonPermissionList"
              :key="item.value"
              :value="item.value"
              :label="item.label"
            />
          </el-select>
        </template>
      </FormWrapper>
      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton
            type="primary"
            @click="editConfig('add')"
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
        :adeptMeiQl="true"
        :comActive="$attrs['changeTab']"
        url="/api-rbac/api-ql/TwoAuthBuyer/query"
      />
      <EditDialog
        :visible="dialogFormVisible"
        :opration="configOpration"
        :twoAuthInfo="twoAuthInfo"
        @on-cancle="saveSuccessFn"
        @on-success="saveSuccessFn"
      />
    </el-main>
  </el-container>
</template>

<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import EditDialog from './editDialog'
import { parseTime } from '@/utils'
import { securitySettingsApi } from 'modb@/accountAccess/api'
import { transformMQL } from '@/library/utils/util'
import { rest } from 'lodash'

export default {
  name: 'SecondaryAuthenticationConfig',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    EditDialog
  },
  data () {
    return {
      gridId: 'roleMananger',
      dialogFormVisible: false,
      pageSize: 15,
      tableData: [],
      tableHeader: [],
      queryParam: {},
      queryObj: {},
      configOpration: 'add',
      twoAuthInfo: {},
      permissionList: [], // 场景下拉
      buttonPermissionList: [], // 操作类型下拉
      preArr: [
        {
          prop: 'twoAuthNum',
          label: () => this.$t('secondaryAuthentication.twoAuthNum')
        },
        {
          prop: 'permissionCode',
          label: () => this.$t('secondaryAuthentication.permissionCode'),
          type: 'slot',
          slot: 'permissionCode'
        }, // "业务场景"
        {
          prop: 'buttonPermissionCode',
          label: () => this.$t('secondaryAuthentication.buttonPermissionCode'),
          type: 'slot',
          slot: 'buttonPermissionCode'
        }, // "认证操作"
        {
          prop: 'authStatus',
          label: () => this.$t('secondaryAuthentication.authStatus'),
          type: 'dict',
          code: 'TOW_FACTOR_AUTH_STATUS' // DRAFT VALID INVALID
        },
        {
          prop: 'authType',
          label: () => this.$t('secondaryAuthentication.authType'), // '认证方式'
          multiple: true,
          collapseTags: true,
          type: 'select',
          options:  [
            { value: 'emailVerify', label: this.$t('secondaryAuthentication.emailVerify')}, // '邮箱认证'
            { value: 'smsVerify', label: this.$t('secondaryAuthentication.smsVerify')}, // '账密认证'
            { value: 'accountPwd', label: this.$t('secondaryAuthentication.accountPwd')} // '短信认证'
          ]
        }
      ]
    }
  },
  async created () {
    await this.getPermissionList()
    var _this = this
    this.tableHeader = [
      {
        prop: 'twoAuthNum',
        label: () => this.$t('secondaryAuthentication.twoAuthNum'),
        width: 130,
        btnStyle: 'text',
        showType: 'button',
        callback: function (row) {
          this.editConfig('view', row)
        }.bind(this),
      },
      {
        prop: 'authStatus',
        label: () => this.$t('secondaryAuthentication.authStatus'), // "状态"
        dataType: 'dict',
        code: 'TOW_FACTOR_AUTH_STATUS'
      },
      {
        prop: 'permissionName',
        label: () => this.$t('secondaryAuthentication.permissionCode'),
        width: 150
      },
      {
        prop: 'buttonPermissionName',
        label: () => this.$t('secondaryAuthentication.buttonPermissionCode'),
        width: 150
      }, // "认证操作"
      {
        prop: 'authType',
        label: () => this.$t('secondaryAuthentication.authType'), // "认证方式"
        minWidth: 200,
        formattor: (scope, row) => {
          let emailVerify = row.emailVerify == 'Y' ? this.$t('secondaryAuthentication.emailVerify') : ''  // '邮箱认证'
          let smsVerify = row.smsVerify == 'Y' ? this.$t('secondaryAuthentication.smsVerify') : '' // '短信认证'
          let accountPwd = row.accountPwd == 'Y' ? this.$t('secondaryAuthentication.accountPwd') : '' // '账密认证'
          let textArr = [emailVerify, smsVerify, accountPwd].filter(i => i !='')
          return textArr.toString()
        }
      },
      {
        prop: 'createdFullName',
        label: () => this.$t('common.creator'),
        minWidth: 120
      },
      {
        prop: 'creationDate',
        label: () => this.$t('common.creationTime'),
        minWidth: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'lastUpdatedFullName',
        label: () => this.$t('common.updatePeople'),
        minWidth: 120
      },
      {
        prop: 'lastUpdateDate',
        label: () => this.$t('common.updateTime'),
        minWidth: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'), // "操作"
        width: 150,
        btnStyle: 'text',
        fixed: 'right',
        showType: 'buttons',
        buttons: [
          // 编辑
          {
            callback: function (row) {
              this.editConfig('edit', row)
            }.bind(this),
            code: 'securitySettings:securityAuthEdit',
            formattor (val) {
              return _this.$t('common.edit')
            },
            show: row => ['DRAFT','INVALID'].includes(row.authStatus)
          },
          // 删除
          {
            callback: function (row) {
              this.deleteRow(row, true)
            }.bind(this),
            code: 'securitySettings:securityAuthDelete',
            popconfirm: {
              title: 'common.confirmDeleteRow'
            },
            formattor (val) {
              return _this.$t('common.delete')
            },
            show: row => ['DRAFT','INVALID'].includes(row.authStatus)
          },
          // 生效
          {
            callback: function (row) {
              this.effectOrFailureRow(row, 'effect')
            }.bind(this),
            code: 'securitySettings:securityAuthEffect',
            formattor (val) {
              return _this.$t('common.active')
            },
            show: row => ['INVALID'].includes(row.authStatus)
          },
          // 失效
          {
            callback: function (row) {
              this.effectOrFailureRow(row, 'invalid')
            }.bind(this),
            code: 'securitySettings:securityAuthInEffect',
            formattor (val) {
              return _this.$t('common.inactive')
            },
            show: row => ['VALID'].includes(row.authStatus)
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    // 获取业务场景下拉
    async getPermissionList () {
      let permissionRes = await this.$http({
        url: '/api-rbac/buyer/permission/listLeafMenu',
        method: 'POST',
        data: {}
      })
      if (permissionRes) {
        this.permissionList = (permissionRes.data || []).map(i => ({
          permissionCode: i.permissionCode,
          value: (i.functionId).toString(),
          label: i.permissionName
        }))
      }
    },
    // 业务场景切换
    async permissionChange (val) {
      if (val) {
        let functionId = val
        let buttonRes = await this.$http({
          url: '/api-rbac/buyer/permission/listPermissionByParam',
          method: 'POST',
          data: { functionId, permissionType: 'BUTTON' }
        })
        if (buttonRes) {
          this.buttonPermissionList = (buttonRes.data || []).map(i => ({
            permissionCode: i.permissionCode,
            value: (i.permissionId).toString(),
            label: i.permissionName
          }))
        }
      } else {
        this.buttonPermissionList = []
      }
    },
    queryReset () {
      this.buttonPermissionList = []
    },
    getQuerydata (params, isReset = false) {
      let paramData = {}
      let filterRq = {}
      let filterOr = {}
      if (isReset) {
        let { authType = [], ...rest } = params
        let authTypeObj = {}
        // 认证类型处理
        if (params.authType && params.authType.length > 0) {
          params.authType.forEach(key => {
            authTypeObj[key] = 'Y'
          })
        }
        paramData = {
          ...authTypeObj,
          ...rest
        }

        for (let key in rest) {
          if (rest[key]) {
            filterRq[key] = { contains: rest[key].toString() }
          }
        }
        for (let key in authTypeObj) {
          if (authTypeObj[key]) {
            filterOr[key] = { contains: authTypeObj[key] }
          }
        }
      }
      let filter = {
        '$and':{
          ...filterRq,
          '$or': {
            ...filterOr
          }
        }
      }
      this.queryParam = transformMQL.listGetData('TwoAuthBuyer', paramData, 'lastUpdateDate', undefined, 'query', filter, { emailVerify: 'contains', smsVerify: 'contains', accountPwd: 'contains', authStatus: 'eq' })
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 新增 编辑
    editConfig (type, row) {
      if (type == 'add') {
        this.configOpration = type
        this.twoAuthInfo = {
          twoAuthId: '',
          permissionCode: ''
        }
      } else {
        this.configOpration = type
        this.twoAuthInfo = {
          twoAuthId: row.twoAuthId,
          permissionCode: row.permissionCode
        }
      }
      this.dialogFormVisible = true
    },
    // 删除
    async deleteRow (row) {
      let twoAuthId = row.twoAuthId
      let transformParams = transformMQL.save('TwoAuthBuyer', [twoAuthId], 'delete')
      let res = await this.$http({
        url: '/api-rbac/api-ql/TwoAuthBuyer/delete',
        method: 'POST',
        data: transformParams
      })
      if (res) {
        this.$message.success(this.$t('common.success'))
        this.$refs[this.gridId].query()
      }
    },
    // 生效 失效
    async effectOrFailureRow (row, type) {
      let twoAuthId = row.twoAuthId
      let transformParams = transformMQL.save('TwoAuthBuyer', [twoAuthId], type)
      let res = await this.$http({
        url: `/api-rbac/api-ql/TwoAuthBuyer/${type}`,
        method: 'POST',
        data: transformParams
      })
      if (res) {
        this.$message.success(this.$t('common.success'))
        this.$refs[this.gridId].query()
      }
    },
    saveSuccessFn (optType, visible) {
      if (['submit'].includes(optType)) {
        this.dialogFormVisible = false
        this.$refs[this.gridId].query()
      } else {
        if (optType=='cancle') {
          this.dialogFormVisible = false
        }
      }
    }
  }
}
</script>

<style lang="scss">
.secondary-authentication-config-wrapper{
  padding: 16px !important;
}
</style>
