<template>
  <el-container
    class="flex-container monitorbizconfig_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQueryData"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            type="primary"
            code="monitorBizConfig:add"
            @click="addHandle"
          >
            {{ $t('common.add') }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="true"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-base/base/monitor_biz_config/listPage"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from '@/library/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import monitorbizconfigEdit from './edit.vue'
import TableOperation from '@/library/mixins/table-operation'
import { monitorBizConfig } from 'modb@/basicSetting/api/basicSetting'
export default {
  name: 'MonitorBizConfig',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  mixins: [tabTodoWatch, tabTodoMixin, TableOperation],
  provide () {
    return { context: this }
  },
  data () {
    return {
      componentName: 'monitorBizConfig',
      componentConfig: {
        add: monitorbizconfigEdit,
        edit: monitorbizconfigEdit
      },
      pageSize: 15,
      operationFunction: {
        deleteInfo: monitorBizConfig.delete
      },
      tableHeader: [
        {
          prop: 'monitorBizName',
          label: () => this.$t('monitorBizConfig.monitorBizName'),
          width: 100
        },
        {
          prop: 'monitorBizType',
          label: () => this.$t('monitorBizConfig.monitorBizType'),
          width: 100,
          dataType: 'dict',
          code: 'MONITOR_BIZ_TYPE'
        },
        {
          prop: 'monitorMode',
          label: () => this.$t('monitorBizConfig.monitorMode'),
          width: 100,
          dataType: 'dict',
          code: 'MONITOR_MODE'
        },
        {
          prop: 'queryModule',
          label: () => this.$t('monitorBizConfig.queryModule'),
          width: 100,
          dataType: 'dict',
          code: 'MODULE_DIVISION'
        },
        {
          prop: 'columnName',
          label: () => this.$t('monitorBizConfig.columnName'),
          width: 100
        },
        {
          prop: 'operationSymbol',
          label: () => this.$t('monitorBizConfig.operationSymbol'),
          width: 100,
          dataType: 'dict',
          code: 'OPERATION_SYMBOL'
        },
        {
          prop: 'dataValue',
          label: () => this.$t('monitorBizConfig.dataValue'),
          width: 100
        },
        {
          prop: 'cycleType',
          label: () => this.$t('monitorBizConfig.cycleType'),
          width: 100,
          dataType: 'dict',
          code: 'CYCLE_TYPE'
        },
        {
          prop: 'triggerName',
          label: () => this.$t('dataConfMod.triggerName'),
          width: 100
        },
        {
          prop: 'cronExpression',
          label: () => this.$t('monitorBizConfig.cronExpression'),
          width: 100
        },
        {
          prop: 'validStatus',
          label: () => this.$t('monitorBizConfig.validStatus'),
          width: 100,
          dataType: 'dict',
          code: 'VALID_STATUS'
        },
        {
          prop: 'latestBizStatus',
          label: () => this.$t('monitorBizConfig.latestBizStatus'),
          width: 140,
          dataType: 'dict',
          code: 'MONITOR_BIZ_STATUS'
        },
        {
          prop: 'creationDate',
          label: () => this.$t('common.creationDate'),
          width: 100,
          dataType: 'dateTime'
        },
        {
          prop: 'createdBy',
          label: () => this.$t('common.creator'),
          width: 100
        },
        {
          prop: 'operation',
          label: () => this.$t('common.operation'),
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 160,
          buttons: [
            {
              code: 'monitorBizConfig:edit',
              callback: row => this.editHandle(row, row.monitorBizConfigId),
              formattor: () => {
                return this.$t('common.edit')
              }
            },
            {
              code: 'monitorBizConfig:delete',
              callback: row => this.deleteHandle(row.monitorBizConfigId),
              formattor: () => {
                return this.$t('common.delete')
              }
            },
            {
              callback: row => this.dealHandle(row, 'Y'),
              code: 'monitorBizConfig:active',
              show: row => row.validStatus === 'N',
              formattor: () => {
                return this.$t('common.enable')
              }
            },
            {
              callback: row => this.dealHandle(row, 'N'),
              show: row => row.validStatus === 'Y',
              code: 'monitorBizConfig:inActive',
              formattor: () => {
                return this.$t('common.blockUp')
              }
            }
          ]
        }
      ],
      filterConfig: [
        { prop: 'monitorBizName', label: () => this.$t('monitorBizConfig.monitorBizName'), width: 180 },
        { prop: 'monitorBizType', label: () => this.$t('monitorBizConfig.monitorBizType'), width: 180, type: 'dict', code: 'MONITOR_BIZ_TYPE' }
      ]
    }
  },

  methods: {
    dealHandle (row, type) {
      this.$confirm(this.$t('dataConfMod.enabledUse'), {
        confirmButtonText: this.$t('common.yes'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        if (type === 'Y') {
          monitorBizConfig.startTask({ monitorBizConfigId: row.monitorBizConfigId }).then(res => {
            this.$message.success(res.message)
            this.getQueryData()
          })
        } else {
          monitorBizConfig.stopTask({ monitorBizConfigId: row.monitorBizConfigId }).then(res => {
            this.$message.success(res.message)
            this.getQueryData()
          })
        }
      })
    }
  }
}
</script>
