<template>
  <el-container class="flex-container-notab" direction="vertical">
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        :preFormObj="preFormObj"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <MainHeader>
        <template slot="left">
          <el-button type="primary" @click="editTab('add')">
            {{ $t('common.add') }}
          </el-button>
          <ExportExcel
            page-url="/api-cm/contract/performPlan/listPage"
            :filter-params="queryParam"
            :table-header="computedTableHeader"
            :dict-codes="dictCodes"
            export-mode="front"
            type="default"
          />
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
          label: this.$t('bid_mod.perPlanNo')  // '合同履约计划单号'
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
          label: () => this.$t('contractMod.contractType'),  // '合同类型'
          type: 'dict',
          code: 'ELEM_CONTRACT_TYPE'
        },
        {
          prop: 'creationDate',
          label: () => this.$t('common.creationDate'),  // '创建日期'
          type: 'daterange'
        },
        {
          prop: 'status',
          label: () => this.$t('vendorMod.relegation.documentStatus'),  // '单据状态'
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
        label: () => _this.$t('bid_mod.perPlanNo'), // '合同履约计划单号'
        showType: 'button',
        btnStyle: 'text',
        width: 150,
        callback: (row) => {
          this.editTab('view', row)
        }
      },
      {
        prop: 'processNum',
        label: () => _this.$t('contract_mod.processNum'),  // '里程碑模板编号'
        width: 150
      },
      {
        prop: 'templateName',
        label: () => _this.$t('contract_mod.templateName'), // '里程碑模板名称'
        width: 150
      },
      {
        prop: 'status',
        label: () => _this.$t('vendorMod.relegation.documentStatus'),  // '单据状态'
        dataType: 'dict',
        code: 'CONTRACT_PLAN_STATUS',
        width: 150
      },
      {
        prop: 'buName',
        label: () => _this.$t('bid_mod.businessEntity'), // 业务实体
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
        label: () => _this.$t('contractMod.contractType'),  // '合同类型'
        dataType: 'dict',
        code: 'ELEM_CONTRACT_TYPE',
        width: 120
      },
      {
        prop: 'contractNo',
        label: () => _this.$t('bidMod.compactIndex'),  // '合同序号'
        width: 150
      },
      {
        prop: 'includeTaxAmount',
        label: () => _this.$t('contract_mod.contractTaxAmount'),  // '合同含税金额'
        width: 130
      },
      {
        prop: 'createdFullName',
        label: () => _this.$t('common.creator'),  // '创建人'
        width: 120
      },
      {
        prop: 'creationDate',
        label: () => _this.$t('common.creationTime'),  // '创建时间'
        width: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'currentMilestoneType',
        label: () => _this.$t('contract_mod.currentMilestoneNode'),  // '当前里程碑节点'
        width: 130,
        dataType: 'dict',
        code: 'MILESTONE_SCHEDULE'
      },
      {
        prop: 'currentNodePersonName',
        label: () => _this.$t('contract_mod.nodePerson'),  // '节点责任人'
        width: 120
      },
      {
        prop: 'currentPlanEndDate',
        label: () => _this.$t('contract_mod.milestonePlanEndDate'),  // '里程碑计划结束日期'
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
              // '编辑'
              return () => _this.$t('common.edit')
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
              // '删除'
              return () => _this.$t('components.common.delete')
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
              // '废弃'
              return () => _this.$t('components.approvalHead.headers.abandon')
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
              // '审批'
              return () => _this.$t('common.approve')
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
              // '管理'
              return () => _this.$t('bidMod.management')
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
              // '管理'
              return () => _this.$t('bidMod.management')
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
      // '确认废弃此数据?'
      this.$confirm(this.$t('cusEntry.supplement20250205.confirmDiscardData'), {
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
          tabName: this.$t('contract_mod.performancePlan') + name  // '履约计划'
        },
        // title: name ? '履约计划' + name : '新增履约计划',
        title: name ? this.$t('contract_mod.performancePlan') + name : this.$t('cusEntry.supplement20250205.addPerformancePlan'),
        name: this.$t('contract_mod.performancePlan') + name  // '履约计划'
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
