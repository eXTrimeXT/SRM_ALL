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
            {{
              $t("accountMod.createStatement")
            }}
          </el-button>
          <el-button
            type="primary"
            @click="importOne"
          >
            {{
              $t("accountMod.importStatement")
            }}
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
import statementBillDetail from './statementBillDetail'
import { adaptDictData, parseTime } from '@/utils'

export default {
  name: 'StatementBillList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    statementBillDetail
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      name: 'statementBillList',
      tableName: 'statementBillList',
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
          prop: 'purOrg',
          label: this.$t('common.orgName') // 采购组织
        },
        {
          prop: 'statementStatus',
          label: this.$t('purSettlementMod.statementStatus'),
          type: 'dict',
          code: 'PROJECT_TYPE'
 // 对账状态
        },
        {
          prop: 'statementDate',
          label: this.$t('accountMod.statementDate'),
          type: 'daterange'
        }, // 对账期间
        {
          prop: 'statementOrder',
          label: this.$t('purSettlementMod.statementNumber')
        } // 对账单号
      ],
      queryParam: {}
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'statementOrder',
        label: this.$t('purSettlementMod.statementNumber'), // 对账单号
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
        prop: 'vendorName',
        label: this.$t('common.vendorName'),
        minWidth: 150
      }, // 供应商名称
      { prop: 'vendorCode', label: this.$t('common.vendorCode'), width: 150 }, // 供应商编码
      {
        prop: 'statementStatus',
        label: this.$t('purSettlementMod.statementStatus'), // 对账状态
        width: 100,
        dataType: 'dict',
        code: 'PROJECT_TYPE'

      },
      {
        prop: 'inspectionDate',
        label: this.$t('purSettlementMod.statementStartTime'), //  对账开始日期
        width: 120,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'endDate',
        label: this.$t('purSettlementMod.statementEndTime'), // 对账结束日期
        width: 120,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'contractAmount',
        label: this.$t('accountMod.reconciliationTotal'),
        width: 120
      }, // 对账总数量
      {
        prop: 'inspectionAmount',
        label: this.$t('accountMod.reconciliationAmount'),
        width: 120
      }, // 对账总金额
      {
        prop: 'inspector',
        label: this.$t('accountMod.deductionAmount'),
        width: 100
      }, // 扣罚金额
      {
        prop: 'enableDate',
        label: this.$t('accountMod.advancePaymentAmount'),
        width: 120
      }, // 预付款金额
      {
        prop: 'purchaseNum',
        label: this.$t('bidMod.rejectReason1'),
        width: 100
      }, // 驳回原因
      { prop: 'purOrg', label: this.$t('common.orgName'), width: 100 }, // 采购组织
      {
        prop: 'createdUserName', // createdBy
        label: this.$t('common.creator'),
width: 100
      },
      {
        prop: 'creationDateTime',
        label: this.$t('common.creationTime'),
        width: 150,
        formattor (val) {
          return val ? parseTime(val) : ''
        }
      },
      {
        prop: 'lastUpdateBy',
        label: this.$t('contractMod.lastUpdatedBy'),
        width: 120
      },
      {
        prop: 'lastUpdateDateTime',
        label: this.$t('contractMod.lastUpdateDate'),
        width: 150,
        formattor (val) {
          return val ? parseTime(val) : ''
        }
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
              return _this.$t('accountMod.review') // 审核
            }
          },
          {
            callback: function (row) {
              this.editOne(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.toRefuse') // 驳回
            }
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader
    // 状态
    // 项目类型
    // 发布范围

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
    importOne () {},
    addOne () {
      this.$emit('tab-add', {
        component: statementBillDetail,
        params: {
          flag: 'add'
        },
        title: this.$t('accountMod.createStatement'),
        name: 'statementBillDetail'
      })
    },
    editOne (row) {
      this.$emit('tab-add', {
        component: statementBillDetail,
        params: {
          flag: 'edit',
          row: row
        },
        title: row.projName,
        name: 'statementBillDetail' + row.projName
      })
    },
    enableOne () {},
    disableOne () {},
    deleteOne () {}
  }
}
</script>
<style scoped lang="scss"></style>
