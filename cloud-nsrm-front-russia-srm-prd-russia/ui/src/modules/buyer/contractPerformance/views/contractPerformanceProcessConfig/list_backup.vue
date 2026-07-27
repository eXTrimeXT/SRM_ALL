<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQuerydata"
      >
        <template #contractType="{ scope }">
          <dict-select
            v-model="scope.contractType"
            code="ELEM_CONTRACT_TYPE"
          />
        </template>
        <template #status="{ scope }">
          <dict-select
            v-model="scope.status"
            code="PERFORMANCE_OF_CONTRACT"
          />
        </template>
      </FormWrapper>
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            type="primary"
            @click="addHandle"
          >
            {{ $t("common.add") }}
          </AuthorityButton>
          <el-button
            type="primary"
            @click="copyConfig"
          >
            {{ $t('contract_mod.copyConfig') }}
          </el-button>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        open-custom-table
        checkbox
        :comActive="$attrs['changeTab']"
        :source="$api.cmPerform.buyer.main.performanceTpl.listPage"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import contractPerformanceProcessConfigEdit from './edit_backup.vue'

export default {
  name: 'ContractPerformanceProcessConfigList',
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
      name: 'contractPerformanceProcessConfigList',
      tableName: 'contractPerformanceProcessConfigList',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      tableHeader: [],
      defaultTableHeader: [],
      filterConfig: [
        {
          prop: 'contractType',
          label: this.$t('contract_mod.contractType'),
          type: 'slot',
          slot: 'contractType'
        },
        {
          prop: 'status',
          label: this.$t('contract_mod.configStatus'),
          type: 'slot',
          slot: 'status'
        },
        { prop: 'processNum', label: this.$t('contract_mod.processNum') },
        { prop: 'templateName', label: this.$t('contract_mod.templateName') }
      ],
      queryParam: {}
    }
  },
  created () {
    this.tableHeader = [
      {
        prop: 'contractType',
        label: this.$t('contract_mod.contractType'),
        width: 150,
        formattor: (val) => this.$getDictLabel('ELEM_CONTRACT_TYPE', val)
      },
      {
        prop: 'processNum',
        label: this.$t('contract_mod.processNum'),
        width: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: (row) => this.view(row)
      },
      {
        prop: 'templateName',
        label: this.$t('contract_mod.templateName'),
        width: 150
      },
      {
        prop: 'status',
        label: this.$t('contract_mod.configStatus'),
        width: 100,
        formattor: (val) => this.$getDictLabel('PERFORMANCE_OF_CONTRACT', val)
      },
      {
        prop: 'createdFullName',
        label: this.$t('common.creator')
      },
      {
        prop: 'creationDate',
        label: this.$t('common.creationTime'),
        width: 100,
        formattor: (val) => (val ? this.$dayjs(val).format('YYYY-MM-DD') : '')
      },
      {
        prop: 'lastUpdatedFullName',
        label: this.$t('contract_mod.updateBy')
      },
      {
        prop: 'lastUpdateDate',
        label: this.$t('contract_mod.updateDate'),
        width: 100,
        formattor: (val) => (val ? this.$dayjs(val).format('YYYY-MM-DD') : '')
      },
      {
        prop: 'operation',
        label: this.$t('components.headers.operation'),
        showType: 'buttons',
        btnStyle: 'text',
        fixed: 'right',
        buttons: [
          {
            callback: (row) => this.editHandle(row),
            show: (row) => ['DRAFT'].includes(row.status),
            formattor: () => {
              return this.$t('common.edit')
            }
          },
          {
            callback: async (row) => {
              this.$confirm(this.$t('common.delRow'), {
                confirmButtonText: this.$t('common.confirm'),
                cancelButtonText: this.$t('common.cancel'),
                type: 'warning'
              })
                .then(async () => {
                  const res = await this.$api.cmPerform.buyer.main.performanceTpl.bathDelete([
                    row.performTemplHeadId
                  ])
                  this.$message.success(res.message)
                  this.getQuerydata()
                })
                .catch(() => {})
            },
            show: (row) => ['DRAFT'].includes(row.status),
            formattor: () => {
              return this.$t('common.delete')
            }
          },
          {
            callback: async (row) => {
              this.$confirm(this.$t('contractMod.validConfirm'), {
                confirmButtonText: this.$t('common.confirm'),
                cancelButtonText: this.$t('common.cancel'),
                type: 'warning'
              }).then(async () => {
                const res = await this.$api.cmPerform.buyer.main.performanceTpl.failure(row.performTemplHeadId)
                this.$message.success(res.message)
                this.getQuerydata()
              }).catch(() => {})
            },
            show: (row) => row.status === 'VALID',
            formattor: () => {
              return this.$t('contract_mod.invalid')
            }
          },
          {
            callback: row => {
              this.view(row)
            },
            show: row => row.status === 'INVALID',
            formattor: () => {
              return this.$t('common.view')
            }
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    copyConfig () {
      if (!this.currentRows.length) {
        return this.$message.warning(this.$t('contract_mod.lessOneConfig'))
      }
      if (this.currentRows.length > 1) {
        return this.$message.warning(this.$t('contract_mod.justOneConifg'))
      }
      this.addHandle(this.currentRows[0])
    },
    dolayout () {
      // this.$refs[this.gridId].query()
      this.$refs[this.gridId] && this.$refs[this.gridId].doLayout()
    },
    getQuerydata (obj) {
      let objs = obj || this.queryParam
      this.queryParam = { ...objs }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    addHandle (row) {
      this.mode = 'add'
      const tab = {
        component: contractPerformanceProcessConfigEdit,
        params: {
          row,
          flag: this.mode,
          tabName: 'contractPerformanceProcessConfigEdit'
        },
        title: this.$t('common.add'),
        name: 'contractPerformanceProcessConfigEdit'
      }
      this.$emit('tab-add', tab)
    },
    editHandle (row) {
      this.mode = 'edit'
      const tab = {
        component: contractPerformanceProcessConfigEdit,
        params: {
          row,
          flag: this.mode,
          tabName: 'contractPerformanceProcessConfigEdit' + row.performTemplHeadId
        },
        title: row.processNum,
        name: 'contractPerformanceProcessConfigEdit' + row.performTemplHeadId
      }
      this.$emit('tab-add', tab)
    },
    view (row) {
      this.mode = 'view'
      const tab = {
        component: contractPerformanceProcessConfigEdit,
        params: {
          row,
          flag: this.mode,
          tabName: 'contractPerformanceProcessConfigView' + row.performTemplHeadId
        },
        title: `${ this.$t('common.view') } - ${row.processNum}`,
        name: 'contractPerformanceProcessConfigView' + row.performTemplHeadId
      }
      this.$emit('tab-add', tab)
    },
    handleCurrentChange (val) {
      this.currentRows = val
    }
  }
}
</script>
