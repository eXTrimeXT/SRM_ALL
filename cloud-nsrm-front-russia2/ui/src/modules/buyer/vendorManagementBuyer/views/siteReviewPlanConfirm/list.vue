<template>
  <el-container
    class="flex-container sitereviewplanconfirm_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQuerydata"
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
        :source="siteReviewPlanConfirm.planList"
      />
      <srm-dialog
        :title="$t('dataConfMod.categoryDetails')"
        :visible.sync="dialogFormVisible2"
        :close-on-click-modal="false"
        size="middle"
      >
        <el-table
          :data="displayItemPL"
          style="width: 100%"
          border
          height="250px"
          highlight-current-row
        >
          <el-table-column
            align="center"
            type="index"
            :label="$t('contractMod.tabindex')"
            width="60"
          />
          <!-- 品类编码 -->
          <el-table-column
            align="center"
            prop="categoryCode"
            :label="$t('common.categoryCode')"
            min-width="150"
            :show-overflow-tooltip="true"
          >
            <template slot-scope="scope">
              <QuickSearch
                :show-input="scope.row.categoryCode"
                show-key="categoryCode"
                :scope-data="scope.row"
                name="scc_base_purchase_category2"
              />
            </template>
          </el-table-column>
          <!-- 品类名称 -->
          <el-table-column
            align="center"
            prop="categoryName"
            :label="$t('common.categoryName')"
            min-width="150"
            :show-overflow-tooltip="true"
          />
        </el-table>
        <template #footer>
          <el-button @click="dialogFormVisible2 = false">
            {{ $t('common.backTo') }}
          </el-button>
        </template>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch'
import sitereviewplanconfirmEdit from './edit.vue'
import siteAssessmentDetail from 'modb@/vendorManagementBuyer/views/siteAssessment/siteAssessmentDetail'
import { siteReviewPlanConfirm } from 'modb@/vendorManagementBuyer/api/vendorManagement'
import { supCommonApi } from 'modb@/vendorManagementBuyer/api/supApi'

export default {
  name: 'SitereviewplanconfirmList',
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
      siteReviewPlanConfirm: siteReviewPlanConfirm,
      dialogFormVisible2: false,
      displayItemPL: [],
      name: 'sitereviewplanconfirmList',
      tableName: 'sitereviewplanconfirmTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      tableHeader: [
        {
          prop: 'planConfirmCode',
          label: this.$t('vendorMod.planConfirmCode2'), // 计划落实编号
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.editHandle2(row),
          width: 120
        },

        {
          prop: 'vendorName',
          label: this.$t('vendorMod.vendorName'), // 供应商名称
          width: 120
        },
        {
          prop: 'vendorCode',
          label: this.$t('vendorMod.vendorCode'), // 供应商编码
          width: 120
        },
        {
          prop: 'orgName',
          label: this.$t('vendorMod.orgName'), // 采购组织
          width: 100
        },
        {
          prop: 'categoryName',
          label: this.$t('vendorMod.categoryName'), // 品类
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.readOne(row),
          formattor: val => {
            return this.$t('vendorMod.check') // 查看
          },
          width: 100
        },
        {
          prop: 'categoryCode',
          label: this.$t('vendorMod.categoryCode2'), // 品类编码
          width: 100
        },
        {
          prop: 'planType',
          label: this.$t('vendorMod.planType'), // 计划类型
          width: 120,
          formattor: val => {
            let label = null
            this.planTypeAll.forEach(datas => {
              if (datas.value == val) {
                label = datas.label
              }
            })
            return label
          }
        },
        {
          prop: 'planName',
          label: this.$t('vendorMod.planName'), // 关联计划
          width: 100
        },
        {
          prop: 'creationDate',
          label: this.$t('vendorMod.creationDate2'), // 创建时间
          width: 100,
          dataType: 'dateTime'
        },
        {
          prop: 'personList',
          label: this.$t('vendorMod.personList'), // 工作小组成员
          width: 130,
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.editHandle2(row),
          formattor: val => {
            return this.$t('vendorMod.check') // 查看
          }
        },
        {
          prop: 'vendorContact',
          label: this.$t('vendorMod.vendorContact'), // 供应商联系人
          width: 130
        },
        {
          prop: 'vendorContactTel',
          label: this.$t('vendorMod.vendorContactTel'), // 供应商联系电话
          width: 130
        },
        {
          prop: 'planSetOutTime',
          label: this.$t('vendorMod.planSetOutTime'), // 计划出发时间
          width: 130,
          dataType: 'dateTime'
        },
        {
          prop: 'planVisitTime',
          label: this.$t('vendorMod.planVisitTime'), // 计划到访时间
          width: 130,
          dataType: 'dateTime'
        },
        {
          prop: 'visitDays',
          label: this.$t('vendorMod.visitDays'), // 计划到访天数
          width: 130
        },
        {
          prop: 'approveStatus',
          label: this.$t('vendorMod.status'), // 状态
          width: 120,
          formattor: val => {
            let label = null
            this.planStatusAll.forEach(datas => {
              if (datas.value == val) {
                label = datas.label
              }
            })
            return label
          }
        },
        {
          prop: 'rejectReason',
          label: this.$t('vendorMod.rejectReason'), // 驳回原因
          minWidth: 150
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
              callback: row => this.siteAssess(row),
              // code: "pr:requirementApply:edit",
              show: row =>
                row.approveStatus === 'PASS',
              formattor: () => {
                return this.$t('vendorMod.createSiteReviewCode') // 创建评审单
              }
            },
            {
              callback: row => this.editHandle(row),
              // code: "pr:requirementApply:edit",
              show: row =>
                row.approveStatus === 'DRAFT' ||
                row.approveStatus === 'VENDOR_REJECT' ||
                row.approveStatus === 'REJECT',
              formattor: () => {
                return this.$t('common.edit')
              }
            },
            {
              callback: row => this.publishHandle(row),
              // code: "pr:requirementApply:edit",
              show: row =>
                row.approveStatus === 'DRAFT' ||
                row.approveStatus === 'VENDOR_REJECT' ||
                row.approveStatus === 'REJECT',
              formattor: () => {
                return this.$t('common.publish')
              }
            },
            {
              callback: row => this.deleteHandle(row),
              // code: "pr:requirementApply:edit",
              show: row => row.approveStatus === 'DRAFT',
              formattor: () => {
                return this.$t('common.delete')
              }
            },
            {
              callback: row => this.approveHandle(row),
              // code: "pr:requirementApply:edit",
              show: row => row.approveStatus === 'VENDOR_CONFIRMED',
              formattor: () => {
                return this.$t('common.approve')
              }
            }
          ]
        }
      ],
      planTypeAll: [
        {
          value: 'YEAR',
          label: this.$t('vendorMod.YEAR') // 年度
        },
        {
          value: 'HALF_YEAR',
          label: this.$t('vendorMod.HALF_YEAR') // 半年度
        },
        {
          value: 'QUARTER',
          label: this.$t('vendorMod.QUARTER') // 季度
        },
        {
          value: 'ALLOW',
          label: this.$t('vendorMod.ALLOW') // 准入
        },
        {
          value: 'MONTH',
          label: this.$t('vendorMod.MONTH') // 月度
        }
      ],
      planStatusAll: [
        {
          value: 'DRAFT',
          label: this.$t('vendorMod.DRAFT') // 拟定
        },
        {
          value: 'RELEASED',
          label: this.$t('vendorMod.RELEASED') // 已发布
        },
        {
          value: 'VENDOR_CONFIRMED',
          label: this.$t('vendorMod.VENDOR_CONFIRMED') // 供应商已确认
        },
        {
          value: 'VENDOR_REJECT',
          label: this.$t('vendorMod.VENDOR_REJECT') // 供应商已驳回
        },
        {
          value: 'PASS',
          label: this.$t('vendorMod.PASS') // 审批已通过
        },
        {
          value: 'REJECT',
          label: this.$t('vendorMod.REJECT') // 审批已驳回
        },
        {
          value: 'ABANDONED',
          label: this.$t('perfMod.OBSOLETE') // 已废弃
        }
      ],
      planProcessStatusAll: [
        {
          value: 'NOT_STARTED',
          label: this.$t('vendorMod.NOT_STARTED') // 未启动
        },
        {
          value: 'ONGOING',
          label: this.$t('vendorMod.ONGOING') // 进行中
        },
        {
          value: 'COMPLETED',
          label: this.$t('vendorMod.COMPLETED') // 已完成
        }
      ],
      filterConfig: [
        {
          prop: 'planName',
          label: this.$t('vendorMod.planName') // 计划名称
        },
        {
          prop: 'orgId',
          label: this.$t('vendorMod.orgId'), // 采购组织
          type: 'OUorganizationSelector'
        },
        {
          prop: 'planType',
          label: this.$t('vendorMod.planType'), // 计划类型
          type: 'select',
          options: () => this.planTypeAll
        },
        // {
        //   prop: 'categoryCode',
        //   label: '品类名称',
        //   type: 'quicksearch',
        //   showKey: 'categoryName',
        //   propKey: 'categoryCode',
        //   name: 'scc_base_purchase_category2'
        // },
        {
          prop: 'approveStatus',
          label: this.$t('vendorMod.planStatus'), // 计划状态
          type: 'select',
          options: () => this.planStatusAll
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
          this.$route.params.funName === 'siteReviewPlanConfirm'
        ) {
          let planConfirmId = Number(this.$route.params.formId)
          let planConfirmCode = this.$route.params.formNo // 流程标题
          let row = {
            ...this.$route.params,
            planConfirmId,
            planConfirmCode: planConfirmCode // tab 标题显示
          }
          this.editHandle2(row)
        }
      }
    }
  },
  created () {
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    // 点击创建评审单
    siteAssess (row) {
      this.mode = 'adds'
      const tab = {
        component: siteAssessmentDetail,
        params: {
          readOnly: false,
          row,
          flag: this.mode
        },
        title: this.$t('vendorMod.createSiteReviewCode'), // 创建评审单
        name: 'siteAssessmentDetail'
      }
      this.$emit('tab-add', tab)
    },
    // 点击审批
    approveHandle (row) {
      this.mode = 'approve'
      const tab = {
        component: sitereviewplanconfirmEdit,
        params: {
          readOnly: false,
          row,
          flag: this.mode
        },
        title: this.$t('vendorMod.planConfirmEdit'), // 计划落实管理编辑
        name: 'sitereviewplanconfirmEdit' + row.planConfirmId
      }
      this.$emit('tab-add', tab)
    },
    // 维护品类
    readOne (row) {
      console.log(this.$store.state)
      this.displayItemPL = []
      supCommonApi.getCategoryList(row.siteReviewPlanId).then(data => {
          this.displayItemPL = data.data.list
          this.dialogFormVisible2 = true
          this.siteReviewPlanId = row.siteReviewPlanId
        })
        .catch(err => {
          console.log(err)
        })
    },
    // 点击发布
    publishHandle (row) {
      // console.log(row);
      const planConfirmId = row.planConfirmId
      const obj = {
        planConfirmId: planConfirmId,
        approveStatus: 'RELEASED'
      }
      siteReviewPlanConfirm.planUpdateStatus(obj).then(res => {
        this.$message.success(res.message)
        this.getQuerydata()
      })
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
          siteReviewPlanConfirm.planDelete(row.planConfirmId).then(res => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    addHandle (row) {
      this.mode = 'add'
      const tab = {
        component: sitereviewplanconfirmEdit,
        params: {
          row,
          flag: this.mode
        },
        title: this.$t('vendorMod.planConfirmAdd'), // 计划落实管理新增
        name: 'sitereviewplanconfirmEdit'
      }
      this.$emit('tab-add', tab)
      this.getQuerydata()
    },
    editHandle (row) {
      this.mode = 'edit'
      const tab = {
        component: sitereviewplanconfirmEdit,
        params: {
          readOnly: false,
          row,
          flag: this.mode
        },
        title: this.$t('vendorMod.planConfirmEdit'), // 计划落实管理编辑
        name: 'sitereviewplanconfirmEdit' + row.planConfirmId
      }
      this.$emit('tab-add', tab)
      this.getQuerydata()
    },
    editHandle2 (row) {
      this.mode = 'view'
      const tab = {
        component: sitereviewplanconfirmEdit,
        params: {
          readOnly: true,
          row,
          flag: this.mode
        },
        title: this.$t('vendorMod.planConfirmCheck'), // 计划落实管理查看
        name: 'sitereviewplanconfirmEdit' + row.planConfirmId
      }
      this.$emit('tab-add', tab)
      this.getQuerydata()
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
