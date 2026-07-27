<template>
  <el-container
    class="flex-container sitereviewplan_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            type="primary"
            @click="addHandle"
          >
            {{
              $t('common.add')
            }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        :source="financeInfoChangeApi.list"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch'
import { downloadFileLink } from 'lib@/utils/file'
import financialInforChangesDeatil from './edit'
import quaOfReviewDetail from 'modb@/vendorManagementBuyer/views/quaOfReview/quaOfReviewDetail'
import { financeInfoChangeApi, siteReviewPlan } from 'modb@/vendorManagementBuyer/api/vendorManagement'

export default {
  name: 'FinancialInforChangesList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    QuickSearch
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      financeInfoChangeApi: financeInfoChangeApi,
      displayItem: [],
      dialogFormVisible2: false,
      name: 'financialInforChangesList',
      tableName: 'sitereviewplanTable',
      pageSize: 15,
      gridId: 'list',
      rules: {},
      dictCodes: {
        planType: 'planType',
        planStatus: 'planStatus',
        planProcessStatus: 'planProcessStatus'
      },
      filterParams: {},
      tableHeader: [
        {
          prop: 'changeHeaderCode',
          label: this.$t('vendorMod.inviteVendorNo') // 单据编码
        },
        {
          prop: 'changeHeaderName',
          label: this.$t('bidMod.documentTitle'), // 单据标题
          showType: 'button',
          btnStyle: 'text',
          callback: function (row) {
            this.editTab('view', row)
          }.bind(this)
        },
        {
          prop: 'approveStatus',
          label: this.$t('dataConfMod.triggerState'), // 状态
          dataType: 'dict',
          code: 'APPROVE_STATUS_TYPE'
        },
        {
          prop: 'createdFullName',
          label: this.$t('purchaseDemand.applicant') // 申请人
        },
        {
          prop: 'creationDate',
          label: this.$t('purchaseDemand.creationDate') // 创建时间
        },
        {
          prop: 'approveTime',
          label: this.$t('supplierRating.approvalTime') // 审批时间
        },
        {
          prop: 'operation',
          label: this.$t('common.operation'), // 操作
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 130,
          buttons: [
            {
              callback: row => this.editTab('edit', row),
              show: row => row.approveStatus === 'DRAFT' || row.approveStatus === 'REJECTED' || row.approveStatus === 'WITHDRAW',
              formattor: () => {
                return this.$t('common.edit') // 编辑
              }
            },
            {
              callback: row => this.editTab('approved', row),
              show: row => row.approveStatus === 'REJECTED' || row.approveStatus === 'WITHDRAW',
              formattor: () => {
                return this.$t('vendorMod.relegation.abandon') // 废弃
              }
            },
            {
              callback: row => this.editTab('approved', row),
              show: row => row.approveStatus === 'SUBMITTED',
              formattor: () => {
                return this.$t('vendorMod.relegation.examineApprove') // 审批
              }
            },
            {
              callback: row => this.editTab('view', row),
              show: row => row.approveStatus === 'APPROVED',
              formattor: () => {
                return this.$t('vendorMod.check') // 查看
              }
            },
            {
              callback: row => this.deleteHandle(row),
              show: row => row.approveStatus === 'DRAFT',
              formattor: () => {
                return this.$t('common.delete')// 删除
              }
            }
          ]
        }
      ],
      filterConfig: [
        {
          prop: 'changeHeaderCode',
          label: this.$t('vendorMod.inviteVendorNo') // 单据编码
        },
        {
          prop: 'changeHeaderName',
          label: this.$t('bidMod.documentTitle') // 单据标题
        },
        {
          prop: 'createdBy',
          label: this.$t('purchaseDemand.applicant'), // 申请人
          width: 100,
          type: 'quicksearch',
          propKey: 'username',
          showKey: 'nickname',
          name: 'scc_rbac_user_display'
        },
        {
          prop: 'approveStatus',
          label: this.$t('bidMod.status'), // 状态
          type: 'dict', // 字典类型
          code: 'APPROVE_STATUS_TYPE' // 字典code
        }
      ],
      queryParam: {}
    }
  },
  created () {
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    // 编辑tab
    editTab (type, row) {
      let tab = {}
        // 查看
        tab = {
          component: financialInforChangesDeatil,
          params: {
            flag: type,
            row: row,
            tabName: 'financialInforChangesDeatil' + row.changeHeaderName
          },
          title: row.changeHeaderName,
          name: 'financialInforChangesDeatil' + row.changeHeaderName
        }
      this.$emit('tab-add', tab)
    },
    deleteOneContent (index, row) {
      this.displayItem.splice(index, 1)
    },
    getCategoryObj (val, scope) {
      scope.categoryId = val ? val.categoryId : ''
      scope.categoryCode = val ? val.categoryCode : ''
      scope.categoryName = val ? val.categoryName : ''
    },
    addCategorys () {
      let datas = {
        siteReviewPlanId: this.siteReviewPlanId,
        siteReviewPlanCategoryList: this.displayItem.filter(v => v.categoryId)
      }
      financeInfoChangeApi.saveCategoryList(datas).then(data => {
          if (data.data.categoryListSaveStatus) {
            this.$message.warning(this.$t('dataConfMod.msgRepeatDel')) // 你选择的重复的品类已被删除!
            this.dialogFormVisible2 = false
            this.getQuerydata()
          } else {
            this.$message.success(this.$t('common.success'))
            this.dialogFormVisible2 = false
            this.getQuerydata()
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    handleItemSelection (val) {
      this.multipleSelection = val
    },
    // 新增单个品类
    addOneItem () {
      this.displayItem.push({
        categoryId: null,
        categoryCode: null,
        categoryName: null
      })
    },
    // 点击新增采购组织
    selectHandler2 (node, value) {
      this.form.orgId = node.organizationId
      this.form.orgCode = node.organizationCode
      this.form.orgName = node.organizationName
      console.log(this.form)
    },
    // 快查供应商名称
    getDepObjName (val, scpoe) {
      scpoe.vendorId = val.companyId
      scpoe.vendorName = val.companyName
      scpoe.vendorCode = val.companyCode
      console.log(this.form)
    },
    handleSuccess () {
      this.getQuerydata()
    },
    downloadTemplate () {
      downloadFileLink('/api-sup/sup/sitereviewplan/importExcelTemplate', '导入模板.xlsx').catch(
        () => {
          this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
        }
      )
    },
    cancel () {
      this.visible = false
    },
    confirmSave () {
      const flag = this.mode
      this.form.siteReviewPlanCategoryList = this.displayItem
      let formAll = this.form
      formAll.submitFlag = 'SAVE'
      if (this.displayItem.length < 1) {
        this.$message.error(this.$t('vendorMod.pleaseFillInTheCategory')) // 请填写品类
        return false
      }

      if (flag === 'add') {
        siteReviewPlan.planAdd(formAll).then(res => {
          this.visible = false
          this.$message.success(res.message)
          this.getQuerydata()
        })
      } else if (flag === 'edit') {
        siteReviewPlan.planUpdate(formAll).then(res => {
          this.visible = false
          this.$message.success(res.message)
          this.getQuerydata()
        })
      }
    },
    confirm () {
      this.$refs.form.validate(result => {
        if (result) {
          if (this.displayItem.length < 1) {
            this.$message.error(this.$t('vendorMod.pleaseFillInTheCategory')) // 请填写品类
            return false
          }
          const flag = this.mode
          // 新增时不用提交主键值
          this.form.siteReviewPlanCategoryList = this.displayItem
          let formAll = this.form
          formAll.submitFlag = 'SUBMIT'
          if (flag === 'add') {
            siteReviewPlan.planAdd(formAll).then(res => {
              this.visible = false
              this.$message.success(res.message)
              this.getQuerydata()
            })
          } else if (flag === 'edit') {
            siteReviewPlan.planUpdate(formAll).then(res => {
              this.visible = false
              this.$message.success(res.message)
              this.getQuerydata()
            })
          }
        }
      })
    },

    syncFilterParams (values) {
      this.filterParams = values
    },
    getQuerydata (params) {
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    deleteHandle (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          financeInfoChangeApi.delete(row.changeHeaderId).then(res => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    addHandle (row) {
      this.mode = 'add'
      const tab = {
        component: financialInforChangesDeatil,
        params: {
          row,
          flag: this.mode
        },
        title: this.$t('vendorMod.financialInforChangesAdd'), // 邀请供应商新增
        name: 'financialInforChangesDeatil'
      }
      this.$emit('tab-add', tab)
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    // 上传附件成功
    handleUploadSuccess (file, row, key) {
      const { id, name } = file
      row[key] = id.toString()
    },
    // 删除文件
    handleAttachmentRemove (row, key) {
      row[key] = ''
    }
  }
}
</script>
