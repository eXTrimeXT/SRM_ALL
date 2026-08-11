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
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-pef/pj/scoring/perfOverallScore/listPerfOverallScorePage"
      />
    </el-main>
    <srm-dialog
      :title="dialogTitle"
      size="large"
      :visible.sync="visible"
    >
      <div class="sitereviewplanEdit">
        <el-form
          ref="form"
          :model="form"
          :rules="rules"
        >
          <srm-row>
            <srm-col :intCol="3">
              <el-form-item
                prop="planName"
                :label="$t('perfMod.planName')"
              >
                <el-input v-model="form.planName" />
              </el-form-item>
            </srm-col>
            <srm-col :intCol="3">
              <el-form-item
                prop="vendorId"
                :label="$t('perfMod.vendorName')"
              >
                <QuickSearch
                  :show-input="form.vendorName"
                  show-key="companyName"
                  :scope-data="form"
                  name="scc_sup_company_info_display"
                  @close-quicksearch="getDepObjName"
                />
              </el-form-item>
            </srm-col>
            <srm-col :intCol="3">
              <el-form-item
                prop="orgName"
                :label="$t('perfMod.orgName')"
              >
                <OrganizationSelector
                  ref="organizationSelector"
                  v-model="form.orgId"
                  :parent-id="-1"
                  :limit="false"
                  @select="selectHandler2"
                />
                <!-- node-type="PO" -->
              </el-form-item>
            </srm-col>
            <srm-col :intCol="3">
              <el-form-item
                prop="planType"
                :label="$t('perfMod.planType')"
              >
                <el-select
                  v-model="form.planType"
                  :placeholder="$t('common.pleaseSelect')"
                >
                  <el-option
                    v-for="item in planTypeAll"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>
            </srm-col>
            <srm-col :intCol="3">
              <el-form-item
                prop="planStartDate"
                :label="$t('perfMod.planStartDate')"
              >
                <el-date-picker
                  v-model="form.planStartDate"
                  type="date"
                  :placeholder="$t('common.pleaseSelectDate')"
                  :format="$formatDatePicker"
                  value-format="yyyy-MM-dd"
                />
              </el-form-item>
            </srm-col>
          </srm-row>
        </el-form>
      </div>
      <template
        #footer
        class="dialog-footer"
      >
        <el-button @click="confirmSave">
          {{ $t('common.staging') }}
        </el-button>
        <el-button
          type="primary"
          @click="confirm"
        >
          {{ $t('common.submit') }}
        </el-button>
      </template>
    </srm-dialog>
    <srm-dialog
      :title="$t('dataConfMod.accessFlowConfCate')"
      :visible.sync="dialogFormVisible2"
      :close-on-click-modal="false"
      size="middle"
    >
      <div class="mb10">
        <el-button
          type="primary"
          class="detail-pbtn"
          @click="addOneItem"
        >
          {{
            $t('common.add')
          }}
        </el-button>
      </div>
      <el-table
        :data="displayItem"
        style="width: 100%"
        border
        height="250px"
        highlight-current-row
        @selection-change="handleItemSelection"
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
              @close-quicksearch="getCategoryObj"
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
        <!-- 操作 -->
        <el-table-column
          :label="$t('common.operation')"
          width="60"
        >
          <template slot-scope="scope">
            <!-- 删除 -->
            <el-button
              type="text"
              @click="deleteOneContent(scope.$index, scope.row)"
            >
              {{ $t('common.delete') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="dialogFormVisible2 = false">
          <!-- 取 消 -->
          {{ $t('common.cancel') }}
        </el-button>
        <el-button
          type="primary"
          @click="addCategorys"
        >
          {{ $t('common.confirm') }}
        </el-button>
      </template>
    </srm-dialog>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import ExportExcel from 'lib@/components/export-excel'
import performanceScoreItemsDetail from 'modb@/performanceManagement/views/performanceScoreItems/performanceScoreItemsDetail'
import assessmentDetail from 'modb@/performanceManagement/views/performanceAssessment/assessmentDetail'
import performanceQueryDetail from 'modb@/performanceManagement/views/performanceQuery/performanceQueryDetail'
import improvementDetail from 'modb@/performanceManagement/views/vendorImprovement/improvementDetail'
import { performanceManagement } from 'modb@/performanceManagement/api/index'
import yujingdanEdit from 'modc@/buyer/performanceManagement/views/yujingdan/edit'

export default {
  name: 'SitereviewplanList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    QuickSearch,
    MImport,
    OrganizationSelector,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      displayItem: [],
      dialogFormVisible2: false,
      name: 'sitereviewplanList',
      tableName: 'sitereviewplanTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      visible: false,
      mode: 'add',
      dialogTitle: this.$t('perfMod.detail'),
      planTypeAll: [
        {
          value: 'YEAR',
          label: this.$t('perfMod.YEAR')
        },
        {
          value: 'HALF_YEAR',
          label: this.$t('perfMod.HALF_YEAR')
        },
        {
          value: 'QUARTER',
          label: this.$t('perfMod.QUARTER')
        },
        {
          value: 'ALLOW',
          label: this.$t('perfMod.ALLOW')
        },
        {
          value: 'MONTH',
          label: this.$t('perfMod.MONTH')
        }
      ],
      form: {
        siteReviewPlanId: null,
        vendorId: null,
        vendorName: '',
        vendorCode: '',
        orgId: null,
        orgCode: '',
        orgName: '',
        categoryCode: '',
        categoryName: '',
        planType: '',
        planStartDate: '',
        planProcessStatus: '',
        planConfirmCode: '',
        siteReviewCode: '',
        planStatus: '',
        createdId: null,
        createdBy: '',
        creationDate: '',
        createdFullName: '',
        createdByIp: '',
        lastUpdatedId: null,
        lastUpdatedBy: '',
        lastUpdateDate: '',
        lastUpdatedByIp: '',
        lastUpdatedFullName: '',
        tenantId: null,
        version: '',
        submitFlag: ''
      },
      rules: {},
      extraData: {
        sourceType: 'WEB_APP',
        uploadType: 'MINIO',
        fileModular: 'base',
        fileFunction: 'sitereviewplan',
        fileType: 'excel'
      },
      dictCodes: {},
      filterParams: {},
      tableHeader: [
        {
          prop: 'companyName',
          label: this.$t('perfMod.vendorName'),
          width: 120
        },
        {
          prop: 'organizationName',
          label: this.$t('cusEntry.perfMod.orgName'),
          width: 120
        },
        {
          prop: 'projectName',
          label: this.$t('perfMod.projectName3'),
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.projectNameFunction(row),
          width: 120
        },
        // 预警状态
        {
          prop: 'improveStatus',
          label:this.$t('cusEntry.supplement20250205.improveStatus'),
          formattor: (val) => {
            let mainHtml = ''
            switch (val) {
              case 'DRAFT':
                mainHtml = this.$t('perfMod.DRAFT')
                break
              case 'IMPROVING':
                mainHtml = this.$t('perfMod.IMPROVING')
                break
              case 'UNDER_EVALUATION':
                mainHtml = this.$t('perfMod.UNDER_EVALUATION')
                break
              case 'EVALUATED':
                mainHtml = this.$t('perfMod.EVALUATED')
                break
              default:
                mainHtml = this.$t('perfMod.DRAFT')
            }
            return mainHtml
          },
          width: 100
        },
        // 预警单据
        {
          prop: 'improveNo',
          label: this.$t('cusEntry.supplement20250205.improveNo'),
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.improveNoFunction(row),
          width: 100
        },
        {
          prop: 'categoryName',
          label: this.$t('perfMod.categoryName3'),
          width: 130
        },
        {
          prop: 'score',
          label: this.$t('perfMod.scoreAll'),
          width: 130
        },
        {
          prop: 'rank',
          label: this.$t('perfMod.rankAll'),
          width: 130
        },
        {
          prop: 'results',
          label: this.$t('perfMod.resultsReview'),
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.performanceDetails(row),
          formattor: (val) => {
            return this.$t('perfMod.detailsReview')
          },
          width: 130
        },
        {
          prop: 'perStartMonth',
          label: this.$t('perfMod.perStartMonth'),
          width: 130
        },
        {
          prop: 'perEndMonth',
          label: this.$t('perfMod.perEndMonth'),
          width: 130
        },
        {
          prop: 'operation',
          label: this.$t('common.operation'),
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 180,
          buttons: [
            {
              callback: row => this.editHandle1(row),
              // code: "pr:requirementApply:edit",
              show: row => row.improveNo == '' || !row.improveNo,
              formattor: () => {
                return this.$t('cusEntry.supplement20250205.createAlertForm') // 创建预警单
              }
            },
          ]
        }
      ],
      planStatusAll: [
        {
          value: 'DRAFT',
          label: this.$t('perfMod.DRAFT')
        },
        {
          value: 'SUBMITTED',
          label: this.$t('perfMod.SUBMITTED')
        }
      ],
      planProcessStatusAll: [
        {
          value: 'NOT_STARTED',
          label: this.$t('perfMod.NOT_STARTED')
        },
        {
          value: 'ONGOING',
          label: this.$t('perfMod.ONGOING')
        },
        {
          value: 'COMPLETED',
          label: this.$t('perfMod.COMPLETED')
        }
      ],
      filterConfig: [
        {
          prop: 'organizationId',
          label: this.$t('cusEntry.perfMod.orgName'),
          type: 'OUorganizationSelector'
        },
        {
          prop: 'companyId',
          label: this.$t('perfMod.vendorName'),
          type: 'quicksearch',
          showKey: 'companyName',
          propKey: 'companyId',
          name: 'scc_sup_company_info_display_buyer'
        },
        {
          prop: 'scoreItemsId',
          label: this.$t('perfMod.scoreItemsId'),
          type: 'select',
          options: []
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
    // 项目下拉
    performanceManagement.findCalculatedScoreItemsList().then(res => {
      if (res.data) {
        this.projectList = res.data.map(i => ({
          value: i.scoreItemsId,
          label: i.projectName
        }))
        this.filterConfig[2].options = this.projectList
      }
    })
  },
  methods: {
    performanceDetails (row) {
      console.log(row)
      let tab = {
        component: performanceQueryDetail,
        params: {
          row
        },
        title: row.companyName,
        name: 'performanceQueryDetail' + row.overallScoreId
      }
      this.$emit('tab-add', tab)
    },
    assessmentNoFunction (row) {
      let tab = {}
      tab = {
        component: assessmentDetail,
        params: {
          flag: 'view',
          orderId: row.vendorAssesId,
          tabName: 'assessmentDetail' + row.vendorAssesId
        },
        title: row.companyName,
        name: 'assessmentDetail' + row.vendorAssesId
      }
      this.$emit('tab-add', tab)
    },
    improveNoFunction (row) {
      console.log(row)
      let tab = {}
      tab = {
        component: improvementDetail,
        params: {
          flag: 'view',
          orderId: row.vendorImproveId,
          tabName: 'improvementDetail' + row.vendorImproveId
        },
        title: row.companyName,
        name: 'improvementDetail' + row.vendorImproveId
      }
      this.$emit('tab-add', tab)
    },
    // 点击绩效项目名称
    projectNameFunction (row) {
      console.log(row)
      this.$emit('tab-add', {
        component: performanceScoreItemsDetail,
        params: {
          flag: 'view',
          orderId: row.scoreItemsId,
          tabName: 'performanceScoreItemsDetail' + row.scoreItemsId
        },
        title: row.projectName,
        name: 'performanceScoreItemsDetail' + row.scoreItemsId
      })
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
      performanceManagement.saveCategoryList(datas).then(data => {
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
    // 维护品类
    readOne (row) {
      this.$http({
        url: '/api-sup/sup/sitereviewplan/getCategoryList',
        method: 'GET',
        params: { id: row.siteReviewPlanId },
        loading: true
      })
        .then(data => {
          this.displayItem = data.data.list
          this.dialogFormVisible2 = true
          this.siteReviewPlanId = row.siteReviewPlanId
        })
        .catch(err => {
          console.log(err)
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
      downloadFileLink('/api-sup/sup/sitereviewplan/importExcelTemplate', this.$t('logisticsMod.importTemplateXLSX')).catch(
        () => {
          this.$message.error(this.$t('perfMod.downLoadError'))
        }
      )
    },
    cancel () {
      this.visible = false
    },
    confirmSave () {
      const flag = this.mode
      let formAll = this.form
      formAll.submitFlag = 'SAVE'
      if (flag === 'add') {
        performanceManagement.planAdd(formAll).then(res => {
          if (res.code == '0') {
            this.visible = false
            this.$message.success(res.message)
            this.getQuerydata()
          } else {
            this.$message.error(res.message)
          }
        })
      } else if (flag === 'edit') {
        performanceManagement.planUpdate(formAll).then(res => {
          if (res.code == '0') {
            this.visible = false
            this.$message.success(res.message)
            this.getQuerydata()
          } else {
            this.$message.error(res.message)
          }
        })
      }
    },
    confirm () {
      this.$refs.form.validate(result => {
        if (result) {
          const flag = this.mode
          // 新增时不用提交主键值
          let formAll = this.form
          formAll.submitFlag = 'SUBMIT'
          if (flag === 'add') {
            performanceManagement.planAdd(formAll).then(res => {
              if (res.code == '0') {
                this.visible = false
                this.$message.success(res.message)
                this.getQuerydata()
              } else {
                this.$message.error(res.message)
              }
            })
          } else if (flag === 'edit') {
            performanceManagement.planUpdate(formAll).then(res => {
              if (res.code == '0') {
                this.visible = false
                this.$message.success(res.message)
                this.getQuerydata()
              } else {
                this.$message.error(res.message)
              }
            })
          }
        }
      })
    },

    syncFilterParams (values) {
      this.filterParams = values
    },
    getQuerydata (params) {
      // this.queryParam = params
      const levelNames = {
        // levelNames: ['不合格','合格待改善']
        levelNames: [this.$t('vendorMod.text8'), this.$t('cusEntry.supplement20250205.qualifiedToImprove')]
      }
      Object.assign(this.queryParam,params,levelNames)
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
          performanceManagement.planDelete(row.siteReviewPlanId).then(res => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    addHandle (row) {
      for (let i in this.form) {
        this.form[i] = ''
      }
      this.dialogTitle = this.$t('perfMod.siteReviewProgramManagementAdded')
      this.visible = true
      this.mode = 'add'
    },
    editHandle1 (row) {
      console.log(row)
      // return
      let tab = {
        component: yujingdanEdit,
        params: {
          row,
          flag: 'add',
          warningType:'ORDER'
        },
        title: row.projectName,
        name: 'yujingdanEdit' + row.warningId
      }
      this.$emit('tab-add', tab)
    },
    editHandle2 (row) {
      let tab = {}
      tab = {
        component: assessmentDetail,
        params: {
          flag: 'adds',
          row
        },
        title: row.companyName,
        name: 'assessmentDetail' + row.vendorAssesId
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
