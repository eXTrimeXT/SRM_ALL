<template>
  <el-container class="flex-container-notab" direction="vertical">
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        :preFormObj="preFormObj"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        :source="$api.cmPerform.vendor.plan.performPlan.listPage"
      />
    </el-main>
  </el-container>
</template>
<script>
import MImport from 'lib@/components/import'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import ExportExcel from 'lib@/components/export-excel'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import ContractPerformancePlanDetail from './edit'

export default {
  name: 'ContractPerformancePlanList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    QuickSearch,
    OrganizationSelector,
    MImport,
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
        contractClass: 'ELEM_CONTRACT_TYPE',
        currentMilestoneType: 'MILESTONE_SCHEDULE'
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
          label: this.$t('bid_mod.perPlanNo')
        },
        {
          prop: 'buId',
          label: this.$t('bid_mod.businessEntity'), // 业务实体
          type: 'OUorganizationSelector'
        },
        {
          prop: 'status',
          label: this.$t('vendorMod.relegation.documentStatus'),
          type: 'dict',
          code: 'CONTRACT_PLAN_STATUS'
        },
        {
          prop: 'contractCreatedFullName',
          label: () => this.$t('common.creator') // 创建人
        },
        {
          prop: 'contractNo',
          label: () => this.$t('contractMod.contractNo_1')
        },
        {
          prop: 'contractClass',
          label: this.$t('contractMod.contractType'),
          type: 'dict',
          code: 'ELEM_CONTRACT_TYPE'
        },
        {
          prop: 'creationDate',
          label: this.$t('common.creationDate'),
          type: 'daterange'
        }
      ],
      currentRow: {}
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
        label: this.$t('bid_mod.perPlanNo'),
        showType: 'button',
        btnStyle: 'text',
        width: 150,
        callback: (row) => {
          let type = ['SUBMITTED', 'APPROVED'].includes(row.status) ? 'approval' : 'view'
          this.editTab(type, row)
        }
      },
      {
        prop: 'processNum',
        label: this.$t('contract_mod.processNum'),
        width: 150
      },
      {
        prop: 'templateName',
        label: this.$t('contract_mod.templateName'),
        width: 150
      },
      {
        prop: 'status',
        label: this.$t('vendorMod.relegation.documentStatus'),
        dataType: 'dict',
        code: 'CONTRACT_PLAN_STATUS',
        width: 150
      },
      {
        prop: 'buName',
        label: () => this.$t('bid_mod.businessEntity'), // 业务实体
        width: 130
      },
      {
        prop: 'vendorName',
        label: () => _this.$t('common.vendorName'), // 供应商名称
        width: 150
      },
      {
        prop: 'vendorCode',
        label: () => _this.$t('common.vendorCode'), // 供应商编码
        width: 120
      },
      {
        prop: 'contractClass',
        label: this.$t('contractMod.contractType'),
        dataType: 'dict',
        code: 'ELEM_CONTRACT_TYPE',
        width: 120
      },
      {
        prop: 'contractNo',
        label: this.$t('bidMod.compactIndex'),
        width: 150
      },
      {
        prop: 'includeTaxAmount',
        label: this.$t('contract_mod.contractTaxAmount'),
        width: 130
      },
      {
        prop: 'createdFullName',
        label: this.$t('common.creator'),
        width: 120
      },
      {
        prop: 'creationDate',
        label: this.$t('common.creationTime'),
        width: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'currentMilestoneType',
        label: this.$t('contract_mod.currentMilestoneNode'),
        width: 130,
        dataType: 'dict',
        code: 'MILESTONE_SCHEDULE'
      },
      {
        prop: 'currentNodePersonName',
        label: this.$t('contract_mod.nodePerson'),
        width: 120
      },
      {
        prop: 'currentPlanEndDate',
        label: this.$t('contract_mod.milestonePlanEndDate'),
        width: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'operation',
        label: () => _this.$t('common.operation'), // 操作
        width: 120,
        btnStyle: 'text',
        fixed: 'right',
        showType: 'buttons',
        buttons: [
          {
            callback: function (row) {
              this.editTab('manage', row)
            }.bind(this),
            formattor (val) {
              return this.$t('bidMod.management')
            },
            show: row => {
              return ['IN_PERFORMANCE', 'APPROVED'].includes(row.status)
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
    abandonHandle (row) {
      // 确认废弃此数据?
      this.$confirm(this.$t('cusEntry.supplement20250205.confirmDiscardData'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
          this.$api.cmPerform.vendor.plan.performPlan.abandon(row.perPlanId).then((res) => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
      }).catch(() => {})
    },
    syncFilterParams (values) {
      this.filterParams = values
    },
    getQuerydata (v) {
      this.queryParam = { ...v, ...this.preFormObj }
      let { creationDate = [] } = this.queryParam
      if (creationDate.length) {
        this.queryParam.contractCreationDateStart = creationDate[0]
        this.queryParam.contractCreationDateEnd = creationDate[1]
      }
      delete this.queryParam.creationDate
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 编辑tab
    editTab (type, row = {}) {
      let name = row.perPlanNo || ''
      let tab = {
        component: ContractPerformancePlanDetail,
        params: {
          flag: type,
          row,
          tabName: this.$t('contract_mod.performancePlan') + name
        },
        title: name ? this.$t('contract_mod.performancePlan') + name : this.$t('cusEntry.supplement20250205.addPerformancePlan'), // 新增履约计划
        name: this.$t('contract_mod.performancePlan') + name
      }
      this.$emit('tab-add', tab)
    },
    deleteOne (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$api.cmPerform.vendor.plan.performPlan.deleteById(row.perPlanId).then((res) => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    }
  }
}
</script>
