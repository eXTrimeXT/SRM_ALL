<template>
  <el-container class="flex-container-notab" direction="vertical">
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        :preFormObj="preFormObj"
        @getFormData="getQuerydata"
      />
      <MainHeader>
        <template slot="left">
          <ExportExcel
            page-url="/api-cm/contract/performPlan/listPerPlanReportPage"
            :filter-params="queryParam"
            :table-header="computedTableHeader"
            :dict-codes="dictCodes"
            export-mode="front"
          />
          <span class="text-info">延期天数核算说明：负数表示提前天数，正数为延期天数</span>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-cm/contract/performPlan/listPerPlanReportPage"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch'
import ExportExcel from 'lib@/components/export-excel'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { parseTime } from '@/utils'
export default {
  name: 'ContractPerformanceReportList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    QuickSearch,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      dictCodes: {
        status: 'CONTRACT_PLAN_STATUS',
        milestoneType: 'MILESTONE_SCHEDULE',
        planStatus: 'MILESTONE_STATE',
        milestoneDelay: 'YES_OR_NO',
        paymentDelay: 'YES_OR_NO'
      },
      dialogVisible: false,
      preFormObj: {},
      curRole: this.$store.getters.userType,
      tableName: 'contractPerformancePlanList',
      defaultTableHeader: [],
      pageSize: 15,
      gridId: 'contractPerformancePlanList',
      queryParam: {},
      filterParams: {},
      tableHeader: [],
      tableData: [],
      queryForm: [
        {
          prop: 'perPlanNo',
          label: '合同履约计划单号'
        },
        {
          prop: 'buId',
          label: this.$t('bid_mod.businessEntity'), // 业务实体
          type: 'OUorganizationSelector'
        },
        {
          prop: 'milestoneType',
          label: '里程碑',
          type: 'dict',
          code: 'MILESTONE_SCHEDULE'
        },
        {
          prop: 'vendorName',
          label: () => this.$t('common.vendorName'), // 供应商名称
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info'
        },
        {
          prop: 'perAcceptanceNo',
          label: '合同验收单号' // 合同验收单号
        },
        {
          prop: 'contractNo',
          label: () => this.$t('contractMod.contractNo_1')
        },
        {
          prop: 'paymentDelay',
          label: '付款是否延期',
          type: 'dict',
          code: 'YES_OR_NO'
        },
        {
          prop: 'milestoneDelay',
          label: '里程碑是否延期',
          type: 'dict',
          code: 'YES_OR_NO'
        }
      ]
    }
  },
  computed: {
    computedTableHeader () {
      return this.tableHeader.filter(item => !['operation'].includes(item.prop))
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'perPlanNo',
        label: '合同履约计划单号',
        minWidth: 150
      },
      {
        prop: 'perAcceptanceNo',
        label: '合同验收单号',
        minWidth: 150
      },
      {
        prop: 'buName',
        label: () => this.$t('bid_mod.businessEntity'), // 业务实体
        minWidth: 130
      },
      {
        prop: 'vendorName',
        label: () => _this.$t('common.vendorName'), // 供应商名称
        minWidth: 150
      },
      {
        prop: 'vendorCode',
        label: () => _this.$t('common.vendorCode'), // 供应商编码
        minWidth: 120
      },
      {
        prop: 'contractNo',
        label: '合同序号',
        minWidth: 150
      },
      {
        prop: 'createdFullName',
        label: '合同验收单创建人',
        minWidth: 150
      },
      {
        prop: 'creationDate',
        label: '合同验收单创建时间',
        minWidth: 150,
        formattor: val => {
          return val ? parseTime(val, '{y}-{m}-{d}') : null
        }
      },
      {
        prop: 'status',
        label: '单据状态',
        dataType: 'dict',
        code: 'CONTRACT_PLAN_STATUS',
        minWidth: 120
      },
      {
        prop: 'milestoneType',
        label: '里程碑',
        dataType: 'dict',
        code: 'MILESTONE_SCHEDULE',
        minWidth: 130
      },
      {
        prop: 'planStatus',
        label: '里程碑状态',
        minWidth: 130,
        dataType: 'dict',
        code: 'MILESTONE_STATE'
      },
      {
        prop: 'nodePersonName',
        label: '节点责任人',
        minWidth: 120
      },
      {
        prop: 'planEndDate',
        label: '里程碑计划结束时间',
        minWidth: 150,
        formattor: val => {
          return val ? parseTime(val, '{y}-{m}-{d}') : null
        }
      },
      {
        prop: 'actualCompleteDate',
        label: '里程碑实际结束时间',
        minWidth: 150,
        formattor: val => {
          return val ? parseTime(val, '{y}-{m}-{d}') : null
        }
      },
      {
        prop: 'milestoneDelay',
        label: '里程碑是否延期',
        dataType: 'dict',
        code: 'YES_OR_NO',
        minWidth: 130
      },
      {
        prop: 'milestoneDelayDay',
        label: '里程碑延期天数',
        minWidth: 130
      },
      {
        prop: 'invoiceNo',
        label: '开票申请单号',
        minWidth: 130
      },
      {
        prop: 'invoiceCompleteDate',
        label: '实际开票完成时间',
        minWidth: 150,
        formattor: val => {
          return val ? parseTime(val, '{y}-{m}-{d}') : null
        }
      },
      {
        prop: 'invoicedTaxedAmount',
        label: '实际开票金额含税',
        minWidth: 150
      },
      {
        prop: 'paymentApplyNo',
        label: '付款申请单号',
        minWidth: 130
      },
      {
        prop: 'palnPaymentDate',
        label: '计划付款完成时间',
        minWidth: 150,
        formattor: val => {
          return val ? parseTime(val, '{y}-{m}-{d}') : null
        }
      },
      {
        prop: 'actualPaymentDate',
        label: '实际付款完成时间',
        minWidth: 150,
        formattor: val => {
          return val ? parseTime(val, '{y}-{m}-{d}') : null
        }
      },
      {
        prop: 'stagePaymentAmount',
        label: '实际付款金额',
        minWidth: 130
      },
      {
        prop: 'paymentDelay',
        label: '付款是否延期',
        dataType: 'dict',
        code: 'YES_OR_NO',
        minWidth: 130
      },
      {
        prop: 'paymentDelayDay',
        label: '付款延期天数',
        minWidth: 130
      }
    ]
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    getQuerydata (v) {
      this.queryParam = { ...v, ...this.preFormObj }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    }
  }
}
</script>
<style scoped lang="scss">
.text-info {
  font-size:12px;
  margin-left: 20px;
}
</style>
