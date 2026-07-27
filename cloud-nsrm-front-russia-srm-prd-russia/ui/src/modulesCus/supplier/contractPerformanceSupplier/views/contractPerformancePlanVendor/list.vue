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
          label: '合同履约计划单号'
        },
        {
          prop: 'buId',
          label: this.$t('bid_mod.businessEntity'), // 业务实体
          type: 'OUorganizationSelector'
        },
        {
          prop: 'status',
          label: () => '单据状态',
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
          label: '合同类型',
          type: 'dict',
          code: 'ELEM_CONTRACT_TYPE'
        },
        {
          prop: 'creationDate',
          label: '创建日期',
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
        label: '合同履约计划单号',
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
        label: '里程碑模板编号',
        width: 150
      },
      {
        prop: 'templateName',
        label: '里程碑模板名称',
        width: 150
      },
      {
        prop: 'status',
        label: '单据状态',
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
        label: '合同类型',
        dataType: 'dict',
        code: 'ELEM_CONTRACT_TYPE',
        width: 120
      },
      {
        prop: 'contractNo',
        label: '合同序号',
        width: 150
      },
      {
        prop: 'includeTaxAmount',
        label: '合同含税金额',
        width: 130
      },
      {
        prop: 'createdFullName',
        label: '创建人',
        width: 120
      },
      {
        prop: 'creationDate',
        label: '创建时间',
        width: 150
      },
      {
        prop: 'currentMilestoneType',
        label: '当前里程碑节点',
        width: 130,
        dataType: 'dict',
        code: 'MILESTONE_SCHEDULE'
      },
      {
        prop: 'currentNodePersonName',
        label: '节点责任人',
        width: 120
      },
      {
        prop: 'currentPlanEndDate',
        label: '里程碑计划结束日期',
        width: 150
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
              return '管理'
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
      this.$confirm('确认废弃此数据?', {
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
          tabName: '履约计划' + name
        },
        title: name ? '履约计划' + name : '新增履约计划',
        name: '履约计划' + name
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
