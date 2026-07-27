<template>
  <el-container
    class="flex-container monitorbizlog_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="filterConfig"
        @getFormData="getQuerydata"
      />
      <table-view
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="true"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-base/base/monitor_biz_log/listPage"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from '@/library/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import monitorbizlogEdit from './edit.vue'
import { monitorBizLog } from 'modb@/basicSetting/api/basicSetting'

export default {
  name: 'MonitorbizlogList',
  components: {
    TableView,
    FormWrapper
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      name: 'monitorbizlogList',
      tableName: 'monitorbizlogTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      tableHeader: [
        {
          prop: 'monitorBizName',
          label: () => this.$t('monitorBizConfig.monitorBizName'),
          width: 180
        },
        {
          prop: 'receiveMails',
          label: () => this.$t('monitorBizConfig.receiveMails'),
          width: 180
        },
        {
          prop: 'executeStartTime',
          label: () => this.$t('monitorBizConfig.executeStartTime'),
          width: 180
        },
        {
          prop: 'executeEndTime',
          label: () => this.$t('monitorBizConfig.executeEndTime'),
          width: 180
        },
        {
          prop: 'bizStatus',
          label: () => this.$t('monitorBizConfig.bizStatus'),
          width: 100,
          dataType: 'dict',
          code: 'MONITOR_BIZ_STATUS'
        },
        {
          prop: 'errorMsg',
          label: () => this.$t('dataConfMod.errorInfo'),
          width: 200
        },
        {
          prop: 'dealStatus',
          label: this.$t('monitorBizConfig.dealStatus'),
          width: 120,
          dataType: 'dict',
          code: 'DEAL_STATUS'
        },
        {
          prop: 'creationDate',
          label: () => this.$t('common.creationDate'),
          width: 180
        },
        {
          prop: 'createdBy',
          label: () => this.$t('common.creator'),
          width: 180
        },
        {
          prop: 'operation',
          label: () => this.$t('common.operation'),
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 130,
          buttons: [
            {
              callback: row => this.dealHandle(row),
              show: row => row.dealStatus === 'N',
              formattor: () => {
                return this.$t('common.handle')
              }
            }
          ]
        }
      ],

      filterConfig: [
        { prop: 'dealStatus', label: () => this.$t('monitorBizConfig.dealStatus'), width: 180, type: 'dict', code: 'DEAL_STATUS' }
      ],
      queryParam: {}
    }
  },
  created () {
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    getQuerydata (params) {
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    dealHandle (row) {
      this.$confirm(this.$t('monitorBizConfig.isMonitorBizLog'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          monitorBizLog.dealHandle({ monitorBizLogId: row.monitorBizLogId }).then(res => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
    },
    handleCurrentChange (val) {
      this.currentRows = val
    }
  }
}
</script>
