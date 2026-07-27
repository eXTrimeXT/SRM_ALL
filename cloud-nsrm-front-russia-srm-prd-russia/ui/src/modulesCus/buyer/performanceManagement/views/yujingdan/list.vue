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
        url="/api-pef/projectScoreWarning/listPage"
      >
        <template #createdBy="{ scope }">
          {{ scope.row?.createdFullName }} {{scope.row?.createdBy}}
        </template>
      </TableView>
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
import performanceScoreItemsDetail from './edit'
import { performanceManagement } from 'modb@/performanceManagement/api/index'
import performanceModelDetail from 'modc@/buyer/performanceManagement/views/XMorderReview/orderReviewDetail'

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
      curRole: this.$store.getters.userType,
      buyerTableHeader: [],
      suplierTableHeader: [],
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
        { prop: 'warningCode', label: '预警单号' },
        { prop: 'companyName', label: '供应商名称' },
        {
          prop: 'perfModelType',
          label: '预警类型',
          type: 'dict', // 字典类型
          code: 'PERF_MODEL_TYPE' // 字典code
        },
        {
          prop: 'bidManagerFullPath',
          label: '招标负责人部门',
        },
        { prop: 'contractManagerFullPath', label: '合同经办人部门' }
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

      }
    }
  },
  mounted () {
    let _this = this
    // 采购商列表
    this.buyerTableHeader = [
      {
        prop: 'warningCode',
        label: '预警单号',
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
      {
        prop: 'projectName',
        label: '评分项目名称',
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.projectNameFunction(row)
        }.bind(this),
        formattor (val) {
          return val || '--'
        }
      },
      {
        prop: "warningStatus",
        label: '预警状态',
        width: 120,
        dataType: 'dict', // 数据类型为字典
        code: 'PERF_WARNING_STATUS' // 字典code
      },
      {
        prop: "perfModelType",
        label: '预警类型',
        width: 120,
        dataType: 'dict', // 数据类型为字典
        code: 'PERF_MODEL_TYPE' // 字典code
      },
      { prop: "ouOrganizationName", label: '公司', width: 120 },
      { prop: "bidManager", label: '招标负责人', width: 120 },
      { prop: "bidManagerFullPath", label: '招标负责人公司部门', width: 120 },
      { prop: "contractManager", label:'合同经办人', width: 120 },
      { prop: "contractManagerFullPath", label:'合同经办人公司部门', width: 120 },
      {
        prop: "createdBy",
        label:'创建人',
        width: 120,
        showType: 'slot',
        slot: 'createdBy'
      },
      {
        prop: 'readStatus',
        label: '查看状态',
        width: 100,
        dataType: 'dict', // 数据类型为字典
        code: 'PERF_READ_STATUS' // 字典code
      },
      {
        prop: 'creationDate',
        label: '创建时间',
        width: 100
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
              return '编辑'
            },
            show: function (row) {
              // 拟定和撤回的单据可以编辑
              if (row.warningStatus === 'DRAFT' && _this.$store.getters.userType == 'BUYER') {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.delRowData(row)
            }.bind(this),
            formattor (val) {
              return '删除'
            },
            show: function (row) {
              // 拟定和撤回的单据可以编辑
              if (row.warningStatus === 'DRAFT' && _this.$store.getters.userType == 'BUYER') {
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
              if (row.warningStatus !== 'DRAFT' || _this.$store.getters.userType != 'BUYER') {
                return true
              } else {
                return false
              }
            }
          }
        ]
      }
    ]
    // 供应商列表
    this.suplierTableHeader = [
      {
        prop: 'warningCode',
        label: '预警单号',
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
      {
        prop: "warningStatus",
        label: '预警状态',
        width: 120,
        dataType: 'dict', // 数据类型为字典
        code: 'PERF_WARNING_STATUS' // 字典code
      },
      {
        prop: "perfModelType",
        label: '预警类型',
        width: 120,
        dataType: 'dict', // 数据类型为字典
        code: 'PERF_MODEL_TYPE' // 字典code
      },
      { prop: "createdBy", label:'创建人', width: 120 },
      {
        prop: 'readStatus',
        label: '查看状态',
        width: 100,
        dataType: 'dict', // 数据类型为字典
        code: 'PERF_READ_STATUS' // 字典code
      },
      {
        prop: 'creationDate',
        label: '创建时间',
        width: 100
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
              return '编辑'
            },
            show: function (row) {
              // 拟定和撤回的单据可以编辑
              if (row.warningStatus === 'DRAFT' && _this.$store.getters.userType == 'BUYER') {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.delRowData(row)
            }.bind(this),
            formattor (val) {
              return '删除'
            },
            show: function (row) {
              // 拟定和撤回的单据可以编辑
              if (row.warningStatus === 'DRAFT' && _this.$store.getters.userType == 'BUYER') {
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
              if (row.warningStatus !== 'DRAFT' || _this.$store.getters.userType != 'BUYER') {
                return true
              } else {
                return false
              }
            }
          }
        ]
      }
    ]
    if (this.curRole == 'BUYER') {
      this.tableHeader = this.buyerTableHeader
    } else {
      this.tableHeader = this.suplierTableHeader
      this.preArr = [
        { prop: 'warningCode', label: '预警单号' }
      ]
    }
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    projectNameFunction (row) {
      console.log(row)
      this.$emit('tab-add', {
        component: performanceModelDetail,
        params: {
          flag: 'view',
          row,
          tabName: 'performanceScoreItemsDetail' + row.projectScoreItemsId
        },
        title: row.projectName,
        name: 'performanceScoreItemsDetail' + row.projectScoreItemsId
      })
    },
    getQuerydata (v) {
      this.queryParam = JSON.parse(JSON.stringify(v || {}))
      console.log(this.$store.getters, 'getters')
      if (this.curRole != 'BUYER') {
        this.queryParam['companyId'] = this.$store.getters.userInfo?.companyId
      }
      let { creationDate } = this.queryParam
      if (creationDate && creationDate.length) {
        this.queryParam.createStartDate = creationDate[0]
        this.queryParam.createEndDate = creationDate[1]
      }
      delete this.queryParam.creationDate

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
        title: '新增预警单',
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
      console.log(row)
      this.$confirm('确认删除该单据', {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: `/api-pef/projectScoreWarning/delete`,
            method: 'DELETE',
            params: {
              warningId: row?.warningId
            },
            loading: true
          }).then( res  => {
            this.$message.success('删除成功')
            this.$refs[this.gridId].query()
          })
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
          tabName: 'performanceScoreItemsDetail' + row.warningCode
        },
        title: row.warningName,
        name: 'performanceScoreItemsDetail' + row.warningCode
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
