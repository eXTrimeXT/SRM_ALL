<template>
  <el-container
    class="flex-container the_dictionary_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <MainHeader>
        <template slot="left">
          <AuthorityButton
            type="primary"
            code="sup:nonQuaOfReviewList:add"
            @click="editTab('add')"
          >
            {{ $t('common.add') }}
          </AuthorityButton>
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
        url="/api-sup/review/serviceReviewForm/listPageByParm"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import quaOfReviewDetail from './quaOfReviewDetail'
import { adaptDictData, parseTime } from '@/utils'
import { quaApi } from 'modb@/vendorManagementBuyer/api/vendorManagement'

export default {
  name: 'QuaOfReviewList',
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
      tableName: 'quaOfReviewList',
      defaultTableHeader: [],
      approveStatusList: [],
      quaReviewTypeList: [],
      yesOrNoList: [],
      pageSize: 15,
      gridId: 'quaOfReviewList',
      selectList: [],
      currentRow: null,
      tableHeader: [],
      tableData: [],
      statusList: [],
      preArr: [],
      queryParam: {}
    }
  },
  watch: {
    $route: {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler () {
        if (this.$route.params.from === 'fromFun' && this.$route.params.funName === 'quaOfReview') {
          let reviewFormId = Number(this.$route.params.formId)
          let reviewFormNumber = this.$route.params.formNo // 流程标题
          let row = {
            ...this.$route.params,
            reviewFormId,
            reviewFormNumber: reviewFormNumber // tab 标题显示
          }
          this.editTab('view', row)
        }
        // 证件到期跳转
        if (this.$route.params.dataResources) {
          let reviewFormId = Number(this.$route.params.dataResources.managementAttachId)
          let reviewFormNumber = this.$route.params.dataResources.dataSources // 流程标题
          let row = {
            reviewFormId,
            reviewFormNumber: reviewFormNumber // tab 标题显示
          }
          this.editTab('view', row)
        }
      }
    }
  },
  created () {
    let _this = this
    _this.preArr = [
      {
        prop: 'reviewFormNumber',
        label: () => this.$t('vendorMod.quaNum') // '资质审查单号'
      },
      {
        prop: 'vendorName',
        label: () => this.$t('common.vendorName'), // '供应商名称'
        type: 'quicksearch',
        showKey: 'companyName',
        name: 'scc_sup_company_info_display_buyer'
      },
      {
        prop: 'approveStatus',
        label: () => this.$t('vendorMod.orderStatus'), // '状态'
        type: 'dict', // 字典类型
        code: 'APPROVE_STATUS_TYPE' // 字典code
      },
      {
        prop: 'quaReviewType',
        label: () => this.$t('vendorMod.quaType'), // '资质审查类型'
        type: 'dict', // 字典类型
        code: 'QUA_REVIEW_TYPE' // 字典code
      }
    ]
    this.tableHeader = [
      {
        prop: 'approveStatus',
        label: () => this.$t('vendorMod.orderStatus'), // '状态'
        width: 100,
        dataType: 'dict', // 数据类型为字典
        code: 'APPROVE_STATUS_TYPE' // 字典code
      },
      {
        prop: 'vendorCode',
        label: () => this.$t('common.vendorCode'), // '供应商编码'
        width: 120
      },
      // { prop: 'erpVendorId',label:'ERP供应商ID',width:140 },
      {
        prop: 'vendorName',
        label: () => this.$t('common.vendorName'), // '供应商名称'
        minWidth: 150
      },
      {
        prop: 'reviewFormNumber',
        label: () => this.$t('vendorMod.quaNum'), // '资质审查单号'
        width: 140,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.editTab('view', row)
        }.bind(this)
      },
      {
        prop: 'quaReviewType',
        label: () => this.$t('vendorMod.quaType'), // '资质审查类型'
        width: 150,
        dataType: 'dict', // 数据类型为字典
        code: 'QUA_REVIEW_TYPE' // 字典code
      },
      {
        prop: 'createdUserName', // createdBy
        label: () => this.$t('common.creator'), // '创建人'
        width: 100
      },
      {
        prop: 'creationDate',
        label: () => this.$t('common.creationTime'), // '创建时间'
        width: 100,
        formattor (val) {
          // return val ? parseTime(val):''
          return val // ? _this.$dayjs(val).format("YYYY-MM-DD") : "";
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
            code: 'sup:quaOfReviewList:edit',
            show: function (row) {
              if (
                row.approveStatus === 'DRAFT' ||
                row.approveStatus === 'REJECTED' ||
                row.approveStatus === 'WITHDRAW'
              ) {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.intoFlow('view', row)
            }.bind(this),
            formattor (val) {
              return _this.$t('vendorMod.doApproval') // 审批
            },
            code: 'sup:nonQuaOfReviewList:Approval',
            show: function (row) {
              if (row.approveStatus === 'SUBMITTED') {
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
            code: 'sup:nonQuaOfReviewList:delete',
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
              this.intoFlow('view', row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.abandon') // 废弃
            },
            code: 'sup:nonQuaOfReviewList:abandon',
            show: row => ['WITHDRAW', 'REJECTED'].includes(row.approveStatus)
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader // 自定义表格表头
    this.$nextTick(() => {
      this.getQuerydata()
    })

    if (
      this.$route.params.from === 'vendorProfileList' &&
      this.$route.params.funName === 'nonQuaOfReview'
    ) {
      let row = {
        vendorId: this.$route.params.fdSubject.companyId,
        vendorCode: this.$route.params.fdSubject.companyCode,
        vendorName: this.$route.params.fdSubject.companyName
      }
      this.editTab('add', row)
    }
  },
  methods: {
    getQuerydata (v) {
      this.queryParam = v || this.queryParam
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    syncFilterParams (values) {
      this.queryParam = values
    },
    // 编辑tab
    editTab (type, row) {
      let tab = {}
      if (type === 'add') {
        // 新增
        tab = {
          component: quaOfReviewDetail,
          params: {
            flag: 'add',
            row: row,
            tabName: 'quaOfReviewDetail'
          },
          title: () => this.$t('vendorMod.noAddQua'), // '资质审查新增',
          name: 'quaOfReviewDetail'
        }
      } else if (type === 'view') {
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
        tab = {
          component: quaOfReviewDetail,
          params: {
            flag: 'edit',
            row: row,
            tabName: 'quaOfReviewDetail' + row.reviewFormNumber
          },
          title: row.reviewFormNumber,
          name: 'quaOfReviewDetail' + row.reviewFormNumber
        }
      }
      this.$emit('tab-add', tab)
    },
    // 进入流程页面
    intoFlow (type, row) {
      this.editTab(type, row)
    },
    // 删除数据
    delRowData (row) {
      let reviewFormId = row.reviewFormId
      // '当前操将永久删除这条数据，确认删除这条数据？'
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          quaApi.serviceReviewFormhDel({ reviewFormId }).then(res => {
            this.$message({
              message: res.message,
              type: 'success'
            })
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    approvalOne (row) {
      // 确认审批吗？
      let reviewFormId = row.reviewFormId
      this.$confirm(this.$t('common.approvalTips'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-sup/review/serviceReviewForm/pass',
            method: 'POST',
            data: { reviewFormId: row.reviewFormId },
            loading: true
          })
            .then(data => {
              this.$message.success(this.$t('common.success')) // 操作成功
              this.getQuerydata()
            })
            .catch(err => {
              console.log(err)
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
