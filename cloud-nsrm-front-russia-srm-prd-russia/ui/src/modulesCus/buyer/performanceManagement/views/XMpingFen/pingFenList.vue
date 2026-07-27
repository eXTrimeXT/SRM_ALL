<template>
  <el-container
    class="flex-container the_contractTemplateList_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
      />
      <MainHeader>
        <template slot="left">
          <!-- <el-button
            type="primary"
            @click="addOne"
          >
            {{ $t('common.add') }}
          </el-button> -->
        </template>
      </MainHeader>

      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-pef/projectScoreMan/listPage"
      />
      <el-dialog
        :title="$t('common.tips')"
        :visible.sync="dialogVisible"
      >
        <div v-if="calculateMessage.length > 0">
          <li
            v-for="item in calculateMessage"
            :key="item"
            style="list-style: none"
          >
            {{ item }}
          </li>
          <li>{{ $t('perfMod.continueCalculation') }}</li>
        </div>
        <div v-else>
          <span>{{ $t('perfMod.confirmCalculate') }}</span>
        </div>

        <span
          slot="footer"
          class="dialog-footer"
        >
          <el-button @click="dialogVisible = false">{{ $t('common.cancel') }}</el-button>
          <el-button
            type="primary"
            @click="confirmCalculateVisible"
          >{{
            $t('common.confirm')
          }}</el-button>
        </span>
      </el-dialog>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import pingfenDetail from './pingfenDetail'
import { performanceManagement } from 'modb@/performanceManagement/api/index'

export default {
  name: 'PerformanceScoreItemsList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    pingfenDetail
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      scoreItemsIdConfirm: null,
      perEndMonth: null,
      perStartMonth: null,
      scoreEndTime: null,
      scoreStartTime: null,
      calculateMessage: [],
      dialogVisible: false,
      name: 'performanceScoreItemsList',
      tableName: 'performanceScoreItemsList',
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      currentRow: null,
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      statusList: [], // 项目状态
      perfPeriod: [], // 期间
      approveStatus: [], // 期间
      isModify: false,
      preArr: [
        { prop: 'projectName', label: '评分项目名称' },
        { prop: 'contractName', label: '合同名称' },
        { prop: 'companyName', label: '供应商名称' },
        {
          prop: 'performanceCode',
          label: '履约阶段',
          type: 'dict',
          code: 'MILESTONE_SCHEDULE'
        },
        {
          prop: 'approveStatus',
          label: '审批状态', // 项目状态
          type: 'dict', // 字典类型
          code: 'PROJECT_SCORE_MAN_STATUS' // 字典code
        }
      ],
      queryParam: {},
      pubRangeList: [],
      projectTypeList: []
    }
  },
  watch: {
    $route: {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler () {
        if (
          this.$route.params.from === 'fromFun' &&
          this.$route.params.funName === 'XMpingFen'
        ) {
          let row = {
            ...this.$route.params.row,
            projectScoreManId: this.$route.params.formId,
            projectName: this.$route.params.formNo
          }
          if (this.$route.params.taskIndex === 1) {
            this.editTab('edit', row)
          } else {
            this.editTab('view', row)
          }
        }
      }
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'projectName',
        label: () => this.$t('perfMod.projectName2'),
        minWidth: 150,
        formattor (val) {
          return val || '--'
        }
      },
      { prop: 'contractNo', label: '合同编码', width: 120 },
      { prop: 'contractName', label: '合同名称', width: 120 },
      { prop: 'categoryName', label: '品类', width: 120 },
      {
        prop: 'performanceCode',
        label: '履约阶段',
        width: 120,
        dataType: 'dict', // 数据类型为字典
        code: 'MILESTONE_SCHEDULE' // 字典code
      },
      { prop: 'companyName', label: '供应商名称', width: 120 },
      { prop: 'ouOrganizationName', label: '公司', width: 120 },
      {
        prop: 'approveStatus',
        label: '审批状态',
        width: 120,
        dataType: 'dict', // 数据类型为字典
        code: 'PROJECT_SCORE_MAN_STATUS' // 字典code
      },
      { prop: 'scoreManName', label: '评分人', width: 120 },
      {
        prop: 'perStartMonth',
        label: '评分开始时间',
        width: 120
      },
      {
        prop: 'perEndMonth',
        label: '评分结束时间',
        width: 120
      },
      {
        prop: 'scoreDate',
        label: '实际评分时间',
        width: 120
      },
      {
        prop: 'extCancelStatus',
        label: () => this.$t('cusEntry.common.extCancelStatus'),
        width: 120,
        formattor: val => {
          return val == '1' ? '是' : '否'
        }
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'),
        width: 240,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: function (row) {
              this.editTab('edit', row)
            }.bind(this),
            formattor (val) {
              return '评分'
            },
            show: function (row) {
              // 拟定和撤回的单据可以编辑
              if (['DRAFT', 'FLOW_REJECT', 'CHECK_REJECT'].includes(row.approveStatus)) {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.editTab('view', row)
            }.bind(this),
            formattor (val) {
              return '查看'
            },
            show: function (row) {
              // 结果未发布
              if (!['DRAFT', 'FLOW_REJECT', 'CHECK_REJECT'].includes(row.approveStatus)) {
                return true
              } else {
                return false
              }
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
    getQuerydata (v) {
      this.queryParam = JSON.parse(JSON.stringify(v || {}))
      let { creationDate } = this.queryParam
      if (creationDate && creationDate.length) {
        this.queryParam.startDate = creationDate[0]
        this.queryParam.endDate = creationDate[1]
      }
      delete this.queryParam.creationDate
      // this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    addOne () {
      this.$emit('tab-add', {
        component: pingfenDetail,
        params: {
          flag: 'add',
          tabName: 'pingfenDetail'
        },
        title: this.$t('perfMod.performanceScoringProgram '),
        name: 'pingfenDetail'
      })
    },
    copyProject (row) {
      performanceManagement.ceeaCopyProject({ scoreItemsId: row.scoreItemsId }).then((res) => {
        if (res) {
          this.$message.success(res.message)
          this.$refs[this.gridId].query()
        }
      })
    },
    confirmCalculateVisible () {
      let scoreItemsId = this.scoreItemsIdConfirm
      let approveStatus = 'SCORE_CALCULATED'
      performanceManagement.calculateScoreItems({
        scoreItemsId,
        approveStatus,
        perEndMonth: this.perEndMonth,
        perStartMonth: this.perStartMonth,
        scoreEndTime: this.scoreEndTime,
        scoreStartTime: this.scoreStartTime
      })
        .then((res) => {
          this.$message.success(res.message)
          this.getQuerydata()
          this.dialogVisible = false
          this.perEndMonth = null
          this.perStartMonth = null
          this.scoreEndTime = null
          this.scoreStartTime = null
          this.scoreItemsIdConfirm = null
        })
    },
    rowHandel (type, row) {

    },
    calculateOne (row) {
      this.$message.success(this.$t('perfMod.performanceScoreCalculation'))
    },
    publishOne (row) {
      this.$message.success(this.$t('perfMod.performanceScore'))
    },
    noticeOne (row) {
      this.$message.success(this.$t('perfMod.informedScore'))
    },
    delRowData (row) {
      this.$confirm(this.$t('perfMod.scrapPerformance'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$message.success(this.$t('perfMod.abandonmentComplete'))
        })
        .catch(() => {})
    },
    // 查看 编辑
    editTab (type, row) {
      this.$emit('tab-add', {
        component: pingfenDetail,
        params: {
          flag: type,
          row,
          tabName: 'pingfenDetail' + row.projectScoreItemsId
        },
        title: row.projectName,
        name: 'pingfenDetail' + row.projectScoreItemsId
      })
    },
    editOne (row) {
      this.$emit('tab-add', {
        component: pingfenDetail,
        params: {
          flag: 'edit',
          orderId: row.scoreItemsId,
          tabName: 'pingfenDetail' + row.scoreItemsId
        },
        title: row.projectName,
        name: 'pingfenDetail' + row.scoreItemsId
      })
    },
    enableOne () {},
    disableOne () {},
    deleteOne () {}
  }
}
</script>
<style scoped lang="scss"></style>
