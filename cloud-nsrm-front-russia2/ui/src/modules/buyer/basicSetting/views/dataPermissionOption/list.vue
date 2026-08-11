<template>
  <el-container
    class="flex-container datapermissionvaroptions_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQueryData"
      />
      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton type="primary" code="dataPermissionOption:add" @click="addHandle">
            {{ $t('common.add') }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :com-active="$attrs['changeTab']"
        :source="dataPermissionOption.list"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import datapermissionvaroptionsEdit from './edit.vue'
import { dataPermissionOption } from 'modb@/basicSetting/api/basicSetting'

export default {
  name: 'DatapermissionvaroptionsList',
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
      dataPermissionOption: dataPermissionOption,
      name: 'datapermissionvaroptionsList',
      tableName: 'datapermissionvaroptionsTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      tableHeader: [
        {
          prop: 'varName',
          label: this.$t('dataConfMod.varName'),
          minWidth: 100
        },
        {
          prop: 'varDesc',
          label: this.$t('dataConfMod.varDesc'),
          minWidth: 100
        },
        {
          prop: 'varType',
          label: this.$t('dataConfMod.varType'),
          minWidth: 100,
          dataType: 'dict',
          code: 'VAR_TYPE'
        },
        {
          prop: 'varValue',
          label: this.$t('dataConfMod.varValue'),
          minWidth: 100
        },
        {
          prop: 'permissionClassName',
          label: this.$t('dataConfMod.permissionClassName'),
          minWidth: 100
        },
        {
          prop: 'permissionMethodName',
          label: this.$t('dataConfMod.permissionMethodName'),
          minWidth: 100
        },
        {
          prop: 'remark',
          label: this.$t('common.remark'),
          minWidth: 100
        },
        {
          prop: 'varStatus',
          label: this.$t('dataConfMod.varStatus'),
          minWidth: 100,
          dataType: 'dict',
          code: 'PERMISSION_STATUS'
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
              code: 'dataPermissionOption:edit',
              // show: row => row.status === "DRAFT",
              formattor: () => {
                return this.$t('common.edit')
              }
            },
            {
              callback: row => this.deleteHandle(row),
              code: 'dataPermissionOption:delete',
              // show: row => row.status === "DRAFT",
              formattor: () => {
                return this.$t('common.delete')
              }
            }
          ]
        }
      ],

      filterConfig: [
        { prop: 'varName', label: this.$t('dataConfMod.varName') },
        { prop: 'varDesc', label: this.$t('dataConfMod.varDesc') }
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
      })
        .then(() => {
          dataPermissionOption.delete(row.dataPermissionVarOptionId).then(res => {
            this.$message.success(res.message)
            this.getQueryData()
          })
        })
    },
    addHandle (row) {
      this.mode = 'add'
      const tab = {
        component: datapermissionvaroptionsEdit,
        ctrlHeight: true,
        params: {
          row,
          flag: this.mode
        },
        title: this.$t('datapermission.varSettingAdd'),
        name: 'datapermissionvaroptionsEdit'
      }
      this.$emit('tab-add', tab)
    },
    editHandle (row) {
      this.mode = 'edit'
      const tab = {
        component: datapermissionvaroptionsEdit,
        ctrlHeight: true,
        params: {
          row,
          flag: this.mode
        },
        title: this.$t('datapermission.varSettingEdit'),
        name: 'datapermissionvaroptionsEdit' + row.dataPermissionVarOptionId
      }
      this.$emit('tab-add', tab)
    },
    handleCurrentChange (val) {
      this.currentRows = val
    }
  }
}
</script>
