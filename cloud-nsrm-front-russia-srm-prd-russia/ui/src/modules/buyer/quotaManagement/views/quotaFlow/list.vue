<template>
  <el-container
    class="flex-container quotaflow_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        @getFormData="getQuerydata"
      />
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        :source="quotaApproveApi.listFlowPage"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import quotaflowEdit from './edit.vue'
import { quotaApproveApi } from 'modb@/quotaManagement/api/quotaApi'

export default {
  name: 'QuotaflowList',
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
      quotaApproveApi: quotaApproveApi,
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      tableHeader: [
        // 单据编码
        {
          prop: 'flowCode',
          label: this.$t('dataConfMod.sequenceCode'),
          showType: 'button',
          btnStyle: 'text',
          callback: function (row) {
            this.checkDetails(row)
          }.bind(this),
          width: 100
        },
        // 审批时间
        {
          prop: 'approveDate',
          label: this.$t('supplierRating.approvalTime')
        },
        // 创建时间
        {
          prop: 'creationDate',
          label: this.$t('elementDefinition.creationDate')
        },
        // 创建人
        {
          prop: 'createdBy',
          label: this.$t('supplierCapacityReport.createdBy')
        },
        // 单据状态
        {
          prop: 'approveStatus',
          label: this.$t('purSettlementMod.paymentPlanStatus'),
          width: 100,
          dataType: 'dict', // 字典类型
          code: 'QUOTA_APPROVE_STATUS'
        },
        // 审批说明
        {
          prop: 'approveExplain',
          label: this.$t('quota.instructionsApprove')
        },
        // 操作
        {
          prop: 'operation',
          label: this.$t('common.operation'),
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 130,
          buttons: [
            {
              callback: (row) => this.checkDetails(row),
              // code: "pr:requirementApply:edit",
              // show: (row) => row.approveStatus === 'APPROVAL',
              show: (row) => ['APPROVAL', 'DESTORY'].includes(row.approveStatus),
              formattor: () => {
                return this.$t('orderMod.viewDetail')
              }
            },
            {
              callback: (row) => this.checkDetailsShenpi(row),
              // code: "pr:requirementApply:edit",
              // show: (row) => row.approveStatus != 'APPROVAL',
              show: (row) => !['APPROVAL', 'DESTORY'].includes(row.approveStatus),
              formattor: () => {
                return this.$t('common.approve')
              }
            }
          ]
        }
      ],
      queryForm: [
        // 审批单号
        {
          label: () => this.$t('supplierRating.approvalNumber'),
          prop: 'flowCode'
        },
        // 创建人
        {
          label: () => this.$t('common.creator'),
          type: 'input',
          prop: 'createdBy'
        },
        // 单据状态
        {
          label: () => this.$t('vendorMod.relegation.documentStatus'),
          type: 'dict', // 字典类型
          code: 'QUOTA_APPROVE_STATUS', // 字典code
          prop: 'approveStatus',
          name: 'scc_sup_auth_review_form_qua_sample'
        },
        // 创建时间
        {
          label: () => this.$t('common.creationTime'),
          type: 'daterange',
          prop: 'queryCreationDate'
        },
        // 审批时间
        {
          label: () => this.$t('components.processTable.headers.fdHandlerTime'),
          type: 'daterange',
          prop: 'queryApproveDate'
        }
      ],
      queryParam: {}
    }
  },
  created () {
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    // 点击查看详情
    checkDetails (row) {
      this.$emit('tab-add', {
        component: quotaflowEdit,
        params: {
          flag: 'edit',
          isReadonly: true,
          row: row
        },
        title: row.flowCode,
        name: 'quotaflowEdit' + row.flowCode
      })
    },
    checkDetailsShenpi (row) {
      this.$emit('tab-add', {
        component: quotaflowEdit,
        params: {
          flag: 'edit',
          // isReadonly: true,
          row: row
        },
        title: row.flowCode,
        name: 'quotaflowEdit' + row.flowCode
      })
    },
    getQuerydata (params) {
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRows = val
    }
  }
}
</script>
