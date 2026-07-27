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
          <!-- 导入 -->
          <!--          <m-import-->
          <!--            :title="$t('common.import')"-->
          <!--            up-load-url="/api-sup/sup/sitereviewplan/importExcel"-->
          <!--            :extra-data="extraData"-->
          <!--            type="default"-->
          <!--            @downloadTemplate="downloadTemplate"-->
          <!--            @handleSuccess="handleSuccess"-->
          <!--          />-->
          <ExportExcel
            page-url="/api-sup/sup/sitereviewplan/listPage"
            :filter-params="queryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            :timeout="1000000"
            export-mode="front"
            type="default"
          />
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
        :source="siteReviewPlan.planList"
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
          <srm-row :gutter="32">
            <!-- 计划名称 -->
            <srm-col :span="8">
              <el-form-item
                prop="planName"
                :label="$t('vendorMod.planName')"
              >
                <el-input v-model="form.planName" />
              </el-form-item>
            </srm-col>
            <!-- 供应商名称 -->
            <srm-col :span="8">
              <el-form-item
                prop="vendorId"
                :label="$t('vendorMod.vendorId')"
              >
                <QuickSearch
                  :show-input="form.vendorName"
                  show-key="companyName"
                  :scope-data="form"
                  name="scc_sup_company_info2"
                  @close-quicksearch="getDepObjName"
                />
              </el-form-item>
            </srm-col>
            <!-- 采购组织 -->
            <srm-col :span="8">
              <el-form-item
                prop="orgName"
                :label="$t('vendorMod.orgName')"
              >
                <OrganizationSelector
                  ref="organizationSelector"
                  v-model="form.orgId"
                  :placeholder="$t('common.pleaseSelect')"
                  :parent-id="-1"
                  node-type="OU"
                  @select="selectHandler2"
                />
                <!-- node-type="PO" -->
              </el-form-item>
            </srm-col>
            <!-- 计划类型 -->
            <srm-col :span="8">
              <el-form-item
                prop="planType"
                :label="$t('vendorMod.planType')"
              >
                <!-- 请选择 -->
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
            <!-- 计划开始时间 -->
            <srm-col :span="8">
              <el-form-item
                prop="planStartDate"
                :label="$t('vendorMod.planStartDate')"
              >
                <el-date-picker
                  v-model="form.planStartDate"
                  type="date"
                  :placeholder="$t('common.pleaseSelectDate')"
                  format="yyyy-MM-dd"
                  value-format="yyyy-MM-dd"
                />
              </el-form-item>
            </srm-col>
          </srm-row>
        </el-form>
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
                {{
                  $t('common.delete')
                }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
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
      :title="$t('dataConfMod.categoryDetails')"
      :visible.sync="dialogFormVisible2"
      :close-on-click-modal="false"
      size="middle"
    >
      <!-- <div class="mb10">
        <el-button type="primary" class="detail-pbtn" @click="addOneItem">{{
          $t('common.add')
        }}</el-button>
      </div> -->
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
      </el-table>
      <template #footer>
        <el-button @click="dialogFormVisible2 = false">
          <!-- 取 消 -->
          {{ $t('common.cancel') }}
        </el-button>
        <!-- <el-button type="primary" @click="addCategorys">{{ $t('common.confirm') }}</el-button> -->
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
import siteReviewPlanConfirm from 'modb@/vendorManagementBuyer/views/siteReviewPlanConfirm/edit'
import siteAssessment from 'modb@/vendorManagementBuyer/views/siteAssessment/siteAssessmentDetail'
import { financeInfoChangeApi, siteReviewPlan } from 'modb@/vendorManagementBuyer/api/vendorManagement'
import { supCommonApi } from 'modb@/vendorManagementBuyer/api/supApi'

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
      siteReviewPlan: siteReviewPlan,
      displayItem: [],
      dialogFormVisible2: false,
      name: 'sitereviewplanList',
      tableName: 'sitereviewplanTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      visible: false,
      mode: 'add',
      dialogTitle: this.$t('vendorMod.detail'),
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
      form: {
        siteReviewPlanId: 'ID',
        vendorId: '供应商ID',
        vendorName: '供应商名称',
        vendorCode: '供应商编码',
        orgId: '采购组织ID',
        orgCode: '采购组织编码',
        orgName: '采购组织',
        categoryCode: '品类编码',
        categoryName: '品类名称',
        planType: '计划类型',
        planStartDate: '计划开始时间(YYYY-MM-DD)',
        planProcessStatus: '计划进度状态',
        planConfirmCode: '计划落实单号',
        siteReviewCode: '现场评审单号',
        planStatus: '计划状态',
        createdId: '创建人ID',
        createdBy: '创建人',
        creationDate: '创建时间',
        createdFullName: '创建人新姓名',
        createdByIp: '创建人IP',
        lastUpdatedId: '最后更新人ID',
        lastUpdatedBy: '最后更新人',
        lastUpdateDate: '最后更新时间',
        lastUpdatedByIp: '最后更新人IP',
        lastUpdatedFullName: '最后更新人姓名',
        tenantId: '租户ID',
        version: '版本号',
        submitFlag: ''
      },
      rules: {
        planName: [{ required: true, message: this.$t('common.pleaseInput') }],
        vendorId: [{ required: true, message: this.$t('common.pleaseInput') }],
        orgName: [{ required: true, message: this.$t('common.pleaseInput') }],
        planType: [{ required: true, message: this.$t('common.pleaseInput') }],
        planStartDate: [{ required: true, message: this.$t('common.pleaseInput') }]
      },
      extraData: {
        fileModular: 'base',
        fileFunction: 'sitereviewplan',
        fileType: 'excel'
      },
      dictCodes: {
        planType: 'planType',
        planStatus: 'planStatus',
        planProcessStatus: 'planProcessStatus'
      },
      filterParams: {},
      tableHeader: [
        {
          prop: 'planName',
          label: this.$t('vendorMod.planName'), // 计划名称
          width: 100
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
          formattor: (val) => {
            return this.$t('vendorMod.viewCategory') // 查看品类维护品类
          },
          width: 100
        },
        {
          prop: 'planType',
          label: this.$t('vendorMod.planType'), // 计划类型
          formattor: val => {
            let re = /[^\u4E00-\u9FA5]/
            const planTypeAll = this.planTypeAll
            if (re.test(val)) {
              let label = planTypeAll.find(element => { return element.value == val })
              if (label) {
                return label.label
              } else {
                return ''
              }
            } else {
              return val
            }
          },
          width: 100
        },
        {
          prop: 'creationDate',
          label: this.$t('vendorMod.creationDate2'), // 创建时间
          width: 130
        },
        {
          prop: 'planStartDate',
          label: this.$t('vendorMod.planStartDate'), // 计划开始时间
          width: 130
        },
        {
          prop: 'planProcessStatus',
          label: this.$t('vendorMod.planProcessStatus'), // 计划进度状态
          formattor: val => {
            const planProcessStatusAll = this.planProcessStatusAll
            let label = ''
            planProcessStatusAll.forEach(element => {
              if (element.value == val) {
                label = element.label
              }
            })
            return label
          },
          width: 130
        },
        {
          prop: 'planConfirmCode',
          label: this.$t('vendorMod.planConfirmCode'), // 计划落实单号
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.readOne2(row),
          formattor: (val) => {
            if (val) {
              return val
            } else {
              return this.$t('vendorMod.createPlanConfirm') // 创建计划落实
            }
          },
          width: 130
        },
        {
          prop: 'siteReviewCode',
          label: this.$t('vendorMod.siteReviewCode'), // 现场评审单号
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.readOne3(row),
          formattor: (val) => {
            if (val) {
              return val
            } else {
              return this.$t('vendorMod.createSiteReviewCode') // 创建评审单
            }
          },
          width: 130
        },
        {
          prop: 'planStatus',
          label: this.$t('vendorMod.planStatus'), // 计划状态
          formattor: val => {
            const planStatusAll = this.planStatusAll
            let label = ''
            planStatusAll.forEach(element => {
              if (element.value == val) {
                label = element.label
              }
            })
            return label
          },
          minWidth: 120
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
              callback: row => this.editHandle(row),
              // code: "pr:requirementApply:edit",
              show: row => row.planStatus === 'DRAFT',
              formattor: () => {
                return this.$t('common.edit')
              }
            },
            {
              callback: row => this.editHandle(row),
              // code: "pr:requirementApply:edit",
              show: row => row.planStatus === 'DRAFT',
              formattor: () => {
                return this.$t('common.submit')
              }
            },
            {
              callback: row => this.deleteHandle(row),
              // code: "pr:requirementApply:edit",
              show: row => row.planStatus === 'DRAFT',
              formattor: () => {
                return this.$t('common.delete')
              }
            }
          ]
        }
      ],
      planStatusAll: [
        {
          value: 'DRAFT',
          label: this.$t('vendorMod.DRAFT') // 拟定
        },
        {
          value: 'SUBMITTED',
          label: this.$t('vendorMod.SUBMITTED') // 已提交
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
          prop: 'vendorId',
          label: this.$t('vendorMod.vendorId'), // 供应商名称
          type: 'quicksearch',
          showKey: 'companyName',
          propKey: 'companyId',
          name: 'scc_sup_company_info_all'
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
        {
          prop: 'categoryCode',
          label: this.$t('vendorMod.categoryCode'), // 品类名称
          type: 'quicksearch',
          showKey: 'categoryName',
          propKey: 'categoryCode',
          name: 'scc_base_purchase_category2'
        },
        {
          prop: 'planStatus',
          label: this.$t('vendorMod.planStatus'), // 计划状态
          type: 'select',
          options: () => this.planStatusAll
        },
        {
          prop: 'planProcessStatus',
          label: this.$t('vendorMod.planProcessStatus'), // 计划进度状态
          type: 'select',
          options: () => this.planProcessStatusAll
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
    // 维护品类
    readOne (row) {
      supCommonApi.getCategoryList(row.siteReviewPlanId).then(data => {
          this.displayItem = data.data.list
          this.dialogFormVisible2 = true
          this.siteReviewPlanId = row.siteReviewPlanId
        })
        .catch(err => {
          console.log(err)
        })
    },
    // 计划落实单号
    readOne2 (row) {
      console.log(row)
      if (row.planConfirmCode == '' || row.planConfirmCode == null) {
        this.mode = 'add'
        let datas = {
          siteReviewPlanId: row.siteReviewPlanId,
          vendorName: row.vendorName,
          orgName: row.orgName,
          categoryName: row.categoryName,
          planName: row.planName,
          planType: row.planType
        }
        const tab = {
          component: siteReviewPlanConfirm,
          params: {
            row,
            flag: this.mode,
            datas: datas
          },
          title: this.$t('vendorMod.planConfirmManagement'), // 计划落实管理
          name: 'siteReviewPlanConfirm'
        }
        this.$emit('tab-add', tab)
      } else {
        console.log(row)
        this.mode = 'edit'
        const tab = {
          component: siteReviewPlanConfirm,
          params: {
            row,
            flag: this.mode
          },
          title: this.$t('vendorMod.planConfirmManagement'), // 计划落实管理
          name: 'siteReviewPlanConfirm'
        }
        this.$emit('tab-add', tab)
      }
    },
    // 现场评审单号
    readOne3 (row) {
      console.log(row)
      if (row.siteReviewCode == '' || row.siteReviewCode == null) {
        this.mode = 'adds'
        const tab = {
          component: siteAssessment,
          params: {
            flag: this.mode,
            row
          },
          title: this.$t('vendorMod.appraisal'), // 评审
          name: 'siteAssessment'
        }
        this.$emit('tab-add', tab)
      } else {
        this.mode = 'edit'
        const tab = {
          component: siteAssessment,
          params: {
            siteFormId: row.siteFormId,
            flag: this.mode
          },
          title: this.$t('vendorMod.appraisal'), // 评审
          name: 'siteAssessment'
        }
        this.$emit('tab-add', tab)
      }
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
      if (val) {
        scpoe.vendorId = val.companyId
        scpoe.vendorName = val.companyName
        scpoe.vendorCode = val.companyCode
      } else {
        scpoe.vendorId = null
        scpoe.vendorName = null
        scpoe.vendorCode = null
      }
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
          siteReviewPlan.planDelete(row.siteReviewPlanId).then(res => {
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
      this.dialogTitle = this.$t('vendorMod.siteReviewProgramManagementAdded') // 现场评审计划管理新增
      this.displayItem = []
      this.visible = true
      this.mode = 'add'
    },
    editHandle (row) {
      this.form = row
      supCommonApi.getCategoryList(row.siteReviewPlanId).then(data => {
          this.displayItem = data.data.list
          this.siteReviewPlanId = row.siteReviewPlanId
        })
        .catch(err => {
          console.log(err)
        })
      this.dialogTitle = this.$t('vendorMod.siteReviewProgramManagementEditor') // 现场评审计划管理编辑
      this.visible = true
      this.mode = 'edit'
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
