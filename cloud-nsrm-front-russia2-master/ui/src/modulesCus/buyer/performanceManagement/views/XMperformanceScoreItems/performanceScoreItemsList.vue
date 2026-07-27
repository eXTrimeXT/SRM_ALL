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
          <!--          <el-button-->
          <!--            type="primary"-->
          <!--            @click="addOne"-->
          <!--          >-->
          <!--            {{ $t('common.add') }}-->
          <!--          </el-button>-->
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
        url="/api-pef/pj/projectScoreItems/listPage"
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
import performanceScoreItemsDetail from './performanceScoreItemsDetail'
import { performanceManagement } from 'modb@/performanceManagement/api/index'

export default {
  name: 'PerformanceScoreItemsList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    performanceScoreItemsDetail
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
        { prop: 'contractName', label: this.$t('vendorMod.contractName') },  // '合同名称'
        { prop: 'bidCode', label: this.$t('cusEntry.bidMod.extProjectNo') },  // '招标编号'
        {
          prop: 'performanceCode',
          label: this.$t('cusEntry.supplement20250121.performanceStage'),  // '履约阶段'
          type: 'dict',
          code: 'MILESTONE_SCHEDULE'
        },
        { prop: 'projectName', label:this.$t('perfMod.projectName2') },//  '评分项目名称'
        {
          prop: 'projectStatus',
          label: () => this.$t('perfMod.projectStatus'), // 项目状态
          type: 'dict', // 字典类型
          code: 'PROJECT_SCORE_ITEM_STATUS' // 字典code
        },
        {
          prop: 'creationDate',
          label: () => this.$t('common.creationTime'), // 创建时间
          type: 'daterange'
        },
        { prop: 'buOrganizationName', label: this.$t('cusEntry.bidSuperviseReport.extOrgBuName') },  // '板块'
        { prop: 'extInvestNo', label: this.$t('cusEntry.bidMod.investNum') }  // '投资编号'
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
          this.$route.params.funName === 'performanceScoreItems'
        ) {
          // let scoreItemsId = Number(this.$route.params.fdFormInstanceId);
          // let fdSubject = this.$route.params.fdSubject; // 流程标题
          let scoreItemsId = Number(this.$route.params.formId)
          let formNo = this.$route.params.formNo // 流程标题
          let title = this.$route.params.row.title
          let row = {
            ...this.$route.params,
            scoreItemsId,
            projectName: formNo // tab 标题显示
          }
          this.$emit('tab-add', {
            component: performanceScoreItemsDetail,
            params: {
              flag: 'approve',
              orderId: row.scoreItemsId,
              tabName: 'performanceScoreItemsDetail' + row.scoreItemsId
            },
            title: title,
            name: 'performanceScoreItemsDetail' + row.scoreItemsId
          })
        }
        if (this.$route.params.from === 'contract') {
          const contractNo = this.$route.params.row?.contractNo
          const contractId = this.$route.params.row?.contractId
          const milestoneType = this.$route.params.row?.milestoneType
          this.$emit('tab-add', {
            component: performanceScoreItemsDetail,
            params: {
              flag: 'add',
              contractNo,
              milestoneType,
              contractId,
              tabName: 'performanceScoreItemsDetail' + contractNo
            },
            title: contractNo,
            name: 'performanceScoreItemsDetail' + contractNo
          })
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
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.editTab('view', row)
        }.bind(this),
        formattor (val) {
          return val || '--'
        }
      },
      { prop: 'contractNo', label: this.$t('cusEntry.supplement20250205.contractNumber'), width: 120 }, // '合同单号'
      { prop: 'contractName', label: this.$t('vendorMod.contractName'), width: 120 }, // '合同名称'
      { prop: 'ouOrganizationName', label: this.$t('components.organization.COMPANY'), width: 120 }, //  '公司'
      { prop: 'companyCode', label: this.$t('common.vendorCode'), width: 120 }, // '供应商编码'
      { prop: 'companyName', label: this.$t('common.companyName'), width: 120 },  // '供应商名称'
      { prop: 'bidCode', label: this.$t('cusEntry.bidMod.extProjectNo'), width: 120 },  // '招标编号'
      { prop: 'extInvestNo', label: this.$t('cusEntry.bidMod.investNum'), width: 120 },  // '投资编号'
      {
        prop: 'projectStatus',
        label: () => this.$t('perfMod.projectStatus'),
        width: 100,
        dataType: 'dict', // 数据类型为字典
        code: 'PROJECT_SCORE_ITEM_STATUS' // 字典code
      },
      {
        prop: 'performanceCode',
        label: this.$t('cusEntry.supplement20250121.performanceStage'),  // '履约阶段'
        width: 100,
        dataType: 'dict', // 数据类型为字典
        code: 'MILESTONE_SCHEDULE' // 字典code
      },
      {
        prop: 'perStartMonth',
        label: this.$t('perfMod.scoreStartTime'),  // '评分开始时间'
        width: 100
      },
      {
        prop: 'perEndMonth',
        label: this.$t('perfMod.scoreEndTime'),  // '评分截止时间'
        width: 100
      },
      {
        prop: 'creationDate',
        label: () => this.$t('perfMod.creationDate'),
        width: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'extCancelStatus',
        label: () => this.$t('cusEntry.common.extCancelStatus'),
        width: 120,
        formattor: val => {
          return val == '1' ? this.$t('common.yes') : this.$t('common.no')
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
              return _this.$t('perfMod.compile')
            },
            show: function (row) {
              // 拟定和撤回的单据可以编辑
              if (row.projectStatus === 'DRAFT') {
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
              return this.$t('common.view')
            },
            show: function (row) {
              // 结果未发布
              if (row.projectStatus !== 'DRAFT') {
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
        this.queryParam.createStartDate = creationDate[0]
        this.queryParam.createEndDate = creationDate[1]
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
        component: performanceScoreItemsDetail,
        params: {
          flag: 'add',
          tabName: 'performanceScoreItemsDetail'
        },
        title: this.$t('perfMod.performanceScoringProgram '),
        name: 'performanceScoreItemsDetail'
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
      let projectStatus = 'SCORE_CALCULATED'
      performanceManagement.calculateScoreItems({
        scoreItemsId,
        projectStatus,
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
        component: performanceScoreItemsDetail,
        params: {
          flag: type,
          row,
          tabName: 'performanceScoreItemsDetail' + row.projectScoreItemsId
        },
        title: row.projectName,
        name: 'performanceScoreItemsDetail' + row.projectScoreItemsId
      })
    },
    editOne (row) {
      this.$emit('tab-add', {
        component: performanceScoreItemsDetail,
        params: {
          flag: 'edit',
          orderId: row.scoreItemsId,
          tabName: 'performanceScoreItemsDetail' + row.scoreItemsId
        },
        title: row.projectName,
        name: 'performanceScoreItemsDetail' + row.scoreItemsId
      })
    },
    enableOne () {},
    disableOne () {},
    deleteOne () {}
  }
}
</script>
<style scoped lang="scss"></style>
