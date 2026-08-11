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
        :source="$api.cmPerform.vendor.inv.performInvoice.listPage"
      >
        <template #outstanding="{scope}">
          <el-progress :show-text="false" :percentage="percentage(scope.row.paymentAmount, scope.row.stagePaymentAmount)" :color="customColorMethod" />
        </template>
        <template #payExplain="{scope}">
          <dict-select
            :value="num(scope.row.payExplain)"
            code="payExplain"
            custom-select-type="payExplain"
            :disabled="true"
          />
        </template>
      </TableView>
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
import ContractPerformanceInvoiceDetail from './edit'
import paymentPlanDetail from '@/modules/purSettlement/views/purPaymentApply/paymentPlanDetail'
import advancePaymentDetail from '@/modules/purSettlement/views/advancePayment/advancePaymentDetail'

export default {
  name: 'ContractPerformanceInvoiceList',
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
        status: 'CONTRACT_INVOICE_STATUS',
        contractClass: 'ELEM_CONTRACT_TYPE'
      },
      dialogVisible: false,
      preFormObj: {},
      curRole: this.$store.getters.userType,
      tableName: 'contractPerformanceInvoiceList',
      defaultTableHeader: [],
      pageSize: 15,
      gridId: 'contractPerformanceInvoiceList',
      queryParam: {},
      filterParams: {},
      tableHeader: [],
      tableData: [],
      queryForm: [
        {
          prop: 'invoiceNo',
          label: this.$t('contract_mod.processNum2')
        },
        {
          prop: 'buId',
          label: this.$t('bid_mod.businessEntity'), // 业务实体
          type: 'OUorganizationSelector'
        },
        {
          prop: 'status',
          label: () => this.$t('vendorMod.relegation.documentStatus'),
          type: 'dict',
          code: 'CONTRACT_INVOICE_STATUS'
        },
               {
          prop: 'createdFullName',
          label: () => this.$t('common.creator') // 创建人
        },
        {
          prop: 'creationDate',
          label: this.$t('common.creationDate'),
          type: 'daterange'
        },
        {
          prop: 'vendorName',
          label: () => this.$t('common.vendorName'), // 供应商名称
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info'
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
        prop: 'invoiceNo',
        label: this.$t('contract_mod.processNum2'),
        showType: 'button',
        btnStyle: 'text',
        minWidth: 138,
        fixed: 'left',
        callback: (row) => {
          this.editTab('view', row)
        }
      },
      {
        prop: 'advanceApplyNumber',
        label: this.$t('purSettlementMod.advancePaymentNum'),
        fixed: 'left',
        minWidth: 125
      },
      {
        prop: 'paymentApplyNumber',
        label: this.$t('contractMod.paymentApplyNumber'),
        fixed: 'left',
        minWidth: 120
      },
      {
        prop: 'creationDate',
        label: this.$t('contractMod.unpaidApplicationBalance'),
        minWidth: 125,
        fixed: 'left',
        showType: 'slot',
        slot: 'outstanding'
      },
      {
        prop: 'contractName',
        label: () => this.$t('vendorMod.contractName'), // 合同名称
        minWidth: 130
      },
      {
        prop: 'milestoneType',
        label: () => this.$t('contract_mod.processNodeName'), // 里程碑名称
        dataType: 'dict',
        code: 'MILESTONE_SCHEDULE',
        minWidth: 130
      },
      {
        prop: 'paymentStage',
        label: () => this.$t('bidMod.payStage'), // 付款阶段
        dataType: 'dict',
        code: 'PAYMENT_STAGE',
        minWidth: 130
      },
      {
        prop: 'payExplain',
        label: () => this.$t('route.contractPaymentType'), // 付款条件
        minWidth: 130,
        showType: 'slot',
        slot: 'payExplain'
      },
      {
        prop: 'payMethod',
        label: () => this.$t('bidMod.category_paymentMethod'), // 付款方式
        dataType: 'dict',
        code: 'PAYMENT_MODE',
        minWidth: 130
      },
      {
        prop: 'paymentRatio',
        label: () => this.$t('contractMod.payRatio'), // 付款比例
        minWidth: 130
      },
      {
        prop: 'stagePaymentAmount',
        label: () => this.$t('contractMod.stagePaymentAmount'), // 阶段应付金额
        minWidth: 130
      },
      {
        prop: 'status',
        label: _this.$t('vendorMod.relegation.documentStatus'),
        dataType: 'dict',
        code: 'CONTRACT_INVOICE_STATUS',
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
        prop: 'taxAmount',
        label: _this.$t('purSettlementMod.taxTotalAmount'),
        minWidth: 150
      },
      {
        prop: 'createdFullName',
        label: _this.$t('common.creator'),
        minWidth: 120
      },
      {
        prop: 'creationDate',
        label: _this.$t('common.creationTime'),
        minWidth: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'operation',
        label: () => _this.$t('common.operation'), // 操作
        width: 120,
        btnStyle: 'text',
        showType: 'buttons',
        buttons: [
          {
            callback: function (row) {
              this.editTab('edit', row)
            }.bind(this),
            formattor (val) {
              return this.$t('common.edit')
            },
            show: row => {
              return ['DRAFT', 'REJECTED'].includes(row.status)
            }
          },
          {
            callback: function (row) {
              this.deleteOne(row)
            }.bind(this),
            formattor (val) {
              return this.$t('components.common.delete')
            },
            show: row => {
              return ['DRAFT'].includes(row.status)
            }
          },
          {
            callback: function (row) {
              this.abandonHandle(row)
            }.bind(this),
            formattor (val) {
              return this.$t('components.approvalHead.headers.abandon')
            },
            show: row => {
              return ['REJECTED'].includes(row.status) && this.curRole === 'VENDOR'
            }
          },
          {
            callback: function (row) {
              this.editTab('manage', row)
            }.bind(this),
            formattor (val) {
              return this.$t('bidMod.management')
            },
            show: row => {
              return ['SUPPLIER_SUBMITTED'].includes(row.status) && this.curRole === 'BUYER'
            }
          },
          {
            callback: function (row) {
              this.payment(row)
            }.bind(this),
            formattor (val) {
              return this.$t('cusEntry.supplement20250211.paymentRequest') // 付款申请
            },
            show: row => {
              const payment = parseFloat(row.invoiceCompPayTaxedAmount || 0) // 已付款金额
              const invoiced = parseFloat(row.taxAmount || 0) // 已开票金额
              if (payment < invoiced) {
                return ['APPROVED'].includes(row.status) && this.curRole === 'BUYER'
              } else {
                return false // 暂时
              }
          }
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader
    this.queryForm = this.queryForm.filter(item => item.prop !== 'vendorName')

    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    paymentApply (id) {
      this.$emit('tab-add', {
        component: paymentPlanDetail,
        params: {
          flag: 'readOnly',
          showType: 'approveNumber',
          paymentApplyId: id,
          tabName: 'paymentPlanDetail' + id
        },
        title: id,
        name: 'paymentPlanDetail' + id
      })
    },
    advanceApply (id) {
      this.$emit('tab-add', {
        component: advancePaymentDetail,
        params: {
          flag: 'readOnly',
          row: {
            advanceApplyId: id
          },
          showType: 'approveNumber',
          tabName: 'advancePaymentDetail' + id
        },
        title: id,
        name: 'advancePaymentDetail' + id
      })
    },
    num (nums) {
      return parseInt(nums) ? parseInt(nums) : ''
    },
    payment (row) {
      let con = null
      if (row.paymentStage == 'ADVANCE_CHARGE') {
        con = 'advancePayment'
      } else {
        con = 'purPaymentApply'
      }
      this.$router.push({
        name: con,
        params: {
          from: 'contractPerformancePlan',
          row: row,
          form: row
        }
      })
    },
    percentage (paymentAmount, stagePaymentAmount) {
      const payment = parseFloat(paymentAmount || 0)
      const stagePayment = parseFloat(stagePaymentAmount || 0)
      let aws = ((payment / stagePayment).toFixed(2)) * 100
      aws = aws < 100 ? aws : 100
      if (payment === 0) {
        aws = 0
      }
      return aws
    },
    customColorMethod (percentage) {
      if (percentage < 100) {
        return '#1997f2'
      } else if (percentage == 100) {
        return '#67c23a'
      }
    },
    abandonHandle (row) {
      // 确认废弃此数据?
      this.$confirm(this.$t('cusEntry.supplement20250205.confirmDiscardData'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
          this.$api.cmPerform.vendor.inv.performInvoice.abandon(row.perInvoiceId).then((res) => {
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
      let name = row.invoiceNo || ''
      let tab = {
        component: ContractPerformanceInvoiceDetail,
        params: {
          flag: type,
          row,
          tabName: this.$t('cusEntry.supplement20250211.performContractBilling') + name // 履约开票
        },
        title: name ? this.$t('cusEntry.supplement20250211.performContractBilling') + name : this.$t('route.newContractPerformanceInvoice'),
        name: this.$t('cusEntry.supplement20250211.performContractBilling') + name // 履约开票
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
          this.$api.cmPerform.vendor.inv.performInvoice.delete(row.perInvoiceId).then((res) => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    }
  }
}
</script>
