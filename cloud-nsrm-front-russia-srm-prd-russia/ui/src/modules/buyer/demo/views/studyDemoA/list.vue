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
      <MainHeader>
        <template slot="left">
          <el-button
            v-if="curRole === 'BUYER'"
            type="primary"
            @click="editTab('add')"
          >
            {{ $t("common.add") }}
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
import Edit from './edit'
import quaOfReviewDetail from 'modb@/vendorManagementBuyer/views/quaOfReview/quaOfReviewDetail'
import { adaptDictData, findMenuIdByPath } from '@/utils'
import { getDictItem } from '@/api/common'
import { quaSampleApi } from 'modb@/vendorManagementBuyer/api/vendorManagement'

export default {
  name: 'List',
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
      }
    }
  },

  created () {
    let _this = this
    this.fatchDictData() // 字典
    _this.queryForm = [
      {
        prop: 'purchaseOrgId',
        label: () => _this.$t('common.orgName'), // 采购组织
        type: 'OUorganizationSelector'
      },
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
        name: 'scc_sup_company_info_display_buyer'
      },
      {
        prop: 'testResult',
        label: () => _this.$t('vendorMod.sampleTestResult'), // 样品测试结果
        type: 'select',
        options: []
      },
      {
        prop: 'isMaterialTrial',
        label: () => _this.$t('vendorMod.isTrial'), // 是否试用
        type: 'select',
        options: _this.yesOrNo
      },
      {
        prop: 'reviewFormNumber',
        label: () => _this.$t('vendorMod.quaNum') // '资质审查单号'
      },
      {
        prop: 'approveStatus',
        label: () => _this.$t('vendorMod.approveStatus'), // '审批状态'
        type: 'select',
        options: _this.approveStatus
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
    if (this.curRole === 'VENDOR') {
      this.queryForm[4] = {
        prop: 'vendorName',
        label: () => _this.$t('common.vendorName'), // 供应商名称
        type: 'quicksearch',
        disabled: true,
        showKey: 'companyName',
        name: 'scc_sup_company_info_display_buyer'
      }
    }
    _this.tableHeader = [
      {
        prop: 'approveStatus',
        label: () => _this.$t('vendorMod.orderStatus'), // '状态',
        formattor (val) {
          return _this.$getDictLabelByValue(_this.approveStatus, val)
        }
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
        align: 'center',
        formattor (val) {
          return val === 'Y' ? _this.$t('common.yes') : _this.$t('common.no') // yes no
        }
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
        width: 130
      },
      {
        prop: 'createdBy',
        label: () => _this.$t('common.creator'), // 创建人
        minWidth: 100
      },
      {
        prop: 'creationDate',
        label: () => _this.$t('common.creationTime'), // 创建时间
        width: 150
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
                (row.approveStatus === 'DRAFT' ||
                  row.approveStatus === 'REJECTED') &&
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
              if (
                row.approveStatus === 'PUBLISHED' &&
                _this.curRole === 'VENDOR'
              ) {
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
              return _this.$t('common.refused') // 拒绝
            },
            show: function (row) {
              if (
                row.approveStatus === 'PUBLISHED' &&
                _this.curRole === 'VENDOR'
              ) {
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
              if (
                row.approveStatus === 'CONFIRMED' &&
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
              this.editTab('doApproval', row)
            }.bind(this),
            formattor (val) {
              return _this.$t('vendorMod.doApproval') // '审批'
            },
            show: function (row) {
              if (
                row.approveStatus === 'SUBMITTED' &&
                _this.curRole === 'BUYER'
              ) {
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
    let routeParam = this.$route.params
    if (
      routeParam.from === 'workCount' &&
      this.firstLoad &&
      this.curRole === 'VENDOR'
    ) {
      this.queryParam.approveStatus = routeParam.approveStatus
      this.firstLoad = false
      this.preFormObj.approveStatus = routeParam.approveStatus
    }
    // ]]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    getQuerydata (v) {
      // if (v && v.dateList) {
      //   v.requireStarTime = v.dateList[0];
      //   v.requireEndTime = v.dateList[1];
      // } else if (v && !v.dateList) {
      //   delete v.requireStarTime;
      //   delete v.requireEndTime;
      // }
      this.queryParam = v // || this.preFormObj;
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 获取数据字典
    fatchDictData () {
      // 审批状态
      getDictItem('SAMPLE_STATUS').then(res => {
        this.approveStatus = adaptDictData(res.data, 'dict')
        this.queryForm[8].options = this.approveStatus
      })
      // 测试结果
      getDictItem('SAMPLE_TEST_RESULT').then(res => {
        this.testResult = adaptDictData(res.data, 'dict')
        this.queryForm[5].options = this.testResult
      })
      getDictItem('YES_OR_NO').then(res => {
        this.yesOrNo = adaptDictData(res.data, 'dict')
        this.queryForm[6].options = this.testResult
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
          quaSampleApi.quaSampleBathDel([id]).then(res => {
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
          component: Edit,
          params: {
            flag: 'add',
            tabName: 'edit'
          },
          title: () => this.$t('vendorMod.addSimpel'), // '新增样品确认单',
          name: 'edit'
        }
      } else if (type === 'view') {
        // 修改
        let sampleId = row.sampleId
        tab = {
          component: Edit,
          params: {
            flag: type,
            sampleId: sampleId,
            tabName: 'edit' + row.sampleNumber
          },
          title: row.sampleNumber,
          name: 'edit' + row.sampleNumber
        }
      } else if (type === 'doApproval') {
        // 审批
        let sampleId = row.sampleId
        tab = {
          component: Edit,
          params: {
            flag: type,
            sampleId: sampleId,
            tabName: 'edit' + row.sampleId
          },
          title: row.sampleNumber,
          name: 'edit' + row.sampleId
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
          component: Edit,
          params: {
            flag: 'edit',
            sampleId: sampleId,
            tabName: 'edit' + row.sampleNumber
          },
          title: title,
          name: 'edit' + row.sampleNumber
        }
      }
      this.$emit('tab-add', tab)
    },
    importOne () {},
    exportOne () {},
    handleCurrentChange (val) {
      this.currentRow = val
    }
  }
}
</script>
<style scoped lang="scss"></style>
