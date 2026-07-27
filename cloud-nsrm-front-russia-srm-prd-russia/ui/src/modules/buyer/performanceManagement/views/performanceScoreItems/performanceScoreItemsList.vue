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
          <el-button
            type="primary"
            @click="addOne"
          >
            {{ $t('common.add') }}
          </el-button>
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
        url="/api-pef/scoreproject/scoreItems/listPerfScoreItemsPage"
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
        { prop: 'projectName', label: () => this.$t('perfMod.projectName') },
        // {
        //   prop: "evaluationPeriod",
        //   label: () => this.$t("perfMod.evaluationPeriod"),
        //   type: "select",
        //   options: []
        // },
        {
          prop: 'projectStatus',
          label: () => this.$t('perfMod.projectStatus'),
          type: 'dict', // 字典类型
          code: 'PERF_PROJECT_STATUS' // 字典code
        },
        /* {
          prop: "approveStatus",
          label: () => this.$t("perfMod.approveStatus"),
          type: "select",
          options: []
        }, */
        { prop: 'templateName', label: () => this.$t('perfMod.templateName3') }
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
      // {
      //   prop: "evaluationPeriod",
      //   label: () => this.$t("perfMod.evaluationPeriod"),
      //   width: 100,
      //   formattor(val) {
      //     return _this.$getDictLabelByValue(_this.perfPeriod, val);
      //   }
      // },
      // { prop: "version", label: "版本号", width: 100 },
      {
        prop: 'projectStatus',
        label: () => this.$t('perfMod.projectStatus'),
        width: 100,
        dataType: 'dict', // 数据类型为字典
        code: 'PERF_PROJECT_STATUS' // 字典code
      },
      /* {
        prop: "approveStatus",
        label: () => this.$t("perfMod.approveStatus"),
        width: 100,
        formattor(val) {
          return _this.$getDictLabelByValue(_this.approveStatus, val);
        }
      }, */
      {
        prop: 'templateName',
        label: () => this.$t('perfMod.perModel'),
        width: 100
      },
      {
        prop: 'createdFullName',
        label: () => this.$t('perfMod.createdBy'),
        width: 100
      },
      {
        prop: 'scorePeople',
        label: () => this.$t('perfMod.scorePeople'),
        width: 100,
        getRow: true,
        formattor (data) {
          let scorePeopleCount = data.row.scorePeopleCount
          return data.col + '/' + scorePeopleCount
        }
      },
      {
        prop: 'creationDate',
        label: () => this.$t('perfMod.creationDate'),
        width: 150,
        formattor (val) {
          return val ? val.substr(0, 10) : ''
        }
      },
      {
        prop: 'scoreEndTime',
        label: () => this.$t('perfMod.scoreEndTime'),
        width: 150
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
              if (row.projectStatus === 'SCORE_DRAFT') {
                return true
              } else {
                return false
              }
            }
          },
          // {
          //   callback: function(row) {
          //     this.rowHandel("notice", row);
          //   }.bind(this),
          //   formattor: val => this.$t("perfMod.noticePeople"),
          //   show: function(row) {
          //     // 拟定和撤回的单据可以编辑
          //     if (row.projectStatus === "SCORE_DRAFT") {
          //       return true;
          //     } else {
          //       return false;
          //     }
          //   }
          // },
          {
            callback: function (row) {
              this.rowHandel('calculate', row) // 计算
            }.bind(this),
            formattor: (val) => this.$t('perfMod.rowCalculate'),
            show: function (row) {
              // 已通知评分的状态下可计算
              if (row.projectStatus === 'SCORE_NOTIFIED') {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.rowHandel('publish', row) // 发布
            }.bind(this),
            formattor (val) {
              return _this.$t('perfMod.release')
            },
            show: function (row) {
              // 结果未发布
              if (row.projectStatus === 'RESULT_NO_PUBLISHED' && row.approveStatus === 'APPROVED') {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.rowHandel('abandon', row) // 废弃
            }.bind(this),
            formattor (val) {
              return _this.$t('perfMod.abandon')
            },
            show: function (row) {
              // 拟定和撤回的单据可以编辑
              if (row.projectStatus === 'SCORE_DRAFT') {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.rowHandel('abandonAlready', row) // 废弃
            }.bind(this),
            formattor (val) {
              return _this.$t('perfMod.abandonPerformanceProjects')
            },
            show: function (row) {
              // 已通知状态的才会显示
              if (row.projectStatus === 'SCORE_NOTIFIED' && row.approveStatus !== 'ABANDONED') {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.rowHandel('delete', row)
            }.bind(this),
            formattor (val) {
              return _this.$t('perfMod.delete')
            },
            show: function (row) {
              // 作废和拟定的单据可以删除 || row.projectStatus === "SCORE_DRAFT"
              if (row.projectStatus === 'OBSOLETE') {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.rowHandel('viewResult', row)
            }.bind(this),
            formattor: (val) => this.$t('perfMod.readCalResule'),
            show: function (row) {
              // 结果已发布
              if (row.projectStatus === 'RESULT_PUBLISHED') {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              // 复制项目
              this.copyProject(row)
            }.bind(this),
            formattor: (val) => this.$t('perfMod.copyProject')
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
        component: performanceScoreItemsDetail,
        params: {
          flag: 'add',
          tabName: 'performanceScoreItemsDetail'
        },
        title: this.$t('perfMod.performanceScoringProgram '),
        name: 'performanceScoreItemsDetail'
      })
    },
    approve (row) {
      this.$emit('tab-add', {
        component: performanceScoreItemsDetail,
        params: {
          flag: 'approve',
          orderId: row.scoreItemsId,
          tabName: 'performanceScoreItemsDetail' + row.scoreItemsId
        },
        title: row.projectName,
        name: 'performanceScoreItemsDetail' + row.scoreItemsId
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
      let scoreItemsId = row.scoreItemsId
      let projectStatus = row.projectStatus
      if (type === 'calculate') {
        // 计算
        this.scoreItemsIdConfirm = row.scoreItemsId || ''
        this.perEndMonth = row.perEndMonth || ''
        this.perStartMonth = row.perStartMonth || ''
        this.scoreEndTime = row.scoreEndTime || ''
        this.scoreStartTime = row.scoreStartTime || ''
        performanceManagement.confirmBeforeCalculate({ scoreItemsId }).then((res) => {
          if (res.data) {
            this.calculateMessage = res.data
          } else {
            this.calculateMessage = []
            // 当前项目所有评分人已完成评分，请确认进行计算
          }
          this.dialogVisible = true
        })
      } else if (type === 'publish') {
        // 发布
        let projectStatus = 'RESULT_PUBLISHED'
        performanceManagement.publishScoreItems({ scoreItemsId, projectStatus }).then((res) => {
          this.$message.success(res.message)
          this.getQuerydata()
        })
      } else if (type === 'abandon') {
        // 废弃
        this.$confirm(this.$t('bidMod.bidsInquireDoc'), {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        })
          .then(() => {
            let projectStatus = 'OBSOLETE' // 任何项目状态都可以废弃
            performanceManagement.abandonScoreItems({ scoreItemsId, projectStatus }).then((res) => {
              this.$message.success(res.message)
              this.getQuerydata()
            })
          })
          .catch(() => { })
      } else if (type === 'notice') {
        // 通知
        this.$confirm(this.$t('perfMod.graderGrading'), this.$t('common.tips'), {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        })
          .then(() => {
            let projectStatus = 'SCORE_NOTIFIED'
            performanceManagement.notifyScorers({ scoreItemsId, projectStatus }).then((res) => {
              this.$message.success(res.message)
              this.getQuerydata()
            })
          })
          .catch(() => {
            this.$message({
              type: 'info',
              message: this.$t('perfMod.noticeCancellation')
            })
          })
      } else if (type === 'abandonAlready') {
        // 通知
        this.$confirm(this.$t('perfMod.confirmAbandonment'), this.$t('common.tips'), {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        })
          .then(() => {
            this.$http({
              url: '/api-pef/scoreproject/scoreItems/abandonAlreadyScoreItems',
              method: 'GET',
              params: { scoreItemId: scoreItemsId },
              loading: true
            })
              .then((data) => {
                this.getQuerydata()
              })
              .catch((err) => {
                this.getQuerydata()
                console.log(err)
              })
          })
          .catch(() => {})
      } else if (type === 'viewResult') {
        // 查看绩效结果明细
        this.editTab('viewResult', row)
      } else if (type === 'approve') {
        // 提交审批
        let projectStatus = 'RESULT_NO_PUBLISHED' // 结果未发布
        performanceManagement.submitProcessScoreItems({ scoreItemsId, projectStatus }).then((res) => {
          this.$message.success(res.message)
          this.getQuerydata()
        })
      } else if (type === 'delete') {
        // 删除
        this.$confirm(this.$t('perfMod.sureDeleteData'), {
          confirmButtonText: this.$t('common.affirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        })
          .then(() => {
            performanceManagement.delScoreItemsAndSon({ scoreItemsId }).then((res) => {
              this.$message.success(res.message)
              this.getQuerydata()
            })
          })
          .catch(() => {})
      }
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
          orderId: row.scoreItemsId,
          tabName: 'performanceScoreItemsDetail' + row.scoreItemsId
        },
        title: row.projectName,
        name: 'performanceScoreItemsDetail' + row.scoreItemsId
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
