<template>
  <el-container
    class="flex-container the_sampleConfirmedList_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        :pre-form-obj="preFormObj"
        @getFormData="getQuerydata"
      />
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-sup/materialTrial/listPageByParam"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import materialTrialDetail from './materialTrialDetail'
import sampleConfirmedDetail from 'mods@/vendorManagementSupplier/views/vendorSampleConfirmed/sampleConfirmedDetail'
import { parseTime } from '@/utils'
import { materialTrial } from 'mods@/vendorManagementSupplier/api/index'

export default {
  name: 'MaterialTrialList',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  provide () {
    return { context: this }
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      customBol: true,
      tableName: 'materialTrialList',
      defaultTableHeader: [],
      name: '',
      curRole: this.$store.getters.userType, // VENDOR BUYER
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'materialTrialList',
      selectList: [],
      currentRow: null,
      showFilterBar: 1,
      approveStatus: [], // 审批状态
      testResult: [], // 测试结果
      tableHeader: [],
      tableData: [],
      statusList: [],
      queryParam: {},
      formLabelWidth: '100px',
      queryForm: [
        {
          prop: 'categoryName',
          label: () => this.$t('common.category'), // 品类
          type: 'catSelect',
          showKey: 'categoryName'
        },
        {
          prop: 'materialCode',
          label: () => this.$t('common.materialCode'), // 物料编码,
          type: 'quicksearch',
          showKey: 'materialCode',
          name: 'scc_base_material_item'
        },
        {
          prop: 'trialNumber',
          label: () => this.$t('vendorMod.mtTrialNum') // 物料试用单号
        },
        {
          prop: 'vendorName',
          label: () => this.$t('common.vendorName'), // 供应商名称
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_display'
        },
        {
          prop: 'trialResult',
          label: () => this.$t('vendorMod.mtTestResult'), // 物料试用结果
          type: 'dict', // 字典类型
          code: 'SAMPLE_TEST_RESULT' // 字典code
        },
        {
          prop: 'sampleNumber',
          label: () => this.$t('vendorMod.sampleNum') // 样品确认单号
        },
        {
          prop: 'dateList',
          label: () => this.$t('vendorMod.trialTime'), // 试用时间
          type: 'daterange'
        },
        {
          prop: 'approveStatus',
          label: () => this.$t('vendorMod.orderStatus'), // 状态
          type: 'dict', // 字典类型
          code: 'SAMPLE_STATUS' // 字典code
        }
      ],
      firstLoad: true,
      preFormObj: {}
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
          this.$route.params.funName === 'vendorMaterialTrial'
        ) {
          let materialTrialId = Number(this.$route.params.formId)
          let fromNo = this.$route.params.formNo // 流程标题
          let row = {
            ...this.$route.params,
            materialTrialId,
            trialNumber: fromNo // tab 标题显示
          }
          this.editTab('doApproval', row)
        }
        // 证件到期跳转
        if (this.$route.params.dataResources) {
          let materialTrialId = Number(this.$route.params.dataResources.managementAttachId)
          let trialNumber = this.$route.params.dataResources.dataSources // 流程标题
          let row = {
            materialTrialId,
            trialNumber: trialNumber // tab 标题显示
          }
          this.editTab('view', row)
        }
        // 供应商待办跳转过来
        if (
          this.$route.params.from === 'workCount' &&
          this.$route.params.funName === 'vendorMaterialTrial'
        ) {
          // 供应商 工作台跳转
          console.log(this.$route.params.approveStatus)
          this.queryParam.approveStatus = 'PUBLISHED'
          this.preFormObj.approveStatus = 'PUBLISHED'
        }
      }
    }
  },
  created () {
    this.queryForm = this.queryForm.filter(item => item.prop !== 'vendorName')
    this.customBol = false
    let _this = this
    this.tableHeader = [
      {
        prop: 'approveStatus',
        label: () => _this.$t('vendorMod.orderStatus'),
        width: 80,
        dataType: 'dict', // 数据类型为字典
        code: 'SAMPLE_STATUS' // 字典code
      },
      {
        prop: 'entryType',
        label: '试用类型',
        width: 120,
        dataType: 'dict',
        code: 'MATERRIAL_ENTRY_TYPE'
      },
      {
        prop: 'vendorCode',
        label: () => _this.$t('common.vendorCode'), // 供应商编码
        width: 120
      },
      {
        prop: 'vendorName',
        label: () => _this.$t('common.vendorName'), // 供应商名称
        minWidth: 150
      },
      {
        prop: 'trialNumber',
        label: () => _this.$t('vendorMod.mtTrialNum'), // 物料试用单号
        width: 130,
        showType: 'button',
        btnStyle: 'text',
        formattor (val) {
          return val || '-'
        },
        callback: function (row) {
          if (!row.trialNumber) return
          this.editTab('view', row)
        }.bind(this)
      },
      {
        prop: 'sampleNumber',
        label: () => _this.$t('vendorMod.sampleNum'), // 样品确认单号
        width: 130,
        showType: 'button',
        btnStyle: 'text',
        formattor (val) {
          return val || '-'
        },
        callback: function (row) {
          if (!row.sampleNumber) return
          this.editTab('sampleView', row)
        }.bind(this)
      },
      {
        prop: 'trialStartDate',
        label: () => _this.$t('vendorMod.trialStartTime'), // 试用开始时间
        width: 120,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'trialEndDate',
        label: () => _this.$t('vendorMod.trialEndTime'), // 试用结束时间
        width: 120,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'sourceData',
        label: '来源系统',
        width: 120
      },
      {
        prop: 'createdUserName', // createdBy
        label: () => _this.$t('common.creator'), // 创建人
        width: 100
      },
      {
        prop: 'creationDate',
        label: () => _this.$t('common.creationTime'), // 创建时间
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
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
              return _this.$t('common.edit') // '编辑'
            },
            show: function (row) {
              if (row.approveStatus === 'DRAFT' && _this.curRole === 'BUYER') {
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
              return _this.$t('common.delete') // '删除'
            },
            show: function (row) {
              if (row.approveStatus === 'DRAFT' && _this.curRole === 'BUYER') {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.editTab('edit', row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.affirm') // '确认'
            },
            show: function (row) {
              if (row.approveStatus === 'PUBLISHED' && _this.curRole === 'VENDOR') {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.editTab('edit', row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.refused') // 拒绝
            },
            show: function (row) {
              if (row.approveStatus === 'PUBLISHED' && _this.curRole === 'VENDOR') {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.editTab('edit', row)
            }.bind(this),
            formattor (val) {
              return _this.$t('vendorMod.evaluateMt') // '评价物料'
            },
            show: function (row) {
              if (row.approveStatus === 'CONFIRMED' && _this.curRole === 'BUYER') {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.editTab('doApproval', row)
            }.bind(this),
            formattor (val) {
              return _this.$t('vendorMod.doApproval') // '审批'
            },
            show: function (row) {
              if (row.approveStatus === 'SUBMITTED' && _this.curRole === 'BUYER') {
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
    // 供应商第一次从工作台跳转过来的场景 [[
    // let routeParam = this.$route.params
    // if (routeParam.from === 'workCount' && this.firstLoad && this.curRole === 'VENDOR') {
    //   this.queryParam.approveStatus = routeParam.approveStatus
    //   this.firstLoad = false
    //   this.preFormObj.approveStatus = routeParam.approveStatus
    // }
    // ]]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    getQuerydata (v) {
      if (v && v.dateList) {
        v.startDate = v.dateList[0]
        v.endDate = v.dateList[1]
        // delete v.dateList;
      } else if (v && !v.dateList) {
        delete v.startDate
        delete v.endDate
      }
      this.queryParam = v || this.preFormObj
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 编辑tab
    editTab (type, row) {
      let tab = {}
      if (type === 'add') {
        // 新增
        tab = {
          component: materialTrialDetail,
          params: {
            flag: 'add',
            tabName: 'materialTrialDetail'
          },
          title: () => this.$t('vendorMod.addMtTrial'), // '新增物料试用',
          name: 'materialTrialDetail'
        }
      } else if (type === 'view') {
        // 修改
        tab = {
          component: materialTrialDetail,
          params: {
            flag: 'view',
            row: row,
            materialTrialId: row.materialTrialId,
            tabName: 'materialTrialDetail' + row.trialNumber
          },
          title: row.trialNumber,
          name: 'materialTrialDetail' + row.trialNumber
        }
      } else if (type === 'sampleView') {
        // 修改
        let sampleId = row.sampleId
        tab = {
          component: sampleConfirmedDetail,
          params: {
            flag: 'view',
            sampleId: sampleId,
            tabName: 'sampleConfirmedDetail' + row.sampleNumber
          },
          title: row.sampleNumber,
          name: 'sampleConfirmedDetail'
        }
      } else if (type === 'doApproval') {
        tab = {
          component: materialTrialDetail,
          params: {
            flag: type,
            row,
            materialTrialId: row.materialTrialId,
            tabName: 'materialTrialDetail' + row.trialNumber
          },
          title: row.trialNumber,
          name: 'materialTrialDetail' + row.trialNumber
        }
      } else {
        // 修改
        tab = {
          component: materialTrialDetail,
          params: {
            flag: 'edit',
            row: row,
            materialTrialId: row.materialTrialId,
            tabName: 'materialTrialDetail' + row.trialNumber
          },
          title: row.trialNumber,
          name: 'materialTrialDetail' + row.trialNumber
        }
      }
      this.$emit('tab-add', tab)
    },
    // 删除数据
    delRowData (row) {
      let id = row.materialTrialId
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          materialTrial.materialTrialOrderDel([id]).then((res) => {
            this.$message({
              message: res.message,
              type: 'success'
            })
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    handleCurrentChange (val) {
      this.currentRow = val
    }
  }
}
</script>
<style scoped lang="scss">
</style>
