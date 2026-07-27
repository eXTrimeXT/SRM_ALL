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
      <p style="padding-left: 11px; margin: 3px">
        <el-button
          type="primary"
          @click="addOne"
        >
          {{ $t('bidMod.affairsIncreased') }}
        </el-button>
      </p>

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
import returnGoodsBillDetail from './returnGoodsBillDetail'
import { parseTime } from '@/utils'

export default {
  name: 'ReturnGoodsBillList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    returnGoodsBillDetail
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      name: 'returnGoodsBillList',
      tableName: 'returnGoodsBillList',
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
        { prop: 'returnBill', label: '退货单号' },
        { prop: 'contractName', label: '合同名称' },
        { prop: 'contractNum', label: '合同编号' },
        { prop: 'status', label: '退货状态', type: 'dict', code: 'PROJ_STATUS' },
        {
          prop: 'companyName',
          label: '供应商名称',
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_display_buyer'
        }
      ],
      queryParam: {}
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      { prop: 'status', label: '状态', width: 100, dataType: 'dict', code: 'PROJ_STATUS' },
      {
        prop: 'templateType',
        label: '验收类型',
        width: 100,
        dataType: 'dict',
        code: 'PROJECT_TYPE'
      },
      {
        prop: 'inspectionBill',
        label: '验收单号',
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
      { prop: 'vendorName', label: '供应商名称', minWidth: 150 },
      { prop: 'contractNum', label: '合同编号', width: 150 },
      {
        prop: 'inspectionDate',
        label: '验收日期',
        width: 100,
        formattor (val) {
          return val ? parseTime(val) : ''
        }
      },
      {
        prop: 'endDate',
        label: '实际完成日期',
        width: 100,
        formattor (val) {
          return val ? parseTime(val) : ''
        }
      },
      { prop: 'contractAmount', label: '合同金额', width: 100 },
      { prop: 'inspectionAmount', label: '实际验收金额', width: 100 },
      { prop: 'inspector', label: '验收人', width: 100 },
      { prop: 'enableDate', label: '实际验收情况', width: 100 },
      { prop: 'purchaseNum', label: '采购单号', width: 100 },
      { prop: 'quanlityStandard', label: '质量标准', width: 100 },
      {
        prop: 'createdUserName', // createdBy
        label: '创建人',
        width: 100
      },
      {
        prop: 'creationDateTime',
        label: '创建时间',
        width: 150,
        formattor (val) {
          return val ? parseTime(val) : ''
        }
      },
      {
        prop: 'lastUpdatedUserName', // lastUpdatedBy
        label: '最后更新人',
        width: 100
      },
      {
        prop: 'lastUpdateDateTime',
        label: '最后更新时间',
        width: 150,
        formattor (val) {
          return val ? parseTime(val) : ''
        }
      },
      {
        prop: 'operation',
        label: '操作',
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
              return '审核'
            }
          },
          {
            callback: function (row) {
              this.editOne(row)
            }.bind(this),
            formattor (val) {
              return '驳回'
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
        component: returnGoodsBillDetail,
        params: {
          flag: 'add'
        },
        title: '创建合同模板',
        name: 'returnGoodsBillDetail'
      })
    },
    editOne (row) {
      this.$emit('tab-add', {
        component: returnGoodsBillDetail,
        params: {
          flag: 'edit',
          row: row
        },
        title: row.projName,
        name: 'returnGoodsBillDetail' + row.projName
      })
    },
    enableOne () {},
    disableOne () {},
    deleteOne () {}
  }
}
</script>
<style scoped lang="scss"></style>
