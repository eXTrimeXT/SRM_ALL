<template>
  <el-container
    class="flex-container the_siteAssessment_wrapper"
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
            code="sup:siteAssessmentList:add"
            type="primary"
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
        url="/api-sup/review/siteForm/listPageByParm"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import siteAssessmentDetail from './siteAssessmentDetail'
import quaOfReviewDetail from 'modb@/vendorManagementBuyer/views/quaOfReview/quaOfReviewDetail'
import { parseTime } from '@/utils'
import { siteAssessmentApi, siteReviewPlan } from 'modb@/vendorManagementBuyer/api/vendorManagement'

export default {
  name: 'SiteAssessmentList',
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
      tableName: 'siteAssessmentList',
      defaultTableHeader: [],
      reviewResultList: [],
      approveStatusList: [],
      quaReviewTypeList: [],
      siteTypeList: [],
      yesOrNoList: [],
      pageSize: 15,
      gridId: 'siteAssessmentList',
      selectList: [],
      currentRow: null,
      tableHeader: [],
      tableData: [],
      statusList: [],
      preArr: [
        {
          prop: 'siteFormNumber',
          label: () => this.$t('vendorMod.siteOrderInfoV') // 供应商评审单号
        },
        {
          prop: 'reviewFormNumber',
          label: () => this.$t('vendorMod.quaNum') // '资质审查单号'
        },
        {
          prop: 'approveStatus',
          label: () => this.$t('vendorMod.orderStatus'), // '状态'
          type: 'dict', // 字典类型
          code: 'SUPPLIER_APPROVE_STATUS_TYPE' // 字典code
        },
        {
          prop: 'assessmentType',
          label: () => this.$t('vendorMod.siteTypeV'), // 供应商评审类型
          type: 'dict', // 字典类型
          code: 'CEEA_ASSESSMENT_TYPE' // 字典code
        },
        {
          prop: 'reviewResult',
          label: () => this.$t('vendorMod.certificationResult'), // 认证结果
          type: 'dict', // 字典类型
          code: 'CEEA_RESULT_TYPE' // 字典code
        },
        {
          prop: 'vendorName',
          label: () => this.$t('common.vendorName'), // '供应商名称'
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_all'
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
          this.$route.params.funName === 'siteAssessment'
        ) {
          let siteFormId = Number(this.$route.params.formId)
          let siteFormNumber = this.$route.params.formNo // 流程标题
          let row = {
            ...this.$route.params,
            siteFormId,
            siteFormNumber: siteFormNumber // tab 标题显示
          }
          this.editTab('view', row)
        }
        // 证件到期跳转
        if (this.$route.params.dataResources) {
          let siteFormId = Number(this.$route.params.dataResources.managementAttachId)
          let siteFormNumber = this.$route.params.dataResources.dataSources // 流程标题
          let row = {
            siteFormId,
            siteFormNumber: siteFormNumber // tab 标题显示
          }
          this.editTab('view', row)
        }
      }
    }
  },
  created () {
    let _this = this
    _this.tableHeader = [
      {
        prop: 'approveStatus',
        label: () => _this.$t('vendorMod.orderStatus'), // '状态'
        width: 90,
        dataType: 'dict', // 数据类型为字典
        code: 'SUPPLIER_APPROVE_STATUS_TYPE' // 字典code
      },
      {
        prop: 'vendorCode',
        label: () => _this.$t('common.vendorCode'), // '供应商编码'
        width: 120
      },
      {
        prop: 'vendorName',
        label: () => _this.$t('common.vendorName'), // '供应商名称'
        minWidth: 150
      },
      {
        prop: 'siteFormNumber',
        label: () => this.$t('vendorMod.siteOrderInfoV'), // 供应商评审单号
        width: 140,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.editTab('view', row)
        }.bind(this)
      },
      {
        prop: 'assessmentType',
        label: () => this.$t('vendorMod.siteTypeV'), // 供应商评审类型
        minWidth: 150,
        dataType: 'dict', // 数据类型为字典
        code: 'CEEA_ASSESSMENT_TYPE' // 字典code
      },
      {
        prop: 'reviewProcess',
        label: this.$t('vendorMod.reviewProcess'), // 评审进度
        minWidth: 150
      },
      {
        prop: 'reviewResult',
        label: () => this.$t('vendorMod.certificationResult'), // 认证结果
        width: 100,
        dataType: 'dict', // 数据类型为字典
        code: 'CEEA_RESULT_TYPE' // 字典code
      },
      {
        prop: 'reviewFormNumber',
        label: () => _this.$t('vendorMod.quaNum'), // '资质审查单号'
        width: 140,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.editTab('reviewView', row)
        }.bind(this)
      },
      {
        prop: 'createdUserName', // createdBy
        label: () => _this.$t('common.creator'), // '创建人'
        width: 110
      },
      {
        prop: 'creationDate',
        label: () => _this.$t('common.creationTime'), // '创建时间'
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'operation',
        label: () => _this.$t('common.operation'), // '操作'
        width: 120,
        btnStyle: 'text',
        fixed: 'right',
        showType: 'buttons',
        buttons: [
          {
            callback: function (row) {
              this.editTab('edit', row)
            }.bind(this),
            formattor: (val) => {
              return this.$t('vendorMod.appraisal') // '评审'
            },
            show: row => {
              if (['PUBLISH', 'SUBMITTED'].includes(row.approveStatus) && row.reviewResult == '') {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.recall(row)
            }.bind(this),
            formattor: (val) => {
              return this.$t('common.recall') // '撤回'
            },
            show: row => {
              if (['PUBLISH'].includes(row.approveStatus) && row.reviewResult == '' && row.createdId == this.$store.getters.userId) {
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
            code: 'sup:siteAssessmentList:edit',
            formattor (val) {
              return _this.$t('common.edit') // '编辑'
            },
            show: row => ['DRAFT', 'WITHDRAW', 'REJECTED'].includes(row.approveStatus)
          },
          {
            callback: function (row) {
              this.editTab('view', row)
            }.bind(this),
            formattor (val) {
              return _this.$t('vendorMod.doApproval') // 审批
            },
            code: 'sup:siteAssessmentList:approval',
            show: function (row) {
              if (['PUBLISH', 'SUBMITTED'].includes(row.approveStatus) && row.reviewResult != '') {
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
            code: 'sup:siteAssessmentList:delete',
            formattor (val) {
              return _this.$t('common.delete') // '删除'
            },
            show: row => ['DRAFT'].includes(row.approveStatus)
          },
          {
            callback: function (row) {
              this.editTab('view', row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.abandon') // 废弃
            },
            show: row => ['REJECTED', 'WITHDRAW'].includes(row.approveStatus)
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader // 自定义表格表头
    console.log(this.$store.getters)
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    recall (row) {
      siteReviewPlan.recall(row.siteFormId).then(res => {
        this.$message.success(res.message)
        this.$refs[this.gridId].query()
      })
    },
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
          component: siteAssessmentDetail,
          params: {
            flag: 'add',
            tabName: 'siteAssessmentDetail'
          },
          title: this.$t('vendorMod.addSite'), // 供应商评审新增
          name: 'siteAssessmentDetail'
        }
      } else if (type === 'view') {
        // 查看
        tab = {
          component: siteAssessmentDetail,
          params: {
            flag: 'view',
            row: row,
            siteFormId: row.siteFormId,
            tabName: 'siteAssessmentDetail' + row.siteFormNumber || row.siteFormId
          },
          title: row.siteFormNumber,
          name: 'siteAssessmentDetail' + row.siteFormNumber || row.siteFormId
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
          title: () => this.$t('vendorMod.checkQuaOrderInfo'), // '查看资质审查单',
          name: 'quaOfReviewDetail' + row.reviewFormNumber
        }
      } else {
        // 修改
        tab = {
          component: siteAssessmentDetail,
          params: {
            flag: 'edit',
            row: row,
            siteFormId: row.siteFormId,
            tabName: 'siteAssessmentDetail' + row.siteFormNumber || row.siteFormId
          },
          title: row.siteFormNumber,
          name: 'siteAssessmentDetail' + row.siteFormNumber || row.siteFormId
        }
      }
      this.$emit('tab-add', tab)
    },
    // 删除数据
    delRowData (row) {
      let siteFormId = row.siteFormId
      // '当前操将永久删除这条数据，确认删除这条数据？'
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          siteAssessmentApi.siteFormDel({ siteFormId }).then(res => {
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
