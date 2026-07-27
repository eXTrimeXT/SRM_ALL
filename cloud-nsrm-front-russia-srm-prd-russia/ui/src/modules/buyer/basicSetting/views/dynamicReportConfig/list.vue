<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="formArray"
        @getFormData="getFormData"
      />
      <EasyTable
        ref="table"
        :selection="false"
        :methods="methods"
        :columns="columns"
        row-key="sqlId"
        table-name="dynamic_report_config_table"
        :query-params.sync="queryParams"
        :editable="false"
        :com-active="$attrs['changeTab']"
      >
        <template #btns>
          <AuthorityButton type="primary" code="dynamicReportConfig:add" @click="add">
            {{ $t('common.add') }}
          </AuthorityButton>
        </template>
      </EasyTable>
    </el-main>
  </el-container>
</template>

<script>
import EasyTable from 'lib@/components/BaseTable/EasyTable'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import dynamicReportConfigEdit from './edit'
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import { dynamicReportConfig } from 'modb@/basicSetting/api/basicSetting'

export default {
  name: 'DynamicReportConfigList',
  components: {
    EasyTable,
    FormWrapper
  },
  mixins: [tabTodoMixin, tabTodoWatch],
  provide () {
    return { context: this }
  },
  data () {
    return {
      queryParams: {},
      formArray: [
        {
          prop: 'nickName',
          label: () => this.$t('dataConfMod.reportName') // 报表名称
        },
        {
          prop: 'name',
          label: () => this.$t('dataConfMod.code')
        }, // 编码
        {
          prop: 'queryModule',
          label: () => this.$t('dataConfMod.model'), // 模块
          type: 'dict',
          code: 'MODULE_DIVISION'
        },
        {
          prop: 'description',
          label: () => this.$t('dataConfMod.description') // 描述
        }
      ],
      methods: {
        listPage: async params => {
          const res = await dynamicReportConfig.listPage(params)
          return res
        }
      },
      columns: [
        {
          attrs: {
            label: () => this.$t('dataConfMod.reportName'), // 报表名称
            prop: 'nickName',
            sortable: true
          }
        },
        {
          attrs: {
            label: () => this.$t('dataConfMod.code'), // 编码
            prop: 'name',
            sortable: true
          }
        },
        {
          attrs: {
            label: 'id',
            prop: 'sqlId',
            sortable: true
          }
        },
        {
          attrs: {
            label: () => this.$t('dataConfMod.model'), // 模块
            prop: 'queryModule',
            sortable: true,
            formatter: value => this.$getDictLabel('MODULE_DIVISION', value)
          }
        },
        // {
        //   attrs: {
        //     label: () => this.$t('dataConfMod.querySql'), // 查询语句类型
        //     prop: 'querySql',
        //     sortable: true
        //   }
        // },
        {
          attrs: {
            label: () => this.$t('dataConfMod.valueAttr'), // 主键Key
            prop: 'valueAttr',
            sortable: true
          }
        },
        {
          attrs: {
            label: () => this.$t('dataConfMod.description'), // 描述
            prop: 'description',
            sortable: true
          }
        },
        {
          attrs: {
            prop: 'operation',
            label: () => this.$t('common.operation'), // 操作
            width: 150,
            fixed: 'right'
          },
          operations: [
            {
              event: 'eidtItem',
              code: 'dynamicReportConfig:edit',
              name: this.$t('common.edit'),
              func: this.eidtItem
            },
            {
              event: 'deleteItem',
              code: 'dynamicReportConfig:delete',
              name: this.$t('common.delete'),
              func: this.deleteItem
            }
          ]
        }
      ]
    }
  },
  methods: {
    dolayout () {
      this.$refs.table.doLayout && this.$refs.table.doLayout()
    },
    getFormData (params) {
      const { pageSize, pageNum } = this.queryParams
      const querys = { pageSize, pageNum, ...params }
      this.$refs.table.search(querys)
    },
    deleteItem ({ row }) {
      this.$confirm(this.$t('common.delRow'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          dynamicReportConfig.delete(row.sqlId).then(res => {
            this.$message.success(res.message)
            this.$refs.table.search()
          })
        })
    },
    add () {
      this.$emit('tab-add', {
        component: dynamicReportConfigEdit,
        params: { flag: 'add' },
        title: this.$t('common.add'),
        name: 'dynamicReportConfigEdit'
      })
    },
    eidtItem ({ row }) {
      const { sqlId, name } = row
      this.$emit('tab-add', {
        component: dynamicReportConfigEdit,
        params: { flag: 'edit', sqlId },
        title: name,
        name: 'dynamicReportConfigEdit' + sqlId
      })
    }
  }
}
</script>

<style lang="scss" scoped></style>
