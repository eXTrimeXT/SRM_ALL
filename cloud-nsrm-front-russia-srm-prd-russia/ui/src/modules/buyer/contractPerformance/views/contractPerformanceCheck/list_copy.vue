<template>
  <el-container class="flex-container-notab" direction="vertical">
    <el-main>
      <form-wrapper
        :form-array="queryForm"
        :preFormObj="preFormObj"
        @getFormData="getQuerydata"
      />
      <main-header>
        <template slot="left">
          <export-excel
            page-url="/api-cm/contract/performAcceptance/listPage"
            :filter-params="queryParam"
            :table-header="computedTableHeader"
            :dict-codes="dictCodes"
            export-mode="front"
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
          label: '合同验收单号'
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
          prop: 'createdFullName',
          label: () => this.$t('common.creator') // 创建人
        },
        {
          prop: 'contractNo',
          label: () => this.$t('contractMod.contractNo_1')
        },
        {
          prop: 'status',
          label: () => '单据状态',
          type: 'dict',
          code: 'CONTRACT_CHECK_STATUS'
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
        label: '合同验收单号',
        showType: 'button',
        btnStyle: 'text',
        minWidth: 150,
        callback: (row) => {
          this.editTab('view', row)
        }
      },
      {
        prop: 'perPlanNo',
        label: '合同履约计划单号',
        minWidth: 150
      },
      {
        prop: 'status',
        label: '单据状态',
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
        label: '合同序号',
        minWidth: 130
      },
      {
        prop: 'nodePersonName',
        label: '节点负责人',
        minWidth: 130
      },
      {
        prop: 'createdFullName',
        label: '创建人',
        minWidth: 120
      },
      {
        prop: 'creationDate',
        label: '创建时间',
        minWidth: 150
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
              return '交付'
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
              return '编辑'
            },
            show: row => {
              return ['WITHDRAW', 'REJECTED'].includes(row.status)
            }
          },
          {
            callback: function (row) {
              this.editTab('edit', row)
            }.bind(this),
            formattor (val) {
              return '交付确认'
            },
            show: row => {
              return row.status === 'SUPPLIER_SUBMITTED'
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
              this.editTab('edit', row)
            }.bind(this),
            formattor (val) {
              return '提交审批'
            },
            show: row => {
              return row.status === 'FIRST_PASS'
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
          this.$api.cmPerform.buyer.main.performCheck.abandon(row.perCheckId).then((res) => {
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
          tabName: '合同验收' + name
        },
        title: name ? '合同验收' + name : '新增合同验收',
        name: '合同验收' + name
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
          this.$api.cmPerform.buyer.main.performCheck.deleteById(row.perCheckId).then((res) => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    }
  }
}
</script>
