<template>
  <el-container class="flex-container-notab" direction="vertical">
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        :preFormObj="preFormObj"
        @getFormData="getQuerydata"
      />
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-cm/contract/performAcceptance/listPage"
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
import ContractPerformanceCheckDetail from './edit'

export default {
  name: 'ContractPerformanceCheckList',
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
        status: 'CONTRACT_CHECK_STATUS'
      },
      dialogVisible: false,
      preFormObj: {},
      curRole: this.$store.getters.userType,
      tableName: 'contractPerformanceCheckList',
      defaultTableHeader: [],
      pageSize: 15,
      gridId: 'contractPerformanceCheckList',
      queryParam: {},
      tableHeader: [],
      tableData: [],
      queryForm: [
        {
          prop: 'perAcceptanceNo',
          // label: '合同验收单号'
          label: () => this.$t('bid_mod.perAcceptanceNo')
        },
        {
          prop: 'buId',
          label: this.$t('bid_mod.businessEntity'), // 业务实体
          type: 'OUorganizationSelector'
        },
        {
          prop: 'createdFullName',
          label: () => this.$t('common.creator') // 创建人
        },
        {
          prop: 'contractNo',
          label: () => this.$t('contractMod.contractNo_1')
        },
        {
          prop: 'status',
          // label: () => '单据状态',
          label: () => this.$t('vendorMod.relegation.documentStatus'),
          type: 'dict',
          code: 'CONTRACT_CHECK_STATUS'
        },
        {
          prop: 'creationDate',
          // label: '创建日期',
          label: () => this.$t('common.creationDate'),
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
  watch: {
    '$route': {
      handler (nVal) {
        if (nVal) {
          let { from, row } = nVal.params
          if (from === 'contractPerformancePlan') {
            this.editTab('edit', row)
          }
        }
      },
      immediate: true,
      deep: true
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'perAcceptanceNo',
        // label: '合同验收单号',
        label: () => this.$t('bid_mod.perAcceptanceNo'),
        showType: 'button',
        btnStyle: 'text',
        minWidth: 150,
        callback: (row) => {
          let type = ['SUBMITTED', 'APPROVED'].includes(row.status) ? 'approval' : 'view'
          this.editTab(type, row)
        }
      },
      {
        prop: 'perPlanNo',
        // label: '合同履约计划单号',
        label: () => this.$t('bid_mod.perPlanNo'),
        minWidth: 150
      },
      {
        prop: 'status',
        // label: '单据状态',
        label: () => this.$t('vendorMod.relegation.documentStatus'),
        dataType: 'dict',
        code: 'CONTRACT_CHECK_STATUS',
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
        // label: '合同序号',
        label: () => this.$t('bidMod.compactIndex'),
        minWidth: 130
      },
      {
        prop: 'nodePersonName',
        // label: '节点负责人',
        label: () => this.$t('common.nodeLeader'),
        minWidth: 130
      },
      {
        prop: 'createdFullName',
        // label: '创建人',
        label: () => this.$t('common.creator'),
        minWidth: 120
      },
      {
        prop: 'creationDate',
        // label: '创建时间',
        label: () => this.$t('common.creationTime'),
        minWidth: 150,
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
              // return '交付'
              return this.$t('supRisk.deliver')
            },
            show: row => {
              return ['DRAFT'].includes(row.status)
            }
          },
          {
            callback: function (row) {
              this.editTab('edit', row)
            }.bind(this),
            formattor (val) {
              // return '编辑'
              return this.$t('common.edit')
            },
            show: row => {
              return ['FIRST_REJECTED'].includes(row.status)
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
          this.$api.cmPerform.vendor.check.performCheck.abandon(row.perCheckId).then((res) => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
      }).catch(() => {})
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
      let name = row.perAcceptanceNo || ''
      let tab = {
        component: ContractPerformanceCheckDetail,
        params: {
          flag: type,
          row,
          // tabName: '合同验收' + name
          tabName: this.$t('contractMod.contractAcceptance') + name
        },
        // title: name ? '合同验收' + name : '新增合同验收',
        title: name ? $t('contractMod.contractAcceptance') + name : $t('contractMod.newContractAcceptance'),
        // name: '合同验收' + name
        name: $t('contractMod.contractAcceptance') + name
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
          this.$api.cmPerform.vendor.check.performCheck.deleteById(row.perCheckId).then((res) => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    }
  }
}
</script>
