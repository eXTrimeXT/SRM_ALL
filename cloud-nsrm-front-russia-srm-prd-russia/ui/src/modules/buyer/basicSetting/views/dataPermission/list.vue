<template>
  <el-container class="flex-container datapermission_list_wrapper" direction="vertical">
    <el-main>
      <FormWrapper :form-array="filterConfig" @getFormData="getQueryData" />
      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton type="primary" code="dataPermission:add" @click="addHandle">
            {{ $t('common.add') }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :com-active="$attrs['changeTab']"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        url="/api-rbac/rbac/data_permission/listPage"
        @afterQuery="afterQuery"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import datapermissionEdit from './edit.vue'
import { dataPermission } from 'modb@/basicSetting/api/basicSetting'

export default {
  name: 'DatapermissionList',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      name: 'datapermissionList',
      tableName: 'datapermissionTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      tableHeader: [
        {
          prop: 'dataPermissionName',
          label: this.$t('dataConfMod.dataPermissionName'),
          minWidth: 30
        },
        {
          prop: 'apiUrl',
          label: this.$t('dataConfMod.apiUrl'),
          minWidth: 50
        },
        {
          prop: 'permissionType',
          label: this.$t('dataConfMod.permissionType'),
          minWidth: 100
        },
        {
          prop: 'showInfo',
          label: this.$t('dataConfMod.permissionShowInfo'),
          minWidth: 100
        },
        {
          prop: 'permissionStatus',
          label: this.$t('dataConfMod.permissionStatus'),
          dataType: 'dict',
          code: 'PERMISSION_STATUS',
          minWidth: 30
        },
        {
          prop: 'operation',
          label: this.$t('common.operation'),
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 130,
          buttons: [
            {
              callback: row => this.editHandle(row),
              code: 'dataPermission:edit',
              formattor: () => {
                return this.$t('common.edit')
              }
            },
            {
              callback: row => this.viewHandle(row),
              code: 'dataPermission:query',
              formattor: () => {
                return this.$t('common.view')
              }
            },
            {
              callback: row => this.deleteHandle(row),
              code: 'dataPermission:delete',
              formattor: () => {
                return this.$t('common.delete')
              }
            }
          ]
        }
      ],

      filterConfig: [
        { prop: 'dataPermissionName', label: this.$t('dataConfMod.dataPermissionName') },
        { prop: 'apiUrl', label: this.$t('dataConfMod.apiUrl') },
        {
          prop: 'permissionStatus',
          label: this.$t('dataConfMod.permissionOfStatus'),
          type: 'dict',
          code: 'PERMISSION_STATUS'
        },
        { prop: 'mybatisSqlId', label: 'MYBATIS_SQL_ID' }
      ],
      queryParam: {}
    }
  },
  created () {
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQueryData()
    })
  },
  methods: {
    getQueryData (params) {
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    deleteHandle (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        dataPermission.delete(row.dataPermissionId).then(res => {
          this.$message.success(res.message)
          this.getQueryData()
        })
      })
    },
    addHandle (row) {
      this.mode = 'add'
      const tab = {
        component: datapermissionEdit,
        ctrlHeight: true,
        params: {
          row,
          flag: this.mode
        },
        title: this.$t('dataConfMod.datapermissionAdd'),
        name: 'datapermissionEdit'
      }
      this.$emit('tab-add', tab)
    },
    editHandle (row) {
      this.mode = 'edit'
      const tab = {
        component: datapermissionEdit,
        ctrlHeight: true,
        params: {
          row,
          flag: this.mode
        },
        title: this.$t('dataConfMod.datapermissionEdit'),
        name: 'datapermissionEdit' + row.dataPermissionId
      }
      this.$emit('tab-add', tab)
    },
    viewHandle (row) {
      this.mode = 'view'
      const tab = {
        component: datapermissionEdit,
        ctrlHeight: true,
        params: {
          row,
          flag: this.mode,
          readOnly: true
        },
        title: this.$t('dataConfMod.datapermissionView'),
        name: 'datapermissionView' + row.dataPermissionId
      }
      this.$emit('tab-add', tab)
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    afterQuery (res) {
      if (res.length > 0) {
        res.map(row => {
          if (row.permissionType === 'MYBATIS') {
            row.showInfo = row.mybatisSqlId
          } else if (row.permissionType === 'MEIQL') {
            row.showInfo = row.modelInfo
          } else {
            row.showInfo = ''
          }
        })
      }
      console.log(res)
    }
  }
}
</script>
