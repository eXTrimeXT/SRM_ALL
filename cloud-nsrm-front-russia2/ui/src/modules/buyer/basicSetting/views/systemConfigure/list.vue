<template>
  <el-container
    class="flex-container systemconfigure_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQueryData"
      />
      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton type="primary" @click="addHandle">
            {{ $t('common.add') }}
          </AuthorityButton>
          <AuthorityButton @click="refreshCache">
            {{ $t('common.refreshCache') }}
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
        :source="systemConfigure.list"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import systemconfigureEdit from './edit.vue'
import { systemConfigure } from 'modb@/basicSetting/api/basicSetting'

export default {
  name: 'SystemconfigureList',
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
      systemConfigure: systemConfigure,
      name: 'systemconfigureList',
      tableName: 'systemconfigureTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      tableHeader: [
        {
          prop: 'paramKey',
          label: this.$t('dataConfMod.paramKey'),
          width: 100
        },
        {
          prop: 'paramValue',
          label: this.$t('dataConfMod.paramValue'),
          width: 100
        },
        {
          prop: 'paramDesc',
          label: this.$t('dataConfMod.paramDesc'),
          width: 100
        },
        {
          prop: 'paramStatus',
          label: this.$t('dataConfMod.paramStatus'),
          width: 100,
          dataType: 'dict',
          code: 'YES_OR_NO'
        },
        {
          prop: 'operation',
          label: this.$t('common.operation'),
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 100,
          buttons: [
            {
              callback: row => this.editHandle(row),
              formattor: () => {
                return this.$t('common.edit')
              }
            },
            {
              callback: row => this.deleteHandle(row),
              formattor: () => {
                return this.$t('common.delete')
              }
            }
          ]
        }
      ],

      filterConfig: [
        {
          prop: 'paramKey',
          label: this.$t('dataConfMod.paramKey')
        },
        {
          prop: 'paramValue',
          label: this.$t('dataConfMod.paramValue')
        },
        {
          prop: 'paramStatus',
          label: this.$t('dataConfMod.paramStatus'),
          type: 'dict',
          code: 'YES_OR_NO'
        },
        {
          prop: 'paramDesc',
          label: this.$t('dataConfMod.paramDesc')
        }
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
          systemConfigure.delete(row.systemConfigureId).then(res => {
            this.$message.success(res.message)
            this.getQueryData()
          })
        })
    },
    addHandle (row) {
      this.mode = 'add'
      const tab = {
        component: systemconfigureEdit,
        params: {
          row,
          flag: this.mode
        },
        title: () => this.$t('dataConfMod.systemconfigureAdd'),
        name: 'systemconfigureEdit'
      }
      this.$emit('tab-add', tab)
    },
    refreshCache () {
      this.$confirm(this.$t('dataConfMod.sureFlushCache'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          systemConfigure.refreshConfigureCache().then(res => {
            this.$message.success(res.message)
          })
        })
    },
    editHandle (row) {
      this.mode = 'edit'
      const tab = {
        component: systemconfigureEdit,
        params: {
          row,
          flag: this.mode
        },
        title: () => this.$t('dataConfMod.systemconfigureEdit'),
        name: 'systemconfigureEdit' + row.systemConfigureId
      }
      this.$emit('tab-add', tab)
    },
    handleCurrentChange (val) {
      this.currentRows = val
    }
  }
}
</script>
