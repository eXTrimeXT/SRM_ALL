<template>
  <el-container class="todo-config-wrapper" direction="vertical">
    <el-main class="deepClass">
      <FormWrapper :form-array="preArr" @getFormData="getQuerydata" />
      <!-- <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
        </template>
      </MainHeader> -->
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :page-size="pageSize"
        :auto-query="false"
        :adeptMeiQl="true"
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
  name: 'Done',
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
        label: () => this.$t('secondaryAuthentication.lockType'),
        minWidth: 100,
        dataType: 'dict',
        code: 'USER_LOCK_TYPE'
      }, // "类型"
      {
        prop: 'lockDesc',
        label: () => this.$t('secondaryAuthentication.lockDesc'),
        minWidth: 150
      },
      {
        prop: 'username',
        label: () => this.$t('secondaryAuthentication.username'),
        minWidth: 100
      },
      {
        prop: 'unlockTime',
        label: () => this.$t('secondaryAuthentication.unlockTime'), // "解锁时间"
        minWidth: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'unlockType',
        label: () => this.$t('secondaryAuthentication.unlockType'), // '解锁方式',
        minWidth: 120,
        dataType: 'dict',
        code: 'TWO_AUTH_UNLOCK_MODE'
      },
      {
        prop: 'unlockUsername',
        label: () => this.$t('secondaryAuthentication.unlockUsername'), // '解锁账号',
        minWidth: 120
      }
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
      this.queryParam = transformMQL.listGetData('UserLockBuyer', { ...paramData, unlockType: ['HAND', 'AUTO'] }, 'lastUpdateDate', undefined, 'query', undefined, { unlockType: 'in' })
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    }
  }
}
</script>

<style lang="scss">
.secondary-authentication-config-wrapper{
  padding: 16px !important;
}
</style>
