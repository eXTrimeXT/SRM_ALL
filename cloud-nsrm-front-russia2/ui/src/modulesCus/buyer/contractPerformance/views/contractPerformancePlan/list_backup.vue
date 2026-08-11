<template>
  <el-container class="flex-container-notab" direction="vertical">
    <el-main>
      <form-wrapper
        :form-array="queryForm"
        :preFormObj="preFormObj"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <main-header>
        <template slot="left">
          <el-button type="primary" @click="editTab('add')">
            {{ $t('common.add') }}
          </el-button>
          <export-excel
            page-url="/api-cm/contract/performPlan/listPage"
            :filter-params="queryParam"
            :table-header="computedTableHeader"
            :dict-codes="dictCodes"
            export-mode="front"
            type="default"
          />
        </template>
      </main-header>
      <table-view
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        :source="$api.cmPerform.buyer.main.performPlan.listPage"
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
import ContractPerformancePlanDetail from './edit_backup'

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
          prop: 'vendorName',
          label: () => this.$t('common.vendorName'), // 供应商名称
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info'
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
        },
        {
          prop: 'status',
          label: () => '单据状态',
          type: 'dict',
          code: 'CONTRACT_PLAN_STATUS'
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
          this.editTab('view', row)
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
        width: 150,
        dataType: 'dateTime'
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
              this.editTab('edit', row)
            }.bind(this),
            formattor (val) {
              return '编辑'
            },
            show: row => {
              return ['REJECTED', 'DRAFT', 'WITHDRAW'].includes(row.status)
            }
          },
          {
            callback: function (row) {
              this.deleteOne(row)
            }.bind(this),
            formattor (val) {
              return '删除'
            },
            show: row => {
              return row.status === 'DRAFT'
            }
          },
          {
            callback: function (row) {
              this.abandonHandle(row)
            }.bind(this),
            formattor (val) {
              return '废弃'
            },
            show: row => {
              return row.status === 'REJECTED'
            }
          },
          {
            callback: function (row) {
              this.editTab('view', row)
            }.bind(this),
            formattor (val) {
              return '审批'
            },
            show: row => {
              return row.status === 'SUBMITTED'
            }
          },
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
          },
          { // 履约完成后还需要付款
            callback: function (row) {
              this.editTab('view', row)
            }.bind(this),
            formattor (val) {
              return '管理'
            },
            show: row => {
              return ['COMPLETE_PERFORMANCE'].includes(row.status)
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
          this.$api.cmPerform.buyer.main.performPlan.abandon(row.perPlanId).then((res) => {
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
        this.queryParam.creationDateStart = creationDate[0]
        this.queryParam.creationDateEnd = creationDate[1]
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
          this.$api.cmPerform.buyer.main.performPlan.deleteById(row.perPlanId).then((res) => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    }
  }
}
</script>
