<template>
  <el-container
    class="flex-container the_contractTemplateList_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
      />

      <main-header
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <el-button
            type="primary"
            @click="addOne"
          >
            {{ $t('common.add') }}
          </el-button>
        </template>
      </main-header>

      <table-view
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-bid/techDiscuss/techDiscussReply/listPage"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import paymentPlanDetail from './paymentPlanDetail'
import { parseTime } from '@/utils'

export default {
  name: 'PaymentPlanList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    paymentPlanDetail
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      name: 'contractTemplateTable',
      tableName: 'paymentPlanList',
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      currentRow: null,
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      isModify: false,
      preArr: [
        {
          prop: 'companyName',
          label: this.$t('common.vendorName'), // 供应商名称
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_display'
        },
        {
          prop: 'purorg',
          label: this.$t('common.orgName') // 采购组织
        },
        {
          prop: 'billStatus',
          label: this.$t('bidMod.billstatus'),
          type: 'dict',
          code: 'PROJ_STATUS'
 // 单据状态
        },
        {
          prop: 'paymentType',
          label: this.$t('contractMod.payType') // 付款类型
        },
        {
          prop: 'sourceNum',
          label: this.$t('contractMod.sourceNumber') // 来源单号
        },
        {
          prop: 'billDate',
          label: this.$t('qualitySynergy.orderDate'),
          type: 'daterange' // 单据日期
        },
        {
          prop: 'creationDate',
          label: this.$t('contractMod.plannedPaymentDate'),
          type: 'daterange' // 计划付款日期
        }
      ],
      queryParam: {}
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'paymentBill',
        label: this.$t('contractMod.paymentPlanNumber'), // 付款计划单号
        width: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.editTab(row)
        }.bind(this),
        formattor (val) {
          return val || '--'
        }
      },
      {
        prop: 'paymentDate',
        label: this.$t('contractMod.plannedPaymentDate'), // 计划付款日期
        width: 120,
        formattor (val) {
          return val ? parseTime(val) : ''
        }
      },
      {
        prop: 'paymentType',
        label: this.$t('contractMod.payType'), // 付款类型
        width: 100,
        dataType: 'dict',
        code: 'PROJECT_TYPE'
      },
      {
        prop: 'billDate',
        label: this.$t('qualitySynergy.orderDate'), // 单据日期
        width: 100,
        formattor (val) {
          return val ? parseTime(val) : ''
        }
      },
      {
        prop: 'currency',
        label: this.$t('contractMod.currencyCode'),
        width: 100
      }, // 币种
      { prop: 'taxRate', label: this.$t('contractMod.taxRate'), width: 100 }, // 税率
      {
        prop: 'billAmount',
        label: this.$t('contractMod.totalAmountNoTax2'),
        width: 120
      }, // 未税总金额
      {
        prop: 'billAmount',
        label: this.$t('contractMod.totalAmountTax'),
        width: 120
      }, // 含税总金额
      { prop: 'billStatus', label: this.$t('bidMod.billstatus'), width: 100 }, // 单据状态
      { prop: 'purorg', label: this.$t('contractMod.fullPathId'), width: 100 }, // 采购组织
      {
        prop: 'createdUserName', // createdBy
        label: this.$t('common.creator'),
        width: 100
      },
      {
        prop: 'operation',
        label: this.$t('common.operation'),
        width: 180,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: function (row) {
              this.editOne(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.edit') // 编辑
            }
          },
          {
            callback: function (row) {
              this.editOne(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('accountMod.review') // 审核
            }
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader

    this.$nextTick(() => {
      // this.getQuerydata()
    })
  },
  methods: {
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    addOne () {
      this.$emit('tab-add', {
        component: paymentPlanDetail,
        params: {
          flag: 'add'
        },
        title: this.$t('purSettlementMod.newPaymentPlan'), // 创建付款计划
        name: 'paymentPlanDetail'
      })
    },
    editOne (row) {
      this.$emit('tab-add', {
        component: paymentPlanDetail,
        params: {
          flag: 'edit',
          row: row
        },
        title: row.projName,
        name: 'paymentPlanDetail' + row.projName
      })
    },
    enableOne () {},
    disableOne () {},
    deleteOne () {}
  }
}
</script>
<style scoped lang="scss"></style>
