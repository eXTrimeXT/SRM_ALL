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
              $t("accountMod.createDeductionPenatly")
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
import penaltyDeductionOrderDetail from './penaltyDeductionOrderDetail'
import { parseTime } from '@/utils'

export default {
  name: 'PenaltyDeductionOrderList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    penaltyDeductionOrderDetail
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      name: 'penaltyDeductionOrderList',
      tableName: 'penaltyDeductionOrderList',
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
        { prop: 'deductionBill', label: this.$t('accountMod.deductionBill') }, // 扣罚款单据
        {
          prop: 'deductionType',
          label: this.$t('accountMod.deductionType'), // 扣罚款类型
          type: 'dict',
          code: 'PROJECT_TYPE'
        },
        { prop: 'sourceBill',
          label: this.$t('accountMod.sourceBill'),
          type: 'dict',
          code: 'PUBLISH_RANGE'
        }, // 来源单据
        {
          prop: 'companyName',
          label: this.$t('common.vendorName'), // 供应商名称
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_display'
        },
        {
          prop: 'deductionDate',
          label: this.$t('accountMod.deductionDate'),
          type: 'daterange'
        }, // 扣罚款日期
        {
          prop: 'deductionStatus',
          label: this.$t('accountMod.deductionStatus'),
          type: 'dict',
          code: 'PROJ_STATUS'
        } // 扣罚状态
      ],
      queryParam: {}
    }
  },
  // 备注:该功能所有字典信息为空
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'vendorName',
        label: this.$t('common.vendorName'), // 供应商名称
        minWidth: 150
      },
      {
        prop: 'deductionOrderNo',
        label: this.$t('accountMod.deductionOrderNo'), // 扣罚款单号
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
        prop: 'deductionType',
        label: this.$t('accountMod.deductionType'), // 扣罚款类型
        width: 120,
        dataType: 'dict',
        code: 'PROJECT_TYPE'
      },
      {
        prop: 'sourceNum',
        label: this.$t('contractMod.sourceNumber'),
        width: 100
      }, // 来源单号
      {
        prop: 'deductionNoTaxAmount',
        label: this.$t('accountMod.deductionNoTaxAmount'),
        width: 130
      }, // 扣罚款税前金额
      {
        prop: 'deductionTaxRate',
        label: this.$t('accountMod.deductionTaxRate'),
        width: 120
      }, // 扣罚款税率
      {
        prop: 'deductionTaxAmount',
        label: this.$t('accountMod.deductionTaxAmount'),
        width: 130
      }, // 扣罚款税后金额
      {
        prop: 'deductionStatus',
        label: this.$t('accountMod.deductionStatus'), // 扣罚状态
        width: 100,
        dataType: 'dict',
        code: 'PROJ_STATUS'
      },
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
    addOne () {
      this.$emit('tab-add', {
        component: penaltyDeductionOrderDetail,
        params: {
          flag: 'add'
        },
        title: this.$t('accountMod.createDeductionPenatly'), // 创建扣罚款
        name: 'penaltyDeductionOrderDetail'
      })
    },
    editOne (row) {
      this.$emit('tab-add', {
        component: penaltyDeductionOrderDetail,
        params: {
          flag: 'edit',
          row: row
        },
        title: row.projName,
        name: 'penaltyDeductionOrderDetail' + row.projName
      })
    },
    enableOne () {},
    disableOne () {},
    deleteOne () {}
  }
}
</script>
<style scoped lang="scss"></style>
