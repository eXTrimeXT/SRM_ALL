<template>
  <el-container
    class="flex-container the_vendorEffect_wrapper"
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
        url="/api-sup/orgcategory/orgCatForm/listPageByParm"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import CooperationEndedDetail from './cooperationEndedDetail'
import { parseTime } from '@/utils'
import { cooperationEndedApi } from 'modb@/vendorManagementBuyer/api/vendorManagement'

export default {
  name: 'CooperationEndedList',
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
      tableName: 'cooperationEndedListList',
      defaultTableHeader: [],
      approveStatusList: [], // 审批状态
      supplierControlType: [], // 控制类型
      pageSize: 15,
      gridId: 'cooperationEndedListList',
      curRole: this.$store.getters.userType, // VENDOR BUYER
      selectList: [],
      currentRow: null,
      tableHeader: [],
      tableData: [],
      statusList: [],
      preArr: [
        {
          prop: 'orgCatFormNumber',
          label: () => this.$t('vendorMod.controlNumber') // '控制单号'
        },
        {
          prop: 'vendorName',
          label: () => this.$t('common.vendorName'), // '供应商名称'
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_all'
        },
        {
          prop: 'approveStatus',
          label: () => this.$t('common.status'), // '状态'
          type: 'dict', // 字典类型
          code: 'APPROVE_STATUS_TYPE' // 字典code
        },
        {
          prop: 'supplierControlType',
          label: () => this.$t('vendorMod.controlType'), // '控制类型'
          type: 'dict', // 字典类型
          code: 'SUPPLIER_CONTROL_TYPE2' // 字典code
        }
      ],
      queryParam: {}
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
          this.$route.params.funName === 'cooperationEnded'
        ) {
          let orgCatFormId = Number(this.$route.params.formId)
          let formNo = this.$route.params.formNo // 流程标题
          let row = {
            ...this.$route.params,
            orgCatFormId,
            orgCatFormNumber: formNo // tab 标题显示
          }
          this.editTab('doApproval', row)
        }
      }
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'approveStatus',
        label: () => this.$t('common.status'), // '状态'
        width: 100,
        dataType: 'dict', // 数据类型为字典
        code: 'APPROVE_STATUS_TYPE' // 字典code
      },
      {
        prop: 'vendorCode',
        label: () => this.$t('common.vendorCode'), // '供应商编码'
        width: 120
      },
      {
        prop: 'vendorName',
        label: () => this.$t('common.vendorName'), // '供应商名称'
        minWidth: 150
      },
      {
        prop: 'orgCatFormNumber',
        label: () => this.$t('vendorMod.controlNumber'), // '控制单号'
        width: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.editTab('view', row)
        }.bind(this)
      },
      {
        prop: 'supplierControlType',
        label: () => this.$t('vendorMod.controlType'), // '控制类型'
        width: 100,
        dataType: 'dict', // 数据类型为字典
        code: 'SUPPLIER_CONTROL_TYPE2' // 字典code
      },
      {
        prop: 'createdUserName', // createdBy
        label: () => this.$t('common.creator'), // '创建人'
        width: 100
      },
      {
        prop: 'creationDate',
        label: () => this.$t('common.creationTime'), // '创建时间'
        minWidth: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'startDate',
        label: this.$t('common.effectTime'), // '生效时间'
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'), // '操作'
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
              if (row.approveStatus === 'DRAFT' || row.approveStatus === 'REJECTED' || row.approveStatus === 'WITHDRAW') {
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
              if (row.approveStatus === 'DRAFT') {
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
          },
          {
            callback: function (row) {
              this.editTab('doApproval', row)
            }.bind(this),
            formattor (val) {
              return _this.$t('perfMod.abandon') // '废弃'
            },
            show: function (row) {
              if (row.approveStatus === 'REJECTED' || row.approveStatus === 'WITHDRAW') {
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
    // 编辑tab
    editTab (type, row) {
      let tab = {}
      if (type === 'add') {
        // 新增
        tab = {
          component: CooperationEndedDetail,
          params: {
            flag: 'add',
            tabName: 'CooperationEndedDetail',
            activeWorkflowTab: false
          },
          title: () => this.$t('vendorMod.addCooperationEnded'), // '新增合作终止',
          name: 'CooperationEndedDetail'
        }
      } else if (type === 'view') {
        // 修改
        tab = {
          component: CooperationEndedDetail,
          params: {
            flag: 'view',
            orderId: row.orgCatFormId,
            tabName: 'CooperationEndedDetail' + row.orgCatFormNumber,
            activeWorkflowTab: false
          },
          title: row.orgCatFormNumber,
          name: 'CooperationEndedDetail' + row.orgCatFormNumber
        }
      } else if (type === 'doApproval') {
        tab = {
          component: CooperationEndedDetail,
          params: {
            flag: 'doApproval',
            orderId: row.orgCatFormId,
            tabName: 'CooperationEndedDetail' + row.orgCatFormNumber,
            activeWorkflowTab: true
          },
          title: row.orgCatFormNumber,
          name: 'CooperationEndedDetail' + row.orgCatFormNumber
        }
      } else {
        // 修改
        tab = {
          component: CooperationEndedDetail,
          params: {
            flag: 'edit',
            orderId: row.orgCatFormId,
            tabName: 'CooperationEndedDetail' + row.orgCatFormNumber,
            activeWorkflowTab: false
          },
          title: row.orgCatFormNumber,
          name: 'CooperationEndedDetail' + row.orgCatFormNumber
        }
      }
      this.$emit('tab-add', tab)
    },
    // 删除数据
    delRowData (row) {
      let orgCatFormId = row.orgCatFormId
      // '当前操将永久删除这条数据，确认删除这条数据？'
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          cooperationEndedApi.orgCatFormDel({ orgCatFormId }).then((res) => {
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
<style scoped lang="scss"></style>
