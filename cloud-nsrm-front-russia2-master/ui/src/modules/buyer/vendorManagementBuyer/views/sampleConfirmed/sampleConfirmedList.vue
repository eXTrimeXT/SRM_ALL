<template>
  <el-container
    class="flex-container the_dictionary_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        :pre-form-obj="preFormObj"
        @getFormData="getQuerydata"
      />
      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <el-button
            v-if="curRole === 'BUYER'"
            type="primary"
            @click="editTab('add')"
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
        url="/api-sup/qua/quaSample/listPageByParam"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import SampleConfirmedDetail from './sampleConfirmedDetail'
import quaOfReviewDetail from 'modb@/vendorManagementBuyer/views/quaOfReview/quaOfReviewDetail'
import { saveOrUpdateOrderByUrl, quaSampleApi } from 'modb@/vendorManagementBuyer/api/vendorManagement'

export default {
  name: 'SampleConfirmedList',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      tableName: 'SampleConfirmedList',
      defaultTableHeader: [],
      name: '',
      curRole: this.$store.getters.userType, // vendor buyer
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'sampleConfirmedList',
      selectList: [],
      currentRow: null,
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      statusList: [],
      relations: [], // 境内外管理
      natureList: [], // 企业性质
      approveStatus: [], // 审批状态
      testResult: [], // 测试结果
      yesOrNo: [],
      isModify: false,
      dialogFormVisible: false,
      formLabelWidth: '100px',
      queryForm: [],
      queryParam: {},
      firstLoad: true,
      preFormObj: {},
      userType: this.$store.getters.userType
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
          this.$route.params.funName === 'sampleConfirmed'
        ) {
          let sampleId = Number(this.$route.params.formId)
          let formNo = this.$route.params.formNo // 流程标题
          let row = {
            ...this.$route.params,
            sampleId,
            sampleNumber: formNo // tab 标题显示
          }
          this.editTab('view', row)
        }
        // 证件到期跳转
        if (this.$route.params.dataResources) {
          let sampleId = Number(this.$route.params.dataResources.managementAttachId)
          let formNo = this.$route.params.dataResources.dataSources // 流程标题
          let row = {
            sampleId,
            sampleNumber: formNo // tab 标题显示
          }
          this.editTab('view', row)
        }
        if (
          this.$route.params.from === 'workCount' &&
          this.$route.params.funName === 'sampleConfirmed'
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
    let _this = this
    _this.queryForm = [
      // {
      //   prop: "purchaseOrgId",
      //   label: () => _this.$t("common.orgName"), //采购组织
      //   type:'OUorganizationSelector',
      // },
      {
        prop: 'categoryName',
        label: () => _this.$t('common.category'), // 品类
        type: 'catSelect',
        showKey: 'categoryName'
      },
      {
        prop: 'materialCode',
        label: () => _this.$t('common.materialCode'), // 物料编码,
        type: 'quicksearch',
        showKey: 'materialCode',
        name: 'scc_base_material_item'
      },
      {
        prop: 'sampleNumber',
        label: () => _this.$t('vendorMod.sampleNum') // 样品确认单号,
      },
      {
        prop: 'vendorName',
        label: () => _this.$t('common.vendorName'), // 供应商名称
        type: 'quicksearch',
        showKey: 'companyName',
        name: 'scc_sup_company_info_all',
        hidden: () => {
          return this.curRole === 'VENDOR'
        }
      },
      {
        prop: 'testResult',
        label: () => _this.$t('vendorMod.sampleTestResult'), // 样品测试结果
        type: 'dict',
        code: 'SAMPLE_TEST_RESULT'
      },
      {
        prop: 'isMaterialTrial',
        label: () => _this.$t('vendorMod.isTrial'), // 是否试用
        type: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'reviewFormNumber',
        label: () => _this.$t('vendorMod.quaNum') // '资质审查单号'
      },
      {
        prop: 'approveStatus',
        label: () => _this.$t('vendorMod.approveStatus'), // '审批状态'
        type: 'dict',
        code: this.userType == 'BUYER' ? 'SAMPLE_STATUS' : 'SAMPLE_STATUS_Supplier'
      },
      {
        prop: 'receiver',
        label: () => _this.$t('vendorMod.sampleReceiver') // '样品接收人',
      },
      {
        prop: 'receiverPhone',
        label: () => _this.$t('vendorMod.receiverPhone') // '接收人电话',
      },
      {
        prop: 'dateList',
        label: () => _this.$t('vendorMod.requestTime'), // '要求送样时间',
        type: 'daterange'
      }
    ]
    // if (this.curRole === "VENDOR") {
    //   this.queryForm[4] = {
    //     prop: "vendorName",
    //     label: () => _this.$t("common.vendorName"), //供应商名称
    //     type: "quicksearch",
    //     disabled: true,
    //     showKey: "companyName",
    //     name: "scc_sup_company_info_display"
    //   };
    // }
    _this.tableHeader = [
      {
        prop: 'approveStatus',
        label: () => _this.$t('vendorMod.orderStatus'), // '状态',
        dataType: 'dict', // 数据类型为字典
        code: 'SAMPLE_STATUS' // 字典code
      },
      {
        prop: 'vendorCode',
        label: () => _this.$t('common.vendorCode'), // 供应商编码
        minWidth: 120
      },
      {
        prop: 'vendorName',
        label: () => _this.$t('common.vendorName'), // 供应商名称
        width: 150
      },
      {
        prop: 'sampleNumber',
        label: () => _this.$t('vendorMod.sampleNum'), // 样品确认单号
        width: 120,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.editTab('view', row)
        }.bind(this)
      },
      {
        prop: 'isMaterialTrial',
        label: () => _this.$t('vendorMod.isTrial'), // '是否试用',
        minWidth: 100,
        dataType: 'dict', // 数据类型为字典
        code: 'YES_OR_NO' // 字典code
      },
      {
        prop: 'reviewFormNumber',
        label: () => _this.$t('vendorMod.quaNum'), // '资质审查单号',
        width: 120,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.editTab('reviewView', row)
        }.bind(this)
      },
      {
        prop: 'receiver',
        label: () => _this.$t('vendorMod.sampleReceiver'), // '样品接收人',
        minWidth: 120
      },
      {
        prop: 'receiverPhone',
        label: () => _this.$t('vendorMod.receiverPhone'), // '接收人电话',
        minWidth: 120
      },
      {
        prop: 'requireSendTime',
        label: () => _this.$t('vendorMod.sendTime'), // '要求送样时间',
        width: 130,
        dataType: 'dateTime'
      },
      {
        prop: 'createdUserName', // createdBy
        label: () => _this.$t('common.creator'), // 创建人
        minWidth: 100
      },
      {
        prop: 'creationDate',
        label: () => _this.$t('common.creationTime'), // 创建时间
        width: 150,
        dataType: 'dateTime'
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
              if (
                (row.approveStatus === 'DRAFT' || row.approveStatus === 'REJECTED') &&
                _this.curRole === 'BUYER'
              ) {
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
              this.editTab('vendorEdit', row)
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
              this.editTab('vendorEditRefuse', row)
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
              this.editTab('editResult', row)
            }.bind(this),
            formattor (val) {
              return _this.$t('vendorMod.evaluateSimpel') // '评价样品'
            },
            show: function (row) {
              if (['CONFIRMED', 'WITHDRAW', 'REJECTED'].includes(row.approveStatus) && _this.curRole === 'BUYER') {
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
    this.defaultTableHeader = _this.tableHeader
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
        v.requireStarTime = v.dateList[0]
        v.requireEndTime = v.dateList[1]
      } else if (v && !v.dateList) {
        delete v.requireStarTime
        delete v.requireEndTime
      }
      this.queryParam = v || this.preFormObj
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 删除数据
    delRowData (row) {
      let id = row.sampleId
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          quaSampleApi.quaSampleBathDel([id]).then((res) => {
            this.$message({
              message: res.message,
              type: 'success'
            })
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    // 编辑编辑tab
    editTab (type, row) {
      let tab = {}
      if (type === 'add') {
        // 新增
        tab = {
          component: SampleConfirmedDetail,
          params: {
            flag: 'add',
            tabName: 'sampleConfirmedDetail'
          },
          title: () => this.$t('vendorMod.addSimpel'), // '新增样品确认单',
          name: 'sampleConfirmedDetail'
        }
      } else if (type === 'view') {
        // 修改
        let sampleId = row.sampleId
        tab = {
          component: SampleConfirmedDetail,
          params: {
            flag: type,
            sampleId: sampleId,
            tabName: 'sampleConfirmedDetail' + row.sampleNumber
          },
          title: row.sampleNumber,
          name: 'sampleConfirmedDetail' + row.sampleNumber
        }
      } else if (type === 'doApproval') {
        // 审批
        let sampleId = row.sampleId
        tab = {
          component: SampleConfirmedDetail,
          params: {
            flag: type,
            sampleId: sampleId,
            tabName: 'sampleConfirmedDetail' + row.sampleId
          },
          title: row.sampleNumber,
          name: 'sampleConfirmedDetail' + row.sampleId
        }
      } else if (type === 'reviewView') {
        // 查看
        tab = {
          component: quaOfReviewDetail,
          params: {
            flag: 'view',
            row: row,
            tabName: 'quaOfReviewDetail' + row.reviewFormNumber
          },
          title: row.reviewFormNumber,
          name: 'quaOfReviewDetail' + row.reviewFormNumber
        }
      } else if (type === 'vendorEditRefuse') {
        console.log(row)
        // 确认拒绝该样品确认单吗?
        this.$confirm(this.$t('vendorMod.isRejectSampleConfirmation'), this.$t('common.tips'), {
          confirmButtonText: this.$t('common.affirm'), // 确定
          cancelButtonText: this.$t('common.cancel'), // 取消
          type: 'warning'
        }).then(() => {
          let url = '/api-sup/qua/quaSample/refused'
          let submitData = {
            sampleId: row.sampleId
          }
          saveOrUpdateOrderByUrl(url, submitData).then(res => {
            this.$message({
              type: 'success',
              message: res.message
            })
            this.getQuerydata()
          })
        }).catch(() => {

        })
        return false
      } else {
        // 修改
        let title = this.$t('vendorMod.sampleConfirmed') + row.sampleNumber // '样品确认单'
        if (type === 'editResult') {
          title = this.$t('vendorMod.evaluateSimpel') + row.sampleNumber // '评价样品'
        } else if (type === 'vendorEdit') {
          title = this.$t('vendorMod.sampleConfirmed') // '样品确认'
        }
        let sampleId = row.sampleId
        tab = {
          component: SampleConfirmedDetail,
          params: {
            flag: 'edit',
            sampleId: sampleId,
            tabName: 'sampleConfirmedDetail' + row.sampleNumber
          },
          title: title,
          name: 'sampleConfirmedDetail' + row.sampleNumber
        }
      }
      this.$emit('tab-add', tab)
    },
    handleCurrentChange (val) {
      this.currentRow = val
    }
  }
}
</script>
