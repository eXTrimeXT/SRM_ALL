<template>
  <el-container class="todo-config-wrapper" direction="vertical">
    <el-main class="deepClass">
      <FormWrapper :form-array="preArr" @getFormData="getQuerydata" />
      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton
            type="primary"
            code="securitySettings:unlock"
            @click="unlockFn"
          >
            {{ $t('secondaryAuthentication.unlock') }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :page-size="pageSize"
        :checkbox="true"
        :adeptMeiQl="true"
        :checkChange="checkChangeChange"
        :comActive="$attrs['changeTab']"
        url="/api-rbac/api-ql/UserLockBuyer/query"
      />
    </el-main>
  </el-container>
</template>

<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime } from '@/utils'
import { securitySettingsApi } from 'modb@/accountAccess/api'
import { transformMQL } from '@/library/utils/util'

export default {
  name: 'Todo',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  data () {
    return {
      gridId: 'roleMananger',
      pageSize: 15,
      tableData: [],
      currentRows: [],
      tableHeader: [],
      queryParam: {},
      preArr: [
        { prop: 'username', label: () => this.$t('secondaryAuthentication.username') } // "账号"
      ]
    }
  },
  created () {
    var _this = this
    this.tableHeader = [
      {
        prop: 'lockType',
        label: () => this.$t('secondaryAuthentication.lockType'), // '类型',
        minWidth: 150,
        dataType: 'dict',
        code: 'USER_LOCK_TYPE'
      }, // "类型" 
      {
        prop: 'lockDesc',
        label: () => this.$t('secondaryAuthentication.lockDesc'), // 描述
        minWidth: 150
      },
      // {
      //   prop: 'lockTime',
      //   label: this.$t('secondaryAuthentication.lockTime'), // "锁定开始时间"
      //   minWidth: 150
      // },
      {
        prop: 'lockExpireTime',
        label: () => this.$t('secondaryAuthentication.lockExpireTime'), // "锁定到期时间"
        minWidth: 150,
        // editType: 'none',
        // formattor (val) {
        //   return val ? parseTime(val, '{y}-{m}-{d}') : ''
        // }
      },
      {
        prop: 'username',
        minWidth: 100,
        label: () => this.$t('secondaryAuthentication.username') // '账号'
      }
      // {
      //   prop: 'operation',
      //   label: () => this.$t('common.operation'), // "操作"
      //   width: 150,
      //   btnStyle: 'text',
      //   fixed: 'right',
      //   showType: 'buttons',
      //   buttons: [
      //     {
      //       callback: function (row) {
      //         this.editOne(row)
      //       }.bind(this),
      //       code: 'rbac:roleMaintenance:edit',
      //       formattor (val) {
      //         return _this.$t('common.edit') // "编辑"
      //       }
      //     }
      //   ]
      // }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    getQuerydata (params, isReset = false) {
      let paramData = {}
      if (isReset) {
        paramData = params
      }
      this.queryParam = transformMQL.listGetData('UserLockBuyer', { ...paramData, unlockType: 'NO' }, 'lastUpdateDate', undefined, 'query', undefined, { unlockType: 'eq' })
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 多选删除
    checkChangeChange (rows) {
      this.currentRows = rows
    },
    // 解锁
    async unlockFn () {
      if (this.currentRows.length == 0) {
        return this.$message.error(this.$t('secondaryAuthentication.unlockUsernameTip')) // '请选择需要解锁的账号'
      } else {
        let submitData = this.currentRows.map(i => (i.userLockId))
        let transformParams = transformMQL.save('UserLockBuyer', [...submitData], 'unlock')
        let res = await securitySettingsApi.unlockUser(transformParams)
        if (res) {
          this.$message.success(this.$t('common.success'))
          this.$refs[this.gridId].query()
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
